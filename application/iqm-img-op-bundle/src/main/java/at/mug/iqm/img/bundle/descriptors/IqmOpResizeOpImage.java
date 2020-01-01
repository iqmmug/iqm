package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpResizeOpImage.java
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
 * @since   2009 04
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpResizeOpImage extends PointOpImage {

	private RenderedImage ri;
	private float zoomX;
	private float zoomY;
	private int newWidth;
	private int newHeight;
	private int interP;
	private int zoomOrSize;

	public IqmOpResizeOpImage(RenderedImage ri, float zoomX, float zoomY,
			int newWidth, int newHeight, int interP, int zoomOrSize,
			ImageLayout layout, RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		this.newWidth = newWidth;
		this.newHeight = newHeight;
		this.interP = interP;
		this.zoomOrSize = zoomOrSize;
	}

	@Override
	public Raster computeTile(int x, int y) {
		// This method does nothing because the work is already done in
		// IqmResizeOperation
		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
