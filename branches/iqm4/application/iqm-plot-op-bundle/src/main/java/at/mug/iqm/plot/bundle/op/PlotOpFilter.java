package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFilter.java
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

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFilterDescriptor;

/**
 * @author Philipp Kainz, Michael Mayrhofer-R.
 * @since 3.2
 */
public class PlotOpFilter extends AbstractOperator {

	public PlotOpFilter() {
	}

	@Override
	public IResult run(IWorkPackage wp) throws Exception {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		// get the plot model
		PlotModel plotModel = ((IqmDataBox) pb.getSource(0)).getPlotModel();
		
		// get the parameters
		int method = pb.getIntParameter("method");
		int range = pb.getIntParameter("range");
		
		String plotModelName = plotModel.getModelName();

		Vector<Double> signal = plotModel.getData();
		Vector<Double> filteredSignal = new Vector<Double>();
		
		int size = signal.size();
		double[] signalDbl = new double[size];
		
				
		for (int i=0;i<size;i++){
			signalDbl[i] = signal.get(i);
		}
			
			

		if (method == PlotOpFilterDescriptor.METHOD_MOVING_AVERAGE) { 

			// ------------------------------------------------------------------------------
			// calculate filtered signal (with moving mean / "moving average curve (MAC)"):
			
			Mean tempMean = new Mean();
			
			for (int i=0;i<(range-1)/2;i++) {
				filteredSignal.add(tempMean.evaluate(signalDbl, 0, i + (range-1)/2 +1));
			}
			for (int i=(range-1)/2;i<size-(range-1)/2;i++) {
				filteredSignal.add(tempMean.evaluate(signalDbl, i - (range-1)/2, range));
			}
			for (int i=size-(range-1)/2;i<size;i++) {
				filteredSignal.add(tempMean.evaluate(signalDbl, i - (range-1)/2, (size-i) + (range-1)/2 ));				
			}

			plotModelName += ": Moving average filtered";
		} // end if (method == MAC)
		
		if (method == PlotOpFilterDescriptor.METHOD_MOVING_MEDIAN) {

			// ------------------------------------------------------------------------------
			//calculate filtered signal (with moving median)
						
			Median tempMedian = new Median();
			
			for (int i=0;i<(range-1)/2;i++) {
				filteredSignal.add(tempMedian.evaluate(signalDbl, 0, i + (range-1)/2 +1));
			}
			for (int i=(range-1)/2;i<size-(range-1)/2;i++) {
				filteredSignal.add(tempMedian.evaluate(signalDbl, i - (range-1)/2, range));
			}
			for (int i=size-(range-1)/2;i<size;i++) {
				filteredSignal.add(tempMedian.evaluate(signalDbl, i - (range-1)/2, (size-i) + (range-1)/2 ));				
			}
			
			
			
			plotModelName += ": Moving median filtered";
		}

		// generate the new plot model
		PlotModel plotModelNew = new PlotModel(plotModel.getDomainHeader(),
				plotModel.getDomainUnit(), plotModel.getDataHeader(),
				plotModel.getDataUnit(), plotModel.getDomain(), filteredSignal);

		plotModelNew.setModelName(plotModelName);

		if (this.isCancelled(this.getParentTask()))
			return null;
		return new Result(plotModelNew);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new PlotOpFilterDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpFilterDescriptor.TYPE;
	}

}
