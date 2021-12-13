package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracBox.java
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


import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;

import javax.media.jai.Histogram;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpFracBoxDescriptor;

/**
 * <li> 2010 03 added grey value images support DBC (Differential Box Counting), see Sarker Chauduri, IEEE Trans Syst Man Cyb 1994
 * RDBC Relative DBC, see Jin Ong Jayasooriah, Patt Rec Lett 16 1995   
 * @author Ahammer, Kainz
 * @since  2009-11
 * @update 2016-11 changed progressbar counter to number of boxes to speed up prozessing durations significantly.
 */
public class IqmOpFracBox extends AbstractOperator {

	public IqmOpFracBox() {
		this.setCancelable(true);
	}

	@Override
	public IResult run(IWorkPackage wp) {

		// PlanarImage piOut = null;
		
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		int number   = pb.getIntParameter("NumBoxes");
		int greyMode = pb.getIntParameter("GreyMode");
		int regStart = pb.getIntParameter("RegStart");
		int regEnd   = pb.getIntParameter("RegEnd");
		boolean optShowPlot = false;
		boolean optDeleteExistingPlot = false;
		if (pb.getIntParameter("ShowPlot") == 1)
			optShowPlot = true;
		if (pb.getIntParameter("DeleteExistingPlot") == 1)
			optDeleteExistingPlot = true;

		int numBands = pi.getData().getNumBands();
		String type = ImageTools.getImgType(pi);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		boolean isBinary = true;
		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		String imgName  = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		// initialize table for Dimension data
		TableModel model = new TableModel("Box Dimension [" + imgName + "]");
		// adding a lot of columns would be very slow due to active model
		// operatorProgressListener
		model.addColumn("FileName");
		model.addColumn("ImageName");
		model.addColumn("Band");
		model.addColumn("RegStart");
		model.addColumn("RegEnd");
		for (int b = 0; b < pi.getNumBands(); b++) { // mehrere Bands
			model.addRow(new Object[] { fileName, imgName, b, regStart, regEnd });
		}
		double[][] totals = new double[number][numBands];
		// double[] totalsMax = new double[numBands];
		double[][] boxWidth = new double[number][numBands];
		double width = 1d;

		// Set up the parameters for the Histogram object.
		int[] bins = { (int) typeGreyMax + 1, (int) typeGreyMax + 1,
				(int) typeGreyMax + 1 }; // The number of bins e.g. {256, 256, 256}
		double[] lows = { 0.0D, 0.0D, 0.0D }; // The low incl.value e.g. {0.0D,
												// 0.0D, 0.0D}
		double[] highs = { typeGreyMax + 1, typeGreyMax + 1, typeGreyMax + 1 }; // The high excl.value/ e.g. {256.0D, 256.0D,/ 256.0D}
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
		// System.out.println("IqmOpFracPyramid: (int)typeGreyMax: " +
		// (int)typeGreyMax);

		for (int b = 0; b < numBands; b++) {
			double totalGrey = histogram.getSubTotal(b, 1, (int) typeGreyMax); // without 0!
			double totalBinary = histogram.getSubTotal(b, (int) typeGreyMax,
					(int) typeGreyMax);
			if (totalBinary == totalGrey) {
				isBinary = true;
			} else {
				isBinary = false;
			}
			// System.out.println("IqmOpFracBox: isBinary: " + isBinary);
			if (isBinary) {
				totals[0][b] = totalBinary; // Binary Image
			} else {
				// First value
				// does not fit the regression line, too small!
				if (greyMode == 0) { // DBC
					totals[0][b] = 1.0 * pi.getWidth() * pi.getHeight(); // Grey Image (l-k+1) is always 1 because l=k
				}
				if (greyMode == 1) { // RDBC
					// totals[0][b] = 0; //Grey Image because u-b is always zero
					totals[0][b] = 1.0 * pi.getWidth() * pi.getHeight();
				}
			}

			// System.out.println("IqmOpFracBox: totals[0][b] "+ totals[0][b]);
			// totalsMax[b] = totals[0][b];
			// totals[0][b] = totals[0][b]/totalsMax[b];
			boxWidth[0][b] = 1d;
		}

		ParameterBlock pbFmt = new ParameterBlock();
		pbFmt.addSource(pi);
		pbFmt.removeParameters();

		int proz = (int) (1f * 100.f / (float)number);
		this.fireProgressChanged(proz); 

		
		for (int n = 1; n < number; n++) {
			
			width = width * 2;
			pbFmt.removeParameters();
			if (type.equals(IQMConstants.IMAGE_TYPE_RGB))
				pbFmt.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))
				pbFmt.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))
				pbFmt.add(DataBuffer.TYPE_USHORT);
			
			ImageLayout layout = new ImageLayout();
			layout.setTileWidth((int) width);
			layout.setTileHeight((int) width);
			RenderingHints rh = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);

			pi = JAI.create("Format", pbFmt, rh);

			Raster[] tileRaster = pi.getTiles();
			// System.out.println("IqmOpFracBox: tileRaster.length: " +
			// tileRaster.length);

			for (int b = 0; b < numBands; b++)
				totals[n][b] = 0d;

			for (int r = 0; r < tileRaster.length; r++) { // tileRaster[]
				
				//int proz = (int) (r + 1) * 100 / tileRaster.length;
				//this.fireProgressChanged(proz); //too much events at this place
				
				if (isCancelled(getParentTask())) {
					return null;
				}
				// slow
				// version--------------------------------------------------------------------
				// only binary images!!!
				// and additionally problem with arbitrary image size (512 x 512
				// is ok, 800 x 800 is not ok)
				// WritableRaster wr = (WritableRaster) tileRaster[r];
				// WritableRaster newRaster =
				// wr.createWritableTranslatedChild(0,0);
				// BufferedImage bi = new BufferedImage(pi.getColorModel(),
				// newRaster, false, null);
				// rOp = JAI.create("Histogram", bi);
				// histo = (Histogram)rOp.getProperty("histogram");
				// for (int b = 0; b < numBands; b++) {
				// double total = (double) histo.getSubTotal(b, 1, 255);
				// if (total > 0) totals[n][b] = totals[n][b] + 1d;
				// ////System.out.println("IqmOpFracBox: totals[n][b]  "+
				// totals[n][b] );
				// ////totals[n][b] = totals[n][b]/totalsMax[b];
				//
				// ////System.out.println("IqmOpFracBox: totals[n][b]  "+
				// totals[n][b] );
				// } //bands

				// fast
				// version--------------------------------------------------------------------
				// test image stack_0015.tif 5x enlarged to 930x1130 pixel: 10
				// times faster!
				int minX = tileRaster[r].getMinX();
				int minY = tileRaster[r].getMinY();
				int tileWidth = tileRaster[r].getWidth(); // ==imgWidth
				int tileHeight = tileRaster[r].getHeight(); // ==imgWidth
				// System.out.println("IqmOpFracBox: minX: "+minX+ "  minY: "
				// +minY+ "   tileWidth: "+ tileWidth+ "     tileHeight: "+
				// tileHeight);
				for (int b = 0; b < numBands; b++) {
					boolean isGreaterZeroFound = false;
					double pixelMax = 0.0; // initial value
					double pixelMin = typeGreyMax; // initial value
					for (int x = 0; x < tileWidth; x++) {
						for (int y = 0; y < tileHeight; y++) {
							// System.out.println("IqmOpFracBox: b, x, y, "
							// +b+"  " + x+ "  "+y);
							int pixel = tileRaster[r].getSample(minX + x, minY
									+ y, b);
							if (isBinary) { // binary image
								if (pixel > 0) {
									totals[n][b] = totals[n][b] + 1d;
									isGreaterZeroFound = true;
								}
							} else { // grey image
								if (pixel > pixelMax)
									pixelMax = pixel;
								if (pixel < pixelMin)
									pixelMin = pixel;
							}
							if (isGreaterZeroFound)
								break;
							// System.out.println("IqmOpFracBox: totals[n][b]  "+
							// totals[n][b] );
						}
						if (isGreaterZeroFound)
							break;
					}
					if (!isBinary) {
						double widthZ = width * typeGreyMax
								/ ((pi.getWidth() + pi.getHeight()) / 2.0); // epsilonZ/255 = epsilon/imageSize
						if (greyMode == 0) { // DBC
							double l = pixelMax / widthZ; // number of boxes in
															// z direction
							double k = pixelMin / widthZ;
							l = Math.ceil(l);
							k = Math.ceil(k);
							totals[n][b] = totals[n][b] + (l - k + 1);
						}

						if (greyMode == 1) { // RDBC
							double diff = pixelMax - pixelMin;
							diff = Math.ceil(diff / widthZ);
							totals[n][b] = totals[n][b] + diff;
						}
					}
				}

				// ---------------------------------------------------------------------------------

			} // r tileRaster[]

			// normalization
			for (int b = 0; b < numBands; b++) {
				if (isBinary) {
					// do nothing
				} else {
					// totals[n][b] = totals[n][b]*imgWidth*imgWidth; //average volume
				}
			}
			for (int b = 0; b < numBands; b++) {
				boxWidth[n][b] = width;
			}
			proz = (int) ((n+1) * 100.f / (float)number);
			this.fireProgressChanged(proz); 

		} // n loop

		// Log
		double[][] lnTotals = new double[number][numBands];
		// double[][] lnNumbers = new double[number][numBands];
		double[][] lnBoxWidth = new double[number][numBands];
		for (int n = 0; n < number; n++) {
			for (int b = 0; b < numBands; b++) {
				if (totals[n][b] == 0) {
					lnTotals[n][b] = Math.log(Float.MIN_VALUE); // damit logarithmus nicht undefiniert ist
				} else {
					lnTotals[n][b] = Math.log(totals[n][b]);
				}
				lnBoxWidth[n][b] = Math.log(boxWidth[n][b]);
				// System.out.println("IqmOpFracBox: " );
			}
		}

		// set table
		int numColumns = model.getColumnCount();
		if (isBinary) {
			model.addColumn("Db");
		}
		if (!isBinary) {
			if (greyMode == 0) { // DBC
				model.addColumn("Db_DBC");
			}
			if (greyMode == 1) { // RDBC
				model.addColumn("Db_RDBC");
			}
		}
		model.addColumn("StdDev");
		model.addColumn("r2");
		model.addColumn("adjustet_r2");
		// set regression data headers
		for (int n = 0; n < number; n++)
			model.addColumn("DataX_" + (n + 1));
		for (int n = 0; n < number; n++)
			model.addColumn("DataY_" + (n + 1));

		boolean isLineVisible = false;

		for (int b = 0; b < numBands; b++) { // several bands

			// Plot //nur ein Band!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			Vector<Double> dataX = new Vector<Double>();
			Vector<Double> dataY = new Vector<Double>();
			for (int n = 0; n < number; n++) {
				dataY.add(lnTotals[n][b]);
				dataX.add(lnBoxWidth[n][b]);
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

			if (optShowPlot) {
				if (isBinary) {
					PlotTools.displayRegressionPlotXY(dataX, dataY,
							isLineVisible, imgName + "_Band" + b,
							"Box Dimension", "ln(Box Width)", "ln(Count)",
							regStart, regEnd, optDeleteExistingPlot);
				}
				if (!isBinary) {
					if (greyMode == 0) { // DBC
						PlotTools.displayRegressionPlotXY(dataX, dataY,
								isLineVisible, imgName + "_Band" + b,
								"Box Dimension (DBC)", "ln(Box Width)",
								"ln(Count)", regStart, regEnd,
								optDeleteExistingPlot);
					}
					if (greyMode == 1) { // RDBC
						PlotTools.displayRegressionPlotXY(dataX, dataY,
								isLineVisible, imgName + "_Band" + b,
								"Box Dimension (RDBC)", "ln(Box Width)",
								"ln(Count)", regStart, regEnd,
								optDeleteExistingPlot);
					}
				}
			}
			// double[] p =
			// IqmTools.getLinearRegression(IqmTools.reverse(dataX),
			// IqmTools.reverse(dataY), regStart, regEnd);
			double[] p = PlotTools.getLinearRegression(dataX, dataY, regStart,
					regEnd);

			double slope = p[1];
			double dim = 0.0;
			if (isBinary) {
				dim = -slope;
			} else {
				// dim = 3.0-(slope/2.0);
				dim = -slope;
			}

			// set table data
			model.setValueAt(dim, b, numColumns);
			model.setValueAt(p[3], b, numColumns + 1);
			model.setValueAt(p[4], b, numColumns + 2);
			model.setValueAt(p[5], b, numColumns + 3);
			// set regression data
			for (int n = 0; n < number; n++) {
				// model.setValueAt(lnBoxWidth[n][b], b, numColumns+(n+1));
				model.setValueAt(boxWidth[n][b], b, numColumns + 3 + 1 + n);
			}
			for (int n = 0; n < number; n++) {
				// model.setValueAt(lnTotals[n][b], b, numColumns+number+(n+1));
				model.setValueAt(totals[n][b], b, numColumns + number + 3 + 1
						+ n);
			}
		}
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(model);
		// return new Result(new IqmDataBox(piOut)); //Optionally put out an image
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpFracBoxDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpFracBoxDescriptor.TYPE;
	}
}
