package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: PlotSelectionFrame.java
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


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.util.CheckBoxTableHeader;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.CompletionWaiter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.plot.PlotParser;

/**
 * This class is responsible for displaying a 2-dim Vector
 * Vector&lt;Vector&lt;String&gt;&gt; dataString. Columns (1 x-data, n y-data),
 * headers, units and an interval of rows can be selected which are extracted
 * into Vector&lt;Vector&lt;Double&gt;&gt; dataValues (by using a PlotParser)
 * and written into PlotModels, which are added to the tank.
 * 
 * @author Michael Mayrhofer-R., Philipp Kainz
 * @since 2012 10 14
 * 
 */

public class PlotSelectionFrame extends JFrame implements ActionListener,
		ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5939949181941247476L;

	// Logging variables
	private static final Logger logger = LogManager.getLogger(PlotSelectionFrame.class);

	private JPanel jPanelRowHeader = null;
	private JLabel jLabelRowHeader = null;
	private JSpinner jSpinnerRowHeader = null;

	private JPanel jPanelRowUnits = null;
	private JLabel jLabelRowUnits = null;
	private JSpinner jSpinnerRowUnits = null;

	private JPanel jPanelRowStart = null;
	private JLabel jLabelRowStart = null;
	private JSpinner jSpinnerRowStart = null;

	private JPanel jPanelRowEnd = null;
	private JLabel jLabelRowEnd = null;
	private JSpinner jSpinnerRowEnd = null;

	private JPanel jPanelColRange = null;
	private JLabel jLabelColRange = null;
	private JSpinner jSpinnerColRange = null;

	private JButton buttOk      = null;
	private JButton selectAll   = null;
	private JButton unSelectAll = null;

	private DefaultTableModel model = null;
	private JTable jTable = null;

	private String[] dataHeaders;
	private String[] dataUnits;
	private Vector<Double> domain;

	private int numColumns;
	private int numRows;

	private Vector<Integer> columnSelection;

	private Vector<Vector<Double>> dataValues = null;
	private Vector<Vector<String>> dataString = null;
	
	private File file = null;

	public PlotSelectionFrame() {
		logger.debug("Creating a new instance of '" + this.getClass().getName()
				+ "'...");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public PlotSelectionFrame(File file){
		this();
		this.file = file;
	}

	public PlotSelectionFrame(Vector<Vector<String>> data) {
		this();
		this.setDataString(data);
	}

	public Vector<Vector<String>> getDataString() {
		return this.dataString;
	}

	public void setDataString(Vector<Vector<String>> data) {
		this.dataString = data;
		numRows = data.size();
		numColumns = data.get(1).size();
	}

	public Vector<Vector<Double>> getData() {
		return this.dataValues;
	}

	public void setData(Vector<Vector<Double>> data) {
		this.dataValues = data;
	}

	public void seDatatHeaders(String[] dataHeaders) {
		this.dataHeaders = dataHeaders;
	}

	public String[] getDataHeaders() {
		return this.dataHeaders;
	}

	public String getDataHeader(int i) {
		if (this.dataHeaders == null) {
			return null;
		} else {
			return this.dataHeaders[i];
		}
	}

	public void setDataUnits(String[] units) {
		this.dataUnits = units;
	}

	public String[] getDataUnits() {
		return this.dataUnits;
	}

	public String getDataUnit(int i) {
		if (this.dataUnits == null) {
			return null;
		} else {
			return this.dataUnits[i];
		}
	}

	public int getNumColumns() {
		return numColumns;
	}

	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}

	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public Vector<Double> getDomain() {
		return this.domain;
	}

	public void setDomain(Vector<Double> domain) {
		this.domain = domain;
	}

	public String[] getRowFromDataString(int row) {
		return this.dataUnits;
	}

	public void createAndSelect(JTableHeader preExistingHeader) {
		this.createAndShowJTableGUI(preExistingHeader);
	}

	private void createAndShowJTableGUI(JTableHeader preExistingHeader) {
		logger.debug("Creating GUI and displaying table...");

		// Prepare table
		model = new DefaultTableModel();
		jTable = new JTable(model);

		// adding a lot of columns would be very slow due to active model
		// listener
		model.removeTableModelListener(jTable);

		// this vector stores the current column selection
		columnSelection = new Vector<Integer>();

		// PK: don't add the first column automatically
		// columnSelection.add(0);

		// add columns to table
		// first column should be the line number, so there are numColumns+1
		// columns
		for (int c = 0; c <= numColumns; c++) { // first column should be the
												// line number
			model.addColumn((c > 0) ? String.valueOf(c) : "#");
		}

		// fill in the rows
		Vector<String> vectorTemp = new Vector<String>();
		for (int i = 0; i < numRows; i++) {
			vectorTemp.clear();
			vectorTemp.add(String.valueOf(i + 1));
			vectorTemp.addAll(dataString.get(i));
			model.addRow(new Vector<String>(vectorTemp));
		}

		model.addTableModelListener(jTable);
		model.fireTableStructureChanged();
		jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// table headers with checkboxes
		for (int i = 1; i < jTable.getModel().getColumnCount(); i++) {
			TableColumn tc = jTable.getColumnModel().getColumn(i);
			CheckBoxTableHeader cbth = null;
			if (preExistingHeader == null){
				cbth = new CheckBoxTableHeader(this);
			} else {
				cbth = new CheckBoxTableHeader(this, String.valueOf(preExistingHeader.getColumnModel().getColumn(i-1).getHeaderValue()));
			}
			tc.setHeaderRenderer(cbth);
		}

		JScrollPane scrollpane = new JScrollPane(jTable);
	
		// panel with selection row1:
		JPanel panelSelectionInput1 = new JPanel(new FlowLayout());
		panelSelectionInput1.add(getJPanelRowHeader());
		panelSelectionInput1.add(getJPanelRowUnits());
		panelSelectionInput1.add(getJPanelColRange());
		panelSelectionInput1.add(getJPanelRowStart());
		panelSelectionInput1.add(getJPanelRowEnd());
		panelSelectionInput1.add(getJButtonOk());
		
		// panel with selection row2
		JPanel panelSelectionInput2 = new JPanel(new FlowLayout());
		panelSelectionInput2.add(getJButtonSelectAll());
		panelSelectionInput2.add(getJButtonUnSelectAll());
		
		//combine selection rows to one panel
		JPanel panelSelectionInput = new JPanel(new BorderLayout());
		panelSelectionInput.add(panelSelectionInput1, BorderLayout.NORTH);
		panelSelectionInput.add(panelSelectionInput2, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout()); // only for automatic size readjust
		panel.add(panelSelectionInput, BorderLayout.NORTH);
		// scrollpane with Table:
		panel.add(scrollpane, BorderLayout.CENTER);

		this.setResizable(true);

		// the table with the data gets a grey icon
		this.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.application.grey.32x32")).getImage());
		this.add(panel);
		this.pack();
		CommonTools.centerFrameOnScreen(this);

		logger.debug("Done.");
	}

	private JPanel getJPanelRowHeader() {

		if (jPanelRowHeader == null) {
			jPanelRowHeader = new JPanel();
			// jPanelRowStart.setLayout(new FlowLayout());
			jLabelRowHeader = new JLabel(
					I18N.getGUILabelText("application.dialog.openPlot.tableWindow.lblRowHeader.text"));
			// jPanelRowStart.setPreferredSize(new Dimension(100, 22));
			// jPanelRowStart.setMaximumSize(new Dimension(100, 22));
			// jLabelRowStart.setHorizontalAlignment(JLabel.LEFT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0,
					getRowCount() - 1, 1); // init, min, max, step
			jSpinnerRowHeader = new JSpinner(sModel);

			// jSpinnerRowStart.setPreferredSize(new Dimension(100, 22));
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRowHeader
					.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf
					.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter
					.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			ftf.setEditable(true);
			jSpinnerRowHeader.setValue(0);

			jPanelRowHeader.add(jLabelRowHeader);
			jPanelRowHeader.add(jSpinnerRowHeader);
		}
		return jPanelRowHeader;
	}

	private JPanel getJPanelRowUnits() {

		if (jPanelRowUnits == null) {
			jPanelRowUnits = new JPanel();
			// jPanelRowStart.setLayout(new FlowLayout());
			jLabelRowUnits = new JLabel(
					I18N.getGUILabelText("application.dialog.openPlot.tableWindow.lblRowUnits.text"));
			// jPanelRowStart.setPreferredSize(new Dimension(100, 22));
			// jPanelRowStart.setMaximumSize(new Dimension(100, 22));
			// jLabelRowStart.setHorizontalAlignment(JLabel.LEFT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0,
					getRowCount() - 1, 1); // init, min, max, step
			jSpinnerRowUnits = new JSpinner(sModel);
			// jSpinnerRowStart.setPreferredSize(new Dimension(100, 22));
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRowUnits
					.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf
					.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter
					.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
												// ;
			ftf.setEditable(true);
			jSpinnerRowUnits.setValue(0); // only in order to set format pattern
			// jSpinnerRowUnits.setValue(1); //only in order to set format
			// pattern
			// jSpinnerRowStart.addChangeListener(this);

			jPanelRowUnits.add(jLabelRowUnits);// , BorderLayout.WEST);
			jPanelRowUnits.add(jSpinnerRowUnits);// , BorderLayout.CENTER);
			// jPanelRowStart.setAlignmentY(Component.LEFT_ALIGNMENT);
		}
		return jPanelRowUnits;
	}

	private JPanel getJPanelColRange() {

		if (jPanelColRange == null) {
			jPanelColRange = new JPanel();

			jLabelColRange = new JLabel(
					I18N.getGUILabelText("application.dialog.openPlot.tableWindow.lblColRange.text"));

			SpinnerModel sModel = new SpinnerNumberModel(0, 0, getNumColumns(),
					1); // init, min, max, step
			jSpinnerColRange = new JSpinner(sModel);

			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerColRange
					.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf
					.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter
					.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
												// ;
			ftf.setEditable(true);
			jSpinnerColRange.setValue(0); // only in order to set format pattern

			jPanelColRange.add(jLabelColRange);// , BorderLayout.WEST);
			jPanelColRange.add(jSpinnerColRange);// , BorderLayout.CENTER);

		}
		return jPanelColRange;
	}

	private JPanel getJPanelRowStart() {

		if (jPanelRowStart == null) {
			jPanelRowStart = new JPanel();
			// jPanelRowStart.setLayout(new FlowLayout());
			jLabelRowStart = new JLabel(
					I18N.getGUILabelText("application.dialog.openPlot.tableWindow.lblRowStart.text"));
			// jPanelRowStart.setPreferredSize(new Dimension(100, 22));
			// jPanelRowStart.setMaximumSize(new Dimension(100, 22));
			// jLabelRowStart.setHorizontalAlignment(JLabel.LEFT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1,
					getRowCount() - 1, 1); // init, min, max, step
			jSpinnerRowStart = new JSpinner(sModel);
			// jSpinnerRowStart.setPreferredSize(new Dimension(100, 22));
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRowStart
					.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf
					.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter
					.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
												// ;
			ftf.setEditable(true);
			jSpinnerRowStart.setValue(1); // only in order to set format pattern
			// jSpinnerRowStart.setValue(1); //only in order to set format
			// pattern
			// jSpinnerRowStart.addChangeListener(this);

			jPanelRowStart.add(jLabelRowStart);// , BorderLayout.WEST);
			jPanelRowStart.add(jSpinnerRowStart);// , BorderLayout.CENTER);
			// jPanelRowStart.setAlignmentY(Component.LEFT_ALIGNMENT);
		}
		return jPanelRowStart;
	}

	private JPanel getJPanelRowEnd() {

		if (jPanelRowEnd == null) {
			jPanelRowEnd = new JPanel();
			// jPanelRowEnd.setLayout(new FlowLayout());
			jLabelRowEnd = new JLabel(
					I18N.getGUILabelText("application.dialog.openPlot.tableWindow.lblRowEnd.text"));
			// jPanelRowEnd.setPreferredSize(new Dimension(70, 22));
			// jLabelRowEnd.setHorizontalAlignment(JLabel.LEFT);
			SpinnerModel sModel = new SpinnerNumberModel(getRowCount(), 1,
					getRowCount(), 1); // init, min, max, step
			jSpinnerRowEnd = new JSpinner(sModel);
			// jSpinnerRowEnd.setPreferredSize(new Dimension(50, 22));
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRowEnd
					.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf
					.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter
					.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
												// ;
			ftf.setEditable(true);
			jSpinnerRowEnd.setValue(getRowCount() - 1); // only in order to set
														// format pattern
			jSpinnerRowEnd.setValue(getRowCount()); // only in order to set
													// format pattern
			// jSpinnerRowEnd.addChangeListener(this);

			jPanelRowEnd.add(jLabelRowEnd);// , BorderLayout.WEST);
			jPanelRowEnd.add(jSpinnerRowEnd);// , BorderLayout.CENTER);
			// jPanelRowStart.setAlignmentY(Component.LEFT_ALIGNMENT);
		}
		return jPanelRowEnd;
	}

	private JButton getJButtonOk() {

		if (buttOk == null) {
			buttOk = new JButton();
			buttOk.setText(I18N
					.getGUILabelText("application.dialog.openPlot.tableWindow.buttOK.text"));
			buttOk.setEnabled(true);
			buttOk.addActionListener(this);
			buttOk.setActionCommand("ok");
			// jPanelCol.setAlignmentY(Component.CENTER_ALIGNMENT);
		}
		return buttOk;
	}

	
	private JButton  getJButtonSelectAll() {
		if (selectAll == null) {	
			selectAll = new JButton();
			selectAll.setText(I18N.getGUILabelText("application.dialog.openPlot.tableWindow.lblSelectAll.text"));
			selectAll.addActionListener(this);
			selectAll.setActionCommand("selectall");		
		}
		return selectAll;
	}
	
	private JButton getJButtonUnSelectAll() {
		if (unSelectAll == null) {	
			unSelectAll = new JButton ();
			unSelectAll.setText(I18N.getGUILabelText("application.dialog.openPlot.tableWindow.lblUnSelectAll.text"));
			unSelectAll.addActionListener(this);
			unSelectAll.setActionCommand("unselectall");		
		}
		return unSelectAll;
	}
	
	
	private int getRowCount() {
		return this.numRows;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (source instanceof AbstractButton == false) {
			return;
		}

		AbstractButton aButton = (AbstractButton) source;
		boolean checked = e.getStateChange() == ItemEvent.SELECTED;
		if (checked) {
			boolean success = columnSelection.add(Integer.valueOf(aButton
					.getName()) - 1);
			logger.debug("Added column " + Integer.valueOf(aButton.getName())
					+ ": " + success + ".");
		} else {
			boolean success = columnSelection.removeElement(Integer
					.valueOf(aButton.getName()) - 1);
			logger.debug("Removed column " + Integer.valueOf(aButton.getName())
					+ ": " + success + ".");
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if ("ok".equals(e.getActionCommand())) {

			// work in background thread
			SwingWorker<Void, Void> loader = new SwingWorker<Void, Void>() {

				Vector<IqmDataBox> vecDataBoxes = new Vector<IqmDataBox>(
						getNumColumns());

				@Override
				protected Void doInBackground() throws Exception {
					try {
						setProgress(0);
						if (columnSelection.size() < 1) {
							DialogUtil
									.getInstance()
									.showDefaultInfoMessage(
											I18N.getMessage("application.dialog.openPlot.noColumnSelected"));
							return null;
						}

						// read values from Inputs:
						int rowHeader = ((Number) jSpinnerRowHeader.getValue())
								.intValue();
						int rowUnits = ((Number) jSpinnerRowUnits.getValue())
								.intValue();
						int colRange = ((Number) jSpinnerColRange.getValue())
								.intValue();
						int rowStart = ((Number) jSpinnerRowStart.getValue())
								.intValue();
						int rowEnd = ((Number) jSpinnerRowEnd.getValue())
								.intValue();

						Vector<Integer> vecRows = new Vector<Integer>();
						Vector<Integer> vecCols = new Vector<Integer>();
						Vector<Integer> vecColDomain = new Vector<Integer>();
						vecRows.add(rowStart);
						vecRows.add(rowEnd);
						vecColDomain.add(colRange - 1);
						vecCols.addAll(columnSelection);

						// use an instance of PlotParser to get data (Double!),
						// Headers
						// and Units:
						dataHeaders = null;
						dataUnits = null;
						PlotParser pP = new PlotParser();
						pP.setDataString(dataString);
						if (rowHeader > 0) {
							pP.setHeaders(rowHeader);
							dataHeaders = pP.extractHeaders(vecCols);
						}
						if (rowUnits > 0) {
							pP.setUnits(rowUnits);
							dataUnits = pP.extractUnits(vecCols);
						}
						dataValues = pP.extractData(vecRows, vecCols);
						numRows = vecRows.size();
						numColumns = vecCols.size();

						// use an instance of PlotParser to get the domain, i.e.
						// x-data:
						String domainHeader = null;
						String domainUnit = null;
						Vector<Double> domain = new Vector<Double>();

						// if a column is selected as the domain:
						if (vecColDomain.get(0) > -1) {
							PlotParser pPDomain = new PlotParser();
							pPDomain.setData(pP.extractData(vecRows,
									vecColDomain));
							domain = pPDomain.extractColumn(0);
							if (rowHeader > 0) {
								pPDomain.setHeaders(pP
										.extractHeaders(vecColDomain));
								domainHeader = pPDomain.extractHeader(0);
							}
							if (rowUnits > 0) {
								pPDomain.setUnits(pP.extractUnits(vecColDomain));
								domainUnit = pPDomain.extractUnit(0);
							}
						}
						// if no range-column is selected: generate line numbers
						// as
						// range
						else {
							for (int i = rowStart; i <= rowEnd; i++) {
								domain.add(Double.valueOf(i));
							}
						}

						// close the frame
						setVisible(false);

						// use an instance of PlotParser to extract just
						// selected data:
						pP = new PlotParser();
						pP.setData(getData());
						pP.setHeaders(getDataHeaders());
						pP.setUnits(getDataUnits());

						Vector<Double> dataTemp;
						PlotModel plotModel = null;

						for (int i = 1; i <= getNumColumns(); i++) {
							setProgress((int) (i / numColumns * 100));
							dataTemp = pP.extractColumn(i - 1);
							plotModel = new PlotModel(domainHeader, domainUnit,
									getDataHeader(i - 1), getDataUnit(i - 1),
									domain, dataTemp);
							
							//set plotModelNames to column headers or if null to file name 
							String columnHeader = getDataHeader(i - 1);		
							//System.out.println("PlotSelectionFrame: columnHeader: " + columnHeader);
							if (columnHeader != null) {
								plotModel.setModelName(columnHeader);
							} else if (file != null) {
								plotModel.setModelName("Filename-" + file.getName());
							}
							
							IqmDataBox box = new IqmDataBox(plotModel);
							vecDataBoxes.add(i - 1, box);
						}

					} catch (ArrayIndexOutOfBoundsException ex) {
						DialogUtil.getInstance().showErrorMessage(
								I18N.getMessage("application.error.generic"),
								ex, true);
						logger.error("An error occurred: ", ex);
					} catch (NumberFormatException ex) {
						DialogUtil.getInstance().showErrorMessage(
								I18N.getMessage("application.error.generic"),
								ex, true);
						logger.error("An error occurred: ", ex);
					}
					return null;
				}

				@Override
				protected void done() {
					if (!vecDataBoxes.isEmpty()) {
						Application.getTank().addNewItems(vecDataBoxes);
						// dispose the frame and run garbage collector
						dispose();
						System.gc();
					}
					setProgress(0);
				}
			};

			// create a waiting dialog/frame
			WaitingDialog dialog = new WaitingDialog(
					"Constructing plot models...", false);

			loader.addPropertyChangeListener(Application.getMainFrame()
					.getStatusPanel());
			loader.addPropertyChangeListener(new CompletionWaiter(dialog));

			// execute the thread
			loader.execute();

			dialog.setVisible(true);
		}//end Ok
		
		if ("selectall".equals(e.getActionCommand())) {
			for (int i = 1; i < jTable.getModel().getColumnCount(); i++) {
				TableColumn tc = jTable.getColumnModel().getColumn(i);	
				((JCheckBox)tc.getHeaderRenderer()).setSelected(true);	
			}
			((JTableHeader)jTable.getTableHeader()).repaint();
		}
		if ("unselectall".equals(e.getActionCommand())) {
			for (int i = 1; i < jTable.getModel().getColumnCount(); i++) {
				TableColumn tc = jTable.getColumnModel().getColumn(i);	
				((JCheckBox)tc.getHeaderRenderer()).setSelected(false);
			}
			((JTableHeader)jTable.getTableHeader()).repaint();
		}
	}
}
