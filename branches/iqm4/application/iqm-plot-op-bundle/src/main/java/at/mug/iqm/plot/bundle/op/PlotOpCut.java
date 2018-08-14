package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpCut.java
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
import at.mug.iqm.plot.bundle.descriptors.PlotOpCutDescriptor;

/**
 * @author Ahammer
 * @since   2012 11
 */
@SuppressWarnings("unused")
public class PlotOpCut extends AbstractOperator{

	public PlotOpCut() {
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
		int start = pb.getIntParameter("start");
		int end   = pb.getIntParameter("end");
			
		String plotModelName = plotModel.getModelName();
		
		//new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
		Vector<Double> domain = new Vector<Double>(plotModel.getDomain());

		int length = signal.size();
		
		if (end < length){
			for (int i = length-1; i >= end ; i--){
				signal.remove(i);
				domain.remove(i);
				//this.fireProgressChanged((int)(i-length)*-50/(length-end));  //needs far too much processing time
				if (this.isCancelled(this.getParentTask())) return null;
			}		
		}
		this.fireProgressChanged(20); 
	    if (start > 1){
			for (int i = start-2; i >= 0; i--){
				if (i >= 0 ){
				   signal.remove(i);
				   domain.remove(i);
				}
				//this.fireProgressChanged((int)(i-start -2)*-45/(start) + 50); //needs far too much processing time
				if (this.isCancelled(this.getParentTask())) return null;
			}
	    }
	    
		this.fireProgressChanged(95);
	

		//set new data to current model does not work, because changes are fed back to prior instances (items) e.g. in Tank 
		//plotModel.setData(signal); 
		
		//it is necessary to generate a new instance of PlotModel
		PlotModel plotModelNew = new PlotModel(plotModel.getDomainHeader(),
											   plotModel.getDomainUnit(),
											   plotModel.getDataHeader(),
											   plotModel.getDataUnit(),
											   domain,  ///plotModel.getDomain(),....orignal is too long  bugfix 24.10.2017 
											   signal);
	    
		plotModelNew.setModelName("Cut");
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(plotModelNew);
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new PlotOpCutDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpCutDescriptor.TYPE;
	}
	
}
