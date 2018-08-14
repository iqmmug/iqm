package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpPointFinderDescriptor.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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
import at.mug.iqm.plot.bundle.gui.PlotGUI_PointFinder;
import at.mug.iqm.plot.bundle.op.PlotOpPointFinder;
import at.mug.iqm.plot.bundle.validators.PlotOpPointFinderValidator;


/**
 * 
 * @author Philipp Kainz
 * @update 2017 Adam Dolgos added several options 
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpPointFinderDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2382146032464255096L;

	public static final OperatorType TYPE = OperatorType.PLOT;
	
	public static final int METHOD_SLOPE   = 0;
	public static final int METHOD_PEAKS   = 1;
	public static final int METHOD_VALLEYS = 2;
	
	public static final int OPTION_THRES = 3;
	public static final int OPTION_MAC   = 4;

	public static final int SLOPE_POSITIVE = 5;
	public static final int SLOPE_NEGATIVE = 6;
	
	public static final int OUTPUTOPTION_COORDINATES  = 7;
	public static final int OUTPUTOPTION_INTERVALS    = 8;
	public static final int OUTPUTOPTION_HEIGHTS      = 9;
	public static final int OUTPUTOPTION_DELTAHEIGHTS = 10;
	public static final int OUTPUTOPTION_ENERGIES     = 11;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.PLOT };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpPointFinder" }, 
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Point Finder Function for plots" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "2.0" }, 
			{ "arg0Desc", "method" }, //Slope, Peaks, Valleys
			{ "arg1Desc", "option" }, //Threshold, MAC, ....
			{ "arg2Desc", "The threshold value" },    //for Threshold  
			{ "arg3Desc", "The scaling down value" }, //for Threshold and MAC
			{ "arg4Desc", "Tau" },    //for MAC
			{ "arg5Desc", "Offset" }, //for MAC	
			{ "arg6Desc", "The slope value" },
			{ "arg7Desc", "Output Options"}}; //Coordinates, Intervals, Heights, DeltaHeights, Energies

	private static final int numSources = 1;
	private static final String[] paramNames = { "method", "options", "threshold", "scaledown", "tau", "offset", "slope", "outputoptions" };

	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class,  Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { METHOD_SLOPE, OPTION_THRES, 1, 1, 1, 0, SLOPE_POSITIVE, OUTPUTOPTION_INTERVALS};
	private static final Range[] validParamValues = {
			new Range(Integer.class, METHOD_SLOPE, METHOD_VALLEYS),
			new Range(Integer.class, OPTION_THRES, OPTION_MAC),
			new Range(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, SLOPE_POSITIVE, SLOPE_NEGATIVE),
			new Range(Integer.class, OUTPUTOPTION_COORDINATES, OUTPUTOPTION_ENERGIES)};

	/**
	 * constructor
	 */
	public PlotOpPointFinderDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpPointFinderDescriptor odesc = new PlotOpPointFinderDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpPointFinder.class,
				PlotGUI_PointFinder.class, PlotOpPointFinderValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
