package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCreateFracSurfDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_CreateFracSurf;
import at.mug.iqm.img.bundle.op.IqmOpCreateFracSurf;
import at.mug.iqm.img.bundle.validators.IqmOpCreateFracSurfValidator;

/**
 * <li>2011 01 added 16bit option
 * <li>2011 11 added method option: Sum of sin
 * 
 * @author Ahammer
 * @since    2010 01
 */
@SuppressWarnings({ "rawtypes" })
public class IqmOpCreateFracSurfDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2506028745519074892L;
	
	public static final int METHOD_FFT = 0; 
	public static final int METHOD_MIDPOINTDISPLACEMENT = 1; 
	public static final int METHOD_SUMOFSINE = 2; 

	public static final OperatorType TYPE = OperatorType.IMAGE_GENERATOR;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpCreateFracSurf" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Creates a fractal grey value surface" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Width" },
			{ "arg1Desc", "Height" },
			{ "arg2Desc", "Method" }, // 0 FFT, 1 Sum of sin,
			{ "arg3Desc", "FracD" },
			{ "arg4Desc", "OutBit" }, // 8bit or 16bit or RGB
	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Width", "Height", "Method", "FracD", "OutBit" };
	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Float.class, Integer.class };
	private static final Object[] paramDefaults = { 512, 512, 0, 2.5f, 0 };
	private static final Range[] validParamValues = {
													 new Range(Integer.class, 0, Integer.MAX_VALUE),
													 new Range(Integer.class, 0, Integer.MAX_VALUE),
													 new Range(Integer.class, 0, 2),
													 new Range(Float.class, 2.f, 3.f),
													 new Range(Integer.class, 0, 2) };

	/**
	 * constructor
	 */
	public IqmOpCreateFracSurfDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance().getOperationRegistry();
		IqmOpCreateFracSurfDescriptor descriptor = new IqmOpCreateFracSurfDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpCreateFracSurfRIF rif = new IqmOpCreateFracSurfRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpCreateFracSurfDescriptor odesc = new IqmOpCreateFracSurfDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpCreateFracSurf.class,
				OperatorGUI_CreateFracSurf.class,
				IqmOpCreateFracSurfValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
