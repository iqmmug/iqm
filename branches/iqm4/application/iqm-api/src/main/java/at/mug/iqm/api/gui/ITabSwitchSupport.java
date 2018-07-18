package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ITabSwitchSupport.java
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


/**
 * This interface provides methods for selecting a tab and accessing selected
 * tabs.
 * 
 * @author Philipp Kainz
 * 
 */
public interface ITabSwitchSupport {

	/**
	 * Set the selected tab.
	 * 
	 * @param i
	 *            the tab index
	 */
	void setSelectedTabIndex(int i);

	/**
	 * Get the selected tab.
	 * 
	 * @return the tab index
	 */
	int getSelectedTabIndex();

}
