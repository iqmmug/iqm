package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: SaveSingleDialog.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.commons.io.ImageFileWriter;
import at.mug.iqm.config.ConfigManager;

/**
 * This class extends the {@link JFileChooser} for determining the location in
 * order to save a single image.
 * 
 * Sample usage in the code:
 * <ol>
 * <li>create a dialog object: {@link #SaveSingleImageDialog()}
 * <li>show the dialog and get the target file: {@link #showDialog()}
 * <li>get the encoding: {@link #getEncoding()}
 * <li>run the {@link ImageFileWriter}
 * </ol>
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * 
 */
public class SaveSingleImageDialog extends AbstractImageSavingDialog {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 748811921452389786L;

	// Logging variables
	private static final Logger logger = LogManager.getLogger(SaveSingleImageDialog.class);

	/**
	 * Standard constructor.
	 */
	public SaveSingleImageDialog() {
		super();
		init();
	}

	/**
	 * Create a new file chooser for locating the target file.
	 * 
	 * @param directory
	 */
	public SaveSingleImageDialog(File directory) {
		super(directory);
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
				.getImageURL("icon.menu.file.saveSingle")).getImage());
		
		int returnVal = super.showSaveDialog(frame);

		// react to the user's choice
		if (returnVal != APPROVE_OPTION) {
			logger.info("No image saved.");
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
	protected void init() {
		super.init();
		this.setDialogTitle(I18N
				.getGUILabelText("application.dialog.saveSingle.title"));
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				File f = new SaveSingleImageDialog().showDialog();
				System.out.println(f);
			}
		});
	}
}
