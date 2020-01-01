package at.mug.iqm.commons.io;

/*
 * #%L
 * Project: IQM - API
 * File: ObjectFileWriter.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This helper class writes a POJO to a file.
 * 
 * @author Philipp Kainz
 * @since 3.2
 */
public class ObjectFileWriter implements Runnable {
	
	private static final Logger logger = LogManager.getLogger(ObjectFileWriter.class);

	protected File file;
	protected OutputStream fos = null;
	protected ObjectOutputStream oos = null;
	protected Object outputObject = null;

	/**
	 * Create a new writer for a given file with the specified content.
	 * 
	 * @param file
	 * @param outputObject
	 */
	public ObjectFileWriter(File file, Object outputObject) {
		this.file = file;
		this.outputObject = outputObject;
	}

	/**
	 * Performs the actual writing of the content.
	 * 
	 * @throws IOException
	 */
	public void write() throws IOException {
		fos = new FileOutputStream(file.toString());
		oos = new ObjectOutputStream(fos);
		
		// write the object
		oos.writeObject(outputObject);
		
		// close streams
		oos.close();
		fos.close();
	}

	@Override
	public void run() {
		try {
			write();
		} catch (IOException e) {
			logger.error("Error writing object to file!", e);
		}
	}
}
