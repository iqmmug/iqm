package at.mug.iqm.api.model;

/*
 * #%L
 * Project: IQM - API
 * File: VirtualDataBox.java
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


import java.io.File;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class represents a virtual data box stored in the temporary directory
 * 
 * @author phil
 * 
 */
public class VirtualDataBox extends IqmDataBox implements IVirtualizable {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -8754425667539753391L;

	/**
	 * The unique ID of this object.
	 */
	private transient UUID id = UUID.randomUUID();

	/**
	 * The variable for the location of the temporary file.
	 */
	private transient File location = null;

	/**
	 * Empty default constructor.
	 */
	public VirtualDataBox() {
	}

	/**
	 * Construct new {@link VirtualDataBox} with a new ID and set the location
	 * of the box to the specified file.
	 * 
	 * @param location
	 *            the location on the hard drive
	 */
	public VirtualDataBox(File location) {
		this.setLocation(location);
	}

	@Override
	public UUID getID() {
		return id;
	}

	/**
	 * @see at.mug.iqm.api.model.IVirtualizable#getLocation()
	 */
	@Override
	public File getLocation() {
		return location;
	}

	/**
	 * @see at.mug.iqm.api.model.IVirtualizable#setLocation(File)
	 */
	@Override
	public void setLocation(File location) {
		this.location = location;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public VirtualDataBox clone() throws CloneNotSupportedException {
		VirtualDataBox theClone = null;
		switch (getDataType()) {
		case IMAGE:
		case PLOT:
		case TABLE:
		case CUSTOM:
		case EMPTY:
			theClone = new VirtualDataBox(getLocation());
			break;

		default:
		case UNSUPPORTED:
			break;
		}
		
		theClone.setProperties((HashMap<String, Object>) getProperties().clone());

		return theClone;
	}

}
