package at.mug.iqm.core.util;

/*
 * #%L
 * Project: IQM - Application Core
 * File: CoreTools.java
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


import java.awt.RenderingHints;

import javax.media.jai.JAI;
import javax.media.jai.TileCache;

/**
 * @author Philipp Kainz
 * 
 */
public final class CoreTools {

	/**
	 * The current version number of the application.
	 */
	private static String versionNumber;
	/**
	 * The minimum required JVM version.
	 */
	private static String javaVersionMin;

	/**
	 * This method gets the number of processors
	 * 
	 * @return int number of available processors
	 */
	public static int getNumberOfProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * This methods converts an integer to a string with leading zeros.
	 * 
	 * @param n
	 *            - number to be converted
	 * @param d
	 *            - digits
	 */
	public static String getStringWithLeadingZeros(int n, int d) {
		String str = String.valueOf(n);
		while (str.length() < d) {
			str = "0" + str;
		}
		return str;
	}

	/**
	 * This methods converts an integer to a string with leading space.
	 * 
	 * @param n
	 *            - number to be converted
	 * @param d
	 *            - digits
	 */
	public static String getStringWithLeadingSpace(int n, int d) {
		String str = String.valueOf(n);
		while (str.length() < d) {
			str = " " + str;
		}
		return str;
	}

	/**
	 * This method gets default Tile Cache Rendering Hints used for user
	 * operators, such as IqmInvert, IqmThreshold, IqmRGBRelative,...
	 */
	public static RenderingHints getDefaultTileCacheRenderingHints() {
		final long tileCacheSize = 256 * 1024 * 1024L; // 256 Megabytes;
		//
		// //DEFAULT_RENDERING_HINTS.put(RenderingHints.KEY_RENDERING,
		// RenderingHints.VALUE_RENDER_QUALITY);
		// //RenderingHints defaultRendHints = new
		// RenderingHints(RenderingHints.KEY_RENDERING,
		// RenderingHints.VALUE_RENDER_QUALITY);
		// //defaultRendHints.put(JAI.KEY_INTERPOLATION,
		// Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
		// // .... more hints set to the default values
		//
		// //RenderingHints rendHints =
		// (RenderingHints)defaultRendHints.clone();
		// //rendHints.put(JAI.KEY_TILE_CACHE, tileCache);
		//
		TileCache tc = JAI.createTileCache();
		tc.setMemoryCapacity(tileCacheSize);
		RenderingHints rh = new RenderingHints(JAI.KEY_TILE_CACHE, tc);
		return rh;
		// ((TileCache) rh.get(JAI.KEY_TILE_CACHE)).flush();
	}

	/**
	 * This method sets the version number of IQM
	 * 
	 * @param number
	 *            - version number of IQM
	 */

	public static void setVersionNumber(String number) {
		versionNumber = number;
	}

	/**
	 * This method gets the version number of Iqm
	 * 
	 * @return version of Iqm
	 */
	public static String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * This method sets the version of Java
	 * 
	 * @param arg
	 *            - version of Java
	 */
	public static void setJavaVersionMin(String arg) {
		javaVersionMin = arg;
	}

	/**
	 * This method gets the version of Java
	 * 
	 * @return version of Java
	 */
	public static String getJavaVersionMin() {
		return javaVersionMin;
	}

}
