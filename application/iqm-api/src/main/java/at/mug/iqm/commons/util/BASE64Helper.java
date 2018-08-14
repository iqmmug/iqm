package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: BASE64Helper.java
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

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

/**
 * This is a helper class for Base64 encoding and decoding.
 * 
 * @author Philipp Kainz
 * 
 */
public final class BASE64Helper {
	/**
	 * Decodes a given byte array.
	 * 
	 * @param bytes
	 * @return a byte array
	 */
	public static byte[] decode(byte[] bytes) {
		return new Base64().decode(bytes);
	}

	/**
	 * Decodes a Base64 encoded UTF-8 {@link String} into a byte[].
	 * 
	 * @param b64String
	 * @return a byte representation of the decoded {@link String}
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] decode(String b64String)
			throws UnsupportedEncodingException {
		return new Base64().decode(b64String.getBytes("UTF-8"));
	}

	/**
	 * Encodes a given byte array.
	 * 
	 * @param bytes
	 * @return a byte array
	 */
	public static byte[] encode(byte[] bytes) {
		return new Base64().encode(bytes);
	}

	/**
	 * Encodes byte[] to a string.
	 * 
	 * @param bytes a byte array
	 * @return a String representation of the byte array
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeToString(byte[] bytes) {
		byte[] stringBytes = new Base64().encode(bytes);
		return new String(stringBytes);
	}

}
