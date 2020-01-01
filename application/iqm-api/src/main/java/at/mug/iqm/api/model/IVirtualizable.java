package at.mug.iqm.api.model;

/*
 * #%L
 * Project: IQM - API
 * File: IVirtualizable.java
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


import java.io.File;
import java.io.Serializable;
import java.util.UUID;

/**
 * This interface has to be implemented by IQM objects which are temporarily
 * stored on the hard disk.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IVirtualizable extends Serializable {

	/**
	 * Set the location in the file system for the {@link IVirtualizable}
	 * object.
	 * 
	 * @param location
	 *            the location in the file system
	 */
	void setLocation(File location);

	/**
	 * Get the location of the {@link IVirtualizable} object in the file system.
	 * 
	 * @return the {@link File} object containing the location
	 */
	File getLocation();

	/**
	 * Returns the unique identifier (ID) of this {@link IqmDataBox} object.
	 * <p>
	 * This ID will be used to map the object to it's serialized state on the
	 * hard drive.
	 * 
	 * @return the id of the object
	 */
	UUID getID();

}
