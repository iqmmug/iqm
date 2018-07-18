package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracIFSDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_FracIFS;
import at.mug.iqm.img.bundle.op.IqmOpFracIFS;
import at.mug.iqm.img.bundle.validators.IqmOpFracIFSValidator;

/**
 * @author Ahammer
 * @since   2009 10
 */
@SuppressWarnings({ "rawtypes" })
public class IqmOpFracIFSDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -431208993152657604L;

	public static final OperatorType TYPE = OperatorType.IMAGE_GENERATOR;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpFracIFS" }, { "Vendor", "mug.qmnm" },
			{ "Description", "generates fractals using IFS" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "Width" },
			{ "arg1Desc", "Height" }, { "arg2Desc", "ItMax" },
			{ "arg3Desc", "NumPoly" }, { "arg4Desc", "FracType" }, };
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 0;
	private static final String[] paramNames = { "Width", "Height", "ItMax",
			"NumPoly", "FracType" };
	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 512, 512, 4, 3, 0 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 6) };

	/**
	 * constructor
	 */
	public IqmOpFracIFSDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpFracIFSDescriptor descriptor = new IqmOpFracIFSDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpFracIFSRIF rif = new IqmOpFracIFSRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpFracIFSDescriptor odesc = new IqmOpFracIFSDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpFracIFS.class,
				OperatorGUI_FracIFS.class, IqmOpFracIFSValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
