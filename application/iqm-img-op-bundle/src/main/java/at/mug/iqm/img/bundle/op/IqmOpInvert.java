package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpInvert.java
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


import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.img.bundle.descriptors.IqmOpInvertDescriptor;

/**
 * @author Ahammer
 * @since 2009 03
 */
public class IqmOpInvert extends AbstractOperator {

	public IqmOpInvert() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@Override
	public IResult run(IWorkPackage wp) {
		PlanarImage result = null;

		// clone the parameters and sources
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		// get the parameters
		int invOpt = pb.getIntParameter(0);
		// get the sources from the work package
		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		
		if (invOpt == 0) { // Invert Image
			
			// TODO convert to TYPE_BYTE before inverting, since 
			// JAI does not support FLOAT/DOUBLE data type inversion
						
			ParameterBlock pbInvert = pb.toParameterBlock();
			pbInvert.removeSources();
			pbInvert.addSource(pi);

			result = JAI.create("Invert", pbInvert, null);
		}

		if (invOpt == 1) { // Invert lookup table
			DialogUtil.getInstance().showDefaultInfoMessage(
					"LUT inversion not implemented yet!");
			return null;
		}

		// OLD IMPLEMENTATION
		// does work for indexed color models
		// if (ImageAnalyzer.isIndexed(pi)) {
		// // System.out.println("IndexColorModel");
		// result = JAI.create("Invert", pb, null);
		// }
		// // does not work for indexed color models
		// else { // This routine uses a JAI custom operator
		// (IqmOpInvertOpImage)
		// // System.out.println("Other color model");
		// result = JAI.create(
		// "IqmOpInvert", pb,
		// null);
		// }
		ImageModel im = new ImageModel(result);
		im.setFileName(fileName);
		im.setModelName(imgName);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpInvertDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpInvertDescriptor.TYPE;
	}
}
