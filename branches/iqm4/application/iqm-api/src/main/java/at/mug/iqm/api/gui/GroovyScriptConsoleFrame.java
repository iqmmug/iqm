package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: GroovyScriptConsoleFrame.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.Resources;
import at.mug.iqm.commons.io.PlainTextFileWriter;
import at.mug.iqm.commons.util.PropertyManager;

/**
 * The output of a running Groovy script will be redirected to this console.
 * 
 * @author Philipp Kainz
 * 
 */
public class GroovyScriptConsoleFrame extends JFrame {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -9021344817902594714L;
	private JTextArea console;
	private static GroovyScriptConsoleFrame instance;
	private PropertyManager pm;

	/**
	 * Create the frame.
	 */
	private GroovyScriptConsoleFrame() {
		setTitle(I18N.getGUILabelText("script.console.frame.defaultTitle"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(new ImageIcon(
				Resources.getImageURL("icon.script.generic16")).getImage());
		getContentPane().setLayout(new BorderLayout(0, 0));

		console = new JTextArea();
		console.setEditable(false);
		console.setBorder(new EmptyBorder(5, 5, 5, 5));
		console.setFont(new Font("Consolas", Font.PLAIN, 13));
		console.setRows(20);
		console.setColumns(50);
		getContentPane().add(new JScrollPane(console));

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnConsole = new JMenu(
				I18N.getGUILabelText("script.console.menu.file.text"));
		menuBar.add(mnConsole);

		JMenuItem mntmExit = new JMenuItem(
				I18N.getGUILabelText("script.console.menu.exit.text"));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JMenuItem mntmClear = new JMenuItem(
				I18N.getGUILabelText("script.console.menu.clear.text"));
		mntmClear.setToolTipText(I18N
				.getGUILabelText("script.console.menu.clear.ttp"));
		mntmClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearText();
			}
		});
		mnConsole.add(mntmClear);

		JMenuItem mntmSaveAs = new JMenuItem(
				I18N.getGUILabelText("script.console.menu.saveas.text"));
		mntmSaveAs.setToolTipText(I18N
				.getGUILabelText("script.console.menu.saveas.ttp"));
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});
		mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				getToolkit().getMenuShortcutKeyMask()));
		mnConsole.add(mntmSaveAs);
		mnConsole.addSeparator();

		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				getToolkit().getMenuShortcutKeyMask()));
		mnConsole.add(mntmExit);

		pm = PropertyManager.getManager(this);
		
		pack();
	}

	/**
	 * Clears the console.
	 */
	public void clearText() {
		console.setText(null);
	}

	/**
	 * Save the file using a file chooser
	 */
	public void saveAs() {
		// locate a new file
		JFileChooser fc = new JFileChooser(getCurDir());
		fc.setAcceptAllFileFilterUsed(false);

		FileFilter txtFilter = new FileNameExtensionFilter(
				IQMConstants.TXT_FILTER_DESCRIPTION, IQMConstants.TXT_EXTENSION);
		fc.setFileFilter(txtFilter);

		int ans = fc.showSaveDialog(this);

		if (ans != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File f = fc.getSelectedFile();
		
		// append ".txt" if necessary
		int idx = f.getName().lastIndexOf(".");
		if (idx == -1){
			f = new File(f.getPath() + ".txt");
		}
		
		// save the location
		pm.setProperty("dir", f.getParent());
		save(f);
	}

	private File getCurDir() {
		return new File(
				pm.getProperty("dir") == null ? System.getProperty("user.home")
						: pm.getProperty("dir"));
	}

	private void save(File f) {
		PlainTextFileWriter ptfw = new PlainTextFileWriter(f, getConsole()
				.getText());
		Thread t = new Thread(ptfw);
		t.start();
	}

	/**
	 * Gets the {@link JTextArea} where the input goes.
	 * 
	 * @return the {@link JTextArea}
	 */
	public JTextArea getConsole() {
		return console;
	}

	/**
	 * Gets the singleton instance of the frame.
	 * 
	 * @return the singleton instance
	 */
	public static GroovyScriptConsoleFrame getInstance() {
		if (instance == null) {
			instance = new GroovyScriptConsoleFrame();
		}
		return instance;
	}
}
