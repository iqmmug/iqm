package at.mug.iqm.img.bundle.validators;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpRegGrowValidator.java
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


import java.util.Vector;

import javax.media.jai.PlanarImage;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.DefaultImageOperatorValidator;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.commons.util.image.ImageAnalyzer;


public class IqmOpRegGrowValidator extends DefaultImageOperatorValidator {

	public IqmOpRegGrowValidator() {
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
		
		// the sources must not be empty and must contain one single band image
		if (super.validateSources(wp)){
			IqmDataBox box = (IqmDataBox) sources.elementAt(0);
			if (box.getDataType() == DataType.IMAGE) {
				PlanarImage pi = box.getImage();
				if (!ImageAnalyzer.has1Band(pi))
					throw new IllegalArgumentException(
							"This operator requires a single band image at index 0 of the source vector!");
			}
		}
		
		return true;
	}
}
