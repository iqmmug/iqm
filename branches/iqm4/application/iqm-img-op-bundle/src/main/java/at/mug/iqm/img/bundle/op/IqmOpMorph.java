package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpMorph.java
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


import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageStatistics;

import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;

import javax.media.jai.BorderExtender;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import at.mug.iqm.api.IQMConstants;
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
import at.mug.iqm.commons.jaitools.KernelFactory;
import at.mug.iqm.commons.util.BlobFinder;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpMorphDescriptor;

/**
 * The Skeleton and the Fill Holes options are carried out by launching ImageJ
 * and by using the corresponding plugins. 
 * http://rsbweb.nih.gov/ij/
 * 
 * erasion of blobs is carried out by using the BlobFinder class
 * by A.Greensted http://www.labbookpages.co.uk
 * 
 * @author Ahammer, Kainz, Kaar
 * @since   2009 04
 * @update Kaar: implemented morphological operations without using JAI, added circular (elliptical) and diamond shapes for kernel
 * @update 2015 05 07 HA:  changed Skeleton and FillHoles to operate on white pixels as foreground
 */
public class IqmOpMorph extends AbstractOperator {    
	public IqmOpMorph() {
		setCancelable(true);
	}
        
        private TiledImage cropBorder(TiledImage ti, int borderX, int borderY, int originalWidth, int originalHeight) {
            ParameterBlock pbCrop = new ParameterBlock();
            pbCrop.addSource(ti);
            pbCrop.add((float) borderX);
            pbCrop.add((float) borderY);
            pbCrop.add((float) originalWidth);
            pbCrop.add((float) originalHeight);

            PlanarImage pi = null;

            try {
                    pi = JAI.create("Crop", pbCrop, null);
            } catch (Exception e) {
                    BoardPanel.appendTextln("IqmOpMorph: Crop error, return original image");
                    pi = ti;
            }
            if (pi.getMinX() != 0 || pi.getMinY() != 0) {
                    ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
                    pbTrans.addSource(pi);
                    pbTrans.setParameter("xTrans", pi.getMinX() * -1.0f);
                    pbTrans.setParameter("yTrans", pi.getMinY() * -1.0f);
                    pi = JAI.create("translate", pbTrans);
            }

            TiledImage tiResult = new TiledImage(0, 0, originalWidth, originalHeight,
                            ti.getTileGridXOffset(), ti.getTileGridYOffset(),
                            ti.getSampleModel(), ti.getColorModel());

            tiResult.setData(pi.getData());

            return tiResult;
        }
        
        private PlanarImage copyBorder(PlanarImage pi, int borderX, int borderY) {
            // settings for border extension
            ParameterBlock pbBrdr = new ParameterBlock();
            pbBrdr.addSource(pi);
            pbBrdr.add(borderX);
            pbBrdr.add(borderX);
            pbBrdr.add(borderY);
            pbBrdr.add(borderY);
            pbBrdr.add(BorderExtender.createInstance(BorderExtender.BORDER_COPY));

            // copy border
            pi = JAI.create("Border", pbBrdr, null);

            if (pi.getMinX() != 0 || pi.getMinY() != 0) {
                    ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
                    pbTrans.addSource(pi);
                    pbTrans.setParameter("xTrans", pi.getMinX() * -1.0f);
                    pbTrans.setParameter("yTrans", pi.getMinY() * -1.0f);
                    pi = JAI.create("translate", pbTrans);
            }
            
            return pi;
        }
        
        private TiledImage dilate(PlanarImage pi, int borderX, int borderY, int[] kernelMarginX, int[] kernelMarginY) {
            int imageWidth = pi.getWidth();
            int imageHeight = pi.getHeight();
            
            // create tiled image
            TiledImage ti = new TiledImage(pi, imageWidth, imageHeight);
            
            for(int band = 0; band < pi.getNumBands(); band++) {
                // copy image data in array
                float[] arr = new float[imageWidth * imageHeight];
                arr = pi.getData().getSamples(0, 0, imageWidth, imageHeight, band, arr);

                // calculate each pixel
                for(int r = 0 + borderY; r < imageHeight - borderY; r++) {
                    for( int c = 0 + borderX; c < imageWidth - borderX; c++) {
                        float max = 0.0f;

                        int kernelIndex = 0;

                        while(kernelIndex < kernelMarginY.length && max != 255.0f) {
                            float data = arr[(imageWidth * (r + kernelMarginY[kernelIndex])) + (c + kernelMarginX[kernelIndex])];

                            if (data > max)
                                max = data;

                            kernelIndex++;
                        }

                        ti.setSample(c, r, band, max);
                    }
                }
            }
            
            return ti;
        }
        
        private TiledImage erode(PlanarImage pi, int borderX, int borderY, int[] kernelMarginX, int[] kernelMarginY) {
            int imageWidth = pi.getWidth();
            int imageHeight = pi.getHeight();
            
            // create tiled image
            TiledImage ti = new TiledImage(pi, imageWidth, imageHeight);
            
            for(int band = 0; band < pi.getNumBands(); band++) {
                // copy image data in array
                float[] arr = new float[imageWidth * imageHeight];
                arr = pi.getData().getSamples(0, 0, imageWidth, imageHeight, band, arr);

                // calculate each pixel
                for(int r = 0 + borderY; r < imageHeight - borderY; r++) {
                    for( int c = 0 + borderX; c < imageWidth - borderX; c++) {
                        float min = 255.0f;

                        int kernelIndex = 0;

                        while(kernelIndex < kernelMarginY.length && min != 0.0f) {
                            float data = arr[(imageWidth * (r + kernelMarginY[kernelIndex])) + (c + kernelMarginX[kernelIndex])];

                            if (data < min)
                                min = data;

                            kernelIndex++;
                        }

                        ti.setSample(c, r, band, min);
                    }
                }
            }
            
            return ti;
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

            // original size of picture
            int originalWidth = pi.getWidth();
            int originalHeight = pi.getHeight();

            // settings
            int morph = pb.getIntParameter("Morph");
            int kernelShape = pb.getIntParameter("KernelShape");
            int kernelWidth = pb.getIntParameter("KernelWidth");
            int kernelHeight = pb.getIntParameter("KernelHeight");
            int times = pb.getIntParameter("Iterations");
            double minBlobSize = pb.getDoubleParameter("MinBlobSize");
            double maxBlobSize = pb.getDoubleParameter("MaxBlobSize");

            String type = ImageTools.getImgType(pi);
            double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
            String imgName = String.valueOf(pi.getProperty("image_name"));
            String fileName = String.valueOf(pi.getProperty("file_name"));

            // required border size
            int borderX = kernelWidth / 2;
            int borderY = kernelHeight / 2;

            fireProgressChanged(5);
            if (isCancelled(getParentTask())){
                    return null;
            }                 

            int[] kernelMarginX = null;
            int[] kernelMarginY = null;

            if (kernelShape == 0) {// create square kernel
                kernelMarginX = new int[kernelWidth * kernelHeight];
                kernelMarginY = new int[kernelWidth * kernelHeight];

                for(int r = 0; r < kernelHeight; r++) {
                    for (int c = 0; c < kernelWidth; c++) {
                        kernelMarginY[r * kernelWidth + c] = r - borderY;
                        kernelMarginX[r * kernelWidth + c] = c - borderX;
                    }
                }
            } else if (kernelShape == 1) {
                if (kernelWidth == kernelHeight) { // create circle kernel
                    
                    // use KernelJAI for creating a circle kernel
                    KernelJAI kernel = KernelFactory.createCircle(borderY);
                    
                    int index = 0;

                    int[] kernelMarginXTemp = new int[kernelWidth * kernelHeight];
                    int[] kernelMarginYTemp = new int[kernelWidth * kernelHeight];

                    for(int r = 0; r < kernelHeight; r++) {
                        for (int c = 0; c < kernelWidth; c++) {
                            float element = kernel.getElement(c, r);

                            if (element == 1.0f) {
                                kernelMarginYTemp[index] = r - borderY;
                                kernelMarginXTemp[index] = c - borderX;

                                index++;
                            }
                        }
                    }
                    
                    kernelMarginX = Arrays.copyOfRange(kernelMarginXTemp, 0, index);
                    kernelMarginY = Arrays.copyOfRange(kernelMarginYTemp, 0, index);
                } else {
                    // create ellipse kernel
                    
                    // detect largest kernel side
                    int largestDim = kernelWidth;
                    
                    if (kernelHeight > largestDim) {
                        largestDim = kernelHeight;
                    }
                    
                    // use KernelJAI for creating a circle kernel
                    KernelJAI kernel = KernelFactory.createCircle(largestDim / 2);
                    
                    SampleModel sm = pi.getSampleModel();
                    ColorModel cm = pi.getColorModel();
 
                    // represent kernel as image
                    TiledImage tiKernel = new TiledImage(0, 0, largestDim, largestDim, 0, 0, sm, cm);
                    
                    // set grey values of the kernel image
                    for(int r = 0; r < largestDim; r++) {
                        for (int c = 0; c < largestDim; c++) {
                            float element = kernel.getElement(c, r) * 255.0f;

                            tiKernel.setSample(c, r, 0, element); 
                        }
                    }
                    
                    // scale tiled image
                    float zoomX = (float) kernelWidth / (float) tiKernel.getWidth();
                    float zoomY = (float) kernelHeight / (float) tiKernel.getHeight();
                    
                    ParameterBlock pbScale = new ParameterBlock();
                    pbScale.addSource(tiKernel);
                    pbScale.add(zoomX);
                    pbScale.add(zoomY);
                    pbScale.add(0.0F);
                    pbScale.add(0.0F);
                    pbScale.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
                    
                    RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY));
                    
                    PlanarImage piScaledKernel = JAI.create("scale", pbScale, rh);
                    
                    // convert scaled image into kernelMargin vectors
                    int index = 0;
                    
                    int[] kernelMarginXTemp = new int[kernelWidth * kernelHeight];
                    int[] kernelMarginYTemp = new int[kernelWidth * kernelHeight];
                    
                    Raster raster = piScaledKernel.getData();
                    
                    for(int r = 0; r < kernelHeight; r++) {
                        for (int c = 0; c < kernelWidth; c++) {
                            float element = raster.getSampleFloat(c, r, 0);
                            
                            if (element == 255.0f) {
                                kernelMarginYTemp[index] = r - borderY;
                                kernelMarginXTemp[index] = c - borderX;

                                index++;
                            }
                        }
                    }
                    
                    kernelMarginX = Arrays.copyOfRange(kernelMarginXTemp, 0, index);
                    kernelMarginY = Arrays.copyOfRange(kernelMarginYTemp, 0, index);
                }                    
            } else if (kernelShape == 2) {// create diamond kernel
                if (kernelWidth == kernelHeight) { 
                    // create diamond kernel (square)
                    int nrOfElements = (kernelWidth * kernelWidth) - (((((kernelWidth / 2) + 1) * (kernelWidth / 2)) / 2) * 4);
                    kernelMarginX = new int[nrOfElements];
                    kernelMarginY = new int[nrOfElements];

                    int nrOfElementsInRow = 1;

                    int index = 0;
                    for (int r = 0; r < kernelWidth; r++) {
                            for (int c = 0; c < kernelWidth; c++) {
                                if ((c >= (kernelWidth - nrOfElementsInRow) / 2) && (c < ((kernelWidth - nrOfElementsInRow) / 2) + nrOfElementsInRow))
                                {
                                    kernelMarginY[index] = r - borderY;
                                    kernelMarginX[index] = c - borderX;
                                    index++;
                                }                                        
                            }

                            if (r < kernelWidth / 2)
                                nrOfElementsInRow = nrOfElementsInRow + 2;
                            else
                                nrOfElementsInRow = nrOfElementsInRow - 2;    
                    }  
                } else {
                    // create diamond kernel (rectangle)
                    
                    // detect largest kernel side
                    int largestDim = kernelWidth;
                    
                    if (kernelHeight > largestDim) {
                        largestDim = kernelHeight;
                    }
                    
                    SampleModel sm = pi.getSampleModel();
                    ColorModel cm = pi.getColorModel();
 
                    // represent kernel as image
                    TiledImage tiKernel = new TiledImage(0, 0, largestDim, largestDim, 0, 0, sm, cm);
                    
                    int nrOfElementsInRow = 1;

                    for (int r = 0; r < largestDim; r++) {
                            for (int c = 0; c < largestDim; c++) {
                                if ((c >= (largestDim - nrOfElementsInRow) / 2) && (c < ((largestDim - nrOfElementsInRow) / 2) + nrOfElementsInRow))
                                {
                                    tiKernel.setSample(c, r, 0, 255.0f);
                                } else
                                {
                                    tiKernel.setSample(c, r, 0, 0.0f);
                                }
                            }

                            if (r < largestDim / 2)
                                nrOfElementsInRow = nrOfElementsInRow + 2;
                            else
                                nrOfElementsInRow = nrOfElementsInRow - 2;    
                    }
                    
                    // scale tiled image
                    float zoomX = (float) kernelWidth / (float) tiKernel.getWidth();
                    float zoomY = (float) kernelHeight / (float) tiKernel.getHeight();
                    
                    ParameterBlock pbScale = new ParameterBlock();
                    pbScale.addSource(tiKernel);
                    pbScale.add(zoomX);
                    pbScale.add(zoomY);
                    pbScale.add(0.0F);
                    pbScale.add(0.0F);
                    pbScale.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
                    
                    RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY));
                    
                    PlanarImage piScaledKernel = JAI.create("scale", pbScale, rh);
                    
                    // convert scaled image into kernelMargin vectors
                    int index = 0;
                    
                    int[] kernelMarginXTemp = new int[kernelWidth * kernelHeight];
                    int[] kernelMarginYTemp = new int[kernelWidth * kernelHeight];
                    
                    Raster raster = piScaledKernel.getData();
                    
                    for(int r = 0; r < kernelHeight; r++) {
                        for (int c = 0; c < kernelWidth; c++) {
                            float element = raster.getSampleFloat(c, r, 0);
                            
                            if (element == 255.0f) {
                                kernelMarginYTemp[index] = r - borderY;
                                kernelMarginXTemp[index] = c - borderX;

                                index++;
                            }
                        }
                    }
                    
                    kernelMarginX = Arrays.copyOfRange(kernelMarginXTemp, 0, index);
                    kernelMarginY = Arrays.copyOfRange(kernelMarginYTemp, 0, index);
                }
            }

            fireProgressChanged(55);
            if (isCancelled(getParentTask())){
                    return null;
            }

            if (morph == 0) { // dilate
                for (int t = 0; t < times; t++) {
                    // copy border
                    pi = copyBorder(pi, borderX, borderY);

                    // dilate
                    TiledImage ti = dilate(pi, borderX, borderY, kernelMarginX, kernelMarginY);

                    // crop border
                    ti = cropBorder(ti, borderX, borderY, originalWidth, originalHeight);
                    pi = ti;
                }

                piOut = pi;
            }
            if (morph == 1) { // erode
                for (int t = 0; t < times; t++) {
                    // copy border
                    pi = copyBorder(pi, borderX, borderY);

                    // erode
                    TiledImage ti = erode(pi, borderX, borderY, kernelMarginX, kernelMarginY);

                    // crop border
                    ti = cropBorder(ti, borderX, borderY, originalWidth, originalHeight);
                    pi = ti;
                }

                piOut = pi;
            }
            if (morph == 2) { // close
                // copy border
                pi = copyBorder(pi, borderX, borderY);

                // dilate and erode
                TiledImage ti = dilate(pi, borderX, borderY, kernelMarginX, kernelMarginY);
                ti = erode(ti, borderX, borderY, kernelMarginX, kernelMarginY);

                // crop border
                ti = cropBorder(ti, borderX, borderY, originalWidth, originalHeight);

                piOut = ti;
            }
            if (morph == 3) { // open
                // copy border
                pi = copyBorder(pi, borderX, borderY);

                // erode and dilate
                TiledImage ti = erode(pi, borderX, borderY, kernelMarginX, kernelMarginY);
                ti = dilate(ti, borderX, borderY, kernelMarginX, kernelMarginY);

                // crop border
                ti = cropBorder(ti, borderX, borderY, originalWidth, originalHeight);

                piOut = ti;
            }

            if (morph == 4) { // this part uses ImageJ's binary sekeletonize
            	
					//invert because ImageJ handles black areas as foreground 2015-05-07	
					ParameterBlock pbInvert = pb.toParameterBlock();
					pbInvert.removeSources();
					pbInvert.addSource(pi);	
					pi = JAI.create("Invert", pbInvert, null);
					
                    // PlanarImage pi = (PlanarImage) pb.getRenderedSource(0);
                    Image image = pi.getAsBufferedImage();
                    int dataType = pi.getSampleModel().getDataType();
                    // new ImagePlus("", pi.getAsBufferedImage()).show();
                    // ImagePlus imp = IJ.getImage();
                    ImagePlus imp = new ImagePlus("", image);
                    if (imp != null
                                    && (imp.getType() == ImagePlus.GRAY8 || imp.getType() == ImagePlus.COLOR_256)) {
                            ImageStatistics stats = imp.getStatistics();
                            if (stats.histogram[0] + stats.histogram[255] != stats.pixelCount) {
                                    BoardPanel.appendTextln("IqmOpMorph: 8-bit binary (black and white only) image required");
                                    return new Result(new IqmDataBox(pi));
                            }
                    }

                    // IJ.run(imp,"Invert","");
                    IJ.run(imp, "Skeletonize", "");
                    // ImageJ instance = new ImageJ();
                    // IJ.run(imp,"Invert","");
                    imp.updateImage();
                    // imp.updateAndDraw();
                    // Image image = imp.getImage();
                    Image image2 = imp.getBufferedImage();
                    pi = JAI.create("AWTImage", image2);
                    ParameterBlock pb2 = new ParameterBlock();
                    pb2.addSource(pi);
                    pb2.add(dataType);
                    piOut = JAI.create("format", pb2); // imp ist zb. bei RGB vom Typ int anstatt byte
                    //piOut.setProperty("image_name", imgName);
                    //piOut.setProperty("file_name", fileName);
                    imp.flush();
                    // wr.setDataElements(minX, minY, pi.getData()); //
                    // wr = (WritableRaster) pi.getData(); gehr hier nicht, da AWT image Tile Info verliert!!!!!!
                    
    				//invert image back
        			pbInvert = pb.toParameterBlock();
        			pbInvert.removeSources();
        			pbInvert.addSource(piOut);
        			piOut = JAI.create("Invert", pbInvert, null);
            }
            if (morph == 5) { // this part uses ImageJ's hole filling
            	
                	//invert because ImageJ handles black areas as foreground 2015-05-07	
					ParameterBlock pbInvert = pb.toParameterBlock();
					pbInvert.removeSources();
					pbInvert.addSource(pi);
					pi = JAI.create("Invert", pbInvert, null);
					
                    // PlanarImage pi = (PlanarImage) pb.getRenderedSource(0);
                    int dataType = pi.getSampleModel().getDataType();
                    // new ImagePlus("", pi.getAsBufferedImage()).show();
                    // ImagePlus imp = IJ.getImage();
                    ImagePlus imp = new ImagePlus("", pi.getAsBufferedImage());
                    if (imp != null
                                    && (imp.getType() == ImagePlus.GRAY8 || imp.getType() == ImagePlus.COLOR_256)) {
                            ImageStatistics stats = imp.getStatistics();
                            if (stats.histogram[0] + stats.histogram[255] != stats.pixelCount) {
                                    BoardPanel.appendTextln("IqmOpMorph: 8-bit binary (black and white only) image required");
                                    return new Result(new IqmDataBox(pi));
                            }
                    }
                    // IJ.run(imp,"Invert","");

                    IJ.run(imp, "Fill Holes", "");
                    // IJ.run(imp,"Invert","");
                    imp.updateImage();
                    // imp.updateAndDraw();
                    // Image image = imp.getImage();
                    Image image = imp.getBufferedImage();
                    pi = JAI.create("AWTImage", image);
                    ParameterBlock pb2 = new ParameterBlock();
                    pb2.addSource(pi);
                    pb2.add(dataType);
                    piOut = JAI.create("format", pb2); // imp ist zb. bei RGB vom Typ int anstatt byte
                    //piOut.setProperty("image_name", imgName);
                    //piOut.setProperty("file_name", fileName);

                    imp.flush();
                    // wr.setDataElements(minX, minY, pi.getData()); //
                    // wr = (WritableRaster) pi.getData(); gehr hier nicht, da AWT image
                    // Tile Info verliert!!!!!!
                    
            		//invert image back
        			pbInvert = pb.toParameterBlock();
        			pbInvert.removeSources();
        			pbInvert.addSource(piOut);
        			piOut = JAI.create("Invert", pbInvert, null);
            }

            if (morph == 6) { // small blob elimination
                ParameterBlock pbMorph = new ParameterBlock();
                    if (type.equals(IQMConstants.IMAGE_TYPE_RGB)) { // RGB
                            double[][] m = { { 1. / 3, 1. / 3, 1. / 3, 0 } };
                            pbMorph = new ParameterBlock();
                            pbMorph.addSource(pi);
                            pbMorph.add(m);
                            pi = JAI.create("bandcombine", pbMorph, null);
                    }
                    if (pi.getNumBands() != 1) {
                            BoardPanel.appendTextln("IqmOpMorph: error");
                            return new Result(new IqmDataBox(pi));
                    }
                    if (type.equals(IQMConstants.IMAGE_TYPE_16_BIT)) { // 16 bit
                            BoardPanel.appendTextln("IqmOpMorph: 8-bit binary (black and white only) image required");
                            return new Result(new IqmDataBox(pi));
                    }

                    int width = pi.getWidth();
                    int height = pi.getHeight();

                    // Get raw image data
                    Raster raster = pi.getData();
                    DataBuffer buffer = raster.getDataBuffer();
                    // TODO PK: implement a conversion/normalization from DataBuffer.TYPE_DOUBLE to 
                    // DataBuffer.TYPE_BYTE in order to avoid errors
                    DataBufferByte byteBuffer = (DataBufferByte) buffer;
                    byte[] srcData = byteBuffer.getData(0);
                    byte[] dstData = new byte[srcData.length * 3];
                    // Create Blob Finder
                    BlobFinder finder = new BlobFinder(width, height);
                    ArrayList<BlobFinder.Blob> blobList = new ArrayList<BlobFinder.Blob>();
                    // detectBlobs(byte[] srcData, byte[] dstData, int minBlobMass, int
                    // maxBlobMass, byte matchVal, List<Blob> blobList)
                    int minBlobMass = new Double(Math.round((double) width * (double) height / 100.0 * minBlobSize)).intValue();
                    int maxBlobMass = new Double(Math.round((double) width * (double) height / 100.0 * maxBlobSize)).intValue();
                    // finder.detectBlobs(srcData, dstData, 0, -1, (byte)255, blobList);
                    System.out.println("IqmOpMorph minBlobMass: " + minBlobMass);
                    System.out.println("IqmOpMorph maxBlobMass: " + maxBlobMass);
                    finder.detectBlobs(srcData, dstData, minBlobMass, maxBlobMass,
                                    (byte) 255, blobList);
                    // List Blobs
                    System.out.printf("Found %d blobs:\n", blobList.size());
                    for (BlobFinder.Blob blob : blobList)
                            System.out.println(blob);
                    // Create outputImage (colored blobs)
                    DataBufferByte dataBuffer = new DataBufferByte(dstData, dstData.length);
                    int[] colOrder = new int[] { 2, 1, 0 };
                    SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 3, 3 * width, colOrder);
                    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                    ComponentColorModel colourModel = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
                    WritableRaster wr = Raster.createWritableRaster(sampleModel, dataBuffer, new Point(0, 0));
                    BufferedImage bi = new BufferedImage(colourModel, wr, false, null);
                    piOut = PlanarImage.wrapRenderedImage(bi);

                    // create a single banded grey image
                    double[][] m = { { 1. / 3, 1. / 3, 1. / 3, 0 } };
                    pbMorph = new ParameterBlock();
                    pbMorph.addSource(piOut);
                    pbMorph.add(m);
                    piOut = JAI.create("bandcombine", pbMorph, null);

                    // binarize
                    double low[] = { 1 };
                    double high[] = { 255 };
                    double map[] = { 255 };
                    pbMorph = new ParameterBlock();
                    pbMorph.addSource(piOut).add(low).add(high).add(map);
                    piOut = JAI.create("threshold", pbMorph, null);



            }

            piOut.setProperty("image_name", imgName);
            piOut.setProperty("file_name", fileName);

            ImageModel im = new ImageModel(piOut);
            im.setModelName(imgName);
            im.setFileName(fileName);

            fireProgressChanged(95);
            if (isCancelled(getParentTask())){
                    return null;
            }
            
            return new Result(im);
	}
	
	@Override
	public String getName() {
            if (this.name == null){
                    this.name = new IqmOpMorphDescriptor().getName();
            }
            return this.name;
	}
	
		@Override
	public OperatorType getType() {
            return IqmOpMorphDescriptor.TYPE;
	}
}
