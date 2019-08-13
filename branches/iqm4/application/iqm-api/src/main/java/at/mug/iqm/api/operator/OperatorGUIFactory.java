package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: OperatorGUIFactory.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;

/**
 * This class produces operator GUIs for IQM operators.
 * 
 * @author Philipp Kainz
 */
public final class OperatorGUIFactory {

	/**
	 * Class specific logger.
	 */
	private static final Logger logger = Logger
			.getLogger(OperatorGUIFactory.class);

	/**
	 * This method creates an instance of a specified operator GUI.
	 * 
	 * @param name
	 *            the <code>String</code> representation of the operator GUI
	 * @return an instance of {@link IOperatorGUI}
	 */
	public static IOperatorGUI createGUI(String name) {
		try {
			logger.debug("Creating operator GUI '" + name + "'...");

			// find the class in the registry
			String className = Application.getOperatorRegistry()
					.getOperatorGUI(name).getName();

			// use reflection to instantiate the object
			Class<?> c = ClassLoader.getSystemClassLoader()
					.loadClass(className);

			IOperatorGUI opGUI = (IOperatorGUI) c.newInstance();
			opGUI.setOpName(name);
			
			return opGUI;

		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.error("No operation found with name: '" + name + "'.", e);
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("No operation found with name: '" + name + "'.", e);
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			logger.error("No operation found with name: '" + name + "'.", e);
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			logger.error("No operation found with name: '" + name + "'.", e);
			return null;
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			logger.error("No operation found with name: '" + name + "'.", e);
			return null;
		}
	}

	/**
	 * Creates a new GUI for a specified operator's name. If the work package is
	 * not passed as an argument, the method returns an empty
	 * {@link IOperatorGUI}.
	 * 
	 * @param name
	 *            the <code>String</code> representation of the operator GUI
	 * @param wp
	 *            can be <code>null</code>
	 * 
	 * @return an instance of {@link IOperatorGUI}
	 */
	public static IOperatorGUI createAndInitializeGUI(String name,
			IWorkPackage wp) {
		try {
			logger.debug("Creating and initializing operator GUI '" + name
					+ "' with default parameters...");

			// find the class in the registry
			String className = Application.getOperatorRegistry()
					.getOperatorGUI(name).getName();

			// use reflection to instantiate the object
			Class<?> c = ClassLoader.getSystemClassLoader()
					.loadClass(className);

			// try to initialize the gui with the default parameters
			IOperatorGUI opGUI = null;
			try {
				if (wp != null) {
					opGUI = (IOperatorGUI) c.getConstructor(IWorkPackage.class)
							.newInstance(wp);
				} else {
					opGUI = createGUI(name);
				}
				
				opGUI.setOpName(name);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				logger.error("No operation found with name: '" + name + "'.", e);
			} catch (SecurityException e) {
				e.printStackTrace();
				logger.error("No operation found with name: '" + name + "'.", e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				logger.error("No operation found with name: '" + name + "'.", e);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				logger.error("No operation found with name: '" + name + "'.", e);
			}
			return opGUI;

		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.error("No operation found with name: '" + name + "'.", e);
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("No operation found with name: '" + name + "'.", e);
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			logger.error("No operation found with name: '" + name + "'.", e);
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			logger.error("No operation found with name: '" + name + "'.", e);
			return null;
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			logger.error("No operation found with name: '" + name + "'.", e);
			return null;
		}

	}
}
