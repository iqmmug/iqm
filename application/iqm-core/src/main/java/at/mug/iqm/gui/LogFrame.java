package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: LogFrame.java
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
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class holds the static instance of {@link BoardPanel} in 
 * a {@link JFrame}.
 * @author Philipp Kainz
 *
 */
public class LogFrame extends JFrame implements WindowListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3348559897504047466L;
	
	// Standard class logger
	private static final Logger logger = LogManager.getLogger(LogFrame.class);

	/**
	 * Constructs a new log frame.
	 */
	public LogFrame() {
		logger.debug("Constructing log frame...");
		this.setTitle(I18N.getGUILabelText("board.frame.title"));
		// custom window closing handler
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.setPreferredSize(new Dimension(500, 400));
		this.setIconImage(new ImageIcon(Resources.getImageURL("icon.menu.info.logWindow")).getImage());
		this.add(new JScrollPane(GUITools.getMainFrame().getBoardPanel()));
		this.pack();
		this.setLocation(new Point(
				(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - this.getWidth() - 20, 
				(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - this.getHeight() - 70));
	}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		GUITools.setLogFrame(null);
		this.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}
	
	
}
