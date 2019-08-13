package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_BUnwarpJ.java
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.FloatNumberVerifier;
import at.mug.iqm.img.bundle.descriptors.IqmOpBUnwarpJDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 05
 * @update 2014 12 change buttons to JRadioButtons and added TitledBorder
 */
public class OperatorGUI_BUnwarpJ extends AbstractImageOperatorGUI
implements ActionListener, PropertyChangeListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6769473458548406778L;

	// class specific logger
	private static Class<?> caller = OperatorGUI_BUnwarpJ.class;
	private static final Logger logger = Logger.getLogger(OperatorGUI_BUnwarpJ.class);

	private  ParameterBlockIQM pb = null; 

	private int regMode;  //Registration Mode 
	private int subSamp;  //SubSampling
	private int initDef;  //Initial Deformation
	private int finalDef; //Final Deformation
	private float divW;   //Divergency Weight
	private float curlW;  //Curl Weight
	private float landW;  //Landmark Weight
	private float imgW;   //Image Weight
	private float consW;  //Consistency Weight
	private float stopTh; //Stop Threshold


	private  JRadioButton buttFast 			= null;
	private  JRadioButton buttAccurate		= null;
	private  JRadioButton buttMono	   		= null;
	private  JPanel 	  jPanelRegMode  	= null;
	private  ButtonGroup  buttGroupRegMode 	= null;

	private  JPanel 	pnlSubSamp  = null;
	private  JSpinner   spnrSubSamp = null;
	private  JLabel     lblSubSamp  = null;

	private  JRadioButton buttInitDefVeryCoarse = null;
	private  JRadioButton buttInitDefCoarse		= null;
	private  JRadioButton buttInitDefFine	   	= null;
	private  JRadioButton buttInitDefVeryFine	= null;
	private  JPanel 	  jPanelInitDef  		= null;
	private  ButtonGroup  buttGroupInitDef 		= null;

	private  JRadioButton buttFinalDefVeryCoarse = null;
	private  JRadioButton buttFinalDefCoarse	 = null;
	private  JRadioButton buttFinalDefFine	   	 = null;
	private  JRadioButton buttFinalDefVeryFine	 = null;
	private  JPanel 	  jPanelFinalDef  		 = null;
	private  ButtonGroup  buttGroupFinalDef 	 = null;

	private JPanel 				 	jPanelDivW					= null;	
	private JLabel				 	jLabelDivW					= null;
	private JFormattedTextField 	jFormattedTextFieldDivW 	= null;

	private JPanel 				 	jPanelCurlW					= null;	
	private JLabel				 	jLabelCurlW					= null;
	private JFormattedTextField 	jFormattedTextFieldCurlW 	= null;

	private JPanel 				 	jPanelLandW					= null;	
	private JLabel				 	jLabelLandW					= null;
	private JFormattedTextField 	jFormattedTextFieldLandW 	= null;

	private JPanel 				 	jPanelImgW					= null;	
	private JLabel				 	jLabelImgW					= null;
	private JFormattedTextField 	jFormattedTextFieldImgW 	= null;

	private JPanel 				 	jPanelConsW					= null;	
	private JLabel				 	jLabelConsW					= null;
	private JFormattedTextField 	jFormattedTextFieldConsW 	= null;

	private JPanel 				 	jPanelStopTh				= null;	
	private JLabel				 	jLabelStopTh				= null;
	private JFormattedTextField 	jFormattedTextFieldStopTh 	= null;
	private JPanel weightPanel;


	/**
	 * constructor
	 */
	public OperatorGUI_BUnwarpJ() {
		logger.debug("Now initializing...");
		
		this.setOpName(new IqmOpBUnwarpJDescriptor().getName());	
		this.initialize(); 	
		this.setTitle("BUnwarpJ Image Registration");
		this.getOpGUIContent().setLayout(new GridBagLayout());
		
		this.getOpGUIContent().add(getJPanelRegMode(),      getGridBagConstraints_ButtonGroupRegMode());
		this.getOpGUIContent().add(getSpnrSubSamp(),        getGridBagConstraints_SubSamp());
		this.getOpGUIContent().add(getJPanelInitDef(),      getGridBagConstraints_ButtonGroupInitDef());
		this.getOpGUIContent().add(getJPanelFinalDef(),     getGridBagConstraints_ButtonGroupFinalDef());
		this.getOpGUIContent().add(getWeightPanel(),        getGridBagConstraints_WeightPanel());

		this.pack();
	}
	

	private  GridBagConstraints getGridBagConstraints_ButtonGroupRegMode(){
		GridBagConstraints gbc_ButtonGroupRegMode = new GridBagConstraints();
		gbc_ButtonGroupRegMode.gridx = 1;
		gbc_ButtonGroupRegMode.gridy = 0;
		gbc_ButtonGroupRegMode.insets = new Insets(10, 0, 0, 0);  //top, left bottom right
		gbc_ButtonGroupRegMode.fill = GridBagConstraints.BOTH;
		return gbc_ButtonGroupRegMode;
	}

	private  GridBagConstraints getGridBagConstraints_SubSamp(){
		GridBagConstraints gbc_SubSamp = new GridBagConstraints();
		gbc_SubSamp.gridx = 0;
		gbc_SubSamp.gridy = 1;
		gbc_SubSamp.gridwidth = 2;
		gbc_SubSamp.fill = GridBagConstraints.HORIZONTAL;
		gbc_SubSamp.insets = new Insets(5, 0, 0, 0);  //top, left bottom right
		//gridBagConstraintsSubSamp.fill = GridBagConstraints.BOTH;
		return gbc_SubSamp;
	}

	private  GridBagConstraints getGridBagConstraints_ButtonGroupInitDef(){
		GridBagConstraints gbc_ButtonGroupInitDef = new GridBagConstraints();
		gbc_ButtonGroupInitDef.gridx = 1;
		gbc_ButtonGroupInitDef.gridy = 2;
		gbc_ButtonGroupInitDef.insets = new Insets(5, 0, 0, 0);  //top, left bottom right
		gbc_ButtonGroupInitDef.fill = GridBagConstraints.BOTH;
		return gbc_ButtonGroupInitDef;
	}
	
	private  GridBagConstraints getGridBagConstraints_ButtonGroupFinalDef(){
		GridBagConstraints gbc_ButtonGroupFinalDef = new GridBagConstraints();
		gbc_ButtonGroupFinalDef.gridx = 1;
		gbc_ButtonGroupFinalDef.gridy = 3;
		gbc_ButtonGroupFinalDef.gridwidth = 1;
		gbc_ButtonGroupFinalDef.insets = new Insets(5, 0, 0, 0);  //top, left bottom right
		gbc_ButtonGroupFinalDef.fill = GridBagConstraints.BOTH;
		return gbc_ButtonGroupFinalDef;
	}
	private  GridBagConstraints getGridBagConstraints_WeightPanel(){
		GridBagConstraints gbc_weightPanel = new GridBagConstraints();
		gbc_weightPanel.gridwidth = 2;
		gbc_weightPanel.gridx = 0;
		gbc_weightPanel.gridy = 4;
		gbc_weightPanel.insets = new Insets(5, 0, 5, 0);  //top, left bottom right
		gbc_weightPanel.fill = GridBagConstraints.BOTH;
	return gbc_weightPanel;
	}

	/**
	 * This method sets the current parameter block
	 * The individual values of the GUI current ParameterBlock
	 *
	 */
	@Override
	public void updateParameterBlock(){
		if (buttFast.isSelected())     	pb.setParameter("RegMode", 0);
		if (buttAccurate.isSelected())  pb.setParameter("RegMode", 1);
		if (buttMono.isSelected())    	pb.setParameter("RegMode", 2);

		pb.setParameter("SubSamp", subSamp);

		if (buttInitDefVeryCoarse.isSelected())  pb.setParameter("InitDef", 0);
		if (buttInitDefCoarse.isSelected())  	 pb.setParameter("InitDef", 1);
		if (buttInitDefFine.isSelected())    	 pb.setParameter("InitDef", 2);
		if (buttInitDefVeryFine.isSelected())    pb.setParameter("InitDef", 3);

		if (buttFinalDefVeryCoarse.isSelected()) pb.setParameter("FinalDef", 0);
		if (buttFinalDefCoarse.isSelected())  	 pb.setParameter("FinalDef", 1);
		if (buttFinalDefFine.isSelected())    	 pb.setParameter("FinalDef", 2);
		if (buttFinalDefVeryFine.isSelected())   pb.setParameter("FinalDef", 3);

		pb.setParameter("DivW",   ((Number)jFormattedTextFieldDivW.getValue()).floatValue());
		pb.setParameter("CurlW",  ((Number)jFormattedTextFieldCurlW.getValue()).floatValue());
		pb.setParameter("LandW",  ((Number)jFormattedTextFieldLandW.getValue()).floatValue());
		pb.setParameter("ImgW",   ((Number)jFormattedTextFieldImgW.getValue()).floatValue());
		pb.setParameter("ConsW",  ((Number)jFormattedTextFieldConsW.getValue()).floatValue());
		pb.setParameter("StopTh", ((Number)jFormattedTextFieldStopTh.getValue()).floatValue());
	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI(){
		
		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("RegMode") == 0 ) buttFast.setSelected(true);		
		if (pb.getIntParameter("RegMode") == 1 ) buttAccurate.setSelected(true);		
		if (pb.getIntParameter("RegMode") == 2 ) buttMono.setSelected(true);

		subSamp = pb.getIntParameter("SubSamp");
		spnrSubSamp.removeChangeListener(this);
		spnrSubSamp.setValue(subSamp);
		spnrSubSamp.addChangeListener(this);

		if (pb.getIntParameter("InitDef") == 0) buttInitDefVeryCoarse.setSelected(true);		
		if (pb.getIntParameter("InitDef") == 1) buttInitDefCoarse.setSelected(true);		
		if (pb.getIntParameter("InitDef") == 2) buttInitDefFine.setSelected(true);
		if (pb.getIntParameter("InitDef") == 3) buttInitDefVeryFine.setSelected(true);

		if (pb.getIntParameter("FinalDef") == 0) buttFinalDefVeryCoarse.setSelected(true);		
		if (pb.getIntParameter("FinalDef") == 1) buttFinalDefCoarse.setSelected(true);		
		if (pb.getIntParameter("FinalDef") == 2) buttFinalDefFine.setSelected(true);
		if (pb.getIntParameter("FinalDef") == 3) buttFinalDefVeryFine.setSelected(true);

		//	    jFormattedTextFieldDivW.removePropertyChangeListener("DivW", this);	 
		//	    jFormattedTextFieldCurlW.removePropertyChangeListener("CurlW", this);	    
		//	    jFormattedTextFieldLandW.removePropertyChangeListener("LandW", this);	    
		//	    jFormattedTextFieldImgW.removePropertyChangeListener("ImgW", this);	    
		//	    jFormattedTextFieldConsW.removePropertyChangeListener("ConsW", this);	   
		//	    jFormattedTextFieldStopTh.removePropertyChangeListener("StopTh", this);	    
		//	    
		//	    jFormattedTextFieldDivW.setValue(pbJAI.getFloatParameter("DivW"));
		//		jFormattedTextFieldCurlW.setValue(pbJAI.getFloatParameter("CurlW"));
		//	    jFormattedTextFieldLandW.setValue(pbJAI.getFloatParameter("LandW"));
		//	    jFormattedTextFieldImgW.setValue(pbJAI.getFloatParameter("ImgW"));
		//	    jFormattedTextFieldConsW.setValue(pbJAI.getFloatParameter("ConsW"));
		//	    jFormattedTextFieldStopTh.setValue(pbJAI.getFloatParameter("StopTh"));
		//
		//	    jFormattedTextFieldDivW.addPropertyChangeListener("DivW", this);
		//	    jFormattedTextFieldCurlW.addPropertyChangeListener("CurlW", this);
		//	    jFormattedTextFieldLandW.addPropertyChangeListener("LandW", this);
		//	    jFormattedTextFieldImgW.addPropertyChangeListener("ImgW", this);
		//	    jFormattedTextFieldConsW.addPropertyChangeListener("ConsW", this);
		//	    jFormattedTextFieldStopTh.addPropertyChangeListener("StopTh", this);

		jFormattedTextFieldDivW.removePropertyChangeListener("value", this);	 
		jFormattedTextFieldCurlW.removePropertyChangeListener("value", this);	    
		jFormattedTextFieldLandW.removePropertyChangeListener("value", this);	    
		jFormattedTextFieldImgW.removePropertyChangeListener("value", this);	    
		jFormattedTextFieldConsW.removePropertyChangeListener("value", this);	   
		jFormattedTextFieldStopTh.removePropertyChangeListener("value", this);	    

		jFormattedTextFieldDivW.setValue(pb.getFloatParameter("DivW"));
		jFormattedTextFieldCurlW.setValue(pb.getFloatParameter("CurlW"));
		jFormattedTextFieldLandW.setValue(pb.getFloatParameter("LandW"));
		jFormattedTextFieldImgW.setValue(pb.getFloatParameter("ImgW"));
		jFormattedTextFieldConsW.setValue(pb.getFloatParameter("ConsW"));
		jFormattedTextFieldStopTh.setValue(pb.getFloatParameter("StopTh"));

		jFormattedTextFieldDivW.addPropertyChangeListener("value", this);
		jFormattedTextFieldCurlW.addPropertyChangeListener("value", this);
		jFormattedTextFieldLandW.addPropertyChangeListener("value", this);
		jFormattedTextFieldImgW.addPropertyChangeListener("value", this);
		jFormattedTextFieldConsW.addPropertyChangeListener("value", this);
		jFormattedTextFieldStopTh.addPropertyChangeListener("value", this);
	}

	//------------------------------------------------------------------------------



	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update(){
		logger.debug("Updating GUI...");
		// here, it does nothing
	}
	
	/**
	 * This method initializes the Option: Fast 
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtFast() {
		buttFast = new JRadioButton();
		buttFast.setText("Fast");
		buttFast.setToolTipText("fast registration");
		buttFast.addActionListener(this);
		buttFast.setActionCommand("parameter");
		return buttFast;
	}
	/**
	 * This method initializes the Option: Accurate
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtAccurate() {
		buttAccurate = new JRadioButton();
		buttAccurate.setText("Accurate");
		buttAccurate.setToolTipText("accurate registration");
		buttAccurate.addActionListener(this);
		buttAccurate.setActionCommand("parameter");
		return buttAccurate;
	}
	/**
	 * This method initializes the Option: Mono
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtMono() {
		buttMono = new JRadioButton();
		buttMono.setText("Mono");
		buttMono.setToolTipText("unidirectional registration");
		buttMono.addActionListener(this);
		buttMono.setActionCommand("parameter");
		return buttMono;
	}
	
	/**
	 * This method initializes JPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private   JPanel getJPanelRegMode() {
		jPanelRegMode = new JPanel();
		jPanelRegMode.setLayout(new BoxLayout(jPanelRegMode, BoxLayout.Y_AXIS));
		//jPanelRegMode.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelRegMode.setBorder(new TitledBorder(null, "Registration modus", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelRegMode.add(getJRadioButtonButtFast());
		jPanelRegMode.add(getJRadioButtonButtAccurate());			
		jPanelRegMode.add(getJRadioButtonButtMono());

		this.setButtonGroupRegMode();	//Grouping of JRadioButtons
		return jPanelRegMode;
	}
	private  void setButtonGroupRegMode() {
		//if (ButtonGroup buttGroupRegMode == null) {
		buttGroupRegMode = new ButtonGroup();
		buttGroupRegMode.add(buttFast);
		buttGroupRegMode.add(buttAccurate);
		buttGroupRegMode.add(buttMono);
	}


	/**
	 * This method initializes pnlSubSamp	
	 * @return {@link javax.swing.JPanel}	
	 */
	private JPanel getSpnrSubSamp() {
		if (pnlSubSamp == null) {
			pnlSubSamp = new JPanel();	
			//pnlSubSamp.setLayout(new BorderLayout());
			//pnlSubSamp.setLayout(new BoxLayout(pnlSubSamp, BoxLayout.Y_AXIS));
			pnlSubSamp.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			pnlSubSamp.setBorder(new TitledBorder(null, "Subsampling factor", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			spnrSubSamp = new JSpinner();
			spnrSubSamp.setModel(new SpinnerNumberModel(0, 0, 10, 1));
			spnrSubSamp.setToolTipText("Subsampling factor");
			lblSubSamp = new JLabel();
			lblSubSamp.setLabelFor(spnrSubSamp);
			lblSubSamp.setText  ("Value: ");
			//lblSubSamp.setPreferredSize(new Dimension(120,10));
			lblSubSamp.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlSubSamp.add(lblSubSamp);
			pnlSubSamp.add(spnrSubSamp);		
		}
		return pnlSubSamp;
	}
	//-----------------------------------------------------------------------------------------
	
	/**
	 * This method initializes the Option: InitDefVeryCoarse 
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtInitDefVeryCoarse() {
		//if (buttInitDefVeryCoarse == null) {
		buttInitDefVeryCoarse = new JRadioButton();
		buttInitDefVeryCoarse.setText("Very Coarse");
		buttInitDefVeryCoarse.setToolTipText("Very coarse initial deformation");
		buttInitDefVeryCoarse.addActionListener(this);
		buttInitDefVeryCoarse.setActionCommand("parameter");
		//}
		return buttInitDefVeryCoarse;
	}
	/**
	 * This method initializes the Option: InitDefCoarse
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtInitDefCoarse() {
		//if (buttInitDefCoarse == null) {
		buttInitDefCoarse = new JRadioButton();
		buttInitDefCoarse.setText("Coarse");
		buttInitDefCoarse.setToolTipText("Coarse initial deformation");
		buttInitDefCoarse.addActionListener(this);
		buttInitDefCoarse.setActionCommand("parameter");
		//}
		return buttInitDefCoarse;
	}
	/**
	 * This method initializes the Option: InitDefFine
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtInitDefFine() {
		//if (buttInitDefFine == null) {
		buttInitDefFine = new JRadioButton();
		buttInitDefFine.setText("Fine");
		buttInitDefFine.setToolTipText("Fine initial deformation");
		buttInitDefFine.addActionListener(this);
		buttInitDefFine.setActionCommand("parameter");
		//}
		return buttInitDefFine;
	}
	/**
	 * This method initializes the Option: InitDefVeryFine
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtInitDefVeryFine() {
		//if (buttInitDefVeryFine == null) {
		buttInitDefVeryFine = new JRadioButton();
		buttInitDefVeryFine.setText("Very Fine");
		buttInitDefVeryFine.setToolTipText("Very fine initial deformation");
		buttInitDefVeryFine.addActionListener(this);
		buttInitDefVeryFine.setActionCommand("parameter");
		//}
		return buttInitDefVeryFine;
	}

	/**
	 * This method initializes JPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private   JPanel getJPanelInitDef() {
		//		if (jPanelInitDef == null) {
		jPanelInitDef = new JPanel();
		jPanelInitDef.setLayout(new BoxLayout(jPanelInitDef, BoxLayout.Y_AXIS));
		//jPanelInitDef.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelInitDef.setBorder(new TitledBorder(null, "Initial Deformation", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelInitDef.add(getJRadioButtonButtInitDefVeryCoarse());
		jPanelInitDef.add(getJRadioButtonButtInitDefCoarse());			
		jPanelInitDef.add(getJRadioButtonButtInitDefFine());
		jPanelInitDef.add(getJRadioButtonButtInitDefVeryFine());

		//jPanelRGB.addSeparator();
		this.setButtonGroupInitDef();	//Grouping of JRadioButtons
		//		}
		return jPanelInitDef;
	}
	private  void setButtonGroupInitDef() {
		//if (ButtonGroup buttGroupInitDef == null) {
		buttGroupInitDef = new ButtonGroup();
		buttGroupInitDef.add(buttInitDefVeryCoarse);
		buttGroupInitDef.add(buttInitDefCoarse);
		buttGroupInitDef.add(buttInitDefFine);
		buttGroupInitDef.add(buttInitDefVeryFine);
	}
	//-----------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: FinalDefVeryCoarse 
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtFinalDefVeryCoarse() {
		//if (buttFinalDefVeryCoarse == null) {
		buttFinalDefVeryCoarse = new JRadioButton();
		buttFinalDefVeryCoarse.setText("Very Coarse");
		buttFinalDefVeryCoarse.setToolTipText("Very coarse final deformation");
		buttFinalDefVeryCoarse.addActionListener(this);

		buttFinalDefVeryCoarse.setActionCommand("parameter");
		//}
		return buttFinalDefVeryCoarse;
	}
	/**
	 * This method initializes the Option: FinalDefCoarse
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtFinalDefCoarse() {
		//if (buttFinalDefCoarse == null) {
		buttFinalDefCoarse = new JRadioButton();
		buttFinalDefCoarse.setText("Coarse");
		buttFinalDefCoarse.setToolTipText("Coarse final deformation");
		buttFinalDefCoarse.addActionListener(this);
		buttFinalDefCoarse.setActionCommand("parameter");
		//}
		return buttFinalDefCoarse;
	}
	/**
	 * This method initializes the Option: FinalDefFine
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtFinalDefFine() {
		//if (buttFinalDefFine == null) {
		buttFinalDefFine = new JRadioButton();
		buttFinalDefFine.setText("Fine");
		buttFinalDefFine.setToolTipText("Fine final deformation");
		buttFinalDefFine.addActionListener(this);
		buttFinalDefFine.setActionCommand("parameter");
		//}
		return buttFinalDefFine;
	}
	/**
	 * This method initializes the Option: FinalDefVeryFine
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonButtFinalDefVeryFine() {
		//if (buttFinalDefVeryFine == null) {
		buttFinalDefVeryFine = new JRadioButton();
		buttFinalDefVeryFine.setText("Very Fine");
		buttFinalDefVeryFine.setToolTipText("Very fine final deformation");
		buttFinalDefVeryFine.addActionListener(this);
		buttFinalDefVeryFine.setActionCommand("parameter");
		//}
		return buttFinalDefVeryFine;
	}

	/**
	 * This method initializes JPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private   JPanel getJPanelFinalDef() {
		//		if (jPanelFinalDef == null) {
		jPanelFinalDef = new JPanel();
		jPanelFinalDef.setLayout(new BoxLayout(jPanelFinalDef, BoxLayout.Y_AXIS));
		//jPanelFinalDef.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelFinalDef.setBorder(new TitledBorder(null, "Final Deformation", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelFinalDef.add(getJRadioButtonButtFinalDefVeryCoarse());
		jPanelFinalDef.add(getJRadioButtonButtFinalDefCoarse());			
		jPanelFinalDef.add(getJRadioButtonButtFinalDefFine());
		jPanelFinalDef.add(getJRadioButtonButtFinalDefVeryFine());

		//jPanelRGB.addSeparator();
		this.setButtonGroupFinalDef();	//Grouping of JRadioButtons
		//		}
		return jPanelFinalDef;
	}
	private  void setButtonGroupFinalDef() {
		//if (ButtonGroup buttGroupFinalDef == null) {
		buttGroupFinalDef = new ButtonGroup();
		buttGroupFinalDef.add(buttFinalDefVeryCoarse);
		buttGroupFinalDef.add(buttFinalDefCoarse);
		buttGroupFinalDef.add(buttFinalDefFine);
		buttGroupFinalDef.add(buttFinalDefVeryFine);
	}
	//----------------------------------------------------------------------------------------


	/**
	 * This method initializes jJPanelDivW	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDivW() {
		if (jPanelDivW == null) {
			jPanelDivW = new JPanel();
			jPanelDivW.setBorder(new EmptyBorder(5, 5, 5, 5));
			BorderLayout bl_jPanelDivW = new BorderLayout();
			bl_jPanelDivW.setHgap(5);
			jPanelDivW.setLayout(bl_jPanelDivW);
			//jPanelDivW.setPreferredSize(new Dimension(250,18));					
			jLabelDivW = new JLabel("Divergency Weight");
			jLabelDivW.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldDivW = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldDivW.addPropertyChangeListener("value", this);
			jFormattedTextFieldDivW.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldDivW.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldDivW.setColumns(5);
			jPanelDivW.add(jLabelDivW, 			    BorderLayout.CENTER);
			jPanelDivW.add(jFormattedTextFieldDivW, BorderLayout.EAST);	

		}
		return jPanelDivW;
	}
	/**
	 * This method initializes jJPanelCurlW	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelCurlW() {
		if (jPanelCurlW == null) {
			jPanelCurlW = new JPanel();
			jPanelCurlW.setBorder(new EmptyBorder(0, 5, 0, 5));
			BorderLayout bl_jPanelCurlW = new BorderLayout();
			bl_jPanelCurlW.setHgap(5);
			jPanelCurlW.setLayout(bl_jPanelCurlW);
			//jPanelCurlW.setPreferredSize(new Dimension(250,18));					
			jLabelCurlW = new JLabel("Curl Weight");
			jLabelCurlW.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldCurlW = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldCurlW.addPropertyChangeListener("value", this);
			jFormattedTextFieldCurlW.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldCurlW.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldCurlW.setColumns(5);
			jPanelCurlW.add(jLabelCurlW, 			  BorderLayout.CENTER);
			jPanelCurlW.add(jFormattedTextFieldCurlW, BorderLayout.EAST);	

		}
		return jPanelCurlW;
	}
	/**
	 * This method initializes jJPanelLandW	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelLandW() {
		if (jPanelLandW == null) {
			jPanelLandW = new JPanel();
			jPanelLandW.setBorder(new EmptyBorder(5, 5, 5, 5));
			BorderLayout bl_jPanelLandW = new BorderLayout();
			bl_jPanelLandW.setHgap(5);
			jPanelLandW.setLayout(bl_jPanelLandW);
			//jPanelLandW.setPreferredSize(new Dimension(250,18));					
			jLabelLandW = new JLabel("Landmark Weight");
			jLabelLandW.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldLandW = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldLandW.addPropertyChangeListener("value", this);
			jFormattedTextFieldLandW.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldLandW.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldLandW.setColumns(5);
			jPanelLandW.add(jLabelLandW, 			  BorderLayout.CENTER);
			jPanelLandW.add(jFormattedTextFieldLandW, BorderLayout.EAST);	
			jFormattedTextFieldLandW.setEnabled(false);
		}
		return jPanelLandW;
	}
	/**
	 * This method initializes jJPanelImgW	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelImgW() {
		if (jPanelImgW == null) {
			jPanelImgW = new JPanel();
			jPanelImgW.setBorder(new EmptyBorder(0, 5, 0, 5));
			BorderLayout bl_jPanelImgW = new BorderLayout();
			bl_jPanelImgW.setHgap(5);
			jPanelImgW.setLayout(bl_jPanelImgW);
			//jPanelImgW.setPreferredSize(new Dimension(250,18));					
			jLabelImgW = new JLabel("Image Weight");
			jLabelImgW.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldImgW = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldImgW.addPropertyChangeListener("value", this);
			jFormattedTextFieldImgW.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldImgW.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldImgW.setColumns(5);
			jPanelImgW.add(jLabelImgW, 			    BorderLayout.CENTER);
			jPanelImgW.add(jFormattedTextFieldImgW, BorderLayout.EAST);	

		}
		return jPanelImgW;
	}
	/**
	 * This method initializes jJPanelConsW	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelConsW() {
		if (jPanelConsW == null) {
			jPanelConsW = new JPanel();
			jPanelConsW.setBorder(new EmptyBorder(5, 5, 5, 5));
			BorderLayout bl_jPanelConsW = new BorderLayout();
			bl_jPanelConsW.setHgap(5);
			jPanelConsW.setLayout(bl_jPanelConsW);
			jLabelConsW = new JLabel("Consistency Weight");
			jLabelConsW.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldConsW = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldConsW.addPropertyChangeListener("value", this);
			jFormattedTextFieldConsW.setInputVerifier(new FloatNumberVerifier());
			jFormattedTextFieldConsW.setColumns(5);
			jPanelConsW.add(jLabelConsW, 			  BorderLayout.CENTER);
			jPanelConsW.add(jFormattedTextFieldConsW, BorderLayout.EAST);	

		}
		return jPanelConsW;
	}
	/**
	 * This method initializes jJPanelStopTh	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelStopTh() {
		if (jPanelStopTh == null) {
			jPanelStopTh = new JPanel();
			jPanelStopTh.setBorder(new EmptyBorder(0, 5, 5, 5));
			BorderLayout bl_jPanelStopTh = new BorderLayout();
			bl_jPanelStopTh.setHgap(5);
			jPanelStopTh.setLayout(bl_jPanelStopTh);
			//jPanelStopTh.setPreferredSize(new Dimension(250,18));					
			jLabelStopTh = new JLabel("Stop Threshold");
			jLabelStopTh.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldStopTh = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldStopTh.addPropertyChangeListener("value", this);
			jFormattedTextFieldStopTh.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldStopTh.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldStopTh.setColumns(5);
			jPanelStopTh.add(jLabelStopTh, 			    BorderLayout.CENTER);
			jPanelStopTh.add(jFormattedTextFieldStopTh, BorderLayout.EAST);	

		}
		return jPanelStopTh;
	}



	//--------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
	//--------------------------------------------------------------------------------------------------
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		this.updateParameterBlock();

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
	private JPanel getWeightPanel() {
		if (weightPanel == null) {
			weightPanel = new JPanel();
			weightPanel.setBorder(new TitledBorder(null, "Weight settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			weightPanel.setLayout(new BoxLayout(weightPanel, BoxLayout.Y_AXIS));
			weightPanel.add(getJPanelDivW());
			weightPanel.add(getJPanelCurlW());
			weightPanel.add(getJPanelLandW());
			weightPanel.add(getJPanelImgW());
			weightPanel.add(getJPanelConsW());
			weightPanel.add(getJPanelStopTh());
		}
		return weightPanel;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		logger.debug("State changed.");
		Object obE = e.getSource();
		if (obE == spnrSubSamp){
			subSamp = (Integer) spnrSubSamp.getValue();
		}		
		this.updateParameterBlock();	
		
		//execute Auto Preview or not
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
}//END
