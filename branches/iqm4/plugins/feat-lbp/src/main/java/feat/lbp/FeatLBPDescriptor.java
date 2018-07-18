package feat.lbp;

/*
* This file is part of IQM, hereinafter referred to as "this program".
* 
* Copyright (C) 2009 - 2014 Helmut Ahammer, Philipp Kainz
* 
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
*/

import javax.media.jai.util.Range;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.AbstractOperatorDescriptor;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.operator.OperatorType;

/**
 * This is the descriptor for the operator. Each operator is associated with
 * exactly one descriptor. The descriptor contains all definitions of sources
 * and parameters required for processing.
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings("rawtypes")
public class FeatLBPDescriptor extends AbstractOperatorDescriptor {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -5305973731415830719L;

	/**
	 * Set the type of the associated operator to be one of the types declared
	 * in {@link OperatorType}.
	 */
	public static final OperatorType TYPE = OperatorType.IMAGE;

	/**
	 * Set the output types of the associated operator to be one or more of the
	 * types declared in {@link DataType}.
	 */
	public static final DataType[] OUTPUT_TYPES = new DataType[] {
			DataType.IMAGE, DataType.PLOT, DataType.TABLE };

	// Optionally, you can declare a particular stack processing type, if the
	// operator differs from default processing
	// If you specify the stack processing type, you have to include it in the
	// register() method as last argument.
	// public static final StackProcessingType STACK_PROCESSING_TYPE =
	// StackProcessingType.MULTI_STACK_EVEN;

	/**
	 * Use these resources for describing the parts of your parameters.
	 */
	private final static String[][] resources = {

			{ "GlobalName", "feat-lbp" },
			{ "Vendor", "IQM" },
			{
					"Description",
					"Local Binary Patterns (LBP) is a statistical texture descriptor "
							+ "for grey-value images. "
							+ "Rotation invariant LBPs are computed as response-map (image), "
							+ "histogram, and code-table. " },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "The neigbourhood of the center pixel." },
			{ "arg1Desc", "The radius around the center pixel." },
			{
					"arg2Desc",
					"A flag whether or not to smooth the image with "
							+ "a Gaussian prior to calculation." },
			{ "arg3Desc", "The kernel size of the isotropic Gaussian." },
			{ "arg4Desc", "The size of each cell." }, };
	/**
	 * Use the supported modes for JAI registration.
	 */
	private final static String[] supportedModes = { "rendered" };
	/**
	 * Declare the number of sources.
	 */
	private final static int numSources = 1;
	/**
	 * Declare the name of the parameters. The order of the elements matters!
	 */
	private final static String[] paramNames = { "neighbours", "radius",
			"smooth", "kernelsize", "cells" };
	/**
	 * Declare the classes of the parameters. The order is associated to indexes
	 * given in {@link #paramNames}.
	 */
	private final static Class[] paramClasses = { Integer.class, Float.class,
			Boolean.class, Integer.class, Integer.class };
	/**
	 * Set some default values for the parameters. The default parameter block
	 * will be constructed using these values.
	 */
	private final static Object[] paramDefaults = { 8, 1.0F, false, 3, 1 };
	/**
	 * Define a set of valid parameter values.
	 */
	private static final Range[] validParamValues = {
			new Range(Integer.class, 1, 32),
			new Range(Float.class, 1.0F, Float.MAX_VALUE),
			new Range(Boolean.class, true, false),
			new Range(Integer.class, 3, 101),
			new Range(Integer.class, 1, Integer.MAX_VALUE), };

	/**
	 * Constructor for this descriptor.
	 */
	public FeatLBPDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		FeatLBPDescriptor odesc = new FeatLBPDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), FeatLBP.class, FeatLBPGUI.class,
				FeatLBPValidator.class, odesc.getType(),
				odesc.getStackProcessingType());

		return odesc;
	}

}
