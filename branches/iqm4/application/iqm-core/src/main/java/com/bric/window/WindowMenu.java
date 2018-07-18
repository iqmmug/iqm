/*
 * @(#)WindowMenu.java
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
 * File: WindowMenu.java
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

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import at.mug.iqm.core.I18N;

/**
 * This is a Window menu resembling <A HREF=
 * "http://developer.apple.com/documentation/UserExperience/Conceptual/AppleHIGuidelines/XHIGMenus/chapter_17_section_4.html#//apple_ref/doc/uid/TP30000356-TPXREF106"
 * >the menu</A> found in most Cocoa applications.
 * <P>
 * This menu will automatically update itself to always list all visible Frames.
 * Their title will appear in this menu, or the text "Untitled" will be used if
 * no frame title is available.
 * <P>
 * This uses the {@link com.bric.window.WindowList} to keep track of frames,
 * their order, and their layering.
 * <P>
 * As of this version, this class is not a perfect replica of Apple's menu. It
 * lacks a few key elements: <BR>
 * 1. The "Zoom" menu item. In Java it is not directly possible to emulate this
 * behavior. Probably a JNI-based approach would be simplest way to add this
 * feature. <BR>
 * 2. Window titles do not have a bullet displayed next to their name when they
 * have unsaved changes, or a diamond displayed next to their name when
 * minimized. I started to develop a <code>FrameMenuItemUI</code> to address
 * this problem, but then realized that if a Java program on Mac uses the screen
 * menubar (which is the preferred behavior): customized MenuItemUI's are
 * ignored. Apple does some slight-of-hand and maps every JMenuItem to some sort
 * of Cocoa peer, so the UI is ignored. <BR>
 * 3. Holding down the option/alt key doesn't toggle menu items like "Minimize".
 * I was able to implement this when a JMenuBar is placed in the JFrame, but not
 * when the screen menubar is used.
 * 
 * <P>
 * So ironically: I can get more Mac-like behavior on non-Macs. (Which defeats
 * the purpose.) But in the mean time: really all I personally need from my
 * Window menu is a list of available frames, so this meets my needs for now.
 * 
 * @see <a
 *      href="http://javagraphics.blogspot.com/2008/11/windows-adding-window-menu.html">Windows:
 *      Adding a Window Menu</a>
 */
public class WindowMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	/** The menu item that minimizes this window. */
	JMenuItem minimizeItem = new JMenuItem(
			I18N.getGUILabelText("menu.window.minimize.text"));

	/**
	 * The "Bright All to Front" menu item. TODO: this is implemented hackish-ly
	 * and causes windows to flicker over one another. I'm not sure it's worth
	 * keeping; for now the lines that add it to the menu are commented out.
	 */
	JMenuItem bringItem = new JMenuItem(
			I18N.getGUILabelText("menu.window.bringalltofront.text"));

	Runnable updateRunnable = new Runnable() {
		public void run() {
			removeAll();
			add(minimizeItem);
			if (customItems.length != 0) {
				addSeparator();
				for (int a = 0; a < customItems.length; a++) {
					add(customItems[a]);
				}
			}
			addSeparator();
			add(bringItem);
			addSeparator();
			Frame[] frames = WindowList.getFrames(false, false, true);
			for (int a = 0; a < frames.length; a++) {
				JCheckBoxMenuItem item = new SummonMenuItem(frames[a]);
				item.setSelected(frames[a] == myFrame);
				add(item);
			}
			// fixMenuBar(myFrame,WindowMenu.this);
			myFrame.validate();
			myFrame.repaint();
		}
	};

	/**
	 * On Mac often the menus won't really update without this hack-ish twist:
	 * remove the menu and re-add it. Voila! It's both unnecessary and crucial
	 * at the same time. <b>EDIT:</b> calling {@link JFrame#validate()} and
	 * {@link JFrame#repaint()} also accounts for this problem and allows you to
	 * put the menu where you want in your menu bar.
	 * 
	 * @param f
	 *            the frame whose menubar you need to update.
	 * @param menu
	 *            the menu you need to update.
	 */
	static void fixMenuBar(JFrame f, JMenu menu) {
		JMenuBar mb = f.getJMenuBar();
		if (mb != null) {
			JMenu[] menus = new JMenu[mb.getMenuCount()];
			int i = -1;
			for (int a = 0; a < menus.length; a++) {
				menus[a] = mb.getMenu(a);
				if (menus[a] == menu)
					i = a;

				if (i != -1) {
					mb.remove(i);
					mb.add(menus[a]);
				}
			}
		}
	}

	private JFrame myFrame = null;

	private ChangeListener changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			SwingUtilities.invokeLater(updateRunnable);
		}
	};

	ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == minimizeItem) {
				myFrame.setExtendedState(Frame.ICONIFIED);
			} else if (src == bringItem) {
				Frame[] frames = WindowList.getFrames(false, false, true);
				for (int a = 0; a < frames.length; a++) {
					if (frames[a].isVisible()
							|| frames[a].getExtendedState() == Frame.ICONIFIED) {
						frames[a].toFront();
						if (frames[a].getExtendedState() == Frame.ICONIFIED)
							frames[a].setExtendedState(Frame.NORMAL);
					}
				}
			}
		}
	};

	JMenuItem[] customItems;

	/**
	 * Creates a new WindowMenu for a specific JFrame.
	 * 
	 * @param frame
	 *            the frame that this menu belongs to.
	 */
	public WindowMenu(JFrame frame) {
		this(frame, new JMenuItem[] {});
	}

	/**
	 * Creates a new WindowMenu for a specific JFrame.
	 * 
	 * @param frame
	 *            the frame that this menu belongs to.
	 * @param extraItems
	 *            an optional array of extra items to put in this menu.
	 * */
	public WindowMenu(JFrame frame, JMenuItem[] extraItems) {
		super(I18N.getGUILabelText("menu.window.text"));
		minimizeItem.addActionListener(actionListener);
		bringItem.addActionListener(actionListener);

		customItems = new JMenuItem[extraItems.length];
		System.arraycopy(extraItems, 0, customItems, 0, extraItems.length);

		myFrame = frame;
		minimizeItem.setAccelerator(KeyStroke.getKeyStroke('M', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));

		WindowList.addChangeListener(changeListener);
		updateRunnable.run();
	}
}
