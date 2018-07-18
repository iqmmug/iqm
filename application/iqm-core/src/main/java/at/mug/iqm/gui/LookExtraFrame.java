package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: LookExtraFrame.java
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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.workflow.Look;

/**
 * This class represents an image canvas window outside the main frame.
 * 
 * @author Helmut Ahammer
 * @since  2012 02 24
 */

public class LookExtraFrame extends JFrame implements WindowListener{

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -352188282236658964L;
	
	/**
	 * Custom class logger.
	 */
	private static final Logger logger = Logger.getLogger(LookExtraFrame.class);
	
	/**
	 * The container.
	 */
	private JPanel       contentPane = null;
	/**
	 * The instance of {@link LookPanel} held in this {@link LookExtraFrame}.
	 */
	private LookPanel 	lookPanel = null;

	/**
	 * Default constructor. {@link JFrame#setVisible(boolean)} has to
	 * be called manually.
	 */
	public LookExtraFrame() {
		super();
		logger.debug("Creating extra look frame...");
		
		this.createAndAssemble();
	
		logger.debug("Done.");
	}

	
	/**
	 * This method creates and assembles the GUI.
	 */
	public void createAndAssemble() {
		logger.debug("Constructing GUI elements...");
		
		this.setPreferredSize(new Dimension(800, 600));
		this.setResizable(true);
		this.setContentPane(this.createJContentPane());

		logger.debug("ExtraLookFrame LookPanel instance: " + this.lookPanel);
		
		this.setIconImage(new ImageIcon(Resources.getImageURL("icon.application.red.32x32")).getImage());
		
		this.setTitle(I18N.getGUILabelText("look.frame.title"));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);  //there is an extra quit routine
		this.addWindowListener(this);              //for exit
		this.pack();
		CommonTools.centerFrameOnScreen(this);
	}


	/**
	 * This method initializes controlsPanel
	 * @return javax.swing.JPanel
	 */
	public JPanel createJContentPane() {
		if (this.contentPane == null) {
			this.contentPane = new JPanel();
			this.contentPane.setLayout(new BorderLayout());		
			this.contentPane.add(this.getLookPanel());
		}
		return this.contentPane;
	}

	/**
	 * Get the look panel.
	 * 
	 * @return the {@link LookPanel}
	 */
	public LookPanel getLookPanel() {
		if (this.lookPanel == null){
			this.lookPanel = new LookPanel();
			this.lookPanel.initialize();
		}
		return this.lookPanel;
	}
	
	public void setLookPanel(LookPanel lookPanel) {
		this.lookPanel = lookPanel;
	}

	/**
	 * This method destroys the GUI
	*/
	public void destroyGUI() throws Exception{
		
		this.lookPanel.destroyGUI();
		this.dispose();

		logger.debug("Window destroyed, now running garbage collector.");	
		System.gc();
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {
		logger.debug("Activated LookPanel instance, setting current instance in Look: " + this.getLookPanel());
		Look.getInstance().setCurrentLookPanel(this.getLookPanel());
		
//		logger.debug("Activated LookPanel instance, setting LookDisplayJAI in CoreTools: " + this.getLookPanel().getLookDisplayJAI());
//		CoreTools.setLookDisplayJAI(this.getLookPanel().getLookDisplayJAI()); 
	}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowClosing(WindowEvent arg0) {
		try {
			this.destroyGUI();
		} catch (Exception e1) {	
			logger.error("Error while destruction: ", e1);
		}	
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}
}
