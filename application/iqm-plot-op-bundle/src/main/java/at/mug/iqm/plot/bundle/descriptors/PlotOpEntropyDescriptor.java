package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpEntropyDescriptor.java
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


import javax.media.jai.util.Range;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.AbstractOperatorDescriptor;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.plot.bundle.gui.PlotGUI_Entropy;
import at.mug.iqm.plot.bundle.op.PlotOpEntropy;
import at.mug.iqm.plot.bundle.validators.PlotOpEntropyValidator;


/**
 * 
 * @author Philipp Kainz
 * <li>2014 02 HA: changed float to double for r
 * @update 2018-03 added surrogate option 
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpEntropyDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4493207854982165124L;

	public static final OperatorType TYPE = OperatorType.PLOT;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpEntropy" },
			{ "Vendor",      "mug.qmnm" },
			{ "Description", "Entropy Functions for plots" },
			{ "DocURL",      "https://sourceforge.net/projects/iqm/" },
			{ "Version",   "2.0" },
			{ "arg0Desc", "Surrogate Method" }, //-1 no surrogate
			{ "arg1Desc", "Number of Surrogates" },
			{ "arg2Desc",  "A flag whether or not the ApEn should be calculated" },
			{ "arg3Desc",  "A flag whether or not the Sample Entropy should be calculated" },
			{ "arg4Desc",  "A flag whether or not the QSE should be calculated" },
			{ "arg5Desc",  "A flag whether or not the COSEn should be calculated" },
			{ "arg6Desc",  "A flag whether or not the PEn should be calculated" },
			{ "arg7Desc",  "The ApEn parameter 'M'" },
			{ "arg8Desc",  "The ApEn parameter 'R'" },
			{ "arg9Desc",  "The ApEn parameter 'D'" },
			{ "arg10Desc",  "The Sample Entropy parameter 'M'" },
			{ "arg11Desc",  "The Sample Entropy parameter 'R'" },
			{ "arg12Desc", "The Sample Entropy parameter 'D'" },
			{ "arg13Desc", "The pEn parameter 'M'" },
			{ "arg14Desc", "The Sample Entropy parameter 'D'" },
			{ "arg15Desc", "The calculation method" },
			{ "arg16Desc", "The box length" } };

	private static final int numSources = 1;
	private static final String[] paramNames = { "typeSurr", "nSurr", "calcApEn", "calcSampEn",
			"calcQSE", "calcCOSEn", "calcPEn", "apEnParam_M", "apEnParam_R",
			"apEnParam_D", "sampEnParam_M", "sampEnParam_R", "sampEnParam_D",
			"pEnParam_M", "pEnParam_D", "method", "boxLength" };

	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Double.class, Integer.class, Integer.class, Double.class,
			Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { -1, 0, 1, 1, 1, 0, 1, 2, 0.2d, 1,
			2, 0.2d, 1, 2, 1, 0, 100 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, -1, Integer.MAX_VALUE),
			new Range(Integer.class, 0,  Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 1, 100),
			new Range(Double.class, 0.05d, 1.0d),
			new Range(Integer.class, 1, 100), new Range(Integer.class, 1, 100),
			new Range(Double.class, 0.05d, 1.0d),
			new Range(Integer.class, 1, 100), new Range(Integer.class, 1, 100),
			new Range(Integer.class, 1, 100), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 10, Integer.MAX_VALUE) };

	/**
	 * constructor
	 */
	public PlotOpEntropyDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpEntropyDescriptor odesc = new PlotOpEntropyDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpEntropy.class, PlotGUI_Entropy.class,
				PlotOpEntropyValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
