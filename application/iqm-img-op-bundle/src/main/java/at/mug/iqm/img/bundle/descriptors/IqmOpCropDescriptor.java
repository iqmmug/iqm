package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCropDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Crop;
import at.mug.iqm.img.bundle.op.IqmOpCrop;
import at.mug.iqm.img.bundle.validators.IqmOpCropValidator;

/**
 * @author Ahammer
 * @since   2009 06
 */
public class IqmOpCropDescriptor extends AbstractOperatorDescriptor {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 3734994843866421272L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpCrop" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Crops an image" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Method" }, // update 2009 12 Manual or ROI
			{ "arg1Desc", "OffSetX" }, { "arg2Desc", "OffSetY" },
			{ "arg3Desc", "NewWidth" }, { "arg4Desc", "NewHeight" },
			{ "arg5Desc", "OffSet" } };
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Method", "OffSetX",
			"OffSetY", "NewWidth", "NewHeight", "OffSet" };
	@SuppressWarnings("rawtypes")
	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 0, 0, 0, 100, 100, 1 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 5) };

	/**
	 * Default constructor.
	 */
	public IqmOpCropDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpCropDescriptor descriptor = new IqmOpCropDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpCropRIF rif = new IqmOpCropRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpCropDescriptor odesc = new IqmOpCropDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpCrop.class, OperatorGUI_Crop.class,
				IqmOpCropValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
