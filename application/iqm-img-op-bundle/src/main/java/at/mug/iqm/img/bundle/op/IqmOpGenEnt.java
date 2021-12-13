package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpGenEnt.java
 * 
 * $Id: IqmOpGenEnt.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/op/IqmOpGenEnt.java $
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
import java.util.Arrays;
import javax.media.jai.BorderExtender;
import javax.media.jai.Histogram;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import org.apache.commons.math3.special.Gamma;
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
import at.mug.iqm.commons.util.GammaFunction;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpGenEntDescriptor;

/**
 * <li>Generalized Entropies
 * <li>according to a review of Amigó, J.M., Balogh, S.G., Hernández, S., 2018. A Brief Review of Generalized Entropies. Entropy 20, 813. https://doi.org/10.3390/e20110813
 * <li>and to: Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * <li>Renyi, Tsallis, H1,H2,H3 according to Amigo etal.
 * <li>H       according to Amigo etal. 
 * <li>SE      according to Amigo etal. and Tsekouras, G.A.; Tsallis, C. Generalized entropy arising from a distribution of q indices. Phys. Rev. E 2005,
 * <li>SEta    according to Amigo etal. and Anteneodo, C.; Plastino, A.R. Maximum entropy approach to stretched exponential probability distributions. J. Phys. A Math. Gen. 1999, 32, 1089–1098.	
 * <li>SKappa  according to Amigo etal. and Kaniadakis, G. Statistical mechanics in the context of special relativity. Phys. Rev. E 2002, 66, 056125
 * <li>SB      according to Amigo etal. and Curado, E.M.; Nobre, F.D. On the stability of analytic entropic forms. Physica A 2004, 335, 94–106.
 * <li>SBeta   according to Amigo etal. and Shafee, F. Lambert function and a new non-extensive form of entropy. IMA J. Appl. Math. 2007, 72, 785–800.
 * <li>SGamma  according to Amigo etal. and Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S61
 * <li>SNorm   according to Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * <li>SEscort according to Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * 
 * @author Ahammer
 * @since  2018-12-04
 */
public class IqmOpGenEnt extends AbstractOperator {

	public IqmOpGenEnt() {
		this.setCancelable(true);
	}

	/**
	 * This method calculates the number of pixels >0 param PlanarImage pi
	 * return double[]
	 */
	private double[] getNumberOfNonZeroPixels(PlanarImage pi) {
		int numBands = pi.getNumBands();
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		System.out.println("IqmOpGenEnt: typeGreyMax: " + typeGreyMax);

		double[] totalGrey   = new double[numBands];
		double[] totalBinary = new double[numBands];

		// Get total amount of object pixels
		// Set up the parameters for the Histogram object.
		int[] bins = { (int) typeGreyMax + 1, (int) typeGreyMax + 1, (int) typeGreyMax + 1 }; // The number of bins e.g. {256, 256,// 256}
		double[] lows = { 0.0D, 0.0D, 0.0D }; // The low incl.value e.g. {0.0D,// 0.0D, 0.0D}
		double[] highs = { typeGreyMax + 1, typeGreyMax + 1, typeGreyMax + 1 }; // The high excl.value e.g. {256.0D, 256.0D,/ 256.0D}
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
		// System.out.println("IqmOpGenEnt: (int)typeGreyMax: " +
		// (int)typeGreyMax);
		for (int b = 0; b < numBands; b++) {
			totalGrey[b] = histogram.getSubTotal(b, 1, (int) typeGreyMax); // without 0!
			totalBinary[b] = histogram.getSubTotal(b, (int) typeGreyMax, (int) typeGreyMax);
			// System.out.println("IqmOpGenEnt: totalGrey[b]: "+
			// totalGrey[b] + "totalBinary[b]: " + totalBinary[b]);
		}
		return totalGrey; //or totalBinary 
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

		int sE      = pb.getIntParameter("SE");
		int h       = pb.getIntParameter("H");
		int renyi   = pb.getIntParameter("Renyi");
		int tsallis = pb.getIntParameter("Tsallis");
		int sNorm   = pb.getIntParameter("SNorm");
		int sEscort = pb.getIntParameter("SEscort");
		int sEta    = pb.getIntParameter("SEta");
		int sKappa  = pb.getIntParameter("SKappa");
		int sB      = pb.getIntParameter("SB");
		int sBeta   = pb.getIntParameter("SBeta");
		int sGamma  = pb.getIntParameter("SGamma");
	
		int eps   = pb.getIntParameter("Eps"); // epsilon in pixels
		
		int minQ     = pb.getIntParameter("MinQ");
		int maxQ     = pb.getIntParameter("MaxQ");
		double minEta   = pb.getDoubleParameter("MinEta");
		double maxEta   = pb.getDoubleParameter("MaxEta");
		double minKappa = pb.getDoubleParameter("MinKappa");
		double maxKappa = pb.getDoubleParameter("MaxKappa");
		double minB     = pb.getDoubleParameter("MinB");
		double maxB     = pb.getDoubleParameter("MaxB");
		double minBeta  = pb.getDoubleParameter("MinBeta");
		double maxBeta  = pb.getDoubleParameter("MaxBeta");
		double minGamma = pb.getDoubleParameter("MinGamma");
		double maxGamma = pb.getDoubleParameter("MaxGamma");
	
		int gridMethod = pb.getIntParameter("GridMethod"); // 0: Gliding Box,// 1:Alternative method
	
		int  stepQ     = 1;
		double stepEta   = 0.1;
		double stepKappa = 0.1;
		double stepB     = 1.0;
		double stepE     = 0.1;
		double stepBeta  = 0.1;
		double stepGamma = 0.1;
	
		int numQ     = maxQ - minQ + 1;
		int numEta   = (int) ((maxEta - minEta)/stepEta + 1);
		int numKappa = (int) ((maxKappa - minKappa)/stepKappa + 1);
		int numB     = (int) ((maxB - minB)/stepB + 1);
		int numBeta  = (int) ((maxBeta - minBeta)/stepBeta + 1);
		int numGamma = (int) ((maxGamma - minGamma)/stepGamma + 1);
		
		
		int numBands = pi.getData().getNumBands();
		String type = ImageTools.getImgType(pi);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		int width       = pi.getWidth();
		int height      = pi.getHeight();
		String imgName  = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));
		
		String strGridMethod = "?";
		if (gridMethod == 0) strGridMethod = "Gliding box";
		if (gridMethod == 1) strGridMethod = "Raster box";

		// initialize table for Dimension data
		TableModel model = new TableModel("Generalized Entropies [" + imgName + "]");
		// JTable jTable = new JTable(model);
		
		// remove table model listener when adding a lot of columns because otherwise it would be very slow due to active model listener
		// don't forget to activate it at the end!
		// model.removeTableModelListener(jTable);
	
		model.addColumn("FileName");
		model.addColumn("ImageName");
		model.addColumn("Grid method");	
		model.addColumn("Eps");	
		model.addColumn("Band");	
	
		for (int b = 0; b < pi.getNumBands(); b++) { // severalBands
			model.addRow(new Object[] {fileName, imgName, strGridMethod, eps, b });
		}

		// data arrays		
		double[]   genEntSE      = new double[numBands];
		double[]   genEntH1      = new double[numBands];	
		double[]   genEntH2      = new double[numBands];	
		double[]   genEntH3      = new double[numBands];
		double[][] genEntRenyi   = new double[numQ][numBands];
		double[][] genEntTsallis = new double[numQ][numBands];	
		double[][] genEntSNorm   = new double[numQ][numBands];	
		double[][] genEntSEscort = new double[numQ][numBands];	
		double[][] genEntSEta    = new double[numEta][numBands];	
		double[][] genEntSKappa  = new double[numKappa][numBands];	
		double[][] genEntSB      = new double[numB][numBands];	
		double[][] genEntSBeta   = new double[numBeta][numBands];	
		double[][] genEntSGamma  = new double[numGamma][numBands];
		
		
		double[][] probs = null; //Probabilities
		double[]   totalsMax     = new double[numBands];
		
		fireProgressChanged(5);
		if (isCancelled(getParentTask())) return null;
		
		// --------------------------------------------------------------------------------------------------------------
		if (gridMethod == 0) { // 0 gliding mass box counting

			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));

			ParameterBlock pbTmp = new ParameterBlock();
			pbTmp.addSource(pi);
			pbTmp.add(DataBuffer.TYPE_DOUBLE);
			PlanarImage piDouble = JAI.create("Format", pbTmp, rh);

			// create binary with 0 and 1
			pbTmp = new ParameterBlock();
			pbTmp.addSource(piDouble);
			double[] low  = new double[numBands];
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
			
			//int proz = (ee + 1) * 100 / numEps;
			//this.fireProgressChanged(proz);

			if (isCancelled(getParentTask())) {
				return null;
			}

			// JAI Method using convolution
			// int kernelSize = (int)(2*epsWidth+1);
			int kernelSize = (2 * eps + 1);
			int size = kernelSize * kernelSize;
			float[] kernel = new float[size];
			// Arrays.fill(kernel, 0.0f); //Damit Hintergrund nicht auf 1 gesetzt wird bei dilate, ....
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
			
			//compute vector of Pi's for GenEntropies for each band
			probs   = new double[width*height][numBands]; //pi's
			double[] sample = new double[numBands];
			
			int pp = 0;
			for (int x = 0; x < width; x++) { // scroll through image
				for (int y = 0; y < height; y++) {
					ra.getPixel(x, y, sample);
					for (int b = 0; b < numBands; b++) { // several bands	
						totalsMax[b] = totalsMax[b] + sample[b]; // calculate total count for normalization
						probs[pp][b] = sample[b];// -1 ; //-1: subtract point itself			
						//probabilities[pp][b] = sample[b]/size;// -1 ; //-1: subtract point itself			
						//probabilities[pp][b] = sample[b]/(width*height);// -1 ; //-1: subtract point itself			
					}// b
					pp=pp+1;
				}// y
			} // x
			
			// normalization
			for (int p = 0; p < probs.length; p++) {
				for (int b = 0; b < numBands; b++) { // several bands
					probs[p][b] = probs[p][b] / totalsMax[b];
				}
			}
		} // Method = 0 gliding box
		
		
		// --------------------------------------------------------------------------------------------------------------
		if (gridMethod == 1) { // raster mass box counting

			double[] numObjectPixels; // number of pixel >0
			numObjectPixels = this.getNumberOfNonZeroPixels(pi);

			ParameterBlock pbTmp = new ParameterBlock();
			pbTmp.addSource(pi);
			pbTmp.removeParameters();
			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));

			//int proz = (int) (ee + 1) * 100 / numEps;
			//this.fireProgressChanged(proz);

			if (isCancelled(getParentTask())) {
				return null;
			}

			pbTmp.removeParameters();

			if (type.equals(IQMConstants.IMAGE_TYPE_RGB))    pbTmp.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))  pbTmp.add(DataBuffer.TYPE_BYTE);
			if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT)) pbTmp.add(DataBuffer.TYPE_USHORT);
				
			width = eps;
			ImageLayout layout = new ImageLayout();
			layout.setTileWidth((int) width);
			layout.setTileHeight((int) width);
			rh = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
			pi = JAI.create("Format", pbTmp, rh);
	
			Raster[] tileRaster = pi.getTiles();
		
			//vector of Pi's for GenEntropies for each band
			probs   = new double[tileRaster.length][numBands]; //pi's
				
			for (int r = 0; r < tileRaster.length; r++) { // tileRaster[]
				int minX = tileRaster[r].getMinX();
				int minY = tileRaster[r].getMinY();
				int tileWidth  = tileRaster[r].getWidth();  // =imgWidth
				int tileHeight = tileRaster[r].getHeight(); // =imgWidth
				// System.out.println("IqmOpGenEnt: minX: "+minX+ "  minY: " +minY+ "   tileWidth: "+ tileWidth+ "     tileHeight: "+ tileHeight);
				for (int b = 0; b < numBands; b++) {
					double count = 0.0d;
					for (int x = 0; x < tileWidth; x++) {
						for (int y = 0; y < tileHeight; y++) {
							// System.out.println("IqmOpFracBox: b, x, y, " +b+"  " + x+ "  "+y);
							int pixel = tileRaster[r].getSample(minX + x, minY + y, b);
							if (pixel > 0) {
								count = count + 1.0d;
							}
						}
					}
					// count = count/totalBinary[b];
					count = count / numObjectPixels[b]; // normalized mass/ of current box tileRaster[r]
					probs[r][b] = count;// -1 ; //-1: subtract point itself			
					// System.out.println("IqmOpGenEnt: b: "+b+ "   count: "+ count );
					//if (count > 0) {
					//}
				} // b bands
			} // r tileRaster[] for (int q =0; q < numQ; q++){ System.out.println("IqmOpGenEnt:  q: "+ (q+minQ) + "    totals[q][0]: "+ totals[q][0] ); }
				
		} // method 1 raster box counting
			
		// --------------------------------------------------------------------------------------------------------------	
		//xxxxxxxxxxx DO NOT CHANGE THE VARIABLE probabilities[pp][b]!!!! xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		//xxxxxxxxxxx SUBSEQUENT ENTROPIES NEED IT UNCHANGED xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		
		if (sE == 1) {//SE according to Amigo etal. paper	
			for (int b = 0; b < numBands; b++) {
				double sum = 0.0;
				for (int pp = 0; pp < probs.length; pp++) {
					if (probs[pp][b] != 0) {
						sum = sum +  probs[pp][b] * (1.0 - Math.exp((probs[pp][b] - 1.0) / probs[pp][b]) );				
					}
				}
				genEntSE[b] = sum;
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			model.addColumn("SE");	
			for (int b = 0; b < numBands; b++) { //several bands			
					model.setValueAt(genEntSE[b], b, numColumns); // set table data			
			}//bands
		}
		//--------------------------------------------------------------------------------------------------
		if (h == 1) {//H1 according to Amigo etal. paper	
			for (int b = 0; b < numBands; b++) {
				double sum = 0.0;
				for (int pp = 0; pp < probs.length; pp++) {
					if (probs[pp][b] != 0) {
							double pHochp = Math.pow(probs[pp][b], probs[pp][b]);
							genEntH1[b] = genEntH1[b] + (1.0 - pHochp);
							genEntH2[b] = genEntH2[b] + Math.log(2.0-pHochp);
							genEntH3[b] = genEntH3[b] + (probs[pp][b] + Math.log(2.0-pHochp));	
					}
				}
				genEntH2[b] = Math.exp(genEntH2[b]);
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			model.addColumn("H1");	
			for (int b = 0; b < numBands; b++) { //several bands			
					model.setValueAt(genEntH1[b], b, numColumns); // set table data			
			}//bands
			numColumns = model.getColumnCount();
			model.addColumn("H2");	
			for (int b = 0; b < numBands; b++) { //several bands					
					model.setValueAt(genEntH2[b], b, numColumns); // set table data			
			}//bands
			numColumns = model.getColumnCount();
			model.addColumn("H3");	
			for (int b = 0; b < numBands; b++) { //several bands	
				model.setValueAt(genEntH3[b], b, numColumns); // set table data			
			}//bands
			
			int H3OutOfH2 = 0; //only for test purposes
			if (H3OutOfH2 == 1) {
				numColumns = model.getColumnCount();
				model.addColumn("H3OutOfH2");	
				for (int b = 0; b < numBands; b++) { //several bands	
					model.setValueAt(1.0 + Math.log(genEntH2[b]), b, numColumns); // H3 = 1+ln(H2);  set table data			
				}//bands	
			}
		}	
		//--------------------------------------------------------------------------------------------------
		if (renyi == 1) {//Renyi according to Amigo etal. paper	
			for (int b = 0; b < numBands; b++) {
				for (int q = 0; q < numQ; q++) {
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if ((q + minQ) == 1) { //q=1 special case
							if (probs[pp][b] == 0) {// damit logarithmus nicht undefiniert ist;
								sum = sum +  Double.MIN_VALUE*Math.log( Double.MIN_VALUE); //for q=1 Renyi is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
							else {
								sum = sum + probs[pp][b]*Math.log(probs[pp][b]); //for q=1 Renyi is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
						}
						else if (((q + minQ) <=  0 ) && (probs[pp][b] != 0.0)){ //leaving out 0 is essential! and according to Amigo etal. page 2
							sum = sum + Math.pow(probs[pp][b],(q + minQ));	
						}
						else if ( (q + minQ) > 0 ) {
							sum = sum + Math.pow(probs[pp][b],(q + minQ));
						}
					}			
					if ((q + minQ) == 1) { //special case q=1 Renyi is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
						genEntRenyi[q][b] = -sum; 
					}	
					else {
						if (sum == 0) sum = Double.MIN_VALUE; // damit logarithmus nicht undefiniert ist
						genEntRenyi[q][b] = Math.log(sum)/(1.0-(q + minQ));	
					}
				}//q		
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			//data header
			for (int q = 0; q < numQ; q++) {
				model.addColumn("Renyi_q" + (minQ + q));
			}
			for (int b = 0; b < numBands; b++) { //several bands	
				for (int q = 0; q < numQ; q++) {			
					model.setValueAt(genEntRenyi[q][b], b, numColumns + q); // set table data			
				}//q
			}//bands
		}
		//--------------------------------------------------------------------------------------------------
		if (tsallis == 1) {//Tsallis according to Amigo etal. paper
			for (int b = 0; b < numBands; b++) {
				for (int q = 0; q < numQ; q++) {
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if ((q + minQ) == 1) { //q=1 special case
							if (probs[pp][b] == 0) {// damit logarithmus nicht undefiniert ist;
								sum = sum +  Double.MIN_VALUE*Math.log( Double.MIN_VALUE); //for q=1 Renyi is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
							else {
								sum = sum + probs[pp][b]*Math.log(probs[pp][b]); //for q=1 Renyi is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
						}
						else if (((q + minQ) <=  0 ) && (probs[pp][b] != 0.0)) { //leaving out 0 is essential! and according to Amigo etal. page 2
							 sum = sum + Math.pow(probs[pp][b],(q + minQ));	
						}
						else if ( (q + minQ) > 0 ) {
							 sum = sum + Math.pow(probs[pp][b],(q + minQ));	
						}				
					}				
					if ((q + minQ) == 1) { // special case for q=1 Tsallis is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
						genEntTsallis[q][b] = -sum;
					}
					else {
						genEntTsallis[q][b] = (sum-1)/(1.0-(q + minQ));
					}		
				}//q	
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			//data header
			for (int q = 0; q < numQ; q++) {
				model.addColumn("Tsallis_q" + (minQ + q));	
			}
			for (int b = 0; b < numBands; b++) { //several bands	
				for (int q = 0; q < numQ; q++) {			
					model.setValueAt(genEntTsallis[q][b], b, numColumns + q); // set table data			
				}//q
			}//bands
			
			int tsallisOutOfRenyi = 0;  //only for test purposes
			if (tsallisOutOfRenyi == 1) {//Tsallis out of Renyi according to Amigo etal. paper
				numColumns = model.getColumnCount();
				//data header
				for (int q = 0; q < numQ; q++) {
					model.addColumn("TsallisAusReneyi_q" + (minQ + q));
				}
				for (int b = 0; b < numBands; b++) { //several bands	
					for (int q = 0; q < numQ; q++) {					
						double genEnt = 0.0;
						if ((q + minQ) != 1) genEnt = (Math.exp((1-(q + minQ))*genEntRenyi[q][b])-1)/(1-(q + minQ));
						if ((q + minQ) == 1) genEnt = genEntRenyi[q][b];
						model.setValueAt(genEnt, b, numColumns + q); // set table data			
					}//q
				}//bands
			}
		}
		//--------------------------------------------------------------------------------------------------
		if (sNorm == 1) {//SNorm according to Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
			for (int b = 0; b < numBands; b++) {
				for (int q = 0; q < numQ; q++) {
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if ((q + minQ) == 1) { //q=1 special case
							if (probs[pp][b] == 0) {// damit logarithmus nicht undefiniert ist;
								sum = sum +  Double.MIN_VALUE*Math.log( Double.MIN_VALUE); //for q=1 Snorm is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
							else {
								sum = sum + probs[pp][b]*Math.log(probs[pp][b]); //for q=1 Snorm is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
						}
						else if (((q + minQ) <=  0 ) && (probs[pp][b] != 0.0)){ //leaving out 0 is essential! and according to Amigo etal. page 2
							sum = sum + Math.pow(probs[pp][b],(q + minQ));	
						}
						else if ( (q + minQ) > 0 ) {
							sum = sum + Math.pow(probs[pp][b],(q + minQ));
						}
//						else {
//							sum = sum + Math.pow(probabilities[pp][b],(q + minQ));
//						}
					}			
					if ((q + minQ) == 1) { //special case q=1 SNorm is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
						genEntSNorm[q][b] = -sum; 
					}	
					else {
						genEntSNorm[q][b] = (1.0-(1.0/sum))/(1.0-(q + minQ));	
					}
				}//q		
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			//data header
			for (int q = 0; q < numQ; q++) {
				model.addColumn("SNorm_q" + (minQ + q));
			}
			for (int b = 0; b < numBands; b++) { //several bands	
				for (int q = 0; q < numQ; q++) {			
					model.setValueAt(genEntSNorm[q][b], b, numColumns + q); // set table data			
				}//q
			}//bands
		}
		//--------------------------------------------------------------------------------------------------
		if (sEscort == 1) {//SEscort according to Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
			for (int b = 0; b < numBands; b++) {
				for (int q = 0; q < numQ; q++) {
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if ((q + minQ) == 1) { //q=1 special case
							if (probs[pp][b] == 0) {// damit logarithmus nicht undefiniert ist;
								sum = sum +  Double.MIN_VALUE*Math.log( Double.MIN_VALUE); //for q=1 SEscort is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
							else {
								sum = sum + probs[pp][b]*Math.log(probs[pp][b]); //for q=1 SEscort is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
						}
						else if (((q + minQ) <=  0 ) && (probs[pp][b] != 0.0)){ //leaving out 0 is essential! and according to Amigo etal. page 2
							sum = sum + Math.pow(probs[pp][b], 1.0/(q + minQ));	
						}
						else if ( (q + minQ) > 0 ) {
							sum = sum + Math.pow(probs[pp][b], 1.0/(q + minQ));
						}
//						else {
//							sum = sum + Math.pow(probabilities[pp][b], 1.0/(q + minQ));
//						}
					}			
					if ((q + minQ) == 1) { //special case q=1 SEscort is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
						genEntSEscort[q][b] = -sum; 
					}	
					else {
						genEntSEscort[q][b] = (1.0 - Math.pow(sum, -(q+minQ)))/((q + minQ) - 1.0);	
					}
				}//q		
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			//data header
			for (int q = 0; q < numQ; q++) {
				model.addColumn("SEscort_q" + (minQ + q));
			}
			for (int b = 0; b < numBands; b++) { //several bands	
				for (int q = 0; q < numQ; q++) {			
					model.setValueAt(genEntSEscort[q][b], b, numColumns + q); // set table data			
				}//q
			}//bands
		}
		//--------------------------------------------------------------------------------------------------
		if (sEta == 1) { //SEta   according to Amigo etal. and Anteneodo, C.; Plastino, A.R. Maximum entropy approach to stretched exponential probability distributions. J. Phys. A Math. Gen. 1999, 32, 1089–1098.	
			for (int b = 0; b < numBands; b++) {
				for (int n = 0; n < numEta; n++) {
					double eta = minEta + n*stepEta; //SEta is equal to S_BGS (Bolzmann Gibbs Shannon entropy) for eta = 1 
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if (probs[pp][b] != 0){
							//both following methods provide same results (only some minor deviations in the last decimals) 
							//compute incomplete Gamma function using IQM's classes
							double gam1 = GammaFunction.upperIncomplete((eta+1.0)/eta, -Math.log(probs[pp][b]));
							double gam2 = probs[pp][b]*GammaFunction.gamma((eta+1.0)/eta); 
							
							//or compute incomplete Gamma function using Apache's classes
							//double gam1 = Gamma.regularizedGammaQ((eta+1.0)/eta, -Math.log(probabilities[pp][b])) * Math.exp(Gamma.logGamma((eta+1.0)/eta));
							//double gam2 = probabilities[pp][b]*Math.exp(Gamma.logGamma((eta+1.0f)/eta)); 
							sum = sum + gam1 - gam2;	
						}
					}	
					genEntSEta[n][b] = sum;
				}//q		
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			//data header
			for (int n = 0; n < numEta; n++) {	
				model.addColumn("SEta_n" + String.format ("%.1f", minEta + n*stepEta));
			}
			for (int b = 0; b < numBands; b++) { //several bands	
				for (int n = 0; n < numEta; n++) {			
					model.setValueAt(genEntSEta[n][b], b, numColumns + n); // set table data			
				}//q
			}//bands
		}
		//--------------------------------------------------------------------------------------------------
		if (sKappa == 1) { //SKappa according to Amigo etal. and Kaniadakis, G. Statistical mechanics in the context of special relativity. Phys. Rev. E 2002, 66, 056125
			for (int b = 0; b < numBands; b++) {
				for (int k = 0; k < numKappa; k++) {
					double kappa = minKappa + k*stepKappa; //SKappa is equal to S_BGS (Bolzmann Gibbs Shannon entropy) for kappa = 0 
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						if (kappa == 0) { //kappa=0 special case S_BGS (Bolzmann Gibbs Shannon entropy)
							if (probs[pp][b] == 0) {// damit logarithmus nicht undefiniert ist;
								sum = sum +  Double.MIN_VALUE*Math.log( Double.MIN_VALUE); //for k = 0 SKappa is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
							else {
								sum = sum + probs[pp][b]*Math.log(probs[pp][b]); //for k=0 SKappa is equal to S_BGS (Bolzmann Gibbs Shannon entropy)
							}
						}
						else {
							//if (probabilities[pp][b] != 0){			
								sum = sum + (Math.pow(probs[pp][b], 1.0-kappa) - Math.pow(probs[pp][b], 1.0+kappa))/(2.0*kappa);			
							//}
						}
					}
					if (kappa == 0){
						genEntSKappa[k][b] = -sum;
					}
					else {
						genEntSKappa[k][b] = sum;
					}
				}//q		
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			//data header
			for (int k = 0; k < numKappa; k++) {	
				model.addColumn("SKappa_k" + String.format ("%.1f", minKappa + k*stepKappa));
			}
			for (int b = 0; b < numBands; b++) { //several bands	
				for (int k = 0; k < numKappa; k++) {			
					model.setValueAt(genEntSKappa[k][b], b, numColumns + k); // set table data			
				}//k
			}//bands
		}
		//--------------------------------------------------------------------------------------------------
		if (sB == 1) { //SB  according to Amigo etal. and Curado, E.M.; Nobre, F.D. On the stability of analytic entropic forms. Physica A 2004, 335, 94–106.
			for (int b = 0; b < numBands; b++) {
				for (int n = 0; n < numB; n++) {
					double valueB = minB + n*stepB; //SB is equal to S_BGS (Bolzmann Gibbs Shannon entropy) for ????????????????? 
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {
						//if (probabilities[pp][b] != 0){
							sum = sum + (1.0 - Math.exp(-valueB*probs[pp][b]));
						//}
					}	
					genEntSB[n][b] = sum + (Math.exp(-valueB)-1.0); 
				}//q		
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			//data header
			for (int n = 0; n < numB; n++) {	
				model.addColumn("SB_b" + String.format ("%.1f", minB + n*stepB));
			}
			for (int b = 0; b < numBands; b++) { //several bands	
				for (int n = 0; n < numB; n++) {			
					model.setValueAt(genEntSB[n][b], b, numColumns + n); // set table data			
				}//n
			}//bands
		}
		//--------------------------------------------------------------------------------------------------
		if (sBeta == 1) {//SBeta  according to Amigo etal. and Shafee, F. Lambert function and a new non-extensive form of entropy. IMA J. Appl. Math. 2007, 72, 785–800.
			for (int b = 0; b < numBands; b++) {
				for (int n = 0; n < numBeta; n++) {
					double valueBeta = minBeta + n*stepBeta; //SBeta is equal to S_BGS (Bolzmann Gibbs Shannon entropy) for valueBeta = 1; 
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {		
						if (probs[pp][b] != 0.0){ //leaving out 0 
							sum = sum + Math.pow(probs[pp][b],  valueBeta) * Math.log(1.0/probs[pp][b]);
						}
					}
					genEntSBeta[n][b] = sum;					
				}//q		
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			//data header
			for (int n = 0; n < numBeta; n++) {
				model.addColumn("SBeta_b" + String.format ("%.1f", minBeta + n*stepBeta));
			}
			for (int b = 0; b < numBands; b++) { //several bands	
				for (int n = 0; n < numBeta; n++) {			
					model.setValueAt(genEntSBeta[n][b], b, numColumns + n); // set table data			
				}//n
			}//bands
		}
		//--------------------------------------------------------------------------------------------------
		if (sGamma == 1) { //SGamma according to Amigo etal. and Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S61
			for (int b = 0; b < numBands; b++) {
				for (int g = 0; g < numGamma; g++) {
					double valueGamma = minGamma + g*stepGamma; //SGama is equal to S_BGS (Bolzmann Gibbs Shannon entropy) for valueGamma = 1; 
					double sum = 0.0;
					for (int pp = 0; pp < probs.length; pp++) {		
						if (probs[pp][b] != 0.0){ //leaving out 0 
							sum = sum + Math.pow(probs[pp][b],  1.0/valueGamma) * Math.log(1.0/probs[pp][b]);
						}
					}
					genEntSGamma[g][b] = sum;					
				}//q		
			}//band
			
			//set table data
			int numColumns = model.getColumnCount();
			//data header
			for (int g = 0; g < numBeta; g++) {
				model.addColumn("SGamma_g" + String.format ("%.1f", minGamma + g*stepGamma));
			}
			for (int b = 0; b < numBands; b++) { //several bands	
				for (int g = 0; g < numGamma; g++) {			
					model.setValueAt(genEntSGamma[g][b], b, numColumns + g); // set table data			
				}//n
			}//bands
		}
		//---------------------------------------------------------------------------------------------------------------------
		
		// model.addTableModelListener(jTable);
		// model.fireTableStructureChanged(); // this is mandatory because it updates the table

		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(model);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpGenEntDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpGenEntDescriptor.TYPE;
	}
}
