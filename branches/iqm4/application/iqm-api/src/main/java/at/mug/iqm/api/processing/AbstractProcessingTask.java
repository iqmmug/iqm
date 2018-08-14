package at.mug.iqm.api.processing;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractProcessingTask.java
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

import javax.swing.SwingWorker;

import at.mug.iqm.api.operator.IOperator;
import at.mug.iqm.api.operator.IOperatorGUI;
import at.mug.iqm.api.operator.IWorkPackage;

/**
 * 
 * @author Philipp Kainz
 * 
 */
public abstract class AbstractProcessingTask extends
		SwingWorker<Object, Object> implements IProcessingTask {

	/**
	 * A flag whether or not the processing task uses temporary hard disk
	 * storage for large data formats.
	 */
	protected boolean virtual;

	/**
	 * A flag whether or not the processing task is launched without GUI.
	 */
	protected boolean headless;

	/**
	 * The start time will be set, when the processing starts.
	 */
	protected long startTime;

	/**
	 * A variable for recording the actual running time.
	 */
	protected long duration;

	/**
	 * The cached {@link IWorkPackage}.
	 */
	protected IWorkPackage workPackage;

	/**
	 * The cached {@link IOperator} of this task. Usually the operator shares
	 * the reference with the {@link IOperator} in the work package.
	 */
	protected IOperator operator;

	/**
	 * The {@link IOperatorGUI} of the operator in this task.
	 */
	protected IOperatorGUI operatorGUI;

	/**
	 * A reference to the parent task, if present.
	 */
	protected AbstractProcessingTask parentTask;

	/**
	 * A reference to the child task, if present.
	 */
	protected AbstractProcessingTask childTask;

	/**
	 * Empty default constructor.
	 */
	public AbstractProcessingTask() {
	}

	/**
	 * Create a new task from a given {@link IWorkPackage}.
	 * 
	 * @param workPackage
	 */
	public AbstractProcessingTask(IWorkPackage workPackage) {
		this.setWorkPackage(workPackage);
		this.setOperator(workPackage.getOperator());
	}

	/**
	 * This method must be implemented by all subclasses.
	 * 
	 * @return the processed result, retrievable by <code>get()</code>.
	 */
	@Override
	protected abstract Object doInBackground() throws Exception;

	/**
	 * This method is called by the EventDispatcherThread, when
	 * <code>publish()</code> is called within the <code>doInBackground()</code>
	 * method.
	 * 
	 * @param chunks
	 *            - the objects to process
	 */
	@Override
	protected void process(List<Object> chunks) {
		super.process(chunks);
	}

	/**
	 * This method is called after the <code>doInBackground()</code> method and
	 * must be implemented by all subclasses. It is called too, when the
	 * SwingWorker is cancelled.
	 */
	@Override
	protected void done() {
		super.done();
	}

	/**
	 * @return the workPackage
	 */
	public IWorkPackage getWorkPackage() {
		return workPackage;
	}

	/**
	 * @param workPackage
	 *            the workPackage to set
	 */
	public void setWorkPackage(IWorkPackage workPackage) {
		this.workPackage = workPackage;
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

	public IOperatorGUI getOperatorGUI() {
		return operatorGUI;
	}

	public void setOperatorGUI(IOperatorGUI operatorGUI) {
		this.operatorGUI = operatorGUI;
		if (operatorGUI != null) {
			this.setHeadless(false); // automatically set headless to false, if
										// the GUI is not null
		} else {
			this.setHeadless(true);
		}
	}

	public boolean isVirtual() {
		return this.virtual;
	}

	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}

	public boolean isHeadless() {
		return headless;
	}

	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	public AbstractProcessingTask getParentTask() {
		return parentTask;
	}

	public void setParentTask(AbstractProcessingTask parentTask) {
		this.parentTask = parentTask;
	}

	public AbstractProcessingTask getChildTask() {
		return childTask;
	}

	public void setChildTask(AbstractProcessingTask childTask) {
		this.childTask = childTask;
	}
}
