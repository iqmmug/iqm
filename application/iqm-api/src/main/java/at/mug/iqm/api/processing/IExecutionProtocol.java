package at.mug.iqm.api.processing;

/*
 * #%L
 * Project: IQM - API
 * File: IExecutionProtocol.java
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


import at.mug.iqm.api.gui.MultiResultDialog;
import at.mug.iqm.api.operator.AbstractOperatorGUI;
import at.mug.iqm.api.operator.IOperator;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorGUI;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.Result;

/**
 * This interface declares methods for an execution process of a single
 * {@link IOperator} in IQM.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IExecutionProtocol {

	/**
	 * Executes a preview with the given work package and GUI.
	 * 
	 * @param wp
	 * @param opGUI
	 */
	void executePreview(IWorkPackage wp, IOperatorGUI opGUI);

	/**
	 * Finishes the protocol for an operator execution. New operators may be
	 * launched thereafter.
	 */
	void finishProtocol();

	/**
	 * Updates the GUI, if a change in the sources occurs, e.g. if RGB images
	 * are loaded and the GUI has to adapt.
	 * 
	 * @throws Exception
	 *             , if this adaption fails
	 */
	void updateGUI() throws Exception;

	/**
	 * This method tries to update the manager indices for the processing with
	 * the launched operator.
	 * 
	 * @param currMgrIdxs
	 *            the current manager indices
	 * @param targetMgrIdxs
	 *            the target manager indices
	 * @return either the current indices, if updating fails, or the target
	 *         indices, if it succeeds
	 */
	int[] updateSources(int[] currMgrIdxs, int[] targetMgrIdxs);

	/**
	 * Gets the name of the currently launched operator.
	 * 
	 * @return the unique operator name specified in the
	 *         <code>String[][] resources</code> with the key
	 *         <code>GlobalName</code>
	 */
	String getOperatorName();

	/**
	 * Gets the work package of this protocol.
	 * 
	 * @return the work package
	 */
	IWorkPackage getWorkPackage();

	/**
	 * Gets the GUI of this protocol.
	 * 
	 * @return the GUI
	 */
	IOperatorGUI getOperatorGUI();

	/**
	 * Gets the operator descriptor of this protocol.
	 * 
	 * @return the operator descriptor.
	 */
	IOperatorDescriptor getOperatorDescriptor();

	/**
	 * Enables the user input on a given window, i.e. an operator's GUI.
	 * Furthermore, this method displays a wait cursor on the window.
	 * 
	 * @param window
	 *            the window to disable the input on
	 */
	void enableInputs(final AbstractOperatorGUI window);

	/**
	 * Disables the user input on a given window, i.e. an operator's GUI.
	 * Furthermore, this method hides the wait cursor and switches back to
	 * default cursor.
	 * 
	 * @param window
	 *            the window to disable the input on
	 */
	void disableInputs(final AbstractOperatorGUI window);

	/**
	 * Executes the serial processing of all selected manager items. Every item
	 * will be processed with the same operator and parameters.
	 */
	void executeSerialProcessing();

	/**
	 * Executes the processing of all selected manager items in a parallel
	 * manner. Every item will be processed with the same operator and
	 * parameters, but each item is processed in a separate thread. The number
	 * of threads is specified by <code>nThreads</code>.
	 * 
	 * @param nThreads
	 *            the number of threads that are supposed to run concurrently
	 */
	void executeParallelProcessing(int nThreads);

	/**
	 * Sets the "virtual" flag to the operator's GUI, if supported.
	 * 
	 * @param virtual
	 *            <code>true</code>, if the check box should be selected,
	 *            <code>false</code> otherwise
	 */
	void setVirtualFlagToOperatorGUI(boolean virtual);

	/**
	 * Launches the protocol and initiates the GUI building.
	 * 
	 * @return the {@link ExecutionState} after launching
	 */
	int launchProtocol();

	/**
	 * Gets the Dialog for selecting multiple output results in order to add
	 * them to the Tank list.
	 * 
	 * @return a reference to the {@link MultiResultDialog}
	 */
	MultiResultDialog getMultiResultDialog();

	/**
	 * Sets the preview result in the current execution protocol.
	 * 
	 * @param previewResult
	 */
	void setPreviewResult(IResult previewResult);

	/**
	 * Gets the preview result in the current execution protocol.
	 * 
	 * @return the preview result instance
	 */
	Result getPreviewResult();
}
