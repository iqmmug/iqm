package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Border.java
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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.img.bundle.descriptors.IqmOpBorderDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 06
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_Border extends AbstractImageOperatorGUI
                                implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -480694124676367697L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_Border.class);

	private ParameterBlockIQM pb = null;

	private boolean isLastTimeChangedLeft = false; // initially left will be changed at first when  newWidth is changed
	private boolean isLastTimeChangedTop  = false; // initially top will be  changed at first when  newHeightis changed

	private JPanel              jPanelOldWidth               = null;
	private JLabel              jLabelOldWidth               = null;
	private JFormattedTextField jFormattedTextFieldOldWidth  = null;
	private JPanel              jPanelOldHeight              = null;
	private JLabel              jLabelOldHeight              = null;
	private JFormattedTextField jFormattedTextFieldOldHeight = null;	
	private JPanel              jPanelOldSize                = null;

	private JPanel   jPanelLeft      = null;
	private JLabel   jLabelLeft      = null;
	private JSpinner jSpinnerLeft    = null;
	private JPanel   jPanelRight     = null;
	private JLabel   jLabelRight     = null;
	private JSpinner jSpinnerRight   = null;
	private JPanel   jPanelTop       = null;
	private JLabel   jLabelTop       = null;
	private JSpinner jSpinnerTop     = null;
	private JPanel   jPanelBottom    = null;
	private JLabel   jLabelBottom    = null;
	private JSpinner jSpinnerBottom  = null;	
	private JPanel   jPanelBorder    = null;

	private JPanel   jPanelNewWidth    = null;
	private JLabel   jLabelNewWidth    = null;
	private JSpinner jSpinnerNewWidth  = null;
	private JPanel   jPanelNewHeight   = null;
	private JLabel   jLabelNewHeight   = null;
	private JSpinner jSpinnerNewHeight = null;
	private JPanel   jPanelNewSize     = null;

	private JButton jButtonMaxImgSize = null; // to get and set maximal image imgWidth and imgHeight in an image stack

	private JPanel       jPanelConst   = null;
	private JLabel       jLabelConst   = null;
	private JSpinner     jSpinnerConst = null;
	private TitledBorder tbConst       = null;

	private JRadioButton buttZero        = null;
	private JRadioButton buttConst       = null;
	private JRadioButton buttCopy        = null;
	private JRadioButton buttReflect     = null;
	private JRadioButton buttWrap        = null;
	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;

	private JRadioButton buttBorder             = null; // Preference for image stacks without same image sizes
	private JRadioButton buttSize               = null;
	private JPanel       jPanelBorderOrSize     = null;
	private ButtonGroup  buttGroupBorderOrSize  = null;

	/**
	 * constructor
	 */
	public OperatorGUI_Border() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpBorderDescriptor().getName());
		this.initialize();
		this.setTitle("Add Border");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelOldSize(),     getGridBagConstraints_OldSize());
		this.getOpGUIContent().add(getJPanelBorder(),      getGridBagConstraints_Border());
		this.getOpGUIContent().add(getJPanelNewSize(),     getGridBagConstraints_NewSize());
		this.getOpGUIContent().add(getJPanelMethod(),      getGridBagConstraints_ButtonMethodGroup());
		this.getOpGUIContent().add(getJPanelConst(),       getGridBagConstraints_Const());
		this.getOpGUIContent().add(getJPanelBorderOrSize(),getGridBagConstraints_BorderOrSize());

		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_OldSize() {
		GridBagConstraints gbc_OldSize = new GridBagConstraints();
		gbc_OldSize.gridx = 0;
		gbc_OldSize.gridy = 0;
		gbc_OldSize.insets = new Insets(10, 0, 0, 0); // top  left  bottom  right
		gbc_OldSize.gridwidth = 2;
		gbc_OldSize.fill = GridBagConstraints.HORIZONTAL;
		//gbc_OldSize.anchor = GridBagConstraints.WEST;
		return gbc_OldSize;
	}
	

	private GridBagConstraints getGridBagConstraints_Border() {
		GridBagConstraints gbc_Border = new GridBagConstraints();
		gbc_Border.gridx = 0;
		gbc_Border.gridy = 1;
		gbc_Border.gridwidth = 2;
		gbc_Border.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		//gbc_Border.fill = GridBagConstraints.HORIZONTAL;
		return gbc_Border;
	}
	
	private GridBagConstraints getGridBagConstraints_NewSize() {
		GridBagConstraints gbc_NewSize = new GridBagConstraints();
		gbc_NewSize.gridx = 0;
		gbc_NewSize.gridy = 2;
		gbc_NewSize.gridwidth = 2;
		gbc_NewSize.insets = new Insets(5, 0, 0, 0); // top  left  bottom  right
		gbc_NewSize.fill = GridBagConstraints.HORIZONTAL;
		return gbc_NewSize;
	}
		
	private GridBagConstraints getGridBagConstraints_ButtonMethodGroup() {
		GridBagConstraints gbc_ButtonMethodGroup = new GridBagConstraints();
		gbc_ButtonMethodGroup.gridx = 0;
		gbc_ButtonMethodGroup.gridy = 4;
		gbc_ButtonMethodGroup.gridwidth = 1;
		gbc_ButtonMethodGroup.insets = new Insets(5, 30, 0, 0); // top left bottom right
		//gridBagConstraintsButtonMethodGroup.fill = GridBagConstraints.BOTH;
		//gridBagConstraintsButtonMethodGroup.anchor = GridBagConstraints.CENTER;
		return gbc_ButtonMethodGroup;
	}
	
	private GridBagConstraints getGridBagConstraints_Const() {
		GridBagConstraints gbc_Const = new GridBagConstraints();
		gbc_Const.gridx = 1;
		gbc_Const.gridy = 4;
		gbc_Const.gridwidth = 1;
		gbc_Const.insets = new Insets(5, 0, 0, 0); // top left bottom right
		// gridBagConstraintsConst.fill = GridBagConstraints.BOTH;
		return gbc_Const;
	}
	
	private GridBagConstraints getGridBagConstraints_BorderOrSize() {
		GridBagConstraints gbc_BorderOrSizeGroup = new GridBagConstraints();
		gbc_BorderOrSizeGroup.gridx = 0;
		gbc_BorderOrSizeGroup.gridy = 5;
		gbc_BorderOrSizeGroup.gridwidth = 2;
		gbc_BorderOrSizeGroup.insets = new Insets(5, 0, 0, 0);
		gbc_BorderOrSizeGroup.fill = GridBagConstraints.HORIZONTAL;
		return gbc_BorderOrSizeGroup;
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		pb.setParameter("Left",  ((Number) jSpinnerLeft.getValue()).intValue());
		pb.setParameter("Right", ((Number) jSpinnerRight.getValue()).intValue());
		pb.setParameter("Top",   ((Number) jSpinnerTop.getValue()).intValue());
		pb.setParameter("Bottom",((Number) jSpinnerBottom.getValue()).intValue());
		pb.setParameter("Const", ((Number) jSpinnerConst.getValue()).intValue());

		pb.setParameter("NewWidth",  ((Number) jSpinnerNewWidth.getValue()).intValue());
		pb.setParameter("NewHeight", ((Number) jSpinnerNewHeight.getValue()).intValue());

		if (buttZero.isSelected())    pb.setParameter("Method", IqmOpBorderDescriptor.ZERO);
		if (buttConst.isSelected())   pb.setParameter("Method", IqmOpBorderDescriptor.CONSTANT);
		if (buttCopy.isSelected())    pb.setParameter("Method", IqmOpBorderDescriptor.COPY);
		if (buttReflect.isSelected()) pb.setParameter("Method", IqmOpBorderDescriptor.REFLECT);
		if (buttWrap.isSelected())    pb.setParameter("Method", IqmOpBorderDescriptor.WRAP);
		
		if (buttBorder.isSelected())  pb.setParameter("BorderOrSize", IqmOpBorderDescriptor.PREFERENCE_BORDER);
		if (buttSize.isSelected())    pb.setParameter("BorderOrSize", IqmOpBorderDescriptor.PREFERENCE_SIZE);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		jSpinnerLeft.removeChangeListener(this);
		jSpinnerRight.removeChangeListener(this);
		jSpinnerTop.removeChangeListener(this);
		jSpinnerBottom.removeChangeListener(this);
		jSpinnerConst.removeChangeListener(this);

		jSpinnerLeft.setValue(pb.getIntParameter("Left"));
		jSpinnerRight.setValue(pb.getIntParameter("Right"));
		jSpinnerTop.setValue(pb.getIntParameter("Top"));
		jSpinnerBottom.setValue(pb.getIntParameter("Bottom"));
		jSpinnerConst.setValue(pb.getIntParameter("Const"));

		jSpinnerLeft.addChangeListener(this);
		jSpinnerRight.addChangeListener(this);
		jSpinnerTop.addChangeListener(this);
		jSpinnerBottom.addChangeListener(this);
		jSpinnerConst.addChangeListener(this);

		jSpinnerNewWidth.removeChangeListener(this);
		jSpinnerNewHeight.removeChangeListener(this);
		jSpinnerNewWidth.setValue(pb.getIntParameter("NewWidth"));
		jSpinnerNewHeight.setValue(pb.getIntParameter("NewHeight"));
		jSpinnerNewWidth.addChangeListener(this);
		jSpinnerNewHeight.addChangeListener(this);

		if (pb.getIntParameter("Method") == IqmOpBorderDescriptor.ZERO)     buttZero.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpBorderDescriptor.CONSTANT) buttConst.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpBorderDescriptor.COPY)     buttCopy.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpBorderDescriptor.REFLECT)  buttReflect.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpBorderDescriptor.WRAP)     buttWrap.setSelected(true);

		if (buttConst.isSelected()) {
			jSpinnerConst.setEnabled(true);
		} else {
			jSpinnerConst.setEnabled(false);
		}

		if (pb.getIntParameter("BorderOrSize") == IqmOpBorderDescriptor.PREFERENCE_BORDER) buttBorder.setSelected(true);
		if (pb.getIntParameter("BorderOrSize") == IqmOpBorderDescriptor.PREFERENCE_SIZE)   buttSize.setSelected(true);

	}

	/**
	 * This method updates the GUI, if needed. This method overrides
	 * OperationGUI
	 */
	@Override
	public void update() {
		// get the first element and set the values according to its
		// information
		IqmDataBox iqmDataBox = (IqmDataBox) this.workPackage.getSources().get(0);
		PlanarImage pi = iqmDataBox.getImage();

		jFormattedTextFieldOldWidth.setValue(pi.getWidth());
		jFormattedTextFieldOldHeight.setValue(pi.getHeight());

		int left   = ((Number) jSpinnerLeft.getValue()).intValue();
		int right  = ((Number) jSpinnerRight.getValue()).intValue();
		int top    = ((Number) jSpinnerTop.getValue()).intValue();
		int bottom = ((Number) jSpinnerBottom.getValue()).intValue();

		// Set image dependent initial values;
		jSpinnerNewWidth.removeChangeListener(this);
		jSpinnerNewWidth.setValue(pi.getWidth() + left + right);
		jSpinnerNewWidth.addChangeListener(this);
		jSpinnerNewHeight.removeChangeListener(this);
		jSpinnerNewHeight.setValue(pi.getHeight() + top + bottom);
		jSpinnerNewHeight.addChangeListener(this);
		
		if (buttConst.isSelected()){
			tbConst.setTitleColor(Color.BLACK);
			jLabelConst.setEnabled(true);
			jSpinnerConst.setEnabled(true);
		} else {
			tbConst.setTitleColor(Color.GRAY);
			jLabelConst.setEnabled(false);
			jSpinnerConst.setEnabled(false);
		}
		this.repaint(); //because of tb TitledBorder cahnge of color
		

		this.updateParameterBlock();
		this.setParameterValuesToGUI();
	}



	/**
	 * This method initializes jJPanelOldWidth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOldWidth() {
		if (jPanelOldWidth == null) {
			jPanelOldWidth = new JPanel();
			jPanelOldWidth.setLayout(new BorderLayout());
			// jPanelOldWidth.setPreferredSize(new Dimension(250,18));
			jLabelOldWidth = new JLabel("Width: ");
			//jLabelOldWidth.setPreferredSize(new Dimension(65, 20));
			//jLabelOldWidth.setHorizontalAlignment(SwingConstants.RIGHT);
			jFormattedTextFieldOldWidth = new JFormattedTextField(NumberFormat.getNumberInstance());
			// jFormattedTextFieldOldWidth.setPreferredSize(new Dimension(70,18));
			// jFormattedTextFieldOldWidth.addPropertyChangeListener("value",/ this);
			// jFormattedTextFieldOldWidth.setInputVerifier(new IntNumberVerifier());
			jFormattedTextFieldOldWidth.setEditable(false);
			// jFormattedTextFieldOldWidth.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
			jFormattedTextFieldOldWidth.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) jFormattedTextFieldOldWidth.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelOldWidth.add(jLabelOldWidth, BorderLayout.WEST);
			jPanelOldWidth.add(jFormattedTextFieldOldWidth, BorderLayout.CENTER);

		}
		return jPanelOldWidth;
	}

	/**
	 * This method initializes jJPanelOldHeight
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOldHeight() {
		if (jPanelOldHeight == null) {
			jPanelOldHeight = new JPanel();
			jPanelOldHeight.setLayout(new BorderLayout());
			// jPanelOldHeight.setPreferredSize(new Dimension(250,18));
			jLabelOldHeight = new JLabel("Height: ");
			//jLabelOldHeight.setPreferredSize(new Dimension(65, 20));
			//jLabelOldHeight.setHorizontalAlignment(SwingConstants.RIGHT);
			jFormattedTextFieldOldHeight = new JFormattedTextField(NumberFormat.getNumberInstance());
			// jFormattedTextFieldOldHeight.setPreferredSize(new Dimension(70,18));
			// jFormattedTextFieldOldHeight.addPropertyChangeListener("value", this);
			// jFormattedTextFieldOldHeight.setInputVerifier(new IntNumberVerifier());
			jFormattedTextFieldOldHeight.setEditable(false);
			// jFormattedTextFieldOldHeight.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
			jFormattedTextFieldOldHeight.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) jFormattedTextFieldOldHeight.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelOldHeight.add(jLabelOldHeight, BorderLayout.WEST);
			jPanelOldHeight.add(jFormattedTextFieldOldHeight, BorderLayout.CENTER);
			jFormattedTextFieldOldHeight.setEnabled(true);
		}
		return jPanelOldHeight;
	}
	
	/**
	 * This method initializes jJPanelOldsize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOldSize() {
		if (jPanelOldSize == null) {
			jPanelOldSize = new JPanel();
			//jPanelOldSize.setLayout(new BoxLayout(jPanelOldSize, BoxLayout.X_AXIS));
			jPanelOldSize.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
			jPanelOldSize.setBorder(new TitledBorder(null, "Original size", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelOldSize.add(this.getJPanelOldWidth());
			jPanelOldSize.add(this.getJPanelOldHeight());	
		}
		return jPanelOldSize;
	}

	/**
	 * This method initializes jJPanelLeft
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelLeft() {
		if (jPanelLeft == null) {
			jPanelLeft = new JPanel();
			jPanelLeft.setLayout(new BorderLayout());
			jLabelLeft = new JLabel("Left: ");
			//jLabelLeft.setPreferredSize(new Dimension(50, 20));
			jLabelLeft.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerLeft = new JSpinner(sModel);
			//jSpinnerLeft.setPreferredSize(new Dimension(60, 20));
			jSpinnerLeft.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerLeft.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelLeft.add(jLabelLeft, BorderLayout.WEST);
			jPanelLeft.add(jSpinnerLeft, BorderLayout.CENTER);
		}
		return jPanelLeft;
	}

	/**
	 * This method initializes jJPanelRight
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRight() {
		if (jPanelRight == null) {
			jPanelRight = new JPanel();
			jPanelRight.setLayout(new BorderLayout());
			jLabelRight = new JLabel("Right: ");
			//jLabelRight.setPreferredSize(new Dimension(40, 20));
			jLabelRight.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerRight = new JSpinner(sModel);
			//jSpinnerRight.setPreferredSize(new Dimension(60, 20));
			jSpinnerRight.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRight.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")

			jPanelRight.add(jLabelRight, BorderLayout.WEST);
			jPanelRight.add(jSpinnerRight, BorderLayout.CENTER);
		}
		return jPanelRight;
	}

	/**
	 * This method initializes jJPanelTop
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelTop() {
		if (jPanelTop == null) {
			jPanelTop = new JPanel();
			jPanelTop.setLayout(new BorderLayout());
			jLabelTop = new JLabel("Top: ");
			//jLabelTop.setPreferredSize(new Dimension(45, 20));
			jLabelTop.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerTop = new JSpinner(sModel);
			//jSpinnerTop.setPreferredSize(new Dimension(60, 20));
			jSpinnerTop.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerTop.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")

			jPanelTop.add(jLabelTop, BorderLayout.WEST);
			jPanelTop.add(jSpinnerTop, BorderLayout.CENTER);
		}
		return jPanelTop;
	}

	/**
	 * This method initializes jJPanelBottom
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBottom() {
		if (jPanelBottom == null) {
			jPanelBottom = new JPanel();
			jPanelBottom.setLayout(new BorderLayout());
			jLabelBottom = new JLabel("Bottom: ");
			//jLabelBottom.setPreferredSize(new Dimension(50, 20));
			jLabelBottom.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerBottom = new JSpinner(sModel);
			//jSpinnerBottom.setPreferredSize(new Dimension(60, 20));
			jSpinnerBottom.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerBottom.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelBottom.add(jLabelBottom, BorderLayout.WEST);
			jPanelBottom.add(jSpinnerBottom, BorderLayout.CENTER);
		}
		return jPanelBottom;
	}
	
	/**
	 * This method initializes jJPanelBorder
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBorder() {
		if (jPanelBorder == null) {
			jPanelBorder = new JPanel();
			jPanelBorder.setLayout(new GridBagLayout());
			//jPanelBorder.setLayout(new BoxLayout(jPanelBorder, BoxLayout.Y_AXIS));
			//jPanelBorder.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));	
			jPanelBorder.setBorder(new TitledBorder(null, "Border", TitledBorder.LEADING, TitledBorder.TOP, null, null));
	
			GridBagConstraints gbc_Top= new GridBagConstraints();
			gbc_Top.gridx = 1;
			gbc_Top.gridy = 0;
			gbc_Top.gridwidth = 1;
			gbc_Top.insets = new Insets(5, 0, 0, 0); // top  left  bottom  right
			//gbc_Top.fill = GridBagConstraints.BOTH;
			
			GridBagConstraints gbc_Left = new GridBagConstraints();
			gbc_Left.gridx = 0;
			gbc_Left.gridy = 1;
			gbc_Left.gridwidth = 1;
			gbc_Left.insets = new Insets(10, 0, 0, 0); // top  left  bottom  right
			//gbc_Left.fill = GridBagConstraints.BOTH;
			
			GridBagConstraints gbc_Right = new GridBagConstraints();
			gbc_Right.gridx = 2;
			gbc_Right.gridy = 1;
			gbc_Right.gridwidth = 1;
			gbc_Right.insets = new Insets(10, 0, 0, 0); // top  left  bottom  right
			//gbc_Right.fill = GridBagConstraints.BOTH;
			
			GridBagConstraints gbc_Bottom = new GridBagConstraints();
			gbc_Bottom.gridx = 1;
			gbc_Bottom.gridy = 2;
			gbc_Bottom.gridwidth = 1;
			gbc_Bottom.insets = new Insets(10, 0, 5, 0); // top  left  bottom  right
			//gbc_Bottom.fill = GridBagConstraints.BOTH;
		
			jPanelBorder.add(this.getJPanelTop(),    gbc_Top);
			jPanelBorder.add(this.getJPanelLeft(),   gbc_Left);
			jPanelBorder.add(this.getJPanelRight(),  gbc_Right);	
			jPanelBorder.add(this.getJPanelBottom(), gbc_Bottom);	
		}
		return jPanelBorder;
	}
	// -------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelNewWidth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNewWidth() {
		if (jPanelNewWidth == null) {
			jPanelNewWidth = new JPanel();
			jPanelNewWidth.setLayout(new BorderLayout());
			jLabelNewWidth = new JLabel("Width: ");
			//jLabelNewWidth.setPreferredSize(new Dimension(70, 20));
			//jLabelNewWidth.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNewWidth = new JSpinner(sModel);
			//jSpinnerNewWidth.setPreferredSize(new Dimension(60, 20));
			jSpinnerNewWidth.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNewWidth.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelNewWidth.add(jLabelNewWidth, BorderLayout.WEST);
			jPanelNewWidth.add(jSpinnerNewWidth, BorderLayout.CENTER);
		}
		return jPanelNewWidth;
	}

	/**
	 * This method initializes jJPanelNewHeight
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNewHeight() {
		if (jPanelNewHeight == null) {
			jPanelNewHeight = new JPanel();
			jPanelNewHeight.setLayout(new BorderLayout());
			jLabelNewHeight = new JLabel("Height: ");
			//jLabelNewHeight.setHorizontalAlignment(SwingConstants.RIGHT);
			//jLabelNewHeight.setPreferredSize(new Dimension(70, 20));
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNewHeight = new JSpinner(sModel);
			//jSpinnerNewHeight.setPreferredSize(new Dimension(60, 20));
			jSpinnerNewHeight.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNewHeight.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelNewHeight.add(jLabelNewHeight, BorderLayout.WEST);
			jPanelNewHeight.add(jSpinnerNewHeight, BorderLayout.CENTER);
		}
		return jPanelNewHeight;
	}
	/**
	 * This method initializes jJPanelNewsize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNewSize() {
		if (jPanelNewSize == null) {
			jPanelNewSize = new JPanel();
			jPanelNewSize.setLayout(new BoxLayout(jPanelNewSize, BoxLayout.Y_AXIS));
			//jPanelNewSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelNewSize.setBorder(new TitledBorder(null, "New size", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		
			JPanel pl =  new JPanel();
			pl.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
			pl.add(this.getJPanelNewWidth());
			pl.add(this.getJPanelNewHeight());	
			
			jPanelNewSize.add(pl);	
			jPanelNewSize.add(getJButtonMaxImgSize());
			
		}
		return jPanelNewSize;
	}

	// -------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonMaxImgSize() {
		if (jButtonMaxImgSize == null) {
			jButtonMaxImgSize = new JButton("Set to max of image stack");
			// jButtonMaxImgSize.setPreferredSize(new Dimension(50, 22));
			jButtonMaxImgSize.setAlignmentX(Component.CENTER_ALIGNMENT);
			jButtonMaxImgSize.setToolTipText("set to maximum image size of all images in the selected manager list");
			jButtonMaxImgSize.addActionListener(this);
			jButtonMaxImgSize.setActionCommand("parameter");
		}
		return jButtonMaxImgSize;
	}

	// -------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelConst
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelConst() {
		if (jPanelConst == null) {
			jPanelConst = new JPanel();
			jPanelConst.setLayout(new BoxLayout(jPanelConst, BoxLayout.X_AXIS));
			//jPanelConst.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			tbConst = new TitledBorder(null, "Constant", TitledBorder.LEADING, TitledBorder.TOP, null, null);
			jPanelConst.setBorder(tbConst);	
			jLabelConst = new JLabel("Value: ");
			//jLabelConst.setPreferredSize(new Dimension(50, 20));
			//jLabelConst.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, 255, 1); // init, min, max, step
			jSpinnerConst = new JSpinner(sModel);
			//jSpinnerConst.setPreferredSize(new Dimension(60, 20));
			jSpinnerConst.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerConst.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelConst.add(jLabelConst);
			jPanelConst.add(jSpinnerConst);
		}
		return jPanelConst;
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtZero() {
		if (buttZero == null) {
			buttZero = new JRadioButton();
			buttZero.setText("Zero");
			buttZero.setToolTipText("filling borders with zeros");
			buttZero.addActionListener(this);
			buttZero.setActionCommand("parameter");
		}
		return buttZero;
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
			buttConst.setToolTipText("filling borders with a user defined constant");
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
	private JRadioButton getJRadioButtonButtCopy() {
		// if (buttCopy == null) {
		buttCopy = new JRadioButton();
		buttCopy.setText("Copy");
		buttCopy.setToolTipText("filling borders with a copy of the edge pixels");
		buttCopy.addActionListener(this);
		buttCopy.setActionCommand("parameter");
		// }
		return buttCopy;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtReflect() {
		// if (buttReflect == null) {
		buttReflect = new JRadioButton();
		buttReflect.setText("Reflect");
		buttReflect.setToolTipText("filling borders with copies of the whole image");
		buttReflect.addActionListener(this);
		buttReflect.setActionCommand("parameter");
		// }
		return buttReflect;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtWrap() {
		// if (buttWrap == null) {
		buttWrap = new JRadioButton();
		buttWrap.setText("Wrap");
		buttWrap.setToolTipText("filling borders with copies of the whole image");
		buttWrap.addActionListener(this);
		buttWrap.setActionCommand("parameter");
		// }
		return buttWrap;
	}

	/**
	 * This method initializes jJPanelBar
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		// if (jPanelMethod== null) {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		jPanelMethod.add(getJRadioButtonButtZero());
		jPanelMethod.add(getJRadioButtonButtConst());
		jPanelMethod.add(getJRadioButtonButtCopy());
		jPanelMethod.add(getJRadioButtonButtReflect());
		jPanelMethod.add(getJRadioButtonButtWrap());
		// jPanelMethod.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		// }
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttZero);
		buttGroupMethod.add(buttConst);
		buttGroupMethod.add(buttCopy);
		buttGroupMethod.add(buttReflect);
		buttGroupMethod.add(buttWrap);
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtBorder() {
		// if (buttBorder == null) {
		buttBorder = new JRadioButton();
		buttBorder.setText("Border");
		buttBorder.setToolTipText("prefers the border settings instead of the new size values");
		buttBorder.addActionListener(this);
		buttBorder.setActionCommand("parameter");
		// }
		return buttBorder;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtSize() {
		// if (buttSize == null) {
		buttSize = new JRadioButton();
		buttSize.setText("New Size");
		buttSize.setToolTipText("prefers the new size setting instead of the zoom values");
		buttSize.addActionListener(this);
		buttSize.setActionCommand("parameter");
		// }
		return buttSize;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBorderOrSize() {
		// if (jPanelBorderOrSize== null) {
		jPanelBorderOrSize = new JPanel();
		//jPanelBorderOrSize.setLayout(new BoxLayout(jPanelBorderOrSize, BoxLayout.Y_AXIS));
		jPanelBorderOrSize.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelBorderOrSize.setBorder(new TitledBorder(null, "Border preference", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		//jPanelBorderOrSize.add(jLabelBorderOrSize);
		jPanelBorderOrSize.add(getJRadioButtonButtBorder());
		jPanelBorderOrSize.add(getJRadioButtonButtSize());
		// jPanelBorderOrSize.addSeparator();
		this.setButtonGroupBorderOrSize(); // Grouping of JRadioButtons
		return jPanelBorderOrSize;
	}

	private void setButtonGroupBorderOrSize() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupBorderOrSize = new ButtonGroup();
		buttGroupBorderOrSize.add(buttBorder);
		buttGroupBorderOrSize.add(buttSize);
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (buttConst == e.getSource()) {
		
			}

			if (jButtonMaxImgSize == e.getSource()) {
				// System.out.println("OperatorGUI_Border: jButtonMaxImgSize Event");
				// Scroll through all images
				BoardPanel.appendTextln("OperatorGUI_Border: Looking for largest image imgWidth and imgHeight, please wait.....");
				int maxWidth = 0;
				int maxHeight = 0;

				int curr = Application.getTank().getCurrIndex();
				if (curr == -1) {
					System.out.println("OperatorGUI_Border: Current tank index not available");
					return;
				}

				List<IqmDataBox> currentTankStack = Application.getTank().getTankDataAt(curr);

				PlanarImage pi = null;
				for (int i = 0; i < currentTankStack.size(); i++) {

					try {
						IqmDataBox iqmDataBox = (IqmDataBox) currentTankStack.get(i);

						// load the items on demand
						if (iqmDataBox instanceof VirtualDataBox) {
							iqmDataBox = VirtualDataManager.getInstance().load((IVirtualizable) iqmDataBox);
						}

						pi = (PlanarImage) iqmDataBox.getImage();
						
						maxWidth = Math.max(maxWidth, pi.getWidth());
						maxHeight = Math.max(maxHeight, pi.getHeight());

						pi = null;
					} catch (ArrayIndexOutOfBoundsException e1) {
						System.out.println("OperatorGUI_Border: Current image not available");
						return;
					}

				}
				BoardPanel.appendTextln("OperatorGUI_Border: Largest imgWidth: "
								+ maxWidth + "  Largest imgHeight: "
								+ maxHeight);
				jSpinnerNewWidth.setValue(maxWidth); // initiates wanted change events
				jSpinnerNewHeight.setValue(maxHeight); // initiates wanted change events
			}
		}
		this.update();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {

		int left      = ((Number) jSpinnerLeft.getValue()).intValue();
		int right     = ((Number) jSpinnerRight.getValue()).intValue();
		int top       = ((Number) jSpinnerTop.getValue()).intValue();
		int bottom    = ((Number) jSpinnerBottom.getValue()).intValue();
		int newWidth  = ((Number) jSpinnerNewWidth.getValue()).intValue();
		int newHeight = ((Number) jSpinnerNewHeight.getValue()).intValue();
		int oldWidth  = ((Number) jFormattedTextFieldOldWidth.getValue()).intValue();
		int oldHeight = ((Number) jFormattedTextFieldOldHeight.getValue()).intValue();
		@SuppressWarnings("unused")
		int c         = ((Number) jSpinnerConst.getValue()).intValue();

		if (jSpinnerLeft == e.getSource() || jSpinnerRight == e.getSource()) {

			if ((oldWidth + left + right) > 0) {
				jSpinnerNewWidth.removeChangeListener(this);
				jSpinnerNewWidth.setValue(oldWidth + left + right);
				jSpinnerNewWidth.addChangeListener(this);
			}
		}
		if (jSpinnerTop == e.getSource() || jSpinnerBottom == e.getSource()) {
			if ((oldHeight + top + bottom) > 0) {
				jSpinnerNewHeight.removeChangeListener(this);
				jSpinnerNewHeight.setValue(oldHeight + top + bottom);
				jSpinnerNewHeight.addChangeListener(this);
			}
		}

		if (jSpinnerNewWidth == e.getSource()) {
			while (newWidth > (oldWidth + left + right)) {
				if (isLastTimeChangedLeft) {
					right = right + 1;
					jSpinnerRight.removeChangeListener(this);
					jSpinnerRight.setValue(right);
					jSpinnerRight.addChangeListener(this);
					isLastTimeChangedLeft = false;
				} else {
					left = left + 1;
					jSpinnerLeft.removeChangeListener(this);
					jSpinnerLeft.setValue(left);
					jSpinnerLeft.addChangeListener(this);
					isLastTimeChangedLeft = true;
				}
			}
			while (newWidth < (oldWidth + left + right)) {
				if (isLastTimeChangedLeft) {
					right = right - 1;
					jSpinnerRight.removeChangeListener(this);
					jSpinnerRight.setValue(right);
					jSpinnerRight.addChangeListener(this);
					isLastTimeChangedLeft = false;
				} else {
					left = left - 1;
					jSpinnerLeft.removeChangeListener(this);
					jSpinnerLeft.setValue(left);
					jSpinnerLeft.addChangeListener(this);
					isLastTimeChangedLeft = true;
				}
			}
		}
		if (jSpinnerNewHeight == e.getSource()) {
			while (newHeight > (oldHeight + top + bottom)) {
				if (isLastTimeChangedTop) {
					bottom = bottom + 1;
					jSpinnerBottom.removeChangeListener(this);
					jSpinnerBottom.setValue(bottom);
					jSpinnerBottom.addChangeListener(this);
					isLastTimeChangedTop = false;
				} else {
					top = top + 1;
					jSpinnerTop.removeChangeListener(this);
					jSpinnerTop.setValue(top);
					jSpinnerTop.addChangeListener(this);
					isLastTimeChangedTop = true;
				}
			}
			while (newHeight < (oldHeight + top + bottom)) {
				if (isLastTimeChangedTop) {
					bottom = bottom - 1;
					jSpinnerBottom.removeChangeListener(this);
					jSpinnerBottom.setValue(bottom);
					jSpinnerBottom.addChangeListener(this);
					isLastTimeChangedTop = false;
				} else {
					top = top - 1;
					jSpinnerTop.removeChangeListener(this);
					jSpinnerTop.setValue(top);
					jSpinnerTop.addChangeListener(this);
					isLastTimeChangedTop = true;
				}
			}
		}
		this.update();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
}// END
