package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFracHurst.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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
import at.mug.iqm.commons.util.plot.PlotTools;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFFTDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracHurstDescriptor;

/**
 * <li>Eke Herman Bassingthwaighte Raymond Percival Cannon Balla Ikrenyi 
 * "Physiological time series: distinguishing fractal noises from motion"
 * Eur.J.Physiol. 2000 439:403-415
 * 
 * @author Ahammer
 * @since   2012 12
 */
public class PlotOpFracHurst extends AbstractOperator{

	public PlotOpFracHurst() {
	}
	
	/**
	 * This method calculates the mean of a data series
	 * @param data1D
	 * @return mean 
	 * 
	 */
	public Double calcMean(Vector<Double> data1D){
		double sum = 0;
		for(double d: data1D){
			sum += d;
		}
		return sum/data1D.size();
	}
	/**
	 * This method calculates the variance of a data series
	 * @param data1D
	 * @return variance
	 */
	private double calcVariance(Vector<Double> data1D){
		double mean = calcMean(data1D);
		double sum = 0;
		for(double d: data1D){
			sum = sum + ((d - mean) * (d - mean));
		}
		return sum/(data1D.size()-1);  //  1/(n-1) is used by histo.getStandardDeviation() too
	}
	/**
	 * This method calculates the standard deviation of a data series
	 * @param data1D
	 * @return standard deviation
	 */
	private double calcStandardDeviation(Vector<Double> data1D){
		double variance  = this.calcVariance(data1D);
		return Math.sqrt(variance);
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
	
		boolean is_PSD_selected      = false;
		boolean is_lowPSDwe_selected = false;
		boolean is_SWV_selected      = false;
		boolean is_bdSWV_selected    = false;
		if (pb.getIntParameter("methodPSD") == 0)  is_PSD_selected      = true;	  
		if (pb.getIntParameter("methodPSD") == 1)  is_lowPSDwe_selected = true;	  
		if (pb.getIntParameter("methodSWV") == 0)  is_SWV_selected      = true;	  
		if (pb.getIntParameter("methodSWV") == 1)  is_bdSWV_selected    = true;	  
			
		int   regStart = pb.getIntParameter("regStart");		
		int   regEnd   = pb.getIntParameter("regEnd");		
	
		boolean showPlot           = false;
		boolean deleteExistingPlot = false;
		if (pb.getIntParameter("showPlot") == 1 )          showPlot = true;	  	
		if (pb.getIntParameter("deletePlot") == 1) deleteExistingPlot = true;	  	

		String plotModelName = plotModel.getModelName();
		
		//new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
		
		//eliminate mean
		double signalMean = this.calcMean(signal);
		for (int i = 0; i < signal.size(); i++){
			signal.set(i, signal.get(i) - signalMean);
		}
	
		double psd_Beta    = Double.NaN;
		double psd_H       = Double.NaN;
		double psd_stdDev  = Double.NaN;
		double psd_r2      = Double.NaN;
		double psd_adjR2   = Double.NaN;
		
		double ssc_Beta    = Double.NaN;
		double ssc_stdDev  = Double.NaN;
		double ssc_r2      = Double.NaN;
		double ssc_adjR2   = Double.NaN;
				
		
		double disp_Beta    = Double.NaN;
		double disp_H 		= Double.NaN;
		double disp_stdDev  = Double.NaN;
		double disp_r2      = Double.NaN;
		double disp_adjR2   = Double.NaN;
		
		double swv_Beta    = Double.NaN;
		double swv_H 	   = Double.NaN;
		double swv_stdDev  = Double.NaN;
		double swv_r2      = Double.NaN;
		double swv_adjR2   = Double.NaN;
		
		int windowing = PlotOpFFTDescriptor.WINDOWING_WITHOUT;
		
		double[] resultPSD = null;
	
		if (is_PSD_selected ){
			boolean onlyLowFrequ = false;
			resultPSD = this.calcBetaPSD(signal, windowing, regStart, regEnd, onlyLowFrequ, showPlot, deleteExistingPlot);
		}
		if (is_lowPSDwe_selected ){
			//apply parabolic windowing	
			Vector<Double> signalNew = this.calcParabolicWindowing(signal); 
			//apply bridge dentrending
			signalNew = this.calcBridgeDentrending(signalNew); 
			
			boolean onlyLowFrequ = true;
			resultPSD = this.calcBetaPSD(signalNew, windowing, regStart, regEnd, onlyLowFrequ, showPlot, deleteExistingPlot);
		}
		
		this.fireProgressChanged(50);
		if (this.isCancelled(this.getParentTask())) return null;
		
		psd_Beta    = -resultPSD[0];
		psd_stdDev  = resultPSD[1];
		psd_r2      = resultPSD[2];
		psd_adjR2   = resultPSD[3];
		
		
			
		boolean signalIsfGn     = false;
		boolean signalIsfBm     = false;
		boolean signalIsUnknown = true;
		String  signalType = "?";
		
		if (psd_Beta <= -1.2){
			BoardPanel.appendTextln("PlotOpFracHurst: signal type is wether fGn nor fBm");
		}
		if (psd_Beta >=3.2){
			BoardPanel.appendTextln("PlotOpFracHurst: signal type is wether fGn nor fBm");
		}
		
		if ((psd_Beta <= 0.38) && (psd_Beta >-1.2)){ //fGn
			BoardPanel.appendTextln("PlotOpFracHurst: fGn signal detected");
			signalIsfGn     = true;
			signalIsfBm     = false;	
			signalIsUnknown = false;
			signalType = "fGn";
		}
		if ((psd_Beta >= 1.04) && (psd_Beta < 3.2)){ //fBm
			BoardPanel.appendTextln("PlotOpFracHurst: fBm signal detected");
			signalIsfGn     = false;
			signalIsfBm     = true;	
			signalIsUnknown = false;
			signalType = "fBm";
		}	
		if ((psd_Beta > 0.38) && (psd_Beta < 1.04)){ //signal between fGn and fBm
			BoardPanel.appendTextln("PlotOpFracHurst: signal type could not be detected, starting SSC method");
			//signal summation conversion (fGn to fBm, fBm to summed fBm)
			Vector<Double> signalSSC = new Vector<Double>();
			signalSSC.add(signal.get(0));		
			for (int i = 1; i < signal.size(); i++){
				signalSSC.add(signalSSC.get(i-1) + signal.get(i));
			}
			double[] resultSSC = this.calcBetaSWVH(signalSSC, showPlot, deleteExistingPlot);
			ssc_Beta    = -resultSSC[0];
			ssc_stdDev  = resultSSC[1];
			ssc_r2      = resultSSC[2];
			ssc_adjR2   = resultSSC[3];
			if (ssc_Beta <= 0.6){ //fGn
				BoardPanel.appendTextln("PlotOpFracHurst: fGn signal after SSC detected");
				signalIsfGn     = true;
				signalIsfBm     = false;	
				signalIsUnknown = false;
				signalType = "fGn";
			}
			if (ssc_Beta >= 1.0){ //fBm
				BoardPanel.appendTextln("PlotOpFracHurst: fBm signal after SSC detected");
				signalIsfGn     = false;
				signalIsfBm     = true;	
				signalIsUnknown = false;
				signalType = "fBm";
			}	
			if ((ssc_Beta > 0.6) && (ssc_Beta < 1.0)){ //not clear
				BoardPanel.appendTextln("PlotOpFracHurst: signal type could not be detected, even using SSC");
				signalIsfGn     = false;
				signalIsfBm     = false;	
				signalIsUnknown = true;
				signalType = "?";
			}
		
		}
		if (signalIsfGn){ //fGn
			psd_H = (psd_Beta + 1)/2;
			double[] resultDispH = this.calcBetaDispH(signal, showPlot, deleteExistingPlot);
			disp_Beta 	 = resultDispH[0];
			disp_H		 = 1 + resultDispH[0];	
			disp_stdDev  = resultDispH[1];
			disp_r2      = resultDispH[2];
			disp_adjR2   = resultDispH[3];
		}
		if (signalIsfBm){ //fBm
			psd_H = (psd_Beta - 1)/2;
			double[] resultSWVH = null;
			 if (is_SWV_selected){
				 resultSWVH	= this.calcBetaSWVH(signal, showPlot, deleteExistingPlot);
			 }
			 if (is_bdSWV_selected){
				 Vector<Double> signalNew = this.calcBridgeDentrending(signal); 
				 resultSWVH	= this.calcBetaSWVH(signalNew, showPlot, deleteExistingPlot);
			 }
		
			swv_Beta 	= resultSWVH[0];
			swv_H		= resultSWVH[0];	
			swv_stdDev  = resultSWVH[1];
			swv_r2      = resultSWVH[2];
			swv_adjR2   = resultSWVH[3];
		}
		this.fireProgressChanged(90);
		if (this.isCancelled(this.getParentTask())) return null;
		//create model 
		TableModel model = new TableModel("Hurst");
	
		model.addColumn("Plot");
		model.addColumn("RegStart");
		model.addColumn("RegEnd");
		model.addRow(new String[] {plotModelName, String.valueOf(regStart), String.valueOf(regEnd) });

		int numColumns = model.getColumnCount();
		
		if (is_PSD_selected){
			model.addColumn("Type");	
			model.addColumn("PSD_Beta");
			model.addColumn("PSD_H");		
			model.addColumn("PSD_StdDev");
			model.addColumn("PSD_r2");
			model.addColumn("PSD_adjusted_r2");
		}
		if (is_lowPSDwe_selected){
			model.addColumn("Type");	
			model.addColumn("lowPSDwe_Beta");
			model.addColumn("lowPSDwe_H");	
			model.addColumn("lowPSDwe_StdDev");
			model.addColumn("lowPSDwe_r2");
			model.addColumn("lowPSDwe_adjusted_r2");
		}

		model.addColumn("SSC_Beta");
		model.addColumn("SSC_StdDev");
		model.addColumn("SSC_r2");
		model.addColumn("SSC_adjusted_r2");
		
		model.addColumn("Disp_Beta");	
		model.addColumn("Disp_H");
		model.addColumn("Disp_StdDev");
		model.addColumn("Disp_r2");
		model.addColumn("Disp_adjusted_r2");
		
		if (is_SWV_selected){
			model.addColumn("SWV_Beta");	
			model.addColumn("SWV_H");
			model.addColumn("SWV_StdDev");
			model.addColumn("SWV_r2");
			model.addColumn("SWV_adjusted_r2");
		}
		if (is_bdSWV_selected){
			model.addColumn("bdSWV_Beta");	
			model.addColumn("bdSWV_H");
			model.addColumn("bdSWV_StdDev");
			model.addColumn("bdSWV_r2");
			model.addColumn("bdSWV_adjusted_r2");
		}
		model.setValueAt(signalType, 0, numColumns);
		model.setValueAt(psd_Beta,   0, numColumns+1);
		model.setValueAt(psd_H,      0, numColumns+2);
		model.setValueAt(psd_stdDev, 0, numColumns+3);
		model.setValueAt(psd_r2,     0, numColumns+4);
		model.setValueAt(psd_adjR2,  0, numColumns+5);
	
		model.setValueAt(ssc_Beta,   0, numColumns+6);
		model.setValueAt(ssc_stdDev, 0, numColumns+7);
		model.setValueAt(ssc_r2,     0, numColumns+8);
		model.setValueAt(ssc_adjR2,  0, numColumns+9);
	
		
		model.setValueAt(disp_Beta,   0, numColumns+10);
		model.setValueAt(disp_H,      0, numColumns+11);
		model.setValueAt(disp_stdDev, 0, numColumns+12);
		model.setValueAt(disp_r2,     0, numColumns+13);
		model.setValueAt(disp_adjR2,  0, numColumns+14);
	
		model.setValueAt(swv_Beta,   0, numColumns+15);
		model.setValueAt(swv_H,      0, numColumns+16);
		model.setValueAt(swv_stdDev, 0, numColumns+17);
		model.setValueAt(swv_r2,     0, numColumns+18);
		model.setValueAt(swv_adjR2,  0, numColumns+19);
	
//		//format column widths
//		numColumns = model.getColumnCount();
//		for (int i = 0; i < numColumns; i++){
//			//jTable.getColumnModel().getColumn(i).setPreferredWidth(250);
//			if ((model.getColumnName(i) == "RegStart") ||
//				(model.getColumnName(i) == "RegEnd")) {
//				jTable.getColumnModel().getColumn(i).setPreferredWidth(40);
//			}
////			if ((model.getColumnName(i) == "Beta")){
////				jTable.getColumnModel().getColumn(i).setPreferredWidth(40);
////			}
//		}
		
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(model);
	}


	/**
	 * This Method applies bridge dentrending (end matching)
	 * subtraction of a line connecting the first and the last point
	 * @param signal
	 * @return bridge dentrending as {@link Vector} of doubles
	 */
	private Vector<Double> calcBridgeDentrending(Vector<Double> signal) {
		Vector<Double> signalBD = new Vector<Double>();
		double first = signal.get(0);
		double last = signal.lastElement();
		double step = (last - first)/(signal.size() - 1);
		for  (int i = 0; i < signal.size(); i++){
			double lineValue = first + (i * step);
			signalBD.add(signal.get(i)- lineValue);
		}
		return signalBD;
	}

	/**
	 * This method applies parabolic windowing
	 * according to Equ6 Eke etal 2000
	 * @param signal
	 * @return the parabolic windowing of the signal
	 */
	private Vector<Double> calcParabolicWindowing(Vector<Double> signal) {
		Vector<Double> signalW = new Vector<Double>();
		for  (int j = 0; j < signal.size(); j++){
			double b = (2.0d*(j+1))/(signal.size()+1) - 1;
			double parabWin = 1.0d - b*b;
			signalW.add(signal.get(j)*parabWin);
		}
		return signalW;
	}

	private double[] calcBetaPSD(Vector<Double> signal, int windowing, int regStart, int regEnd, boolean onlyLowFrequ,  boolean showPlot, boolean deleteExistingPlot) {
	
//		FourierTransform ft = new FourierTransform();		
//		if (windowing == PlotGUI_FFT.WINDOWING_WITHOUT){	
//			ft.removeWindow();
//		}	
//		if (windowing == PlotGUI_FFT.WINDOWING_RECTANGULAR){ 	
//			ft.setRectangular();
//		}
//		if (windowing == PlotGUI_FFT.WINDOWING_BARTLETT){ 	
//			ft.setBartlett();
//		}
//		if (windowing == PlotGUI_FFT.WINDOWING_WELCH){ 	
//			ft.setWelch();
//		}
//		if (windowing == PlotGUI_FFT.WINDOWING_HANN){ 	
//			ft.setHann();
//		}
//		if (windowing == PlotGUI_FFT.WINDOWING_HAMMING){ 	
//			ft.setHamming();
//		}
//		if (windowing == PlotGUI_FFT.WINDOWING_KAISER){ 	
//			ft.setKaiser();
//		}
//		if (windowing == PlotGUI_FFT.WINDOWING_GAUSSIAN){ 	
//			ft.setGaussian();
//		}		
//		 //data length must not have a power of 2 because .powerSpectrum() does automatic zero padding 
//		 double[] data = new double[signal.size()];
//		 //set data
//		 for (int i = 0; i < signal.size(); i++){
//				 data[i] = signal.get(i);
//	  	 }	
//		 ft.setData(data);
//		 //ft.transform();
//		 double[][] ps = ft.powerSpectrum();
////		 for (int i = 0; i < ps[0].length; i++){
////			signalPS.add(ps[1][i]);  //Power Spectrum
////			rangePS.add(ps[0][i]);   //frequencies
////		 }
//				
//		String plotName = "Plot";
//
//		Vector<Double> lnDataY = new Vector<Double>();
//		Vector<Double> lnDataX = new Vector<Double>();  
////		for (int i = 0; i < signal.size(); i++){
////			if (signal.get(i) == 0) signal.set(i, Double.MIN_VALUE);
////		}
//		//avoid zeros
//		for (int i = 0; i < ps[1].length; i++){
//			//if (ps[0][i] == 0) ps[0][i] = Double.MIN_VALUE;
//			//if (ps[1][i] == 0) ps[1][i] = Double.MIN_VALUE;
//			//ps[0][i] = ps[0][i] + 1;
//			//ps[1][i] = ps[1][i] + 1;
//		}
//		//check endpoint of regression
//		if (regEnd >  ps[1].length){
//			regEnd  = ps[1].length;
//		}	
//		if (onlyLowFrequ){
//			regStart = ps[1].length/8;
//			regEnd   = ps[1].length/2;
//		}		
//		for (int i = 0; i < ps[0].length; i++){ 
//			lnDataX.add(Math.log(i+1));
//			lnDataY.add(Math.log(ps[1][i]));
//		}
		
	
		Vector<Double> signalPower = new Vector<Double>();
		signalPower = this.calcDFTPower(signal);
		//signalPower.remove(0);
	
		String plotName = "Plot";
		Vector<Double> lnDataY = new Vector<Double>();
		Vector<Double> lnDataX = new Vector<Double>();  

		//avoid zeros
		for (int i = 0; i < signalPower.size(); i++){
			//if (signalPower.get(i) == 0) signalPower.set(i, Double.MIN_VALUE);
		}
			
		//check endpoint of regression
		if (regEnd > signalPower.size()){
			regEnd = signalPower.size();
		}		
		if (onlyLowFrequ){
			regStart = signalPower.size()/8;
			regEnd   = signalPower.size()/2;
		}		
		for (int i = 0; i < signalPower.size(); i++){
			lnDataX.add(Math.log(i+1));
			//lnDataY.add(Math.log(signalPower.get(i)) /Math.log(signalPower.get(0)));
			lnDataY.add(Math.log(signalPower.get(i)));
		}
		
		
		double[] p = PlotTools.getLinearRegression(lnDataX, lnDataY, regStart, regEnd);
		if (showPlot) {
			boolean isLineVisible = false;
			PlotTools.displayRegressionPlotXY(lnDataX, lnDataY, isLineVisible, plotName, "Power Spectral Densitiy", "ln(k)", "ln(P)", regStart, regEnd, deleteExistingPlot);
		}				
		double[] result = {p[1], p[3], p[4], p[5]};  // slope, standard deviation, r2 , adjusted r2  

		BoardPanel.appendTextln("IqmOpFracHurst  Beta:" + (-result[0])+ " StdDev:" + result[1] + " r2:" + result[2] + " adj.r2:"+result[3]);
//		beta           = -result[0];
//		double stdDev  = result[1];
//		double r2      = result[2];
//		double adjR2   = result[3];
		
		return result;
	}

	

	/**
	 * This method calculates the power spectrum of the DFT.
	 * @param signal
	 * @return the DFT power spectrum
	 */
	private Vector<Double> calcDFTPower(Vector<Double> signal) {
		Vector<Double> signalPower = new Vector<Double>();
		int N = signal.size();
		for (int k = 0; k < N/2; k++) { //N/2 because spectrum is symmetric
			double sumReal = 0;
			double sumImag = 0;
			for (int n = 0; n < N; n++) { //input points
				double cos = Math.cos(2*Math.PI * n * k / N);
				double sin = Math.sin(2*Math.PI * n * k / N);		
				sumReal +=  signal.get(n) * cos;
				sumImag += -signal.get(n) * sin;
			}
			signalPower.add(sumReal*sumReal+sumImag*sumImag);
		}
		return signalPower;
	}
	
	
	/**
	 * This method calculates H using dispersional analysis according to Eke et al.
	 * Standard Deviations of local averages
	 */
	@SuppressWarnings("unused")
	private double[] calcBetaDispH(Vector<Double> signal, boolean showPlot, boolean deleteExistingPlot) {
	
		int length = signal.size();
		
		Vector<Double> dataY = new Vector<Double>();
		Vector<Double> dataX = new Vector<Double>();  
	

		int winSize = 3; //initial window size
	    while ((signal.size() / winSize) > 1){ // at least two big windows
	    	Vector<Double> meanVec = new Vector<Double>();
	    	int w = 0; 	
	    	while (w <= (signal.size() - winSize)){   //scroll through signal with windows
	    		double winMean = 0.0d;
	    		for (int i = w; i < w + winSize; i++){ //scroll through data points of a single window
	    			winMean = winMean +signal.get(i);
	    		}
	    		winMean = winMean/winSize;
	    		meanVec.add(winMean);  // add local mean
	    		w = w + winSize;
	    	}
	    	//calculate SD and set data values:
	    	dataY.add(this.calcStandardDeviation(meanVec));
	    	dataX.add((double)winSize);
	    	winSize = winSize*2;	
		}
	    
	    //calculate logarithm of normalized data
		Vector<Double> lnDataY = new Vector<Double>();
		Vector<Double> lnDataX = new Vector<Double>();  //k
	
		for (int i = 0; i < dataY.size(); i++){
			if (dataY.get(i) == 0) dataY.set(i, Double.MIN_VALUE);
		}
		for (int i = 0; i < dataY.size(); i++){
			lnDataX.add(Math.log(dataX.get(i).doubleValue()/dataX.get(0).doubleValue()));
			lnDataY.add(Math.log(dataY.get(i).doubleValue()/dataY.get(0).doubleValue()));
			//lnDataX.add(Math.log(dataX.get(i).doubleValue()));
			//lnDataY.add(Math.log(dataY.get(i).doubleValue()));
		}
		int regStart = 1;
		int regEnd = lnDataY.size();
		double[] p = PlotTools.getLinearRegression(lnDataX, lnDataY, regStart, regEnd);

	
		String plotName = "Dispersional plot for H fGn";
		if (showPlot) {
			boolean isLineVisible = false;
	
			PlotTools.displayRegressionPlotXY(lnDataX, lnDataY, isLineVisible, plotName, "Disp H", "ln(winSize/winSize0)", "ln(SD/SD0)", regStart, regEnd, deleteExistingPlot);
		}				
		double[] result = {p[1], p[3], p[4], p[5]};  //slope, standard deviation, r2 , adjusted r2  
		
		//dispH = 1+ p[1];
		 
		return result;
	}

	/**
	 * This method calculates H using scaled window variance according to Eke et al.
	 * Average of local Standard Deviations 
	 */
	@SuppressWarnings("unused")
	private double[] calcBetaSWVH(Vector<Double> signal, boolean showPlot, boolean deleteExistingPlot) {
		
		int length = signal.size();
		
		Vector<Double> dataY = new Vector<Double>();
		Vector<Double> dataX = new Vector<Double>();  
	
		int winSize = 3; //initial window size
	    while ((signal.size() / winSize) > 1){ // at least two big windows
	    	Vector<Double> sdtDevVec = new Vector<Double>();
	    	int w = 0; 	
	    	while (w <= (signal.size() - winSize)){   //scroll through signal with windows
	    		double winMean = 0.0d;
	    		for (int i = w; i < w + winSize; i++){ //scroll through data points of a single window
	    			winMean = winMean +signal.get(i);
	    		}
	    		winMean = winMean/winSize;
	    		double winSdtDev = 0;
	    		for (int i = w; i < w + winSize; i++){ //scroll through data points of a single window
	    			winSdtDev = winSdtDev + ((signal.get(i) - winMean) * (signal.get(i) - winMean));  			
	    		}
	    		winSdtDev = winSdtDev/winSize;
	    		winSdtDev = Math.sqrt(winSdtDev);
	    		sdtDevVec.add(winSdtDev);  // add local mean
	    		w = w + winSize;
	    	}
	    	//calculate mean and set data values:
	    	dataY.add(this.calcMean(sdtDevVec));
	    	dataX.add((double)winSize);
	    	winSize = winSize*2;	
		}
	    
	    //calculate logarithm of normalized data
		Vector<Double> lnDataY = new Vector<Double>();
		Vector<Double> lnDataX = new Vector<Double>();  //k
	
		for (int i = 0; i < dataY.size(); i++){
			if (dataY.get(i) == 0) dataY.set(i, Double.MIN_VALUE);
		}
		for (int i = 0; i < dataY.size(); i++){
			lnDataX.add(Math.log(dataX.get(i).doubleValue()/dataX.get(0).doubleValue()));
			lnDataY.add(Math.log(dataY.get(i).doubleValue()/dataY.get(0).doubleValue()));
			//lnDataX.add(Math.log(dataX.get(i).doubleValue()));
			//lnDataY.add(Math.log(dataY.get(i).doubleValue()));
		}
		int regStart = 1;
		int regEnd = lnDataY.size();
		double[] p = PlotTools.getLinearRegression(lnDataX, lnDataY, regStart, regEnd);

		String plotName = "Scaled window variance for H fBm";
		if (showPlot) {
			boolean isLineVisible = false;
		
			PlotTools.displayRegressionPlotXY(lnDataX, lnDataY, isLineVisible, plotName, "SWV H", "ln(winSize/winSize0)", "ln(SD/SD0)", regStart, regEnd, deleteExistingPlot);
		}				
		double[] result = {p[1], p[3], p[4], p[5]};  //slope, standard deviation, r2 , adjusted r2  
		
		//swvH = p[1];
		
		return result;
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new PlotOpFracHurstDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpFracHurstDescriptor.TYPE;
	}
}
