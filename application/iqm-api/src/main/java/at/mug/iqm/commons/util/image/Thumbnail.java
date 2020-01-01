package at.mug.iqm.commons.util.image;

/*
 * #%L
 * Project: IQM - API
 * File: Thumbnail.java
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

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * This class represents a thumb nail, i.e. a resized version of the actual
 * image for various purposes.
 * 
 * @author Philipp Kainz
 * 
 */
public class Thumbnail extends ImageIcon implements Cloneable {
	private static final long serialVersionUID = 6320908079954312732L;

	/**
	 * Create a new thumbnail from a given image.
	 * 
	 * @param image
	 */
	public Thumbnail(Image image) {
		super(image);
	}

	public Thumbnail clone() {
		try {
			return (Thumbnail) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return this;
	}
}
