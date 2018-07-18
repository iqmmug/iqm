package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpSmoothDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Smooth;
import at.mug.iqm.img.bundle.op.IqmOpSmooth;
import at.mug.iqm.img.bundle.validators.IqmOpSmoothValidator;

/**
 * @author Ahammer
 * @since 2009 06
 */
public class IqmOpSmoothDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2557848624508420513L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpSmooth" }, { "Vendor", "mug.qmnm" },
			{ "Description", "smoothing filter" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "method" }, // Mean Gauss
			{ "arg1Desc", "kernel size" }, };
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Method", "KernelSize" };
	@SuppressWarnings("rawtypes")
	private static final Class[] paramClasses = { Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 0, 3 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 1), // Mean Gauss
			new Range(Integer.class, 3, 101) };

	/**
	 * constructor
	 */
	public IqmOpSmoothDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpSmoothDescriptor descriptor = new IqmOpSmoothDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpSmoothRIF rif = new IqmOpSmoothRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}
	
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpSmoothDescriptor odesc = new IqmOpSmoothDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpSmooth.class, OperatorGUI_Smooth.class,
				IqmOpSmoothValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
