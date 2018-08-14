package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: IDeactivatableMenu.java
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


import at.mug.iqm.api.operator.OperatorType;


/**
 * This interface is implemented by the menus in IQM.
 * The application observer can call the deactivateItems() 
 * method and deactivates menu items for a specific operator
 * type. 
 * 
 * @author Philipp Kainz
 *
 */
public interface IDeactivatableMenu {
	
	/**
	 * Deactivates {@link OperatorMenuItem}s according to a given type.
	 * 
	 * @param type 
	 */
	void deactivate(OperatorType type);
	
	/**
	 * Activates {@link OperatorMenuItem}s according to a given type.
	 * 
	 * @param type
	 */
	void activate(OperatorType type);
		
	/**
	 * Deactivates all menu items on the menu.
	 */
	void deactivateAll();
	
	/**
	 * Activates all menu items on the menu.
	 */
	void activateAll();
	
}
