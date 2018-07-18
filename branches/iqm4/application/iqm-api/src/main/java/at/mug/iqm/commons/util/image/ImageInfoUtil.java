package at.mug.iqm.commons.util.image;

/*
 * #%L
 * Project: IQM - API
 * File: ImageInfoUtil.java
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


import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

import at.mug.iqm.api.IQMConstants;


/**  
 * This class has been adapted from:
 * A utility class for obtaining a variety of image information.
 * <p>
 * http://my.execpc.com/~larryhr/
 * 
 * @since 1.1, June 2012
 * @author Lawrence Rodrigues, altered by Helmut Ahammer and Philipp Kainz.
 */
public final class ImageInfoUtil {
	public final static int RED = 0;
	public final static int GREEN = 1;
	public final static int BLUE = 2;
	public final static int ALPHA = 3;

	public final static int DIRECT = 0;
	public final static int INDEX = 1;
	public final static int COMPONENT = 2;

	public final static int COMPONENT_SAMPLE = 0;
	public final static int BANDED = 1;
	public final static int PIXEL_INTERLEAVED = 2;
	public final static int SINGLE_PIXEL_PACKED =3;
	public final static int MULTI_PIXEL_PACKED = 4;


	public static String[][] getImageInfo(final BufferedImage img) {
		SampleModel sm = img.getSampleModel();
		WritableRaster wr = img.getRaster();
		DataBuffer db = wr.getDataBuffer();
		ColorModel cm = img.getColorModel();
		String str = getColorModelAsText(getColorModelType(cm));
		ColorSpace cs = cm.getColorSpace();
		String[][] rowData = new String[10][2];
		int i = 0;
		rowData[i][0] = "ColorModel";
		rowData[i++][1] = str;
		rowData[i][0] = "ColorSpace";
		rowData[i++][1] = getColorSpaceAsText(cs.getType());

		rowData[i][0] = "SampleModel";
		rowData[i++][1] = getSampleModelAsText(getSampleModelType(sm));
		//DataBuffer db = wr.getDataBuffer();
		rowData[i][0] = "DataType";
		rowData[i++][1] = getDataTypeAsText(sm, cm);
		int numbands = sm.getNumBands();
		rowData[i][0] = "NumberOfBands";
		rowData[i++][1] =  Integer.toString(numbands);
		int numbanks = db.getNumBanks();
		rowData[i][0] = "NumberOfBanks";
		rowData[i++][1] =  Integer.toString(numbanks);

		rowData[i][0] = "Width";
		rowData[i++][1] = Integer.toString(img.getWidth());
		rowData[i][0] = "Height";
		rowData[i++][1] = Integer.toString(img.getHeight());
		int scanlineStride=0;
		if(sm instanceof ComponentSampleModel) {
			ComponentSampleModel csm = (ComponentSampleModel)sm;
			scanlineStride = csm.getScanlineStride();
			rowData[i][0] = "ScanlineStride";
			rowData[i++][1] = Integer.toString(scanlineStride);
			int pixelStride =   csm.getPixelStride();
			rowData[i][0] = "PixelStride";
			rowData[i++][1] = Integer.toString(pixelStride);
//			int[] bankIndices = csm.getBankIndices();
//			System.out.println("bank indices = " );
//			for(int j=0; j<bankIndices.length;j++)
//			    System.out.println(bankIndices[j]);
//			int[] bandOffsets = csm.getBandOffsets();
//			System.out.println("bank offsets = " );
//			for(int j=0; j<bandOffsets.length;j++)
//			System.out.println(bandOffsets[j]);
		} else {
			if( sm  instanceof SinglePixelPackedSampleModel){
				SinglePixelPackedSampleModel ssm = (SinglePixelPackedSampleModel) sm;
				scanlineStride = ssm.getScanlineStride();
				rowData[i][0] = "ScanlineStride";
				rowData[i++][1] = Integer.toString(scanlineStride);
				rowData[i][0] = "PixelStride";
				rowData[i++][1] = "N/A";
			} else if ( sm  instanceof MultiPixelPackedSampleModel){
				MultiPixelPackedSampleModel  msm = (MultiPixelPackedSampleModel) sm;
				scanlineStride = msm.getScanlineStride();
				rowData[i][0] = "ScanlineStride";
				rowData[i++][1] = Integer.toString(scanlineStride);
				int pixelBitStride = msm.getPixelBitStride();
				rowData[i][0] = "PixelBitStride";
				rowData[i++][1] = Integer.toString(pixelBitStride);
			}
		}
		return rowData;
	}

	public static String getColorSpaceAsText(int cs){
		switch(cs) {
		case ColorSpace.CS_GRAY:
			return "Gray";
		case ColorSpace.CS_PYCC:
			return "Photo YCC";
		case ColorSpace.CS_sRGB:
			return "sRGB";
		case ColorSpace.CS_LINEAR_RGB:
			return "Linear RGB";
		case ColorSpace.CS_CIEXYZ:
			return "CIEXYZ ";
		case ColorSpace.TYPE_GRAY:
			return "Gray";
		case ColorSpace.TYPE_CMYK:
			return "CMYK";
		case ColorSpace.TYPE_RGB:
			return "RGB";
		case ColorSpace.TYPE_HSV:
			return "HSV";
		case ColorSpace.TYPE_XYZ:
			return "XYZ";
		default:
			return "Unknown";
		}
	}

	public static String getDataTypeAsText(SampleModel sm, ColorModel cm){
		String type = IQMConstants.IMAGE_TYPE_DEFAULT;
		
		if (cm instanceof IndexColorModel) {
			type = IQMConstants.IMAGE_TYPE_INDEXED;
		} else if ((sm.getDataType() == DataBuffer.TYPE_BYTE)
				&& (sm.getNumBands() == 3)) {
			type = IQMConstants.IMAGE_TYPE_RGB;
		} else if ((sm.getDataType() == DataBuffer.TYPE_BYTE)
				&& (sm.getNumBands() == 4)) {
			type = IQMConstants.IMAGE_TYPE_RGBA;
		} else if ((sm.getDataType() == DataBuffer.TYPE_BYTE)
				&& (sm.getNumBands() == 1)) {
			type = IQMConstants.IMAGE_TYPE_8_BIT;
		} else if ((sm.getDataType() == DataBuffer.TYPE_USHORT)
				&& (sm.getNumBands() == 1)) {
			type = IQMConstants.IMAGE_TYPE_16_BIT;
		} else if (sm.getDataType() == DataBuffer.TYPE_FLOAT) {
			type = IQMConstants.IMAGE_TYPE_FLOAT;
		} else if (sm.getDataType() == DataBuffer.TYPE_DOUBLE) {
			type = IQMConstants.IMAGE_TYPE_DOUBLE;
		} else if (sm.getDataType() == DataBuffer.TYPE_INT) {
			type = IQMConstants.IMAGE_TYPE_INTEGER;
		} else if (sm.getDataType() == DataBuffer.TYPE_UNDEFINED) {
			type = IQMConstants.IMAGE_TYPE_UNDEFINED;
		}
		
		return type;
	}

	public static String getColorModelAsText(int cmtype){
		switch(cmtype) {
		case DIRECT:
			return "Direct";
		case COMPONENT:
			return "Component";
		case INDEX:
			return "Indexed";
		default:
			return "Unknown";
		}
	}

	public static String getSampleModelAsText(int smtype){
		switch(smtype) {
		case PIXEL_INTERLEAVED:
			return "Pixel interleaved";
		case BANDED:
			return "Banded";
		case SINGLE_PIXEL_PACKED:
			return "Single pixel packed";
		case MULTI_PIXEL_PACKED:
			return "Muti pixel packed";
		case COMPONENT_SAMPLE:
			return "Component ";
		default:
			return "Unknown";
		}
	}

	public static int getColorModelType(ColorModel cm) {
		int type = DIRECT;
		if(cm instanceof ComponentColorModel){
			type = COMPONENT;
			//System.out.println("Component color model");
		}
		if(cm instanceof DirectColorModel){
			type =  DIRECT;
			//System.out.println("Direct color model");
		}
		if(cm instanceof IndexColorModel) {
			type = INDEX;
			//System.out.println("Indexed color model");
		}
		return type;
	}

	public static int getSampleModelType(SampleModel cm) {
		int type = COMPONENT_SAMPLE;
		if(cm instanceof ComponentSampleModel){
			type = COMPONENT_SAMPLE;

			//System.out.println("Component sample model");
		}
		if(cm instanceof  SinglePixelPackedSampleModel ){
			type = SINGLE_PIXEL_PACKED;
			//System.out.println("single pixel model");
		}
		if(cm instanceof MultiPixelPackedSampleModel) {
			type = MULTI_PIXEL_PACKED;
			//System.out.println("Multi sample model");
		}

		if(cm instanceof BandedSampleModel) {
			type = BANDED;
			//System.out.println("Banded sample model");
		}

		if(cm instanceof PixelInterleavedSampleModel) {
			type = PIXEL_INTERLEAVED;
			//System.out.println("Pixel interleaved sample model");
		}

		return type;
	}


	public static int[][] getRGBStats(ColorModel cm, int pixels[]){
		if(pixels == null) return null;
		int pix;

		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;

		int[] pixelComps = {RED, GREEN, BLUE};
		int stats[][] =  new int[pixelComps.length][3];
		for(int j=0;j<pixelComps.length;j++){
			int sum =0;
			for(int i=0; i<pixels.length;i++){
				switch(pixelComps[j]){
				case RED:
					pix = cm.getRed(pixels[i]);
					break;
				case GREEN:
					pix = cm.getGreen(pixels[i]);
					break;
				case BLUE:
					pix = cm.getBlue(pixels[i]);
					break;
				case ALPHA:
					pix = cm.getAlpha(pixels[i]);
					break;
				default:
					pix =0;
				}
				if(pix > max) max = pix;
				if(pix < min ) min = pix;
				sum += pix;
			}

			double average = sum/pixels.length;
			stats[j][0] = min; stats[j][1] = max; stats[j][2] = (int)average;
		}
		return stats;
	}

	public static int[][] getImageStats(BufferedImage img){
		if(img == null) return null;
		SampleModel sm = img.getSampleModel();
		WritableRaster wr = img.getRaster();
		DataBuffer db = wr.getDataBuffer();
		@SuppressWarnings("unused")
		ColorModel cm = img.getColorModel();
		return ImageInfoUtil.getImageStats(sm, db, new Dimension(img.getWidth(), img.getHeight()));
	}


	/**
	 * @return a two dimensional array. First dimension is the component and second is the stats
	 **/
	public static int[][] getImageStats(SampleModel sm, DataBuffer db, Dimension imageSize){
		int imageWidth = imageSize.width;
		int imageHeight = imageSize.height;
		int pixel[][] = new int[imageHeight*imageWidth][];
		for(int i=0;i<imageHeight;i++){
			for(int j=0; j<imageWidth;j++) {
				int pix[] = null;
				pixel[i*imageWidth+j] =  sm.getPixel(j,i, pix, db);
			}
		}

		int len = pixel[0].length;
		int sum[] = new int[len];
		int max[] = new int[len];
		int min[] = new int[len];
		int[][] imageStats = new int[len][3];
		for(int j=0;j<len;j++) {
			sum[j] =0;
			max[j] = Integer.MIN_VALUE;
			min[j] = Integer.MAX_VALUE;
			for(int i=0;i<pixel.length;i++) {
				int pix = pixel[i][j];
				if(pix > max[j]) max[j] = pix;
				if(pix < min[j] ) min[j] = pix;
				sum[j] += pix;
			}
			//System.out.println("min = "+min[j]+ " max = "+max[j]+ " average = "+ sum[j]/(imageHeight*imageWidth));
			imageStats[j][0] = min[j];
			imageStats[j][1] = max[j];
			imageStats[j][2] = sum[j]/(imageHeight*imageWidth);
		}
		return imageStats;
	}

	public static int[][] getPixelSamples(BufferedImage img) {
		WritableRaster wr = img.getRaster();
		Dimension size = new Dimension(img.getWidth(), img.getHeight());
		return getPixelSamples(wr, size);
	}

	public static int[][] getPixelSamples(Raster raster, Dimension imageSize){
		if((raster == null) || (imageSize ==null)) return null;
		SampleModel sm = raster.getSampleModel();
		DataBuffer db = raster.getDataBuffer();
		int imageWidth = imageSize.width;
		int imageHeight = imageSize.height;
		int totalPix = imageHeight*imageWidth;
		int sample[][] = new int[totalPix][];
		for(int i=0;i<imageHeight;i++){
			for(int j=0; j<imageWidth;j++) {
				int pix[] = null;
				sample[i*imageWidth+j] =  sm.getPixel(j,i, pix, db);
			}
		}
		int pixel[][] = new int[sample[0].length][ totalPix];
		for(int i=0; i<pixel.length;i++){
			for(int j=0; j<totalPix;j++) {
				pixel[i][j] = sample[j][i];
			}
		}
		return pixel;
	}

	public static int[][] getPixelSamples(SampleModel sm, DataBuffer db, Dimension imageSize){
		if((sm == null) ||(db == null) ||(imageSize ==null)) return null;
		int imageWidth = imageSize.width;
		int imageHeight = imageSize.height;
		int totalPix = imageHeight*imageWidth;
		int sample[][] = new int[totalPix][];
		for(int i=0;i<imageHeight;i++){
			for(int j=0; j<imageWidth;j++) {
				int pix[] = null;
				sample[i*imageWidth+j] =  sm.getPixel(j,i, pix, db);
			}
		}
		int pixel[][] = new int[sample[0].length][ totalPix];
		for(int i=0; i<pixel.length;i++){
			for(int j=0; j<totalPix;j++) {
				pixel[i][j] = sample[j][i];
			}
		}
		return pixel;
	}
}
