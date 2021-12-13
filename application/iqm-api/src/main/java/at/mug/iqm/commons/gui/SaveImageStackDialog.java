package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: SaveImageStackDialog.java
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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.config.ConfigManager;

/**
 * This class is responsible for saving an image stack to a single file.
 * 
 * @author Philipp Kainz
 * 
 */
public class SaveImageStackDialog extends AbstractImageSavingDialog {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 6950638952885672970L;
	// Logging variables
	private static Class<?> caller = SaveImageStackDialog.class;
	private static final Logger logger = LogManager.getLogger(SaveImageStackDialog.class);

	/**
	 * Standard constructor.
	 */
	public SaveImageStackDialog() {
		super();
		init();
	}

	public SaveImageStackDialog(File file) {
		super(file);
		init();
	}

	/**
	 * Displays the dialog and returns the user's file choice.
	 * 
	 * @return the selected {@link File} object
	 */
	public File showDialog() {
		JFrame frame = new JFrame();
		frame.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.menu.file.saveStack")).getImage());

		int returnVal = super.showSaveDialog(frame);

		// react to the user's choice
		if (returnVal != APPROVE_OPTION) {
			logger.info("No image stack saved.");
			return null;
		}

		File destination = this.getSelectedFile();

		// Save property to XML file
		// update configuration
		ConfigManager.getCurrentInstance().setImagePath(
				this.getCurrentDirectory());

		return resolveDestination(destination);
	}

	@Override
	protected File resolveDestination(File destination) {
		
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
				BoardPanel.appendTextln(
						I18N.getMessage("application.imageStackNotSaved"),
						caller);
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
				encoding = "TIFF"; // JAI-specific protocol string, do
								// not externalize!
				extension = IQMConstants.TIF_EXTENSION;
			}
		}

		// otherwise take the entered file name, add '.tif' and save
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
			}
			// default, if "all files" is chosen and the extension
			// is empty, save file without extension,
			// but tiff encoded.
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

	@Override
	protected void init() {
		this.setMultiSelectionEnabled(false);
		this.setDialogTitle(I18N
				.getGUILabelText("application.dialog.saveStack.title"));

		allFormats = new FileNameExtensionFilter(
				I18N.getGUILabelText("application.dialog.fileExtensionFilter.imageFiles.supported"),
				IQMConstants.TIF_EXTENSION, IQMConstants.TIFF_EXTENSION);
		FileNameExtensionFilter filtTIFF = new FileNameExtensionFilter(
				IQMConstants.TIFF_FILTER_DESCRIPTION,
				IQMConstants.TIF_EXTENSION, IQMConstants.TIFF_EXTENSION);
		this.addChoosableFileFilter(allFormats);
		this.addChoosableFileFilter(filtTIFF);
		this.setFileFilter(filtTIFF); // default setting

		try {
			currImgDir = ConfigManager.getCurrentInstance().getImagePath();
		} catch (NullPointerException npe) {
			currImgDir = new File(System.getProperty("user.home"));
		}

		this.setCurrentDirectory(currImgDir);
		
		//set default file name	
		String file_name = "Image.tif";
		try {
			String image_name = (String) Application.getMainFrame().getTitle();
			int beginIndex = image_name.indexOf("[");
			int endIndex = image_name.indexOf("]");
			file_name = (String) image_name.subSequence(beginIndex+1, endIndex);	
			//eliminate extension
			endIndex = file_name.lastIndexOf(".");
			file_name = (String) file_name.subSequence(0, endIndex);
			//add tif extension
			file_name = file_name +".tif";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//use default file name	
		}	
		this.setSelectedFile(new File(file_name));
		//System.out.println("SaveImageStackDialog: file_name: " + file_name);
		
		this.setPreferredSize(new Dimension(700, 500));
		this.setAccessory(getAdditionalOptionsPanel());
	}
}
