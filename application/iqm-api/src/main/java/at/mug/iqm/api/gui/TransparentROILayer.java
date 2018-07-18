package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: TransparentROILayer.java
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * This class is able to render ROI elements on a transparent layer over the
 * displayed image.
 * 
 * @author Philipp Kainz
 * 
 */
public class TransparentROILayer extends DefaultDrawingLayer {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 1609765988235193209L;

	/**
	 * Create a new transparent ROI layer.
	 */
	public TransparentROILayer() {
		super();
	}

	public TransparentROILayer(ILookPanel displayPanel) {
		super(displayPanel);
	}
	
	public void initialize(){
		super.initialize();
	}

	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
	}

	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
	}

	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
	}

	public void mouseEntered(MouseEvent e) {
		// does nothing
	}

	public void mouseExited(MouseEvent e) {
		// does nothing
	}

	public void mouseClicked(MouseEvent e) {
		// does nothing
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		super.mouseWheelMoved(e);
	}
}
