package at.mug.iqm.main;

/*
 * #%L
 * Project: IQM - Application
 * File: ApplicationStarter.java
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
import java.io.File;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.TileScheduler;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

 
 

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IApplicationStarter;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.util.MemoryMonitor;
import at.mug.iqm.api.plugin.IPluginService;
import at.mug.iqm.cleaner.CleanerTask;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.CursorFactory;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.OperatingSystem;
import at.mug.iqm.commons.util.PropertyManager;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.plugin.PluginServiceFactory;
import at.mug.iqm.core.processing.TaskFactory;
import at.mug.iqm.core.registry.OperatorRegistry;
import at.mug.iqm.core.registry.OperatorRegistryHelper;
import at.mug.iqm.core.registry.PluginRegistry;
import at.mug.iqm.core.util.ApplicationObserver;
import at.mug.iqm.core.util.CoreTools;
import at.mug.iqm.core.util.DeleteFilesTask;
import at.mug.iqm.core.workflow.Look;
import at.mug.iqm.core.workflow.Manager;
import at.mug.iqm.core.workflow.Plot;
import at.mug.iqm.core.workflow.Table;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.core.workflow.Text;
import at.mug.iqm.gui.MainFrame;
import at.mug.iqm.gui.dialog.WelcomeDialog;
import at.mug.iqm.gui.menu.PluginMenu;
import at.mug.iqm.gui.util.GUITools;

/**
 * The {@link ApplicationStarter} is responsible for initializing the
 * configuration, constructing the GUI elements and starting off various
 * background tasks, while the main thread is displaying the startup splash
 * screen.
 * 
 * @author Philipp Kainz
 * 
 */
public class ApplicationStarter extends SwingWorker<Void, Void> implements
		IApplicationStarter {

	  

	// define the partial title of the MainFrame
	// set this to the project number e.g. 3.1 in IQMConstants
	protected String versionNumber;

	protected ConfigManager configManager;

	protected WelcomeDialog welcomeDialog;

	/**
	 * The constructor.
	 * 
	 * @param versionNumber
	 *            - the running number of IQM
	 * @param isCore
	 *            - core = <code>true</code>, extended = <code>false</code>
	 */
	public ApplicationStarter(String versionNumber, boolean isCore) {
		Application.setApplicationStarter(this);

		this.setVersionNumber(versionNumber);
		CoreTools.setVersionNumber(this.getVersionNumber());
	}

	/**
	 * Constructs the GUI elements and initializes the configuration.
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 * @throws Exception
	 */
	@Override
	protected Void doInBackground() throws Exception {
		System.out.println("IQM:  Initializing application and constructing GUI elements...");

		// check for configuration files and its contents
		this.initConfiguration();

		// this.runWelcomeScreenThread();
		this.runCleanerTaskThread();

		// initialize the application
		this.initApplication();

		// ###################
		// CORE
		// initialize and show the main IQM frame
		this.updateDynamicWelcomeText("Constructing and initializing user interface...");
		MainFrame mainFrame = new MainFrame();

		// defaults = "core" version of IQM
		mainFrame.initializeDefaults();
		// END CORE
		// ###################

		// activate the initial menus: all items but the GENERATOR type are
		// deactivated at startup
		ApplicationObserver.setInitialMenuActivation();

		this.startMemoryMonitorThread();

		this.updateDynamicWelcomeText("Done, application is launching...");

		System.out.println("IQM:  Done.");
		return null;
	}

	protected void startMemoryMonitorThread() {
		Thread memoryMonitorThread = null;
		MemoryMonitor memoryMonitor = new MemoryMonitor();

		try {
			memoryMonitorThread = new Thread(memoryMonitor);
			memoryMonitorThread.setDaemon(true);
			// read from config file
			memoryMonitor.setSleepDelay(configManager.getIQMConfig()
					.getApplication().getMemoryMonitor().getInterval());
			memoryMonitorThread.setName(configManager.getIQMConfig()
					.getApplication().getMemoryMonitor().getName());
			memoryMonitorThread.start();

			// register the memory monitor with the application
			Application.setMemoryMonitor(memoryMonitorThread);
		} catch (SecurityException se) {
			// log the message
			System.out.println("IQM Error: Could not start thread memory monitor. "+ se);
		} catch (IllegalThreadStateException itse) {
			// log the message
			System.out.println("IQM Error: Thread " + memoryMonitorThread.getName()
					+ " has already been started. "+ itse);
		}
	}

	/**
	 * Run the garbage collector at the end of construction.
	 * 
	 * @see javax.swing.SwingWorker#done()
	 */
	@Override
	protected void done() {
		try {
			this.get();

			Runnable run = new Runnable() {

				@Override
				public void run() {
					try {
						MainFrame mf = GUITools.getMainFrame();
						
						// try to restore the window at the last location
						PropertyManager pm = PropertyManager.getManager(mf);
						Properties p = pm.read();
						// first run, write default values
						if (p.isEmpty()){
							Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
							int width = (int) dimScreen.getWidth() / 9 * 8;
							int height = (int) dimScreen.getHeight() / 9 * 8;
							mf.setLocation(width / 9 * 1 / 2, height / 9 * 1 / 3);
							mf.setPreferredSize(new Dimension(width, height));
							
							p.setProperty("loc_x", String.valueOf(mf.getLocation().x));
							p.setProperty("loc_y", String.valueOf(mf.getLocation().y));
							p.setProperty("width", String.valueOf(width));
							p.setProperty("height", String.valueOf(height));
							p.setProperty("extendedState", String.valueOf(JFrame.MAXIMIZED_BOTH));
							pm.write();
						}
						
						mf.setVisible(true);
						// start like last time
						mf.setLocation(new Point(Integer.valueOf(p.getProperty("loc_x")), Integer.valueOf(p.getProperty("loc_y")))); 
						mf.setSize(Integer.valueOf(p.getProperty("width")), Integer.valueOf(p.getProperty("height")));
						
						if (Integer.valueOf(p.getProperty("extendedState")) != JFrame.MAXIMIZED_BOTH){
							mf.setExtendedState(JFrame.NORMAL);
						}
					} catch (Exception e) {

					}

				}
			};
			// finally display (from EDT)
			SwingUtilities.invokeLater(run);

			System.gc();

		} catch (InterruptedException e1) {
			System.out.println("IQM Fatal: "+ e1);
			DialogUtil.getInstance().showErrorMessage(null, e1, true);
			System.exit(-1);
		} catch (ExecutionException e1) {
			System.out.println("IQM Fatal: "+ e1);
			DialogUtil.getInstance().showErrorMessage(null, e1, true);
			System.exit(-1);
		} catch (Exception e) {
			System.out.println("IQM Fatal: "+ e);
			DialogUtil.getInstance().showErrorMessage(null, e, true);
			System.exit(-1);
		}
	}

	/**
	 * This method initializes global application properties and sets globally
	 * available variables.
	 * 
	 * @param version
	 * @throws Exception
	 */
	protected void initApplication() throws Exception {
		this.updateDynamicWelcomeText("Initializing application subsystem...");

		// default (true) is that ImageIO caches images to disk when they are
		// read!
		ImageIO.setUseCache(false);

		// Use of MediaLib acceleration or not
		// JAI 1.1.3 native acceleration problems:
		// SubSampleAverage: sometimes black lines around tiles (Pyramid
		// Dimension)
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");

		// initialize all IQM user operators for JAI
		CommonTools.setDefaultToolTipDelays();

		// initialize the factories, registries and dynamic menus
		TaskFactory.getInstance();
		PluginRegistry.getInstance();
		OperatorRegistry.getInstance();
		PluginMenu.getInstance();
		// initialize the Groovy script engine

		// initialize the workflow control classes
		Tank.getInstance();
		Manager.getInstance();
		Look.getInstance();
		Plot.getInstance();
		Table.getInstance();
		Text.getInstance();

		// register other singletons
		DialogUtil.getInstance();

		// register all standard operators
		OperatorRegistryHelper.registerImageOperators();
		OperatorRegistryHelper.registerPlotOperators();

		// initialize the custom cursors
		CursorFactory.initializeCustomCursors();

		// register the plugins
		this.loadPlugins();

		// TileCache Memory, etc..
		this.setJAIDefaultOptions();
	}

	/**
	 * This method sets the default JAI Tile calculation properties
	 */
	protected void setJAIDefaultOptions() {

		this.updateDynamicWelcomeText("Setting default JAI options...");

		final long memCapacity = 1024L * 1024 * 1024; // 1024MByte
		// final long memCapacity = 0L; // 256MByte
		// final long tileCapacity = 1000L;
		JAI.getDefaultInstance().setTileCache(JAI.createTileCache(memCapacity));
		JAI.getDefaultInstance().setRenderingHint(
				JAI.KEY_CACHED_TILE_RECYCLING_ENABLED, Boolean.FALSE);
		TileScheduler ts = JAI.createTileScheduler();
		ts.setPriority(Thread.MAX_PRIORITY);
		// ts.setParallelism(2); //default 2
		JAI.getDefaultInstance().setTileScheduler(ts);

	}

	/**
	 * Check configuration before initial startup. If errors in parsing XML
	 * exist, program is not starting up.
	 */
	protected void initConfiguration() {
		this.updateDynamicWelcomeText("Initializing configuration...");

		// set default locale to English just for debug
		// standard dialogs are set in the system's default language
		Locale.setDefault(Locale.ENGLISH);

		// assign the look and feel to the current swing UI manager
		try {
			// for all messages initially set the default look and feel from the
			// system
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			// initialize the configuration manager
			try {
				this.configManager = ConfigManager.getCurrentInstance();

				this.configManager.validateAndSetConfiguration();

				// get the file path of the temporary directory
				File userTempDir = ConfigManager.getCurrentInstance()
						.getTempPath();

				// Delete all temporary files/dirs in user temp directory
				// therefore, use the public method from the deletion task,
				// without executing a new thread
				new DeleteFilesTask().deleteTemporaryUserData(userTempDir);

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("IQM Error: An error occurred: " + e);
				DialogUtil.getInstance().showErrorMessage(
						I18N.getMessage("application.exception.config.invalid",
								System.getProperty("user.home")
										+ IQMConstants.FILE_SEPARATOR + "IQM"
										+ IQMConstants.FILE_SEPARATOR + "logs"
										+ IQMConstants.FILE_SEPARATOR
										+ "iqm.log"), e, true);
				// exiting
				System.exit(-1);
			}

			// get the system's look and feel, if default or none is specified
			// in config file
			String laf = configManager.getIQMConfig().getApplication().getGui()
					.getLookAndFeel();

			if (OperatingSystem.isUnix() && !laf.toLowerCase().equals("system")) {
				UIManager
						.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			} else {
				if (laf != null
						&& (laf.toLowerCase().equals("default") || laf
								.toLowerCase().equals("system"))) {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} else if (laf != null
						&& (laf.toLowerCase().contains("nimbus"))) {
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} else if (laf == null) {
					UIManager.setLookAndFeel(UIManager
							.getCrossPlatformLookAndFeelClassName());
				} else {
					UIManager.setLookAndFeel(laf);
				}
			}
			this.updateDynamicWelcomeText("Done.");
		} catch (ClassNotFoundException e) {
			// log the error message
			DialogUtil.getInstance().showErrorMessage(null, e, true);
			System.out.println("IQM Error: An error occurred: " + e);
			e.printStackTrace();
			System.exit(-1);
		} catch (InstantiationException e) {
			// log the error message
			DialogUtil.getInstance().showErrorMessage(null, e, true);
			System.out.println("IQM Error: An error occurred: " + e);
			e.printStackTrace();
			System.exit(-1);
		} catch (IllegalAccessException e) {
			// log the error message
			DialogUtil.getInstance().showErrorMessage(null, e, true);
			System.out.println("IQM Error: An error occurred: " + e);
			e.printStackTrace();
			System.exit(-1);
		} catch (UnsupportedLookAndFeelException e) {
			// log the error message
			DialogUtil.getInstance().showErrorMessage(null, e, true);
			System.out.println("IQM Error: An error occurred: " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * This method starts the background thread for the customized garbage
	 * collector.
	 */
	protected void runCleanerTaskThread() {
		// start a new garbage collector thread every 10 seconds
		Thread cleanerTaskThread = null;
		CleanerTask cleanerTask = new CleanerTask();

		try {
			// get config from xml file for polling interval
			long interval = configManager.getIQMConfig()
					.getApplication().getCleanerTask().getInterval();
			
			System.out.println("IQM:  The cleaner task interval is set to " + interval);
			
			if (interval > 0){
				this.updateDynamicWelcomeText("Running cleaner task...");
				cleanerTask.setSleepDelay(interval);
	
				cleanerTaskThread = new Thread(cleanerTask);
				cleanerTaskThread.setDaemon(true);
				cleanerTaskThread.setName(configManager.getIQMConfig()
						.getApplication().getCleanerTask().getName());
				cleanerTaskThread.start();
	
				// register the instance with the application
				Application.setCleanerTask(cleanerTaskThread);
				this.updateDynamicWelcomeText("Done.");
			}
		} catch (SecurityException se) {
			// log the message
			se.printStackTrace();
			System.out.println("IQM Error: Could not start thread cleaner task. "+ se);
		} catch (IllegalThreadStateException itse) {
			// log the message
			itse.printStackTrace();
			System.out.println("IQM Error: Thread " + cleanerTaskThread.getName()
					+ " has already been started. "+ itse);
		} 
	}

	protected void loadPlugins() throws Exception {
		updateDynamicWelcomeText("Loading Plugins...");

		PluginServiceFactory.addPluginJarsToClasspath();

		IPluginService pluginService = PluginServiceFactory
				.createDefaultPluginService();
		pluginService.initPlugins();
	}

	/**
	 * Sets a text to the dynamic label in {@link WelcomeDialog}.
	 * 
	 * @param text
	 */
	@Override
	public void updateDynamicWelcomeText(String text) {
		this.welcomeDialog.updateDynamicText(text);
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return this.versionNumber;
	}

	/**
	 * @return the configManager
	 */
	public ConfigManager getConfigManager() {
		return configManager;
	}

	/**
	 * @param versionNumber
	 *            the versionNumber to set
	 */
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	/**
	 * @param configManager
	 *            the configManager to set
	 */
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public WelcomeDialog getWelcomeDialog() {
		return welcomeDialog;
	}

	public void setWelcomeDialog(WelcomeDialog welcomeDialog) {
		this.welcomeDialog = welcomeDialog;
	}
}
