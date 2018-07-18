package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracPyramidDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_FracPyramid;
import at.mug.iqm.img.bundle.op.IqmOpFracPyramid;
import at.mug.iqm.img.bundle.validators.IqmOpFracPyramidValidator;

/**
 * @author Ahammer
 * @since   2009 07
 * @update 2015 06 HA added Information Dimension Option
 */
@SuppressWarnings("rawtypes")
public class IqmOpFracPyramidDescriptor extends AbstractOperatorDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 472180587987361883L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE, DataType.PLOT, DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpFracPyramid" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "calculates the fractal dimension using image pyramids" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Number of pyramid images" },
			{ "arg1Desc", "Interpolation" }, // NearestNeigh, Bilinear, Bicubic,Bicubic2
			{ "arg2Desc", "GreyMode" }, // 0: Gradient, 1 Blanket, 2 Allometric, 3 Information, 4 PDM, 5 KC
			{ "arg3Desc", "Regression Start" },
			{ "arg4Desc", "Regression End" },
			{ "arg5Desc", "Show Plot" },            // 0,1
			{ "arg6Desc", "Delete Existing Plot" }, // 0,1
	};

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "NumPyrImgs", "Interpolation", "GreyMode", 
			                                     "RegStart", "RegEnd", "ShowPlot", "DeleteExistingPlot" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, };
	private static final Object[] paramDefaults = { 3, 0, 4, 1, 2, 1, 1 }; // Count NearestNeigh, Bilinear, Bicubic, Bicubic2
	private static final Range[] validParamValues = {
			new Range(Integer.class, 3, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 4),
			new Range(Integer.class, 0, 5),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), };

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpFracPyramidDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpFracPyramidDescriptor descriptor = new IqmOpFracPyramidDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpFracPyramidRIF rif = new IqmOpFracPyramidRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpFracPyramidDescriptor odesc = new IqmOpFracPyramidDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpFracPyramid.class,
				OperatorGUI_FracPyramid.class, IqmOpFracPyramidValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
