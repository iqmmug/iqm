package at.mug.iqm.api.plot.charts;

/*
 * #%L
 * Project: IQM - API
 * File: PlotDisplayFrame.java
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import at.mug.iqm.api.Resources;
import at.mug.iqm.commons.util.CommonTools;

/**
 * This method shows XY Data in an extra window. This class uses JFreeChart: a
 * free chart library for the Java(tm) platform http://www.jfree.org/jfreechart/
 * 
 * @author Helmut Ahammer, Philipp Kainz
 */
public class PlotDisplayFrame extends JFrame {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -3898916287018191384L;

	/**
	 * The default constructor.
	 */
	public PlotDisplayFrame() {
		this.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.application.magenta.32x32")).getImage());

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(false);
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(0); // ms
		ttm.setReshowDelay(10000);
		ttm.setDismissDelay(5000);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeAndDestroyFrame();
			}
		});
	}

	/**
	 * Constructor to be used, if a title should be directly set to the
	 * {@link JFrame}.
	 * 
	 * @param frameTitle
	 */
	public PlotDisplayFrame(String frameTitle) {
		this();
		this.setTitle(frameTitle);
	}

	/**
	 * This constructor creates an instance that displays a plot containing a
	 * single data series.
	 */
	@SuppressWarnings("rawtypes")
	public PlotDisplayFrame(Vector dataX, Vector dataY, boolean isLineVisible,
			String frameTitle, String imageTitle, String xLabel, String yLabel) {
		this(frameTitle);

		DefaultXYLineChart chartPanel = new DefaultXYLineChart(dataX, dataY,
				isLineVisible, imageTitle, xLabel, yLabel);

		this.getContentPane().add(chartPanel, BorderLayout.CENTER);
		this.pack();
	}

	/**
	 * This constructor creates an instance that displays multiple data series
	 * of the same length in a single plot.
	 */
	@SuppressWarnings("rawtypes")
	public PlotDisplayFrame(Vector dataX, Vector[] dataY,
			boolean isLineVisible, String frameTitle, String imageTitle,
			String xLabel, String yLabel) {
		this(frameTitle);

		DefaultXYLineChart chartPanel = new DefaultXYLineChart(dataX, dataY,
				isLineVisible, imageTitle, xLabel, yLabel);

		this.getContentPane().add(chartPanel, BorderLayout.CENTER);
		this.pack();
	}

	/**
	 * This method disposes the frame. It also resets some ToolTip parameters.
	 */
	private void closeAndDestroyFrame() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(CommonTools.getDefaultToolTipInitDelay()); // ms
		ttm.setReshowDelay(CommonTools.getDefaultToolTipReshowDelay());
		ttm.setDismissDelay(CommonTools.getDefaultToolTipDismissDelay());
		this.setVisible(false);
		this.dispose();
	}
}
