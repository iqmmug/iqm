package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_DFT.java
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


import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpDFTDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 11
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_DFT extends AbstractImageOperatorGUI 
implements
ActionListener,
AdjustmentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4782882532625875921L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_DFT.class);

	private  ParameterBlockIQM    pb         = null; 

	private  JRadioButton buttScaleNone		= null;
	private  JRadioButton buttScaleUnit		= null;
	private  JRadioButton buttScaleDim	    = null;
	private  JPanel 	  jPanelScale	    = null;
	private  ButtonGroup  buttGroupScale	= null;

	private JPanel 		 jPanelResultOptions 	 = null;
	private ButtonGroup  buttGroupResultOptions	 = null;
	private JRadioButton buttMagnitudeSquared	 = null;
	private JRadioButton buttMagnitude			 = null;
	private JRadioButton buttPhase			     = null;
	private JRadioButton buttReal			     = null;
	private JRadioButton buttImaginary			 = null;


	/**
	 * constructor
	 */
	public OperatorGUI_DFT() {
		getOpGUIContent().setBorder(null);
		logger.debug("Now initializing...");
		
		this.setOpName(new IqmOpDFTDescriptor().getName());	
		this.initialize(); 	
		this.setTitle("Discrete Fourier Transformation");
		
		getOpGUIContent().setLayout(new GridBagLayout());
	
		getOpGUIContent().add(getJPanelScale(),         getGridBagConstraints_Scale());
		getOpGUIContent().add(getJPanelResultOptions(), getGridBagConstraints_ResultOptions());

		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_Scale() {
		GridBagConstraints gbc_Scale = new GridBagConstraints();
		gbc_Scale.gridx = 0;
		gbc_Scale.gridy = 0;
		gbc_Scale.gridwidth = 1;// ?
		gbc_Scale.insets = new Insets(10, 0, 0, 0); // top left  bottom  right
		gbc_Scale.fill = GridBagConstraints.BOTH;
		return gbc_Scale;
	}

	

	private GridBagConstraints getGridBagConstraints_ResultOptions() {
		GridBagConstraints gbcResultOptions = new GridBagConstraints();
		gbcResultOptions.gridx = 1;
		gbcResultOptions.gridy = 0;
		gbcResultOptions.gridwidth = 1;//
		gbcResultOptions.insets = new Insets(10, 0, 0, 0); // top  left  bottom  right
		gbcResultOptions.fill = GridBagConstraints.BOTH;
		return gbcResultOptions;
	}

	
	/**
	 * This method sets the current parameter block
	 * The individual values of the GUI  current ParameterBlock
	 *
	 */
	@Override
	public void updateParameterBlock(){

		if (buttScaleNone.isSelected()) pb.setParameter("Scale", 0);
		if (buttScaleUnit.isSelected()) pb.setParameter("Scale", 1);
		if (buttScaleDim.isSelected())	pb.setParameter("Scale", 2);

		if (buttMagnitudeSquared.isSelected()) pb.setParameter("ResultOptions", 0);
		if (buttMagnitude.isSelected())        pb.setParameter("ResultOptions", 1);
		if (buttPhase.isSelected())            pb.setParameter("ResultOptions", 2);
		if (buttReal.isSelected())             pb.setParameter("ResultOptions", 3);
		if (buttImaginary.isSelected())        pb.setParameter("ResultOptions", 4);
	}

	/**
	 * This method sets the current parameter values
	 *
	 */
	@Override
	public void setParameterValuesToGUI(){
		this.pb = this.workPackage.getParameters();
		
		if (pb.getIntParameter("Scale") == 0)     buttScaleNone.setSelected(true);
		if (pb.getIntParameter("Scale") == 1)     buttScaleUnit.setSelected(true);
		if (pb.getIntParameter("Scale") == 2)     buttScaleDim.setSelected(true);

		if (pb.getIntParameter("ResultOptions") == 0)     buttMagnitudeSquared.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 1)     buttMagnitude.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 2)     buttPhase.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 3)     buttReal.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 4)     buttImaginary.setSelected(true);
	}

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
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelScale() {
		if (jPanelScale == null) {
				
			buttScaleNone = new JRadioButton();
			buttScaleNone.setText("None");
			buttScaleNone.setToolTipText("no scaling");
			buttScaleNone.addActionListener(this);
			buttScaleNone.setActionCommand("parameter");
			
			buttScaleUnit = new JRadioButton();
			buttScaleUnit.setText("Unitary");
			buttScaleUnit.setToolTipText("scaling with sqrt(image width + image height)");
			buttScaleUnit.addActionListener(this);
			buttScaleUnit.setActionCommand("parameter");
			
			buttScaleDim = new JRadioButton();
			buttScaleDim.setText("Dimensions");
			buttScaleDim.setToolTipText("scaling with (image width*image height)");
			buttScaleDim.addActionListener(this);
			buttScaleDim.setActionCommand("parameter");
			
			jPanelScale = new JPanel();
			jPanelScale.setLayout(new BoxLayout(jPanelScale, BoxLayout.Y_AXIS));
			//jPanelScale.setAlignmentY(Component.CENTER_ALIGNMENT);
			jPanelScale.setBorder(new TitledBorder(null, "Scaling", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jPanelScale.add(buttScaleNone);			
			jPanelScale.add(buttScaleUnit);			
			jPanelScale.add(buttScaleDim);

			setButtonGroupScale();
			
		}
		return jPanelScale;
	}
	
	
	private  void setButtonGroupScale() {
		//if (ButtonGroup buttGroupScale == null) {
		buttGroupScale = new ButtonGroup();
		buttGroupScale.add(buttScaleNone);
		buttGroupScale.add(buttScaleUnit);
		buttGroupScale.add(buttScaleDim);
	}

	//------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: MagnitudeSquared
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonMagnitudeSquared() {
		if (buttMagnitudeSquared == null) {
			buttMagnitudeSquared  = new JRadioButton();
			buttMagnitudeSquared.setText("Magnitude^2");
			buttMagnitudeSquared.setToolTipText("calculates the squared magnitude");
			buttMagnitudeSquared.addActionListener(this);
			buttMagnitudeSquared.setActionCommand("parameter");
		}
		return buttMagnitudeSquared;
	}
	/**
	 * This method initializes the Option: Magnitude
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonMagnitude() {
		if (buttMagnitude == null) {
			buttMagnitude  = new JRadioButton();
			buttMagnitude.setText("Magnitude");
			buttMagnitude.setToolTipText("calculates the magnitude");
			buttMagnitude.addActionListener(this);
			buttMagnitude.setActionCommand("parameter");
		}
		return buttMagnitude;
	}
	/**
	 * This method initializes the Option: Phase
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonPhase() {
		if (buttPhase == null) {
			buttPhase  = new JRadioButton();
			buttPhase.setText("Phase");
			buttPhase.setToolTipText("calculates the phase");
			buttPhase.addActionListener(this);
			buttPhase.setActionCommand("parameter");
			//buttPhase.setEnabled(false);
		}
		return buttPhase;
	}
	/**
	 * This method initializes the Option: Real
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonReal() {
		if (buttReal == null) {
			buttReal  = new JRadioButton();
			buttReal.setText("Real");
			buttReal.setToolTipText("calculates the real part");
			buttReal.addActionListener(this);
			buttReal.setActionCommand("parameter");
			//buttReal.setEnabled(false);
		}
		return buttReal;
	}
	/**
	 * This method initializes the Option: Imaginary
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonImaginary() {
		if (buttImaginary == null) {
			buttImaginary  = new JRadioButton();
			buttImaginary.setText("Imaginary");
			buttImaginary.setToolTipText("calculates the imaginary part");
			buttImaginary.addActionListener(this);
			buttImaginary.setActionCommand("parameter");
			//buttImaginary.setEnabled(false);
		}
		return buttImaginary;
	}

	/**
	 * This method initializes JPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private   JPanel getJPanelResultOptions() {
		jPanelResultOptions = new JPanel();
		jPanelResultOptions.setLayout(new BoxLayout(jPanelResultOptions, BoxLayout.Y_AXIS));
		//jPanelResultOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelResultOptions.setBorder(new TitledBorder(null, "Result image", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelResultOptions.add(getJRadioButtonMagnitudeSquared());			
		jPanelResultOptions.add(getJRadioButtonMagnitude());			
		jPanelResultOptions.add(getJRadioButtonPhase());			
		jPanelResultOptions.add(getJRadioButtonReal());			
		jPanelResultOptions.add(getJRadioButtonImaginary());

		this.setButtonGroupResultOptions();	//Grouping of JRadioButtons
		return jPanelResultOptions;
	}
	private  void setButtonGroupResultOptions() {
		buttGroupResultOptions = new ButtonGroup();
		buttGroupResultOptions.add(buttMagnitudeSquared);			
		buttGroupResultOptions.add(buttMagnitude);
		buttGroupResultOptions.add(buttPhase);
		buttGroupResultOptions.add(buttReal);
		buttGroupResultOptions.add(buttImaginary);
	}





	//--------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}


	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		this.updateParameterBlock();

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
	
	
	
}//END
