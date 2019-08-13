package at.mug.iqm.commons.io;

/*
 * #%L
 * Project: IQM - API
 * File: PlainTextFileWriter.java
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

/**
 * This helper class writes a plain text UTF-8 (default) encoded file.
 * Encoding can be set in a custom constructor {@link #PlainTextFileWriter(File, String, String)}.
 * 
 * @author Philipp Kainz
 * 
 */
public class PlainTextFileWriter implements Runnable {
	
	private static final Logger logger = Logger.getLogger(PlainTextFileWriter.class);
			
	private File file;
	private String content;
	private String encoding = "UTF-8";

	/**
	 * Create a new writer for a given file with the specified content.
	 * 
	 * @param file
	 * @param content
	 */
	public PlainTextFileWriter(File file, String content) {
		this.file = file;
		this.content = content;
	}

	/**
	 * Create a new writer for a given file with the specified content and
	 * encoding.
	 * 
	 * @param file
	 *            the destination file
	 * @param content
	 *            the content
	 * @param encoding
	 *            specify the encoding string, if <code>null</code> is passed,
	 *            UTF-8 is assumed
	 */
	public PlainTextFileWriter(File file, String content, String encoding) {
		this(file, content);
		if (encoding != null) {
			this.encoding = encoding;
		}
	}

	/**
	 * Performs the actual writing of the content.
	 * 
	 * @throws IOException
	 */
	public void write() throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), encoding));
		bw.write(content);
		bw.flush();
		bw.close();
	}

	@Override
	public void run() {
		try {
			write();
		} catch (IOException e) {
			logger.error("Error writing file!", e);
		}
	}
}
