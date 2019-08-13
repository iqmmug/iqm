package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracFFT.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.DFTDescriptor;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.plot.PlotTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracFFTDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2011 02
 */
public class IqmOpFracFFT extends AbstractOperator {

	public IqmOpFracFFT() {
		this.setCancelable(true);
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
		
		int numMaxK = pb.getIntParameter("NumMaxK");
		int topDim = pb.getIntParameter("TopDim") + 1; // because 0: Dt = 1,
															// 1: Dt = 2
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
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		// initialize table for Dimension data
		TableModel model = new TableModel("FFT Dimension [" + imgName + "]");
		// adding a lot of columns would be very slow due to active model
		model.addColumn("FileName");
		model.addColumn("ImageName");
		model.addColumn("Band");
		model.addColumn("RegStart");
		model.addColumn("RegEnd");
		for (int b = 0; b < pi.getNumBands(); b++) { // mehrere Bands
			model.addRow(new Object[] { fileName, imgName, b, regStart, regEnd });
		}

		// data array
		// double[][] totals = new double[numEps][numBands];
		// double[][] eps = new double[numEps][numBands];

		// data vector array, because k (eps) could be any number
		Vector<Vector<Double>> totalsB = new Vector<Vector<Double>>(); // bands
		Vector<Double> totals = new Vector<Double>(); // data
		Vector<Vector<Double>> epsB = new Vector<Vector<Double>>(); // bands
		Vector<Double> eps = new Vector<Double>(); // data

		ParameterBlock pbTmp = new ParameterBlock();
		pbTmp.addSource(pi);
		pbTmp.add(DFTDescriptor.SCALING_NONE); // scaling does not matter
		// pb.add(DFTDescriptor.SCALING_UNITARY);
		// pb.add(DFTDescriptor.SCALING_DIMENSIONS);
		pbTmp.add(DFTDescriptor.REAL_TO_COMPLEX);
		piOut = JAI.create("DFT", pbTmp, null);

		pbTmp = new ParameterBlock();
		pbTmp.addSource(piOut);
		piOut = JAI.create("MagnitudeSquared", pbTmp, null); // Magnitude or
															// MagnitudeSquared

		// Periodic shift: corners are shifted to the center of the
		// image----------
		pbTmp = new ParameterBlock();
		pbTmp.addSource(piOut);
		// pb.add(shiftX); //default: Width/2
		// pb.add(shiftY); //default: Height/2
		piOut = JAI.create("PeriodicShift", pbTmp);

		// Taking only the upper half part of the image, containing all
		// information
		int powerHeight = piOut.getHeight();
		int powerWidth = piOut.getWidth();
		int halfPowerHeight = Math.round(powerHeight / 2.0f);
		int offSetHeight = powerHeight - halfPowerHeight;
		pbTmp = new ParameterBlock();
		pbTmp.addSource(piOut);
		pbTmp.add((float) 0); // offSetX
		pbTmp.add((float) offSetHeight); // offSetY
		pbTmp.add((float) powerWidth); // newWidth
		pbTmp.add((float) halfPowerHeight); // newHeight

		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY)); // nicht
																			// sicher
																			// ob
																			// n�tig
		piOut = JAI.create("Crop", pbTmp, rh);
		
		if (isCancelled(getParentTask())){
			return null;
		}

		// eliminate shift!!!! essential
		if (piOut.getMinX() != 0 || piOut.getMinY() != 0) {
			ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
			pbTrans.addSource(piOut);
			pbTrans.setParameter("xTrans", piOut.getMinX() * -1.0f);
			pbTrans.setParameter("yTrans", piOut.getMinY() * -1.0f);
			piOut = JAI.create("translate", pbTrans);
		}

		int cropWidth = piOut.getWidth();
		int cropHeight = piOut.getHeight();

		double halfCropWidth = (double) cropWidth / 2.0f;
		double offSetCropWidth = cropWidth - halfCropWidth;

		int halfCropHeight = cropHeight / 2;

		Raster ra = piOut.getData();
		for (int b = 0; b < numBands; b++) { // several bands
			totals = new Vector<Double>();
			eps = new Vector<Double>();
			// for (int n = 0; n < numEps; n++){ //does not work
			for (int x = 0; x < cropWidth; x++) { // scroll through half of the
													// power image
				if (isCancelled(getParentTask())){
					return null;
				}
				
				int proz = (x) * 100 / cropWidth;
				this.fireProgressChanged(proz);
				for (int y = 0; y < halfCropHeight; y++) {
					double sample = ra.getSampleDouble(x, y, b);
					if (sample > 0) { // no zero values!!!therefore k can be
										// smaller than set in GUI!!!
						double valueX = ((double) x + 1)
								- (offSetCropWidth + 0.5); // in the middle of
															// the image = 0
						double k = Math.sqrt(valueX * valueX + (y + 0.5)
								* (y + 0.5)); // Magnitude of k
						totals.add(sample);
						eps.add(k);
					}
				}
			} // 0>=n<=numEps loop through eps
			totalsB.add(totals);
			epsB.add(eps);
			// correct RegEnd if necessary //bugfix 2012 01 30
			if (totals.size() < regEnd) {
				regEnd = totals.size();
				model.setValueAt(regEnd, b, 4); // settting for regEnd
			}

		} // b band loop

		// Log
		// double[][] lnTotals = new double[numEps][numBands];
		// //double[][] lnNumbers = new double[numEps][numBands];
		// double[][] lnEps = new double[numEps][numBands];
		// for (int n = 0; n < numEps; n++){
		// for(int b = 0; b < numBands; b++){
		// if (totals[n][b] == 0) totals[n][b] = Float.MIN_VALUE; //avoiding
		// zero for logarithm
		// lnTotals[n][b] = Math.log(totals[n][b]);
		// lnEps[n][b] = Math.log(eps[n][b]);
		// //System.out.println("IqmOpFracFFT: " );
		// }
		// }

		// set table headers
		int numColumns = model.getColumnCount();
		model.addColumn("Df");
		model.addColumn("StdDev");
		model.addColumn("r2");
		model.addColumn("adjustet_r2");
		// set table headers for regression data
		// set regression data //beware: for an image stack this could be a lot
		// of data!!!!!!!!!!
		boolean setRegressionData = false;
		if (setRegressionData) {
			numColumns = model.getColumnCount();
			for (int n = 0; n < epsB.get(0).size(); n++)
				model.addColumn("DataX_" + (n + 1));
			for (int n = 0; n < totalsB.get(0).size(); n++)
				model.addColumn("DataY_" + (n + 1));
		}

		// Log is calculated directly in loop below
		boolean isLineVisible = false;
		for (int b = 0; b < numBands; b++) { // several bands
			// Plot //only one Band!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			final Vector<Double> dataX = new Vector<Double>();
			final Vector<Double> dataY = new Vector<Double>();
			for (int n = 0; n < totalsB.get(b).size(); n++) {
				
				if (isCancelled(getParentTask())){
					return null;
				}
				
				// dataY.add(lnTotals[n][b]);
				// dataX.add(lnEps[n][b]);
				double tot = totalsB.get(b).get(n);
				double ep = epsB.get(b).get(n);
				if (tot == 0.0)
					tot = Double.MIN_VALUE;
				if (ep == 0.0)
					ep = Double.MIN_VALUE;
				dataY.add(Math.log(tot));
				dataX.add(Math.log(ep));
			}
			// dataX and datY are unsorted!!
			// sorting essential for limited RegStart RegEnd settings
			// get the sorted index
			Integer[] idx = new Integer[dataX.size()];
			for (int i = 0; i < idx.length; i++)
				idx[i] = i;

			// for (int i = 0; i < idx.length; i++ ) System.out.println("idx: "
			// + idx[i]);
			Arrays.sort(idx, new Comparator<Integer>() {
				@Override
				public int compare(Integer idx1, Integer idx2) {
					return Double.compare(dataX.get(idx1), dataX.get(idx2));
				}
			});
			// for (int i = 0; i < idx.length; i++ ) System.out.println("idx: "
			// + idx[i]);

			// get sorted vectors and restrict to maximal k
			Vector<Double> dataXSorted = new Vector<Double>();
			Vector<Double> dataYSorted = new Vector<Double>();
			for (int i = 0; i < idx.length; i++) {
				if ((i + 1) <= numMaxK) { // restrict to maximal k
					dataXSorted.add(i, dataX.get(idx[i])); // idx is sorted
					dataYSorted.add(i, dataY.get(idx[i]));
				}
			}

			// System.out.println("IqmOpFracFFT: right before regressionplotXY");
			if (optShowPlot) {
				// System.out.println("IqmopFracFFT: regStart, regEnd," +
				// regStart +"   " +regEnd);
				PlotTools.displayRegressionPlotXY(dataXSorted, dataYSorted,
						isLineVisible, imgName + "_Band" + b, "FFT Dimension",
						"ln(k)", "ln(Count)", regStart, regEnd,
						optDeleteExistingPlot);
			}
			// double[] p =
			// IqmTools.getLinearRegression(IqmTools.reverse(dataX),
			// IqmTools.reverse(dataY), regStart, regEnd);

			// System.out.println("IqmOpFracFFT: right before linear regression");
			double[] p = PlotTools.getLinearRegression(dataXSorted,
					dataYSorted, regStart, regEnd);

			double slope = p[1];
			double dim = 0.0;
			dim = (3.0 * topDim + 2.0 + slope) / 2.0;

			// set table data
			model.setValueAt(dim, b, numColumns);
			model.setValueAt(p[3], b, numColumns + 1);
			model.setValueAt(p[4], b, numColumns + 2);
			model.setValueAt(p[5], b, numColumns + 3);

			// System.out.println("IqmOpFracFFT: right before setting regression data");
			// set regression data
			if (setRegressionData) {
				eps = epsB.get(b);
				int epsSize = eps.size();
				for (int n = 0; n < epsSize; n++) {
					model.setValueAt(eps.get(n), b, numColumns + 3 + 1 + n);
				}
				totals = totalsB.get(b);
				int totalsSize = totals.size();
				for (int n = 0; n < totalsSize; n++) {
					model.setValueAt(totals.get(n), b, numColumns + epsSize + 3
							+ 1 + n);
				}
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
			this.name = new IqmOpFracFFTDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpFracFFTDescriptor.TYPE;
	}
}
