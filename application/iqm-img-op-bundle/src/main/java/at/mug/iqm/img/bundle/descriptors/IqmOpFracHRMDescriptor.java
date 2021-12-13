package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracHRMDescriptor.java
 * 
 * $Id: IqmOpFracHRMDescriptor.java 548 2016-01-18 09:36:47Z kainzp $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/descriptors/IqmOpFracHRMDescriptor.java $
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_FracHRM;
import at.mug.iqm.img.bundle.op.IqmOpFracHRM;
import at.mug.iqm.img.bundle.validators.IqmOpFracHRMValidator;

/**
 * @author Ahammer
 * @since  2016 6
 */
@SuppressWarnings({ "rawtypes" })
public class IqmOpFracHRMDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2537858417257686778L;
	
	public static final int PLOTNICK      = 0;
	public static final int ALTERNATIVE1 = 1;
	public static final int ALTERNATIVE2 = 2;

	public static final OperatorType TYPE = OperatorType.IMAGE_GENERATOR;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpFracHRM" }, { "Vendor", "mug.qmnm" },
			{ "Description", "generates images using hirarchical random maps HRM" }, //according to Plotnick etal 1993 Lacunarity indices as measures of landscape texture
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Width" },
			{ "arg1Desc", "Height" },
			{ "arg2Desc", "ItMax" },
			{ "arg3Desc", "P1" },
			{ "arg4Desc", "P2" },
			{ "arg5Desc", "P3" },
			{ "arg6Desc", "P4" },
			{ "arg7Desc", "P5" },
			{ "arg8Desc", "P6" },
			{ "arg9Desc", "P7" },
			{ "arg10Desc", "P8" },
			{ "arg11Desc", "Method" },	
			 };
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 0;
	private static final String[] paramNames = { "Width", "Height", "ItMax",
			"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8",  
			"Method" };
	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class,
			Double.class, Double.class, Double.class, Double.class,
			Double.class, Double.class, Double.class, Double.class,
			Integer.class, };
	private static final Object[] paramDefaults = { 3, 3, 3, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0};
	private static final Range[] validParamValues = {
			new Range(Integer.class, 2, 10),
			new Range(Integer.class, 2, 10),
			new Range(Integer.class, 1, 8),
			new Range(Double.class, 0.0, 1.0),
			new Range(Double.class, 0.0, 1.0),
			new Range(Double.class, 0.0, 1.0),
			new Range(Double.class, 0.0, 1.0),
			new Range(Double.class, 0.0, 1.0),
			new Range(Double.class, 0.0, 1.0),
			new Range(Double.class, 0.0, 1.0),
			new Range(Double.class, 0.0, 1.0),
			new Range(Integer.class, 0, 2),
			};

	/**
	 * constructor
	 */
	public IqmOpFracHRMDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpFracHRMDescriptor descriptor = new IqmOpFracHRMDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpFracHRMRIF rif = new IqmOpFracHRMRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpFracHRMDescriptor odesc = new IqmOpFracHRMDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpFracHRM.class,
				OperatorGUI_FracHRM.class, IqmOpFracHRMValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
