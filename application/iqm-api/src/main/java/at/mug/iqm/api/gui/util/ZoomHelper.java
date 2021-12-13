package at.mug.iqm.api.gui.util;

/*
 * #%L
 * Project: IQM - API
 * File: ZoomHelper.java
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


import java.util.Collections;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author Philipp Kainz
 * @since   27.07.2012
 *
 */
public class ZoomHelper {
	
	/**
	 * Custom class logger
	 */
	private static final Logger logger = LogManager.getLogger(ZoomHelper.class);

	/**
	 * A predefined list of fixed zoom levels.
	 */
	private final double[] fixedLevels = {
		1/32.0, 1/24.0, 1/16.0, 1/12.0, 
		1/8.0, 1/6.0, 1/4.0, 1/3.0, 1/2.0, 0.75,
		1.0, 1.5,
		2.0, 3.0, 4.0, 6.0, 8.0, 12.0, 16.0, 24.0, 
		32.0, 48.0, 64.0, 72.0, 80.0, 92.0
	};
	
	/**
	 * The list of supported zoom levels in this class.
	 */
	private LinkedList<Double> zoomLevels = new LinkedList<Double>();

	/**
	 * Default constructor.
	 */
	public ZoomHelper() {
		logger.debug("Constructing a new instance...");
	}
	
	/**
	 * Get the next lower zoom level according to a given level.
	 * @param currentLevel the given zoom level
	 * @return the next lower zoom level
	 */
	public double getLowerZoomLevel(double currentLevel) {
		logger.trace("Current zoom level=" + currentLevel);
		double newLevel = zoomLevels.getFirst();
		
		for (int i=0; i < zoomLevels.size(); i++){
			if (zoomLevels.get(i) < currentLevel){
				newLevel = zoomLevels.get(i);
			}else{
				break;
			}
		}
		logger.trace("New zoom level=" + newLevel);
		return newLevel;
	}

	/**
	 * Get the next higher zoom level according to a given level.
	 * @param currentLevel the given zoom level
	 * @return the next higher zoom level
	 */
	public double getHigherZoomLevel(double currentLevel) {
		logger.trace("Current zoom level=" + currentLevel);
		double newLevel = zoomLevels.getLast();
		
		for (int i=zoomLevels.size()-1; i >=0 ; i--){
			if (zoomLevels.get(i) > currentLevel){
				newLevel = zoomLevels.get(i);
			}else{
				break;
			}
		}
		logger.trace("New zoom level=" + newLevel);
		return newLevel;
	}
	
	/**
	 * Calculates the zoom level according to a given minimum dimension.
	 * @param minDimension the minimum dimension
	 */
	public void calculateZoomLevels(double minDimension){
		
		logger.trace("Calculating zoom levels...");
		
		// clear the list
		zoomLevels.clear();
		// calculate custom levels for a specified minimum (either height or 
		// width of the image)
		final double constantDiff = 8.0d;
		// determine the scaling factors
		for (double i = 32.0d; i <= minDimension; i+=constantDiff){
			zoomLevels.add(1.0d/i);
		}
		// revert the list
		Collections.reverse(zoomLevels);
		
		// and add the rest of the zoom levels
		for (double d : fixedLevels){
			zoomLevels.add(d);
		}
		
		logger.trace("Done: " + zoomLevels);
	}
	
	/**
	 * Gets the minimal zoom of the calculated zoom list.
	 */
	public double getMinimalZoom(){
		logger.trace("The minimal zoom is currently: " + this.zoomLevels.getFirst());
		return this.zoomLevels.getFirst();
	}
	
	/**
	 * Gets the maximal zoom of the calculated zoom list.
	 */
	public double getMaximalZoom(){
		logger.trace("The maximal zoom is currently: " + this.zoomLevels.getLast());
		return this.zoomLevels.getLast();
	}

	/**
	 * @return the zoomLevels
	 */
	public LinkedList<Double> getZoomLevels() {
		return zoomLevels;
	}

	/**
	 * @param zoomLevels the zoomLevels to set
	 */
	protected void setZoomLevels(LinkedList<Double> zoomLevels) {
		this.zoomLevels = zoomLevels;
	}
	
}


