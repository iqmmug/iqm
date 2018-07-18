package at.mug.iqm.commons.util.table;

/*
 * #%L
 * Project: IQM - API
 * File: TableTools.java
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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import at.mug.iqm.api.exception.EmptyFileException;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.commons.util.CommonTools;

public final class TableTools {

	/**
	 * Creates an image out of a given {@link JTable}.
	 * 
	 * @param table
	 *            the table to be rendered
	 * @return a {@link PlanarImage} of type {@link BufferedImage#TYPE_INT_RGB}.
	 * 
	 * @see CommonTools#createSnapshot(java.awt.Component)
	 */
	public static PlanarImage toPlanarImage(JTable table) {
		return PlanarImage.wrapRenderedImage(CommonTools.createSnapshot(table));
	}

	/**
	 * Creates an image out of a given {@link TableModel}.
	 * <p>
	 * The method temporarily wraps the TableModel into a JTable and returns the
	 * image of the table
	 * 
	 * @param model
	 *            the table model to be rendered
	 * @return a {@link PlanarImage} of type {@link BufferedImage#TYPE_INT_RGB}.
	 * 
	 * @see #toPlanarImage(JTable)
	 */
	public static PlanarImage toPlanarImage(TableModel model) {
		return PlanarImage.wrapRenderedImage(CommonTools
				.createSnapshot(new JTable(model)));
	}

	/**
	 * This method converts a JTable to the csv format
	 * 
	 * @param table
	 *            JTable
	 * @return text data String
	 */
	public static String convertToCSV(JTable table) {
		String text = "";
		for (int c = 0; c < table.getModel().getColumnCount(); c++) { // Column
																		// Names
			String colName = String.valueOf(table.getModel().getColumnName(c));
			if (colName == null) {
				colName = "";
			}
			colName = colName.replace(",", "");
			text = text + colName;
			if (c < (table.getModel().getColumnCount() - 1)) {
				text = text + ",";
			}
			if (c == (table.getModel().getColumnCount() - 1)) {
				text = text + "\n";
			}
		}

		for (int i = 0; i < table.getModel().getRowCount(); i++) { // Data rows
			for (int j = 0; j < table.getModel().getColumnCount(); j++) {
				int col = table.convertColumnIndexToView(j);
				String currVal = String.valueOf(table.getModel().getValueAt(i,
						col));
				if (currVal == null) {
					currVal = "";
				}
				currVal = currVal.replace(",", "");
				text = text + currVal;
				if (j < (table.getModel().getColumnCount() - 1)) {
					text = text + ",";
				}
				if (j == (table.getModel().getColumnCount() - 1)) {
					text = text + "\n";
				}

			}
		}
		return text;
	}

	/**
	 * This method converts a {@link TableModel} to the csv format. The columns
	 * in the exported data string are appended as they appear in the model.
	 * <p>
	 * If you wish to export a custom order, construct a {@link JTable}, order
	 * the columns and use {@link #convertToCSV(JTable)}.
	 * 
	 * @param tableModel
	 *            the model
	 * @return text data String
	 */
	public static String convertToCSV(TableModel tableModel) {
		String text = "";
		for (int c = 0; c < tableModel.getColumnCount(); c++) { // Column Names
			String colName = String.valueOf(tableModel.getColumnName(c));
			if (colName == null) {
				colName = "";
			}
			colName = colName.replace(",", "");
			text = text + colName;
			if (c < (tableModel.getColumnCount() - 1)) {
				text = text + ",";
			}
			if (c == (tableModel.getColumnCount() - 1)) {
				text = text + "\n";
			}
		}

		for (int i = 0; i < tableModel.getRowCount(); i++) { // Data rows
			for (int j = 0; j < tableModel.getColumnCount(); j++) {
				String currVal = String.valueOf(tableModel.getValueAt(i, j));
				if (currVal == null) {
					currVal = "";
				}
				currVal = currVal.replace(",", "");
				text = text + currVal;
				if (j < (tableModel.getColumnCount() - 1)) {
					text = text + ",";
				}
				if (j == (tableModel.getColumnCount() - 1)) {
					text = text + "\n";
				}
			}
		}
		return text;
	}

	/**
	 * This method converts a JTable to the tab delimited format.
	 * 
	 * @param table
	 *            JTable
	 * @return text data String
	 */
	public static String convertToTabDelimited(JTable table) {
		String text = "";
		for (int c = 0; c < table.getModel().getColumnCount(); c++) { // Column
																		// Names
			String colName = String.valueOf(table.getModel().getColumnName(c));
			if (colName == null) {
				colName = "";
			}
			// colName = colName.replace(",", "");
			text = text + colName;
			if (c < (table.getModel().getColumnCount() - 1)) {
				text = text + "\t"; // Tabulator
			}
			if (c == (table.getModel().getColumnCount() - 1)) {
				text = text + "\n";
			}
		}

		for (int i = 0; i < table.getModel().getRowCount(); i++) { // Data rows
			for (int j = 0; j < table.getModel().getColumnCount(); j++) {
				int col = table.convertColumnIndexToView(j);
				String currVal = String.valueOf(table.getModel().getValueAt(i,
						col));
				if (currVal == null) {
					currVal = "";
				}
				// currVal = currVal.replace(",", "");
				text = text + currVal;
				if (j < (table.getModel().getColumnCount() - 1)) {
					text = text + "\t";
				}
				if (j == (table.getModel().getColumnCount() - 1)) {
					text = text + "\n";
				}

			}
		}
		return text;

	}

	/**
	 * This method converts a {@link TableModel} to the tab delimited format.
	 * 
	 * @param tableModel
	 *            the table model instance to be converted
	 * @return text data String
	 */
	public static String convertToTabDelimited(TableModel tableModel) {
		String text = "";
		for (int c = 0; c < tableModel.getColumnCount(); c++) { // Column
																// Names
			String colName = String.valueOf(tableModel.getColumnName(c));
			if (colName == null) {
				colName = "";
			}
			// colName = colName.replace(",", "");
			text = text + colName;
			if (c < (tableModel.getColumnCount() - 1)) {
				text = text + "\t"; // Tabulator
			}
			if (c == (tableModel.getColumnCount() - 1)) {
				text = text + "\n";
			}
		}

		for (int i = 0; i < tableModel.getRowCount(); i++) { // Data rows
			for (int j = 0; j < tableModel.getColumnCount(); j++) {
				String currVal = String.valueOf(tableModel.getValueAt(i, j));
				if (currVal == null) {
					currVal = "";
				}
				// currVal = currVal.replace(",", "");
				text = text + currVal;
				if (j < (tableModel.getColumnCount() - 1)) {
					text = text + "\t";
				}
				if (j == (tableModel.getColumnCount() - 1)) {
					text = text + "\n";
				}

			}
		}
		return text;
	}

	/**
	 * This method converts a {@link TableModel} to the tab delimited format.
	 * 
	 * @param csvString
	 *            the table model instance to be converted
	 * @return the {@link TableModel}
	 */
	public static TableModel convertToTableModel(String csvString)
			throws EmptyFileException {

		if (csvString == null || csvString.isEmpty()) {
			throw new EmptyFileException();
		}

		// remove any leading or trailing white spaces
		csvString = csvString.trim();

		TableModel tableModel = new TableModel();

		// extract the lines
		String[] lines = csvString.split("\n");

		for (String line : lines) {
			String[] elements = line.split("\t");
			// TODO try to parse the first line as header
			tableModel.addRow(elements);
		}

		return tableModel;
	}

	/**
	 * This method displays a table model in a separate {@link JFrame}.
	 * 
	 * @param tableModel
	 */
	public static void displayTable(TableModel tableModel) {
		JTable table = new JTable(tableModel);
		JFrame frame = new JFrame();
		// Get the size of the default screen
		Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
		int xLoc = dimScreen.width / 3 * 2 - 20;
		int yLoc = dimScreen.height / 4 * 1;
		frame.setLocation(xLoc, yLoc);
		frame.setResizable(true);
		frame.setAlwaysOnTop(true);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // IMPORTANT FOR
															// SCROLLBARS!
		frame.getContentPane().add(new JScrollPane(table));
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * This method converts a {@link javax.swing.table.TableModel} to the
	 * internal {@link TableModel}.
	 * 
	 * @param tm
	 *            the {@link JTable}s {@link TableModel}
	 * @return a new, unnamed instance of {@link TableModel}
	 */
	public static TableModel convertToTableModel(javax.swing.table.TableModel tm) {
		// convert the table's model into internal model
		Object[] columnNames = new Object[tm.getColumnCount()];

		Object[][] rowData = new Object[tm.getRowCount()][tm.getColumnCount()];
		for (int row = 0; row < tm.getRowCount(); row++) {
			for (int col = 0; col < tm.getColumnCount(); col++) {
				rowData[row][col] = tm.getValueAt(row, col);
				columnNames[col] = tm.getColumnName(col);
			}
		}

		return new TableModel(rowData, columnNames);
	}

	/**
	 * This method merges several table models from {@link IqmDataBox}es to a
	 * single model, if the identifier and number of columns match.
	 * 
	 * @param boxList
	 *            the list of data boxes containing the table models
	 * @return a merged table model with the column identifiers of the first
	 *         model as header
	 * @throws IllegalArgumentException
	 *             if the <code>boxList</code> is <code>null</code> or empty
	 */
	public static TableModel mergeBoxes(List<IqmDataBox> boxList) {
		if (boxList == null || boxList.isEmpty()) {
			throw new IllegalArgumentException(
					"Cannot merge table models. The list of IqmDataBoxes must not be empty or null.");
		}

		int nModels = boxList.size();
		IqmDataBox b = boxList.get(0);
		if (b instanceof VirtualDataBox) {
			b = VirtualDataManager.getInstance().load((IVirtualizable) b);
		}
		TableModel firstModel = b.getTableModel();

		if (nModels == 0) {
			return firstModel;
		}

		// clone the model and properties
		TableModel theResult = null;
		try {
			theResult = firstModel.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		int numColumns = theResult.getColumnCount();

		Object[] newDummyValues = new Object[numColumns];
		for (int n = 1; n < nModels; n++) { // model loop
			int numRows = theResult.getRowCount();
			IqmDataBox box = boxList.get(n);
			if (box instanceof VirtualDataBox) {
				box = VirtualDataManager.getInstance().load(
						(IVirtualizable) box);
			}

			TableModel newModel = box.getTableModel();
			int numNewRows = newModel.getRowCount();
			for (int r = 0; r < numNewRows; r++) {// Row loop
				theResult.addRow(newDummyValues);
				for (int c = 0; c < numColumns; c++) { // Columns loop
					Object newValue = newModel.getValueAt(r, c);
					theResult.setValueAt(newValue, r + numRows, c);
				}
			}
		}

		// copy the model name
		theResult.setModelName(firstModel.getModelName());

		return theResult;
	}

	/**
	 * This method merges several table models to a single model, if the
	 * identifier and number of columns match.
	 * 
	 * @param modelList
	 *            the list of data boxes containing the table models
	 * @return a merged table model with the column identifiers of the first
	 *         model as header, or <code>null</code>, if creating the new model
	 *         fails
	 * @throws IllegalArgumentException
	 *             if the <code>modelList</code> is <code>null</code> or empty
	 */
	public static TableModel mergeTableModels(List<TableModel> modelList) {
		if (modelList == null || modelList.isEmpty()) {
			throw new IllegalArgumentException(
					"Cannot merge table models. The list of TableModels must not be empty or null.");
		}

		int nModels = modelList.size();

		// get the first model
		TableModel firstModel = modelList.get(0);
		if (nModels == 1) {
			return firstModel;
		}

		int maxCols = -1;
		// result model contains number of columns of largest model in stack
		TableModel theResultModel = null;
		
		// check for maximum number of columns
		for (TableModel mdl : modelList) {
			int tmp = mdl.getColumnCount();
			if (tmp > maxCols) {
				// clone the model and alter the new reference
				try {
					theResultModel = mdl.clone();
					maxCols = tmp;
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		
		int numColumns = maxCols;
		theResultModel = removeAllRows(theResultModel);
		
		Object[] newDummyValues = new Object[numColumns];
		for (int n = 0; n < nModels; n++) { // model loop
			TableModel nextModel = modelList.get(n);
			int offset = theResultModel.getRowCount();
			int numNewRows = nextModel.getRowCount();
			for (int r = 0; r < numNewRows; r++) {// Row loop
				theResultModel.addRow(newDummyValues);
				for (int c = 0; c < numColumns; c++) { // Columns loop
					Object newValue = null;
					try {
						newValue = nextModel.getValueAt(r, c);
					} catch (ArrayIndexOutOfBoundsException ae) {
						// fill missing columns with 'NaN' values
						newValue = Double.NaN;
					}
					theResultModel.setValueAt(newValue, r + offset, c);
				}
			}
		}

		return theResultModel;
	}
	
	/**
	 * Removes all rows of the table model.
	 * 
	 * @param model the {@link TableModel}
	 * @return the empty {@link TableModel}
	 */
	public static TableModel removeAllRows(TableModel model){
		if (model.getRowCount() > 0) {
            for (int i = model.getRowCount() - 1; i > -1; i--) {
                model.removeRow(i);
            }
        }
		return model;
	}
}
