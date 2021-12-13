package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracPyramid.java
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
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.jai.BorderExtender;
import javax.media.jai.Histogram;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.TIFFEncodeParam;

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
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracPyramidDescriptor;

/**
 * <li> 2010 03 added grey value image support, Surface is calculated
 *         according to Chinga etal. J Microsc. 227 2007 not sure if it works
 *         properly!!!
 * <li> 2011 01 bug fix for binary images. Condition for filteredsubsample :
 *         resizing previously resized image
 * <li> 2011 01 added 16 bit support
 * <li> 2011 02 added regression data to table
 * <li> 2011 02 removing ModelListener extremely speeds up model
 *         configuration (adding columns, writing data,...)
 * <li> 2011 03 created separate method for estimation of surface area
 *         (Gradient method according to Chinga)
 * <li> 2011 03 added Blanket method for estimation of surface area (Blanket
 *         method according to Tang, Tao, Lam PattRec 2002, 35 1071-1081)
 * <li> 2011 05 added Allometric scaling (variance in dependence on the mean)
 *         according to B. West
 * <li> 2015 03 updated Gradient Method (Chinga) for FD estimation of 
 * 		   grey value surfaces, coefficients of kernels have to include scale
 * <li> 2016 01 updated PGM (Pyramidal Gradient Method) + added PDM, both for FD estimation of 
 * 		   grey value surfaces	
 * <li> 2016 01 added Kolmogorov Complexity
 *         
 * @author Helmut Ahammer, Philipp Kainz, Michael Mayrhofer-R.
 * @since  2009 07
 * @update 2015 06 HA added Information Dimension Option
 * @update 2016 01 MMR added PDM, Update of PGM
 * @update 2016 02 HA added option KC
 */

public class IqmOpFracPyramid extends AbstractOperator {

	/**
	 * Logging variable.
	 */
	private static final Logger logger = LogManager.getLogger(IqmOpFracPyramid.class);
	
	private File logicalDepthDir;
	
	public IqmOpFracPyramid() {
		this.setCancelable(true);
	}
	
	/**
	 * This method is used by the KC method
	 * This method looks for the file (image) and deletes it
	 * @param string
	 */
	private void deleteTempFile(String fileName) {
		File[] files = logicalDepthDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				if (files[i].toString().contains(fileName)) {
					logger.info(fileName + " already exists");
					boolean success = files[i].delete();
					if (success)  logger.info("Successfully deleted " + fileName);
					if (!success) logger.info("Could not delete " + fileName);
				}
			}
		}	
	}

	/**
	 * PGM - Pyramidal Gradients Method (Sobel kernel)
	 * This method calculates the area of a grey value surface based on the
	 * PGM method developed by Michael Mayrhofer-R.
	 * (see Mayrhofer-R., PhD thesis, Med. Univ. Graz, 2015)
	 * using area calculations with gradients (according to Chinga etal. J Microsc. 227 2007) 
	 * 
	 * @param PlanarImage pi;
	 * @output double[];
	 */
	private double[] calculateTotalsGradientMethod(PlanarImage pi, float[] scale) {
		int numBands = pi.getNumBands();
		double[] totalsBands = new double[numBands];
		double[] totalsMax = new double[numBands];

		for (int b = 0; b < numBands; b++) {
			totalsMax[b] = pi.getWidth() * pi.getHeight();
		}
		ParameterBlock pbF = new ParameterBlock();
		pbF.addSource(pi);
		pbF.add(DataBuffer.TYPE_FLOAT);
		PlanarImage piF = JAI.create("Format", pbF, null);

		// stretch grey values:
		int newGreyMax = Math.max(piF.getWidth(),piF.getHeight());
		double imgTypeGreyMax = ImageTools.getImgTypeGreyMax(pi); //Maximum possible grey value for the image
		
		ParameterBlock pbPiFStretched = new ParameterBlock();
		pbPiFStretched.addSource(pi);
		pbPiFStretched.add(new double[] {  newGreyMax/imgTypeGreyMax, newGreyMax/imgTypeGreyMax, newGreyMax/imgTypeGreyMax });
		PlanarImage piFStretched = JAI.create("MultiplyConst", piF, null);
		
		float[] h_data = { 1.0f, 0.0f, -1.0f, 2.0f, 0.0f, -2.0f, 1.0f, 0.0f, -1.0f };
		float[] v_data = { 1.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, -2.0f, -1.0f };
		for (int i = 0; i < h_data.length; i++) {
			h_data[i] = h_data[i] * (1.0f / (8.0f * scale[0]));
		}
		for (int i = 0; i < v_data.length; i++) {
			v_data[i] = v_data[i] * (1.0f / (8.0f * scale[1]));
		}
		KernelJAI kernel_h = new KernelJAI(3, 3, h_data);
		KernelJAI kernel_v = new KernelJAI(3, 3, v_data);
		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));

		ParameterBlock pbGH = new ParameterBlock();
		pbGH.addSource(piFStretched);
		pbGH.add(kernel_h);
		PlanarImage piGH = JAI.create("Convolve", pbGH, rh);

		ParameterBlock pbM = new ParameterBlock();
		pbM.addSource(piGH);
		pbM.addSource(piGH);
		PlanarImage piGH2 = JAI.create("Multiply", pbM, null);

		ParameterBlock pbGV = new ParameterBlock();
		pbGV.addSource(piFStretched);
		pbGV.add(kernel_v);
		PlanarImage piGV = JAI.create("Convolve", pbGV, rh);

		pbM = new ParameterBlock();
		pbM.addSource(piGV);
		pbM.addSource(piGV);
		PlanarImage piGV2 = JAI.create("Multiply", pbM, null);

		ParameterBlock pbA = new ParameterBlock();
		pbA.addSource(piGH2);
		pbA.addSource(piGV2);
		PlanarImage piGM = JAI.create("Add", pbA, null);

		ParameterBlock pbGM = new ParameterBlock();
		pbGM.addSource(piGM);
		pbGM.add(new double[] { 1.0d, 1.0d, 1.0d });
		piGM = JAI.create("AddConst", pbGM, null);

		// calculate square root
		ParameterBlock pbLog = new ParameterBlock();
		pbLog.addSource(piGM);
		PlanarImage piLog = JAI.create("Log", pbLog, null);

		ParameterBlock pbMConst = new ParameterBlock();
		pbMConst.addSource(piLog);
		pbMConst.add(new double[] { 0.5d, 0.5d, 0.5d });
		piLog = JAI.create("MultiplyConst", pbMConst, null);

		ParameterBlock pbEx = new ParameterBlock();
		pbEx.addSource(piLog);
		PlanarImage piEx = JAI.create("Exp", pbEx, null);

		Raster r = piEx.getData();
		
		int minX = r.getMinX();
		int minY = r.getMinY();
		
		for (int x = 1; x < r.getWidth()-1; x++) {
			for (int y = 1; y < r.getHeight()-1; y++) {
				for (int b = 0; b < numBands; b++) {
					totalsBands[b] = totalsBands[b] + r.getSample(minX + x, minY + y, b);
				} // b band
			}
		}
		// totalsBands[b] = totalsBands[0][b] / totalsMax[b];
		return totalsBands;
	}
	
	/**
	 * This method calculates the sum of differences according to the PDM 
	 * (see Mayrhofer-R., PhD thesis, Med. Univ. Graz, 2015) 
	 * 
	 * @param PlanarImage pi;
	 * @output double[];
	 */
	private double[] calculatePDM(PlanarImage pi) {
		int numBands = pi.getNumBands();
		double[] totalsBands = new double[numBands];
		double[] totalsMax = new double[numBands];

		for (int b = 0; b < numBands; b++) {
			totalsMax[b] = pi.getWidth() * pi.getHeight();
		}
		ParameterBlock pbF = new ParameterBlock();
		pbF.addSource(pi);
		pbF.add(DataBuffer.TYPE_FLOAT);
		PlanarImage piF = JAI.create("Format", pbF, null);
		
		Raster r = piF.getData();
		int minX = r.getMinX();
		int minY = r.getMinY();
		for (int x = 0; x < r.getWidth()-1; x++) {
			for (int y = 0; y < r.getHeight()-1; y++) {
				for (int b = 0; b < numBands; b++) {
					totalsBands[b] = totalsBands[b]
							+ Math.abs(r.getSample(minX + x + 1, minY + y, b)
							- r.getSample(minX + x, minY + y, b)) 
							+ Math.abs(r.getSample(minX + x, minY + y + 1, b)
							- r.getSample(minX + x, minY + y, b)); 
				} // b band
			}
		}		
		
		// totalsBands[b] = totalsBands[0][b] / totalsMax[b];
		return totalsBands;
	}
	
	/**
	 * This method estimates the Kolmogorov Complexity by lossless compression
	 * 
	 * @param PlanarImage pi;
	 * @output double[];
	 */
	private double[] calculateKC(PlanarImage pi) {
		int numBands = pi.getNumBands();
		double[] totalsBands = new double[numBands];
		double[] totalsMax = new double[numBands];
	
		//calculate complexities
		double ld         = 9999.9;  //Logical depth
		double ldCorr     = 9999.9;  //corrected logical depth = ld -systemBias
		
		double kc  = 9999.9;  //Kolmogorov complexity (size of compressed image in MB)
	
		//prepare destination for saving and reloading
		File userTempDir = ConfigManager.getCurrentInstance().getTempPath();
	
		String newFolder = userTempDir.toString() + IQMConstants.FILE_SEPARATOR + "LogicalDepth";
		
		//check if new directory is already available
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		File[] files = userTempDir.listFiles(fileFilter);
		boolean found = false;
		logicalDepthDir = null;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				if (files[i].toString().contains("LogicalDepth")) {
					found = true;
					logicalDepthDir = files[i]; 
					logger.info("Directory " + logicalDepthDir.toString() + " already exists");
				}
			}
		}
		
	    fireProgressChanged(1);
        if (isCancelled(getParentTask())){
                return null;
        }    
		
		//create new director if it is not available
		if (!found){
			logicalDepthDir = new File(newFolder);
			boolean success = logicalDepthDir.mkdir();
			if (success) {
				logger.info("Created directory: " + logicalDepthDir.toString());
			} else {
				logger.error("Unable to create directory:  " + logicalDepthDir.toString());
				return null;
			}
		}
		
	    fireProgressChanged(2);
        if (isCancelled(getParentTask())){
                return null;
        }  
		//calculate Reference file if chosen
		double durationReference  = 0.0;
		double megabytesReference = 0.0;
		
	
		double  durationTarget  = 0;
		double megabytesTarget = 0.0;
		File targetFile = null;
			
	    fireProgressChanged(5);
        if (isCancelled(getParentTask())){
                return null;
        }      
        int method;
        method = 0; //PNG compression
        //method = 1; //ZIP
        //method = 2; //LZW  //does not work
        
    	int iterations = 10;
        
		if(method == 0){ //png compression 

			this.deleteTempFile("Temp.png");
			String target    = logicalDepthDir.toString() + IQMConstants.FILE_SEPARATOR + "Temp.png";
			targetFile = new File(target);
					
			// save image using JAI
			JAI.create("filestore", pi, target, "PNG");
//			try {
//				ImageIO.write(pi.getAsBufferedImage(), "PNG", targetFile); level of compression lower ~ zip tif
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
				
			//PlanarImage piTarget = null;		
		
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (int i = 0; i < iterations; i++){    
	            int proz = (int) (i + 1) * 90 / iterations;		
				fireProgressChanged(proz);    
		        if (isCancelled(getParentTask())){
		                return null;
		        }  
				long startTime = System.nanoTime();
				//piTarget = JAI.create("fileload", target);
				//piTarget.getHeight(); //Essential for proper time stamps. Without this, duration times are almost identical for every image and very small
				try {
					BufferedImage bi = ImageIO.read(targetFile);  //faster than JAI
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long time = System.nanoTime();
				stats.addValue((double)(time - startTime));
			}
			//durationTarget = (double)(System.nanoTime() - startTime) / (double)iterations;
		    durationTarget = stats.getPercentile(50); //Median
	
			//piTarget.dispose();		
			
		}

		if(method == 1){ //ZIP
	
			this.deleteTempFile("Temp_ZIP.tif");
			String target    = logicalDepthDir.toString() + IQMConstants.FILE_SEPARATOR + "Temp_ZIP.tif";
			targetFile = new File(target);
					
//			// save image using JAI	//JAI does not support LZW
//			JAI.create("filestore", pi, target, "TIFF");
			
			OutputStream out = null;
			try {
				out = new FileOutputStream(target);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TIFFEncodeParam param = new TIFFEncodeParam();
			param.setWriteTiled(true);
			param.setCompression(TIFFEncodeParam.COMPRESSION_DEFLATE); 
			param.setDeflateLevel(9);
			ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF",  out,  param);
			try {
				encoder.encode(pi);
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}							
			//PlanarImage piTarget = null;		
		
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (int i = 0; i < iterations; i++){
			    int proz = (int) (i + 1) * 90 / iterations;		
				fireProgressChanged(proz);    
			    if (isCancelled(getParentTask())){
			    	return null;
			    }  
				long startTime = System.nanoTime();
				//piTarget = JAI.create("fileload", target);	
				//piTarget.getHeight(); //Essential for proper time stamps. Without this, duration times are almost identical  for every image and very small
				try {
					BufferedImage bi = ImageIO.read(targetFile);  //faster than JAI
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long time = System.nanoTime();
				stats.addValue((double)(time - startTime));
			}
			//durationTarget = (double)(System.nanaoTime() - startTime) / (double)iterations;
		    durationTarget = stats.getPercentile(50); //Median
				
			//piTarget.dispose();	
		}
		
		if(method == 2){ //LZW
			
			this.deleteTempFile("Temp_LZW.tif");
			String target    = logicalDepthDir.toString() + IQMConstants.FILE_SEPARATOR + "Temp_LZW.tif";
			targetFile = new File(target);
					
//			// save image using JAI	//JAI does not support LZW
//			JAI.create("filestore", pi, target, "TIFF");
			
			OutputStream out = null;
			try {
				out = new FileOutputStream(target);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TIFFEncodeParam param = new TIFFEncodeParam();
			param.setWriteTiled(true);
			//param.setCompression(TIFFEncodeParam.COMPRESSION_LZW);
			param.setCompression(TIFFEncodeParam.COMPRESSION_LZW);  //TIFFEncodeParam.COMPRESSION_LZW = 5 DOES NOT WORK!!!!!!!!
			ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF",  out,  param);
			try {
				encoder.encode(pi);
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}							
			//PlanarImage piTarget = null;		
		
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (int i = 0; i < iterations; i++){
			    int proz = (int) (i + 1) * 90 / iterations;		
			    fireProgressChanged(proz);    
			    if (isCancelled(getParentTask())){
			    	return null;
			    }  
				long startTime = System.nanoTime();
				//piTarget = JAI.create("fileload", target);	
				//piTarget.getHeight(); //Essential for proper time stamps. Without this, duration times are almost identical  for every image and very small
				try {
					BufferedImage bi = ImageIO.read(targetFile);  //faster than JAI
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long time = System.nanoTime();
				stats.addValue((double)(time - startTime));
			}
			//durationTarget = (double)(System.nanaoTime() - startTime) / (double)iterations;
		    durationTarget = stats.getPercentile(50); //Median
				
			//piTarget.dispose();	
		}
		
			
		double bytes = targetFile.length();
		double kilobytes = (bytes / 1024);
		megabytesTarget = (kilobytes / 1024);
//		double gigabytes = (megabytes / 1024);
//		double terabytes = (gigabytes / 1024);
//		double petabytes = (terabytes / 1024);
		
		boolean success1 = false;
		if (targetFile.exists()){
			//success1 = targetFile.delete();
			if (success1)  logger.info("Successfully deleted temp target image");
			if (!success1) logger.info("Could not delete temp targetimage");
		}
		
		
		
		ld         = durationTarget;
		ld         = ld         * 1000.0;
		
		logger.info("Iterations: " +iterations+ "   Duration Image: "     + durationTarget +     "   ld: " + ld + "   Duration Reference: " + durationReference + "   ldCorr: " + ldCorr );
		logger.info("megabytesTarget: " +megabytesTarget + "     megabytesReference: " + megabytesReference);
		kc = megabytesTarget;
		
		for (int b = 0; b < numBands; b++) {
			if (b == 0){
				totalsBands[b] = kc;
			} else {
				totalsBands[b] = Double.NaN;
			}
		}
		// totalsBands[b] = totalsBands[0][b] / totalsMax[b];
		return totalsBands;
	}

	// ---------------------------------------------------------------------------------------------------
	/**
	 * This method calculates the area of a grey value surface one step of
	 * Blanket Method (Minkowski dilation for surface) according to Tang, Tao,
	 * Lam. Patt Rec. 2002, 35,1071-1081
	 * 
	 * @param PlanarImage
	 *            pi2;
	 * @output double[];
	 */
	@SuppressWarnings("unused")
	private double[] calculateTotalsBlanketMethod(PlanarImage pi) {
		int width = pi.getWidth();
		int height = pi.getHeight();
		int numBands = pi.getNumBands();
		double[] totalsAreas = new double[numBands];
		double[] totalsMax = new double[numBands];

		KernelJAI kernelJAI = null;
		int kernelSize = 2 * 1 + 1; // m x n = 3x3
		int kernelWidth = kernelSize;
		int kernelHeight = kernelSize;
		int kernelShape = 0;
		if (kernelShape == 0) {
			// kernelJAI = KernelFactory.createRectangle(kernelWidth,
			// kernelHeight);
			int size = kernelSize * kernelSize;
			float[] kernel = new float[size];
			Arrays.fill(kernel, 0.0f); // Damit Hintergrund nicht auf 1 gesetzt wird
			kernelJAI = new KernelJAI(kernelSize, kernelSize, kernel);
		}
		if (kernelShape == 1) {
			// kernelJAI = KernelFactory.createCircle((int)
			// Math.round(kernelRadius[r])); //Size = radius*2 +1
		}
		double[] plusOne = new double[3];
		double[] minusOne = new double[3];
		Arrays.fill(plusOne, 1.0d);
		Arrays.fill(minusOne, -1.0d); // bug fix 2012 01 27 Michael M.
		// Format to float because of negative values
		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		String type = ImageTools.getImgType(pi);
		if (type.equals(IQMConstants.IMAGE_TYPE_RGB))
			pb.add(DataBuffer.TYPE_BYTE);
		if (type.equals(IQMConstants.IMAGE_TYPE_8_BIT))
			pb.add(DataBuffer.TYPE_BYTE);
		if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT))
			pb.add(DataBuffer.TYPE_USHORT);

		PlanarImage piU = JAI.create("Format", pb, rh);
		PlanarImage piB = piU;

		int[] pixel = new int[numBands];
		int eps = 2; // dilation epsilon

		// Calculate image U
		// Calculate image U+1
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piU);
		pb.add(plusOne);
		PlanarImage piUplusOne = JAI.create("AddConst", pb, rh);
		// Calculate dilated image
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piU);
		pb.add(kernelJAI);
		PlanarImage piUDil = JAI.create("dilate", pb, rh);
		// get Max image
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piUplusOne);
		pb.addSource(piUDil);
		piU = JAI.create("Max", pb, rh);

		// Calculate image B
		// Calculate image B-1
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piB);
		pb.add(minusOne);
		PlanarImage piBminusOne = JAI.create("AddConst", pb, rh);
		// Calculate dilated image
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piB);
		pb.add(kernelJAI);
		PlanarImage piBErode = JAI.create("Erode", pb, rh);
		// get Min image
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piBminusOne);
		pb.addSource(piBErode);
		piB = JAI.create("Min", pb, rh);

		// Calculate Volume
		pb.removeSources();
		pb.removeParameters();
		pb.addSource(piU);
		pb.addSource(piB);
		PlanarImage piV = JAI.create("Subtract", pb, rh);

		Raster raster = piV.getData();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				raster.getPixel(x, y, pixel);
				for (int b = 0; b < numBands; b++)
					totalsAreas[b] = totalsAreas[b] + pixel[b];
			}
		}
		for (int b = 0; b < numBands; b++)
			totalsAreas[b] = totalsAreas[b] / (2 * eps);

		return totalsAreas;
	}
	// ---------------------------------------------------------------------------------------------------
	/**
	 * This method calculates the entropy of a grey value surface
	 * 
	 * @param PlanarImage  pi2;
	 * @output double[];
	 */
	//@SuppressWarnings("unused")
	private double[] calculateTotalsEntropyMethod(PlanarImage pi) {
		
		int numBands = pi.getNumBands();
		double[] totalsAreas = new double[numBands];
		
		
		//get Histogram
		double imgTypeGreyMax = ImageTools.getImgTypeGreyMax(pi); //Maximum possible grey value for the image
	    ParameterBlock pbWork = new ParameterBlock();
        pbWork.addSource(pi);
        pbWork.add(null); // ROI
        pbWork.add(1);
        pbWork.add(1); // Sampling
        pbWork.add(new int[]{(int) imgTypeGreyMax + 1}); // Bins
        pbWork.add(new double[]{0.0d});
        pbWork.add(new double[]{imgTypeGreyMax + 1}); // Range for inclusion
   
        // RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
        // BorderExtender.createInstance(BorderExtender.BORDER_COPY));
        RenderedOp rOp = JAI.create("Histogram", pbWork, null);
        Histogram histo = (Histogram) rOp.getProperty("histogram");

		for (int b = 0; b < numBands; b++)
			totalsAreas[b] = histo.getEntropy()[b];

		return totalsAreas;
	}

	
	@SuppressWarnings("unused")
	public IResult run(IWorkPackage wp) {

		PlanarImage piOut = null;

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		int number = pb.getIntParameter("NumPyrImgs");
		int optIntP = pb.getIntParameter("Interpolation");
		int greyMode = pb.getIntParameter("GreyMode");
		int regStart = pb.getIntParameter("RegStart");
		int regEnd = pb.getIntParameter("RegEnd");

		int width = pi.getWidth();
		int height = pi.getHeight();
		boolean optShowPlot = false;
		boolean optDeleteExistingPlot = false;
		if (pb.getIntParameter("ShowPlot") == 1)
			optShowPlot = true;
		if (pb.getIntParameter("DeleteExistingPlot") == 1)
			optDeleteExistingPlot = true;

		int numBands = pi.getData().getNumBands();
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		boolean isBinary = true;
		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));
		// initialize table for Dimension data
		TableModel model = new TableModel("Pyramid Dimension [" + imgName + "]");
		// adding a lot of columns would be very slow due to active model
		// operatorProgressListener
		model.addColumn("FileName");
		model.addColumn("ImageName");
		model.addColumn("Band");
		model.addColumn("RegStart");
		model.addColumn("RegEnd");
		model.addColumn("Interp");
		for (int b = 0; b < pi.getNumBands(); b++) { // every band
			model.addRow(new Object[] { fileName, imgName, b, regStart, regEnd,
					optIntP });
		}
		double[][] totals = new double[number][numBands];
		// double[] totalsMax = new double[numBands]; //for binary images
		double[][] eps = new double[number][numBands];



		// first image
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
		// System.out.println("IqmOpFracPyramid: (int)typeGreyMax: " + (int)typeGreyMax);
		double totalGrey = histogram.getSubTotal(0, 1, (int) typeGreyMax); // without
																			// 0!
		double totalBinary = histogram.getSubTotal(0, (int) typeGreyMax,
				(int) typeGreyMax);
		if (totalBinary == totalGrey) {
			isBinary = true;
			// System.out.println("IqmOpFracPyramid: Binary image = true");
			BoardPanel.appendTextln("IqmOpOpFracPyramid: Binary image detected - Histogram count of object pixels");
		} else {
			isBinary = false;
			// System.out.println("IqmOpFracPyramid: Binary image = false");
			if ((greyMode == 0) || (greyMode == 1)) { // Gradient (PGM) or Blanket
				BoardPanel.appendTextln("IqmOpFracPyramid: Grey image detected - Areas of grey value surfaces");
			}
			else if (greyMode == 2) { // Allometric scaling
				BoardPanel.appendTextln("IqmOpFracPyramid: Grey image detected - Variances and means of grey value surfaces");
			}
			else if (greyMode == 3) { // Information
				BoardPanel.appendTextln("IqmOpFracPyramid: Grey image detected - Information Dimension");
			}
			else if (greyMode == 4) { // PDM
				BoardPanel.appendTextln("IqmOpFracPyramid: Grey image detected - Differences (for PDM) of grey values");
			}
			else if (greyMode == 5) { // KC
				BoardPanel.appendTextln("IqmOpFracPyramid: Grey image detected - Kolmogorov Complexity");
			}
		}

		// definition of eps
		for (int n = 0; n < number; n++) {
			for (int b = 0; b < numBands; b++) {
				if (!isBinary && greyMode == 0) {
					eps[n][b] = 1.0 / Math.pow(2, n);
				}
				else {
					eps[n][b] = 1.0 / Math.pow(2, n); // *width*height (not necessary);		
				
				}
				BoardPanel.appendTextln("IqmOpFracPyramid: n:" + n + " eps:  " + eps[n][b]);
				
				if (isCancelled(getParentTask())){
					return null;
				}
			}
		}		
		
		// totals[0][b] = (double)histo.getSubTotal(b, 1, (int)typeGreyMax);
		// //without 0!
		if (isBinary) {// binary image
			for (int b = 0; b < numBands; b++) {
				totalBinary = histogram.getSubTotal(b, (int) typeGreyMax, (int) typeGreyMax); // [1, (int)typeGreyMax] or  [(int)typeGreyMax, (int)typeGreyMax] both ok totalsMax[b] = totalBinary; //Binary Image
				totals[0][b] = totalBinary; // totalsMax[b]; //Binary Image			
				if (isCancelled(getParentTask())){
					return null;
				}
			}
		} else { // grey value image
			if (greyMode == 0) {
				float[] scale = {1.0f,1.0f};
				totals[0] = this.calculateTotalsGradientMethod(pi,scale);
			}
			else if (greyMode == 1)
				totals[0] = this.calculateTotalsBlanketMethod(pi);
			else if (greyMode == 2) { // Allometric scaling
				//eps[0] = histogram.getMean();
				double[] sd = histogram.getStandardDeviation();
				for (int b = 0; b < histogram.getNumBands(); b++) {
					eps[0][b] = Math.pow(2, 0);
					totals[0][b] = sd[b] * sd[b];
				}
			}
			else if (greyMode ==3) {
				totals[0] = this.calculateTotalsEntropyMethod(pi);
			}
			else if (greyMode ==4) {
				totals[0] = this.calculatePDM(pi);
			}
			else if (greyMode ==5) {
				totals[0] = this.calculateKC(pi);
			}
		} // END for first image

		PlanarImage pi2 = pi; // pi2 will be the size reduced image

		for (int n = 1; n < number; n++) {
			int proz = (n + 1) * 100 / number;
			this.fireProgressChanged(proz);
			
			if (isCancelled(getParentTask())){
				return null;
			}
			
			if ((optIntP == 0) || (optIntP == 1) || (optIntP == 2) || (optIntP == 3)) {
				// condition for filteredsubsample : resize previously resized image
				ParameterBlock pbWork = new ParameterBlock();
				
				 // for Gradient (PGM) and Differences (PDM) Method and KC method: 
				 // "base-pyramid", i.e. layers of pyramid from base layer
				if ((greyMode == 0) || (greyMode == 4) || (greyMode == 5)) {
					pbWork.addSource(pi);
					pbWork.add((int)Math.pow(2,n));// x downsample factor
					pbWork.add((int)Math.pow(2,n));// y downsample factor
				}
				// for others: 
				// "recursive-pyramid", i.e. new layer from previous layer
				else {
					pbWork.addSource(pi2);
					pbWork.add(2);// x downsample factor
					pbWork.add(2);// y downsample factor
				}
							
				// pbWork.add((int)Math.pow(2, n));//x downsample factor
				// pbWork.add((int)Math.pow(2, n));//y downsample factor

				// Gauss filter
				pbWork.add(new float[] { 1.0f }); // for binary images 1.0f
				// pb.add(new float[] {0.0632f, 0.0558f});
				// pb.add(new float[] {0.0632f, 0.0558f, 0.0383f});
				// pb.add(new float[] {0.0117f, 0.0115f, 0.0108f, 0.0098f, 0.0085f, 0.0071f});

				if (optIntP == 0)      pbWork.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
				else if (optIntP == 1) pbWork.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
				else if (optIntP == 2) pbWork.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
				else if (optIntP == 3) pbWork.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2));

				RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender .createInstance(BorderExtender.BORDER_COPY));
				pi2 = JAI.create("filteredsubsample", pbWork, rh);
				// pi2 = JAI.create("scale", pb, rh);
				if (isCancelled(getParentTask())){
					return null;
				}
			}
			if (optIntP == 4) {
				// condition for subsampleaverage : resize always from original image!
				ParameterBlock pbWork = new ParameterBlock();
				pbWork.addSource(pi);
				pbWork.add(1.0 / Math.pow(2, n));// x downsample factor
				pbWork.add(1.0 / Math.pow(2, n));// y downsample factor

				RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
				// rh = null;
				pi2 = JAI.create("subsampleaverage", pbWork, rh);
				// System.out.println("IqmOpFracPyramid: pi2.getWidth "+
				// pi2.getWidth());
				if (isCancelled(getParentTask())){
					return null;
				}
			}
			// totals[n][b] = (double) histo.getSubTotal(b, 1,
			// (int)typeGreyMax-1); only binary
			if (isBinary) { // binary image
				pbHisto.removeSources();
				pbHisto.addSource(pi2); // Source image
				// Perform the histogram operation.
				rOp = JAI.create("Histogram", pbHisto, null);
				// Retrieve the histogram data.
				histogram = (Histogram) rOp.getProperty("histogram");
				for (int b = 0; b < numBands; b++) {
					totals[n][b] = histogram.getSubTotal(b, 1, (int) typeGreyMax); // Binary Image //[1, 255] and not [255, 255] because interpolation introduces grey values other than 255!
					totals[n][b] = totals[n][b]; // / totalsMax[b];
				}
				if (isCancelled(getParentTask())){
					return null;
				}
			} else { // grey value image
				if (greyMode == 0) {
					float[] scale = {(float)Math.pow(2, n), (float) Math.pow(2, n)};
					totals[n] = this.calculateTotalsGradientMethod(pi2, scale);
				}
				else if (greyMode == 1)
					totals[n] = this.calculateTotalsBlanketMethod(pi2);
				else if (greyMode == 2) { // Allometric scaling		
					pbHisto.removeSources();
					pbHisto.addSource(pi2); // Source image
					// Perform the histogram operation.
					rOp = JAI.create("Histogram", pbHisto, null);
					// Retrieve the histogram data.
					histogram = (Histogram) rOp.getProperty("histogram");
					//eps[n] = histogram.getMean();
					
					double[] sd = histogram.getStandardDeviation();
					for (int b = 0; b < histogram.getNumBands(); b++) {
						eps[n][b] = Math.pow(2, n);
						totals[n][b] = sd[b] * sd[b];	
					}
				}
				else if (greyMode ==3) {
					totals[n] = this.calculateTotalsEntropyMethod(pi2);
				}
				else if (greyMode ==4) {
					totals[n] = this.calculatePDM(pi2);
				}
				else if (greyMode ==5) {
					totals[n] = this.calculateKC(pi2);
				}
			}
		}
		// Creating log plot
		double[][] lnTotals = new double[number][numBands];
		double[][] lnEps    = new double[number][numBands];
		for (int n = 0; n < number; n++) {
			for (int b = 0; b < histogram.getNumBands(); b++) {
				if (totals[n][b] == 0) {
					lnTotals[n][b] = Math.log(Float.MIN_VALUE); // damit logarithmus nicht undefiniert ist
				} else {
					lnTotals[n][b] = Math.log(totals[n][b]);
				}
				lnEps[n][b] = Math.log(eps[n][b]);
				BoardPanel.appendTextln("IqmOpFracPyramid: n:" + n + " eps:  " + eps[n][b]);
				// BoardPanel.appendTextln("IqmOpFracPyramid: n:" + n + " lnEps:  "+  lnEps[n][b] );
				BoardPanel.appendTextln("IqmOpFracPyramid: n:" + n + " totals[n][b]: " + totals[n][b]);
			}
		}
		// set table data header
		int numColumns = model.getColumnCount();
		if (isBinary)
			model.addColumn("Dp");
		if (!isBinary) {
			if (greyMode == 0)      model.addColumn("Dp_Gradient (PGM)");
			else if (greyMode == 1) model.addColumn("Dp_Blanket");
			else if (greyMode == 2) model.addColumn("Dp_Allometric");
			else if (greyMode == 3) model.addColumn("Dp_Information");
			else if (greyMode == 4) model.addColumn("Dp_Differences (PDM)");
			else if (greyMode == 5) model.addColumn("Dp_KC");
		}
		model.addColumn("StdDev");
		model.addColumn("r2");
		model.addColumn("adjustet_r2");
		// set table regression data header
		for (int n = 0; n < number; n++)
			model.addColumn("DataX_" + (n + 1));
		for (int n = 0; n < number; n++)
			model.addColumn("DataY_" + (n + 1));

		boolean isLineVisible = false;
		for (int b = 0; b < histogram.getNumBands(); b++) { // mehrere Bands
			// Plot //nur ein Band!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			// double[] dataX = new double[number];
			// double[] dataY = new double[number];
			Vector<Double> dataX = new Vector<Double>();
			Vector<Double> dataY = new Vector<Double>();		
			for (int n = 0; n < number; n++) {
				if (!isBinary && greyMode == 3){
					dataY.add(totals[n][b]);
				} else {
				dataY.add(lnTotals[n][b]);
				}
				dataX.add(lnEps[n][b]);
			}
			// System.out.println("IqmOpFracPyramid: dataY: "+ dataY);
			// System.out.println("IqmOpFracPyramid: dataX: "+ dataX);

			if (optShowPlot) {
				if (isBinary) {
					PlotTools.displayRegressionPlotXY(dataX, dataY,
							isLineVisible, imgName + "_Band" + b,
							"Pyramid Dimension", "ln(1/2^n)", "ln(Count)",
							regStart, regEnd, optDeleteExistingPlot);
				}
				if (!isBinary) {
					if (greyMode == 0) {
						PlotTools.displayRegressionPlotXY(dataX, dataY,
								isLineVisible, imgName + "_Band" + b,
								"Pyramid (Gradient, PGM) Dimension", "ln(1/2^n)", "ln(Area)",
								regStart, regEnd, optDeleteExistingPlot);
					}
					else if (greyMode == 1) {
						PlotTools.displayRegressionPlotXY(dataX, dataY,
								isLineVisible, imgName + "_Band" + b,
								"Pyramid (Blanket) Dimension", "ln(1/2^n)", "ln(Area)",
								regStart, regEnd, optDeleteExistingPlot);
					}
					else if (greyMode == 2) {
						PlotTools.displayRegressionPlotXY(dataX, dataY,
								isLineVisible, imgName + "_Band" + b,
								"Pyramid (Allometric) Dimension", "ln(aggregation)", "ln(variance)",
								regStart, regEnd, optDeleteExistingPlot);
					}
					else if (greyMode == 3) {
						PlotTools.displayRegressionPlotXY(dataX, dataY,
								isLineVisible, imgName + "_Band" + b,
								"Pyramid (Information) Dimension", "ln(1/2^n)", "ln (I)",
								regStart, regEnd, optDeleteExistingPlot);
					}
					else if (greyMode == 4) {
						PlotTools.displayRegressionPlotXY(dataX, dataY,
								isLineVisible, imgName + "_Band" + b,
								"Pyramid (Differences, PDM) Dimension", "ln(1/2^n)", "ln(Summed Differences)",
								regStart, regEnd, optDeleteExistingPlot);
					}
					else if (greyMode == 5) {
						PlotTools.displayRegressionPlotXY(dataX, dataY,
								isLineVisible, imgName + "_Band" + b,
								"Pyramid (KC) Dimension", "ln(1/2^n)", "ln(KC)",
								regStart, regEnd, optDeleteExistingPlot);
					}
				}
			}
			// reverse the vectors
			Collections.reverse(dataX);
			Collections.reverse(dataY);
			double[] p = PlotTools.getLinearRegression(dataX, dataY, regStart, regEnd);
			// double[] p = IqmTools.getLinearRegression(dataX, dataY, regStart,
			// regEnd);

			double dim = 0.0;
			if (isBinary) {
				dim = p[1];
			} else {
				// dim = 3-(p[1]/2);
				if (greyMode == 0) { // PGM (Gradient)
					dim =  p[1];					
				}
				else if (greyMode == 1) { //Blanket
					//dim = 3 - (p[1] / 2);
					dim = p[1] +1; //??????
				}
				else if (greyMode == 2) { // allometric scaling
					dim = 3 - p[1] / 2;// ?????????
				}
				else if (greyMode == 3) { // Information dimension
					dim =  p[1];
				}
				else if (greyMode == 4) { // PDM (Differences)
					dim =  1+p[1];
				}
				else if (greyMode == 5) { // KC
					dim =  p[1];
				}
			}

			// set table data
			model.setValueAt(dim, b, numColumns);
			model.setValueAt(p[3], b, numColumns + 1);
			model.setValueAt(p[4], b, numColumns + 2);
			model.setValueAt(p[5], b, numColumns + 3);

			// set regression data
			for (int n = 0; n < number; n++) {
				// model.setValueAt(lnEps[n][b], b, numColumns+n);
				model.setValueAt(eps[n][b], b, numColumns + 3 + 1 + n);
			}
			for (int n = 0; n < number; n++) {
				// model.setValueAt(lnTotals[n][b], b, numColumns+number+n);
				model.setValueAt(totals[n][b], b, numColumns + number + 3 + 1 + n);
			}
		}

		if (isCancelled(getParentTask())){
			return null;
		}
		
		return new Result(model);
		// return new IqmDataBox(piOut); //Optionally put out an image
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpFracPyramidDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpFracPyramidDescriptor.TYPE;
	}

}// end of class

