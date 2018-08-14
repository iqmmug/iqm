package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: DFA.java
 * 
 * $Id: DFA.java 619 2017-11-02 18:46:37Z kainzp $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-api/src/main/java/at/mug/iqm/commons/util/DFA.java $
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

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.commons.util.plot.PlotTools;

/**
 * 
 * @author Helmut Ahammer
 * @since 2018-04-13 DFA according to Peng etal 1994 and e.g. Hardstone etal. 2012
 */
public class DFA {

	private AbstractOperator operator = null;
	private int progressBarMin = 0;
	private int progressBarMax = 100;
	private Vector<Double> lnDataX;
	private Vector<Double> lnDataY;

	public Vector<Double> getLnDataX() {
		return lnDataX;
	}

	public void setLnDataX(Vector<Double> lnDataX) {
		this.lnDataX = lnDataX;
	}

	public Vector<Double> getLnDataY() {
		return lnDataY;
	}

	public void setLnDataY(Vector<Double> lnDataY) {
		this.lnDataY = lnDataY;
	}

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
	 * This is the standard constructor with the possibility to import calling
	 * class This is useful for progress bar and canceling functionality
	 * 
	 * @param operator
	 * 
	 */
	public DFA(AbstractOperator operator) {
		this.operator = operator;
	}

	/**
	 * This is the standard constructor
	 */
	public DFA() {

	}

	/**
	 * This method calculates the "Fluctuation function" of the series
	 * 
	 * @param data  1D data vector
	 * @param winSize
	 *            winSize must be smaller than the total number of time points
	 *            winSize should not be greater than N/3 (N number of data points)!
	 * @return Vector F[winSize] "Fluctuation functions"
	 */
	public Vector<Double> calcFluctuationFunctions(Vector<Double> data, int winSize) {
		int N = data.size();
		if (winSize > N) {
			winSize = N / 3;
			BoardPanel.appendTextln("DFA parameter winSize too large, automatically set to data length/3");
		}
		
		//Mean of data series
		double meanData = 0.0;
		for (int i = 0; i < data.size(); i++) {
			meanData = meanData + data.get(i);
		}
		
		//Cumulative sum of data series
		for (int i = 1; i < data.size(); i++) {
			data.set(i, data.get(i-1) + (data.get(i) - meanData));
		}
		
		
		// double[] F = new double[winSize];
		Vector<Double> F = new Vector<Double>();
		F.add(0, Double.MIN_VALUE);
		F.add(1, Double.MIN_VALUE);
		F.add(2, Double.MIN_VALUE);  //will be hopefully eliminated later on
		
		for (int winLength = 4; winLength <= winSize; winLength++) {// windows with size 1,2, and 3 should not be used according to Peng et al.
			Integer numWin = (int)Math.floor((double)(data.size()/(double)winLength));	
			double[] flucWin = new double[numWin];
			
			for (int w = 1; w <= numWin; w++) {
				//Extract data points with length winLength
				double[] regDataY = new double[winLength];
				double[] regDataX = new double[winLength];
				int startIndex = (w-1) * winLength;  
				int endIndex   = startIndex + winLength -1 ;		
				for (int i = startIndex; i <= endIndex; i++) {
					regDataY[i-startIndex] = data.get(i);
					regDataX[i-startIndex] = i-startIndex +1; 
				}
					
				//Compute fluctuation of segment
				flanagan.analysis.Regression reg = new flanagan.analysis.Regression(regDataX, regDataY);
				reg.linear();

				//reg.linearPlot();
//				double[] coef    = reg.getCoeff();
//				double[] coefSd  = reg.getCoeffSd();
//				double[] coefVar = reg.getCoeffVar();
//				double adjR   = reg.getAdjustedR();
//				double adjR2  = reg.getAdjustedR2();
//				double sampR  = reg.getSampleR();
//				double sampR2 = reg.getSampleR2();
//				double chi2   = reg.getChiSquare(); 
							
						     						   //See Wikipedia
				double[]residuals = reg.getResiduals();//Simply the differences of the data y values and the computed regression y values.
			
				double rms = 0.0;		
				for (int y = 0; y < residuals.length; y++) {
					rms = rms + (residuals[y]*residuals[y]);
				}
				rms = rms/residuals.length;
				rms = Math.sqrt(rms);
				flucWin[w-1] = rms;			
			}
			//Mean fluctuation for one window size:
			double meanF = 0.0;
			for (int w = 1; w <= numWin; w++) {
				meanF = meanF + flucWin[w-1];
			}			
			meanF = meanF / numWin;

			F.add(winLength-1, meanF);
	
			if (operator != null) {
				this.operator.fireProgressChanged(winLength * (progressBarMax - progressBarMin) / winSize + progressBarMin);
				if (this.operator.isCancelled(this.operator.getParentTask()))
					return null;
			}
		}
		//F.remove(0);  // does not work because of linear regression later on
		//Set first three values to the 4th with is really computed
		F.set(0, F.get(3));
		F.set(1, F.get(3));	
		F.set(2, F.get(3));
		return F;
	}

	/**
	 * 
	 * @param F   DFA Fluctuation function
	 * @param regStart
	 * @param regEnd
	 * @return double alpha
	 */
	public double[] calcAlphas(Vector<Double> F, int regStart, int regEnd, String plotName, boolean showPlot,
			boolean deleteExistingPlot) {
		// double[] lnDataY = new double[L.size()];
		// double[] lnDataX = new double[L.size()]; //k
		lnDataY = new Vector<Double>();
		lnDataX = new Vector<Double>(); // k
		for (int i = 0; i < F.size(); i++) {
			if (F.get(i) == 0)
				F.set(i, Double.MIN_VALUE);
		}
		// System.out.println("DFA: lnk ln(L)");
		BoardPanel.appendTextln("DFA: lnk   ln(F)"); 
		for (int i = 0; i < F.size(); i++) {
			double lnX = Math.log(i + 1);
			double lnY = Math.log(F.get(i).doubleValue());
			lnDataX.add(lnX);
			lnDataY.add(lnY);
			// System.out.println(lnX + " " + lnY);
			BoardPanel.appendTextln(lnX + "        " + lnY); 
		}
		double[] p = PlotTools.getLinearRegression(lnDataX, lnDataY, regStart, regEnd);

		if (deleteExistingPlot) {
			PlotTools.deleteExistingPlot();
		}

		if (showPlot) {
			boolean isLineVisible = false;
			PlotTools.displayRegressionPlotXY(lnDataX, lnDataY, isLineVisible, plotName, "DFA Alpha", "ln(winSize)",
					"ln(F)", regStart, regEnd, deleteExistingPlot);
		}

		double[] out = {p[1], p[3], p[4], p[5] }; // -slope = alpha, SD, r2 , adjusted r2
		return out;
	}

}// END
