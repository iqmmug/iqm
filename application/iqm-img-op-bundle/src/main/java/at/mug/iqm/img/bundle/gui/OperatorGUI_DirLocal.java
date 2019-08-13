package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_DirLocal.java
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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpDirLocalDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2010 05
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_DirLocal extends AbstractImageOperatorGUI 
implements
ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2427781667343644360L;

	private  ParameterBlockIQM pb = null; 

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_DirLocal.class);

	private int			 kernelSize = 0;

	private JPanel 		 jPanelGradient 	   = null;
	private ButtonGroup  buttGroupGradient     = null;
	private JRadioButton buttRoberts		   = null;
	private JRadioButton buttPixDiff		   = null;
	private JRadioButton buttSepPix			   = null;
	private JRadioButton buttSobel		       = null;
	private JRadioButton buttPrewitt		   = null;
	private JRadioButton buttFreiChen		   = null;

	private JPanel 	     jPanelKernelSize;
	private JSpinner     jSpinnerKernelSize;
	private JLabel       jLabelKernelSize;

	private JPanel 	     jPanelEigen 	   = null;
	private ButtonGroup  buttGroupEigen    = null;
	private JRadioButton buttJakobi		   = null;
	private JRadioButton buttInvIter	   = null;
	private JRadioButton buttQR			   = null;

	private JPanel 		 jPanelOutImg 	   = null;
	private ButtonGroup  buttGroupOutImg   = null;
	private JRadioButton buttAngle		   = null;
	private JRadioButton buttConf	       = null;

	private  JCheckBox 	 jCheckBoxShowVecPlot   = null;
	private JPanel 	     jPanelCheckBoxes       = null;

	/**
	 * constructor
	 */
	public OperatorGUI_DirLocal() {
		logger.debug("Now initializing...");
		
		this.setOpName(new IqmOpDirLocalDescriptor().getName());
		
		this.initialize(); 

		this.setTitle("Local Directions");
		
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelGradient() ,      getGridBagConstraints_Gradient());
		this.getOpGUIContent().add(getSpnrKernelSize(),       getGridBagConstraints_KernelSize());
		this.getOpGUIContent().add(getJPanelEigen() ,         getGridBagConstraints_Eigen());
		this.getOpGUIContent().add(getJPanelOutImg() ,        getGridBagConstraints_OutImg());
		this.getOpGUIContent().add(getJPanelCheckBoxes(),     getGridBagConstraints_CheckBoxes());	

		this.pack();
		
//		IJ.run("Options...", "iterations=1 black count=1"); //Options for Skeletonize Fill holes
	}
	/**
	 * This method sets the current parameter block
	 * The individual values of the GUI  current ParameterBlock
	 *
	 */
	@Override
	public void updateParameterBlock(){
		if (buttRoberts.isSelected())  pb.setParameter("Gradient", 0);
		if (buttPixDiff.isSelected())  pb.setParameter("Gradient", 1);
		if (buttSepPix.isSelected())   pb.setParameter("Gradient", 2);
		if (buttSobel.isSelected())    pb.setParameter("Gradient", 3);
		if (buttPrewitt.isSelected())  pb.setParameter("Gradient", 4);
		if (buttFreiChen.isSelected()) pb.setParameter("Gradient", 5);

		pb.setParameter("KernelSize", kernelSize);	

		if (buttJakobi.isSelected())  pb.setParameter("Eigen", 0);
		if (buttInvIter.isSelected()) pb.setParameter("Eigen", 1);
		if (buttQR.isSelected())      pb.setParameter("Eigen", 2);

		if (buttAngle.isSelected()) pb.setParameter("OutImg", 0);
		if (buttConf.isSelected())  pb.setParameter("OutImg", 1);

		if (jCheckBoxShowVecPlot.isSelected())  pb.setParameter("ShowVecPlot", 1);
		if (!jCheckBoxShowVecPlot.isSelected()) pb.setParameter("ShowVecPlot", 0);
	}

	/**
	 * This method sets the current parameter values
	 *
	 */
	@Override
	public void setParameterValuesToGUI(){
		this.pb = this.workPackage.getParameters();
		
		if (pb.getIntParameter("Gradient") == 0) buttRoberts.setSelected(true);
		if (pb.getIntParameter("Gradient") == 1) buttPixDiff.setSelected(true);
		if (pb.getIntParameter("Gradient") == 2) buttSepPix.setSelected(true);
		if (pb.getIntParameter("Gradient") == 3) buttSobel.setSelected(true);
		if (pb.getIntParameter("Gradient") == 4) buttPrewitt.setSelected(true);
		if (pb.getIntParameter("Gradient") == 5) buttFreiChen.setSelected(true);

		kernelSize = pb.getIntParameter("KernelSize");
		jSpinnerKernelSize.removeChangeListener(this);
		jSpinnerKernelSize.setValue(kernelSize);
		jSpinnerKernelSize.addChangeListener(this);
		jSpinnerKernelSize.setToolTipText("Kernel size: "+ kernelSize+"x"+kernelSize);

		if (pb.getIntParameter("Eigen") == 0) buttJakobi.setSelected(true);
		if (pb.getIntParameter("Eigen") == 1) buttInvIter.setSelected(true);
		if (pb.getIntParameter("Eigen") == 2) buttQR.setSelected(true);

		if (pb.getIntParameter("OutImg") == 0) buttAngle.setSelected(true);
		if (pb.getIntParameter("OutImg") == 1) buttConf.setSelected(true);

		if (pb.getIntParameter("ShowVecPlot")   == 0) jCheckBoxShowVecPlot.setSelected(false);
		if (pb.getIntParameter("ShowVecPlot")   == 1) jCheckBoxShowVecPlot.setSelected(true);
	}

	private  GridBagConstraints getGridBagConstraints_Gradient(){
		GridBagConstraints gbc_Gradient = new GridBagConstraints();
		gbc_Gradient.gridx = 0;
		gbc_Gradient.gridy = 0;
		gbc_Gradient.insets = new Insets(10, 0, 0, 0); //top left bottom right
		gbc_Gradient.fill = GridBagConstraints.BOTH;
		return gbc_Gradient;
	}
	private  GridBagConstraints getGridBagConstraints_KernelSize(){
		GridBagConstraints gbc_KernelSize = new GridBagConstraints();
		gbc_KernelSize.gridx = 0;
		gbc_KernelSize.gridy = 1;
		gbc_KernelSize.insets = new Insets(5, 0, 0, 0); //top left bottom right
		gbc_KernelSize.fill = GridBagConstraints.BOTH;
		return gbc_KernelSize;
	}
	private  GridBagConstraints getGridBagConstraints_Eigen(){
		GridBagConstraints gbc_Eigen = new GridBagConstraints();
		gbc_Eigen.gridx = 0;
		gbc_Eigen.gridy = 2;
		gbc_Eigen.insets = new Insets(5, 0, 0, 0); //top left bottom right
		gbc_Eigen.fill = GridBagConstraints.BOTH;
		return gbc_Eigen;
	}
	private  GridBagConstraints getGridBagConstraints_OutImg(){
		GridBagConstraints gbc_OutImg = new GridBagConstraints();
		gbc_OutImg.gridx = 0;
		gbc_OutImg.gridy = 3;
		gbc_OutImg.insets = new Insets(5, 0, 0, 0); //top left bottom right
		gbc_OutImg.fill = GridBagConstraints.BOTH;
		return gbc_OutImg;
	}
	private  GridBagConstraints getGridBagConstraints_CheckBoxes(){
		GridBagConstraints gbc_CheckBoxes = new GridBagConstraints();
		gbc_CheckBoxes.gridx = 0;
		gbc_CheckBoxes.gridy = 4;
		gbc_CheckBoxes.insets = new Insets(5, 0, 5, 0); //top left bottom right
		gbc_CheckBoxes.fill = GridBagConstraints.BOTH;
		return gbc_CheckBoxes;
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

	//------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Roberts
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonRoberts() {
		if (buttRoberts == null) {
			buttRoberts  = new JRadioButton();
			buttRoberts.setText("Roberts");
			buttRoberts.setToolTipText("Roberts");
			buttRoberts.addActionListener(this);
			buttRoberts.setActionCommand("parameter");
		}
		return buttRoberts;
	}
	/**
	 * This method initializes the Option: PixDiff
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonPixDiff() {
		if (buttPixDiff == null) {
			buttPixDiff  = new JRadioButton();
			buttPixDiff.setText("Pixel Difference");
			buttPixDiff.setToolTipText("Pixel difference, see WK Pratt Digital Image Processing 2nd ed. p503");
			buttPixDiff.addActionListener(this);
			buttPixDiff.setActionCommand("parameter");
		}
		return buttPixDiff;
	}
	/**
	 * This method initializes the Option: SepPix
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonSepPix() {
		if (buttSepPix == null) {
			buttSepPix  = new JRadioButton();
			buttSepPix.setText("Separated Pixeld Difference");
			buttSepPix.setToolTipText("Separated pixel difference, see WK Pratt Digital Image Processing 2nd ed. p503");
			buttSepPix.addActionListener(this);
			buttSepPix.setActionCommand("parameter");
		}
		return buttSepPix;
	}
	/**
	 * This method initializes the Option: Sobel
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonSobel() {
		if (buttSobel == null) {
			buttSobel  = new JRadioButton();
			buttSobel.setText("Sobel");
			buttSobel.setToolTipText("Sobel");
			buttSobel.addActionListener(this);
			buttSobel.setActionCommand("parameter");
		}
		return buttSobel;
	}
	/**
	 * This method initializes the Option: Prewitt
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonPrewitt() {
		if (buttPrewitt == null) {
			buttPrewitt  = new JRadioButton();
			buttPrewitt.setText("Prewitt");
			buttPrewitt.setToolTipText("Prewitt");
			buttPrewitt.addActionListener(this);
			buttPrewitt.setActionCommand("parameter");
		}
		return buttPrewitt;
	}
	/**
	 * This method initializes the Option: FreiChen
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonFreiChen() {
		if (buttFreiChen == null) {
			buttFreiChen  = new JRadioButton();
			buttFreiChen.setText("FreiChen");
			buttFreiChen.setToolTipText("FreiChen");
			buttFreiChen.addActionListener(this);
			buttFreiChen.setActionCommand("parameter");
		}
		return buttFreiChen;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private   JPanel getJPanelGradient() {
		//		if (jPanelGradient == null) {
		jPanelGradient = new JPanel();
		jPanelGradient.setLayout(new BoxLayout(jPanelGradient, BoxLayout.Y_AXIS));
		//jPanelGradient.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelGradient.setBorder(new TitledBorder(null, "Gradient", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelGradient.add(getJRadioButtonRoberts());			
		jPanelGradient.add(getJRadioButtonPixDiff());			
		jPanelGradient.add(getJRadioButtonSepPix());			
		jPanelGradient.add(getJRadioButtonSobel());			
		jPanelGradient.add(getJRadioButtonPrewitt());			
		jPanelGradient.add(getJRadioButtonFreiChen());			

		this.setButtonGroupGradient();	//Grouping of JRadioButtons
		return jPanelGradient;
	}
	private  void setButtonGroupGradient() {
		//if (ButtonGroup buttGroupGradient == null) {
		buttGroupGradient = new ButtonGroup();
		buttGroupGradient.add(buttRoberts);
		buttGroupGradient.add(buttPixDiff);
		buttGroupGradient.add(buttSepPix);
		buttGroupGradient.add(buttSobel);
		buttGroupGradient.add(buttPrewitt);
		buttGroupGradient.add(buttFreiChen);
	}

	//------------------------------------------------------------------------------------------------
	/**
	 * This method initializes spnrKernelSize	
	 * @return {@link javax.swing.JPanel}	
	 */
	private JPanel getSpnrKernelSize() {
		if (jPanelKernelSize == null) {
			jPanelKernelSize = new JPanel();	
			//BorderLayout bl_ = new BorderLayout();
			//bl_.setHgap(5);
			//jPanelKernelSize.setLayout(bl_);
			//jPanelKernelSize.setLayout(new BoxLayout(jPanelKernelSize, BoxLayout.Y_AXIS));
			jPanelKernelSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));
			jPanelKernelSize.setBorder(new TitledBorder(null, "Kernel size (square)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jSpinnerKernelSize = new JSpinner(new SpinnerNumberModel(3, 3, 101, 2));
			jSpinnerKernelSize.setToolTipText("Size of structuring element");
			jSpinnerKernelSize.setEditor(new JSpinner.NumberEditor(jSpinnerKernelSize, "0"));
			((JSpinner.NumberEditor) jSpinnerKernelSize.getEditor()).getTextField().setEditable(false);
			jSpinnerKernelSize.setEnabled(false); 
			jLabelKernelSize = new JLabel();
			jLabelKernelSize.setText("Value:");
			jPanelKernelSize.add(jLabelKernelSize);
			jPanelKernelSize.add(jSpinnerKernelSize);	
		}
		return jPanelKernelSize;
	}
	//------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Jakobi
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonJakobi() {
		if (buttJakobi == null) {
			buttJakobi  = new JRadioButton();
			buttJakobi.setText("Jakobi");
			buttJakobi.setToolTipText("Jakobi method");
			buttJakobi.addActionListener(this);
			buttJakobi.setActionCommand("parameter");
		}
		return buttJakobi;
	}
	/**
	 * This method initializes the Option: InvIter
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonInvIter() {
		if (buttInvIter == null) {
			buttInvIter  = new JRadioButton();
			buttInvIter.setText("Inverse Iteration");
			buttInvIter.setToolTipText("inverse iteration");
			buttInvIter.addActionListener(this);
			buttInvIter.setActionCommand("parameter");
		}
		return buttInvIter;
	}
	/**
	 * This method initializes the Option: QR
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonQR() {
		if (buttQR == null) {
			buttQR  = new JRadioButton();
			buttQR.setText("QR");
			buttQR.setToolTipText("QR method");
			buttQR.addActionListener(this);
			buttQR.setActionCommand("parameter");
		}
		return buttQR;
	}

	/**
	 * This method initializes JPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private   JPanel getJPanelEigen() {
		jPanelEigen = new JPanel();
		jPanelEigen.setLayout(new BoxLayout(jPanelEigen, BoxLayout.Y_AXIS));
		//jPanelEigen.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelEigen.setBorder(new TitledBorder(null, "Algorithmus", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelEigen.add(getJRadioButtonJakobi());			
		jPanelEigen.add(getJRadioButtonInvIter());			
		jPanelEigen.add(getJRadioButtonQR());				

		this.setButtonGroupEigen();	//Grouping of JRadioButtons
		return jPanelEigen;
	}
	private  void setButtonGroupEigen() {
		buttGroupEigen = new ButtonGroup();
		buttGroupEigen.add(buttJakobi);
		buttGroupEigen.add(buttInvIter);
		buttGroupEigen.add(buttQR);
	}
	
	//------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Angle
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonAngle() {
		if (buttAngle == null) {
			buttAngle  = new JRadioButton();
			buttAngle.setText("Angle");
			buttAngle.setToolTipText("each pixel grey value represents the angle of largest direction");
			buttAngle.addActionListener(this);
			buttJakobi.setActionCommand("parameter");
		}
		return buttAngle;
	}
	/**
	 * This method initializes the Option: Conf
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private  JRadioButton getJRadioButtonConf() {
		if (buttConf == null) {
			buttConf  = new JRadioButton();
			buttConf.setText("Confidence Value");
			buttConf.setToolTipText("cofidence value of direction = eigval1/(eigval1+eigval2)");
			buttConf.addActionListener(this);
			buttConf.setActionCommand("parameter");
		}
		return buttConf;
	}

	/**
	 * This method initializes JPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private   JPanel getJPanelOutImg() {
		jPanelOutImg = new JPanel();
		//jPanelOutImg.setLayout(new BoxLayout(jPanelOutImg, BoxLayout.Y_AXIS));
		jPanelOutImg.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		jPanelOutImg.setBorder(new TitledBorder(null, "Output image", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelOutImg.add(getJRadioButtonAngle());			
		jPanelOutImg.add(getJRadioButtonConf());					
		this.setButtonGroupOutImg();	//Grouping of JRadioButtons
		return jPanelOutImg;
	}
	private void setButtonGroupOutImg() {
		buttGroupOutImg = new ButtonGroup();
		buttGroupOutImg.add(buttAngle);
		buttGroupOutImg.add(buttConf);
	}

	//------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxShowVecPlot	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private  JCheckBox getJCheckBoxShowVecPlot() {
		if (jCheckBoxShowVecPlot == null) {
			jCheckBoxShowVecPlot = new JCheckBox();
			jCheckBoxShowVecPlot.setText("Show Vector Plot");
			jCheckBoxShowVecPlot.addActionListener(this);			
			jCheckBoxShowVecPlot.setActionCommand("parameter");			
		}
		return jCheckBoxShowVecPlot;
	}
	
	/**
	 * This method initializes JPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private   JPanel getJPanelCheckBoxes() {
		jPanelCheckBoxes = new JPanel();
		//jPanelCheckBoxes.setLayout(new BoxLayout(jPanelCheckBoxes, BoxLayout.Y_AXIS));
		jPanelCheckBoxes.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		jPanelCheckBoxes.setBorder(new TitledBorder(null, "Option", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelCheckBoxes.add(getJCheckBoxShowVecPlot());					
		return jPanelCheckBoxes;
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
	public void stateChanged(ChangeEvent e) {
		Object obE = e.getSource();
		if (obE == jSpinnerKernelSize){
			kernelSize = ((Number) jSpinnerKernelSize.getValue()).intValue();
			jSpinnerKernelSize.setToolTipText("Kernel size: "+ kernelSize+"x"+kernelSize);
		}
		
		this.updateParameterBlock();

		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
}//END
