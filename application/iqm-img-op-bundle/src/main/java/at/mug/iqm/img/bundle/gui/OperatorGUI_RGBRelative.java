package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_RGBRelative.java
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpRGBRelativeDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 04
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_RGBRelative extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7553460701806759973L;

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(OperatorGUI_RGBRelative.class);

	private ParameterBlockIQM pb = null; // @jve:decl-index=0:

	private int ratio; // Slider Values

	private JPanel   jSpinnerPanelRatio = null;
	private JSpinner jSpinnerRatio = null;
	private JLabel   jLabelRatio;

	private JRadioButton buttChR           = null;
	private JRadioButton buttChG           = null;
	private JRadioButton buttChB           = null;
	private JRadioButton buttChRank        = null;
	private JLabel       jLabelText        = null;
	private JPanel       jPanelChSelect    = null;
	private ButtonGroup  buttGroupChSelect = null;

	private JCheckBox jCheckBoxRankLeftRG = null;
	private JCheckBox jCheckBoxRankLeftRB = null;
	private JCheckBox jCheckBoxRankLeftGR = null;
	private JCheckBox jCheckBoxRankLeftGB = null;
	private JCheckBox jCheckBoxRankLeftBR = null;
	private JCheckBox jCheckBoxRankLeftBG = null;

	private JCheckBox jCheckBoxAND = null;

	private JCheckBox jCheckBoxRankRightRG = null;
	private JCheckBox jCheckBoxRankRightRB = null;
	private JCheckBox jCheckBoxRankRightGR = null;
	private JCheckBox jCheckBoxRankRightGB = null;
	private JCheckBox jCheckBoxRankRightBR = null;
	private JCheckBox jCheckBoxRankRightBG = null;

	private JCheckBox jCheckBoxBinarize = null;

	/**
	 * constructor
	 */
	public OperatorGUI_RGBRelative() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpRGBRelativeDescriptor().getName());

		this.initialize();

		this.setTitle("RGB Relative");

		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelChSelect(),      getGridBagConstraintsButtonGroupChSelect());
		this.getOpGUIContent().add(getJSpinnerPanelRatio(),  getGridBagConstraintsRatio());
		this.getOpGUIContent().add(getJCheckBoxRankLeftRG(), getGridBagConstraintsRankLeftRG());
		this.getOpGUIContent().add(getJCheckBoxRankLeftRB(), getGridBagConstraintsRankLeftRB());
		this.getOpGUIContent().add(getJCheckBoxRankLeftGR(), getGridBagConstraintsRankLeftGR());
		this.getOpGUIContent().add(getJCheckBoxRankLeftGB(), getGridBagConstraintsRankLeftGB());
		this.getOpGUIContent().add(getJCheckBoxRankLeftBR(), getGridBagConstraintsRankLeftBR());
		this.getOpGUIContent().add(getJCheckBoxRankLeftBG(), getGridBagConstraintsRankLeftBG());
		this.getOpGUIContent().add(getJCheckBoxAND(),        getGridBagConstraintsAND());
		this.getOpGUIContent().add(getJCheckBoxRankRightRG(),getGridBagConstraintsRankRightRG());
		this.getOpGUIContent().add(getJCheckBoxRankRightRB(),getGridBagConstraintsRankRightRB());
		this.getOpGUIContent().add(getJCheckBoxRankRightGR(),getGridBagConstraintsRankRightGR());
		this.getOpGUIContent().add(getJCheckBoxRankRightGB(),getGridBagConstraintsRankRightGB());
		this.getOpGUIContent().add(getJCheckBoxRankRightBR(),getGridBagConstraintsRankRightBR());
		this.getOpGUIContent().add(getJCheckBoxRankRightBG(),getGridBagConstraintsRankRightBG());
		this.getOpGUIContent().add(getJCheckBoxBinarize(),   getGridBagConstraintsBinarize());

		this.setRankCheckBoxesLeftEnabled(false);
		this.setRankCheckBoxesRightEnabled(false);

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttChR.isSelected())    pb.setParameter("Method", 0);
		if (buttChG.isSelected())    pb.setParameter("Method", 1);
		if (buttChB.isSelected())    pb.setParameter("Method", 2);
		if (buttChRank.isSelected()) pb.setParameter("Method", 3);

		pb.setParameter("Ratio", ratio);

		if (jCheckBoxRankLeftRG.isSelected())  pb.setParameter("RankLeftRG", 1);
		if (!jCheckBoxRankLeftRG.isSelected()) pb.setParameter("RankLeftRG", 0);
		if (jCheckBoxRankLeftRB.isSelected())  pb.setParameter("RankLeftRB", 1);
		if (!jCheckBoxRankLeftRB.isSelected()) pb.setParameter("RankLeftRB", 0);
		if (jCheckBoxRankLeftGR.isSelected())  pb.setParameter("RankLeftGR", 1);
		if (!jCheckBoxRankLeftGR.isSelected()) pb.setParameter("RankLeftGR", 0);
		if (jCheckBoxRankLeftGB.isSelected())  pb.setParameter("RankLeftGB", 1);
		if (!jCheckBoxRankLeftGB.isSelected()) pb.setParameter("RankLeftGB", 0);
		if (jCheckBoxRankLeftBR.isSelected())  pb.setParameter("RankLeftBR", 1);
		if (!jCheckBoxRankLeftBR.isSelected()) pb.setParameter("RankLeftBR", 0);
		if (jCheckBoxRankLeftBG.isSelected())  pb.setParameter("RankLeftBG", 1);
		if (!jCheckBoxRankLeftBG.isSelected()) pb.setParameter("RankLeftBG", 0);

		if (jCheckBoxAND.isSelected())  pb.setParameter("AND", 1);
		if (!jCheckBoxAND.isSelected()) pb.setParameter("AND", 0);

		if (jCheckBoxRankRightRG.isSelected())  pb.setParameter("RankRightRG", 1);
		if (!jCheckBoxRankRightRG.isSelected()) pb.setParameter("RankRightRG", 0);
		if (jCheckBoxRankRightRB.isSelected())  pb.setParameter("RankRightRB", 1);
		if (!jCheckBoxRankRightRB.isSelected()) pb.setParameter("RankRightRB", 0);
		if (jCheckBoxRankRightGR.isSelected())  pb.setParameter("RankRightGR", 1);
		if (!jCheckBoxRankRightGR.isSelected()) pb.setParameter("RankRightGR", 0);
		if (jCheckBoxRankRightGB.isSelected())  pb.setParameter("RankRightGB", 1);
		if (!jCheckBoxRankRightGB.isSelected()) pb.setParameter("RankRightGB", 0);
		if (jCheckBoxRankRightBR.isSelected())  pb.setParameter("RankRightBR", 1);
		if (!jCheckBoxRankRightBR.isSelected()) pb.setParameter("RankRightBR", 0);
		if (jCheckBoxRankRightBG.isSelected())  pb.setParameter("RankRightBG", 1);
		if (!jCheckBoxRankRightBG.isSelected()) pb.setParameter("RankRightBG", 0);

		if (jCheckBoxBinarize.isSelected())  pb.setParameter("Binarize", 1);
		if (!jCheckBoxBinarize.isSelected()) pb.setParameter("Binarize", 0);
	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter(0) == 0) buttChR.setSelected(true);
		if (pb.getIntParameter(0) == 1) buttChG.setSelected(true);
		if (pb.getIntParameter(0) == 2) buttChB.setSelected(true);
		if (pb.getIntParameter(0) == 3) buttChRank.setSelected(true);

		if (buttChR.isSelected())    jLabelText.setText("Ratio = R/(R+G+B)");
		if (buttChG.isSelected())    jLabelText.setText("Ratio = G/(R+G+B)");
		if (buttChB.isSelected())    jLabelText.setText("Ratio = B/(R+G+B)");
		if (buttChRank.isSelected()) jLabelText.setText("Rank");

		ratio = pb.getIntParameter("Ratio");
		jSpinnerRatio.removeChangeListener(this);
		jSpinnerRatio.setValue(ratio);
		jSpinnerRatio.addChangeListener(this);
		jLabelRatio.setText("Ratio[%]: ");
		if (pb.getIntParameter("RankLeftRG") == 0) jCheckBoxRankLeftRG.setSelected(false);
		if (pb.getIntParameter("RankLeftRG") == 1) jCheckBoxRankLeftRG.setSelected(true);
		if (pb.getIntParameter("RankLeftRB") == 0) jCheckBoxRankLeftRB.setSelected(false);
		if (pb.getIntParameter("RankLeftRB") == 1) jCheckBoxRankLeftRB.setSelected(true);
		if (pb.getIntParameter("RankLeftGR") == 0) jCheckBoxRankLeftGR.setSelected(false);
		if (pb.getIntParameter("RankLeftGR") == 1) jCheckBoxRankLeftGR.setSelected(true);
		if (pb.getIntParameter("RankLeftGB") == 0) jCheckBoxRankLeftGB.setSelected(false);
		if (pb.getIntParameter("RankLeftGB") == 1) jCheckBoxRankLeftGB.setSelected(true);
		if (pb.getIntParameter("RankLeftBR") == 0) jCheckBoxRankLeftBR.setSelected(false);
		if (pb.getIntParameter("RankLeftBR") == 1) jCheckBoxRankLeftBR.setSelected(true);
		if (pb.getIntParameter("RankLeftBG") == 0) jCheckBoxRankLeftBG.setSelected(false);
		if (pb.getIntParameter("RankLeftBG") == 1) jCheckBoxRankLeftBG.setSelected(true);

		if (pb.getIntParameter("AND") == 0) jCheckBoxAND.setSelected(false);
		if (pb.getIntParameter("AND") == 1) jCheckBoxAND.setSelected(true);

		if (pb.getIntParameter("RankRightRG") == 0) jCheckBoxRankRightRG.setSelected(false);
		if (pb.getIntParameter("RankRightRG") == 1) jCheckBoxRankRightRG.setSelected(true);
		if (pb.getIntParameter("RankRightRB") == 0) jCheckBoxRankRightRB.setSelected(false);
		if (pb.getIntParameter("RankRightRB") == 1) jCheckBoxRankRightRB.setSelected(true);
		if (pb.getIntParameter("RankRightGR") == 0) jCheckBoxRankRightGR.setSelected(false);
		if (pb.getIntParameter("RankRightGR") == 1) jCheckBoxRankRightGR.setSelected(true);
		if (pb.getIntParameter("RankRightGB") == 0) jCheckBoxRankRightGB.setSelected(false);
		if (pb.getIntParameter("RankRightGB") == 1) jCheckBoxRankRightGB.setSelected(true);
		if (pb.getIntParameter("RankRightBR") == 0) jCheckBoxRankRightBR.setSelected(false);
		if (pb.getIntParameter("RankRightBR") == 1) jCheckBoxRankRightBR.setSelected(true);
		if (pb.getIntParameter("RankRightBG") == 0) jCheckBoxRankRightBG.setSelected(false);
		if (pb.getIntParameter("RankRightBG") == 1) jCheckBoxRankRightBG.setSelected(true);

		if (pb.getIntParameter("Binarize") == 0) jCheckBoxBinarize.setSelected(false);
		if (pb.getIntParameter("Binarize") == 1) jCheckBoxBinarize.setSelected(true);

	}

	// ------------------------------------------------------------------------------
	private GridBagConstraints getGridBagConstraintsButtonGroupChSelect() {
		GridBagConstraints gridBagConstraintsButtonGroupChSelect = new GridBagConstraints();
		gridBagConstraintsButtonGroupChSelect.gridx = 0;
		gridBagConstraintsButtonGroupChSelect.gridy = 0;
		gridBagConstraintsButtonGroupChSelect.gridwidth = 3;
		gridBagConstraintsButtonGroupChSelect.insets = new Insets(10, 0, 0, 0);
		gridBagConstraintsButtonGroupChSelect.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsButtonGroupChSelect;
	}

	private GridBagConstraints getGridBagConstraintsRatio() {
		GridBagConstraints gridBagConstraintsRatio = new GridBagConstraints();
		gridBagConstraintsRatio.gridx = 0;
		gridBagConstraintsRatio.gridy = 1;
		gridBagConstraintsRatio.gridwidth = 3;
		gridBagConstraintsRatio.insets = new Insets(15, 10, 5, 10); // top left bottom right
		// gridBagConstraintsRatio.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRatio;
	}

	// ---------------------------------------------------------------------------------------------------------
	private GridBagConstraints getGridBagConstraintsRankLeftRG() {
		GridBagConstraints gridBagConstraintsRankLeftRG = new GridBagConstraints();
		gridBagConstraintsRankLeftRG.gridx = 0;
		gridBagConstraintsRankLeftRG.gridy = 2;
		gridBagConstraintsRankLeftRG.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankLeftRG.insets = new Insets(15, 15, 5, 5); // top left bottom right																	
		// gridBagConstraintsRankLeftRG.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankLeftRG;
	}

	private GridBagConstraints getGridBagConstraintsRankLeftRB() {
		GridBagConstraints gridBagConstraintsRankLeftRB = new GridBagConstraints();
		gridBagConstraintsRankLeftRB.gridx = 0;
		gridBagConstraintsRankLeftRB.gridy = 3;
		gridBagConstraintsRankLeftRB.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankLeftRB.insets = new Insets(5, 15, 5, 5); // top left bottom right
		// gridBagConstraintsRankLeftRB.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankLeftRB;
	}

	private GridBagConstraints getGridBagConstraintsRankLeftGR() {
		GridBagConstraints gridBagConstraintsRankLeftGR = new GridBagConstraints();
		gridBagConstraintsRankLeftGR.gridx = 0;
		gridBagConstraintsRankLeftGR.gridy = 4;
		gridBagConstraintsRankLeftGR.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankLeftGR.insets = new Insets(5, 15, 5, 5); // top left bottom right
		// gridBagConstraintsRankLeftGR.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankLeftGR;
	}

	private GridBagConstraints getGridBagConstraintsRankLeftGB() {
		GridBagConstraints gridBagConstraintsRankLeftGB = new GridBagConstraints();
		gridBagConstraintsRankLeftGB.gridx = 0;
		gridBagConstraintsRankLeftGB.gridy = 5;
		gridBagConstraintsRankLeftGB.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankLeftGB.insets = new Insets(5, 15, 5, 5); // top left bottom right
		// gridBagConstraintsRankLeftGB.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankLeftGB;
	}

	private GridBagConstraints getGridBagConstraintsRankLeftBR() {
		GridBagConstraints gridBagConstraintsRankLeftBR = new GridBagConstraints();
		gridBagConstraintsRankLeftBR.gridx = 0;
		gridBagConstraintsRankLeftBR.gridy = 6;
		gridBagConstraintsRankLeftBR.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankLeftBR.insets = new Insets(5, 15, 5, 5); // top left bottom right
		// gridBagConstraintsRankLeftBR.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankLeftBR;
	}

	private GridBagConstraints getGridBagConstraintsRankLeftBG() {
		GridBagConstraints gridBagConstraintsRankLeftBG = new GridBagConstraints();
		gridBagConstraintsRankLeftBG.gridx = 0;
		gridBagConstraintsRankLeftBG.gridy = 7;
		gridBagConstraintsRankLeftBG.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankLeftBG.insets = new Insets(5, 15, 5, 5); // top left bottom right
		// gridBagConstraintsRankLeftBG.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankLeftBG;
	}

	// ---------------------------------------------------------------------------------------------------------
	private GridBagConstraints getGridBagConstraintsAND() {
		GridBagConstraints gridBagConstraintsAND = new GridBagConstraints();
		gridBagConstraintsAND.gridx = 1;
		gridBagConstraintsAND.gridy = 4;
		gridBagConstraintsAND.anchor = GridBagConstraints.SOUTHWEST;
		gridBagConstraintsAND.insets = new Insets(0, 0, 5, 5); // top left bottom right
		// gridBagConstraintsAND.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsAND;
	}

	// ---------------------------------------------------------------------------------------------------------
	private GridBagConstraints getGridBagConstraintsRankRightRG() {
		GridBagConstraints gridBagConstraintsRankRightRG = new GridBagConstraints();
		gridBagConstraintsRankRightRG.gridx = 2;
		gridBagConstraintsRankRightRG.gridy = 2;
		gridBagConstraintsRankRightRG.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankRightRG.insets = new Insets(15, 0, 5, 0); // top left bottom right
		// gridBagConstraintsRankRightRG.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankRightRG;
	}

	private GridBagConstraints getGridBagConstraintsRankRightRB() {
		GridBagConstraints gridBagConstraintsRankRightRB = new GridBagConstraints();
		gridBagConstraintsRankRightRB.gridx = 2;
		gridBagConstraintsRankRightRB.gridy = 3;
		gridBagConstraintsRankRightRB.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankRightRB.insets = new Insets(5, 0, 5, 0); // top left bottom right
		// gridBagConstraintsRankRightRB.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankRightRB;
	}

	private GridBagConstraints getGridBagConstraintsRankRightGR() {
		GridBagConstraints gridBagConstraintsRankRightGR = new GridBagConstraints();
		gridBagConstraintsRankRightGR.gridx = 2;
		gridBagConstraintsRankRightGR.gridy = 4;
		gridBagConstraintsRankRightGR.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankRightGR.insets = new Insets(5, 0, 5, 0); // top left bottom right
		// gridBagConstraintsRankRightGR.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankRightGR;
	}

	private GridBagConstraints getGridBagConstraintsRankRightGB() {
		GridBagConstraints gridBagConstraintsRankRightGB = new GridBagConstraints();
		gridBagConstraintsRankRightGB.gridx = 2;
		gridBagConstraintsRankRightGB.gridy = 5;
		gridBagConstraintsRankRightGB.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankRightGB.insets = new Insets(5, 0, 5, 0); // top left bottom right
		// gridBagConstraintsRankRightGB.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankRightGB;
	}

	private GridBagConstraints getGridBagConstraintsRankRightBR() {
		GridBagConstraints gridBagConstraintsRankRightBR = new GridBagConstraints();
		gridBagConstraintsRankRightBR.gridx = 2;
		gridBagConstraintsRankRightBR.gridy = 6;
		gridBagConstraintsRankRightBR.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankRightBR.insets = new Insets(5, 0, 5, 0); // top left bottom right
		// gridBagConstraintsRankRightBR.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankRightBR;
	}

	private GridBagConstraints getGridBagConstraintsRankRightBG() {
		GridBagConstraints gridBagConstraintsRankRightBG = new GridBagConstraints();
		gridBagConstraintsRankRightBG.gridx = 2;
		gridBagConstraintsRankRightBG.gridy = 7;
		gridBagConstraintsRankRightBG.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsRankRightBG.insets = new Insets(5, 0, 5, 0); // top left bottom right
		// gridBagConstraintsRankRightBG.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRankRightBG;
	}

	// --------------------------------------------------------------------------------------------
	private GridBagConstraints getGridBagConstraintsBinarize() {
		GridBagConstraints gridBagConstraintsBinarize = new GridBagConstraints();
		gridBagConstraintsBinarize.gridx = 2;
		gridBagConstraintsBinarize.gridy = 8;
		gridBagConstraintsBinarize.anchor = GridBagConstraints.LINE_START;
		gridBagConstraintsBinarize.insets = new Insets(15, 0, 10, 0); // top left bottom right
		// gridBagConstraintsBinarize.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsBinarize;
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method updates the GUI if needed This method overrides OperatorGUI
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	/**
	 * This method initializes the Option: Extract red channel
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtChR() {
		buttChR = new JRadioButton();
		buttChR.setText("R");
		buttChR.setToolTipText("calculates ratio for red channel");
		buttChR.addActionListener(this);
		buttChR.setActionCommand("parameter");
		return buttChR;
	}

	/**
	 * This method initializes the Option: Extract the green channel
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtChG() {
		buttChG = new JRadioButton();
		buttChG.setText("G");
		buttChG.setToolTipText("calculates ratio for green channel");
		buttChG.addActionListener(this);
		buttChG.setActionCommand("parameter");
		return buttChG;
	}

	/**
	 * This method initializes the Option: Extract the blue channel
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtChB() {
		buttChB = new JRadioButton();
		buttChB.setText("B");
		buttChB.setToolTipText("calculates ratio for blue channel");
		buttChB.addActionListener(this);
		buttChB.setActionCommand("parameter");
		return buttChB;
	}

	/**
	 * This method initializes the Option: Extract the blue channel
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtChRank() {
		buttChRank = new JRadioButton();
		buttChRank.setText("Rank");
		buttChRank.setToolTipText("calculates rank of RGB channels");
		buttChRank.addActionListener(this);
		buttChRank.setActionCommand("parameter");
		return buttChRank;
	}

	/**
	 * This method initializes jJPanelBar
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelChSelect() {
		jPanelChSelect = new JPanel();
		jPanelChSelect.setLayout(new BoxLayout(jPanelChSelect, BoxLayout.X_AXIS));
		//jPanelChSelect.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelChSelect.setBorder(new TitledBorder(null, "Channel selection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		JPanel pnl_Buttons = new JPanel();
		pnl_Buttons.setLayout(new BoxLayout(pnl_Buttons, BoxLayout.Y_AXIS));
		pnl_Buttons.add(getJRadioButtonButtChR());
		pnl_Buttons.add(getJRadioButtonButtChG());
		pnl_Buttons.add(getJRadioButtonButtChB());
		pnl_Buttons.add(getJRadioButtonButtChRank());
		jPanelChSelect.add(pnl_Buttons);
		jPanelChSelect.add(getJLabelText());
		this.setButtonGroupChSelect(); // Grouping of JRadioButtons
		return jPanelChSelect;
	}

	private void setButtonGroupChSelect() {
		buttGroupChSelect = new ButtonGroup();
		buttGroupChSelect.add(buttChR);
		buttGroupChSelect.add(buttChG);
		buttGroupChSelect.add(buttChB);
		buttGroupChSelect.add(buttChRank);
	}

	/**
	 * This method initializes jLabelText
	 * 
	 * @return javax.swing.JLabel
	 */
	private JLabel getJLabelText() {
		jLabelText = new JLabel();
		jLabelText.setText("<Equation>");
		return jLabelText;
	}
	/**
	 * This method initializes jJPanelRatio
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelRatio() {
		if (jSpinnerPanelRatio == null) {
			jSpinnerPanelRatio = new JPanel();
			jSpinnerPanelRatio.setLayout(new BorderLayout());
			jLabelRatio = new JLabel("Ratio ");
			// jLabelRatio.setPreferredSize(new Dimension(70, 20));
			jLabelRatio.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(50, 0, 100, 1); // init, min, max, step
			jSpinnerRatio = new JSpinner(sModel);
			jSpinnerRatio.setToolTipText("ratio (selected plane)/(sum of planes)");
			//jSpinnerRatio.setPreferredSize(new Dimension(60, 20));
			jSpinnerRatio.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRatio.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
										
			jSpinnerPanelRatio.add(jLabelRatio, BorderLayout.WEST);
			jSpinnerPanelRatio.add(jSpinnerRatio, BorderLayout.CENTER);
		}
		return jSpinnerPanelRatio;
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxRankLeftRG
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankLeftRG() {
		jCheckBoxRankLeftRG = new JCheckBox();
		jCheckBoxRankLeftRG.setText("R > G");
		jCheckBoxRankLeftRG.addActionListener(this);
		jCheckBoxRankLeftRG.setActionCommand("parameter");
		return jCheckBoxRankLeftRG;
	}

	/**
	 * This method initializes jCheckBoxRankLeftRB
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankLeftRB() {
		jCheckBoxRankLeftRB = new JCheckBox();
		jCheckBoxRankLeftRB.setText("R > B");
		jCheckBoxRankLeftRB.addActionListener(this);
		jCheckBoxRankLeftRB.setActionCommand("parameter");
		return jCheckBoxRankLeftRB;
	}

	/**
	 * This method initializes jCheckBoxRankLeftGR
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankLeftGR() {
		jCheckBoxRankLeftGR = new JCheckBox();
		jCheckBoxRankLeftGR.setText("G > R");
		jCheckBoxRankLeftGR.addActionListener(this);
		jCheckBoxRankLeftGR.setActionCommand("parameter");
		return jCheckBoxRankLeftGR;
	}

	/**
	 * This method initializes jCheckBoxRankLeftGB
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankLeftGB() {
		jCheckBoxRankLeftGB = new JCheckBox();
		jCheckBoxRankLeftGB.setText("G > B");
		jCheckBoxRankLeftGB.addActionListener(this);
		jCheckBoxRankLeftGB.setActionCommand("parameter");
		return jCheckBoxRankLeftGB;
	}

	/**
	 * This method initializes jCheckBoxRankLeftBR
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankLeftBR() {
		jCheckBoxRankLeftBR = new JCheckBox();
		jCheckBoxRankLeftBR.setText("B > R");
		jCheckBoxRankLeftBR.addActionListener(this);
		jCheckBoxRankLeftBR.setActionCommand("parameter");
		return jCheckBoxRankLeftBR;
	}

	/**
	 * This method initializes jCheckBoxRankLeftBG
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankLeftBG() {
		jCheckBoxRankLeftBG = new JCheckBox();
		jCheckBoxRankLeftBG.setText("B > G");
		jCheckBoxRankLeftBG.addActionListener(this);
		jCheckBoxRankLeftBG.setActionCommand("parameter");
		return jCheckBoxRankLeftBG;
	}

	// ----------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxAND
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxAND() {
		jCheckBoxAND = new JCheckBox();
		jCheckBoxAND.setText("AND");
		jCheckBoxAND.addActionListener(this);
		jCheckBoxAND.setActionCommand("parameter");
		return jCheckBoxAND;
	}

	// ----------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxRankRightRG
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankRightRG() {
		jCheckBoxRankRightRG = new JCheckBox();
		jCheckBoxRankRightRG.setText("R > G");
		jCheckBoxRankRightRG.addActionListener(this);
		jCheckBoxRankRightRG.setActionCommand("parameter");
		return jCheckBoxRankRightRG;
	}

	/**
	 * This method initializes jCheckBoxRankRightRB
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankRightRB() {
		jCheckBoxRankRightRB = new JCheckBox();
		jCheckBoxRankRightRB.setText("R > B");
		jCheckBoxRankRightRB.addActionListener(this);
		jCheckBoxRankRightRB.setActionCommand("parameter");
		return jCheckBoxRankRightRB;
	}

	/**
	 * This method initializes jCheckBoxRankRightGR
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankRightGR() {
		jCheckBoxRankRightGR = new JCheckBox();
		jCheckBoxRankRightGR.setText("G > R");
		jCheckBoxRankRightGR.addActionListener(this);
		jCheckBoxRankRightGR.setActionCommand("parameter");
		return jCheckBoxRankRightGR;
	}

	/**
	 * This method initializes jCheckBoxRankRightGB
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankRightGB() {
		jCheckBoxRankRightGB = new JCheckBox();
		jCheckBoxRankRightGB.setText("G > B");
		jCheckBoxRankRightGB.addActionListener(this);
		jCheckBoxRankRightGB.setActionCommand("parameter");
		return jCheckBoxRankRightGB;
	}

	/**
	 * This method initializes jCheckBoxRankRightBR
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankRightBR() {
		jCheckBoxRankRightBR = new JCheckBox();
		jCheckBoxRankRightBR.setText("B > R");
		jCheckBoxRankRightBR.addActionListener(this);
		jCheckBoxRankRightBR.setActionCommand("parameter");
		return jCheckBoxRankRightBR;
	}

	/**
	 * This method initializes jCheckBoxRankRightBG
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRankRightBG() {
		jCheckBoxRankRightBG = new JCheckBox();
		jCheckBoxRankRightBG.setText("B > G");
		jCheckBoxRankRightBG.addActionListener(this);
		jCheckBoxRankRightBG.setActionCommand("parameter");
		return jCheckBoxRankRightBG;
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxBinarize
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxBinarize() {
		jCheckBoxBinarize = new JCheckBox();
		jCheckBoxBinarize.setText("Binarize");
		jCheckBoxBinarize.addActionListener(this);
		jCheckBoxBinarize.setActionCommand("parameter");
		return jCheckBoxBinarize;
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * This method sets the visibility of the rank checkboxes
	 * 
	 * @param b
	 */
	private void setRankCheckBoxesLeftEnabled(boolean b) {
		jCheckBoxRankLeftRG.setEnabled(b);
		jCheckBoxRankLeftRB.setEnabled(b);
		jCheckBoxRankLeftGR.setEnabled(b);
		jCheckBoxRankLeftGB.setEnabled(b);
		jCheckBoxRankLeftBR.setEnabled(b);
		jCheckBoxRankLeftBG.setEnabled(b);
		jCheckBoxAND.setEnabled(b);
	}

	/**
	 * This method sets the visibility of the rank checkboxes
	 * 
	 * @param b
	 */
	private void setRankCheckBoxesRightEnabled(boolean b) {
		jCheckBoxRankRightRG.setEnabled(b);
		jCheckBoxRankRightRB.setEnabled(b);
		jCheckBoxRankRightGR.setEnabled(b);
		jCheckBoxRankRightGB.setEnabled(b);
		jCheckBoxRankRightBR.setEnabled(b);
		jCheckBoxRankRightBG.setEnabled(b);
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("parameter".equals(e.getActionCommand())) {

			if (buttChR.isSelected()) {
				jLabelText.setText("Ratio = R/(R+G+B)");
				jLabelRatio.setText("Ratio[%]: ");
				this.setRankCheckBoxesLeftEnabled(false);
				this.setRankCheckBoxesRightEnabled(false);
			}
			if (buttChG.isSelected()) {
				jLabelText.setText("Ratio = G/(R+G+B)");
				jLabelRatio.setText("Ratio[%]: ");
				this.setRankCheckBoxesLeftEnabled(false);
				this.setRankCheckBoxesRightEnabled(false);
			}
			if (buttChB.isSelected()) {
				jLabelText.setText("Ratio = B/(R+G+B)");
				jLabelRatio.setText("Ratio[%]: ");
				this.setRankCheckBoxesLeftEnabled(false);
				this.setRankCheckBoxesRightEnabled(false);
			}
			if (buttChRank.isSelected()) {
				jLabelText.setText("Rank");
				jLabelRatio.setText("Sensitivity[%]: ");
				this.setRankCheckBoxesLeftEnabled(true);
				if (jCheckBoxAND.isSelected()) {
					this.setRankCheckBoxesRightEnabled(true);
				} else {
					this.setRankCheckBoxesRightEnabled(false);
				}
			}

			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object obE = e.getSource();
		if (obE instanceof JSpinner) {
			jSpinnerRatio.removeChangeListener(this);

			if (obE == jSpinnerRatio) {
				ratio = (Integer) jSpinnerRatio.getValue();
				logger.debug("Updating ratio: " + ratio + ".");
				if (buttChRank.isSelected()) {
					//jLabelRatio.setText("Sensitivity[%]: " + ratio);
				} else {
					//jLabelRatio.setText("Ratio[%]: " + ratio);
				}
			}
			jSpinnerRatio.addChangeListener(this);

		}
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
		
	}
}// END
