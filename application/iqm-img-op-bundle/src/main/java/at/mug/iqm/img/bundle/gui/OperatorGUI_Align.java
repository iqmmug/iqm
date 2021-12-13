package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Align.java
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
import java.awt.Dimension;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpAlignDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 06
 * @update 2014 12 JRadioButtons and TitledBorder HA
 */
public class OperatorGUI_Align  extends AbstractImageOperatorGUI	
implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3125658974488246390L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_Align.class);

	private  ParameterBlockIQM   pb         = null;  

	private  JRadioButton buttP90			= null;
	private  JRadioButton buttM90			= null;
	private  JRadioButton butt180			= null;
	private  JRadioButton buttRot			= null;
	private  JRadioButton buttFlipVer		= null;
	private  JRadioButton buttFlipHor		= null;
	private  JRadioButton buttFlipDiag		= null;
	private  JRadioButton buttFlipADiag		= null;
	private  JRadioButton buttShift			= null;
	private  JRadioButton buttCenter		= null;
	private  JPanel 	  jPanelMethod	    = null;
	private  ButtonGroup  buttGroupMethod	= null;

	private JPanel 	  jPanelAngle	= null;	
	private JLabel	  jLabelAngle	= null;
	private JSpinner  jSpinnerAngle	= null;


	private JPanel 	  jPanelDX		= null;	
	private JLabel	  jLabelDX		= null;
	private JSpinner  jSpinnerDX	= null;

	private JPanel 	  jPanelDY		= null;	
	private JLabel	  jLabelDY		= null;
	private JSpinner  jSpinnerDY	= null;

	private  JRadioButton  buttNN         = null;	//nearest neighbor
	private  JRadioButton  buttBL         = null;	//bilinear
	private  JRadioButton  buttBC         = null;	//bicubic
	private  JRadioButton  buttBC2        = null;	//bicubic2
	private  JPanel        jPanelIntP     = null;
	private  ButtonGroup   buttGroupIntP  = null;

	/**
	 * constructor
	 */
	public OperatorGUI_Align() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpAlignDescriptor().getName());

		this.initialize();

		this.setTitle("Transform");

		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelMethod(), getGridBagConstraintsMethod());
		this.getOpGUIContent().add(getJPanelAngle(),  getGridBagConstraintsAngle());
		this.getOpGUIContent().add(getJPanelDX(),     getGridBagConstraintsDX());
		this.getOpGUIContent().add(getJPanelDY(),     getGridBagConstraintsDY());
		this.getOpGUIContent().add(getJPanelIntP(),   getGridBagConstraintsButtonIntPGroup());

		this.pack();
	}

	@Override
	public void updateParameterBlock(){

		if (buttP90.isSelected())       pb.setParameter("Method", 0);
		if (buttM90.isSelected())       pb.setParameter("Method", 1);
		if (butt180.isSelected())  	    pb.setParameter("Method", 2);
		if (buttRot.isSelected())       pb.setParameter("Method", 3);
		if (buttFlipVer.isSelected())   pb.setParameter("Method", 4);
		if (buttFlipHor.isSelected())   pb.setParameter("Method", 5);
		if (buttFlipDiag.isSelected())  pb.setParameter("Method", 6);
		if (buttFlipADiag.isSelected()) pb.setParameter("Method", 7);
		if (buttShift.isSelected())     pb.setParameter("Method", 8);
		if (buttCenter.isSelected())    pb.setParameter("Method", 9);
		pb.setParameter("Angle", ((Number)jSpinnerAngle.getValue()).intValue());
		pb.setParameter("DX",    ((Number)jSpinnerDX.getValue()).intValue());
		pb.setParameter("DY",    ((Number)jSpinnerDY.getValue()).intValue());
		if (buttNN.isSelected())  pb.setParameter("Interpolation", 0);
		if (buttBL.isSelected())  pb.setParameter("Interpolation", 1);
		if (buttBC.isSelected())  pb.setParameter("Interpolation", 2);
		if (buttBC2.isSelected()) pb.setParameter("Interpolation", 3);
	}

	/**
	 * This method sets the current parameter values
	 *
	 */
	@Override
	public void setParameterValuesToGUI(){
		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Method") == 0) buttP90.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttM90.setSelected(true);
		if (pb.getIntParameter("Method") == 2) butt180.setSelected(true);
		if (pb.getIntParameter("Method") == 3) buttRot.setSelected(true);
		if (pb.getIntParameter("Method") == 4) buttFlipVer.setSelected(true);
		if (pb.getIntParameter("Method") == 5) buttFlipHor.setSelected(true);
		if (pb.getIntParameter("Method") == 6) buttFlipDiag.setSelected(true);
		if (pb.getIntParameter("Method") == 7) buttFlipADiag.setSelected(true);
		if (pb.getIntParameter("Method") == 8) buttShift.setSelected(true);
		if (pb.getIntParameter("Method") == 9) buttCenter.setSelected(true);
		jSpinnerAngle.removeChangeListener(this);	 
		jSpinnerDX.removeChangeListener(this);	    
		jSpinnerDY.removeChangeListener(this);	    	    
		jSpinnerAngle.setValue(pb.getIntParameter("Angle"));
		jSpinnerDX.setValue(pb.getIntParameter("DX"));
		jSpinnerDY.setValue(pb.getIntParameter("DY"));
		jSpinnerAngle.addChangeListener(this);
		jSpinnerDX.addChangeListener(this);
		jSpinnerDY.addChangeListener(this);
		if (pb.getIntParameter("Interpolation") == 0)  buttNN.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 1)  buttBL.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 2)  buttBC.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 3)  buttBC2.setSelected(true);

	}

	private  GridBagConstraints getGridBagConstraintsMethod(){
		GridBagConstraints gridBagConstraintsMethod = new GridBagConstraints();
		gridBagConstraintsMethod.gridx = 0;
		gridBagConstraintsMethod.gridy = 0;
		gridBagConstraintsMethod.gridwidth = 2;
		gridBagConstraintsMethod.gridheight = 4;

		gridBagConstraintsMethod.insets = new Insets(10, 0, 5, 0);  //top left bottom right
		//gridBagConstraintsMethod.anchor = GridBagConstraints.LINE_START;
		//gridBagConstraintsMethod.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsMethod;
	}
	private  GridBagConstraints getGridBagConstraintsAngle(){
		GridBagConstraints gridBagConstraintsAngle = new GridBagConstraints();
		gridBagConstraintsAngle.gridx = 3;
		gridBagConstraintsAngle.gridy = 0;
		gridBagConstraintsAngle.gridwidth = 1;//?
		gridBagConstraintsAngle.insets = new Insets(20, 0, 0, 0); //top left bottom right
		//gridBagConstraintsAngle.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsAngle;
	}
	private  GridBagConstraints getGridBagConstraintsDX(){
		GridBagConstraints gridBagConstraintsDX = new GridBagConstraints();
		gridBagConstraintsDX.gridx = 3;
		gridBagConstraintsDX.gridy = 1;
		gridBagConstraintsDX.gridwidth = 1;//?
		gridBagConstraintsDX.insets = new Insets(5, 0, 0, 0);  //top left bottom right
		//gridBagConstraintsDX.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsDX;
	}
	private  GridBagConstraints getGridBagConstraintsDY(){
		GridBagConstraints gridBagConstraintsDY = new GridBagConstraints();
		gridBagConstraintsDY.gridx = 3;
		gridBagConstraintsDY.gridy = 2;
		gridBagConstraintsDY.gridwidth = 1;//?
		gridBagConstraintsDY.insets = new Insets(0, 0, 0, 0);  //top left bottom right
		//gridBagConstraintsDY.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsDY;
	}
	private  GridBagConstraints getGridBagConstraintsButtonIntPGroup(){
		GridBagConstraints gridBagConstraintsButtonIntPGroup = new GridBagConstraints();
		gridBagConstraintsButtonIntPGroup.gridx = 3;
		gridBagConstraintsButtonIntPGroup.gridy = 3;
		gridBagConstraintsButtonIntPGroup.gridwidth = 1;//?
		//gridBagConstraintsButtonIntPGroup.gridheight = 
		gridBagConstraintsButtonIntPGroup.insets = new Insets(20, 0, 5, 0);  //top left bottom right
		//gridBagConstraintsButtonIntPGroup.fill = GridBagConstraints.BOTH;
		gridBagConstraintsButtonIntPGroup.anchor = GridBagConstraints.SOUTH;
		return gridBagConstraintsButtonIntPGroup;
	}
	
	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update(){
		logger.debug("Updating GUI...");
	
		jLabelAngle.setEnabled(false);
		jSpinnerAngle.setEnabled(false);
			
		jLabelDX.setEnabled(false);
		jLabelDY.setEnabled(false);
		jSpinnerDX.setEnabled(false);
		jSpinnerDY.setEnabled(false);
		
		if (buttP90.isSelected());      
		if (buttM90.isSelected());      
		if (butt180.isSelected());  	 
		if (buttRot.isSelected()){
			jSpinnerAngle.setEnabled(true);
		}
		if (buttFlipVer.isSelected());  
		if (buttFlipHor.isSelected());  
		if (buttFlipDiag.isSelected()); 
		if (buttFlipADiag.isSelected());
		if (buttShift.isSelected()){
			jLabelDX.setEnabled(true);
			jLabelDY.setEnabled(true);
			jSpinnerDX.setEnabled(true);
			jSpinnerDY.setEnabled(true);
		}
		if (buttCenter.isSelected());   
	
		
	
		
	}

	/**
	 * This method initializes the Option: P90
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonP90() {
		if (buttP90 == null) {
			buttP90  = new JRadioButton();
			buttP90.setText("+90°");
			buttP90.setToolTipText("rotation 90° clockwise");
			buttP90.addActionListener(this);
			buttP90.setActionCommand("parameter");
		}
		return buttP90;
	}
	/**
	 * This method initializes the Option: M90
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonM90() {
		if (buttM90 == null) {
			buttM90  = new JRadioButton();
			buttM90.setText("-90°");
			buttM90.setToolTipText("rotation 90° counterclockwise");
			buttM90.addActionListener(this);
			buttM90.setActionCommand("parameter");
		}
		return buttM90;
	}
	/**
	 * This method initializes the Option: 180
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButton180() {
		if (butt180 == null) {
			butt180  = new JRadioButton();
			butt180.setText("180°");
			butt180.setToolTipText("rotation 180°");
			butt180.addActionListener(this);
			butt180.setActionCommand("parameter");
		}
		return butt180;
	}
	/**
	 * This method initializes the Option: Rot
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonRot() {
		if (buttRot == null) {
			buttRot  = new JRadioButton();
			buttRot.setText("Rotation");
			buttRot.setToolTipText("Rotation user value");
			buttRot.addActionListener(this);
			buttRot.setActionCommand("parameter");
		}
		return buttRot;
	}
	/**
	 * This method initializes the Option: FlipVer
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonFlipVer() {
		if (buttFlipVer == null) {
			buttFlipVer  = new JRadioButton();
			buttFlipVer.setText("Flip Vertical");
			buttFlipVer.setToolTipText("Flip (mirror) vertical");
			buttFlipVer.addActionListener(this);
			buttFlipVer.setActionCommand("parameter");
			buttFlipVer.setEnabled(true);
		}
		return buttFlipVer;
	}
	/**
	 * This method initializes the Option: FlipHor
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonFlipHor() {
		if (buttFlipHor == null) {
			buttFlipHor  = new JRadioButton();
			buttFlipHor.setText("Flip horizontal");
			buttFlipHor.setToolTipText("Flip (mirror) horizontal");
			buttFlipHor.addActionListener(this);
			buttFlipHor.setActionCommand("parameter");
			buttFlipHor.setEnabled(true);
		}
		return buttFlipHor;	
	}
	/**
	 * This method initializes the Option: FlipDiag
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonFlipDiag() {
		if (buttFlipDiag == null) {
			buttFlipDiag  = new JRadioButton();
			buttFlipDiag.setText("Flip Diagonal");
			buttFlipDiag.setToolTipText("Flip diagonal");
			buttFlipDiag.addActionListener(this);
			buttFlipDiag.setActionCommand("parameter");
			buttFlipDiag.setEnabled(true);
		}
		return buttFlipDiag;	
	}
	/**
	 * This method initializes the Option: FlipADiag
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonFlipADiag() {
		if (buttFlipADiag == null) {
			buttFlipADiag  = new JRadioButton();
			buttFlipADiag.setText("Flip AntiDiagonal");
			buttFlipADiag.setToolTipText("flip antidiagonal");
			buttFlipADiag.addActionListener(this);
			buttFlipADiag.setActionCommand("parameter");
			buttFlipADiag.setEnabled(true);
		}
		return buttFlipADiag;	
	}
	/**
	 * This method initializes the Option: Shift
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonShift() {
		if (buttShift == null) {
			buttShift  = new JRadioButton();
			buttShift.setText("Shift");
			buttShift.setToolTipText("shift in x, y direction");
			buttShift.addActionListener(this);
			buttShift.setActionCommand("parameter");
			buttShift.setEnabled(true);
		}
		return buttShift;	
	}
	/**
	 * This method initializes the Option: Center
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonCenter() {
		if (buttCenter == null) {
			buttCenter  = new JRadioButton();
			buttCenter.setText("Center");
			buttCenter.setToolTipText("the center of gravity is shifted to the center of the image");
			buttCenter.addActionListener(this);
			buttCenter.setActionCommand("parameter");
			buttCenter.setEnabled(true);
		}
		return buttCenter;	
	}

	/**
	 * This method initializes JPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private   JPanel getJPanelMethod() {
		//		if (jPanelMethod == null) {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethod.add(getJRadioButtonP90());			
		jPanelMethod.add(getJRadioButtonM90());			
		jPanelMethod.add(getJRadioButton180());			
		jPanelMethod.add(getJRadioButtonRot());
		jPanelMethod.add(getJRadioButtonFlipVer());			
		jPanelMethod.add(getJRadioButtonFlipHor());
		jPanelMethod.add(getJRadioButtonFlipDiag());
		jPanelMethod.add(getJRadioButtonFlipADiag());
		jPanelMethod.add(getJRadioButtonShift());
		jPanelMethod.add(getJRadioButtonCenter());
		//jPanelMethod.addSeparator();
		this.setButtonGroupMethod();	//Grouping of JRadioButtons
		//		}
		return jPanelMethod;
	}
	private  void setButtonGroupMethod() {
		//if (ButtonGroup buttGroupMethod == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttP90);
		buttGroupMethod.add(buttM90);
		buttGroupMethod.add(butt180);
		buttGroupMethod.add(buttRot);
		buttGroupMethod.add(buttFlipVer);
		buttGroupMethod.add(buttFlipHor);
		buttGroupMethod.add(buttFlipDiag);
		buttGroupMethod.add(buttFlipADiag);
		buttGroupMethod.add(buttShift);
		buttGroupMethod.add(buttCenter);
	}
	//-----------------------------------------------------------------------
	//	class IntegerNumberVerifier extends InputVerifier { //damit muss Eingabe richtig sein 
	//		@Override
	//		public boolean verify(JComponent input) {
	//		   JFormattedTextField ftf = (JFormattedTextField)input;
	//		   JFormattedTextField.AbstractFormatter formatter = ftf.getFormatter();
	//		   if (formatter != null) {
	//		      String text = ftf.getText();
	//		  	  try {
	//				   text = text.replace(",", ".");
	//				   Integer.valueOf(text);
	//					//Float.valueOf(text);
	//					return true;
	//			  } catch (NumberFormatException e) {
	//					return false;
	//			  }	 
	//		   }
	//		   return true;
	//		}
	////		public boolean shouldYieldFocus(JComponent input) {
	////		System.out.println("NumberVerifier  shouldYieldFocus");
	////
	////          return verify(input);
	////    }
	//  }
	//	/**
	//	 * This method initializes jJPanelAngle	
	//	 * 	
	//	 * @return javax.swing.JPanel
	//	 */
	//	private JPanel getJPanelAngle() {
	//		if (jPanelAngle == null) {
	//			jPanelAngle = new JPanel();
	//			jPanelAngle.setLayout(new BorderLayout());
	//			//jPanelAngle.setPreferredSize(new Dimension(250,18));					
	//			jLabelAngle = new JLabel("Angle:");
	//			jLabelAngle.setPreferredSize(new Dimension(40,18));
	//			jFormattedTextFieldAngle = new JFormattedTextField(NumberFormat.getNumberInstance());
	//			jFormattedTextFieldAngle.setPreferredSize(new Dimension(70,18));
	//			jFormattedTextFieldAngle.addPropertyChangeListener("value", this);
	//			//jFormattedTextFieldAngle.setInputVerifier(new IntegerNumberVerifier());
	//			//jFormattedTextFieldAngle.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
	//			jFormattedTextFieldAngle.setColumns(5);
	//			InternationalFormatter intFormatter = (InternationalFormatter)jFormattedTextFieldAngle.getFormatter() ;
	//			DecimalFormat decimalFormat = (DecimalFormat)intFormatter.getFormat() ;
	//			decimalFormat.applyPattern("#");  //decimalFormat.applyPattern("#,##0.0")  ;
	//			jPanelAngle.add(jLabelAngle, 			    BorderLayout.WEST);
	//			jPanelAngle.add(jFormattedTextFieldAngle, BorderLayout.CENTER);	
	//		
	//		}
	//		return jPanelAngle;
	//	}
	/**
	 * This method initializes jJPanelAngle	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelAngle() {
		if (jPanelAngle == null) {
			jPanelAngle = new JPanel();
			jPanelAngle.setLayout(new BorderLayout());		
			jLabelAngle = new JLabel("Angle: ");
			jLabelAngle.setPreferredSize(new Dimension(40, 20));
			jLabelAngle.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); //init, min, max, step
			jSpinnerAngle = new JSpinner(sModel);
			jSpinnerAngle.setPreferredSize(new Dimension(70, 20));
			jSpinnerAngle.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor)jSpinnerAngle.getEditor()  ;
			JFormattedTextField ftf = defEditor.getTextField() ;
			InternationalFormatter intFormatter = (InternationalFormatter)ftf.getFormatter() ;
			DecimalFormat decimalFormat = (DecimalFormat)intFormatter.getFormat() ;
			decimalFormat.applyPattern("#");  //decimalFormat.applyPattern("#,##0.0")  ;		
			jPanelAngle.add(jLabelAngle,   BorderLayout.WEST);
			jPanelAngle.add(jSpinnerAngle, BorderLayout.CENTER);			
		}
		return jPanelAngle;
	}
	//	/**
	//	 * This method initializes jJPanelDX	
	//	 * 	
	//	 * @return javax.swing.JPanel
	//	 */
	//	private JPanel getJPanelDX() {
	//		if (jPanelDX == null) {
	//			jPanelDX = new JPanel();
	//			jPanelDX.setLayout(new BorderLayout());
	//			//jPanelDX.setPreferredSize(new Dimension(250,18));					
	//			jLabelDX = new JLabel("dX:");
	//			jLabelDX.setPreferredSize(new Dimension(40,18));
	//			jFormattedTextFieldDX = new JFormattedTextField(NumberFormat.getNumberInstance());
	//			jFormattedTextFieldDX.setPreferredSize(new Dimension(70,18));
	//			jFormattedTextFieldDX.addPropertyChangeListener("value", this);
	//			//jFormattedTextFieldDX.setInputVerifier(new IntegerNumberVerifier());
	//			//jFormattedTextFieldDX.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
	//			jFormattedTextFieldDX.setColumns(5);
	//			InternationalFormatter intFormatter = (InternationalFormatter)jFormattedTextFieldDX.getFormatter() ;
	//			DecimalFormat decimalFormat = (DecimalFormat)intFormatter.getFormat() ;
	//			decimalFormat.applyPattern("#");  //decimalFormat.applyPattern("#,##0.0")  ;
	//			jPanelDX.add(jLabelDX, 			  BorderLayout.WEST);
	//			jPanelDX.add(jFormattedTextFieldDX, BorderLayout.CENTER);
	//			jFormattedTextFieldDX.setEnabled(true);	
	//		}
	//		return jPanelDX;
	//	}
	/**
	 * This method initializes jJPanelDX	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDX() {
		if (jPanelDX == null) {
			jPanelDX = new JPanel();
			jPanelDX.setLayout(new BorderLayout());		
			jLabelDX = new JLabel("dX: ");
			jLabelDX.setPreferredSize(new Dimension(40, 20));
			jLabelDX.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); //init, min, max, step
			jSpinnerDX = new JSpinner(sModel);
			jSpinnerDX.setPreferredSize(new Dimension(70, 20));
			jSpinnerDX.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor)jSpinnerDX.getEditor()  ;
			JFormattedTextField ftf = defEditor.getTextField() ;
			InternationalFormatter intFormatter = (InternationalFormatter)ftf.getFormatter() ;
			DecimalFormat decimalFormat = (DecimalFormat)intFormatter.getFormat() ;
			decimalFormat.applyPattern("#");  //decimalFormat.applyPattern("#,##0.0")  ;		
			jPanelDX.add(jLabelDX,   BorderLayout.WEST);
			jPanelDX.add(jSpinnerDX, BorderLayout.CENTER);			
		}
		return jPanelDX;
	}
	//	/**
	//	 * This method initializes jJPanelDY	
	//	 * 	
	//	 * @return javax.swing.JPanel
	//	 */
	//	private JPanel getJPanelDY() {
	//		if (jPanelDY == null) {
	//			jPanelDY = new JPanel();
	//			jPanelDY.setLayout(new BorderLayout());
	//			//jPanelDY.setPreferredSize(new Dimension(250,18));					
	//			jLabelDY = new JLabel("dY:");
	//			jLabelDY.setPreferredSize(new Dimension(40,18));
	//			jFormattedTextFieldDY = new JFormattedTextField(NumberFormat.getNumberInstance());
	//			jFormattedTextFieldDY.setPreferredSize(new Dimension(70,18));
	//			jFormattedTextFieldDY.addPropertyChangeListener("value", this);
	//			//jFormattedTextFieldDY.setInputVerifier(new IntegerNumberVerifier());
	//			//jFormattedTextFieldDY.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
	//			jFormattedTextFieldDY.setColumns(5);
	//			InternationalFormatter intFormatter = (InternationalFormatter)jFormattedTextFieldDY.getFormatter() ;
	//			DecimalFormat decimalFormat = (DecimalFormat)intFormatter.getFormat() ;
	//			decimalFormat.applyPattern("#");  //decimalFormat.applyPattern("#,##0.0")  ;
	//			jPanelDY.add(jLabelDY, 			    BorderLayout.WEST);
	//			jPanelDY.add(jFormattedTextFieldDY, BorderLayout.CENTER);	
	//			jFormattedTextFieldDY.setEnabled(true);
	//		}
	//		return jPanelDY;
	//	}
	/**
	 * This method initializes jJPanelDY	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDY() {
		if (jPanelDY == null) {
			jPanelDY = new JPanel();
			jPanelDY.setLayout(new BorderLayout());		
			jLabelDY = new JLabel("dY: ");
			jLabelDY.setPreferredSize(new Dimension(40, 20));
			jLabelDY.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); //init, min, max, step
			jSpinnerDY = new JSpinner(sModel);
			jSpinnerDY.setPreferredSize(new Dimension(70, 20));
			jSpinnerDY.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor)jSpinnerDY.getEditor()  ;
			JFormattedTextField ftf = defEditor.getTextField() ;
			InternationalFormatter intFormatter = (InternationalFormatter)ftf.getFormatter() ;
			DecimalFormat decimalFormat = (DecimalFormat)intFormatter.getFormat() ;
			decimalFormat.applyPattern("#");  //decimalFormat.applyPattern("#,##0.0")  ;		
			jPanelDY.add(jLabelDY,   BorderLayout.WEST);
			jPanelDY.add(jSpinnerDY, BorderLayout.CENTER);			
		}
		return jPanelDY;
	}
	//--------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Nearest Neighbor	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonMenuButtNN() {
		if (buttNN == null) {
			buttNN  = new JRadioButton();
			buttNN .setText("Near Neigh");
			buttNN .setToolTipText("uses Nearest Neighbor resampling for rotation");
			buttNN .addActionListener(this);
			buttNN.setActionCommand("parameter");
		}
		return buttNN;
	}
	/**
	 * This method initializes the Option: Bilinear interpolation
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonMenuButtBL() {
		//if (buttBL == null) {
		buttBL  = new JRadioButton();
		buttBL .setText("Bilinear");
		buttBL .setToolTipText("uses Bilinear interpolation for rotation");
		buttBL .addActionListener(this);
		buttBL.setActionCommand("parameter");
		//}
		return buttBL;
	}
	/**
	 * This method initializes the Option: Bicubic interpolation	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonMenuButtBC() {
		//if (buttBC == null) {
		buttBC  = new JRadioButton();
		buttBC .setText("Bicubic");
		buttBC .setToolTipText("uses Bicubic interpoolation for rotation");
		buttBC .addActionListener(this);
		buttBC.setActionCommand("parameter");
		//}
		return buttBC;
	}
	/**
	 * This method initializes the Option: Bicubic2 interpolation	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonMenuButtBC2() {
		//if (buttBC2 == null) {
		buttBC2  = new JRadioButton();
		buttBC2 .setText("Bicubic2");
		buttBC2 .setToolTipText("uses Bicubic2 interpolation for rotation");
		buttBC2 .addActionListener(this);
		buttBC2.setActionCommand("parameter");
		//}
		return buttBC2;
	}
	/**
	 * This method initializes JPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private   JPanel getJPanelIntP() {
		//		if (jPanelIntP== null) {
		jPanelIntP = new JPanel();
		jPanelIntP.setLayout(new BoxLayout(jPanelIntP, BoxLayout.Y_AXIS));
		//jPanelIntP.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelIntP.setBorder(new TitledBorder(null, "Interpolation", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelIntP.add(getJRadioButtonMenuButtNN());
		jPanelIntP.add(getJRadioButtonMenuButtBL());
		jPanelIntP.add(getJRadioButtonMenuButtBC());
		jPanelIntP.add(getJRadioButtonMenuButtBC2());
		//jPanelIntP.addSeparator();
		this.setButtonGroupIntP();	//Grouping of JRadioButtons
		//		}
		return jPanelIntP;
	}
	private  void setButtonGroupIntP() {
		//if (ButtonGroup buttGroup == null) {
		buttGroupIntP = new ButtonGroup();
		buttGroupIntP.add(buttNN);
		buttGroupIntP.add(buttBL);
		buttGroupIntP.add(buttBC);
		buttGroupIntP.add(buttBC2);
	}

	//--------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.update();
			this.updateParameterBlock();
		}

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
	//-----------------------------------------------------------------------------------------------


	@Override
	public void stateChanged(ChangeEvent e) {
		if (jSpinnerAngle == e.getSource()) {
		}
		if (jSpinnerDX  == e.getSource()) {
		}
		if (jSpinnerDY  == e.getSource()) {
		}
		this.updateParameterBlock();

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
}//END
