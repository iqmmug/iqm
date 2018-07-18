package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpStackStat.java
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
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferDouble;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;
import javax.swing.DefaultListModel;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.api.workflow.IManager;
import at.mug.iqm.api.workflow.ITank;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpStackStatDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2012 01
 */
public class IqmOpStackStat extends AbstractOperator {

	public IqmOpStackStat() {
	}

	private double typeGrayMax = 255.0; // default

	@SuppressWarnings("rawtypes")
	@Override
	public IResult run(IWorkPackage wp) {

		PlanarImage piOut = null;

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();// input image
		int greyTolerance = pb.getIntParameter("GreyTolerance");
		int method = pb.getIntParameter("Method"); // //0 Range, 1 Mean, 2 StdDev, 3Energy, 4 Entropy, 5 Skewness, 6 Kurtosis
		int resultOptions = pb.getIntParameter("ResultOptions");// 0 clamp to byte , 1 normalize to byte, 2 actual

		typeGrayMax = ImageTools.getImgTypeGreyMax(pi);

		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		int width = pi.getWidth();
		int height = pi.getHeight();
		int numBands = pi.getNumBands();

		SampleModel sm = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_DOUBLE, width, height, numBands);
		double[] doubleArr = new double[width * height * numBands];
		DataBufferDouble db = new DataBufferDouble(doubleArr, width * height * numBands);
		WritableRaster wr = RasterFactory.createWritableRaster(sm, db, new Point(0, 0));

		// get image stack
		DefaultListModel model = null;
		IManager mgr = Application.getManager();
		ITank tank = Application.getTank();
		if (tank.getCurrIndex() == mgr.getTankIndexInMainLeft()) {
			model = mgr.getManagerModelLeft();
		}
		if (tank.getCurrIndex() == mgr.getTankIndexInMainRight()) {
			model = mgr.getManagerModelRight();
		}
		int nStackMax = model.getSize();
		scanImage(wr, nStackMax, numBands, method, greyTolerance); // greyTolerance only for alikeness

		if (isCancelled(getParentTask())) return null;
		if (wr == null) return null;
		
		ColorModel cm = PlanarImage.createColorModel(sm); // compatible color
															// model
		TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sm, cm);
		ti.setData(wr);

		// ----------------output generation--------------------------------------
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
			this.name = new IqmOpStackStatDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpStackStatDescriptor.TYPE;
	}

	/**
	 * Calculates statistic range for data-vector.
	 * 
	 * @param data
	 * @return the range
	 */
	private double calcRange(double[] data) {
		double min = typeGrayMax;
		double max = 0d;
		for (double d : data) {
			if (d > max)
				max = d;
			if (d < min)
				min = d;
		}
		return max - min;
	}

	/**
	 * Calculates statistic mean for data-vector.
	 * 
	 * @param data
	 * @return the mean
	 */
	private double calcMean(double[] data) {
		double sum = 0;
		for (double d : data) {
			sum += d;
		}
		return sum / data.length;
	}

	/**
	 * Calculates statistic standard deviation for data-vector.
	 * 
	 * @param data
	 * @return the standard deviation
	 */
	private double calcStandardDeviation(double[] data) {
		double mean = calcMean(data);
		double variance = 0;
		for (double d : data) {
			variance = variance + ((d - mean) * (d - mean));
		}
		variance = variance / (data.length - 1); // 1/(n-1) is used by
													// histo.getStandardDeviation()
													// too
		return Math.sqrt(variance);
	}

	/**
	 * Calculates statistic variance for data-vector.
	 * 
	 * @param data
	 * @return the variance
	 */
	private double calcVariance(double[] data) {
		double mean = calcMean(data);
		double variance = 0;
		for (double d : data) {
			variance = variance + ((d - mean) * (d - mean));
		}
		variance = variance / (data.length - 1); // 1/(n-1) is used by
													// histo.getStandardDeviation()
													// too
		return variance;
	}

	/**
	 * Calculates statistic moment3 for data-vector.
	 * 
	 * @param data
	 * @return the 3rd moment
	 */
	private double calcMom3(double[] data) {
		double mean = calcMean(data);
		double mom = 0;
		for (double d : data) {
			mom = mom + ((d - mean) * (d - mean) * (d - mean));
		}
		mom = mom / (data.length - 1); // 1/(n-1) is used by
										// histo.getStandardDeviation() too
		return mom;
	}

	/**
	 * Calculates statistic moment4 for data-vector.
	 * 
	 * @param data
	 * @return the 4th moment
	 */
	private double calcMom4(double[] data) {
		double mean = calcMean(data);
		double mom = 0;
		for (double d : data) {
			mom = mom + ((d - mean) * (d - mean) * (d - mean) * (d - mean));
		}
		mom = mom / (data.length - 1); // 1/(n-1) is used by
										// histo.getStandardDeviation() too
		return mom;
	}

	/**
	 * calculates statistic energy for data-vector.
	 * 
	 * @param data
	 * @return energy
	 */
	private double calcEnergy(double[] data) {
		int size = data.length;
		double energy = 0;
		double[] histo = new double[(int) typeGrayMax + 1];
		for (double d : data) {
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
	 * calculates statistic entropy for data-vector.
	 * 
	 * @param data
	 * @return entropy
	 */
	private double calcEntropy(double[] data) {
		int size = data.length;
		double entropy = 0;
		double[] histo = new double[(int) typeGrayMax + 1];
		for (double d : data) {
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
	 * calculates statistic Skewness for data-vector.
	 * 
	 * @param data
	 * @return skewness
	 */
	private double calcSkewness(double[] data) {
		double skewness = 0;

		double mom3 = this.calcMom3(data);
		double stDev = this.calcStandardDeviation(data);
		skewness = mom3 / (stDev * stDev * stDev);

		return skewness;
	}

	/**
	 * calculates statistic Kurtosis for data-vector.
	 * 
	 * @param data
	 * @return kurtosis
	 */
	private double calcKurtosis(double[] data) {
		double kurtosis = 0;

		double mom4 = this.calcMom4(data);
		double mom2 = this.calcVariance(data);
		kurtosis = mom4 / (mom2 * mom2); // -3 would be "Exzess" and not
											// Kurtosis, see Wikipedia

		return kurtosis;
	}

	/**
	 * calculates Alikeness for data-vector. Alikeness is the number of pixels
	 * that have the same grey value as the center pixel a tolerance percentage
	 * can be set
	 * 
	 * @param data
	 * @param greyTolerance
	 * @return alikeness
	 */
	private double calcAlikeness(double[] data, double greyTolerance) {
		double alikeness = 0;
		int size = data.length;

		double greyCenter = data[(size - 1) / 2];
		// System.out.println("IqmOpStackStat: (size-1)/2:" + ((size-1)/2) +
		// "     greyCenter:" + greyCenter);
		double greyDiff = greyCenter / 100.0 * greyTolerance;
		double greyMax = greyCenter + greyDiff;
		double greyMin = greyCenter - greyDiff;

		for (double g : data) {
			if (g <= greyMax && g >= greyMin)
				alikeness = alikeness + 1;
		}
		return alikeness;
	}

	/**
	 * Calculates different parameters based on vectors.
	 * 
	 * @param wr
	 * @param nStackMax
	 * @param numBands
	 * @param method
	 * @param greyTolerance
	 */
	@SuppressWarnings("unused")
	private void scanImage(WritableRaster wr, int nStackMax, int numBands, int method, int greyTolerance) {
		int width = wr.getWidth(); // imgWidth of the final Image
		int height = wr.getHeight(); // imgHeight of the final Image
		double result = 0;

		int numIterations = numBands * width;
	

		// for each pixel calculation of requested parameter
		for (int b = 0; b < numBands; b++) { // bands
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {

					double[] data = new double[nStackMax]; // data array for each pixel location

					for (int s = 0; s < nStackMax; s++) {
						// get a writable raster for the calculation
						Raster r = Application.getTank().getCurrentTankIqmDataBoxAt(s).getImage().getData();
						data[s] = r.getSample(i, j, b);
					}

					switch (method) {
					case 0:
						result = calcRange(data);
						break;
					case 1:
						result = calcMean(data);
						break;
					case 2:
						result = calcStandardDeviation(data);
						break;
					case 3:
						result = calcEnergy(data);
						break;
					case 4:
						result = calcEntropy(data);
						break;
					case 5:
						result = calcSkewness(data);
						break;
					case 6:
						result = calcKurtosis(data);
						break;
					case 7:
						result = calcAlikeness(data, greyTolerance);
						break; // 10% alikeness
					}
					wr.setSample(i, j, b, result);
				}
				
				int proz = i * 95 / width/numBands + (b*95)/numBands + 0;
				fireProgressChanged(proz);
				if (isCancelled(getParentTask())) return;
			}
		}
	}

}
