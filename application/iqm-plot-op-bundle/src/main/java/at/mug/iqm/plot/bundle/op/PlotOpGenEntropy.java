package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpGenEntropy.java
 * 
 * $Id: PlotOpGenEntropy.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-plot-op-bundle/src/main/java/at/mug/iqm/plot/bundle/op/PlotOpGenEntropy.java $
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
import at.mug.iqm.commons.util.GammaFunction;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpGenEntropyDescriptor;
import flanagan.analysis.Stat;

/**
 * <li>Generalized Entropies
 * <li>according to a review of Amigó, J.M., Balogh, S.G., Hernández, S., 2018. A Brief Review of Generalized Entropies. Entropy 20, 813. https://doi.org/10.3390/e20110813
 * <li>and to: Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * <li>(SE     according to Amigo etal. and Tsekouras, G.A.; Tsallis, C. Generalized entropy arising from a distribution of q indices. Phys. Rev. E 2005,)
 * <li>SE      according to N. R. Pal and S. K. Pal: Object background segmentation using new definitions of entropy, IEEE Proc. 366 (1989), 284–295.
							and N. R. Pal and S. K. Pal, Entropy: a new definitions and its applications, IEEE Transactions on systems, Man and Cybernetics, 21(5), 1260-1270, 1999
 * <li>H       according to Amigo etal.
 * <li>Renyi   according to Amigo etal.
 * <li>Tsallis according to Amigo etal.
 * <li>SNorm   according to Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * <li>SEscort according to Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * <li>SEta    according to Amigo etal. and Anteneodo, C.; Plastino, A.R. Maximum entropy approach to stretched exponential probability distributions. J. Phys. A Math. Gen. 1999, 32, 1089–1098.	
 * <li>SKappa  according to Amigo etal. and Kaniadakis, G. Statistical mechanics in the context of special relativity. Phys. Rev. E 2002, 66, 056125
 * <li>SB      according to Amigo etal. and Curado, E.M.; Nobre, F.D. On the stability of analytic entropic forms. Physica A 2004, 335, 94–106.
 * <li>SBeta   according to Amigo etal. and Shafee, F. Lambert function and a new non-extensive form of entropy. IMA J. Appl. Math. 2007, 72, 785–800.
 * <li>SGamma  according to Amigo etal. and Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S61
 * 
 * @author Ahammer
 * @since  2018-12-14
 */

public class PlotOpGenEntropy extends AbstractOperator {

	public PlotOpGenEntropy() {
		this.setCancelable(true);
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
	
		int sE      = pb.getIntParameter("SE");
		int h       = pb.getIntParameter("H");
		int renyi   = pb.getIntParameter("Renyi");
		int tsallis = pb.getIntParameter("Tsallis");
		int sNorm   = pb.getIntParameter("SNorm");
		int sEscort = pb.getIntParameter("SEscort");
		int sEta    = pb.getIntParameter("SEta");
		int sKappa  = pb.getIntParameter("SKappa");
		int sB      = pb.getIntParameter("SB");
		int sBeta   = pb.getIntParameter("SBeta");
		int sGamma  = pb.getIntParameter("SGamma");
		
		int minQ     = pb.getIntParameter("MinQ");
		int maxQ     = pb.getIntParameter("MaxQ");
		double minEta   = pb.getDoubleParameter("MinEta");
		double maxEta   = pb.getDoubleParameter("MaxEta");
		double minKappa = pb.getDoubleParameter("MinKappa");
		double maxKappa = pb.getDoubleParameter("MaxKappa");
		double minB     = pb.getDoubleParameter("MinB");
		double maxB     = pb.getDoubleParameter("MaxB");
		double minBeta  = pb.getDoubleParameter("MinBeta");
		double maxBeta  = pb.getDoubleParameter("MaxBeta");
		double minGamma = pb.getDoubleParameter("MinGamma");
		double maxGamma = pb.getDoubleParameter("MaxGamma");
		
		int probOption   = pb.getIntParameter("ProbOption"); // Probabilities computation option
		int eps          = pb.getIntParameter("Eps"); // epsilon in pixels
		
		int method    = pb.getIntParameter("Method");
		int boxLength = pb.getIntParameter("BoxLength");
		int typeSurr  = pb.getIntParameter("TypeSurr");
		int nSurr     = pb.getIntParameter("NSurr");
	
		//System.out.println("typeSurr:  " + typeSurr);
		//System.out.println("nSurr:  " + nSurr);
				
		String plotModelName = plotModel.getModelName();
		
		// new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());

		int  stepQ     = 1;
		double stepEta   = 0.1;
		double stepKappa = 0.1;
		double stepB     = 1.0;
		double stepBeta  = 0.1;
		double stepGamma = 0.1;
	
		int numQ     =        (maxQ - minQ)/stepQ + 1;
		int numEta   = (int) ((maxEta - minEta)/stepEta + 1);
		int numKappa = (int) ((maxKappa - minKappa)/stepKappa + 1);
		int numB     = (int) ((maxB - minB)/stepB + 1);
		int numBeta  = (int) ((maxBeta - minBeta)/stepBeta + 1);
		int numGamma = (int) ((maxGamma - minGamma)/stepGamma + 1);
		
		
		int numGlidingValues = 1;
		//number of entropy values depends on single or gliding option
		if (method == 0)  numGlidingValues = 1; // Single value
		if (method == 1)  numGlidingValues = signal.size() - boxLength + 1;	//Gliding Box
		
		if ((method == 1) && (numGlidingValues < 1)) { // check for gliding method
			DialogUtil.getInstance().showDefaultErrorMessage("Entropies cannot be calculated because gliding box length is too large");
			boxLength = signal.size()/2;
			numGlidingValues = signal.size() - boxLength + 1;
			DialogUtil.getInstance().showDefaultErrorMessage("Box length set to: " + boxLength);
			//return null;
		}
		
		String strMethod = "?";
		if (method == 0) strMethod = "Single value";
		if (method == 1) strMethod = "Gliding values";

		// data arrays		
		double   genEntSE = 0.0;
		double   genEntH1 = 0.0;	
		double   genEntH2 = 0.0;	
		double   genEntH3 = 0.0;
		double[] genEntRenyi   = new double[numQ];
		double[] genEntTsallis = new double[numQ];	
		double[] genEntSNorm   = new double[numQ];	
		double[] genEntSEscort = new double[numQ];	
		double[] genEntSEta    = new double[numEta];	
		double[] genEntSKappa  = new double[numKappa];	
		double[] genEntSB      = new double[numB];	
		double[] genEntSBeta   = new double[numBeta];	
		double[] genEntSGamma  = new double[numGamma];
		
		double[] probabilities         = null; //pi's
		double[] probabilitiesSurrMean = null; //pi's
		
		fireProgressChanged(5);
		if (isCancelled(getParentTask())) return null;
		
		//determine pi's
		if (method == 0) { // single value
	
			if (typeSurr == -1) {  //no surrogate			
				probabilities = compProbabilities(signal, eps, probOption);			
			}
			if (typeSurr >= 0) { //Surrogates before computing probabilities			
				probabilitiesSurrMean = compProbabilitiesSurr(signal, eps, probOption, typeSurr, nSurr);			
			}

			fireProgressChanged(30);
			if (isCancelled(getParentTask())) return null;
				
		} //end of method ==  0

		if (method == 1) { // gliding values
			if (typeSurr == -1) {  //no surrogate
				for (int i = 0; i < numGlidingValues; i++) {
					int proz = (int) (i + 1) * 90 / numGlidingValues;
					fireProgressChanged(proz);
					if (isCancelled(getParentTask())) return null;
			
				}
			} //end of no surrogates
			if (typeSurr >= 0) { //Surrogates before computing entropies 	
				for (int i = 0; i < numGlidingValues; i++) {
					int proz = (int) (i + 1) * 90 / numGlidingValues;
					fireProgressChanged(proz);
					if (isCancelled(getParentTask())) return null;
				}
			}//en od surrogates
		} //end of method == 1
		
		
		fireProgressChanged(85);
		if (isCancelled(getParentTask())) return null;
		// create model and Table
		TableModel model = new TableModel("Entropy" + (plotModel.getModelName().equals("") ? "" : " of '" + plotModel.getModelName() + "'"));

		//------------------------------------------------------------------------------------------------------

		if (method == 0) { // single value
			model.addColumn("Plot name");
			model.addColumn("Method");
			model.addColumn("p");
			model.addColumn("Eps");
			model.addColumn("Surrogate");
			// model.addRow(new String[] {plotModelName, String.valueOf(numK),
			// String.valueOf(regStart), String.valueOf(regEnd) });
			String surrogate = "";
			if (typeSurr == -1) surrogate = "No"; 
			if (typeSurr == Surrogate.SURROGATE_AAFT)        surrogate = "AAFT x" + nSurr ; 
			if (typeSurr == Surrogate.SURROGATE_GAUSSIAN)    surrogate = "Gaussian x" +nSurr; 
			if (typeSurr == Surrogate.SURROGATE_RANDOMPHASE) surrogate = "Rand Phase x"+ nSurr; 
			if (typeSurr == Surrogate.SURROGATE_SHUFFLE)     surrogate = "Shuffle x "+nSurr; 
			
			if (probOption == 0) model.addRow(new String[] {plotModelName, strMethod, "Actual",    "--",                surrogate});
			if (probOption == 1) model.addRow(new String[] {plotModelName, strMethod, "Pair diff", String.valueOf(eps), surrogate});
			if (probOption == 2) model.addRow(new String[] {plotModelName, strMethod, "Sum diff",  String.valueOf(eps), surrogate});
			if (probOption == 3) model.addRow(new String[] {plotModelName, strMethod, "SD",        String.valueOf(eps), surrogate});
		}
		if (method == 1) { // gliding values
			model.addColumn("Plot name");
			model.addColumn("Method");
			model.addColumn("p");
			model.addColumn("Eps");	
			model.addColumn("BoxSize=" + boxLength);
			model.addColumn("Surrogate");
			for (int i = 0; i < numGlidingValues; i++) {
				// model.addRow(new String[] {"#:"+(i+1), String.valueOf(numK),
				// String.valueOf(regStart), String.valueOf(regEnd) });
				String surrogate = "";
				if (typeSurr == -1) surrogate = "No"; 
				if (typeSurr == Surrogate.SURROGATE_AAFT)        surrogate = "AAFT x" + nSurr ; 
				if (typeSurr == Surrogate.SURROGATE_GAUSSIAN)    surrogate = "Gaussian x" +nSurr; 
				if (typeSurr == Surrogate.SURROGATE_RANDOMPHASE) surrogate = "Rand Phase x"+ nSurr; 
				if (typeSurr == Surrogate.SURROGATE_SHUFFLE)     surrogate = "Shuffle x "+nSurr; 
				model.addRow(new String[] {plotModelName, strMethod, String.valueOf(eps), "#:" + (i + 1), surrogate });
			}
			model.addColumn("GLIDING VALUES FOR SURROGATES IS NOT IMPLEMENTED");		
		}
			
		//------------------------------------------------------------------------------------------------------
		if (method == 0) { // single value
			
			double[] probs = null;
			if (typeSurr == -1) probs = probabilities;          // Probabilities
			if (typeSurr >= 0)  probs = probabilitiesSurrMean;  // Probabilities of surrogate(s)
			//--------------------------------------------------------------------------------------------------
			if (sE == 1) {//SE according to Amigo or Pal or Hassan	
				double sum = 0.0;
				for (int pp = 0; pp < probs.length; pp++) {
					//if (probs[pp] != 0) {
						//sum = sum +  probs[pp] * (1.0 - Math.exp((probs[pp] - 1.0) / probs[pp]) ); //almost always exact  1!?	//According to and Tsekouras & Tsallis, and Tsallis book
						sum = sum +  probs[pp] * (Math.exp(1.0 - probs[pp]) - 1.0); //around 1.7 // N. R. Pal and S. K. Pal: Object background segmentation using new definitions of entropy, IEEE Proc. 366 (1989), 284–295.
																			        // N. R. Pal and S. K. Pal, Entropy: a new definitions and its applications, IEEE Transactions on systems, Man and Cybernetics, 21(5), 1260-1270, 1999
						//sum = sum +  probs[pp] * Math.exp(1.0 - probs[pp]); //always around 2.7 // Hassan Badry Mohamed El-Owny, Exponential Entropy Approach for Image Edge Detection, International Journal of Theoretical and Applied Mathematics 2016; 2(2): 150-155 http://www.sciencepublishinggroup.com/j/ijtam doi: 10.11648/j.ijtam.20160202.29 Hassan Badry Mohamed El-Owny1, 
					//}
				}
				genEntSE = sum;
								
				//set table data
				int numColumns = model.getColumnCount();
				if (typeSurr == -1) model.addColumn("SE");		
				if (typeSurr >= 0)  model.addColumn("SE_Surr");		
				model.setValueAt(genEntSE, 0, numColumns); // set table data						
			}
			//--------------------------------------------------------------------------------------------------
			if (h == 1) {//H1 according to Amigo etal. paper				
				//double sum = 0.0;
				for (int pp = 0; pp < probs.length; pp++) {
					if (probs[pp] != 0) {
							double pHochp = Math.pow(probs[pp], probs[pp]);
							genEntH1 = genEntH1 + (1.0 - pHochp);
							genEntH2 = genEntH2 + Math.log(2.0-pHochp);
							genEntH3 = genEntH3 + (probs[pp] + Math.log(2.0-pHochp));	
					}
				}
				genEntH2 = Math.exp(genEntH2);
					
				//set table data
				int numColumns = model.getColumnCount();
				if (typeSurr == -1) model.addColumn("H1");	
				if (typeSurr >= 0)  model.addColumn("H1_Surr");						
				model.setValueAt(genEntH1, 0, numColumns); // set table data			
				
				numColumns = model.getColumnCount();
				if (typeSurr == -1) model.addColumn("H2");
				if (typeSurr >= 0)  model.addColumn("H2_Surr");	
				model.setValueAt(genEntH2, 0, numColumns); // set table data			
				
				numColumns = model.getColumnCount();
				if (typeSurr == -1) model.addColumn("H3");	
				if (typeSurr >= 0)  model.addColumn("H3_Surr");	
				model.setValueAt(genEntH3, 0, numColumns); // set table data			
				
				int H3OutOfH2 = 0; //only for test purposes
				if (H3OutOfH2 == 1) {
					numColumns = model.getColumnCount();
					if (typeSurr == -1) model.addColumn("H3OutOfH2");	
					if (typeSurr >= 0)  model.addColumn("H3OutOfH2_Surr");
					model.setValueAt(1.0 + Math.log(genEntH2), 0, numColumns); // H3 = 1+ln(H2);  set table data				
				}
			}	
			//--------------------------------------------------------------------------------------------------
			if (renyi == 1) {//Renyi according to Amigo etal. paper				
				for (int q = 0; q < numQ; q++) {
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if ((q + minQ) == 1) { //q=1 special case
							if (probs[pp] == 0) {// damit logarithmus nicht undefiniert ist;
								sum = sum +  Double.MIN_VALUE*Math.log( Double.MIN_VALUE); //for q=1 Renyi is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
							else {
								sum = sum + probs[pp]*Math.log(probs[pp]); //for q=1 Renyi is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
						}
						else if (((q + minQ) <=  0 ) && (probs[pp] != 0.0)){ //leaving out 0 is essential! and according to Amigo etal. page 2
							sum = sum + Math.pow(probs[pp],(q + minQ));	
						}
						else if ( (q + minQ) > 0 ) {
							sum = sum + Math.pow(probs[pp],(q + minQ));
						}
					}			
					if ((q + minQ) == 1) { //special case q=1 Renyi is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
						genEntRenyi[q] = -sum; 
					}	
					else {
						if (sum == 0) sum = Double.MIN_VALUE; // damit logarithmus nicht undefiniert ist
						genEntRenyi[q] = Math.log(sum)/(1.0-(q + minQ));	
					}
				}//q		
							
				//set table data
				int numColumns = model.getColumnCount();
				//data header
				for (int q = 0; q < numQ; q++) {
					if (typeSurr == -1) model.addColumn("Renyi_q" + (minQ + q));
					if (typeSurr >= 0)  model.addColumn("Renyi_Surr_q" + (minQ + q));
				}			
				for (int q = 0; q < numQ; q++) {			
					model.setValueAt(genEntRenyi[q], 0, numColumns + q); // set table data			
				}//q		
			}
			//--------------------------------------------------------------------------------------------------
			if (tsallis == 1) {//Tsallis according to Amigo etal. paper		
				for (int q = 0; q < numQ; q++) {
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if ((q + minQ) == 1) { //q=1 special case
							if (probs[pp] == 0) {// damit logarithmus nicht undefiniert ist;
								sum = sum +  Double.MIN_VALUE*Math.log( Double.MIN_VALUE); //for q=1 Renyi is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
							else {
								sum = sum + probs[pp]*Math.log(probs[pp]); //for q=1 Renyi is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
						}
						else if (((q + minQ) <=  0 ) && (probs[pp] != 0.0)) { //leaving out 0 is essential! and according to Amigo etal. page 2
								sum = sum + Math.pow(probs[pp],(q + minQ));	
						}
						else if ( (q + minQ) > 0 ) {
								sum = sum + Math.pow(probs[pp],(q + minQ));	
						}				
					}				
					if ((q + minQ) == 1) { // special case for q=1 Tsallis is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
						genEntTsallis[q] = -sum;
					}
					else {
						genEntTsallis[q] = (sum-1)/(1.0-(q + minQ));
					}		
				}//q
								
				//set table data
				int numColumns = model.getColumnCount();
				//data header
				for (int q = 0; q < numQ; q++) {
					if (typeSurr == -1) model.addColumn("Tsallis_q"      + (minQ + q));
					if (typeSurr >= 0)  model.addColumn("Tsallis_Surr_q" + (minQ + q));
				}
				
				for (int q = 0; q < numQ; q++) {			
					model.setValueAt(genEntTsallis[q], 0, numColumns + q); // set table data			
				}//q
					
				int tsallisOutOfRenyi = 0;  //only for test purposes
				if (tsallisOutOfRenyi == 1) {//Tsallis out of Renyi according to Amigo etal. paper
					numColumns = model.getColumnCount();
					//data header
					for (int q = 0; q < numQ; q++) {
						if (typeSurr == -1) model.addColumn("TsallisAusReneyi_q"      + (minQ + q));
						if (typeSurr >= 0)  model.addColumn("TsallisAusReneyi_Surr_q" + (minQ + q));
					}				
					for (int q = 0; q < numQ; q++) {					
						double genEnt = 0.0;
						if ((q + minQ) != 1) genEnt = (Math.exp((1-(q + minQ))*genEntRenyi[q])-1)/(1-(q + minQ));
						if ((q + minQ) == 1) genEnt = genEntRenyi[q];
						model.setValueAt(genEnt, 0, numColumns + q); // set table data			
					}//q			
				}
			}
			//--------------------------------------------------------------------------------------------------
			if (sNorm == 1) {//SNorm according to Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
				
				for (int q = 0; q < numQ; q++) {
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if ((q + minQ) == 1) { //q=1 special case
							if (probs[pp] == 0) {// damit logarithmus nicht undefiniert ist;
								sum = sum +  Double.MIN_VALUE*Math.log( Double.MIN_VALUE); //for q=1 Snorm is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
							else {
								sum = sum + probs[pp]*Math.log(probs[pp]); //for q=1 Snorm is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
						}
						else if (((q + minQ) <=  0 ) && (probs[pp] != 0.0)){ //leaving out 0 is essential! and according to Amigo etal. page 2
							sum = sum + Math.pow(probs[pp],(q + minQ));	
						}
						else if ( (q + minQ) > 0 ) {
							sum = sum + Math.pow(probs[pp],(q + minQ));
						}
//						else {
//							sum = sum + Math.pow(probabilities[pp],(q + minQ));
//						}
					}			
					if ((q + minQ) == 1) { //special case q=1 SNorm is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
						genEntSNorm[q] = -sum; 
					}	
					else {
						genEntSNorm[q] = (1.0-(1.0/sum))/(1.0-(q + minQ));	
					}
				}//q		
						
				//set table data
				int numColumns = model.getColumnCount();
				//data header
				for (int q = 0; q < numQ; q++) {
					if (typeSurr == -1) model.addColumn("SNorm_q"      + (minQ + q));
					if (typeSurr >= 0)  model.addColumn("SNorm_Surr_q" + (minQ + q));
				}		
				for (int q = 0; q < numQ; q++) {			
					model.setValueAt(genEntSNorm[q], 0, numColumns + q); // set table data			
				}//q
			}
			//--------------------------------------------------------------------------------------------------
			if (sEscort == 1) {//SEscort according to Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
				for (int q = 0; q < numQ; q++) {
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if ((q + minQ) == 1) { //q=1 special case
							if (probs[pp] == 0) {// damit logarithmus nicht undefiniert ist;
								sum = sum +  Double.MIN_VALUE*Math.log( Double.MIN_VALUE); //for q=1 SEscort is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
							else {
								sum = sum + probs[pp]*Math.log(probs[pp]); //for q=1 SEscort is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
						}
						else if (((q + minQ) <=  0 ) && (probs[pp] != 0.0)){ //leaving out 0 is essential! and according to Amigo etal. page 2
							sum = sum + Math.pow(probs[pp], 1.0/(q + minQ));	
						}
						else if ( (q + minQ) > 0 ) {
							sum = sum + Math.pow(probs[pp], 1.0/(q + minQ));
						}
//						else {
//							sum = sum + Math.pow(probabilities[pp], 1.0/(q + minQ));
//						}
					}			
					if ((q + minQ) == 1) { //special case q=1 SEscort is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
						genEntSEscort[q] = -sum; 
					}	
					else {
						genEntSEscort[q] = (1.0 - Math.pow(sum, -(q+minQ)))/((q + minQ) - 1.0);	
					}
				}//q		
					
				//set table data
				int numColumns = model.getColumnCount();
				//data header
				for (int q = 0; q < numQ; q++) {
					if (typeSurr == -1) model.addColumn("SEscort_q"      + (minQ + q));
					if (typeSurr >= 0)  model.addColumn("SEscort_Surr_q" + (minQ + q));
				}		
				for (int q = 0; q < numQ; q++) {			
					model.setValueAt(genEntSEscort[q], 0, numColumns + q); // set table data			
				}//q		
			}
			//--------------------------------------------------------------------------------------------------
			if (sEta == 1) { //SEta   according to Amigo etal. and Anteneodo, C.; Plastino, A.R. Maximum entropy approach to stretched exponential probability distributions. J. Phys. A Math. Gen. 1999, 32, 1089–1098.	
			
				for (int n = 0; n < numEta; n++) {
					double eta = minEta + n*stepEta; //SEta is equal to S_BGS (Bolzmann Gibbs Shannon entropy) for eta = 1 
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if (probs[pp] != 0){
							//both following methods provide same results (only some minor deviations in the last decimals) 
							//compute incomplete Gamma function using IQM's classes
							double gam1 = GammaFunction.upperIncomplete((eta+1.0)/eta, -Math.log(probs[pp]));
							double gam2 = probs[pp]*GammaFunction.gamma((eta+1.0)/eta); 
							
							//or compute incomplete Gamma function using Apache's classes
							//double gam1 = Gamma.regularizedGammaQ((eta+1.0)/eta, -Math.log(probabilities[pp])) * Math.exp(Gamma.logGamma((eta+1.0)/eta));
							//double gam2 = probabilities[pp]*Math.exp(Gamma.logGamma((eta+1.0f)/eta)); 
							sum = sum + gam1 - gam2;	
						}
					}	
					genEntSEta[n] = sum;
				}//q		
			
				
				//set table data
				int numColumns = model.getColumnCount();
				//data header
				for (int n = 0; n < numEta; n++) {	
					if (typeSurr == -1) model.addColumn("SEta_n"      + String.format ("%.1f", minEta + n*stepEta));
					if (typeSurr >= 0)  model.addColumn("SEta_Surr_n" + String.format ("%.1f", minEta + n*stepEta));
				}		
				for (int n = 0; n < numEta; n++) {			
					model.setValueAt(genEntSEta[n], 0, numColumns + n); // set table data			
				}//q			
			}
			//--------------------------------------------------------------------------------------------------
			if (sKappa == 1) { //SKappa according to Amigo etal. and Kaniadakis, G. Statistical mechanics in the context of special relativity. Phys. Rev. E 2002, 66, 056125
			
				for (int k = 0; k < numKappa; k++) {
					double kappa = minKappa + k*stepKappa; //SKappa is equal to S_BGS (Bolzmann Gibbs Shannon entropy) for kappa = 0 
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if (kappa == 0) { //kappa=0 special case S_BGS (Bolzmann Gibbs Shannon entropy)
							if (probs[pp] == 0) {// damit logarithmus nicht undefiniert ist;
								sum = sum +  Double.MIN_VALUE*Math.log( Double.MIN_VALUE); //for k = 0 SKappa is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
							else {
								sum = sum + probs[pp]*Math.log(probs[pp]); //for k=0 SKappa is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
						}
						else {
							//if (probabilities[pp] != 0){			
								sum = sum + (Math.pow(probs[pp], 1.0-kappa) - Math.pow(probs[pp], 1.0+kappa))/(2.0*kappa);			
							//}
						}
					}
					if (kappa == 0){
						genEntSKappa[k] = -sum;
					}
					else {
						genEntSKappa[k] = sum;
					}
				}//q		
				
				//set table data
				int numColumns = model.getColumnCount();
				//data header
				for (int k = 0; k < numKappa; k++) {	
					if (typeSurr == -1) model.addColumn("SKappa_k"      + String.format ("%.1f", minKappa + k*stepKappa));
					if (typeSurr >= 0)  model.addColumn("SKappa_Surr_k" + String.format ("%.1f", minKappa + k*stepKappa));
				}
				
				for (int k = 0; k < numKappa; k++) {			
					model.setValueAt(genEntSKappa[k], 0, numColumns + k); // set table data			
				}//k	
			}
			//--------------------------------------------------------------------------------------------------
			if (sB == 1) { //SB  according to Amigo etal. and Curado, E.M.; Nobre, F.D. On the stability of analytic entropic forms. Physica A 2004, 335, 94–106.
				
				for (int n = 0; n < numB; n++) {
					double valueB = minB + n*stepB; //SB is equal to S_BGS (Bolzmann Gibbs Shannon entropy) for ????????????????? 
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						//if (probabilities[pp] != 0){
							sum = sum + (1.0 - Math.exp(-valueB*probs[pp]));
						//}
					}	
					genEntSB[n] = sum + (Math.exp(-valueB)-1.0); 
				}//q		
					
				//set table data
				int numColumns = model.getColumnCount();
				//data header
				for (int n = 0; n < numB; n++) {	
					if (typeSurr == -1) model.addColumn("SB_b"      + String.format ("%.1f", minB + n*stepB));
					if (typeSurr >= 0)  model.addColumn("SB_Surr_b" + String.format ("%.1f", minB + n*stepB));
				}
				
				for (int n = 0; n < numB; n++) {			
					model.setValueAt(genEntSB[n], 0, numColumns + n); // set table data			
				}//n
				
			}
			//--------------------------------------------------------------------------------------------------
			if (sBeta == 1) {//SBeta  according to Amigo etal. and Shafee, F. Lambert function and a new non-extensive form of entropy. IMA J. Appl. Math. 2007, 72, 785–800.

				for (int n = 0; n < numBeta; n++) {
					double valueBeta = minBeta + n*stepBeta; //SBeta is equal to S_BGS (Bolzmann Gibbs Shannon entropy) for valueBeta = 1; 
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {		
						if (probs[pp] != 0.0){ //leaving out 0 
							sum = sum + Math.pow(probs[pp],  valueBeta) * Math.log(1.0/probs[pp]);
						}
					}
					genEntSBeta[n] = sum;					
				}//q		
				
				
				//set table data
				int numColumns = model.getColumnCount();
				//data header
				for (int n = 0; n < numBeta; n++) {
					if (typeSurr == -1) model.addColumn("SBeta_b"      + String.format ("%.1f", minBeta + n*stepBeta));
					if (typeSurr >= 0)  model.addColumn("SBeta_Surr_b" + String.format ("%.1f", minBeta + n*stepBeta));
				}
				
				for (int n = 0; n < numBeta; n++) {			
					model.setValueAt(genEntSBeta[n], 0, numColumns + n); // set table data			
				}//n		
			}
			//--------------------------------------------------------------------------------------------------
			if (sGamma == 1) { //SGamma according to Amigo etal. and Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S61
				
				for (int g = 0; g < numGamma; g++) {
					double valueGamma = minGamma + g*stepGamma; //SGama is equal to S_BGS (Bolzmann Gibbs Shannon entropy) for valueGamma = 1; 
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {		
						if (probs[pp] != 0.0){ //leaving out 0 
							sum = sum + Math.pow(probs[pp],  1.0/valueGamma) * Math.log(1.0/probs[pp]);
						}
					}
					genEntSGamma[g] = sum;					
				}//q		
				
				//set table data
				int numColumns = model.getColumnCount();
				//data header
				for (int g = 0; g < numBeta; g++) {
					if (typeSurr == -1) model.addColumn("SGamma_g"      + String.format ("%.1f", minGamma + g*stepGamma));
					if (typeSurr >= 0)  model.addColumn("SGamma_Surr_g" + String.format ("%.1f", minGamma + g*stepGamma));
				}
				
				for (int g = 0; g < numGamma; g++) {			
					model.setValueAt(genEntSGamma[g], 0, numColumns + g); // set table data			
				}//n		
			}
		
		}
		//--------------------------------------------------------------------------------------------------------------------------------------------
		if (method == 1) { // gliding values NOT IMPLEMENTED

//			if (calcApEn == 1) {
//				int numColumns = model.getColumnCount();
//				if (typeSurr ==-1) model.addColumn("ApEn");  else model.addColumn("ApEn-Surr");
//				for (int i = 0; i < numGlidingValues; i++) {
//					model.setValueAt(apEn[i], i, numColumns);
//				}
//			}
		
		}
		model.fireTableStructureChanged(); // this is mandatory because it updates the table

		this.fireProgressChanged(95);
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(model);

	}

	//------------------------------------------------------------------------------------------------------
	/**
	 * This computes probabilities of actual values
	 * 
	 * @param signal
	 * @param eps
	 * @param probOption
	 * @return probabilities[]
	 */
	private double[] compProbabilities(Vector<Double> signal, int eps, int probOption) {
		if (probOption == 0) eps = 0; //to be sure that eps = 0 for that case
		double signalMin = Double.MAX_VALUE;
		double signalMax = -Double.MAX_VALUE;
		double signalDouble[] = new double[signal.size() - eps]; 
		for (int i = 0; i < signal.size() - eps; i++) {
			if (probOption == 0) {//Actual values
				signalDouble[i] = signal.get(i);
			}
			if (probOption == 1) {//Pairwise differences
				signalDouble[i] = Math.abs(signal.get(i+eps) - signal.get(i)); //Difference
			}
			if (probOption == 2) {//Sum of differences inbetween eps
				double sum = 0.0;
				for (int ii = 0; ii < eps; ii++) {
					sum = sum + Math.abs(signal.get(i+ii+1) - signal.get(i+ii)); //Difference
				}
				signalDouble[i] = sum;
			}
			if (probOption == 3) {//SD inbetween eps
				double mean = 0.0;
				for (int ii = 0; ii < eps; ii++) {
					mean = mean + signal.get(i+ii); //Difference
				}
				mean = mean/((double)eps);
				double sumDiff2 = 0.0;
				for (int ii = 0; ii <= eps; ii++) {
					sumDiff2 = sumDiff2 + Math.pow(signal.get(i+ii) - mean, 2); //Difference
				}	
				signalDouble[i] = Math.sqrt(sumDiff2/((double)eps));
			}
			if (signalDouble[i] < signalMin) signalMin = signalDouble[i];  
			if (signalDouble[i] > signalMax) signalMax = signalDouble[i];  
		}	
		double binWidth = (signalMax - signalMin)/1000;
		double[][] histo = Stat.histogramBins(signalDouble, binWidth, signalMin, signalMax);   //hist[0][] are the center bin values  hist[1][] are the frequencies of the bins
		//Additionally with a Flanagan plot
		//double[][] histo = Stat.histogramBinsPlot(signalDouble, binWidth, signalMin, signalMax);   //hist[0][] are the center bin values  hist[1][] are the frequencies of the bins
    	double[] pis = new double[histo[1].length]; 

		double totalsMax = 0.0;
		for (int p= 0; p < histo[1].length; p++) {
			pis[p] = histo[1][p];
			totalsMax = totalsMax + histo[1][p]; // calculate total count for normalization
		}	
		
		// normalization
		double sumP = 0.0;
		for (int p = 0; p < pis.length; p++) {	
			pis[p] = pis[p] / totalsMax;
			//System.out.println("PlotOpGenEntropy: p: " + p + "  probabilities[p]: " + probabilities[p]);
			sumP = sumP + pis[p];
		}
		System.out.println("PlotOpGenEntropy: Sum of probabilities: " + sumP);
		return pis;
	}
	
	/**
	 * This computes probabilities of surrogates of actual values
	 * 
	 * @param signal
	 * @param eps
	 * @param probOption
	 * @param typeSurr
	 * @param nSurr
	 * @return double[]   probabilitiesSurrMean
	 */
	private double[] compProbabilitiesSurr(Vector<Double> signal, int eps, int probOption, int typeSurr, int nSurr) {
		if (probOption == 0) eps = 0;
		double[]   probabilitiesSurrMean = null; //pi's
		double[][] probabilitiesSurr = null;
		for (int n= 0; n < nSurr; n++) {
			double totalsMaxSurr = 0.0;
			//create a surrogate signal
			Surrogate surrogate = new Surrogate(this);
			Vector<Vector<Double>> plots = surrogate.calcSurrogateSeries(signal, typeSurr, 1);
			Vector<Double> signalSurr = plots.get(0);
				
			double signalMinSurr = Double.MAX_VALUE;
			double signalMaxSurr = -Double.MAX_VALUE;
			double signalDouble[] = new double[signalSurr.size() - eps]; 
			for (int s = 0; s < signalSurr.size() - eps; s++) {
				
				if (probOption == 0) {//Actual values
					signalDouble[s] = signalSurr.get(s);
				}
				if (probOption == 1) {//Difference values
					signalDouble[s] = Math.abs(signalSurr.get(s+eps) - signalSurr.get(s)); //Difference
				}
				if (probOption == 2) {//Sum of differences inbetween eps
					double sum = 0.0;
					for (int ii = 0; ii < eps; ii++) {
						sum = sum + Math.abs(signalSurr.get(s+ii+1) - signalSurr.get(s+ii)); //Difference
					}
					signalDouble[s] = sum;
				}
				if (probOption == 3) {//SD inbetween eps
					double mean = 0.0;
					for (int ii = 0; ii < eps; ii++) {
						mean = mean + signalSurr.get(s+ii); //Difference
					}
					mean = mean/((double)eps);
					double sumDiff2 = 0.0;
					for (int ii = 0; ii <= eps; ii++) {
						sumDiff2 = sumDiff2 + Math.pow(signalSurr.get(s+ii) - mean, 2); //Difference
					}	
					signalDouble[s] = Math.sqrt(sumDiff2/((double)eps));
				}
				if (signalDouble[s] < signalMinSurr) signalMinSurr = signalDouble[s];  
				if (signalDouble[s] > signalMaxSurr) signalMaxSurr = signalDouble[s];  
			}	
			double binWidth = (signalMaxSurr - signalMinSurr)/1000;
	    	double[][] histoSurr = Stat.histogramBins(signalDouble, binWidth, signalMinSurr, signalMaxSurr);   //histoSurr[0][] are the center bin values  hist[1][] are the frequencies of the bins
	    	//Additionally with a Flanagan plot
	    	//double[][] histoSurr = Stat.histogramBinsPlot(signalDouble, binWidth, signalMinSurr, signalMaxSurr);   //histoSurr[0][] are the center bin values  hist[1][] are the frequencies of the bins
	    	
	    	probabilitiesSurr = new double[histoSurr[1].length][nSurr]; 

			for (int p=0; p<probabilitiesSurr.length; p++) {	
				probabilitiesSurr[p][n] = histoSurr[1][p];
				totalsMaxSurr = totalsMaxSurr + histoSurr[1][p]; // calculate total count for normalization
			}		
			// normalization
			double sumPSurr = 0.0;
			for (int p = 0; p < probabilitiesSurr.length; p++) {	
				probabilitiesSurr[p][n] = probabilitiesSurr[p][n] / totalsMaxSurr;	
				sumPSurr = sumPSurr + probabilitiesSurr[p][n];
			}
			System.out.println("PlotOpGenEntropy: Sum of surrogte probabilities: " + sumPSurr);
					
			fireProgressChanged(50);
			if (isCancelled(getParentTask())) return null;
		}
		probabilitiesSurrMean = this.calcSurrMean(probabilitiesSurr);
		
		return probabilitiesSurrMean;
	}
	//------------------------------------------------------------------------------------------------------

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
	
	/**
	 * This method calculates the mean of a data series
	 * 
	 * @param data1D[][numberOfSurrogates]
	 * @return double Mean
	 */
	private double[] calcSurrMean(double[][] data1D) {
		double surrMean[] = new double [data1D.length];
		for (int p = 0; p <  data1D.length; p++) {	
			for (int n = 0; n < data1D[0].length; n++ ) {
				surrMean[p] = surrMean[p] + data1D[p][n]/data1D[0].length;
			}	
		}
		return surrMean;
	}


	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new PlotOpGenEntropyDescriptor().getName();
		}
		return name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpGenEntropyDescriptor.TYPE;
	}

}
