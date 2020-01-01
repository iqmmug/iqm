package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpUnsharpMask.java
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


import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpUnsharpMaskDescriptor;

/**
 * @author Ahammer, Kainz
 * @since 2009 04
 */
public class IqmOpUnsharpMask extends AbstractOperator {

	public IqmOpUnsharpMask() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@Override
	public IResult run(IWorkPackage wp) {
		PlanarImage piOut = null;
		
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		int kernelSize = pb.getIntParameter("KernelSize");
		float gain     = pb.getFloatParameter("Gain");
		int size = kernelSize * kernelSize;
		float[] kernel = new float[size];
		// ********************************************************
		int method = 1;
		// ********************************************************
		if (method == 0) { // Mean
			for (int i = 0; i < kernelSize; i++) {
				for (int j = 0; j < kernelSize; j++) {
					kernel[i * kernelSize + j] = 1.0f / size;
				}
			}
		}
		if (method == 1) { // Gauss
			float radius = (kernelSize - 1) / 2;
			float sigma = radius;
			float total = 0.0f;
			for (int i = 0; i < kernelSize; i++) {
				for (int j = 0; j < kernelSize; j++) {
					kernel[i * kernelSize + j] = (float) ((Math.exp(-0.5
							* ((i - radius) * (i - radius) + (j - radius)
									* (j - radius)) / (sigma * sigma))) / (2 * Math.PI * (sigma * sigma)));
					total = total + kernel[i * kernelSize + j];
				}
			}
			for (int i = 0; i < kernel.length; i++) {
				kernel[i] = kernel[i] / total;
			}
		}
		KernelJAI kernelJAI = new KernelJAI(kernelSize, kernelSize, kernel);
		// System.out.println(KernelUtil.kernelToString(kernelJAI, true));

		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		ParameterBlock pbWork = new ParameterBlock();
		pbWork.addSource(pi);
		pbWork.add(kernelJAI);
		pbWork.add(gain);

		piOut = JAI.create("UnsharpMask", pbWork, rh);
		
		ImageModel im = new ImageModel(piOut);
		im.setFileName(String.valueOf(pi.getProperty("file_name")));
		im.setModelName(String.valueOf(pi.getProperty("image_name")));
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpUnsharpMaskDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpUnsharpMaskDescriptor.TYPE;
	}
	
}
