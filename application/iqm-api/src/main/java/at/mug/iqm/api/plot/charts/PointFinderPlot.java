package at.mug.iqm.api.plot.charts;
/*
 * #%L
 * Project: IQM - API
 * File: RegressionPlot.java
 * 
 * $Id: RegressionPlot.java 548 2016-01-18 09:36:47Z kainzp $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-api/src/main/java/at/mug/iqm/api/plot/charts/RegressionPlot.java $
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
import java.awt.geom.Ellipse2D;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * 
 * @author Helmut Ahammer, Philipp Kainz, Adam Dolgos adding plus signs defined in a second vector
 *
 **/

public class PointFinderPlot extends DefaultXYLineChart implements ChangeListener {

	/**
	 * The UID for serialization.
	 */

	private JPanel jPanelSouth = null; // a panel for all components in the south

	private JPanel jPanelRegression = null;
	private JLabel jLabelRegression = null;;
	private JPanel jPanelRegStart = null;
	private JLabel jLabelRegStart = null;
	private JSpinner jSpinnerRegStart = null;
	private JPanel jPanelRegEnd = null;
	private JLabel jLabelRegEnd = null;
	private JSpinner jSpinnerRegEnd = null;

	private int numPoints = 0;
	private int regStart = 0;
	private int regEnd = 0;

	private JPanel jPanelRegResult = null;
	private JLabel jLabelRegResult = null;
	private JLabel jLabelRegResultP0 = null; // y=
	private JLabel jLabelRegResultP1 = null; // x
	private JLabel jLabelRegResultP2 = null; // r2=

	private XYSeriesCollection mSeries = null;

	/**
	 * This constructor creates a single regression plot
	 * 
	 * @param dataX
	 * @param dataY
	 * @param isLineVisible
	 * @param frameTitle
	 * @param imageTitle
	 * @param xTitle
	 * @param yTitle
	 * @param regStart
	 * @param regEnd
	 */
	@SuppressWarnings("rawtypes")
	public PointFinderPlot(Vector dataX, Vector dataY, Vector dataX2, Vector dataY2, boolean isLineVisible,
			String frameTitle, String imageTitle, String xTitle, String yTitle) {
		super(dataX, dataY, dataX2, dataY2, isLineVisible, imageTitle, xTitle, yTitle);
		numPoints = dataX.size();
		//System.out.println("RegressionPlot:  regStart:"+regStart + "   regEnd:"+regEnd);
		this.plotPointFinder();

	}

	/**
	 * This class displays various regression plots.
	 * 
	 * @param dataX
	 * @param dataY
	 * @param isLineVisible
	 * @param frameTitle
	 * @param imageTitle
	 * @param xTitle
	 * @param yTitle
	 * @param regStart
	 * @param regEnd
	 */
	@SuppressWarnings("rawtypes")
	public PointFinderPlot(Vector dataX, Vector[] dataY, Vector dataX2, Vector[] dataY2, boolean isLineVisible,
			String frameTitle, String imageTitle, String xTitle, String yTitle) {
		super(dataX, dataY, dataX2, dataY2, isLineVisible, imageTitle, xTitle, yTitle);
		numPoints = dataX.size();
		this.plotPointFinder();

	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method displays a regression line for each data series
	 */
	private void plotPointFinder() {
		JFreeChart chart = this.getChartPanel().getChart();
		//	XYSeriesCollection sc = (XYSeriesCollection) chart.getXYPlot().getDataset(0);

		// set renderer
		XYLineAndShapeRenderer renderer1 = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer(0);

		renderer1.setBaseShapesVisible(true); //
		renderer1.setBaseShapesFilled(false);
		renderer1.setSeriesPaint(0, Color.BLACK);
		renderer1.setBaseLinesVisible(false);

		chart.getXYPlot().setRenderer(0, renderer1);

		// set renderer
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer(1);
		if (renderer instanceof XYLineAndShapeRenderer) {

			renderer.setBaseShapesVisible(true); //
			renderer.setBaseShapesFilled(true);
			double size = 13;
			double delta = size / 2.0;
			Shape shape = new Ellipse2D.Double(-delta, -delta, size, size);
			renderer.setSeriesPaint(0, Color.RED);
			renderer.setSeriesShape(0, shape);
			renderer.setBaseLinesVisible(false);
	
			chart.getXYPlot().setRenderer(1, renderer);
		}
		/*	int numSeries = sc.getSeriesCount();
			// System.out.println("IqmRegressionPlot: numSeries" + numSeries);
			for (int s = 0; s < numSeries; s++) {
				// System.out.println("IqmRegressionPlot: s" + s);
			//	this.plotPeak(s);
			}*/
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method displays a regression line
	 * 
	 * @param s
	 *            number of data Series
	 */
	@SuppressWarnings("unused")
	private void plotPeak(int s) {
		JFreeChart chart = this.getChartPanel().getChart();
		XYSeriesCollection dataPointSeriesCollection = (XYSeriesCollection) chart.getXYPlot().getDataset(0); // 0 Data Points, 1 Regression lines
		XYSeries series = dataPointSeriesCollection.getSeries(s); // get the data point series at index s
		int numPoints = dataPointSeriesCollection.getItemCount(s); // get the number of data points at index s

		// set renderer
		XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
		renderer1.setSeriesPaint(1, Color.GREEN);
		renderer1.clearSeriesPaints(true);
		renderer1.setBaseShapesVisible(false);
		renderer1.setBaseSeriesVisibleInLegend(false);

		chart.getXYPlot().setRenderer(1, renderer1);

		// set renderer
		XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, false);
		renderer1.setSeriesPaint(0, Color.RED);
		renderer1.clearSeriesPaints(true);
		renderer1.setBaseShapesVisible(false);
		renderer1.setBaseSeriesVisibleInLegend(false);

		chart.getXYPlot().setRenderer(0, renderer2);

	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void stateChanged(ChangeEvent e) {
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);

		int start = ((Number) jSpinnerRegStart.getValue()).intValue();
		int end = ((Number) jSpinnerRegEnd.getValue()).intValue();
		if (jSpinnerRegStart == e.getSource()) {
			if (start >= (end - 1)) {
				jSpinnerRegStart.setValue(end - 2);
			}
		}
		if (jSpinnerRegEnd == e.getSource()) {
			if (end <= (start + 1)) {
				jSpinnerRegEnd.setValue(start + 2);
			}
		}
		jSpinnerRegStart.addChangeListener(this);
		jSpinnerRegEnd.addChangeListener(this);

		// remove all series from the regression data set collection
		this.mSeries.removeAllSeries();

		this.plotPointFinder();
	}

}
