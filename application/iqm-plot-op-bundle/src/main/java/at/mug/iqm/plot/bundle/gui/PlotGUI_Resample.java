package at.mug.iqm.plot.bundle.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Resample.java
 * 
 * $Id: OperatorGUI_Resample.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/gui/OperatorGUI_Resample.java $
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpResampleDescriptor;

/**
 * @author Ahammer
 * @since  2018-11
 * @update 
 */
public class PlotGUI_Resample extends AbstractPlotOperatorGUI implements
		ActionListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5161696780502561840L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(PlotGUI_Resample.class);

	private ParameterBlockIQM pb = null;

	private JRadioButton buttDown                = null;
	private JRadioButton buttUp 	             = null;
	private JPanel       jPanelResampleOption    = null;
	private ButtonGroup  buttGroupResampleOption = null;
	
	private JPanel   jPanelFactor    = null;
	private JLabel   jLabelFactor    = null;
	private JSpinner jSpinnerFactor  = null;
	private JPanel   jPanelSetting     = null;
	

	private JRadioButton buttNone      = null; // simple resampling
	private JRadioButton buttBL 	   = null; // bilinear
	private JRadioButton buttBC 	   = null; // bicubic
	private JRadioButton buttBC2       = null; // bicubic2
	private JPanel       jPanelIntP    = null;
	private ButtonGroup  buttGroupIntP = null;



	/**
	 * constructor
	 */
	public PlotGUI_Resample() {
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpResampleDescriptor().getName());

		this.initialize();

		this.setTitle("Resample");

		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(createJPanelResampleOption(), createGBCButtonGroupResampleOption());
		this.getOpGUIContent().add(createJPanelSetting(),        createGridBagConstraintsSetting());
		this.getOpGUIContent().add(createJPanelIntP(),           createGBCButtonGroupIntP());

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 */
	@Override
	public void updateParameterBlock() {

		if (buttDown.isSelected()) pb.setParameter("ResampleOption", PlotOpResampleDescriptor.DOWNSAMPLE);
		if (buttUp.isSelected())   pb.setParameter("ResampleOption", PlotOpResampleDescriptor.UPSAMPLE);
		
		pb.setParameter("ResampleFactor", ((Number) jSpinnerFactor.getValue()).intValue());
		
		if (buttNone.isSelected()) pb.setParameter("Interpolation", PlotOpResampleDescriptor.INTERPOLATION_NONE);
		if (buttBL.isSelected())   pb.setParameter("Interpolation", PlotOpResampleDescriptor.INTERPOLATION_BILINEAR);
		if (buttBC.isSelected())   pb.setParameter("Interpolation", PlotOpResampleDescriptor.INTERPOLATION_BICUBIC);
		if (buttBC2.isSelected())  pb.setParameter("Interpolation", PlotOpResampleDescriptor.INTERPOLATION_BICUBIC2);	
	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();
		
		if (pb.getIntParameter("ResampleOption") == PlotOpResampleDescriptor.DOWNSAMPLE) buttDown.setSelected(true);
		if (pb.getIntParameter("ResampleOption") == PlotOpResampleDescriptor.UPSAMPLE)   buttUp.setSelected(true);
		
		jSpinnerFactor.removeChangeListener(this);	
		jSpinnerFactor.setValue(pb.getIntParameter("ResampleFactor"));
		jSpinnerFactor.addChangeListener(this);
		
		if (pb.getIntParameter("Interpolation") == PlotOpResampleDescriptor.INTERPOLATION_NONE)     buttNone.setSelected(true);
		if (pb.getIntParameter("Interpolation") == PlotOpResampleDescriptor.INTERPOLATION_BILINEAR) buttBL.setSelected(true);
		if (pb.getIntParameter("Interpolation") == PlotOpResampleDescriptor.INTERPOLATION_BICUBIC)  buttBC.setSelected(true);
		if (pb.getIntParameter("Interpolation") == PlotOpResampleDescriptor.INTERPOLATION_BICUBIC2) buttBC2.setSelected(true);
	}

	/**
	 * This method updates the GUI if needed This method overrides IqmOpJFrame
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		this.updateParameterBlock();
		this.setParameterValuesToGUI();
	}

	private GridBagConstraints createGBCButtonGroupResampleOption() {
		GridBagConstraints gBCButtonResampleOptionGroup = new GridBagConstraints();
		gBCButtonResampleOptionGroup.gridx = 0;
		gBCButtonResampleOptionGroup.gridy = 0;
		//gBCButtonResampleOptionGroup.gridwidth = 3;//?
		gBCButtonResampleOptionGroup.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		gBCButtonResampleOptionGroup.fill = GridBagConstraints.BOTH;
		return gBCButtonResampleOptionGroup;
	}
	
	private GridBagConstraints createGridBagConstraintsSetting() {
		GridBagConstraints gBCSetting = new GridBagConstraints();
		//gBCSetting.anchor = GridBagConstraints.WEST;
		gBCSetting.gridx = 0;
		gBCSetting.gridy = 1;
		//gBCSetting.gridwidth = 2;// ?
		gBCSetting.insets = new Insets(5, 0, 0, 0); // top  left  bottom  right
		gBCSetting.fill = GridBagConstraints.BOTH;
		return gBCSetting;
	}

	private GridBagConstraints createGBCButtonGroupIntP() {
		GridBagConstraints gBCButtonIntPGroup = new GridBagConstraints();
		gBCButtonIntPGroup.gridx = 0;
		gBCButtonIntPGroup.gridy = 2;
		gBCButtonIntPGroup.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		gBCButtonIntPGroup.fill = GridBagConstraints.BOTH;
		//gBCButtonIntPGroup.anchor = GridBagConstraints.NORTH;
		return gBCButtonIntPGroup;
	}

	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: Downscaling
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDown() {
		buttDown = new JRadioButton();
		buttDown.setText("Down-sampling");
		buttDown.setToolTipText("Downsampling of signal");
		buttDown.addActionListener(this);
		buttDown.setActionCommand("parameter");
		return buttDown;
	}

	/**
	 * This method initializes the Option: Upscaling
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonUp() {
		buttUp = new JRadioButton();
		buttUp.setText("Up-sampling");
		buttUp.setToolTipText("Upsampling of signal");
		buttUp.addActionListener(this);
		buttUp.setActionCommand("parameter");
		return buttUp;
	}


	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelResampleOption() {
		jPanelResampleOption = new JPanel();
		jPanelResampleOption.setLayout(new BoxLayout(jPanelResampleOption, BoxLayout.Y_AXIS));
		//jPanelResampleOption.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelResampleOption.setBorder(new TitledBorder(null, "Option", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelResampleOption.add(getJRadioButtonDown());
		jPanelResampleOption.add(getJRadioButtonUp());
		//jPanelResampleOption.addSeparator();
		this.setButtonGroupResampleOption(); // Grouping of JRadioButtons
		return jPanelResampleOption;
	}

	private void setButtonGroupResampleOption() {
		buttGroupResampleOption = new ButtonGroup();
		buttGroupResampleOption.add(buttDown);
		buttGroupResampleOption.add(buttUp);
	}

	// -------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelFactor
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelFactor() {
		if (jPanelFactor == null) {
			jPanelFactor = new JPanel();
			jPanelFactor.setLayout(new BorderLayout());
			jLabelFactor = new JLabel("Factor: ");
			jLabelFactor.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(2, 2, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerFactor = new JSpinner(sModel);
			//jSpinnerFactor.setPreferredSize(new Dimension(60, 20));
			jSpinnerFactor.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerFactor.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelFactor.add(jLabelFactor, BorderLayout.WEST);
			jPanelFactor.add(jSpinnerFactor, BorderLayout.CENTER);
		}
		return jPanelFactor;
	}
	
	/**
	 * This method initializes jJPanelSetting
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelSetting() {
		if (jPanelSetting == null) {
			jPanelSetting = new JPanel();
			//jPanelSetting.setLayout(new BoxLayout(jPanelSetting, BoxLayout.X_AXIS));
			jPanelSetting.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jPanelSetting.setBorder(new TitledBorder(null, "Setting", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelSetting.add(this.createJPanelFactor());
		}
		return jPanelSetting;
	}
	
	// -------------------------------------------------------------------------------------------
	
	/**
	 * This method initializes the Option: None
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtNone() {
		buttNone = new JRadioButton();
		buttNone.setText("None");
		buttNone.setToolTipText("uses simple resampling");
		buttNone.addActionListener(this);
		buttNone.setActionCommand("parameter");
		return buttNone;
	}

	/**
	 * This method initializes the Option: Bilinear interpolation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtBL() {
		buttBL = new JRadioButton();
		buttBL.setText("Bilinear");
		buttBL.setToolTipText("uses Bilinear interpolation for resizing");
		buttBL.addActionListener(this);
		buttBL.setActionCommand("parameter");
		return buttBL;
	}

	/**
	 * This method initializes the Option: Bicubic interpolation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtBC() {
		buttBC = new JRadioButton();
		buttBC.setText("Bicubic");
		buttBC.setToolTipText("uses Bicubic interpoolation for resizing");
		buttBC.addActionListener(this);
		buttBC.setActionCommand("parameter");
		buttBC.setEnabled(false);
		return buttBC;
	}

	/**
	 * This method initializes the Option: Bicubic2 interpolation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtBC2() {
		buttBC2 = new JRadioButton();
		buttBC2.setText("Bicubic2");
		buttBC2.setToolTipText("uses Bicubic2 interpolation for resizing");
		buttBC2.addActionListener(this);
		buttBC2.setActionCommand("parameter");
		buttBC2.setEnabled(false);
		return buttBC2;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelIntP() {
		jPanelIntP = new JPanel();
		jPanelIntP.setLayout(new BoxLayout(jPanelIntP, BoxLayout.Y_AXIS));
		//jPanelIntP.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelIntP.setBorder(new TitledBorder(null, "Interpolation", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelIntP.add(getJRadioButtonButtNone());
		jPanelIntP.add(getJRadioButtonButtBL());
		jPanelIntP.add(getJRadioButtonButtBC());
		jPanelIntP.add(getJRadioButtonButtBC2());
		// jPanelIntP.addSeparator();
		this.setButtonGroupIntP(); // Grouping of JRadioButtons
		return jPanelIntP;
	}

	private void setButtonGroupIntP() {
		buttGroupIntP = new ButtonGroup();
		buttGroupIntP.add(buttNone);
		buttGroupIntP.add(buttBL);
		buttGroupIntP.add(buttBC);
		buttGroupIntP.add(buttBC2);
	}

	// ----------------------------------------------------------------------------------------------------------

	

	// ----------------------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("parameter".equals(e.getActionCommand())) {

			if (buttDown == e.getSource()) {
				
				
			}
			if (buttUp == e.getSource()) {
				
				
			}
			
			this.update();
			//this.updateParameterBlock();
			//this.setParameterValuesToGUI();
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
		logger.debug("State has changed.");
	
	
		this.updateParameterBlock();
		this.setParameterValuesToGUI();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

}// END
