package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: SaveImageSequenceDialog.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.config.ConfigManager;

/**
 * This class is responsible for saving a sequence of images.
 * <p>
 * <b>Changes</b>
 * <ul>
 * <li>2009 11 elimination of automatic "_" (at two positions)
 * <li>2010 09 added progress bar functionality
 * <li>2012 04 05 PK: i18n, logging
 * <li>2014 02 HA: Option: Image names instead of automatic image numbers added
 * </ul>
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2009
 */
public class SaveImageSequenceDialog extends AbstractImageSavingDialog {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -7949304658347822868L;

	// Logging variables
	private static final Logger logger = Logger
			.getLogger(SaveImageSequenceDialog.class);

	/**
	 * Standard constructor.
	 */
	public SaveImageSequenceDialog() {
		super();
		init();
	}

	public SaveImageSequenceDialog(File target) {
		super(target);
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
				.getImageURL("icon.menu.file.saveSequ")).getImage());

		int returnVal = super.showSaveDialog(frame);

		// react to the user's choice
		if (returnVal != APPROVE_OPTION) {
			logger.info("No image(s) saved.");
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
				.getGUILabelText("application.dialog.saveSequ.title"));

		String text = "<html><b>Options</b><br><ol>"
				+ "<li>enter custom image name (sequence number gets appended)</li>"
				+ "<li>use '+' for original image name</li>"
				+ "<li>use trailing '$' to skip sequence number</li>"
				+ "</ol>"
				+ "<html>";
		
		this.setToolTipText(text);
	}

}
