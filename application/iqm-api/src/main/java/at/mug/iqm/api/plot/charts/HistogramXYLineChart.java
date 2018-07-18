package at.mug.iqm.api.plot.charts;

/*
 * #%L
 * Project: IQM - API
 * File: HistogramXYLineChart.java
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


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.AxisEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.JFreeChartEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import at.mug.iqm.commons.util.image.ImageTools;

/**
 * This method is a helper class for displaying a histogram inside an operator
 * JFrame used by e.g. <code>IqmOpThreshold</code> don't change without checking e.g.
 * <code>IqmThreshold</code>!
 * 
 * This class uses JFreeChart: a free chart library for the Java(tm) platform
 * http://www.jfree.org/jfreechart/
 * 
 * @author Helmut Ahammer
 * @since 2009
 * 
 */
public class HistogramXYLineChart extends ChartPanel implements
		MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7448955256858029905L;

	private Color colBand1 = Color.RED;
	private Color colBand2 = Color.GREEN;
	private Color colBand3 = Color.BLUE;
	private ChartPanel chartPanel = null;
	
	private JFreeChart chart = null;
	
	private XYPlot plot = null;
	private Marker mLow = null;
	private Marker mMid = null;
	private Marker mHigh = null;
	public int binLow = 0;
	public int binMid = 0;
	public int binHigh = 0;
	private int histoMax;

	/**
	 * This class displays the JAI histogram
	 * 
	 * @param histo
	 *            JAI histogram
	 * @param type
	 *            the type of the image
	 */
	public HistogramXYLineChart(Histogram histo, String type) {
		super((JFreeChart) null, false);
		XYDataset dataset = createDataset(histo, type);
		histoMax = ((int) histo.getHighValue()[0]) - 1; // 256 -1 = 255
		chart = createChart(dataset);
		chartPanel = new ChartPanel(chart, true);
		chartPanel.setDisplayToolTips(true);
		chartPanel.setMouseZoomable(true);
		// chartPanel.setHorizontalAxisTrace(true);
		// chartPanel.setVerticalAxisTrace(true);
		chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
		chartPanel.addMouseMotionListener(this);
		setLayout(new BorderLayout(0, 0));

		this.add(chartPanel, BorderLayout.CENTER);
	}

	/**
	 * Gets the ChartPanel
	 * 
	 * @return ChartPanel chartPanel
	 */
	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * @return A chart.
	 */
	private JFreeChart createChart(XYDataset dataset) {

		JFreeChart chart = ChartFactory.createXYLineChart(null, // "Histogram",
																// // title
				null, // "Bin", // x-axis label
				null, // "Count", // y-axis label
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

		// plot = (XYPlot) chart.getPlot();
		plot = chart.getXYPlot();
		plot.setBackgroundPaint(null);
		// plot.setDomainGridlinePaint(Color.black); // regular Grid in background
		// plot.setRangeGridlinePaint(Color.black); //
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true); // cross hair after left mouse click
		plot.setRangeCrosshairVisible(true);
		
		//2017-7 Adam Dolgos ->>>>   JFreeCharts scrollable !!!!
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
		}

		// ValueAxis axis = plot.getDomainAxis();
		NumberAxis axis = (NumberAxis) plot.getDomainAxis();
		// show all bins
		// axis.setTickUnit(new NumberTickUnit(1,new DecimalFormat("0"))); //
		// show all units as integer
		axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 

		// set marker
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
	 * Creates a dataset, consisting of the histogram.
	 * 
	 * @return The dataset.
	 */
	private XYDataset createDataset(Histogram histo, String type) {
		XYSeries s1 = null;
		XYSeriesCollection dataset = null;
		if (histo.getNumBands() == 1) { // grey
			if (type == "grey") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("Grey "); // Erscheint bei mouse move samt Daten
			}
			if (type == "red") {
				colBand1 = Color.RED;
				s1 = new XYSeries("R "); // Erscheint bei mouse move samt Daten
			}
			if (type == "green") {
				colBand1 = Color.GREEN;
				s1 = new XYSeries("G "); // Erscheint bei mouse move samt Daten
			}
			if (type == "blue") {
				colBand1 = Color.BLUE;
				s1 = new XYSeries("B "); // Erscheint bei mouse move samt Daten
			}
			if (type == "hue") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("H "); // Erscheint bei mouse move samt Daten
			}
			if (type == "sat") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("S "); // Erscheint bei mouse move samt Daten
			}
			if (type == "value") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("V "); // Erscheint bei mouse move samt Daten
			}
			if (type == "light") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("L "); // Erscheint bei mouse move samt Daten
			}
			if (type == "L*") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("L* "); // Erscheint bei mouse move samt Daten
			}
			if (type == "a*") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("a* "); // Erscheint bei mouse move samt Daten
			}
			if (type == "b*") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("b* "); // Erscheint bei mouse move samt Daten
			}
			if (type == "u*") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("u* "); // Erscheint bei mouse move samt Daten
			}
			if (type == "v*") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("v* "); // Erscheint bei mouse move samt Daten
			}
			if (type == "X") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("X "); // Erscheint bei mouse move samt Daten
			}
			if (type == "Y") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("Y "); // Erscheint bei mouse move samt Daten
			}
			if (type == "Z") {
				colBand1 = Color.BLACK;
				s1 = new XYSeries("Z "); // Erscheint bei mouse move samt Daten
			}

			for (int i = 0; i < histo.getNumBins(0); i++)
				s1.add(i, histo.getBinSize(0, i));
			dataset = new XYSeriesCollection();
			dataset.addSeries(s1);

		}
		return dataset;
	}

	/**
	 * This method sets the marker in the plot
	 * 
	 * @param bin 
	 * @param arg "Low" or "Mid" or "High"
	 */
	public void setMarker(int bin, String arg) {
		if (bin > (histoMax))
			bin = histoMax;
		if (arg.equals("Low")) {
			if (mLow != null)
				plot.removeDomainMarker(mLow);
			mLow = new ValueMarker(bin, Color.BLUE, new BasicStroke(1.2f));
			mLow.setLabel("Low");
			mLow.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			mLow.setLabelTextAnchor(TextAnchor.TOP_LEFT);
			plot.addDomainMarker(mLow);
			binLow = bin;
		}
		if (arg.equals("Mid")) {
			if (mMid != null)
				plot.removeDomainMarker(mMid);
			mMid = new ValueMarker(bin, Color.GREEN, new BasicStroke(1.2f));
			mMid.setLabel("Mid");
			mMid.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			mMid.setLabelTextAnchor(TextAnchor.TOP_LEFT);
			plot.addDomainMarker(mMid);
			binMid = bin;
		}
		if (arg.equals("High")) {
			if (mHigh != null)
				plot.removeDomainMarker(mHigh);
			mHigh = new ValueMarker(bin, Color.RED, new BasicStroke(1.2f));
			mHigh.setLabel("High");
			mHigh.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			mHigh.setLabelTextAnchor(TextAnchor.TOP_LEFT);
			plot.addDomainMarker(mHigh);
			binHigh = bin;
		}

	}

	@SuppressWarnings("unused")
	private void dragMarker(MouseEvent e) {
		chartPanel.setDomainZoomable(false);
		chartPanel.setRangeZoomable(false);

		// MouseEvent tr = e.getTrigger();
		MouseEvent tr = e;
		ChartEntity entity = chartPanel.getEntityForPoint(tr.getX(), tr.getY());

		XYItemEntity xyItemEntity = null; // exactly on the line
		PlotEntity plotEntity = null; // everywhere else in the plot
		JFreeChartEntity jFreeChartEntity = null; // outside of the plot
		AxisEntity axisEntity = null; // on the axis
		if (entity != null) {
			if (entity instanceof XYItemEntity) {
				xyItemEntity = (XYItemEntity) entity;
				int item = ((XYItemEntity) entity).getItem();
				// System.out.println("HistoPanelXYLineChart: mouse move event item: "+
				// item);
			}
			if (entity instanceof PlotEntity) {
				plotEntity = (PlotEntity) entity;
				// System.out.println("HistoPanelXYLineChart: mouse move event plotEntity: "+
				// plotEntity);
				int panelX = tr.getX();
				int panelY = tr.getY();

				// the following translation takes account of the fact that the
				// chart image may have been scaled up or down to fit the
				// panel...
				Point2D panelPoint = chartPanel
						.translateScreenToJava2D(new Point(panelX, panelY));
				// now convert the Java2D coordinate to axis coordinates...
				ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
				Rectangle2D dataArea = info.getPlotInfo().getDataArea();
				XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
				double chartAxisX = plot.getDomainAxis().java2DToValue(
						panelPoint.getX(), dataArea, plot.getDomainAxisEdge());
				double chartAxisY = plot.getRangeAxis().java2DToValue(
						panelPoint.getY(), dataArea, plot.getRangeAxisEdge());
				// System.out.println("HistoPanelXYLineChart: mouse move event chartAxisX: "+
				// chartAxisX);
				// System.out.println("HistoPanelXYLineChart: mouse move event chartAxisY: "+
				// chartAxisY);
				int range = 20;
				if ((chartAxisX - range < binLow)
						&& (binLow < chartAxisX + range)) {
					setMarker((int) chartAxisX, "Low");
				}
				if ((chartAxisX - range < binMid)
						&& (binMid < chartAxisX + range)) {
					setMarker((int) chartAxisX, "Mid");
				}
				if ((chartAxisX - range < binHigh)
						&& (binHigh < chartAxisX + range)) {
					setMarker((int) chartAxisX, "High");
				}
			}
			if (entity instanceof JFreeChartEntity) {
				jFreeChartEntity = (JFreeChartEntity) entity;
				// does nothing
			}
			if (entity instanceof AxisEntity) {
				axisEntity = (AxisEntity) entity;
				// does nothing
			}

		}
		chartPanel.setDomainZoomable(true);
		chartPanel.setRangeZoomable(true);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (e.isControlDown()) { // dragging marker
			dragMarker(e);
			chart.fireChartChanged();
		} else {
			// chart zoomable
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				System.setProperty("com.sun.media.jai.disableMediaLib", "true");
				
				PlanarImage pi = JAI.create("fileload", "C:\\Users\\phil\\pictures\\FluorescentCells_8bit.jpg");
				pi = JAI.create("bandselect", pi, new int[] {0});
				double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);  	
				ParameterBlock pb = new ParameterBlock();  
				pb.addSource(pi);  
				pb.add(null); //Roi
				pb.add(1); //Sampling
				pb.add(1); 
				pb.add(new int[]{(int)typeGreyMax+1}); //Number of bins
				pb.add(new double[]{0}); //Start
				pb.add(new double[]{typeGreyMax+1}); //End
				PlanarImage pi2 = JAI.create("histogram", pb);  
				Histogram histo = (Histogram) pi2.getProperty("histogram");
				
				JFrame frame = new JFrame();
				frame.getContentPane().add(new HistogramXYLineChart(histo, "grey"));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

}
