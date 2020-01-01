package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracLac.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.media.jai.PlanarImage;
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

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracLacDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2011 04
 */
public class OperatorGUI_FracLac extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3671706108316698463L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_FracLac.class);

	private ParameterBlockIQM pb = null;

	private int numMaxEps;

	private JPanel   jPanelNumMaxEps   = null;
	private JLabel   jLabelNumMaxEps   = null;
	private JSpinner jSpinnerNumMaxEps = null;

	private JRadioButton buttGlidingBox  = null;
	private JRadioButton buttMethod2     = null;
	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;

	private JPanel   jPanelRegression = null;
	private JPanel   jPanelRegStart   = null;
	private JLabel   jLabelRegStart   = null;
	private JSpinner jSpinnerRegStart = null;
	private JPanel   jPanelRegEnd     = null;
	private JLabel   jLabelRegEnd     = null;
	private JSpinner jSpinnerRegEnd   = null;

	private JCheckBox jCheckBoxShowPlot           = null;
	private JCheckBox jCheckBoxDeleteExistingPlot = null;
	private JPanel    plotOptionsPanel;
	private JPanel    jpanelOptions;

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		pb.setParameter("NumMaxEps", ((Number) jSpinnerNumMaxEps.getValue()).intValue());

		if (buttGlidingBox.isSelected()) pb.setParameter("Method", 0);
		if (buttMethod2.isSelected())    pb.setParameter("Method", 1);

		pb.setParameter("RegStart", ((Number) jSpinnerRegStart.getValue()).intValue());
		pb.setParameter("RegEnd",   ((Number) jSpinnerRegEnd.getValue()).intValue());

		if (jCheckBoxShowPlot.isSelected())  pb.setParameter("ShowPlot", 1);
		if (!jCheckBoxShowPlot.isSelected()) pb.setParameter("ShowPlot", 0);
		if (jCheckBoxDeleteExistingPlot.isSelected())  pb.setParameter("DeleteExistingPlot", 1);
		if (!jCheckBoxDeleteExistingPlot.isSelected()) pb.setParameter("DeleteExistingPlot", 0);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		jSpinnerNumMaxEps.removeChangeListener(this);
		jSpinnerNumMaxEps.setValue(pb.getIntParameter("NumMaxEps"));
		jSpinnerNumMaxEps.addChangeListener(this);

		if (pb.getIntParameter("Method") == 0) buttGlidingBox.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttMethod2.setSelected(true);

		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegStart.setValue(pb.getIntParameter("RegStart"));
		jSpinnerRegStart.addChangeListener(this);

		jSpinnerRegEnd.removeChangeListener(this);
		jSpinnerRegEnd.setValue(pb.getIntParameter("RegEnd"));
		jSpinnerRegEnd.addChangeListener(this);

		if (pb.getIntParameter("ShowPlot") == 0) jCheckBoxShowPlot.setSelected(false);
		if (pb.getIntParameter("ShowPlot") == 1) jCheckBoxShowPlot.setSelected(true);
		if (pb.getIntParameter("DeleteExistingPlot") == 0) jCheckBoxDeleteExistingPlot.setSelected(false);
		if (pb.getIntParameter("DeleteExistingPlot") == 1) jCheckBoxDeleteExistingPlot.setSelected(true);

	}

	/**
	 * constructor
	 */
	public OperatorGUI_FracLac() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpFracLacDescriptor().getName());

		this.initialize();

		this.setTitle("Fractal Lacunarity");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.lacunarity.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());
		
	
		this.getOpGUIContent().add(getJPanelOptions(),    getGridBagConstraints_JPanelOptions());
		this.getOpGUIContent().add(getJPanelRegression(), getGridBagConstraints_Regression());
		this.getOpGUIContent().add(getPlotOptionsPanel(), getGridBagConstraints_plotOptionsPanel());

		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_JPanelOptions(){
		GridBagConstraints gbc_jPanelOptions = new GridBagConstraints();
		gbc_jPanelOptions.gridx = 0;
		gbc_jPanelOptions.gridy = 0;
		gbc_jPanelOptions.insets = new Insets(5, 0, 0, 0); // top, left bottom right
		gbc_jPanelOptions.fill = GridBagConstraints.BOTH;
	return gbc_jPanelOptions;
	}
	
	private GridBagConstraints getGridBagConstraints_Regression() {
		GridBagConstraints gbcRegression = new GridBagConstraints();
		//gbcRegression.fill = GridBagConstraints.HORIZONTAL;
		gbcRegression.gridx = 0;
		gbcRegression.gridy = 1;
		// gbcRegression.fill = GridBagConstraints.BOTH;
		return gbcRegression;
	}
	
	private GridBagConstraints getGridBagConstraints_plotOptionsPanel(){
		GridBagConstraints gbc_plotOptionsPanel = new GridBagConstraints();
		gbc_plotOptionsPanel.gridx = 0;
		gbc_plotOptionsPanel.gridy = 2;
		gbc_plotOptionsPanel.fill = GridBagConstraints.BOTH;
	return gbc_plotOptionsPanel;
	}

	// ---------------------------------------------------------------------------------------------
	private int getMaxEps(int width, int height) { // as normal Box counting
		float boxWidth = 1f;
		float kernelSize = (int) (2 * boxWidth + 1);
		int number = 1; // inclusive original image
		while ((kernelSize < width) || (kernelSize < height)) {
			boxWidth = boxWidth * 2;
			kernelSize = 2 * boxWidth + 1;
			// System.out.println("OperatorGUI_FracLac: newBoxWidth: " +
			// newBoxWidth);
			number = number + 1;
		}
		return number - 1;
	}


	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method updates the GUI if needed. This method overrides OperatorGUI
	 */
	@Override
	public void update() {
		PlanarImage pi = ((IqmDataBox) this.pb.getSources().firstElement())
				.getImage();
		int width = pi.getWidth();
		int height = pi.getHeight();
		numMaxEps = this.getMaxEps(width, height);

		jSpinnerNumMaxEps.removeChangeListener(this);
		jSpinnerNumMaxEps.setModel(new SpinnerNumberModel(numMaxEps, 3, numMaxEps, 1));
		jSpinnerNumMaxEps.addChangeListener(this);

		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegStart.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		jSpinnerRegStart.addChangeListener(this);

		jSpinnerRegEnd.removeChangeListener(this);
		jSpinnerRegEnd.setModel(new SpinnerNumberModel(numMaxEps, 1, Integer.MAX_VALUE, 1));
		jSpinnerRegEnd.addChangeListener(this);

		updateParameterBlock();
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelNumMaxEps
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumMaxEps() {
		if (jPanelNumMaxEps == null) {
			jPanelNumMaxEps = new JPanel();
			jPanelNumMaxEps.setLayout(new BorderLayout());
			jLabelNumMaxEps = new JLabel("Maximal epsilon: ");
			// jLabelNumMaxEps.setPreferredSize(new Dimension(70, 22));
			jLabelNumMaxEps.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumMaxEps = new JSpinner(sModel);
			jSpinnerNumMaxEps.setPreferredSize(new Dimension(60, 20));
			jSpinnerNumMaxEps.setEditor(new JSpinner.NumberEditor(jSpinnerNumMaxEps, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerNumMaxEps.getEditor()).getTextField().setEditable(true);
			jSpinnerNumMaxEps.addChangeListener(this);							
			jPanelNumMaxEps.add(jLabelNumMaxEps, BorderLayout.NORTH);
			jPanelNumMaxEps.add(jSpinnerNumMaxEps, BorderLayout.CENTER);
		}
		return jPanelNumMaxEps;
	}

	// -------------------------------------------------------------------------------------------

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttGlidingBox);
		buttGroupMethod.add(buttMethod2);
	}

	// -----------------------------------------------------------------------------------------

	/**
	 * This method initializes jJPanelRegression
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRegression() {
		if (jPanelRegression == null) {
			jPanelRegression = new JPanel();
			//jPanelRegression.setLayout(new BoxLayout(jPanelRegression, BoxLayout.X_AXIS));
			jPanelRegression.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelRegression.setBorder(new TitledBorder(null, "Regression", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
			// jLabelRegStart.setPreferredSize(new Dimension(40, 22));
			jLabelRegStart.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRegStart = new JSpinner(sModel);
			jSpinnerRegStart.setPreferredSize(new Dimension(60, 20));
			jSpinnerRegStart.setEditor(new JSpinner.NumberEditor(jSpinnerRegStart, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerRegStart.getEditor()).getTextField().setEditable(true);
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
			// jLabelRegEnd.setPreferredSize(new Dimension(40, 22));
			jLabelRegEnd.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3,Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRegEnd = new JSpinner(sModel);
			jSpinnerRegEnd.setPreferredSize(new Dimension(60, 20));
			jSpinnerRegEnd.setEditor(new JSpinner.NumberEditor(jSpinnerRegEnd, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerRegEnd.getEditor()).getTextField().setEditable(true);
			jSpinnerRegEnd.addChangeListener(this);
	
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
		}
		return jCheckBoxDeleteExistingPlot;
	}

	// ----------------------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	// -------------------------------------------------------------------------------------------------
	@Override
	public void stateChanged(ChangeEvent e) {

		numMaxEps    = ((Number) jSpinnerNumMaxEps.getValue()).intValue();
		int regStart = ((Number) jSpinnerRegStart.getValue()).intValue();
		int regEnd   = ((Number) jSpinnerRegEnd.getValue()).intValue();

		jSpinnerNumMaxEps.removeChangeListener(this);
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);

		if (jSpinnerNumMaxEps == e.getSource()) {
			if (regEnd > numMaxEps) {
				jSpinnerRegEnd.setValue(numMaxEps);
				regEnd = numMaxEps;
			}
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 1);
				regStart = regEnd - 1;
			}
		}
		if (jSpinnerRegEnd == e.getSource()) {
			if (regEnd > numMaxEps) {
				jSpinnerRegEnd.setValue(numMaxEps);
				regEnd = numMaxEps;
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

		jSpinnerNumMaxEps.addChangeListener(this);
		jSpinnerRegStart.addChangeListener(this);
		jSpinnerRegEnd.addChangeListener(this);

		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	private JPanel getPlotOptionsPanel() {
		if (plotOptionsPanel == null) {
			plotOptionsPanel = new JPanel();
			plotOptionsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Plot output options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			plotOptionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			plotOptionsPanel.add(getJCheckBoxShowPlot());
			plotOptionsPanel.add(getJCheckBoxDeleteExistingPlot());
		}
		return plotOptionsPanel;
	}

	private JPanel getJPanelOptions() {
		if (jpanelOptions == null) {
			jpanelOptions = new JPanel();
			jpanelOptions.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Lacunarity options",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			jpanelOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jpanelOptions.add(getJPanelNumMaxEps());

			buttGlidingBox = new JRadioButton();
			buttGlidingBox.setText("Gliding Box");
			buttGlidingBox.setToolTipText("Gliding Box");
			buttGlidingBox.addActionListener(this);
			buttGlidingBox.setActionCommand("parameter");

			buttMethod2 = new JRadioButton();
			buttMethod2.setText("Alternative");
			buttMethod2.setToolTipText("Alternative method (not implemented yet)");
			buttMethod2.addActionListener(this);
			buttMethod2.setActionCommand("parameter");
			buttMethod2.setEnabled(false);

			jPanelMethod = new JPanel();
			jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
			jPanelMethod.add(buttGlidingBox);
			jPanelMethod.add(buttMethod2);
			jpanelOptions.add(jPanelMethod);

			this.setButtonGroupMethod(); // Grouping of JRadioButtons
		}
		return jpanelOptions;
	}
}// END
