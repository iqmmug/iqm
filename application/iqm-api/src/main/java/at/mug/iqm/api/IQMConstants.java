package at.mug.iqm.api;

/*
 * #%L
 * Project: IQM - API
 * File: IQMConstants.java
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

import java.util.Calendar;

import at.mug.iqm.api.model.IVirtualizable;

/**
 * This class contains constants, statically accessible in the whole application
 * scope. This class cannot be instantiated.
 * 
 * @author Philipp Kainz
 */
public final class IQMConstants {
	/**
	 * The application version string.
	 */
	public final static String APPLICATION_VERSION = "4.0.0";
	/**
	 * The application name.
	 */
	public final static String APPLICATION_NAME = "IQM";
	
	/*
	 * declare constants for the supported file types
	 */
	public final static String JPEG_EXTENSION = "jpeg";
	public final static String JPG_EXTENSION = "jpg";
	public final static String GIF_EXTENSION = "gif";
	public final static String TIFF_EXTENSION = "tiff";
	public final static String TIF_EXTENSION = "tif";
	public final static String PNG_EXTENSION = "png";
	public final static String BMP_EXTENSION = "bmp";
	public final static String DCM_EXTENSION = "dcm";
	public final static String FITS_EXTENSION = "fits";
	public final static String SVS_EXTENSION = "svs";

	public final static String TXT_EXTENSION = "txt";

	public final static String JTB_EXTENSION = "jtb";
	public final static String DAT_EXTENSION = "dat";
	public final static String CSV_EXTENSION = "csv";

	public static final String GY_EXTENSION = "gy";
	public static final String GVY_EXTENSION = "gvy";
	public static final String GROOVY_EXTENSION = "groovy";
	public static final String GSH_EXTENSION = "gsh";
	
	public static final String WAV_EXTENSION = "wav";
	public static final String B16_EXTENSION = "b16";
	public static final String RAW_EXTENSION = "raw";

	/**
	 * The extension for a serialized {@link IVirtualizable} object:<br>
	 * <b>S</b>erialized <b>V</b>irtualizable <b>O</b>bject.
	 */
	public final static String SERIALIZATION_EXTENSION = "svo";

	/*
	 * declare constants for the file type descriptions used in file filters
	 */
	public final static String JPEG_FILTER_DESCRIPTION = "JPEG (*." + JPG_EXTENSION + ", *." + JPEG_EXTENSION + ")";
	public final static String JPG_FILTER_DESCRIPTION = JPEG_FILTER_DESCRIPTION;
	public final static String GIF_FILTER_DESCRIPTION = "GIF (*." + GIF_EXTENSION + ")";
	public final static String TIFF_FILTER_DESCRIPTION = "TIFF (*." + TIF_EXTENSION + ", *." + TIFF_EXTENSION + ")";
	public final static String TIF_FILTER_DESCRIPTION = TIFF_FILTER_DESCRIPTION;
	public final static String PNG_FILTER_DESCRIPTION = "PNG (*." + PNG_EXTENSION + ")";
	public final static String BMP_FILTER_DESCRIPTION = "BMP (*." + BMP_EXTENSION + ")";
	public final static String DCM_FILTER_DESCRIPTION = "DICOM (*." + DCM_EXTENSION + ")";
	public final static String FITS_FILTER_DESCRIPTION = "FITS (*." + FITS_EXTENSION + ")";
	public final static String SVS_FILTER_DESCRIPTION = "SVS (*." + SVS_EXTENSION + ")";

	public final static String TXT_FILTER_DESCRIPTION = "Text (*." + TXT_EXTENSION + ")";

	public final static String JTB_FILTER_DESCRIPTION = "Java Table (*." + JTB_EXTENSION + ")";
	public final static String DAT_FILTER_DESCRIPTION = "DAT (*." + DAT_EXTENSION + ")";
	public final static String CSV_FILTER_DESCRIPTION = "CSV (*." + CSV_EXTENSION + ")";

	public final static String GROOVY_FILTER_DESCRIPTION = "Groovy Source Files (*."
			+ GY_EXTENSION + ", *."
			+ GVY_EXTENSION + ", *."
			+ GROOVY_EXTENSION + ", *." + GSH_EXTENSION + ", *." + TXT_EXTENSION + ")";
	
	public final static String WAV_FILTER_DESCRIPTION = "WAV (*." + WAV_EXTENSION + ")";
	public final static String B16_FILTER_DESCRIPTION = "B16 (*." + B16_EXTENSION + ")";
	public final static String RAW_FILTER_DESCRIPTION = "RAW (*." + RAW_EXTENSION + ")";
	

	/**
     * 
     */
	public final static String LINE_SEPARATOR = System
			.getProperty("line.separator");

	/**
     * 
     */
	public final static String FILE_SEPARATOR = System
			.getProperty("file.separator");

	/**
     * 
     */
	public final static int INTERPOLATION_NEAREST_NEIGHBOR = 0;
	public final static int INTERPOLATION_BILINEAR = 1;
	public final static int INTERPOLATION_BICUBIC = 2;
	public final static int INTERPOLATION_BICUBIC2 = 3;

	/**
	 * Constants for the configuration manager
	 */
	public final static String CONF_LASTUPDATE = "lastUpdate";
	public final static String CONF_ROOTPATH = "rootPath";
	public final static String CONF_TEMPPATH = "tempPath";
	public final static String CONF_CONFPATH = "confPath";
	public final static String CONF_IMAGEPATH = "imagePath";
	public final static String CONF_CLEANERINTERVAL = "cleanerTaskInterval";
	public final static String CONF_MEMORYMONITORINTERVAL = "memoryMonitorInterval";
	public final static String CONF_LOOKANDFEEL = "lookAndFeel";

	/**
	 * Constants for the description of image types.
	 */
	public final static String IMAGE_TYPE_16_BIT = "16 bit";
	public final static String IMAGE_TYPE_8_BIT = "8 bit";
	public final static String IMAGE_TYPE_DEFAULT = "Type?";
	public final static String IMAGE_TYPE_DOUBLE = "Double";
	public final static String IMAGE_TYPE_FLOAT = "Float";
	public final static String IMAGE_TYPE_INDEXED = "Palette";
	public final static String IMAGE_TYPE_INTEGER = "Integer";
	public final static String IMAGE_TYPE_RGB = "RGB";
	public final static String IMAGE_TYPE_RGBA = "RGBa";
	public final static String IMAGE_TYPE_UNDEFINED = "[UNDEF.]";

	/**
	 * Date constants
	 */
	public final static String CURRENTYEAR = String.valueOf(Calendar
			.getInstance().get(Calendar.YEAR));
	
	/**
	 * Direction constants
	 */
	public final static int DIRECTION_X = 0;
	public final static int DIRECTION_Y = 1;
	public final static int DIRECTION_Z = 2;
	
	public final static int DIRECTION_HORIZONTAL = DIRECTION_X;
	public final static int DIRECTION_VERTICAL = DIRECTION_Y;
	
}
