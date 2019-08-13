package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_CalcImage.java
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


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpCalcImageDescriptor;

/**
 * @author Ahammer, Kainz
 * @since 2009 05
 */
public class OperatorGUI_CalcImage extends AbstractImageOperatorGUI implements
		ActionListener, AdjustmentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8115551464947983970L;

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(OperatorGUI_CalcImage.class);

	private ParameterBlockIQM pb = null;

	private JRadioButton buttAdd      = null;
	private JRadioButton buttSubtr    = null;
	private JRadioButton buttMult     = null;
	private JRadioButton buttDiv      = null;
	private JRadioButton buttAND      = null;
	private JRadioButton buttOR       = null;
	private JRadioButton buttXOR      = null;
	private JRadioButton buttMin      = null;
	private JRadioButton buttMax      = null;
	private JRadioButton buttAverage  = null;
	private JRadioButton buttDiff     = null;
	private JRadioButton buttROI      = null;
	private JPanel       jPanelCalc   = null;
	private ButtonGroup buttGroupCalc = null;

	private JPanel       jPanelResultOptions    = null;
	private ButtonGroup  buttGroupResultOptions = null;
	private JRadioButton buttClamp              = null;
	private JRadioButton buttNormalize          = null;
	private JRadioButton buttActual             = null;

	/**
	 * constructor
	 */
	public OperatorGUI_CalcImage() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpCalcImageDescriptor().getName());
		this.initialize();
		this.setTitle("Image Calculation");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelCalc(),          getGridBagConstraintsCalc());
		this.getOpGUIContent().add(getJPanelResultOptions(), getGridBagConstraintsResultOptions());

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttAdd.isSelected())     pb.setParameter("Calc", 0);
		if (buttSubtr.isSelected())   pb.setParameter("Calc", 1);
		if (buttMult.isSelected())    pb.setParameter("Calc", 2);
		if (buttDiv.isSelected())     pb.setParameter("Calc", 3);
		if (buttAND.isSelected())     pb.setParameter("Calc", 4);
		if (buttOR.isSelected())      pb.setParameter("Calc", 5);
		if (buttXOR.isSelected())     pb.setParameter("Calc", 6);
		if (buttMin.isSelected())     pb.setParameter("Calc", 7);
		if (buttMax.isSelected())     pb.setParameter("Calc", 8);
		if (buttAverage.isSelected()) pb.setParameter("Calc", 9);
		if (buttDiff.isSelected())    pb.setParameter("Calc", 10);
		if (buttROI.isSelected())     pb.setParameter("Calc", 11);

		if (buttClamp.isSelected())     pb.setParameter("ResultOptions", 0);
		if (buttNormalize.isSelected()) pb.setParameter("ResultOptions", 1);
		if (buttActual.isSelected())    pb.setParameter("ResultOptions", 2);

	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Calc") == 0) buttAdd.setSelected(true);
		if (pb.getIntParameter("Calc") == 1) buttSubtr.setSelected(true);
		if (pb.getIntParameter("Calc") == 2) buttMult.setSelected(true);
		if (pb.getIntParameter("Calc") == 3) buttDiv.setSelected(true);
		if (pb.getIntParameter("Calc") == 4) buttAND.setSelected(true);
		if (pb.getIntParameter("Calc") == 5) buttOR.setSelected(true);
		if (pb.getIntParameter("Calc") == 6) buttXOR.setSelected(true);
		if (pb.getIntParameter("Calc") == 7) buttMin.setSelected(true);
		if (pb.getIntParameter("Calc") == 8) buttMax.setSelected(true);
		if (pb.getIntParameter("Calc") == 9) buttAverage.setSelected(true);
		if (pb.getIntParameter("Calc") == 10)buttDiff.setSelected(true);
		if (pb.getIntParameter("Calc") == 11)buttROI.setSelected(true);

		if (pb.getIntParameter("ResultOptions") == 0) buttClamp.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 1) buttNormalize.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 2) buttActual.setSelected(true);
	}

	private GridBagConstraints getGridBagConstraintsCalc() {
		GridBagConstraints gridBagConstraintsCalc = new GridBagConstraints();
		gridBagConstraintsCalc.gridx = 0;
		gridBagConstraintsCalc.gridy = 0;
		gridBagConstraintsCalc.insets = new Insets(10, 0, 5, 0);
		return gridBagConstraintsCalc;
	}

	private GridBagConstraints getGridBagConstraintsResultOptions() {
		GridBagConstraints gridBagConstraintsResultOptions = new GridBagConstraints();
		gridBagConstraintsResultOptions.anchor = GridBagConstraints.NORTH;
		gridBagConstraintsResultOptions.gridx = 1;
		gridBagConstraintsResultOptions.gridy = 0;
		gridBagConstraintsResultOptions.insets = new Insets(10, 10, 5, 0);
		gridBagConstraintsResultOptions.anchor = GridBagConstraints.SOUTH;
		return gridBagConstraintsResultOptions;
	}

	/**
	 * This method updates the GUI This method overrides OperationGUI
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Add
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAdd() {
		if (buttAdd == null) {
			buttAdd = new JRadioButton();
			buttAdd.setText("+");
			buttAdd.setToolTipText("image addition");
			buttAdd.addActionListener(this);
			buttAdd.setActionCommand("parameter");
		}
		return buttAdd;
	}

	/**
	 * This method initializes the Option: Subtr
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSubtr() {
		if (buttSubtr == null) {
			buttSubtr = new JRadioButton();
			buttSubtr.setText("-");
			buttSubtr.setToolTipText("image subtraction");
			buttSubtr.addActionListener(this);
			buttSubtr.setActionCommand("parameter");
		}
		return buttSubtr;
	}

	/**
	 * This method initializes the Option: Mult
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMult() {
		if (buttMult == null) {
			buttMult = new JRadioButton();
			buttMult.setText("X");
			buttMult.setToolTipText("image multiplication");
			buttMult.addActionListener(this);
			buttMult.setActionCommand("parameter");
		}
		return buttMult;
	}

	/**
	 * This method initializes the Option: Div
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDiv() {
		if (buttDiv == null) {
			buttDiv = new JRadioButton();
			buttDiv.setText("/");
			buttDiv.setToolTipText("Image division");
			buttDiv.addActionListener(this);
			buttDiv.setActionCommand("parameter");
		}
		return buttDiv;
	}

	/**
	 * This method initializes the Option: AND
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAND() {
		if (buttAND == null) {
			buttAND = new JRadioButton();
			buttAND.setText("AND");
			buttAND.setToolTipText("logical AND");
			buttAND.addActionListener(this);
			buttAND.setActionCommand("parameter");
			buttAND.setEnabled(true);
		}
		return buttAND;
	}

	/**
	 * This method initializes the Option: OR
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonOR() {
		if (buttOR == null) {
			buttOR = new JRadioButton();
			buttOR.setText("OR");
			buttOR.setToolTipText("logical OR");
			buttOR.addActionListener(this);
			buttOR.setActionCommand("parameter");
			buttOR.setEnabled(true);
		}
		return buttOR;
	}

	/**
	 * This method initializes the Option: XOR
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonXOR() {
		if (buttXOR == null) {
			buttXOR = new JRadioButton();
			buttXOR.setText("XOR");
			buttXOR.setToolTipText("logical XOR");
			buttXOR.addActionListener(this);
			buttXOR.setActionCommand("parameter");
			buttXOR.setEnabled(true);
		}
		return buttXOR;
	}

	/**
	 * This method initializes the Option: Min
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMin() {
		if (buttMin == null) {
			buttMin = new JRadioButton();
			buttMin.setText("Min");
			buttMin.setToolTipText("minimal value of two images");
			buttMin.addActionListener(this);
			buttMin.setActionCommand("parameter");
			buttMin.setEnabled(true);
		}
		return buttMin;
	}

	/**
	 * This method initializes the Option: Max
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMax() {
		if (buttMax == null) {
			buttMax = new JRadioButton();
			buttMax.setText("Max");
			buttMax.setToolTipText("maximal value of two images");
			buttMax.addActionListener(this);
			buttMax.setActionCommand("parameter");
			buttMax.setEnabled(true);
		}
		return buttMax;
	}

	/**
	 * This method initializes the Option: Average
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAverage() {
		if (buttAverage == null) {
			buttAverage = new JRadioButton();
			buttAverage.setText("Average");
			buttAverage.setToolTipText("average of two images");
			buttAverage.addActionListener(this);
			buttAverage.setActionCommand("parameter");
			buttAverage.setEnabled(true);
		}
		return buttAverage;
	}

	/**
	 * This method initializes the Option: Diff
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDiff() {
		if (buttDiff == null) {
			buttDiff = new JRadioButton();
			buttDiff.setText("Difference");
			buttDiff.setToolTipText("difference of two images");
			buttDiff.addActionListener(this);
			buttDiff.setActionCommand("parameter");
			buttDiff.setEnabled(true);
		}
		return buttDiff;
	}

	/**
	 * This method initializes the Option: ROI
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonROI() {
		if (buttROI == null) {
			buttROI = new JRadioButton();
			buttROI.setText("ROI");
			buttROI.setToolTipText("uses the second image as a mask");
			buttROI.addActionListener(this);
			buttROI.setActionCommand("parameter");
			buttROI.setEnabled(true);
		}
		return buttROI;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelCalc() {
		// if (jPanelCalc == null) {
		jPanelCalc = new JPanel();
		jPanelCalc.setLayout(new BoxLayout(jPanelCalc, BoxLayout.Y_AXIS));
		//jPanelCalc.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelCalc.setBorder(new TitledBorder(null, "Calculation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelCalc.add(getJRadioButtonAdd());
		jPanelCalc.add(getJRadioButtonSubtr());
		jPanelCalc.add(getJRadioButtonMult());
		jPanelCalc.add(getJRadioButtonDiv());
		jPanelCalc.add(getJRadioButtonAND());
		jPanelCalc.add(getJRadioButtonOR());
		jPanelCalc.add(getJRadioButtonXOR());
		jPanelCalc.add(getJRadioButtonMin());
		jPanelCalc.add(getJRadioButtonMax());
		jPanelCalc.add(getJRadioButtonAverage());
		jPanelCalc.add(getJRadioButtonDiff());
		jPanelCalc.add(getJRadioButtonROI());

		// jPanelCalc.addSeparator();
		this.setButtonGroupCalc(); // Grouping of JRadioButtons
		// }
		return jPanelCalc;
	}

	private void setButtonGroupCalc() {
		// if (ButtonGroup buttGroupCalc == null) {
		buttGroupCalc = new ButtonGroup();
		buttGroupCalc.add(buttAdd);
		buttGroupCalc.add(buttSubtr);
		buttGroupCalc.add(buttMult);
		buttGroupCalc.add(buttDiv);
		buttGroupCalc.add(buttAND);
		buttGroupCalc.add(buttOR);
		buttGroupCalc.add(buttXOR);
		buttGroupCalc.add(buttMin);
		buttGroupCalc.add(buttMax);
		buttGroupCalc.add(buttAverage);
		buttGroupCalc.add(buttDiff);
		buttGroupCalc.add(buttROI);
	}

	// ------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: Clamp
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonClamp() {
		if (buttClamp == null) {
			buttClamp = new JRadioButton();
			buttClamp.setText("Clamp");
			buttClamp.setToolTipText("clamps result to byte");
			buttClamp.addActionListener(this);
			buttClamp.setActionCommand("parameter");
		}
		return buttClamp;
	}

	/**
	 * This method initializes the Option: Normalize
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonNormalize() {
		if (buttNormalize == null) {
			buttNormalize = new JRadioButton();
			buttNormalize.setText("Normalize");
			// buttNormalize.setPreferredSize(new Dimension(90,10));
			buttNormalize.setToolTipText("normalizes result to byte");
			buttNormalize.addActionListener(this);
			buttNormalize.setActionCommand("parameter");
		}
		return buttNormalize;
	}

	/**
	 * This method initializes the Option: Actual
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonActual() {
		if (buttActual == null) {
			buttActual = new JRadioButton();
			buttActual.setText("Actual");
			// buttActual.setPreferredSize(new Dimension(85,10));
			buttActual.setToolTipText("does nothing with result");
			buttActual.addActionListener(this);
			buttActual.setActionCommand("parameter");
			// buttActual.setEnabled(false);
		}
		return buttActual;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelResultOptions() {
		// if (jPanelResultOptions == null) {
		jPanelResultOptions = new JPanel();
		jPanelResultOptions.setLayout(new BoxLayout(jPanelResultOptions, BoxLayout.Y_AXIS));
		//jPanelResultOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelResultOptions.setBorder(new TitledBorder(null, "Result", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelResultOptions.add(getJRadioButtonClamp());
		jPanelResultOptions.add(getJRadioButtonNormalize());
		jPanelResultOptions.add(getJRadioButtonActual());

		// jPanelResultOptions.addSeparator();
		this.setButtonGroupResultOptions(); // Grouping of JRadioButtons
		// }
		return jPanelResultOptions;
	}

	private void setButtonGroupResultOptions() {
		// if (ButtonGroup buttGroupResultOptions == null) {
		buttGroupResultOptions = new ButtonGroup();
		buttGroupResultOptions.add(buttClamp);
		buttGroupResultOptions.add(buttNormalize);
		buttGroupResultOptions.add(buttActual);
	}


	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("OperatorGUI_CalcImage: event e: "
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
			this.showPreview();
		}
	}
}// END
