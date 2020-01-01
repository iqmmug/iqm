package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFracDFADescriptor.java
 * 
 * $Id: PlotOpFracDFADescriptor.java 630 2018-03-14 13:05:20Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-plot-op-bundle/src/main/java/at/mug/iqm/plot/bundle/descriptors/PlotOpFracDFADescriptor.java $
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
import at.mug.iqm.plot.bundle.gui.PlotGUI_FracDFA;
import at.mug.iqm.plot.bundle.op.PlotOpFracDFA;
import at.mug.iqm.plot.bundle.validators.PlotOpFracDFAValidator;


/**
 * 
 * @author HA
 * @date 2018-04-13 DFA according to Peng etal 1994 and e.g. Hardstone etal. 2012 
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpFracDFADescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1186140306638937988L;

	public static final OperatorType TYPE = OperatorType.PLOT;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpFracDFA" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Fractal DFA Dimension for plots" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Window size" },
			{ "arg1Desc", "The regression start point" },
			{ "arg2Desc", "The regression end point" },
			{ "arg3Desc", "Surrogate Method" }, //-1 no surrogate
			{ "arg4Desc", "Number of Surrogates" },
			{ "arg5Desc", "A flag whether or not the plot should be shown" },
			{ "arg6Desc", "A flag whether or not the existing plot should be closed" },
			{ "arg7Desc", "The calculation method" },
			{ "arg8Desc", "The box length" } };

	private static final int numSources = 1;
	private static final String[] paramNames = { "winSize", "regStart", "regEnd", "typeSurr", "nSurr",
			"showPlot", "deletePlot", "method", "boxLength" };

	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 100, 1, 3, -1, 0,  1, 1, 0, 100 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 4,  Integer.MAX_VALUE),
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
	public PlotOpFracDFADescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpFracDFADescriptor odesc = new PlotOpFracDFADescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpFracDFA.class,
				PlotGUI_FracDFA.class, PlotOpFracDFAValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
