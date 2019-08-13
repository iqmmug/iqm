package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_DistMap.java
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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.jai.KernelJAI;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.jaitools.KernelFactory;
import at.mug.iqm.commons.jaitools.KernelUtil;
import at.mug.iqm.img.bundle.descriptors.IqmOpDistMapDescriptor;

/**
 * <li> 2011 12 29 added 4SED and 8SED method according to Danielsson P.E. Comp Graph ImgProc 14, 227-248, 1980
 * 
 * @author Ahammer, Kainz
 * @since  2010 05
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 * 
 */
public class OperatorGUI_DistMap extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 983558953077917123L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_DistMap.class);

	private ParameterBlockIQM pb = null;

	private int kernelSize = 0;

	private JRadioButton buttErode       = null;
	private JRadioButton butt4SED        = null;
	private JRadioButton butt8SED        = null;
	private JRadioButton butt8SEDGrevera = null;
	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;

	
	private JRadioButton buttRectangle        = null;
	private JRadioButton buttCircle           = null;
	private JPanel       jPanelKernelShape    = null;
	private ButtonGroup  buttGroupKernelShape = null;
	private TitledBorder tbKernelShape   = null;

	private JPanel       pnlKernelSize  = null;
	private JSpinner     spnrKernelSize = null;
	private JLabel       lblKernelSize  = null;
	private TitledBorder tbKernelSize   = null;

	private JRadioButton buttClamp              = null;
	private JRadioButton buttNormalize          = null;
	private JRadioButton buttActual             = null;
	private JPanel       jPanelResultOptions    = null;
	private ButtonGroup  buttGroupResultOptions = null;

	/**
	 * constructor
	 */
	public OperatorGUI_DistMap() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpDistMapDescriptor().getName());
		this.initialize();
		this.setTitle("Distance Map");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelMethod(),        getGridBagConstraintsMethod());
		this.getOpGUIContent().add(getJPanelKernelShape(),   getGridBagConstraintsKernelShape());
		this.getOpGUIContent().add(getPnlKernelSize(),       getGridBagConstraintsKernelSize());
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
		if (buttErode.isSelected())       pb.setParameter("Method", 0);
		if (butt4SED.isSelected())        pb.setParameter("Method", 1);
		if (butt8SED.isSelected())        pb.setParameter("Method", 2);
		if (butt8SEDGrevera.isSelected()) pb.setParameter("Method", 3);

		if (buttRectangle.isSelected())   pb.setParameter("KernelShape", 0);
		if (buttCircle.isSelected())      pb.setParameter("KernelShape", 1);

		pb.setParameter("KernelSize", kernelSize);

		if (buttClamp.isSelected())       pb.setParameter("ResultOptions", 0);
		if (buttNormalize.isSelected())   pb.setParameter("ResultOptions", 1);
		if (buttActual.isSelected())      pb.setParameter("ResultOptions", 2);

	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Method") == 0) buttErode.setSelected(true);
		if (pb.getIntParameter("Method") == 1) butt4SED.setSelected(true);
		if (pb.getIntParameter("Method") == 2) butt8SED.setSelected(true);
		if (pb.getIntParameter("Method") == 3) butt8SEDGrevera.setSelected(true);

		if (pb.getIntParameter("KernelShape") == 0) buttRectangle.setSelected(true);
		if (pb.getIntParameter("KernelShape") == 1) buttCircle.setSelected(true);

		kernelSize = pb.getIntParameter("KernelSize");
		spnrKernelSize.removeChangeListener(this);
		spnrKernelSize.setValue(kernelSize);
		spnrKernelSize.addChangeListener(this);
		spnrKernelSize.setToolTipText("Kernel size: " + kernelSize + "x" + kernelSize);

		if (pb.getIntParameter("ResultOptions") == 0) buttClamp.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 1) buttNormalize.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 2) buttActual.setSelected(true);

		// update the GUI according to default values
		this.update();
	}

	private GridBagConstraints getGridBagConstraintsMethod() {
		GridBagConstraints gridBagConstraintsMethod = new GridBagConstraints();
		gridBagConstraintsMethod.gridx = 0;
		gridBagConstraintsMethod.gridy = 0;
		gridBagConstraintsMethod.insets = new Insets(10, 0, 0, 0); // top  left  bottom  right
		gridBagConstraintsMethod.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsMethod;
	}

	private GridBagConstraints getGridBagConstraintsKernelShape() {
		GridBagConstraints gridBagConstraintsKernelShape = new GridBagConstraints();
		gridBagConstraintsKernelShape.gridx = 0;
		gridBagConstraintsKernelShape.gridy = 1;
		gridBagConstraintsKernelShape.insets = new Insets(5, 0, 0, 0); // top  left  bottom  right
		gridBagConstraintsKernelShape.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsKernelShape;
	}

	private GridBagConstraints getGridBagConstraintsKernelSize() {
		GridBagConstraints gridBagConstraintsKernelSize = new GridBagConstraints();
		gridBagConstraintsKernelSize.gridx = 0;
		gridBagConstraintsKernelSize.gridy = 2;
		gridBagConstraintsKernelSize.insets = new Insets(5, 0, 0, 0); // top  left  bottom  right
		gridBagConstraintsKernelSize.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsKernelSize;
	}

	private GridBagConstraints getGridBagConstraintsResultOptions() {
		GridBagConstraints gridBagConstraintsResultOptions = new GridBagConstraints();
		gridBagConstraintsResultOptions.gridx = 0;
		gridBagConstraintsResultOptions.gridy = 3;
		gridBagConstraintsResultOptions.insets = new Insets(5, 0, 5, 0); // top  left  bottom  right
		gridBagConstraintsResultOptions.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsResultOptions;
	}

	/**
	 * This method updates the GUI This method overrides OperatorGUI
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		if (buttErode.isSelected()) {
			buttRectangle.setEnabled(true);
			buttCircle.setEnabled(false); // Circle always disabled!!!!
			tbKernelShape.setTitleColor(Color.BLACK); repaint();

			spnrKernelSize.setEnabled(true);
			lblKernelSize.setEnabled(true);
			tbKernelSize.setTitleColor(Color.BLACK); repaint();
		}
		if (butt4SED.isSelected() || butt8SED.isSelected() || butt8SEDGrevera.isSelected()) {
			buttRectangle.setEnabled(false);
			buttCircle.setEnabled(false);
			tbKernelShape.setTitleColor(Color.GRAY); repaint();

			spnrKernelSize.setEnabled(false);
			lblKernelSize.setEnabled(false);
			tbKernelSize.setTitleColor(Color.GRAY); repaint();
		}
		this.updateParameterBlock();
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Erode
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonErode() {
		if (buttErode == null) {
			buttErode = new JRadioButton();
			buttErode.setText("Erode");
			buttErode.setToolTipText("Subsequent ersoion amd image addition");
			buttErode.addActionListener(this);
			buttErode.setActionCommand("parameter");
		}
		return buttErode;
	}

	/**
	 * This method initializes the Option: 4SED
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButton4SED() {
		if (butt4SED == null) {
			butt4SED = new JRadioButton();
			butt4SED.setText("4SED");
			// butt4SED.setPreferredSize(new Dimension(90,10));
			butt4SED.setToolTipText("4 point sequential algorithm");
			butt4SED.addActionListener(this);
			butt4SED.setActionCommand("parameter");
		}
		return butt4SED;
	}

	/**
	 * This method initializes the Option: 8SED
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButton8SED() {
		if (butt8SED == null) {
			butt8SED = new JRadioButton();
			butt8SED.setText("8SED");
			// butt8SED.setPreferredSize(new Dimension(85,10));
			butt8SED.setToolTipText("8 point sequential algorithm");
			butt8SED.addActionListener(this);
			butt8SED.setActionCommand("parameter");
			// butt8SED.setEnabled(false);
		}
		return butt8SED;
	}

	/**
	 * This method initializes the Option: 8SEDGrevera
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButton8SEDGrevera() {
		if (butt8SEDGrevera == null) {
			butt8SEDGrevera = new JRadioButton();
			butt8SEDGrevera.setText("8SEDGrevera");
			// butt8SEDGrevera.setPreferredSize(new Dimension(85,10));
			butt8SEDGrevera.setToolTipText("Grevera's 8 point sequential algorithm");
			butt8SEDGrevera.addActionListener(this);
			butt8SEDGrevera.setActionCommand("parameter");
			// butt8SEDGrevera.setEnabled(false);
		}
		return butt8SEDGrevera;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		// if (jPanelMethod == null) {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethod.add(getJRadioButtonErode());
		jPanelMethod.add(getJRadioButton4SED());
		jPanelMethod.add(getJRadioButton8SED());
		jPanelMethod.add(getJRadioButton8SEDGrevera());

		// jPanelMethod.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		// }
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroupMethod == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttErode);
		buttGroupMethod.add(butt4SED);
		buttGroupMethod.add(butt8SED);
		buttGroupMethod.add(butt8SEDGrevera);
	}


	// -----------------------------------------------------------------------------------------
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
			//buttRectangle.setIconTextGap(0);
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
			buttCircle.setToolTipText("circular kernel shape");
			//buttCircle.setIconTextGap(0);
			buttCircle.addActionListener(this);
			buttCircle.setActionCommand("parameter");
			buttCircle.setEnabled(false);
		}
		return buttCircle;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernelShape() {
		jPanelKernelShape = new JPanel();
		//jPanelKernelShape.setLayout(new BoxLayout(jPanelKernelShape, BoxLayout.Y_AXIS));
		jPanelKernelShape.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		tbKernelShape = new TitledBorder(null, "Kernel shape", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		jPanelKernelShape.setBorder(tbKernelShape);		
		jPanelKernelShape.add(getJRadioButtonRectangle());
		jPanelKernelShape.add(getJRadioButtonCircle());
		this.setButtonGroupKernelShape(); // Grouping of JRadioButtons
		return jPanelKernelShape;
	}

	private void setButtonGroupKernelShape() {
		buttGroupKernelShape = new ButtonGroup();
		buttGroupKernelShape.add(buttRectangle);
		buttGroupKernelShape.add(buttCircle);
	}

	/**
	 * This method initializes pnlKernelSize
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getPnlKernelSize() {
		if (pnlKernelSize == null) {
			pnlKernelSize = new JPanel();
			//jPanelKernelSize.setLayout(new BoxLayout(jPanelKernelSize, BoxLayout.Y_AXIS));
			pnlKernelSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			tbKernelSize = new TitledBorder(null, "Kernel size", TitledBorder.LEADING, TitledBorder.TOP, null, null);
			pnlKernelSize.setBorder(tbKernelSize);	
			spnrKernelSize = new JSpinner(new SpinnerNumberModel(3, 3, 101, 2));
			spnrKernelSize.setToolTipText("Size of structuring element");
			spnrKernelSize.setEditor(new JSpinner.NumberEditor(spnrKernelSize, "0"));
			((JSpinner.NumberEditor) spnrKernelSize.getEditor()).getTextField().setEditable(false);	
			lblKernelSize = new JLabel();
			lblKernelSize.setLabelFor(spnrKernelSize);
			lblKernelSize.setText("Value:");
			pnlKernelSize.add(lblKernelSize, BorderLayout.WEST);
			pnlKernelSize.add(spnrKernelSize, BorderLayout.CENTER);
		}
		return pnlKernelSize;
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
			buttNormalize.setToolTipText("normalize result to byte");
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
	 * @return javax.swing.Panel
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
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {

			if (e.getSource() == buttErode) {
				this.update();
			}
			if (e.getSource() == butt4SED) {
				this.update();
			}
			if (e.getSource() == butt8SED) {
				this.update();
			}
			if (e.getSource() == butt8SEDGrevera) {
				this.update();
			}

			if (e.getSource() == buttRectangle) {
				KernelJAI kernel = KernelFactory.createRectangle(kernelSize, kernelSize);
				BoardPanel.appendTextln("Kernel shape:");
				BoardPanel .appendTextln(KernelUtil.kernelToString(kernel, true));
			}
			if (e.getSource() == buttCircle) {
				KernelJAI kernel = KernelFactory .createCircle((kernelSize - 1) / 2); // Size = radius*2
																// +1
				BoardPanel.appendTextln("Kernel shape:");
				BoardPanel .appendTextln(KernelUtil.kernelToString(kernel, true));
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
		if (obE == spnrKernelSize) {
			kernelSize = ((Number)spnrKernelSize.getValue()).intValue();
			spnrKernelSize.setToolTipText("Kernel size: " + kernelSize + "x" + kernelSize);
		}
			
		this.updateParameterBlock();

		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
}// END
