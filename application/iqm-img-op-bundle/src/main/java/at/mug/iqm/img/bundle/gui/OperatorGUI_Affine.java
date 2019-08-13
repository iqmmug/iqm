package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Affine.java
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
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.FloatNumberVerifier;
import at.mug.iqm.img.bundle.descriptors.IqmOpAffineDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 06
 * @update 2014 12 change buttons to JRadioButtons and added some TitledBorder
 */
public class OperatorGUI_Affine 
extends AbstractImageOperatorGUI	
implements 
ActionListener, 
PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3290162188898205594L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_Affine.class);

	private  ParameterBlockIQM   pb         = null;  

	private JPanel 				 	jPanelM00					= null;	
	private JLabel				 	jLabelM00					= null;
	private JFormattedTextField 	jFormattedTextFieldM00 	    = null;	
	private JPanel 				 	jPanelM10					= null;	
	private JLabel				 	jLabelM10					= null;
	private JFormattedTextField 	jFormattedTextFieldM10 		= null;	
	private JPanel 				 	jPanelM01					= null;	
	private JLabel				 	jLabelM01					= null;
	private JFormattedTextField 	jFormattedTextFieldM01 		= null;
	private JPanel 				 	jPanelM11					= null;	
	private JLabel				 	jLabelM11					= null;
	private JFormattedTextField 	jFormattedTextFieldM11 		= null;
	private JPanel 				 	jPanelM02					= null;	
	private JLabel				 	jLabelM02					= null;
	private JFormattedTextField 	jFormattedTextFieldM02 		= null;
	private JPanel 				 	jPanelM12					= null;	
	private JLabel				 	jLabelM12					= null;
	private JFormattedTextField 	jFormattedTextFieldM12 		= null;
	
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_Parameters;
	
	private  JRadioButton  buttNN         = null;	//nearest neighbor
	private  JRadioButton  buttBL         = null;	//bilinear
	private  JRadioButton  buttBC      	  = null;	//bicubic
	private  JRadioButton  buttBC2   	  = null;	//bicubic2
	private  JPanel        jPanelIntP     = null;
	private  ButtonGroup   buttGroupIntP  = null;

	/**
	 * constructor
	 */
	public OperatorGUI_Affine() {
		getScrollPane().setPreferredSize(new Dimension(400, 200));
		getBtnPreview().setFont(new Font("Tahoma", Font.BOLD, 12));
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpAffineDescriptor().getName());
		this.initialize(); 
		this.setTitle("Affine Transformation");

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 1.0};
		gridBagLayout.rowHeights = new int[]{0, 102};
		this.getOpGUIContent().setLayout(gridBagLayout);
		
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();	
		gbc_panel_3.fill = GridBagConstraints.VERTICAL;
		gbc_panel_3.insets = new Insets(10, 0, 0, 0); // top left bottom right
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 0;
		getOpGUIContent().add(getPanel_Parameters(), gbc_panel_3);
	
		GridBagConstraints gbc_interpolation_panel = new GridBagConstraints();
		gbc_interpolation_panel.insets = new Insets(10, 0, 5, 0); // top left bottom right
		//gbc_interpolation_panel.anchor = GridBagConstraints.BASELINE;
		gbc_interpolation_panel.gridx = 0;
		gbc_interpolation_panel.gridy = 1;
		getOpGUIContent().add(getInterpolation_panel(), gbc_interpolation_panel);

		this.pack();
	}

	/**
	 * This method sets the current parameter block
	 * The individual values of the GUI  current ParameterBlock
	 *
	 */
	@Override
	public void updateParameterBlock(){
		pb.setParameter("M00", ((Number)jFormattedTextFieldM00.getValue()).floatValue());
		pb.setParameter("M10", ((Number)jFormattedTextFieldM10.getValue()).floatValue());
		pb.setParameter("M01", ((Number)jFormattedTextFieldM01.getValue()).floatValue());
		pb.setParameter("M11", ((Number)jFormattedTextFieldM11.getValue()).floatValue());
		pb.setParameter("M02", ((Number)jFormattedTextFieldM02.getValue()).floatValue());
		pb.setParameter("M12", ((Number)jFormattedTextFieldM12.getValue()).floatValue());
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

		jFormattedTextFieldM00.removePropertyChangeListener("value", this);	 
		jFormattedTextFieldM10.removePropertyChangeListener("value", this);	    
		jFormattedTextFieldM01.removePropertyChangeListener("value", this);	    	    
		jFormattedTextFieldM11.removePropertyChangeListener("value", this);	    	    
		jFormattedTextFieldM02.removePropertyChangeListener("value", this);	    	    
		jFormattedTextFieldM12.removePropertyChangeListener("value", this);	    	    
		jFormattedTextFieldM00.setValue(pb.getFloatParameter("M00"));
		jFormattedTextFieldM10.setValue(pb.getFloatParameter("M10"));
		jFormattedTextFieldM01.setValue(pb.getFloatParameter("M01"));
		jFormattedTextFieldM11.setValue(pb.getFloatParameter("M11"));
		jFormattedTextFieldM02.setValue(pb.getFloatParameter("M02"));
		jFormattedTextFieldM12.setValue(pb.getFloatParameter("M12"));
		jFormattedTextFieldM00.addPropertyChangeListener("value", this);
		jFormattedTextFieldM10.addPropertyChangeListener("value", this);
		jFormattedTextFieldM01.addPropertyChangeListener("value", this);
		jFormattedTextFieldM11.addPropertyChangeListener("value", this);
		jFormattedTextFieldM02.addPropertyChangeListener("value", this);
		jFormattedTextFieldM12.addPropertyChangeListener("value", this);

		if (pb.getIntParameter("Interpolation") == 0)  buttNN.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 1)  buttBL.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 2)  buttBC.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 3)  buttBC2.setSelected(true);
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
	 * This method initializes jJPanelM00	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelM00() {
		if (jPanelM00 == null) {
			jPanelM00 = new JPanel();
			BorderLayout bl_jPanelM00 = new BorderLayout();
			bl_jPanelM00.setHgap(5);
			jPanelM00.setLayout(bl_jPanelM00);
			//jPanelM00.setPreferredSize(new Dimension(250,18));					
			jLabelM00 = new JLabel("Scale X (M00):");
			jLabelM00.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelM00.setToolTipText("Scale factor (x-direction)");
			jFormattedTextFieldM00 = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldM00.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleEnterPressEventTextField();
				}
			});
			jFormattedTextFieldM00.addPropertyChangeListener("value", this);
			jFormattedTextFieldM00.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldM00.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldM00.setColumns(5);
			jPanelM00.add(jLabelM00, 			  BorderLayout.WEST);
			jPanelM00.add(jFormattedTextFieldM00, BorderLayout.CENTER);	

		}
		return jPanelM00;
	}
	
	/**
	 * This method initializes jJPanelM10	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelM10() {
		if (jPanelM10 == null) {
			jPanelM10 = new JPanel();
			BorderLayout bl_jPanelM10 = new BorderLayout();
			bl_jPanelM10.setHgap(5);
			jPanelM10.setLayout(bl_jPanelM10);
			//jPanelM10.setPreferredSize(new Dimension(250,18));					
			jLabelM10 = new JLabel("Shear Y (M10):");
			jLabelM10.setToolTipText("Shear factor (y-direction)");
			jLabelM10.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldM10 = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldM10.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			jFormattedTextFieldM10.addPropertyChangeListener("value", this);
			jFormattedTextFieldM10.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldM10.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldM10.setColumns(5);
			jPanelM10.add(jLabelM10, 			  BorderLayout.WEST);
			jPanelM10.add(jFormattedTextFieldM10, BorderLayout.CENTER);
			jFormattedTextFieldM10.setEnabled(true);	
		}
		return jPanelM10;
	}
	/**
	 * This method initializes jJPanelM01	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelM01() {
		if (jPanelM01 == null) {
			jPanelM01 = new JPanel();
			BorderLayout bl_jPanelM01 = new BorderLayout();
			bl_jPanelM01.setHgap(5);
			jPanelM01.setLayout(bl_jPanelM01);
			//jPanelM01.setPreferredSize(new Dimension(250,18));					
			jLabelM01 = new JLabel("Shear X (M01):");
			jLabelM01.setToolTipText("Shear factor (x-direction)");
			jLabelM01.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldM01 = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldM01.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleEnterPressEventTextField();
				}
			});
			jFormattedTextFieldM01.addPropertyChangeListener("value", this);
			jFormattedTextFieldM01.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldM01.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldM01.setColumns(5);
			jPanelM01.add(jLabelM01, 			  BorderLayout.WEST);
			jPanelM01.add(jFormattedTextFieldM01, BorderLayout.CENTER);	
			jFormattedTextFieldM01.setEnabled(true);
		}
		return jPanelM01;
	}
	/**
	 * This method initializes jJPanelM11	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelM11() {
		if (jPanelM11 == null) {
			jPanelM11 = new JPanel();
			BorderLayout bl_jPanelM11 = new BorderLayout();
			bl_jPanelM11.setHgap(5);
			jPanelM11.setLayout(bl_jPanelM11);
			//jPanelM11.setPreferredSize(new Dimension(250,18));					
			jLabelM11 = new JLabel("Scale Y (M11):");
			jLabelM11.setToolTipText("Scale factor (0...1) (y-direction)");
			jLabelM11.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldM11 = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldM11.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleEnterPressEventTextField();
				}
			});
			jFormattedTextFieldM11.addPropertyChangeListener("value", this);
			jFormattedTextFieldM11.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldM11.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldM11.setColumns(5);
			jPanelM11.add(jLabelM11, 			  BorderLayout.WEST);
			jPanelM11.add(jFormattedTextFieldM11, BorderLayout.CENTER);	
			jFormattedTextFieldM11.setEnabled(true);
		}
		return jPanelM11;
	}
	/**
	 * This method initializes jJPanelM02	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelM02() {
		if (jPanelM02 == null) {
			jPanelM02 = new JPanel();
			BorderLayout bl_jPanelM02 = new BorderLayout();
			bl_jPanelM02.setHgap(5);
			jPanelM02.setLayout(bl_jPanelM02);
			//jPanelM02.setPreferredSize(new Dimension(250,18));					
			jLabelM02 = new JLabel("Translate X (M02):");
			jLabelM02.setToolTipText("Translate the destination (x-direction in px)");
			jLabelM02.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldM02 = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldM02.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleEnterPressEventTextField();
				}
			});
			jFormattedTextFieldM02.addPropertyChangeListener("value", this);
			jFormattedTextFieldM02.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldM02.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldM02.setColumns(5);
			jPanelM02.add(jLabelM02, 			  BorderLayout.WEST);
			jPanelM02.add(jFormattedTextFieldM02, BorderLayout.CENTER);	
			jFormattedTextFieldM02.setEnabled(true);
		}
		return jPanelM02;
	}
	/**
	 * This method initializes jJPanelM12	
	 * 	
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelM12() {
		if (jPanelM12 == null) {
			jPanelM12 = new JPanel();
			BorderLayout bl_jPanelM12 = new BorderLayout();
			bl_jPanelM12.setHgap(5);
			jPanelM12.setLayout(bl_jPanelM12);
			//jPanelM12.setPreferredSize(new Dimension(250,18));					
			jLabelM12 = new JLabel("Translate Y (M12):");
			jLabelM12.setToolTipText("Translate the destination (y-direction in px)");
			jLabelM12.setHorizontalAlignment(SwingConstants.TRAILING);
			jFormattedTextFieldM12 = new JFormattedTextField(NumberFormat.getNumberInstance());
			jFormattedTextFieldM12.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleEnterPressEventTextField();
				}
			});
			jFormattedTextFieldM12.addPropertyChangeListener("value", this);
			jFormattedTextFieldM12.setInputVerifier(new FloatNumberVerifier());
			//jFormattedTextFieldM12.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
			jFormattedTextFieldM12.setColumns(5);
			jPanelM12.add(jLabelM12, 			  BorderLayout.WEST);
			jPanelM12.add(jFormattedTextFieldM12, BorderLayout.CENTER);	
			jFormattedTextFieldM12.setEnabled(true);
		}
		return jPanelM12;
	}
	
	protected void handleEnterPressEventTextField() {
		this.updateParameterBlock();
		
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
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
	public void propertyChange(PropertyChangeEvent e) {
		if (jFormattedTextFieldM00 == e.getSource()) {
		}
		if (jFormattedTextFieldM10  == e.getSource()) {
		}
		if (jFormattedTextFieldM01  == e.getSource()) {
		}
		if (jFormattedTextFieldM11  == e.getSource()) {
		}
		if (jFormattedTextFieldM02  == e.getSource()) {
		}
		if (jFormattedTextFieldM12  == e.getSource()) {
		}
		this.updateParameterBlock();

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}

	private JPanel getInterpolation_panel() {
		if (jPanelIntP == null) {
			
			buttNN  = new JRadioButton();
			buttNN .setText("Nearest Neighbor");
			buttNN .setToolTipText("uses Nearest Neighbor resampling for rotation");
			buttNN .addActionListener(this);
			buttNN.setActionCommand("parameter");

			buttBL  = new JRadioButton();
			buttBL .setText("Bilinear");
			buttBL .setToolTipText("uses Bilinear interpolation for rotation");
			buttBL .addActionListener(this);
			buttBL.setActionCommand("parameter");

			buttBC  = new JRadioButton();
			buttBC .setText("Bicubic");
			buttBC .setToolTipText("uses Bicubic interpoolation for rotation");
			buttBC .addActionListener(this);
			buttBC.setActionCommand("parameter");

			buttBC2  = new JRadioButton();
			buttBC2 .setText("Bicubic2");
			buttBC2 .setToolTipText("uses Bicubic2 interpolation for rotation");
			buttBC2 .addActionListener(this);
			buttBC2.setActionCommand("parameter");


			jPanelIntP = new JPanel();
			jPanelIntP.setLayout(new BoxLayout(jPanelIntP, BoxLayout.Y_AXIS));
			//jPanelIntP.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelIntP.setBorder(new TitledBorder(null, "Interpolation", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelIntP.add(buttNN);
			jPanelIntP.add(buttBL);
			jPanelIntP.add(buttBC);
			jPanelIntP.add(buttBC2);
		
			this.setButtonGroupIntP();
		}
		return jPanelIntP;
	}
	private JPanel getPanel_Param1() {
		if (panel == null) {
			panel = new JPanel();
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{0, 0};
			gbl_panel.rowHeights = new int[]{0, 0, 0};
			gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
		
			GridBagConstraints gbc_jPanelM00 = new GridBagConstraints();
			gbc_jPanelM00.anchor = GridBagConstraints.EAST;
			gbc_jPanelM00.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelM00.gridx = 0;
			gbc_jPanelM00.gridy = 0;
			panel.add(getJPanelM00(), gbc_jPanelM00);
		
			GridBagConstraints gbc_jPanelM10 = new GridBagConstraints();
			gbc_jPanelM10.anchor = GridBagConstraints.EAST;
			gbc_jPanelM10.gridx = 0;
			gbc_jPanelM10.gridy = 1;
			panel.add(getJPanelM10(), gbc_jPanelM10);
		}
		return panel;
	}
	private JPanel getPanel_Param2() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			GridBagLayout gbl_panel_1 = new GridBagLayout();
			gbl_panel_1.columnWidths = new int[]{0, 0};
			gbl_panel_1.rowHeights = new int[]{0, 0, 0};
			gbl_panel_1.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_panel_1.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			panel_1.setLayout(gbl_panel_1);
		
			GridBagConstraints gbc_jPanelM01 = new GridBagConstraints();
			gbc_jPanelM01.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelM01.gridx = 0;
			gbc_jPanelM01.gridy = 0;
			panel_1.add(getJPanelM01(), gbc_jPanelM01);
		
			GridBagConstraints gbc_jPanelM11 = new GridBagConstraints();
			gbc_jPanelM11.anchor = GridBagConstraints.EAST;
			gbc_jPanelM11.gridx = 0;
			gbc_jPanelM11.gridy = 1;
			panel_1.add(getJPanelM11(), gbc_jPanelM11);
		}
		return panel_1;
	}
	private JPanel getPanel_Param3() {
		if (panel_2 == null) {
			panel_2 = new JPanel();
			GridBagLayout gbl_panel_2 = new GridBagLayout();
			gbl_panel_2.columnWidths = new int[]{0, 0};
			gbl_panel_2.rowHeights = new int[]{0, 0, 0};
			gbl_panel_2.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_panel_2.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			panel_2.setLayout(gbl_panel_2);
		
			GridBagConstraints gbc_jPanelM02 = new GridBagConstraints();
			gbc_jPanelM02.anchor = GridBagConstraints.EAST;
			gbc_jPanelM02.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelM02.gridx = 0;
			gbc_jPanelM02.gridy = 0;
			panel_2.add(getJPanelM02(), gbc_jPanelM02);
			
			GridBagConstraints gbc_jPanelM12 = new GridBagConstraints();
			gbc_jPanelM12.anchor = GridBagConstraints.EAST;
			gbc_jPanelM12.gridx = 0;
			gbc_jPanelM12.gridy = 1;
			panel_2.add(getJPanelM12(), gbc_jPanelM12);
		}
		return panel_2;
	}
	private JPanel getPanel_Parameters() {
		if (panel_Parameters == null) {
			panel_Parameters = new JPanel();
			GridBagLayout gbl_param = new GridBagLayout();
			gbl_param.columnWidths = new int[]{0, 0, 0, 0};
			gbl_param.rowHeights = new int[]{0, 0};
			gbl_param.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_param.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			panel_Parameters.setLayout(gbl_param);
			
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.insets = new Insets(0, 0, 0, 5);
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 0;
			panel_Parameters.add(getPanel_Param1(), gbc_panel);
		
			GridBagConstraints gbc_panel_1 = new GridBagConstraints();
			gbc_panel_1.fill = GridBagConstraints.BOTH;
			gbc_panel_1.insets = new Insets(0, 0, 0, 5);
			gbc_panel_1.gridx = 1;
			gbc_panel_1.gridy = 0;
			panel_Parameters.add(getPanel_Param2(), gbc_panel_1);
		
			GridBagConstraints gbc_panel_2 = new GridBagConstraints();
			gbc_panel_2.fill = GridBagConstraints.BOTH;
			gbc_panel_2.gridx = 2;
			gbc_panel_2.gridy = 0;
			panel_Parameters.add(getPanel_Param3(), gbc_panel_2);
		}
		return panel_Parameters;
	}
}//END
