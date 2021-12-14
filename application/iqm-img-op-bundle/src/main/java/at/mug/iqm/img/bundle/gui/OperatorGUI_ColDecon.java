package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_ColDecon.java
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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

 
 

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpColDeconDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 05
 * @update 2014 12 change buttons to JRadioButtons and added TitledBorder
 */
public class OperatorGUI_ColDecon extends AbstractImageOperatorGUI implements
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7696319497542112748L;
	// class specific logger
	  

	private ParameterBlockIQM pb = null;

	private JRadioButton buttH_E                  = null;
	private JRadioButton buttH_E2                 = null;
	private JRadioButton buttH_DAB                = null;
	private JRadioButton buttFeulgenLightGreen    = null;
	private JRadioButton buttGiemsa               = null;
	private JRadioButton buttFastRed_FastBlue_DAB = null;
	private JRadioButton buttMethyl_Green_DAB     = null;
	private JRadioButton buttH_E_DAB              = null;
	private JRadioButton buttH_AEC                = null;
	private JRadioButton buttAcan_Mallory         = null;
	private JRadioButton buttMassonTrichrome      = null;
	private JRadioButton buttAlcian_blue_H        = null;
	private JRadioButton buttH_PAS                = null;
	private JRadioButton buttDAB_NBTBCIP          = null;
	private JRadioButton buttDAB                  = null;
	private JRadioButton buttRGB                  = null;
	private JRadioButton buttCMY                  = null;
	private JRadioButton buttUser                 = null;
	private JPanel       jPanelColDecon           = null;
	private ButtonGroup  buttGroupColDecon        = null;

	/**
	 * constructor
	 */
	public OperatorGUI_ColDecon() {
		System.out.println("IQM:  Now initializing...");

		this.setOpName(new IqmOpColDeconDescriptor().getName());
		this.initialize();
		this.setTitle("Color Deconvolution");
		this.getOpGUIContent().add(getJPanelColDecon());

		this.pack();
	}

	@Override
	public void updateParameterBlock() {
		if (buttH_E.isSelected())                  pb.setParameter("ColDecon", 0);
		if (buttH_E2.isSelected())                 pb.setParameter("ColDecon", 1);
		if (buttH_DAB.isSelected())                pb.setParameter("ColDecon", 2);
		if (buttFeulgenLightGreen.isSelected())    pb.setParameter("ColDecon", 3);
		if (buttGiemsa.isSelected())               pb.setParameter("ColDecon", 4);
		if (buttFastRed_FastBlue_DAB.isSelected()) pb.setParameter("ColDecon", 5);
		if (buttMethyl_Green_DAB.isSelected())     pb.setParameter("ColDecon", 6);
		if (buttH_E_DAB.isSelected())              pb.setParameter("ColDecon", 7);
		if (buttH_AEC.isSelected())                pb.setParameter("ColDecon", 8);
		if (buttAcan_Mallory.isSelected())         pb.setParameter("ColDecon", 9);
		if (buttMassonTrichrome.isSelected())      pb.setParameter("ColDecon", 10);
		if (buttAlcian_blue_H.isSelected())        pb.setParameter("ColDecon", 11);
		if (buttH_PAS.isSelected())                pb.setParameter("ColDecon", 12);
		if (buttDAB_NBTBCIP.isSelected())          pb.setParameter("ColDecon", 13);
		if (buttDAB.isSelected())                  pb.setParameter("ColDecon", 14);
		if (buttRGB.isSelected())                  pb.setParameter("ColDecon", 15);
		if (buttCMY.isSelected())                  pb.setParameter("ColDecon", 16);
		if (buttUser.isSelected())                 pb.setParameter("ColDecon", 17);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("ColDecon") == 0)   buttH_E.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 1)   buttH_E2.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 2)   buttH_DAB.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 3)   buttFeulgenLightGreen.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 4)   buttGiemsa.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 5)   buttFastRed_FastBlue_DAB.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 6)   buttMethyl_Green_DAB.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 7)   buttH_E_DAB.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 8)   buttH_AEC.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 9)   buttAcan_Mallory.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 10)  buttMassonTrichrome.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 11)  buttAlcian_blue_H.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 12)  buttH_PAS.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 13)  buttDAB_NBTBCIP.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 14)  buttDAB.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 15)  buttRGB.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 16)  buttCMY.setSelected(true);
		if (pb.getIntParameter("ColDecon") == 17)  buttUser.setSelected(true);
	}

	/**
	 * This method updates the GUI This method overrides IqmOpJFrame
	 */
	@Override
	public void update() {
		System.out.println("IQM:  Updating GUI...");
		// here, it does nothing
	}

	/**
	 * This method initializes the Option: H_E
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonH_E() {
		if (buttH_E == null) {
			buttH_E = new JRadioButton();
			buttH_E.setText("H&E");
			buttH_E.setToolTipText("H&E");
			buttH_E.addActionListener(this);
			buttH_E.setActionCommand("parameter");
		}
		return buttH_E;
	}

	/**
	 * This method initializes the Option: H_E2
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonH_E2() {
		if (buttH_E2 == null) {
			buttH_E2 = new JRadioButton();
			buttH_E2.setText("H&E2");
			buttH_E2.setToolTipText("H&E2");
			buttH_E2.addActionListener(this);
			buttH_E2.setActionCommand("parameter");
		}
		return buttH_E2;
	}

	/**
	 * This method initializes the Option: H_DAB
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonH_DAB() {
		if (buttH_DAB == null) {
			buttH_DAB = new JRadioButton();
			buttH_DAB.setText("H DAB");
			buttH_DAB.setToolTipText("H DAB");
			buttH_DAB.addActionListener(this);
			buttH_DAB.setActionCommand("parameter");
		}
		return buttH_DAB;
	}

	/**
	 * This method initializes the Option: FeulgenLightGreen
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonFeulgenLightGreen() {
		if (buttFeulgenLightGreen == null) {
			buttFeulgenLightGreen = new JRadioButton();
			buttFeulgenLightGreen.setText("Feulgen Light Green");
			buttFeulgenLightGreen.setToolTipText("Feulgen Light Green");
			buttFeulgenLightGreen.addActionListener(this);
			buttFeulgenLightGreen.setActionCommand("parameter");
		}
		return buttFeulgenLightGreen;
	}

	/**
	 * This method initializes the Option: Giemsa
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonGiemsa() {
		if (buttGiemsa == null) {
			buttGiemsa = new JRadioButton();
			buttGiemsa.setText("Giemsa");
			buttGiemsa.setToolTipText("Giemsa");
			buttGiemsa.addActionListener(this);
			buttGiemsa.setActionCommand("parameter");
		}
		return buttGiemsa;
	}

	/**
	 * This method initializes the Option: FastRed_FastBlue_DAB
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonFastRed_FastBlue_DAB() {
		if (buttFastRed_FastBlue_DAB == null) {
			buttFastRed_FastBlue_DAB = new JRadioButton();
			buttFastRed_FastBlue_DAB.setText("FastRed FastBlue DAB");
			buttFastRed_FastBlue_DAB.setToolTipText("FastRed FastBlue DAB");
			buttFastRed_FastBlue_DAB.addActionListener(this);
			buttFastRed_FastBlue_DAB.setActionCommand("parameter");
		}
		return buttFastRed_FastBlue_DAB;
	}

	/**
	 * This method initializes the Option: Methyl_Green_DAB
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMethyl_Green_DAB() {
		if (buttMethyl_Green_DAB == null) {
			buttMethyl_Green_DAB = new JRadioButton();
			buttMethyl_Green_DAB.setText("Methyl Green DAB");
			buttMethyl_Green_DAB.setToolTipText("Methyl Green DAB");
			buttMethyl_Green_DAB.addActionListener(this);
			buttMethyl_Green_DAB.setActionCommand("parameter");
		}
		return buttMethyl_Green_DAB;
	}

	/**
	 * This method initializes the Option: H_E_DAB
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonH_E_DAB() {
		if (buttH_E_DAB == null) {
			buttH_E_DAB = new JRadioButton();
			buttH_E_DAB.setText("H&E DAB");
			buttH_E_DAB.setToolTipText("H&E DAB");
			buttH_E_DAB.addActionListener(this);
			buttH_E_DAB.setActionCommand("parameter");
			buttH_E_DAB.setEnabled(true);
		}
		return buttH_E_DAB;
	}

	/**
	 * This method initializes the Option: H_AEC
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonH_AEC() {
		if (buttH_AEC == null) {
			buttH_AEC = new JRadioButton();
			buttH_AEC.setText("H AEC");
			buttH_AEC.setToolTipText("H AEC");
			buttH_AEC.addActionListener(this);
			buttH_AEC.setActionCommand("parameter");
			buttH_AEC.setEnabled(true);
		}
		return buttH_AEC;
	}

	/**
	 * This method initializes the Option: Acan_Mallory
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAcan_Mallory() {
		if (buttAcan_Mallory == null) {
			buttAcan_Mallory = new JRadioButton();
			buttAcan_Mallory.setText("Acan Mallory");
			buttAcan_Mallory.setToolTipText("Acan Mallory");
			buttAcan_Mallory.addActionListener(this);
			buttAcan_Mallory.setActionCommand("parameter");
			buttAcan_Mallory.setEnabled(true);
		}
		return buttAcan_Mallory;
	}

	/**
	 * This method initializes the Option: MassonTrichrome
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMassonTrichrome() {
		if (buttMassonTrichrome == null) {
			buttMassonTrichrome = new JRadioButton();
			buttMassonTrichrome.setText("Masson Trichrome");
			buttMassonTrichrome.setToolTipText("Masson Trichrome");
			buttMassonTrichrome.addActionListener(this);
			buttMassonTrichrome.setActionCommand("parameter");
			buttMassonTrichrome.setEnabled(true);
		}
		return buttMassonTrichrome;
	}

	/**
	 * This method initializes the Option: Alcian_blue_H
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAlcian_blue_H() {
		if (buttAlcian_blue_H == null) {
			buttAlcian_blue_H = new JRadioButton();
			buttAlcian_blue_H.setText("Alcian blue H");
			buttAlcian_blue_H.setToolTipText("Alcian blue H");
			buttAlcian_blue_H.addActionListener(this);
			buttAlcian_blue_H.setActionCommand("parameter");
			buttAlcian_blue_H.setEnabled(true);
		}
		return buttAlcian_blue_H;
	}

	/**
	 * This method initializes the Option: H_PAS
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonH_PAS() {
		if (buttH_PAS == null) {
			buttH_PAS = new JRadioButton();
			buttH_PAS.setText("H PAS");
			buttH_PAS.setToolTipText("H PAS");
			buttH_PAS.addActionListener(this);
			buttH_PAS.setActionCommand("parameter");
			buttH_PAS.setEnabled(true);
		}
		return buttH_PAS;
	}

	/**
	 * This method initializes the Option: DAB_NBTBCIP
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDAB_NBTBCIP() {
		if (buttDAB_NBTBCIP == null) {
			buttDAB_NBTBCIP = new JRadioButton();
			buttDAB_NBTBCIP.setText("DAB NBTBCIP");
			buttDAB_NBTBCIP.setToolTipText("DAB NBTBCIP");
			buttDAB_NBTBCIP.addActionListener(this);
			buttDAB_NBTBCIP.setActionCommand("parameter");
			buttDAB_NBTBCIP.setEnabled(true);
		}
		return buttDAB_NBTBCIP;
	}

	/**
	 * This method initializes the Option: DAB
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDAB() {
		if (buttDAB == null) {
			buttDAB = new JRadioButton();
			buttDAB.setText("DAB");
			buttDAB.setToolTipText("DAB");
			buttDAB.addActionListener(this);
			buttDAB.setActionCommand("parameter");
			buttDAB.setEnabled(true);
		}
		return buttDAB;
	}

	/**
	 * This method initializes the Option: RGB
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonRGB() {
		if (buttRGB == null) {
			buttRGB = new JRadioButton();
			buttRGB.setText("RGB");
			buttRGB.setToolTipText("RGB");
			buttRGB.addActionListener(this);
			buttRGB.setActionCommand("parameter");
			buttRGB.setEnabled(true);
		}
		return buttRGB;
	}

	/**
	 * This method initializes the Option: CMY
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonCMY() {
		if (buttCMY == null) {
			buttCMY = new JRadioButton();
			buttCMY.setText("CMY");
			buttCMY.setToolTipText("CMY");
			buttCMY.addActionListener(this);
			buttCMY.setActionCommand("parameter");
			buttCMY.setEnabled(true);
		}
		return buttCMY;
	}

	/**
	 * This method initializes the Option: User
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonUser() {
		if (buttUser == null) {
			buttUser = new JRadioButton();
			buttUser.setText("User");
			buttUser.setToolTipText("User");
			buttUser.addActionListener(this);
			buttUser.setActionCommand("parameter");
			buttUser.setEnabled(true);
		}
		return buttUser;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelColDecon() {
		// if (jPanelColDecon == null) {
		jPanelColDecon = new JPanel();
		jPanelColDecon.setLayout(new BoxLayout(jPanelColDecon, BoxLayout.Y_AXIS));
		//jPanelColDecon.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelColDecon.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelColDecon.add(getJRadioButtonH_E());
		jPanelColDecon.add(getJRadioButtonH_E2());
		jPanelColDecon.add(getJRadioButtonH_DAB());
		jPanelColDecon.add(getJRadioButtonFeulgenLightGreen());
		jPanelColDecon.add(getJRadioButtonGiemsa());
		jPanelColDecon.add(getJRadioButtonFastRed_FastBlue_DAB());
		jPanelColDecon.add(getJRadioButtonMethyl_Green_DAB());
		jPanelColDecon.add(getJRadioButtonH_E_DAB());
		jPanelColDecon.add(getJRadioButtonH_AEC());
		jPanelColDecon.add(getJRadioButtonAcan_Mallory());
		jPanelColDecon.add(getJRadioButtonMassonTrichrome());
		jPanelColDecon.add(getJRadioButtonAlcian_blue_H());
		jPanelColDecon.add(getJRadioButtonH_PAS());
		jPanelColDecon.add(getJRadioButtonDAB_NBTBCIP());
		jPanelColDecon.add(getJRadioButtonDAB());
		jPanelColDecon.add(getJRadioButtonRGB());
		jPanelColDecon.add(getJRadioButtonCMY());
		jPanelColDecon.add(getJRadioButtonUser());
		// jPanelColDecon.addSeparator();
		this.setButtonGroupColDecon(); // Grouping of JRadioButtons
		// }
		return jPanelColDecon;
	}

	private void setButtonGroupColDecon() {
		buttGroupColDecon = new ButtonGroup();
		buttGroupColDecon.add(buttH_E);
		buttGroupColDecon.add(buttH_E2);
		buttGroupColDecon.add(buttH_DAB);
		buttGroupColDecon.add(buttFeulgenLightGreen);
		buttGroupColDecon.add(buttGiemsa);
		buttGroupColDecon.add(buttFastRed_FastBlue_DAB);
		buttGroupColDecon.add(buttMethyl_Green_DAB);
		buttGroupColDecon.add(buttH_E_DAB);
		buttGroupColDecon.add(buttH_AEC);
		buttGroupColDecon.add(buttAcan_Mallory);
		buttGroupColDecon.add(buttMassonTrichrome);
		buttGroupColDecon.add(buttAlcian_blue_H);
		buttGroupColDecon.add(buttH_PAS);
		buttGroupColDecon.add(buttDAB_NBTBCIP);
		buttGroupColDecon.add(buttDAB);
		buttGroupColDecon.add(buttRGB);
		buttGroupColDecon.add(buttCMY);
		buttGroupColDecon.add(buttUser);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("OperatorGUI_ColDecon: event e: "
		// +e.getActionCommand());
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			System.out.println("IQM:  Performing AutoPreview");
			this.showPreview();
		}

	}
}// END
