package at.mug.iqm.core.plugin;

/*
 * #%L
 * Project: IQM - Application Core
 * File: DefaultPluginService.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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


import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.ClasspathUtils;
import at.mug.iqm.api.plugin.IPlugin;
import at.mug.iqm.api.plugin.IPluginRegistry;
import at.mug.iqm.api.plugin.IPluginService;

/**
 * This class is responsible for loading all classes from the class path that
 * are implementing the {@link IPlugin} interface.
 * 
 * @author Philipp Kainz
 * 
 */
public class DefaultPluginService implements IPluginService {

	/**
	 * Custom class logger.
	 */
	private static final Logger logger = LogManager.getLogger(DefaultPluginService.class);

	/**
	 * The singleton reference.
	 */
	private static DefaultPluginService pluginService;

	/**
	 * The service loader for the {@link IPlugin}s.
	 */
	private ServiceLoader<IPlugin> serviceLoader;

	/**
	 * Private constructor.
	 */
	private DefaultPluginService() {
		// get all classes from the classpath
		// that implement the given interface
		serviceLoader = ServiceLoader.load(IPlugin.class);
	}

	/**
	 * Gets a <b>singleton</b> instance of {@link DefaultPluginService}.
	 * 
	 * @return the reference to the singleton
	 */
	public static DefaultPluginService getInstance() {
		if (pluginService == null) {
			pluginService = new DefaultPluginService();
		}
		return pluginService;
	}

	@Override
	public Iterator<IPlugin> getPlugins() {
		return serviceLoader.iterator();
	}

	/**
	 * Initialize the plugin.
	 * <p>
	 * On calling <code>IPlugin.init()</code> the {@link IPlugin} registers its
	 * operator with the default {@link IPluginRegistry};
	 * 
	 * @throws Exception
	 */
	@Override
	public void initPlugins() throws Exception {
		Iterator<IPlugin> iterator = getPlugins();
		if (!iterator.hasNext()) {
			System.out.println("No plugins were found!");
		} else {
			while (iterator.hasNext()) {
				IPlugin plugin = iterator.next();
				Application.getApplicationStarter().updateDynamicWelcomeText(
						"Registering plugin [" + plugin.getName()
								+ ", " + plugin.getUID() + "]");
				// DEBUG OPTIONS
				// Thread.sleep(3000L);
				// in the init() method,
				// read the metadata from the iqm-plugin.xml and register the
				// plugin and the containing operator with the corresponding
				// registries
				try {
					plugin.init();
					String path = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
					String menuStructure = ClasspathUtils.getRelativePathToDir(path, "plugins");
					if (menuStructure.equals("")){
						Application.getPluginMenu().addMenuItem(plugin);
					}else{
						Application.getPluginMenu().addMenuItem(plugin, menuStructure);
					}
				} catch (Exception e) {
					logger.error(
							"Cannot load plugin " + plugin.getName() + ".", e);
				}
			}
		}
	}
}
