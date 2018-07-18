package at.mug.iqm.api.plugin;

/*
 * #%L
 * Project: IQM - API
 * File: IPluginRegistry.java
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


import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * The plugin registry is responsible for registering 
 * a plugin as IQM launches. 
 * 
 * @author Philipp Kainz
 *
 */
public interface IPluginRegistry {

	/**
	 * Registers an {@link IPlugin} in the IQM registry with its name (String).
	 * @param plugin
	 */
	void register(IPlugin plugin, String operatorName);

	/**
	 * Removes an {@link IPlugin} from the IQM registry.
	 * @param plugin
	 */
	void unregister(IPlugin plugin);

	/**
	 * Gets the {@link IPlugin} instance from the registry.
	 * @param pluginName the unique name of a plugin
	 * @return the instance of {@link IPlugin}
	 * @throws NoSuchElementException if the name is not registered
	 */
	IPlugin getPlugin(String pluginName) throws NoSuchElementException;

	/**
	 * Returns all registered plugins.
	 * @return a <code>HashMap</code> containing all plugins 
	 */
	HashMap<String, IPlugin> getPlugins();

	/**
	 * Gets the name of the operator associated with the plugin.
	 * @param pluginName the <code>uid</code> of a plugin
	 * @return the unique name of the operator
	 * @throws NoSuchElementException if the plugin is not registered
	 */
	String findOperatorName(String pluginName) throws NoSuchElementException;

}
