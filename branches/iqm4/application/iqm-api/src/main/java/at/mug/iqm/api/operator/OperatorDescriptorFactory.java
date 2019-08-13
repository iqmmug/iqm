package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: OperatorDescriptorFactory.java
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


import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;

/**
 * This class produces operator descriptors for IQM operators.
 * 
 * @author Philipp Kainz
 */
public final class OperatorDescriptorFactory{

	/**
	 * Class specific logger.
	 */
	private static final Logger logger = Logger.getLogger(OperatorDescriptorFactory.class);

	/**
	 * This method creates an instance of a specified operator.
	 * 
	 * @param name the <code>String</code> representation of the operator descriptor
	 * @return an instance of {@link IOperatorDescriptor} or <code>null</code> if no operator is
	 * found with this name in the registry
	 */
	public static IOperatorDescriptor createDescriptor(String name){
		try{
			logger.debug("Initializing operator descriptor '" + name + "'...");

			// find the class in the registry
			String className = Application.getOperatorRegistry().getDescriptor(name).getName();
			
			// use reflection to instantiate the object
			Class<?> c = ClassLoader.getSystemClassLoader().loadClass(className);
			IOperatorDescriptor descriptor = (IOperatorDescriptor) c.newInstance();

			return descriptor;

		}catch (NullPointerException e){
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


