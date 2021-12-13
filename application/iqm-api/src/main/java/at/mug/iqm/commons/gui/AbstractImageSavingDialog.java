package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractImageSavingDialog.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2021 Helmut Ahammer, Philipp Kainz
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.config.ConfigManager;

/**
 * An abstract implementation of the {@link ISaveImageDialog}. This class may be
 * subclassed by any dialog required for storing images to the file system.
 * 
 * @author Philipp Kainz
 * @since 3.1
 */
public abstract class AbstractImageSavingDialog extends JFileChooser implements
		ISaveImageDialog {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 3879779302565940024L;
	private static final Logger logger = LogManager.getLogger(AbstractImageSavingDialog.class);

	protected File currImgDir;
	protected boolean extensionExists = false;
	protected JPanel additionalOptionsPanel;
	protected JCheckBox chbxDrawROIs;
	protected FileNameExtensionFilter allFormats;
	protected String encoding;
	protected String extension;

	public AbstractImageSavingDialog() {
		super();
		this.setDialogType(JFileChooser.SAVE_DIALOG);
	}

	public AbstractImageSavingDialog(File file) {
		super(file);
		this.setDialogType(JFileChooser.SAVE_DIALOG);
	}

	@Override
	public abstract File showDialog();

	/**
	 * Resolves the file extension and determines the file encoding. Encoding
	 * string for the JAI library is obtained using {@link #getEncoding()}.
	 * 
	 * @param destination
	 *            the file selected using the dialog
	 * @return the resolved file name
	 */
	protected File resolveDestination(File destination) {
		
		// initialize the variables
		extensionExists = false;
		encoding = "default"; // default
		extension = "default";

		// get the already existing extension
		// and compare it to all known and allowed extensions.
		try {
			extension = destination.toString().substring(
					destination.toString().lastIndexOf("."),
					destination.toString().length());
			extension = extension.substring(1);
			for (String s : allFormats.getExtensions()) {
				if (!s.equals(extension)) {
					extensionExists = false;
				} else {
					extensionExists = true;
					break;
				}
			}
		} catch (NullPointerException npe) {
			extensionExists = false;
			logger.debug(npe);
		} catch (StringIndexOutOfBoundsException se) {
			extensionExists = false;
			logger.debug(se);
		}

		File testFile;
		boolean testFileExists = false;
		if (!extensionExists) {
			// check, whether a special filter is selected and take
			// its extension
			if (this.getFileFilter().getDescription()
					.equals(IQMConstants.TIFF_FILTER_DESCRIPTION)) {
				extension = IQMConstants.TIF_EXTENSION;
			} else if (this
					.getFileFilter()
					.getDescription()
					.equals(I18N
							.getGUILabelText("application.dialog.fileExtensionFilter.imageFiles.supported"))) {
				extension = IQMConstants.TIF_EXTENSION;
			} else if (this.getFileFilter().getDescription()
					.equals(IQMConstants.JPEG_FILTER_DESCRIPTION)) {
				extension = IQMConstants.JPG_EXTENSION;
			} else if (this.getFileFilter().getDescription()
					.equals(IQMConstants.PNG_FILTER_DESCRIPTION)) {
				extension = IQMConstants.PNG_EXTENSION;
			} else if (this.getFileFilter().getDescription()
					.equals(IQMConstants.BMP_FILTER_DESCRIPTION)) {
				extension = IQMConstants.BMP_EXTENSION;
			}

			// test with an pseudo file
			testFile = new File(destination.toString() + "." + extension);

			// look
			testFileExists = testFile.exists();
			extension = "default";
		}

		// file name is not altered until here!
		// check for existing file names (may be saved with selected
		// filter)
		if (destination.exists() || testFileExists) {
			Toolkit.getDefaultToolkit().beep();
			int selected = DialogUtil.getInstance().showDefaultWarnMessage(
					I18N.getMessage("application.fileExists.overwrite"));
			if (selected != IDialogUtil.YES_OPTION) {
				BoardPanel.appendTextln(I18N
						.getMessage("application.imageNotSaved"));
				return null;
			} else {
				// get the already existing extension
				try {
					extension = destination.toString().substring(
							destination.toString().lastIndexOf("."),
							destination.toString().length());
					extension = extension.substring(1);
					for (String s : allFormats.getExtensions()) {
						if (!s.equals(extension)) {
							extensionExists = false;
						} else {
							extensionExists = true;
							break;
						}
					}
				} catch (NullPointerException npe) {
					extensionExists = false;
					logger.debug(npe);
				} catch (StringIndexOutOfBoundsException se) {
					extensionExists = false;
					logger.debug(se);
				}
			}
		}

		if (extensionExists) {
			// check entered file name + extension, if an extension
			// exists
			if (destination.toString().toLowerCase()
					.endsWith(IQMConstants.TIF_EXTENSION)
					|| destination.toString().toLowerCase()
							.endsWith(IQMConstants.TIFF_EXTENSION)) {
				encoding = "TIFF";
				extension = IQMConstants.TIF_EXTENSION;
			} else if (destination.toString().toLowerCase()
					.endsWith(IQMConstants.JPEG_EXTENSION)
					|| destination.toString().toLowerCase()
							.endsWith(IQMConstants.JPG_EXTENSION)) {
				encoding = "JPEG";
				extension = IQMConstants.JPG_EXTENSION;
			} else if (destination.toString().toLowerCase()
					.endsWith(IQMConstants.PNG_EXTENSION)) {
				encoding = "PNG";
				extension = IQMConstants.PNG_EXTENSION;
			} else if (destination.toString().toLowerCase()
					.endsWith(IQMConstants.BMP_EXTENSION)) {
				encoding = "BMP";
				extension = IQMConstants.BMP_EXTENSION;
			}
		}

		// if name differs from the known extensions and is written
		// without extension
		if (encoding.equals("default")) {

			// check, whether a special filter is selected and take
			// its extension
			if (this.getFileFilter().getDescription()
					.equals(IQMConstants.TIFF_FILTER_DESCRIPTION)
					|| this.getFileFilter()
							.getDescription()
							.equals(I18N
									.getGUILabelText("application.dialog.fileExtensionFilter.imageFiles.supported"))) {
				encoding = "TIFF"; // JAI-specific protocol string, do
									// not externalize!
				extension = IQMConstants.TIF_EXTENSION;
			} else if (this.getFileFilter().getDescription()
					.equals(IQMConstants.JPEG_FILTER_DESCRIPTION)) {
				encoding = "JPEG";
				extension = IQMConstants.JPG_EXTENSION;
			} else if (this.getFileFilter().getDescription()
					.equals(IQMConstants.PNG_FILTER_DESCRIPTION)) {
				encoding = "PNG";
				extension = IQMConstants.PNG_EXTENSION;
			} else if (this.getFileFilter().getDescription()
					.equals(IQMConstants.BMP_FILTER_DESCRIPTION)) {
				encoding = "BMP";
				extension = IQMConstants.BMP_EXTENSION;
			}
			// default, if "all files" is chosen and the extension
			// is empty, save file without extension,
			// but tiff encoded.
			// afterwards, the user can manually add ".tif" and
			// open/use the image
			else {
				encoding = "TIFF";
				extension = "default";
			}
		}

		// if required, add extensions
		if (!extension.equals("default") && extensionExists == false) {
			destination = new File(destination.toString() + "." + extension);
		}

		return destination;
	}

	/**
	 * Gets the encoding of the image file according to the extension or
	 * selected file filter.
	 * 
	 * @return a protocol {@link String} for the file writer (JAI)
	 */
	@Override
	public String getEncoding() {
		return encoding;
	}
	
	/**
	 * Gets the extension of the image file.
	 */
	@Override
	public String getExtension() {
		return extension;
	}

	/**
	 * Indicates whether or not the visible ROIs should be superimposed on the
	 * image before exporting.
	 * 
	 * @return <code>true</code>, if all visible ROIs are about to be written to
	 *         the image, <code>false</code> otherwise
	 */
	@Override
	public boolean drawROIs() {
		return chbxDrawROIs.isSelected();
	}

	/**
	 * This method initializes the image saving dialog. It constructs the
	 * supported file extensions and filters as well as the panel with the
	 * additional options (e.g. drawing ROIs on export).
	 */
	protected void init() {
		this.setMultiSelectionEnabled(false);
		
		allFormats = new FileNameExtensionFilter(
				I18N.getGUILabelText("application.dialog.fileExtensionFilter.imageFiles.supported"),
				IQMConstants.TIF_EXTENSION, IQMConstants.TIFF_EXTENSION,
				IQMConstants.JPG_EXTENSION, IQMConstants.JPEG_EXTENSION,
				IQMConstants.PNG_EXTENSION, IQMConstants.BMP_EXTENSION);
		this.addChoosableFileFilter(allFormats);
		this.addChoosableFileFilter(new FileNameExtensionFilter(
				IQMConstants.TIFF_FILTER_DESCRIPTION,
				IQMConstants.TIF_EXTENSION, IQMConstants.TIFF_EXTENSION));
		this.addChoosableFileFilter(new FileNameExtensionFilter(
				IQMConstants.JPEG_FILTER_DESCRIPTION,
				IQMConstants.JPG_EXTENSION, IQMConstants.JPEG_EXTENSION));
		this.addChoosableFileFilter(new FileNameExtensionFilter(
				IQMConstants.PNG_FILTER_DESCRIPTION, IQMConstants.PNG_EXTENSION));
		this.addChoosableFileFilter(new FileNameExtensionFilter(
				IQMConstants.BMP_FILTER_DESCRIPTION, IQMConstants.BMP_EXTENSION));
		this.setFileFilter(allFormats); // default setting

		try {
			currImgDir = ConfigManager.getCurrentInstance().getImagePath();
		} catch (NullPointerException npe) {
			currImgDir = new File(System.getProperty("user.home"));
		}	
		this.setCurrentDirectory(currImgDir);
		
		//set default file name
		String file_name = "Image.png";
		try {
			String image_name = (String) Application.getMainFrame().getTitle();
			int beginIndex = image_name.indexOf("[");
			int endIndex = image_name.indexOf("]");
			file_name = (String) image_name.subSequence(beginIndex+1, endIndex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//use default file name		
		}	
		
		this.setSelectedFile(new File(file_name));
		//System.out.println("AbstractImageSavingDialog: file_name: " + file_name);
		
		this.setPreferredSize(new Dimension(700, 500));
		this.setAccessory(getAdditionalOptionsPanel());
	}

	/**
	 * Initializes the accessory panel.
	 * 
	 * @return a panel with custom saving options
	 */
	protected JPanel getAdditionalOptionsPanel() {
		if (additionalOptionsPanel == null) {
			additionalOptionsPanel = new JPanel();

			JPanel roiOptions = new JPanel(new BorderLayout());
			roiOptions.setBorder(new TitledBorder("Annotation Options"));
			BoxLayout boxlayout = new BoxLayout(roiOptions, BoxLayout.Y_AXIS);
			roiOptions.setLayout(boxlayout);
			
			chbxDrawROIs = new JCheckBox("Draw visible ROIs on export");
			chbxDrawROIs.setToolTipText("draws all visible regions of "
					+ "interest on the image before saving");
			chbxDrawROIs.setSelected(false);

			roiOptions.add(Box.createVerticalGlue());
			roiOptions.add(chbxDrawROIs);

			additionalOptionsPanel.add(roiOptions, BorderLayout.NORTH);
		}
		return additionalOptionsPanel;
	}

}
