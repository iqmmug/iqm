package at.mug.iqm.plot.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpResampleDescriptor.java
 * 
 * $Id: IqmOpResampleDescriptor.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/descriptors/IqmOpResampleDescriptor.java $
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


import javax.media.jai.util.Range;
import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.AbstractOperatorDescriptor;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.plot.bundle.gui.PlotGUI_Resample;
import at.mug.iqm.plot.bundle.op.PlotOpResample;
import at.mug.iqm.plot.bundle.validators.PlotOpResampleValidator;


/**
 * @author Ahammer
 * @since  2018-11
 */
@SuppressWarnings("rawtypes")
public class PlotOpResampleDescriptor extends AbstractOperatorDescriptor {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7921279526375089452L;
	
	public static final Integer DOWNSAMPLE = 0;
	public static final Integer UPSAMPLE   = 1;
	
	public static final Integer INTERPOLATION_NONE     = 0;
	public static final Integer INTERPOLATION_BILINEAR = 1;
	public static final Integer INTERPOLATION_BICUBIC  = 2;
	public static final Integer INTERPOLATION_BICUBIC2 = 3;


	public static final OperatorType TYPE = OperatorType.PLOT;
	
	private static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.PLOT };

	private static final String[][] resources = {
			{ "GlobalName", "PlotOpResample" },
			{ "Vendor", "mug.qmnm" },
			{ "Description", "Resamples plots" },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "Resample Option" }, //Down , Up
			{ "arg1Desc", "Resample Factor" },
			{ "arg2Desc", "Interpolation" }, // None, Bilinear, Bicubic, Bicubic2
			};

	private static final int numSources = 1;
	private static final String[] paramNames = {"ResampleOption", "ResampleFactor", "Interpolation"};
	
	private static final Class[] paramClasses = {Integer.class,  Integer.class, Integer.class};
	private static final Object[] paramDefaults = {0, 2,  1};
	private static final Range[] validParamValues = {
			new Range(Integer.class, 0,  1),
			new Range(Integer.class, 2,  Integer.MAX_VALUE),
			new Range(Integer.class, 0, 3)
	};

	/**
	 * constructor: calls AbstractOperatorDescriptor
	 */
	public PlotOpResampleDescriptor() {
		super(resources, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}


	
	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		PlotOpResampleDescriptor odesc = new PlotOpResampleDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), PlotOpResample.class, PlotGUI_Resample.class,
				PlotOpResampleValidator.class, odesc.getType(),
				odesc.getStackProcessingType());

		return odesc;
	}

} // END
