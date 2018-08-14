package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpWhiteBalance.java
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


import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.BoardPanel;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpWhiteBalanceDescriptor;

/**
 * @author Ahammer, Kainz
 * @since    2009 12
 */
public class IqmOpWhiteBalance extends AbstractOperator {

	public IqmOpWhiteBalance() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@Override
	public IResult run(IWorkPackage wp) {
		// Calculation: Image/(Mean of ROI) * 255

		// PlanarImage piOut = null;
		// piOut =
		// ,
		// pbJAI, null);
		// RenderingHints rh = IqmTools.getDefaultTileCacheRenderingHints();

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}
		
		fireProgressChanged(5);

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		int method = pb.getIntParameter("Method");
		int inR = pb.getIntParameter("InR");
		int inG = pb.getIntParameter("InG");
		int inB = pb.getIntParameter("InB");
		int outR = pb.getIntParameter("OutR");
		int outG = pb.getIntParameter("OutG");
		int outB = pb.getIntParameter("OutB");

		String type = ImageTools.getImgType(pi);
		// double typeGreyMax = IqmTools.getImgTypeGreyMax(pi);

		double[] out = new double[] { outR, outG, outB };

		ROIShape roi = Application.getLook().getCurrentLookPanel().getCurrentROILayer().getCurrentROIShape();
		if (method == 0) { // ROI
			if (roi == null) {
				BoardPanel
						.appendTextln("IqmOpWhiteBalance: ROI is not defined and null!");
				BoardPanel
						.appendTextln("IqmOpWhiteBalance: calculation with whole image!");
			} else {
				BoardPanel
						.appendTextln("IqmOpWhiteBalance: ROI is defined and valid");
			}
		}

		fireProgressChanged(30);
		
		ParameterBlock pbWork = new ParameterBlock();
		pbWork.addSource(pi);
		pbWork.add(DataBuffer.TYPE_FLOAT);
		pi = JAI.create("format", pbWork);

		fireProgressChanged(60);
		
		double[] mean = null;
		if (method == 0) { // ROI
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(pi);
			pbWork.add(roi); // ROI
			pbWork.add(1); // sampling
			pbWork.add(1);
			RenderedOp meanOp = JAI.create("Mean", pbWork, null);
			mean = (double[]) meanOp.getProperty("mean");
		}
		if (method == 1) { // Manual
			mean = new double[] { inR, inG, inB };
		}

		BoardPanel.appendTextln("IqmOpWhiteBalance: ROI Mean of Band 0 = "
				+ mean[0]);
		if (mean.length > 1)
			BoardPanel.appendTextln("IqmOpWhiteBalance: ROI Mean of Band 1 = "
					+ mean[1]);
		if (mean.length > 2)
			BoardPanel.appendTextln("IqmOpWhiteBalance: ROI Mean of Band 2 = "
					+ mean[2]);

		pbWork.removeSources();
		pbWork.removeParameters();
		pbWork.addSource(pi);
		pbWork.add(mean);
		pi = JAI.create("DivideByConst", pbWork, null);

		fireProgressChanged(50);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		pbWork.removeSources();
		pbWork.removeParameters();
		pbWork.addSource(pi);
		// pb.add(new double[] {255});
		pbWork.add(out);
		pi = JAI.create("MultiplyConst", pbWork);

		fireProgressChanged(75);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		pbWork.removeSources();
		pbWork.removeParameters();
		pbWork.addSource(pi);
		
		if (type.equals(IQMConstants.IMAGE_TYPE_RGB))
			pbWork.add(DataBuffer.TYPE_BYTE);
		if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))
			pbWork.add(DataBuffer.TYPE_BYTE);
		if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))
			pbWork.add(DataBuffer.TYPE_USHORT);
		
		pi = JAI.create("Format", pbWork);

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
			this.name = new IqmOpWhiteBalanceDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpWhiteBalanceDescriptor.TYPE;
	}
}
