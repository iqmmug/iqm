package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpImgStabilizer.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2021 Helmut Ahammer, Philipp Kainz
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
import at.mug.iqm.img.bundle.descriptors.IqmOpImgStabilizerDescriptor;
import at.mug.iqm.img.bundle.imagej.IqmImage_Stabilizer;

/**
 * This is an implementation of the ImageJ ImageStabilizer plugin by Kang Li
 * K. Li, "The image stabilizer plugin for ImageJ," http://www.cs.cmu.edu/~kangli/code/Image_Stabilizer.html
 * This plugin stabilizes jittery image stacks using the Lucas-Kanade algorithm. It supports both grayscale and color images.
 * 
 * @author Ahammer, Kainz
 * @since 2010 05
 */
public class IqmOpImgStabilizer extends AbstractOperator {

	public IqmOpImgStabilizer() {
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
		
		PlanarImage pi1 = ((IqmDataBox) pb.getSource(0)).getImage();
		PlanarImage pi2 = ((IqmDataBox) pb.getSource(1)).getImage();
		
		int regMode = pb.getIntParameter("RegMode");
		int pyrLevels = pb.getIntParameter("PyrLevels");
		float tempUpCo = pb.getFloatParameter("TempUpCo");
		float maxIt = pb.getFloatParameter("MaxIt");
		float errTol = pb.getFloatParameter("ErrTol");
		// int logCo = pb.getIntParameter("LogCo");

		Image image1 = pi1.getAsBufferedImage();
		Image image2 = pi2.getAsBufferedImage();
		// SampleModel sampleModel = pi1.getSampleModel();
		String imgName = String.valueOf(pi2.getProperty("image_name"));
		String fileName = String.valueOf(pi2.getProperty("file_name"));
		ImagePlus targetImPlus = new ImagePlus("", image1);
		ImagePlus sourceImPlus = new ImagePlus("", image2);
		// ImageProcessor targetMskIP = targetImPlus.getProcessor();
		// ImageProcessor sourceMskIP = sourceImPlus.getProcessor();

		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		// RenderingHints rh = null;
		// ParameterBlock pb = new ParameterBlock();
		fireProgressChanged(25);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		BoardPanel
				.appendTextln("IqmOpImgStabilizer: Stabilization, please wait.......");

		IqmImage_Stabilizer imgSt = new IqmImage_Stabilizer();
		ImagePlus imp = new ImagePlus();
		imgSt.processIqm(targetImPlus, sourceImPlus, regMode, pyrLevels,
				tempUpCo, maxIt, errTol);
		imp = imgSt.getStabilizedImage();
		
		fireProgressChanged(75);
		if (isCancelled(getParentTask())){
			return null;
		}

		// int bytesPerPixel = imp.getBytesPerPixel();
		// System.out.println("IqmOpImgStabilizer: bytesPerPixel: "+bytesPerPixel
		// );
		// IJ.run(imp, "RGB Color", "");
		// imp.updateImage();
		// if (pi1.getNumBands() == 1){ //grey
		// IJ.run(imp, "8-bit", "");
		// }
		imp.updateImage();
		// Image imageResult = imp.getBufferedImage();
		Image imageResult = imp.getImage();
		piOut = JAI.create("AWTImage", imageResult);

		// if (((PlanarImage)piOut).getColorModel() instanceof IndexColorModel){
		// IndexColorModel icm =
		// (IndexColorModel)((PlanarImage)piOut).getColorModel();
		// byte[][] data = new byte[3][icm.getMapSize()];
		// icm.getReds(data[0]);
		// icm.getGreens(data[1]);
		// icm.getBlues(data[2]);
		// LookupTableJAI lut = new LookupTableJAI(data);
		//
		// ParameterBlock pb = new ParameterBlock();
		// pb.addSource(piOut);
		// pb.add(lut);
		// piOut = JAI.create("lookup", pb); //RGB image
		// }

		// Folgendes geht auch
		// ImageLayout layout = new ImageLayout();
		// ColorModel cm = new
		// ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new
		// int[] { 8, 8, 8 }, false, true, Transparency.OPAQUE,
		// DataBuffer.TYPE_BYTE);
		// layout.setColorModel(cm);
		// RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT,
		// layout);
		// ParameterBlockJAI pbFormat = new ParameterBlockJAI("format");
		// pbFormat.addSource(piOut);
		// piOut = JAI.create("format", pbFormat, hints);

		ParameterBlockJAI pbFormat = new ParameterBlockJAI("format");
		pbFormat.addSource(piOut);
		pbFormat.setParameter("dataType", DataBuffer.TYPE_BYTE);
		// piOut = JAI.create("format", pbFormat, hints);
		piOut = JAI.create("format", pbFormat);

		// if (pi1.getNumBands() == 1){ //grey
		// double[][] matrix = {{ 0.114, 0.587, 0.299, 0 }};
		//
		// ParameterBlock pb = new ParameterBlock();
		// pb.addSource(piOut);
		// pb.add(matrix);
		// piOut = JAI.create("bandcombine", pb, null);
		// }

		ImageModel im = new ImageModel(piOut);
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
			this.name = new IqmOpImgStabilizerDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpImgStabilizerDescriptor.TYPE;
	}
}
