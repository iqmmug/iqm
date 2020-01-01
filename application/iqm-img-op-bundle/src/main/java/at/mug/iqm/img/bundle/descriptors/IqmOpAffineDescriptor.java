package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpAffineDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Affine;
import at.mug.iqm.img.bundle.op.IqmOpAffine;
import at.mug.iqm.img.bundle.validators.IqmOpAffineValidator;

/**
 * @author Ahammer
 * @since 2009 06
 */
@SuppressWarnings({"rawtypes"})
public class IqmOpAffineDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -548810328632423507L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpAffine" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "affine transformation of an image" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "m00" }, { "arg1Desc", "m10" },
			{ "arg2Desc", "m01" }, { "arg3Desc", "m11" },
			{ "arg4Desc", "m02" }, { "arg5Desc", "m12" },
			{ "arg6Desc", "interpolation" }, };
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "M00", "M10", "M01", "M11",
			"M02", "M12", "Interpolation" };
	private static final Class[] paramClasses = { Float.class, Float.class,
			Float.class, Float.class, Float.class, Float.class, Integer.class };
	private static final Object[] paramDefaults = { 1.0f, 0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1 };
	private static final Range[] validParamValues = {
			new Range(Float.class, -Float.MAX_VALUE, Float.MAX_VALUE),
			new Range(Float.class, -Float.MAX_VALUE, Float.MAX_VALUE),
			new Range(Float.class, -Float.MAX_VALUE, Float.MAX_VALUE),
			new Range(Float.class, -Float.MAX_VALUE, Float.MAX_VALUE),
			new Range(Float.class, -Float.MAX_VALUE, Float.MAX_VALUE),
			new Range(Float.class, -Float.MAX_VALUE, Float.MAX_VALUE),
			new Range(Integer.class, 0, 3) };

	/**
	 * constructor
	 */
	public IqmOpAffineDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpAffineDescriptor descriptor = new IqmOpAffineDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpAffineRIF rif = new IqmOpAffineRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpAffineDescriptor odesc = new IqmOpAffineDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpAffine.class, OperatorGUI_Affine.class,
				IqmOpAffineValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
