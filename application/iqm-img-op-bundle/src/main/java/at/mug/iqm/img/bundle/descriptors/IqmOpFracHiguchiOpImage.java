package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracHiguchiOpImage.java
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
 * <li> 2011 05 added new options radial and spiral 
 * <li> 2011 08 added new option Single line ROI
 * @author Ahammer
 * @since   2010 04
 * 
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpFracHiguchiOpImage extends PointOpImage {

	private RenderedImage ri;
	private int numK;
	private int optAppend;
	private int optBack;
	private int optDx;
	private int optDy;
	private int regStart;
	private int regEnd;
	private int optShowPlot;
	private int optDeleteExistingPlot;

	public IqmOpFracHiguchiOpImage(RenderedImage ri, int numK, int optAppend,
			int optBack, int optDx, int optDy, int regStart, int regEnd,
			int optShowPlot, int optDeleteExistingPlot, ImageLayout layout,
			RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.numK = numK;
		this.optAppend = optAppend;
		this.optBack = optBack;
		this.optDx = optDx;
		this.optDy = optDy;
		this.regStart = regStart;
		this.regEnd = regEnd;
		this.optShowPlot = optShowPlot;
		this.optDeleteExistingPlot = optDeleteExistingPlot;

	}

	@Override
	public Raster computeTile(int x, int y) {
		// This method does nothing because the work is already done in
		// IqmFracHiguchiOperation
		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
