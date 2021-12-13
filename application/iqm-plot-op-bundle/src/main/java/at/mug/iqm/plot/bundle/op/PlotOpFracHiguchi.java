package at.mug.iqm.plot.bundle.op;

import java.util.ArrayList;
import java.util.List;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFracHiguchi.java
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

import at.mug.iqm.api.gui.BoardPanel;
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
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.Higuchi;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracHiguchiDescriptor;

/**
 * @author Ahammer
 * @since   2012 11
 */
public class PlotOpFracHiguchi extends AbstractOperator{

	public PlotOpFracHiguchi() {
	}
	
	/**
	 * This method calculates the mean of a data series
	 * 
	 * @param data1D
	 * @return Double Mean
	 */
	private Double calcMean(Vector<Double> data1D) {
		double sum = 0;
		for (double d : data1D) {
			sum += d;
		}
		return sum / data1D.size();
	}
	
	@Override
	public IResult run(IWorkPackage wp) throws Exception {
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlotModel plotModel = ((IqmDataBox) pb.getSource(0)).getPlotModel();
		int method    = pb.getIntParameter("method");
		int numK      = pb.getIntParameter("numK");
		int boxLength = pb.getIntParameter("boxLength");
		int regStart  = pb.getIntParameter("regStart");		
		int regEnd    = pb.getIntParameter("regEnd");	
		int typeSurr  = pb.getIntParameter("typeSurr");
		int nSurr     = pb.getIntParameter("nSurr");
		boolean optShowPlot = false;
		boolean optDeleteExistingPlot = false;
		if (pb.getIntParameter("showPlot") == 1 )          optShowPlot = true;	  	
		if (pb.getIntParameter("deletePlot") == 1) optDeleteExistingPlot = true;	  	

		if (method == 1) optShowPlot =  false;	
		
		String plotModelName = plotModel.getModelName();
		
		//new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());

		int numDhValues = 1;
		
		//number of Dh values	
		if (method == 0)  numDhValues = 1; // Single value
		if (method == 1)  numDhValues = signal.size() - boxLength + 1;	//Gliding Box
		
		if ((method == 1) && (numDhValues < 1)){ //check for gliding method
			DialogUtil.getInstance().showDefaultErrorMessage("Higuchi dimension cannot be calculated because gliding box length is too large");
			boxLength = signal.size()/2;
			numDhValues = signal.size() - boxLength + 1;
			DialogUtil.getInstance().showDefaultErrorMessage("Box length set to: " + boxLength);				
			//return null;
		}
		
		//check data length 
		if (numK > signal.size()/3 ){
			numK = signal.size()/3; //reset to 1/3 of data length
		}
		
		double[] dim    = new double [numDhValues]; //for single Dh value only one element is used
		double[] stdDev = new double [numDhValues];
		double[] r2     = new double [numDhValues];
		double[] adjR2  = new double [numDhValues];
		Vector<Double> lnDataX = null;
		Vector<Double> lnDataY = null;
		Vector<Double> dimSurr = new Vector<Double>(); //May be used to store individual surrogate Dh values
		double dimSurrMean     = 0.0;
		double stdDevSurrMean  = 0.0;
		double r2SurrMean      = 0.0; 
		double adjR2SurrMean   = 0.0;
			
		if (method == 0) { //single value
			//if (typeSurr == -1) {  //no surrogate
				Higuchi hig = new Higuchi(this);
				Vector<Double> L = hig.calcLengths(signal, numK);
				String plotName = "Plot";
				plotName = plotModelName;
				double[] result = hig.calcDimension(L, regStart, regEnd, plotName, optShowPlot, optDeleteExistingPlot);
				BoardPanel.appendTextln("IqmOpFracHiguchi  Dh:" + (-result[0])+ " StdDev:" + result[1] + " r2:" + result[2] + " adj.r2:"+result[3]);
				dim[0]     = -result[0];
				stdDev[0]  = result[1];
				r2[0]      = result[2];
				adjR2[0]   = result[3];
				lnDataX = hig.getLnDataX();
				lnDataY = hig.getLnDataY();
				
	//			//Special for Alex and Wolfram  (Higuchi Zeilen 170 und  177)
	//			BoardPanel.appendTextln("IqmOpFracHiguchi");
	//			BoardPanel.appendTextln("K      Dh     StdDev      r2      adj.r2");
	//			for (int i = 3; i<=numK; i++){
	//				result = hig.calcDimension(L, regStart, i, plotName, optShowPlot, optDeleteExistingPlot);
	//				dim[0]     = -result[0];
	//				stdDev[0]  = result[1];
	//				r2[0]      = result[2];
	//				adjR2[0]   = result[3];
	//				BoardPanel.appendTextln(i +"     "+ (-result[0])+ "     " + result[1] + "      " + result[2] + "     "+result[3]);
	//			}
			//}
			if (typeSurr >= 0) { //Surrogates 			
				for (int n= 1; n <= nSurr; n++) {							
					
					//create a surrogate signal
					Surrogate surrogate = new Surrogate(this);
					Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(signal, typeSurr, 1);
					Vector<Double> signalSurr = plots.get(0);
															
					hig = new Higuchi(this);
					L = hig.calcLengths(signalSurr, numK);
					plotName = "Plot";
					plotName = plotModelName;
					result = hig.calcDimension(L, regStart, regEnd, plotName, optShowPlot, optDeleteExistingPlot);
					//BoardPanel.appendTextln("IqmOpFracHiguchi  Dh:" + (-result[0])+ " StdDev:" + result[1] + " r2:" + result[2] + " adj.r2:"+result[3]);
					dimSurr.add(-result[0]);
					stdDevSurrMean  = stdDevSurrMean + result[1];
					r2SurrMean      = r2SurrMean + result[2];
					adjR2SurrMean   = adjR2SurrMean + result[3];
				}	
				dimSurrMean = this.calcMean(dimSurr);
				stdDevSurrMean  = stdDevSurrMean/nSurr;
				r2SurrMean      = r2SurrMean/nSurr;
				adjR2SurrMean   = adjR2SurrMean/nSurr;				
			}
			
			
		}
		
		if (method == 1) { //gliding values		
			if (typeSurr == -1) {  //no surrogate
				for (int i = 0; i < numDhValues; i++){
					Higuchi hig = new Higuchi();
					Vector<Double> subSignal = new Vector<Double>();
					for (int ii = i; ii < i+boxLength; ii++){ // get subvector
						subSignal.add(signal.get(ii));
					}
					Vector<Double> L = hig.calcLengths(subSignal, numK);
					String plotName = "Plot";
					double[] result = hig.calcDimension(L, regStart, regEnd, plotName, optShowPlot, optDeleteExistingPlot);
					//IqmBoardPanel.appendTexln("IqmOpFracHiguchi  Dhx:" + (-result[0])+ " StdDev:" + result[1] + " r2:" + result[2] + " adj.r2:"+result[3]);
					dim[i]     = -result[0];
					stdDev[i]  = result[1];
					r2[i]      = result[2];
					adjR2[i]   = result[3];
				}
			}	
			if (typeSurr >= 0) { //Surrogates before 		
				for (int i = 0; i < numDhValues; i++){
					Higuchi hig = new Higuchi();
					Vector<Double> subSignal = new Vector<Double>();
					for (int ii = i; ii < i+boxLength; ii++){ // get subvector
						subSignal.add(signal.get(ii));
					}
					for (int n= 1; n <= nSurr; n++) {		
						
						//create a surrogate signal
						Surrogate surrogate = new Surrogate(this);
						Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(subSignal, typeSurr, 1);
						Vector<Double> subSignalSurr = plots.get(0);
					
						Vector<Double> L = hig.calcLengths(subSignalSurr, numK);
						String plotName = "Plot";
						double[] result = hig.calcDimension(L, regStart, regEnd, plotName, optShowPlot, optDeleteExistingPlot);
						//IqmBoardPanel.appendTexln("IqmOpFracHiguchi  Dhx:" + (-result[0])+ " StdDev:" + result[1] + " r2:" + result[2] + " adj.r2:"+result[3]);
					
						dim[i]     = dim[i]    -result[0];
						stdDev[i]  = stdDev[i] + result[1];
						r2[i]      = r2[i]     + result[2];
						adjR2[i]   = adjR2[i]  + result[3];	
					}
					dim[i]     = dim[i]/nSurr;
					stdDev[i]  = stdDev[i]/nSurr;
					r2[i]      = r2[i]/nSurr;
					adjR2[i]   = adjR2[i]/nSurr;				
				}
			}
		}

	
		//create model and Table
		TableModel model = new TableModel("Higuchi");
	
		if (method == 0) { //single value
			model.addColumn("Plot name");
			model.addColumn("k");
			model.addColumn("RegStart");
			model.addColumn("RegEnd");
			model.addColumn("Surrogate");
			//model.addRow(new String[] {"Current plot", String.valueOf(numK), String.valueOf(regStart), String.valueOf(regEnd) });
			//model.addRow(new String[] {"Current plot" });
			String surrogate = "";
			if (typeSurr == -1) surrogate = "No"; 
			if (typeSurr == Surrogate.SURROGATE_AAFT)        surrogate = "AAFT x" + nSurr ; 
			if (typeSurr == Surrogate.SURROGATE_GAUSSIAN)    surrogate = "Gaussian x" +nSurr; 
			if (typeSurr == Surrogate.SURROGATE_RANDOMPHASE) surrogate = "Rand Phase x"+ nSurr; 
			if (typeSurr == Surrogate.SURROGATE_SHUFFLE)     surrogate = "Shuffle x "+nSurr; 
			model.addRow(new String[] {plotModelName, String.valueOf(numK), String.valueOf(regStart), String.valueOf(regEnd), surrogate});
	
		}
		if (method == 1) { //gliding values	
			model.addColumn("Plot name");
			model.addColumn("BoxSize="+ boxLength);
			model.addColumn("k");
			model.addColumn("RegStart");
			model.addColumn("RegEnd");
			model.addColumn("Surrogate");
			
			for (int i = 0; i < numDhValues; i++){
				//model.addRow(new String[] {"#:"+(i+1), String.valueOf(numK), String.valueOf(regStart), String.valueOf(regEnd) });
				String surrogate = "";
				if (typeSurr == -1) surrogate = "No"; 
				if (typeSurr == Surrogate.SURROGATE_AAFT)        surrogate = "AAFT x" + nSurr ; 
				if (typeSurr == Surrogate.SURROGATE_GAUSSIAN)    surrogate = "Gaussian x" +nSurr; 
				if (typeSurr == Surrogate.SURROGATE_RANDOMPHASE) surrogate = "Rand Phase x"+ nSurr; 
				if (typeSurr == Surrogate.SURROGATE_SHUFFLE)     surrogate = "Shuffle x "+nSurr; 
				model.addRow(new String[] {plotModelName, "#:"+(i+1), String.valueOf(numK), String.valueOf(regStart), String.valueOf(regEnd), surrogate});
			}
		}
		//model.addColumn("k");
		//model.addColumn("RegStart");
		//model.addColumn("RegEnd");
			
	
		int numColumns = model.getColumnCount();

		if (method == 0) { //single value
			model.addColumn("Dh");
			model.addColumn("StdDev");
			model.addColumn("r2");
			model.addColumn("adjusted_r2");
			model.setValueAt(dim[0],    0, numColumns);
			model.setValueAt(stdDev[0], 0, numColumns+1);
			model.setValueAt(r2[0],     0, numColumns+2);
			model.setValueAt(adjR2[0],  0, numColumns+3);
				
			numColumns = model.getColumnCount();
			if (typeSurr == -1) {// Double log plot data only without surrogates
				for (int x=0; x<lnDataX.size(); x++){
					model.addColumn("lnk"+(x+1));
					model.setValueAt(lnDataX.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int y=0; y<lnDataY.size(); y++){
					model.addColumn("lnL"+(y+1));
					model.setValueAt(lnDataY.get(y),  0, numColumns + y);
				}
			}
			if (typeSurr >= 0) {// DhMean and Dh for each surrogate 
				numColumns = model.getColumnCount();
				model.addColumn("Dh-Surr");
				model.addColumn("StdDev-Surr");
				model.addColumn("r2-Surr");
				model.addColumn("adjusted_r2-Surr");
				model.setValueAt(dimSurrMean,    0, numColumns);
				model.setValueAt(stdDevSurrMean, 0, numColumns+1);
				model.setValueAt(r2SurrMean,     0, numColumns+2);
				model.setValueAt(adjR2SurrMean,  0, numColumns+3);
				
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("Dh-Surr"+(x+1));
					model.setValueAt(dimSurr.get(x),  0, numColumns + x);
				}
			}
			
			
	
		}
		if (method == 1) { //gliding values	
			if (typeSurr == -1) {
				model.addColumn("Dh");
				model.addColumn("r2");
			}
			else {
				model.addColumn("Dh-Surr");
				model.addColumn("r2-Surr");
			}
			for (int i = 0; i < numDhValues; i++){		
				model.setValueAt(dim[i],    i, numColumns);
				model.setValueAt(r2[i],     i, numColumns+1);
			}
		}
		
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

		
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(model);
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new PlotOpFracHiguchiDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpFracHiguchiDescriptor.TYPE;
	}
	
}
