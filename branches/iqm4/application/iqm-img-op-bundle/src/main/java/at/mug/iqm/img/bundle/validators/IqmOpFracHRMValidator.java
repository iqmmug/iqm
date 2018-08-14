package at.mug.iqm.img.bundle.validators;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracHRMValidator.java
 * 
 * $Id: IqmOpFracHRMValidator.java 548 2016-01-18 09:36:47Z kainzp $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/validators/IqmOpFracHRMValidator.java $
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


import at.mug.iqm.api.operator.DefaultImageOperatorValidator;
import at.mug.iqm.api.operator.IWorkPackage;

public class IqmOpFracHRMValidator extends DefaultImageOperatorValidator {

	public IqmOpFracHRMValidator() {
	}

	@Override
	public boolean validate(IWorkPackage wp) throws Exception {
		// DON'T PERFORM VALIDATION ON A GENERATOR
		// throw new IllegalArgumentException();
		return true;
	}
}
