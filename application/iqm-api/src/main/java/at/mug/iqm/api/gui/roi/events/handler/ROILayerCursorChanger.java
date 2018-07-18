package at.mug.iqm.api.gui.roi.events.handler;

/*
 * #%L
 * Project: IQM - API
 * File: ROILayerCursorChanger.java
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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ListIterator;

import javax.media.jai.ROIShape;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.DefaultDrawingLayer;
import at.mug.iqm.api.gui.ILookPanel;
import at.mug.iqm.api.gui.LookToolboxPanel;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.CursorFactory;

/**
 * This event handler is responsible for highlighting ROI shapes on a layer, if
 * the user hovers the element. Furthermore, this class changes the cursor for
 * various actions.
 * 
 * @author Philipp Kainz
 * 
 */
public class ROILayerCursorChanger extends MouseMotionAdapter {

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(ROILayerCursorChanger.class);

	/**
	 * The reference to the {@link ILookPanel}.
	 */
	private ILookPanel displayPanel;

	/*
	 * Helper flags for better performance.
	 */
	private boolean isEditing;
	private boolean wasInAROI = false;

	/**
	 * Default constructor receiving the reference to the {@link ILookPanel}.
	 * 
	 * @param displayPanel
	 */
	public ROILayerCursorChanger(ILookPanel displayPanel) {
		logger.debug("Creating a new instance...");
		this.displayPanel = displayPanel;
	}

	/**
	 * @see MouseMotionListener#mouseMoved(MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent evt) {
		if (this.displayPanel.getCurrentImage() == null)
			return;

		Point p = evt.getPoint();

		DefaultDrawingLayer activeLayer = (DefaultDrawingLayer) this.displayPanel
				.getCurrentROILayer();

		if (!activeLayer.isLayerVisible())
			return;

		LookToolboxPanel toolbox = this.displayPanel.getToolboxPanel();

		try {
			if (toolbox.getButtonArrow().isSelected()) {
				if (activeLayer.getAllROIShapes().isEmpty()) {
					activeLayer.setCursor(CursorFactory.getDefaultCursor());
					return;
				}

				// highlight possible roi shapes on the layer
				if (searchForOtherROIs(p, activeLayer)) {
					activeLayer.update();
					wasInAROI = true;
				} else {
					if (wasInAROI) {
						activeLayer.update();
						wasInAROI = false;
					}
				}
			} else if (toolbox.getButtonZoom().isSelected()) {
				activeLayer.setCursor(CursorFactory.getZoomCursor());
				return;
			} else if (toolbox.getButtonLine().isSelected()) {
				activeLayer.setCursor(CursorFactory.getLineCursor());
				return;
			} else if (toolbox.getButtonRectangle().isSelected()
					|| toolbox.getButtonSingleRow().isSelected()
					|| toolbox.getButtonSingleColumn().isSelected()) {
				activeLayer.setCursor(CursorFactory.getRectangleCursor());
				return;
			} else if (toolbox.getButtonOval().isSelected()) {
				activeLayer.setCursor(CursorFactory.getOvalCursor());
				return;
			} else if (toolbox.getButtonAngle().isSelected()) {
				activeLayer.setCursor(CursorFactory.getAngleCursor());
				return;
			} else if (toolbox.getButtonPolygon().isSelected()) {
				activeLayer.setCursor(CursorFactory.getPolygonCursor());
				return;
			} else if (toolbox.getButtonFreehand().isSelected()) {
				activeLayer.setCursor(CursorFactory.getFreehandCursor());
				return;
			} else if (toolbox.getButtonDragCanvas().isSelected()) {
				activeLayer.setCursor(CursorFactory.getOpenHandCursor());
				return;
			} else if (toolbox.getButtonPoint().isSelected()) {
				activeLayer.setCursor(CursorFactory.getPointCursor());
				return;
			}
			// if the underlying pixels match the bounding ROI elements, change
			// the cursor
			else if (toolbox.getButtonEditRoi().isSelected()
					&& !activeLayer.getAllROIShapes().isEmpty()) {

				System.out.println("Searching for ROI to highlight in Layer "
						+ activeLayer.getName() + "...");

				isEditing = true;

				// change the first two cursors on a LineROI element boundaries
				if (activeLayer.getBoundEditableRectanglesUntransformed().length == 2) {
					if (activeLayer.getBoundEditableRectanglesUntransformed()[0]
							.getBounds().contains(p)) {
						activeLayer.setCursor(CursorFactory.getHandCursor());
						// System.out.println("StartPoint");
					} else if (activeLayer
							.getBoundEditableRectanglesUntransformed()[1]
							.getBounds().contains(p)) {
						activeLayer.setCursor(CursorFactory.getHandCursor());
						// System.out.println("EndPoint");
					} else {
						if (searchForOtherROIs(p, activeLayer)) {
							activeLayer.update();
							wasInAROI = true;
						} else {
							if (wasInAROI) {
								activeLayer.update();
								wasInAROI = false;
							}
						}
					}
				} else {
					if (activeLayer.getBoundEditableRectanglesUntransformed()[0]
							.getBounds().contains(p)) {
						activeLayer.setCursor(CursorFactory
								.getResizeCursor_NW());
						// System.out.println("NW");
					} else if (activeLayer
							.getBoundEditableRectanglesUntransformed()[1]
							.getBounds().contains(p)) {
						activeLayer
								.setCursor(CursorFactory.getResizeCursor_N());
						// System.out.println("N");
					} else if (activeLayer
							.getBoundEditableRectanglesUntransformed()[2]
							.getBounds().contains(p)) {
						activeLayer.setCursor(CursorFactory
								.getResizeCursor_NE());
						// System.out.println("NE");
					} else if (activeLayer
							.getBoundEditableRectanglesUntransformed()[3]
							.getBounds().contains(p)) {
						activeLayer
								.setCursor(CursorFactory.getResizeCursor_E());
						// System.out.println("E");
					} else if (activeLayer
							.getBoundEditableRectanglesUntransformed()[4]
							.getBounds().contains(p)) {
						activeLayer.setCursor(CursorFactory
								.getResizeCursor_SE());
						// System.out.println("SE");
					} else if (activeLayer
							.getBoundEditableRectanglesUntransformed()[5]
							.getBounds().contains(p)) {
						activeLayer
								.setCursor(CursorFactory.getResizeCursor_S());
						// System.out.println("S");
					} else if (activeLayer
							.getBoundEditableRectanglesUntransformed()[6]
							.getBounds().contains(p)) {
						activeLayer.setCursor(CursorFactory
								.getResizeCursor_SW());
						// System.out.println("SW");
					} else if (activeLayer
							.getBoundEditableRectanglesUntransformed()[7]
							.getBounds().contains(p)) {
						activeLayer
								.setCursor(CursorFactory.getResizeCursor_W());
						// System.out.println("W");
					} else {
						if (searchForOtherROIs(p, activeLayer)) {
							activeLayer.update();
							wasInAROI = true;
						} else {
							if (wasInAROI) {
								activeLayer.update();
								wasInAROI = false;
							}
						}
					}
				}
			} else {
				activeLayer.setCursor(CursorFactory.getDefaultCursor());
				// System.out.println("outer");
			}

			isEditing = false;

		} catch (NullPointerException ignored) {
		}
	}

	private boolean searchForOtherROIs(Point p, DefaultDrawingLayer activeLayer) {
		// System.out.println("Scanning overlapping Regions");
		// otherwise look up, if click happens inside of any ROI
		ListIterator<ROIShape> itr = activeLayer.getAllROIShapes()
				.listIterator(activeLayer.getAllROIShapes().size());
		Point pCorr = null;

		boolean roiFound = false;

		while (itr.hasPrevious()) {
			ROIShape shapeToTest = itr.previous();
			activeLayer.setInsideRoi(shapeToTest.contains(CommonTools
					.getRealPixelPosition(p, this.displayPanel.getZoom())));

			// if test does not result that point is inside a ROI
			// try to check clicking the line of a shape
			if (!activeLayer.isInsideRoi()) {

				// calculate the roi shape's current position
				pCorr = CommonTools.getRealPixelPosition(p,
						this.displayPanel.getZoom());

				// for lines true!
				boolean intersectsROI = shapeToTest.intersects(pCorr.x - 1,
						pCorr.y - 1, 2, 2);

				activeLayer.setInsideRoi(intersectsROI);
			}
			if (activeLayer.isInsideRoi()) {

				System.out.println("inner");
				if (isEditing) {
					if (activeLayer.getCurrentROIShape() != shapeToTest) {
						System.out
								.println("Found a ROI, going to highlight it.");
						activeLayer.setCursor(CursorFactory.getHandCursor());
						// draw temp bounds (white)
						activeLayer.setRoiShapeForHighlighting(shapeToTest);
					} else {
						activeLayer.setCursor(CursorFactory.getMoveCursor());
					}
				} else {
					System.out.println("Found a ROI, going to highlight it.");
					activeLayer.setCursor(CursorFactory.getHandCursor());
					// draw temp bounds (white)
					activeLayer.setRoiShapeForHighlighting(shapeToTest);
				}
				roiFound = true;
				break; // a valid ROI found
			} else {
				activeLayer.setCursor(CursorFactory.getDefaultCursor());
			}
		}
		return roiFound;
	}
}
