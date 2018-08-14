package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: IOperatorRegistry.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The OperatorRegistry maintains a set of {@link ArrayList}s of {@link Class}es
 * for
 * <ul>
 * <li><code>names</code>
 * <li><code>IOperatorDescriptor</code>
 * <li><code>IOperator</code>
 * <li><code>IOperatorGUI</code>
 * <li><code>IOperatorValidator</code>
 * </ul>
 * 
 * <p>
 * The list of <code>names</code> contains the indices of all other lists'
 * elements corresponding to the specified unique operator name.<br>
 * E.g. if "Operator1" is added to the registry at index position 0, the
 * {@link IOperatorDescriptor} of "Operator1" goes to the index position 0 of
 * the {@link IOperatorDescriptor} list, and so on.
 * <p>
 * If a specific implementation’s class name is required, any instance can look
 * up the class name e.g. of an operator’s descriptor in the
 * <code>OperatorRegistry</code> using
 * <code>OperatorRegistry.getInstance().getDescriptor(String)</code>.
 * <p>
 * 
 * @author Philipp Kainz
 * 
 */
public interface IOperatorRegistry {

	/**
	 * Adds a new operator to the registry using the unique name associated with
	 * the {@link IOperatorDescriptor}.
	 * 
	 * @param name
	 * @param opDesc
	 * @param op
	 * @param opGUI
	 * @param opVal
	 * @param type
	 * @param stackProcessingType
	 */
	@SuppressWarnings("rawtypes")
	void register(String name, Class opDesc, Class op, Class opGUI,
			Class opVal, OperatorType type,
			StackProcessingType stackProcessingType);

	/**
	 * Removes an operator from the registry using the unique name.
	 * 
	 * @param name
	 * @throws NoSuchElementException
	 *             if the provided name is not found in the registry
	 */
	void unRegister(String name) throws NoSuchElementException;

	/**
	 * Gets the descriptor of an operator by name.
	 * 
	 * @param name the operator name
	 * @return the {@link Class} of the descriptor
	 * @throws NoSuchElementException
	 *             if the provided name is not found in the registry
	 */
	Class<IOperatorDescriptor> getDescriptor(String name)
			throws NoSuchElementException;

	/**
	 * Gets an operator by name.
	 * 
	 * @param name the operator name
	 * @return the {@link Class} of the operator
	 * @throws NoSuchElementException
	 *             if the provided name is not found in the registry
	 */
	Class<IOperator> getOperator(String name) throws NoSuchElementException;

	/**
	 * Gets the GUI of an operator by name.
	 * 
	 * @param name the name of the operator
	 * @return the {@link Class} of the operator GUI
	 * @throws NoSuchElementException
	 *             if the provided name is not found in the registry
	 */
	Class<IOperatorGUI> getOperatorGUI(String name)
			throws NoSuchElementException;

	/**
	 * Gets the validator of an operator by name.
	 * 
	 * @param name the name of the operator
	 * @return the {@link Class} of the operator validator
	 * @throws NoSuchElementException
	 *             if the provided name is not found in the registry
	 */
	Class<IOperatorValidator> getOperatorValidator(String name)
			throws NoSuchElementException;

	/**
	 * Gets the type of an operator by name.
	 * 
	 * @param name
	 * @return the {@link OperatorType}
	 */
	OperatorType getType(String name);

	/**
	 * Gets the {@link StackProcessingType} of an operator by name.
	 * 
	 * @param name
	 * @return the {@link StackProcessingType}
	 */
	StackProcessingType getStackProcessingType(String name);

	/**
	 * Gets the list of registered names from the registry.
	 * 
	 * @return the {@link List} of operator names
	 */
	List<String> getNames();

	/**
	 * Updates a registry entry and re-registers the {@link IOperatorDescriptor}
	 * with the same name in the registry.
	 * 
	 * @param name the operator name
	 */
	void updateRegistryEntry(String name) throws NoSuchElementException;

	/**
	 * Checks whether or not a given name is registered with this registry
	 * 
	 * @param name the operator name
	 * @return <code>true</code>, if the name is registered, <code>false</code>,
	 *         if not
	 */
	boolean isRegistered(String name);

}
