package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_CalcValue.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpCalcValueDescriptor;

/**
 * @author Ahammer, Kainz
 * @since 2009 05
 * @update 2014 12 changed buttons to JRadiobuttons and added some TitledBorder
 */
public class OperatorGUI_CalcValue extends AbstractImageOperatorGUI implements
		ActionListener, AdjustmentListener, PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -419828425410763232L;

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(OperatorGUI_CalcValue.class);

	private ParameterBlockIQM pb = null;

	private JRadioButton buttAdd            = null;
	private JRadioButton buttSubtr          = null;
	private JRadioButton buttMult           = null;
	private JRadioButton buttDiv            = null;
	private JRadioButton buttSubtrFromConst = null;
	private JRadioButton buttDivIntoConst   = null;
	private JRadioButton buttAND            = null;
	private JRadioButton buttOR             = null;
	private JRadioButton buttXOR            = null;
	private JPanel       jPanelCalc         = null;
	private ButtonGroup  buttGroupCalc      = null;

	private JPanel       jPanelResultOptions    = null;
	private ButtonGroup  buttGroupResultOptions = null;
	private JRadioButton buttClamp              = null;
	private JRadioButton buttNormalize          = null;
	private JRadioButton buttActual             = null;

	private JPanel              jPanelValue              = null;
	private JLabel              jLabelValue              = null;
	private JFormattedTextField jFormattedTextFieldValue = null;

	/**
	 * constructor
	 */
	public OperatorGUI_CalcValue() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpCalcValueDescriptor().getName());
		this.initialize();
		this.setTitle("Value Calculation");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelCalc(),          getGridBagConstraintsCalc());
		this.getOpGUIContent().add(getJPanelValue(),         getGridBagConstraintsValue());
		this.getOpGUIContent().add(getJPanelResultOptions(), getGridBagConstraintsResultOptions());

		this.pack();
	}

	@Override
	public void updateParameterBlock() {

		if (buttAdd.isSelected())            pb.setParameter("Calc", 0);
		if (buttSubtr.isSelected())          pb.setParameter("Calc", 1);
		if (buttMult.isSelected())           pb.setParameter("Calc", 2);
		if (buttDiv.isSelected())            pb.setParameter("Calc", 3);
		if (buttSubtrFromConst.isSelected()) pb.setParameter("Calc", 4);
		if (buttDivIntoConst.isSelected())   pb.setParameter("Calc", 5);
		if (buttAND.isSelected())            pb.setParameter("Calc", 6);
		if (buttOR.isSelected())             pb.setParameter("Calc", 7);
		if (buttXOR.isSelected())            pb.setParameter("Calc", 8);

		pb.setParameter("Value", ((Number) jFormattedTextFieldValue.getValue()).doubleValue());

		if (buttClamp.isSelected())      pb.setParameter("ResultOptions", 0);
		if (buttNormalize.isSelected())  pb.setParameter("ResultOptions", 1);
		if (buttActual.isSelected())     pb.setParameter("ResultOptions", 2);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Calc") == 0)  buttAdd.setSelected(true);
		if (pb.getIntParameter("Calc") == 1)  buttSubtr.setSelected(true);
		if (pb.getIntParameter("Calc") == 2)  buttMult.setSelected(true);
		if (pb.getIntParameter("Calc") == 3)  buttDiv.setSelected(true);
		if (pb.getIntParameter("Calc") == 4)  buttSubtrFromConst.setSelected(true);
		if (pb.getIntParameter("Calc") == 5)  buttDivIntoConst.setSelected(true);
		if (pb.getIntParameter("Calc") == 6)  buttAND.setSelected(true);
		if (pb.getIntParameter("Calc") == 7)  buttOR.setSelected(true);
		if (pb.getIntParameter("Calc") == 8)  buttXOR.setSelected(true);

		jFormattedTextFieldValue.removePropertyChangeListener("value", this);
		jFormattedTextFieldValue.setValue(pb.getDoubleParameter("Value"));
		jFormattedTextFieldValue.addPropertyChangeListener("value", this);

		if (pb.getIntParameter("ResultOptions") == 0)  buttClamp.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 1)  buttNormalize.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 2)  buttActual.setSelected(true);
	}

	private GridBagConstraints getGridBagConstraintsCalc() {
		GridBagConstraints gridBagConstraintsCalc = new GridBagConstraints();
		gridBagConstraintsCalc.gridx = 0;
		gridBagConstraintsCalc.gridy = 0;
		// gridBagConstraintsCalc.gridwidth = 3;
		gridBagConstraintsCalc.gridheight = 2;
		gridBagConstraintsCalc.insets = new Insets(10, 0, 0, 0); // top left  bottom  right
		// gridBagConstraintsCalc.anchor = GridBagConstraints.LINE_START;
		// gridBagConstraintsCalc.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsCalc;
	}

	private GridBagConstraints getGridBagConstraintsValue() {
		GridBagConstraints gridBagConstraintsValue = new GridBagConstraints();
		gridBagConstraintsValue.gridx = 1;
		gridBagConstraintsValue.gridy = 0;
		gridBagConstraintsValue.gridwidth = 1;// ?
		gridBagConstraintsValue.insets = new Insets(10, 10, 0, 0); // top left  bottom  right
		// gridBagConstraintsValue.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsValue;
	}

	private GridBagConstraints getGridBagConstraintsResultOptions() {
		GridBagConstraints gridBagConstraintsResultOptions = new GridBagConstraints();
		gridBagConstraintsResultOptions.gridx = 1;
		gridBagConstraintsResultOptions.gridy = 1;
		gridBagConstraintsResultOptions.gridwidth = 1;// ?
		gridBagConstraintsResultOptions.insets = new Insets(10, 10, 0, 0); // top  left  bottom  right
		// gridBagConstraintsResultOptions.fill = GridBagConstraints.BOTH;
		gridBagConstraintsResultOptions.anchor = GridBagConstraints.SOUTH;
		return gridBagConstraintsResultOptions;
	}

	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	/**
	 * This method initializes the Option: Add
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAdd() {
		if (buttAdd == null) {
			buttAdd = new JRadioButton();
			buttAdd.setText("Image  +  c");
			buttAdd.setToolTipText("adds constant to image");
			buttAdd.addActionListener(this);
			buttAdd.setActionCommand("parameter");
		}
		return buttAdd;
	}

	/**
	 * This method initializes the Option: Subtr
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSubtr() {
		if (buttSubtr == null) {
			buttSubtr = new JRadioButton();
			buttSubtr.setText("Image  -  c");
			buttSubtr.setToolTipText("subtracts constant from image");
			buttSubtr.addActionListener(this);
			buttSubtr.setActionCommand("parameter");
		}
		return buttSubtr;
	}

	/**
	 * This method initializes the Option: Mult
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMult() {
		if (buttMult == null) {
			buttMult = new JRadioButton();
			buttMult.setText("Image  X  c");
			buttMult.setToolTipText("multiply image by constant");
			buttMult.addActionListener(this);
			buttMult.setActionCommand("parameter");
		}
		return buttMult;
	}

	/**
	 * This method initializes the Option: Div
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDiv() {
		if (buttDiv == null) {
			buttDiv = new JRadioButton();
			buttDiv.setText("Image  /  c");
			buttDiv.setToolTipText("divide image by constant");
			buttDiv.addActionListener(this);
			buttDiv.setActionCommand("parameter");
		}
		return buttDiv;
	}

	/**
	 * This method initializes the Option: SubtrFromConst
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSubtrFromConst() {
		if (buttSubtrFromConst == null) {
			buttSubtrFromConst = new JRadioButton();
			buttSubtrFromConst.setText("c  -  Image");
			buttSubtrFromConst.setToolTipText("subtracts image from constant");
			buttSubtrFromConst.addActionListener(this);
			buttSubtrFromConst.setActionCommand("parameter");
		}
		return buttSubtrFromConst;
	}

	/**
	 * This method initializes the Option: DivIntoConst
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDivIntoConst() {
		if (buttDivIntoConst == null) {
			buttDivIntoConst = new JRadioButton();
			buttDivIntoConst.setText("c  /  Image");
			buttDivIntoConst.setToolTipText("divide constant by image");
			buttDivIntoConst.addActionListener(this);
			buttDivIntoConst.setActionCommand("parameter");
		}
		return buttDivIntoConst;
	}

	/**
	 * This method initializes the Option: AND
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAND() {
		if (buttAND == null) {
			buttAND = new JRadioButton();
			buttAND.setText("Image AND c");
			buttAND.setToolTipText("bitwise logical AND");
			buttAND.addActionListener(this);
			buttAND.setActionCommand("parameter");
			buttAND.setEnabled(true);
		}
		return buttAND;
	}

	/**
	 * This method initializes the Option: OR
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonOR() {
		if (buttOR == null) {
			buttOR = new JRadioButton();
			buttOR.setText("Image  OR  c");
			buttOR.setToolTipText("bitwise logical OR");
			buttOR.addActionListener(this);
			buttOR.setActionCommand("parameter");
			buttOR.setEnabled(true);
		}
		return buttOR;
	}

	/**
	 * This method initializes the Option: XOR
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonXOR() {
		if (buttXOR == null) {
			buttXOR = new JRadioButton();
			buttXOR.setText("Image XOR c");
			buttXOR.setToolTipText("bitwise logical XOR");
			buttXOR.addActionListener(this);
			buttXOR.setActionCommand("parameter");
			buttXOR.setEnabled(true);
		}
		return buttXOR;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelCalc() {
		// if (jPanelCalc == null) {
		jPanelCalc = new JPanel();
		jPanelCalc.setLayout(new BoxLayout(jPanelCalc, BoxLayout.Y_AXIS));
		//jPanelCalc.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelCalc.setBorder(new TitledBorder(null, "Calculation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelCalc.add(getJRadioButtonAdd());
		jPanelCalc.add(getJRadioButtonSubtr());
		jPanelCalc.add(getJRadioButtonMult());
		jPanelCalc.add(getJRadioButtonDiv());
		jPanelCalc.add(getJRadioButtonSubtrFromConst());
		jPanelCalc.add(getJRadioButtonDivIntoConst());
		jPanelCalc.add(getJRadioButtonAND());
		jPanelCalc.add(getJRadioButtonOR());
		jPanelCalc.add(getJRadioButtonXOR());

		// jPanelCalc.addSeparator();
		this.setButtonGroupCalc(); // Grouping of JRadioButtons
		// }
		return jPanelCalc;
	}

	private void setButtonGroupCalc() {
		// if (ButtonGroup buttGroupCalc == null) {
		buttGroupCalc = new ButtonGroup();
		buttGroupCalc.add(buttAdd);
		buttGroupCalc.add(buttSubtr);
		buttGroupCalc.add(buttMult);
		buttGroupCalc.add(buttDiv);
		buttGroupCalc.add(buttSubtrFromConst);
		buttGroupCalc.add(buttDivIntoConst);
		buttGroupCalc.add(buttAND);
		buttGroupCalc.add(buttOR);
		buttGroupCalc.add(buttXOR);
	}

	// ------------------------------------------------------------------------------------
	class FloatNumberVerifier extends InputVerifier {
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
	 * This method initializes jJPanelValue
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelValue() {
		if (jPanelValue == null) {
			jPanelValue = new JPanel();
			//jPanelValue.setLayout(new BoxLayout(jPanelValue, BoxLayout.Y_AXIS));
			jPanelValue.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelValue.setBorder(new TitledBorder(null, "Constant", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			// jPanelValue.setPreferredSize(new Dimension(250,18));
			jLabelValue = new JLabel("c: ");
			//jLabelValue.setPreferredSize(new Dimension(20, 20));
			jFormattedTextFieldValue = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldValue.addPropertyChangeListener("value", this);
			// jFormattedTextFieldValue.setInputVerifier(new FloatNumberVerifier());
			InternationalFormatter intFormatter = (InternationalFormatter) jFormattedTextFieldValue.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.000"); // decimalFormat.applyPattern("#,##0.0") ;
			// jFormattedTextFieldValue.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
			jFormattedTextFieldValue.setColumns(5);
			jPanelValue.add(jLabelValue);
			jPanelValue.add(jFormattedTextFieldValue);

		}
		return jPanelValue;
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Clamp
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonClamp() {
		if (buttClamp == null) {
			buttClamp = new JRadioButton();
			buttClamp.setText("Clamp");
			buttClamp.setToolTipText("clamps result to byte");
			buttClamp.addActionListener(this);
			buttClamp.setActionCommand("parameter");
		}
		return buttClamp;
	}

	/**
	 * This method initializes the Option: Normalize
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonNormalize() {
		if (buttNormalize == null) {
			buttNormalize = new JRadioButton();
			buttNormalize.setText("Normalize");
			// buttNormalize.setPreferredSize(new Dimension(90,10));
			buttNormalize.setToolTipText("normalize result to byte");
			buttNormalize.addActionListener(this);
			buttNormalize.setActionCommand("parameter");
		}
		return buttNormalize;
	}

	/**
	 * This method initializes the Option: Actual
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonActual() {
		if (buttActual == null) {
			buttActual = new JRadioButton();
			buttActual.setText("Actual");
			// buttActual.setPreferredSize(new Dimension(85,10));
			buttActual.setToolTipText("does nothing with result");
			buttActual.addActionListener(this);
			buttActual.setActionCommand("parameter");
			// buttActual.setEnabled(false);
		}
		return buttActual;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelResultOptions() {
		// if (jPanelResultOptions == null) {
		jPanelResultOptions = new JPanel();
		jPanelResultOptions.setLayout(new BoxLayout(jPanelResultOptions, BoxLayout.Y_AXIS));
		//jPanelResultOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelResultOptions.setBorder(new TitledBorder(null, "Result", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelResultOptions.add(getJRadioButtonClamp());
		jPanelResultOptions.add(getJRadioButtonNormalize());
		jPanelResultOptions.add(getJRadioButtonActual());

		// jPanelResultOptions.addSeparator();
		this.setButtonGroupResultOptions(); // Grouping of JRadioButtons
		// }
		return jPanelResultOptions;
	}

	private void setButtonGroupResultOptions() {
		// if (ButtonGroup buttGroupResultOptions == null) {
		buttGroupResultOptions = new ButtonGroup();
		buttGroupResultOptions.add(buttClamp);
		buttGroupResultOptions.add(buttNormalize);
		buttGroupResultOptions.add(buttActual);
	}


	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("OperatorGUI_CalcValue: event e: "
		// +e.getActionCommand());
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (jFormattedTextFieldValue == e.getSource()) {
			this.updateParameterBlock();
			// this.update(); //if necessary here or some lines above
		}
		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
}// END
