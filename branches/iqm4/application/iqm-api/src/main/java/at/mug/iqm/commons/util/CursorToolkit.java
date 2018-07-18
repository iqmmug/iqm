package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: CursorToolkit.java
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


import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

/**
 * Basic CursorToolkit that swallows mouseclicks see
 * http://www.javaspecialists.eu/archive/Issue065.html
 * */
public class CursorToolkit {
	private final static MouseAdapter mouseAdapter = new MouseAdapter() {
	};

	private CursorToolkit() {
	}

	/** Sets cursor for specified component to Wait cursor */
	public static void startWaitCursor(JComponent component) {
		RootPaneContainer root = ((RootPaneContainer) component
				.getTopLevelAncestor());
		root.getGlassPane().setCursor(CursorFactory.getWaitCursor());
		root.getGlassPane().addMouseListener(mouseAdapter);
		root.getGlassPane().setVisible(true);
	}

	/** Sets cursor for specified component to normal cursor */
	public static void stopWaitCursor(JComponent component) {
		RootPaneContainer root = ((RootPaneContainer) component
				.getTopLevelAncestor());
		root.getGlassPane().setCursor(CursorFactory.getDefaultCursor());
		root.getGlassPane().removeMouseListener(mouseAdapter);
		root.getGlassPane().setVisible(false);
	}

	// public static void main(String[] args) {
	// final JFrame frame = new JFrame("Test App");
	// frame.getContentPane().add(
	// new JLabel("I'm a Frame"), BorderLayout.NORTH);
	// frame.getContentPane().add(
	// new JButton(new AbstractAction("Wait Cursor") {
	// public void actionPerformed(ActionEvent event) {
	// System.out.println("Setting Wait cursor on frame");
	// startWaitCursor(frame.getRootPane());
	// }
	// }));
	// frame.setSize(800, 600);
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// frame.show();
	// }
}
