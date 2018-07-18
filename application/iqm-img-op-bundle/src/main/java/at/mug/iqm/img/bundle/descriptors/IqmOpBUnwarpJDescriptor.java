package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpBUnwarpJDescriptor.java
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
import at.mug.iqm.api.operator.StackProcessingType;
import at.mug.iqm.img.bundle.gui.OperatorGUI_BUnwarpJ;
import at.mug.iqm.img.bundle.op.IqmOpBUnwarpJ;
import at.mug.iqm.img.bundle.validators.IqmOpBUnwarpJValidator;

/**
 * @author Ahammer
 * @since 2009 05
 */
@SuppressWarnings("rawtypes")
public class IqmOpBUnwarpJDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3820982359396797672L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	public static final StackProcessingType STACK_PROCESSING_TYPE = StackProcessingType.SINGLE_STACK_SEQUENTIAL;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };
	
	private static final String[][] resources = {
			{ "GlobalName", "IqmOpBUnwarpJ" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Image Registration using ImageJ plugin BUnwarpJ" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "RegMode" },
			{ "arg1Desc", "SubSamp" }, { "arg2Desc", "InitDef" },
			{ "arg3Desc", "FinalDef" }, { "arg4Desc", "DivW" },
			{ "arg5Desc", "CurlW" }, { "arg6Desc", "LandW" },
			{ "arg7Desc", "ImgW" }, { "arg8Desc", "ConsW" },
			{ "arg9Desc", "StopTh" }, };
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 2;
	private static final String[] paramNames = { "RegMode", "SubSamp",
			"InitDef", "FinalDef", "DivW", "CurlW", "LandW", "ImgW", "ConsW",
			"StopTh" };
	
	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Float.class, Float.class,
			Float.class, Float.class, Float.class, Float.class };
	private static final Object[] paramDefaults = { 0, 0, 0, 2, 0.1f, 0.1f,
			0.0f, 0.1f, 10.0f, 0.01f };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 2), new Range(Integer.class, 0, 7),
			new Range(Integer.class, 0, 3), new Range(Integer.class, 0, 3),
			new Range(Float.class, 0.0f, Float.MAX_VALUE),
			new Range(Float.class, 0.0f, Float.MAX_VALUE),
			new Range(Float.class, 0.0f, Float.MAX_VALUE),
			new Range(Float.class, 0.0f, Float.MAX_VALUE),
			new Range(Float.class, 0.0f, Float.MAX_VALUE),
			new Range(Float.class, 0.0f, Float.MAX_VALUE) };

	/**
	 * constructor
	 */
	public IqmOpBUnwarpJDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES, STACK_PROCESSING_TYPE);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpBUnwarpJDescriptor descriptor = new IqmOpBUnwarpJDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpBUnwarpJRIF rif = new IqmOpBUnwarpJRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpBUnwarpJDescriptor odesc = new IqmOpBUnwarpJDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpBUnwarpJ.class, OperatorGUI_BUnwarpJ.class,
				IqmOpBUnwarpJValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}
} // END
