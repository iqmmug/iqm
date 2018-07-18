package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: Higuchi.java
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
 * Fractal Dimension using Higuchi Dimension see T. Higuchi, Physica D 31, 1988,
 * 277 and T. Higuchi, Physica D 46, 1990, 254 see also W.Klonowski, E.
 * Olejarczyk, R. Stepien, P. Jalowiecki and R. Rudner, Monitoring The Depth Of
 * Anaesthesia Using Fractal Complexity Method,333-342 in:Complexus Mundi:
 * Emergent Patterns In Nature, M.M. Novak ed., World Scientific Publishing,
 * 2006
 * <p>
 * <b>Changes</b>
 * <ul>
 * <li>2011 08 17 eliminated bug - plot only for Dh > 1.5
 * </ul>
 * 
 * 
 * @author Helmut Ahammer
 * @since 2010 02
 */
public class Higuchi {

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
	public Higuchi(AbstractOperator operator) {
		this.operator = operator;
	}

	/**
	 * This is the standard constructor
	 */
	public Higuchi() {

	}

	/**
	 * This method calculates the "Lengths" of the series
	 * 
	 * @param data
	 *            1D data vector
	 * @param k
	 *            number of newly calculated time series, k must be smaller than
	 *            the total number of time points k should not be greater than
	 *            N/3 (N number of data points)!
	 * @return Vector L[k] "lengths"
	 */
	public Vector<Double> calcLengths(Vector<Double> data, int k) {
		int N = data.size();
		if (k > N) {
			k = N / 3;
			BoardPanel.appendTextln("Higuchi parameter k too large, automatically set to data length/3");
		}

		// double[] L = new double[k];
		Vector<Double> L = new Vector<Double>();
		for (int kk = 1; kk <= k; kk++) {
			double[] Lmk = new double[kk];
			L.add(0.0d);
			for (int m = 1; m <= kk; m++) {
				double norm = (N - 1) / Math.floor((double) (N - m) / (double) kk) / kk / kk;
				// double[] TimeSerArr = new double[(N-m)/kk];
				// TimeSerArr[0] = data[m-1];
				int i = 1;
				while (i <= (N - m) / kk) {
					// System.out.println("Higuchi i:" + i +" m:"+ m +" k:"+kk);
					// TimeSerArr[i] = data[m-1+i*kk];
					Lmk[m - 1] += Math
							.abs(data.get(m - 1 + i * kk).doubleValue() - data.get(m - 1 + (i - 1) * kk).doubleValue())
							* norm;
					i = i + 1;
				}
				// Lmk[m-1] =
				// FLOAT(TOTAL(ABS(REFORM(TimeSerArr[0:*])-([REFORM(TimeSerArr[0]),
				// REFORM(TimeSerArr[0:*])])))) * FLOAT((N-1))
				// /FLOAT(FLOAT(N-m)/kk) / FLOAT(kk) /FLOAT(kk)
				// L.set(kk-1, L.get(kk-1).doubleValue() + Lmk[m-1]);
			}
			// L[kk-1] = FLOAT(TOTAL(Lmk))/ FLOAT(kk)
			// L[kk-1] = L[kk-1] / (double) kk;

			// Compute mean:
			double mean = 0.0;
			for (int m = 1; m <= kk; m++) {
				mean = mean + Lmk[m - 1];
			}
			mean = mean / kk;

			// //Optional Compute Median
			// double median = 0.0;
			// ArrayList<Double> severalLenghts = new ArrayList<Double>();
			// for (int m = 1; m <= kk; m++){
			// severalLenghts.add(Lmk[m-1]);
			// }
			// if (severalLenghts.size() > 0) {
			// Collections.sort(severalLenghts);
			// int middleOfInterval = (severalLenghts.size()) / 2;
			// if (severalLenghts.size() % 2 == 0) {
			// median = (severalLenghts.get(middleOfInterval)
			// + severalLenghts.get(middleOfInterval - 1)) / 2;
			// } else {
			// median = severalLenghts.get(middleOfInterval);
			// }
			// }

			L.set(kk - 1, mean);
			// L.set(kk-1, L.get(kk-1).doubleValue() /kk);
			if (operator != null) {
				this.operator.fireProgressChanged(kk * (progressBarMax - progressBarMin) / k + progressBarMin);
				if (this.operator.isCancelled(this.operator.getParentTask()))
					return null;
			}
		}
		return L;
	}

	/**
	 * 
	 * @param L
	 *            Higuchi Lengths
	 * @param regStart
	 * @param regEnd
	 * @return double Dh
	 */
	public double[] calcDimension(Vector<Double> L, int regStart, int regEnd, String plotName, boolean showPlot,
			boolean deleteExistingPlot) {
		// double[] lnDataY = new double[L.size()];
		// double[] lnDataX = new double[L.size()]; //k
		lnDataY = new Vector<Double>();
		lnDataX = new Vector<Double>(); // k
		for (int i = 0; i < L.size(); i++) {
			if (L.get(i) == 0)
				L.set(i, Double.MIN_VALUE);
		}
		// System.out.println("Higuchi: lnk ln(L)");
		BoardPanel.appendTextln("Higuchi: lnk        ln(L)"); // Eliminate this
																// line for Alex
																// (auch noch in
																// PlotOpFracHiguchi
																// Zeile 126)
		for (int i = 0; i < L.size(); i++) {
			double lnX = Math.log(i + 1);
			double lnY = Math.log(L.get(i).doubleValue());
			lnDataX.add(lnX);
			lnDataY.add(lnY);
			// System.out.println(lnX + " " + lnY);
			BoardPanel.appendTextln(lnX + "        " + lnY); // Eliminate this
																// line for Alex
																// (auch noch in
																// PlotOpFracHiguchi
																// Zeile 126)
		}
		double[] p = PlotTools.getLinearRegression(lnDataX, lnDataY, regStart, regEnd);

		if (deleteExistingPlot) {
			PlotTools.deleteExistingPlot();
		}

		if (showPlot) {
			// if (-p[1] > 1.5) {????
			boolean isLineVisible = false;
			PlotTools.displayRegressionPlotXY(lnDataX, lnDataY, isLineVisible, plotName, "Higuchi Dimension", "ln(k)",
					"ln(L)", regStart, regEnd, deleteExistingPlot);
		}

		double[] out = { p[1], p[3], p[4], p[5] }; // -slope = Dh, standard
													// deviation, r2 , adjusted
													// r2
		return out;
	}

}// END
