package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_SignalGenerator.java
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
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpSignalGeneratorDescriptor;

/**
 * @author Ahammer
 * @since  2012 11
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 * @update 2017 10 added square, triangle and sawtooth
 */
public class PlotGUI_SignalGenerator extends AbstractPlotOperatorGUI implements
		ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8557561248409563634L;

	private static final Logger logger = LogManager.getLogger(PlotGUI_SignalGenerator.class);

	private ParameterBlockIQM pb;

	private JPanel       jPanelSignal    = null;
	private ButtonGroup  buttGroupSignal = null;
	private JRadioButton buttSine        = null;
	private JRadioButton buttSquare      = null;
	private JRadioButton buttTria        = null;
	private JRadioButton buttSaw        = null;
	private JRadioButton buttConst       = null;
	private JRadioButton buttGaussian    = null;
	private JRadioButton buttUniform     = null;

	private JPanel   jPanelNumSignals   = null;
	private JLabel   jLabelNumSignals   = null;
	private JSpinner jSpinnerNumSignals = null;

	private JPanel   jPanelFrequency   = null;
	private JLabel   jLabelFrequency   = null;
	private JSpinner jSpinnerFrequency = null;

	private JPanel   jPanelNumPeriods   = null;
	private JLabel   jLabelNumPeriods   = null;
	private JSpinner jSpinnerNumPeriods = null;

	private JPanel   jPanelSampleRate   = null;
	private JLabel   jLabelSampleRate   = null;
	private JSpinner jSpinnerSampleRate = null;

	private JPanel   jPanelAmplitude   = null;
	private JLabel   jLabelAmplitude   = null;
	private JSpinner jSpinnerAmplitude = null;

	private JPanel   jPanelNumDataPoints   = null;
	private JLabel   jLabelNumDataPoints   = null;
	private JSpinner jSpinnerNumDataPoints = null;

	private JPanel   jPanelMean   = null;
	private JLabel   jLabelMean   = null;
	private JSpinner jSpinnerMean = null;

	private JPanel   jPanelStdDev   = null;
	private JLabel   jLabelStdDev   = null;
	private JSpinner jSpinnerStdDev = null;
	private JPanel   jPanelParameters;

	public PlotGUI_SignalGenerator() {
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpSignalGeneratorDescriptor().getName());
		this.initialize();
		this.setTitle("Signal Generator");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		getOpGUIContent().add(getJPanelSignal(),          getGridBagConstraints_Signal());
		getOpGUIContent().add(getJPanelNumSignals(),      getGridBagConstraints_NumSignals());
		getOpGUIContent().add(getPanelParameters(), getGridBagConstraints_Parameters());

		this.pack();
	}

	private GridBagConstraints getGridBagConstraints_Signal() {
		GridBagConstraints gbc_Signal = new GridBagConstraints();
		gbc_Signal.insets = new Insets(10, 0, 0, 0);
		gbc_Signal.gridx = 0;
		gbc_Signal.gridy = 0;
		gbc_Signal.fill = GridBagConstraints.BOTH;
		return gbc_Signal;
	}
	
	private GridBagConstraints getGridBagConstraints_NumSignals() {
		GridBagConstraints gbc_jPanelNumSignals = new GridBagConstraints();
		gbc_jPanelNumSignals.gridx = 0;
		gbc_jPanelNumSignals.gridy = 1;
		gbc_jPanelNumSignals.fill = GridBagConstraints.BOTH;
	    return gbc_jPanelNumSignals;
}
	
	private GridBagConstraints getGridBagConstraints_Parameters() {
		GridBagConstraints gbc_Parameters = new GridBagConstraints();
		gbc_Parameters.fill = GridBagConstraints.BOTH;
		gbc_Parameters.gridx = 0;
		gbc_Parameters.gridy = 2;
		gbc_Parameters.fill = GridBagConstraints.BOTH;
		return gbc_Parameters;
	}
	
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		switch (pb.getIntParameter("method")) {
		case 0:
			buttSine.setSelected(true);
			break;
		case 1:
			buttConst.setSelected(true);
			break;
		case 2:
			buttGaussian.setSelected(true);
			break;
		case 3:
			buttUniform.setSelected(true);
			break;
		case 4:
			buttSquare.setSelected(true);
			break;
		case 5:
			buttTria.setSelected(true);
			break;
		case 6:
			buttSaw.setSelected(true);
			break;
			
		}

		this.removeListeners();
		jSpinnerFrequency.setValue(pb.getIntParameter("frequency"));
		jSpinnerNumPeriods.setValue(pb.getIntParameter("nPeriods"));
		jSpinnerSampleRate.setValue(pb.getIntParameter("sampleRate"));
		jSpinnerAmplitude.setValue(pb.getIntParameter("amplitude"));
		jSpinnerNumDataPoints.setValue(pb.getIntParameter("nDataPoints"));
		jSpinnerMean.setValue(pb.getIntParameter("mean"));
		jSpinnerStdDev.setValue(pb.getIntParameter("stdDev"));
		this.addListeners();
	}

	@Override
	public void updateParameterBlock() {
		if (this.buttSine.isSelected())     pb.setParameter("method", 0);
		if (this.buttConst.isSelected())    pb.setParameter("method", 1);
		if (this.buttGaussian.isSelected()) pb.setParameter("method", 2);
		if (this.buttUniform.isSelected())  pb.setParameter("method", 3);
		if (this.buttSquare.isSelected())   pb.setParameter("method", 4);
		if (this.buttTria.isSelected())     pb.setParameter("method", 5);
		if (this.buttSaw.isSelected())      pb.setParameter("method", 6);

		pb.setParameter("frequency",   ((Number) jSpinnerFrequency.getValue()).intValue());
		pb.setParameter("nPeriods",    ((Number) jSpinnerNumPeriods.getValue()).intValue());
		pb.setParameter("sampleRate",  ((Number) jSpinnerSampleRate.getValue()).intValue());
		pb.setParameter("amplitude",   ((Number) jSpinnerAmplitude.getValue()).intValue());
		pb.setParameter("nDataPoints", ((Number) jSpinnerNumDataPoints.getValue()).intValue());
		pb.setParameter("mean",        ((Number) jSpinnerMean.getValue()).intValue());
		pb.setParameter("stdDev",      ((Number) jSpinnerStdDev.getValue()).intValue());
		
		this.workPackage.setIterations(((Number) jSpinnerNumSignals.getValue()).intValue());
	}
	
	private void removeListeners() {
		jSpinnerAmplitude.removeChangeListener(this);
		jSpinnerFrequency.removeChangeListener(this);
		jSpinnerMean.removeChangeListener(this);
		jSpinnerNumDataPoints.removeChangeListener(this);
		jSpinnerNumPeriods.removeChangeListener(this);
		jSpinnerNumSignals.removeChangeListener(this);
		jSpinnerSampleRate.removeChangeListener(this);
		jSpinnerStdDev.removeChangeListener(this);
	}

	private void addListeners() {
		jSpinnerAmplitude.addChangeListener(this);
		jSpinnerFrequency.addChangeListener(this);
		jSpinnerMean.addChangeListener(this);
		jSpinnerNumDataPoints.addChangeListener(this);
		jSpinnerNumPeriods.addChangeListener(this);
		jSpinnerNumSignals.addChangeListener(this);
		jSpinnerSampleRate.addChangeListener(this);
		jSpinnerStdDev.addChangeListener(this);
	}

	/**
	 * This method initializes the Option: Sine
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSine() {
		if (buttSine == null) {
			buttSine = new JRadioButton();
			buttSine.setText("Sine");
			buttSine.setToolTipText("generates a sinusoidal signal");
			buttSine.addActionListener(this);
			buttSine.setActionCommand("parameter");
			buttSine.setSelected(true);
		}
		return buttSine;
	}
	/**
	 * This method initializes the Option: Square
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSquare() {
		if (buttSquare == null) {
			buttSquare = new JRadioButton();
			buttSquare.setText("Square");
			buttSquare.setToolTipText("generates a square signal");
			buttSquare.addActionListener(this);
			buttSquare.setActionCommand("parameter");
			buttSquare.setSelected(true);
		}
		return buttSquare;
	}
	/**
	 * This method initializes the Option: Tria
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonTria() {
		if (buttTria == null) {
			buttTria = new JRadioButton();
			buttTria.setText("Triangle");
			buttTria.setToolTipText("generates a triangle signal");
			buttTria.addActionListener(this);
			buttTria.setActionCommand("parameter");
			buttTria.setSelected(true);
		}
		return buttTria;
	}
	/**
	 * This method initializes the Option: Saw
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSaw() {
		if (buttSaw == null) {
			buttSaw = new JRadioButton();
			buttSaw.setText("Sawtooth");
			buttSaw.setToolTipText("generates a sawtooth signal");
			buttSaw.addActionListener(this);
			buttSaw.setActionCommand("parameter");
			buttSaw.setSelected(true);
		}
		return buttSaw;
	}

	/**
	 * This method initializes the Option: Const
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonConst() {
		if (buttConst == null) {
			buttConst = new JRadioButton();
			buttConst.setText("Constant");
			buttConst.setToolTipText("generates a constant signal");
			buttConst.addActionListener(this);
			buttConst.setActionCommand("parameter");
			buttConst.setEnabled(true);
			buttConst.setVisible(true);
		}
		return buttConst;
	}

	/**
	 * This method initializes the Option: Gaussian
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonGaussian() {
		if (buttGaussian == null) {
			buttGaussian = new JRadioButton();
			buttGaussian.setText("Gaussian");
			buttGaussian.setToolTipText("generates a Gaussian distributed signal");
			buttGaussian.addActionListener(this);
			buttGaussian.setActionCommand("parameter");
			buttGaussian.setEnabled(true);
			buttGaussian.setVisible(true);
		}
		return buttGaussian;
	}

	/**
	 * This method initializes the Option: Uniform
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonUniform() {
		if (buttUniform == null) {
			buttUniform = new JRadioButton();
			buttUniform.setText("Uniform");
			buttUniform.setToolTipText("generates a uniform random signal");
			buttUniform.addActionListener(this);
			buttUniform.setActionCommand("parameter");
			buttUniform.setEnabled(true);
			buttUniform.setVisible(true);
		}
		return buttUniform;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSignal() {
		// if (jPanelSignal == null) {
		jPanelSignal = new JPanel();
		jPanelSignal.setLayout(new BoxLayout(jPanelSignal, BoxLayout.Y_AXIS));
		//jPanelSignal.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelSignal.setBorder(new TitledBorder(null, "Signal type", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelSignal.add(getJRadioButtonSine());
		jPanelSignal.add(getJRadioButtonSquare());
		jPanelSignal.add(getJRadioButtonTria());
		jPanelSignal.add(getJRadioButtonSaw());
		jPanelSignal.add(getJRadioButtonConst());
		jPanelSignal.add(getJRadioButtonGaussian());
		jPanelSignal.add(getJRadioButtonUniform());
		
		this.setButtonGroupSignal(); // Grouping of JRadioButtons
		// }
		return jPanelSignal;
	}

	private void setButtonGroupSignal() {
		// if (ButtonGroup buttGroupSignal == null) {
		buttGroupSignal = new ButtonGroup();
		buttGroupSignal.add(buttSine);
		buttGroupSignal.add(buttSquare);
		buttGroupSignal.add(buttTria);
		buttGroupSignal.add(buttSaw);
		buttGroupSignal.add(buttConst);
		buttGroupSignal.add(buttGaussian);
		buttGroupSignal.add(buttUniform);
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
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumSignals .getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf .getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#");   // decimalFormat.applyPattern("#,##0.0")  ;
			jSpinnerNumSignals.setValue(1);    // only in order to set format  pattern
			jSpinnerNumSignals.setValue(init); // only in order to set format / pattern
			jSpinnerNumSignals.addChangeListener(this);
			jPanelNumSignals.add(jLabelNumSignals);
			jPanelNumSignals.add(jSpinnerNumSignals);
		}
		return jPanelNumSignals;
	}

	/**
	 * This method initializes jJPanelFrequency
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelFrequency() {
		if (jPanelFrequency == null) {
			jPanelFrequency = new JPanel();
			//jPanelFrequency.setBorder(new EmptyBorder(5, 5, 5, 5));
			jPanelFrequency.setLayout(new BorderLayout());
			jLabelFrequency = new JLabel("Frequency [Hz]: ");
			jLabelFrequency.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 1;
			SpinnerModel sModel = new SpinnerNumberModel(init, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerFrequency = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerFrequency .getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf .getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")  ;
			jSpinnerFrequency.setValue(1); // only in order to set format / pattern
			jSpinnerFrequency.setValue(init); // only in order to set format  pattern
			jSpinnerFrequency.addChangeListener(this);
			jPanelFrequency.add(jLabelFrequency, BorderLayout.WEST);
			jPanelFrequency.add(jSpinnerFrequency, BorderLayout.CENTER);
		}
		return jPanelFrequency;
	}

	/**
	 * This method initializes jJPanelNumPeriods
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumPeriods() {
		if (jPanelNumPeriods == null) {
			jPanelNumPeriods = new JPanel();
			//jPanelNumPeriods.setBorder(new EmptyBorder(5, 5, 5, 5));
			jPanelNumPeriods.setLayout(new BorderLayout());
			jLabelNumPeriods = new JLabel("Number of Periods: ");
			jLabelNumPeriods.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 1;
			SpinnerModel sModel = new SpinnerNumberModel(init, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumPeriods = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumPeriods .getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf .getFormatter();
			ftf.setColumns(5);
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")  ;
			jSpinnerNumPeriods.setValue(1); // only in order to set format  pattern
			jSpinnerNumPeriods.setValue(init); // only in order to set format  pattern
			jSpinnerNumPeriods.addChangeListener(this);
			jPanelNumPeriods.add(jLabelNumPeriods, BorderLayout.WEST);
			jPanelNumPeriods.add(jSpinnerNumPeriods, BorderLayout.CENTER);
		}
		return jPanelNumPeriods;
	}

	/**
	 * This method initializes jJPanelSampleRate
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSampleRate() {
		if (jPanelSampleRate == null) {
			jPanelSampleRate = new JPanel();
			//jPanelSampleRate.setBorder(new EmptyBorder(5, 5, 5, 5));
			jPanelSampleRate.setLayout(new BorderLayout());
			jLabelSampleRate = new JLabel("Sample Rate [Hz]: ");
			jLabelSampleRate.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 1000;
			SpinnerModel sModel = new SpinnerNumberModel(init, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerSampleRate = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerSampleRate .getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			ftf.setColumns(5);
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")  ;
			jSpinnerSampleRate.setValue(1); // only in order to set format  pattern
			jSpinnerSampleRate.setValue(init); // only in order to set format  pattern
			jSpinnerSampleRate.addChangeListener(this);
			jPanelSampleRate.add(jLabelSampleRate, BorderLayout.WEST);
			jPanelSampleRate.add(jSpinnerSampleRate, BorderLayout.CENTER);
		}
		return jPanelSampleRate;
	}

	/**
	 * This method initializes jJPanelAmplitude
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelAmplitude() {
		if (jPanelAmplitude == null) {
			jPanelAmplitude = new JPanel();
			//jPanelAmplitude.setBorder(new EmptyBorder(5, 5, 5, 5));
			jPanelAmplitude.setLayout(new BorderLayout());
			jLabelAmplitude = new JLabel("Amplitude [a.u.]: ");
			jLabelAmplitude.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 1;
			SpinnerModel sModel = new SpinnerNumberModel(init, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerAmplitude = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerAmplitude .getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerAmplitude.setValue(1); // only in order to set format pattern
			jSpinnerAmplitude.setValue(init); // only in order to set format pattern
			jSpinnerAmplitude.addChangeListener(this);
			jPanelAmplitude.add(jLabelAmplitude, BorderLayout.WEST);
			jPanelAmplitude.add(jSpinnerAmplitude, BorderLayout.CENTER);
		}
		return jPanelAmplitude;
	}

	/**
	 * This method initializes jJPanelNumDataPoints
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumDataPoints() {
		if (jPanelNumDataPoints == null) {
			jPanelNumDataPoints = new JPanel();
			//jPanelNumDataPoints.setBorder(new EmptyBorder(5, 5, 5, 5));
			jPanelNumDataPoints.setLayout(new BorderLayout());
			jLabelNumDataPoints = new JLabel("Number data points: ");
			jLabelNumDataPoints.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 1000;
			SpinnerModel sModel = new SpinnerNumberModel(init, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumDataPoints = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumDataPoints.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerNumDataPoints.setValue(1); // only in order to set format/ pattern
			jSpinnerNumDataPoints.setValue(init); // only in order to set format/ pattern
			jSpinnerNumDataPoints.addChangeListener(this);
			jPanelNumDataPoints.add(jLabelNumDataPoints, BorderLayout.WEST);
			jPanelNumDataPoints.add(jSpinnerNumDataPoints, BorderLayout.CENTER);
			jLabelNumDataPoints.setEnabled(false);
			jSpinnerNumDataPoints.setEnabled(false);
		}
		return jPanelNumDataPoints;
	}

	/**
	 * This method initializes jJPanelMean
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMean() {
		if (jPanelMean == null) {
			jPanelMean = new JPanel();
			//jPanelMean.setBorder(new EmptyBorder(5, 5, 5, 5));
			jPanelMean.setLayout(new BorderLayout());
			jLabelMean = new JLabel("Mean: ");
			jLabelMean.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 0;
			SpinnerModel sModel = new SpinnerNumberModel(init, Integer.MIN_VALUE, Integer.MAX_VALUE, 1); // init, min, max,  step
			jSpinnerMean = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMean.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerMean.setValue(1); // only in order to set format pattern
			jSpinnerMean.setValue(init); // only in order to set format pattern
			jSpinnerMean.addChangeListener(this);
			jPanelMean.add(jLabelMean, BorderLayout.WEST);
			jPanelMean.add(jSpinnerMean, BorderLayout.CENTER);
			jLabelMean.setEnabled(false);
			jSpinnerMean.setEnabled(false);
		}
		return jPanelMean;
	}

	/**
	 * This method initializes jJPanelNumDataPoints
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelStdDev() {
		if (jPanelStdDev == null) {
			jPanelStdDev = new JPanel();
			//jPanelStdDev.setBorder(new EmptyBorder(5, 5, 5, 5));
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
	
	private JPanel getPanelParameters() {
		if (jPanelParameters == null) {
			jPanelParameters = new JPanel();
			jPanelParameters.setBorder(new TitledBorder(UIManager .getBorder("TitledBorder.border"), "Parameters", 
					                       TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			GridBagLayout gbl_panelParameters = new GridBagLayout();
			gbl_panelParameters.columnWidths = new int[] { 0, 0 };
			gbl_panelParameters.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gbl_panelParameters.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
			gbl_panelParameters.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			jPanelParameters.setLayout(gbl_panelParameters);
			
			GridBagConstraints gbc_jPanelFrequency = new GridBagConstraints();
			gbc_jPanelFrequency.anchor = GridBagConstraints.EAST;
			gbc_jPanelFrequency.insets = new Insets(0, 5, 5, 5);
			gbc_jPanelFrequency.gridx = 0;
			gbc_jPanelFrequency.gridy = 0;
			jPanelParameters .add(getJPanelFrequency(), gbc_jPanelFrequency);
			
			GridBagConstraints gbc_jPanelNumPeriods = new GridBagConstraints();
			gbc_jPanelNumPeriods.anchor = GridBagConstraints.EAST;
			gbc_jPanelNumPeriods.insets = new Insets(0, 5, 5, 5);
			gbc_jPanelNumPeriods.gridx = 0;
			gbc_jPanelNumPeriods.gridy = 1;
			jPanelParameters.add(getJPanelNumPeriods(), gbc_jPanelNumPeriods);
			
			GridBagConstraints gbc_jPanelSampleRate = new GridBagConstraints();
			gbc_jPanelSampleRate.anchor = GridBagConstraints.EAST;
			gbc_jPanelSampleRate.insets = new Insets(0, 5, 5, 5);
			gbc_jPanelSampleRate.gridx = 0;
			gbc_jPanelSampleRate.gridy = 2;
			jPanelParameters.add(getJPanelSampleRate(), gbc_jPanelSampleRate);
			
			GridBagConstraints gbc_jPanelAmplitude = new GridBagConstraints();
			gbc_jPanelAmplitude.anchor = GridBagConstraints.EAST;
			gbc_jPanelAmplitude.insets = new Insets(0, 5, 5, 5);
			gbc_jPanelAmplitude.gridx = 0;
			gbc_jPanelAmplitude.gridy = 3;
			jPanelParameters .add(getJPanelAmplitude(), gbc_jPanelAmplitude);
			
			GridBagConstraints gbc_jPanelNumDataPoints = new GridBagConstraints();
			gbc_jPanelNumDataPoints.anchor = GridBagConstraints.EAST;
			gbc_jPanelNumDataPoints.insets = new Insets(0, 5, 5, 5);
			gbc_jPanelNumDataPoints.gridx = 0;
			gbc_jPanelNumDataPoints.gridy = 4;
			jPanelParameters.add(getJPanelNumDataPoints(), gbc_jPanelNumDataPoints);
			
			GridBagConstraints gbc_jPanelMean = new GridBagConstraints();
			gbc_jPanelMean.anchor = GridBagConstraints.EAST;
			gbc_jPanelMean.insets = new Insets(0, 5, 5, 5);
			gbc_jPanelMean.gridx = 0;
			gbc_jPanelMean.gridy = 5;
			jPanelParameters.add(getJPanelMean(), gbc_jPanelMean);
			
			GridBagConstraints gbc_jPanelStdDev = new GridBagConstraints();
			gbc_jPanelStdDev.anchor = GridBagConstraints.EAST;
			gbc_jPanelStdDev.insets = new Insets(0, 5, 5, 5);
			gbc_jPanelStdDev.gridx = 0;
			gbc_jPanelStdDev.gridy = 6;
			jPanelParameters.add(getJPanelStdDev(), gbc_jPanelStdDev);
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

			if (e.getSource() == buttSine) {
				jSpinnerFrequency.setEnabled(true);
				jSpinnerNumPeriods.setEnabled(true);
				jSpinnerSampleRate.setEnabled(true);
				jSpinnerAmplitude.setEnabled(true);
				jSpinnerNumDataPoints.setEnabled(false);
				jSpinnerMean.setEnabled(false);
				jSpinnerStdDev.setEnabled(false);

				jLabelFrequency.setEnabled(true);
				jLabelNumPeriods.setEnabled(true);
				jLabelSampleRate.setEnabled(true);
				jLabelAmplitude.setEnabled(true);
				jLabelNumDataPoints.setEnabled(false);
				jLabelMean.setEnabled(false);
				jLabelStdDev.setEnabled(false);
			}
			if (e.getSource() == buttSquare) {
				jSpinnerFrequency.setEnabled(true);
				jSpinnerNumPeriods.setEnabled(true);
				jSpinnerSampleRate.setEnabled(true);
				jSpinnerAmplitude.setEnabled(true);
				jSpinnerNumDataPoints.setEnabled(false);
				jSpinnerMean.setEnabled(false);
				jSpinnerStdDev.setEnabled(false);

				jLabelFrequency.setEnabled(true);
				jLabelNumPeriods.setEnabled(true);
				jLabelSampleRate.setEnabled(true);
				jLabelAmplitude.setEnabled(true);
				jLabelNumDataPoints.setEnabled(false);
				jLabelMean.setEnabled(false);
				jLabelStdDev.setEnabled(false);
			}
			if (e.getSource() == buttTria) {
				jSpinnerFrequency.setEnabled(true);
				jSpinnerNumPeriods.setEnabled(true);
				jSpinnerSampleRate.setEnabled(true);
				jSpinnerAmplitude.setEnabled(true);
				jSpinnerNumDataPoints.setEnabled(false);
				jSpinnerMean.setEnabled(false);
				jSpinnerStdDev.setEnabled(false);

				jLabelFrequency.setEnabled(true);
				jLabelNumPeriods.setEnabled(true);
				jLabelSampleRate.setEnabled(true);
				jLabelAmplitude.setEnabled(true);
				jLabelNumDataPoints.setEnabled(false);
				jLabelMean.setEnabled(false);
				jLabelStdDev.setEnabled(false);
			}
			if (e.getSource() == buttSaw) {
				jSpinnerFrequency.setEnabled(true);
				jSpinnerNumPeriods.setEnabled(true);
				jSpinnerSampleRate.setEnabled(true);
				jSpinnerAmplitude.setEnabled(true);
				jSpinnerNumDataPoints.setEnabled(false);
				jSpinnerMean.setEnabled(false);
				jSpinnerStdDev.setEnabled(false);

				jLabelFrequency.setEnabled(true);
				jLabelNumPeriods.setEnabled(true);
				jLabelSampleRate.setEnabled(true);
				jLabelAmplitude.setEnabled(true);
				jLabelNumDataPoints.setEnabled(false);
				jLabelMean.setEnabled(false);
				jLabelStdDev.setEnabled(false);
			}
			
			if (e.getSource() == buttConst) {
				jSpinnerFrequency.setEnabled(false);
				jSpinnerNumPeriods.setEnabled(false);
				jSpinnerSampleRate.setEnabled(false);
				jSpinnerAmplitude.setEnabled(true);
				jSpinnerNumDataPoints.setEnabled(true);
				jSpinnerMean.setEnabled(false);
				jSpinnerStdDev.setEnabled(false);

				jLabelFrequency.setEnabled(false);
				jLabelNumPeriods.setEnabled(false);
				jLabelSampleRate.setEnabled(false);
				jLabelAmplitude.setEnabled(true);
				jLabelNumDataPoints.setEnabled(true);
				jLabelMean.setEnabled(false);
				jLabelStdDev.setEnabled(false);
			}
			if (e.getSource() == buttGaussian) {
				jSpinnerFrequency.setEnabled(false);
				jSpinnerNumPeriods.setEnabled(false);
				jSpinnerSampleRate.setEnabled(false);
				jSpinnerAmplitude.setEnabled(false);
				jSpinnerNumDataPoints.setEnabled(true);
				jSpinnerMean.setEnabled(true);
				jSpinnerStdDev.setEnabled(true);

				jLabelFrequency.setEnabled(false);
				jLabelNumPeriods.setEnabled(false);
				jLabelSampleRate.setEnabled(false);
				jLabelAmplitude.setEnabled(false);
				jLabelNumDataPoints.setEnabled(true);
				jLabelMean.setEnabled(true);
				jLabelStdDev.setEnabled(true);
			}
			if (e.getSource() == buttUniform) {
				jSpinnerFrequency.setEnabled(false);
				jSpinnerNumPeriods.setEnabled(false);
				jSpinnerSampleRate.setEnabled(false);
				jSpinnerAmplitude.setEnabled(true);
				jSpinnerNumDataPoints.setEnabled(true);
				jSpinnerMean.setEnabled(false);
				jSpinnerStdDev.setEnabled(false);

				jLabelFrequency.setEnabled(false);
				jLabelNumPeriods.setEnabled(false);
				jLabelSampleRate.setEnabled(false);
				jLabelAmplitude.setEnabled(true);
				jLabelNumDataPoints.setEnabled(true);
				jLabelMean.setEnabled(false);
				jLabelStdDev.setEnabled(false);
			}
			this.updateParameterBlock();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateParameterBlock();
	}
	
}
