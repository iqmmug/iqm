package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_TempMatch.java
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


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpTempMatchDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 05
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_TempMatch extends AbstractImageOperatorGUI implements
		ActionListener, AdjustmentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 101857840809416483L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_Invert.class);

	private ParameterBlockIQM pbJAI = null;

	private JRadioButton buttRast         = null;
	private JRadioButton buttIter         = null;
	private JRadioButton buttMethodHidden = null;
	private JPanel       jPanelMethod     = null;
	private ButtonGroup  buttGroupMethod  = null;

	/**
	 * constructor
	 */
	public OperatorGUI_TempMatch() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpTempMatchDescriptor().getName());
		this.initialize();
		this.setTitle("Template Matching");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelMethod(), getGridBagConstraintsMethod());

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttRast.isSelected()) pbJAI.setParameter("Method", 0);
		if (buttIter.isSelected()) pbJAI.setParameter("Method", 1);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pbJAI = this.workPackage.getParameters();
		if (pbJAI.getIntParameter("Method") == 0) buttRast.setSelected(true);
		if (pbJAI.getIntParameter("Method") == 1) buttIter.setSelected(true);
	}

	private GridBagConstraints getGridBagConstraintsMethod() {
		GridBagConstraints gridBagConstraintsMethod = new GridBagConstraints();
		gridBagConstraintsMethod.gridx = 0;
		gridBagConstraintsMethod.gridy = 0;
		gridBagConstraintsMethod.insets = new Insets(10, 0, 5, 0); //top left bottom right
		gridBagConstraintsMethod.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsMethod;
	}

	/**
	 * This method updates the GUI This method overrides IqmOpJFrame
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Rast
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonRast() {
		if (buttRast == null) {
			buttRast = new JRadioButton();
			buttRast.setText("Raster");
			buttRast.setToolTipText("uses rasters to match");
			buttRast.addActionListener(this);
			buttRast.setActionCommand("parameter");
		}
		return buttRast;
	}

	/**
	 * This method initializes the Option: Iter
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonIter() {
		if (buttIter == null) {
			buttIter = new JRadioButton();
			buttIter.setText("Iterator");
			buttIter.setToolTipText("uses iterators to match");
			buttIter.addActionListener(this);
			buttIter.setActionCommand("parameter");
		}
		return buttIter;
	}

	/**
	 * This method initializes the hidden Button option
	 * 
	 * @return javax.swing.JRadioButton
	 */
	@SuppressWarnings("unused")
	private JRadioButton getJRadioButtonMethodHidden() {
		// if (buttMethodHidden == null) {
		buttMethodHidden = new JRadioButton();
		buttMethodHidden.setText("Hidden");
		buttMethodHidden.setVisible(false);
		// }
		return buttMethodHidden;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelMethod.add(getJRadioButtonRast());
		jPanelMethod.add(getJRadioButtonIter());
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttRast);
		buttGroupMethod.add(buttIter);
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("OperatorGUI_TempMatch: event e: "
		// +e.getActionCommand());
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
}// END
