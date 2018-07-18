package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpWatershed.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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
import imageware.Builder;
import imageware.ImageWare;

import java.awt.Image;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;

import watershedflooding.Watershed;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpWatershedDescriptor;

/**
 * This is an implementation of an ImageJ plugin by Daniel Sage
 * Biomecial Imaging Group (BIG) Ecole Polytechnique Federale de Lausanne (EPFL), Lausanne, Switzerland
 * http://bigwww.epfl.ch/sage/soft/watershed/
 * note: the algorithm searches for dark objects in bright background
 * 
 * @author Ahammer, Kainz
 * @since   2010 05
 */
public class IqmOpWatershed extends AbstractOperator {

	public IqmOpWatershed() {
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
		int invert = pb.getIntParameter("Invert");
		int preProc = pb.getIntParameter("PreProc");
		int kernelSize = pb.getIntParameter("Kernel");
		int connect = pb.getIntParameter("Connectivity");
		int thresMin = pb.getIntParameter("ThresMin");
		int thresMax = pb.getIntParameter("ThresMax");
		int output = pb.getIntParameter("Output");

		// int numBands = pi.getData().getNumBands();
		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		// int sampleModel = pi.getSampleModel().getDataType();
		// int imgWidth = pi.getWidth();
		// int imgHeight = pi.getHeight();
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		// get image
		Image image = pi.getAsBufferedImage();
		// ImagePlus imp = WindowManager.getCurrentImage();
		ImagePlus imp = new ImagePlus("", image);
		// if (imp == null) {
		// IqmBoardPanel.appendTexln("IqmOpWatershed: No open image");
		// return null;
		// }
		if (imp.getType() != ImagePlus.GRAY8
				&& imp.getType() != ImagePlus.GRAY32) {
			BoardPanel
					.appendTextln("IqmOpWatershed: only 8-bit or 32-bit image possible ");
			return null;
		}

		fireProgressChanged(20);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		// create ImageWare image
		ImagePlus impWatershed = new ImagePlus("", imp.getImage());
		impWatershed.updateImage();
		ImageWare iw = Builder.wrap(impWatershed);

		if (invert == 0) {
		}
		if (invert == 1) { // invert
			// imp.getProcessor().invert();
			// imp.updateImage();
			iw.invert();
		}

		// Preprocessing if necessary
		if (preProc == 0) { // None
		}
		if (preProc == 1) { // Gauss
			double radius = Math.sqrt(kernelSize * kernelSize / Math.PI);
			iw.smoothGaussian(radius);
		}
		
		fireProgressChanged(30);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		// Start Watershed segmentation
		boolean displayProgressionMessage = false;
		Watershed watershed = new Watershed(displayProgressionMessage);
		boolean connectivity8 = false;
		if (connect == 0)
			connectivity8 = false; // 4
		if (connect == 1)
			connectivity8 = true; // 8
		watershed.doWatershed(iw, connectivity8, thresMin, thresMax);
		// output
		if (output == 0) { // Dams
			imp = watershed.getDams();
		}
		if (output == 1) { // Overlaid Red Dams
			imp = watershed.getRedDams(imp);
		}
		if (output == 2) { // Colorized Basins
			imp = watershed.getBasins();
		}
		if (output == 3) { // Composite
			imp = watershed.getComposite(imp);
		}
		
		fireProgressChanged(50);
		if (isCancelled(getParentTask())){
			return null;
		}

		// dams.show();
		// ImagePlus impInput = new ImagePlus("", image);
		// impInput.setTitle("Target");
		// WindowManager.closeAllWindows();
		// impInput.show();

		// Image imageResult = imp.getBufferedImage(); //Fehlermeldung
		Image imageResult = imp.getImage();
		piOut = JAI.create("AWTImage", imageResult); //
		// piOut = PlanarImage.wrapRenderedImage((RenderedImage) imageResult);
		// //macht Fehler bei 32bit bildern,

		// System.out.println("IqmOpWatershed: piOut.getNumBands() : " +
		// ((PlanarImage)piOut).getNumBands());
		// imp.show();
		// WindowManager.closeAllWindows();
		imp.flush();

		ParameterBlockJAI pbFormat = new ParameterBlockJAI("format");
		pbFormat.addSource(piOut);
		pbFormat.setParameter("dataType", DataBuffer.TYPE_BYTE);
		// piOut = JAI.create("format", pbFormat, hints);
		piOut = JAI.create("format", pbFormat);

		fireProgressChanged(60);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		// Palette to Grey 8bit conversion if necessary
		if (output == 0 || output == 3) { // Dams or Composite
			IndexColorModel icm = (IndexColorModel) piOut.getColorModel();
			byte[][] data = new byte[3][icm.getMapSize()];
			icm.getReds(data[0]);
			icm.getGreens(data[1]);
			icm.getBlues(data[2]);
			LookupTableJAI lut = new LookupTableJAI(data);
			ParameterBlock pbWork = new ParameterBlock();
			pbWork.addSource(piOut);
			pbWork.add(lut);
			piOut = JAI.create("lookup", pbWork, null); // RGB image

			// pbJAI = new ParameterBlockJAI("bandmerge");
			// for(int n = 0; n < 3; n++){
			// pbJAI.setSource(piOut, n);
			// }
			// piOut = JAI.create("bandmerge", pbJAI, null);

			double[][] m = { { 0.114, 0.587, 0.299, 0 } };
			pbWork = new ParameterBlock();
			pbWork.addSource(piOut);
			pbWork.add(m);
			piOut = JAI.create("bandcombine", pbWork, null);
		}
//		if (output == 3) { // RGB to grey conversion
//
//			double[][] m = { { 0.333, 0.333, 0.333, 0 } };
//			ParameterBlock pbBC = new ParameterBlock();
//			pbBC.addSource(piOut);
//			pbBC.add(m);
//			piOut = JAI.create("bandcombine", pbBC, null);
//		}

		fireProgressChanged(95);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		ImageModel im = new ImageModel(piOut);
		im.setModelName(imgName);
		im.setFileName(fileName);
		
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpWatershedDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpWatershedDescriptor.TYPE;
	}
}
