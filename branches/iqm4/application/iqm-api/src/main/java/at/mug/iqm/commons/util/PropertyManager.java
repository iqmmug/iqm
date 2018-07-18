package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: PropertyManager.java
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import at.mug.iqm.config.ConfigManager;

/**
 * This class manages properties stored in a specific .properties file.
 * 
 * @author Philipp Kainz
 * 
 */
public class PropertyManager {

	private File propFile;
	private Properties props;

	/**
	 * Standard constructor.
	 */
	public PropertyManager() {
		this.props = new Properties();
	}

	/**
	 * Constructs a {@link PropertyManager} and stores the key-value pairs in
	 * the specified file.
	 * 
	 * @param f
	 */
	public PropertyManager(File f) {
		this();
		this.propFile = f;
	}

	/**
	 * Writes properties to a file.
	 * 
	 * @param props
	 * @param file
	 * @param comment
	 */
	public static synchronized void write(Properties props, File file,
			String comment) {
		try {
			OutputStream os = new FileOutputStream(file);
			props.store(os, comment);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads properties from a file.
	 * 
	 * @param file
	 * @param createIfNotExists
	 *            if <code>true</code>, the file will be created and the
	 *            properties associated with that file are returned, if
	 *            <code>false</code>, the method returns null
	 * 
	 * @return the {@link Properties} object
	 */
	public static synchronized Properties read(File file,
			boolean createIfNotExists) {
		try {
			Properties props = new Properties();
			if (!file.exists() && createIfNotExists) {
				write(props, file, null);
			}
			InputStream is = new FileInputStream(file);
			props.load(is);
			return props;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a property manager for a {@link Class} name of a specific object.
	 * This method instantiates a property file for this class and loads it to
	 * the manager.
	 * 
	 * @param o
	 * @return the {@link PropertyManager} for a given class
	 */
	public static PropertyManager getManager(Object o) {
		String fileName = o.getClass().getName() + ".properties";

		File file = new File(ConfigManager.getCurrentInstance()
				.getDefaultIqmConfPath().getPath()
				+ File.separator + fileName);

		// try to overwrite it with a custom path specified by the user
		try {
			file = new File(ConfigManager.getCurrentInstance().getConfPath()
					.getPath()
					+ File.separator + fileName);
		} catch (Exception e) {
			System.out.println("No config manager loaded.");
		}

		PropertyManager pm = new PropertyManager(file);

		// fill the manager with the properties
		pm.read();

		// return the reference
		return pm;
	}

	/**
	 * Sets the file of this manager.
	 * 
	 * @param propFile
	 */
	public void setPropFile(File propFile) {
		this.propFile = propFile;
	}

	/**
	 * Gets the file of this manager.
	 * 
	 * @return the property file
	 */
	public File getPropFile() {
		return propFile;
	}

	/**
	 * Sets the property and writes it to the file.
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		props.setProperty(key, value);
		write(props, propFile, null);
	}

	/**
	 * Get a specific property by key.
	 * 
	 * @param key
	 * @return a {@link String}, the value associated with the key, or
	 *         <code>null</code> if the property is not found
	 */
	public String getProperty(String key) {
		props = read();
		return props.getProperty(key);
	}

	/**
	 * Reads the current property file.
	 * 
	 * @return the properties as {@link Properties} object
	 */
	public Properties read() {
		props = read(propFile, true);
		return props;
	}

	/**
	 * Writes the current property file.
	 */
	public void write() {
		write(props, propFile, null);
	}

	public static void main(String[] args) throws JAXBException {
		File file = new File(ConfigManager.getCurrentInstance().getDefaultIqmConfPath()
				.getPath() + File.separator + "foo.properties");
		Properties p = new Properties();
		p.setProperty("foo", "a value");
		PropertyManager.write(p, file, null);

		p = PropertyManager.read(file, true);
		p.setProperty("bar", "another value");
		PropertyManager.write(p, file, "IQM foo");
	}
}
