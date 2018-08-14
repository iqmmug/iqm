/*
 * This is an implementation of the ImageJ bUnwarpJ plugin by I. Arganda-Carreras
 * I. Arganda-Carreras, C. Sanchez Sorzano, R. Marabini, J. M. Carazo, C. Ortiz-de Solorzano, and J. Kybic,
 * "Consistent and elastic registration of histological sections using vector-spline regularization",
 * in Computer Vision Approaches to Medical Image Analysis,
 * ser. Lecture Notes in Computer Science, vol. 4241.
 * Springer Berlin / Heidelberg, May 2006, pp. 85-95.
 * http://biocomp.cnb.uam.es/~iarganda/bUnwarpJ/
 */

package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpBUnwarpJ.java
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
import ij.gui.PointRoi;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;

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
import at.mug.iqm.img.bundle.descriptors.IqmOpBUnwarpJDescriptor;
import at.mug.iqm.img.bundle.imagej.bUnwarpJ_;


/**
 * @author Ahammer, Kainz
 * @since 2009 05
 */
public class IqmOpBUnwarpJ extends AbstractOperator {

	public IqmOpBUnwarpJ() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@SuppressWarnings("unused")
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
		PlanarImage pi2 = ((IqmDataBox) pb.getSource(1)).getImage();

		int regMode = pb.getIntParameter("RegMode");
		int subSamp = pb.getIntParameter("SubSamp");
		int initDef = pb.getIntParameter("InitDef");
		int finalDef = pb.getIntParameter("FinalDef");
		float divW = pb.getFloatParameter("DivW");
		float curlW = pb.getFloatParameter("CurlW");
		float landW = pb.getFloatParameter("LandW");
		float imgW = pb.getFloatParameter("ImgW");
		float consW = pb.getFloatParameter("ConsW");
		float stopTh = pb.getFloatParameter("StopTh");

		// String regModeStr;
		// if (regMode == 0){
		// regModeStr = "Fast";
		// }
		// if (regMode == 1){
		// regModeStr = "Accurate";
		// }
		// if (regMode == 2){
		// regModeStr = "Mono";
		// }
		// //-------------------------------------
		// String initDefStr = null;
		// if (initDef == 0){
		// initDefStr = "Very Coarse";
		// }
		// if (initDef == 1){
		// initDefStr = "Coarse";
		// }
		// if (initDef == 2){
		// initDefStr = "Fine";
		// }
		// if (initDef == 3){
		// initDefStr = "Very Fine";
		// }
		// //-------------------------------------
		// String finalDefStr = null;
		// if (finalDef == 0){
		// finalDefStr = "Very Coarse";
		// }
		// if (finalDef == 1){
		// finalDefStr = "Coarse";
		// }
		// if (finalDef == 2){
		// finalDefStr = "Fine";
		// }
		// if (finalDef == 3){
		// finalDefStr = "Very Fine";
		// }

		Image image1 = pi1.getAsBufferedImage();
		Image image2 = pi2.getAsBufferedImage();
		SampleModel sampleModel = pi1.getSampleModel();
		String imgName = String.valueOf(pi2.getProperty("image_name"));
		String fileName = String.valueOf(pi2.getProperty("file_name"));
		ImagePlus targetImp = new ImagePlus("", image1);
		ImagePlus sourceImp = new ImagePlus("", image2);
		ImageProcessor targetMskIP = targetImp.getProcessor();
		ImageProcessor sourceMskIP = sourceImp.getProcessor();

		// PK 2012 03 14: dummy arrays necessary for imageJ 1.46g
		int[] dummyX = null;
		int[] dummyY = null;

		PointRoi sourcePointRoi = new PointRoi(dummyX, dummyY, finalDef);
		PointRoi targetPointRoi = new PointRoi(dummyX, dummyY, finalDef);
		sourceImp.setRoi(sourcePointRoi);
		sourceImp.setRoi(targetPointRoi);

		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		// RenderingHints rh = null;
		// ParameterBlock pb = new ParameterBlock();

		BoardPanel
				.appendTextln("IqmOpBUnwarpJ: Registration, please wait.......");
		ImagePlus[] impStack = bUnwarpJ_.alignImagesBatch(targetImp, sourceImp,
				targetMskIP, sourceMskIP, regMode, subSamp, initDef, finalDef,
				divW, curlW, landW, imgW, consW, stopTh);

		ImagePlus imp = impStack[0];
		// int bytesPerPixel = imp.getBytesPerPixel();
		// System.out.println("IqmOpBUnwarpJ: bytesPerPixel: "+bytesPerPixel );
		// IJ.run(imp, "RGB Color", "");
		// imp.updateImage();
		// if (pi1.getNumBands() == 1){ //grey
		// IJ.run(imp, "8-bit", "");
		// }
		imp.updateImage();
		// Image imageResult = imp.getBufferedImage();
		Image imageResult = imp.getImage();
		piOut = JAI.create("AWTImage", imageResult);

		// if ( piOut.getColorModel() instanceof IndexColorModel){
		// IndexColorModel icm = (IndexColorModel)( piOut.getColorModel();
		// byte[][] data = new byte[3][icm.getMapSize()];
		// icm.getReds(data[0]);
		// icm.getGreens(data[1]);
		// icm.getBlues(data[2]);
		// LookupTableJAI lut = new LookupTableJAI(data);
		//
		// ParameterBlock pb = new ParameterBlock();
		// pb.addSource( piOut);
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

		if (pi1.getNumBands() == 1) { // grey
			double[][] matrix = { { 0.114, 0.587, 0.299, 0 } };

			ParameterBlock pbBC = new ParameterBlock();
			pbBC.addSource(piOut);
			pbBC.add(matrix);
			piOut = JAI.create("bandcombine", pbBC, null);
		}

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
			this.name = new IqmOpBUnwarpJDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpBUnwarpJDescriptor.TYPE;
	}
}
