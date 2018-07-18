package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: IDrawingLayer.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.media.jai.ROIShape;
import javax.swing.JLayeredPane;

import at.mug.iqm.api.gui.roi.AngleROI;
import at.mug.iqm.api.gui.roi.EllipseROI;
import at.mug.iqm.api.gui.roi.FreehandROI;
import at.mug.iqm.api.gui.roi.LineROI;
import at.mug.iqm.api.gui.roi.PointROI;
import at.mug.iqm.api.gui.roi.PolygonROI;
import at.mug.iqm.api.gui.roi.RectangleROI;
import at.mug.iqm.api.gui.roi.events.handler.ROILayerCursorChanger;

/**
 * This interface declares methods for a layer where the user may draw regions
 * of interest (ROIs) of various shapes.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IDrawingLayer extends IDrawingLayerSupport {
	/**
	 * This constant defines the default position of the image layer.
	 */
	public static final Integer DEFAULT_IMAGE_LAYER = JLayeredPane.DEFAULT_LAYER;

	/**
	 * This constant defines the layer where the topmost component goes.
	 */
	public static final Integer ACTIVE_FRONT_LAYER = new Integer(
			Integer.MAX_VALUE - 500);

	/**
	 * The dragging layer in this layer, where the mouse dragged gestures are
	 * drawn.
	 */
	public static final Integer DRAGGING_LAYER = new Integer(Integer.MAX_VALUE);
	
	/**
	 * The variable name of the default layer color.
	 */
	public static final String DEFAULT_LAYER_COLOR_NAME = "DEFAULT_LAYER_COLOR";
	/**
	 * The standard color for a new layer.
	 */
	public static final Color DEFAULT_LAYER_COLOR = Color.RED;

	/**
	 * The variable name of the default layer stroke.  
	 */
	public static final String DEFAULT_LAYER_STROKE_NAME = "DEFAULT_LAYER_STROKE";
	/**
	 * The standard stroke of the layer.
	 */
	public static final BasicStroke DEFAULT_LAYER_STROKE = new BasicStroke(1.5F);
	
	/**
	 * The variable name of the highlight layer color.
	 */
	public static final String HIGHLIGHT_COLOR_NAME = "HIGHLIGHT_COLOR";
	/**
	 * The color for highlighting a ROI shape on the layer.
	 */
	public static final Color HIGHLIGHT_COLOR = Color.white;
	
	/**
	 * The variable name of the stroke for highlighting a shape.  
	 */
	public static final String HIGHLIGHT_STROKE_NAME = "HIGHLIGHT_STROKE";
	/**
	 * The standard stroke for highlighting a shape on the layer.
	 */
	public static final BasicStroke HIGHLIGHT_STROKE = new BasicStroke(1.0F);
	
	/**
	 * Determine the name of the layer which is shown in the selection panel.
	 * 
	 * @param name
	 *            a unique name of the layer
	 */
	void setName(String name);

	/**
	 * Get the name of the layer.
	 * 
	 * @return the unique name of the layer
	 */
	String getName();

	/**
	 * The order of the layer in z-direction. Values <code>&gt;0</code> are
	 * permitted.
	 * 
	 * @return an {@link Integer} indicating the layer's order in a stack of
	 *         layers
	 */
	int getZOrder();

	/**
	 * Determine the order of the layer in z-direction. Values
	 * <code>&gt;0</code> are permitted.
	 * 
	 * @param order
	 *            an {@link Integer} indicating the layer's order in a stack of
	 *            layers
	 */
	void setZOrder(int order);

	/**
	 * A flag whether or not the layer is visible in the canvas.
	 * 
	 * @return <code>true</code>, if the layer's components are drawn on the
	 *         canvas, <code>false</code> otherwise
	 */
	boolean isLayerVisible();

	/**
	 * Set a flag determining whether or not the layer is visible in the canvas.
	 * 
	 * @param visible
	 *            set to <code>true</code>, if the layer's components should be
	 *            drawn on the canvas, <code>false</code> otherwise
	 */
	void setLayerVisible(boolean visible);

	/**
	 * Set the color for ROIs on this layer.
	 * 
	 * @param color
	 */
	void setLayerColor(Color color);

	/**
	 * Get the color for ROIs on this layer.
	 * 
	 * @return the color
	 */
	Color getLayerColor();

	/**
	 * Set the selection state of this layer.
	 * 
	 * @param selected
	 * 
	 */
	void setSelected(boolean selected);

	/**
	 * Get the selection state of this layer.
	 * 
	 * @return <code>true</code> if the layer is selected, <code>false</code>
	 *         otherwise
	 */
	boolean isSelected();

	/**
	 * Gets the total amount of elements in this layer.
	 * 
	 * @return an integer value indicating the number of elements
	 */
	int getElementCount();

	/**
	 * Determines whether or not the layer is empty.
	 * 
	 * @return a flag
	 */
	boolean isEmpty();

	// components of a drawing layer are lists of ROI shapes
	// ANGLE ROI
	/**
	 * Set all {@link AngleROI}s in this layer.
	 * 
	 * @param angleROIs
	 */
	void setAngleROIs(ArrayList<AngleROI> angleROIs);

	/**
	 * Get all {@link AngleROI}s drawn in this layer.
	 * 
	 * @return all angleROIs
	 */
	ArrayList<AngleROI> getAngleROIs();

	/**
	 * A helper variable used by mouse event handlers to draw {@link AngleROI}s.
	 * 
	 * @param numAnglePoints
	 */
	void setNumAnglePoints(int numAnglePoints);

	/**
	 * A helper variable used by mouse event handlers to draw {@link AngleROI}s.
	 * 
	 * @return the number of currently drawn angle points
	 */
	int getNumAnglePoints();

	/**
	 * A helper variable used by mouse event handlers to draw {@link AngleROI}s.
	 * 
	 * @param angleShape
	 *            the shape of the angle, usually 2 legs, determined by 3 points
	 */
	void setAngleShape(GeneralPath angleShape);

	/**
	 * A helper variable used by mouse event handlers to draw {@link AngleROI}s.
	 * 
	 * @return the shape of the angle, usually 2 legs, determined by 3 points
	 */
	GeneralPath getAngleShape();

	/**
	 * Set the current {@link AngleROI} in this layer. The current
	 * {@link AngleROI} will be highlighted first on editing ROIs.
	 * 
	 * @param currentAngleROI
	 *            an {@link AngleROI}
	 */
	void setCurrentAngleROI(AngleROI currentAngleROI);

	/**
	 * Get the current {@link AngleROI} in this layer.
	 * 
	 * @return the current {@link AngleROI}
	 */
	AngleROI getCurrentAngleROI();

	// POLYGON ROI
	/**
	 * Set all {@link PolygonROI}s in this layer. This will overwrite any
	 * existing {@link PolygonROI}s.
	 * 
	 * @param polygonROIs
	 */
	void setPolygonROIs(ArrayList<PolygonROI> polygonROIs);

	/**
	 * Get all drawn {@link PolygonROI}s in this layer.
	 * 
	 * @return a list of {@link PolygonROI}s in this layer.
	 */
	ArrayList<PolygonROI> getPolygonROIs();

	/**
	 * Set the current {@link PolygonROI}.
	 * 
	 * @param currentPolygonROI
	 */
	void setCurrentPolygonROI(PolygonROI currentPolygonROI);

	/**
	 * Get the current {@link PolygonROI} in this layer.
	 * 
	 * @return the selected or latest {@link PolygonROI}, or <code>null</code>
	 *         if there is no {@link PolygonROI} present
	 */
	PolygonROI getCurrentPolygonROI();

	/**
	 * Sets the shape of the latest {@link PolygonROI}.
	 * 
	 * @param polygonShape
	 */
	void setPolygonShape(GeneralPath polygonShape);

	/**
	 * Gets the shape of the latest {@link PolygonROI}.
	 * 
	 * @return the shape or <code>null</code>, if there is no {@link PolygonROI}
	 */
	GeneralPath getPolygonShape();

	/**
	 * Set the number of {@link PolygonROI} corners of the currently drawn
	 * polygon ROI.
	 * 
	 * @param numPolyPoints
	 */
	void setNumPolyPoints(int numPolyPoints);

	/**
	 * Gets the number of corners of the currently drawn {@link PolygonROI}.
	 * 
	 * @return the number of polygon points
	 */
	int getNumPolyPoints();

	// FREEHAND ROI
	/**
	 * Set all {@link FreehandROI}s in this layer. This will overwrite any
	 * existing {@link FreehandROI}s.
	 * 
	 * @param freehandROIs
	 */
	void setFreehandROIs(ArrayList<FreehandROI> freehandROIs);

	/**
	 * Get all drawn {@link FreehandROI}s in this layer.
	 * 
	 * @return a list of {@link FreehandROI}s in this layer.
	 */
	ArrayList<FreehandROI> getFreehandROIs();

	/**
	 * Set the current {@link FreehandROI}.
	 * 
	 * @param currentFreehandROI
	 */
	void setCurrentFreehandROI(FreehandROI currentFreehandROI);

	/**
	 * Get the current {@link FreehandROI} in this layer.
	 * 
	 * @return the selected or latest {@link FreehandROI}, or <code>null</code>
	 *         if there is no {@link FreehandROI} present
	 */
	FreehandROI getCurrentFreehandROI();

	/**
	 * Set the path of the latest shape.
	 * 
	 * @param freehandShape
	 */
	void setFreehandShape(GeneralPath freehandShape);

	/**
	 * Gets the path of the latest drawn shape.
	 * 
	 * @return the path or <code>null</code>
	 */
	GeneralPath getFreehandShape();

	// ELLIPSE ROI
	/**
	 * Set all {@link EllipseROI}s in this layer. This will overwrite the entire
	 * list of ellipse rois in this layer.
	 * 
	 * @param ellipseROIs
	 */
	void setEllipseROIs(ArrayList<EllipseROI> ellipseROIs);

	/**
	 * Get all {@link EllipseROI}s in this layer.
	 * 
	 * @return a list of ellipse rois in this layer
	 */
	ArrayList<EllipseROI> getEllipseROIs();

	/**
	 * Set the current {@link EllipseROI} in this layer.
	 * 
	 * @param currentEllipseROI
	 */
	void setCurrentEllipseROI(EllipseROI currentEllipseROI);

	/**
	 * Get the current {@link EllipseROI} in this layer.
	 * 
	 * @return the selected {@link EllipseROI} or the shape that has been drawn
	 *         latest
	 */
	EllipseROI getCurrentEllipseROI();

	// RECTANGLE ROI
	/**
	 * Set all {@link RectangleROI}s in this layer. This will overwrite the
	 * entire list of rectangle rois in this layer.
	 * 
	 * @param rectangleROIs
	 */
	void setRectangleROIs(ArrayList<RectangleROI> rectangleROIs);

	/**
	 * Get all {@link RectangleROI}s in this layer.
	 * 
	 * @return a list of rectangle rois in this layer
	 */
	ArrayList<RectangleROI> getRectangleROIs();

	/**
	 * Set the current {@link RectangleROI} in this layer.
	 * 
	 * @param currentRectangleROI
	 */
	void setCurrentRectangleROI(RectangleROI currentRectangleROI);

	/**
	 * Get the current {@link RectangleROI} in this layer.
	 * 
	 * @return the selected {@link RectangleROI} or the shape that has been
	 *         drawn latest
	 */
	RectangleROI getCurrentRectangleROI();

	// LINE ROI
	/**
	 * Set the current {@link LineROI} in this layer.
	 * 
	 * @param currentLineROI
	 */
	void setCurrentLineROI(LineROI currentLineROI);

	/**
	 * Get the current {@link LineROI} in this layer.
	 * 
	 * @return the selected {@link LineROI} or the shape that has been drawn
	 *         latest
	 */
	LineROI getCurrentLineROI();

	/**
	 * Set all {@link LineROI}s in this layer. This will overwrite the entire
	 * list of line rois in this layer.
	 * 
	 * @param lineROIs
	 */
	void setLineROIs(ArrayList<LineROI> lineROIs);

	/**
	 * Get all {@link LineROI}s in this layer.
	 * 
	 * @return a list of line rois in this layer
	 */
	ArrayList<LineROI> getLineROIs();

	// POINT ROI
	/**
	 * Get the current {@link PointROI} in this layer.
	 * 
	 * @return the selected {@link PointROI} or the shape that has been drawn
	 *         latest
	 */
	PointROI getCurrentPointROI();

	/**
	 * Set the current {@link PointROI} in this layer.
	 * 
	 * @param currentPointROI
	 */
	void setCurrentPointROI(PointROI currentPointROI);

	/**
	 * Set all {@link PointROI}s in this layer. This will overwrite the entire
	 * list of point rois in this layer.
	 * 
	 * @param pointROIs
	 */
	void setPointROIs(ArrayList<PointROI> pointROIs);

	/**
	 * Get all {@link PointROI}s in this layer.
	 * 
	 * @return a list of point rois in this layer
	 */
	ArrayList<PointROI> getPointROIs();

	// methods for temporary ROI shapes
	/**
	 * Sets a current ROI shape to be drawn additionally to all present ones.
	 * 
	 * @param tmp
	 */
	void setTmpROIShape(ROIShape tmp);

	/**
	 * Gets the temporary ROI shape to be drawn additionally to all present
	 * ones.
	 * 
	 * @return the {@link ROIShape}, or <code>null</code>, if none is specified
	 */
	ROIShape getTmpROIShape();

	// methods for ALL ROI shapes
	/**
	 * Sets all ROI shapes in this layer. The shape list may contain different
	 * {@link ROIShape}s.
	 * 
	 * @param shapes
	 *            a list of ROI shapes.
	 */
	void setAllROIShapes(List<ROIShape> shapes);

	/**
	 * Get the list of all {@link ROIShape}s in this layer.
	 * 
	 * @return all {@link ROIShape}s of this layer
	 */
	List<ROIShape> getAllROIShapes();

	/**
	 * Set the currently selected (active) {@link ROIShape}.
	 * 
	 * @param shape
	 */
	void setCurrentROIShape(ROIShape shape);

	/**
	 * Get the currently selected {@link ROIShape}.
	 * 
	 * @return the active {@link ROIShape}, which may be of any kind
	 */
	ROIShape getCurrentROIShape();

	/**
	 * Set the currently clicked {@link ROIShape}.
	 * 
	 * @param clickedROIShape
	 *            the clicked {@link ROIShape}, which may be of any kind
	 */
	void setClickedROIShape(ROIShape clickedROIShape);

	/**
	 * Get the previously clicked {@link ROIShape}.
	 * 
	 * @return the active {@link ROIShape}, which may be of any kind
	 */
	ROIShape getClickedROIShape();

	/**
	 * Set a flag whether or not the mouse is in the west boundary point.
	 * 
	 * @param isInsidePtWest
	 */
	void setInsidePtWest(boolean isInsidePtWest);

	/**
	 * Determines whether or not the mouse is inside of the west boundary point.
	 * 
	 * @return <code>true</code> if inside, <code>false</code> otherwise
	 */
	boolean isInsidePtWest();

	/**
	 * Set a flag whether or not the mouse is in the south-west boundary point.
	 * 
	 * @param isInsidePtSouthWest
	 */
	void setInsidePtSouthWest(boolean isInsidePtSouthWest);

	/**
	 * Determines whether or not the mouse is inside of the south-west boundary
	 * point.
	 * 
	 * @return <code>true</code> if inside, <code>false</code> otherwise
	 */
	boolean isInsidePtSouthWest();

	/**
	 * Set a flag whether or not the mouse is in the south boundary point.
	 * 
	 * @param isInsidePtSouth
	 */
	void setInsidePtSouth(boolean isInsidePtSouth);

	/**
	 * Determines whether or not the mouse is inside of the south boundary
	 * point.
	 * 
	 * @return <code>true</code> if inside, <code>false</code> otherwise
	 */
	boolean isInsidePtSouth();

	/**
	 * Set a flag whether or not the mouse is in the south-east boundary point.
	 * 
	 * @param isInsidePtSouthEast
	 */
	void setInsidePtSouthEast(boolean isInsidePtSouthEast);

	/**
	 * Determines whether or not the mouse is inside of the south-east boundary
	 * point.
	 * 
	 * @return <code>true</code> if inside, <code>false</code> otherwise
	 */
	boolean isInsidePtSouthEast();

	/**
	 * Set a flag whether or not the mouse is in the east boundary point.
	 * 
	 * @param isInsidePtEast
	 */
	void setInsidePtEast(boolean isInsidePtEast);

	/**
	 * Determines whether or not the mouse is inside of the east boundary point.
	 * 
	 * @return <code>true</code> if inside, <code>false</code> otherwise
	 */
	boolean isInsidePtEast();

	/**
	 * Set a flag whether or not the mouse is in the north-east boundary point.
	 * 
	 * @param isInsidePtNorthEast
	 */
	void setInsidePtNorthEast(boolean isInsidePtNorthEast);

	/**
	 * Determines whether or not the mouse is inside of the north-east boundary
	 * point.
	 * 
	 * @return <code>true</code> if inside, <code>false</code> otherwise
	 */
	boolean isInsidePtNorthEast();

	/**
	 * Set a flag whether or not the mouse is in the north boundary point.
	 * 
	 * @param isInsidePtNorth
	 */
	void setInsidePtNorth(boolean isInsidePtNorth);

	/**
	 * Determines whether or not the mouse is inside of the north boundary
	 * point.
	 * 
	 * @return <code>true</code> if inside, <code>false</code> otherwise
	 */
	boolean isInsidePtNorth();

	/**
	 * Set a flag whether or not the mouse is in the north-west boundary point.
	 * 
	 * @param isInsidePtNorthWest
	 */
	void setInsidePtNorthWest(boolean isInsidePtNorthWest);

	/**
	 * Determines whether or not the mouse is inside of the north-west boundary
	 * point.
	 * 
	 * @return <code>true</code> if inside, <code>false</code> otherwise
	 */
	boolean isInsidePtNorthWest();

	/**
	 * Set a flag whether or not the mouse is inside of a drawn shape.
	 * 
	 * @param isInsideRoi
	 */
	void setInsideRoi(boolean isInsideRoi);

	/**
	 * Determines whether or not the mouse is inside of a drawn shape.
	 * 
	 * @return <code>true</code> if inside, <code>false</code> otherwise
	 */
	boolean isInsideRoi();

	/**
	 * Advise the {@link IDrawingLayer} to repaint all shapes.
	 */
	void update();

	/**
	 * This method updates the additional information in the
	 * {@link ImageInfoPanel} according to the given {@link ROIShape}.
	 * 
	 * @param roi
	 */
	void fireNewProperties(ROIShape roi);

	/**
	 * Set a flag whether or not a ROI is being dragged by mouse gesture.
	 * 
	 * @param isROIDragging
	 */
	void setROIDragging(boolean isROIDragging);

	/**
	 * Get a flag determining whether or not a ROI is currently being dragged by
	 * mouse gesture.
	 * 
	 * @return <code>true</code> if the roi is currently being dragged,
	 *         <code>false</code> otherwise
	 */
	boolean isROIDragging();

	/**
	 * Set the y-coordinate of the target point of a translation (shift) without
	 * transformation of a ROI.
	 * 
	 * @param roiShiftY0
	 *            the coordinate in image pixels
	 */
	void setRoiShiftY0(int roiShiftY0);

	/**
	 * Get the y-coordinate of the target point of a translation (shift) without
	 * transformation of a ROI.
	 * 
	 * @return the coordinate in image pixels
	 */
	int getRoiShiftY0();

	/**
	 * Set the x-coordinate of the target point of a translation (shift) without
	 * transformation of a ROI.
	 * 
	 * @param roiShiftX0
	 *            the coordinate in image pixels
	 */
	void setRoiShiftX0(int roiShiftX0);

	/**
	 * Get the x-coordinate of the target point of a translation (shift) without
	 * transformation of a ROI.
	 * 
	 * @return the coordinate in image pixels
	 */
	int getRoiShiftX0();

	/**
	 * Set the y-coordinate of the origin of a translation (shift) without
	 * transformation of a ROI.
	 * 
	 * @param roiY0
	 *            the coordinate in image pixels
	 */
	void setRoiY0(int roiY0);

	/**
	 * Get the y-coordinate of the target point of a translation (shift) without
	 * transformation of a ROI.
	 * 
	 * @return the coordinate in image pixels
	 */
	int getRoiY0();

	/**
	 * Set the x-coordinate of the origin of a translation (shift) without
	 * transformation of a ROI.
	 * 
	 * @param roiX0
	 *            the coordinate in image pixels
	 */
	void setRoiX0(int roiX0);

	/**
	 * Get the x-coordinate of the target point of a translation (shift) without
	 * transformation of a ROI.
	 * 
	 * @return the coordinate in image pixels
	 */
	int getRoiX0();

	/**
	 * Adds all default listeners.
	 */
	void addAllDefaultListeners();

	/**
	 * Removes all default listeners.
	 */
	void removeAllDefaultListeners();

	/**
	 * Sets a {@link Cursor} to this layer.
	 * 
	 * @param cursor
	 */
	void setCursor(Cursor cursor);

	/**
	 * Removes the default key layer listener.
	 */
	void removeDefaultCanvasMover();

	/**
	 * (Re-)Adds the default key listener.
	 */
	void addDefaultCanvasMover();

	/**
	 * Sets the unique ID of this drawing layer.
	 * 
	 * @param id
	 */
	void setID(UUID id);

	/**
	 * Gets the unique ID of this drawing layer.
	 * 
	 * @return an {@link UUID} object
	 */
	UUID getID();

	void setHighlightStroke(BasicStroke highlightStroke);

	BasicStroke getHighlightStroke();

	void setHighlightColor(Color highlightColor);

	Color getHighlightColor();

	void removeRoiShapeForHighlighting();

	Color getLineROIBoundaryRectangleColor();

	ROIShape getRoiShapeForHighlighting();

	Color getRoiBoundaryRectangleColor();

	Color getLineROIBoundaryColor();

	void setDefaultROIStroke(BasicStroke defaultROIStroke);

	BasicStroke getDefaultROIStroke();

	void setDefaultLayerColor(Color defaultLayerColor);

	Color getDefaultLayerColor();

	void setBoundEditableRectanglesUntransformed(
			ROIShape[] boundEditableRectanglesUntransformed);

	void setBoundEditableRectangles(ROIShape[] rs);

	void removeTmpROIShape();

	ROILayerCursorChanger getCursorChanger();

	/**
	 * Determines whether or not the layer contains any ROI shapes.
	 * 
	 * @return <code>true</code>, if minimum one shape is present,
	 *         <code>false</code> otherwise
	 */
	boolean hasROIs();

}
