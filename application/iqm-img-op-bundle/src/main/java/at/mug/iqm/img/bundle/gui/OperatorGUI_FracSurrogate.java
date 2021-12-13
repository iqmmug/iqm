package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracSurrogate.java
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


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracSurrogateDescriptor;

/**
 * This class calculates surrogate images according to
 * see e.g. Mark Shelhammer, Nonlinear Dynamics in Physiology, World Scientific 2007
 * 
 * @author Ahammer, Kainz
 * @since   2012 11
 */
public class OperatorGUI_FracSurrogate extends AbstractImageOperatorGUI
		implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6176765982911579660L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_FracSurrogate.class);

	private ParameterBlockIQM pb = null;

	private JPanel       jPanelMethod = null;
	private ButtonGroup  buttGroupMethod = null;
	private JRadioButton buttShuffle = null;
	private JRadioButton buttGaussian = null;
	private JRadioButton buttRandomPhase = null;
	private JRadioButton buttAAFT = null;
	private JRadioButton buttNotYetImplemented = null;

	/**
	 * constructor
	 */
	public OperatorGUI_FracSurrogate() {
		setResizable(false);
		logger.debug("Now initializing...");
		
		this.setOpName(new IqmOpFracSurrogateDescriptor().getName());
		
		this.initialize();
		this.setTitle("Fractal Surrogate");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.surrogate.enabled")));
		
		this.getOpGUIContent().setLayout(new GridBagLayout());
		this.getOpGUIContent().add(getJPanelMethod(), getGridBagConstraintsMethod());

		this.pack();
	}

	private GridBagConstraints getGridBagConstraintsMethod() {
		GridBagConstraints gridBagConstraintsMethod = new GridBagConstraints();
		gridBagConstraintsMethod.gridx = 0;
		gridBagConstraintsMethod.gridy = 0;
		gridBagConstraintsMethod.insets = new Insets(10,0,10,0);  //top, left, bottom, right
		gridBagConstraintsMethod.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsMethod;
	}
	
	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		if (buttShuffle.isSelected())           pb.setParameter("Method", 0);
		if (buttGaussian.isSelected())          pb.setParameter("Method", 1);
		if (buttRandomPhase.isSelected())       pb.setParameter("Method", 2);
		if (buttAAFT.isSelected())              pb.setParameter("Method", 3);
		if (buttNotYetImplemented.isSelected()) pb.setParameter("Method", 4);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();
		
		if (pb.getIntParameter("Method") == 0) buttShuffle.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttGaussian.setSelected(true);
		if (pb.getIntParameter("Method") == 2) buttRandomPhase.setSelected(true);
		if (pb.getIntParameter("Method") == 3) buttAAFT.setSelected(true);
		if (pb.getIntParameter("Method") == 4) buttNotYetImplemented.setSelected(true);
	}



	/**
	 * This method updates the GUI, if needed. This method overrides
	 * OperatorGUI.
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Shuffle
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonShuffle() {
		if (buttShuffle == null) {
			buttShuffle = new JRadioButton();
			buttShuffle.setText("Shuffle");
			buttShuffle.setToolTipText("calculates a randomly shuffled image");
			buttShuffle.addActionListener(this);
			buttShuffle.setActionCommand("parameter");
		}
		return buttShuffle;
	}

	/**
	 * This method initializes the Option: Gaussian
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonGaussian() {
		if (buttGaussian == null) {
			buttGaussian = new JRadioButton();
			buttGaussian.setText("Gaussian");
			buttGaussian.setToolTipText("calculates a Gaussian image with identical mean and standardeviation as original image");
			buttGaussian.addActionListener(this);
			buttGaussian.setActionCommand("parameter");
		}
		return buttGaussian;
	}

	/**
	 * This method initializes the Option: RandomPhase
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonRandomPhase() {
		if (buttRandomPhase == null) {
			buttRandomPhase = new JRadioButton();
			buttRandomPhase.setText("Random Phase");
			buttRandomPhase.setToolTipText("calculates a phase randomized image using DFT");
			buttRandomPhase.addActionListener(this);
			buttRandomPhase.setActionCommand("parameter");
			// buttRandomPhase.setEnabled(false);
		}
		return buttRandomPhase;
	}

	/**
	 * This method initializes the Option: AAFT
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAAFT() {
		if (buttAAFT == null) {
			buttAAFT = new JRadioButton();
			buttAAFT.setText("AAFT");
			buttAAFT.setToolTipText("calculates an image using the AAFT method");
			buttAAFT.addActionListener(this);
			buttAAFT.setActionCommand("parameter");
			buttAAFT.setEnabled(false);
		}
		return buttAAFT;
	}

	/**
	 * This method initializes the Option: NotYetImplemented
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonNotYetImplemented() {
		if (buttNotYetImplemented == null) {
			buttNotYetImplemented = new JRadioButton();
			buttNotYetImplemented.setVisible(false);
			buttNotYetImplemented.setText("NotYetImplemented");
			buttNotYetImplemented.setToolTipText("calculates the NotYetImplemented part");
			buttNotYetImplemented.addActionListener(this);
			buttNotYetImplemented.setActionCommand("parameter");
			buttNotYetImplemented.setEnabled(false);
		}
		return buttNotYetImplemented;
	}

	/**
	 * This method initializes jJPanelMethod
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		// if (jPanelMethod == null) {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethod.add(getJRadioButtonShuffle());
		jPanelMethod.add(getJRadioButtonGaussian());
		jPanelMethod.add(getJRadioButtonRandomPhase());
		jPanelMethod.add(getJRadioButtonAAFT());
		jPanelMethod.add(getJRadioButtonNotYetImplemented());

		// jPanelMethod.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		// }
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroupMethod == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttShuffle);
		buttGroupMethod.add(buttGaussian);
		buttGroupMethod.add(buttRandomPhase);
		buttGroupMethod.add(buttAAFT);
		buttGroupMethod.add(buttNotYetImplemented);
	}

	// --------------------------------------------------------------------------------------------
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
}// END
