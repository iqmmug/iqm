package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: GenericFileDialog.java
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

import javax.swing.JFileChooser;

/**
 * This file dialog represents a generic wrapper for choosing or saving files.
 * The action (save/open) is determined using the
 * {@link #showOpenDialog(java.awt.Component)} or
 * {@link #showSaveDialog(java.awt.Component)} method in the calling instance.
 * 
 * @author Philipp Kainz
 * 
 */
public class GenericFileDialog extends JFileChooser {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -8530273651140769333L;

	public GenericFileDialog(String path, String title) {
		super(path);
		this.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.setMultiSelectionEnabled(false);
		this.setAcceptAllFileFilterUsed(true);
	}
	
	public GenericFileDialog(){
		super();
	}
}
