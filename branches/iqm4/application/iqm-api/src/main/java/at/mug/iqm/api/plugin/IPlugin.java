package at.mug.iqm.api.plugin;

/*
 * #%L
 * Project: IQM - API
 * File: IPlugin.java
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


import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Properties;

import javax.swing.ImageIcon;

import at.mug.iqm.api.operator.IOperator;

/**
 * This is the method declaration for a plugin in IQM.
 * <p>
 * Each class implementing this interface must be accompanied with an XML-file
 * named <code>iqm-plugin.xml</code> containing an XML representation of a
 * {@link Properties} instance. This file is read at the {@link #init()} call
 * and split into atomic strings.
 * 
 * 
 * @author Philipp Kainz
 * 
 */
public interface IPlugin extends Serializable {

	/**
	 * Initialize this plugin. On this method, the plugin has to call at least
	 * the {@link IPluginRegistry#register(IPlugin, String)} using a self-reference as
	 * parameter.
	 * <p>
	 * If the plugin furthermore contains an {@link IOperator}, the second call
	 * will the registration of the operator descriptor.
	 */
	IPlugin init() throws Exception;

	/**
	 * The unique identifier of the plugin. This is usually the fully-qualified
	 * path name to the implementing plugin class.
	 * 
	 * @return the UID of the plugin
	 */
	String getUID();

	/**
	 * The name of the plugin, which will be used for menu entry creation.
	 * 
	 * @return the name of the plugin (menu entry)
	 */
	String getName();

	/**
	 * This is a wrapper method and returns the UID of the plugin.
	 * <p>
	 * The UID will furthermore be used for adding {@link ActionListener}
	 * interfaces to the plugin.
	 * 
	 * @return the action command, usually the UID of the plugin
	 */
	String getActionCommand();

	/**
	 * Get the version of the plugin.
	 * 
	 * @return the version of the plugin
	 */
	String getVersion();

	/**
	 * Get the type of the plugin. This information is used for menu hierarchy
	 * construction.
	 * 
	 * @return the type of the plugin
	 */
	String getType();

	/**
	 * Get the whole <code>iqm-plugin.xml</code> file as {@link Properties}.
	 * 
	 * @return the plugin properties
	 */
	Properties getProperties();

	/**
	 * Gets the menu icon for the enabled menu item of this plugin.
	 * <p>
	 * The class loader searches for a file named
	 * <code>menu-item-enabled.png</code> in the resource sub package
	 * <code>icons</code> of the <code>IPlugin</code> instance. For instance, if
	 * the implementing class of {@link IPlugin} is located in the package
	 * <code>my.plugins</code>, the loader looks up
	 * <code>resources/my/plugins/icons/menu-item-enabled.png</code> and
	 * constructs a new {@link ImageIcon}.
	 * <p>
	 * If <code>menu-item-enabled.png</code> is not contained in this package,
	 * the default icon from the public API is used.
	 * <p>
	 * One may specify a custom file name to be searched for in the mentioned 
	 * resource directory. If these files are not found, the default icon is loaded.
	 * 
	 * @return the menu icon for enabled menu items, if present, or the default
	 *         enabled plugin menu item icon
	 */
	ImageIcon getEnabledMenuIcon();

	/**
	 * Gets the menu icon for the disabled menu item of this plugin.
	 * <p>
	 * The class loader searches for a file named
	 * <code>menu-item-disabled.png</code> in the resource sub package
	 * <code>icons</code> of the <code>IPlugin</code> instance. For instance, if
	 * the implementing class of {@link IPlugin} is located in the package
	 * <code>my.plugins</code>, the loader looks up
	 * <code>resources/my/plugins/icons/menu-item-disabled.png</code> and
	 * constructs a new {@link ImageIcon}.
	 * <p>
	 * If <code>menu-item-disabled.png</code> is not contained in this package,
	 * the default icon from the public API is used.
	 * <p>
	 * One may specify a custom file name to be searched for in the mentioned 
	 * resource directory. If these files are not found, the default icon is loaded.
	 * 
	 * @return the menu icon for disabled menu items, if present, or the default
	 *         disabled plugin menu item icon
	 */
	ImageIcon getDisabledMenuIcon();

}
