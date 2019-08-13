package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpSignalGenerator.java
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


import java.util.Random;
import java.util.Vector;

import org.apache.commons.math3.util.Precision;

import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.plot.bundle.descriptors.PlotOpSignalGeneratorDescriptor;

/**
 * @author Ahammer
 * @since   2012 11
 * @update 2017 10 added square, triangle and sawtooth
 */
public class PlotOpSignalGenerator extends AbstractOperator {

	public PlotOpSignalGenerator() {
	}

	@Override
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		int method = pb.getIntParameter("method"); // 0 Sine, 1 const, 2 Gaussian, 3 uniform  4 square
		int frequency     = pb.getIntParameter("frequency");
		int numPeriods    = pb.getIntParameter("nPeriods");
		int sampleRate    = pb.getIntParameter("sampleRate");
		int amplitude     = pb.getIntParameter("amplitude");
		int numDataPoints = pb.getIntParameter("nDataPoints");
		int mean          = pb.getIntParameter("mean");
		int stdDev        = pb.getIntParameter("stdDev");
		
		Vector<Double> signal = new Vector<Double>();
		Vector<Double> range = new Vector<Double>();
		String type = "";
		if (method == 0) {// sinus
			numDataPoints = (numPeriods * sampleRate / frequency);
			double factor = 2.0 * Math.PI * frequency / sampleRate;
			for (int i = 0; i < numDataPoints; i++) {		
				//Zero is only approximated by a very small number
				//Therefore we need to round 
				signal.add(Precision.round(amplitude * Math.sin(i * factor), 12));		
				range.add((double) i / sampleRate);
				this.fireProgressChanged((i+1)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}
			type = "Sine";
		}
		if (method == 1) {// const
			for (int i = 0; i < numDataPoints; i++) {
				signal.add((double) amplitude);
				range.add((double) i + 1.0d);
				this.fireProgressChanged((i+1)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}
			type = "Constant";
		}
		if (method == 2) {// Gaussian
			Random generator = new Random();
			for (int i = 0; i < numDataPoints; i++) {
				signal.add(generator.nextGaussian() * stdDev + mean);
				range.add((double) i + 1.0d);
				this.fireProgressChanged((i+1)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}
			type = "Gaussian";
		}
		if (method == 3) {// uniform random
			Random generator = new Random();
			for (int i = 0; i < numDataPoints; i++) {
				signal.add(generator.nextDouble() * amplitude);
				range.add((double) i + 1.0d);
				this.fireProgressChanged((i+1)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}
			type = "Uniform Random";
		}
		if (method == 4) {//square
			numDataPoints = (numPeriods * sampleRate / frequency);
			double factor = 2.0 * Math.PI * frequency / sampleRate;
			for (int i = 0; i < numDataPoints; i++) {
				//Zero is only approximated by a very small number
				//Therefore we need to round 
				signal.add(amplitude * Math.signum(Precision.round(Math.sin(i * factor), 12)));		
				range.add((double) i / sampleRate);
				this.fireProgressChanged((i+1)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}
			type = "Square";
		}
		if (method == 5) {//triangle
			numDataPoints = (numPeriods * sampleRate / frequency);
			double factor = 2.0 * Math.PI * frequency / sampleRate;
			for (int i = 0; i < numDataPoints; i++) {
				//Zero is only approximated by a very small number
				//Therefore we need to round 
				signal.add(amplitude * Math.asin(Precision.round(Math.sin(i * factor), 12))/Math.asin(1));		
				range.add((double) i / sampleRate);
				this.fireProgressChanged((i+1)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}
			type = "Triangle";
		}
		if (method == 6) {//sawtooth
			numDataPoints = (numPeriods * sampleRate / frequency);
			for (int i = 0; i < numDataPoints; i++) {		
			    //double sawPoint  = 2.0 * (frequency - Floor(frequency)) - 1.0;  //sawtooth
				double t = (double)i * frequency  /sampleRate;
			    signal.add(amplitude * ((t - Math.floor(t)) * 2.0 - 1.0));	
				range.add((double) i / sampleRate);
				this.fireProgressChanged((i+1)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}
			type = "Sawtooth";
		}

		// it is necessary to generate a new instance of PlotModel
		String rangeHeader = null;
		String rangeUnit = null;
		String dataHeader = null;
		String dataUnit = null;

		if (method == 0) {
			rangeHeader = "Time";
			rangeUnit = "s";
		} else {
			rangeHeader = "Data points";
			rangeUnit = "a.u.";
		}

		dataHeader = "Value";
		dataUnit = "a.u.";

		PlotModel plotModelNew = new PlotModel(rangeHeader, rangeUnit, dataHeader, dataUnit, range, signal);

		plotModelNew.setModelName("Signal" + (type.equals("")?"":" "+type));

		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(plotModelNew);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new PlotOpSignalGeneratorDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpSignalGeneratorDescriptor.TYPE;
	}

}
