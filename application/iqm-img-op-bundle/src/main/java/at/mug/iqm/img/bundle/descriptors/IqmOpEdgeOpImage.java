package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpEdgeOpImage.java
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
 * <li>2010 09 added option: direction
 * <li>2011 11 07 option Laplace added
 * <li>2014 01 28 option DoG added
 * 
 * @author Ahammer, Kainz
 * @since   2009 06
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpEdgeOpImage extends PointOpImage {

	private RenderedImage ri;
	private int method;
	private int kernelSize;
	private int direction;

	public IqmOpEdgeOpImage(RenderedImage ri, int method, int kernelSize,
			int direction, ImageLayout layout, RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.method = method;
		this.kernelSize = kernelSize;
		this.direction = direction;

	}

	@Override
	public Raster computeTile(int x, int y) {

		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
