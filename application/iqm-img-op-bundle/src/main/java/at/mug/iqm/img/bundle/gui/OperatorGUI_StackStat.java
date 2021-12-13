package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_StackStat.java
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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpStackStatDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2012 01
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_StackStat extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4182220982766100098L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_StackStat.class);

	private ParameterBlockIQM pb = null;
;

	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;
	private JRadioButton buttRange       = null;
	private JRadioButton buttMean        = null;
	private JRadioButton buttStDev       = null;
	private JRadioButton buttEnergy      = null;
	private JRadioButton buttEntropy     = null;
	private JRadioButton buttSkewness    = null;
	private JRadioButton buttKurtosis    = null;
	private JRadioButton buttAlikeness   = null;
	
	private JPanel       jPanelGreyTolerance   = null;
	private JLabel       jLabelGreyTolerance   = null;
	private JSpinner     jSpinnerGreyTolerance = null;
	private TitledBorder tbGreyTolerance       = null;

	private JRadioButton buttClamp              = null;
	private JRadioButton buttNormalize          = null;
	private JRadioButton buttActual             = null;
	private JPanel       jPanelResultOptions    = null;
	private ButtonGroup  buttGroupResultOptions = null;

	/**
	 * constructor
	 */
	public OperatorGUI_StackStat() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpStackStatDescriptor().getName());
		this.initialize();
		this.setTitle("Stack Statistics");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelGreyTolerance(),  getGridBagConstraints_GreyTolerance());
		this.getOpGUIContent().add(getJPanelMethod(),         getGridBagConstraints_Method());
		this.getOpGUIContent().add(getJPanelResultOptions(),  getGridBagConstraints_ResultOptions());

		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_Method() {
		GridBagConstraints gbc_Method = new GridBagConstraints();
		gbc_Method.gridx = 0;
		gbc_Method.gridy = 0;
		gbc_Method.gridwidth = 1;// ?
		gbc_Method.insets = new Insets(10, 0, 0, 0); // top left  bottom  right
		gbc_Method.fill = GridBagConstraints.BOTH;
		return gbc_Method;
	}

	private GridBagConstraints getGridBagConstraints_GreyTolerance() {
		GridBagConstraints gbc_GreyTolerance = new GridBagConstraints();
		gbc_GreyTolerance.gridx = 0;
		gbc_GreyTolerance.gridy = 1;
		gbc_GreyTolerance.gridwidth = 1;// ?
		gbc_GreyTolerance.insets = new Insets(5, 0, 0, 0); // top  left  bottom  right
		gbc_GreyTolerance.fill = GridBagConstraints.BOTH;
		return gbc_GreyTolerance;
	}

	private GridBagConstraints getGridBagConstraints_ResultOptions() {
		GridBagConstraints gbc_ResultOptions = new GridBagConstraints();
		gbc_ResultOptions.gridx = 0;
		gbc_ResultOptions.gridy = 2;
		gbc_ResultOptions.gridwidth = 1;//
		gbc_ResultOptions.insets = new Insets(5, 0, 5, 0); // top  left  bottom  right
		gbc_ResultOptions.fill = GridBagConstraints.BOTH;
		return gbc_ResultOptions;
	}


	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		if (buttRange.isSelected())     pb.setParameter("Method", 0);
		if (buttMean.isSelected())      pb.setParameter("Method", 1);
		if (buttStDev.isSelected())     pb.setParameter("Method", 2);
		if (buttEnergy.isSelected())    pb.setParameter("Method", 3);
		if (buttEntropy.isSelected())   pb.setParameter("Method", 4);
		if (buttSkewness.isSelected())  pb.setParameter("Method", 5);
		if (buttKurtosis.isSelected())  pb.setParameter("Method", 6);
		if (buttAlikeness.isSelected()) pb.setParameter("Method", 7);
		
		pb.setParameter("GreyTolerance", ((Number) jSpinnerGreyTolerance.getValue()).intValue());

		if (buttClamp.isSelected())     pb.setParameter("ResultOptions", 0);
		if (buttNormalize.isSelected()) pb.setParameter("ResultOptions", 1);
		if (buttActual.isSelected())    pb.setParameter("ResultOptions", 2);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Method") == 0) buttRange.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttMean.setSelected(true);
		if (pb.getIntParameter("Method") == 2) buttStDev.setSelected(true);
		if (pb.getIntParameter("Method") == 3) buttEnergy.setSelected(true);
		if (pb.getIntParameter("Method") == 4) buttEntropy.setSelected(true);
		if (pb.getIntParameter("Method") == 5) buttSkewness.setSelected(true);
		if (pb.getIntParameter("Method") == 6) buttKurtosis.setSelected(true);
		if (pb.getIntParameter("Method") == 7) buttAlikeness.setSelected(true);
		
		jSpinnerGreyTolerance.removeChangeListener(this);
		jSpinnerGreyTolerance.setValue(pb.getIntParameter("GreyTolerance"));
		jSpinnerGreyTolerance.addChangeListener(this);

		if (pb.getIntParameter("ResultOptions") == 0) buttClamp.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 1) buttNormalize.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 2) buttActual.setSelected(true);

		this.update();
	}


	/**
	 * This method updates the GUI. This method overrides OperatorGUI
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// if (buttRange.isSelected() ||
		// buttMean.isSelected() ||
		// buttMean.isSelected() ||
		// buttStDev.isSelected() ||
		// buttEnergy.isSelected() ||
		// buttEntropy.isSelected() ||
		// buttSkewness.isSelected() ||
		// buttKurtosis.isSelected())
		// {
		// //do nothing
		// }
		if (buttAlikeness.isSelected()) {
			jPanelGreyTolerance.setEnabled(true);
			jLabelGreyTolerance.setEnabled(true);
			jSpinnerGreyTolerance.setEnabled(true);
			tbGreyTolerance.setTitleColor(Color.BLACK); repaint();		
		} else {
			jPanelGreyTolerance.setEnabled(false);
			jLabelGreyTolerance.setEnabled(false);
			jSpinnerGreyTolerance.setEnabled(false);
			tbGreyTolerance.setTitleColor(Color.GRAY); repaint();	
		}

	}


	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: Range
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonRange() {
		if (buttRange == null) {
			buttRange = new JRadioButton();
			buttRange.setText("Range");
			buttRange.setToolTipText("Range");
			buttRange.addActionListener(this);
			buttRange.setActionCommand("parameter");
			buttRange.setEnabled(true);
		}
		return buttRange;
	}

	/**
	 * This method initializes the Option: Mean
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMean() {
		if (buttMean == null) {
			buttMean = new JRadioButton();
			buttMean.setText("Mean");
			buttMean.setToolTipText("Mean");
			buttMean.addActionListener(this);
			buttMean.setActionCommand("parameter");
		}
		return buttMean;
	}

	/**
	 * This method initializes the Option: StDev
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonStDev() {
		if (buttStDev == null) {
			buttStDev = new JRadioButton();
			buttStDev.setText("Standard Deviation");
			buttStDev.setToolTipText("standard deviation");
			buttStDev.addActionListener(this);
			buttStDev.setActionCommand("parameter");
		}
		return buttStDev;
	}

	/**
	 * This method initializes the Option: Energy
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonEnergy() {
		if (buttEnergy == null) {
			buttEnergy = new JRadioButton();
			buttEnergy.setText("Energy");
			buttEnergy.setToolTipText("energy");
			buttEnergy.addActionListener(this);
			buttEnergy.setActionCommand("parameter");
			buttEnergy.setEnabled(true);
		}
		return buttEnergy;
	}

	/**
	 * This method initializes the Option: Entropy
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonEntropy() {
		if (buttEntropy == null) {
			buttEntropy = new JRadioButton();
			buttEntropy.setText("Entropy");
			buttEntropy.setToolTipText("Entropy");
			buttEntropy.addActionListener(this);
			buttEntropy.setActionCommand("parameter");
			buttEntropy.setEnabled(true);
		}
		return buttEntropy;
	}

	/**
	 * This method initializes the Option: Skewness
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSkewness() {
		if (buttSkewness == null) {
			buttSkewness = new JRadioButton();
			buttSkewness.setText("Skewness");
			buttSkewness.setToolTipText("Skewness");
			buttSkewness.addActionListener(this);
			buttSkewness.setActionCommand("parameter");
			buttSkewness.setEnabled(true);
		}
		return buttSkewness;
	}

	/**
	 * This method initializes the Option: Kurtosis
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonKurtosis() {
		if (buttKurtosis == null) {
			buttKurtosis = new JRadioButton();
			buttKurtosis.setText("Kurtosis");
			buttKurtosis.setToolTipText("Kurtosis");
			buttKurtosis.addActionListener(this);
			buttKurtosis.setActionCommand("parameter");
			buttKurtosis.setEnabled(true);
		}
		return buttKurtosis;
	}

	/**
	 * This method initializes the Option: Alikeness
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAlikeness() {
		if (buttAlikeness == null) {
			buttAlikeness = new JRadioButton();
			buttAlikeness.setText("Alikeness");
			buttAlikeness.setToolTipText("Alikeness");
			buttAlikeness.addActionListener(this);
			buttAlikeness.setActionCommand("parameter");
			buttAlikeness.setEnabled(true);
		}
		return buttAlikeness;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		// if (jPanelMethod == null) {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethod.add(getJRadioButtonRange());
		jPanelMethod.add(getJRadioButtonMean());
		jPanelMethod.add(getJRadioButtonStDev());
		jPanelMethod.add(getJRadioButtonEnergy());
		jPanelMethod.add(getJRadioButtonEntropy());
		jPanelMethod.add(getJRadioButtonSkewness());
		jPanelMethod.add(getJRadioButtonKurtosis());
		jPanelMethod.add(getJRadioButtonAlikeness());

		// jPanelMethod.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		// }
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroupMethod == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttRange);
		buttGroupMethod.add(buttMean);
		buttGroupMethod.add(buttStDev);
		buttGroupMethod.add(buttEnergy);
		buttGroupMethod.add(buttEntropy);
		buttGroupMethod.add(buttSkewness);
		buttGroupMethod.add(buttKurtosis);
		buttGroupMethod.add(buttAlikeness);
	}

	// ---------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelGreyTolerance
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelGreyTolerance() {
		if (jPanelGreyTolerance == null) {
			jPanelGreyTolerance = new JPanel();
			jPanelGreyTolerance.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));
			tbGreyTolerance = new TitledBorder(null, "Grey value tolerance", TitledBorder.LEADING, TitledBorder.TOP, null, null);
			jPanelGreyTolerance.setBorder(tbGreyTolerance);
			jLabelGreyTolerance = new JLabel("% ");
			// jLabelGreyTolerance.setPreferredSize(new Dimension(70, 20));
			jLabelGreyTolerance.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 0, 100, 1); // init, min, max, step
			jSpinnerGreyTolerance = new JSpinner(sModel);
			// jSpinnerGreyTolerance = new JSpinner();
			//jSpinnerGreyTolerance.setPreferredSize(new Dimension(60, 20));
			jSpinnerGreyTolerance.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerGreyTolerance.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelGreyTolerance.add(jLabelGreyTolerance, BorderLayout.WEST);
			jPanelGreyTolerance.add(jSpinnerGreyTolerance, BorderLayout.CENTER);
		}
		return jPanelGreyTolerance;
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
		jPanelResultOptions.setBorder(new TitledBorder(null, "Result options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelResultOptions.add(getJRadioButtonClamp());
		jPanelResultOptions.add(getJRadioButtonNormalize());
		jPanelResultOptions.add(getJRadioButtonActual());

		this.setButtonGroupResultOptions(); // Grouping of JRadioButtons
		// }
		return jPanelResultOptions;
	}

	private void setButtonGroupResultOptions() {
		buttGroupResultOptions = new ButtonGroup();
		buttGroupResultOptions.add(buttClamp);
		buttGroupResultOptions.add(buttNormalize);
		buttGroupResultOptions.add(buttActual);
	}


	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.update(); // if necessary here or some lines above
			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
}// END
