package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracFFTDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_FracFFT;
import at.mug.iqm.img.bundle.op.IqmOpFracFFT;
import at.mug.iqm.img.bundle.validators.IqmOpFracFFTValidator;

/**
 * @author Ahammer
 * @since 2011 02
 */
@SuppressWarnings("rawtypes")
public class IqmOpFracFFTDescriptor extends AbstractOperatorDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6069193517411247600L;

	public static final OperatorType TYPE = OperatorType.IMAGE;

	private static final DataType[] OUTPUT_TYPES = new DataType[] {
			DataType.IMAGE, DataType.PLOT, DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpFracFFT" }, { "Vendor", "mug.qmnm" },
			{ "Description", "calculates the fractal dimension using FFT" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Number of maximal k" },
			{ "arg1Desc", "Topological Dimension" }, // 0 Dt=1, 1 Dt=2
			{ "arg2Desc", "Regression Start" },
			{ "arg3Desc", "Regression End" }, { "arg4Desc", "Show Plot" }, // 0,1
			{ "arg5Desc", "Delete Existing Plot" }, // 0,1
	};

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = { "NumMaxK", "TopDim",
			"RegStart", "RegEnd", "ShowPlot", "DeleteExistingPlot" };

	private static final Class[] paramClasses = { Integer.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class, };
	private static final Object[] paramDefaults = { 2, 1, 1, 2, 1, 1 }; //
	private static final Range[] validParamValues = {
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 1, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1), new Range(Integer.class, 0, 1), };

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpFracFFTDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpFracFFTDescriptor descriptor = new IqmOpFracFFTDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpFracFFTRIF rif = new IqmOpFracFFTRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpFracFFTDescriptor odesc = new IqmOpFracFFTDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpFracFFT.class,
				OperatorGUI_FracFFT.class, IqmOpFracFFTValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
