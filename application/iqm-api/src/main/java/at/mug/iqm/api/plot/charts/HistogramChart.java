package at.mug.iqm.api.plot.charts;

/*
 * #%L
 * Project: IQM - API
 * File: HistogramChart.java
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


import java.awt.Dimension;
import java.awt.image.RenderedImage;

import javax.media.jai.Histogram;
import javax.media.jai.PlanarImage;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

import at.mug.iqm.commons.util.plot.PlotFactory;
import at.mug.iqm.commons.util.plot.PlotTools;

/**
 * This chart displays a histogram of one or more bands.
 * <p>
 * It uses the {@link PlotTools} class for constructing the data set and
 * {@link PlotFactory} for constructing the chart.
 * 
 * @author Philipp Kainz
 * 
 * 
 * @see PlotTools
 */
public class HistogramChart extends ChartPanel {

	/**
	 * The UID for serialization
	 */
	private static final long serialVersionUID = -4142413862069296474L;

	/**
	 * Create the chart.
	 * 
	 * @param histo
	 *            the JAI histogram
	 * @param histoTitle
	 *            the title of the histogram
	 */
	public HistogramChart(Histogram histo, String histoTitle) {
		super((JFreeChart) null, false);
		XYDataset dataset = PlotTools.createHistogramDataset(histo);
		// this.setHorizontalAxisTrace(true); 
		// this.setVerticalAxisTrace(true);
		this.setChart(PlotFactory.createHistogramPlot(dataset, histoTitle));

		this.setPreferredSize(new Dimension(600, 400));
		this.setMouseZoomable(true, false);
	}

	/**
	 * Create the chart.
	 * 
	 * @param pi
	 *            the image to create a histogram from 
	 * @param histoTitle
	 *            the title of the histogram
	 */
	public HistogramChart(PlanarImage pi, String histoTitle) {
		super((JFreeChart) null, false);
		// this.setHorizontalAxisTrace(true); 
		// this.setVerticalAxisTrace(true);
		this.setChart(PlotFactory.createHistogramPlot(
				(RenderedImage) PlotTools.getHistogram(pi), histoTitle));

		this.setPreferredSize(new Dimension(600, 400));
		this.setMouseZoomable(true, false);
	}
}
