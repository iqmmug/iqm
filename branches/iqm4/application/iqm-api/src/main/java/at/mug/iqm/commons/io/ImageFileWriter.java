package at.mug.iqm.commons.io;

/*
 * #%L
 * Project: IQM - API
 * File: ImageFileWriter.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.awt.Toolkit;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.gui.IDrawingLayer;
import at.mug.iqm.api.gui.ROILayerManager;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.image.ImageTools;

import com.sun.media.jai.codec.TIFFEncodeParam;

/**
 * This class stores one or more images with a given encoding to the file
 * system.
 * 
 * @author Philipp Kainz
 * @since 3.1
 * @update 2018-09-09 HA JAI does not save jpg since Java9+, changed to ImageIO
 */
public class ImageFileWriter implements Runnable {

	// private class logger
	private static final Logger logger = LogManager.getLogger(ImageFileWriter.class);

	public static final int MODE_SINGLE = 0;
	public static final int MODE_SEQUENCE = 1;
	public static final int MODE_STACK = 2;

	private File destination;
	private RenderedImage image;
	private List<IqmDataBox> boxes;
	private String encoding;
	private String extension;
	private boolean withROIs;
	private int mode;

	/**
	 * Create an empty file writer.
	 */
	public ImageFileWriter(int mode) {
		this.mode = mode;
	}

	/**
	 * Create a new image file writer.
	 * 
	 * @param destination
	 *            the target file
	 * @param image
	 *            the image
	 * @param encoding
	 *            a JAI-specific string
	 */
	public ImageFileWriter(File destination, RenderedImage image, String encoding) {
		this.destination = destination;
		this.image = image;
		this.encoding = encoding;
		this.mode = MODE_SINGLE;
	}

	/**
	 * Create a new image file writer.
	 * 
	 * @param destination
	 *            the target file
	 * @param box
	 *            the {@link IqmDataBox} containing the image
	 * @param encoding
	 *            a JAI-specific string
	 */
	public ImageFileWriter(File destination, IqmDataBox box, String encoding) {
		this.destination = destination;
		this.image = box.getImage();
		this.encoding = encoding;
		this.mode = MODE_SINGLE;
	}

	/**
	 * Create a new image file writer.
	 * 
	 * @param destination
	 *            the target file
	 * @param itemList
	 *            the list of items ({@link IqmDataBox} containing the images,
	 *            or rendered images)
	 * @param encoding
	 *            a JAI-specific string
	 * @param extension
	 *            the file extension
	 * @param mode
	 *            determine the writing mode, either {@link #MODE_SEQUENCE} or
	 *            {@link #MODE_STACK}
	 */
	@SuppressWarnings("unchecked")
	public ImageFileWriter(File destination, List<?> itemList, String encoding,
			String extension, int mode) {
		this.destination = destination;
		if (itemList == null || itemList.isEmpty())
			throw new IllegalArgumentException(
					"The list of items is null or empty!");

		Object o = itemList.get(0);
		if (o instanceof RenderedImage) {
			for (RenderedImage im : (List<RenderedImage>) itemList) {
				this.boxes
						.add(new IqmDataBox(PlanarImage.wrapRenderedImage(im)));
			}
		} else if (o instanceof IqmDataBox) {
			this.boxes = (List<IqmDataBox>) itemList;
		}
		this.encoding = encoding;
		this.extension = extension;
		if (mode == MODE_SINGLE) {
			throw new IllegalArgumentException(
					"The mode must not be storing a single file!");
		}
		this.mode = mode;
	}

	/**
	 * Set the destination of the image file(s).
	 * 
	 * @param destination
	 *            the target file name
	 */
	public void setDestination(File destination) {
		this.destination = destination;
	}

	/**
	 * Get the destination of the image file.
	 * 
	 * @return the target file name
	 */
	public File getDestination() {
		return destination;
	}

	/**
	 * Set the encoding for the image(s) to be saved.
	 * 
	 * @param encoding
	 *            a JAI specific string
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Get the encoding of the image(s) to be saved.
	 * 
	 * @return the encoding, a JAI specific string
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Get the file name extension.
	 * 
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * Set the extension of the file.
	 * 
	 * @param extension
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * Run the file writer in a separate thread.
	 */
	@Override
	public void run() {
		try {
			switch (mode) {
			case MODE_SINGLE:
				write();
				break;

			case MODE_SEQUENCE:
				writeSequence();
				break;

			case MODE_STACK:
				writeStack();
				break;

			default:
				break;
			}
		} catch (IOException e) {
			logger.error("Error writing file!", e);
		}
	}

	/**
	 * Perform the file writing action.
	 * 
	 * @throws IOException
	 *             if the file could not be written.
	 */
	public void write() throws IOException {
		// only tiff allowed for non byte data
		if (image.getSampleModel().getDataType() != DataBuffer.TYPE_BYTE) {
			if (encoding != "TIFF") {
				BoardPanel.appendTextln(I18N.getMessage(
						"application.imageFormat.byte.allowed", encoding));
				return;
			}
		}

		logger.info("Saving single image with encoding: " + encoding);

		// write ROIs to file, if required
		if (isWithROIs()) {
			// get a list of all visible ROI layers
			List<IDrawingLayer> visLayers = ROILayerManager.getVisibleLayers();

			image = ImageTools.paintROIsOnImage(
					PlanarImage.wrapRenderedImage(image), visLayers);
		}

		
		try { // try imageIO
			ImageIO.write(image, encoding, destination);
		} catch (Exception ex) {
			logger.error("It was not possible to write this image to: " + destination.getPath());
		}
		
		//JAI does not work any more for jpg since Java9+
		// save image using JAI   
		//JAI.create("filestore", image, destination.toString(), encoding);

		BoardPanel.appendTextln(I18N.getMessage("application.imageSaved", encoding, destination));
	}

	/**
	 * Saves an image sequence to multiple files. This method will automatically
	 * append proper sequence numbers.
	 */
	public void writeSequence() {
		// a temp image
		PlanarImage pi = null;

		// ######################################################################################
		// NOTE: This works, but the check against existing files is not valid,
		// until the digits of total tank images is identical with the digits,
		// when the existing sequence has been stored!

		// if 40 images have to be stored, then the sequence number shall only
		// have 2 digits
		// -> rule: #sequence.set(#digits) = #images.get(#digits)

		// construct the string format for the sequence number
		// String sequenceDigitFormat = "%0" + String.valueOf(length).length() +
		// "d";
		// ######################################################################################

		String testName;
		String plainName;

		int length = boxes.size();
		int nDigits = 6;
		
		// extract plain file name
		// no extension is selected/entered
		if (extension.equals("default")) {
			// file name e.g. filename_0001
			if (destination.toString().endsWith("_" + String.format("%0" + nDigits
					+ "d", 1))) {
				// take just 'filename'
				plainName = destination.toString().substring(0,
						destination.toString().lastIndexOf("_"));
			}
			// file name ends arbitrarily e.g. filename_subFileName
			else {
				// take the whole entered string
				plainName = destination.toString();
			}
		}

		// if any recognized extension is set
		else {
			// file name e.g. filename_0001.png
			if (destination.toString().endsWith(
					"_" + String.format("%0" + nDigits
							+ "d", 1) + "." + extension)) {
				// take just 'filename'
				plainName = destination.toString().substring(0,
						destination.toString().lastIndexOf("_"));
			}
			// file name ends arbitrarily e.g. filename_subFileName_.png
			else if (destination.toString().endsWith("." + extension)) {
				// remove the extension and take 'filename_subFileName_'
				plainName = destination.toString().substring(0,
						destination.toString().lastIndexOf("."));
			}
			// existing file name has been selected via mouse/keyboard
			else {
				plainName = destination.toString();
			}
		}

		// the name to check for
		String plainNameCheck = "";
		// construct a test file name for checking, if the sequence already
		// exists
		if (plainName.contains("+")) { // Names are taken from PlanarImages
			IqmDataBox box = boxes.get(0);
			if (box instanceof VirtualDataBox) {
				box = VirtualDataManager.getInstance().load(
						(IVirtualizable) box);
			}
			String imgName = box.getImageModel().getModelName();
			String plainNameNew = plainName.replaceFirst("\\+", imgName);

			plainNameNew = plainNameNew.replaceFirst(".tif", "");
			plainNameNew = plainNameNew.replaceFirst(".TIF", "");
			plainNameNew = plainNameNew.replaceFirst(".tiff", "");
			plainNameNew = plainNameNew.replaceFirst(".TIFF", "");
			plainNameNew = plainNameNew.replaceFirst(".jpg", "");
			plainNameNew = plainNameNew.replaceFirst(".JPG", "");
			plainNameNew = plainNameNew.replaceFirst(".jpeg", "");
			plainNameNew = plainNameNew.replaceFirst(".JPEG", "");
			plainNameNew = plainNameNew.replaceFirst(".png", "");
			plainNameNew = plainNameNew.replaceFirst(".PNG", "");
			plainNameNew = plainNameNew.replaceFirst(".gif", "");
			plainNameNew = plainNameNew.replaceFirst(".GIF", "");
			plainNameNew = plainNameNew.replaceFirst(".bmp", "");
			plainNameNew = plainNameNew.replaceFirst(".BMP", "");

			plainNameCheck = plainNameNew;
		} else {
			plainNameCheck = plainName;
		}

		if (plainNameCheck.endsWith("$")){
			// skip the sequence number
			testName = plainNameCheck.replace("$", "") + "." + extension;
		} else {
			testName = plainNameCheck + "_" + String.format("%0" + nDigits
					+ "d", 1) + "." + extension;
		}

		File testFile = new File(testName);

		if (testFile.exists()) {
			Toolkit.getDefaultToolkit().beep();

			int selected = DialogUtil.getInstance().showDefaultWarnMessage(
					I18N.getMessage("application.fileExists.overwrite"));
			if (selected != IDialogUtil.YES_OPTION) {
				BoardPanel.appendTextln(I18N
						.getMessage("application.imageNotSaved"));
				return;
			}
		}

		String fileName = "";

		// store each image with the corresponding file name
		for (int n = 0; n < length; n++) {

			// calculate values for progress bar (each image increases the
			// value)
			int proz = (n + 1) * 100;
			proz = proz / length;
			Application.getMainFrame().getStatusPanel()
					.setProgressBarValueStack(proz);

			IqmDataBox box = boxes.get(n);

			if (box instanceof VirtualDataBox) {
				box = VirtualDataManager.getInstance().load(
						(IVirtualizable) box);
			}

			// only for real images
			pi = box.getImage();
			
			String plainNameSave = "";
			
			// Names are taken or added from PlanarImages
			if (plainName.contains("+")) {
				// the name of the image
				String imgName = box.getImageModel().getModelName();
				String plainNameNew = plainName.replaceFirst("\\+", imgName);

				plainNameNew = plainNameNew.replaceFirst(".tif", "");
				plainNameNew = plainNameNew.replaceFirst(".TIF", "");
				plainNameNew = plainNameNew.replaceFirst(".tiff", "");
				plainNameNew = plainNameNew.replaceFirst(".TIFF", "");
				plainNameNew = plainNameNew.replaceFirst(".jpg", "");
				plainNameNew = plainNameNew.replaceFirst(".JPG", "");
				plainNameNew = plainNameNew.replaceFirst(".jpeg", "");
				plainNameNew = plainNameNew.replaceFirst(".JPEG", "");
				plainNameNew = plainNameNew.replaceFirst(".png", "");
				plainNameNew = plainNameNew.replaceFirst(".PNG", "");
				plainNameNew = plainNameNew.replaceFirst(".gif", "");
				plainNameNew = plainNameNew.replaceFirst(".GIF", "");
				plainNameNew = plainNameNew.replaceFirst(".bmp", "");
				plainNameNew = plainNameNew.replaceFirst(".BMP", "");

				plainNameSave = plainNameNew;
			} else {
				plainNameSave = plainName;
			}
			
			if (plainNameSave.endsWith("$")){
				// skip the sequence number
				fileName = plainNameSave.replace("$", "") + "." + extension;
			} else {
				// add the sequence number
				fileName = plainNameSave + "_" + String.format("%0" + nDigits
					+ "d", n + 1) + "."
					+ extension;
			}

			
			// only tiff allowed for non byte data
			if (pi.getSampleModel().getDataType() != DataBuffer.TYPE_BYTE) {
				if (encoding != "TIFF") {
					DialogUtil.getInstance().showDefaultErrorMessage(I18N.getMessage(
							"application.imageFormat.byte.allowed", encoding));
					continue;
				}
			}

			// write ROIs to file, if required
			if (isWithROIs()) {
				// get a list of all visible ROI layers
				List<IDrawingLayer> visLayers = ROILayerManager
						.getVisibleLayers();

				pi = ImageTools.paintROIsOnImage(pi, visLayers);
			}

			try { // try imageIO
				ImageIO.write(pi, encoding, new File(fileName));
			} catch (Exception ex) {
				logger.error("It was not possible to write this image to: " + destination.getPath());
			}
			// write the image  JAI does not work any more for jpg since Java9+
			//JAI.create("filestore", pi, fileName, encoding);

			logger.info("Successfully stored image [" + fileName + "].");
		}

		// print to board
		BoardPanel.appendTextln(I18N.getMessage("application.imageSequSaved",
				encoding, new File(fileName).getParent()));

		// reset progress bar
		Application.getMainFrame().getStatusPanel().setProgressBarValueStack(0);
	}

	/**
	 * Write a list of images to a stack.
	 */
	public void writeStack() {
		int index = Application.getTank().getCurrIndex();
		if (index == -1) {
			BoardPanel
					.appendTextln(I18N.getMessage("application.missingImage"));
			return;
		}

		int length = boxes.size();

		logger.debug("Number of images to save to stack: " + length);

		PlanarImage pi = null;
		PlanarImage piFirst = null;
		Vector<PlanarImage> vec = new Vector<PlanarImage>(length - 1);
		for (int n = 0; n < length; n++) {
			IqmDataBox box = boxes.get(n);

			if (box instanceof VirtualDataBox) {
				box = VirtualDataManager.getInstance().load(
						(IVirtualizable) box);
			}

			pi = box.getImage();

			if (isWithROIs()) {
				pi = ImageTools.paintROIsOnImage(pi,
						ROILayerManager.getVisibleLayers());
			}

			if (n == 0)
				piFirst = pi;
			if (n > 0)
				vec.add(n - 1, pi);
		}

		TIFFEncodeParam param = new TIFFEncodeParam();
		param.setExtraImages(vec.iterator());

		JAI.create("filestore", piFirst, destination.toString(), "TIFF", param);
	}

	/**
	 * Gets a flag whether or not ROIs should be drawn on export.
	 * 
	 * @return draw ROIs
	 */
	public boolean isWithROIs() {
		return withROIs;
	}

	/**
	 * Set a flag whether or not ROIs should be drawn on export.
	 * 
	 * @param withROIs
	 *            <code>true</code> or <code>false</code>
	 */
	public void setWithROIs(boolean withROIs) {
		this.withROIs = withROIs;
	}

}
