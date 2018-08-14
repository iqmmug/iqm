package at.mug.iqm.api.model;

/*
 * #%L
 * Project: IQM - API
 * File: ImageModel.java
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


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.media.jai.PlanarImage;
import javax.media.jai.remote.SerializableRenderedImage;

import at.mug.iqm.commons.util.image.ImageAnalyzer;

/**
 * The image model represents the container object for holding images in IQM.
 * 
 * @author Philipp Kainz
 * 
 */
public class ImageModel extends AbstractDataModel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7408466192830465190L;

	/**
	 * The variable for the image, will not be serialized.
	 */
	private transient PlanarImage image = null;

	/**
	 * Serialized version of the image.
	 */
	private SerializableRenderedImage serializableImage = null;

	/**
	 * Default constructor.
	 */
	public ImageModel() {
		// super() sets the model name to "AbstractDataModel" at first
		// and then calls ImageModel#setModelName(String)
		super();
	}

	/**
	 * Constructor receiving an instance of {@link PlanarImage}.
	 * 
	 * @param pi
	 */
	public ImageModel(PlanarImage pi) {
		this();
		this.setImage(pi);
	}

	/**
	 * Constructor receiving an instance of {@link PlanarImage} and a
	 * {@link Hashtable}<code>&lt;String, Object&gt;</code>.
	 * 
	 * @param pi
	 * @param properties
	 */
	public ImageModel(PlanarImage pi, Hashtable<String, Object> properties) {
		this();
		this.setImage(pi);
		this.setProperties(properties);
	}

	/**
	 * Sets the property <code>image_name</code> of the {@link ImageModel}
	 * instance. This model uses the built-in properties from the
	 * {@link PlanarImage} class.
	 * 
	 * @see at.mug.iqm.api.model.IDataModel#setModelName(java.lang.String)
	 */
	@Override
	public void setModelName(String name) {
		// set the image name "" to the dummy image,
		// if the image name is null
		String value = "";
		if (name != null) {
			value = name;
		} else if (name == null) {
			value = this.toString();
		}
		this.modelName = value;
		this.setProperty("model_name", value);

		// special for this model is the setting of the image name
		// wrap the model_name to the image_name, because Look.setNewImage is
		// accessing image_name of the image properties
		this.setProperty("image_name", value);
	}

	/**
	 * Gets the property <code>file_name</code> from the {@link ImageModel}
	 * instance. This model uses the built-in properties from the
	 * {@link PlanarImage} class.
	 * 
	 * @return the file name of the image in the file system
	 */
	public String getFileName() {
		return String.valueOf(this.getProperty("file_name"));
	}

	/**
	 * Gets the property <code>file_name</code> from the {@link ImageModel}
	 * instance. This model uses the built-in properties from the
	 * {@link PlanarImage} class.
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		// set the file name "" to the image,
		// if the file name is null
		String value = "";
		if (fileName != null) {
			value = fileName;
		} else if (fileName == null) {
			value = this.toString();
		}
		this.setProperty("file_name", value);
	}

	/**
	 * This method is a wrapper method, so that every property is written to the
	 * {@link PlanarImage} object's property list, too.
	 * <p>
	 * <b>Note</b>: If the <code>image</code> is of another type than
	 * {@link PlanarImage} this method must be altered!
	 * </p>
	 * 
	 * @see at.mug.iqm.api.model.IDataModel#setProperty(String, Object)
	 */
	@Override
	public void setProperty(String key, Object value) {
		super.setProperty(key, value);

		// set the value to the image's property list, too
		// this can only be done, if the image already exists
		if (this.image != null) {
			this.image.setProperty(key, value);
			// this.copyPropertiesToPlanarImage(); // overwrites the whole hash
			// table
		}
	}

	/**
	 * @return the image
	 */
	public PlanarImage getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(PlanarImage image) {
		this.image = image;
		// read all properties and put it to the model
		this.properties.putAll(ImageAnalyzer.getProperties(image));
	}

	/**
	 * Convenient method for copying the hashtable properties to the image
	 * properties.
	 */
	@SuppressWarnings("rawtypes")
	protected void copyPropertiesToPlanarImage() {
		Set set = this.getProperties().entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			this.getImage().setProperty((String) entry.getKey(),
					entry.getValue());
		}
	}

	/**
	 * @return the serializableImage
	 */
	protected synchronized SerializableRenderedImage getSerializableImage() {
		return serializableImage;
	}

	/**
	 * @param serializableImage
	 *            the serializableImage to set
	 */
	protected synchronized void setSerializableImage(
			SerializableRenderedImage serializableImage) {
		this.serializableImage = serializableImage;
	}

	/**
	 * This methods overrides the default <code>writeObject()</code>-Method of
	 * {@link Serializable}.
	 * 
	 * @param oos
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream oos) throws IOException {

		// add a routine for serializing a planar image and it's properties
		this.setSerializableImage(new SerializableRenderedImage(
				this.getImage(), true));
		oos.writeObject(this.getSerializableImage());
		oos.writeObject(this.getProperties());
	}

	/**
	 * This methods overrides the default <code>readObject()</code>-Method of
	 * {@link Serializable}.
	 * 
	 * @param ois
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		this.setSerializableImage((SerializableRenderedImage) ois.readObject());
		this.setImage(PlanarImage.wrapRenderedImage(this.getSerializableImage()));
		this.setProperties((Hashtable<String, Object>) ois.readObject());
	}

	@Override
	public ImageModel clone() throws CloneNotSupportedException {
		ImageModel theClone = new ImageModel(image, properties);
		return theClone;
	}
}
