package at.mug.iqm.api.exception;

/*
 * #%L
 * Project: IQM - API
 * File: OperatorNotAllowedException.java
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


/**
 * This exception is thrown at runtime, if an operator cannot be applied to 
 * an image.
 * @author Philipp Kainz
 */
public class OperatorNotAllowedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8087523956577641386L;

	/**
	 * This is the default constructor.
	 */
	public OperatorNotAllowedException(){
		super();
	}
	
	/**
	 * 
	 * Add a customized description to the exception.
	 * @param description
	 */
	public OperatorNotAllowedException(String description){
		super(description);
	}
}


