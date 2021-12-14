package at.mug.iqm.commons.util.image;

/*
 * #%L
 * Project: IQM - API
 * File: ImageAnalyzer.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2021 Helmut Ahammer, Philipp Kainz
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


import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.LookupTable;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

 
 

/**
 * This class analyzes an image. Its methods return e.g. the color model or the
 * size of the image.
 * 
 * @author Philipp Kainz
 */
public final class ImageAnalyzer {

	/**
	 * Custom class logger.
	 */
	  

	/**
	 * Read the most common properties of an image.
	 * 
	 * @param bi
	 *            - the {@link RenderedImage} to analyze
	 * @return a Map with {@link String} key-value pairs containing the image's
	 *         information.
	 */
	public static synchronized Map<String, Object> getProperties(
			final BufferedImage bi) {
		Map<String, Object> properties = new HashMap<String, Object>();
		String[][] infos = ImageInfoUtil.getImageInfo(PlanarImage
				.wrapRenderedImage(bi).getAsBufferedImage());

		// construct the hashmap
		for (int i = 0; i < infos.length - 1; i++) {
			properties.put(infos[i][0], infos[i][1]);
		}

		System.out.println("IQM Trace: Analyzed the image properties, done.");

		return properties;
	}

	/**
	 * Read the most common properties of an image.
	 * 
	 * @param img
	 *            - the {@link PlanarImage} to analyze
	 * @return a Map with key-value pairs containing the image's information.
	 */
	public static synchronized Map<String, Object> getProperties(
			final PlanarImage img) {
		System.out.println("IQM Trace: Retrieving image properties...");
		return getProperties(img.getAsBufferedImage());
	}

	/**
	 * Print out the most common properties of an image as HTML string.
	 * <p>
	 * Each property is represented by a single row in an HTML table.
	 * 
	 * @param pi
	 *            - the {@link PlanarImage} to analyze
	 */
	public static String printProperties(final PlanarImage pi) {
		Map<String, Object> infos = getProperties(pi);

		// print the hashmap
		Iterator<String> iterator = infos.keySet().iterator();

		String s = "<html><table style=\"border:0px\">";
		
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = infos.get(key).toString();

			s += "<tr><td>" + key + "</td><td>" + value + "</td></tr>";
		}

		return s + "</table></html>";
	}

	/**
	 * Returns the {@link SampleModel} of a given {@link PlanarImage}.
	 * 
	 * @param img
	 * @return the sample model
	 */
	public static synchronized SampleModel getSampleModel(final PlanarImage img) {
		System.out.println("IQM Trace: SampleModel is " + img.getSampleModel().toString());
		return img.getSampleModel();
	}

	/**
	 * Returns the {@link DataBuffer} type of a given {@link PlanarImage}'s
	 * {@link SampleModel}.
	 * 
	 * @param img
	 * @return the data buffer type
	 */
	public static synchronized int getDataBufferType(final PlanarImage img) {
		System.out.println("IQM Trace: DataBuffer is of TYPE: "
				+ img.getSampleModel().getDataType());
		return img.getSampleModel().getDataType();
	}

	/**
	 * Returns the number of bands of a given {@link PlanarImage}.
	 * 
	 * @param img
	 * @return the number of bands
	 */
	public static synchronized int getNumberOfBands(final PlanarImage img) {
		System.out.println("IQM Trace: The number of bands is: " + img.getNumBands());
		return img.getNumBands();
	}

	/**
	 * Returns the {@link ColorModel} of a given {@link PlanarImage}.
	 * 
	 * @param img
	 * @return the color model
	 */
	public static synchronized ColorModel getColorModel(final PlanarImage img) {
		System.out.println("IQM Trace: The ColorModel is " + img.getColorModel().toString());
		return img.getColorModel();
	}

	/**
	 * Returns whether or not the {@link ColorModel} is {@link IndexColorModel}
	 * of a given {@link PlanarImage}.
	 * 
	 * @param img
	 * @return <code>true</code> if indexed, <code>false</code> otherwise
	 */
	public static synchronized boolean isIndexed(final PlanarImage img) {
		boolean idx = (img.getColorModel() instanceof IndexColorModel) ? true
				: false;
		System.out.println("IQM Trace: The image uses an IndexColorModel? " + idx);
		return idx;
	}

	/**
	 * Returns the {@link IndexColorModel} of a given {@link PlanarImage}.
	 * 
	 * @param img
	 * @return <code>null</code>, if the {@link ColorModel} is not
	 *         {@link IndexColorModel}
	 */
	public static synchronized IndexColorModel getIndexColorModel(
			final PlanarImage img) {
		IndexColorModel icm = (img.getColorModel() instanceof IndexColorModel) ? (IndexColorModel) img
				.getColorModel() : null;
		System.out.println("IQM Trace: The image has an IndexColorModel (null = no)? " + icm);
		return icm;
	}

	/**
	 * Returns the {@link LookupTable} as <code>byte[][]</code> of a given
	 * {@link PlanarImage}.
	 * 
	 * @param img
	 * @return <code>null</code>, if the given image's {@link ColorModel} is not
	 *         of {@link IndexColorModel}.
	 */
	public static synchronized byte[][] getByteLookUpTable(final PlanarImage img) {
		byte[][] byteLUT = null;
		if (isIndexed(img)) {
			IndexColorModel icm = getIndexColorModel(img);
			int mapSize = icm.getMapSize();
			byteLUT = new byte[4][mapSize];
			icm.getReds(byteLUT[0]);
			icm.getGreens(byteLUT[1]);
			icm.getBlues(byteLUT[2]);
			icm.getAlphas(byteLUT[3]);
		}
		return byteLUT;
	}

	/**
	 * Returns the {@link LookupTable} as normalized 8bit (0-255)
	 * <code>short[][]</code> of a given {@link PlanarImage}.
	 * 
	 * @param img
	 * @return <code>null</code>, if the given image's {@link ColorModel} is not
	 *         of {@link IndexColorModel}.
	 */
	public static synchronized short[][] getShortLookUpTable(
			final PlanarImage img) {
		short[][] shortLUT = null;
		if (isIndexed(img)) {
			byte[][] byteLUT = getByteLookUpTable(img);

			int mapSize = getIndexColorModel(img).getMapSize();

			// normalize the LUT to 0-255
			shortLUT = new short[4][mapSize];
			for (int i = 0; i < mapSize; i++) {
				shortLUT[0][i] = byteLUT[0][i] >= 0 ? byteLUT[0][i]
						: (short) (byteLUT[0][i] + 256); // red
				shortLUT[1][i] = byteLUT[1][i] >= 0 ? byteLUT[1][i]
						: (short) (byteLUT[1][i] + 256); // green
				shortLUT[2][i] = byteLUT[2][i] >= 0 ? byteLUT[2][i]
						: (short) (byteLUT[2][i] + 256); // blue
				shortLUT[3][i] = byteLUT[2][i] >= 0 ? byteLUT[3][i]
						: (short) (byteLUT[3][i] + 256); // alpha
			}
		}
		return shortLUT;
	}

	/**
	 * Returns the {@link ColorSpace} associated with a {@link ColorModel} of a
	 * given {@link PlanarImage}.
	 * 
	 * @param img
	 * @return the color space
	 */
	public static synchronized ColorSpace getColorSpace(final PlanarImage img) {
		ColorSpace cs = img.getColorModel().getColorSpace();
		System.out.println("IQM Trace: The image's ColorModel has the ColorSpace: " + cs);
		return cs;
	}
	// TODO http://docs.oracle.com/javase/6/docs/api/java/awt/color/ColorSpace.html
	public static synchronized boolean isRGB(final PlanarImage img){
		ColorSpace cs = img.getColorModel().getColorSpace();
		return (cs.getType() == ColorSpace.TYPE_RGB);
	}
	
	/**
	 * Returns the pixel bits (e.g. 8, 12, 16, 24, ...) of {@link PlanarImage}'s
	 * {@link ColorModel}.
	 * 
	 * @param img
	 * @return the bit per pixel
	 */
	public static synchronized int getPixelBits(final PlanarImage img) {
		System.out.println("IQM Trace: PixelBits per channel="
				+ img.getColorModel().getPixelSize());
		return img.getColorModel().getPixelSize();
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} has <code>1</code>
	 * band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has 1 band, <code>false</code> if
	 *         not
	 */
	public static synchronized boolean has1Band(final PlanarImage img) {
		return ((img.getNumBands() == 1) ? true : false);
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} has <code>3</code>
	 * bands.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has 3 bands, <code>false</code> if
	 *         not
	 */
	public static synchronized boolean has3Bands(final PlanarImage img) {
		return ((img.getNumBands() == 3) ? true : false);
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} has <code>4</code>
	 * bands (+ alpha).
	 * 
	 * @param img
	 * @return <code>true</code> if the image has 4 bands, <code>false</code> if
	 *         not
	 */
	public static synchronized boolean has4Bands(final PlanarImage img) {
		return ((img.getNumBands() == 4) ? true : false);
	}

	/**
	 * Returns whether or not a given {@link PlanarImage}'s {@link ColorModel}
	 * uses the alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image uses the alpha channel,
	 *         <code>false</code> if not
	 */
	public static synchronized boolean hasAlphaBand(final PlanarImage img) {
		return img.getColorModel().hasAlpha();
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} is an binary (1bit)
	 * value single band image without an alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has a single 1bit value band,
	 *         <code>false</code> if not
	 */
	public static synchronized boolean isBinary(final PlanarImage img) {
		boolean result = false;

		if (getPixelBits(img) == 1 && has1Band(img) && !hasAlphaBand(img)) {
			result = true;
		}
		return result;
	}

	/**
	 * Returns the {@link Transparency} type of a given {@link PlanarImage}'s
	 * {@link ColorModel}
	 * 
	 * @param img
	 * @return <code>null</code> if the image's color model has no transparency
	 */
	public static synchronized int getTransparency(final PlanarImage img) {
		// log the output
		switch (getColorModel(img).getTransparency()) {
		case Transparency.OPAQUE:
			System.out.println("IQM Trace: Transparency='opaque'");
			break;
		case Transparency.BITMASK:
			System.out.println("IQM Trace: Transparency='bitmask'");
			break;
		case Transparency.TRANSLUCENT:
			System.out.println("IQM Trace: Transparency='translucent'");
			break;
		}

		return getColorModel(img).getTransparency();
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} is an 8bit grey value
	 * single band image without an alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has a single 8bit grey value band,
	 *         <code>false</code> if not
	 */
	public static synchronized boolean is8BitGrey(final PlanarImage img) {
		boolean result = false;

		if (getPixelBits(img) == 8 && has1Band(img) && !hasAlphaBand(img)) {
			result = true;
		}
		return result;
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} is an 12bit grey value
	 * single band image without an alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has a single 12bit grey value
	 *         band, <code>false</code> if not
	 */
	public static synchronized boolean is12BitGrey(final PlanarImage img) {
		boolean result = false;

		if (getPixelBits(img) == 12 && has1Band(img) && !hasAlphaBand(img)) {
			result = true;
		}
		return result;
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} is an 16bit grey value
	 * single band image without an alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has a single 16bit grey value
	 *         band, <code>false</code> if not
	 */
	public static synchronized boolean is16BitGrey(final PlanarImage img) {
		boolean result = false;

		if (getPixelBits(img) == 16 && has1Band(img) && !hasAlphaBand(img)) {
			result = true;
		}
		return result;
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} is an 8bit three band
	 * image without an alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has 3 bands using 8bit values
	 *         each, <code>false</code> if not
	 */
	public static synchronized boolean is8Bit3Band(final PlanarImage img) {
		boolean result = false;

		if (getPixelBits(img) == 24 && has3Bands(img) && !hasAlphaBand(img)) {
			result = true;
		}
		return result;
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} is an 12bit three band
	 * image without an alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has 3 bands using 12bit values
	 *         each, <code>false</code> if not
	 */
	public static synchronized boolean is12Bit3Band(final PlanarImage img) {
		boolean result = false;

		if (getPixelBits(img) == 36 && has3Bands(img) && !hasAlphaBand(img)) {
			result = true;
		}
		return result;
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} is an 16bit three band
	 * image without an alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has 3 bands using 16bit values
	 *         each, <code>false</code> if not
	 */
	public static synchronized boolean is16Bit3Band(final PlanarImage img) {
		boolean result = false;

		if (getPixelBits(img) == 48 && has3Bands(img) && !hasAlphaBand(img)) {
			result = true;
		}
		return result;
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} is an 8bit four band
	 * image with an alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has 4 bands using 8bit values
	 *         each, <code>false</code> if not
	 */
	public static synchronized boolean is8Bit4Band(final PlanarImage img) {
		boolean result = false;

		if (getPixelBits(img) == 32 && has4Bands(img) && hasAlphaBand(img)) {
			result = true;
		}
		return result;
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} is an 12bit four band
	 * image with an alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has 4 bands using 12bit values
	 *         each, <code>false</code> if not
	 */
	public static synchronized boolean is12Bit4Band(final PlanarImage img) {
		boolean result = false;

		if (getPixelBits(img) == 48 && has4Bands(img) && hasAlphaBand(img)) {
			result = true;
		}
		return result;
	}

	/**
	 * Returns whether or not a given {@link PlanarImage} is an 16bit four band
	 * image with an alpha band.
	 * 
	 * @param img
	 * @return <code>true</code> if the image has 4 bands using 16bit values
	 *         each, <code>false</code> if not
	 */
	public static synchronized boolean is16Bit4Band(final PlanarImage img) {
		boolean result = false;

		if (getPixelBits(img) == 64 && has4Bands(img) && hasAlphaBand(img)) {
			result = true;
		}
		return result;
	}

	public static void main(String[] args) {
		// load an image and try to read the properties.
		PlanarImage pi = JAI.create("fileload",
				"C:\\Users\\phil\\Pictures\\eclipse.PNG");
		// System.out.println("IQM Trace: "+ getColorModel(pi));
		System.out.println("IQM Trace: "+ is8Bit4Band(pi));
		printProperties(pi);
		pi = JAI.create("fileload",
				"C:\\Users\\phil\\Pictures\\FluorescentCells.jpg");
		System.out.println("IQM Trace: "+ getColorModel(pi));
		System.out.println("IQM Trace: "+ is8BitGrey(pi));
		pi = JAI.create("fileload",
				"C:\\Users\\phil\\Pictures\\FluorescentCells_8bit.jpg");
		// System.out.println("IQM Trace: "+ getColorModel(pi));
		System.out.println("IQM Trace: "+ is8BitGrey(pi));
		printProperties(pi);
		pi = JAI.create("fileload",
				"C:\\Users\\phil\\Pictures\\Head_Original.jpg");
		// System.out.println("IQM Trace: "+ getColorModel(pi));
		System.out.println("IQM Trace: "+ is8BitGrey(pi));
		printProperties(pi);
		pi = JAI.create("fileload", "C:\\Users\\phil\\Pictures\\16bitGrey.tif");
		// System.out.println("IQM Trace: "+ getColorModel(pi));
		System.out.println("IQM Trace: "+ is16BitGrey(pi));
		printProperties(pi);
	}
}
