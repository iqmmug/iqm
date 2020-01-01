package at.mug.iqm.api.script;

/*
 * #%L
 * Project: IQM - API
 * File: GroovyScriptConnector.java
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

import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.commons.util.DialogUtil;

/**
 * The {@link GroovyScriptConnector} is the interface to the Groovy scripting
 * language. Generally, scripts are stored as plain text files in the file
 * system and are read by the Groovy Execution Engine.
 * <p>
 * In order to use variables and object instances of the IQM framework, the
 * binding must contain all these.
 * 
 * @author Philipp Kainz
 * 
 */
public class GroovyScriptConnector {

	// private logging variable
	private static final Logger logger = LogManager.getLogger(GroovyScriptConnector.class);

	/**
	 * This variable contains all variables for the execution environment of a
	 * Groovy script.
	 */
	private DefaultScriptBinding binding;
	private List<String> paths;

	public GroovyScriptConnector() {
		// add the standard paths
		this.paths = new ArrayList<String>();
		this.paths.add(new File(".").getPath());

		// inject all necessary variables, which should be available
		// within the scripts without explicit imports
		this.binding = new DefaultScriptBinding();
	}

	/**
	 * Runs a script with the specified file name.
	 * 
	 * @param file
	 */
	public void runScript(File file) {
		File parent = file.getParentFile();
		logger.info("Running script [" + file.getPath() + "]...");
		GroovyScriptEngine gse = null;
		try {
			if (!paths.contains(parent.getPath())){
				paths.add(parent.getPath());
			}
			String[] urls = new String[paths.size()];
			paths.toArray(urls);
			gse = new GroovyScriptEngine(urls);
			gse.getConfig().setSourceEncoding("UTF-8");
			gse.run(file.getName(), binding);
		} catch (IOException ioe) {
			DialogUtil.getInstance().showErrorMessage(ioe.getMessage(), ioe,
					true);
		} catch (ResourceException re) {
			DialogUtil.getInstance()
					.showErrorMessage(re.getMessage(), re, true);
		} catch (ScriptException se) {
			DialogUtil.getInstance()
					.showErrorMessage(se.getMessage(), se, true);
		} catch (Exception e) {
			DialogUtil.getInstance().showErrorMessage(e.getMessage(), e, true);
		}
	}

	public static void main(String[] args) {
		new GroovyScriptConnector().runScript(new File(
				"/Users/phil/IQM/script/helloWorld.groovy"));
	}
}
