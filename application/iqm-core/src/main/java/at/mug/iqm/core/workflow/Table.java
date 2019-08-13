package at.mug.iqm.core.workflow;

/*
 * #%L
 * Project: IQM - Application Core
 * File: Table.java
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


import java.util.List;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.ITablePanel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.workflow.ITable;
import at.mug.iqm.core.I18N;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class is a static accessor for the current {@link ITablePanel}.
 * 
 * @author Philipp Kainz
 * 
 */
public final class Table implements ITable {

	// class specific logger
	private static final Logger logger = Logger.getLogger(Table.class);

	/**
	 * The current {@link ITablePanel} instance to control.
	 */
	private ITablePanel tablePanel = null;

	private Table() {
		Application.setTable(this);
	}

	public static ITable getInstance() {
		ITable table = Application.getTable();
		if (table == null) {
			table = new Table();
		}
		return table;
	}

	/**
	 * Gets the current {@link ITablePanel} instance.
	 * 
	 * @return a reference to the current instance
	 */
	@Override
	public ITablePanel getTablePanel() {
		return this.tablePanel;
	}

	/**
	 * Sets the {@link ITablePanel} instance to control.
	 * 
	 * @param arg
	 */
	@Override
	public void setTablePanel(ITablePanel arg) {
		this.tablePanel = arg;
	}

	/**
	 * Set the new data to the table.
	 * 
	 * @param tableModel
	 */
	@Override
	public void setNewData(TableModel tableModel) {
		logger.debug("Setting new data...");
		this.tablePanel.setTableModel(tableModel);
		updateMainFrameTitle();
	}

	/**
	 * Set the new data to the table.
	 * 
	 * @param tableModels
	 */
	@Override
	public void setNewData(List<TableModel> tableModels) {
		logger.debug("Setting new multiple data...");
		this.tablePanel.setTableModels(tableModels);
		updateMainFrameTitle();
	}

	/**
	 * Resets the table panel.
	 * 
	 * @see ITablePanel#reset()
	 */
	@Override
	public void reset() {
		this.tablePanel.reset();

		GUITools.getMainFrame().resetTitleBar();
	}

	/**
	 * Returns whether or not the panel currently displays an item.
	 * 
	 * @return <code>true</code>, if an item is displayed, <code>false</code>,
	 *         if not
	 * @see ITablePanel#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.tablePanel.isEmpty();
	}

	/**
	 * Updates the title of the main frame according to single or
	 * multi-selection of table models.
	 * 
	 * @param multiSelection
	 */
	private void updateMainFrameTitle() {
		// update the title of the main frame according to the model name
		try {
			String modelName = "";
			if (this.tablePanel.isMultipleModelsDisplayed()) {
				modelName = I18N.getGUILabelText("application.table.multi");
			} else {
				modelName = this.tablePanel.getTableModel().getModelName();
			}

			GUITools.getMainFrame().setTitle(
					I18N.getGUILabelText(
							"application.frame.main.titleWithModelName",
							IQMConstants.APPLICATION_NAME,
							IQMConstants.APPLICATION_VERSION, modelName));

		} catch (Exception ex) {
			logger.error("Cannot update main frame title from tables: ", ex);
		}
	}
}
