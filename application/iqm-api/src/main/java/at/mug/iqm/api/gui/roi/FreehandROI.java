package at.mug.iqm.api.gui.roi;

/*
 * #%L
 * Project: IQM - API
 * File: FreehandROI.java
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


import java.awt.Shape;
import java.awt.geom.Area;

/**
 * A {@link FreehandROI} represents an arbitrary shape, which may or may not
 * follow certain geometric laws.
 * 
 * @author Philipp Kainz
 * 
 */
public class FreehandROI extends AbstractROIShape {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 3457522401275954959L;
	
	/**
	 * A public constructor using a {@link Shape} object.
	 * @param s
	 */
	public FreehandROI(Shape s) {
		super(s);
	}
	
	/**
	 * A public constructor using an {@link Area} object.
	 * @param a
	 */
	public FreehandROI(Area a) {
		super(a);
	}
}
