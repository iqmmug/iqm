package at.mug.iqm.api.gui.util;

/*
 * #%L
 * Project: IQM - API
 * File: MemoryMonitor.java
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


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.IMainFrame;

/**
 * This class performs the update of the memory label in the {@link IMainFrame}.
 * 
 * @author Philipp Kainz
 */
public class MemoryMonitor implements Runnable {

	// class specific logger
	private static final Logger logger = LogManager.getLogger(MemoryMonitor.class);

	/**
	 * The sleep sleepDelay for this thread in Milliseconds. Default is 5.000
	 * ms.
	 */
	private long sleepDelay = 5000L;
	
	/**
	 * A flag whether or not the thread should be stopped.
	 */
	private boolean isStopped;

	/**
	 * This is the standard constructor. It creates a new instance using the
	 * default value of 5.000 milliseconds for the sleep delay.
	 */
	public MemoryMonitor() {
		new MemoryMonitor(this.sleepDelay);
	}

	/**
	 * Create a new CleanerTask with a specified sleepDelay.
	 * 
	 * @param sleepDelay
	 *            - in Milliseconds
	 */
	public MemoryMonitor(final long sleepDelay) {
		this.sleepDelay = sleepDelay;
	}

	/**
	 * Starts the memory monitor thread
	 */
	@Override
	public void run() {
		logger.debug("Memory Monitor thread is being executed.");

		while (!isStopped) {
			try {
				Thread.sleep(this.sleepDelay);
				double freeMem = Runtime.getRuntime().freeMemory();
				double totMem = Runtime.getRuntime().totalMemory();
				double maxMem = Runtime.getRuntime().maxMemory();

				freeMem = freeMem / 1024 / 1024;
				totMem = totMem / 1024 / 1024;
				maxMem = maxMem / 1024 / 1024;

				double usedMem = totMem - freeMem;
				double usedMemPercent = Math.ceil((usedMem / maxMem) * 100);

				logger.trace("Updating memory information: " + "Memory: "
						+ (int) usedMem + "M (" + (int) usedMemPercent
						+ "%) used / " + (int) totMem + "M tot / "
						+ (int) maxMem + "M max ");
				((IMainFrame) Application.getMainFrame())
						.getMainPanel()
						.getMemoryLabel()
						.setText(
								"Memory: " + (int) usedMem + "M ("
										+ (int) usedMemPercent + "%) used / "
										+ (int) totMem + "M tot / "
										+ (int) maxMem + "M max ");

			} catch (InterruptedException e) {
				// log the message
				logger.error("Thread has been interrupted. ", e);
			}
		}
	}

	/**
	 * @return the sleepDelay
	 */
	public long getSleepDelay() {
		return sleepDelay;
	}

	/**
	 * @param sleepDelay
	 *            the sleepDelay to set
	 */
	public void setSleepDelay(long sleepDelay) {
		this.sleepDelay = sleepDelay;
	}
	
	public boolean isStopped() {
		return isStopped;
	}
	
	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}
	
	public void stop(){
		this.setStopped(true);
	}
}
