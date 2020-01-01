package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_ROISegment.java
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


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpROISegmentDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 12
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_ROISegment extends AbstractImageOperatorGUI implements
		ActionListener, AdjustmentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7999109157385058184L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_ROISegment.class);

	private ParameterBlockIQM pb = null; // @jve:decl-index=0:

	private JRadioButton buttCurrROI      = null;
	private JRadioButton buttAllROIs      = null;
	private JPanel       jPanelMethod     = null;
	private ButtonGroup  buttGroupMethod  = null;
	
	private JCheckBox   jCheckBoxBinarize = null;

	/**
	 * constructor
	 */
	public OperatorGUI_ROISegment() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpROISegmentDescriptor().getName());
		this.initialize();
		this.setTitle("ROI Segmentation");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelMethod(),      getGridBagConstraints_ButtonMethodGroup());
		this.getOpGUIContent().add(getJCheckBoxBinarize(), getGridBagConstraints_Binarize());

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttCurrROI.isSelected())  pb.setParameter("Method", 0);
		if (buttAllROIs.isSelected())  pb.setParameter("Method", 1);
		if (jCheckBoxBinarize.isSelected())  pb.setParameter("Binarize", 1);
		if (!jCheckBoxBinarize.isSelected()) pb.setParameter("Binarize", 0);
	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Method") == 0)  buttCurrROI.setSelected(true);
		if (pb.getIntParameter("Method") == 1)  buttAllROIs.setSelected(true);
		if (pb.getIntParameter("Binarize") == 0)  jCheckBoxBinarize.setSelected(false);
		if (pb.getIntParameter("Binarize") == 1)  jCheckBoxBinarize.setSelected(true);
	}

	// ------------------------------------------------------------------------------
	private GridBagConstraints getGridBagConstraints_ButtonMethodGroup() {
		GridBagConstraints gbc_ButtonMethodGroup = new GridBagConstraints();
		gbc_ButtonMethodGroup.gridx = 0;
		gbc_ButtonMethodGroup.gridy = 0;
		gbc_ButtonMethodGroup.insets = new Insets(10, 0, 5, 0); // top  left  bottom  right
		// gbc_ButtonMethodGroup.fill = GridBagConstraints.BOTH;
		return gbc_ButtonMethodGroup;
	}

	private GridBagConstraints getGridBagConstraints_Binarize() {
		GridBagConstraints gbc_Binarize = new GridBagConstraints();
		gbc_Binarize.gridx = 1;
		gbc_Binarize.gridy = 0;
		//gbc_Binarize.anchor = GridBagConstraints.LINE_START;
		gbc_Binarize.insets = new Insets(5, 0, 5, 0); // top,  left  bottom  right
		//gbc_Binarize.fill = GridBagConstraints.BOTH;
		return gbc_Binarize;
	}

	// ------------------------------------------------------------------------------------------------------------------
	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	// Buttons--------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtCurrROI() {
		buttCurrROI = new JRadioButton();
		buttCurrROI.setText("Current ROI");
		buttCurrROI.setToolTipText("only current ROI is segmented");
		buttCurrROI.addActionListener(this);
		buttCurrROI.setActionCommand("parameter");
		return buttCurrROI;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtAllROIs() {
		// if (buttAllROIs == null) {
		buttAllROIs = new JRadioButton();
		buttAllROIs.setText("All ROIs");
		buttAllROIs.setToolTipText("all ROIs are segmented");
		buttAllROIs.addActionListener(this);
		buttAllROIs.setActionCommand("parameter");
		// }
		return buttAllROIs;
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
		jPanelMethod.setBorder(new TitledBorder(null, "ROI selection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelMethod.add(getJRadioButtonButtCurrROI());
		jPanelMethod.add(getJRadioButtonButtAllROIs());
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttCurrROI);
		buttGroupMethod.add(buttAllROIs);
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * This method initializes jCheckBoxBinarize
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxBinarize() {
		if (jCheckBoxBinarize == null) {
			jCheckBoxBinarize = new JCheckBox();
			jCheckBoxBinarize.setText("Binarize");
			jCheckBoxBinarize.addActionListener(this);
			jCheckBoxBinarize.setActionCommand("parameter");
		}
		return jCheckBoxBinarize;
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		// preview automatically, if selected
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
