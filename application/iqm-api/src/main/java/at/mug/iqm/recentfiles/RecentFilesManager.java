package at.mug.iqm.recentfiles;

/*
 * #%L
 * Project: IQM - API
 * File: RecentFilesManager.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.commons.util.XMLGregCalUtil;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.recentfiles.jaxb.ObjectFactory;
import at.mug.iqm.recentfiles.jaxb.RecentFiles;

/**
 * This class manages the recently opened files of IQM. Properties of each file
 * are kept in an XML file.
 * 
 * @author Philipp Kainz
 * @since 3.1
 * 
 */
public class RecentFilesManager {

	// private logger
	private static final Logger logger = LogManager.getLogger(RecentFilesManager.class);

	private static RecentFilesManager currentManager = null;

	// hard wired file names
	private File path = ConfigManager.getCurrentInstance().getConfPath(); 
	private String fileName = "RecentFiles.xml";
	private final String xmlSchemaLocation = "/xsd/RecentFiles.xsd";

	private File xmlFile;
	private InputStream xmlSchemaFileIS;

	// JAXB objects
	private JAXBContext jaxbContext;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	// the java object
	private RecentFiles recentFiles;
	private ObjectFactory of = new ObjectFactory();

	/**
	 * The constructor.
	 */
	public RecentFilesManager() {
		setCurrentInstance(this);
		this.xmlFile = new File(this.path + File.separator + this.fileName);
		// try to write the file if it doesn't exist
		if (!xmlFile.exists()){
			ObjectFactory of = new ObjectFactory();
			RecentFiles iRF = of.createRecentFiles();
			iRF.setFileList(of.createFileList());
			recentFiles = iRF;
			setLastUpdate();
		}
	}

	/**
	 * This method adds a recently opened file to the top of the existing ones.
	 * Files are ordered by access/modification date.
	 * 
	 * @param f
	 *            the file
	 * @param dataType
	 *            {@link DataType} of the file
	 * @param maxItems
	 *            maximum number of items to keep
	 * @throws Exception
	 */
	public void addNewRecentFile(File f, DataType dataType, int maxItems)
			throws Exception {
		// read files and order by modification date (or id) descending
		readXML();

		// get a reference to the list
		List<at.mug.iqm.recentfiles.jaxb.File> allFiles = recentFiles
				.getFileList().getFile();

		// ##############################################
		// construct new file and set properties
		at.mug.iqm.recentfiles.jaxb.File jaxbFile = new at.mug.iqm.recentfiles.jaxb.File();

		// filename
		jaxbFile.setName(f.getName());

		// file path
		jaxbFile.setPath(f.getParentFile().getAbsolutePath());

		// data type
		jaxbFile.setDataType(dataType.toString());

		// check, if the file is already in the list and return the 
		// reference, if true
		at.mug.iqm.recentfiles.jaxb.File existingFile = exist(jaxbFile);
		
		if (existingFile != null){
			// the file exists
			// update time stamp in the collection
			existingFile.setLastAccess(XMLGregCalUtil
				.asXMLGregorianCalendar(new Date()));
		}else{
			// write last access of new file
			jaxbFile.setLastAccess(XMLGregCalUtil
					.asXMLGregorianCalendar(new Date()));
			
			// add to all files
			allFiles.add(jaxbFile);
		}

		// sort the list according to the last access date
		Collections.sort(allFiles,
				new Comparator<at.mug.iqm.recentfiles.jaxb.File>() {

					// sort
					public int compare(at.mug.iqm.recentfiles.jaxb.File f1,
							at.mug.iqm.recentfiles.jaxb.File f2) {

						int cmp = -f1.getLastAccess().compare(
								f2.getLastAccess());
						return cmp;
					}
				});

		// re-number file ids
		int id = 0;
		for (at.mug.iqm.recentfiles.jaxb.File fi : allFiles) {
			fi.setFileId(id++);
		}

		// kick out the files having indices > maxItem
		if (allFiles.size() > maxItems) {
			allFiles.subList(maxItems, allFiles.size()).clear();
		}

		// set file access time stamp and save
		this.setLastUpdate();
	}

	/**
	 * Search for an existing file in the list.
	 * 
	 * @param jaxbFile
	 * @return a {@link at.mug.iqm.recentfiles.jaxb.File} object
	 */
	private at.mug.iqm.recentfiles.jaxb.File exist(at.mug.iqm.recentfiles.jaxb.File jaxbFile) {

		// get the properties for comparison
		String name = jaxbFile.getName();
		String path = jaxbFile.getPath();
		String dtype = jaxbFile.getDataType();

		for (at.mug.iqm.recentfiles.jaxb.File f : recentFiles.getFileList()
				.getFile()) {
			if (f.getName().equals(name) && f.getPath().equals(path)
					&& f.getDataType().equals(dtype))
				return f;
		}

		return null;
	}

	/**
	 * This method erases the list of latest files.
	 * 
	 * @throws Exception
	 */
	public void clearRecentFiles() throws Exception {
		// read XML file
		readXML();

		// delete all entries in the file and update the file
		this.recentFiles.setFileList(of.createFileList());
		this.setLastUpdate();
	}
	
	/**
	 * Gets the recent file list.
	 * 
	 * @return the Java object of the file
	 */
	public RecentFiles getRecentFiles() {
		return recentFiles;
	}

	/**
	 * Validates the XML file against the defined schema.
	 */
	private void validate() {
		Schema schema;
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			this.jaxbContext = JAXBContext.newInstance(RecentFiles.class
					.getPackage().getName());

			this.xmlSchemaFileIS = RecentFilesManager.class
					.getResourceAsStream(this.xmlSchemaLocation);

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
	 * Public delegate method for reading the xml file.
	 * @throws JAXBException
	 */
	public void read() throws JAXBException{
		readXML();
	}

	/**
	 * Loads the list of recent files from an existing XML file. The file is
	 * validated against the corresponding XML schema before loading.
	 * 
	 * @throws JAXBException
	 *             - if the XML file does not comply with the schema
	 */
	protected void readXML() throws JAXBException {
		// load from XML bean using ObjectFactory
		logger.debug("Loading recent file list from file "
				+ this.xmlFile.toString() + "...");
		this.validate();
		this.recentFiles = (RecentFiles) unmarshaller.unmarshal(this.xmlFile);
	}

	/**
	 * Writes an XML file to the specified path in the file system.
	 */
	protected synchronized void writeXML() {
		logger.debug("Writing recent files...");

		OutputStreamWriter out;
		try {
			out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8");
			try {
				this.jaxbContext = JAXBContext.newInstance(recentFiles
						.getClass().getPackage().getName());

				this.marshaller = this.jaxbContext.createMarshaller();
				this.marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						true);
				this.marshaller.marshal(recentFiles, out);
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
	 * Writes an initial XML file to the specified path in the file system.
	 */
	protected synchronized void writeInitialXML(RecentFiles empty) {
		logger.debug("Writing configuration...");

		OutputStreamWriter out;
		try {
			out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8");
			try {
				this.jaxbContext = JAXBContext.newInstance(empty
						.getClass().getPackage().getName());

				this.marshaller = this.jaxbContext.createMarshaller();
				this.marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						true);
				this.marshaller.marshal(empty, out);
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
	 * Each time the configuration is updated, write the time stamp too.
	 */
	private void setLastUpdate() {
		this.recentFiles.setLastUpdate(XMLGregCalUtil
				.asXMLGregorianCalendar(new Date()));
		this.writeXML();
	}

	public static RecentFilesManager getCurrentInstance() {
		if (currentManager == null){
			new RecentFilesManager();
		}
		return currentManager;
	}

	private static void setCurrentInstance(RecentFilesManager arg) {
		RecentFilesManager.currentManager = arg;
	}

	public static void main(String[] args) {
		RecentFilesManager mgr = new RecentFilesManager();
		try {
			mgr.addNewRecentFile(new File("/Users/phil/foo.c"), DataType.IMAGE, 5);

			//mgr.clearRecentFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
