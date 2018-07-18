package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_Entropy.java
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
import java.awt.Color;
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

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpEntropyDescriptor;

/**
 * <li>2012 11 QSE   quadratic sample entropy      according to Lake Moormann Am J Physiol Circ Physiol 300 H319-H325 2011
 * <li>2012 11 COSEn coefficient of sample entropy according to Lake Moormann Am J Physiol Circ Physiol 300 H319-H325 2011
 * <li>2012 02 Permutation entropy according to Bandt C and Pompe B. Permutation Entropy: A Natural Complexity Measure for Time Series. Phys Rev Lett Vol88(17) 2002
 * 
 * @author Ahammer
 * @since  2012 11
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 * @update 2018-03 added surrogate option
 */
public class PlotGUI_Entropy extends AbstractPlotOperatorGUI implements
		ChangeListener  {

	private static final Logger logger = Logger.getLogger(PlotGUI_Entropy.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8971570064739948207L;

	private ParameterBlockIQM pb;

	private int nSurr = 1;
	
	private JPanel       jPanelMethod      = null;
	private ButtonGroup  buttGroupMethod   = null;
	private JRadioButton buttSingleValue   = null;
	private JRadioButton buttGlidingValues = null;
	
	private JPanel       jPanelSingleGliding  = null;

	private JPanel   jPanelBoxLength   = null;
	private JLabel   jLabelBoxLength   = null;
	private JSpinner jSpinnerBoxLength = null;

	private JPanel       jPanelOptionSurrogate        = null;
	private JPanel       jPanelJRadioButtonsSurrogate = null;
	private ButtonGroup  buttGroupSurrogate           = null;
	private JRadioButton buttNoSurr                   = null;
	private JRadioButton buttShuffle                  = null;
	private JRadioButton buttGaussian                 = null;
	private JRadioButton buttRandPhase                = null;
	private JRadioButton buttAAFT                     = null;
	
	private JPanel       jPanelNSurr   = null;
	private JLabel       jLabelNSurr   = null;
	private JSpinner     jSpinnerNSurr = null;

	
	private JCheckBox jCheckBoxApEn   = null;
	private JCheckBox jCheckBoxSampEn = null;
	private JCheckBox jCheckBoxQSE    = null;
	private JCheckBox jCheckBoxCOSEn  = null;
	private JCheckBox jCheckBoxPEn    = null;

	private JPanel   jPanelApEnParamM   = null;
	private JLabel   jLabelApEnParamM   = null;
	private JSpinner jSpinnerApEnParamM = null;

	private JPanel   jPanelApEnParamR   = null;
	private JSpinner jSpinnerApEnParamR = null;
	private JLabel   jLabelApEnParamR   = null;

	private JPanel   jPanelApEnParamD   = null;
	private JLabel   jLabelApEnParamD   = null;
	private JSpinner jSpinnerApEnParamD = null;

	private JPanel   jPanelSampEnParamM   = null;
	private JLabel   jLabelSampEnParamM   = null;
	private JSpinner jSpinnerSampEnParamM = null;

	private JPanel   jPanelSampEnParamR   = null;
	private JSpinner jSpinnerSampEnParamR = null;
	private JLabel   jLabelSampEnParamR   = null;

	private JPanel   jPanelSampEnParamD   = null;
	private JLabel   jLabelSampEnParamD   = null;
	private JSpinner jSpinnerSampEnParamD = null;

	private JPanel   jPanelPEnParamM   = null;
	private JLabel   jLabelPEnParamM   = null;
	private JSpinner jSpinnerPEnParamM = null;

	private JPanel   jPanelPEnParamR   = null;
	private JSpinner jSpinnerPEnParamR = null;
	private JLabel   jLabelPEnParamR   = null;

	private JPanel   jPanelPEnParamD   = null;
	private JLabel   jLabelPEnParamD   = null;
	private JSpinner jSpinnerPEnParamD = null;
	
	private JPanel   jPanelOptions     = null;


	public PlotGUI_Entropy() {
		getOpGUIContent().setBorder(new EmptyBorder(10, 10, 10, 10));
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpEntropyDescriptor().getName());
		this.initialize();
		this.setTitle("Plot Entropy");
		this.getOpGUIContent().setLayout(new GridBagLayout());
	
		this.getOpGUIContent().add(getJPanelOptions(),         getGBC_Options());
		this.getOpGUIContent().add(getJPanelSingleGliding(),   getGBC_SingleGliding());
		this.getOpGUIContent().add(getJPanelOptionSurrogate(), getGBC_SurrogateOption());
		
	
		this.pack();
	}
	
	private GridBagConstraints getGBC_Options(){
		GridBagConstraints gbc_Options = new GridBagConstraints();
		gbc_Options.gridx = 0;
		gbc_Options.gridy = 0;
		gbc_Options.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gbc_Options.fill = GridBagConstraints.BOTH;
		return gbc_Options;
	}
	private GridBagConstraints getGBC_SingleGliding(){
		GridBagConstraints gbc_SingleGliding = new GridBagConstraints();
		gbc_SingleGliding.gridx = 0;
		gbc_SingleGliding.gridy = 1;
		gbc_SingleGliding.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_SingleGliding.fill = GridBagConstraints.BOTH;
		return gbc_SingleGliding;
	}
	private GridBagConstraints getGBC_SurrogateOption() {
		GridBagConstraints gbc_Surr= new GridBagConstraints();	
		gbc_Surr.gridx = 0;
		gbc_Surr.gridy = 2;
		gbc_Surr.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_Surr.fill = GridBagConstraints.BOTH;
		
		return gbc_Surr;
	}
	
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();
		
		if (buttSingleValue.isSelected()) {
			pb.setParameter("method", 0);
		} else if (buttGlidingValues.isSelected()) {
			pb.setParameter("method", 1);
		}

		if (pb.getIntParameter("typeSurr") == -1)                               buttNoSurr.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_AAFT)         buttAAFT.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_GAUSSIAN)     buttGaussian.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_RANDOMPHASE)  buttRandPhase.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_SHUFFLE)      buttShuffle.doClick();
		
		this.removeAllListeners();
		jSpinnerNSurr.setValue(pb.getIntParameter("nSurr"));
		jSpinnerBoxLength.setValue(pb.getIntParameter("boxLength"));
		
		jSpinnerApEnParamM.setValue(pb.getIntParameter("apEnParam_M"));
		jSpinnerApEnParamR.setValue(pb.getDoubleParameter("apEnParam_R"));
		jSpinnerApEnParamD.setValue(pb.getIntParameter("apEnParam_D"));

		jSpinnerSampEnParamM.setValue(pb.getIntParameter("sampEnParam_M"));
		jSpinnerSampEnParamR.setValue(pb.getDoubleParameter("sampEnParam_R"));
		jSpinnerSampEnParamD.setValue(pb.getIntParameter("sampEnParam_D"));

		jSpinnerPEnParamM.setValue(pb.getIntParameter("pEnParam_M"));
		// jSpinnerPEnParamR.setValues(pb.getFloatParameter("pEnParam_R"));
		jSpinnerPEnParamD.setValue(pb.getIntParameter("pEnParam_D"));

		this.addAllListeners();
	}

	private void addAllListeners() {
		jSpinnerNSurr.addChangeListener(this);
		
		jSpinnerApEnParamM.addChangeListener(this);
		jSpinnerApEnParamR.addChangeListener(this);
		jSpinnerApEnParamD.addChangeListener(this);

		jSpinnerSampEnParamM.addChangeListener(this);
		jSpinnerSampEnParamR.addChangeListener(this);
		jSpinnerSampEnParamD.addChangeListener(this);
		
		jSpinnerBoxLength.addChangeListener(this);
	}

	private void removeAllListeners() {
		jSpinnerNSurr.removeChangeListener(this);
		
		jSpinnerApEnParamM.removeChangeListener(this);
		jSpinnerApEnParamR.removeChangeListener(this);
		jSpinnerApEnParamD.removeChangeListener(this);

		jSpinnerSampEnParamM.removeChangeListener(this);
		jSpinnerSampEnParamR.removeChangeListener(this);
		jSpinnerSampEnParamD.removeChangeListener(this);
		
		jSpinnerBoxLength.removeChangeListener(this);
	}

	@Override
	public void updateParameterBlock() {
		
		if (this.buttSingleValue.isSelected())    pb.setParameter("method", 0); 
		if (this.buttGlidingValues.isSelected())  pb.setParameter("method", 1); 
		
		if (this.buttNoSurr.isSelected())    pb.setParameter("typeSurr", -1);
		if (this.buttAAFT.isSelected())      pb.setParameter("typeSurr", Surrogate.SURROGATE_AAFT);
		if (this.buttGaussian.isSelected())  pb.setParameter("typeSurr", Surrogate.SURROGATE_GAUSSIAN);
		if (this.buttRandPhase.isSelected()) pb.setParameter("typeSurr", Surrogate.SURROGATE_RANDOMPHASE);
		if (this.buttShuffle.isSelected())   pb.setParameter("typeSurr", Surrogate.SURROGATE_SHUFFLE);
		
		pb.setParameter("nSurr", nSurr);
		pb.setParameter("boxLength", ((Number)jSpinnerBoxLength.getValue()).intValue());	
		
		if (jCheckBoxApEn.isSelected()) { pb.setParameter("calcApEn", 1);
		} else {                          pb.setParameter("calcApEn", 0);
		}
		if (jCheckBoxSampEn.isSelected()) { pb.setParameter("calcSampEn", 1);
		} else {                            pb.setParameter("calcSampEn", 0);
		}
		if (jCheckBoxQSE.isSelected()) { pb.setParameter("calcQSE", 1);
		} else {                         pb.setParameter("calcQSE", 0);
		}
		if (jCheckBoxCOSEn.isSelected()) { pb.setParameter("calcCOSEn", 1);
		} else {                           pb.setParameter("calcCOSEn", 0);
		}
		if (jCheckBoxPEn.isSelected()) { pb.setParameter("calcPEn", 1);
		} else {                         pb.setParameter("calcPEn", 0);
		}
		pb.setParameter("apEnParam_M", ((Number) jSpinnerApEnParamM.getValue()).intValue());
		pb.setParameter("apEnParam_R", ((Number) jSpinnerApEnParamR.getValue()).doubleValue());
		pb.setParameter("apEnParam_D", ((Number) jSpinnerApEnParamD.getValue()).intValue());

		pb.setParameter("sampEnParam_M", ((Number) jSpinnerSampEnParamM.getValue()).intValue());
		pb.setParameter("sampEnParam_R", ((Number) jSpinnerSampEnParamR.getValue()).doubleValue());
		pb.setParameter("sampEnParam_D", ((Number) jSpinnerSampEnParamD.getValue()).intValue());

		pb.setParameter("sampEnParam_M", ((Number) jSpinnerPEnParamM.getValue()).intValue());
		// pb.setParameter("sampEnParam_R", ((Number)jSpinnerPEnParamR.getValue()).doubleValue());
		pb.setParameter("sampEnParam_D", ((Number) jSpinnerPEnParamD.getValue()).intValue());
	}
	
	// -------------------------------------------------------------------------------------------
	//Surrogate panel options
	
	/**
	 * This method initializes the Option: NoSurr
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonNoSurr() {
		if (buttNoSurr == null) {
			buttNoSurr = new JRadioButton();
			buttNoSurr.addActionListener(this);
			buttNoSurr.setText("No Surrogate(s)");
			buttNoSurr.setToolTipText("No surrogate(s) of the signal before computation of the Higuchi dimension(s)");
			buttNoSurr.setActionCommand("parameter");
			//buttNoSurr.setSelected(true);
		}
		return buttNoSurr;
	}
	/**
	 * This method initializes the Option: Shuffle
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonShuffle() {
		if (buttShuffle == null) {
			buttShuffle = new JRadioButton();
			buttShuffle.addActionListener(this);
			buttShuffle.setText("Shuffle");
			buttShuffle.setToolTipText("Shuffled surrogate(s) of the signal before computation of the Higuchi dimension(s)");
			buttShuffle.setActionCommand("parameter");
			//buttShuffle.setSelected(true);
		}
		return buttShuffle;
	}
	/**
	 * This method initializes the Option: Gaussian
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonGaussian() {
		if (buttGaussian == null) {
			buttGaussian = new JRadioButton();
			buttGaussian.addActionListener(this);
			buttGaussian.setText("Gaussian");
			buttGaussian.setToolTipText("Gaussian surrogate(s) of the signal before computation of the Higuchi dimension(s)");
			buttGaussian.setActionCommand("parameter");
			//buttGaussian.setSelected(true);
		}
		return buttGaussian;
	}
	/**
	 * This method initializes the Option: RandPhase
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonRandPhase() {
		if (buttRandPhase == null) {
			buttRandPhase = new JRadioButton();
			buttRandPhase.addActionListener(this);
			buttRandPhase.setText("RandPhase");
			buttRandPhase.setToolTipText("Randome phase surrogate(s) of the signal before computation of the Higuchi dimension(s)");
			buttRandPhase.setActionCommand("parameter");
			//buttRandPhase.setSelected(true);
		}
		return buttRandPhase;
	}
	/**
	 * This method initializes the Option: AAFT
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonAAFT() {
		if (buttAAFT == null) {
			buttAAFT = new JRadioButton();
			buttAAFT.addActionListener(this);
			buttAAFT.setText("AAFT");
			buttAAFT.setToolTipText("AAFT surrogate(s) of the signal before computatoin of the Higuchi dimension(s)");
			buttAAFT.setActionCommand("parameter");
			//buttAAFT.setSelected(true);
		}
		return buttAAFT;
	}
	
	private void setSurrogateOptionButtonGroup() {
		buttGroupSurrogate = new ButtonGroup();
		buttGroupSurrogate.add(buttNoSurr);
		buttGroupSurrogate.add(buttShuffle);
		buttGroupSurrogate.add(buttGaussian);
		buttGroupSurrogate.add(buttRandPhase);
		buttGroupSurrogate.add(buttAAFT);
	}
	
	private JPanel getJPanelJRadioButtonsSurrogate() {
		if (jPanelJRadioButtonsSurrogate == null) {
			jPanelJRadioButtonsSurrogate = new JPanel();
			jPanelJRadioButtonsSurrogate.setLayout(new BoxLayout(jPanelJRadioButtonsSurrogate, BoxLayout.Y_AXIS));
			jPanelJRadioButtonsSurrogate.add(getJRadioButtonNoSurr());
			jPanelJRadioButtonsSurrogate.add(getJRadioButtonShuffle());
			jPanelJRadioButtonsSurrogate.add(getJRadioButtonGaussian());
			jPanelJRadioButtonsSurrogate.add(getJRadioButtonRandPhase());
			jPanelJRadioButtonsSurrogate.add(getJRadioButtonAAFT());
			this.setSurrogateOptionButtonGroup();
		}
		return jPanelJRadioButtonsSurrogate;
	}
	
	
	/**
	 * This method initializes jJPanelNSurr
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNSurr() {
		if (jPanelNSurr == null) {
			jPanelNSurr = new JPanel();
			jPanelNSurr.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jLabelNSurr = new JLabel("#:");
			jLabelNSurr.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNSurr = new JSpinner(sModel);
			jSpinnerNSurr.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNSurr.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelNSurr.add(jLabelNSurr);
			jPanelNSurr.add(jSpinnerNSurr);
			jLabelNSurr.setEnabled(false);
			jSpinnerNSurr.setEnabled(false);
		}
		return jPanelNSurr;
	}
	

	/**
	 * This method initializes 
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOptionSurrogate() {
		if (jPanelOptionSurrogate == null) {
			jPanelOptionSurrogate = new JPanel();
			jPanelOptionSurrogate.setLayout(new BoxLayout(jPanelOptionSurrogate, BoxLayout.Y_AXIS));
			//jPanelOptionSurrogate.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			jPanelOptionSurrogate.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), 
					               "Surrogate options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			jPanelOptionSurrogate.add(getJPanelJRadioButtonsSurrogate());
			jPanelOptionSurrogate.add(getJPanelNSurr());
	    }
		return jPanelOptionSurrogate;
	}
	

	// -------------------------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------------------------------------
	
	private JPanel getJPanelSingleGliding() {
		if (jPanelSingleGliding == null) {
			jPanelSingleGliding = new JPanel();
			jPanelSingleGliding.setLayout(new BoxLayout(jPanelSingleGliding, BoxLayout.Y_AXIS));
			//jPanelSingleGliding.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			//jPanelSingleGlidingOption.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
			jPanelSingleGliding.setBorder(new TitledBorder(null, "Calcualtion options", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelSingleGliding.add(getJPanelMethod());
			jPanelSingleGliding.add(getJPanelBoxLength());
		}
		return jPanelSingleGliding;
	}
	
	/**
	 * This method initializes the Option: SingleValue
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSingleValue() {
		if (buttSingleValue == null) {
			buttSingleValue = new JRadioButton();
			buttSingleValue.setText("Single value");
			buttSingleValue.setToolTipText("calculates a single Higuchi dimension value of the signal");
			buttSingleValue.addActionListener(this);
			buttSingleValue.setActionCommand("parameter");
			buttSingleValue.setSelected(true);
		}
		return buttSingleValue;
	}

	/**
	 * This method initializes the Option: GlidingValues
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonGlidingValues() {
		if (buttGlidingValues == null) {
			buttGlidingValues = new JRadioButton();
			buttGlidingValues.setText("Gliding values");
			buttGlidingValues.setToolTipText("calculates gliding Higuchi dimension values of the signal");
			buttGlidingValues.addActionListener(this);
			buttGlidingValues.setActionCommand("parameter");
		}
		return buttGlidingValues;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.PanelPanel
	 */
	private JPanel getJPanelMethod() {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		//jPanelMethod.setBorder(new TitledBorder(null, "Calculation options", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethod.add(getJRadioButtonSingleValue());
		jPanelMethod.add(getJRadioButtonGlidingValues());

		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttSingleValue);
		buttGroupMethod.add(buttGlidingValues);
	}

	// -------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelBoxLength
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBoxLength() {
		if (jPanelBoxLength == null) {
			jPanelBoxLength = new JPanel();
			jPanelBoxLength.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jLabelBoxLength = new JLabel("Box length: ");
			jLabelBoxLength.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(100, 10, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerBoxLength = new JSpinner(sModel);
			jSpinnerBoxLength.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerBoxLength.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelBoxLength.add(jLabelBoxLength);
			jPanelBoxLength.add(jSpinnerBoxLength);
			jLabelBoxLength.setEnabled(false);
			jSpinnerBoxLength.setEnabled(false);
		}
		return jPanelBoxLength;
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * This method initializes jCheckBoxApEn
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxApEn() {
		if (jCheckBoxApEn == null) {
			jCheckBoxApEn = new JCheckBox();
			jCheckBoxApEn.setText("ApEn");
			jCheckBoxApEn.setToolTipText("calculates the approximate entropy (Pincus entropy)");
			jCheckBoxApEn.addActionListener(this);
			jCheckBoxApEn.setActionCommand("parameter");
			jCheckBoxApEn.setSelected(true);
		}
		return jCheckBoxApEn;
	}

	/**
	 * This method initializes jCheckBoxSampEn
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxSampEn() {
		if (jCheckBoxSampEn == null) {
			jCheckBoxSampEn = new JCheckBox();
			jCheckBoxSampEn.setText("SampEn");
			jCheckBoxSampEn.setToolTipText("calculates the sample entropy");
			jCheckBoxSampEn.addActionListener(this);
			jCheckBoxSampEn.setActionCommand("parameter");
			jCheckBoxSampEn.setSelected(true);
		}
		return jCheckBoxSampEn;
	}

	/**
	 * This method initializes jCheckBoxQSE
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxQSE() {
		if (jCheckBoxQSE == null) {
			jCheckBoxQSE = new JCheckBox();
			jCheckBoxQSE.setText("QSE");
			jCheckBoxQSE.setToolTipText("calculates the quadratic sample entropy = SampEn +ln(2*r)");
			jCheckBoxQSE.addActionListener(this);
			jCheckBoxQSE.setActionCommand("parameter");
			jCheckBoxQSE.setEnabled(false);
		}
		return jCheckBoxQSE;
	}

	/**
	 * This method initializes jCheckBoxCOSEn
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxCOSEn() {
		if (jCheckBoxCOSEn == null) {
			jCheckBoxCOSEn = new JCheckBox();
			jCheckBoxCOSEn.setText("COSEn");
			jCheckBoxCOSEn.setToolTipText("calculates the coefficient of sample entropy = SampEn -ln(r) - ln(mean)");
			jCheckBoxCOSEn.addActionListener(this);
			jCheckBoxCOSEn.setActionCommand("parameter");
			jCheckBoxCOSEn.setEnabled(false);
		}
		return jCheckBoxCOSEn;
	}

	/**
	 * This method initializes jCheckBoxPEn
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxPEn() {
		if (jCheckBoxPEn == null) {
			jCheckBoxPEn = new JCheckBox();
			jCheckBoxPEn.setText("PEn");
			jCheckBoxPEn.setToolTipText("calculates the permutation entropy");
			jCheckBoxPEn.addActionListener(this);
			jCheckBoxPEn.setActionCommand("parameter");
			jCheckBoxPEn.setSelected(true);
		}
		return jCheckBoxPEn;
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelApEnParamM
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelApEnParamM() {
		if (jPanelApEnParamM == null) {
			jPanelApEnParamM = new JPanel();
			jPanelApEnParamM.setBorder(new EmptyBorder(0, 0, 0, 5));
			jPanelApEnParamM.setLayout(new BorderLayout());
			jLabelApEnParamM = new JLabel("m: ");
			jLabelApEnParamM.setToolTipText("number of data points");
			// jLabelApEnParamM.setPreferredSize(new Dimension(70, 22));
			jLabelApEnParamM.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(2, 1, 100, 1); // init, min, max. step
			jSpinnerApEnParamM = new JSpinner(sModel);
			// jSpinnerApEnParamM.setPreferredSize(new Dimension(60, 22));
			jSpinnerApEnParamM.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerApEnParamM.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelApEnParamM.add(jLabelApEnParamM, BorderLayout.WEST);
			jPanelApEnParamM.add(jSpinnerApEnParamM, BorderLayout.CENTER);
		}
		return jPanelApEnParamM;
	}	
	/**
	 * This method initializes jJPanelApEnParamR
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelApEnParamR() {
		if (jPanelApEnParamR == null) {
			jPanelApEnParamR = new JPanel();
			jPanelApEnParamR.setBorder(new EmptyBorder(0, 0, 0, 5));
			jPanelApEnParamR.setLayout(new BorderLayout());
			jLabelApEnParamR = new JLabel("r: ");
			jLabelApEnParamR.setToolTipText("maximal distance radius");
			jLabelApEnParamR.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0.15, 0.05, 1.0, 0.05); // init, min, max. step
			jSpinnerApEnParamR = new JSpinner(sModel);
			jSpinnerApEnParamR.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerApEnParamR.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelApEnParamR.add(jLabelApEnParamR, BorderLayout.WEST);
			jPanelApEnParamR.add(jSpinnerApEnParamR, BorderLayout.CENTER);
		}
		return jPanelApEnParamR;
	}

	/**
	 * This method initializes jJPanelApEnParamD
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelApEnParamD() {
		if (jPanelApEnParamD == null) {
			jPanelApEnParamD = new JPanel();
			jPanelApEnParamD.setBorder(new EmptyBorder(0, 0, 0, 5));
			jPanelApEnParamD.setLayout(new BorderLayout());
			jLabelApEnParamD = new JLabel("delay: ");
			jLabelApEnParamD.setToolTipText("delay");
			jLabelApEnParamD.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, 100, 1); // init, min, max. step
			jSpinnerApEnParamD = new JSpinner(sModel);
			jSpinnerApEnParamD.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerApEnParamD.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelApEnParamD.add(jLabelApEnParamD, BorderLayout.WEST);
			jPanelApEnParamD.add(jSpinnerApEnParamD, BorderLayout.CENTER);
			jLabelApEnParamD.setEnabled(true);
			jSpinnerApEnParamD.setEnabled(true);
		}
		return jPanelApEnParamD;
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelSampEnParamM
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSampEnParamM() {
		if (jPanelSampEnParamM == null) {
			jPanelSampEnParamM = new JPanel();
			jPanelSampEnParamM.setBorder(new EmptyBorder(0, 0, 0, 5));
			jPanelSampEnParamM.setLayout(new BorderLayout());
			jLabelSampEnParamM = new JLabel("m: ");
			jLabelSampEnParamM.setToolTipText("number of data points");
			jLabelSampEnParamM.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(2, 1, 100, 1); // init, min, max. step
			jSpinnerSampEnParamM = new JSpinner(sModel);
			jSpinnerSampEnParamM.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerSampEnParamM.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelSampEnParamM.add(jLabelSampEnParamM, BorderLayout.WEST);
			jPanelSampEnParamM.add(jSpinnerSampEnParamM, BorderLayout.CENTER);
		}
		return jPanelSampEnParamM;
	}
	/**
	 * This method initializes jJPanelSampEnParamR
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSampEnParamR() {
		if (jPanelSampEnParamR == null) {
			jPanelSampEnParamR = new JPanel();
			jPanelSampEnParamR.setBorder(new EmptyBorder(0, 0, 0, 5));
			jPanelSampEnParamR.setLayout(new BorderLayout());
			jLabelSampEnParamR = new JLabel("r: ");
			jLabelSampEnParamR.setToolTipText("maximal distance radius");
			jLabelSampEnParamR.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0.15, 0.05, 1.0, 0.05); // init, min, max. step
			jSpinnerSampEnParamR = new JSpinner(sModel);
			jSpinnerSampEnParamR.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerSampEnParamR.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelSampEnParamR.add(jLabelSampEnParamR, BorderLayout.WEST);
			jPanelSampEnParamR.add(jSpinnerSampEnParamR, BorderLayout.CENTER);
		}
		return jPanelSampEnParamR;
	}

	/**
	 * This method initializes jJPanelSampEnParamD
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSampEnParamD() {
		if (jPanelSampEnParamD == null) {
			jPanelSampEnParamD = new JPanel();
			jPanelSampEnParamD.setBorder(new EmptyBorder(0, 0, 0, 5));
			jPanelSampEnParamD.setLayout(new BorderLayout());
			jLabelSampEnParamD = new JLabel("delay: ");
			jLabelSampEnParamD.setToolTipText("delay");
			jLabelSampEnParamD.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, 100, 1); // init, min, max. step
			jSpinnerSampEnParamD = new JSpinner(sModel);
			jSpinnerSampEnParamD.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerSampEnParamD.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelSampEnParamD.add(jLabelSampEnParamD, BorderLayout.WEST);
			jPanelSampEnParamD.add(jSpinnerSampEnParamD, BorderLayout.CENTER);
			jLabelSampEnParamD.setEnabled(true);
			jSpinnerSampEnParamD.setEnabled(true);
		}
		return jPanelSampEnParamD;
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelPEnParamM
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelPEnParamM() {
		if (jPanelPEnParamM == null) {
			jPanelPEnParamM = new JPanel();
			jPanelPEnParamM.setBorder(new EmptyBorder(0, 0, 0, 5));
			jPanelPEnParamM.setLayout(new BorderLayout());
			jLabelPEnParamM = new JLabel("n: ");
			jLabelPEnParamM.setToolTipText("order of permuation");
			jLabelPEnParamM.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, 100, 1); // init, min, max. step
			jSpinnerPEnParamM = new JSpinner(sModel);
			jSpinnerPEnParamM.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerPEnParamM.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelPEnParamM.add(jLabelPEnParamM, BorderLayout.WEST);
			jPanelPEnParamM.add(jSpinnerPEnParamM, BorderLayout.CENTER);
		}
		return jPanelPEnParamM;
	}
	/**
	 * This method initializes jJPanelPEnParamR
	 * 
	 * @return javax.swing.JPanel
	 */
	@SuppressWarnings("unused")
	private JPanel getJPanelPEnParamR() {
		if (jPanelPEnParamR == null) {
			jPanelPEnParamR = new JPanel();
			jPanelPEnParamR.setBorder(new EmptyBorder(0, 0, 0, 5));
			jPanelPEnParamR.setLayout(new BorderLayout());
			jLabelPEnParamR = new JLabel("r: ");
			jLabelPEnParamR.setToolTipText("maximal distance radius");
			jLabelPEnParamR.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0.15, 0.05, 1.0, 0.05); // init, min, max. step
			jSpinnerPEnParamR = new JSpinner(sModel);
			jSpinnerPEnParamR.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerPEnParamR.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelPEnParamR.add(jLabelPEnParamR, BorderLayout.WEST);
			jPanelPEnParamR.add(jSpinnerPEnParamR, BorderLayout.CENTER);
		}
		return jPanelPEnParamR;
	}


	/**
	 * This method initializes jJPanelPEnParamD
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelPEnParamD() {
		if (jPanelPEnParamD == null) {
			jPanelPEnParamD = new JPanel();
			jPanelPEnParamD.setBorder(new EmptyBorder(0, 0, 0, 5));
			jPanelPEnParamD.setLayout(new BorderLayout());
			jLabelPEnParamD = new JLabel("delay: ");
			jLabelPEnParamD.setToolTipText("delay");
			jLabelPEnParamD.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, 100, 1); // init, min, max. step
			jSpinnerPEnParamD = new JSpinner(sModel);
			jSpinnerPEnParamD.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerPEnParamD.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelPEnParamD.add(jLabelPEnParamD, BorderLayout.WEST);
			jPanelPEnParamD.add(jSpinnerPEnParamD, BorderLayout.CENTER);
			jLabelPEnParamD.setEnabled(true);
			jSpinnerPEnParamD.setEnabled(true);
		}
		return jPanelPEnParamD;
	}
	

	private GridBagConstraints getGBC_ApEn() {
		GridBagConstraints gridBagConstraintsApEn = new GridBagConstraints();
		gridBagConstraintsApEn.gridx = 0;
		gridBagConstraintsApEn.gridy = 2;
		gridBagConstraintsApEn.anchor = GridBagConstraints.WEST;
		gridBagConstraintsApEn.insets = new Insets(0, 35, 0, 0); // top left bottom right
		return gridBagConstraintsApEn;
	}
	private GridBagConstraints getGBC_SampEn() {
		GridBagConstraints gridBagConstraintsSampEn = new GridBagConstraints();
		gridBagConstraintsSampEn.gridx = 1;
		gridBagConstraintsSampEn.gridy = 2;
		gridBagConstraintsSampEn.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsSampEn.insets = new Insets(0, 35, 0, 0); // top left bottom right
		return gridBagConstraintsSampEn;
	}

	private GridBagConstraints getGBC_QSE() {
		GridBagConstraints gridBagConstraintsQSE = new GridBagConstraints();
		gridBagConstraintsQSE.gridx = 1;
		gridBagConstraintsQSE.gridy = 3;
		gridBagConstraintsQSE.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsQSE.insets = new Insets(0, 35, 0, 0); // top left bottom right
		return gridBagConstraintsQSE;
	}

	private GridBagConstraints getGBC_COSEn() {
		GridBagConstraints gridBagConstraintsCOSEn = new GridBagConstraints();
		gridBagConstraintsCOSEn.gridx = 1;
		gridBagConstraintsCOSEn.gridy = 4;
		gridBagConstraintsCOSEn.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsCOSEn.insets = new Insets(0, 35, 0, 0); // top left bottom right
		return gridBagConstraintsCOSEn;
	}

	private GridBagConstraints getGBC_PEn() {
		GridBagConstraints gridBagConstraintsPEn = new GridBagConstraints();
		gridBagConstraintsPEn.gridx = 2;
		gridBagConstraintsPEn.gridy = 2;
		gridBagConstraintsPEn.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsPEn.insets = new Insets(0, 35, 0, 0); // top left bottom right
		return gridBagConstraintsPEn;
	}

	private GridBagConstraints getGBC_ApEnParamM() {
		GridBagConstraints gridBagConstraintsApEnParamM = new GridBagConstraints();
		gridBagConstraintsApEnParamM.gridx = 0;
		gridBagConstraintsApEnParamM.gridy = 5;
		gridBagConstraintsApEnParamM.gridwidth = 1;
		gridBagConstraintsApEnParamM.anchor = GridBagConstraints.EAST;
		gridBagConstraintsApEnParamM.insets = new Insets(5, 5, 0, 5); // top left bottom right
		return gridBagConstraintsApEnParamM;
	}

	private GridBagConstraints getGBC_ApEnParamR() {
		GridBagConstraints gridBagConstraintsApEnParamR = new GridBagConstraints();
		gridBagConstraintsApEnParamR.anchor = GridBagConstraints.EAST;
		gridBagConstraintsApEnParamR.gridx = 0;
		gridBagConstraintsApEnParamR.gridy = 6;
		gridBagConstraintsApEnParamR.gridwidth = 1;
		gridBagConstraintsApEnParamR.insets = new Insets(5, 5, 0, 5); // top left bottom right
		return gridBagConstraintsApEnParamR;
	}

	private GridBagConstraints getGBC_ApEnParamD() {
		GridBagConstraints gridBagConstraintsApEnParamD = new GridBagConstraints();
		gridBagConstraintsApEnParamD.gridx = 0;
		gridBagConstraintsApEnParamD.gridy = 7;
		gridBagConstraintsApEnParamD.gridwidth = 1;
		gridBagConstraintsApEnParamD.anchor = GridBagConstraints.EAST;
		gridBagConstraintsApEnParamD.insets = new Insets(5, 5, 5, 5); // top left bottom right
		return gridBagConstraintsApEnParamD;
	}

	private GridBagConstraints getGBC_SampEnParamM() {
		GridBagConstraints gridBagConstraintsSampEnParamM = new GridBagConstraints();
		gridBagConstraintsSampEnParamM.gridx = 1;
		gridBagConstraintsSampEnParamM.gridy = 5;
		gridBagConstraintsSampEnParamM.gridwidth = 1;
		gridBagConstraintsSampEnParamM.anchor = GridBagConstraints.EAST;
		gridBagConstraintsSampEnParamM.insets = new Insets(5, 5, 0, 5); // top left bottom right
		return gridBagConstraintsSampEnParamM;
	}

	private GridBagConstraints getGBC_SampEnParamR() {
		GridBagConstraints gridBagConstraintsSampEnParamR = new GridBagConstraints();
		gridBagConstraintsSampEnParamR.anchor = GridBagConstraints.EAST;
		gridBagConstraintsSampEnParamR.gridx = 1;
		gridBagConstraintsSampEnParamR.gridy = 6;
		gridBagConstraintsSampEnParamR.gridwidth = 1;
		gridBagConstraintsSampEnParamR.insets = new Insets(5, 5, 0, 5); // top left bottom right
		return gridBagConstraintsSampEnParamR;
	}

	private GridBagConstraints getGBC_SampEnParamD() {
		GridBagConstraints gridBagConstraintsSampEnParamD = new GridBagConstraints();
		gridBagConstraintsSampEnParamD.gridx = 1;
		gridBagConstraintsSampEnParamD.gridy = 7;
		gridBagConstraintsSampEnParamD.gridwidth = 1;
		gridBagConstraintsSampEnParamD.anchor = GridBagConstraints.EAST;
		gridBagConstraintsSampEnParamD.insets = new Insets(5, 5, 5, 5); // top left bottom right
		return gridBagConstraintsSampEnParamD;
	}

	private GridBagConstraints getGBC_PEnParamM() {
		GridBagConstraints gridBagConstraintsPEnParamM = new GridBagConstraints();
		gridBagConstraintsPEnParamM.anchor = GridBagConstraints.EAST;
		gridBagConstraintsPEnParamM.gridx = 2;
		gridBagConstraintsPEnParamM.gridy = 5;
		gridBagConstraintsPEnParamM.gridwidth = 1;
		gridBagConstraintsPEnParamM.insets = new Insets(5, 0, 0, 0); // top left bottom right
		return gridBagConstraintsPEnParamM;
	}

	// private GridBagConstraints getGBC_PEnParamR(){
	// GridBagConstraints gridBagConstraintsPEnParamR = new
	// GridBagConstraints();
	// gridBagConstraintsPEnParamR.gridx = 3;
	// gridBagConstraintsPEnParamR.gridy = 10;
	// gridBagConstraintsPEnParamR.gridwidth = 1;//?
	// gridBagConstraintsPEnParamR.anchor = GridBagConstraints.LINE_START;
	// gridBagConstraintsPEnParamR.insets = new Insets(0,5,10,0); //top left bottom right
	// return gridBagConstraintsPEnParamR;
	// }
	private GridBagConstraints getGBC_PEnParamD() {
		GridBagConstraints gridBagConstraintsPEnParamD = new GridBagConstraints();
		gridBagConstraintsPEnParamD.gridx = 2;
		gridBagConstraintsPEnParamD.gridy = 7;
		gridBagConstraintsPEnParamD.gridwidth = 1;
		gridBagConstraintsPEnParamD.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsPEnParamD.insets = new Insets(5, 5, 5, 0); // top left bottom right
		return gridBagConstraintsPEnParamD;
	}

	
	private JPanel getJPanelOptions(){
		if (jPanelOptions == null){
			jPanelOptions = new JPanel();
			jPanelOptions.setLayout(new GridBagLayout());
			jPanelOptions.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			
			jPanelOptions.add(getJCheckBoxApEn(),          getGBC_ApEn());
			jPanelOptions.add(getJCheckBoxSampEn(),        getGBC_SampEn());
			jPanelOptions.add(getJCheckBoxQSE(),           getGBC_QSE());
			jPanelOptions.add(getJCheckBoxCOSEn(),         getGBC_COSEn());
			jPanelOptions.add(getJCheckBoxPEn(),           getGBC_PEn());
			jPanelOptions.add(getJPanelApEnParamM(),       getGBC_ApEnParamM());
			jPanelOptions.add(getJPanelApEnParamR(),       getGBC_ApEnParamR());
			jPanelOptions.add(getJPanelApEnParamD(),       getGBC_ApEnParamD());
			jPanelOptions.add(getJPanelSampEnParamM(),     getGBC_SampEnParamM());
			jPanelOptions.add(getJPanelSampEnParamR(),     getGBC_SampEnParamR());
			jPanelOptions.add(getJPanelSampEnParamD(),     getGBC_SampEnParamD());
			jPanelOptions.add(getJPanelPEnParamM(),        getGBC_PEnParamM());
			//jPanelOptions.add(getJSpinnerPEnParamR(),    getGBC_PEnParamR());
			jPanelOptions.add(getJPanelPEnParamD(),        getGBC_PEnParamD());
				
		}	
		return jPanelOptions;
	}
	
	
	
	
    //----------------------------------------------------------------------------------------------------------------------
	@Override
	public void update() {
		if (buttNoSurr.isSelected()) {
			jLabelNSurr.setEnabled(false);
			jSpinnerNSurr.setEnabled(false);
		}
		if (buttAAFT.isSelected() || buttGaussian.isSelected() || buttRandPhase.isSelected() || buttShuffle.isSelected()) {
			jLabelNSurr.setEnabled(true);
			jSpinnerNSurr.setEnabled(true);
		}
		if (buttSingleValue.isSelected()){
			jLabelBoxLength.setEnabled(false);
			jSpinnerBoxLength.setEnabled(false);
		}
		if (buttGlidingValues.isSelected()){
			jLabelBoxLength.setEnabled(true);
			jSpinnerBoxLength.setEnabled(true);
		}	
	
		if (jCheckBoxSampEn.isSelected()){
			jCheckBoxQSE.setEnabled(true);
			jCheckBoxCOSEn.setEnabled(true);
		} else {
			jCheckBoxQSE.setEnabled(false);
			jCheckBoxCOSEn.setEnabled(false);
			jCheckBoxQSE.setSelected(false);
			jCheckBoxCOSEn.setSelected(false);
		}
		
		
		if (jCheckBoxSampEn.isSelected() || jCheckBoxQSE.isSelected() || jCheckBoxCOSEn.isSelected()){
	
		}
		this.pack();
		this.updateParameterBlock();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.update();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		nSurr = ((Number) jSpinnerNSurr.getValue()).intValue();
		Object obE = e.getSource();

		if (jSpinnerNSurr == e.getSource()) {
			//do nothing
		}
		
		if (obE instanceof JSpinner) {

		}
		this.updateParameterBlock();
	}

}
