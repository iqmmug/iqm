package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_FFT.java
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFFTDescriptor;

/**
 * @author Ahammer
 * @since  2012 12
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class PlotGUI_FFT extends AbstractPlotOperatorGUI implements ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8050400556849306323L;

	private static final Logger logger = Logger.getLogger(PlotGUI_FFT.class);

	private ParameterBlockIQM pb;

	private JPanel       jPanelWindowing    = null;
	private ButtonGroup  buttGroupWindowing = null;
	private JRadioButton buttWithout        = null;
	private JRadioButton buttRectangular    = null;
	private JRadioButton buttBartlett       = null;
	private JRadioButton buttWelch          = null;
	private JRadioButton buttHann           = null;
	private JRadioButton buttHamming        = null;
	private JRadioButton buttKaiser         = null;
	private JRadioButton buttGaussian       = null;

	private JPanel   jPanelSampleRate   = null;
	private JLabel   jLabelSampleRate   = null;
	private JSpinner jSpinnerSampleRate = null;

	private JRadioButton buttResultLog    = null;
	private JRadioButton buttResultLin    = null;
	private JPanel 		 jPanelResult 	  = null;
	private ButtonGroup  buttGroupResult  = null;

	public PlotGUI_FFT() {
		getOpGUIContent().setBorder(new EmptyBorder(10, 0, 0, 0));
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpFFTDescriptor().getName());
		this.initialize();
		this.setTitle("FFT of Plots");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelWindowing(),   getGridBagConstraints_Windowing());
		this.getOpGUIContent().add(getJPanelSampleRate(),  getGridBagConstraints_SampleRate());
		this.getOpGUIContent().add(getJPanelResult(),      getGridBagConstraints_Result());
		this.pack();
	}

	private GridBagConstraints getGridBagConstraints_Windowing() {
		GridBagConstraints gbc_Windowing = new GridBagConstraints();
		gbc_Windowing.gridx = 0;
		gbc_Windowing.gridy = 0;
		gbc_Windowing.insets = new Insets(10, 0, 0, 0); // top left bottom right
		gbc_Windowing.fill = GridBagConstraints.BOTH;
		return gbc_Windowing;
	}

	private GridBagConstraints getGridBagConstraints_SampleRate() {
		GridBagConstraints gbc_SampleRate = new GridBagConstraints();
		gbc_SampleRate.gridx = 0;
		gbc_SampleRate.gridy = 1;
		gbc_SampleRate.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_SampleRate.fill = GridBagConstraints.BOTH;
		return gbc_SampleRate;
	}

	private GridBagConstraints getGridBagConstraints_Result() {
		GridBagConstraints gbc_Result = new GridBagConstraints();
		gbc_Result.gridx = 0;
		gbc_Result.gridy = 2;
		gbc_Result.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gbc_Result.fill = GridBagConstraints.BOTH;
		return gbc_Result;
	}


	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();	
		int windowing    = pb.getIntParameter("windowing");
		int resultLogLin = pb.getIntParameter("resultLogLin");

		if (windowing == PlotOpFFTDescriptor.WINDOWING_WITHOUT)     buttWithout.setSelected(true);
		if (windowing == PlotOpFFTDescriptor.WINDOWING_RECTANGULAR) buttRectangular.setSelected(true);
		if (windowing == PlotOpFFTDescriptor.WINDOWING_BARTLETT)    buttBartlett.setSelected(true);
		if (windowing == PlotOpFFTDescriptor.WINDOWING_WELCH)       buttWelch.setSelected(true);	
		if (windowing == PlotOpFFTDescriptor.WINDOWING_HANN)        buttHann.setSelected(true);	
		if (windowing == PlotOpFFTDescriptor.WINDOWING_HAMMING)     buttHamming.setSelected(true);
		if (windowing == PlotOpFFTDescriptor.WINDOWING_KAISER)      buttKaiser.setSelected(true);	
		if (windowing == PlotOpFFTDescriptor.WINDOWING_GAUSSIAN)    buttGaussian.setSelected(true);
		
		jSpinnerSampleRate.removeChangeListener(this);
		jSpinnerSampleRate.setValue(pb.getIntParameter("sampleRate"));
		jSpinnerSampleRate.addChangeListener(this);
		
		if (resultLogLin == PlotOpFFTDescriptor.RESULT_LIN)  buttResultLin.setSelected(true);
		if (resultLogLin == PlotOpFFTDescriptor.RESULT_LOG)  buttResultLog.setSelected(true);
	}

	@Override
	public void updateParameterBlock() {
		if (this.buttWithout.isSelected())      pb.setParameter("windowing", PlotOpFFTDescriptor.WINDOWING_WITHOUT);
		if (this.buttRectangular.isSelected())  pb.setParameter("windowing", PlotOpFFTDescriptor.WINDOWING_RECTANGULAR);
		if (this.buttBartlett.isSelected())     pb.setParameter("windowing", PlotOpFFTDescriptor.WINDOWING_BARTLETT);
		if (this.buttWelch.isSelected())        pb.setParameter("windowing", PlotOpFFTDescriptor.WINDOWING_WELCH);
		if (this.buttHann.isSelected())         pb.setParameter("windowing", PlotOpFFTDescriptor.WINDOWING_HANN);
		if (this.buttHamming.isSelected())      pb.setParameter("windowing", PlotOpFFTDescriptor.WINDOWING_HAMMING);
		if (this.buttKaiser.isSelected())       pb.setParameter("windowing", PlotOpFFTDescriptor.WINDOWING_KAISER);
		if (this.buttGaussian.isSelected())     pb.setParameter("windowing", PlotOpFFTDescriptor.WINDOWING_GAUSSIAN);
		
		 pb.setParameter("sampleRate", ((Number) jSpinnerSampleRate.getValue()).intValue());
		
		if (this.buttResultLin.isSelected())  pb.setParameter("resultLogLin", PlotOpFFTDescriptor.RESULT_LIN);
		if (this.buttResultLog.isSelected())  pb.setParameter("resultLogLin", PlotOpFFTDescriptor.RESULT_LOG);	
	}

	/**
	 * This method initializes the Option: Without
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonWithout() {
		if (buttWithout == null) {
			buttWithout = new JRadioButton();
			buttWithout.setText("Without");
			buttWithout.setToolTipText("calculates FFT without windowing");
			buttWithout.addActionListener(this);
			buttWithout.setActionCommand("parameter");
		}
		return buttWithout;
	}

	/**
	 * This method initializes the Option: Rectangular
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonRectangular() {
		if (buttRectangular == null) {
			buttRectangular = new JRadioButton();
			buttRectangular.setText("Rectangular");
			buttRectangular.setToolTipText("calculates FFT using rectangular windowing");
			buttRectangular.addActionListener(this);
			buttRectangular.setActionCommand("parameter");
		}
		return buttRectangular;
	}

	/**
	 * This method initializes the Option: Bartlett
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonBartlett() {
		if (buttBartlett == null) {
			buttBartlett = new JRadioButton();
			buttBartlett.setText("Bartlett");
			buttBartlett.setToolTipText("calculates FFT using Bartlett windowing");
			buttBartlett.addActionListener(this);
			buttBartlett.setActionCommand("parameter");
			buttBartlett.setEnabled(true);
			buttBartlett.setSelected(true);
		}
		return buttBartlett;
	}

	/**
	 * This method initializes the Option: Welch
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonWelch() {
		if (buttWelch == null) {
			buttWelch = new JRadioButton();
			buttWelch.setText("Welch");
			buttWelch.setToolTipText("calculates FFT using Welch windowing");
			buttWelch.addActionListener(this);
			buttWelch.setActionCommand("parameter");
			buttWelch.setEnabled(true);
			buttWelch.setVisible(true);
		}
		return buttWelch;
	}

	/**
	 * This method initializes the Option: Hann
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonHann() {
		if (buttHann == null) {
			buttHann = new JRadioButton();
			buttHann.setText("Hann");
			buttHann.setToolTipText("calculates FFT using Hann windowing");
			buttHann.addActionListener(this);
			buttHann.setActionCommand("parameter");
			buttHann.setEnabled(true);
			buttHann.setVisible(true);
		}
		return buttHann;
	}

	/**
	 * This method initializes the Option: Hamming
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonHamming() {
		if (buttHamming == null) {
			buttHamming = new JRadioButton();
			buttHamming.setText("Hamming");
			buttHamming.setToolTipText("calculates FFT using Hamming windowing");
			buttHamming.addActionListener(this);
			buttHamming.setActionCommand("parameter");
			buttHamming.setEnabled(true);
			buttHamming.setVisible(true);
		}
		return buttHamming;
	}

	/**
	 * This method initializes the Option: Kaiser
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonKaiser() {
		if (buttKaiser == null) {
			buttKaiser = new JRadioButton();
			buttKaiser.setText("Kaiser");
			buttKaiser.setToolTipText("calculates FFT using Kaiser windowing");
			buttKaiser.addActionListener(this);
			buttKaiser.setActionCommand("parameter");
			buttKaiser.setEnabled(true);
			buttKaiser.setVisible(true);
		}
		return buttKaiser;
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
			buttGaussian.setToolTipText("calculates FFT using Gaussian windowing");
			buttGaussian.addActionListener(this);
			buttGaussian.setActionCommand("parameter");
			buttGaussian.setEnabled(true);
			buttGaussian.setVisible(true);
		}
		return buttGaussian;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelWindowing() {
		// if (jPanelWindowing == null) {
		jPanelWindowing = new JPanel();
		jPanelWindowing.setLayout(new BoxLayout(jPanelWindowing, BoxLayout.Y_AXIS));
		//jPanelWindowing.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelWindowing.setBorder(new TitledBorder(null, "Windowing options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelWindowing.add(getJRadioButtonWithout());
		jPanelWindowing.add(getJRadioButtonRectangular());
		jPanelWindowing.add(getJRadioButtonBartlett());
		jPanelWindowing.add(getJRadioButtonWelch());
		jPanelWindowing.add(getJRadioButtonHann());
		jPanelWindowing.add(getJRadioButtonHamming());
		jPanelWindowing.add(getJRadioButtonKaiser());
		jPanelWindowing.add(getJRadioButtonGaussian());

		// jPanelWindowing.addSeparator();
		this.setButtonGroupWindowing(); // Grouping of JRadioButtons
		// }
		return jPanelWindowing;
	}

	private void setButtonGroupWindowing() {
		buttGroupWindowing = new ButtonGroup();
		buttGroupWindowing.add(buttWithout);
		buttGroupWindowing.add(buttRectangular);
		buttGroupWindowing.add(buttBartlett);
		buttGroupWindowing.add(buttWelch);
		buttGroupWindowing.add(buttHann);
		buttGroupWindowing.add(buttHamming);
		buttGroupWindowing.add(buttKaiser);
		buttGroupWindowing.add(buttGaussian);
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelSampleRate
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSampleRate() {
		if (jPanelSampleRate == null) {
			jPanelSampleRate = new JPanel();
			//jPanelSampleRate.setLayout(new BoxLayout(jPanelSampleRate, BoxLayout.Y_AXIS));
			jPanelSampleRate.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelSampleRate.setBorder(new TitledBorder(null, "Sample rate", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jLabelSampleRate = new JLabel("[Hz]: ");
			jLabelSampleRate.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 1000;
			SpinnerModel sModel = new SpinnerNumberModel(init, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerSampleRate = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerSampleRate.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#");   // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerSampleRate.setValue(1);    // only in order to set format pattern
			jSpinnerSampleRate.setValue(init); // only in order to set format pattern
			jSpinnerSampleRate.addChangeListener(this);
			jPanelSampleRate.add(jLabelSampleRate);
			jPanelSampleRate.add(jSpinnerSampleRate);
		}
		return jPanelSampleRate;
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: ResultLog
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonResultLog() {
		if (buttResultLog == null) {
			buttResultLog = new JRadioButton();
			buttResultLog.setText("Log");
			buttResultLog.setToolTipText("calculates logarithmic result values");
			buttResultLog.addActionListener(this);
			buttResultLog.setActionCommand("parameter");
			buttResultLog.setSelected(true);
		}
		return buttResultLog;
	}

	/**
	 * This method initializes the Option: ResultLin
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonResultLin() {
		if (buttResultLin == null) {
			buttResultLin = new JRadioButton();
			buttResultLin.setText("Lin");
			buttResultLin.setToolTipText("calculates linear result values");
			buttResultLin.addActionListener(this);
			buttResultLin.setActionCommand("parameter");
		}
		return buttResultLin;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelResult() {
		jPanelResult = new JPanel();
		//jPanelResult.setLayout(new BoxLayout(jPanelResult, BoxLayout.Y_AXIS));
		jPanelResult.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelResult.setBorder(new TitledBorder(null, "Result option", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelResult.add(getJRadioButtonResultLog());
		jPanelResult.add(getJRadioButtonResultLin());
		this.setButtonGroupResult(); // Grouping of JRadioButtons
		return jPanelResult;
	}

	private void setButtonGroupResult() {
		buttGroupResult = new ButtonGroup();
		buttGroupResult.add(buttResultLog);
		buttGroupResult.add(buttResultLin);
	}

	@Override
	public void update() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (e.getSource() == buttWithout) {
			}
			if (e.getSource() == buttRectangular) {
			}
			if (e.getSource() == buttBartlett) {
			}
			if (e.getSource() == buttWelch) {
			}
			if (e.getSource() == buttHann) {
			}
			if (e.getSource() == buttHamming) {
			}
			if (e.getSource() == buttKaiser) {
			}
			if (e.getSource() == buttGaussian) {
			}
			if (e.getSource() == buttResultLog) {
			}
			if (e.getSource() == buttResultLin) {
			}		
			this.updateParameterBlock();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateParameterBlock();
	}
}
