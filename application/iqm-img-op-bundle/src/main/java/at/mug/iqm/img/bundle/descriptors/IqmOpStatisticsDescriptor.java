package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpStatisticsDescriptor.java
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


import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.Range;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.AbstractOperatorDescriptor;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.img.bundle.gui.OperatorGUI_Statistics;
import at.mug.iqm.img.bundle.op.IqmOpStatistics;
import at.mug.iqm.img.bundle.validators.IqmOpStatisticsValidator;

/**
 * @author Ahammer
 * @since   2009 06
 */
@SuppressWarnings("rawtypes")
public class IqmOpStatisticsDescriptor extends AbstractOperatorDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -529339992834562934L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpStatistics" },
			{ "Vendor", "mug.qmnm" }, { "Description", "Statisticss images" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "Binary" },
			{ "arg1Desc", "Order1" }, { "arg2Desc", "Order2" },
			{ "arg3Desc", "Range" }, { "arg4Desc", "Distance" },
			{ "arg5Desc", "Direction" }, { "arg6Desc", "Background" }, };

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Binary", "Order1", "Order2",
			"Range", "Distance", "Direction", "Background" };
	
	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class };
	private static final Object[] paramDefaults = { 1, 1, 0, 5, 1, 4, 0 }; //
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1),
			new Range(Integer.class, 1, 8), // 2^n n = 1,...,8
			new Range(Integer.class, 0, 100), new Range(Integer.class, 0, 4),
			new Range(Integer.class, 0, 1) };

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpStatisticsDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpStatisticsDescriptor descriptor = new IqmOpStatisticsDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpStatisticsRIF rif = new IqmOpStatisticsRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM",
				rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpStatisticsDescriptor odesc = new IqmOpStatisticsDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpStatistics.class, OperatorGUI_Statistics.class,
				IqmOpStatisticsValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
