package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: IManagerPanel.java
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


import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JSlider;

/**
 * This interface declares methods for a manager panel.
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings("rawtypes")
public interface IManagerPanel {

	/**
	 * Check if the toggle preview check box is selected.
	 * 
	 * @return <code>true</code>, if so, <code>false</code> otherwise
	 */
	boolean isCheckBoxTogglePreviewSelected();

	/**
	 * Check if the auto preview check box is selected.
	 * 
	 * @return <code>true</code>, if so, <code>false</code> otherwise
	 */
	boolean isCheckBoxAutoPreviewSelected();

	/**
	 * Check if the "hide/show thumbnails" check box of the right manager list
	 * is selected.
	 * 
	 * @return <code>true</code>, if so, <code>false</code> otherwise
	 */
	boolean isCheckBoxMainRightSelected();

	/**
	 * Check if the right manager is selected.
	 * 
	 * @return <code>true</code>, if so, <code>false</code> otherwise
	 */
	boolean isRadioButtonMainRightSelected();

	/**
	 * Check if the "hide/show thumbnails" check box of the left manager list is
	 * selected.
	 * 
	 * @return <code>true</code>, if so, <code>false</code> otherwise
	 */
	boolean isCheckBoxMainLeftSelected();

	/**
	 * Check if the left manager is selected.
	 * 
	 * @return <code>true</code>, if so, <code>false</code> otherwise
	 */
	boolean isRadioButtonMainLeftSelected();

	/**
	 * Gets the radio button for the right manager list.
	 * 
	 * @return the {@link JRadioButton}
	 */
	JRadioButton getJRadioButtonMainRight();

	/**
	 * Gets the radio button for the left manager list.
	 * 
	 * @return the {@link JRadioButton}
	 */
	JRadioButton getJRadioButtonMainLeft();

	/**
	 * Gets the GUI element for the right manager list.
	 * 
	 * @return the {@link JList}
	 */
	JList getManagerListRight();

	/**
	 * Gets the model of the right manager list.
	 * 
	 * @return the {@link DefaultListModel}
	 */
	DefaultListModel getManagerModelRight();

	/**
	 * Gets the GUI element for the left manager list.
	 * 
	 * @return the {@link JList}
	 */
	JList getManagerListLeft();

	/**
	 * Gets the model of the left manager list.
	 * 
	 * @return the {@link DefaultListModel}
	 */
	DefaultListModel getManagerModelLeft();

	/**
	 * Gets the scroll bar for scrolling through the item numbers.
	 * 
	 * @return a {@link JSlider}
	 */
	JSlider getItemSlider();

	/**
	 * Gets the label for the scroll bar.
	 * 
	 * @return a {@link JLabel}
	 */
	JLabel getLabelItemNr();

	/**
	 * This is a convenient wrapper method for setting single items in a manager
	 * list.
	 * 
	 * @param index
	 *            the index to be selected
	 * @throws NullPointerException
	 * @throws Exception
	 */
	public void setManagerForIndex(int index) throws NullPointerException,
			Exception;

	/**
	 * Sets the manager's selection model according to the user's input on the
	 * {@link JList}.
	 * 
	 * @param indices
	 *            all selected indices from the {@link JList}
	 * @param indexForDisplay
	 *            the index which will be displayed in the item panel
	 * @throws NullPointerException
	 * @throws Exception
	 */
	void setManagerForIndex(int[] indices, int indexForDisplay)
			throws NullPointerException, Exception;

	/**
	 * Set the currently selected manager indices.
	 * 
	 * @param currMgrIdxs
	 */
	void setCurrMgrIdxs(int[] currMgrIdxs);

	/**
	 * Get the currently selected manager indices.
	 * 
	 * @return the currently selected indices as integer array
	 */
	int[] getCurrMgrIdxs();

	/**
	 * Stops the toggling preview, if it is currently running.
	 */
	void resetTogglePreviewIfRunning();

	/**
	 * Start the toggling preview, if the check box is selected.
	 */
	void startTogglePreviewIfSelected();

	/**
	 * Gets the current index for display.
	 * 
	 * @return the current index for display
	 */
	int getIndexForDisplay();

	/**
	 * Sets the current index for display.
	 * 
	 * @param indexForDisplay
	 */
	void setIndexForDisplay(int indexForDisplay);

	/**
	 * De-/activates the right manager list.
	 * 
	 * @param b
	 *            <code>true</code> if the list is supposed to be active,
	 *            <code>false</code> otherwise
	 */
	void setRadioButtonMainRight(boolean b);
	
	/**
	 * De-/activates the left manager list.
	 * 
	 * @param b
	 *            <code>true</code> if the list is supposed to be active,
	 *            <code>false</code> otherwise
	 */
	void setRadioButtonMainLeft(boolean b);
	
	/**
	 * Selects all items in the currently activated list.
	 */
	void selectAll();

}
