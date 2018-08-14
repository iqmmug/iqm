package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracHiguchiDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_FracHiguchi;
import at.mug.iqm.img.bundle.op.IqmOpFracHiguchi;
import at.mug.iqm.img.bundle.validators.IqmOpFracHiguchiValidator;

/**
 * <li> 2011 05 added new options radial and spiral 
 * <li> 2011 08 added new option Single line ROI
 * @author Ahammer
 * @since   2010 04
 * 
 */
@SuppressWarnings("rawtypes")
public class IqmOpFracHiguchiDescriptor extends AbstractOperatorDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5097965093389180122L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE, DataType.PLOT, DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpFracHiguchi" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "calculates the Higuchi dimension of images" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Number of k" },
			{ "arg1Desc", "Append mode" }, // 0 projected, 1 separated, 2
											// Meander, 3 Contiguous, 4 Radial,
											// 5 Spiral, 6 single line ROI
			{ "arg2Desc", "Background option" }, // 0 inclusive, 1 exclusive
			{ "arg3Desc", "Dx option" }, // 0, 1
			{ "arg4Desc", "Dy option" }, // 0, 1
			{ "arg5Desc", "Regression Start" },
			{ "arg6Desc", "Regression End" }, { "arg7Desc", "Show Plot" }, // 0,1
			{ "arg8Desc", "Delete Existing Plot" }, // 0,1
	};

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "NumK", "Append", "Back",
			"Dx", "Dy", "RegStart", "RegEnd", "ShowPlot", "DeleteExistingPlot" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, };
	private static final Object[] paramDefaults = { 3, 0, 0, 1, 1, 1, 3, 1, 1 }; // K
	private static final Range[] validParamValues = {
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 6), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1), };

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpFracHiguchiDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpFracHiguchiDescriptor descriptor = new IqmOpFracHiguchiDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpFracHiguchiRIF rif = new IqmOpFracHiguchiRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpFracHiguchiDescriptor odesc = new IqmOpFracHiguchiDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpFracHiguchi.class, OperatorGUI_FracHiguchi.class,
				IqmOpFracHiguchiValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
