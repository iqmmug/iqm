package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: FileContentParser.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import at.mug.iqm.api.exception.MultipleMIMETypesException;
import at.mug.iqm.api.exception.UnsupportedMIMETypeException;
import at.mug.iqm.api.operator.DataType;

/**
 * Parses the content of a file into internal {@link DataType}s.
 * 
 * @author Philipp Kainz
 * 
 */
public class FileContentParser {

	// private logger
	private static final Logger logger = Logger
			.getLogger(FileContentParser.class);

	public static DataType parseContent(List<File> fileList)
			throws IllegalArgumentException, MultipleMIMETypesException,
			UnsupportedMIMETypeException {
		if (fileList == null || fileList.isEmpty())
			throw new IllegalArgumentException(
					"The file list must not be empty!");

		DataType currType = null;

		AutoDetectParser parser = new AutoDetectParser();
		parser.setParsers(new HashMap<MediaType, Parser>());
		InputStream stream;

		String firstType = "";

		for (int i = 0; i < fileList.size(); i++) {

			File f = fileList.get(i);
			Metadata metadata = new Metadata();
			metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, f.getName());

			try {
				stream = new FileInputStream(f);
				parser.parse(stream, new DefaultHandler(), metadata,
						new ParseContext());
				stream.close();
			} catch (IOException e) {
				logger.error("", e);
			} catch (SAXException e) {
				logger.error("", e);
			} catch (TikaException e) {
				logger.error("", e);
			}

			// read the mime type
			String mimeType = metadata.get(HttpHeaders.CONTENT_TYPE);

			// store the first type
			if (i == 0) {
				firstType = mimeType;
			}

			// check for allowed types
			if (mimeType.startsWith("image/")) {
				// all images are allowed
				currType = DataType.IMAGE;
			} else if (mimeType.equals("text/plain")) {
				// all plot files in plain text are allowed
				currType = DataType.PLOT;
			} else {
				throw new UnsupportedMIMETypeException("Current MIME type ["
						+ mimeType + "] is not supported in IQM.");
			}

			logger.debug("MIME type of [" + f.toString() + "]: " + mimeType);

			// check if mime type changed
			if (!firstType.equals(mimeType)) {
				throw new MultipleMIMETypesException("Current MIME type ["
						+ mimeType + "] differs from first MIME type ["
						+ firstType + "]");
			}

		}

		return currType;
	}

	public static String parseContent(File f) throws IllegalArgumentException{
		if (f == null)
			throw new IllegalArgumentException("The file must not be empty!");

		AutoDetectParser parser = new AutoDetectParser();
		parser.setParsers(new HashMap<MediaType, Parser>());
		InputStream stream;

		Metadata metadata = new Metadata();
		metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, f.getName());

		try {
			stream = new FileInputStream(f);
			parser.parse(stream, new DefaultHandler(), metadata,
					new ParseContext());
			stream.close();
		} catch (IOException e) {
			logger.error("", e);
		} catch (SAXException e) {
			logger.error("", e);
		} catch (TikaException e) {
			logger.error("", e);
		}

		// read the mime type
		String mimeType = metadata.get(HttpHeaders.CONTENT_TYPE);

		logger.debug("MIME type of [" + f.toString() + "]: " + mimeType);

		return mimeType;
	}
}
