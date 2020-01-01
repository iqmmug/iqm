package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpFFTDescriptor.java
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


import javax.media.jai.util.Range;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.AbstractOperatorDescriptor;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.plot.bundle.gui.PlotGUI_FFT;
import at.mug.iqm.plot.bundle.op.PlotOpFFT;
import at.mug.iqm.plot.bundle.validators.PlotOpFFTValidator;


/**
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class PlotOpFFTDescriptor extends AbstractOperatorDescriptor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 290286622216108636L;
	
	public static final int WINDOWING_WITHOUT = 0;
	public static final int WINDOWING_RECTANGULAR = 1;
	public static final int WINDOWING_BARTLETT = 2;
	public static final int WINDOWING_WELCH = 3;
	public static final int WINDOWING_HANN = 4;
	public static final int WINDOWING_HAMMING = 5;
	public static final int WINDOWING_KAISER = 6;
	public static final int WINDOWING_GAUSSIAN = 7;
	
	public static final int RESULT_LOG = 0;
	public static final int RESULT_LIN = 1;

	public static final OperatorType TYPE = OperatorType.PLOT;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.PLOT };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpFFT" }, { "Vendor", "mug.qmnm" },
			{ "Description", "Fourier Transform Function for plots" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" }, { "arg0Desc", "Windowing" }, { "arg1Desc", "sampleRate" }, { "arg2Desc", "resultLogLin" }};

	private static final int numSources = 1;
	private static final String[] paramNames = { "windowing", "sampleRate", "resultLogLin" };

	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 2, 1000, 0};
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 7),
			new Range(Integer.class, 0, Integer.MAX_VALUE),
			new Range(Integer.class, 0, 1) };

	/**
	 * constructor
	 */
	public PlotOpFFTDescriptor() {
		super(resources, numSources, paramNames, paramClasses, paramDefaults,
				validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpFFTDescriptor odesc = new PlotOpFFTDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpFFT.class,
				PlotGUI_FFT.class, PlotOpFFTValidator.class,
				odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
