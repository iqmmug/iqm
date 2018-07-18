package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCreateImageDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_CreateImage;
import at.mug.iqm.img.bundle.op.IqmOpCreateImage;
import at.mug.iqm.img.bundle.validators.IqmOpCreateImageValidator;

/**
 * @author Ahammer
 * @since    2010 02
 * @update 2014 11 added RGB option
 */
@SuppressWarnings({ "rawtypes" })
public class IqmOpCreateImageDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8818619411808712407L;
	
	public static final int CREATERANDOM   = 0;
	public static final int CREATEGAUSSIAN = 1;
	public static final int CREATECONSTANT = 2;
	public static final int CREATESINUS    = 3;
	public static final int CREATECOSINUS  = 4;

	public static final OperatorType TYPE = OperatorType.IMAGE_GENERATOR;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpCreateImage" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Creates an image" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Width" },
			{ "arg1Desc", "Height" },
			{ "arg2Desc", "Method" }, // Random, Gauss, Constant, sin, cos
			{ "arg3Desc", "Const1" },
			{ "arg3Desc", "Const2" },
			{ "arg3Desc", "Const3" },
			{ "arg4Desc", "Omega" },
			{ "arg5Desc", "OutBit" }, // 8bit 16bit or RGB output image
	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Width", "Height", "Method", "Const1", "Const2", "Const3", "Omega", "OutBit" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 512, 512, 0, 127, 200, 200, 0, 0 };
	private static final Range[] validParamValues = { new Range(Integer.class, 0, Integer.MAX_VALUE),
	                                           		  new Range(Integer.class, 0, Integer.MAX_VALUE),
			                                          new Range(Integer.class, 0, 4),
			                                          new Range(Integer.class, 0, 255),
			                                          new Range(Integer.class, 0, 255),
			                                          new Range(Integer.class, 0, 255),
		                                           	  new Range(Integer.class, 0, Integer.MAX_VALUE),
			                                          new Range(Integer.class, 0, 2), };



	/**
	 * constructor
	 */
	public IqmOpCreateImageDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance().getOperationRegistry();
		IqmOpCreateImageDescriptor descriptor = new IqmOpCreateImageDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpCreateImageRIF rif = new IqmOpCreateImageRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpCreateImageDescriptor odesc = new IqmOpCreateImageDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpCreateImage.class,
				OperatorGUI_CreateImage.class, IqmOpCreateImageValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
