package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpBorder.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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

import javax.media.jai.BorderExtender;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpBorderDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 06
 */
public class IqmOpBorder extends AbstractOperator {

	// private static final Class<BorderDescriptor> BorderDescriptor = null;
	
	public IqmOpBorder() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@Override
	public IResult run(IWorkPackage wp) {
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}
		
		fireProgressChanged(5);

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		int method   = pb.getIntParameter("Method");
		int left     = pb.getIntParameter("Left");
		int right    = pb.getIntParameter("Right");
		int top      = pb.getIntParameter("Top");
		int bottom   = pb.getIntParameter("Bottom");
		double c     = pb.getIntParameter("Const"); // Value of Constant for
													// method constant
		int newWidth     = pb.getIntParameter("NewWidth");
		int newHeight    = pb.getIntParameter("NewHeight");
		int borderOrSize = pb.getIntParameter("BorderOrSize");

		int numBands = pi.getData().getNumBands();
		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		// int imgWidth = pi.getWidth();
		// int imgHeight = pi.getHeight();
		double[] fillValue = new double[numBands];
		for (int i = 0; i < numBands; i++) {
			fillValue[i] = c;
		}

		if (borderOrSize == IqmOpBorderDescriptor.PREFERENCE_BORDER) { // take border settings do nothing

		}
		if (borderOrSize == IqmOpBorderDescriptor.PREFERENCE_SIZE) { // take new imgWidth and new imgHeight
			int oldWidth = pi.getWidth();
			int oldHeight = pi.getHeight();
			left   = (newWidth - oldWidth) / 2;
			right  = newWidth - oldWidth - left;
			top    = (newHeight - oldHeight) / 2;
			bottom = newHeight - oldHeight - top;
		}

		ParameterBlock pbBrdr = new ParameterBlock();
		pbBrdr.addSource(pi);
		pbBrdr.add(left);
		pbBrdr.add(right);
		pbBrdr.add(top);
		pbBrdr.add(bottom);
		System.out.println("IqmOpBorder: method#: " + method);
		if (method == IqmOpBorderDescriptor.ZERO)     pbBrdr.add(BorderExtender.createInstance(BorderExtender.BORDER_ZERO));// Zero
		if (method == IqmOpBorderDescriptor.CONSTANT) pbBrdr.add(new BorderExtenderConstant(fillValue));// Const
		if (method == IqmOpBorderDescriptor.COPY)     pbBrdr.add(BorderExtender.createInstance(BorderExtender.BORDER_COPY));// Copy
		if (method == IqmOpBorderDescriptor.REFLECT)  pbBrdr.add(BorderExtender.createInstance(BorderExtender.BORDER_REFLECT));// Reflect
		if (method == IqmOpBorderDescriptor.WRAP)     pbBrdr.add(BorderExtender.createInstance(BorderExtender.BORDER_WRAP));// Wrap

		fireProgressChanged(50);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		pi = JAI.create("Border", pbBrdr, null);
		
		if (pi.getMinX() != 0 || pi.getMinY() != 0) {
			ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
			pbTrans.addSource(pi);
			pbTrans.setParameter("xTrans", pi.getMinX() * -1.0f);
			pbTrans.setParameter("yTrans", pi.getMinY() * -1.0f);
			pi = JAI.create("translate", pbTrans);
		}
		
		ImageModel im = new ImageModel(pi);
		im.setFileName(String.valueOf(pi.getProperty("file_name")));
		im.setModelName(String.valueOf(pi.getProperty("image_name")));
		
		fireProgressChanged(95);
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpBorderDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpBorderDescriptor.TYPE;
	}
}
