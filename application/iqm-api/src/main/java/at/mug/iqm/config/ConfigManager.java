package at.mug.iqm.config;

/*
 * #%L
 * Project: IQM - API
 * File: ConfigManager.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.filechooser.FileSystemView;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.exception.EnvVariableNotFoundException;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.XMLGregCalUtil;
import at.mug.iqm.config.jaxb.Annotations;
import at.mug.iqm.config.jaxb.Application;
import at.mug.iqm.config.jaxb.CleanerTask;
import at.mug.iqm.config.jaxb.Gui;
import at.mug.iqm.config.jaxb.IQMConfig;
import at.mug.iqm.config.jaxb.MemoryMonitor;
import at.mug.iqm.config.jaxb.ObjectFactory;
import at.mug.iqm.config.jaxb.Paths;

/**
 * This class is responsible for loading the configuration or writing the
 * defaults.
 * 
 * @author Philipp Kainz
 */
public class ConfigManager {
	// class specific logger
	private static final Logger logger = LogManager.getLogger(ConfigManager.class);

	private static ConfigManager currentConfigManager = null;

	// class variable declaration
	public static final String USER_HOME = System.getProperty("user.home");

	public static final File DEFAULT_IQM_ROOT_DIR = new File(USER_HOME
			+ IQMConstants.FILE_SEPARATOR + "IQM");
	public static final File DEFAULT_IQM_TEMP_DIR = new File(
			DEFAULT_IQM_ROOT_DIR + IQMConstants.FILE_SEPARATOR + "temp");
	public static final File DEFAULT_IQM_CONF_DIR = new File(
			DEFAULT_IQM_ROOT_DIR + IQMConstants.FILE_SEPARATOR + "conf");
	public static final File DEFAULT_IQM_SCRIPT_DIR = new File(
			DEFAULT_IQM_ROOT_DIR + IQMConstants.FILE_SEPARATOR + "script");
	private final String configFileName = "IQMConfig.xml";
	private final String configSchema = "/xsd/IQMConfig.xsd";

	// the default config file is placed within the packed jar file
	private InputStream defaultConfigFileIS;

	// an external file for configuration
	private File xmlConfigFile;
	private File xmlSchemaFile;
	private InputStream xmlSchemaFileIS;

	// JAXB objects
	private JAXBContext jaxbContext;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	private IQMConfig iqmConfiguration;

	private ArrayList<String> errorMsgs = new ArrayList<String>();

	/**
	 * Create a new instance of the configuration manager.
	 */
	private ConfigManager() throws JAXBException {
		logger.debug("Initializing configuration in " + this.getClass() + "...");

		this.loadDefaultConfiguration();

		logger.info("User root IQM directory: " + DEFAULT_IQM_ROOT_DIR);
		logger.info("Default configuration file stream (packed in JAR): "
				+ this.defaultConfigFileIS);

		// try to create all necessary directories
		// 1. temporary
		// 2. configuration
		// 3. script
		// create the default temporary directory
		try {
			// if the directory does not already exist
			if (DEFAULT_IQM_TEMP_DIR.mkdirs()) {
				logger.info("Temporary directory '" + DEFAULT_IQM_TEMP_DIR
						+ "' has been created.");
			} else {
				logger.debug(DEFAULT_IQM_TEMP_DIR.toString()
						+ " already exists.");
			}
		} catch (SecurityException se) {
			se.printStackTrace();
			logger.error(
					"Temporary directory '"
							+ DEFAULT_IQM_TEMP_DIR
							+ "' cannot be created.\nCheck write permissions in directory.",
					se);
			DialogUtil
					.getInstance()
					.showErrorMessage(
							"Temporary directory '"
									+ DEFAULT_IQM_TEMP_DIR
									+ "' cannot be created.\nCheck write permissions in directory.",
							se);
			System.exit(-1);
		}

		// create default configuration directory
		try {
			// if the directory does not already exist
			if (DEFAULT_IQM_CONF_DIR.mkdirs()) {
				logger.info("Configuration directory '" + DEFAULT_IQM_CONF_DIR
						+ "' has been created.");
			} else {
				logger.debug(DEFAULT_IQM_CONF_DIR.toString()
						+ " already exists.");
			}
		} catch (SecurityException se) {
			se.printStackTrace();
			logger.error(
					"Configuration directory '"
							+ DEFAULT_IQM_CONF_DIR
							+ "' cannot be created.\nCheck write permissions in directory.",
					se);
			DialogUtil
					.getInstance()
					.showErrorMessage(
							"Configuration directory '"
									+ DEFAULT_IQM_CONF_DIR
									+ "' cannot be created.\nCheck write permissions in directory.",
							se);
			System.exit(-1);
		}

		// create default script directory
		try {
			// if the directory does not already exist
			if (DEFAULT_IQM_SCRIPT_DIR.mkdirs()) {
				logger.info("Script directory '" + DEFAULT_IQM_SCRIPT_DIR
						+ "' has been created.");
			} else {
				logger.debug(DEFAULT_IQM_SCRIPT_DIR.toString()
						+ " already exists.");
			}
		} catch (SecurityException se) {
			se.printStackTrace();
			logger.error(
					"Script directory '"
							+ DEFAULT_IQM_SCRIPT_DIR
							+ "' cannot be created.\nCheck write permissions in directory.",
					se);
			DialogUtil
					.getInstance()
					.showErrorMessage(
							"Script directory '"
									+ DEFAULT_IQM_SCRIPT_DIR
									+ "' cannot be created.\nCheck write permissions in directory.",
							se);
			System.exit(-1);
		}

		// check, if another config file is specified in the default path
		this.xmlConfigFile = new File(DEFAULT_IQM_CONF_DIR
				+ IQMConstants.FILE_SEPARATOR + this.configFileName);

		// if it exists, load the configuration file and validate it
		if (this.xmlConfigFile.exists()) {
			// load configuration from the existing file
			try {
				this.loadXMLConfiguration(this.xmlConfigFile);
			} catch (Exception e) {
				logger.error(
						"Sorry, but the stored file cannot be unmarshalled. "
								+ "I will write a new default configuration file.",
						e);
				this.xmlConfigFile
						.renameTo(new File(this.xmlConfigFile.getPath()
								+ "_CORRUPT_"
								+ new SimpleDateFormat("yyyy-MM-dd")
										.format(new Date())));
			}
		} else {
			// try to write a new xml config file with the standard preferences
			this.writeDefaultXMLConfiguration();
		}
	}

	private void loadDefaultConfiguration() throws JAXBException {
		logger.debug("Loading and validating default configuration.");
		this.defaultConfigFileIS = ConfigManager.class
				.getResourceAsStream("/xml/" + this.configFileName);

		this.loadXMLConfiguration(this.defaultConfigFileIS);
	}

	/**
	 * Collects configuration violations and stores it in a
	 * <code>List&lt;String&gt;</code> of error messages. If there are some
	 * errors, throw new <code>Exception</code> and print error messages to log
	 * file.
	 * 
	 * @throws Exception
	 *             - if the configuration is invalid
	 */
	public void validateAndSetConfiguration() throws Exception {
		// rules for the application's configuration configuration
		// set the configuration
		// 1. root dir
		// 2. temp dir
		// 3. conf dir
		// 4. script dir
		// 5. images dir

		// set the default root directory
		try {
			File defaultIqmRoot = DEFAULT_IQM_ROOT_DIR;

			// get configuration entry
			String iqmRootPath = this.iqmConfiguration.getApplication()
					.getPaths().getRoot();
			String resolved = "";
			if (iqmRootPath != null && !iqmRootPath.isEmpty()) {
				resolved = this.parseEnvVars(iqmRootPath);
				defaultIqmRoot = new File(resolved);
			}

			logger.info("User root IQM directory: " + defaultIqmRoot);
			// set globally available default directory
			ConfigManager.getCurrentInstance().setRootPath(defaultIqmRoot);
		} catch (EnvVariableNotFoundException e) {
			errorMsgs.add(e + "");
		}

		// set the default temp directory
		try {
			File defaultIqmTemp = DEFAULT_IQM_TEMP_DIR;

			// get configuration entry
			String iqmTempPath = this.iqmConfiguration.getApplication()
					.getPaths().getTemp();
			String resolved = "";
			if (iqmTempPath != null && !iqmTempPath.isEmpty()) {
				resolved = this.parseEnvVars(iqmTempPath);
				defaultIqmTemp = new File(resolved);
			}

			logger.info("Temporary user IQM directory: " + DEFAULT_IQM_CONF_DIR);
			// set globally available default directory
			ConfigManager.getCurrentInstance().setTempPath(defaultIqmTemp);
		} catch (EnvVariableNotFoundException e) {
			errorMsgs.add(e + "");
		}

		// set the default configuration directory
		try {
			File defaultIqmConf = DEFAULT_IQM_CONF_DIR;

			// get configuration entry
			String iqmConfPath = this.iqmConfiguration.getApplication()
					.getPaths().getConf();
			String resolved = "";
			if (iqmConfPath != null && !iqmConfPath.isEmpty()) {
				resolved = this.parseEnvVars(iqmConfPath);
				defaultIqmConf = new File(resolved);
			}

			logger.info("IQM configuration directory: " + DEFAULT_IQM_CONF_DIR);
			// set globally available default directory
			ConfigManager.getCurrentInstance().setConfPath(defaultIqmConf);
		} catch (EnvVariableNotFoundException e) {
			errorMsgs.add(e + "");
		}

		// set the default script directory
		try {
			File defaultIqmScript = DEFAULT_IQM_SCRIPT_DIR;

			// get script entry
			String iqmScriptPath = this.iqmConfiguration.getApplication()
					.getPaths().getScript();
			String resolved = "";
			if (iqmScriptPath != null && !iqmScriptPath.isEmpty()) {
				resolved = this.parseEnvVars(iqmScriptPath);
				defaultIqmScript = new File(resolved);
			}

			logger.info("IQM script directory: " + DEFAULT_IQM_SCRIPT_DIR);
			// set globally available default directory
			ConfigManager.getCurrentInstance().setScriptPath(defaultIqmScript);
		} catch (EnvVariableNotFoundException e) {
			errorMsgs.add(e + "");
		}

		// set the default image directory
		try {
			// get default from file system view
			File defaultImageDirectory = new File(FileSystemView
					.getFileSystemView().getDefaultDirectory().toString());

			// get configuration entry
			String imagePath = this.iqmConfiguration.getApplication()
					.getPaths().getImages();
			String resolved = "";
			if (imagePath != null && !imagePath.isEmpty()) {
				resolved = this.parseEnvVars(imagePath);
				defaultImageDirectory = new File(resolved);
			}

			logger.debug("Current image/file path: " + defaultImageDirectory);

			// set globally available default directory
			ConfigManager.getCurrentInstance().setImagePath(
					defaultImageDirectory);
		} catch (EnvVariableNotFoundException e) {
			errorMsgs.add(e + "");
		}

		// set time stamp for last update
		this.iqmConfiguration.setLastUpdate(XMLGregCalUtil
				.asXMLGregorianCalendar(new Date()));

		// check if errors exist
		if (!errorMsgs.isEmpty()) {
			// write all messages to log and console (error stream)
			for (String s : errorMsgs) {
				System.err.println(s);
				logger.error(s);
			}

			// throw new exception and catch it in calling method
			// (ProxyContextListener)
			throw new Exception(
					"Misconfigured 'IQMConfig.xml'. Check log file for details.");
		}
		// otherwise finish
		else {
			logger.debug("Done.");
		}
	}

	/**
	 * Loads a configuration from an existing XML file. The file is validated
	 * against the corresponding XML schema before loading.
	 * 
	 * @throws JAXBException
	 *             - if the XML file does not comply with the schema
	 * 
	 */
	private void loadXMLConfiguration(File xmlFile) throws JAXBException,
			Exception {
		// load from XML bean (IQMConfig) using ObjectFactory
		logger.debug("Loading configuration from file " + xmlFile.toString()
				+ "...");
		this.validate();
		this.iqmConfiguration = (IQMConfig) unmarshaller.unmarshal(xmlFile);
	}

	/**
	 * Loads a configuration from an existing XML file. The file is validated
	 * against the corresponding XML schema before loading.
	 * 
	 * @throws JAXBException
	 *             - if the XML file does not comply with the schema
	 * 
	 */
	private void loadXMLConfiguration(InputStream is) throws JAXBException {
		// load from XML bean (IQMConfig) using ObjectFactory
		logger.debug("Loading configuration from input stream " + is.toString()
				+ "...");
		this.validate();
		this.iqmConfiguration = (IQMConfig) unmarshaller.unmarshal(is);
	}

	/**
	 * Validates the configuration file against the defined schema.
	 */
	private void validate() {
		Schema schema;
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			this.jaxbContext = JAXBContext.newInstance(IQMConfig.class
					.getPackage().getName());

			this.xmlSchemaFileIS = ConfigManager.class
					.getResourceAsStream(this.configSchema);

			schema = schemaFactory.newSchema(new StreamSource(
					this.xmlSchemaFileIS));
			this.unmarshaller = jaxbContext.createUnmarshaller();
			this.unmarshaller.setSchema(schema);
		} catch (SAXException e) {
			// log the error message
			logger.error("An error occurred: ", e);
		} catch (NullPointerException e) {
			// log the error message
			logger.error("An error occurred: ", e);
		} catch (JAXBException e) {
			// log the error message
			logger.error("An error occurred: ", e);
		}
	}

	/**
	 * Writes a new default XML configuration file to the specified path in the
	 * file system.
	 */
	private synchronized void writeDefaultXMLConfiguration() {
		logger.debug("Writing new default configuration...");

		File xmlFile = new File(DEFAULT_IQM_CONF_DIR
				+ IQMConstants.FILE_SEPARATOR + this.configFileName);
		OutputStreamWriter out;
		try {
			out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8");
			try {
				this.jaxbContext = JAXBContext.newInstance(getIQMConfig()
						.getClass().getPackage().getName());

				this.marshaller = this.jaxbContext.createMarshaller();
				this.marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						true);
				this.marshaller.marshal(getDefaultIQMConfig(), out);
			} catch (JAXBException ex) {
				logger.error(ex);
			}
			out.flush();
			out.close();
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	/**
	 * Store the current configuration to the config file.
	 */
	public void write() {
		writeXMLConfiguration();
	}

	/**
	 * Writes an XML configuration file to the specified path in the file
	 * system.
	 */
	private synchronized void writeXMLConfiguration() {
		logger.debug("Writing configuration...");

		File xmlFile = new File(DEFAULT_IQM_CONF_DIR
				+ IQMConstants.FILE_SEPARATOR + this.configFileName);
		OutputStreamWriter out;
		try {
			out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8");
			try {
				this.jaxbContext = JAXBContext.newInstance(getIQMConfig()
						.getClass().getPackage().getName());

				this.marshaller = this.jaxbContext.createMarshaller();
				this.marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						true);
				this.marshaller.marshal(getIQMConfig(), out);
			} catch (JAXBException ex) {
				logger.error(ex);
			}
			out.flush();
			out.close();
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	/**
	 * @return the xmlSchemaFile
	 */
	public File getXmlSchemaFile() {
		return xmlSchemaFile;
	}

	/**
	 * @param xmlSchemaFile
	 *            the xmlSchemaFile to set
	 */
	public void setXmlSchemaFile(File xmlSchemaFile) {
		this.xmlSchemaFile = xmlSchemaFile;
	}

	/**
	 * Gets the applications configuration, it constructs a default one, if it
	 * is null.
	 * 
	 * @return an IQMConfig object
	 */
	public IQMConfig getIQMConfig() {
		if (this.iqmConfiguration == null) {
			this.iqmConfiguration = this.getDefaultIQMConfig();
		}
		return this.iqmConfiguration;
	}

	/**
	 * Constructs a default configuration with the standard values for starting
	 * up the application.
	 * 
	 * @return an IQMConfig object
	 */
	private IQMConfig getDefaultIQMConfig() {
		// construct and return a default configuration
		ObjectFactory of = new ObjectFactory();
		IQMConfig defaultConfig = of.createIQMConfig();

		// set the last update
		defaultConfig.setLastUpdate(XMLGregCalUtil
				.asXMLGregorianCalendar(new Date()));

		// construct the application object
		Application app = of.createApplication();

		// construct the paths
		Paths paths = of.createPaths();
		paths.setRoot("${user.home}" + IQMConstants.FILE_SEPARATOR + "IQM");
		paths.setTemp("${user.home}" + IQMConstants.FILE_SEPARATOR + "IQM"
				+ IQMConstants.FILE_SEPARATOR + "temp");
		paths.setConf("${user.home}" + IQMConstants.FILE_SEPARATOR + "IQM"
				+ IQMConstants.FILE_SEPARATOR + "conf");
		paths.setScript("${user.home}" + IQMConstants.FILE_SEPARATOR + "IQM"
				+ IQMConstants.FILE_SEPARATOR + "script");
		paths.setImages(FileSystemView.getFileSystemView()
				.getDefaultDirectory().toString());

		// construct the cleaner task configuration
		CleanerTask cleaner = of.createCleanerTask();
		cleaner.setName("CleanerTask");
		cleaner.setInterval(10000);

		// construct the memory monitor configuration
		MemoryMonitor memory = of.createMemoryMonitor();
		memory.setName("MemoryMonitor");
		memory.setInterval(5000);

		// construct the GUI configuration
		Gui gui = of.createGui();
		gui.setLookAndFeel("default");

		Annotations annotations = of.createAnnotations();
		annotations.setKeepExistingRois(true);
		gui.setAnnotations(annotations);

		// assemble the whole default configuration
		app.setPaths(paths);
		app.setCleanerTask(cleaner);
		app.setMemoryMonitor(memory);
		app.setGui(gui);

		defaultConfig.setApplication(app);

		return defaultConfig;
	}

	/**
	 * This method gets the names of the environment variables from a specified
	 * String <code>s</code>.
	 * 
	 * @param original
	 *            - the string from the configuration file
	 * @return a resolved pathname as String without environment variable
	 *         references
	 * @throws EnvVariableNotFoundException
	 *             if a specified environment variable cannot be found
	 */
	private String parseEnvVars(final String original)
			throws EnvVariableNotFoundException {
		ArrayList<String> envVarList = new ArrayList<String>();
		String resolved = "";

		// check condition if any variable is declared
		if (original.contains("${") && original.contains("}")) {
			// fill the list with keys
			this.extractSingleVariable(original, envVarList);

			// reassemble
			resolved = resolve(original, envVarList);

			logger.debug("Found " + envVarList.size() + " variables in "
					+ original + ": " + envVarList.toString()
					+ ". Reassembled in: " + resolved);
		} else {
			// take the specified string, if no vars are referenced
			resolved = original;
			logger.debug("No variables found in this string: " + original);
		}

		// return the resolved string
		return resolved;
	}

	/**
	 * Extract all keys from referenced environment variables from a given
	 * String <code>s</code>. This method works recursively
	 * 
	 * @param s
	 *            - the String to
	 * @param envVarList
	 * @return a string
	 */
	private String extractSingleVariable(final String s,
			ArrayList<String> envVarList) {
		String result = null;
		if (s != null) {
			// get index positions
			int startIdx = s.indexOf("${") + 2;
			int endIdx = s.indexOf("}", startIdx);

			// check for a closing } after ${
			if (endIdx > startIdx) {
				// get env var from system environment
				String currVar = s.substring(startIdx, endIdx);
				envVarList.add(currVar);

				// from right after the '}' until the end
				String postfix = s.substring(endIdx + 1);

				// recurse with the remaining postfix
				extractSingleVariable(postfix, envVarList);
			} else {
				logger.debug("No environment variable references found in this string: "
						+ ((s.isEmpty()) ? "<emptyString>" : s));
				result = null;
			}
		}
		// collect the results
		return result;
	}

	/**
	 * Resolves the path from a list of environment variables.
	 * 
	 * @param original
	 *            - the original string
	 * @param envVars
	 *            - the list of variable keys
	 * @return a reassembled, fully qualified path name
	 * @throws EnvVariableNotFoundException
	 *             - if one of the keys is not available
	 */
	private String resolve(String original, ArrayList<String> envVars)
			throws EnvVariableNotFoundException {
		for (String key : envVars) {
			String var = System.getProperty(key);
			if (var != null) {
				// replace matching patterns
				original = original.replace("${" + key + "}", var);
			} else {
				throw new EnvVariableNotFoundException(
						"Cannot find environment variable for key [" + key
								+ "].");
			}
		}
		return original;
	}

	/**
	 * Get the currently configured path to the script directory.
	 * 
	 * @return scriptPath
	 */
	public File getScriptPath() {
		return new File(this.iqmConfiguration.getApplication().getPaths()
				.getScript());
	}

	/**
	 * Get the currently configured temporary file path.
	 * 
	 * @return tempPath
	 */
	public File getTempPath() {
		return new File(this.iqmConfiguration.getApplication().getPaths()
				.getTemp());
	}

	/**
	 * Get the currently configured image path as file object.
	 * 
	 * @return imagePath
	 */
	public File getImagePath() {
		return new File(this.iqmConfiguration.getApplication().getPaths()
				.getImages());
	}

	/**
	 * Get the currently configured root file path as file object.
	 * 
	 * @return rootPath
	 */
	public File getRootPath() {
		return new File(this.iqmConfiguration.getApplication().getPaths()
				.getRoot());
	}

	/**
	 * Get the currently configured configuration file path as file object.
	 * 
	 * @return confPath
	 */
	public File getConfPath() {
		return new File(this.iqmConfiguration.getApplication().getPaths()
				.getConf());
	}

	/**
	 * Updates the current configuration file with the specified
	 * <code>path</code> for temporary files.
	 * 
	 * @param path
	 *            - temporary file store
	 */
	public void setTempPath(String path) {
		this.iqmConfiguration.getApplication().getPaths().setTemp(path);
		this.setLastUpdate();
	}

	/**
	 * Updates the current configuration file with the specified
	 * <code>path</code> for temporary files.
	 * 
	 * @param path
	 *            - temporary file store
	 */
	public void setTempPath(File path) {
		this.iqmConfiguration.getApplication().getPaths()
				.setTemp(path.toString());
		this.setLastUpdate();
	}

	/**
	 * Updates the current configuration file with the specified
	 * <code>path</code> for the images.
	 * 
	 * @param path
	 *            - last used image path
	 */
	public void setImagePath(String path) {
		this.iqmConfiguration.getApplication().getPaths().setImages(path);
		this.setLastUpdate();
	}

	/**
	 * Updates the current configuration file with the specified
	 * <code>path</code> for the images.
	 * 
	 * @param path
	 *            - last used image path
	 */
	public void setImagePath(File path) {
		this.iqmConfiguration.getApplication().getPaths()
				.setImages(path.toString());
		this.setLastUpdate();
	}

	/**
	 * Updates the current configuration file with the specified
	 * <code>path</code> for the IQM root.
	 * 
	 * @param path
	 *            - path to the root where the application has writing
	 *            permissions
	 */
	public void setRootPath(String path) {
		this.iqmConfiguration.getApplication().getPaths().setRoot(path);
		this.setLastUpdate();
	}

	/**
	 * Updates the current configuration file with the specified
	 * <code>path</code> for the IQM root.
	 * 
	 * @param path
	 *            - path to the root where the application has writing
	 *            permissions
	 */
	public void setRootPath(File path) {
		this.iqmConfiguration.getApplication().getPaths()
				.setRoot(path.toString());
		this.setLastUpdate();
	}

	/**
	 * Updates the current configuration file with the specified
	 * <code>path</code> for the IQM configuration.
	 * 
	 * @param path
	 *            - path to the configuration files where the application has
	 *            writing permissions
	 */
	public void setConfPath(String path) {
		this.iqmConfiguration.getApplication().getPaths().setConf(path);
		this.setLastUpdate();
	}

	/**
	 * Updates the current configuration file with the specified
	 * <code>path</code> for the IQM configuration.
	 * 
	 * @param path
	 *            - path to the configuration files where the application has
	 *            writing permissions
	 */
	public void setConfPath(File path) {
		this.iqmConfiguration.getApplication().getPaths()
				.setConf(path.toString());
		this.setLastUpdate();
	}

	/**
	 * Updates the current configuration file with the specified
	 * <code>path</code> for the IQM configuration.
	 * 
	 * @param path
	 *            - path to the Groovy script files where the application has
	 *            writing permissions
	 */
	public void setScriptPath(String path) {
		this.iqmConfiguration.getApplication().getPaths().setScript(path);
		this.setLastUpdate();
	}

	/**
	 * Updates the current configuration file with the specified
	 * <code>path</code> for the IQM configuration.
	 * 
	 * @param path
	 *            - path to the Groovy script files where the application has
	 *            writing permissions
	 */
	public void setScriptPath(File path) {
		this.iqmConfiguration.getApplication().getPaths()
				.setScript(path.toString());
		this.setLastUpdate();
	}

	/**
	 * Get a flag, whether or not the existing ROIs should be kept while drawing
	 * new ones.
	 * <p>
	 * If the config file does not contain any annotation tags, the default
	 * values will be written to the file.
	 * 
	 * @return <code>true</code> or <code>false</code>
	 */
	public boolean getKeepExistingROIs() {
		try {
			return this.iqmConfiguration.getApplication().getGui()
					.getAnnotations().isKeepExistingRois();
		} catch (NullPointerException e) {
			// if no annotation tag is present, create default value and write
			// the file
			Annotations annotations = new ObjectFactory().createAnnotations();
			annotations.setKeepExistingRois(true);
			this.iqmConfiguration.getApplication().getGui()
					.setAnnotations(annotations);
			write();
			return this.iqmConfiguration.getApplication().getGui()
					.getAnnotations().isKeepExistingRois();
		}
	}

	/**
	 * Set the flag, whether or not the existing ROIs should be kept while
	 * drawing new ones.
	 * 
	 * @param value
	 */
	public void setKeepExistingROIs(boolean value) {
		this.iqmConfiguration.getApplication().getGui().getAnnotations()
				.setKeepExistingRois(value);
		this.setLastUpdate();
	}

	/**
	 * Each time the configuration is updated, write the time stamp too.
	 */
	private void setLastUpdate() {
		this.iqmConfiguration.setLastUpdate(XMLGregCalUtil
				.asXMLGregorianCalendar(new Date()));
		this.writeXMLConfiguration();
	}

	public static ConfigManager getCurrentInstance() {
		if (ConfigManager.currentConfigManager == null) {
			try {
				currentConfigManager = new ConfigManager();
			} catch (JAXBException e) {
				DialogUtil.getInstance().showErrorMessage(e.getMessage(), e,
						true);
			}
		}
		return currentConfigManager;
	}

	public File getDefaultIqmConfPath() {
		return DEFAULT_IQM_CONF_DIR;
	}

	public File getDefaultIqmScriptPath() {
		return DEFAULT_IQM_SCRIPT_DIR;
	}

	public File getDefaultIqmRootPath() {
		return DEFAULT_IQM_ROOT_DIR;
	}

	public File getDefaultIqmTempPath() {
		return DEFAULT_IQM_TEMP_DIR;
	}

	public String getDefaultUserHome() {
		return USER_HOME;
	}
}
