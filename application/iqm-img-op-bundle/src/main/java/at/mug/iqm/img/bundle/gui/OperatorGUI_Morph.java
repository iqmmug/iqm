package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Morph.java
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
import java.awt.Color;
import java.awt.Dimension;
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

 
 

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpMorphDescriptor;

/**
 * @author Ahammer, Kainz, Kaar
 * @since  2009 04
 * @update 2014 06 Kaar, added Circular and Diamond shapes
 * @update 2015 05 HA added some tooltiptexts and changed some button texts
 */
public class OperatorGUI_Morph extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9055982407935426185L;

	// class specific logger
	  

	private ParameterBlockIQM pb = null;

	private JRadioButton buttRectangle        = null;
	private JRadioButton buttCircle           = null;
	private JRadioButton buttDiamond          = null;
	private JPanel       jPanelKernelShape    = null;
	private ButtonGroup  buttGroupKernelShape = null;
	private TitledBorder tbKernelShape        = null;

	private JPanel       jPanelKernelWidth    = null;
	private JLabel       jLabelKernelWidth    = null;
	private JSpinner     jSpinnerKernelWidth  = null;
	private JPanel       jPanelKernelHeight   = null;
	private JLabel       jLabelKernelHeight   = null;
	private JSpinner     jSpinnerKernelHeight = null;
	private JPanel       jPanelKernelSize     = null;
	private TitledBorder tbKernelSize         = null;
	
	private JCheckBox jCheckBoxEqual = null;

	private JPanel   jSpinnerPanelIterations = null;
	private JSpinner jSpinnerIterations      = null;
	private JLabel   jLabelIterations;

	private JPanel       jPanelMinBlobSize   = null;
	private JLabel       jLabelMinBlobSize   = null;
	private JSpinner     jSpinnerMinBlobSize = null;
	private JPanel       jPanelMaxBlobSize   = null;
	private JLabel       jLabelMaxBlobSize   = null;
	private JSpinner     jSpinnerMaxBlobSize = null;
	private JPanel       jPanelBlobSize      = null;
	private TitledBorder tbBlobSize          = null;

	private JRadioButton buttDilate     = null;
	private JRadioButton buttErode      = null;
	private JRadioButton buttClose      = null;
	private JRadioButton buttOpen       = null;
	private JRadioButton buttSkeleton   = null;
	private JRadioButton buttFillHoles  = null;
	private JRadioButton buttEraseBlobs = null;
	private ButtonGroup  buttGroupMorph = null;
	private JPanel       jPanelMorph    = null;

	/**
	 * constructor
	 */
	public OperatorGUI_Morph() {
		System.out.println("IQM:  Now initializing...");

		this.setOpName(new IqmOpMorphDescriptor().getName());

		this.initialize();

		this.setTitle("Morphological Operations");

		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelKernelShape(),  getGridBagConstraints_KernelShape());
		this.getOpGUIContent().add(getJPanelKernelSize(),   getGridBagConstraints_KernelSize());
		//this.getOpGUIContent().add(getJCheckBoxEqual(),    getGridBagConstraints_Equal());
		this.getOpGUIContent().add(getJPanelIterations(),   getGridBagConstraints_Iterations());
		this.getOpGUIContent().add(getJPanelBlobSize(),     getGridBagConstraints_BlobSize());
		this.getOpGUIContent().add(getJPanelMorph(),        getGridBagConstraints_Morph());

		this.pack();
		// IJ.run("Options...", "iterations=1 black count=1"); // Options for
		// Skeletonize Fill
		// holes
	}
	
	private GridBagConstraints getGridBagConstraints_KernelShape() {
		GridBagConstraints gbc_KernelShape = new GridBagConstraints();
		gbc_KernelShape.gridx = 0;
		gbc_KernelShape.gridy = 0;
		gbc_KernelShape.gridwidth = 1;// 
		gbc_KernelShape.insets = new Insets(10, 0, 0, 0); // top left bottom right
		gbc_KernelShape.fill = GridBagConstraints.HORIZONTAL;
		return gbc_KernelShape;
	}

	private GridBagConstraints getGridBagConstraints_KernelSize() {
		GridBagConstraints gbc_KernelSize = new GridBagConstraints();
		gbc_KernelSize.gridx = 0;
		gbc_KernelSize.gridy = 1;
		gbc_KernelSize.gridwidth = 1;// 
		gbc_KernelSize.insets = new Insets(5, 0, 0, 0); // top top left bottom right
		gbc_KernelSize.fill = GridBagConstraints.HORIZONTAL;
		return gbc_KernelSize;
	}

	private GridBagConstraints getGridBagConstraints_Equal() {
		GridBagConstraints gbc_Equal = new GridBagConstraints();
		gbc_Equal.gridx = 0;
		gbc_Equal.gridy = 2;
		gbc_Equal.gridwidth = 1;// 
		// gbc_Equal.anchor = GridBagConstraints.LINE_START;
		gbc_Equal.insets = new Insets(5, 0, 0, 0); // top top left bottom right
		// gbc_Equal.fill = GridBagConstraints.BOTH;
		return gbc_Equal;
	}
	private GridBagConstraints getGridBagConstraints_Iterations() {
		GridBagConstraints gbc_Iterations = new GridBagConstraints();
		gbc_Iterations.gridx = 0;
		gbc_Iterations.gridy = 3;
		gbc_Iterations.gridwidth = 1;// 
		gbc_Iterations.insets = new Insets(10, 0, 0, 0); // top top left bottom right
		// gbc_Iterations.fill = GridBagConstraints.BOTH;
		return gbc_Iterations;
	}

	private GridBagConstraints getGridBagConstraints_BlobSize() {
		GridBagConstraints gbc_BlobSize = new GridBagConstraints();
		gbc_BlobSize.anchor = GridBagConstraints.EAST;
		gbc_BlobSize.gridx = 0;
		gbc_BlobSize.gridy = 4;
		gbc_BlobSize.gridwidth = 1;// 
		gbc_BlobSize.insets = new Insets(5, 0, 0, 0); // top top left bottom right
		gbc_BlobSize.fill = GridBagConstraints.BOTH;
		return gbc_BlobSize;
	}
	private GridBagConstraints getGridBagConstraints_Morph() {
		GridBagConstraints gbc_Morph = new GridBagConstraints();
		gbc_Morph.gridx = 0;
		gbc_Morph.gridy = 5;
		gbc_Morph.gridwidth = 1;
		gbc_Morph.insets = new Insets(5, 0, 5, 0); // top top left bottom right
		// gbc_Morph.anchor = GridBagConstraints.LINE_START;
		// gbc_Morph.fill = GridBagConstraints.BOTH;
		return gbc_Morph;
	}
	
	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		if (buttRectangle.isSelected()) pb.setParameter("KernelShape", 0);
		if (buttCircle.isSelected())    pb.setParameter("KernelShape", 1);
		if (buttDiamond.isSelected())   pb.setParameter("KernelShape", 2);

		pb.setParameter("Iterations", jSpinnerIterations.getValue());

		pb.setParameter("KernelWidth", ((Number) jSpinnerKernelWidth.getValue()).intValue());
		pb.setParameter("KernelHeight",((Number) jSpinnerKernelHeight.getValue()).intValue());

		pb.setParameter("MinBlobSize", ((Number) jSpinnerMinBlobSize.getValue()).doubleValue());
		pb.setParameter("MaxBlobSize", ((Number) jSpinnerMaxBlobSize.getValue()).doubleValue());

		if (buttDilate.isSelected())     pb.setParameter("Morph", 0);
		if (buttErode.isSelected())      pb.setParameter("Morph", 1);
		if (buttClose.isSelected())      pb.setParameter("Morph", 2);
		if (buttOpen.isSelected())       pb.setParameter("Morph", 3);
		if (buttSkeleton.isSelected())   pb.setParameter("Morph", 4);
		if (buttFillHoles.isSelected())  pb.setParameter("Morph", 5);
		if (buttEraseBlobs.isSelected()) pb.setParameter("Morph", 6);
	}
	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("KernelShape") == 0) buttRectangle.setSelected(true);
		if (pb.getIntParameter("KernelShape") == 1) buttCircle.setSelected(true);
		if (pb.getIntParameter("KernelShape") == 2) buttDiamond.setSelected(true);

		int kernelWidth = pb.getIntParameter("KernelWidth");
		int kernelHeight = pb.getIntParameter("KernelHeight");
		jSpinnerKernelWidth.removeChangeListener(this);
		jSpinnerKernelHeight.removeChangeListener(this);
		jSpinnerKernelWidth.setValue(kernelWidth);
		jSpinnerKernelHeight.setValue(kernelHeight);
		jSpinnerKernelWidth.addChangeListener(this);
		jSpinnerKernelHeight.addChangeListener(this);

		int iterations = pb.getIntParameter("Iterations");
		jSpinnerIterations.removeChangeListener(this);
		jSpinnerIterations.setValue(iterations);
		jSpinnerIterations.addChangeListener(this);
		//jLabelIterations.setText("Iterations: " + iterations);

		jSpinnerMinBlobSize.removeChangeListener(this);
		jSpinnerMaxBlobSize.removeChangeListener(this);

		jSpinnerMinBlobSize.setValue(pb.getDoubleParameter("MinBlobSize"));
		jSpinnerMaxBlobSize.setValue(pb.getDoubleParameter("MaxBlobSize"));

		jSpinnerMinBlobSize.addChangeListener(this);
		jSpinnerMaxBlobSize.addChangeListener(this);

		if (pb.getIntParameter("Morph") == 0) buttDilate.setSelected(true);
		if (pb.getIntParameter("Morph") == 1) buttErode.setSelected(true);
		if (pb.getIntParameter("Morph") == 2) buttClose.setSelected(true);
		if (pb.getIntParameter("Morph") == 3) buttOpen.setSelected(true);
		if (pb.getIntParameter("Morph") == 4) buttSkeleton.setSelected(true);
		if (pb.getIntParameter("Morph") == 5) buttFillHoles.setSelected(true);
		if (pb.getIntParameter("Morph") == 6) buttEraseBlobs.setSelected(true);

		this.update();
	}
	
	/**
	 * This method updates the GUI
	 */
	@Override
	public void update() {
		if (buttDilate.isSelected() || buttErode.isSelected()
				|| buttClose.isSelected() || buttOpen.isSelected()) {
			buttRectangle.setEnabled(true);
			buttCircle.setEnabled(true);
			buttDiamond.setEnabled(true);
			tbKernelShape.setTitleColor(Color.BLACK);
			jSpinnerKernelWidth.setEnabled(true);
			jSpinnerKernelHeight.setEnabled(true);
			jLabelKernelWidth.setEnabled(true);
			jLabelKernelHeight.setEnabled(true);
			tbKernelSize.setTitleColor(Color.BLACK);
			jCheckBoxEqual.setEnabled(true);
			jSpinnerIterations.setEnabled(true);
			jLabelIterations.setEnabled(true);
			jLabelMinBlobSize.setEnabled(false);
			jLabelMaxBlobSize.setEnabled(false);
			jSpinnerMinBlobSize.setEnabled(false);
			jSpinnerMaxBlobSize.setEnabled(false);
			tbBlobSize.setTitleColor(Color.GRAY);
			this.repaint();
		}
		if (buttSkeleton.isSelected() || buttFillHoles.isSelected()) {	
			buttRectangle.setEnabled(false);
			buttCircle.setEnabled(false);
			buttDiamond.setEnabled(false);
			tbKernelShape.setTitleColor(Color.GRAY);
			jSpinnerKernelWidth.setEnabled(false);
			jSpinnerKernelHeight.setEnabled(false);
			jLabelKernelWidth.setEnabled(false);
			jLabelKernelHeight.setEnabled(false);
			tbKernelSize.setTitleColor(Color.GRAY);
			jCheckBoxEqual.setEnabled(false);
			jSpinnerIterations.setEnabled(false);
			jLabelIterations.setEnabled(false);
			jLabelMinBlobSize.setEnabled(false);
			jLabelMaxBlobSize.setEnabled(false);
			jSpinnerMinBlobSize.setEnabled(false);
			jSpinnerMaxBlobSize.setEnabled(false);
			tbBlobSize.setTitleColor(Color.GRAY);
			this.repaint();
		}
		if (buttEraseBlobs.isSelected()) {
			buttRectangle.setEnabled(false);
			buttCircle.setEnabled(false);
			buttDiamond.setEnabled(false);
			tbKernelShape.setTitleColor(Color.GRAY);
			jSpinnerKernelWidth.setEnabled(false);
			jSpinnerKernelHeight.setEnabled(false);
			jLabelKernelWidth.setEnabled(false);
			jLabelKernelHeight.setEnabled(false);
			tbKernelSize.setTitleColor(Color.GRAY);
			jCheckBoxEqual.setEnabled(false);
			jSpinnerIterations.setEnabled(false);
			jLabelIterations.setEnabled(false);
			jLabelMinBlobSize.setEnabled(true);
			jLabelMaxBlobSize.setEnabled(true);
			jSpinnerMinBlobSize.setEnabled(true);
			jSpinnerMaxBlobSize.setEnabled(true);
			tbBlobSize.setTitleColor(Color.BLACK);
			this.repaint();
		}
		this.updateParameterBlock();
	}
	/**
	 * This method initializes the Option: Rectangle
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonRectangle() {
		if (buttRectangle == null) {
			buttRectangle = new JRadioButton();
			buttRectangle.setText("Rectangle");
			buttRectangle.setToolTipText("rectangular kernel shape");
			buttRectangle.addActionListener(this);
			buttRectangle.setActionCommand("parameter");
		}
		return buttRectangle;
	}
	/**
	 * This method initializes the Option: Circle
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonCircle() {
		if (buttCircle == null) {
			buttCircle = new JRadioButton();
			buttCircle.setText("Circle");
			buttCircle.setToolTipText("circular or elliptical kernel shape");
			buttCircle.addActionListener(this);
			buttCircle.setActionCommand("parameter");
			buttCircle.setEnabled(false);
		}
		return buttCircle;
	}
	/**
	 * This method initializes the Option: Diamond
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDiamond() {
		if (buttDiamond == null) {
			buttDiamond = new JRadioButton();
			buttDiamond.setText("Diamond");
			buttDiamond.setToolTipText("diamond kernel shape");
			buttDiamond.addActionListener(this);
			buttDiamond.setActionCommand("parameter");
			buttDiamond.setEnabled(false);
		}
		return buttDiamond;
	}

	private void setButtonGroupKernelShape() {
		buttGroupKernelShape = new ButtonGroup();

		buttGroupKernelShape.add(buttRectangle);
		buttGroupKernelShape.add(buttCircle);
		buttGroupKernelShape.add(buttDiamond);;
	
	}
	/**
	 * This method initializes jPanelKernelShape
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernelShape() {
		if (jPanelKernelShape == null) {
			jPanelKernelShape = new JPanel();
			jPanelKernelShape.setLayout(new BoxLayout(jPanelKernelShape, BoxLayout.X_AXIS));
			tbKernelShape = new TitledBorder(null, "Kernel shape", TitledBorder.LEADING, TitledBorder.TOP, null, null);
			jPanelKernelShape.setBorder(tbKernelShape);	
			jPanelKernelShape.add(getJRadioButtonRectangle());
			jPanelKernelShape.add(getJRadioButtonCircle());
			jPanelKernelShape.add(getJRadioButtonDiamond());
			this.setButtonGroupKernelShape() ; // Grouping of JRadioButtons			
		}
		return jPanelKernelShape;
	}
	/**
	 * This method initializes jJPanelKernelWidth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernelWidth() {
		if (jPanelKernelWidth == null) {
			jPanelKernelWidth = new JPanel();
			jPanelKernelWidth.setLayout(new BorderLayout());
			jLabelKernelWidth = new JLabel("Width: ");
			//jLabelKernelWidth.setPreferredSize(new Dimension(70, 20));
			jLabelKernelWidth.setHorizontalAlignment(SwingConstants.LEFT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 2); // init, min, max, step
			jSpinnerKernelWidth = new JSpinner(sModel);
			//jSpinnerKernelWidth.setPreferredSize(new Dimension(60, 20));
			jSpinnerKernelWidth.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerKernelWidth.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelKernelWidth.add(jLabelKernelWidth, BorderLayout.WEST);
			jPanelKernelWidth.add(jSpinnerKernelWidth, BorderLayout.CENTER);
		}
		return jPanelKernelWidth;
	}
	/**
	 * This method initializes jJPanelKernelHeight
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernelHeight() {
		if (jPanelKernelHeight == null) {
			jPanelKernelHeight = new JPanel();
			jPanelKernelHeight.setLayout(new BorderLayout());
			jLabelKernelHeight = new JLabel("Height: ");
			jLabelKernelHeight.setHorizontalAlignment(SwingConstants.LEFT);
			//jLabelKernelHeight.setPreferredSize(new Dimension(70, 20));
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 2); // init, min, max, step
			jSpinnerKernelHeight = new JSpinner(sModel);
			//jSpinnerKernelHeight.setPreferredSize(new Dimension(60, 20));
			jSpinnerKernelHeight.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerKernelHeight.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelKernelHeight.add(jLabelKernelHeight, BorderLayout.WEST);
			jPanelKernelHeight.add(jSpinnerKernelHeight, BorderLayout.CENTER);
		}
		return jPanelKernelHeight;
	}
	/**
	 * This method initializes jPanelKernelSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernelSize() {
		if (jPanelKernelSize == null) {
			jPanelKernelSize = new JPanel();
			//jPanelKernelSize.setLayout(new BoxLayout(jPanelKernelSize, BoxLayout.X_AXIS));
			jPanelKernelSize.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
			tbKernelSize = new TitledBorder(null, "Kernel size", TitledBorder.LEADING, TitledBorder.TOP, null, null);
			jPanelKernelSize.setBorder(tbKernelSize);	
			jPanelKernelSize.add(this.getJPanelKernelWidth());
			jPanelKernelSize.add(this.getJCheckBoxEqual());
			jPanelKernelSize.add(this.getJPanelKernelHeight());
		}
		return jPanelKernelSize;
	}
	// -------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxEqual
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxEqual() {
		if (jCheckBoxEqual == null) {
			jCheckBoxEqual = new JCheckBox();
			jCheckBoxEqual.setText("Equal");
			jCheckBoxEqual.setToolTipText("allows only identical width and height");
			jCheckBoxEqual.addActionListener(this);
			jCheckBoxEqual.setActionCommand("parameter");
			jCheckBoxEqual.setSelected(true);
		}
		return jCheckBoxEqual;
	}
	// Spinners------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelIterations
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIterations() {
		if (jSpinnerPanelIterations == null) {
			jSpinnerPanelIterations = new JPanel();
			jSpinnerPanelIterations.setLayout(new BorderLayout());
			jLabelIterations = new JLabel("Iterations: ");
			// jLabelIterations.setPreferredSize(new Dimension(70, 20));
			jLabelIterations.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, 100, 1); // init, min, max, step
			jSpinnerIterations = new JSpinner(sModel);
			jSpinnerIterations.setToolTipText("Number of iterations (maximum 100)");
			//jSpinnerIterations.setPreferredSize(new Dimension(60, 20));
			jSpinnerIterations.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerIterations.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jSpinnerPanelIterations.add(jLabelIterations,   BorderLayout.WEST);
			jSpinnerPanelIterations.add(jSpinnerIterations, BorderLayout.CENTER);
		}
		return jSpinnerPanelIterations;
	}	
	/**
	 * This method initializes jJPanelMinBlobSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMinBlobSize() {
		if (jPanelMinBlobSize == null) {
			jPanelMinBlobSize = new JPanel();
			jPanelMinBlobSize.setLayout(new BorderLayout());
			jLabelMinBlobSize = new JLabel("Min[%]: ");
			jLabelMinBlobSize.setToolTipText("minimum threshold value in % of image size");
			// jLabelMinBlobSize.setPreferredSize(new Dimension(120, 20));
			jLabelMinBlobSize.setHorizontalAlignment(SwingConstants.LEFT);
			SpinnerModel sModel = new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1); // init, min, max, step
			jSpinnerMinBlobSize = new JSpinner(sModel);
			//jSpinnerMinBlobSize.setPreferredSize(new Dimension(100, 20));
			jSpinnerMinBlobSize.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMinBlobSize.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.0000000"); // decimalFormat.applyPattern("#,##0.0");

			jPanelMinBlobSize.add(jLabelMinBlobSize, BorderLayout.WEST);
			jPanelMinBlobSize.add(jSpinnerMinBlobSize, BorderLayout.CENTER);
		}
		return jPanelMinBlobSize;
	}
	/**
	 * This method initializes jJPanelMaxBlobSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxBlobSize() {
		if (jPanelMaxBlobSize == null) {
			jPanelMaxBlobSize = new JPanel();
			jPanelMaxBlobSize.setLayout(new BorderLayout());
			jLabelMaxBlobSize = new JLabel("Max[%]: ");
			jLabelMaxBlobSize.setToolTipText("maximum threshold value in % of image size");
			// jLabelMaxBlobSize.setPreferredSize(new Dimension(120, 20));
			jLabelMaxBlobSize.setHorizontalAlignment(SwingConstants.LEFT);
			SpinnerModel sModel = new SpinnerNumberModel(100.0, 0.0, 100.0, 0.1); // init, min, max, step
			jSpinnerMaxBlobSize = new JSpinner(sModel);
			//jSpinnerMaxBlobSize.setPreferredSize(new Dimension(100, 20));
			jSpinnerMaxBlobSize.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMaxBlobSize.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.0000000"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelMaxBlobSize.add(jLabelMaxBlobSize, BorderLayout.WEST);
			jPanelMaxBlobSize.add(jSpinnerMaxBlobSize, BorderLayout.CENTER);
		}
		return jPanelMaxBlobSize;
	}
	
	/**
	 * This method initializes jJPanelOldsize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBlobSize() {
		if (jPanelBlobSize == null) {
			jPanelBlobSize = new JPanel();
			//jPanelBlobSize.setLayout(new BoxLayout(jPanelBlobSize, BoxLayout.X_AXIS));
			jPanelBlobSize.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
			tbBlobSize = new TitledBorder(null, "Blob size", TitledBorder.LEADING, TitledBorder.TOP, null, null);
			jPanelBlobSize.setBorder(tbBlobSize);		
			jPanelBlobSize.add(this.getJPanelMinBlobSize());
			jPanelBlobSize.add(this.getJPanelMaxBlobSize());	
		}
		return jPanelBlobSize;
	}
	// ------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Dilate
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDilate() {
		if (buttDilate == null) {
			buttDilate = new JRadioButton();
			buttDilate.setText("Dilate");
			buttDilate.setToolTipText("morphological dilation");
			buttDilate.addActionListener(this);
			buttDilate.setActionCommand("parameter");
		}
		return buttDilate;
	}
	/**
	 * This method initializes the Option: Erode
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonErode() {
		if (buttErode == null) {
			buttErode = new JRadioButton();
			buttErode.setText("Erode");
			buttErode.setToolTipText("morphological erosion");
			buttErode.addActionListener(this);
			buttErode.setActionCommand("parameter");
		}
		return buttErode;
	}
	/**
	 * This method initializes the Option: Close
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonClose() {
		if (buttClose == null) {
			buttClose = new JRadioButton();
			buttClose.setText("Close");
			buttClose.setToolTipText("morphological closing");
			buttClose.addActionListener(this);
			buttClose.setActionCommand("parameter");
		}
		return buttClose;
	}
	/**
	 * This method initializes the Option: Open
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonOpen() {
		if (buttOpen == null) {
			buttOpen = new JRadioButton();
			buttOpen.setText("Open");
			buttOpen.setToolTipText("morphological opening");
			buttOpen.addActionListener(this);
			buttOpen.setActionCommand("parameter");
		}
		return buttOpen;
	}
	/**
	 * This method initializes the Option: Skeleton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSkeleton() {
		if (buttSkeleton == null) {
			buttSkeleton = new JRadioButton();
			buttSkeleton.setText("Skeleton");
			buttSkeleton.setToolTipText("skeletonizing");
			buttSkeleton.addActionListener(this);
			buttSkeleton.setActionCommand("parameter");
			buttSkeleton.setEnabled(true);
		}
		return buttSkeleton;
	}
	/**
	 * This method initializes the Option: FillHoles
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonFillHoles() {
		if (buttFillHoles == null) {
			buttFillHoles = new JRadioButton();
			buttFillHoles.setText("Fill holes");
			buttFillHoles.setToolTipText("filling residual holes");
			buttFillHoles.addActionListener(this);
			buttFillHoles.setActionCommand("parameter");
			buttFillHoles.setEnabled(true);
		}
		return buttFillHoles;
	}
	/**
	 * This method initializes the Option: EraseBlobs
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonEraseBlobs() {
		if (buttEraseBlobs == null) {
			buttEraseBlobs = new JRadioButton();
			buttEraseBlobs.setText("Blob size threshold");
			buttEraseBlobs.setToolTipText("eliminates blobs with size smaller than minimum[% image size] and larger than maximum [% imnage size]");
			buttEraseBlobs.addActionListener(this);
			buttEraseBlobs.setActionCommand("parameter");
			buttEraseBlobs.setEnabled(true);
		}
		return buttEraseBlobs;
	}
	/**
	 * This method initializes jPanelMorph
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMorph() {
		// if (jPanelMorph == null) {
		jPanelMorph = new JPanel();
		jPanelMorph.setLayout(new BoxLayout(jPanelMorph, BoxLayout.Y_AXIS));
		jPanelMorph.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		jPanelMorph.add(getJRadioButtonDilate());
		jPanelMorph.add(getJRadioButtonErode());
		jPanelMorph.add(getJRadioButtonClose());
		jPanelMorph.add(getJRadioButtonOpen());
		jPanelMorph.add(getJRadioButtonSkeleton());
		jPanelMorph.add(getJRadioButtonFillHoles());
		jPanelMorph.add(getJRadioButtonEraseBlobs());
		// jPanelMorph.addSeparator();
		this.setButtonGroupMorph(); // Grouping of JRadioButtons
		// }
		return jPanelMorph;
	}
	private void setButtonGroupMorph() {
		// if (ButtonGroup buttGroupMorph == null) {
		buttGroupMorph = new ButtonGroup();
		buttGroupMorph.add(buttDilate);
		buttGroupMorph.add(buttErode);
		buttGroupMorph.add(buttClose);
		buttGroupMorph.add(buttOpen);
		buttGroupMorph.add(buttSkeleton);
		buttGroupMorph.add(buttFillHoles);
		buttGroupMorph.add(buttEraseBlobs);
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("OperatorGUI_Morph: event e: "
		// +e.getActionCommand());
		if ("parameter".equals(e.getActionCommand())) {

			if (e.getSource() == buttRectangle) {

			}
			if (e.getSource() == buttCircle) {

			}
			if (e.getSource() == buttDiamond) {

			}
			if (e.getSource() == jCheckBoxEqual) {
				if (jCheckBoxEqual.isSelected()) {
					int kernelWidth = ((Number) jSpinnerKernelWidth.getValue()).intValue();
					@SuppressWarnings("unused")
					int kernelHeight = ((Number) jSpinnerKernelHeight.getValue()).intValue();
					jSpinnerKernelWidth.removeChangeListener(this);
					jSpinnerKernelHeight.removeChangeListener(this);
					jSpinnerKernelHeight.setValue(kernelWidth);
					// jSpinnerKernelWidth.setValue(kernelHeight);
					jSpinnerKernelWidth.addChangeListener(this);
					jSpinnerKernelHeight.addChangeListener(this);
				} else {

				}
			}
			if (e.getSource() == buttDilate) {
				// this.updateIqmOperatorGUI();
			}
			if (e.getSource() == buttErode) {
			}
			if (e.getSource() == buttClose) {
			}
			if (e.getSource() == buttOpen) {
			}
			if (e.getSource() == buttSkeleton) {
			}
			if (e.getSource() == buttFillHoles) {
			}
			if (e.getSource() == buttEraseBlobs) {
			}
			this.updateParameterBlock();
			this.update(); // if necessary here or some lines above
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			System.out.println("IQM:  Performing AutoPreview");
			this.showPreview();
		}
	}
	// -------------------------------------------------------------------------------------------------
	@SuppressWarnings("unused")
	@Override
	public void stateChanged(ChangeEvent e) {
		
		int iterations   = (Integer) jSpinnerIterations.getValue();
		double inR       = ((Number) jSpinnerMinBlobSize.getValue()).doubleValue();
		double inG       = ((Number) jSpinnerMaxBlobSize.getValue()).doubleValue();
		int kernelWidth  = ((Number) jSpinnerKernelWidth.getValue()).intValue();
		int kernelHeight = ((Number) jSpinnerKernelHeight.getValue()).intValue();
			
		if (e.getSource() == jSpinnerIterations){
		
		}	
		if (e.getSource() == jSpinnerKernelWidth){
			if ((kernelWidth % 2)==0){ //even numbers are not allowed
				kernelWidth = kernelWidth+1;
				jSpinnerKernelWidth.removeChangeListener(this);
				jSpinnerKernelWidth.setValue(kernelWidth);
				jSpinnerKernelWidth.addChangeListener(this);
			}
		}
		if (e.getSource() == jSpinnerKernelHeight){
			if ((kernelHeight % 2)==0){ //even numbers are not allowed
				kernelHeight = kernelHeight+1;
				jSpinnerKernelHeight.removeChangeListener(this);
				jSpinnerKernelHeight.setValue(kernelHeight);
				jSpinnerKernelHeight.addChangeListener(this);
			}
		}
		if (buttCircle.isSelected()) { 
//			// only equal kernels possible 
//			jSpinnerKernelWidth.removeChangeListener(this);
//			jSpinnerKernelHeight.removeChangeListener(this);
//			if (e.getSource() == jSpinnerKernelWidth)  jSpinnerKernelHeight.setValue(kernelWidth);
//			if (e.getSource() == jSpinnerKernelHeight) jSpinnerKernelWidth.setValue(kernelHeight);
//			jSpinnerKernelWidth.addChangeListener(this);
//			jSpinnerKernelHeight.addChangeListener(this);
		}
		if (jCheckBoxEqual.isSelected()) {
			jSpinnerKernelWidth.removeChangeListener(this);
			jSpinnerKernelHeight.removeChangeListener(this);
			if (e.getSource() == jSpinnerKernelWidth)   jSpinnerKernelHeight.setValue(kernelWidth);
			if (e.getSource() == jSpinnerKernelHeight)  jSpinnerKernelWidth.setValue(kernelHeight);
			jSpinnerKernelWidth.addChangeListener(this);
			jSpinnerKernelHeight.addChangeListener(this);
		}
		if (buttDiamond.isSelected()) { 
//			// only equal kernels possible
//			jSpinnerKernelWidth.removeChangeListener(this);
//			jSpinnerKernelHeight.removeChangeListener(this);
//			if (e.getSource() == jSpinnerKernelWidth)  jSpinnerKernelHeight.setValue(kernelWidth);
//			if (e.getSource() == jSpinnerKernelHeight) jSpinnerKernelWidth.setValue(kernelHeight);
//			jSpinnerKernelWidth.addChangeListener(this);
//			jSpinnerKernelHeight.addChangeListener(this);
		}
		this.updateParameterBlock();
		// this.update(); //if necessary here or some lines above

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			System.out.println("IQM:  Performing AutoPreview");
			this.showPreview();
		}
	}
}// END
