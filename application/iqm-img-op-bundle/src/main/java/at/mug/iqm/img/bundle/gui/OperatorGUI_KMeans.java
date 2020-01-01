package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_KMeans.java
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


import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpKMeansDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 05
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_KMeans extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5935902012162517556L;
	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_KMeans.class);

	private ParameterBlockIQM pb = null; // @jve:decl-index=0:

	private int nK; // k
	private int itMax; // maximal iteration
	private double eps; // epsilon

	private JRadioButton buttEven         = null;
	private JRadioButton buttRandomSample = null;
	private JRadioButton buttRandom       = null;
	private JPanel       jPanelInit       = null;
	private ButtonGroup  buttGroupInit    = null;

	private JPanel   jSpinnerPanelK = null;
	private JSpinner jSpinnerK;
	private JLabel   jLabelK;

	private JPanel   jSpinnerPanelItMax = null;
	private JSpinner jSpinnerItMax;
	private JLabel   jLabelItMax;

	private JPanel   jSliderPanelEps   = null;
	private JSlider  jSliderEps;
	private JLabel   jLabelEps;
	
	private JPanel  jPanelContr;

	private JRadioButton buttOrigCol     = null;
	private JRadioButton buttEquiCol     = null;
	private JPanel       jPanelOutCol    = null;
	private ButtonGroup  buttGroupOutCol = null;
	

	/**
	 * constructor
	 */
	public OperatorGUI_KMeans() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpKMeansDescriptor().getName());
		this.initialize();
		this.setTitle("k-Means Clustering");
		this.getOpGUIContent().setLayout(new GridBagLayout());
		
		GridBagConstraints gbc_PanelInit = new GridBagConstraints();
		gbc_PanelInit.fill = GridBagConstraints.BOTH;
		gbc_PanelInit.insets = new Insets(10, 0, 0, 0);
		gbc_PanelInit.gridx = 0;
		gbc_PanelInit.gridy = 0;
		getOpGUIContent().add(this.getJPanelInit(), gbc_PanelInit);
		
		GridBagConstraints gbc_ContrPanel = new GridBagConstraints();
		gbc_ContrPanel.fill = GridBagConstraints.VERTICAL;
		gbc_ContrPanel.insets = new Insets(5, 0, 0, 0);
		gbc_ContrPanel.gridx = 0;
		gbc_ContrPanel.gridy = 1;
		getOpGUIContent().add(getJPanelContr(), gbc_ContrPanel);
		
		GridBagConstraints gbc_panel_OutColor = new GridBagConstraints();
		gbc_panel_OutColor.insets = new Insets(5, 0, 5, 0);
		gbc_panel_OutColor.fill = GridBagConstraints.BOTH;
		gbc_panel_OutColor.gridx = 0;
		gbc_panel_OutColor.gridy = 2;
		getOpGUIContent().add(getJPanelOutCol(), gbc_panel_OutColor);

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttEven.isSelected())         pb.setParameter("Init", 0);
		if (buttRandomSample.isSelected()) pb.setParameter("Init", 1);
		if (buttRandom.isSelected())       pb.setParameter("Init", 2);

		pb.setParameter("nK", nK);
		pb.setParameter("ItMax", itMax);
		pb.setParameter("Eps", eps);

		if (buttOrigCol.isSelected()) pb.setParameter("Out", 0);
		if (buttEquiCol.isSelected()) pb.setParameter("Out", 1);
	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Init") == 0) buttEven.setSelected(true);
		if (pb.getIntParameter("Init") == 1) buttRandomSample.setSelected(true);
		if (pb.getIntParameter("Init") == 2) buttRandom.setSelected(true);

		nK = pb.getIntParameter("nK");
		jSpinnerK.removeChangeListener(this);
		jSpinnerK.setValue(nK);
		jSpinnerK.addChangeListener(this);

		itMax = pb.getIntParameter("ItMax");
		jSpinnerItMax.removeChangeListener(this);
		jSpinnerItMax.setValue(itMax);
		jSpinnerItMax.addChangeListener(this);

		eps = pb.getDoubleParameter("Eps");
		jSliderEps.removeChangeListener(this);
		jSliderEps.setValue((int) (Math.log10(eps)));
		jSliderEps.addChangeListener(this);
		jLabelEps.setText("eps: " + eps);

		if (pb.getIntParameter("Out") == 0) buttOrigCol.setSelected(true);
		if (pb.getIntParameter("Out") == 1) buttEquiCol.setSelected(true);
	}

	// ------------------------------------------------------------------------------------------------------------------
	/**
	 * This method updates the GUI if needed This method overrides IqmOpJFrame
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}


	/**
	 * This method initializes the Option: Even
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtEven() {
		if (buttEven == null) {
			buttEven = new JRadioButton();
			buttEven.setText("Equidistant");
			buttEven.setToolTipText("evenly distributed initial values");
			buttEven.addActionListener(this);
			buttEven.setActionCommand("parameter");
		}
		return buttEven;
	}

	/**
	 * This method initializes the Option: Random Sample
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtRandomSample() {
		if (buttRandomSample == null) {
			buttRandomSample = new JRadioButton();
			buttRandomSample.setText("Random Sample");
			buttRandomSample.setToolTipText("randomly sampled initial values");
			buttRandomSample.addActionListener(this);
			buttRandomSample.setActionCommand("parameter");
		}
		return buttRandomSample;
	}

	/**
	 * This method initializes the Option: Random
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtRandom() {
		if (buttRandom == null) {
			buttRandom = new JRadioButton();
			buttRandom.setText("Random");
			buttRandom.setToolTipText("random initial values");
			buttRandom.addActionListener(this);
			buttRandom.setActionCommand("parameter");
		}
		return buttRandom;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelInit() {
		if (jPanelInit == null) {
			jPanelInit = new JPanel();
			jPanelInit.setLayout(new BoxLayout(jPanelInit, BoxLayout.Y_AXIS));
			//jPanelInit.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelInit.setBorder(new TitledBorder(null, "Distribution of initial values", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelInit.add(getJRadioButtonButtEven());
			jPanelInit.add(getJRadioButtonButtRandomSample());
			jPanelInit.add(getJRadioButtonButtRandom());
			this.setButtonGroupInit(); // Grouping of JRadioButtons
		}
		return jPanelInit;
	}

	private void setButtonGroupInit() {
		// if (ButtonGroup buttGroupInit == null) {
		buttGroupInit = new ButtonGroup();
		buttGroupInit.add(buttEven);
		buttGroupInit.add(buttRandomSample);
		buttGroupInit.add(buttRandom);
	}
    //------------------------------------------------------------------------------
	/**
	 * This method initializes jSpinnerK
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getJSpinnerK() {
		if (jSpinnerPanelK == null) {
			jSpinnerPanelK = new JPanel();
			BorderLayout bl_jSpinnerPanelK = new BorderLayout();
			bl_jSpinnerPanelK.setHgap(5);
			jSpinnerPanelK.setLayout(bl_jSpinnerPanelK);
			jSpinnerK = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1));
			jSpinnerK.setToolTipText("number of cluster centers");
			jSpinnerK.addChangeListener(this);		
			jLabelK = new JLabel();
			jLabelK.setToolTipText("the number of clusters");
			jLabelK.setText("Number of clusters k:");
			jSpinnerPanelK.add(jLabelK, BorderLayout.CENTER);
			jSpinnerPanelK.add(jSpinnerK, BorderLayout.EAST);
		}
		return jSpinnerPanelK;
	}

	/**
	 * This method initializes jSpinnerItMax
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getJSpinnerItMax() {
		if (jSpinnerPanelItMax == null) {
			jSpinnerPanelItMax = new JPanel();
			BorderLayout bl_jSpinnerPanelItMax = new BorderLayout();
			bl_jSpinnerPanelItMax.setHgap(5);
			jSpinnerPanelItMax.setLayout(bl_jSpinnerPanelItMax);
			jSpinnerItMax = new JSpinner(new SpinnerNumberModel(20, 20, 1000, 10));
			jSpinnerItMax.setToolTipText("maximal number of iterations");
			jSpinnerItMax.addChangeListener(this);
			jLabelItMax = new JLabel();
			jLabelItMax.setText("Maximal iterations:");
			//jLabelItMax.setPreferredSize(new Dimension(75, 10));
			jSpinnerPanelItMax.add(jLabelItMax, BorderLayout.CENTER);
			jSpinnerPanelItMax.add(jSpinnerItMax, BorderLayout.EAST);
		}
		return jSpinnerPanelItMax;
	}

	/**
	 * This method initializes jSliderEps
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getJSliderEps() {
		if (jSliderPanelEps == null) {
			jSliderPanelEps = new JPanel();
			BorderLayout bl_jSpinnerPanelEps = new BorderLayout();
			bl_jSpinnerPanelEps.setHgap(5);
			jSliderPanelEps.setLayout(bl_jSpinnerPanelEps);
			jSliderEps = new JSlider(Adjustable.HORIZONTAL);
			jSliderEps.setSnapToTicks(true);
			jSliderEps.setMajorTickSpacing(1);
			jSliderEps.setMinimum(-6);
			jSliderEps.setMaximum(3);
			jSliderEps.setValue(-6);
			jSliderEps.setPaintTicks(true);
			jSliderEps.setToolTipText("accuracy");
			
			eps = Math.pow(10.d, (double) jSliderEps.getValue());
			
			jLabelEps = new JLabel();
			jLabelEps.setText("eps: " + eps);
			jLabelEps.setPreferredSize(new Dimension(75, 10));
			jSliderPanelEps.add(jLabelEps, BorderLayout.CENTER);
			jSliderPanelEps.add(jSliderEps, BorderLayout.EAST);
		}
		return jSliderPanelEps;
	}
	
	private JPanel getJPanelContr() {
		if (jPanelContr == null) {
			jPanelContr = new JPanel();
			jPanelContr.setLayout(new GridBagLayout());
			//contrPanel.setLayout(new BoxLayout(contrPanel, BoxLayout.Y_AXIS));
			//contrPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelContr.setBorder(new TitledBorder(null, "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			
			
			GridBagConstraints gbc_1= new GridBagConstraints();
			gbc_1.gridx = 0;
			gbc_1.gridy = 0;
			gbc_1.gridwidth = 1;
			gbc_1.insets = new Insets(5, 5, 0, 5); // top  left  bottom  right
			//gbc_1.fill = GridBagConstraints.BOTH;
			gbc_1.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_2 = new GridBagConstraints();
			gbc_2.gridx = 0;
			gbc_2.gridy = 1;
			gbc_2.gridwidth = 1;
			gbc_2.insets = new Insets(5, 5, 0, 5); // top  left  bottom  right
			//gbc_2.fill = GridBagConstraints.BOTH;
			gbc_2.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_3 = new GridBagConstraints();
			gbc_3.gridx = 0;
			gbc_3.gridy = 2;
			gbc_3.gridwidth = 1;
			gbc_3.insets = new Insets(5, 5, 5, 5); // top  left  bottom  right
			//gbc_3.fill = GridBagConstraints.BOTH;
			gbc_3.anchor = GridBagConstraints.EAST;
				
			jPanelContr.add(getJSpinnerK(),     gbc_1);
			jPanelContr.add(getJSpinnerItMax(), gbc_2);
			jPanelContr.add(getJSliderEps(),    gbc_3);
		}
		return jPanelContr;
	}


	/**
	 * This method initializes the Option: OrigCol
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtOrigCol() {
		if (buttOrigCol == null) {
			buttOrigCol = new JRadioButton();
			buttOrigCol.setText("Calculated clusters");
			buttOrigCol .setToolTipText("the calculated clusters ares used as output colors");
			buttOrigCol.addActionListener(this);
			buttOrigCol.setActionCommand("parameter");
		}
		return buttOrigCol;
	}

	/**
	 * This method initializes the Option: equidistant colors
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtEquiCol() {
		if (buttEquiCol == null) {
			buttEquiCol = new JRadioButton();
			buttEquiCol.setText("Equidistant grey values");
			buttEquiCol .setToolTipText("equidistant clusters are used as output grey values");
			buttEquiCol.addActionListener(this);
			buttEquiCol.setActionCommand("parameter");
		}
		return buttEquiCol;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOutCol() {
		if (jPanelOutCol == null) {
			jPanelOutCol = new JPanel();
			jPanelOutCol.setLayout(new BoxLayout(jPanelOutCol, BoxLayout.Y_AXIS));
			//jPanelOutCol.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelOutCol.setBorder(new TitledBorder(null, "Output color of clusters", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelOutCol.add(getJRadioButtonButtOrigCol());
			jPanelOutCol.add(getJRadioButtonButtEquiCol());
			this.setButtonGroupOutCol(); 
		}
		return jPanelOutCol;
	}

	private void setButtonGroupOutCol() {
		if (buttGroupOutCol == null) {
			buttGroupOutCol = new ButtonGroup();
			buttGroupOutCol.add(buttOrigCol);
			buttGroupOutCol.add(buttEquiCol);
		}
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("OperatorGUI_KMeans: event e: "
		// +e.getActionCommand());
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
		jSpinnerK.removeChangeListener(this);
		jSpinnerItMax.removeChangeListener(this);
		jSliderEps.removeChangeListener(this);
		// Spinners-----------------------------------------------------------------------------
		if (obE == jSpinnerK) {
			nK = ((Number) jSpinnerK.getValue()).intValue();
		}
		if (obE == jSpinnerItMax) {
			itMax = ((Number) jSpinnerItMax.getValue()).intValue();
		}
		if (obE == jSliderEps) {
			int exponent = jSliderEps.getValue();
			eps = Math.pow(10d, (double) exponent);
			logger.debug("jSpinnerEps.getValue()" + jSliderEps.getValue());
			// System.out.println("OperatorGUI_KMeans: eps " + eps );
			// if (jSpinnerEps.getValue() == 2) eps = Math.pow(10d, -5d);
			// //10e-5 is not properly displayed, why??
			// System.out.println("OperatorGUI_KMeans: eps " + eps );
			jLabelEps.setText("eps: " + eps);
		}
		jSpinnerK.addChangeListener(this);
		jSpinnerItMax.addChangeListener(this);
		jSliderEps.addChangeListener(this);

		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}


}// END
