package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpMorphDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Morph;
import at.mug.iqm.img.bundle.op.IqmOpMorph;
import at.mug.iqm.img.bundle.validators.IqmOpMorphValidator;

/**
 * @author Ahammer
 * @since 2009 04
 */
@SuppressWarnings("rawtypes")
public class IqmOpMorphDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1873492037068873865L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };
	
	private static final String[][] resources = {
			{ "GlobalName", "IqmOpMorph" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "morphological operations" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "KernelShape" }, // 0 Rect 1 circular 2 diamond
			{ "arg1Desc", "KernelWidth" },
			{ "arg2Desc", "KernelHeight" },
			{ "arg3Desc", "Iterations" },
			{ "arg4Desc", "minimal blob size in % of total image pixels" },
			{ "arg5Desc", "maximal blob size in % of total image pixels" },
			{ "arg6Desc", "morph options" }, // 0 dilate, 1 erode, 2 close, 3 open, 4 skeleton, 5 hole fill,6 erase blobs
	};
	private static final String[] supportedModes = { "rendered" };
	private static final int      numSources = 1;
	private static final String[] paramNames = { "KernelShape", "KernelWidth",
			                                     "KernelHeight", "Iterations", 
			                                     "MinBlobSize", "MaxBlobSize", "Morph" };
	
	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Double.class, Double.class,
			Integer.class };
	private static final Object[] paramDefaults = { 1, 3, 3, 1, 0.0, 100.0, 0 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 2), new Range(Integer.class, 3, 1001),
			new Range(Integer.class, 3, 1001),
			new Range(Integer.class, 1, 100),
			new Range(Double.class, 0.0, 100.0),
			new Range(Double.class, 0.0, 100.0), new Range(Integer.class, 0, 6) };

	/**
	 * constructor
	 */
	public IqmOpMorphDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance().getOperationRegistry();
		IqmOpMorphDescriptor descriptor = new IqmOpMorphDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpMorphRIF rif = new IqmOpMorphRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpMorphDescriptor odesc = new IqmOpMorphDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(), odesc.getClass(), IqmOpMorph.class, OperatorGUI_Morph.class,
				IqmOpMorphValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
