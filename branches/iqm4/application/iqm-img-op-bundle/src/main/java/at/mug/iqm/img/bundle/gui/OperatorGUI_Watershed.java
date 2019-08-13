package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Watershed.java
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
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpWatershedDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2010 05
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_Watershed extends AbstractImageOperatorGUI implements
		ActionListener, PropertyChangeListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7499171872308073521L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_Watershed.class);

	private ParameterBlockIQM pb = null;

	private JCheckBox jCheckBoxInvert2 = null;

	private JRadioButton buttNone         = null;
	private JRadioButton buttGauss        = null;
	private JPanel       jPanelPreProc    = null;
	private ButtonGroup  buttGroupPreProc = null;

	private JPanel   jPanelKernel   = null;
	private JLabel   jLabelKernel   = null;
	private JSpinner jSpinnerKernel = null;

	private JRadioButton buttCon4              = null;
	private JRadioButton buttCon8              = null;
	private JPanel       jPanelConnectivity    = null;
	private ButtonGroup  buttGroupConnectivity = null;

	private JPanel   jPanelThresMin   = null;
	private JLabel   jLabelThresMin   = null;
	private JSpinner jSpinnerThresMin = null;
	private JPanel   jPanelThresMax   = null;
	private JLabel   jLabelThresMax   = null;
	private JSpinner jSpinnerThresMax = null;
	private JPanel   jPanelThreshold  = null;
	

	private JRadioButton buttDam         = null;
	private JRadioButton buttOverDam     = null;
	private JRadioButton buttColBas      = null;
	private JRadioButton buttComposite   = null;
	private ButtonGroup  buttGroupOutput = null;
	private JPanel       preProcPanel;
	private JPanel       blurPanel;
	private JLabel       lblOutput;
	private JPanel       jPanelOutput    = null;

	/**
	 * constructor
	 */
	public OperatorGUI_Watershed() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpWatershedDescriptor().getName());
		this.initialize();
		this.setTitle("Watershed Segmentation");	
		this.getOpGUIContent().setLayout(new GridBagLayout());
	
		this.getOpGUIContent().add(getPreProcPanel(),        getGridBagConstraints_PreProcessing());
		this.getOpGUIContent().add(getJPanelConnectivity(),  getGridBagConstraints_Connectivity());
		this.getOpGUIContent().add(getJPanelThreshold(),     getGridBagConstraints_Threshold());
		this.getOpGUIContent().add(getJPanelOutput(),        getGridBagConstraints_Output());

		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_PreProcessing(){
		GridBagConstraints gbc_PreProc = new GridBagConstraints();
		gbc_PreProc.gridx = 0;
		gbc_PreProc.gridy = 0;
		gbc_PreProc.gridwidth = 2;
		gbc_PreProc.insets = new Insets(10, 0, 0, 0); // top left  bottom  right
		gbc_PreProc.fill = GridBagConstraints.BOTH;
		return gbc_PreProc;
	}
	
	private GridBagConstraints getGridBagConstraints_Connectivity() {
		GridBagConstraints gbc_Connectivity = new GridBagConstraints();
		gbc_Connectivity.gridx = 0;
		gbc_Connectivity.gridy = 1;
		gbc_Connectivity.gridwidth = 1;// ?
		gbc_Connectivity.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		gbc_Connectivity.fill = GridBagConstraints.BOTH;
		return gbc_Connectivity;
	}

	private GridBagConstraints getGridBagConstraints_Threshold() {
		GridBagConstraints gbc_Thresold = new GridBagConstraints();
		gbc_Thresold.gridx = 0;
		gbc_Thresold.gridy = 2;
		gbc_Thresold.gridwidth = 1;// ?
		gbc_Thresold.insets = new Insets(5, 0, 0, 0); // top  left  bottom  right
		gbc_Thresold.fill = GridBagConstraints.BOTH;
		return gbc_Thresold;
	}
	
	private GridBagConstraints getGridBagConstraints_Output(){
		GridBagConstraints gbc_OutPanel = new GridBagConstraints();
		gbc_OutPanel.gridx = 0;
		gbc_OutPanel.gridy = 3;
		gbc_OutPanel.gridwidth = 1;
		gbc_OutPanel.insets = new Insets(5, 0, 5, 0);
		gbc_OutPanel.fill = GridBagConstraints.BOTH;
		return gbc_OutPanel;
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (jCheckBoxInvert2.isSelected())   pb.setParameter("Invert", 1);
		if (!jCheckBoxInvert2.isSelected())  pb.setParameter("Invert", 0);
		if (buttNone.isSelected())           pb.setParameter("PreProc", 0);
		if (buttGauss.isSelected())          pb.setParameter("PreProc", 1);
		pb.setParameter("Kernel",            ((Number) jSpinnerKernel.getValue()).intValue());
		if (buttCon4.isSelected())           pb.setParameter("Connectivity", 0);
		if (buttCon8.isSelected())           pb.setParameter("Connectivity", 1);
		pb.setParameter("ThresMin",          ((Number) jSpinnerThresMin.getValue()).intValue());
		pb.setParameter("ThresMax",          ((Number) jSpinnerThresMax.getValue()).intValue());
		if (buttDam.isSelected())            pb.setParameter("Output", 0);
		if (buttOverDam.isSelected())        pb.setParameter("Output", 1);
		if (buttColBas.isSelected())         pb.setParameter("Output", 2);
		if (buttComposite.isSelected())      pb.setParameter("Output", 3);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Invert") == 0)  jCheckBoxInvert2.setSelected(false);
		if (pb.getIntParameter("Invert") == 1)  jCheckBoxInvert2.setSelected(true);

		if (pb.getIntParameter("PreProc") == 0) buttNone.setSelected(true);
		if (pb.getIntParameter("PreProc") == 1) buttGauss.setSelected(true);

		jSpinnerKernel.removeChangeListener(this);
		jSpinnerThresMin.removeChangeListener(this);
		jSpinnerThresMax.removeChangeListener(this);
		jSpinnerKernel.setValue(pb.getIntParameter("Kernel"));

		if (pb.getIntParameter("Connectivity") == 0) buttCon4.setSelected(true);
		if (pb.getIntParameter("Connectivity") == 1) buttCon8.setSelected(true);

		jSpinnerThresMin.setValue(pb.getIntParameter("ThresMin"));
		jSpinnerThresMax.setValue(pb.getIntParameter("ThresMax"));
		jSpinnerKernel.addChangeListener(this);
		jSpinnerThresMin.addChangeListener(this);
		jSpinnerThresMax.addChangeListener(this);

		if (pb.getIntParameter("Output") == 0) buttDam.setSelected(true);
		if (pb.getIntParameter("Output") == 1) buttOverDam.setSelected(true);
		if (pb.getIntParameter("Output") == 2) buttColBas.setSelected(true);
		if (pb.getIntParameter("Output") == 3) buttComposite.setSelected(true);

		if (buttGauss.isSelected()) {
			jSpinnerKernel.setEnabled(true);
		} else {
			jSpinnerKernel.setEnabled(false);
		}
	}

	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	// -----------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxInvert2
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxInvert() {
		if (jCheckBoxInvert2 == null) {
			jCheckBoxInvert2 = new JCheckBox();
			jCheckBoxInvert2.setHorizontalAlignment(SwingConstants.CENTER);
			jCheckBoxInvert2.setText("Invert Input Image");
			jCheckBoxInvert2 .setToolTipText("inverts input image if necessary (the code searches for dark objects in bright background)");
			jCheckBoxInvert2.addActionListener(this);
			jCheckBoxInvert2.setActionCommand("parameter");
		}
		return jCheckBoxInvert2;
	}

	// -----------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtNone() {
		if (buttNone == null) {
			buttNone = new JRadioButton();
			buttNone.setText("None");
			buttNone.setToolTipText("no preprocessing");
			buttNone.addActionListener(this);
			buttNone.setActionCommand("parameter");
		}
		return buttNone;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtGauss() {
		if (buttGauss == null) {
			buttGauss = new JRadioButton();
			buttGauss.setText("Gaussian Blur");
			buttGauss.setToolTipText("preprocess with gaussian blur");
			buttGauss.addActionListener(this);
			buttGauss.setActionCommand("parameter");
		}
		return buttGauss;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelPreProc() {
		jPanelPreProc = new JPanel();
		jPanelPreProc.setLayout(new BoxLayout(jPanelPreProc, BoxLayout.Y_AXIS));
		//jPanelPreProc.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		//jPanelPreProc.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelPreProc.add(getJRadioButtonButtNone());
		jPanelPreProc.add(getJRadioButtonButtGauss());
		this.setButtonGroupPreProc(); // Grouping of JRadioButtons
		return jPanelPreProc;
	}

	private void setButtonGroupPreProc() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupPreProc = new ButtonGroup();
		buttGroupPreProc.add(buttNone);
		buttGroupPreProc.add(buttGauss);
	}

	// -----------------------------------------------------------------------
	// class IntNumberVerifier extends InputVerifier { //damit muss Eingabe richtig sein
	// @Override
	// public boolean verify(JComponent input) {
	// JFormattedTextField ftf = (JFormattedTextField)input;
	// JFormattedTextField.AbstractFormatter formatter = ftf.getFormatter();
	// if (formatter != null) {
	// String text = ftf.getText();
	// try {
	// text = text.replace(",", ".");
	// Integer.valueOf(text);
	// //Float.valueOf(text);
	// return true;
	// } catch (NumberFormatException e) {
	// return false;
	// }
	// }
	// return true;
	// }
	// // public boolean shouldYieldFocus(JComponent input) {
	// // System.out.println("NumberVerifier  shouldYieldFocus");
	// //
	// // return verify(input);
	// // }
	// }

	/**
	 * This method initializes jJPanelKernel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernel() {
		if (jPanelKernel == null) {
			jPanelKernel = new JPanel();
			jPanelKernel.setLayout(new BorderLayout());
			jLabelKernel = new JLabel("Kernel: ");
			jLabelKernel.setToolTipText("kernel size");
			//jLabelKernel.setPreferredSize(new Dimension(65, 20));
			jLabelKernel.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 2); // init, min, max, step
			jSpinnerKernel = new JSpinner(sModel);
			//jSpinnerKernel.setPreferredSize(new Dimension(60, 20));
			jSpinnerKernel.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerKernel .getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf .getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")  ;
			jPanelKernel.add(jLabelKernel, BorderLayout.WEST);
			jPanelKernel.add(jSpinnerKernel, BorderLayout.CENTER);
		}
		return jPanelKernel;
	}
	
	

	private JPanel getPreProcPanel() {
		if (preProcPanel == null) {
			preProcPanel = new JPanel();
			preProcPanel.setBorder(new TitledBorder(null, "Pre-processing", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			preProcPanel.setLayout(new BorderLayout(0, 5));
			preProcPanel.add(getJCheckBoxInvert(), BorderLayout.NORTH);
			preProcPanel.add(getBluroptions(),     BorderLayout.CENTER);
		}
		return preProcPanel;
	}

	private JPanel getBluroptions() {
		if (blurPanel == null) {
			blurPanel = new JPanel();
			blurPanel.setBorder(null);
			blurPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			blurPanel.add(getJPanelPreProc());
			blurPanel.add(getJPanelKernel());

		}
		return blurPanel;
	}

	// -----------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonbuttCon4() {
		// if (buttCon4 == null) {
		buttCon4 = new JRadioButton();
		buttCon4.setText("4-connectivety");
		buttCon4.setToolTipText("4-connectivety");
		buttCon4.addActionListener(this);
		buttCon4.setActionCommand("parameter");
		// }
		return buttCon4;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonbuttCon8() {
		// if (buttCon8 == null) {
		buttCon8 = new JRadioButton();
		buttCon8.setText("8-connectivety");
		buttCon8.setToolTipText("8-connectivety");
		buttCon8.addActionListener(this);
		buttCon8.setActionCommand("parameter");
		buttCon8.setEnabled(true);
		// }
		return buttCon8;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelConnectivity() {
		// if (jPanelConnectivity== null) {
		jPanelConnectivity = new JPanel();
		jPanelConnectivity.setLayout(new BoxLayout(jPanelConnectivity, BoxLayout.Y_AXIS));
		//jPanelConnectivity.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelConnectivity.setBorder(new TitledBorder(null, "Connectivity", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		jPanelConnectivity.add(getJRadioButtonbuttCon4());
		jPanelConnectivity.add(getJRadioButtonbuttCon8());
		// jPanelConnectivity.addSeparator();
		this.setButtonGroupConnectivity(); // Grouping of JRadioButtons
		// }
		return jPanelConnectivity;
	}

	private void setButtonGroupConnectivity() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupConnectivity = new ButtonGroup();
		buttGroupConnectivity.add(buttCon4);
		buttGroupConnectivity.add(buttCon8);
	}

	// -----------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelThresMin
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelThresMin() {
		if (jPanelThresMin == null) {
			jPanelThresMin = new JPanel();
			jPanelThresMin.setLayout(new BorderLayout());
			jLabelThresMin = new JLabel("Min: ");
			jLabelThresMin.setToolTipText("threshold minimum");
			jLabelThresMin.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, 255, 1); // init, min,/ max, step
			jSpinnerThresMin = new JSpinner(sModel);
			//jSpinnerThresMin.setPreferredSize(new Dimension(60, 20));
			jSpinnerThresMin.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThresMin .getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf .getFormatter();
			ftf.setColumns(5);
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")  ;
			jPanelThresMin.add(jLabelThresMin, BorderLayout.WEST);
			jPanelThresMin.add(jSpinnerThresMin, BorderLayout.CENTER);
		}
		return jPanelThresMin;
	}

	/**
	 * This method initializes jJPanelThresMax
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelThresMax() {
		if (jPanelThresMax == null) {
			jPanelThresMax = new JPanel();
			jPanelThresMax.setLayout(new BorderLayout());
			jLabelThresMax = new JLabel("TMax: ");
			jLabelThresMax.setToolTipText("threshold maximum");
			jLabelThresMax.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(255, 0, 255, 1); // init,  min,  max,  step
			jSpinnerThresMax = new JSpinner(sModel);
			//jSpinnerThresMax.setPreferredSize(new Dimension(60, 20));
			jSpinnerThresMax.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThresMax .getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf .getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")  ;
			jPanelThresMax.add(jLabelThresMax, BorderLayout.WEST);
			jPanelThresMax.add(jSpinnerThresMax, BorderLayout.CENTER);
		}
		return jPanelThresMax;
	}
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelThreshold() {
		// if (jPanelThreshold== null) {
		jPanelThreshold = new JPanel();
		//jPanelThreshold.setLayout(new BoxLayout(jPanelThreshold, BoxLayout.Y_AXIS));
		jPanelThreshold.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelThreshold.setBorder(new TitledBorder(null, "Threshold", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		jPanelThreshold.add(getJPanelThresMin());
		jPanelThreshold.add(getJPanelThresMax());
		// }
		return jPanelThreshold;
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtDam() {
		if (buttDam == null) {
			buttDam = new JRadioButton();
			buttDam.setText("Dams");
			buttDam.setToolTipText("output: dams");
			buttDam.addActionListener(this);
			buttDam.setActionCommand("parameter");
		}
		return buttDam;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtOverDam() {
		if (buttOverDam == null) {
			buttOverDam = new JRadioButton();
			buttOverDam.setText("Overlaid Dams");
			buttOverDam.setToolTipText("output: overlaid dams");
			buttOverDam.addActionListener(this);
			buttOverDam.setActionCommand("parameter");
		}
		return buttOverDam;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtColBas() {
		if (buttColBas == null) {
			buttColBas = new JRadioButton();
			buttColBas.setText("Colorized Basins");
			buttColBas.setToolTipText("output: colorized basins");
			buttColBas.addActionListener(this);
			buttColBas.setActionCommand("parameter");
		}
		return buttColBas;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtComposite() {
		if (buttComposite == null) {
			buttComposite = new JRadioButton();
			buttComposite.setText("Composite");
			buttComposite.setToolTipText("output: composite");
			buttComposite.addActionListener(this);
			buttComposite.setActionCommand("parameter");
		}
		return buttComposite;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOutput() {

		jPanelOutput = new JPanel();
		jPanelOutput.setLayout(new BoxLayout(jPanelOutput, BoxLayout.Y_AXIS));
		//jPanelOutput.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelOutput.setBorder(new TitledBorder(null, "Output image", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelOutput.add(getJRadioButtonButtDam());
		jPanelOutput.add(getJRadioButtonButtOverDam());
		jPanelOutput.add(getJRadioButtonButtColBas());
		jPanelOutput.add(getJRadioButtonButtComposite());

		this.setButtonGroupOutput(); // Grouping of JRadioButtons
		return jPanelOutput;
	}

	private void setButtonGroupOutput() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupOutput = new ButtonGroup();
		buttGroupOutput.add(buttDam);
		buttGroupOutput.add(buttOverDam);
		buttGroupOutput.add(buttColBas);
		buttGroupOutput.add(buttComposite);
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {

			if (buttNone == e.getSource()) {
			}
			if (buttGauss == e.getSource()) {
			}

			if (buttGauss.isSelected()) {
				jSpinnerKernel.setEnabled(true);
			} else {
				jSpinnerKernel.setEnabled(false);
			}
			this.updateParameterBlock();
			// this.update(); //if necessary here or some lines above
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	// -----------------------------------------------------------------------------------------------

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		((Number) jSpinnerKernel.getValue()).intValue();
		((Number) jSpinnerThresMin.getValue()).intValue();
		((Number) jSpinnerThresMax.getValue()).intValue();

		this.updateParameterBlock();
		// this.update(); //if necessary here or some lines above

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}

	// -------------------------------------------------------------------------------------------------
	@Override
	public void stateChanged(ChangeEvent e) {

		int kernelSize = ((Number) jSpinnerKernel.getValue()).intValue();
		int thresMin = ((Number) jSpinnerThresMin.getValue()).intValue();
		int thresMax = ((Number) jSpinnerThresMax.getValue()).intValue();

		if (jSpinnerKernel == e.getSource()) {
			if (kernelSize % 2 == 0) {// even;
				jSpinnerKernel.removeChangeListener(this);
				jSpinnerKernel.setValue(kernelSize + 1);
				jSpinnerKernel.addChangeListener(this);

			}
			if (kernelSize % 2 != 0) {// odd;
			}

		}
		if (jSpinnerThresMin == e.getSource()) {
			if (thresMin >= thresMax) {
				jSpinnerThresMin.removeChangeListener(this);
				jSpinnerThresMin.setValue(thresMax - 1);
				jSpinnerThresMin.addChangeListener(this);

			}
		}
		if (jSpinnerThresMax == e.getSource()) {
			if (thresMax <= thresMin) {
				jSpinnerThresMax.removeChangeListener(this);
				jSpinnerThresMax.setValue(thresMin + 1);
				jSpinnerThresMax.addChangeListener(this);

			}
		}
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

}// END
