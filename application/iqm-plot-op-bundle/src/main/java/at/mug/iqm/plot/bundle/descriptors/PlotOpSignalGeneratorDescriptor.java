package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpSignalGeneratorDescriptor.java
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


import javax.media.jai.util.Range;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.AbstractOperatorDescriptor;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.plot.bundle.gui.PlotGUI_SignalGenerator;
import at.mug.iqm.plot.bundle.op.PlotOpSignalGenerator;
import at.mug.iqm.plot.bundle.validators.PlotOpSignalGeneratorValidator;


/**
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpSignalGeneratorDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6813562230314308938L;

	public static final OperatorType TYPE = OperatorType.PLOT_GENERATOR;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.PLOT };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpSignalGenerator" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Signal Generator Function" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "The sampling method" },
			{ "arg1Desc", "The frequency" },
			{ "arg2Desc", "The number of periods" },
			{ "arg3Desc", "The sample rate" },
			{ "arg4Desc", "The amplitude of the signal" },
			{ "arg5Desc", "The number of data points" },
			{ "arg6Desc", "The mean value of the Gaussian distribution" },
			{ "arg7Desc", "The standard deviation of the Gaussian distribution" } };

	private static final int numSources = 0;
	private static final String[] paramNames = { "method", "frequency",
			"nPeriods", "sampleRate", "amplitude", "nDataPoints", "mean",
			"stdDev" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class};
	private static final Object[] paramDefaults = { 0, 1, 1, 1000, 1, 1000, 0, 1 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 4),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE) };

	/**
	 * constructor
	 */
	public PlotOpSignalGeneratorDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpSignalGeneratorDescriptor odesc = new PlotOpSignalGeneratorDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpSignalGenerator.class,
				PlotGUI_SignalGenerator.class,
				PlotOpSignalGeneratorValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
