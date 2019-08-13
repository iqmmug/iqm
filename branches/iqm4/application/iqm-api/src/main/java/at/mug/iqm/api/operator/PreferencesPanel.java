package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: PreferencesPanel.java
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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.ITemplateManagerGUI;
import at.mug.iqm.api.gui.ITemplateSupport;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.templatemanager.TemplateManagerGUI;
import at.mug.iqm.templatemanager.XMLPreferencesManager;
import at.mug.iqm.templatemanager.jaxb.IqmOperatorDescriptor;
import at.mug.iqm.templatemanager.jaxb.IqmOperatorTemplates.ParameterBlock;
import java.awt.BorderLayout;

/**
 * This {@link JPanel} is the GUI front end for managing the operator
 * preferences.
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PreferencesPanel extends JPanel implements ITemplateSupport {

	private static final Logger logger = LogManager.getLogger(PreferencesPanel.class);

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 8750555395028528754L;

	/**
	 * The cached name of the operator.
	 */
	private String opName;

	/**
	 * The cached reference to the parent {@link IOperatorGUI}.
	 */
	private IOperatorGUI parentGUI;

	/**
	 * The reference to the {@link ITemplateManagerGUI}.
	 */
	private ITemplateManagerGUI templateManagerGUI;

	/**
	 * The combo box containing the template names for a given operator.
	 */
	private JComboBox preferenceBox;
	/**
	 * The actual templates as Java objects (read from
	 * <code>IQMOperatorTemplates.xml</code>).
	 */
	private List<IqmOperatorDescriptor> templateList;
	/**
	 * The model of the combo-box.
	 */
	private DefaultComboBoxModel templateModel;

	/**
	 * The last chosen template index, this value will be passed to the
	 * {@link ITemplateManagerGUI} instance in this class for convenient
	 * template managing.
	 */
	private int lastChosenTemplateIndex;

	/**
	 * The label containing the description of the combo box.
	 */
	private JLabel lblPreferences;

	/**
	 * Ties the preferences to a specific operator GUI.
	 * 
	 * @param parentGUI
	 */
	public PreferencesPanel(IOperatorGUI parentGUI) {
		this();
		this.initialize(parentGUI);
	}

	/**
	 * Create the panel.
	 * <p>
	 * The use of {@link #PreferencesPanel(IOperatorGUI)} is encouraged.
	 * <p>
	 * You will have to set the following properties manually:
	 * <ul>
	 * <li>opName
	 * <li>parentGUI
	 * </ul>
	 * and after that you have to call {@link #initialize(IOperatorGUI)} in order to get a
	 * working {@link PreferencesPanel}.
	 */
	public PreferencesPanel() {
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setAlignment(FlowLayout.TRAILING);

		lblPreferences = new JLabel(
				I18N.getGUILabelText("prefPanel.lblPreferences.text"));
		add(lblPreferences);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(200, 23));
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		preferenceBox = new JComboBox();
		panel.add(preferenceBox);
		preferenceBox.setMaximumRowCount(15);
	}

	/**
	 * Initializes all functionality of the panel.
	 */
	public void initialize(IOperatorGUI parentGUI) {
		this.opName = parentGUI.getOpName();
		this.parentGUI = parentGUI;

		templateManagerGUI = this.createTemplateManagerGUI();

		// add the model
		preferenceBox.setModel(createTemplateModel());

		try {
			this.fillPreferencesBox();
		} catch (Exception e) {
			e.printStackTrace();
		}

		preferenceBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent evt) {
				preferenceBoxItemStateChanged(evt);
			}
		});

	}

	/**
	 * A wrapper for custom {@link ItemEvent} on the <code>preferenceBox</code>.
	 * 
	 * @param evt
	 */
	protected void preferenceBoxItemStateChanged(ItemEvent evt) {
		this.handleItemStateChange();
	}

	/**
	 * A custom handler, if the user changes a value in the combo box of
	 * templates.
	 */
	protected void handleItemStateChange() {
		if (preferenceBox.isEditable()) { // Only if user might be the reason
			// for the event.
			String choosen = (String) preferenceBox.getSelectedItem();
			if (preferenceBox.getSelectedIndex() == preferenceBox
					.getItemCount() - 1) {
				preferenceBox.setEditable(false);
				templateManagerGUI
						.setSelectedTemplateIndex(lastChosenTemplateIndex);

				try {
					// invoke the template manager
					SwingUtilities.invokeLater(this.templateManagerGUI);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				createParametersFromTemplate(choosen);
				lastChosenTemplateIndex = preferenceBox.getSelectedIndex();
			}

		}

	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getOpName() {
		return this.opName;
	}

	public JComboBox getPreferenceBox() {
		return preferenceBox;
	}

	public JLabel getLblPreferences() {
		return lblPreferences;
	}

	protected DefaultComboBoxModel createTemplateModel() {
		this.templateModel = new DefaultComboBoxModel();
		this.templateModel.addElement(I18N
				.getGUILabelText("templateManager.templates.default.text"));
		this.templateModel.addElement(I18N
				.getGUILabelText("templateManager.templates.new.text"));
		return this.templateModel;
	}

	/**
	 * This function is called to fill the combo-box with appropriate templates
	 * from IQMOperatorTemplates.xml.<br />
	 * The entries are sorted alphabetically, but the first entry is "default"
	 * and the last is "New Template".<br />
	 * If no template is found in IQMOperatorTemplates.xml this function also
	 * generates a new "default" template from the hard-coded default values and
	 * saves them to IQMOperatorTemplates.xml.
	 * <p>
	 * <b>Author:</b> Philipp W. <java@scaenicus.net>
	 */
	public void fillPreferencesBox() {
		this.templateModel.removeAllElements();
		this.preferenceBox.setEditable(false);
		List<String> callstrings = new ArrayList<String>();
		@SuppressWarnings("unused")
		boolean defaultisset = false;

		ParameterBlock xmlPB = XMLPreferencesManager.getInstance()
				.getParameterBlockByName(this.opName);
		if (xmlPB != null) {
			this.templateList = xmlPB.getTemplate();
			for (IqmOperatorDescriptor tlt : this.templateList) {
				if (tlt.getTemplatename()
						.equalsIgnoreCase(
								I18N.getGUILabelText("templateManager.templates.default.text"))) {
					defaultisset = true;
				} else {
					callstrings.add(tlt.getTemplatename());
				}
			}
		}
		Collections.sort(callstrings);
		callstrings.add(0,
				I18N.getGUILabelText("templateManager.templates.default.text"));
		// if (defaultisset == false) { //modified by Helmut Ahammer: always
		// load default ParameterBlock parameters
		ParameterBlockIQM pb = new ParameterBlockIQM(this.opName);
		XMLPreferencesManager.getInstance().setTemplate(this.opName,
				I18N.getGUILabelText("templateManager.templates.default.text"),
				pb);
		// }

		callstrings.add(I18N
				.getGUILabelText("templateManager.startManager.text"));

		for (String s : callstrings) {
			this.getTemplateModel().addElement(s);
		}
		this.preferenceBox.setEditable(true);
		this.preferenceBox.validate();

		createParametersFromTemplate(I18N
				.getGUILabelText("templateManager.templates.default.text"));
		lastChosenTemplateIndex = 0;
	}

	/**
	 * Sets the GUI's ParameterBlock with a template saved in the
	 * IQMOperatorTemplates.xml. <br />
	 * This function is called, by the combo-box. If called otherwise make sure
	 * the template exists, as nothing will happen on a wrong name.
	 * <p>
	 * <b>Author:</b> Philipp W. <java@scaenicus.net>
	 * @param templateName
	 *            Name of the template in IQMOperatorTemplates.xml
	 */
	protected void createParametersFromTemplate(String templateName) {

		// load the template
		IqmOperatorDescriptor template = XMLPreferencesManager.getInstance()
				.getTemplateByName(this.opName, templateName);

		if (template != null) {
			ParameterBlockIQM pb = null;
			if (this.parentGUI.getWorkPackage() != null) {
				pb = this.parentGUI.getWorkPackage().getParameters();
			} else {
				pb = new ParameterBlockIQM(this.opName);
			}

			// try to convert the serialized template to a parameterblock
			try {
				pb = XMLPreferencesManager.getInstance()
						.loadParameterBlockFromTemplate(pb, template);
			} catch (Exception e) {
				// log the error message
				Application
						.getDialogUtil()
						.showErrorMessage(
								I18N.getMessage(
										"application.exception.templates.definition.invalid",
										ConfigManager.getCurrentInstance()
												.getRootPath().toString()
												+ IQMConstants.FILE_SEPARATOR
												+ "logs"
												+ IQMConstants.FILE_SEPARATOR
												+ "iqm.log"), e);
			}

			try {
				// setting the pb values to the GUI's pb
				// copy the source settings
				ParameterBlockIQM prev = this.parentGUI.getWorkPackage()
						.getParameters();
				pb.setSources(prev.getSources());

				this.parentGUI.updateParameterBlock(pb);

				// perform an autopreview
				try {
					if (this.parentGUI instanceof AbstractImageOperatorGUI) {
						AbstractImageOperatorGUI imgOpGui = ((AbstractImageOperatorGUI) parentGUI);
						if (imgOpGui.isAutoPreviewSelected()) {
							logger.debug("Performing AutoPreview");
							imgOpGui.showPreview();
						}
					}
				} catch (ClassCastException ignored) {
				}

			} catch (Exception e) {
				DialogUtil.getInstance().showErrorMessage(
						"An error occurred "
								+ "when setting the ParameterBlockIQM from "
								+ "the PreferencesPanel to the operator GUI!",
						e);
			}
		}
	}

	/**
	 * Set the template model for the combo box.
	 * 
	 * @param templateModel
	 */
	public void setTemplateModel(DefaultComboBoxModel templateModel) {
		this.templateModel = templateModel;
	}

	/**
	 * Get the template model of the combo box.
	 * 
	 * @return the template model of the combo box
	 */
	public DefaultComboBoxModel getTemplateModel() {
		return templateModel;
	}

	public int getLastChosenTemplateIndex() {
		return lastChosenTemplateIndex;
	}

	public void setLastChosenTemplateIndex(int lastChosenTemplateIndex) {
		this.lastChosenTemplateIndex = lastChosenTemplateIndex;
	}

	/**
	 * Create a new {@link ITemplateManagerGUI} associated with
	 * <code>this</code> as particular operator GUI and {@link PreferencesPanel}
	 * .
	 * 
	 * @return a new instance of {@link ITemplateManagerGUI}
	 */
	private ITemplateManagerGUI createTemplateManagerGUI() {
		final ITemplateSupport templateSupport = this;

		templateManagerGUI = new TemplateManagerGUI(templateSupport);

		return templateManagerGUI;
	}

	/**
	 * Get the parameters wrapped in a {@link ParameterBlockIQM} of a GUI.
	 * 
	 * @return the parameter block
	 */
	public ParameterBlockIQM getParameters() {
		return this.parentGUI.getWorkPackage().getParameters();
	}

	/**
	 * Set the template name, selected in the {@link TemplateManagerGUI} in the
	 * combo box, when the manager's GUI is closed.
	 * 
	 * @param templateName
	 */
	public void setChosenTemplate(String templateName) {
		this.templateModel.setSelectedItem(templateName);
	}
}
