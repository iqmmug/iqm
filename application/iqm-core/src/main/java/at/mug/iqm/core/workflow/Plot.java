package at.mug.iqm.core.workflow;

/*
 * #%L
 * Project: IQM - Application Core
 * File: Plot.java
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


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartPanel;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.IPlotPanel;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.plot.charts.ChartType;
import at.mug.iqm.api.workflow.IPlot;
import at.mug.iqm.core.I18N;
import at.mug.iqm.gui.PlotPanel;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class is a accessor for the current {@link PlotPanel}.
 * 
 * @author Michael Mayrhofer-R.
 * @since 2012 10 21
 */
public final class Plot implements IPlot {

	// class specific logger
	private final Logger logger = LogManager.getLogger(Plot.class);

	/**
	 * The current {@link PlotPanel} instance to control.
	 */
	private IPlotPanel plotPanel = null;

	private Plot() {
		Application.setPlot(this);
	}

	public static IPlot getInstance() {
		IPlot plot = Application.getPlot();
		if (plot == null) {
			plot = new Plot();
		}
		return plot;
	}

	/**
	 * Gets the current {@link PlotPanel} instance.
	 * 
	 * @return a reference to the current instance
	 */
	@Override
	public IPlotPanel getPlotPanel() {
		return plotPanel;
	}

	/**
	 * Sets the {@link PlotPanel} instance to control.
	 * 
	 * @param arg
	 */
	@Override
	public void setPlotPanel(IPlotPanel arg) {
		plotPanel = arg;
	}

	/**
	 * Sets new data to the {@link PlotPanel}. The meta information of the first
	 * {@link PlotModel} in the array is taken as the description of the graph
	 * (x,y-axes, domain and range units, etc.). All plot models are computed in
	 * a single XY line chart on the fly.
	 * 
	 * @param plotModels
	 *            can be 1...n plot models
	 * @param type
	 *            the type of the chart to be displayed
	 * 
	 * @see #setNewData(PlotModel, ChartType) for setting a single model to the
	 *      view
	 */
	@Override
	public void setNewData(List<PlotModel> plotModels, ChartType type) {
		logger.debug("Setting new data...");

		plotPanel.setPlotModels(plotModels, type);

		if (plotModels.size() > 1){
			updateMainFrameTitle(true);
		}else {
			updateMainFrameTitle(false);
		}
	}

	/**
	 * Sets new data to the {@link PlotPanel}.
	 * 
	 * @param plotModel
	 *            one single plot model
	 * @param type
	 *            the type of the chart to be displayed
	 * 
	 * @see #setNewData(List, ChartType) for setting multiple models to the view
	 */
	@Override
	public void setNewData(PlotModel plotModel, ChartType type) {
		logger.debug("Setting new data...");

		ArrayList<PlotModel> wrapper = new ArrayList<PlotModel>(1);
		wrapper.add(0, plotModel);

		plotPanel.setPlotModels(wrapper, type);

		updateMainFrameTitle(false);
	}

	/**
	 * Sets new data to the {@link PlotPanel}.
	 * 
	 * @param chartPanel
	 *            an already constructed {@link ChartPanel}, e.g. a histogram
	 * @param type
	 *            the type of the chart to be displayed
	 * 
	 * @see #setNewData(List, ChartType) for setting multiple models to the view
	 */
	@Override
	public void setChartData(ChartPanel chartPanel, ChartType type) {
		logger.debug("Setting new data...");

		plotPanel.setChart(chartPanel, type);
	}

	/**
	 * Resets the plot panel.
	 */
	@Override
	public void reset() {

		this.plotPanel.reset();
		
		GUITools.getMainFrame().resetTitleBar();
		
	}

	/**
	 * Returns whether or not the panel currently displays an item.
	 * 
	 * @return <code>true</code>, if an item is displayed, <code>false</code>,
	 *         if not
	 */
	@Override
	public boolean isEmpty() {
		return this.plotPanel.isEmpty();
	}

	private void updateMainFrameTitle(boolean multiSelection) {
		// update the title of the main frame according to the model name
		try {
			String modelName = "";
			if (multiSelection) {
				modelName = I18N.getGUILabelText("application.plot.multi");
			} else {
				modelName = this.plotPanel.getPlotModels().get(0)
						.getModelName();
			}

			GUITools.getMainFrame().setTitle(
					I18N.getGUILabelText(
							"application.frame.main.titleWithModelName",
							IQMConstants.APPLICATION_NAME,
							IQMConstants.APPLICATION_VERSION, modelName));
			
		} catch (Exception ex) {
			logger.error("Cannot update main frame title from plots: ", ex);
		}
	}
} // End of Plot
