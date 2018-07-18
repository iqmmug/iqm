package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: IOperatorGUI.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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
 * This interface declares methods for an operator GUI in IQM.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IOperatorGUI {
	/**
	 * Initialize the default settings for an {@link IOperatorGUI}, e.g. the
	 * location of the window or the default close operation.
	 */
	void initialize();

	/**
	 * A custom routine for setting the GUI visible (this may override the
	 * default implementation when using <code>JFrame</code> as base container
	 * class).
	 */
	void setVisible(boolean b);

	/**
	 * Determines whether or not the GUI is currently visible to the user.
	 * 
	 * @return <code>true</code> if the GUI is displayed, <code>false</code>
	 *         otherwise
	 */
	boolean isVisible();

	/**
	 * Instruct the GUI to validate and repaint for updated parameters, e.g. if the GUI is
	 * altered for another input type.
	 * <p>
	 * This method alters the GUI elements in a container, if the input type is
	 * non-standard for this operator GUI. Furthermore, this method is
	 * responsible for setting the parameters to the elements and calls
	 * {@link #updateParameterBlock()} before {@link #setParameterValuesToGUI()}
	 * as last statements.
	 * 
	 * @throws Exception
	 */
	void update() throws Exception;

	/**
	 * Sets the work package to the operator's GUI, so that the custom GUI
	 * elements can be constructed using the parameter values.
	 * 
	 * @param wp
	 */
	void setWorkPackage(IWorkPackage wp);

	/**
	 * Gets the work package of an operator GUI using the current parameters set
	 * by the custom GUI elements.
	 * 
	 * @return the work package
	 */
	IWorkPackage getWorkPackage();

	/**
	 * This method implements the initialization of the GUI. An instance of
	 * {@link IOperatorGUI} may be constructed empty or directly using a valid
	 * {@link IWorkPackage}.
	 * <p>
	 * If the GUI is constructed empty, the work package must be set manually
	 * using {@link #setWorkPackage(IWorkPackage)} and {@link
	 * #setParameterValuesToGUI()} must be called in order to set the parameter
	 * values to the GUI.
	 * 
	 * @throws Exception
	 */
	void setParameterValuesToGUI() throws Exception;

	/**
	 * Updates the parameter block in the work package using the current states
	 * of the custom GUI elements.
	 * 
	 * @throws Exception
	 */
	void updateParameterBlock() throws Exception;

	/**
	 * Sets the parameter block in the work package. This method replaces the
	 * <code>ParameterBlockIQM</code> instance in the work package.
	 * <p>
	 * <b>Hint</b>: This method call should be followed by
	 * {@link #setParameterValuesToGUI()} in order to update the GUI elements,
	 * too.
	 * 
	 * @param pb
	 *            the parameter block containing the new data
	 * @throws Exception
	 */
	void updateParameterBlock(ParameterBlockIQM pb) throws Exception;

	/**
	 * Performs a preview of the operator using the current work package. This
	 * method calls <code>executeTask(AbstractProcessingTask)</code>.
	 */
	void showPreview();

	/**
	 * Resets the GUI to it's default values and default parameters in the work
	 * package.
	 */
	void reset();

	/**
	 * Custom routine for disposing the GUI, may be overridden.
	 */
	void destroy();

	/**
	 * Disables inputs on the GUI elements.
	 */
	void disableInputs();

	/**
	 * Enables or re-enables inputs on the GUI elements.
	 */
	void enableInputs();

	/**
	 * Sets the name of the operator to this GUI.
	 * 
	 * @param opName
	 */
	void setOpName(String opName);

	/**
	 * Gets the operator's name associated with this GUI.
	 * 
	 * @return the opName
	 */
	String getOpName();
}
