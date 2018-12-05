package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpGenEntOpImage.java
 * 
 * $Id: IqmOpGenEntOpImage.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/descriptors/IqmOpGenEntOpImage.java $
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
 * @since  2018-12-04
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpGenEntOpImage extends PointOpImage {

	private RenderedImage ri;
	private int minQ;
	private int maxQ;
	private int numEps;
	private int maxEps;
	private int methodEps;
	private int method;
	private int regStart;
	private int regEnd;
	private int optShowPlot;
	private int optDeleteExistingPlot;
	private int optShowPlotDq;
	private int optShowPlotF;

	public IqmOpGenEntOpImage(RenderedImage ri, int minQ, int maxQ,
			int numEps, int maxEps, int methodEps, int method, int regStart,
			int regEnd, int optShowPlot, int optDeleteExistingPlot,
			int optShowPlotDq, int optShowPlotF, ImageLayout layout,
			RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		this.ri = ri;
		this.minQ = minQ;
		this.maxQ = maxQ;
		this.numEps = numEps;
		this.maxEps = maxEps;
		this.method = method;
		this.methodEps = methodEps;
		this.regStart = regStart;
		this.regEnd = regEnd;
		this.optShowPlot = optShowPlot;
		this.optDeleteExistingPlot = optDeleteExistingPlot;
		this.optShowPlotDq = optShowPlotDq;
		this.optShowPlotF = optShowPlotF;
	}

	@Override
	public Raster computeTile(int x, int y) {
		// This method does nothing because the work is already done in
		// IqmGenEntOperation
		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
