package at.mug.iqm.img.bundle.descriptors;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpInvertRIF.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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
 * @author Ahammer
 * @since 2009 03
 */
public class IqmOpInvertRIF implements RenderedImageFactory {

	public IqmOpInvertRIF() {
	}

	@Override
	public RenderedImage create(ParameterBlock pb, RenderingHints hints) {
		RenderedImage source = pb.getRenderedSource(0);
		ImageLayout layout = new ImageLayout(source);
		// ImageLayout layout = RIFUtil.getImageLayoutHint(hints); //problems
		// for large images

		// call the function and return a rendered image to the caller
		return new IqmOpInvertOpImage(source, pb.getIntParameter(0), layout,
				hints, false);

	}
}
