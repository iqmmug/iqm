package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: MainPanel.java
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


import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.gui.IMainPanel;
import at.mug.iqm.core.I18N;

/**
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2012 02 24
 */ 
public class MainPanel extends JPanel implements IMainPanel{

	private static final long serialVersionUID = -2405514212516374323L;

	// Logging variables
	private static final Logger logger = LogManager.getLogger(MainPanel.class);

	// variable declaration
	//private AbstractMenuBar	      menuBar; 
	private ControlPanel		controlPanel;
	private JLabel               memoryLabel;

	/**
	 * This method creates a new default (core) instance of IqmMainPanel. 
	 */
	public MainPanel() {
		logger.debug("Creating new instance of '" + this.getClass().getName() + "'.");
		
		// variable initialization	
		this.controlPanel = new ControlPanel();

		this.memoryLabel = new JLabel();

		// construct the gui
		this.createAndShowGUI();
	}

	/**
	 * This method creates and shows the GUI
	 */
	protected void createAndShowGUI(){
		this.setLayout(new BorderLayout());	
		this.add(this.getControlPanel(), BorderLayout.CENTER);
		this.add(this.createMemoryLabel(),   BorderLayout.EAST);
		this.setVisible(true);	 	
	}

	/**
	 * This method initializes memoryLabel
	 * @return javax.swing.JLabel
	 */
	protected JLabel createMemoryLabel() {
		long maxMem = Runtime.getRuntime().maxMemory();
		maxMem = maxMem/1024/1024;
		this.memoryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		this.memoryLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		this.memoryLabel.setText(I18N.getGUILabelText("main.memoryLabel.max", maxMem));
		return memoryLabel;
	}
	
	@Override
	public void setVirtual(boolean virtual) {
		this.controlPanel.getCheckBoxVirtual().setSelected(virtual);
	}

	/**
	 * @return the controlPanel
	 */
	public ControlPanel getControlPanel() {
		return controlPanel;
	}


	/**
	 * @param controlPanel the controlPanel to set
	 */
	public void setControlPanel(ControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}


	/**
	 * @param memoryLabel the memoryLabel to set
	 */
	public void setMemoryLabel(JLabel memoryLabel) {
		this.memoryLabel = memoryLabel;
	}

	/**
	 * @return the memoryLabel
	 */
	public JLabel getMemoryLabel() {
		return memoryLabel;
	}
}
