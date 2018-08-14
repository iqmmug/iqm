package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: FileTools.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * This class provides common tools for file handling.
 * 
 * @author Philipp Kainz
 * 
 */
public class FileTools {

	/**
	 * Copy a file from a source to a destination.
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static synchronized void copyFile(File source, File dest)
			throws IOException {
		FileChannel srcChannel = null;
		FileChannel dstChannel = null;
		try {
			srcChannel = new FileInputStream(source).getChannel();
			dstChannel = new FileOutputStream(dest).getChannel();
			dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
		} finally {
			srcChannel.close();
			dstChannel.close();
		}
	}
	
	public static synchronized void copyFiles(File[] sources, File toDir)
			throws IOException {
		if (!toDir.isDirectory())
			throw new IllegalArgumentException("The target is no directory!");
		
		for (File f : sources){
			File targetFile = new File(toDir.toString().concat(f.getName()));
			copyFile(f, targetFile);
		}
	}
}
