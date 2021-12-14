package at.mug.iqm.api.gui.roi.events.handler;

/*
 * #%L
 * Project: IQM - API
 * File: CanvasMover.java
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


import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JViewport;

 
 

import at.mug.iqm.api.gui.DefaultDrawingLayer;
import at.mug.iqm.api.gui.ILookPanel;
import at.mug.iqm.commons.util.CursorFactory;

/**
 * This event handler class handles events on the display panel. E.g. if the
 * space bar is pressed, etc.
 * 
 * @author Philipp Kainz
 * 
 */
public class CanvasMover implements MouseMotionListener,
		KeyListener, MouseListener {

	// class specific logger
	  

	/**
	 * The reference to the {@link ILookPanel}.
	 */
	private ILookPanel displayPanel;

	/**
	 * A flag, determining whether or not the space bar is currently pressed.
	 */
	private boolean isSpaceBarDown = false;

	/**
	 * The starting point for mouse dragging to start.
	 */
	private Point dragStart;

	/**
	 * Default constructor receiving the reference to the {@link ILookPanel}.
	 * 
	 * @param displayPanel
	 */
	public CanvasMover(ILookPanel displayPanel) {
		System.out.println("IQM:  Creating a new instance...");
		this.displayPanel = displayPanel;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (isSpaceBarDown)
				return;
			System.out.println("IQM:  Pressed space!");
			isSpaceBarDown = true;
			CursorFactory.initializeCustomCursors();
			displayPanel.getCurrentROILayer().removeAllDefaultListeners();
			displayPanel.getCurrentROILayer().setCursor(
					CursorFactory.getOpenHandCursor());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (!isSpaceBarDown)
				return;
			System.out.println("IQM:  Released space!");
			isSpaceBarDown = false;
			displayPanel.getCurrentROILayer().addAllDefaultListeners();
			displayPanel.getCurrentROILayer().setCursor(
					CursorFactory.getDefaultCursor());
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		try{
		// System.out.println("Dragging: " + e.getPoint());
		if (isSpaceBarDown) {
			JViewport vp = displayPanel.getScrollPane().getViewport();
			Point oldViewPosition = vp.getViewPosition();
			Dimension extendSize = vp.getExtentSize();
			Dimension viewSize = vp.getViewSize();

//			System.out.println("ExtendSize: " + extendSize);
//			Point locationOnScreen = vp.getLocationOnScreen();
//			System.out.println("LocationOnScreen: " + locationOnScreen);
//			System.out.println("OldViewPosition: " + oldViewPosition);
//			System.out.println("ViewSize: " + viewSize);

			Point currentPoint = e.getPoint();
			double dX = currentPoint.getX() - this.dragStart.getX();
			double dY = currentPoint.getY() - this.dragStart.getY();

			Point newViewPosition = new Point();
			double newX = oldViewPosition.getX() - dX;
			double newY = oldViewPosition.getY() - dY;

			// System.out.print("dX=" + dX + ", dY="+dY + " --> ");

			if (newX < 0.0d) {
				newX = 0.0d;
			}
			if (newY < 0.0d) {
				newY = 0.0d;
			}
			if (newX > (viewSize.getWidth() - extendSize.getWidth())) {
				newX = (viewSize.getWidth() - extendSize.getWidth());
			}
			if (newY > (viewSize.getHeight() - extendSize.getHeight())) {
				newY = (viewSize.getHeight() - extendSize.getHeight());
			}
			// System.out.println(newX + ", " + newY);

			newViewPosition.setLocation(newX, newY);
			vp.setViewPosition(newViewPosition);
			vp.repaint();
		}
		}catch (NullPointerException npe){
			System.out.println("IQM Error: "+ npe);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// do nothing
	}
	
	public void setSpaceBarDown(boolean isSpaceBarDown) {
		this.isSpaceBarDown = isSpaceBarDown;
	}

	public boolean isSpaceBarDown() {
		return isSpaceBarDown;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// do nothing
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!isSpaceBarDown)
			return;

		// set the start point for the drag
		dragStart = e.getPoint();

		// System.out.println("DragStart: " + dragStart);

		DefaultDrawingLayer activeLayer = (DefaultDrawingLayer) displayPanel
				.getCurrentROILayer();

		activeLayer.setCursor(CursorFactory.getDragHandCursor());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!isSpaceBarDown)
			return;

		DefaultDrawingLayer activeLayer = (DefaultDrawingLayer) displayPanel
				.getCurrentROILayer();

		activeLayer.setCursor(CursorFactory.getOpenHandCursor());

		// System.out.println("DragEnd: " + e.getPoint());
	}

}
