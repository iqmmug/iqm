package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpHRVDescriptor.java
 * 
 * $Id: PlotOpHRVDescriptor.java 505 2015-01-09 09:19:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-plot-op-bundle/src/main/java/at/mug/iqm/plot/bundle/descriptors/PlotOpHRVDescriptor.java $
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
import at.mug.iqm.plot.bundle.gui.PlotGUI_HRV;
import at.mug.iqm.plot.bundle.op.PlotOpHRV;
import at.mug.iqm.plot.bundle.validators.PlotOpHRVValidator;


/**
 * 
 * @author Helmut Ahammer
 * @date   2018 10
 * @update 
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpHRVDescriptor extends AbstractOperatorDescriptor {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1366939788708762755L;
	
	public static final OperatorType TYPE = OperatorType.PLOT;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpHRV" },
			{ "Vendor",      "mug.qmnm" },
			{ "Description", "standard HRV parameters" },
			{ "DocURL",      "https://sourceforge.net/projects/iqm/" },
			{ "Version",     "1.0" },
			{ "arg0Desc", "Method standard" },
			{ "arg1Desc", "Method advanced" },
			{ "arg2Desc", "Time base in s or ms" },
			{ "arg3Desc", "The calculation method" },  //gliding or not
			{ "arg4Desc", "The box length" },
			{ "arg5Desc", "Surrogate Method" }, //-1 no surrogate
			{ "arg6Desc", "Number of Surrogates" },
			};

	private static final int numSources = 1;
	private static final String[] paramNames       = { "standard", "advanced", "timeBase", "method", "boxLength", "typeSurr", "nSurr" };
	private static final Class[]  paramClasses     = { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults    = {1, 0, 0, 0, 100, -1, 1};
	private static final Range[]  validParamValues = {new Range(Integer.class, 0, 1), 
													  new Range(Integer.class, 0, 1),
													  new Range(Integer.class, 1, Integer.MAX_VALUE),
													  new Range(Integer.class, 0, 1),
													  new Range(Integer.class, 10, Integer.MAX_VALUE),
													  new Range(Integer.class, -1, Integer.MAX_VALUE),
													  new Range(Integer.class,  0, Integer.MAX_VALUE)
													  };
	/**
	 * constructor
	 */
	public PlotOpHRVDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpHRVDescriptor odesc = new PlotOpHRVDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpHRV.class, PlotGUI_HRV.class,
				PlotOpHRVValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
