package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_FracHurst.java
 * 
 * $Id$
 * $HeadURL$
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

import at.mug.iqm.api.Application;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracHurstDescriptor;

/**
 * @author Ahammer
 * @since  2012 12
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class PlotGUI_FracHurst extends AbstractPlotOperatorGUI implements
		ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3854313562990110676L;

	private static final Logger logger = LogManager.getLogger(PlotGUI_FracHurst.class);

	private ParameterBlockIQM pb;

	private JPanel       jPanelMethodPSD    = null;
	private ButtonGroup  buttGroupMethodPSD = null;
	private JRadioButton buttPSD            = null;
	private JRadioButton buttlowPSDwe       = null;

	private JPanel       jPanelMethodSWV    = null;
	private ButtonGroup  buttGroupMethodSWV = null;
	private JRadioButton buttSWV            = null;
	private JRadioButton buttBdSWV          = null;

	private JPanel   jPanelRegression = null;
	private JPanel   jPanelRegStart   = null;
	private JLabel   jLabelRegStart   = null;
	private JSpinner jSpinnerRegStart = null;
	private JPanel   jPanelRegEnd     = null;
	private JLabel   jLabelRegEnd     = null;
	private JSpinner jSpinnerRegEnd   = null;

	private JCheckBox jCheckBoxShowPlot           = null;
	private JCheckBox jCheckBoxDeleteExistingPlot = null;
	private JPanel    jPanelPloOptions            = null;

	
	public PlotGUI_FracHurst() {
		logger.debug("Now initializing...");
		
		this.setOpName(new PlotOpFracHurstDescriptor().getName());	
		this.initialize();	
		this.setTitle("Fractal Hurst Coefficient");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelMethodPSD(),   getGBC_MethodPSD());
		this.getOpGUIContent().add(getJPanelMethodSWV(),   getGBC_MethodSWV());
		this.getOpGUIContent().add(getJPanelRegression(),  getGBC_Regression());
		this.getOpGUIContent().add(getJPanelPlotOptions(), getGBC_PlotOptions());

		this.pack();
	}
	
	private GridBagConstraints getGBC_MethodPSD() {
		GridBagConstraints gbc_MethodPSD = new GridBagConstraints();
		gbc_MethodPSD.gridx = 0;
		gbc_MethodPSD.gridy = 0;
		gbc_MethodPSD.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_MethodPSD.fill = GridBagConstraints.BOTH;
		return gbc_MethodPSD;
	}

	private GridBagConstraints getGBC_MethodSWV() {
		GridBagConstraints gbc_MethodSWV = new GridBagConstraints();
		gbc_MethodSWV.gridx = 0;
		gbc_MethodSWV.gridy = 1;
		gbc_MethodSWV.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_MethodSWV.fill = GridBagConstraints.BOTH;
		return gbc_MethodSWV;
	}

	private GridBagConstraints getGBC_Regression() {
		GridBagConstraints gbc_Regression = new GridBagConstraints();
		gbc_Regression.gridx = 0;
		gbc_Regression.gridy = 2;
		gbc_Regression.insets = new Insets(5, 0, 0, 0);	// top left bottom right
		gbc_Regression.fill = GridBagConstraints.BOTH;
		return gbc_Regression;
	}
	private GridBagConstraints getGBC_PlotOptions(){
		GridBagConstraints gbc_PloOptions = new GridBagConstraints();	
		gbc_PloOptions.gridx = 0;
		gbc_PloOptions.gridy = 3;
		gbc_PloOptions.insets = new Insets(5, 0, 5, 0);	// top left bottom right
		gbc_PloOptions.fill = GridBagConstraints.BOTH;
		return gbc_PloOptions;
	}
	
	//--------------------------------------------------------------------------------------
	
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("methodPSD") == 0) {
			buttPSD.setSelected(true);
		} else if (pb.getIntParameter("methodPSD") == 1) {
			buttlowPSDwe.setSelected(true);
		}

		if (pb.getIntParameter("methodSWV") == 0) {
			buttSWV.setSelected(true);
		} else if (pb.getIntParameter("methodSWV") == 1) {
			buttBdSWV.setSelected(true);
		}

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
		if (buttPSD.isSelected())
			pb.setParameter("methodPSD", 0);
		if (buttlowPSDwe.isSelected())
			pb.setParameter("methodPSD", 1);

		if (buttSWV.isSelected())
			pb.setParameter("methodSWV", 0);
		if (buttBdSWV.isSelected())
			pb.setParameter("methodSWV", 1);

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
	//----------------------------------------------------------------------------------------


	/**
	 * This method initializes the Option: PSD
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonPSD() {
		if (buttPSD == null) {
			buttPSD = new JRadioButton();
			buttPSD.setText("PSD");
			// buttPSD.setPreferredSize(new Dimension(105,10));
			buttPSD.setToolTipText("calculates the power spectrum density without any improvements");
			buttPSD.addActionListener(this);
			buttPSD.setActionCommand("parameter");
		}
		return buttPSD;
	}

	/**
	 * This method initializes the Option: lowPSDwe
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonlowPSDwe() {
		if (buttlowPSDwe == null) {
			buttlowPSDwe = new JRadioButton();
			buttlowPSDwe.setText("lowPSDw,e");
			// buttlowPSDwe.setPreferredSize(new Dimension(95,10));
			buttlowPSDwe.setToolTipText("calculates the power spectrum density using only lower frequencies (low), parabolic windowing (w) and end matching (e) (bridge detrending) ");
			buttlowPSDwe.addActionListener(this);
			buttlowPSDwe.setActionCommand("parameter");
			buttlowPSDwe.setSelected(true);
		}
		return buttlowPSDwe;
	}



	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethodPSD() {
		jPanelMethodPSD = new JPanel();
		jPanelMethodPSD.setLayout(new BoxLayout(jPanelMethodPSD, BoxLayout.Y_AXIS));
		//jPanelMethodPSD.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethodPSD.setBorder(new TitledBorder(null, "Method PSD", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethodPSD.add(getJRadioButtonPSD());
		jPanelMethodPSD.add(getJRadioButtonlowPSDwe());

		this.setButtonGroupMethodPSD(); // Grouping of JRadioButtons
		return jPanelMethodPSD;
	}

	private void setButtonGroupMethodPSD() {
		buttGroupMethodPSD = new ButtonGroup();
		buttGroupMethodPSD.add(buttPSD);
		buttGroupMethodPSD.add(buttlowPSDwe);
	}

	

	// ------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: PSD
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSWV() {
		if (buttSWV == null) {
			buttSWV = new JRadioButton();
			buttSWV.setText("SWV");
			// buttSWV.setPreferredSize(new Dimension(105,10));
			buttSWV.setToolTipText("calculates the scaled windowed variance (mean of SD's)");
			buttSWV.addActionListener(this);
			buttSWV.setActionCommand("parameter");
		}
		return buttSWV;
	}

	/**
	 * This method initializes the Option: BdSWV
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonBdSWV() {
		if (buttBdSWV == null) {
			buttBdSWV = new JRadioButton();
			buttBdSWV.setText("bdSWV");
			// buttBdSWV.setPreferredSize(new Dimension(95,10));
			buttBdSWV
					.setToolTipText("calculates the scaled windowed variance (mean of SD's) with bridge detrending");
			buttBdSWV.addActionListener(this);
			buttBdSWV.setActionCommand("parameter");
			buttBdSWV.setSelected(true);
		}
		return buttBdSWV;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethodSWV() {
		jPanelMethodSWV = new JPanel();
		jPanelMethodSWV.setLayout(new BoxLayout(jPanelMethodSWV, BoxLayout.Y_AXIS));
		//jPanelMethodSWV.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethodSWV.setBorder(new TitledBorder(null, "Method SWV", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethodSWV.add(getJRadioButtonSWV());
		jPanelMethodSWV.add(getJRadioButtonBdSWV());
		this.setButtonGroupMethodSWV(); // Grouping of JRadioButtons
		return jPanelMethodSWV;
	}

	private void setButtonGroupMethodSWV() {
		buttGroupMethodSWV = new ButtonGroup();
		buttGroupMethodSWV.add(buttSWV);
		buttGroupMethodSWV.add(buttBdSWV);
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
			int initValue = 1;
			SpinnerModel sModel = new SpinnerNumberModel(initValue, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRegStart = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegStart.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerRegStart.setValue(1); // just to set format
			jSpinnerRegStart.setValue(initValue); // just to set format
			jSpinnerRegStart.addChangeListener(this);
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
			int initValue = 3;
			SpinnerModel sModel = new SpinnerNumberModel(initValue, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRegEnd = new JSpinner(sModel);
			// jSpinnerRegEnd = new JSpinner();
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegEnd.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jSpinnerRegEnd.setValue(1); // just to set format
			jSpinnerRegEnd.setValue(initValue); // just to set format
			jSpinnerRegEnd.addChangeListener(this);
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
		if (jPanelPloOptions == null) {
			jPanelPloOptions = new JPanel();
			jPanelPloOptions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Plot output options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			jPanelPloOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelPloOptions.add(getJCheckBoxShowPlot());
			jPanelPloOptions.add(getJCheckBoxDeleteExistingPlot());
		}
		return jPanelPloOptions;
	}

	// ------------------------------------------------------------------------------------------------------

	@Override
	public void update() {
		// set maximal end value for regression
		int idx = Application.getManager().getCurrItemIndex();
		IqmDataBox iqmDataBox = Application.getTank().getCurrentTankIqmDataBoxAt(
				idx);
		int length = iqmDataBox.getPlotModel().getDomain().size();

		jSpinnerRegEnd.removeChangeListener(this);
		SpinnerModel sModel = new SpinnerNumberModel(length, 3, Integer.MAX_VALUE, 1); // init, min, max, step
		jSpinnerRegEnd.setModel(sModel);
		// jSpinnerRegEnd.setPreferredSize(new Dimension(60, 22));
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegEnd.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setColumns(5);
		ftf.setEditable(true);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
		jSpinnerRegEnd.setValue(1); // just to set format
		jSpinnerRegEnd.setValue(length); // just to set format
		jSpinnerRegEnd.addChangeListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {

			if (e.getSource() == buttPSD) {
			}
			if (e.getSource() == buttlowPSDwe) {
			}
			if (e.getSource() == buttSWV) {
			}
			if (e.getSource() == buttlowPSDwe) {
			}

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
