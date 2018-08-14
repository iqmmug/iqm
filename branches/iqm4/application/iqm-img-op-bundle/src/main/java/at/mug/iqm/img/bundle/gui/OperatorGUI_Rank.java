package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Rank.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpRankDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 06
 */
public class OperatorGUI_Rank extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 *	The UID for serialization.
	 */
	private static final long serialVersionUID = -3515321451110526788L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_Rank.class);

	private ParameterBlockIQM pb = null;

	private int kernelSize = 0;
	private int iterations = 1;
	private double alpha = 0.25d;

	private JRadioButton buttMedian;
	private JRadioButton buttMin;
	private JRadioButton buttMax;
	private JPanel               jPanelMethod    = null;

	private JRadioButton buttSquare    = null;
	private JRadioButton buttPlus      = null;
	private JRadioButton buttX         = null;
	private JRadioButton buttSquareSep = null;
	
	private JPanel       jPanelKernelShape    = null;

	private JPanel   jSpinnerPanelKernelSize = null;
	private JSpinner jSpinnerKernelSize      = null;
	private JLabel   jLabelKernelSize;
	
	private JPanel   pnlIterations;
	private JSpinner spnrIterations;
	private JLabel   lblIterations;
	private JRadioButton buttAlphaFilt;
	private JPanel pnlAlpha;
	private JLabel lblAlpha;
	private JSpinner spnrAlpha;
	private final ButtonGroup btnGrpMethod = new ButtonGroup();
	private final ButtonGroup btnGrpKernel = new ButtonGroup();

	/**
	 * constructor
	 */
	public OperatorGUI_Rank() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpRankDescriptor().getName());

		this.initialize();

		this.setTitle("Rank Filter");

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{0, 37, 0, 0, 0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0};
		this.getOpGUIContent().setLayout(gridBagLayout);

		this.getOpGUIContent().add(getJPanelMethod(),      getGridBagConstraintsMethod());
		GridBagConstraints gbc_pnlAlpha = new GridBagConstraints();
		gbc_pnlAlpha.gridwidth = 3;
		gbc_pnlAlpha.insets = new Insets(0, 0, 5, 0);
		gbc_pnlAlpha.gridx = 0;
		gbc_pnlAlpha.gridy = 1;
		getOpGUIContent().add(getPnlAlpha(), gbc_pnlAlpha);
		this.getOpGUIContent().add(getJPanelKernelShape(), getGridBagConstraintsKernelShape());
		this.getOpGUIContent().add(getJSpinnerKernelSize(),getGridBagConstraintsKernelSize());
		this.getOpGUIContent().add(getJSpinnerIterations(),getGridBagConstraintsIterations());

		this.pack();
		// IJ.run("Options...", "iterations=1 black count=1"); //Options for
		// Skeletonize Fill holes
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttMedian.isSelected()) pb.setParameter("Method", 0);
		if (buttMin.isSelected())    pb.setParameter("Method", 1);
		if (buttMax.isSelected())    pb.setParameter("Method", 2);
		if (buttAlphaFilt.isSelected())    pb.setParameter("Method", 3);

		if (buttSquare.isSelected())    pb.setParameter("KernelShape", 0);
		if (buttPlus.isSelected())      pb.setParameter("KernelShape", 1);
		if (buttX.isSelected())         pb.setParameter("KernelShape", 2);
		if (buttSquareSep.isSelected()) pb.setParameter("KernelShape", 3);
		
		pb.setParameter("KernelSize", kernelSize);
		pb.setParameter("Iterations", iterations);
		pb.setParameter("Alpha", alpha);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Method") == 0) buttMedian.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttMin.setSelected(true);
		if (pb.getIntParameter("Method") == 2) buttMax.setSelected(true);
		if (pb.getIntParameter("Method") == 3) buttAlphaFilt.setSelected(true);

		if (pb.getIntParameter("KernelShape") == 0) buttSquare.setSelected(true);
		if (pb.getIntParameter("KernelShape") == 1) buttPlus.setSelected(true);
		if (pb.getIntParameter("KernelShape") == 2) buttX.setSelected(true);
		if (pb.getIntParameter("KernelShape") == 3) buttSquareSep.setSelected(true);
		
		
		kernelSize = pb.getIntParameter("KernelSize");
		jSpinnerKernelSize.removeChangeListener(this);
		jSpinnerKernelSize.setValue(kernelSize);
		jSpinnerKernelSize.addChangeListener(this);
		jLabelKernelSize.setText("Kernel size: " + kernelSize + "x" + kernelSize + " ");
		
		iterations = pb.getIntParameter("Iterations");
		spnrIterations.removeChangeListener(this);
		spnrIterations.setValue(iterations);
		spnrIterations.addChangeListener(this);
		
		alpha = pb.getDoubleParameter("Alpha");
		spnrAlpha.removeChangeListener(this);
		spnrAlpha.setValue(alpha);
		spnrAlpha.addChangeListener(this);
	}

	private GridBagConstraints getGridBagConstraintsMethod() {
		GridBagConstraints gridBagConstraintsMethodText = new GridBagConstraints();
		gridBagConstraintsMethodText.gridx = 0;
		gridBagConstraintsMethodText.gridy = 0;
		gridBagConstraintsMethodText.gridwidth = 3;
		gridBagConstraintsMethodText.insets = new Insets(10, 0, 5, 0); // top left bottom right
		return gridBagConstraintsMethodText;
	}

	private GridBagConstraints getGridBagConstraintsKernelShape() {
		GridBagConstraints gridBagConstraintsKernelShape = new GridBagConstraints();
		gridBagConstraintsKernelShape.gridx = 0;
		gridBagConstraintsKernelShape.gridy = 2;
		gridBagConstraintsKernelShape.gridwidth = 3;
		gridBagConstraintsKernelShape.insets = new Insets(10, 0, 5, 0); // top left bottom right
		return gridBagConstraintsKernelShape;
	}

	private GridBagConstraints getGridBagConstraintsKernelSize() {
		GridBagConstraints gridBagConstraintsKernelSize = new GridBagConstraints();
		gridBagConstraintsKernelSize.gridwidth = 3;
		gridBagConstraintsKernelSize.gridx = 0;
		gridBagConstraintsKernelSize.gridy = 3;
		gridBagConstraintsKernelSize.insets = new Insets(10, 0, 5, 0); // top left bottom right
		return gridBagConstraintsKernelSize;
	}
	
	private GridBagConstraints getGridBagConstraintsIterations() {
		GridBagConstraints gridBagConstraintsKernelSize = new GridBagConstraints();
		gridBagConstraintsKernelSize.gridwidth = 3;
		gridBagConstraintsKernelSize.gridx = 0;
		gridBagConstraintsKernelSize.gridy = 4;
		gridBagConstraintsKernelSize.insets = new Insets(10, 0, 0, 0); // top left bottom right
		return gridBagConstraintsKernelSize;
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
	 * This method initializes jPanelMethod
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		if (jPanelMethod == null) {
			jPanelMethod = new JPanel();
			jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
			buttMedian = new JRadioButton();
			btnGrpMethod.add(buttMedian);
			jPanelMethod.add(buttMedian);
			buttMedian.addActionListener(this);
			buttMedian.setText("Median");
			buttMedian.setToolTipText("Median rank filter");
			buttMedian.setActionCommand("parameter");
			buttMin = new JRadioButton();
			btnGrpMethod.add(buttMin);
			jPanelMethod.add(buttMin);
			buttMin.addActionListener(this);
			buttMin.setText("Min");
			buttMin.setToolTipText("Min rank filter");
			buttMin.setActionCommand("parameter");
			buttMax = new JRadioButton();
			btnGrpMethod.add(buttMax);
			jPanelMethod.add(buttMax);
			buttMax.setText("Max");
			buttMax.addActionListener(this);
			buttMax.setToolTipText("Max rank filter");
			buttMax.setActionCommand("parameter");
			jPanelMethod.add(getButtAlphaFilt());
		}
		return jPanelMethod;
	}

	// ---------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Square
	 */
	private JRadioButton getJRadioButtonSquare() {
		if (buttSquare == null) {
			buttSquare = new JRadioButton();
			btnGrpKernel.add(buttSquare);
			buttSquare.setText("Square");
			buttSquare.addActionListener(this);
			buttSquare.setToolTipText("rectangular kernel shape");
			buttSquare.setActionCommand("parameter");
		}
		return buttSquare;
	}

	/**
	 * This method initializes the Option: Plus
	 */
	private JRadioButton getJRadioButtonPlus() {
		if (buttPlus == null) {
			buttPlus = new JRadioButton();
			btnGrpKernel.add(buttPlus);
			buttPlus.setText("Plus");
			buttPlus.addActionListener(this);
			buttPlus.setToolTipText("plus shaped kernel");
			buttPlus.setActionCommand("parameter");
		}
		return buttPlus;
	}

	/**
	 * This method initializes the Option: X
	 */
	private JRadioButton getJRadioButtonX() {
		if (buttX == null) {
			buttX = new JRadioButton();
			btnGrpKernel.add(buttX);
			buttX.setText("X");
			buttX.addActionListener(this);
			buttX.setToolTipText("X shaped kernel");
			buttX.setActionCommand("parameter");
		}
		return buttX;
	}

	/**
	 * This method initializes the Option: SquareSep
	 */
	private JRadioButton getJRadioButtonSquareSep() {
		if (buttSquareSep == null) {
			buttSquareSep = new JRadioButton();
			btnGrpKernel.add(buttSquareSep);
			buttSquareSep.addActionListener(this);
			buttSquareSep.setText("Square Separable");
			buttSquareSep.setToolTipText("Square shaped kernel, separable median: median of the medians of each row");
			buttSquareSep.setActionCommand("parameter");
		}
		return buttSquareSep;
	}

	/**
	 * This method initializes jPanelKernelShape
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernelShape() {
		if (jPanelKernelShape == null) {
			jPanelKernelShape = new JPanel();
			jPanelKernelShape.setBorder(new TitledBorder(null, "Kernel Shape", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jPanelKernelShape.setLayout(new BoxLayout(jPanelKernelShape, BoxLayout.Y_AXIS));
			jPanelKernelShape.add(getJRadioButtonSquare());
			jPanelKernelShape.add(getJRadioButtonPlus());
			jPanelKernelShape.add(getJRadioButtonX());
			jPanelKernelShape.add(getJRadioButtonSquareSep());
		}
		return jPanelKernelShape;
	}

	/**
	 * This method initializes jSpinnerKernelSize
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getJSpinnerKernelSize() {
		if (jSpinnerPanelKernelSize == null) {
			jSpinnerPanelKernelSize = new JPanel();
			jSpinnerKernelSize = new JSpinner();
			jSpinnerKernelSize.setToolTipText("Size of the structuring element (maximum 101)");
			jSpinnerPanelKernelSize.setLayout(new BorderLayout(0, 0));
			jSpinnerKernelSize.setModel(new SpinnerNumberModel(3, 3, 101, 2));
			jSpinnerKernelSize.addChangeListener(this);	
			jSpinnerKernelSize.setEditor(new JSpinner.NumberEditor(jSpinnerKernelSize, "0"));
			((JSpinner.NumberEditor) jSpinnerKernelSize.getEditor()).getTextField().setEditable(false);
			
			jLabelKernelSize = new JLabel();
			jLabelKernelSize.setText("Kernel size: " + kernelSize + "x" + kernelSize+ " ");
			
			jSpinnerPanelKernelSize.add(jLabelKernelSize, BorderLayout.WEST);
			jSpinnerPanelKernelSize.add(jSpinnerKernelSize);
		}
		return jSpinnerPanelKernelSize;
	}
	
	/**
	 * This method initializes spnrIterations
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getJSpinnerIterations() {
		if (pnlIterations == null) {
			pnlIterations = new JPanel();
			spnrIterations = new JSpinner();
			spnrIterations.setToolTipText("number of iterations the algorithm is applied subsequently (1-100)");
			pnlIterations.setLayout(new BorderLayout(0, 0));
			spnrIterations.setModel(new SpinnerNumberModel(1, 1, 100, 1));
			spnrIterations.addChangeListener(this);	
			lblIterations = new JLabel();
			lblIterations.setText("Iterations:");
			spnrIterations.setEditor(new JSpinner.NumberEditor(spnrIterations, "#"));
			
			pnlIterations.add(lblIterations, BorderLayout.WEST);
			pnlIterations.add(spnrIterations);
		}
		return pnlIterations;
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if ("parameter".equals(e.getActionCommand())) {
			if (src == buttAlphaFilt){
				// disable the separable kernel
				buttSquareSep.setEnabled(false);
				// enable the spinner
				spnrAlpha.setEnabled(true);
				// set the kernel to default square
				if (buttSquareSep.isSelected()){
					buttSquare.setSelected(true);
				}
			}
			else if (src == buttMedian || src == buttMin || src == buttMax){
				// enable the separable kernel
				buttSquareSep.setEnabled(true);
				spnrAlpha.setEnabled(false);
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

		if (obE == jSpinnerKernelSize) {
			kernelSize = ((Number) jSpinnerKernelSize.getValue()).intValue();
			jLabelKernelSize.setText("Kernel size: " + kernelSize + "x" + kernelSize+ " ");
		}
		if (obE == spnrIterations){
			iterations = ((Number) spnrIterations.getValue()).intValue();
		}
		if (obE == spnrAlpha){
			alpha = ((Number) spnrAlpha.getValue()).doubleValue();
		}
		this.updateParameterBlock();

		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
	private JRadioButton getButtAlphaFilt() {
		if (buttAlphaFilt == null) {
			buttAlphaFilt = new JRadioButton();
			btnGrpMethod.add(buttAlphaFilt);
			buttAlphaFilt.addActionListener(this);
			buttAlphaFilt.setToolTipText("alpha-trimmed mean filter");
			buttAlphaFilt.setText("Alpha-trimmed mean");
			buttAlphaFilt.setActionCommand("parameter");
		}
		return buttAlphaFilt;
	}
	private JPanel getPnlAlpha() {
		if (pnlAlpha == null) {
			pnlAlpha = new JPanel();
			pnlAlpha.setLayout(new BorderLayout(0, 0));
			pnlAlpha.add(getLblAlpha(), BorderLayout.WEST);
			pnlAlpha.add(getSpnrAlpha(), BorderLayout.CENTER);
		}
		return pnlAlpha;
	}
	private JLabel getLblAlpha() {
		if (lblAlpha == null) {
			lblAlpha = new JLabel("Alpha (two-tailed):");
			lblAlpha.setToolTipText("choose a value which will be trimmed left and right from the ranked pixel intensities");
		}
		return lblAlpha;
	}
	private JSpinner getSpnrAlpha() {
		if (spnrAlpha == null) {
			spnrAlpha = new JSpinner(new SpinnerNumberModel(0.25d, 0.0d, 0.45d, 0.05d));
			spnrAlpha.setEditor(new JSpinner.NumberEditor(spnrAlpha, "#0.00"));
			spnrAlpha.addChangeListener(this);
			spnrAlpha.setEnabled(false);
		}
		return spnrAlpha;
	}
}// END
