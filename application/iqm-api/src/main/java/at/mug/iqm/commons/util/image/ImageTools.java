package at.mug.iqm.commons.util.image;

/*
 * #%L
 * Project: IQM - API
 * File: ImageTools.java
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

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.jai.BorderExtender;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;
import javax.media.jai.operator.BandSelectDescriptor;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.IDrawingLayer;
import at.mug.iqm.api.gui.roi.PointROI;
import at.mug.iqm.commons.util.BASE64Helper;

/**
 * @author Philipp Kainz
 * 
 */
public final class ImageTools {

	private static final Logger logger = LogManager.getLogger(ImageTools.class);

	/**
	 * Copies the image properties
	 * <ul>
	 * <li>
	 * <code>image_name</code></li>
	 * <li>
	 * <code>file_name</code></li>
	 * <li>
	 * <code>model_name</code></li>
	 * </ul>
	 * from the source to the destination object.
	 * 
	 * @param source
	 * @param destination
	 * @return a planar image with copied properties
	 */
	public static PlanarImage copyCustomImageProperties(PlanarImage source,
			PlanarImage destination) {
		destination.setProperty("image_name",
				String.valueOf(source.getProperty("image_name")));
		destination.setProperty("file_name",
				String.valueOf(source.getProperty("file_name")));
		destination.setProperty("model_name",
				String.valueOf(source.getProperty("model_name")));
		return destination;
	}

	/**
	 * This method calculates the center of gravity The center coordinates refer
	 * to the upper left corner
	 */
	public static double[] getCenterOfGravity(PlanarImage pi) {
		double[] center = new double[2];
		int width = pi.getWidth();
		int height = pi.getHeight();
		int numBands = pi.getNumBands();
		if (numBands == 3) {
			double[][] m = { { 1. / 3, 1. / 3, 1. / 3, 0 } };
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(pi);
			pb.add(m);
			pi = JAI.create("bandcombine", pb, null);
			pb.removeSources();
			pb.removeParameters();
			pb.addSource(pi);
			pi = JAI.create("invert", pb, null);
		}
		Raster r = pi.getData();
		center[0] = 0.0; // x
		center[1] = 0.0; // y
		int sumGrey = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				center[0] = center[0] + getRasterSample(r, x, y, 0) * x;
				center[1] = center[1] + getRasterSample(r, x, y, 0) * y;
				sumGrey = sumGrey + getRasterSample(r, x, y, 0);
			}
		}
		center[0] = center[0] / sumGrey;
		center[1] = center[1] / sumGrey;
		return center;
	}

	/**
	 * This method returns a raster sample from within the Raster's bounding
	 * rectangle.
	 * 
	 * @param r
	 * @param x
	 * @param y
	 * @param band
	 * @return raster sample
	 */
	public static int getRasterSample(Raster r, int x, int y, int band) {
		return r.getSample(x + r.getMinX(), y + r.getMinY(), band);
	}

	/**
	 * This static method gets the image type
	 * 
	 * @param pi
	 *            - PlanarImage instance
	 * @return image type as String
	 */
	public static String getImgType(PlanarImage pi) {
		return ImageInfoUtil.getDataTypeAsText(pi.getSampleModel(),
				pi.getColorModel());
	}

	/**
	 * This method gets the maximal possible grey value depending on the image
	 * type
	 */
	public static double getImgTypeGreyMax(PlanarImage pi) {
		double typeGreyMax = 0d;
		int dt = pi.getSampleModel().getDataType();
		if (dt == DataBuffer.TYPE_BYTE) {
			// typeGreyMax = Byte.MAX_VALUE; //liefert 127!!
			typeGreyMax = 255d;
		}
		if (dt == DataBuffer.TYPE_DOUBLE) {
			typeGreyMax = Double.MAX_VALUE;
		}
		if (dt == DataBuffer.TYPE_FLOAT) {
			typeGreyMax = Float.MAX_VALUE;
		}
		if (dt == DataBuffer.TYPE_INT) {
			typeGreyMax = Integer.MAX_VALUE;
		}
		if (dt == DataBuffer.TYPE_SHORT) {
			typeGreyMax = Short.MAX_VALUE;
		}
		if (dt == DataBuffer.TYPE_USHORT) {
			// unsigned short [0,65535]
			typeGreyMax = 65535d;
		}
		return typeGreyMax;
	}

	/**
	 * Generate a 60-by-60 pixel thumb nail of a given image for the Tank list.
	 * 
	 * @param pi
	 *            an image
	 * @return an image icon for the tank list, scaled to fit 60x60 px
	 */
	public static Thumbnail createTankThumb(PlanarImage pi) {
		logger.trace("Creating tank thumb nail.");
		return new Thumbnail(createThumbnail(pi, 60, 60, true, true).getImage());
	}

	/**
	 * Generate a 40-by-40 pixel thumb nail of a given image.
	 * 
	 * @param pi
	 *            the image to be scaled
	 * @return ImageIcon
	 */
	public static Thumbnail createManagerThumb(PlanarImage pi) {
		logger.trace("Creating manager thumb nail.");
		return new Thumbnail(createThumbnail(pi, 40, 40, true, true).getImage());
	}

	/**
	 * This static method returns an image icon of a given {@link PlanarImage}.
	 * 
	 * @param image
	 *            the image to be scaled
	 * @param targetWidth
	 *            the target width of the thumb nail
	 * @param targetHeight
	 *            the target height of the thumb nail (only used if
	 *            <code>keepAspectRatio</code> is <code>false</code>)
	 * @param keepAspectRatio
	 *            whether or not the value given in <code>targetWidth</code>
	 *            should be used as longest edge
	 * @param withBorder
	 *            whether or not a grey border should be painted around the
	 *            scaled image
	 * @return ImageIcon
	 */
	public static ImageIcon createThumbnail(PlanarImage image, int targetWidth,
			int targetHeight, boolean keepAspectRatio, boolean withBorder) {
		ImageIcon ic = null;
		
		// TODO contrast changes for some images! 
		// PK 2014-05-23: Closed ticket for now, since the difference
		// in contrast is just observable in the thumbnails on Mac OS X
		// see ticket #42: https://sourceforge.net/p/iqm/tickets/42/

		if ((image != null) && (image instanceof PlanarImage)) {

			PlanarImage pi = null;
			if (keepAspectRatio) {
				pi = scaleKeepAspectRatio(image, targetWidth);
			} else {
				pi = scale(image, targetWidth, targetHeight);
			}

			if (withBorder) {
				ParameterBlock pbBackground = new ParameterBlock();
				pbBackground.removeSources();
				pbBackground.removeParameters();
				pbBackground.addSource(pi);
				// eg. 40 - 1 / 2
				pbBackground.add((targetWidth - pi.getWidth()) / 2); // left
				pbBackground.add((targetWidth - pi.getWidth()) / 2); // right
				pbBackground.add((targetHeight - pi.getHeight()) / 2); // top
				pbBackground.add((targetHeight - pi.getHeight()) / 2); // bottom
				double borderColor1[] = { 150, 150, 150, 150 }; // color outside
																// thumbnail
				
				pbBackground.add(new BorderExtenderConstant(borderColor1));
				pbBackground.add(borderColor1);// fill color
				pi = JAI.create("border", pbBackground);

				ParameterBlock pbBorder = new ParameterBlock(); // black border
				pbBorder.removeSources();
				pbBorder.removeParameters();
				pbBorder.addSource(pi);
				pbBorder.add(1); // left
				pbBorder.add(1); // right
				pbBorder.add(1); // top
				pbBorder.add(1); // bottom
				double borderColor2[] = { 0, 0, 0, 0 };
				pbBorder.add(new BorderExtenderConstant(borderColor2));
				
				pbBorder.add(borderColor2);// fill color
				pi = JAI.create("border", pbBorder);
			}

			try {
				BufferedImage bi = pi.getAsBufferedImage();
				ic = new ImageIcon(bi);
			} catch (Exception e) {
				logger.error(
						"An error occurred, the ImageIcon could not be generated!",
						e);
				BufferedImage bi;
				try {
					bi = ImageIO.read(Resources
							.getImageURL("icon.unsupportedContent.generic64"));
					pi = scaleKeepAspectRatio(
							PlanarImage.wrapRenderedImage(bi), targetWidth);
					ic = new ImageIcon(pi.getAsBufferedImage());
				} catch (IOException e1) {
					logger.error(
							"An error occurred, the ImageIcon could not be generated!",
							e1);
					ic = null;
				}
			}
		}
		return ic;
	}

	/**
	 * Creates a base64 encoded string for the data URI of an image to be used
	 * in an HTML tag.
	 * 
	 * @param image
	 *            the image
	 * @param targetWidth
	 *            the target width
	 * @param targetHeight
	 *            the target height
	 * @return a base64 encoded {@link String}
	 */
	public static String createB64EncodedToolTipImg(PlanarImage image,
			int targetWidth, int targetHeight) {
		String b64img = null;

		try {
			PlanarImage pi = scale(image, targetWidth, targetHeight);

			// convert it to a byte[] and String
			ByteArrayOutputStream tmp_out = new ByteArrayOutputStream();
			try {
				ImageIO.write(pi, "JPG", tmp_out);
				b64img = BASE64Helper.encodeToString(tmp_out.toByteArray());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return b64img;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b64img;
	}

	/**
	 * Scales a given image to a target size by keeping the aspect ratio.
	 * 
	 * @param image
	 * @param targetSize
	 * @return
	 */
	public static PlanarImage scaleKeepAspectRatio(PlanarImage image,
			int targetSize) {
		PlanarImage result = null;

		int height = image.getHeight();
		int width = image.getWidth();
		boolean isPortrait = (height > width);
		boolean isSquare = (height == width);

		float zoomX = 1.0F;
		float zoomY = 1.0F;

		if (isSquare) {
			if (width > (targetSize)) {
				zoomX = targetSize / (float) width;
				zoomY = zoomX;
			} else {
				zoomX = 1.0F;
				zoomY = zoomX;
			}
		} else if (isPortrait) {
			zoomY = targetSize / (float) height;
			if (width > (targetSize)) {
				zoomX = zoomY;
			} else {
				zoomX = 1.0F;
			}
		} else if (!isSquare && !isPortrait) {
			zoomX = targetSize / (float) width;
			if (height > (targetSize)) {
				zoomY = zoomX;
			} else {
				zoomY = 1.0F;
			}
		}

		ParameterBlock pbScale = new ParameterBlock();
		pbScale.addSource(image);

		pbScale.add(zoomX);// x scale factor
		pbScale.add(zoomY);// y scale factor
		pbScale.add(0.0F);// x translate
		pbScale.add(0.0F);// y translate
		// errors for 16 bit images
		// pb.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));

		pbScale.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY));

		result = JAI.create("scale", pbScale, rh);
		return result;
	}

	/**
	 * Scales a given image to a specified size.
	 * 
	 * @param image
	 *            the original image
	 * @param targetWidth
	 *            the target width
	 * @param targetHeight
	 *            the target height
	 * @return the scaled image
	 */
	public static PlanarImage scale(PlanarImage image, int targetWidth,
			int targetHeight) {
		PlanarImage result = null;

		float zoomX = targetWidth / (float) image.getWidth();
		float zoomY = targetHeight / (float) image.getHeight();

		ParameterBlock pbScale = new ParameterBlock();
		pbScale.addSource(image);

		pbScale.add(zoomX);// x scale factor
		pbScale.add(zoomY);// y scale factor
		pbScale.add(0.0F);// x translate
		pbScale.add(0.0F);// y translate
		// errors for 16 bit images
		// pb.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));

		pbScale.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY));

		result = JAI.create("scale", pbScale, rh);
		return result;
	}

	/**
	 * Displays the {@link BufferedImage} in a separate {@link JFrame}.
	 * 
	 * @param image
	 */
	public static void showImage(BufferedImage image, String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel p = new JPanel();
		JLabel picLabel = new JLabel(new ImageIcon(image));
		p.add(picLabel);
		frame.getContentPane().add(p);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Creates a base64 encoded string for the data URI of an image to be used
	 * in an HTML tag.
	 * 
	 * @param image
	 *            the image
	 * @param encoding
	 *            the encoding string for
	 *            {@link ImageIO#write(java.awt.image.RenderedImage, String, java.io.OutputStream)}
	 * @return a <code>byte[]</code>
	 */
	public static byte[] toByteArray(PlanarImage image, String encoding) {
		byte[] bytes = null;

		try {
			// convert it to a byte[]
			ByteArrayOutputStream tmp_out = new ByteArrayOutputStream();
			try {
				if (encoding == null || encoding.equals("")) {
					encoding = "PNG";
				}
				ImageIO.write(image, encoding, tmp_out);
				bytes = tmp_out.toByteArray();
			} catch (Exception ex) {
				ex.printStackTrace();
				bytes = null;
			}
			return bytes;
		} catch (Exception ex) {
			ex.printStackTrace();
			bytes = null;
		}
		return bytes;
	}

	public static void main(String[] args) {
		// PlanarImage pi = JAI.create("fileload",
		// "C:\\Users\\Phil\\Downloads\\landscapes-1.png");
		// CommonTools.showImage(scaleKeepAspectRatio(pi, 1200)
		// .getAsBufferedImage(), null);
	}

	/**
	 * Removes the alpha channel, if any present and returns the image with the
	 * number of original color bands.
	 * <p>
	 * If the image does not have an alpha channel, it is returned unchanged.
	 * 
	 * @param image
	 * @return a planar image with alpha channel removed
	 * @throws IllegalArgumentException
	 *             if the image is <code>null</code>
	 */
	public static PlanarImage removeAlphaChannel(PlanarImage image) {
		if (image == null)
			throw new IllegalArgumentException(
					"The passed image must not be null in order to remove the alpha channel!");

		if (ImageAnalyzer.hasAlphaBand(image)) {
			int numColorComponents = image.getColorModel()
					.getNumColorComponents();
			final int bands[] = new int[numColorComponents];
			for (int i = 0; i < numColorComponents; i++)
				bands[i] = i;
			image = BandSelectDescriptor.create(image, bands, null);
			return image;
		} else {
			return image;
		}
	}

	/**
	 * Removes the alpha channel, if any present and returns the raster with the
	 * number of original color bands.
	 * <p>
	 * If the raster does not have an alpha channel, it is returned unchanged.
	 * 
	 * @param raster
	 * @return a planar image with alpha channel removed
	 * @throws IllegalArgumentException
	 *             if the image is <code>null</code>
	 */
	public static Raster removeAlphaChannel(Raster raster) {
		if (raster == null)
			throw new IllegalArgumentException(
					"The passed raster must not be null in order to remove the alpha channel!");

		BufferedImage image = new BufferedImage(raster.getWidth(),
				raster.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

		for (int band = 0; band < raster.getNumBands(); band++) {
			System.out.println(band);
			for (int x = 0; x < raster.getWidth(); x++) {
				for (int y = 0; y < raster.getHeight(); y++) {
					image.setRGB(x, y, getRasterSample(raster, x, y, band));
				}
			}
		}

		image = ImageTools.removeAlphaChannel(
				PlanarImage.wrapRenderedImage(image)).getAsBufferedImage();

		return image.getData();
	}

	/**
	 * Extracts a specified channel (band) from a given image.
	 * 
	 * @param pi
	 *            the source image
	 * @param band
	 *            the band to be extracted
	 * @return an image with a single band
	 * @throws IllegalArgumentException
	 *             if <code>pi</code> is <code>null</code>,
	 *             <code>band&lt;0</code>, or <code>band</code>&gt;
	 *             <code>pi.getNumBands()</code>
	 */
	public static PlanarImage extractChannel(PlanarImage pi, int band) {
		if (pi == null) {
			throw new IllegalArgumentException("The provided image is null.");
		}
		if (band < 0) {
			throw new IllegalArgumentException("The band is smaller than 0.");
		}
		if (pi.getNumBands() < band) {
			throw new IllegalArgumentException(
					"You try to access a band within the image that does not exist: #bands="
							+ pi.getNumBands() + ", band=" + band);
		}

		PlanarImage result = null;
		int numBands = pi.getNumBands();

		double[][] matrix = new double[1][numBands + 1];

		// set matrix 1.0d at the band position and 0.0d otherwise
		for (int i = 0; i < numBands; i++) {
			if (i == band) {
				matrix[0][i] = 1.0d;
			} else {
				matrix[0][i] = 0.0d;
			}
		}

		result = JAI.create("bandcombine", new ParameterBlock().addSource(pi)
				.add(matrix), null);

		return result;
	}

	/**
	 * Extracts an entirely black channel of the size of a given image.
	 * 
	 * @param pi
	 *            the source image
	 * @return a black image with a single band
	 * @throws IllegalArgumentException
	 *             if <code>pi</code> is <code>null</code>
	 */
	public static PlanarImage extractBlackChannel(PlanarImage pi) {
		if (pi == null) {
			throw new IllegalArgumentException("The provided image is null.");
		}

		PlanarImage result = null;
		int numBands = pi.getNumBands();

		double[][] matrix = new double[1][numBands + 1];

		// set matrix 1.0d at the band position and 0.0d otherwise
		for (int i = 0; i < numBands; i++) {
			matrix[0][i] = 0.0d;
		}

		result = JAI.create("bandcombine", new ParameterBlock().addSource(pi)
				.add(matrix), null);

		return result;
	}

	/**
	 * Combines the specified channels (bands) from a given image to a single
	 * band grey value image.
	 * 
	 * @param pi
	 *            the source image
	 * @param bands
	 *            the ordered list of bands to be extracted
	 * @return an image with a single band
	 * @throws IllegalArgumentException
	 *             if <code>pi</code> is <code>null</code>,
	 *             <code>bands.size()==0</code>, or
	 *             <code>bands.get(i)&gt;pi.getNumBands()</code>
	 */
	public static PlanarImage combineChannels(PlanarImage pi,
			List<Integer> bands) {
		if (pi == null || bands == null) {
			throw new IllegalArgumentException(
					"The provided image or the band list is null.");
		}
		if (bands.size() == 0) {
			throw new IllegalArgumentException(
					"The band list must at least contain a single element.");
		}
		if (pi.getNumBands() < bands.size()) {
			throw new IllegalArgumentException(
					"You try to access more bands than the image contains: #imageBands="
							+ pi.getNumBands() + ", #bands=" + bands.size());
		}
		for (Integer i : bands) {
			if (i > pi.getNumBands()) {
				throw new IllegalArgumentException(
						"You try to access a band within the image that does not exist: #imagebands="
								+ pi.getNumBands() + ", bands["
								+ bands.indexOf(i) + "]=" + i);
			}
		}

		PlanarImage result = null;
		int numBands = pi.getNumBands();

		double[][] matrix = new double[1][numBands + 1];

		// set matrix 1.0d / bands.size() at the specified band positions and
		// 0.0d otherwise
		for (int i = 0; i < numBands; i++) {
			if (bands.contains(i)) {
				matrix[0][i] = 1.0d / bands.size();
			} else {
				matrix[0][i] = 0.0d;
			}
		}

		result = JAI.create("bandcombine", new ParameterBlock().addSource(pi)
				.add(matrix), null);

		return result;
	}

	/**
	 * Extracts a specified channels (bands) from a given image and produces a
	 * multi-banded image.
	 * 
	 * @param pi
	 *            the source image
	 * @param bands
	 *            the ordered list of bands to be extracted
	 * @return an image with a single band
	 * @throws IllegalArgumentException
	 *             if <code>pi</code> is <code>null</code>,
	 *             <code>bands.size()==0</code>, or
	 *             <code>bands.get(i)&gt;pi.getNumBands()</code>
	 */
	public static PlanarImage extractChannels(PlanarImage pi,
			List<Integer> bands) {
		if (pi == null || bands == null) {
			throw new IllegalArgumentException(
					"The provided image or the band list is null.");
		}
		if (bands.size() == 0) {
			throw new IllegalArgumentException(
					"The band list must at least contain a single element.");
		}
		if (pi.getNumBands() < bands.size()) {
			throw new IllegalArgumentException(
					"You try to access more bands than the image contains: #imageBands="
							+ pi.getNumBands() + ", #bands=" + bands.size());
		}
		for (Integer i : bands) {
			if (i > pi.getNumBands()) {
				throw new IllegalArgumentException(
						"You try to access a band within the image that does not exist: #imagebands="
								+ pi.getNumBands() + ", bands["
								+ bands.indexOf(i) + "]=" + i);
			}
		}

		PlanarImage result = null;
		int numBands = pi.getNumBands();

		// extract the single channel if specified
		if (bands.size() == 1) {
			result = extractChannel(pi, bands.get(0));
		} else {
			ParameterBlockJAI pbBM = new ParameterBlockJAI("bandmerge");
			for (int n = 0; n < numBands; n++) {
				if (bands.contains(n)) {
					PlanarImage channel = extractChannel(pi,
							bands.get(bands.indexOf(n)));
					// DEBUG ONLY
					// CommonTools.showImage(channel.getAsBufferedImage(),
					// null);
					pbBM.setSource(channel, n);
				} else {
					pbBM.setSource(extractBlackChannel(pi), n);
				}
			}
			result = JAI.create("bandmerge", pbBM, null);
		}

		return result;
	}

	public static BufferedImage toBufferedImage(RenderedImage img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		ColorModel cm = img.getColorModel();
		int width = img.getWidth();
		int height = img.getHeight();
		WritableRaster raster = cm
				.createCompatibleWritableRaster(width, height);
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		String[] keys = img.getPropertyNames();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				properties.put(keys[i], img.getProperty(keys[i]));
			}
		}
		BufferedImage result = new BufferedImage(cm, raster,
				isAlphaPremultiplied, properties);
		img.copyData(raster);
		return result;
	}

	/**
	 * Draws the input image to a compatible format for the current local
	 * {@link GraphicsEnvironment}.
	 * 
	 * @param input
	 *            the image
	 * @return the input, if the image is already compatible, the converted
	 *         image otherwise
	 */
	public static RenderedImage toCompatibleImage(RenderedImage input) {
		long start = System.currentTimeMillis();

		// obtain the current system graphical settings
		GraphicsConfiguration gfx_config = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		/*
		 * if image is already compatible and optimized for current system
		 * settings, simply return it
		 */
		if (input.getColorModel().equals(gfx_config.getColorModel()))
			return input;

		try {
			BufferedImage image = toBufferedImage(input);

			// image is not optimized, so create a new image that is
			BufferedImage new_image = gfx_config.createCompatibleImage(
					image.getWidth(), image.getHeight(),
					image.getTransparency());

			// get the graphics context of the new image to draw the old image
			// on
			Graphics2D g2d = (Graphics2D) new_image.getGraphics();

			// actually draw the image and dispose of context no longer needed
			g2d.drawImage(image, 0, 0, null);
			g2d.dispose();

			logger.info("Image optimization done in: "
					+ (System.currentTimeMillis() - start) / 1000.0d + " s.");

			// return the new optimized image
			return new_image;
		} catch (OutOfMemoryError oome) {
			oome.printStackTrace();
			return input;
		}
	}

	/**
	 * Superimposes a set of regions of interest on an underlying image.
	 * 
	 * @param image
	 * @param layers
	 * @return an image with the ROIs drawn
	 */
	public static PlanarImage paintROIsOnImage(PlanarImage image,
			List<IDrawingLayer> layers) {
		BufferedImage bi = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_RGB);

		image = PlanarImage.wrapRenderedImage(image);

		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		// draw the image
		g.drawImage(image.getAsBufferedImage(), 0, 0, null);

		for (IDrawingLayer layer : layers) {
			List<ROIShape> allROIShapes = layer.getAllROIShapes();
			// paint all ROI shapes of this layer
			if (allROIShapes != null) {
				Iterator<ROIShape> iter = allROIShapes.iterator();
				while (iter.hasNext()) {
					ROIShape roiShape = iter.next();
					g.setColor(layer.getLayerColor());
					if (roiShape instanceof PointROI) {
						// g2.setColor(Color.cyan);
						g.fill(roiShape.getAsShape());
					} else {
						g.draw(roiShape.getAsShape());
					}
				}
			}
		}

		g.dispose();
		PlanarImage out = PlanarImage.wrapRenderedImage(bi);

		// showImage(bi, "output");
		return out;
	}
}
