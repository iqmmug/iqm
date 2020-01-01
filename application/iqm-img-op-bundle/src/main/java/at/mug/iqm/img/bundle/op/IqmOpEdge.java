package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpEdge.java
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
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpEdgeDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 06
 */
public class IqmOpEdge extends AbstractOperator {
	
	public IqmOpEdge() {
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

		PlanarImage pi  = ((IqmDataBox) pb.getSource(0)).getImage();
		String imgName  = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));
		
		int method       = pb.getIntParameter("Method");
		int direction    = pb.getIntParameter("Direction");
		int kernelSize_1 = pb.getIntParameter("KernelSize1"); // the first DoG kernel
		int kernelSize_2 = pb.getIntParameter("KernelSize2"); // the second DoG kernel
		int resultOption = pb.getIntParameter("ResultOption");

		String type = ImageTools.getImgType(pi);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

		KernelJAI kernel_h     = null;
		KernelJAI kernel_v     = null;
		KernelJAI kernel       = null;
		KernelJAI kernelGauss1 = null;
		KernelJAI kernelGauss2 = null;

		RenderingHints rh = null;
		ParameterBlock pbFmt = new ParameterBlock();
		pbFmt.addSource(pi);
		pbFmt.add(DataBuffer.TYPE_FLOAT);
		pi = JAI.create("format", pbFmt, rh);

		// -----------------------------------------------------
		if (method == 0) { // Roberts
			float[] h_data = { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
					0.0f };
			float[] v_data = { -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
					0.0f };
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
		}
		// -----------------------------------------------------
		if (method == 1) { // Pixel difference see WK Pratt Digital Image
							// Processing 2nd ed. p503
			float[] h_data = { 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f,
					0.0f };
			float[] v_data = { 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
					0.0f };
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
		}
		// -----------------------------------------------------
		if (method == 2) { // Separated Pixel Difference see WK Pratt Digital
							// Image Processing 2nd ed. p503
			float[] h_data = { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f,
					0.0f };
			float[] v_data = { 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
					0.0f };
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
		}
		// -----------------------------------------------------
		if (method == 3) { // Sobel
			float[] h_data = { 1.0f, 0.0f, -1.0f, 2.0f, 0.0f, -2.0f, 1.0f,
					0.0f, -1.0f };
			float[] v_data = { -1.0f, -2.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
					2.0f, 1.0f };
			// for (int i = 0; i < h_data.length; i++){
			// h_data[i] = h_data[i]*(1.0f/4.0f);
			// }
			// for (int i = 0; i < v_data.length; i++){
			// v_data[i] = v_data[i]*(1.0f/4.0f);
			// }
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
			// kernel_h = KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL;
			// kernel_v = KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;
		}
		// -----------------------------------------------------
		if (method == 4) { // Prewitt
			float[] h_data = { 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f,
					0.0f, -1.0f };
			float[] v_data = { -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
					1.0f, 1.0f };
			// for (int i = 0; i < h_data.length; i++){
			// h_data[i] = h_data[i]*(1.0f/3.0f);
			// }
			// for (int i = 0; i < v_data.length; i++){
			// v_data[i] = v_data[i]*(1.0f/3.0f);
			// }
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
		}
		// -----------------------------------------------------
		if (method == 5) { // FreiChen
			float[] h_data = { 1.0f, 0.0f, -1.0f, (float) Math.sqrt(2), 0.0f,
					-(float) Math.sqrt(2), 1.0f, 0.0f, -1.0f };
			float[] v_data = { -1.0f, -(float) Math.sqrt(2), -1.0f, 0.0f, 0.0f,
					0.0f, 1.0f, (float) Math.sqrt(2), 1.0f };
			// for (int i = 0; i < h_data.length; i++){
			// h_data[i] = h_data[i]*(1.0f/(2.0f + (float)Math.sqrt(2)));
			// }
			// for (int i = 0; i < v_data.length; i++){
			// v_data[i] = v_data[i]*(1.0f/(2.0f + (float)Math.sqrt(2)));
			// }
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
		}
		// -----------------------------------------------------
		if (method == 6) { // Laplace
			float[] h_data = { -1.0f, 2.0f, -1.0f, -1.0f, 2.0f, -1.0f, -1.0f,
					2.0f, -1.0f };
			float[] v_data = { -1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f, -1.0f,
					-1.0f, -1.0f };
			float[] data = { -1.0f, -1.0f, -1.0f, -1.0f, 8.0f, -1.0f, -1.0f,
					-1.0f, -1.0f };
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
			kernel = new KernelJAI(3, 3, data);

		}
		if (method == 7) { // DoG
			
			// ################### construct first kernel #######################	
			int size_1 = kernelSize_1 * kernelSize_1;
			float[] kernel_1 = new float[size_1];
			float radius = (kernelSize_1 - 1) / 2;
			float sigma = radius;
			float total = 0.0f;
			for (int i = 0; i < kernelSize_1; i++) {
				for (int j = 0; j < kernelSize_1; j++) {
					kernel_1[i * kernelSize_1 + j] = (float) ((Math.exp(-0.5
							* ((i - radius) * (i - radius) + (j - radius)
									* (j - radius)) / (sigma * sigma))) / (2 * Math.PI * (sigma * sigma)));
					total = total + kernel_1[i * kernelSize_1 + j];
				}
			}
			for (int i = 0; i < kernel_1.length; i++) {
				kernel_1[i] = kernel_1[i] / total;
			}	
			kernelGauss1 = new KernelJAI(kernelSize_1, kernelSize_1, kernel_1);			
			
			// ################### construct second kernel #######################	
			int size_2 = kernelSize_2 * kernelSize_2;
			float[] kernel_2 = new float[size_2];
			radius = (kernelSize_2 - 1) / 2;
			sigma = radius;
			total = 0.0f;
			for (int i = 0; i < kernelSize_2; i++) {
				for (int j = 0; j < kernelSize_2; j++) {
					kernel_2[i * kernelSize_2 + j] = (float) ((Math.exp(-0.5
							* ((i - radius) * (i - radius) + (j - radius)
									* (j - radius)) / (sigma * sigma))) / (2 * Math.PI * (sigma * sigma)));
					total = total + kernel_2[i * kernelSize_2 + j];
				}
			}
			for (int i = 0; i < kernel_2.length; i++) {
				kernel_2[i] = kernel_2[i] / total;
			}
			kernelGauss2 = new KernelJAI(kernelSize_2, kernelSize_2, kernel_2);
		}
		// -----------------------------------------------------
		// if (method == ){ //
		// float[] h_data = { 0.0f, 0.0f, 0.0f,
		// 0.0f, 0.0f, 0.0f,
		// 0.0f, 0.0f, 0.0f };
		// float[] v_data = { 0.0f, 0.0f, 0.0f,
		// 0.0f, 0.0f, 0.0f,
		// 0.0f, 0.0f, 0.0f };
		// kernel_h = new KernelJAI(3,3, h_data);
		// kernel_v = new KernelJAI(3,3, v_data);
		// }

		// -----------------------------------------------------

		// int kernelSize = pbJAI.getIntParameter("KernelSize");

		//calculate edges using kernels 
		if (method == 7) { //DoG
			// compute the first Gaussian blur
			RenderingHints rh_gaussian = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
					BorderExtender.createInstance(BorderExtender.BORDER_COPY));
			ParameterBlock pbConv = new ParameterBlock();
			pbConv.addSource(pi);
			pbConv.add(kernelGauss1);
			PlanarImage gauss_1 = JAI.create("convolve", pbConv, rh_gaussian);
			
			// compute the second Gaussian blur
			pbConv.removeParameters();
			pbConv.add(kernelGauss2);
			PlanarImage gauss_2 = JAI.create("convolve", pbConv, rh_gaussian);
			
			// ################### subtract the images #######################	
			ParameterBlock pbSub = new ParameterBlock();
			pbSub.addSource(gauss_1);
			pbSub.addSource(gauss_2);
			piOut = JAI.create("subtract", pbSub, null);
	
		} else { //directional dependent calculations for all other methods
			rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
					BorderExtender.createInstance(BorderExtender.BORDER_COPY));
			if (direction == 0) { // GradientMagnitude
				pbFmt = new ParameterBlock();
				pbFmt.addSource(pi);
				pbFmt.add(kernel_h);
				pbFmt.add(kernel_v);
				piOut = JAI.create("GradientMagnitude", pbFmt, rh);
			}
			if (direction == 2) { // horizontal (vertical edges)
				pbFmt = new ParameterBlock();
				pbFmt.addSource(pi);
				pbFmt.add(kernel_h);
				piOut = JAI.create("Convolve", pbFmt, rh);

				double greyMaxHalf = Math.round(typeGreyMax / 2.0) - 1.0; // 127
				double[] addValue = { greyMaxHalf, greyMaxHalf, greyMaxHalf };
				pbFmt = new ParameterBlock();
				pbFmt.addSource(piOut);
				pbFmt.add(addValue);
				piOut = JAI.create("AddConst", pbFmt, null);
			}
			if (direction == 1) { // vertical (horizontal edges)
				pbFmt = new ParameterBlock();
				pbFmt.addSource(pi);
				pbFmt.add(kernel_v);
				piOut = JAI.create("Convolve", pbFmt, rh);

				double greyMaxHalf = Math.round(typeGreyMax / 2.0) - 1.0; // 127
				double[] addValue = { greyMaxHalf, greyMaxHalf, greyMaxHalf };
				pbFmt = new ParameterBlock();
				pbFmt.addSource(piOut);
				pbFmt.add(addValue);
				piOut = JAI.create("AddConst", pbFmt, null);
			}

			if (direction == 3) { // vertical and horizontal
				if (method == 6) { // 2D kernel Laplace
					pbFmt = new ParameterBlock();
					pbFmt.addSource(pi);
					pbFmt.add(kernel);
					piOut = JAI.create("Convolve", pbFmt, rh);
				} else { // for all kernels with 2 separate kernels
					pbFmt = new ParameterBlock();
					pbFmt.addSource(pi);
					pbFmt.add(kernel_v);
					piOut = JAI.create("Convolve", pbFmt, rh);

					pbFmt = new ParameterBlock();
					pbFmt.addSource(piOut);
					pbFmt.add(kernel_h);
					piOut = JAI.create("Convolve", pbFmt, rh);
				}

				double greyMaxHalf = Math.round(typeGreyMax / 2.0) - 1.0; // 127
				double[] addValue = { greyMaxHalf, greyMaxHalf, greyMaxHalf };
				pbFmt = new ParameterBlock();
				pbFmt.addSource(piOut);
				pbFmt.add(addValue);
				piOut = JAI.create("AddConst", pbFmt, null);
			}
		}
	
		
		
		

		// Handling of result
		// --------------------------------------------------------------------
		if (resultOption == 0) { // clamp to byte
			pbFmt = new ParameterBlock();
			pbFmt.addSource(piOut);
			if (type.equals(IQMConstants.IMAGE_TYPE_RGB))
				pbFmt.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))
				pbFmt.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))
				pbFmt.add(DataBuffer.TYPE_USHORT);
			
			piOut = JAI.create("format", pbFmt);
		}
		if (resultOption == 1) { // normalize to byte
			pbFmt = new ParameterBlock();
			pbFmt.addSource(piOut);
			RenderedOp extrema = JAI.create("extrema", pbFmt);
			double[] minVec = (double[]) extrema.getProperty("minimum");
			double[] maxVec = (double[]) extrema.getProperty("maximum");
			double min = minVec[0];
			double max = maxVec[0];

			double[] rescale = minVec; // initialize Rescale Vector
			double[] offset = minVec; // initialize Offset Vector

			for (int i = 0; i < rescale.length; i++) {
				rescale[i] = typeGreyMax / (maxVec[i] - minVec[i]);
				offset[i] = (typeGreyMax * minVec[i]) / (minVec[i] - maxVec[i]);
			}
			pbFmt = new ParameterBlock();
			pbFmt.addSource(piOut);
			pbFmt.add(new double[] { (typeGreyMax / (max - min)) }); // Rescale
			pbFmt.add(new double[] { ((typeGreyMax * min) / (min - max)) }); // offset
			// pb.add(rescale); //Rescale //unfortunately, doesn't work properly
			// pb.add(offset); //offset
			piOut = JAI.create("rescale", pbFmt);

			pbFmt = new ParameterBlock();
			pbFmt.addSource(piOut);
			if (type.equals(IQMConstants.IMAGE_TYPE_RGB))
				pbFmt.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))
				pbFmt.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))
				pbFmt.add(DataBuffer.TYPE_USHORT);
			
			piOut = JAI.create("format", pbFmt);
		}
		
		if (resultOption == 2){
			// actual float values
			// do nothing
		}
		
		ImageModel im = new ImageModel(piOut);
		im.setFileName(fileName);
		im.setModelName(imgName);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpEdgeDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpEdgeDescriptor.TYPE;
	}
}
