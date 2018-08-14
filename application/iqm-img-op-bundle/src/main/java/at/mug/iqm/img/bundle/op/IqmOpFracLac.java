package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracLac.java
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


import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;
import java.util.Vector;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.plot.PlotTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracLacDescriptor;

/**
 * <li> Allain C, Cloitre M, Characterizing the lacunarity of random and deterministic fractal sets. Phys. Rev. A, 44, 1991, 3552-3558
 * <li> Plotnik RE, Gardner RH, Hargrove WW, Prestegaard K, Perlmutter M, Lacunarity analysis: A general technique for the analysis of spatial patterns, Phys Rev E, 53, 1996, 5461-5468.
 * <li> Sengupta K, Vinoy KJ, A new measure of lacunarity for generalized fractals and its impact in the electromagnetic behavior of the Koch dipole antennas, Fractals, 14, 2006, 271-282.   
 *
 * @author Ahammer, Kainz
 * @since   2011 04   //isn't ready yet!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class IqmOpFracLac extends AbstractOperator {

	public IqmOpFracLac() {
		setCancelable(true);
	}

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

		int numEps = pb.getIntParameter("NumMaxEps");
		int method = pb.getIntParameter("Method"); // 0: Gliding Box,
													// 1:Alternative method
		int regStart = pb.getIntParameter("RegStart");
		int regEnd = pb.getIntParameter("RegEnd");
		boolean optShowPlot = false;
		boolean optDeleteExistingPlot = false;
		if (pb.getIntParameter("ShowPlot") == 1)
			optShowPlot = true;
		if (pb.getIntParameter("DeleteExistingPlot") == 1)
			optDeleteExistingPlot = true;
		int numBands = pi.getData().getNumBands();

		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		int width = pi.getWidth();
		int height = pi.getHeight();
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		// initialize table for Dimension data
		TableModel tableModel = new TableModel("Lacunarity [" + imgName + "]");
		tableModel.setProperty("file_name", fileName);
		// adding a lot of columns would be very slow due to active model
		tableModel.addColumn("FileName");
		tableModel.addColumn("ImageName");
		tableModel.addColumn("Band");
		tableModel.addColumn("RegStart");
		tableModel.addColumn("RegEnd");
		for (int b = 0; b < pi.getNumBands(); b++) { // mehrere Bands
			tableModel.addRow(new Object[] { fileName, imgName, b, regStart, regEnd });
		}

		// data array
		double[][] totals = new double[numEps][numBands];
		// double[] totalsMax = new double[numBands];
		double[][] eps = new double[numEps][numBands];
		double epsWidth = 1d;

		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY));

		ParameterBlock pbWork = new ParameterBlock();
		pbWork.addSource(pi);
		pbWork.add(DataBuffer.TYPE_DOUBLE);
		PlanarImage piDouble = JAI.create("Format", pbWork, rh);

		// create binary with 0 and 1
		pbWork = new ParameterBlock();
		pbWork.addSource(piDouble);
		double[] low = new double[numBands];
		double[] high = new double[numBands];
		for (int b = 0; b < numBands; b++) {
			low[b] = 0.0;
			high[b] = 1.0;
		}
		pbWork.add(low); // <=Threshold ? low
		pbWork.add(high); // >=Threshold ? high
		PlanarImage piBin = JAI.create("Clamp", pbWork, null);

		PlanarImage piConv = null;

		for (int ee = 0; ee < numEps; ee++) {// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			int proz = (int) (ee + 1) * 95 / numEps;
			this.fireProgressChanged(proz);
			if (isCancelled(getParentTask()))
				return null;

			// JAI Method using convolution
			int kernelSize = (int) (2 * epsWidth + 1);
			int size = kernelSize * kernelSize;
			float[] kernel = new float[size];
			// Arrays.fill(kernel, 0.0f); //Damit Hintergrund nicht auf 1
			// gesetzt wird bei dilate, ....
			Arrays.fill(kernel, 1.0f); // Sum of box
			KernelJAI kernelJAI = new KernelJAI(kernelSize, kernelSize, kernel);

			pbWork = new ParameterBlock();
			pbWork.addSource(piBin);
			pbWork.add(kernelJAI);
			piConv = JAI.create("convolve", pbWork, rh);

			// subtract midpoint itself from number
			pbWork = new ParameterBlock();
			pbWork.addSource(piConv);
			double[] value = new double[1];
			value[0] = 1.0;
			pbWork.add(value);
			piConv = JAI.create("SubtractConst", pbWork, rh);

			pbWork = new ParameterBlock(); // set pixels that were 0 again to
											// 0(because "convolve" changed some
											// zeros)
			pbWork.addSource(piConv);
			pbWork.addSource(piBin); // 0 or 1
			piConv = JAI.create("multiply", pbWork, rh);

			// each pixel value is now the sum of the box (neighborhood values)

			// get statistics
			double[] mean = new double[numBands];
			double[] var = new double[numBands];
			int[] N = new int[numBands];
			Raster ra = piConv.getData();
			// mean
			double sample[] = new double[numBands];
			for (int x = 0; x < width; x++) { // scroll through image
				for (int y = 0; y < height; y++) {
					ra.getPixel(x, y, sample);
					for (int b = 0; b < numBands; b++) { // several bands
						if (sample[b] > 0.0) { // no zero values!!!
							N[b] = N[b] + 1;
							mean[b] = mean[b] + sample[b];
						}// sample > 0
					}// b
				}// y
			} // x
			for (int b = 0; b < numBands; b++) { // several bands
				mean[b] = mean[b] / N[b];
			}
			// variance
			sample = new double[numBands];
			for (int x = 0; x < width; x++) { // scroll through image
				for (int y = 0; y < height; y++) {
					ra.getPixel(x, y, sample);
					for (int b = 0; b < numBands; b++) { // several bands
						if (sample[b] > 0.0) { // no zero values!!!
							// N[b]=N[b]+1; //calculated already
							var[b] = (var[b] + (sample[b] - mean[b])
									* (sample[b] - mean[b]));
						}// sample > 0
					}// b
				}// y
			} // x
			for (int b = 0; b < numBands; b++) { // several bands
				var[b] = var[b] / (N[b] - 1);
			}
			// calculate and set lacunarity value
			for (int b = 0; b < numBands; b++) {
				totals[ee][b] = var[b] / (mean[b] * mean[b]) + 1; // lacunarity
				eps[ee][b] = kernelSize;// = epsWidth; = kernelSize;
				// System.out.println("IqmOpFracLac: b: " + b+ "     mean: " +
				// mean[b] + "      stddev: "+stddev[b] );
				// System.out.println("IqmOpFracLac: b: " + b+ "     epsWidth: "
				// + epsWidth + "      lac: "+lac );
			}
			epsWidth = epsWidth * 2;
			// epsWidth = epsWidth+1;
		} // 0>=ee<=numEps loop through eps

		// Log
		double[][] lnTotals = new double[numEps][numBands];
		double[][] lnEps = new double[numEps][numBands];
		for (int n = 0; n < numEps; n++) {
			for (int b = 0; b < numBands; b++) {
				if (totals[n][b] == 0) {
					lnTotals[n][b] = Math.log(Float.MIN_VALUE); // damit
																// logarithmus
																// nicht
																// undefiniert
																// ist
				} else {
					lnTotals[n][b] = Math.log(totals[n][b]);
				}
				lnEps[n][b] = Math.log(eps[n][b]);
				// System.out.println("IqmFracBoxOperator: " );
			}
		}

		Result r = new Result();

		// set table header
		int numColumns = tableModel.getColumnCount();
		tableModel.addColumn("Lac");
		// model.addColumn("StdDev");
		// model.addColumn("r2");
		// model.addColumn("adjustet_r2");
		// sete regression data header
		for (int n = 0; n < numEps; n++)
			tableModel.addColumn("DataX_" + (n + 1));
		for (int n = 0; n < numEps; n++)
			tableModel.addColumn("DataY_" + (n + 1));

		boolean isLineVisible = false;
		for (int b = 0; b < numBands; b++) { // several bands
			// Plot //only one Band!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			final Vector<Double> dataX = new Vector<Double>();
			final Vector<Double> dataY = new Vector<Double>();
			for (int n = 0; n < numEps; n++) {
				dataY.add(lnTotals[n][b]);
				dataX.add(lnEps[n][b]);
			}
			// //if dataX and datY are unsorted!!
			// //sorting essential for limited RegStart RegEnd settings
			// //get the sorted index
			// Integer[] idx = new Integer[dataX.size()];
			// for (int i = 0; i < idx.length; i++ ) idx[i] = i;
			//
			// //for (int i = 0; i < idx.length; i++ )
			// System.out.println("idx: " + idx[i]);
			// Arrays.sort(idx, new Comparator<Integer>() {
			// @Override public int compare(Integer idx1, Integer idx2) {
			// return Double.compare(dataX.get(idx1), dataX.get(idx2));
			// }
			// });
			// //for (int i = 0; i < idx.length; i++ )
			// System.out.println("idx: " + idx[i]);
			//
			// //get sorted vectors an
			// Vector<Double> dataXSorted = new Vector<Double>();
			// Vector<Double> dataYSorted = new Vector<Double>();
			// for (int i = 0; i < idx.length; i++ ){
			// if ((i+1) <= numEps){ //restrict to maximal eps
			// dataXSorted.add(i, dataX.get(idx[i])); //idx is sorted
			// dataYSorted.add(i, dataY.get(idx[i]));
			// }
			// }

			// System.out.println("IqmOpFracLac: right before regressionplotXY");
			if (optShowPlot) {
				PlotTools.displayRegressionPlotXY(dataX, dataY, isLineVisible,
						imgName + "_Band" + b, "Lacunarity", "ln(boxWidth)",
						"ln(L)", regStart, regEnd, optDeleteExistingPlot);
			}

			if (wp.isPlotComputationEnabled()) {
				r.addItem(new PlotModel("Lacunarity: " + imgName + ", Band "
						+ b, "ln(boxWidth)", null, "ln(L)", null, dataX, dataY));
			}
			// double[] p =
			// IqmTools.getLinearRegression(IqmTools.reverse(dataX),
			// IqmTools.reverse(dataY), regStart, regEnd);

			// System.out.println("IqmOpFracLac: right before linear regression");
			double[] p = PlotTools.getLinearRegression(dataX, dataY, regStart,
					regEnd);

			double slope = p[1];
			double lac = 0.0;
			// slope not used

			// Single value for Lacunarity according to:
			// Sengupta K, Vinoy KJ, A new measure of lacunarity for generalized
			// fractals and its impact in the electromagnetic behavior of the
			// Koch dipole antennas, Fractals, 14, 2006, 271-282.
			for (int n = 0; n < (numEps - 1); n++) {
				lac = lac
						+ 0.5
						* ((totals[n + 1][b] + totals[n][b]) * (eps[n + 1][b] - eps[n][b])); // "Sehnentrapezformel"
			}
			lac = lac / (eps[regEnd - 1][b] - eps[regStart - 1][b]);

			// set table data
			tableModel.setValueAt(lac, b, numColumns);
			// model.setValueAt(p[3], b, numColumns+1);
			// model.setValueAt(p[4], b, numColumns+2);
			// model.setValueAt(p[5], b, numColumns+3);

			// System.out.println("IqmOpFracLac: right before setting regression data");
			// set regression data
			for (int n = 0; n < numEps; n++) {
				System.out.println("IqmOpFracLac: n:" + n);
				tableModel.setValueAt(eps[n][b], b, numColumns + 1 + n);
			}
			for (int n = 0; n < numEps; n++) {
				tableModel.setValueAt(totals[n][b], b, numColumns + numEps + 1 + n);
			}
		}// bands

		if (wp.isTableComputationEnabled()) {
			r.addItem(tableModel);
		}
		if (wp.isImageComputationEnabled()) {
			ImageModel im = new ImageModel(piConv);
			im.setModelName(imgName);
			r.addItem(im);
		}
		if (isCancelled(getParentTask())) {
			return null;
		}
		return r;
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpFracLacDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpFracLacDescriptor.TYPE;
	}
}
