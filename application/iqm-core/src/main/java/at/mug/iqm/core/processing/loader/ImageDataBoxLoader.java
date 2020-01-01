package at.mug.iqm.core.processing.loader;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ImageDataBoxLoader.java
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

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import javax.media.jai.PlanarImage;
import javax.swing.DefaultListModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IDataModel;
import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.commons.io.ImageFileReader;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.util.ApplicationObserver;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.TankPanel;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class is responsible for adding images from the HD to the {@link Tank}
 * list. This class wraps the {@link IDataModel}s into an instance of
 * {@link IqmDataBox} and adds this data box to the {@link DefaultListModel} in
 * the {@link TankPanel}.
 * 
 * @author Philipp Kainz
 */
public class ImageDataBoxLoader extends AbstractProcessingTask {

	/**
	 * The class.
	 */
	private static Class<?> caller = ImageDataBoxLoader.class;
	/**
	 * Logger variable.
	 */
	private static final Logger logger = LogManager.getLogger(ImageDataBoxLoader.class);

	/**
	 * The progress publishing {@link PropertyChangeSupport}.
	 */
	private PropertyChangeSupport pcs;

	/**
	 * The file list.
	 */
	private File[] files;

	/**
	 * Custom constructor.
	 * 
	 * @param files
	 *            the file list as <code>File[]</code>
	 * @param virtual
	 *            if the files should be processed virtually, i.e. temporarily
	 *            be saved to the hard drive
	 */
	public ImageDataBoxLoader(File[] files, boolean virtual) {
		// sort the file array
		Arrays.sort(files);

		this.files = files;
		this.virtual = virtual;

		// register the property change support for this object
		this.setPcs(new PropertyChangeSupport(this));
		// add the operatorProgressListener to the pcs
		this.getPcs().addPropertyChangeListener(GUITools.getStatusPanel());
	}

	/**
	 * Custom constructor for receiving a {@link List} of {@link File} objects.
	 * 
	 * @param files
	 *            the file {@link List}
	 * @param virtual
	 *            if the files should be processed virtually, i.e. temporarily
	 *            be saved to the hard drive
	 */
	public ImageDataBoxLoader(List<File> files, boolean virtual) {
		this(files.toArray(new File[files.size()]), virtual);
	}

	/**
	 * @see at.mug.iqm.api.processing.AbstractProcessingTask#doInBackground()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List<IqmDataBox> doInBackground() throws Exception {
		GUITools.disableMainFrameInteraction();
		this.startTime = System.currentTimeMillis();

		List<IqmDataBox> result = null;
		if (this.isVirtual()) {
			result = this.getBoxListForHD(this.files);
		} else {
			result = this.getBoxListForMemory(this.files);
		}

		if (result == null || result.isEmpty()) {
			return null;
		}

		TankPanel tp = (TankPanel) Tank.getInstance().getTankPanel();
		
		tp.getModel().addElement(result);
		Tank.getInstance().setMaxIndex(tp.getModel().getSize()-1);
		Tank.getInstance().setCurrIndex(tp.getModel().getSize()-1);
		
		return result;
	}

	/**
	 * Currently unused.
	 * 
	 * @param chunks
	 *            the intermediate results
	 */
	@Override
	protected void process(List<Object> chunks) {
	}

	/**
	 * This method adds the constructed List of {@link IqmDataBox}es to the
	 * {@link Tank} list.
	 */
	@Override
	protected void done() {
		try {		
			if (this.get() == null) return;
			
			// perform GUI stuff on the EDT
			TankPanel tp = (TankPanel) Tank.getInstance().getTankPanel();
			int modelSize = tp.getModel().getSize();
			tp.getTankList().ensureIndexIsVisible(modelSize - 1);

			// send icons and data of the last tank item to the manager
			Tank.getInstance().setManagerForTankIndex(
					Tank.getInstance().getMaxIndex());
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			// ####### TIME STUFF
			this.duration = System.currentTimeMillis() - this.startTime;
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("HHH:mm:ss:SSS");
			BoardPanel.appendTextln(
					"Time for loading image(s): " + sdf.format(this.duration),
					caller);
			GUITools.getStatusPanel().resetProgressBarValueStack();
			GUITools.enableMainFrameInteraction();
			System.gc();
		}
	}

	/**
	 * Adds each image in the specified file list to a list of
	 * {@link IqmDataBox} in memory.
	 * 
	 * @param fileList
	 *            the list of files
	 * @return the list of {@link IqmDataBox}es, holding images in memory
	 */
	@SuppressWarnings("static-access")
	public List<IqmDataBox> getBoxListForMemory(File[] fileList) {
		List<IqmDataBox> boxList = new ArrayList<IqmDataBox>();

		// Start loading the items
		int iList = 0;
		int nFiles = fileList.length;

		for (int i = 0; i < nFiles; i++) {
			int proz = (i + 1) * 100;
			proz = proz / nFiles;

			logger.debug("Loading image file #: " + (i + 1) + " of " + nFiles);

			// get file info
			File file = fileList[i];

			try {
				// PK 2012 05 31:
				// let the thread sleep, due to progress bar painting!
				Thread.currentThread().sleep(2L);
			} catch (InterruptedException ignored) {
				return null;
			}

			try {
				// read in the image(s)
				ImageFileReader fr = new ImageFileReader(file);
				List<PlanarImage> list = fr.read(file);

				for (PlanarImage img : list) {

					// create new IqmDataBox object for the image data
					// *************************************************
					ImageModel im = new ImageModel(img);
					im.setModelName(file.getName().toString());
					im.setFileName(file.toString());

					IqmDataBox iqmDataBox = new IqmDataBox(im);
					// *************************************************

					boxList.add(iList, iqmDataBox);

					iList = iList + 1;

					// add the file to the list of recent files
					ApplicationObserver.addToRecentFiles(file, DataType.IMAGE);
				}
			} catch (Exception e) {
				DialogUtil.getInstance().showErrorMessage(
						I18N.getMessage(
								"application.tank.error.openType.failed",
								file.toString()), e, true);
				return null;
			}

			this.setProgress(proz);

		} // END FOR LOOP
		return boxList;
	}

	/**
	 * This method reads each image (stack) from a given list of files and
	 * stores them not in memory but as a file in the temporary directory of
	 * IQM.
	 * <p>
	 * The location to that file will be put to a {@link HashMap} in
	 * {@link VirtualDataManager} and can be queried, if the item needs to be
	 * loaded.
	 * 
	 * @param fileList
	 *            list of files
	 * @return a list of {@link VirtualDataBox}es
	 */
	@SuppressWarnings("static-access")
	public List<IqmDataBox> getBoxListForHD(File[] fileList) {
		List<IqmDataBox> boxList = new ArrayList<IqmDataBox>();

		// ##############################################################
		// CREATE NEW VIRTUAL DIRECTORY FOR THIS STACK
		File virtDir = VirtualDataManager.getNewVirtualDirectory();
		boolean success = virtDir.mkdir();
		if (success) {
			logger.info("Created directory: " + virtDir.toString());
		} else {
			logger.error("Unable to create directory:  " + virtDir.toString());
			DialogUtil.getInstance().showDefaultErrorMessage(
					I18N.getMessage(
							"application.tank.error.virtDirCreate.failed",
							virtDir.toString()));
			return null;
		}

		// ##############################################################
		// Start loading the items
		int iList = 0;
		int nFiles = fileList.length;

		for (int i = 0; i < nFiles; i++) {
			int proz = (i + 1) * 100;
			proz = proz / nFiles;

			logger.debug("Loading image file #: " + (i + 1) + " of " + nFiles);

			// get file info
			File file = fileList[i];

			try {
				// PK 2012 05 31:
				// let the thread sleep, due to progress bar painting!
				Thread.currentThread().sleep(2L);
			} catch (InterruptedException ignored) {
				return null;
			}

			try {
				// read in the image(s)
				ImageFileReader fr = new ImageFileReader(file);
				List<PlanarImage> list = fr.read(file);

				for (PlanarImage img : list) {

					// create new IqmDataBox object for the image data
					// *************************************************
					ImageModel im = new ImageModel(img);
					im.setModelName(file.getName().toString());
					im.setFileName(file.toString());

					IqmDataBox iqmDataBox = new IqmDataBox(im);
					// *************************************************

					// Serialize the entire box's state and return the
					// virtual box's reference
					VirtualDataBox vBox = (VirtualDataBox) VirtualDataManager
							.getInstance().save(iqmDataBox, virtDir);

					boxList.add(iList, vBox);

					iList = iList + 1;

					// add the file to the list of recent files
					ApplicationObserver.addToRecentFiles(file, DataType.IMAGE);
				}
			} catch (Exception e) {
				DialogUtil.getInstance().showErrorMessage(
						I18N.getMessage(
								"application.tank.error.openType.failed",
								file.toString()), e, true);
				return null;
			}

			this.setProgress(proz);

		} // END FOR LOOP

		logger.info("Virtual image(s) stored in " + virtDir.toString());

		return boxList;
	}

	/**
	 * @return the pcs
	 */
	public PropertyChangeSupport getPcs() {
		return pcs;
	}

	/**
	 * @param pcs
	 *            the pcs to set
	 */
	public void setPcs(PropertyChangeSupport pcs) {
		this.pcs = pcs;
	}

}
