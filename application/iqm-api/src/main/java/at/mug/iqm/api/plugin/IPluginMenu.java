package at.mug.iqm.api.plugin;

/*
 * #%L
 * Project: IQM - API
 * File: IPluginMenu.java
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


import java.awt.event.ActionListener;

import at.mug.iqm.api.gui.IDeactivatableMenu;

/**
 * This interface declares methods for the menu of the plugins.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IPluginMenu extends ActionListener, IDeactivatableMenu {

	/**
	 * Adds a menu item for a plugin to the root of each menu ("/").
	 * 
	 * @param plugin
	 *            the plugin
	 */
	void addMenuItem(IPlugin plugin);

	/**
	 * Adds a menu item for a plugin at a given relative path, or
	 * "mounting point" within the menu.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param relativePath
	 *            the relative path in the menu
	 */
	void addMenuItem(IPlugin plugin, String relativePath);

	/**
	 * Updates the menu structure.
	 */
	void update();

	/**
	 * Sorts the menu items in all menus in an alphabetically ascending fashion.
	 */
	void sortAndAdd();
}
