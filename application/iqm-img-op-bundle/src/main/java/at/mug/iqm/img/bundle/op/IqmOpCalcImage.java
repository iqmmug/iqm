package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCalcImage.java
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


import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpCalcImageDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 05
 */
public class IqmOpCalcImage extends AbstractOperator {

	public IqmOpCalcImage() {
		setCancelable(true);
	}

	@SuppressWarnings("unused")
	@Override
	public IResult run(IWorkPackage wp) {

		PlanarImage piOut = null;

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi1 = ((IqmDataBox) pb.getSource(0)).getImage();
		PlanarImage pi2 = ((IqmDataBox) pb.getSource(1)).getImage();
		
		String imgName1 = (String) pi1.getProperty("image_name");
		String imgName2 = (String) pi2.getProperty("image_name");	
		String fileName1 = String.valueOf(pi1.getProperty("file_name"));
		String fileName2 = String.valueOf(pi2.getProperty("file_name"));
		
		int calc = pb.getIntParameter(0);
		int resultOptions = pb.getIntParameter(1);

		String type1 = ImageTools.getImgType(pi1);
		String type2 = ImageTools.getImgType(pi2);
		// if (type1 != type2) {
		// Board.appendTexln("IqmOpCalcImage: Image types do not fit together!");
		// return null;
		// }
		double typeGreyMax1 = ImageTools.getImgTypeGreyMax(pi1);
		double typeGreyMax2 = ImageTools.getImgTypeGreyMax(pi2);

		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		RenderingHints rh = null;
		ParameterBlock pbWork = new ParameterBlock();

		switch (calc) {
			case 0: case 1: case 2: case 3: case 10: case 11:
				pbWork.addSource(pi1);
				pbWork.add(DataBuffer.TYPE_DOUBLE);
				pi1 = JAI.create("format", pbWork);
				
				pbWork.removeSources();
				pbWork.addSource(pi2);
				pbWork.add(DataBuffer.TYPE_DOUBLE);
				pi2 = JAI.create("format", pbWork);
				break;

			default:
				break;
		}
			
		fireProgressChanged(10);
		if (isCancelled(getParentTask())){
			return null;
		}

		pbWork.removeSources();
		pbWork.removeParameters();

		// if number of bands in each image does not match
		int pi1NumBands = pi1.getNumBands();
		int pi2NumBands = pi2.getNumBands();
		if (pi2NumBands == 1 && pi2NumBands < pi1NumBands) { // second image
																// less bands
																// than first
																// image
			ParameterBlockJAI pbMerge = new ParameterBlockJAI("bandmerge");
			for (int n = 0; n < pi1NumBands; n++) {
				pbMerge.setSource(pi2, n);
			}
			pi2 = JAI.create("bandmerge", pbMerge, null); //
		}
		fireProgressChanged(20);
		if (isCancelled(getParentTask())){
			return null;
		}
		

		pbWork.removeSources();
		pbWork.removeParameters();

		if (calc == 0) { // Add
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			piOut = JAI.create("add", pbWork, rh);
		}
		if (calc == 1) { // Subtract
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			piOut = JAI.create("Subtract", pbWork, rh);
		}
		if (calc == 2) { // Multiply
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			piOut = JAI.create("Multiply", pbWork, rh);
		}
		if (calc == 3) { // Divide
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			piOut = JAI.create("Divide", pbWork, rh);
		}
		if (calc == 4) { // AND
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			piOut = JAI.create("And", pbWork, rh);
		}
		if (calc == 5) { // OR
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			piOut = JAI.create("Or", pbWork, rh);
		}
		if (calc == 6) { // XOR
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			piOut = JAI.create("Xor", pbWork, rh);
		}
		if (calc == 6) { // XOR
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			piOut = JAI.create("Xor", pbWork, rh);
		}
		if (calc == 7) { // Min
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			piOut = JAI.create("Min", pbWork, rh);
		}
		if (calc == 8) { // Max
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			piOut = JAI.create("Max", pbWork, rh);
		}
		if (calc == 9) { // Average
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			PlanarImage pi3 = JAI.create("Add", pbWork, null);

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(pi3);
			pbWork.add(new double[] { 2, 2, 2 });
			piOut = JAI.create("DivideByConst", pbWork, null);

		}
		if (calc == 10) { // Difference
			pbWork.addSource(pi1);
			pbWork.addSource(pi2);
			PlanarImage pi3 = JAI.create("Subtract", pbWork, null);

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(pi3);
			piOut = JAI.create("Absolute", pbWork, null);
		}
		if (calc == 11) { // ROI
			pbWork.addSource(pi2);
			pbWork.add(new double[] { typeGreyMax2, typeGreyMax2, typeGreyMax2 }); // {255,
																				// 255,
																				// 255}
			PlanarImage pi3 = JAI.create("DivideByConst", pbWork, null);

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(pi1);
			pbWork.addSource(pi3);
			piOut = JAI.create("Multiply", pbWork, null); // multiply with [0,1]
		}
		fireProgressChanged(40);
		if (isCancelled(getParentTask())){
			return null;
		}
		

		// --------------------------------------------------------------------
		if (resultOptions == 0) { // clamp to image type
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			if (type1.equals(IQMConstants.IMAGE_TYPE_RGB))
				pbWork.add(DataBuffer.TYPE_BYTE);
			if (type1.equals(IQMConstants.IMAGE_TYPE_8_BIT))
				pbWork.add(DataBuffer.TYPE_BYTE);
			if (type1.equals(IQMConstants.IMAGE_TYPE_16_BIT))
				pbWork.add(DataBuffer.TYPE_USHORT);
			
			piOut = JAI.create("format", pbWork);
			fireProgressChanged(60);
			if (isCancelled(getParentTask())){
				return null;
			}
		}
		if (resultOptions == 1) { // normalize to byte
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			RenderedOp extrema = JAI.create("extrema", pbWork);
			double[] minVec = (double[]) extrema.getProperty("minimum");
			double[] maxVec = (double[]) extrema.getProperty("maximum");
			double min = minVec[0];
			double max = maxVec[0];
			fireProgressChanged(60);
			if (isCancelled(getParentTask())){
				return null;
			}
			

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			pbWork.add(new double[] { (typeGreyMax1 / (max - min)) }); // Rescale
			pbWork.add(new double[] { ((typeGreyMax1 * min) / (min - max)) }); // offset
			piOut = JAI.create("rescale", pbWork);

			fireProgressChanged(80);
			if (isCancelled(getParentTask())){
				return null;
			}
			
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			if (type1 == "RGB")
				pbWork.add(DataBuffer.TYPE_BYTE);
			if (type1 == "8 bit")
				pbWork.add(DataBuffer.TYPE_BYTE);
			if (type1 == "16 bit")
				pbWork.add(DataBuffer.TYPE_USHORT);
			piOut = JAI.create("format", pbWork);
		}

		if (resultOptions == 2) { 
			// Actually does nothing but keeps the data buffer TYPE_DOUBLE format!
		}
		ImageModel im = new ImageModel(piOut);
		im.setFileName(fileName1 + "-@-" +fileName2);
		im.setModelName(imgName1 + "-@-" + imgName2);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		
		fireProgressChanged(95);
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpCalcImageDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpCalcImageDescriptor.TYPE;
	}
}
