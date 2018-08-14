package at.mug.iqm.api.exception;

/*
 * #%L
 * Project: IQM - API
 * File: OperatorAlreadyOpenException.java
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


import at.mug.iqm.api.processing.IExecutionProtocol;

/**
 * This exception is thrown at runtime, if an operator is already open in an
 * {@link IExecutionProtocol}.
 * 
 * @author Philipp Kainz
 */
public class OperatorAlreadyOpenException extends Exception {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 2543471453504388586L;

	/**
	 * The name of the opened operator.
	 */
	private String name = "";

	/**
	 * This is the default constructor.
	 */
	public OperatorAlreadyOpenException() {
		super();
	}

	/**
	 * 
	 * Add a customized description to the exception.
	 * 
	 * @param description
	 */
	public OperatorAlreadyOpenException(String description) {
		super(description);
	}

	/**
	 * 
	 * Add a customized description to the exception.
	 * 
	 * @param description
	 */
	public OperatorAlreadyOpenException(String description, String name) {
		super(description);
		this.setName(name);
	}

	/**
	 * Sets the name of the opened operator.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the opened operator.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
