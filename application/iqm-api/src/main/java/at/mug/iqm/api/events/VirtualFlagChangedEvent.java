package at.mug.iqm.api.events;

/*
 * #%L
 * Project: IQM - API
 * File: VirtualFlagChangedEvent.java
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


import java.beans.PropertyChangeEvent;

/**
 * This event is fired, if the application is switched to virtual processing
 * mode (using the hard disk as temporary storage) to RAM processing mode or
 * vice versa.
 * <p>
 * This event uses "<code>virtualFlagChanged</code>
 * " as property name and transports the flag in the "<code>newValue</code>"
 * object.
 * 
 * @author Philipp Kainz
 * 
 */
public class VirtualFlagChangedEvent extends PropertyChangeEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8960228573333236186L;

	public VirtualFlagChangedEvent(Object source, boolean virtual) {
		super(source, "virtualFlagChanged", null, virtual);
	}

}
