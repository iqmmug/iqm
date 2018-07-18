package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: AllometricScaling.java
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

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.commons.util.plot.PlotTools;


/**
 * Fractal Dimension using Allometric Scaling
 * 
 * @author Helmut Ahammer
 * @since   2013 09
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class AllometricScaling {

	
	@SuppressWarnings("unused")
	private AbstractOperator operator = null;
	private int progressBarMin = 0;
	private int progressBarMax = 100;
	
	
	public int getProgressBarMin() {
		return progressBarMin;
	}
	public void setProgressBarMin(int progressBarMin) {
		this.progressBarMin = progressBarMin;
	}
	public int getProgressBarMax() {
		return progressBarMax;
	}
	public void setProgressBarMax(int progressBarMax) {
		this.progressBarMax = progressBarMax;
	}
	
	/**
	 * This is the standard constructor with the possibility to import calling class
	 * This is useful for progress bar and canceling functionality
	 * 
	 * @param operator
	 *          
	 */
	public AllometricScaling (AbstractOperator operator) {
		this.operator = operator;
	}
	
	/**
	 * This is the standard constructor
	 */
	public AllometricScaling(){}
	
	/**
	 * This method calculates the mean of a series
	 * @param data a vector of doubles
	 * @return double
	 */
	private double calcMean(Vector<Double> data){
		double sum = 0.0;
		double mean = 0.0;
		for  (int i = 0; i < data.size(); i++){
			sum = sum + data.get(i);
		}
		mean = sum / data.size();
		return mean;
	}
	

	/**
	 * This method calculates the standard deviation of a series
	 * @param data a vector of doubles
	 * @param mean 
	 * @return double
	 */
	@SuppressWarnings("unused")
	private double calcStdDev(Vector<Double> data, double mean){
		double var = this.calcVariance(data, mean);
		return Math.sqrt(var);
	}
	
	/**
	 * This method calculates the variance of a series
	 * @param data the data 
	 * @param mean
	 * @return double
	 */
	private double calcVariance(Vector<Double> data, double mean){
		double sum = 0.0;
		for  (int i = 0; i < data.size(); i++){
			sum = sum + (data.get(i) - mean) * (data.get(i) - mean);
		}
		sum = sum / data.size();	
		return sum;
	}
	
	/**
	 * This method calculates the standard deviations and the means of the downscaled data series
	 * @param data 1D data vector
	 * @return Vector[] array of two vectors
	 */
	public Vector[] calcMeansAndVariances(Vector<Double> data){
	
		if (data.size() < 10){
			//BoardPanel.appendTextln("  ");
		}
		
		int N = 0;
		while (N+1 < data.size()/4){	
			N = N +1;
		}
		N= N-1;

		
		Vector[] asData = new  Vector[2];   //[0] means Vector    [1] standard deviations Vector
		Vector<Double>means       = new Vector<Double>();
		Vector<Double>variances   = new Vector<Double>();
		
		for(int f = 0; f < N; f++){ 
			//int numInt = (int)Math.pow(2, f);  //number of data points for interpolation
			int numInt = f+1;  //number of data points for interpolation
			//System.out.println("Allometric Scaling: f(N):" + f+"("+N+")" + "    numInt:"+numInt);
					
			Vector<Double> dataAggregated = new Vector<Double>();
			for (int i = 0; i <= data.size()-numInt; i = i+numInt){
				double sum = 0.0;
				for (int ii = i; ii < i + numInt; ii++){
					sum += data.get(ii);
				}
				//sum = sum/numInt;
				//for (int ii = i; ii < i + numInt; ii++){
					dataAggregated.add(sum);
				//}
			}		
			double mean  = this.calcMean(dataAggregated);	
			double var   = this.calcVariance(dataAggregated, mean);
			//means.add(mean);  //mean can be negative and that is a log problem 
			means.add((double)numInt);
			variances.add(var);	
			//System.out.println("Allometric Scaling:   mean:"+mean+"   variance:"+var);
					
		}	
	
		asData[0] = means;
		asData[1] = variances;	
		return asData;
	}

	/**
	 * 
	 * @param asData data pairs
	 * @param regStart 
	 * @param regEnd
	 * @param plotName the name
	 * @param showPlot 
	 * @param deleteExistingPlot
	 * @return double[] result 
	 */
	public double[] calcLogRegression(Vector[] asData, int regStart, int regEnd, String plotName, boolean showPlot, boolean deleteExistingPlot){

		
		if (regEnd > asData[0].size()){
			BoardPanel.appendTextln("AllometricScaling: regEnd > signal size, regEnd set to signal size");
			DialogUtil.getInstance().showDefaultInfoMessage("AllometricScaling: regEnd > signal size, regEnd set to signal size");
			regEnd = asData[0].size();
		}
		
		Vector<Double> lnDataX = new Vector<Double>();
		Vector<Double> lnDataY = new Vector<Double>();  
		for (int i = 0; i < asData[0].size(); i++){
			//System.out.println("Allometric Scaling: i:" + i + "  mean:"+ asData[0].get(i)+ "  variance: " + asData[1].get(i));
			if ((Double)asData[0].get(i) == 0) asData[0].set(i, Double.MIN_VALUE);
			if ((Double)asData[1].get(i) == 0) asData[1].set(i, Double.MIN_VALUE);
		}
		boolean NaNdetected = false;
		for (int i = 0; i < asData[0].size(); i++){
			double lnX = Math.log((Double) asData[0].get(i));
			double lnY = Math.log((Double) asData[1].get(i));
			if (Double.isNaN(lnX)){
				NaNdetected = true;
				BoardPanel.appendTextln("AllometricScaling: NaN value detected, maybe a mean is negative, which is not allowed for allometric scaling");
			}
			if (Double.isNaN(lnY)){
				NaNdetected = true;
				BoardPanel.appendTextln("AllometricScaling: NaN value detected, maybe standard deviation is negative, which is not allowed for allometric scaling");
			}	
			lnDataX.add(lnX);
			lnDataY.add(lnY);
		}
		if (NaNdetected) DialogUtil.getInstance().showDefaultErrorMessage("AllometricScaling: NaN value detected, maybe a mean is negative, which is not allowed for allometric scaling");

		
//		for (int i= 0; i < lnDataX.size(); i++){
//			System.out.println("Allometric Scaling: lnDataX.get(i):" + lnDataX.get(i) + "   lnDataY.get(i):" + lnDataY.get(i)); 
//		}
		//System.out.println("Allometric Scaling: regStart:"+regStart + "   regEnd:" + regEnd);
		double[] p = PlotTools.getLinearRegression(lnDataX, lnDataY, regStart, regEnd);

		if (deleteExistingPlot) {
			PlotTools.deleteExistingPlot();
		}
		
		if (showPlot) {
			boolean isLineVisible = false;
			PlotTools.displayRegressionPlotXY(lnDataX, lnDataY, isLineVisible, plotName, "Allometric Scaling", "ln(aggregation)", "ln(variance)", regStart, regEnd, deleteExistingPlot);
		}
	
		double[] out = {p[1], p[3], p[4], p[5]};  //slope, standard deviation, r2 , adjusted r2  
		return out;
	}
}
