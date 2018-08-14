package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: ParameterBlockImg.java
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


/**
 * A parameter block for an image operator. This class simply subclasses the
 * {@link ParameterBlockIQM} and adds specific methods for image operators.
 * 
 * @author Philipp Kainz
 * 
 */
public class ParameterBlockImg extends ParameterBlockIQM {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -2545264639692789605L;

	/**
	 * Creates a default {@link ParameterBlockImg} from the specified
	 * {@link IOperatorDescriptor}.
	 * 
	 * @param odesc
	 *            the descriptor of the parameters
	 */
	public ParameterBlockImg(IOperatorDescriptor odesc) {
		super(odesc);
	}

	/**
	 * Creates a default {@link ParameterBlockImg} from the specified name.
	 * 
	 * @param name
	 *            the name of the operator
	 */
	public ParameterBlockImg(String name) {
		super(name);
	}

	@Override
	public ParameterBlockIQM clone() {
		return super.clone();
	}
}
