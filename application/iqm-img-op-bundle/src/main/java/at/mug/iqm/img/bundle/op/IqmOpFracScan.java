package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracScan.java
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
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferDouble;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.BorderExtender;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;

 
 

import at.mug.iqm.api.Application;
import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorFactory;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.api.operator.WorkPackage;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.api.processing.IExplicitProcessingTask;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpCropDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracBoxDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracFFTDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracPyramidDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracScanDescriptor;

/**
 * Extension of nexel scan.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * 
 */
@SuppressWarnings("unused")
public class IqmOpFracScan extends AbstractOperator {
	// class specific logger
	  

	private PlanarImage piSource;
	private String imgName = "";
	private String fileName = "";
	private double typeGrayMax = 255.0; // default

	public IqmOpFracScan() {
	}

	@Override
	public IResult run(IWorkPackage wp) {

		System.out.println("IQM:  Running the operator...");

		PlanarImage piOut = null;
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		this.piSource = ((IqmDataBox) pb.getSource(0)).getImage(); // input
																	// image

		int scanType = pb.getIntParameter("ScanType"); // 0 Box scan 1 Nexel
														// scan
		int boxSize = pb.getIntParameter("BoxSize");
		int kernelShape = pb.getIntParameter("KernelShape");
		int kernelSize = pb.getIntParameter("KernelSize");
		int eps = pb.getIntParameter("Eps");
		int method = pb.getIntParameter("Method"); // //0 Box, 1 Pyramid, 2
													// Minkowski, 3 FFT
		int resultOptions = pb.getIntParameter("ResultOptions");// 0 clamp to
																// byte , 1
																// normalize
																// to byte,
																// 2 actual

		typeGrayMax = ImageTools.getImgTypeGreyMax(this.piSource);
		// check that kernelSize is odd
		// kernelSize = (kernelSize%2==0)?kernelSize+1:kernelSize;

		// select if JAI-Histogram should be used for calculation (=0 -
		// calcStatisticsForPx uses ImageJAI) or
		// if custom calculation for parameters should be used (=1 -
		// customStatisticsForPx uses custom Array)

		int numBands = this.piSource.getData().getNumBands();
		this.imgName = String.valueOf(this.piSource.getProperty("image_name"));
		this.fileName = String.valueOf(this.piSource.getProperty("file_name"));
		System.out.println("IQM:  Source image properties: " + this.imgName + " -- "
				+ this.fileName);

		// for nexel scan is a border necessary
		// how the border should be calculated: 0 (ZERO), 1 (CONST), 2 (COPY), 3
		// (REFLECT) or 4 (WRAP)
		int borderMethod = 2; // TODO: GUI - selection
		double constBorderValue = 127; // TODO: GUI for constant Bordervalue
										// input

		// value for CONST-resized-Border
		double[] borderValue = new double[numBands];
		for (int i = 0; i < numBands; i++) {
			borderValue[i] = constBorderValue;
		}

		int width = this.piSource.getWidth();
		int height = this.piSource.getHeight();
		SampleModel sm = RasterFactory.createPixelInterleavedSampleModel(
				DataBuffer.TYPE_DOUBLE, width, height, 1);
		double[] doubleArr = new double[width * height];
		DataBufferDouble db = new DataBufferDouble(doubleArr, width * height);
		WritableRaster wr = RasterFactory.createWritableRaster(sm, db,
				new Point(0, 0));

		if (scanType == 0) {
			this.boxScan(wr, this.piSource, kernelSize, method, eps, boxSize); // Box
																				// scan
		}
		if (scanType == 1) {
			// add border to pi and get piWithBorder for calculating parameters
			PlanarImage piWithBorder = addBorder(this.piSource, kernelSize,
					borderMethod, borderValue);
			// copy image properties
			piWithBorder = ImageTools.copyCustomImageProperties(this.piSource,
					piWithBorder);
			this.nexelScan(wr, piWithBorder, kernelSize, method, eps); // Nexel
																		// scan
		}

		ColorModel cm = PlanarImage.createColorModel(sm); // compatible color
															// model
		TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sm, cm);
		ti.setData(wr);

		System.out.println("IQM:  Generating the output...");
		// ----------------output
		// generation--------------------------------------
		ParameterBlock pbWork = new ParameterBlock();
		if (resultOptions == 0) { // clamp to byte
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(ti);
			pbWork.add(DataBuffer.TYPE_BYTE);
			/*
			 * if (type == "RGB") pb.add(DataBuffer.TYPE_BYTE); if (type ==
			 * "8 bit") pb.add(DataBuffer.TYPE_BYTE); if (type == "16 bit")
			 * pb.add(DataBuffer.TYPE_USHORT);
			 */
			piOut = JAI.create("format", pbWork);
		}
		if (resultOptions == 1) { // normalize to byte
			double typeGreyMax = ImageTools.getImgTypeGreyMax(this.piSource);
			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(ti);
			RenderedOp extrema = JAI.create("extrema", pbWork);
			double[] minVec = (double[]) extrema.getProperty("minimum");
			double[] maxVec = (double[]) extrema.getProperty("maximum");
			double min = minVec[0];
			double max = maxVec[0];

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(ti);
			pbWork.add(new double[] { (typeGreyMax / (max - min)) }); // Rescale
			pbWork.add(new double[] { ((typeGreyMax * min) / (min - max)) }); // offset
			piOut = JAI.create("rescale", pbWork);

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			pbWork.add(DataBuffer.TYPE_BYTE);
			/*
			 * if (type == "RGB") pb.add(DataBuffer.TYPE_BYTE); if (type ==
			 * "8 bit") pb.add(DataBuffer.TYPE_BYTE); if (type == "16 bit")
			 * pb.add(DataBuffer.TYPE_USHORT);
			 */
			piOut = JAI.create("format", pbWork);
		}

		if (resultOptions == 2) { // Actual
			// does nothing
			piOut = ti;
		}
		if (resultOptions == 3) { // normalize 0-3 to 0-255
			double typeGreyMax = ImageTools.getImgTypeGreyMax(this.piSource);

			double min = 0;
			double max = 3;

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(ti);
			pbWork.add(new double[] { (typeGreyMax / (max - min)) }); // Rescale
			pbWork.add(new double[] { ((typeGreyMax * min) / (min - max)) }); // offset
			piOut = JAI.create("rescale", pbWork);

			pbWork.removeSources();
			pbWork.removeParameters();
			pbWork.addSource(piOut);
			pbWork.add(DataBuffer.TYPE_BYTE);
			/*
			 * if (type == "RGB") pb.add(DataBuffer.TYPE_BYTE); if (type ==
			 * "8 bit") pb.add(DataBuffer.TYPE_BYTE); if (type == "16 bit")
			 * pb.add(DataBuffer.TYPE_USHORT);
			 */
			piOut = JAI.create("format", pbWork);
		}

		System.out.println("IQM:  "+this.fileName);
		piOut.setProperty("image_name", this.imgName);
		piOut.setProperty("file_name", this.fileName);
		
		ImageModel im = new ImageModel(piOut);
		im.setModelName(imgName);
		im.setFileName(fileName);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpFracScanDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpFracScanDescriptor.TYPE;
	}

	/**
	 * Adds a border to the PlanarImage.
	 * 
	 * @param pi
	 * @param kernelSize
	 * @param borderMethod
	 * @param borderValue
	 * @return the image with the border
	 */
	private PlanarImage addBorder(PlanarImage pi, int kernelSize,
			int borderMethod, double[] borderValue) {
		System.out.println("IQM:  Adding a border to the image...");
		int toadd = (kernelSize) / 2;
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(toadd); // left
		pb.add(toadd); // right
		pb.add(toadd); // top
		pb.add(toadd); // bottom
		if (borderMethod == 0)
			pb.add(BorderExtender.createInstance(BorderExtender.BORDER_ZERO));
		if (borderMethod == 1)
			pb.add(new BorderExtenderConstant(borderValue));
		if (borderMethod == 2)
			pb.add(BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		if (borderMethod == 3)
			pb.add(BorderExtender.createInstance(BorderExtender.BORDER_REFLECT));
		if (borderMethod == 4)
			pb.add(BorderExtender.createInstance(BorderExtender.BORDER_WRAP));
		PlanarImage piNew = JAI.create("Border", pb, null);
		if (piNew.getMinX() != 0 || piNew.getMinY() != 0) {
			ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
			pbTrans.addSource(piNew);
			pbTrans.setParameter("xTrans", piNew.getMinX() * -1.0f);
			pbTrans.setParameter("yTrans", piNew.getMinY() * -1.0f);
			piNew = JAI.create("translate", pbTrans);
		}
		return piNew;
	}

	/**
	 * Calculates Box dimension for kernel-array.
	 * 
	 * @param PlanarImage
	 * @return Fractal dimension
	 */
	private double calcBoxDim(PlanarImage pi, int eps) {
		System.out.println("IQM:  Running Box operator on image ["
				+ pi.getProperty("image_name") + "]...");
		double dim = 0;

		String opName = new IqmOpFracBoxDescriptor().getName();
		IqmOpFracBox boxOp = (IqmOpFracBox) OperatorFactory
				.createOperator(opName);
		boxOp.setCancelable(this.isCancelable()); // set the task cancellable

		ParameterBlockImg pb = new ParameterBlockImg(opName);
		pb.setParameter("NumBoxes", eps);// Binary Image: radius; Grey scale
											// image: Blanket epsilon
		pb.setParameter("GreyMode", 0); // 0 DBC 1 RDBC
		pb.setParameter("RegStart", 1);
		pb.setParameter("RegEnd", eps);
		pb.setParameter("ShowPlot", 0);
		pb.setParameter("DeleteExistingPlot", 0);

		// set the source
		pb.setSource(new IqmDataBox(pi), 0);

		AbstractProcessingTask calcBoxTask = Application.getTaskFactory()
				.createSingleSubTask(this, new WorkPackage(boxOp, pb), false);

		IResult result = null;
		TableModel tableModel = null;
		try {
			result = (IResult) ((IExplicitProcessingTask) calcBoxTask).processExplicit();
			if (result != null && result.hasTables()) {
				tableModel = result.listTableResults().get(0).getTableModel();

				String colName = tableModel.getColumnName(5);
				if (colName.contains("Db")) {
					dim = (Double) tableModel.getValueAt(0, 5);
					System.out.println("IQM:  Box Dimension: " + dim);
				} else {
					System.out.println("IQM Error: Could not find a proper value for Db");
				}
			}
		} catch (Exception e) {
			System.out.println("IQM Error: An error occured during processing: "+ e);
		}

		return dim;
	}

	/**
	 * Calculates Pyramid dimension for kernel-array.
	 * 
	 * @param kernel
	 * @return Fractal dimension
	 */
	private double calcPyrDim(PlanarImage pi, int eps) {
		System.out.println("IQM:  Running Pyramid operator on image ["
				+ pi.getProperty("image_name") + "]...");
		double dim = 0;

		String opName = new IqmOpFracPyramidDescriptor().getName();

		ParameterBlockImg pb = new ParameterBlockImg(opName);
		IqmOpFracPyramid pyrOp = (IqmOpFracPyramid) OperatorFactory
				.createOperator(opName);
		pyrOp.setCancelable(this.isCancelable());

		pb.setParameter("NumPyrImgs", eps); //
		pb.setParameter("Interpolation", 1); // 0 NearestNeigh, 1Bilinear,
												// 2Bicubic, 3Bicubic2
		pb.setParameter("GreyMode", 0); // 0: Gradient, 1 Blanket, 2
										// Allometric
		pb.setParameter("RegStart", 1);
		pb.setParameter("RegEnd", eps);
		pb.setParameter("ShowPlot", 0);
		pb.setParameter("DeleteExistingPlot", 0);

		pb.setSource(new IqmDataBox(pi), 0);

		AbstractProcessingTask calcPyrTask = Application.getTaskFactory()
				.createSingleSubTask(this, new WorkPackage(pyrOp, pb), false);

		IResult result = null;
		TableModel tableModel = null;
		try {
			result = (IResult) ((IExplicitProcessingTask) calcPyrTask).processExplicit();
			if (result != null && result.hasTables()) {
				tableModel = result.listTableResults().get(0).getTableModel();

				String colName = tableModel.getColumnName(6);
				if (colName.contains("Dp")) {
					dim = (Double) tableModel.getValueAt(0, 6);
					System.out.println("IQM:  Pyramid Dimension: " + dim);
				} else {
					System.out.println("IQM Error: Could not find a proper value for Dp");
				}
			}
		} catch (Exception e) {
			System.out.println("IQM Error: An error occured during processing: "+ e);
		}

		return dim;
	}

	/**
	 * Calculates Minkowski dimension for kernel-array.
	 * 
	 * @param PlanarImage
	 * @return Fractal dimension
	 */
	private double calcMinkDim(PlanarImage pi, int eps) {
		System.out.println("IQM:  Running Minkowski operator on image ["
				+ pi.getProperty("image_name") + "]...");
		double dim = 0;

		String opName = new IqmOpFracMinkowski().getName();
		IqmOpFracMinkowski minkOp = (IqmOpFracMinkowski) OperatorFactory
				.createOperator(opName);

		ParameterBlockImg pb = new ParameterBlockImg(opName);
		pb.setParameter("DilEps", eps);// Binary Image: radius; Grey scale
										// iamge: Blanket epsilon
		pb.setParameter("KernelShape", 0); // 0 Square, 1 Horizontal line, 2
											// Vertical line
		pb.setParameter("RegStart", 1);
		pb.setParameter("RegEnd", eps);
		pb.setParameter("ShowPlot", 0);
		pb.setParameter("DeleteExistingPlot", 0);

		pb.setSource(new IqmDataBox(pi), 0);

		AbstractProcessingTask calcMinkTask = Application.getTaskFactory()
				.createSingleSubTask(this, new WorkPackage(minkOp, pb), false);

		IResult result = null;
		TableModel tableModel = null;
		try {
			result = (IResult) ((IExplicitProcessingTask) calcMinkTask).processExplicit();
			if (result != null && result.hasTables()) {
				tableModel = result.listTableResults().get(0).getTableModel();
				String colName = tableModel.getColumnName(6);
				if (colName.contains("Dm")) {
					dim = (Double) tableModel.getValueAt(0, 6);
					System.out.println("IQM:  Minkowski Dimension: " + dim);
				} else {
					System.out.println("IQM Error: Could not find a proper value for Dm");
				}
			}
		} catch (Exception e) {
			System.out.println("IQM Error: An error occured during processing: "+ e);
		}

		return dim;
	}

	/**
	 * Calculates FFT dimension for kernel-array.
	 * 
	 * @param kernel
	 * @return Fractal dimension
	 */
	private double calcFFTDim(PlanarImage pi, int eps) {
		System.out.println("IQM:  Running FFT operator on image ["
				+ pi.getProperty("image_name") + "]...");
		double dim = 0;

		String opName = new IqmOpFracFFTDescriptor().getName();
		IqmOpFracFFT fftOp = (IqmOpFracFFT) OperatorFactory
				.createOperator(opName);

		ParameterBlockImg pb = new ParameterBlockImg(opName);
		pb.setParameter("NumMaxK", eps);// Binary Image: radius; Grey scale
										// iamge: Blanket epsilon
		pb.setParameter("Topdim", 1); // 0 Dt = 1, 1 Dt = 2
		pb.setParameter("RegStart", 1);
		pb.setParameter("RegEnd", eps);
		pb.setParameter("ShowPlot", 0);
		pb.setParameter("DeleteExistingPlot", 0);

		pb.setSource(new IqmDataBox(pi), 0);

		AbstractProcessingTask calcFFTTask = Application.getTaskFactory()
				.createSingleSubTask(this, new WorkPackage(fftOp, pb), false);

		IResult result = null;
		TableModel tableModel = null;
		try {
			result = (IResult) ((IExplicitProcessingTask) calcFFTTask).processExplicit();
			if (result != null && result.hasTables()) {
				tableModel = result.listTableResults().get(0).getTableModel();
				String colName = tableModel.getColumnName(5);
				if (colName.contains("Df")) {
					dim = (Double) tableModel.getValueAt(0, 5);
					System.out.println("IQM:  FFT Dimension: " + dim);
				} else {
					System.out.println("IQM Error: Could not find a proper value for Df");
				}
			}
		} catch (Exception e) {
			System.out.println("IQM Error: An error occured during processing: "+ e);
		}

		return dim;
	}

	/**
	 * Calculates different parameters based on arrays.
	 * 
	 * @param wr
	 * @param pi
	 * @param kernelSize
	 * @param statMethod
	 * @param boxSizue
	 */
	private void boxScan(WritableRaster wr, PlanarImage pi, int kernelSize,
			int method, int eps, int boxSize) {
		System.out.println("IQM:  Running box scan on image ["
				+ pi.getProperty("image_name") + "]...");

		int width = wr.getWidth(); // imgWidth of the final Image
		int height = wr.getHeight(); // imgHeight of the final Image
		double result = 0;
		// double[] kernel = new double[kernelSize*kernelSize];

		// set image tiles (boxes)
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.removeParameters();
		ImageLayout layout = new ImageLayout();
		layout.setTileWidth((int) boxSize);
		layout.setTileHeight((int) boxSize);
		RenderingHints rh = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
		pi = JAI.create("Format", pb, rh);

		// get the tiles
		Raster[] tileRaster = pi.getTiles();

		// System.out.println("IQM:  IqmOpFracScan: tileRaster.length: " +
		// tileRaster.length);

		System.out.println("IQM:  Iterating through boxes in the tileRaster "
				+ tileRaster.toString() + " of length " + tileRaster.length);
		for (int r = 0; r < tileRaster.length; r++) { // tileRaster[]
			int proz = (r + 1) * 100 / tileRaster.length;
			this.fireProgressChanged(proz);

			if (isCancelled(getParentTask()))
				return;

			int minX = tileRaster[r].getMinX();
			int minY = tileRaster[r].getMinY();
			int tileWidth = tileRaster[r].getWidth(); // ==imgWidth
			int tileHeight = tileRaster[r].getHeight(); // ==imgWidth
			System.out.println("IQM:  Box#: " + r + " -- " + "minX: " + minX + "   minY: "
					+ minY + "   tileWidth: " + tileWidth + "   tileHeight: "
					+ tileHeight);

			// Create TiledImage from one tile
			TiledImage tiKernel = new TiledImage(minX, // minX, //because
														// tilRaster[r] has
														// offset
					minY, // 0minY,
					tileWidth, tileHeight, 0, // pi.getTileGridXOffset(),
					0, // pi.getTileGridYOffset(),
					pi.getSampleModel(), pi.getColorModel());
			tiKernel.setData(tileRaster[r]);

			PlanarImage piKernel = tiKernel;
			// eliminate offset
			if (piKernel.getMinX() != 0 || piKernel.getMinY() != 0) {
				ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
				pbTrans.addSource(piKernel);
				pbTrans.setParameter("xTrans", piKernel.getMinX() * -1.0f);
				pbTrans.setParameter("yTrans", piKernel.getMinY() * -1.0f);
				piKernel = JAI.create("translate", pbTrans);
			}

			// copy the custom image properties such as file_name and image_name
			piKernel = ImageTools.copyCustomImageProperties(
					(PlanarImage) pb.getSource(0), piKernel);

			switch (method) {
			case 0:
				result = calcBoxDim(piKernel, eps);
				break;
			case 1:
				result = calcPyrDim(piKernel, eps);
				break;
			case 2:
				result = calcMinkDim(piKernel, eps);
				break;
			case 3:
				result = calcFFTDim(piKernel, eps);
				break;
			}

			System.out.println("IQM:  -------------------------- Box# " + r
					+ " --------------------------");
			// set all pixels of a tile to the result
			for (int x = 0; x < tileWidth; x++) {
				for (int y = 0; y < tileHeight; y++) {
					// System.out.println("IQM:  For loop info: " + x + " -- " + y +
					// "    result: " + result);
					if (((minX + x) < width) && ((minY + y) < height)) {
						// if (r == 0) System.out.println("IQM:  Box: " + r +
						// "      x: "+x+"   y: "+y+"  .getSample: "
						// +piKernel.getData().getSample(x, y, 0));
						wr.setSample(minX + x, minY + y, 0, result);
						// wr.setSample(minX + x, minY + y, 0,
						// piKernel.getData().getSample(x, y, 0)); //source
						// image as a result, only for testing
						// wr.setSample(minX + x, minY + y, 0, 33); //constant
						// image as a result, only for testing

					}

				}
			}
		} // r tileRaster[]
			// recreate WritableRaster
			// int width = ti.getWidth();
		// int height = ti.getHeight();
		// SampleModel sm =
		// RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_DOUBLE,
		// width, height, 1);
		// double[] doubleArr = new double[width*height];
		// DataBufferDouble db = new DataBufferDouble(doubleArr, width*height);
		// wr = RasterFactory.createWritableRaster(sm, db, new Point(0,0));

	}

	/**
	 * Calculates different parameters based on arrays.
	 * 
	 * @param wr
	 * @param piWithBorder
	 * @param kernelSize
	 * @param statMethod
	 */
	private void nexelScan(WritableRaster wr, PlanarImage piWithBorder,
			int kernelSize, int method, int eps) {
		System.out.println("IQM:  Running nexel scan on image ["
				+ piWithBorder.getProperty("image_name") + "]...");

		int width = wr.getWidth(); // imgWidth of the final Image
		int height = wr.getHeight(); // imgHeight of the final Image
		double result = 0;
		double[] kernel = new double[kernelSize * kernelSize];

		// get a raster for the calculation
		// Raster r = piWithBorder.getData(); //image has added border

		// for each pixel calculation of requested parameter
		int numIterations = width;

		String cropOpName = new IqmOpCropDescriptor().getName();
		IqmOpCrop cropOp = (IqmOpCrop) OperatorFactory
				.createOperator(cropOpName);

		ParameterBlockImg pb = new ParameterBlockImg(cropOpName);
		pb.setParameter("Method", 0); // 0 Manual 1 ROI
		pb.setParameter("NewWidth", kernelSize);
		pb.setParameter("NewHeight", kernelSize);
		pb.setParameter("OffSet", 5); // user offset

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// kernel = r.getSamples(i, j, kernelSize, kernelSize, 0,
				// kernel);
				if (isCancelled(getParentTask())) {
					return;
				}
				int proz = ((j+1) * 99 / height / width) + ((i+1) * 99 / width);
				fireProgressChanged(proz);

				pb.setParameter("OffSetX", i);
				pb.setParameter("OffSetY", j);

				pb.setSource(new IqmDataBox(piWithBorder), 0);

				// PlanarImage piKernel =
				// JAI.create(IqmResources.getOperatorName("operator.crop"),
				// pbJAI, null);
				AbstractProcessingTask calcImageTask1 = Application
						.getTaskFactory().createSingleSubTask(this,
								new WorkPackage(cropOp, pb), false);

				IResult kernelResult = null;
				PlanarImage piKernel = null;
				try {
					kernelResult = (Result) ((IExplicitProcessingTask) calcImageTask1)
							.processExplicit();
					if (kernelResult != null && kernelResult.hasImages()) {
						piKernel = kernelResult.listImageResults().get(0)
								.getImage();

						switch (method) {
						case 0:
							result = calcBoxDim(piKernel, eps);
							break;
						case 1:
							result = calcPyrDim(piKernel, eps);
							break;
						case 2:
							result = calcMinkDim(piKernel, eps);
							break;
						case 3:
							result = calcFFTDim(piKernel, eps);
							break;
						}
						wr.setSample(i, j, 0, result);
					}
				} catch (Exception e) {
					System.out.println("IQM Error: An error occurred: " + e);
				}
			}
		}
	}
}
