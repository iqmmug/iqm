package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCoGReg.java
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


import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.BorderExtender;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
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
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpCoGRegDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 07
 */
public class IqmOpCoGReg extends AbstractOperator {

	public IqmOpCoGReg() {
		setCancelable(true);
	}

	/**
	 * This method shifts the image
	 * 
	 * @param PlanarImage
	 *            pi
	 * @param dX
	 * @param dY
	 */
	private PlanarImage shiftImage(PlanarImage pi, int dX, int dY) {
		int width = pi.getWidth();
		int height = pi.getHeight();
		PlanarImage piOut = null;
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

		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(left);
		pb.add(right);
		pb.add(top);
		pb.add(bottom);
		if (pi.getNumBands() == 1)
			pb.add(BorderExtender.createInstance(BorderExtender.BORDER_ZERO));// Zero
		if (pi.getNumBands() == 3) {
			double[] fillValue = new double[pi.getNumBands()];
			for (int i = 0; i < pi.getNumBands(); i++) {
				fillValue[i] = 255.0;
			}
			pb.add(new BorderExtenderConstant(fillValue));// Const
		}
		
		fireProgressChanged(40);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		piOut = JAI.create("Border", pb, null);
		if (piOut.getMinX() != 0 || piOut.getMinY() != 0) {
			ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
			pbTrans.addSource(piOut);
			pbTrans.setParameter("xTrans", piOut.getMinX() * -1.0f);
			pbTrans.setParameter("yTrans", piOut.getMinY() * -1.0f);
			piOut = JAI.create("translate", pbTrans);
		}
		
		fireProgressChanged(60);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		
		int offSetX = 0;
		int offSetY = 0;
		int newWidth = width;
		int newHeight = height;
		if (dX < 0)
			offSetX = Math.abs(dX);
		if (dY > 0)
			offSetY = Math.abs(dY);
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piOut);
		pb.add((float) offSetX);
		pb.add((float) offSetY);
		pb.add((float) newWidth);
		pb.add((float) newHeight);
		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY)); 
		try {
			piOut = JAI.create("Crop", pb, rh);
			fireProgressChanged(80);
			
			if (isCancelled(getParentTask())){
				return null;
			}
		} catch (Exception e) {
			BoardPanel
					.appendTextln("CROP: Crop error, return original image");
			piOut = pi;
		}
		if (piOut.getMinX() != 0 || (piOut.getMinY() != 0)) {
			ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
			pbTrans.addSource(piOut);
			pbTrans.setParameter("xTrans", piOut.getMinX() * -1.0f);
			pbTrans.setParameter("yTrans", piOut.getMinY() * -1.0f);
			piOut = JAI.create("translate", pbTrans);
		}
		fireProgressChanged(95);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		
		return piOut;
	}

	@Override
	public IResult run(IWorkPackage wp) {

		// PlanarImage piOut = null;
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi1 = ((IqmDataBox) pb.getSource(0)).getImage();
		PlanarImage pi2 = ((IqmDataBox) pb.getSource(1)).getImage();
		
		String imgName1 = String.valueOf(pi1.getProperty("image_name"));
		String fileName1 = String.valueOf(pi1.getProperty("file_name"));
		
		// int imgWidth = pi1.getWidth();
		int height = pi1.getHeight();
		// int first = pbJAI.getIntParameter("First");

		// int numBands = pi.getData().getNumBands();
		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		// int greyTypMax = (int) IqmTools.getImgTypeGreyMax(pi);
		fireProgressChanged(10);
		if (isCancelled(getParentTask())){
			return null;
		}

		double[] center1 = ImageTools.getCenterOfGravity(pi1);
		double[] center2 = ImageTools.getCenterOfGravity(pi2);
		center1[0] = center1[0];
		center2[0] = center2[0];
		center1[1] = height - 1 - center1[1];
		center2[1] = height - 1 - center2[1];

		int dX = (int) Math.round(center1[0] - center2[0]);
		int dY = (int) Math.round(center1[1] - center2[1]);
		// System.out.println("IqmCoGReg: dX  dY " + dX + "   "+dY);
		// piOut = this.shiftImage(pi2, dX, dY);

		fireProgressChanged(20);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		ImageModel im = new ImageModel(this.shiftImage(pi2, dX, dY));
		im.setFileName(fileName1);
		im.setModelName(imgName1);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpCoGRegDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpCoGRegDescriptor.TYPE;
	}
}
