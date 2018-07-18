package at.mug.iqm.api.model;

/*
 * #%L
 * Project: IQM - API
 * File: CustomDataModel.java
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


/**
 * @author Philipp Kainz
 *
 */
public class CustomDataModel 
extends AbstractDataModel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -3712101336752554500L;

	private Object[] contentDescriptors = null;
	private Object[] content = null;
	
	/**
	 * Default constructor, puts the <code>model_name</code> to the 
	 * properties hashtable.
	 * Default name is "CustomDataModel".
	 */
	public CustomDataModel() {
		this("CustomDataModel");
	}
	
	/**
	 * Custom constructor, puts the <code>model_name</code> to the 
	 * properties hashtable.
	 * @param name the name of the model
	 */
	protected CustomDataModel(String name) {
		this.setModelName(modelName);
	}
	
	/**
	 * @return the contentDescriptors
	 */
	public Object[] getContentDescriptors() {
		return contentDescriptors;
	}

	/**
	 * @param contentDescriptors the contentDescriptors to set
	 */
	public void setContentDescriptors(Object[] contentDescriptors) {
		this.contentDescriptors = contentDescriptors;
	}

	/**
	 * @return the content
	 */
	public Object[] getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(Object[] content) {
		this.content = content;
	}
	
	@Override
	public CustomDataModel clone() throws CloneNotSupportedException {
		CustomDataModel theClone = new CustomDataModel(getModelName());
		theClone.setContentDescriptors(getContentDescriptors());
		theClone.setContent(getContent());
		return theClone;
	}

}
