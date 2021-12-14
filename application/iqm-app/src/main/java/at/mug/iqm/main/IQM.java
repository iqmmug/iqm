package at.mug.iqm.main;

/*
 * #%L
 * Project: IQM - Application
 * File: IQM.java
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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

 
 

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.commons.util.CompletionWaiter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.OperatingSystem;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.core.util.CoreTools;
import at.mug.iqm.gui.dialog.WelcomeDialog;
import at.mug.iqm.gui.util.GUITools;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

/**
 * This is the main class for IQM representing its entry point.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 */
public class IQM {

	// defining a static logger for THIS CLASS
	// this line must occur in every class which shall log to file and console
	// Logging variables
	  

	private static File f;
	private static FileChannel channel;
	private static FileLock lock;

	/**
	 * Constructs a new instance of the IQM application.
	 * 
	 * @param ignored
	 * @throws Exception
	 */
	public IQM(boolean ignored) throws Exception {
		this.checkAlreadyStarted();

		// display a splash screen while the GUI is being constructed
		ApplicationStarter appStarter = new ApplicationStarter(
				IQMConstants.APPLICATION_VERSION, true);
		WelcomeDialog dlg = new WelcomeDialog();
		appStarter.addPropertyChangeListener(new CompletionWaiter(dlg));
		try {
			appStarter.setWelcomeDialog(dlg); // for updates
			appStarter.execute();
			dlg.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method checks, whether another instance of IQM is already running.
	 * 
	 * @throws RuntimeException
	 */
	@SuppressWarnings("resource")
	protected void checkAlreadyStarted() throws RuntimeException {
		try {
			f = new File(System.getProperty("java.io.tmpdir") + File.separator
					+ "iqm.lock");
			// Check if the lock exist
			if (f.exists()) {
				// if exist try to delete it
				f.delete();
			}
			// Try to get the lock
			channel = new RandomAccessFile(f, "rw").getChannel();
			lock = channel.tryLock();
			if (lock == null) {
				// File is lock by other application
				channel.close();
				throw new RuntimeException("Only 1 instance of IQM is allowed.");
			}
			// Add shutdown hook to release lock when application shutdown
			ShutdownHook shutdownHook = new ShutdownHook();
			Runtime.getRuntime().addShutdownHook(shutdownHook);
		} catch (IOException e) {
			throw new RuntimeException("Could not start process.", e);
		}

	}

	/**
	 * Unlock the file, enabling another IQM instance to start up.
	 */
	private static void unlockFile() {
		// release and delete file lock
		try {
			if (lock != null) {
				lock.release();
				channel.close();
				System.out.println(Thread.currentThread().getName()
						+ ": Deleting shutdown hook.");
				boolean success = f.delete();
				System.out.println("Deleting shutdown hook was "
						+ (success ? "successful." : "not successful."));
			}
		} catch (IOException e) {
			System.out.println("IQM Error: An error occurred: " + e);
		}
	}

	/**
	 * Custom {@link ShutdownHook} for IQM, which releases the lock on the file
	 * after the shutdown has been initiated.
	 * 
	 * @author Philipp Kainz
	 * 
	 */
	private static class ShutdownHook extends Thread {
		@Override
		public void run() {
			unlockFile();
			Runtime.getRuntime().halt(0);
		}
	}

	/**
	 * Apply the system's look-and-feel setting before initializing the
	 * configuration from {@link ConfigManager}.
	 */
	protected static void applyDefaultLookAndFeel() {
		// assign the look and feel to the current swing UI manager
		try {
			// for all messages initially set the default look and feel from the
			// system
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
	 * This is the entry point for the extended IQM application.
	 * 
	 * @param args
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws IOException,
			URISyntaxException {
		System.setProperty("iqmrootdir", new File(IQM.class
				.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParentFile().getCanonicalPath());
		System.out.println("IQM:  Hey there, IQM core is starting up, scheduling a new GUI creation task in the EventDispatcherThread...");

		// add initial properties for macintosh environments
		if (OperatingSystem.isMac()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name", "IQM");
			System.setProperty("apple.awt.fileDialogForDirectories", "true");

			Application macOSXApplication = Application.getApplication();
			macOSXApplication.setAboutHandler(new AboutHandler() {

				@Override
				public void handleAbout(AboutEvent arg0) {
					GUITools.showAboutDialog();
				}
			});
			macOSXApplication.setQuitHandler(new QuitHandler() {
				@Override
				public void handleQuitRequestWith(QuitEvent evt,
						QuitResponse resp) {
					resp.cancelQuit();
					
					// close IQM
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							try {
								GUITools.getMainFrame().windowClosing(null);
							} catch (Exception e1) {
								// log the error message
								System.out.println("IQM Error: " + e1);
								System.out.println("IQM Info: Exit code: -1.");
								System.exit(-1);
							}
						}
					});
				}
			});
		}

		Runnable application = new Runnable() {
			@Override
			public void run() {
				try {
					// assign the look and feel to the current swing UI manager
					applyDefaultLookAndFeel();
					checkJavaVersion();
					Thread.currentThread().setName("IQM-AWT-Event-Queue");
					new IQM(true);
				} catch (Exception e) {
					System.out.println("IQM Error: "+ 
							"Oops, We're sorry, but something went wrong. Informing user and shutting down."+ e);
					DialogUtil.getInstance().showErrorMessage(
							I18N.getMessage("application.exception.fatalError",
									System.getProperty("user.home")
											+ IQMConstants.FILE_SEPARATOR
											+ "IQM"
											+ IQMConstants.FILE_SEPARATOR
											+ "logs"
											+ IQMConstants.FILE_SEPARATOR
											+ "iqm.log"), e);
					System.exit(-1);
				}

			}
		};

		// invokeLater constructs a separate thread for assembling the gui
		// and runs it asynchronously
		SwingUtilities.invokeLater(application);

		System.out.println("IQM:  Scheduling done, main thread is exiting, now everything runs in the EventDispatcherThread. Have fun!");
	}

	protected static void checkJavaVersion() {
		// check JAVA version
		// read out environment variables System
		String javaVersionMin = "10.0.2";
		String javaVersionInUse = System.getProperty("java.version");
		if (javaVersionInUse.replace("_", "").replace(".", "")
				.compareTo(javaVersionMin.replace("_", "").replace(".", "")) < 0) {
			DialogUtil.getInstance().showDefaultErrorMessage(
					I18N.getMessage("application.java.version.inUse",
							javaVersionInUse)
							+ "\n "
							+ I18N.getMessage(
									"application.java.version.required",
									javaVersionMin));
			System.exit(-1);
		} else {
			CoreTools.setJavaVersionMin(javaVersionMin);
		}
	}
}
