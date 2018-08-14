package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpRGBRelativeOpImage.java
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


import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.TiledImage;

/**
 * @author Ahammer
 * @since   2009 04
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpRGBRelativeOpImage extends PointOpImage {

	private RenderedImage ri;
	private int method;
	private int rankLeftRG;
	private int rankLeftRB;
	private int rankLeftGR;
	private int rankLeftGB;
	private int rankLeftBR;
	private int rankLeftBG;
	private int and;
	private int rankRightRG;
	private int rankRightRB;
	private int rankRightGR;
	private int rankRightGB;
	private int rankRightBR;
	private int rankRightBG;
	private int ratio;
	private int binarize;

	public IqmOpRGBRelativeOpImage(RenderedImage ri, int method,
			int rankLeftRG, int rankLeftRB, int rankLeftGR, int rankLeftGB,
			int rankLeftBR, int rankLeftBG, int and, int rankRightRG,
			int rankRightRB, int rankRightGR, int rankRightGB, int rankRightBR,
			int rankRightBG, int ratio, int binarize, ImageLayout layout,
			RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.method = method;
		this.rankLeftRG = rankLeftRG;
		this.rankLeftRB = rankLeftRB;
		this.rankLeftGR = rankLeftGR;
		this.rankLeftGB = rankLeftGB;
		this.rankLeftBR = rankLeftBR;
		this.rankLeftBG = rankLeftBG;
		this.and = and;
		this.rankRightRG = rankRightRG;
		this.rankRightRB = rankRightRB;
		this.rankRightGR = rankRightGR;
		this.rankRightGB = rankRightGB;
		this.rankRightBR = rankRightBR;
		this.rankRightBG = rankRightBG;

		this.ratio = ratio;
		this.binarize = binarize;
	}

	@Override
	public Raster computeTile(int x, int y) {
		// System.out.println("IqmRGBRelativeOpImage parameter: "
		// +p0+" "+p1+" "+p2+" "+p3+" "+p4+" "+p5+" "+binarize);
		// System.out.println("IqmRGBRelativeOpImage parameter: x, y, imgWidth, imgHeight "
		// +x+ "  "+y+ " "+imgWidth+"  "+imgHeight);

		Raster r = this.ri.getTile(x, y);
		int minX = r.getMinX();
		int minY = r.getMinY();
		int width = r.getWidth();
		int height = r.getHeight();
		WritableRaster wr = r.createCompatibleWritableRaster(minX, minY, width,
				height);
		TiledImage ti = new TiledImage(ri, true);
		ti = ti.getSubImage(minX, minY, width, height);

		wr = (WritableRaster) ti.getData();
		// wr = (WritableRaster) ti.copyData();
		// wr.setDataElements(minX, minY, pi.getData());
		return wr;
	}
}
