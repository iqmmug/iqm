package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpNexelScan.java
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


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferDouble;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.BorderExtender;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;
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
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpNexelScanDescriptor;

/**
 * @author Ahammer, Kleinoscheg, Kainz
 * @since 2011 12 
 */
public class IqmOpNexelScan extends AbstractOperator {

	public IqmOpNexelScan() {
	}

	private double typeGrayMax = 255.0; // default

	@SuppressWarnings("unused")
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
		int greyTolerance = pb.getIntParameter("GreyTolerance");
		int method = pb.getIntParameter("Method"); // //0 Range, 1 Mean, 2
													// StdDev, 3Energy, 4
													// Entropy, 5 Skewness,
													// 6 Kurtosis
		int resultOptions = pb.getIntParameter("ResultOptions");// 0 clamp to
																// byte , 1
																// normalize
																// to byte,
																// 2 actual

		typeGrayMax = ImageTools.getImgTypeGreyMax(pi);
		// check that kernelSize is odd
		// kernelSize = (kernelSize%2==0)?kernelSize+1:kernelSize;

		// how the border should be calculated: 0 (ZERO), 1 (CONST), 2 (COPY), 3
		// (REFLECT) or 4 (WRAP)
		int borderMethod = 2; // TODO: GUI - selection
		double constBorderValue = 127; // TODO: GUI for constant Bordervalue
										// input

		// select if JAI-Histogram should be used for calculation (=0 -
		// calcStatisticsForPx uses ImageJAI) or
		// if custom calculation for parameters should be used (=1 -
		// customStatisticsForPx uses custom Array)

		int numBands = pi.getData().getNumBands();
		String fileName = String.valueOf(pi.getProperty("file_name"));
		String imgName = String.valueOf(pi.getProperty("image_name"));

		// value for CONST-resized-Border
		double[] borderValue = new double[numBands];
		for (int i = 0; i < numBands; i++) {
			borderValue[i] = constBorderValue;
		}

		// add border to pi and get piWithBorder for calculating parameters
		PlanarImage piWithBorder = addBorder(pi, kernelSize, borderMethod,
				borderValue);

		int width = pi.getWidth();
		int height = pi.getHeight();
		SampleModel sm = RasterFactory.createPixelInterleavedSampleModel(
				DataBuffer.TYPE_DOUBLE, width, height, 1);
		double[] doubleArr = new double[width * height];
		DataBufferDouble db = new DataBufferDouble(doubleArr, width * height);
		WritableRaster wr = RasterFactory.createWritableRaster(sm, db,
				new Point(0, 0));

		scanImage(wr, piWithBorder, kernelSize, method, greyTolerance); // greyTolerance
																		// only
																		// for
																		// alikeness
		// or
		// scanImageWithROI(wr, piWithBorder, kernelSize, method); //very slow,
		// only Mean StDev and Entropy!!!!

		ColorModel cm = PlanarImage.createColorModel(sm); // compatible color
															// model
		TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sm, cm);
		ti.setData(wr);

		// ----------------output
		// generation--------------------------------------
		ParameterBlock pbWork = new ParameterBlock();
		if (resultOptions == 0) { // clamp to byte
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(ti);
			pbWork.add(DataBuffer.TYPE_BYTE);
			/*
			 * if (type == "RGB") pb.add(DataBuffer.TYPE_BYTE); if (type ==
			 * "8 bit") pb.add(DataBuffer.TYPE_BYTE); if (type == "16 bit")
			 * pb.add(DataBuffer.TYPE_USHORT);
			 */
			piOut = JAI.create("format", pbWork);
		}
		if (resultOptions == 1) { // normalize to byte
			double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(ti);
			RenderedOp extrema = JAI.create("extrema", pbWork);
			double[] minVec = (double[]) extrema.getProperty("minimum");
			double[] maxVec = (double[]) extrema.getProperty("maximum");
			double min = minVec[0];
			double max = maxVec[0];

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(ti);
			pbWork.add(new double[] { (typeGreyMax / (max - min)) }); // Rescale
			pbWork.add(new double[] { ((typeGreyMax * min) / (min - max)) }); // offset
			piOut = JAI.create("rescale", pbWork);

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			pbWork.add(DataBuffer.TYPE_BYTE);
			/*
			 * if (type == "RGB") pb.add(DataBuffer.TYPE_BYTE); if (type ==
			 * "8 bit") pb.add(DataBuffer.TYPE_BYTE); if (type == "16 bit")
			 * pb.add(DataBuffer.TYPE_USHORT);
			 */
			piOut = JAI.create("format", pbWork);
		}

		if (resultOptions == 2) { // Actual
			// does nothing
			piOut = ti;
		}

		ImageModel im = new ImageModel(piOut);
		im.setFileName(fileName);
		im.setModelName(imgName);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpNexelScanDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpNexelScanDescriptor.TYPE;
	}

	/**
	 * Adds a border to the PlanarImage.
	 * 
	 * @param pi
	 * @param kernelSize
	 * @param borderMethod
	 * @param borderValue
	 * @return the image with the border
	 */
	private PlanarImage addBorder(PlanarImage pi, int kernelSize,
			int borderMethod, double[] borderValue) {
		int toadd = (kernelSize) / 2;
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(toadd); // left
		pb.add(toadd); // right
		pb.add(toadd); // top
		pb.add(toadd); // bottom
		if (borderMethod == 0)
			pb.add(BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
		if (borderMethod == 1)
			pb.add(new BorderExtenderConstant(borderValue));
		if (borderMethod == 2)
			pb.add(BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		if (borderMethod == 3)
			pb.add(BorderExtender.createInstance(BorderExtender.BORDER_REFLECT));
		if (borderMethod == 4)
			pb.add(BorderExtender.createInstance(BorderExtender.BORDER_WRAP));
		PlanarImage piNew = JAI.create("Border", pb, null);
		if (piNew.getMinX() != 0 || piNew.getMinY() != 0) {
			ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
			pbTrans.addSource(piNew);
			pbTrans.setParameter("xTrans", piNew.getMinX() * -1.0f);
			pbTrans.setParameter("yTrans", piNew.getMinY() * -1.0f);
			piNew = JAI.create("translate", pbTrans);
		}
		return piNew;
	}

	/**
	 * Calculates different parameters based on JAI API. Slow! only Mean StDev
	 * and Entropy!!!!
	 * 
	 * @param wr
	 * @param piWithBorder
	 * @param kernelSize
	 * @param statMethod
	 */
	@SuppressWarnings("unused")
	private void scanImageWithROI(WritableRaster wr, PlanarImage piWithBorder,
			int kernelSize, int method) {
		int width = wr.getWidth(); // imgWidth of the final Image
		int height = wr.getHeight(); // imgHeight of the final Image
		double result = 0;
		RenderedOp rOp;
		Histogram histo;

		// get a writable raster for the calculation
		Raster r = piWithBorder.getData();
		WritableRaster wrForCalc = r.createCompatibleWritableRaster(
				piWithBorder.getMinX(), piWithBorder.getMinY(),
				piWithBorder.getWidth(), piWithBorder.getHeight());
		wrForCalc.setRect(r);

		// Parameters for TiledImage
		int minX = piWithBorder.getMinX();
		int minY = piWithBorder.getMinY();
		int tileGridXOffset = piWithBorder.getTileGridXOffset();
		int tileGridYOffset = piWithBorder.getTileGridYOffset();
		SampleModel sampleModel = piWithBorder.getSampleModel();
		ColorModel colorModel = piWithBorder.getColorModel();

		// Working with ParameterBlockJAI
		ParameterBlockJAI pbjai = new ParameterBlockJAI("histogram");
		Rectangle2D rect = new Rectangle();
		TiledImage ti = new TiledImage(minX, minY, wrForCalc.getWidth(),
				wrForCalc.getHeight(), tileGridXOffset, tileGridYOffset,
				sampleModel, colorModel);
		ti.setData(wrForCalc);
		pbjai.addSource(ti);

		// for each pixel calculation of requested statistical parameter
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// Working with ParameterBlockJAI
				rect.setRect(i, j, kernelSize, kernelSize);
				pbjai.setParameter("roi", new ROIShape(rect));

				rOp = JAI.create("Histogram", pbjai); // very time consuming

				histo = (Histogram) rOp.getProperty("histogram");

				switch (method) {
				case 1:
					result = histo.getMean()[0];
					break;
				case 2:
					result = histo.getStandardDeviation()[0];
					break;
				case 4:
					result = histo.getEntropy()[0];
					break;
				// System.out.println("IqmOpNexelScan: This case is not implemented!");
				}
				wr.setSample(i, j, 0, result);
			}
		}
	}

	/**
	 * Calculates statistic range for kernel-array.
	 * 
	 * @param kernel
	 * @return the range
	 */
	private double calcRange(double[] kernel) {
		double min = typeGrayMax;
		double max = 0d;
		for (double d : kernel) {
			if (d > max)
				max = d;
			if (d < min)
				min = d;
		}
		return max - min;
	}

	/**
	 * Calculates statistic mean for kernel-array.
	 * 
	 * @param kernel
	 * @return the mean
	 */
	private double calcMean(double[] kernel) {
		double sum = 0;
		for (double d : kernel) {
			sum += d;
		}
		return sum / kernel.length;
	}

	/**
	 * Calculates statistic standard deviation for kernel-array.
	 * 
	 * @param kernel
	 * @return the standard deviation
	 */
	private double calcStandardDeviation(double[] kernel) {
		double mean = calcMean(kernel);
		double variance = 0;
		for (double d : kernel) {
			variance = variance + ((d - mean) * (d - mean));
		}
		variance = variance / (kernel.length - 1); // 1/(n-1) is used by
													// histo.getStandardDeviation()
													// too
		return Math.sqrt(variance);
	}

	/**
	 * Calculates statistic variance for kernel-array.
	 * 
	 * @param kernel
	 * @return the variance
	 */
	private double calcVariance(double[] kernel) {
		double mean = calcMean(kernel);
		double variance = 0;
		for (double d : kernel) {
			variance = variance + ((d - mean) * (d - mean));
		}
		variance = variance / (kernel.length - 1); // 1/(n-1) is used by
													// histo.getStandardDeviation()
													// too
		return variance;
	}

	/**
	 * Calculates statistic moment3 for kernel-array.
	 * 
	 * @param kernel
	 * @return the 3rd moment
	 */
	private double calcMom3(double[] kernel) {
		double mean = calcMean(kernel);
		double mom = 0;
		for (double d : kernel) {
			mom = mom + ((d - mean) * (d - mean) * (d - mean));
		}
		mom = mom / (kernel.length - 1); // 1/(n-1) is used by
											// histo.getStandardDeviation() too
		return mom;
	}

	/**
	 * Calculates statistic moment4 for kernel-array.
	 * 
	 * @param kernel
	 * @return the 4th moment
	 */
	private double calcMom4(double[] kernel) {
		double mean = calcMean(kernel);
		double mom = 0;
		for (double d : kernel) {
			mom = mom + ((d - mean) * (d - mean) * (d - mean) * (d - mean));
		}
		mom = mom / (kernel.length - 1); // 1/(n-1) is used by
											// histo.getStandardDeviation() too
		return mom;
	}

	/**
	 * calculates statistic energy for kernel-array.
	 * 
	 * @param kernel
	 * @return energy
	 */
	private double calcEnergy(double[] kernel) {
		int size = kernel.length;
		double energy = 0;
		double[] histo = new double[(int) typeGrayMax + 1];
		for (double d : kernel) {
			histo[(int) d] = histo[(int) d] + 1;
		}
		for (double p : histo) {
			if (p > 0) {
				p = p / (size);
				energy = energy + p * p;
			}
		}
		return energy;
	}

	/**
	 * calculates statistic entropy for kernel-array.
	 * 
	 * @param kernel
	 * @return entropy
	 */
	private double calcEntropy(double[] kernel) {
		int size = kernel.length;
		double entropy = 0;
		double[] histo = new double[(int) typeGrayMax + 1];
		for (double d : kernel) {
			histo[(int) d] = histo[(int) d] + 1;
		}
		for (double p : histo) {
			if (p > 0) {
				p = p / (size);
				entropy = entropy + (p * Math.log(p) / Math.log(2));
			}
		}
		return -entropy;
	}

	/**
	 * calculates statistic Skewness for kernel-array.
	 * 
	 * @param kernel
	 * @return skewness
	 */
	private double calcSkewness(double[] kernel) {
		double skewness = 0;

		double mom3 = this.calcMom3(kernel);
		double stDev = this.calcStandardDeviation(kernel);
		skewness = mom3 / (stDev * stDev * stDev);

		return skewness;
	}

	/**
	 * calculates statistic Kurtosis for kernel-array.
	 * 
	 * @param kernel
	 * @return kurtosis
	 */
	private double calcKurtosis(double[] kernel) {
		double kurtosis = 0;

		double mom4 = this.calcMom4(kernel);
		double mom2 = this.calcVariance(kernel);
		kurtosis = mom4 / (mom2 * mom2); // -3 would be "Exzess" and not
											// Kurtosis, see Wikipedia

		return kurtosis;
	}

	/**
	 * calculates Alikeness for kernel-array. Alikeness is the number of pixels
	 * that have the same grey value as the center pixel a tolerance percentage
	 * can be set
	 * 
	 * @param kernel
	 * @param greyTolerance
	 * @return alikeness
	 */
	private double calcAlikeness(double[] kernel, double greyTolerance) {
		double alikeness = 0;
		int size = kernel.length;

		double greyCenter = kernel[(size - 1) / 2];
		// System.out.println("IqmOpNexelScan: (size-1)/2:" + ((size-1)/2) +
		// "     greyCenter:" + greyCenter);
		double greyDiff = greyCenter / 100.0 * greyTolerance;
		double greyMax = greyCenter + greyDiff;
		double greyMin = greyCenter - greyDiff;

		for (double g : kernel) {
			if (g <= greyMax && g >= greyMin)
				alikeness = alikeness + 1;
		}
		return alikeness;
	}

	/**
	 * Calculates different parameters based on arrays.
	 * 
	 * @param wr
	 * @param piWithBorder
	 * @param kernelSize
	 * @param statMethod
	 * @param method
	 * @param greyTolerance
	 */
	private void scanImage(WritableRaster wr, PlanarImage piWithBorder,
			int kernelSize, int method, int greyTolerance) {
		int width = wr.getWidth(); // imgWidth of the final Image
		int height = wr.getHeight(); // imgHeight of the final Image
		double result = 0.0;
		double[] kernel = new double[kernelSize * kernelSize];

		// get a raster for the calculation
		Raster r = piWithBorder.getData(); // image has added border

		// for each pixel calculation of requested parameter
		int numIterations = width;

		for (int i = 0; i < width; i++) {
			int proz = (i + 1) * 100 / numIterations;
			this.fireProgressChanged(proz);
			for (int j = 0; j < height; j++) {
				kernel = r.getSamples(i, j, kernelSize, kernelSize, 0, kernel);
				switch (method) {
				case 0:
					result = calcRange(kernel);
					break;
				case 1:
					result = calcMean(kernel);
					break;
				case 2:
					result = calcStandardDeviation(kernel);
					break;
				case 3:
					result = calcEnergy(kernel);
					break;
				case 4:
					result = calcEntropy(kernel);
					break;
				case 5:
					result = calcSkewness(kernel);
					if (Double.isNaN(result)) result = 0.0; //e.g.for identical values Skewness is NaN
					break;
				case 6:
					result = calcKurtosis(kernel);
					if (Double.isNaN(result)) result = 0.0; //e.g.for identical values Kurtosis is NaN
					break;
				case 7:
					result = calcAlikeness(kernel, greyTolerance);
					break; // 10% alikeness
				}
				if (Double.isNaN(result)) result = 0.0;  
				wr.setSample(i, j, 0, result);
			}
		}
	}

}
