package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpEntropy.java
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

import org.apache.commons.math3.util.ArithmeticUtils;

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
import at.mug.iqm.commons.util.plot.AppEntropy;
import at.mug.iqm.commons.util.plot.PEntropy;
import at.mug.iqm.commons.util.plot.SampleEntropy;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpEntropyDescriptor;

/**
 * <li>2012 11 QSE   quadratic sample entropy      according to Lake Moormann Am J Physiol Circ Physiol 300 H319-H325 2011
 * <li>2012 11 COSEn coefficient of sample entropy according to Lake Moormann Am J Physiol Circ Physiol 300 H319-H325 2011
 * <li>2012 02 Permutation entropy according to Bandt C and Pompe B. Permutation Entropy: A Natural Complexity Measure for Time Series. Phys Rev Lett Vol88(17) 2002
 * 
 * @author Ahammer
 * @since   2012 11
 * 
 */
public class PlotOpEntropy extends AbstractOperator {

	public PlotOpEntropy() {
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
		int typeSurr         = pb.getIntParameter("typeSurr");
		int nSurr            = pb.getIntParameter("nSurr");
		int calcApEn         = pb.getIntParameter("calcApEn");
		int calcSampEn       = pb.getIntParameter("calcSampEn");
		int calcQSE          = pb.getIntParameter("calcQSE");
		int calcCOSEn        = pb.getIntParameter("calcCOSEn");
		int calcPEn          = pb.getIntParameter("calcPEn");
		int apEnParam_M      = pb.getIntParameter("apEnParam_M");
		double apEnParam_R   = pb.getDoubleParameter("apEnParam_R");
		int apEnParam_D      = pb.getIntParameter("apEnParam_D");
		int sampEnParam_M    = pb.getIntParameter("sampEnParam_M");
		double sampEnParam_R = pb.getDoubleParameter("sampEnParam_R");
		int sampEnParam_D    = pb.getIntParameter("sampEnParam_D");
		int pEnParam_M       = pb.getIntParameter("pEnParam_M");
		//double pEnParam_R  = pb.getDoubleParameter("pEnParam_R");
		int pEnParam_D       = pb.getIntParameter("pEnParam_D");

		//System.out.println("typeSurr:  " + typeSurr);
		//System.out.println("nSurr:  " + nSurr);
				
		String plotModelName = plotModel.getModelName();
		
		// new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());

	    int numDhValues = 1;
		
		//number of entropy values depends on single or gliding option
		if (method == 0)  numDhValues = 1; // Single value
		if (method == 1)  numDhValues = signal.size() - boxLength + 1;	//Gliding Box
		
		if ((method == 1) && (numDhValues < 1)) { // check for gliding method
			DialogUtil.getInstance().showDefaultErrorMessage("Entropies cannot be calculated because gliding box length is too large");
			boxLength = signal.size()/2;
			numDhValues = signal.size() - boxLength + 1;
			DialogUtil.getInstance().showDefaultErrorMessage("Box length set to: " + boxLength);
			//return null;
		}

		double[] apEn   = new double[numDhValues]; // for single Dh value only the first element is used
		double[] sampEn = new double[numDhValues];
		double[] qse    = new double[numDhValues];
		double[] cosEn  = new double[numDhValues];
		double[] pEn    = new double[numDhValues];
		
		Vector<Double> apEnSurr   = new Vector<Double>(); //May be used to store individual surrogate entropy values, not for gliding option
		Vector<Double> sampEnSurr = new Vector<Double>();
		Vector<Double> qseSurr    = new Vector<Double>();
		Vector<Double> cosEnSurr  = new Vector<Double>();
		Vector<Double> pEnSurr    = new Vector<Double>();
		double apEnSurrMean   = 0.0;
		double sampEnSurrMean = 0.0;
		double qseSurrMean    = 0.0;
		double cosEnSurrMean  = 0.0;
		double pEnSurrMean    = 0.0;
		
		fireProgressChanged(5);
		if (isCancelled(getParentTask())) return null;
		
		if (method == 0) { // single value
			//if (typeSurr == -1) {  //no surrogate
				if (calcApEn == 1) {
					AppEntropy appEntropy = new AppEntropy();
					apEn[0] = appEntropy.calcAppEntropy(signal, apEnParam_M, apEnParam_R, apEnParam_D);
					fireProgressChanged(30);
					if (isCancelled(getParentTask())) return null;
				}
				if ((calcSampEn == 1) || (calcQSE == 1) || (calcCOSEn == 1)) {
					SampleEntropy sampleEntropy = new SampleEntropy();
					sampEn[0] = sampleEntropy.calcSampleEntropy(signal, sampEnParam_M, sampEnParam_R, sampEnParam_D);
					fireProgressChanged(50);
					if (isCancelled(getParentTask())) return null;
				}
				if (calcQSE == 1) {
					qse[0] = sampEn[0] + Math.log(2 * sampEnParam_R);
				}
				if (calcCOSEn == 1) {
					cosEn[0] = sampEn[0] - Math.log(sampEnParam_R) - this.calcMean(signal);
				}
				fireProgressChanged(70);
				if (isCancelled(getParentTask())) return null;
				if (calcPEn == 1) {
					PEntropy pEntropy = new PEntropy();
					pEn[0] = pEntropy.calcPEntropy(signal, pEnParam_M, pEnParam_D);
					// pEn_Pre[0] = pEntropy.calcPEntropy(signal, param_M-1,
					// param_D);
				}
				fireProgressChanged(80);
				if (isCancelled(getParentTask())) return null;
			//}
			if (typeSurr >= 0) { //Surrogates before computing entropies
		
				for (int n= 0; n < nSurr; n++) {
					
					//create a surrogate signal
					Surrogate surrogate = new Surrogate(this);
					Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(signal, typeSurr, 1);
					Vector<Double> signalSurr = plots.get(0);

					if (calcApEn == 1) {
						AppEntropy appEntropy = new AppEntropy();
						apEnSurr.add(appEntropy.calcAppEntropy(signalSurr, apEnParam_M, apEnParam_R, apEnParam_D));
						fireProgressChanged(30);
						if (isCancelled(getParentTask())) return null;
					}
					double se = 0.0; //helper variable
					if ((calcSampEn == 1) || (calcQSE == 1) || (calcCOSEn == 1)) {
						SampleEntropy sampleEntropy = new SampleEntropy();
						se = sampleEntropy.calcSampleEntropy(signalSurr, sampEnParam_M, sampEnParam_R, sampEnParam_D);
						sampEnSurr.add(se);
						fireProgressChanged(50);
						if (isCancelled(getParentTask())) return null;
					}
					if (calcQSE == 1) {
						qseSurr.add(se + Math.log(2 * sampEnParam_R));
					}
					if (calcCOSEn == 1) {
						cosEnSurr.add(se - Math.log(sampEnParam_R) - this.calcMean(signalSurr));
					}
					fireProgressChanged(70);
					if (isCancelled(getParentTask())) return null;
					if (calcPEn == 1) {
						PEntropy pEntropy = new PEntropy();
						pEnSurr.add(pEntropy.calcPEntropy(signalSurr, pEnParam_M, pEnParam_D));
						// pEn_PreSurr = pEntropy.calcPEntropy(signalSurr, param_M-1,
						// param_D);
					}
					fireProgressChanged(80);
					if (isCancelled(getParentTask())) return null;
				}
				apEnSurrMean   = this.calcMean(apEnSurr);
				sampEnSurrMean = this.calcMean(sampEnSurr);
				qseSurrMean    = this.calcMean(qseSurr);
				cosEnSurrMean  = this.calcMean(cosEnSurr);
				pEnSurrMean    = this.calcMean(pEnSurr);
			}
		} //end of method ==  0

		if (method == 1) { // gliding values
			if (typeSurr == -1) {  //no surrogate
				for (int i = 0; i < numDhValues; i++) {
					int proz = (int) (i + 1) * 90 / numDhValues;
					fireProgressChanged(proz);
					if (isCancelled(getParentTask())) return null;
					Vector<Double> subSignalSurr = new Vector<Double>();
					for (int ii = i; ii < i + boxLength; ii++) { // get subvector
						subSignalSurr.add(signal.get(ii));
					}
					if (calcApEn == 1) {
						AppEntropy appEntropy = new AppEntropy();
						apEn[i] += appEntropy.calcAppEntropy(subSignalSurr, apEnParam_M, apEnParam_R, apEnParam_D);
					}
					double se = 0.0; //helper variable
					if ((calcSampEn == 1) || (calcQSE == 1) || (calcCOSEn == 1)) {
						SampleEntropy sampleEntropy = new SampleEntropy();
						se = sampleEntropy.calcSampleEntropy(subSignalSurr, sampEnParam_M, sampEnParam_R, sampEnParam_D);
						sampEn[i] += se;
					}
					if (calcQSE == 1) {
						qse[i] += se + Math.log(2 * sampEnParam_R);
					}
					if (calcCOSEn == 1) {
						cosEn[i] += se - Math.log(sampEnParam_R) - this.calcMean(subSignalSurr);
					}
					if (calcPEn == 1) {
						PEntropy pEntropy = new PEntropy();
						pEn[i] += pEntropy.calcPEntropy(subSignalSurr, pEnParam_M, pEnParam_D);
						// pEn_Pre[i] += pEntropy.calcPEntropy(subSignal, param_M-1, param_D);
					}
				}
			} //end of no surrogates
			if (typeSurr >= 0) { //Surrogates before computing entropies 	
				for (int i = 0; i < numDhValues; i++) {
					int proz = (int) (i + 1) * 90 / numDhValues;
					fireProgressChanged(proz);
					if (isCancelled(getParentTask())) return null;
					Vector<Double> subSignal = new Vector<Double>();
					for (int ii = i; ii < i + boxLength; ii++) { // get subvector
						subSignal.add(signal.get(ii));
					}
					for (int n= 1; n <= nSurr; n++) {
						//create a surrogate signal
						Surrogate surrogate = new Surrogate(this);
						Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(subSignal, typeSurr, 1);
						Vector<Double> signalSurr = plots.get(0);
	
						if (calcApEn == 1) {
							AppEntropy appEntropy = new AppEntropy();
							apEn[i] += appEntropy.calcAppEntropy(signalSurr, apEnParam_M, apEnParam_R, apEnParam_D);
						}
						double se = 0.0; //helper variable
						if ((calcSampEn == 1) || (calcQSE == 1) || (calcCOSEn == 1)) {
							SampleEntropy sampleEntropy = new SampleEntropy();
							se = sampleEntropy.calcSampleEntropy(signalSurr, sampEnParam_M, sampEnParam_R, sampEnParam_D);
							sampEn[i] += se;
						}
						if (calcQSE == 1) {
							qse[i] += se + Math.log(2 * sampEnParam_R);
						}
						if (calcCOSEn == 1) {
							cosEn[i] += se - Math.log(sampEnParam_R) - this.calcMean(signalSurr);
						}
						if (calcPEn == 1) {
							PEntropy pEntropy = new PEntropy();
							pEn[i] += pEntropy.calcPEntropy(signalSurr, pEnParam_M, pEnParam_D);
							// pEn_Pre[i] += pEntropy.calcPEntropy(signalSurr, param_M-1, param_D);
						}
					}
					apEn[i]   = apEn[i]/nSurr;
					sampEn[i] = sampEn[i]/nSurr;
					qse[i]    = qse[i]/nSurr;
					cosEn[i]  = cosEn[i]/nSurr;
					pEn[i]    = pEn[i]/nSurr;
				}
			}//en od surrogates
		} //end of method == 1
		fireProgressChanged(85);
		if (isCancelled(getParentTask())) return null;
		// create model and Table
		TableModel model = new TableModel("Entropy" + (plotModel.getModelName().equals("") ? "" : " of '" + plotModel.getModelName() + "'"));

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
			model.addColumn("Plot name");
			model.addColumn("BoxSize=" + boxLength);
			model.addColumn("Surrogate");
			for (int i = 0; i < numDhValues; i++) {
				// model.addRow(new String[] {"#:"+(i+1), String.valueOf(numK),
				// String.valueOf(regStart), String.valueOf(regEnd) });
				String surrogate = "";
				if (typeSurr == -1) surrogate = "No"; 
				if (typeSurr == Surrogate.SURROGATE_AAFT)        surrogate = "AAFT x" + nSurr ; 
				if (typeSurr == Surrogate.SURROGATE_GAUSSIAN)    surrogate = "Gaussian x" +nSurr; 
				if (typeSurr == Surrogate.SURROGATE_RANDOMPHASE) surrogate = "Rand Phase x"+ nSurr; 
				if (typeSurr == Surrogate.SURROGATE_SHUFFLE)     surrogate = "Shuffle x "+nSurr; 
				model.addRow(new String[] {plotModelName, "#:" + (i + 1), surrogate });
			}
		}
		if (method == 0) { // single value
			if (calcApEn == 1) {
				int numColumns = model.getColumnCount();
				model.addColumn("m_ApEn");
				model.addColumn("r_ApEn");
				model.addColumn("d_ApEn");
				model.setValueAt(String.valueOf(apEnParam_M), 0, numColumns);
				model.setValueAt(String.valueOf(apEnParam_R), 0, numColumns + 1);
				model.setValueAt(String.valueOf(apEnParam_D), 0, numColumns + 2);
			}
			if ((calcSampEn == 1) || (calcQSE == 1) || (calcCOSEn == 1)) {
				int numColumns = model.getColumnCount();
				model.addColumn("m_SampEn");
				model.addColumn("r_SampEn");
				model.addColumn("d_SampEn");
				model.setValueAt(String.valueOf(sampEnParam_M), 0, numColumns);
				model.setValueAt(String.valueOf(sampEnParam_R), 0, numColumns + 1);
				model.setValueAt(String.valueOf(sampEnParam_D), 0, numColumns + 2);
			}
			if (calcPEn == 1) {
				int numColumns = model.getColumnCount();
				model.addColumn("n_PEn");
				model.addColumn("d_PEn");
				model.setValueAt(String.valueOf(pEnParam_M), 0, numColumns);
				model.setValueAt(String.valueOf(pEnParam_D), 0, numColumns + 1);
			}
			if (calcApEn == 1) {
				int numColumns = model.getColumnCount();
				model.addColumn("ApEn");
				model.setValueAt(apEn[0], 0, numColumns);
			}
			if ((calcSampEn == 1) || (calcQSE == 1) || (calcCOSEn == 1)) {
				int numColumns = model.getColumnCount();
				model.addColumn("SampEn");
				model.setValueAt(sampEn[0], 0, numColumns);
			}
			if (calcQSE == 1) {
				int numColumns = model.getColumnCount();
				model.addColumn("QSE");
				model.setValueAt(qse[0], 0, numColumns);
			}
			if (calcCOSEn == 1) {
				int numColumns = model.getColumnCount();
				model.addColumn("COSEn");
				model.setValueAt(cosEn[0], 0, numColumns);
			}
			if (calcPEn == 1) {
				int numColumns = model.getColumnCount();
				model.addColumn("PEn");
				model.setValueAt(pEn[0], 0, numColumns);
				model.addColumn("PEn/log(n!)");
				model.setValueAt(pEn[0] / Math.log(ArithmeticUtils.factorial(pEnParam_M)), 0, numColumns + 1);
			}
			if (typeSurr >= 0) {// Entropies of surrogate(s)
				if (calcApEn == 1) {
					int numColumns = model.getColumnCount();
					model.addColumn("ApEn-Surr");
					model.setValueAt(apEnSurrMean, 0, numColumns);
				}
				if ((calcSampEn == 1) || (calcQSE == 1) || (calcCOSEn == 1)) {
					int numColumns = model.getColumnCount();
					model.addColumn("SampEn-Surr");
					model.setValueAt(sampEnSurrMean, 0, numColumns);
				}
				if (calcQSE == 1) {
					int numColumns = model.getColumnCount();
					model.addColumn("QSE-Sur");
					model.setValueAt(qseSurrMean, 0, numColumns);
				}
				if (calcCOSEn == 1) {
					int numColumns = model.getColumnCount();
					model.addColumn("COSEn-Surr");
					model.setValueAt(cosEnSurrMean, 0, numColumns);
				}
				if (calcPEn == 1) {
					int numColumns = model.getColumnCount();
					model.addColumn("PEn-Surr");
					model.setValueAt(pEnSurrMean, 0, numColumns);
					model.addColumn("PEn/log(n!)-Surr");
					model.setValueAt(pEnSurrMean / Math.log(ArithmeticUtils.factorial(pEnParam_M)), 0, numColumns + 1);
				}
				if (calcApEn == 1) {
					int numColumns = model.getColumnCount();
					for (int x=0; x < nSurr; x++){
						model.addColumn("ApEn-Surr"+(x+1));
						model.setValueAt(apEnSurr.get(x),  0, numColumns + x);
					}
				}
				if ((calcSampEn == 1) || (calcQSE == 1) || (calcCOSEn == 1)) {
					int numColumns = model.getColumnCount();
					for (int x=0; x < nSurr; x++){
						model.addColumn("SampEn-Surr"+(x+1));
						model.setValueAt(sampEnSurr.get(x),  0, numColumns + x);
					}
				}
				if (calcQSE == 1) {
					int numColumns = model.getColumnCount();
					for (int x=0; x < nSurr; x++){
						model.addColumn("QSE-Surr"+(x+1));
						model.setValueAt(qseSurr.get(x),  0, numColumns + x);
					}
				}
				if (calcCOSEn == 1) {
					int numColumns = model.getColumnCount();
					for (int x=0; x < nSurr; x++){
						model.addColumn("COSEn-Surr"+(x+1));
						model.setValueAt(cosEnSurr.get(x),  0, numColumns + x);
					}	
				}
				if (calcPEn == 1) {
					int numColumns = model.getColumnCount();
					for (int x=0; x < nSurr; x++){
						model.addColumn("PEn-Surr"+(x+1));
						model.setValueAt(pEnSurr.get(x),  0, numColumns + x);
					}
				}	
			}
		}
		if (method == 1) { // gliding values

			if (calcApEn == 1) {
				int numColumns = model.getColumnCount();
				if (typeSurr ==-1) model.addColumn("ApEn");  else model.addColumn("ApEn-Surr");
				for (int i = 0; i < numDhValues; i++) {
					model.setValueAt(apEn[i], i, numColumns);
				}
			}
			if ((calcSampEn == 1) || (calcQSE == 1) || (calcCOSEn == 1)) {
				int numColumns = model.getColumnCount();
				if (typeSurr ==-1) model.addColumn("SampEn"); else model.addColumn("SampEn-Surr");
				for (int i = 0; i < numDhValues; i++) {
					model.setValueAt(sampEn[i], i, numColumns);
				}
			}
			if (calcQSE == 1) {
				int numColumns = model.getColumnCount();
				if (typeSurr ==-1) model.addColumn("QSE"); else model.addColumn("QSE-Surr"); 
				for (int i = 0; i < numDhValues; i++) {
					model.setValueAt(qse[i], i, numColumns);
				}
			}
			if (calcCOSEn == 1) {
				int numColumns = model.getColumnCount();
				if (typeSurr ==-1) model.addColumn("COSEn"); else  model.addColumn("COSEn-Surr");
				for (int i = 0; i < numDhValues; i++) {
					model.setValueAt(cosEn[i], i, numColumns);
				}
			}
			if (calcPEn == 1) {
				int numColumns = model.getColumnCount();
				if (typeSurr ==-1) model.addColumn("PEn");         else model.addColumn("PEn-Surr");
				if (typeSurr ==-1) model.addColumn("PEn/log(n!)"); else model.addColumn("PEn/log(n!)-Surr");
				for (int i = 0; i < numDhValues; i++) {
					model.setValueAt(pEn[i], i, numColumns);
					model.setValueAt(pEn[i] / Math.log(ArithmeticUtils.factorial(pEnParam_M)), i, numColumns + 1);
				}
			}
		}
		model.fireTableStructureChanged(); // this is mandatory because it
											// updates the table
//		// format column widths
//		int numColumns = model.getColumnCount();
//		for (int i = 0; i < numColumns; i++) {
//			if ((model.getColumnName(i) == "m_ApEn")
//					|| (model.getColumnName(i) == "m_SampEn")
//					|| (model.getColumnName(i) == "n_PEn")
//					|| (model.getColumnName(i) == "r_ApEn")
//					|| (model.getColumnName(i) == "r_SampEn")
//					|| (model.getColumnName(i) == "r_PEn")
//					|| (model.getColumnName(i) == "d_ApEn")
//					|| (model.getColumnName(i) == "d_SampEn")
//					|| (model.getColumnName(i) == "d_PEn")) {
//				jTable.getColumnModel().getColumn(i).setPreferredWidth(10);
//			}
//			if ((model.getColumnName(i) == "ApEn")
//					|| (model.getColumnName(i) == "SampEn")
//					|| (model.getColumnName(i) == "QSE")
//					|| (model.getColumnName(i) == "COSEn")
//					|| (model.getColumnName(i) == "PEn")
//					|| (model.getColumnName(i) == "PEn/log(n!)")) {
//				jTable.getColumnModel().getColumn(i).setPreferredWidth(80);
//			}
//		}

		this.fireProgressChanged(95);
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(model);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new PlotOpEntropyDescriptor().getName();
		}
		return name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpEntropyDescriptor.TYPE;
	}

}
