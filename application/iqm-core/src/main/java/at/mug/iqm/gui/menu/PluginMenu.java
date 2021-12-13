package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: PluginMenu.java
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


import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JMenu;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.exception.UnknownOperatorException;
import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.plugin.IPlugin;
import at.mug.iqm.api.plugin.IPluginMenu;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.registry.PluginRegistry;
import at.mug.iqm.core.workflow.ExecutionProxy;

/**
 * This class represents a dynamic menu for IQM plugins.
 * 
 * @author Philipp Kainz
 * 
 */
public class PluginMenu extends JMenu implements IPluginMenu {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7908601443483638065L;

	/**
	 * The menu for the image plugins.
	 */
	private JMenu imageMenu;

	/**
	 * A registry of all image menu items.
	 */
	private HashMap<String, OperatorMenuItem> imageItems;

	/**
	 * A list of relative paths in the image plugin menu.
	 */
	private HashMap<String, JMenu> imageSubMenus = new HashMap<String, JMenu>();

	/**
	 * The menu for the plot plugins.
	 */
	private JMenu plotMenu;

	/**
	 * A registry of all plot menu items.
	 */
	private HashMap<String, OperatorMenuItem> plotItems;

	/**
	 * A list of relative paths in the plot plugin menu.
	 */
	private HashMap<String, JMenu> plotSubMenus = new HashMap<String, JMenu>();
	
	/**
	 * The menu for the other plugins.
	 */
	private JMenu otherMenu;
	
	/**
	 * A registry of all other menu items.
	 */
	private HashMap<String, OperatorMenuItem> otherItems;
	
	/**
	 * A list of relative paths in the OTHER plugin menu.
	 */
	private HashMap<String, JMenu> otherSubMenus = new HashMap<String, JMenu>();
	
	/**
	 * The menu for the scripts.
	 */
	private ScriptMenu scriptMenu;

	/**
	 * Private constructor.
	 */
	private PluginMenu() {
		super(I18N.getGUILabelText("application.menu.plugin.text"));
		this.imageMenu = new JMenu(
				I18N.getGUILabelText("application.menu.plugin.image.text"));
		this.imageMenu.setName("image");
		this.imageSubMenus.put("/", this.imageMenu);
		this.imageItems = new HashMap<String, OperatorMenuItem>();

		this.plotMenu = new JMenu(
				I18N.getGUILabelText("application.menu.plugin.plot.text"));
		this.plotMenu.setName("plot");
		this.plotSubMenus.put("/", this.plotMenu);
		this.plotItems = new HashMap<String, OperatorMenuItem>();
		
		this.otherMenu = new JMenu(
				I18N.getGUILabelText("application.menu.plugin.other.text"));
		this.otherMenu.setName("other");
		this.otherSubMenus.put("/", this.otherMenu);
		this.otherItems = new HashMap<String, OperatorMenuItem>();
		
		this.scriptMenu = new ScriptMenu();
		
		this.add(this.imageMenu);
		this.add(this.plotMenu);
		this.add(this.otherMenu);
		this.addSeparator();
		this.add(this.scriptMenu);

		Application.setPluginMenu(this);
	}

	/**
	 * Gets the reference to this singleton.
	 * 
	 * @return the singleton instance
	 */
	public static IPluginMenu getInstance() {
		IPluginMenu menu = Application.getPluginMenu();
		if (menu == null) {
			menu = new PluginMenu();
		}
		return menu;
	}

	@Override
	public void addMenuItem(IPlugin plugin) {
		this.addMenuItem(plugin, "/");
	}

	@Override
	public void addMenuItem(IPlugin plugin, String relativePath) {
		// get the type of the plugin
		String type = plugin.getType();
		if (type.equalsIgnoreCase("image")) {
			OperatorMenuItem item = new OperatorMenuItem(OperatorType.IMAGE);
			item.setText(plugin.getName() + " [v" + plugin.getVersion() + "]");
			item.setActionCommand(plugin.getUID());
			item.addActionListener(this);
			item.setIcon(plugin.getEnabledMenuIcon());
			item.setDisabledIcon(plugin.getDisabledMenuIcon());

			if (relativePath.equals("/image") || relativePath.equals("/")) {
				// mount the standard path to plugins/image
				item.setMountPoint("/");
			} else {
				// strip the name
				if (relativePath.startsWith("/image")) {
					relativePath = relativePath.substring(6);
				}
				// mount the item to a custom location
				item.setMountPoint(relativePath);

				// if the menu structure for a given path does not yet exist,
				// build it
				if (!this.imageSubMenus.containsKey(relativePath)) {
					// build the menu structure
					this.buildMenuStructure(imageMenu, relativePath,
							imageSubMenus);
				}
			}

			// finally add the item to the image-item registry
			this.imageItems.put(item.getText(), item);

		} else if (type.equalsIgnoreCase("plot")) {
			OperatorMenuItem item = new OperatorMenuItem(OperatorType.PLOT);
			item.setText(plugin.getName() + " [v" + plugin.getVersion() + "]");
			item.setActionCommand(plugin.getUID());
			item.addActionListener(this);
			item.setIcon(plugin.getEnabledMenuIcon());
			item.setDisabledIcon(plugin.getDisabledMenuIcon());

			if (relativePath.equals("/plot") || relativePath.equals("/")) {
				item.setMountPoint("/");
			} else {
				// strip the name
				if (relativePath.startsWith("/plot")) {
					relativePath = relativePath.substring(5);
				}
				// mount the item to a custom location
				item.setMountPoint(relativePath);

				// if the menu structure for a given path does not yet exist,
				// build it
				if (!this.plotSubMenus.containsKey(relativePath)) {
					// build the menu structure
					this.buildMenuStructure(plotMenu, relativePath,
							plotSubMenus);
				}
			}
			this.plotItems.put(item.getText(), item);
		}
		else if (type.equalsIgnoreCase("other")) {
			OperatorMenuItem item = new OperatorMenuItem(OperatorType.OTHER);
			item.setText(plugin.getName() + " [v" + plugin.getVersion() + "]");
			item.setActionCommand(plugin.getUID());
			item.addActionListener(this);
			item.setIcon(plugin.getEnabledMenuIcon());
			item.setDisabledIcon(plugin.getDisabledMenuIcon());

			if (relativePath.equals("/other") || relativePath.equals("/")) {
				item.setMountPoint("/");
			} else {
				// strip the name
				if (relativePath.startsWith("/other")) {
					relativePath = relativePath.substring(5);
				}
				// mount the item to a custom location
				item.setMountPoint(relativePath);

				// if the menu structure for a given path does not yet exist,
				// build it
				if (!this.otherSubMenus.containsKey(relativePath)) {
					// build the menu structure
					this.buildMenuStructure(otherMenu, relativePath,
							otherSubMenus);
				}
			}
			this.otherItems.put(item.getText(), item);
		}
		
		this.update();
	}

	/**
	 * Builds a deep structure and adds the new {@link JMenu} items to a given
	 * registry.
	 * 
	 * @param rootMenu
	 *            either the image, plot or script root menu
	 * @param relativePath
	 *            the relative path as string like "/group/subgroup"
	 * @param registry
	 *            the registry where the new created (sub)menus go
	 * @return a reference to the deepest element in the created menu structure
	 *         for a given relative path
	 */
	protected JMenu buildMenuStructure(JMenu rootMenu, String relativePath,
			HashMap<String, JMenu> registry) {
		JMenu parentMenu = rootMenu;
		// look up, if the sub menu already exists
		String[] menuNames = relativePath.substring(1).split("/");
		// this yields the names of the menus, e.g. [0]=group, [1]=subgroup

		String path = "";
		// construct all menus
		for (String s : menuNames) {
			JMenu subMenu = new JMenu(s);
			subMenu.setName(s);
			parentMenu.add(subMenu);
			parentMenu = subMenu;

			// 1st iteration it is "/group"
			// 2nd iteration it is "/group/subgroup"
			path = path + "/" + s;
			registry.put(path, parentMenu);
		}

		// finally return the menu where the item goes
		return parentMenu;
	}

	@Override
	public void update() {
		this.sortAndAdd();
		this.revalidate();
		this.repaint();
	}

	@Override
	public void sortAndAdd() {
		// ###### IMAGE MENU STRUCTURE ######
		// sort all image menu items at first (key = item.getText())
		Set<String> imageItemKeys = this.imageItems.keySet();
		ArrayList<String> sortedImageItemKeys = new ArrayList<String>(
				imageItemKeys);
		Collections.sort(sortedImageItemKeys);

		// get each element from the registry of all menu items
		// results in something like "/group", "/group/subgroup",
		// "/anotherGroup", "/anotherGroup/anotherSubGroup/", ...
		Set<String> imageSubMenuKeys = this.imageSubMenus.keySet();
		// sort the keys
		ArrayList<String> sortedimageSubMenuKeys = new ArrayList<String>(
				imageSubMenuKeys);
		Collections.sort(sortedimageSubMenuKeys);

		// look up, whether or not there exists elements for this menu by
		// comparing the mount point of the OperatorMenuItems stored in the
		// registry with the key of the menu
		for (int index = 0; index < sortedimageSubMenuKeys.size(); index++) {
			String keyName = sortedimageSubMenuKeys.get(index);
			JMenu theSubMenu = this.imageSubMenus.get(keyName);

			// image item keys are already sorted
			for (String s : sortedImageItemKeys) {
				OperatorMenuItem item = this.imageItems.get(s);
				if (item.getMountPoint().equals(keyName)) {
					// add the item to the menu
					theSubMenu.add(item);
				}
			}
		}

		// ###### PLOT MENU STRUCTURE ######
		// sort all plot menu items at first (key = item.getText())
		Set<String> plotItemKeys = this.plotItems.keySet();
		ArrayList<String> sortedPlotItemKeys = new ArrayList<String>(
				plotItemKeys);
		Collections.sort(sortedPlotItemKeys);

		// get each element from the registry of all menu items
		// results in something like "/group", "/group/subgroup",
		// "/anotherGroup", "/anotherGroup/anotherSubGroup/", ...
		Set<String> plotSubMenuKeys = this.plotSubMenus.keySet();
		// sort the keys
		ArrayList<String> sortedPlotSubMenuKeys = new ArrayList<String>(
				plotSubMenuKeys);
		Collections.sort(sortedPlotSubMenuKeys);

		// look up, whether or not there exists elements for this menu by
		// comparing the mount point of the OperatorMenuItems stored in the
		// registry with the key of the menu
		for (int index = 0; index < sortedPlotSubMenuKeys.size(); index++) {
			String keyName = sortedPlotSubMenuKeys.get(index);
			JMenu theSubMenu = this.plotSubMenus.get(keyName);

			// plot item keys are already sorted
			for (String s : sortedPlotItemKeys) {
				OperatorMenuItem item = this.plotItems.get(s);
				if (item.getMountPoint().equals(keyName)) {
					// add the item to the menu
					theSubMenu.add(item);
				}
			}
		}

		// ###### OTHER MENU STRUCTURE ######
		// sort all other menu items at first (key = item.getText())
		Set<String> otherItemKeys = this.otherItems.keySet();
		ArrayList<String> sortedOtherItemKeys = new ArrayList<String>(
				otherItemKeys);
		Collections.sort(sortedOtherItemKeys);

		// get each element from the registry of all menu items
		// results in something like "/group", "/group/subgroup",
		// "/anotherGroup", "/anotherGroup/anotherSubGroup/", ...
		Set<String> otherSubMenuKeys = this.otherSubMenus.keySet();
		// sort the keys
		ArrayList<String> sortedOtherSubMenuKeys = new ArrayList<String>(
				otherSubMenuKeys);
		Collections.sort(sortedOtherSubMenuKeys);

		// look up, whether or not there exists elements for this menu by
		// comparing the mount point of the OperatorMenuItems stored in the
		// registry with the key of the menu
		for (int index = 0; index < sortedOtherSubMenuKeys.size(); index++) {
			String keyName = sortedOtherSubMenuKeys.get(index);
			JMenu theSubMenu = this.otherSubMenus.get(keyName);

			// other item keys are already sorted
			for (String s : sortedOtherItemKeys) {
				OperatorMenuItem item = this.otherItems.get(s);
				if (item.getMountPoint().equals(keyName)) {
					// add the item to the menu
					theSubMenu.add(item);
				}
			}
		}
	
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String pluginUID = e.getActionCommand();
		System.out.println("Plugin UID has been called: " + pluginUID);
		String operatorName = PluginRegistry.getInstance().findOperatorName(
				pluginUID);
		System.out.println("The associated operator is :" + operatorName);

		// CALL THE PROXY METHOD
		try {
			ExecutionProxy.launchInstance(operatorName);
		} catch (UnknownOperatorException e1) {
			DialogUtil.getInstance().showErrorMessage(null, e1, true);
		}
	}

	@Override
	public void deactivate(OperatorType type) {
		switch (type) {
		case IMAGE:
			this.deactivateImageMenuItems();
			break;

		case PLOT:
			this.deactivatePlotMenuItems();
			break;

		default:
			break;
		}
	}

	@Override
	public void activate(OperatorType type) {

		switch (type) {
		case IMAGE:
			this.activateImageMenuItems();
			break;

		case PLOT:
			this.activatePlotMenuItems();
			break;

		default:
			break;
		}
	}

	@Override
	public void deactivateAll() {
		this.deactivateImageMenuItems();
		this.deactivatePlotMenuItems();
	}

	@Override
	public void activateAll() {
		this.activateImageMenuItems();
		this.activatePlotMenuItems();
	}

	/**
	 * Activates all image menu items.
	 */
	protected void activateImageMenuItems() {
		Set<String> imageMenuItemKeys = this.imageItems.keySet();
		for (String key : imageMenuItemKeys) {
			this.imageItems.get(key).setEnabled(true);
		}
	}

	/**
	 * Activates all plot menu items.
	 */
	protected void activatePlotMenuItems() {
		Set<String> plotMenuItemKeys = this.plotItems.keySet();
		for (String key : plotMenuItemKeys) {
			this.plotItems.get(key).setEnabled(true);
		}
	}

	/**
	 * Deactivates all image menu items.
	 */
	protected void deactivateImageMenuItems() {
		Set<String> imageMenuItemKeys = this.imageItems.keySet();
		for (String key : imageMenuItemKeys) {
			this.imageItems.get(key).setEnabled(false);
		}
	}

	/**
	 * Deactivates all plot menu items.
	 */
	protected void deactivatePlotMenuItems() {
		Set<String> plotMenuItemKeys = this.plotItems.keySet();
		for (String key : plotMenuItemKeys) {
			this.plotItems.get(key).setEnabled(false);
		}
	}
}
