package at.mug.iqm.core.workflow;

/*
 * #%L
 * Project: IQM - Application Core
 * File: Tank.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2021 Helmut Ahammer, Philipp Kainz
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.exception.MultipleMIMETypesException;
import at.mug.iqm.api.exception.UnsupportedMIMETypeException;
import at.mug.iqm.api.gui.ITankPanel;
import at.mug.iqm.api.gui.PlotSelectionFrame;
import at.mug.iqm.api.gui.WaitingDialog;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.workflow.IManager;
import at.mug.iqm.api.workflow.ITank;
import at.mug.iqm.commons.gui.OpenImageDialog;
import at.mug.iqm.commons.util.CompletionWaiter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.FileContentParser;
import at.mug.iqm.commons.util.plot.PlotParser;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.processing.loader.ImageDataBoxLoader;
import at.mug.iqm.core.processing.loader.SynchronousTankItemLoader;
import at.mug.iqm.core.processing.loader.TankItemLoader;
import at.mug.iqm.core.util.ApplicationObserver;
import at.mug.iqm.gui.TankPanel;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class contains a single instance of {@link TankPanel} (if set) and
 * implements static operations on it. If no panel is set, the panel stays
 * <code>null</code>.
 * <p>
 * <b>Changes</b>
 * <ul>
 * <li>2010 06 added fits format support
 * <li>2010 08 added dcm format support
 * <li>2010 09 tif file opening performance increased
 * <li>2010 09 added progress bar functionality for loading image sequences
 * <li>2012 02 21 MAJOR PROJECT CHANGE: Iqm uses the new IqmDataBox class
 * <li>2012 02 24 adapted to new "Iqm2" layout
 * <li>2012 03 29 PK: introduced method getModel() from IqmTankPanel. This
 * substitutes the static member access.
 * <li>2012 04 14 PK: i18n
 * </ul>
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2009
 */
public final class Tank implements ITank {

	// Logging variables
	private static final Logger logger = LogManager.getLogger(Tank.class);
	/**
	 * The tank panel.
	 */
	private TankPanel tankPanel = null;
	/**
	 * The currently selected item (stack) in the tank. Default is
	 * <code>-1</code>.
	 */
	private int currTankIndex = -1;

	/**
	 * The maximum number (index) for the tank. Default is 0.
	 */
	private int maxTankIndex = 0;

	/**
	 * A {@link PropertyChangeSupport} for this workflow control instance.
	 */
	private PropertyChangeSupport pcs = new PropertyChangeSupport(Tank.class);

	/**
	 * A private constructor for this singleton
	 */
	private Tank() {
		// add the status panel as property change listener
		this.pcs.addPropertyChangeListener(GUITools.getStatusPanel());
		// set the singleton
		Application.setTank(this);
	}

	/**
	 * Gets the tank instance within this application.
	 * 
	 * @return the tank associated with this application
	 */
	public static ITank getInstance() {
		ITank tank = Application.getTank();
		if (tank == null) {
			tank = new Tank();
		}
		return tank;
	}

	/**
	 * This method gets the tank panel instance.
	 * 
	 * @return the tank panel, currently held by the class
	 */
	@Override
	public TankPanel getTankPanel() {
		return tankPanel;
	}

	/**
	 * This method sets the tank panel instance in this class.
	 * 
	 * @param tankPanel
	 */
	@Override
	public void setTankPanel(ITankPanel tankPanel) {
		this.tankPanel = (TankPanel) tankPanel;
	}

	/**
	 * This method sets the current tank item number.
	 * <p>
	 * It also highlights the corresponding cell of the {@link JList}.
	 * 
	 * @param arg
	 *            - current tank item number
	 */
	@Override
	public void setCurrIndex(int arg) {
		currTankIndex = arg;
		if (arg >= 0) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					tankPanel.getTankList().setSelectedIndex(currTankIndex);
				}

			});
		}
	}

	/**
	 * This method gets the current tank item number.
	 * 
	 * @return current tank item number
	 */
	@Override
	public int getCurrIndex() {
		return currTankIndex;
	}

	/**
	 * This method sets the maximal tank image number
	 * 
	 * @param arg
	 *            - maximal tank image number
	 */
	@Override
	public void setMaxIndex(int arg) {
		maxTankIndex = arg;
	}

	/**
	 * This method gets the maximal tank item index.
	 * 
	 * @return maximal tank item index
	 */
	@Override
	public int getMaxIndex() {
		return maxTankIndex;
	}

	/**
	 * Tell the {@link Manager} to (re)set for a new tank index.
	 * 
	 * @param tankIndex
	 *            the tank index to generate the new icons from
	 * 
	 * @see IManager#setNewIconsForTankIndex(int)
	 */
	@Override
	public void setManagerForTankIndex(int tankIndex) {
		Manager.getInstance().setNewIconsForTankIndex(tankIndex);
	}

	/**
	 * Gets the entire item stack at a specified index in the tank list.
	 * 
	 * @param index
	 *            the tank list index
	 * 
	 * @see ITankPanel#getModel()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<IqmDataBox> getTankDataAt(int index) {
		if (index < 0 || index > tankPanel.getModel().getSize())
			throw new IllegalArgumentException(
					"Requested index is smaller than zero or greater than amount of items in the tank.");

		return (List<IqmDataBox>) tankPanel.getModel().getElementAt(index);
	}

	/**
	 * This method destroys existing tank items using the FIFO principle
	 * (queuing). First added items are destroyed first, if the number of tank
	 * items exceeds a determined value. The number of items to keep is set in
	 * the option-menu bar in {@link TankPanel}.
	 */
	@Override
	public void destroyItems() {

		int tankElements = tankPanel.getModel().getSize();

		if (tankPanel.getKeepNumber() == ITankPanel.KEEP_ALL) {
			// do nothing
		} else if (tankPanel.getKeepNumber() == ITankPanel.KEEP_1
				&& (tankElements >= 1)) {
			while (tankPanel.getModel().getSize() >= 1)
				tankPanel.getModel().remove(0);
		} else if (tankPanel.getKeepNumber() == ITankPanel.KEEP_10
				&& (tankElements >= 10)) {
			while (tankPanel.getModel().getSize() >= 10)
				tankPanel.getModel().remove(0);
		} else if (tankPanel.getKeepNumber() == ITankPanel.KEEP_50
				&& (tankElements >= 50)) {
			while (tankPanel.getModel().getSize() >= 50)
				tankPanel.getModel().remove(0);
		}

		// run GC for lock release
		System.gc();
	}

	/**
	 * This method adds 1...n new items to the <code>JList</code>.
	 * <p>
	 * This method should be used whenever new items are processed by the
	 * {@link ResultVisualizer} or new items or item stacks are loaded from any
	 * of the "Open" dialogs.
	 * 
	 * @param itemList
	 *            a list of {@link IqmDataBox}es containing displayable items.
	 *            This could be of the following types: {@link DataType#IMAGE} ,
	 *            {@link DataType#PLOT}, {@link DataType#TABLE}, or
	 *            {@link DataType#CUSTOM}
	 */
	@Override
	public synchronized void addNewItems(List<IqmDataBox> itemList) {
		if (itemList == null || itemList.isEmpty())
			return;

		// delete tank elements, according to the user's preferences
		this.destroyItems();

		// we have to check the data type in order to proceed
		switch (itemList.get(0).getDataType()) {
		case IMAGE:
		case PLOT:
		case TABLE:
		case CUSTOM:
			logger.debug("Displayable object detected for the tank!");
			break;

		default:
			DialogUtil.getInstance().showDefaultErrorMessage(
					I18N.getMessage("application.tank.error.content"));
			return;
		}

		TankItemLoader til = new TankItemLoader(itemList, false, -1);
		til.addPropertyChangeListener(GUITools.getStatusPanel());
		til.execute();
	}

	@Override
	public synchronized void update(List<IqmDataBox> replacements, int tankIndex) {
		TankItemLoader til = new TankItemLoader(replacements, true, tankIndex);
		til.addPropertyChangeListener(GUITools.getStatusPanel());
		til.execute();
	}

	@Override
	public synchronized void update(int tankIndex) {
		TankItemLoader til = new TankItemLoader(getTankDataAt(tankIndex), true,
				tankIndex);
		til.addPropertyChangeListener(GUITools.getStatusPanel());
		til.execute();
	}

	/**
	 * This method adds a batch of items (also stacks) sequentially to the
	 * {@link JList} in the tank panel.
	 * <p>
	 * This is used, when the order of items or item stacks is important.
	 * 
	 * @param batch
	 *            <code>List</code> of <code>File[]</code>
	 */
	@Override
	public synchronized void loadNewImagesFromHDSequentially(List<File[]> batch) {
		if (batch == null || batch.isEmpty())
			return;

		this.destroyItems();

		for (Iterator<File[]> iter = batch.iterator(); iter.hasNext();) {
			ImageDataBoxLoader loader = null;
			if (Application.isVirtual()) {
				loader = new ImageDataBoxLoader(iter.next(), true);
			} else if (!Application.isVirtual()) {
				loader = new ImageDataBoxLoader(iter.next(), false);
			}

			if (loader != null) {
				loader.addPropertyChangeListener(GUITools.getStatusPanel());

				// a modal dialog will be visible until the SwingWorker is done
				WaitingDialog dialog = new WaitingDialog(
						I18N.getMessage("application.dialog.waiting.loadingImages"),
						true);
				// add another property change operatorProgressListener (dialog)
				loader.addPropertyChangeListener(new CompletionWaiter(dialog));
				// execute the loader
				loader.execute();
				// display the dialog, it will be disposed by the
				// CompletionWaiter, when
				// the background thread (SwingWorker -> TankDataBoxLoader) is
				// completed.
				dialog.setVisible(true);
			}
		}
		logger.debug("Finished sequential loading of images.");
	}

	/**
	 * This method adds new single items (also stacks) to the <code>JList</code>
	 * in the tank panel. The file list is loaded by a
	 * {@link ImageDataBoxLoader} instance in the background, displaying
	 * progress to the status panel.
	 * <p>
	 * This method is called by the {@link OpenImageDialog}.
	 * 
	 * @param fileList
	 *            a <code>File[]</code> containing new items for the tank list
	 */
	@Override
	public synchronized void loadImagesFromHD(File[] fileList) {
		if (fileList == null || fileList.length == 0)
			return;

		loadImagesFromHD(Arrays.asList(fileList));

	}

	/**
	 * This method adds new single items (also stacks) to the <code>JList</code>
	 * in the tank panel. The file list is loaded by a
	 * {@link ImageDataBoxLoader} instance in the background, displaying
	 * progress to the status panel.
	 * <p>
	 * This method is called by the {@link OpenImageDialog}.
	 * 
	 * @param fileList
	 *            a <code>File[]</code> containing new items for the tank list
	 * @param waitForCompletion
	 *            waits for the loading to be finished
	 */
	@Override
	public synchronized void loadImagesFromHD(File[] fileList,
			boolean waitForCompletion) {
		if (fileList == null || fileList.length == 0)
			return;

		this.destroyItems();

		ImageDataBoxLoader loader = new ImageDataBoxLoader(fileList,
				Application.isVirtual());
		loader.addPropertyChangeListener(GUITools.getStatusPanel());

		if (waitForCompletion) {
			loader.execute();
			try {
				loader.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} else {
			loader.execute();
		}
	}

	/**
	 * This method adds new single items (also stacks) to the <code>JList</code>
	 * in the tank panel. The file list is loaded by a
	 * {@link ImageDataBoxLoader} instance in the background, displaying
	 * progress to the status panel.
	 * <p>
	 * This method is called by dragging images from the file system to the
	 * {@link ITankPanel}.
	 * 
	 * @param fileList
	 *            a {@link List} of {@link File}s containing new items for the
	 *            tank list
	 */
	@Override
	public synchronized void loadImagesFromHD(List<File> fileList) {
		if (fileList == null || fileList.isEmpty())
			return;

		this.destroyItems();

		ImageDataBoxLoader loader = new ImageDataBoxLoader(fileList,
				Application.isVirtual());
		loader.addPropertyChangeListener(GUITools.getStatusPanel());
		WaitingDialog dlg = new WaitingDialog();
		CompletionWaiter wtr = new CompletionWaiter(dlg);
		loader.addPropertyChangeListener(wtr);
		loader.execute();
		dlg.setVisible(true);
	}

	/**
	 * This method gets the {@link IqmDataBox} at the index
	 * <code>mgrIndex</code> of the currently selected tank list cell.
	 * 
	 * @param mgrIndex
	 *            - manager index
	 * @return the data box, or <code>null</code>, if the tank index is not
	 *         valid (i.e. &lt;0)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public IqmDataBox getCurrentTankIqmDataBoxAt(int mgrIndex) {
		IqmDataBox result = null;
		if (mgrIndex == -1) {
			logger.warn("Tank is empty: Index '-1' not allowed, returning null.");
		} else {
			int curr = getCurrIndex();
			if (curr == -1) {
				logger.warn("Current tank index not available, returning null.");
			} else {
				try {
					result = ((List<IqmDataBox>) tankPanel.getModel()
							.getElementAt(curr)).get(mgrIndex);

				} catch (IndexOutOfBoundsException e) {
					logger.error("Current item index not available", e);
					result = null;
				} catch (NullPointerException e) {
					logger.error("Current item index not available", e);
					result = null;
				}
			}
		}
		return result;
	}

	/**
	 * This method gets the {@link IqmDataBox} with the <code>imgIndex</code> at
	 * the index <code>tankIndex</code> (which is a cell) of the tank model.
	 * 
	 * @param tankIndex
	 *            - tank index
	 * @param mgrIndex
	 *            - manager index
	 * @return the data box of the requested item, or <code>null</code>, if the
	 *         tank index is not valid (i.e. &lt;0)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public IqmDataBox getTankIqmDataBoxAt(int tankIndex, int mgrIndex) {
		IqmDataBox result = null;

		// logger.debug("Requesting box #"+mgrIndex + " of tank #" + tankIndex);

		if (mgrIndex == -1) {
			logger.warn("Manager is empty: Index '-1' not allowed, returning null.");
		} else {
			if (tankIndex == -1) {
				logger.warn("Current tank index not available, returning null.");
			} else {
				try {
					result = ((List<IqmDataBox>) tankPanel.getModel()
							.getElementAt(tankIndex)).get(mgrIndex);
				} catch (IndexOutOfBoundsException e) {
					// logger.error("Current item index not available", e);
					result = null;
				} catch (NullPointerException e) {
					// logger.error("Current item index not available", e);
					result = null;
				}
			}
		}
		return result;
	}

	/**
	 * This method gets the {@link IqmDataBox}, which according to the selected
	 * index in the current manager list, at the index of the currently selected
	 * tank list cell.
	 * 
	 * @return the data box, or <code>null</code>, if the tank index is not
	 *         valid (i.e. &lt;0)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public IqmDataBox getSelectedIqmDataBox() {

		IqmDataBox result = null;

		// get the selected manager indices from the list (either left or right)
		int mgrIndex = Manager.getInstance().getCurrItemIndex();

		if (mgrIndex == -1) {
			logger.warn("Manager is empty: Index '-1' not allowed, returning null.");
		} else {
			int curr = getCurrIndex();
			if (curr == -1) {
				logger.warn("Current tank index not available, returning null.");
			} else {
				try {
					result = ((List<IqmDataBox>) tankPanel.getModel()
							.getElementAt(curr)).get(mgrIndex);

				} catch (IndexOutOfBoundsException e) {
					logger.error("Current item index not available: " + e);
					result = null;
				} catch (NullPointerException e) {
					logger.error("Current item index not available: " + e);
					result = null;
				}
			}
		}
		return result;
	}

	/**
	 * This method gets the number of List items
	 * 
	 * @return the number of list items
	 */
	@Override
	public int getNumberOfTankItems() {
		return tankPanel.getTankList().getModel().getSize();
	}

	/**
	 * This method deletes the entire tank list.
	 */
	@Override
	public void deleteAllTankItems() {
		tankPanel.deleteAllIndices();
	}

	/**
	 * This method deletes a range of indices in the tank.
	 */
	@Override
	public void deleteTankItems(int[] indices) {
		tankPanel.deleteSelectedIndices(indices);
		;
	}

	@Override
	public boolean isEmpty() {
		if (currTankIndex == -1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isVirtual(int index) {
		try {
			if (getTankIqmDataBoxAt(index, 0) instanceof IVirtualizable) {
				return true;
			}
		} catch (Exception e) {
		}

		return false;
	}

	@Override
	public void addNewItem(IqmDataBox item) {
		List<IqmDataBox> itemList = new ArrayList<IqmDataBox>(1);
		itemList.add(item);
		this.addNewItems(itemList);
	}

	@Override
	public DataType parseDraggedContent(List<File> fileList) {
		DataType type = null;
		try {
			type = FileContentParser.parseContent(fileList);
			logger.debug("File type detected for loading via drag'n'drop: ["
					+ type.toString() + "]");
		} catch (IllegalArgumentException e) {
			logger.error("", e);
		} catch (MultipleMIMETypesException e) {
			logger.error("", e);
			DialogUtil.getInstance().showErrorMessage("error.mime.multiple", e);
		} catch (UnsupportedMIMETypeException e) {
			logger.error("", e);
			DialogUtil.getInstance().showErrorMessage("error.mime.unsupported",
					e);
		}
		return type;
	}

	/**
	 * Kicks off a {@link PlotParser} instance parsing the file and showing a
	 * {@link PlotSelectionFrame} for choosing data.
	 * 
	 * @param f
	 *            the {@link File} to be parsed
	 */
	public void runPlotParser(File f) {
		// using a PlotParser to read file and show content for selection of
		// data:
		PlotParser pP = new PlotParser(f);
		WaitingDialog dialog = new WaitingDialog();
		pP.addPropertyChangeListener(new CompletionWaiter(dialog));
		pP.addPropertyChangeListener(Application.getMainFrame()
				.getStatusPanel());
		dialog.setVisible(true);
		pP.execute();

		// add the file to the list of recent files
		ApplicationObserver.addToRecentFiles(f, DataType.PLOT);
	}
	
	@Override
	public synchronized void addNewItemsSync(List<IqmDataBox> itemList) {
		if (itemList == null || itemList.isEmpty())
			return;

		// delete tank elements, according to the user's preferences
		this.destroyItems();

		// we have to check the data type in order to proceed
		switch (itemList.get(0).getDataType()) {
		case IMAGE:
		case PLOT:
		case TABLE:
		case CUSTOM:
			logger.debug("Displayable object detected for the tank!");
			break;

		default:
			DialogUtil.getInstance().showDefaultErrorMessage(
					I18N.getMessage("application.tank.error.content"));
			return;
		}

		// synchronized item loading
		SynchronousTankItemLoader stil = new SynchronousTankItemLoader(itemList, false, -1);
		stil.addPropertyChangeListener(GUITools.getStatusPanel());
		stil.load();
	}
	
	@Override
	public void addNewItemSync(IqmDataBox item) {
		// synchronized item loading
		ArrayList<IqmDataBox> list = new ArrayList<IqmDataBox>(1);
		list.add(item);
		this.addNewItemsSync(list);
	}

}// END
