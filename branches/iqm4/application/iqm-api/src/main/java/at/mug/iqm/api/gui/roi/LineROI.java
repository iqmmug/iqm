package at.mug.iqm.api.gui.roi;

/*
 * #%L
 * Project: IQM - API
 * File: LineROI.java
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

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import at.mug.iqm.commons.util.CommonTools;

/**
 * A {@link LineROI} represents a straight line between two points.
 * 
 * @author Philipp Kainz
 * 
 */
public class LineROI extends AbstractROIShape {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 709303692320603616L;

	/**
	 * The length of the object.
	 */
	private double length = 0.0d;

	/**
	 * The start point of the line.
	 */
	private Point2D startPoint = new Point2D.Double();

	/**
	 * The end point of the line.
	 */
	private Point2D endPoint = new Point2D.Double();

	/**
	 * A public constructor using a {@link Shape} object.
	 * 
	 * @param s
	 */
	public LineROI(Shape s) {
		super(s);
		calculateLength();
		calculateEndpoints();
	}

	/**
	 * A private constructor using an {@link Area} object. NOTE: this
	 * constructor should not be used for a line object.
	 * 
	 * @param a
	 */
	private LineROI(Area a) {
		super(a);
		throw new IllegalArgumentException(
				"Creating a line from an area is not possible!");
	}

	/**
	 * Constructs a new line using start and end point.
	 * 
	 * @param start
	 * @param end
	 */
	public LineROI(Point2D start, Point2D end) {
		this(new Line2D.Double(start, end));
	}

	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(double length) {
		this.length = length;
	}

	/**
	 * Calculates and sets the length of this shape.
	 */
	public void calculateLength() {
		setLength(CommonTools.calcLength(CommonTools.getShapeCoordinates(this)));
	}

	/**
	 * Calculate the start and end point of the segment.
	 */
	public void calculateEndpoints() {
		Object s = getAsShape();
		if (s instanceof Line2D) {
			System.out.println("SHAPE IS LINE2D");
			Line2D.Double line = (Line2D.Double) getAsShape();
			setStartPoint(line.getP1());
			setEndPoint(line.getP2());
		} else if (s instanceof Path2D) {
			System.out.println("SHAPE IS PATH2D");
			Path2D path = (Path2D) getAsShape();
			PathIterator pathIterator = path.getPathIterator(null);

			// get the two points from the segment
			for (int i = 0; i < 2; i++) {
				double[] coordinates = new double[6];
				int type = pathIterator.currentSegment(coordinates);
				switch (type) {
				// the first point
				case PathIterator.SEG_MOVETO:
					setStartPoint(new Point2D.Double(coordinates[0],
							coordinates[1]));
					break;
				// the second point
				case PathIterator.SEG_LINETO:
					setEndPoint(new Point2D.Double(coordinates[0],
							coordinates[1]));
					break;
				default:
					break;
				}
				pathIterator.next();
			}
		}
	}

	public void setStartPoint(Point2D startPoint) {
		this.startPoint = startPoint;
	}

	public Point2D getStartPoint() {
		return startPoint;
	}

	public void setEndPoint(Point2D endPoint) {
		this.endPoint = endPoint;
	}

	public Point2D getEndPoint() {
		return endPoint;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();

		s.append(super.toString());
		s.append(", Length=");
		s.append(this.length);

		s.append(", start=(");
		s.append(startPoint.getX() + ", " + this.startPoint.getY() + ")");
		s.append(", end=(");
		s.append(this.endPoint.getX() + ", " + this.endPoint.getY() + ")");

		return s.toString();
	}

}
