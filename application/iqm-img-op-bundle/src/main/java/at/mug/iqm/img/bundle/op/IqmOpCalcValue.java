package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCalcValue.java
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

import javax.media.jai.JAI;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpCalcValueDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 05
 */
public class IqmOpCalcValue extends AbstractOperator {

	public IqmOpCalcValue() {
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

		PlanarImage pi1 = ((IqmDataBox) pb.getSource(0)).getImage();
		String imgName = (String) pi1.getProperty("image_name");
		String fileName = String.valueOf(pi1.getProperty("file_name"));
		
		int calc = pb.getIntParameter(0);
		double v = pb.getDoubleParameter(1);
		double value[] = { v, v, v };
		int resultOptions = pb.getIntParameter(2);

		String type = ImageTools.getImgType(pi1);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi1);

		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		RenderingHints rh = null;
		ParameterBlock pbWork = new ParameterBlock();
		if ((calc == 0) || (calc == 1) || (calc == 2) || (calc == 3)
				|| (calc == 4) || (calc == 5)) {
			pbWork.addSource(pi1);
			pbWork.add(DataBuffer.TYPE_DOUBLE);
			pi1 = JAI.create("format", pbWork);
		}
		if ((calc == 6) || (calc == 7) || (calc == 8)) {
			// pb.addSource(pi1);
			// pb.add(DataBuffer.TYPE_BYTE);
			// pi1 = JAI.create("format", pb);
		}

		pbWork.removeSources();
		pbWork.removeParameters();

		if (calc == 0) { // Add
			pbWork.addSource(pi1);
			pbWork.add(value);
			piOut = JAI.create("AddConst", pbWork, rh);
		} else if (calc == 1) { // Subtract Const from Image
			pbWork.addSource(pi1);
			pbWork.add(value);
			piOut = JAI.create("SubtractConst", pbWork, rh);
		} else if (calc == 2) { // Multiply
			pbWork.addSource(pi1);
			pbWork.add(value);
			piOut = JAI.create("MultiplyConst", pbWork, rh);
		} else if (calc == 3) { // Divide
			pbWork.addSource(pi1);
			pbWork.add(value);
			piOut = JAI.create("DivideByConst", pbWork, rh);
		} else if (calc == 4) { // Subtract Image From Const
			pbWork.addSource(pi1);
			pbWork.add(value);
			piOut = JAI.create("SubtractFromConst", pbWork, rh);
		} else if (calc == 5) { // Divide Into Constant
			pbWork.addSource(pi1);
			pbWork.add(value);
			piOut = JAI.create("DivideIntoConst", pbWork, rh);
		} else if (calc == 6) { // AND
			pbWork.addSource(pi1);
			// Integer[] intValue = new Integer[3];
			// intValue[0] = (int) Math.round(value[0]);
			// intValue[1] = (int) Math.round(value[1]);
			// intValue[2] = (int) Math.round(value[2]);

			int[] intValue = { (int) Math.round(value[0]) };
			pbWork.add(intValue);
			piOut = JAI.create("AndConst", pbWork, rh);
		} else if (calc == 7) { // OR
			pbWork.addSource(pi1);
			// Integer[] intValue = new Integer[3];
			// intValue[0] = (int) Math.round(value[0]);
			// intValue[1] = (int) Math.round(value[1]);
			// intValue[2] = (int) Math.round(value[2]);

			int[] intValue = { (int) Math.round(value[0]) };
			pbWork.add(intValue);
			piOut = JAI.create("OrConst", pbWork, rh);
		} else if (calc == 8) { // XOR
			pbWork.addSource(pi1);
			// Integer[] intValue = new Integer[3];
			// intValue[0] = (int) Math.round(value[0]);
			// intValue[1] = (int) Math.round(value[1]);
			// intValue[2] = (int) Math.round(value[2]);

			int[] intValue = { (int) Math.round(value[0]) };
			pbWork.add(intValue);
			piOut = JAI.create("XorConst", pbWork, rh);
		}

		// --------------------------------------------------------------------
		if (resultOptions == 0) { // clamp to byte
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			if (type.equals(IQMConstants.IMAGE_TYPE_RGB))
				pbWork.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))
				pbWork.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))
				pbWork.add(DataBuffer.TYPE_USHORT);
			
			piOut = JAI.create("format", pbWork);
		} else if (resultOptions == 1) { // normalize to byte
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			RenderedOp extrema = JAI.create("extrema", pbWork);
			double[] minVec = (double[]) extrema.getProperty("minimum");
			double[] maxVec = (double[]) extrema.getProperty("maximum");
			double min = minVec[0];
			double max = maxVec[0];

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			pbWork.add(new double[] { (typeGreyMax / (max - min)) }); // Rescale
			pbWork.add(new double[] { ((typeGreyMax * min) / (min - max)) }); // offset
			piOut = JAI.create("rescale", pbWork);

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			if (type.equals(IQMConstants.IMAGE_TYPE_RGB))
				pbWork.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))
				pbWork.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))
				pbWork.add(DataBuffer.TYPE_USHORT);
			
			piOut = JAI.create("format", pbWork);
		}

		else if (resultOptions == 2) { // Actual
			// does nothing
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
			this.name = new IqmOpCalcValueDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpCalcValueDescriptor.TYPE;
	}
}
