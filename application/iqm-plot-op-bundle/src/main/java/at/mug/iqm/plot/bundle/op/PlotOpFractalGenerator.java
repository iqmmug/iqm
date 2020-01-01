package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFractalGenerator.java
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


import java.util.Random;
import java.util.Vector;

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFractalGeneratorDescriptor;
import flanagan.complex.Complex;

/**
 * Analyzing exact fractal time series: evaluation dispersional analysis and rescaled range methods. Physica A 1997 246 609-632
 * <p>
 * <b>Changes</b>
 * <ul>
 * 	<li> DHM Davies and Harte method
 * 	<li> SSM spectral synthesis method
 * 	<li> accordiing to Caccia Percival Cannon Raymond Bassingthwaigthe. 
 * </ul>
 * 
 * @author Ahammer
 * @since   2012 12
 */
public class PlotOpFractalGenerator extends AbstractOperator {

	public PlotOpFractalGenerator() {
	}

	/**
	 * This method converts a fBm signal into a fGn signal
	 * 
	 * @param signal
	 * @return converted signal
	 */
	private Vector<Double> convert_fBmTofGn(Vector<Double> signal) {
		Vector<Double> signalNew = new Vector<Double>();
		for (int i = 1; i < signal.size(); i++) {
			signalNew.add(signal.get(i) - signal.get(i - 1));
		}
		return signalNew;
	}

	/**
	 * This method converts a fGn signal into a fBm signal
	 * 
	 * @param signal
	 * @return converted signal
	 */
	private Vector<Double> convert_fGnTofBm(Vector<Double> signal) {
		Vector<Double> signalNew = new Vector<Double>();
		signalNew.add(signal.get(0));
		for (int i = 1; i < signal.size(); i++) {
			signalNew.add(signalNew.get(i - 1) + signal.get(i));
		}
		return signalNew;
	}

	/**
	 * this method generates an auto covariance function with defined length and
	 * Hurst coefficient
	 * 
	 * @param M
	 * @param hurst
	 * @return auto covariance function
	 */
	private Vector<Double> generateAC(int M, double hurst) {
		Vector<Double> signalAC = new Vector<Double>();
		double sigma = 1;
		double sigma2 = sigma * sigma;
		for (int t = 0; t <= M / 2; t++) {
			double g = sigma2 / 2.0d * (Math.pow(Math.abs(t + 1), 2 * hurst) - 2 * Math.pow(Math.abs(t), 2 * hurst) + Math.pow(Math.abs(t - 1), 2 * hurst));
			signalAC.add(g);
		}
		for (int t = M / 2 - 1; t >= 1; t--) {
			signalAC.add(signalAC.get(t));
		}
		return signalAC;
	}

	/**
	 * This method calculates the DFT of a complex number
	 * 
	 * @param inReal
	 * @param inImag
	 * @param outReal
	 * @param outImag
	 */
	@SuppressWarnings("unused")
	private void calcDFT(Vector<Double> inReal, Vector<Double> inImag,
			Vector<Double> outReal, Vector<Double> outImag) {
		int N = inReal.size();
		for (int k = 0; k < N; k++) { // output points
			double sumReal = 0;
			double sumImag = 0;
			for (int n = 0; n < N; n++) { // input points
				double cos = Math.cos(2 * Math.PI * n * k / N);
				double sin = Math.sin(2 * Math.PI * n * k / N);
				sumReal += inReal.get(n) * cos + inImag.get(n) * sin;
				sumImag += -inReal.get(n) * sin + inImag.get(n) * cos;
			}
			outReal.add(sumReal);
			outImag.add(sumImag);
		}
	}

	/**
	 * This method calculates the DFT of a real number
	 * 
	 * @param inReal
	 * @param inImag
	 * @param outReal
	 * @param outImag
	 */
	@SuppressWarnings("unused")
	private void calcDFT(Vector<Double> inReal, Vector<Double> outReal,
			Vector<Double> outImag) {
		int N = inReal.size();
		for (int k = 0; k < N; k++) { // output points
			double sumReal = 0;
			double sumImag = 0;
			for (int n = 0; n < N; n++) { // input points
				double cos = Math.cos(2 * Math.PI * n * k / N);
				double sin = Math.sin(2 * Math.PI * n * k / N);
				sumReal += inReal.get(n) * cos;
				sumImag += -inReal.get(n) * sin;
			}
			outReal.add(sumReal);
			outImag.add(sumImag);
		}
	}

	/**
	 * This method calculates the DFT of a real numbered auto correlation
	 * function according to DHM Method Caccia et al 1997 Equ 6a
	 * 
	 * @param inReal
	 * @return signalS
	 */
	@SuppressWarnings("unused")
	private Vector<Double> calcDFTofAC_Equ6a(Vector<Double> inReal) {
		int M = inReal.size();
		Vector<Double> signalS = new Vector<Double>();
		for (int j = 0; j <= M / 2; j++) { // Sj
			double sumReal1 = 0;
			double sumImag1 = 0;
			double sumReal2 = 0;
			double sumImag2 = 0;
			for (int t = 0; t <= M / 2; t++) { // Sum1
				double cos = Math.cos(2 * Math.PI * j * t / M);
				// double sin = Math.sin(2*Math.PI * j * t / M);
				sumReal1 += inReal.get(t) * cos;
				// sumImag1 += -inReal.get(t) * sin;
			}
			for (int t = M / 2 + 1; t <= M - 1; t++) { // Sum2
				double cos = Math.cos(2 * Math.PI * j * t / M);
				// double sin = Math.sin(2*Math.PI * j * t / M);
				sumReal2 += inReal.get(M - t) * cos;
				// sumImag2 += -inReal.get(M-t) * sin;
			}
			signalS.add(sumReal1 + sumReal2);
			// signalS.add(sumReal1 + sumReal2 + (M/2 - j));???????????????
			// signalS.add(sumImag1 + sumImag2);
			this.fireProgressChanged((int)(j+1)*30/(M/2) +10);
			if (this.isCancelled(this.getParentTask())) return null;
		}
		return signalS;
	}

	private boolean checkIfDataPointsAreNotNegative(Vector<Double> signal) {
		boolean allDataPointsAreNotNegative = true;
		for (int i = 0; i < signal.size(); i++) {
			if (signal.get(i) < 0) {
				BoardPanel
						.appendTextln("PlotOpFractalGenerator: Signal has negative values, which is not allowed");
				allDataPointsAreNotNegative = false;
			}
		}
		return allDataPointsAreNotNegative;
	}

	private Vector<Complex> calcRSA_Equ6be(Vector<Double> signalS) {
		Vector<Complex> signalV = new Vector<Complex>();
		Vector<Double> signalW = new Vector<Double>(); // Gaussian random
														// variables
		int M = (signalS.size() - 1) * 2;

		// generate random variables
		Random generator = new Random();
		// RandomData generator = new RandomDataImpl();
		double stdDev = 1.0d;
		double mean = 0.0d;
		for (int i = 0; i < M; i++) {
			signalW.add(generator.nextGaussian() * stdDev + mean);
			// signalW.add(generator.nextGaussian(mean, stdDev));
		}

		// calculate randomized spectral amplitudes

		// set first value Equ6b
		Complex complex = new Complex();
		complex.setReal(Math.sqrt(signalS.get(0)) * signalW.get(0));
		complex.setImag(0.0d);
		// set next values Equ6c
		signalV.add(complex);
		for (int k = 1; k < M / 2; k++) {
			complex = new Complex();
			complex.setReal(Math.sqrt(1.0d / 2.0d * signalS.get(k)) * signalW.get(2 * k - 1));
			complex.setImag(Math.sqrt(1.0d / 2.0d * signalS.get(k)) * signalW.get(2 * k));
			signalV.add(complex);
			this.fireProgressChanged((int)(k)*10/(M/2) + 40);
			if (this.isCancelled(this.getParentTask())) return null;
		}
		// set middle value Equ6d
		complex = new Complex();
		complex.setReal(Math.sqrt(signalS.get(M / 2)) * signalW.get(M - 1));
		complex.setImag(0.0d);
		signalV.add(complex);

		// set next values Equ6e
		for (int k = M / 2 + 1; k <= (M - 1); k++) {
			signalV.add((signalV.get(M - k)).conjugate());
		}
		return signalV;
	}

	/**
	 * This method calculates the simulated time series
	 * 
	 * @param signalV
	 * @return the simulated signal
	 */
	private Vector<Double> calcDFTofRSA_Equ6f(Vector<Complex> signalV) {
		Vector<Double> signalY = new Vector<Double>();

		int M = signalV.size();

		for (int k = 0; k < M / 2; k++) { // half of output points
			double sumReal = 0;
			// double sumImag = 0;
			for (int n = 0; n < M; n++) { // input points
				double cos = Math.cos(2 * Math.PI * n * k / M);
				double sin = Math.sin(2 * Math.PI * n * k / M);
				Complex complex = signalV.get(n);
				sumReal += complex.getReal() * cos + complex.getImag() * sin;
				// sumImag += -complex.getReal() * sin + complex.getImag() *
				// cos; //Imag is always 0
			}
			signalY.add(1.0d / Math.sqrt(M) * sumReal);
			this.fireProgressChanged((int)(k+1)*30/(M/2) +50);
			if (this.isCancelled(this.getParentTask())) return null;
		}

		return signalY;
	}

	/**
	 * This method calculates the power spectrum of a signal
	 * 
	 * @param signal
	 * @return the power spectrum of the signal
	 */
	@SuppressWarnings("unused")
	private Vector<Double> calcPowerSpectrum(Vector<Double> signal) {
		Vector<Double> signalPower = new Vector<Double>();
		Vector<Double> signalPhase = new Vector<Double>();
		int N = signal.size();
		for (int k = 0; k < N; k++) { // output points
			double sumReal = 0;
			double sumImag = 0;
			for (int n = 0; n < N; n++) { // input points
				double cos = Math.cos(2 * Math.PI * n * k / N);
				double sin = Math.sin(2 * Math.PI * n * k / N);
				sumReal += signal.get(n) * cos;
				sumImag += -signal.get(n) * sin;
			}
			Complex complex = new Complex(sumReal, sumImag);
			// complex.setReal(sumReal);
			// complex.setImag(sumImag);

			// double magnitude = complex.abs();
			double power = complex.squareAbs();
			double phase = complex.argRad();
			signalPower.add(power);
			signalPhase.add(phase);
		}
		return signalPower;
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

		// PlotModel plotModel = (PlotModel) pb.getObjectParameter(0);
		int method   = pb.getIntParameter("method"); // 0 DHM, 1 SSM, 2
		double hurst = pb.getDoubleParameter("hurst");
		int powerN   = pb.getIntParameter("powerNDataPoints");
		int fGnTofBm = pb.getIntParameter("fGnTofBm");
		int fBmTofGn = pb.getIntParameter("fBmTofGn");

		int numDataPoints     = (int) Math.pow(2, powerN);
		Vector<Double> signal = new Vector<Double>();
		Vector<Double> range  = new Vector<Double>();
		
		String type = "";

		if (method == 0) {// DHM
			type = "DHM";
			int M = numDataPoints * 2;
			Vector<Double> signalAC = new Vector<Double>();
			Vector<Double> signalS  = new Vector<Double>();
			Vector<Complex> signalV = new Vector<Complex>();

			// generate auto covariance signal
			signalAC = generateAC(M, hurst);
		
			fireProgressChanged(10);
			if (isCancelled(getParentTask())) return null;
			
			signalS = this.calcDFTofAC_Equ6a(signalAC); // Imaginary signal strictly not needed, it is always 0

			
			boolean allDataPointsAreNotNegative = this.checkIfDataPointsAreNotNegative(signalS);
			if (allDataPointsAreNotNegative) {
				signalV = this.calcRSA_Equ6be(signalS);
					
				signal  = this.calcDFTofRSA_Equ6f(signalV);

				// take another signal
				// signal = signalAC;

			} else {
				// output nothing
			}

			// take another signal
			// signal = signalS;
			// signal = new Vector<Double>();
			// for (int i = 0; i < signalV.size(); i++){
			// signal.add(signalV.get(i).getReal());
			// }

			// set x-axis data points
			
			fireProgressChanged(85);
			if (isCancelled(getParentTask())) return null;
			
			for (int i = 0; i < signal.size(); i++) {
				range.add((double) i + 1.0d);
			}
			
			fireProgressChanged(90);
			if (isCancelled(getParentTask())) return null;
			
			if (fGnTofBm == 1) {
				signal = this.convert_fGnTofBm(signal);
			}
			
			fireProgressChanged(95);
			if (isCancelled(getParentTask())) return null;

		}
		if (method == 1) {// SSM
			type = "SSM";
			int M = (2 * numDataPoints) * 4; // 2* because of DFT halves the number, *2 or *4 is not so memory intensive than *8 as suggested by Caccia et al 1997
			double beta = 2.0d * hurst + 1.0d;

			// generate random phase variables [0, 2pi]
			Vector<Double> signalPhase = new Vector<Double>();
			Random generator = new Random();
			for (int i = 0; i < M; i++) {
				signalPhase.add(generator.nextDouble() * 2.0d * Math.PI);// -1.0d*Math.PI);
			}

			fireProgressChanged(5);
			if (isCancelled(getParentTask())) return null;
			
			// set 1/fbeta power spectrum
			Vector<Double> signalPower = new Vector<Double>();
			for (int i = 0; i < M; i++) {
				// signalPower.set(i, signalPower.get(i)*(1.0d/Math.pow(i+1, beta)));
				signalPower.add(1.0d / Math.pow(i + 1, beta));
			}
			
			fireProgressChanged(10);
			if (isCancelled(getParentTask())) return null;

			// calculate invers FFT
			Vector<Double> signalReal = new Vector<Double>();
			// Vector<Double> signalImag = new Vector<Double>();
			int N = signalPower.size();
			for (int k = 0; k < N / 2; k++) { // output points
				double sumReal = 0;
				double sumImag = 0;
				for (int n = 0; n < N; n++) { // input points
					double cos = Math.cos(2 * Math.PI * n * k / N);
					double sin = Math.sin(2 * Math.PI * n * k / N);
					double real = Math.sqrt(signalPower.get(n)) * Math.cos(signalPhase.get(n));
					double imag = Math.sqrt(signalPower.get(n)) * Math.sin(signalPhase.get(n));
					sumReal += real * cos + imag * sin;
					// sumImag += -real * sin + imag * cos;
				}
				signalReal.add(sumReal);
				// signalImag.add(sumImag);
				this.fireProgressChanged((int)(k+1)*80/(N/2)+10);
				if (this.isCancelled(this.getParentTask())) return null;
			}
			signal = signalReal;

			fireProgressChanged(90);
			if (isCancelled(getParentTask())) return null;
			
			// eliminate data points which are too much
			if (signal.size() > numDataPoints) {
				for (int i = 0; i < (signal.size() - numDataPoints) / 2; i++) {
					signal.remove(0);
				}
				while (signal.size() > numDataPoints) {
					signal.remove(signal.size() - 1);
				}
			}
			
			fireProgressChanged(94);
			if (isCancelled(getParentTask())) return null;
			
			// set x-axis data points
			for (int i = 0; i < signal.size(); i++) {
				range.add((double) i + 1.0d);
			}
			
			fireProgressChanged(97);
			if (isCancelled(getParentTask())) return null;
			
			if (fBmTofGn == 1) {
				signal = this.convert_fBmTofGn(signal);
			}
		}

		// it is necessary to generate a new instance of PlotModel
		String rangeHeader = null;
		String rangeUnit   = null;
		String dataHeader  = null;
		String dataUnit    = null;

		rangeHeader = "Data points";
		rangeUnit   = "a.u.";

		dataHeader = "Value";
		dataUnit   = "a.u.";

		PlotModel plotModelNew = new PlotModel(rangeHeader, rangeUnit, dataHeader, dataUnit, range, signal);
		plotModelNew.setModelName("Fractal Signal" + (type.equals("")?"":" "+type));
		
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(plotModelNew);
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new PlotOpFractalGeneratorDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpFractalGeneratorDescriptor.TYPE;
	}
}
