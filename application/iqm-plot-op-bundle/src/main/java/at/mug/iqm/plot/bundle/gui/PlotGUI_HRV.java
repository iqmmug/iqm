package at.mug.iqm.plot.bundle.gui;

import java.awt.Color;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_HRV.java
 * 
 * $Id: PlotGUI_HRV.java 505 2015-01-09 09:19:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-plot-op-bundle/src/main/java/at/mug/iqm/plot/bundle/gui/PlotGUI_HRV.java $
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
 
 
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpHRVDescriptor;

/**
 * <li>according to  
 * 
 * @author Ahammer
 * @since  2018 08
 * @update 
 */
public class PlotGUI_HRV extends AbstractPlotOperatorGUI implements ChangeListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2139630637976119964L;

	  

	private ParameterBlockIQM pb;
	
	private int nSurr = 1;

	private JPanel       jPanelMethod      = null;
	private ButtonGroup  buttGroupMethod   = null;
	private JRadioButton buttSingleValue   = null;
	private JRadioButton buttGlidingValues = null;
	
	private JPanel       jPanelSingleGliding  = null;

	private JPanel       jPanelBoxLength   = null;
	private JLabel       jLabelBoxLength   = null;
	private JSpinner     jSpinnerBoxLength = null;

	private JCheckBox    buttStandard              = null;
	private JCheckBox    buttAdvanced              = null;
	private JPanel       jPanelParameterSelection  = null;
	
	
	private JRadioButton buttMillSec               = null;
	private JRadioButton buttSec                   = null;
	private JPanel       jPanelTimeBase            = null;
	private ButtonGroup  buttGroupTimeBase         = null;
	
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
	


	public PlotGUI_HRV() {
		getOpGUIContent().setBorder(new EmptyBorder(10, 10, 10, 10));
		System.out.println("IQM:  Now initializing...");

		this.setOpName(new PlotOpHRVDescriptor().getName());
		this.initialize();
		this.setTitle("Plot HRV");
		this.getOpGUIContent().setLayout(new GridBagLayout());
	
		this.getOpGUIContent().add(getJPanelParameterSelection(),  getGBC_ParameterSelection());
		this.getOpGUIContent().add(getJPanelTimeBase(),            getGBC_TimeBase());
		this.getOpGUIContent().add(getJPanelSingleGliding(),       getGBC_SingleGliding());
		this.getOpGUIContent().add(getJPanelOptionSurrogate(),     getGBC_SurrogateOption());

		this.pack();
	}
	
	
	private GridBagConstraints getGBC_ParameterSelection(){
		GridBagConstraints gbc_ParameterSelection = new GridBagConstraints();
		gbc_ParameterSelection.gridx = 0;
		gbc_ParameterSelection.gridy = 0;
		gbc_ParameterSelection.insets = new Insets(5, 0, 0, 0);  //top, left, bottom, right
		gbc_ParameterSelection.fill = GridBagConstraints.BOTH;
		return gbc_ParameterSelection;
	}	
	private GridBagConstraints getGBC_TimeBase() {
		GridBagConstraints gbc_TimeBase = new GridBagConstraints();
		gbc_TimeBase.gridx = 0;
		gbc_TimeBase.gridy = 1;
		gbc_TimeBase.insets = new Insets(5, 0, 0, 0);  //top, left, bottom, right
		gbc_TimeBase.fill = GridBagConstraints.BOTH;
		return gbc_TimeBase;
	}
	private GridBagConstraints getGBC_SingleGliding(){
		GridBagConstraints gbc_SingleGliding = new GridBagConstraints();
		gbc_SingleGliding.gridx = 0;
		gbc_SingleGliding.gridy = 2;
		gbc_SingleGliding.insets = new Insets(5, 0, 0, 0);  //top, left, bottom, right
		gbc_SingleGliding.fill = GridBagConstraints.BOTH;
		return gbc_SingleGliding;
	}	
	private GridBagConstraints getGBC_SurrogateOption() {
		GridBagConstraints gbc_Surr= new GridBagConstraints();	
		gbc_Surr.gridx = 0;
		gbc_Surr.gridy = 3;
		gbc_Surr.insets = new Insets(5, 0, 5, 0);
		gbc_Surr.fill = GridBagConstraints.BOTH;
		
		return gbc_Surr;
	}	
	
	
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();
		
		if (pb.getIntParameter("method") == 0) buttSingleValue.setSelected(true);
		if (pb.getIntParameter("method") == 1) buttGlidingValues.setSelected(true);
			
		this.removeAllListeners();
		jSpinnerBoxLength.setValue (pb.getIntParameter("boxLength"));
		jSpinnerNSurr.setValue(pb.getIntParameter("nSurr"));
		this.addAllListeners();
		
		if (pb.getIntParameter("timeBase") == 0) buttMillSec.setSelected(true);
		if (pb.getIntParameter("timeBase") == 1) buttSec.setSelected(true);
			
		
		if (pb.getIntParameter("standard") ==  1) buttStandard.setSelected(true);
		if (pb.getIntParameter("advanced") ==  1) buttAdvanced.setSelected(true);	

		if (pb.getIntParameter("typeSurr") == -1)                               buttNoSurr.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_AAFT)         buttAAFT.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_GAUSSIAN)     buttGaussian.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_RANDOMPHASE)  buttRandPhase.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_SHUFFLE)      buttShuffle.doClick();

	}
	
	private void addAllListeners() {	
		jSpinnerBoxLength.addChangeListener(this);
		jSpinnerNSurr.removeChangeListener(this);
	}

	private void removeAllListeners() {		
		jSpinnerBoxLength.removeChangeListener(this);
		jSpinnerNSurr.addChangeListener(this);
	}

	@Override
	public void updateParameterBlock() {
			
		if (this.buttSingleValue.isSelected())    pb.setParameter("method", 0); 
		if (this.buttGlidingValues.isSelected())  pb.setParameter("method", 1); 
		
		pb.setParameter ("boxLength", ((Number)jSpinnerBoxLength.getValue()).intValue());	
		
		if (buttStandard.isSelected())     pb.setParameter("standard", 1);
		if (buttAdvanced.isSelected())     pb.setParameter("advanced", 1);
		
		if (buttMillSec.isSelected()) pb.setParameter("timeBase", 0);
		if (buttSec.isSelected())     pb.setParameter("timeBase", 1);
			
		if (this.buttNoSurr.isSelected())    pb.setParameter("typeSurr", -1);
		if (this.buttAAFT.isSelected())      pb.setParameter("typeSurr", Surrogate.SURROGATE_AAFT);
		if (this.buttGaussian.isSelected())  pb.setParameter("typeSurr", Surrogate.SURROGATE_GAUSSIAN);
		if (this.buttRandPhase.isSelected()) pb.setParameter("typeSurr", Surrogate.SURROGATE_RANDOMPHASE);
		if (this.buttShuffle.isSelected())   pb.setParameter("typeSurr", Surrogate.SURROGATE_SHUFFLE);
				
		pb.setParameter("nSurr", nSurr);
	

	}
	
	
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
			buttSingleValue.setToolTipText("computes single values of the signal");
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
			buttGlidingValues.setToolTipText("computes gliding values of the signal");
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
	 * This method initializes the Option: Standard
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxStandard() {
		if (buttStandard == null) {
			buttStandard = new JCheckBox();
			buttStandard.setText("Standard");
			buttStandard.setToolTipText("Computes a list of standard HRV parameters");
			buttStandard.addActionListener(this);
			buttStandard.setActionCommand("parameter");
		}
		return buttStandard;
	}

	/**
	 * This method initializes the Option: Advanced
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxAdvanced() {
		if (buttAdvanced == null) {
			buttAdvanced = new JCheckBox();
			buttAdvanced.setText("Advanced");
			buttAdvanced.setToolTipText("Computes a list of additional HRV parameters");
			buttAdvanced.addActionListener(this);
			buttAdvanced.setActionCommand("parameter");
			buttAdvanced.setEnabled(false);
			buttAdvanced.setVisible(true);
		}
		return buttAdvanced;
	}

	/**
	 * This Method initializes jJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelParameterSelection() {
		// if (jPanelParameterSelection == null) {
		jPanelParameterSelection = new JPanel();
		jPanelParameterSelection.setLayout(new BoxLayout(jPanelParameterSelection, BoxLayout.X_AXIS));
		jPanelParameterSelection.setBorder(new TitledBorder(null, "Parameter selection", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		jPanelParameterSelection.add(getJCheckBoxStandard());
		jPanelParameterSelection.add(getJCheckBoxAdvanced());
		// jPanelParameterSelection.addSeparator();
		return jPanelParameterSelection;
	}

	
	
	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method gets the JRadioButton MillSec
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMillSec() {
		if (buttMillSec == null) {
			buttMillSec = new JRadioButton();
			buttMillSec.addActionListener(this);
			buttMillSec.setText("ms");
			buttMillSec.setToolTipText("Unit of data values is milliseconds");
			buttMillSec.setActionCommand("parameter");
			//buttMillSec.setSelected(true);
		}
		return buttMillSec;
	}
	
	/**
	 * This method gets the JRadioButton Sec
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSec() {
		if (buttSec == null) {
			buttSec = new JRadioButton();
			buttSec.addActionListener(this);
			buttSec.setText("s");
			buttSec.setToolTipText("Unit of data values is seconds");
			buttSec.setActionCommand("parameter");
			//buttSec.setSelected(true);
		}
		return buttSec;
	}
	
	/**
	 * This method initializes jJPanelTimeBase
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelTimeBase() {
		if (jPanelTimeBase == null) {
			jPanelTimeBase = new JPanel();
			//jPanelTimeBase.setLayout(new BoxLayout(jPanelTimeBase, BoxLayout.X_AXIS));
			jPanelTimeBase.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));
			jPanelTimeBase.setBorder(new TitledBorder(null, "Time base", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelTimeBase.setToolTipText("time base in ms or s");
			
			jPanelTimeBase.add(getJRadioButtonMillSec());
			jPanelTimeBase.add(getJRadioButtonSec());
			this.setButtonGroupTimeBase();
		}
		return jPanelTimeBase;
	}
	
	
	private void setButtonGroupTimeBase() {
		buttGroupTimeBase = new ButtonGroup();
		buttGroupTimeBase.add(buttMillSec);
		buttGroupTimeBase.add(buttSec);
		
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
			buttGaussian.setEnabled(false);
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
			buttRandPhase.setEnabled(false);
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
			buttAAFT.setEnabled(false);
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
	
    //----------------------------------------------------------------------------------------------------------------------
	@Override
	public void update() {
		if (buttSingleValue.isSelected()){
			jLabelBoxLength.setEnabled(false);
			jSpinnerBoxLength.setEnabled(false);
		}
		if (buttGlidingValues.isSelected()){
			jLabelBoxLength.setEnabled(true);
			jSpinnerBoxLength.setEnabled(true);
		}
		if (buttNoSurr.isSelected()) {
			jLabelNSurr.setEnabled(false);
			jSpinnerNSurr.setEnabled(false);
		}
		if (buttAAFT.isSelected()|| buttGaussian.isSelected()  || buttRandPhase.isSelected()|| buttShuffle.isSelected()) {
			jLabelNSurr.setEnabled(true);
			jSpinnerNSurr.setEnabled(true);
		}
		
		this.pack();
		this.updateParameterBlock();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("IQM:  "+e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.update();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		nSurr = ((Number) jSpinnerNSurr.getValue()).intValue();
		
		jSpinnerNSurr.removeChangeListener(this);
		
		if (jSpinnerNSurr == e.getSource()) {
			//do nothing
		}
		
		jSpinnerNSurr.addChangeListener(this);
		this.updateParameterBlock();
	}

}
