package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpMath.java
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
import at.mug.iqm.plot.bundle.descriptors.PlotOpMathDescriptor;
import at.mug.iqm.plot.bundle.gui.PlotGUI_Math;

/**
 * @author Ahammer
 * @since   2012 12
 */
@SuppressWarnings("unused")
public class PlotOpMath extends AbstractOperator{

	public PlotOpMath() {
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
		int method     = pb.getIntParameter("method");
		int optDeltaX  = pb.getIntParameter("optDeltaX");
		
		String plotModelName = plotModel.getModelName();
		
		//new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
		Vector<Double> range  = new Vector<Double>(plotModel.getDomain());
			
		Vector<Double> signalNew = new Vector<Double>();
		Vector<Double> rangeNew  = new Vector<Double>();
		this.fireProgressChanged(1);
		if (this.isCancelled(this.getParentTask())) return null;
					
		if (method == PlotGUI_Math.DIFF_Y_PLUS){	
			for (int i = 0; i < signal.size()-1; i++){
				double deltaX = 0; 
				if (optDeltaX == PlotGUI_Math.DELTA_X_UNITY)  deltaX = 1.0d;
				if (optDeltaX == PlotGUI_Math.DELTA_X_ACTUAL) deltaX = range.get(i+1) - range.get(i); 
				signalNew.add((signal.get(i+1) - signal.get(i))/deltaX);
				this.fireProgressChanged((i+1)*90/signal.size());
				if (this.isCancelled(this.getParentTask())) return null;
			}
		}	
		if (method == PlotGUI_Math.DIFF_Y_MINUS){
			for (int i = 1; i < signal.size(); i++){
				double deltaX = 0; 
				if (optDeltaX == PlotGUI_Math.DELTA_X_UNITY)  deltaX = 1.0d;
				if (optDeltaX == PlotGUI_Math.DELTA_X_ACTUAL) deltaX = range.get(i) - range.get(i-1); 
				signalNew.add((signal.get(i) - signal.get(i-1))/deltaX);
				this.fireProgressChanged((i+1)*90/signal.size());
				if (this.isCancelled(this.getParentTask())) return null;
			}
		}	
		if (method == PlotGUI_Math.DIFF_Y_PLUSMINUS){	
			for (int i = 1; i < signal.size()-1; i++){
				double deltaX = 0; 
				if (optDeltaX == PlotGUI_Math.DELTA_X_UNITY)  deltaX = 2.0d;
				if (optDeltaX == PlotGUI_Math.DELTA_X_ACTUAL) deltaX = range.get(i+1) - range.get(i-1); 
				signalNew.add((signal.get(i+1) - signal.get(i-1))/deltaX);
				this.fireProgressChanged((i+1)*90/signal.size());
				if (this.isCancelled(this.getParentTask())) return null;
			}
		}
		if (method == PlotGUI_Math.SUM){	
			signalNew.add(signal.get(0));
			for (int i = 1; i < signal.size(); i++){
				double deltaX = 0; 
				if (optDeltaX == PlotGUI_Math.DELTA_X_UNITY)  deltaX = 1.0d;
				if (optDeltaX == PlotGUI_Math.DELTA_X_ACTUAL) deltaX = range.get(i) - range.get(i-1); 
				signalNew.add(signalNew.get(i-1) + signal.get(i)*deltaX);	
				this.fireProgressChanged((i+1)*90/signal.size());
				if (this.isCancelled(this.getParentTask())) return null;
			}
		}
		//set range vector
		for (int i = 0; i < signalNew.size(); i++){		
			if (optDeltaX == PlotGUI_Math.DELTA_X_UNITY)  rangeNew.add((double)i+1);  
			if (optDeltaX == PlotGUI_Math.DELTA_X_ACTUAL) rangeNew.add(range.get(i));
		}
		 
		this.fireProgressChanged(95);
		if (this.isCancelled(this.getParentTask())) return null;
		//it is necessary to generate a new instance of PlotModel
		PlotModel plotModelNew = new PlotModel(plotModel.getDomainHeader(),
											   plotModel.getDomainUnit(),
											   plotModel.getDataHeader(),
											   plotModel.getDataUnit(),
											   rangeNew,
											   signalNew);
	    
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(plotModelNew);
	}


	@Override
	public String getName() {
		if (this.name == null){
			this.name = new PlotOpMathDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpMathDescriptor.TYPE;
	}
	
}
