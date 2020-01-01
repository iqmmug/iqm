package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: RecentFilesMenu.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.recentfiles.RecentFilesManager;

/**
 * 
 * This menu implements a list of recently opened files via the open dialogs or
 * drag and drop (images) on the TankPanel.
 * 
 * @author Philipp Kainz
 * 
 */
public class RecentFilesMenu extends JMenu implements ActionListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -7445651388124491594L;

	// private logger
	public static final Logger logger = LogManager.getLogger(RecentFilesMenu.class);

	/**
	 * The menu items for recently opened files.
	 */
	private ArrayList<JMenuItem> recentMenuItems;
	/**
	 * The list of recent items.
	 */
	private List<at.mug.iqm.recentfiles.jaxb.File> recentItems;

	/**
	 * Default menu item for <code>empty</code>. This element is always disabled
	 * and just visible, if there are no items in the list.
	 */
	private JMenuItem emptyListItem;

	/**
	 * The menu item for clearing the list.
	 */
	private JMenuItem clearListItem;

	/**
	 * The maximum number of items to keep in the list of recent items. Default
	 * is <code>15</code>.
	 */
	private int maxItems = 15;

	/**
	 * A reference to the current instance.
	 */
	private static RecentFilesMenu currentInstance = null;

	/**
	 * The constructor.
	 */
	public RecentFilesMenu() {
		this.recentMenuItems = new ArrayList<JMenuItem>();
		this.recentItems = new ArrayList<at.mug.iqm.recentfiles.jaxb.File>();
		setCurrentInstance(this);
	}

	/**
	 * Reads the list of recent files.
	 * 
	 * @return a {@link List} of {@link at.mug.iqm.recentfiles.jaxb.File}
	 *         objects
	 */
	protected List<at.mug.iqm.recentfiles.jaxb.File> readRecentFiles() {
		// read out the list of recently used documents/files using the
		// RecentFilesManager
		RecentFilesManager rfm = RecentFilesManager.getCurrentInstance();
		try {
			rfm.read();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		recentItems = rfm.getRecentFiles().getFileList().getFile();
		return recentItems;
	}

	/**
	 * Adds a recent file of a given data type to the menu.
	 * 
	 * @param f the {@link File}
	 * @param dataType the {@link DataType}
	 */
	public void addRecentFile(File f, DataType dataType) {
		try {
			RecentFilesManager.getCurrentInstance().addNewRecentFile(f,
					dataType, maxItems);
			createMenu();
		} catch (Exception e) {
			logger.error(
					"An error occurred, could not add the file to list of recent files. ",
					e);
		}
	}

	/**
	 * Creates the menu.
	 */
	public void createMenu() {
		// read recent items
		recentItems = readRecentFiles();

		// clear the items
		recentMenuItems.clear();
		this.removeAll();

		if (recentItems.isEmpty()) {
			recentMenuItems.add(getEmptyList());
		} else {
			for (at.mug.iqm.recentfiles.jaxb.File f : recentItems) {
				// construct a corresponding menu item
				JMenuItem item = constructMenuItem(f);
				recentMenuItems.add(item);
			}
		}

		// add all files in descending order to the menu
		for (Iterator<JMenuItem> iterator = recentMenuItems.iterator(); iterator
				.hasNext();) {
			JMenuItem item = (JMenuItem) iterator.next();
			this.add(item);
		}

		// add a separator
		this.addSeparator();

		// add the item for clearing the list
		this.add(this.getClearListItem());
	}

	/**
	 * Constructs the menu item.
	 * 
	 * @param f the {@link at.mug.iqm.recentfiles.jaxb.File} 
	 * @return the dynamically constructed {@link JMenuItem}
	 */
	private JMenuItem constructMenuItem(at.mug.iqm.recentfiles.jaxb.File f) {
		JMenuItem item = new JMenuItem();
		
		// reconstruct the path to the file
		final File fullPath = new File(f.getPath() + IQMConstants.FILE_SEPARATOR
						+ f.getName());

		// strip the names if the path is > maxLen chars
		int maxLen = 20;
		if (f.getPath().length() > maxLen) {
			// search for the next path seperator after 25 characters
			String shortPath = f.getPath().substring(0,
					f.getPath().indexOf(IQMConstants.FILE_SEPARATOR, maxLen)+1)
					+ "..." + IQMConstants.FILE_SEPARATOR + f.getName();
			item.setText(shortPath);
		} else {
			item.setText(fullPath.getAbsolutePath());
		}

		// set the entire path to the tool tip text
		item.setToolTipText(f.getPath() + IQMConstants.FILE_SEPARATOR
				+ f.getName());

		// add an icon depending on the data type
		switch (DataType.valueOf(f.getDataType())) {
		case IMAGE:
			item.setIcon(new ImageIcon(Resources
					.getImageURL("icon.image.generic16")));
			
			// add custom action listener
			item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Tank.getInstance().loadImagesFromHD(new File[]{ fullPath });
				}
			});
			
			break;
		case PLOT:
			item.setIcon(new ImageIcon(Resources
					.getImageURL("icon.plot.generic16")));

			// add custom action listener
			item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Tank.getInstance().runPlotParser(fullPath);
				}
			});
			
			break;
		case TABLE:
			item.setIcon(new ImageIcon(Resources
					.getImageURL("icon.table.generic16")));
			
			// TODO currently not supported
			
			break;
		case CUSTOM:
			item.setIcon(new ImageIcon(Resources
					.getImageURL("icon.custom.generic16")));
			
			// TODO currently not supported
			
			break;
		default:
			break;
		}

		return item;
	}

	/**
	 * Gets the number of maximum items to be kept in the menu.
	 * 
	 * @return the number of maximum items to be kept in the menu
	 */
	public int getMaxItems() {
		return maxItems;
	}

	/**
	 * Sets the number of maximum items to be kept in the menu.
	 * 
	 * @param maxItems
	 */
	public void setMaxItems(int maxItems) {
		this.maxItems = maxItems;
	}

	private JMenuItem getEmptyList() {
		emptyListItem = new JMenuItem();
		emptyListItem.setEnabled(false);

		emptyListItem.setText(I18N
				.getGUILabelText("menu.file.openRecent.empty"));

		return emptyListItem;
	}

	public JMenuItem getClearListItem() {
		clearListItem = new JMenuItem();

		clearListItem.setText(I18N
				.getGUILabelText("menu.file.openRecent.clear"));
		clearListItem.addActionListener(this);
		clearListItem.setActionCommand("clearList");

		return clearListItem;
	}

	/**
	 * Clears the entire list of recent files.
	 */
	public void clearList() {
		try {
			// remove the file entries
			RecentFilesManager.getCurrentInstance().clearRecentFiles();
			// rebuild the menu entries
			createMenu();
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("clearList")) {
			this.clearList();
		}
	}

	/**
	 * Sets the current instance of the menu.
	 */
	private static void setCurrentInstance(RecentFilesMenu currentInstance) {
		RecentFilesMenu.currentInstance = currentInstance;
	}

	/**
	 * Gets the current instance of the menu.
	 */
	public static RecentFilesMenu getCurrentInstance() {
		return currentInstance;
	}
}
