package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpBorderDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Border;
import at.mug.iqm.img.bundle.op.IqmOpBorder;
import at.mug.iqm.img.bundle.validators.IqmOpBorderValidator;


/**
 * @author Ahammer
 * @since 2009 06
 *
 */
@SuppressWarnings("rawtypes")
public class IqmOpBorderDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 888955379871843842L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };
	
	public static final Integer ZERO     = 0;
	public static final Integer CONSTANT = 1;
	public static final Integer COPY     = 2;
	public static final Integer REFLECT  = 3;
	public static final Integer WRAP     = 4;
	
	public static final Integer PREFERENCE_BORDER = 0;
	public static final Integer PREFERENCE_SIZE   = 1;
	

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpBorder" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Adds a border to the image" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Method" },
			{ "arg1Desc", "Left padding" },
			{ "arg2Desc", "Right padding" },
			{ "arg3Desc", "Top padding" },
			{ "arg4Desc", "Bottom padding" },
			{ "arg5Desc", "NewWidth" },
			{ "arg6Desc", "NewHeight" },
			{ "arg7Desc", "Constant" },     // for filling with a constant or color
			{ "arg8Desc", "BorderOrSize" }, // preference for image stacks with different image sizes
	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Method", "Left", "Right", "Top", "Bottom", "NewWidth", "NewHeight", "Const", "BorderOrSize" };

	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { IqmOpBorderDescriptor.ZERO, 0, 0, 0, 0, 0, 0, 0, IqmOpBorderDescriptor.PREFERENCE_BORDER };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 4),
			new Range(Integer.class, -Integer.MAX_VALUE, Integer.MAX_VALUE),
			new Range(Integer.class, -Integer.MAX_VALUE, Integer.MAX_VALUE),
			new Range(Integer.class, -Integer.MAX_VALUE, Integer.MAX_VALUE),
			new Range(Integer.class, -Integer.MAX_VALUE, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 255), new Range(Integer.class, 0, 1)
	};

	/**
	 * constructor
	 */
	public IqmOpBorderDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance().getOperationRegistry();
		IqmOpBorderDescriptor descriptor = new IqmOpBorderDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpBorderRIF rif = new IqmOpBorderRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpBorderDescriptor odesc = new IqmOpBorderDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(), odesc.getClass(), IqmOpBorder.class, OperatorGUI_Border.class, IqmOpBorderValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
