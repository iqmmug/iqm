package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpHistoModify.java
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


import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

import at.mug.iqm.api.gui.BoardPanel;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpHistoModifyDescriptor;

/**
 * @author Ahammer, Kainz
 * @since    2009 06
 */
public class IqmOpHistoModify extends AbstractOperator {

	public IqmOpHistoModify() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	private Histogram getHistogram(RenderedOp rendOp) {
		Histogram histo = (Histogram) rendOp.getProperty("histogram");
		return histo;
	}

	private RenderedOp getHistogramRendOp(PlanarImage pi) {
		int numBands = pi.getNumBands();
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		int[] bins = new int[numBands];
		double[] low = new double[numBands];
		double[] high = new double[numBands];
		for (int b = 0; b < numBands; b++) {
			bins[b] = (int) typeGreyMax + 1;
			low[b] = 0.0d;
			high[b] = typeGreyMax + 1;
		}
		ParameterBlock pb = new ParameterBlock();
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(pi);
		pb.add(null);
		pb.add(1);
		pb.add(1);
		pb.add(bins);
		pb.add(low);
		pb.add(high);
		RenderedOp rendOp = JAI.create("histogram", pb, null);
		return rendOp;
	}

	private int[][] getSubTotalValues(Histogram histo, double subLow,
			double subHigh) {
		int numBands = histo.getNumBands();
		int numBins = histo.getNumBins(0);
		int[] totals = histo.getTotals();
		int[] subTotalLow = new int[numBands];
		int[] subTotalHigh = new int[numBands];
		;
		int[][] subTotals = new int[2][numBands]; // low and high
		for (int b = 0; b < numBands; b++) {
			// calculate low subTotalLevel
			subTotalLow[b] = 0; // initial value
			double subTotal = 0.0d;
			while (subTotal / totals[b] < subLow) {
				subTotalLow[b] = subTotalLow[b] + 1;
				subTotal = histo.getSubTotal(b, 0, subTotalLow[b]);
				if (subTotalLow[b] >= (numBins - 10)) {
					BoardPanel
							.appendTextln("IqmOpHistoModify: Alert: Low Offset value too high:");
					BoardPanel
							.appendTextln("IqmOpHistoModify: Low Offset value set to: "
									+ subTotalLow[b]);
					break;
				}
				// System.out.println("IqmOpHistoModify: subTotal: " +
				// subTotal);
			}
			// System.out.println("IqmOpHistoModify: subTotalLow[b]: " +
			// subTotalLow[b]);
			// System.out.println("IqmOpHistoModify: subTotal/Totals[b]: " +
			// (subTotal/totals[b]));
			// calculate high subTotalLevel
			subTotalHigh[b] = numBins - 1; // initial value
			subTotal = 0.0d;
			while (subTotal / totals[b] < subHigh) {
				subTotalHigh[b] = subTotalHigh[b] - 1;
				subTotal = histo.getSubTotal(b, subTotalHigh[b], numBins - 1);
				if (subTotalHigh[b] <= (subTotalLow[b] + 10)) {
					BoardPanel
							.appendTextln("IqmOpHistoModify: Alert: High Offset value too high:");
					BoardPanel
							.appendTextln("IqmOpHistoModify: High Offset value set to: "
									+ subTotalHigh[b]);
					break;
				}
				// System.out.println("IqmOpHistoModify: subTotal: " +
				// subTotal);
			}
			// System.out.println("IqmOpHistoModify: subTotalHigh[b]: " +
			// subTotalHigh[b]);
			// System.out.println("IqmOpHistoModify: subTotal/Totals[b]: " +
			// (subTotal/totals[b]));
		}
		subTotals[0] = subTotalLow;
		subTotals[1] = subTotalHigh;
		return subTotals;
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

		fireProgressChanged(5);
		
		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		// IqmTools.plotHistogram(pi);
		String fileName = String.valueOf(pi.getProperty("file_name"));
		String imgName = (String) pi.getProperty("image_name");
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		int numBands = pi.getNumBands();
		int width = pi.getWidth();
		int height = pi.getHeight();
		int method = pb.getIntParameter("Method");
		double offSetLow = pb.getDoubleParameter("OffSetLow") / 100.d; 
		double offSetHigh = pb.getDoubleParameter("OffSetHigh") / 100.d;
		boolean isBandCombineSet = false;
		if (pb.getIntParameter("BandCombine") == 0)
			isBandCombineSet = false;
		if (pb.getIntParameter("BandCombine") == 1)
			isBandCombineSet = true;

		if ((offSetLow > 0) || (offSetHigh > 0)) {
			System.out.println("IqmOpHistoModify: offSetLow: " + offSetLow);
			System.out.println("IqmOpHistoModify: offSetHigh: " + offSetHigh);
			// IqmTools.plotHistogram(pi);

			RenderedOp rendOp = getHistogramRendOp(pi);
			Histogram histo = getHistogram(rendOp);

			int[][] subTotals = getSubTotalValues(histo, offSetLow, offSetHigh);

			double[] lowClamp = new double[numBands]; // = {offSetLowValue,
														// offSetLowValue,
														// offSetLowValue};
			double[] highClamp = new double[numBands]; // = {offSetHighValue,
														// offSetHighValue,
														// offSetHighValue};
			for (int b = 0; b < numBands; b++) {
				lowClamp[b] = subTotals[0][b];
				highClamp[b] = subTotals[1][b];
				System.out
						.println("IqmOpHistoModify: Band, lowClamp[b], highClamp[b] "
								+ b + " " + lowClamp[b] + "  " + highClamp[b]);
			}
			if (isBandCombineSet) {
				double lowClampMean = 0d;
				double highClampMean = 0d;
				for (int b = 0; b < numBands; b++) {
					lowClampMean = lowClampMean + lowClamp[b];
					highClampMean = highClampMean + highClamp[b];
				}
				lowClampMean = lowClampMean / numBands; // Mean
				highClampMean = highClampMean / numBands;
				for (int b = 0; b < numBands; b++) {
					lowClamp[b] = lowClampMean;
					highClamp[b] = highClampMean;
					System.out
							.println("IqmOpHistoModify: Band, lowClamp[b], highClamp[b] "
									+ b
									+ " "
									+ lowClamp[b]
									+ "  "
									+ highClamp[b]);
				}
			}
			// IqmTools.plotHistogram(pi);
			ParameterBlock pbClamp = new ParameterBlock();
			pbClamp.removeParameters();
			pbClamp.removeSources();
			pbClamp.addSource(pi);
			pbClamp.add(lowClamp);
			pbClamp.add(highClamp);
			pi = JAI.create("clamp", pbClamp, null);
			// IqmTools.plotHistogram(pi);
		}
		
		fireProgressChanged(50);
		if (isCancelled(getParentTask())){
			return null;
		}

		if (method == 0) { // rescale
			// get Extrema
			ParameterBlock pbWork = new ParameterBlock();
			pbWork.addSource(pi);
			pbWork.add(null);
			pbWork.add(1);
			pbWork.add(1);
			RenderedOp rendOp = JAI.create("extrema", pbWork, null);
			double[] greyMinVec = (double[]) rendOp.getProperty("minimum");
			double[] greyMaxVec = (double[]) rendOp.getProperty("maximum");
			double greyMin = greyMinVec[0];
			double greyMax = greyMaxVec[0];
			for (int b = 1; b < greyMinVec.length; b++) {
				if (greyMinVec[b] < greyMin)
					greyMin = greyMinVec[b];
				if (greyMaxVec[b] > greyMax)
					greyMax = greyMaxVec[b];
			}
			double[] scale = new double[1];
			double[] offset = new double[1];
			scale[0] = typeGreyMax / (greyMax - greyMin);
			offset[0] = typeGreyMax * greyMin / (greyMin - greyMax);

			pbWork.removeParameters();
			pbWork.removeSources();
			pbWork.addSource(pi);
			pbWork.add(scale);
			pbWork.add(offset);
			piOut = JAI.create("rescale", pbWork, null);
			// IqmTools.plotHistogram((PlanarImage) piOut);
		}
		if (method == 1) { // equalize //Flat uniform histogram
			float[][] CDFeq = new float[numBands][];
			for (int b = 0; b < numBands; b++) {
				CDFeq[b] = new float[(int) typeGreyMax + 1];
				for (int i = 0; i <= typeGreyMax; i++) {
					CDFeq[b][i] = (i + 1) / ((float) (typeGreyMax + 1));
				}
			}

			RenderedOp rendOp = getHistogramRendOp(pi);
			piOut = JAI.create("matchcdf", rendOp, CDFeq);
			// IqmTools.plotHistogram((PlanarImage) piOut);

			// old method
			// int sum = 0;
			// byte[] cumulative = new byte[(int)typeGreyMax+1];
			// int array[] = getHistogram(pi, typeGreyMax);
			// float scale = (float) (typeGreyMax / (float) (imgWidth *
			// imgHeight));
			// for ( int i = 0; i <= typeGreyMax; i++ ) {
			// sum += array[i];
			// cumulative[i] = (byte)((sum * scale) + .5F);
			// }
			// LookupTableJAI lookup = new LookupTableJAI(cumulative);
			// ParameterBlock pb = new ParameterBlock();
			// pb.addSource(pi);
			// pb.add(lookup);
			// piOut = JAI.create("lookup", pb, null);
			// IqmTools.plotHistogram((PlanarImage) piOut);
		}

		if (method == 2) { // normalize
			double[] mean = new double[] { typeGreyMax / 2d, typeGreyMax / 2d,
					typeGreyMax / 2d };
			double[] stDev = new double[] { (typeGreyMax + 1d) / 4d,
					(typeGreyMax + 1d) / 4d, (typeGreyMax + 1d) / 4d }; // ?? 34
																		// bei
																		// Byte
																		// bei
																		// short???
			float[][] CDFnorm = new float[numBands][];
			for (int b = 0; b < numBands; b++) {
				CDFnorm[b] = new float[(int) typeGreyMax + 1];
				double mu = mean[b];
				double twoSigmaSquared = 2.0 * stDev[b] * stDev[b];
				CDFnorm[b][0] = (float) Math.exp(-mu * mu / twoSigmaSquared);
				for (int i = 1; i <= typeGreyMax; i++) {
					double deviation = i - mu;
					CDFnorm[b][i] = CDFnorm[b][i - 1]
							+ (float) Math.exp(-deviation * deviation
									/ twoSigmaSquared);
				}
			}
			for (int b = 0; b < numBands; b++) {
				double CDFnormLast = CDFnorm[b][(int) typeGreyMax];
				for (int i = 0; i <= typeGreyMax; i++) {
					CDFnorm[b][i] /= CDFnormLast;
				}
			}

			RenderedOp rendOp = getHistogramRendOp(pi);
			piOut = JAI.create("matchcdf", rendOp, CDFnorm);
			// IqmTools.plotHistogram((PlanarImage) piOut);
		}
		if (method == 3) { // piecewise
			float[][][] bp = new float[1][2][];
			bp[0][0] = new float[] { 0.0F,
					32.0f / 255.0f * ((float) typeGreyMax),
					64.0f / 255.0f * ((float) typeGreyMax), (float) typeGreyMax };
			bp[0][1] = new float[] { 0.0F,
					128.0f / 255.0f * ((float) typeGreyMax),
					112.0f / 255.0f * ((float) typeGreyMax),
					(float) typeGreyMax };
			// bp[0][1] = new float[] { 0.0F, ((float)typeGreyMax+1f)/2f,
			// 112.0f, (float)typeGreyMax };
			// bp[0][0] = new float[] { 0.0F, 32.0F, 64.0F, 255.0F };
			// bp[0][1] = new float[] { 0.0F, 128.0F, 112.0F, 255.0F };
			piOut = JAI.create("piecewise", pi, bp);
			// IqmTools.plotHistogram((PlanarImage) piOut);
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
		if (this.name == null){
			this.name = new IqmOpHistoModifyDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpHistoModifyDescriptor.TYPE;
	}
}
