package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: OpenImageDialog.java
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
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

 
 

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.Resources;
import at.mug.iqm.commons.util.ImagePreviewPanel;
import at.mug.iqm.config.ConfigManager;

/**
 * This dialog is responsible for selecting one or more image files to be
 * loaded.
 * <p>
 * <b>Changes</b>
 * <ul>
 * <li>2010 05 added gif format support
 * <li>2010 06 added fits format support
 * <li>2010 08 added dcm format support
 * <li>2012 04 02 PK: i18n
 * </ul>
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2009
 * 
 */
public class OpenImageDialog extends JFileChooser {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 8349949857606692581L;
	// Logging variables
	  

	private File currImgDir = null;

	/**
	 * Standard constructor.
	 */
	public OpenImageDialog() {
		super();
		init();
	}

	/**
	 * Create an {@link OpenImageDialog} in a specific directory.
	 * 
	 * @param directory
	 */
	public OpenImageDialog(File directory) {
		super(directory);
		init();
	}

	/**
	 * Shows the dialog and returns the list of selected files.
	 * 
	 * @return a file array
	 */
	public File[] showDialog() {
		JFrame frame = new JFrame();
		frame.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.menu.file.open")).getImage());

		int returnVal = this.showOpenDialog(frame);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			System.out.println("IQM Info: "+I18N.getMessage("application.noImagesOpened"));
			return null;
		}

		File[] files = this.getSelectedFiles();

		// set current image directory for the configuration
		ConfigManager.getCurrentInstance().setImagePath(getCurrentDirectory());

		return files;
	}

	/**
	 * Initializes the open dialog.
	 */
	protected void init() {
		this.setMultiSelectionEnabled(true);
		this.setDialogTitle(I18N
				.getGUILabelText("application.dialog.openImages.title"));
		FileNameExtensionFilter allFormats = new FileNameExtensionFilter(
				I18N.getGUILabelText("application.dialog.fileExtensionFilter.imageFiles.supported"),
				IQMConstants.TIF_EXTENSION, IQMConstants.TIFF_EXTENSION,
				IQMConstants.JPG_EXTENSION, IQMConstants.JPEG_EXTENSION,
				IQMConstants.PNG_EXTENSION, IQMConstants.BMP_EXTENSION,
				IQMConstants.GIF_EXTENSION, IQMConstants.DCM_EXTENSION,
				IQMConstants.FITS_EXTENSION);
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
		this.addChoosableFileFilter(new FileNameExtensionFilter(
				IQMConstants.GIF_FILTER_DESCRIPTION, IQMConstants.GIF_EXTENSION));
		this.addChoosableFileFilter(new FileNameExtensionFilter(
				IQMConstants.DCM_FILTER_DESCRIPTION, IQMConstants.DCM_EXTENSION));
		this.addChoosableFileFilter(new FileNameExtensionFilter(
				IQMConstants.FITS_FILTER_DESCRIPTION,
				IQMConstants.FITS_EXTENSION));
		this.setFileFilter(allFormats); // default setting

		try {
			currImgDir = ConfigManager.getCurrentInstance().getImagePath();
		} catch (NullPointerException npe) {
			currImgDir = new File(System.getProperty("user.home"));
		}

		this.setCurrentDirectory(currImgDir);

		// Add thumbnail preview
		// Thanks to Jakob Hatzl, FH Joanneum Graz for this link:
		// http://www.javalobby.org/java/forums/t49462.html
		ImagePreviewPanel preview = new ImagePreviewPanel();
		this.setAccessory(preview);
		this.addPropertyChangeListener(preview);
		this.setPreferredSize(new Dimension(700, 500));
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				File[] files = new OpenImageDialog().showDialog();
				System.out.println(files);
			}
		});
	}
}
