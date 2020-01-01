package at.mug.iqm.api.gui.roi;

/*
 * #%L
 * Project: IQM - API
 * File: PointROI.java
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

import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.media.jai.ROIShape;

/**
 * This class represents a {@link ROIShape}, drawn as circle of dimensions
 * <code>3x3</code> and origin at {@link Point} <code>O(p.x-1|p.y-1)</code>.
 * 
 * @author Philipp Kainz
 * 
 */
public class PointROI extends AbstractROIShape {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 1278634522234941418L;

	/**
	 * The center of this ROI shape.
	 */
	private Point2D location;

	/**
	 * A public constructor.
	 */
	public PointROI(Point location) {
		super(new Ellipse2D.Double(location.x - 1, location.y - 1, 3, 3));
		this.location = new Point2D.Double((double) location.x,
				(double) location.y);
	}

	/**
	 * A public constructor.
	 */
	public PointROI(Point2D location) {
		super(new Ellipse2D.Double(location.getX() - 1, location.getY() - 1, 3,
				3));
		this.location = location;
	}
	
	/**
	 * A public constructor.
	 */
	public PointROI(double x, double y) {
		super(new Ellipse2D.Double(x - 1, y - 1, 3,
				3));
		this.location = new Point2D.Double(x, y);
	}

	@Override
	public String toString() {
		String str = "";

		str += this.getClass() + " - Bounds=";
		str += this.getBounds() + " - Point=";
		str += this.location;

		return str;
	}

	/**
	 * Gets the location of this ROI shape.
	 * 
	 * @return the location of this ROI shape
	 */
	public Point2D getLocation() {
		return location;
	}

	/**
	 * Sets the location of this ROI shape.
	 * 
	 * @param location
	 */
	public void setLocation(Point location) {
		this.location = location;
	}

	@Override
	public Point2D getStartPoint() {
		return new Point2D.Double(this.location.getX(), this.location.getY());
	}

	@Override
	public Point2D getEndPoint() {
		return getStartPoint();
	}

}
