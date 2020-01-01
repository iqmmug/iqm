package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_DCMGenerator.java
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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
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
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpDCMGeneratorDescriptor;

/**
 * @author Ahammer
 * @since  2012 12
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class PlotGUI_DCMGenerator extends AbstractPlotOperatorGUI implements
		ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8557561248409563634L;

	private static final Logger logger = LogManager.getLogger(PlotGUI_DCMGenerator.class);

	private ParameterBlockIQM pb;

	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;
	private JRadioButton buttLogistic    = null;
	private JRadioButton buttHenon       = null;
	private JRadioButton buttCubic       = null;
	private JRadioButton buttSpence      = null;

	private JPanel   jPanelNumSignals   = null;
	private JLabel   jLabelNumSignals   = null;
	private JSpinner jSpinnerNumSignals = null;

	private JPanel              jPanelParamA              = null;
	private JLabel              jLabelParamA              = null;
	private JFormattedTextField jFormattedTextFieldParamA = null;

	private JPanel              jPanelParamB = null;
	private JLabel              jLabelParamB = null;
	private JFormattedTextField jFormattedTextFieldParamB = null;

	private JPanel   jPanelNumDataPoints   = null;
	private JLabel   jLabelNumDataPoints   = null;
	private JSpinner jSpinnerNumDataPoints = null;
	private JPanel   jPanelDCMParameters   = null;

	public PlotGUI_DCMGenerator() {
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpDCMGeneratorDescriptor().getName());
		this.initialize();
		this.setTitle("Discrete Chaotic Map Generator");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelMethod(),        getGridBagConstraints_Method());
		this.getOpGUIContent().add(getJPanelNumSignals(),    getGridBagConstraints_NumSignals());
		this.getOpGUIContent().add(getJPanelDCMParameters(), getGridBagConstraints_DCMParameters());

		this.pack();

	}

	private GridBagConstraints getGridBagConstraints_Method() {
		GridBagConstraints gbc_Method = new GridBagConstraints();
		gbc_Method.gridx = 0;
		gbc_Method.gridy = 0;
		gbc_Method.insets = new Insets(10, 0, 0, 0); // top left  bottom right
		gbc_Method.fill = GridBagConstraints.BOTH;
		return gbc_Method;
	}

	private GridBagConstraints getGridBagConstraints_NumSignals() {
		GridBagConstraints gbc_NumSignals = new GridBagConstraints();
		gbc_NumSignals.gridx = 0;
		gbc_NumSignals.gridy = 1;
		gbc_NumSignals.insets = new Insets(5, 0, 0, 0); // top  left  bottom  right
		gbc_NumSignals.fill = GridBagConstraints.BOTH;
		return gbc_NumSignals;
	}
	private GridBagConstraints getGridBagConstraints_DCMParameters(){
		GridBagConstraints gbc_DCMParameters = new GridBagConstraints();
		gbc_DCMParameters.gridx = 0;
		gbc_DCMParameters.gridy = 2;
		gbc_DCMParameters.insets = new Insets(5, 0, 5, 0); // top  left  bottom  right
		gbc_DCMParameters.fill = GridBagConstraints.BOTH;
		return gbc_DCMParameters;
	}

	// ----------------------------------------------------------------------------------------------------------
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		switch (pb.getIntParameter("method")) {
		case 0:
			this.buttLogistic.setSelected(true);
			break;
		case 1:
			this.buttHenon.setSelected(true);
			break;
		case 2:
			this.buttCubic.setSelected(true);
			break;

		case 3:
			this.buttSpence.setSelected(true);
			break;
		}

		jFormattedTextFieldParamA.setText(String.valueOf(pb.getDoubleParameter("paramA")));
		jFormattedTextFieldParamB.setText(String.valueOf(pb.getDoubleParameter("paramB")));

		jSpinnerNumDataPoints.removeChangeListener(this);
		jSpinnerNumDataPoints.setValue(pb.getIntParameter("powerNDataPoints"));
		jSpinnerNumDataPoints.addChangeListener(this);

	}

	@Override
	public void updateParameterBlock() {
		if (this.buttLogistic.isSelected())  pb.setParameter("method", 0);
		if (this.buttHenon.isSelected())     pb.setParameter("method", 1);
		if (this.buttCubic.isSelected())     pb.setParameter("method", 2);
		if (this.buttSpence.isSelected())    pb.setParameter("method", 3);

		pb.setParameter("paramA",  ((Number) jFormattedTextFieldParamA.getValue()).doubleValue());
		pb.setParameter("paramB",  ((Number) jFormattedTextFieldParamB.getValue()).doubleValue());
		
		pb.setParameter("powerNDataPoints", ((Number) jSpinnerNumDataPoints.getValue()).intValue());

		this.workPackage.setIterations(((Number) jSpinnerNumSignals.getValue()).intValue());
	}
	//----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Sinus
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSinus() {
		if (buttLogistic == null) {
			buttLogistic = new JRadioButton();
			buttLogistic.setText("Logistic");
			buttLogistic.setToolTipText("generates a Logistic map");
			buttLogistic.addActionListener(this);
			buttLogistic.setActionCommand("parameter");
			buttLogistic.setSelected(true);
		}
		return buttLogistic;
	}

	/**
	 * This method initializes the Option: Const
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonConst() {
		if (buttHenon == null) {
			buttHenon = new JRadioButton();
			buttHenon.setText("Henon");
			buttHenon.setToolTipText("generates a Henon map");
			buttHenon.addActionListener(this);
			buttHenon.setActionCommand("parameter");
			buttHenon.setEnabled(true);
			buttHenon.setVisible(true);

		}
		return buttHenon;
	}

	/**
	 * This method initializes the Option: Cubic
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonCubic() {
		if (buttCubic == null) {
			buttCubic = new JRadioButton();
			buttCubic.setText("Cubic");
			buttCubic.setToolTipText("generates a Cubic map");
			buttCubic.addActionListener(this);
			buttCubic.setActionCommand("parameter");
			buttCubic.setEnabled(true);
			buttCubic.setVisible(true);
		}
		return buttCubic;
	}

	/**
	 * This method initializes the Option: Spence
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSpence() {
		if (buttSpence == null) {
			buttSpence = new JRadioButton();
			buttSpence.setText("Spence");
			buttSpence.setToolTipText("generates a Spence map");
			buttSpence.addActionListener(this);
			buttSpence.setActionCommand("parameter");
			buttSpence.setEnabled(true);
			buttSpence.setVisible(true);
		}
		return buttSpence;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		// if (jPanelMethod == null) {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.X_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethod.setBorder(new TitledBorder(null, "Chaotic map", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethod.add(getJRadioButtonSinus());
		jPanelMethod.add(getJRadioButtonConst());
		jPanelMethod.add(getJRadioButtonCubic());
		jPanelMethod.add(getJRadioButtonSpence());

		// jPanelMethod.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		// }
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroupMethod == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttLogistic);
		buttGroupMethod.add(buttHenon);
		buttGroupMethod.add(buttCubic);
		buttGroupMethod.add(buttSpence);
	}

	

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes jJPanelNumSignals
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumSignals() {
		if (jPanelNumSignals == null) {
			jPanelNumSignals = new JPanel();
			//jPanelNumSignals.setLayout(new BoxLayout(jPanelNumSignals, BoxLayout.Y_AXIS));
			jPanelNumSignals.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelNumSignals.setBorder(new TitledBorder(null, "Number of signals", TitledBorder.LEADING, TitledBorder.TOP, null, null));			
			jLabelNumSignals = new JLabel("#: ");
			jLabelNumSignals.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 1;
			SpinnerModel sModel = new SpinnerNumberModel(init, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumSignals = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumSignals.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#");   // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerNumSignals.setValue(1);    // only in order to set format pattern
			jSpinnerNumSignals.setValue(init); // only in order to set format pattern
			jSpinnerNumSignals.addChangeListener(this);
			jPanelNumSignals.add(jLabelNumSignals);
			jPanelNumSignals.add(jSpinnerNumSignals);
			jLabelNumSignals.setEnabled(true);
			jSpinnerNumSignals.setEnabled(true);

		}
		return jPanelNumSignals;
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes jJPanelParamA
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelParamA() {
		if (jPanelParamA == null) {
			jPanelParamA = new JPanel();
			jPanelParamA.setBorder(new EmptyBorder(0, 5, 0, 5));
			jPanelParamA.setLayout(new BorderLayout());
			jLabelParamA = new JLabel("a: ");
			jLabelParamA.setHorizontalAlignment(SwingConstants.RIGHT);
			double init = 4.0d;
			jFormattedTextFieldParamA = new JFormattedTextField(init);
			jFormattedTextFieldParamA.setColumns(7);
			jFormattedTextFieldParamA.addActionListener(this);
			jFormattedTextFieldParamA.setActionCommand("parama");
			jPanelParamA.add(jLabelParamA, BorderLayout.WEST);
			jPanelParamA.add(jFormattedTextFieldParamA, BorderLayout.CENTER);
		}
		return jPanelParamA;
	}

	/**
	 * This method initializes jJPanelParamB
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelParamB() {
		if (jPanelParamB == null) {
			jPanelParamB = new JPanel();
			jPanelParamB.setBorder(new EmptyBorder(0, 5, 0, 5));
			jPanelParamB.setLayout(new BorderLayout());
			jLabelParamB = new JLabel("b: ");
			jLabelParamB.setHorizontalAlignment(SwingConstants.RIGHT);
			double init = 0.3d;
			jFormattedTextFieldParamB = new JFormattedTextField(init);
			jFormattedTextFieldParamB.setColumns(7);
			jFormattedTextFieldParamB.addActionListener(this);
			jFormattedTextFieldParamB.setActionCommand("paramb");
			jPanelParamB.add(jLabelParamB, BorderLayout.WEST);
			jPanelParamB.add(jFormattedTextFieldParamB, BorderLayout.CENTER);
			jLabelParamB.setEnabled(false);
			jFormattedTextFieldParamB.setEnabled(false);
		}
		return jPanelParamB;
	}
	/**
	 * This method initializes jJPanelNumDataPoints
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumDataPoints() {
		if (jPanelNumDataPoints == null) {
			jPanelNumDataPoints = new JPanel();
			jPanelNumDataPoints.setBorder(new EmptyBorder(0, 5, 5, 5));
			jPanelNumDataPoints.setLayout(new BorderLayout());
			jLabelNumDataPoints = new JLabel("Number data points 2^n: ");
			jLabelNumDataPoints.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 10;
			SpinnerModel sModel = new SpinnerNumberModel(init, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumDataPoints = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumDataPoints.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#");      // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerNumDataPoints.setValue(1);    // only in order to set format pattern
			jSpinnerNumDataPoints.setValue(init); // only in order to set format pattern
			jSpinnerNumDataPoints.addChangeListener(this);
			jPanelNumDataPoints.add(jLabelNumDataPoints, BorderLayout.WEST);
			jPanelNumDataPoints.add(jSpinnerNumDataPoints, BorderLayout.CENTER);
			jLabelNumDataPoints.setEnabled(true);
			jSpinnerNumDataPoints.setEnabled(true);
		}
		return jPanelNumDataPoints;
	}
	
	private JPanel getJPanelDCMParameters() {
		if (jPanelDCMParameters == null) {
			jPanelDCMParameters = new JPanel();
			jPanelDCMParameters.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "DCM parameters",
					                  TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			GridBagLayout gbl_parametersPanel = new GridBagLayout();
			gbl_parametersPanel.columnWidths = new int[] { 0, 0 };
			gbl_parametersPanel.rowHeights = new int[] { 0, 0, 0, 0 };
			gbl_parametersPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
			gbl_parametersPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
			jPanelDCMParameters.setLayout(gbl_parametersPanel);
		
			GridBagConstraints gbc_1 = new GridBagConstraints();
			gbc_1.anchor = GridBagConstraints.EAST;
			gbc_1.insets = new Insets(5, 0, 0, 0);
			gbc_1.gridx = 0;
			gbc_1.gridy = 0;
			
		
			GridBagConstraints gbc_2 = new GridBagConstraints();
			gbc_2.anchor = GridBagConstraints.EAST;
			gbc_2.insets = new Insets(5, 0, 0, 0);
			gbc_2.gridx = 0;
			gbc_2.gridy = 1;
			
			
			GridBagConstraints gbc_3 = new GridBagConstraints();
			gbc_3.anchor = GridBagConstraints.EAST;
			gbc_3.gridx = 0;
			gbc_3.gridy = 2;
			gbc_3.insets = new Insets(5, 0, 0, 0);
			
			jPanelDCMParameters.add(getJPanelParamA(),        gbc_1);
			jPanelDCMParameters.add(getJPanelParamB(),        gbc_2);
			jPanelDCMParameters.add(getJPanelNumDataPoints(), gbc_3);
			
			
			
		}
		return jPanelDCMParameters;
	}

	@Override
	public void update() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {

			if (e.getSource() == buttLogistic) {
				jLabelParamA.setEnabled(true);
				jFormattedTextFieldParamA.setEnabled(true);
				jFormattedTextFieldParamA.setValue(4.0d);

				jLabelParamB.setEnabled(false);
				jFormattedTextFieldParamB.setEnabled(false);

			}
			if (e.getSource() == buttHenon) {
				jLabelParamA.setEnabled(true);
				jFormattedTextFieldParamA.setEnabled(true);
				jFormattedTextFieldParamA.setValue(1.4d);

				jLabelParamB.setEnabled(true);
				jFormattedTextFieldParamB.setEnabled(true);
				jFormattedTextFieldParamB.setValue(0.3);

			}
			if (e.getSource() == buttCubic) {
				jLabelParamA.setEnabled(true);
				jFormattedTextFieldParamA.setEnabled(true);
				jFormattedTextFieldParamA.setValue(3.0d);

				jLabelParamB.setEnabled(false);
				jFormattedTextFieldParamB.setEnabled(false);
			}
			if (e.getSource() == buttSpence) {
				jLabelParamA.setEnabled(false);
				jFormattedTextFieldParamA.setEnabled(false);

				jLabelParamB.setEnabled(false);
				jFormattedTextFieldParamB.setEnabled(false);
			}
			if ("parama".equals(e.getActionCommand())) {
			}
			if ("paramb".equals(e.getActionCommand())) {
			}

			this.updateParameterBlock();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateParameterBlock();
	}



	
}
