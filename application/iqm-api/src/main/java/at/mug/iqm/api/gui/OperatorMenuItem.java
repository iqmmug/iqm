package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: OperatorMenuItem.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2021 Helmut Ahammer, Philipp Kainz
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


import javax.swing.JMenuItem;

import at.mug.iqm.api.operator.IOperator;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.OperatorType;

/**
 * This menu item class is used to distinguish between certain kinds of operator
 * types and enables a type-specific activation and deactivation of the
 * {@link OperatorMenuItem}.
 * 
 * @author Philipp Kainz
 * 
 */
public class OperatorMenuItem extends JMenuItem implements IAssignableMenuItem {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 9127440735693507608L;

	/**
	 * The type of the operator triggered by the menu item.
	 */
	private OperatorType type;

	/**
	 * The relative location to a menu.
	 */
	private String mountPoint;

	/**
	 * Constructs a new {@link OperatorMenuItem} and assigns
	 * {@link OperatorType#UNASSIGNED} to the menu item.
	 */
	public OperatorMenuItem() {
		this.type = OperatorType.UNASSIGNED;
	}

	/**
	 * Constructs a new {@link OperatorMenuItem} and assigns a given
	 * {@link OperatorType} to the menu item.
	 * 
	 * @param type
	 */
	public OperatorMenuItem(OperatorType type) {
		this.type = type;
	}

	/**
	 * Constructs a new {@link OperatorMenuItem} and assigns the
	 * {@link OperatorType} of the {@link IOperator} to the menu item as
	 * described in the corresponding {@link IOperatorDescriptor}.
	 * 
	 * @param operator
	 */
	public OperatorMenuItem(IOperator operator) {
		this.type = operator.getType();
	}

	/**
	 * Set the operator type of the menu item.
	 * 
	 * @param type
	 */
	public void setType(OperatorType type) {
		this.type = type;
	}

	/**
	 * Gets the operator type of the menu item.
	 * 
	 * @return the {@link OperatorType}
	 */
	public OperatorType getType() {
		return this.type;
	}

	/**
	 * Gets the item's mount point.
	 * 
	 * @return the mount point
	 */
	@Override
	public String getMountPoint() {
		return this.mountPoint;
	}

	/**
	 * Sets the item's mount point.
	 * 
	 * @param mountPoint
	 */
	@Override
	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}
}
