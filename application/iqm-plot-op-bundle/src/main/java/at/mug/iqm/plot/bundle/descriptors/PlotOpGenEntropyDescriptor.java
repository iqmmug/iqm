package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpGenEntropyDescriptor.java
 * 
 * $Id: PlotOpGenEntropyDescriptor.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-plot-op-bundle/src/main/java/at/mug/iqm/plot/bundle/descriptors/PlotOpGenEntropyDescriptor.java $
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


import javax.media.jai.util.Range;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.AbstractOperatorDescriptor;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.plot.bundle.gui.PlotGUI_GenEntropy;
import at.mug.iqm.plot.bundle.op.PlotOpGenEntropy;
import at.mug.iqm.plot.bundle.validators.PlotOpGenEntropyValidator;


/**
 * <li>Generalized Entropies
 * <li>according to a review of Amigó, J.M., Balogh, S.G., Hernández, S., 2018. A Brief Review of Generalized Entropies. Entropy 20, 813. https://doi.org/10.3390/e20110813
 * <li>and to: Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * <li>Renyi, Tsallis, H1,H2,H3 according to Amigo etal.
 * <li>H       according to Amigo etal. 
 * <li>SE      according to Amigo etal. and Tsekouras, G.A.; Tsallis, C. Generalized entropy arising from a distribution of q indices. Phys. Rev. E 2005,
 * <li>SEta    according to Amigo etal. and Anteneodo, C.; Plastino, A.R. Maximum entropy approach to stretched exponential probability distributions. J. Phys. A Math. Gen. 1999, 32, 1089–1098.	
 * <li>SKappa  according to Amigo etal. and Kaniadakis, G. Statistical mechanics in the context of special relativity. Phys. Rev. E 2002, 66, 056125
 * <li>SB      according to Amigo etal. and Curado, E.M.; Nobre, F.D. On the stability of analytic entropic forms. Physica A 2004, 335, 94–106.
 * <li>SBeta   according to Amigo etal. and Shafee, F. Lambert function and a new non-extensive form of entropy. IMA J. Appl. Math. 2007, 72, 785–800.
 * <li>SGamma  according to Amigo etal. and Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S61
 * <li>SNorm   according to Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * <li>SEscort according to Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * 
 * @author Ahammer
 * @since  2018-12-14
 */

@SuppressWarnings({ "rawtypes" })
public class PlotOpGenEntropyDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4493207854982165124L;

	public static final OperatorType TYPE = OperatorType.PLOT;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpGenEntropy" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "computes eneralized dimensions" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "SEta" },
			{ "arg1Desc", "H" }, //H1, H2, H3
			{ "arg2Desc", "Renyi" },
			{ "arg3Desc", "Tsallis" },
			{ "arg4Desc", "SNorm" },
			{ "arg5Desc", "SEscort" },		
			{ "arg6Desc", "SKappa" },
			{ "arg7Desc", "SB" },
			{ "arg8Desc", "SE" },
			{ "arg9Desc", "SBeta" },
			{ "arg10Desc", "SGamma" },			
			{ "arg11Desc",  "minimal q" },
			{ "arg12Desc", "maximal q" },
			{ "arg13Desc", "minimal Eta" },
			{ "arg14Desc", "maximal Eta" },
			{ "arg15Desc", "minimal Kappa" },
			{ "arg16Desc", "maximal Kappa" },
			{ "arg17Desc", "minimal B" },
			{ "arg18Desc", "maximal B" },
			{ "arg19Desc", "minmal Beta" },
			{ "arg20Desc", "maximal Beta" },	
			{ "arg21Desc", "minimal Gamma" },
			{ "arg22Desc", "maximal Gamma" },
			{ "arg23Desc", "Probabilities option" }, //Actual,  pairwise differences, sum of differences, SD,   ......
			{ "arg24Desc", "Epsilon" }, //Distance
			{ "arg25Desc", "Method" }, // 0
			{ "arg26Desc", "Box length" }, // 100
			{ "arg27Desc", "Surrogate Type" }, // -1. 0, 1,....5
			{ "arg28Desc", "# of surrogates" }, //
	};

	private static final int numSources = 1;
	private static final String[] paramNames = {"SE", "H", "Renyi", "Tsallis", "SNorm", "SEscort", "SEta", "SKappa", "SB",  "SBeta", "SGamma", 
											    "MinQ", "MaxQ",
												"MinEta", "MaxEta", "MinKappa", "MaxKappa", "MinB", "MaxB", "MinBeta", "MaxBeta", "MinGamma", "MaxGamma",
												"ProbOption", "Eps",
												"Method", "BoxLength", "TypeSurr", "NSurr"};

	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, 
												  Integer.class, Integer.class,
												  Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class,
												  Integer.class,  Integer.class, 
												  Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
												  -5, 5, 
												  0.1, 1.0, 0.0, 0.9, 1.0, 10.0, 0.5, 1.5, 0.1, 1.0,	
												  1, 1,
												  0, 100, -1, 0};
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 1), //SE
			new Range(Integer.class, 0, 1), //H
			new Range(Integer.class, 0, 1), //Renyi
			new Range(Integer.class, 0, 1), //Tsallis
			new Range(Integer.class, 0, 1), //SNorm
			new Range(Integer.class, 0, 1), //SEscort	
			new Range(Integer.class, 0, 1), //SEta
			new Range(Integer.class, 0, 1), //SKappa
			new Range(Integer.class, 0, 1), //SB		
			new Range(Integer.class, 0, 1), //SBeta
			new Range(Integer.class, 0, 1), //SGamma			
			new Range(Integer.class, -Integer.MAX_VALUE, Integer.MAX_VALUE), //minQ
			new Range(Integer.class, -Integer.MAX_VALUE, Integer.MAX_VALUE), //maxQ		
			new Range(Double.class, Double.MIN_VALUE, Double.MAX_VALUE), //minEta  Eta > 0
			new Range(Double.class, Double.MIN_VALUE, Double.MAX_VALUE), //maxEta  Eta > 0
			new Range(Double.class,              0.0, (1.0-Double.MIN_VALUE)), //minKappa 0 <= Kappa < 1
			new Range(Double.class, Double.MIN_VALUE, (1.0-Double.MIN_VALUE)), //maxKappa 0 <= Kappa < 1
			new Range(Double.class, Double.MIN_VALUE, Double.MAX_VALUE), //minB B > 0
			new Range(Double.class,              1.0, Double.MAX_VALUE), //maxB B > 0
			new Range(Double.class, Double.MIN_VALUE, Double.MAX_VALUE), //minBeta  
			new Range(Double.class, Double.MIN_VALUE, Double.MAX_VALUE), //maxBeta  
			new Range(Double.class,              0.0, 1.0), //minGamma 0 < Gamma < 1	
			new Range(Double.class, Double.MIN_VALUE, 1.0), //maxGamma 0 < Gamma < 1
			new Range(Integer.class, 0, 3), //ProbOption	
			new Range(Integer.class, 1, Integer.MAX_VALUE), //Eps	
			new Range(Integer.class, 0, 1), //Method
 			new Range(Integer.class, 10, Integer.MAX_VALUE), //BoxLength
			new Range(Integer.class, -1, Integer.MAX_VALUE), //TypeSurr
			new Range(Integer.class,  0, Integer.MAX_VALUE)  //NSurr
	}; 

	/**
	 * constructor
	 */
	public PlotOpGenEntropyDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpGenEntropyDescriptor odesc = new PlotOpGenEntropyDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpGenEntropy.class, PlotGUI_GenEntropy.class,
				PlotOpGenEntropyValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
