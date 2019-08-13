package at.mug.iqm.api.plot.charts;

/*
 * #%L
 * Project: IQM - API
 * File: DefaultXYLineChart.java
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
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.DateRange;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

/**
 * This class is a GUI element for a default 2D line chart.
 * 
 * @author Philipp Kainz
 * @update 2017-07- Adam Dolgos added second Vector for additional plus signs
 *
 */
public class DefaultXYLineChart extends JPanel implements ChangeListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7541928072908981166L;

	private Color colSeries1 = Color.RED;
	private Color colSeries2 = Color.BLUE;
	private Color colSeries3 = Color.GREEN;

	private boolean isLineVisible = false;
	private ChartPanel chartPanel = null;

	private String imageTitle = null;
	private String xLabel = null;
	private String yLabel = null;

	private static int SLIDER_INITIAL_VALUE = 50;
	private JSlider slider;
	private DateAxis domainAxis;
	private int lastValue = SLIDER_INITIAL_VALUE;

	// one month (milliseconds, seconds, minutes, hours, days)
	private int delta = 1000 * 60 * 60 * 24 * 30;

	@SuppressWarnings("rawtypes")
	public DefaultXYLineChart(Vector dataX, Vector dataY, boolean isLineVisible, String imageTitle, String xLabel, String yLabel) {

		this.isLineVisible = isLineVisible;
		this.imageTitle = imageTitle;
		this.xLabel = xLabel;
		this.yLabel = yLabel;

		this.chartPanel = new ChartPanel((JFreeChart) null, true);

		XYDataset dataset = this.createDataset(dataX, dataY, "Series 1");
		this.chartPanel.setChart(createChart(dataset));
		// this.setHorizontalAxisTrace(true);
		// this.setVerticalAxisTrace(true);
		this.chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
		this.chartPanel.setMouseZoomable(true, false);

		this.setLayout(new BorderLayout());
		this.add(this.chartPanel, BorderLayout.CENTER);

	}

	/**
	 * This class displays multiple data series in a single plot window.
	 * 
	 * @param dataX
	 * @param dataY
	 * @param isLineVisible
	 * @param imageTitle
	 * @param xLabel
	 * @param yLabel
	 */
	@SuppressWarnings("rawtypes")
	public DefaultXYLineChart(Vector dataX, Vector[] dataY, boolean isLineVisible, String imageTitle, String xLabel,
			String yLabel) {
		this.chartPanel = new ChartPanel((JFreeChart) null, true);

		XYDataset dataset = this.createDataset(dataX, dataY);
		this.chartPanel.setChart(createChart(dataset));
		// this.setHorizontalAxisTrace(true);
		// this.setVerticalAxisTrace(true);
		this.chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
		this.chartPanel.setMouseZoomable(true, false);

		this.setLayout(new BorderLayout());
		this.add(this.chartPanel, BorderLayout.CENTER);
	}

	/**
	 * 2017-07- Adam Dolgos added second Vector for additional signs for points
	 * 
	 * @param dataX
	 * @param dataY
	 * @param dataX2
	 * @param dataY2
	 * @param isLineVisible
	 * @param imageTitle
	 * @param xLabel
	 * @param yLabel
	 */
	@SuppressWarnings("rawtypes")
	public DefaultXYLineChart(Vector dataX, Vector dataY, Vector dataX2, Vector dataY2, boolean isLineVisible,
			String imageTitle, String xLabel, String yLabel) {

		this.isLineVisible = isLineVisible;
		this.imageTitle = imageTitle;
		this.xLabel = xLabel;
		this.yLabel = yLabel;

		this.chartPanel = new ChartPanel((JFreeChart) null, true);

		XYDataset dataset = this.createDataset(dataX, dataY, "Data");
		//	this.chartPanel.setChart(createChart(dataset));
		//	this.chartPanel.setChart(createChart2(0, dataset));

		XYDataset dataset2 = this.createDataset(dataX2, dataY2, "Points");
		this.chartPanel.setChart(createChart(dataset, dataset2));

		// this.setHorizontalAxisTrace(true);
		// this.setVerticalAxisTrace(true);
		this.chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
		this.chartPanel.setMouseZoomable(true, false);

		this.setLayout(new BorderLayout());
		this.add(this.chartPanel, BorderLayout.CENTER);

	}

	@SuppressWarnings("rawtypes")
	public DefaultXYLineChart(Vector dataX, Vector[] dataY, Vector dataX2, Vector[] dataY2, boolean isLineVisible,
			String imageTitle, String xLabel, String yLabel) {

		this.isLineVisible = isLineVisible;
		this.imageTitle = imageTitle;
		this.xLabel = xLabel;
		this.yLabel = yLabel;

		this.chartPanel = new ChartPanel((JFreeChart) null, true);

		XYDataset dataset = this.createDataset(dataX, dataY);
		//	this.chartPanel.setChart(createChart(dataset));
		//	this.chartPanel.setChart(createChart2(0, dataset));

		XYDataset dataset2 = this.createDataset(dataX2, dataY2);

		this.chartPanel.setChart(createChart(dataset, dataset2));

		// this.setHorizontalAxisTrace(true);
		// this.setVerticalAxisTrace(true);
		this.chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
		this.chartPanel.setMouseZoomable(true, false);

		this.setLayout(new BorderLayout());
		this.add(this.chartPanel, BorderLayout.CENTER);

	}

	private JFreeChart createChart(XYDataset dataset, XYDataset dataset2) {

		JFreeChart chart = ChartFactory.createXYLineChart(imageTitle, // "", //
				// title
				xLabel, // x-axis label
				yLabel, // y-axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation?
				true, // generate legends?
				true, // generate tooltips?
				false // generate URLs?
		);

		chart.setBackgroundPaint(Color.WHITE);

		// legend to the right of the chart
		LegendTitle legend = (LegendTitle) chart.getSubtitle(0);
		legend.setPosition(RectangleEdge.RIGHT);

		XYPlot plot = (XYPlot) chart.getPlot();

		plot.setDataset(1, dataset2);
		plot.setRenderer(1, new XYLineAndShapeRenderer());

		plot.setBackgroundPaint(null);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.black);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		
		//2017-7 Adam Dolgos ->>>>   JFreeCharts scrollable !!!!
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		
		// plot.setPadding(0.0, 0.0, 0.0, 15.0);

		XYItemRenderer r = plot.getRenderer(0);
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true); //
			renderer.setBaseShapesFilled(false);
			renderer.setBaseLinesVisible(isLineVisible);
		}

		XYItemRenderer r2 = plot.getRenderer(1);
		if (r2 instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r2;
			renderer.setBaseShapesVisible(true); //
			renderer.setBaseShapesFilled(true);
			renderer.setBaseLinesVisible(isLineVisible);
		}

		return chart;

	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset a dataset.
	 * @return A chart.
	 */
	private JFreeChart createChart(XYDataset dataset) {

		JFreeChart chart = ChartFactory.createXYLineChart(imageTitle, // "", //
				// title
				xLabel, // x-axis label
				yLabel, // y-axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation?
				true, // generate legends?
				true, // generate tooltips?
				false // generate URLs?
		);

		chart.setBackgroundPaint(Color.WHITE);
		// legend to the right of the chart
		LegendTitle legend = (LegendTitle) chart.getSubtitle(0);
		legend.setPosition(RectangleEdge.RIGHT);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(null);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.black);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		//2017-7 Adam Dolgos ->>>>   JFreeCharts scrollable !!!!
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		
		// plot.setPadding(0.0, 0.0, 0.0, 15.0);
		plot.getRenderer().setSeriesPaint(0, colSeries1);
		plot.getRenderer().setSeriesPaint(1, colSeries2);
		plot.getRenderer().setSeriesPaint(2, colSeries3);
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true); //
			renderer.setBaseShapesFilled(false);
			// Shape[] shapes = DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE;
			// //0 square, 1 circle, 2 triangle; 3 diamond; .......9
			// renderer.setSeriesShape(0, shapes[2]);
			// Shape shape = new Rectangle2D.Double(-1, -1, 2, 2); //small
			// rectangle
			// renderer.setSeriesShape(0, shape);
			renderer.setBaseLinesVisible(isLineVisible);
			// renderer.setSeriesOutlinePaint(0, Color.black);
			// renderer.setUseOutlinePaint(true);
		}

		// NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		// domainAxis.setAutoRangeIncludesZero(false);
		// domainAxis.setTickMarkInsideLength(2.0f);
		// domainAxis.setTickMarkOutsideLength(0.0f);
		//
		// NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		// rangeAxis.setTickMarkInsideLength(2.0f);
		// rangeAxis.setTickMarkOutsideLength(0.0f);

		// ValueAxis axis = plot.getDomainAxis();
		// @SuppressWarnings("unused")
		// NumberAxis axis = (NumberAxis) plot.getDomainAxis();
		// axis.setTickUnit(new NumberTickUnit(1,new DecimalFormat("0"))); //
		// show every bin
		// axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); //
		// show integer ticker units

		// marker setzen
		// final Marker vmLow = new ValueMarker(10);
		// vmLow.setPaint(Color.BLUE);
		// vmLow.setLabel("Low");
		// vmLow.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		// vmLow.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		// plot.addDomainMarker(vmLow);
		//
		// final Marker vmHigh = new ValueMarker(200);
		// vmHigh.setPaint(Color.RED);
		// vmHigh.setLabel("High");
		// vmHigh.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		// vmHigh.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		// plot.addDomainMarker(vmHigh);

		return chart;
	}

	/**
	 * Creates a dataset, consisting of one vector data.
	 * 
	 * @return the dataset
	 */
	@SuppressWarnings("rawtypes")
	private XYDataset createDataset(Vector dataX, Vector dataY, String seriesName) {

		XYSeries s = null;
		XYSeriesCollection dataset = null;

		colSeries1 = Color.black;
		s = new XYSeries(seriesName);
		// s = new XYSeries("");
		// for (int i = 0; i < dataX.length; i++) s.add(dataX[i], dataY[i]);
		for (int i = 0; i < dataX.size(); i++)
			s.add(((Double) dataX.get(i)).doubleValue(), ((Double) dataY.get(i)).doubleValue());
		dataset = new XYSeriesCollection();
		dataset.addSeries(s);
		// dataset.addSeries(s2);
		return dataset;

	}

	/**
	 * Creates a dataset, consisting of multiple series.
	 * 
	 * @return the dataset
	 */
	@SuppressWarnings({ "rawtypes" })
	private XYDataset createDataset(Vector dataX, Vector[] dataY) {

		XYSeries[] s = new XYSeries[dataY.length];
		XYSeriesCollection dataset = null;

		colSeries1 = Color.black;
		for (int v = 0; v < dataY.length; v++)
			s[v] = new XYSeries("Series " + (v + 1)); // several data series
		// s = new XYSeries("");
		dataset = new XYSeriesCollection();
		for (int v = 0; v < dataY.length; v++) {
			for (int i = 0; i < dataX.size(); i++)
				s[v].add(((Double) dataX.get(i)).doubleValue(), ((Double) dataY[v].get(i)).doubleValue());
			dataset.addSeries(s[v]);
		}
		// dataset.addSeries(s2);
		return dataset;
	}

	/**
	 * Creates the chart panel (container).
	 * 
	 * @return A panel.
	 */
	@SuppressWarnings("rawtypes")
	public JPanel createPanel(Vector dataX, Vector dataY) {
		JFreeChart chart = createChart(createDataset(dataX, dataY, "Series 1"));
		ChartPanel chartPanel = new ChartPanel(chart);
		// chartPanel.setVerticalAxisTrace(true);
		// chartPanel.setHorizontalAxisTrace(true);
		// popup menu conflicts with axis trace
		chartPanel.setPopupMenu(null);
		chartPanel.setDomainZoomable(true);
		chartPanel.setRangeZoomable(true);

		return chartPanel;

	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public void setChartPanel(ChartPanel chartPanel) {
		this.chartPanel = chartPanel;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		int value = this.slider.getValue();
		long minimum = domainAxis.getMinimumDate().getTime();
		long maximum = domainAxis.getMaximumDate().getTime();
		if (value < lastValue) { // left
			minimum = minimum - delta;
			maximum = maximum - delta;
		} else { // right
			minimum = minimum + delta;
			maximum = maximum + delta;
		}
		DateRange range = new DateRange(minimum, maximum);
		domainAxis.setRange(range);
		lastValue = value;
	}

}
