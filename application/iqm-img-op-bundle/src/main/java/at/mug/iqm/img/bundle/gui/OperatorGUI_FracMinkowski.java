package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracMinkowski.java
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

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.JPanel;
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

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracMinkowskiDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 07
 */
public class OperatorGUI_FracMinkowski extends AbstractImageOperatorGUI
		implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1063317293988140497L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_FracMinkowski.class);

	private ParameterBlockIQM pb = null;

	private JPanel   jPanelDilEps   = null;
	private JLabel   jLabelDilEps   = null;
	private JSpinner jSpinnerDilEps = null;

	private JRadioButton buttSquare = null;
	private JRadioButton buttHoriz  = null;
	private JRadioButton buttVert   = null;
	private JPanel       jPanelShapeButtons    = null;
	private ButtonGroup  buttGroupShapeButtons = null;

	private JLabel       jLabelShapeButtons    = null;
	
	private JPanel       jPanelKernelShape  = null;

	private JPanel   jPanelRegression = null;
	private JPanel   jPanelRegStart   = null;
	private JLabel   jLabelRegStart   = null;
	private JSpinner jSpinnerRegStart = null;
	private JPanel   jPanelRegEnd     = null;
	private JLabel   jLabelRegEnd     = null;
	private JSpinner jSpinnerRegEnd   = null;

	private JCheckBox jCheckBoxShowPlot = null;
	private JCheckBox jCheckBoxDeleteExistingPlot = null;
	private JPanel plotOptionsPanel;
	private JPanel minkowskiOptionsPanel;

	/**
	 * constructor
	 */
	public OperatorGUI_FracMinkowski() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpFracMinkowskiDescriptor().getName());

		this.initialize();

		this.setTitle("Fractal Minkowski Dimension");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.minkowski.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());
		
	
		this.getOpGUIContent().add(getMinkowskiOptionsPanel(), getGridBagConstraints_MinkowskiOptionsPanel());
		this.getOpGUIContent().add(getJPanelRegression(),      getGridBagConstraints_Regression());
		this.getOpGUIContent().add(getPlotOptionsPanel(),      getGridBagConstraints_PlotOptionsPanel());

		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_MinkowskiOptionsPanel(){
		GridBagConstraints gbc_minkowskiOptionsPanel = new GridBagConstraints();
		gbc_minkowskiOptionsPanel.fill = GridBagConstraints.BOTH;
		gbc_minkowskiOptionsPanel.gridx = 0;
		gbc_minkowskiOptionsPanel.gridy = 0;
		gbc_minkowskiOptionsPanel.insets = new Insets(5, 0, 0, 0); // top, left bottom right
		return gbc_minkowskiOptionsPanel;
	}
	
	private GridBagConstraints getGridBagConstraints_Regression() {
		GridBagConstraints gridBagConstraintsRegression = new GridBagConstraints();
		gridBagConstraintsRegression.gridx = 0;
		gridBagConstraintsRegression.gridy = 1;
		gridBagConstraintsRegression.fill = GridBagConstraints.HORIZONTAL;
		// gridBagConstraintsRegression.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRegression;
	}

	private GridBagConstraints getGridBagConstraints_PlotOptionsPanel(){
		GridBagConstraints gbc_plotOptionsPanel = new GridBagConstraints();
		gbc_plotOptionsPanel.gridx = 0;
		gbc_plotOptionsPanel.gridy = 2;
		gbc_plotOptionsPanel.fill = GridBagConstraints.BOTH;
		return gbc_plotOptionsPanel;
	}
	
	
	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttSquare.isSelected()) pb.setParameter("KernelShape", 0);
		if (buttHoriz.isSelected())  pb.setParameter("KernelShape", 1);
		if (buttVert.isSelected())   pb.setParameter("KernelShape", 2);

		pb.setParameter("DilEps",   ((Number) jSpinnerDilEps.getValue()).intValue());
		pb.setParameter("RegStart", ((Number) jSpinnerRegStart.getValue()).intValue());
		pb.setParameter("RegEnd",   ((Number) jSpinnerRegEnd.getValue()).intValue());

		if (jCheckBoxShowPlot.isSelected())  pb.setParameter("ShowPlot", 1);
		if (!jCheckBoxShowPlot.isSelected()) pb.setParameter("ShowPlot", 0);
		if (jCheckBoxDeleteExistingPlot.isSelected()) pb.setParameter("DeleteExistingPlot", 1);
		if (!jCheckBoxDeleteExistingPlot.isSelected())pb.setParameter("DeleteExistingPlot", 0);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		jSpinnerDilEps.removeChangeListener(this);
		jSpinnerDilEps.setValue(pb.getIntParameter("DilEps"));
		jSpinnerDilEps.addChangeListener(this);

		if (pb.getIntParameter("KernelShape") == 0) buttSquare.setSelected(true);
		if (pb.getIntParameter("KernelShape") == 1) buttHoriz.setSelected(true);
		if (pb.getIntParameter("KernelShape") == 2) buttVert.setSelected(true);

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
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	/**
	 * This method initializes jJPanelDilEps
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDilEps() {
		if (jPanelDilEps == null) {
			jPanelDilEps = new JPanel();
			jPanelDilEps.setBorder(new EmptyBorder(5, 5, 5, 5));
			BorderLayout bl_jPanelDilEps = new BorderLayout();
			jPanelDilEps.setLayout(bl_jPanelDilEps);
			jLabelDilEps = new JLabel("Largest dilation step [pixel]: ");
			// jLabelDilEps.setPreferredSize(new Dimension(70, 22));
			jLabelDilEps.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(20, 3, 100, 1); // init,  min,  max, step
			jSpinnerDilEps = new JSpinner(sModel);
			// jSpinnerDilEps = new JSpinner();
			jSpinnerDilEps.setPreferredSize(new Dimension(60, 20));
			jSpinnerDilEps.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerDilEps.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelDilEps.add(jLabelDilEps,   BorderLayout.WEST);
			jPanelDilEps.add(jSpinnerDilEps, BorderLayout.CENTER);
		}
		return jPanelDilEps;
	}

	// -------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Square
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSquare() {
		if (buttSquare == null) {
			buttSquare = new JRadioButton();
			buttSquare.setText("Square");
			buttSquare.setToolTipText("Square shaped kernel");
			buttSquare.addActionListener(this);
			buttSquare.setActionCommand("parameter");
		}
		return buttSquare;
	}

	/**
	 * This method initializes the Option: Horiz
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonHoriz() {
		if (buttHoriz == null) {
			buttHoriz = new JRadioButton();
			buttHoriz.setText("Horizontal");
			// buttHoriz.setPreferredSize(new Dimension(90,10));
			buttHoriz.setToolTipText("Horizontal line shaped kernel");
			buttHoriz.addActionListener(this);
			buttHoriz.setActionCommand("parameter");
		}
		return buttHoriz;
	}

	/**
	 * This method initializes the Option: Vert
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonVert() {
		if (buttVert == null) {
			buttVert = new JRadioButton();
			buttVert.setText("Vertical");
			// buttVert.setPreferredSize(new Dimension(85,10));
			buttVert.setToolTipText("Vertical line shaped kernel");
			buttVert.addActionListener(this);
			buttVert.setActionCommand("parameter");
			// buttVert.setEnabled(false);
		}
		return buttVert;
	}

	/**
	 * This method initializes jJPanelShapeButtons
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelShapeButtons() {
		// if (jPanelShapeButtons == null) {
		jPanelShapeButtons = new JPanel();
		jPanelShapeButtons.setLayout(new BoxLayout(jPanelShapeButtons, BoxLayout.Y_AXIS));
		jPanelShapeButtons.add(getJRadioButtonSquare());
		jPanelShapeButtons.add(getJRadioButtonHoriz());
		jPanelShapeButtons.add(getJRadioButtonVert());

		// jPanelShapeButtons.addSeparator();
		this.setButtonGroupShapeButtons(); // Grouping of JRadioButtons
		// }
		return jPanelShapeButtons;
	}

	private void setButtonGroupShapeButtons() {
		// if (ButtonGroup buttGroupShapeButtons == null) {
		buttGroupShapeButtons = new ButtonGroup();
		buttGroupShapeButtons.add(buttSquare);
		buttGroupShapeButtons.add(buttHoriz);
		buttGroupShapeButtons.add(buttVert);
	}

	/**
	 * This method initializes jPanelMethod
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernelShape() {
		if (jPanelKernelShape == null) {
			jPanelKernelShape = new JPanel();
			jPanelKernelShape.setBorder(new EmptyBorder(5, 5, 5, 5));
			jPanelKernelShape.setLayout(new BorderLayout());
			// jPanelKernelShape.setPreferredSize(new Dimension(290, 20));
			jLabelShapeButtons = new JLabel("Kernel shape: ");
			// jLabelShapeButtons.setPreferredSize(new Dimension(60,10));
			jPanelKernelShape.add(jLabelShapeButtons,      BorderLayout.WEST);
			jPanelKernelShape.add(getJPanelShapeButtons(), BorderLayout.CENTER);
		}
		return jPanelKernelShape;
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
			jPanelRegression.setBorder(new TitledBorder(null, "Regression", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
			// jLabelRegStart.setPreferredSize(new Dimension(40, 22));
			jLabelRegStart.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, 99, 1); // init, min,  max, step
			jSpinnerRegStart = new JSpinner(sModel);
			// jSpinnerRegStart = new JSpinner();
			jSpinnerRegStart.setPreferredSize(new Dimension(60, 20));
			jSpinnerRegStart.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegStart.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(false);
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
			// jLabelRegEnd.setPreferredSize(new Dimension(40, 22));
			jLabelRegEnd.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(20, 2, 100, 1); // init, min, max,step
			jSpinnerRegEnd = new JSpinner(sModel);
			// jSpinnerRegEnd = new JSpinner();
			jSpinnerRegEnd.setPreferredSize(new Dimension(60, 20));
			jSpinnerRegEnd.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegEnd.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(false);
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
		int dilRadius = ((Number) jSpinnerDilEps.getValue()).intValue();
		int regStart = ((Number) jSpinnerRegStart.getValue()).intValue();
		int regEnd = ((Number) jSpinnerRegEnd.getValue()).intValue();

		jSpinnerDilEps.removeChangeListener(this);
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);

		if (jSpinnerDilEps == e.getSource()) {
			// if(regEnd > dilRadius){
			jSpinnerRegEnd.setValue(dilRadius);
			regEnd = dilRadius;
			// }
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 2);
				regStart = regEnd - 2;
			}
		}
		if (jSpinnerRegEnd == e.getSource()) {
			if (regEnd > dilRadius) {
				jSpinnerRegEnd.setValue(dilRadius);
				regEnd = dilRadius;
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

		jSpinnerDilEps.addChangeListener(this);
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
			plotOptionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			plotOptionsPanel.add(getJCheckBoxShowPlot());
			plotOptionsPanel.add(getJCheckBoxDeleteExistingPlot());
		}
		return plotOptionsPanel;
	}

	private JPanel getMinkowskiOptionsPanel() {
		if (minkowskiOptionsPanel == null) {
			minkowskiOptionsPanel = new JPanel();
			minkowskiOptionsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Minkowski options",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			minkowskiOptionsPanel.setLayout(new BorderLayout(0, 0));
			minkowskiOptionsPanel.add(getJPanelDilEps(),      BorderLayout.NORTH);
			minkowskiOptionsPanel.add(getJPanelKernelShape(), BorderLayout.CENTER);
		}
		return minkowskiOptionsPanel;
	}
}// END
