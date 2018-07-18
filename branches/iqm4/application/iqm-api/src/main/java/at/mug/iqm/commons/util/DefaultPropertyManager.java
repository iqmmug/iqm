package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: DefaultPropertyManager.java
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
import java.util.Properties;

import at.mug.iqm.config.ConfigManager;

/**
 * This property manager maintains a global properties file for IQM.
 * 
 * @author Philipp Kainz
 * 
 */
public class DefaultPropertyManager extends PropertyManager {

	/**
	 * The path to the default property file.
	 */
	protected File defaultPropFile = null;
	/**
	 * The properties object.
	 */
	protected Properties defaultProperties = null;

	/**
	 * Standard constructor.
	 */
	public DefaultPropertyManager() {
		this.defaultPropFile = new File(ConfigManager.getCurrentInstance()
				.getDefaultIqmConfPath().getPath()
				+ File.separator + "default.properties");

		// create the file, if it doesn't exist
		if (!this.defaultPropFile.exists()) {
			defaultProperties = new Properties();
			write(defaultProperties, defaultPropFile,
					"DEFAULT IQM PROPERTIES FILE");
		} else {
			// otherwise read the properties
			defaultProperties = read(defaultPropFile, false);
		}
	}

	/**
	 * Add or update a property with the given key/value pair. Existing
	 * properties are overwritten.
	 * 
	 * @param key
	 *            the identifier
	 * @param value
	 *            the value
	 */
	public synchronized void addOrUpdate(String key, String value) {
		defaultProperties.setProperty(key, value);
		write(defaultProperties, defaultPropFile, null);
	}

	/**
	 * Delete a property with a given key.
	 * 
	 * @param key
	 *            the property name to be deleted
	 */
	public synchronized void delete(String key) {
		defaultProperties.remove(key);
		write(defaultProperties, defaultPropFile, null);
	}

	/**
	 * Gets a property according to a given key.
	 * 
	 * @param key
	 * @return the value associated with the key, or <code>null</code>, if the
	 *         property does not exist.
	 */
	public String get(String key) {
		defaultProperties = read(defaultPropFile, true);
		return defaultProperties.getProperty(key);
	}

	/**
	 * Checks, whether or not a given property already exists
	 * 
	 * @param key
	 *            an identifier for the property
	 * @return <code>true</code>, if it exists, <code>false</code> otherwise
	 */
	public boolean exists(String key) {
		return defaultProperties.containsKey(key);
	}

	public static void main(String[] args) {
		new DefaultPropertyManager();
	}
}
