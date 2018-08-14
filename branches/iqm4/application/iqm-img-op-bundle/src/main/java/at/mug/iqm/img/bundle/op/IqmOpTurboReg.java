package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpTurboReg.java
 * 
 * $Id$
 * $HeadURL$
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


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.awt.Image;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;

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
import at.mug.iqm.img.bundle.descriptors.IqmOpTurboRegDescriptor;

/**
 * This is an implementation of an ImageJ plugin by P. Thevenaz
 * P. Thevenaz, U.E. Ruttimann, M. Unser, "A Pyramid Approach to Subpixel Registration Based on Intensity"
 * IEEE Transactions on Image Processing, vol. 7, no. 1, pp. 27-41, January 1998
 * http://bigwww.epfl.ch/thevenaz/turboreg/
 *
 * @author Ahammer, Kainz
 * @since 2009 05
 */
public class IqmOpTurboReg extends AbstractOperator {

	public IqmOpTurboReg() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@SuppressWarnings("all")
	@Override
	public IResult run(IWorkPackage wp) {

		PlanarImage piOut = null;
		
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi1 = ((IqmDataBox) pb.getSource(0)).getImage();
		PlanarImage pi2 = ((IqmDataBox) pb.getSource(1)).getImage();

		int width = pi1.getWidth();
		int height = pi1.getHeight();


		int regMode = pb.getIntParameter("RegMode");
		int method = pb.getIntParameter("Method");

		String regModeStr;
		if (regMode == 0) {
			regModeStr = "Fast";
		}
		if (regMode == 1) {
			regModeStr = "Accurate";
		}

		// -------------------------------------
		String methodStr = null;
		if (method == 0) {
			methodStr = "-translation " + (width / 2) + " " + (height / 2)
					+ " " // Source translation landmark.
					+ (width / 2) + " " + (height / 2) + " "; // Target
																// translation
																// landmark.
		}
		if (method == 1) {
			methodStr = "-rigidBody " + // registration method.
					// + (imgWidth / 2) + " " + (imgHeight / 2) + " " // Source
					// translation landmark.
					// + (imgWidth / 2) + " " + (imgHeight / 2) + " " // Target
					// translation landmark.
					// + "0 " + (imgHeight / 2) + " " // Source first rotation
					// landmark.
					// + "0 " + (imgHeight / 2) + " " // Target first rotation
					// landmark.
					// + (imgWidth - 1) + " " + (imgHeight / 2) + " " // Source
					// second rotation landmark.
					// + (imgWidth - 1) + " " + (imgHeight / 2) + " " ;// Target
					// second rotation landmark.
					+(width / 2) + " " + (height / 2) + " " // Source
															// translation
															// landmark.
					+ (width / 2) + " " + (height / 2) + " " // Target
																// translation
																// landmark.
					+ (width / 2) + " " + (height / 2) + " " // Source first
																// rotation
																// landmark.
					+ (width / 2) + " " + (height / 2) + " " // Target first
																// rotation
																// landmark.
					+ (width / 2) + " " + (height / 2) + " " // Source second
																// rotation
																// landmark.
					+ (width / 2) + " " + (height / 2) + " ";// Target second
																// rotation
																// landmark.
		}
		if (method == 2) {
			methodStr = "-scaledRotation " + +(width / 3) + " " + (height / 2)
					+ " " // Source landmark1.
					+ (width / 3) + " " + (height / 2) + " " // Target
																// landmark1.
					+ (width / 3 * 2) + " " + (height / 2) + " " // Source
																	// landmark2.
					+ (width / 3 * 2) + " " + (height / 2) + " "; // Target
																	// landmark2.

		}
		if (method == 3) {
			methodStr = "-affine " + +(width / 2) + " " + (height / 4) + " " // Source
																				// landmark1.
					+ (width / 2) + " " + (height / 4) + " " // Target
																// landmark1.
					+ (width / 4) + " " + (height / 4 * 3) + " " // Source
																	// landmark2.
					+ (width / 4) + " " + (height / 4 * 3) + " " // Target
																	// landmark2.
					+ (width / 4 * 3) + " " + (height / 4 * 3) + " " // Source
																		// landmark3.
					+ (width / 4 * 3) + " " + (height / 4 * 3) + " ";// Target
																		// landmark3.
		}
		if (method == 4) {
			methodStr = "-bilinear " + +(width / 4) + " " + (height / 4) + " " // Source
																				// landmark1.
					+ (width / 4) + " " + (height / 4) + " " // Target
																// landmark1.
					+ (width / 4) + " " + (height / 4 * 3) + " " // Source
																	// landmark2.
					+ (width / 4) + " " + (height / 4 * 3) + " " // Target
																	// landmark2.
					+ (width / 4 * 3) + " " + (height / 4) + " " // Source
																	// landmark3.
					+ (width / 4 * 3) + " " + (height / 4) + " " // Target
																	// landmark3.
					+ (width / 4 * 3) + " " + (height / 4 * 3) + " " // Source
																		// landmark4.
					+ (width / 4 * 3) + " " + (height / 4 * 3) + " ";// Target
																		// landmark4.
		}

		// OpenDialog ImageJ
		ImageJ imageJ = IJ.getInstance();
		if (imageJ != null) {
			BoardPanel.appendTextln("IqmOpTurboReg: ImageJ already running");
			try {
				imageJ.setVisible(true);
			} catch (Exception e2) {
				imageJ = new ImageJ();
				BoardPanel
						.appendTextln("IqmOpTurboReg: New instance of ImageJ");
			}
		}
		if (imageJ == null) {
			imageJ = new ImageJ();
			BoardPanel.appendTextln("IqmOpTurboReg: New instance of ImageJ");
		}

		// get AWT Image direct way
		Image image1 = pi1.getAsBufferedImage();
		Image image2 = pi2.getAsBufferedImage();

		// get AWT Image
		// BufferedImage bi1 = pi1.getAsBufferedImage();
		// BufferedImage bi2 = pi2.getAsBufferedImage();
		// Image image1 = bi1.getScaledInstance(bi1.getWidth(),bi1.getHeight(),
		// Image.SCALE_DEFAULT);
		// Image image2 = bi2.getScaledInstance(bi2.getWidth(),bi2.getHeight(),
		// Image.SCALE_DEFAULT);

		SampleModel sampleModel = pi1.getSampleModel();
		String imgName = String.valueOf(pi1.getProperty("image_name"));
		String fileName = String.valueOf(pi1.getProperty("file_name"));
		ImagePlus targetImp = new ImagePlus("", image1);
		ImagePlus sourceImp = new ImagePlus("", image2);
		targetImp.setTitle("Target");
		sourceImp.setTitle("Source");
		ij.WindowManager.closeAllWindows();
		targetImp.show();
		sourceImp.show();
		
		// ImageProcessor targetMskIP = targetImp.getProcessor();
		// ImageProcessor sourceMskIP = sourceImp.getProcessor();

		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		// RenderingHints rh = null;
		// ParameterBlock pb = new ParameterBlock();
		BoardPanel
				.appendTextln("IqmOpTurboReg: Registration, please wait.......");
		Object myTurboRegObject = IJ.runPlugIn("TurboReg_", "-align "
				+ "-window Source "// Source (window reference).
				+ "0 0 " + (width - 1) + " " + (height - 1) + " " // No
																	// cropping.
				+ "-window Target "// Target (window reference).
				+ "0 0 " + (width - 1) + " " + (height - 1) + " " // No
																	// cropping.
				+ methodStr + " " // registration method + landmarks.
				+ "-hideOutput"); // -showOutput In case -hideOutput is
									// selected, the only way to
		// retrieve the registration results is by the way of another plugin.

		Method meth = null;
		try {
			meth = myTurboRegObject.getClass().getMethod("getTransformedImage",
					null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImagePlus imp = null;
		try {
			imp = (ImagePlus) meth.invoke(myTurboRegObject, null);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// int bytesPerPixel = imp.getBytesPerPixel();
		// System.out.println("IqmOpTurboReg: bytesPerPixel: "+bytesPerPixel );
		// IJ.run(imp, "RGB Color", "");
		// imp.updateImage();
		// if (pi1.getNumBands() == 1){ //grey
		// IJ.run(imp, "8-bit", "");
		// }
		imp.updateImage(); // imp is here always a 32bit 3band image
		// System.out.println("IqmOpTurboReg: imp.getBitDepth(): " +
		// imp.getBitDepth() );
		// System.out.println("IqmOpTurboReg: imp.getDisplayRangeMax(): " +
		// imp.getDisplayRangeMax() );
		// System.out.println("IqmOpTurboReg: imp.getDisplayRangeMin(): " +
		// imp.getDisplayRangeMin() );
		// System.out.println("IqmOpTurboReg: imp.getNChannels() : " +
		// imp.getNChannels() );
		// if(imp.getNChannels() == 1){ //32bit to 8bit
		// IJ.run(imp, "8-bit", "");
		// }
		// Threshold, da manchmal negative Werte herauskommen
		// double intMax = (double)imp.getProcessor().getMax();
		// IJ.setThreshold(0.0d, intMax);
		// IJ.run(imp, "Threshold...", "thresholded remaining black");

		// Image imageResult = imp.getBufferedImage(); //Fehlermeldung
		Image imageResult = imp.getImage();
		piOut = JAI.create("AWTImage", imageResult); // result is always 8bit?
		// piOut = PlanarImage.wrapRenderedImage((RenderedImage) imageResult);
		// //macht Fehler bei 32bit bildern,

		// System.out.println("IqmOpTurboReg: piOut.getNumBands() : " +
		// ((PlanarImage)piOut).getNumBands());
		imp.show();

		ij.WindowManager.closeAllWindows();
		targetImp.flush();
		sourceImp.flush();
		imp.flush();

//		ij.IJ.getInstance().quit();
		// if (((PlanarImage)piOut).getColorModel() instanceof IndexColorModel){
		// IndexColorModel icm =
		// (IndexColorModel)((PlanarImage)piOut).getColorModel();
		// byte[][] data = new byte[3][icm.getMapSize()];
		// icm.getReds(data[0]);
		// icm.getGreens(data[1]);
		// icm.getBlues(data[2]);
		// LookupTableJAI lut = new LookupTableJAI(data);
		//
		// ParameterBlock pb = new ParameterBlock();
		// pb.addSource(piOut);
		// pb.add(lut);
		// piOut = JAI.create("lookup", pb); //RGB image
		// }

		// Folgendes geht auch
		// ImageLayout layout = new ImageLayout();
		// ColorModel cm = new
		// ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new
		// int[] { 8, 8, 8 }, false, true, Transparency.OPAQUE,
		// DataBuffer.TYPE_BYTE);
		// layout.setColorModel(cm);
		// RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT,
		// layout);
		// ParameterBlockJAI pbFormat = new ParameterBlockJAI("format");
		// pbFormat.addSource(piOut);
		// piOut = JAI.create("format", pbFormat, hints);

		ParameterBlockJAI pbFormat = new ParameterBlockJAI("format");
		pbFormat.addSource(piOut);
		pbFormat.setParameter("dataType", DataBuffer.TYPE_BYTE);
		// if (type == "RGB" ) pbFormat.setParameter("dataType",
		// DataBuffer.TYPE_BYTE);
		// if (type == "8 bit" ) pbFormat.setParameter("dataType",
		// DataBuffer.TYPE_BYTE);
		// if (type == "16 bit" ) pbFormat.setParameter("dataType",
		// DataBuffer.TYPE_USHORT);
		piOut = JAI.create("format", pbFormat);

		if (pi1.getNumBands() != 1) { // grey, da Ergebnis normalerweise immer
										// 32bit 3band
			// double[][] matrix = {{ 0.114, 0.587, 0.299, 0 }}; geht auch
			double[][] matrix = { { 0.333, 0.333, 0.333, 0 } };
			// double[][] matrix = {{ 0.25, 0.25, 0.25, 0.25 }}; liefert falsche
			// Werte da nur 3 bands
			ParameterBlock pbBC = new ParameterBlock();
			pbBC.addSource(piOut);
			pbBC.add(matrix);
			piOut = JAI.create("bandcombine", pbBC, null);
		}

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
			this.name = new IqmOpTurboRegDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpTurboRegDescriptor.TYPE;
	}
}
