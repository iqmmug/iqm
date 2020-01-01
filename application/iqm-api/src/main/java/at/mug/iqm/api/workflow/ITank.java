package at.mug.iqm.api.workflow;

/*
 * #%L
 * Project: IQM - API
 * File: ITank.java
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
import java.util.List;

import at.mug.iqm.api.exception.MultipleMIMETypesException;
import at.mug.iqm.api.gui.ITankPanel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.DataType;

/**
 * This interface defines methods for the the Tank, a major workflow control
 * element.
 * 
 * @author Philipp Kainz
 * @since 3.0
 */
public interface ITank {

	/**
	 * Purges the tank.
	 */
	void deleteAllTankItems();

	/**
	 * Get the number of total tank items.
	 * 
	 * @return
	 */
	int getNumberOfTankItems();

	/**
	 * Fetches the {@link IqmDataBox} instance at a specified tank and a
	 * specified manager index.
	 * 
	 * @param tankIndex
	 *            a custom tank item
	 * @param mgrIndex
	 *            the selected item in the stack at tank position
	 *            <code>tankIndex</code>
	 * @return the data box at the specified location, or <code>null</code> if
	 *         no item is present
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 *             if either one of the requested indices is out of bounds
	 */
	IqmDataBox getTankIqmDataBoxAt(int tankIndex, int mgrIndex)
			throws ArrayIndexOutOfBoundsException;

	/**
	 * Fetches the {@link IqmDataBox} instance at the specified index of the
	 * currently selected Tank item.
	 * 
	 * @param mgrIndex
	 *            the selected stack item at the current tank position
	 * @return the data box at the requested position, or <code>null</code> if
	 *         no item is present
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the requested index is out of bounds
	 */
	IqmDataBox getCurrentTankIqmDataBoxAt(int mgrIndex)
			throws ArrayIndexOutOfBoundsException;

	/**
	 * Fetches the current {@link IqmDataBox} according to the selected item in
	 * the manager. Whether the right or left list represents and maintains the
	 * tank item is determined automatically.
	 * <p>
	 * Note: This method is similar to {@link #getTankDataAt(int)}.
	 * 
	 * @return the data box at the selected location or <code>null</code>, if no
	 *         item is present
	 * 
	 * @see #getTankDataAt(int)
	 */
	IqmDataBox getSelectedIqmDataBox();

	/**
	 * Fetches all data boxes in a specified tank index.
	 * <p>
	 * This method gets the data as List of <code>IqmDataBox</code>es at the
	 * specified index.
	 * 
	 * @param index
	 *            the tank list position
	 * @return a {@link List}
	 */
	List<IqmDataBox> getTankDataAt(int index);

	/**
	 * Loads items from files on the hard drive.
	 * 
	 * @param fileList
	 *            an array of {@link File}s
	 */
	void loadImagesFromHD(File[] fileList);

	/**
	 * Loads items from files on the hard drive.
	 * 
	 * @param fileList
	 *            a {@link List} of {@link File}s
	 */
	void loadImagesFromHD(List<File> fileList);

	/**
	 * Sequentially adds a {@link List} of {@link File}[] to the tank.
	 * 
	 * @param batch
	 */
	void loadNewImagesFromHDSequentially(List<File[]> batch);

	/**
	 * Add a list of {@link IqmDataBox}es as a single item in the tank. This is
	 * an asynchronous method, which will do the loading in background.
	 * 
	 * @param itemList
	 */
	void addNewItems(List<IqmDataBox> itemList);

	/**
	 * Add a list of {@link IqmDataBox}es as a single item in the tank. This is
	 * a <b>synchronous</b> method that is waiting until loading has finished.
	 * 
	 * 
	 * @param itemList
	 */
	void addNewItemsSync(List<IqmDataBox> itemList);

	/**
	 * This method adds 1 new item to the <code>JList</code>. This is an
	 * asynchronous method, which will do the loading in background.
	 * 
	 * @param item
	 *            an {@link IqmDataBox} containing a single displayable item.
	 *            This could be of the following types: {@link DataType#IMAGE} ,
	 *            {@link DataType#PLOT}, {@link DataType#TABLE}, or
	 *            {@link DataType#CUSTOM}
	 */
	void addNewItem(IqmDataBox item);

	/**
	 * This method adds 1 new item to the <code>JList</code>. This is a
	 * <b>synchronous</b> method that is waiting until loading has finished.
	 * 
	 * @param item
	 *            an {@link IqmDataBox} containing a single displayable item.
	 *            This could be of the following types: {@link DataType#IMAGE} ,
	 *            {@link DataType#PLOT}, {@link DataType#TABLE}, or
	 *            {@link DataType#CUSTOM}
	 * 
	 * @see ITank#addNewItem(IqmDataBox)
	 */
	void addNewItemSync(IqmDataBox item);

	/**
	 * Destroys the items according to the current settings.
	 */
	void destroyItems();

	/**
	 * Set the manager for a tank index. This loads the stack to the selected
	 * manager list and displays the first item in the corresponding tab.
	 * 
	 * @param tankIndex
	 */
	void setManagerForTankIndex(int tankIndex);

	/**
	 * Determine whether or not the Tank contains items.
	 * 
	 * @return <code>true</code> if it is empty, <code>false</code> otherwise
	 */
	boolean isEmpty();

	/**
	 * Get the current index.
	 * 
	 * @return
	 */
	int getCurrIndex();

	/**
	 * Set the current index.
	 * 
	 * @param arg
	 */
	void setCurrIndex(int arg);

	/**
	 * Set the tank panel.
	 * 
	 * @param tankPanel
	 */
	void setTankPanel(ITankPanel tankPanel);

	/**
	 * Get the tank panel.
	 * 
	 * @return
	 */
	ITankPanel getTankPanel();

	int getMaxIndex();

	void setMaxIndex(int arg);

	/**
	 * Determine whether a stack at position <code>index</code> is virtual or
	 * not.
	 * 
	 * @param index
	 * @return <code>true</code>, if virtual, <code>false</code> otherwise
	 */
	boolean isVirtual(int index);

	/**
	 * Checks for the {@link DataType} in a file dragged over the
	 * {@link ITankPanel} associated with this {@link ITank} instance.
	 * 
	 * @param fileList
	 *            the {@link List} of files
	 * @return a {@link DataType}
	 * @throws MultipleMIMETypesException
	 *             if the list includes more than one MIME type
	 */
	DataType parseDraggedContent(List<File> fileList)
			throws MultipleMIMETypesException;

	/**
	 * Runs the plot parser for a selected file.
	 * 
	 * @param f
	 *            a file containing signal data
	 */
	void runPlotParser(File f);

	/**
	 * Delete selected indices of the tank.
	 * 
	 * @param indices
	 *            the indices to be deleted
	 */
	void deleteTankItems(int[] indices);

	/**
	 * Updates the specified tank items.
	 * 
	 * @param tankIndex
	 */
	void update(int tankIndex);

	/**
	 * Replaces the specified tank items.
	 * 
	 * @param replacement
	 *            the new items
	 * @param tankIndex
	 *            the items to be replaced
	 */
	void update(List<IqmDataBox> replacement, int tankIndex);

	/**
	 * Loads items from files on the hard drive.
	 * 
	 * @param fileList
	 *            an array of {@link File}s
	 * @param waitForCompletion
	 *            a flag whether to block the caller until file loading
	 *            completed
	 */
	void loadImagesFromHD(File[] fileList, boolean waitForCompletion);
}
