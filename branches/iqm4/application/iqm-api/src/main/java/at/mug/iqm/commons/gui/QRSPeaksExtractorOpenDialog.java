package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: QRSPeaksExtractorOpenDialog.java
 * 
 * $Id: QRSPeaksExtractorOpenDialog.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-api/src/main/java/at/mug/iqm/commons/gui/QRSPeaksExtractorOpenDialog.java $
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.Resources;
import at.mug.iqm.config.ConfigManager;

/**
 * This class is responsible for opening an ECG file and extracting QRS peak times.
 * Hamilton, Tompkins, W. J., "Quantitative investigation of QRS detection rules using the MIT/BIH arrhythmia database",IEEE Trans. Biomed. Eng., BME-33, pp. 1158-1165, 1987.
 * osea4java	 
 * <p>
 * <b>Changes</b>
 * <ul>
 * 	<li>
 * 	<li>
 * </ul>
 * 
 * @author HA
 * @since  2018-11
 *
 */
public class QRSPeaksExtractorOpenDialog extends Thread{

	// Logging variables
	private static final Logger logger = LogManager.getLogger(QRSPeaksExtractorOpenDialog.class);

	private File currImgDir;

	/**
	 * Standard constructor.
	 */
	public QRSPeaksExtractorOpenDialog(){};

	/**
	 * Runs the dialog.
	 */
	public void run() {

		JFileChooser fc = new JFileChooser();

		fc.setMultiSelectionEnabled(true);
		fc.setDialogTitle(I18N.getGUILabelText("application.dialog.extractQRSPeaks.title"));
		FileNameExtensionFilter filt = new FileNameExtensionFilter(IQMConstants.B16_FILTER_DESCRIPTION, IQMConstants.B16_EXTENSION);
		fc.addChoosableFileFilter(filt);
		fc.setFileFilter(filt);        //default setting

		currImgDir = ConfigManager.getCurrentInstance().getImagePath();
		fc.setCurrentDirectory(currImgDir);
		fc.setPreferredSize(new Dimension(700, 500));

		JFrame frame = new JFrame();
		frame.setIconImage(new ImageIcon(Resources.getImageURL("icon.menu.file.extractSVS")).getImage());

		int returnVal = fc.showOpenDialog(frame);
		if (returnVal!=JFileChooser.APPROVE_OPTION) {
			logger.info("No ECG files(s) selected for extraction.");
			return;
		} else{
			currImgDir = fc.getCurrentDirectory();
			
			// update configuration
			ConfigManager.getCurrentInstance().setImagePath(currImgDir);
			
			// get the selected files
			File[] files = fc.getSelectedFiles();	
			if (files.length==0) { // getSelectedFiles does not work on some JVMs
				files = new File[1];
				files[0] = fc.getSelectedFile();
			}

			// run Extraction GUI and pass selected files
			new QRSPeaksExtractorFrame(files);
		}
	}

}
