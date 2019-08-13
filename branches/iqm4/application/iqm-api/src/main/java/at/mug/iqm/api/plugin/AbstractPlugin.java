package at.mug.iqm.api.plugin;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractPlugin.java
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


import java.io.InputStream;
import java.util.Properties;

import javax.swing.ImageIcon;


/**
 * The abstract super class for all plugins.
 * 
 * @author Philipp Kainz
 *
 */
public abstract class AbstractPlugin implements IPlugin {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -2828853162709331533L;
	
	/**
	 * The properties from the XML file.
	 */
	protected Properties props = PluginPropertyManager.readPluginProperties(this);

	/** 
	 * @see at.mug.iqm.api.plugin.IPlugin#init()
	 */
	@Override
	public abstract IPlugin init();

	
	@Override
	public String getUID() {
		return this.props.getProperty("uid");
	}
	
	@Override
	public String getName() {
		return this.props.getProperty("name");
	}
	
	@Override
	public String getActionCommand() {
		return this.getUID();
	}
	
	@Override
	public String getVersion() {
		return this.props.getProperty("version");
	}
	
	@Override
	public String getType() {
		return this.props.getProperty("type");
	}

	@Override
	public Properties getProperties() {
		return PluginPropertyManager.readPluginProperties(this);
	}
	
	public ImageIcon getEnabledMenuIcon(){
		// look up if menu-item-disabled.png exists in the package of the plugin
		String fileName = "menu-item-enabled.png";
		String location = "/" + this.getClass().getPackage().getName().replace(".", "/") + "/icons/" + fileName;
		InputStream is = this.getClass().getResourceAsStream(location);
		if (is == null){
			return PluginPropertyManager.getDefaultEnabledMenuIcon();
		}else {
			return PluginPropertyManager.getCustomMenuIcon(this, fileName);
		}
	}
	
	public ImageIcon getDisabledMenuIcon(){
		// look up if menu-item-disabled.png exists in the package of the plugin
		String fileName = "menu-item-disabled.png";
		String location = "/" + this.getClass().getPackage().getName().replace(".", "/") + "/icons/" + fileName;
		InputStream is = this.getClass().getResourceAsStream(location);
		if (is == null){
			return PluginPropertyManager.getDefaultDisabledMenuIcon();
		}else {
			return PluginPropertyManager.getCustomMenuIcon(this, fileName);
		}
	}
}
