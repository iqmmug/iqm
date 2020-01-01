package at.mug.iqm.core.processing;

/*
 * #%L
 * Project: IQM - Application Core
 * File: TaskFactory.java
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


import java.beans.PropertyChangeListener;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.IOperator;
import at.mug.iqm.api.operator.IOperatorGUI;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.api.processing.ITaskFactory;


/**
 * @author Philipp Kainz
 *
 */
public class TaskFactory implements ITaskFactory {

	private TaskFactory() {
		Application.setTaskFactory(this);
	}

	public static ITaskFactory getInstance() {
		ITaskFactory tFactory = Application.getTaskFactory();
		if (tFactory == null) {
			new TaskFactory();
		}
		return tFactory;
	}

	@Override
	public AbstractProcessingTask createPreviewTask() {
		PreviewProcessingTask task = new PreviewProcessingTask();
		return task;
	}

	@Override
	public AbstractProcessingTask createPreviewTask(IWorkPackage wp,
			IOperatorGUI opGUI) {
		PreviewProcessingTask task = new PreviewProcessingTask(wp, opGUI);
		return task;
	}

	@Override
	public AbstractProcessingTask createSingleTask() {
		SingleProcessingTask task = new SingleProcessingTask();
		return task;
	}

	@Override
	public AbstractProcessingTask createSingleTask(IWorkPackage wp) {
		SingleProcessingTask task = new SingleProcessingTask(wp);
		return task;
	}

	@Override
	public AbstractProcessingTask createSingleTask(IWorkPackage wp,
			IOperatorGUI opGUI) {
		SingleProcessingTask task = new SingleProcessingTask(wp, opGUI);
		return task;
	}

	@Override
	public AbstractProcessingTask createSerialTask() {
		SerialProcessingTask task = new SerialProcessingTask();
		return task;
	}

	@Override
	public AbstractProcessingTask createSerialTask(IWorkPackage wp, boolean virtual) {
		SerialProcessingTask task = new SerialProcessingTask(wp, virtual);
		return task;
	}
	
	@Override
	public AbstractProcessingTask createSerialTask(IWorkPackage wp, IOperatorGUI opGUI, boolean virtual) {
		SerialProcessingTask task = new SerialProcessingTask(wp, opGUI, virtual);
		return task;
	}

	@Override
	public AbstractProcessingTask createParallelTask() {
		ExecutorServiceProcessingTask task = new ExecutorServiceProcessingTask();
		return task;
	}
	
	@Override
	public AbstractProcessingTask createParallelTask(IWorkPackage wp,
			boolean virtual, int nThreads) {
		ExecutorServiceProcessingTask task = new ExecutorServiceProcessingTask(wp, virtual, nThreads);
		return task;
	}

	@Override
	public AbstractProcessingTask createParallelTask(IWorkPackage wp,
			IOperatorGUI opGUI, boolean virtual, int nThreads) {
		ExecutorServiceProcessingTask task = new ExecutorServiceProcessingTask(wp, virtual, opGUI, nThreads);
		return task;
	}

	@Override
	public AbstractProcessingTask createCopiesTask(IWorkPackage wp,
			int numCopies, boolean virtual) {
		CreateCopiesTask task = new CreateCopiesTask(wp, numCopies, virtual);
		return task;
	}

	@Override
	public AbstractProcessingTask createSingleSubTask(
			IOperator callingOperator, IWorkPackage newWorkPackage,
			boolean forwardListeners) {
		
		if (forwardListeners) {
			// forward all listeners to the subtask's operator
			for (PropertyChangeListener pcl : callingOperator
					.getProgressListeners(null)) {
				newWorkPackage.getOperator().addProgressListener(pcl, null);
			}
		}
		
		SingleProcessingTask singleSubTask = new SingleProcessingTask(
				newWorkPackage);
		
		singleSubTask.setParentTask(callingOperator.getParentTask());

		return singleSubTask;
	}

}
