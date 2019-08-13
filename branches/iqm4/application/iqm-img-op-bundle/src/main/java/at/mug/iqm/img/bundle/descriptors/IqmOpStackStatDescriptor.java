package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpStackStatDescriptor.java
 * 
 * $Id$
 * $HeadURL$
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_StackStat;
import at.mug.iqm.img.bundle.op.IqmOpStackStat;
import at.mug.iqm.img.bundle.validators.IqmOpStackStatValidator;

/**
 * @author Ahammer
 * @since 2012 01
 */
@SuppressWarnings("rawtypes")
public class IqmOpStackStatDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9018366960436050325L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };
	
	private static final String[][] resources = {
			{ "GlobalName", "IqmOpStackStat" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Image stack statistics" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "grey value tolerance" }, // only
																			// for
																			// Alikeness
			{ "arg1Desc", "method" }, // 0 Range, 1 Mean, 2 StdDev, 3Energy, 4
										// Entropy, 5 Skewness, 6 Kurtosis, 7
										// Alikeness
			{ "arg2Desc", "ResultOptions" }, // Clamp, Normalize, Actual
	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "GreyTolerance", "Method",
			"ResultOptions" };
	
	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class };
	private static final Object[] paramDefaults = { 10, 1, 1 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 100), new Range(Integer.class, 0, 7),
			new Range(Integer.class, 0, 2), };

	/**
	 * constructor
	 */
	public IqmOpStackStatDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpStackStatDescriptor descriptor = new IqmOpStackStatDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpStackStatRIF rif = new IqmOpStackStatRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM",
				rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpStackStatDescriptor odesc = new IqmOpStackStatDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpStackStat.class, OperatorGUI_StackStat.class,
				IqmOpStackStatValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
