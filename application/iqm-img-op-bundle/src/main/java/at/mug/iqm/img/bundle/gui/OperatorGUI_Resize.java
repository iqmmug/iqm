package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Resize.java
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.media.jai.PlanarImage;
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

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpResizeDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 04
 * @update 2014 12 JRadioButtons and TitledBorder
 */
public class OperatorGUI_Resize extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5161696780502561840L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_Resize.class);

	private ParameterBlockIQM pb = null;

	private float zoomX = 1.0f;
	private float zoomY = 1.0f;

	private JRadioButton butt_10     = null;
	private JRadioButton butt_5 	 = null;
	private JRadioButton butt_2      = null; // "_" means "divide by"
	private JRadioButton buttX2      = null;
	private JRadioButton buttX5      = null;
	private JRadioButton buttX10     = null;
	private JPanel       jPanelMag   = null;
	private ButtonGroup buttGroupMag = null;

	private JRadioButton buttNN        = null; // nearest neighbor
	private JRadioButton buttBL 	   = null; // bilinear
	private JRadioButton buttBC 	   = null; // bicubic
	private JRadioButton buttBC2       = null; // bicubic2
	private JPanel       jPanelIntP    = null;
	private ButtonGroup  buttGroupIntP = null;

	private JPanel jPanelOldWidth = null;
	private JLabel jLabelOldWidth = null;
	private JFormattedTextField jFormattedTextFieldOldWidth = null;
	
	private JPanel jPanelOldHeight = null;
	private JLabel jLabelOldHeight = null;
	private JFormattedTextField jFormattedTextFieldOldHeight = null;
	
	private JPanel jPanelOldSize    = null;
	
	private JPanel   jPanelNewWidth    = null;
	private JLabel   jLabelNewWidth    = null;
	private JSpinner jSpinnerNewWidth  = null;
	private JPanel   jPanelNewHeight   = null;
	private JLabel   jLabelNewHeight   = null;
	private JSpinner jSpinnerNewHeight = null;
	
	private JPanel   jPanelNewSize     = null;

	private JRadioButton buttZoom            = null; // Preference for image stacks without same image sizes
	private JRadioButton buttSize            = null;
	private JPanel       jPanelZoomOrSize    = null;
	private ButtonGroup  buttGroupZoomOrSize = null;
	private JLabel       jLabelZoomOrSize    = null;

	/**
	 * constructor
	 */
	public OperatorGUI_Resize() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpResizeDescriptor().getName());

		this.initialize();

		this.setTitle("Resize");

		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(createJPanelMag(),       createGridBagConstraintsButtonGroupMag());
		this.getOpGUIContent().add(createJPanelIntP(),      createGridBagConstraintsButtonGroupIntP());
//		this.getOpGUIContent().add(createJPanelOldWidth(),  createGridBagConstraintsOldWidth());
//		this.getOpGUIContent().add(createJPanelOldHeight(), createGridBagConstraintsOldHeight());
//		this.getOpGUIContent().add(createJPanelNewWidth(),  createGridBagConstraintsNewWidth());
//		this.getOpGUIContent().add(createJPanelNewHeight(), createGridBagConstraintsNewHeight());
		this.getOpGUIContent().add(createJPanelOldSize(),   createGridBagConstraintsOldSize());
		this.getOpGUIContent().add(createJPanelNewSize(),   createGridBagConstraintsNewSize());
		this.getOpGUIContent().add(createJPanelZoomOrSize(),createGridBagConstraintsZoomOrSize());

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 */
	@Override
	public void updateParameterBlock() {
		pb.setParameter("ZoomX", zoomX);
		pb.setParameter("ZoomY", zoomY);

		pb.setParameter("NewWidth", ((Number) jSpinnerNewWidth.getValue()).intValue());
		pb.setParameter("NewHeight",((Number) jSpinnerNewHeight.getValue()).intValue());

		if (buttNN.isSelected()) pb.setParameter("Interpolation", 0);
		if (buttBL.isSelected()) pb.setParameter("Interpolation", 1);
		if (buttBC.isSelected()) pb.setParameter("Interpolation", 2);
		if (buttBC2.isSelected())pb.setParameter("Interpolation", 3);

		if (buttZoom.isSelected()) pb.setParameter("ZoomOrSize", 0);
		if (buttSize.isSelected()) pb.setParameter("ZoomOrSize", 1);
	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		zoomX = pb.getFloatParameter("ZoomX");
		zoomY = pb.getFloatParameter("ZoomY");

		jSpinnerNewWidth.removeChangeListener(this);
		jSpinnerNewHeight.removeChangeListener(this);
		jSpinnerNewWidth.setValue(pb.getIntParameter("NewWidth"));
		jSpinnerNewHeight.setValue(pb.getIntParameter("NewHeight"));
		jSpinnerNewWidth.addChangeListener(this);
		jSpinnerNewHeight.addChangeListener(this);

		if (new Float(zoomX).equals(new Float(zoomY))) {
			if (Math.round(1.0f / zoomX) == 10.0f) butt_10.setSelected(true); // ensures button appearance
			if (Math.round(1.0f / zoomX) == 5.0f)  butt_5.setSelected(true); // ensures button appearance
			if (zoomX == (1.0f / 2.0f))            butt_2.setSelected(true);
			if (zoomX == 2.0f)                     buttX2.setSelected(true);
			if (zoomX == 5.0f)                     buttX5.setSelected(true);
			if (zoomX == 10.0f)                    buttX10.setSelected(true);
		}
		if (pb.getIntParameter("Interpolation") == 0) buttNN.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 1) buttBL.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 2) buttBC.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 3) buttBC2.setSelected(true);

		if (pb.getIntParameter("ZoomOrSize") == 0) buttZoom.setSelected(true);
		if (pb.getIntParameter("ZoomOrSize") == 1) buttSize.setSelected(true);

	}

	/**
	 * This method updates the GUI if needed This method overrides IqmOpJFrame
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		this.setOldValues();
		this.setNewWidthHeight();
		this.updateParameterBlock();
		this.setParameterValuesToGUI();
	}

	/**
	 * This method displays the old values
	 */
	private void setOldValues() {
		PlanarImage pi = ((IqmDataBox) this.workPackage.getSources().get(0)).getImage();
		jFormattedTextFieldOldWidth.setValue(pi.getWidth());
		jFormattedTextFieldOldHeight.setValue(pi.getHeight());
	}

	/**
	 * This method displays the new values
	 */
	private void setNewWidthHeight() {

		IqmDataBox iqmDataBox = (IqmDataBox) this.workPackage.getParameters().getSource(0);
		PlanarImage pi = iqmDataBox.getImage();

		int[] newValues = calcNewWidthHeight(zoomX, zoomY, pi.getWidth(),pi.getHeight());
		jSpinnerNewWidth.removeChangeListener(this);
		jSpinnerNewHeight.removeChangeListener(this);
		jSpinnerNewWidth.setValue(newValues[0]);
		jSpinnerNewHeight.setValue(newValues[1]);
		jSpinnerNewWidth.addChangeListener(this);
		jSpinnerNewHeight.addChangeListener(this);

	}

	/**
	 * This method calculates the new values
	 * 
	 * @param int optZoom pressed button int argX old x value, int argY old y
	 *        value
	 * @return int[] the new x and y value
	 */
	private int[] calcNewWidthHeight(float zoomX, float zoomY, int argX,
			int argY) {
		// calculate new size
		int result[] = new int[2];
		result[0] = Math.round(argX * zoomX);
		result[1] = Math.round(argY * zoomY);
		return result;
	}
	
//	private GridBagConstraints createGridBagConstraintsOldWidth() {
//	GridBagConstraints gridBagConstraintsOldWidth = new GridBagConstraints();
//	gridBagConstraintsOldWidth.anchor = GridBagConstraints.EAST;
//	gridBagConstraintsOldWidth.gridx = 0;
//	gridBagConstraintsOldWidth.gridy = 1;
//	gridBagConstraintsOldWidth.gridwidth = 1;// ?
//	gridBagConstraintsOldWidth.insets = new Insets(10, 5, 0, 0); // top left  bottom  right
//	// gridBagConstraintsOldWidth.fill = GridBagConstraints.BOTH;
//	return gridBagConstraintsOldWidth;
//}
//
//private GridBagConstraints createGridBagConstraintsOldHeight() {
//	GridBagConstraints gridBagConstraintsOldHeight = new GridBagConstraints();
//	gridBagConstraintsOldHeight.anchor = GridBagConstraints.WEST;
//	gridBagConstraintsOldHeight.gridx = 1;
//	gridBagConstraintsOldHeight.gridy = 1;
//	gridBagConstraintsOldHeight.gridwidth = 1;// ?
//	gridBagConstraintsOldHeight.insets = new Insets(10, 10, 0, 5); // top  left  bottom  right
//	// gridBagConstraintsOldHeight.fill = GridBagConstraints.BOTH;
//	return gridBagConstraintsOldHeight;
//}

private GridBagConstraints createGridBagConstraintsOldSize() {
	GridBagConstraints gridBagConstraintsOldSize = new GridBagConstraints();
	gridBagConstraintsOldSize.anchor = GridBagConstraints.WEST;
	gridBagConstraintsOldSize.gridx = 0;
	gridBagConstraintsOldSize.gridy = 0;
	gridBagConstraintsOldSize.gridwidth = 2;// ?
	gridBagConstraintsOldSize.insets = new Insets(10, 0, 0, 5); // top  left  bottom  right
	gridBagConstraintsOldSize.fill = GridBagConstraints.BOTH;
	return gridBagConstraintsOldSize;
}

	private GridBagConstraints createGridBagConstraintsButtonGroupMag() {
		GridBagConstraints gridBagConstraintsButtonMagGroup = new GridBagConstraints();
		gridBagConstraintsButtonMagGroup.gridx = 0;
		gridBagConstraintsButtonMagGroup.gridy = 1;
		// gridBagConstraintsButtonMagGroup.gridwidth = 3;//?
		gridBagConstraintsButtonMagGroup.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		gridBagConstraintsButtonMagGroup.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsButtonMagGroup;
	}

	private GridBagConstraints createGridBagConstraintsButtonGroupIntP() {
		GridBagConstraints gridBagConstraintsButtonIntPGroup = new GridBagConstraints();
		gridBagConstraintsButtonIntPGroup.gridx = 1;
		gridBagConstraintsButtonIntPGroup.gridy = 1;
		gridBagConstraintsButtonIntPGroup.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		//gridBagConstraintsButtonIntPGroup.fill = GridBagConstraints.BOTH;
		//gridBagConstraintsButtonIntPGroup.anchor = GridBagConstraints.NORTH;
		return gridBagConstraintsButtonIntPGroup;
	}


//	private GridBagConstraints createGridBagConstraintsNewWidth() {
//		GridBagConstraints gridBagConstraintsNewWidth = new GridBagConstraints();
//		gridBagConstraintsNewWidth.anchor = GridBagConstraints.EAST;
//		gridBagConstraintsNewWidth.gridx = 0;
//		gridBagConstraintsNewWidth.gridy = 2;
//		gridBagConstraintsNewWidth.gridwidth = 1;// ?
//		gridBagConstraintsNewWidth.insets = new Insets(10, 5, 0, 0); // top  left  bottom  right
//		// gridBagConstraintsNewWidth.fill = GridBagConstraints.BOTH;
//		return gridBagConstraintsNewWidth;
//	}
//
//	private GridBagConstraints createGridBagConstraintsNewHeight() {
//		GridBagConstraints gridBagConstraintsNewHeight = new GridBagConstraints();
//		gridBagConstraintsNewHeight.anchor = GridBagConstraints.WEST;
//		gridBagConstraintsNewHeight.gridx = 1;
//		gridBagConstraintsNewHeight.gridy = 2;
//		gridBagConstraintsNewHeight.gridwidth = 1;// ?
//		gridBagConstraintsNewHeight.insets = new Insets(10, 10, 0, 5); // top  left  bottom  right
//		// gridBagConstraintsNewHeight.fill = GridBagConstraints.BOTH;
//		return gridBagConstraintsNewHeight;
//	}
	
	private GridBagConstraints createGridBagConstraintsNewSize() {
		GridBagConstraints gridBagConstraintsNewSize = new GridBagConstraints();
		gridBagConstraintsNewSize.anchor = GridBagConstraints.WEST;
		gridBagConstraintsNewSize.gridx = 0;
		gridBagConstraintsNewSize.gridy = 2;
		gridBagConstraintsNewSize.gridwidth = 2;// ?
		gridBagConstraintsNewSize.insets = new Insets(5, 0, 0, 5); // top  left  bottom  right
		gridBagConstraintsNewSize.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsNewSize;
	}

	private GridBagConstraints createGridBagConstraintsZoomOrSize() {
		GridBagConstraints gridBagConstraintsButtonZoomOrSizeGroup = new GridBagConstraints();
		gridBagConstraintsButtonZoomOrSizeGroup.gridx = 0;
		gridBagConstraintsButtonZoomOrSizeGroup.gridy = 3;
		gridBagConstraintsButtonZoomOrSizeGroup.gridwidth = 2;// ?
		gridBagConstraintsButtonZoomOrSizeGroup.insets = new Insets(10, 0, 0, 5); // top  left  bottom  right
		gridBagConstraintsButtonZoomOrSizeGroup.fill = GridBagConstraints.HORIZONTAL;
		return gridBagConstraintsButtonZoomOrSizeGroup;
	}

	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: /10
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButt_10() {
		butt_10 = new JRadioButton();
		butt_10.setText("/10");
		butt_10.setToolTipText("resizes to 1/10");
		butt_10.addActionListener(this);
		butt_10.setActionCommand("parameter");
		return butt_10;
	}

	/**
	 * This method initializes the Option: /5
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButt_5() {
		butt_5 = new JRadioButton();
		butt_5.setText("/5");
		butt_5.setToolTipText("resizes to 1/5");
		butt_5.addActionListener(this);
		butt_5.setActionCommand("parameter");
		return butt_5;
	}

	/**
	 * This method initializes the Option: /2
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButt_2() {
		butt_2 = new JRadioButton();
		butt_2.setText("/2");
		butt_2.setToolTipText("resizes to 1/2");
		butt_2.addActionListener(this);
		butt_2.setActionCommand("parameter");
		return butt_2;
	}

	/**
	 * This method initializes the Option: 2 times
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtX2() {
		buttX2 = new JRadioButton();
		buttX2.setText("x2");
		buttX2.setToolTipText("resizes 2 times");
		buttX2.addActionListener(this);
		buttX2.setActionCommand("parameter");
		return buttX2;
	}

	/**
	 * This method initializes the Option: 5 times
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtX5() {
		buttX5 = new JRadioButton();
		buttX5.setText("x5");
		buttX5.setToolTipText("resizes 5 times");
		buttX5.addActionListener(this);
		buttX5.setActionCommand("parameter");
		return buttX5;
	}

	/**
	 * This method initializes the Option: 10 times
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtX10() {
		buttX10 = new JRadioButton();
		buttX10.setText("x10");
		buttX10.setToolTipText("resizes 10 times");
		buttX10.addActionListener(this);
		buttX10.setActionCommand("parameter");
		return buttX10;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelMag() {
		jPanelMag = new JPanel();
		jPanelMag.setLayout(new BoxLayout(jPanelMag, BoxLayout.Y_AXIS));
		//jPanelMag.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelMag.setBorder(new TitledBorder(null, "Factor", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMag.add(getJRadioButtonButt_10());
		jPanelMag.add(getJRadioButtonButt_5());
		jPanelMag.add(getJRadioButtonButt_2());
		jPanelMag.add(getJRadioButtonButtX2());
		jPanelMag.add(getJRadioButtonButtX5());
		jPanelMag.add(getJRadioButtonButtX10());
		// jPanelMag.addSeparator();
		this.setButtonGroupMag(); // Grouping of JRadioButtons
		return jPanelMag;
	}

	private void setButtonGroupMag() {
		buttGroupMag = new ButtonGroup();
		buttGroupMag.add(butt_10);
		buttGroupMag.add(butt_5);
		buttGroupMag.add(butt_2);
		buttGroupMag.add(buttX2);
		buttGroupMag.add(buttX5);
		buttGroupMag.add(buttX10);
	}

	// -------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Nearest Neighbor
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtNN() {
		buttNN = new JRadioButton();
		buttNN.setText("Nearest Neighbor");
		buttNN.setToolTipText("uses Nearest Neighbor resampling for resizing");
		buttNN.addActionListener(this);
		buttNN.setActionCommand("parameter");
		return buttNN;
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
		jPanelIntP.add(getJRadioButtonButtNN());
		jPanelIntP.add(getJRadioButtonButtBL());
		jPanelIntP.add(getJRadioButtonButtBC());
		jPanelIntP.add(getJRadioButtonButtBC2());
		// jPanelIntP.addSeparator();
		this.setButtonGroupIntP(); // Grouping of JRadioButtons
		return jPanelIntP;
	}

	private void setButtonGroupIntP() {
		buttGroupIntP = new ButtonGroup();
		buttGroupIntP.add(buttNN);
		buttGroupIntP.add(buttBL);
		buttGroupIntP.add(buttBC);
		buttGroupIntP.add(buttBC2);
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelOldWidth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelOldWidth() {
		if (jPanelOldWidth == null) {
			jPanelOldWidth = new JPanel();
			jPanelOldWidth.setLayout(new BorderLayout());
			// jPanelOldWidth.setPreferredSize(new Dimension(250,18));
			jLabelOldWidth = new JLabel("Width: ");
			jLabelOldWidth.setHorizontalAlignment(SwingConstants.RIGHT);
			jFormattedTextFieldOldWidth = new JFormattedTextField(NumberFormat.getNumberInstance());
			// jFormattedTextFieldOldWidth.addPropertyChangeListener("value",/ this);
			// jFormattedTextFieldOldWidth.setInputVerifier(new IntNumberVerifier());
			jFormattedTextFieldOldWidth.setEditable(false);
			// jFormattedTextFieldOldWidth.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
			jFormattedTextFieldOldWidth.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) jFormattedTextFieldOldWidth.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelOldWidth.add(jLabelOldWidth, BorderLayout.WEST);
			jPanelOldWidth.add(jFormattedTextFieldOldWidth, BorderLayout.CENTER);

		}
		return jPanelOldWidth;
	}

	/**
	 * This method initializes jJPanelOldHeight
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelOldHeight() {
		if (jPanelOldHeight == null) {
			jPanelOldHeight = new JPanel();
			jPanelOldHeight.setLayout(new BorderLayout());
			jLabelOldHeight = new JLabel("Height: ");
			jLabelOldHeight.setHorizontalAlignment(SwingConstants.RIGHT);
			jFormattedTextFieldOldHeight = new JFormattedTextField(NumberFormat.getNumberInstance());
			// jFormattedTextFieldOldHeight.addPropertyChangeListener("value", this);
			// jFormattedTextFieldOldHeight.setInputVerifier(new IntNumberVerifier());
			jFormattedTextFieldOldHeight.setEditable(false);
			// jFormattedTextFieldOldHeight.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
			jFormattedTextFieldOldHeight.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) jFormattedTextFieldOldHeight.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelOldHeight.add(jLabelOldHeight, BorderLayout.WEST);
			jPanelOldHeight.add(jFormattedTextFieldOldHeight, BorderLayout.CENTER);
			jFormattedTextFieldOldHeight.setEnabled(true);
		}
		return jPanelOldHeight;
	}

	/**
	 * This method initializes jJPanelOldsize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelOldSize() {
		if (jPanelOldSize == null) {
			jPanelOldSize = new JPanel();
			//jPanelOldSize.setLayout(new BoxLayout(jPanelOldSize, BoxLayout.X_AXIS));
			jPanelOldSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jPanelOldSize.setBorder(new TitledBorder(null, "Original size", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelOldSize.add(this.createJPanelOldWidth());
			jPanelOldSize.add(this.createJPanelOldHeight());	
		}
		return jPanelOldSize;
	}
	
	//----------------------------------------------------------------------------------------------------
	
	/**
	 * This method initializes jJPanelNewWidth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelNewWidth() {
		if (jPanelNewWidth == null) {
			jPanelNewWidth = new JPanel();
			jPanelNewWidth.setLayout(new BorderLayout());
			jLabelNewWidth = new JLabel("Width: ");
			jLabelNewWidth.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNewWidth = new JSpinner(sModel);
			//jSpinnerNewWidth.setPreferredSize(new Dimension(60, 20));
			jSpinnerNewWidth.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNewWidth.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelNewWidth.add(jLabelNewWidth, BorderLayout.WEST);
			jPanelNewWidth.add(jSpinnerNewWidth, BorderLayout.CENTER);
		}
		return jPanelNewWidth;
	}

	/**
	 * This method initializes jJPanelNewHeight
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelNewHeight() {
		if (jPanelNewHeight == null) {
			jPanelNewHeight = new JPanel();
			jPanelNewHeight.setLayout(new BorderLayout());
			jLabelNewHeight = new JLabel("Height: ");
			jLabelNewHeight.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNewHeight = new JSpinner(sModel);
			//jSpinnerNewHeight.setPreferredSize(new Dimension(60, 20));
			jSpinnerNewHeight.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNewHeight.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelNewHeight.add(jLabelNewHeight, BorderLayout.WEST);
			jPanelNewHeight.add(jSpinnerNewHeight, BorderLayout.CENTER);
		}
		return jPanelNewHeight;
	}
	
	/**
	 * This method initializes jJPanelNewsize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelNewSize() {
		if (jPanelNewSize == null) {
			jPanelNewSize = new JPanel();
			//jPanelNewSize.setLayout(new BoxLayout(jPanelNewSize, BoxLayout.X_AXIS));
			jPanelNewSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jPanelNewSize.setBorder(new TitledBorder(null, "New size", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelNewSize.add(this.createJPanelNewWidth());
			jPanelNewSize.add(this.createJPanelNewHeight());	
		}
		return jPanelNewSize;
	}
	
	//--------------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtZoom() {
		buttZoom = new JRadioButton();
		buttZoom.setText("Zoom");
		buttZoom.setToolTipText("prefers zoom setting instead of new size values");
		buttZoom.addActionListener(this);
		buttZoom.setActionCommand("parameter");
		return buttZoom;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtSize() {
		buttSize = new JRadioButton();
		buttSize.setText("New Size");
		buttSize.setToolTipText("prefers new size setting instead of zoom values");
		buttSize.addActionListener(this);
		buttSize.setActionCommand("parameter");
		return buttSize;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelZoomOrSize() {	
		jLabelZoomOrSize = new JLabel("<html>Preference, if image <br/>size varies in a stack : </html>");
		jPanelZoomOrSize = new JPanel();
		//jPanelZoomOrSize.setLayout(new BoxLayout(jPanelZoomOrSize, BoxLayout.Y_AXIS));
		jPanelZoomOrSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelZoomOrSize.setBorder(new TitledBorder(null, "Resize preference", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelZoomOrSize.add(getJRadioButtonButtZoom());
		jPanelZoomOrSize.add(getJRadioButtonButtSize());
		// jPanelZoomOrSize.addSeparator();
		this.setButtonGroupZoomOrSize(); // Grouping of JRadioButtons
		return jPanelZoomOrSize;
	}

	private void setButtonGroupZoomOrSize() {
		buttGroupZoomOrSize = new ButtonGroup();
		buttGroupZoomOrSize.add(buttZoom);
		buttGroupZoomOrSize.add(buttSize);
	}

	// ----------------------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("parameter".equals(e.getActionCommand())) {

			if (butt_10 == e.getSource()) {
				zoomX = 1.0f / 10.0f;
				zoomY = 1.0f / 10.0f;
			}
			if (butt_5 == e.getSource()) {
				zoomX = 1.0f / 5.0f;
				zoomY = 1.0f / 5.0f;
			}
			if (butt_2 == e.getSource()) {
				zoomX = 1.0f / 2.0f;
				zoomY = 1.0f / 2.0f;
			}
			if (buttX2 == e.getSource()) {
				zoomX = 2.0f;
				zoomY = 2.0f;
			}
			if (buttX5 == e.getSource()) {
				zoomX = 5.0f;
				zoomY = 5.0f;
			}
			if (buttX10 == e.getSource()) {
				zoomX = 10.0f;
				zoomY = 10.0f;
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
		int newWidth   = ((Number) jSpinnerNewWidth.getValue()).intValue();
		int newHeight  = ((Number) jSpinnerNewHeight.getValue()).intValue();
		int oldWidth   = ((Number) jFormattedTextFieldOldWidth.getValue()).intValue();
		int oldHeight  = ((Number) jFormattedTextFieldOldHeight.getValue()).intValue();

		if (jSpinnerNewWidth == e.getSource()) {
			zoomX = (float) newWidth / (float) oldWidth;
			zoomY = (float) newHeight / (float) oldHeight;

		}
		if (jSpinnerNewHeight == e.getSource()) {
			zoomX = (float) newWidth  / (float) oldWidth;
			zoomY = (float) newHeight / (float) oldHeight;
		}
		this.updateParameterBlock();
		this.setParameterValuesToGUI();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

}// END
