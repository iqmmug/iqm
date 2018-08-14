package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: SaveTableSequDialog.java
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
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;

/**
 * Uses the {@link JFileChooser} for locating the target in order to save a
 * table.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2013 08
 * 
 */
public class SaveTableSequDialog extends AbstractTableSavingDialog {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -6902096710599188281L;

	// Logging variables
	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(SaveTableDialog.class);

	/**
	 * Standard constructor.
	 */
	public SaveTableSequDialog() {
		super();
		init();
	}
	
	public SaveTableSequDialog(File directory){
		super(directory);
		init();
	}

	@Override
	protected void init() {
		super.init();
		this.addPropertyChangeListener(
				JFileChooser.FILE_FILTER_CHANGED_PROPERTY, this);
		this.setDialogTitle(I18N
				.getGUILabelText("application.dialog.saveTableSequ.title"));
		this.setFrameIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.file.saveTable")));
		this.getAccessory().setVisible(false);
	}
}
