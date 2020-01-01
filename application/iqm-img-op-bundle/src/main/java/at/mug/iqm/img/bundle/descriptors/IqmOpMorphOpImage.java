package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpMorphOpImage.java
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


import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

/**
 * @author Ahammer
 * @since 2009 04
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpMorphOpImage extends PointOpImage {

	private RenderedImage ri;
	private int kernelShape;
	private int kernelWidth;
	private int kernelHeight;
	private int iterations;
	private double minBlobSize;
	private double maxBlobSize;
	private int morph;

	public IqmOpMorphOpImage(RenderedImage ri, int kernelShape,
			int kernelWidth, int kernelHeight, int iterations, double minBlobSize,
			double maxBlobSize, int morph, ImageLayout layout,
			RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.kernelShape  = kernelShape;
		this.kernelWidth  = kernelWidth;
		this.kernelHeight = kernelHeight;
		this.iterations   = iterations;
		this.minBlobSize  = minBlobSize;
		this.maxBlobSize  = maxBlobSize;
		this.morph = morph;
	}

	@Override
	public Raster computeTile(int x, int y) {

		Raster r = this.ri.getTile(x, y);
		// int minX = r.getMinX();
		// int minY = r.getMinY();
		// int imgWidth = r.getWidth();
		// int imgHeight = r.getHeight();
		// WritableRaster wr =
		// r.createCompatibleWritableRaster(minX,minY,imgWidth,imgHeight);
		// TiledImage ti = new TiledImage(ri, true);
		// ti = ti.getSubImage(minX, minY, imgWidth, imgHeight);
		//
		// KernelJAI kernel = null;
		// int kernelWidth = kernelSize;
		// int kernelHeight = kernelSize;
		// if (kernelShape == 0){
		// kernel = KernelFactory.createRectangle(kernelWidth, kernelHeight);
		// }
		// if (kernelShape == 1){
		// kernel = KernelFactory.createCircle((kernelSize-1)/2); //Size =
		// radius*2 +1
		// }
		// //System.out.println(KernelUtil.kernelToString(kernel, true));
		// PlanarImage pi = ti;
		// ParameterBlock pb = new ParameterBlock();
		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		//
		// if (morph == 0){ //dilate
		// pb.add(kernel);
		// for (int t = 0; t < times; t++){
		// pb.addSource(pi);
		// pi = JAI.create("dilate", pb, rh);
		// pb.removeSources();
		// }
		// wr = (WritableRaster) pi.getData();
		// //wr = (WritableRaster) pi.copyData();
		// }
		// if (morph == 1){ //erode
		// pb.add(kernel);
		// for (int t = 0; t < times; t++){
		// pb.addSource(pi);
		// pi = JAI.create("erode", pb, rh);
		// pb.removeSources();
		// }
		// wr = (WritableRaster) pi.getData();
		// //wr = (WritableRaster) pi.copyData();
		// }
		// if (morph == 2){ //close
		// pb.add(kernel);
		// for (int t = 0; t < times; t++){
		// pb.addSource(pi);
		// pi = JAI.create("dilate", pb, rh);
		// pb.removeSources();
		// pb.addSource(pi);
		// pi = JAI.create("erode", pb, rh);
		// pb.removeSources();
		// }
		// wr = (WritableRaster) pi.getData();
		// //wr = (WritableRaster) pi.copyData();
		// }
		// if (morph == 3){ //open
		// pb.add(kernel);
		// for (int t = 0; t < times; t++){
		// pb.addSource(pi);
		// pi = JAI.create("erode", pb, rh);
		// pb.removeSources();
		// pb.addSource(pi);
		// pi = JAI.create("dilate", pb, rh);
		// pb.removeSources();
		// }
		// wr = (WritableRaster) pi.getData();
		// //wr = (WritableRaster) pi.copyData();
		// }
		// // morph == 4 Skeletonize and Morph == 5 Fill holes in
		// IqmMorphOperator.java implementiert
		// return wr;
		return r;
	}
}
