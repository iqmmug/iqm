package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_CreateFracSurf.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpCreateFracSurfDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2010 01
 * @update  2014 11 RGB output
 */
@SuppressWarnings({ "unused" })
public class OperatorGUI_CreateFracSurf extends AbstractImageOperatorGUI
		implements ActionListener, PropertyChangeListener,
		ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3876887695569434389L;

	// class specific logger
	private static Class<?> caller = OperatorGUI_CreateFracSurf.class;
	private static final Logger logger = Logger
			.getLogger(OperatorGUI_CreateFracSurf.class);

	/**
	 * The cached parameter block within the work package.
	 */
	private ParameterBlockIQM pb = null;

	private JPanel   jPanelWidth = null;
	private JLabel   jLabelWidth = null;
	private JFormattedTextField jFormattedTextFieldWidth = null;
	private JSpinner jSpinnerWidth = null;
	private JPanel   jPanelHeight = null;
	private JLabel   jLabelHeight = null;
	private JFormattedTextField jFormattedTextFieldHeight = null;
	private JSpinner jSpinnerHeight = null;
	private JPanel   jPanelSize     = null;

	private JRadioButton buttFFT         = null;
	private JRadioButton buttMidPointDis = null;
	private JRadioButton buttSumSin      = null;
	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;
	
	private JSpinner     spnrFracD = null;
	private JPanel       pnlFracD  = null;
	private TitledBorder tbFracD   = null;

	private JRadioButton butt8Bit        = null;
	private JRadioButton butt16Bit       = null;
	private JRadioButton buttRGB         = null;
	private ButtonGroup  buttGroupOutBit = null;
	private JPanel       jPanelOutBit    = null;

	/**
	 * constructor
	 */
	public OperatorGUI_CreateFracSurf() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpCreateFracSurfDescriptor().getName());

		this.initialize();

		this.setTitle("Create Fractal Surface");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.surface.enabled")));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] { 0, 75, 0, 30 };
		this.getOpGUIContent().setLayout(gridBagLayout);

		this.getOpGUIContent().add(getPnlFracD(),         getGridBagConstraintsFracD());
		this.getOpGUIContent().add(getJPanelSize(),       getGridBagConstraintsSize());
		this.getOpGUIContent().add(getJPanelMethod(),     getGridBagConstraintsButtonMethodGroup());
		this.getOpGUIContent().add(getJPanelOutBit(),     getGridBagConstraintsButtonOutBitGroup());

		this.pack();
	}

	public OperatorGUI_CreateFracSurf(IWorkPackage wp) {
		this();
		this.workPackage = wp;
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		pb.setParameter("Width", ((Number) jSpinnerWidth.getValue()).intValue());
		pb.setParameter("Height",((Number) jSpinnerHeight.getValue()).intValue());
		pb.setParameter("FracD", ((Number) spnrFracD.getValue()).floatValue());

		if (buttFFT.isSelected())        pb.setParameter("Method", IqmOpCreateFracSurfDescriptor.METHOD_FFT);
		if (buttMidPointDis.isSelected())pb.setParameter("Method", IqmOpCreateFracSurfDescriptor.METHOD_MIDPOINTDISPLACEMENT);
		if (buttSumSin.isSelected())     pb.setParameter("Method", IqmOpCreateFracSurfDescriptor.METHOD_SUMOFSINE);
		if (butt8Bit.isSelected())       pb.setParameter("OutBit", 0);
		if (butt16Bit.isSelected())      pb.setParameter("OutBit", 1);
		if (buttRGB.isSelected())        pb.setParameter("OutBit", 2);
		
		logger.debug(pb);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		jSpinnerWidth.removeChangeListener(this);
		jSpinnerHeight.removeChangeListener(this);
		jSpinnerWidth.setValue(pb.getIntParameter("Width"));
		jSpinnerHeight.setValue(pb.getIntParameter("Height"));
		jSpinnerWidth.addChangeListener(this);
		jSpinnerHeight.addChangeListener(this);

		if (pb.getIntParameter("Method") == IqmOpCreateFracSurfDescriptor.METHOD_FFT)                  buttFFT.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpCreateFracSurfDescriptor.METHOD_MIDPOINTDISPLACEMENT) buttMidPointDis.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpCreateFracSurfDescriptor.METHOD_SUMOFSINE)            buttSumSin.setSelected(true);

		float fracD = pb.getFloatParameter("FracD");
		spnrFracD.removeChangeListener(this);
		spnrFracD.setValue((Float) fracD);
		spnrFracD.addChangeListener(this);

		if (pb.getIntParameter("OutBit") == 0) butt8Bit.setSelected(true);
		if (pb.getIntParameter("OutBit") == 1) butt16Bit.setSelected(true);
		if (pb.getIntParameter("OutBit") == 2) buttRGB.setSelected(true);
	}
	
	
	private GridBagConstraints getGridBagConstraintsSize() {
		GridBagConstraints gridBagConstraintsSize = new GridBagConstraints();
		gridBagConstraintsSize.gridx = 0;
		gridBagConstraintsSize.gridy = 1;
		gridBagConstraintsSize.gridwidth = 2;
		gridBagConstraintsSize.insets = new Insets(5, 0, 0, 0); // top left bottom right
		return gridBagConstraintsSize;
	}
	
	private GridBagConstraints getGridBagConstraintsFracD() {
		GridBagConstraints gridBagConstraintsFracD = new GridBagConstraints();
		gridBagConstraintsFracD.gridx = 0;
		gridBagConstraintsFracD.gridy = 2;
		gridBagConstraintsFracD.gridwidth = 1;
		gridBagConstraintsFracD.insets = new Insets(0, 0, 0, 0); // top, left bottom right
		gridBagConstraintsFracD.anchor = GridBagConstraints.NORTH;
		return gridBagConstraintsFracD;
	}

	private GridBagConstraints getGridBagConstraintsButtonMethodGroup() {
		GridBagConstraints gridBagConstraintsButtonMethodGroup = new GridBagConstraints();
		gridBagConstraintsButtonMethodGroup.gridx = 1;
		gridBagConstraintsButtonMethodGroup.gridy = 2;
		gridBagConstraintsButtonMethodGroup.gridwidth = 1;
		gridBagConstraintsButtonMethodGroup.insets = new Insets(0, 0, 0, 0); // top left bottom right
		gridBagConstraintsButtonMethodGroup.anchor = GridBagConstraints.EAST;
		return gridBagConstraintsButtonMethodGroup;
	}

	private GridBagConstraints getGridBagConstraintsButtonOutBitGroup() {
		GridBagConstraints gridBagConstraintsButtonOutBitGroup = new GridBagConstraints();
		gridBagConstraintsButtonOutBitGroup.gridx = 0;
		gridBagConstraintsButtonOutBitGroup.gridy = 3;
		gridBagConstraintsButtonOutBitGroup.gridwidth = 2;
		gridBagConstraintsButtonOutBitGroup.insets = new Insets(0, 0, 0, 0); // top left bottom right
		gridBagConstraintsButtonOutBitGroup.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsButtonOutBitGroup;
	}

	/**
	 * This method updates the GUI. This method overrides OperationGUI
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		if (buttFFT.isSelected()) {
			spnrFracD.setEnabled(true);
			tbFracD.setTitleColor(Color.BLACK);
			pnlFracD.setBorder(tbFracD);
			repaint();
		}
		if (buttMidPointDis.isSelected()) {
			spnrFracD.setEnabled(true);
			tbFracD.setTitleColor(Color.BLACK);
			pnlFracD.setBorder(tbFracD);
			repaint();
		}
		if (buttSumSin.isSelected()) {
			spnrFracD.setEnabled(false);
			tbFracD.setTitleColor(Color.GRAY);
			pnlFracD.setBorder(tbFracD);
			repaint();
		}
		this.setParameterValuesToGUI();
	}

	/**
	 * This method initializes jJPanelWidth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelWidth() {
		if (jPanelWidth == null) {
			jPanelWidth = new JPanel();
			jPanelWidth.setLayout(new BorderLayout());
			jLabelWidth = new JLabel("Width: ");
			jLabelWidth.setPreferredSize(new Dimension(50, 20));
			jLabelWidth.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerWidth = new JSpinner(sModel);
			jSpinnerWidth.setPreferredSize(new Dimension(60, 20));
			jSpinnerWidth.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerWidth.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			jFormattedTextFieldWidth = ftf;
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelWidth.add(jLabelWidth, BorderLayout.WEST);
			jPanelWidth.add(jSpinnerWidth, BorderLayout.CENTER);
		}
		return jPanelWidth;
	}

	/**
	 * This method initializes jJPanelHeight
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelHeight() {
		if (jPanelHeight == null) {
			jPanelHeight = new JPanel();
			jPanelHeight.setLayout(new BorderLayout());
			jLabelHeight = new JLabel("Height: ");
			jLabelHeight.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelHeight.setPreferredSize(new Dimension(50, 20));
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerHeight = new JSpinner(sModel);
			jSpinnerHeight.setPreferredSize(new Dimension(60, 20));
			jSpinnerHeight.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerHeight.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			jFormattedTextFieldHeight = ftf;
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")										// ;
			jPanelHeight.add(jLabelHeight, BorderLayout.WEST);
			jPanelHeight.add(jSpinnerHeight, BorderLayout.CENTER);
		}
		return jPanelHeight;
	}
	
	/**
	 * This method initializes jPanelSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSize() {
		if (jPanelSize == null) {
			jPanelSize = new JPanel();
			jPanelSize.setLayout(new BoxLayout(jPanelSize, BoxLayout.X_AXIS));
			jPanelSize.setBorder(new TitledBorder(null, "Image size", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelSize.add(getJPanelWidth());
			jPanelSize.add(getJPanelHeight());
			// jPanelSize.addSeparator();
		}
		return jPanelSize;
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtFFT() {
		if (buttFFT == null) {
			buttFFT = new JRadioButton();
			buttFFT.setText("FFT");
			buttFFT.setToolTipText("inverse FFT method");
			buttFFT.addActionListener(this);
			buttFFT.setActionCommand("parameter");
		}
		return buttFFT;
	}
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtMidPointDis() {
		if (buttMidPointDis == null) {
			buttMidPointDis = new JRadioButton();
			buttMidPointDis.setText("Midpoint Displacement");
			buttMidPointDis.setToolTipText("Midpoint displacement method");
			buttMidPointDis.addActionListener(this);
			buttMidPointDis.setActionCommand("parameter");
		}
		return buttMidPointDis;
	}
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtSumSin() {
		if (buttSumSin == null) {
			buttSumSin = new JRadioButton();
			buttSumSin.setText("Sum of Sine");
			buttSumSin.setToolTipText("Sum of sine functions");
			buttSumSin.addActionListener(this);
			buttSumSin.setActionCommand("parameter");
		}
		return buttSumSin;
	}

	/**
	 * This method initializes j
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		if (jPanelMethod == null) {	
			jPanelMethod = new JPanel();
			jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
			jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelMethod.add(getJRadioButtonButtFFT());
			jPanelMethod.add(getJRadioButtonButtMidPointDis());
			jPanelMethod.add(getJRadioButtonButtSumSin());
			// jPanelMethod.addSeparator();
			this.setButtonGroupMethod(); // Grouping of JRadioButtons
		}
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttFFT);
		buttGroupMethod.add(buttMidPointDis);
		buttGroupMethod.add(buttSumSin);
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes spnrFracD
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getPnlFracD() {
		if (pnlFracD == null) {
			pnlFracD = new JPanel();	
			pnlFracD.setLayout(new BoxLayout(pnlFracD, BoxLayout.X_AXIS));
			tbFracD = new TitledBorder(null, "FD", TitledBorder.LEADING, TitledBorder.TOP, null, null);
			tbFracD.setTitleColor(Color.BLACK);
			pnlFracD.setBorder(tbFracD);			
			spnrFracD = new JSpinner();
			spnrFracD.setModel(new SpinnerNumberModel(2.0d, 2.0d, 3.0d, 0.1d));
			spnrFracD.setEditor(new JSpinner.NumberEditor(spnrFracD, "#0.0"));
			spnrFracD.setToolTipText("desired fractal dimension");
			pnlFracD.add(spnrFracD);
		}
		return pnlFracD;
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonButt8Bit() {
		if (butt8Bit == null) {
			butt8Bit = new JRadioButton();
			butt8Bit.setText("8Bit");
			butt8Bit.setToolTipText("8 bit output image");
			butt8Bit.addActionListener(this);
			butt8Bit.setActionCommand("parameter");
		}
		return butt8Bit;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonButt16Bit() {
		if (butt16Bit == null) {
			butt16Bit = new JRadioButton();
			butt16Bit.setText("16Bit");
			butt16Bit.setToolTipText("16 bit output image");
			butt16Bit.addActionListener(this);
			butt16Bit.setActionCommand("parameter");
		}
		return butt16Bit;
	}
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButtonMenuItem
	 */
	private JRadioButton getJRadioButtonButtRGB() {
		if (buttRGB == null) {
			buttRGB = new JRadioButton();
			buttRGB.setText("RGB");
			buttRGB.setToolTipText("RGB output image");
			buttRGB.addActionListener(this);
			buttRGB.setActionCommand("parameter");
		}
		return buttRGB;
	}

	/**
	 * This method initializes jPanelOutbit
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOutBit() {
		if (jPanelOutBit == null) {
			jPanelOutBit = new JPanel();
			jPanelOutBit.setLayout(new BoxLayout(jPanelOutBit, BoxLayout.X_AXIS));
			jPanelOutBit.setBorder(new TitledBorder(null, "Output", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelOutBit.add(getJRadioButtonButt8Bit());
			jPanelOutBit.add(getJRadioButtonButt16Bit());
			jPanelOutBit.add(getJRadioButtonButtRGB());
			// jPanelOutBit.addSeparator();
			this.setButtonGroupOutBit(); // Grouping of JRadioButtons
		}
		return jPanelOutBit;
	}

	private void setButtonGroupOutBit() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupOutBit = new ButtonGroup();
		buttGroupOutBit.add(butt8Bit);
		buttGroupOutBit.add(butt16Bit);
		buttGroupOutBit.add(buttRGB);
	}
	
	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (buttFFT == e.getSource()) {		
			}
			if (buttMidPointDis == e.getSource()) {		
			}
			if (buttSumSin == e.getSource()) {	
			}	
			if (butt8Bit == e.getSource()) {	
			}
			if (this.butt16Bit == e.getSource()) {	
			}
			if (this.buttRGB == e.getSource()) {	
			}
			this.updateParameterBlock();
			this.update(); 
			
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	// -----------------------------------------------------------------------------------------------
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}

	// -------------------------------------------------------------------------------------------------
	@Override
	public void stateChanged(ChangeEvent e) {
		
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
}// END
