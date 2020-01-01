package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpDistMap.java
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


import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;

import javax.media.jai.BorderExtender;
import javax.media.jai.DataBufferFloat;
import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpDistMapDescriptor;

/**
 * <li> 2010 09 erode of a constant image did not change the image any more
 *                 therefore, sometimes images did not converge to totally black
 *                 now it stops eroding if the image is constant
 * <li> 2011 12 29 added 4SED and 8SED method according to Danielsson P.E. Comp Graph ImgProc 14, 227-248, 1980
 * 
 * @author Ahammer, Kainz
 * @since   2010 05
 */
public class IqmOpDistMap extends AbstractOperator {

	public IqmOpDistMap() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	/**
	 * This method tests if image has a constant grey value
	 * 
	 * @param PlanarImage
	 *            pi
	 * @return isConstant boolean
	 */
	private boolean isConstant(PlanarImage pi) {
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(null); // ROI
		pb.add(1); // Sampling
		pb.add(1);
		// pb.add(new int[]{(int)typeGreyMax+1}); //Number of bins
		pb.add(new int[] { (int) typeGreyMax, (int) typeGreyMax,
				(int) typeGreyMax }); // Number of bins
		pb.add(new double[] { 0d, 0d, 0d }); // Start
		pb.add(new double[] { typeGreyMax, typeGreyMax, typeGreyMax }); // End
		// pb.add(new double[]{typeGreyMax + 1}); //End
		RenderedOp rendOp = JAI.create("histogram", pb);
		Histogram histo = (Histogram) rendOp.getProperty("histogram");

		boolean isConstant = false;
		for (int i = 0; i < typeGreyMax; i++) {
			// System.out.println("IqmOpDistMap i: " +1);
			int total = histo.getSubTotal(0, i, i);
			// if (total != 0 )System.out.println("IqmOpDistMap total: " +
			// total);
			if (total == pi.getWidth() * pi.getHeight()) {
				isConstant = true; // image with constant grey value found
				break;

			} else {
				isConstant = false;
			}
		}
		return isConstant;
	}

	/**
	 * This method calculates the distance map using morphological erosion
	 * 
	 * @param int kernelShape, int kernelSize, PlanarImage pi
	 * @return PlanarImage
	 */
	@SuppressWarnings("unused")
	private PlanarImage distMapErode(int kernelShape, int kernelSize,
			PlanarImage pi) {
		int width = pi.getWidth();
		int height = pi.getHeight();
		KernelJAI kernelJAI = null;
		if (kernelShape == 0) {
			// kernelJAI = KernelFactory.createRectangle(kernelWidth,
			// kernelHeight);
			int size = kernelSize * kernelSize;
			float[] kernel = new float[size];
			Arrays.fill(kernel, 0.0f); // Damit Hintergrund nicht auf 1 gesetzt
										// wird
			kernelJAI = new KernelJAI(kernelSize, kernelSize, kernel);
		}
		if (kernelShape == 1) {
			// kernel = KernelFactory.createCircle((kernelSize-1)/2); //Size =
			// radius*2 +1
			int size = kernelSize * kernelSize;
			float[] kernel = new float[size];
			// Arrays.fill(kernel, 1.0f);
			for (int i = 0; i < kernelSize; i++) {
				for (int j = 0; j < kernelSize; j++) {
					if ((i == (kernelSize / 2)) && (j == (kernelSize / 2))) {
						kernel[i * kernelSize + j] = 0.0f;
					} else {
						kernel[i * kernelSize + j] = 0.0f;
					}
				}
			}
			kernelJAI = new KernelJAI(kernelSize, kernelSize, kernel);
		}

		// Optional print kernel
		// System.out.println(KernelUtil.kernelToString(kernelJAI, true));

		ParameterBlock pb = new ParameterBlock();
		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY));

		// create empty image
		// PlanarImage resultImage = ConstantDescriptor.create((float) imgWidth,
		// (float) imgHeight, new Byte[]{0x0}, null);

		SampleModel sm = RasterFactory.createBandedSampleModel(
				DataBuffer.TYPE_FLOAT, width, height, 1); // 1 banded sample
															// model
		// createBandedSampleModel can yield a too bright display
		// SampleModel sm =
		// RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT,
		// imgWidth, imgHeight, 1); //1 banded sample model
		float[] floatArr = new float[width * height];
		DataBufferFloat db = new DataBufferFloat(floatArr, width * height);
		WritableRaster wrOut = RasterFactory.createWritableRaster(sm, db,
				new Point(0, 0));
		// create tiled image
		ColorModel cm = PlanarImage.createColorModel(sm); // compatible color
															// model
		TiledImage tiOut = new TiledImage(0, 0, width, height, 0, 0, sm, cm);
		tiOut.setData(wrOut);
		PlanarImage piOut = tiOut.createSnapshot();

		ParameterBlock pbErode = null;
		// pbErode.add(kernelJAI);
		ParameterBlock pbAdd = null;

		while (!isConstant(pi)) {
			if (isCancelled(getParentTask())){
				return null;
			}
			
			pbErode = new ParameterBlock();
			pbErode.add(kernelJAI);
			pbErode.setSource(pi, 0);
			pi = JAI.create("erode", pbErode, rh);
			// erode of a constant image doesn't change image
			// therefore, the loop leads sometimes to an image with a constant
			// grey value > zero
			// these images will never be totally black!

			pbAdd = new ParameterBlock();
			pbAdd.setSource(pi, 0);
			pbAdd.setSource(piOut, 1);
			piOut = JAI.create("Add", pbAdd, null);
		}
		return piOut;
	}

	/**
	 * This method transforms the Matrix L to a TiledImage
	 * 
	 * @param L
	 * @return TiledImage
	 */
	@SuppressWarnings("unused")
	private TiledImage getTiledImageFromL(float[][][] L) {

		int width = L.length;
		int height = L[0].length;
		ParameterBlock pb = new ParameterBlock();
		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY));

		// create empty image
		// PlanarImage resultImage = ConstantDescriptor.create((float) imgWidth,
		// (float) imgHeight, new Byte[]{0x0}, null);

		SampleModel sm = RasterFactory.createBandedSampleModel(
				DataBuffer.TYPE_FLOAT, width, height, 1); // 1 banded sample
															// model
		// createBandedSampleModel can yield a too bright display
		// SampleModel sm =
		// RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT,
		// imgWidth, imgHeight, 1); //1 banded sample model
		float[] floatArr = new float[width * height];

		DataBufferFloat db = new DataBufferFloat(floatArr, width * height);
		WritableRaster wr = RasterFactory.createWritableRaster(sm, db,
				new Point(0, 0));

		// copy data to image raster
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				wr.setSample(
						i,
						j,
						0,
						(float) Math.sqrt(L[i][j][0] * L[i][j][0] + L[i][j][1]
								* L[i][j][1]));
			}
		}
		// create tiled image
		ColorModel cm = PlanarImage.createColorModel(sm); // compatible color
															// model
		TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sm, cm);
		ti.setData(wr);
		return ti;
	}

	/**
	 * This method calculates the distance map using the the 8 point sequential
	 * algorithm according to Danielsson P.E. Comp Graph ImgProc 14, 227-248,
	 * 1980
	 * 
	 * @param double typeGreyMax, PlanarImage pi
	 * @return PlanarImage
	 */
	private PlanarImage distMap4SED(double typeGreyMax, PlanarImage pi) {
		int width = pi.getWidth();
		int height = pi.getHeight();

		// System.out.println("IqmOpDistMap: distMap4SED");
		// initialize L
		Raster r = pi.getData();
		float[][][] L = new float[width][height][2]; // vector for each image
														// point
		float diag = (float) Math.sqrt(width * width + height * height); // largest
																			// possible
																			// distance
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (r.getSample(i, j, 0) == typeGreyMax) {
					L[i][j][0] = diag;
					L[i][j][1] = diag;
				} else {
					L[i][j][0] = 0;
					L[i][j][1] = 0;
				}
			}
		}
		// First scan
		for (int j = 1; j < height; j++) {
			for (int i = 0; i < width; i++) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt(L[i][j - 1][0] * L[i][j - 1][0]
						+ (L[i][j - 1][1] + 1) * (L[i][j - 1][1] + 1));
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i][j - 1][0];
					L[i][j][1] = L[i][j - 1][1] + 1;
				}
			}
			for (int i = 1; i < width; i++) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i - 1][j][0] + 1)
						* (L[i - 1][j][0] + 1) + L[i - 1][j][1]
						* L[i - 1][j][1]);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i - 1][j][0] + 1;
					L[i][j][1] = L[i - 1][j][1];
				}
			}
			for (int i = width - 2; i >= 0; i--) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i + 1][j][0] + 1)
						* (L[i + 1][j][0] + 1) + L[i + 1][j][1]
						* L[i + 1][j][1]);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i + 1][j][0] + 1;
					L[i][j][1] = L[i + 1][j][1];
				}
			}

		}
		// Second scan
		for (int j = height - 2; j >= 0; j--) {
			for (int i = 0; i < width; i++) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt(L[i][j + 1][0] * L[i][j + 1][0]
						+ (L[i][j + 1][1] + 1) * (L[i][j + 1][1] + 1));
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i][j + 1][0];
					L[i][j][1] = L[i][j + 1][1] + 1;
				}
			}
			for (int i = 1; i < width; i++) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i - 1][j][0] + 1)
						* (L[i - 1][j][0] + 1) + L[i - 1][j][1]
						* L[i - 1][j][1]);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i - 1][j][0] + 1;
					L[i][j][1] = L[i - 1][j][1];
				}
			}
			for (int i = width - 2; i >= 0; i--) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i + 1][j][0] + 1)
						* (L[i + 1][j][0] + 1) + L[i + 1][j][1]
						* L[i + 1][j][1]);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i + 1][j][0] + 1;
					L[i][j][1] = L[i + 1][j][1];
				}
			}
		}
		TiledImage ti = getTiledImageFromL(L);
		// return PlanarImage
		return ti.createSnapshot();
	}

	/**
	 * This method calculates the distance map using the 8 point sequential
	 * algorithm according to to Danielsson P.E. Comp Graph ImgProc 14, 227-248,
	 * 1980
	 * 
	 * @param double typeGreyMax, PlanarImage pi
	 * @return PlanarImage
	 */
	private PlanarImage distMap8SED(double typeGreyMax, PlanarImage pi) {
		int width = pi.getWidth();
		int height = pi.getHeight();

		// System.out.println("IqmOpDistMap: distMap8SED");
		// initialize L
		Raster r = pi.getData();
		float[][][] L = new float[width][height][2]; // vector for each image
														// point
		float diag = (float) Math.sqrt(width * width + height * height); // largest
																			// possible
																			// distance
		float d = (float) (Math.sqrt(2)); // smallest diagonal distance
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (r.getSample(i, j, 0) == typeGreyMax) {
					// L[i][j][0] = Float.MAX_VALUE;
					// L[i][j][1] = Float.MAX_VALUE;
					// L[i][j][0] = (float)typeGreyMax*100;
					// L[i][j][1] = (float)typeGreyMax*100;
					L[i][j][0] = diag;
					L[i][j][1] = diag;
				} else {
					L[i][j][0] = 0;
					L[i][j][1] = 0;
				}
			}
		}
		// First scan
		for (int j = 1; j < height; j++) {
			for (int i = 0; i < width; i++) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2;
				if (i > 0) {
					dist2 = (float) Math.sqrt((L[i - 1][j - 1][0] + d)
							* (L[i - 1][j - 1][0] + d)
							+ (L[i - 1][j - 1][1] + d)
							* (L[i - 1][j - 1][1] + d));
				} else {
					dist2 = Float.MAX_VALUE;
				}
				float dist3 = (float) Math.sqrt(L[i][j - 1][0] * L[i][j - 1][0]
						+ (L[i][j - 1][1] + 1) * (L[i][j - 1][1] + 1));
				float dist4;
				if (i < (width - 1)) {
					dist4 = (float) Math.sqrt((L[i + 1][j - 1][0] + d)
							* (L[i + 1][j - 1][0] + d)
							+ (L[i + 1][j - 1][1] + d)
							* (L[i + 1][j - 1][1] + d));
				} else {
					dist4 = Float.MAX_VALUE;
				}
				// if (dist1<dist2 && dist1<dist3 && dist1<dist4) do nothing
				if (dist2 < dist1 && dist2 < dist3 && dist2 < dist4) {
					L[i][j][0] = L[i - 1][j - 1][0] + d;
					L[i][j][1] = L[i - 1][j - 1][1] + d;
				}
				if (dist3 < dist1 && dist3 < dist2 && dist3 < dist4) {
					L[i][j][0] = L[i][j - 1][0];
					L[i][j][1] = L[i][j - 1][1] + 1;
				}
				if (dist4 < dist1 && dist4 < dist2 && dist4 < dist3) {
					L[i][j][0] = L[i + 1][j - 1][0] + d;
					L[i][j][1] = L[i + 1][j - 1][1] + d;
				}
			}
			for (int i = 1; i < width; i++) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i - 1][j][0] + 1)
						* (L[i - 1][j][0] + 1) + L[i - 1][j][1]
						* L[i - 1][j][1]);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i - 1][j][0] + 1;
					L[i][j][1] = L[i - 1][j][1];
				}
			}
			for (int i = width - 2; i >= 0; i--) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i + 1][j][0] + 1)
						* (L[i + 1][j][0] + 1) + L[i + 1][j][1]
						* L[i + 1][j][1]);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i + 1][j][0] + 1;
					L[i][j][1] = L[i + 1][j][1];
				}
			}

		}
		// Second scan
		for (int j = height - 2; j >= 0; j--) {
			for (int i = 0; i < width; i++) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2;
				if (i > 0) {
					dist2 = (float) Math.sqrt((L[i - 1][j + 1][0] + d)
							* (L[i - 1][j + 1][0] + d)
							+ (L[i - 1][j + 1][1] + d)
							* (L[i - 1][j + 1][1] + d));
				} else {
					dist2 = Float.MAX_VALUE;
				}
				float dist3 = (float) Math.sqrt(L[i][j + 1][0] * L[i][j + 1][0]
						+ (L[i][j + 1][1] + 1) * (L[i][j + 1][1] + 1));
				float dist4;
				if (i < (width - 1)) {
					dist4 = (float) Math.sqrt((L[i + 1][j + 1][0] + d)
							* (L[i + 1][j + 1][0] + d)
							+ (L[i + 1][j + 1][1] + d)
							* (L[i + 1][j + 1][1] + d));
				} else {
					dist4 = Float.MAX_VALUE;
				}

				// if (dist1<dist2 && dist1<dist3 && dist1<dist4) do nothing
				if (dist2 < dist1 && dist2 < dist3 && dist2 < dist4) {
					L[i][j][0] = L[i - 1][j + 1][0] + d;
					L[i][j][1] = L[i - 1][j + 1][1] + d;
				}
				if (dist3 < dist1 && dist3 < dist2 && dist3 < dist4) {
					L[i][j][0] = L[i][j + 1][0];
					L[i][j][1] = L[i][j + 1][1] + 1;
				}
				if (dist4 < dist1 && dist4 < dist2 && dist4 < dist3) {
					L[i][j][0] = L[i + 1][j + 1][0] + d;
					L[i][j][1] = L[i + 1][j + 1][1] + d;
				}
			}
			for (int i = 1; i < width; i++) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i - 1][j][0] + 1)
						* (L[i - 1][j][0] + 1) + L[i - 1][j][1]
						* L[i - 1][j][1]);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i - 1][j][0] + 1;
					L[i][j][1] = L[i - 1][j][1];
				}
			}
			for (int i = width - 2; i >= 0; i--) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i + 1][j][0] + 1)
						* (L[i + 1][j][0] + 1) + L[i + 1][j][1]
						* L[i + 1][j][1]);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i + 1][j][0] + 1;
					L[i][j][1] = L[i + 1][j][1];
				}
			}
		}
		TiledImage ti = getTiledImageFromL(L);
		// return PlanarImage
		return ti.createSnapshot();
	}

	/**
	 * This method calculates the distance map using the 8 point sequential
	 * algorithm according to to Danielsson P.E. Comp Graph ImgProc 14, 227-248,
	 * 1980
	 * 
	 * @param double typeGreyMax, PlanarImage pi
	 * @return PlanarImage
	 */
	@SuppressWarnings("unused")
	private PlanarImage distMap8SEDShort(double typeGreyMax, PlanarImage pi) {
		// This version is not faster, indeed it is lightly slower!!!!!!!!!!!!!
		int width = pi.getWidth();
		int height = pi.getHeight();

		// System.out.println("IqmOpDistMap: distMap8SEDShort");
		// initialize L
		Raster r = pi.getData();
		float[][][] L = new float[width][height][2]; // vector for each image
														// point
		float diag = (float) Math.sqrt(width * width + height * height); // largest
																			// possible
																			// distance
		float d = (float) (Math.sqrt(2)); // smallest diagonal distance
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (r.getSample(i, j, 0) == typeGreyMax) {
					L[i][j][0] = diag;
					L[i][j][1] = diag;
				} else {
					L[i][j][0] = 0;
					L[i][j][1] = 0;
				}
			}
		}
		float a1 = 0, b1 = 0;
		float a2 = 0, b2 = 0;
		float a3 = 0, b3 = 0;
		float a4 = 0, b4 = 0;
		// First scan
		for (int j = 1; j < height; j++) {
			for (int i = 0; i < width; i++) {
				a1 = L[i][j][0];
				b1 = L[i][j][1];
				float dist1 = (float) Math.sqrt(a1 * a1 + b1 * b1);
				float dist2;
				if (i > 0) {
					a2 = L[i - 1][j - 1][0] + d;
					b2 = L[i - 1][j - 1][1] + d;
					dist2 = (float) Math.sqrt(a2 * a2 + b2 * b2);
				} else {
					dist2 = Float.MAX_VALUE;
				}
				a3 = L[i][j - 1][0];
				b3 = L[i][j - 1][1] + 1;
				float dist3 = (float) Math.sqrt(a3 * a3 + b3 * b3);
				float dist4;
				if (i < (width - 1)) {
					a4 = L[i + 1][j - 1][0] + d;
					b4 = L[i + 1][j - 1][1] + d;
					dist4 = (float) Math.sqrt(a4 * a4 + b4 * b4);
				} else {
					dist4 = Float.MAX_VALUE;
				}
				// if (dist1<dist2 && dist1<dist3 && dist1<dist4) do nothing
				if (dist2 < dist1 && dist2 < dist3 && dist2 < dist4) {
					L[i][j][0] = a2;
					L[i][j][1] = b2;
				}
				if (dist3 < dist1 && dist3 < dist2 && dist3 < dist4) {
					L[i][j][0] = a3;
					L[i][j][1] = b3;
				}
				if (dist4 < dist1 && dist4 < dist2 && dist4 < dist3) {
					L[i][j][0] = a4;
					L[i][j][1] = b4;
				}
			}
			for (int i = 1; i < width; i++) {
				a1 = L[i][j][0];
				b1 = L[i][j][1];
				float dist1 = (float) Math.sqrt(a1 * a1 + b1 * b1);
				a2 = L[i - 1][j][0] + 1;
				b2 = L[i - 1][j][1];
				float dist2 = (float) Math.sqrt(a2 * a2 + b2 * b2);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = a2;
					L[i][j][1] = b2;
				}
			}
			for (int i = width - 2; i >= 0; i--) {
				a1 = L[i][j][0];
				b1 = L[i][j][1];
				float dist1 = (float) Math.sqrt(a1 * a1 + b1 * b1);
				a2 = L[i + 1][j][0] + 1;
				b2 = L[i + 1][j][1];
				float dist2 = (float) Math.sqrt(a2 * a2 + b2 * b2);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = a2;
					L[i][j][1] = b2;
				}
			}

		}
		// Second scan
		for (int j = height - 2; j >= 0; j--) {
			for (int i = 0; i < width; i++) {
				a1 = L[i][j][0];
				b1 = L[i][j][1];
				float dist1 = (float) Math.sqrt(a1 * a1 + b1 * b1);
				float dist2;
				if (i > 0) {
					a2 = L[i - 1][j + 1][0] + d;
					b2 = L[i - 1][j + 1][1] + d;
					dist2 = (float) Math.sqrt(a2 * a2 + b2 * b2);
				} else {
					dist2 = Float.MAX_VALUE;
				}
				a3 = L[i][j + 1][0];
				b3 = L[i][j + 1][1] + 1;
				float dist3 = (float) Math.sqrt(a3 * a3 + b3 * b3);
				float dist4;
				if (i < (width - 1)) {
					a4 = L[i + 1][j + 1][0] + d;
					b4 = L[i + 1][j + 1][1] + d;
					dist4 = (float) Math.sqrt(a4 * a4 + b4 * b4);
				} else {
					dist4 = Float.MAX_VALUE;
				}

				// if (dist1<dist2 && dist1<dist3 && dist1<dist4) do nothing
				if (dist2 < dist1 && dist2 < dist3 && dist2 < dist4) {
					L[i][j][0] = a2;
					L[i][j][1] = b2;
				}
				if (dist3 < dist1 && dist3 < dist2 && dist3 < dist4) {
					L[i][j][0] = a3;
					L[i][j][1] = b3;
				}
				if (dist4 < dist1 && dist4 < dist2 && dist4 < dist3) {
					L[i][j][0] = a4;
					L[i][j][1] = b4;
				}
			}
			for (int i = 1; i < width; i++) {
				a1 = L[i][j][0];
				b1 = L[i][j][1];
				float dist1 = (float) Math.sqrt(a1 * a1 + b1 * b1);
				a2 = L[i - 1][j][0] + 1;
				b2 = L[i - 1][j][1];
				float dist2 = (float) Math.sqrt(a2 * a2 + b2 * b2);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = a2;
					L[i][j][1] = b2;
				}
			}
			for (int i = width - 2; i >= 0; i--) {
				a1 = L[i][j][0];
				b1 = L[i][j][1];
				float dist1 = (float) Math.sqrt(a1 * a1 + b1 * b1);
				a2 = L[i + 1][j][0] + 1;
				b2 = L[i + 1][j][1];
				float dist2 = (float) Math.sqrt(a2 * a2 + b2 * b2);
				// if (dist1 < dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = a2;
					L[i][j][1] = b2;
				}
			}
		}
		TiledImage ti = getTiledImageFromL(L);
		// return PlanarImage
		return ti.createSnapshot();
	}

	/**
	 * This method calculates the distance map using the 8 point sequential
	 * algorithm adapted by Grevera G.J.
	 * 
	 * @param double typeGreyMax, PlanarImage pi
	 * @return PlanarImage
	 */
	private PlanarImage distMap8SEDGrevera(double typeGreyMax, PlanarImage pi) {
		int width = pi.getWidth();
		int height = pi.getHeight();

		// System.out.println("IqmOpDistMap: distMap8SEDGrevera");
		// initialize L
		Raster r = pi.getData();
		float[][][] L = new float[width][height][2]; // vector for each image
														// point
		float diag = (float) Math.sqrt(width * width + height * height); // largest
																			// possible
																			// distance
		float d = (float) (Math.sqrt(2)); // smallest diagonal distance
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (r.getSample(i, j, 0) == typeGreyMax) {
					L[i][j][0] = diag;
					L[i][j][1] = diag;
				} else {
					L[i][j][0] = 0;
					L[i][j][1] = 0;
				}
			}
		}
		// First scan
		// boolean check1 = false;
		// boolean check2 = false;
		for (int j = 1; j < height; j++) {
			for (int i = 0; i < width; i++) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt(L[i][j - 1][0] * L[i][j - 1][0]
						+ (L[i][j - 1][1] + 1) * (L[i][j - 1][1] + 1));

				// if (dist1<dist2) do nothing
				if (dist2 < dist1) {
					L[i][j][0] = L[i][j - 1][0];
					L[i][j][1] = L[i][j - 1][1] + 1;
				}
			}
			for (int i = 1; i < width; i++) {
				// check1 = false;
				// check2 = false;
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i - 1][j][0] + 1)
						* (L[i - 1][j][0] + 1) + L[i - 1][j][1]
						* L[i - 1][j][1]);
				float dist3 = (float) Math.sqrt((L[i - 1][j - 1][0] + d)
						* (L[i - 1][j - 1][0] + d) + (L[i - 1][j - 1][1] + d)
						* (L[i - 1][j - 1][1] + d));
				// if (dist1 < dist2 && dist1<dist3) do nothing
				if (dist2 < dist1 && dist2 < dist3) {
					L[i][j][0] = L[i - 1][j][0] + 1;
					L[i][j][1] = L[i - 1][j][1];
					// check1 = true;
				}
				if (dist3 < dist1 && dist3 < dist2) {
					L[i][j][0] = L[i - 1][j - 1][0] + d;
					L[i][j][1] = L[i - 1][j - 1][1] + d;
					// check2 = true;
					// if (check1 && check2
					// )System.out.println("IqmOpdistMap: Check 1 and Check2 is true!!!!!!!");

				}
			}
			for (int i = width - 2; i >= 0; i--) {
				// check1 = false;
				// check2 = false;
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i + 1][j][0] + 1)
						* (L[i + 1][j][0] + 1) + L[i + 1][j][1]
						* L[i + 1][j][1]);
				float dist3 = (float) Math.sqrt((L[i + 1][j - 1][0] + d)
						* (L[i + 1][j - 1][0] + d) + (L[i + 1][j - 1][1] + d)
						* (L[i + 1][j - 1][1] + d));
				// if (dist1<dist2 && dist1 <dist3) do nothing
				if (dist2 < dist1 && dist2 < dist3) {
					L[i][j][0] = L[i + 1][j][0] + 1;
					L[i][j][1] = L[i + 1][j][1];
					// check1 = true;
				}
				if (dist3 < dist1 && dist3 < dist2) {
					L[i][j][0] = L[i + 1][j - 1][0] + d;
					L[i][j][1] = L[i + 1][j - 1][1] + d;
					// check2 = true;
					// if (check1 && check2
					// )System.out.println("IqmOpdistMap: Check 1 and Check2 is true!!!!!!!");

				}
			}

		}
		// Second scan
		for (int j = height - 2; j >= 0; j--) {
			for (int i = 0; i < width; i++) {
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt(L[i][j + 1][0] * L[i][j + 1][0]
						+ (L[i][j + 1][1] + 1) * (L[i][j + 1][1] + 1));
				// if (dist1<dist2) do nothing

				if (dist2 < dist1) {
					L[i][j][0] = L[i][j + 1][0];
					L[i][j][1] = L[i][j + 1][1] + 1;
				}
			}
			for (int i = 1; i < width; i++) {
				// check1 = false;
				// check2 = false;
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i - 1][j][0] + 1)
						* (L[i - 1][j][0] + 1) + L[i - 1][j][1]
						* L[i - 1][j][1]);
				float dist3 = (float) Math.sqrt((L[i - 1][j + 1][0] + d)
						* (L[i - 1][j + 1][0] + d) + (L[i - 1][j + 1][1] + d)
						* (L[i - 1][j + 1][1] + d));
				// if (dist1 < dist2 && dist1<dist3) do nothing
				if (dist2 < dist1 && dist2 < dist3) {
					L[i][j][0] = L[i - 1][j][0] + 1;
					L[i][j][1] = L[i - 1][j][1];
					// check1 = true;
				}
				if (dist3 < dist1 && dist3 < dist2) {
					L[i][j][0] = L[i - 1][j + 1][0] + d;
					L[i][j][1] = L[i - 1][j + 1][1] + d;
					// check2 = true;
					// if (check1 && check2
					// )System.out.println("IqmOpdistMap: Check 1 and Check2 is true!!!!!!!");
				}
			}
			for (int i = width - 2; i >= 0; i--) {
				// check1 = false;
				// check2 = false;
				float dist1 = (float) Math.sqrt(L[i][j][0] * L[i][j][0]
						+ L[i][j][1] * L[i][j][1]);
				float dist2 = (float) Math.sqrt((L[i + 1][j][0] + 1)
						* (L[i + 1][j][0] + 1) + L[i + 1][j][1]
						* L[i + 1][j][1]);
				float dist3 = (float) Math.sqrt((L[i + 1][j + 1][0] + d)
						* (L[i + 1][j + 1][0] + d) + (L[i + 1][j + 1][1] + d)
						* (L[i + 1][j + 1][1] + d));
				// if (dist1<dist2 && dist1 <dist3) do nothing
				if (dist2 < dist1 && dist2 < dist3) {
					L[i][j][0] = L[i + 1][j][0] + 1;
					L[i][j][1] = L[i + 1][j][1];
					// check1 = true;
				}
				if (dist3 < dist1 && dist3 < dist2) {
					L[i][j][0] = L[i + 1][j + 1][0] + d;
					L[i][j][1] = L[i + 1][j + 1][1] + d;
					// check2 = true;
					// if (check1 && check2
					// )System.out.println("IqmOpdistMap: Check 1 and Check2 is true!!!!!!!");

				}
			}
		}
		TiledImage ti = getTiledImageFromL(L);
		// return PlanarImage
		return ti.createSnapshot();
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

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();

		int kernelShape = pb.getIntParameter("KernelShape");
		int kernelSize = pb.getIntParameter("KernelSize");
		int resultOptions = pb.getIntParameter("ResultOptions");
		int method = pb.getIntParameter("Method");

		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));
		String type = ImageTools.getImgType(pi);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

		if (method == 0)
			piOut = distMapErode(kernelShape, kernelSize, pi);
		if (method == 1)
			piOut = distMap4SED(typeGreyMax, pi); // 4 point sequential distance
													// mapping
		if (method == 2)
			piOut = distMap8SED(typeGreyMax, pi); // 8 point sequential distance
													// mapping
		if (method == 3)
			piOut = distMap8SEDGrevera(typeGreyMax, pi); // 8 point sequential
															// distance mapping
															// adapted by
															// Grevera
		// if (method == 3) piOut = distMap8SEDShort(typeGreyMax, pi); //8 point
		// sequential distance mapping adapted by Grevera
		
		if (piOut == null || isCancelled(getParentTask())){
			return null;
		}
		
		ParameterBlock pbFormat = null;

		if (resultOptions == 0) { // clamp to byte
			pbFormat = new ParameterBlock();
			pbFormat.addSource(piOut);
			if (type.equals(IQMConstants.IMAGE_TYPE_RGB))
				pbFormat.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))
				pbFormat.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))
				pbFormat.add(DataBuffer.TYPE_USHORT);
			
			piOut = JAI.create("format", pbFormat);
		}
		if (resultOptions == 1) { // normalize to type
			pbFormat = new ParameterBlock();
			pbFormat.addSource(piOut);
			RenderedOp extrema = JAI.create("extrema", pbFormat);
			double[] minVec = (double[]) extrema.getProperty("minimum");
			double[] maxVec = (double[]) extrema.getProperty("maximum");
			double min = minVec[0];
			double max = maxVec[0];

			pbFormat = new ParameterBlock();
			pbFormat.addSource(piOut);
			pbFormat.add(new double[] { (typeGreyMax / (max - min)) }); // Rescale
			pbFormat.add(new double[] { ((typeGreyMax * min) / (min - max)) }); // offset
			piOut = JAI.create("rescale", pbFormat);

			pbFormat = new ParameterBlock();
			pbFormat.addSource(piOut);
			if (type.equals(IQMConstants.IMAGE_TYPE_RGB))
				pbFormat.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))
				pbFormat.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))
				pbFormat.add(DataBuffer.TYPE_USHORT);
			
			piOut = JAI.create("format", pbFormat);
		}

		if (resultOptions == 2) { // Actual
			// does nothing
		}

		ImageModel im = new ImageModel(piOut);
		im.setModelName(imgName);
		im.setFileName(fileName);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpDistMapDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpDistMapDescriptor.TYPE;
	}

}
