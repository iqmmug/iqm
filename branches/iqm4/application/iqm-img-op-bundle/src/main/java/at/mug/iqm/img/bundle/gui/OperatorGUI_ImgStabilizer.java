package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_ImgStabilizer.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpImgStabilizerDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2010 05
 * @update 2014 12 change buttons to JRadioButtons and added TitledBorder
 */
public class OperatorGUI_ImgStabilizer extends AbstractImageOperatorGUI
		implements ActionListener, ChangeListener, PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5721437365950286003L;

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(OperatorGUI_ImgStabilizer.class);

	private ParameterBlockIQM pb = null; // @jve:decl-index=0:

	@SuppressWarnings("unused")
	private int regMode; // Registration Mode
	private int pyrLevels; // Maximum Pyramid Levels

	private JRadioButton buttTrans         = null;
	private JRadioButton buttAffine        = null;
	private JPanel       jPanelRegMode     = null;
	private ButtonGroup  buttGroupRegMode  = null;

	private JPanel   jSpinnerPanelPyrLevels = null;
	private JSpinner jSpinnerPyrLevels;
	private JLabel   jLabelPyrLevels;

	private JPanel              jPanelTempUpCo              = null;
	private JLabel              jLabelTempUpCo              = null;
	private JFormattedTextField jFormattedTextFieldTempUpCo = null;

	private JPanel              jPanelMaxIt              = null;
	private JLabel              jLabelMaxIt              = null;
	private JFormattedTextField jFormattedTextFieldMaxIt = null;

	private JPanel              jPanelErrTol              = null;
	private JLabel              jLabelErrTol              = null;
	private JFormattedTextField jFormattedTextFieldErrTol = null;

	private JCheckBox jCheckBoxLogCo = null;
	private JPanel    pnlAlgSettings;

	/**
	 * constructor
	 */
	public OperatorGUI_ImgStabilizer() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpImgStabilizerDescriptor().getName());
		this.initialize();
		this.setTitle("Image Stabilizer");
		this.getOpGUIContent().setLayout(new GridBagLayout());
	
	
		this.getOpGUIContent().add(getJPanelRegMode(),  getGridBagConstraints_RegMode());
		this.getOpGUIContent().add(getPnlAlgSettings(), getGridBagConstraints_PnlAlgSettings());
		this.getOpGUIContent().add(getJCheckBoxLogCo(), getGridBagConstraints_BoxLogCo());

		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_RegMode(){
		GridBagConstraints gbc_RegMode = new GridBagConstraints();
		gbc_RegMode.gridx = 0;
		gbc_RegMode.gridy = 0;
		gbc_RegMode.insets = new Insets(10, 0, 0, 0);
		gbc_RegMode.fill = GridBagConstraints.HORIZONTAL;
		return gbc_RegMode;
	}
	private GridBagConstraints getGridBagConstraints_PnlAlgSettings(){
		GridBagConstraints gbc_pnlAlgSettings = new GridBagConstraints();	
		gbc_pnlAlgSettings.gridx = 0;
		gbc_pnlAlgSettings.gridy = 1;
		gbc_pnlAlgSettings.insets = new Insets(5, 0, 0, 0);
		gbc_pnlAlgSettings.fill = GridBagConstraints.HORIZONTAL;
		return gbc_pnlAlgSettings;
	}
	private GridBagConstraints getGridBagConstraints_BoxLogCo(){
		GridBagConstraints gbc_BoxLogCo = new GridBagConstraints();
		gbc_BoxLogCo.gridx = 0;
		gbc_BoxLogCo.gridy = 2;
		gbc_BoxLogCo.insets = new Insets(5, 0, 5, 0); // top, left bottom right
		//gbc_BoxLogCo.fill= GridBagConstraints.BOTH;
		return gbc_BoxLogCo;
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttTrans.isSelected())   pb.setParameter("RegMode", 0);
		if (buttAffine.isSelected())  pb.setParameter("RegMode", 1);

		pb.setParameter("PyrLevels", pyrLevels);

		pb.setParameter("TempUpCo", ((Number) jFormattedTextFieldTempUpCo.getValue()).floatValue());
		pb.setParameter("MaxIt",    ((Number) jFormattedTextFieldMaxIt.getValue()).floatValue());
		pb.setParameter("ErrTol",   ((Number) jFormattedTextFieldErrTol.getValue()).floatValue());

		if (jCheckBoxLogCo.isSelected())  pb.setParameter("LogCo", 1);
		if (!jCheckBoxLogCo.isSelected()) pb.setParameter("LogCo", 0);
	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("RegMode") == 0)  buttTrans.setSelected(true);
		if (pb.getIntParameter("RegMode") == 1)  buttAffine.setSelected(true);

		pyrLevels = pb.getIntParameter("PyrLevels");
		jSpinnerPyrLevels.removeChangeListener(this);
		jSpinnerPyrLevels.setValue(pyrLevels);
		jSpinnerPyrLevels.addChangeListener(this);

		jFormattedTextFieldTempUpCo.removePropertyChangeListener("value", this);
		jFormattedTextFieldMaxIt.removePropertyChangeListener("value", this);
		jFormattedTextFieldErrTol.removePropertyChangeListener("value", this);

		jFormattedTextFieldTempUpCo.setValue(pb.getFloatParameter("TempUpCo"));
		jFormattedTextFieldMaxIt.setValue(pb.getFloatParameter("MaxIt"));
		jFormattedTextFieldErrTol.setValue(pb.getFloatParameter("ErrTol"));

		jFormattedTextFieldTempUpCo.addPropertyChangeListener("value", this);
		jFormattedTextFieldMaxIt.addPropertyChangeListener("value", this);
		jFormattedTextFieldErrTol.addPropertyChangeListener("value", this);

		if (pb.getIntParameter("LogCo") == 0)  jCheckBoxLogCo.setSelected(false);
		if (pb.getIntParameter("LogCo") == 1)  jCheckBoxLogCo.setSelected(true);
	}


	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}


	/**
	 * This method initializes the Option: Trans
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtTrans() {
		if (buttTrans == null) {
			buttTrans = new JRadioButton();
			buttTrans.setText("Translation");
			buttTrans.setToolTipText("stabilization using translation");
			buttTrans.addActionListener(this);
			buttTrans.setActionCommand("parameter");
		}
		return buttTrans;
	}

	/**
	 * This method initializes the Option: Affine
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtAffine() {
		if (buttAffine == null) {
			buttAffine = new JRadioButton();
			buttAffine.setText("Affine");
			buttAffine
					.setToolTipText("stabilization using affine transformation");
			buttAffine.addActionListener(this);
			buttAffine.setActionCommand("parameter");
		}
		return buttAffine;
	}

	/**
	 * This method initializes jJPanelBar
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRegMode() {
		if (jPanelRegMode == null) {
			jPanelRegMode = new JPanel();
			//jPanelRegMode.setLayout(new BoxLayout(jPanelRegMode, BoxLayout.Y_AXIS));
			jPanelRegMode.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelRegMode.setBorder(new TitledBorder(null, "Registration method", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelRegMode.add(getJRadioButtonButtTrans());
			jPanelRegMode.add(getJRadioButtonButtAffine());
			this.setButtonGroupRegMode(); // Grouping of JRadioButtons
		}
		return jPanelRegMode;

	}

	private void setButtonGroupRegMode() {
		if (buttGroupRegMode == null) {
			buttGroupRegMode = new ButtonGroup();
			buttGroupRegMode.add(buttTrans);
			buttGroupRegMode.add(buttAffine);
		}
	}
    //-----------------------------------------------------------------------------------------------
	/**
	 * This method initializes jSpinnerPyrLevels
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getJSpinnerPyrLevels() {
		if (jSpinnerPanelPyrLevels == null) {
			jSpinnerPanelPyrLevels = new JPanel(new BorderLayout(5, 0));
			jSpinnerPyrLevels = new JSpinner(new SpinnerNumberModel(1, 0, 4, 1));
			jSpinnerPyrLevels.setToolTipText("maximum pyramid levels");
			jLabelPyrLevels = new JLabel();
			jLabelPyrLevels.setLabelFor(jSpinnerPyrLevels);
			jLabelPyrLevels.setText("Max pyramid levels:");
			jSpinnerPanelPyrLevels.add(jLabelPyrLevels, BorderLayout.CENTER);
			jSpinnerPanelPyrLevels.add(jSpinnerPyrLevels, BorderLayout.EAST);
		}
		return jSpinnerPanelPyrLevels;
	}

	// ----------------------------------------------------------------------------------------

	class FloatNumberVerifier extends InputVerifier { // damit muss Eingabe
														// richtig sein
		@Override
		public boolean verify(JComponent input) {
			JFormattedTextField ftf = (JFormattedTextField) input;
			JFormattedTextField.AbstractFormatter formatter = ftf
					.getFormatter();
			if (formatter != null) {
				String text = ftf.getText();
				try {
					text = text.replace(",", ".");
					Float.valueOf(text);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}
			return true;
		}
		// public boolean shouldYieldFocus(JComponent input) {
		// System.out.println("NumberVerifier  shouldYieldFocus");
		//
		// return verify(input);
		// }
	}

	/**
	 * This method initializes jJPanelTempUpCo
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelTempUpCo() {
		if (jPanelTempUpCo == null) {
			jPanelTempUpCo = new JPanel();
			jPanelTempUpCo.setLayout(new BorderLayout());
			// jPanelTempUpCo.setPreferredSize(new Dimension(250,18));
			jLabelTempUpCo = new JLabel("Template update coefficient (0...1): ");
			jFormattedTextFieldTempUpCo = new JFormattedTextField( NumberFormat.getNumberInstance());
			jFormattedTextFieldTempUpCo.addPropertyChangeListener("value", this);
			jFormattedTextFieldTempUpCo.setInputVerifier(new FloatNumberVerifier());
			// jFormattedTextFieldTempUpCo.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
			jFormattedTextFieldTempUpCo.setColumns(5);
			jPanelTempUpCo.add(jLabelTempUpCo, BorderLayout.WEST);
			jPanelTempUpCo.add(jFormattedTextFieldTempUpCo, BorderLayout.CENTER);

		}
		return jPanelTempUpCo;
	}

	/**
	 * This method initializes jJPanelMaxIt
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxIt() {
		if (jPanelMaxIt == null) {
			jPanelMaxIt = new JPanel();
			jPanelMaxIt.setLayout(new BorderLayout());
			// jPanelMaxIt.setPreferredSize(new Dimension(250,18));
			jLabelMaxIt = new JLabel("Maximum iteration: ");
			jFormattedTextFieldMaxIt = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldMaxIt.addPropertyChangeListener("value", this);
			jFormattedTextFieldMaxIt.setInputVerifier(new FloatNumberVerifier());
			// jFormattedTextFieldMaxIt.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
			jFormattedTextFieldMaxIt.setColumns(5);
			jPanelMaxIt.add(jLabelMaxIt, BorderLayout.WEST);
			jPanelMaxIt.add(jFormattedTextFieldMaxIt, BorderLayout.CENTER);

		}
		return jPanelMaxIt;
	}

	/**
	 * This method initializes jJPanelErrTol
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelErrTol() {
		if (jPanelErrTol == null) {
			jPanelErrTol = new JPanel();
			jPanelErrTol.setLayout(new BorderLayout());
			// jPanelErrTol.setPreferredSize(new Dimension(250,18));
			jLabelErrTol = new JLabel("Error tolerance: ");
			jFormattedTextFieldErrTol = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldErrTol.addPropertyChangeListener("value", this);
			jFormattedTextFieldErrTol.setInputVerifier(new FloatNumberVerifier());
			// jFormattedTextFieldErrTol.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
			jFormattedTextFieldErrTol.setColumns(10);
			jPanelErrTol.add(jLabelErrTol, BorderLayout.WEST);
			jPanelErrTol.add(jFormattedTextFieldErrTol, BorderLayout.CENTER);
			// jFormattedTextFieldErrTol.setEnabled(false);
		}
		return jPanelErrTol;
	}
	
	private JPanel getPnlAlgSettings() {
		if (pnlAlgSettings == null) {
			pnlAlgSettings = new JPanel();
			pnlAlgSettings.setLayout(new GridBagLayout()); 
			//pnlAlgSettings.setLayout(new BoxLayout(pnlAlgSettings, BoxLayout.Y_AXIS));
			//pnlAlgSettings.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			pnlAlgSettings.setBorder(new TitledBorder(null, "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
					
			GridBagConstraints gbc_1= new GridBagConstraints();
			gbc_1.gridx = 0;
			gbc_1.gridy = 0;
			gbc_1.gridwidth = 1;
			gbc_1.insets = new Insets(5, 5, 0, 5); // top  left  bottom  right
			//gbc_1.fill = GridBagConstraints.BOTH;
			gbc_1.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_2 = new GridBagConstraints();
			gbc_2.gridx = 0;
			gbc_2.gridy = 1;
			gbc_2.gridwidth = 1;
			gbc_2.insets = new Insets(5, 5, 0, 5); // top  left  bottom  right
			//gbc_2.fill = GridBagConstraints.BOTH;
			gbc_2.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_3 = new GridBagConstraints();
			gbc_3.gridx = 0;
			gbc_3.gridy = 2;
			gbc_3.gridwidth = 1;
			gbc_3.insets = new Insets(5, 5, 0, 5); // top  left  bottom  right
			//gbc_3.fill = GridBagConstraints.BOTH;
			gbc_3.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_4 = new GridBagConstraints();
			gbc_4.gridx = 0;
			gbc_4.gridy = 3;
			gbc_4.gridwidth = 1;
			gbc_4.insets = new Insets(5, 5, 5, 5); // top  left  bottom  right
			//gbc_4.fill = GridBagConstraints.BOTH;
			gbc_4.anchor = GridBagConstraints.EAST;
				
			pnlAlgSettings.add(getJSpinnerPyrLevels(), gbc_1);
			pnlAlgSettings.add(getJPanelTempUpCo(),    gbc_2);
			pnlAlgSettings.add(getJPanelMaxIt(),       gbc_3);
			pnlAlgSettings.add(getJPanelErrTol(),      gbc_4);
		}
		return pnlAlgSettings;
	}

	// jCheckBox--------------------------------------------------------------------------------------------

	/**
	 * This method initializes jCheckBoxLogCo
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxLogCo() {
		if (jCheckBoxLogCo == null) {
			jCheckBoxLogCo = new JCheckBox();
			jCheckBoxLogCo.setText("Log transformation coefficients");
			jCheckBoxLogCo.addActionListener(this);
			jCheckBoxLogCo.setActionCommand("parameter");
			jCheckBoxLogCo.setEnabled(false);
		}
		return jCheckBoxLogCo;
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	// ------------------------------------------------------------------------------------------------
	@Override
	public void stateChanged(ChangeEvent e) {
		Object obE = e.getSource();
		if (obE == jSpinnerPyrLevels) {
			pyrLevels = ((Number) jSpinnerPyrLevels.getValue()).intValue();
		}
		this.updateParameterBlock();

		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}

}// END
