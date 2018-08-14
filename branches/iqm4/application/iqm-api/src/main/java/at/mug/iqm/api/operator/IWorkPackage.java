package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: IWorkPackage.java
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import at.mug.iqm.api.processing.IExecutionProtocol;

/**
 * This interface declares input parameters for a specific {@link IOperator}. An
 * instance of IWorkPackage represents an encapsulated collection of parameters
 * and sources in a subclass of {@link ParameterBlockIQM}. Furthermore the work
 * package contains the operator instance to be executed by any execution
 * thread.
 * <p>
 * During the parameter tweaking the {@link ParameterBlockIQM} is altered until
 * the operator has to process this package and construct a {@link IResult}.
 * 
 * @author Philipp Kainz
 */
public interface IWorkPackage extends Cloneable {

	/**
	 * Set the {@link IOperator} to this work package.
	 * 
	 * @param operator
	 */
	void setOperator(IOperator operator);

	/**
	 * Get the {@link IOperator} associated with this work package.
	 * 
	 * @return the operator
	 */
	IOperator getOperator();

	/**
	 * Get the parameters and sources associated with this work package.
	 * 
	 * @return the {@link ParameterBlockIQM}
	 */
	ParameterBlockIQM getParameters();

	/**
	 * Set the parameters and sources to this work package.
	 * 
	 * @param parameters
	 */
	void setParameters(ParameterBlockIQM parameters);

	/**
	 * A method for updating the entire source list at once.
	 * 
	 * @param sources
	 */
	void updateSources(Vector<Object> sources);

	/**
	 * Implements the <code>protected Object.clone()</code> method and returns a
	 * new reference to the clone of the object.
	 * 
	 * @return a cloned instance
	 * @throws CloneNotSupportedException
	 * 
	 * @see Object#clone()
	 */
	IWorkPackage clone();

	/**
	 * Remove all sources from the parameter block.
	 */
	void removeSources();

	/**
	 * Gets all sources from the parameter block.
	 * 
	 * @return the {@link Vector} of objects
	 */
	List<Object> getSources();

	/**
	 * Updates sources in the parameter block.
	 * <p>
	 * If the source at the specified index is <code>null</code>, an
	 * {@link NoSuchElementException} will be thrown. If you need to set a new
	 * source use {@link #setSource(Object, int)} instead.
	 * 
	 * @param source
	 *            the source object
	 * @param index
	 *            the index position
	 * 
	 * @throws NoSuchElementException
	 *             if the requested index does not contain any element that can
	 *             be updated
	 * 
	 * @see #setSource(Object, int)
	 */
	void updateSource(Object source, int index) throws NoSuchElementException;

	/**
	 * Sets all sources to the parameter block.
	 * 
	 * @param sources
	 *            the source vector of objects
	 */
	void setSources(List<Object> sources);

	/**
	 * Sets a single source to the parameter block.
	 * <p>
	 * The source at the specified index will be overwritten without any
	 * warning.
	 * 
	 * @param source
	 *            the source object
	 * @param index
	 *            the position where to set the source to
	 */
	void setSource(Object source, int index);

	/**
	 * Adds a single source to the end of the parameter's source list.
	 * 
	 * @param source
	 *            the source object
	 */
	void addSource(Object source);

	/**
	 * Sets an integer flag determining whether or not the algorithm (IOperator)
	 * associated with this work package should be executed more than once. The
	 * number of executions is determined by parameter <code>n</code>.
	 * <p>
	 * The controlling {@link IExecutionProtocol} will be responsible for
	 * collecting all outputs before displaying them as a batch.
	 * 
	 * @param n
	 *            the number of iterations, an algorithm should run, or any
	 *            value <code>&le;1</code> for single iteration
	 */
	void setIterations(int n);

	/**
	 * Gets the integer flag indicating whether or not the algorithm (IOperator)
	 * associated with this work package should be executed more than once.
	 * 
	 * @return the number of iterations, usually a value <code>&gt;1</code>, or
	 *         any value <code>&le;1</code>, if the operator should only run
	 *         once
	 */
	int getIterations();

	/**
	 * Gets the currently selected manager indices of this work package.
	 * 
	 * @return the array, stored in the {@link ParameterBlockIQM} of this
	 *         {@link WorkPackage}
	 * @see ParameterBlockIQM#getManagerIndices()
	 */
	int[] getManagerIndices();

	/**
	 * Sets the currently selected manager indices to this work package.
	 * 
	 * @param managerIndices
	 *            the array of manager indices in this {@link WorkPackage}
	 * @see ParameterBlockIQM#setManagerIndices(int[])
	 */
	void setManagerIndices(int[] managerIndices);

	/**
	 * Set a flag for the operator whether or not the computation of custom
	 * results should be performed or not.
	 * <p>
	 * This flag advises the algorithm to produce (partial) results.
	 * 
	 * @param customComputationEnabled
	 */
	void setCustomComputationEnabled(boolean customComputationEnabled);

	/**
	 * Get a flag whether or not the operator should perform the computation of
	 * custom results.
	 * 
	 * @return <code>true</code> if the flag is set, <code>false</code>
	 *         otherwise
	 */
	boolean isCustomComputationEnabled();

	/**
	 * Set a flag for the operator whether or not the computation of table
	 * results should be performed or not.
	 * <p>
	 * This flag advises the algorithm to produce (partial) results.
	 * 
	 * @param tableComputationEnabled
	 */
	void setTableComputationEnabled(boolean tableComputationEnabled);

	/**
	 * Get a flag whether or not the operator should perform the computation of
	 * table results.
	 * 
	 * @return <code>true</code> if the flag is set, <code>false</code>
	 *         otherwise
	 */
	boolean isTableComputationEnabled();

	/**
	 * Set a flag for the operator whether or not the computation of plot
	 * results should be performed or not.
	 * <p>
	 * This flag advises the algorithm to produce (partial) results.
	 * 
	 * @param plotComputationEnabled
	 */
	void setPlotComputationEnabled(boolean plotComputationEnabled);

	/**
	 * Get a flag whether or not the operator should perform the computation of
	 * plot results.
	 * 
	 * @return <code>true</code> if the flag is set, <code>false</code>
	 *         otherwise
	 */
	boolean isPlotComputationEnabled();

	/**
	 * Set a flag for the operator whether or not the computation of image
	 * results should be performed or not.
	 * <p>
	 * This flag advises the algorithm to produce (partial) results.
	 * 
	 * @param imageComputationEnabled
	 */
	void setImageComputationEnabled(boolean imageComputationEnabled);

	/**
	 * Get a flag whether or not the operator should perform the computation of
	 * image results.
	 * 
	 * @return <code>true</code> if the flag is set, <code>false</code>
	 *         otherwise
	 */
	boolean isImageComputationEnabled();

	/**
	 * Get a predefined set of parameters for an operator which has been
	 * serialized to the template file.
	 * 
	 * @param name
	 *            the unique name of the template for the associated operator
	 * @return a {@link ParameterBlockIQM} containing the template
	 */
	ParameterBlockIQM getTemplate(String name);
}
