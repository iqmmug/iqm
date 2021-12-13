package at.mug.iqm.core.registry;

/*
 * #%L
 * Project: IQM - Application Core
 * File: PluginRegistry.java
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


import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.IOperator;
import at.mug.iqm.api.plugin.IPlugin;
import at.mug.iqm.api.plugin.IPluginRegistry;

/**
 * The <code>PluginRegistry</code> maintains a
 * <code>static HashMap&lt;String, IPlugin&gt;</code>, where all operators are
 * put when {@link IPlugin#init()} is called.
 * <p>
 * The <code>PluginRegistry</code> can be queried for all currently loaded
 * plugins and contains the unique names of the associated {@link IOperator}.
 * <p>
 * The <code>PluginRegistry</code> is designed as <b>singleton</b>.
 * 
 * @author Philipp Kainz
 * 
 */
public class PluginRegistry implements IPluginRegistry {
	
	/**
	 * Class specific logger.
	 */
	private static final Logger logger = LogManager.getLogger(PluginRegistry.class);

	/**
	 * The {@link HashMap} containing the {@link IPlugin}s registered at
	 * runtime.
	 */
	private static HashMap<String, IPlugin> pluginRegistry = new HashMap<String, IPlugin>(
			10);
	/**
	 * A {@link HashMap} containing the containing operator name in the plugin.
	 */
	private static HashMap<String, String> associatedOperators = new HashMap<String, String>(
			10);

	/**
	 * Private constructor, since {@link PluginRegistry} is a singleton.
	 */
	private PluginRegistry() {
		Application.setPluginRegistry(this);
	}

	/**
	 * Returns a reference to the singleton.
	 * 
	 * @return the singleton instance of the {@link PluginRegistry}
	 */
	public static IPluginRegistry getInstance() {
		IPluginRegistry pReg = Application.getPluginRegistry();
		if (pReg == null) {
			new PluginRegistry();
		}
		return pReg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pluggableapp.IPluginRegistry#register(com.pluggableapp.plugins.api
	 * .IPlugin)
	 */
	@Override
	public void register(IPlugin plugin, String opName) {
		String pluginID = plugin.getUID();
		// check if operator is already registered
		if (!pluginRegistry.containsKey(pluginID)) {
			pluginRegistry.put(pluginID, plugin);
			logger.info("Registered plugin [" + pluginID + "]");
		}

		// register the op name with this plugin
		if (!associatedOperators.containsKey(pluginID)) {
			associatedOperators.put(pluginID, opName);
			logger.info("Associated operator [" + opName
					+ "] with plugin [" + pluginID + "]");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pluggableapp.IPluginRegistry#unregister(com.pluggableapp.plugins.
	 * api.IPlugin)
	 */
	@Override
	public void unregister(IPlugin plugin) {
		String pluginID = plugin.getUID();
		// check if operator is registered
		if (pluginRegistry.containsKey(pluginID)) {
			pluginRegistry.remove(pluginID);
			logger.info("Unregistered plugin [" + pluginID + "]");
		}

		if (associatedOperators.containsKey(pluginID)) {
			String opName = associatedOperators.remove(pluginID);
			logger.info("Unregistered operator [" + opName
					+ "] with plugin [" + pluginID + "]");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pluggableapp.IPluginRegistry#getPlugin(java.lang.String)
	 */
	@Override
	public IPlugin getPlugin(String pluginName) throws NoSuchElementException {
		IPlugin plugin = pluginRegistry.get(pluginName);
		if (plugin == null) {
			throw new NoSuchElementException();
		}
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pluggableapp.IPluginRegistry#getPlugins()
	 */
	@Override
	public HashMap<String, IPlugin> getPlugins() {
		return pluginRegistry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pluggableapp.IPluginRegistry#findOperatorName(java.lang.String)
	 */
	@Override
	public String findOperatorName(String pluginName)
			throws NoSuchElementException {
		String operatorName = associatedOperators.get(pluginName);
		if (operatorName == null) {
			throw new NoSuchElementException();
		}
		return operatorName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pluggableapp.IPluginRegistry#printAllRegisteredPlugins()
	 */
	public void printAllRegisteredPlugins() {
		Iterator<String> iter = pluginRegistry.keySet().iterator();

		while (iter.hasNext()) {
			String key = iter.next().toString();
			IPlugin value = pluginRegistry.get(key);

			logger.info("[" + key + "]=" + value);
		}
	}

}
