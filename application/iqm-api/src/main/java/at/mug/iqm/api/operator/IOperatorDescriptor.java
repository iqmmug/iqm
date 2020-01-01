package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: IOperatorDescriptor.java
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


import java.io.Serializable;
import java.util.List;

import javax.media.jai.util.Range;


/**
 * This interface declares methods for an operator's descriptor.
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({"rawtypes"})
public interface IOperatorDescriptor extends Serializable{	
	
	/**
	 * @return the unique name of the operator
	 */
	String getName();
	
	/**
	 * @return the {@link OperatorType} 
	 */
	OperatorType getType();
	
	/**
	 * @return the {@link StackProcessingType}
	 */
	StackProcessingType getStackProcessingType();
	
	/**
	 * @return the required number of sources
	 */
	int getNumSources();

	/**
	 * @return the defined resources
	 */
	String[][] getResources();
	
	/**
	 * @return the parameters' names
	 */
	String[] getParamNames();
	
	/**
	 * @return the parameters' classes
	 */
	Class[] getParamClasses();
	
	/**
	 * @return the default settings of the parameters
	 */
	Object[] getParamDefaults();
	
	/**
	 * @return the valid ranges of the parameters
	 */
	Range[] getValidParamValues();

	/**
	 * Returns a list of data types, this operator is able to produce.
	 * @return a list of data types, this operator is able to produce
	 */
	List<DataType> getOutputTypes();

}
