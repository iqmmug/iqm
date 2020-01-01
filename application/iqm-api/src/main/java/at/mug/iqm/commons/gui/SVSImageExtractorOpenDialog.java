package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: SVSImageExtractorOpenDialog.java
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
 * This class is responsible for opening an Aperio SVS file, showing information 
 * about its image pyramid and selecting images to be extracted into defined image
 * formats.
 * For jpeg2000 compressed svs files the loci-tools package is used.
 * See Bio-Formats  http://www.loci.wisc.edu/software/bio-formats
 * <p>
 * <b>Changes</b>
 * <ul>
 * 	<li>2010 01 Aperio jpeg2000 33005 compression
 * 	<li>2010 05 registering tiff reader if reader is not available through native imagio
 * </ul>
 * 
 * @author Philipp Kainz
 * @since   2009 09
 *
 */
public class SVSImageExtractorOpenDialog extends Thread{

	// Logging variables
	private static final Logger logger = LogManager.getLogger(SVSImageExtractorOpenDialog.class);

	private File currImgDir;

	/**
	 * Standard constructor.
	 */
	public SVSImageExtractorOpenDialog(){};

	/**
	 * Runs the dialog.
	 */
	public void run() {

		JFileChooser fc = new JFileChooser();

		fc.setMultiSelectionEnabled(true);
		fc.setDialogTitle(I18N.getGUILabelText("application.dialog.extractSVS.title"));
		FileNameExtensionFilter filt = new FileNameExtensionFilter(IQMConstants.SVS_FILTER_DESCRIPTION, IQMConstants.SVS_EXTENSION);
		fc.addChoosableFileFilter(filt);
		fc.setFileFilter(filt);        //default setting

		currImgDir = ConfigManager.getCurrentInstance().getImagePath();
		fc.setCurrentDirectory(currImgDir);
		fc.setPreferredSize(new Dimension(700, 500));

		JFrame frame = new JFrame();
		frame.setIconImage(new ImageIcon(Resources.getImageURL("icon.menu.file.extractSVS")).getImage());

		int returnVal = fc.showOpenDialog(frame);
		if (returnVal!=JFileChooser.APPROVE_OPTION) {
			logger.info("No image(s) selected for extraction.");
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
			new SVSImageExtractorFrame(files);
		}
	}

}
