package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpImgStabilizerDescriptor.java
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
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.StackProcessingType;
import at.mug.iqm.img.bundle.gui.OperatorGUI_ImgStabilizer;
import at.mug.iqm.img.bundle.op.IqmOpImgStabilizer;
import at.mug.iqm.img.bundle.validators.IqmOpImgStabilizerValidator;

/**
 * @author Ahammer
 * @since 2010 05
 */
@SuppressWarnings("rawtypes")
public class IqmOpImgStabilizerDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6100002822241066808L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	public static final StackProcessingType STACK_PROCESSING_TYPE = StackProcessingType.SINGLE_STACK_SEQUENTIAL;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpImgStabilizer" },
			{ "Vendor", "mug.qmnm" },
			{ "Description",
					"Image Registration using ImageJ plugin Image_Stabilizer" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "RegMode" },
			{ "arg1Desc", "PyrLevels" }, { "arg2Desc", "TempUpCo" },
			{ "arg3Desc", "MaxIt" }, { "arg4Desc", "ErrTol" },
			{ "arg5Desc", "LogCo" }, };

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 2;
	private static final String[] paramNames = { "RegMode", "PyrLevels",
			"TempUpCo", "MaxIt", "ErrTol", "LogCo" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Float.class, Float.class, Float.class, Integer.class };
	private static final Object[] paramDefaults = { 0, 1, 0.9f, 200f, 0.001f, 0 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 4),
			new Range(Float.class, 0.0f, Float.MAX_VALUE),
			new Range(Float.class, 0.0f, Float.MAX_VALUE),
			new Range(Float.class, 0.0f, Float.MAX_VALUE),
			new Range(Integer.class, 0, 1), };

	/**
	 * constructor
	 */
	public IqmOpImgStabilizerDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES, STACK_PROCESSING_TYPE);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpImgStabilizerDescriptor descriptor = new IqmOpImgStabilizerDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpImgStabilizerRIF rif = new IqmOpImgStabilizerRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	public static IOperatorDescriptor register() {
		IqmOpImgStabilizerDescriptor odesc = new IqmOpImgStabilizerDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpImgStabilizer.class,
				OperatorGUI_ImgStabilizer.class,
				IqmOpImgStabilizerValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
