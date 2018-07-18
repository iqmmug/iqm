package at.mug.iqm.commons.util.image;

/*
 * #%L
 * Project: IQM - API
 * File: SVSImageExtractor.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.BorderExtender;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;
import javax.swing.SwingWorker;

import loci.formats.FormatException;
import loci.formats.gui.BufferedImageReader;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.exception.UnreadableSVSFileException;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.commons.util.DialogUtil;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

/**
 * @author Philipp Kainz
 * @since 2012 05 29
 */
public class SVSImageExtractor extends SwingWorker<Boolean, Void> {
	// Logging variables
	private static Class<?> caller = SVSImageExtractor.class;
	private static final Logger logger = Logger
			.getLogger(SVSImageExtractor.class);

	/**
	 * Default parameters for the image extraction: first image, no rescale,
	 * bilinear interpolation, tif extension.
	 */
	private int[] params = new int[] { 0, 1, 1, 0 };

	/**
	 * 
	 */
	private File[] files = new File[0];

	/**
	 * 
	 */
	private Vector<File> extractedFiles = new Vector<File>();

	private int imageToLoad = 0; // image number
	private int rescale = 1; // rescale factor
	private int interP = 1; // interpolation method
	private int extension = 0; // file extension

	private int firstWidth = 0;
	private int firstHeight = 0;

	int numImages = 0;

	private float theRatioW = 0.0f;
	private float theRatioH = 0.0f;
	private boolean allWidthsEqual = true;
	private boolean allHeightsEqual = true;

	public SVSImageExtractor() {
	}

	/**
	 * This method reads out the image information from a specified image
	 * <code>file</code>.
	 * 
	 * @param file
	 *            - the specified file to be read
	 * @return a 2D Object array
	 */
	public Object[][] readImageMetaData(File file)
			throws UnreadableSVSFileException {
		StringBuffer sb = new StringBuffer();
		Object[][] data = null;
		try {
			logger.debug("Reading Meta Data of SVS image...");

			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

			if (!readers.hasNext()) { // no reader available, perhaps simple
										// imageio without tiff reader installed
				IIORegistry registry = IIORegistry.getDefaultInstance();
				// registry.registerServiceProvider(new
				// com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi());
				registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi());
				BoardPanel.appendTextln("Additional TIFF reader registered.",
						caller);
				readers = ImageIO.getImageReaders(iis);
			}

			if (readers.hasNext()) {// reader(s) available

				// pick the first available ImageReader
				ImageReader reader = readers.next();

				// attach source to the reader
				reader.setInput(iis, false); // ImageInputStream, boolean
												// seekForwardOnly

				// read metadata of first image
				// reader.getNumThumbnails(arg0);

				// determine the number of images in the svs file
				numImages = reader.getNumImages(true); // boolean allowSearch
														// (allowSearch == true
														// and seekForwardOnly
														// == true is not
														// working)
				BoardPanel.appendTextln(
						"Number of images in [" + file.toString() + "]: "
								+ numImages, caller);

				// initialize the data array
				data = new Object[numImages][6];

				firstWidth = reader.getWidth(0);
				firstHeight = reader.getHeight(0);
				for (int n = 0; n < numImages; n++) {
					int width = reader.getWidth(n);
					int height = reader.getHeight(n);
					double expFileSize = ((double) width * (double) height * 3.0d)
							/ (Math.pow(1024, 3));
					float ratioWidth = (float) firstWidth / width;
					float ratioHeight = (float) firstHeight / height;
					String strNumber = String.format("%3d", n + 1);
					String strWidth = String.format("%6d", width);
					String strHeight = String.format("%6d", height);
					String strRatioWidth = String.format("%5.1f", ratioWidth);
					String strRatioHeight = String.format("%5.1f", ratioHeight);
					String strExpFileSize = String.format("%5.4f", expFileSize);

					// construct array for table
					data[n][0] = strNumber;
					data[n][1] = strWidth;
					data[n][2] = strHeight;
					data[n][3] = strRatioWidth;
					data[n][4] = strRatioHeight;
					data[n][5] = strExpFileSize;

					sb.append("Image Number: " + strNumber + "     Width: "
							+ strWidth + "   Height: " + strHeight
							+ "     Width Ratio: " + strRatioWidth + ":1"
							+ "  Height Ratio: " + strRatioHeight + ":1 "
							+ " Expected Size: " + strExpFileSize + "\n");
				}
				BoardPanel.appendTextln(new String(sb));
				logger.debug(new String(sb));
			}
		} catch (IOException e) {
			logger.error("An error occurred: ", e);
			throw new UnreadableSVSFileException();
		}
		return data;
	}

	/**
	 * This method rescales an instance of PlanarImage (pi) using a given
	 * interpolation method (optIntP).
	 * 
	 * @param pi
	 * @param rescale
	 * @param optIntP
	 * @return a planar image
	 */
	private PlanarImage rescaleImage(PlanarImage pi, int rescale, int optIntP) {
		if (rescale > 1) {
			logger.debug("Rescaling SVS image with parameters [rescale="
					+ rescale + "], [interpolationMethod=" + optIntP + "]");
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(pi);
			pb.add(1.0f / rescale);// x scale factor
			pb.add(1.0f / rescale);// y scale factor
			pb.add(0.0F);// x translate
			pb.add(0.0F);// y translate

			if (optIntP == 0)
				pb.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
			if (optIntP == 1)
				pb.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
			if (optIntP == 2)
				pb.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
			if (optIntP == 3)
				pb.add(Interpolation
						.getInstance(Interpolation.INTERP_BICUBIC_2));

			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
					BorderExtender.createInstance(BorderExtender.BORDER_COPY));
			pi = JAI.create("scale", pb, rh);
		}
		return pi;
	}

	/**
	 * Retrieve the Size of the first image in an SVS file stack.
	 * 
	 * @param file
	 * @return the integer array containing [width,height]
	 */
	private int[] getSizeOfFirstImg(File file) {

		int firstWidth = 0;
		int firstHeight = 0;

		try {
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

			if (readers.hasNext()) {// reader(s) available

				// pick the first available ImageReader
				ImageReader reader = readers.next();

				// attach source to the reader
				reader.setInput(iis, false); // ImageInputStream, boolean
												// seekForwardOnly

				firstWidth = reader.getWidth(0);
				firstHeight = reader.getHeight(0);

			}
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
		}
		return new int[] { firstWidth, firstHeight };

	}

	/**
	 * This method extracts the files with the given params.
	 * 
	 * @param files
	 *            - the file list
	 * @param params
	 *            - parameters for processing the file list:
	 *            <ul>
	 *            <li>[0]... image number of the stack</li>
	 *            <li>[1]... rescaling factor</li>
	 *            <li>[2]... interpolation method</li>
	 *            <li>[3]... file extension</li>
	 *            </ul>
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean extract(File[] files, int[] params) {
		if (params == null)
			return false;

		if ((params[0] < 0) || (params[1] < 1)) {
			// use default values
			params = this.params;
		}

		imageToLoad = params[0] - 1; // index at the image array (image number)
		logger.debug("Extracting image number [" + imageToLoad
				+ "] from the specified SVS files.");

		rescale = params[1];
		interP = params[2];
		extension = params[3];
		for (int i = 0; i < files.length; i++) {
			SeekableStream s = null;
			PlanarImage pi = null;
			try {
				s = new FileSeekableStream(files[i].toString());
				TIFFDecodeParam param = new TIFFDecodeParam();
				ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s,
						param);
				RenderedImage ri = dec.decodeAsRenderedImage(imageToLoad);
				pi = new RenderedImageAdapter(ri);

			} catch (IOException e) { // try another method e.g for Aperio jpeg
										// 2000 compressed images
				logger.error("An error occurred: ", e);

				BoardPanel
						.appendTextln(
								"Standard method (jpeg compressed,...) failed, trying another method (e.g. for jpeg2000 compression,...)",
								caller);

				BufferedImageReader bR = new BufferedImageReader();
				BufferedImage bi = null;

				try {
					bR.setId(files[i].toString());
					// bi = bR.openImage(0, 0, 0, bR.getSizeX(), bR.getSizeY());
					// System.out.println("SVSImageExtractor: bR.getImageCount(): "
					// + bR.getImageCount());
					// System.out.println("SVSImageExtractor: bR.getSeriesCount(): "
					// + bR.getSeriesCount());
					bR.setSeries(imageToLoad - 1); // "-1" added because new
													// loci_tools 5.0.0 needs
													// this index now with -1
					bi = bR.openImage(0);
					bR.close();
				} catch (FormatException e2) {
					// e2.printStackTrace();
					DialogUtil.getInstance().showDefaultErrorMessage(
							"Could not open the image, perhaps it is defect!");
					break;
				} catch (IOException e2) {
					// e2.printStackTrace();
					DialogUtil.getInstance().showDefaultErrorMessage(
							"Could not open the image, perhaps it is defect!");
					break;
				}

				pi = PlanarImage.wrapRenderedImage(bi);
				// ParameterBlockJAI pb = new ParameterBlockJAI("AWTImage");
				// pb.setParameter("awtImage", bi);
				// pi =(PlanarImage)JAI.create("AWTImage", pb);

			}

			// construct the filename
			File newFile = appendTextToImageName(
					files[i],
					"_NR" + String.valueOf(imageToLoad + 1) + "_RF"
							+ String.valueOf(rescale) + "_IP"
							+ String.valueOf(interP));

			pi.setProperty("file_name", newFile.toString());
			pi.setProperty("image_name", newFile.getName().toString());
			if (rescale > 1) {
				pi = rescaleImage(pi, rescale, interP); // PlanarImage,
														// rescaling factor,
														// interpolation option
			}

			// This is necessary because of:
			// Problem if original image is jpeg2000 33005 compressed and
			// rescaled!
			// Exception only when saved as jpeg!
			// java.lang.IllegalArgumentException: Raster ByteInterleavedRaster
			// is incompatible with ColorModel DirectColorModel;
			// Probably a bug in jpeg codec?
			ParameterBlock pb = new ParameterBlock();
			// pb.removeSources();
			// pb.removeParameters();
			pb.addSource(pi);
			pb.add(DataBuffer.TYPE_BYTE);
			pi = JAI.create("format", pb);

			// DEBUG ONLY
			// CommonTools.showImage(pi.getAsBufferedImage(), null);

			logger.debug("Image ["
					+ (imageToLoad + 1)
					+ "] for file ["
					+ newFile.getName()
					+ "] has been formatted, now storing to disk using selected encoding.");

			try {
				if (extension == 0)
					JAI.create("filestore", pi, newFile.toString(), "TIFF");
				if (extension == 1)
					JAI.create("filestore", pi, newFile.toString(), "JPEG");
				if (extension == 2)
					JAI.create("filestore", pi, newFile.toString(), "PNG");
				if (extension == 3)
					JAI.create("filestore", pi, newFile.toString(), "BMP");

				logger.debug("Image ["
						+ (imageToLoad + 1)
						+ "] for file ["
						+ newFile.getName()
						+ "] has been stored to disk using the selected encoding.");

				extractedFiles.add(newFile);

				// Print results
				int[] size = getSizeOfFirstImg(files[i]);
				int firstWidth = size[0];
				int firstHeight = size[1];
				int width = pi.getWidth();
				int height = pi.getHeight();
				float ratioWidth = (float) firstWidth / width;
				float ratioHeight = (float) firstHeight / height;

				// round to 1 decimals
				ratioWidth = ratioWidth * 10;
				ratioHeight = ratioHeight * 10;
				ratioWidth = Math.round(ratioWidth);
				ratioHeight = Math.round(ratioHeight);
				ratioWidth = ratioWidth / 10;
				ratioHeight = ratioHeight / 10;

				if (i == 0) {
					theRatioW = ratioWidth;
					theRatioH = ratioHeight;
				}
				if (i != 0) {
					if (theRatioW != ratioWidth)
						allWidthsEqual = false;
					if (theRatioH != ratioHeight)
						allHeightsEqual = false;
				}

				String strNumber = String.format("%3d", imageToLoad + 1);
				String strWidth = String.format("%6d", width);
				String strHeight = String.format("%6d", height);
				String strRatioWidth = String.format("%5.1f", ratioWidth);
				String strRatioHeight = String.format("%5.1f", ratioHeight);

				BoardPanel
						.appendTextln("File: " + (i + 1) + "/" + files.length);
				BoardPanel.appendTextln("Extracted to: " + newFile);
				BoardPanel.appendTextln("Extracted image number: " + strNumber
						+ "     Width: " + strWidth + "   Height: " + strHeight
						+ "     Width Ratio: " + strRatioWidth + ":1"
						+ "  Height Ratio: " + strRatioHeight + ":1");
			} catch (Exception e) {
				DialogUtil.getInstance().showErrorMessage(
						"Cannot store the extracted image, try TIFF encoding!",
						e, true);
				return false;
			}

		}

		if (allWidthsEqual) {
			BoardPanel.appendTextln("All width ratios are identical: "
					+ theRatioW);
		} else {
			BoardPanel.appendTextln("NOT all width ratios are identical!");
		}
		if (allHeightsEqual) {
			BoardPanel.appendTextln("All heigth ratios are identical: "
					+ theRatioH);
		} else {
			BoardPanel.appendTextln("NOT all height ratios are identical!");
		}

		return true;
	}

	/**
	 * This method appends String(num) to the image name
	 */
	private File appendTextToImageName(File file, String strAppend) {
		String str = file.toString();
		int dotPos = str.lastIndexOf(".");
		String name = str.substring(0, dotPos);
		String ext = str.substring(dotPos + 1); // "svs"
		if (extension == 0)
			ext = IQMConstants.TIF_EXTENSION;
		if (extension == 1)
			ext = IQMConstants.JPG_EXTENSION;
		if (extension == 2)
			ext = IQMConstants.PNG_EXTENSION;
		if (extension == 3)
			ext = IQMConstants.BMP_EXTENSION;
		str = name + strAppend + "." + ext;
		return new File(str);
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		this.firePropertyChange("singleTaskRunning", 0, 1);
		boolean success = this.extract(this.files, this.params);
		this.firePropertyChange("singleTaskRunning", 1, 0);
		logger.info("The extraction was " + (success ? "" : "NOT")
				+ " successful, returning [" + (success ? "true" : "false")
				+ "].");
		return success;
	}

	@Override
	protected void done() {
		try {
			boolean success = this.get();
			if (success) {
				String message = "";
				// display, when either height or width values are not equal
				if (!allHeightsEqual || !allWidthsEqual) {
					message = "Either some height ratios or width ratios are NOT identical!\nPlease check the log (Menu-Info-Open Log Window).";
				} else {
					message = "All height and width ratios are identical.";
				}
				int selection = DialogUtil
						.getInstance()
						.showDefaultQuestionMessage(
								message
										+ "\nDo you want to load the extracted files now?");
				if (selection == IDialogUtil.YES_OPTION) {
					File[] newFiles = new File[this.extractedFiles.size()];
					for (int i = 0; i < newFiles.length; i++) {
						newFiles[i] = this.extractedFiles.get(i);
					}
					Application.getTank().loadImagesFromHD(newFiles);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
			e.printStackTrace();
		} finally {
			System.gc();
		}

	}

	/**
	 * @return the params
	 */
	public int[] getParams() {
		return params;
	}

	/**
	 * @return the imageToLoad
	 */
	public int getImageToLoad() {
		return imageToLoad;
	}

	/**
	 * @return the rescale
	 */
	public int getRescale() {
		return rescale;
	}

	/**
	 * @return the interP
	 */
	public int getInterP() {
		return interP;
	}

	/**
	 * @return the extension
	 */
	public int getExtension() {
		return extension;
	}

	/**
	 * @return the firstWidth
	 */
	public int getFirstWidth() {
		return firstWidth;
	}

	/**
	 * @return the firstHeight
	 */
	public int getFirstHeight() {
		return firstHeight;
	}

	/**
	 * @return the numImages
	 */
	public int getNumImages() {
		return numImages;
	}

	/**
	 * @return the theRatioW
	 */
	public float getTheRatioW() {
		return theRatioW;
	}

	/**
	 * @return the theRatioH
	 */
	public float getTheRatioH() {
		return theRatioH;
	}

	/**
	 * @return the allWidthsEqual
	 */
	public boolean isAllWidthsEqual() {
		return allWidthsEqual;
	}

	/**
	 * @return the allHeightsEqual
	 */
	public boolean isAllHeightsEqual() {
		return allHeightsEqual;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(int[] params) {
		this.params = params;
	}

	/**
	 * @param imageToLoad
	 *            the imageToLoad to set
	 */
	public void setImageToLoad(int imageToLoad) {
		this.imageToLoad = imageToLoad;
	}

	/**
	 * @param rescale
	 *            the rescale to set
	 */
	public void setRescale(int rescale) {
		this.rescale = rescale;
	}

	/**
	 * @param interP
	 *            the interP to set
	 */
	public void setInterP(int interP) {
		this.interP = interP;
	}

	/**
	 * @param extension
	 *            the extension to set
	 */
	public void setExtension(int extension) {
		this.extension = extension;
	}

	/**
	 * @param firstWidth
	 *            the firstWidth to set
	 */
	public void setFirstWidth(int firstWidth) {
		this.firstWidth = firstWidth;
	}

	/**
	 * @param firstHeight
	 *            the firstHeight to set
	 */
	public void setFirstHeight(int firstHeight) {
		this.firstHeight = firstHeight;
	}

	/**
	 * @param numImages
	 *            the numImages to set
	 */
	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}

	/**
	 * @param theRatioW
	 *            the theRatioW to set
	 */
	public void setTheRatioW(float theRatioW) {
		this.theRatioW = theRatioW;
	}

	/**
	 * @param theRatioH
	 *            the theRatioH to set
	 */
	public void setTheRatioH(float theRatioH) {
		this.theRatioH = theRatioH;
	}

	/**
	 * @param allWidthsEqual
	 *            the allWidthsEqual to set
	 */
	public void setAllWidthsEqual(boolean allWidthsEqual) {
		this.allWidthsEqual = allWidthsEqual;
	}

	/**
	 * @param allHeightsEqual
	 *            the allHeightsEqual to set
	 */
	public void setAllHeightsEqual(boolean allHeightsEqual) {
		this.allHeightsEqual = allHeightsEqual;
	}

	/**
	 * @return the files
	 */
	public File[] getFiles() {
		return files;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles(File[] files) {
		this.files = files;
	}

	/**
	 * @return the extractedFiles
	 */
	public Vector<File> getExtractedFiles() {
		return extractedFiles;
	}

	/**
	 * @param extractedFiles
	 *            the extractedFiles to set
	 */
	public void setExtractedFiles(Vector<File> extractedFiles) {
		this.extractedFiles = extractedFiles;
	}

}
