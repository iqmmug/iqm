package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpAlign.java
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
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;

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
import at.mug.iqm.img.bundle.descriptors.IqmOpAlignDescriptor;

/**
 * @author Ahammer, Kainz
 * @since 2009 06
 */
public class IqmOpAlign extends AbstractOperator {

	public IqmOpAlign() {
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
		
		int method = pb.getIntParameter("Method");
		int angleGrad = pb.getIntParameter("Angle");
		int dX = pb.getIntParameter("DX");
		int dY = pb.getIntParameter("DY");
		int inter = pb.getIntParameter("Interpolation");
		float angle = 0;
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
		int width = pi.getWidth();
		int height = pi.getHeight();

		if (method == 0)
			angle = (float) (90 * (Math.PI / 180.0F)); // +90�
		if (method == 1)
			angle = (float) (-90 * (Math.PI / 180.0F)); // -90�
		if (method == 2)
			angle = (float) (180 * (Math.PI / 180.0F)); // 180�
		if (method == 3)
			angle = (float) (angleGrad * (Math.PI / 180.0F)); // Angle user
																// value
		if (method <= 3) { // all rotations
			ParameterBlock pbRotate = new ParameterBlock();
			pbRotate.addSource(pi);
			pbRotate.add((float) width / 2); // x Origin
			pbRotate.add((float) height / 2); // y Origin
			pbRotate.add(angle);
			pbRotate.add(interP);
			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
					BorderExtender.createInstance(BorderExtender.BORDER_COPY)); // nicht
																				// sicher
																				// ob
																				// noetig
			PlanarImage pRot = JAI.create("Rotate", pbRotate, rh);

			if (pRot.getMinX() != 0 || pRot.getMinY() != 0) {
				ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
				pbTrans.addSource(pRot);
				pbTrans.setParameter("xTrans", pRot.getMinX() * -1.0f);
				pbTrans.setParameter("yTrans", pRot.getMinY() * -1.0f);
				piOut = JAI.create("translate", pbTrans);
			} else {
				piOut = pRot;
			}
		}
		TransposeType flipType = null;
		if (method == 4)
			flipType = TransposeDescriptor.FLIP_VERTICAL;
		if (method == 5)
			flipType = TransposeDescriptor.FLIP_HORIZONTAL;
		if (method == 6)
			flipType = TransposeDescriptor.FLIP_DIAGONAL;
		if (method == 7)
			flipType = TransposeDescriptor.ROTATE_90;

		if ((method >= 4) && (method <= 7)) { // Flip
			ParameterBlock pbTranspose = new ParameterBlock();
			pbTranspose.addSource(pi);
			pbTranspose.add(flipType); //
			// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
			// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
			piOut = JAI.create("Transpose", pbTranspose, null);
		}
		if (method == 8) { // Shift
			int left = 0;
			int right = 0;
			int top = 0;
			int bottom = 0;
			if (dX > 0)
				left = Math.abs(dX);
			if (dX < 0)
				right = Math.abs(dX);
			if (dY > 0)
				bottom = Math.abs(dY);
			if (dY < 0)
				top = Math.abs(dY);

			ParameterBlock pbBorder = new ParameterBlock();
			pbBorder.addSource(pi);
			pbBorder.add(left);
			pbBorder.add(right);
			pbBorder.add(top);
			pbBorder.add(bottom);
			if (pi.getNumBands() == 1)
				pbBorder.add(BorderExtender
						.createInstance(BorderExtender.BORDER_ZERO));// Zero
			if (pi.getNumBands() == 3) {
				double[] fillValue = new double[pi.getNumBands()];
				for (int i = 0; i < pi.getNumBands(); i++) {
					fillValue[i] = 255.0;
				}
				pbBorder.add(new BorderExtenderConstant(fillValue));// Const
			}
			piOut = JAI.create("Border", pbBorder, null);
			if (piOut.getMinX() != 0 || piOut.getMinY() != 0) {
				ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
				pbTrans.addSource(piOut);
				pbTrans.setParameter("xTrans", piOut.getMinX() * -1.0f);
				pbTrans.setParameter("yTrans", piOut.getMinY() * -1.0f);
				piOut = JAI.create("translate", pbTrans);
			}
			int offSetX = 0;
			int offSetY = 0;
			int newWidth = width;
			int newHeight = height;
			if (dX < 0)
				offSetX = Math.abs(dX);
			if (dY > 0)
				offSetY = Math.abs(dY);
			pbBorder.removeSources();
			pbBorder.removeParameters();
			pbBorder.addSource(piOut);
			pbBorder.add((float) offSetX);
			pbBorder.add((float) offSetY);
			pbBorder.add((float) newWidth);
			pbBorder.add((float) newHeight);
			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
					BorderExtender.createInstance(BorderExtender.BORDER_COPY)); // nicht
																				// sicher
																				// ob
																				// noetig
			try {
				piOut = JAI.create("Crop", pbBorder, rh);
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
		}
		if (method == 9) { // Center
			double[] center1 = new double[2]; // Target
			center1[0] = width / 2;
			center1[1] = height / 2;
			double[] center2 = ImageTools.getCenterOfGravity(pi);
			// System.out.println("IqmOpAlign: center2: " + center2[0] + "  "+
			// center2[1]);
			center1[0] = center1[0];
			center2[0] = center2[0];
			center1[1] = height - 1 - center1[1];
			center2[1] = height - 1 - center2[1];

			dX = (int) Math.round(center1[0] - center2[0]);
			dY = (int) Math.round(center1[1] - center2[1]);
			// System.out.println("IqmOpAlign: dX  dY " + dX + "   "+dY);

			int left = 0;
			int right = 0;
			int top = 0;
			int bottom = 0;
			if (dX > 0)
				left = Math.abs(dX);
			if (dX < 0)
				right = Math.abs(dX);
			if (dY > 0)
				bottom = Math.abs(dY);
			if (dY < 0)
				top = Math.abs(dY);

			ParameterBlock pbBorder2 = new ParameterBlock();
			pbBorder2.addSource(pi);
			pbBorder2.add(left);
			pbBorder2.add(right);
			pbBorder2.add(top);
			pbBorder2.add(bottom);
			if (pi.getNumBands() == 1)
				pbBorder2.add(BorderExtender
						.createInstance(BorderExtender.BORDER_ZERO));// Zero
			if (pi.getNumBands() == 3) {
				double[] fillValue = new double[pi.getNumBands()];
				for (int i = 0; i < pi.getNumBands(); i++) {
					fillValue[i] = 255.0;
				}
				pbBorder2.add(new BorderExtenderConstant(fillValue));// Const
			}
			piOut = JAI.create("Border", pbBorder2, null);
			if (piOut.getMinX() != 0 || piOut.getMinY() != 0) {
				ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
				pbTrans.addSource(piOut);
				pbTrans.setParameter("xTrans", piOut.getMinX() * -1.0f);
				pbTrans.setParameter("yTrans", piOut.getMinY() * -1.0f);
				piOut = JAI.create("translate", pbTrans);
			}
			int offSetX = 0;
			int offSetY = 0;
			int newWidth = width;
			int newHeight = height;
			if (dX < 0)
				offSetX = Math.abs(dX);
			if (dY > 0)
				offSetY = Math.abs(dY);
			pbBorder2.removeSources();
			pbBorder2.removeParameters();
			pbBorder2.addSource(piOut);
			pbBorder2.add((float) offSetX);
			pbBorder2.add((float) offSetY);
			pbBorder2.add((float) newWidth);
			pbBorder2.add((float) newHeight);
			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
					BorderExtender.createInstance(BorderExtender.BORDER_COPY)); // nicht
																				// sicher
																				// ob
																				// noetig
			try {
				piOut = JAI.create("Crop", pbBorder2, rh);
			} catch (Exception e) {
				BoardPanel
						.appendTextln("IqmOpCrop: Crop error, return original image");
				piOut = pi;
			}
			if (piOut.getMinX() != 0 || (piOut.getMinY() != 0)) {
				ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
				pbTrans.addSource(piOut);
				pbTrans.setParameter("xTrans", piOut.getMinX() * -1.0f);
				pbTrans.setParameter("yTrans", piOut.getMinY() * -1.0f);
				piOut = JAI.create("translate", pbTrans);
			}

		}
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
			this.name = new IqmOpAlignDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpAlignDescriptor.TYPE;
	}
}
