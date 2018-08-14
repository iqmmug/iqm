package at.mug.iqm.core.registry;

/*
 * #%L
 * Project: IQM - Application Core
 * File: OperatorRegistry.java
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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.exception.OperatorAlreadyRegisteredException;
import at.mug.iqm.api.operator.IOperator;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorGUI;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.operator.IOperatorValidator;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.StackProcessingType;
import at.mug.iqm.commons.util.DialogUtil;

/**
 * The implementation of the {@link IOperatorRegistry}.
 * <p>
 * The <code>OperatorRegistry</code> is designed as <code>singleton</code>.
 * 
 * @author Philipp Kainz
 * 
 */
public class OperatorRegistry implements IOperatorRegistry {

	private static final Logger logger = Logger
			.getLogger(OperatorRegistry.class);

	/**
	 * The operator names.
	 */
	private ArrayList<String> names;

	/**
	 * The descriptor object associated with the name.
	 */
	private ArrayList<Class<IOperatorDescriptor>> descriptors;
	/**
	 * The operator object associated with the name.
	 */
	private ArrayList<Class<IOperator>> operators;
	/**
	 * The operator validator object associated with the name.
	 */
	private ArrayList<Class<IOperatorValidator>> validators;
	/**
	 * The operator GUI object associated with the name.
	 */
	private ArrayList<Class<IOperatorGUI>> guis;
	/**
	 * The operator type object associated with the name.
	 */
	private ArrayList<OperatorType> types;
	/**
	 * The stack processing type associated with the name.
	 */
	private ArrayList<StackProcessingType> stackProcessingTypes;

	/**
	 * The default constructor is set private since this object is a singleton.
	 * <p>
	 * The reference to the operator registry can be retrieved using
	 * {@link #getInstance()}.
	 */
	private OperatorRegistry() {

		names = new ArrayList<String>();

		descriptors = new ArrayList<Class<IOperatorDescriptor>>();
		operators = new ArrayList<Class<IOperator>>();
		validators = new ArrayList<Class<IOperatorValidator>>();
		guis = new ArrayList<Class<IOperatorGUI>>();
		types = new ArrayList<OperatorType>();
		stackProcessingTypes = new ArrayList<StackProcessingType>();

		Application.setOperatorRegistry(this);
	}

	/**
	 * Returns the reference to this singleton.
	 * 
	 * @return the singleton object of the registry
	 */
	public static IOperatorRegistry getInstance() {
		IOperatorRegistry opReg = Application.getOperatorRegistry();
		if (opReg == null) {
			new OperatorRegistry();
		}
		return opReg;
	}

	/**
	 * Gets the index of a given operator name from the registry.
	 * 
	 * @param name
	 * @return the index in the array
	 * @throws NoSuchElementException
	 *             if no such name is found in the registry
	 */
	public int indexOf(String name) throws NoSuchElementException {
		int index = this.names.indexOf(name);
		if (index == -1) {
			throw new NoSuchElementException();
		}
		return this.names.indexOf(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void register(String name, Class opDesc, Class op, Class opGUI,
			Class opVal, OperatorType type,
			StackProcessingType stackProcessingType) {

		// check for an already registered operator with the given name
		if (this.names.contains(name)) {
			Class operator = getOperator(name);
			try {
				throw new OperatorAlreadyRegisteredException("Operator ["
						+ operator + "] is already associated with the name ["
						+ name + "]. Please choose a different name. \n"
						+ "Hint: Unique operator names are case sensitive.");
			} catch (OperatorAlreadyRegisteredException e) {
				DialogUtil.getInstance().showErrorMessage(
						"This name is already used for an operator!", e);
			}
		} else {
			this.names.add(name);
			this.descriptors.add(opDesc);
			this.operators.add(op);
			this.guis.add(opGUI);
			this.validators.add(opVal);
			this.types.add(type);
			this.stackProcessingTypes.add(stackProcessingType);
		}
	}

	@Override
	public void updateRegistryEntry(String name) throws NoSuchElementException {

		logger.debug("Updating registry entry for operator name [" + name
				+ "].");

		Class<IOperatorDescriptor> desc = getDescriptor(name);

		// unregister
		unRegister(name);

		// use reflection to call the descriptor's register method again
		try {
			Method method = desc.getDeclaredMethod("register",
					(Class<?>[]) null);
			method.setAccessible(true);
			method.invoke(null, (Object[]) null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unRegister(String name) throws NoSuchElementException {
		int index = indexOf(name);
		if (index == -1) {
			throw new NoSuchElementException(
					"There is no operator registered with the name [" + name
							+ "]!");
		}
		this.names.remove(index);
		this.descriptors.remove(index);
		this.operators.remove(index);
		this.validators.remove(index);
		this.guis.remove(index);
		this.types.remove(index);
	}

	@Override
	public Class<IOperatorDescriptor> getDescriptor(String name)
			throws NoSuchElementException {
		return this.descriptors.get(indexOf(name));
	}

	@Override
	public Class<IOperator> getOperator(String name)
			throws NoSuchElementException {
		return this.operators.get(indexOf(name));
	}

	@Override
	public Class<IOperatorGUI> getOperatorGUI(String name)
			throws NoSuchElementException {
		return this.guis.get(indexOf(name));
	}

	@Override
	public Class<IOperatorValidator> getOperatorValidator(String name)
			throws NoSuchElementException {
		return this.validators.get(indexOf(name));
	}

	@Override
	public OperatorType getType(String name) throws NoSuchElementException {
		return this.types.get(indexOf(name));
	}

	@Override
	public List<String> getNames() {
		return this.names;
	}

	@Override
	public StackProcessingType getStackProcessingType(String name)
			throws NoSuchElementException {
		return this.stackProcessingTypes.get(indexOf(name));
	}

	@Override
	public boolean isRegistered(String name){
		return this.names.contains(name)?true:false;		
	}
}
