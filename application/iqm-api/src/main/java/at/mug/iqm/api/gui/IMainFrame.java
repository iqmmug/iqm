package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: IMainFrame.java
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


import javax.swing.JTabbedPane;

/**
 * This interface represents the IQM main frame.
 * 
 * @author Philipp Kainz
 * @since 3.0
 *
 */
public interface IMainFrame extends ITabSwitchSupport {

	/**
	 * Initializes the frame.
	 */
	void initialize();

	/**
	 * This method closes the windows, initiates the deletion of temporary files
	 * and quits IQM.
	 * 
	 * @throws Exception
	 */
	void closeIQMAndExit() throws Exception;

	/**
	 * Gets the current main panel.
	 * 
	 * @return the main panel
	 */
	IMainPanel getMainPanel();

	/**
	 * Gets the current manager panel.
	 * 
	 * @return the manager panel
	 */
	IManagerPanel getManagerPanel();

	/**
	 * Gets the current look panel in this frame. This method does not return
	 * look panels constructed in extra look frames.
	 * 
	 * @return the image canvas panel
	 */
	ILookPanel getLookPanel();

	/**
	 * Get the current plot panel.
	 * 
	 * @return the plot panel
	 */
	IPlotPanel getPlotPanel();

	/**
	 * Gets the current table panel.
	 * 
	 * @return the table panel
	 */
	ITablePanel getTablePanel();

	/**
	 * Gets the current text panel.
	 * 
	 * @return the text panel
	 */
	ITextPanel getTextPanel();

	/**
	 * Gets the current tank panel.
	 * 
	 * @return the tank panel
	 */
	ITankPanel getTankPanel();

	/**
	 * Gets the status panel.
	 * 
	 * @return the status panel
	 */
	IStatusPanel getStatusPanel();

	/**
	 * Gets the item content.
	 * 
	 * @return the {@link JTabbedPane} container
	 */
	JTabbedPane getItemContent();

	/**
	 * Resets the title bar to the default value, as if no item is displayed.
	 */
	void resetTitleBar();
	
	/**
	 * Gets the current main frame title.
	 * 
	 * @return main frame title
	 */
	String getTitle();
}
