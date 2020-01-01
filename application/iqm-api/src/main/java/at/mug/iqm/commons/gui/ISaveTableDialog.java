package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ISaveTableDialog.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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

import java.io.File;

/**
 * This interface provides signature for a file dialog.
 * 
 * @author Philipp Kainz
 * @since 3.2
 */
public interface ISaveTableDialog {

	/**
	 * Shows the dialog and lets the user choose a file name.
	 * 
	 * @return the {@link File} object
	 */
	File showDialog();

	/**
	 * Returns the file extension for the selected file.
	 * 
	 * @return a {@link String} without leading "."
	 */
	String getExtension();

	/**
	 * Returns whether or not the model should be exported (according to the
	 * selection state of the check box).
	 * 
	 * @return <code>true</code> if the model should be exported,
	 *         <code>false</code> if the table should be exported.
	 */
	boolean exportModel();
}
