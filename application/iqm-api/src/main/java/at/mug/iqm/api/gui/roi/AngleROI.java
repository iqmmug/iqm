package at.mug.iqm.api.gui.roi;

/*
 * #%L
 * Project: IQM - API
 * File: AngleROI.java
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

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import at.mug.iqm.commons.util.CommonTools;

/**
 * An angle ROI has two legs, and an enclosing and complementary angle in
 * degrees.
 * 
 * @author Philipp Kainz
 * 
 */
public class AngleROI extends AbstractROIShape {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 1399335925642401922L;

	/**
	 * The length of the first leg.
	 */
	private double lengthFirstLeg = 0.0d;

	/**
	 * The length of the second leg.
	 */
	private double lengthSecondLeg = 0.0d;

	/**
	 * The angle in degree.
	 */
	private double angle = 0.0d;

	/**
	 * The complementary angle in degree.
	 */
	private double complementaryAngle = 0.0d;

	/**
	 * A public constructor using a {@link Shape} object.
	 * 
	 * @param s
	 */
	public AngleROI(Shape s) {
		super(s);
		calculateProperties();
	}

	/**
	 * Constructs an angle ROI for a triple of {@link Point2D} objects.
	 * 
	 * @param p1
	 *            start point
	 * @param p2
	 *            apex
	 * @param p3
	 *            end point
	 * @return a new {@link AngleROI} with angle and leg length properties
	 */
	public static AngleROI create(Point2D start, Point2D apex, Point2D end) {
		GeneralPath gp = new GeneralPath();
		gp.moveTo(start.getX(), start.getY());
		gp.lineTo(apex.getX(), apex.getY());
		gp.lineTo(end.getX(), end.getY());
		return new AngleROI(gp);
	}

	/**
	 * Hidden private constructor.
	 * 
	 * @param a
	 */
	private AngleROI(Area a) {
		super(a);
		throw new IllegalArgumentException(
				"Creating an angle ROI from an area is not possible!");
	}

	/**
	 * @return the lengthFirstLeg
	 */
	public double getLengthFirstLeg() {
		return lengthFirstLeg;
	}

	/**
	 * @param lengthFirstLeg
	 *            the lengthFirstLeg to set
	 */
	public void setLengthFirstLeg(double lengthFirstLeg) {
		this.lengthFirstLeg = lengthFirstLeg;
	}

	/**
	 * @return the lengthSecondLeg
	 */
	public double getLengthSecondLeg() {
		return lengthSecondLeg;
	}

	/**
	 * @param lengthSecondLeg
	 *            the lengthSecondLeg to set
	 */
	public void setLengthSecondLeg(double lengthSecondLeg) {
		this.lengthSecondLeg = lengthSecondLeg;
	}

	/**
	 * @return the angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @param angle
	 *            the angle to set
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * @return the complementaryAngle
	 */
	public double getComplementaryAngle() {
		return complementaryAngle;
	}

	/**
	 * @param complementaryAngle
	 *            the complementaryAngle to set
	 */
	public void setComplementaryAngle(double complementaryAngle) {
		this.complementaryAngle = complementaryAngle;
	}

	/**
	 * Computes and sets the properties of this shape.
	 */
	public void calculateProperties() {
		double[] vals = CommonTools.calcAngleAndLegLenghts(this);
		setAngle(vals[0]);
		setComplementaryAngle(vals[1]);
		setLengthFirstLeg(vals[2]);
		setLengthSecondLeg(vals[3]);
	}

	/**
	 * Gets the 2D coordinates of the apex.
	 * 
	 * @return the coordinates
	 */
	public Point2D getApex() {
		PathIterator iterator = getPathIterator();
		double[] coords = new double[6];
		int ptCnt = 0;
		while (!iterator.isDone()) {
			coords = new double[6];
			if (ptCnt == 1) {
				iterator.currentSegment(coords);
				break;
			}
			ptCnt++;
			iterator.next();
		}
		return new Point2D.Double(coords[0], coords[1]);
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();

		s.append(super.toString());
		s.append(", length first leg=");
		s.append(this.lengthFirstLeg);
		s.append(", length second leg=");
		s.append(this.lengthSecondLeg);
		s.append(", angle=");
		s.append(this.angle);
		s.append(" degree, complementary angle=");
		s.append(this.complementaryAngle);
		s.append(" degree");

		return s.toString();
	}

}
