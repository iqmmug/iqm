package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpDirLocalOpImage.java
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
 * @since 2010 05
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpDirLocalOpImage extends PointOpImage {

	private RenderedImage ri;
	private int gradient;
	private int kernelSize;
	private int eigen;
	private int outImg;
	private int showVecPlot;

	public IqmOpDirLocalOpImage(RenderedImage ri, int gradient, int kernelSize,
			int eigen, int outImg, int showVecPlot, ImageLayout layout,
			RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.gradient = gradient;
		this.kernelSize = kernelSize;
		this.eigen = eigen;
		this.showVecPlot = showVecPlot;
		this.outImg = outImg;
	}

	@Override
	public Raster computeTile(int x, int y) {

		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
