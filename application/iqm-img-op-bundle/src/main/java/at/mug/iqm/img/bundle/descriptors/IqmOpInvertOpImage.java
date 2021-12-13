/*
 * This file is part of IQM.
 * Copyright (c) 2010-2013 Helmut Ahammer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpInvertOpImage.java
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
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.TiledImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.commons.util.DialogUtil;

/**
 * @author Ahammer
 * @since 2009 03
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpInvertOpImage extends PointOpImage {

	// class specific logger
	private static Class<?> caller = IqmOpInvertOpImage.class;
	private static final Logger logger = LogManager.getLogger(IqmOpInvertOpImage.class);

	private RenderedImage ri;
	private int invOpt;

	// private WritableRaster wr;

	public IqmOpInvertOpImage(RenderedImage ri, int invOpt, ImageLayout layout,
			RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.invOpt = invOpt;
	}

	@Override
	public Raster computeTile(int x, int y) {
		// System.out.println("IqmInvert computeTile: ");
		// ImageInfo.printInfo((PlanarImage) ri);
		Raster r = this.ri.getTile(x, y);
		int numBands = r.getNumBands();
		// System.out.println("Number of bands: "+numBands);
		int minX = r.getMinX();
		int minY = r.getMinY();
		int width = r.getWidth();
		int height = r.getHeight();
		// System.out.println("Tile Image Info");
		// System.out.println("IqmInvertOpimage: x:" + x);
		// System.out.println("IqmInvertOpimage: y:" + y);
		// System.out.println("IqmInvertOpimage: minX:" + minX);
		// System.out.println("IqmInvertOpimage: minY:" + minY);
		// System.out.println("IqmInvertOpimage: imgWidth:" + imgWidth);
		// System.out.println("IqmInvertOpimage: imgHeight:" + imgHeight);
		WritableRaster wr = r.createCompatibleWritableRaster(minX, minY, width,
				height);

		// String option = "no JAI"; //16bit not ok
		String option = "JAI"; // 16 bit ok

		// if (option.equals("no JAI")){
		// //wr =
		// r.createCompatibleWritableRaster(minX,minY,imgWidth,imgHeight);
		// for(int l=0;l<r.getHeight();l++)
		// for(int c=0;c<r.getWidth();c++){
		// if (numBands == 1){
		// int p = r.getSample(c+minX,l+minY,0);
		// p=255-p;
		// wr.setSample(c+minX,l+minY,0,p);
		// }
		// if (numBands == 3){
		// for (int n = 0; n <= 2; n++){
		// int p = r.getSample(c+minX,l+minY,n);
		// p=255-p;
		// wr.setSample(c+minX,l+minY,n,p);
		// }
		// }
		// }//Ende Pixel Schleife
		// //return wr;
		// }

		// JAI Alternative
		// crop image
		if (option.equals("JAI")) {
			// Raster r2 = null;
			// ParameterBlock pb = IqmTools.getCurrParameterBlock();
			if (this.invOpt == 0) { // Invert Image
				TiledImage ti = new TiledImage(ri, true);
				ti = ti.getSubImage(minX, minY, width, height);

				ParameterBlockJAI pb = new ParameterBlockJAI("invert");
				pb.addSource(ti);
				// PlanarImage pi = JAI.create("invert", pb2,
				// RenderableRegistryMode.MODE_NAME);
				PlanarImage pi = null;
				// for (int i=0; i<1000; i++){
				// pi2 = null;
				pi = JAI.create("invert", pb, null);
				// }
				// r2 = pi.getData();
				wr = (WritableRaster) pi.getData();
				// wr = (WritableRaster) pi.copyData();
				// wr.setDataElements(minX, minY, pi.getData());
				return wr;
				// return r2;
			}

			if (this.invOpt == 1) { // Invert lookup table
				DialogUtil.getInstance().showDefaultInfoMessage(
						"LUT inversion not implemented yet!");
				// IqmBoardPanel.appendTexln("LUT inversion not implemented",
				// caller);
				// wr =
				// r.createCompatibleWritableRaster(minX,minY,width,height);
				// r2 = r;

			}
			// return r2;
		}
		return wr;

	}
}
