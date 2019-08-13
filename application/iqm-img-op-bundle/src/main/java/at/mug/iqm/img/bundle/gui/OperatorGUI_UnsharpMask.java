package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_UnsharpMask.java
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


import ij.IJ;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpUnsharpMaskDescriptor;

/**
 * @author Ahammer, Kainz
 * @since 2009 06
 */
public class OperatorGUI_UnsharpMask extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8987583671390606215L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_UnsharpMask.class);

	private ParameterBlockIQM pb = null;

	private int   kernelSize = 0;
	private float gain = 0.0f;

	private JPanel    jSpinnerPanelKernelSize = null;
	private JSpinner  jSpinnerKernelSize      = null;
	private JLabel    jLabelKernelSize;

	private JPanel    jSpinnerPanelGain = null;
	private JSpinner  jSpinnerGain      = null;
	private JLabel    jLabelGain;

	/**
	 * constructor
	 */
	public OperatorGUI_UnsharpMask() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpUnsharpMaskDescriptor().getName());

		this.initialize();

		this.setTitle("UnsharpMask Filter");

		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelKernelSize(), getGridBagConstraintsKernelSize());
		this.getOpGUIContent().add(getJPanelGain(),       getGridBagConstraintsGain());

		this.pack();
		IJ.run("Options...", "iterations=1 black count=1"); // Options for Skeletonize Fill holes
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		pb.setParameter("Gain", gain);
		pb.setParameter("KernelSize", kernelSize);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		kernelSize = pb.getIntParameter("KernelSize");
		gain       = pb.getFloatParameter("Gain");

		jSpinnerKernelSize.removeChangeListener(this);
		jSpinnerGain.removeChangeListener(this);
		jSpinnerKernelSize.setValue(kernelSize);
		jSpinnerGain.setValue(gain);
		jSpinnerKernelSize.addChangeListener(this);
		jSpinnerGain.addChangeListener(this);
		jLabelKernelSize.setText("Kernel size: " + kernelSize + "x" + kernelSize +" ");
		//jLabelGain.setText("Gain: " + gain);
	}

	private GridBagConstraints getGridBagConstraintsKernelSize() {
		GridBagConstraints gridBagConstraintsKernelSize = new GridBagConstraints();
		gridBagConstraintsKernelSize.gridx = 0;
		gridBagConstraintsKernelSize.gridy = 0;
		gridBagConstraintsKernelSize.insets = new Insets(10, 10, 10, 10); // top left bottom right
		// gridBagConstraintsKernelSize.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsKernelSize;
	}

	private GridBagConstraints getGridBagConstraintsGain() {
		GridBagConstraints gridBagConstraintsGain = new GridBagConstraints();
		gridBagConstraintsGain.gridx = 0;
		gridBagConstraintsGain.gridy = 1;
		gridBagConstraintsGain.insets = new Insets(10, 10, 10, 10); // top lef bottom right
		// gridBagConstraintsGain.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsGain;
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
	 * This method initializes jJPanelKernelSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernelSize() {
		if (jSpinnerPanelKernelSize == null) {
			jSpinnerPanelKernelSize = new JPanel();
			jSpinnerPanelKernelSize.setLayout(new BorderLayout());
			jLabelKernelSize = new JLabel("Kernel size: ");
			// jLabelKernelSize.setPreferredSize(new Dimension(70, 22));
			jLabelKernelSize.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, 101, 2); // init, min, max, step
			jSpinnerKernelSize = new JSpinner(sModel);
			jSpinnerKernelSize.setToolTipText("Size of the structuring element");
			//jSpinnerKernelSize.setPreferredSize(new Dimension(60, 24));
			jSpinnerKernelSize.addChangeListener(this);
			jSpinnerKernelSize.setEditor(new JSpinner.NumberEditor(jSpinnerKernelSize, "0"));
			((JSpinner.NumberEditor) jSpinnerKernelSize.getEditor()).getTextField().setEditable(false);
													
			jSpinnerPanelKernelSize.add(jLabelKernelSize, BorderLayout.WEST);
			jSpinnerPanelKernelSize.add(jSpinnerKernelSize, BorderLayout.CENTER);
		}
		return jSpinnerPanelKernelSize;
	}
	
	/**
	 * This method initializes jJPanelGain
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelGain() {
		if (jSpinnerPanelGain == null) {
			jSpinnerPanelGain = new JPanel();
			jSpinnerPanelGain.setLayout(new BorderLayout());
			jLabelGain = new JLabel("Gain: ");
			// jLabelGain.setPreferredSize(new Dimension(70, 22));
			jLabelGain.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1.0, 1.0, 10.0, 0.1); // init, min, max, step
			//values will be changed to float 1.0 - 10.0 
			jSpinnerGain = new JSpinner(sModel);
			//jSpinnerGain.setPreferredSize(new Dimension(60, 24));
			jSpinnerGain.addChangeListener(this);
			jSpinnerGain.setEditor(new JSpinner.NumberEditor(jSpinnerGain, "#0.00"));
										
			jSpinnerPanelGain.add(jLabelGain, BorderLayout.WEST);
			jSpinnerPanelGain.add(jSpinnerGain, BorderLayout.CENTER);
		}
		return jSpinnerPanelGain;
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
		
		logger.debug(e.getSource());
		
		Object obE = e.getSource();
		if (obE instanceof JSpinner) {

			jSpinnerKernelSize.removeChangeListener(this);
			jSpinnerGain.removeChangeListener(this);

			// ScrollBars1-----------------------------------------------------------------------------
			if (obE == jSpinnerKernelSize) {
				kernelSize = (Integer) jSpinnerKernelSize.getValue();
				jLabelKernelSize.setText("Kernel size: " + kernelSize + "x" + kernelSize +" ");
			}
			if (obE == jSpinnerGain) {
				gain = ((Number)jSpinnerGain.getValue()).floatValue();
				//jLabelGain.setText("Gain: " + gain);
			}

			jSpinnerKernelSize.addChangeListener(this);
			jSpinnerGain.addChangeListener(this);

		}
		this.updateParameterBlock();
		// this.update(); //if necessary here or some lines above

		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
}// END
