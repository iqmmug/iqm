package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpAffineOpImage.java
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

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

/**
 * @author Ahammer
 * @since 2009 06
 */
@SuppressWarnings({ "unused", "unchecked" })
public class IqmOpAffineOpImage extends PointOpImage {

	private RenderedImage ri;
	private float m00;
	private float m10;
	private float m01;
	private float m11;
	private float m02;
	private float m12;
	private int interpolation;

	public IqmOpAffineOpImage(RenderedImage ri, float m00, float m10,
			float m01, float m11, float m02, float m12, int interpolation,
			ImageLayout layout, RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.m00 = m00;
		this.m10 = m10;
		this.m01 = m01;
		this.m11 = m11;
		this.m02 = m02;
		this.m12 = m12;
		this.interpolation = interpolation;

	}

	@Override
	public Raster computeTile(int x, int y) {
		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
