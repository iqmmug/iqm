/*
 * @(#)SummonMenuItem.java
 *
 * $Date$
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */

package com.bric.window;

/*
 * #%L
 * Project: IQM - Application Core
 * File: SummonMenuItem.java
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBoxMenuItem;

/**
 * This menu item calls <code>Frame.toFront()</code> when the item is selected.
 * 
 */
public class SummonMenuItem extends JCheckBoxMenuItem {

	private static final long serialVersionUID = 1L;

	Frame frame;

	ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			frame.toFront();
			if (frame.getExtendedState() == Frame.ICONIFIED)
				frame.setExtendedState(Frame.NORMAL);
			setSelected(true);
		}
	};

	/**
	 * Create a new <code>SummonMenuItem</code>.
	 * 
	 * @param f
	 *            the frame to bring to front when this menu item is activated
	 */
	public SummonMenuItem(Frame f) {
		super();
		frame = f;
		addActionListener(actionListener);
		updateText();

		frame.addPropertyChangeListener("title", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				updateText();
			}
		});

		// this UI is buggy, and has issues.
		// the main issue is that it won't even show up on Macs
		// if you use the screen menubar, and since the goal
		// is to emulate macs: why bother?
		// if(frame instanceof JFrame)
		// setUI(new FrameMenuItemUI((JFrame)frame));
	}

	private void updateText() {
		String text = frame.getTitle();
		if (text == null || text.trim().length() == 0)
			text = "Untitled";
		setText(text);
	}
}
