package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: XMLPropertyManager.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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
 * This class manages properties stored in a specific XML file.
 * 
 * @author Philipp Kainz
 * 
 */
public class XMLPropertyManager extends PropertyManager{

	/**
	 * Standard constructor.
	 */
	public XMLPropertyManager() {
	}

	/**
	 * Writes properties to a file.
	 * 
	 * @param props
	 * @param file
	 */
	public static void writeXML(Properties props, File file) {
		try {
			OutputStream os = new FileOutputStream(file);
			props.storeToXML(os, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes properties to a file.
	 * 
	 * @param file
	 */
	public static Properties readXML(File file) {
		try {
			InputStream is = new FileInputStream(file);
			Properties props = new Properties();
			props.loadFromXML(is);
			return props;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws JAXBException {
		File file = new File(ConfigManager.getCurrentInstance().getDefaultIqmConfPath()
				.getPath() + File.separator + "foo.xml");
		Properties p = new Properties();
		p.setProperty("foo", "a value");
		XMLPropertyManager.writeXML(p, file);

		p = XMLPropertyManager.readXML(file);
		p.setProperty("bar", "another value");
		XMLPropertyManager.writeXML(p, file);
	}
}
