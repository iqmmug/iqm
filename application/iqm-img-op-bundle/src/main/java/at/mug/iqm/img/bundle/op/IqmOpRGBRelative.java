package at.mug.iqm.img.bundle.op;

import java.awt.image.ColorModel;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpRGBRelative.java
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


import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpRGBRelativeDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 04
 */
public class IqmOpRGBRelative extends AbstractOperator {

	public IqmOpRGBRelative() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@SuppressWarnings("unused")
	private void displayMinMax(PlanarImage pi, String text) {
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(null); // ROI
		pb.add(1);
		pb.add(1);
		RenderedOp rOp = JAI.create("extrema", pb);
		double[] maxVec = (double[]) rOp.getProperty("maximum");
		double[] minVec = (double[]) rOp.getProperty("minimum");
		double max = maxVec[0];
		double min = minVec[0];
		System.out.println("IqmRGBRelativeOpImage: image Min Max " + text + " " + min + "  " + max);
	}

	/**
	 * This method calculates the ratios of two images in percent Ratio =
	 * pi1/(pi1+pi2+pi3)
	 * 
	 * @param pi1
	 *            PlanarImage single plane
	 * @param pi
	 *            PlanarImage three plane
	 * 
	 * @return PlanarImage
	 */
	private PlanarImage calcRatio(PlanarImage piBand, PlanarImage pi) {
		ParameterBlock pb = new ParameterBlock();

		double[][] m = { { 1., 1., 1., 0 } };
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(pi);
		pb.add(m);
		pi = JAI.create("bandcombine", pb, null);
		// displayMinMax(pi, "bandcombine:");

		// divide images
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piBand);
		pb.addSource(pi);
		pi = JAI.create("divide", pb); // = piBand/(piBand1+piBand2+piBand3)
		// displayMinMax(pi,"divide:");

		return pi;
	}

	/**
	 * This method calculates difference of two bands: band1 -band2
	 * 
	 * @param pi  PlanarImage RGB 3-band
	 * @param band1 band1
	 * @param band2 band2
	 * @param p percentage
	 * @return PlanarImage 1-band (normalized 0...255 and Type DataBuffer.TYPE_??)
	 */
	private PlanarImage calcDifferenceForBand(PlanarImage pi, int band1,
			int band2, int sensitivity) {

		// ParameterBlock pb = new ParameterBlock();
		// pb.addSource(pi);
		// pb.add(DataBuffer.TYPE_FLOAT);
		// pi = JAI.create("format", pb);

		PlanarImage pi1 = JAI.create("bandselect", pi, new int[] { band1 });
		PlanarImage pi2 = JAI.create("bandselect", pi, new int[] { band2 });
		PlanarImage piDiff = calcDifference(pi1, pi2, sensitivity);
		piDiff = calcBinarizedToOne(piDiff);

		// pi = normalizeImage(pi);

		// pb.removeSources();
		// pb.removeParameters();
		// pb.addSource(piDiff);
		// pb.add(DataBuffer.TYPE_BYTE);
		// piDiff = JAI.create("format", pb);

		// displayMinMax(piRatio, "piRatio:");
		return piDiff;
	}

	/**
	 * This method calculates the difference of two images pdiff = pi1 -pi2
	 * 
	 * @param pi1 PlanarImage single plane
	 * @param pi2 PlanarImage single plane
	 * @param double sensitivity
	 * @return PlanarImage
	 */
	private PlanarImage calcDifference(PlanarImage pi1, PlanarImage pi2, double sensitivity) {
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi1);
		pb.addSource(pi2);
		PlanarImage piDiff = JAI.create("subtract", pb); // positive is positive, negative is 0 displayMinMax(piDiff,"subtract:");

		double thres = (1.0 - (sensitivity) / 100.0) * 255.0; // maximal difference is 255
		// System.out.println("IqmOpRGBRelative: sensitivity: " + sensitivity);
		// System.out.println("IqmOpRGBRelative: thres: " + thres);
		double low[]  = { 0 };
		double high[] = { 0 + thres };
		double map[]  = { 0 };
		pb = new ParameterBlock();
		pb.addSource(piDiff);
		pb.add(low);
		pb.add(high);
		pb.add(map);
		piDiff = JAI.create("threshold", pb, null);
		// displayMinMax(piDiff, "threshold to 0- thres to 0:");

		return piDiff;
	}

	/**
	 * This method compares the input image with the preset percentage
	 * 
	 * @param pi PlanarImage single plane
	 * @param p percentage
	 * @return PlanarImage with values 0 and 1
	 */
	private PlanarImage calcThreshold(PlanarImage pi, double ratio) {
		// System.out.println("calcThreshold p: " +p);
		ParameterBlock pb = new ParameterBlock();

		double low[] = { 0 };
		double high[] = { ratio - 0.000000001 };
		double map[] = { 0 };
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(pi);
		pb.add(low);
		pb.add(high);
		pb.add(map);
		pi = JAI.create("threshold", pb, null);
		// displayMinMax(pi, "threshold to 0-p-0.000000001 to 0:");

		low[0] = ratio;
		high[0] = 1;
		map[0] = 255;
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(pi);
		pb.add(low);
		pb.add(high);
		pb.add(map);
		pi = JAI.create("threshold", pb, null);
		// displayMinMax(pi, "threshold p bis 1  to 255:");
		return pi;
	}

	/**
	 * This method binarizes an image (all values over 1 are set to one)
	 * 
	 * @param pi
	 *            PlanarImage single plane
	 * @return PlanarImage with values 0 and 1
	 */
	private PlanarImage calcBinarizedToOne(PlanarImage pi) {
		// System.out.println("calcBinarizedToOne");
		ParameterBlock pb = new ParameterBlock();
		//

		// double low[] = {0};
		// double high[] = {1.0-0000000000000000001};
		// double map[] = {0};
		// pb = new ParameterBlock();
		// pb.addSource(pi);
		// pb.add(low);
		// pb.add(high);
		// pb.add(map);
		// pi = JAI.create("threshold", pb, null);
		// displayMinMax(pi, "threshold to 0- thres to 0:");

		// low[0] = 1.0;
		// high[0] = 255.0;
		// map[0] = 1;
		double low[] = { 1.0 };
		double high[] = { 255.0 };
		double map[] = { 1 };
		pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(low);
		pb.add(high);
		pb.add(map);
		pi = JAI.create("threshold", pb, null);
		// displayMinMax(pi, "1 +thres bis 255  to 1:");
		return pi;
	}

	/**
	 * This method merges two binary images (0,1)
	 * 
	 * @param pi PlanarImage single plane
	 * @return PlanarImage with values 0 and 1
	 */
	private PlanarImage mergeImages(PlanarImage pi1, PlanarImage pi2) {
		// System.out.println("calcBinarizedToOne");
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi1);
		pb.addSource(pi2);
		PlanarImage piMerge = JAI.create("add", pb);
		piMerge = this.calcBinarizedToOne(piMerge);
		// displayMinMax(piMerge,"add:");
		return piMerge;
	}

	/**
	 * This method normalizes the image
	 * 
	 * @param pi
	 *            binary PlanarImage only 0 or 1 is allowed
	 * @return PlanarImage with maximum 255
	 */
	private PlanarImage normalizeImage(PlanarImage pi) {
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(new double[] { 255 });
		return pi = JAI.create("multiplyconst", pb);
	}

	/**
	 * This method calculates one band
	 * 
	 * @param pi PlanarImage RGB 3-band
	 * @param band0 band
	 * @param p percentage
	 * @return PlanarImage 1-band (normalized 0...255 and Type DataBuffer.TYPE_??)
	 */
	private PlanarImage calcRatioForBand(PlanarImage pi, int band, int ratio) {

		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(DataBuffer.TYPE_FLOAT);
		pi = JAI.create("format", pb);

		PlanarImage pi0 = JAI.create("bandselect", pi, new int[] { band });
		PlanarImage piRatio = calcRatio(pi0, pi);
		piRatio = calcThreshold(piRatio, ratio / 100.);

		// pi = normalizeImage(pi);

		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piRatio);
		pb.add(DataBuffer.TYPE_BYTE);
		piRatio = JAI.create("format", pb);

		// displayMinMax(piRatio, "piRatio:");
		return piRatio;
	}

	private PlanarImage calcOutImage(PlanarImage piRatio, PlanarImage pi,
			int band, int binarize) {

		// PlanarImage pi0 = JAI.create("bandselect", pi, new int[] {0});
		// PlanarImage pi1 = JAI.create("bandselect", pi, new int[] {1});
		// PlanarImage pi2 = JAI.create("bandselect", pi, new int[] {2});

		double[][] m0 = { { 1.0d, 0.0d, 0.0d, 0.0d } };
		double[][] m1 = { { 0.0d, 1.0d, 0.0d, 0.0d } };
		double[][] m2 = { { 0.0d, 0.0d, 1.0d, 0.0d } };
		PlanarImage pi0 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(m0), null);
		PlanarImage pi1 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(m1), null);
		PlanarImage pi2 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(m2), null);

		ParameterBlock pb = new ParameterBlock();

		if (binarize == 0) {
			if (band == 0) { // Red
				pb.removeSources();
				pb.removeParameters();
				pb.addSource(pi0);
				pb.addSource(piRatio);
				pi0 = JAI.create("add", pb); // alles was �ber 255 geht wird abgeschnitten und ist einfach wei� displayMinMax(pi0, "pi0:");
			
				pb.removeSources();
				pb.removeParameters();
				pb.addSource(pi1);
				pb.addSource(piRatio);
				pi1 = JAI.create("subtract", pb); // alles was �ber -0 geht wird abgeschnitten und ist einfach 0 displayMinMax(pi1, "pi1:");

				pb.removeSources();
				pb.removeParameters();
				pb.addSource(pi2);
				pb.addSource(piRatio);
				pi2 = JAI.create("subtract", pb); // alles was �ber -0 geht wird abgeschnitten und ist einfach 0 displayMinMax(pi2, "pi2:");
			}
			if (band == 1) { // Green
				pb.removeSources();
				pb.removeParameters();
				pb.addSource(pi0);
				pb.addSource(piRatio);
				pi0 = JAI.create("subtract", pb);

				pb.removeSources();
				pb.removeParameters();
				pb.addSource(pi1);
				pb.addSource(piRatio);
				pi1 = JAI.create("add", pb);

				pb.removeSources();
				pb.removeParameters();
				pb.addSource(pi2);
				pb.addSource(piRatio);
				pi2 = JAI.create("subtract", pb);
			}
			if (band == 2) { // Blue
				pb.removeSources();
				pb.removeParameters();
				pb.addSource(pi0);
				pb.addSource(piRatio);
				pi0 = JAI.create("subtract", pb);

				pb.removeSources();
				pb.removeParameters();
				pb.addSource(pi1);
				pb.addSource(piRatio);
				pi1 = JAI.create("subtract", pb);

				pb.removeSources();
				pb.removeParameters();
				pb.addSource(pi2);
				pb.addSource(piRatio);
				pi2 = JAI.create("add", pb);
			}

			pb.removeSources();
			pb.removeParameters();
			pb.setSource(pi0, 0);
			pb.setSource(pi1, 1);
			pb.setSource(pi2, 2);
			pi = JAI.create("bandmerge", pb);
			// displayMinMax(pi, "pi:");
			// pi = JAI.create("bandselect", pi, new int[]{2,1,0});

			// double[][] m = { { 1.0d, 0.0d, 0.0d, 0.0d },
			// { 0.0d, 1.0d, 0.0d, 0.0d },
			// { 0.0d, 0.0d, 1.0d, 0.0d }};
			// pi = JAI.create( "bandcombine", new
			// ParameterBlock().addSource(pi).add(m), null);

		}
		if (binarize == 1) {
			// ParameterBlockJAI pbJAI = new ParameterBlockJAI("bandmerge");
			// for(int n = 0; n < 3; n++){
			// pbJAI.setSource(piRatio, n);
			// }
			// pi = JAI.create("bandmerge", pbJAI, null); RGB image with three
			// identical bands

			pi = piRatio; // single band grey
		}

		return pi;
	}

	/**
	 * This method calculates the out put image
	 * 
	 * @param PlanarImage
	 * @param PlanarImage
	 * @param int
	 * @return PlanarImage
	 */
	private PlanarImage calcOutImageForRankImage(PlanarImage piRank,
			PlanarImage pi, int binarize) {

		// PlanarImage pi0 = JAI.create("bandselect", pi, new int[] {0});
		// PlanarImage pi1 = JAI.create("bandselect", pi, new int[] {1});
		// PlanarImage pi2 = JAI.create("bandselect", pi, new int[] {2});

		double[][] m0 = { { 1.0d, 0.0d, 0.0d, 0.0d } };
		double[][] m1 = { { 0.0d, 1.0d, 0.0d, 0.0d } };
		double[][] m2 = { { 0.0d, 0.0d, 1.0d, 0.0d } };
		PlanarImage pi0 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(m0), null);
		PlanarImage pi1 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(m1), null);
		PlanarImage pi2 = JAI.create("bandcombine", new ParameterBlock().addSource(pi).add(m2), null);

		piRank = this.normalizeImage(piRank); // now 0 and 255
		ParameterBlock pb = new ParameterBlock();

		if (binarize == 0) {
			// get boundary image of blobs
			int size = 3 * 3;
			float[] kernel = new float[size];
			Arrays.fill(kernel, 0.0f); // Damit Hintergrund nicht auf 1 gesetzt wird
			KernelJAI kernelJAI = new KernelJAI(3, 3, kernel);

			pb = new ParameterBlock();
			pb.addSource(piRank);
			pb.add(kernelJAI);
			PlanarImage piDil = JAI.create("dilate", pb);

			pb = new ParameterBlock();
			pb.addSource(piDil);
			pb.addSource(piRank);
			@SuppressWarnings("unused")
			PlanarImage piBoundary = JAI.create("subtract", pb);

			// piBoundary = this.normalizeImage(piBoundary); //now 0 and 255

			// pb = new ParameterBlock();
			// pb.addSource(piBoundary);
			// pb.add(DataBuffer.TYPE_BYTE);
			// piBoundary = JAI.create("format", pb);

			pb = new ParameterBlock();
			// pb.addSource(piBoundary);
			pb.addSource(piRank);
			pb.addSource(pi0);
			pi0 = JAI.create("add", pb); // values over 255 are clamped to 255

			pb = new ParameterBlock();
			pb.addSource(pi1);
			// pb.addSource(piBoundary);
			pb.addSource(piRank);
			pi1 = JAI.create("subtract", pb); // negative values are clamped to 0

			pb = new ParameterBlock();
			pb.addSource(pi2);
			// pb.addSource(piBoundary);
			pb.addSource(piRank);
			pi2 = JAI.create("subtract", pb); // negative values are clamped to 0

			pb.removeSources();
			pb.removeParameters();
			pb.setSource(pi0, 0);
			pb.setSource(pi1, 1);
			pb.setSource(pi2, 2);
			pi = JAI.create("bandmerge", pb);
			// displayMinMax(pi, "pi:");
			// pi = JAI.create("bandselect", pi, new int[]{2,1,0});

			// double[][] m = { { 1.0d, 0.0d, 0.0d, 0.0d },
			// { 0.0d, 1.0d, 0.0d, 0.0d },
			// { 0.0d, 0.0d, 1.0d, 0.0d }};
			// pi = JAI.create( "bandcombine", new
			// ParameterBlock().addSource(pi).add(m), null);

		}

		if (binarize == 1) {
			//This converts to an RGB type binary image
//			ParameterBlockJAI pbJAI = new ParameterBlockJAI("bandmerge");
//			for (int n = 0; n < 3; n++) {
//				pbJAI.setSource(piRank, n);
//			}
//			pi = JAI.create("bandmerge", pbJAI, null);
			
			//This is simply an 8-bit binary image;
			//pi = piRank;
			//Does not work, because DataType is not right, no color model no sample model.
			
			
//		    ParameterBlock pbFormat = new ParameterBlock();
//		    pbFormat.addSource(piRank);
//		    pbFormat.add(DataBuffer.TYPE_BYTE);
//          pi = JAI.create("format", pbFormat);
			//This does not work, too, I think because the colormodel and the samplemodel are still null.
			
			int dataType = DataBuffer.TYPE_BYTE;
			int width    = piRank.getWidth();
			int height   = piRank.getHeight();
			SampleModel sm = RasterFactory.createPixelInterleavedSampleModel(dataType, width, height, 1);
			ColorModel cm = PlanarImage.createColorModel(sm);
			TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sm, cm);
			Raster ra = piRank.getData();
			ti.setData(ra);
			pi = ti;
		}

		return pi;
	}

	@Override
	public IResult run(IWorkPackage wp) {

		PlanarImage       piOut       = null;
		PlanarImage       piRatio     = null;
		PlanarImage       piDiffLeft  = null;
		PlanarImage       piDiffRight = null;
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();

		int method = pb.getIntParameter("Method");
		int ratio = pb.getIntParameter("Ratio");

		int rankLeftRG = pb.getIntParameter("RankLeftRG");
		int rankLeftRB = pb.getIntParameter("RankLeftRB");
		int rankLeftGR = pb.getIntParameter("RankLeftGR");
		int rankLeftGB = pb.getIntParameter("RankLeftGB");
		int rankLeftBR = pb.getIntParameter("RankLeftBR");
		int rankLeftBG = pb.getIntParameter("RankLeftBG");
		int and = pb.getIntParameter("AND");
		int rankRightRG = pb.getIntParameter("RankRightRG");
		int rankRightRB = pb.getIntParameter("RankRightRB");
		int rankRightGR = pb.getIntParameter("RankRightGR");
		int rankRightGB = pb.getIntParameter("RankRightGB");
		int rankRightBR = pb.getIntParameter("RankRightBR");
		int rankRightBG = pb.getIntParameter("RankRightBG");

		int binarize = pb.getIntParameter("Binarize");

		// PlanarImage piRatio = this.calcRatioForBand(pi, band, ratio);
		// piOut = this.calcOutImage(piRatio, pi, band, binarize);

		if (method == 0 || method == 1 || method == 2) { // single R,G, or B e.g: R/(R+G+B)
			int band = method;
			piRatio = this.calcRatioForBand(pi, band, ratio);
			piOut = this.calcOutImage(piRatio, pi, band, binarize);
		}

		if (method > 2) { // Rank e.g. R > G
			// int rankLeftBR,
			// int rankLeftBG,
			//
			// int rankRightRG,
			// int rankRightRB,
			// int rankRightGR,
			// int rankRightGB,
			// int rankRightBR,
			// int rankRightBG,
			int sensitivity = ratio;

			// left side
			// create an initial image
			piDiffLeft = JAI.create("bandselect", pi, new int[] { 0 });
			// set image to zero values;
			ParameterBlock pbTmp = new ParameterBlock();
			pbTmp.addSource(piDiffLeft);
			pbTmp.add(new double[] { 0 });
			piDiffLeft = JAI.create("multiplyconst", pbTmp);

			if (rankLeftRG == 1) {
				PlanarImage piDiffLeftNew = this.calcDifferenceForBand(pi, 0, 1, sensitivity);
				piDiffLeft = this.mergeImages(piDiffLeftNew, piDiffLeft);
			}
			if (rankLeftRB == 1) {
				PlanarImage piDiffLeftNew = this.calcDifferenceForBand(pi, 0, 2, sensitivity);
				piDiffLeft = this.mergeImages(piDiffLeftNew, piDiffLeft);
			}
			if (rankLeftGR == 1) {
				PlanarImage piDiffLeftNew = this.calcDifferenceForBand(pi, 1, 0, sensitivity);
				piDiffLeft = this.mergeImages(piDiffLeftNew, piDiffLeft);
			}
			if (rankLeftGB == 1) {
				PlanarImage piDiffLeftNew = this.calcDifferenceForBand(pi, 1, 2, sensitivity);
				piDiffLeft = this.mergeImages(piDiffLeftNew, piDiffLeft);
			}
			if (rankLeftBR == 1) {
				PlanarImage piDiffLeftNew = this.calcDifferenceForBand(pi, 2, 0, sensitivity);
				piDiffLeft = this.mergeImages(piDiffLeftNew, piDiffLeft);
			}
			if (rankLeftBG == 1) {
				PlanarImage piDiffLeftNew = this.calcDifferenceForBand(pi, 2, 1, sensitivity);
				piDiffLeft = this.mergeImages(piDiffLeftNew, piDiffLeft);
			}

			if (and == 1) {
				// right side 
				// create an initial image
				piDiffRight = JAI.create("bandselect", pi, new int[] { 0 });
				// set image to zero values;
				pbTmp = new ParameterBlock();
				pbTmp.addSource(piDiffRight);
				pbTmp.add(new double[] { 0 });
				piDiffRight = JAI.create("multiplyconst", pbTmp);

				if (rankRightRG == 1) {
					PlanarImage piDiffRightNew = this.calcDifferenceForBand(pi, 0, 1, sensitivity);
					piDiffRight = this.mergeImages(piDiffRightNew, piDiffRight);
				}
				if (rankRightRB == 1) {
					PlanarImage piDiffRightNew = this.calcDifferenceForBand(pi, 0, 2, sensitivity);
					piDiffRight = this.mergeImages(piDiffRightNew, piDiffRight);
				}
				if (rankRightGR == 1) {
					PlanarImage piDiffRightNew = this.calcDifferenceForBand(pi, 1, 0, sensitivity);
					piDiffRight = this.mergeImages(piDiffRightNew, piDiffRight);
				}
				if (rankRightGB == 1) {
					PlanarImage piDiffRightNew = this.calcDifferenceForBand(pi, 1, 2, sensitivity);
					piDiffRight = this.mergeImages(piDiffRightNew, piDiffRight);
				}
				if (rankRightBR == 1) {
					PlanarImage piDiffRightNew = this.calcDifferenceForBand(pi, 2, 0, sensitivity);
					piDiffRight = this.mergeImages(piDiffRightNew, piDiffRight);
				}
				if (rankRightBG == 1) {
					PlanarImage piDiffRightNew = this.calcDifferenceForBand(pi, 2, 1, sensitivity);
					piDiffRight = this.mergeImages(piDiffRightNew, piDiffLeft);
				}

				// AND
				pbTmp = new ParameterBlock();
				pbTmp.addSource(piDiffLeft); // only 0 and 1
				pbTmp.addSource(piDiffRight);// only 0 and 1
				// this.displayMinMax(piDiffLeft, "piDiffLeft");
				// this.displayMinMax(piDiffRight, "piDiffright");
				PlanarImage piDiff = JAI.create("multiply", pbTmp);
				
				if (isCancelled(getParentTask())){
					return null;
				}
				
				// this.displayMinMax(piDiff, "piDifft");
				piOut = this.calcOutImageForRankImage(piDiff, pi, binarize);
			} else {
				if (isCancelled(getParentTask())){
					return null;
				}
				piOut = this.calcOutImageForRankImage(piDiffLeft, pi, binarize);
			}
		}

		ImageModel im = new ImageModel(piOut);
		im.setFileName(String.valueOf(pi.getProperty("file_name")));
		im.setModelName(String.valueOf(pi.getProperty("image_name")));

		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpRGBRelativeDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpRGBRelativeDescriptor.TYPE;
	}
}
