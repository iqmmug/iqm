package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: DataType.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2021 Helmut Ahammer, Philipp Kainz
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
 * This enumeration contains all possible output types of an {@link IOperator}
 * in IQM.
 * <p>
 * Supported items:
 * <ul>
 * <li>{@link #IMAGE}
 * <li>{@link #PLOT}
 * <li>{@link #TABLE}
 * <li>{@link #CUSTOM}
 * <li>{@link #EMPTY}
 * <li>{@link #UNSUPPORTED}
 * </ul>
 * 
 * @author Philipp Kainz
 * 
 */
public enum DataType {
	/**
	 * The enum element declaring an operator to produce <code>1...n</code>
	 * image results.
	 */
	IMAGE,
	/**
	 * The enum element declaring an operator to produce <code>1...n</code> plot
	 * results.
	 */
	PLOT,
	/**
	 * The enum element declaring an operator to produce <code>1...n</code>
	 * table results.
	 */
	TABLE,
	/**
	 * The enum element declaring an operator to produce <code>1...n</code>
	 * custom results.
	 */
	CUSTOM,

	EMPTY,

	UNSUPPORTED;

	/**
	 * Maps a given {@link DataType} to a string.
	 * 
	 * @param dataType
	 *            an enum value indicating the data type
	 * @return the string representation of the data type or an empty string, if
	 *         the data type is not recognized
	 */
	public static String dataTypeAsString(DataType dataType) {
		switch (dataType) {
		case IMAGE:
			return "TYPE_IMAGE";

		case PLOT:
			return "TYPE_PLOT";

		case TABLE:
			return "TYPE_TABLE";

		case CUSTOM:
			return "TYPE_CUSTOM";

		case EMPTY:
			return "TYPE_EMPTY";

		case UNSUPPORTED:
			return "TYPE_UNSUPPORTED";

		default:
			return "";
		}
	}
}
