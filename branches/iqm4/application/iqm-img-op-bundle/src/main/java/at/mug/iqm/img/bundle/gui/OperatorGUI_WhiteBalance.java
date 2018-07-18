package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_WhiteBalance.java
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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.renderable.ParameterBlock;
import java.text.DecimalFormat;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;
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
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpWhiteBalanceDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 12
 */
public class OperatorGUI_WhiteBalance extends AbstractImageOperatorGUI
		implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2733479495382144465L;

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(OperatorGUI_WhiteBalance.class);

	private ParameterBlockIQM pb = null; // @jve:decl-index=0:

	private JRadioButton buttROI         = null;
	private JRadioButton buttManual      = null;
	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;

	private JPanel   jPanelInR   = null;
	private JLabel   jLabelInR   = null;
	private JSpinner jSpinnerInR = null;
	private JPanel   jPanelInG   = null;
	private JLabel   jLabelInG   = null;
	private JSpinner jSpinnerInG = null;
	private JPanel   jPanelInB   = null;
	private JLabel   jLabelInB   = null;
	private JSpinner jSpinnerInB = null;

	private JPanel   jPanelOutR   = null;
	private JLabel   jLabelOutR   = null;
	private JSpinner jSpinnerOutR = null;
	private JPanel   jPanelOutG   = null;
	private JLabel   jLabelOutG   = null;
	private JSpinner jSpinnerOutG = null;
	private JPanel   jPanelOutB   = null;
	private JLabel   jLabelOutB   = null;
	private JSpinner jSpinnerOutB = null;

	private JCheckBox jCheckBoxLinkIn = null;
	private JCheckBox jCheckBoxLinkOut = null;
	private JPanel    inputPanel;
	private JPanel    outputPanel;

	/**
	 * constructor
	 */
	public OperatorGUI_WhiteBalance() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpWhiteBalanceDescriptor().getName());

		this.initialize();
		this.setTitle("White Balance");

		this.getOpGUIContent().setLayout(new GridBagLayout());
		this.getOpGUIContent().add(getJPanelMethod(), getGridBagConstraints_ButtonMethodGroup());
		this.getOpGUIContent().add(getInputPanel(),   getGridBagConstraints_InputPanel());		
		this.getOpGUIContent().add(getOutputPanel(),  getGridBagConstraints_Panel());
		this.pack();
	}
	
	
	private GridBagConstraints getGridBagConstraints_ButtonMethodGroup() {
		GridBagConstraints gbc_MethodGroup = new GridBagConstraints();
		gbc_MethodGroup.gridx = 0;
		gbc_MethodGroup.gridy = 0;
		gbc_MethodGroup.gridwidth = 2;
		gbc_MethodGroup.insets = new Insets(10, 0, 0, 0); // top left bottom right
		gbc_MethodGroup.fill = GridBagConstraints.HORIZONTAL;
		return gbc_MethodGroup;
	}
	
	private GridBagConstraints getGridBagConstraints_InputPanel(){
		GridBagConstraints gbc_inputPanel = new GridBagConstraints();
		gbc_inputPanel.insets = new Insets(10, 0, 10, 0); // top left bottom right
		gbc_inputPanel.gridx = 0;
		gbc_inputPanel.gridy = 1;
		return gbc_inputPanel;
	}
	
	private GridBagConstraints getGridBagConstraints_Panel(){
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(10, 0, 10, 0); // top left bottom right
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		return gbc_panel;
    }


	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		if (buttROI.isSelected())
			pb.setParameter("Method", 0);
		if (buttManual.isSelected())
			pb.setParameter("Method", 1);

		pb.setParameter("InR", ((Number) jSpinnerInR.getValue()).intValue());
		pb.setParameter("InG", ((Number) jSpinnerInG.getValue()).intValue());
		pb.setParameter("InB", ((Number) jSpinnerInB.getValue()).intValue());

		pb.setParameter("OutR", ((Number) jSpinnerOutR.getValue()).intValue());
		pb.setParameter("OutG", ((Number) jSpinnerOutG.getValue()).intValue());
		pb.setParameter("OutB", ((Number) jSpinnerOutB.getValue()).intValue());

		if (jCheckBoxLinkIn.isSelected())  pb.setParameter("LinkIn", 1);
		if (!jCheckBoxLinkIn.isSelected()) pb.setParameter("LinkIn", 0);
		if (jCheckBoxLinkOut.isSelected()) pb.setParameter("LinkOut", 1);
		if (!jCheckBoxLinkOut.isSelected())pb.setParameter("LinkOut", 0);

	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Method") == 0) buttROI.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttManual.setSelected(true);

		jSpinnerInR.removeChangeListener(this);
		jSpinnerInG.removeChangeListener(this);
		jSpinnerInB.removeChangeListener(this);
		jSpinnerOutR.removeChangeListener(this);
		jSpinnerOutG.removeChangeListener(this);
		jSpinnerOutB.removeChangeListener(this);

		jSpinnerInR.setValue(pb.getIntParameter("InR"));
		jSpinnerInG.setValue(pb.getIntParameter("InG"));
		jSpinnerInB.setValue(pb.getIntParameter("InB"));
		jSpinnerOutR.setValue(pb.getIntParameter("OutR"));
		jSpinnerOutG.setValue(pb.getIntParameter("OutG"));
		jSpinnerOutB.setValue(pb.getIntParameter("OutB"));

		jSpinnerInR.addChangeListener(this);
		jSpinnerInG.addChangeListener(this);
		jSpinnerInB.addChangeListener(this);
		jSpinnerOutR.addChangeListener(this);
		jSpinnerOutG.addChangeListener(this);
		jSpinnerOutB.addChangeListener(this);

		if (pb.getIntParameter("LinkIn") == 0)  jCheckBoxLinkIn.setSelected(false);
		if (pb.getIntParameter("LinkIn") == 1)  jCheckBoxLinkIn.setSelected(true);
		if (pb.getIntParameter("LinkOut") == 0) jCheckBoxLinkOut.setSelected(false);
		if (pb.getIntParameter("LinkOut") == 1) jCheckBoxLinkOut.setSelected(true);

		if (buttROI.isSelected()) {
			jCheckBoxLinkIn.setSelected(false);
			jCheckBoxLinkIn.setEnabled(false);
			jSpinnerInR.setEnabled(false);
			jSpinnerInG.setEnabled(false);
			jSpinnerInB.setEnabled(false);
		}
		if (buttManual.isSelected()) {
			jCheckBoxLinkIn.setEnabled(true);
			jSpinnerInR.setEnabled(true);
			jSpinnerInG.setEnabled(true);
			jSpinnerInB.setEnabled(true);
		}
	}

	// ------------------------------------------------------------------------------

	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");

		PlanarImage pi = ((IqmDataBox) this.workPackage.getSources()
				.get(0)).getImage();
		
		int numBands = pi.getNumBands();
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

		// Set image or ROI dependent initial values;
		if (numBands == 1) {// Grey value image
			jLabelInR.setText("Grey: ");
			jLabelOutR.setText("Grey: ");
			jPanelInG.setVisible(false);
			jPanelInB.setVisible(false);
			jPanelOutG.setVisible(false);
			jPanelOutB.setVisible(false);
			jCheckBoxLinkIn.setVisible(false);
			jCheckBoxLinkOut.setVisible(false);
		} else {
			// reset the values
			jLabelInR.setText("R: ");
			jLabelOutR.setText("R: ");
			jPanelInG.setVisible(true);
			jPanelInB.setVisible(true);
			jPanelOutG.setVisible(true);
			jPanelOutB.setVisible(true);
			jCheckBoxLinkIn.setVisible(true);
			jCheckBoxLinkOut.setVisible(true);
		}

		if (buttROI.isSelected()) {
			jCheckBoxLinkIn.setSelected(false);
			jSpinnerInR.setEnabled(false);
			jSpinnerInG.setEnabled(false);
			jSpinnerInB.setEnabled(false);

			ROIShape roi = Application.getLook().getCurrentLookPanel().getCurrentROILayer().getCurrentROIShape();
			if (roi == null) {
				BoardPanel.appendTextln("ROI is not defined and null!");
				BoardPanel.appendTextln("Calculation with whole image!");
			} else {
				BoardPanel.appendTextln("ROI is defined and valid.");
			}
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(pi);
			pb.add(roi); // ROI
			pb.add(1); // sampling
			pb.add(1);
			RenderedOp meanOp = JAI.create("Mean", pb, null);
			double[] mean = (double[]) meanOp.getProperty("mean");

			jSpinnerInR.setValue(Math.round(mean[0]));
			if (numBands > 1)
				jSpinnerInG.setValue(Math.round(mean[1]));
			if (numBands > 2)
				jSpinnerInB.setValue(Math.round(mean[2]));
		}

		jSpinnerOutR.setValue(250.0 / 255.0 * typeGreyMax);
		jSpinnerOutG.setValue(250.0 / 255.0 * typeGreyMax);
		jSpinnerOutB.setValue(250.0 / 255.0 * typeGreyMax);

		this.updateParameterBlock();
		this.setParameterValuesToGUI();

		this.pack();
	}

	// Buttons--------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtROI() {
		buttROI = new JRadioButton();
		buttROI.setText("ROI");
		buttROI.setToolTipText("initial values taken from current ROI");
		buttROI.addActionListener(this);
		buttROI.setActionCommand("parameter");
		return buttROI;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtManual() {
		// if (buttManual == null) {
		buttManual = new JRadioButton();
		buttManual.setText("Manual");
		buttManual.setToolTipText("initial values taken from spinners");
		buttManual.addActionListener(this);
		buttManual.setActionCommand("parameter");
		// }
		return buttManual;
	}

	/**
	 * This method initializes jJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		if (jPanelMethod== null) {
			jPanelMethod = new JPanel();
			//jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.X_AXIS));
			jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jPanelMethod.add(getJRadioButtonMenuButtROI());
			jPanelMethod.add(getJRadioButtonMenuButtManual());
			// jPanelMethod.addSeparator();
			this.setButtonGroupMethod(); // Grouping of JRadioButtons
		}
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttROI);
		buttGroupMethod.add(buttManual);
	}

	// Spinners------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelInR
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelInR() {
		if (jPanelInR == null) {
			jPanelInR = new JPanel();
			jPanelInR.setLayout(new BorderLayout());
			jLabelInR = new JLabel("R: ");
			jLabelInR.setPreferredSize(new Dimension(50, 20));
			jLabelInR.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, 255, 1); // init, min, max, step
			jSpinnerInR = new JSpinner(sModel);
			jSpinnerInR.setPreferredSize(new Dimension(60, 20));
			jSpinnerInR.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerInR.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelInR.add(jLabelInR, BorderLayout.WEST);
			jPanelInR.add(jSpinnerInR, BorderLayout.CENTER);
		}
		return jPanelInR;
	}

	/**
	 * This method initializes jJPanelInG
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelInG() {
		if (jPanelInG == null) {
			jPanelInG = new JPanel();
			jPanelInG.setLayout(new BorderLayout());
			jLabelInG = new JLabel("G: ");
			jLabelInG.setPreferredSize(new Dimension(50, 20));
			jLabelInG.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, 255, 1); // init, min, max, step
			jSpinnerInG = new JSpinner(sModel);
			jSpinnerInG.setPreferredSize(new Dimension(60, 20));
			jSpinnerInG.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerInG.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelInG.add(jLabelInG, BorderLayout.WEST);
			jPanelInG.add(jSpinnerInG, BorderLayout.CENTER);
		}
		return jPanelInG;
	}

	/**
	 * This method initializes jJPanelInB
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelInB() {
		if (jPanelInB == null) {
			jPanelInB = new JPanel();
			jPanelInB.setLayout(new BorderLayout());
			jLabelInB = new JLabel("B: ");
			jLabelInB.setPreferredSize(new Dimension(50, 20));
			jLabelInB.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, 255, 1); // init, min, max, step
			jSpinnerInB = new JSpinner(sModel);
			jSpinnerInB.setPreferredSize(new Dimension(60, 20));
			jSpinnerInB.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerInB.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelInB.add(jLabelInB, BorderLayout.WEST);
			jPanelInB.add(jSpinnerInB, BorderLayout.CENTER);
		}
		return jPanelInB;
	}

	/**
	 * This method initializes jJPanelOutR
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOutR() {
		if (jPanelOutR == null) {
			jPanelOutR = new JPanel();
			jPanelOutR.setLayout(new BorderLayout());
			jLabelOutR = new JLabel("R: ");
			jLabelOutR.setPreferredSize(new Dimension(50, 20));
			jLabelOutR.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, 255, 1); // init,/ min, max, step
			jSpinnerOutR = new JSpinner(sModel);
			jSpinnerOutR.setPreferredSize(new Dimension(60, 20));
			jSpinnerOutR.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerOutR.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")// ;
			jPanelOutR.add(jLabelOutR, BorderLayout.WEST);
			jPanelOutR.add(jSpinnerOutR, BorderLayout.CENTER);
		}
		return jPanelOutR;
	}

	/**
	 * This method initializes jJPanelOutG
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOutG() {
		if (jPanelOutG == null) {
			jPanelOutG = new JPanel();
			jPanelOutG.setLayout(new BorderLayout());
			jLabelOutG = new JLabel("G: ");
			jLabelOutG.setPreferredSize(new Dimension(50, 20));
			jLabelOutG.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, 255, 1); // init, min,/ max, step
			jSpinnerOutG = new JSpinner(sModel);
			jSpinnerOutG.setPreferredSize(new Dimension(60, 20));
			jSpinnerOutG.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerOutG.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelOutG.add(jLabelOutG, BorderLayout.WEST);
			jPanelOutG.add(jSpinnerOutG, BorderLayout.CENTER);
		}
		return jPanelOutG;
	}

	/**
	 * This method initializes jJPanelOutB
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOutB() {
		if (jPanelOutB == null) {
			jPanelOutB = new JPanel();
			jPanelOutB.setLayout(new BorderLayout());
			jLabelOutB = new JLabel("B: ");
			jLabelOutB.setPreferredSize(new Dimension(50, 20));
			jLabelOutB.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, 255, 1); // init, min, max, step
			jSpinnerOutB = new JSpinner(sModel);
			jSpinnerOutB.setPreferredSize(new Dimension(60, 20));
			jSpinnerOutB.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerOutB.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")// ;
			jPanelOutB.add(jLabelOutB, BorderLayout.WEST);
			jPanelOutB.add(jSpinnerOutB, BorderLayout.CENTER);
		}
		return jPanelOutB;
	}

	// jCheckBox--------------------------------------------------------------------------------------------

	/**
	 * This method initializes jCheckBoxLinkIn
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxLinkIn() {
		if (jCheckBoxLinkIn == null) {
			jCheckBoxLinkIn = new JCheckBox();
			jCheckBoxLinkIn.setText("Link");
			jCheckBoxLinkIn.addActionListener(this);
			jCheckBoxLinkIn.setActionCommand("parameter");
		}
		return jCheckBoxLinkIn;
	}

	/**
	 * This method initializes jCheckBoxLinkOut
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxLinkOut() {
		if (jCheckBoxLinkOut == null) {
			jCheckBoxLinkOut = new JCheckBox();
			jCheckBoxLinkOut.setText("Link");
			jCheckBoxLinkOut.addActionListener(this);
			jCheckBoxLinkOut.setActionCommand("parameter");
		}
		return jCheckBoxLinkOut;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (e.getSource() == buttROI) { // get ROI information
				jCheckBoxLinkIn.setSelected(false);
				jCheckBoxLinkIn.setEnabled(false);
				jSpinnerInR.setEnabled(false);
				jSpinnerInG.setEnabled(false);
				jSpinnerInB.setEnabled(false);

				ROIShape roi = Application.getLook().getCurrentLookPanel()
						.getCurrentROILayer().getCurrentROIShape();
				if (roi == null) {
					BoardPanel
							.appendTextln("OperatorGUI_WhiteBalance: ROI is not defined and null!");
					BoardPanel
							.appendTextln("OperatorGUI_WhiteBalance: calculation with whole image!");
				} else {
					BoardPanel
							.appendTextln("OperatorGUI_WhiteBalance: ROI is defined and valid");
				}
				// PlanarImage pi =
				// Tank.getCurrentTankIqmDataBoxAt(CommonTools.getCurrManagerImgIndex()).getPlanarImage();
				int idx = Application.getManager().getCurrItemIndex();
				IqmDataBox iqmDataBox = Application.getTank().getCurrentTankIqmDataBoxAt(idx);
				PlanarImage pi = iqmDataBox.getImage();
				int numBands = pi.getNumBands();
				ParameterBlock pb = new ParameterBlock();
				pb.addSource(pi);
				pb.removeSources();
				pb.removeParameters();
				pb.addSource(pi);
				pb.add(roi); // ROI
				pb.add(1); // sampling
				pb.add(1);
				RenderedOp meanOp = JAI.create("Mean", pb, null);
				double[] mean = (double[]) meanOp.getProperty("mean");

				jSpinnerInR.setValue(Math.round(mean[0]));
				if (numBands > 1) jSpinnerInG.setValue(Math.round(mean[1]));
				if (numBands > 2) jSpinnerInB.setValue(Math.round(mean[2]));
			}
			if (e.getSource() == buttManual) {
				jCheckBoxLinkIn.setEnabled(true);
				jSpinnerInR.setEnabled(true);
				jSpinnerInG.setEnabled(true);
				jSpinnerInB.setEnabled(true);
			}
		}
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	// -------------------------------------------------------------------------------------------------

	@Override
	public void stateChanged(ChangeEvent e) {
		int inR  = ((Number) jSpinnerInR.getValue()).intValue();
		int inG  = ((Number) jSpinnerInG.getValue()).intValue();
		int inB  = ((Number) jSpinnerInB.getValue()).intValue();
		int outR = ((Number) jSpinnerOutR.getValue()).intValue();
		int outG = ((Number) jSpinnerOutG.getValue()).intValue();
		int outB = ((Number) jSpinnerOutB.getValue()).intValue();

		if (buttROI.isSelected()) {
			jCheckBoxLinkIn.setSelected(false);
		}
		if (jCheckBoxLinkIn.isSelected()) {
			if (jSpinnerInR == e.getSource()) {
				jSpinnerInG.setValue(inR);
				jSpinnerInB.setValue(inR);
			}
			if (jSpinnerInG == e.getSource()) {
				jSpinnerInR.setValue(inG);
				jSpinnerInB.setValue(inG);
			}
			if (jSpinnerInB == e.getSource()) {
				jSpinnerInR.setValue(inB);
				jSpinnerInG.setValue(inB);
			}
		}
		if (jCheckBoxLinkOut.isSelected()) {
			if (jSpinnerOutR == e.getSource()) {
				jSpinnerOutG.setValue(outR);
				jSpinnerOutB.setValue(outR);
			}
			if (jSpinnerOutG == e.getSource()) {
				jSpinnerOutR.setValue(outG);
				jSpinnerOutB.setValue(outG);
			}
			if (jSpinnerOutB == e.getSource()) {
				jSpinnerOutR.setValue(outB);
				jSpinnerOutG.setValue(outB);
			}
		}
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}

	private JPanel getInputPanel() {
		if (inputPanel == null) {
			inputPanel = new JPanel();
			inputPanel.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Input",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0,0, 0)));
			GridBagLayout gbl_inputPanel = new GridBagLayout();
			gbl_inputPanel.columnWidths = new int[] { 0, 0 };
			gbl_inputPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
			gbl_inputPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
			gbl_inputPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			inputPanel.setLayout(gbl_inputPanel);
			GridBagConstraints gbc_jPanelInR = new GridBagConstraints();
			gbc_jPanelInR.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelInR.gridx = 0;
			gbc_jPanelInR.gridy = 0;
			inputPanel.add(getJPanelInR(), gbc_jPanelInR);
			GridBagConstraints gbc_jPanelInG = new GridBagConstraints();
			gbc_jPanelInG.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelInG.gridx = 0;
			gbc_jPanelInG.gridy = 1;
			inputPanel.add(getJPanelInG(), gbc_jPanelInG);
			GridBagConstraints gbc_jPanelInB = new GridBagConstraints();
			gbc_jPanelInB.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelInB.gridx = 0;
			gbc_jPanelInB.gridy = 2;
			inputPanel.add(getJPanelInB(), gbc_jPanelInB);
			GridBagConstraints gbc_jCheckBoxLinkIn = new GridBagConstraints();
			gbc_jCheckBoxLinkIn.anchor = GridBagConstraints.EAST;
			gbc_jCheckBoxLinkIn.gridx = 0;
			gbc_jCheckBoxLinkIn.gridy = 3;
			inputPanel.add(getJCheckBoxLinkIn(), gbc_jCheckBoxLinkIn);
		}
		return inputPanel;
	}

	private JPanel getOutputPanel() {
		if (outputPanel == null) {
			outputPanel = new JPanel();
			outputPanel.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Output",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0,0, 0)));
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 0, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			outputPanel.setLayout(gbl_panel);
			GridBagConstraints gbc_jPanelOutR = new GridBagConstraints();
			gbc_jPanelOutR.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelOutR.gridx = 0;
			gbc_jPanelOutR.gridy = 0;
			outputPanel.add(getJPanelOutR(), gbc_jPanelOutR);
			GridBagConstraints gbc_jPanelOutG = new GridBagConstraints();
			gbc_jPanelOutG.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelOutG.gridx = 0;
			gbc_jPanelOutG.gridy = 1;
			outputPanel.add(getJPanelOutG(), gbc_jPanelOutG);
			GridBagConstraints gbc_jPanelOutB = new GridBagConstraints();
			gbc_jPanelOutB.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelOutB.gridx = 0;
			gbc_jPanelOutB.gridy = 2;
			outputPanel.add(getJPanelOutB(), gbc_jPanelOutB);
			GridBagConstraints gbc_jCheckBoxLinkOut = new GridBagConstraints();
			gbc_jCheckBoxLinkOut.anchor = GridBagConstraints.EAST;
			gbc_jCheckBoxLinkOut.gridx = 0;
			gbc_jCheckBoxLinkOut.gridy = 3;
			outputPanel.add(getJCheckBoxLinkOut(), gbc_jCheckBoxLinkOut);
		}
		return outputPanel;
	}
}// END
