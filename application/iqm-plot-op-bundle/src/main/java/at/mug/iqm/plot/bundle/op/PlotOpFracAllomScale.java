package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFracAllomScale.java
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
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.AllometricScaling;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracAllomScaleDescriptor;

/**
 * @author Ahammer
 * @since   2013 09
 */
public class PlotOpFracAllomScale extends AbstractOperator{

	public PlotOpFracAllomScale() {
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
		int regStart  = pb.getIntParameter("regStart");		
		int regEnd    = pb.getIntParameter("regEnd");		
		boolean showPlot = false;
		boolean deleteExistingPlot = false;
		if (pb.getIntParameter("showPlot") == 1 )          showPlot = true;	  	
		if (pb.getIntParameter("deletePlot") == 1) deleteExistingPlot = true;	  	
	
		String plotModelName = plotModel.getModelName();
		
		//new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());


		
		//check data length 
		if (signal.size() < 10){
		
		}
		
		@SuppressWarnings("rawtypes")
		Vector[] asData;  //asData[0].. means     asData[1]... SDs
		double[] result;
		String plotName = "Allometric Scaling Plot";
		
		double dim    = -999999.0; 
		double stdDev = -999999.0; 
		double r2     = -999999.0; 
		double adjR2  = -999999.0; 
	
		this.fireProgressChanged(5);
		AllometricScaling as = new AllometricScaling();
		asData = as.calcMeansAndVariances(signal);
		this.fireProgressChanged(50);
		result = as.calcLogRegression(asData, regStart, regEnd, plotName, showPlot, deleteExistingPlot);
		System.out.println("PlotOpFracAllomScale: slope: " + result[0]);
		this.fireProgressChanged(90);
		dim    = 2.00 - result[0]/2.0 ; 
		stdDev = result[1]; 
		r2     = result[2]; 
		adjR2  = result[3]; 
	
		//create model and Table
		TableModel model = new TableModel("AllomScale");
		
		model.addColumn("Plot name (RegStart="+regStart+" RegEnd="+regEnd+")");
		model.addColumn("RegStart");
		model.addColumn("RegEnd");
	
		model.addRow(new String[] {plotModelName, String.valueOf(regStart), String.valueOf(regEnd) });
			
		int numColumns = model.getColumnCount();

		model.addColumn("Das");
		model.addColumn("StdDev");
		model.addColumn("r2");
		model.addColumn("adjusted_r2");
		model.setValueAt(dim,    0, numColumns);
		model.setValueAt(stdDev, 0, numColumns+1);
		model.setValueAt(r2,     0, numColumns+2);
		model.setValueAt(adjR2,  0, numColumns+3);
	
		
		
		//format column widths
//		numColumns = model.getColumnCount();
//		for (int i = 0; i < numColumns; i++){	
//			if ((model.getColumnName(i) == "k") ||
//				(model.getColumnName(i) == "RegStart") ||
//				(model.getColumnName(i) == "RegEnd")) {
//				jTable.getColumnModel().getColumn(i).setPreferredWidth(40);
//			}
////			if ((model.getColumnName(i) == "Dh")){
////				jTable.getColumnModel().getColumn(i).setPreferredWidth(40);
////			}
//		}

		this.fireProgressChanged(95);
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(model);
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new PlotOpFracAllomScaleDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpFracAllomScaleDescriptor.TYPE;
	}
	
}
