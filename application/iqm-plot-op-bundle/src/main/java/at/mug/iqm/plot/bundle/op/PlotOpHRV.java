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
import at.mug.iqm.commons.util.ErrorMessagePanel;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFFTDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpHRVDescriptor;
import flanagan.interpolation.CubicSpline;
import flanagan.math.FourierTransform;


/**
 *  <li>2018 10 
 *  according to
 *  Heart rate variability Standards of measurement, physiological interpretation, and clinical use
 *  Task Force of The European Society of Cardiology and The North American Society of Pacing and Electrophysiology
 *  Malik et al. European Heart Journal (1996) 17,354-381
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
	
	double maxValue = -Double.MAX_VALUE;
	double minValue = Double.MAX_VALUE;

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
	 * This method calculates the SD 
	 * 
	 * @param data1D
	 * @return Double 
	 */
	private Double calcSD(Vector<Double> data1D) {
		double sd = 0.0;
		double sum = 0.0;
		double mean = calcMean(data1D);
		for (double d : data1D) {
			sum += Math.pow(d-mean, 2.0);
		}
		sd = Math.sqrt(sum/(data1D.size()-1)); //ms
		return sd;
	}
	
	/**
	 * This method calculates the mean of intervals
	 * 
	 * @param data1D
	 * @return Double Mean intervals
	 */
	private Double calcMeanNN(Vector<Double> data1D, int timeBase) {
		double meanNN = 999.111;
		double sum = 0.0;
		for (double d : data1D) {
			sum += d;
		}
		if (timeBase == 0) meanNN = sum / data1D.size(); //ms
		if (timeBase == 1) meanNN = sum / data1D.size() * 1000.0; //s   meanNN dannin ms für Ausgabe
		
		return meanNN;
	}
	
	/**
	 * This method calculates the SDNN (simply the SD)
	 * This is simply the Standard Deviation
	 * 
	 * @param data1D, timeBase
	 * @return Double 
	 */
	private Double calcSDNN(Vector<Double> data1D, int timeBase) {
		double sdnn = 999.111;
		double sum = 0.0;
		double mean = calcMean(data1D);
		for (double d : data1D) {
			sum += Math.pow(d-mean, 2.0);
		}
		if (timeBase == 0) sdnn = Math.sqrt(sum/(data1D.size()-1)); //ms
		if (timeBase == 1) sdnn = Math.sqrt(sum/(data1D.size()-1)) *1000.0; //s    sdnn dann in ms für Ausagabe
		return sdnn;
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
		if (timeBase == 0) sdann = calcSD(fiveMinutesMeans); //ms
		if (timeBase == 1) sdann = calcSD(fiveMinutesMeans) *1000.0; //s    sdnn dann in ms für Ausagabe
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
				//double mean = calcMean(fiveMinutesSignal); 
				fiveMinutesSDs.add(calcSD(fiveMinutesSignal));
				fiveMinutesSignal = new Vector<Double>();
				sumOfSubsequentIntervals = 0.0;
			}			
		}	
		if (timeBase == 0) sdnni = calcMean(fiveMinutesSDs); //ms
		if (timeBase == 1) sdnni = calcMean(fiveMinutesSDs) *1000.0; //s    sdnn dann in ms für Ausagabe
		return sdnni;
	}
	
	/**
	 * This method calculates the HRVTI (HRV triangular index) with a histogram
	 * according to McNames & Aboy Med Bio Eng Comput (2006) 44:747–756)
	 * 
	 * @param data1D, timeBase
	 * @return Double 
	 */
	private Double calcHRVTI(Vector<Double> data1D, int timeBase) {
		double hrvti    = 0.0;
		double histoMax = 0.0;
		double binSize = 8; //ms  8ms according to McNames & Aboy Med Bio Eng Comput (2006) 44:747–756)
		
		if (timeBase == 0) binSize = binSize; //ms
		if (timeBase == 1) binSize = binSize/1000.0; //s
		
		
		// create a histogram with bin size
		double maxValue =  this.maxValue;

		int binNumber = (int) Math.ceil(maxValue/binSize);
		Vector<Integer> histo = new Vector<Integer>();
		for (int b = 0; b < binNumber; b++) histo.add(0);
				
		for (int i = 0; i < data1D.size(); i++) { // scroll through intervals, negative values are not allowed
			//System.out.println("PlotOpHRV: i: " + i + "     data1D[i]: " + data1D.get(i));
			int index = (int) Math.ceil(data1D.get(i)/binSize);
			if (index == 0) index = 1; //can occur if data1D.get(i) = 0.0 
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
	 * @param diffData1D, timeBase
	 * @return Double 
	 */
	private Double calcRMSSD(Vector<Double> diffData1D, int timeBase) {
		double rmssd= 999.111;
		double sum = 0.0;
		for (double d : diffData1D) {
			sum += Math.pow(d, 2.0);
		}
		if (timeBase == 0) rmssd = Math.sqrt(sum/(diffData1D.size()));
		if (timeBase == 1) rmssd = Math.sqrt(sum/(diffData1D.size())) *  1000.0; //s    rmssd dann in ms für Ausagabe
		return rmssd;
	}
	/**
	 * This method calculates the SDSD (SD of interval differences)
	 * 
	 * @param diffData1D, timeBase
	 * @return Double 
	 */
	private Double calcSDSD(Vector<Double> diffData1D, int timeBase ) {
		double sdsd = 999.111;
		if (timeBase == 0) sdsd = calcSD(diffData1D);
		if (timeBase == 1) sdsd = calcSD(diffData1D) * 1000.0; //s    rmssd dann in ms für Ausagabe
		return sdsd;
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
	
	/**
	 * This method calculates the frequency components
	 * VLF    very low frequency 0-0.04 Hz
	 * LF     low frequency 0.04-0.15 Hz
	 * HF     high frequency 0.15-0.4 Hz
	 * TP     total power 0-1.5 Hz
	 * LFn    LF norm = 100*LF/(TP-VLF)
	 * HFn    HF norm = 100*HF/(TP-VLF)
	 * LF/HF  ratio
	 * 
	 * @param XData1D, YData1D, timeBase
	 * @return Double[7] (VLF, LF, HF, LFn, HFn, LF/HF, TP,)
	 */
	private double[] calcPSDParameters(Vector<Double> xData1D, Vector<Double> yData1D,  int timeBase) {
		//xData1D are the absolute times tn of subsequent beats (the first beat is missing)
		//yData1D are the corresponding beat to beat intervals in ms or seconds,  
		
		double vlf = 0.0;
		double lf  = 0.0;
		double hf  = 0.0;
		double tp  = 0.0;
		double[] psdParameters = new double[7];
		
		//convert Vector to double array for Flanagan 
		double[] xData = new double[xData1D.size()];
		double[] yData = new double[yData1D.size()];
		
		
		// set data
		for (int i = 0; i < xData1D.size(); i++) {
			if (timeBase == 0) {//ms  convert to seconds because of FFT in Hz
				xData[i] = xData1D.get(i)/1000.0;
				yData[i] = yData1D.get(i)/1000.0;
			}
			if (timeBase == 1) {//s 
				xData[i] = xData1D.get(i);
				yData[i] = yData1D.get(i);
			}
		}
		//interpolate interval sequence to get equidistant time spacings - a sample rate
		CubicSpline cs =new CubicSpline(xData, yData);
		
		//Virtual sample rate = 4Hz
		double virtSampleTime = 0.25; //virtual sample time in sec (first virtual time point)
		double timeOfLastBeat = xData[xData.length -1]; //time of last beat (interval) in seconds, roughly the recording time
		int numbVirtTimePts = (int) Math.floor(timeOfLastBeat/0.25);
	
		//System.out.println("PlotOpHRV: number of virtual data points for FFT: "+ numbVirtTimePts);
		if (numbVirtTimePts < 10) {
			DialogUtil.getInstance().showDefaultInfoMessage("Number of datapoints for FFT: "+numbVirtTimePts+ " is too low!");
			
			psdParameters[0] = 999.111;
			psdParameters[1] = 999.111;
			psdParameters[2] = 999.111;
			psdParameters[3] = 999.111;
			psdParameters[4] = 999.111;
			psdParameters[5] = 999.111;
			psdParameters[6] = 999.111;
		
			return psdParameters;
		}
		
		//double[] xInterpolData = new double [numbVirtTimePts];
		double[] yInterpolData = new double[numbVirtTimePts];
		
		for (int t = 1; t <= numbVirtTimePts; t++) { //
			yInterpolData[t-1] = cs.interpolate(t*virtSampleTime);
			//System.out.println("PlotOpHRV: t: " + t + "       time: " + (t*virtSampleTime) + "     yInterpolate[t]: " +  yInterpolData[t-1]);
		}
		
//		//not necessary because .powerSpectrum() does extend to power of 2 by itself!
//		//data length should have a power of 2 because .powerSpectrum() truncates the signal to the next smaller power of 2 value
//		//therefore, extend with zeros 
//		int powerSize = 1;
//		while (yInterpolData.length > powerSize){
//			powerSize = powerSize*2;
//		}
//		//powerSize = powerSize /2;
//		double[] yInterpolDataExtended = new double[powerSize];
//		//set data
//		if (powerSize <= yInterpolData.length) {
//			for (int i = 0; i < powerSize; i++){
//				yInterpolDataExtended[i] = yInterpolData[i];
//		 }
//		 } else {
//			 for (int i = 0; i < yInterpolData.length; i++){
//				 yInterpolDataExtended[i] = yInterpolData[i];
//			 }
//			 for (int i = yInterpolData.length; i < powerSize; i++){
//				 yInterpolDataExtended[i] =0.0d; //extend with zeros
//			 }
//		 }
		
		   
	    //FFT from 0 to 2Hz
	    FourierTransform ft = new FourierTransform();
		//ft.removeWindow();
		//ft.setRectangular();
		ft.setBartlett();
		//ft.setWelch();
		//ft.setHann();
		//ft.setHamming();
		//ft.setKaiser();
		//ft.setGaussian();
		
		ft.setData(yInterpolData);
		ft.setDeltaT(virtSampleTime);
	    //ft.transform();
		//ft.plotPowerSpectrum(); //Flanagan plot window
		double[][] ps = ft.powerSpectrum();
	
//		//to see power spectrum in IQM 
//		//return argument has to be changed to Result
//		Vector<Double> signalPS = new Vector<Double>();
//		Vector<Double> rangePS = new Vector<Double>();
//		int resultLogLin = PlotOpFFTDescriptor.RESULT_LIN;
//		for (int i = 0; i < ps[0].length; i++){
//			if(resultLogLin == PlotOpFFTDescriptor.RESULT_LOG){
//				signalPS.add(Math.log(ps[1][i]));  //log Power Spectrum
//			}
//			if(resultLogLin == PlotOpFFTDescriptor.RESULT_LIN){
//				signalPS.add(ps[1][i]);  //Power Spectrum
//			}
//			rangePS.add(ps[0][i]); //if ft.setDeltaT(1/samplerate) is set
//		}
//
//		// it is necessary to generate a new instance of PlotModel
//		PlotModel plotModelNew = new PlotModel("DomainHeader", "DomainUnit", "DataHeader", "DataUnit", rangePS, signalPS);
//		
//		if (this.isCancelled(this.getParentTask())) return null;
//		return new Result(plotModelNew);
		
		//scroll through spectrum and sum up 
		for (int f = 0; f < ps[0].length; f++) {
			if (ps[0][f] <= 0.04){ //VLF
				vlf = vlf + ps[1][f];
			}
			if ((ps[0][f] > 0.04) && (ps[0][f] <= 0.15)){ //LF
				lf = lf + ps[1][f];
			}
			if ((ps[0][f] > 0.15) && (ps[0][f] <= 0.4)){ //HF
				hf = hf + ps[1][f];
			}
			if (ps[0][f] <= 0.15){ //TP
				tp = tp + ps[1][f];
			}	
		}
		psdParameters[0] = vlf;
		psdParameters[1] = lf;
		psdParameters[2] = hf;
		psdParameters[3] = 100.0*lf/(tp-vlf);
		psdParameters[4] = 100.0*hf/(tp-vlf);
		psdParameters[5] = lf/hf;
		psdParameters[6] = tp;
	
		return psdParameters;
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
		Vector<Double> domain = new Vector<Double>(plotModel.getDomain());
		
		//check min max values
		this.maxValue =  0.0; // for histogram
		this.minValue = Double.MAX_VALUE; //in order to check if there are negative values
		for (double v : signal) {
			if (v > maxValue) maxValue = v;
			if (v < minValue) minValue = v;
		}	
		if (minValue < 0.0) { //stop execution, negative values are not allowed	
			DialogUtil.getInstance().showDefaultErrorMessage("Negative values are not allowed!");
			return null;
		}
		
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

		double[] numbnn = new double[numValues]; // for single value only one element is used
		double[] meannn = new double[numValues];
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
		double[] vlf    = new double[numValues];	 //power spectral parameters
		double[] lf     = new double[numValues];	
		double[] hf     = new double[numValues];	
		double[] lfn    = new double[numValues];	
		double[] hfn    = new double[numValues];	
		double[] lfhf   = new double[numValues];	
		double[] tp     = new double[numValues];	
			 
		Vector<Double> numbnnSurr= new Vector<Double>(); //May be used to store individual surrogate values
		Vector<Double> meannnSurr= new Vector<Double>(); //May be used to store individual surrogate values
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
		Vector<Double> vlfSurr  = new Vector<Double>();
		Vector<Double> lfSurr   = new Vector<Double>();
		Vector<Double> hfSurr   = new Vector<Double>();
		Vector<Double> lfnSurr  = new Vector<Double>();
		Vector<Double> hfnSurr  = new Vector<Double>();
		Vector<Double> lfhfSurr = new Vector<Double>();
		Vector<Double> tpSurr   = new Vector<Double>();
		
		
		double numbnnSurrMean= 0.0;
		double meannnSurrMean= 0.0;
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
		double vlfSurrMean   = 0.0;
		double lfSurrMean    = 0.0;
		double hfSurrMean    = 0.0;
		double lfnSurrMean   = 0.0;
		double hfnSurrMean   = 0.0;
		double lfhfSurrMean  = 0.0;
		double tpSurrMean    = 0.0;
	    
		fireProgressChanged(5);
		if (isCancelled(getParentTask())) return null;
		
		if (method == 0) { // single value		
			if (standard == 1) {
							
				numbnn[0]   = (double) signal.size();
				meannn[0] = calcMeanNN(signal, timeBase);
				sdnn[0]   = calcSDNN  (signal, timeBase);
				sdann[0]  = calcSDANN (signal, timeBase);		
				sdnni[0]  = calcSDNNI (signal, timeBase);	
				hrvti[0]  = calcHRVTI (signal, timeBase);	
				
				Vector<Double> diffSignal = getAbsDiffSignal(signal);		
				rmssd[0]  = calcRMSSD(diffSignal, timeBase);		
				sdsd[0]   = calcSDSD (diffSignal, timeBase);		
				nn50[0]   = calcNN50 (diffSignal, timeBase);		
				pnn50[0]  = nn50[0]/numbnn[0];		
				nn20[0]   = calcNN20(diffSignal ,timeBase);		
				pnn20[0]  = nn20[0]/numbnn[0];	
			
				double[] psdParameters = calcPSDParameters(domain, signal, timeBase);
				vlf[0]  = psdParameters[0];
				lf[0]   = psdParameters[1];
				hf[0]   = psdParameters[2];
				lfn[0]  = psdParameters[3];
				hfn[0]  = psdParameters[4];
				lfhf[0] = psdParameters[5];
				tp[0]   = psdParameters[6];
				
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
						
						numbnnSurr.add  ((double) signalSurr.size()); ;
						meannnSurr.add(calcMeanNN(signalSurr, timeBase));
						sdnnSurr.add  (calcSDNN  (signalSurr, timeBase));
						sdannSurr.add (calcSDANN(signalSurr, timeBase));
						sdnniSurr.add (calcSDNNI(signalSurr, timeBase));
						hrvtiSurr.add (calcHRVTI(signalSurr, timeBase));
						
						Vector<Double> diffSignalSurr = getAbsDiffSignal(signalSurr);		
						rmssdSurr.add(calcRMSSD(diffSignalSurr, timeBase));
						sdsdSurr.add (calcSDSD (diffSignalSurr, timeBase));
						Double nn50SurrLocal = calcNN50(diffSignalSurr, timeBase); 
						nn50Surr.add(nn50SurrLocal);
						pnn50Surr.add(nn50SurrLocal/diffSignalSurr.size());
						Double nn20SurrLocal = calcNN20(diffSignalSurr, timeBase); 
						nn20Surr.add(nn20SurrLocal);
						pnn20Surr.add(nn20SurrLocal/diffSignalSurr.size());
						
						double[] psdParameters = calcPSDParameters(domain, signalSurr, timeBase);
						vlfSurr.add(psdParameters[0]);
						lfSurr.add(psdParameters[1]);
						hfSurr.add(psdParameters[2]);
						lfnSurr.add(psdParameters[3]);
						hfnSurr.add(psdParameters[4]);
						lfhfSurr.add(psdParameters[5]);
						tpSurr.add(psdParameters[6]);
										
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
				numbnnSurrMean= this.calcMean(numbnnSurr);
				meannnSurrMean= this.calcMean(meannnSurr);
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
				vlfSurrMean   = this.calcMean(vlfSurr);
				lfSurrMean    = this.calcMean(lfSurr);
				hfSurrMean    = this.calcMean(hfSurr);
				lfnSurrMean   = this.calcMean(lfnSurr);
				hfnSurrMean   = this.calcMean(hfnSurr);
				lfhfSurrMean  = this.calcMean(lfhfSurr);
				tpSurrMean    = this.calcMean(tpSurr);
				
			}//Surrogates
		}//method == 0 single value
		
	
		if (method == 1) { // gliding values
			if (typeSurr == -1) {  //no surrogate
				for (int i = 0; i < numValues; i++) {
					int proz = (int) (i + 1) * 90 / numValues;
					fireProgressChanged(proz);
					if (isCancelled(getParentTask())) return null;
					Vector<Double> subSignal = new Vector<Double>();
					Vector<Double> subDomain = new Vector<Double>();
					for (int ii = i; ii < i + boxLength; ii++) { // get subvector
						subSignal.add(signal.get(ii));
						subDomain.add(domain.get(ii));
					}
					
					if (standard == 1) {
						
						numbnn[i]   = (double) subSignal.size();
						meannn[i] = calcMeanNN(subSignal, timeBase);
						sdnn[i]   = calcSDNN  (subSignal, timeBase);
						sdann[i]  = calcSDANN (subSignal, timeBase);		
						sdnni[i]  = calcSDNNI (subSignal, timeBase);	
						hrvti[i]  = calcHRVTI (subSignal, timeBase);	
						
						Vector<Double> diffSubSignal = getAbsDiffSignal(subSignal);		
						rmssd[i]  = calcRMSSD(diffSubSignal, timeBase);		
						sdsd[i]   = calcSDSD (diffSubSignal, timeBase);		
						nn50[i]   = calcNN50 (diffSubSignal, timeBase);		
						pnn50[i]  = nn50[i]/numbnn[i];		
						nn20[i]   = calcNN20(diffSubSignal, timeBase);		
						pnn20[i]  = nn20[i]/numbnn[i];	
						
						double[] psdParameters = calcPSDParameters(subDomain, subSignal, timeBase);
						vlf[i]  = psdParameters[0];
						lf[i]   = psdParameters[1];
						hf[i]   = psdParameters[2];
						lfn[i]  = psdParameters[3];
						hfn[i]  = psdParameters[4];
						lfhf[i] = psdParameters[5];
						tp[i]   = psdParameters[6];				
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
					Vector<Double> subDomain = new Vector<Double>();
					for (int ii = i; ii < i + boxLength; ii++) { // get subvector
						subSignal.add(signal.get(ii));
						subDomain.add(domain.get(ii));
					}
					for (int s= 1; s <= nSurr; s++) {	
						
						//create a surrogate signal
						Surrogate surrogate = new Surrogate(this);
						Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(subSignal, typeSurr, 1);
						Vector<Double> subSignalSurr = plots.get(0);
					
						if (standard == 1) {
													
							numbnn[i] += (double)   subSignalSurr.size();
							meannn[i] += calcMeanNN(subSignalSurr, timeBase);
							sdnn[i]   += calcSDNN  (subSignalSurr, timeBase);
							sdann[i]  += calcSDANN (subSignalSurr, timeBase);		
							sdnni[i]  += calcSDNNI (subSignalSurr, timeBase);	
							hrvti[i]  += calcHRVTI (subSignalSurr, timeBase);	
							
							Vector<Double> diffSubSignalSurr = getAbsDiffSignal(subSignalSurr);		
							rmssd[i]  += calcRMSSD(diffSubSignalSurr, timeBase);		
							sdsd[i]   += calcSDSD (diffSubSignalSurr, timeBase);		
							nn50[i]   += calcNN50 (diffSubSignalSurr, timeBase);		
							pnn50[i]  += nn50[i]/numbnn[i];		
							nn20[i]   += calcNN20 (diffSubSignalSurr, timeBase);		
							pnn20[i]  += nn20[i]/numbnn[i];		
							
							
							double[] psdParameters = calcPSDParameters(subDomain, subSignalSurr, timeBase);
							vlf[i]  += psdParameters[0];
							lf[i]   += psdParameters[1];
							hf[i]   += psdParameters[2];
							lfn[i]  += psdParameters[3];
							hfn[i]  += psdParameters[4];
							lfhf[i] += psdParameters[5];
							tp[i]   += psdParameters[6];		
							
						}
						if (advanced == 1) {
										
						}
					}
					numbnn[i]= numbnn[i]/nSurr;
					meannn[i]= meannn[i]/nSurr;
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
					vlf[i]   = vlf[i]/nSurr;
					lf[i]    = lf[i]/nSurr;
					hf[i]    = hf[i]/nSurr;
					lfn[i]   = lfn[i]/nSurr;
					hfn[i]   = hfn[i]/nSurr;
					lfhf[i]  = lfhf[i]/nSurr;
					tp[i]    = tp[i]/nSurr;		
					
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
				model.addColumn("Beats [#]");
				model.addColumn("MeanHR [1/min]");
				model.addColumn("MeanNN [ms]");
				model.addColumn("SDNN [ms]");
				model.addColumn("SDANN [ms]");
				model.addColumn("SDNNI [ms]");
				model.addColumn("HRVTI");
				model.addColumn("RMSSD [ms]");
				model.addColumn("SDSD [ms]");
				model.addColumn("NN50 [#]");
				model.addColumn("PNN50 [%]");
				model.addColumn("NN20 [#]");
				model.addColumn("PNN20 [%]");
				model.addColumn("VLF [%]");
				model.addColumn("LF [%]");
				model.addColumn("HF [%]");
				model.addColumn("LFnorm");
				model.addColumn("HFnorm");
				model.addColumn("LF/HF");
				model.addColumn("TP [%]");
				model.setValueAt(numbnn[0],  0, numColumns);
				model.setValueAt(1.0/meannn[0]*1000.0*60.0, 0, numColumns+1);
				model.setValueAt(meannn[0],0, numColumns+2);
				model.setValueAt(sdnn[0],  0, numColumns+3);
				model.setValueAt(sdann[0], 0, numColumns+4);
				model.setValueAt(sdnni[0], 0, numColumns+5);
				model.setValueAt(hrvti[0], 0, numColumns+6);
				model.setValueAt(rmssd[0], 0, numColumns+7);
				model.setValueAt(sdsd[0],  0, numColumns+8);
				model.setValueAt(nn50[0],  0, numColumns+9);
				model.setValueAt(pnn50[0], 0, numColumns+10);
				model.setValueAt(nn20[0],  0, numColumns+11);
				model.setValueAt(pnn20[0], 0, numColumns+12);
				model.setValueAt(vlf[0],   0, numColumns+13);
				model.setValueAt(lf[0],    0, numColumns+14);
				model.setValueAt(hf[0],    0, numColumns+15);
				model.setValueAt(lfn[0],   0, numColumns+16);
				model.setValueAt(hfn[0],   0, numColumns+17);
				model.setValueAt(lfhf[0],  0, numColumns+18);
				model.setValueAt(tp[0],    0, numColumns+19);
			
			}
			if (advanced == 1) {
//				int numColumns = model.getColumnCount();
//				model.addColumn("????");	
//				model.setValueAt(??[0], 0, numColumns);		
			}
			
			if (typeSurr >= 0) {// numb Mean , mean Mean, sdnn Mean,...... and for each surrogate 
						
				int numColumns = model.getColumnCount();
				model.addColumn("Beats-Surr [#]");
				model.addColumn("MeanHR-Surr [1/min]");	
				model.addColumn("MeanNN-Surr [ms]");	
				model.addColumn("SDNN-Surr [ms]");	
				model.addColumn("SDANN-Surr [ms]");
				model.addColumn("SDNNI-Surr [ms]");
				model.addColumn("HRVTI-Surr");
				model.addColumn("RMSSD-Surr [ms]");
				model.addColumn("SDSD-Surr [ms]");
				model.addColumn("NN50-Surr [#]");
				model.addColumn("PNN50-Surr [%]");
				model.addColumn("NN20-Surr [#]");
				model.addColumn("PNN20-Surr[%]");
				model.addColumn("VLF-Surr [%]");
				model.addColumn("LF-Surr [%]");
				model.addColumn("HF-Surr [%]");
				model.addColumn("LFnorm-Surr");
				model.addColumn("HFnorm-Surr");
				model.addColumn("LF/HF-Surr");
				model.addColumn("TP-Surr [%]");
								
				model.setValueAt(numbnnSurrMean,0, numColumns);
				model.setValueAt(1.0/meannnSurrMean * 1000.0*60, 0, numColumns+1);	
				model.setValueAt(meannnSurrMean,0, numColumns+2);	
				model.setValueAt(sdnnSurrMean,  0, numColumns+3);
				model.setValueAt(sdannSurrMean, 0, numColumns+4);
				model.setValueAt(sdnniSurrMean, 0, numColumns+5);
				model.setValueAt(hrvtiSurrMean, 0, numColumns+6);
				model.setValueAt(rmssdSurrMean, 0, numColumns+7);
				model.setValueAt(sdsdSurrMean,  0, numColumns+8);
				model.setValueAt(nn50SurrMean,  0, numColumns+9);
				model.setValueAt(pnn50SurrMean, 0, numColumns+10);
				model.setValueAt(nn20SurrMean,  0, numColumns+11);
				model.setValueAt(pnn20SurrMean, 0, numColumns+12);
				model.setValueAt(vlfSurrMean,   0, numColumns+13);
				model.setValueAt(lfSurrMean,    0, numColumns+14);
				model.setValueAt(hfSurrMean,    0, numColumns+15);
				model.setValueAt(lfnSurrMean,   0, numColumns+16);
				model.setValueAt(hfnSurrMean,   0, numColumns+17);
				model.setValueAt(lfhfSurrMean,  0, numColumns+18);
				model.setValueAt(tpSurrMean,    0, numColumns+19);
				
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("Beats-Surr [#]"+(x+1));
					model.setValueAt(numbnnSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("MeanHR-Surr [1/min]"+(x+1));
					model.setValueAt(1.0/meannnSurr.get(x) *1000.0*60,  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("MeanNN-Surr [ms]"+(x+1));
					model.setValueAt(meannnSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("SDNN-Surr [ms]"+(x+1));
					model.setValueAt(sdnnSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("SDANN-Surr [ms]"+(x+1));
					model.setValueAt(sdannSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("SDNNI-Surr [ms]"+(x+1));
					model.setValueAt(sdnniSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("HRTVI-Surr"+(x+1));
					model.setValueAt(hrvtiSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("RMSSD-Surr [ms]"+(x+1));
					model.setValueAt(rmssdSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("SDSD-Surr [ms]"+(x+1));
					model.setValueAt(sdsdSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("NN50-Surr"+(x+1));
					model.setValueAt(nn50Surr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("PNN50-Surr [%]"+(x+1));
					model.setValueAt(pnn50Surr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("NN20-Surr"+(x+1));
					model.setValueAt(nn20Surr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("PNN20-Surr [%]"+(x+1));
					model.setValueAt(pnn20Surr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("VLF-Surr [%]"+(x+1));
					model.setValueAt(vlfSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("LF-Surr [%]"+(x+1));
					model.setValueAt(lfSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("HF-Surr [%]"+(x+1));
					model.setValueAt(hfSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("LFnorm-Surr"+(x+1));
					model.setValueAt(lfnSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("HFnorm-Surr"+(x+1));
					model.setValueAt(hfnSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("LF/HF-Surr"+(x+1));
					model.setValueAt(lfhfSurr.get(x),  0, numColumns + x);
				}
				numColumns = model.getColumnCount();
				for (int x=0; x < nSurr; x++){
					model.addColumn("TP-Surr [%]"+(x+1));
					model.setValueAt(tpSurr.get(x),  0, numColumns + x);
				}
			}
		}
		if (method == 1) { // gliding values
			
			
			if (standard == 1) {
				int numColumns = model.getColumnCount();
				if (typeSurr == -1) { //no surrogates
					model.addColumn("Beats [#]");
					model.addColumn("MeanHR [1/min]");
					model.addColumn("MeanNN [ms]");
					model.addColumn("SDNN [ms]");
					model.addColumn("SDANN [ms]");
					model.addColumn("SDNNI [ms]");
					model.addColumn("HRVTI");
					model.addColumn("RMSSD [ms]");
					model.addColumn("SDSD [ms]");
					model.addColumn("NN50 [#]");
					model.addColumn("PNN50 [%]");
					model.addColumn("NN20 [#]");
					model.addColumn("PNN20 [%]");
					model.addColumn("VLF [%]");
					model.addColumn("LF [%]");
					model.addColumn("HF [%]");
					model.addColumn("LFnorm");
					model.addColumn("HFnorm");
					model.addColumn("LF/HF");
					model.addColumn("TP [%]");
				}
				else {
					model.addColumn("Beats-Surr [#]");
					model.addColumn("MeanHR-Surr [1/min]");	
					model.addColumn("MeanNN-Surr [ms]");	
					model.addColumn("SDNN-Surr [ms]");	
					model.addColumn("SDANN-Surr [ms]");
					model.addColumn("SDNNI-Surr [ms]");
					model.addColumn("HRVTI-Surr");
					model.addColumn("RMSSD-Surr [ms]");
					model.addColumn("SDSD-Surr [ms]");
					model.addColumn("NN50-Surr [#]");
					model.addColumn("PNN50-Surr [%]");
					model.addColumn("NN20-Surr [#]");
					model.addColumn("PNN20-Surr [%]");
					model.addColumn("VLF-Surr [%]");
					model.addColumn("LF-Surr [%]");
					model.addColumn("HF-Surr [%]");
					model.addColumn("LFnorm-Surr");
					model.addColumn("HFnorm-Surr");
					model.addColumn("LF/HF-Surr");
					model.addColumn("TP-Surr [%]");
				}		
				for (int i = 0; i < numValues; i++) {		
					model.setValueAt(numbnn[i], i, numColumns);
					model.setValueAt(1.0/meannn[i]*1000.0*60.0, i, numColumns+1);
					model.setValueAt(meannn[i], i, numColumns+2);
					model.setValueAt(sdnn[i],  i, numColumns+3);
					model.setValueAt(sdann[i], i, numColumns+4);
					model.setValueAt(sdnni[i], i, numColumns+5);
					model.setValueAt(hrvti[i], i, numColumns+6);
					model.setValueAt(rmssd[i], i, numColumns+7);
					model.setValueAt(sdsd[i],  i, numColumns+8);
					model.setValueAt(nn50[i],  i, numColumns+9);
					model.setValueAt(pnn50[i], i, numColumns+10);
					model.setValueAt(nn20[i],  i, numColumns+11);
					model.setValueAt(pnn20[i], i, numColumns+12);
					model.setValueAt(vlf[i],   i, numColumns+13);
					model.setValueAt(lf[i],    i, numColumns+14);
					model.setValueAt(hf[i],    i, numColumns+15);
					model.setValueAt(lfn[i],   i, numColumns+16);
					model.setValueAt(hfn[i],   i, numColumns+17);
					model.setValueAt(lfhf[i],  i, numColumns+18);
					model.setValueAt(tp[i],    i, numColumns+19);
					
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
