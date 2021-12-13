package at.mug.iqm.templatemanager;

/*
 * #%L
 * Project: IQM - API
 * File: TemplateManagerGUI.java
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

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.ITemplateManagerGUI;
import at.mug.iqm.api.gui.ITemplateSupport;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.templatemanager.jaxb.IqmOperatorDescriptor;
import at.mug.iqm.templatemanager.jaxb.IqmOperatorTemplates.ParameterBlock;

/**
 * This is a GUI for managing templates.
 * 
 * @author Philipp W. <java@scaenicus.net>, Philipp Kainz
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class TemplateManagerGUI extends JFrame implements ITemplateManagerGUI {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -7603330014937360474L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(TemplateManagerGUI.class);

	private String opName;

	private List<IqmOperatorDescriptor> templateList;
	private boolean userHasToChangeName = false;
	private boolean userWantsToChangeName = false;
	private ITemplateSupport templateSupport;

	private DefaultComboBoxModel templateModel = null;
	private String lastTemplateName = null;

	/** Creates new form NewTemplateGUI */
	public TemplateManagerGUI(ITemplateSupport templateSupport) {
		this.templateSupport = templateSupport;
		this.opName = templateSupport.getOpName();
		this.initComponents();
		this.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.application.orange.32x32")).getImage());
		this.getTemplateComboBox();
	}

	/**
	 * This function permanently deletes a template from the jaitemplates.xml.
	 * 
	 * <b>Author:</b> Philipp W. <java@scaenicus.net>
	 * @param templateName
	 *            Name of the template to be deleted.
	 */
	protected void deleteTemplate(String templateName) {
		XMLPreferencesManager.getInstance().deleteTemplateByName(this.opName,
				templateName);
	}

	/**
	 * This function is called to fill the combo-box with appropriate templates
	 * from jaitemplates.xml.<br />
	 * The entries are sorted alphabetically, but the first entry is "default"
	 * and the last is "New Template".<br />
	 * If no template is found in jaitemplates.xml this function also generates
	 * a new "default" template from the hard-coded default values and saves
	 * them to jaitemplates.xml.
	 * 
	 * <b>Author:</b> Philipp W. <java@scaenicus.net>
	 */
	protected void fillTemplateComboBox() {
		templateModel.removeAllElements();
		String currFunc = this.opName;
		List<String> callstrings = new ArrayList<String>();
		boolean defaultisset = false;

		ParameterBlock xmlPB = XMLPreferencesManager.getInstance()
				.getParameterBlockByName(currFunc);
		if (xmlPB != null) {
			templateList = xmlPB.getTemplate();
			for (IqmOperatorDescriptor tlt : templateList) {
				if (tlt.getTemplatename()
						.equalsIgnoreCase(
								I18N.getGUILabelText("templateManager.templates.default.text"))) {
					defaultisset = true;
				} else {
					callstrings.add(tlt.getTemplatename());
				}
			}
		}
		java.util.Collections.sort(callstrings);
		callstrings.add(0,
				I18N.getGUILabelText("templateManager.templates.default.text"));
		if (defaultisset == false) {
			ParameterBlockIQM pb = new ParameterBlockIQM(currFunc);
			XMLPreferencesManager
					.getInstance()
					.setTemplate(
							currFunc,
							I18N.getGUILabelText("templateManager.templates.default.text"),
							pb);
		}

		callstrings.add(I18N
				.getGUILabelText("templateManager.templates.new.text"));

		for (String s : callstrings) {
			templateModel.addElement(s);
		}
		getTemplateComboBox().validate();

	}

	public void setSelectedTemplateIndex(int index) {
		// if the name has been entered manually
		if (index == -1) {
			index = templateModel.getSize() - 1;
			getTemplateComboBox().setSelectedIndex(index);
			return;
		}
		getTemplateComboBox().setSelectedIndex(index);
		lastTemplateName = (String) getTemplateComboBox().getItemAt(index);
	}

	private boolean shouldBTN_saveEnable() {
		return !userHasToChangeName && !userWantsToChangeName;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		cbox_chooseParameterBlock = new JComboBox();
		lbl_tn = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		txt_newTemplateName = new javax.swing.JTextField();
		btn_delete = new javax.swing.JButton();
		btn_changename = new javax.swing.JButton();
		btn_close = new javax.swing.JButton();
		btn_save = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(I18N.getGUILabelText("templateManager.frame.title"));
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		cbox_chooseParameterBlock
				.addItemListener(new java.awt.event.ItemListener() {
					@Override
					public void itemStateChanged(java.awt.event.ItemEvent evt) {
						cbox_chooseParameterBlockItemStateChanged(evt);
					}
				});

		lbl_tn.setText(I18N
				.getGUILabelText("templateManager.lblTemplateName.text"));

		txt_newTemplateName.setEditable(false);
		txt_newTemplateName.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				txt_newTemplateNameKeyReleased(evt);
			}
		});

		btn_delete.setText(I18N
				.getGUILabelText("templateManager.buttDelete.text"));
		btn_delete.setToolTipText(I18N
				.getGUILabelText("templateManager.buttDelete.ttp"));
		btn_delete.setEnabled(false);
		btn_delete.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_deleteActionPerformed(evt);
			}
		});

		btn_changename.setText(I18N
				.getGUILabelText("templateManager.buttChangeName.text"));
		btn_changename.setToolTipText(I18N
				.getGUILabelText("templateManager.buttChangeName.ttp"));
		btn_changename.setEnabled(false);
		btn_changename.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_changeNameActionPerformed(evt);
			}
		});

		btn_close.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		btn_close.setText(I18N
				.getGUILabelText("templateManager.buttClose.text"));
		btn_close.setToolTipText(I18N
				.getGUILabelText("templateManager.buttClose.ttp"));
		btn_close.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_closeActionPerformed(evt);
			}
		});

		btn_save.setText(I18N.getGUILabelText("templateManager.buttSave.text"));
		btn_save.setToolTipText(I18N
				.getGUILabelText("templateManager.buttSave.ttp"));
		btn_save.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_saveActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														cbox_chooseParameterBlock,
														0, 395, Short.MAX_VALUE)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		lbl_tn)
																.addGap(84, 84,
																		84)
																.addComponent(
																		txt_newTemplateName,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		238,
																		Short.MAX_VALUE))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		btn_delete)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		btn_changename,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		238,
																		Short.MAX_VALUE))
												.addComponent(
														jSeparator1,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														395, Short.MAX_VALUE)
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		btn_save,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		326,
																		Short.MAX_VALUE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		btn_close)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(cbox_chooseParameterBlock,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(2, 2, 2)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(btn_delete)
												.addComponent(btn_changename))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jSeparator1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										10,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(1, 1, 1)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(lbl_tn)
												.addComponent(
														txt_newTemplateName,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(btn_close)
												.addComponent(btn_save))
								.addContainerGap()));

		this.pack();
	}// </editor-fold>//GEN-END:initComponents

	private void btn_changeNameActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_changenameActionPerformed
		String templatename = (String) getTemplateComboBox().getSelectedItem();
		if (txt_newTemplateName.isEditable()) {
			txt_newTemplateName.setEditable(false); // set back
			userWantsToChangeName = false; // set back
			btn_delete.setEnabled(true);
			btn_save.setEnabled(true);
			btn_changename.setText(I18N
					.getGUILabelText("templateManager.buttChangeName.text"));
			IqmOperatorDescriptor template = XMLPreferencesManager
					.getInstance().getTemplateByName(this.opName, templatename);
			if (template != null) {
				String newTemplateName = txt_newTemplateName.getText();
				logger.debug("TemplateManagerGUI Change template name: "
						+ templatename + " to: " + newTemplateName);
				// get data from current template
				ParameterBlockIQM pb = new ParameterBlockIQM(this.opName);
				try {
					pb = XMLPreferencesManager.getInstance()
							.loadParameterBlockFromTemplate(pb, template);
				} catch (Exception e) {
					// log the error message
					logger.error("An error occurred: ", e);
					DialogUtil
							.getInstance()
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
				// Delete template
				deleteTemplate(templatename);
				// Save under new name
				XMLPreferencesManager.getInstance().setTemplate(this.opName,
						newTemplateName, pb);
				// Rebuild
				fillTemplateComboBox();
				// getChooseParameterBlock().setSelectedItem(templatename);
				getTemplateComboBox().setSelectedItem(newTemplateName);
				cbox_chooseParameterBlockItemStateChanged(null);
				// txt_newTemplateName.setText(templatename); //default text for
				// new name is old name
				this.lastTemplateName = newTemplateName;
				txt_newTemplateName.setText((String) getTemplateComboBox()
						.getSelectedItem()); // default text for new name is
												// nothing
			}
		} else {
			userWantsToChangeName = true;
			txt_newTemplateName.setEditable(true);
			btn_delete.setEnabled(false);
			btn_save.setEnabled(false);
			btn_changename
					.setText(I18N
							.getGUILabelText("templateManager.buttChangeName.saveNewName.text"));
			txt_newTemplateName.setText(templatename);
		}
	}// GEN-LAST:event_btn_changenameActionPerformed

	/**
	 * This method is called, when the user clicks on the delete button.
	 * 
	 * @param evt
	 */
	private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_deleteActionPerformed
		String templatename = (String) getTemplateComboBox().getSelectedItem();
		deleteTemplate(templatename);
		fillTemplateComboBox();
		getTemplateComboBox().setSelectedItem(
				I18N.getGUILabelText("templateManager.templates.default.text"));
		cbox_chooseParameterBlockItemStateChanged(null);
		lastTemplateName = I18N
				.getGUILabelText("templateManager.templates.default.text");

	}// GEN-LAST:event_btn_deleteActionPerformed

	/**
	 * This function sets the GUIs ParameterBlock to the user's choice.<br />
	 * This function is called, when the changes the item in the combo-box.
	 * 
	 * <b>Author:</b> Philipp W. <java@scaenicus.net>
	 * @param evt
	 *            Unused meta-data from the closing of the item changing.
	 */
	private void cbox_chooseParameterBlockItemStateChanged(
			java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbox_chooseParameterBlockItemStateChanged
		if (getTemplateComboBox().getSelectedIndex() == getTemplateComboBox()
				.getItemCount() - 1) {
			btn_changename.setEnabled(false);
			btn_delete.setEnabled(false);
			btn_delete.setText(I18N
					.getGUILabelText("templateManager.buttDelete.text"));
			txt_newTemplateName.setEditable(true);
			txt_newTemplateName
					.setText(I18N
							.getGUILabelText("templateManager.txtNewTemplateName.text"));
			txt_newTemplateName.selectAll();
			txt_newTemplateName.requestFocusInWindow();
			userHasToChangeName = true;
			btn_save.setEnabled(shouldBTN_saveEnable());
		} else if (getTemplateComboBox().getSelectedIndex() == 0) {
			btn_changename.setEnabled(false);
			btn_delete.setEnabled(false);
			btn_delete.setText(I18N
					.getGUILabelText("templateManager.buttDelete.text"));
			txt_newTemplateName.setEditable(false);
			txt_newTemplateName
					.setText(I18N
							.getGUILabelText("templateManager.txtNewTemplateName.noChange.text"));
			userHasToChangeName = false;
			btn_save.setEnabled(false);
		} else {
			btn_changename.setEnabled(true);
			btn_delete.setEnabled(true);
			btn_delete.setText(I18N
					.getGUILabelText("templateManager.buttDelete.text"));
			txt_newTemplateName.setEditable(false);
			txt_newTemplateName.setText("");
			userHasToChangeName = false;
			btn_save.setEnabled(shouldBTN_saveEnable());
		}
	}// GEN-LAST:event_cbox_chooseParameterBlockItemStateChanged

	private void txt_newTemplateNameKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txt_newTemplateNameKeyReleased
		if (cbox_chooseParameterBlock.getSelectedIndex() == 0) {
			return;
		}
		// PK: 2013-06-20: enable, if the templates should only contain letters, whitespaces and numbers.
//		if (Character.isLetterOrDigit(evt.getKeyChar()) || Character.isSpaceChar(evt.getKeyChar()) {
			userHasToChangeName = false;
			btn_save.setEnabled(shouldBTN_saveEnable());
//		}
	}// GEN-LAST:event_txt_newTemplateNameKeyReleased

	private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_saveActionPerformed
		String templatename;
		if (txt_newTemplateName.isEditable()) {
			templatename = txt_newTemplateName.getText();
		} else {
			templatename = (String) getTemplateComboBox().getSelectedItem();
		}
		logger.debug("Create template: " + templatename);
		try {
			XMLPreferencesManager.getInstance().setTemplate(this.opName,
					templatename, templateSupport.getParameters());
		} catch (Exception e) {
			// log the error message
			logger.error(e.getMessage().toString());
		}
		fillTemplateComboBox();
		getTemplateComboBox().setSelectedItem(templatename);
		cbox_chooseParameterBlockItemStateChanged(null);
		txt_newTemplateName.setText("");
		this.lastTemplateName = templatename;
	}// GEN-LAST:event_btn_saveActionPerformed

	private void btn_closeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_closeActionPerformed
		this.dispose();
		// set the parameter blocks to the preferences panel
		templateSupport.fillPreferencesBox();
		templateSupport.setChosenTemplate(this.lastTemplateName);
	}// GEN-LAST:event_btn_closeActionPerformed

	private void formWindowClosing(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
		btn_closeActionPerformed(null);
	}// GEN-LAST:event_formWindowClosing

	/**
	 * Initializes and/or returns the combo-box used to choose existing
	 * templates (parameter block).
	 * 
	 * <b>Author:</b> Philipp W. <java@scaenicus.net>
	 * @return JComboBox Representation of the combo-box.
	 */
	protected javax.swing.JComboBox getTemplateComboBox() {
		if (templateModel == null) {
			templateModel = new javax.swing.DefaultComboBoxModel();
			templateModel.addElement(I18N
					.getGUILabelText("templateManager.templates.default.text"));
			templateModel.addElement(I18N
					.getGUILabelText("templateManager.templates.new.text"));
			cbox_chooseParameterBlock.setModel(templateModel);
			fillTemplateComboBox();
			getTemplateComboBox().setSelectedIndex(0);
			cbox_chooseParameterBlockItemStateChanged(null);
		}
		return cbox_chooseParameterBlock;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btn_changename;
	private javax.swing.JButton btn_close;
	private javax.swing.JButton btn_delete;
	private javax.swing.JButton btn_save;
	private javax.swing.JComboBox cbox_chooseParameterBlock;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JLabel lbl_tn;
	private javax.swing.JTextField txt_newTemplateName;

	// End of variables declaration//GEN-END:variables

	@Override
	public void run() {
		logger.debug("Starting a new runnable of '" + this.getClass().getName()
				+ "'");

		int left = ((int) (Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth() * (2d / 3d))
				- this.getWidth() - 10);
		int top = 10;
		this.setLocation(left, top);
		this.setResizable(false);
		this.setVisible(true);
	}
}
// /**
// * This function is called, if the user presses a key on the combo-box.<br/>
// * On [ENTER] or [RETURN] the current settings are stored to jaitemplates.xml
// with the same name, as the current selected name. Default can be overwritten
// by this. <br />
// * On [DEL] the currently selected template is deleted from jaitemplates.xml.
// * @author Philipp W. <java@scaenicus.net>
// * @param evt Meta-data, including the pressed key.
// */
// private void chooseParameterBlockKeyPressed(java.awt.event.KeyEvent evt) {
// if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
// txt_newTemplateName.setText((String)
// getChooseParameterBlock().getSelectedItem());
// } else if (evt.getKeyCode() == KeyEvent.VK_DELETE &&
// getChooseParameterBlock().getSelectedIndex() > 0 &&
// getChooseParameterBlock().getSelectedIndex() <
// getChooseParameterBlock().getItemCount() - 1) {
// String templatename = (String) getChooseParameterBlock().getSelectedItem();
// //getChooseParameterBlock().removeItemAt(getChooseParameterBlock().getSelectedIndex());
// deleteTemplate(templatename);
// fillChooseParameterBlock();
// getChooseParameterBlock().setSelectedItem("default");
// chooseParameterBlockChoosen("default");
// this.setParameterValues();
// }
// }
// /**
// * This function is called, if the user presses a key on the text-box.<br/>
// * On [ENTER] or [RETURN] the current settings are stored to jaitemplates.xml
// with the same name, as the String in the text-box. Any template can be
// overwritten by this. <br />
// * @author Philipp W. <java@scaenicus.net>
// * @param evt Meta-data, including the pressed key.
// */
// private void newTemplateNameKeyPressed(java.awt.event.KeyEvent evt) {
// if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
// String templatename = txt_newTemplateName.getText();
// System.out.println("Create/overwrite template: " + templatename);
// IqmTools.getJaiTemplatesXML().setTemplate(CommonTools.getCurrImgProcessFunc(),
// templatename, IqmTools.getCurrParameterBlockJAI());
// // int index = chooseParameterBlock.getSelectedIndex();
// // int count = chooseParameterBlock.getItemCount();
// // chooseParameterBlock.removeItemAt(count - 1);
// // if (index > 0 && index < count &&
// !templatename.equals(chooseParameterBlock.getSelectedItem())) {
// // //chooseParameterBlock.removeItemAt(index);
// // chooseParameterBlock.addItem(templatename);
// // }
// // chooseParameterBlock.addItem("<New Template>");
// // chooseParameterBlock.setSelectedItem(templatename);
// fillChooseParameterBlock();
// getChooseParameterBlock().setSelectedItem(templatename);
// txt_newTemplateName.setText("");
// }
// }
// /**
// * Initializes and/or returns the text-field used to set new, and update
// existing templates.
// * @return Representations of the text-field.
// * @author Philipp W. <java@scaenicus.net>
// */
// private JTextField txt_newTemplateName {
//
// if (newTemplateName == null) {
// newTemplateName = new javax.swing.JTextField();
// newTemplateName.setText("");
// newTemplateName.addKeyListener(new java.awt.event.KeyAdapter() {
//
// @Override
// public void keyPressed(java.awt.event.KeyEvent evt) {
// newTemplateNameKeyPressed(evt);
// }
// });
// }
// return newTemplateName;
// }

