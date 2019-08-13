package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCrop.java
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


import java.awt.Rectangle;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;
import javax.media.jai.TiledImage;

import at.mug.iqm.api.Application;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpCropDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 06
 */
public class IqmOpCrop extends AbstractOperator {

	public IqmOpCrop() {
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

		fireProgressChanged(5);
		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		int method = pb.getIntParameter("Method");
		int offSetX = pb.getIntParameter("OffSetX");
		int offSetY = pb.getIntParameter("OffSetY");
		int newWidth = pb.getIntParameter("NewWidth");
		int newHeight = pb.getIntParameter("NewHeight");
		int offSet = pb.getIntParameter("OffSet");

		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		if (method == 0) { // Manual
			if (offSet == 0) {// Center
				offSetX = (pi.getWidth() - newWidth) / 2;
				offSetY = (pi.getHeight() - newHeight) / 2;
			}
			if (offSet == 1) {// Left Top
				offSetX = 0;
				offSetY = 0;
			}
			if (offSet == 2) {// Left Bottom
				offSetX = 0;
				offSetY = pi.getHeight() - newHeight;
			}
			if (offSet == 3) {// Right Top
				offSetX = pi.getWidth() - newWidth;
				offSetY = 0;
			}
			if (offSet == 4) {// Right Bottom
				offSetX = pi.getWidth() - newWidth;
				offSetY = pi.getHeight() - newHeight;
			}
			if (offSet == 5) {// User
				// do nothing, already set
			}
		}
		if (method == 1) { // ROI
			ROIShape rs = Application.getLook().getCurrentLookPanel()
					.getCurrentROILayer().getCurrentROIShape();
			Rectangle bounds = rs.getBounds();
			offSetX = bounds.x;
			offSetY = bounds.y;
			newWidth = bounds.width;
			newHeight = bounds.height;
		}

		// int numBands = pi.getData().getNumBands();
		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		// int imgWidth = pi.getWidth();
		// int imgHeight = pi.getHeight();

		ParameterBlock pbCrop = new ParameterBlock();
		pbCrop.addSource(pi);
		pbCrop.add((float) offSetX);
		pbCrop.add((float) offSetY);
		pbCrop.add((float) newWidth);
		pbCrop.add((float) newHeight);
		
		fireProgressChanged(50);

		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY)); 
		try {
			piOut = JAI.create("Crop", pbCrop, null);
		} catch (Exception e) {
			BoardPanel
					.appendTextln("IqmOpCrop: Crop error, return original image");
			piOut = pi;
		}
		if (piOut.getMinX() != 0 || piOut.getMinY() != 0) {
			ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
			pbTrans.addSource(piOut);
			pbTrans.setParameter("xTrans", piOut.getMinX() * -1.0f);
			pbTrans.setParameter("yTrans", piOut.getMinY() * -1.0f);
			piOut = JAI.create("translate", pbTrans);
		}
		// tiled image necessary
		// because jpg input images have no tiles defined
		// output without proper offset if saved again as jpg
		// TiledImage ti = new TiledImage(pi.getMinX(), pi.getMinY(),
		// pi.getWidth(), pi.getHeight(),
		// pi.getTileGridXOffset(), pi.getTileGridYOffset(),
		// pi.getSampleModel(), pi.getColorModel());
		TiledImage ti = new TiledImage(0, 0, newWidth, newHeight,
				pi.getTileGridXOffset(), pi.getTileGridYOffset(),
				pi.getSampleModel(), pi.getColorModel());

		ti.setData(piOut.getData());

		ti.setProperty("image_name", imgName);
		ti.setProperty("file_name", fileName);
		
		ImageModel im = new ImageModel(ti);
		im.setModelName(imgName);
		im.setFileName(fileName);
		
		fireProgressChanged(95);
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpCropDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpCropDescriptor.TYPE;
	}
}
