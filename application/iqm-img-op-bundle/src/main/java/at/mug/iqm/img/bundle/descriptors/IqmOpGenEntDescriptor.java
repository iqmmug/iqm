package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpGenEntDescriptor.java
 * 
 * $Id: IqmOpGenEntDescriptor.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/descriptors/IqmOpGenEntDescriptor.java $
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
import at.mug.iqm.img.bundle.gui.OperatorGUI_GenEnt;
import at.mug.iqm.img.bundle.op.IqmOpGenEnt;
import at.mug.iqm.img.bundle.validators.IqmOpGenEntValidator;

/**
 * <li>Generalized Entropies
 * <li>according to a review of Amigó, J.M., Balogh, S.G., Hernández, S., 2018. A Brief Review of Generalized Entropies. Entropy 20, 813. https://doi.org/10.3390/e20110813
 * <li>and to: Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * 
 * @author Ahammer
 * @since  2018-12-04
 */

@SuppressWarnings("rawtypes")
public class IqmOpGenEntDescriptor extends AbstractOperatorDescriptor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -965433752465270136L;
	
	public static final OperatorType TYPE = OperatorType.IMAGE;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.IMAGE, DataType.TABLE };

	private static final String[][] resources = {
			{ "GlobalName", "IqmOpGenEnt" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "computes eneralized dimensions" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "SEta" },
			{ "arg1Desc", "H" }, //H1, H2, H3
			{ "arg2Desc", "Renyi" },
			{ "arg3Desc", "Tsallis" },
			{ "arg4Desc", "SNorm" },
			{ "arg5Desc", "SEscort" },		
			{ "arg6Desc", "SKappa" },
			{ "arg7Desc", "SB" },
			{ "arg8Desc", "SE" },
			{ "arg9Desc", "SBeta" },
			{ "arg10Desc", "SGamma" },		
			{ "arg11Desc", "Epsilon" }, //Distance
			{ "arg12Desc",  "minimal q" },
			{ "arg13Desc", "maximal q" },
			{ "arg14Desc", "minimal Eta" },
			{ "arg15Desc", "maximal Eta" },
			{ "arg16Desc", "minimal Kappa" },
			{ "arg17Desc", "maximal Kappa" },
			{ "arg18Desc", "minimal B" },
			{ "arg19Desc", "maximal B" },
			{ "arg20Desc", "minmal Beta" },
			{ "arg21Desc", "maximal Beta" },	
			{ "arg22Desc", "minimal Gamma" },
			{ "arg23Desc", "maximal Gamma" },	
			{ "arg24Desc", "Grid Method" }, // 0 Gliding Box, 1 Grid Box
	};

	private static final String[] supportedModes = { "rendered" };
	private static final int numSources = 1;
	private static final String[] paramNames = {"SE", "H", "Renyi", "Tsallis", "SNorm", "SEscort", "SEta", "SKappa", "SB",  "SBeta", "SGamma", 
			 									"Eps", "MinQ", "MaxQ",
												"MinEta", "MaxEta", "MinKappa", "MaxKappa", "MinB", "MaxB", "MinBeta", "MaxBeta", "MinGamma", "MaxGamma",
												"GridMethod"};

	private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, 
												  Integer.class, Integer.class, Integer.class,
												  Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class,
												  Integer.class,};
	private static final Object[] paramDefaults = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
												   10, -5, 5, 
												   0.1, 1.0, 0.0, 0.9, 1.0, 10.0, 0.5, 1.5, 0.1, 1.0,
												   0,}; //
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0, 1), //SE
			new Range(Integer.class, 0, 1), //H
			new Range(Integer.class, 0, 1), //Renyi
			new Range(Integer.class, 0, 1), //Tsallis
			new Range(Integer.class, 0, 1), //SNorm
			new Range(Integer.class, 0, 1), //SEscort	
			new Range(Integer.class, 0, 1), //SEta
			new Range(Integer.class, 0, 1), //SKappa
			new Range(Integer.class, 0, 1), //SB		
			new Range(Integer.class, 0, 1), //SBeta
			new Range(Integer.class, 0, 1), //SGamma		
			new Range(Integer.class, 2, Integer.MAX_VALUE), //Eps		
			new Range(Integer.class, -Integer.MAX_VALUE, Integer.MAX_VALUE), //minQ
			new Range(Integer.class, -Integer.MAX_VALUE, Integer.MAX_VALUE), //maxQ		
			new Range(Double.class, Double.MIN_VALUE, Double.MAX_VALUE), //minEta  Eta > 0
			new Range(Double.class, Double.MIN_VALUE, Double.MAX_VALUE), //maxEta  Eta > 0
			new Range(Double.class,              0.0, (1.0-Double.MIN_VALUE)), //minKappa 0 <= Kappa < 1
			new Range(Double.class, Double.MIN_VALUE, (1.0-Double.MIN_VALUE)), //maxKappa 0 <= Kappa < 1
			new Range(Double.class, Double.MIN_VALUE, Double.MAX_VALUE), //minB B > 0
			new Range(Double.class,              1.0, Double.MAX_VALUE), //maxB B > 0
			new Range(Double.class, Double.MIN_VALUE, Double.MAX_VALUE), //minBeta  
			new Range(Double.class, Double.MIN_VALUE, Double.MAX_VALUE), //maxBeta  
			new Range(Double.class,              0.0, 1.0), //minGamma 0 < Gamma < 1	
			new Range(Double.class, Double.MIN_VALUE, 1.0), //maxGamma 0 < Gamma < 1
			new Range(Integer.class, 0, 1), //GridMethods
			};

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public IqmOpGenEntDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the {@link OperationRegistry}.
	 */
	public static void registerWithJAI() {
		OperationRegistry opReg = JAI.getDefaultInstance()
				.getOperationRegistry();
		IqmOpGenEntDescriptor descriptor = new IqmOpGenEntDescriptor();
		opReg.registerDescriptor(descriptor);
		IqmOpGenEntRIF rif = new IqmOpGenEntRIF();
		RIFRegistry.register(opReg, descriptor.getName(), "IQM", rif);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		IqmOpGenEntDescriptor odesc = new IqmOpGenEntDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), IqmOpGenEnt.class, OperatorGUI_GenEnt.class,
				IqmOpGenEntValidator.class, odesc.getType(), odesc.getStackProcessingType());

		return odesc;
	}
	
} // END
