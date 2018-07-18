package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpComplexLogDepth.java
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


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

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
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.img.bundle.descriptors.IqmOpComplexLogDepthDescriptor;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.TIFFEncodeParam;

/**
 * Logical depth (Physical depth) is calculated according to:
 * <br>
 * Zenil et al Image Characterization and Classification by Physical Complexity, Complexity 2011 Vol17 No3 26-42   
 * 
 * @author Ahammer
 * @since  2014 01
 * @info   Use System.nanoTime() for calculating/measuring elapsed time 
 * @info   see:  https://blogs.oracle.com/dholmes/entry/inside_the_hotspot_vm_clocks
 * @info   System.currentTimeMillis() is better for absolute time
 * @update 2015 01 HA Time stamps are now correct
 * 					  with the help of an additional dummy command e.g. pi.getHeight()
 * 					  just right after the JAI.create("fileload"  command;
 */
public class IqmOpComplexLogDepth extends AbstractOperator {
	
	/**
	 * Logging variable.
	 */
	private static final Logger logger = Logger.getLogger(IqmOpComplexLogDepth.class);
	
	private File logicalDepthDir;

	public IqmOpComplexLogDepth() {
		this.setCancelable(true);
	}

	@SuppressWarnings("unused")
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		int method           = pb.getIntParameter("Method");
		int iterations       = pb.getIntParameter("Iterations");
		int corrSystemBias   = pb.getIntParameter("CorrSystemBias");

		int width          = pi.getWidth();
		int height         = pi.getHeight();
		int numBands       = pi.getData().getNumBands();
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
	
		String imgName  = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));
		// initialize table for Dimension data
		TableModel model = new TableModel("Logical Depth [" + imgName + "]");
		// adding a lot of columns would be very slow due to active model
		// operatorProgressListener
		model.addColumn("FileName");
		model.addColumn("ImageName");
		model.addColumn(" Pixel #");
		model.addRow(new Object[] { fileName, imgName, width*height });
		
		//calculate complexities
		double ld         = 9999.9;  //Logical depth
		double systemBias = 0.0;     //duration of system without compression
		double ldCorr     = 9999.9;  //corrected logical depth = ld -systemBias
		
		double kc  = 9999.9;  //Kolmogorov complexity (size of compressed image in MB)
		double is  = 9999.9;  //image size of uncopressed image in MB
	
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
		
		if (corrSystemBias == IqmOpComplexLogDepthDescriptor.LOADTIMECORRECTION_YES){
			//Reference Image for correction of loading and system bias;
			String reference = logicalDepthDir.toString() + IQMConstants.FILE_SEPARATOR + "Temp.tif";
			File referenceFile = new File(reference);

			JAI.create("filestore", pi, reference, "TIFF");	
			PlanarImage piReference = null;	
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (int i = 0; i < iterations; i++){
				long startTime = System.nanoTime();
				//piReference = JAI.create("fileload", reference);
				//piReference.getHeight(); //Essential for proper time stamps. Without this, duration times are almost identical for every image  and very small
				try {
					BufferedImage bi = ImageIO.read(referenceFile);  //faster than JAI
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long time = System.nanoTime();
				stats.addValue((double)(time - startTime));
			}
			//durationReference = (double)(System.nanaoTime() - startTime)/iterations;
			durationReference = stats.getPercentile(50); //Median
			
			//get size of reference file
			double bytes = referenceFile.length();
			double kilobytes = (bytes / 1024);
			megabytesReference = (kilobytes / 1024);
			
			//delete Reference file
			boolean success2 = false;
			if (referenceFile.exists()){
				//success2 = referenceFile.delete();
				if (success2)  logger.info("Successfully deleted temp reference image");
				if (!success2) logger.info("Could not delete temp reference image");
			}
		}
	
		double  durationTarget  = 0;
		double megabytesTarget = 0.0;
		File targetFile = null;
		
		
	    fireProgressChanged(5);
        if (isCancelled(getParentTask())){
                return null;
        }  
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNG){

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
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGCRUSH){
			//TODO
		}
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGADVANCEDCOMP){
			//TODO;
		}
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_ZIP){
	
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
		
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_LZW){
			
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
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_JPEG2000){
			//TODO;
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
		
		
		
		ld     = durationTarget;
		systemBias = durationReference; //* megabytesTarget / megabytesReference; //Duration of tiff image with same size as compressed image
		ldCorr = ld - systemBias;
		
		ld         = ld         * 1000.0;
		systemBias = systemBias * 1000.0;
		ldCorr     = ldCorr     * 1000.0;
		
		logger.info("Iterations: " +iterations+ "   Duration Image: "     + durationTarget +     "   ld: " + ld + "   Duration Reference: " + durationReference +  "   systemBias: " + systemBias + "   ldCorr: " + ldCorr );
		logger.info("megabytesTarget: " +megabytesTarget + "     megabytesReference: " + megabytesReference);
		kc = megabytesTarget;
		is = megabytesReference;
		
		int numColumns = model.getColumnCount();
	
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNG)             model.addColumn("LD_PNG[ns]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGCRUSH)        model.addColumn("LD_PNGCrush[ns]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGADVANCEDCOMP) model.addColumn("LD_PNGAdvCOMP[ns]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_ZIP)             model.addColumn("LD_ZIP[ns]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_LZW)             model.addColumn("LD_LWZ[ns]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_JPEG2000)        model.addColumn("LD_J2K[ns]");
		
		model.addColumn("System[ns]");
		
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNG)             model.addColumn("LD_PNG-System[ns]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGCRUSH)        model.addColumn("LD_PNGCrush-System[ns]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGADVANCEDCOMP) model.addColumn("LD_PNGAdvCOMP-System[ns]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_ZIP)             model.addColumn("LD_ZIP-System[ns]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_LZW)             model.addColumn("LD_LWZ-System[ns]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_JPEG2000)        model.addColumn("LD_J2K-System[ns]");
		
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNG)             model.addColumn("KC_PNG[MB]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGCRUSH)        model.addColumn("KC_PNGCrush[MB]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGADVANCEDCOMP) model.addColumn("KC_PNGAdvCOMP[MB]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_ZIP)             model.addColumn("KC_ZIP[MB]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_LZW)             model.addColumn("KC_LWZ[MB]");
		if(method == IqmOpComplexLogDepthDescriptor.COMPRESSION_JPEG2000)        model.addColumn("KC_J2K[MB]");
		
		model.addColumn("ImageSize[MB]");
		model.addColumn("ImageSize-KC[MB] ");
		
		// set table data
		model.setValueAt(ld,         0, numColumns);
		model.setValueAt(systemBias, 0, numColumns + 1);
		model.setValueAt(ldCorr,     0, numColumns + 2);
		model.setValueAt(kc,         0, numColumns + 3);
		model.setValueAt(is,         0, numColumns + 4);
		model.setValueAt(is-kc,      0, numColumns + 5);
		
		
	     
		fireProgressChanged(95);    
		if (isCancelled(getParentTask())){
			return null;
		}
	
		return new Result(model);
		// return new IqmDataBox(piOut); //Optionally put out an image
	}
	
	/**
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

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpComplexLogDepthDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpComplexLogDepthDescriptor.TYPE;
	}

}// end of class

