package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ISaveImageDialog.java
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

import java.io.File;

/**
 * This interface provides signature for a file dialog.
 * 
 * @author Philipp Kainz
 * 
 */
public interface ISaveImageDialog {

	/**
	 * Shows the dialog and lets the user choose a file name.
	 * 
	 * @return the {@link File} object
	 */
	File showDialog();

	/**
	 * Gets the encoding of the image file according to the extension or
	 * selected file filter.
	 * 
	 * @return a protocol {@link String} for the image file writer (JAI)
	 */
	String getEncoding();

	/**
	 * Returns the file extension for the selected file.
	 * 
	 * @return a string without leading "."
	 */
	String getExtension();

	/**
	 * Indicates whether or not the visible ROIs should be superimposed on the
	 * image before exporting.
	 * 
	 * @return <code>true</code>, if all visible ROIs are about to be written to
	 *         the image, <code>false</code> otherwise
	 */
	boolean drawROIs();

}
