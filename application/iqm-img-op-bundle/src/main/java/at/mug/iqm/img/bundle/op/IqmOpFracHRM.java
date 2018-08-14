package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracHRM.java
 * 
 * $Id: IqmOpFracHRM.java 548 2016-01-18 09:36:47Z kainzp $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/op/IqmOpFracHRM.java $
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
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.util.Random;
import javax.media.jai.BorderExtender;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpFracHRMDescriptor;

/**
 * HRM Hierarchical random maps in order to get images with distinct lacunarities
 * Plotnick et al 1993 Lacunarity indices as measures of landscape texture
 * 
 * @author Ahammer
 * @since  2016 06
 */

public class IqmOpFracHRM extends AbstractOperator {

	public IqmOpFracHRM() {
		this.setCancelable(true);
	}


	@SuppressWarnings("unused")
	@Override
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage piOut = null;

		// PlanarImage pi = (PlanarImage) pbJAI.getSource(0); //no source image
		// needed
		PlanarImage pi;
		int width   = pb.getIntParameter("Width");
		int height  = pb.getIntParameter("Height");
		int itMax   = pb.getIntParameter("ItMax");
		double p1   = pb.getDoubleParameter("P1");
		double p2   = pb.getDoubleParameter("P2");
		double p3   = pb.getDoubleParameter("P3");
		double p4   = pb.getDoubleParameter("P4");
		double p5   = pb.getDoubleParameter("P5");
		double p6   = pb.getDoubleParameter("P6");
		double p7   = pb.getDoubleParameter("P7");
		double p8   = pb.getDoubleParameter("P8");
	
		int method = pb.getIntParameter("Method");
		
		double[] probs = new double[8];
		probs[0] = p1;
		probs[1] = p2;
		probs[2] = p3;
		probs[3] = p4;
		probs[4] = p5;
		probs[5] = p6;
		probs[6] = p7;
		probs[7] = p8;
		
		//Original Plotnick
		if (method == IqmOpFracHRMDescriptor.PLOTNICK){
			// create new image
			SampleModel sampleModel = RasterFactory.createBandedSampleModel( DataBuffer.TYPE_BYTE, width, height, 1);
			// SampleModel sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1);
			ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
			byte[] byteVector = new byte[width * height];
			Random random = new Random();
			int index = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					// index += 1;			
					if (random.nextDouble() < probs[0]) byteVector[index++] = (byte) 255;
					else 						  byteVector[index++] = 0;
				}
			}
			DataBufferByte db = new DataBufferByte(byteVector, width * height);
			Raster wr = RasterFactory.createWritableRaster(sampleModel, db, new Point(0, 0));
			TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sampleModel, colorModel);
			ti.setData(wr);
			pi = ti;
			piOut = pi;
	
			for (int i = 1; i < itMax; i++) {
				int proz = (i + 1) * 100 / itMax;
				this.fireProgressChanged(proz);
	
				
				float zoomX = (float)width;
				float zoomY = (float)height;
				ParameterBlock pbScale = new ParameterBlock();
				pbScale.addSource(ti);
				pbScale.add(zoomX);//x scale factor
				pbScale.add(zoomY);//y scale factor
				pbScale.add(0.0F);//x translate
				pbScale.add(0.0F);//y translate
				int optIntP = 0;
				if (optIntP == 0) pbScale.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
				if (optIntP == 1) pbScale.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
				if (optIntP == 2) pbScale.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
				if (optIntP == 3) pbScale.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2));
				
				RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
				pi = JAI.create("scale", pbScale, rh);
				int newWidth = pi.getWidth();
				int newHeight = pi.getHeight();
				
				ti = new TiledImage(pi, false);
				
				for (int x = 0; x < newWidth; x++) {
					for (int y = 0; y < newHeight; y++) {
						if (ti.getSample(x, y, 0) == 255){
							if (random.nextDouble() < probs[i]){
								ti.setSample(x, y, 0, 255);
							}
							else {
								ti.setSample(x, y, 0, 0);
							}
						}	
					}
				}
				pi = ti;
			}
	
			piOut = pi;
		}//Plotnick
		
		piOut.setProperty("image_name", "HRM " + itMax + " iterations");
		piOut.setProperty("file_name", "");
	
		ImageModel im = new ImageModel(piOut);
		im.setModelName(String.valueOf(piOut.getProperty("image_name")));
			
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
		
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpFracHRMDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpFracHRMDescriptor.TYPE;
	}
}
