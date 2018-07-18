package at.mug.iqm.commons.util.plot;

/*
 * #%L
 * Project: IQM - API
 * File: PlotParser.java
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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.gui.PlotSelectionFrame;
import at.mug.iqm.commons.util.DialogUtil;

/**
 * This class is responsible for reading Plot(time Series)-data from a given
 * file with a certain format (x header + y data lines, each ends with CR LF)
 * and writing it into 2-dim Vector of type String. Afterwards the Vector is
 * forwarded to an instance of {@link PlotSelectionFrame}.
 * 
 * Furthermore, this class can be used for extracting specified data (header,
 * unit: e.g. extractColumn(int column), extractData(Vector&lt;Integer&gt;
 * vecRows, Vector&lt;Integer&gt; vecCols)) from given data
 * (setData(Vector&lt;Vector&lt;Double&gt;&gt; data))
 * 
 * @author Michael Mayrhofer-R.
 * @author Philipp Kainz
 * @since 2012 10 14
 * 
 */

public class PlotParser extends SwingWorker<Vector<Vector<String>>, Void> {

	// Logging variables
	private static final Logger logger = Logger.getLogger(PlotParser.class);

	private File file = null;

	private int numColumns; // number of columns
	private int numRows; // number of rows

	private FileInputStream fin = null;
	private BufferedReader buf = null;

	private String[] headers;
	private String[] units;
	private Vector<Vector<Double>> dataValues = null;
	private Vector<Vector<String>> dataString = null;

	/**
	 * Default constructor.
	 */
	public PlotParser() {
		logger.debug("Creating new instance...");
	}

	/**
	 * Constructs a plot parser for a given file.
	 * 
	 * @param file
	 *            an ASCII encoded plain text file
	 */
	public PlotParser(File file) {
		this();
		this.setFile(file);
	}

	/**
	 * Sets the file to be parsed.
	 * 
	 * @param file
	 *            the file to be parsed
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @param data
	 *            Vector&lt;Vector&lt;Double&gt;&gt;
	 */
	public void setData(Vector<Vector<Double>> data) {
		this.dataValues = data;
		numColumns = this.dataValues.get(0).size();
		numRows = this.dataValues.size();
	}

	/**
	 * @return data Vector&lt;Vector&lt;Double&gt;&gt;
	 */
	public Vector<Vector<Double>> getData() {
		return (dataValues);
	}

	/**
	 * @param headers
	 *            String[]
	 */
	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	/**
	 * @return headers String[]
	 */
	public String[] getHeaders() {
		return headers;
	}

	/**
	 * @param units
	 *            String[]
	 */
	public void setUnits(String[] units) {
		this.units = units;
	}

	/**
	 * @return the units String[]
	 */
	public String[] getUnits() {
		return units;
	}

	/**
	 * @param data
	 *            Vector&lt;Vector&lt;String&gt;&gt;
	 */
	public void setDataString(Vector<Vector<String>> data) {
		this.dataString = data;
		numRows = dataString.size();
		this.numColumns = 0;
		int i = 0;
		Vector<String> lineElements = dataString.get(i);
		// as long as there are more columns in the next row: update numColumns
		while (this.numColumns < lineElements.size()) {
			numColumns = lineElements.size();
			i++;
			lineElements = dataString.get(i);
		}
	}

	/**
	 * @return the data Vector&lt;Vector&lt;String&gt;&gt;
	 */
	public Vector<Vector<String>> getDataString() {
		return dataString;
	}

	/**
	 * @param row
	 *            (int) from which to set the headers String[]
	 */
	public void setHeaders(int row) throws ArrayIndexOutOfBoundsException {
		Vector<String> vecHeaders = this.getRowFromDataString(row);
		this.headers = new String[numColumns];
		for (int i = 0; i < numColumns; i++) {
			if (i>=vecHeaders.size()){
				this.headers[i] = "";
			}
			else {					
				this.headers[i] = vecHeaders.get(i);
			}
		}
	}

	/**
	 * @param row
	 *            (int) from which to set the units String[]
	 */
	public void setUnits(int row) {
		Vector<String> vecUnits = this.getRowFromDataString(row);
		this.units = new String[numColumns];
		for (int i = 0; i < numColumns; i++) {
			this.units[i] = vecUnits.get(i);
		}
	}

	/**
	 * @return the row (int) from the dataString Vector&lt;String&gt;
	 */
	public Vector<String> getRowFromDataString(int row) {
		return dataString.get(row - 1);
	}

	/**
	 * Parse the file's content into a 2D-Vector array: {@link Vector}&lt;
	 * {@link Vector}&lt;{@link String}&gt;&gt;. In the end, pass the parsed
	 * content on to the {@link PlotSelectionFrame} instance in order to enable
	 * the user to choose the data.
	 */
	public Vector<Vector<String>> parseContent() {
		try {
			this.setProgress(5);
			// Prepare vector
			dataString = new Vector<Vector<String>>();

			// first read the file
			fin = new FileInputStream(this.file);
			buf = new BufferedReader(new InputStreamReader(fin));
			
			// search for number of columns, e.g. the first line may be only one
			// string
			// As long as there are more columns in the next row, update
			// numColumns
			String[] lineElements = buf.readLine().split("\t");
			while (numColumns < lineElements.length) {
				numColumns = lineElements.length;
				lineElements = buf.readLine().split("\t");
			}
			this.setProgress(25);
			
			// reload data - now to get whole data out of file
			fin = new FileInputStream(this.file);
			buf = new BufferedReader(new InputStreamReader(fin));

			String bufLine;

			numRows = 0;
			Vector<String> line = new Vector<String>();
			while ((bufLine = buf.readLine()) != null) {
				lineElements = bufLine.split("\t");
				line.clear();
				for (int i = 0; i < lineElements.length; i++) {
					line.add(i, String.valueOf(lineElements[i]));
				}
				dataString.add(new Vector<String>(line));
				numRows++;
			}
			this.setProgress(90);
			fin.close();

		} catch (FileNotFoundException e) {
			logger.error("An error occurred: ", e);
			DialogUtil.getInstance().showErrorMessage(
					I18N.getMessage("application.error.generic"), e, true);
			this.dataString = null;
		} catch (IOException e) {
			logger.error("An error occurred: ", e);
			DialogUtil.getInstance().showErrorMessage(
					I18N.getMessage("application.error.generic"), e, true);
			this.dataString = null;
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
			DialogUtil.getInstance().showErrorMessage(
					I18N.getMessage("application.error.generic"), e, true);
			this.dataString = null;
		} finally {
			if (buf != null) {
				try {
					buf.close();
				} catch (IOException e) {
					// do nothing
				}
			}
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		return this.dataString;
	}

	/**
	 * @return the data of one column (int), all rows Vector&lt;Double&gt;
	 */
	public Vector<Double> extractColumn(int column) {

		Vector<Double> dataValuesTemp = new Vector<Double>();

		for (int i = 0; i < numRows; i++) {
			// break at the first null-entry in a column: 
			if(dataValues.get(i).get(column)==null)
				break;
			dataValuesTemp.add(dataValues.get(i).get(column));
		}

		return dataValuesTemp;
	}

	/**
	 * @param vecRows the vector of row indices
	 * @param vecCols the vector of column indices
	 * @return the data of more columns and rows (Vector&lt;Integer&gt;)
	 *         Vector&lt;Vector&lt;Double&gt;&gt;
	 */
	public Vector<Vector<Double>> extractData(Vector<Integer> vecRows,
			Vector<Integer> vecCols) throws NumberFormatException {

		Vector<Vector<Double>> dataValuesTemp = new Vector<Vector<Double>>();
		Vector<Double> vecTemp = new Vector<Double>();
		@SuppressWarnings("unused")
		String strTemp = null;
		
		Vector<String> row = new Vector<String>();
		
		for (int i = vecRows.get(0) - 1; i < vecRows.get(1); i++) {
			
			vecTemp.clear();			
			row.clear();
			row.addAll(this.dataString.get(i));
			
			for (int j = 0; j < vecCols.size(); j++) {				
				
				// all columns have same length (rows):
				//vecTemp.add(Double.valueOf(this.dataString.get(i).get(
				//		vecCols.get(j))));
				
				// if the columns contain data with different lengths:
				// remaining fields are null(if in the last columns) or are filled with "" (if inbetween) - insert null in vecTemp:							
				
				int colId = vecCols.get(j);
		
				if(colId>=row.size() || row.get(colId).equals(null) || row.get(colId).equals("")) {
					vecTemp.add(null);
				}
				else{
					vecTemp.add(Double.valueOf(row.get(colId)));
				}
				
			}
			dataValuesTemp.add(new Vector<Double>(vecTemp));
		}

		return dataValuesTemp;
	}

	/**
	 * @return the headers from specified columns (Vector&lt;Integer&gt;)
	 *         String[]
	 */
	public String[] extractHeaders(Vector<Integer> vecCols) {
		String[] tempHeaders = new String[vecCols.size()];
		for (int i = 0; i < vecCols.size(); i++) {
			tempHeaders[i] = this.headers[vecCols.get(i)];
		}
		return tempHeaders;
	}

	/**
	 * @return the units from specified columns (Vector&lt;Integer&gt;) String[]
	 */
	public String[] extractUnits(Vector<Integer> vecCols) {
		String[] tempUnits = new String[vecCols.size()];
		for (int i = 0; i < vecCols.size(); i++) {
			tempUnits[i] = this.units[vecCols.get(i)];
		}
		return tempUnits;
	}

	/**
	 * @return the header from specified columns (int) String
	 */
	public String extractHeader(int col) {
		return this.headers[col];
	}

	/**
	 * @return the unit from specified columns (int) String
	 */
	public String extractUnit(int col) {
		return this.units[col];
	}

	@Override
	protected Vector<Vector<String>> doInBackground() throws Exception {
		// build the content
		return parseContent();
	}

	@Override
	protected void done() {
		Vector<Vector<String>> result;
		try {
			result = this.get();
			if (result != null) {
				// creating an instance of PlotSelectionFrame for choosing the data
				PlotSelectionFrame psf = new PlotSelectionFrame(file);
				psf.setDataString(result);
				psf.createAndSelect(null);
				psf.setVisible(true);
			}
		} catch (InterruptedException e) {
			DialogUtil.getInstance().showErrorMessage(null, e, true);
		} catch (ExecutionException e) {
			DialogUtil.getInstance().showErrorMessage(null, e, true);
		}finally{
			this.setProgress(0);
		}
	}
}
