package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpRGBRelativeDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_RGBRelative;
import at.mug.iqm.img.bundle.op.IqmOpRGBRelative;
import at.mug.iqm.img.bundle.validators.IqmOpRGBRelativeValidator;

/**
 * @author Ahammer
 * @since   2009 04
 */
@SuppressWarnings("rawtypes")
public class IqmOpRGBRelativeDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5806890689112590227L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };
	
	private static final String[][] resources = {
			{ "GlobalName", "IqmOpRGBRelative" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "RGB relative segmentation of RGB color images" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "Method" },
			{ "arg1Desc", "RankLeftRG" }, { "arg2Desc", "RankLeftRB" },
			{ "arg3Desc", "RankLeftGR" }, { "arg4Desc", "RankLeftGB" },
			{ "arg5Desc", "RankLeftBR" }, { "arg6Desc", "RankLeftBG" },

			{ "arg7Desc", "AND" },

			{ "arg8Desc", "RankRightRG" }, { "arg9Desc", "RankRightRB" },
			{ "arg10Desc", "RankRightGR" }, { "arg11Desc", "RankRightGB" },
			{ "arg12Desc", "RankRightBR" }, { "arg13Desc", "RankRightBG" },

			{ "arg14Desc", "Percentage" }, { "arg15Desc", "Binarize" }, };
	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "Method", "RankLeftRG",
			"RankLeftRB", "RankLeftGR", "RankLeftGB", "RankLeftBR",
			"RankLeftBG", "AND", "RankRightRG", "RankRightRB", "RankRightGR",
			"RankRightGB", "RankRightBR", "RankRightBG", "Ratio", "Binarize" };
	
	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class,
			Integer.class, Integer.class, };
	private static final Object[] paramDefaults = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 50, 0 };
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 3), // R,G,B,Rank
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 100),
			new Range(Integer.class, 0, 1) };

	/**
	 * constructor
	 */
	public IqmOpRGBRelativeDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpRGBRelativeDescriptor descriptor = new IqmOpRGBRelativeDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpRGBRelativeRIF rif = new IqmOpRGBRelativeRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpRGBRelativeDescriptor odesc = new IqmOpRGBRelativeDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpRGBRelative.class, OperatorGUI_RGBRelative.class,
				IqmOpRGBRelativeValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
