package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpWatershedDescriptor.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 *  *
 * %%
 * Copyright (C) 2009 - 2021 Helmut Ahammer, Philipp Kainz
 * %%
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Watershed;
import at.mug.iqm.img.bundle.op.IqmOpWatershed;
import at.mug.iqm.img.bundle.validators.IqmOpWatershedValidator;

/**
 * @author Ahammer
 * @since   2010 05
 */
@SuppressWarnings("rawtypes")
public class IqmOpWatershedDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1275456871597387045L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpWatershed" }, { "Vendor", "mug.qmnm" },
			{ "Description", "Watershed segmentation of an image" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "Invert" }, // Invert 0 no
															// invert, 1 invert
			{ "arg1Desc", "PreProc" }, // PreProcessing: 0 none, 1 gaussian blur
			{ "arg2Desc", "Kernel" }, // kernelsize
			{ "arg3Desc", "Connectivety" }, // 0 4-connectivety, 1
											// 8-connectivety
			{ "arg4Desc", "ThresMin" }, // threshold min [0,255]
			{ "arg5Desc", "ThresMax" }, // threshold max [0,255]
			{ "arg6Desc", "Output" }, // output option: 0 dams, 1 overlaid dams,
										// 2 colorized basins, 3 composite
	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Invert", "PreProc", "Kernel",
			"Connectivity", "ThresMin", "ThresMax", "Output" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class };
	private static final Object[] paramDefaults = { 0, 0, 3, 0, 0, 255, 1 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 255),
			new Range(Integer.class, 0, 255), new Range(Integer.class, 0, 3) };

	/**
	 * constructor
	 */
	public IqmOpWatershedDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpWatershedDescriptor descriptor = new IqmOpWatershedDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpWatershedRIF rif = new IqmOpWatershedRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpWatershedDescriptor odesc = new IqmOpWatershedDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpWatershed.class,
				OperatorGUI_Watershed.class, IqmOpWatershedValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
