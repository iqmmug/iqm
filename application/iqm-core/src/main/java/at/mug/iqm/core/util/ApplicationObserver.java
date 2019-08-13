package at.mug.iqm.core.util;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ApplicationObserver.java
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

import java.awt.Component;
import java.io.File;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.IDeactivatableMenu;
import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.gui.menu.AbstractMenuBar;
import at.mug.iqm.gui.menu.RecentFilesMenu;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class observes the application's state such as activating and
 * deactivating menu entries.
 * 
 * @author Philipp Kainz
 * 
 */
public final class ApplicationObserver {

	private static final Logger logger = Logger
			.getLogger(ApplicationObserver.class);

	/**
	 * Enables all menu items where {@link OperatorType#IMAGE} is assigned to.
	 */
	protected static void enableImageMenus() {
		AbstractMenuBar menuBar = (AbstractMenuBar) GUITools.getMainFrame()
				.getCoreMenuBar();
		int nItems = menuBar.getComponentCount();

		for (int i = 0; i < nItems; i++) {
			if (menuBar.getMenu(i) instanceof JMenu) {
				enableMenu(menuBar.getMenu(i), OperatorType.IMAGE);
			}
		}
	}
	
	/**
	 * Enables all menu items where {@link OperatorType#TABLE} is assigned to.
	 */
	protected static void enableTableMenus() {
		AbstractMenuBar menuBar = (AbstractMenuBar) GUITools.getMainFrame()
				.getCoreMenuBar();
		int nItems = menuBar.getComponentCount();

		for (int i = 0; i < nItems; i++) {
			if (menuBar.getMenu(i) instanceof JMenu) {
				enableMenu(menuBar.getMenu(i), OperatorType.TABLE);
			}
		}
	}

	/**
	 * Recursively searches for {@link IDeactivatableMenu} instances within a
	 * given menu and activates instances of {@link OperatorMenuItem} according
	 * to the operator type.
	 * 
	 * @param menu
	 *            the menu
	 * @param type
	 *            the {@link OperatorType}
	 */
	private static void enableMenu(JMenu menu, OperatorType type) {
		try {
			// activate all items on the top menu
			if (menu instanceof IDeactivatableMenu) {
				((IDeactivatableMenu) menu).activate(type);
			}
			// get the submenus
			Component[] menuItems = menu.getMenuComponents();
			for (Component c : menuItems) {
				if (c instanceof JMenu)
					enableMenu((JMenu) c, type);
			}
		} catch (Exception e) {
			logger.error("An error occurred when activating the " + type
					+ " menu items: " + e.getMessage());
		}
	}

	/**
	 * Recursively searches for {@link IDeactivatableMenu} instances within a
	 * given menu and deactivates instances of {@link OperatorMenuItem}
	 * according to the operator type.
	 * 
	 * @param menu
	 *            the menu
	 * @param type
	 *            the {@link OperatorType}
	 */
	private static void disableMenu(JMenu menu, OperatorType type) {
		try {
			// de-activate all items on the top menu
			if (menu instanceof IDeactivatableMenu) {
				((IDeactivatableMenu) menu).deactivate(type);
			}
			// get the submenus
			Component[] menuItems = menu.getMenuComponents();
			for (Component c : menuItems) {
				if (c instanceof JMenu)
					disableMenu((JMenu) c, type);
			}
		} catch (Exception e) {
			logger.error("An error occurred when de-activating the " + type
					+ " menu items: " + e.getMessage());
		}
	}

	/**
	 * Disables all menu entries, where an image is required.
	 */
	protected static void disableImageMenus() {
		AbstractMenuBar menuBar = (AbstractMenuBar) GUITools.getMainFrame()
				.getCoreMenuBar();
		int nItems = menuBar.getComponentCount();

		for (int i = 0; i < nItems; i++) {
			if (menuBar.getMenu(i) instanceof JMenu) {
				disableMenu(menuBar.getMenu(i), OperatorType.IMAGE);
			}
		}
	}

	/**
	 * Enables all menu entries, where a plot is required.
	 */
	protected static void enablePlotMenus() {
		AbstractMenuBar menuBar = (AbstractMenuBar) GUITools.getMainFrame()
				.getCoreMenuBar();
		int nMenus = menuBar.getMenuCount();

		for (int i = 0; i < nMenus; i++) {
			if (menuBar.getMenu(i) instanceof JMenu) {
				enableMenu(menuBar.getMenu(i), OperatorType.PLOT);
			}
		}
	}

	/**
	 * Disables all menu entries where a plot is required.
	 */
	protected static void disablePlotMenus() {
		AbstractMenuBar menuBar = (AbstractMenuBar) GUITools.getMainFrame()
				.getCoreMenuBar();
		int nMenus = menuBar.getMenuCount();

		for (int i = 0; i < nMenus; i++) {
			if (menuBar.getMenu(i) instanceof JMenu) {
				disableMenu(menuBar.getMenu(i), OperatorType.PLOT);
			}
		}
	}
	
	/**
	 * Disables all menu entries, where a table is required.
	 */
	protected static void disableTableMenus() {
		AbstractMenuBar menuBar = (AbstractMenuBar) GUITools.getMainFrame()
				.getCoreMenuBar();
		int nItems = menuBar.getComponentCount();

		for (int i = 0; i < nItems; i++) {
			if (menuBar.getMenu(i) instanceof JMenu) {
				disableMenu(menuBar.getMenu(i), OperatorType.TABLE);
			}
		}
	}

	/**
	 * Disables all menus, where items are required.
	 */
	public static void setInitialMenuActivation() {
		disableImageMenus();
		disablePlotMenus();
		disableTableMenus();
	}

	/**
	 * Enables menu items, where a {@link DataType#PLOT} is required. 
	 * Disables all other menu items.
	 */
	public static void enablePlotMenusExclusively() {
		disableImageMenus();
		enablePlotMenus();
		disableTableMenus();
	}

	/**
	 * Enables menu items, where a {@link DataType#IMAGE} is required. 
	 * Disables all other menu items.
	 */
	public static void enableImageMenusExclusively() {
		enableImageMenus();
		disablePlotMenus();
		disableTableMenus();
	}
	
	/**
	 * Enables menu items, where a {@link DataType#TABLE} is required. 
	 * Disables all other menu items.
	 */
	public static void enableTableMenusExclusively() {
		disableImageMenus();
		disablePlotMenus();
		enableTableMenus();
	}

	/**
	 * Add a file location of a given data type to the list of recently opened files.
	 * 
	 * @param f the {@link File} object
	 * @param d the {@link DataType}
	 */
	public synchronized static void addToRecentFiles(File f, DataType d) {
		RecentFilesMenu.getCurrentInstance().addRecentFile(f, d);
	}

}
