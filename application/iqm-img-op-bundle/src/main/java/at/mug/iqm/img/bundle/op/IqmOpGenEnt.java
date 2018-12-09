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
import at.mug.iqm.img.bundle.descriptors.IqmOpGenEntDescriptor;

/**
 * <li>Generalized Entropies
 * <li>according to a review of Amigó, J.M., Balogh, S.G., Hernández, S., 2018. A Brief Review of Generalized Entropies. Entropy 20, 813. https://doi.org/10.3390/e20110813
 * <li>and to: Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
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

		double[] totalGrey = new double[numBands];
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

		
		int renyi   = pb.getIntParameter("Renyi");
		int tsallis = pb.getIntParameter("Tsallis");
		int h1      = pb.getIntParameter("H1");
		int h2      = pb.getIntParameter("H2");
		int h3      = pb.getIntParameter("H3");
		int sEta    = pb.getIntParameter("SEta");
		int sKappa  = pb.getIntParameter("SKappa");
		int sB      = pb.getIntParameter("SB");
		int sE      = pb.getIntParameter("SE");
		int sBeta   = pb.getIntParameter("SBeta");
		int sGamma  = pb.getIntParameter("SGamma");
		
		int minQ   = pb.getIntParameter("MinQ");
		int maxQ   = pb.getIntParameter("MaxQ");
		int maxEps = pb.getIntParameter("MaxEps"); // maximal eps in pixels
		
		float paramEta   = pb.getFloatParameter("ParamEta");
		float paramKappa = pb.getFloatParameter("ParamKappa");
		float paramB     = pb.getFloatParameter("ParamB");
		float paramBeta  = pb.getFloatParameter("ParamBeta");
		float paramGamma = pb.getFloatParameter("ParamGamma");
	
		int method = pb.getIntParameter("GridMethod"); // 0: Gliding Box,// 1:Alternative method
	
		int numQ = maxQ - minQ + 1;
		
		int numBands = pi.getData().getNumBands();
		String type = ImageTools.getImgType(pi);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		int width       = pi.getWidth();
		int height      = pi.getHeight();
		String imgName  = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		// initialize table for Dimension data
		TableModel model = new TableModel("Generalized Entropies [" + imgName + "]");
		// JTable jTable = new JTable(model);
		// adding a lot of columns would be very slow due to active model listener
		// model.removeTableModelListener(jTable);
		model.addColumn("FileName");
		model.addColumn("ImageName");
		model.addColumn("Band");	
		for (int b = 0; b < pi.getNumBands(); b++) { // mehrere Bands
			model.addRow(new Object[] {fileName, imgName, b});
		}

//		// data arrays
//		double[][] totals    = new double[numQ][numBands];
//		double[]   totalsMax = new double[numBands];
				
		double[][] genEntRenyi   = new double[numQ][numBands];
		double[][] genEntTsallis = new double[numQ][numBands];	
		double[][] genEntH1      = new double[numQ][numBands];	
		double[][] genEntH2      = new double[numQ][numBands];	
		double[][] genEntH3      = new double[numQ][numBands];	
		double[][] genEntSEta    = new double[numQ][numBands];	
		double[][] genEntSKappa  = new double[numQ][numBands];	
		double[][] genEntSB      = new double[numQ][numBands];	
		double[][] genEntSE      = new double[numQ][numBands];	
		double[][] genEntSBeta   = new double[numQ][numBands];	
		double[][] genEntSGamma  = new double[numQ][numBands];	
		
		double[][] probabilities = null;
		
		// --------------------------------------------------------------------------------------------------------------
		if (method == 0) { // 0 gliding mass box counting

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
			int kernelSize = (2 * maxEps + 1);
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
			probabilities   = new double[width*height][numBands]; //pi's
			double[] sample = new double[numBands];
			
			int pp = 0;
			for (int x = 0; x < width; x++) { // scroll through image
				for (int y = 0; y < height; y++) {
					ra.getPixel(x, y, sample);
					for (int b = 0; b < numBands; b++) { // several bands	
						probabilities[pp][b] = sample[b]/size;// -1 ; //-1: subtract point itself							
					}// b
					pp=pp+1;
				}// y
			} // x

		} // Method = 0 gliding box
		
		
		// --------------------------------------------------------------------------------------------------------------
		if (method == 1) { // raster mass box counting

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
				
			width = maxEps;
			ImageLayout layout = new ImageLayout();
			layout.setTileWidth((int) width);
			layout.setTileHeight((int) width);
			rh = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
			pi = JAI.create("Format", pbTmp, rh);
	
			Raster[] tileRaster = pi.getTiles();
		
			//vector of Pi's for GenEntropies for each band
			probabilities   = new double[tileRaster.length][numBands]; //pi's
				
			int pp = 0;
			for (int r = 0; r < tileRaster.length; r++) { // tileRaster[]
				int minX = tileRaster[r].getMinX();
				int minY = tileRaster[r].getMinY();
				int tileWidth = tileRaster[r].getWidth(); // ==imgWidth
				int tileHeight = tileRaster[r].getHeight(); // ==imgWidth
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
					probabilities[pp][b] = count;// -1 ; //-1: subtract point itself			
					// System.out.println("IqmOpGenEnt: b: "+b+ "   count: "+ count );
					//if (count > 0) {
					//}
				} // b bands
			} // r tileRaster[] for (int q =0; q < numQ; q++){ System.out.println("IqmOpGenEnt:  q: "+ (q+minQ) + "    totals[q][0]: "+ totals[q][0] ); }
			pp= pp +1;	
			
			
		} // method 1 raster box counting
			
		// --------------------------------------------------------------------------------------------------------------	
		if (renyi == 1) {//Renyi	
			for (int b = 0; b < numBands; b++) {
				for (int q = 0; q < numQ; q++) {
					double sum = 0.0;
					for (int pp = 0; pp < probabilities.length; pp++) {
						if ((q + minQ) != 1) sum = sum + Math.pow(probabilities[pp][b],(q + minQ));			
						if ((q + minQ) == 1) { //q=1 special case
							if (probabilities[pp][b] == 0) probabilities[pp][b] = Double.MIN_VALUE; // damit logarithmus nicht undefiniert ist;
							sum = sum + probabilities[pp][b]*Math.log(probabilities[pp][b]);
						}
					}
					if (sum == 0) sum = Double.MIN_VALUE; // damit logarithmus nicht undefiniert ist
					if ((q + minQ) != 1) genEntRenyi[q][b] = Math.log(sum)/(1.0-(q + minQ));
					if ((q + minQ) == 1) genEntRenyi[q][b] = -sum;
				}//q		
			}//band
		}
		if (tsallis == 1) {//Tsallis	
			for (int b = 0; b < numBands; b++) {
				for (int q = 0; q < numQ; q++) {
					double sum = 0.0;
					for (int pp = 0; pp < probabilities.length; pp++) {
						if ((q + minQ) != 1) sum = sum + Math.pow(probabilities[pp][b],(q + minQ));			
						if ((q + minQ) == 1) { //q=1 special case
							if ((q + minQ) == 1) { //q=1 special case
								if (probabilities[pp][b] == 0) probabilities[pp][b] = Double.MIN_VALUE; // damit logarithmus nicht undefiniert ist;
								sum = sum + probabilities[pp][b]*Math.log(probabilities[pp][b]);
							}
						}
					}
					if ((q + minQ) != 1) genEntTsallis[q][b] = (sum-1.0)/(1.0-(q + minQ));
					if ((q + minQ) == 1) genEntTsallis[q][b] = -sum;
				}//q
				;		
			}//band
		}
		//---------------------------------------------------------------------------------------------------------------------
		//set table data
		if (renyi == 1) {//Renyi	
			int numColumns = model.getColumnCount();
			//data header
			for (int q = 0; q < numQ; q++)   model.addColumn("Renyi_q" + (minQ + q));	
			for (int b = 0; b < numBands; b++) { //several bands			
				for (int q = 0; q < numQ; q++) {		
					model.setValueAt(genEntRenyi[q][b], b, numColumns + q); // set table data			
				}
			}// bands
		}
		if (tsallis == 1) {//Tsallis
			int numColumns = model.getColumnCount();
			//data header
			for (int q = 0; q < numQ; q++)   model.addColumn("Tsallis_q" + (minQ + q));	
			for (int b = 0; b < numBands; b++) { //several bands			
				for (int q = 0; q < numQ; q++) {		
					model.setValueAt(genEntTsallis[q][b], b, numColumns + q); // set table data			
				}
			}// bands
		}
		
		if (tsallis == 1) {//Tsallis out of Renyi
			int numColumns = model.getColumnCount();
			//data header
			for (int q = 0; q < numQ; q++)   model.addColumn("TsallisAusReneyi_q" + (minQ + q));	
			for (int b = 0; b < numBands; b++) { //several bands			
				for (int q = 0; q < numQ; q++) {
					double genEnt = 0.0;
					if ((q + minQ) != 1) genEnt = (Math.exp((1-(q + minQ))*genEntRenyi[q][b])-1)/(1-(q + minQ));
					if ((q + minQ) == 1) genEnt = genEntRenyi[q][b];
					model.setValueAt(genEnt, b, numColumns + q); // set table data			
				}
			}// bands
		}
		
		// model.addTableModelListener(jTable);
		// model.fireTableStructureChanged(); // this is mandatory because it
		// updates the table

		// jTable.getColumnModel().getColumn(2).setPreferredWidth(30); // Band
		// jTable.getColumnModel().getColumn(3).setPreferredWidth(30); // RegStart
		// jTable.getColumnModel().getColumn(4).setPreferredWidth(30); // RegEnd

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
