package at.mug.iqm.commons.io;

/*
 * #%L
 * Project: IQM - API
 * File: PlainTextFileLoader.java
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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

import at.mug.iqm.api.exception.UnsupportedMIMETypeException;
import at.mug.iqm.commons.util.FileContentParser;

/**
 * This helper class loads a plain text file.
 * 
 * @author Philipp Kainz
 * 
 */
public class PlainTextFileReader implements Callable<String> {

	private File file;

	/**
	 * Create a new loader for a given file.
	 * @param file
	 */
	public PlainTextFileReader(File file) {
		this.file = file;
	}

	/**
	 * Performs the actual input stream reading.
	 * 
	 * @param file
	 * @return the string contained in the plaintext file
	 * @throws IOException 
	 */
	public String read(File file) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(file));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}

	/**
	 * Validates the content of the file to be text.
	 * 
	 * @throws UnsupportedMIMETypeException
	 */
	private void validate() throws UnsupportedMIMETypeException {
		try {
			String mime = FileContentParser.parseContent(file);
			if (!mime.startsWith("text")) {
				throw new UnsupportedMIMETypeException(
						"The file must contain plain text!");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String call() throws Exception {
		try {
			validate();
			return read(file);
		} catch (UnsupportedMIMETypeException e){
			e.printStackTrace();
			return null;
		}
	}
}
