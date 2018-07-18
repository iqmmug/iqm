package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_CoGReg.java
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


import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpCoGRegDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 07
 */
public class OperatorGUI_CoGReg extends AbstractImageOperatorGUI implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6045776218926915933L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_CoGReg.class);

	private  ParameterBlockIQM     pbJAI       = null;  

	private  JCheckBox 	    jCheckBoxFirst	 = null;

	/**
	 * constructor
	 */
	public OperatorGUI_CoGReg() {
		logger.debug("Now initializing...");
		
		this.setOpName(new IqmOpCoGRegDescriptor().getName());
		
		this.initialize(); 
		
		this.setTitle("Center of Gravity Image Registration");  

		this.getOpGUIContent().add(getJCheckBoxFirst(),   getGridBagConstraintsFirst());	

		this.pack();
		
	}
	
	/**
	 * This method sets the current parameter block
	 * The individual values of the GUI  current ParameterBlock
	 *
	 */
	@Override
	public void updateParameterBlock(){
		if (jCheckBoxFirst.isSelected())    pbJAI.setParameter("First", 1);
		if (!jCheckBoxFirst.isSelected())   pbJAI.setParameter("First", 0);		
	}

	/**
	 * This method sets the current parameter values
	 *
	 */
	@Override
	public void setParameterValuesToGUI(){
		this.pbJAI = this.workPackage.getParameters();
		
		if (pbJAI.getIntParameter("First")  == 0) jCheckBoxFirst.setSelected(false);
		if (pbJAI.getIntParameter("First")  == 1) jCheckBoxFirst.setSelected(true);
	}

	private  GridBagConstraints getGridBagConstraintsFirst(){
		GridBagConstraints gridBagConstraintsFirst = new GridBagConstraints();
		gridBagConstraintsFirst.gridx = 0;
		gridBagConstraintsFirst.gridy = 0;
		gridBagConstraintsFirst.anchor = GridBagConstraints.CENTER;
		gridBagConstraintsFirst.insets = new Insets(10,0,20,0); //top left bottom right
		return gridBagConstraintsFirst;
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
	 * This method initializes jCheckBoxFirst	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private  JCheckBox getJCheckBoxFirst() {
		if (jCheckBoxFirst == null) {
			jCheckBoxFirst = new JCheckBox();
			jCheckBoxFirst.setText("center first image");
			jCheckBoxFirst.addActionListener(this);			
			jCheckBoxFirst.setActionCommand("parameter");	
			jCheckBoxFirst.setEnabled(false);
		}
		return jCheckBoxFirst;
	}

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
	public void stateChanged(ChangeEvent e) {

		this.updateParameterBlock();

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
}//END
