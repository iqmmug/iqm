package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_FracDFA.java
 * 
 * $Id: PlotGUI_FracDFA.java 634 2018-03-15 14:48:53Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-plot-op-bundle/src/main/java/at/mug/iqm/plot/bundle/gui/PlotGUI_FracDFA.java $
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
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracDFADescriptor;

/**
 * @author Ahammer
 * @since 2018-04-13 DFA according to Peng etal 1994 and e.g. Hardstone etal. 2012 
 */
public class PlotGUI_FracDFA extends AbstractPlotOperatorGUI implements
		ChangeListener {


	private static final long serialVersionUID = -8013592403743179707L;

	private static final Logger logger = LogManager.getLogger(PlotGUI_FracDFA.class);

	private ParameterBlockIQM pb;

	private int winSize  = 100;
	private int nSurr = 1;

    private JPanel  jRadioButtonsSingleGlidingPanel;
	
	private JPanel       jPanelSingleGlidingOption  = null;
	private ButtonGroup  buttGroupSingleGliding      = null;
	private JRadioButton buttSingleValue            = null;
	private JRadioButton buttGlidingValues          = null;
	
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

	private JPanel   jPanelWinSize   = null;
	private JLabel   jLabelWinSize   = null;
	private JSpinner jSpinnerWinSize = null;


	private JPanel   jPanelRegression = null;;
	private JPanel   jPanelRegStart   = null;
	private JLabel   jLabelRegStart   = null;
	private JSpinner jSpinnerRegStart = null;
	private JPanel   jPanelRegEnd     = null;
	private JLabel   jLabelRegEnd     = null;
	private JSpinner jSpinnerRegEnd   = null;

	private JCheckBox jCheckBoxShowPlot = null;
	private JCheckBox jCheckBoxDeleteExistingPlot = null;
	private JPanel    jPanelPlotOptions;
	

	public PlotGUI_FracDFA() {
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpFracDFADescriptor().getName());

		this.initialize();

		this.setTitle("Fractal DFA Dimension (Plot)");

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		getOpGUIContent().setLayout(gridBagLayout);

		this.getOpGUIContent().add(getJPanelWinSize(),             getGBC_WinSize());
		this.getOpGUIContent().add(getJPanelRegression(),          getGBC_Regression());
		this.getOpGUIContent().add(getJPanelPlotOptions(),         getGBC_plotOptionsPanel());
		this.getOpGUIContent().add(getJPanelSingleGlidingOption(), getGBC_SingleGlidingOption());
		this.getOpGUIContent().add(getJPanelOptionSurrogate(),     getGBC_SurrogateOption());

		this.setSingleGlidingOptionButtonGroup();
		this.setSurrogateOptionButtonGroup();

		this.pack();
	}

	private GridBagConstraints getGBC_WinSize() {
		GridBagConstraints gbc_WinSize = new GridBagConstraints();
		gbc_WinSize.gridx = 0;
		gbc_WinSize.gridy = 0;
		gbc_WinSize.insets = new Insets(10, 0, 0, 0); // top left bottom right
		gbc_WinSize.fill = GridBagConstraints.BOTH;
		return gbc_WinSize;
	}
	private GridBagConstraints getGBC_Regression() {
		GridBagConstraints gbc_Regression = new GridBagConstraints();	
		gbc_Regression.gridx = 0;
		gbc_Regression.gridy = 1;
		gbc_Regression.insets = new Insets(5, 0, 0, 0);
		gbc_Regression.fill = GridBagConstraints.BOTH;
		
		return gbc_Regression;
	}
	private GridBagConstraints getGBC_plotOptionsPanel(){
		GridBagConstraints gbc_PlotOptions = new GridBagConstraints();
		gbc_PlotOptions.gridx = 0;
		gbc_PlotOptions.gridy = 2;
		gbc_PlotOptions.insets = new Insets(5, 0, 5, 0);
		gbc_PlotOptions.fill = GridBagConstraints.BOTH;
	return gbc_PlotOptions;
	}
	private GridBagConstraints getGBC_SingleGlidingOption() {
		GridBagConstraints gbc_Options = new GridBagConstraints();
		gbc_Options.gridx = 0;
		gbc_Options.gridy = 3;
		gbc_Options.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_Options.fill = GridBagConstraints.BOTH;
		return gbc_Options;
	}

	private GridBagConstraints getGBC_SurrogateOption() {
		GridBagConstraints gbc_Surr= new GridBagConstraints();	
		gbc_Surr.gridx = 0;
		gbc_Surr.gridy = 4;
		gbc_Surr.insets = new Insets(5, 0, 5, 0);
		gbc_Surr.fill = GridBagConstraints.BOTH;
		
		return gbc_Surr;
	}	
	
	//-----------------------------------------------------------------------------------------------------
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("method") == 0) { buttSingleValue.doClick();
		} else if (pb.getIntParameter("method") == 1) {
			buttGlidingValues.doClick();
		}
		
		if (pb.getIntParameter("typeSurr") == -1)                               buttNoSurr.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_AAFT)         buttAAFT.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_GAUSSIAN)     buttGaussian.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_RANDOMPHASE)  buttRandPhase.doClick();
		if (pb.getIntParameter("typeSurr") == Surrogate.SURROGATE_SHUFFLE)      buttShuffle.doClick();
	
		
		jSpinnerNSurr.removeChangeListener(this);
		jSpinnerWinSize.removeChangeListener(this);
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);
		jSpinnerBoxLength.removeChangeListener(this);

		jSpinnerNSurr.setValue(pb.getIntParameter("nSurr"));
		jSpinnerWinSize.setValue(pb.getIntParameter("winSize"));
		jSpinnerRegStart.setValue(pb.getIntParameter("regStart"));
		jSpinnerRegEnd.setValue(pb.getIntParameter("regEnd"));
		jSpinnerBoxLength.setValue(pb.getIntParameter("boxLength"));

		jSpinnerNSurr.addChangeListener(this);
		jSpinnerWinSize.addChangeListener(this);
		jSpinnerRegStart.addChangeListener(this);
		jSpinnerRegEnd.addChangeListener(this);
		jSpinnerBoxLength.addChangeListener(this);

		if (pb.getIntParameter("showPlot") == 0) {
			jCheckBoxShowPlot.setSelected(false);
		} else {
			jCheckBoxShowPlot.setSelected(true);
		}

		if (pb.getIntParameter("deletePlot") == 0) {
			jCheckBoxDeleteExistingPlot.setSelected(false);
		} else {
			jCheckBoxDeleteExistingPlot.setSelected(true);
		}

	}

	@Override
	public void updateParameterBlock() {

		if (this.buttSingleValue.isSelected())   pb.setParameter("method", 0);
		if (this.buttGlidingValues.isSelected()) pb.setParameter("method", 1);
		
		if (this.buttNoSurr.isSelected())    pb.setParameter("typeSurr", -1);
		if (this.buttAAFT.isSelected())      pb.setParameter("typeSurr", Surrogate.SURROGATE_AAFT);
		if (this.buttGaussian.isSelected())  pb.setParameter("typeSurr", Surrogate.SURROGATE_GAUSSIAN);
		if (this.buttRandPhase.isSelected()) pb.setParameter("typeSurr", Surrogate.SURROGATE_RANDOMPHASE);
		if (this.buttShuffle.isSelected())   pb.setParameter("typeSurr", Surrogate.SURROGATE_SHUFFLE);
				
		pb.setParameter("nSurr", nSurr);
		pb.setParameter("winSize", winSize);

		pb.setParameter("boxLength", ((Number) jSpinnerBoxLength.getValue()).intValue());
		pb.setParameter("regStart",  ((Number) jSpinnerRegStart.getValue()).intValue());
		pb.setParameter("regEnd",    ((Number) jSpinnerRegEnd.getValue()).intValue());

		if (jCheckBoxShowPlot.isSelected()) {
			pb.setParameter("showPlot", 1);
		} else {
			pb.setParameter("showPlot", 0);
		}
		if (jCheckBoxDeleteExistingPlot.isSelected()) {
			pb.setParameter("deletePlot", 1);
		} else {
			pb.setParameter("deletePlot", 0);
		}
	}
    //-----------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: SingleValue
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonSingleValue() {
		if (buttSingleValue == null) {
			buttSingleValue = new JRadioButton();
			buttSingleValue.addActionListener(this);
			buttSingleValue.setText("Single value");
			buttSingleValue.setToolTipText("calculates a single DFA dimension value of the signal");
			buttSingleValue.setActionCommand("parameter");
			buttSingleValue.setSelected(true);
		}
		return buttSingleValue;
	}

	/**
	 * This method initializes the Option: GlidingValues
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonGlidingValues() {
		if (buttGlidingValues == null) {
			buttGlidingValues = new JRadioButton();
			buttGlidingValues.setText("Gliding values");
			buttGlidingValues.addActionListener(this);
			buttGlidingValues.setToolTipText("calculates gliding DFA dimension values of the signal");
			buttGlidingValues.setActionCommand("parameter");
		}
		return buttGlidingValues;
	}

	private void setSingleGlidingOptionButtonGroup() {
		buttGroupSingleGliding = new ButtonGroup();
		buttGroupSingleGliding.add(buttSingleValue);
		buttGroupSingleGliding.add(buttGlidingValues);
	}
	
	private JPanel getJRadioButtonPanel() {
		if (jRadioButtonsSingleGlidingPanel == null) {
			jRadioButtonsSingleGlidingPanel = new JPanel();
			jRadioButtonsSingleGlidingPanel.setLayout(new BoxLayout(jRadioButtonsSingleGlidingPanel, BoxLayout.Y_AXIS));
			jRadioButtonsSingleGlidingPanel.add(getJRadioButtonSingleValue());
			jRadioButtonsSingleGlidingPanel.add(getJRadioButtonGlidingValues());
		}
		return jRadioButtonsSingleGlidingPanel;
	}

	/**
	 * This method initializes jPanelSingleGlidingOption
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSingleGlidingOption() {
		if (jPanelSingleGlidingOption == null) {
			jPanelSingleGlidingOption = new JPanel();
			jPanelSingleGlidingOption.setLayout(new BoxLayout(jPanelSingleGlidingOption, BoxLayout.Y_AXIS));
			//jPanelSingleGlidingOption.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			jPanelSingleGlidingOption.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), 
					               "Calculation options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			jPanelSingleGlidingOption.add(getJRadioButtonPanel());
			jPanelSingleGlidingOption.add(getJPanelBoxLength());
	    }
		return jPanelSingleGlidingOption;
	}
	/**
	 * This method initializes jJPanelnSurr
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBoxLength() {
		if (jPanelBoxLength == null) {
			jPanelBoxLength = new JPanel();
			jPanelBoxLength.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jLabelBoxLength = new JLabel("Box length:");
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
	
	
	/**
	 * This method initializes jJPanelNumK
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelWinSize() {
		if (jPanelWinSize == null) {
			jPanelWinSize = new JPanel();
			
			jPanelWinSize.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), 
		               "Maximal window size", TitledBorder.LEADING, TitledBorder.TOP, 
		               null, new Color(0, 0, 0)));
			jPanelWinSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jLabelWinSize = new JLabel("window size: ");
			jLabelWinSize.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(100, 4, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerWinSize = new JSpinner(sModel);
			// jSpinnerNumK = new JSpinner();
			jSpinnerWinSize.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerWinSize.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelWinSize.add(jLabelWinSize);
			jPanelWinSize.add(jSpinnerWinSize);
		}
		return jPanelWinSize;
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
			buttNoSurr.setToolTipText("No surrogate(s) of the signal before computation of the DFA");
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
			buttShuffle.setToolTipText("Shuffled surrogate(s) of the signal before computation of the DFA");
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
			buttGaussian.setToolTipText("Gaussian surrogate(s) of the signal before computation of the DFA");
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
			buttRandPhase.setToolTipText("Randome phase surrogate(s) of the signal before computation of the DFA");
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
			buttAAFT.setToolTipText("AAFT surrogate(s) of the signal before computatoin of the DFA");
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

	/**
	 * This method initializes jJPanelRegStart
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRegStart() {
		if (jPanelRegStart == null) {
			jPanelRegStart = new JPanel();
			jPanelRegStart.setLayout(new FlowLayout());
			jLabelRegStart = new JLabel("Start:");
			jLabelRegStart.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(4, 4, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRegStart = new JSpinner(sModel);
			// jSpinnerRegStart = new JSpinner();
			jSpinnerRegStart.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegStart.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")												// ;
			jPanelRegStart.add(jLabelRegStart);
			jPanelRegStart.add(jSpinnerRegStart);
		}
		return jPanelRegStart;
	}

	/**
	 * This method initializes jJPanelRegEnd
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRegEnd() {
		if (jPanelRegEnd == null) {
			jPanelRegEnd = new JPanel();
			jPanelRegEnd.setLayout(new FlowLayout());
			jLabelRegEnd = new JLabel("End:");
			jLabelRegEnd.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(100, 4, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRegEnd = new JSpinner(sModel);
			// jSpinnerRegEnd = new JSpinner();
			jSpinnerRegEnd.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegEnd.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")												
			jPanelRegEnd.add(jLabelRegEnd);
			jPanelRegEnd.add(jSpinnerRegEnd);
		}
		return jPanelRegEnd;
	}
	/**
	 * This method initializes jJPanelRegression
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRegression() {
		if (jPanelRegression == null) {
			jPanelRegression = new JPanel();
			jPanelRegression.setBorder(new TitledBorder(null, "Regression", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jPanelRegression.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelRegression.add(getJPanelRegStart());
			jPanelRegression.add(getJPanelRegEnd());
		}
		return jPanelRegression;
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxShowPlot
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxShowPlot() {
		if (jCheckBoxShowPlot == null) {
			jCheckBoxShowPlot = new JCheckBox();
			jCheckBoxShowPlot.setText("ShowPlot");
			jCheckBoxShowPlot.addActionListener(this);
			jCheckBoxShowPlot.setActionCommand("parameter");
			jCheckBoxShowPlot.setSelected(true);
		}
		return jCheckBoxShowPlot;
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxDeleteExistingPlot
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxDeleteExistingPlot() {
		if (jCheckBoxDeleteExistingPlot == null) {
			jCheckBoxDeleteExistingPlot = new JCheckBox();
			jCheckBoxDeleteExistingPlot.setText("DeleteExistingPlot");
			jCheckBoxDeleteExistingPlot.addActionListener(this);
			jCheckBoxDeleteExistingPlot.setActionCommand("parameter");
			jCheckBoxDeleteExistingPlot.setSelected(true);
		}
		return jCheckBoxDeleteExistingPlot;
	}
	
	private JPanel getJPanelPlotOptions() {
		if (jPanelPlotOptions == null) {
			jPanelPlotOptions = new JPanel();
			jPanelPlotOptions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Plot output options",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			jPanelPlotOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelPlotOptions.add(getJCheckBoxShowPlot());
			jPanelPlotOptions.add(getJCheckBoxDeleteExistingPlot());
		}
		return jPanelPlotOptions;
	}

	@Override
	public void update() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (e.getSource() == buttSingleValue) {
				jLabelBoxLength.setEnabled(false);
				jSpinnerBoxLength.setEnabled(false);
			}
			if (e.getSource() == buttGlidingValues) {
				jLabelBoxLength.setEnabled(true);
				jSpinnerBoxLength.setEnabled(true);
				jCheckBoxShowPlot.setSelected(false);
			}
			
			if (e.getSource() == buttNoSurr) {
				jLabelNSurr.setEnabled(false);
				jSpinnerNSurr.setEnabled(false);
			}
			if ((e.getSource() == buttAAFT) || (e.getSource() == buttGaussian)  || (e.getSource() == buttRandPhase) || (e.getSource() == buttShuffle)) {
				jLabelNSurr.setEnabled(true);
				jSpinnerNSurr.setEnabled(true);
			}
			this.updateParameterBlock();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		nSurr        = ((Number) jSpinnerNSurr.getValue()).intValue();
		winSize      = ((Number) jSpinnerWinSize.getValue()).intValue();
		int regStart = ((Number) jSpinnerRegStart.getValue()).intValue();
		int regEnd   = ((Number) jSpinnerRegEnd.getValue()).intValue();

		jSpinnerNSurr.removeChangeListener(this);
		jSpinnerWinSize.removeChangeListener(this);
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);

		if (jSpinnerNSurr == e.getSource()) {
			//do nothing
		}
		if (jSpinnerWinSize == e.getSource()) {
			// if(regEnd > numK){
			jSpinnerRegEnd.setValue(winSize);
			regEnd = winSize;
			// }
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 2);
				regStart = regEnd - 2;
			}
		}
		if (jSpinnerRegEnd == e.getSource()) {
			if (regEnd > winSize) {
				jSpinnerRegEnd.setValue(winSize);
				regEnd = winSize;
			}
			if (regEnd <= (regStart + 1)) {
				jSpinnerRegEnd.setValue(regStart + 2);
				regEnd = regStart + 2;
			}
		}
		if (jSpinnerRegStart == e.getSource()) {
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 2);
				regStart = regEnd - 2;
			}
		}

		jSpinnerNSurr.addChangeListener(this);
		jSpinnerWinSize.addChangeListener(this);
		jSpinnerRegStart.addChangeListener(this);
		jSpinnerRegEnd.addChangeListener(this);

		this.updateParameterBlock();

	}

	
}
