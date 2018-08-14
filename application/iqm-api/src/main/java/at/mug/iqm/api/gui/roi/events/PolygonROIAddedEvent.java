package at.mug.iqm.api.gui.roi.events;

/*
 * #%L
 * Project: IQM - API
 * File: PolygonROIAddedEvent.java
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


import java.beans.PropertyChangeEvent;
import java.util.List;

import at.mug.iqm.api.gui.roi.PolygonROI;

/**
 * @author Philipp Kainz
 *
 */
public class PolygonROIAddedEvent extends PropertyChangeEvent {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -2181635451870826135L;

	/**
	 * A new instance is generated, passing a reference to the source 
	 * object.
	 * @param source - the source
	 * @param polygonROIs - the {@link List} of {@link PolygonROI}s
	 */
	public PolygonROIAddedEvent(Object source, List<PolygonROI> polygonROIs) {
		super(source, "polygonROIAdded", null, polygonROIs);
	}
}
