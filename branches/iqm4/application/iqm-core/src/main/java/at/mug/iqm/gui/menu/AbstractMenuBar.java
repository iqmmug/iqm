package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: AbstractMenuBar.java
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


import javax.swing.JMenu;
import javax.swing.JMenuBar;


/**
 * This is the base class for the menu bars. Each concrete implementation has to extend
 * this class.
 * @author Philipp Kainz
 */
public abstract class AbstractMenuBar extends JMenuBar {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -4935879658423522815L;
	
	/**
	 * This method constructs the items and is implemented by the subclasses.
	 */
	protected abstract void createAndAssemble();

	/**
	 * This method enables other applications to add menu items to the menu bar.
	 * @param abstractMenu - a {@link JMenu} instance to add
	 */
	public void addNewMenu(JMenu abstractMenu){
		this.add(abstractMenu);
	}
}


