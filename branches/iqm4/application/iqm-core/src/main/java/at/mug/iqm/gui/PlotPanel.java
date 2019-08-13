package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: PlotPanel.java
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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

import at.mug.iqm.api.gui.IPlotPanel;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.plot.charts.ChartType;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.plot.PlotFactory;
import at.mug.iqm.commons.util.plot.PlotTools;
import at.mug.iqm.commons.util.table.TableTools;
import at.mug.iqm.core.workflow.Plot;


/**
 * This panel holds responsible for displaying plots
 * 
 * @author Michael Mayrhofer
 * @since 2012 10 20 new Plot layout
 * 
 */
/**
 * @author phil
 * 
 */
public class PlotPanel extends JPanel implements IPlotPanel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -3912758686700608387L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(PlotPanel.class);
	/**
	 * The panel, where the chart is displayed.
	 */
	private JPanel chartContent;

	private JTable table = null;
	private DefaultTableModel tableModel = null;

	private JFreeChart chart = null;
	private ChartPanel chartPanel = new ChartPanel((JFreeChart) null);

	private List<PlotModel> plotModels = null;

	/**
	 * A flag whether or not the panel currently displays an item.
	 */
	private boolean isEmpty = true;
	/**
	 * The pane, where the table is displayed.
	 */
	private JScrollPane scrollPaneTable;
	private JPanel panelDebugOptions;

	public PlotPanel() {
		super();
		setBackground(Color.GRAY);

		setBorder(new MatteBorder(5, 1, 1, 1, (Color) Color.GRAY));

		logger.debug("Creating new instance...");

		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(2);
		borderLayout.setHgap(2);
		this.setLayout(borderLayout);

		panelDebugOptions = new JPanel();
//		add(panelDebugOptions, BorderLayout.NORTH);
		panelDebugOptions.setBackground(Color.GREEN);

		JLabel label = new JLabel("DEBUG BUTTONS");
		panelDebugOptions.add(label);

		JButton buttReset = new JButton("Reset");
		buttReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		panelDebugOptions.add(buttReset);

		JButton btnAddPlottable = new JButton("Add Plot/Table");
		btnAddPlottable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Vector<Double> domain = new Vector<Double>(5);
				domain.add(1.0d);
				domain.add(3.0d);
				domain.add(14.0d);
				domain.add(23.0d);
				domain.add(134.d);

				@SuppressWarnings("unchecked")
				Vector<Double> range = (Vector<Double>) domain.clone();

				PlotModel tmpModel = new PlotModel("DomainHeader", "t",
						"DataHeader", "s", domain, range);
				Vector<PlotModel> v = new Vector<PlotModel>(1);
				v.add(tmpModel);
				setPlotModels(v, null);
			}
		});
		panelDebugOptions.add(btnAddPlottable);
		chartContent = new JPanel();
		add(chartContent, BorderLayout.CENTER);
		chartContent.setLayout(new BorderLayout(0, 0));
		chartContent.add(chartPanel);
		chartPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		this.table = new JTable();
		scrollPaneTable = new JScrollPane(this.table);
		scrollPaneTable.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPaneTable.setPreferredSize(new Dimension(170, 2));
		add(scrollPaneTable, BorderLayout.EAST);
		
		JPanel panelOptions = new JPanel();
		panelOptions.setPreferredSize(new Dimension(10, 26));
		panelOptions.setBorder(new EmptyBorder(2, 2, 2, 2));
		panelOptions.setBackground(Color.LIGHT_GRAY);
		add(panelOptions, BorderLayout.SOUTH);
		panelOptions.setLayout(new BorderLayout(5, 5));
		
		JButton button = new JButton("Reset");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Plot.getInstance().reset();
			}
		});
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelOptions.add(button, BorderLayout.EAST);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.getContentPane().add(new PlotPanel());
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

	/**
	 * Get all plot models currently displayed on this panel.
	 * 
	 * @return <code>null</code>, if there are no models available
	 * 
	 * @see at.mug.iqm.api.gui.IPlotPanel#getPlotModels()
	 */
	@Override
	public List<PlotModel> getPlotModels() {
		return plotModels;
	}

	/**
	 * Set the plot models to the panel and display them in a table and a chart.
	 * 
	 * @param plotModels
	 *            the plot models to be displayed
	 * @param type
	 *            the type of chart to be displayed, if <code>null</code> is
	 *            passed {@link ChartType#DEFAULT} will be constructed
	 * @see ChartType
	 */
	@Override
	public void setPlotModels(List<PlotModel> plotModels, ChartType type) {
		if (plotModels == null) {
			return;
		}

		if (type == null) {
			type = ChartType.DEFAULT;
		}

		this.plotModels = plotModels;
		this.setTableData();
		this.createChart(type);

		this.isEmpty = false;

		this.revalidate();
		this.repaint();

		logger.debug("isEmpty? --> " + isEmpty());
	}

	/**
	 * Set the table data from a plot model to the panel.
	 */
	protected void setTableData() {
		// Prepare table
		int numPlotModels = plotModels.size();
		int numColumns = numPlotModels;
		int numRows = 0;
		Vector<?>[] data = new Vector<?>[numPlotModels];
		Vector<?>[] domain = new Vector<?>[numPlotModels];
		String[] dataHeaders = new String[numPlotModels];
		String[] dataUnits = new String[numPlotModels];
		String[] domainHeaders = new String[numPlotModels];
		String[] domainUnits = new String[numPlotModels];

		// Prepare table
		tableModel = new DefaultTableModel();
		table = new JTable(tableModel);

		// adding a lot of columns would be very slow due to active model
		// listener
		tableModel.removeTableModelListener(table);

		for (int c = 0; c < numColumns; c++) { // read content from plotModels
			data[c] = plotModels.get(c).getData();
			domain[c] = plotModels.get(c).getDomain();
			dataHeaders[c] = plotModels.get(c).getDataHeader();
			dataUnits[c] = plotModels.get(c).getDataUnit();
			domainHeaders[c] = plotModels.get(c).getDomainHeader();
			domainUnits[c] = plotModels.get(c).getDomainUnit();
		}

		for (int c = 0; c <= numColumns; c++) { // first column should be the
												// domain interval
			String stringCol = "";
			if (c == 0) {
				if (domainHeaders[0] == null && domainUnits[0] == null) {
					stringCol = "#";
				} else if (domainHeaders[0] != null) {
					stringCol = String.valueOf(domainHeaders[0]);
				} else {
					stringCol = " [" + String.valueOf(domainUnits[0]) + "]";
				}

			} else {
				if (!(dataHeaders[c - 1] == null)
						&& !dataHeaders[c - 1].isEmpty()) {
					stringCol = String.valueOf(dataHeaders[c - 1]);
				}
				if (!(dataUnits[c - 1] == null) && !dataUnits[c - 1].isEmpty()) {
					stringCol = stringCol + " ["
							+ String.valueOf(dataUnits[c - 1]) + "]";
				}
			}

			tableModel.addColumn(stringCol);
		}

		//get maximal number of rows (maximal length of plots)
		numRows = 0;
		for (int c = 0; c < numColumns; c++){
			if (data[c].size() > numRows) numRows = data[c].size();
		}
		
		Vector<String> vectorTemp = new Vector<String>();

		for (int i = 0; i < numRows; i++) {
			vectorTemp.clear();
			
			// if more than one signal is selected: x-axis are ascending integers:
			if(numColumns>1){
				vectorTemp.add(String.valueOf(i+1));
			}
			else{
				vectorTemp.add(String.valueOf(domain[0].get(i))); // x-axis
			}
						
			for (int ii = 0; ii < numColumns; ii++) { // y-axis
				if (i < data[ii].size()){  //some plots are shorter and have no more data points
					vectorTemp.add(String.valueOf(data[ii].get(i)));
				}
				else {
					vectorTemp.add(null);
				}
			}
			tableModel.addRow(new Vector<String>(vectorTemp));
		}
		vectorTemp = null;

		this.scrollPaneTable.setViewportView(this.table);

		tableModel.addTableModelListener(table);
		tableModel.fireTableStructureChanged();
		tableModel.fireTableDataChanged();
		table.setAutoResizeMode(0);
	}

	protected ChartPanel createChart(ChartType chartType) {
		switch (chartType) {
		case IMAGE_HISTOGRAM:
			this.chart = PlotFactory.createHistogramPlot(this.plotModels,
					"Histogram");
			break;

		case XY_LINE_CHART:
			this.chart = PlotFactory.createXYLinePlot(this.plotModels);
			break;
		default:
		case DEFAULT:
			try {
				this.chart = PlotFactory.createDefaultPlot(this.plotModels);
			} catch (Exception e) {
				DialogUtil
						.getInstance()
						.showErrorMessage(
								"The chart cannot be created from the given PlotModels!",
								e, true);
				return null;
			}
			break;
		}

		this.chartPanel = new ChartPanel(this.chart);

		chartContent.removeAll();
		chartContent.add(this.chartPanel);
		return this.chartPanel;
	}

	/**
	 * Sets the chart of the chart panel and - in turn - creates a
	 * {@link PlotModel} which can be processed.
	 * 
	 * @param cp
	 *            the chart panel
	 * @param chartType
	 *            the {@link ChartType}, if <code>null</code> is passed,
	 *            {@link ChartType#DEFAULT} will be constructed
	 */
	@Override
	public void setChart(ChartPanel cp, ChartType chartType) {
		if (chartType == null) {
			chartType = ChartType.DEFAULT;
		}
		// extract the data from the chart panel
		JFreeChart chart = cp.getChart();
		XYDataset dataSet = chart.getXYPlot().getDataset();
		
		chartContent.removeAll();
		
		switch (chartType) {
		case IMAGE_HISTOGRAM:
		case DEFAULT:
		default:
			// convert histogram data to plot model and reconstruct the data
			this.plotModels = PlotTools.toPlotModel(dataSet);

			// set the GUI elements
			this.setTableData();
			this.chart = chart; // take the chart which has been passed
			this.chartPanel = new ChartPanel(this.chart);
			break;
		}

		chartContent.add(this.chartPanel);

		this.isEmpty = false;
	}

	@Override
	public void reset() {
		
		this.plotModels = null;
		// remove the table from view port view
		this.scrollPaneTable.setViewportView(null);

		// remove the chart from the chart content panel
		this.chartContent.removeAll();

		this.table = null;
		this.tableModel = null;
		this.chart = null;
		this.chartPanel = null;
		this.plotModels = null;

		this.isEmpty = true;

		this.revalidate();
		this.repaint();

		logger.debug("is empty? --> " + isEmpty());
	}

	@Override
	public JTable exportTable() {
		if (table == null)
			return null;
		return new JTable(table.getModel());
	}
	
	@Override
	public TableModel getTableModel() {
		return TableTools.convertToTableModel(this.tableModel);
	}

	@Override
	public JFreeChart exportChart() {
		if (chart == null)
			return null;
		try {
			return (JFreeChart) chart.clone();
		} catch (CloneNotSupportedException e) {
			logger.error("The chart cannot be cloned! ", e);
		}
		return null;
	}

	/**
	 * Returns whether or not the panel currently displays an item.
	 * 
	 * @return <code>true</code>, if an item is displayed, <code>false</code>,
	 *         if not
	 */
	@Override
	public boolean isEmpty() {
		return isEmpty;
	}

	public JScrollPane getScrollPaneTable() {
		return scrollPaneTable;
	}
}
