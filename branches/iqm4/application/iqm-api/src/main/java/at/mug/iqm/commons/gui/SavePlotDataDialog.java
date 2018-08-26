package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: SavePlotDataDialog.java
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

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;

/**
 * Uses the {@link JFileChooser} to save the data of a displayed plot.
 * 
 * @author Philipp Kainz
 * @since 3.2
 * @update HA 2018-08  added wav reading writing
 * 
 */
public class SavePlotDataDialog extends AbstractTableSavingDialog {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -2356640360710186980L;

	// Logging variables
	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(SavePlotDataDialog.class);

	/**
	 * Standard constructor.
	 */
	public SavePlotDataDialog() {
		super();
		init();
	}

	public SavePlotDataDialog(File directory) {
		super(directory);
		init();
	}

	@Override
	protected void init() {
		super.init();
		this.addPropertyChangeListener(
				JFileChooser.FILE_FILTER_CHANGED_PROPERTY, this);
		this.setDialogTitle(I18N
				.getGUILabelText("application.dialog.savePlotData.title"));
		this.setFrameIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.file.savePlotData")));
		
		//set default file name
		String file_name = "Plot.txt";
		try {
			String name = (String) Application.getMainFrame().getTitle();
			int beginIndex    = name.indexOf("[");
			int endIndex      = name.lastIndexOf("]");
			file_name = (String) name.subSequence(beginIndex+1, endIndex);		
			file_name = "Plot_" + file_name + ".txt";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//default file name		
		}
		this.setSelectedFile(new File(file_name));
		//System.out.println("SaveTableDialog: file_name: " + file_name);
	}
}
