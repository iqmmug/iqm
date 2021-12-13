package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpThresholdDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Threshold;
import at.mug.iqm.img.bundle.op.IqmOpThreshold;
import at.mug.iqm.img.bundle.validators.IqmOpThresholdValidator;

/**
 * @author Ahammer
 * @since   2009 04
 */
@SuppressWarnings("rawtypes")
public class IqmOpThresholdDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5458954515408712605L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpThreshold" }, { "Vendor", "mug.qmnm" },
			{ "Description", "thresholds images" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "color space options" },
			{ "arg1Desc", "band1 low threshold level" },
			{ "arg2Desc", "band1 high threshold level" },
			{ "arg3Desc", "band2 low threshold level" },
			{ "arg4Desc", "band2 high threshold level" },
			{ "arg5Desc", "band3 low threshold level" },
			{ "arg6Desc", "band3 high threshold level" },
			{ "arg7Desc", "link slider option" },
			{ "arg8Desc", "binarize option" },
			{ "arg9Desc", "lower threshold preset options" },

	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Color", "ThresholdLow1",
			"ThresholdHigh1", "ThresholdLow2", "ThresholdHigh2",
			"ThresholdLow3", "ThresholdHigh3", "LinkSlider", "Binarize",
			"Preset" };
	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 0, // Grey
			0, 100, 0, 100, 0, 100, 1, 0, 0 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 6), // Grey, RGB, HSV, HLS, CIELAB,
											// CIELUV, XYZ
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 8) };

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpThresholdDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpThresholdDescriptor descriptor = new IqmOpThresholdDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpThresholdRIF rif = new IqmOpThresholdRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpThresholdDescriptor odesc = new IqmOpThresholdDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpThreshold.class,
				OperatorGUI_Threshold.class,
				IqmOpThresholdValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
