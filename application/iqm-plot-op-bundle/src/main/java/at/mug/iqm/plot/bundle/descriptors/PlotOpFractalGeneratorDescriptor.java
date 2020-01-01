package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFractalGeneratorDescriptor.java
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
import at.mug.iqm.plot.bundle.gui.PlotGUI_FractalGenerator;
import at.mug.iqm.plot.bundle.op.PlotOpFractalGenerator;
import at.mug.iqm.plot.bundle.validators.PlotOpFractalGeneratorValidator;


/**
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpFractalGeneratorDescriptor extends
		AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3942394797743315766L;

	public static final OperatorType TYPE = OperatorType.PLOT_GENERATOR;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.PLOT };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpFractalGenerator" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Fractal Signal Generator Function" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "The sampling method" },
			{ "arg1Desc", "The Hurst coefficient" },
			{ "arg2Desc", "The number of data points 2^n" },
			{ "arg3Desc", "The standard deviation" },
			{ "arg4Desc", "fGn to fBm conversion" },
			{ "arg5Desc", "fBm to fGn" }
			};

	private static final int numSources = 0;
	private static final String[] paramNames = { "method", "hurst",
			"powerNDataPoints", "stdDev", "fGnTofBm", "fBmTofGn" };

	private static final Class[] paramClasses = { Integer.class, Double.class,
			Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 0, 0.5d, 10, 1, 0, 0 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 1),
			new Range(Double.class, 0.0d, 1.0d),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1) };

	/**
	 * constructor
	 */
	public PlotOpFractalGeneratorDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpFractalGeneratorDescriptor odesc = new PlotOpFractalGeneratorDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpFractalGenerator.class,
				PlotGUI_FractalGenerator.class,
				PlotOpFractalGeneratorValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
