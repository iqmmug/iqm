package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: DeactivatableMenu.java
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


import javax.swing.JMenu;

import at.mug.iqm.api.gui.IDeactivatableMenu;
import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.operator.OperatorType;

/**
 * @author Philipp Kainz
 * 
 */
public class DeactivatableMenu extends JMenu implements IDeactivatableMenu {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -4940080843567167575L;

	@Override
	public void deactivate(OperatorType type) {
		switch (type) {
		case IMAGE:
			this.deactivateImageMenuItems();
			break;

		case PLOT:
			this.deactivatePlotMenuItems();
			break;

		case TABLE:
			this.deactivateTableMenuItems();
			break;
			
		case OTHER:
			this.deactivateOtherMenuItems();
			break;
			
		default:
			break;
		}
	}

	@Override
	public void activate(OperatorType type) {

		switch (type) {
		case IMAGE:
			this.activateImageMenuItems();
			break;

		case PLOT:
			this.activatePlotMenuItems();
			break;
		
		case TABLE:
			this.activateTableMenuItems();
			break;
			
		case OTHER:
			this.activateOtherMenuItems();
			break;
			
		default:
			break;
		}
	}

	@Override
	public void deactivateAll() {
		this.deactivateImageMenuItems();
		this.deactivatePlotMenuItems();
		this.deactivateTableMenuItems();
//		this.deactivateOtherMenuItems();
	}

	@Override
	public void activateAll() {
		this.activateImageMenuItems();
		this.activatePlotMenuItems();
		this.activateTableMenuItems();
		this.activateOtherMenuItems();
	}

	/**
	 * Activates all image menu items.
	 */
	protected void activateImageMenuItems() {
		for (int i = 0; i < this.getItemCount(); i++) {
			try {
				OperatorMenuItem item = (OperatorMenuItem) this.getItem(i);
				if (item != null && item.getType() == OperatorType.IMAGE) {
					item.setEnabled(true);
				}
			} catch (ClassCastException ignoredForSeparators) {
			}
		}
	}

	/**
	 * Activates all plot menu items.
	 */
	protected void activatePlotMenuItems() {
		for (int i = 0; i < this.getItemCount(); i++) {
			try {
				OperatorMenuItem item = (OperatorMenuItem) this.getItem(i);
				if (item != null && item.getType() == OperatorType.PLOT) {
					item.setEnabled(true);
				}
			} catch (ClassCastException ignoredForSeparators) {
			}
		}
	}
	
	/**
	 * Activates all table menu items.
	 */
	protected void activateTableMenuItems() {
		for (int i = 0; i < this.getItemCount(); i++) {
			try {
				OperatorMenuItem item = (OperatorMenuItem) this.getItem(i);
				if (item != null && item.getType() == OperatorType.TABLE) {
					item.setEnabled(true);
				}
			} catch (ClassCastException ignoredForSeparators) {
			}
		}
	}
	
	/**
	 * Activates all 'OTHER' menu items.
	 */
	protected void activateOtherMenuItems() {
		for (int i = 0; i < this.getItemCount(); i++) {
			try {
				OperatorMenuItem item = (OperatorMenuItem) this.getItem(i);
				if (item != null && item.getType() == OperatorType.OTHER) {
					item.setEnabled(true);
				}
			} catch (ClassCastException ignoredForSeparators) {
			}
		}
	}

	/**
	 * Deactivates all image menu items.
	 */
	protected void deactivateImageMenuItems() {
		for (int i = 0; i < this.getItemCount(); i++) {
			try {
				OperatorMenuItem item = (OperatorMenuItem) this.getItem(i);
				if (item != null && item.getType() == OperatorType.IMAGE) {
					item.setEnabled(false);
				}
			} catch (ClassCastException ignoredForSeparators) {
			}
		}
	}

	/**
	 * Deactivates all plot menu items.
	 */
	protected void deactivatePlotMenuItems() {
		for (int i = 0; i < this.getItemCount(); i++) {
			try {
				OperatorMenuItem item = (OperatorMenuItem) this.getItem(i);
				if (item != null && item.getType() == OperatorType.PLOT) {
					item.setEnabled(false);
				}
			} catch (ClassCastException ignoredForSeparators) {
			}
		}
	}
	
	/**
	 * Deactivates all table menu items.
	 */
	protected void deactivateTableMenuItems() {
		for (int i = 0; i < this.getItemCount(); i++) {
			try {
				OperatorMenuItem item = (OperatorMenuItem) this.getItem(i);
				if (item != null && item.getType() == OperatorType.TABLE) {
					item.setEnabled(false);
				}
			} catch (ClassCastException ignoredForSeparators) {
			}
		}
	}
	
	/**
	 * Deactivates all 'OTHER' menu items.
	 */
	protected void deactivateOtherMenuItems() {
		for (int i = 0; i < this.getItemCount(); i++) {
			try {
				OperatorMenuItem item = (OperatorMenuItem) this.getItem(i);
				if (item != null && item.getType() == OperatorType.OTHER) {
					item.setEnabled(false);
				}
			} catch (ClassCastException ignoredForSeparators) {
			}
		}
	}

}
