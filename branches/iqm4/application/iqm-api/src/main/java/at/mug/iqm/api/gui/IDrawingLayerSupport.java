package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: IDrawingLayerSupport.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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

import java.util.List;

import javax.media.jai.ROIShape;

import at.mug.iqm.api.gui.roi.AngleROI;
import at.mug.iqm.api.gui.roi.EllipseROI;
import at.mug.iqm.api.gui.roi.FreehandROI;
import at.mug.iqm.api.gui.roi.LineROI;
import at.mug.iqm.api.gui.roi.PointROI;
import at.mug.iqm.api.gui.roi.PolygonROI;
import at.mug.iqm.api.gui.roi.RectangleROI;

/**
 * This interface provides methods for drawing regions of interest (ROIs) on a
 * {@link IDrawingLayer}.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IDrawingLayerSupport {

	/**
	 * Draw a given list of generic {@link ROIShape}s.
	 * 
	 * @param rois
	 *            the list to be drawn, shapes may be of different types
	 */
	void drawROIs(List<ROIShape> rois);

	/**
	 * Draw a given generic {@link ROIShape}.
	 * 
	 * @param roi
	 *            the shape to be drawn, shapes may be of different types
	 */
	void drawROI(ROIShape roi);

	/**
	 * Draw a list of {@link LineROI}s.
	 * 
	 * @param rois
	 */
	void drawLineROIs(List<LineROI> rois);

	/**
	 * Draw a list of {@link RectangleROI}s.
	 * 
	 * @param rois
	 */
	void drawRectangleROIs(List<RectangleROI> rois);

	/**
	 * Draw a list of {@link EllipseROI}s.
	 * 
	 * @param rois
	 */
	void drawEllipseROIs(List<EllipseROI> rois);

	/**
	 * Draw a list of {@link FreehandROI}s.
	 * 
	 * @param rois
	 */
	void drawFreehandROIs(List<FreehandROI> rois);

	/**
	 * Draw a list of {@link AngleROI}s.
	 * 
	 * @param rois
	 */
	void drawAngleROIs(List<AngleROI> rois);

	/**
	 * Draw a list of {@link PolygonROI}s.
	 * 
	 * @param rois
	 */
	void drawPolygonROIs(List<PolygonROI> rois);

	/**
	 * Draw a list of {@link PointROI}s.
	 * 
	 * @param rois
	 */
	void drawPointROIs(List<PointROI> rois);

	/**
	 * Draw a single {@link LineROI}.
	 * 
	 * @param roi
	 */
	void drawLineROI(LineROI roi);

	/**
	 * Draw a single {@link AngleROI}.
	 * 
	 * @param roi
	 */
	void drawAngleROI(AngleROI roi);

	/**
	 * Draw a single {@link EllipseROI}.
	 * 
	 * @param roi
	 */
	void drawEllipseROI(EllipseROI roi);

	/**
	 * Draw a single {@link FreehandROI}.
	 * 
	 * @param roi
	 */
	void drawFreehandROI(FreehandROI roi);

	/**
	 * Draw a single {@link PolygonROI}.
	 * 
	 * @param roi
	 */
	void drawPolygonROI(PolygonROI roi);

	/**
	 * Draw a single {@link RectangleROI}.
	 * 
	 * @param roi
	 */
	void drawRectangleROI(RectangleROI roi);

	/**
	 * Draw a single {@link PointROI}.
	 * 
	 * @param roi
	 */
	void drawPointROI(PointROI roi);

	/**
	 * Deletes a given ROI shape.
	 * 
	 * @param roi
	 */
	void deleteROI(ROIShape roi);

	/**
	 * Deletes all given shapes from the layer.
	 * 
	 * @param rois
	 */
	void deleteROIs(List<ROIShape> rois);

	/**
	 * Delete all {@link ROIShape}s on this layer.
	 */
	void deleteAllROIs();

	/**
	 * Delete the selected {@link ROIShape} on this layer.
	 */
	void deleteSelectedROI();

}
