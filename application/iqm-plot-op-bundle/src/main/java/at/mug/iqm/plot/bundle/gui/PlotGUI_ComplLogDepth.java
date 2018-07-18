package at.mug.iqm.plot.bundle.gui;

import java.awt.Color;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_ComplLogDepth.java
 * 
 * $Id: PlotGUI_ComplLogDepth.java 505 2015-01-09 09:19:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-plot-op-bundle/src/main/java/at/mug/iqm/plot/bundle/gui/PlotGUI_ComplLogDepth.java $
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
import org.apache.log4j.Logger;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpComplLogDepthDescriptor;

/**
 * <li>2015 01  according to image implementation (Zenil et al)
 * 
 * @author Ahammer
 * @since  2015 01
 * @update 2018 03  HA Surrogate option
 */
public class PlotGUI_ComplLogDepth extends AbstractPlotOperatorGUI implements
		ChangeListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2173029778894918284L;

	private static final Logger logger = Logger.getLogger(PlotGUI_ComplLogDepth.class);

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

	private JRadioButton buttZLIB                = null;
	private JRadioButton buttGZIB                = null;
	private JPanel       jPanelCompressMethod    = null;
	private ButtonGroup  buttGroupCompressMethod = null;
	
	private JPanel   jPanelIterations   = null;
	private JLabel   jLabelIterations   = null;
	private JSpinner jSpinnerIterations = null;
	
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
	


	public PlotGUI_ComplLogDepth() {
		getOpGUIContent().setBorder(new EmptyBorder(10, 10, 10, 10));
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpComplLogDepthDescriptor().getName());
		this.initialize();
		this.setTitle("Plot ComplLogDepth");
		this.getOpGUIContent().setLayout(new GridBagLayout());
	
		this.getOpGUIContent().add(getJPanelSingleGliding(),   getGBC_SingleGliding());
		this.getOpGUIContent().add(getJPanelCompressMethod(),  getGBC_CompressMethod());
		this.getOpGUIContent().add(getJPanelIterations(),      getGBC_Iterations());
		this.getOpGUIContent().add(getJPanelOptionSurrogate(), getGBC_SurrogateOption());

		this.pack();
	}
	
	private GridBagConstraints getGBC_SingleGliding(){
		GridBagConstraints gbc_SingleGliding = new GridBagConstraints();
		gbc_SingleGliding.gridx = 0;
		gbc_SingleGliding.gridy = 0;
		gbc_SingleGliding.insets = new Insets(5, 0, 0, 0);  //top, left, bottom, right
		gbc_SingleGliding.fill = GridBagConstraints.BOTH;
		return gbc_SingleGliding;
	}	
	private GridBagConstraints getGBC_CompressMethod(){
		GridBagConstraints gbc_CompressMethod = new GridBagConstraints();
		gbc_CompressMethod.gridx = 0;
		gbc_CompressMethod.gridy = 1;
		gbc_CompressMethod.insets = new Insets(5, 0, 0, 0);  //top, left, bottom, right
		gbc_CompressMethod.fill = GridBagConstraints.BOTH;
		return gbc_CompressMethod;
	}	
	private GridBagConstraints getGBC_Iterations() {
		GridBagConstraints gbc_Iterations = new GridBagConstraints();
		gbc_Iterations.gridx = 0;
		gbc_Iterations.gridy = 2;
		gbc_Iterations.insets = new Insets(5, 0, 0, 0);  //top, left, bottom, right
		gbc_Iterations.fill = GridBagConstraints.BOTH;
		return gbc_Iterations;
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
		jSpinnerIterations.setValue(pb.getIntParameter("iterations"));
		jSpinnerNSurr.setValue(pb.getIntParameter("nSurr"));
		this.addAllListeners();
		
		if (pb.getIntParameter("compression") ==  PlotOpComplLogDepthDescriptor.COMPRESSION_ZLIB) buttZLIB.setSelected(true);
		if (pb.getIntParameter("compression") ==  PlotOpComplLogDepthDescriptor.COMPRESSION_GZIB) buttGZIB.setSelected(true);	

		if (pb.getIntParameter("typeSurr") == -1)                               buttNoSurr.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_AAFT)         buttAAFT.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_GAUSSIAN)     buttGaussian.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_RANDOMPHASE)  buttRandPhase.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_SHUFFLE)      buttShuffle.doClick();

	}
	
	private void addAllListeners() {	
		jSpinnerBoxLength.addChangeListener(this);
		jSpinnerIterations.addChangeListener(this);
		jSpinnerNSurr.removeChangeListener(this);
	}

	private void removeAllListeners() {		
		jSpinnerBoxLength.removeChangeListener(this);
		jSpinnerIterations.removeChangeListener(this);
		jSpinnerNSurr.addChangeListener(this);
	}

	@Override
	public void updateParameterBlock() {
		
		if (this.buttSingleValue.isSelected())    pb.setParameter("method", 0); 
		if (this.buttGlidingValues.isSelected())  pb.setParameter("method", 1); 
		
		pb.setParameter("boxLength", ((Number)jSpinnerBoxLength.getValue()).intValue());	
		
		if (buttZLIB.isSelected())     pb.setParameter("compression", PlotOpComplLogDepthDescriptor.COMPRESSION_ZLIB);
		if (buttGZIB.isSelected())     pb.setParameter("compression", PlotOpComplLogDepthDescriptor.COMPRESSION_GZIB);
		
		pb.setParameter("iterations", ((Number) jSpinnerIterations.getValue()).intValue());
		
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
	 * This method initializes the Option: ZLIB
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonZLIB() {
		if (buttZLIB == null) {
			buttZLIB = new JRadioButton();
			buttZLIB.setText("ZLIB");
			buttZLIB.setToolTipText("ZLIB compression (part of PNG standard)");
			buttZLIB.addActionListener(this);
			buttZLIB.setActionCommand("parameter");
		}
		return buttZLIB;
	}

	/**
	 * This method initializes the Option: GZIB
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonGZIB() {
		if (buttGZIB == null) {
			buttGZIB = new JRadioButton();
			buttGZIB.setText("GZIB");
			buttGZIB.setToolTipText("GZIB compression");
			buttGZIB.addActionListener(this);
			buttGZIB.setActionCommand("parameter");
			buttGZIB.setEnabled(true);
			buttGZIB.setVisible(true);
		}
		return buttGZIB;
	}

	/**
	 * This Method initializes jJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelCompressMethod() {
		// if (jPanelCompressMethod == null) {
		jPanelCompressMethod = new JPanel();
		jPanelCompressMethod.setLayout(new BoxLayout(jPanelCompressMethod, BoxLayout.X_AXIS));
		jPanelCompressMethod.setBorder(new TitledBorder(null, "Compression method", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		jPanelCompressMethod.add(getJRadioButtonZLIB());
		jPanelCompressMethod.add(getJRadioButtonGZIB());
		// jPanelCompressiMethod.addSeparator();
		this.setButtonGroupCompressMethod(); // Grouping of JRadioButtons
		// }
		return jPanelCompressMethod;
	}

	private void setButtonGroupCompressMethod() {
		// if (ButtonGroup buttGroupCompressMethod == null) {
		buttGroupCompressMethod = new ButtonGroup();
		buttGroupCompressMethod.add(buttZLIB);
		buttGroupCompressMethod.add(buttGZIB);
		
	}
	
	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelIterations
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIterations() {
		if (jPanelIterations == null) {
			jPanelIterations = new JPanel();
			//jPanelIterations.setLayout(new BoxLayout(jPanelIterations, BoxLayout.X_AXIS));
			jPanelIterations.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));
			jPanelIterations.setBorder(new TitledBorder(null, "Iterations", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelIterations.setToolTipText("Number of iterations. Each iteration yields a vlaue and the Median of these values is the result");
			jLabelIterations = new JLabel("#: ");
			// jLabelIterations.setPreferredSize(new Dimension(70, 22));
			//jLabelIterations.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerIterations = new JSpinner(sModel);
			jSpinnerIterations.setToolTipText("Number of iterations. Each iteration yields a duratioin and the Median of these durations is the result");
			//jSpinnerIterations.setPreferredSize(new Dimension(60, 24));
			jSpinnerIterations.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerIterations.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")								
			jPanelIterations.add(jLabelIterations);
			jPanelIterations.add(jSpinnerIterations);
		}
		return jPanelIterations;
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
		logger.debug(e.getActionCommand() + " event has been triggered.");
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
