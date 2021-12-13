package at.mug.iqm.api.processing;

/*
 * #%L
 * Project: IQM - API
 * File: ITaskFactory.java
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


import at.mug.iqm.api.operator.IOperator;
import at.mug.iqm.api.operator.IOperatorGUI;
import at.mug.iqm.api.operator.IWorkPackage;

public interface ITaskFactory {

	AbstractProcessingTask createPreviewTask();

	AbstractProcessingTask createPreviewTask(IWorkPackage wp, IOperatorGUI opGUI);

	AbstractProcessingTask createSingleTask();

	AbstractProcessingTask createSingleTask(IWorkPackage wp);

	AbstractProcessingTask createSingleTask(IWorkPackage wp, IOperatorGUI opGUI);

	AbstractProcessingTask createSerialTask();

	AbstractProcessingTask createSerialTask(IWorkPackage wp, boolean virtual);

	AbstractProcessingTask createSerialTask(IWorkPackage wp,
			IOperatorGUI opGUI, boolean virtual);

	AbstractProcessingTask createParallelTask();

	AbstractProcessingTask createParallelTask(IWorkPackage wp, boolean virtual,
			int nThreads);

	AbstractProcessingTask createParallelTask(IWorkPackage wp,
			IOperatorGUI opGUI, boolean virtual, int nThreads);

	AbstractProcessingTask createCopiesTask(IWorkPackage wp, int numCopies,
			boolean virtual);

	AbstractProcessingTask createSingleSubTask(IOperator callingOperator,
			IWorkPackage newWorkPackage, boolean forwardListeners);
}
