package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpDCMGenerator.java
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


import java.util.Random;
import java.util.Vector;

import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.plot.bundle.descriptors.PlotOpDCMGeneratorDescriptor;

/**
 * <b>DCM discrete chaotic map</b><br>
 * According to L.Eduardo V.Silva L.O.Murta Jr Evaluation of physiologic complexity in time series
 * using generalized sample entropy and surrogate data analysis. Chaos 22, 2012
 * 
 * @author Ahammer
 * @since   2012 12
 */
public class PlotOpDCMGenerator extends AbstractOperator{

	public PlotOpDCMGenerator() {
	}
	
	@Override
	public IResult run(IWorkPackage wp) {
		
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}
		
		//PlotModel plotModel = (PlotModel) pb.getObjectParameter(0);		
		int method        = pb.getIntParameter("method"); //0 Logistic 1 Henon 2 Cubic 3 Spence
		double paramA     = pb.getDoubleParameter("paramA");
		double paramB     = pb.getDoubleParameter("paramB");
		int    powerN     = pb.getIntParameter("powerNDataPoints");
	
	
		int numDataPoints = (int) Math.pow(2, powerN);
		
		Vector<Double> signal = new Vector<Double>();
		Vector<Double> range  = new Vector<Double>();
		Vector<Double> signal2 = new Vector<Double>(); //only for Henon map
		
		
		//set first data point randomly
		Random generator = new Random();
		signal.add(generator.nextDouble() + 0.0000001d);
		signal2.add(generator.nextDouble() + 0.0000001d);
		range.add((double)0.0d + 1.0d);
		
		String type = "";
		if (method == 0){// Logistic
			for (int i = 1; i < numDataPoints; i++){
				signal.add(paramA*signal.get(i-1)*(1.0d-signal.get(i-1)));
				range.add((double)i + 1.0d);
				this.fireProgressChanged((i)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}	
			type = "Logistic";
		}	
		if (method == 1){//Henon
			for (int i = 1; i < numDataPoints; i++){
				signal.add(signal2.get(i-1) + 1.0d - (paramA*signal.get(i-1)*signal.get(i-1)));
				signal2.add(paramB * signal.get(i-1));
				range.add((double)i + 1.0d);
				this.fireProgressChanged((i)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}	
			type = "Henon";
		}
		if (method == 2){//Cubic
			for (int i = 1; i < numDataPoints; i++){
				signal.add(paramA*signal.get(i-1)*(1.0d-signal.get(i-1)*signal.get(i-1)));
				range.add((double)i + 1.0d);
				this.fireProgressChanged((i)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}	
			type = "Cubic";
		}
		if (method == 3){//Spence
			for (int i = 1; i < numDataPoints; i++){
				signal.add(Math.abs(Math.log(signal.get(i-1))));
				range.add((double)i + 1.0d);
				this.fireProgressChanged((i)*90/numDataPoints);
				if (this.isCancelled(this.getParentTask())) return null;
			}	
			
			type = "Spence";
		}
		
		//it is necessary to generate a new instance of PlotModel
		String rangeHeader = null;
		String rangeUnit   = null;
		String dataHeader  = null;
		String dataUnit    = null;
		
		rangeHeader = "Data points";
		rangeUnit   = "a.u.";
		
		dataHeader  = "Value";
		dataUnit    = "a.u.";
		
		PlotModel plotModelNew = new PlotModel(rangeHeader,
											   rangeUnit,
											   dataHeader,
											   dataUnit,
											   range,
											   signal);
		
		plotModelNew.setModelName("DCM Signal" + (type.equals("")?"":" "+type));
	    
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(plotModelNew);
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new PlotOpDCMGeneratorDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpDCMGeneratorDescriptor.TYPE;
	}
	
}
