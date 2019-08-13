package at.mug.iqm.api.persistence;

/*
 * #%L
 * Project: IQM - API
 * File: Serializer.java
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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class serializes given {@link Object}s into a <code>byte[]</code>.
 * 
 * @author Philipp Kainz
 * 
 */
public class Serializer {

	/**
	 * Custom logger.
	 */
	private static final Logger logger = LogManager.getLogger(Serializer.class);

	/**
	 * The {@link OutputStream} for serialization.
	 */
	private OutputStream fos = null;
	/**
	 * The {@link ObjectOutputStream} for serialization.
	 */
	private ObjectOutputStream oos = null;

	/**
	 * The {@link InputStream} for serialization.
	 */
	private InputStream fis = null;
	/**
	 * The {@link ObjectInputStream} for serialization.
	 */
	private ObjectInputStream ois = null;

	/**
	 * Default constructor.
	 */
	public Serializer() {
	}

	/**
	 * Serialize an {@link Object} to a file on the hard drive. Each object to
	 * be serialized has to implement the {@link Serializable} interface.
	 * 
	 * @param o
	 *            the {@link Object}
	 * @param f
	 *            the location
	 * 
	 * @throws FileNotFoundException
	 *             if the file cannot be located
	 * @throws IOException
	 *             if the file cannot be read or written
	 */
	public synchronized void serialize(Object o, File f)
			throws FileNotFoundException, IOException {

		logger.debug("Attempting to serialize object to [" + f.toString() + "]");

		// open streams
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		fos = new FileOutputStream(raf.getFD());
		oos = new ObjectOutputStream(fos);

		// store the object
		oos.writeObject(o);

		// close streams
		oos.close();
		fos.close();
		raf.close();
		logger.debug("Successfully serialized object to [" + f.toString()
				+ "].");
	}

	/**
	 * Deserialize an object from a given binary file. Each object to be
	 * deserialized has to implement the {@link Serializable} interface.
	 * 
	 * @param f
	 *            the file on the hard drive
	 * @return the {@link Object}
	 * 
	 * @throws FileNotFoundException
	 *             if the file cannot be located
	 * @throws IOException
	 *             if the file cannot be read or written
	 * @throws ClassNotFoundException
	 *             if the object cannot be restored into a class on the
	 *             class path
	 */
	public synchronized Object deserialize(File f)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		Object result = null;
		logger.debug("Attempting to deserialize object from [" + f.toString()
				+ "]");

		// open streams
		fis = new FileInputStream(f);
		ois = new ObjectInputStream(fis);

		// read the object
		result = ois.readObject();

		// close streams
		ois.close();
		fis.close();

		logger.debug("Read output object, it's an instance of ["
				+ result.getClass() + "].");
		return result;
	}

	/**
	 * Serializes an {@link Object} into a <code>byte[]</code>. Each object to
	 * be serialized has to implement the {@link Serializable} interface.
	 * 
	 * @param o
	 *            the object to be serialized
	 * @return the byte representation
	 */
	public static byte[] getBytes(Object o) {
		byte[] array = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			array = baos.toByteArray();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return array;
	}

	/**
	 * Deserializes an <code>byte[]</code> to an {@link Object}. Each object to
	 * be deserialized has to implement the {@link Serializable} interface.
	 * 
	 * @param data
	 *            the byte array
	 * @return the restored object
	 */
	public static Object getObject(byte[] data) {
		Object object = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(in);

			object = is.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}
}
