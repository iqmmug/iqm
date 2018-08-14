package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpResize.java
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
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.BorderExtender;
import javax.media.jai.Interpolation;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpResizeDescriptor;

/**
 * @author Ahammer
 * @since   2009 04
 */
public class IqmOpResize extends AbstractOperator {

	public IqmOpResize() {
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

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		float zoomX = pb.getFloatParameter("ZoomX");
		float zoomY = pb.getFloatParameter("ZoomY");
		int newWidth = pb.getIntParameter("NewWidth");
		int newHeight = pb.getIntParameter("NewHeight");
		int optIntP = pb.getIntParameter("Interpolation");
		int zoomOrSize = pb.getIntParameter("ZoomOrSize");
		// int numBands = pi.getData().getNumBands();
		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		if (zoomOrSize == 1) { // new size values are essential
			zoomX = (float) newWidth / (float) pi.getWidth();
			zoomY = (float) newHeight / (float) pi.getHeight();
		} else { // zoom values are essential
					// do nothing zoom values already set
		}

		ParameterBlock pbScale = new ParameterBlock();
		pbScale.addSource(pi);
		pbScale.add(zoomX);// x scale factor
		pbScale.add(zoomY);// y scale factor
		pbScale.add(0.0F);// x translate
		pbScale.add(0.0F);// y translate
		// if (optIntP == 0) pb.add(new InterpolationNearest());
		// if (optIntP == 1) pb.add(new InterpolationBilinear());
		// if (optIntP == 2) pb.add(new InterpolationBicubic(10)); //???? 1 oder
		// was????
		// if (optIntP == 3) pb.add(new InterpolationBicubic2(10));

		if (optIntP == 0)
			pbScale.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
		if (optIntP == 1)
			pbScale.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
		if (optIntP == 2)
			pbScale.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
		if (optIntP == 3)
			pbScale.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2));

		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		pi = JAI.create("scale", pbScale, rh);

		ImageModel im = new ImageModel(pi);
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
			this.name = new IqmOpResizeDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpResizeDescriptor.TYPE;
	}
}
