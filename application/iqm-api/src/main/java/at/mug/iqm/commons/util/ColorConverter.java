package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: ColorConverter.java
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

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * This class consists of static methods for color space conversions for pixel
 * values. Take care of input and/or output range [0,..,1] or [0,..,255] or
 * [0,..,360] or [0,..,100].
 * 
 * @author Helmut Ahammer
 */

public final class ColorConverter {

	// there are two methods rgb2hsb and rgb2hsv, which should give identical
	// results
	/**
	 * RGB to HSB tested input and output [0,..,255];
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return float[] one pixel HSV values;
	 */
	public static float[] rgb2hsb(float r, float g, float b) {
		float[] hsbvals = new float[3];
		Color.RGBtoHSB((int) r, (int) g, (int) b, hsbvals);
		hsbvals[0] *= 255;
		hsbvals[1] *= 255;
		hsbvals[2] *= 255;
		return hsbvals;
	}

	/**
	 * RGB to HSB
	 * 
	 * @param raster
	 *            3bands RGB
	 * @return raster 3bands HSV
	 */
	public static Raster RGBtoHSV(Raster raster) {
		int minX = raster.getMinX();
		int minY = raster.getMinY();
		int width = raster.getWidth();
		int height = raster.getHeight();
		WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY,
				width, height);

		float[] rgb = new float[3];
		float[] hsv = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x + minX, y + minY, rgb);
				hsv = ColorConverter.rgb2hsb(rgb[0], rgb[1], rgb[2]);
				// rgb = new float[]{0,0,0};
				wr.setPixel(x + minX, y + minY, hsv);
			}
		}
		return wr;
	}

	// --------------------------------------------------------------------------------------
	/**
	 * RGB to HLS tested OK 5.1.2010 input and output [0,..,255] This is from:
	 * Wilhelm BURGER Mark J. BURGE, Digital Image Processing, An Algorithmic
	 * Introduction using Java
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return float[] one pixel HLS values;
	 */
	public static float[] RGBtoHLS(float r, float g, float b) {
		r = (r / 255f);
		g = (g / 255f);
		b = (b / 255f);
		// calculation [0,..,1]
		float cHi = Math.max(r, Math.max(g, b)); // highest color
		float cLo = Math.min(r, Math.min(g, b)); // lowest color
		float cRng = cHi - cLo; // color Range

		// luminance
		float lum = (cHi + cLo) / 2;

		// saturation
		float sat = 0;
		if (0 < lum && lum < 1) {
			float d = (lum <= 0.5f) ? lum : (1 - lum);
			sat = 0.5f * cRng / d;
		}
		// hue
		float hue = 0;
		if (cHi > 0 && cRng > 0) {
			float rr = (cHi - r) / cRng;
			float gg = (cHi - g) / cRng;
			float bb = (cHi - b) / cRng;
			float hh;

			if (r == cHi)
				hh = bb - gg;
			else if (g == cHi)
				hh = rr - bb + 2.0f;
			else
				hh = gg - rr + 4.0f;

			if (hh < 0)
				hh = hh + 6;
			hue = hh / 6;
		}
		hue = hue * 255;
		lum = lum * 255;
		sat = sat * 255;
		return new float[] { hue, lum, sat };
	}

	/**
	 * RGB to HLS
	 * 
	 * @param raster
	 *            3bands RGB
	 * @return raster 3bands HLS
	 */
	public static Raster RGBtoHLS(Raster raster) {
		int minX = raster.getMinX();
		int minY = raster.getMinY();
		int width = raster.getWidth();
		int height = raster.getHeight();
		WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY,
				width, height);

		float[] rgb = new float[3];
		float[] hls = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x + minX, y + minY, rgb);
				hls = ColorConverter.RGBtoHLS(rgb[0], rgb[1], rgb[2]);
				// rgb = new float[]{0,0,0};
				wr.setPixel(x + minX, y + minY, hls);
			}
		}
		return wr;
	}

	// --------------------------------------------------------------------------------------

	/**
	 * HLS to RGB tested OK 5.1.2010 input and output [0,..,255] This is from:
	 * Wilhelm BURGER Mark J. BURGE, Digital Image Processing, An Algorithmic
	 * Introduction using Java
	 * 
	 * @param h
	 * @param l
	 * @param s
	 * @return float[] one pixel RGB values;
	 **/
	public static float[] HLStoRGB(float h, float l, float s) {
		h = (h / 255f);
		l = (l / 255f);
		s = (s / 255f);
		// calculation [0,..,1]
		float r = 0, g = 0, b = 0;
		if (l <= 0)
			r = g = b = 0; // black
		else if (l >= 1)
			r = g = b = 1; // white
		else {
			float hh = (6 * h) % 6; // sollte eventuell /6 sein?
			int c1 = (int) hh;
			float c2 = hh - c1;
			float d = (l <= 0.5f) ? (s * l) : (s * (1 - l));
			float w = l + d;
			float x = l - d;
			float y = w - (w - x) * c2;
			float z = x + (w - x) * c2;
			switch (c1) {
			case 0:
				r = w;
				g = z;
				b = x;
				break;
			case 1:
				r = y;
				g = w;
				b = x;
				break;
			case 2:
				r = x;
				g = w;
				b = z;
				break;
			case 3:
				r = x;
				g = y;
				b = w;
				break;
			case 4:
				r = z;
				g = x;
				b = w;
				break;
			case 5:
				r = w;
				g = x;
				b = y;
				break;
			}
		}
		r = r * 255;
		g = g * 255;
		b = b * 255;
		// int N = 256;
		// r = Math.min(Math.round(r*N), N-1);
		// g = Math.min(Math.round(g*N), N-1);
		// b = Math.min(Math.round(b*N), N-1);
		return new float[] { r, g, b };
	}

	/**
	 * HLS to RGB
	 * 
	 * @param raster
	 *            3bands HLS
	 * @return raster 3bands RGB
	 */
	public static Raster HLStoRGB(Raster raster) {
		int minX = raster.getMinX();
		int minY = raster.getMinY();
		int width = raster.getWidth();
		int height = raster.getHeight();
		WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY,
				width, height);

		float[] rgb = new float[3];
		float[] hls = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x + minX, y + minY, hls);
				rgb = ColorConverter.HLStoRGB(hls[0], hls[1], hls[2]);
				// rgb = new float[]{0,0,0};
				wr.setPixel(x + minX, y + minY, rgb);
			}
		}
		return wr;
	}

	// --------------------------------------------------------------------------------------
	/**
	 * HSV to RGB tested OK 5.1.2010 input and output [0,..,255] This is from:
	 * Wilhelm BURGER Mark J. BURGE, Digital Image Processing, An Algorithmic
	 * Introduction using Java
	 * 
	 * @param h
	 * @param s
	 * @param v
	 * @return float[] one pixel RGB values;
	 */

	public static float[] HSVtoRGB(float h, float s, float v) {
		h = (h / 255f);
		v = (v / 255f);
		s = (s / 255f);
		// calculation [0,..,1]
		float r = 0, g = 0, b = 0;
		float hh = (6 * h) % 6; // sollte eventuell /6 sein?
		int c1 = (int) hh;
		float c2 = hh - c1;
		float x = (1 - s) * v;
		float y = (1 - (s * c2)) * v;
		float z = (1 - (s * (1 - c2))) * v;
		;
		switch (c1) {
		case 0:
			r = v;
			g = z;
			b = x;
			break;
		case 1:
			r = y;
			g = v;
			b = x;
			break;
		case 2:
			r = x;
			g = v;
			b = z;
			break;
		case 3:
			r = x;
			g = y;
			b = v;
			break;
		case 4:
			r = z;
			g = x;
			b = v;
			break;
		case 5:
			r = v;
			g = x;
			b = y;
			break;
		}

		r = r * 255;
		g = g * 255;
		b = b * 255;
		// int N = 256;
		// r = Math.min(Math.round(r*N), N-1);
		// g = Math.min(Math.round(g*N), N-1);
		// b = Math.min(Math.round(b*N), N-1);
		return new float[] { r, g, b };
	}

	/**
	 * HSV to RGB
	 * 
	 * @param raster
	 *            3bands HSV
	 * @return raster 3bands RGB
	 */
	public static Raster HSVtoRGB(Raster raster) {
		int minX = raster.getMinX();
		int minY = raster.getMinY();
		int width = raster.getWidth();
		int height = raster.getHeight();
		WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY,
				width, height);

		float[] rgb = new float[3];
		float[] hsv = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x + minX, y + minY, hsv);
				rgb = ColorConverter.HSVtoRGB(hsv[0], hsv[1], hsv[2]);
				// rgb = new float[]{0,0,0};
				wr.setPixel(x + minX, y + minY, rgb);
			}
		}
		return wr;
	}

	// --------------------------------------------------------------------------------------
	/**
	 * RGB to CIEXYZ
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return float[] one pixel CIEXYZ values; input and output [0,..,255]
	 *         tested 8.1.2010
	 */
	public static float[] RGBtoCIEXYZ(float r, float g, float b) {
		r = r / 255;
		g = g / 255;
		b = b / 255;
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		float[] ciexyz = cs.toCIEXYZ(new float[] { r, g, b });
		ciexyz[0] = ciexyz[0] * 255;
		ciexyz[1] = ciexyz[1] * 255;
		ciexyz[2] = ciexyz[2] * 255;
		return ciexyz;
	}

	/**
	 * RGB to CIEXYZ
	 * 
	 * @param raster
	 *            3bands RGB
	 * @return raster 3bands CIEXYZ input and output [0,..,255]
	 */
	public static Raster RGBtoCIEXYZ(Raster raster) {
		int minX = raster.getMinX();
		int minY = raster.getMinY();
		int width = raster.getWidth();
		int height = raster.getHeight();
		WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY,
				width, height);

		float[] rgb = new float[3];
		float[] ciexyz = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x + minX, y + minY, rgb);
				ciexyz = ColorConverter.RGBtoCIEXYZ(rgb[0], rgb[1], rgb[2]);
				// rgb = new float[]{0,0,0};
				wr.setPixel(x + minX, y + minY, ciexyz);
			}
		}
		return wr;
	}

	// --------------------------------------------------------------------------------------

	/**
	 * CIEXYZ to RGB
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return float[] one pixel RGB values; input and output [0,..,255] tested
	 *         8.1.2010
	 */
	public static float[] CIEXYZtoRGB(float x, float y, float z) {
		x = x / 255;
		y = y / 255;
		z = z / 255;
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
		float[] rgb = cs.toRGB(new float[] { x, y, z });
		// System.out.println("IqmColorConvert: rgb "+ rgb[0] +"  "+ rgb[1]+
		// "  " +rgb[2]);
		rgb[0] = rgb[0] * 255;
		rgb[1] = rgb[1] * 255;
		rgb[2] = rgb[2] * 255;
		return rgb;
	}

	/**
	 * CIEXYZ to RGB
	 * 
	 * @param raster
	 *            3bands CIEXYZ
	 * @return raster 3bands RGB
	 */
	public static Raster CIEXYZtoRGB(Raster raster) {
		int minX = raster.getMinX();
		int minY = raster.getMinY();
		int width = raster.getWidth();
		int height = raster.getHeight();
		WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY,
				width, height);

		float[] rgb = new float[3];
		float[] ciexyz = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x + minX, y + minY, ciexyz);
				rgb = ColorConverter.CIEXYZtoRGB(ciexyz[0], ciexyz[1],
						ciexyz[2]);
				// rgb = new float[]{0,0,0};
				wr.setPixel(x + minX, y + minY, rgb);
			}
		}
		return wr;
	}

	// --------------------------------------------------------------------------------------

	/**
	 * These are helper routines for CIELAB and CIELUV Parts are from: Wilhelm
	 * BURGER Mark J. BURGE, Digital Image Processing, An Algorithmic
	 * Introduction using Java
	 */
	private static double f1(double t) {
		if (t > 0.008856) {
			return Math.pow(t, 1.0 / 3);
		} else
			return (7.787 * t) + (16.0 / 116);
	}

	private static double f2(double t) {
		double c3 = Math.pow(t, 3.0);
		if (c3 > 0.008856) {
			return c3;
		} else
			return (t - 16.0 / 116) / 7.787;
	}

	// D65 reference illuminant coordinates
	private final static double Xref = 0.95047;
	private final static double Yref = 1.0;
	private final static double Zref = 1.08883;

	// for CIELUV calculations
	private final static double c4 = Math.pow(6d / 29d, 3); // = 0.008856
	private final static double uuRef = 4 * Xref
			/ (Xref + 15 * Yref + 3 * Zref); // = 0.19784 D50 = 0.2009?;
	private final static double vvRef = 9 * Yref
			/ (Xref + 15 * Yref + 3 * Zref); // = 0.4610;

	// --------------------------------------------------------------------------------------
	/**
	 * RGB to CIELAB
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return float[] one pixel CIELAB values; This is from: Wilhelm BURGER
	 *         Mark J. BURGE, Digital Image Processing, An Algorithmic
	 *         Introduction using Java not yet tested
	 */
	public static float[] RGBtoCIELAB(float r, float g, float b) {
		r = r / 255;
		g = g / 255;
		b = b / 255;
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		float[] ciexyz = cs.toCIEXYZ(new float[] { r, g, b }); // CIEXYZ
		// CIEXYZ to CIELAB
		double xx = f1(ciexyz[0] / Xref);
		double yy = f1(ciexyz[1] / Yref);
		double zz = f1(ciexyz[2] / Zref);
		float[] cielab = new float[3];
		cielab[0] = (float) (116 * yy - 16); // [0..100;]
		cielab[1] = (float) (500 * (xx - yy)); // [-128...+127;]
		cielab[2] = (float) (200 * (yy - zz)); // [-128...+127];
		cielab[0] = cielab[0] * 2.5f; // [0...255]
		cielab[1] = cielab[1] + 128; // [0...255]
		cielab[2] = cielab[2] + 128; // [0...255]
		// System.out.println("IqmColorConvert: cielab "+ cielab[0] +"  "+
		// cielab[1]+ "  " + cielab[2]);
		return cielab;
	}

	/**
	 * RGB to CIELAB
	 * 
	 * @param raster
	 *            3bands RGB
	 * @return raster 3bands CIELAB
	 */
	public static Raster RGBtoCIELAB(Raster raster) {
		int minX = raster.getMinX();
		int minY = raster.getMinY();
		int width = raster.getWidth();
		int height = raster.getHeight();
		WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY,
				width, height);

		float[] rgb = new float[3];
		float[] cielab = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x + minX, y + minY, rgb);
				cielab = ColorConverter.RGBtoCIELAB(rgb[0], rgb[1], rgb[2]);
				// rgb = new float[]{0,0,0};
				wr.setPixel(x + minX, y + minY, cielab);
			}
		}
		return wr;
	}

	// --------------------------------------------------------------------------------------

	/**
	 * CIELAB to RGB
	 * 
	 * @param l
	 * @param a
	 * @param b
	 * @return float[] one pixel RGB values; input and output [0,..,255] This is
	 *         from: Wilhelm BURGER Mark J. BURGE, Digital Image Processing, An
	 *         Algorithmic Introduction using Java not yet tested
	 */
	public static float[] CIELABtoRGB(float l, float a, float b) {
		l = l / 2.5f; // [0..100]
		a = a - 128; // [-128..+127]
		b = b - 128; // [-128..+127]
		// CIELAB to CIEXYZ
		double yy = (l + 16) / 116;
		float[] ciexyz = new float[3];
		ciexyz[0] = (float) (Xref * f2(a / 500 + yy));
		ciexyz[1] = (float) (Yref * f2(yy));
		ciexyz[2] = (float) (Zref * f2(yy - b / 200));

		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
		float[] rgb = cs.toRGB(ciexyz);
		rgb[0] = rgb[0] * 255;
		rgb[1] = rgb[1] * 255;
		rgb[2] = rgb[2] * 255;
		return rgb;
	}

	/**
	 * CIELAB to RGB
	 * 
	 * @param raster
	 *            3bands CIELAB
	 * @return raster 3bands RGB
	 */
	public static Raster CIELABtoRGB(Raster raster) {
		int minX = raster.getMinX();
		int minY = raster.getMinY();
		int width = raster.getWidth();
		int height = raster.getHeight();
		WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY,
				width, height);

		float[] rgb = new float[3];
		float[] cielab = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x + minX, y + minY, cielab);
				rgb = ColorConverter.CIELABtoRGB(cielab[0], cielab[1],
						cielab[2]);
				// rgb = new float[]{0,0,0};
				wr.setPixel(x + minX, y + minY, rgb);
			}
		}
		return wr;
	}

	// --------------------------------------------------------------------------------------
	/**
	 * RGB to CIELUV
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return float[] one pixel CIELUV values; not yet tested
	 */
	public static float[] RGBtoCIELUV(float r, float g, float b) {
		r = r / 255;
		g = g / 255;
		b = b / 255;
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		float[] ciexyz = cs.toCIEXYZ(new float[] { r, g, b }); // CIEXYZ
		// CIEXYZ to CIELUV
		double yy = ciexyz[1] / Yref;
		float[] cieluv = new float[3];
		if (yy > c4) {
			cieluv[0] = (float) (116 * Math.pow(yy, 1.0 / 3) - 16); // [0..100;]
		} else {
			cieluv[0] = (float) (Math.pow(29.0 / 3, 3) * yy); // [0..100;]
		}
		float uu = 4 * ciexyz[0] / (ciexyz[0] + 15 * ciexyz[1] + 3 * ciexyz[2]);
		float vv = 9 * ciexyz[1] / (ciexyz[0] + 15 * ciexyz[1] + 3 * ciexyz[2]);

		cieluv[1] = (float) (13f * cieluv[0] * (uu - uuRef)); // [-134...220;]
		cieluv[2] = (float) (13f * cieluv[0] * (vv - vvRef)); // [-140...122];
		// System.out.println("IqmColorConvert: cieluv "+ cieluv[0] +"  "+
		// cieluv[1]+ "  " + cieluv[2]);
		cieluv[0] = cieluv[0] * 2.5f; // [0...255]
		cieluv[1] = (cieluv[1] + 134) / 354 * 255; // [0...255]
		cieluv[2] = (cieluv[2] + 140) / 262 * 255; // [0...255]
		return cieluv;
	}

	/**
	 * RGB to CIELUV
	 * 
	 * @param raster
	 *            3bands RGB
	 * @return raster 3bands CIELUV
	 */
	public static Raster RGBtoCIELUV(Raster raster) {
		int minX = raster.getMinX();
		int minY = raster.getMinY();
		int width = raster.getWidth();
		int height = raster.getHeight();
		WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY,
				width, height);

		float[] rgb = new float[3];
		float[] cieluv = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x + minX, y + minY, rgb);
				cieluv = ColorConverter.RGBtoCIELUV(rgb[0], rgb[1], rgb[2]);
				// rgb = new float[]{0,0,0};
				wr.setPixel(x + minX, y + minY, cieluv);
			}
		}
		return wr;
	}

	// --------------------------------------------------------------------------------------

	/**
	 * CIELUV to RGB
	 * 
	 * @param l
	 * @param u
	 * @param v
	 * @return float[] one pixel RGB values; input and output [0,..,255] not yet
	 *         tested
	 */
	public static float[] CIELUVtoRGB(float l, float u, float v) {
		l = l / 2.5f; // [0..100]
		u = u / 255f * 354f - 134; // [-134..+220]
		v = v / 255f * 262f - 140; // [-140..+122]
		// CIELUV to CIEXYZ
		float uu = (float) (u / (13 * l) + uuRef);
		float vv = (float) (v / (13 * l) + vvRef);
		float[] ciexyz = new float[3];
		// if (l <= 8){
		// ciexyz[0] = (float) (Yref*l*Math.pow(3.0/29, 3));
		// }else{
		// ciexyz[0] = (float) (Yref*Math.pow((l+16/116), 3));
		// }
		// ciexyz[1] = ciexyz[0]*((9.0f*uu)/(4.0f*vv));
		// ciexyz[2] = ciexyz[0]*((12 - 3*uu - 20*vv)/4 * vv);
		//

		ciexyz[1] = (float) (Yref * Math.pow(((l + 16) / 116), 3));
		ciexyz[0] = ((-9.0f) * ciexyz[1] * uu) / ((uu - 4.0f) * vv - uu * vv);
		ciexyz[2] = (9.0f * ciexyz[1] - 15 * vv * ciexyz[1] - vv * ciexyz[0])
				/ (3 * vv);

		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
		float[] rgb = cs.toRGB(ciexyz);
		rgb[0] = rgb[0] * 255;
		rgb[1] = rgb[1] * 255;
		rgb[2] = rgb[2] * 255;
		return rgb;
	}

	/**
	 * CIELUV to RGB
	 * 
	 * @param raster
	 *            3bands CIELUV
	 * @return raster 3bands RGB
	 */
	public static Raster CIELUVtoRGB(Raster raster) {
		int minX = raster.getMinX();
		int minY = raster.getMinY();
		int width = raster.getWidth();
		int height = raster.getHeight();
		WritableRaster wr = raster.createCompatibleWritableRaster(minX, minY,
				width, height);

		float[] rgb = new float[3];
		float[] cieluv = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x + minX, y + minY, cieluv);
				rgb = ColorConverter.CIELUVtoRGB(cieluv[0], cieluv[1],
						cieluv[2]);
				// rgb = new float[]{0,0,0};
				wr.setPixel(x + minX, y + minY, rgb);
			}
		}
		return wr;
	}

	// --------------------------------------------------------------------------------------

	// RGB to HSL
	// not yet tested
	public static float[] rgb2hsl(float r, float g, float b) {

		float var_R = (r / 255f);
		float var_G = (g / 255f);
		float var_B = (b / 255f);

		float var_Min; // Min. value of RGB
		float var_Max; // Max. value of RGB
		float del_Max; // Delta RGB value

		if (var_R > var_G) {
			var_Min = var_G;
			var_Max = var_R;
		} else {
			var_Min = var_R;
			var_Max = var_G;
		}

		if (var_B > var_Max)
			var_Max = var_B;
		if (var_B < var_Min)
			var_Min = var_B;

		del_Max = var_Max - var_Min;

		float H = 0, S, L;
		L = (var_Max + var_Min) / 2f;

		if (del_Max == 0) {
			H = 0;
			S = 0;
		} // gray
		else { // Chroma
			if (L < 0.5)
				S = del_Max / (var_Max + var_Min);
			else
				S = del_Max / (2 - var_Max - var_Min);

			float del_R = (((var_Max - var_R) / 6f) + (del_Max / 2f)) / del_Max;
			float del_G = (((var_Max - var_G) / 6f) + (del_Max / 2f)) / del_Max;
			float del_B = (((var_Max - var_B) / 6f) + (del_Max / 2f)) / del_Max;

			if (var_R == var_Max)
				H = del_B - del_G;
			else if (var_G == var_Max)
				H = (1 / 3f) + del_R - del_B;
			else if (var_B == var_Max)
				H = (2 / 3f) + del_G - del_R;
			if (H < 0)
				H += 1;
			if (H > 1)
				H -= 1;
		}

		return new float[] { (360 * H), (S * 100), (L * 100) };
	}

	// --------------------------------------------------------------------------------------

	// RGB to HSV
	// not yet tested
	public static float[] rgb2hsv(float r, float g, float b) {

		float min; // Min. value of RGB
		float max; // Max. value of RGB
		float delMax; // Delta RGB value

		if (r > g) {
			min = g;
			max = r;
		} else {
			min = r;
			max = g;
		}
		if (b > max)
			max = b;
		if (b < min)
			min = b;

		delMax = max - min;

		float H = 0, S;
		float V = max;

		if (delMax == 0) {
			H = 0;
			S = 0;
		} else {
			S = delMax / 255f;
			if (r == max)
				H = ((g - b) / delMax) * 60;
			else if (g == max)
				H = (2 + (b - r) / delMax) * 60;
			else if (b == max)
				H = (4 + (r - g) / delMax) * 60;
		}
		return new float[] { (H), (S * 100), (V * 100) };
	}

	// --------------------------------------------------------------------------------------

	// RGB to xyY
	// not yet tested
	public static float[] rgb2xyY(float R, float G, float B) {
		// http://www.brucelindbloom.com

		@SuppressWarnings("unused")
		float rf, gf, bf;
		float r, g, b, X, Y, Z;

		// RGB to XYZ
		r = R / 255.f; // R 0..1
		g = G / 255.f; // G 0..1
		b = B / 255.f; // B 0..1

		if (r <= 0.04045)
			r = r / 12;
		else
			r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

		if (g <= 0.04045)
			g = g / 12;
		else
			g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

		if (b <= 0.04045)
			b = b / 12;
		else
			b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

		X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
		Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
		Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

		float x;
		float y;

		float sum = X + Y + Z;
		if (sum != 0) {
			x = X / sum;
			y = Y / sum;
		} else {
			float Xr = 0.964221f; // reference white
			float Yr = 1.0f;
			float Zr = 0.825211f;

			x = Xr / (Xr + Yr + Zr);
			y = Yr / (Xr + Yr + Zr);
		}

		return new float[] { (float) (255 * x + .5), (float) (255 * y + .5),
				(float) (255 * Y + .5) };

	}

	// --------------------------------------------------------------------------------------

	// RGB to XYZ
	// not yet tested
	public static float[] rgb2xyz(float R, float G, float B) {
		@SuppressWarnings("unused")
		float rf, gf, bf;
		float r, g, b, X, Y, Z;

		r = R / 255.f; // R 0..1
		g = G / 255.f; // G 0..1
		b = B / 255.f; // B 0..1

		if (r <= 0.04045)
			r = r / 12;
		else
			r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

		if (g <= 0.04045)
			g = g / 12;
		else
			g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

		if (b <= 0.04045)
			b = b / 12;
		else
			b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

		X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
		Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
		Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

		// xyz[1] = (float) (255*Y + .5);
		// xyz[0] = (float) (255*X + .5);
		// xyz[2] = (float) (255*Z + .5);
		return new float[] { (float) (255 * X + .5), (float) (255 * Y + .5),
				(float) (255 * Z + .5) };
	}

	// --------------------------------------------------------------------------------------

	// RGB to LAB
	// not yet tested
	public static float[] rgb2lab(float R, float G, float B) {
		// http://www.brucelindbloom.com

		float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
		float Ls, as, bs;
		float eps = 216.f / 24389.f;
		float k = 24389.f / 27.f;

		float Xr = 0.964221f; // reference white D50
		float Yr = 1.0f;
		float Zr = 0.825211f;

		// RGB to XYZ
		r = R / 255.f; // R 0..1
		g = G / 255.f; // G 0..1
		b = B / 255.f; // B 0..1

		// assuming sRGB (D65)
		if (r <= 0.04045)
			r = r / 12;
		else
			r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

		if (g <= 0.04045)
			g = g / 12;
		else
			g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

		if (b <= 0.04045)
			b = b / 12;
		else
			b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

		X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
		Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
		Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

		// XYZ to Lab
		xr = X / Xr;
		yr = Y / Yr;
		zr = Z / Zr;

		if (xr > eps)
			fx = (float) Math.pow(xr, 1 / 3.);
		else
			fx = (float) ((k * xr + 16.) / 116.);

		if (yr > eps)
			fy = (float) Math.pow(yr, 1 / 3.);
		else
			fy = (float) ((k * yr + 16.) / 116.);

		if (zr > eps)
			fz = (float) Math.pow(zr, 1 / 3.);
		else
			fz = (float) ((k * zr + 16.) / 116);

		Ls = (116 * fy) - 16;
		as = 500 * (fx - fy);
		bs = 200 * (fy - fz);

		// lab[0] = (float) (2.55*Ls + .5);
		// lab[1] = (float) (as + .5);
		// lab[2] = (float) (bs + .5);
		return new float[] { (float) (2.55 * Ls + .5), (float) (as + .5),
				(float) (bs + .5) };
	}

	// --------------------------------------------------------------------------------------

	// RGB to LUV
	// not yet tested
	public static float[] rgb2luv(float R, float G, float B) {
		// http://www.brucelindbloom.com

		@SuppressWarnings("unused")
		float rf, gf, bf;
		@SuppressWarnings("unused")
		float r, g, b, X_, Y_, Z_, X, Y, Z, fx, fy, fz, xr, yr, zr;
		float L;
		float eps = 216.f / 24389.f;
		float k = 24389.f / 27.f;

		float Xr = 0.964221f; // reference white D50
		float Yr = 1.0f;
		float Zr = 0.825211f;

		// RGB to XYZ

		r = R / 255.f; // R 0..1
		g = G / 255.f; // G 0..1
		b = B / 255.f; // B 0..1

		// assuming sRGB (D65)
		if (r <= 0.04045)
			r = r / 12;
		else
			r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

		if (g <= 0.04045)
			g = g / 12;
		else
			g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

		if (b <= 0.04045)
			b = b / 12;
		else
			b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

		X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
		Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
		Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

		// XYZ to Luv

		float u, v, u_, v_, ur_, vr_;

		u_ = 4 * X / (X + 15 * Y + 3 * Z);
		v_ = 9 * Y / (X + 15 * Y + 3 * Z);

		ur_ = 4 * Xr / (Xr + 15 * Yr + 3 * Zr);
		vr_ = 9 * Yr / (Xr + 15 * Yr + 3 * Zr);

		yr = Y / Yr;

		if (yr > eps)
			L = (float) (116 * Math.pow(yr, 1 / 3.) - 16);
		else
			L = k * yr;

		u = 13 * L * (u_ - ur_);
		v = 13 * L * (v_ - vr_);

		// luv[0] = (float) (2.55*L + .5);
		// luv[1] = (float) (u + .5);
		// luv[2] = (float) (v + .5);
		return new float[] { (float) (2.55 * L + .5), (float) (u + .5),
				(float) (v + .5) };
	}

	// --------------------------------------------------------------------------------------

	// RGB to YCbCr
	// not yet tested
	public static float[] rgb2ycbcr(float r, float g, float b) {
		float y = (float) (0.299 * r + 0.587 * g + 0.114 * b);
		float cb = (float) (-0.16874 * r - 0.33126 * g + 0.50000 * b);
		float cr = (float) (0.50000 * r - 0.41869 * g - 0.08131 * b);

		return new float[] { y, cb, cr };
	}

	// --------------------------------------------------------------------------------------

	// RGB to YUV
	// not yet tested
	public static float[] rgb2yuv(float r, float g, float b) {
		float y = (float) (0.299 * r + 0.587 * g + 0.114 * b);
		float u = ((b - y) * 0.492f);
		float v = ((r - y) * 0.877f);

		return new float[] { y, u, v };
	}

	// --------------------------------------------------------------------------------------

	/**
	 * RGB to HMMD not yet tested
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return float[] HMMD values;
	 */
	public static float[] rgb2hmmd(float r, float g, float b) {

		float max = Math.max(Math.max(r, g), Math.max(g, b));
		float min = Math.min(Math.min(r, g), Math.min(g, b));
		float diff = (max - min);
		@SuppressWarnings("unused")
		float sum = (float) ((max + min) / 2.);

		float hue = 0;
		if (diff == 0)
			hue = 0;
		else if (r == max && (g - b) > 0)
			hue = 60 * (g - b) / (max - min);
		else if (r == max && (g - b) <= 0)
			hue = 60 * (g - b) / (max - min) + 360;
		else if (g == max)
			hue = (float) (60 * (2. + (b - r) / (max - min)));
		else if (b == max)
			hue = (float) (60 * (4. + (r - g) / (max - min)));

		return new float[] { (hue), (max), (min), (diff) };
	}
	// --------------------------------------------------------------------------------------

}
