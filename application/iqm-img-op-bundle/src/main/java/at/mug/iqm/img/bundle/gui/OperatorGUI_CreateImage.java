package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_CreateImage.java
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
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpCreateImageDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2010 02
 * @update 2014 RGB
 */
@SuppressWarnings({ "unused" })
public class OperatorGUI_CreateImage extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8406476350988107618L;

	// class specific logger
	private static Class<?> caller = OperatorGUI_CreateImage.class;
	private static final Logger logger = LogManager.getLogger(OperatorGUI_CreateImage.class);

	/**
	 * The cached parameter block within the work package.
	 */
	private ParameterBlockIQM pb;

	private JPanel              jPanelWidth               = null;
	private JLabel              jLabelWidth               = null;
	private JFormattedTextField jFormattedTextFieldWidth  = null;
	private JSpinner            jSpinnerWidth             = null;
	private JPanel              jPanelHeight              = null;
	private JLabel              jLabelHeight              = null;
	private JFormattedTextField jFormattedTextFieldHeight = null;
	private JSpinner            jSpinnerHeight            = null;
	private JPanel              jPanelSize                = null;

	private JRadioButton buttRandom      = null;
	private JRadioButton buttGauss       = null;
	private JRadioButton buttConst       = null;
	private JRadioButton buttSin         = null;
	private JRadioButton buttCos         = null;
	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;

	private JPanel              jPanelConst1              = null;
	private JLabel              jLabelConst1              = null;
	private JFormattedTextField jFormattedTextFieldConst1 = null;
	private JSpinner            jSpinnerConst1            = null;
	
	private JPanel              jPanelConst2              = null;
	private JLabel              jLabelConst2              = null;
	private JFormattedTextField jFormattedTextFieldConst2 = null;
	private JSpinner            jSpinnerConst2            = null;
	
	private JPanel              jPanelConst3              = null;
	private JLabel              jLabelConst3              = null;
	private JFormattedTextField jFormattedTextFieldConst3 = null;
	private JSpinner            jSpinnerConst3            = null;
	private JPanel              jPanelConst				  = null;
	private TitledBorder        tbConst                   = null;
	
	private JFormattedTextField jFormattedTextFieldOmega = null;
	private JSpinner            jSpinnerOmega            = null;
	private JPanel              jPanelOmega              = null;
	private TitledBorder        tbOmega                  = null;

	private JRadioButton butt8Bit        = null;
	private JRadioButton butt16Bit       = null;
	private JRadioButton buttRGB         = null;
	private ButtonGroup  buttGroupOutBit = null;
	private JPanel       jPanelOutBit    = null;

	/**
	 * constructor
	 */
	public OperatorGUI_CreateImage() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpCreateImageDescriptor().getName());

		this.initialize();

		this.setTitle("Create Image");

		this.getOpGUIContent().setLayout(new GridBagLayout());
		this.getOpGUIContent().add(getJPanelSize(),     getGridBagConstraintsSize());
		this.getOpGUIContent().add(getJPanelMethod(),   getGridBagConstraintsButtonMethodGroup());
		this.getOpGUIContent().add(getJPanelConst(),    getGridBagConstraintsConst());
		this.getOpGUIContent().add(getJPanelOmega(),    getGridBagConstraintsOmega());
		this.getOpGUIContent().add(getJPanelOutBit(),   getGridBagConstraintsButtonOutBitGroup());

		this.pack();

	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		// get the parameters from the work package
		this.pb = this.workPackage.getParameters();
		logger.debug("setParameterValuesToGUI(): pb=" + pb);

		// there is no need for a current image
		if (pb.getIntParameter("Method") == IqmOpCreateImageDescriptor.CREATERANDOM)   buttRandom.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpCreateImageDescriptor.CREATEGAUSSIAN) buttGauss.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpCreateImageDescriptor.CREATECONSTANT) buttConst.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpCreateImageDescriptor.CREATECOSINUS)  buttSin.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpCreateImageDescriptor.CREATECOSINUS)  buttCos.setSelected(true);

		jSpinnerWidth.removeChangeListener(this);
		jSpinnerHeight.removeChangeListener(this);
		jSpinnerConst1.removeChangeListener(this);
		jSpinnerConst2.removeChangeListener(this);
		jSpinnerConst3.removeChangeListener(this);
		jSpinnerOmega.removeChangeListener(this);
		jSpinnerWidth.setValue(pb.getIntParameter("Width"));
		jSpinnerHeight.setValue(pb.getIntParameter("Height"));
		jSpinnerConst1.setValue(pb.getIntParameter("Const1"));
		jSpinnerConst2.setValue(pb.getIntParameter("Const2"));
		jSpinnerConst3.setValue(pb.getIntParameter("Const3"));
		jSpinnerOmega.setValue(pb.getIntParameter("Omega"));
		jSpinnerWidth.addChangeListener(this);
		jSpinnerHeight.addChangeListener(this);
		jSpinnerConst1.addChangeListener(this);
		jSpinnerConst2.addChangeListener(this);
		jSpinnerConst3.addChangeListener(this);
		jSpinnerOmega.addChangeListener(this);

		if (buttConst.isSelected()) {
			jSpinnerConst1.setEnabled(true);
			if (buttRGB.isEnabled() == true){
				jSpinnerConst2.setEnabled(true);
				jSpinnerConst2.setEnabled(true);
			} else{
				jSpinnerConst2.setEnabled(false);
				jSpinnerConst2.setEnabled(false);
			}
		
		} else {
			jSpinnerConst1.setEnabled(false);
			jSpinnerConst2.setEnabled(false);
			jSpinnerConst3.setEnabled(false);
		}
		if (buttSin.isSelected() || buttCos.isSelected()) {
			jSpinnerOmega.setEnabled(true);
		} else {
			jSpinnerOmega.setEnabled(false);
		}

		if (pb.getIntParameter("OutBit") == 0) butt8Bit.setSelected(true);
		if (pb.getIntParameter("OutBit") == 1) butt16Bit.setSelected(true);
		if (pb.getIntParameter("OutBit") == 2) buttRGB.setSelected(true);

	}

	@Override
	public void updateParameterBlock() {
		logger.debug("updateParameterBlock(): pb=" + pb);

		pb.setParameter("Width", ((Number) jSpinnerWidth.getValue()).intValue());
		pb.setParameter("Height",
				((Number) jSpinnerHeight.getValue()).intValue());

		if (buttRandom.isSelected()) pb.setParameter("Method", IqmOpCreateImageDescriptor.CREATERANDOM);
		if (buttGauss.isSelected())  pb.setParameter("Method", IqmOpCreateImageDescriptor.CREATEGAUSSIAN);
		if (buttConst.isSelected())  pb.setParameter("Method", IqmOpCreateImageDescriptor.CREATECONSTANT);
		if (buttSin.isSelected())    pb.setParameter("Method", IqmOpCreateImageDescriptor.CREATESINUS);
		if (buttCos.isSelected())    pb.setParameter("Method", IqmOpCreateImageDescriptor.CREATECOSINUS);

		pb.setParameter("Const1", ((Number) jSpinnerConst1.getValue()).intValue());
		pb.setParameter("Const2", ((Number) jSpinnerConst2.getValue()).intValue());
		pb.setParameter("Const3", ((Number) jSpinnerConst3.getValue()).intValue());
		pb.setParameter("Omega", ((Number) jSpinnerOmega.getValue()).intValue());

		if (butt8Bit.isSelected())  pb.setParameter("OutBit", 0);
		if (butt16Bit.isSelected()) pb.setParameter("OutBit", 1);
		if (buttRGB.isSelected())   pb.setParameter("OutBit", 2);

	}

	private GridBagConstraints getGridBagConstraintsSize() {
		GridBagConstraints gridBagConstraintsSize = new GridBagConstraints();
		gridBagConstraintsSize.gridx = 0;
		gridBagConstraintsSize.gridy = 0;
		gridBagConstraintsSize.gridwidth = 2;
		gridBagConstraintsSize.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gridBagConstraintsSize.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsSize;
	}
	private GridBagConstraints getGridBagConstraintsButtonMethodGroup() {
		GridBagConstraints gridBagConstraintsButtonMethodGroup = new GridBagConstraints();
		//gridBagConstraintsButtonMethodGroup.anchor = GridBagConstraints.NORTH;
		gridBagConstraintsButtonMethodGroup.gridx = 0;
		gridBagConstraintsButtonMethodGroup.gridy = 1;
		gridBagConstraintsButtonMethodGroup.gridwidth = 2;
		gridBagConstraintsButtonMethodGroup.insets = new Insets(5, 0, 0, 0); // top left bottom right
		//gridBagConstraintsButtonMethodGroup.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsButtonMethodGroup;
	}

	private GridBagConstraints getGridBagConstraintsConst() {
		GridBagConstraints gridBagConstraintsConst = new GridBagConstraints();
		gridBagConstraintsConst.gridx = 0;
		gridBagConstraintsConst.gridy = 2;
		gridBagConstraintsConst.gridwidth = 1;
		gridBagConstraintsConst.insets = new Insets(5, 0, 0, 0); // top left bottom right
		return gridBagConstraintsConst;
	}

	private GridBagConstraints getGridBagConstraintsOmega() {
		GridBagConstraints gridBagConstraintsOmega = new GridBagConstraints();
		gridBagConstraintsOmega.gridx = 1;
		gridBagConstraintsOmega.gridy = 2;
		gridBagConstraintsOmega.gridwidth = 1;
		gridBagConstraintsOmega.anchor = GridBagConstraints.NORTH;
		gridBagConstraintsOmega.insets = new Insets(5, 0, 0, 0); // top left bottom right
		return gridBagConstraintsOmega;
	}

	private GridBagConstraints getGridBagConstraintsButtonOutBitGroup() {
		GridBagConstraints gridBagConstraintsButtonOutBitGroup = new GridBagConstraints();
		gridBagConstraintsButtonOutBitGroup.anchor = GridBagConstraints.NORTH;
		gridBagConstraintsButtonOutBitGroup.gridx = 0;
		gridBagConstraintsButtonOutBitGroup.gridy = 5;
		gridBagConstraintsButtonOutBitGroup.gridwidth = 2;
		gridBagConstraintsButtonOutBitGroup.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gridBagConstraintsButtonOutBitGroup.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsButtonOutBitGroup;
	}

	/**
	 * This method updates the GUI, if needed.
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
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
			jLabelWidth.setPreferredSize(new Dimension(50, 22));
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
			jLabelHeight.setPreferredSize(new Dimension(50, 22));
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerHeight = new JSpinner(sModel);
			jSpinnerHeight.setPreferredSize(new Dimension(60, 20));
			jSpinnerHeight.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerHeight.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			jFormattedTextFieldHeight = ftf;
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
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
	private JRadioButton getJRadioButtonButtRandom() {
		if (buttRandom == null) {
			buttRandom = new JRadioButton();
			buttRandom.setText("Random");
			buttRandom.setToolTipText("randomly distributed grey values");
			buttRandom.addActionListener(this);
			buttRandom.setActionCommand("parameter");
		}
		return buttRandom;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtGauss() {
		if (buttGauss == null) {
			buttGauss = new JRadioButton();
			buttGauss.setText("Gauss");
			buttGauss.setToolTipText("gaussian distributed grey values");
			buttGauss.addActionListener(this);
			buttGauss.setActionCommand("parameter");
		}
		return buttGauss;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtConst() {
		if (buttConst == null) {
			buttConst = new JRadioButton();
			buttConst.setText("Constant");
			buttConst.setToolTipText("constant grey values");
			buttConst.addActionListener(this);
			buttConst.setActionCommand("parameter");
		}
		return buttConst;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtSin() {
		if (buttSin == null) {
			buttSin = new JRadioButton();
			buttSin.setText("Sin");
			buttSin.setToolTipText("sinus shaped grey values");
			buttSin.addActionListener(this);
			buttSin.setActionCommand("parameter");
		}
		return buttSin;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtCos() {
		if (buttCos == null) {
			buttCos = new JRadioButton();
			buttCos.setText("Cos");
			buttCos.setToolTipText("cosinus shaped grey values");
			buttCos.addActionListener(this);
			buttCos.setActionCommand("parameter");
		}
		return buttCos;
	}

	/**
	 * This method initializes jJPanelMethod
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		if (jPanelMethod == null) {
			jPanelMethod = new JPanel();
			jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
			//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelMethod.add(getJRadioButtonButtRandom());
			jPanelMethod.add(getJRadioButtonButtGauss());
			jPanelMethod.add(getJRadioButtonButtConst());
			jPanelMethod.add(getJRadioButtonButtSin());
			jPanelMethod.add(getJRadioButtonButtCos());
			// jPanelMethod.addSeparator();
			this.setButtonGroupMethod(); // Grouping of JRadioButtons
		}
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttRandom);
		buttGroupMethod.add(buttGauss);
		buttGroupMethod.add(buttConst);
		buttGroupMethod.add(buttSin);
		buttGroupMethod.add(buttCos);
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * This method initializes jJPanelConst1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelConst1() {
		if (jPanelConst1 == null) {
			jPanelConst1 = new JPanel();
			jPanelConst1.setLayout(new BorderLayout());
			jLabelConst1 = new JLabel("grey ");
			jLabelConst1.setForeground(Color.GRAY);
			jLabelConst1.setPreferredSize(new Dimension(50, 22));
			jLabelConst1.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(127, 0, 255, 1); // init, min, max, step
			jSpinnerConst1 = new JSpinner(sModel);
			jSpinnerConst1.setPreferredSize(new Dimension(60, 20));
			jSpinnerConst1.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerConst1.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			jFormattedTextFieldConst1 = ftf;
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")							
			jPanelConst1.add(jLabelConst1, BorderLayout.WEST);
			jPanelConst1.add(jSpinnerConst1, BorderLayout.CENTER);
		}
		return jPanelConst1;
	}
	/**
	 * This method initializes jJPanelConst2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelConst2() {
		if (jPanelConst2 == null) {
			jPanelConst2 = new JPanel();
			jPanelConst2.setLayout(new BorderLayout());
			jLabelConst2 = new JLabel("G ");
			jLabelConst2.setForeground(Color.GRAY);
			jLabelConst2.setPreferredSize(new Dimension(50, 20));
			jLabelConst2.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(127, 0, 255, 1); // init, min, max, step
			jSpinnerConst2 = new JSpinner(sModel);
			jSpinnerConst2.setPreferredSize(new Dimension(60, 20));
			jSpinnerConst2.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerConst2.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			jFormattedTextFieldConst2 = ftf;
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")								
			jPanelConst2.add(jLabelConst2, BorderLayout.WEST);
			jPanelConst2.add(jSpinnerConst2, BorderLayout.CENTER);
		}
		return jPanelConst2;
	}
	/**
	 * This method initializes jJPanelConst3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelConst3() {
		if (jPanelConst3 == null) {
			jPanelConst3 = new JPanel();
			jPanelConst3.setLayout(new BorderLayout());
			jLabelConst3 = new JLabel("B ");
			jLabelConst3.setForeground(Color.GRAY);
			jLabelConst3.setPreferredSize(new Dimension(50, 22));
			jLabelConst3.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(127, 0, 255, 1); // init, min, max, step
			jSpinnerConst3 = new JSpinner(sModel);
			jSpinnerConst3.setPreferredSize(new Dimension(60, 20));
			jSpinnerConst3.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerConst3.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			jFormattedTextFieldConst3 = ftf;
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")									
			jPanelConst3.add(jLabelConst3, BorderLayout.WEST);
			jPanelConst3.add(jSpinnerConst3, BorderLayout.CENTER);
		}
		return jPanelConst3;
	}
	
	/**
	 * This method initializes jPanelConst
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelConst() {
		if (jPanelConst == null) {
			jPanelConst = new JPanel();
			jPanelConst.setLayout(new BoxLayout(jPanelConst, BoxLayout.Y_AXIS));
			tbConst = new TitledBorder(null, "Const", TitledBorder.LEADING, TitledBorder.TOP, null, null);
			tbConst.setTitleColor(Color.GRAY);
			jPanelConst.setBorder(tbConst);	
			jPanelConst.add(getJPanelConst1());
			jPanelConst.add(getJPanelConst2());
			jPanelConst.add(getJPanelConst3());
			// jPanelConst.addSeparator();
		}
		return jPanelConst;
	}
	

	/**
	 * This method initializes jJPanelOmega
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOmega() {
		if (jPanelOmega == null) {
			jPanelOmega = new JPanel();
			jPanelOmega.setLayout(new BoxLayout(jPanelOmega, BoxLayout.Y_AXIS));
			//jPanelOmega.setBorder(new TitledBorder(null, "Omega", TitledBorder.LEADING, TitledBorder.TOP, null, null));			
			tbOmega = new TitledBorder(null, "Omega", TitledBorder.LEADING, TitledBorder.TOP, null, null);
			tbOmega.setTitleColor(Color.GRAY);
			jPanelOmega.setBorder(tbOmega);		
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerOmega = new JSpinner(sModel);
			jSpinnerOmega.setPreferredSize(new Dimension(60, 20));
			jSpinnerOmega.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerOmega.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			jFormattedTextFieldOmega = ftf;
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
												
			jPanelOmega.add(jSpinnerOmega, BorderLayout.CENTER);
		}
		return jPanelOmega;
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
			if (buttConst.isSelected()) {
				jSpinnerConst1.setEnabled(true);
				tbConst.setTitleColor(Color.BLACK);
				jPanelConst.setBorder(tbConst);	
				repaint();
				if (buttRGB.isSelected() == true){
					jLabelConst1.setText("R ");
					jLabelConst1.setForeground(Color.BLACK);
					jLabelConst2.setForeground(Color.BLACK);
					jLabelConst3.setForeground(Color.BLACK);
					repaint();
					jSpinnerConst2.setEnabled(true);
					jSpinnerConst3.setEnabled(true);
				} else{
					jLabelConst1.setText("Grey ");
					jLabelConst1.setForeground(Color.BLACK);
					jLabelConst2.setForeground(Color.GRAY);
					jLabelConst3.setForeground(Color.GRAY);
					repaint();
					jSpinnerConst2.setEnabled(false);
					jSpinnerConst3.setEnabled(false);
				}
			} else {
				tbConst.setTitleColor(Color.LIGHT_GRAY);
				jPanelConst.setBorder(tbConst);	
				jLabelConst1.setForeground(Color.GRAY);
				jLabelConst2.setForeground(Color.GRAY);
				jLabelConst3.setForeground(Color.GRAY);
				repaint();
				jSpinnerConst1.setEnabled(false);
				jSpinnerConst2.setEnabled(false);
				jSpinnerConst3.setEnabled(false);
			}
			if (buttSin.isSelected() || buttCos.isSelected()) {
				jSpinnerOmega.setEnabled(true);
				tbOmega.setTitleColor(Color.BLACK);
				jPanelOmega.setBorder(tbOmega);	
				repaint();
			} else {
				jSpinnerOmega.setEnabled(false);
				tbOmega.setTitleColor(Color.GRAY);
				jPanelOmega.setBorder(tbOmega);
				repaint();
			}

			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	// -------------------------------------------------------------------------------------------------
	@Override
	public void stateChanged(ChangeEvent e) {
		
		jSpinnerConst1.removeChangeListener(this);
		jSpinnerConst2.removeChangeListener(this);
		jSpinnerConst3.removeChangeListener(this);
		
		int const1 = ((Number) jSpinnerConst1.getValue()).intValue();
		int const2 = ((Number) jSpinnerConst2.getValue()).intValue();
		int const3 = ((Number) jSpinnerConst3.getValue()).intValue();
		
		if (const1 > 255) jSpinnerConst1.setValue(255);
		if (const2 > 255) jSpinnerConst2.setValue(255);
		if (const3 > 255) jSpinnerConst3.setValue(255);
		if (const1 < 0)   jSpinnerConst1.setValue(0);
		if (const2 < 0)   jSpinnerConst2.setValue(0);
		if (const3 < 0)   jSpinnerConst3.setValue(0);
			
		jSpinnerConst1.addChangeListener(this);
		jSpinnerConst2.addChangeListener(this);
		jSpinnerConst3.addChangeListener(this);
			
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

}// END
