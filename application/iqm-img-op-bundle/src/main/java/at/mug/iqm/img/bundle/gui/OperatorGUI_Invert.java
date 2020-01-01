package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Invert.java
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

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpInvertDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009
 * @update 2014 12 changed buttons to JRadioButtons
 */
public class OperatorGUI_Invert extends AbstractImageOperatorGUI implements
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3378935465370173571L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_Invert.class);

	/**
	 * The cached parameter block from the work package.
	 */
	private ParameterBlockIQM pb = null;

	private ButtonGroup  buttGroup   = null;
	private JRadioButton buttInvLut  = null;
	private JRadioButton buttInvImg  = null;
	private JPanel       jPanelOpt = null;

	/**
	 * This is the default constructor
	 */
	public OperatorGUI_Invert() {
		logger.debug("Now initializing...");
		
		this.setOpName(new IqmOpInvertDescriptor().getName());
		this.initialize();	
		this.setTitle("Invert Image");	
		getOpGUIContent().setLayout(new GridBagLayout());

		GridBagConstraints gbc_jPanelOpt = new GridBagConstraints();
		gbc_jPanelOpt.insets = new Insets(10, 0, 5, 0);
		gbc_jPanelOpt.gridy = 0;
		gbc_jPanelOpt.gridx = 0;
		this.getOpGUIContent().add(createJPanelOpt(), gbc_jPanelOpt);

		this.pack();
	}

	@Override
	public void updateParameterBlock() {
		if (buttInvImg.isSelected())  pb.setParameter("invertOption", 0);
		if (buttInvLut.isSelected())  pb.setParameter("invertOption", 1);
	}

	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();
		
		buttInvImg.setSelected(false);
		buttInvLut.setSelected(false);

		if (pb.getIntParameter("invertOption") == 0) buttInvImg.setSelected(true);
		if (pb.getIntParameter("invertOption") == 1) buttInvLut.setSelected(true);
	}

	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	/**
	 * This method initializes the Option Invert LUT
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonButtInvLut() {
		buttInvLut = new JRadioButton();
		buttInvLut.setEnabled(false);
		buttInvLut.setText("Invert LUT");
		buttInvLut.addActionListener(this);
		buttInvLut.setToolTipText("The inversion of the lookup table is not yet implemented!");
		buttInvLut.setActionCommand("parameter");
		return buttInvLut;
	}

	/**
	 * This method initializes the Option Invert Image
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonButtInvImg() {
		buttInvImg = new JRadioButton();
		buttInvImg.setText("Invert Image");
		buttInvImg.addActionListener(this);
		buttInvImg.setActionCommand("parameter");
		return buttInvImg;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelOpt() {
		jPanelOpt = new JPanel();
		jPanelOpt.setLayout(new BoxLayout(jPanelOpt, BoxLayout.Y_AXIS));
		//jPanelOpt.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelOpt.setBorder(new TitledBorder(null, "Option", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelOpt.add(createJRadioButtonButtInvImg());
		jPanelOpt.add(createJRadioButtonButtInvLut());
		this.setButtonGroupOptInv(); // Grouping of JRadioButtons
		return jPanelOpt;
	}

	private void setButtonGroupOptInv() {
		buttGroup = new ButtonGroup();
		buttGroup.add(buttInvImg);
		buttGroup.add(buttInvLut);
	}

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
}// END
