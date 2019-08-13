package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracGenDim.java
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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
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

import org.apache.log4j.Logger;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracGenDimDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2012 03
 */
public class OperatorGUI_FracGenDim extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 47978534483668000L;

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(OperatorGUI_FracGenDim.class);

	private ParameterBlockIQM pb = null;

	private int minQ;
	private int maxQ;
	private int numEps;
	private int maxEps;

	private JPanel   jPanelMinQ   = null;
	private JLabel   jLabelMinQ   = null;
	private JSpinner jSpinnerMinQ = null;

	private JPanel   jPanelMaxQ   = null;
	private JLabel   jLabelMaxQ   = null;
	private JSpinner jSpinnerMaxQ = null;

	private JPanel   jPanelNumEps   = null;
	private JLabel   jLabelNumEps   = null;
	private JSpinner jSpinnerNumEps = null;

	private JPanel   jPanelMaxEps   = null;
	private JLabel   jLabelMaxEps   = null;
	private JSpinner jSpinnerMaxEps = null;

	private JRadioButton buttLin            = null;
	private JRadioButton buttLog            = null;
	private JPanel       jPanelMethodEps    = null;
	private ButtonGroup  buttGroupMethodEps = null;

	private JRadioButton buttGlidingBox  = null;
	private JRadioButton buttRasterBox   = null;
	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;

	private JPanel   jPanelRegression = null;;
	private JPanel   jPanelRegStart   = null;
	private JLabel   jLabelRegStart   = null;
	private JSpinner jSpinnerRegStart = null;
	private JPanel   jPanelRegEnd     = null;
	private JLabel   jLabelRegEnd     = null;
	private JSpinner jSpinnerRegEnd   = null;

	private JCheckBox jCheckBoxShowPlot           = null;
	private JCheckBox jCheckBoxDeleteExistingPlot = null;

	private JCheckBox jCheckBoxShowPlotDq = null;
	private JCheckBox jCheckBoxShowPlotF  = null;
	private JPanel    plotOptionsPanel;
	private JPanel    panel;

	/**
	 * constructor
	 */
	public OperatorGUI_FracGenDim() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpFracGenDimDescriptor().getName());

		this.initialize();

		this.setTitle("Fractal Generalized Dimension");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.gendim.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());
	
	
		this.getOpGUIContent().add(getPanel(),            getGridBagConstraints_Panel());
		this.getOpGUIContent().add(getJPanelRegression(), getGridBagConstraints_Regression());
		this.getOpGUIContent().add(getPlotOptionsPanel(), getGridBagConstraints_PlotOptionsPanel());
		
		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_Panel(){
		GridBagConstraints gbc_Panel = new GridBagConstraints();
		gbc_Panel.fill = GridBagConstraints.BOTH;
		gbc_Panel.insets = new Insets(5, 0, 0, 0);
		gbc_Panel.gridx = 0;
		gbc_Panel.gridy = 0;
	return gbc_Panel;
    }
	
	private GridBagConstraints getGridBagConstraints_Regression() {
		GridBagConstraints gbc_Regression = new GridBagConstraints();
		gbc_Regression.insets = new Insets(0, 0, 5, 0);
		gbc_Regression.fill = GridBagConstraints.HORIZONTAL;
		gbc_Regression.gridx = 0;
		gbc_Regression.gridy = 1;
		// gridBagConstraintsRegression.fill = GridBagConstraints.BOTH;
		return gbc_Regression;
	}

	private GridBagConstraints getGridBagConstraints_PlotOptionsPanel(){
		GridBagConstraints gbc_plotOptionsPanel = new GridBagConstraints();
		gbc_plotOptionsPanel.fill = GridBagConstraints.BOTH;
		gbc_plotOptionsPanel.gridx = 0;
		gbc_plotOptionsPanel.gridy = 2;
		return gbc_plotOptionsPanel;
	}
	
	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		pb.setParameter("MinQ", ((Number) jSpinnerMinQ.getValue()).intValue());
		pb.setParameter("MaxQ", ((Number) jSpinnerMaxQ.getValue()).intValue());
		pb.setParameter("NumEps",
				((Number) jSpinnerNumEps.getValue()).intValue());
		pb.setParameter("MaxEps",
				((Number) jSpinnerMaxEps.getValue()).intValue());

		if (buttLin.isSelected())
			pb.setParameter("MethodEps", 0);
		if (buttLog.isSelected())
			pb.setParameter("MethodEps", 1);

		if (buttGlidingBox.isSelected())
			pb.setParameter("Method", 0);
		if (buttRasterBox.isSelected())
			pb.setParameter("Method", 1);

		pb.setParameter("RegStart",
				((Number) jSpinnerRegStart.getValue()).intValue());
		pb.setParameter("RegEnd",
				((Number) jSpinnerRegEnd.getValue()).intValue());

		if (jCheckBoxShowPlot.isSelected())
			pb.setParameter("ShowPlot", 1);
		if (!jCheckBoxShowPlot.isSelected())
			pb.setParameter("ShowPlot", 0);
		if (jCheckBoxDeleteExistingPlot.isSelected())
			pb.setParameter("DeleteExistingPlot", 1);
		if (!jCheckBoxDeleteExistingPlot.isSelected())
			pb.setParameter("DeleteExistingPlot", 0);
		if (jCheckBoxShowPlotDq.isSelected())
			pb.setParameter("ShowPlotDq", 1);
		if (!jCheckBoxShowPlotDq.isSelected())
			pb.setParameter("ShowPlotDq", 0);
		if (jCheckBoxShowPlotF.isSelected())
			pb.setParameter("ShowPlotF", 1);
		if (!jCheckBoxShowPlotF.isSelected())
			pb.setParameter("ShowPlotF", 0);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		jSpinnerMinQ.removeChangeListener(this);
		jSpinnerMinQ.setValue(pb.getIntParameter("MinQ"));
		jSpinnerMinQ.addChangeListener(this);

		jSpinnerMaxQ.removeChangeListener(this);
		jSpinnerMaxQ.setValue(pb.getIntParameter("MaxQ"));
		jSpinnerMaxQ.addChangeListener(this);

		jSpinnerNumEps.removeChangeListener(this);
		jSpinnerNumEps.setValue(pb.getIntParameter("NumEps"));
		jSpinnerNumEps.addChangeListener(this);

		jSpinnerMaxEps.removeChangeListener(this);
		jSpinnerMaxEps.setValue(pb.getIntParameter("MaxEps"));
		jSpinnerMaxEps.addChangeListener(this);

		if (pb.getIntParameter("MethodEps") == 0) buttLin.setSelected(true);
		if (pb.getIntParameter("MethodEps") == 1) buttLog.setSelected(true);

		if (pb.getIntParameter("Method") == 0) buttGlidingBox.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttRasterBox.setSelected(true);

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
		if (pb.getIntParameter("ShowPlotDq") == 0) jCheckBoxShowPlotDq.setSelected(false);
		if (pb.getIntParameter("ShowPlotDq") == 1) jCheckBoxShowPlotDq.setSelected(true);
		if (pb.getIntParameter("ShowPlotF") == 0)  jCheckBoxShowPlotF.setSelected(false);
		if (pb.getIntParameter("ShowPlotF") == 1)  jCheckBoxShowPlotF.setSelected(true);

	}

	// ---------------------------------------------------------------------------------------------
	private int getMaxEps(int width, int height) { // as normal Box counting
		int result = 0;
		if (buttGlidingBox.isSelected()) {
			result = (width + height) / 2;
		}
		if (buttRasterBox.isSelected()) {
			float boxWidth = 1f;
			int number = 1; // inclusive original image
			while ((boxWidth <= width) && (boxWidth <= height)) {
				boxWidth = boxWidth * 2;
				// System.out.println("OperatorGUI_FracBox: newBoxWidth: " +
				// newBoxWidth);
				number = number + 1;
			}
			result = number - 1;
		}
		return result;
	}
	
	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method updates the GUI if needed This method overrides IqmOpJFrame
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		PlanarImage pi = ((IqmDataBox) this.pb.getSources().firstElement())
				.getImage();
		int width = pi.getWidth();
		int height = pi.getHeight();
		minQ = -5;
		maxQ = 5;
		maxEps = this.getMaxEps(width, height);

		// System.out.println("OperatorGUI_FracGenDim: numberMax: " +
		// numberMax);
		SpinnerModel sModel = new SpinnerNumberModel(minQ, -Integer.MAX_VALUE,
				Integer.MAX_VALUE, 1); // init, min,
										// max, step
		jSpinnerMinQ.removeChangeListener(this);
		jSpinnerMinQ.setModel(sModel);
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMinQ
				.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setEditable(true);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf
				.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
											// ;
		jSpinnerMinQ.setValue(minQ);
		jSpinnerMinQ.addChangeListener(this);

		sModel = new SpinnerNumberModel(maxQ, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); // init, min, max, step
		jSpinnerMaxQ.removeChangeListener(this);
		jSpinnerMaxQ.setModel(sModel);
		defEditor = (JSpinner.DefaultEditor) jSpinnerMaxQ.getEditor();
		ftf = defEditor.getTextField();
		ftf.setEditable(true);
		intFormatter = (InternationalFormatter) ftf.getFormatter();
		decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
											// ;
		jSpinnerMaxQ.setValue(maxQ);
		jSpinnerMaxQ.addChangeListener(this);

		sModel = new SpinnerNumberModel(maxEps, 3, maxEps, 1); // init, min,  max, step
		jSpinnerMaxEps.removeChangeListener(this);
		jSpinnerMaxEps.setModel(sModel);
		defEditor = (JSpinner.DefaultEditor) jSpinnerMaxEps.getEditor();
		ftf = defEditor.getTextField();
		ftf.setEditable(true);
		intFormatter = (InternationalFormatter) ftf.getFormatter();
		decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
											// ;
		// jSpinnerNumMaxEps.setValue(maxEps);
		jSpinnerMaxEps.addChangeListener(this);

		int init = 10;
		if (buttGlidingBox.isSelected()) {
			if (maxEps < 10)
				init = maxEps;
		}
		if (buttRasterBox.isSelected()) {
			init = maxEps;
		}
		sModel = new SpinnerNumberModel(init, 3, maxEps, 1); // init, min,
																// max, step
		jSpinnerNumEps.removeChangeListener(this);
		jSpinnerNumEps.setModel(sModel);
		defEditor = (JSpinner.DefaultEditor) jSpinnerNumEps.getEditor();
		ftf = defEditor.getTextField();
		ftf.setEditable(true);
		intFormatter = (InternationalFormatter) ftf.getFormatter();
		decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
											// ;
		// jSpinnerNumEps.setValue(numEps);
		jSpinnerNumEps.addChangeListener(this);

		jSpinnerRegEnd.removeChangeListener(this);
		jSpinnerRegEnd.setValue(((Number) jSpinnerNumEps.getValue()).intValue());
		jSpinnerRegEnd.addChangeListener(this);

		this.updateParameterBlock();
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelMinQ
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMinQ() {
		if (jPanelMinQ == null) {
			jPanelMinQ = new JPanel();
			jPanelMinQ.setLayout(new BorderLayout());
			jLabelMinQ = new JLabel("min q: ");
			// jLabelMinQ.setPreferredSize(new Dimension(70, 20));
			jLabelMinQ.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(-5, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); // init, mi max, step
			jSpinnerMinQ = new JSpinner(sModel);
			// jSpinnerMinQ = new JSpinner();
			jSpinnerMinQ.setPreferredSize(new Dimension(60, 20));
			jSpinnerMinQ.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMinQ.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMinQ.add(jLabelMinQ, BorderLayout.WEST);
			jPanelMinQ.add(jSpinnerMinQ, BorderLayout.CENTER);
		}
		return jPanelMinQ;
	}

	/**
	 * This method initializes jJPanelMaxQ
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxQ() {
		if (jPanelMaxQ == null) {
			jPanelMaxQ = new JPanel();
			jPanelMaxQ.setLayout(new BorderLayout());
			jLabelMaxQ = new JLabel("max q: ");
			// jLabelMaxQ.setPreferredSize(new Dimension(70, 20));
			jLabelMaxQ.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(5, -Integer.MAX_VALUE,Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerMaxQ = new JSpinner(sModel);
			// jSpinnerMaxQ = new JSpinner();
			jSpinnerMaxQ.setPreferredSize(new Dimension(60, 20));
			jSpinnerMaxQ.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMaxQ.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMaxQ.add(jLabelMaxQ, BorderLayout.WEST);
			jPanelMaxQ.add(jSpinnerMaxQ, BorderLayout.CENTER);
		}
		return jPanelMaxQ;
	}

	/**
	 * This method initializes jJPanelNumEps
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumEps() {
		if (jPanelNumEps == null) {
			jPanelNumEps = new JPanel();
			jPanelNumEps.setLayout(new BorderLayout());
			jLabelNumEps = new JLabel("# of eps: ");
			// jLabelNumEps.setPreferredSize(new Dimension(70, 20));
			jLabelNumEps.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumEps = new JSpinner(sModel);
			// jSpinnerNumEps = new JSpinner();
			jSpinnerNumEps.setPreferredSize(new Dimension(60, 20));
			jSpinnerNumEps.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumEps.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelNumEps.add(jLabelNumEps, BorderLayout.NORTH);
			jPanelNumEps.add(jSpinnerNumEps, BorderLayout.CENTER);
		}
		return jPanelNumEps;
	}

	/**
	 * This method initializes jJPanelMaxEps
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxEps() {
		if (jPanelMaxEps == null) {
			jPanelMaxEps = new JPanel();
			jPanelMaxEps.setLayout(new BorderLayout());
			jLabelMaxEps = new JLabel("Max eps [pixel]: ");
			// jLabelMaxEps.setPreferredSize(new Dimension(70, 20));
			jLabelMaxEps.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerMaxEps = new JSpinner(sModel);
			// jSpinnerMaxEps = new JSpinner();
			jSpinnerMaxEps.setPreferredSize(new Dimension(60, 20));
			jSpinnerMaxEps.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMaxEps.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMaxEps.add(jLabelMaxEps, BorderLayout.NORTH);
			jPanelMaxEps.add(jSpinnerMaxEps, BorderLayout.CENTER);
		}
		return jPanelMaxEps;
	}

	// -------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtLin() {
		if (buttLin == null) {
			buttLin = new JRadioButton();
			buttLin.setText("Linear");
			buttLin.setToolTipText("linear distribution of epsilon values");
			buttLin.addActionListener(this);
			buttLin.setActionCommand("parameter");
		}
		return buttLin;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtLog() {
		if (buttLog == null) {
			buttLog = new JRadioButton();
			buttLog.setText("Log");
			buttLog.setToolTipText("logarithmic distribution of epsilon values");
			buttLog.addActionListener(this);
			buttLog.setActionCommand("parameter");
			buttLog.setEnabled(true);
		}
		return buttLog;
	}

	/**
	 * This method initializes jJPanelMethodEps
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethodEps() {
		if (jPanelMethodEps == null) {
			jPanelMethodEps = new JPanel();
			jPanelMethodEps.setLayout(new BoxLayout(jPanelMethodEps, BoxLayout.X_AXIS));
			jPanelMethodEps.add(getJRadioButtonButtLin());
			jPanelMethodEps.add(getJRadioButtonButtLog());
			// jPanelMethodEps.addSeparator();
			this.setButtonGroupMethodEps(); // Grouping of JRadioButtons
		}
		return jPanelMethodEps;
	}

	private void setButtonGroupMethodEps() {
		buttGroupMethodEps = new ButtonGroup();
		buttGroupMethodEps.add(buttLin);
		buttGroupMethodEps.add(buttLog);
	}

	// -------------------------------------------------------------------------------------------

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttGlidingBox);
		buttGroupMethod.add(buttRasterBox);
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
			jPanelRegression.setBorder(new TitledBorder(null, "Regression",TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jPanelRegression.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
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
			// jLabelRegStart.setPreferredSize(new Dimension(40, 20));
			jLabelRegStart.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRegStart = new JSpinner(sModel);
			// jSpinnerRegStart = new JSpinner();
			jSpinnerRegStart.setPreferredSize(new Dimension(60, 20));
			jSpinnerRegStart.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegStart.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
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
			// jLabelRegEnd.setPreferredSize(new Dimension(40, 20));
			jLabelRegEnd.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRegEnd = new JSpinner(sModel);
			// jSpinnerRegEnd = new JSpinner();
			jSpinnerRegEnd.setPreferredSize(new Dimension(60, 20));
			jSpinnerRegEnd.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegEnd.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
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

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxShowPlotDq
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxShowPlotDq() {
		if (jCheckBoxShowPlotDq == null) {
			jCheckBoxShowPlotDq = new JCheckBox();
			jCheckBoxShowPlotDq.setText("Show Dq plot");
			jCheckBoxShowPlotDq.addActionListener(this);
			jCheckBoxShowPlotDq.setActionCommand("parameter");
		}
		return jCheckBoxShowPlotDq;
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxShowPlotF
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxShowPlotF() {
		if (jCheckBoxShowPlotF == null) {
			jCheckBoxShowPlotF = new JCheckBox();
			jCheckBoxShowPlotF.setText("Show F spectrum");
			jCheckBoxShowPlotF.addActionListener(this);
			jCheckBoxShowPlotF.setActionCommand("parameter");
		}
		return jCheckBoxShowPlotF;
	}

	// ----------------------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (e.getSource() == buttGlidingBox) {
				buttLin.setEnabled(true);
				buttLog.setEnabled(true);
				jLabelMaxEps.setEnabled(true);
				jSpinnerMaxEps.setEnabled(true);
			}
			if (e.getSource() == buttRasterBox) {
				buttLin.setEnabled(false);
				buttLog.setEnabled(false);
				jLabelMaxEps.setEnabled(false);
				jSpinnerMaxEps.setEnabled(false);
			}
			
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

		minQ = ((Number) jSpinnerMinQ.getValue()).intValue();
		maxQ = ((Number) jSpinnerMaxQ.getValue()).intValue();
		numEps = ((Number) jSpinnerNumEps.getValue()).intValue();
		maxEps = ((Number) jSpinnerMaxEps.getValue()).intValue();
		int regStart = ((Number) jSpinnerRegStart.getValue()).intValue();
		int regEnd = ((Number) jSpinnerRegEnd.getValue()).intValue();

		jSpinnerMinQ.removeChangeListener(this);
		jSpinnerMaxQ.removeChangeListener(this);
		jSpinnerNumEps.removeChangeListener(this);
		jSpinnerMaxEps.removeChangeListener(this);
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);

		if (jSpinnerMinQ == e.getSource()) {
			if (maxQ <= minQ) {
				maxQ = minQ + 1;
				jSpinnerMaxQ.setValue(maxQ);
			}
		}
		if (jSpinnerMaxQ == e.getSource()) {
			if (minQ >= maxQ) {
				minQ = maxQ - 1;
				jSpinnerMinQ.setValue(minQ);
			}
		}
		if (jSpinnerNumEps == e.getSource()) {
			if (buttGlidingBox.isSelected()) {
				if (numEps > maxEps) {
					numEps = maxEps;
					jSpinnerNumEps.setValue(numEps);
				}
			}
			if (buttRasterBox.isSelected()) {

			}
			// if(numEps > regEnd){
			regEnd = numEps;
			jSpinnerRegEnd.setValue(regEnd);
			// }
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 2);
				regStart = regEnd - 2;
			}
		}
		if (jSpinnerMaxEps == e.getSource()) {
			if (maxEps < numEps) {
				numEps = maxEps;
				jSpinnerNumEps.setValue(numEps);
			}
			if (maxEps < regEnd) {
				regEnd = maxEps;
				jSpinnerRegEnd.setValue(regEnd);
			}
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 2);
				regStart = regEnd - 2;
			}
		}

		if (jSpinnerRegEnd == e.getSource()) {
			if (regEnd > numEps) {
				jSpinnerRegEnd.setValue(numEps);
				regEnd = numEps;
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

		jSpinnerMinQ.addChangeListener(this);
		jSpinnerMaxQ.addChangeListener(this);
		jSpinnerNumEps.addChangeListener(this);
		jSpinnerMaxEps.addChangeListener(this);
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
			plotOptionsPanel.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Plot output options",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			plotOptionsPanel.setLayout(new GridLayout(0, 2, 5, 5));
			plotOptionsPanel.add(getJCheckBoxShowPlot());
			plotOptionsPanel.add(getJCheckBoxDeleteExistingPlot());
			plotOptionsPanel.add(getJCheckBoxShowPlotDq());
			plotOptionsPanel.add(getJCheckBoxShowPlotF());
		}
		return plotOptionsPanel;
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"),
					"Generalized dimension options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 0, 0, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			panel.setLayout(gbl_panel);
			
			GridBagConstraints gbc_jPanelMinQ = new GridBagConstraints();
			gbc_jPanelMinQ.insets = new Insets(5, 5, 5, 5);
			gbc_jPanelMinQ.gridx = 0;
			gbc_jPanelMinQ.gridy = 0;
			panel.add(getJPanelMinQ(), gbc_jPanelMinQ);
			
			GridBagConstraints gbc_jPanelMaxQ = new GridBagConstraints();
			gbc_jPanelMaxQ.insets = new Insets(5, 5, 5, 5);
			gbc_jPanelMaxQ.gridx = 1;
			gbc_jPanelMaxQ.gridy = 0;
			panel.add(getJPanelMaxQ(), gbc_jPanelMaxQ);

			buttGlidingBox = new JRadioButton();
			buttGlidingBox.setText("Gliding Box");
			buttGlidingBox.setToolTipText("Gliding Box");
			buttGlidingBox.addActionListener(this);
			buttGlidingBox.setActionCommand("parameter");

			buttRasterBox = new JRadioButton();
			buttRasterBox.setText("Raster Box");
			buttRasterBox.setToolTipText("Raster Box");
			buttRasterBox.addActionListener(this);
			buttRasterBox.setActionCommand("parameter");
			buttRasterBox.setEnabled(true);

			GridBagConstraints gbc_jPanelMethod = new GridBagConstraints();
			gbc_jPanelMethod.gridwidth = 2;
			gbc_jPanelMethod.gridx = 0;
			gbc_jPanelMethod.gridy = 1;
			jPanelMethod = new JPanel();
			jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.X_AXIS));
			jPanelMethod.add(buttGlidingBox);
			jPanelMethod.add(buttRasterBox);
			panel.add(jPanelMethod, gbc_jPanelMethod);
		

			GridBagConstraints gbc_jPanelMaxEps = new GridBagConstraints();
			gbc_jPanelMaxEps.insets = new Insets(5, 5, 5, 5);
			gbc_jPanelMaxEps.gridx = 0;
			gbc_jPanelMaxEps.gridy = 2;
			panel.add(getJPanelMaxEps(), gbc_jPanelMaxEps);
			
			GridBagConstraints gbc_jPanelNumEps = new GridBagConstraints();
			gbc_jPanelNumEps.insets = new Insets(5, 5, 5, 5);
			gbc_jPanelNumEps.gridx = 1;
			gbc_jPanelNumEps.gridy = 2;
			panel.add(getJPanelNumEps(), gbc_jPanelNumEps);
			
			GridBagConstraints gbc_jPanelMethodEps = new GridBagConstraints();
			gbc_jPanelMethodEps.gridwidth = 2;
			gbc_jPanelMethodEps.gridx = 0;
			gbc_jPanelMethodEps.gridy = 3;
			panel.add(getJPanelMethodEps(), gbc_jPanelMethodEps);

			this.setButtonGroupMethod(); // Grouping of JRadioButtons
		}
		return panel;
	}
}// END
