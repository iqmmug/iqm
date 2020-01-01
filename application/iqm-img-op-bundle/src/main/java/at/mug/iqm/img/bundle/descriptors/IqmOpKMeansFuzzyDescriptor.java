package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpKMeansFuzzyDescriptor.java
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_KMeansFuzzy;
import at.mug.iqm.img.bundle.op.IqmOpKMeansFuzzy;
import at.mug.iqm.img.bundle.validators.IqmOpKMeansFuzzyValidator;

/**
 * @author Ahammer
 * @since    2009 05
 */
@SuppressWarnings("rawtypes")
public class IqmOpKMeansFuzzyDescriptor extends AbstractOperatorDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4180488572411809016L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE };
	
	private static final String[][] resources = {
			{ "GlobalName", "IqmOpKMeansFuzzy" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "K-means clustering" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Method" },
			{ "arg1Desc", "nK" },
			{ "arg2Desc", "ItMax" },
			{ "arg3Desc", "Eps" },
			{ "arg4Desc", "Fuzzy" },
			{ "arg5Desc", "Rank" },
			{ "arg6Desc", "Out" }, };
	private static final String[] supportedModes = { "rendered" };
	private static final int      numSources = 1;
	private static final String[] paramNames = { "Method", "nK", "ItMax", "Eps", "Fuzzy", "Rank", "Out" };
	
	private static final Class[]  paramClasses = { Integer.class, Integer.class, Integer.class,
		                                          Double.class, Double.class, Integer.class, Integer.class };
	private static final Object[] paramDefaults = { 0, 2, 20, 0.1d, 1.5d, 1, 0 };
	private static final Range[]  validParamValues = {
													new Range(Integer.class, 0, 2),
													new Range(Integer.class, 2, 100),
													new Range(Integer.class, 20, 1000),
													new Range(Double.class, 0.000001d, 1000.0d),
													new Range(Double.class, 1.0d, 10.0d),
													new Range(Integer.class, 1, 10),
													new Range(Integer.class, 0, 1),
													};

	/**
	 * constructor
	 */
	public IqmOpKMeansFuzzyDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance().getOperationRegistry();
		IqmOpKMeansFuzzyDescriptor descriptor = new IqmOpKMeansFuzzyDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpKMeansFuzzyRIF rif = new IqmOpKMeansFuzzyRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}
	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpKMeansFuzzyDescriptor odesc = new IqmOpKMeansFuzzyDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpKMeansFuzzy.class, OperatorGUI_KMeansFuzzy.class,
				IqmOpKMeansFuzzyValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}

} // END
