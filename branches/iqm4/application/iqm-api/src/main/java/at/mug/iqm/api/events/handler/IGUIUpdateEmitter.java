package at.mug.iqm.api.events.handler;

/*
 * #%L
 * Project: IQM - API
 * File: IGUIUpdateEmitter.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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
import java.beans.PropertyChangeListener;

import at.mug.iqm.api.events.VirtualFlagChangedEvent;

/**
 * This interface declares methods for events occurring on GUI elements, whose
 * properties have changed and a notification of {@link PropertyChangeListener}s
 * is required.
 * <p>
 * Currently this interface declares a single method for firing a
 * {@link VirtualFlagChangedEvent} from a GUI.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IGUIUpdateEmitter {

	/**
	 * Add a {@link PropertyChangeListener} to the {@link IGUIUpdateEmitter}, in
	 * order to be notified on GUI updates.
	 * 
	 * @param listener
	 *            the {@link PropertyChangeListener} to be added
	 * @param propertyName
	 *            the name of the property the listener should listen to, or
	 *            <code>null</code>, if the listener shall receive all events.
	 */
	void addGUIUpdateListener(PropertyChangeListener listener,
			String propertyName);

	/**
	 * List all registered {@link PropertyChangeListener}s for a given property
	 * name.
	 * 
	 * @param propertyName
	 *            the name of the property declared in the corresponding
	 *            {@link PropertyChangeEvent}, or <code>null</code> if all
	 *            {@link PropertyChangeListener}s shall be retrieved.
	 * @return an array of {@link PropertyChangeListener}s
	 */
	PropertyChangeListener[] getGUIUpdateListeners(String propertyName);

	/**
	 * Fires a new {@link VirtualFlagChangedEvent} to the registered
	 * {@link PropertyChangeListener}s, containing the boolean value for the
	 * flag.
	 * 
	 * @param virtual
	 *            <code>true</code> if the GUI item indicates virtual
	 *            processing, <code>false</code> otherwise
	 */
	void fireVirtualFlagChanged(boolean virtual);

	/*
	 * More GUI update declarations go here.
	 */

}
