package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Statistics.java
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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpStatisticsDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 06
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_Statistics extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8574040031822506518L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_Statistics.class);

	private ParameterBlockIQM pb = null;

	private JCheckBox    jCheckBoxBinary  = null;
	private JCheckBox    jCheckBoxOrder1  = null;
	private JCheckBox    jCheckBoxOrder2  = null;
	private JPanel       jPanelCheckBoxes = null;

	private JPanel       jPanelRange      = null; // Grey level Range
	private JLabel       jLabelRange      = null;
	private JLabel       jLabelValueRange = null;
	private JSpinner     jSpinnerRange    = null;
	
	private JPanel       jPanelDistance   = null;
	private JLabel       jLabelDistance   = null;
	private JSpinner     jSpinnerDistance = null;
	
	private JPanel       jPanel2ndOptions = null;
	private TitledBorder tb2ndOptions     = null;

	private JRadioButton buttVert           = null;
	private JRadioButton buttHoriz          = null;
	private JRadioButton buttPlus           = null;
	private JRadioButton buttX              = null;
	private JRadioButton buttAll            = null;
	private JPanel       jPanelDirection    = null;
	private ButtonGroup  buttGroupDirection = null;

	private JRadioButton buttIncl            = null;
	private JRadioButton buttExcl            = null;
	private JPanel       jPanelBackground    = null;
	private ButtonGroup  buttGroupBackground = null;
	

	/**
	 * constructor
	 */
	public OperatorGUI_Statistics() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpStatisticsDescriptor().getName());
		this.initialize();
		this.setTitle("Image Statistics");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelCheckBoxes(), getGridBagConstraints_CheckBoxes());
		this.getOpGUIContent().add(getJPanel2ndOptions(), getGridBagConstraints_2ndOptions());
		this.getOpGUIContent().add(getJPanelBackground(), getGridBagConstraints_Background());

		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_CheckBoxes() {
		GridBagConstraints gbc_CheckBoxes = new GridBagConstraints();
		gbc_CheckBoxes.gridx = 0;
		gbc_CheckBoxes.gridy = 0;
		gbc_CheckBoxes.anchor = GridBagConstraints.WEST;
		gbc_CheckBoxes.insets = new Insets(10, 0, 0, 0); // top left  bottom  right
		gbc_CheckBoxes.fill = GridBagConstraints.BOTH;
		return gbc_CheckBoxes;
	}

	private GridBagConstraints getGridBagConstraints_2ndOptions() {
		GridBagConstraints gbc_2ndOptions = new GridBagConstraints();
		gbc_2ndOptions.gridx = 0;
		gbc_2ndOptions.gridy = 1;
		gbc_2ndOptions.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		gbc_2ndOptions.fill = GridBagConstraints.BOTH;
		return gbc_2ndOptions;
	}

	private GridBagConstraints getGridBagConstraints_Background() {
		GridBagConstraints gbc_Background = new GridBagConstraints();
		gbc_Background.gridx = 0;
		gbc_Background.gridy = 2;
		//gbc_Background.gridwidth = 3;// ?
		gbc_Background.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gbc_Background.fill = GridBagConstraints.BOTH;
		return gbc_Background;
	}


	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (jCheckBoxBinary.isSelected())  pb.setParameter("Binary", 1);
		if (!jCheckBoxBinary.isSelected()) pb.setParameter("Binary", 0);
		if (jCheckBoxOrder1.isSelected())  pb.setParameter("Order1", 1);
		if (!jCheckBoxOrder1.isSelected()) pb.setParameter("Order1", 0);
		if (jCheckBoxOrder2.isSelected())  pb.setParameter("Order2", 1);
		if (!jCheckBoxOrder2.isSelected()) pb.setParameter("Order2", 0);

		pb.setParameter("Distance", ((Number) jSpinnerDistance.getValue()).intValue());
		pb.setParameter("Range",    ((Number) jSpinnerRange.getValue()).intValue());

		if (buttVert.isSelected())  pb.setParameter("Direction", 0);
		if (buttHoriz.isSelected()) pb.setParameter("Direction", 1);
		if (buttPlus.isSelected())  pb.setParameter("Direction", 2);
		if (buttX.isSelected())     pb.setParameter("Direction", 3);
		if (buttAll.isSelected())   pb.setParameter("Direction", 4);

		if (buttIncl.isSelected())  pb.setParameter("Background", 0);
		if (buttExcl.isSelected())  pb.setParameter("Background", 1);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Binary") == 0)  jCheckBoxBinary.setSelected(false);
		if (pb.getIntParameter("Binary") == 1)  jCheckBoxBinary.setSelected(true);
		if (pb.getIntParameter("Order1") == 0)  jCheckBoxOrder1.setSelected(false);
		if (pb.getIntParameter("Order1") == 1)  jCheckBoxOrder1.setSelected(true);
		if (pb.getIntParameter("Order2") == 0)  jCheckBoxOrder2.setSelected(false);
		if (pb.getIntParameter("Order2") == 1)  jCheckBoxOrder2.setSelected(true);

		jSpinnerDistance.removeChangeListener(this);
		jSpinnerDistance.setValue(pb.getIntParameter("Distance"));
		jSpinnerDistance.addChangeListener(this);

		jSpinnerRange.removeChangeListener(this);
		jSpinnerRange.setValue(pb.getIntParameter("Range"));
		jSpinnerRange.addChangeListener(this);

		if (pb.getIntParameter("Direction") == 0)  buttVert.setSelected(true);
		if (pb.getIntParameter("Direction") == 1)  buttHoriz.setSelected(true);
		if (pb.getIntParameter("Direction") == 2)  buttPlus.setSelected(true);
		if (pb.getIntParameter("Direction") == 3)  buttX.setSelected(true);
		if (pb.getIntParameter("Direction") == 4)  buttAll.setSelected(true);

		if (pb.getIntParameter("Background") == 0) buttIncl.setSelected(true);
		if (pb.getIntParameter("Background") == 1) buttExcl.setSelected(true);

		if (jCheckBoxOrder2.isSelected()) {
			setOrder2OptionsStatus(true);
		} else {
			setOrder2OptionsStatus(false);
		}
		int range = ((Number) jSpinnerRange.getValue()).intValue();
		int value = 1;
		for (int i = 1; i <= range; i++) {
			value = value * 2;
		}
		jLabelValueRange.setText("# Grey values: " + String.valueOf(value));
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * This method enables or disables the 2nd order options
	 */
	private void setOrder2OptionsStatus(boolean b) {
		if (b) {
			jLabelDistance.setEnabled(true);
			jSpinnerDistance.setEnabled(true);
			jLabelRange.setEnabled(true);
			jSpinnerRange.setEnabled(true);
			jLabelValueRange.setEnabled(true);
			jPanelDirection.setEnabled(true);
			buttVert.setEnabled(true);
			buttHoriz.setEnabled(true);
			buttPlus.setEnabled(true);
			buttX.setEnabled(true);
			buttAll.setEnabled(true);
			tb2ndOptions.setTitleColor(Color.BLACK);
			repaint();
		} else {
			jLabelDistance.setEnabled(false);
			jSpinnerDistance.setEnabled(false);
			jLabelRange.setEnabled(false);
			jSpinnerRange.setEnabled(false);
			jLabelValueRange.setEnabled(false);
			jPanelDirection.setEnabled(false);
			buttVert.setEnabled(false);
			buttHoriz.setEnabled(false);
			buttPlus.setEnabled(false);
			buttX.setEnabled(false);
			buttAll.setEnabled(false);
			tb2ndOptions.setTitleColor(Color.GRAY);
			repaint();
		}

	}

	
	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	// ------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxBinary
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxBinary() {
		if (jCheckBoxBinary == null) {
			jCheckBoxBinary = new JCheckBox();
			jCheckBoxBinary.setText("Binary");
			jCheckBoxBinary.setToolTipText("Calculates binary features such as total pixel number, number of pixels greater than 0, ......");
			jCheckBoxBinary.addActionListener(this);
			jCheckBoxBinary.setActionCommand("parameter");
		}
		return jCheckBoxBinary;
	}

	/**
	 * This method initializes jCheckBoxOrder1
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxOrder1() {
		if (jCheckBoxOrder1 == null) {
			jCheckBoxOrder1 = new JCheckBox();
			jCheckBoxOrder1.setText("1st Order");
			jCheckBoxOrder1.setToolTipText("Calculates 1st order features such as Min, Max, Mean, Energy, Entropy, Skewness, Kurtosis,....");
			jCheckBoxOrder1.addActionListener(this);
			jCheckBoxOrder1.setActionCommand("parameter");
		}
		return jCheckBoxOrder1;
	}

	/**
	 * This method initializes jCheckBoxOrder2
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxOrder2() {
		if (jCheckBoxOrder2 == null) {
			jCheckBoxOrder2 = new JCheckBox();
			jCheckBoxOrder2.setText("2nd Order");
			jCheckBoxOrder2.setToolTipText("Calculates 2nd order features using the co-occurance matrix");
			jCheckBoxOrder2.addActionListener(this);
			jCheckBoxOrder2.setActionCommand("parameter");
			jCheckBoxOrder2.setEnabled(true);
		}
		return jCheckBoxOrder2;
	}
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelCheckBoxes(){
		if (jPanelCheckBoxes == null){
			jPanelCheckBoxes = new JPanel();
			//jPanelCheckBoxes.setLayout(new BoxLayout(jPanelCheckBoxes, BoxLayout.Y_AXIS));
			jPanelCheckBoxes.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelCheckBoxes.setBorder(new TitledBorder(null, "CheckBoxes", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelCheckBoxes.add(getJCheckBoxBinary());
			jPanelCheckBoxes.add(getJCheckBoxOrder1());
			jPanelCheckBoxes.add(getJCheckBoxOrder2());
	
		}
		return jPanelCheckBoxes;
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelDistance
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDistance() {
		if (jPanelDistance == null) {
			jPanelDistance = new JPanel();
			jPanelDistance.setLayout(new BorderLayout());
			jLabelDistance = new JLabel("Distance: ");
			//jLabelDistance.setPreferredSize(new Dimension(75, 20));
			jLabelDistance.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerDistance = new JSpinner(sModel);
			//jSpinnerDistance.setPreferredSize(new Dimension(60, 20));
			jSpinnerDistance.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerDistance.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0") ;
			jPanelDistance.add(jLabelDistance, BorderLayout.WEST);
			jPanelDistance.add(jSpinnerDistance, BorderLayout.CENTER);
		}
		return jPanelDistance;
	}

	/**
	 * This method initializes jJPanelRange
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRange() {
		if (jPanelRange == null) {
			jPanelRange = new JPanel();
			jPanelRange.setLayout(new BorderLayout(0, 5));
			
			jLabelRange = new JLabel("Range: ");
			//jLabelRange.setPreferredSize(new Dimension(75, 20));
			//jLabelRange.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelValueRange = new JLabel("Value: ");
			//jLabelValueRange.setPreferredSize(new Dimension(75, 20));
			jLabelValueRange.setHorizontalAlignment(SwingConstants.LEFT);
			SpinnerModel sModel = new SpinnerNumberModel(8, 1, 8, 1); // init,  min,  max,  step  range=2^n  n=1,....,8
			jSpinnerRange = new JSpinner(sModel);
			//jSpinnerRange.setPreferredSize(new Dimension(60, 20));
			jSpinnerRange.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRange .getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter .getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")  ;
			jPanelRange.add(jLabelRange,      BorderLayout.WEST);
			jPanelRange.add(jSpinnerRange,    BorderLayout.CENTER);
			jPanelRange.add(jLabelValueRange, BorderLayout.SOUTH);
		}
		return jPanelRange;
	}

	// -------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: vertical Direction
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonbuttVert() {
		// if (buttVert == null) {
		buttVert = new JRadioButton();
		buttVert.setText("Vertical");
		buttVert.setToolTipText("uses the vertical pixels");
		buttVert.addActionListener(this);
		buttVert.setActionCommand("parameter");
		// }
		return buttVert;
	}

	/**
	 * This method initializes the Option: horizontal Direction
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonbuttHoriz() {
		// if (buttHoriz == null) {
		buttHoriz = new JRadioButton();
		buttHoriz.setText("Horizontal");
		buttHoriz.setToolTipText("uses the horizontal pixels");
		buttHoriz.addActionListener(this);
		buttHoriz.setActionCommand("parameter");
		// }
		return buttHoriz;
	}

	/**
	 * This method initializes the Option: Plus Direction
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonbuttPlus() {
		// if (buttPlus == null) {
		buttPlus = new JRadioButton();
		buttPlus.setText("+");
		buttPlus.setToolTipText("uses the vertical and horizontal pixels");
		buttPlus.addActionListener(this);
		buttPlus.setActionCommand("parameter");
		// }
		return buttPlus;
	}

	/**
	 * This method initializes the Option: X shaped Direction
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonbuttX() {
		// if (buttX == null) {
		buttX = new JRadioButton();
		buttX.setText("X shaped");
		buttX.setToolTipText("uses the diagonal pixels");
		buttX.addActionListener(this);
		buttX.setActionCommand("parameter");
		// }
		return buttX;
	}

	/**
	 * This method initializes the Option: all Directions
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonbuttAll() {
		// if (buttAll == null) {
		buttAll = new JRadioButton();
		buttAll.setText("All");
		buttAll.setToolTipText("uses all neighboring pixels");
		buttAll.addActionListener(this);
		buttAll.setActionCommand("parameter");
		// }
		return buttAll;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDirection() {
		// if (jPanelDirection== null) {
		jPanelDirection = new JPanel();
		jPanelDirection.setLayout(new BoxLayout(jPanelDirection, BoxLayout.Y_AXIS));
		//jPanelDirection.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelDirection.setBorder(new TitledBorder(null, "Direction", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelDirection.add(getJRadioButtonbuttVert());
		jPanelDirection.add(getJRadioButtonbuttHoriz());
		jPanelDirection.add(getJRadioButtonbuttPlus());
		jPanelDirection.add(getJRadioButtonbuttX());
		jPanelDirection.add(getJRadioButtonbuttAll());
		// jPanelDirection.addSeparator();
		this.setButtonGroupDirection(); // Grouping of JRadioButtons
		// }
		return jPanelDirection;
	}

	private void setButtonGroupDirection() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupDirection = new ButtonGroup();
		buttGroupDirection.add(buttVert);
		buttGroupDirection.add(buttHoriz);
		buttGroupDirection.add(buttPlus);
		buttGroupDirection.add(buttX);
		buttGroupDirection.add(buttAll);
	}
	//--------------------------------------------------------------------------------------------
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2ndOptions(){
		if (jPanel2ndOptions== null) {
			jPanel2ndOptions = new JPanel();
			jPanel2ndOptions.setLayout(new GridBagLayout());
			//jPanel2ndOptions.setLayout(new BoxLayout(jPanel2ndOptions, BoxLayout.Y_AXIS));
			//jPanel2ndOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			tb2ndOptions = new TitledBorder(null, "2nd Order options", TitledBorder.LEADING, TitledBorder.TOP, null, null);	
			jPanel2ndOptions.setBorder(tb2ndOptions);
			
			GridBagConstraints gbc_1= new GridBagConstraints();
			gbc_1.gridx = 0;
			gbc_1.gridy = 0;
			gbc_1.gridwidth = 1;
			gbc_1.insets = new Insets(20, 0, 0, 0); // top  left  bottom  right
			//gbc_1.fill = GridBagConstraints.BOTH;
			gbc_1.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_2 = new GridBagConstraints();
			gbc_2.gridx = 0;
			gbc_2.gridy = 1;
			gbc_2.gridwidth = 1;
			gbc_2.insets = new Insets(5, 0, 0, 0); // top  left  bottom  right
			//gbc_2.fill = GridBagConstraints.BOTH;
			gbc_2.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_3 = new GridBagConstraints();
			gbc_3.gridx = 1;
			gbc_3.gridy = 0;
			gbc_3.gridwidth = 1;
			gbc_3.gridheight = 2;
			gbc_3.insets = new Insets(5, 10, 0, 0); // top  left  bottom  right
			//gbc_3.fill = GridBagConstraints.BOTH;
			gbc_3.anchor = GridBagConstraints.EAST;
			
			jPanel2ndOptions.add(getJPanelRange(),     gbc_1);
			jPanel2ndOptions.add(getJPanelDistance(),  gbc_2);
			jPanel2ndOptions.add(getJPanelDirection(), gbc_3);
		
		}
		return jPanel2ndOptions;
	}
	
	

	//--------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonbuttIncl() {
		// if (buttIncl == null) {
		buttIncl = new JRadioButton();
		buttIncl.setText("Inclusive Background");
		buttIncl.setToolTipText("statistics of all pixel values");
		buttIncl.addActionListener(this);
		buttIncl.setActionCommand("parameter");
		// }
		return buttIncl;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonbuttExcl() {
		// if (buttExcl == null) {
		buttExcl = new JRadioButton();
		buttExcl.setText("Exclusive Background");
		buttExcl.setToolTipText("statistics without zero pixel values");
		buttExcl.addActionListener(this);
		buttExcl.setActionCommand("parameter");
		buttExcl.setEnabled(true);
		// }
		return buttExcl;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBackground() {
		// if (jPanelBackground== null) {
		jPanelBackground = new JPanel();
		jPanelBackground.setLayout(new BoxLayout(jPanelBackground, BoxLayout.Y_AXIS));
		//jPanelBackground.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelBackground.setBorder(new TitledBorder(null, "Calculation option", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelBackground.add(getJRadioButtonbuttIncl());
		jPanelBackground.add(getJRadioButtonbuttExcl());
		this.setButtonGroupBackground(); // Grouping of JRadioButtons
	
		// }
		return jPanelBackground;
	}

	private void setButtonGroupBackground() {
		buttGroupBackground = new ButtonGroup();
		buttGroupBackground.add(buttIncl);
		buttGroupBackground.add(buttExcl);
	}

	// ----------------------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (jCheckBoxOrder2 == e.getSource()) {
				if (jCheckBoxOrder2.isSelected()) {
					this.setOrder2OptionsStatus(true);
				} else {
					this.setOrder2OptionsStatus(false);
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

	@Override
	public void stateChanged(ChangeEvent e) {
		int range = ((Number) jSpinnerRange.getValue()).intValue();

		if (jSpinnerRange == e.getSource()) {
			int value = 1;
			for (int i = 1; i <= range; i++) {
				value = value * 2;
			}
			jLabelValueRange.setText("# Grey values: " + String.valueOf(value));
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
