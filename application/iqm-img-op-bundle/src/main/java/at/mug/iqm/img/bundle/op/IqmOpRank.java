package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpRank.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.media.jai.operator.MaxFilterDescriptor;
import javax.media.jai.operator.MaxFilterShape;
import javax.media.jai.operator.MedianFilterDescriptor;
import javax.media.jai.operator.MedianFilterShape;
import javax.media.jai.operator.MinFilterDescriptor;
import javax.media.jai.operator.MinFilterShape;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.img.bundle.descriptors.IqmOpRankDescriptor;

/**
 * <b>Known issue:</b> 2011 01: MinFilterDescriptor.MIN_MASK_SQUARE_SEPARABLE does not work properly at the image borders
 * 
 * @author Ahammer, Kainz
 * @since 2009 06
 */
public class IqmOpRank extends AbstractOperator {

	public IqmOpRank() {
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
		
		PlanarImage original = ((IqmDataBox) pb.getSource(0)).getImage();

		int method = pb.getIntParameter("Method");
		int kernelShape = pb.getIntParameter("KernelShape");
		int maskSize = pb.getIntParameter("KernelSize");
		int iterations = pb.getIntParameter("Iterations");
		double alpha = pb.getDoubleParameter("Alpha");

		// for the first iteration set the original to the output image
		piOut = original;
		for (int iter = 0; iter < iterations; iter++){
			if (method == 0) { // Median
				MedianFilterShape maskShape = null;
				if (kernelShape == 0)
					maskShape = MedianFilterDescriptor.MEDIAN_MASK_SQUARE;
				if (kernelShape == 1)
					maskShape = MedianFilterDescriptor.MEDIAN_MASK_PLUS;
				if (kernelShape == 2)
					maskShape = MedianFilterDescriptor.MEDIAN_MASK_X;
				if (kernelShape == 3)
					maskShape = MedianFilterDescriptor.MEDIAN_MASK_SQUARE_SEPARABLE;
	
				RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
						BorderExtender.createInstance(BorderExtender.BORDER_COPY));
				ParameterBlock pbWork = new ParameterBlock();
				pbWork.addSource(piOut);
				pbWork.add(maskShape);
				pbWork.add(maskSize);
				piOut = JAI.create("MedianFilter", pbWork, rh);
			}
			if (method == 1) { // Min
				MinFilterShape maskShape = null;
				if (kernelShape == 0)
					maskShape = MinFilterDescriptor.MIN_MASK_SQUARE;
				if (kernelShape == 1)
					maskShape = MinFilterDescriptor.MIN_MASK_PLUS;
				if (kernelShape == 2)
					maskShape = MinFilterDescriptor.MIN_MASK_X;
				if (kernelShape == 3)
					maskShape = MinFilterDescriptor.MIN_MASK_SQUARE_SEPARABLE;
	
				RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
						BorderExtender.createInstance(BorderExtender.BORDER_COPY));
				ParameterBlock pbWork = new ParameterBlock();
				pbWork.addSource(piOut);
				pbWork.add(maskShape);
				pbWork.add(maskSize);
				piOut = JAI.create("MinFilter", pbWork, rh);
			}
			if (method == 2) { // Max
				MaxFilterShape maskShape = null;
				if (kernelShape == 0)
					maskShape = MaxFilterDescriptor.MAX_MASK_SQUARE;
				if (kernelShape == 1)
					maskShape = MaxFilterDescriptor.MAX_MASK_PLUS;
				if (kernelShape == 2)
					maskShape = MaxFilterDescriptor.MAX_MASK_X;
				if (kernelShape == 3)
					maskShape = MaxFilterDescriptor.MAX_MASK_SQUARE_SEPARABLE;
	
				RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
						BorderExtender.createInstance(BorderExtender.BORDER_COPY));
				ParameterBlock pbWork = new ParameterBlock();
				pbWork.addSource(piOut);
				pbWork.add(maskShape);
				pbWork.add(maskSize);
				piOut = JAI.create("MaxFilter", pbWork, rh);
			}
			
			if (method == 3) { // Alpha-trimmed mean
				// get the raster of the image
				int width = piOut.getWidth();
				int height = piOut.getHeight();
				
				TiledImage ti = new TiledImage(
						piOut.getMinX(), 
						piOut.getMinY(), 
						width, 
						height, 
						piOut.getTileGridXOffset(), 
						piOut.getTileGridYOffset(), 
						piOut.getSampleModel(), 
						piOut.getColorModel());
				
				int nBands = piOut.getNumBands();
				WritableRaster wr = piOut.getData().createCompatibleWritableRaster();

				// compute symmetric margin
				int maskMargin = (int) Math.floor(maskSize/2.0d);
				
				// extend the border of the image for the computation
				ParameterBlock pbBrdr = new ParameterBlock();
				pbBrdr.addSource(piOut);
				pbBrdr.add(maskMargin);// left
				pbBrdr.add(maskMargin);// right
				pbBrdr.add(maskMargin);// top
				pbBrdr.add(maskMargin);// bottom
				pbBrdr.add(BorderExtender.createInstance(BorderExtender.BORDER_COPY));// Copy

				piOut = JAI.create("Border", pbBrdr, null);
				if (piOut.getMinX() != 0 || piOut.getMinY() != 0) {
					ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
					pbTrans.addSource(piOut);
					pbTrans.setParameter("xTrans", piOut.getMinX() * -1.0f);
					pbTrans.setParameter("yTrans", piOut.getMinY() * -1.0f);
					piOut = JAI.create("translate", pbTrans);
				}
				Raster r = piOut.getData();
				width = r.getWidth();
				height = r.getHeight();

				for (int x = 0+maskMargin; x < width-maskMargin; x++){
					for (int y = 0+maskMargin; y < height-maskMargin; y++){
						// get the pixels under the mask
						int[][][] maskedPixels = new int[maskSize][maskSize][nBands]; 
						boolean[][] mask = new boolean[maskSize][maskSize];
						
						// get the shape of the kernel
						switch(kernelShape){
						case 0: // square
							// take all pixels in the neigbourhood and rank them
							int id_x_sq = 0;
							for (int xx = -maskMargin; xx <= maskMargin; xx++){
								int id_y_sq = 0;
								for (int yy = -maskMargin; yy <= maskMargin; yy++){
									for (int band = 0; band < nBands; band++){
										maskedPixels[id_x_sq][id_y_sq][band] = r.getSample(x+xx, y+yy, band);
										mask[id_x_sq][id_y_sq] = true;
									}
									id_y_sq++;
								}
								id_x_sq++;
							}
							break;
						case 1: // plus
							// just take pixels in horizontal and vertical neigbourhood and rank them
							int id_x_pl = 0;
							for (int xx = -maskMargin; xx <= maskMargin; xx++){
								int id_y_pl = 0;
								for (int yy = -maskMargin; yy <= maskMargin; yy++){
									if (xx == 0 || yy == 0){
										for (int band = 0; band < nBands; band++){
											maskedPixels[id_x_pl][id_y_pl][band] = r.getSample(x+xx, y+yy, band);
											mask[id_x_pl][id_y_pl] = true;
										}
										id_y_pl++;
									}
								}
								id_x_pl++;
							}
							break;
						case 2: // X
							int id_x_cr = 0;
							for (int xx = -maskMargin; xx <= maskMargin; xx++){
								int id_y_cr = 0;
								for (int yy = -maskMargin; yy <= maskMargin; yy++){
									if (
											(xx == 0 && yy == 0) || // center pixel
											 Math.abs(Math.round(yy/(xx+10e-10d))) == 1) // each 45 degrees
										{
										for (int band = 0; band < nBands; band++){
											maskedPixels[id_x_cr][id_y_cr][band] = r.getSample(x+xx, y+yy, band);
											mask[id_x_cr][id_y_cr] = true;
										}
										id_y_cr++;
									}
								}
								id_x_cr++;
							}
							break;
							
						case 3: // square separable
							DialogUtil.getInstance().showDefaultInfoMessage("Not yet implemented for this kernel type.");
							break;
						default: break;
						}
								
						// rank all values under the mask for the particular band
						for (int band = 0; band < nBands; band++){
							int[] bndIntensities = new int[countTrues(mask)];
							int u = 0;
							for (int k = 0; k < maskSize; k++){
								for (int l = 0; l < maskSize; l++){
									// collect all values in the mask
									if (mask[k][l]==true){
										bndIntensities[u] = maskedPixels[k][l][band];
										u++;
									}
								}
							}
							// sort the array
							Arrays.sort(bndIntensities);
							// trim alpha percent from left and right
							int nTrim = (int) Math.floor(alpha * bndIntensities.length);
							 
							int intensity = 0;
							int n = 0;
							for (int i = nTrim; i < bndIntensities.length-nTrim; i++){
								// sum the remaining elements
								intensity += bndIntensities[i];
								n++;
							}
							// compute the mean  
							intensity = Math.round(intensity/n);
							
							// set the pixel intensities for each band
							wr.setSample(x-maskMargin, y-maskMargin, band, intensity);
						}
					}
				}
				// set the image data
				ti.setData(wr);
				piOut = ti;
			}
		}
		fireProgressChanged(85);
		
		ImageModel im = new ImageModel(piOut);
		im.setFileName(String.valueOf(original.getProperty("file_name")));
		im.setModelName(String.valueOf(original.getProperty("image_name")));
		
		fireProgressChanged(95);
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpRankDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpRankDescriptor.TYPE;
	}
	
	private int countTrues(boolean[][] values) {
		int n = 0;
		for (int x = 0; x < values.length; x++){
			for (int y = 0; y < values.length; y++){
		       if (values[x][y] == true){
		    	   n++;
		       }
			}
	    }
	    return n;
	}
}
