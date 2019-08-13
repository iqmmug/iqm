package at.mug.iqm.api.exception;

/*
 * #%L
 * Project: IQM - API
 * File: UnreadableSVSFileException.java
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


/**
 * This exception is thrown at runtime, when the content of the SVS
 * file cannot be read.
 * @author Philipp Kainz
 */
public class UnreadableSVSFileException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3008056742849772347L;

	/**
	 * This is the default constructor.
	 */
	public UnreadableSVSFileException(){
		super();
	}
	
	/**
	 * 
	 * Add a customized description to the exception.
	 * @param description
	 */
	public UnreadableSVSFileException(String description){
		super(description);
	}
}


