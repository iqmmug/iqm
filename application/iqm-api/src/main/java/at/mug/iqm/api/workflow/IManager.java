package at.mug.iqm.api.workflow;

/*
 * #%L
 * Project: IQM - API
 * File: IManager.java
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

import java.awt.image.RenderedImage;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.swing.DefaultListModel;

import at.mug.iqm.api.gui.IManagerPanel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.DataType;

/**
 * This is the interface for the Manager, a central control instance of IQM.
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings("rawtypes")
public interface IManager {

	void resetTogglePreviewIfRunning();

	void startTogglePreviewIfSelected();

	PlanarImage getPreviewImage();

	void setPreviewImage(RenderedImage pi);

	void displayPlotItems(int[] indices);

	void displayItem(int managerIndex);

	void setNewIconsMainRight(DefaultListModel listOfIcons, int tankIndex);

	void setNewIconsMainLeft(DefaultListModel listOfIcons, int tankIndex);

	void setNewIconsForTankIndex(int tankIndex);

	DefaultListModel getIconList(int index, boolean onlyNumbers);

	int getTankIndexInMainRight();

	void setTankIndexInMainRight(int tankIndex);

	int getTankIndexInMainLeft();

	void setTankIndexInMainLeft(int tankIndex);

	void resetRightModel();

	void resetLeftModel();

	/**
	 * Returns a flag whether the right list is active.
	 * 
	 * @return
	 */
	boolean isRightListActive();

	/**
	 * Returns a flag whether the left list is active.
	 * 
	 * @return
	 */
	boolean isLeftListActive();

	/**
	 * De-/activates the right manager list.
	 * 
	 * @param b
	 *            <code>true</code> if the list is supposed to be active,
	 *            <code>false</code> otherwise
	 */
	void setRightListActive(boolean b);

	/**
	 * De-/activates the left manager list.
	 * 
	 * @param b
	 *            <code>true</code> if the list is supposed to be active,
	 *            <code>false</code> otherwise
	 */
	void setLeftListActive(boolean b);

	/**
	 * Returns the index of the active manager list.
	 * 
	 * @return 0, for the left list and 1 for the right list
	 */
	int getActiveListIndex();

	DefaultListModel getManagerModelRight();

	DefaultListModel getManagerModelLeft();

	IManagerPanel getManagerPanel();

	void setManagerPanel(IManagerPanel arg);

	void initialize();

	/**
	 * Gets the index of the item currently being displayed in the viewport.
	 * 
	 * @return an integer
	 */
	int getCurrItemIndex();

	/**
	 * Sets the index of the item currently being displayed in the viewport.
	 * 
	 * @param arg
	 *            the index as integer
	 */
	void setCurrItemIndex(int arg);

	DataType getDataBoxTypeInSelectedManager();

	void displayTableItems(int[] indices);

	/**
	 * Gets a list of references to the currently selected data boxes.
	 * 
	 * @return a {@link List} of {@link IqmDataBox}es
	 */
	List<IqmDataBox> getSelectedDataBoxes();
}
