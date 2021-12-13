package at.mug.iqm.api.plot.charts;

/*
 * #%L
 * Project: IQM - API
 * File: VectorPlot.java
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


import java.awt.Color;
import java.awt.image.Raster;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.VectorRenderer;
import org.jfree.data.xy.VectorSeries;
import org.jfree.data.xy.VectorSeriesCollection;
import org.jfree.data.xy.VectorXYDataset;
import org.jfree.chart.ui.RectangleInsets;

public class VectorPlot extends ChartPanel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7883875462950578277L;
	private String plotTitle = "";

	public VectorPlot(Raster r, String plotTitle) {
		super((JFreeChart) null, true);
		this.plotTitle = plotTitle;

		// VectorXYDataset dataset = createDemoDataset();
		VectorXYDataset dataset = createDataset(r);
		this.setChart(createChart(dataset));
		// this.setHorizontalAxisTrace(true);
		// this.setVerticalAxisTrace(true);
		this.setPreferredSize(new java.awt.Dimension(500, 500));
		this.setMouseZoomable(true, false);
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * @return A chart.
	 */
	private JFreeChart createChart(VectorXYDataset dataset) {

		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xAxis.setLowerMargin(0.01);
		xAxis.setUpperMargin(0.01);
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis("Y");
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		yAxis.setLowerMargin(0.01);
		yAxis.setUpperMargin(0.01);
		yAxis.setAutoRangeIncludesZero(false);
		VectorRenderer renderer = new VectorRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
		plot.setOutlinePaint(Color.black);
		//2017-7 Adam Dolgos ->>>>   JFreeCharts scrollable !!!!
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		
		JFreeChart chart = new JFreeChart(plotTitle, plot);
		chart.setBackgroundPaint(Color.white);

		return chart;
	}

	/**
	 * Creates a dataset for the {@link VectorPlot}.
	 * 
	 * @return {@link VectorXYDataset}
	 */
	private VectorXYDataset createDataset(Raster r) {
		VectorSeries s1 = new VectorSeries("Series 1");
		// System.out.println("IqmVectorPlot: r.getWidth: " + r.getWidth()+
		// " r.getHeight(): " + r.getHeight());
		for (int x = 0; x < r.getWidth(); x++) {
			for (int y = 0; y < r.getHeight(); y++) {
				float[] fArray = new float[2];
				r.getPixel(x, r.getHeight() - 1 - y, fArray);
				s1.add(x * 10, y * 10, fArray[0], fArray[1]);
			}
		}
		VectorSeriesCollection dataset = new VectorSeriesCollection();
		dataset.addSeries(s1);
		return dataset;
	}

	/**
	 * Creates a demo dataset for the {@link VectorPlot}.
	 * 
	 * @return {@link VectorXYDataset}
	 */
	private VectorXYDataset createDemoDataset() {
		VectorSeries s1 = new VectorSeries("Series 1");
		for (double rr = 0; rr < 20.0; rr++) {
			for (double cc = 0; cc < 20.0; cc++) {
				s1.add(rr + 10.0, cc + 10.0, Math.sin(rr / 5.0) / 2,
						Math.cos(cc / 5.0) / 2);
			}
		}
		VectorSeriesCollection dataset = new VectorSeriesCollection();
		dataset.addSeries(s1);
		return dataset;
	}

	/**
	 * Creates a panel for the demo (used by SuperDemo.java).
	 * 
	 * @return {@link JPanel}
	 */
	public JPanel createDemoPanel() {
		JFreeChart chart = createChart(createDemoDataset());
		return new ChartPanel(chart);
	}

}
