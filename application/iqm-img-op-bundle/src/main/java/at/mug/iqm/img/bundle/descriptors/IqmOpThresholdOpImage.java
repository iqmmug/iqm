package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpThresholdOpImage.java
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
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

import at.mug.iqm.commons.util.ColorConverter;

/**
 * @author Ahammer
 * @since   2009 04
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpThresholdOpImage extends PointOpImage {

	private RenderedImage ri;
	private int color;
	private int p0;
	private int p1;
	private int p2;
	private int p3;
	private int p4;
	private int p5;
	private int linkSlider;
	private int binarize;

	private int preset;

	public IqmOpThresholdOpImage(RenderedImage ri,
			int color, // 0grey 1RGB 2HSV 3HLS 4CIELAB 5CIELUV 6XYZ
			int p0, int p1, int p2, int p3, int p4, int p5, int linkSlider,
			int binarize, // nur 0 oder 1
			int preset, // 0 bis 8 0 is without preset
			ImageLayout layout, RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.color = color;
		this.ri = ri;
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.p5 = p5;
		this.linkSlider = linkSlider; // not used here in IqmThreshold
		this.binarize = binarize;
		this.preset = preset; // not used here in IqmThreshold
	}

	@Override
	public Raster computeTile(int x, int y) {
		// System.out.println("IqmThresholdOpImage parameter: "
		// +p0+" "+p1+" "+p2+" "+p3+" "+p4+" "+p5+" "+binarize);
		// System.out.println("IqmThresholdOpImage parameter: x, y  " +x+
		// "  "+y);
		// if (ri == null) System.out.println("IqmThresholdOpImage ri == null");
		Raster r = this.ri.getTile(x, y);
		// if (r == null)
		// System.out.println("IqmThresholdOpImage Raster r == null");

		// if (binarize == 1) System.out.println("Raster: binarize == 1");

		// remove alpha channel
		String type = "";
		double typeGreyMax = 0.0d;
		switch (r.getDataBuffer().getDataType()) {
		case DataBuffer.TYPE_BYTE:
			if (r.getNumBands() == 1)
				type = "8 bit";
			if (r.getNumBands() == 3)
				type = "RGB";
			typeGreyMax = 255.0d;
			break;
		case DataBuffer.TYPE_USHORT:
			type = "16 bit";
			typeGreyMax = 65535.0d;
			break;
		default:
			type = "default";
		}
		
		

		int numBands = r.getNumBands();
		int minX = r.getMinX();
		int minY = r.getMinY();
		int width = r.getWidth();
		int height = r.getHeight();
		// System.out.println("IqmThresholdOpImage parameter: x, y, imgWidth, imgHeight "
		// +x+ "  "+y+ " "+imgWidth+"  "+imgHeight);

		WritableRaster wr = r.createCompatibleWritableRaster(minX, minY, width,
				height);

		// if (color == 0) //Grey do nothing
		// if (color == 1) //RGB do nothing
		if (color == 2)
			r = ColorConverter.RGBtoHSV(r); // HSV
		if (color == 3)
			r = ColorConverter.RGBtoHLS(r); // HLS
		if (color == 4)
			r = ColorConverter.RGBtoCIELAB(r); // CIELAB
		if (color == 5)
			r = ColorConverter.RGBtoCIELUV(r); // CIELUV
		if (color == 6)
			r = ColorConverter.RGBtoCIEXYZ(r); // XYZ

		double[] low = { p0, p2, p4 };
		double[] high = { p1, p3, p5 };
		if (numBands == 1) {
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					int r0 = r.getSample(i + minX, j + minY, 0);
					if (r0 < low[0]) {
						r0 = 0;
					} else if (r0 > high[0]) {
						r0 = 0;
					} else {
						// original grey values
						// or
						if (binarize == 1) {
							r0 = (int) typeGreyMax;
						}
					}
					wr.setSample(i + minX, j + minY, 0, r0);
				}// i
			}// j
		}// numBands == 1
		if (numBands == 3) {
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					int r0 = r.getSample(i + minX, j + minY, 0);
					int r1 = r.getSample(i + minX, j + minY, 1);
					int r2 = r.getSample(i + minX, j + minY, 2);
					if ((r0 < low[0]) || (r1 < low[1]) || (r2 < low[2])) { // black
						r0 = 0;
						r1 = 0;
						r2 = 0;
					} else if ((r0 > high[0]) || (r1 > high[1])
							|| (r2 > high[2])) { // black
						r0 = 0;
						r1 = 0;
						r2 = 0;
					} else { // not black
						if (binarize == 1) { // white
							r0 = (int) typeGreyMax;
							r1 = (int) typeGreyMax;
							r2 = (int) typeGreyMax;
						}// if binarize == 1
						if (binarize == 0) { // original grey values
							// if (color == 1){ //RGB
							// //do nothing
							// }
							if (color == 2) { // HSV
								float[] out = ColorConverter.HSVtoRGB(r0, r1,
										r2);
								r0 = (int) out[0];
								r1 = (int) out[1];
								r2 = (int) out[2];
							}
							if (color == 3) { // HLS
								float[] out = ColorConverter.HLStoRGB(r0, r1,
										r2);
								r0 = (int) out[0];
								r1 = (int) out[1];
								r2 = (int) out[2];
							}
							if (color == 4) { // CIELAB
								float[] out = ColorConverter.CIELABtoRGB(r0,
										r1, r2);
								r0 = (int) out[0];
								r1 = (int) out[1];
								r2 = (int) out[2];
							}
							if (color == 5) { // CIELUV
								float[] out = ColorConverter.CIELUVtoRGB(r0,
										r1, r2);
								r0 = (int) out[0];
								r1 = (int) out[1];
								r2 = (int) out[2];
							}
							if (color == 6) { // XYZ
								float[] out = ColorConverter.CIEXYZtoRGB(r0,
										r1, r2);
								r0 = (int) out[0];
								r1 = (int) out[1];
								r2 = (int) out[2];
							}

						}// if binarize == 0

					}// not black
					wr.setSample(i + minX, j + minY, 0, r0);
					wr.setSample(i + minX, j + minY, 1, r1);
					wr.setSample(i + minX, j + minY, 2, r2);
				} // i
			} // j
		} // numBands == 3

		return wr;
	}
}
