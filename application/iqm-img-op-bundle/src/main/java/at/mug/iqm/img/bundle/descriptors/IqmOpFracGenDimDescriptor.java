package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracGenDimDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_FracGenDim;
import at.mug.iqm.img.bundle.op.IqmOpFracGenDim;
import at.mug.iqm.img.bundle.validators.IqmOpFracGenDimValidator;

/**
 * @author Ahammer
 * @since   2012 03
 */

@SuppressWarnings("rawtypes")
public class IqmOpFracGenDimDescriptor extends AbstractOperatorDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4458391764396816577L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE, DataType.PLOT, DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpFracGenDim" },
			{ "Vendor", "mug.qmnm" },
			{ "Description",
					"calculates the lacunarity and the generalized dimensions" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "minimal q" },
			{ "arg1Desc", "maximal q" }, { "arg2Desc", "# epsilon" },
			{ "arg3Desc", "max epsilon" },
			{ "arg4Desc", "Method eps" }, // 0 Lin, 1 log
			{ "arg5Desc", "Method" }, // 0 Gliding Box, 1 Method2
			{ "arg6Desc", "Regression Start" },
			{ "arg7Desc", "Regression End" }, { "arg8Desc", "Show Plot" }, // 0,1
			{ "arg9Desc", "Delete Existing Plot" }, // 0,1
			{ "arg10Desc", "Show Dq Plot" }, // 0,1
			{ "arg11Desc", "Show F spectrum" }, // 0,1

	};

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "MinQ", "MaxQ", "NumEps",
			"MaxEps", "MethodEps", "Method", "RegStart", "RegEnd", "ShowPlot",
			"DeleteExistingPlot", "ShowPlotDq", "ShowPlotF" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, };
	private static final Object[] paramDefaults = { -5, 5, 2, 2, 0, 0, 1, 2, 1,
			1, 1, 1 }; //
	private static final Range[] validParamValues = {
			new Range(Integer.class, -Integer.MAX_VALUE, Integer.MAX_VALUE),
			new Range(Integer.class, -Integer.MAX_VALUE, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1), };

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpFracGenDimDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpFracGenDimDescriptor descriptor = new IqmOpFracGenDimDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpFracGenDimRIF rif = new IqmOpFracGenDimRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpFracGenDimDescriptor odesc = new IqmOpFracGenDimDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpFracGenDim.class, OperatorGUI_FracGenDim.class,
				IqmOpFracGenDimValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}
	
} // END
