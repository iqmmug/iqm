package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: IOperatorValidator.java
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
 * This interface declares methods for an operator's validator.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IOperatorValidator {

	/**
	 * This method is called in order to validate a given {@link IWorkPackage}
	 * for an operator.
	 * <p>
	 * Within this method execution rules are declared and evaluated.
	 * <p>
	 * One may bypass the validation returning <code>true</code> on every call.
	 * <b>Note</b> That the execution of the operator is not guaranteed to be
	 * finished without uncaught errors!
	 * 
	 * @param wp
	 *            the work package
	 * @return <code>true</code>, if the package is valid for this operator
	 * @throws IllegalArgumentException
	 *             if the source or parameters do evaluate the rules to
	 *             <code>true</code>
	 * @throws Exception if other errors occur during validation
	 */
	boolean validate(IWorkPackage wp) throws IllegalArgumentException, Exception;

}
