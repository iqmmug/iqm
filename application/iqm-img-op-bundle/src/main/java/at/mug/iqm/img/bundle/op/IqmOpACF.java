package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpACF.java
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


import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.DFTDescriptor;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.img.bundle.descriptors.IqmOpACFDescriptor;

/**
 * This is the main image processing class There exist two approaches: A user
 * defined JAI operator is just called or: The actual processing is implemented
 * in this class
 * 
 * @author Ahammer, Kainz
 * @since 2012 11 This class calculates the Autocorrelation function ACF of an
 *       image using DFT FFT(ACF) = FFT(image) * (complex conjugate (FFT(image))
 */
public class IqmOpACF extends AbstractOperator {

	public IqmOpACF() {
	}

	/**
	 * This method calculates the imaginary part of a DFT transformed image
	 * 
	 * @param pi
	 *            (DFT of an image)
	 * @return pi
	 */
	@SuppressWarnings("unused")
	private PlanarImage calcImaginaryImage(PlanarImage pi) {
		// pi = JAI.create("bandselect", pi, new int[] {0}); //zu hell in der
		// Darstellung!!!
		double[][] matrix = null;
		if (pi.getNumBands() == 2) { // Grey
			matrix = new double[][] { { 0.0d, 1.0d, 0.0d } };
			pi = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
		}
		if (pi.getNumBands() == 6) { // RGB
			matrix = new double[][] { { 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d,0.0d } };
			PlanarImage pi1 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
			matrix = new double[][] { { 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d,0.0d } };
			PlanarImage pi2 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
			matrix = new double[][] { { 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d,0.0d } };
			PlanarImage pi3 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
			ParameterBlock pb = new ParameterBlock();
			pb.setSource(pi1, 0);
			pb.setSource(pi2, 1);
			pb.setSource(pi3, 2);
			pi = JAI.create("bandmerge", pb, null);
		}
		return pi;
	}

	/**
	 * This method calculates the real part of a DFT transformed image
	 * 
	 * @param pi
	 *            (DFT of an image)
	 * @return pi
	 */
	@SuppressWarnings("unused")
	private PlanarImage calcRealImage(PlanarImage pi) {
		// pi = JAI.create("bandselect", pi, new int[] {0}); //too bright in display!
		double[][] matrix = null;
		System.out.println("Number of Bands: " + pi.getNumBands());
		if (pi.getNumBands() == 2) {
			matrix = new double[][] { { 1.0d, 0.0d, 0.0d } };
			pi = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
		}
		if (pi.getNumBands() == 6) { // RGB
			matrix = new double[][] { { 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d,0.0d } };
			PlanarImage pi1 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
			matrix = new double[][] { { 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d,0.0d } };
			PlanarImage pi2 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
			matrix = new double[][] { { 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d,0.0d } };
			PlanarImage pi3 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
			ParameterBlock pb = new ParameterBlock();
			pb.setSource(pi1, 0);
			pb.setSource(pi2, 1);
			pb.setSource(pi3, 2);
			pi = JAI.create("bandmerge", pb, null);
		}
		return pi;
	}

	@Override
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		IqmDataBox box = (IqmDataBox) pb.getSource(0);
		PlanarImage pi = (PlanarImage) box.getImage();

		// check whether or not the operator has been cancelled
		if (isCancelled(getParentTask())) return null;

		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		RenderingHints rh = null;
		ParameterBlock pbWork = new ParameterBlock();
		pbWork.addSource(pi);	
		this.fireProgressChanged(1);

		// get FFT of image
		pbWork.add(DFTDescriptor.SCALING_NONE);
		// pb.add(DFTDescriptor.SCALING_UNITARY);
		// pb.add(DFTDescriptor.SCALING_DIMENSIONS);
		pbWork.add(DFTDescriptor.REAL_TO_COMPLEX);
		PlanarImage piDFT = JAI.create("DFT", pbWork, rh);
		this.fireProgressChanged(5);
		if (isCancelled(getParentTask())) return null;

		// construct complex conjugate of piDFT
		pbWork = new ParameterBlock();
		pbWork.addSource(piDFT);
		PlanarImage piDFTConjugate = JAI.create("Conjugate", pbWork, rh);
		this.fireProgressChanged(10);
		if (isCancelled(getParentTask())) return null;

		// multiply FFT times complex conjugate of FFT
		pbWork = new ParameterBlock();
		pbWork.addSource(piDFT);
		pbWork.addSource(piDFTConjugate);
		PlanarImage piDFTOfACF = JAI.create("Multiply", pbWork, rh);
		this.fireProgressChanged(15);
		if (isCancelled(getParentTask())) return null;

		// get inverse FFT
		pbWork = new ParameterBlock();
		pbWork.addSource(piDFTOfACF);
		// pb.add(DFTDescriptor.SCALING_NONE);
		// pb.add(DFTDescriptor.SCALING_UNITARY);
		pbWork.add(DFTDescriptor.SCALING_DIMENSIONS);
		pbWork.add(DFTDescriptor.COMPLEX_TO_REAL);
		PlanarImage piACF = JAI.create("IDFT", pbWork, rh);
		this.fireProgressChanged(20);
		if (isCancelled(getParentTask())) return null;

		// //Reconstruct--------------------------------------------------------------

		// Periodic shift: corners are shifted to the center of the
		// image----------
		pbWork = new ParameterBlock();
		pbWork.addSource(piACF);
		// pb.add(shiftX); //default: Width/2
		// pb.add(shiftY); //default: Height/2
		piACF = JAI.create("PeriodicShift", pbWork);
		this.fireProgressChanged(25);
		if (isCancelled(getParentTask())) return null;

		// //Normalization of result
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		pbWork = new ParameterBlock();
		pbWork.addSource(piACF);
		RenderedOp extrema = JAI.create("extrema", pbWork);
		this.fireProgressChanged(30);
		if (isCancelled(getParentTask())) return null;

		double[] allMins = (double[]) extrema.getProperty("minimum");
		double[] allMaxs = (double[]) extrema.getProperty("maximum");
		
		for (int i = 0; i < allMins.length; i++) {
			min = Math.min(allMins[i], min);
		}
		for (int i = 0; i < allMaxs.length; i++) {
			max = Math.max(allMaxs[i], max);
		}
		pbWork = new ParameterBlock();
		pbWork.addSource(piACF);
		pbWork.add(new double[] { 255d / (max - min), 255d / (max - min),
				255d / (max - min) });
		pbWork.add(new double[] { (255 * min) / (min - max),
				(255 * min) / (min - max), (255 * min) / (min - max) });
		piACF = JAI.create("rescale", pbWork);
		this.fireProgressChanged(80);
		if (isCancelled(getParentTask())) return null;
		//
		//
		// //Change float to byte
		pbWork = new ParameterBlock();
		pbWork.addSource(piACF);
		pbWork.add(DataBuffer.TYPE_BYTE);
		piACF = JAI.create("format", pbWork);
		this.fireProgressChanged(90);

		// //Crop to original size for image size not 2^b
		// pb = new ParameterBlock();
		// pb.addSource(piACF);
		// pb.add(0f);
		// pb.add(0f);
		// pb.add((float)pi.getWidth());
		// pb.add((float)pi.getHeight());
		// piACF = JAI.create("Crop", pb);
		// //--------------------------------------------------------------------------
	
		ImageModel im = new ImageModel(piACF);
		im.setFileName (String.valueOf(pi.getProperty("file_name")));
		im.setModelName(String.valueOf(pi.getProperty("image_name")));
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpACFDescriptor().getName();
		}
		return this.name;
	}

	public OperatorType getType() {
		if (this.type == null) {
			this.type = IqmOpACFDescriptor.TYPE;
		}
		return this.type;
	}

}
