package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpComplLogDepthDescriptor.java
 * 
 * $Id: PlotOpComplLogDepthDescriptor.java 505 2015-01-09 09:19:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-plot-op-bundle/src/main/java/at/mug/iqm/plot/bundle/descriptors/PlotOpComplLogDepthDescriptor.java $
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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
import at.mug.iqm.plot.bundle.gui.PlotGUI_ComplLogDepth;
import at.mug.iqm.plot.bundle.op.PlotOpComplLogDepth;
import at.mug.iqm.plot.bundle.validators.PlotOpComplLogDepthValidator;


/**
 * 
 * @author Helmut Ahammer
 * @date   2015 01
 * @update 2018 03 HA Surrogate option
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpComplLogDepthDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6273310407437522925L;
	
	
	public static final int COMPRESSION_ZLIB = 0;
	public static final int COMPRESSION_GZIB = 1;

	public static final OperatorType TYPE = OperatorType.PLOT;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpComplLogDepth" },
			{ "Vendor",      "mug.qmnm" },
			{ "Description", "Logical depth and Kolmogorov complexity for plots" },
			{ "DocURL",      "https://sourceforge.net/projects/iqm/" },
			{ "Version",   "1.0" },
			{ "arg0Desc", "Compression method" },
			{ "arg1Desc", "Number of iterations" },
			{ "arg2Desc", "The calculation method" },  //gliding or not
			{ "arg3Desc", "The box length" },
			{ "arg4Desc", "Surrogate Method" }, //-1 no surrogate
			{ "arg5Desc", "Number of Surrogates" },
			};

	private static final int numSources = 1;
	private static final String[] paramNames       = { "compression", "iterations", "method", "boxLength", "typeSurr", "nSurr" };
	private static final Class[]  paramClasses     = { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults    = {0, 10, 0, 100, -1, 1};
	private static final Range[]  validParamValues = {new Range(Integer.class, 0, 1), 
													  new Range(Integer.class, 0, Integer.MAX_VALUE),
													  new Range(Integer.class, 0, 1),
													  new Range(Integer.class, 10, Integer.MAX_VALUE),
													  new Range(Integer.class, -1, Integer.MAX_VALUE),
													  new Range(Integer.class,  0, Integer.MAX_VALUE)
													  };
	/**
	 * constructor
	 */
	public PlotOpComplLogDepthDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpComplLogDepthDescriptor odesc = new PlotOpComplLogDepthDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpComplLogDepth.class, PlotGUI_ComplLogDepth.class,
				PlotOpComplLogDepthValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
