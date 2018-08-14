package at.mug.iqm.commons.util.plot;

/*
 * #%L
 * Project: IQM - API
 * File: PlotTools.java
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

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.List;
import java.util.Vector;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.IPlotPanel;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.plot.charts.ChartType;
import at.mug.iqm.api.plot.charts.DefaultXYLineChart;
import at.mug.iqm.api.plot.charts.HistogramPlotFrame;
import at.mug.iqm.api.plot.charts.PointFinderPlotFrame;
import at.mug.iqm.api.plot.charts.PlotDisplayFrame;
import at.mug.iqm.api.plot.charts.RegressionPlotFrame;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.LinearRegression;
import at.mug.iqm.commons.util.image.ImageTools;

/**
 * This class provides helper functions for plot processing.
 * 
 * @author Philipp Kainz
 * @update 2017-07 Adam Dolgos- displaying additional plus signs defined in a second vector
 * 
 */
public final class PlotTools {

	private static PointFinderPlotFrame currPeakPlot = null;

	/**
	 * The current <code>IqmRegressionPlot</code>. Default is 'null'.
	 */
	private static RegressionPlotFrame currRegPlot = null;
	/**
	 * The current plot data (X-axis). Default is 'null'.
	 */
	private static Vector<Double> currPlotDataX = null;
	/**
	 * The current plot data (Y-axis). Default is 'null'.
	 */
	private static Vector<Double> currPlotDataY = null;
	/**
	 * The title for the plot to be displayed. Default is 'null'.
	 */
	private static String currPlotTitle = null;
	/**
	 * The title for the X-axis of the plot to be displayed. Default is 'null'.
	 */
	private static String currPlotTitleX = null;
	/**
	 * The title for the Y-axis of the plot to be displayed. Default is 'null'.
	 */
	private static String currPlotTitleY = null;

	/**
	 * This method deletes an exisitng plot
	 */
	public static void deleteExistingPlot() {
		RegressionPlotFrame plCurr = PlotTools.getCurrRegPlot();
		if (plCurr != null) {
			plCurr.setVisible(false);
			plCurr.dispose();
		}
	}

	/**
	 * 
	 * This method converts double array data to vector data and calls the
	 * display routine
	 */
	public static void displayRegressionPlotXY(double[] x, double[] y, boolean isLineVisible, String frameTitle,
			String plotTitle, String xTitle, String yTitle, int regStart, int regEnd, boolean deleteExistingPlot) {

		// Display
		Vector<Double> dataX = new Vector<Double>();
		Vector<Double> dataY = new Vector<Double>();
		for (int i = 0; i < x.length; i++) {
			dataX.add(x[i]);
		}
		for (int i = 0; i < y.length; i++) {
			dataY.add(y[i]);
		}

		PlotTools.displayRegressionPlotXY(dataX, dataY, isLineVisible, frameTitle, plotTitle, xTitle, yTitle, regStart,
				regEnd, deleteExistingPlot);

	}

	/**
	 * This method plots XY Values in an separate window
	 */
	public static void displayRegressionPlotXY(Vector<Double> dataX, Vector<Double> dataY, boolean isLineVisible,
			String frameTitle, String plotTitle, String xTitle, String yTitle, int regStart, int regEnd,
			boolean deleteExistingPlot) {
		if (deleteExistingPlot) {
			PlotTools.deleteExistingPlot();
		}
		// SimpleGraph sg = new SimpleGraph("X v. Y Demo", "x", "y");

		// jFreeChart
		RegressionPlotFrame pl = new RegressionPlotFrame(dataX, dataY, isLineVisible, frameTitle, plotTitle, xTitle,
				yTitle, regStart, regEnd);
		pl.pack();
		// int horizontalPercent = 5;
		// int verticalPercent = 5;
		// RefineryUtilities.positionFrameOnScreen(pl, horizontalPercent,
		// verticalPercent);
		CommonTools.centerFrameOnScreen(pl);
		pl.setVisible(true);
		PlotTools.setCurrRegPlot(pl);

	}

	/**
	 * 2017-07 This method plots XY Values in a separate window by Adam Dolgos
	 * for displaying additional plus signs defined in a second vector
	 */
	public static void displayPointFinderPlotXY(Vector<Double> dataX, Vector<Double> dataY, Vector<Double> dataX2,
			Vector<Double> dataY2, boolean isLineVisible, String frameTitle, String plotTitle, String xTitle,
			String yTitle) {

		// SimpleGraph sg = new SimpleGraph("X v. Y Demo", "x", "y");

		// jFreeChart
		PointFinderPlotFrame pl = new PointFinderPlotFrame(dataX, dataY, dataX2, dataY2, isLineVisible, frameTitle, plotTitle, xTitle, yTitle);
		pl.pack();
		// int horizontalPercent = 5;
		// int verticalPercent = 5;
		// RefineryUtilities.positionFrameOnScreen(pl, horizontalPercent,
		// verticalPercent);
		CommonTools.centerFrameOnScreen(pl);
		pl.setVisible(true);
		PlotTools.setCurrPeakFinderPlot(pl);

	}

	/**
	 * 2017-07 This method plots XY Values in a separate window by Adam Dolgos
	 * for displaying additional plus signs defined in a second vector
	 */
	public static void displayPointFinderPlotXY(double[] x, double[] y, double[] x2, double[] y2, boolean isLineVisible,
			String frameTitle, String plotTitle, String xTitle, String yTitle) {

		// Display
		Vector<Double> dataX = new Vector<Double>();
		Vector<Double> dataY = new Vector<Double>();
		Vector<Double> dataX2 = new Vector<Double>();
		Vector<Double> dataY2 = new Vector<Double>();
		for (int i = 0; i < x.length; i++) {
			dataX.add(x[i]);
		}
		for (int i = 0; i < y.length; i++) {
			dataY.add(y[i]);
		}
		for (int i = 0; i < x2.length; i++) {
			dataX.add(x2[i]);
		}
		for (int i = 0; i < y2.length; i++) {
			dataY.add(y2[i]);
		}

		PlotTools.displayPointFinderPlotXY(dataX, dataY, dataX2, dataY2, isLineVisible, frameTitle, plotTitle, xTitle, yTitle);

	}

	/**
	 * This method plots several XY Values in an separate window
	 */
	public static void displayRegressionPlotXY(Vector<Double> dataX, Vector<Double>[] dataY, boolean isLineVisible,
			String frameTitle, String plotTitle, String xTitle, String yTitle, int regStart, int regEnd,
			boolean deleteExistingPlot) {
		if (deleteExistingPlot) {
			RegressionPlotFrame plCurr = PlotTools.getCurrRegPlot();
			if (plCurr != null) {
				plCurr.setVisible(false);
				plCurr.dispose();
			}

		}
		// SimpleGraph sg = new SimpleGraph("X v. Y Demo", "x", "y");

		// jFreeChart
		RegressionPlotFrame pl = new RegressionPlotFrame(dataX, dataY, isLineVisible, frameTitle, plotTitle, xTitle,
				yTitle, regStart, regEnd);
		pl.pack();
		// int horizontalPercent = 5;
		// int verticalPercent = 5;
		// RefineryUtilities.positionFrameOnScreen(pl, horizontalPercent,
		// verticalPercent);
		CommonTools.centerFrameOnScreen(pl);
		pl.setVisible(true);
		PlotTools.setCurrRegPlot(pl);

	}

	/**
	 * this method calculates the linear Regression parameters Y = p[0] + p[1].X
	 */
	public static double[] getLinearRegression(Vector<Double> dataX, Vector<Double> dataY, int regStart, int regEnd) {

		LinearRegression lr = new LinearRegression();

		double[] dataXArray = new double[dataX.size()];
		double[] dataYArray = new double[dataY.size()];
		for (int i = 0; i < dataX.size(); i++) {
			dataXArray[i] = dataX.get(i).doubleValue();
		}
		for (int i = 0; i < dataY.size(); i++) {
			dataYArray[i] = dataY.get(i).doubleValue();
		}

		double p[] = lr.calculateParameters(dataXArray, dataYArray, regStart, regEnd);
		// double p[] = lr.calculateParameters(IqmTools.reverse(dataX),
		// IqmTools.reverse(dataY), regStart, regEnd);
		return p;
	}

	/**
	 * This method calculates the Maximum Likelihood Estimation instead of a
	 * linear regression. according to Clauset A., Shalizi C.R., Newman M.E.J.,
	 * Power-Law Distributions in Empirical Data, SIAM Review, 2009 does not
	 * work properly until now!!!!!!!!!!
	 */
	public static double[] getMaxLikeliHoodEstimate(Vector<Double> dataX, Vector<Double> dataY, int regStart,
			int regEnd) {

		// maximum likelihood estimator MLE according to Clauset A. SIAM 2009
		Vector<Double> dataYNorm = new Vector<Double>();
		dataYNorm = dataY;
		int nMin = regStart - 1;
		int nMax = regEnd - 1;
		int N = nMax - nMin;
		double dataXMin = dataX.get(nMin); // not so easy to get this value
											// automatically!!

		// normalize so that area under the curve is 1
		double area = 0.0;
		for (int nn = nMin; nn <= nMax; nn++) {
			area = area + dataY.get(nn);
		}
		for (int nn = nMin; nn <= nMax; nn++) {
			dataYNorm.set(nn, dataY.get(nn) / area);
		}

		double mle = 0.0;
		for (int nn = nMin; nn <= nMax; nn++) {
			// mle = mle + Math.log(dataY.get(nn)/(dataYMin -0.5));
			mle = mle + Math.log(dataX.get(nn) / (dataXMin));
		}
		mle = 1.0 / mle * N + 1.0;

		double[] p = new double[6]; // p0, p1, StDev0 StDev1, R2, adR2
		// p[0] no relevance
		p[1] = mle;
		// p[2] = no relevance
		p[3] = 0.0; // not yet implemented
		// p[4] = no relevance
		// p[5] = no relevance

		return p;
	}

	/**
	 * This method plots XY Values in an separate window without the x data
	 */
	public static void displayPlotXY(Vector<Double> dataY, boolean isLineVisible, String plotTitle, String xTitle,
			String yTitle) {
		int length = dataY.size();
		Vector<Double> dataX = new Vector<Double>();
		for (int i = 0; i < length; i++) {
			dataX.add((double) (i + 1));
		}
		PlotTools.displayPlotXY(dataX, dataY, isLineVisible, plotTitle, xTitle, yTitle);
	}

	/**
	 * This method plots XY Values in an separate window
	 */
	public static void displayPlotXY(Vector<Double> dataX, Vector<Double> dataY, boolean isLineVisible,
			String plotTitle, String xTitle, String yTitle) {

		// SimpleGraph sg = new SimpleGraph("X v. Y Demo", "x", "y");
		// int idx = CommonTools.getCurrManagerImgIndex();
		// //PlanarImage pi =
		// Tank.getCurrentTankIqmDataBoxAt(idx).getPlanarImage();
		// IqmDataBox iqmDataBox = Tank.getCurrentTankIqmDataBoxAt(idx);
		// PlanarImage pi = IqmDataBox.iqmDataBoxToPlanarImage(iqmDataBox);
		// if (pi == null){
		// System.out.println("IqmGUI_Convert: Current image not defined");
		// return;
		// }
		// if (pi == null){
		// Board.appendTexln("Look: PlanarImage == null");
		// return;
		// }

		String frameTitle = " ";
		// jFreeChart
		PlotDisplayFrame pl = new PlotDisplayFrame(dataX, dataY, isLineVisible, frameTitle, plotTitle, xTitle, yTitle);
		pl.pack();
		// int horizontalPercent = 5;
		// int verticalPercent = 5;
		// RefineryUtilities.positionFrameOnScreen(pl, horizontalPercent,
		// verticalPercent);
		CommonTools.centerFrameOnScreen(pl);
		pl.setVisible(true);
	}

	/**
	 * This method sets the current plot data
	 * 
	 * @param dataX
	 * @param dataY
	 * @param plotTitle
	 * @param titleX
	 * @param titleY
	 */
	public static void setCurrPlotXYData(Vector<Double> dataX, Vector<Double> dataY, String plotTitle, String titleX,
			String titleY) {
		currPlotDataX = dataX;
		currPlotDataY = dataY;
		currPlotTitle = plotTitle;
		currPlotTitleX = titleX;
		currPlotTitleY = titleY;
	}

	/**
	 * This method gets current plot data
	 * 
	 * @return Vector
	 */
	public static Vector<Double> getCurrPlotDataX() {
		return currPlotDataX;
	}

	/**
	 * This method gets current plot data
	 * 
	 * @return Vector
	 */
	public static Vector<Double> getCurrPlotDataY() {
		return currPlotDataY;
	}

	/**
	 * This method gets current plot data
	 * 
	 * @return String
	 */
	public static String getCurrPlotTitle() {
		return currPlotTitle;
	}

	/**
	 * This method gets current plot data
	 * 
	 * @return String
	 */
	public static String getCurrPlotTitleX() {
		return currPlotTitleX;
	}

	/**
	 * This method gets current plot data
	 * 
	 * @return String
	 */
	public static String getCurrPlotTitleY() {
		return currPlotTitleY;
	}

	/**
	 * This method plots the histogram in a new {@link HistogramPlotFrame} and
	 * returns the reference to the {@link Histogram} object.
	 * 
	 * @param pi
	 *            the image to create the histogram from
	 * @return a reference to the histogram
	 */
	public static Histogram showHistogram(PlanarImage pi) {

		String frameTitle = "Histogram ";
		try {
			frameTitle += ": " + String.valueOf(pi.getProperty("file_name"));
		} catch (Exception ignored) {
		}

		String histoTitle = String.valueOf(pi.getProperty("image_name"));

		Histogram h = getHistogram(pi);
		HistogramPlotFrame hpFrame = new HistogramPlotFrame(h, frameTitle, histoTitle);
		hpFrame.pack();
		CommonTools.centerFrameOnScreen(hpFrame);
		hpFrame.setVisible(true);
		return h;
	}

	/**
	 * Creates an {@link XYDataset}, consisting of the histogram.
	 * 
	 * @return The data set.
	 */
	public static XYDataset createHistogramDataset(Histogram histo) {
		XYSeries s1 = null;
		XYSeries s2 = null;
		XYSeries s3 = null;
		XYSeriesCollection dataset = null;

		if (histo.getNumBands() == 1) { // grey
			// Color colBand1 = Color.black;
			s1 = new XYSeries("Grey "); // displayed at mouse moved event
			// double[] dataX = new double[histo.getNumBins(0)]; //in order to
			// set current plot to IqmTools
			// double[] dataY = new double[histo.getNumBins(0)];
			// Vector<Double> domain = new Vector<Double>();
			// Vector<Double> range = new Vector<Double>();
			for (int i = 0; i < histo.getNumBins(0); i++) {
				// int value = histo.getBinSize(0, i);
				double value = (double) histo.getBinSize(0, i);
				s1.add(i, value);
				// domain.add((double) (i + 1));
				// range.add(value);
			}
			// String plotTitle = "Histogram";
			// String xTitle = "Bin";
			// String yTitle = "#";
			// PlotTools // TODO
			// .setCurrPlotXYData(dataX, dataY, plotTitle, xTitle, yTitle);
			dataset = new XYSeriesCollection();
			dataset.addSeries(s1);
		}
		if (histo.getNumBands() == 3) { // RGB
			s1 = new XYSeries("R "); // displayed at mouse moved event
			s2 = new XYSeries("G "); // displayed at mouse moved event
			s3 = new XYSeries("B "); // displayed at mouse moved event
			// double[] dataX = new double[histo.getNumBins(0)]; //in order to
			// set current plot to IqmTools
			// double[] dataY = new double[histo.getNumBins(0)];
			// Vector<Double> domain = new Vector<Double>();
			// Vector<Double> range = new Vector<Double>();
			for (int i = 0; i < histo.getNumBins(0); i++) {
				// int value = histo.getBinSize(0, i);
				double value = (double) histo.getBinSize(0, i);
				s1.add(i, value);
				// domain.add((double) (i + 1));
				// range.add(value);
			}
			for (int i = 0; i < histo.getNumBins(1); i++)
				s2.add(i, histo.getBinSize(1, i));
			for (int i = 0; i < histo.getNumBins(2); i++)
				s3.add(i, histo.getBinSize(2, i));
			// String plotTitle = "Histogram";
			// String xTitle = "Bin";
			// String yTitle = "#Red";
			// PlotTools // TODO
			// .setCurrPlotXYData(dataX, dataY, plotTitle, xTitle, yTitle);
			dataset = new XYSeriesCollection();
			dataset.addSeries(s1);
			dataset.addSeries(s2);
			dataset.addSeries(s3);
		}
		return dataset;
	}

	/**
	 * Creates an {@link XYDataset}, consisting of the histogram.
	 * 
	 * @return The data set.
	 */
	public static XYDataset createHistogramDataset(List<PlotModel> plotModels) {
		XYSeries s1 = null;
		XYSeries s2 = null;
		XYSeries s3 = null;
		XYSeriesCollection dataset = null;

		if (plotModels.size() == 1) { // grey
			PlotModel pm = plotModels.get(0);
			s1 = new XYSeries("Grey ");
			Vector<Double> domain = new Vector<Double>();
			Vector<Double> range = new Vector<Double>();
			for (int i = 0; i < pm.getDomain().size(); i++) {
				double value = (double) pm.getData().get(i);
				s1.add(i, value);
				domain.add((double) (i + 1));
				range.add(value);
			}
			dataset = new XYSeriesCollection();
			dataset.addSeries(s1);
		}
		if (plotModels.size() == 3) { // RGB

			PlotModel pm0 = plotModels.get(0);
			PlotModel pm1 = plotModels.get(1);
			PlotModel pm2 = plotModels.get(2);

			s1 = new XYSeries("R ");
			s2 = new XYSeries("G ");
			s3 = new XYSeries("B ");
			Vector<Double> domain = new Vector<Double>();
			Vector<Double> range = new Vector<Double>();
			for (int i = 0; i < pm0.getDomain().size(); i++) {
				double value = (double) pm0.getData().get(i);
				s1.add(i, value);
				domain.add((double) (i));
				range.add(value);
			}
			for (int i = 0; i < pm1.getDomain().size(); i++)
				s2.add(i, pm1.getData().get(i));
			for (int i = 0; i < pm2.getDomain().size(); i++)
				s3.add(i, pm2.getData().get(i));

			dataset = new XYSeriesCollection();
			dataset.addSeries(s1);
			dataset.addSeries(s2);
			dataset.addSeries(s3);
		}
		return dataset;
	}

	/**
	 * This method creates the histogram of a given image and returns the
	 * reference to the {@link Histogram} object.
	 * 
	 * @param image
	 *            the image to create the histogram from
	 * @return a {@link Histogram}
	 * 
	 * @see #getHistogram(PlanarImage)
	 */
	public static Histogram getHistogram(RenderedImage image) {
		return getHistogram(PlanarImage.wrapRenderedImage(image));
	}

	/**
	 * This method creates the histogram of a given image and returns the
	 * reference to the {@link Histogram} object.
	 * 
	 * @param pi
	 *            the image to create the histogram from
	 * @return a {@link Histogram}
	 */
	public static Histogram getHistogram(PlanarImage pi) {
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(null); // Roi
		pb.add(1); // Sampling
		pb.add(1);
		pb.add(new int[] { (int) typeGreyMax + 1 }); // Number of bins
		pb.add(new double[] { 0d }); // Start
		pb.add(new double[] { typeGreyMax + 1 }); // End
		RenderedOp rendOp = JAI.create("histogram", pb);

		return (Histogram) rendOp.getProperty("histogram");
	}

	private static void setCurrRegPlot(RegressionPlotFrame pl) {
		currRegPlot = pl;
	}

	private static RegressionPlotFrame getCurrRegPlot() {
		return currRegPlot;
	}

	private static void setCurrPeakFinderPlot(PointFinderPlotFrame pl) {
		currPeakPlot = pl;
	}

	private static PointFinderPlotFrame getCurrPeakFinderPlot() {
		return currPeakPlot;
	}

	// /**
	// * This method creates a PlanarImage from an IqmPlot
	// *
	// * @param PlotDisplayFrame
	// * plot
	// * @return PlanarImage
	// * @deprecated Use {@link #toPlanarImage(PlotModel)} instead.
	// */
	// public static PlanarImage toPlanarImage(PlotDisplayFrame plot) {
	// if (plot == null)
	// return null;
	//
	// PlanarImage pi = null;
	// ChartRenderingInfo info = new ChartRenderingInfo(
	// new StandardEntityCollection());
	// BufferedImage bi = plot.getJFreeChart().createBufferedImage(150, 100,
	// BufferedImage.TYPE_3BYTE_BGR, info);
	//
	// pi = PlanarImage.wrapRenderedImage(bi);
	// pi.setProperty("image_name", plot.getJFreeChart().getTitle().toString());
	// pi.setProperty("file_name", plot.getJFreeChart().getTitle().toString());
	// return pi;
	// }

	/**
	 * This method creates a {@link PlanarImage} from a {@link List} of
	 * {@link PlotModel}s. This list of <code>PlotModel</code>s are separate
	 * data series in a {@link JFreeChart}.
	 * <p>
	 * The standard dimension for the generated image are <code>150x100</code>
	 * px.
	 * 
	 * @param plotModels
	 *            the list of 1...n plot models
	 * @return an image of the data rows (plot models), or <code>null</code> if
	 *         the parameter is <code>null</code>, or the list is empty
	 * @throws Exception
	 *             if any error occurs in the conversion
	 */
	public static PlanarImage toPlanarImage(List<PlotModel> plotModels) throws Exception {
		if (plotModels == null || plotModels.isEmpty())
			return null;

		PlanarImage pi = null;
		ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());

		BufferedImage bi = PlotFactory.createDefaultPlot(plotModels).createBufferedImage(150, 100,
				BufferedImage.TYPE_3BYTE_BGR, info);

		pi = PlanarImage.wrapRenderedImage(bi);

		return pi;
	}

	/**
	 * This method creates a {@link PlanarImage} from a single {@link PlotModel}
	 * .
	 * <p>
	 * The standard dimension for the generated image are <code>150x100</code>
	 * px.
	 * 
	 * @param plotModel
	 *            the single {@link PlotModel}
	 * @param showAxes
	 *            a flag whether or not the axes should be displayed in the
	 *            image of the plot model
	 * @return an image of the plot model, or <code>null</code> if the parameter
	 *         is <code>null</code>
	 * @throws Exception
	 *             if any error occurs in the conversion
	 */
	public static PlanarImage toPlanarImage(PlotModel plotModel, boolean showAxes) throws Exception {
		if (plotModel == null)
			return null;

		PlanarImage pi = null;
		ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());

		Vector<PlotModel> v = new Vector<PlotModel>(1);
		v.add(0, plotModel);

		BufferedImage bi = PlotFactory.createThumbNailPlot(v, showAxes).createBufferedImage(150, 100,
				BufferedImage.TYPE_3BYTE_BGR, info);

		pi = PlanarImage.wrapRenderedImage(bi);

		return pi;
	}

	/**
	 * Project a given image to a selected direction of the Cartesian coordinate
	 * system.
	 * 
	 * @param pi
	 * @param direction
	 *            must not be <code>null</code>, must, either be 0 (x-axis) or 1
	 *            (y-axis), see also {@link IQMConstants}
	 */
	public static void projectImage(final PlanarImage pi, int direction) {
		if (pi == null)
			return;
		switch (direction) {
		case IQMConstants.DIRECTION_X:
		case IQMConstants.DIRECTION_Y:
			break;
		default:
			return;
		}

		SwingWorker<Void, Void> task = null;

		if (direction == IQMConstants.DIRECTION_X) {
			task = new SwingWorker<Void, Void>() {

				private Vector<Double> dataX;
				private Vector<Double> dataY;
				private boolean isLineVisible;
				private String plotTitle;
				private String xTitle;
				private String yTitle;

				@Override
				protected Void doInBackground() throws Exception {
					Raster raster = pi.getData();
					int width = pi.getWidth();
					int height = pi.getHeight();
					dataX = new Vector<Double>(width);
					dataY = new Vector<Double>(width);
					int[] iArray = new int[3];
					for (int x = 0; x < width; x++) {
						dataX.add((double) (x + 1));
						dataY.add(0.0d);
						for (int y = 0; y < height; y++) {
							raster.getPixel(x, y, iArray);
							// System.out.println("x: "+x);
							// System.out.println("dataY.size(): "+dataY.size());
							dataY.set(x, dataY.get(x).doubleValue() + iArray[0]);
							// dataY.setElementAt(((Double)dataY.get(x)).doubleValue()
							// + (double)iArray[0], x);
						}
					}
					isLineVisible = true;
					plotTitle = I18N.getGUILabelText("projection.X.title");
					xTitle = "pixel";
					yTitle = "#";

					DefaultXYLineChart chartPanel = new DefaultXYLineChart(dataX, dataY, isLineVisible, plotTitle,
							xTitle, yTitle);

					Application.getPlot().setChartData(chartPanel.getChartPanel(), ChartType.DEFAULT);
					return null;
				}

				@Override
				protected void done() {
					super.done();
					// PlotTools.displayPlotXY(dataX, dataY, isLineVisible,
					// plotTitle, xTitle, yTitle);
					// TODO
					// PlotTools.setCurrPlotXYData(dataX, dataY, plotTitle,
					// xTitle, yTitle);

					Application.getMainFrame().setSelectedTabIndex(1);
				}

			};

		}

		else if (direction == IQMConstants.DIRECTION_Y) {
			task = new SwingWorker<Void, Void>() {

				private Vector<Double> dataX;
				private Vector<Double> dataY;
				private boolean isLineVisible;
				private String plotTitle;
				private String xTitle;
				private String yTitle;

				@Override
				protected Void doInBackground() throws Exception {
					Raster raster = pi.getData();
					int width = pi.getWidth();
					int height = pi.getHeight();
					dataX = new Vector<Double>(height);
					dataY = new Vector<Double>(height);
					int[] iArray = new int[3];
					for (int x = 0; x < height; x++) {
						dataX.add((double) (x + 1));
						dataY.add(0.0d);
						for (int y = 0; y < width; y++) {
							raster.getPixel(y, x, iArray);
							dataY.set(x, dataY.get(x).doubleValue() + iArray[0]);
						}
					}
					isLineVisible = true;
					plotTitle = I18N.getGUILabelText("projection.Y.title");
					xTitle = "pixel";
					yTitle = "#";

					DefaultXYLineChart chartPanel = new DefaultXYLineChart(dataX, dataY, isLineVisible, plotTitle,
							xTitle, yTitle);

					Application.getPlot().setChartData(chartPanel.getChartPanel(), ChartType.DEFAULT);
					return null;
				}

				@Override
				protected void done() {
					super.done();
					// PlotTools.displayPlotXY(dataX, dataY, isLineVisible,
					// plotTitle, xTitle, yTitle);

					// TODO what for??
					// PlotTools.setCurrPlotXYData(dataX, dataY, plotTitle,
					// xTitle, yTitle);

					Application.getMainFrame().setSelectedTabIndex(1);
				}
			};

		}

		// EXECUTE THE TASK
		try {
			task.execute();
		} catch (Exception ignored) {
		}
	}

	/**
	 * Creates a list of {@link PlotModel}s out of an {@link XYDataset}.
	 * <p>
	 * The series count of the <code>XYDataSet</code> determines the number of
	 * list elements, such as the list may just contain a single element.
	 * 
	 * @param dataSet
	 *            the data set
	 * @return a {@link List} of {@link PlotModel}s of the data set that is
	 *         displayable in the {@link IPlotPanel}
	 * @throws IllegalArgumentException
	 *             if the data set is <code>null</code> or does not contain any
	 *             data series
	 */
	public static List<PlotModel> toPlotModel(XYDataset dataSet) {
		if (dataSet == null || dataSet.getSeriesCount() < 1) {
			throw new IllegalArgumentException("The passed XYDataSet is null or does not contain "
					+ "any series to be converted to List<PlotModel>!");
		}

		List<PlotModel> modelList = new Vector<PlotModel>(dataSet.getSeriesCount());

		int nSeries = dataSet.getSeriesCount();

		for (int series = 0; series < nSeries; series++) {
			int nItems = dataSet.getItemCount(series);

			// for each series one plot model is constructed
			PlotModel pm = new PlotModel();
			Vector<Double> domain = new Vector<Double>(nItems);
			Vector<Double> range = new Vector<Double>(nItems);

			for (int item = 0; item < nItems; item++) {
				// from each series, extract X (domain) and Y (range)
				double item_x = dataSet.getXValue(series, item);
				double item_y = dataSet.getYValue(series, item);

				// add them to the vector
				domain.add(item, item_x);
				range.add(item, item_y);
			}

			// set the data to the temporary PlotModel
			pm.setData(range);
			if (dataSet.getSeriesKey(series) instanceof String) {
				pm.setDataHeader((String) dataSet.getSeriesKey(series));
			} else {
				pm.setDataHeader(null);
			}
			pm.setDataUnit("N/A");

			pm.setDomain(domain);
			pm.setDomainUnit("N/A");

			// finally add the model to the entire list
			modelList.add(pm);
		}
		return modelList;
	}

	/**
	 * Creates a list of {@link PlotModel}s out of a {@link DefaultTableModel}.
	 * 
	 * @param tableModel
	 *            the {@link DefaultTableModel}
	 * @param direction
	 *            an integer indicating whether the rows (vertical) or columns
	 *            (horizontal) contain the values of a single signal, see also
	 *            {@link IQMConstants}
	 * @return a plot model of the data set that is displayable in the
	 *         {@link IPlotPanel}
	 * 
	 * @throws IllegalArgumentException
	 *             if the table model is <code>null</code> or does not contain
	 *             any elements
	 */
	public static List<PlotModel> toPlotModels(DefaultTableModel tableModel, int direction) {
		if (tableModel == null || direction < 0) {
			throw new IllegalArgumentException("The passed table model is null, cannot convert to List<PlotModel>!");
		}

		// TODO implement just converting a selected range of columns/rows

		// construct the result container
		List<PlotModel> modelList = null;

		if (direction == IQMConstants.DIRECTION_HORIZONTAL) {
			int nSignals = tableModel.getRowCount();
			int sigLen = tableModel.getColumnCount();
			modelList = new Vector<PlotModel>(nSignals);

			// get each row data
			for (int row = 0; row < nSignals; row++) {
				Vector<Double> domainVector = new Vector<Double>(sigLen);
				Vector<Double> dataVector = new Vector<Double>(sigLen);
				// get each row as data vector
				for (int col = 0; col < sigLen; col++) {
					double element = (double) tableModel.getValueAt(row, col);
					dataVector.add(col, element);
					domainVector.add(col, (double) col);
				}

				PlotModel pm = new PlotModel();
				pm.setModelName("Row " + row);
				pm.setDomain(domainVector);
				pm.setData(dataVector);

				modelList.add(pm);
			}

		} else if (direction == IQMConstants.DIRECTION_VERTICAL) {
			int nSignals = tableModel.getColumnCount();
			int sigLen = tableModel.getRowCount();
			modelList = new Vector<PlotModel>(nSignals);

			// get each column data
			for (int col = 0; col < nSignals; col++) {
				Vector<Double> domainVector = new Vector<Double>(sigLen);
				Vector<Double> dataVector = new Vector<Double>(sigLen);
				// get each column as data vector
				for (int row = 0; row < sigLen; row++) {
					double element = (double) tableModel.getValueAt(row, col);
					dataVector.add(row, element);
					domainVector.add(row, (double) row);
				}

				PlotModel pm = new PlotModel();
				pm.setModelName("Column " + col);
				pm.setDomain(domainVector);
				pm.setData(dataVector);

				modelList.add(pm);
			}
		}
		return modelList;
	}

	/**
	 * Extracts the intensity profile of a given row or column index.
	 * 
	 * @param r
	 * @param direction
	 * @param index
	 * @return the list of plot models (one for each color band)
	 */
	public static List<PlotModel> getIntensityProfile(Raster r, int direction, int index) {
		if (r == null) {
			throw new IllegalArgumentException("The raster must not be null!");
		}

		// get number of bands
		int nBands = r.getNumBands();

		// result object
		List<PlotModel> modelList = new Vector<PlotModel>(nBands);

		// perform range checks
		switch (direction) {
		case IQMConstants.DIRECTION_HORIZONTAL:
			if (index < 0 || index >= r.getWidth()) {
				throw new ArrayIndexOutOfBoundsException(
						"The index must be within the " + "horizontal bounds of the image: >0 and <" + r.getWidth());
			}

			for (int band = 0; band < nBands; band++) {
				Vector<Double> domainVector = new Vector<Double>(r.getWidth());
				Vector<Double> dataVector = new Vector<Double>(r.getWidth());

				PlotModel pm = new PlotModel();

				for (int col = 0; col < r.getWidth(); col++) {
					domainVector.add(col, (double) col);
					dataVector.add(col, r.getSampleDouble(col, index, band));
				}

				pm.setModelName("Row Intensity Profile Band [" + band + "]");
				pm.setDomain(domainVector);
				pm.setData(dataVector);

				modelList.add(pm);
			}

			break;
		case IQMConstants.DIRECTION_VERTICAL:
			if (index < 0 || index >= r.getHeight()) {
				throw new ArrayIndexOutOfBoundsException(
						"The index must be within the " + "vertical bounds of the image: >0 and <" + r.getHeight());
			}

			for (int band = 0; band < nBands; band++) {
				Vector<Double> domainVector = new Vector<Double>(r.getHeight());
				Vector<Double> dataVector = new Vector<Double>(r.getHeight());

				PlotModel pm = new PlotModel();

				for (int row = 0; row < r.getHeight(); row++) {
					domainVector.add(row, (double) row);
					dataVector.add(row, r.getSampleDouble(index, row, band));
				}

				pm.setModelName("Column Intensity Profile Band [" + band + "]");
				pm.setDomain(domainVector);
				pm.setData(dataVector);

				modelList.add(pm);
			}

			break;
		default:
			throw new IllegalArgumentException("The direction must either be " + "0 (horizontal), or 1 (vertical)!");
		}

		return modelList;
	}
}
