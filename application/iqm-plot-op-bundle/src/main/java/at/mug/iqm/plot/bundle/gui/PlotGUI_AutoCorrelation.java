package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_AutoCorrelation.java
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpAutoCorrelationDescriptor;

/**
 * @author Ahammer
 * @since  2012 11
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class PlotGUI_AutoCorrelation extends AbstractPlotOperatorGUI implements
		ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2548053727607836322L;

	private static final Logger logger = Logger.getLogger(PlotGUI_AutoCorrelation.class);

	private ParameterBlockIQM pb;

	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;
	private JRadioButton buttLargeN      = null;
	private JRadioButton buttSmallN      = null;
	private JRadioButton buttFFT         = null;

	private JCheckBox jCheckBoxLimitDelays = null;

	private JPanel   jPanelNumDelays   = null;
	private JLabel   jLabelNumDelays   = null;
	private JSpinner jSpinnerNumDelays = null;
	
	private JPanel       jPanelDelaysOptions = null;
	private TitledBorder tbDelaysOptions     = null;

	public PlotGUI_AutoCorrelation() {
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpAutoCorrelationDescriptor().getName());
		this.initialize();
		this.setTitle("Plot Auto Correlation");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelMethod(),         getGridBagConstraints_Method());
		this.getOpGUIContent().add(getJPanelDelaysOptions(),  getGridBagConstraints_DelaysOptions());

		this.pack();

	}

	private GridBagConstraints getGridBagConstraints_Method() {
		GridBagConstraints gbc_Method = new GridBagConstraints();
		gbc_Method.gridx = 0;
		gbc_Method.gridy = 0;
		gbc_Method.insets = new Insets(10, 0, 0, 0);
		gbc_Method.fill = GridBagConstraints.BOTH;
		return gbc_Method;
	}

	private GridBagConstraints getGridBagConstraints_DelaysOptions() {
		GridBagConstraints gbc_DelaysOptions = new GridBagConstraints();
		gbc_DelaysOptions.gridx = 0;
		gbc_DelaysOptions.gridy = 1;
		gbc_DelaysOptions.insets = new Insets(5, 0, 5, 0);
		gbc_DelaysOptions.fill = GridBagConstraints.BOTH;
		return gbc_DelaysOptions;
	}
	

	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		switch (pb.getIntParameter("method")) {
		case 0:
			this.buttLargeN.setSelected(true);
			break;
		case 1:
			this.buttSmallN.setSelected(true);
			break;
		case 2:
			this.buttFFT.setSelected(true);
			break;
		}

		if (pb.getIntParameter("limitDelays") == 0) {
			jCheckBoxLimitDelays.setSelected(false);
		} else if (pb.getIntParameter("limitDelays") == 1) {
			jCheckBoxLimitDelays.setSelected(true);
		}

		jSpinnerNumDelays.removeChangeListener(this);
		jSpinnerNumDelays.setValue(pb.getIntParameter("numDelays"));
		jSpinnerNumDelays.addChangeListener(this);
	}

	@Override
	public void updateParameterBlock() {
		if (this.buttLargeN.isSelected())
			pb.setParameter("method", 0);
		if (this.buttSmallN.isSelected())
			pb.setParameter("method", 1);
		if (this.buttFFT.isSelected())
			pb.setParameter("method", 2);

		if (jCheckBoxLimitDelays.isSelected()) {
			pb.setParameter("limitDelays", 1);
		} else {
			pb.setParameter("limitDelays", 0);
		}
		pb.setParameter("numDelays", ((Number) jSpinnerNumDelays.getValue()).intValue());
	}


	/**
	 * This method initializes the Option: LargeN
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonLargeN() {
		if (buttLargeN == null) {
			buttLargeN = new JRadioButton();
			buttLargeN.setText("large N");
			buttLargeN.setToolTipText("calculates the auto correlation for a large number of data points");
			buttLargeN.addActionListener(this);
			buttLargeN.setActionCommand("parameter");
		}
		return buttLargeN;
	}

	/**
	 * This method initializes the Option: SmallN
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSmallN() {
		if (buttSmallN == null) {
			buttSmallN = new JRadioButton();
			buttSmallN.setText("small N");
			buttSmallN.setToolTipText("calculates the auto correlation for a small number of data points");
			buttSmallN.addActionListener(this);
			buttSmallN.setActionCommand("parameter");
		}
		return buttSmallN;
	}

	/**
	 * This method initializes the Option: FFT
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonFFT() {
		if (buttFFT == null) {
			buttFFT = new JRadioButton();
			buttFFT.setText("FFT");
			buttFFT.setToolTipText("calculates the auto correlation using FFT");
			buttFFT.addActionListener(this);
			buttFFT.setActionCommand("parameter");
			buttFFT.setEnabled(true);
			buttFFT.setSelected(true);
		}
		return buttFFT;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.X_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelMethod.add(getJRadioButtonFFT());
		jPanelMethod.add(getJRadioButtonLargeN());
		jPanelMethod.add(getJRadioButtonSmallN());

		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttFFT);
		buttGroupMethod.add(buttLargeN);
		buttGroupMethod.add(buttSmallN);
	}

	/**
	 * This method initializes jCheckBoxLimitDelays
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxLimitDelays() {
		if (jCheckBoxLimitDelays == null) {
			jCheckBoxLimitDelays = new JCheckBox();
			jCheckBoxLimitDelays.setText("limit delays");
			jCheckBoxLimitDelays.setToolTipText("limited number of delays");
			jCheckBoxLimitDelays.addActionListener(this);
			jCheckBoxLimitDelays.setActionCommand("parameter");
			jCheckBoxLimitDelays.setEnabled(false);
		}
		return jCheckBoxLimitDelays;
	}

	/**
	 * This method initializes jJPanelNumDelays
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumDelays() {
		if (jPanelNumDelays == null) {
			jPanelNumDelays = new JPanel();
			//jPanelNumDelays.setBorder(new EmptyBorder(5, 0, 5, 0));
			jPanelNumDelays.setLayout(new BorderLayout());
			//jPanelNumDelays.setBorder(new TitledBorder(null, "Delays", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jLabelNumDelays = new JLabel("#: ");
			jLabelNumDelays.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 20;
			SpinnerModel sModel = new SpinnerNumberModel(init, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumDelays = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumDelays.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerNumDelays.setValue(1); // only in order to set format pattern
			jSpinnerNumDelays.setValue(init); // only in order to set format pattern
			jSpinnerNumDelays.addChangeListener(this);
			jPanelNumDelays.add(jLabelNumDelays, BorderLayout.WEST);
			jPanelNumDelays.add(jSpinnerNumDelays, BorderLayout.CENTER);
			jLabelNumDelays.setEnabled(false);
			jSpinnerNumDelays.setEnabled(false);
		}
		return jPanelNumDelays;
	}
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDelaysOptions() {
		jPanelDelaysOptions = new JPanel();
		//jPanelDelaysOptions.setLayout(new BoxLayout(jPanelDelaysOptions, BoxLayout.X_AXIS));
		jPanelDelaysOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		tbDelaysOptions = new TitledBorder(null, "Delays options", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		tbDelaysOptions.setTitleColor(Color.GRAY);
		jPanelDelaysOptions.setBorder(tbDelaysOptions);		
		jPanelDelaysOptions.add(getJPanelNumDelays());
		jPanelDelaysOptions.add(getJCheckBoxLimitDelays());
		return jPanelDelaysOptions;
	}
	

	@Override
	public void update() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (e.getSource() == buttLargeN) {
			}
			if (e.getSource() == buttSmallN) {
			}
			if (e.getSource() == buttFFT) {
			}
			if (jCheckBoxLimitDelays == e.getSource()) {
			}

			if (buttFFT.isSelected()) {
				jCheckBoxLimitDelays.setSelected(false);
				jCheckBoxLimitDelays.setEnabled(false);
				jLabelNumDelays.setEnabled(false);
				jSpinnerNumDelays.setEnabled(false);
				tbDelaysOptions.setTitleColor(Color.GRAY); repaint();
			} else {
				// jCheckBoxLimitDelays.setSelected(false);
				jCheckBoxLimitDelays.setEnabled(true);
				jLabelNumDelays.setEnabled(true);
				jSpinnerNumDelays.setEnabled(true);
				tbDelaysOptions.setTitleColor(Color.BLACK); repaint();
			}
			this.updateParameterBlock();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateParameterBlock();
	}

}
