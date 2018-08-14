package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpInvertDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Invert;
import at.mug.iqm.img.bundle.op.IqmOpInvert;
import at.mug.iqm.img.bundle.validators.IqmOpInvertValidator;

/**
 * @author Ahammer
 * @since 2009 03
 */
@SuppressWarnings({ "rawtypes" })
public class IqmOpInvertDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8870702098588363531L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {

	{ "GlobalName", "IqmOpInvert" }, { "Vendor", "mug.qmnm" },
			{ "Description", "A simple invert operator" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "Invert Option" } };

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "invertOption" };
	private static final Class[] paramClasses = { Integer.class };
	private static final Object[] paramDefaults = { 0 }; // 0 for invert image,
															// 1 for invert LUT
	private static final Range[] validParamValues = { new Range(Integer.class,
			0, 1) };

	/**
	 * constructor
	 */
	public IqmOpInvertDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		// construct a new descriptor
		IqmOpInvertDescriptor descriptor = new IqmOpInvertDescriptor();

		// register the descriptor with the registry
		opReg.registerDescriptor(descriptor);

		IqmOpInvertRIF rif = new IqmOpInvertRIF();
		// tie the name of the operator to a specific operation
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpInvertDescriptor odesc = new IqmOpInvertDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpInvert.class, OperatorGUI_Invert.class,
				IqmOpInvertValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
