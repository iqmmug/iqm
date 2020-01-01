package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Crop.java
 * 
 * $Id$
 * $HeadURL$
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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;
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
import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpCropDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 06
 * @update 2014 12 JRadioButtons and TitledBorders 
 */
public class OperatorGUI_Crop extends AbstractImageOperatorGUI implements
		ActionListener, PropertyChangeListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6705207366786692245L;

	// class specific logger
	private static Class<?> caller = OperatorGUI_Crop.class;
	private static final Logger logger = LogManager.getLogger(OperatorGUI_Crop.class);

	private ParameterBlockIQM pb = null;

	private JRadioButton buttManual       = null;
	private JRadioButton buttROI          = null;
	private JRadioButton buttMethodHidden = null;
	private JPanel       jPanelMethod     = null;
	private ButtonGroup  buttGroupMethod  = null;

	private JPanel    jPanelOldWidth = null;
	private JLabel    jLabelOldWidth = null;
	private JFormattedTextField jFormattedTextFieldOldWidth = null;
	private JPanel    jPanelOldHeight = null;
	private JLabel    jLabelOldHeight = null;
	private JFormattedTextField jFormattedTextFieldOldHeight = null;
	private JPanel    jPanelOldSize   = null;
	
	private JPanel    jPanelOffSetX   = null;
	private JLabel    jLabelOffSetX   = null;
	private JSpinner  jSpinnerOffSetX = null;
	private JPanel    jPanelOffSetY   = null;
	private JLabel    jLabelOffSetY   = null;
	private JSpinner  jSpinnerOffSetY = null;
	private JPanel    jPanelOffSetXY  = null;
	
	private JRadioButton buttCenter        = null;
	private JRadioButton buttLeftTop       = null;
	private JRadioButton buttLeftBottom    = null;
	private JRadioButton buttRightTop      = null;
	private JRadioButton buttRightBottom   = null;
	private JRadioButton buttUser          = null;
	private JPanel       jPanel_ButtOffSet = null;
	private ButtonGroup  buttGroupOffSet   = null;
	
	private JPanel    jPanelOffSet  = null;
	
	private JPanel              jPanelNewWidth              = null;
	private JLabel              jLabelNewWidth              = null;
	private JFormattedTextField jFormattedTextFieldNewWidth = null;
	private JSpinner            jSpinnerNewWidth            = null;
	
	private JPanel              jPanelNewHeight              = null;
	private JLabel              jLabelNewHeight              = null;
	private JFormattedTextField jFormattedTextFieldNewHeight = null;
	private JSpinner            jSpinnerNewHeight            = null;
	private JPanel              jPanelNewSize                = null;
	
	/**
	 * constructor
	 */
	public OperatorGUI_Crop() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpCropDescriptor().getName());
		this.initialize();
		this.setTitle("Crop");

		this.getOpGUIContent().setLayout(new GridBagLayout());
		this.getOpGUIContent().add(getJPanel_OldSize(),    getGridBagConstraints_OldSize());
		this.getOpGUIContent().add(getJPanel_Method(),     getGridBagConstraints_ButtonMethodGroup());
		this.getOpGUIContent().add(getJPanel_OffSet(),     getGridBagConstraints_OffSet());
		this.getOpGUIContent().add(getJPanel_NewSize(),    getGridBagConstraints_NewSize());
	
		this.pack();
	}
	
//	private GridBagConstraints getGridBagConstraintsOldWidth() {
//		GridBagConstraints gbc_OldWidth = new GridBagConstraints();
//		gbc_OldWidth.anchor = GridBagConstraints.EAST;
//		gbc_OldWidth.gridx = 0;
//		gbc_OldWidth.gridy = 1;
//		gbc_OldWidth.gridwidth = 1;
//		gbc_OldWidth.insets = new Insets(10, 0, 5, 5); // top left bottom right
//		// gbc_OldWidth.fill = GridBagConstraints.BOTH;
//		return gbc_OldWidth;
//	}
//	private GridBagConstraints getGridBagConstraintsOldHeight() {
//		GridBagConstraints gbc_OldHeight = new GridBagConstraints();
//		gbc_OldHeight.anchor = GridBagConstraints.EAST;
//		gbc_OldHeight.gridx = 1;
//		gbc_OldHeight.gridy = 1;
//		gbc_OldHeight.insets = new Insets(10, 20, 5, 0); // top left bottom right
//		// gbc_OldHeight.fill = GridBagConstraints.BOTH;
//		return gbc_OldHeight;
//	}
	private GridBagConstraints getGridBagConstraints_OldSize() {
		GridBagConstraints gbc_OldSize = new GridBagConstraints();
		gbc_OldSize.anchor = GridBagConstraints.EAST;
		gbc_OldSize.gridx = 0;
		gbc_OldSize.gridy = 0;
		gbc_OldSize.gridwidth = 1;
		gbc_OldSize.insets = new Insets(10, 0, 0, 0); // top left bottom right
		gbc_OldSize.fill = GridBagConstraints.BOTH;
		return gbc_OldSize;
	}
	private GridBagConstraints getGridBagConstraints_ButtonMethodGroup() {
		GridBagConstraints gbc_ButtonMethodGroup = new GridBagConstraints();
		gbc_ButtonMethodGroup.gridx = 0;
		gbc_ButtonMethodGroup.gridy = 1;
		gbc_ButtonMethodGroup.gridwidth = 1;
		gbc_ButtonMethodGroup.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_ButtonMethodGroup.fill = GridBagConstraints.BOTH;
		return gbc_ButtonMethodGroup;
	}
//	private GridBagConstraints getGridBagConstraints_ButtOffSet(){
//		GridBagConstraints gbc_OffSet = new GridBagConstraints();
//		gbc_OffSet.gridx = 0;
//		gbc_OffSet.gridy = 2;
//		gbc_OffSet.gridwidth = 2;
//		gbc_OffSet.insets = new Insets(10, 0, 0, 0);// top left bottom right
//		gbc_OffSet.anchor = GridBagConstraints.NORTH;
//		return gbc_OffSet;
//	}
//	private GridBagConstraints getGridBagConstraints_OffSetX() {
//		GridBagConstraints gbc_OffSetX = new GridBagConstraints();
//		gbc_OffSetX.anchor = GridBagConstraints.EAST;
//		gbc_OffSetX.gridx = 0;
//		gbc_OffSetX.gridy = 3;
//		gbc_OffSetX.insets = new Insets(10, 0, 0, 0); // top left bottom right
//		// gbc_OffSetX.fill = GridBagConstraints.BOTH;
//		return gbc_OffSetX;
//	}
//	private GridBagConstraints getGridBagConstraints_OffSetY() {
//		GridBagConstraints gbc_OffSetY = new GridBagConstraints();
//		gbc_OffSetY.gridx = 1;
//		gbc_OffSetY.gridy = 3;
//		gbc_OffSetY.insets = new Insets(10, 0, 0, 0); // top left bottom right
//		//gbc_OffSetY.fill = GridBagConstraints.BOTH;
//		return gbc_OffSetY;
//	}
	
	private GridBagConstraints getGridBagConstraints_OffSet(){
		GridBagConstraints gbc_OffSet = new GridBagConstraints();
		gbc_OffSet.gridx = 0;
		gbc_OffSet.gridy = 2;
		gbc_OffSet.gridwidth = 1;
		gbc_OffSet.insets = new Insets(10, 0, 0, 0);// top left bottom right
		//gbc_OffSet.anchor = GridBagConstraints.NORTH;
		gbc_OffSet.fill =  GridBagConstraints.BOTH;
		return gbc_OffSet;
	}
	
//	private GridBagConstraints getGridBagConstraintsNewWidth() {
//		GridBagConstraints gbc_NewWidth = new GridBagConstraints();
//		gbc_NewWidth.gridx = 0;
//		gbc_NewWidth.gridy = 3;
//		gbc_NewWidth.gridwidth = 1;
//		gbc_NewWidth.insets = new Insets(10, 0, 0, 0); // top left bottom right
//		// gridBagConstraintsNewWidth.fill = GridBagConstraints.BOTH;
//		return gbc_NewWidth;
//	}
//	private GridBagConstraints getGridBagConstraintsNewHeight() {
//		GridBagConstraints gbc_NewHeight = new GridBagConstraints();
//		gbc_NewHeight.anchor = GridBagConstraints.EAST;
//		gbc_NewHeight.gridx = 1;
//		gbc_NewHeight.gridy = 3;
//		gbc_NewHeight.insets = new Insets(10, 0, 0, 0); // top left bottom right
//		// gridBagConstraintsNewHeight.fill = GridBagConstraints.BOTH;
//		return gbc_NewHeight;
//	}
	private GridBagConstraints getGridBagConstraints_NewSize() {
		GridBagConstraints gbc_NewSize = new GridBagConstraints();
		gbc_NewSize.gridx = 0;
		gbc_NewSize.gridy = 3;
		gbc_NewSize.gridwidth = 1;
		gbc_NewSize.insets = new Insets(10, 0, 0, 0); // top left bottom right
		gbc_NewSize.fill = GridBagConstraints.BOTH;
		return gbc_NewSize;
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttManual.isSelected())  pb.setParameter("Method", 0);
		if (buttROI.isSelected())     pb.setParameter("Method", 1);
		
		pb.setParameter("OffSetX",   ((Number) jSpinnerOffSetX.getValue()).intValue());
		pb.setParameter("OffSetY",   ((Number) jSpinnerOffSetY.getValue()).intValue());
		pb.setParameter("NewWidth",  ((Number) jSpinnerNewWidth.getValue()).intValue());
		pb.setParameter("NewHeight", ((Number) jSpinnerNewHeight.getValue()).intValue());
		
		if (buttCenter.isSelected())      pb.setParameter("OffSet", 0);
		if (buttLeftTop.isSelected())     pb.setParameter("OffSet", 1);	
		if (buttLeftBottom.isSelected())  pb.setParameter("OffSet", 2);
		if (buttRightTop.isSelected())    pb.setParameter("OffSet", 3);
		if (buttRightBottom.isSelected()) pb.setParameter("OffSet", 4);
		if (buttUser.isSelected())        pb.setParameter("OffSet", 5);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		// #####
		// set the old width from the source to the text fields
		PlanarImage pi = ((IqmDataBox) this.pb.getSources().firstElement()).getImage();

		jFormattedTextFieldOldWidth.setValue(pi.getWidth());
		jFormattedTextFieldOldHeight.setValue(pi.getHeight());// #####

		if (pb.getIntParameter("Method") == 0) buttManual.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttROI.setSelected(true);

		jSpinnerOffSetX.removeChangeListener(this);
		jSpinnerOffSetY.removeChangeListener(this);
		jSpinnerNewWidth.removeChangeListener(this);
		jSpinnerNewHeight.removeChangeListener(this);
		jSpinnerOffSetX.setValue(pb.getIntParameter("OffSetX"));
		jSpinnerOffSetY.setValue(pb.getIntParameter("OffSetY"));
		jSpinnerNewWidth.setValue(pb.getIntParameter("NewWidth"));
		jSpinnerNewHeight.setValue(pb.getIntParameter("NewHeight"));
		jSpinnerOffSetX.addChangeListener(this);
		jSpinnerOffSetY.addChangeListener(this);
		jSpinnerNewWidth.addChangeListener(this);
		jSpinnerNewHeight.addChangeListener(this);

		if (pb.getIntParameter("OffSet") == 0) buttCenter.setSelected(true);
		if (pb.getIntParameter("OffSet") == 1) buttLeftTop.setSelected(true);
		if (pb.getIntParameter("OffSet") == 2) buttLeftBottom.setSelected(true);
		if (pb.getIntParameter("OffSet") == 3) buttRightTop.setSelected(true);
		if (pb.getIntParameter("OffSet") == 4) buttRightBottom.setSelected(true);
		if (pb.getIntParameter("OffSet") == 5) buttUser.setSelected(true);

		if ((buttUser.isSelected()) && (buttManual.isSelected())) {
			jSpinnerOffSetX.setEnabled(true);
			jSpinnerOffSetY.setEnabled(true);
		} else {
			jSpinnerOffSetX.setEnabled(false);
			jSpinnerOffSetY.setEnabled(false);
		}
	}


	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		
//		//already implemented in ActionPerformed event
//		if (buttManual.isSelected()){					
//		}
//		if (buttROI.isSelected()){		
//		}
		
		this.updateParameterBlock();
		this.setParameterValuesToGUI();
	}

	// -----------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtManual() {
		if (buttManual == null) {
			buttManual = new JRadioButton();
			buttManual.setText("Manual");
			buttManual.setToolTipText("manual crop settings");
			buttManual.addActionListener(this);
			buttManual.setActionCommand("parameter");
		}
		return buttManual;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtROI() {
		if (buttROI == null) {
			buttROI = new JRadioButton();
			buttROI.setText("ROI");
			buttROI.setToolTipText("crop ROI");
			buttROI.addActionListener(this);
			buttROI.setActionCommand("parameter");
		}
		return buttROI;
	}

	/**
	 * This method initializes the hidden option
	 * 
	 * @return javax.swing.JRadioButton
	 */
	@SuppressWarnings("unused")
	private JRadioButton getJRadioButtonMenuButtMethodHidden() {
		// if (buttMethodHidden == null) {
		buttMethodHidden = new JRadioButton();
		buttMethodHidden.setText("Hidden");
		buttMethodHidden.setVisible(false);
		// }
		return buttMethodHidden;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_Method() {
		// if (jPanelMethod== null) {
		jPanelMethod = new JPanel();
		//jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethod.add(getJRadioButtonMenuButtManual());
		jPanelMethod.add(getJRadioButtonMenuButtROI());
		// jPanelMethod.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons 
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttManual);
		buttGroupMethod.add(buttROI);
		buttGroupMethod.add(buttMethodHidden);
	}

	// -----------------------------------------------------------------------
	// class IntNumberVerifier extends InputVerifier { //damit muss Eingabe
	// richtig sein
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
	 * This method initializes jJPanelOldWidth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOldWidth() {
		if (jPanelOldWidth == null) {
			jPanelOldWidth = new JPanel();
			jPanelOldWidth.setLayout(new BorderLayout());
			// jPanelOldWidth.setPreferredSize(new Dimension(250,18));
			jLabelOldWidth = new JLabel("Width: ");
			jLabelOldWidth.setHorizontalAlignment(SwingConstants.RIGHT);
			jFormattedTextFieldOldWidth = new JFormattedTextField(NumberFormat.getNumberInstance());
			// jFormattedTextFieldOldWidth.addPropertyChangeListener("value", this);
			// jFormattedTextFieldOldWidth.setInputVerifier(new IntNumberVerifier());
			jFormattedTextFieldOldWidth.setEditable(false);
			// jFormattedTextFieldOldWidth.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
			jFormattedTextFieldOldWidth.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) jFormattedTextFieldOldWidth.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
												// ;
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
	private JPanel getJPanelOldHeight() {
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
			jPanelOldHeight.add(jFormattedTextFieldOldHeight,BorderLayout.CENTER);
			jFormattedTextFieldOldHeight.setEnabled(true);
		}
		return jPanelOldHeight;
	}
	/**
	 * This method initializes jJPanelOldsize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_OldSize() {
		if (jPanelOldSize == null) {
			jPanelOldSize = new JPanel();
			//jPanelOldSize.setLayout(new BoxLayout(jPanelOldSize, BoxLayout.X_AXIS));
			jPanelOldSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelOldSize.setBorder(new TitledBorder(null, "Original size", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelOldSize.add(this.getJPanelOldWidth());
			jPanelOldSize.add(this.getJPanelOldHeight());	
		}
		return jPanelOldSize;
	}
    //---------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelOffSetX
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_OffSetX() {
		if (jPanelOffSetX == null) {
			jPanelOffSetX = new JPanel();
			jPanelOffSetX.setLayout(new BorderLayout());
			jLabelOffSetX = new JLabel("X: ");
			//jLabelOffSetX.setPreferredSize(new Dimension(65, 20));
			jLabelOffSetX.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerOffSetX = new JSpinner(sModel);
			//jSpinnerOffSetX.setPreferredSize(new Dimension(60, 20));
			jSpinnerOffSetX.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerOffSetX.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelOffSetX.add(jLabelOffSetX, BorderLayout.WEST);
			jPanelOffSetX.add(jSpinnerOffSetX, BorderLayout.CENTER);
		}
		return jPanelOffSetX;
	}

	/**
	 * This method initializes jJPanelOffSetY
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_OffSetY() {
		if (jPanelOffSetY == null) {
			jPanelOffSetY = new JPanel();
			jPanelOffSetY.setLayout(new BorderLayout());
			jLabelOffSetY = new JLabel("Y: ");
			//jLabelOffSetY.setPreferredSize(new Dimension(65, 20));
			jLabelOffSetY.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerOffSetY = new JSpinner(sModel);
			//jSpinnerOffSetY.setPreferredSize(new Dimension(60, 20));
			jSpinnerOffSetY.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerOffSetY.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelOffSetY.add(jLabelOffSetY, BorderLayout.WEST);
			jPanelOffSetY.add(jSpinnerOffSetY, BorderLayout.CENTER);
		}
		return jPanelOffSetY;
	}
	/**
	 * This method initializes JPanel
	 * ----
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_OffSetXY() {
		if (jPanelOffSetXY== null) {
			jPanelOffSetXY = new JPanel();
			//jPanelOffSetXY.setLayout(new BoxLayout(jPanelOffSetXY, BoxLayout.Y_AXIS));
			jPanelOffSetXY.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			//jPanelOffSetXY.setBorder(new TitledBorder(null, "OffSetXY", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelOffSetXY.add(this.getJPanel_OffSetX());
			jPanelOffSetXY.add(this.getJPanel_OffSetY());
			// jPanelOffSetXY.addSeparator();
		}
		return jPanelOffSetXY;
	}
	//-------------------------------------------------------------------------------------------------

	private void setButtonGroupOffSet() {
		buttGroupOffSet = new ButtonGroup();
		buttGroupOffSet.add(buttCenter);
		buttGroupOffSet.add(buttLeftTop);
		buttGroupOffSet.add(buttLeftBottom);
		buttGroupOffSet.add(buttRightTop);
		buttGroupOffSet.add(buttRightBottom);
		buttGroupOffSet.add(buttUser);
	}
	
	private JPanel getJPanel_ButtOffset() {
		if (jPanel_ButtOffSet == null) {
		
			buttCenter = new JRadioButton();
			buttCenter.setText("Center");
			buttCenter.setToolTipText("crop center");
			buttCenter.addActionListener(this);
			buttCenter.setActionCommand("parameter");

			buttLeftTop = new JRadioButton();
			buttLeftTop.setText("Left Top");
			buttLeftTop.setToolTipText("crop left top");
			buttLeftTop.addActionListener(this);
			buttLeftTop.setActionCommand("parameter");

			buttLeftBottom = new JRadioButton();
			buttLeftBottom.setText("Left Bottom");
			buttLeftBottom.setToolTipText("crop left bottom");
			buttLeftBottom.addActionListener(this);
			buttLeftBottom.setActionCommand("parameter");

			buttRightTop = new JRadioButton();
			buttRightTop.setText("Right Top");
			buttRightTop.setToolTipText("crop right top");
			buttRightTop.addActionListener(this);
			buttRightTop.setActionCommand("parameter");

			buttRightBottom = new JRadioButton();
			buttRightBottom.setText("Right Bottom");
			buttRightBottom.setToolTipText("crop right bottom");
			buttRightBottom.addActionListener(this);
			buttRightBottom.setActionCommand("parameter");

			buttUser = new JRadioButton();
			buttUser.setText("User");
			buttUser.setToolTipText("Offset user values");
			buttUser.addActionListener(this);
			buttUser.setActionCommand("parameter");

			jPanel_ButtOffSet = new JPanel();
			jPanel_ButtOffSet.setLayout(new BoxLayout(jPanel_ButtOffSet, BoxLayout.Y_AXIS));
			//jPanelOffSet.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			//jPanel_ButtOffSet.setBorder(new TitledBorder(null, "title", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanel_ButtOffSet.add(buttCenter);
			jPanel_ButtOffSet.add(buttLeftTop);
			jPanel_ButtOffSet.add(buttLeftBottom);
			jPanel_ButtOffSet.add(buttRightTop);
			jPanel_ButtOffSet.add(buttRightBottom);
			jPanel_ButtOffSet.add(buttUser);

			setButtonGroupOffSet();

		}
		return jPanel_ButtOffSet;
	}
	//------------------------------------------------------------------------------------------
	/**
	 * This method initializes JPanel
	 * ----
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_OffSet() {
		if (jPanelOffSet== null) {
			jPanelOffSet = new JPanel();
			jPanelOffSet.setLayout(new BoxLayout(jPanelOffSet, BoxLayout.Y_AXIS));
			//jPanelOffSet.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelOffSet.setBorder(new TitledBorder(null, "OffSet", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelOffSet.add(this.getJPanel_ButtOffset());
			jPanelOffSet.add(this.getJPanel_OffSetXY());
			// jPanelOffSet.addSeparator();
		}
		return jPanelOffSet;
	}
	//------------------------------------------------------------------
	/**
	 * This method initializes jJPanelNewWidth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNewWidth() {
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
			jFormattedTextFieldNewWidth = ftf;
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
	private JPanel getJPanelNewHeight() {
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
			jFormattedTextFieldNewHeight = ftf;
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
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
	private JPanel getJPanel_NewSize() {
		if (jPanelNewSize == null) {
			jPanelNewSize = new JPanel();
			//jPanelNewSize.setLayout(new BoxLayout(jPanelNewSize, BoxLayout.X_AXIS));
			jPanelNewSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelNewSize.setBorder(new TitledBorder(null, "New size", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelNewSize.add(this.getJPanelNewWidth());
			jPanelNewSize.add(this.getJPanelNewHeight());	
		}
		return jPanelNewSize;
	}
	
	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {

			if (buttManual == e.getSource()) {
				
				buttCenter.setEnabled(true);
				buttLeftTop.setEnabled(true);
				buttLeftBottom.setEnabled(true);
				buttRightTop.setEnabled(true);
				buttRightBottom.setEnabled(true);
				buttUser.setEnabled(true);
				//buttUser.setSelected(true);
				
				// jPanelOffSetX.setEnabled(true);
				// jPanelOffSetY.setEnabled(true);
				jSpinnerOffSetX.setEnabled(true);
				jSpinnerOffSetY.setEnabled(true);
				jSpinnerNewWidth.setEnabled(true);
				jSpinnerNewHeight.setEnabled(true);
				jFormattedTextFieldNewWidth.setEditable(true);
				jFormattedTextFieldNewHeight.setEditable(true);
			}
			if (buttROI == e.getSource()) {
				ROIShape rs = Application.getLook().getCurrentLookPanel().getCurrentROILayer().getCurrentROIShape();
				if (rs == null) {
					buttManual.setSelected(true);
					BoardPanel.appendTextln("No ROI defined, using manual settings.", caller);
					return;
				}
				
				buttCenter.setEnabled(false);
				buttLeftTop.setEnabled(false);
				buttLeftBottom.setEnabled(false);
				buttRightTop.setEnabled(false);
				buttRightBottom.setEnabled(false);
				buttUser.setEnabled(false);
				buttUser.setSelected(true);
				// jPanelOffSetX.setEnabled(false);
				// jPanelOffSetY.setEnabled(false);
				jSpinnerOffSetX.setEnabled(false);
				jSpinnerOffSetY.setEnabled(false);
				jSpinnerNewWidth.setEnabled(false);
				jSpinnerNewHeight.setEnabled(false);
				jFormattedTextFieldNewWidth.setEditable(false);
				jFormattedTextFieldNewHeight.setEditable(false);

				PlanarImage pi = ((IqmDataBox) this.pb.getSources().firstElement()).getImage();
				int height = pi.getHeight();
				Rectangle bounds = rs.getBounds();
				jSpinnerOffSetX.setValue(bounds.x);
				jSpinnerOffSetY.setValue(height - bounds.y - bounds.height);
				jFormattedTextFieldNewWidth.setValue(bounds.width);
				jFormattedTextFieldNewHeight.setValue(bounds.height);
			}

			if (buttManual.isSelected()) {
				
				if (buttCenter.isSelected()){
					int oldWidth  = ((Number) jFormattedTextFieldOldWidth.getValue()).intValue();
					int oldHeight = ((Number) jFormattedTextFieldOldHeight.getValue()).intValue();
					int newWidth  = ((Number) jFormattedTextFieldNewWidth.getValue()).intValue();
					int newHeight = ((Number) jFormattedTextFieldNewHeight.getValue()).intValue();
					jSpinnerOffSetX.setValue((oldWidth-newWidth)/2);
					jSpinnerOffSetY.setValue((oldHeight-newHeight)/2);
					jSpinnerOffSetX.setEnabled(false);
					jSpinnerOffSetY.setEnabled(false);
				}
				if (buttLeftTop.isSelected()){
					jSpinnerOffSetX.setValue(0);
					jSpinnerOffSetY.setValue(0);
					jSpinnerOffSetX.setEnabled(false);
					jSpinnerOffSetY.setEnabled(false);
				}
				if (buttLeftBottom.isSelected()){
					int oldHeight = ((Number) jFormattedTextFieldOldHeight.getValue()).intValue();
					int newHeight = ((Number) jFormattedTextFieldNewHeight.getValue()).intValue();
					jSpinnerOffSetX.setValue(0);
					jSpinnerOffSetY.setValue(oldHeight-newHeight);
					jSpinnerOffSetX.setEnabled(false);
					jSpinnerOffSetY.setEnabled(false);
				}
				if (buttRightTop.isSelected()){
					int oldWidth  = ((Number) jFormattedTextFieldOldWidth.getValue()).intValue();
					int newWidth  = ((Number) jFormattedTextFieldNewWidth.getValue()).intValue();
					jSpinnerOffSetX.setValue(oldWidth-newWidth);
					jSpinnerOffSetY.setValue(0);
					jSpinnerOffSetX.setEnabled(false);
					jSpinnerOffSetY.setEnabled(false);
				}
				if (buttRightBottom.isSelected()){
					int oldWidth  = ((Number) jFormattedTextFieldOldWidth.getValue()).intValue();
					int oldHeight = ((Number) jFormattedTextFieldOldHeight.getValue()).intValue();
					int newWidth  = ((Number) jFormattedTextFieldNewWidth.getValue()).intValue();
					int newHeight = ((Number) jFormattedTextFieldNewHeight.getValue()).intValue();
					jSpinnerOffSetX.setValue(oldWidth-newWidth);
					jSpinnerOffSetY.setValue(oldHeight-newHeight);
					jSpinnerOffSetX.setEnabled(false);
					jSpinnerOffSetY.setEnabled(false);
				}
				if (buttUser.isSelected()){	
					//System.out.println("OperatorGUI_Crop:  buttUser.isSelected() && buttManual.isSelected()");
					jSpinnerOffSetX.setEnabled(true);
					jSpinnerOffSetY.setEnabled(true);
				}
			
			}
			this.updateParameterBlock();
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
		this.update();
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

		int offSetX   = ((Number) jSpinnerOffSetX.getValue()).intValue();
		int offSetY   = ((Number) jSpinnerOffSetY.getValue()).intValue();
		int newWidth  = ((Number) jSpinnerNewWidth.getValue()).intValue();
		int newHeight = ((Number) jSpinnerNewHeight.getValue()).intValue();
		int oldWidth  = ((Number) jFormattedTextFieldOldWidth.getValue()).intValue();
		int oldHeight = ((Number) jFormattedTextFieldOldHeight.getValue()).intValue();

		if (jSpinnerOffSetX == e.getSource()) {
			if (offSetX + newWidth > oldWidth)
				jSpinnerNewWidth.setValue(oldWidth - offSetX);
		}
		if (jSpinnerOffSetY == e.getSource()) {
			if (offSetY + newHeight > oldHeight)
				jSpinnerNewHeight.setValue(oldHeight - offSetY);
		}
		if (jSpinnerNewWidth == e.getSource()) {
			if (newWidth > oldWidth) {
				jSpinnerNewWidth.setValue(oldWidth);
				newWidth = oldWidth;
			}
			if (offSetX + newWidth > oldWidth)
				jSpinnerOffSetX.setValue(oldWidth - newWidth);
		}
		if (jSpinnerNewHeight == e.getSource()) {
			if (newHeight > oldHeight) {
				jSpinnerNewHeight.setValue(oldHeight);
				newHeight = oldHeight;
			}
			if (offSetY + newHeight > oldHeight)
				jSpinnerOffSetY.setValue(oldHeight - newHeight);
		}
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	
}// END
