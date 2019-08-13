package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: OperatorType.java
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
 * This enumeration contains all possible assignments of 
 * an {@link IOperator} to a processing target in IQM.
 * <p>
 * Supported items: 
 * <ul>
 * <li>IMAGE for image operators
 * <li>PLOT for plot operators
 * <li>IMAGE_GENERATOR for image generators
 * <li>PLOT_GENERATOR for plot generators
 * <li>TABLE for tables
 * <li>OTHER for any operator, e.g. combined operators
 * <li>UNASSIGNED for unknown types
 * </ul>
 * 
 * 
 * @author Philipp Kainz
 *
 */
public enum OperatorType{
	/**
	 * The enum element for an image operator.
	 */
	IMAGE, 
	/**
	 * The enum element for a plot operator.
	 */
	PLOT, 
	/**
	 * The enum element for an image generator.
	 */
	IMAGE_GENERATOR,
	/**
	 * The enum element for a plot generator.
	 */
	PLOT_GENERATOR,
	/**
	 * The enum element for a plot operator.
	 */
	TABLE, 
	/**
	 * The enum element for any operator, e.g. combined operators.
	 */
	OTHER,
	/**
	 * The enum element for unassigned types.
	 */
	UNASSIGNED;
	
	/**
	 * Maps a given {@link OperatorType} to a string.
	 * 
	 * @param dataType
	 *            an enum value indicating the operator type
	 * @return the string representation of the operator type or an empty string, if
	 *         the operator type is not recognized
	 */
	public static String operatorTypeAsString(OperatorType opType) {
		switch (opType) {
		case IMAGE:
			return "IMAGE";

		case PLOT:
			return "PLOT";

		case TABLE:
			return "TABLE";

		case IMAGE_GENERATOR:
			return "IMAGE_GENERATOR";

		case PLOT_GENERATOR:
			return "PLOT_GENERATOR";

		case UNASSIGNED:
			return "UNASSIGNED";

		default:
			return "";
		}
	}
}
