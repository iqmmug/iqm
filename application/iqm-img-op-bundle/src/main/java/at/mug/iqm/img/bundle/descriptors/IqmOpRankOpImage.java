package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpRankOpImage.java
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
public class IqmOpRankOpImage extends PointOpImage {

	private RenderedImage ri;
	private int method;
	private int kernelShape;
	private int kernelSize;
	private int iterations;
	private double alpha;

	public IqmOpRankOpImage(RenderedImage ri, int method, int kernelShape,
			int kernelSize, int iterations, double alpha, ImageLayout layout, RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.method = method;
		this.kernelShape = kernelShape;
		this.kernelSize = kernelSize;
		this.iterations = iterations;
		this.alpha = alpha;
	}

	@Override
	public Raster computeTile(int x, int y) {

		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
