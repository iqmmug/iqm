package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracScanDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_FracScan;
import at.mug.iqm.img.bundle.op.IqmOpFracScan;
import at.mug.iqm.img.bundle.validators.IqmOpFracScanValidator;

/**
 * @author Ahammer
 * @since 2012 06
 */
@SuppressWarnings("rawtypes")
public class IqmOpFracScanDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4218149598907423771L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };
	
	private static final String[][] resources = {
			{ "GlobalName", "IqmOpFracScan" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Distance Transformation" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "scan type" }, // 0 Box scan 1 Nexel scan
			{ "arg1Desc", "box size" }, // only for Box scan
			{ "arg2Desc", "kernel shape" }, { "arg3Desc", "kernel size" },
			{ "arg4Desc", "epsilon" }, { "arg5Desc", "method" }, // 0 BoxCount,
																	// 1
																	// Pyramid,
																	// 2
																	// Minkowski,
																	// 3 FFT
			{ "arg6Desc", "ResultOptions" }, // Clamp, Normalize, Actual,
												// Normalize0to3
	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "ScanType", "BoxSize",
			"KernelShape", "KernelSize", "Eps", "Method", "ResultOptions" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class };
	private static final Object[] paramDefaults = { 0, 16, 0, 3, 10, 2, 1 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 1), new Range(Integer.class, 1, 1000),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 3, 101),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 3), new Range(Integer.class, 0, 3), };

	/**
	 * constructor
	 */
	public IqmOpFracScanDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpFracScanDescriptor descriptor = new IqmOpFracScanDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpFracScanRIF rif = new IqmOpFracScanRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM",
				rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpFracScanDescriptor odesc = new IqmOpFracScanDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpFracScan.class, OperatorGUI_FracScan.class,
				IqmOpFracScanValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
