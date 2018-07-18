package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracSurrogate.java
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


import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Random;
import java.util.Vector;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;
import javax.media.jai.operator.DFTDescriptor;
import javax.media.jai.operator.IDFTDescriptor;

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
import at.mug.iqm.img.bundle.descriptors.IqmOpFracSurrogateDescriptor;

/**
 * This class calculates surrogate images according to
 * see e.g. Mark Shelhammer, Nonlinear Dynamics in Physiology, World Scientific 2007
 * 
 * @author Ahammer, Kainz
 * @since   2012 11
 */
public class IqmOpFracSurrogate extends AbstractOperator {

	public IqmOpFracSurrogate() {
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
			matrix = new double[][] { { 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d } };
			PlanarImage pi1 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
			matrix = new double[][] { { 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d } };
			PlanarImage pi2 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
			matrix = new double[][] { { 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d } };
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
		// pi = JAI.create("bandselect", pi, new int[] {0}); //zu hell in der
		// Darstellung!!!
		double[][] matrix = null;
		System.out.println("Number of Bands: " + pi.getNumBands());
		if (pi.getNumBands() == 2) {
			matrix = new double[][] { { 1.0d, 0.0d, 0.0d } };
			pi = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
		}
		if (pi.getNumBands() == 6) { // RGB
			matrix = new double[][] { { 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d } };
			PlanarImage pi1 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
			matrix = new double[][] { { 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d } };
			PlanarImage pi2 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(matrix), null);
			matrix = new double[][] { { 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d } };
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
	public IResult run(IWorkPackage wp) throws Exception {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}
		PlanarImage piOut = null;

		IqmDataBox box = (IqmDataBox) wp.getParameters().getSource(0);

		PlanarImage pi = box.getImage();
		int method = pb.getIntParameter(0);

		// Get extrema for normalization of the output image
		double minPi = 255d;
		double maxPi = 0d;
		ParameterBlock pbTmp = new ParameterBlock();
		pbTmp.addSource(pi);
		RenderedOp extrema = JAI.create("extrema", pbTmp);
		double[] allMins = (double[]) extrema.getProperty("minimum");
		double[] allMaxs = (double[]) extrema.getProperty("maximum");
		for (int i = 0; i < allMins.length; i++) {
			minPi = Math.min(allMins[i], minPi);
		}
		for (int i = 0; i < allMaxs.length; i++) {
			maxPi = Math.max(allMaxs[i], maxPi);
		}

		this.fireProgressChanged(10);
		if (isCancelled(getParentTask())) return null;
		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		RenderingHints rh = null;
		pbTmp = new ParameterBlock();
		// --------------------------------------------------------------------
		if (method == 0) { // Shuffle
			// System.out.println("Method 0 Shuffle");
			Raster rasterPi = pi.getData();
			TiledImage tiShuffle = new TiledImage(pi, false);
			Vector<Vector<Integer>> vecVecPi = new Vector<Vector<Integer>>();

			// int[] intArray = null;
			int value = 0;

			for (int b = 0; b < pi.getNumBands(); b++) {
				Vector<Integer> vecBand = new Vector<Integer>();
				for (int x = 0; x < rasterPi.getWidth(); x++) {
					
					if (isCancelled(getParentTask())) return null;
					
					for (int y = 0; y < rasterPi.getHeight(); y++) {
						value = rasterPi.getSample(x, y, b);
						vecBand.add(value);
						// for (int ii = 0; ii < vecBand.size(); ii++) {
						// System.out.println("ii: " +ii+
						// "  vecPi.get(ii): " + vecBand.get(ii));
						// }
						// System.out.println("vecPi.lastElement: " +
						// vecBand.lastElement());
						// System.out.println("vecPi.size(): " +
						// vecBand.size());
					}
				}
				vecVecPi.add(vecBand);
			}
		
			this.fireProgressChanged(10);
			if (isCancelled(getParentTask())) return null;

			Random generator = new Random();
			for (int b = 0; b < pi.getNumBands(); b++) {
				Vector<Integer> vecBand = vecVecPi.get(b);
				int i = 0;
				int numb = vecBand.size();
				while (vecBand.size() > 0) {
					int index = generator.nextInt(vecBand.size());
					int y = i / tiShuffle.getWidth();
					int x = i - (y * tiShuffle.getWidth());
					value = vecBand.get(index);
					tiShuffle.setSample(x, y, b, value);
					vecBand.removeElementAt(index);
					i = i + 1;
					int proz = (int) (i + 1) * 80 / numb/pi.getNumBands() + (b*80)/pi.getNumBands() + 10;
					fireProgressChanged(proz);
					if (isCancelled(getParentTask())) return null;
				}
			}		
			this.fireProgressChanged(90);	
			piOut = tiShuffle;

			// //Change float to byte----------------------------------------------------
			// pb = new ParameterBlock();;
			// pb.addSource(piOut);
			// pb.add(DataBuffer.TYPE_BYTE);
			// piOut = JAI.create("format", pb);
		}
		if (method == 1) { // Gaussian
			double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

			pbTmp = new ParameterBlock();
			pbTmp.addSource(pi);
			pbTmp.add(null); // ROI
			pbTmp.add(1);
			pbTmp.add(1); // Sampling
			pbTmp.add(new int[] { (int) typeGreyMax + 1 }); // Bins
			pbTmp.add(new double[] { 0.0d });
			pbTmp.add(new double[] { typeGreyMax + 1 }); // Range for inclusion
			// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
			// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
			RenderedOp rOp = JAI.create("Histogram", pbTmp, null);
			Histogram histo = (Histogram) rOp.getProperty("histogram");
			double[] mean = histo.getMean();
			double[] stdDev = histo.getStandardDeviation();
			fireProgressChanged(10);
			if (isCancelled(getParentTask()))return null;

			Random generator = new Random();
			TiledImage tiGauss = new TiledImage(pi, false);
			for (int b = 0; b < pi.getNumBands(); b++) {
			for (int x = 0; x < tiGauss.getWidth(); x++) {
				int proz = (int) (x + 1) * 80 / tiGauss.getWidth()/pi.getNumBands() + (b*80)/pi.getNumBands() + 10;
				fireProgressChanged(proz);
				if (isCancelled(getParentTask()))return null;
					
				for (int y = 0; y < tiGauss.getHeight(); y++) {
				
						tiGauss.setSample(x, y, b, generator.nextGaussian() * stdDev[b] + mean[b]);
					}
				}
			}
			fireProgressChanged(90);
			piOut = tiGauss;

		}

		if (method == 2) { // Random Phase
			pbTmp = new ParameterBlock();
			pbTmp.addSource(pi);
			pbTmp.add(DFTDescriptor.SCALING_NONE);
			// pb.add(DFTDescriptor.SCALING_UNITARY);
			// pb.add(DFTDescriptor.SCALING_DIMENSIONS);
			pbTmp.add(DFTDescriptor.REAL_TO_COMPLEX);
			PlanarImage piDFT = JAI.create("DFT", pbTmp, rh);

			pbTmp = new ParameterBlock();
			pbTmp.addSource(piDFT);
			PlanarImage piMag = JAI.create("Magnitude", pbTmp);

			pbTmp = new ParameterBlock();
			pbTmp.addSource(piDFT);
			PlanarImage piPhase = JAI.create("Phase", pbTmp); // Possible phase values: [-pi,...,0,.....+pi]
			
			fireProgressChanged(10);
			if (isCancelled(getParentTask()))return null;

			// //Get extrema of phase values
			// double minPhase = Double.MAX_VALUE;
			// double maxPhase = -Double.MIN_VALUE;
			// pb = new ParameterBlock();
			// pb.addSource(piPhase);
			// extrema = JAI.create("extrema", pb);
			// allMins = (double[])extrema.getProperty("minimum");
			// allMaxs = (double[])extrema.getProperty("maximum");
			// for (int i = 0; i < allMins.length; i++){
			// minPhase = Math.min(allMins[i], minPhase);
			// }
			// for (int i = 0; i < allMaxs.length; i++){
			// maxPhase = Math.max(allMaxs[i], maxPhase);
			// }
			// System.out.println("minPhase: " + minPhase+ "   maxPhase: " +
			// maxPhase);

			// //Randomize phase by shuffling the phases (slow)
			// Raster raster = piPhase.getData();
			// TiledImage tiPhase = new TiledImage(piPhase, false);
			// Vector<Vector<Float>> vecPhase = new Vector<Vector<Float>>();
			// for (int b = 0; b < piPhase.getNumBands(); b++){
			// Vector<Float> vecBand = new Vector<Float>();
			// for (int x =0; x < piPhase.getWidth(); x++){
			// for (int y =0; y < piPhase.getHeight(); y++){
			// vecBand.add(raster.getSampleFloat(x, y, b));
			// }
			// }
			// vecPhase.add(vecBand);
			// }
			// Random generator = new Random();
			// for (int b = 0; b < piPhase.getNumBands(); b++){
			// Vector<Float> vecBand = vecPhase.get(b);
			// int i = 0;
			// while (vecBand.size() > 0){
			// int index = generator.nextInt(vecBand.size());
			// int y = i /tiPhase.getWidth();
			// int x = i -(y*tiPhase.getWidth());
			// //System.out.println("b: " + b+ "   x: " + x+ "  y: "+y);
			// tiPhase.setSample(x, y, b, vecBand.get(index));
			// vecBand.removeElementAt(index);
			// i = i+1;
			// }
			//
			// }
			// Randomize phase by randomly creating phases in the range[-pi,pi]
			// (fast)
			TiledImage tiPhase = new TiledImage(piPhase, false);
			Random generator = new Random();
			for (int x = 0; x < tiPhase.getWidth(); x++) {
					int proz = ((int) (x + 1) * 40 / tiPhase.getWidth()) + 10;
					fireProgressChanged(proz);
					
					if (isCancelled(getParentTask())){
						return null;
					}
					
				for (int y = 0; y < tiPhase.getHeight(); y++) {
						double phase = generator.nextDouble(); // [0,1
						phase = phase * 2 * Math.PI - Math.PI;
						// System.out.println("phase: " + phase);
					for (int b = 0; b < tiPhase.getNumBands(); b++) {
							tiPhase.setSample(x, y, b, phase);
					}
				}
			}
		

			// Reconstruct--------------------------------------------------------------
			pbTmp = new ParameterBlock();
			pbTmp.addSource(piMag);
			pbTmp.addSource(tiPhase);
			pbTmp.add(DFTDescriptor.SCALING_NONE);
			// pb.addSource(IDFTDescriptor.SCALING_DIMENSIONS);
			pbTmp.addSource(IDFTDescriptor.COMPLEX_TO_REAL);
			PlanarImage ptc = JAI.create("PolarToComplex", pbTmp);
			fireProgressChanged(50);
			if (isCancelled(getParentTask()))return null;


			pbTmp = new ParameterBlock();
			pbTmp.addSource(ptc);
			pbTmp.addSource(IDFTDescriptor.SCALING_DIMENSIONS);
			pbTmp.addSource(IDFTDescriptor.COMPLEX_TO_REAL);
			piOut = JAI.create("IDFT", pbTmp);
			fireProgressChanged(55);
			if (isCancelled(getParentTask()))return null;

			// Periodic shift: corners are shifted to the center of the
			// image----------
			// pb = new ParameterBlock();
			// pb.addSource(piOut);
			// //pb.add(shiftX); //default: Width/2
			// //pb.add(shiftY); //default: Height/2
			// piOut = JAI.create("PeriodicShift", pb);

			// //Normalize to original grey value range
			double min = Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;
			pbTmp = new ParameterBlock();
			pbTmp.addSource(piOut);
			extrema = JAI.create("extrema", pbTmp);
			allMins = (double[]) extrema.getProperty("minimum");
			allMaxs = (double[]) extrema.getProperty("maximum");
			fireProgressChanged(80);
			if (isCancelled(getParentTask()))return null;
			for (int i = 0; i < allMins.length; i++) {
				min = Math.min(allMins[i], min);
			}
			for (int i = 0; i < allMaxs.length; i++) {
				max = Math.max(allMaxs[i], max);
			}
			pbTmp = new ParameterBlock();
			pbTmp.addSource(piOut);
			pbTmp.add(new double[] { (maxPi - minPi) / (max - min),
					(maxPi - minPi) / (max - min),
					(maxPi - minPi) / (max - min) });
			pbTmp.add(new double[] {
					((maxPi - minPi) * min) / (min - max) + minPi,
					((maxPi - minPi) * min) / (min - max) + minPi,
					((maxPi - minPi) * min) / (min - max) + minPi });
			// System.out.println("min: " + min+ "   max: " + max+ "  minPi: "+
			// minPi + "   maxPi: " + maxPi);
			piOut = JAI.create("rescale", pbTmp);
			fireProgressChanged(85);
			if (isCancelled(getParentTask()))return null;

			// crop to original size
			// pb = new ParameterBlock();
			// pb.addSource(piOut);
			// pb.add(0f);
			// pb.add(0f);
			// pb.add((float)pi.getWidth());
			// pb.add((float)pi.getHeight());
			// piOut = JAI.create("Crop", pb);

			// Change float to
			// byte----------------------------------------------------
			pbTmp = new ParameterBlock();
			
			pbTmp.addSource(piOut);
			pbTmp.add(DataBuffer.TYPE_BYTE);
			piOut = JAI.create("format", pbTmp);
			fireProgressChanged(90);
		
		}
		if (method == 3) { // AAFT
			piOut = pi;

		}
		if (method == 4) { // notYetImplemented
			piOut = pi;
		}

		if (isCancelled(getParentTask())){
			return null;
		}
		
		ImageModel im = new ImageModel(piOut);
		im.setModelName(String.valueOf(pi.getProperty("image_name")));
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpFracSurrogateDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpFracSurrogateDescriptor.TYPE;
	}

}
