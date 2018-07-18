package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFracSurrogate.java
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


import java.util.Vector;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracSurrogateDescriptor;

/**
 * @author Ahammer
 * @since   2012 11
 */
@SuppressWarnings("unused")
public class PlotOpFracSurrogate extends AbstractOperator {

	public PlotOpFracSurrogate() {
	}

	@Override
	public IResult run(IWorkPackage wp) {
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlotModel plotModel = ((IqmDataBox) pb.getSource(0)).getPlotModel();
		int method = pb.getIntParameter("method");

		String plotModelName = plotModel.getModelName();
		
		// new instance is essential
		// Vector<Double> signal = new Vector<Double>(plotModel.getData());

		// Here this works too
		Vector<Double> dataY = plotModel.getData();

		if (this.isCancelled(this.getParentTask())) return null;

		Surrogate surrogate = new Surrogate(this);
		surrogate.setProgressBarMin(0);
		surrogate.setProgressBarMax(95);
		Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(dataY, method, 1);
	
		
		// set new data to current model does not work, because changes are fed
		// back to prior instances (items) e.g. in Tank
		// plotModel.setData(plots.get(0));

		// it is necessary to generate a new instance of PlotModel
		PlotModel plotModelNew = new PlotModel(plotModel.getDomainHeader(),
				plotModel.getDomainUnit(), plotModel.getDataHeader(),
				plotModel.getDataUnit(), plotModel.getDomain(), plots.get(0));
		
		if (method == Surrogate.SURROGATE_AAFT)           plotModelNew.setModelName("Surrogate-AAFT-" + plotModelName);
		if (method == Surrogate.SURROGATE_GAUSSIAN)       plotModelNew.setModelName("Surrogate-Gaussian-" + plotModelName);
		if (method == Surrogate.SURROGATE_MULTIVARIATE)   plotModelNew.setModelName("Surrogate-Multivariate-" + plotModelName);
		if (method == Surrogate.SURROGATE_PSEUDOPERIODIC) plotModelNew.setModelName("Surrogate-Pseudoperiodic-" + plotModelName);
		if (method == Surrogate.SURROGATE_RANDOMPHASE)    plotModelNew.setModelName("Surrogate-Randomphase-" + plotModelName);
		if (method == Surrogate.SURROGATE_SHUFFLE)        plotModelNew.setModelName("Surrogate-Shuffle-" + plotModelName);
		
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(plotModelNew);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new PlotOpFracSurrogateDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpFracSurrogateDescriptor.TYPE;
	}

}
