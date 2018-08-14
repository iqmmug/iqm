package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_FracAllomScale.java
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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracAllomScaleDescriptor;

/**
 * @author Ahammer
 * @since  2013 09
 
 */
public class PlotGUI_FracAllomScale extends AbstractPlotOperatorGUI implements
		ChangeListener {

	private static final long serialVersionUID = 3273196002797739135L;

	private static final Logger logger = Logger.getLogger(PlotGUI_FracAllomScale.class);

	private ParameterBlockIQM pb;

	

	private JPanel   jPanelRegression = null;;
	private JPanel   jPanelRegStart   = null;
	private JLabel   jLabelRegStart   = null;
	private JSpinner jSpinnerRegStart = null;
	
	private JPanel   jPanelRegEnd   = null;
	private JLabel   jLabelRegEnd   = null;
	private JSpinner jSpinnerRegEnd = null;

	private JCheckBox jCheckBoxShowPlot           = null;
	private JCheckBox jCheckBoxDeleteExistingPlot = null;
	private JPanel    jPanelPlotOptions           = null;

	public PlotGUI_FracAllomScale() {
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpFracAllomScaleDescriptor().getName());
		this.initialize();
		this.setTitle("Fractal Allometric Scaling Dimension (Plot)");
		getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelRegression(),  getGBC_Regression());
		this.getOpGUIContent().add(getJPanelPlotOptions(), getGBC_plotOptionsPanel());

		this.pack();
	}

	private GridBagConstraints getGBC_Regression() {
		GridBagConstraints gbc_Regression = new GridBagConstraints();
		gbc_Regression.gridx = 0;
		gbc_Regression.gridy = 3;
		gbc_Regression.insets = new Insets(10, 0, 0, 0);
		gbc_Regression.fill = GridBagConstraints.BOTH;
		return gbc_Regression;
	}
	

	private GridBagConstraints getGBC_plotOptionsPanel() {
	    GridBagConstraints gbc_PotOptions = new GridBagConstraints();
		gbc_PotOptions.fill = GridBagConstraints.BOTH;
		gbc_PotOptions.gridx = 0;
		gbc_PotOptions.gridy = 4;
		gbc_PotOptions.insets = new Insets(5, 0, 5, 0);
		gbc_PotOptions.fill = GridBagConstraints.BOTH;
		return gbc_PotOptions;
	}

	// -------------------------------------------------------------------------------------------
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);
	
		jSpinnerRegStart.setValue(pb.getIntParameter("regStart"));
		jSpinnerRegEnd.setValue(pb.getIntParameter("regEnd"));

		jSpinnerRegStart.addChangeListener(this);
		jSpinnerRegEnd.addChangeListener(this);

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

		pb.setParameter("regStart",
				((Number) jSpinnerRegStart.getValue()).intValue());
		pb.setParameter("regEnd",
				((Number) jSpinnerRegEnd.getValue()).intValue());

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
	//-----------------------------------------------------------------------------------------

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

	// ----------------------------------------------------------------------------------------------------------
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
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRegStart = new JSpinner(sModel);
			// jSpinnerRegStart = new JSpinner();	
			jSpinnerRegStart.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegStart.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
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
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRegEnd = new JSpinner(sModel);
			// jSpinnerRegEnd = new JSpinner();
			jSpinnerRegEnd.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegEnd.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelRegEnd.add(jLabelRegEnd);
			jPanelRegEnd.add(jSpinnerRegEnd);
		}
		return jPanelRegEnd;
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
			jPanelPlotOptions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Plot output options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			jPanelPlotOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelPlotOptions.add(getJCheckBoxShowPlot());
			jPanelPlotOptions.add(getJCheckBoxDeleteExistingPlot());
		}
		return jPanelPlotOptions;
	}

	@Override
	public void update() {
		IqmDataBox box = (IqmDataBox) this.workPackage.getSources().get(0);
		PlotModel plotModel = box.getPlotModel();
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
		int size = signal.size();
		
		int N = 0;
		while (N+1 < size/4){	
			N = N +1;
		}
		N= N-1;
		
		//set spinner
		SpinnerModel sModel = new SpinnerNumberModel(N, 1, size, 1); // init, min, max, step
		jSpinnerRegEnd.setModel(sModel);
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegEnd.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		ftf.setColumns(5);
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
		jSpinnerRegEnd.setValue(N);	     // only in order to set format pattern
		jSpinnerRegEnd.addChangeListener(this);
		
		this.updateParameterBlock();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			
			this.updateParameterBlock();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
	
		int regStart = ((Number) jSpinnerRegStart.getValue()).intValue();
		int regEnd = ((Number) jSpinnerRegEnd.getValue()).intValue();

		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);

	
		if (jSpinnerRegEnd == e.getSource()) {
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
		
		jSpinnerRegStart.addChangeListener(this);
		jSpinnerRegEnd.addChangeListener(this);

		this.updateParameterBlock();

	}




}
