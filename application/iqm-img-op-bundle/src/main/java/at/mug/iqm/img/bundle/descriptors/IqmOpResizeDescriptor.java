package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpResizeDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Resize;
import at.mug.iqm.img.bundle.op.IqmOpResize;
import at.mug.iqm.img.bundle.validators.IqmOpResizeValidator;

/**
 * @author Ahammer
 * @since   2009 04
 */
@SuppressWarnings("rawtypes")
public class IqmOpResizeDescriptor extends AbstractOperatorDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1127764518303500096L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpResize" },
			{ "Vendor", "mug.qmnm" }, { "Description", "Resizes images" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "zoomX" },
			{ "arg1Desc", "zoomY" }, { "arg2Desc", "newWidth" },
			{ "arg3Desc", "newHeight" }, { "arg4Desc", "Interpolation" }, // NearestNeigh,
																			// Bilinear,
																			// Bicubic,
																			// Bicubic2
			{ "arg5Desc", "zoomOrSize" }, };

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "ZoomX", "ZoomY", "NewWidth",
			"NewHeight", "Interpolation", "ZoomOrSize" };
	
	private static final Class[] paramClasses = { Float.class, Float.class,
			Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 0.5f, 0.5f, 0, 0, 1, 0 };
	private static final Range[] validParamValues = {
			new Range(Float.class, Float.MIN_VALUE, Float.MAX_VALUE),
			new Range(Float.class, Float.MIN_VALUE, Float.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 3), new Range(Integer.class, 0, 1) };

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpResizeDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpResizeDescriptor descriptor = new IqmOpResizeDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpResizeRIF rif = new IqmOpResizeRIF();
		RIFRegistry
				.register(opReg, descriptor.getName(),
						"IQM", rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpResizeDescriptor odesc = new IqmOpResizeDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpResize.class, OperatorGUI_Resize.class,
				IqmOpResizeValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
