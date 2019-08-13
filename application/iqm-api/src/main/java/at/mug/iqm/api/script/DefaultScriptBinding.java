package at.mug.iqm.api.script;

/*
 * #%L
 * Project: IQM - API
 * File: DefaultScriptBinding.java
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

import groovy.lang.Binding;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.commons.util.DialogUtil;

/**
 * The {@link DefaultScriptBinding} class represents a base class containing all
 * default variables to be used in a script. It may be extended by custom
 * variables or properties.
 * <p>
 * Furthermore, bindings inherited from this class enable the script to access a
 * short API for several IQM commands and operator executions.
 * 
 * @author Philipp Kainz
 * 
 */
public class DefaultScriptBinding extends Binding {

	// private logging variable
	private static final Logger logger = Logger
			.getLogger(DefaultScriptBinding.class);

	/**
	 * Creates a default binding object common to all scripts.
	 */
	public DefaultScriptBinding() {
		initStandardBinding();
	}

	/**
	 * This method initializes all standard variables via injection into the
	 * Groovy binding.
	 */
	private void initStandardBinding() {
		setEnvVar("Dialog", DialogUtil.getInstance());
		setEnvVar("Tank", Application.getTank());
		setEnvVar("Manager", Application.getManager());
		setEnvVar("IQM", this);
	}

	/**
	 * Set a new variable to be available in the script API.
	 * 
	 * @param varName
	 *            the name of the variable (how it is accessed in the script)
	 * @param o
	 *            the value, i.e. the class or a concrete object/instance
	 */
	public void setEnvVar(String varName, Object o) {
		logger.debug("Setting script variable  [" + varName + "=" + o + "]");
		setVariable(varName, o);
	}

	/*
	 * ####################################################################
	 * HERE, WE ADD ALL DELEGATE METHODS, WHICH SHOULD BE AVAILABLE WITHOUT
	 * EXPLICIT IMPORTS IN THE SCRIPTS
	 */
	/**
	 * Let the user execute a shell command in the current directory.
	 * 
	 * @param command
	 * @return a reference to the executing {@link Process}
	 * @throws IOException
	 */
	public Process exec(String command) throws IOException {
		logger.info("Executing command...");
		Process proc = null;
		try {
			String line;
			Runtime r = Runtime.getRuntime();
			proc = r.exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();
		} catch (Exception e) {
			DialogUtil.getInstance().showErrorMessage(null, e, true);
		}
		return proc;
	}

	/**
	 * Lets the user execute a shell command in a specified directory.
	 * 
	 * @param command
	 * @param inDir
	 * @return a reference to the executing {@link Process}
	 * @throws IOException
	 */
	public Process exec(String command, File inDir) throws IOException {
		logger.info("Executing command...");
		Process proc = null;
		try {
			String line;
			Runtime r = Runtime.getRuntime();
			proc = r.exec(command, null, inDir);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();
		} catch (Exception e) {
			DialogUtil.getInstance().showErrorMessage(null, e, true);
		}
		return proc;
	}
}
