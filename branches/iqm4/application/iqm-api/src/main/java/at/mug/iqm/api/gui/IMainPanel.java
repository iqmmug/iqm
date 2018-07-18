package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: IMainPanel.java
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

import javax.swing.JLabel;

/**
 * This interface declares methods for the main panel.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IMainPanel {

	/**
	 * Get the {@link JLabel} displaying the current memory information of the
	 * JVM.
	 * 
	 * @return the memory label
	 */
	JLabel getMemoryLabel();

	/**
	 * A flag for setting the tick at the checkbox.
	 * 
	 * @param virtual
	 */
	void setVirtual(boolean virtual);
}
