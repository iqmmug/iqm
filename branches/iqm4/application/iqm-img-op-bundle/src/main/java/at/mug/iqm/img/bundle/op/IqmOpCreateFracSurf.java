package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCreateFracSurf.java
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


import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Random;

import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;
import javax.media.jai.operator.IDFTDescriptor;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.img.bundle.descriptors.IqmOpCreateFracSurfDescriptor;

/**
 * The implemented method uses IFR Inverse Fourier Reconstruction  with 1/(f^(b/2)), filtering and b = (8-2D)
 * see book: Turner, Blackledge, Andrews, Fractal Geometry in Digital imaging, S.172
 * see also Anguiano etal. J. Microscopy 1993, Vol 172, 223- 232 (FFT Dimension etc.)
 * and book Voss R.F. 1985
 * 
 * <li>2011 11 added option: image creation by sum of sin functions
 * Nottale L. Scale Relativity and Fractal Space-Time 2011 pp130-135. not ready yet!!!!!!!
 *                             
 * <li>2016 01 added option: Michael Mayrhofer-Reinhartshuber Midpoint Displacement Method
 * as published by Zhou and Lam, Computers and Geosciences 31 (2005), 1260-1269
 * http://www.cescg.org/CESCG97/marak/node3.html#
 *                                                            
 * @author Helmut Ahammer, Kainz, Mayrhofer-Reinhartshuber
 * @since  2010 01
 * @update 2014 11 RGB output
 */
public class IqmOpCreateFracSurf extends AbstractOperator {

	public IqmOpCreateFracSurf() {
		this.setCancelable(true);
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

		// PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		int   width  = pb.getIntParameter("Width");
		int   height = pb.getIntParameter("Height");
		int   method = pb.getIntParameter("Method"); // 0 FFT; 1 Sum of Sin
		float fracD  = pb.getFloatParameter("FracD");
		int   outbit = pb.getIntParameter("OutBit");

		//System.out.println("IqmOpCreateFracSurf: outbit" + outbit);
		int greyValueMax = 0;
		if (outbit == 0) { // 8bit
			greyValueMax = 255;
			System.out.println("IqmOpCreateFracSurf: 8-bit output image will be calculated");
		}
		if (outbit == 1) { // 16bit
			greyValueMax = 65535;
			System.out.println("IqmOpCreateFracSurf: 16-bit output image will be calculated");
		}
		if (outbit == 2) { // RGB
			greyValueMax = 255;
			System.out.println("IqmOpCreateFracSurf: RGB output image will be calculated");
		}
		if (method == IqmOpCreateFracSurfDescriptor.METHOD_FFT) { // FFT
			if ((outbit == 0)||(outbit == 1)){ //8bit 16bit
				piOut = this.calcFFTSurface(width, height, greyValueMax, fracD);
			}
			if (outbit == 2){ //RGB 
				ParameterBlockJAI pbBM = new ParameterBlockJAI("bandmerge");
				for (int b = 0; b < 3; b++) {
					pbBM.setSource(this.calcFFTSurface(width, height, greyValueMax, fracD), b);
				}
				piOut = JAI.create("bandmerge", pbBM, null);
			}
		} 
		if (method == IqmOpCreateFracSurfDescriptor.METHOD_MIDPOINTDISPLACEMENT) { // Midpoint Displacement
			if ((outbit == 0)||(outbit == 1)){ //8bit 16bit
				piOut = this.calcMPDSurface(width, height, greyValueMax, fracD);
			}
			if (outbit == 2){ //RGB 
				ParameterBlockJAI pbBM = new ParameterBlockJAI("bandmerge");
				for (int b = 0; b < 3; b++) {
					pbBM.setSource(this.calcMPDSurface(width, height, greyValueMax, fracD), b);
				}
				piOut = JAI.create("bandmerge", pbBM, null);
			}
		} 
		if (method == IqmOpCreateFracSurfDescriptor.METHOD_SUMOFSINE) { // Sum of sin functions
			if ((outbit == 0)||(outbit == 1)){
				piOut = this.calcSumSinesSurface(width, height, greyValueMax, fracD);
			}
			if (outbit == 2){ //RGB 
				ParameterBlockJAI pbBM = new ParameterBlockJAI("bandmerge");
				for (int b = 0; b < 3; b++) {
					pbBM.setSource(this.calcSumSinesSurface(width, height, greyValueMax, fracD), b);
				}
				piOut = JAI.create("bandmerge", pbBM, null);
			}
		}

		// //Change float to
		// byte----------------------------------------------------
		ParameterBlock pbFormat = new ParameterBlock();
		pbFormat.addSource(piOut);
		if (outbit == 0) pbFormat.add(DataBuffer.TYPE_BYTE);
		if (outbit == 1) pbFormat.add(DataBuffer.TYPE_USHORT);
		if (outbit == 2) pbFormat.add(DataBuffer.TYPE_BYTE);

		piOut = JAI.create("format", pbFormat);

		// piOut = piN;
		// System.out.println("IqmCreateFracSurf piOut.getClass()" +
		// piOut.getClass());
		

		ImageModel im = new ImageModel(piOut);
		if (method == IqmOpCreateFracSurfDescriptor.METHOD_FFT)                  im.setModelName("FFT FD=" + String.valueOf(fracD));
		if (method == IqmOpCreateFracSurfDescriptor.METHOD_MIDPOINTDISPLACEMENT) im.setModelName("MPD FD=" + String.valueOf(fracD));
		if (method == IqmOpCreateFracSurfDescriptor.METHOD_SUMOFSINE)            im.setModelName("SumOfSine");
		im.setFileName("IQM generated image");
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpCreateFracSurfDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpCreateFracSurfDescriptor.TYPE;
	}
	//--------------------------------------------------------------------------------------------------
	/**
	 * This methods calculates a fractal surface using FFT
	 */
	private PlanarImage calcFFTSurface(int width, int height, int greyValueMax, float fracD){
		PlanarImage piOut = null;
		// DFT image imgWidth and imgHeight is only possible in 2^n steps.
		int dftWidth = 1;
		int dftHeight = 1;
		while (dftWidth < width)   dftWidth *= 2;
		while (dftHeight < height) dftHeight *= 2;
		if (dftWidth > dftHeight)  dftHeight = dftWidth;
		if (dftWidth < dftHeight)  dftWidth = dftHeight;

		// create real and imaginary part
		// SampleModel smN = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_FLOAT, dftWidth, dftHeight, 1); //1 banded sample model
		// SampleModel smM = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_FLOAT, dftWidth, dftHeight, 1); //1 banded sample model.createBandedSampleModel can yield a too bright display
		SampleModel smN = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT, dftWidth, dftHeight, 1); // 1 banded sample model
		SampleModel smM = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT, dftWidth, dftHeight, 1); // 1 banded sample model
		float[] floatArrN = new float[dftWidth * dftHeight];
		float[] floatArrM = new float[dftWidth * dftHeight];
		DataBufferFloat dbN = new DataBufferFloat(floatArrN, dftWidth * dftHeight);
		DataBufferFloat dbM = new DataBufferFloat(floatArrM, dftWidth * dftHeight);
		WritableRaster wrN = RasterFactory.createWritableRaster(smN, dbN, new Point(0, 0));
		WritableRaster wrM = RasterFactory.createWritableRaster(smM, dbM, new Point(0, 0));

		// generate random pixel values
		Random generator = new Random();
		// Random generator2 = new Random();
		float b = 8.0f - (2.0f * fracD);
		for (int k1 = 0; k1 < dftWidth; k1++) {
			int proz = (k1 + 1) * 100 / dftWidth;
			this.fireProgressChanged(proz);

			// ##########################################################################
			// this is the operator-side implementation of the cancellation routine
			// copy this to each implementing class of at.mug.iqm.api.Operator or
			// its subclasses to enable cancellation of the tasks
			if (isCancelled(getParentTask())) {
				// System.out.println("The parent task has been cancelled.");
				return null;
			}
			// ##########################################################################

			for (int k2 = 0; k2 < dftHeight; k2++) {
				float k = (float) Math.sqrt(Math.pow(
						Math.abs(k1 - dftWidth / 2) + 1, 2)
						+ Math.pow(Math.abs(k2 - dftHeight / 2) + 1, 2));
				// k[1,.......]
				// if (k == 0.0){
				// k = Float.MIN_VALUE;
				// System.out.println("k = Float.MIN_VALUE: " + k);
				// }
				// k = k/dftWidth; //not necessary
				double g = generator.nextGaussian();
				double u = generator.nextFloat();
				double n = g * Math.cos(2 * Math.PI * u);
				double m = g * Math.sin(2 * Math.PI * u);
				n = n * Math.pow(k, -b / 2);
				m = m * Math.pow(k, -b / 2);
				wrN.setSample(k1, k2, 0, (float) n);
				wrM.setSample(k1, k2, 0, (float) m);
			}
		}
		// create tiled images
		ColorModel cmN = PlanarImage.createColorModel(smN); // compatible color model
		ColorModel cmM = PlanarImage.createColorModel(smM); // compatible  color model
		TiledImage tiN = new TiledImage(0, 0, dftWidth, dftHeight, 0, 0, smN, cmN);
		TiledImage tiM = new TiledImage(0, 0, dftWidth, dftHeight, 0, 0, smM, cmM);
		tiN.setData(wrN);
		tiM.setData(wrM);

		ParameterBlock pbTmp = new ParameterBlock();
		// Periodic shift: center is shifted to the corners of the
		// image----------
		pbTmp.removeSources();
		pbTmp.removeParameters();
		pbTmp.addSource(tiN);
		// pb.add(shiftX); //default: Width/2
		// pb.add(shiftY); //default: Height/2
		PlanarImage piN = JAI.create("PeriodicShift", pbTmp);

		pbTmp.removeSources();
		pbTmp.removeParameters();
		pbTmp.addSource(tiM);
		// pb.add(shiftX); //default: Width/2
		// pb.add(shiftY); //default: Height/2
		PlanarImage piM = JAI.create("PeriodicShift", pbTmp);

		// create IDFT image

		// Reconstruct--------------------------------------------------------------
		pbTmp.removeSources();
		pbTmp.removeParameters();
		pbTmp.setSource(piN, 0);
		pbTmp.setSource(piM, 1);
		PlanarImage piDftNew = JAI.create("bandmerge", pbTmp, null);

		pbTmp.removeSources();
		pbTmp.removeParameters();
		pbTmp.addSource(piDftNew);
		pbTmp.add(IDFTDescriptor.SCALING_UNITARY);
		pbTmp.add(IDFTDescriptor.COMPLEX_TO_REAL);
		piOut = JAI.create("IDFT", pbTmp);

		// Normalize grey
		// values---------------------------------------------------
		pbTmp.removeSources();
		pbTmp.removeParameters();
		pbTmp.addSource(piOut);
		RenderedOp extrema = JAI.create("extrema", pbTmp);
		double[] allMin = (double[]) extrema.getProperty("minimum");
		double[] allMax = (double[]) extrema.getProperty("maximum");
		double[] scale = new double[allMin.length];
		double[] offset = new double[allMin.length];
		for (int i = 0; i < allMin.length; i++) {
			scale[i] = greyValueMax / (allMax[i] - allMin[i]);
			offset[i] = (greyValueMax * allMin[i]) / (allMin[i] - allMax[i]);
			// System.out.println("IqmOpCreateFracSurf: allMin[i] allMax[i] "  + String.valueOf(i)+ ":  " + allMin[i] + "  "+allMax[i] );
		}
		pbTmp.removeSources();
		pbTmp.removeParameters();
		pbTmp.addSource(piOut);
		pbTmp.add(scale);
		pbTmp.add(offset);
		piOut = JAI.create("rescale", pbTmp);

		// Crop to original
		// size----------------------------------------------------
		pbTmp.removeSources();
		pbTmp.removeParameters();
		pbTmp.addSource(piOut);
		pbTmp.add(0f);
		pbTmp.add(0f);
		pbTmp.add((float) width);
		pbTmp.add((float) height);
		piOut = JAI.create("Crop", pbTmp);
		
		return piOut;
	}
	//--------------------------------------------------------------------------------------------------
	/**
	 * This methods calculates a fractal surface using Midpoint Displacement
	 */
	private PlanarImage calcMPDSurface(int width, int height, int greyValueMax, float fracD){
		PlanarImage piOut = null;
		// Size of Image [2^(N_steps-1)+1] x [2^(N_steps-1)+1]:
		int N_steps = 1;
		while( ((Math.pow(2, N_steps-1) + 1) < width) || ((Math.pow(2, N_steps-1) + 1) < height) ){
			N_steps = N_steps +1;
		}
	    int mpdWidth  = (int)Math.pow(2, N_steps-1) + 1; 
	    int mpdHeight = (int)Math.pow(2, N_steps-1) + 1; 
		
		// create image model
		// SampleModel sm = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_FLOAT, dftWidth, dftHeight, 1); //1 banded sample model
		SampleModel sm = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT, mpdWidth, mpdHeight, 1); // 1 banded sample model
		float[] floatArr = new float[mpdWidth * mpdHeight];
		DataBufferFloat db = new DataBufferFloat(floatArr, mpdWidth * mpdHeight);
		WritableRaster wr = RasterFactory.createWritableRaster(sm, db, new Point(0, 0));
	
		//-----------------------------------
		//Hurst exponent:
		double H = 3.0-fracD;

		double mu    = 0.0;
		double sigma = 1.0;

		//Starting-image:
		int N = 2;	
		//generate random values matrix NxN
		double[][] I = new double[N][N];
		Random generator = new Random();
		for (int nn = 0; nn < N; nn++) {
			for (int mm = 0; mm < N; mm++) {
				I[nn][mm] = generator.nextGaussian() * sigma + mu;
			}
		}
       
		for(int kk = 1; kk < N_steps; kk++){
			int proz = (kk) * 100 / N_steps;
			this.fireProgressChanged(proz);

			// ##########################################################################
			// this is the operator-side implementation of the cancellation routine
			// copy this to each implementing class of at.mug.iqm.api.Operator or
			// its subclasses to enable cancellation of the tasks
			if (isCancelled(getParentTask())) {
				// System.out.println("The parent task has been cancelled.");
				return null;
			}
			// ##########################################################################


			//next step:
		    //new sigma:
		    sigma = sigma/Math.pow(2.0,H);
			 
		    //size of new image:    
		    int N_new = 2*N-1;  //ungerade Zahlen 3  5  7  9  
		    double[][]   I2         = new double[N_new][N_new]; // array filled with zeros
		  
		    //data from old image in new image:
		    for(int x = 0; x < N_new; x++){
		    	if (x % 2 == 0) { //x is even % gives the remainder, even numbers do not have a remainder
			        for(int y = 0; y < N_new; y++){
			        	if (y % 2 == 0) { //y is even
			        		I2[x][y] = I[x/2][y/2];
			        	}
			        }
		    	}
		    }    
		    //new central middle points + random shift
		    for(int x = 0; x < N_new; x++){
		    	if (x % 2 != 0) { //x is odd
			        for(int y = 0; y < N_new; y++){
			        	if (y % 2 != 0) { //y is odd
			        		I2[x][y] = (1.f/4.f*(I2[x-1][y-1]+I2[x+1][y-1]+I2[x-1][y+1]+I2[x+1][y+1]))
			        				 + (float) (generator.nextGaussian()*sigma+mu); //random shift
			        	}
			        }
		    	}
		    }	     
		    //new not-central middle points 1 + random shift	         
		    for(int x = 0; x < N_new; x++){
		    	if (x % 2 != 0) { //x is odd
			        for(int y = 1; y < N_new-2; y++){
			        	if (y % 2 == 0) { //y is even
			        		I2[x][y] = (1.f/4.f*(I2[x][y-1]+I2[x][y+1]+I2[x-1][y]+I2[x+1][y])) 
			        				 + (float) (generator.nextGaussian() * sigma + mu); //random shift
			        	}
			        }
		    	}
			}
		    //new not-central middle points 2 + random shift	         
		    for(int x = 1; x < N_new-2; x++){
		    	if (x % 2 == 0) { //x is even
			        for(int y = 0; y < N_new; y++){
			        	if (y % 2 != 0) { //y is odd
			        		I2[x][y] = (1.f/4.f*(I2[x][y-1]+I2[x][y+1]+I2[x-1][y]+I2[x+1][y])) 
			        				 + (float) (generator.nextGaussian() * sigma + mu); //random shift
			        	}
			        }
		    	}
			}


		    //missing points at borders
		    //left
		    int x = 0;
			for(int y = 0; y < N_new; y++){
				if (y % 2 != 0) { //y is odd
					I2[x][y] = (1.f/3.f*(I2[x][y-1]+I2[x][y+1]+I2[x+1][y])) 
			        		 + (float) (generator.nextGaussian() * sigma + mu); //random shift
			    }
			}
		    //right
		    x = N_new-1;
			for(int y = 0; y < N_new; y++){
				if (y % 2 != 0) { //y is odd
					I2[x][y] = (1.f/3.f*(I2[x][y-1]+I2[x][y+1]+I2[x-1][y])) 
			        		 + (float) (generator.nextGaussian() * sigma + mu); //random shift
			    }
			}
		    //top
		    int y = 0;
			for( x = 0; x < N_new; x++){
				if (x % 2 != 0) { //y is odd
					I2[x][y] = (1.f/3.f*(I2[x-1][y]+I2[x+1][y]+I2[x][y+1])) 
			        		 + (float) (generator.nextGaussian() * sigma + mu); //random shift
			    }
			}
		    //bottom
		    y = N_new-1;
			for( x = 0; x < N_new; x++){
				if (x % 2 != 0) { //y is odd
					I2[x][y] = (1.f/3.f*(I2[x-1][y]+I2[x+1][y]+I2[x][y-1])) 
			        		 + (float) (generator.nextGaussian() * sigma + mu); //random shift
			    }
			}
		    N = N_new;
		    I = I2;
		} //kk

		for (int x = 0; x < mpdWidth; x++){
			for (int y = 0; y < mpdHeight; y++){
				wr.setSample(x, y, 0, I[x][y] );
			}
		}
		
		// create tiled images
		ColorModel cm = PlanarImage.createColorModel(sm); // compatible color model
		TiledImage ti = new TiledImage(0, 0, mpdWidth, mpdHeight, 0, 0, sm, cm);
		ti.setData(wr);
		piOut = ti;
		
		// Normalize grey values---------------------------------------------------
		ParameterBlock pb = new ParameterBlock();
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piOut);
		RenderedOp extrema = JAI.create("extrema", pb);
		double[] allMin = (double[]) extrema.getProperty("minimum");
		double[] allMax = (double[]) extrema.getProperty("maximum");
		double[] scale = new double[allMin.length];
		double[] offset = new double[allMin.length];
		for (int i = 0; i < allMin.length; i++) {
			scale[i] = greyValueMax / (allMax[i] - allMin[i]);
			offset[i] = (greyValueMax * allMin[i])/ (allMin[i] - allMax[i]);
			// System.out.println("IqmOpCreateFracSurf: allMin[i] allMax[i] "+ String.valueOf(i)+ ":  " + allMin[i] + "  "+allMax[i] );
		}
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piOut);
		pb.add(scale);
		pb.add(offset);
		piOut = JAI.create("rescale", pb);
		

		// Crop to original size----------------------------------------------------
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piOut);
		pb.add((float)Math.floor((mpdWidth - width) /2.0));  //Offset
		pb.add((float)Math.floor((mpdHeight- height)/2.0));  //Offset
		pb.add((float) width);
		pb.add((float) height);
		piOut = JAI.create("Crop", pb);
		
		if (piOut.getMinX() != 0 || piOut.getMinY() != 0) {
			ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
			pbTrans.addSource(piOut);
			pbTrans.setParameter("xTrans", piOut.getMinX() * -1.0f);
			pbTrans.setParameter("yTrans", piOut.getMinY() * -1.0f);
			piOut = JAI.create("translate", pbTrans);
		}
		
		return piOut;
	}
	//--------------------------------------------------------------------------------------------------
	/**
	 * This methods calculates a fractal surface using sum of sines 
	 */
	private PlanarImage calcSumSinesSurface(int width, int height, int greyValueMax, float fracD){
		PlanarImage piOut = null;
		SampleModel sm = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT, width, height, 1); // 1 banded sample  model
		float[] floatArr = new float[width * height];
		DataBufferFloat db = new DataBufferFloat(floatArr, width * height);
		WritableRaster wr = RasterFactory.createWritableRaster(sm, db, new Point(0, 0));

		int n = 10; // iteration (number of summations)
		float p = 2; // frequency
		float q = 2; // amplitude
		for (int x = 0; x < width; x++) {
			
			int proz = (x + 1) * 100 / width;
			this.fireProgressChanged(proz);

			// ##########################################################################
			// this is the operator-side implementation of the cancellation routine
			// copy this to each implementing class of at.mug.iqm.api.Operator or
			// its subclasses to enable cancellation of the tasks
			if (isCancelled(getParentTask())) {
				// System.out.println("The parent task has been cancelled.");
				return null;
			}
			// ##########################################################################
			
			for (int y = 0; y < height; y++) {
				float value = 0;
				for (int k = 0; k <= n; k++) {
					// normalize x and y to -pi,....0, +pi
					float xx = (float) ((float) x / ((float) (width - 1)) * 2 * Math.PI);
					float yy = (float) ((float) y / ((float) (height - 1))* 2 * Math.PI);
					xx = (float) (xx - (Math.PI));
					yy = (float) (yy - (Math.PI));
					value = value + (float) ((Math.sin(xx * Math.pow(p, k)) + Math.sin(yy * Math.pow(p, k))) / Math.pow(q, k));
				}
				wr.setSample(x, y, 0, value);
			}
		}

		// create tiled image
		ColorModel cm = PlanarImage.createColorModel(sm); // compatible color model
		TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sm, cm);
		ti.setData(wr);
		piOut = ti;
		// Normalize grey values---------------------------------------------------
		ParameterBlock pbExtrema = new ParameterBlock();
		pbExtrema.removeSources();
		pbExtrema.removeParameters();
		pbExtrema.addSource(piOut);
		RenderedOp extrema = JAI.create("extrema", pbExtrema);
		double[] allMin = (double[]) extrema.getProperty("minimum");
		double[] allMax = (double[]) extrema.getProperty("maximum");
		double[] scale = new double[allMin.length];
		double[] offset = new double[allMin.length];
		for (int i = 0; i < allMin.length; i++) {
			scale[i] = greyValueMax / (allMax[i] - allMin[i]);
			offset[i] = (greyValueMax * allMin[i])/ (allMin[i] - allMax[i]);
			// System.out.println("IqmOpCreateFracSurf: allMin[i] allMax[i] "+ String.valueOf(i)+ ":  " + allMin[i] + "  "+allMax[i] );
		}
		pbExtrema.removeSources();
		pbExtrema.removeParameters();
		pbExtrema.addSource(piOut);
		pbExtrema.add(scale);
		pbExtrema.add(offset);
		piOut = JAI.create("rescale", pbExtrema);
		
		return piOut;
	}
	//--------------------------------------------------------------------------------------------------
}

