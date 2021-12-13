package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpSymbolicAggregationDescriptor.java
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
import at.mug.iqm.plot.bundle.gui.PlotGUI_SymbolicAggregation;
import at.mug.iqm.plot.bundle.op.PlotOpSymbolicAggregation;
import at.mug.iqm.plot.bundle.validators.PlotOpSymbolicAggregationValidator;


/**
 * According to Lin, Keogh, Lonardi Chiu, A Symbolic Representation of Time Series, with Implications for Streaming Algorithms. 
 * Proceedings of the 8th ACM SIGMOD Workshop on Research Issues in Data Mining and Knowledge Discovery 2003
 * 
 * @author Ahammer
 * @since   2014 01
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpSymbolicAggregationDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7221918146941733152L;
	
	public static final int COLORMODEL_GREY  = 0;
	public static final int COLORMODEL_COLOR = 1;

	public static final OperatorType TYPE = OperatorType.PLOT;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpSymbolicAggregation" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Symbolic Aggregation" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Spinner - Aggregation length" },          //2,3,4,5,...
			{ "arg1Desc", "Spinner - Alphabet size" },               //4 ?
			{ "arg2Desc", "Spinner - Word length" },	             //2,3,.
			{ "arg3Desc", "Spinner - Subword length" },		         //1,2,3,....Word size
			{ "arg4Desc", "Spinner - Magnification for output image" },//1,2,3,.....
			{ "arg5Desc", "Button  - Colormodel for output image" }, //Grey, Color
			};

	private static final int numSources = 1;
	private static final String[] paramNames = {"aggLength",
												"alphabetSize", 
												"wordLength",
												"subWordLength",
												"mag",
												"colorModel" };

	private static final Class[] paramClasses = {Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = {2, 4, 4, 2, 100, COLORMODEL_COLOR};
	private static final Range[] validParamValues = {new Range(Integer.class, 2, Integer.MAX_VALUE), 
													 new Range(Integer.class, 2, 10),
													 new Range(Integer.class, 2, Integer.MAX_VALUE),
													 new Range(Integer.class, 1, Integer.MAX_VALUE),
													 new Range(Integer.class, 1, Integer.MAX_VALUE),
	 												 new Range(Integer.class, 0, Integer.MAX_VALUE) };
	/**
	 * constructor
	 */
	public PlotOpSymbolicAggregationDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpSymbolicAggregationDescriptor odesc = new PlotOpSymbolicAggregationDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpSymbolicAggregation.class, PlotGUI_SymbolicAggregation.class,
				PlotOpSymbolicAggregationValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
