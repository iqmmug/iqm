package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpStatistics.java
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
import at.mug.iqm.plot.bundle.descriptors.PlotOpStatisticsDescriptor;
import flanagan.analysis.Stat;

/**
 * @author Ahammer, Mayrhofer
 * @since   2012 11
 */
public class PlotOpStatistics extends AbstractOperator {

	public PlotOpStatistics() {
	}

	/**
	 * this method calculates a normalized histogram using flanagans library the
	 * histogram is mainly used to calculate entropies
	 * 
	 * @param s
	 * @return double[] histogram
	 */
	private double[] calcNormHistogram(Stat st) {
		double min = st.minimum_as_double();
		double max = st.maximum_as_double();
		int numb = st.length();
		double binWidth = (max - min) / numb;
		if (max == min) { // constant signal is for histogram a problem
			max = max + 1.0d;
			min = min - 1.0d;
			binWidth = 1.0d;
		}
		// double[][] histo = st.histogramBins(st.array_as_double(), binWidth);
		double[][] histo = Stat.histogramBins(st.array_as_double(), binWidth,
				min, max);
		// histo[0][] ...center of bin values (x-values of histogram)
		// histo[1][]... number of occurences (y-values of histogram)

		// get sum
		double sum = 0.0d;
		for (int i = 0; i < histo[1].length; i++) {
			sum = sum + histo[1][i];
		}

		// Normalize
		double[] normHisto = new double[histo[1].length];
		for (int i = 0; i < histo[1].length; i++) {
			normHisto[i] = histo[1][i] / sum;
		}

		// avoid 0 entries by adding a small value
		for (int i = 0; i < normHisto.length; i++) {
			if (normHisto[i] == 0)
				normHisto[i] = Double.MIN_VALUE;
		}

		// //avoid 0 entries by eliminating them
		// //result is quite identical to setting zeros to Double.MIN_VALUE
		// //calulate number of nonzero values
		// int numNonZeroValues = 0;
		// for (int i =0; i < normHisto.length; i++){
		// if (normHisto[i] != 0) numNonZeroValues = numNonZeroValues +1;
		// }
		// double[] newNormHisto = new double[numNonZeroValues];
		// int j = 0;
		// for (int i = 0; i < normHisto.length; i++){
		// if (normHisto[i] != 0) {
		// newNormHisto[j] = normHisto[i];
		// j = j + 1;
		// }
		// }

		// //test sum, should be 1
		// sum = 0.0d;
		// for (int i = 0; i < normHisto.length; i++){
		// sum = sum + normHisto[i];
		// }
		return normHisto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlotModel plotModel = ((IqmDataBox) pb.getSource(0)).getPlotModel();
		int method            = pb.getIntParameter("method");
		int boxLength         = pb.getIntParameter("boxLength");
		int calcNumDataPoints = pb.getIntParameter("calcNDataPoints");
		int calcMin           = pb.getIntParameter("calcMin");
		int calcMax           = pb.getIntParameter("calcMax");
		int calcMean          = pb.getIntParameter("calcMean");
		int calcMedian        = pb.getIntParameter("calcMedian");
		int calcStdDev        = pb.getIntParameter("calcStdDev");
		int calcKurt          = pb.getIntParameter("calcKurtosis");
		int calcSkew          = pb.getIntParameter("calcSkewness");
		int calcShannonEn     = pb.getIntParameter("calcShannonEn");

		String plotModelName = plotModel.getModelName();
		
		// new instance is not essential, because output is not a vector
		// Vector<Double> signal = new Vector<Double>(plotModel.getData());
		@SuppressWarnings("rawtypes")
		Vector signal = plotModel.getData();
		
		@SuppressWarnings("rawtypes")
		Vector domain = plotModel.getDomain();
		
		
		//number of values	
		int numValues = 1;
		if (method == 0)  numValues = 1; // Single values
		if (method == 1)  numValues = signal.size() - boxLength + 1;//Gliding Box values

		double[] normHisto = null;

		double startDummy    = 99999.0d;
		double numDataPoints = startDummy;
		double min           = startDummy;
		double minPosition	 = startDummy;		
		double max           = startDummy;
		double maxPosition	 = startDummy;
		double mean          = startDummy;
		double median        = startDummy;
		double stdDev        = startDummy;
		double kurt          = startDummy;
		double skew          = startDummy;
		double shannonEn     = startDummy;
		
		//int startDummy = 999999;
		int colNr_NumDataPoints = (int)startDummy;
		int colNr_Min           = (int)startDummy;
		int colNr_MinPosition	= (int)startDummy;
		int colNr_Max           = (int)startDummy;
		int colNr_MaxPosition	= (int)startDummy;
		int colNr_Mean          = (int)startDummy;
		int colNr_Median        = (int)startDummy;
		int colNr_StdDev        = (int)startDummy;
		int colNr_Kurt          = (int)startDummy;
		int colNr_Skew          = (int)startDummy;
		int colNr_ShannonEn     = (int)startDummy;


		// //get an Apache DescriptiveStatistics instance
		// DescriptiveStatistics stats = new DescriptiveStatistics();
		// //add the data from the vector
		// for( int i = 0; i < signal.size(); i++) {
		// stats.addValue((Double) signal.get(i));
		// }

		// create model and Table
		TableModel model = new TableModel("Plot Statistics");

		model.addColumn("Plot");
		
		if (calcNumDataPoints == 1) {
			colNr_NumDataPoints = model.getColumnCount();
			model.addColumn("#");		
		}
		if (calcMin == 1) {
			colNr_Min = model.getColumnCount();
			model.addColumn("Min");
			colNr_MinPosition = model.getColumnCount();
			model.addColumn("Position (x-value) of (first) Min");
		}
		if (calcMax == 1) {
			colNr_Max = model.getColumnCount();
			model.addColumn("Max");
			colNr_MaxPosition = model.getColumnCount();
			model.addColumn("Position (x-value) of (first) Max");
			
		}
		if (calcMean == 1) {
			colNr_Mean = model.getColumnCount();
			model.addColumn("Mean");
			
		}
		if (calcMedian == 1) {
			colNr_Median = model.getColumnCount();
			model.addColumn("Median");
			
		}
		if (calcStdDev == 1) {
			colNr_StdDev = model.getColumnCount();
			model.addColumn("StdDev");
		}
		if (calcKurt == 1) {
			colNr_Kurt = model.getColumnCount();
			model.addColumn("Kurtosis");
			
		}
		if (calcSkew == 1) {
			colNr_Skew = model.getColumnCount();
			model.addColumn("Skewness");
			
		}
		if (calcShannonEn == 1) {
			colNr_ShannonEn = model.getColumnCount();
			model.addColumn("Shannon entropy");
			
		}
		
		if (method == 0) { //single value
			
			model.addRow(new String[] { plotModelName });
	
			// get a flanagan Stats instance
			Stat st = new Stat(signal);	
			// get statistics
			if (calcNumDataPoints == 1) {
				// numDataPoints = stats.getN();
				numDataPoints = st.length();
				model.setValueAt(numDataPoints, 0, colNr_NumDataPoints);
			}
			this.fireProgressChanged(10);
			if (this.isCancelled(this.getParentTask())) return null;
			
			if (calcMin == 1) {
				// min = stats.getMin();
				min = st.minimum_as_double();
				model.setValueAt(min, 0,  colNr_Min);
				minPosition =  (double)domain.get(st.minimumIndex());
				model.setValueAt(minPosition, 0, colNr_MinPosition);
			}
			this.fireProgressChanged(20);
			if (this.isCancelled(this.getParentTask())) return null;
			
			if (calcMax == 1) {
				// max = stats.getMax();
				max = st.maximum_as_double();
				model.setValueAt(max, 0,  colNr_Max);
				maxPosition =  (double)domain.get(st.maximumIndex());
				model.setValueAt(maxPosition, 0, colNr_MaxPosition);
			}
			this.fireProgressChanged(30);
			if (this.isCancelled(this.getParentTask())) return null;
			
			if (calcMean == 1) {
				// mean = stats.getMean();
				mean = st.mean_as_double();
				// mean = this.calcMean(signal);
				model.setValueAt(mean, 0,  colNr_Mean);
			}
			this.fireProgressChanged(40);
			if (this.isCancelled(this.getParentTask())) return null;
			
			if (calcMedian == 1) {
				// median = stats.getPercentile(50);
				median = st.median_as_double();
				model.setValueAt(median, 0,  colNr_Median);
			}
			this.fireProgressChanged(50);
			if (this.isCancelled(this.getParentTask())) return null;
			
			if (calcStdDev == 1) {
				// stdDev = stats.getStandardDeviation();
				stdDev = st.standardDeviation_as_double();
				model.setValueAt(stdDev, 0,  colNr_StdDev);
			}
			this.fireProgressChanged(60);
			if (this.isCancelled(this.getParentTask())) return null;
			
			if (calcKurt == 1) {
				// kurt = stats.getKurtosis();
				kurt = st.kurtosis_as_double();
				model.setValueAt(kurt, 0,  colNr_Kurt);
			}
			this.fireProgressChanged(70);
			if (this.isCancelled(this.getParentTask())) return null;
			
			if (calcSkew == 1) {
				// skew = stats.getSkewness();
				skew = st.momentSkewness_as_double();
				model.setValueAt(skew, 0,  colNr_Skew);
			}
			this.fireProgressChanged(80);
			if (this.isCancelled(this.getParentTask())) return null;
			
			if (calcShannonEn == 1) {
				if (normHisto == null) {
					normHisto = this.calcNormHistogram(st);
				}
				shannonEn = Stat.shannonEntropyNat(normHisto);
				model.setValueAt(shannonEn, 0,  colNr_ShannonEn);
			}
			this.fireProgressChanged(90);
			if (this.isCancelled(this.getParentTask())) return null;
		
		}
		if (method == 1) { //gliding values
			for (int i = 0; i < numValues; i++){
			
				model.addRow(new String[] { plotModelName + " Box #: " + (i+1) });
				
				this.fireProgressChanged( (int)((float)i/(float)numValues * 100.0f));
				if (this.isCancelled(this.getParentTask())) return null;
				
				@SuppressWarnings("rawtypes")
				Vector subSignal = new Vector();
				
				@SuppressWarnings("rawtypes")
				Vector subDomain = new Vector();
				
				for (int ii = i; ii < i+boxLength; ii++){ // get subvector
					subSignal.add((Double) signal.get(ii));
					subDomain.add((Double) domain.get(ii));
				}			
				// get a flanagan Stats instance
				Stat st = new Stat(subSignal);	
			
				// get statistics
				if (calcNumDataPoints == 1) {
					// numDataPoints = stats.getN();
					numDataPoints = st.length();
					model.setValueAt(numDataPoints, i, colNr_NumDataPoints);
				}		
				if (calcMin == 1) {
					// min = stats.getMin();
					min = st.minimum_as_double();
					model.setValueAt(min, i,  colNr_Min);
					@SuppressWarnings("unused")
					int ixMin = st.minimumIndex();
					minPosition =  (double)subDomain.get(st.minimumIndex());
					model.setValueAt(minPosition, i, colNr_MinPosition);
				}
				if (calcMax == 1) {
					// max = stats.getMax();
					max = st.maximum_as_double();
					model.setValueAt(max, i,  colNr_Max);
					maxPosition =  (double)subDomain.get(st.maximumIndex());
					model.setValueAt(maxPosition, i, colNr_MaxPosition);
				}
				if (calcMean == 1) {
					// mean = stats.getMean();
					mean = st.mean_as_double();
					// mean = this.calcMean(signal);
					model.setValueAt(mean, i,  colNr_Mean);
				}
				if (calcMedian == 1) {
					// median = stats.getPercentile(50);
					median = st.median_as_double();
					model.setValueAt(median, i,  colNr_Median);
				}
				if (calcStdDev == 1) {
					// stdDev = stats.getStandardDeviation();
					stdDev = st.standardDeviation_as_double();
					model.setValueAt(stdDev, i,  colNr_StdDev);
				}
				if (calcKurt == 1) {
					// kurt = stats.getKurtosis();
					kurt = st.kurtosis_as_double();
					model.setValueAt(kurt, i,  colNr_Kurt);
				}	
				if (calcSkew == 1) {
					// skew = stats.getSkewness();
					skew = st.momentSkewness_as_double();
					model.setValueAt(skew, i,  colNr_Skew);
				}
				if (calcShannonEn == 1) {
					if (normHisto == null) {
						normHisto = this.calcNormHistogram(st);
					}
					shannonEn = Stat.shannonEntropyNat(normHisto);
					model.setValueAt(shannonEn, i,  colNr_ShannonEn);
				}
				if (this.isCancelled(this.getParentTask())) return null;
			}
		
		}




//		// format column widths
//		int numColumns = model.getColumnCount();
//
//		for (int i = 0; i < numColumns; i++) {
//			jTable.getColumnModel().getColumn(i).setPreferredWidth(140);
//			if ((model.getColumnName(i) == "?")
//					|| (model.getColumnName(i) == "?")
//					|| (model.getColumnName(i) == "?")) {
//				jTable.getColumnModel().getColumn(i).setPreferredWidth(40);
//			}
//			if ((model.getColumnName(i) == "??")
//					|| (model.getColumnName(i) == "??")
//					|| (model.getColumnName(i) == "??")
//					|| (model.getColumnName(i) == "??")) {
//				jTable.getColumnModel().getColumn(i).setPreferredWidth(100);
//			}
//		}
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(model);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new PlotOpStatisticsDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpStatisticsDescriptor.TYPE;
	}

}
