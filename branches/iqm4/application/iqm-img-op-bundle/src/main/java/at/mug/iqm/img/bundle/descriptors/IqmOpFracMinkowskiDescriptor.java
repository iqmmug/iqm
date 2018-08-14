package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracMinkowskiDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_FracMinkowski;
import at.mug.iqm.img.bundle.op.IqmOpFracMinkowski;
import at.mug.iqm.img.bundle.validators.IqmOpFracMinkowskiValidator;

/**
 * <li> 2012 01 04 added horizontal and vertical kernel shape
 * 
 * @author Ahammer
 * @since   2009 07
 * 
 */
@SuppressWarnings("rawtypes")
public class IqmOpFracMinkowskiDescriptor extends AbstractOperatorDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2760699859094696103L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE, DataType.PLOT, DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpFracMinkowski" },
			{ "Vendor", "mug.qmnm" },
			{ "Description",
					"calculates the fractal Minkowski dimension using dilation" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Dilation Epsilon" }, // Binary Image: radius; Grey
												// scale iamge: Blanket epsilon
			{ "arg1Desc", "Kernel Shape" }, // 0 Square, 1 Horizontal line, 2
											// Vertical line
			{ "arg2Desc", "Regression Start" },
			{ "arg3Desc", "Regression End" }, { "arg4Desc", "Show Plot" }, // 0,1
			{ "arg5Desc", "Delete Existing Plot" }, // 0,1
	};

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "DilEps", "KernelShape",
			"RegStart", "RegEnd", "ShowPlot", "DeleteExistingPlot" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class, };
	private static final Object[] paramDefaults = { 20, 0, 1, 20, 1, 1 }; //
	private static final Range[] validParamValues = {
			new Range(Integer.class, 3, 100), new Range(Integer.class, 0, 2),
			new Range(Integer.class, 1, 99), new Range(Integer.class, 2, 100),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1), };

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpFracMinkowskiDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpFracMinkowskiDescriptor descriptor = new IqmOpFracMinkowskiDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpFracMinkowskiRIF rif = new IqmOpFracMinkowskiRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpFracMinkowskiDescriptor odesc = new IqmOpFracMinkowskiDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpFracMinkowski.class,
				OperatorGUI_FracMinkowski.class,
				IqmOpFracMinkowskiValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
