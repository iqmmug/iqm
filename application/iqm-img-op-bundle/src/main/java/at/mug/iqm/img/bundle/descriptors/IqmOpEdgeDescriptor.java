package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpEdgeDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Edge;
import at.mug.iqm.img.bundle.op.IqmOpEdge;
import at.mug.iqm.img.bundle.validators.IqmOpEdgeValidator;

/**
 * <li>2010 09 added option: direction
 * <li>2011 11 07 option Laplace added
 * <li>2014 01 28 option DoG added
 * 
 * @author Ahammer, Kainz
 * @since   2009 06
 */
@SuppressWarnings("rawtypes")
public class IqmOpEdgeDescriptor extends AbstractOperatorDescriptor {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7010541863574792280L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpEdge" }, { "Vendor", "mug.qmnm" },
			{ "Description", "Edge Detecion Algorithms" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "method" }, // 0 Roberts, 1 diff,
															// 2 pix diff,
															// 3 Sobel, 4 Prewitt,
															// 5 FreiChen,
															// 6 Laplace
															// 7 DoG
			{ "arg1Desc", "size of the standard kernel" }, 
			{ "arg2Desc", "direction" }, 	// 0 standard,
											// 1 horizontal,
											// 2 vertical,
											// 3 verticalAndHorizontal
			{ "arg3Desc", "size of the second kernel for the DoG" }, // the size of the second DoG kernel
			{ "arg4Desc", "The result option: 0 = clamp, 1 = normalize, 2 = actual float value" }, 
	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Method", "KernelSize1",
			"Direction", "KernelSize2", "ResultOption"};

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 0, 5, 0, 3, 0};
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 7), new Range(Integer.class, 3, 101),
			new Range(Integer.class, 0, 3), new Range(Integer.class, 3, 101),
			new Range(Integer.class, 0, 1)};

	/**
	 * constructor
	 */
	public IqmOpEdgeDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpEdgeDescriptor descriptor = new IqmOpEdgeDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpEdgeRIF rif = new IqmOpEdgeRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpEdgeDescriptor odesc = new IqmOpEdgeDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpEdge.class, OperatorGUI_Edge.class,
				IqmOpEdgeValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
