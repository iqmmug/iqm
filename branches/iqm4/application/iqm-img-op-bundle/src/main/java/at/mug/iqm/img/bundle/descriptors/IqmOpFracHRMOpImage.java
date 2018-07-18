package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracHRMOpImage.java
 * 
 * $Id: IqmOpFracHRMOpImage.java 548 2016-01-18 09:36:47Z kainzp $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/descriptors/IqmOpFracHRMOpImage.java $
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
 * @since  2016 05
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpFracHRMOpImage extends PointOpImage {

	private RenderedImage ri;
	private int width;
	private int height;
	private int itMax;
	private int numPoly;
	private int fracType;

	public IqmOpFracHRMOpImage(RenderedImage ri, int width, int height,
			int itMax, int numPoly, int fracType, ImageLayout layout,
			RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);

		this.width = width;
		this.height = height;
		this.itMax = itMax;
		this.numPoly = numPoly;
		this.fracType = fracType;
	}

	@Override
	public Raster computeTile(int x, int y) {
		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
