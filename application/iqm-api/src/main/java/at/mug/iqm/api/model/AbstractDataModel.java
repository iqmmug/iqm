package at.mug.iqm.api.model;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractDataModel.java
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


import java.util.Hashtable;

/**
 * @author Philipp Kainz
 * 
 */
public abstract class AbstractDataModel implements IDataModel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -6245902389375270638L;

	/**
	 * The name of the {@link AbstractDataModel}.
	 */
	protected String modelName = "";

	/**
	 * The properties of the {@link AbstractDataModel} with an initial capacity
	 * of 5 items.
	 */
	protected Hashtable<String, Object> properties = new Hashtable<String, Object>(
			5);

	/**
	 * Default constructor. Sets the name of the model to the default value of
	 * "AbstractDataModel".
	 */
	public AbstractDataModel() {
		this.setModelName(this.modelName);
	}

	/**
	 * Get the name of the model. If no name is specified in the properties, the
	 * {@link Object#toString()} method is called and the String is set as the
	 * name of the model.
	 * 
	 * @see at.mug.iqm.api.model.IDataModel#getModelName()
	 */
	@Override
	public String getModelName() {
		String name = (String) this.getProperty("model_name");
		if (name == null) {
			name = this.toString();
		}
		return name;
	}

	/**
	 * Get the name of the model. If name is <code>null</code>, the
	 * {@link Object#toString()} method is called and the String is set as the
	 * name of the model.
	 * 
	 * @see at.mug.iqm.api.model.IDataModel#setModelName(java.lang.String)
	 */
	@Override
	public void setModelName(String name) {
		if (name == null) {
			name = this.toString();
		}
		this.modelName = name;
		this.setProperty("model_name", name);
	}

	/**
	 * @see at.mug.iqm.api.model.IDataModel#getProperties()
	 */
	@Override
	public Hashtable<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * @see at.mug.iqm.api.model.IDataModel#setProperties(Hashtable)
	 */
	@Override
	public void setProperties(Hashtable<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * @see at.mug.iqm.api.model.IDataModel#getProperty(String)
	 */
	@Override
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	/**
	 * @see at.mug.iqm.api.model.IDataModel#setProperty(String, Object)
	 */
	@Override
	public void setProperty(String key, Object value) {
		this.properties.put(key, value);
	}

	/**
	 * @see at.mug.iqm.api.model.IDataModel#copyProperties(java.util.Hashtable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Hashtable<String, Object> copyProperties(
			Hashtable<String, Object> target) {
		Hashtable<String, Object> targetProperties = new Hashtable<String, Object>(
				this.properties.size());

		targetProperties = (Hashtable<String, Object>) this.properties.clone();

		return targetProperties;
	}

	@Override
	public abstract AbstractDataModel clone()
			throws CloneNotSupportedException;

	public void setDataType(String type) {
		this.setProperty("data_type", (String) type);
	}

	public String getDataType() {
		return (String) this.properties.get("data_type");
	}
}
