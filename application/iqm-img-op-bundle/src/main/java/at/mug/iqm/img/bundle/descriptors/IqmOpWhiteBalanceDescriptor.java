package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpWhiteBalanceDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_WhiteBalance;
import at.mug.iqm.img.bundle.op.IqmOpWhiteBalance;
import at.mug.iqm.img.bundle.validators.IqmOpWhiteBalanceValidator;

/**
 * @author Ahammer
 * @since   2009 12
 */
@SuppressWarnings("rawtypes")
public class IqmOpWhiteBalanceDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8540897840061603307L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpWhiteBalance" }, { "Vendor", "mug.qmnm" },
			{ "Description", "ROI defined White Balance" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "Method" },
			{ "arg1Desc", "InR" }, { "arg2Desc", "InG" },
			{ "arg3Desc", "InB" }, { "arg4Desc", "OutR" },
			{ "arg5Desc", "OutG" }, { "arg6Desc", "OutB" },
			{ "arg7Desc", "LinkIn" }, { "arg8Desc", "LinkOut" },

	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Method", "InR", "InG", "InB",
			"OutR", "OutG", "OutB", "LinkIn", "LinkOut" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 0, 100, 100, 100, 250, 250,
			250, 1, 1 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 1), // ROI or Manual
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1) };

	/**
	 * constructor
	 */
	public IqmOpWhiteBalanceDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpWhiteBalanceDescriptor descriptor = new IqmOpWhiteBalanceDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpWhiteBalanceRIF rif = new IqmOpWhiteBalanceRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpWhiteBalanceDescriptor odesc = new IqmOpWhiteBalanceDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpWhiteBalance.class,
				OperatorGUI_WhiteBalance.class,
				IqmOpWhiteBalanceValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}
} // END
