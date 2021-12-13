package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_ComplexLogDepth.java
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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpComplexLogDepthDescriptor;

/**
 * Logical depth (physical depth) is calculated according to:
 * <p>
 * 		Zenil et al. Image Characterization and Classification by Physical Complexity, Complexity 2011 Vol17 No3 26-42 
 * </p> 
 * 
 * @author Ahammer
 * @since   2014 01
 * @update 2014 01 added ZIP in tiff file compression option
 */
public class OperatorGUI_ComplexLogDepth extends AbstractImageOperatorGUI
		implements ActionListener, ChangeListener {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7853998533862206929L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_ComplexLogDepth.class);

	private ParameterBlockIQM pb = null;

	private JRadioButton buttPNG             = null;
	private JRadioButton buttPNGCrush        = null;
	private JRadioButton buttPNGAdvancedCOMP = null;
	private JRadioButton buttZIP             = null;
	private JRadioButton buttLZW             = null;
	private JRadioButton buttJpeg2000        = null;
	private JPanel       jPanelMethod        = null;
	private ButtonGroup  buttGroupMethod     = null;
	
	private JPanel   jPanelIterations   = null;
	private JLabel   jLabelIterations   = null;
	private JSpinner jSpinnerIterations = null;
	
	private JCheckBox jCheckBoxCorrSystemBias = null;
	
	private JPanel    jPanelOptions = null;
	/**
	 * constructor
	 */
	public OperatorGUI_ComplexLogDepth() {
		setResizable(false);
		logger.debug("Now initializing...");
		
		this.setOpName(new IqmOpComplexLogDepthDescriptor().getName());
		
		this.initialize();
		this.setTitle("Logical Depth");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.complex.logicaldepth.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());	
		
		this.getOpGUIContent().add(getJPanelMethod(),       getGridBagConstraintsMethod());
		this.getOpGUIContent().add(getJPanelIterations(),   getGridBagConstraintsIterations());
		this.getOpGUIContent().add(getJPanelOptions(),      getGridBagConstraintsOptions());

		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraintsMethod() {
		GridBagConstraints gridBagConstraintsMethod = new GridBagConstraints();
		gridBagConstraintsMethod.gridx = 0;
		gridBagConstraintsMethod.gridy = 0;
		gridBagConstraintsMethod.insets = new Insets(5, 0, 0, 0);  //top, left, bottom, right
		gridBagConstraintsMethod.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsMethod;
	}
	private GridBagConstraints getGridBagConstraintsIterations() {
		GridBagConstraints gridBagConstraintsIterations = new GridBagConstraints();
		gridBagConstraintsIterations.gridx = 0;
		gridBagConstraintsIterations.gridy = 1;
		gridBagConstraintsIterations.insets = new Insets(5, 0, 0, 0);  //top, left, bottom, right
		//gridBagConstraintsIterations.anchor = GridBagConstraints.WEST;
		gridBagConstraintsIterations.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsIterations;
	}
	private GridBagConstraints getGridBagConstraintsOptions() {
		GridBagConstraints gridBagConstraints_Options = new GridBagConstraints();
		gridBagConstraints_Options.gridx = 0;
		gridBagConstraints_Options.gridy = 2;
		//gridBagConstraintsLoadCorr.anchor = GridBagConstraints.WEST;
		gridBagConstraints_Options.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gridBagConstraints_Options.fill = GridBagConstraints.BOTH;

		return gridBagConstraints_Options;
	}
	

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		if (buttPNG.isSelected())             pb.setParameter("Method", IqmOpComplexLogDepthDescriptor.COMPRESSION_PNG);
		if (buttPNGCrush.isSelected())        pb.setParameter("Method", IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGCRUSH);
		if (buttPNGAdvancedCOMP.isSelected()) pb.setParameter("Method", IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGADVANCEDCOMP);
		if (buttZIP.isSelected())             pb.setParameter("Method", IqmOpComplexLogDepthDescriptor.COMPRESSION_ZIP);
		if (buttLZW.isSelected())             pb.setParameter("Method", IqmOpComplexLogDepthDescriptor.COMPRESSION_LZW);
		if (buttJpeg2000.isSelected())        pb.setParameter("Method", IqmOpComplexLogDepthDescriptor.COMPRESSION_JPEG2000);
		
		pb.setParameter("Iterations", ((Number) jSpinnerIterations.getValue()).intValue());
		
		if (jCheckBoxCorrSystemBias.isSelected())  pb.setParameter("CorrSystemBias", IqmOpComplexLogDepthDescriptor.LOADTIMECORRECTION_YES);
		if (!jCheckBoxCorrSystemBias.isSelected()) pb.setParameter("CorrSystemBias", IqmOpComplexLogDepthDescriptor.LOADTIMECORRECTION_NO);

	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();
		
		if (pb.getIntParameter("Method") == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNG)             buttPNG.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGCRUSH)        buttPNGCrush.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpComplexLogDepthDescriptor.COMPRESSION_PNGADVANCEDCOMP) buttPNGAdvancedCOMP.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpComplexLogDepthDescriptor.COMPRESSION_ZIP)             buttZIP.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpComplexLogDepthDescriptor.COMPRESSION_LZW)             buttLZW.setSelected(true);
		if (pb.getIntParameter("Method") == IqmOpComplexLogDepthDescriptor.COMPRESSION_JPEG2000)        buttJpeg2000.setSelected(true);
		
		jSpinnerIterations.removeChangeListener(this);
		jSpinnerIterations.setValue(pb.getIntParameter("Iterations"));
		jSpinnerIterations.addChangeListener(this);
		
		if (pb.getIntParameter("CorrSystemBias") == IqmOpComplexLogDepthDescriptor.LOADTIMECORRECTION_NO)  jCheckBoxCorrSystemBias.setSelected(false);
		if (pb.getIntParameter("CorrSystemBias") == IqmOpComplexLogDepthDescriptor.LOADTIMECORRECTION_YES) jCheckBoxCorrSystemBias.setSelected(true);
	}



	/**
	 * This method updates the GUI, if needed. This method overrides
	 * OperatorGUI.
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: PNG
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonPNG() {
		if (buttPNG == null) {
			buttPNG = new JRadioButton();
			buttPNG.setText("PNG");
			buttPNG.setToolTipText("PNG compression");
			buttPNG.addActionListener(this);
			buttPNG.setActionCommand("parameter");
		}
		return buttPNG;
	}

	/**
	 * This method initializes the Option: PNGCrush
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonPNGCrush() {
		if (buttPNGCrush == null) {
			buttPNGCrush = new JRadioButton();
			buttPNGCrush.setText("PNGCrush");
			buttPNGCrush.setToolTipText("Pngcrush compression");
			buttPNGCrush.addActionListener(this);
			buttPNGCrush.setActionCommand("parameter");
			buttPNGCrush.setEnabled(false);
			buttPNGCrush.setVisible(false);
		}
		return buttPNGCrush;
	}

	/**
	 * This method initializes the Option: PNGAdvancedCOMP
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonPNGAdvancedCOMP() {
		if (buttPNGAdvancedCOMP == null) {
			buttPNGAdvancedCOMP = new JRadioButton();
			buttPNGAdvancedCOMP.setText("PNGAdvancedCOMP");
			buttPNGAdvancedCOMP.setToolTipText("AdvancedCOMP compression");
			buttPNGAdvancedCOMP.addActionListener(this);
			buttPNGAdvancedCOMP.setActionCommand("parameter");
			buttPNGAdvancedCOMP.setEnabled(false);
			buttPNGAdvancedCOMP.setVisible(false);
		}
		return buttPNGAdvancedCOMP;
	}
	/**
	 * This method initializes the Option: ZIP
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonZIP() {
		if (buttZIP == null) {
			buttZIP = new JRadioButton();
			buttZIP.setText("ZIP");
			buttZIP.setToolTipText("ZIP in TIF file compression");
			buttZIP.addActionListener(this);
			buttZIP.setActionCommand("parameter");
			buttZIP.setEnabled(true);
		}
		return buttZIP;
	}
	/**
	 * This method initializes the Option: LZW
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonLZW() {
		if (buttLZW == null) {
			buttLZW = new JRadioButton();
			buttLZW.setText("LZW");
			buttLZW.setToolTipText("LZW in TIF file compression");
			buttLZW.addActionListener(this);
			buttLZW.setActionCommand("parameter");
			buttLZW.setVisible(false);
			buttLZW.setEnabled(false);
		}
		return buttLZW;
	}

	/**
	 * This method initializes the Option: Jpeg2000
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonJpeg2000() {
		if (buttJpeg2000 == null) {
			buttJpeg2000 = new JRadioButton();
			buttJpeg2000.setText("Jpeg2000");
			buttJpeg2000.setToolTipText("Jpeg2000 compression");
			buttJpeg2000.addActionListener(this);
			buttJpeg2000.setActionCommand("parameter");
			buttJpeg2000.setEnabled(false);
			buttJpeg2000.setVisible(false);
		}
		return buttJpeg2000;
	}

	/**
	 * This method initializes jJPanelMethod
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		// if (jPanelMethod == null) {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.X_AXIS));
		jPanelMethod.setBorder(new TitledBorder(null, "Method:", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		jPanelMethod.add(getJRadioButtonPNG());
		jPanelMethod.add(getJRadioButtonPNGCrush());
		jPanelMethod.add(getJRadioButtonPNGAdvancedCOMP());
		jPanelMethod.add(getJRadioButtonZIP());
		jPanelMethod.add(getJRadioButtonLZW());
		jPanelMethod.add(getJRadioButtonJpeg2000());

		// jPanelMethod.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		// }
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroupMethod == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttPNG);
		buttGroupMethod.add(buttPNGCrush);
		buttGroupMethod.add(buttPNGAdvancedCOMP);
		buttGroupMethod.add(buttZIP);
		buttGroupMethod.add(buttLZW);
		buttGroupMethod.add(buttJpeg2000);
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelIterations
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIterations() {
		if (jPanelIterations == null) {
			jPanelIterations = new JPanel();
			//jPanelIterations.setLayout(new BoxLayout(jPanelIterations, BoxLayout.X_AXIS));
			jPanelIterations.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));
			jPanelIterations.setBorder(new TitledBorder(null, "Iterations", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelIterations.setToolTipText("Number of iterations. Each iteration yields a vlaue and the Median of these values is the result");
			jLabelIterations = new JLabel("#: ");
			// jLabelIterations.setPreferredSize(new Dimension(70, 22));
			//jLabelIterations.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerIterations = new JSpinner(sModel);
			jSpinnerIterations.setToolTipText("Number of iterations. Each iteration yields a duratioin and the Median of these durations is the result");
			//jSpinnerIterations.setPreferredSize(new Dimension(60, 24));
			jSpinnerIterations.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerIterations.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")								
			jPanelIterations.add(jLabelIterations);
			jPanelIterations.add(jSpinnerIterations);
		}
		return jPanelIterations;
	}
	// ------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxLoadCorr
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxLoadCorr() {
		if (jCheckBoxCorrSystemBias == null) {
			jCheckBoxCorrSystemBias = new JCheckBox();
			jCheckBoxCorrSystemBias.setText("Correct system bias");
			jCheckBoxCorrSystemBias.setToolTipText("Correction by loading a TIF version of the image");
			jCheckBoxCorrSystemBias.addActionListener(this);
			jCheckBoxCorrSystemBias.setActionCommand("parameter");
			jCheckBoxCorrSystemBias.setSelected(true);
		}
		return jCheckBoxCorrSystemBias;
	}
	
	/**
	 * This method initializes jJPanelOptions
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOptions() {
		if (jPanelOptions == null) {
			jPanelOptions = new JPanel();
			jPanelOptions.setLayout(new BoxLayout(jPanelOptions, BoxLayout.X_AXIS));
			jPanelOptions.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));				
			jPanelOptions.add(getJCheckBoxLoadCorr());
		}
		return jPanelOptions;
	}	
	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
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

		@SuppressWarnings("unused")
		int iterations = ((Number) jSpinnerIterations.getValue()).intValue();
	
		jSpinnerIterations.removeChangeListener(this);
        //change spinner value
		jSpinnerIterations.addChangeListener(this);
	

		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
}// END
