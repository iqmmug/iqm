package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: InfoMenu.java
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

import java.awt.Desktop;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.commons.gui.OpenImageDialog;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.OperatingSystem;
import at.mug.iqm.commons.util.image.ImageHeaderExtractor;
import at.mug.iqm.commons.util.image.ImageInfoExtractor;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.registry.OperatorRegistryHelper;
import at.mug.iqm.core.workflow.Look;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.LogFrame;
import at.mug.iqm.gui.TextDisplayFrame;
import at.mug.iqm.gui.TextPanel;
import at.mug.iqm.gui.tilecachetool.TCTool;
import at.mug.iqm.gui.util.GUITools;

/**
 * This is the base class for the info/help menu in IQM.
 * <p>
 * <i>Note: Naming the menu "Help" causes Mac OS to create a searchable help menu.</i>
 * 
 * @author Helmut Ahammer, Philipp Kainz
 */
public class InfoMenu extends DeactivatableMenu implements ActionListener {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -2574293426763777651L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(InfoMenu.class);

	// class variable declaration
	private JMenuItem currImgInfoMenuItem;
	private JMenuItem currImgHeaderMenuItem;
	private JMenuItem currImgDirMenuItem;
	private JMenuItem memInfoMenuItem;
	private JMenuItem systemInfoMenuItem;
	private JMenuItem threadInfoMenuItem;
	private JMenuItem tileInfoMenuItem;
	private JMenuItem boardPanelMenuItem;
	private JMenuItem refreshOperatorRegistry;
	private JMenuItem aboutMenuItem;
	private JMenuItem wikiItem;
	private JMenuItem webSiteItem;
	private JMenuItem issueTrackerItem;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped InfoMenu.
	 */
	public InfoMenu() {
		logger.debug("Generating new instance.");

		// initialize the variables
		this.currImgInfoMenuItem = new JMenuItem();
		this.currImgHeaderMenuItem = new JMenuItem();
		this.currImgDirMenuItem = new JMenuItem();
		this.memInfoMenuItem = new JMenuItem();
		this.systemInfoMenuItem = new JMenuItem();
		this.threadInfoMenuItem = new JMenuItem();
		this.tileInfoMenuItem = new JMenuItem();
		this.boardPanelMenuItem = new JMenuItem();
		this.refreshOperatorRegistry = new JMenuItem();
		this.aboutMenuItem = new JMenuItem();
		this.wikiItem = new JMenuItem();
		this.webSiteItem = new JMenuItem();
		this.issueTrackerItem = new JMenuItem();

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Menu 'info' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items in 'info' menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.info.text"));

		// assemble: add created elements to the JMenu
		this.add(this.createRefreshOperatorRegistryMenuItem());
		this.addSeparator();
		this.add(this.createCurrImgInfoMenuItem());
		this.add(this.createCurrImgHeaderMenuItem());
		this.add(this.createCurrImgDirMenuItem());
		this.add(this.createMemInfoMenuItem());
		this.add(this.createSystemInfoMenuItem());
		this.add(this.createThreadInfoMenuItem());
		this.add(this.createTileInfoMenuItem());
		this.addSeparator();
		this.add(this.createBoardPanelMenuItem());
		this.addSeparator();
		this.add(this.createIssueTrackerItem());
		this.add(this.createWebSiteItem());
		this.add(this.createWikiItem());
		this.add(this.createAboutMenuItem());
	}

	/**
	 * Create the issue tracker item.
	 * 
	 * @return a {@link JMenuItem}
	 */
	private JMenuItem createIssueTrackerItem() {
		this.issueTrackerItem.setText(I18N
				.getGUILabelText("menu.info.issuetracker.text"));
		this.issueTrackerItem.setToolTipText(I18N
				.getGUILabelText("menu.info.issuetracker.ttp"));
		this.issueTrackerItem.addActionListener(this);
		this.issueTrackerItem.setActionCommand("issuetracker");
		return this.issueTrackerItem;
	}

	/**
	 * Create the website item.
	 * 
	 * @return a {@link JMenuItem}
	 */
	private JMenuItem createWebSiteItem() {
		this.webSiteItem
				.setText(I18N.getGUILabelText("menu.info.website.text"));
		this.webSiteItem.setToolTipText(I18N
				.getGUILabelText("menu.info.website.ttp"));
		this.webSiteItem.addActionListener(this);
		this.webSiteItem.setActionCommand("iqmweb");
		return this.webSiteItem;
	}

	/**
	 * Create the wiki menu item.
	 * 
	 * @return a {@link JMenuItem}
	 */
	private JMenuItem createWikiItem() {
		this.wikiItem.setText(I18N.getGUILabelText("menu.info.wiki.text"));
		this.wikiItem
				.setToolTipText(I18N.getGUILabelText("menu.info.wiki.ttp"));
		this.wikiItem.addActionListener(this);
		this.wikiItem.setActionCommand("iqmwiki");
		return this.wikiItem;
	}

	/**
	 * This method initializes createCurrImgInfoMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createCurrImgInfoMenuItem() {
		this.currImgInfoMenuItem.setText(I18N
				.getGUILabelText("menu.info.currImg.info.text"));
		this.currImgInfoMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.info.currImg.info.ttp"));
		this.currImgInfoMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.info.currImage.info")));
		this.currImgInfoMenuItem.addActionListener(this);
		this.currImgInfoMenuItem.setActionCommand("currimginfo");
		this.currImgInfoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_I, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		return this.currImgInfoMenuItem;
	}

	/**
	 * This method initializes createCurrImgHeaderMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createCurrImgHeaderMenuItem() {
		this.currImgHeaderMenuItem.setText(I18N
				.getGUILabelText("menu.info.currImg.header.text"));
		this.currImgHeaderMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.info.currImg.header.ttp"));
		this.currImgHeaderMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.info.currImage.header")));
		this.currImgHeaderMenuItem.addActionListener(this);
		this.currImgHeaderMenuItem.setActionCommand("currimgheader");
		if (OperatingSystem.isMac()) {
			this.currImgHeaderMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_H, Event.CTRL_MASK
							+ Toolkit.getDefaultToolkit()
									.getMenuShortcutKeyMask()));
		} else {
			this.currImgHeaderMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_H, Toolkit.getDefaultToolkit()
							.getMenuShortcutKeyMask()));
		}
		return this.currImgHeaderMenuItem;
	}

	/**
	 * This method initializes createCurrImgDirMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createCurrImgDirMenuItem() {
		this.currImgDirMenuItem.setText(I18N
				.getGUILabelText("menu.info.currImgPath.text"));
		this.currImgDirMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.info.currImgPath.ttp"));
		this.currImgDirMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.info.currImgPath")));
		this.currImgDirMenuItem.addActionListener(this);
		this.currImgDirMenuItem.setActionCommand("currimgdir");
		return this.currImgDirMenuItem;
	}

	/**
	 * This method initializes createMemInfoMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createMemInfoMenuItem() {
		this.memInfoMenuItem.setText(I18N
				.getGUILabelText("menu.info.memInfo.text"));
		this.memInfoMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.info.memInfo.ttp"));
		this.memInfoMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.info.memInfo")));
		this.memInfoMenuItem.addActionListener(this);
		this.memInfoMenuItem.setActionCommand("meminfo");
		return this.memInfoMenuItem;
	}

	/**
	 * This method initializes createSystemInfoMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSystemInfoMenuItem() {
		this.systemInfoMenuItem.setText(I18N
				.getGUILabelText("menu.info.sysInfo.text"));
		this.systemInfoMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.info.sysInfo.ttp"));
		this.systemInfoMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.info.sysInfo")));
		this.systemInfoMenuItem.addActionListener(this);
		this.systemInfoMenuItem.setActionCommand("systeminfo");
		return this.systemInfoMenuItem;
	}

	/**
	 * This method initializes createThreadInfoMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createThreadInfoMenuItem() {
		this.threadInfoMenuItem.setText(I18N
				.getGUILabelText("menu.info.threadInfo.text"));
		this.threadInfoMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.info.threadInfo.ttp"));
		this.threadInfoMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.info.threadInfo")));
		this.threadInfoMenuItem.addActionListener(this);
		this.threadInfoMenuItem.setActionCommand("threadinfo");
		return this.threadInfoMenuItem;
	}

	/**
	 * This method initializes createTileInfoMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createTileInfoMenuItem() {
		this.tileInfoMenuItem.setText(I18N
				.getGUILabelText("menu.info.tileInfo.text"));
		this.tileInfoMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.info.tileInfo.ttp"));
		this.tileInfoMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.info.tileInfo")));
		this.tileInfoMenuItem.addActionListener(this);
		this.tileInfoMenuItem.setActionCommand("tileinfo");

		return this.tileInfoMenuItem;
	}

	/**
	 * This method initializes createBoardPanelMenuItem
	 * 
	 * @return a {@link JMenuItem}
	 */
	private JMenuItem createBoardPanelMenuItem() {
		this.boardPanelMenuItem.setText(I18N
				.getGUILabelText("menu.info.logWindow.text"));
		this.boardPanelMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.info.logWindow.ttp"));
		this.boardPanelMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.info.logWindow")));
		this.boardPanelMenuItem.addActionListener(this);
		this.boardPanelMenuItem.setActionCommand("boardpanel");
		this.boardPanelMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F2, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		return this.boardPanelMenuItem;
	}

	/**
	 * Create a menu item for refreshing the operator registry.
	 * 
	 * @return a {@link JMenuItem}
	 */
	private JMenuItem createRefreshOperatorRegistryMenuItem() {
		this.refreshOperatorRegistry.setText(I18N
				.getGUILabelText("menu.info.refreshOperatorRegistry.text"));
		this.refreshOperatorRegistry.setToolTipText(I18N
				.getGUILabelText("menu.info.refreshOperatorRegistry.ttp"));
		this.refreshOperatorRegistry.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.info.refreshOperatorRegistry")));
		this.refreshOperatorRegistry.addActionListener(this);
		this.refreshOperatorRegistry
				.setActionCommand("refreshoperatorregistry");
		return this.refreshOperatorRegistry;
	}

	/**
	 * This method initializes the "About IQM..." menu item.
	 * 
	 * @return a {@link JMenuItem}
	 */
	private JMenuItem createAboutMenuItem() {
		this.aboutMenuItem
				.setText(I18N.getGUILabelText("menu.info.about.text"));
		this.aboutMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.info.about.ttp"));
		this.aboutMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.info.about")));
		this.aboutMenuItem.addActionListener(this);
		this.aboutMenuItem.setActionCommand("about");
		this.aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F12, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		return this.aboutMenuItem;
	}

	/**
	 * This method sets and performs the corresponding actions to the menu
	 * items.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		logger.debug(e.getActionCommand());

		if ("currimginfo".equals(e.getActionCommand())) {
			// display current image information
			TextPanel tp = new TextPanel();
			TextDisplayFrame tdf = new TextDisplayFrame(tp);
			String info = new ImageInfoExtractor().getImageInfo(Look
					.getInstance().getCurrentImage());
			if (info != null) {
				tdf.setTitle(I18N.getGUILabelText(
						"textDisplay.frame.imageInfo.single.title",
						Look.getInstance().getCurrentImage()
								.getProperty("file_name")));
				tp.writeText(info);
				tdf.setVisible(true);
			} else {
				int selection = DialogUtil
						.getInstance()
						.showDefaultQuestionMessage(
								I18N.getMessage("application.dialog.noImageOpen.question"));
				if (selection == IDialogUtil.YES_OPTION) {
					OpenImageDialog dlg = new OpenImageDialog();
					File[] files = dlg.showDialog();
					
					if (files == null || files.length < 1){
						return;
					}
					
					Tank.getInstance().loadImagesFromHD(files);
				}
			}
		}
		if ("currimgheader".equals(e.getActionCommand())) {
			TextPanel tp = new TextPanel();
			TextDisplayFrame tdf = new TextDisplayFrame(tp);
			String header = new ImageHeaderExtractor().getImageHeader(Look
					.getInstance().getCurrentImage());
			if (header != null) {
				tdf.setTitle(I18N.getGUILabelText(
						"textDisplay.frame.imageHeader.single.title",
						Look.getInstance().getCurrentImage()
								.getProperty("file_name")));
				tp.writeText(header);
				tdf.setVisible(true);
			} else {
				int selection = DialogUtil
						.getInstance()
						.showDefaultQuestionMessage(
								I18N.getMessage("application.dialog.noImageOpen.question"));
				if (selection == IDialogUtil.YES_OPTION) {
					OpenImageDialog dlg = new OpenImageDialog();
					File[] files = dlg.showDialog();
					
					if (files == null || files.length < 1){
						return;
					}
					
					Tank.getInstance().loadImagesFromHD(files);
				}
			}

		}
		if ("currimgdir".equals(e.getActionCommand())) {
			// display current image directory
			DialogUtil.getInstance().showDefaultInfoMessage(
					"The current image path is \n ["
							+ ConfigManager.getCurrentInstance().getImagePath()
							+ "]");
		}
		if ("meminfo".equals(e.getActionCommand())) {
			long maxMem = Runtime.getRuntime().maxMemory();
			long totMem = Runtime.getRuntime().totalMemory();
			long freeMem = Runtime.getRuntime().freeMemory();
			maxMem = maxMem / 1024 / 1024;
			totMem = totMem / 1024 / 1024;
			freeMem = freeMem / 1024 / 1024;

			StringBuffer sb = new StringBuffer();
			sb.append("Current Memory Information" + "\n");
			sb.append("  - " + String.format("%-14s", "Max Memory: ")
					+ String.format("%7s", maxMem) + "M" + "\n");
			sb.append("  - " + String.format("%-14s", "Total Memory: ")
					+ String.format("%7s", totMem) + "M" + "\n");
			sb.append("  - " + String.format("%-14s", "Used Memory: ")
					+ String.format("%7s", (totMem - freeMem)) + "M" + "\n");
			sb.append("  - " + String.format("%-14s", "Free Memory: ")
					+ String.format("%7s", freeMem) + "M" + "\n");

			DialogUtil.getInstance().showDefaultInfoMessage(new String(sb));
		}
		if ("systeminfo".equals(e.getActionCommand())) {
			try {
				TextPanel tp = new TextPanel();
				TextDisplayFrame tdf = new TextDisplayFrame(tp);
				tdf.setTitle(I18N
						.getGUILabelText("textDisplay.frame.systemInfo.title"));
				StringBuffer sb = new StringBuffer();

				Properties p = System.getProperties();
				Enumeration<Object> keys = p.keys();
				ArrayList<String> propertyArray = new ArrayList<String>();

				// for sorting, store elements in a sortable list
				try {
					while (keys.hasMoreElements()) {
						propertyArray.add(keys.nextElement().toString());
					}
				} catch (NoSuchElementException nsee) {
					logger.error(nsee);
				}

				// sort the array
				Collections.sort(propertyArray, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});

				sb.append("System Properties\n-----------------\n");
				// print out system properties
				for (String key : propertyArray) {
					String val = p.getProperty(key);
					if (val.equals("\n")) {
						val = "\\n";
					} else if (val.equals("\r\n")) {
						val = "\\r\\n";
					}
					sb.append(String.format("%-45s", key + ": ") + val + "\n");
				}

				// //////////////////////////////// ENVIRONMENT VARIABLES
				// read out environment variables
				Map<String, String> env = System.getenv();
				ArrayList<String> envArray = new ArrayList<String>();

				// fill the array for sorting
				for (String envName : env.keySet()) {
					envArray.add(envName);
				}

				// Sort the array
				Collections.sort(envArray, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});

				sb.append("\n\nEnvironment Variables\n---------------------\n");
				// print out the environment variables
				for (String str : envArray) {
					sb.append(String.format("%-45s", str + ": ") + env.get(str)
							+ "\n");
				}

				tp.writeText(new String(sb));
				tdf.setVisible(true);

			} catch (SecurityException se) {
				logger.error(se);
			} catch (NullPointerException npe) {
				logger.error(npe);
			}

		}
		if ("threadinfo".equals(e.getActionCommand())) {
			// read all active threads
			StringBuffer sb = new StringBuffer();
			Thread[] threads = new Thread[Thread.activeCount()];
			Thread.enumerate(threads);
			sb.append("Active threads: " + Thread.activeCount() + "\n");
			// run through all threads
			for (Thread current : threads) {
				sb.append(" - "
						+ (current.isAlive() ? current.getName()
								+ " is alive.\n" : ""));
			}
			DialogUtil.getInstance().showDefaultInfoMessage(new String(sb));
		}
		if ("tileinfo".equals(e.getActionCommand())) {
			// constructor sets this class visible
			// check whether the tool is already open or not
			if (GUITools.getTcTool() == null) {
				TCTool t = new TCTool();
				GUITools.setTcTool(t);
				t.setVisible(true);
			} else {
				// bring it to front
				GUITools.getTcTool().toFront();
			}

		}
		if ("boardpanel".equals(e.getActionCommand())) {
			// check whether the board panel is open or not
			if (GUITools.getLogFrame() == null) {
				LogFrame l = new LogFrame();
				GUITools.setLogFrame(l);
				l.setVisible(true);
			} else {
				// bring it to front
				GUITools.getLogFrame().toFront();
			}
		}
		if ("about".equals(e.getActionCommand())) {
			GUITools.showAboutDialog();
		}
		if ("refreshoperatorregistry".equals(e.getActionCommand())) {
			try {
				OperatorRegistryHelper.updateRegistry();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if ("iqmwiki".equals(e.getActionCommand())) {
			if (Desktop.isDesktopSupported()) {
				Desktop d = Desktop.getDesktop();
				try {
					d.browse(new URI("https://sf.net/p/iqm/wiki/Home/"));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		}
		if ("iqmweb".equals(e.getActionCommand())) {
			if (Desktop.isDesktopSupported()) {
				Desktop d = Desktop.getDesktop();
				try {
					d.browse(new URI("http://iqm.sourceforge.net/"));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		}
		if ("issuetracker".equals(e.getActionCommand())) {
			if (Desktop.isDesktopSupported()) {
				Desktop d = Desktop.getDesktop();
				try {
					d.browse(new URI("https://sf.net/p/iqm/tickets"));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
