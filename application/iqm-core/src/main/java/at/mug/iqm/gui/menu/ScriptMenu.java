package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ScriptMenu.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.gui.ScriptEditor;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.FileContentParser;
import at.mug.iqm.commons.util.FileTools;
import at.mug.iqm.commons.util.PropertyManager;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;

/**
 * This is the class for the script menu in IQM.
 * 
 * @author Philipp Kainz
 */
public class ScriptMenu extends JMenu implements ActionListener {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 5914103324270969214L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(ScriptMenu.class);

	// class variable declaration
	// private JMenuItem neighDilMenuItem;
	private JMenuItem mntmNew;
	private JMenuItem mntmEdit;
	private JMenuItem mntmImport;
	private JMenuItem mntmExport;
	private JMenu mnInstalledScripts;
	private List<JMenuItem> scriptList;
	private PropertyManager pm;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped ScriptMenu.
	 */
	public ScriptMenu() {
		logger.debug("Generating new instance of 'script' menu.");

		this.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.script")));

		this.pm = PropertyManager.getManager(this);

		// initialize the variables
		// this.neighDilMenuItem = new JMenuItem();

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Menu 'script' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items to 'script' menu.");

		// set menu attributes
		setText(I18N.getGUILabelText("menu.script.text"));

		// assemble: add created elements to the JMenu
		// this.add(this.createNeighDilMenuItem());

		mntmNew = new JMenuItem(I18N.getGUILabelText("menu.script.new.text"));
		mntmNew.setToolTipText(I18N.getGUILabelText("menu.script.new.ttp"));
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newScript();
			}
		});
		add(mntmNew);

		mntmEdit = new JMenuItem(
				I18N.getGUILabelText("menu.script.editrun.text"));
		mntmEdit.setToolTipText(I18N.getGUILabelText("menu.script.editrun.ttp"));
		mntmEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editAndRun();
			}
		});
		add(mntmEdit);
		addSeparator();

		mntmImport = new JMenuItem(
				I18N.getGUILabelText("menu.script.import.text"));
		mntmImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importScripts();
			}
		});
		mntmImport.setToolTipText(I18N
				.getGUILabelText("menu.script.import.ttp"));
		add(mntmImport);

		mntmExport = new JMenuItem(
				I18N.getGUILabelText("menu.script.export.text"));
		mntmExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportScripts();
			}
		});
		mntmExport.setToolTipText(I18N
				.getGUILabelText("menu.script.export.ttp"));
		add(mntmExport);

		mnInstalledScripts = new JMenu(
				I18N.getGUILabelText("menu.script.installedscripts.text"));
		addSeparator();
		add(mnInstalledScripts);

		scriptList = new ArrayList<JMenuItem>(10);
		buildInstalledScriptsMenu();
	}

	/**
	 * Advise the script editor to open a file.
	 */
	protected void editAndRun() {
		ScriptEditor se = ScriptEditor.getInstance();
		se.setVisible(true);
		se.openFile(true);
	}

	protected void newScript() {
		ScriptEditor se = ScriptEditor.getInstance();
		// add a new tab to the editor
		se.setVisible(true);
	}

	// private JMenuItem createNeighDilMenuItem() {
	// this.neighDilMenuItem.setText(I18N
	// .getGUILabelText("menu.script.neighDil.text"));
	// this.neighDilMenuItem.setToolTipText(I18N
	// .getGUILabelText("menu.script.neighDil.ttp"));
	// this.neighDilMenuItem.setIcon(new ImageIcon(Resources
	// .getImageURL("icon.menu.script")));
	// this.neighDilMenuItem.addActionListener(this);
	// this.neighDilMenuItem.setEnabled(false);
	// this.neighDilMenuItem.setActionCommand("neighdil");
	// return neighDilMenuItem;
	// }

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 *      This method sets and performs the corresponding actions to the menu
	 *      items.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// if ("neighdil".equals(e.getActionCommand())) {
		// }
	}

	public void buildInstalledScriptsMenu() {
		logger.debug("Parsing installed script files...");

		// clear list and menu
		scriptList.clear();
		mnInstalledScripts.removeAll();

		// parse the IQM/scripts/ folder for plain text files and add them
		// in the menu
		File scriptPath = null;
		try {
			scriptPath = ConfigManager.getCurrentInstance().getDefaultIqmScriptPath();
			scriptPath = ConfigManager.getCurrentInstance().getScriptPath();
		} catch (Exception e) {
			logger.error("Configuration error, no manager available. ", e);
		}

		File[] allFiles = scriptPath.listFiles();

		for (final File f : allFiles) {

			try {
				// parse content
				if (!isScriptFile(f))
					return;

				// get the file name and assemble a menu item
				String fileName = f.getName();

				JMenuItem item = new JMenuItem();
				item.setText(fileName);
				item.setName(f.getPath());
				item.setToolTipText(f.getPath());
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// open a new editor tab and load this file
						ScriptEditor ed = ScriptEditor.getInstance();
						ed.openInNewTab(f);
						if (!ed.isVisible()) {
							ed.setVisible(true);
						}
						ed.toFront();
					}
				});

				// add the menu item to the list
				scriptList.add(item);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		// sort the menu entries alphabetically
		Collections.sort(scriptList, new Comparator<JMenuItem>() {
			@Override
			public int compare(JMenuItem o1, JMenuItem o2) {
				String name1 = o1.getText();
				String name2 = o2.getText();
				return name1.compareTo(name2);
			}
		});

		// assemble the menu items in the menu
		Iterator<JMenuItem> iter = scriptList.iterator();

		while (iter.hasNext()) {
			mnInstalledScripts.add(iter.next());
		}
		revalidate();
	}

	public void importScripts() {
		// locate files
		File ld = getDirectory("lastImportDir");
		JFileChooser fc = new JFileChooser(ld);
		fc.setMultiSelectionEnabled(true);
		fc.setAcceptAllFileFilterUsed(true);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				
		int ans = fc.showOpenDialog(null);
		
		if (ans != JFileChooser.APPROVE_OPTION){
			return;
		}
		
		File[] sources = fc.getSelectedFiles();
		
		// get the script directory from the configuration
		File target = ConfigManager.getCurrentInstance().getScriptPath();
		
		// parse files and import them
		for (File src : sources){
			try {
				File dest = new File(target.getPath() + File.separator + src.getName());
				
				// TODO handle "overwrite ALL" case
				if (dest.exists()) {
					int ow = DialogUtil.getInstance().showDefaultWarnMessage(
							I18N.getMessage("application.fileExists.overwrite"));
					if (ow == IDialogUtil.YES_OPTION) {
						// perform the writing
						FileTools.copyFile(src, dest);
					}
				} else {
					// perform the writing
					FileTools.copyFile(src, dest);
				}
			} catch (Exception e) {
				logger.error("",e);
			}
		}
		
		// refresh the menu
		buildInstalledScriptsMenu();

		pm.setProperty("lastImportDir", sources[0].getParent());
	}

	public void exportScripts() {
		// locate directory
		File ld = getDirectory("lastExportDir");
		JFileChooser fc = new JFileChooser(ld);
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int ans = fc.showSaveDialog(null);
		
		if (ans != JFileChooser.APPROVE_OPTION){
			return;
		}
		
		File target = fc.getSelectedFile();

		// get the script directory from the configuration
		File srcDir = ConfigManager.getCurrentInstance().getScriptPath();
		
		// list all files from the script directory
		File[] sources = srcDir.listFiles();
		
		for (File src : sources){
			try {
				// TODO skip directories (for now)
				if (src.isDirectory()) continue;
				
				// TODO handle "overwrite ALL" case
				File dest = new File(target.getPath() + File.separator + src.getName());
				
				if (dest.exists()) {
					int ow = DialogUtil.getInstance().showDefaultWarnMessage(
							I18N.getMessage("application.fileExists.overwrite"));
					if (ow == IDialogUtil.YES_OPTION) {
						// perform the writing
						FileTools.copyFile(src, dest);
					}
				} else {
					// perform the writing
					FileTools.copyFile(src, dest);
				}
			} catch (Exception e) {
				logger.error("",e);
			}
		}
		
		pm.setProperty("lastExportDir", target.getPath());
	}

	private File getDirectory(String key) {
		File cd = null;

		// try to read the property file
		String path = pm.getProperty(key);
		if (path == null) {
			// take the user home
			cd = new File(System.getProperty("user.home"));
		} else {
			cd = new File(path);
		}
		return cd;
	}
	
	private boolean isScriptFile(File f){
		// parse content
		String mimetype = FileContentParser.parseContent(f);

		if (mimetype.startsWith("text")) {
			return true;
		}else {
			return false;
		}
	}
}
