package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_TurboReg.java
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


import java.awt.FlowLayout;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpTurboRegDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 05
 * @update 2014 12 changed buttons to JradioButtons and added TitledBorder
 */
public class OperatorGUI_TurboReg extends AbstractImageOperatorGUI implements
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6242485796697743426L;

	private static final Logger logger = LogManager.getLogger(OperatorGUI_TurboReg.class);

	private ParameterBlockIQM pb = null; // @jve:decl-index=0:

	private JRadioButton buttFast         = null;
	private JRadioButton buttAccurate     = null;
	private JPanel       jPanelRegMode    = null;
	private ButtonGroup  buttGroupRegMode = null;

	private JRadioButton buttMethodTrans   = null;
	private JRadioButton buttMethodRigBod  = null;
	private JRadioButton buttMethodScalRot = null;
	private JRadioButton buttMethodAff     = null;
	private JRadioButton buttMethodBiLin   = null;
	private JPanel       jPanelMethod      = null;
	private ButtonGroup  buttGroupMethod   = null;

	/**
	 * constructor
	 */
	public OperatorGUI_TurboReg() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpTurboRegDescriptor().getName());
		this.initialize();
		this.setTitle("TurboReg Image Registration");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelRegMode(), getGridBagConstraints_ButtonGroupRegMode());
		this.getOpGUIContent().add(getJPanelMethod(),  getGridBagConstraints_ButtonGroupMethod());

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttFast.isSelected())     pb.setParameter("RegMode", 0);
		if (buttAccurate.isSelected()) pb.setParameter("RegMode", 1);

		if (buttMethodTrans.isSelected())   pb.setParameter("Method", 0);
		if (buttMethodRigBod.isSelected())  pb.setParameter("Method", 1);
		if (buttMethodScalRot.isSelected()) pb.setParameter("Method", 2);
		if (buttMethodAff.isSelected())     pb.setParameter("Method", 3);
		if (buttMethodBiLin.isSelected())   pb.setParameter("Method", 4);

	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("RegMode") == 0)  buttFast.setSelected(true);
		if (pb.getIntParameter("RegMode") == 1)  buttAccurate.setSelected(true);

		if (pb.getIntParameter("Method") == 0)  buttMethodTrans.setSelected(true);
		if (pb.getIntParameter("Method") == 1)  buttMethodRigBod.setSelected(true);
		if (pb.getIntParameter("Method") == 2)  buttMethodScalRot.setSelected(true);
		if (pb.getIntParameter("Method") == 3)  buttMethodAff.setSelected(true);
		if (pb.getIntParameter("Method") == 4)  buttMethodBiLin.setSelected(true);
	}

	// ------------------------------------------------------------------------------


	private GridBagConstraints getGridBagConstraints_ButtonGroupRegMode() {
		GridBagConstraints gbc_ButtonGroupRegMode = new GridBagConstraints();
		gbc_ButtonGroupRegMode.gridx = 0;
		gbc_ButtonGroupRegMode.gridy = 0;
		gbc_ButtonGroupRegMode.insets = new Insets(10, 0, 0, 0); // top, left bottom right
		gbc_ButtonGroupRegMode.fill = GridBagConstraints.HORIZONTAL;
		return gbc_ButtonGroupRegMode;
	}

	private GridBagConstraints getGridBagConstraints_ButtonGroupMethod() {
		GridBagConstraints gbc_ButtonGroupMethod = new GridBagConstraints();
		gbc_ButtonGroupMethod.gridx = 0;
		gbc_ButtonGroupMethod.gridy = 1;
		gbc_ButtonGroupMethod.insets = new Insets(5, 0, 0, 0); // top, left bottom right
		gbc_ButtonGroupMethod.fill = GridBagConstraints.HORIZONTAL;
		return gbc_ButtonGroupMethod;
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
	 * This method initializes the Option: Fast
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtFast() {
		// if (buttFast == null) {
		buttFast = new JRadioButton();
		buttFast.setText("Fast");
		buttFast.setToolTipText("fast registration");
		buttFast.addActionListener(this);
		buttFast.setActionCommand("parameter");
		buttFast.setEnabled(true);
		// }
		return buttFast;
	}

	/**
	 * This method initializes the Option: Accurate
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtAccurate() {
		// if (buttAccurate == null) {
		buttAccurate = new JRadioButton();
		buttAccurate.setText("Accurate");
		buttAccurate.setToolTipText("accurate registration");
		buttAccurate.addActionListener(this);
		buttAccurate.setActionCommand("parameter");
		buttAccurate.setEnabled(false);
		// }
		return buttAccurate;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRegMode() {
		jPanelRegMode = new JPanel();
		//jPanelRegMode.setLayout(new BoxLayout(jPanelRegMode, BoxLayout.X_AXIS));
		jPanelRegMode.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelRegMode.setBorder(new TitledBorder(null, "Registration mode", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		jPanelRegMode.add(getJRadioButtonButtFast());
		jPanelRegMode.add(getJRadioButtonButtAccurate());
		this.setButtonGroupRegMode(); // Grouping of JRadioButtons
		return jPanelRegMode;
	}

	private void setButtonGroupRegMode() {
		buttGroupRegMode = new ButtonGroup();
		buttGroupRegMode.add(buttFast);
		buttGroupRegMode.add(buttAccurate);
	}

	// -----------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: MethodTrans
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtMethodTrans() {
		buttMethodTrans = new JRadioButton();
		buttMethodTrans.setText("Translation");
		buttMethodTrans.setToolTipText("translation registration");
		buttMethodTrans.addActionListener(this);
		buttMethodTrans.setActionCommand("parameter");
		return buttMethodTrans;
	}

	/**
	 * This method initializes the Option: MethodRigBod
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtMethodRigBod() {
		buttMethodRigBod = new JRadioButton();
		buttMethodRigBod.setText("Rigid Body");
		buttMethodRigBod.setToolTipText("rigid body registration");
		buttMethodRigBod.addActionListener(this);
		buttMethodRigBod.setActionCommand("parameter");
		return buttMethodRigBod;
	}

	/**
	 * This method initializes the Option: MethodScalRot
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtMethodScalRot() {
		buttMethodScalRot = new JRadioButton();
		buttMethodScalRot.setText("Scaled Rotation");
		buttMethodScalRot.setToolTipText("scaled rotation registraion");
		buttMethodScalRot.addActionListener(this);
		buttMethodScalRot.setActionCommand("parameter");
		return buttMethodScalRot;
	}

	/**
	 * This method initializes the Option: MethodAff
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtMethodAff() {
		buttMethodAff = new JRadioButton();
		buttMethodAff.setText("Affine");
		buttMethodAff.setToolTipText("affine registration");
		buttMethodAff.addActionListener(this);
		buttMethodAff.setActionCommand("parameter");
		return buttMethodAff;
	}

	/**
	 * This method initializes the Option: MethodBiLin
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtMethodBiLin() {
		buttMethodBiLin = new JRadioButton();
		buttMethodBiLin.setText("Bilinear");
		buttMethodBiLin.setToolTipText("bilinear registration");
		buttMethodBiLin.addActionListener(this);
		buttMethodBiLin.setActionCommand("parameter");
		return buttMethodBiLin;
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
		jPanelMethod.add(getJRadioButtonButtMethodTrans());
		jPanelMethod.add(getJRadioButtonButtMethodRigBod());
		jPanelMethod.add(getJRadioButtonButtMethodScalRot());
		jPanelMethod.add(getJRadioButtonButtMethodAff());
		jPanelMethod.add(getJRadioButtonButtMethodBiLin());

		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttMethodTrans);
		buttGroupMethod.add(buttMethodRigBod);
		buttGroupMethod.add(buttMethodScalRot);
		buttGroupMethod.add(buttMethodAff);
		buttGroupMethod.add(buttMethodBiLin);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
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
