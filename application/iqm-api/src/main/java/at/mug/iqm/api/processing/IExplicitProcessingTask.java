package at.mug.iqm.api.processing;

import java.util.List;

import at.mug.iqm.api.operator.IResult;

/*
 * #%L
 * Project: IQM - API
 * File: IExplicitProcessingTask.java
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
 * This interface declares methods for a processing task to be run explicitly.
 * 
 * @author Philipp Kainz
 *
 */
public interface IExplicitProcessingTask {
	
	/**
	 * This method returns an object that can be cast to any other class.
	 * E.g. some operators return an {@link IResult}, others use some kind
	 * of {@link List} interface as container. 
	 * 
	 * @return a very generic {@link Object}
	 * @throws Exception
	 */
	Object processExplicit() throws Exception;

}
