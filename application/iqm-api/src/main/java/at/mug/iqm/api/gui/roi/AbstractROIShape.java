package at.mug.iqm.api.gui.roi;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractROIShape.java
 * 
 * $Id$
 * $HeadURL$
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

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.media.jai.ROIShape;

public abstract class AbstractROIShape extends ROIShape implements
		ISerializableROI {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -8147634630710468070L;

	public AbstractROIShape(Shape s) {
		super(s);
	}

	public AbstractROIShape(Area a) {
		super(a);
	}

	/**
	 * Converts the segment code to a string.
	 * 
	 * @param segmentCode
	 * @return
	 */
	public static String segmentCodeToString(int segmentCode) {
		// get the name of the variable
		switch (segmentCode) {
		case PathIterator.SEG_MOVETO:
			return "SEG_MOVETO";

		case PathIterator.SEG_LINETO:
			return "SEG_LINETO";

		case PathIterator.SEG_CUBICTO:
			return "SEG_CUBICTO";

		case PathIterator.SEG_QUADTO:
			return "SEG_QUADTO";

		case PathIterator.SEG_CLOSE:
			return "SEG_CLOSE";

		default:
			return "unkown";
		}
	}

	/**
	 * Converts the winding rule code to a readable string.
	 * 
	 * @param windCode
	 * @return
	 */
	public static String windingRuleToString(int windCode) {
		// get the name of the variable
		switch (windCode) {
		case PathIterator.WIND_EVEN_ODD:
			return "WIND_EVEN_ODD";

		case PathIterator.WIND_NON_ZERO:
			return "WIND_NON_ZERO";

		default:
			return "unkown";
		}
	}

	public boolean isClosed() {
		PathIterator iterator = this.getAsShape().getPathIterator(null);
		while (!iterator.isDone()) {
			if (iterator.currentSegment(new float[6]) == AbstractROIShape.SEG_CLOSE) {
				return true;
			} else {
				iterator.next();
			}
		}
		return false;
	}

	/**
	 * Get the first segment and return the start point.
	 * 
	 * @return the 2D coordinates of the start point
	 */
	public Point2D getStartPoint() {
		PathIterator iterator = getPathIterator();
		double[] coords = new double[6];
		iterator.currentSegment(coords);
		return new Point2D.Double(coords[0], coords[1]);
	}

	/**
	 * Returns the coordinates of the last path segment.
	 * 
	 * @return the 2D coordinates
	 */
	public Point2D getEndPoint() {
		PathIterator iterator = getPathIterator();
		double[] coords = new double[6];
		// loop through all segments
		while (!iterator.isDone()) {
			iterator.next();
		}

		iterator.currentSegment(coords);

		return new Point2D.Double(coords[0], coords[1]);
	}

	/**
	 * Returns the center point of the boundary rectangle.
	 * 
	 * @return the coordinates
	 */
	public Point2D getBoundaryCenter() {
		Rectangle2D r = getBounds2D();
		return new Point2D.Double(r.getCenterX(), r.getCenterY());
	}

	@Override
	public PathIterator getPathIterator() {
		return this.getAsShape().getPathIterator(null);
	}
}
