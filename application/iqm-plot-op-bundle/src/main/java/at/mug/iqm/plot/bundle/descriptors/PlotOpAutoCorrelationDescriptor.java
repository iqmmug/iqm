package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpAutoCorrelationDescriptor.java
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
import at.mug.iqm.plot.bundle.gui.PlotGUI_AutoCorrelation;
import at.mug.iqm.plot.bundle.op.PlotOpAutoCorrelation;
import at.mug.iqm.plot.bundle.validators.PlotOpAutoCorrelationValidator;


/**
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpAutoCorrelationDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3839643165289024926L;

	public static final OperatorType TYPE = OperatorType.PLOT;

	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.PLOT };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpAutoCorrelation" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Auto Correlation Function for plots" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "The method" },
			{ "arg1Desc", "A flag whether or not the delays are limited" },
			{ "arg2Desc", "The number of delays" } };

	private static final int numSources = 1;
	private static final String[] paramNames = { "method", "limitDelays",
			"numDelays" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class };
	private static final Object[] paramDefaults = { 2, 0, 20 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 2), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 1, Integer.MAX_VALUE) };

	/**
	 * constructor
	 */
	public PlotOpAutoCorrelationDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpAutoCorrelationDescriptor odesc = new PlotOpAutoCorrelationDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpAutoCorrelation.class,
				PlotGUI_AutoCorrelation.class,
				PlotOpAutoCorrelationValidator.class, odesc.getType(),
				odesc.getStackProcessingType());

		return odesc;
	}

} // END
