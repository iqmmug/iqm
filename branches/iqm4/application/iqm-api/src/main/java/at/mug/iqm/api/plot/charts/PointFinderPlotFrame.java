package at.mug.iqm.api.plot.charts;

/*
 * #%L
 * Project: IQM - API
 * File: PointFinderPlotFrame.java
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


import java.util.Vector;

/**
 * This method shows XY Data in an extra window. This class uses JFreeChart: a
 * free chart library for the Java(tm) platform http://www.jfree.org/jfreechart/
 * 
 * @author Helmut Ahammer, Philipp Kainz, Adam Dolgos 2017-07 displaying additional plus signs defined in a second vector
 */

public class PointFinderPlotFrame extends PlotDisplayFrame {

	public PointFinderPlotFrame(Vector dataX, Vector dataY, Vector dataX2, Vector dataY2, boolean isLineVisible, String frameTitle, String imageTitle,
			String xLabel, String yLabel) {
		super(frameTitle);

		PointFinderPlot rp = new PointFinderPlot(dataX, dataY, dataX2, dataY2, isLineVisible, frameTitle, imageTitle, xLabel, yLabel);

		this.setContentPane(rp);

		this.pack();
	}

	public PointFinderPlotFrame(Vector dataX, Vector[] dataY, Vector dataX2, Vector[] dataY2, boolean isLineVisible, String frameTitle,
			String imageTitle, String xLabel, String yLabel) {
		super(frameTitle);

		PointFinderPlot rp = new PointFinderPlot(dataX, dataY, dataX2, dataY2, isLineVisible, frameTitle, imageTitle, xLabel, yLabel);

		this.setContentPane(rp);

		this.pack();
	}

}
