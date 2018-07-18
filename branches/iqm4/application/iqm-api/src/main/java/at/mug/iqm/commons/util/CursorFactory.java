package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: CursorFactory.java
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


import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import at.mug.iqm.api.Resources;

/**
 * This class generates cursors for the GUI.
 * 
 * @author Philipp Kainz
 * 
 */
public final class CursorFactory {

	private static Toolkit toolkit = Toolkit.getDefaultToolkit();

	public static Cursor getPredefinedCursor(int type) {
		return Cursor.getPredefinedCursor(type);
	}

	public static Cursor getDefaultCursor() {
		return Cursor.getDefaultCursor();
	}

	public static Cursor getSystemCustomCursor(String name)
			throws HeadlessException, AWTException {
		return Cursor.getSystemCustomCursor(name);
	}

	public static Cursor getWaitCursor() {
		return getPredefinedCursor(Cursor.WAIT_CURSOR);
	}

	public static Cursor getZoomCursor() {
		return getDefaultCursor();
	}

	public static Cursor getLineCursor() {
		Cursor lineDrawCursor = getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		return lineDrawCursor;
	}

	public static Cursor getRectangleCursor() {
		Cursor rectangleCursor = getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		return rectangleCursor;
	}

	public static Cursor getOvalCursor() {
		Cursor ovalCursor = getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		return ovalCursor;
	}

	public static Cursor getAngleCursor() {
		Cursor angleCursor = getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		return angleCursor;
	}

	public static Cursor getPolygonCursor() {
		Cursor polyCursor = getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		return polyCursor;
	}

	public static Cursor getFreehandCursor() {
		Cursor freehandCursor = getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		return freehandCursor;
	}

	public static Cursor getPointCursor() {
		Cursor pointCursor = getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		return pointCursor;
	}

	public static Cursor getEditROICursor() {
		Cursor editROICursor = getDefaultCursor();
		return editROICursor;
	}

	public static Cursor getResizeCursor_NW() {
		return getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
	}

	public static Cursor getResizeCursor_N() {
		return getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
	}

	public static Cursor getResizeCursor_NE() {
		return getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
	}

	public static Cursor getResizeCursor_E() {
		return getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
	}

	public static Cursor getResizeCursor_SE() {
		return getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
	}

	public static Cursor getResizeCursor_S() {
		return getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
	}

	public static Cursor getResizeCursor_SW() {
		return getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
	}

	public static Cursor getResizeCursor_W() {
		return getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
	}

	public static Cursor getMoveCursor() {
		Image dragArrowCursorImage = toolkit.getImage(Resources
				.getImageURL("cursor.drag.arrow"));
		Point hotSpot = new Point();
		hotSpot.setLocation((int) (dragArrowCursorImage.getWidth(null) / 2),
				(int) (dragArrowCursorImage.getWidth(null) / 2));

		return toolkit.createCustomCursor(dragArrowCursorImage, hotSpot,
				"DragArrowCursor");
	}

	public static Cursor getHandCursor() {
		return getPredefinedCursor(Cursor.HAND_CURSOR);
	}

	public static Cursor getOpenHandCursor() {
		Image openHandCursorImage = toolkit.getImage(Resources
				.getImageURL("cursor.hand.open"));
		Point hotSpot = new Point();
		hotSpot.setLocation((int) (openHandCursorImage.getWidth(null) / 2),
				(int) (openHandCursorImage.getWidth(null) / 2));
		return toolkit.createCustomCursor(openHandCursorImage, hotSpot,
				"OpenHandCursor");
	}

	public static Cursor getDragHandCursor() {
		Image dragHandCursorImage = toolkit.getImage(Resources
				.getImageURL("cursor.hand.drag"));
		Point hotSpot = new Point();
		hotSpot.setLocation((int) (dragHandCursorImage.getWidth(null) / 2),
				(int) (dragHandCursorImage.getWidth(null) / 2));
		return toolkit.createCustomCursor(dragHandCursorImage, hotSpot,
				"DragHandCursor");
	}
	
	/**
	 * Initializes the custom cursors.
	 */
	public static void initializeCustomCursors(){
		getOpenHandCursor();
		getDragHandCursor();
		getMoveCursor();
	}
}
