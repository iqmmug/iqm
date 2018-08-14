package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpDFT.java
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

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpDFTDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 11
 */
public class IqmOpDFT extends AbstractOperator {

	public IqmOpDFT() {
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
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));
		
		int scale = pb.getIntParameter(0);
		int resultOptions = pb.getIntParameter(1);

		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		RenderingHints rh = null;
		ParameterBlock pbWork = new ParameterBlock();
		pbWork.addSource(pi);

		if (scale == 0) { // none
			pbWork.add(DFTDescriptor.SCALING_NONE);
		}
		if (scale == 1) { // unitary
			pbWork.add(DFTDescriptor.SCALING_UNITARY);
		}
		if (scale == 2) { // dimensions
			pbWork.add(DFTDescriptor.SCALING_DIMENSIONS);
		}
		pbWork.add(DFTDescriptor.REAL_TO_COMPLEX);
	
		fireProgressChanged(20);
		if (isCancelled(getParentTask())){
			return null;
		}
		piOut = JAI.create("DFT", pbWork, rh);

		fireProgressChanged(40);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		// PlanarImage mag = null;
		// PlanarImage phase = null;
		// --------------------------------------------------------------------
		if (resultOptions == 0) { // Power
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			piOut = JAI.create("MagnitudeSquared", pbWork);
		}
		if (resultOptions == 1) { // Magnitude
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			piOut = JAI.create("Magnitude", pbWork);
		}

		if (resultOptions == 2) { // Phase
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			piOut = JAI.create("Phase", pbWork);
		}
		if (resultOptions == 3) { // Real
			// pi = JAI.create("bandselect", pi, new int[] {0}); //zu hell in
			// der Darstellung!!!
			double[][] matrix = null;
			System.out.println("Number of Bands: " + piOut.getNumBands());
			if (piOut.getNumBands() == 2) {
				matrix = new double[][] { { 1.0d, 0.0d, 0.0d } };
				piOut = JAI.create("bandcombine", new ParameterBlock()
						.addSource(piOut).add(matrix), null);
			}
			if (piOut.getNumBands() == 6) { // RGB
				matrix = new double[][] { { 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d,
						0.0d } };
				PlanarImage pi1 = JAI
						.create("bandcombine",
								new ParameterBlock().addSource(piOut).add(
										matrix), null);
				matrix = new double[][] { { 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d,
						0.0d } };
				PlanarImage pi2 = JAI
						.create("bandcombine",
								new ParameterBlock().addSource(piOut).add(
										matrix), null);
				matrix = new double[][] { { 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d,
						0.0d } };
				PlanarImage pi3 = JAI
						.create("bandcombine",
								new ParameterBlock().addSource(piOut).add(
										matrix), null);
				pbWork.removeSources();
				pbWork.removeParameters();
				pbWork.setSource(pi1, 0);
				pbWork.setSource(pi2, 1);
				pbWork.setSource(pi3, 2);
				piOut = JAI.create("bandmerge", pbWork, null);
			}

		}
		if (resultOptions == 4) { // Imaginary part
			// pi = JAI.create("bandselect", pi, new int[] {0}); //zu hell in
			// der Darstellung!!!
			double[][] matrix = null;
			if (piOut.getNumBands() == 2) { // Grey
				matrix = new double[][] { { 0.0d, 1.0d, 0.0d } };
				piOut = JAI.create("bandcombine", new ParameterBlock()
						.addSource(piOut).add(matrix), null);
			}
			if (piOut.getNumBands() == 6) { // RGB
				matrix = new double[][] { { 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d,
						0.0d } };
				PlanarImage pi1 = JAI
						.create("bandcombine",
								new ParameterBlock().addSource(piOut).add(
										matrix), null);
				matrix = new double[][] { { 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d,
						0.0d } };
				PlanarImage pi2 = JAI
						.create("bandcombine",
								new ParameterBlock().addSource(piOut).add(
										matrix), null);
				matrix = new double[][] { { 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d,
						0.0d } };
				PlanarImage pi3 = JAI
						.create("bandcombine",
								new ParameterBlock().addSource(piOut).add(
										matrix), null);
				pbWork.removeSources();
				pbWork.removeParameters();
				pbWork.setSource(pi1, 0);
				pbWork.setSource(pi2, 1);
				pbWork.setSource(pi3, 2);
				piOut = JAI.create("bandmerge", pbWork, null);
			}
		}
		
		fireProgressChanged(60);
		if (isCancelled(getParentTask())){
			return null;
		}

		// Periodic shift: corners are shifted to the center of the
		// image----------
		pbWork.removeSources();
		pbWork.removeParameters();
		pbWork.addSource(piOut);
		// pb.add(shiftX); //default: Width/2
		// pb.add(shiftY); //default: Height/2
		piOut = JAI.create("PeriodicShift", pbWork);

		fireProgressChanged(85);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		// //Normalization of result
		// //double min = 0d;
		// double max = 0d;
		// pb.removeSources();
		// pb.removeParameters();
		// pb.addSource(piOut);
		// RenderedOp extrema = JAI.create("extrema", pb);
		// //double[] allMins = (double[])extrema.getProperty("minimum");
		// double[] allMaxs = (double[])extrema.getProperty("maximum");
		// for (int i = 0; i < allMaxs.length; i++){
		// max = Math.max(allMaxs[i], max);
		// }
		// pb.removeSources();
		// pb.removeParameters();
		// pb.addSource(piOut);
		// pb.add(new double[]{255d/max});
		// piOut = JAI.create("rescale", pb);

		// //Change float to
		// byte----------------------------------------------------
		// pb.removeSources();
		// pb.removeParameters();
		// pb.addSource(piOut);
		// pb.add(DataBuffer.TYPE_BYTE);
		// piOut = JAI.create("format", pb);

		// //Reconstruct--------------------------------------------------------------
		// pb.removeSources();
		// pb.removeParameters();
		// pb.addSource(mag);
		// pb.addSource(phase);
		// pb.addSource(IDFTDescriptor.SCALING_DIMENSIONS);
		// pb.addSource(IDFTDescriptor.COMPLEX_TO_REAL);
		// PlanarImage ptc = JAI.create("PolarToComplex", pb);
		// pb.removeSources();
		// pb.removeParameters();
		// pb.addSource(ptc);
		// piOut = JAI.create("IDFT", pb);
		//
		// //Change float to byte
		// pb.removeSources();
		// pb.removeParameters();
		// pb.addSource(piOut);
		// pb.add(DataBuffer.TYPE_BYTE);
		// piOut = JAI.create("format", pb);
		//
		// //Crop to original size
		// pb.removeSources();
		// pb.removeParameters();
		// pb.addSource(piOut);
		// pb.add(0f);
		// pb.add(0f);
		// pb.add((float)pi.getWidth());
		// pb.add((float)pi.getHeight());
		// piOut = JAI.create("Crop", pb);
		// //--------------------------------------------------------------------------

		// LookJDFTFrame f = new LookJDFTFrame();
		// f.createAndShowGUI();
		// f.setVisible(true);
		// f.setImage((PlanarImage)piOut);
		
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
			this.name = new IqmOpDFTDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpDFTDescriptor.TYPE;
	}
}
