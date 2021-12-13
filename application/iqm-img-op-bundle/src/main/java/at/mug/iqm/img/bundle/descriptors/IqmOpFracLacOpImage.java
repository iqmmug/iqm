package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracLacOpImage.java
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

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

/**
 * @author Ahammer
 * @since   2011 04
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpFracLacOpImage extends PointOpImage {

	private RenderedImage ri;
	private int numMaxEps;
	private int method;
	private int regStart;
	private int regEnd;
	private int optShowPlot;
	private int optDeleteExistingPlot;

	public IqmOpFracLacOpImage(RenderedImage ri, int numMaxEps, int method,
			int regStart, int regEnd, int optShowPlot,
			int optDeleteExistingPlot, ImageLayout layout,
			RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.numMaxEps = numMaxEps;
		this.method = method;
		this.regStart = regStart;
		this.regEnd = regEnd;
		this.optShowPlot = optShowPlot;
		this.optDeleteExistingPlot = optDeleteExistingPlot;
	}

	@Override
	public Raster computeTile(int x, int y) {
		// This method does nothing because the work is already done in
		// IqmFracLacOperation
		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
