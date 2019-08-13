package at.mug.iqm.plot.bundle.gui;


/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_Statistics.java
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

/**
 * @author Ahammer
 * @since  2012 11
 * @update 2014 10 added gliding box option, changed some format 
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
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

import org.apache.log4j.Logger;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpStatisticsDescriptor;


public class PlotGUI_Statistics extends AbstractPlotOperatorGUI implements
                                   ChangeListener{

	private static final Logger logger = Logger.getLogger(PlotGUI_Statistics.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8971570064739948207L;

	private ParameterBlockIQM pb;

	private JPanel       jRadioButtonsSingleGlidingPanel = null;
	
	private JPanel       jPanelSingleGlidingOption  = null;
	private ButtonGroup  buttGroupOption            = null;
	private JRadioButton buttSingleValue            = null;
	private JRadioButton buttGlidingValues          = null;
	
	private JPanel   jPanelBoxLength   = null;
	private JLabel   jLabelBoxLength   = null;
	private JSpinner jSpinnerBoxLength = null;
	
	private JCheckBox jCheckBoxNumDataPoints = null;
	private JCheckBox jCheckBoxMin           = null;
	private JCheckBox jCheckBoxMax           = null;
	private JCheckBox jCheckBoxMean          = null;
	private JCheckBox jCheckBoxMedian        = null;
	private JCheckBox jCheckBoxStdDev        = null;
	private JCheckBox jCheckBoxKurt          = null;
	private JCheckBox jCheckBoxSkew          = null;
	private JCheckBox jCheckBoxShannonEn     = null;
	private JPanel    jPanelOptions          = null;
	

	public PlotGUI_Statistics() {
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpStatisticsDescriptor().getName());
		this.initialize();
		this.setTitle("Plot Statistics");
		this.getOpGUIContent().setLayout(new GridBagLayout());
		
		this.getOpGUIContent().add(getJPanelSingleGlidingOption(),  get_gbc_SingleGlidingOption());	
		this.getOpGUIContent().add(getJPanelStatisticsOptions(),    get_gbc_Options());

		this.setSingleGlidingOptionButtonGroup();
		this.pack();
	}
	
	private GridBagConstraints get_gbc_SingleGlidingOption() {
		GridBagConstraints gbc_Method = new GridBagConstraints();
		gbc_Method.gridx = 0;
		gbc_Method.gridy = 0;
		gbc_Method.insets = new Insets(10, 0, 0, 0); // top left bottom right
		gbc_Method.fill = GridBagConstraints.BOTH;
		return gbc_Method;
	}
	
	private GridBagConstraints get_gbc_Options() {
		GridBagConstraints gbc_Options = new GridBagConstraints();
		gbc_Options.gridx = 0;
		gbc_Options.gridy = 1;
		gbc_Options.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gbc_Options.fill = GridBagConstraints.BOTH;
		return gbc_Options;
	}
	
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();
		
		if (pb.getIntParameter("method") == 0) {
			buttSingleValue.doClick();
		} else if (pb.getIntParameter("method") == 1) {
			buttGlidingValues.doClick();
		}
		
		jSpinnerBoxLength.removeChangeListener(this);
		jSpinnerBoxLength.setValue(pb.getIntParameter("boxLength"));
		jSpinnerBoxLength.addChangeListener(this);

		
		if (pb.getIntParameter("calcNDataPoints") == 0) {
			jCheckBoxNumDataPoints.setSelected(false);
		} else if (pb.getIntParameter("calcNDataPoints") == 1) {
			jCheckBoxNumDataPoints.setSelected(true);
		}
		if (pb.getIntParameter("calcMin") == 0) {
			jCheckBoxMin.setSelected(false);
		} else if (pb.getIntParameter("calcMin") == 1) {
			jCheckBoxMin.setSelected(true);
		}
		if (pb.getIntParameter("calcMax") == 0) {
			jCheckBoxMax.setSelected(false);
		} else if (pb.getIntParameter("calcMax") == 1) {
			jCheckBoxMax.setSelected(true);
		}
		if (pb.getIntParameter("calcMean") == 0) {
			jCheckBoxMean.setSelected(false);
		} else if (pb.getIntParameter("calcMean") == 1) {
			jCheckBoxMean.setSelected(true);
		}
		if (pb.getIntParameter("calcMedian") == 0) {
			jCheckBoxMedian.setSelected(false);
		} else if (pb.getIntParameter("calcMedian") == 1) {
			jCheckBoxMedian.setSelected(true);
		}
		if (pb.getIntParameter("calcStdDev") == 0) {
			jCheckBoxStdDev.setSelected(false);
		} else if (pb.getIntParameter("calcStdDev") == 1) {
			jCheckBoxStdDev.setEnabled(true);
		}
		if (pb.getIntParameter("calcKurtosis") == 0) {
			jCheckBoxKurt.setSelected(false);
		} else if (pb.getIntParameter("calcKurtosis") == 1) {
			jCheckBoxKurt.setSelected(true);
		}
		if (pb.getIntParameter("calcSkewness") == 0) {
			jCheckBoxSkew.setSelected(false);
		} else if (pb.getIntParameter("calcSkewness") == 1) {
			jCheckBoxSkew.setSelected(true);
		}
		if (pb.getIntParameter("calcShannonEn") == 0) {
			jCheckBoxShannonEn.setSelected(false);
		} else if (pb.getIntParameter("calcShannonEn") == 1) {
			jCheckBoxShannonEn.setSelected(true);
		}
	}

	@Override
	public void updateParameterBlock() {
		
		if (this.buttSingleValue.isSelected())
			pb.setParameter("method", 0);
		if (this.buttGlidingValues.isSelected())
			pb.setParameter("method", 1);
		
		pb.setParameter("boxLength", ((Number) jSpinnerBoxLength.getValue()).intValue());
		
		if (jCheckBoxNumDataPoints.isSelected()) {
			pb.setParameter("calcNDataPoints", 1);
		} else {
			pb.setParameter("calcNDataPoints", 0);
		}
		if (jCheckBoxMin.isSelected()) {
			pb.setParameter("calcMin", 1);
		} else {
			pb.setParameter("calcMin", 0);
		}
		if (jCheckBoxMax.isSelected()) {
			pb.setParameter("calcMax", 1);
		} else {
			pb.setParameter("calcMax", 0);
		}
		if (jCheckBoxMean.isSelected()) {
			pb.setParameter("calcMean", 1);
		} else {
			pb.setParameter("calcMean", 0);
		}
		if (jCheckBoxMedian.isSelected()) {
			pb.setParameter("calcMedian", 1);
		} else {
			pb.setParameter("calcMedian", 0);
		}
		if (jCheckBoxStdDev.isSelected()) {
			pb.setParameter("calcStdDev", 1);
		} else {
			pb.setParameter("calcStdDev", 0);
		}
		if (jCheckBoxKurt.isSelected()) {
			pb.setParameter("calcKurtosis", 1);
		} else {
			pb.setParameter("calcKurtosis", 0);
		}
		if (jCheckBoxSkew.isSelected()) {
			pb.setParameter("calcSkewness", 1);
		} else {
			pb.setParameter("calcSkewness", 0);
		}
		if (jCheckBoxShannonEn.isSelected()) {
			pb.setParameter("calcShannonEn", 1);
		} else {
			pb.setParameter("calcShannonEn", 0);
		}
	}
	
	/**
	 * This method initializes the Option: SingleValue
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonSingleValue() {
		if (buttSingleValue == null) {
			buttSingleValue = new JRadioButton();
			buttSingleValue.addActionListener(this);
			buttSingleValue.setText("Single value(s)");
			buttSingleValue.setToolTipText("calculates a single statistical values of the signal");
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
			buttGlidingValues.setToolTipText("calculates gliding box values values of the signal");
			buttGlidingValues.setActionCommand("parameter");
		}
		return buttGlidingValues;
	}

	private void setSingleGlidingOptionButtonGroup() {
		buttGroupOption = new ButtonGroup();
		buttGroupOption.add(buttSingleValue);
		buttGroupOption.add(buttGlidingValues);
	}

	/**
	 * This method initializes jPanelSingleGlidingOption
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSingleGlidingOption() {
		if (jPanelSingleGlidingOption == null) {
			jPanelSingleGlidingOption = new JPanel();
			jPanelSingleGlidingOption.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), 
					               "Calculation options", TitledBorder.LEADING, TitledBorder.TOP, 
					               null, new Color(0, 0, 0)));
			
			jPanelSingleGlidingOption.setLayout(new BoxLayout(jPanelSingleGlidingOption, BoxLayout.Y_AXIS));
			//jPanelSingleGlidingOption.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));			
			
			jPanelSingleGlidingOption.add(getJRadioButtonPanel());
			jPanelSingleGlidingOption.add(getJPanelBoxLength());
	    }
		return jPanelSingleGlidingOption;
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
	 * This method initializes jJPanelBoxLength
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
			jSpinnerBoxLength.addChangeListener((ChangeListener) this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerBoxLength.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")											// ;
			jPanelBoxLength.add(jLabelBoxLength);
			jPanelBoxLength.add(jSpinnerBoxLength);
			jLabelBoxLength.setEnabled(false);
			jSpinnerBoxLength.setEnabled(false);
		}
		return jPanelBoxLength;
	}
    //-------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxNumDataPoints
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxNumDataPoints() {
		if (jCheckBoxNumDataPoints == null) {
			jCheckBoxNumDataPoints = new JCheckBox();
			jCheckBoxNumDataPoints.setText("NumDataPoints");
			jCheckBoxNumDataPoints.setToolTipText("calculates the number of data points");
			jCheckBoxNumDataPoints.addActionListener(this);
			jCheckBoxNumDataPoints.setActionCommand("parameter");
			jCheckBoxNumDataPoints.setSelected(true);
		}
		return jCheckBoxNumDataPoints;
	}

	/**
	 * This method initializes jCheckBoxMin
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxMin() {
		if (jCheckBoxMin == null) {
			jCheckBoxMin = new JCheckBox();
			jCheckBoxMin.setText("Min");
			jCheckBoxMin.setToolTipText("calculates the minimum");
			jCheckBoxMin.addActionListener(this);
			jCheckBoxMin.setActionCommand("parameter");
			jCheckBoxMin.setSelected(true);
		}
		return jCheckBoxMin;
	}

	/**
	 * This method initializes jCheckBoxMax
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxMax() {
		if (jCheckBoxMax == null) {
			jCheckBoxMax = new JCheckBox();
			jCheckBoxMax.setText("Max");
			jCheckBoxMax.setToolTipText("calculates the maximum");
			jCheckBoxMax.addActionListener(this);
			jCheckBoxMax.setActionCommand("parameter");
			jCheckBoxMax.setEnabled(true);
			jCheckBoxMax.setSelected(true);
		}
		return jCheckBoxMax;
	}

	/**
	 * This method initializes jCheckBoxMean
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxMean() {
		if (jCheckBoxMean == null) {
			jCheckBoxMean = new JCheckBox();
			jCheckBoxMean.setText("Mean");
			jCheckBoxMean.setToolTipText("calculates the mean");
			jCheckBoxMean.addActionListener(this);
			jCheckBoxMean.setActionCommand("parameter");
			jCheckBoxMean.setEnabled(true);
			jCheckBoxMean.setSelected(true);
		}
		return jCheckBoxMean;
	}

	/**
	 * This method initializes jCheckBoxMean
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxMedian() {
		if (jCheckBoxMedian == null) {
			jCheckBoxMedian = new JCheckBox();
			jCheckBoxMedian.setText("Median");
			jCheckBoxMedian.setToolTipText("calculates the median");
			jCheckBoxMedian.addActionListener(this);
			jCheckBoxMedian.setActionCommand("parameter");
			jCheckBoxMedian.setEnabled(true);
			jCheckBoxMedian.setSelected(true);
		}
		return jCheckBoxMedian;
	}

	/**
	 * This method initializes jCheckBoxStdDev
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxStdDev() {
		if (jCheckBoxStdDev == null) {
			jCheckBoxStdDev = new JCheckBox();
			jCheckBoxStdDev.setText("StdDev");
			jCheckBoxStdDev.setToolTipText("calculates the standard deviation");
			jCheckBoxStdDev.addActionListener(this);
			jCheckBoxStdDev.setActionCommand("parameter");
			jCheckBoxStdDev.setEnabled(true);
			jCheckBoxStdDev.setSelected(true);
		}
		return jCheckBoxStdDev;
	}

	/**
	 * This method initializes jCheckBoxKurt
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxKurt() {
		if (jCheckBoxKurt == null) {
			jCheckBoxKurt = new JCheckBox();
			jCheckBoxKurt.setText("Kurtosis");
			jCheckBoxKurt.setToolTipText("calculates the kurtosis");
			jCheckBoxKurt.addActionListener(this);
			jCheckBoxKurt.setActionCommand("parameter");
			jCheckBoxKurt.setEnabled(true);
			jCheckBoxKurt.setSelected(true);
		}
		return jCheckBoxKurt;
	}

	/**
	 * This method initializes jCheckBoxSkew
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxSkew() {
		if (jCheckBoxSkew == null) {
			jCheckBoxSkew = new JCheckBox();
			jCheckBoxSkew.setText("Skewness");
			jCheckBoxSkew.setToolTipText("calculates the skewness");
			jCheckBoxSkew.addActionListener(this);
			jCheckBoxSkew.setActionCommand("parameter");
			jCheckBoxSkew.setEnabled(true);
			jCheckBoxSkew.setSelected(true);
		}
		return jCheckBoxSkew;
	}

	/**
	 * This method initializes jCheckBoxShannonEn
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxShannonEn() {
		if (jCheckBoxShannonEn == null) {
			jCheckBoxShannonEn = new JCheckBox();
			jCheckBoxShannonEn.setText("Shannon entropy");
			jCheckBoxShannonEn.setToolTipText("calculates the Shannon entropy");
			jCheckBoxShannonEn.addActionListener(this);
			jCheckBoxShannonEn.setActionCommand("parameter");
			jCheckBoxShannonEn.setEnabled(true);
			jCheckBoxShannonEn.setSelected(true);
		}
		return jCheckBoxShannonEn;
	}
	
	private JPanel getJPanelStatisticsOptions() {
		if (jPanelOptions == null) {
			jPanelOptions = new JPanel();
			
			jPanelOptions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), 
		                                    "Statistics options", TitledBorder.LEADING, TitledBorder.TOP, 
		                                     null, new Color(0, 0, 0)));
			
			jPanelOptions.setLayout(new BoxLayout(jPanelOptions, BoxLayout.Y_AXIS));
			
			jPanelOptions.add(getJCheckBoxNumDataPoints());
			jPanelOptions.add(getJCheckBoxMin());
			jPanelOptions.add(getJCheckBoxMax());
			jPanelOptions.add(getJCheckBoxMean());
			jPanelOptions.add(getJCheckBoxMedian());
			jPanelOptions.add(getJCheckBoxStdDev());
			jPanelOptions.add(getJCheckBoxKurt());
			jPanelOptions.add(getJCheckBoxSkew());
			jPanelOptions.add(getJCheckBoxShannonEn());
		}
		return jPanelOptions;
	}

	@Override
	public void update() {
		PlotModel plotModel = ((IqmDataBox) pb.getSource(0)).getPlotModel();
		
		SpinnerModel sModel = new SpinnerNumberModel(1, 1, plotModel.getData().size(), 1); // init, min, max, step
		jSpinnerBoxLength.setModel(sModel);
		
		this.updateParameterBlock();
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
			}
			
			if (jCheckBoxNumDataPoints == e.getSource()) {
			}
			if (jCheckBoxMin == e.getSource()) {
			}
			if (jCheckBoxMax == e.getSource()) {
			}
			if (jCheckBoxMean == e.getSource()) {
			}
			if (jCheckBoxMedian == e.getSource()) {
			}
			if (jCheckBoxStdDev == e.getSource()) {
			}
			if (jCheckBoxKurt == e.getSource()) {
			}
			if (jCheckBoxSkew == e.getSource()) {
			}
			if (jCheckBoxShannonEn == e.getSource()) {
			}

			this.updateParameterBlock();
		}

	}
	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		this.updateParameterBlock();
	}
}
