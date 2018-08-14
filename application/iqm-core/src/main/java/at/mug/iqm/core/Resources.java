package at.mug.iqm.core;

/*
 * #%L
 * Project: IQM - Application Core
 * File: Resources.java
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


import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * This class gets resources by identifier.
 * @author Philipp Kainz
 */
public class Resources {
	// class specific logger
	private static final Logger logger = Logger.getLogger(Resources.class);

	// class variable declaration
	private static final ClassLoader loader = Resources.class.getClassLoader();

	private static final ResourceBundle IMAGEPATHS_BUNDLE    = ResourceBundle.getBundle("core.protocol.imagePaths");


	/**
	 * Fetches the relative URL from a specified resource. 
	 * @param resName
	 * @exception Exception 
	 * @return null or the relative URL to the requested resource
	 */
	private static URL fetchURLfor(final String resName){
		try{
			logger.trace("Trying to fetch an URL from resource folder: '" + resName + "'");
			URL tmp = loader.getResource(resName);
			if (tmp != null){
				logger.trace("'" + tmp + "': success.");
				return tmp;
			} else{
				logger.error("'" + resName + "': failed. Could not find resource.");
				return null;
			}
		}catch(Exception e){
			logger.error("'" + resName + "': failed: " + e);
			return null;
		}
	}

	/**
	 * This method fetches a String Object from a given String <code>key</code> in a specific resource file 
	 * addressed by ResourceBundle <code>rb</code>. It returns '!key!' if the required value is not found
	 * for the key.
	 * @param key
	 * @param rb
	 * @return value or '!'+<code>key</code>+'!'.
	 */
	private static String getString(final String key, ResourceBundle rb) {
		try {
			 return rb.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * This method fetches a String Object from a given String <code>key</code> in a specific resource file 
	 * addressed by ResourceBundle <code>IMAGEPATHS_BUNDLE</code>. It returns '!key!' if the required value is not found
	 * for the key.
	 * @param key
	 * @return value, or !key!, if the value is not found
	 */
	public static URL getImageURL(final String key){
		return fetchURLfor(getString(key, IMAGEPATHS_BUNDLE));
	}

}


