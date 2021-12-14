package at.mug.iqm.core.plugin;

/*
 * #%L
 * Project: IQM - Application Core
 * File: PluginServiceFactory.java
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


import java.io.File;
import java.io.IOException;

 
 

import at.mug.iqm.api.ClasspathUtils;
import at.mug.iqm.api.plugin.IPluginService;

/**
 * This class produces {@link IPluginService}s.
 * <p>
 * <b>Currently only the <code>DefaultPluginService</code> can be created.</b>
 * 
 * @author Philipp Kainz
 * @since 3.0.1
 * 
 */
public class PluginServiceFactory {

	  

	/**
	 * Creates a new {@link DefaultPluginService}.
	 * 
	 * @return a new {@link DefaultPluginService}
	 */
	public static IPluginService createDefaultPluginService() {
		return DefaultPluginService.getInstance();
	}
	
	/**
	 * Adds the plugin jars to the class path.
	 */
	public static void addPluginJarsToClasspath() {
		try {
			// get the directory of the iqm-app.jar file
			File pluginDir = new File(System.getProperty("iqmrootdir")
					+ File.separator + "plugins");
			System.out.println("IQM Info: Trying to locate plugins in: [" + pluginDir + "]...");

			// search recursively for the plugins in the "plugins" directory
			ClasspathUtils.addDirToClasspath(pluginDir);
		} catch (IOException ex) {
			System.out.println("IQM Error: " + ex);
		}
	}
}
