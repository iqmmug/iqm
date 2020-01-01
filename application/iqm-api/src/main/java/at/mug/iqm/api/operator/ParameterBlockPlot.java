package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: ParameterBlockPlot.java
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
 * A parameter block for a plot operator. This class simply subclasses the
 * {@link ParameterBlockIQM} and adds specific methods for plot operators.
 * 
 * @author Philipp Kainz
 * 
 */
public class ParameterBlockPlot extends ParameterBlockIQM {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -7038802699692452050L;

	/**
	 * Creates a default {@link ParameterBlockPlot} from the specified
	 * {@link IOperatorDescriptor}.
	 * 
	 * @param odesc
	 *            the descriptor of the parameters
	 */
	public ParameterBlockPlot(IOperatorDescriptor odesc) {
		super(odesc);
	}

	/**
	 * Creates a default {@link ParameterBlockPlot} from the specified name.
	 * 
	 * @param name
	 *            the name of the operator
	 */
	public ParameterBlockPlot(String name) {
		super(name);
	}

	@Override
	public ParameterBlockIQM clone() {
		return super.clone();
	}

}
