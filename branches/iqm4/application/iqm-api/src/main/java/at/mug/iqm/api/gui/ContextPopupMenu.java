package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ContextPopupMenu.java
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


import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;

public class ContextPopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6782357694491178195L;
	
	/**
	 * The action listener for the popup menu.
	 */
	private ActionListener actionListener;
	
	/**
	 * The menu item for the image information.
	 */
	private JMenuItem menuItemImageInfo;
	
	/**
	 * The menu item for the image header.
	 */
	private JMenuItem menuItemImageHeader;
	
	/**
	 * The menu item for deleting the current ROI.
	 */
	private JMenuItem menuItemDeleteCurrROI;
	
	/**
	 * The menu item for deleting all ROIs on the currently selected layer.
	 */
	private JMenuItem menuItemDeleteAllROI;
	
	/**
	 * The menu item for deleting all ROIs on any layer.
	 */
	private JMenuItem menuItemDeleteAllROIsAllLayers;
	
	/**
	 * Default constructor. The {@link ActionListener} has to be set
	 * explicitly the corresponding commands for the {@link ContextPopupMenu}.
	 * Also, the {@link ContextPopupMenu#createAndAssemble()} has to be called, 
	 * after all {@link JMenuItem}s have been constructed. 
	 * <p>
	 * For default behavior don't set custom {@link JMenuItem}s to this instance.
	 * </p>
	 */
	public ContextPopupMenu(){}
	
	/**
	 * Custom constructor. The {@link ActionListener} implements the 
	 * corresponding commands for the {@link ContextPopupMenu}.
	 * @param actionListener
	 */
	public ContextPopupMenu(ActionListener actionListener) {
		this.setActionListener(actionListener);
		this.createAndAssemble();
	}

	
	/**
	 * This method adds all menu items to the popup menu.
	 */
	public void createAndAssemble() {
		this.add(this.getMenuItemImageInfo());
		this.add(this.getMenuItemImageHeader());
		this.add(this.getMenuItemDeleteCurrROI());
		this.add(this.getMenuItemDeleteAllROI());
		this.addSeparator();
		this.add(this.getMenuItemDeleteROIAllLayers());
	}

	/**
	 * @return the actionListener
	 */
	public ActionListener getActionListener() {
		return actionListener;
	}

	/**
	 * @param actionListener the actionListener to set
	 */
	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	/**
	 * This menu emits the default action command <code>imageinfo</code>, where the 
	 * {@link ActionListener} has to implement the corresponding action.
	 * 
	 * @return the menuItemImageInfo
	 */
	public JMenuItem getMenuItemImageInfo() {
		if (menuItemImageInfo == null){
			menuItemImageInfo = new JMenuItem(I18N.getGUILabelText("popup.imageInfo.text"));
			menuItemImageInfo.setActionCommand("imageinfo");
			menuItemImageInfo.setIcon(new ImageIcon(Resources.getImageURL("icon.popup.currImg.info")));
			menuItemImageInfo.addActionListener(this.getActionListener());
		}
		return menuItemImageInfo;
	}

	/**
	 * @param menuItemImageInfo the menuItemImageInfo to set
	 */
	public void setMenuItemImageInfo(JMenuItem menuItemImageInfo) {
		this.menuItemImageInfo = menuItemImageInfo;
	}

	/**
	 * This menu emits the default action command <code>imageheader</code>, where the 
	 * {@link ActionListener} has to implement the corresponding action.
	 * 
	 * @return the menuItemImageHeader
	 */
	public JMenuItem getMenuItemImageHeader() {
		if (menuItemImageHeader == null){
			menuItemImageHeader = new JMenuItem(I18N.getGUILabelText("popup.imageHeader.text"));
			menuItemImageHeader.setActionCommand("imageheader");
			menuItemImageHeader.setIcon(new ImageIcon(Resources.getImageURL("icon.popup.currImg.header")));
			menuItemImageHeader.addActionListener(this.getActionListener());
		}
		return menuItemImageHeader;
	}

	/**
	 * @param menuItemImageHeader the menuItemImageHeader to set
	 */
	public void setMenuItemImageHeader(JMenuItem menuItemImageHeader) {
		this.menuItemImageHeader = menuItemImageHeader;
	}

	/**
	 * This menu emits the default action command <code>deletecurrroi</code>, where the 
	 * {@link ActionListener} has to implement the corresponding action.
	 * 
	 * @return the menuItemDeleteCurrROI
	 */
	public JMenuItem getMenuItemDeleteCurrROI() {
		if (menuItemDeleteCurrROI == null){
			menuItemDeleteCurrROI = new JMenuItem(I18N.getGUILabelText("popup.deleteSelectedROI.text"));
			menuItemDeleteCurrROI.setIcon(new ImageIcon(Resources.getImageURL("icon.popup.deleteSelectedROI")));
			menuItemDeleteCurrROI.setActionCommand("deletecurrroi");
			menuItemDeleteCurrROI.addActionListener(this.getActionListener());
		}
		return menuItemDeleteCurrROI;
	}

	/**
	 * @param menuItemDeleteCurrROI the menuItemDeleteCurrROI to set
	 */
	public void setMenuItemDeleteCurrROI(JMenuItem menuItemDeleteCurrROI) {
		this.menuItemDeleteCurrROI = menuItemDeleteCurrROI;
	}

	/**
	 * This menu emits the default action command <code>deleteallrois</code>, where the 
	 * {@link ActionListener} has to implement the corresponding action.
	 * 
	 * @return the menuItemDeleteAllROI
	 */
	public JMenuItem getMenuItemDeleteAllROI() {
		if (menuItemDeleteAllROI == null){
			menuItemDeleteAllROI = new JMenuItem(I18N.getGUILabelText("popup.deleteAllROIs.text"));
			menuItemDeleteAllROI.setActionCommand("deleteallrois");
			menuItemDeleteAllROI.setIcon(new ImageIcon(Resources.getImageURL("icon.popup.deleteAllROIs")));
			menuItemDeleteAllROI.addActionListener(this.getActionListener());
		}
		return menuItemDeleteAllROI;
	}

	/**
	 * @param menuItemDeleteAllROI the menuItemDeleteAllROI to set
	 */
	public void setMenuItemDeleteAllROI(JMenuItem menuItemDeleteAllROI) {
		this.menuItemDeleteAllROI = menuItemDeleteAllROI;
	}
	
	/**
	 * This menu emits the default action command <code>deleteallroisalllayers</code>, where the 
	 * {@link ActionListener} has to implement the corresponding action.
	 * 
	 * @return the menuItemDeleteAllROIsAllLayers
	 */
	public JMenuItem getMenuItemDeleteROIAllLayers() {
		if (menuItemDeleteAllROIsAllLayers == null){
			menuItemDeleteAllROIsAllLayers = new JMenuItem(I18N.getGUILabelText("popup.deleteAllROIs.allLayers.text"));
			menuItemDeleteAllROIsAllLayers.setActionCommand("deleteallroisalllayers");
			menuItemDeleteAllROIsAllLayers.setIcon(new ImageIcon(Resources.getImageURL("icon.popup.deleteAllROIs")));
			menuItemDeleteAllROIsAllLayers.addActionListener(this.getActionListener());
		}
		return menuItemDeleteAllROIsAllLayers;
	}

	/**
	 * @param menuItemDeleteAllROIsAllLayers the menuItemDeleteAllROIsAllLayers to set
	 */
	public void setMenuItemDeleteROIAllLayers(JMenuItem menuItemDeleteAllROIsAllLayers) {
		this.menuItemDeleteAllROIsAllLayers = menuItemDeleteAllROIsAllLayers;
	}
	
	
}
