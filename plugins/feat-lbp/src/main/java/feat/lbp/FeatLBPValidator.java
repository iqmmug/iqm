package feat.lbp;

/*
* This file is part of IQM, hereinafter referred to as "this program".
* 
* Copyright (C) 2009 - 2014 Helmut Ahammer, Philipp Kainz
* 
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
*/

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.DefaultImageOperatorValidator;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.commons.util.image.ImageAnalyzer;

/**
 * This class implements custom validation activities for the operator. One is
 * free to inherit directly from the {@link DefaultImageOperatorValidator} in
 * order to spare double code.
 * 
 * If you want to extend the default validation routine or override it (e.g.
 * perform NO validation at all), simply override
 * {@link DefaultImageOperatorValidator#validate(at.mug.iqm.api.operator.IWorkPackage)}
 * in this class.
 * 
 * @author Philipp Kainz
 * 
 */
public class FeatLBPValidator extends DefaultImageOperatorValidator {

	public FeatLBPValidator() {
	}

	@Override
	public boolean validate(IWorkPackage wp) throws IllegalArgumentException,
			Exception {
		boolean valid = super.validate(wp);
		
		IqmDataBox box = (IqmDataBox) wp.getSources().get(0);
		boolean isGrey = ImageAnalyzer.is8BitGrey(box.getImage());
		if (!isGrey) 
			throw new IllegalArgumentException(I18N.getMessage("error.wrongImageFormat"));
		
		return valid && isGrey;
	}
	
}
