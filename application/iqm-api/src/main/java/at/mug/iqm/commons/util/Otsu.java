package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: Otsu.java
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


import javax.media.jai.Histogram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Implementation of Otsu's thresholding algorithm
 * @author 2012 11 Jürgen Kleinowitz
 *
 */
public class Otsu {
	private static final Logger logger = LogManager.getLogger(Otsu.class);
	
	Histogram histo;
	
	public Otsu() {
		logger.debug("Constructing a new instance of '" + Otsu.class + "'...");
	}
	
	public Otsu(Histogram histo) {
		this();
		setHisto(histo);
	}
	/**
	 * Calculates a threshold for each of the bands stored in histo
	 * @return an array of threshold values
	 */
	public double[] calcThreshold() {
		int numBands = this.histo.getNumBands();		
		double[] thresholds = new double[numBands];
		
		for (int i = 0; i < numBands; i++) {
			thresholds[i] = calcOtsu(this.histo.getBins(i));
		}
		return thresholds;		
	}
	/**
	 * Otsu calculation for a single band based on http://www.labbookpages.co.uk/software/imgProc/otsuThreshold.html
	 * @param histogram an int[] array containing the histogram
	 * @return double threshold
	 */
	private double calcOtsu(int[] histogram) {
		double total = 0;
		long sum = 0;
		for (int t=0 ; t<histogram.length ; t++) {
			sum += t * histogram[t];
			total += histogram[t];
		}
		double sumB = 0;
		double weightBackground = 0;
		double weightForgeground = 0;

		double varMax = 0;
		double threshold = 0;

		for (int t=0 ; t<256 ; t++) {
		   weightBackground += histogram[t];
		   if (weightBackground == 0) {
			   continue;
		   }

		   weightForgeground = total - weightBackground;
		   if (weightForgeground == 0) {
			   break;
		   }

		   sumB += (float) (t * histogram[t]);

		   double meanBackground = sumB / weightBackground;            
		   double meanForgeground = (sum - sumB) / weightForgeground;    

		   // Calculate Between Class Variance
		   double varBetween = weightBackground * weightForgeground * (meanBackground - meanForgeground) * (meanBackground - meanForgeground);

		   // Check if new maximum found
		   if (varBetween > varMax) {
		      varMax = varBetween;
		      threshold = t;
		   }
		}
		return threshold;
		
	}
	
	public void setHisto(Histogram histo) {
		this.histo = histo;
	}

}
