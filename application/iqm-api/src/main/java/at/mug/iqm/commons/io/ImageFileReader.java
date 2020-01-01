package at.mug.iqm.commons.io;

import ij.IJ;
import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.commons.util.FileContentParser;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFDirectory;

/*
 * #%L
 * Project: IQM - API
 * File: ImageFileReader.java
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

/**
 * This class is responsible for reading image files and parsing them into the
 * internal {@link PlanarImage} format.
 * 
 * <p>
 * It uses ImageJ plugins for decoding FITS and DICOM images.
 * 
 * @author Philipp Kainz
 * @update 2018-08-14 HA JAI cannot open jpg images with Java 1.9 and higher
 * 
 */
public class ImageFileReader implements Callable<List<PlanarImage>> {

	private static final Logger logger = LogManager.getLogger(ImageFileReader.class);

	private File source;

	/**
	 * Construct a new reader for a given source file.
	 * 
	 * @param source
	 *            the file
	 */
	public ImageFileReader(File source) {
		this.source = source;
	}

	@Override
	public List<PlanarImage> call() throws Exception {
		return read(source);
	}

	/**
	 * Performs the actual reading.
	 * 
	 * @param file
	 *            the source file
	 * 
	 * @throws Exception
	 *             if reading the image(s) fails
	 */
	public List<PlanarImage> read(File file) throws Exception {
		List<PlanarImage> list = new ArrayList<PlanarImage>();

		PlanarImage img = null;

		try {
			logger.info("Trying to read image file, detected MIME type is ["
					+ FileContentParser.parseContent(file) + "]");
		} catch (Exception e) {
			logger.error("Cannot read MIME type of file: " + file.toString());
		}

		int dotPos = file.toString().lastIndexOf(".");
		String ext = file.toString().substring(dotPos + 1);

		// tiff could be a stack
		if (ext.toLowerCase().equals(IQMConstants.TIF_EXTENSION)
				|| ext.toLowerCase().equals(IQMConstants.TIFF_EXTENSION)) {
			// logger.debug("tif extension registered");
			FileSeekableStream fs = null;
			// BufferedInputStream bs = null; //to enhance performance
			fs = new FileSeekableStream(file.toString());
			// fs = new FileInputStream(arg[i].toString());
			// SeekableStream ss = SeekableStream.wrapInputStream(s,
			// true);
			int nStack = TIFFDirectory.getNumDirectories(fs);
			if (nStack == 1) { // single image
				img = JAI.create("stream", fs); // very fast loading
				list.add(img);
			} else {
				BufferedInputStream bs = new BufferedInputStream(fs);
				TIFFDecodeParam param = null;
				ImageDecoder dec = ImageCodec.createImageDecoder("tiff", bs,
						param);
				// nStack = dec.getNumPages();
				logger.debug("Number of images in this tif file: " + nStack);

				for (int imageToLoad = 0; imageToLoad < nStack; imageToLoad++) {
					RenderedImage ri = dec.decodeAsRenderedImage(imageToLoad);
					img = new RenderedImageAdapter(ri);
					list.add(img);
				}
			}
		} else if (ext.toLowerCase().equals(IQMConstants.JPG_EXTENSION) 
				|| ext.toLowerCase().equals(IQMConstants.JPEG_EXTENSION)) { // jpg image		
			try { // try imageIO
				BufferedImage bi = ImageIO.read(file);
				img = PlanarImage.wrapRenderedImage(bi);
			} catch (Exception ex) {
				logger.error("It is not possible to open this image: " + file.getPath());
			}
			list.add(img);
		
		} else if (ext.toLowerCase().equals(IQMConstants.DCM_EXTENSION)) { // dicom mimage
			// this uses ImageJ's DICOM reader
			ImagePlus imp = (ImagePlus) IJ.runPlugIn("ij.plugin.DICOM",
					file.toString());
			imp.updateImage();
			// if (imp.getWidth()!=0) pi = JAI.create("AWTImage",
			// imp.getImage()); //3 band integer
			if (imp.getWidth() != 0) {
				img = PlanarImage.wrapRenderedImage(imp.getBufferedImage());
				list.add(img);
			}
		} else if (ext.toLowerCase().equals(IQMConstants.FITS_EXTENSION)) {
			// this uses ImageJ's fits reader
			ImagePlus imp = (ImagePlus) IJ.runPlugIn("ij.plugin.FITS_Reader",
					file.toString());
			imp.updateImage();
			// if (imp.getWidth()!=0) pi = JAI.create("AWTImage",
			// imp.getImage()); //3 band integer
			if (imp.getWidth() != 0) {
				img = PlanarImage.wrapRenderedImage(imp.getBufferedImage());
				list.add(img);
			}
		} else { // all other images shorter but other codecs and works without JAI-ImageIO
			//since Java1.9  JAI cannot open jpg images any more
			img = JAI.create("fileload", file.toString());
			
			if (img == null) {
				try { // try imageIO
					BufferedImage bi = ImageIO.read(file);
					img = PlanarImage.wrapRenderedImage(bi);
				} catch (Exception ex) {
					logger.error("It is not possible to open this image: "
							+ file.getPath());
				}
			}
			
			// finally add the image
			list.add(img);

			// Alternative using ImageRead------------------------
			// when using imageread, JAI-ImageIO has to be fully
			// installed
			// pi = JAI.create("imageread", arg[i].toString());
			// ---------------------------------------------------
			// Alternative using ImageIO--------------------------
			// BufferedImage bi = null;
			// try {
			// bi = ImageIO.read(arg[i]);
			// } catch (IOException e) {
			// e.printStackTrace();
			// } //"imageread" is newer than fileload
			// pi = PlanarImage.wrapRenderedImage(bi);
			// ----------------------------------------------------
		}

		return list;
	}
}
