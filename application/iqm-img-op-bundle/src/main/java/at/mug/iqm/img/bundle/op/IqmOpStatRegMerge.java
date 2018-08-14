package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpStatRegMerge.java
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


import ij.ImagePlus;

import java.awt.Image;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;

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
import at.mug.iqm.img.bundle.descriptors.IqmOpStatRegMergeDescriptor;
import at.mug.iqm.img.bundle.imagej.IqmSRM;

/**
 * This uses an ImageJ algorithm by Johannes Schindelin
 * http://fiji.sc/wiki/index.php/Statistical_Region_Merging#cite_note-0
 * based on following algorithm:
 * R. Nock, F. Nielsen (2004), "Statistical Region Merging", IEEE Trans. Pattern Anal. Mach. Intell. 26 (11): 1452-1458
 * 
 * @author Ahammer, Kainz
 * @since   2012 01
 */
public class IqmOpStatRegMerge extends AbstractOperator {

	public IqmOpStatRegMerge() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@Override
	public IResult run(IWorkPackage wp) {

		PlanarImage piOut = null;
		// piOut =
		// ,
		
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		int nQ = pb.getIntParameter("nQ");
		int methodRGB = pb.getIntParameter("MethodRGB");
		int out = pb.getIntParameter("Out");

		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		// System.out.println("I'm here");

		this.fireProgressChanged(10);
		if (this.isCancelled(this.getParentTask())) return null;

		// get image
		Image image = pi.getAsBufferedImage();
		// ImagePlus imp = WindowManager.getCurrentImage();
		ImagePlus imp = new ImagePlus("", image);
		// if (imp == null) {
		// IqmBoardPanel.appendTexln("IqmOpStatRegMerge: No open image");
		// return null;
		// }
		if (imp.getType() != ImagePlus.GRAY8
				&& imp.getType() != ImagePlus.GRAY32
				&& imp.getType() != ImagePlus.COLOR_RGB) {
			BoardPanel.appendTextln("IqmOpStatRegMerge: only 8-bit possible");
			return null;
		}
		imp.setTitle("image_name");

		IqmSRM srm = new IqmSRM();
		ImagePlus impOut = srm.run(imp.getProcessor(), nQ, methodRGB, out);
		// impOut.show();

		this.fireProgressChanged(20);
		if (this.isCancelled(this.getParentTask())) return null;
		
		image = impOut.getBufferedImage();
		piOut = JAI.create("AWTImage", image);

		ParameterBlock pbWork = new ParameterBlock();
		pbWork.addSource(piOut);
		pbWork.add(DataBuffer.TYPE_BYTE); // for 8bit and RGB 16bit is not
										// implemented
		piOut = JAI.create("format", pbWork);

		ImageModel im = new ImageModel(piOut);
		im.setModelName(imgName);
		im.setFileName(fileName);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpStatRegMergeDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpStatRegMergeDescriptor.TYPE;
	}
	
}
