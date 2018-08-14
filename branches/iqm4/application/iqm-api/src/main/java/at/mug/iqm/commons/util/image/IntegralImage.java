package at.mug.iqm.commons.util.image;

/*
 * #%L
 * Project: IQM - API
 * File: IntegralImage.java
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


import java.awt.Color;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import javax.media.jai.TiledImage;

/**
 * This class represents the integral image (summed area table) of an image.
 * 
 * <p>
 * Reference: P. Viola, M. Jones. Rapid Object Detection Using A Boosted Cascade Of Simple Features. In Proc. CVPR. 2001.
 * 
 * @author Philipp Kainz
 * @since 3.1
 */
public class IntegralImage {

	/**
	 * The container of the integral values
	 */
	private double[][] integralImage;
	/**
	 * Original image width.
	 */
	private int width = 0;
	/**
	 * Original image height.
	 */
	private int height = 0;

	/**
	 * Construct a new integral image.
	 * 
	 * @param img
	 *            the image to create the integral image from
	 */
	public IntegralImage(BufferedImage img) {
		this.width = img.getWidth();

		this.height = img.getHeight();
		// preallocate the buffer
		this.integralImage = new double[this.height][this.width];
		for (int j = 0; j < this.height; j++) {
			// get each RGB value
			double rowSum = 0;
			for (int i = 0; i < this.width; i++) {
				int rgb_val = img.getRGB(i, j);
				float[] hsb = Color.RGBtoHSB((rgb_val >> 16) & 0xff,
						(rgb_val >> 8) & 0xff, rgb_val & 0xff, null);
				rowSum += hsb[2] * 255;

				// set the content
				if (j > 0) {
					this.integralImage[j][i] = this.integralImage[j - 1][i]
							+ rowSum;
				} else {
					this.integralImage[j][i] = rowSum;
				}
			}
		}
	}

	/**
	 * Returns the value at position x,y of the integral image.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return
	 */
	public double getValue(int x, int y) {
		if ((x >= this.width) || (y >= this.height)) {
			return 0;
		} else {
			return this.integralImage[y][x];
		}
	}

	/**
	 * Returns the area spanned by two points.
	 * 
	 * @param p1
	 *            top left corner of the rectangular area
	 * @param p2
	 *            bottom right corner of the rectangular area
	 * @return
	 */
	public double getArea(Point p1, Point p2) {
		return getArea(p1.x, p1.y, p2.x, p2.y);
	}

	/**
	 * Returns the area spanned by two points.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public double getArea(int x1, int y1, int x2, int y2) {
		double A = getValue(x1, y1);
		double D = getValue(x2, y2);
		double B = getValue(x2, y1);
		double C = getValue(x1, y2);
		return A + D - (C + B);
	}

	/**
	 * Returns the integral image as normalized 8-bit grey value image.
	 * 
	 * @return
	 */
	public BufferedImage getAsImage() {
		// DataBufferDouble dbf = new DataBufferDouble(this.width *
		// this.height);
		// int offsets[] = new int[] { 0 };
		// ComponentSampleModel csm = new ComponentSampleModel(
		// DataBuffer.TYPE_DOUBLE, this.width, this.height, 1, this.width,
		// offsets);

		ColorModel cm = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false,
				Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		WritableRaster wr = cm.createCompatibleWritableRaster(width, height);
		SampleModel csm = cm.createCompatibleSampleModel(width, height);

		TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, csm, cm);

		// System.out.println("Width: " + width + ", Height: " + height);
		// System.out.println("Intimage: Width: " + integralImage[0].length
		// + ", Height: " + integralImage.length);

		// compute min and max value for rescaling
		double minVal = integralImage[0][0];
		double maxVal = integralImage[0][0];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				double val = integralImage[j][i];
				minVal = Math.min(minVal, val);
				maxVal = Math.max(maxVal, val);
			}
		}

		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				double foo = (integralImage[j][i] - minVal) / (maxVal - minVal);

				wr.setPixel(i, j, new int[] { (int) (foo * 255) });
			}
		}

		ti.setData(wr);
		return ti.getAsBufferedImage();
	}
}
