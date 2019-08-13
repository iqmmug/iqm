package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpResample.java
 * 
 * $Id: IqmOpResample.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/op/IqmOpResample.java $
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



import java.util.Vector;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFilterDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpResampleDescriptor;

/**
 * @author Ahammer
 * @since  2018-11
 */
public class PlotOpResample extends AbstractOperator {

	public PlotOpResample() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@Override
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}
		
		// get the plot model
		PlotModel plotModel = ((IqmDataBox) pb.getSource(0)).getPlotModel();

		
		// get the parameters
		
		int resampleOption = pb.getIntParameter("ResampleOption"); //Down   Up
		int resampleFactor = pb.getIntParameter("ResampleFactor");
		int optIntP        = pb.getIntParameter("Interpolation");
		
		String plotModelName = plotModel.getModelName();

		Vector<Double> signal          = plotModel.getData();
		Vector<Double> domain          = plotModel.getDomain();
		Vector<Double> resampledSignal = new Vector<Double>();
		Vector<Double> resampledDomain = new Vector<Double>();
		
		int size = signal.size();
	
		//Convert to double array
//		double[] signalDbl = new double[size];		
//		for (int i=0;i<size;i++){
//			signalDbl[i] = signal.get(i);
//		}
					
		
		if (resampleOption == PlotOpResampleDescriptor.DOWNSAMPLE) {
			
			if (optIntP == PlotOpResampleDescriptor.INTERPOLATION_NONE) { 
				for (int i=0;  i < signal.size();  i=i+resampleFactor) {
					resampledSignal.add(signal.get(i));
					resampledDomain.add(domain.get(i));
				}
			}
			
			if (optIntP == PlotOpResampleDescriptor.INTERPOLATION_BILINEAR) { 
				for (int i=0;  i < signal.size()-resampleFactor+1;  i=i+resampleFactor) {
					double meanSignal =0.0;
					double meanDomain =0.0;
					for (int d=0; d<resampleFactor; d++) {
						meanSignal += (signal.get(i+d));
						meanDomain += (domain.get(i+d));
					}
					meanSignal = meanSignal/(double)resampleFactor;
					meanDomain = meanDomain/(double)resampleFactor;
					
					resampledSignal.add(meanSignal);
					resampledDomain.add(meanDomain);
				}		
			}
			if (optIntP == PlotOpResampleDescriptor.INTERPOLATION_BICUBIC) { 
				
			}
			if (optIntP == PlotOpResampleDescriptor.INTERPOLATION_BICUBIC2) { 
		
			}
			plotModelName += ": Downsampled";			
		}	
		
		
		if (resampleOption == PlotOpResampleDescriptor.UPSAMPLE) {
			if (optIntP == PlotOpResampleDescriptor.INTERPOLATION_NONE) { 
				for (int i=0;  i < signal.size();  i++) {
					for (int d=0; d<resampleFactor; d++) { //adding identical data values
						resampledSignal.add(signal.get(i));
						resampledDomain.add(domain.get(i));
					}
				}	
			}		
			if (optIntP == PlotOpResampleDescriptor.INTERPOLATION_BILINEAR) { 
				
				for (int i=0;  i < signal.size()-1;  i++) {
					double linearDeltaSignal = (signal.get(i+1) - signal.get(i))/resampleFactor;
					double linearDeltaDomain = (domain.get(i+1) - domain.get(i))/resampleFactor;
					for (int d=0; d<resampleFactor; d++) { //adding values
						resampledSignal.add(signal.get(i) + d*linearDeltaSignal);
						resampledDomain.add(domain.get(i) + d*linearDeltaDomain);
					}
				}	
			}
			if (optIntP == PlotOpResampleDescriptor.INTERPOLATION_BICUBIC) { 
				
			}
			if (optIntP == PlotOpResampleDescriptor.INTERPOLATION_BICUBIC2) { 
		
			}		
			plotModelName += ": Upsampled";
		}
				
		// generate the new plot model
		PlotModel plotModelNew = new PlotModel(plotModel.getDomainHeader(), plotModel.getDomainUnit(),
				                               plotModel.getDataHeader(),   plotModel.getDataUnit(), 
				                               resampledDomain, resampledSignal);

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
		return PlotOpResampleDescriptor.TYPE;
	}
}
