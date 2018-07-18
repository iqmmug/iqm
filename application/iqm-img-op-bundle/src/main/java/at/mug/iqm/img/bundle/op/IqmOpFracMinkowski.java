package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracMinkowski.java
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


import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;
import java.util.Vector;

import javax.media.jai.BorderExtender;
import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.BoardPanel;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpFracMinkowskiDescriptor;

/**
 * <li> 2010 01 added progress bar support 
 * <li> 2010 04 added grey image support, "Blanket Dimension"
 * <li> Peleg S., Naor J., Hartley R., Avnir D. Multiple Resolution Texture Analysis and Classification, IEEE Trans Patt Anal Mach Intel Vol PAMI-6 No 4 1984, 518-523 !!!
 * <li> Y.Y. Tang, E.C.M. Lam, New method for feature extraction based on fractal behavior, Patt. Rec. 35 (2002) 1071-1081   Ref. zu Peleg etal.
 * <li> Dubuc B., Zucker S.W., Tricot C., Quiniou J.F., Wehbi D. Evaluating the fractal dimension of surfaces, Proc.R.Soc.Lond. A 425, 113-127, 1989 
 * <li> 2011 01 added 16 bit support
 * <li> 2011 02 added regression data to table 
 * <li> 2011 02 removing ModelListener extremely speeds up model configuration (adding columns, writing data,...) 
 * <li> 2011 09 15 improved regression for grey scale (Blanket) method according to Dubuc et al. equation
 * <li> 2011 01 04 bug fix for binary images. Binary images need the old regression 
 * <li> 2012 01 04 added horizontal and vertical kernel shape  
 * 
 * @author Ahammer, Kainz
 * @since   2009 07
 */
public class IqmOpFracMinkowski extends AbstractOperator {

	public IqmOpFracMinkowski() {
		this.setCancelable(true);
	}

	/**
	 * This method creates a kernel for dilation
	 * 
	 * @param int kernelShape, in radius
	 * @return KernelJAI
	 */
	private KernelJAI getKernelJAI(int kernelShape, int r) {
		KernelJAI kernelJAI = null;
		int kernelSize = 2 * r + 1;
		// int kernelSize = (int) Math.round(Math.sqrt(r*r*Math.PI));
		int kernelWidth = 0;
		int kernelHeight = 0;
		if (kernelShape == 0) { // Square
			kernelWidth = kernelSize;
			kernelHeight = kernelSize;
		}
		if (kernelShape == 1) { // Horizontal line
			kernelWidth = kernelSize;
			kernelHeight = 1;
		}
		if (kernelShape == 2) { // Vertical line
			kernelWidth = 1;
			kernelHeight = kernelSize;
		}
		// kernelJAI = KernelFactory.createRectangle(kernelWidth, kernelHeight);
		int size = kernelWidth * kernelHeight;
		float[] kernel = new float[size];
		Arrays.fill(kernel, 0.0f); // Damit Hintergrund nicht auf 1 gesetzt wird
		kernelJAI = new KernelJAI(kernelWidth, kernelHeight, kernel);

		return kernelJAI;
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

		int dilEpsMax = pb.getIntParameter("DilEps"); // Binary: Eps is a
														// radius Grey: Eps
														// is a dilation
														// distance
		int kernelShape = pb.getIntParameter("KernelShape"); // 0 Square, 1
																// Horizontal
																// line, 2
																// Vertical line
		int regStart = pb.getIntParameter("RegStart");
		int regEnd = pb.getIntParameter("RegEnd");
		boolean optShowPlot = false;
		boolean optDeleteExistingPlot = false;
		if (pb.getIntParameter("ShowPlot") == 1)
			optShowPlot = true;
		if (pb.getIntParameter("DeleteExistingPlot") == 1)
			optDeleteExistingPlot = true;

		boolean isBinary = true;
		int numBands = pi.getNumBands();
		int width = pi.getWidth();
		int height = pi.getHeight();
		String type = ImageTools.getImgType(pi);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		// initialize table for Dimension data
		TableModel model = new TableModel("Minkowski Dimension [" + imgName
				+ "]");
		// adding a lot of columns would be very slow due to active model
		model.addColumn("FileName");
		model.addColumn("ImageName");
		model.addColumn("Band");
		model.addColumn("RegStart");
		model.addColumn("RegEnd");
		model.addColumn("DilEps");
		for (int b = 0; b < numBands; b++) { // mehrere Bands
			model.addRow(new Object[] { fileName, imgName, b, regStart, regEnd,
					dilEpsMax });
		}
		double[][] totalAreas = new double[dilEpsMax][numBands];
		double[] eps = new double[dilEpsMax];

		ParameterBlock pbWork = new ParameterBlock(); // TODO duplicate Source
														// entry??
		pbWork.addSource(pi);
		pbWork.removeParameters();

		PlanarImage pi2 = pi;

		// Set up the parameters for the Histogram object.
		int[] bins = { (int) typeGreyMax + 1, (int) typeGreyMax + 1,
				(int) typeGreyMax + 1 }; // The number of bins e.g. {256, 256,
											// 256}
		double[] lows = { 0.0D, 0.0D, 0.0D }; // The low incl.value e.g. {0.0D,
												// 0.0D, 0.0D}
		double[] highs = { typeGreyMax + 1, typeGreyMax + 1, typeGreyMax + 1 }; // The
																				// high
																				// excl.value
																				// e.g.
																				// {256.0D,
																				// 256.0D,
																				// 256.0D}
		// Create the parameter block.
		ParameterBlock pbHisto = new ParameterBlock();
		pbHisto.addSource(pi2); // Source image
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
		// System.out.println("IqmOpFracPyramid: (int)typeGreyMax: " +
		// (int)typeGreyMax);

		for (int b = 0; b < numBands; b++) {
			// double totalGrey = (double)histo.getSubTotal(b, 1, 255);
			// //without 0!
			// double totalBinary = (double)histo.getSubTotal(b, 255, 255);
			double totalGrey = histogram.getSubTotal(b, 1, (int) typeGreyMax); // without
																				// 0!
			double totalBinary = histogram.getSubTotal(b, (int) typeGreyMax,
					(int) typeGreyMax);

			if (totalBinary == totalGrey) {
				isBinary = true;
			} else {
				isBinary = false;
			}
			if (isBinary) {
				totalAreas[0][b] = totalBinary; // Binary Image
				// eps[0] = Math.sqrt((1 * 1) / Math.PI); //kernelSize = 2*0 +1
				if (kernelShape == 0) {
					eps[0] = Math.sqrt((1 * 1) / Math.PI); // equivalent radius
															// of kernel area
				}
				if (kernelShape == 1) {
					eps[0] = 1.0d / 2.0d;
				}
				if (kernelShape == 2) {
					eps[0] = 1.0d / 2.0d;
				}
			} else {
				// totalAreas[0][b] = ??; //GreyImage
			}
			// if (totalAreas[0][b] == 0) totalAreas[0][b] = Float.MIN_VALUE;
			// //damit logarithmus nicht undefiniert ist
			// System.out.println("IqmOpFracMinkowski: totalAreas[n][b]  "+
			// totalAreas[n][b] );
		}
		// -------------------------------------------------------------------------------------------------------------------------------
		if (isBinary) {
			BoardPanel
					.appendTextln("IqmOpFracMinkowski: Binary image detected - Minkowski dilation");
			for (int r = 1; r < dilEpsMax; r++) { // dilRadius for Binary images
				int proz = (r + 1) * 100 / dilEpsMax;
				this.fireProgressChanged(proz);

				if (isCancelled(getParentTask())) {
					return null;
				}

				pi2 = pi;
				pbWork.removeParameters();

				KernelJAI kernelJAI = getKernelJAI(kernelShape, r);

				if (kernelShape == 0) {
					eps[r] = Math.sqrt((kernelJAI.getWidth() * kernelJAI
							.getHeight()) / Math.PI); // equivalent radius of
														// kernel area
				}
				if (kernelShape == 1) {
					eps[r] = kernelJAI.getWidth() / 2.0d;
				}
				if (kernelShape == 2) {
					eps[r] = kernelJAI.getHeight() / 2.0d;
				}

				pbWork.add(kernelJAI);
				RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
						BorderExtender
								.createInstance(BorderExtender.BORDER_COPY));
				// for (int t = 0; t < times; t++){ //erh�ht jedes mal den
				// Grauwert um 1!!!!
				pbWork.addSource(pi2);
				pi2 = JAI.create("dilate", pbWork, rh);
				pbWork.removeSources();
				// }
				pbHisto = new ParameterBlock();
				pbHisto.addSource(pi2); // Source image
				pbHisto.add(null); // ROI
				pbHisto.add(1); // Horizontal sampling
				pbHisto.add(1); // Vertical sampling
				pbHisto.add(bins); // Number of Bins
				pbHisto.add(lows); // Lowest inclusive values
				pbHisto.add(highs); // Highest exclusive values
				// Perform the histogram operation.
				rOp = JAI.create("Histogram", pbHisto, null);
				// Retrieve the histogram data.
				histogram = (Histogram) rOp.getProperty("histogram");
				for (int b = 0; b < numBands; b++) {
					totalAreas[r][b] = histogram.getSubTotal(b, 1,
							(int) typeGreyMax); // optional 2 - 255 weil dilate
												// aus Grauwert 0 einen Grauwert
												// 1 macht!?
					// System.out.println("IqmOpFracMinkowski: totalAreas[n][b]  "+
					// totalAreas[n][b] );
				}
				// if (n == 3){
				// piOut = pi2;
				// }
			}// r dilRadius
		}
		// -------------------------------------------------------------------------------------------------------------------------------
		if (!isBinary) { // Grey scale image "Blanket method"
			BoardPanel
					.appendTextln("IqmOpFracMinkowski: Grey scale image detected - Blanket method");

			int r = 1;
			KernelJAI kernelJAI = getKernelJAI(kernelShape, r);

			double[] plusOne = new double[3];
			double[] minusOne = new double[3];
			Arrays.fill(plusOne, 1.0d);
			Arrays.fill(minusOne, -1.0d); // bug fix 2012 01 27 Michal M.
			// Format to float because of negative values
			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
					BorderExtender.createInstance(BorderExtender.BORDER_COPY));
			pbWork = new ParameterBlock();
			pbWork.addSource(pi);
			if (type.equals(IQMConstants.IMAGE_TYPE_RGB))
				pbWork.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))
				pbWork.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))
				pbWork.add(DataBuffer.TYPE_USHORT);

			PlanarImage piU = JAI.create("Format", pbWork, rh);
			PlanarImage piB = piU;
			int[] pixel = new int[numBands];
			for (int ee = 0; ee < dilEpsMax; ee++) { // epsilon for grey scale
														// images
				int proz = (ee + 1) * 100 / dilEpsMax;
				this.fireProgressChanged(proz);

				if (isCancelled(getParentTask())) {
					return null;
				}

				eps[ee] = ee + 1; // epsilon [1,2,..., DilEpsMax]
				// Calculate image U
				// Calculate image U+1
				pbWork.removeSources();
				pbWork.removeParameters();
				pbWork.addSource(piU);
				pbWork.add(plusOne);
				PlanarImage piUplusOne = JAI.create("AddConst", pbWork, rh);
				// Calcualte dilated image
				pbWork.removeSources();
				pbWork.removeParameters();
				pbWork.addSource(piU);
				pbWork.add(kernelJAI);
				PlanarImage piUDil = JAI.create("dilate", pbWork, rh);
				// get Max image
				pbWork.removeSources();
				pbWork.removeParameters();
				pbWork.addSource(piUplusOne);
				pbWork.addSource(piUDil);
				piU = JAI.create("Max", pbWork, rh);

				// Calculate image B
				// Calculate image B-1
				pbWork.removeSources();
				pbWork.removeParameters();
				pbWork.addSource(piB);
				pbWork.add(minusOne);
				PlanarImage piBminusOne = JAI.create("AddConst", pbWork, rh);
				// Calcualte dilated image
				pbWork.removeSources();
				pbWork.removeParameters();
				pbWork.addSource(piB);
				pbWork.add(kernelJAI);
				PlanarImage piBErode = JAI.create("Erode", pbWork, rh);
				// get Min image
				pbWork.removeSources();
				pbWork.removeParameters();
				pbWork.addSource(piBminusOne);
				pbWork.addSource(piBErode);
				piB = JAI.create("Min", pbWork, rh);

				// Calculate Volume
				pbWork.removeSources();
				pbWork.removeParameters();
				pbWork.addSource(piU);
				pbWork.addSource(piB);
				PlanarImage piV = JAI.create("Subtract", pbWork, rh);

				// pb.removeSources();
				// rOp = JAI.create("Histogram", piV);
				// histo = (Histogram)rOp.getProperty("histogram");
				// totalAreas[ee][b] = (double) histo.getSubTotal(b, 1, 255);
				// //optional 2 - 255 weil dilate aus Grauwert 0 einen Grauwert
				// 1 macht!?
				// System.out.println("IqmOpFracMinkowski: totalAreas[n][b]  "+
				// totalAreas[n][b] );
				Raster raster = piV.getData();
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						raster.getPixel(x, y, pixel);
						for (int b = 0; b < numBands; b++)
							totalAreas[ee][b] = totalAreas[ee][b] + pixel[b];
					}
				}
				// for (int b = 0; b < numBands; b++) totalAreas[ee][b] =
				// totalAreas[ee][b] / (2* (ee+1)); //Peleg et al.
				// better is following according to Dubuc et al. equation 9
				for (int b = 0; b < numBands; b++)
					totalAreas[ee][b] = totalAreas[ee][b]
							/ ((ee + 1) * (ee + 1) * (ee + 1)); // eq.9 Dubuc
																// et.al.

				// if (n == 3){
				// piOut = pi2;
				// }
			}// ee epsilon
		}// grey image
			// -------------------------------------------------------------------------------------------------------------------------------

		// Log
		double[][] lnTotalAreas = new double[dilEpsMax][numBands];
		double[][] lnEps = new double[dilEpsMax][numBands];
		for (int n = 0; n < dilEpsMax; n++) {
			for (int b = 0; b < numBands; b++) {
				if (totalAreas[n][b] == 0) {
					lnTotalAreas[n][b] = Math.log(Float.MIN_VALUE); // damit
																	// logarithmus
																	// nicht
																	// undefiniert
																	// ist
				} else {
					lnTotalAreas[n][b] = Math.log(totalAreas[n][b]);
				}
				lnEps[n][b] = Math.log(eps[n]);
			}
		}
		// set table data header
		int numColumns = model.getColumnCount();
		if (isBinary)
			model.addColumn("Dm");
		if (!isBinary)
			model.addColumn("Dm_Blanket");
		model.addColumn("StdDev");
		model.addColumn("r2");
		model.addColumn("adjustet_r2");
		// set table regression data header
		for (int n = 0; n < dilEpsMax; n++)
			model.addColumn("DataX_" + (n + 1));
		for (int n = 0; n < dilEpsMax; n++)
			model.addColumn("DataY_" + (n + 1));

		boolean isLineVisible = false;
		for (int b = 0; b < pi.getNumBands(); b++) { // mehrere Bands
			// Plot //nur ein Band
			// double[] dataX = new double[dilRadiusMax];
			// double[] dataY = new double[dilRadiusMax];
			Vector<Double> dataX = new Vector<Double>();
			Vector<Double> dataY = new Vector<Double>();
			for (int n = 0; n < dilEpsMax; n++) {
				dataY.add(lnTotalAreas[n][b]);
				dataX.add(lnEps[n][b]);
			}
			if (optShowPlot) {
				if (isBinary) {
					PlotTools.displayRegressionPlotXY(dataX, dataY,
							isLineVisible, imgName + "_Band" + b,
							"Minkowski Dimension", "ln(Dilation Radius)",
							"ln(Count)", regStart, regEnd,
							optDeleteExistingPlot);
				}
				if (!isBinary) {
					PlotTools.displayRegressionPlotXY(dataX, dataY,
							isLineVisible, imgName + "_Band" + b,
							"Minkowski (Blanket) Dimension", "ln(epsilon)",
							"ln(Surface Area)", regStart, regEnd,
							optDeleteExistingPlot);
				}
			}
			// double[] p =
			// PlotTools.getLinearRegression(IqmTools.reverse(dataX),
			// IqmTools.reverse(dataY), regStart, regEnd);
			double[] p = PlotTools.getLinearRegression(dataX, dataY, regStart,
					regEnd);

			// set table data
			if (isBinary)
				model.setValueAt(2.0 - p[1], b, numColumns); // "Standard Dm"
																// Peleg et al.
			if (!isBinary)
				model.setValueAt(-p[1], b, numColumns); // better for grey
														// images "Dm" Dubuc et
														// al.
			model.setValueAt(p[3], b, numColumns + 1);
			model.setValueAt(p[4], b, numColumns + 2);
			model.setValueAt(p[5], b, numColumns + 3);

			// set regression data
			for (int n = 0; n < dilEpsMax; n++) {
				// model.setValueAt(lnBoxWidth[n][b], b, numColumns+n);
				model.setValueAt(eps[n], b, numColumns + 3 + 1 + n);
			}
			for (int n = 0; n < dilEpsMax; n++) {
				// model.setValueAt(lnTotalAreas[n][b], b,
				// numColumns+dilEpsMax+n);
				model.setValueAt(totalAreas[n][b], b, numColumns + dilEpsMax
						+ 3 + 1 + n);
			}
		}
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(model);
		// return piOut; //Optionally put out an image
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpFracMinkowskiDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpFracMinkowskiDescriptor.TYPE;
	}

}
