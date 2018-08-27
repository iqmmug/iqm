package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: DefaultDrawingLayer.java
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import javax.media.jai.ROIShape;
import javax.swing.JLabel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.gui.roi.AngleROI;
import at.mug.iqm.api.gui.roi.AreaEnabledROI;
import at.mug.iqm.api.gui.roi.EllipseROI;
import at.mug.iqm.api.gui.roi.FreehandROI;
import at.mug.iqm.api.gui.roi.LineROI;
import at.mug.iqm.api.gui.roi.PointROI;
import at.mug.iqm.api.gui.roi.PolygonROI;
import at.mug.iqm.api.gui.roi.RectangleROI;
import at.mug.iqm.api.gui.roi.events.AngleROIAddedEvent;
import at.mug.iqm.api.gui.roi.events.EllipseROIAddedEvent;
import at.mug.iqm.api.gui.roi.events.FreehandROIAddedEvent;
import at.mug.iqm.api.gui.roi.events.LineROIAddedEvent;
import at.mug.iqm.api.gui.roi.events.PointROIAddedEvent;
import at.mug.iqm.api.gui.roi.events.PolygonROIAddedEvent;
import at.mug.iqm.api.gui.roi.events.RectangleROIAddedEvent;
import at.mug.iqm.api.gui.roi.events.handler.CanvasMover;
import at.mug.iqm.api.gui.roi.events.handler.ROILayerCursorChanger;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.OperatingSystem;

/**
 * This class represents a default layer where the user may draw regions of
 * interest (ROIs) and other visual components on.
 * 
 * <b>IMPORTANT NOTE</b>: just adding mouse wheel support on non-mac systems,
 * due to a current bug (see ticket #21
 * https://sourceforge.net/p/iqm/tickets/21/)
 * 
 * @author Philipp Kainz
 * 
 */
public abstract class DefaultDrawingLayer extends JLabel implements
		IDrawingLayer, MouseMotionListener, MouseListener, MouseWheelListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 1489707769457879876L;

	/**
	 * A custom class logger
	 */
	private static final Logger logger = Logger
			.getLogger(DefaultDrawingLayer.class);

	/*
	 * ### LAYER MANAGEMENT
	 */
	/**
	 * The display panel where the image is displayed.
	 */
	protected transient ILookPanel displayPanel;

	/**
	 * The context pop up menu for {@link LookToolboxPanel} and the
	 * {@link ImageInfoPanel}.
	 */
	protected transient ContextPopupMenu contextPopupMenu = null; // the menu
	/**
	 * The context pop up menu listener for the {@link ContextPopupMenu}.
	 */
	protected transient ContextPopupListener contextPopupListener = null; // MouseAdapter

	/**
	 * A custom key listener for moving the canvas with spacebar down and mouse
	 * drag gestures.
	 */
	protected transient CanvasMover canvasMover = null;

	/*
	 * ### LAYER VARIABLES
	 */
	protected UUID id = UUID.randomUUID();
	/**
	 * The z-order of this layer.
	 */
	protected int zOrder = -1;

	/**
	 * The name of this layer. Duplicates are permitted.
	 */
	protected String name = "Layer";

	/**
	 * A flag, determining whether or not a layer's content is rendered.
	 */
	protected boolean isVisible = true;

	/**
	 * A flag, determining whether or not a layer is selected.
	 */
	protected boolean isSelected = true;

	/**
	 * The default color for a ROI layer.
	 */
	protected Color defaultLayerColor = IDrawingLayer.DEFAULT_LAYER_COLOR;

	/**
	 * The default stroke for drawing ROIs on the canvas. Default value is a
	 * {@link Float} of <code>1.5</code>.
	 */
	protected BasicStroke defaultROIStroke = IDrawingLayer.DEFAULT_LAYER_STROKE;

	/*
	 * ### ROI CREATING AND EDITING
	 */
	/**
	 * The start point of a ROI drawing, x-coordinate.
	 */
	protected int roiX0;
	/**
	 * The start point of a ROI drawing, y-coordinate.
	 */
	protected int roiY0;

	/**
	 * A value for the translation of any ROI, x-coordinate.
	 */
	protected int roiShiftX0;
	/**
	 * A value for the translation of any ROI, y-coordinate.
	 */
	protected int roiShiftY0;

	/**
	 * A flag, whether a ROI is currently dragged or not.
	 */
	protected boolean isROIDragging = false;
	/**
	 * A flag, whether a click happened inside a ROI or not.
	 */
	protected boolean isInsideRoi = false;
	/**
	 * A flag, whether a click happened inside the NW boundary rectangle.
	 */
	protected boolean isInsidePtNorthWest = false;
	/**
	 * A flag, whether a click happened inside the N boundary rectangle.
	 */
	protected boolean isInsidePtNorth = false;
	/**
	 * A flag, whether a click happened inside the NE boundary rectangle.
	 */
	protected boolean isInsidePtNorthEast = false;
	/**
	 * A flag, whether a click happened inside the E boundary rectangle.
	 */
	protected boolean isInsidePtEast = false;
	/**
	 * A flag, whether a click happened inside the SE boundary rectangle.
	 */
	protected boolean isInsidePtSouthEast = false;
	/**
	 * A flag, whether a click happened inside the S boundary rectangle.
	 */
	protected boolean isInsidePtSouth = false;
	/**
	 * A flag, whether a click happened inside the SW boundary rectangle.
	 */
	protected boolean isInsidePtSouthWest = false;
	/**
	 * A flag, whether a click happened inside the W boundary rectangle.
	 */
	protected boolean isInsidePtWest = false;

	/*
	 * ### EDITING ELEMENTS FOR THE ROIS
	 */
	/**
	 * The 8 filled boundary rectangles for editing a shape, transformed.
	 */
	protected ROIShape[] boundEditableRectangles = null;
	/**
	 * The 8 filled boundary rectangles for editing a shape, untransformed.
	 */
	protected ROIShape[] boundEditableRectanglesUntransformed = null;

	/**
	 * The ROI shape which should be highlighted on mouse over.
	 */
	protected ROIShape roiShapeForHighlighting = null;

	/**
	 * The color for highlighted ROI shapes. Default is {@link Color#white};
	 */
	protected Color highlightColor = IDrawingLayer.HIGHLIGHT_COLOR;

	/**
	 * The stroke for the highlighted ROI shape. Default is {@link BasicStroke}
	 * of value <code>1.0F</code>.
	 */
	protected BasicStroke highlightStroke = IDrawingLayer.HIGHLIGHT_STROKE;

	/**
	 * The default color for drawing a ROI's boundaries on the canvas. Default
	 * value is {@link Color#GREEN}.
	 */
	protected Color roiBoundaryColor = Color.GREEN;

	/**
	 * The default stroke a the ROI's boundary. Default is {@link BasicStroke}
	 * of value <code>1.0F</code>.
	 */
	protected Stroke roiBoundaryStroke = new BasicStroke(1.0F);

	/**
	 * The default color for drawing a {@link LineROI}'s boundaries on the
	 * canvas. Default value is {@link Color#GREEN}.
	 */
	protected Color lineROIBoundaryColor = Color.GREEN;

	/**
	 * The default stroke of a line ROI's boundary. Default is
	 * {@link BasicStroke} of value <code>1.0F</code>.
	 */
	protected Stroke lineROIBoundaryStroke = new BasicStroke(1.0F);

	/**
	 * The default color for drawing a ROI's boundary rectangles on the canvas.
	 * These rectangles can be moved in order to resize the ROI shape. Default
	 * value is {@link Color#RED}.
	 */
	protected Color roiBoundaryRectangleColor = Color.RED;

	/**
	 * The default color for drawing a {@link LineROI}'s boundary rectangles on
	 * the canvas. These rectangles can be moved in order to resize the ROI
	 * shape. Default value is {@link Color#GREEN}.
	 */
	protected Color lineROIBoundaryRectangleColor = Color.GREEN;

	/**
	 * The default color for drawing a temporary ROI shape. Default value is
	 * {@link IDrawingLayer#DEFAULT_LAYER_COLOR}.
	 */
	protected Color tmpROIShapeColor = IDrawingLayer.DEFAULT_LAYER_COLOR;

	/*
	 * ### ALL ROI ELEMENTS
	 */
	/**
	 * The currently selected ROI shape.
	 */
	protected ROIShape currentROIShape = null;
	/**
	 * The clicked ROI shape.
	 */
	protected ROIShape clickedROIShape = null;
	/**
	 * The temporary ROI shape.
	 */
	protected ROIShape tmpROIShape = null;
	/**
	 * An {@link ArrayList} of all ROI shapes drawn on the canvas.
	 */
	protected ArrayList<ROIShape> allROIShapes = new ArrayList<ROIShape>();

	/*
	 * ### SPECIFIC ROI SHAPES
	 */
	/**
	 * The current line ROI shape.
	 */
	protected LineROI currentLineROI = null;
	/**
	 * An {@link ArrayList} of all line ROI elements.
	 */
	protected ArrayList<LineROI> lineROIs = new ArrayList<LineROI>();

	/**
	 * The current rectangle ROI shape.
	 */
	protected RectangleROI currentRectangleROI = null;
	/**
	 * An {@link ArrayList} of all rectangle ROI elements.
	 */
	protected ArrayList<RectangleROI> rectangleROIs = new ArrayList<RectangleROI>();

	/**
	 * The current ellipse ROI shape.
	 */
	protected EllipseROI currentEllipseROI = null;
	/**
	 * An {@link ArrayList} of all ellipse ROI elements.
	 */
	protected ArrayList<EllipseROI> ellipseROIs = new ArrayList<EllipseROI>();

	/**
	 * The current point ROI shape.
	 */
	protected PointROI currentPointROI = null;
	/**
	 * An {@link ArrayList} of all point ROI elements.
	 */
	protected ArrayList<PointROI> pointROIs = new ArrayList<PointROI>();

	/**
	 * The shape (path) for the freehand ROI.
	 */
	protected GeneralPath freehandShape = null;
	/**
	 * The current freehand ROI shape.
	 */
	protected FreehandROI currentFreehandROI = null;
	/**
	 * An {@link ArrayList} of all freehand ROI elements.
	 */
	protected ArrayList<FreehandROI> freehandROIs = new ArrayList<FreehandROI>();

	/**
	 * The shape for the polygon ROI.
	 */
	protected GeneralPath polygonShape = null;
	/**
	 * The number of corners in the polygon.
	 */
	protected int numPolyPoints = 0;
	/**
	 * The current polygon ROI shape.
	 */
	protected PolygonROI currentPolygonROI = null;
	/**
	 * An {@link ArrayList} of all polygon ROI elements.
	 */
	protected ArrayList<PolygonROI> polygonROIs = new ArrayList<PolygonROI>();

	/**
	 * The shape for the angle ROI.
	 */
	protected GeneralPath angleShape = null;
	/**
	 * The number of corners in the angle ROI.
	 */
	protected int numAnglePoints = 0;
	/**
	 * The current angle ROI shape.
	 */
	protected AngleROI currentAngleROI = null;
	/**
	 * An {@link ArrayList} of all angle ROI elements.
	 */
	protected ArrayList<AngleROI> angleROIs = new ArrayList<AngleROI>();

	/**
	 * A custom event handler for highlighting a hovered shape in a specified
	 * color.
	 */
	protected ROILayerCursorChanger cursorChanger = null;

	/**
	 * A property change support object to emit events.
	 */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Default constructor.
	 */
	public DefaultDrawingLayer() {
		super();
	}

	/**
	 * Create a new layer for
	 * 
	 * @param displayPanel
	 */
	public DefaultDrawingLayer(ILookPanel displayPanel) {
		this.displayPanel = displayPanel;
		initialize();
	}

	/**
	 * Initializes the layer with the context menu and listeners.
	 */
	public void initialize() {
		// set the layer to be transparent
		setOpaque(false);
		setLayout(null);
		
		this.cursorChanger = new ROILayerCursorChanger(displayPanel);
		this.canvasMover = new CanvasMover(displayPanel);
		
		this.addAllDefaultListeners();

		this.addDefaultCanvasMover();

		// add a key listener for switching the tool bar
		this.addKeyListener(displayPanel.getToolboxPanel());

		this.createContextMenuAndBehavior();
	}

	/**
	 * Add the default canvas mover.
	 */
	@Override
	public void addDefaultCanvasMover() {
		addKeyListener(canvasMover);
		addMouseMotionListener(canvasMover);
		addMouseListener(canvasMover);
	}

	/**
	 * Remove the default canvas mover.
	 */
	@Override
	public void removeDefaultCanvasMover() {
		removeKeyListener(canvasMover);
		removeMouseMotionListener(canvasMover);
		removeMouseListener(canvasMover);
	}

	/**
	 * This method registers all default listeners with this layer.
	 */
	@Override
	public void addAllDefaultListeners() {
		addMouseMotionListener(cursorChanger);
		addMouseMotionListener(this);
		addMouseListener(this);
		// IMPORTANT NOTE just add mouse wheel support on non-mac systems, due
		// to a current bug
		// (see ticket #21 https://sourceforge.net/p/iqm/tickets/21/)
		if (!OperatingSystem.isMac()) {
			addMouseWheelListener(this);
		}
	}

	/**
	 * This method removes all default listeners from the layer.
	 */
	@Override
	public void removeAllDefaultListeners() {
		removeMouseMotionListener(cursorChanger);
		removeMouseMotionListener(this);
		removeMouseListener(this);

		// IMPORTANT NOTE just add mouse wheel support on non-mac systems, due
		// to a current bug
		// (see ticket #21 https://sourceforge.net/p/iqm/tickets/21/)
		if (!OperatingSystem.isMac()) {
			removeMouseWheelListener(this);
		}
	}

	public ROILayerCursorChanger getCursorChanger() {
		return cursorChanger;
	}

	public void setCursorChanger(ROILayerCursorChanger cursorChanger) {
		this.cursorChanger = cursorChanger;
	}

	@Override
	public void setZOrder(int zOrder) {
		this.zOrder = zOrder;
	}

	@Override
	public int getZOrder() {
		return this.zOrder;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isLayerVisible() {
		return this.isVisible;
	}

	@Override
	public void setLayerVisible(boolean visible) {
		this.isVisible = visible;
	}

	@Override
	public void setLayerColor(Color color) {
		this.defaultLayerColor = color;
		this.tmpROIShapeColor = color;
	}

	@Override
	public Color getLayerColor() {
		return this.defaultLayerColor;
	}

	@Override
	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}

	@Override
	public boolean isSelected() {
		return this.isSelected;
	}

	@Override
	public int getElementCount() {
		return this.allROIShapes.size();
	}

	@Override
	public boolean isEmpty() {
		return this.allROIShapes.isEmpty();
	}

	@Override
	public String toString() {
		String str = this.getClass().getName();
		str += "[layerName=" + getName() + ", " + "isSelected=" + isSelected()
				+ ", ";
		str += "isVisible=" + isVisible() + ", ";
		str += "zOrder=" + getZOrder() + ", ";
		str += "layerColor=" + getLayerColor() + ", " + "elementCount="
				+ getElementCount() + "]";
		return str;
	}

	/**
	 * Generic method to draw any specified class or subclass of
	 * {@link ROIShape} on the look panel.
	 * 
	 * @param roi
	 *            a specified {@link ROIShape} to be drawn
	 */
	@Override
	public void drawROI(ROIShape roi) {

		if (roi instanceof LineROI) {
			setCurrentLineROI((LineROI) roi);
			lineROIs.add((LineROI) roi);

		} else if (roi instanceof RectangleROI) {
			setCurrentRectangleROI((RectangleROI) roi);
			rectangleROIs.add((RectangleROI) roi);

		} else if (roi instanceof EllipseROI) {
			setCurrentEllipseROI((EllipseROI) roi);
			ellipseROIs.add((EllipseROI) roi);

		} else if (roi instanceof FreehandROI) {
			setCurrentFreehandROI((FreehandROI) roi);
			freehandROIs.add((FreehandROI) roi);

		} else if (roi instanceof AngleROI) {
			setCurrentAngleROI((AngleROI) roi);
			angleROIs.add((AngleROI) roi);

		} else if (roi instanceof PolygonROI) {
			setCurrentPolygonROI((PolygonROI) roi);
			polygonROIs.add((PolygonROI) roi);

		} else if (roi instanceof PointROI) {
			setCurrentPointROI((PointROI) roi);
			pointROIs.add((PointROI) roi);
		}

		// tell diverse instances to obtain new properties
		fireNewProperties(roi);

		allROIShapes.add(roi);
		setCurrentROIShape(roi);

		// draw the shapes
		setCurrentROIShape(roi);
		setAllROIShapes(allROIShapes);
		repaint();
	}

	/**
	 * Generic method to draw some {@link ROIShape}s on the look panel.
	 * <p>
	 * The shapes may be of different types.
	 * 
	 * @param rois
	 *            {@link ROIShape}s to be drawn
	 */
	@Override
	public void drawROIs(List<ROIShape> rois) {
		for (ROIShape roi : rois) {
			drawROI(roi);
		}
	}

	/**
	 * Generic method to draw a {@link LineROI} on the look panel.
	 * 
	 * @param roi
	 *            a {@link LineROI} to be drawn
	 */
	@Override
	public void drawLineROI(LineROI roi) {

		setCurrentLineROI((LineROI) roi);
		fireNewProperties(roi);
		lineROIs.add(roi);

		allROIShapes.add(roi);
		setCurrentROIShape(roi);

		// draw the shapes
		setCurrentROIShape(roi);
		setAllROIShapes(allROIShapes);
		repaint();
	}

	/**
	 * Generic method to draw a {@link AngleROI} on the look panel.
	 * 
	 * @param roi
	 *            a {@link AngleROI} to be drawn
	 */
	@Override
	public void drawAngleROI(AngleROI roi) {

		setCurrentAngleROI(roi);
		fireNewProperties(roi);
		angleROIs.add(roi);

		allROIShapes.add(roi);
		setCurrentROIShape(roi);

		// draw the shapes
		setCurrentROIShape(roi);
		setAllROIShapes(allROIShapes);
		repaint();
	}

	/**
	 * Generic method to draw a {@link EllipseROI} on the look panel.
	 * 
	 * @param roi
	 *            a {@link EllipseROI} to be drawn
	 */
	@Override
	public void drawEllipseROI(EllipseROI roi) {

		setCurrentEllipseROI(roi);
		fireNewProperties(roi);
		ellipseROIs.add(roi);

		allROIShapes.add(roi);
		setCurrentROIShape(roi);

		// draw the shapes
		setCurrentROIShape(roi);
		setAllROIShapes(allROIShapes);
		repaint();
	}

	/**
	 * Generic method to draw a {@link FreehandROI} on the look panel.
	 * 
	 * @param roi
	 *            a {@link FreehandROI} to be drawn
	 */
	@Override
	public void drawFreehandROI(FreehandROI roi) {

		setCurrentFreehandROI(roi);
		freehandROIs.add(roi);

		allROIShapes.add(roi);
		setCurrentROIShape(roi);
		setCurrentROIShape(roi);

		// draw the shapes
		setCurrentROIShape(roi);
		setAllROIShapes(allROIShapes);
		repaint();
	}

	/**
	 * Generic method to draw a {@link PolygonROI} on the look panel.
	 * 
	 * @param roi
	 *            a {@link PolygonROI} to be drawn
	 */
	@Override
	public void drawPolygonROI(PolygonROI roi) {

		setCurrentPolygonROI(roi);
		polygonROIs.add(roi);

		allROIShapes.add(roi);
		setCurrentROIShape(roi);

		// draw the shapes
		setCurrentROIShape(roi);
		setAllROIShapes(allROIShapes);
		repaint();
	}

	/**
	 * Generic method to draw a {@link RectangleROI} on the look panel.
	 * 
	 * @param roi
	 *            a {@link RectangleROI} to be drawn
	 */
	@Override
	public void drawRectangleROI(RectangleROI roi) {

		setCurrentRectangleROI(roi);
		fireNewProperties(roi);
		rectangleROIs.add(roi);

		allROIShapes.add(roi);
		setCurrentROIShape(roi);

		// draw the shapes
		setCurrentROIShape(roi);
		setAllROIShapes(allROIShapes);
		repaint();
	}

	/**
	 * Generic method to draw a {@link PointROI} on the look panel.
	 * 
	 * @param roi
	 *            a {@link PointROI} to be drawn
	 */
	@Override
	public void drawPointROI(PointROI roi) {

		setCurrentPointROI(roi);
		pointROIs.add(roi);

		allROIShapes.add(roi);
		setCurrentROIShape(roi);

		// draw the shapes
		setCurrentROIShape(roi);
		setAllROIShapes(allROIShapes);
		repaint();
	}

	/**
	 * Generic method to draw {@link LineROI}s on the look panel.
	 * 
	 * @param rois
	 *            {@link LineROI}s to be drawn
	 */
	@Override
	public void drawLineROIs(List<LineROI> rois) {
		for (ROIShape roi : rois) {
			drawROI(roi);
		}
	}

	/**
	 * Generic method to draw {@link RectangleROI}s on the look panel.
	 * 
	 * @param rois
	 *            {@link RectangleROI}s to be drawn
	 */
	@Override
	public void drawRectangleROIs(List<RectangleROI> rois) {
		for (ROIShape roi : rois) {
			drawROI(roi);
		}
	}

	/**
	 * Generic method to draw {@link EllipseROI}s on the look panel.
	 * 
	 * @param rois
	 *            {@link RectangleROI}s to be drawn
	 */
	@Override
	public void drawEllipseROIs(List<EllipseROI> rois) {
		for (ROIShape roi : rois) {
			drawROI(roi);
		}
	}

	/**
	 * Generic method to draw {@link FreehandROI}s on the look panel.
	 * 
	 * @param rois
	 *            {@link FreehandROI}s to be drawn
	 */
	@Override
	public void drawFreehandROIs(List<FreehandROI> rois) {
		for (ROIShape roi : rois) {
			drawROI(roi);
		}
	}

	/**
	 * Generic method to draw {@link AngleROI}s on the look panel.
	 * 
	 * @param rois
	 *            {@link AngleROI}s to be drawn
	 */
	@Override
	public void drawAngleROIs(List<AngleROI> rois) {
		for (ROIShape roi : rois) {
			drawROI(roi);
		}
	}

	/**
	 * Generic method to draw {@link PointROI}s on the look panel.
	 * 
	 * @param rois
	 *            {@link PointROI}s to be drawn
	 */
	@Override
	public void drawPointROIs(List<PointROI> rois) {
		for (ROIShape roi : rois) {
			drawROI(roi);
		}
	}

	/**
	 * Generic method to draw {@link PolygonROI}s on the look panel.
	 * 
	 * @param rois
	 *            {@link PolygonROI}s to be drawn
	 */
	@Override
	public void drawPolygonROIs(List<PolygonROI> rois) {
		for (ROIShape roi : rois) {
			drawROI(roi);
		}
	}

	@Override
	public void setAllROIShapes(List<ROIShape> shapes) {
		this.allROIShapes = (ArrayList<ROIShape>) shapes;
	}

	@Override
	public ArrayList<ROIShape> getAllROIShapes() {
		return this.allROIShapes;
	}

	/**
	 * @return the currentROIShape
	 */
	@Override
	public ROIShape getCurrentROIShape() {
		return currentROIShape;
	}

	/**
	 * @param currentROIShape
	 *            the currentROIShape to set
	 */
	@Override
	public void setCurrentROIShape(ROIShape currentROIShape) {
		this.currentROIShape = currentROIShape;
	}

	@Override
	public void setTmpROIShape(ROIShape tmp) {
		this.tmpROIShape = tmp;
	}

	@Override
	public ROIShape getTmpROIShape() {
		return this.tmpROIShape;
	}

	@Override
	public void removeTmpROIShape() {
		this.tmpROIShape = null;
	}

	/**
	 * @return the clickedROIShape
	 */
	@Override
	public ROIShape getClickedROIShape() {
		return clickedROIShape;
	}

	/**
	 * @param clickedROIShape
	 *            the clickedROIShape to set
	 */
	@Override
	public void setClickedROIShape(ROIShape clickedROIShape) {
		this.clickedROIShape = clickedROIShape;
	}

	/**
	 * @return the lineROIs
	 */
	@Override
	public ArrayList<LineROI> getLineROIs() {
		return lineROIs;
	}

	/**
	 * @param lineROIs
	 *            the lineROIs to set
	 */
	@Override
	public void setLineROIs(ArrayList<LineROI> lineROIs) {
		this.lineROIs = lineROIs;
	}

	/**
	 * @return the currentLineROI
	 */
	@Override
	public LineROI getCurrentLineROI() {
		return currentLineROI;
	}

	/**
	 * @param currentLineROI
	 *            the currentLineROI to set
	 */
	@Override
	public void setCurrentLineROI(LineROI currentLineROI) {
		this.currentLineROI = currentLineROI;
	}

	/**
	 * @return the currentRectangleROI
	 */
	@Override
	public RectangleROI getCurrentRectangleROI() {
		return currentRectangleROI;
	}

	/**
	 * @param currentRectangleROI
	 *            the currentRectangleROI to set
	 */
	@Override
	public void setCurrentRectangleROI(RectangleROI currentRectangleROI) {
		this.currentRectangleROI = currentRectangleROI;
	}

	/**
	 * @return the rectangleROIs
	 */
	@Override
	public ArrayList<RectangleROI> getRectangleROIs() {
		return rectangleROIs;
	}

	/**
	 * @param rectangleROIs
	 *            the rectangleROIs to set
	 */
	@Override
	public void setRectangleROIs(ArrayList<RectangleROI> rectangleROIs) {
		this.rectangleROIs = rectangleROIs;
	}

	/**
	 * @return the currentEllipseROI
	 */
	@Override
	public EllipseROI getCurrentEllipseROI() {
		return currentEllipseROI;
	}

	/**
	 * @param currentEllipseROI
	 *            the currentEllipseROI to set
	 */
	@Override
	public void setCurrentEllipseROI(EllipseROI currentEllipseROI) {
		this.currentEllipseROI = currentEllipseROI;
	}

	/**
	 * @return the ellipseROIs
	 */
	@Override
	public ArrayList<EllipseROI> getEllipseROIs() {
		return ellipseROIs;
	}

	/**
	 * @param ellipseROIs
	 *            the ellipseROIs to set
	 */
	@Override
	public void setEllipseROIs(ArrayList<EllipseROI> ellipseROIs) {
		this.ellipseROIs = ellipseROIs;
	}

	/**
	 * @return the freehandShape
	 */
	@Override
	public GeneralPath getFreehandShape() {
		return freehandShape;
	}

	/**
	 * @param freehandShape
	 *            the polyLine to set
	 */
	@Override
	public void setFreehandShape(GeneralPath freehandShape) {
		this.freehandShape = freehandShape;
	}

	/**
	 * @return the currentFreehandROI
	 */
	@Override
	public FreehandROI getCurrentFreehandROI() {
		return currentFreehandROI;
	}

	/**
	 * @param currentFreehandROI
	 *            the currentFreehandROI to set
	 */
	@Override
	public void setCurrentFreehandROI(FreehandROI currentFreehandROI) {
		this.currentFreehandROI = currentFreehandROI;
	}

	/**
	 * @return the freehandROIs
	 */
	@Override
	public ArrayList<FreehandROI> getFreehandROIs() {
		return freehandROIs;
	}

	/**
	 * @param freehandROIs
	 *            the freehandROIs to set
	 */
	@Override
	public void setFreehandROIs(ArrayList<FreehandROI> freehandROIs) {
		this.freehandROIs = freehandROIs;
	}

	/**
	 * @return the numPolyPoints
	 */
	@Override
	public int getNumPolyPoints() {
		return numPolyPoints;
	}

	/**
	 * @param numPolyPoints
	 *            the numPolyPoints to set
	 */
	@Override
	public void setNumPolyPoints(int numPolyPoints) {
		this.numPolyPoints = numPolyPoints;
	}

	/**
	 * @return the polygonShape
	 */
	@Override
	public GeneralPath getPolygonShape() {
		return polygonShape;
	}

	/**
	 * @param polygonShape
	 *            the polygonShape to set
	 */
	@Override
	public void setPolygonShape(GeneralPath polygonShape) {
		this.polygonShape = polygonShape;
	}

	/**
	 * @return the currentPolygonROI
	 */
	@Override
	public PolygonROI getCurrentPolygonROI() {
		return currentPolygonROI;
	}

	/**
	 * @param currentPolygonROI
	 *            the currentPolygonROI to set
	 */
	@Override
	public void setCurrentPolygonROI(PolygonROI currentPolygonROI) {
		this.currentPolygonROI = currentPolygonROI;
	}

	/**
	 * @return the polygonROIs
	 */
	@Override
	public ArrayList<PolygonROI> getPolygonROIs() {
		return polygonROIs;
	}

	/**
	 * @param polygonROIs
	 *            the polygonROIs to set
	 */
	@Override
	public void setPolygonROIs(ArrayList<PolygonROI> polygonROIs) {
		this.polygonROIs = polygonROIs;
	}

	/**
	 * @return the currentAngleROI
	 */
	@Override
	public AngleROI getCurrentAngleROI() {
		return currentAngleROI;
	}

	/**
	 * @param currentAngleROI
	 *            the currentAngleROI to set
	 */
	@Override
	public void setCurrentAngleROI(AngleROI currentAngleROI) {
		this.currentAngleROI = currentAngleROI;
	}

	/**
	 * @return the currentPointROI
	 */
	@Override
	public PointROI getCurrentPointROI() {
		return currentPointROI;
	}

	/**
	 * @param currentPointROI
	 *            the currentPointROI to set
	 */
	@Override
	public void setCurrentPointROI(PointROI currentPointROI) {
		this.currentPointROI = currentPointROI;
	}

	/**
	 * @return the angleShape
	 */
	@Override
	public GeneralPath getAngleShape() {
		return angleShape;
	}

	/**
	 * @param angleShape
	 *            the angleShape to set
	 */
	@Override
	public void setAngleShape(GeneralPath angleShape) {
		this.angleShape = angleShape;
	}

	/**
	 * @return the numAnglePoints
	 */
	@Override
	public int getNumAnglePoints() {
		return numAnglePoints;
	}

	/**
	 * @param numAnglePoints
	 *            the numAnglePoints to set
	 */
	@Override
	public void setNumAnglePoints(int numAnglePoints) {
		this.numAnglePoints = numAnglePoints;
	}

	/**
	 * @return the angleROIs
	 */
	@Override
	public ArrayList<AngleROI> getAngleROIs() {
		return angleROIs;
	}

	/**
	 * @param angleROIs
	 *            the angleROIs to set
	 */
	@Override
	public void setAngleROIs(ArrayList<AngleROI> angleROIs) {
		this.angleROIs = angleROIs;
	}

	/**
	 * @return the pointROIs
	 */
	@Override
	public ArrayList<PointROI> getPointROIs() {
		return pointROIs;
	}

	/**
	 * @param pointROIs
	 *            the PointROIs to set
	 */
	@Override
	public void setPointROIs(ArrayList<PointROI> pointROIs) {
		this.pointROIs = pointROIs;
	}

	/**
	 * Various updates are made according to the subclass of {@link ROIShape}:
	 * <ul>
	 * <li>Sets the length in <code>px</code> of a given {@link LineROI} shape
	 * to the {@link ImageInfoPanel}.
	 * <li>Sets the length of the legs in <code>px</code> of a given angle ROI
	 * shape.
	 * <li>Set the area in <code>px²</code> of a given ROI shape, which is
	 * permitted to have a closed area such as classes implementing
	 * {@link AreaEnabledROI} to the {@link ImageInfoPanel}.
	 * </ul>
	 * 
	 * @param roi
	 */
	@Override
	public void fireNewProperties(ROIShape roi) {
		if (displayPanel == null) return;
		
		if (roi instanceof LineROI) {
			LineROI lineRoi = (LineROI) roi;
			displayPanel.setAdditionalText(String.format("%4s", "")
					+ "Length: " + String.format("%.2f", lineRoi.getLength())
					+ "px");

			// log the line ROI length
			logger.trace(lineRoi.toString());
		} else if (roi instanceof AngleROI) {
			AngleROI angleRoi = (AngleROI) roi;
			// show the text in the imageinfopanel
			displayPanel.setAdditionalText(String.format("%4s", "") + "Leg#1: "
					+ String.format("%.2f", angleRoi.getLengthFirstLeg())
					+ "px" + " Angle: "
					+ String.format("%.2f", angleRoi.getAngle()) + "° ("
					+ String.format("%.2f", angleRoi.getComplementaryAngle())
					+ "°)" + " Leg#2: "
					+ String.format("%.2f", angleRoi.getLengthSecondLeg())
					+ "px");

			// log the angle ROI
			logger.trace(angleRoi.toString());
		} else if (roi instanceof AreaEnabledROI) {
			AreaEnabledROI areaEnabledROI = (AreaEnabledROI) roi;

			// get the coordinates and
			// calculate the length of the first leg of the angle ROI shape
			double area = areaEnabledROI.getArea();

			// show the text in the image info panel
			displayPanel.setAdditionalText(String.format("%4s", "")
					+ "Area: "
					+ String.format("%.2f", area)
					+ " px²"
					+ " Width: "
					+ String.format("%.0f", areaEnabledROI.getBounds()
							.getWidth())
					+ "px Height: "
					+ String.format("%.0f", areaEnabledROI.getBounds()
							.getHeight()) + "px");

			// log the line ROI length
			logger.trace(areaEnabledROI.toString());
		}
	}

	@Override
	public void update() {
		repaint();
	}

	@Override
	public void deleteROI(ROIShape roi) {
		this.setROIDragging(false);

		// ##################################################
		// search for the latest ROI drawn and set
		// it to the current ROI
		reOrganizeROIs(roi);

		// ##################################################
		// REMOVE the element from the all ROI shapes array list
		allROIShapes.remove(roi);

		// if there are any existing ROI shapes
		if (!allROIShapes.isEmpty()) {
			// set the latest drawn ROI shape in the look panel
			this.setCurrentROIShape(allROIShapes.get(allROIShapes.size() - 1));

			// set the latest drawn ROI shape in the ROI painter for
			// drawing boundaries
			setCurrentROIShape(allROIShapes.get(allROIShapes.size() - 1));
		}

		// no more ROI shapes are present
		else {
			setCurrentROIShape(null);
		}

		displayPanel.setAdditionalText("");

		// repaint the remaining shapes
		setRoiShapeForHighlighting(null);
		repaint();
	}

	@Override
	public void deleteROIs(List<ROIShape> rois) {
		if (rois == null || rois.isEmpty())
			return;

		this.setROIDragging(false);

		for (ROIShape s : rois) {
			deleteROI(s);
		}

		displayPanel.setAdditionalText("");
	}

	@Override
	public void deleteAllROIs() {
		setROIDragging(false);
		displayPanel.setAdditionalText("");

		setCurrentROIShape(null);
		setClickedROIShape(null);
		setAllROIShapes(new ArrayList<ROIShape>());

		setCurrentEllipseROI(null);
		setEllipseROIs(new ArrayList<EllipseROI>());

		setFreehandShape(new GeneralPath());
		setCurrentFreehandROI(null);
		setFreehandROIs(new ArrayList<FreehandROI>());

		setCurrentLineROI(null);
		setLineROIs(new ArrayList<LineROI>());

		setPolygonShape(new GeneralPath());
		setCurrentPolygonROI(null);
		setPolygonROIs(new ArrayList<PolygonROI>());

		setCurrentRectangleROI(null);
		setRectangleROIs(new ArrayList<RectangleROI>());

		setCurrentAngleROI(null);
		setAngleROIs(new ArrayList<AngleROI>());

		setCurrentPointROI(null);
		setPointROIs(new ArrayList<PointROI>());

		setRoiShapeForHighlighting(null);

		repaint();
	}

	/**
	 * This method deletes the currently selected ROI and sets the last element
	 * of the {@link ArrayList} to the current ROI.
	 */
	@Override
	public void deleteSelectedROI() {
		this.setROIDragging(false);

		// ##################################################
		// search for the latest ROI drawn and set
		// it to the current ROI
		reOrganizeROIs(currentROIShape);

		// ##################################################
		// REMOVE the element from the all ROI shapes array list
		allROIShapes.remove(currentROIShape);

		// if there are any existing ROI shapes
		if (!allROIShapes.isEmpty()) {
			// set the latest drawn ROI shape in the look panel
			this.setCurrentROIShape(allROIShapes.get(allROIShapes.size() - 1));

			// set the latest drawn ROI shape in the ROI painter for
			// drawing boundaries
			setCurrentROIShape(allROIShapes.get(allROIShapes.size() - 1));
		}

		// no more ROI shapes are present
		else {
			setCurrentROIShape(null);
		}

		displayPanel.setAdditionalText("");

		// repaint the remaining shapes
		setRoiShapeForHighlighting(null);
		repaint();
	}

	/**
	 * @return the roiX0
	 */
	@Override
	public int getRoiX0() {
		return roiX0;
	}

	/**
	 * @param roiX0
	 *            the roiX0 to set
	 */
	@Override
	public void setRoiX0(int roiX0) {
		this.roiX0 = roiX0;
	}

	/**
	 * @return the roiY0
	 */
	@Override
	public int getRoiY0() {
		return roiY0;
	}

	/**
	 * @param roiY0
	 *            the roiY0 to set
	 */
	@Override
	public void setRoiY0(int roiY0) {
		this.roiY0 = roiY0;
	}

	/**
	 * @return the roiShiftX0
	 */
	@Override
	public int getRoiShiftX0() {
		return roiShiftX0;
	}

	/**
	 * @param roiShiftX0
	 *            the roiShiftX0 to set
	 */
	@Override
	public void setRoiShiftX0(int roiShiftX0) {
		this.roiShiftX0 = roiShiftX0;
	}

	/**
	 * @return the roiShiftY0
	 */
	@Override
	public int getRoiShiftY0() {
		return roiShiftY0;
	}

	/**
	 * @param roiShiftY0
	 *            the roiShiftY0 to set
	 */
	@Override
	public void setRoiShiftY0(int roiShiftY0) {
		this.roiShiftY0 = roiShiftY0;
	}

	/**
	 * @return the isROIDragging
	 */
	@Override
	public boolean isROIDragging() {
		return isROIDragging;
	}

	/**
	 * @param isROIDragging
	 *            the isROIDragging to set
	 */
	@Override
	public void setROIDragging(boolean isROIDragging) {
		this.isROIDragging = isROIDragging;
	}

	/**
	 * @return the isInsideRoi
	 */
	@Override
	public boolean isInsideRoi() {
		return isInsideRoi;
	}

	/**
	 * @param isInsideRoi
	 *            the isInsideRoi to set
	 */
	@Override
	public void setInsideRoi(boolean isInsideRoi) {
		this.isInsideRoi = isInsideRoi;
	}

	/**
	 * @return the isInsidePtNorthWest
	 */
	@Override
	public boolean isInsidePtNorthWest() {
		return isInsidePtNorthWest;
	}

	/**
	 * @param isInsidePtNorthWest
	 *            the isInsidePtNorthWest to set
	 */
	@Override
	public void setInsidePtNorthWest(boolean isInsidePtNorthWest) {
		this.isInsidePtNorthWest = isInsidePtNorthWest;
	}

	/**
	 * @return the isInsidePtNorth
	 */
	@Override
	public boolean isInsidePtNorth() {
		return isInsidePtNorth;
	}

	/**
	 * @param isInsidePtNorth
	 *            the isInsidePtNorth to set
	 */
	@Override
	public void setInsidePtNorth(boolean isInsidePtNorth) {
		this.isInsidePtNorth = isInsidePtNorth;
	}

	/**
	 * @return the isInsidePtNorthEast
	 */
	@Override
	public boolean isInsidePtNorthEast() {
		return isInsidePtNorthEast;
	}

	/**
	 * @param isInsidePtNorthEast
	 *            the isInsidePtNorthEast to set
	 */
	@Override
	public void setInsidePtNorthEast(boolean isInsidePtNorthEast) {
		this.isInsidePtNorthEast = isInsidePtNorthEast;
	}

	/**
	 * @return the isInsidePtEast
	 */
	@Override
	public boolean isInsidePtEast() {
		return isInsidePtEast;
	}

	/**
	 * @param isInsidePtEast
	 *            the isInsidePtEast to set
	 */
	@Override
	public void setInsidePtEast(boolean isInsidePtEast) {
		this.isInsidePtEast = isInsidePtEast;
	}

	/**
	 * @return the isInsidePtSouthEast
	 */
	@Override
	public boolean isInsidePtSouthEast() {
		return isInsidePtSouthEast;
	}

	/**
	 * @param isInsidePtSouthEast
	 *            the isInsidePtSouthEast to set
	 */
	@Override
	public void setInsidePtSouthEast(boolean isInsidePtSouthEast) {
		this.isInsidePtSouthEast = isInsidePtSouthEast;
	}

	/**
	 * @return the isInsidePtSouth
	 */
	@Override
	public boolean isInsidePtSouth() {
		return isInsidePtSouth;
	}

	/**
	 * @param isInsidePtSouth
	 *            the isInsidePtSouth to set
	 */
	@Override
	public void setInsidePtSouth(boolean isInsidePtSouth) {
		this.isInsidePtSouth = isInsidePtSouth;
	}

	/**
	 * @return the isInsidePtSouthWest
	 */
	@Override
	public boolean isInsidePtSouthWest() {
		return isInsidePtSouthWest;
	}

	/**
	 * @param isInsidePtSouthWest
	 *            the isInsidePtSouthWest to set
	 */
	@Override
	public void setInsidePtSouthWest(boolean isInsidePtSouthWest) {
		this.isInsidePtSouthWest = isInsidePtSouthWest;
	}

	/**
	 * @return the isInsidePtWest
	 */
	@Override
	public boolean isInsidePtWest() {
		return isInsidePtWest;
	}

	/**
	 * @param isInsidePtWest
	 *            the isInsidePtWest to set
	 */
	@Override
	public void setInsidePtWest(boolean isInsidePtWest) {
		this.isInsidePtWest = isInsidePtWest;
	}

	/**
	 * Create a new context popup menu with this instance as
	 * {@link ActionListener}. All commands have to be implemented in this
	 * class.
	 */
	protected void createContextMenuAndBehavior() {
		contextPopupMenu = new ContextPopupMenu(this.displayPanel);
		contextPopupListener = new ContextPopupListener(contextPopupMenu);
	}

	/**
	 * Reorganize the ROI shapes and add set the last drawn element to the
	 * current shape.
	 * 
	 */
	private void reOrganizeROIs(ROIShape currentROIShape) {

		// ##################################################
		// REMOVE the shape from the individual array lists
		// and set the current elements to null

		if (currentROIShape instanceof EllipseROI) {

			ellipseROIs.remove(currentROIShape);

			// check whether there are any other elements
			if (!this.getEllipseROIs().isEmpty()) {
				this.setCurrentEllipseROI(ellipseROIs.get(ellipseROIs.size() - 1));
			} else {
				this.setCurrentEllipseROI(null);
			}
		}

		else if (currentROIShape instanceof FreehandROI) {
			freehandROIs.remove(currentROIShape);
			this.setFreehandShape(new GeneralPath());

			// check whether there are any other elements
			if (!freehandROIs.isEmpty()) {
				this.setCurrentFreehandROI(freehandROIs.get(freehandROIs.size() - 1));
			} else {
				this.setCurrentFreehandROI(null);
			}
		}

		else if (currentROIShape instanceof LineROI) {
			lineROIs.remove(currentROIShape);
			// check whether there are any other elements
			if (!lineROIs.isEmpty()) {
				this.setCurrentLineROI(lineROIs.get(lineROIs.size() - 1));
			} else {
				this.setCurrentLineROI(null);
			}
		}

		else if (currentROIShape instanceof PolygonROI) {
			polygonROIs.remove(currentROIShape);
			this.setPolygonShape(new GeneralPath());

			// check whether there are any other elements
			if (!polygonROIs.isEmpty()) {
				this.setCurrentPolygonROI(polygonROIs.get(polygonROIs.size() - 1));
			} else {
				this.setCurrentPolygonROI(null);
			}
		}

		else if (currentROIShape instanceof RectangleROI) {
			rectangleROIs.remove(currentROIShape);
			// check whether there are any other elements
			if (!rectangleROIs.isEmpty()) {
				this.setCurrentRectangleROI(rectangleROIs.get(rectangleROIs
						.size() - 1));
			} else {
				this.setCurrentRectangleROI(null);
			}
		}

		else if (currentROIShape instanceof AngleROI) {
			angleROIs.remove(currentROIShape);
			// check whether there are any other elements
			if (!angleROIs.isEmpty()) {
				this.setCurrentAngleROI(angleROIs.get(angleROIs.size() - 1));
			} else {
				this.setCurrentAngleROI(null);
			}
		}

		else if (currentROIShape instanceof PointROI) {
			pointROIs.remove(currentROIShape);
			// check whether there are any other elements
			if (!pointROIs.isEmpty()) {
				this.setCurrentPointROI(pointROIs.get(pointROIs.size() - 1));
			} else {
				this.setCurrentPointROI(null);
			}
		}

	}

	@Override
	public void setBoundEditableRectangles(ROIShape[] rs) {
		boundEditableRectangles = rs;
	}

	public ROIShape[] getBoundEditableRectangles() {
		return boundEditableRectangles;
	}

	public ROIShape[] getBoundEditableRectanglesUntransformed() {
		return boundEditableRectanglesUntransformed;
	}

	@Override
	public void setBoundEditableRectanglesUntransformed(
			ROIShape[] boundEditableRectanglesUntransformed) {
		this.boundEditableRectanglesUntransformed = boundEditableRectanglesUntransformed;
	}

	@Override
	public Color getDefaultLayerColor() {
		return defaultLayerColor;
	}

	@Override
	public void setDefaultLayerColor(Color defaultLayerColor) {
		this.defaultLayerColor = defaultLayerColor;
	}

	@Override
	public BasicStroke getDefaultROIStroke() {
		return defaultROIStroke;
	}

	@Override
	public void setDefaultROIStroke(BasicStroke defaultROIStroke) {
		this.defaultROIStroke = defaultROIStroke;
	}

	@Override
	public Color getLineROIBoundaryColor() {
		return lineROIBoundaryColor;
	}

	public void setLineROIBoundaryColor(Color lineROIBoundaryColor) {
		this.lineROIBoundaryColor = lineROIBoundaryColor;
	}

	public Color getTmpROIShapeColor() {
		return tmpROIShapeColor;
	}

	public void setTmpROIShapeColor(Color tmpROIShapeColor) {
		this.tmpROIShapeColor = tmpROIShapeColor;
	}

	@Override
	public Color getRoiBoundaryRectangleColor() {
		return roiBoundaryRectangleColor;
	}

	public void setRoiBoundaryRectangleColor(Color roiBoundaryRectangleColor) {
		this.roiBoundaryRectangleColor = roiBoundaryRectangleColor;
	}

	@Override
	public ROIShape getRoiShapeForHighlighting() {
		return roiShapeForHighlighting;
	}

	@Override
	public Color getLineROIBoundaryRectangleColor() {
		return lineROIBoundaryRectangleColor;
	}

	public void setLineROIBoundaryRectangleColor(
			Color lineROIBoundaryRectangleColor) {
		this.lineROIBoundaryRectangleColor = lineROIBoundaryRectangleColor;
	}

	public void setRoiShapeForHighlighting(ROIShape roiShapeForHighlighting) {
		this.roiShapeForHighlighting = roiShapeForHighlighting;
	}

	/**
	 * Resets the ROI shape for highlighting to <code>null</code>.
	 */
	@Override
	public void removeRoiShapeForHighlighting() {
		this.roiShapeForHighlighting = null;
	}

	@Override
	public Color getHighlightColor() {
		return highlightColor;
	}

	@Override
	public void setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
	}

	@Override
	public BasicStroke getHighlightStroke() {
		return highlightStroke;
	}

	@Override
	public void setHighlightStroke(BasicStroke highlightStroke) {
		this.highlightStroke = highlightStroke;
	}

	/**
	 * This paints the entire component including all ROIs.
	 * 
	 * @param g
	 */
	public void paintComponent(Graphics g) {
		long start = System.currentTimeMillis();

		Graphics2D g2 = (Graphics2D) g;

		// Speed up drawing by restricting the viewable area
		JViewport vp = displayPanel.getScrollPane().getViewport();
		Point location = vp.getViewPosition();
		Dimension extendSize = vp.getExtentSize();

		Rectangle clip = new Rectangle(location, extendSize);
		g.setClip(clip);

		if (!isVisible) {
			System.out.println("Layer " + getZOrder() + " is NOT visible.");
			// remove all painted shapes
			float alpha = 0.0f;
			int type = AlphaComposite.SRC_OVER;
			AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
			g2.setComposite(composite);
			g2.fillRect(0, 0, getWidth(), getHeight());
		} else {
			logger.debug("Layer " + getZOrder() + " is visible.");
			logger.trace(Thread.currentThread().getName()
					+ " paints ROI Shapes of layer [" + this.getZOrder()
					+ "]...");

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setStroke(this.defaultROIStroke);
			g2.setColor(this.defaultLayerColor);

			double zoom;
			try {
				zoom = displayPanel.getZoom();
			} catch (NullPointerException npe) {
				logger.error("An error occurred: ", npe);
				return;
			}

			AffineTransform at = new AffineTransform(zoom, 0.0d, 0.0d, zoom,
					0.0d, 0.0d);

			// paint all ROI shapes of this layer
			if (this.allROIShapes != null) {
				Iterator<ROIShape> iter = this.allROIShapes.iterator();
				while (iter.hasNext()) {
					ROIShape roiShape = iter.next();
					ROIShape roiShapeTrans = new ROIShape(roiShape
							.transform(at).getAsShape());
					g2.setColor(defaultLayerColor);
					if (roiShape instanceof PointROI) {
						// g2.setColor(Color.cyan);
						g2.fill(roiShapeTrans.getAsShape());
					} else {
						g2.draw(roiShapeTrans.getAsShape());
					}
				}
			}

			// temporary roi shape
			if (this.tmpROIShape != null) {
				g2.setColor(tmpROIShapeColor);
				g2.draw(tmpROIShape.transform(at).getAsShape());
				removeTmpROIShape();
			}

			// if this instance is the active roi layer, paint the edit buttons,
			// too
			if (this == displayPanel.getCurrentROILayer()) {
				if ((this.currentROIShape != null)
						&& (displayPanel.getToolboxPanel().getButtonEditRoi()
								.isSelected())) {

					// log the incoming ROI shape type
					logger.trace(this.currentROIShape);

					ROIShape roiShape = new ROIShape(this.currentROIShape
							.transform(at).getAsShape());

					if (this.currentROIShape instanceof LineROI) {

						// get the coordinates of the line
						Point2D lineROIStartPoint = ((LineROI) currentROIShape)
								.getStartPoint();
						Point2D lineROIEndPoint = ((LineROI) currentROIShape)
								.getEndPoint();

						logger.trace(lineROIStartPoint + "|" + lineROIEndPoint);

						g2.setStroke(lineROIBoundaryStroke);
						g2.setColor(lineROIBoundaryColor);
						g2.draw(roiShape.getBounds());

						Rectangle[] rectArray = new Rectangle[2];
						rectArray[0] = new Rectangle(
								(int) lineROIStartPoint.getX() - 3,
								(int) lineROIStartPoint.getY() - 3, 6, 6);
						rectArray[1] = new Rectangle(
								(int) lineROIEndPoint.getX() - 3,
								(int) lineROIEndPoint.getY() - 3, 6, 6);

						g2.setColor(lineROIBoundaryRectangleColor);
						g2.fill(rectArray[0]);
						g2.fill(rectArray[1]);

						// Calculate edit points in real coordinates
						ROIShape[] rectShapesUntransformed = new ROIShape[2];
						for (int i = 0; i < 2; i++) {
							logger.trace("Bounds of LineROI element untransformed: "
									+ rectArray[i].getBounds());
							ROIShape rectShape = new ROIShape(rectArray[i]);
							rectShapesUntransformed[i] = rectShape;
						}
						setBoundEditableRectanglesUntransformed(rectShapesUntransformed);

						// Calculate edit points in real coordinates
						ROIShape[] rectShapes = new ROIShape[2];
						for (int i = 0; i < 2; i++) {
							ROIShape rectShape = new ROIShape(rectArray[i]);
							AffineTransform at2 = new AffineTransform(
									1.0d / zoom, 0.0d, 0.0d, 1.0d / zoom, 0.0d,
									0.0d);
							rectShapes[i] = new ROIShape(rectShape.transform(
									at2).getAsShape());
						}
						setBoundEditableRectangles(rectShapes);
					} else if (this.currentROIShape instanceof PointROI) {
						// draw boundaries without transformable edges
						Rectangle bounds = roiShape.getBounds();
						logger.trace(bounds);
						g2.setColor(roiBoundaryColor);
						g2.setStroke(roiBoundaryStroke);
						bounds.grow(6, 6);
						bounds.setLocation(bounds.getLocation().x - 1,
								bounds.getLocation().y - 1);
						g2.draw(bounds);
					} else {
						Rectangle bounds = roiShape.getBounds();
						logger.trace(bounds);
						g2.setColor(roiBoundaryColor);
						g2.setStroke(roiBoundaryStroke);
						g2.draw(bounds);

						Point pt1 = new Point(bounds.x, bounds.y); // upper left
						Point pt2 = new Point(bounds.x + bounds.width / 2,
								bounds.y); // upper
											// middle
						Point pt3 = new Point(bounds.x + bounds.width, bounds.y); // upper
																					// right
						Point pt4 = new Point(bounds.x + bounds.width, bounds.y
								+ bounds.height / 2); // right middle
						Point pt5 = new Point(bounds.x + bounds.width, bounds.y
								+ bounds.height); // bottom right
						Point pt6 = new Point(bounds.x + bounds.width / 2,
								bounds.y + bounds.height); // bottom middle
						Point pt7 = new Point(bounds.x, bounds.y
								+ bounds.height); // bottom
													// left
						Point pt8 = new Point(bounds.x, bounds.y
								+ bounds.height / 2); // left
														// middle

						Rectangle[] rectArray = new Rectangle[8];
						rectArray[0] = new Rectangle(pt1.x - 3, pt1.y - 3, 6, 6);
						rectArray[1] = new Rectangle(pt2.x - 3, pt2.y - 3, 6, 6);
						rectArray[2] = new Rectangle(pt3.x - 3, pt3.y - 3, 6, 6);
						rectArray[3] = new Rectangle(pt4.x - 3, pt4.y - 3, 6, 6);
						rectArray[4] = new Rectangle(pt5.x - 3, pt5.y - 3, 6, 6);
						rectArray[5] = new Rectangle(pt6.x - 3, pt6.y - 3, 6, 6);
						rectArray[6] = new Rectangle(pt7.x - 3, pt7.y - 3, 6, 6);
						rectArray[7] = new Rectangle(pt8.x - 3, pt8.y - 3, 6, 6);
						g2.setColor(Color.white);
						for (int i = 0; i < 8; i++) {
							// draw the rectangles
							g2.fill(rectArray[i]);
						}

						g2.setColor(roiBoundaryRectangleColor);
						// Calculate edit points in real coordinates
						ROIShape[] rectShapesUntransformed = new ROIShape[8];
						for (int i = 0; i < 8; i++) {

							// draw the rectangles
							g2.draw(rectArray[i]);

							logger.trace("Bounds of non-LineROI element untransformed: "
									+ rectArray[i].getBounds());
							ROIShape rectShape = new ROIShape(rectArray[i]);
							rectShapesUntransformed[i] = rectShape;
						}
						this.setBoundEditableRectanglesUntransformed(rectShapesUntransformed);

						// Calculate edit points in real screen coordinates
						ROIShape[] rectShapes = new ROIShape[8];
						for (int i = 0; i < 8; i++) {
							ROIShape rectShape = new ROIShape(rectArray[i]);
							AffineTransform at2 = new AffineTransform(
									1.0d / zoom, 0.0d, 0.0d, 1.0d / zoom, 0.0d,
									0.0d);
							rectShapes[i] = new ROIShape(rectShape.transform(
									at2).getAsShape());
						}
						this.setBoundEditableRectangles(rectShapes);
					}
				}

				// draw bounds, if edit or arrow button is selected and the
				// current ROI
				// shape is not null
				if ((this.roiShapeForHighlighting != null)
						&& (displayPanel.getToolboxPanel().getButtonEditRoi()
								.isSelected() || displayPanel.getToolboxPanel()
								.getButtonArrow().isSelected())) {

					// log the incoming roi shape type
					logger.trace(this.roiShapeForHighlighting);

					ROIShape roiShape = new ROIShape(
							this.roiShapeForHighlighting.transform(at)
									.getAsShape());

					Rectangle bounds = roiShape.getBounds();
					logger.trace(bounds);
					g2.setColor(highlightColor);
					g2.setStroke(highlightStroke);

					if (roiShapeForHighlighting instanceof PointROI) {
						// grow the bounds
						bounds.grow(6, 6);
						// correct the center of the point
						bounds.setLocation(bounds.getLocation().x - 1,
								bounds.getLocation().y - 1);
					}
					g2.draw(bounds);

					removeRoiShapeForHighlighting();
				}

			}
		}
		logger.debug("Painted all stuff on layer [name=" + getName()
				+ ", zOrder=" + getZOrder() + "] in milliseconds: "
				+ (System.currentTimeMillis() - start) / 1.0d);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (this.displayPanel.getCurrentImage() == null)
			return;
		try {
			// if shift button is not pressed on wheel event, zoom +-25%
			if (e.isShiftDown() == false) {
				if (this.displayPanel.getCurrentImage() != null
						&& this.displayPanel.getToolboxPanel().getButtonZoom()
								.isSelected()) {

					double zoom = this.displayPanel.getZoom();

					// calculation and setting of the mouse click point
					Point mP = CommonTools.getRealPixelPosition(e.getPoint(),
							zoom);

					// get zoom factor (direction) from wheel rotation (precise
					// zooming, if the device supports it)
					// otherwise get rotations as integer
					int zf = e.getWheelRotation();

					if (zf < 0) { // for zooming in (enlarging)

						if (zoom >= this.displayPanel.getZoomHelper()
								.getZoomLevels().getLast()
								|| (zoom * -zf * 1.10d) >= this.displayPanel
										.getZoomHelper().getZoomLevels()
										.getLast()) {
							return;
						} else {
							zoom *= -zf * 1.10F;
							this.displayPanel.setZoom(zoom);
						}

					}
					if (zf > 0) { // for zooming out (shrinking)
						// check, if lower zoom is allowed
						double minDim = Math
								.min(this.displayPanel.getCurrentImage()
										.getWidth(), this.displayPanel
										.getCurrentImage().getHeight()) * 1.0d;
						if ((minDim * zoom) <= 1.25) {
							return;
						} else {
							zoom *= zf * 0.90F;
							this.displayPanel.setZoom(zoom);
						}
					}

					// zoom the image
					this.displayPanel.setRenderedImage(CommonTools.zoomImage(
							this.displayPanel.getCurrentImage(), zoom));

					this.displayPanel.updateImageLayer();

					this.displayPanel.setViewOffset(mP); // centers pixel if
															// possible
					this.displayPanel.getImageLayer().mouseMoved(e);
				}
			}

			// with shift pressed zoom according to the scale
			else if (e.isShiftDown() == true) {
				if (this.displayPanel.getCurrentImage() != null
						&& this.displayPanel.getToolboxPanel().getButtonZoom()
								.isSelected()) {

					double zoom = this.displayPanel.getZoom();

					// calculation and setting of the mouse click point
					Point mP = CommonTools.getRealPixelPosition(e.getPoint(),
							zoom);

					// get zoom factor (direction) from wheel rotation (precise
					// zooming, if the device supports it)
					// otherwise get rotations as integer
					int zf = e.getWheelRotation();

					if (zf < 0.0F) { // for zooming in (enlarging)
						if (zoom >= this.displayPanel.getZoomHelper()
								.getZoomLevels().getLast()) {
							return;
						} else {
							zoom = this.displayPanel.getZoomHelper()
									.getHigherZoomLevel(zoom);
							this.displayPanel.setZoom(zoom);
						}
					}
					if (zf > 0) { // for zooming out (shrinking)
						double minDim = Math
								.min(this.displayPanel.getCurrentImage()
										.getWidth(), this.displayPanel
										.getCurrentImage().getHeight()) * 1.0d;
						if ((minDim * zoom) <= 1.25) {
							return;
						} else {
							zoom = this.displayPanel.getZoomHelper()
									.getLowerZoomLevel(zoom);
							this.displayPanel.setZoom(zoom);
						}
					}
					// zoom the image
					this.displayPanel.setRenderedImage(CommonTools.zoomImage(
							this.displayPanel.getCurrentImage(), zoom));

					this.displayPanel.updateImageLayer();

					this.displayPanel.setViewOffset(mP); // centers pixel if
															// possible
					this.displayPanel.getImageLayer().mouseMoved(e);
				}
			}
		} catch (Exception ex) {
			logger.error("An error occurred: ", ex);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// System.out.println(e);
		if (this.displayPanel.getCurrentImage() == null)
			return;

		if (this.displayPanel == Application.getLook().getCurrentLookPanel()) {
			this.requestFocusInWindow();
			logger.trace("got focus.");
		}

		LookToolboxPanel toolbox = this.displayPanel.getToolboxPanel();

		// at first check for ZOOM events
		// ZOOM (pressed) is implemented in the image layer
		if (toolbox.getButtonZoom().isSelected()
				&& !canvasMover.isSpaceBarDown()) {

			if (!SwingUtilities.isLeftMouseButton(e)
					&& !SwingUtilities.isRightMouseButton(e)) {
				return;
			}

			displayPanel.getImageLayer().applyZoom(e);
		}

		// after ZOOM events there could be another event, thus don't use
		// "else if" here
		// --------------------------------------------------------------------------------------------------------
		// GENERAL MOUSE EVENTS ON LEFT BUTTON CLICK

		if (SwingUtilities.isLeftMouseButton(e)) {

			if (this != e.getSource())
				return;

			// get current mouse
			// position in real image
			// pixel values
			Point p = CommonTools.getRealPixelPosition(e.getPoint(),
					this.displayPanel.getZoom());
			// get the real point for the calculation, whether the click
			// happened inside a ROI or not
			Point screenP = e.getPoint();
			logger.debug("Image pixel point: " + p);
			logger.debug("Screen pixel point: " + screenP);

			// set the flag of dragging to true if the left mouse button is
			// pressed
			setROIDragging(true);

			// LINE (pressed)
			if ((toolbox.getButtonLine().isSelected())
					&& (SwingUtilities.isLeftMouseButton(e))) {

				// set the starting points for the line ROI drawing
				setRoiX0(p.x);
				setRoiY0(p.y);

				// adding multiple line ROIs to vector, if the shift key is
				// pressed at the mouse event
				if (e.isShiftDown()) {

					// remove existing line elements
					List<ROIShape> toDelete = new ArrayList<ROIShape>();
					// collect any existing line element in an array
					for (ROIShape shape : allROIShapes) {
						if (shape instanceof LineROI) {
							toDelete.add(shape);
						}
					}

					if (!toDelete.isEmpty()) {
						if (allROIShapes.removeAll(toDelete)) {
							logger.debug("Removed existing line ROI element(s): "
									+ toDelete);

							// renew the line ROI vector
							setLineROIs(new ArrayList<LineROI>());
							setCurrentLineROI(null);
							setCurrentROIShape(null);
							repaint();
						}
					} else {
						logger.debug("No line ROI element(s) present to remove!");
					}

					// remove the line length text from the panel
					this.displayPanel.setAdditionalText("");
				}
			}

			// RECTANGLE (pressed)
			else if (toolbox.getButtonRectangle().isSelected()) {

				// set the starting points for the rectangle ROI drawing
				setRoiX0(p.x);
				setRoiY0(p.y);

				// adding multiple rectangle ROIs to vector, if the shift key is
				// not pressed at the mouse event
				if (e.isShiftDown()) {

					// remove existing rectangle elements
					List<ROIShape> toDelete = new ArrayList<ROIShape>();
					// collect any existing rectangle element in an array
					for (ROIShape shape : allROIShapes) {
						if (shape instanceof RectangleROI) {
							toDelete.add(shape);
						}
					}

					if (!toDelete.isEmpty()) {
						if (allROIShapes.removeAll(toDelete)) {
							logger.debug("Removed existing rectangle ROI element(s): "
									+ toDelete);

							// renew the rectangle ROI vector
							setRectangleROIs(new ArrayList<RectangleROI>());
							setCurrentRectangleROI(null);
							setCurrentROIShape(null);
							repaint();
						}
					} else {
						logger.debug("No rectangle ROI element(s) present to remove!");
					}
				}
			}

			// OVAL (pressed)
			else if (toolbox.getButtonOval().isSelected()) {

				// set the starting points for the ellipse ROI drawing
				setRoiX0(p.x);
				setRoiY0(p.y);

				// adding multiple ellipse ROIs to vector, if the shift key is
				// not pressed at the mouse event
				if (e.isShiftDown()) {
					logger.debug("Pressing left mouse button on the ellipse ROI, shift is down.");
					// remove existing ellipse ROIs
					List<ROIShape> toDelete = new ArrayList<ROIShape>();
					for (ROIShape ellipse : allROIShapes) {
						if (ellipse instanceof EllipseROI) {
							toDelete.add(ellipse);
						}
					}
					if (!toDelete.isEmpty()) {
						if (allROIShapes.removeAll(toDelete)) {
							logger.debug("Removed existing ellipse ROI element(s): "
									+ toDelete);

							// renew the ellipse ROI vector
							setEllipseROIs(new ArrayList<EllipseROI>());
							setCurrentEllipseROI(null);
							setCurrentROIShape(null);
							repaint();
						}
					} else {
						logger.debug("No ellipse ROI element(s) present to remove!");
					}
				} else {
					logger.debug("Pressing left mouse button on the ellipse ROI, shift is not down.");
				}
			}

			// FREEHAND (pressed)
			else if (toolbox.getButtonFreehand().isSelected()) {

				// adding multiple freehand ROIs to vector, if the shift key is
				// not pressed at the mouse event
				if (e.isShiftDown()) {
					logger.debug("Pressing left mouse button on the freehand ROI, shift is down.");
					// remove existing freehand ROIs
					List<ROIShape> toDelete = new ArrayList<ROIShape>();
					for (ROIShape freehandROI : allROIShapes) {
						if (freehandROI instanceof FreehandROI) {
							toDelete.add(freehandROI);
						}
					}
					if (!toDelete.isEmpty()) {
						if (allROIShapes.removeAll(toDelete)) {
							logger.debug("Removed existing freehand ROI element(s): "
									+ toDelete);

							// renew the freehand ROI vector
							setFreehandROIs(new ArrayList<FreehandROI>());
							setCurrentFreehandROI(null);
							setCurrentROIShape(null);
							repaint();
						}
					} else {
						logger.debug("No freehand ROI element(s) present to remove!");
					}
				} else {
					logger.debug("Pressing left mouse button on the freehand ROI, shift is not down.");
				}

				// initialize a new path for the freehand object
				setFreehandShape(new GeneralPath());
				// set the starting point of the poly line
				getFreehandShape().moveTo(p.x, p.y);
			}

			// POLYGON (pressed)
			else if (toolbox.getButtonPolygon().isSelected()) {

				// initial mouse pressed event (new polygon)
				if (getCurrentPolygonROI() == null) {

					boolean deletedSomePolygonROIs = false;

					// adding multiple polygon ROIs to vector, if the shift key
					// is not pressed at the mouse event
					if (e.isShiftDown()) {
						logger.debug("Pressing left mouse button on the polygon ROI, shift is down.");
						// remove existing polygon ROIs
						List<ROIShape> toDelete = new ArrayList<ROIShape>();
						for (ROIShape polygonROI : allROIShapes) {
							if (polygonROI instanceof PolygonROI) {
								toDelete.add(polygonROI);
							}
						}
						if (!toDelete.isEmpty()) {
							if (allROIShapes.removeAll(toDelete)) {
								logger.debug("Removed existing polygon ROI element(s): "
										+ toDelete);

								// renew the polygon ROI vector
								setPolygonROIs(new ArrayList<PolygonROI>());
								setCurrentPolygonROI(null);
								setCurrentROIShape(null);
								repaint();

								deletedSomePolygonROIs = true;
							}
						} else {
							logger.debug("No polygon ROI element(s) present to remove!");
						}
					} else {
						logger.debug("Pressing left mouse button on the polygon ROI, shift is not down.");
					}

					if (!deletedSomePolygonROIs) {
						// initialize a new path for the polygon object
						setPolygonShape(new GeneralPath());

						// set the starting point of the polygon object
						// to the current point
						getPolygonShape().moveTo(p.x, p.y);

						// set the number of polygon corners to 1 for the first
						// point
						setNumPolyPoints(1);

						// temporarily set the current polygon ROI
						setCurrentPolygonROI(new PolygonROI(getPolygonShape()));
					}

				}

				// if more points are added to the current polygon
				else if (currentPolygonROI != null) {

					// set the next point of the polygon object
					// to the current point
					getPolygonShape().lineTo(p.x, p.y);

					// set the number to number+1 for all other n points
					setNumPolyPoints(numPolyPoints + 1);

					// construct the polygon roi
					PolygonROI tmpROI = new PolygonROI(polygonShape);

					// set the currently drawn polygon ROI to the display panel
					setCurrentPolygonROI(tmpROI);

					setCurrentROIShape(currentPolygonROI);

					// set the temporary ROI shape to be drawn in this event
					setTmpROIShape(tmpROI);

					repaint();
				}

			}

			// ANGLE (pressed)
			else if (toolbox.getButtonAngle().isSelected()) {

				// initial mouse pressed event (new angle)
				if (currentAngleROI == null) {

					boolean deletedSomeAngleROIs = false;

					// adding multiple angle ROIs to vector, if the shift key is
					// not pressed at the mouse event
					if (e.isShiftDown()) {
						logger.debug("Pressing left mouse button on the angle ROI, shift is down.");
						// remove existing angle ROIs
						List<ROIShape> toDelete = new ArrayList<ROIShape>();
						for (ROIShape angleROI : allROIShapes) {
							if (angleROI instanceof AngleROI) {
								toDelete.add(angleROI);
							}
						}
						if (!toDelete.isEmpty()) {
							if (allROIShapes.removeAll(toDelete)) {
								logger.debug("Removed existing angle ROI element(s): "
										+ toDelete);

								// renew the angle ROI vector
								setAngleROIs(new ArrayList<AngleROI>());
								setCurrentAngleROI(null);
								setCurrentROIShape(null);
								repaint();

								deletedSomeAngleROIs = true;
							}
						} else {
							logger.debug("No angle ROI element(s) present to remove!");
						}
					} else {
						logger.debug("Pressing left mouse button on the angle ROI, shift is not down.");
					}

					if (!deletedSomeAngleROIs) {
						logger.debug("mouse pressed (click #1) on angle button");

						// initialize a new path for the angle object
						setAngleShape(new GeneralPath());

						// set the starting point of the polygon object
						// to the current point
						angleShape.moveTo(p.x, p.y);

						// set the number of angle ROI points to 1 for the first
						// point
						setNumAnglePoints(1);

						// temporarily set the current polygon ROI
						setCurrentAngleROI(new AngleROI(angleShape));
					}
				}

				// if more points are added to the current angle ROI
				// there are up to 3 points possible
				else if (currentAngleROI != null && numAnglePoints == 1) {

					logger.debug("mouse pressed (click #2) on angle button");

					// set the next point of the angle object
					// to the current point
					angleShape.lineTo(p.x, p.y);

					// set the number to number+1 for all other n points
					setNumAnglePoints(numAnglePoints + 1);

					// construct the angle roi
					AngleROI tmpROI = new AngleROI(angleShape);

					// set the currently drawn angle ROI to the display panel
					setCurrentAngleROI(tmpROI);

					setCurrentROIShape(currentAngleROI);

					setTmpROIShape(tmpROI);

					repaint();
				}

				else if (currentAngleROI != null && numAnglePoints == 2) {

					boolean add = false;

					if (e.isShiftDown() && !angleROIs.isEmpty()) {
						// ask the user to confirm closing the shape or deleting all
						// other shapes because he might have forgotten to use shift on the
						// right mouse button
						int selection = DialogUtil
								.getInstance()
								.showDefaultQuestionMessage(
										I18N.getMessage("question.finish.roi.angle"));

						// on yes add it to all other paths
						if (selection == IDialogUtil.YES_OPTION) {
							add = true;
						}
						// on no don't add
						else if (selection == IDialogUtil.NO_OPTION) {
							add = false;
						}
						// on ESC or cancel return
						else {
							return;
						}
					}

					// the add flag is just read, if the user does not press
					// shift concurrently with the right mouse button.
					if (!e.isShiftDown() || add) {
						logger.debug("mouse pressed (last click #3) and shift not down on angle button");

						// get the current angle path and finish the angle roi
						angleShape.lineTo(p.x, p.y);
						angleShape.moveTo(p.x, p.y);
						angleShape.closePath();

						// construct a new angle ROI with the final path
						AngleROI finalAngleROI = new AngleROI(angleShape);

						// remove unfinished angle roi from all vectors
						allROIShapes.remove(currentAngleROI);
						angleROIs.remove(currentAngleROI);

						// set the last angle roi to the current variable
						setCurrentAngleROI(finalAngleROI);

						// add the new angle roi element
						angleROIs.add(currentAngleROI);
						allROIShapes.add(currentAngleROI);

						// notify listeners about adding a new angle ROI
						pcs.firePropertyChange(new AngleROIAddedEvent(this,
								angleROIs));

						setCurrentROIShape(currentAngleROI);

						fireNewProperties(currentAngleROI);

						repaint();

						// reset the variables for the next ROI
						setCurrentAngleROI(null);
						setAngleShape(null);
						setNumAnglePoints(0);
					} else {
						logger.debug("mouse pressed (last click #3) on angle button, shift is down");

						// remove all existing angle ROIs
						List<ROIShape> toDelete = new ArrayList<ROIShape>();
						for (ROIShape angle : allROIShapes) {
							if (angle instanceof AngleROI) {
								toDelete.add(angle);
							}
						}
						if (!toDelete.isEmpty()) {
							if (allROIShapes.removeAll(toDelete)) {
								logger.debug("Removed existing angle ROI element(s): "
										+ toDelete);
							}
						} else {
							logger.debug("No angle ROI element(s) present to remove!");
						}

						// renew the angle ROI vector
						setAngleROIs(new ArrayList<AngleROI>());

						// get the current angle path and finish the angle roi
						angleShape.lineTo(p.x, p.y);
						angleShape.moveTo(p.x, p.y);
						angleShape.closePath();

						// construct a new angle ROI with the final path
						AngleROI finalAngleROI = new AngleROI(angleShape);

						// set the last angle roi to the current variable
						setCurrentAngleROI(finalAngleROI);

						// add the last angle ROI
						angleROIs.add(currentAngleROI);
						allROIShapes.add(currentAngleROI);

						setCurrentROIShape(currentAngleROI);

						// notify listeners about adding a new angle ROI
						pcs.firePropertyChange(new AngleROIAddedEvent(this,
								angleROIs));

						fireNewProperties(currentAngleROI);

						repaint();

						// reset the variables for the next ROI
						setCurrentAngleROI(null);
						setAngleShape(null);
						setNumAnglePoints(0);
					}
				}
			}

			// POINT (pressed)
			else if (toolbox.getButtonPoint().isSelected()) {

				// set the starting coordinates for the point ROI drawing
				setRoiX0(p.x);
				setRoiY0(p.y);

				// adding multiple point ROIs to vector, if the shift key is
				// not pressed at the mouse event
				if (e.isShiftDown()) {

					// remove existing rectangle elements
					List<ROIShape> toDelete = new ArrayList<ROIShape>();
					// collect any existing rectangle element in an array
					for (ROIShape shape : allROIShapes) {
						if (shape instanceof PointROI) {
							toDelete.add(shape);
						}
					}

					if (!toDelete.isEmpty()) {
						if (allROIShapes.removeAll(toDelete)) {
							logger.debug("Removed existing point ROI element(s): "
									+ toDelete);

							// renew the point ROI vector
							setPointROIs(new ArrayList<PointROI>());
							setCurrentPointROI(null);
							setCurrentROIShape(null);
							repaint();
						}
					} else {
						logger.debug("No point ROI element(s) present to remove!");
					}
				}
			}

			// mouse search for ROI click
			else if (toolbox.getButtonArrow().isSelected()) {
				// return, if no shapes are present
				if (allROIShapes.isEmpty() || currentROIShape == null) {
					return;
				}
				searchForInsideROIClick(screenP, e);
			}

			// EDIT ROI (pressed)
			else if (toolbox.getButtonEditRoi().isSelected()) {

				// check for a currently selected object
				logger.debug("Current ROI Shape: " + currentROIShape);

				// return, if no shapes are present
				if (allROIShapes.isEmpty() || currentROIShape == null) {
					return;
				}

				setInsideRoi(false);
				setInsidePtNorthWest(false);
				setInsidePtNorth(false);
				setInsidePtNorthEast(false);
				setInsidePtEast(false);
				setInsidePtSouthEast(false);
				setInsidePtSouth(false);
				setInsidePtSouthWest(false);
				setInsidePtWest(false);

				ROIShape[] rectShapes = getBoundEditableRectanglesUntransformed();

				// treat line different than all others
				if (currentROIShape instanceof LineROI) {

					// print the boundary rectangles
					// for (int i = 0; i < 2; i++)
					// logger.debug("Boundary rectangle[" + i + "]: "
					// + rectShapes[i].getBounds());

					// Test the condition whether or not
					// the mouse click occurred within the ROI boundary
					// rectangles
					setInsidePtNorthWest(rectShapes[0].contains(screenP.x,
							screenP.y));
					setInsidePtNorth(rectShapes[1].contains(screenP.x,
							screenP.y));

					// check first, if the mouseEvent occurs inside of any
					// boundary point
					// of the selected object
					if ((isInsidePtNorthWest()) || (isInsidePtNorth())) {

						// the opposite point of the clicked boundary ROI
						// rectangle
						Point origin = null;

						// log the mouse click
						String strBoundaryPoint = "";
						if (isInsidePtNorthWest()) {
							strBoundaryPoint = "start point of lineROI";
							origin = CommonTools.getRealPixelPosition(
									((LineROI) currentROIShape).getEndPoint(),
									this.displayPanel.getZoom());

						} else if (isInsidePtNorth()) {
							strBoundaryPoint = "end point of lineROI";
							origin = CommonTools
									.getRealPixelPosition(
											((LineROI) currentROIShape)
													.getStartPoint(),
											this.displayPanel.getZoom());
						}

						// set start and end point for line ROI drawing
						// in the display panel
						// these values have been set in the look display JAI
						setRoiX0(origin.x);
						setRoiY0(origin.y);

						logger.debug("MouseEvent occured inside ROI boundary point: "
								+ strBoundaryPoint);

						setClickedROIShape(currentROIShape);
						setInsideRoi(false);

						setCurrentLineROI((LineROI) currentROIShape);

						// display the line length of the roi
						fireNewProperties(currentLineROI);

						// fire additional mouse events
						this.displayPanel.getImageLayer().mouseMoved(e);
					} else {
						this.searchForInsideROIClick(p, e);
					}
				}

				// treat the POINT different than all others in editing
				else if (currentROIShape instanceof PointROI) {
					// enable translation only
					this.searchForInsideROIClick(p, e);
				}

				// for all other shapes
				else {
					// for (int i = 0; i < 8; i++)
					// logger.debug("rectangle Bounds: "
					// + rectShapes[i].getBounds());

					setInsidePtNorthWest(rectShapes[0].contains(screenP.x,
							screenP.y));
					setInsidePtNorth(rectShapes[1].contains(screenP.x,
							screenP.y));
					setInsidePtNorthEast(rectShapes[2].contains(screenP.x,
							screenP.y));
					setInsidePtEast(rectShapes[3]
							.contains(screenP.x, screenP.y));
					setInsidePtSouthEast(rectShapes[4].contains(screenP.x,
							screenP.y));
					setInsidePtSouth(rectShapes[5].contains(screenP.x,
							screenP.y));
					setInsidePtSouthWest(rectShapes[6].contains(screenP.x,
							screenP.y));
					setInsidePtWest(rectShapes[7]
							.contains(screenP.x, screenP.y));

					// check first, if the mouseEvent occurs inside of any
					// boundary point
					// of the selected object
					if ((isInsidePtNorthWest()) || (isInsidePtNorth())
							|| (isInsidePtNorthEast()) || (isInsidePtEast())
							|| (isInsidePtSouthEast()) || (isInsidePtSouth())
							|| (isInsidePtSouthWest()) || (isInsidePtWest())) {
						String boundaryPoint = "";
						if (isInsidePtNorthWest()) {
							boundaryPoint = "top left";
						} else if (isInsidePtNorth()) {
							boundaryPoint = "top middle";
						} else if (isInsidePtNorthEast()) {
							boundaryPoint = "top right";
						} else if (isInsidePtEast()) {
							boundaryPoint = "center right";
						} else if (isInsidePtSouthEast()) {
							boundaryPoint = "bottom right";
						} else if (isInsidePtSouth()) {
							boundaryPoint = "bottom middle";
						} else if (isInsidePtSouthWest()) {
							boundaryPoint = "bottom left";
						} else if (isInsidePtWest()) {
							boundaryPoint = "center left";
						}

						logger.debug("MouseEvent occured inside ROI boundary point: "
								+ boundaryPoint);

						setClickedROIShape(currentROIShape);
						setInsideRoi(false);

						// INFO: mouse pressed on line ROI events are treated
						// above
						if (currentROIShape instanceof RectangleROI) {
							setCurrentRectangleROI((RectangleROI) currentROIShape);

							// calculate the area properties
							fireNewProperties(currentRectangleROI);

						} else if (currentROIShape instanceof EllipseROI) {
							setCurrentEllipseROI((EllipseROI) currentROIShape);

							// calculate the area properties
							fireNewProperties(currentEllipseROI);

						} else if (currentROIShape instanceof FreehandROI) {
							setCurrentFreehandROI((FreehandROI) currentROIShape);

						} else if (getCurrentROIShape() instanceof PolygonROI) {
							setCurrentPolygonROI((PolygonROI) currentROIShape);

						} else if (getCurrentROIShape() instanceof AngleROI) {
							setCurrentAngleROI((AngleROI) currentROIShape);

							// display the current angle ROI properties
							fireNewProperties(currentAngleROI);
						}

						// fire additional mouse moved event in order to
						// show new data
						this.displayPanel.getImageLayer().mouseMoved(e);
					} else {
						this.searchForInsideROIClick(p, e);
					}
				}
			}
		}

		else if (SwingUtilities.isRightMouseButton(e)) {
			// logger.debug("Right mouse button");

			// FREEHAND (right mouse button, close path, if it isn't already
			// closed)
			if (toolbox.getButtonFreehand().isSelected()
					&& currentFreehandROI != null
					&& !currentFreehandROI.isClosed()) {

				boolean add = false;

				FreehandROI currentROI = currentFreehandROI;
				FreehandROI firstROI = null;
				try {
					firstROI = freehandROIs.get(0);
				} catch (IndexOutOfBoundsException iobe) {
					logger.debug("The index is out of bounds, no element present in the FreehandROIs.");
				}

				if (e.isShiftDown() && firstROI != null
						&& !(currentROI == firstROI)) {

					// ask the user whether to close the shape or delete all
					// other shapes
					int selection = DialogUtil
							.getInstance()
							.showDefaultQuestionMessage(
									I18N.getMessage("question.finish.roi.freehand"));

					// on yes add it to all other paths
					if (selection == IDialogUtil.YES_OPTION) {
						add = true;
					}
					// on no don't add
					else if (selection == IDialogUtil.NO_OPTION) {
						add = false;
					}
					// on ESC or cancel return
					else {
						return;
					}
				}

				// the add flag is just read, if the user does not press shift
				// concurrently with the right mouse button.
				if (!e.isShiftDown() || add) {
					logger.debug("mouse pressed (right click) and shift not down on freehand button");

					// get the current freehand path and close it
					freehandShape.closePath();

					// construct a new freehand ROI with the final path
					FreehandROI finalFreehandROI = new FreehandROI(
							freehandShape);

					// remove unfinished freehand roi from all vectors
					allROIShapes.remove(currentFreehandROI);
					freehandROIs.remove(currentFreehandROI);

					// set the last freehand roi to the current variable
					setCurrentFreehandROI(finalFreehandROI);

					// add the new freehand roi element
					freehandROIs.add(currentFreehandROI);
					allROIShapes.add(currentFreehandROI);

					// notify listeners about adding a new freehand ROI
					pcs.firePropertyChange(new FreehandROIAddedEvent(this,
							freehandROIs));

					setCurrentROIShape(currentFreehandROI);

					repaint();

					setCurrentFreehandROI(null);
					setFreehandShape(null);
				} else {
					logger.debug("mouse pressed (right click) on freehand button, shift is down");

					// remove all existing freehand ROIs
					List<ROIShape> toDelete = new ArrayList<ROIShape>();
					for (ROIShape freehand : allROIShapes) {
						if (freehand instanceof FreehandROI) {
							toDelete.add(freehand);
						}
					}
					if (!toDelete.isEmpty()) {
						if (allROIShapes.removeAll(toDelete)) {
							logger.debug("Removed existing freehand ROI element(s): "
									+ toDelete);
						}
					} else {
						logger.debug("No freehand ROI element(s) present to remove!");
					}

					// get the current freehand path and close it
					freehandShape.closePath();

					// construct a new freehand ROI with the final path
					FreehandROI finalFreehandROI = new FreehandROI(
							freehandShape);

					// remove unfinished freehand roi from all vectors
					allROIShapes.remove(currentFreehandROI);

					// set the last free hand roi to the current variable
					setCurrentFreehandROI(finalFreehandROI);

					// renew the freehand ROI vector
					setFreehandROIs(new ArrayList<FreehandROI>());

					// add the last freehand ROI
					freehandROIs.add(currentFreehandROI);
					allROIShapes.add(currentFreehandROI);

					setCurrentROIShape(currentFreehandROI);

					// notify listeners about adding a new freehand ROI
					pcs.firePropertyChange(new FreehandROIAddedEvent(this,
							freehandROIs));

					repaint();

					setCurrentFreehandROI(null);
					setFreehandShape(null);
				}
			}

			// POLYGON (right mouse button, close path, if it isn't already
			// closed)
			if (toolbox.getButtonPolygon().isSelected()
					&& currentPolygonROI != null
					&& !currentPolygonROI.isClosed()) {

				boolean add = false;

				if (e.isShiftDown() && !polygonROIs.isEmpty()) {
					// ask the user whether to close the shape or delete all
					// other shapes
					// because he might have forgotten to press shift on the
					// right mouse button
					int selection = DialogUtil
							.getInstance()
							.showDefaultQuestionMessage(
									I18N.getMessage("question.finish.roi.polygon"));

					// on yes add it to all other paths
					if (selection == IDialogUtil.YES_OPTION) {
						add = true;
					}
					// on no don't add
					else if (selection == IDialogUtil.NO_OPTION) {
						add = false;
					}
					// on ESC or cancel return
					else {
						return;
					}
				}

				// the add flag is just read, if the user does not press shift
				// concurrently with the right mouse button.
				if (!e.isShiftDown() || add) {
					logger.debug("mouse pressed (right click) and shift not down on polygon button");

					// get the current polygon path and close it
					polygonShape.closePath();

					// construct a new polygon ROI with the final path
					PolygonROI finalPolygonROI = new PolygonROI(polygonShape);

					// remove unfinished polygon roi from all vectors
					allROIShapes.remove(currentPolygonROI);

					polygonROIs.remove(currentPolygonROI);

					// set the last polygon roi to the current variable
					setCurrentPolygonROI(finalPolygonROI);

					// add the new polygon roi element
					polygonROIs.add(currentPolygonROI);
					allROIShapes.add(currentPolygonROI);

					// notify listeners about adding a new polygon ROI
					pcs.firePropertyChange(new PolygonROIAddedEvent(this,
							polygonROIs));

					setCurrentROIShape(currentPolygonROI);

					repaint();

					// reset the variables for the next polygon ROI
					setCurrentPolygonROI(null);
					setPolygonShape(null);
					setNumPolyPoints(0);
				} else {
					logger.debug("mouse pressed (right click) on polygon button, shift is down");

					// remove all existing polygon ROIs
					List<ROIShape> toDelete = new ArrayList<ROIShape>();
					for (ROIShape polygon : allROIShapes) {
						if (polygon instanceof PolygonROI) {
							toDelete.add(polygon);
						}
					}
					if (!toDelete.isEmpty()) {
						if (allROIShapes.removeAll(toDelete)) {
							logger.debug("Removed existing polygon ROI element(s): "
									+ toDelete);
						}
					} else {
						logger.debug("No polygon ROI element(s) present to remove!");
					}

					// renew the polygon ROI vector
					setPolygonROIs(new ArrayList<PolygonROI>());

					// get the current polygon path and close it
					polygonShape.closePath();

					// construct a new polygon ROI with the final path
					PolygonROI finalPolygonROI = new PolygonROI(polygonShape);

					// set the last polygon roi to the current variable
					setCurrentPolygonROI(finalPolygonROI);

					// add the last polygon ROI
					polygonROIs.add(currentPolygonROI);
					allROIShapes.add(currentPolygonROI);

					setCurrentROIShape(currentPolygonROI);

					// notify listeners about adding a new polygon ROI
					pcs.firePropertyChange(new PolygonROIAddedEvent(this,
							polygonROIs));

					repaint();

					// reset the variables for the next ROI
					setCurrentPolygonROI(null);
					setPolygonShape(null);
					setNumPolyPoints(0);
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println(e.getSource());
		if (this.displayPanel.getCurrentImage() == null)
			return;

		// calculate the real pixel values of the mouse position
		Point p = CommonTools.getRealPixelPosition(e.getPoint(),
				this.displayPanel.getZoom());

		// set the dragging to false since the mouse button has been released
		setROIDragging(false);
		// set the inside roi to false since there is no roi selected right
		// after drawing
		setInsideRoi(false);

		LookToolboxPanel toolbox = this.displayPanel.getToolboxPanel();

		// LINE (released)
		if (toolbox.getButtonLine().isSelected() && currentLineROI != null) {
			logger.debug("mouse released on line ROI button");

			// add the new line ROI to the existing ones
			if (!e.isShiftDown()) {
				logger.debug("mouse released and shift down on line ROI button");
				lineROIs.add(currentLineROI);
				allROIShapes.add(currentLineROI);

				// notify listeners about adding a new line ROI
				pcs.firePropertyChange(new LineROIAddedEvent(this, lineROIs));

				setCurrentROIShape(currentLineROI);

				repaint();

				// calculate and set the ROI length
				fireNewProperties(currentLineROI);
			}

			else {
				logger.debug("mouse released on line ROI button");
				// remove all existing line ROI elements
				List<ROIShape> toDelete = new ArrayList<ROIShape>();
				for (ROIShape lineROI : allROIShapes) {
					if (lineROI instanceof LineROI) {
						toDelete.add(lineROI);
					}
				}
				if (!toDelete.isEmpty()) {
					if (allROIShapes.removeAll(toDelete)) {
						logger.debug("Removed existing line ROI element(s): "
								+ toDelete);
					}
				} else {
					logger.debug("No line ROI element(s) present to remove!");
				}

				// renew the line ROI vector
				setLineROIs(new ArrayList<LineROI>());

				// add the last line ROI
				lineROIs.add(currentLineROI);
				allROIShapes.add(currentLineROI);

				setCurrentROIShape(currentLineROI);

				// notify listeners about adding a new line ROI
				pcs.firePropertyChange(new LineROIAddedEvent(this, lineROIs));

				repaint();

				// calculate and set the ROI length
				fireNewProperties(currentLineROI);
				setCurrentLineROI(null);
			}
		}

		// RECTANGLE (released)
		if (toolbox.getButtonRectangle().isSelected()
				&& currentRectangleROI != null) {
			logger.debug("mouse released on rectangle button");

			if (!e.isShiftDown()) {
				logger.debug("mouse released and shift not down on rectangle button");
				rectangleROIs.add(currentRectangleROI);
				allROIShapes.add(currentRectangleROI);

				// notify listeners about adding a new rectangle ROI
				pcs.firePropertyChange(new RectangleROIAddedEvent(this,
						rectangleROIs));

				setCurrentROIShape(currentRectangleROI);

				repaint();

				// calculate the area properties
				fireNewProperties(currentRectangleROI);
			}

			else {
				logger.debug("mouse released with shift down on rectangle button");
				// remove all existing rectangle ROIs
				List<ROIShape> toDelete = new ArrayList<ROIShape>();
				for (ROIShape shape : allROIShapes) {
					if (shape instanceof RectangleROI) {
						toDelete.add(shape);
					}
				}
				if (!toDelete.isEmpty()) {
					if (allROIShapes.removeAll(toDelete)) {
						logger.debug("Removed existing rectangle ROI element(s): "
								+ toDelete);
					}
				} else {
					logger.debug("No rectangle ROI element(s) present to remove!");
				}

				// renew the rectangle ROI vector
				setRectangleROIs(new ArrayList<RectangleROI>());

				// add the last rectangle ROI
				rectangleROIs.add(currentRectangleROI);
				allROIShapes.add(currentRectangleROI);

				setCurrentROIShape(currentRectangleROI);

				// notify listeners about adding a new rectangle ROI
				pcs.firePropertyChange(new RectangleROIAddedEvent(this,
						rectangleROIs));

				repaint();

				// calculate the area properties
				fireNewProperties(currentRectangleROI);
				setCurrentRectangleROI(null);
			}

		}
		
		// single ROW (released)
		if (toolbox.getButtonSingleRow().isSelected()){
			int width = displayPanel.getCurrentImage().getWidth();
			Rectangle r = new Rectangle(0, p.y, width, 1);
			drawRectangleROI(new RectangleROI(r));
		}
		
		// single COLUMN (released)
		if (toolbox.getButtonSingleColumn().isSelected()){
			int height = displayPanel.getCurrentImage().getHeight();
			Rectangle r = new Rectangle(p.x, 0, 1, height);
			drawRectangleROI(new RectangleROI(r));
		}

		// OVAL (released)
		if (toolbox.getButtonOval().isSelected() && currentEllipseROI != null) {

			if (!e.isShiftDown()) {
				logger.debug("mouse released and shift not down on oval button");
				ellipseROIs.add(currentEllipseROI);
				allROIShapes.add(currentEllipseROI);

				// notify listeners about adding a new ellipse ROI
				pcs.firePropertyChange(new EllipseROIAddedEvent(this,
						ellipseROIs));

				setCurrentROIShape(currentEllipseROI);

				repaint();
			} else {
				logger.debug("mouse released on ellipse button, shift is down");
				// remove all existing ellipse ROIs
				List<ROIShape> toDelete = new ArrayList<ROIShape>();
				for (ROIShape ellipse : allROIShapes) {
					if (ellipse instanceof EllipseROI) {
						toDelete.add(ellipse);
					}
				}
				if (!toDelete.isEmpty()) {
					if (allROIShapes.removeAll(toDelete)) {
						logger.debug("Removed existing ellipse ROI element(s): "
								+ toDelete);
					}
				} else {
					logger.debug("No ellipse ROI element(s) present to remove!");
				}

				// renew the ellipse ROI vector
				setEllipseROIs(new ArrayList<EllipseROI>());

				// add the last ellipse ROI
				ellipseROIs.add(currentEllipseROI);
				allROIShapes.add(currentEllipseROI);

				setCurrentROIShape(currentEllipseROI);

				// notify listeners about adding a new ellipse ROI
				pcs.firePropertyChange(new EllipseROIAddedEvent(this,
						ellipseROIs));

				// calculate the area properties
				fireNewProperties(currentEllipseROI);

				repaint();

				setCurrentEllipseROI(null);
			}
		}

		// FREEHAND (release)
		else if (toolbox.getButtonFreehand().isSelected()
				&& currentFreehandROI != null) {

			if (!e.isShiftDown()) {
				logger.debug("mouse released and shift not down on freehand button");

				freehandROIs.add(currentFreehandROI);
				allROIShapes.add(currentFreehandROI);

				// notify listeners about adding a new freehand ROI
				pcs.firePropertyChange(new FreehandROIAddedEvent(this,
						freehandROIs));

				setCurrentROIShape(currentFreehandROI);

				repaint();
			} else {
				logger.debug("mouse released on freehand button, shift is down");
				// remove all existing freehand ROIs
				List<ROIShape> toDelete = new ArrayList<ROIShape>();
				for (ROIShape freehand : allROIShapes) {
					if (freehand instanceof FreehandROI) {
						toDelete.add(freehand);
					}
				}
				if (!toDelete.isEmpty()) {
					if (allROIShapes.removeAll(toDelete)) {
						logger.debug("Removed existing freehand ROI element(s): "
								+ toDelete);
					}
				} else {
					logger.debug("No freehand ROI element(s) present to remove!");
				}

				// renew the freehand ROI vector
				setFreehandROIs(new ArrayList<FreehandROI>());

				// add the last freehand ROI
				freehandROIs.add(currentFreehandROI);
				allROIShapes.add(currentFreehandROI);

				setCurrentROIShape(currentFreehandROI);

				// notify listeners about adding a new freehand ROI
				pcs.firePropertyChange(new FreehandROIAddedEvent(this,
						freehandROIs));

				repaint();

				// don't set the object to null and don't close the path
				setCurrentFreehandROI((FreehandROI) currentROIShape);
			}
		}
		// POINT (released)
		else if (toolbox.getButtonPoint().isSelected()
				&& SwingUtilities.isLeftMouseButton(e)) {
			logger.debug("mouse released on point ROI button");

			// add the new point ROI to the existing ones
			if (!e.isShiftDown()) {
				logger.debug("mouse released and shift not down on point ROI button");
				// create a new PointROI instance
				PointROI pointROI = new PointROI(p);

				// IMPORTANT: set the instance to the display panel,
				// the mouse listeners queries this field
				setCurrentPointROI(pointROI);

				pointROIs.add(currentPointROI);
				allROIShapes.add(currentPointROI);

				// notify listeners about adding a new point ROI
				pcs.firePropertyChange(new PointROIAddedEvent(this, pointROIs));

				setCurrentROIShape(currentPointROI);

				repaint();
			}

			else {
				logger.debug("mouse released on point ROI button");
				// remove all existing line ROI elements
				List<ROIShape> toDelete = new ArrayList<ROIShape>();
				for (ROIShape pointROI : allROIShapes) {
					if (pointROI instanceof PointROI) {
						toDelete.add(pointROI);
					}
				}
				if (!toDelete.isEmpty()) {
					if (allROIShapes.removeAll(toDelete)) {
						logger.debug("Removed existing point ROI element(s): "
								+ toDelete);
					}
				} else {
					logger.debug("No point ROI element(s) present to remove!");
				}

				// renew the point ROI vector
				setPointROIs(new ArrayList<PointROI>());

				// create a new PointROI instance
				PointROI pointROI = new PointROI(p);

				// IMPORTANT: set the instance to the display panel,
				// the mouse listeners queries this field
				setCurrentPointROI(pointROI);

				// add the last point ROI
				pointROIs.add(currentPointROI);
				allROIShapes.add(currentPointROI);

				setCurrentROIShape(currentPointROI);

				// notify listeners about adding a new point ROI
				pcs.firePropertyChange(new PointROIAddedEvent(this, pointROIs));

				repaint();
			}
		}

		// EDIT ROI (release)
		else if (toolbox.getButtonEditRoi().isSelected()
				&& !allROIShapes.isEmpty()) {
			logger.debug("EDIT ROI RELEASE");

			logger.debug(currentROIShape);

		}

		// ARROW (release)
		else if (toolbox.getButtonArrow().isSelected()
				&& !allROIShapes.isEmpty()) {
			logger.debug("BUTTON ARROW RELEASE");

			logger.debug(currentROIShape);
		}

		logger.debug("The vector 'allROIShapes' of layer " + this.getName()
				+ " contains: " + allROIShapes);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (displayPanel.getImageLayer() != null) {
			// call mouse motion on image layer
			displayPanel.getImageLayer().mouseMoved(e);
		}

		// EDIT ROI (moved)
		if (displayPanel.getToolboxPanel().getButtonEditRoi().isSelected()
				&& !allROIShapes.isEmpty()) {
			updateROIInformation(e);
		} else {
			updateROIInformation(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// System.out.println(e);
		if (this.displayPanel.getCurrentImage() == null)
			return;

		// if the mouse is dragging after an object click
		if (isROIDragging() == true) {

			// calculate the real pixel values of the mouse position
			Point p = CommonTools.getRealPixelPosition(e.getPoint(),
					this.displayPanel.getZoom());

			if (p.x < 0)
				p.x = 0; // negative point values
			if (p.y < 0)
				p.y = 0;

			LookToolboxPanel toolbox = this.displayPanel.getToolboxPanel();

			// LINE (dragging)
			if (toolbox.getButtonLine().isSelected()) {
				// get the start values from the display panel
				// these have been set by the MousePressed method of the
				// MouseListener instance
				LineROI tmpLineROI = new LineROI(new Line2D.Double(getRoiX0(),
						getRoiY0(), p.x, p.y));

				System.out.println(tmpLineROI.toString());

				setTmpROIShape(tmpLineROI);

				// IMPORTANT: set the instance to the display panel,
				// the mouse listeners queries this field
				setCurrentLineROI(tmpLineROI);

				// set the vector to the ROIPainter instance
				repaint();

				// calculate the line length in pixels
				fireNewProperties(currentLineROI);

				this.displayPanel.getImageLayer().mouseMoved(e);
			}

			// OVAL and RECTANGLE (dragging)
			else if (toolbox.getButtonRectangle().isSelected()
					|| toolbox.getButtonOval().isSelected()) {

				int x0 = getRoiX0();
				int y0 = getRoiY0();

				int dX = (p.x) - getRoiX0();
				int dY = (p.y) - getRoiY0();
				if (dX < 0)
					x0 = getRoiX0() + dX;
				if (dY < 0)
					y0 = getRoiY0() + dY;
				dX = (Math.abs(dX));
				dY = (Math.abs(dY));
				if ((float) (x0 + dX) > this.displayPanel.getImageWidth())
					dX = Math.round(this.displayPanel.getImageWidth() - x0) - 1;
				// out of image boundaries
				if ((float) (y0 + dY) > this.displayPanel.getImageHeight())
					dY = Math.round(this.displayPanel.getImageHeight() - y0) - 1;

				// determine the shape to be drawn according to the
				// selected button
				ROIShape tmpROI = null;
				if (toolbox.getButtonRectangle().isSelected()) {
					// logger.debug("Rectangle dragging...");
					tmpROI = new RectangleROI(new Rectangle2D.Double(x0, y0,
							dX, dY));

					setCurrentRectangleROI((RectangleROI) tmpROI);

					// calculate the area properties
					fireNewProperties(currentRectangleROI);

				} else if (toolbox.getButtonOval().isSelected()) {
					// logger.debug("Ellipse dragging...");
					tmpROI = new EllipseROI(
							new Ellipse2D.Double(x0, y0, dX, dY));
					setCurrentEllipseROI((EllipseROI) tmpROI);

					// calculate the area properties
					fireNewProperties(currentEllipseROI);
				}

				setTmpROIShape(tmpROI);

				// set the vector to the ROIPainter instance
				repaint();

				this.displayPanel.getImageLayer().mouseMoved(e);
			}

			// FREEHAND (dragging)
			else if (toolbox.getButtonFreehand().isSelected()) {
				// logger.debug("Freehand ROI mouse dragged...");

				// draw in the temporary poly line object of the look panel
				// using the previous node of the GeneralPath object
				// and connect to the current point under the cursor
				getFreehandShape().lineTo(p.x, p.y);

				FreehandROI tmpROI = new FreehandROI(getFreehandShape());

				// set the currently drawn freehand ROI to the display panel
				setCurrentFreehandROI(tmpROI);

				setTmpROIShape(tmpROI);

				// set the vector to the ROIPainter instance
				repaint();

				this.displayPanel.getImageLayer().mouseMoved(e);
			}

			// EDIT ROI (dragged)
			else if (toolbox.getButtonEditRoi().isSelected()
					&& !getAllROIShapes().isEmpty()) {

				// construct the affine transformation objects
				AffineTransform at = null;

				// EDIT LINE (dragged)
				if (clickedROIShape instanceof LineROI
						&& getBoundEditableRectanglesUntransformed().length == 2) {

					// first or second point in the line roi
					if (isInsidePtNorthWest() || isInsidePtNorth()) {
						// the click did not occur inside a ROI
						setInsideRoi(false);

						// PAINT NEW LINE ROI WITH OPPOSITE POINT OF THE CLICKED
						// BOUNDARY RECTANGLE AS STARTING POINT

						Point2D p1 = new Point2D.Double();
						Point2D p2 = new Point2D.Double();
						// in order to keep the orientation of the line, redraw
						// using mirrored coordinates
						// GeneralPath tmpPath = new GeneralPath();
						if (isInsidePtNorthWest()) {
							// tmpPath.moveTo(p.x, p.y);
							p1 = p;
							// tmpPath.lineTo((double) getRoiX0(),
							// (double) getRoiY0());
							p2 = new Point2D.Double(getRoiX0(), getRoiY0());
						}

						else if (isInsidePtNorth()) {
							p1 = new Point2D.Double(getRoiX0(), getRoiY0());
							// tmpPath.moveTo((double) getRoiX0(),
							// (double) getRoiY0());
							// tmpPath.lineTo(p.x, p.y);
							p2 = p;
						}

						LineROI alteredLineROI = new LineROI(new Line2D.Double(
								p1, p2));

						logger.debug("Clicked ROI Shape exists?: "
								+ getAllROIShapes().contains(
										getClickedROIShape()));

						if (getAllROIShapes().contains(clickedROIShape)) {
							// overwrite the current line ROI with the altered
							// one
							// 1. in all ROI shapes list
							allROIShapes.set(
									allROIShapes.indexOf(clickedROIShape),
									alteredLineROI);
						}

						if (getAllROIShapes().contains(clickedROIShape)) {
							// 2. in the all LineROI list
							lineROIs.set(lineROIs.indexOf(clickedROIShape),
									alteredLineROI);
						}

						// set the current line roi element
						setCurrentLineROI(alteredLineROI);

						// set the current roi shape element
						setCurrentROIShape(alteredLineROI);

						// set the clicked roi shape element
						setClickedROIShape(alteredLineROI);

						// set the roi painter values and repaint
						setCurrentROIShape(currentROIShape);
						repaint();

						// calculate and set the ROI length
						fireNewProperties(currentLineROI);

						// IMPORTANT this transform does nothing to the shape
						// but throws a null pointer, if not set!
						at = new AffineTransform(1.0d, 0.0d, 0.0d, 1.0d, 0.0d,
								0.0d);
					}

					// DRAG THE LINE ROI
					this.dragROI(at, p, e);
				}

				// EDIT POINT (dragged)
				else if (clickedROIShape instanceof PointROI) {
					this.dragROI(at, p, e);
				}

				else {
					if (isInsidePtNorthWest()) { // top left
						Rectangle bounds = clickedROIShape.getBounds();
						double fX = (double) (bounds.x - p.x + bounds.width)
								/ (double) (bounds.width);
						double fY = (double) (bounds.y - p.y + bounds.height)
								/ (double) (bounds.height);
						double deltaX = -(fX * bounds.x - p.x);
						double deltaY = -(fY * bounds.y - p.y);
						at = new AffineTransform(fX, 0d, 0d, fY, deltaX, deltaY);

						setInsideRoi(false);
					} else if (isInsidePtNorth()) { // top
													// middle
						Rectangle bounds = clickedROIShape.getBounds();
						double fX = 1d;
						double fY = (double) (bounds.y - p.y + bounds.height)
								/ (double) (bounds.height);
						double deltaX = 0d;
						double deltaY = -(fY * bounds.y - p.y);
						at = new AffineTransform(fX, 0d, 0d, fY, deltaX, deltaY);

						setInsideRoi(false);
					} else if (isInsidePtNorthEast()) { // top
														// right
						Rectangle bounds = clickedROIShape.getBounds();
						double fX = (double) (p.x - bounds.x)
								/ (double) (bounds.width);
						double fY = (double) (bounds.y - p.y + bounds.height)
								/ (double) (bounds.height);
						double deltaX = -(fX * bounds.x - bounds.x);
						double deltaY = -(fY * bounds.y - p.y);
						at = new AffineTransform(fX, 0d, 0d, fY, deltaX, deltaY);

						setInsideRoi(false);
					} else if (isInsidePtEast()) { // right
													// middle
						Rectangle bounds = clickedROIShape.getBounds();
						double fX = (double) (p.x - bounds.x)
								/ (double) (bounds.width);
						double fY = 1d;
						double deltaX = -(fX * bounds.x - bounds.x);
						double deltaY = 0d;
						at = new AffineTransform(fX, 0d, 0d, fY, deltaX, deltaY);

						setInsideRoi(false);
					} else if (isInsidePtSouthEast()) { // bottom
														// right
						Rectangle bounds = clickedROIShape.getBounds();
						double fX = (double) (p.x - bounds.x)
								/ (double) (bounds.width);
						double fY = (double) (p.y - bounds.y)
								/ (double) (bounds.height);
						double deltaX = -(fX * bounds.x - bounds.x);
						double deltaY = -(fY * bounds.y - bounds.y);
						at = new AffineTransform(fX, 0d, 0d, fY, deltaX, deltaY);

						setInsideRoi(false);
					} else if (isInsidePtSouth()) { // bottom
													// middle
						Rectangle bounds = clickedROIShape.getBounds();
						double fX = 1d;
						double fY = (double) (p.y - bounds.y)
								/ (double) (bounds.height);
						double deltaX = 0d;
						double deltaY = -(fY * bounds.y - bounds.y);
						at = new AffineTransform(fX, 0d, 0d, fY, deltaX, deltaY);

						setInsideRoi(false);
					} else if (isInsidePtSouthWest()) { // bottom
														// left
						Rectangle bounds = clickedROIShape.getBounds();
						double fX = (double) (bounds.x - p.x + bounds.width)
								/ (double) (bounds.width);
						double fY = (double) (p.y - bounds.y)
								/ (double) (bounds.height);
						double deltaX = -(fX * bounds.x - p.x);
						double deltaY = -(fY * bounds.y - bounds.y);
						at = new AffineTransform(fX, 0d, 0d, fY, deltaX, deltaY);

						setInsideRoi(false);
					} else if (isInsidePtWest()) { // middle
													// left
						Rectangle bounds = clickedROIShape.getBounds();
						double fX = (double) (bounds.x - p.x + bounds.width)
								/ (double) (bounds.width);
						double fY = 1d;
						double deltaX = -(fX * bounds.x - p.x);
						double deltaY = 0d;
						at = new AffineTransform(fX, 0d, 0d, fY, deltaX, deltaY);

						setInsideRoi(false);
					}

					// DRAG THE ROI
					this.dragROI(at, p, e);
				}
			}
		}

		// update the image info label
		this.displayPanel.getImageLayer().mouseMoved(e);
	}

	private void dragROI(AffineTransform at, Point p, MouseEvent e) {
		boolean isBeingShifted = false;

		// calculate the translation, if the click happened to be inside a ROI
		if (isInsideRoi()) {
			double deltaX = ((double) p.x - (double) getRoiShiftX0());
			double deltaY = ((double) p.y - (double) getRoiShiftY0());
			at = new AffineTransform(1.0d, 0.0d, 0.0d, 1.0d, deltaX, deltaY);
			isBeingShifted = true;
			logger.debug("Shifting: " + at);
		}

		// DRAG THE SELECTED ROI SHAPE
		// react to the mouse drag gestures
		if ((isInsidePtNorthWest()) || (isInsidePtNorth())
				|| (isInsidePtNorthEast()) || (isInsidePtEast())
				|| (isInsidePtSouthEast()) || (isInsidePtSouth())
				|| (isInsidePtSouthWest()) || (isInsidePtWest())
				|| (isInsideRoi())) {

			// Test if ROI is inside image, so that the ROI
			// is not painted outside of image borders
			ROIShape entireImage = new ROIShape(new Rectangle(0, 0,
					this.displayPanel.getImageWidth(),
					this.displayPanel.getImageHeight()));
			ROIShape testRoiShape = new ROIShape(clickedROIShape.transform(at)
					.getAsShape());

			logger.trace("Clicked ROI Shape: " + getClickedROIShape());
			logger.trace("Current ROI Shape: " + getCurrentROIShape());

			logger.trace("Entire image bounds: " + entireImage.getBounds());
			logger.trace("Test ROIShape bounds: " + testRoiShape.getBounds());

			if (!entireImage.contains(testRoiShape.getBounds())
			// this clause is ESSENTIAL for LineROI translation
					&& !entireImage.contains(p)) {
				logger.trace("ROIShape bounds does not lie within image bounds!");
				repaint();
				return;
			}
			// if the ROI shape is inside the image
			else if (entireImage.getBounds().contains(testRoiShape.getBounds())
			// this clause is ESSENTIAL for LineROI translation
					|| ((currentROIShape instanceof LineROI) ? entireImage
							.contains(p) : false)) {
				logger.debug("ROIShape bounds lies within image bounds.");

				// remove the current ROI shape from the ALL ROIs vector
				// otherwise every shape is added to the vector and drawn by
				// displayJAI class
				allROIShapes.remove(getCurrentROIShape());

				// determine the previously selected shape type and correct
				// the assignment to the ROI shape lists
				if (currentROIShape instanceof LineROI && isBeingShifted) {

					// remove the currently selected LINE ROI element
					lineROIs.remove(currentROIShape);

					// transform to a new LINE ROI shape
					LineROI newLineROI = new LineROI(clickedROIShape.transform(
							at).getAsShape());

					// set the current ROI shape
					setCurrentROIShape(newLineROI);

					// set the current LINE ROI
					setCurrentLineROI((LineROI) currentROIShape);

					// add this LINE ROI to all LINE ROIs
					lineROIs.add(currentLineROI);

					// display the line properties
					fireNewProperties(currentLineROI);

				}

				if (currentROIShape instanceof RectangleROI) {

					// remove the currently selected RECTANGLE ROI element
					rectangleROIs.remove(currentROIShape);

					// transform to a new RECTANGLE ROI shape
					RectangleROI newRectangleROI = new RectangleROI(
							clickedROIShape.transform(at).getAsShape());

					// set the current ROI shape
					setCurrentROIShape(newRectangleROI);

					// set the current RECTANGLE ROI
					setCurrentRectangleROI((RectangleROI) currentROIShape);

					// add this RECTANGLE ROI to all RECTANGLE ROIs
					rectangleROIs.add(currentRectangleROI);

					// calculate the area properties
					fireNewProperties(currentRectangleROI);

				} else if (currentROIShape instanceof EllipseROI) {

					// remove the currently selected ELLIPSE ROI element
					ellipseROIs.remove(currentROIShape);

					// transform to a new ELLIPSE ROI shape
					EllipseROI newEllipseROI = new EllipseROI(clickedROIShape
							.transform(at).getAsShape());

					// set the current ROI shape
					setCurrentROIShape(newEllipseROI);

					// set the current ELLIPSE ROI
					setCurrentEllipseROI((EllipseROI) currentROIShape);

					// add this ELLIPSE ROI to all ELLIPSE ROIs
					ellipseROIs.add(currentEllipseROI);

					// calculate the area properties
					fireNewProperties(currentEllipseROI);

				} else if (currentROIShape instanceof FreehandROI) {

					// remove the currently selected FREEHAND ROI element
					freehandROIs.remove(currentROIShape);

					// transform to a new FREEHAND ROI shape
					FreehandROI newFreehandROI = new FreehandROI(
							clickedROIShape.transform(at).getAsShape());

					// set the current ROI shape
					setCurrentROIShape(newFreehandROI);

					// set the current FREEHAND ROI
					setCurrentFreehandROI((FreehandROI) currentROIShape);

					// add this FREEHAND ROI to all FREEHAND ROIs
					freehandROIs.add(currentFreehandROI);

				} else if (currentROIShape instanceof PolygonROI) {

					// remove the currently selected POLYGON ROI element
					polygonROIs.remove(currentROIShape);

					// transform to a new POLYGON ROI shape
					PolygonROI newPolygonROI = new PolygonROI(clickedROIShape
							.transform(at).getAsShape());

					// set the current ROI shape
					setCurrentROIShape(newPolygonROI);

					// set the current POLYGON ROI
					setCurrentPolygonROI((PolygonROI) currentROIShape);

					// add this POLYGON ROI to all POLYGON ROIs
					polygonROIs.add(currentPolygonROI);

				} else if (currentROIShape instanceof AngleROI) {

					// remove the currently selected ANGLE ROI element
					angleROIs.remove(currentROIShape);

					// transform to a new ANGLE ROI shape
					AngleROI newAngleROI = new AngleROI(clickedROIShape
							.transform(at).getAsShape());

					// set the current ROI shape
					setCurrentROIShape(newAngleROI);

					// set the current ANGLE ROI
					setCurrentAngleROI((AngleROI) currentROIShape);

					// add this ANGLE ROI to all ANGLE ROIs
					angleROIs.add(currentAngleROI);

					// display the angle properties
					fireNewProperties(currentAngleROI);

				} else if (currentROIShape instanceof PointROI) {

					// remove the currently selected POINT ROI element
					pointROIs.remove(currentROIShape);

					// transform to a new POINT ROI shape
					Shape newShape = clickedROIShape.transform(at).getAsShape();
					Point newPoint = new Point(newShape.getBounds().x
							+ newShape.getBounds().width / 2,
							newShape.getBounds().y
									+ newShape.getBounds().height / 2);
					PointROI newPointROI = new PointROI(newPoint);

					// set the current ROI shape
					setCurrentROIShape(newPointROI);

					// set the current POINT ROI
					setCurrentPointROI((PointROI) currentROIShape);

					// add this POINT ROI to all POINT ROIs
					pointROIs.add(currentPointROI);
				}

				// add the new roi shape to the ALL ROI SHAPE list
				allROIShapes.add(currentROIShape);

				repaint();

				// fire additional mouse moved event to
				// show new data in the image info panel
				this.displayPanel.getImageLayer().mouseMoved(e);
			}
		}
	}

	/**
	 * Searches for a click inside of a ROI and activates the corresponding ROI,
	 * if any found.
	 * 
	 * @param p
	 * @param e
	 */
	private void searchForInsideROIClick(Point p, MouseEvent e) {
		// System.out.println("Scanning for Inside ROI click");
		// handle overlapping ROIs
		ListIterator<ROIShape> itr = allROIShapes.listIterator(allROIShapes
				.size());
		while (itr.hasPrevious()) {
			ROIShape shapeToTest = itr.previous();
			logger.debug("ShapeToTest.bounds: " + shapeToTest.getBounds()
					+ " contains point: " + p + "? ->"
					+ shapeToTest.contains(p.x, p.y));
			setInsideRoi(shapeToTest.contains(p.x, p.y));

			// if test does not result that point is inside a ROI
			// try to check clicking the line of a shape
			if (!isInsideRoi())
				setInsideRoi(shapeToTest.intersects(p.x - 1, p.y - 1, 2, 2)); // for
																				// lines
																				// true

			logger.debug("ShapeToTest.bounds: " + shapeToTest.getBounds()
					+ " intersects point: " + p + "? ->"
					+ shapeToTest.intersects(p.x - 1, p.y - 1, 2, 2));

			if (isInsideRoi()) {

				int index = allROIShapes.indexOf(shapeToTest);

				logger.debug("PRE: ROIShape from AllROIShapes ArrayList: "
						+ currentROIShape);

				setCurrentROIShape(allROIShapes.get(index));

				logger.debug("POST: ROIShape from AllROIShapes ArrayList: "
						+ currentROIShape);

				// determine the type of the selected ROI
				if (currentROIShape instanceof LineROI) {

					setCurrentLineROI((LineROI) currentROIShape);

					// display the line length of the roi
					fireNewProperties(currentLineROI);

				} else if (currentROIShape instanceof RectangleROI) {
					setCurrentRectangleROI((RectangleROI) currentROIShape);

					// calculate the area properties
					fireNewProperties(currentRectangleROI);

				} else if (currentROIShape instanceof EllipseROI) {
					setCurrentEllipseROI((EllipseROI) currentROIShape);

					// calculate the area properties
					fireNewProperties(currentEllipseROI);

				} else if (currentROIShape instanceof FreehandROI) {
					setCurrentFreehandROI((FreehandROI) currentROIShape);

				} else if (currentROIShape instanceof PolygonROI) {
					setCurrentPolygonROI((PolygonROI) currentROIShape);

				} else if (currentROIShape instanceof AngleROI) {
					setCurrentAngleROI((AngleROI) currentROIShape);

					// display the current angle ROI properties
					fireNewProperties(currentAngleROI);
				} else if (currentROIShape instanceof PointROI) {
					setCurrentPointROI((PointROI) currentROIShape);
				}

				logger.debug("ROIShape has been selected: " + currentROIShape);

				logger.debug("Click on ROI resulted [" + isInsideRoi()
						+ "] at index [" + index + "]: " + currentROIShape);
				break; // a valid ROI found
			}
		}

		// shift (translate) ROI if inside
		if (isInsideRoi()) {
			logger.debug("Click occured inside of ROI shape: "
					+ currentROIShape);
			// set the starting points for the translation
			setRoiShiftX0(p.x);
			setRoiShiftY0(p.y);

			logger.debug("Starting point for shifting a ROI: " + p);

			setClickedROIShape(currentROIShape);

			// tell the painter, that the current roi shape has just been
			// clicked
			update();

			// set the additional text
			if (clickedROIShape instanceof LineROI
					|| clickedROIShape instanceof AngleROI
					|| clickedROIShape instanceof AreaEnabledROI) {
				fireNewProperties(clickedROIShape);
			} else {
				this.displayPanel.setAdditionalText("");
			}
			// add other ROIs, if required

			// fire additinal mouse moved event to
			// show new data
			displayPanel.getImageLayer().mouseMoved(e);
		}
	}

	private void updateROIInformation(MouseEvent e) {
		LookToolboxPanel toolbox = this.displayPanel.getToolboxPanel();

		// ### ROI SPECIFIC INFORMATION
		// POLYGON (moved)
		if (toolbox.getButtonPolygon().isSelected() && currentPointROI != null) {

			// calculate the real pixel values of the mouse position
			Point p = CommonTools.getRealPixelPosition(e.getPoint(),
					this.displayPanel.getZoom());

			// get the last point of the polygon and draw a line to this
			// mouse event's point
			GeneralPath tmpPath = (GeneralPath) polygonShape.clone();

			tmpPath.lineTo(p.x, p.y);

			PolygonROI tmpROI = new PolygonROI(tmpPath);

			// set the currently drawn polygon ROI to the display panel
			setCurrentPolygonROI(tmpROI);

			setTmpROIShape(tmpROI);

			repaint();
		}

		// ANGLE (moved)
		else if (toolbox.getButtonAngle().isSelected()
				&& currentAngleROI != null && numAnglePoints >= 1) {

			// calculate the real pixel values of the mouse position
			Point p = CommonTools.getRealPixelPosition(e.getPoint(),
					this.displayPanel.getZoom());

			// get the last point of the polygon and draw a line to this
			// mouse event's point
			GeneralPath tmpPath = (GeneralPath) angleShape.clone();

			tmpPath.lineTo(p.x, p.y);

			AngleROI tmpROI = new AngleROI(tmpPath);

			// set the currently drawn angle ROI to the display panel
			setCurrentAngleROI(tmpROI);

			setTmpROIShape(tmpROI);

			fireNewProperties(currentAngleROI);

			repaint();
		}

		// RECTANGLE (moved)
		else if (toolbox.getButtonRectangle().isSelected()
				&& currentRectangleROI != null) {
			// calculate the area properties
			fireNewProperties(currentRectangleROI);
		}

		// ELLIPSE (moved)
		else if (toolbox.getButtonOval().isSelected()
				&& currentEllipseROI != null) {

			// calculate the area properties
			fireNewProperties(currentEllipseROI);
		}
	}

	public void setDisplayPanel(ILookPanel displayPanel) {
		this.displayPanel = displayPanel;
	}

	public ILookPanel getDisplayPanel() {
		return displayPanel;
	}

	@Override
	public UUID getID() {
		return this.id;
	}

	@Override
	public void setID(UUID id) {
		this.id = id;
	}

	@Override
	public boolean hasROIs() {
		return !this.allROIShapes.isEmpty();
	}
}
