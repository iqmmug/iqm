package at.mug.iqm.commons.io;

/*
 * #%L
 * Project: IQM - API
 * File: TableFileWriter.java
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

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.table.TableTools;

/**
 * This class performs writing table data to files (either plain text or the
 * entire table POJO).
 * 
 * @author Philipp Kainz
 * @since 3.2
 */
public class TableFileWriter implements Runnable {

	// private logging
	private static final Logger logger = Logger
			.getLogger(TableFileWriter.class);

	/**
	 * Saves a table as single file.
	 */
	public static final int MODE_SINGLE = 0;
	/**
	 * Saves a table as sequence with increasing numbers.
	 */
	public static final int MODE_SEQUENCE = 1;

	private ArrayList<String> storedFileNames = new ArrayList<String>();
	protected File destination;
	protected String extension;
	protected int mode;
	protected Object tableData;
	protected List<IqmDataBox> boxes;

	/**
	 * Creates a new object in the specified mode.
	 * 
	 * @param mode
	 *            one of {@link #MODE_SINGLE} or {@link #MODE_SEQUENCE}
	 */
	private TableFileWriter(int mode) {
		this.mode = mode;
	}

	/**
	 * Create a new file writer object for a single table. The data type of
	 * {@link #tableData} determines whether the model is exported or not.
	 * <p>
	 * If {@link #tableData} is an instance of {@link JTable}, then the entire
	 * POJO is serialized.
	 * 
	 * @param destination
	 *            the target file
	 * @param tableData
	 *            the table data, which may either be a full {@link JTable}
	 *            object or a {@link TableModel}
	 * @param extension
	 *            the extension of the target file
	 */
	public TableFileWriter(File destination, Object tableData, String extension) {
		this(MODE_SINGLE);
		this.destination = destination;
		this.tableData = tableData;
		this.extension = extension;
	}

	/**
	 * Create a new file writer object for a sequence of tables. 
	 * <p>
	 * If {@link #tableData} is an instance of {@link JTable}, then the entire
	 * POJO is serialized.
	 * 
	 * @param destination the target file
	 * @param boxes the {@link IqmDataBox} list containing the tables
	 * @param extension the extension of the target files
	 * @param mode the mode, which is required to be {@link #MODE_SEQUENCE}
	 * 
	 * @throws IllegalArgumentException if {@link #mode} equals {@link #MODE_SINGLE}
	 */
	public TableFileWriter(File destination, List<IqmDataBox> boxes,
			String extension, int mode) {
		if (mode == MODE_SINGLE) {
			throw new IllegalArgumentException(
					"The mode must not be MODE_SINGLE on this constructor!");
		}

		this.destination = destination;
		this.boxes = boxes;
		this.extension = extension;
		this.mode = mode;
	}
	
	/**
	 * Create a new file writer object for a single table. 
	 * 
	 * @param destination the target file
	 * @param table a {@link IqmDataBox} containing the table
	 * @param extension the extension of the target files
	 */
	public TableFileWriter(File destination, IqmDataBox table,
			String extension) {
		this(MODE_SINGLE);
		this.destination = destination;
		this.boxes = new ArrayList<IqmDataBox>(1);
		this.boxes.add(table);
		this.extension = extension;
	}

	/**
	 * Writes a single table to the destination file.
	 * 
	 * @throws IOException
	 */
	protected void write() throws IOException {
		// the JTable object is serialized using the object writer
		if (extension.equals(IQMConstants.JTB_EXTENSION)) {
			// serialize the object
			try {
				// clone the table model 
				TableModel mdl = ((TableModel) ((JTable) this.tableData).getModel()).clone();
				
				TableModelListener[] tml = mdl.getTableModelListeners();
				for (TableModelListener l : tml){
					mdl.removeTableModelListener(l);
				}
				
				// convert to JTable
				JTable jtb = new JTable(mdl);
				ObjectFileWriter ofw = new ObjectFileWriter(destination,
						jtb);
				ofw.write();
			} catch (IOException e) {
				DialogUtil.getInstance().showErrorMessage(
						"I/O Exception, cannot write table object!", e, true);
				return;
			} catch (CloneNotSupportedException e) {
				DialogUtil.getInstance().showErrorMessage(
						"I/O Exception, cannot clone table model object!", e, true);
				return;
			}
		} else {
//			Object outputObject = null;
//		
//			// write the plain text file as US-ASCII encoded file
//			if (extension.equals(IQMConstants.CSV_EXTENSION)) {
//
//				outputObject = TableTools.convertToCSV(boxes.get(0)
//						.getTableModel());
//			} else if (extension.equals(IQMConstants.DAT_EXTENSION)) {
//				outputObject = TableTools.convertToTabDelimited(boxes.get(0)
//						.getTableModel());
//			} else if (extension.equals(IQMConstants.TXT_EXTENSION)) {
//				outputObject = TableTools.convertToTabDelimited(boxes.get(0)
//						.getTableModel());
//			} else {
//				throw new IllegalArgumentException("Unknown table extension ["
//						+ this.extension + "], cannot write file.");
//			}
//
//			PlainTextFileWriter ptfw = new PlainTextFileWriter(destination, 
//					String.valueOf(outputObject), "US-ASCII");
//			ptfw.run();
			
			
			// write the plain text file as US-ASCII encoded file
			PlainTextFileWriter ptfw = new PlainTextFileWriter(destination,
			String.valueOf(this.tableData), "US-ASCII");
			ptfw.run();
			
		}

		BoardPanel.appendTextln("Saved table data to file "
				+ destination.toString() + ".");
	}

	/**
	 * Writes multiple tables to files with increasing numbering.
	 * 
	 * @throws IOException
	 */
	protected void writeSequence() throws IOException {
		// get the number of tables to be stored as a sequence
		int length = boxes.size();
		int nDigits = 6;

		String testName;
		String plainName;

		// extract plain file name
		// no extension is selected/entered
		if (extension.equals("default")) {
			// file name e.g. filename_0001
			if (destination.toString().endsWith(
					"_" + String.format("%0" + nDigits + "d", 1))) {
				// take just 'filename'
				plainName = destination.toString().substring(0,
						destination.toString().lastIndexOf("_"));
			}
			// file name ends arbitrarily e.g. filename_subFileName
			else {
				// take the whole entered string
				plainName = destination.toString();
			}
		}

		// if any recognized extension is set
		else {
			// file name e.g. filename_0001.txt
			if (destination.toString().endsWith(
					"_" + String.format("%0" + nDigits + "d", 1) + "."
							+ extension)) {
				// take just 'filename'
				plainName = destination.toString().substring(0,
						destination.toString().lastIndexOf("_"));
			}
			// file name ends arbitrarily e.g. filename_subFileName_.txt
			else if (destination.toString().endsWith("." + extension)) {
				// remove the extension and take 'filename_subFileName_'
				plainName = destination.toString().substring(0,
						destination.toString().lastIndexOf("."));
			}
			// existing file name has been selected via mouse/keyboard
			else {
				plainName = destination.toString();
			}
		}

		// construct a test file name for checking, if the sequence already
		// exists
		testName = plainName + "_" + String.format("%0" + nDigits + "d", 1)
				+ "." + extension;
		File testFile = new File(testName);

		if (testFile.exists()) {
			Toolkit.getDefaultToolkit().beep();

			int selected = DialogUtil.getInstance().showDefaultWarnMessage(
					I18N.getMessage("application.fileExists.overwrite"));
			if (selected != IDialogUtil.YES_OPTION) {
				BoardPanel.appendTextln(I18N
						.getMessage("application.tableNotSaved"));
				return;
			}
		}

		String fileName = "";

		// store each table with the corresponding file name
		for (int n = 0; n < length; n++) {

			// calculate values for progress bar (each image increases the
			// value)
			int proz = (n + 1) * 100;
			proz = proz / length;
			Application.getMainFrame().getStatusPanel()
					.setProgressBarValueStack(proz);

			IqmDataBox box = boxes.get(n);

			if (box instanceof VirtualDataBox) {
				box = VirtualDataManager.getInstance().load(
						(IVirtualizable) box);
			}

			fileName = plainName + "_"
					+ String.format("%0" + nDigits + "d", n + 1) + "."
					+ extension;

			try {
				// prepare object
				Object outputObject = null;

				// convert if necessary
				if (extension.equals(IQMConstants.JTB_EXTENSION)) {
					// JTB extension is always a JTable object
					outputObject = new JTable(box.getTableModel());

					ObjectFileWriter ofw = new ObjectFileWriter(new File(
							fileName), outputObject);
					ofw.run();
				} else {
					if (extension.equals(IQMConstants.CSV_EXTENSION)) {

						outputObject = TableTools.convertToCSV(box
								.getTableModel());
					} else if (extension.equals(IQMConstants.DAT_EXTENSION)) {
						outputObject = TableTools.convertToTabDelimited(box
								.getTableModel());
					} else if (extension.equals(IQMConstants.TXT_EXTENSION)) {
						outputObject = TableTools.convertToTabDelimited(box
								.getTableModel());
					}

					PlainTextFileWriter ptfw = new PlainTextFileWriter(new File(
							fileName), String.valueOf(outputObject), "US-ASCII");
					ptfw.run();
				}

				// write to board
				BoardPanel.appendTextln(I18N.getMessage(
						"application.tableSaved", fileName));

				// add file name to array
				storedFileNames.add(fileName);

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("An error occurred: ", e);
				DialogUtil.getInstance().showErrorMessage(
						I18N.getMessage("application.error.generic"), e);
			}
		}

		// print just the first 15 files to board
		BoardPanel.appendTextln(I18N.getMessage("application.tableSequSaved",
				(storedFileNames.size() > 15) ? storedFileNames.subList(0, 15)
						+ ", ..." : storedFileNames));
		// log ALL saved files anyway
		logger.info("Saved image sequence with encoding " + ": "
				+ storedFileNames.toString());

		// reset progress bar
		Application.getMainFrame().getStatusPanel().setProgressBarValueStack(0);
	}

	@Override
	public void run() {
		try {
			switch (mode) {
			case MODE_SINGLE:
				write();
				break;

			case MODE_SEQUENCE:
				writeSequence();
				break;

			default:
				break;
			}
		} catch (IOException e) {
			logger.error("Error writing file!", e);
		}
	}

}
