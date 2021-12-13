package at.mug.iqm.templatemanager;

/*
 * #%L
 * Project: IQM - API
 * File: XMLPreferencesManager.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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

import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.templatemanager.jaxb.IqmOperatorDescriptor;
import at.mug.iqm.templatemanager.jaxb.IqmOperatorDescriptor.Parameters;
import at.mug.iqm.templatemanager.jaxb.IqmOperatorTemplates;
import at.mug.iqm.templatemanager.jaxb.IqmOperatorTemplates.ParameterBlock;
import at.mug.iqm.templatemanager.jaxb.ObjectFactory;

/**
 * This class is used to load and set GUI preferences and IQM-parameter-blocks.
 * 
 * @author Philipp W. <java@scaenicus.net>, Philipp Kainz
 * @version 0.0.2
 */
public final class XMLPreferencesManager {

	// class specific logger
	private static final Logger logger = LogManager.getLogger(XMLPreferencesManager.class);

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	private IqmOperatorTemplates propertyXML;

	private static XMLPreferencesManager currentPrefManager = null;

	private String pathName = "";
	private String fileName = "IQMOperatorTemplates.xml";

	/**
	 * Constructs a new JAIXMLParameterBlocks and a new IQMOperatorTemplates.xml
	 * if necessary.
	 */
	public XMLPreferencesManager(String pathName) {
//		this.getXMLDoc(); //2015-10-01 added by AH because Preferences did not work at all, 2015-10-12 obsolete change (PK)
		this.pathName = pathName + File.separator;
		File file = new File(this.pathName);
		file.mkdirs();
		File xmlFile = new File(this.pathName + this.fileName);
		if (xmlFile.exists()) {
			try {
				this.readXML(xmlFile);
			} catch (JAXBException e) {
				logger.error("An error occurred: ", e);
				// DialogUtil.getCurrentInstance().showErrorMessage(TemplateManagerResources.getMessage("application.exception.templates.validation.failed",
				// ConfigManager.getCurrentInstance().getRootPath().toString() +
				// IqmConstants.FILE_SEPARATOR + "logs"
				// + IqmConstants.FILE_SEPARATOR + "iqm.log"), e);
				// CoreTools.setCurrImgProcessFunc(null);
				return;

			} catch (NullPointerException e) {
				logger.error("An error occurred: ", e);
				// DialogUtil.getCurrentInstance().showErrorMessage(TemplateManagerResources.getMessage("application.exception.templates.validation.failed",
				// ConfigManager.getCurrentInstance().getRootPath().toString() +
				// IqmConstants.FILE_SEPARATOR + "logs"
				// + IqmConstants.FILE_SEPARATOR + "iqm.log"), e);
				// CoreTools.setCurrImgProcessFunc(null);
				return;
			}
		} else {
			getXMLDoc();
		}
		file = null;
		xmlFile = null;
	}

	/**
	 * Returns an object of the DOM-representation of the XML-file currently
	 * written. It it called by the constructor.
	 * 
	 * @return Object representing the XML.
	 */
	private IqmOperatorTemplates getXMLDoc() {
		if (this.propertyXML == null) {
			this.propertyXML = new ObjectFactory().createIqmOperatorTemplates();
		}
		return this.propertyXML;
	}

	/**
	 * Writes the IQMOperatorTemplates.xml to the "IQM"-user-folder of the
	 * application. This function is <b>synchronized</b>.
	 */
	public synchronized void writeXML() {
		// Calendar cal = Calendar.getInstance();
		// cal.setTime(new java.util.Date());
		// /*Time-Format:
		// 'F' ISO 8601 complete date formatted as "%tY-%tm-%td".
		// 'T' Time formatted for the 24-hour clock as "%tH:%tM:%tS".
		// 'H' Hour of the day for the 24-hour clock, formatted as two digits
		// with a leading zero as necessary i.e. 00 - 23.
		// 'M' Minute within the hour formatted as two digits with a leading
		// zero as necessary, i.e. 00 - 59.
		// 'S' Seconds within the minute, formatted as two digits with a leading
		// zero as necessary, i.e. 00 - 60 ("60" is a special value required to
		// support leap seconds).
		// 'L' Millisecond within the second formatted as three digits with
		// leading zeros as necessary, i.e. 000 - 999.
		// 'N' Nanosecond within the second, formatted as nine digits with
		// leading zeros as necessary, i.e. 000000000 - 999999999.
		// */
		// String fileName = String.format("%1$tF_%1$tH-%1$tM-%1$tS", cal) +
		// ".xml";

		File xmlFile = new File(this.pathName + this.fileName);
		OutputStreamWriter out;
		try {
			out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8");
			try {
				JAXBContext jaxbCtx = JAXBContext.newInstance(getXMLDoc()
						.getClass().getPackage().getName());

				this.marshaller = jaxbCtx.createMarshaller();
				this.marshaller.setProperty(
						javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); // NOI18N
				this.marshaller.setProperty(
						javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
				this.marshaller.marshal(getXMLDoc(), out);
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
	 * Reads the specified XML-file. This function is <b>synchronized</b>.
	 * 
	 * @param xmlFile
	 *            XML-file to read in.
	 * @throws JAXBException
	 */
	public synchronized void readXML(File xmlFile) throws JAXBException,
			NullPointerException {
		logger.debug("Validating and reading from XML file...");
		this.validate();
		propertyXML = (IqmOperatorTemplates) unmarshaller.unmarshal(xmlFile); // NOI18N
	}

	/**
	 * Validates the preferences file against a defined schema.
	 */
	private void validate() {
		Schema schema;
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(IqmOperatorTemplates.class
					.getPackage().getName());

			InputStream xmlSchemaFileIS = XMLPreferencesManager.class
					.getResourceAsStream("/xsd/IQMOperatorTemplates.xsd");

			schema = schemaFactory.newSchema(new StreamSource(xmlSchemaFileIS));
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
	 * Returns a List of JaiOperationDescriptor-template from the XML by the
	 * given name.
	 * 
	 * @param parameterblockname
	 *            ParameterBlock-name (GUI-name) to find in the
	 *            IQMOperatorTemplates.xml
	 * @return List<jaixml.JaiOperationDescriptor> or NULL if no element was
	 *         found.
	 */
	public ParameterBlock getParameterBlockByName(String parameterblockname) {
		try {
			List<ParameterBlock> parameterblocks = this.propertyXML
					.getParameterBlock();
			for (ParameterBlock pb : parameterblocks) {
				if (parameterblockname.equalsIgnoreCase(pb.getBlockname())) {
					return pb;
				}
			}
		} catch (Exception ignored) {
		}
		return null;
		// throw new Exception(JAIXMLParameterBlocks.class.getName() +
		// " :: getByName :: " + "No such element!");
	}

	/**
	 * Returns a JaiOperationDescriptor from the XML by the given name.
	 * 
	 * @param parameterblockname
	 *            ParameterBlock-name (GUI-name) to find in the
	 *            IQMOperatorTemplates.xml
	 * @param templatename
	 *            Template-name to find in the IQMOperatorTemplates.xml
	 * @return jaixml.JaiOperationDescriptor or NULL if no element was found.
	 */
	public IqmOperatorDescriptor getTemplateByName(String parameterblockname,
			String templatename) {
		try {
			List<ParameterBlock> parameterblocks = this.propertyXML
					.getParameterBlock();
			for (ParameterBlock pb : parameterblocks) {
				if (parameterblockname.equalsIgnoreCase(pb.getBlockname())) {
					for (IqmOperatorDescriptor tlt : pb.getTemplate()) {
						if (tlt.getTemplatename()
								.equalsIgnoreCase(templatename)) {
							return tlt;
						}
					}
				}
			}
		} catch (Exception ignored) {
		}
		return null;
		// throw new Exception(JAIXMLParameterBlocks.class.getName() +
		// " :: getByName :: " + "No such element!");
	}

	/**
	 * De-serializes an operator's parameter block into a Java object. This
	 * method
	 * 
	 * @param operatorName
	 *            the unique name of the operator
	 * @param templateName
	 *            the unique name of the template for an operator
	 * @return the {@link ParameterBlockIQM} object
	 */
	public ParameterBlockIQM getTemplate(String operatorName,
			String templateName) {

		if (operatorName == null || operatorName.isEmpty()
				|| templateName == null || templateName.isEmpty())
			throw new IllegalArgumentException(
					"The operator name and template name must not be empty!");

		// create a default parameter set
		ParameterBlockIQM pb = new ParameterBlockIQM(operatorName);

		// deserialize the parameter set
		IqmOperatorDescriptor template = getTemplateByName(operatorName,
				templateName);

		// try to convert the serialized template to a parameterblock
		try {
			pb = loadParameterBlockFromTemplate(pb, template);
		} catch (Exception e) {
			// log the error message
			logger.error("Cannot load template '" + templateName
					+ "' for operator [" + operatorName +"], using default settings!");
		}

		return pb;
	}

	/**
	 * Deletes a template from IQMOperatorTemplates.xml.
	 * 
	 * @param parameterblockname
	 *            ParameterBlock-name (GUI-name) of the template to be deleted.
	 * @param templatename
	 *            Template-name to be deleted.
	 */
	public void deleteTemplateByName(String parameterblockname,
			String templatename) {
		List<ParameterBlock> parameterblocks = this.propertyXML
				.getParameterBlock();
		for (ParameterBlock pb : parameterblocks) {
			if (parameterblockname.equalsIgnoreCase(pb.getBlockname())) {
				for (IqmOperatorDescriptor tlt : pb.getTemplate()) {
					if (tlt.getTemplatename().equalsIgnoreCase(templatename)) {
						pb.getTemplate().remove(tlt);
						writeXML();
						return;
					}
				}
			}
		}
	}

	/**
	 * Changes values in a ParameterBlockIQM to the settings in the
	 * IQMOperatorTemplates.xml. <br />
	 * Currently supported parameter-types: Integer, Boolean, Character, Double,
	 * Float.
	 * 
	 * @param block
	 *            ParameterBlockIQM used as basis for the setting (Use the
	 *            hard-coded default ParameterBlock if unsure).
	 * @param template
	 *            JaiOperationDescriptor template to fit into the
	 *            ParameterBlockIQM.
	 * @return ParameterBlockIQM with the changes from the
	 *         IQMOperatorTemplates.xml.
	 * @see XMLPreferencesManager#getTemplateByName(java.lang.String,
	 *      java.lang.String) For a convenient way to get the correct
	 *      {@link IOperatorDescriptor} for parameter template.
	 */
	public ParameterBlockIQM loadParameterBlockFromTemplate(
			ParameterBlockIQM block, IqmOperatorDescriptor template)
			throws Exception {
		List<Parameters> parameters = template.getParameters();
		for (Parameters param : parameters) {
			if (param.getParameterclass().equals(Integer.class.getName())) {
				block.setParameter(param.getParametername(),
						Integer.valueOf(param.getParametervalue()));
			} else if (param.getParameterclass()
					.equals(Boolean.class.getName())) {
				block.setParameter(param.getParametername(),
						Boolean.valueOf(param.getParametervalue()));
			} else if (param.getParameterclass().equals(
					Character.class.getName())) {
				block.setParameter(param.getParametername(), param
						.getParametervalue().charAt(0));
			} else if (param.getParameterclass().equals(Double.class.getName())) {
				block.setParameter(param.getParametername(),
						Double.valueOf(param.getParametervalue()));
			} else if (param.getParameterclass().equals(Float.class.getName())) {
				block.setParameter(param.getParametername(),
						Float.valueOf(param.getParametervalue()));
			}
		}
		return block;
	}

	/**
	 * Sets a new template in IQMOperatorTemplates.xml with the values of a
	 * ParameterBlockIQM.<br />
	 * Currently supported parameter-types: Integer, Boolean, Character, Double,
	 * Float.<br />
	 * This function is <b>synchronized</b>.
	 * 
	 * @param parameterblockname
	 *            operator name of the new entry to IQMOperatorTemplates.xml.
	 * @param templatename
	 *            Template-name of the new entry to IQMOperatorTemplates.xml.
	 * @param block
	 *            Values of this ParameterBlockIQM are saved to
	 *            IQMOperatorTemplates.xml.
	 */
	public synchronized void setTemplate(String parameterblockname,
			String templatename, ParameterBlockIQM block) {
		try {
			List<Parameters> parameters = null;
			IqmOperatorDescriptor template = getTemplateByName(
					parameterblockname, templatename);
			if (template != null) {
				parameters = template.getParameters();
			}
			if (parameters == null) {
				ParameterBlock paramBlock = getParameterBlockByName(parameterblockname);
				if (paramBlock == null) {
					paramBlock = new ParameterBlock(); // New Parameter-Block
					paramBlock.setBlockname(parameterblockname);
					this.propertyXML.getParameterBlock().add(paramBlock);
				}
				template = new IqmOperatorDescriptor(); // New Template
				template.setTemplatename(templatename);
				paramBlock.getTemplate().add(template);
				parameters = template.getParameters();
			}

			Class<?>[] classes = block.getParamClasses();
			if (classes != null) { // PK: 2013 02 22: added empty parameter
									// capability to the template manager.
				parameters.clear();
				for (String pn : block.getParamNames()) {
					String className = classes[block.indexOfParameter(pn)]
							.getName();
					logger.debug("Write to IQMOperatorTemplates.xml " + pn
							+ " :: " + className + " @ " + parameterblockname
							+ "." + templatename);
					Parameters newparam = new Parameters();
					newparam.setParametername(pn);
					newparam.setParameterclass(className);
					if (className.equals(Integer.class.getName())) {
						newparam.setParametervalue(Integer.toString(block
								.getIntParameter(pn)));
					} else if (className.equals(Boolean.class.getName())) {
						newparam.setParametervalue(Boolean.toString(block
								.getBooleanParameter(pn)));
					} else if (className.equals(Character.class.getName())) {
						newparam.setParametervalue(Character.toString(block
								.getCharParameter(pn)));
					} else if (className.equals(Double.class.getName())) {
						newparam.setParametervalue(Double.toString(block
								.getDoubleParameter(pn)));
					} else if (className.equals(Float.class.getName())) {
						newparam.setParametervalue(Float.toString(block
								.getFloatParameter(pn)));
					}
					parameters.add(newparam);
					newparam = null;
				}
			}

			writeXML();
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
		}
	}

	public static XMLPreferencesManager getInstance() {
		if (XMLPreferencesManager.currentPrefManager == null) {
			String path;
			try {
				path = ConfigManager.getCurrentInstance().getConfPath()
						.toString();
			} catch (Exception e) {
				path = ConfigManager.DEFAULT_IQM_CONF_DIR.toString();
			}

			currentPrefManager = new XMLPreferencesManager(path);
		}
		return currentPrefManager;
	}

}
