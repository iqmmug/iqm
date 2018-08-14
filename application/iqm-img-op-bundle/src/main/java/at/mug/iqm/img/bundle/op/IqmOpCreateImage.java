package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCreateImage.java
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


import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Random;

import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.img.bundle.descriptors.IqmOpCreateImageDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2010 01
 * @update 2014 11 RGB
 */
public class IqmOpCreateImage extends AbstractOperator {

	public IqmOpCreateImage() {
		this.setCancelable(true);
	}

	@Override
	public IResult run(IWorkPackage wp) {
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		int width    = pb.getIntParameter("Width");
		int height   = pb.getIntParameter("Height");
		int method   = pb.getIntParameter("Method");
		int gValue1  = pb.getIntParameter("Const1");
		int gValue2  = pb.getIntParameter("Const2");
		int gValue3  = pb.getIntParameter("Const3");
		int omega    = pb.getIntParameter("Omega");
		int outbit   = pb.getIntParameter("OutBit");

		// ---------------------------------------------------------------------------------------------------------------------------
		// create image
		SampleModel sm = null;
		WritableRaster wr = null;
		if (outbit == 0) { // 8bit
			sm = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1); // bugfix 2011 10 26
			//sm = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE, imgWidth, imgHeight, 1); //1 banded sample model //image too bright (only) on display error
			byte[] byteArr = new byte[width * height];
			DataBufferByte db = new DataBufferByte(byteArr, width * height);
			wr = RasterFactory.createWritableRaster(sm, db, new Point(0, 0));
		}
		if (outbit == 1) { // 16bit
			sm = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_USHORT, width, height, 1); // bugfix 2011 1026
			       // sm = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_USHORT, imgWidth, imgHeight, 1); //1 banded sample model
			short[] shortArr = new short[width * height];
			DataBufferUShort db = new DataBufferUShort(shortArr, width * height);
			wr = RasterFactory.createWritableRaster(sm, db, new Point(0, 0));
		}
		if (outbit == 2) { // RGB 24bit
			sm = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 3); // bugfix 2011 10 26
			//sm = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE, imgWidth, imgHeight, 3); //1 banded sample model //image too bright (only) on display error
			byte[] byteArr = new byte[width * height * 3];
			DataBufferByte db = new DataBufferByte(byteArr, width * height * 3);
				
			wr = RasterFactory.createWritableRaster(sm, db, new Point(0, 0));
		}
		

		// ---------------------------------------------------------------------------------------------------------------------------
		if (method == IqmOpCreateImageDescriptor.CREATERANDOM) {// generate random pixel values
			Random generator = new Random();
			for (int x = 0; x < width; x++) {
				int proz = (x + 1) * 100 / width;
				this.fireProgressChanged(proz);
				// ##########################################################################
				// this is the operator-side implementation of the cancellation
				// routine
				// copy this to each implementing class of
				// at.mug.iqm.api.Operator or its
				// subclasses to enable cancellation of the tasks
				if (isCancelled(getParentTask())) {
					System.out.println("The parent task has been cancelled.");
					return null;
				}
				// ##########################################################################
				for (int y = 0; y < height; y++) {
					if (outbit == 0) { // 8bit
						byte value = (byte) generator.nextInt(256);
						wr.setSample(x, y, 0, value);
					}
					if (outbit == 1) { // 16bit
						short value = (short) generator.nextInt(65536);
						wr.setSample(x, y, 0, value);
					}
					if (outbit == 2) { // RGB
						for (int b = 0; b < 3; b++){
							byte value = (byte) generator.nextInt(256);
							wr.setSample(x, y, b, value);
						}
					}
				}
			}
		}
		if (method == IqmOpCreateImageDescriptor.CREATEGAUSSIAN) {// generate gauss distributed pixel values
			Random generator = new Random();
			for (int x = 0; x < width; x++) {
				int proz = (x + 1) * 100 / width;
				this.fireProgressChanged(proz);
				// ##########################################################################
				// this is the operator-side implementation of the cancellation
				// routine
				// copy this to each implementing class of
				// at.mug.iqm.api.Operator or its
				// subclasses to enable cancellation of the tasks
				if (isCancelled(getParentTask())) {
					System.out.println("The parent task has been cancelled.");
					return null;
				}
				// ##########################################################################
				for (int y = 0; y < height; y++) {
					if (outbit == 0) { // 8bit
						byte value = (byte) (generator.nextGaussian() * 30 + 127);
						wr.setSample(x, y, 0, value);
					}
					if (outbit == 1) { // 16bit
						short value = (short) (generator.nextGaussian() * 30 + 32767);
						wr.setSample(x, y, 0, value);
					}
					if (outbit == 2) { // 8RGB
						for (int b = 0; b < 3; b++){
							byte value = (byte) (generator.nextGaussian() * 30 + 127);
							wr.setSample(x, y, b, value);
						}
					}
				}
			}
		}
		if (method == IqmOpCreateImageDescriptor.CREATECONSTANT) {// generate constant grey value image
			for (int x = 0; x < width; x++) {
				int proz = (x + 1) * 100 / width;
				this.fireProgressChanged(proz);
				// ##########################################################################
				// this is the operator-side implementation of the cancellation
				// routine
				// copy this to each implementing class of
				// at.mug.iqm.api.Operator or its
				// subclasses to enable cancellation of the tasks
				if (isCancelled(getParentTask())) {
					System.out.println("The parent task has been cancelled.");
					return null;
				}
				// ##########################################################################
				for (int y = 0; y < height; y++) {
					if (outbit == 0) { // 8bit
						byte value = (byte) gValue1;
						wr.setSample(x, y, 0, value);
					}
					if (outbit == 1) { // 16bit
						short value = (short) gValue1;
						wr.setSample(x, y, 0, value);
					}
					if (outbit == 2) { // RGB
						byte value1 = (byte) gValue1;
						byte value2 = (byte) gValue2;
						byte value3 = (byte) gValue3;
						wr.setSample(x, y, 0, value1);
						wr.setSample(x, y, 1, value2);
						wr.setSample(x, y, 2, value3);
					}
				}
			}
		}
		if (method == IqmOpCreateImageDescriptor.CREATESINUS) {// generate sinus shaped image
			for (int x = 0; x < width; x++) {
				int proz = (x + 1) * 100 / width;
				this.fireProgressChanged(proz);
				// ##########################################################################
				// this is the operator-side implementation of the cancellation
				// routine
				// copy this to each implementing class of
				// at.mug.iqm.api.Operator or its
				// subclasses to enable cancellation of the tasks
				if (isCancelled(getParentTask())) {
					System.out.println("The parent task has been cancelled.");
					return null;
				}
				// ##########################################################################
				for (int y = 0; y < height; y++) {
					float fValue = (float) (Math.sin((float) x / (width - 1) * omega * 2 * Math.PI));
					if (outbit == 0) { // 8bit
						fValue = (fValue + 1.0f) / 2.0f * 255.0f;
						wr.setSample(x, y, 0, (byte) fValue);
					}
					if (outbit == 1) { // 16bit
						fValue = (fValue + 1.0f) / 2.0f * 65535.0f;
						wr.setSample(x, y, 0, (short) fValue);
					}
					if (outbit == 2) { // RGB
						fValue = (fValue + 1.0f) / 2.0f * 255.0f;
						wr.setSample(x, y, 0, (byte) fValue);
						wr.setSample(x, y, 1, (byte) fValue);
						wr.setSample(x, y, 2, (byte) fValue);
					}
				}
			}
		}
		if (method == IqmOpCreateImageDescriptor.CREATECOSINUS) {// generate cosinus shaped image
			for (int x = 0; x < width; x++) {
				int proz = (x + 1) * 100 / width;
				this.fireProgressChanged(proz);
				// ##########################################################################
				// this is the operator-side implementation of the cancellation
				// routine
				// copy this to each implementing class of
				// at.mug.iqm.api.Operator or its
				// subclasses to enable cancellation of the tasks
				if (isCancelled(getParentTask())) {
					System.out.println("The parent task has been cancelled.");
					return null;
				}
				// ##########################################################################
				for (int y = 0; y < height; y++) {
					float fValue = (float) (Math.cos((float) x / (width - 1) * omega * 2 * Math.PI));
					if (outbit == 0) { // 8bit
						fValue = (fValue + 1.0f) / 2.0f * 255.0f;
						wr.setSample(x, y, 0, (byte) fValue);
					}
					if (outbit == 1) { // 16bit
						fValue = (fValue + 1.0f) / 2.0f * 65535.0f;
						wr.setSample(x, y, 0, (short) fValue);
					}
					if (outbit == 2) { // RGB
						fValue = (fValue + 1.0f) / 2.0f * 255.0f;
						wr.setSample(x, y, 0, (byte) fValue);
						wr.setSample(x, y, 1, (byte) fValue);
						wr.setSample(x, y, 2, (byte) fValue);
					}
				}
			}
		}

		// create tiled image
		ColorModel cm = PlanarImage.createColorModel(sm); // compatible color model
		TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sm, cm);
		ti.setData(wr);

		// //Change float to
		// byte----------------------------------------------------
		// ParameterBlock pb = new ParameterBlock();
		// pb.removeSources();
		// pb.removeParameters();
		// pb.addSource(ti);
		// pb.add(DataBuffer.TYPE_BYTE);
		// piOut = JAI.create("format", pb);

		// System.out.println("IqmCreateImage piOut.getClass()" +
		// piOut.getClass());
		ImageModel im = new ImageModel(ti);
		
		//set image name
		if (method == IqmOpCreateImageDescriptor.CREATERANDOM)   im.setModelName("Random");
		if (method == IqmOpCreateImageDescriptor.CREATEGAUSSIAN) im.setModelName("Gaussian");
		if (method == IqmOpCreateImageDescriptor.CREATECONSTANT) im.setModelName("Constant-" + gValue1);
		if (method == IqmOpCreateImageDescriptor.CREATESINUS)    im.setModelName("Sin-" + omega);
		if (method == IqmOpCreateImageDescriptor.CREATECOSINUS)  im.setModelName("Cos-" +omega);
		
		im.setFileName("IQM generated image");
		
		fireProgressChanged(95);
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpCreateImageDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpCreateImageDescriptor.TYPE;
	}
}
