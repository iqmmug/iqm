package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: TableDisplayFrame.java
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


import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

 
 

import at.mug.iqm.api.gui.ITablePanel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.gui.util.GUITools;


/**
 * This class is used to display table data within IQM.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 */
public class TableDisplayFrame extends JFrame implements WindowListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7026605668014295502L;

	// class specific logger
	  

	/**
	 * The table panel class is used in this frame as container.
	 */
	private ITablePanel tablePanel = null;

	/**
	 * This method initializes the frame.
	 */
	public TableDisplayFrame() {
		super();
	}

	/**
	 * This method creates and shows the GUI.
	 */
	public void createAndShowGUI() {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.application.magenta.32x32")).getImage());

		this.setTitle(I18N
				.getGUILabelText("application.dialog.table.generic.title"));

		this.tablePanel = new TablePanel(); // create an anonymous table panel
											// for the frame
		
		Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
		int width = dimScreen.width / 3;
		int height = dimScreen.height / 8;
		this.getContentPane().setPreferredSize(new Dimension(width, height));

		this.getContentPane().add((JPanel) this.tablePanel);

		this.addWindowListener(this); // for custom exit strategy
		this.pack();
	}

	/**
	 * This method closes the window
	 */
	private void destroy() {
		this.dispose();
		GUITools.setCurrTableJFrame(null);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		try {
			this.destroy();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("IQM Error: "+ e1);
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

	/**
	 * @return the table
	 */
	public JTable getTable() {
		return this.tablePanel.getTableClone();
	}

	/**
	 * @param table
	 *            the table to set
	 */
	public void setTable(JTable table) {
		this.tablePanel.setTable(table);		
	}

	/**
	 * @param tableModel
	 *            the tableModel to set
	 */
	public void setTableModel(TableModel tableModel) {
		this.tablePanel.setTableModel(tableModel);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				TableDisplayFrame frame = new TableDisplayFrame();
				frame.createAndShowGUI();			
			}
		});
	}

	public ITablePanel getTablePanel() {
		return this.tablePanel;
	}
}
