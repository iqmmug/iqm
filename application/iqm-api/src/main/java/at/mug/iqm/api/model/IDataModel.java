package at.mug.iqm.api.model;

/*
 * #%L
 * Project: IQM - API
 * File: IDataModel.java
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


import java.io.Serializable;
import java.util.Hashtable;

/**
 * 
 * @author Philipp Kainz
 * 
 */
public interface IDataModel extends Serializable, Cloneable {

	/**
	 * Gets the internal name of the data model. The model name is stored as
	 * <code>model_name</code> in the properties hashtable of the data model.
	 * <p>
	 * This property is read out for the display in the MainFrame's title.
	 * </p>
	 * 
	 * @return the name as {@link String}
	 */
	String getModelName();

	/**
	 * Sets the internal name of the data model. The model name is stored as
	 * <code>model_name</code> in the properties hashtable of the data model.
	 * <p>
	 * This property is read out for the display in the MainFrame's title.
	 * </p>
	 * 
	 * @param name
	 */
	void setModelName(String name);

	/**
	 * Gets the properties {@link Hashtable} of this data model.
	 * 
	 * @return the properties object
	 */
	Hashtable<String, Object> getProperties();

	/**
	 * Sets the properties {@link Hashtable} of this data model
	 * 
	 * @param properties
	 */
	void setProperties(Hashtable<String, Object> properties);

	/**
	 * Get the property specified by <code>key</code>.
	 * 
	 * @param key
	 * @return the value
	 */
	Object getProperty(String key);

	/**
	 * Set a specific property by <code>key</code> and <code>value</code>.
	 * 
	 * @param key
	 * @param value
	 */
	void setProperty(String key, Object value);

	/**
	 * Copies the properties {@link Hashtable} of this data model to a given
	 * instance.
	 * <p>
	 * If <code>null</code> is passed, a new instance will be generated and its
	 * reference returned.
	 * 
	 * @param target
	 *            the target {@link Hashtable}, may be <code>null</code>
	 * 
	 * @return a new instance of {@link Hashtable} containing the object's
	 *         properties
	 */
	Hashtable<String, Object> copyProperties(Hashtable<String, Object> target);
}
