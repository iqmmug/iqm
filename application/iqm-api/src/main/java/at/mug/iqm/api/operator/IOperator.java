package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: IOperator.java
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


import java.beans.PropertyChangeSupport;

import at.mug.iqm.api.events.handler.IProgressEmitter;
import at.mug.iqm.api.processing.AbstractProcessingTask;


/**
 * This is the public interface for an IQM operator. Every operator can be
 * cancelled by user interaction.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IOperator extends ICancelable, IProgressEmitter {
	
	/**
	 * This method contains the entire logic (algorithm) of the operator. An
	 * {@link IOperator} does not declare any class members, since the execution
	 * of this code must be thread-safe.
	 * 
	 * @param workPackage
	 *            the parameters and sources
	 * @return a {@link IResult} containing all processed data
	 * @throws Exception
	 *             if something went wrong
	 */
	IResult run(IWorkPackage workPackage) throws Exception;

	/**
	 * Set the executing task of this operator.
	 * 
	 * @param parentTask
	 */
	void setParentTask(AbstractProcessingTask parentTask);

	/**
	 * Get the executing task of this operator.
	 * 
	 * @return the parent task of this operator
	 */
	AbstractProcessingTask getParentTask();

	/**
	 * Gets the unique name of the {@link IOperator} by returning the
	 * "GlobalName" element from the <code>resources[][]</code> of the
	 * associated {@link IOperatorDescriptor}.
	 * 
	 * @return the unique name of the operator
	 */
	String getName();
	
	/**
	 * Gets the type of the operator listed in an enumeration.
	 * 
	 * @return the {@link OperatorType}
	 */
	OperatorType getType();

	/**
	 * Get the {@link PropertyChangeSupport} for the operator. Property changes
	 * can be fired using {@link #getPcs()}
	 * <code>.firePropertyChanged(PropertyChangeEvent)</code>.
	 * 
	 * @return the {@link PropertyChangeSupport}
	 */
	PropertyChangeSupport getPcs();
	
}
