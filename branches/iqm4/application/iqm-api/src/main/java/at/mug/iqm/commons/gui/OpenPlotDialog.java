package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: OpenPlotDialog.java
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


import java.awt.Dimension;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.config.ConfigManager;

/**
 * This class is responsible for creating a dialog, where the user can select
 * a plot file for being displayed in a JFreeChart instance.
 * <p>
 * <b>Changes</b>
 * <ul>
 * 	<li>2012 04 04 PK: added button for showing selection dialog again, i18n
 * </ul>
 * 
 * 
 * @author Roland Lohr, Philipp Kainz 
 * @since   2012 02 16
 */
public class OpenPlotDialog extends Thread{

	/**
	 * 
	 */
	// Logging variables
	private static Class<?> caller = OpenPlotDialog.class;
	private static final Logger logger = Logger.getLogger(OpenPlotDialog.class);	

	private File currImgDir;

	/**
	 * Constructs the file chooser and sets initial parameters.
	 */
	public OpenPlotDialog() {}
	
	/**
	 * Runs the dialog.
	 */
	
	public void run(){
		try {
			JFileChooser fc = new JFileChooser();

			fc.setMultiSelectionEnabled(false);
			fc.setDialogTitle(I18N.getGUILabelText("application.dialog.openPlot.title"));
			FileNameExtensionFilter filt = new FileNameExtensionFilter(
					I18N.getGUILabelText("application.dialog.fileExtensionFilter.plotFiles.supported"), 
					IQMConstants.TXT_EXTENSION, IQMConstants.WAV_EXTENSION, IQMConstants.B16_EXTENSION);
			fc.addChoosableFileFilter(filt);

			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.TXT_FILTER_DESCRIPTION, IQMConstants.TXT_EXTENSION));
			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.WAV_FILTER_DESCRIPTION, IQMConstants.WAV_EXTENSION));
			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.B16_FILTER_DESCRIPTION, IQMConstants.B16_EXTENSION));
			fc.setFileFilter(filt);        //default setting

			currImgDir = ConfigManager.getCurrentInstance().getImagePath();
			fc.setCurrentDirectory(currImgDir);
			fc.setPreferredSize(new Dimension(700, 500));

			JFrame frame = new JFrame();
			frame.setIconImage(new ImageIcon(Resources.getImageURL("icon.menu.file.openPlot")).getImage());
			
			int returnVal = fc.showOpenDialog(frame);
			if (returnVal!=JFileChooser.APPROVE_OPTION) {
				logger.info(I18N.getMessage("application.noPlotOpened"));
			} else{
				File file = fc.getSelectedFile();
				
				BoardPanel.appendTextln(I18N.getMessage("application.openingPlot", file.getName().toString()), caller);	

				//set default directory
				currImgDir = fc.getCurrentDirectory();
				
				// update configuration
				ConfigManager.getCurrentInstance().setImagePath(currImgDir);

				// using a PlotParser to read file and show content for selection of data:
				Application.getTank().runPlotParser(file);
			}
			
		} 
		catch (Exception e) {
			logger.error("An error occurred: ", e);
			DialogUtil.getInstance().showErrorMessage(I18N.getMessage("application.error.generic"), e);
		} finally {
		}
	}

}//END
