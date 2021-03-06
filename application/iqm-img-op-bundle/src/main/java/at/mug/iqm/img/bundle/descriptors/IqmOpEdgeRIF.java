package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpEdgeRIF.java
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
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.ImageLayout;


/**
 * <li>2010 09 added option: direction
 * <li>2011 11 07 option Laplace added
 * <li>2014 01 28 option DoG added
 * 
 * @author Ahammer, Kainz
 * @since   2009 06
 */
public class IqmOpEdgeRIF implements RenderedImageFactory {

	public IqmOpEdgeRIF() {
	}

	@Override
	public RenderedImage create(ParameterBlock pb, RenderingHints hints) {

		RenderedImage source = pb.getRenderedSource(0);
		ImageLayout layout = new ImageLayout(source);
		// ImageLayout layout = RIFUtil.getImageLayoutHint(hints); //problems
		// for large images
		return new IqmOpEdgeOpImage(pb.getRenderedSource(0),
				pb.getIntParameter(0), pb.getIntParameter(1),
				pb.getIntParameter(2), layout, hints, false);
	}
}
