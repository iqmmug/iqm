package at.mug.iqm.core.util;

/*
 * #%L
 * Project: IQM - Application Core
 * File: DeleteFilesTask.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.core.workflow.Manager;


/**
 * This class is responsible for deleting files in a specified directory. The
 * directory can be either set using the constructor or the setter for the
 * variable.
 * 
 * @author Philipp Kainz
 */
public class DeleteFilesTask extends SwingWorker<Boolean, Void> {

	// Logging variables
	private static final Logger logger = LogManager.getLogger(DeleteFilesTask.class);

	private String userDirName;

	/**
	 * This is the empty standard constructor. The directory to be deleted has
	 * to be set either via setter method, or one calls explicitly the
	 * {@link DeleteFilesTask#deleteTemporaryUserData(File)} method.
	 */
	public DeleteFilesTask() {
	}

	/**
	 * This is the constructor, one can implicitly define the directory to be
	 * deleted.
	 * 
	 * @param userDirName
	 */
	public DeleteFilesTask(String userDirName) {
		this.setUserDirName(userDirName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Boolean doInBackground() throws InterruptedException {
		Thread.currentThread().setName("FileDeleter");
		
		logger.debug("Resetting GUI elements and releasing locks for temporary files...");
		// release all locks for the files
		Manager.getInstance().resetLeftModel(); // for an empty left manager
												// list, this also resets the
												// look panel
		Manager.getInstance().resetRightModel(); // for an empty right manager
													// list, this also resets
													// the look panel
		// run the garbage collector: MANDATORY FOR LOCK RELEASE!
		System.gc();

		File userDir = new File(this.userDirName);
		boolean success = false;
		// try 10 times, then exit
		int attempts = 0;
		while (success == false && attempts < 10) {
			logger.debug("Attempt [" + attempts
					+ " of 10] to delete the user data: [" + userDirName
					+ "]...");
			// count user files/directories in temp directory
			File[] fileCount = userDir.listFiles();
			int numFiles = fileCount.length;
			// if files/directories exist in the temp directory
			if (numFiles > 0) {
				this.deleteTemporaryUserData(userDir);
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
					// log the error message
					logger.error("An error occurred: ", e);
					e.printStackTrace();
				}
			} else {
				logger.info("Attempt [" + attempts
						+ " of 10] successfully deleted user data: ["
						+ userDirName + "].");
				success = true;
			}
			attempts++;
		}
		return success;
	}

	/**
	 * This method deletes all directories and files a specified directory. This
	 * method is used by {@link ConfigManager#validateAndSetConfiguration()} at
	 * application startup for cleaning the temporary directory.
	 * 
	 * @param userTempDir
	 *            - the name of the directory
	 */
	public void deleteTemporaryUserData(File userTempDir) {
		if (userTempDir.isDirectory()) {
			File[] files = userTempDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				// if there is a directory, step into it and delete recursively
				if (files[i].isDirectory()) {
					this.deleteDirectory(files[i]);
				}
				// delete top level files in the specified directory
				else if (files[i].isFile()) {
					try {
						boolean success = files[i].delete();
						if (success) {
							logger.trace("[" + files[i].getName()
									+ "] has been deleted successfully!");
						} else {
							logger.error("["
									+ files[i].getName()
									+ "] cannot be deleted! Maybe it is used by another process, "
									+ "we'll try again at next application startup.");
						}
					} catch (SecurityException se) {
						logger.error("Some temporary files could not be deleted! "
								+ "See log file for details. "
								+ "We'll try again at next application startup.");
					}

				}
			}
		}
	}

	/**
	 * This method deletes a directory all files and sub-directories by
	 * recursion. Returns <code>true</code> if all deletions are successful. If
	 * a deletion fails, the method stops to delete and returns
	 * <code>false</code>. This method is used by
	 * {@link #deleteTemporaryUserData(File)}.
	 * 
	 * @param dir
	 *            - the directory to be (recursively) deleted
	 */
	public boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				this.deleteDirectory(files[i]);
			}
		}

		boolean success = false;

		if (dir.exists()) {
			// Delete the file (at the end of a recursion)
			// and finally delete the last directory directory (top recursion)
			try {
				success = dir.delete();
				if (success) {
					logger.trace("[" + dir.getName()
							+ "] has been deleted successfully!");
				} else {
					logger.error("["
							+ dir.getName()
							+ "] cannot be deleted!  Maybe it is used by another process, "
							+ "we'll try again at next application startup.");
				}
			} catch (SecurityException se) {
				logger.error("Some temporary files could not be deleted! We'll try again at next application startup.");
			}
		} else {
			success = false;
		}
		return success;
	}

	/**
	 * @return the userDirName
	 */
	public String getUserDirName() {
		return userDirName;
	}

	/**
	 * @param userDirName
	 *            the userDirName to set
	 */
	public void setUserDirName(String userDirName) {
		this.userDirName = userDirName;
	}
}
