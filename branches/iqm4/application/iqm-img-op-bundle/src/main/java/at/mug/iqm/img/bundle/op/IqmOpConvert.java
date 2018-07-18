package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpConvert.java
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


import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.ColorConverter;
import at.mug.iqm.commons.util.image.ImageAnalyzer;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpConvertDescriptor;

/**
 * @author Helmut Ahammer, Kainz
 * @since  2009 03
 * @update 2014 05 HA added 8bit to false color options
 */
public class IqmOpConvert extends AbstractOperator {

	public IqmOpConvert() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@Override
	public IResult run(IWorkPackage wp) {
		
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		String imgName = (String) pi.getProperty("image_name");
		String fileName = String.valueOf(pi.getProperty("file_name"));
		
		int opt8bit    = pb.getIntParameter("Options8bit");
		int opt16bit   = pb.getIntParameter("Options16bit");
		int optPalette = pb.getIntParameter("OptionsPalette");
		int optRGB     = pb.getIntParameter("OptionsRGB");
		int optRGBa    = pb.getIntParameter("OptionsRGBa");
		// int numBands = pi.getData().getNumBands();
		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = ImageTools.getImgType(pixelSize, numBands);
		// String type = ImageTools.getImgType(pi);

		if (ImageAnalyzer.is8BitGrey(pi)) { // convert 8 bit Image

			if (opt8bit == 0) { // 8bit to RGB
				// PlanarImage[] pis = new PlanarImage[3];
				// pis[0] = ti;
				// pis[1] = ti;
				// pis[2] = ti;
				ParameterBlockJAI pbBM = new ParameterBlockJAI("bandmerge");
				for (int n = 0; n < 3; n++) {
					pbBM.setSource(pi, n);
				}
				pi = JAI.create("bandmerge", pbBM, null);
			}
			if (opt8bit == 1) { // 8bit to 16bit
				ParameterBlock pbFmt = new ParameterBlock();
				pbFmt.addSource(pi);
				pbFmt.add(DataBuffer.TYPE_USHORT);
				pi = JAI.create("format", pbFmt);

				// pb.removeSources();
				// pb.removeParameters();
				// pb.addSource(pi);
				// RenderedOp extrema = JAI.create("extrema", pb);
				// // Must get the extrema of all bands !
				// double[] minVec = (double[])extrema.getProperty("minimum");
				// double[] maxVec = (double[])extrema.getProperty("maximum");
				// double min = minVec[0];
				// double max = maxVec[0];

				pbFmt.removeSources();
				pbFmt.removeParameters();
				pbFmt.addSource(pi);
				pbFmt.add(new double[] { 65535.0 / 255.0 }); // Rescale
				pbFmt.add(new double[] { 0.0 }); // offset
				pi = JAI.create("rescale", pbFmt);
			}
			if (opt8bit == 2) { // 8bit to RGB false color		
				 // Create the R,G,B arrays for the false color image ...	
				Raster r = pi.getData();
				int red[][]   = new int[r.getWidth()][r.getHeight()];
				int green[][] = new int[r.getWidth()][r.getHeight()];
				int blue[][]  = new int[r.getWidth()][r.getHeight()];
				
				float midSlope = (float)(255.0/(192.0 - 64.0));
				float leftSlope = (float)(255.0/64.0);
				float rightSlope = (float)(-255.0/63.0);
				int greyValue;
				@SuppressWarnings("unused")
				int entry = 0;
				for (int y = 0; y < r.getHeight(); y++) {
					for (int x = 0; x < r.getWidth(); x++){
						greyValue = r.getSample(x, y, 0);
						// Now the false color assignment ...
						if ( greyValue < 64 ) {
							red  [x][y] = 0;
							green[x][y] = Math.round(leftSlope*greyValue);
							blue [x][y] = 255;
						}
						else if ( ( greyValue >= 64 ) && ( greyValue < 192 ) ){
							red  [x][y] = Math.round(255+midSlope*(greyValue-192));
							green[x][y] = 255;
							blue [x][y] = Math.round(255-midSlope*(greyValue-64));
						}
						else {
							red  [x][y] = 255;
							green[x][y]= Math.round(255+rightSlope*(greyValue-192));
							blue [x][y] = 0;
						}
					}
				}
				// Now create the false color image ...
				BufferedImage falseColor = new BufferedImage(r.getWidth(), r.getHeight(), BufferedImage.TYPE_INT_RGB);
				WritableRaster falseColorRaster = falseColor.getRaster();
				for (int y = 0; y < falseColor.getHeight(); y++) {
					for (int x = 0; x < falseColor.getWidth(); x++) {
						falseColorRaster.setSample(x,y,0,   red[x][y]);
						falseColorRaster.setSample(x,y,1, green[x][y]);
						falseColorRaster.setSample(x,y,2,  blue[x][y]);
					}
				}
				// create tiled image
				SampleModel sm = falseColorRaster.getSampleModel();
				ColorModel cm = PlanarImage.createColorModel(sm); // compatible color model
				TiledImage ti = new TiledImage(0, 0, falseColorRaster.getWidth(), falseColorRaster.getHeight(), 0, 0, sm, cm);
				ti.setData(falseColorRaster);
				
				ParameterBlock pbFormat = new ParameterBlock();
				pbFormat.addSource(ti);
				pbFormat.add(DataBuffer.TYPE_BYTE);
				pi = JAI.create("format", pbFormat);
				
			}
			if ((opt8bit == 3) || (opt8bit == 4) || (opt8bit == 5)) { // 8bit to RGB red, blue or green false color		
				 // Create the R,G,B arrays for the false color image ...	
				Raster r = pi.getData();
				int red[][]   = new int[r.getWidth()][r.getHeight()];
				int green[][] = new int[r.getWidth()][r.getHeight()];
				int blue[][]  = new int[r.getWidth()][r.getHeight()];
		
				int greyValue;
				
				if (opt8bit == 3){ //red
					for (int y = 0; y < r.getHeight(); y++) {
						for (int x = 0; x < r.getWidth(); x++){
							greyValue = r.getSample(x, y, 0);
							// Now the false color assignment ...
							red  [x][y] = greyValue;
							green[x][y] = 0;
							blue [x][y] = 0;			
						}
					}
				}
				if (opt8bit == 4){ //green
					for (int y = 0; y < r.getHeight(); y++) {
						for (int x = 0; x < r.getWidth(); x++){
							greyValue = r.getSample(x, y, 0);
							// Now the false color assignment ...
							red  [x][y] = 0;
							green[x][y] = greyValue;
							blue [x][y] = 0;			
						}
					}
				}
				if (opt8bit == 5){ //blue
					for (int y = 0; y < r.getHeight(); y++) {
						for (int x = 0; x < r.getWidth(); x++){
							greyValue = r.getSample(x, y, 0);
							// Now the false color assignment ...
							red  [x][y] = 0;
							green[x][y] = 0;
							blue [x][y] = greyValue;			
						}
					}
				}
				// Now create the false color image ...
				BufferedImage falseColor = new BufferedImage(r.getWidth(), r.getHeight(), BufferedImage.TYPE_INT_RGB);
				WritableRaster falseColorRaster = falseColor.getRaster();
				for (int y = 0; y < falseColor.getHeight(); y++) {
					for (int x = 0; x < falseColor.getWidth(); x++) {
						falseColorRaster.setSample(x,y,0,   red[x][y]);
						falseColorRaster.setSample(x,y,1, green[x][y]);
						falseColorRaster.setSample(x,y,2,  blue[x][y]);
					}
				}
				// create tiled image
				SampleModel sm = falseColorRaster.getSampleModel();
				ColorModel cm = PlanarImage.createColorModel(sm); // compatible color model
				TiledImage ti = new TiledImage(0, 0, falseColorRaster.getWidth(), falseColorRaster.getHeight(), 0, 0, sm, cm);
				ti.setData(falseColorRaster);
				
				ParameterBlock pbFormat = new ParameterBlock();
				pbFormat.addSource(ti);
				pbFormat.add(DataBuffer.TYPE_BYTE);
				pi = JAI.create("format", pbFormat);	
			}
		}
		else if (ImageAnalyzer.is16BitGrey(pi)) { // convert 16bit Image

			if (opt16bit == 0) { // 16bit to 8bit
				ParameterBlock pbTmp = new ParameterBlock();
				pbTmp.addSource(pi);
				RenderedOp extrema = JAI.create("extrema", pbTmp);
				// Must get the extrema of all bands !
				// double[] minVec = (double[])extrema.getProperty("minimum");
				double[] maxVec = (double[]) extrema.getProperty("maximum");
				// double min = minVec[0];
				double max = maxVec[0];

				pbTmp.removeSources();
				pbTmp.removeParameters();
				pbTmp.addSource(pi);
				pbTmp.add(new double[] { (255.0 / max) }); // Rescale
				pbTmp.add(new double[] { 0.0 }); // offset
				pi = JAI.create("rescale", pbTmp);

				pbTmp.removeSources();
				pbTmp.removeParameters();
				pbTmp.addSource(pi);
				pbTmp.add(DataBuffer.TYPE_BYTE);
				pi = JAI.create("format", pbTmp);
			}
			if (opt16bit == 1) { // not yet implemented

			}
		}
		else if (ImageAnalyzer.isIndexed(pi)) { // convert Palette

			if (optPalette == 0) { // Palette to RGB
				IndexColorModel icm = (IndexColorModel) pi.getColorModel();
				byte[][] data = new byte[3][icm.getMapSize()];
				icm.getReds(data[0]);
				icm.getGreens(data[1]);
				icm.getBlues(data[2]);
				LookupTableJAI lut = new LookupTableJAI(data);
				pi = JAI.create("lookup", pi, lut); // RGB image
			}
			if (optPalette == 1) { // Palette to Grey (NTSC)
				IndexColorModel icm = (IndexColorModel) pi.getColorModel();
				byte[][] data = new byte[3][icm.getMapSize()];
				icm.getReds(data[0]);
				icm.getGreens(data[1]);
				icm.getBlues(data[2]);
				LookupTableJAI lut = new LookupTableJAI(data);
				pi = JAI.create("lookup", pi, lut); // RGB image

				double[][] m = { { 0.114, 0.587, 0.299, 0 } };
				ParameterBlock pbBC = new ParameterBlock();
				pbBC.addSource(pi);
				pbBC.add(m);
				pi = JAI.create("bandcombine", pbBC, null);
			}
			if (optPalette == 2) { // Palette to Grey (Mean)
				IndexColorModel icm = (IndexColorModel) pi.getColorModel();
				byte[][] data = new byte[3][icm.getMapSize()];
				icm.getReds(data[0]);
				icm.getGreens(data[1]);
				icm.getBlues(data[2]);
				LookupTableJAI lut = new LookupTableJAI(data);
				pi = JAI.create("lookup", pi, lut); // RGB image

				double[][] m = { { 1. / 3, 1. / 3, 1. / 3, 0 } };
				ParameterBlock pbBC = new ParameterBlock();
				pbBC.addSource(pi);
				pbBC.add(m);
				pi = JAI.create("bandcombine", pbBC, null);
			}
		}

		else if (ImageAnalyzer.is8Bit3Band(pi)) { // convert RGB Image
			// then continue
			if (optRGB == 0) { // NTSC
				double[][] m = { { 0.114, 0.587, 0.299, 0 } };
				ParameterBlock pbBC = new ParameterBlock();
				pbBC.addSource(pi);
				pbBC.add(m);
				pi = JAI.create("bandcombine", pbBC, null);
			}
			if (optRGB == 1) { // RGB Mean
				double[][] m = { { 1. / 3, 1. / 3, 1. / 3, 0 } };
				ParameterBlock pbBC = new ParameterBlock();
				pbBC.addSource(pi);
				pbBC.add(m);
				pi = JAI.create("bandcombine", pbBC, null);
			}
			if (optRGB == 2) { // extract R
				// pi = JAI.create("bandselect", pi, new int[] {0}); //too
				// bright, but only on display!!!
				// bandselect alone does not set the sample model in the right
				// way
				int dataType = pi.getSampleModel().getDataType();
				int width = pi.getWidth();
				int height = pi.getHeight();
				SampleModel sm = RasterFactory
						.createPixelInterleavedSampleModel(dataType, width,
								height, 1);
				ColorModel cm = PlanarImage.createColorModel(sm);
				TiledImage ti = new TiledImage(0, 0, pi.getWidth(),
						pi.getHeight(), 0, 0, sm, cm);
				RenderedImage ri = JAI
						.create("bandselect", pi, new int[] { 0 });
				Raster ra = ri.getData();
				ti.setData(ra);
				pi = ti;
				// double[][] matrix = { { 1.0d, 0.0d, 0.0d, 0.0d } };
				// pi = JAI.create( "bandcombine", new
				// ParameterBlock().addSource(pi).add(matrix), null);
				pi.setProperty("image_name", imgName + "_Band1");
			}
			if (optRGB == 3) { // extract G
				// pi = JAI.create("bandselect", pi, new int[] {1}); //too
				// bright, but only on display!!!
				double[][] matrix = { { 0.0d, 1.0d, 0.0d, 0.0d } };
				pi = JAI.create("bandcombine",
						new ParameterBlock().addSource(pi).add(matrix), null);
				pi.setProperty("image_name", imgName + "_Band2");
			}
			if (optRGB == 4) { // extract B
				// pi = JAI.create("bandselect", pi, new int[] {2}); ////too
				// bright, but only on display!!!
				double[][] matrix = { { 0.0d, 0.0d, 1.0d, 0.0d } };
				pi = JAI.create("bandcombine",
						new ParameterBlock().addSource(pi).add(matrix), null);
				pi.setProperty("image_name", imgName + "_Band3");
			}
			if (optRGB == 5) { // RGB to HSV
				Raster raster = pi.getData();
				TiledImage ti = new TiledImage(pi, false); // true replaces pi!!!
				ti.setData(ColorConverter.RGBtoHSV(raster));
				pi = ti;
			}
			if (optRGB == 6) { // RGB to HLS
				Raster raster = pi.getData();
				TiledImage ti = new TiledImage(pi, false); // true replaces pi!!!
				ti.setData(ColorConverter.RGBtoHLS(raster));
				pi = ti;
			}
			if (optRGB == 7) { // RGB to CIELAB
				Raster raster = pi.getData();
				TiledImage ti = new TiledImage(pi, false); // true replaces pi!!!
				ti.setData(ColorConverter.RGBtoCIELAB(raster));
				pi = ti;
			}
			if (optRGB == 8) { // RGB to CIELUV
				Raster raster = pi.getData();
				TiledImage ti = new TiledImage(pi, false); // true replaces pi!!!
				ti.setData(ColorConverter.RGBtoCIELUV(raster));
				pi = ti;
			}
			if (optRGB == 9) { // RGB to CIEXYZ
				Raster raster = pi.getData();
				TiledImage ti = new TiledImage(pi, false); // true replaces pi!!!
				ti.setData(ColorConverter.RGBtoCIEXYZ(raster));
				pi = ti;
			}

			if (optRGB == 10) { // HSV to RGB
				Raster raster = pi.getData();
				TiledImage ti = new TiledImage(pi, false); // true replaces pi!!!
				ti.setData(ColorConverter.HSVtoRGB(raster));
				pi = ti;

			}
			if (optRGB == 11) { // HLS to RGB
				Raster raster = pi.getData();
				TiledImage ti = new TiledImage(pi, false); // true replaces pi!!!
				ti.setData(ColorConverter.HLStoRGB(raster));
				pi = ti;
			}
			if (optRGB == 12) { // CIELAB to RGB
				Raster raster = pi.getData();
				TiledImage ti = new TiledImage(pi, false); // true replaces pi!!!
				ti.setData(ColorConverter.CIELABtoRGB(raster));
				pi = ti;
			}
			if (optRGB == 13) { // CIELUV to RGB
				Raster raster = pi.getData();
				TiledImage ti = new TiledImage(pi, false); // true replaces pi!!!
				ti.setData(ColorConverter.CIELUVtoRGB(raster));
				pi = ti;
			}
			if (optRGB == 14) { // CIEXYZ to RGB
				Raster raster = pi.getData();
				TiledImage ti = new TiledImage(pi, false); // true replaces pi!!!
				ti.setData(ColorConverter.CIEXYZtoRGB(raster));
				pi = ti;
			}
		}
		
		else if (ImageAnalyzer.is8Bit4Band(pi)){
			if (optRGBa == 0) { // RGBa to RGB (remove channel - no alpha premultiplied)
				pi = ImageTools.removeAlphaChannel(pi);
			}
		}
		
		ImageModel im = new ImageModel(pi);
		im.setFileName(fileName);
		im.setModelName(imgName);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpConvertDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpConvertDescriptor.TYPE;
	}
}// END
