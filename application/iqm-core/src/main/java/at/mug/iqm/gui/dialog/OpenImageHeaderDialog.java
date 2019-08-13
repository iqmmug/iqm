package at.mug.iqm.gui.dialog;

/*
 * #%L
 * Project: IQM - Application Core
 * File: OpenImageHeaderDialog.java
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


import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.ImagePreviewPanel;
import at.mug.iqm.commons.util.image.ImageHeaderExtractor;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.gui.TextDisplayFrame;
import at.mug.iqm.gui.TextPanel;

/**
 * This dialog is responsible for opening an image header of a selected image.
 * <p>
 * <b>Changes</b>
 * <ul>
 *  <li>2010 05 added gif format support
 *  <li>2012 04 04 PK: exception handling, added preview panel
 * </ul>
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2009 09
 * 
 */
public class OpenImageHeaderDialog {

	private File currImgDir;

	// Logging variables
	private static Class<?> caller = OpenImageHeaderDialog.class;
	private static final Logger logger = LogManager.getLogger(OpenImageHeaderDialog.class);

	/**
	 * Standard constructor.
	 */
	public OpenImageHeaderDialog() {
	};

	/**
	 * Run the dialog.
	 */
	public void run() {
		try {
			JFileChooser fc = new JFileChooser();

			fc.setMultiSelectionEnabled(true);
			fc.setDialogTitle(I18N
					.getGUILabelText("application.dialog.openHeader.title"));
			FileNameExtensionFilter allFormats = new FileNameExtensionFilter(
					I18N.getGUILabelText("application.dialog.fileExtensionFilter.imageFiles.supported"),
					IQMConstants.TIF_EXTENSION, IQMConstants.TIFF_EXTENSION,
					IQMConstants.JPG_EXTENSION, IQMConstants.JPEG_EXTENSION,
					IQMConstants.PNG_EXTENSION, IQMConstants.BMP_EXTENSION,
					IQMConstants.GIF_EXTENSION, IQMConstants.DCM_EXTENSION,
					IQMConstants.SVS_EXTENSION);
			fc.addChoosableFileFilter(allFormats);
			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.TIFF_FILTER_DESCRIPTION,
					IQMConstants.TIF_EXTENSION, IQMConstants.TIFF_EXTENSION));
			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.JPEG_FILTER_DESCRIPTION,
					IQMConstants.JPG_EXTENSION, IQMConstants.JPEG_EXTENSION));
			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.PNG_FILTER_DESCRIPTION,
					IQMConstants.PNG_EXTENSION));
			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.BMP_FILTER_DESCRIPTION,
					IQMConstants.BMP_EXTENSION));
			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.GIF_FILTER_DESCRIPTION,
					IQMConstants.GIF_EXTENSION));
			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.DCM_FILTER_DESCRIPTION,
					IQMConstants.DCM_EXTENSION));
			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.SVS_FILTER_DESCRIPTION,
					IQMConstants.SVS_EXTENSION));
			fc.setFileFilter(allFormats); // default setting

			currImgDir = ConfigManager.getCurrentInstance().getImagePath();
			fc.setCurrentDirectory(currImgDir);

			// Add Thumbnail Preview
			// Thanks to Jakob Hatzl FH Joanneum Graz for this link:
			// http://www.javalobby.org/java/forums/t49462.html
			ImagePreviewPanel preview = new ImagePreviewPanel();
			fc.setAccessory(preview);
			fc.addPropertyChangeListener(preview);

			JFrame frame = new JFrame();
			frame.setIconImage(new ImageIcon(Resources
					.getImageURL("icon.menu.file.openHeader")).getImage());

			int returnVal = fc.showOpenDialog(frame);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				BoardPanel.appendTextln(
						I18N.getMessage("application.noImagesOpened"), caller);
			} else {
				File[] files = fc.getSelectedFiles();
				if (files.length == 0) { // getSelectedFiles does not work on
											// some JVMs
					files = new File[1];
					files[0] = fc.getSelectedFile();
				}

				String str = "";
				for (int i = 0; i < files.length; i++) {
					str = str + files[i].getName().toString() + ", ";
				}
				str = str.substring(0, str.length() - 2);
				BoardPanel
						.appendTextln(I18N.getMessage(
								"application.openingImageHeaders", str), caller);

				TextPanel tp = new TextPanel();
				TextDisplayFrame tdf = new TextDisplayFrame(tp);
				tdf.setTitle(I18N
						.getGUILabelText("textDisplay.frame.imageHeader.multiple.title"));
				for (int i = 0; i < files.length; i++) {
					tp.writeLine("");
					tp.writeLine("---------------------------------------------------------------------------------");
					tp.writeLine("");
					tp.writeLine("Header of: " + files[i].getName().toString());
					tp.writeText(new ImageHeaderExtractor()
							.getHeaderOfImage(files[i]));
				}
				tp.writeLine("");
				tp.writeLine("---------------------------------------------------------------------------------");
				tdf.setVisible(true);

				// set current image directory for the configuration
				currImgDir = fc.getCurrentDirectory();

				// update configuration
				ConfigManager.getCurrentInstance().setImagePath(currImgDir);
			}
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
			DialogUtil.getInstance().showErrorMessage(
					I18N.getMessage("application.error.generic"), e);
		}
	}

}
