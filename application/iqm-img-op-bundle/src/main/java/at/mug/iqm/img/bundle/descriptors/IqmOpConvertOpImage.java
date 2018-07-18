package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpConvertOpImage.java
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
import java.awt.image.WritableRaster;

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

/**
 * <p>
 * <b>Changes</b>
 * <ul>
 * 	<li>2010 01 RGBtoHSV, RGBtoHLS,HSVtoRGB, HLStoRGB 
 * </ul>
 * @author Ahammer
 * @since 2009 03
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpConvertOpImage extends PointOpImage {

	private RenderedImage ri;
	private WritableRaster wRast;
	private int numBands;
	private int pixelSize;
	private int opt8bit;
	private int opt16bit;
	private int optPalette;
	private int optRGB;
	private int optRGBa;
	private String type;

	public IqmOpConvertOpImage(RenderedImage ri, int opt8bit, int opt16bit,
			int optPalette, int optRGB, int optRGBa, ImageLayout layout,
			RenderingHints hints, boolean b) {
		super(ri, layout, hints, b);
		// permitInPlaceOperation(); schneller ???
		this.ri = ri;
	}

	@Override
	public Raster computeTile(int x, int y) {
		// This method does nothing because the work is already done in
		// IqmConvertOperation
		Raster r = this.ri.getTile(x, y);
		return r;
	}
}
