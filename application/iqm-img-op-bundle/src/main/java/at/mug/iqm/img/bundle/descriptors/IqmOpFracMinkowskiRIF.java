package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracMinkowskiRIF.java
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
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.ImageLayout;

/**
 * <li> 2012 01 04 added horizontal and vertical kernel shape
 * 
 * @author Ahammer
 * @since   2009 07
 * 
 */
public class IqmOpFracMinkowskiRIF implements RenderedImageFactory {

	public IqmOpFracMinkowskiRIF() {
	}

	@Override
	public RenderedImage create(ParameterBlock pb, RenderingHints hints) {

		RenderedImage source = pb.getRenderedSource(0);
		ImageLayout layout = new ImageLayout(source);
		// ImageLayout layout = RIFUtil.getImageLayoutHint(hints); //problems
		// for large images
		return new IqmOpFracMinkowskiOpImage(pb.getRenderedSource(0),
				pb.getIntParameter(0), pb.getIntParameter(1),
				pb.getIntParameter(2), pb.getIntParameter(3),
				pb.getIntParameter(4), pb.getIntParameter(5), layout, hints,
				false);
	}
}
