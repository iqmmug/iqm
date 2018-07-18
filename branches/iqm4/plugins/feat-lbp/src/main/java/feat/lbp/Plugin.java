package feat.lbp;

/*
* This file is part of IQM, hereinafter referred to as "this program".
* 
* Copyright (C) 2009 - 2014 Helmut Ahammer, Philipp Kainz
* 
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
*/

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.plugin.AbstractPlugin;
import at.mug.iqm.api.plugin.IPlugin;

/**
 * This class represents the entry point of a plugin. The IQM plugin framework
 * launches {@link #init()} and so registers the plugin and the corresponding
 * operator.
 * 
 * @author Philipp Kainz
 * 
 */
public class Plugin extends AbstractPlugin {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 6649962234708122962L;

	@Override
	public IPlugin init() {
		// register the operator (self-registration)
		IOperatorDescriptor odesc = FeatLBPDescriptor.register();

		// register the plugin and associate the plugin with the operator
		// (self-registration)
		Application.getPluginRegistry().register(this, odesc.getName());

		// print out the plugin and operator information to console
		System.out.println("Plugin: '" + this.getName() + "' with operator '"
				+ odesc.getName() + "' initialized!");

		// return a reference to this plugin
		return this;
	}
}
