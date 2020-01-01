package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpConvertDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_Convert;
import at.mug.iqm.img.bundle.op.IqmOpConvert;
import at.mug.iqm.img.bundle.validators.IqmOpConvertValidator;


/**
 * <p>
 * <b>Changes</b>
 * <ul>
 * 	<li>2010 01 RGBtoHSV, RGBtoHLS,HSVtoRGB, HLStoRGB 
 * </ul>
 * @author Ahammer
 * @since   2009 03
 * @update 2014 05 HA added 8bit to false color options
 */
public class IqmOpConvertDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4727989355695797167L;

	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpConvert" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Converts images e.g. RGB to 8bit, 16bit to 8bit,....." },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "8bit convert option" },
			{ "arg1Desc", "16bit convert option" },
			{ "arg2Desc", "Palette convert option" },
			{ "arg3Desc", "RGB convert option" },
			{ "arg4Desc", "RGBa convert option" } };

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Options8bit", "Options16bit", "OptionsPalette", "OptionsRGB", "OptionsRGBa" };
	@SuppressWarnings("rawtypes")
	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 0, 0, 0, 0, 0 };
	private static final Range[] validParamValues = { new Range(Integer.class, 0, 5), new Range(Integer.class, 0, 1),
													  new Range(Integer.class, 0, 2), new Range(Integer.class, 0, 14),
			                                          new Range(Integer.class, 0, 0) };

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpConvertDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance().getOperationRegistry();
		IqmOpConvertDescriptor descriptor = new IqmOpConvertDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpConvertRIF rif = new IqmOpConvertRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpConvertDescriptor odesc = new IqmOpConvertDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),odesc.getClass(), IqmOpConvert.class, OperatorGUI_Convert.class,
				IqmOpConvertValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
