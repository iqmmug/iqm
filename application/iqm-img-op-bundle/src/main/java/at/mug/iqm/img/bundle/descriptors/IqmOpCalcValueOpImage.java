package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpCalcValueOpImage.java
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
 * @since 2009 05
 */
@SuppressWarnings({ "unchecked", "unused" })
public class IqmOpCalcValueOpImage extends PointOpImage {

	private RenderedImage ri1;
	private int calc;
	private double value;
	private int resultOptions;

	public IqmOpCalcValueOpImage(RenderedImage ri1, int calc, double value,
			int resultOptions, ImageLayout layout, RenderingHints hints,
			boolean b) {
		super(ri1, layout, hints, b);
		this.ri1 = ri1;
		this.calc = calc;
		this.value = value;
		this.resultOptions = resultOptions;
	}

	@Override
	public Raster computeTile(int x, int y) {
		Raster r = this.ri1.getTile(x, y);
		return r;
	}
}
