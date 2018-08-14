package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpROISegment.java
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


import java.awt.geom.Area;
import java.awt.image.renderable.ParameterBlock;
import java.util.Iterator;
import java.util.List;

import javax.media.jai.JAI;
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
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpROISegmentDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 12
 */
public class IqmOpROISegment extends AbstractOperator {

	public IqmOpROISegment() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@SuppressWarnings("rawtypes")
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
		
		fireProgressChanged(5);
		
		int method = pb.getIntParameter("Method");
		int binarize = pb.getIntParameter("Binarize");

		// String type = IqmTools.getImgTyp(pi);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		ROIShape rs = Application.getLook().getCurrentLookPanel()
				.getCurrentROILayer().getCurrentROIShape();

		if (rs == null) {
			BoardPanel
					.appendTextln("IqmOpROISegment: Currrent ROI is not defined and null!");
			BoardPanel
					.appendTextln("IqmOpROISegment: ROI processing not possible");
			return null;
		} else {
			BoardPanel
					.appendTextln("IqmOpROISegment: Current ROI is defined and valid");
		}
		if (method == 0) {
			// do nothing, already set
			// rs = IqmTools.getLookDisplayJAI().getCurrROIShape();
		}
		if (method == 1) {
			Area area = new Area(rs.getAsShape());
			List<ROIShape> shapeVector = Application.getLook()
					.getCurrentLookPanel().getCurrentROILayer()
					.getAllROIShapes();
			Iterator itr = shapeVector.iterator();
			while (itr.hasNext()) {
				ROIShape roiShape = (ROIShape) itr.next();
				if (roiShape != null) {
					area.add(new Area(roiShape.getAsShape()));
				}
			}
			rs = new ROIShape(area);
		}

		fireProgressChanged(50);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		if (binarize == 0) {
			TiledImage ti = new TiledImage(pi.getMinX(), pi.getMinY(),
					pi.getWidth(), pi.getHeight(), pi.getTileGridXOffset(),
					pi.getTileGridYOffset(), pi.getSampleModel(),
					pi.getColorModel());
			try {
				ti.setData(pi.copyData(), rs);
			} catch (NullPointerException e) {
				BoardPanel
						.appendTextln("IqmOpROISegment: Segmentation with ROI is not possible, perhaps the ROI is a line");
				// e.printStackTrace();
			}
			piOut = ti; // ROI segmented image
			// piOut = rs.getAsImage(); //output is only ROI as a smaller binary
			// image
			fireProgressChanged(75);
			if (isCancelled(getParentTask())){
				return null;
			}
		}
		if (binarize == 1) {
			ParameterBlock pbWork = new ParameterBlock();
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(pi);
			double[] c = new double[1];
			c[0] = typeGreyMax;
			pbWork.add(c);
			pi = JAI.create("AddConst", pbWork, null);

			if (pi.getNumBands() == 3) { // make single band image
				double[][] m = { { 1.0d / 3.0d, 1.0d / 3.0d, 1.0d / 3.0d, 0.0d } };
				pi = JAI.create("bandcombine",
						new ParameterBlock().addSource(pi).add(m), null);
			}
			TiledImage ti = new TiledImage(pi.getMinX(), pi.getMinY(),
					pi.getWidth(), pi.getHeight(), pi.getTileGridXOffset(),
					pi.getTileGridYOffset(), pi.getSampleModel(),
					pi.getColorModel());
			ti.setData(pi.copyData(), rs);
			piOut = ti;
			fireProgressChanged(75);
			if (isCancelled(getParentTask())){
				return null;
			}
		}
		piOut.setProperty("image_name", imgName);
		piOut.setProperty("file_name", fileName);
		
		ImageModel im = new ImageModel(piOut);
		im.setFileName(fileName);
		im.setModelName(imgName);
		
		fireProgressChanged(95);
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpROISegmentDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpROISegmentDescriptor.TYPE;
	}
}
