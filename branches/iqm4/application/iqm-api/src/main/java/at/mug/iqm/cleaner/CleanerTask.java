package at.mug.iqm.cleaner;

/*
 * #%L
 * Project: IQM - API
 * File: CleanerTask.java
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


import org.apache.log4j.Logger;

/**
 * This task performs a <code>System.gc()</code> every given interval, so that 
 * the used memory is displayed approximately correctly.
 * @author Philipp Kainz
 *
 */
public class CleanerTask implements Runnable {

	// class specific logger
	private static final Logger logger = Logger.getLogger(CleanerTask.class);

	/**
	 * The sleep sleepDelay for this thread in Milliseconds. Default is 10.000 ms.
	 */
	private long sleepDelay = 10000L;

	/**
	 * This is the standard constructor. It creates a new instance using the 
	 * default value of 10.000 milliseconds for the sleep sleepDelay.
	 */
	public CleanerTask() {
		new CleanerTask(this.sleepDelay);
	}

	/**
	 * Create a new CleanerTask with a specified sleepDelay.
	 * @param sleepDelay - in Milliseconds
	 */
	public CleanerTask(final long sleepDelay) {
		this.sleepDelay = sleepDelay;
	}

	/**
	 * Executes the cleaner task. The garbage collector will be run 
	 * each defined interval.
	 */
	@Override
	public void run() {
		logger.debug("Cleaner thread is being executed.");
		
		// do forever
		while (true){
			try {
				// sleep for the defined number of seconds
				Thread.sleep(this.sleepDelay);
				this.clean();
			}catch (InterruptedException e) {
				logger.error("An error occurred: ", e);
			}
		}
	}

	/**
	 * Cleans the application from garbage.
	 */
	private void clean() {
		// collect garbage
		long freeMemBefore = Runtime.getRuntime().freeMemory();
		System.gc();    
		long freeMemAfter = Runtime.getRuntime().freeMemory();

		long diff = (long) ((freeMemAfter - freeMemBefore)/1024.0F/1024.0F);

		// log the freed memory amount
		if (diff > 0){
			logger.trace("I was able to free " + String.valueOf(diff) + " MBytes of RAM! Running again in " + sleepDelay/1000 + " seconds.");
		}else {
			logger.trace("I'm sorry, but I was not able to free any memory! Running again in " + sleepDelay/1000 + " seconds.");
		}
	}
	
	/**
	 * Cleans the application from garbage.
	 */
	public void cleanSystem() {
		// collect garbage
		long freeMemBefore = Runtime.getRuntime().freeMemory();
		System.gc();    
		long freeMemAfter = Runtime.getRuntime().freeMemory();

		long diff = (long) ((freeMemAfter - freeMemBefore)/1024.0F/1024.0F);

		// log the freed memory amount
		if (diff > 0){
			logger.info("I was able to free " + String.valueOf(diff) + " MBytes of RAM!");
		}else {
			logger.info("I'm sorry, but I was not able to free any memory!");
		}
	}

	/**
	 * @return the sleepDelay
	 */
	public long getSleepDelay() {
		return sleepDelay;
	}

	/**
	 * @param sleepDelay the sleepDelay to set
	 */
	public void setSleepDelay(long sleepDelay) {
		this.sleepDelay = sleepDelay;
	}
}
