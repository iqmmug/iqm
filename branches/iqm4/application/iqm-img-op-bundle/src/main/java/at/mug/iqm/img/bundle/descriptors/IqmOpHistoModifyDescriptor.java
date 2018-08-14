package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpHistoModifyDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_HistoModify;
import at.mug.iqm.img.bundle.op.IqmOpHistoModify;
import at.mug.iqm.img.bundle.validators.IqmOpHistoModifyValidator;

/**
 * @author Ahammer
 * @since 2009 05
 */
@SuppressWarnings("rawtypes")
public class IqmOpHistoModifyDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6640418568134385891L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpHistoModify" }, { "Vendor", "mug.qmnm" },
			{ "Description", "histogram manipulation" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "Method" },
			{ "arg1Desc", "OffSetLow" }, { "arg2Desc", "OffSetHigh" },

	};
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Method", "OffSetLow",
			"OffSetHigh", "BandCombine" };

	private static final Class[] paramClasses = { Integer.class, Double.class,
			Double.class, Integer.class };
	private static final Object[] paramDefaults = { 0, 0.d, 0.d, 0 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 3), new Range(Double.class, 0.d, 100.d),
			new Range(Double.class, 0.d, 100.d), new Range(Integer.class, 0, 1) };

	/**
	 * constructor
	 */
	public IqmOpHistoModifyDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpHistoModifyDescriptor descriptor = new IqmOpHistoModifyDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpHistoModifyRIF rif = new IqmOpHistoModifyRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpHistoModifyDescriptor odesc = new IqmOpHistoModifyDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpHistoModify.class,
				OperatorGUI_HistoModify.class, IqmOpHistoModifyValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
