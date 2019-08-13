package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_FractalGenerator.java
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
import javax.swing.JCheckBox;
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

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;

/**
 * @author Ahammer
 * @since  2012 12
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class PlotGUI_FractalGenerator extends AbstractPlotOperatorGUI implements
		ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8557561248409563634L;

	private static final Logger logger = LogManager.getLogger(PlotGUI_FractalGenerator.class);

	private ParameterBlockIQM pb;

	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;
	private JRadioButton buttDHM         = null; // DHM Davis and Hart Method
	private JRadioButton buttSSM         = null; // SSM Spectral synthesis
													// method

	private JPanel   jPanelNumSignals   = null;
	private JLabel   jLabelNumSignals   = null;
	private JSpinner jSpinnerNumSignals = null;

	private JPanel              jPanelHurst              = null;
	private JLabel              jLabelHurst              = null;
	private JFormattedTextField jFormattedTextFieldHurst = null;

	private JPanel   jPanelNumDataPoints   = null;
	private JLabel   jLabelNumDataPoints   = null;
	private JSpinner jSpinnerNumDataPoints = null;

	private JPanel   jPanelStdDev   = null;
	private JLabel   jLabelStdDev   = null;
	private JSpinner jSpinnerStdDev = null;

	private JCheckBox jCheckBoxfGnTofBm  = null;
	private JCheckBox jCheckBoxfBmTofGn  = null;
	
	private JPanel    jPanelParameters   = null;

	public PlotGUI_FractalGenerator() {
		logger.debug("Now initializing...");

		this.initialize();
		this.setTitle("Fractal Signal Generator");	
		this.getOpGUIContent().setLayout(new GridBagLayout());
	
		getOpGUIContent().add(getJPanelMethod(),     getGridBagConstraints_Method());
		getOpGUIContent().add(getJPanelNumSignals(), getGridBagConstraints_NumSignals());
		getOpGUIContent().add(getJPanelParameters(), getGridBagConstraints_Parameters());

		this.pack();
	}

	private GridBagConstraints getGridBagConstraints_Method() {
		GridBagConstraints gbc_Method = new GridBagConstraints();
		gbc_Method.gridx = 0;
		gbc_Method.gridy = 0;
		gbc_Method.insets = new Insets(10, 0, 0, 0); // top left bottom right
		gbc_Method.fill = GridBagConstraints.BOTH;
		return gbc_Method;
	}

	private GridBagConstraints getGridBagConstraints_NumSignals() {
		GridBagConstraints gbc_NumSignals = new GridBagConstraints();
		gbc_NumSignals.gridx = 0;
		gbc_NumSignals.gridy = 1;
		gbc_NumSignals.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_NumSignals.fill = GridBagConstraints.BOTH;
		return gbc_NumSignals;
	}
	
	private GridBagConstraints getGridBagConstraints_Parameters(){
		GridBagConstraints gbc_Parameters = new GridBagConstraints();
		gbc_Parameters.gridx = 0;
		gbc_Parameters.gridy = 2;
		gbc_Parameters.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gbc_Parameters.fill = GridBagConstraints.BOTH;
		return gbc_Parameters;
	}
	
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();
		
		if (pb.getIntParameter("method") == 0){
			this.buttDHM.setSelected(true);
		}else if (pb.getIntParameter("method") == 1){
			this.buttSSM.setSelected(true);
		}
		
		jFormattedTextFieldHurst.setText(String.valueOf(pb.getDoubleParameter("hurst")));
		
		jSpinnerNumDataPoints.removeChangeListener(this);
		jSpinnerNumDataPoints.setValue(pb.getIntParameter("powerNDataPoints"));
		jSpinnerNumDataPoints.addChangeListener(this);
		
		if (pb.getIntParameter("fGnTofBm") == 0){
			jCheckBoxfGnTofBm.setSelected(false);
		}else if (pb.getIntParameter("fGnTofBm") == 1){
			jCheckBoxfGnTofBm.setSelected(true);
		}
		
		if (pb.getIntParameter("fBmTofGn") == 0){
			jCheckBoxfBmTofGn.setSelected(false);
		}
		else if (pb.getIntParameter("fBmTofGn") == 1){
			jCheckBoxfBmTofGn.setSelected(true);
		}
	}

	@Override
	public void updateParameterBlock() {
		if (this.buttDHM.isSelected())
			pb.setParameter("method", 0);
		if (this.buttSSM.isSelected())
			pb.setParameter("method", 1);
		
		pb.setParameter("hurst",((Number) jFormattedTextFieldHurst.getValue()).doubleValue());
		pb.setParameter("powerNDataPoints",((Number) jSpinnerNumDataPoints.getValue()).intValue());
		
		if (jCheckBoxfGnTofBm.isSelected()) {
			pb.setParameter("fGnTofBm", 1);
		} else {
			pb.setParameter("fGnTofBm", 0);
		}
		if (jCheckBoxfBmTofGn.isSelected()) {
			pb.setParameter("fBmTofGn", 1);
		} else {
			pb.setParameter("fBmTofGn", 0);
		}
		
		this.workPackage.setIterations(((Number) jSpinnerNumSignals.getValue())
				.intValue());
	}

	/**
	 * This method initializes the Option: DHM
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDHM() {
		if (buttDHM == null) {
			buttDHM = new JRadioButton();
			buttDHM.setText("fGn (DHM)");
			buttDHM.setToolTipText("generates fGn signals using Davis and Harte autocorrelation method");
			buttDHM.addActionListener(this);
			buttDHM.setActionCommand("parameter");
			buttDHM.setSelected(true);
		}
		return buttDHM;
	}

	/**
	 * This method initializes the Option: SSM
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSSM() {
		if (buttSSM == null) {
			buttSSM = new JRadioButton();
			buttSSM.setText("fBm (SSM)");
			buttSSM.setToolTipText("generates fBm signals using spectral synthesis method");
			buttSSM.addActionListener(this);
			buttSSM.setActionCommand("parameter");
			buttSSM.setEnabled(true);
			buttSSM.setVisible(true);

		}
		return buttSSM;
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
		jPanelMethod.add(getJRadioButtonDHM());
		jPanelMethod.add(getJRadioButtonSSM());

		// jPanelMethod.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		// }
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroupMethod == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttDHM);
		buttGroupMethod.add(buttSSM);
	}
	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes jJPanelHurst
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelHurst() {
		if (jPanelHurst == null) {
			jPanelHurst = new JPanel();
			jPanelHurst.setBorder(new EmptyBorder(0, 5, 0, 5));
			jPanelHurst.setLayout(new BorderLayout());
			jLabelHurst = new JLabel("Hurst [0,1]: ");
			jLabelHurst.setHorizontalAlignment(SwingConstants.RIGHT);
			double init = 0.5d;
			jFormattedTextFieldHurst = new JFormattedTextField(init);
			//jFormattedTextFieldHurst.setPreferredSize(new Dimension(60, 22));
			jFormattedTextFieldHurst.setColumns(7);
			jFormattedTextFieldHurst.addActionListener(this);
			jFormattedTextFieldHurst.setActionCommand("hurst");
			jPanelHurst.add(jLabelHurst, BorderLayout.WEST);
			jPanelHurst.add(jFormattedTextFieldHurst, BorderLayout.CENTER);
		}
		return jPanelHurst;
	}

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
			InternationalFormatter intFormatter = (InternationalFormatter) ftf .getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") / ;
			jSpinnerNumSignals.setValue(1); // only in order to set format  pattern
			jSpinnerNumSignals.setValue(init); // only in order to set format  pattern
			jSpinnerNumSignals.addChangeListener(this);
			jPanelNumSignals.add(jLabelNumSignals);
			jPanelNumSignals.add(jSpinnerNumSignals);
		}
		return jPanelNumSignals;
	}

	/**
	 * This method initializes jJPanelNumDataPoints
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumDataPoints() {
		if (jPanelNumDataPoints == null) {
			jPanelNumDataPoints = new JPanel();
			jPanelNumDataPoints.setBorder(new EmptyBorder(0, 5, 0, 5));
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
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerNumDataPoints.setValue(1); // only in order to set format pattern
			jSpinnerNumDataPoints.setValue(init); // only in order to set format pattern
			jSpinnerNumDataPoints.addChangeListener(this);
			jPanelNumDataPoints.add(jLabelNumDataPoints, BorderLayout.WEST);
			jPanelNumDataPoints.add(jSpinnerNumDataPoints, BorderLayout.CENTER);
			jLabelNumDataPoints.setEnabled(true);
			jSpinnerNumDataPoints.setEnabled(true);
		}
		return jPanelNumDataPoints;
	}

	/**
	 * This method initializes jJPanelNumDataPoints
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelStdDev() {
		if (jPanelStdDev == null) {
			jPanelStdDev = new JPanel();
			jPanelStdDev.setBorder(new EmptyBorder(0, 5, 0, 5));
			jPanelStdDev.setLayout(new BorderLayout());
			jLabelStdDev = new JLabel("Standard Deviation: ");
			jLabelStdDev.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 1;
			SpinnerModel sModel = new SpinnerNumberModel(init, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerStdDev = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerStdDev.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerStdDev.setValue(1); // only in order to set format pattern
			jSpinnerStdDev.setValue(init); // only in order to set format pattern
			jSpinnerStdDev.addChangeListener(this);
			jPanelStdDev.add(jLabelStdDev, BorderLayout.WEST);
			jPanelStdDev.add(jSpinnerStdDev, BorderLayout.CENTER);
			jLabelStdDev.setEnabled(false);
			jSpinnerStdDev.setEnabled(false);
		}
		return jPanelStdDev;
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxfGnTofBm
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxfGnTofBm() {
		if (jCheckBoxfGnTofBm == null) {
			jCheckBoxfGnTofBm = new JCheckBox();
			jCheckBoxfGnTofBm.setText("fGn to fBm conversion");
			jCheckBoxfGnTofBm.setToolTipText("converts a fGn signal into a fBm signal using summation");
			jCheckBoxfGnTofBm.addActionListener(this);
			jCheckBoxfGnTofBm.setActionCommand("parameter");
			jCheckBoxfGnTofBm.setEnabled(true);
			jCheckBoxfGnTofBm.setSelected(false);
		}
		return jCheckBoxfGnTofBm;
	}

	/**
	 * This method initializes jCheckBoxfBmTofGn
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxfBmTofGn() {
		if (jCheckBoxfBmTofGn == null) {
			jCheckBoxfBmTofGn = new JCheckBox();
			jCheckBoxfBmTofGn.setText("fBm to fGn conversion");
			jCheckBoxfBmTofGn.setToolTipText("converts a fBm signal into a fGn signal using differencing");
			jCheckBoxfBmTofGn.addActionListener(this);
			jCheckBoxfBmTofGn.setActionCommand("parameter");
			jCheckBoxfBmTofGn.setEnabled(false);
			jCheckBoxfBmTofGn.setSelected(false);
		}
		return jCheckBoxfBmTofGn;
	}
	
	private JPanel getJPanelParameters() {
		if (jPanelParameters == null) {
			jPanelParameters = new JPanel();
			jPanelParameters.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			GridBagLayout gbl_generatorOptionsPanel = new GridBagLayout();
			gbl_generatorOptionsPanel.columnWidths = new int[]{0, 0};
			gbl_generatorOptionsPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
			gbl_generatorOptionsPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_generatorOptionsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			jPanelParameters.setLayout(gbl_generatorOptionsPanel);
			
			GridBagConstraints gbc_jPanelHurst = new GridBagConstraints();
			gbc_jPanelHurst.anchor = GridBagConstraints.EAST;
			gbc_jPanelHurst.insets = new Insets(5, 0, 0, 0);
			gbc_jPanelHurst.gridx = 0;
			gbc_jPanelHurst.gridy = 0;
			jPanelParameters.add(getJPanelHurst(), gbc_jPanelHurst);
			
			GridBagConstraints gbc_jPanelNumDataPoints = new GridBagConstraints();
			gbc_jPanelNumDataPoints.anchor = GridBagConstraints.EAST;
			gbc_jPanelNumDataPoints.insets = new Insets(5, 0, 0, 0);
			gbc_jPanelNumDataPoints.gridx = 0;
			gbc_jPanelNumDataPoints.gridy = 1;
			jPanelParameters.add(getJPanelNumDataPoints(), gbc_jPanelNumDataPoints);
			
			GridBagConstraints gbc_jPanelStdDev = new GridBagConstraints();
			gbc_jPanelStdDev.anchor = GridBagConstraints.EAST;
			gbc_jPanelStdDev.insets = new Insets(5, 0, 0, 0);
			gbc_jPanelStdDev.gridx = 0;
			gbc_jPanelStdDev.gridy = 2;
			jPanelParameters.add(getJPanelStdDev(), gbc_jPanelStdDev);
			GridBagConstraints gbc_jCheckBoxfGnTofBm = new GridBagConstraints();
			
			gbc_jCheckBoxfGnTofBm.insets = new Insets(5, 0, 0, 0);
			gbc_jCheckBoxfGnTofBm.gridx = 0;
			gbc_jCheckBoxfGnTofBm.gridy = 3;
			jPanelParameters.add(getJCheckBoxfGnTofBm(), gbc_jCheckBoxfGnTofBm);
			
			GridBagConstraints gbc_jCheckBoxfBmTofGn = new GridBagConstraints();
			gbc_jCheckBoxfBmTofGn.insets = new Insets(5, 0, 0, 0);
			gbc_jCheckBoxfBmTofGn.gridx = 0;
			gbc_jCheckBoxfBmTofGn.gridy = 4;
			jPanelParameters.add(getJCheckBoxfBmTofGn(), gbc_jCheckBoxfBmTofGn);
		}
		return jPanelParameters;
	}

	@Override
	public void update() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {

			if (e.getSource() == buttDHM) {
				jCheckBoxfGnTofBm.setEnabled(true);
				jCheckBoxfBmTofGn.setEnabled(false);
				jCheckBoxfBmTofGn.setSelected(false);

			}
			if (e.getSource() == buttSSM) {
				jCheckBoxfGnTofBm.setEnabled(false);
				jCheckBoxfGnTofBm.setSelected(false);
				jCheckBoxfBmTofGn.setEnabled(true);
			}

		}
		if ("hurst".equals(e.getActionCommand())) {

			if (e.getSource() == jFormattedTextFieldHurst) {
				double hurst = ((Number) jFormattedTextFieldHurst.getValue())
						.doubleValue();
				if (hurst > 1) {
					BoardPanel.appendTextln("PlotGUI_FractalGenerator: Hurst coefficient must be in the range 0 <H < 1");
					jFormattedTextFieldHurst.setValue(1.0d);
				}
				if (hurst < 0) {
					BoardPanel.appendTextln("PlotGUI_FractalGenerator: Hurst coefficient must be in the range 0 <H < 1");
					jFormattedTextFieldHurst.setValue(0.0d);
				}
			}
			if (jCheckBoxfGnTofBm == e.getSource()) {
			}
			if (jCheckBoxfBmTofGn == e.getSource()) {
			}

		}
		
		this.updateParameterBlock();

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateParameterBlock();
	}




}
