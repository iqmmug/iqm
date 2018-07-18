package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: TextPanel.java
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.ITextPanel;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;


/**
 * This frame is able to display plain text from a string.
 * 
 * @author Philipp Kainz
 */
public class TextPanel extends JPanel implements ITextPanel, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -985683849398150097L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(TextPanel.class);

	// class variable declaration
	private final JTextPane textPane = new JTextPane();
	private static final String NEWLINE = "\n";
	private final JPopupMenu popup = new JPopupMenu();
	
	/**
	 * A flag whether or not the panel currently displays an item.
	 */
	private boolean isEmpty = true;

	private String debugHTMLContent = "<html><ul>"
			+ "<li>Morbi in sem quis dui placerat ornare. Pellentesque odio nisi, euismod in, pharetra a, ultricies in, diam. Sed arcu. Cras consequat.</li>"
			+ "<li>Praesent dapibus, neque id cursus faucibus, tortor neque egestas augue, eu vulputate magna eros eu erat. Aliquam erat volutpat. Nam dui mi, tincidunt quis, accumsan porttitor, facilisis luctus, metus.</li>"
			+ "<li>Phasellus ultrices nulla quis nibh. Quisque a lectus. Donec consectetuer ligula vulputate sem tristique cursus. Nam nulla quam, gravida non, commodo a, sodales sit amet, nisi.</li>"
			+ "<li>Pellentesque fermentum dolor. Aliquam quam lectus, facilisis auctor, ultrices ut, elementum vulputate, nunc.</li>"
			+ "</ul><br/></html>";
	private JButton btnReset;

	/**
	 * This is the constructor.
	 */
	public TextPanel() {
		super();
		setBackground(Color.GRAY);
		logger.debug("Creating new instance...");
		
		setBorder(new MatteBorder(5, 1, 1, 1, (Color) Color.GRAY));
		this.createAndAssemble();
	}

	/**
	 * Creates the GUI.
	 */
	public void createAndAssemble() {

		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(2);
		borderLayout.setHgap(2);
		this.setLayout(borderLayout);
		textPane.setContentType("text/plain");
		textPane.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new EmptyBorder(10, 10, 10, 10)));

		JMenuItem menuItem = new JMenuItem("Copy to clip board");
		menuItem.setActionCommand("copy");
		menuItem.addActionListener(this);
		menuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.edit.copy")));

		JMenuItem menuItemClear = new JMenuItem(
				I18N.getGUILabelText("board.popup.clear"));
		menuItemClear.setActionCommand("clear");
		menuItemClear.addActionListener(this);
		menuItemClear.setIcon(new ImageIcon(Resources
				.getImageURL("icon.popup.board.clear")));

		this.popup.add(menuItem);
		this.popup.add(menuItemClear);

		// Add popup listener to components that can bring up pop-up
		// menus.
		MouseListener popupListener = new PopupListener();
		this.textPane.addMouseListener(popupListener);

		this.textPane.setFont(new Font("Courier New", Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(this.textPane);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.add(scrollPane, BorderLayout.CENTER);

		JPanel debugPanel = new JPanel();
		debugPanel.setBackground(Color.GREEN);
//		add(debugPanel, BorderLayout.NORTH);

		JLabel label = new JLabel("DEBUG BUTTONS");
		debugPanel.add(label);

		JButton button = new JButton("Reset");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		debugPanel.add(button);

		JButton btnAddString = new JButton("Add String");
		btnAddString.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				writeLine(debugHTMLContent);
			}
		});
		debugPanel.add(btnAddString);
		
		JPanel panelOptions = new JPanel();
		panelOptions.setPreferredSize(new Dimension(10, 26));
		panelOptions.setBorder(new EmptyBorder(2, 2, 2, 2));
		panelOptions.setBackground(Color.LIGHT_GRAY);
		add(panelOptions, BorderLayout.SOUTH);
		panelOptions.setLayout(new BorderLayout(0, 0));
		
		btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		panelOptions.add(btnReset, BorderLayout.EAST);
	}

	/**
	 * Append one line to the {@link JTextPane}.
	 * 
	 * @param str
	 */
	@Override
	public void writeLine(String str) {
		this.textPane.setText(this.textPane.getText().concat(str).concat(NEWLINE));
		this.textPane.setCaretPosition(0);
		this.isEmpty = false;
	}

	/**
	 * Append more text to the {@link JTextPane} without line breaks.
	 * 
	 * @param str
	 */
	@Override
	public void writeText(String str) {
		this.textPane.setText(this.textPane.getText().concat(str));
		this.textPane.setCaretPosition(0);
		this.isEmpty = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("copy".equals(e.getActionCommand())) {
			StringSelection selection = new StringSelection(
					this.textPane.getText());
			try {
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(selection, null);
				DialogUtil
						.getInstance()
						.showDefaultInfoMessage(
								I18N
										.getMessage("application.frame.textDisplay.copy.success"));
			} catch (IllegalStateException ex) {
				DialogUtil
						.getInstance()
						.showDefaultErrorMessage(
								I18N
										.getMessage("application.frame.textDisplay.copy.error"));
			}
		} else if ("clear".equals(e.getActionCommand())) {
			this.reset();
		}
	}

	@Override
	public void reset() {
		this.textPane.setText("");
		this.textPane.setCaretPosition(0);
		this.isEmpty = true;
	}
	
	/**
	 * Returns whether or not the panel currently displays an item.
	 * 
	 * @return <code>true</code>, if an item is displayed, <code>false</code>,
	 *         if not
	 */
	@Override
	public boolean isEmpty() {
		return isEmpty;
	}

	private class PopupListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public Object getText() {
		return this.textPane.getText();
	}

}
