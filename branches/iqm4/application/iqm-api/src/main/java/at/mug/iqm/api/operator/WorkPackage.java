package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: WorkPackage.java
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

import java.awt.image.renderable.ParameterBlock;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.templatemanager.XMLPreferencesManager;

/**
 * This is a ready-to-use implementation of {@link IWorkPackage}.
 * 
 * @author Philipp Kainz
 * 
 */
public class WorkPackage implements IWorkPackage {

	/**
	 * The parameters and sources.
	 */
	protected ParameterBlockIQM parameters;

	/**
	 * The associated operator.
	 */
	protected IOperator operator;

	/**
	 * The number of iterations, the associated operator should run. Default is
	 * 1, indicating, that the operator should only be executed once.
	 */
	protected int iterations = 1;

	/**
	 * A flag whether or not the operator should produce image results. Default
	 * is <code>true</code>.
	 */
	protected boolean imageComputationEnabled;

	/**
	 * A flag whether or not the operator should produce plot results. Default
	 * is <code>true</code>.
	 */
	protected boolean plotComputationEnabled;

	/**
	 * A flag whether or not the operator should produce table results. Default
	 * is <code>true</code>.
	 */
	protected boolean tableComputationEnabled;

	/**
	 * A flag whether or not the operator should produce custom results. Default
	 * is <code>true</code>.
	 */
	protected boolean customComputationEnabled;

	/**
	 * Constructs a new work package with the given parameters.
	 * 
	 * @param operator
	 *            may be <code>null</code>
	 * @param pb
	 *            must not be <code>null</code>
	 */
	public WorkPackage(IOperator operator, ParameterBlockIQM pb) {
		this.setOperator(operator);
		this.setParameters(pb);
	}

	/**
	 * The default constructor.
	 */
	public WorkPackage() {
	}

	/**
	 * @return the parameters
	 */
	public ParameterBlockIQM getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(ParameterBlockIQM parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the operator
	 */
	public IOperator getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(IOperator operator) {
		this.operator = operator;
	}

	/**
	 * Get the a predefined template for the operator in this work package.
	 */
	public ParameterBlockIQM getTemplate(String name) {
		if (this.operator == null) {
			throw new IllegalArgumentException(
					"The operator name is undefined.");
		}

		return XMLPreferencesManager.getInstance().getTemplate(
				this.operator.getName(), name);
	}

	/**
	 * A convenient wrapper method for adding a source to the end of the source
	 * list.
	 * <p>
	 * This method wraps {@link ParameterBlockIQM#addSource(Object)}.
	 * 
	 * @param source
	 */
	public void addSource(Object source) {
		this.parameters.addSource(source);
	}

	/**
	 * A convenient wrapper method for setting a source to a specified index of
	 * the source list.
	 * <p>
	 * This method wraps {@link ParameterBlockIQM#setSource(Object, int)}.
	 * 
	 * @param source
	 * @param index
	 */
	public void setSource(Object source, int index) {
		this.parameters.setSource(source, index);
	}

	/**
	 * A convenient wrapper method for setting the entire source list at once.
	 * <p>
	 * This method wraps {@link ParameterBlockIQM#setSources(Vector)}.
	 * 
	 * @param sources
	 */
	public void setSources(List<Object> sources) {
		this.parameters.setSources(new Vector<Object>(sources));
	}

	/**
	 * A method for updating the entire source list at once.
	 * 
	 * @param sources
	 */
	public void updateSources(Vector<Object> sources) {
		this.setSources(sources);
	}

	/**
	 * A method for setting a source to a specified index of the source list.
	 * 
	 * @param source
	 * @param index
	 */
	public void updateSource(Object source, int index) {
		if (this.getSources().get(index) == null) {
			throw new NoSuchElementException(
					"The requested index does not contain a source to be updated!");
		} else {
			this.setSource(source, index);
		}
	}

	/**
	 * A convenient wrapper method for getting the entire source list at once.
	 * <p>
	 * This method wraps {@link ParameterBlockIQM#getSources()}.
	 * 
	 * @return the source list
	 */
	public List<Object> getSources() {
		return this.parameters.getSources();
	}

	/**
	 * A convenient wrapper method for removing all sources.
	 * <p>
	 * This method wraps {@link ParameterBlock#removeSources()}.
	 */
	public void removeSources() {
		this.parameters.removeSources();
	}

	/**
	 * Gets a copy of the object.
	 */
	public WorkPackage clone() {
		WorkPackage theClone = new WorkPackage(getOperator(), getParameters());

		theClone.setIterations(getIterations());
		theClone.setManagerIndices(getManagerIndices());
		theClone.setCustomComputationEnabled(isCustomComputationEnabled());
		theClone.setImageComputationEnabled(isImageComputationEnabled());
		theClone.setPlotComputationEnabled(isPlotComputationEnabled());
		theClone.setTableComputationEnabled(isTableComputationEnabled());

		return theClone;
	}

	/**
	 * Creates a standard {@link WorkPackage} for a given operator including the
	 * operator and the standard parameter block.
	 * 
	 * @param operatorName
	 *            the unique name (identifier) of the operator
	 * @return a work package for an execution task
	 */
	public static WorkPackage create(String operatorName) {
		WorkPackage wp = new WorkPackage();

		wp.setOperator(OperatorFactory.createOperator(operatorName));
		wp.setParameters(new ParameterBlockIQM(operatorName));

		return wp;
	}

	/**
	 * Process the result of this work package.
	 * 
	 * @return a multi-dimensional {@link Result} object
	 */
	public IResult process() {
		AbstractProcessingTask task = Application.getTaskFactory()
				.createSingleSubTask(this.operator, this, false);
		task.execute();

		try {
			Result res_tmp = (Result) task.get();
			return res_tmp;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setIterations(int n) {
		if (n < 1) {
			n = 1;
		}
		this.iterations = n;
	}

	public int getIterations() {
		return this.iterations;
	}

	public int[] getManagerIndices() {
		return this.getParameters().getManagerIndices();
	}

	public void setManagerIndices(int[] managerIndices) {
		this.getParameters().setManagerIndices(managerIndices);
	}

	public boolean isImageComputationEnabled() {
		return imageComputationEnabled;
	}

	public void setImageComputationEnabled(boolean imageComputationEnabled) {
		this.imageComputationEnabled = imageComputationEnabled;
	}

	public boolean isPlotComputationEnabled() {
		return plotComputationEnabled;
	}

	public void setPlotComputationEnabled(boolean plotComputationEnabled) {
		this.plotComputationEnabled = plotComputationEnabled;
	}

	public boolean isTableComputationEnabled() {
		return tableComputationEnabled;
	}

	public void setTableComputationEnabled(boolean tableComputationEnabled) {
		this.tableComputationEnabled = tableComputationEnabled;
	}

	public boolean isCustomComputationEnabled() {
		return customComputationEnabled;
	}

	public void setCustomComputationEnabled(boolean customComputationEnabled) {
		this.customComputationEnabled = customComputationEnabled;
	}
}
