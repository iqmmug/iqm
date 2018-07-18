package at.mug.iqm.api;

/*
 * #%L
 * Project: IQM - API
 * File: ClasspathUtils.java
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
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

/**
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ClasspathUtils {

	/**
	 * Class specific logger.
	 */
	private static Logger logger = Logger.getLogger(ClasspathUtils.class);

	/**
	 * Parameters for the URL class loader.
	 */
	private static final Class[] parameters = new Class[] { URL.class };

	/**
	 * Recursively searches for <code>jar</code> files in the given root
	 * directory and adds them to the classpath.
	 * 
	 * @param rootDirectory
	 * @throws IOException
	 */
	public static void addDirToClasspath(File rootDirectory) throws IOException {
		if (rootDirectory.exists()) {
			File[] files = rootDirectory.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					addDirToClasspath(file);
				}
				// if the parent file is not "plugins" and the folder contains a
				// plugin
				// create tree
				addURL(file.toURI().toURL());
			}
		} else {
			logger.warn("The directory \"" + rootDirectory
					+ "\" does not exist!");
		}
	}

	/**
	 * Adds an {@link URL} to the classpath.
	 * 
	 * @param url
	 *            URL
	 * @throws IOException
	 *             IOException
	 */
	public static void addURL(URL url) throws IOException {
		URLClassLoader sysLoader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		URL urls[] = sysLoader.getURLs();
		for (int i = 0; i < urls.length; i++) {
			if (urls[i].toString().equalsIgnoreCase(url.toString())) {
				logger.info("URL " + url + " is already in the CLASSPATH");
				return;
			}
		}
		Class sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysLoader, new Object[] { url });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException(
					"Error, could not add URL to system classloader");
		}
	}

	/**
	 * Extracts a relative path starting with the <code>rootDir</code> name from
	 * a given path.
	 * 
	 * @param path
	 *            a path to be searched in
	 * @param rootDir
	 *            a directory to use as new root in the resulting path
	 * @return a relative path, e.g. <code>/relative/path</code> from
	 *         <code>/Path/to/my/relative/path</code>, if <code>relative</code>
	 *         is given as <code>rootDir</code>
	 */
	public static String getRelativePathToDir(String path, String rootDir) {
		try {
			File f = new File(path);
			ArrayList<String> pathCompartments = new ArrayList<String>();
			// search upwards until .../plugins is the parent folder
			while (true) {
				if (f.getParent().endsWith(rootDir)) {
					break;
				} else {
					// remember the parent directory name
					String parentDir = f.getParent().substring(
							f.getParent().lastIndexOf(File.separator));
					// move one directory up
					f = f.getParentFile();
					pathCompartments.add(parentDir);
				}
			}
			Collections.reverse(pathCompartments);
			String relativeTargetPath = "";
			for (String s : pathCompartments) {
				relativeTargetPath += s;
			}
			relativeTargetPath = relativeTargetPath.replace("\\", "/");
			relativeTargetPath = relativeTargetPath.replace("%20", " ");
			logger.debug("Extracted relative path to [" + rootDir + "] from ["
					+ path + "]: [" + relativeTargetPath + "]");
			return relativeTargetPath;
		} catch (Exception ex) {
			logger.debug("Loading plugins into plugin root, application may be in development mode.");
			return "";
		}
	}
}
