package at.mug.iqm.commons.util.plot;

/*
 * #%L
 * Project: IQM - API
 * File: PlotFactory.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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


import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.util.List;
import java.util.Vector;

import javax.media.jai.Histogram;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.plot.charts.ChartType;
import at.mug.iqm.api.plot.charts.HistogramChart;

/**
 * This factory creates {@link JFreeChart} plots from a given data set, plot
 * model or image.
 * <p>
 * This factory is extended by all supported plot types, which are enumerated in
 * {@link ChartType}.
 * 
 * @author Philipp Kainz
 * 
 */
public final class PlotFactory {

	/**
	 * This method creates a default X-Y-{@link JFreeChart} from a single
	 * {@link PlotModel}.
	 * <p>
	 * This is merely a convenient wrapper method for
	 * {@link #createPlot(List, ChartType)}.
	 * 
	 * @param plotModel
	 *            the plot model
	 * @return a {@link JFreeChart} chart of the plot model, or
	 *         <code>null</code>, if the parameter is <code>null</code>
	 * 
	 * @throws Exception
	 *             if any error occurs in the conversion
	 * 
	 * @see #createPlot(List, ChartType)
	 */
	public static JFreeChart createDefaultPlot(PlotModel plotModel)
			throws Exception {
		if (plotModel == null)
			return null;

		Vector<PlotModel> mdlz = new Vector<PlotModel>(1);
		mdlz.add(0, plotModel);

		return createPlot(mdlz, ChartType.DEFAULT);
	}

	/**
	 * This method creates a default X-Y-{@link JFreeChart} from a {@link List}
	 * of {@link PlotModel}s.
	 * <p>
	 * This is merely a convenient wrapper method for
	 * {@link #createPlot(List, ChartType)}.
	 * 
	 * @param plotModels
	 *            the plot models
	 * @param withAxes
	 *            a flag whether or not the axes should be shown in the plot
	 * @return a {@link JFreeChart} chart of the plot model, or
	 *         <code>null</code>, if the parameter is <code>null</code> or the
	 *         list is empty
	 * 
	 * @throws Exception
	 *             if any error occurs in the conversion
	 * 
	 * @see #createPlot(List, ChartType)
	 */
	public static JFreeChart createThumbNailPlot(List<PlotModel> plotModels,
			boolean withAxes) throws Exception {
		if (plotModels == null || plotModels.isEmpty())
			return null;

		JFreeChart chart = createDefaultScatterXYPlot(plotModels);
		
		if (!withAxes){
			XYPlot plot = (XYPlot) chart.getPlot();
			plot.getRangeAxis().setVisible(false);
			plot.getDomainAxis().setVisible(false);
		}
		
		
		return chart;
	}

	/**
	 * This method creates a default X-Y-{@link JFreeChart} from a {@link List}
	 * of {@link PlotModel}s.
	 * <p>
	 * This is merely a convenient wrapper method for
	 * {@link #createPlot(List, ChartType)}.
	 * 
	 * @param plotModels
	 *            the plot models
	 * @return a {@link JFreeChart} chart of the plot model, or
	 *         <code>null</code>, if the parameter is <code>null</code> or the
	 *         list is empty
	 * 
	 * @throws Exception
	 *             if any error occurs in the conversion
	 * 
	 * @see #createPlot(List, ChartType)
	 */
	public static JFreeChart createDefaultPlot(List<PlotModel> plotModels)
			throws Exception {
		if (plotModels == null || plotModels.isEmpty())
			return null;

		return createPlot(plotModels, ChartType.DEFAULT);
	}

	/**
	 * This method creates a {@link JFreeChart} from a {@link List} of
	 * {@link PlotModel}s.
	 * <p>
	 * This is merely a convenient wrapper method for
	 * {@link #createXYLinePlot(List)}.
	 * 
	 * @param plotModels
	 *            the plot models
	 * @param type
	 *            the {@link ChartType} of the chart, may be <code>null</code>,
	 *            but then, a {@link ChartType#DEFAULT} type is constructed
	 * @return a {@link JFreeChart} chart of the plot model, or
	 *         <code>null</code>, if the parameter is <code>null</code> or the
	 *         list is empty
	 * 
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code> or does not contain any
	 *             elements
	 * 
	 * @see #createXYLinePlot(List)
	 */
	public static JFreeChart createPlot(List<PlotModel> plotModels,
			ChartType type) throws Exception {
		if (plotModels == null || plotModels.isEmpty())
			throw new IllegalArgumentException(
					"The arguments are null or does not contain any elements!");

		JFreeChart chart = null;
		switch (type) {
		case XY_LINE_CHART:
			chart = createXYLinePlot(plotModels);
			break;
		case DEFAULT:
		default:
			chart = createDefaultScatterXYPlot(plotModels);
			break;
		}

		return chart;
	}

	/**
	 * This method creates a {@link JFreeChart} from a {@link PlotModel}.
	 * 
	 * @param plotModels
	 *            the plot models
	 * @return a {@link JFreeChart} chart of the plot model, or
	 *         <code>null</code> if the parameter is <code>null</code>
	 * 
	 * @throws Exception
	 *             if any error occurs in the conversion
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code> or does not contain any
	 *             elements
	 */
	public static JFreeChart createXYLinePlot(List<PlotModel> plotModels)
			throws IllegalArgumentException {

		if (plotModels == null || plotModels.size() == 0)
			throw new IllegalArgumentException(
					"The argument is null or does not contain any elements!");

		int numPlotModels = plotModels.size();

		// a collection of all XY series
		XYSeriesCollection dataset = new XYSeriesCollection();

		// build the data set
		for (int i = 0; i < numPlotModels; i++) {
			XYSeries series = new XYSeries("Series: " + (i + 1));
			PlotModel pm = plotModels.get(i);

			// construct the series for the plot model
			for (int j = 0; j < pm.getData().size(); j++) {
				series.add(pm.getDomain().get(j), pm.getData().get(j));
			}
			dataset.addSeries(series);
		}

		String yLabel = "";
		String xLabel = ""; // PK 2013 02 24: removed automatic labeling of
							// x-axis, since
		// it can be set interactively in the plot gui object of JFreeChart
		PlotModel pm = plotModels.get(0);
		String dataHeader = pm.getDataHeader();
		String dataUnit = pm.getDataUnit();
		String domainHeader = pm.getDomainHeader();
		String domainUnit = pm.getDomainUnit();

		if (numPlotModels == 1) {
			if (dataHeader != null && dataUnit != null) {
				yLabel = String.valueOf(dataHeader) + " ["
						+ String.valueOf(dataUnit) + "]";
			} else if (dataHeader != null) {
				yLabel = String.valueOf(dataHeader);
			}
		}

		if (domainHeader != null && domainUnit != null) {
			xLabel = String.valueOf(domainHeader) + " ["
					+ String.valueOf(domainUnit) + "]";
		} else if (domainHeader != null) {
			xLabel = String.valueOf(domainHeader);
		}

		JFreeChart chart = ChartFactory.createXYLineChart("", // "", // title
				xLabel, // x-axis label
				yLabel, // y-axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // generate legends
				true, // generate tooltips
				false // generate URLs
				);

		chart.setBackgroundPaint(Color.WHITE);
		// legend to the right of the chart
		// LegendTitle legend = (LegendTitle) chart.getSubtitle( 0 );
		// legend.setPosition( RectangleEdge.RIGHT );
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
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(false); //
			renderer.setBaseShapesFilled(true);
			// Shape[] shapes = DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE;
			// //0 square, 1 circle, 2 triangle; 3 diamond; .......9
			// renderer.setSeriesShape(0, shapes[2]);
			// Shape shape = new Rectangle2D.Double(-1, -1, 2, 2); //small
			// rectangle
			// renderer.setSeriesShape(0, shape);
			renderer.setBaseLinesVisible(true);
			// renderer.setSeriesOutlinePaint(0, Color.black);
			// renderer.setUseOutlinePaint(true);
		}

		return chart;
	}

	/**
	 * This method creates a {@link JFreeChart} from a {@link PlotModel}.
	 * 
	 * @param plotModels
	 *            the plot models
	 * @return a {@link JFreeChart} chart of the plot model, or
	 *         <code>null</code> if the parameter is <code>null</code>
	 * 
	 * @throws Exception
	 *             if any error occurs in the conversion
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code> or does not contain any
	 *             elements
	 */
	public static JFreeChart createDefaultScatterXYPlot(List<PlotModel> plotModels)
			throws IllegalArgumentException {

		if (plotModels == null || plotModels.size() == 0)
			throw new IllegalArgumentException(
					"The argument is null or does not contain any elements!");

		int numPlotModels = plotModels.size();

		// a collection of all XY series
		XYSeriesCollection dataset = new XYSeriesCollection();

		// build the data set
		for (int i = 0; i < numPlotModels; i++) {
			XYSeries series = new XYSeries("Series: " + (i + 1));
			PlotModel pm = plotModels.get(i);

			// construct the series for the plot model
			for (int j = 0; j < pm.getData().size(); j++) {
				series.add(pm.getDomain().get(j), pm.getData().get(j));
			}
			dataset.addSeries(series);
		}

		String yLabel = "";
		String xLabel = ""; // PK 2013 02 24: removed automatic labeling of
							// x-axis, since
		// it can be set interactively in the plot gui object of JFreeChart
		PlotModel pm = plotModels.get(0);
		String dataHeader = pm.getDataHeader();
		String dataUnit = pm.getDataUnit();
		String domainHeader = pm.getDomainHeader();
		String domainUnit = pm.getDomainUnit();

		if (numPlotModels == 1) {
			if (dataHeader != null && dataUnit != null) {
				yLabel = String.valueOf(dataHeader) + " ["
						+ String.valueOf(dataUnit) + "]";
			} else if (dataHeader != null) {
				yLabel = String.valueOf(dataHeader);
			}
		}

		if (domainHeader != null && domainUnit != null) {
			xLabel = String.valueOf(domainHeader) + " ["
					+ String.valueOf(domainUnit) + "]";
		} else if (domainHeader != null) {
			xLabel = String.valueOf(domainHeader);
		}

		// CREATE THE CHART
		JFreeChart chart = ChartFactory.createXYLineChart("", // "", // title
				xLabel, // x-axis label
				yLabel, // y-axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // generate legends
				true, // generate tooltips
				false // generate URLs
				);

		chart.setBackgroundPaint(Color.WHITE);
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

		if (numPlotModels == 1) {
			plot.getRenderer().setSeriesPaint(0, Color.black);
		}

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(false);
			// Shape[] shapes = DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE;
			// //0 square, 1 circle, 2 triangle; 3 diamond; .......9
			// renderer.setSeriesShape(0, shapes[2]);
			// Shape shape = new Rectangle2D.Double(-1, -1, 2, 2); //small
			// rectangle
			// renderer.setSeriesShape(0, shape);
			renderer.setBaseLinesVisible(false);
			// renderer.setSeriesOutlinePaint(0, Color.black);
			// renderer.setUseOutlinePaint(true);
		}

		return chart;
	}

	/**
	 * Creates a histogram chart from a list of {@link PlotModel}s.
	 * 
	 * @param plotModels
	 *            the list of {@link PlotModel}s
	 * @param histoTitle
	 *            the title of the histogram
	 * @return A chart.
	 */
	public static JFreeChart createHistogramPlot(List<PlotModel> plotModels,
			String histoTitle) {

		XYDataset dataSet = PlotTools.createHistogramDataset(plotModels);

		return createHistogramPlot(dataSet, histoTitle);
	}

	/**
	 * Creates a histogram chart from a {@link Histogram}.
	 * 
	 * @param histo
	 *            the histogram
	 * @param histoTitle
	 *            the title of the histogram
	 * @return A chart.
	 */
	public static HistogramChart createHistogramPlot(Histogram histo,
			String histoTitle) {

		return new HistogramChart(histo, histoTitle);
	}

	/**
	 * Creates a histogram chart from a {@link RenderedImage}.
	 * 
	 * @param image
	 *            the image
	 * @param histoTitle
	 *            the title of the histogram
	 * @return A chart.
	 */
	public static JFreeChart createHistogramPlot(RenderedImage image,
			String histoTitle) {

		return createHistogramPlot(
				PlotTools.createHistogramDataset(PlotTools.getHistogram(image)),
				"Histogram");
	}

	/**
	 * Creates a histogram chart from a data set.
	 * 
	 * @param dataset
	 *            a dataset.
	 * @return A chart.
	 */
	public static JFreeChart createHistogramPlot(XYDataset dataset,
			String histoTitle) {
		Color colBand1 = Color.RED;
		if (dataset.getSeriesCount() == 1) {
			colBand1 = Color.BLACK;
		}

		Color colBand2 = Color.GREEN;
		Color colBand3 = Color.BLUE;

		JFreeChart chart = ChartFactory.createXYLineChart(histoTitle, // "Histogram",
																		// //
																		// title
				"Bin", // x-axis label
				"Count", // y-axis label
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
	
		//Adam Dolgos ->>>>   JFreeCharts scrollable !!!!
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		
		// plot.setPadding(0.0, 0.0, 0.0, 15.0);
		plot.getRenderer().setSeriesPaint(0, colBand1);
		plot.getRenderer().setSeriesPaint(1, colBand2);
		plot.getRenderer().setSeriesPaint(2, colBand3);
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(false); //
			renderer.setBaseShapesFilled(false);
			// Shape[] shapes = DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE;
			// //0 square, 1 circle, 2 triangle; 3 diamond; .......9
			// renderer.setSeriesShape(0, shapes[2]);
			Shape shape = new Rectangle2D.Double(-1, -1, 2, 2); // small
																// rectangle
			renderer.setSeriesShape(0, shape);
		}

		// ValueAxis axis = plot.getDomainAxis();
		NumberAxis axis = (NumberAxis) plot.getDomainAxis();
		// axis.setTickUnit(new NumberTickUnit(1,new DecimalFormat("0")));
		axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

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

}
