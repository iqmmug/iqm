package at.mug.iqm.api.events;

/*
 * #%L
 * Project: IQM - API
 * File: OperatorCancelledEvent.java
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
 * This event is fired by operators during their execution. The
 * <code>StatusPanel</code> implements a standard {@link PropertyChangeListener}
 * for this event and handles the drawing of the status bar.
 * <p>
 * This event uses "<code>operatorCancelled</code>" as property name and any
 * listener must implement custom actions. The "<code>newValue</code>" object
 * will always be true.
 * 
 * @author Philipp Kainz
 * 
 */
public class OperatorCancelledEvent extends PropertyChangeEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2525863297776754713L;

	public OperatorCancelledEvent(Object source) {
		super(source, "operatorCancelled", false, true);
	}

}
