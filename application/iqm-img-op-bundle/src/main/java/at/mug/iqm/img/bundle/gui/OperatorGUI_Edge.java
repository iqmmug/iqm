package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Edge.java
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


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpEdgeDescriptor;

/**
 * @author Ahammer, Kainz
 * @since 2009 06
 */
public class OperatorGUI_Edge extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7725141647003197694L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_Edge.class);

	private ParameterBlockIQM pb = null;

	private final ButtonGroup btnGrpMethod = new ButtonGroup();
	private JPanel       pnlMethod;
	private JRadioButton rdbtnRoberts;
	private JRadioButton rdbtnPixDiff;
	private JRadioButton rdbtnSepPix;
	private JRadioButton rdbtnSobel;
	private JRadioButton rdbtnPrewitt;
	private JRadioButton rdbtnFreiChen;
	private JRadioButton rdbtnLaplace;
	private JRadioButton rdbtnDoG;

	private JPanel   pnlKernels;
	private JPanel   pnlSpinner;
	private JPanel   pnlKernelSize1;
	private JSpinner sprKernelSize1;
	private JLabel   lblKernelSize1;
	
	private JPanel   pnlKernelSize2;
	private JSpinner sprKernelSize2;
	private JLabel   lblKernelSize2;

	private final        ButtonGroup btnGrpDirection = new ButtonGroup();
	private JPanel       pnlDirection;
	private JRadioButton rdbtnGradientMagnitude;
	private JRadioButton rdbtnHorizontal;
	private JRadioButton rdbtnVertical;
	private JRadioButton rdbtnVertAndHoriz;
	
	
	private JPanel            pnlResultOptions;
	private final ButtonGroup btnGrpResultOptions = new ButtonGroup();
	private JRadioButton      rdbtnClampResult;
	private JRadioButton      rdbtnNormalizeResult;
	private JRadioButton      rdbtnActualResult;

	
	


	/**
	 * constructor
	 */
	public OperatorGUI_Edge() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpEdgeDescriptor().getName());
		this.initialize();
		this.setTitle("Edge Detection");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getPnlMethod(),        getGBC_pnlMethod());
		this.getOpGUIContent().add(getPnlKernels(),       getGBC_pnlKernels());	
		this.getOpGUIContent().add(getPnlDirection(),     getGBC_pnlDirection());
		this.getOpGUIContent().add(getPnlResultOptions(), getGBC_pnlResultOptions());

		this.pack();

		// IJ.run("Options...", "iterations=1 black count=1"); //Options for
		// Skeletonize Fill holes
	}
	
	private GridBagConstraints getGBC_pnlMethod() {
		GridBagConstraints gbc_Method = new GridBagConstraints();
		gbc_Method.gridx = 0;
		gbc_Method.gridy = 0;
		gbc_Method.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_Method.fill = GridBagConstraints.BOTH;
		return gbc_Method;
	}

	private GridBagConstraints getGBC_pnlKernels() {
		GridBagConstraints gbc_pnlKernels = new GridBagConstraints();
		gbc_pnlKernels.gridx = 0;
		gbc_pnlKernels.gridy = 1;
		gbc_pnlKernels.insets = new Insets(5, 0, 0, 0);
		gbc_pnlKernels.fill = GridBagConstraints.BOTH;
		return gbc_pnlKernels;
	}
	
	private GridBagConstraints getGBC_pnlDirection() {
		GridBagConstraints gbc_Direction = new GridBagConstraints();
		gbc_Direction.gridx = 0;
		gbc_Direction.gridy = 2;
		gbc_Direction.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_Direction.fill = GridBagConstraints.BOTH;
		return gbc_Direction;
	}

	private GridBagConstraints getGBC_pnlResultOptions() {
		GridBagConstraints gbc_outputMethod = new GridBagConstraints();
		gbc_outputMethod.gridx = 0;
		gbc_outputMethod.gridy = 3;
		gbc_outputMethod.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gbc_outputMethod.fill = GridBagConstraints.BOTH;
		return gbc_outputMethod;
	}


	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		if (rdbtnRoberts.isSelected()) pb.setParameter("Method", 0);
		if (rdbtnPixDiff.isSelected()) pb.setParameter("Method", 1);
		if (rdbtnSepPix.isSelected())  pb.setParameter("Method", 2);
		if (rdbtnSobel.isSelected())   pb.setParameter("Method", 3);
		if (rdbtnPrewitt.isSelected()) pb.setParameter("Method", 4);
		if (rdbtnFreiChen.isSelected())pb.setParameter("Method", 5);
		if (rdbtnLaplace.isSelected()) pb.setParameter("Method", 6);
		if (rdbtnDoG.isSelected())     pb.setParameter("Method", 7);

		pb.setParameter("KernelSize1", ((Number) sprKernelSize1.getValue()).intValue());
		pb.setParameter("KernelSize2", ((Number) sprKernelSize2.getValue()).intValue());
		
		if (rdbtnClampResult.isSelected())     pb.setParameter("ResultOption", 0);
		if (rdbtnNormalizeResult.isSelected()) pb.setParameter("ResultOption", 1);
		if (rdbtnActualResult.isSelected())    pb.setParameter("ResultOption", 2);
		
		if (rdbtnGradientMagnitude.isSelected()) pb.setParameter("Direction", 0);
		if (rdbtnHorizontal.isSelected())        pb.setParameter("Direction", 1);
		if (rdbtnVertical.isSelected())          pb.setParameter("Direction", 2);
		if (rdbtnVertAndHoriz.isSelected())      pb.setParameter("Direction", 3);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Method") == 0) rdbtnRoberts.setSelected(true);
		if (pb.getIntParameter("Method") == 1) rdbtnPixDiff.setSelected(true);
		if (pb.getIntParameter("Method") == 2) rdbtnSepPix.setSelected(true);
		if (pb.getIntParameter("Method") == 3) rdbtnSobel.setSelected(true);
		if (pb.getIntParameter("Method") == 4) rdbtnPrewitt.setSelected(true);
		if (pb.getIntParameter("Method") == 5) rdbtnFreiChen.setSelected(true);
		if (pb.getIntParameter("Method") == 6) rdbtnLaplace.setSelected(true);
		if (pb.getIntParameter("Method") == 7) rdbtnDoG.setSelected(true);

		int kernelSize1 = pb.getIntParameter("KernelSize2");
		sprKernelSize1.removeChangeListener(this);
		sprKernelSize1.setValue(kernelSize1);
		sprKernelSize1.addChangeListener(this);
		
		int kernelSize2 = pb.getIntParameter("KernelSize2");
		sprKernelSize2.removeChangeListener(this);
		sprKernelSize2.setValue(kernelSize2);
		sprKernelSize2.addChangeListener(this);
				
		if (pb.getIntParameter("Direction") == 0) rdbtnGradientMagnitude.setSelected(true);
		if (pb.getIntParameter("Direction") == 1) rdbtnHorizontal.setSelected(true);
		if (pb.getIntParameter("Direction") == 2) rdbtnVertical.setSelected(true);
		if (pb.getIntParameter("Direction") == 3) rdbtnVertAndHoriz.setSelected(true);

		int resultOption = pb.getIntParameter("ResultOption");
		if (resultOption == 0) rdbtnClampResult.setSelected(true);
		if (resultOption == 1) rdbtnNormalizeResult.setSelected(true);
		if (resultOption == 2) rdbtnActualResult.setSelected(true);
	}

	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		if (rdbtnDoG.isSelected()){
			lblKernelSize1.setEnabled(true);
			lblKernelSize2.setEnabled(true);
			sprKernelSize1.setEnabled(true);
			sprKernelSize2.setEnabled(true);
			pnlKernels.setEnabled(true);
			
			rdbtnGradientMagnitude.setEnabled(false);
			rdbtnHorizontal.setEnabled(false);
			rdbtnVertical.setEnabled(false);
			rdbtnVertAndHoriz.setEnabled(false);
			pnlDirection.setEnabled(false);
			
			
		} else { //all other buttons
			lblKernelSize1.setEnabled(false);
			lblKernelSize2.setEnabled(false);
			sprKernelSize1.setEnabled(false);				
			sprKernelSize2.setEnabled(false);
			pnlKernels.setEnabled(false);
			
			rdbtnGradientMagnitude.setEnabled(true);
			rdbtnHorizontal.setEnabled(true);
			rdbtnVertical.setEnabled(true);
			rdbtnVertAndHoriz.setEnabled(true);
			pnlDirection.setEnabled(true);
		}
	}

	/**
	 * This method initializes the Option: Roberts
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonRoberts() {
		if (rdbtnRoberts == null) {
			rdbtnRoberts = new JRadioButton();
			btnGrpMethod.add(rdbtnRoberts);
			rdbtnRoberts.addActionListener(this);
			rdbtnRoberts.setText("Roberts");
			rdbtnRoberts.setToolTipText("Roberts");
			rdbtnRoberts.setActionCommand("parameter");
		}
		return rdbtnRoberts;
	}

	/**
	 * This method initializes the Option: PixDiff
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonPixDiff() {
		if (rdbtnPixDiff == null) {
			rdbtnPixDiff = new JRadioButton();
			btnGrpMethod.add(rdbtnPixDiff);
			rdbtnPixDiff.addActionListener(this);
			rdbtnPixDiff.setText("Pixel Difference");
			rdbtnPixDiff.setToolTipText("Pixel difference, see WK Pratt Digital Image Processing 2nd ed. p503");
			rdbtnPixDiff.setActionCommand("parameter");
		}
		return rdbtnPixDiff;
	}

	/**
	 * This method initializes the Option: SepPix
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonSepPix() {
		if (rdbtnSepPix == null) {
			rdbtnSepPix = new JRadioButton();
			btnGrpMethod.add(rdbtnSepPix);
			rdbtnSepPix.addActionListener(this);
			rdbtnSepPix.setText("Separated Pixel Difference");
			rdbtnSepPix.setToolTipText("Separated pixel difference, see WK Pratt Digital Image Processing 2nd ed. p503");
			rdbtnSepPix.setActionCommand("parameter");
		}
		return rdbtnSepPix;
	}

	/**
	 * This method initializes the Option: Sobel
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonSobel() {
		if (rdbtnSobel == null) {
			rdbtnSobel = new JRadioButton();
			btnGrpMethod.add(rdbtnSobel);
			rdbtnSobel.addActionListener(this);
			rdbtnSobel.setText("Sobel");
			rdbtnSobel.setToolTipText("Sobel");
			rdbtnSobel.setActionCommand("parameter");
		}
		return rdbtnSobel;
	}

	/**
	 * This method initializes the Option: Prewitt
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonPrewitt() {
		if (rdbtnPrewitt == null) {
			rdbtnPrewitt = new JRadioButton();
			btnGrpMethod.add(rdbtnPrewitt);
			rdbtnPrewitt.addActionListener(this);
			rdbtnPrewitt.setText("Prewitt");
			rdbtnPrewitt.setToolTipText("Prewitt");
			rdbtnPrewitt.setActionCommand("parameter");
		}
		return rdbtnPrewitt;
	}

	/**
	 * This method initializes the Option: FreiChen
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonFreiChen() {
		if (rdbtnFreiChen == null) {
			rdbtnFreiChen = new JRadioButton();
			btnGrpMethod.add(rdbtnFreiChen);
			rdbtnFreiChen.addActionListener(this);
			rdbtnFreiChen.setText("FreiChen");
			rdbtnFreiChen.setToolTipText("FreiChen");
			rdbtnFreiChen.setActionCommand("parameter");
		}
		return rdbtnFreiChen;
	}

	/**
	 * This method initializes the Option: Laplace
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonLaplace() {
		if (rdbtnLaplace == null) {
			rdbtnLaplace = new JRadioButton();
			btnGrpMethod.add(rdbtnLaplace);
			rdbtnLaplace.setText("Laplace");
			rdbtnLaplace.addActionListener(this);
			rdbtnLaplace.setToolTipText("Laplace");
			rdbtnLaplace.setActionCommand("parameter");
		}
		return rdbtnLaplace;
	}

	/**
	 * This method initializes the option for difference of gaussians (DoG).
	 * 
	 * @return {@link JRadioButtonMenuItem}
	 */
	private JRadioButton getJRadioButtonDoG() {
		if (rdbtnDoG == null) {
			rdbtnDoG = new JRadioButton();
			btnGrpMethod.add(rdbtnDoG);
			rdbtnDoG.addActionListener(this);
			rdbtnDoG.setToolTipText("DoG (e.g. 5x5 - 3x3)");
			rdbtnDoG.setText("Difference of Gaussians (DoG)");
			rdbtnDoG.setActionCommand("parameter");
		}
		return rdbtnDoG;
	}

	/**
	 * This method initializes jPanelMethod
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPnlMethod() {
		if (pnlMethod == null) {
			pnlMethod = new JPanel();
			pnlMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			pnlMethod.setLayout(new BoxLayout(pnlMethod, BoxLayout.Y_AXIS));
			pnlMethod.add(getJRadioButtonRoberts());
			pnlMethod.add(getJRadioButtonPixDiff());
			pnlMethod.add(getJRadioButtonSepPix());
			pnlMethod.add(getJRadioButtonSobel());
			pnlMethod.add(getJRadioButtonPrewitt());
			pnlMethod.add(getJRadioButtonFreiChen());
			pnlMethod.add(getJRadioButtonLaplace());
			pnlMethod.add(getJRadioButtonDoG());
		}
		return pnlMethod;
	}

	// -----------------------------------------------------------------------------------------
	
	private JLabel getLblKernelSize1() {
		if (lblKernelSize1 == null) {
		lblKernelSize1 = new JLabel();
		lblKernelSize1.setToolTipText("first kernel size for DoG operator");
		lblKernelSize1.setText("Kernel size 1: ");
		}
		return lblKernelSize1;
	}
	private JSpinner getJSpinnerKernelSize1() {
		if (sprKernelSize1 == null) {
			sprKernelSize1 = new JSpinner(new SpinnerNumberModel(3, 3, 101, 2));
			sprKernelSize1.setToolTipText("Kernel size: 3x3");
			sprKernelSize1.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) sprKernelSize1.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
		}
		return sprKernelSize1;
	}
	private JLabel getLblKernelSize2() {
		if (lblKernelSize2 == null) {
			lblKernelSize2 = new JLabel();
			lblKernelSize2.setToolTipText("second kernel size for DoG operator");
			lblKernelSize2.setText("Kernel size 2: ");
		}
		return lblKernelSize2;
	}
	private JSpinner getJSpinnerKernelSize2() {
		if (sprKernelSize2 == null) {
			sprKernelSize2 = new JSpinner(new SpinnerNumberModel(3, 3, 101, 2));
			sprKernelSize2.setToolTipText("Kernel size: 3x3");
			sprKernelSize2.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) sprKernelSize2.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
		}
		return sprKernelSize2;
	}

	private JPanel getJPanelKernelSize1() {
		if (pnlKernelSize1 == null) {
			pnlKernelSize1 = new JPanel();
			pnlKernelSize1.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
			pnlKernelSize1.add(getLblKernelSize1());
			pnlKernelSize1.add(getJSpinnerKernelSize1());
		}
		return pnlKernelSize1;
	}
	private JPanel getJPanelKernelSize2() {
		if (pnlKernelSize2 == null) {
			pnlKernelSize2 = new JPanel();
			pnlKernelSize2.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
			pnlKernelSize2.add(getLblKernelSize2());
			pnlKernelSize2.add(getJSpinnerKernelSize2());
		}
		return pnlKernelSize2;
	}

	private JPanel getSpinnerPanel() {
		if (pnlSpinner == null) {
			pnlSpinner = new JPanel();
			pnlSpinner.setLayout(new BoxLayout(pnlSpinner, BoxLayout.Y_AXIS));
			pnlSpinner.add(getJPanelKernelSize1());
			pnlSpinner.add(getJPanelKernelSize2());
		}
		return pnlSpinner;
	}
	
	private JPanel getPnlKernels() {
		if (pnlKernels == null) {
			pnlKernels = new JPanel();
			pnlKernels.setBorder(new TitledBorder(null, "DoG settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			pnlKernels.setLayout(new BoxLayout(pnlKernels, BoxLayout.Y_AXIS));
			pnlKernels.add(getSpinnerPanel());
		}
		return pnlKernels;
	}
	// ---------------------------------------------------------------------------
	/**
	 * This method initializes the Option: GradientMagnitude
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonGradientMagnitude() {
		if (rdbtnGradientMagnitude == null) {
			rdbtnGradientMagnitude = new JRadioButton();
			rdbtnGradientMagnitude.addActionListener(this);
			btnGrpDirection.add(rdbtnGradientMagnitude);
			rdbtnGradientMagnitude.setText("Gradient Magnitude");
			rdbtnGradientMagnitude.setToolTipText("gradient magnitudes in horizontal and vertical direction");
			rdbtnGradientMagnitude.setActionCommand("parameter");
		}
		return rdbtnGradientMagnitude;
	}

	/**
	 * This method initializes the Option: Horizontal
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonHorizontal() {
		if (rdbtnHorizontal == null) {
			rdbtnHorizontal = new JRadioButton();
			rdbtnHorizontal.addActionListener(this);
			btnGrpDirection.add(rdbtnHorizontal);
			rdbtnHorizontal.setText("Horizontal");
			rdbtnHorizontal.setToolTipText("only horizontal direction");
			rdbtnHorizontal.setActionCommand("parameter");
		}
		return rdbtnHorizontal;
	}

	/**
	 * This method initializes the Option: Vertical
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonVertical() {
		if (rdbtnVertical == null) {
			rdbtnVertical = new JRadioButton();
			btnGrpDirection.add(rdbtnVertical);
			rdbtnVertical.addActionListener(this);
			rdbtnVertical.setText("Vertical");
			rdbtnVertical.setToolTipText("only vertical direction");
			rdbtnVertical.setActionCommand("parameter");
		}
		return rdbtnVertical;
	}

	/**
	 * This method initializes the Option: VertAndHoriz
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonVertAndHoriz() {
		if (rdbtnVertAndHoriz == null) {
			rdbtnVertAndHoriz = new JRadioButton();
			btnGrpDirection.add(rdbtnVertAndHoriz);
			rdbtnVertAndHoriz.setText("Vertical and Horizontal");
			rdbtnVertAndHoriz.addActionListener(this);
			rdbtnVertical.setToolTipText("vertical and horizontal direction subsequently");
			rdbtnVertAndHoriz.setActionCommand("parameter");
		}
		return rdbtnVertAndHoriz;
	}

	/**
	 * This method initializes jPanelDirection
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPnlDirection() {
		if (pnlDirection == null) {
			pnlDirection = new JPanel();
			pnlDirection.setBorder(new TitledBorder(null, "Direction", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			pnlDirection.setLayout(new GridLayout(0, 1, 0, 0));
			pnlDirection.add(getJRadioButtonGradientMagnitude());
			pnlDirection.add(getJRadioButtonHorizontal());
			pnlDirection.add(getJRadioButtonVertical());
			pnlDirection.add(getJRadioButtonVertAndHoriz());
		}
		return pnlDirection;
	}

	   //-----------------------------------------------------------------------------------------------
		private JPanel getPnlResultOptions() {
			if (pnlResultOptions == null) {
				pnlResultOptions = new JPanel();
				pnlResultOptions.setBorder(new TitledBorder(null, "Result 0ptions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				pnlResultOptions.setLayout(new GridLayout(0, 1, 0, 0));
				pnlResultOptions.add(getRdbtnClampResult());
				pnlResultOptions.add(getRdbtnNormalizeResult());
				pnlResultOptions.add(getRdbtnActualResult());
			}
			return pnlResultOptions;
		}
		private JRadioButton getRdbtnClampResult() {
			if (rdbtnClampResult == null) {
				rdbtnClampResult = new JRadioButton("Clamp result");
				rdbtnClampResult.addActionListener(this);
				rdbtnClampResult.setActionCommand("parameter");
				btnGrpResultOptions.add(rdbtnClampResult);
			}
			return rdbtnClampResult;
		}
		private JRadioButton getRdbtnNormalizeResult() {
			if (rdbtnNormalizeResult == null) {
				rdbtnNormalizeResult = new JRadioButton("Normalize result");
				rdbtnNormalizeResult.addActionListener(this);
				rdbtnNormalizeResult.setActionCommand("parameter");
				rdbtnNormalizeResult.setSelected(true);
				btnGrpResultOptions.add(rdbtnNormalizeResult);
			}
			return rdbtnNormalizeResult;
		}
		private JRadioButton getRdbtnActualResult() {
			if (rdbtnActualResult == null) {
				rdbtnActualResult = new JRadioButton("Actual result");
				rdbtnActualResult.addActionListener(this);
				rdbtnActualResult.setActionCommand("parameter");
				btnGrpResultOptions.add(rdbtnActualResult);
			}
			return rdbtnActualResult;
		}
	
	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			
			this.update();		
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
		if (obE == sprKernelSize1) {
			int kernelSize1 = ((Number) sprKernelSize1.getValue()).intValue();
			sprKernelSize1.setToolTipText("Kernel size: " + kernelSize1 + "x" + kernelSize1);
		}
		if (obE == sprKernelSize2) {
			int kernelSize2 = ((Number) sprKernelSize2.getValue()).intValue();
			sprKernelSize2.setToolTipText("Kernel size: " + kernelSize2 + "x" + kernelSize2);
		}
		this.updateParameterBlock();

		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}

	
}// END
