package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Smooth.java
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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpSmoothDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 06
 * @update 2014 11 changed to JRadioButtons 
 */
public class OperatorGUI_Smooth extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -1258023647013582218L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_Smooth.class);

	private ParameterBlockIQM pb = null;

	private int kernelSize = 0;

	private JRadioButton buttMean        = null;
	private JRadioButton buttGauss       = null;
	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;

	private JPanel   jSpinnerPanelKernelSize = null;
	private JSpinner jSpinnerKernelSize;
	private JLabel   jLabelKernelSize;

	/**
	 * constructor
	 */
	public OperatorGUI_Smooth() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpSmoothDescriptor().getName());

		this.initialize();
		this.setTitle("Smooth Filter");
		this.getOpGUIContent().setLayout(new GridBagLayout());
		this.getOpGUIContent().add(getJPanelMethod(),       getGridBagConstraintsMethod());
		this.getOpGUIContent().add(getJSpinnerKernelSize(), getGridBagConstraintsKernelSize());

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
		if (buttMean.isSelected())  pb.setParameter("Method", 0);
		if (buttGauss.isSelected()) pb.setParameter("Method", 1);

		pb.setParameter("KernelSize", kernelSize);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Method") == 0) buttMean.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttGauss.setSelected(true);

		
		kernelSize = pb.getIntParameter("KernelSize");
		jSpinnerKernelSize.removeChangeListener(this);
		jSpinnerKernelSize.setValue(kernelSize);
		jSpinnerKernelSize.addChangeListener(this);
		jLabelKernelSize.setText("Kernel size: " + kernelSize + "x" + kernelSize +" ");
	}

	private GridBagConstraints getGridBagConstraintsMethod() {
		GridBagConstraints gridBagConstraintsMethodText = new GridBagConstraints();
		gridBagConstraintsMethodText.gridx = 0;
		gridBagConstraintsMethodText.gridy = 0;
		gridBagConstraintsMethodText.gridwidth = 3;
		gridBagConstraintsMethodText.insets = new Insets(10, 0, 5, 0); // top left bottom right
		// gridBagConstraintsMethodText.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsMethodText;
	}

	private GridBagConstraints getGridBagConstraintsKernelSize() {
		GridBagConstraints gridBagConstraintsKernelSize = new GridBagConstraints();
		gridBagConstraintsKernelSize.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraintsKernelSize.gridx = 0;
		gridBagConstraintsKernelSize.gridy = 1;
		gridBagConstraintsKernelSize.gridwidth = 3;// ?
		gridBagConstraintsKernelSize.insets = new Insets(10, 0, 10, 0); // top left bottom right
		// gridBagConstraintsKernelSize.fill = GridBagConstraints.BOTH;
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
	 * This method initializes the Option: Mean
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtMean() {
		buttMean = new JRadioButton();
		buttMean.setText("Mean");
		buttMean.setToolTipText("Mean smooth filter");
		buttMean.addActionListener(this);
		buttMean.setActionCommand("parameter");
		return buttMean;
	}

	/**
	 * This method initializes the Option: Gauss
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtGauss() {
		buttGauss = new JRadioButton();
		buttGauss.setText("Gauss");
		buttGauss.setToolTipText("Gauss smooth filter");
		buttGauss.addActionListener(this);
		buttGauss.setActionCommand("parameter");
		return buttGauss;
	}

	/**
	 * This method initializes jJPanelMethod
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelMethod.add(getJRadioButtonMenuButtMean());
		jPanelMethod.add(getJRadioButtonMenuButtGauss());
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttMean);
		buttGroupMethod.add(buttGauss);
	}


	// ---------------------------------------------------------------------------------

	/**
	 * This method initializes jSpinnerKernelSize
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getJSpinnerKernelSize() {
		if (jSpinnerPanelKernelSize == null) {
			jSpinnerPanelKernelSize = new JPanel();
			jSpinnerPanelKernelSize.setLayout(new BorderLayout());
			jSpinnerKernelSize = new JSpinner(new SpinnerNumberModel(3, 3, 101, 2));
			jSpinnerKernelSize.setToolTipText("Size of the structuring element");
			jSpinnerKernelSize.addChangeListener(this);	
			jLabelKernelSize = new JLabel();
			jLabelKernelSize.setText("Kernel size: " + kernelSize + "x" + kernelSize +" ");	
			//jSpinnerRatio.setPreferredSize(new Dimension(60, 24));
			jSpinnerKernelSize.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerKernelSize.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jSpinnerPanelKernelSize.add(jLabelKernelSize,   BorderLayout.WEST);
			jSpinnerPanelKernelSize.add(jSpinnerKernelSize, BorderLayout.CENTER);
		}
		return jSpinnerPanelKernelSize;
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

	@Override
	public void stateChanged(ChangeEvent e) {
		Object obE = e.getSource();
		
		if (obE == jSpinnerKernelSize) {
			kernelSize = ((Number) jSpinnerKernelSize.getValue()).intValue();
			jLabelKernelSize.setText("Kernel size: " + kernelSize + "x" + kernelSize +" ");
		}
		
		this.updateParameterBlock();

		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
}// END
