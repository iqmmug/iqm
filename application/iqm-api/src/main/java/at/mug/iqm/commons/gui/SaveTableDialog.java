package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: SaveTableDialog.java
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

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;

/**
 * Uses the JFileChooser from Swing to save a Table.
 * 
 * @author Philipp Kainz
 * @since 3.2
 * 
 */
public class SaveTableDialog extends AbstractTableSavingDialog {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 213037595037721833L;

	// Logging variables
	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(SaveTableDialog.class);

	/**
	 * Standard constructor.
	 */
	public SaveTableDialog() {
		super();
		init();
	}

	/**
	 * Standard constructor.
	 */
	public SaveTableDialog(File directory) {
		super(directory);
		init();
	}

	@Override
	protected void init() {
		super.init();
		this.setDialogTitle(I18N
				.getGUILabelText("application.dialog.saveTable.title"));
		this.addPropertyChangeListener(
				JFileChooser.FILE_FILTER_CHANGED_PROPERTY, this);
		this.setFrameIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.file.saveTable")));
			
		//set default file name
		String file_name = "Table.txt";
		try {
			String name = (String) Application.getMainFrame().getTitle();
			int beginIndex    = name.indexOf("[");
			int endIndex      = name.lastIndexOf("]");
			file_name  = (String) name.subSequence(beginIndex+1, endIndex);		
			file_name = "Table_" + file_name + ".txt";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//default file name		
		}
		this.setSelectedFile(new File(file_name));
		//System.out.println("SaveTableDialog: file_name: " + file_name);
	}

}
