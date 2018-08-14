package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFFT.java
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

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFFTDescriptor;


import flanagan.math.FourierTransform;

/**
 * @author Ahammer
 * @since   2012 12
 */
public class PlotOpFFT extends AbstractOperator {

	public PlotOpFFT() {
	}

	@SuppressWarnings("unused")
	@Override
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlotModel plotModel = ((IqmDataBox) pb.getSource(0)).getPlotModel();
		int windowing    = pb.getIntParameter("windowing");
		int sampleRate   = pb.getIntParameter("sampleRate");
		int resultLogLin = pb.getIntParameter("resultLogLin");

		String plotModelName = plotModel.getModelName();
		
		// new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
		int numDataPoints = signal.size();

		Vector<Double> signalPS = new Vector<Double>();
		Vector<Double> rangePS = new Vector<Double>();

		FourierTransform ft = new FourierTransform();

		if (windowing == PlotOpFFTDescriptor.WINDOWING_WITHOUT) {
			ft.removeWindow();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_RECTANGULAR) {
			ft.setRectangular();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_BARTLETT) {
			ft.setBartlett();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_WELCH) {
			ft.setWelch();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_HANN) {
			ft.setHann();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_HAMMING) {
			ft.setHamming();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_KAISER) {
			ft.setKaiser();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_GAUSSIAN) {
			ft.setGaussian();
		}

		// //data length must have a power of 2
		// int powerSize = 1;
		// while (signal.size() > powerSize){
		// powerSize = powerSize*2;
		// }
		// //powerSize = powerSize /2;
		// double[] data = new double[powerSize];
		// //set data
		// if (powerSize <= signal.size()) {
		// for (int i = 0; i < powerSize; i++){
		// data[i] = signal.get(i);
		// }
		// } else {
		// for (int i = 0; i < signal.size(); i++){
		// data[i] = signal.get(i);
		// }
		// for (int i = signal.size()-1; i < powerSize; i++){
		// data[i] =0.0d;
		// }
		// }

		// data length must not have a power of 2 because .powerSpectrum() truncates the signal
		// to the next smaller power of 2 value
		double[] data = new double[signal.size()];
		// set data
		for (int i = 0; i < signal.size(); i++) {
			data[i] = signal.get(i);
		}

		ft.setData(data);
		// ft.transform();
		
		this.fireProgressChanged(20);
		if (this.isCancelled(this.getParentTask())) return null;
	
		double[][] ps = ft.powerSpectrum();
		
		this.fireProgressChanged(60);
		if (this.isCancelled(this.getParentTask())) return null;
		
		for (int i = 0; i < ps[0].length; i++){
			if(resultLogLin == PlotOpFFTDescriptor.RESULT_LOG){
				signalPS.add(Math.log(ps[1][i]));  //log Power Spectrum
			}
			if(resultLogLin == PlotOpFFTDescriptor.RESULT_LIN){
				signalPS.add(ps[1][i]);  //Power Spectrum
			}
			//rangePS.add(ps[0][i]);   //frequencies
			//if sample frequency is not known:
			//rangePS.add( (double)i + 1.0d);   //frequencies

			//if sample frequency is known:	
			rangePS.add((((double)i +1.0) /ps[0].length) * (double)(sampleRate)/2.0);

		}

		// it is necessary to generate a new instance of PlotModel
		PlotModel plotModelNew = new PlotModel(plotModel.getDomainHeader(),
				plotModel.getDomainUnit(), plotModel.getDataHeader(),
				plotModel.getDataUnit(), rangePS, signalPS);
		
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(plotModelNew);
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new PlotOpFFTDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpFFTDescriptor.TYPE;
	}

}
