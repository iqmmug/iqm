package at.mug.iqm.plot.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotOpSymbolicAggregation.java
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

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;

import javax.media.jai.BorderExtender;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;

 
 

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracAllomScaleDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpSymbolicAggregationDescriptor;

/**
 * @author Ahammer
 * @since   2014 01
 */
public class PlotOpSymbolicAggregation extends AbstractOperator{
	
	// class specific logger
	  
	
	private String signalString = null; //Symbolic representation of signal
	private String[][] LUMatrix;

	public PlotOpSymbolicAggregation() {
	}
	
	/**
	 * This method calculates the mean of a data series
	 * 
	 * @param data1D
	 * @return Double Mean
	 */
	private Double calcMean(Vector<Double> data1D) {
		double sum = 0;
		for (double d : data1D) {
			sum += d;
		}
		return sum / data1D.size();
	}
	/**
	 * This method calculates the variance of a data series
	 * @param data1D
	 * @return Double Variance
	 */
	@SuppressWarnings("unused")
	private double calcVariance(Vector<Double> data1D){
		double mean = calcMean(data1D);
		double sum = 0;
		for(double d: data1D){
			sum = sum + ((d - mean) * (d - mean));
		}
		return sum/(data1D.size()-1);  //  1/(n-1) is used by histo.getStandardDeviation() too
	}
	/**
	 * This method calculates the variance of a data series
	 * @param data1D, mean
	 * @return Double Variance
	 */
	private double calcVariance(Vector<Double> data1D, double mean){
		double sum = 0;
		for(double d: data1D){
			sum = sum + ((d - mean) * (d - mean));
		}
		return sum/(data1D.size()-1);  //  1/(n-1) is used by histo.getStandardDeviation() too
	}
	/**
	 * This method calculates the standard deviation of a data series
	 * @param data1D
	 * @return Double standard deviation
	 */
	private double calcStandardDeviation(Vector<Double> data1D){
		double mean = this.calcMean(data1D);
		double variance  = this.calcVariance(data1D, mean);
		return Math.sqrt(variance);
	}	
	
	
	@SuppressWarnings("unused")
	@Override
	public IResult run(IWorkPackage wp) {
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlotModel plotModel = ((IqmDataBox) pb.getSource(0)).getPlotModel();
		int aggLength     = pb.getIntParameter("aggLength");		
		int alphabetSize  = pb.getIntParameter("alphabetSize");		
	    int wordLength    = pb.getIntParameter("wordLength");
	    int subWordLength = pb.getIntParameter("subWordLength");
	    int mag           = pb.getIntParameter("mag");
	    int colorModel    = pb.getIntParameter("colorModel");
	 
	    //calculate output image size
	    //int imageSize = ((int) (Math.sqrt((double) alphabetSize))) * subWordLength * mag;
	    		
	    String plotModelName = plotModel.getModelName();
	    
		//new instance is essential
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
	
		//check data length 
		if (signal.size() < 10){
		}
		int numDataPoints = signal.size();
	
	
		this.fireProgressChanged(5);
		
		//normalization to mean = 0 and variance = 1
		double mean   = this.calcMean(signal);
		double stdDev = this.calcStandardDeviation(signal);
		
		for (int i = 0; i < numDataPoints; i++) {
			signal.set(i, (signal.get(i) - mean) / stdDev);
		}
		//System.out.println("PlotOpSymbolicAggregation: mean: " + this.calcMean(signal));
		//System.out.println("PlotOpSymbolicAggregation: stDev: "+ this.calcStandardDeviation(signal));
		//this.fireProgressChanged((i+1)*90/numDataPoints);
		this.fireProgressChanged(10);
		if (this.isCancelled(this.getParentTask())) return null;
		
		//calculate aggregated signal
		int numInt = aggLength;  //number of data points for interpolation				
		Vector<Double> signalAggregated = new Vector<Double>();
		for (int i = 0; i <= signal.size()-numInt; i = i+numInt){
			double sum = 0.0;
			for (int ii = i; ii < i + numInt; ii++){
				sum += signal.get(ii);
			}
			sum = sum/numInt;
			signalAggregated.add(sum);
		}
		
		//convert signal to symbolic representation (symbols of strings a,b,c,d,....)
		//Vector<String> signalSymbols = new Vector<String>();
		this.signalString = new String();
		double[] bpLUT    = this.getBreakpointsLUT(alphabetSize);
		String[] alphabet = this.getAlphabet(alphabetSize);
		for (int i = 0; i < signalAggregated.size(); i++){
			String string =  "a";
			for (int bp = 0; bp < bpLUT.length; bp++){
				if (signalAggregated.get(i) > bpLUT[bp]) string = alphabet[bp+1];	
			}
			this.signalString = this.signalString + string;	
		}
		
		this.fireProgressChanged(20);
		if (this.isCancelled(this.getParentTask())) return null;
		
		SampleModel sm = null;
		WritableRaster wr = null;
		
		//prepare matrices
		if (alphabetSize == 4){		
			//create LookUp Matrix
			LUMatrix = new String[(int) Math.sqrt(alphabetSize)][(int) Math.sqrt(alphabetSize)];
			alphabet = this.getAlphabet(alphabetSize);
			LUMatrix[0][0] = alphabet[0]; //"a"
			LUMatrix[0][1] = alphabet[1]; //"b"
			LUMatrix[1][0] = alphabet[2]; //"c"
			LUMatrix[1][1] = alphabet[3]; //"d"
			
			int n = 1;
			while (n < subWordLength){ //create levels
				String[][] matrix1 = new String[LUMatrix.length][LUMatrix[0].length];
				String[][] matrix2 = new String[LUMatrix.length][LUMatrix[0].length];
				String[][] matrix3 = new String[LUMatrix.length][LUMatrix[0].length];
				String[][] matrix4 = new String[LUMatrix.length][LUMatrix[0].length];
				
				for (int i = 0; i < LUMatrix.length; i++){
					for (int j = 0; j < LUMatrix[0].length; j++){
						matrix1[i][j] = LUMatrix[i][j];
						matrix2[i][j] = LUMatrix[i][j];
						matrix3[i][j] = LUMatrix[i][j];
						matrix4[i][j] = LUMatrix[i][j];
					}
				}	
						
				for (int i = 0; i < LUMatrix.length; i++){
					for (int j = 0; j < LUMatrix[0].length; j++){
						matrix1[i][j] = alphabet[0] + matrix1[i][j];
						matrix2[i][j] = alphabet[1] + matrix2[i][j];
						matrix3[i][j] = alphabet[2] + matrix3[i][j];
						matrix4[i][j] = alphabet[3] + matrix4[i][j];
					}
				}
				LUMatrix = new String[LUMatrix.length * 2][LUMatrix[0].length * 2];
				//paste to four quadrants
				for (int i = 0; i < matrix1.length; i++){
					for (int j = 0; j < matrix1[0].length; j++){
						LUMatrix[i][j] = matrix1[i][j];	
					}
				}
				for (int i = 0; i < matrix2.length; i++){
					for (int j = 0; j < matrix2[0].length; j++){
						LUMatrix[i][j + matrix2[0].length] = matrix2[i][j];	
					}
				}
				for (int i = 0; i < matrix3.length; i++){
					for (int j = 0; j < matrix3[0].length; j++){
						LUMatrix[i + matrix3[0].length][j] = matrix3[i][j];	
					}
				}
				for (int i = 0; i < matrix4.length; i++){
					for (int j = 0; j < matrix4[0].length; j++){
						LUMatrix[i +  matrix4.length][j +  matrix4[0].length] = matrix4[i][j];	
					}
				}
			
				n = n + 1;
			}			
			
			//create new image
			
			
		    int matrixSize = (int) Math.sqrt(alphabetSize) * (int) Math.pow(2, subWordLength-1);
			//System.out.println("PlotOpSymbolicAggregation: matrixSize: " + matrixSize);
			//System.out.println("PlotOpSymbolicAggregation: LUMatrix.length: " + LUMatrix.length);
			
		    if (matrixSize != LUMatrix.length) {
		    	System.out.println("IQM Error: Sizes of matrices do not fit!");
		    }
		    
			sm = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, matrixSize, matrixSize, 1); // bugfix 2011 10 26 sm = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE, imgWidth, imgHeight, 1); 1 banded sample model image too bright (only) on display error
			byte[] byteArr = new byte[matrixSize * matrixSize];
			DataBufferByte db = new DataBufferByte(byteArr, matrixSize * matrixSize);
			wr = RasterFactory.createWritableRaster(sm, db, new Point(0, 0));
			
			//look for matches and count them
			
		} // alphabet = 4
		
		this.fireProgressChanged(60);
		if (this.isCancelled(this.getParentTask())) return null;
		
		//scroll through symbol signal, look for subword matches and count them
		for (int s = 0; s < this.signalString.length() - wordLength; s = s + wordLength ){
			String word = (String) this.signalString.subSequence(s, s + wordLength);
			//look for matches of subwords
			for (int i = 0; i < this.LUMatrix.length; i++){		
				for (int j = 0; j < this.LUMatrix[0].length; j++){
					if (word.contains(LUMatrix[i][j])) wr.setSample(i, j, 0, wr.getSample(i, j, 0) + 1);
				}
			}
		}
		
		this.fireProgressChanged(80);
		if (this.isCancelled(this.getParentTask())) return null;
		
//		//print results
//		for (int i = 0; i < wr.getWidth(); i++){		
//			for (int j = 0; j < wr.getHeight(); j++){
//				System.out.println("PlotOpSymbolicAggregation: result: "+i+"   "+j+"     "+wr.getSample(i, j, 0));
//			}
//		}	
		
		//normalize 
		int max = 0;
		for (int i = 0; i < wr.getWidth(); i++){		
			for (int j = 0; j < wr.getHeight(); j++){
				if (wr.getSample(i, j, 0) > max) max =  wr.getSample(i, j, 0);
			}
		}
		for (int i = 0; i < wr.getWidth(); i++){		
			for (int j = 0; j < wr.getHeight(); j++){
				wr.setSample(i, j, 0, (int)(((double)wr.getSample(i, j, 0)/(double)max) * 255.0));
			}
		}
		
//		//print results
//		for (int i = 0; i < wr.getWidth(); i++){		
//			for (int j = 0; j < wr.getHeight(); j++){
//				System.out.println("PlotOpSymbolicAggregation: result "+i+"   "+j+"     "+wr.getSample(i, j, 0));
//			}
//		}	
		
		//create grey or false color image
		TiledImage ti = null;
		if (colorModel == PlotOpSymbolicAggregationDescriptor.COLORMODEL_GREY) { //8bit
			// create tiled image
			ColorModel cm = PlanarImage.createColorModel(sm); // compatible color model
			ti = new TiledImage(0, 0, wr.getWidth(), wr.getHeight(), 0, 0, sm, cm);
			ti.setData(wr);
		}
		if (colorModel == PlotOpSymbolicAggregationDescriptor.COLORMODEL_COLOR) { //RGB
			 // Create the R,G,B arrays for the false color image ...
			int red[][]   = new int[wr.getWidth()][wr.getHeight()];
			int green[][] = new int[wr.getWidth()][wr.getHeight()];
			int blue[][]  = new int[wr.getWidth()][wr.getHeight()];
			
			float midSlope = (float)(255.0/(192.0 - 64.0));
			float leftSlope = (float)(255.0/64.0);
			float rightSlope = (float)(-255.0/63.0);
			int greyValue;
			int entry = 0;
			for (int y = 0; y < wr.getHeight(); y++) {
				for (int x = 0; x < wr.getWidth(); x++){
					greyValue = wr.getSample(x, y, 0);
					// Now the false color assignment ...
					if ( greyValue < 64 ) {
						red  [x][y] = 0;
						green[x][y] = Math.round(leftSlope*greyValue);
						blue [x][y] = 255;
					}
					else if ( ( greyValue >= 64 ) && ( greyValue < 192 ) ){
						red  [x][y] = Math.round(255+midSlope*(greyValue-192));
						green[x][y] = 255;
						blue [x][y] = Math.round(255-midSlope*(greyValue-64));
					}
					else {
						red  [x][y] = 255;
						green[x][y]= Math.round(255+rightSlope*(greyValue-192));
						blue [x][y] = 0;
					}
				}
			}
			// Now create the false color image ...
			BufferedImage falseColor = new BufferedImage(wr.getWidth(), wr.getHeight(), BufferedImage.TYPE_INT_RGB);
			WritableRaster falseColorRaster = falseColor.getRaster();
			for (int y = 0; y < falseColor.getHeight(); y++) {
				for (int x = 0; x < falseColor.getHeight(); x++) {
					falseColorRaster.setSample(x,y,0,   red[x][y]);
					falseColorRaster.setSample(x,y,1, green[x][y]);
					falseColorRaster.setSample(x,y,2,  blue[x][y]);
				}
			}
			// create tiled image
			sm = falseColorRaster.getSampleModel();
			ColorModel cm = PlanarImage.createColorModel(sm); // compatible color model
			ti = new TiledImage(0, 0, falseColorRaster.getWidth(), falseColorRaster.getHeight(), 0, 0, sm, cm);
			ti.setData(falseColorRaster);
		
		}
			

		//Change float to byte----------------------------------------------------
		ParameterBlock pbFormat = new ParameterBlock();
		pbFormat.addSource(ti);
		pbFormat.add(DataBuffer.TYPE_BYTE);
		PlanarImage piOut = JAI.create("format", pbFormat);
		
		this.fireProgressChanged(90);
		if (this.isCancelled(this.getParentTask())) return null;
		
		//change image size
		ParameterBlock pbScale = new ParameterBlock();
		pbScale.addSource(piOut);
		pbScale.add((float)mag);// x scale factor
		pbScale.add((float)mag);// y scale factor
		pbScale.add(0.0F);// x translate
		pbScale.add(0.0F);// y translate
	
		int optIntP = 0;
		if (optIntP == 0)
			pbScale.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
		if (optIntP == 1)
			pbScale.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
		if (optIntP == 2)
			pbScale.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
		if (optIntP == 3)
			pbScale.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2));

		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		piOut = JAI.create("scale", pbScale, rh);
		

		//System.out.println("IqmCreateImage piOut.getClass()" + piOut.getClass());
		ImageModel im = new ImageModel(piOut);
		im.setModelName("Symbolic aggregated image");
	

		this.fireProgressChanged(95);
		if (this.isCancelled(this.getParentTask())) return null;
		return new Result(im);
	}
	
	/**
	 * This method gets back the alphabet 
	 * @param index
	 * @return character
	 */
	private String[] getAlphabet(int alphabetSize) {
		String[] alphabet = new String[alphabetSize];
		for (int i = 0; i < alphabetSize; i++){
			if (i ==  0) alphabet[0]  = "a";
			if (i ==  1) alphabet[1]  = "b";
			if (i ==  2) alphabet[2]  = "c";
			if (i ==  3) alphabet[3]  = "d";
			if (i ==  4) alphabet[4]  = "e";
			if (i ==  5) alphabet[5]  = "f";
			if (i ==  6) alphabet[6]  = "g";
			if (i ==  7) alphabet[7]  = "h";
			if (i ==  8) alphabet[8]  = "i";
			if (i ==  9) alphabet[9]  = "j";
			if (i == 10) alphabet[10] = "k";
			if (i == 11) alphabet[11] = "l";
			if (i == 12) alphabet[12] = "m";
			if (i == 13) alphabet[13] = "n";
			if (i == 14) alphabet[14] = "o";
			if (i == 15) alphabet[15] = "p";
			if (i == 16) alphabet[16] = "q";
			if (i == 17) alphabet[17] = "r";
			if (i == 18) alphabet[18] = "s";
			if (i == 19) alphabet[19] = "t";
			if (i == 20) alphabet[20] = "u";
			if (i == 21) alphabet[21] = "v";
			if (i == 22) alphabet[22] = "w";
			if (i == 23) alphabet[23] = "x";
			if (i == 24) alphabet[24] = "y";
			if (i == 25) alphabet[25] = "z";		
		}
	
		return alphabet;
	}

	/**
	 * This method gets back a LUT of breakpoints (levels) in order to segment a signal into symbols 
	 * @param alphabetSize
	 * @return a double array
	 */
	private double[] getBreakpointsLUT(int alphabetSize) {
		
		double[] breackpointsLUT = new double[alphabetSize -1];
	
		if (alphabetSize == 2){
			breackpointsLUT[0] = 0.0;
		}
		if (alphabetSize == 3){
			breackpointsLUT[0] = -0.43;
			breackpointsLUT[1] =  0.43;
		}
		if (alphabetSize == 4){
			breackpointsLUT[0] = -0.67;
			breackpointsLUT[1] =  0.0;
			breackpointsLUT[2] =  0.67;
		}
		if (alphabetSize == 5){
			breackpointsLUT[0] = -0.84;
			breackpointsLUT[1] = -0.25;
			breackpointsLUT[2] =  0.25;
			breackpointsLUT[3] =  0.84;
		}
		if (alphabetSize == 6){
			breackpointsLUT[0] = -0.97;
			breackpointsLUT[1] = -0.43;
			breackpointsLUT[2] =  0.0;
			breackpointsLUT[3] =  0.43;
			breackpointsLUT[4] =  0.97;
		}
		if (alphabetSize == 7){
			breackpointsLUT[0] = -1.07;
			breackpointsLUT[1] = -0.57;
			breackpointsLUT[2] = -0.18;
			breackpointsLUT[3] =  0.18;
			breackpointsLUT[4] =  0.57;
			breackpointsLUT[5] =  1.07;
		}
		if (alphabetSize == 8){
			breackpointsLUT[0] = -1.15;
			breackpointsLUT[1] = -0.67;
			breackpointsLUT[2] = -0.32;
			breackpointsLUT[3] =  0.0;
			breackpointsLUT[4] =  0.32;
			breackpointsLUT[5] =  0.67;
			breackpointsLUT[6] =  1.15;
		}
		if (alphabetSize == 9){
			breackpointsLUT[0] = -1.22;
			breackpointsLUT[1] = -0.76;
			breackpointsLUT[2] = -0.43;
			breackpointsLUT[3] = -0.14;
			breackpointsLUT[4] =  0.14;
			breackpointsLUT[5] =  0.43;
			breackpointsLUT[6] =  0.76;
			breackpointsLUT[7] =  1.22;
		}
		if (alphabetSize == 10){
			breackpointsLUT[0] = -1.28;
			breackpointsLUT[1] = -0.84;
			breackpointsLUT[2] = -0.52;
			breackpointsLUT[3] = -0.25;
			breackpointsLUT[4] =  0.0;
			breackpointsLUT[5] =  0.25;
			breackpointsLUT[6] =  0.52;
			breackpointsLUT[7] =  0.84;
			breackpointsLUT[8] =  1.28;
		}
		return breackpointsLUT;
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new PlotOpFracAllomScaleDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return PlotOpFracAllomScaleDescriptor.TYPE;
	}
	
}
