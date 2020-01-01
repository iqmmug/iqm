package at.mug.iqm.img.bundle.validators;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpImgStabilizerValidator.java
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


import java.util.Vector;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.DefaultImageOperatorValidator;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;


public class IqmOpImgStabilizerValidator extends DefaultImageOperatorValidator {

	public IqmOpImgStabilizerValidator() {
	}
	
	@Override
	protected boolean validateSources(IWorkPackage wp)
			throws IllegalArgumentException {
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		Vector<Object> sources = pb.getSources();

		boolean firstValid = true;
		boolean secondValid = true;

		// the sources must not be empty and must contain one single band image
		if (super.validateSources(wp)) {
			IqmDataBox box = (IqmDataBox) sources.elementAt(0);
			if (box.getDataType() != DataType.IMAGE) {
					firstValid = false;
			}

			if (sources.size() > 1) {
				IqmDataBox box2 = (IqmDataBox) sources.elementAt(1);
				if (box2.getDataType() != DataType.IMAGE) {
						secondValid = false;
				}
			}
			if (!firstValid && !secondValid)
				throw new IllegalArgumentException(
						"This operator requires an image at both indices 0 and 1 of the source vector!");
			else if (!firstValid)
				throw new IllegalArgumentException(
						"This operator requires an image at index 0 of the source vector!");
			else if (!secondValid)
				throw new IllegalArgumentException(
						"This operator requires an image at index 1 of the source vector!");
		}

		return true;
	}

}
