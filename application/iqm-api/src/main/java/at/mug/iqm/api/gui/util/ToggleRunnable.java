package at.mug.iqm.api.gui.util;

/*
 * #%L
 * Project: IQM - API
 * File: ToggleRunnable.java
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


import javax.media.jai.PlanarImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;

/**
 * This class performs a toggling preview between the currently processed image and the original.
 * @author Helmut Ahammer, Philipp Kainz
 *
 */
public class ToggleRunnable implements Runnable {
	
	// class specific logger
	private static Class<?> caller = ToggleRunnable.class;
	private static final Logger logger = LogManager.getLogger(ToggleRunnable.class);

	// class variable declaration
	long delay = (long) 1.5 * 1000;
	private PlanarImage pi1;
	private PlanarImage pi2;

	
	public ToggleRunnable(){}
	
	
	public ToggleRunnable(PlanarImage pi1, PlanarImage pi2){
		this.pi1 = pi1;
		this.pi2 = pi2;
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("ToggleRunnable");
		while(true){                        
			try{
				// change the images in look
				Application.getLook().setImage(pi1);
				Thread.sleep(delay);
				Application.getLook().setImage(pi2);
				Thread.sleep(delay);
			}
			catch(InterruptedException e){
				logger.debug("The thread has been interrupted while sleeping: " + e);
				break;
			} catch (NullPointerException npe){
				logger.error("No images pi1 and pi2 set!");
				break;
			}
		}    
	}


	/**
	 * @return the caller
	 */
	public static Class<?> getCaller() {
		return caller;
	}


	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}


	/**
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}


	/**
	 * @return the pi1
	 */
	public PlanarImage getPi1() {
		return pi1;
	}


	/**
	 * @return the pi2
	 */
	public PlanarImage getPi2() {
		return pi2;
	}


	/**
	 * @param caller the caller to set
	 */
	public static void setCaller(Class<?> caller) {
		ToggleRunnable.caller = caller;
	}


	/**
	 * @param delay the delay to set
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}


	/**
	 * @param pi1 the pi1 to set
	 */
	public void setPi1(PlanarImage pi1) {
		this.pi1 = pi1;
	}


	/**
	 * @param pi2 the pi2 to set
	 */
	public void setPi2(PlanarImage pi2) {
		this.pi2 = pi2;
	}			
}
