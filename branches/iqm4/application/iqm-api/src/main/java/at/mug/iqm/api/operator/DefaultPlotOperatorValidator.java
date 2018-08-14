package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: DefaultPlotOperatorValidator.java
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


import java.util.Vector;

import at.mug.iqm.api.model.IqmDataBox;

/**
 * This class implements a basic validation of parameters and sources common to
 * all {@link OperatorType#PLOT} instances.
 * 
 * @author Philipp Kainz
 * 
 */
public class DefaultPlotOperatorValidator implements IOperatorValidator {

	/**
	 * Default constructor.
	 */
	public DefaultPlotOperatorValidator() {
	}

	/**
	 * @see at.mug.iqm.api.operator.IOperatorValidator#validate(at.mug.iqm.api.operator.IWorkPackage)
	 * @see #validateSources(IWorkPackage)
	 */
	@Override
	public boolean validate(IWorkPackage wp) throws IllegalArgumentException,
			Exception {
		// IMPLEMENT ANY CUSTOM VALIDATION OF THE SOURCES IN THE SUBCLASSES
		return this.validateSources(wp);
	}

	/**
	 * This method - as default - checks for a source object at index position
	 * 0.
	 * 
	 * @param wp
	 * @return <code>true</code> if the source vector contains an IqmDataBox
	 *         object containing a plot at index 0
	 * @throws IllegalArgumentException
	 *             if the source vector
	 *             <ul>
	 *             <li>is empty,
	 *             <li>does not contain an {@link IqmDataBox} as wrapper, or
	 *             <li>the data box is not of type {@link OperatorType#PLOT}.
	 *             </ul>
	 */
	protected boolean validateSources(IWorkPackage wp)
			throws IllegalArgumentException {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockPlot) {
			pb = (ParameterBlockPlot) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		Vector<Object> sources = pb.getSources();

		// the sources must not be empty and must contain >= 1 plots
		if (sources.isEmpty()) {
			throw new IllegalArgumentException("The sources must not be empty!");
		}
		if (!(sources.elementAt(0) instanceof IqmDataBox)) {
			throw new IllegalArgumentException(
					"This operator requires an instance of IqmDataBox as data model!");
		} else {
			// check for plot model
			if (((IqmDataBox) sources.get(0)).getDataType() != DataType.PLOT) {
				throw new IllegalArgumentException(
						"This operator requires a plot!");
			}
		}

		return true;
	}

}
