package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpColDecon.java
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


import ij.ImagePlus;

import java.awt.Image;
import java.awt.image.IndexColorModel;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpColDeconDescriptor;
import at.mug.iqm.img.bundle.imagej.IqmColor_Deconvolution;

/**
 * This is an implementation of an ImageJ plugin by G.Landini
 * <br>
 * http://www.dentistry.bham.ac.uk/landinig/software/cdeconv/cdeconv.html
 * 
 * @author Ahammer, Kainz
 * @since    2009 05
 */
public class IqmOpColDecon extends AbstractOperator {

	public IqmOpColDecon() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

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
		
		int colDecon = pb.getIntParameter("ColDecon");
		
		fireProgressChanged(5);

		Image image = pi.getAsBufferedImage();
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		// int sampleModel = pi.getSampleModel().getDataType();
		// new ImagePlus("", pi.getAsBufferedImage()).show();
		// ImagePlus imp = IJ.getImage();
		ImagePlus imp = new ImagePlus("", image);

		// this part uses ImageJ's Color Deconvolution
		String vectors = null;
		if (colDecon == 0)
			vectors = "H&E";
		if (colDecon == 1)
			vectors = "H&E 2";
		if (colDecon == 2)
			vectors = "H DAB";
		if (colDecon == 3)
			vectors = "Feulgen Light Green";
		if (colDecon == 4)
			vectors = "Giemsa";
		if (colDecon == 5)
			vectors = "FastRed FastBlue DAB";
		if (colDecon == 6)
			vectors = "Methyl Green DAB";
		if (colDecon == 7)
			vectors = "H&E DAB";
		if (colDecon == 8)
			vectors = "H AEC";
		if (colDecon == 9)
			vectors = "Azan-Mallory";
		if (colDecon == 10)
			vectors = "Masson Trichrome";
		if (colDecon == 11)
			vectors = "Alcian blue & H";
		if (colDecon == 12)
			vectors = "H PAS";
		if (colDecon == 13)
			vectors = "DAB NBTBCIP"; // only in Iqm version
		if (colDecon == 14)
			vectors = "DAB"; // only in Iqm version
		if (colDecon == 15)
			vectors = "RGB";
		if (colDecon == 16)
			vectors = "CMY";
		if (colDecon == 17)
			vectors = "User values";
		
		fireProgressChanged(10);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		ImagePlus[] resultStack;
		IqmColor_Deconvolution ijPlugIn = new IqmColor_Deconvolution(); // Palette
																		// images
		resultStack = ijPlugIn.run(imp, vectors);

		if (resultStack == null) {
			BoardPanel.appendTextln("IqmOpColDecon: Convolution result is null");
			return null;
		}
		Image image1 = resultStack[0].getImage();// .getBufferedImage();
		Image image2 = resultStack[1].getImage();// .getBufferedImage();
		Image image3 = resultStack[2].getImage();// .getBufferedImage();

		PlanarImage pi1 = JAI.create("AWTImage", image1);
		PlanarImage pi2 = JAI.create("AWTImage", image2);
		PlanarImage pi3 = JAI.create("AWTImage", image3);

		fireProgressChanged(30);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		// System.out.println("pi1. colormodel: " + pi1.getColorModel());

		IndexColorModel icm = (IndexColorModel) pi1.getColorModel();
		byte[][] data = new byte[3][icm.getMapSize()];
		icm.getReds(data[0]);
		icm.getGreens(data[1]);
		icm.getBlues(data[2]);
		LookupTableJAI lut = new LookupTableJAI(data);
		pi1 = JAI.create("lookup", pi1, lut); // RGB image

		fireProgressChanged(40);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		icm = (IndexColorModel) pi2.getColorModel();
		data = new byte[3][icm.getMapSize()];
		icm.getReds(data[0]);
		icm.getGreens(data[1]);
		icm.getBlues(data[2]);
		lut = new LookupTableJAI(data);
		pi2 = JAI.create("lookup", pi2, lut); // RGB image
		
		fireProgressChanged(50);
		if (isCancelled(getParentTask())){
			return null;
		}

		icm = (IndexColorModel) pi3.getColorModel();
		data = new byte[3][icm.getMapSize()];
		icm.getReds(data[0]);
		icm.getGreens(data[1]);
		icm.getBlues(data[2]);
		lut = new LookupTableJAI(data);
		pi3 = JAI.create("lookup", pi3, lut); // RGB image
		
		fireProgressChanged(60);
		if (isCancelled(getParentTask())){
			return null;
		}

		ParameterBlock pb2 = new ParameterBlock();
		double[][] m = { { 0.114, 0.587, 0.299, 0 } };

		pb2.addSource(pi1);
		pb2.add(m);
		pi1 = JAI.create("bandcombine", pb2, null); // Gray
		
		fireProgressChanged(70);
		if (isCancelled(getParentTask())){
			return null;
		}

		pb2.removeSources();
		pb2.removeParameters();
		pb2.addSource(pi2);
		pb2.add(m);
		pi2 = JAI.create("bandcombine", pb2, null); // Gray
		
		fireProgressChanged(80);
		if (isCancelled(getParentTask())){
			return null;
		}

		pb2.removeSources();
		pb2.removeParameters();
		pb2.addSource(pi3);
		pb2.add(m);
		pi3 = JAI.create("bandcombine", pb2, null); // Gray

		// System.out.println("pi1. colormodel: " + pi1.getColorModel());

		// new ImagePlus("", pi1.getAsBufferedImage()).show();

		// ParameterBlock pb2 = new ParameterBlock();
		// ColorModel cm = new
		// ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), new
		// int[] { 8, 8, 8 }, false, true, Transparency.OPAQUE,
		// DataBuffer.TYPE_BYTE);
		// pb2.addSource(pi1);
		// pb2.add(cm);
		// pi1 = JAI.create("ColorConvert", pb2);
		// System.out.println("pi1. colormodel: " + pi1.getColorModel());
		//
		//
		//
		// pb2.removeSources();
		// pb2.removeParameters();
		// pb2.addSource(pi2);
		// pb2.add(cm);
		// pi2 = JAI.create("ColorConvert", pb2);
		//
		// pb2.removeSources();
		// pb2.removeParameters();
		// pb2.addSource(pi3);
		// pb2.add(cm);
		// pi3 = JAI.create("ColorConvert", pb2);
		// RenderingHints rh = new
		// RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, null);

		pb2.removeSources();
		pb2.removeParameters();
		pb2.setSource(pi1, 0);
		pb2.setSource(pi2, 1);
		pb2.setSource(pi3, 2);
		piOut = JAI.create("bandmerge", pb2);
		// System.out.println("piOut. colormodel: " +
		// ((PlanarImage)piOut).getColorModel());

		// WritableRaster pixelraster = ((PlanarImage) piOut).copyData();
		// ColorModel cm = new
		// ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new
		// int[] { 8, 8, 8 }, false, false, Transparency.OPAQUE,
		// DataBuffer.TYPE_BYTE);
		// SampleModel sm= cm.createCompatibleSampleModel(pi.getWidth(),
		// pi.getHeight());
		// piOut = new TiledImage(0,0,pi.getWidth(), pi.getHeight(),0,0,sm,cm);
		// ((TiledImage) piOut).setData(pixelraster);

		// new ImagePlus("", ((PlanarImage) piOut).getAsBufferedImage()).show();

		// ImageLayout layout = new ImageLayout();
		// ColorModel cm = new
		// ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB),
		// new int[] { 8, 8, 8 }, false, true, Transparency.OPAQUE,
		// DataBuffer.TYPE_BYTE);
		// layout.setColorModel(cm);
		// RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT,
		// layout);

		// pb2.removeSources();
		// pb2.removeParameters();
		// pb2.addSource(piOut);
		// pb2.add(sampleModel);
		// piOut = JAI.create("format", pb2); //imp ist zb. bei RGB vom Typ int
		// anstatt byte

		imp.flush();
		// wr.setDataElements(minX, minY, pi.getData()); //
		// wr = (WritableRaster) pi.getData(); gehr hier nicht, da AWT image
		// Tile Info verliert!!!!!!
		
		ImageModel im = new ImageModel(piOut);
		im.setFileName(fileName);
		im.setModelName(imgName);
		
		fireProgressChanged(95);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpColDeconDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpColDeconDescriptor.TYPE;
	}
}
