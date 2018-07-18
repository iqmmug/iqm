package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_RegGrow.java
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpRegGrowDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2011 11
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_RegGrow extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7560653154681389069L;

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(OperatorGUI_RegGrow.class);

	private ParameterBlockIQM pb = null;

	private int greyRange = 10;

	private JPanel   jPanelGreyRange   = null;
	private JLabel   jLabelGreyRange   = null;
	private JSpinner jSpinnerGreyRange = null;

	private JRadioButton buttROICenter  = null;
	private JRadioButton buttROIMean    = null;
	private JPanel       jPanelModus    = null;
	private ButtonGroup  buttGroupModus = null;

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		pb.setParameter("GreyRange", ((Number) jSpinnerGreyRange.getValue()).intValue());

		if (buttROICenter.isSelected())  pb.setParameter("Modus", 0);
		if (buttROIMean.isSelected())    pb.setParameter("Modus", 1);

	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		jSpinnerGreyRange.removeChangeListener(this);
		jSpinnerGreyRange.setValue(pb.getIntParameter("GreyRange"));
		jSpinnerGreyRange.addChangeListener(this);

		if (pb.getIntParameter("Modus") == 0) buttROICenter.setSelected(true);
		if (pb.getIntParameter("Modus") == 1) buttROIMean.setSelected(true);
	}

	/**
	 * constructor
	 */
	public OperatorGUI_RegGrow() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpRegGrowDescriptor().getName());
		this.initialize();
		this.setTitle("Region Growing");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelGreyRange(), getGridBagConstraintsGreyRange());
		this.getOpGUIContent().add(getJPanelModus(),     getGridBagConstraintsModus());

		this.pack();
	}

	// ---------------------------------------------------------------------------------------------
	private GridBagConstraints getGridBagConstraintsGreyRange() {
		GridBagConstraints gridBagConstraintsGreyRange = new GridBagConstraints();
		gridBagConstraintsGreyRange.gridx = 0;
		gridBagConstraintsGreyRange.gridy = 0;
		gridBagConstraintsGreyRange.gridwidth = 1;// ?
		gridBagConstraintsGreyRange.insets = new Insets(10, 0, 5, 0); // top  left  bottom  right
		// gridBagConstraintsGreyRange.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsGreyRange;
	}

	private GridBagConstraints getGridBagConstraintsModus() {
		GridBagConstraints gridBagConstraintsModus = new GridBagConstraints();
		gridBagConstraintsModus.gridx = 1;
		gridBagConstraintsModus.gridy = 0;
		gridBagConstraintsModus.insets = new Insets(10, 0, 5, 0); // top left  bottom  right
		// gridBagConstraintsModus.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsModus;
	}


	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here it does nothing
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelGreyRange
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelGreyRange() {
		if (jPanelGreyRange == null) {
			jPanelGreyRange = new JPanel();
			//jPanelGreyRange.setLayout(new BorderLayout());
			//jPanelGreyRange.setLayout(new BoxLayout(jPanelGreyRange, BoxLayout.Y_AXIS));
			jPanelGreyRange.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelGreyRange.setBorder(new TitledBorder(null, "Grey value range", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jLabelGreyRange = new JLabel("Value:");
			jLabelGreyRange.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(greyRange, 2, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerGreyRange = new JSpinner(sModel);
			//jSpinnerGreyRange.setPreferredSize(new Dimension(60, 20));
			jSpinnerGreyRange.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerGreyRange .getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf .getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelGreyRange.add(jLabelGreyRange);
			jPanelGreyRange.add(jSpinnerGreyRange);
		}
		return jPanelGreyRange;
	}

	// -------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtROICenter() {
		// if (buttROICenter == null) {
		buttROICenter = new JRadioButton();
		buttROICenter.setText("ROI Center");
		buttROICenter.setToolTipText("ROI center seed grey values");
		buttROICenter.addActionListener(this);
		buttROICenter.setActionCommand("parameter");
		// }
		return buttROICenter;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtROIMean() {
		// if (buttROIMean == null) {
		buttROIMean = new JRadioButton();
		buttROIMean.setText("ROI Mean");
		buttROIMean.setToolTipText("ROI mean seed grey values");
		buttROIMean.addActionListener(this);
		buttROIMean.setActionCommand("parameter");
		// }
		return buttROIMean;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelModus() {
		// if (jPanelModus== null) {
		jPanelModus = new JPanel();
		//jPanelModus.setLayout(new BoxLayout(jPanelModus, BoxLayout.Y_AXIS));
		jPanelModus.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelModus.setBorder(new TitledBorder(null, "Initial grey value", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelModus.add(getJRadioButtonButtROICenter());
		jPanelModus.add(getJRadioButtonButtROIMean());
		this.setButtonGroupModus(); // Grouping of JRadioButtons
		// }
		return jPanelModus;
	}

	private void setButtonGroupModus() {
		buttGroupModus = new ButtonGroup();
		buttGroupModus.add(buttROICenter);
		buttGroupModus.add(buttROIMean);
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

		greyRange = ((Number) jSpinnerGreyRange.getValue()).intValue();

		this.updateParameterBlock();
		this.setParameterValuesToGUI();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
}// END
