package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpAutoCorrelation.java
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
import at.mug.iqm.plot.bundle.descriptors.PlotOpAutoCorrelationDescriptor;


import flanagan.math.FourierTransform;

/**
 * @author Ahammer
 * @since   2012 11
 */
@SuppressWarnings("unused")
public class PlotOpAutoCorrelation extends AbstractOperator {

	public PlotOpAutoCorrelation() {
	}

	/**
	 * This method calculates the mean of a data series
	 * 
	 * @param data1D
	 * @return Double Mean
	 */
	public Double calcMean(Vector<Double> data1D) {
		double sum = 0;
		for (double d : data1D) {
			sum += d;
		}
		return sum / data1D.size();
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
		int isLimitDelays = pb.getIntParameter("limitDelays");
		int numDelays = pb.getIntParameter("numDelays");
		
		String plotModelName = plotModel.getModelName();

		String type = "";

		// new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
		int numDataPoints = signal.size();
		// number of delays > numDataPoints is nor allowed
		if (numDelays >= numDataPoints) {
			numDelays = numDataPoints - 1;
		}

		if (isLimitDelays == 1) {
			numDelays = numDelays - 1;
		} else {
			numDelays = numDataPoints - 1;
		}

		Vector<Double> signalAuCorr = new Vector<Double>(numDelays);
		Vector<Double> rangeAuCorr = new Vector<Double>();
		this.fireProgressChanged(10);

		if (method == 0) { // Large N
			type = "Large N";
			double mean = this.calcMean(signal);
			for (int d = 0; d <= numDelays; d++) {
				double a = 0.0d;
				double b = 0.0d;
				for (int i = 0; i < numDataPoints - d; i++) {
					a = a + ((signal.get(i) - mean) * (signal.get(i + d) - mean));
					b = b + ((signal.get(i) - mean) * (signal.get(i) - mean));
				}
				// finalize sum for b
				for (int i = numDataPoints - d - 1; i < numDataPoints; i++) { 
					b = b + ((signal.get(i) - mean) * (signal.get(i) - mean));
				}
				if ((a == 0) && (b == 0)) {
					signalAuCorr.add(1.0d);
				} else {
					signalAuCorr.add(a / b);
				}
				this.fireProgressChanged((int)(d+1)*95/numDelays);
				if (this.isCancelled(this.getParentTask())) return null;
			}
			rangeAuCorr = plotModel.getDomain();
		}
		if (method == 1) { // Small N
			type = "Small N";
			double mean1 = this.calcMean(signal);
			for (int d = 0; d <= numDelays; d++) {
				Vector<Double> signal2 = new Vector<Double>(signal);
				while (signal2.size() > (signal.size() - d)) {
					signal2.remove(0);
				}
				double mean2 = this.calcMean(signal2);
				double a = 0.0d;
				double b = 0.0d;
				double c = 0.0d;
				for (int i = 0; i < numDataPoints - d; i++) {
					a = a + ((signal.get(i)  - mean1) * (signal2.get(i) - mean2));
					b = b + ((signal.get(i)  - mean1) * (signal.get(i)  - mean1));
					c = c + ((signal2.get(i) - mean2) * (signal2.get(i) - mean2));
				}
				b = Math.sqrt(b);
				c = Math.sqrt(c);

				if ((a == 0) && (b == 0) && (c == 0)) {
					signalAuCorr.add(1.0d);
				} else {
					signalAuCorr.add(a / (b * c));
				}
				this.fireProgressChanged((int)(d+1)*95/numDelays);
				if (this.isCancelled(this.getParentTask())) return null;
			}
			rangeAuCorr = plotModel.getDomain();
		}
		if (method == 2) { // FFT
			type = "FFT";
			FourierTransform ft = new FourierTransform();

			// double[] data = new double[signal.size()];
			// //set data
			// for (int i = 0; i < signal.size(); i++){
			// data[i] = signal.get(i);
			// }

			// data length must have a power of 2
			int powerSize = 1;
			while (signal.size() > powerSize) {
				powerSize = powerSize * 2;
			}
			double[] data = new double[powerSize];
			// set data
			if (powerSize <= signal.size()) {
				for (int i = 0; i < powerSize; i++) {
					data[i] = signal.get(i);
				}
			} else {
				for (int i = 0; i < signal.size(); i++) {
					data[i] = signal.get(i);
				}
				for (int i = signal.size() - 1; i < powerSize; i++) {
					data[i] = 0.0d;
				}
			}

			this.fireProgressChanged(50);
			if (this.isCancelled(this.getParentTask())) return null;
			
			double[][] autoCorr = ft.correlate(data, data);
			for (int i = 0; i < autoCorr[0].length; i++) {
				signalAuCorr.add(autoCorr[1][i]); // corr data
				rangeAuCorr.add(autoCorr[0][i]); // delay data points
			}
		}

		// it is necessary to generate a new instance of PlotModel
		PlotModel plotModelNew = new PlotModel(plotModel.getDomainHeader(), 
											   plotModel.getDomainUnit(),
											   plotModel.getDataHeader(),
											   plotModel.getDataUnit(),
											   rangeAuCorr,
											   signalAuCorr);

		plotModelNew.setModelName("Auto Correlation" + (type.equals("") ? "" : " " + type));

		this.fireProgressChanged(95);
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(plotModelNew);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new PlotOpAutoCorrelationDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpAutoCorrelationDescriptor.TYPE;
	}

}
