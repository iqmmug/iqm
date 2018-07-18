package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpStatisticsOpImage.java
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
 * @since   2009 06
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpStatisticsOpImage extends PointOpImage {

	private RenderedImage ri;

	private int binary;
	private int order1;
	private int order2;
	private int range;
	private int distance;
	private int direction;
	private int background;

	public IqmOpStatisticsOpImage(RenderedImage ri, int binary, int order1,
			int order2, int range, int distance, int direction, int background,
			ImageLayout layout, RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.binary = binary;
		this.order1 = order1;
		this.order2 = order2;
		this.range = range;
		this.distance = distance;
		this.direction = direction;
		this.background = background;
	}

	@Override
	public Raster computeTile(int x, int y) {
		// This method does nothing because the work is already done in
		// IqmStatisticsOperation
		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
