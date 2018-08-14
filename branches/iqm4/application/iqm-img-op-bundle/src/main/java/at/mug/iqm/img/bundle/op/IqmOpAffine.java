package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpAffine.java
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
import java.awt.geom.AffineTransform;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.BorderExtender;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

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
import at.mug.iqm.img.bundle.descriptors.IqmOpAffineDescriptor;

/**
 * @author Ahammer, Kainz
 * @since 2009 06
 */
public class IqmOpAffine extends AbstractOperator {

	public IqmOpAffine() {
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

		fireProgressChanged(10);
		
		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		String fileName = String.valueOf(pi.getProperty("file_name"));
		String imgName = (String) pi.getProperty("image_name");
		
		float m00 = pb.getFloatParameter("M00");
		float m10 = pb.getFloatParameter("M10");
		float m01 = pb.getFloatParameter("M01");
		float m11 = pb.getFloatParameter("M11");
		float m02 = pb.getFloatParameter("M02");
		float m12 = pb.getFloatParameter("M12");
		int inter = pb.getIntParameter("Interpolation");

		Interpolation interP = null;
		if (inter == 0) { 
			interP = new InterpolationNearest();
		}
		if (inter == 1) {
			interP = new InterpolationBilinear();
		}
		if (inter == 2) {
			interP = new InterpolationBicubic(10); // ???? 1 oder was????
		}
		if (inter == 3) {
			interP = new InterpolationBicubic2(10);
		}
		// int numBands = pi.getData().getNumBands();
		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		// int imgWidth = pi.getWidth();
		// int imgHeight = pi.getHeight();

		AffineTransform at = new AffineTransform(m00, m10, m01, m11, m02, m12);
		ParameterBlock pbNew = new ParameterBlock();
		pbNew.addSource(pi);
		pbNew.add(at);
		pbNew.add(interP);
		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY)); 
		
		fireProgressChanged(50);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		try {
			pi = JAI.create("Affine", pbNew, rh);
		} catch (Exception e) {
			BoardPanel.appendTextln("IqmOpAffine: Affine transformation error, returning original image");
		}
		fireProgressChanged(95);
		if (isCancelled(getParentTask())){
			return null;
		}
		ImageModel im = new ImageModel(pi);
		im.setFileName(fileName);
		im.setModelName(imgName);
		
		return new Result(new IqmDataBox(pi));
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpAffineDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpAffineDescriptor.TYPE;
	}
}
