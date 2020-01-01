package at.mug.iqm.api.gui.roi;

/*
 * #%L
 * Project: IQM - API
 * File: EllipseROI.java
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
 * An {@link EllipseROI} represents an elliptic (or circular) shape having an
 * area.
 * 
 * @author Philipp Kainz
 * 
 */
public class EllipseROI extends AbstractROIShape implements AreaEnabledROI {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -8623192194397786208L;

	/**
	 * The 2D surface area of this shape.
	 */
	private double area = 0.0d;

	/**
	 * A public constructor using a {@link Shape} object.
	 * 
	 * @param s
	 */
	public EllipseROI(Shape s) {
		super(s);
		this.calculateArea();
	}

	/**
	 * A public constructor using an {@link Area} object.
	 * 
	 * @param a
	 */
	public EllipseROI(Area a) {
		super(a);
		this.calculateArea();
	}

	/**
	 * Gets the current area of this elliptic shape.
	 * 
	 * @return the area in double precision
	 */
	public double getArea() {
		return area;
	}

	/**
	 * Sets the current area of this elliptic shape.
	 * 
	 * @param area
	 */
	public void setArea(double area) {
		this.area = area;
	}

	/**
	 * Calculates the area of this elliptic shape.
	 * 
	 * @return the area in double precision
	 */
	public double calculateArea() {
		this.area = Math.PI * getSemiMajorAxis() * getSemiMinorAxis();
		return this.area;
	}
	
	public double getSemiMajorAxis(){
		return this.getBounds().getWidth()/2.0d;
	}
	
	public double getSemiMinorAxis(){
		return this.getBounds().getHeight()/2.0d;
	}
	
	public double getMajorAxis(){
		return this.getBounds().getWidth();
	}
	
	public double getMinorAxis(){
		return this.getBounds().getHeight();
	}

	@Override
	public String toString() {
		String str = "";

		str += this.getClass() + " - Bounds=";
		str += this.getBounds() + " - Area=";
		str += this.getArea() + "px^2";

		return str;
	}

}
