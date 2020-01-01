package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpStatisticsDescriptor.java
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


import javax.media.jai.util.Range;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.AbstractOperatorDescriptor;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.plot.bundle.gui.PlotGUI_Statistics;
import at.mug.iqm.plot.bundle.op.PlotOpStatistics;
import at.mug.iqm.plot.bundle.validators.PlotOpStatisticsValidator;


/**
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpStatisticsDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1842148496121383472L;

	public static final OperatorType TYPE = OperatorType.PLOT;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpStatistics" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Statistics Function for plots" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "arg0Desc", "The calculation method" }, //single values or gliding values
			{ "arg1Desc", "Gliding box length" },
			{ "arg2Desc", "A flag whether or not the number of data points should be calculated" },
			{ "arg3Desc", "A flag whether or not the minimum value should be calculated" },
			{ "arg4Desc", "A flag whether or not the maximum value should be calculated" },
			{ "arg5Desc", "A flag whether or not the mean value should be calculated" },
			{ "arg6Desc", "A flag whether or not the median value should be calculated" },
			{ "arg7Desc", "A flag whether or not the standard deviation should be calculated" },
			{ "arg8Desc", "A flag whether or not the kurtosis should be calculated" },
			{ "arg9Desc", "A flag whether or not the skewness should be calculated" },
			{ "arg10Desc","A flag whether or not the Shannon entropy value should be calculated" } };

	private static final int numSources = 1;
	private static final String[] paramNames = { "method", "boxLength",
												 "calcNDataPoints", "calcMin", "calcMax", 
												 "calcMean", "calcMedian", "calcStdDev",
												 "calcKurtosis", "calcSkewness", "calcShannonEn" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
												  Integer.class, Integer.class, Integer.class,
												  Integer.class, Integer.class, Integer.class,
												  Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 0, 100, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	private static final Range[] validParamValues = {
		    new Range(Integer.class, 0, 1), new Range(Integer.class, 10, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1) };

	/**
	 * constructor
	 */
	public PlotOpStatisticsDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpStatisticsDescriptor odesc = new PlotOpStatisticsDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpStatistics.class,
				PlotGUI_Statistics.class,
				PlotOpStatisticsValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
