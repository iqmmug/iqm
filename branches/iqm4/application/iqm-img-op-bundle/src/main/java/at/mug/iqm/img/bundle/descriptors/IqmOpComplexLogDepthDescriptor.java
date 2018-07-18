package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpComplexLogDepthDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_ComplexLogDepth;
import at.mug.iqm.img.bundle.op.IqmOpComplexLogDepth;
import at.mug.iqm.img.bundle.validators.IqmOpComplexLogDepthValidator;

/**
 * Logical depth (Physical depth) is calculated according to 
 * <br>
 * Zenil et al Image Characterization and Classification by Physical Complexity, Complexity 2011 Vol17 No3 26-42    
 * 
 * @author Ahammer
 * @since 2014 01
 */
@SuppressWarnings({ "rawtypes" })
public class IqmOpComplexLogDepthDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5737187117502717505L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	public static final Integer COMPRESSION_PNG             = 0;
	public static final Integer COMPRESSION_PNGCRUSH        = 1;
	public static final Integer COMPRESSION_PNGADVANCEDCOMP = 2;
	public static final Integer COMPRESSION_ZIP             = 3;
	public static final Integer COMPRESSION_LZW             = 4;
	public static final Integer COMPRESSION_JPEG2000        = 5;
	
	public static final int LOADTIMECORRECTION_NO  = 0;
	public static final int LOADTIMECORRECTION_YES = 1;
		
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.TABLE};

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpComplexLogDepth" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "calculates the logical depth (physical depth)" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Method" },           //Compression method PNG,.......
			{ "arg1Desc", "Iterations" },       //Number of iterations
			{ "arg2Desc", "CorrSystemBias" },   //CheckBox Correction of loading time
	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Method", "Iterations", "CorrSystemBias"};
	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { COMPRESSION_PNG, 10, 1 };
	private static final Range[] validParamValues = { new Range(Integer.class, 0, 5), 
													  new Range(Integer.class, 0, 1), 
													  new Range(Integer.class, 1, Integer.MAX_VALUE) };

	

	/**
	 * constructor
	 */
	public IqmOpComplexLogDepthDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpComplexLogDepthDescriptor descriptor = new IqmOpComplexLogDepthDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpComplexLogDepthRIF rif = new IqmOpComplexLogDepthRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpComplexLogDepthDescriptor odesc = new IqmOpComplexLogDepthDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpComplexLogDepth.class,
				OperatorGUI_ComplexLogDepth.class,
				IqmOpComplexLogDepthValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
