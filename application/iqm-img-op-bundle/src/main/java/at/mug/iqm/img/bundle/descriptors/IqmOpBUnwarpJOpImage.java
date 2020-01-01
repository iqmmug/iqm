package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpBUnwarpJOpImage.java
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
 * @since 2009 05
 */

@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpBUnwarpJOpImage extends PointOpImage {

	private RenderedImage ri1;
	private RenderedImage ri2;
	private int regMode;
	private int subSamp;
	private int initDef;
	private int finalDef;
	private float divW;
	private float curlW;
	private float landW;
	private float imgW;
	private float consW;
	private float stopTh;

	public IqmOpBUnwarpJOpImage(RenderedImage ri1, RenderedImage ri2,
			int regMode, int subSamp, int initDef, int finalDef, float divW,
			float curlW, float landW, float imgW, float consW, float stopTh,
			ImageLayout layout, RenderingHints hints, boolean b) {
		super(ri1, ri2, layout, hints, b);
		this.regMode = regMode;
		this.subSamp = subSamp;
		this.initDef = initDef;
		this.finalDef = finalDef;
		this.divW = divW;
		this.curlW = curlW;
		this.landW = landW;
		this.imgW = imgW;
		this.consW = consW;
		this.stopTh = stopTh;
	}

	@Override
	public Raster computeTile(int x, int y) {
		Raster r = this.ri1.getTile(x, y);
		return r;
	}
}
