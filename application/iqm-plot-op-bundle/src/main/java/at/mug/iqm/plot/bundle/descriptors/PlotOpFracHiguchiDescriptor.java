package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFracHiguchiDescriptor.java
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
import at.mug.iqm.plot.bundle.gui.PlotGUI_FracHiguchi;
import at.mug.iqm.plot.bundle.op.PlotOpFracHiguchi;
import at.mug.iqm.plot.bundle.validators.PlotOpFracHiguchiValidator;


/**
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpFracHiguchiDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2669854965724001785L;

	public static final OperatorType TYPE = OperatorType.PLOT;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpFracHiguchi" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Fractal Higuchi Dimension for plots" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "The number of data series (k)" },
			{ "arg1Desc", "The regression start point" },
			{ "arg2Desc", "The regression end point" },
			{ "arg3Desc", "Surrogate Method" }, //-1 no surrogate
			{ "arg4Desc", "Number of Surrogates" },
			{ "arg5Desc", "A flag whether or not the plot should be shown" },
			{ "arg6Desc", "A flag whether or not the existing plot should be closed" },
			{ "arg7Desc", "The calculation method" },
			{ "arg8Desc", "The box length" } };

	private static final int numSources = 1;
	private static final String[] paramNames = { "numK", "regStart", "regEnd", "typeSurr", "nSurr",
			"showPlot", "deletePlot", "method", "boxLength" };

	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 3, 1, 3, -1, 0,  1, 1, 0, 100 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 3,  Integer.MAX_VALUE),
			new Range(Integer.class, 1,  Integer.MAX_VALUE),
			new Range(Integer.class, 3,  Integer.MAX_VALUE),
			new Range(Integer.class, -1, Integer.MAX_VALUE),
			new Range(Integer.class, 0,  Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), 
			new Range(Integer.class, 10, Integer.MAX_VALUE)};

	/**
	 * constructor
	 */
	public PlotOpFracHiguchiDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpFracHiguchiDescriptor odesc = new PlotOpFracHiguchiDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpFracHiguchi.class,
				PlotGUI_FracHiguchi.class, PlotOpFracHiguchiValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
