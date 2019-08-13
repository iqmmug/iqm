package at.mug.iqm.api.persistence;

/*
 * #%L
 * Project: IQM - API
 * File: VirtualDataManager.java
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

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.media.jai.JAI;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.config.ConfigManager;

/**
 * The {@link VirtualDataManager} is responsible for holding references to the
 * serialized files where the content is stored.
 * <p>
 * Instances can query this class in order to obtain the location of an
 * {@link IqmDataBox} on the disk, when the application is in virtual state.
 * <p>
 * This is the central instance when dealing with virtual items. This class uses
 * an instance of {@link Serializer} to perform its I/O tasks.
 * 
 * @author Philipp Kainz
 * @since IQM 3.0
 */
public class VirtualDataManager {

	/**
	 * Custom class logger.
	 */
	private static final Logger logger = Logger
			.getLogger(VirtualDataManager.class);

	/**
	 * A serializer for I/O tasks of the manager.
	 */
	private Serializer serializer = null;

	/**
	 * A cache of all serialized items.
	 */
	private HashMap<UUID, File> boxCache = new HashMap<UUID, File>();

	/**
	 * Default constructor.
	 */
	private VirtualDataManager() {
		this.serializer = new Serializer();
		Application.setVirtualDataManager(this);
	}

	/**
	 * @return the serializer
	 */
	public Serializer getSerializer() {
		return serializer;
	}

	/**
	 * @return the current instance
	 */
	public static VirtualDataManager getInstance() {
		VirtualDataManager vdm = Application.getVirtualDataManager();
		if (vdm == null) {
			vdm = new VirtualDataManager();
		}
		return vdm;
	}

	/**
	 * This method creates a new sub-directory (name) within the temporary
	 * directory specified by {@link ConfigManager#getTempPath()} of the current
	 * {@link ConfigManager} instance. <br/>
	 * One virtual directory is tied to a specific stack of items in the Tank,
	 * holding the serialized objects.
	 * <p>
	 * <b>Note</b> that the directory will <b>not yet be created on the hard
	 * drive</b>!
	 * 
	 * @return {@link File} the location of the sub-directory
	 */
	public synchronized static File getNewVirtualDirectory() {
		// get the temporary path
		File userTempDir = ConfigManager.getCurrentInstance().getTempPath();

		// this will be the directory to be created within 'userTempDir'
		File newVirtDir = null;

		if (userTempDir.isDirectory()) {
			// fetch all directory and file names in an array

			FileFilter fileFilter = new FileFilter() {
				public boolean accept(File file) {
					return file.isDirectory();
				}
			};
			File[] files = userTempDir.listFiles(fileFilter);

			int n = 0; // Directory numbers
			boolean found = true;

			while (found == true) {

				found = false;

				// scan the file array for directories starting with
				// "Stack_xxxxx",
				// where xxxxx may be 00005 for n=5
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						if (files[i].toString().contains(
								"Stack_" + String.format("%05d", n))) {
							// if the file name is a directory and named by
							// convention,
							// set found to true and do this for all files.
							// if the number n is not found, found stays false
							found = true;
							logger.trace("Found item stack: "
									+ files[i].getAbsolutePath());
						}
					}
				}

				// increase n, the numbering of the directories
				n = n + 1;
			}

			// if the number n has not been found, decrease n again
			// (for a single new directory this means to start from 0)
			n = n - 1;

			// create just the new directory name in /IQM/temp/
			newVirtDir = new File(userTempDir.toString()
					+ IQMConstants.FILE_SEPARATOR + "Stack_"
					+ String.format("%05d", n));
			logger.debug("Returning the name of a new (subsequent) virtual sub-directory: ["
					+ newVirtDir + "]");
		}

		// return the File object, actual creation is not part of this method
		return newVirtDir;
	}

	/**
	 * This method gets a new file name with number <code>n</code>.
	 * <p>
	 * <b>Note</b> that the new file will <b>not yet be created on the hard
	 * drive</b>!
	 * 
	 * @param dir
	 *            - the directory
	 * @param n
	 *            - the number to be searched for
	 * @return a new {@link File}, containing the name for a
	 *         {@link IVirtualizable} object in a directory
	 */
	public synchronized static File getNewVirtualFileName(File dir, int n) {
		File newSlice = null;
		if (dir.isDirectory()) {
			newSlice = new File(dir.toString() + IQMConstants.FILE_SEPARATOR
					+ "Item_" + String.format("%05d", n) + "."
					+ IQMConstants.SERIALIZATION_EXTENSION);
		}
		return newSlice;
	}

	/**
	 * This method creates a new temporary file name within the specified
	 * directory. A sequential number for individual file naming is
	 * automatically created.
	 * <p>
	 * <b>Note</b> that the file will <b>not yet be created on the hard
	 * drive</b>!
	 * 
	 * @param dir
	 *            some directory for the virtual file
	 * @return a new temporary file name in the specified directory
	 */
	public synchronized static File getRandomVirtualFileName(File dir) {

		File newSlice = null;
		
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}

		newSlice = new File(dir.toString() + IQMConstants.FILE_SEPARATOR
				+ "Item_" + UUID.randomUUID() + "."
				+ IQMConstants.SERIALIZATION_EXTENSION);
		
		// return the File object, actual storing is not part of this method
		return newSlice;
	}

	/**
	 * Save a {@link IVirtualizable} object to its specified target and puts the
	 * file location to the cache.
	 * 
	 * @param target
	 *            the target to be saved, e.g. an {@link IqmDataBox}
	 * @param directory
	 *            the directory, where the target is being saved (<b>must
	 *            already exist!</b>)
	 * @return a reference to the virtual item ({@link VirtualDataBox}) if
	 *         successful, or <code>null</code> otherwise
	 * @throws IllegalArgumentException
	 *             if one of the parameters is null or the directory does not
	 *             exist
	 */
	public synchronized IVirtualizable save(IqmDataBox target, File directory) {
		if (target == null || directory == null || !directory.exists()) {
			throw new IllegalArgumentException(
					"The IqmDataBox to be saved is null, or the directory is not specified.");
		}

		// display the IO processing icon
		Application.getMainFrame().getStatusPanel().showIOProcessingIcon();
		try {
			// get a new item name in the directory
			File location = getRandomVirtualFileName(directory);

			// store the target (write byte data)
			this.getSerializer().serialize(target, location);

			// construct the virtual data box
			VirtualDataBox vBox = new VirtualDataBox(location);
			vBox.setDataType(target.getDataType());
			vBox.setProperties(target.getProperties());

			// copy the thumb nails to the virtual box
			try {
				vBox.setManagerThumbnail(target.getManagerThumbnail().clone());
			} catch (NullPointerException ex) {
				vBox.setManagerThumbnail(null);
			}
			try {
				vBox.setTankThumbnail(target.getTankThumbnail().clone());
			} catch (NullPointerException ex) {
				vBox.setTankThumbnail(null);
			}
			try {
				vBox.setThumbnail(target.getThumbnail().clone());
			} catch (NullPointerException ex) {
				vBox.setThumbnail(null);
			}

			// put the file location to the cache
			this.boxCache.put(vBox.getID(), location);

			target = null;

			return vBox;
		} catch (FileNotFoundException e) {
			logger.error("An error occurred: ", e);
		} catch (IOException e) {
			logger.error("An error occurred: ", e);
		} finally {
			// hide the io processing icon
			Application.getMainFrame().getStatusPanel().hideIOProcessingIcon();
		}
		return null;

	}

	/**
	 * Load a data item from the serialized state on the hard disk to the
	 * memory.
	 * 
	 * @param target
	 *            the target to be loaded
	 * @return the {@link IqmDataBox} containing the data models, or
	 *         <code>null</code>, if no item is associated with the target
	 * @throws IllegalArgumentException
	 *             if the parameter is null
	 */
	public IqmDataBox load(IVirtualizable target) {
		if (target == null)
			throw new IllegalArgumentException(
					"The target to be loaded must not be null.");

		// display the IO processing icon
		Application.getMainFrame().getStatusPanel().showIOProcessingIcon();

		IqmDataBox box = null;
		try {
			// lookup the target in the hashmap
			File location = this.boxCache.get(target.getID());

			if (location == null) {
				return null;
			}

			// if the target is acquired, load it and return it
			box = (IqmDataBox) this.getSerializer().deserialize(location);
			// create thumbnails on demand, if you clone the deserialized box!

		} catch (FileNotFoundException e) {
			logger.error("An error occurred: ", e);
		} catch (IOException e) {
			logger.error("An error occurred: ", e);
		} catch (ClassNotFoundException e) {
			logger.error("An error occurred: ", e);
		} finally {
			// hide the io processing icon
			Application.getMainFrame().getStatusPanel().hideIOProcessingIcon();
		}

		return box;
	}

	/**
	 * Deletes the virtual object from the disk and removes it from the box
	 * cache.
	 * 
	 * @param target
	 *            the object to be deleted
	 * @throws IllegalArgumentException
	 *             if one of the parameters is null
	 */
	public synchronized void delete(IVirtualizable target) {
		if (target == null)
			throw new IllegalArgumentException(
					"The target to be deleted must not be null.");

		// display the IO processing icon
		Application.getMainFrame().getStatusPanel().showIOProcessingIcon();
		try {
			logger.debug("Removing target ID [" + target.getID()
					+ "] from the box cache.");
			this.boxCache.remove(target.getID());
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
		} finally {
			// hide the io processing icon
			Application.getMainFrame().getStatusPanel().hideIOProcessingIcon();
		}
	}

	/**
	 * This method clears the entire virtual box cache.
	 * <p>
	 * <b>CAUTION:</b> Any objects associated with keys in the cache cannot be
	 * located afterwards!
	 */
	public void clearCache() {
		this.boxCache.clear();
	}

	/**
	 * Saves an entire list of {@link IqmDataBox}es to the temporary directory.
	 * 
	 * @param list
	 *            the list of boxes to be saved
	 * @param directory
	 *            the directory, where the target is being saved (<b>must
	 *            already exist!</b>)
	 * @return a reference to the virtual item list if successful, or
	 *         <code>null</code> otherwise
	 * @throws IllegalArgumentException
	 *             if one of the parameters is null, the list is empty or the
	 *             directory does not exist
	 */
	public List<IqmDataBox> save(List<IqmDataBox> list, File directory) {
		if (list == null || list.isEmpty() || directory == null
				|| !directory.exists()) {
			throw new IllegalArgumentException(
					"The list is null, empty or the virtual directory is not specified.");
		}
		try {
			List<IqmDataBox> vList = new ArrayList<IqmDataBox>(list.size());
			for (int i = 0; i < list.size(); i++) {
				VirtualDataBox vBox = (VirtualDataBox) save(list.get(i),
						directory);
				vList.add(i, vBox);
			}
			return vList;
		} catch (Exception e) {
			logger.error(
					"An error occurred when saving the list of IqmDataBoxes: ",
					e);
			return null;
		}
	}

	public static void main(String[] args) throws IOException {
		IqmDataBox box = new IqmDataBox(JAI.create("fileload", "/Users/phil/test.tif"));
		box.getImageModel().setModelName("preName");
		
		File vDir = new File("/Users/phil/IQM/temp/");
		VirtualDataManager vdm = getInstance();
		VirtualDataBox vBox = (VirtualDataBox) vdm.save(box, vDir);
		
		IqmDataBox newBox = vdm.load(vBox);
		System.out.println("ModelName: " + newBox.getImageModel().getModelName());
	}

}
