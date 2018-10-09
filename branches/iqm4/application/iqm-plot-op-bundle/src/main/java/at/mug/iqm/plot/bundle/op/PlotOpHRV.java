package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpHRV.java
 * 
 * $Id: PlotOpHRV.java 505 2015-01-09 09:19:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-plot-op-bundle/src/main/java/at/mug/iqm/plot/bundle/op/PlotOpHRV.java $
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
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpHRVDescriptor;


/**
 *  <li>2018 10 
 *  according to
 *  Heart rate variability Standards of measurement, physiological interpretation, and clinical use
 *  Task Force of The European Society of Cardiology and The North American
 *  Society of Pacing and Electrophysiology (Membership of the Task Force listed in the Appendix)
 *  Malik et al.
 *  European Heart Journal (1996) 17,354-381
 *  
 *  and
 *  McNames & Aboy Med Bio Eng Comput (2006) 44:747–756)
 *    
 * From intervals:              
 * SDNN, the standard deviation of NN intervals. Often calculated over a 24-hour period.
 * SDANN, the standard deviation of the average NN intervals calculated over short periods, usually 5 minutes.
 * SDNN is therefore a measure of changes in heart rate due to cycles longer than 5 minutes. SDNN reflects all the cyclic components responsible for variability in the period of recording, therefore it represents total variability.
 * SDNNIndex is the mean of the 5-min standard deviations of the NN interval calculated over 24 h, which measures the variability due to cycles shorter than 5 min.
 * HRVTI The HRV triangular index is a measure of the shape of the NN interval distribution. Generally, uniform distributions representing large variability have large values and distributions with single large peaks have small values. The metric is defined in terms of a histogram of the NN intervals.
 * 
 * From Interval (absolute) differences:
 * RMSSD ("root mean square of successive differences"), the square root of the mean of the squares of the successive differences between adjacent NNs.
 * SDSD ("standard deviation of successive differences"), the standard deviation of the successive differences between adjacent NNs.
 * NN50, the number of pairs of successive NNs that differ by more than 50 ms.
 * pNN50, the proportion of NN50 divided by total number of NNs.
 * NN20, the number of pairs of successive NNs that differ by more than 20 ms.
 * pNN20, the proportion of NN20 divided by total number of NNs.
 * 
 * @author Ahammer
 * @since  2018 10
 * 
 */
public class PlotOpHRV extends AbstractOperator {

	public PlotOpHRV() {
	}

	/**
	 * This method calculates the mean of a data series
	 * 
	 * @param data1D
	 * @return Double Mean
	 */
	private Double calcMean(Vector<Double> data1D) {
		double sum = 0.0;
		for (double d : data1D) {
			sum += d;
		}
		return sum / data1D.size();
	}
	
	/**
	 * This method calculates the SDNN (simply the SD)
	 * This is simply the Standard Deviation
	 * 
	 * @param data1D, mean
	 * @return Double 
	 */
	private Double calcSDNN(Vector<Double> data1D, Double mean) {
		double sdnn = 0.0;
		for (double d : data1D) {
			sdnn += Math.pow(d-mean, 2.0);
		}
		return Math.sqrt(sdnn/(data1D.size()-1));
	}
	/**
	 * This method calculates the SDANN  (SD over 5 minute means)
	 * 
	 * @param data1D, timeBase
	 * @return Double 
	 */
	private Double calcSDANN(Vector<Double> data1D, int timeBase) {
		double sdann = 0.0;	
		double fiveMinutes = 5.0;
		if (timeBase == 0) fiveMinutes = fiveMinutes * 60.0 *1000.0;  //ms
		if (timeBase == 1) fiveMinutes = fiveMinutes * 60.0 *1.0;     //s
		Vector<Double> fiveMinutesMeans  = new Vector<Double>();
		Vector<Double> fiveMinutesSignal = new Vector<Double>();
		double sumOfSubsequentIntervals = 0.0;
		
		for (int i = 0; i < data1D.size(); i++) { // scroll through intervals		
			fiveMinutesSignal.add(data1D.get(i));
			sumOfSubsequentIntervals += data1D.get(i);
			
			if (sumOfSubsequentIntervals >= fiveMinutes) {
				fiveMinutesMeans.add(calcMean(fiveMinutesSignal));
				fiveMinutesSignal = new Vector<Double>();
				sumOfSubsequentIntervals = 0.0;
			}			
		}
		Double mean = calcMean(fiveMinutesMeans);
		sdann = calcSDNN(fiveMinutesMeans, mean); 
		return sdann;
	}
	/**
	 * This method calculates the SDNNI (Mean over 5 minute SDs)
	 * 
	 * @param data1D, timeBase
	 * @return Double 
	 */
	private Double calcSDNNI(Vector<Double> data1D , int timeBase) {
		double sdnni = 0.0;
		double fiveMinutes = 5.0;
		if (timeBase == 0) fiveMinutes = fiveMinutes * 60.0 *1000.0;  //ms
		if (timeBase == 1) fiveMinutes = fiveMinutes * 60.0 *1.0;     //s
		Vector<Double> fiveMinutesSDs  = new Vector<Double>();	
		Vector<Double> fiveMinutesSignal = new Vector<Double>();
		double sumOfSubsequentIntervals = 0.0;
		
		for (int i = 0; i < data1D.size(); i++) { // scroll through intervals		
			fiveMinutesSignal.add(data1D.get(i));
			sumOfSubsequentIntervals += data1D.get(i);
			
			if (sumOfSubsequentIntervals >= fiveMinutes) {				
				double mean = calcMean(fiveMinutesSignal); 
				fiveMinutesSDs.add(calcSDNN(fiveMinutesSignal, mean));
				fiveMinutesSignal = new Vector<Double>();
				sumOfSubsequentIntervals = 0.0;
			}			
		}	
		sdnni =  calcMean(fiveMinutesSDs);
		return sdnni;
	}
	
	/**
	 * This method calculates the HRVTI (HRV triangular index) with a histogram
	 * according to McNames & Aboy Med Bio Eng Comput (2006) 44:747–756)
	 * 
	 * @param data1D
	 * @return Double 
	 */
	private Double calcHRVTI(Vector<Double> data1D, int timeBase) {
		double hrvti    = 0.0;
		double histoMax = 0.0;
		double binSize = 8; //ms  8ms according to McNames & Aboy Med Bio Eng Comput (2006) 44:747–756)
		
		if (timeBase == 0) binSize = binSize; //ms
		if (timeBase == 1) binSize = binSize/1000.0; //s
		
		
		// create a histogram with bin size
		double maxValue =  0.0;
		for (double v : data1D) {
			if (v > maxValue) maxValue = v;
		}		
		int binNumber = (int) Math.ceil(maxValue/binSize);
		Vector<Integer> histo = new Vector<Integer>();
		for (int b = 0; b < binNumber; b++) histo.add(0);
				
		for (int i = 0; i < data1D.size(); i++) { // scroll through intervals	
			int index = (int) Math.ceil(data1D.get(i)/binSize);
			histo.set(index-1, histo.get(index-1) + 1);		
		}
		//search for maximum in the histogram
		for (double h : histo) {
			//System.out.println("PlotOpHRV: Histogram  h: " + h);
			if (h > histoMax) histoMax = h;
		}
		//System.out.println("PlotOpHRV: histoMax: " + histoMax);
		hrvti = data1D.size()/histoMax;
		return hrvti;
	}
	
	
	/**
	 * This method computes the differences of subsequent data values
	 *  *
	 * @param data1D
	 * @return Vector<Double>
	 */
	private Vector<Double> getAbsDiffSignal(Vector<Double> data1D){
		Vector<Double> diffData1D = new Vector<Double>();
		for (int i = 0; i < data1D.size()-1; i++){	
			diffData1D.add(Math.abs((data1D.get(i+1) - data1D.get(i))));
		}
		return diffData1D;
	}
	
	/**
	 * This method calculates the RMSSD (root of mean squared interval differences)
	 * 
	 * @param diffData1D
	 * @return Double 
	 */
	private Double calcRMSSD(Vector<Double> diffData1D) {
		double  rmssd= 0.0;
		for (double d : diffData1D) {
			rmssd += Math.pow(d, 2.0);
		}
		return Math.sqrt(rmssd/(diffData1D.size()));
	}
	/**
	 * This method calculates the SDSD (SD of interval differences)
	 * 
	 * @param diffData1D
	 * @return Double 
	 */
	private Double calcSDSD(Vector<Double> diffData1D ) {
		double sdsd = 0.0;
		Double mean = calcMean(diffData1D);
		return calcSDNN(diffData1D, mean);
	}
	/**
	 * This method calculates the NN50 (number of interval differences of successive NN intervals greater than 50ms)
	 * 
	 * @param diffData1D, timeBase
	 * @return Double 
	 */
	private Double calcNN50(Vector<Double> diffData1D, int timeBase) {
		double nn50 = 0.0;
		double ms50 = 50.0; //ms
		if (timeBase == 0) // ms do nothing 
		if (timeBase == 1) ms50 = ms50/1000.0;  //s
		for (double d : diffData1D) {
			if (d > ms50) nn50 += 1;
		}
		return nn50;
	}
	/**
	 * This method calculates the NN20 (number of interval differences of successive NN intervals greater than 20 ms)
	 * 
	 * @param diffData1D
	 * @return Double 
	 */
	private Double calcNN20(Vector<Double> diffData1D, int timeBase) {
		double nn20 = 0.0;
		double ms20 = 20.0; //ms
		if (timeBase == 0) // ms do nothing 
		if (timeBase == 1) ms20 = ms20/1000.0;  //s
		for (double d : diffData1D) {
			if (d > ms20) nn20 += 1;
		}
		return nn20;
	}
	
    //---------------------------------------------------------------------------------------------------------------
	@Override
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlotModel plotModel  = ((IqmDataBox) pb.getSource(0)).getPlotModel();
		int method           = pb.getIntParameter("method");
		int boxLength        = pb.getIntParameter("boxLength");
		int standard         = pb.getIntParameter("standard");
		int advanced         = pb.getIntParameter("advanced");
		int timeBase         = pb.getIntParameter("timeBase"); //0  ms   1 seconds
		int typeSurr         = pb.getIntParameter("typeSurr");
		int nSurr            = pb.getIntParameter("nSurr");

		String plotModelName = plotModel.getModelName();
		
		// new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());

	    int numValues = 1;
		
		//number of  values	
		if (method == 0)  numValues = 1; // Single value
		if (method == 1)  numValues = signal.size() - boxLength + 1;	//Gliding Box
		
		if ((method == 1) && (numValues < 1)) { // check for gliding method
			DialogUtil.getInstance().showDefaultErrorMessage("Entropies cannot be calculated because gliding box length is too large");
			boxLength = signal.size()/2;
			numValues = signal.size() - boxLength + 1;
			DialogUtil.getInstance().showDefaultErrorMessage("Box length set to: " + boxLength);
			//return null;
		}

		double[] numb   = new double[numValues]; // for single value only one element is used
		double[] mean   = new double[numValues];
		double[] sdnn   = new double[numValues];
		double[] sdann  = new double[numValues];		
		double[] sdnni  = new double[numValues];	
		double[] hrvti  = new double[numValues];	
		double[] rmssd  = new double[numValues];		
		double[] sdsd   = new double[numValues];		
		double[] nn50   = new double[numValues];		
		double[] pnn50  = new double[numValues];		
		double[] nn20   = new double[numValues];		
		double[] pnn20  = new double[numValues];		
			 
		Vector<Double> numbSurr  = new Vector<Double>(); //May be used to store individual surrogate values
		Vector<Double> meanSurr  = new Vector<Double>(); //May be used to store individual surrogate values
		Vector<Double> sdnnSurr  = new Vector<Double>(); //May be used to store individual surrogate values
		Vector<Double> sdannSurr = new Vector<Double>();
		Vector<Double> sdnniSurr = new Vector<Double>();
		Vector<Double> hrvtiSurr = new Vector<Double>();
		Vector<Double> rmssdSurr = new Vector<Double>();
		Vector<Double> sdsdSurr  = new Vector<Double>();
		Vector<Double> nn50Surr  = new Vector<Double>();
		Vector<Double> pnn50Surr = new Vector<Double>();
		Vector<Double> nn20Surr  = new Vector<Double>();
		Vector<Double> pnn20Surr = new Vector<Double>();
		
		double numbSurrMean  = 0.0;
		double meanSurrMean  = 0.0;
		double sdnnSurrMean  = 0.0;
		double sdannSurrMean = 0.0;
		double sdnniSurrMean = 0.0;
		double hrvtiSurrMean = 0.0;
		double rmssdSurrMean = 0.0;
		double sdsdSurrMean  = 0.0;
		double nn50SurrMean  = 0.0;
		double pnn50SurrMean = 0.0;
		double nn20SurrMean  = 0.0;
		double pnn20SurrMean = 0.0;
	    
		fireProgressChanged(5);
		if (isCancelled(getParentTask())) return null;
		
		if (method == 0) { // single value		
			if (standard == 1) {
							
				numb[0]   = (double) signal.size();
				mean[0]   = calcMean(signal);
				sdnn[0]   = calcSDNN(signal, mean[0]);
				sdann[0]  = calcSDANN(signal, timeBase);		
				sdnni[0]  = calcSDNNI(signal, timeBase);	
				hrvti[0]  = calcHRVTI(signal, timeBase);	
				
				Vector<Double> diffSignal = getAbsDiffSignal(signal);		
				rmssd[0]  = calcRMSSD(diffSignal);		
				sdsd[0]   = calcSDSD(diffSignal);		
				nn50[0]   = calcNN50(diffSignal, timeBase);		
				pnn50[0]  = nn50[0]/numb[0];		
				nn20[0]   = calcNN20(diffSignal ,timeBase);		
				pnn20[0]  = nn20[0]/numb[0];	
			
				fireProgressChanged(50);
				if (isCancelled(getParentTask())) return null;
			}
			if (advanced == 1) {
				
				fireProgressChanged(90);
				if (isCancelled(getParentTask())) return null;
			}
			fireProgressChanged(95);
			if (isCancelled(getParentTask())) return null;
		
			if (typeSurr >= 0) { //Surrogates 	
				for (int n= 1; n <= nSurr; n++) {					
					//create a surrogate signal
					Surrogate surrogate = new Surrogate(this);
					Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(signal, typeSurr, 1);
					Vector<Double> signalSurr = plots.get(0);
					
					if (standard == 1) {
						
						numbSurr.add((double) signalSurr.size()); 
						Double meanSurrLocal = calcMean(signalSurr);
						meanSurr.add(meanSurrLocal);
						sdnnSurr.add(calcSDNN(signalSurr, meanSurrLocal));
						sdannSurr.add(calcSDANN(signalSurr, timeBase));
						sdnniSurr.add(calcSDNNI(signalSurr, timeBase));
						hrvtiSurr.add(calcHRVTI(signalSurr, timeBase));
						
						Vector<Double> diffSignalSurr = getAbsDiffSignal(signalSurr);		
						rmssdSurr.add(calcRMSSD(diffSignalSurr));
						sdsdSurr.add(calcSDSD(diffSignalSurr));
						Double nn50SurrLocal = calcNN50(diffSignalSurr, timeBase); 
						nn50Surr.add(nn50SurrLocal);
						pnn50Surr.add(nn50SurrLocal/diffSignalSurr.size());
						Double nn20SurrLocal = calcNN20(diffSignalSurr, timeBase); 
						nn20Surr.add(nn20SurrLocal);
						pnn20Surr.add(nn20SurrLocal/diffSignalSurr.size());
						
						fireProgressChanged(50);
						if (isCancelled(getParentTask())) return null;
					}
					if (advanced == 1) {						
							fireProgressChanged(90);
							if (isCancelled(getParentTask())) return null;
					}
							fireProgressChanged(95);
							if (isCancelled(getParentTask())) return null;
				} //for loop surrogates
				numbSurrMean  = this.calcMean(numbSurr);
				meanSurrMean  = this.calcMean(meanSurr);
				sdnnSurrMean  = this.calcMean(sdnnSurr);
				sdannSurrMean = this.calcMean(sdannSurr);
				sdnniSurrMean = this.calcMean(sdnniSurr);
				hrvtiSurrMean = this.calcMean(hrvtiSurr);
				rmssdSurrMean = this.calcMean(rmssdSurr);
				sdsdSurrMean  = this.calcMean(sdsdSurr);
				nn50SurrMean  = this.calcMean(nn50Surr);
				pnn50SurrMean = this.calcMean(pnn50Surr);
				nn20SurrMean  = this.calcMean(nn20Surr);
				pnn20SurrMean = this.calcMean(pnn20Surr);
				
			}//Surrogates
		}//method == 0 single value
		
	
		if (method == 1) { // gliding values
			if (typeSurr == -1) {  //no surrogate
				for (int i = 0; i < numValues; i++) {
					int proz = (int) (i + 1) * 90 / numValues;
					fireProgressChanged(proz);
					if (isCancelled(getParentTask())) return null;
					Vector<Double> subSignal = new Vector<Double>();
					for (int ii = i; ii < i + boxLength; ii++) { // get subvector
						subSignal.add(signal.get(ii));
					}
					
					if (standard == 1) {
						
						numb[i]   = (double) subSignal.size();
						mean[i]   = calcMean(subSignal);
						sdnn[i]   = calcSDNN(subSignal, mean[i]);
						sdann[i]  = calcSDANN(subSignal, timeBase);		
						sdnni[i]  = calcSDNNI(subSignal, timeBase);	
						hrvti[i]  = calcHRVTI(subSignal, timeBase);	
						
						Vector<Double> diffSubSignal = getAbsDiffSignal(subSignal);		
						rmssd[i]  = calcRMSSD(diffSubSignal);		
						sdsd[i]   = calcSDSD(diffSubSignal);		
						nn50[i]   = calcNN50(diffSubSignal, timeBase);		
						pnn50[i]  = nn50[i]/numb[i];		
						nn20[i]   = calcNN20(diffSubSignal, timeBase);		
						pnn20[i]  = nn20[i]/numb[i];	
										
					}
					if (advanced == 1) {
					
										
					}
				}
			} //no surrogate
			if (typeSurr >= 0) {  //surrogate
				for (int i = 0; i < numValues; i++) {
					int proz = (int) (i + 1) * 90 / numValues;
					fireProgressChanged(proz);
					if (isCancelled(getParentTask())) return null;
					Vector<Double> subSignal = new Vector<Double>();
					for (int ii = i; ii < i + boxLength; ii++) { // get subvector
						subSignal.add(signal.get(ii));
					}
					for (int s= 1; s <= nSurr; s++) {	
						
						//create a surrogate signal
						Surrogate surrogate = new Surrogate(this);
						Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(subSignal, typeSurr, 1);
						Vector<Double> subSignalSurr = plots.get(0);
					
						if (standard == 1) {
													
							numb[i]   += (double) subSignalSurr.size();
							mean[i]   += calcMean(subSignalSurr);
							sdnn[i]   += calcSDNN(subSignalSurr, mean[i]);
							sdann[i]  += calcSDANN(subSignalSurr, timeBase);		
							sdnni[i]  += calcSDNNI(subSignalSurr, timeBase);	
							hrvti[i]  += calcHRVTI(subSignalSurr, timeBase);	
							
							Vector<Double> diffSubSignalSurr = getAbsDiffSignal(subSignalSurr);		
							rmssd[i]  += calcRMSSD(diffSubSignalSurr);		
							sdsd[i]   += calcSDSD(diffSubSignalSurr);		
							nn50[i]   += calcNN50(diffSubSignalSurr, timeBase);		
							pnn50[i]  += nn50[i]/numb[i];		
							nn20[i]   += calcNN20(diffSubSignalSurr, timeBase);		
							pnn20[i]  += nn20[i]/numb[i];						
							
						}
						if (advanced == 1) {
										
						}
					}
					numb[i]  = numb[i]/nSurr;
					mean[i]  = mean[i]/nSurr;
					sdnn[i]  = sdnn[i]/nSurr;
					sdann[i] = sdann[i]/nSurr;		
					sdnni[i] = sdnni[i]/nSurr;	
					hrvti[i] = hrvti[i]/nSurr;	
					rmssd[i] = rmssd[i]/nSurr;		
					sdsd[i]  = sdsd[i]/nSurr;		
					nn50[i]  = nn50[i]/nSurr;		
					pnn50[i] = pnn50[i]/nSurr;		
					nn20[i]  = nn20[i]/nSurr;		
					pnn20[i] = pnn20[i]/nSurr;	
					
				}
			} //surrogate
			
		} //method == 1 gliding values
		
		fireProgressChanged(85);
		if (isCancelled(getParentTask())) return null;
		// create model and Table
		TableModel model = new TableModel("HRV" + (plotModel.getModelName().equals("") ? "" : " of '" + plotModel.getModelName() + "'"));

		if (method == 0) { // single value
			model.addColumn("Plot name");
			model.addColumn("Surrogate");
			// model.addRow(new String[] {plotModelName, String.valueOf(numK),
			// String.valueOf(regStart), String.valueOf(regEnd) });
			String surrogate = "";
			if (typeSurr == -1) surrogate = "No"; 
			if (typeSurr == Surrogate.SURROGATE_AAFT)        surrogate = "AAFT x" + nSurr ; 
			if (typeSurr == Surrogate.SURROGATE_GAUSSIAN)    surrogate = "Gaussian x" +nSurr; 
			if (typeSurr == Surrogate.SURROGATE_RANDOMPHASE) surrogate = "Rand Phase x"+ nSurr; 
			if (typeSurr == Surrogate.SURROGATE_SHUFFLE)     surrogate = "Shuffle x "+nSurr; 
		
			model.addRow(new String[] {plotModelName, surrogate});
		}
		if (method == 1) { // gliding values
			model.addColumn("Plot name (BoxSize=" + boxLength + ")");
			model.addColumn("Surrogate");
			for (int i = 0; i < numValues; i++) {
				// model.addRow(new String[] {"#:"+(i+1), String.valueOf(numK),
				// String.valueOf(regStart), String.valueOf(regEnd) });
				String surrogate = "";
				if (typeSurr == -1) surrogate = "No"; 
				if (typeSurr == Surrogate.SURROGATE_AAFT)        surrogate = "AAFT x" + nSurr ; 
				if (typeSurr == Surrogate.SURROGATE_GAUSSIAN)    surrogate = "Gaussian x" +nSurr; 
				if (typeSurr == Surrogate.SURROGATE_RANDOMPHASE) surrogate = "Rand Phase x"+ nSurr; 
				if (typeSurr == Surrogate.SURROGATE_SHUFFLE)     surrogate = "Shuffle x "+nSurr; 
				model.addRow(new String[] { "#:" + (i + 1) , surrogate});
			}
		}
		if (method == 0) { // single value
			if (standard == 1) {
				int numColumns = model.getColumnCount();
				model.addColumn("#");
				model.addColumn("Mean");
				model.addColumn("SDNN");
				model.addColumn("SDANN");
				model.addColumn("SDNNI");
				model.addColumn("HRVTI");
				model.addColumn("RMSSD");
				model.addColumn("SDSD");
				model.addColumn("NN50");
				model.addColumn("PNN50");
				model.addColumn("NN20");
				model.addColumn("PNN20");
				model.setValueAt(numb[0],  0, numColumns);
				model.setValueAt(mean[0],  0, numColumns+1);
				model.setValueAt(sdnn[0],  0, numColumns+2);
				model.setValueAt(sdann[0], 0, numColumns+3);
				model.setValueAt(sdnni[0], 0, numColumns+4);
				model.setValueAt(hrvti[0], 0, numColumns+5);
				model.setValueAt(rmssd[0], 0, numColumns+6);
				model.setValueAt(sdsd[0],  0, numColumns+7);
				model.setValueAt(nn50[0],  0, numColumns+8);
				model.setValueAt(pnn50[0], 0, numColumns+9);
				model.setValueAt(nn20[0],  0, numColumns+10);
				model.setValueAt(pnn20[0], 0, numColumns+11);
			
			}
			if (advanced == 1) {
//				int numColumns = model.getColumnCount();
//				model.addColumn("????");	
//				model.setValueAt(??[0], 0, numColumns);		
			}
			
			if (typeSurr >= 0) {// numb Mean , mean Mean, sdnn Mean,...... and for each surrogate 
						
				int numColumns = model.getColumnCount();
				model.addColumn("#-Surr");
				model.addColumn("Mean-Surr");	
				model.addColumn("SDNN-Surr");	
				model.addColumn("SDANN-Surr");
				model.addColumn("SDNNI-Surr");
				model.addColumn("HRVTI-Surr");
				model.addColumn("RMSSD-Surr");
				model.addColumn("SDSD-Surr");
				model.addColumn("NN50-Surr");
				model.addColumn("PNN50-Surr");
				model.addColumn("NN20-Surr");
				model.addColumn("PNN20-Surr");
								
				model.setValueAt(numbSurrMean,  0, numColumns);
				model.setValueAt(meanSurrMean,  0, numColumns+1);	
				model.setValueAt(sdnnSurrMean,  0, numColumns+2);
				model.setValueAt(sdannSurrMean, 0, numColumns+3);
				model.setValueAt(sdnniSurrMean, 0, numColumns+4);
				model.setValueAt(hrvtiSurrMean, 0, numColumns+5);
				model.setValueAt(rmssdSurrMean, 0, numColumns+6);
				model.setValueAt(sdsdSurrMean,  0, numColumns+7);
				model.setValueAt(nn50SurrMean,  0, numColumns+8);
				model.setValueAt(pnn50SurrMean, 0, numColumns+9);
				model.setValueAt(nn20SurrMean,  0, numColumns+10);
				model.setValueAt(pnn20SurrMean, 0, numColumns+11);
				
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("#-Surr"+(x+1));
					model.setValueAt(numbSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("Mean-Surr"+(x+1));
					model.setValueAt(meanSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("SDNN-Surr"+(x+1));
					model.setValueAt(sdnnSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("SDANN-Surr"+(x+1));
					model.setValueAt(sdannSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("SDNNI-Surr"+(x+1));
					model.setValueAt(sdnniSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("HRTVI-Surr"+(x+1));
					model.setValueAt(hrvtiSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("RMSSD-Surr"+(x+1));
					model.setValueAt(rmssdSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("SDSD-Surr"+(x+1));
					model.setValueAt(sdsdSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("NN50-Surr"+(x+1));
					model.setValueAt(nn50Surr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("PNN50-Surr"+(x+1));
					model.setValueAt(pnn50Surr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("NN20-Surr"+(x+1));
					model.setValueAt(nn20Surr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("PNN20-Surr"+(x+1));
					model.setValueAt(pnn20Surr.get(x),  0, numColumns + x);
				}
			}
		}
		if (method == 1) { // gliding values
			
			
			if (standard == 1) {
				int numColumns = model.getColumnCount();
				if (typeSurr == -1) { //no surrogates
					model.addColumn("#");
					model.addColumn("Mean");
					model.addColumn("SDNN");
					model.addColumn("SDANN");
					model.addColumn("SDNNI");
					model.addColumn("HRVTI");
					model.addColumn("RMSSD");
					model.addColumn("SDSD");
					model.addColumn("NN50");
					model.addColumn("PNN50");
					model.addColumn("NN20");
					model.addColumn("PNN20");
				}
				else {
					model.addColumn("#-Surr");
					model.addColumn("Mean-Surr");	
					model.addColumn("SDNN-Surr");	
					model.addColumn("SDANN-Surr");
					model.addColumn("SDNNI-Surr");
					model.addColumn("HRVTI-Surr");
					model.addColumn("RMSSD-Surr");
					model.addColumn("SDSD-Surr");
					model.addColumn("NN50-Surr");
					model.addColumn("PNN50-Surr");
					model.addColumn("NN20-Surr");
					model.addColumn("PNN20-Surr");
				}		
				for (int i = 0; i < numValues; i++) {		
					model.setValueAt(numb[i],  i, numColumns);
					model.setValueAt(mean[i],  i, numColumns+1);
					model.setValueAt(sdnn[i],  i, numColumns+2);
					model.setValueAt(sdann[i], i, numColumns+3);
					model.setValueAt(sdnni[i], i, numColumns+4);
					model.setValueAt(hrvti[i], i, numColumns+5);
					model.setValueAt(rmssd[i], i, numColumns+6);
					model.setValueAt(sdsd[i],  i, numColumns+7);
					model.setValueAt(nn50[i],  i, numColumns+8);
					model.setValueAt(pnn50[i], i, numColumns+9);
					model.setValueAt(nn20[i],  i, numColumns+10);
					model.setValueAt(pnn20[i], i, numColumns+11);
					
				}
			}
			if (advanced == 1) {
				int numColumns = model.getColumnCount();
				if (typeSurr == -1) {
					model.addColumn("??");	
				}
				else {
					model.addColumn("??-Surr");
				}
				for (int i = 0; i < numValues; i++) {
					//model.setValueAt(??[i], i, numColumns);			
				}
			}	
		}
		model.fireTableStructureChanged(); // this is mandatory because it  updates the table


		this.fireProgressChanged(95);
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(model);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new PlotOpHRVDescriptor().getName();
		}
		return name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpHRVDescriptor.TYPE;
	}

}
