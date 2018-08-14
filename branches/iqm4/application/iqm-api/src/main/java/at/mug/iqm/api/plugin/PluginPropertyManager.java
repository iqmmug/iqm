package at.mug.iqm.api.plugin;

/*
 * #%L
 * Project: IQM - API
 * File: PluginPropertyManager.java
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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import at.mug.iqm.commons.util.XMLPropertyManager;

/**
 * A reader for XML-formatted {@link Properties} and other plugin related stuff.
 * <p>
 * The class reads the content of <code>iqm-plugin.xml</code>, which is located
 * right next to the implementing class of {@link IPlugin} in the class path.
 * <p>
 * This class also constructs the enabled/disabled icons for the menu items.
 * They can either be default or customized.
 * 
 * @author Philipp Kainz
 * 
 */
public class PluginPropertyManager extends XMLPropertyManager{

	/**
	 * Read the properties from <code>iqm-plugin.xml</code>.
	 * 
	 * @param plugin
	 * @return the {@link Properties} from the plugin
	 */
	public static synchronized Properties readPluginProperties(IPlugin plugin) {
		try {
			String packageName = plugin.getClass().getPackage().getName()
					.replace(".", "/");

			InputStream fileInput = plugin.getClass().getResourceAsStream(
					"/" + packageName + "/iqm-plugin.xml");
			Properties properties = new Properties();
			properties.loadFromXML(fileInput);
			fileInput.close();

			return properties;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets the default menu icon for an active plugin menu item.
	 * <p>
	 * <b>The use of a 16x16 PNG image is encouraged.</b>
	 * 
	 * @return the {@link ImageIcon} located in
	 *         <code>"/icons/plugin/menu/default-enabled.png"</code>
	 */
	public static synchronized ImageIcon getDefaultEnabledMenuIcon() {
		try {

			InputStream is = PluginPropertyManager.class
					.getResourceAsStream("/icons/plugin/menu/default-enabled.png");

			ImageIcon icon = null;

			icon = new ImageIcon(ImageIO.read(is));
			is.close();

			return icon;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets the default menu icon for an active plugin menu item.
	 * <p>
	 * <b>The use of a 16x16 PNG image is encouraged.</b>
	 * 
	 * @return the {@link ImageIcon} located in
	 *         <code>"/icons/plugin/menu/default-disabled.png"</code>
	 */
	public static synchronized ImageIcon getDefaultDisabledMenuIcon() {
		try {

			InputStream is = PluginPropertyManager.class
					.getResourceAsStream("/icons/plugin/menu/default-disabled.png");

			ImageIcon icon = null;

			icon = new ImageIcon(ImageIO.read(is));
			is.close();

			return icon;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets a custom menu item for a plugin.
	 * <p>
	 * <b>The use of a 16x16 PNG image is encouraged.</b>
	 * 
	 * @return the {@link ImageIcon} located in the plugin's resource directory
	 */
	public static synchronized ImageIcon getCustomMenuIcon(IPlugin plugin,
			String fileName) {
		try {
			String packageName = plugin.getClass().getPackage().getName()
					.replace(".", "/");

			InputStream is = plugin.getClass().getResourceAsStream(
					"/" + packageName + "/icons/" + fileName);

			ImageIcon icon = null;

			icon = new ImageIcon(ImageIO.read(is));
			is.close();

			return icon;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
