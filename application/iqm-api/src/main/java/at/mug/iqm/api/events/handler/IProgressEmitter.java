package at.mug.iqm.api.events.handler;

/*
 * #%L
 * Project: IQM - API
 * File: IProgressEmitter.java
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


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * An interface for classes that are emitting {@link PropertyChangeEvent}s.
 * <p>
 * Currently this interface declares a single method for firing progress of a
 * running operator.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IProgressEmitter {

	/**
	 * Adds the {@link IProgressListener} for the given property name.
	 * 
	 * @param listener
	 *            the {@link IProgressListener} to be added
	 * @param propertyName
	 *            the name of the property declared in the corresponding
	 *            {@link PropertyChangeEvent}, or <code>null</code> if the
	 *            {@link IProgressListener} shall receive all events.
	 */
	void addProgressListener(PropertyChangeListener listener, String propertyName);

	/**
	 * List all registered {@link IProgressListener}s for a given property
	 * name.
	 * 
	 * @param propertyName
	 *            the name of the property declared in the corresponding
	 *            {@link PropertyChangeEvent}, or <code>null</code> if all
	 *            {@link IProgressListener}s shall be retrieved.
	 * @return an array of {@link IProgressListener}s
	 */
	PropertyChangeListener[] getProgressListeners(String propertyName);

	/**
	 * Fire the progress of the operator to any {@link IProgressListener}.
	 * 
	 * @param progress
	 */
	void fireProgressChanged(int progress);
	
	/*
	 * More progress emitting method declarations go here.
	 */
}
