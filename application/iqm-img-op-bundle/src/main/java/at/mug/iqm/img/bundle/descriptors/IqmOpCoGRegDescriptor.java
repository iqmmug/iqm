package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCoGRegDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_CoGReg;
import at.mug.iqm.img.bundle.op.IqmOpCoGReg;
import at.mug.iqm.img.bundle.validators.IqmOpCoGRegValidator;

/**
 * @author Ahammer
 * @since   2009 07
 */
@SuppressWarnings("rawtypes") 
public class IqmOpCoGRegDescriptor extends AbstractOperatorDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5327709654720820860L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	public static final StackProcessingType STACK_PROCESSING_TYPE = StackProcessingType.SINGLE_STACK_SEQUENTIAL;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };
	
	private static final String[][] resources = {
			{ "GlobalName", "IqmOpCoGReg"},
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Center of Gravity registration" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "First" }, };

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "First" };
	
	private static final Class[] paramClasses = { Integer.class };
	private static final Object[] paramDefaults = { 0 }; //
	private static final Range[] validParamValues = { new Range(Integer.class,
			0, 1) };

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpCoGRegDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES, STACK_PROCESSING_TYPE);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpCoGRegDescriptor descriptor = new IqmOpCoGRegDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpCoGRegRIF rif = new IqmOpCoGRegRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpCoGRegDescriptor odesc = new IqmOpCoGRegDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpCoGReg.class, OperatorGUI_CoGReg.class,
				IqmOpCoGRegValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}
} // END
