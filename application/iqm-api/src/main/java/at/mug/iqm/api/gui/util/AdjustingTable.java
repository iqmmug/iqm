package at.mug.iqm.api.gui.util;

/*
 * #%L
 * Project: IQM - API
 * File: AdjustingTable.java
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


import javax.swing.JTable;

import at.mug.iqm.api.model.TableModel;

public class AdjustingTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7673848940021084730L;

	/**
	 * A model holding the data
	 */
	private TableModel tableModel;

	public AdjustingTable() {
		super();
		this.tableModel = new TableModel();
		this.setModel(this.tableModel);
	}

	public AdjustingTable(TableModel model) {
	}

	private void adjustColumns() {
		if (this.columnModel.getColumnCount() > 0) {
			this.setRowHeight(25);

			for (int i = 0; i < this.columnModel.getColumnCount(); i++) {
				this.columnModel.getColumn(i).setCellRenderer(
						new AdjustingTableCellRenderer());
			}
			// KNOWN ISSUE: PK 2013 04 21: if this is enabled, the last column is not resized properly
//			this.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS); 
			TableColumnAdjuster tca = new TableColumnAdjuster(this);
			tca.adjustColumns();

			// highlight the first table row
			this.setRowSelectionInterval(0, 0);
		}
	}

	public void setModel(TableModel model) {
		super.setModel((TableModel) model);
		this.adjustColumns();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}
}
