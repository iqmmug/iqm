package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracGenDim.java
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
import javax.media.jai.Histogram;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.commons.util.plot.PlotTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracGenDimDescriptor;

/**
 * <li>Generalized Correlations method, Multifractal Analysis, Dq
 * <li>Vicsek Fractal Growth Phenomena World Scientific
 * <li>Pawelzik K. und Schuster H.G. Phys. Rev. A, 35, 481, 1987
 * <li>Distance ist Radius um einen Punkt der Trajektorie
 * <li>Für jeden Distance wird von allen Pkten des Attraktors die Anzahl der Pkte die sich innerhalb des Kreises befinden aufsummmiert
 * <li>Regression dieser Anzahl über Distance ergibt Dq's
 * <li>Result ist ein eindimensionales Array mit den Dq's
 * <li>Distance entspricht ungef. Radius  fängt immer bei 1 an
 * <li>siehe auch Ahammer et al. PhysicaD 181 147-156 2003
 * <li>f(alpha) laut Vicsek Fractal Growth Phenomena World Scientific p55    
 * 
 * @author Ahammer, Kainz
 * @since   2012 03 
 */
public class IqmOpFracGenDim extends AbstractOperator {

	public IqmOpFracGenDim() {
		this.setCancelable(true);
	}

	/**
	 * This method calculates the number of pixels >0 param PlanarImage pi
	 * return double[]
	 */
	private double[] getNumberOfNonZeroPixels(PlanarImage pi) {
		int numBands = pi.getNumBands();
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		System.out.println("IqmOpFracGenDim: typeGreyMax: " + typeGreyMax);

		double[] totalGrey = new double[numBands];
		double[] totalBinary = new double[numBands];

		// Get total amount of object pixels
		// Set up the parameters for the Histogram object.
		int[] bins = { (int) typeGreyMax + 1, (int) typeGreyMax + 1,
				(int) typeGreyMax + 1 }; // The number of bins e.g. {256, 256, 256}
		double[] lows = { 0.0D, 0.0D, 0.0D }; // The low incl.value e.g. {0.0D, 0.0D, 0.0D}
		double[] highs = { typeGreyMax + 1, typeGreyMax + 1, typeGreyMax + 1 }; // The high excl.value e.g. {256.0D, 256.0D, 256.0D}
		// Create the parameter block.
		ParameterBlock pbHisto = new ParameterBlock();
		pbHisto.addSource(pi); // Source image
		pbHisto.add(null); // ROI
		pbHisto.add(1); // Horizontal sampling
		pbHisto.add(1); // Vertical sampling
		pbHisto.add(bins); // Number of Bins
		pbHisto.add(lows); // Lowest inclusive values
		pbHisto.add(highs); // Highest exclusive values
		// Perform the histogram operation.
		RenderedOp rOp = JAI.create("Histogram", pbHisto, null);
		// Retrieve the histogram data.
		Histogram histogram = (Histogram) rOp.getProperty("histogram");
		// System.out.println("IqmOpFracGendim: (int)typeGreyMax: " +
		// (int)typeGreyMax);
		for (int b = 0; b < numBands; b++) {
			totalGrey[b] = histogram.getSubTotal(b, 1, (int) typeGreyMax); // without 0!
			totalBinary[b] = histogram.getSubTotal(b, (int) typeGreyMax, (int) typeGreyMax);
			// System.out.println("IqmOpFracGendim: totalGrey[b]: "+
			// totalGrey[b] + "totalBinary[b]: " + totalBinary[b]);
		}
		return totalGrey;
	}

	@SuppressWarnings({ "unchecked", "unused" })
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

		int minQ = pb.getIntParameter("MinQ");
		int maxQ = pb.getIntParameter("MaxQ");
		int numEps = pb.getIntParameter("NumEps"); // number of eps's to
													// calculate
		int maxEps = pb.getIntParameter("MaxEps"); // maximal eps in pixels
		int methodEps = pb.getIntParameter("MethodEps"); // 0 linear
															// distributed 1 log
															// distributed
		int gridMethod = pb.getIntParameter("Method"); // 0: Gliding Box,
													// 1:Alternative method
		int regStart = pb.getIntParameter("RegStart");
		int regEnd = pb.getIntParameter("RegEnd");

		int numQ = maxQ - minQ + 1;
		boolean optShowPlot = false;
		boolean optDeleteExistingPlot = false;
		boolean optShowPlotDq = false;
		boolean optShowPlotF = false;

		if (pb.getIntParameter("ShowPlot") == 1)           optShowPlot = true;
		if (pb.getIntParameter("DeleteExistingPlot") == 1) optDeleteExistingPlot = true;
		if (pb.getIntParameter("ShowPlotDq") == 1)         optShowPlotDq = true;
		if (pb.getIntParameter("ShowPlotF") == 1)          optShowPlotF = true;

		int numBands = pi.getData().getNumBands();
		String type = ImageTools.getImgType(pi);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		int width = pi.getWidth();
		int height = pi.getHeight();
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));
		
		String strGridMethod = "?";
		if (gridMethod == 0) strGridMethod = "Gliding box";
		if (gridMethod == 1) strGridMethod = "Raster box";

		// initialize table for Dimension data
		TableModel model = new TableModel("Generalized Dimensions [" + imgName
				+ "]");
		// JTable jTable = new JTable(model);
		// adding a lot of columns would be very slow due to active model
		// listener
		// model.removeTableModelListener(jTable);
		model.addColumn("FileName");
		model.addColumn("ImageName");
		model.addColumn("Grid method");
		model.addColumn("Band");
		model.addColumn("RegStart");
		model.addColumn("RegEnd");
		for (int b = 0; b < pi.getNumBands(); b++) { // mehrere Bands
			model.addRow(new Object[] { fileName, imgName, strGridMethod, b, regStart, regEnd });
		}

		// data arrays
		double[][][] totals = new double[numQ][numEps][numBands];
		double[][] totalsMax = new double[numEps][numBands];
		int[] eps = new int[numEps];

		// --------------------------------------------------------------------------------------------------------------
		if (gridMethod == 0) { // 0 gliding mass box counting

			// set eps values
			if (methodEps == 0) { // linear distributed
				for (int i = 0; i < numEps; i++) {
					eps[i] = Math.round((i + 1) / (float) numEps * maxEps);
					System.out.println("IqmOpFracGenDim: i, eps[i]: " + i + "   " + eps[i]);
				}
			}
			if (methodEps == 1) { // log distributed
				// eps[0] = 1;
				for (int i = 0; i < numEps; i++) {
					eps[i] = (int) Math.round(Math.exp((i + 1)
							* Math.log(maxEps) / numEps));
					if (i == 0) {
						if (eps[0] == 0)
							eps[0] = 1;
					} else {
						if (eps[i] <= eps[i - 1])
							eps[i] = eps[i - 1] + 1;
					}
					System.out.println("IqmOpFracGenDim: i, eps[i]: " + i + "   " + eps[i]);
				}
			}
			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
					BorderExtender.createInstance(BorderExtender.BORDER_COPY));

			ParameterBlock pbTmp = new ParameterBlock();
			pbTmp.addSource(pi);
			pbTmp.add(DataBuffer.TYPE_DOUBLE);
			PlanarImage piDouble = JAI.create("Format", pbTmp, rh);

			// create binary with 0 and 1
			pbTmp = new ParameterBlock();
			pbTmp.addSource(piDouble);
			double[] low = new double[numBands];
			double[] high = new double[numBands];
			for (int b = 0; b < numBands; b++) {
				low[b] = 0.0;
				high[b] = 1.0;
			}
			pbTmp.add(low); // <=Threshold ? low
			pbTmp.add(high); // >=Threshold ? high
			PlanarImage piBin = JAI.create("Clamp", pbTmp, null);

			// Get number of image pixels
			// Raster ra = piBin.getData();
			// int[] numPixel = new int[numBands];
			// double sample[] = new double[numBands];
			// for (int x = 0; x < imgWidth; x++){ //scroll through image
			// for (int y = 0; y < imgHeight; y++){
			// ra.getPixel(x, y, sample);
			// for(int b = 0; b < numBands; b++){ //several bands
			// if (sample[b] > 0.0) { //pixel found
			// numPixel[b] = numPixel[b]+1;
			// }
			// }
			// }
			// }
			//
			PlanarImage piConv = null;
			for (int ee = 0; ee < numEps; ee++) {

				int proz = (ee + 1) * 100 / numEps;
				this.fireProgressChanged(proz);

				if (isCancelled(getParentTask())) {
					return null;
				}

				// JAI Method using convolution
				// int kernelSize = (int)(2*epsWidth+1);
				int kernelSize = (2 * eps[ee] + 1);
				int size = kernelSize * kernelSize;
				float[] kernel = new float[size];
				// Arrays.fill(kernel, 0.0f); //Damit Hintergrund nicht auf 1
				// gesetzt wird bei dilate, ....
				Arrays.fill(kernel, 1.0f); // Sum of box
				KernelJAI kernelJAI = new KernelJAI(kernelSize, kernelSize, kernel);

				pbTmp = new ParameterBlock();
				pbTmp.addSource(piBin);
				pbTmp.add(kernelJAI);
				piConv = JAI.create("convolve", pbTmp, rh);

				pbTmp = new ParameterBlock(); // set pixels that were 0 again to 0(because of "convolve" changed some zeros)
				pbTmp.addSource(piConv);
				pbTmp.addSource(piBin); // 0 or 1
				piConv = JAI.create("multiply", pbTmp, rh);
				// each pixel value is now the sum of the box (neighborhood values)

				// double[] numObjectPixels; //number of pixel >0
				// numObjectPixels = this.getNumberOfNonZeroPixels(piConv);

				// get statistics
				Raster ra = piConv.getData();
				// n's for GenDim
				double[] sample = new double[numBands];
				for (int x = 0; x < width; x++) { // scroll through image
					for (int y = 0; y < height; y++) {
						ra.getPixel(x, y, sample);
						for (int b = 0; b < numBands; b++) { // several bands
							if (sample[b] > 0.0) { // no zero values!!!
								double count = sample[b];// -1 ; //-1: subtract point itself
								totalsMax[ee][b] = totalsMax[ee][b] + count; // calculate total count for normalization
								// count = count/totalMass[b]; //normalized mass
								// of current box
								// if (count > 0) {
								for (int q = 0; q < numQ; q++) {
									if ((q + minQ) != 1)
										totals[q][ee][b] = totals[q][ee][b] + Math.pow(count, (q + minQ)); // GenDim
									if ((q + minQ) == 1)
										totals[q][ee][b] = totals[q][ee][b] + count * Math.log(count); // GenDim
								}
								// }
							}// sample > 0
						}// b
					}// y
				} // x

			} // 0>=ee<=numEps loop through eps

			// normalization
			for (int ee = 0; ee < numEps; ee++) {
				for (int b = 0; b < numBands; b++) { // several bands
					for (int q = 0; q < numQ; q++) {
						totals[q][ee][b] = totals[q][ee][b] / totalsMax[ee][b];
					}
				}
			}
		} // Method = 0 gliding box
			// --------------------------------------------------------------------------------------------------------------
		if (gridMethod == 1) { // raster mass box counting

			double[] numObjectPixels; // number of pixel >0
			numObjectPixels = this.getNumberOfNonZeroPixels(pi);

			// set eps values
			for (int i = 0; i < numEps; i++) {
				eps[i] = (int) Math.pow(2, i);
				System.out.println("IqmOpFracGenDim: i, eps[i]: " + i + "   " + eps[i]);
			}

			ParameterBlock pbTmp = new ParameterBlock();
			pbTmp.addSource(pi);
			pbTmp.removeParameters();
			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
					BorderExtender.createInstance(BorderExtender.BORDER_COPY));

			for (int ee = 0; ee < numEps; ee++) {
				int proz = (int) (ee + 1) * 100 / numEps;

				this.fireProgressChanged(proz);

				if (isCancelled(getParentTask())) {
					return null;
				}

				pbTmp.removeParameters();

				if (type.equals(IQMConstants.IMAGE_TYPE_RGB))   pbTmp.add(DataBuffer.TYPE_BYTE);
				if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT)) pbTmp.add(DataBuffer.TYPE_BYTE);
				if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))pbTmp.add(DataBuffer.TYPE_USHORT);
				
				width = eps[ee];
				ImageLayout layout = new ImageLayout();
				layout.setTileWidth((int) width);
				layout.setTileHeight((int) width);
				rh = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
				pi = JAI.create("Format", pbTmp, rh);

				Raster[] tileRaster = pi.getTiles();
				for (int r = 0; r < tileRaster.length; r++) { // tileRaster[]

					int minX = tileRaster[r].getMinX();
					int minY = tileRaster[r].getMinY();
					int tileWidth = tileRaster[r].getWidth(); // ==imgWidth
					int tileHeight = tileRaster[r].getHeight(); // ==imgWidth
					// System.out.println("IqmOpFracGendim: minX: "+minX+ "  minY: " +minY+ "   tileWidth: "+ tileWidth+ "     tileHeight: "+ tileHeight);
					for (int b = 0; b < numBands; b++) {
						double count = 0.0d;
						for (int x = 0; x < tileWidth; x++) {
							for (int y = 0; y < tileHeight; y++) {
								// System.out.println("IqmOpFracBox: b, x, y, " +b+"  " + x+ "  "+y);
								int pixel = tileRaster[r].getSample(minX + x,
										minY + y, b);
								if (pixel > 0) {
									count = count + 1.0d;
								}
							}
						}
						// count = count/totalBinary[b];
						count = count / numObjectPixels[b]; // normalized mass of current box tileRaster[r]
						// System.out.println("IqmOpFracGendim: b: "+b+ "   count: "+ count );
						if (count > 0) {
							for (int q = 0; q < numQ; q++) {
								if ((q + minQ) != 1)
									totals[q][ee][b] = totals[q][ee][b]
											+ Math.pow(count, (q + minQ)); // GenDim
								if ((q + minQ) == 1)
									totals[q][ee][b] = totals[q][ee][b] + count
											* Math.log(count); // GenDim

							}
						}
					} // b bands
				} // r tileRaster[]
					// for (int q =0; q < numQ; q++){
					// System.out.println("IqmOpFracGendim: ee: " + ee + "  q: "+ (q+minQ) + "    totals[q][ee][0]: "+ totals[q][ee][0] );
					// }

			} // 0>=ee<=numEps loop through eps
		} // method 1 raster box counting
			// --------------------------------------------------------------------------------------------------------------

		// Log
		double[][][] lnTotals = new double[numQ][numEps][numBands];
		double[] lnEps = new double[numEps];
		for (int ee = 0; ee < numEps; ee++) {
			lnEps[ee] = Math.log(eps[ee]);
			for (int b = 0; b < numBands; b++) {
				for (int q = 0; q < numQ; q++) {
					System.out.println("IqmOpFracGendim: ee: " + ee + "  q: "
							+ (q + minQ) + "    totals[q][ee][0]: "
							+ totals[q][ee][0]);
					if (totals[q][ee][b] == 0)
						totals[q][ee][b] = Double.MIN_VALUE; // damit logarithmus nicht undefiniert ist
					if ((q + minQ) != 1)
						lnTotals[q][ee][b] = Math.log(totals[q][ee][b]);
					if ((q + minQ) == 1)
						lnTotals[q][ee][b] = totals[q][ee][b];
					// System.out.println("IqmFracGenDimOperator: " );
				}
			}
		}

		// set table header
		int numColumns = model.getColumnCount();
		// model.addColumn("StdDev");
		// model.addColumn("r2");
		// model.addColumn("adjustet_r2");
		// sete regression data header
		for (int q = 0; q < numQ; q++)
			model.addColumn("Dq=" + (minQ + q));
		for (int n = 0; n < numEps; n++)
			model.addColumn("DataX_" + (n + 1));
		for (int n = 0; n < numEps; n++)
			model.addColumn("DataY_" + (n + 1));

		boolean isLineVisible = false;
		for (int b = 0; b < numBands; b++) { // several bands
			// Plot //only one Band!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			final Vector<Double> dataX = new Vector<Double>();
			final Vector<Double>[] dataY = new Vector[numQ];
			for (int v = 0; v < dataY.length; v++)
				dataY[v] = new Vector<Double>(); // Initialize, otherwise NullpointerError
			// System.out.println("IqmFracGenDimOperator: dataY.length: "+ dataY.length );

			for (int n = 0; n < numEps; n++) {
				dataX.add(lnEps[n]);
				for (int q = 0; q < numQ; q++) {
					(dataY[q]).add(lnTotals[q][n][b]);
				}
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

			// System.out.println("IqmOpFracGenDim: right before regressionplotXY");
			if (optShowPlot) {
				PlotTools.displayRegressionPlotXY(dataX, dataY, isLineVisible,
						imgName + "_Band" + b, "Generalized Dimensions",
						"ln(epsilon)", "ln(N)", regStart, regEnd,
						optDeleteExistingPlot);
			}
			// double[] p =
			// IqmTools.getLinearRegression(IqmTools.reverse(dataX),
			// IqmTools.reverse(dataY), regStart, regEnd);

			// System.out.println("IqmOpFracGenDim: right before linear regression");
			double slope[] = new double[numQ];
			for (int q = 0; q < numQ; q++) {
				double[] p = PlotTools.getLinearRegression(dataX, dataY[q],
						regStart, regEnd);
				slope[q] = p[1];
			}

			// set table data
			// model.setValueAt(D, b, numColumns);
			// model.setValueAt(p[3], b, numColumns+1);
			// model.setValueAt(p[4], b, numColumns+2);
			// model.setValueAt(p[5], b, numColumns+3);
			Vector<Double> genDimDataQ = new Vector<Double>();
			Vector<Double> genDimDataDq = new Vector<Double>();
			for (int q = 0; q < numQ; q++) {
				genDimDataQ.add((double) (q + minQ));
				if ((q + minQ) == 1) { // q=1
					model.setValueAt(slope[q], b, numColumns + q); // set table data
					genDimDataDq.add(slope[q]); // set plot data
				} else { // all other q's
					model.setValueAt(slope[q] / (q + minQ - 1), b, numColumns+ q); // set table data
					genDimDataDq.add(slope[q] / (q + minQ - 1)); // set plot data
				}
			}

			if (optShowPlotDq) { // Display Dq q plot
				PlotTools.displayPlotXY(genDimDataQ, genDimDataDq,
						isLineVisible, "Generalized dimensions", "q", "Dq");
			}
			if (optShowPlotF) { // Display f(alpha) alpha plot
				// see Vicsek Fractal Growth Phenomena p55
				Vector<Double> alpha = new Vector<Double>();
				Vector<Double> f = new Vector<Double>();
				// alpha == first derivative of Dq first point
				alpha.add((genDimDataDq.get(0) + genDimDataDq.get(1)) / 2.0);
				// several points
				for (int i = 1; i < (genDimDataDq.size() - 1); i++)
					alpha.add((genDimDataDq.get(i - 1) + genDimDataDq
							.get(i + 1)) / 2.0);
				// last point
				alpha.add((genDimDataDq.get(genDimDataDq.size() - 2) + genDimDataDq.get(genDimDataDq.size() - 1)) / 2.0);

				// calcualte f
				for (int i = 0; i < genDimDataDq.size(); i++) {
					f.add(genDimDataQ.get(i) * alpha.get(i) - ((genDimDataQ.get(i) - 1) * genDimDataDq.get(i)));
				}

				PlotTools.displayPlotXY(alpha, f, isLineVisible, "f spectrum", "alpha", "f");
			}

			// System.out.println("IqmOpFracGenDim: right before setting regression data");
			// set regression data
			for (int n = 0; n < numEps; n++) {
				// System.out.println("IqmOpFracGenDim: n:" + n);
				model.setValueAt(eps[n], b, numColumns + numQ + n);
			}
			// for (int n = 0; n < numEps; n++) {
			// // model.setValueAt(totals[?][n][b], b, numColumns+ numEps +
			// // numQ +n); //regression data for which q ?
			// }
		}// bands
			// model.addTableModelListener(jTable);
		// model.fireTableStructureChanged(); // this is mandatory because it
		// updates the table

		// jTable.getColumnModel().getColumn(2).setPreferredWidth(30); // Band
		// jTable.getColumnModel().getColumn(3).setPreferredWidth(30); //
		// RegStart
		// jTable.getColumnModel().getColumn(4).setPreferredWidth(30); // RegEnd

		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(model);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpFracGenDimDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpFracGenDimDescriptor.TYPE;
	}
}
