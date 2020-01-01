package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_StatRegMerge.java
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

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
import at.mug.iqm.img.bundle.descriptors.IqmOpStatRegMergeDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 05
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 * 
 */
public class OperatorGUI_StatRegMerge extends AbstractImageOperatorGUI
		implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3839281484081385359L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_StatRegMerge.class);

	private ParameterBlockIQM pb = null; // @jve:decl-index=0:

	private int nQ; // k

	private JPanel     jSpinnerPanelQ = null;
	private JSpinner   jSpinnerQ      = null;
	private JLabel     jLabelQ;

	private JPanel       jPanelMethod       = null;
	private ButtonGroup  buttGroupMethodRGB = null;
	private JRadioButton buttOne            = null;
	private JRadioButton buttTwo            = null;
	private JRadioButton buttAll            = null;
	private JRadioButton buttRed            = null;
	private JRadioButton buttGreen          = null;
	private JRadioButton buttBlue           = null;

	private JRadioButton buttIndexGV        = null;
	private JRadioButton buttMeanGV         = null;
	private JPanel       jPanelOutCol       = null;
	private ButtonGroup  buttGroupOutCol    = null;

	/**
	 * constructor
	 */
	public OperatorGUI_StatRegMerge() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpStatRegMergeDescriptor().getName());
		this.initialize();
		this.setTitle("Statistical Region Merging");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJSpinnerPanelQ(),  getGridBagConstraintsQ());
		this.getOpGUIContent().add(getJPanelMethod(),    getGridBagConstraintsMethod());
		this.getOpGUIContent().add(getJPanelOutCol(),    getGridBagConstraintsButtonGroupOutCol());

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		pb.setParameter("nQ", nQ);

		if (buttOne.isSelected())   pb.setParameter("MethodRGB", 0);
		if (buttTwo.isSelected())   pb.setParameter("MethodRGB", 1);
		if (buttAll.isSelected())   pb.setParameter("MethodRGB", 2);
		if (buttRed.isSelected())   pb.setParameter("MethodRGB", 3);
		if (buttGreen.isSelected()) pb.setParameter("MethodRGB", 4);
		if (buttBlue.isSelected())  pb.setParameter("MethodRGB", 5);

		if (buttIndexGV.isSelected()) pb.setParameter("Out", 0);
		if (buttMeanGV.isSelected())  pb.setParameter("Out", 1);

	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		nQ = pb.getIntParameter("nQ");
		jSpinnerQ.removeChangeListener(this);
		jSpinnerQ.setValue(nQ);
		jSpinnerQ.addChangeListener(this);
		//jLabelQ.setText("Q: " + nQ);

		if (pb.getIntParameter("MethodRGB") == 0) buttOne.setSelected(true);
		if (pb.getIntParameter("MethodRGB") == 1) buttTwo.setSelected(true);
		if (pb.getIntParameter("MethodRGB") == 2) buttAll.setSelected(true);
		if (pb.getIntParameter("MethodRGB") == 3) buttRed.setSelected(true);
		if (pb.getIntParameter("MethodRGB") == 4) buttGreen.setSelected(true);
		if (pb.getIntParameter("MethodRGB") == 5) buttBlue.setSelected(true);

		if (pb.getIntParameter("Out") == 0) buttIndexGV.setSelected(true);
		if (pb.getIntParameter("Out") == 1) buttMeanGV.setSelected(true);

		this.update();
	}

	private GridBagConstraints getGridBagConstraintsQ() {
		GridBagConstraints gridBagConstraintsQ = new GridBagConstraints();
		gridBagConstraintsQ.gridx = 0;
		gridBagConstraintsQ.gridy = 0;
		gridBagConstraintsQ.gridwidth = 1;
		gridBagConstraintsQ.insets = new Insets(10, 0, 0, 0); // top, left / bottom right
		gridBagConstraintsQ.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsQ;
	}

	private GridBagConstraints getGridBagConstraintsMethod() {
		GridBagConstraints gridBagConstraintsMethodRGB = new GridBagConstraints();
		gridBagConstraintsMethodRGB.gridx = 0;
		gridBagConstraintsMethodRGB.gridy = 1;
		gridBagConstraintsMethodRGB.gridwidth = 3;// ?
		gridBagConstraintsMethodRGB.insets = new Insets(5, 0, 0, 0); // top  left  bottom  right
		gridBagConstraintsMethodRGB.fill = GridBagConstraints.BOTH;
		//gridBagConstraintsMethodRGB.anchor = GridBagConstraints.WEST;
		return gridBagConstraintsMethodRGB;
	}

	private GridBagConstraints getGridBagConstraintsButtonGroupOutCol() {
		GridBagConstraints gridBagConstraintsButtonGroupOutCol = new GridBagConstraints();
		gridBagConstraintsButtonGroupOutCol.gridx = 0;
		gridBagConstraintsButtonGroupOutCol.gridy = 2;
		gridBagConstraintsButtonGroupOutCol.gridwidth = 1;
		gridBagConstraintsButtonGroupOutCol.insets = new Insets(5, 0, 5, 0); // top,  left  bottom  right
		gridBagConstraintsButtonGroupOutCol.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsButtonGroupOutCol;
	}

	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		PlanarImage pi = ((IqmDataBox) this.workPackage.getSources() .get(0)).getImage();
		int numBands = pi.getNumBands();

		if (numBands == 1) { // Grey
			buttOne.setEnabled(true);
			buttTwo.setEnabled(false);
			buttAll.setEnabled(false);
			buttRed.setEnabled(false);
			buttGreen.setEnabled(false);
			buttBlue.setEnabled(false);
		} else {
			buttOne.setEnabled(true);
			buttTwo.setEnabled(true);
			buttAll.setEnabled(true);
			buttRed.setEnabled(true);
			buttGreen.setEnabled(true);
			buttBlue.setEnabled(true);
		}
	}

	/**
	 * This method initializes jJPanelQ
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelQ() {
		if (jSpinnerPanelQ == null) {
			jSpinnerPanelQ = new JPanel();
			//jSpinnerPanelQ.setLayout(new BorderLayout());
			//jSpinnerPanelQ.setLayout(new BoxLayout(jSpinnerPanelQ, BoxLayout.Y_AXIS));
			jSpinnerPanelQ.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jSpinnerPanelQ.setBorder(new TitledBorder(null, "Number of cluster centers", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jLabelQ = new JLabel("Q: ");
			// jLabelQ.setPreferredSize(new Dimension(70, 20));
			jLabelQ.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(2, 2, 100, 1); // init, min, max, step
			jSpinnerQ = new JSpinner(sModel);
			jSpinnerQ.setToolTipText("Number of cluster centers (maximum 100)");
			//jSpinnerQ.setPreferredSize(new Dimension(60, 20));
			jSpinnerQ.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerQ.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")									
			jSpinnerPanelQ.add(jLabelQ, BorderLayout.WEST);
			jSpinnerPanelQ.add(jSpinnerQ, BorderLayout.CENTER);
		}
		return jSpinnerPanelQ;
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: One
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonOne() {
		if (buttOne == null) {
			buttOne = new JRadioButton();
			buttOne.setText("at least one channel");
			buttOne.setToolTipText("At least one channel, which one does not matter");
			buttOne.addActionListener(this);
			buttOne.setActionCommand("parameter");
			buttOne.setEnabled(true);
		}
		return buttOne;
	}

	/**
	 * This method initializes the Option: Two
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonTwo() {
		if (buttTwo == null) {
			buttTwo = new JRadioButton();
			buttTwo.setText("at least two channels");
			buttTwo.setToolTipText("At least two channels, which ones does not matter");
			buttTwo.addActionListener(this);
			buttTwo.setActionCommand("parameter");
		}
		return buttTwo;
	}

	/**
	 * This method initializes the Option: All
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAll() {
		if (buttAll == null) {
			buttAll = new JRadioButton();
			buttAll.setText("all channels");
			buttAll.setToolTipText("All channels");
			buttAll.addActionListener(this);
			buttAll.setActionCommand("parameter");
		}
		return buttAll;
	}

	/**
	 * This method initializes the Option: Red
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonRed() {
		if (buttRed == null) {
			buttRed = new JRadioButton();
			buttRed.setText("red channel");
			buttRed.setToolTipText("Only the red channel");
			buttRed.addActionListener(this);
			buttRed.setActionCommand("parameter");
			buttRed.setEnabled(true);
		}
		return buttRed;
	}

	/**
	 * This method initializes the Option: Green
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonGreen() {
		if (buttGreen == null) {
			buttGreen = new JRadioButton();
			buttGreen.setText("green channel");
			buttGreen.setToolTipText("Only the green channel");
			buttGreen.addActionListener(this);
			buttGreen.setActionCommand("parameter");
			buttGreen.setEnabled(true);
		}
		return buttGreen;
	}

	/**
	 * This method initializes the Option: Blue
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonBlue() {
		if (buttBlue == null) {
			buttBlue = new JRadioButton();
			buttBlue.setText("blue channel");
			buttBlue.setToolTipText("Only the blue channel");
			buttBlue.addActionListener(this);
			buttBlue.setActionCommand("parameter");
			buttBlue.setEnabled(true);
		}
		return buttBlue;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		// if (jPanelMethod == null) {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethod.setBorder(new TitledBorder(null, "Merge predicate must be true for:", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethod.add(getJRadioButtonOne());
		jPanelMethod.add(getJRadioButtonTwo());
		jPanelMethod.add(getJRadioButtonAll());
		jPanelMethod.add(getJRadioButtonRed());
		jPanelMethod.add(getJRadioButtonGreen());
		jPanelMethod.add(getJRadioButtonBlue());

		// jPanelMethod.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		// }
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroupMethodRGB == null) {
		buttGroupMethodRGB = new ButtonGroup();
		buttGroupMethodRGB.add(buttOne);
		buttGroupMethodRGB.add(buttTwo);
		buttGroupMethodRGB.add(buttAll);
		buttGroupMethodRGB.add(buttRed);
		buttGroupMethodRGB.add(buttGreen);
		buttGroupMethodRGB.add(buttBlue);
	}

	// ------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: IndexGV
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtIndexGV() {
		// if (buttIndexGV == null) {
		buttIndexGV = new JRadioButton();
		buttIndexGV.setText("Index grey values");
		buttIndexGV .setToolTipText("indices of regions ares used as output values");
		buttIndexGV.addActionListener(this);
		buttIndexGV.setActionCommand("parameter");
		// }
		return buttIndexGV;
	}

	/**
	 * This method initializes the Option: equidistant colors
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtMeanGV() {
		// if (buttMeanGV == null) {
		buttMeanGV = new JRadioButton();
		buttMeanGV.setText("Mean grey values");
		buttMeanGV.setToolTipText("mean grey values are used as output values");
		buttMeanGV.addActionListener(this);
		buttMeanGV.setActionCommand("parameter");
		// }
		return buttMeanGV;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOutCol() {
		// if (jPanelOutCol == null) {
		jPanelOutCol = new JPanel();
		//jPanelOutCol.setLayout(new BoxLayout(jPanelOutCol, BoxLayout.Y_AXIS));
		jPanelOutCol.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelOutCol.setBorder(new TitledBorder(null, "Output color", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelOutCol.add(getJRadioButtonButtIndexGV());
		jPanelOutCol.add(getJRadioButtonButtMeanGV());

		this.setButtonGroupOutCol(); // Grouping of JRadioButtons
		// }
		return jPanelOutCol;
	}

	private void setButtonGroupOutCol() {
		buttGroupOutCol = new ButtonGroup();
		buttGroupOutCol.add(buttIndexGV);
		buttGroupOutCol.add(buttMeanGV);
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

	@Override
	public void stateChanged(ChangeEvent e) {
		Object obE = e.getSource();
		if (obE instanceof JSpinner) {
			jSpinnerQ.removeChangeListener(this);

			if (obE == jSpinnerQ) {
				nQ = (Integer) jSpinnerQ.getValue();
				//jLabelQ.setText("Q: " + nQ);
			}

			jSpinnerQ.addChangeListener(this);

		}
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
		
	}
}// END
