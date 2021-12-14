package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_KMeansFuzzy.java
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

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
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

 
 

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.descriptors.IqmOpKMeansFuzzyDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 05
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class OperatorGUI_KMeansFuzzy extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5569861273634595903L;
	// class specific logger
	  

	private ParameterBlockIQM pb = null; // @jve:decl-index=0:

	private int    nK;    // k
	private int    itMax; // maximal iteration
	private double eps;   // epsilon
	private double fuzzy; // fuzziness
	private int    rank;  // rank < k

	private JRadioButton buttFCM          = null;
	private JRadioButton buttFLICM        = null;
	private JRadioButton buttMeth3        = null;
	private JPanel       jPanelMethod     = null;

	private JRadioButton buttEven         = null;
	private JRadioButton buttRandomSample = null;
	private JRadioButton buttRandom       = null;
	
	private JPanel     jSpinnerPanelK     = null;
	private JSpinner   jSpinnerK          = null;
	private JLabel     jLabelK            = null;

	private JPanel     jSpinnerPanelItMax = null;
	private JSpinner   jSpinnerItMax      = null;
	private JLabel     jLabelItMax        = null;

	private JPanel     jSpinnerPanelEps   = null;
	private JSpinner   jSpinnerEps        = null;
	private JLabel     jLabelEps          = null;

	private JPanel     jSpinnerPanelFuzzy = null;
	private JSpinner   jSpinnerFuzzy      = null;
	private JLabel     jLabelFuzzy        = null;

	private JPanel     jSpinnerPanelRank  = null;
	private JSpinner   jSpinnerRank       = null;
	private JLabel     jLabelRank         = null;
	
	private JPanel     jPanelSettings     = null;

	private JRadioButton buttOrigCol      = null;
	private JRadioButton buttEquiCol      = null;
	private JPanel       jPanelOutCol     = null;
	
	
	private final ButtonGroup buttonGroup    = new ButtonGroup();
	private final ButtonGroup buttonGroup_1  = new ButtonGroup();


	/**
	 * constructor
	 */
	public OperatorGUI_KMeansFuzzy() {
		System.out.println("IQM:  Now initializing...");

		this.setOpName(new IqmOpKMeansFuzzyDescriptor().getName());

		this.initialize();

		this.setTitle("Fuzzy KMeans Clustering");
		this.getOpGUIContent().setLayout(new GridBagLayout());
		
		this.getOpGUIContent().add(getJPanelMethod(),   getGridbagConstraints_Method());
		this.getOpGUIContent().add(getJPanelSettings(), getGridBagConstraints_Settings());
		this.getOpGUIContent().add(getJPanelOutCol(),   getGridBagConstraints_OutCol());
	
		this.pack();
	}
	
	private GridBagConstraints getGridbagConstraints_Method(){
		GridBagConstraints gbc_pnlMethod = new GridBagConstraints();
		gbc_pnlMethod.gridx = 0;
		gbc_pnlMethod.gridy = 0;
		gbc_pnlMethod.gridwidth = 1;
		gbc_pnlMethod.insets = new Insets(10, 0, 0, 0); // top left bottom right
		gbc_pnlMethod.fill = GridBagConstraints.BOTH;
		return gbc_pnlMethod;
	}
	
	private GridBagConstraints getGridBagConstraints_Settings() {
		GridBagConstraints gbc_Settings = new GridBagConstraints();
		gbc_Settings.gridx = 0;
		gbc_Settings.gridy = 1;
		gbc_Settings.gridwidth = 1;
		gbc_Settings.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_Settings.fill = GridBagConstraints.BOTH;
		return gbc_Settings;
	}


	private GridBagConstraints  getGridBagConstraints_OutCol(){
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		gbc_panel.gridwidth = 1;
		gbc_panel.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gbc_panel.fill = GridBagConstraints.BOTH;
		return gbc_panel;
	}
	
	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttFCM.isSelected())   pb.setParameter("Method", 0);
		if (buttFLICM.isSelected()) pb.setParameter("Method", 1);
		// if (buttEven.isSelected()) pbJAI.setParameter("Init", 0);
		// if (buttRandomSample.isSelected()) pbJAI.setParameter("Init", 1);
		// if (buttRandom.isSelected()) pbJAI.setParameter("Init", 2);
		pb.setParameter("nK",    nK);
		pb.setParameter("ItMax", itMax);
		pb.setParameter("Eps",   eps);
		pb.setParameter("Fuzzy", fuzzy);
		pb.setParameter("Rank",  rank);
		if (buttOrigCol.isSelected()) pb.setParameter("Out", 0);
		if (buttEquiCol.isSelected()) pb.setParameter("Out", 1);
	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("Method") == 0) buttFCM.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttFLICM.setSelected(true);
		if (pb.getIntParameter("Method") == 2) buttMeth3.setSelected(true);

		nK = pb.getIntParameter("nK");
		jSpinnerK.removeChangeListener(this);
		jSpinnerK.setValue(nK);
		jSpinnerK.addChangeListener(this);
		//jLabelK.setText("k: " + nK);

		itMax = pb.getIntParameter("ItMax");
		jSpinnerItMax.removeChangeListener(this);
		jSpinnerItMax.setValue(itMax);
		jSpinnerItMax.addChangeListener(this);
		//jLabelItMax.setText("itMax: " + itMax);

		eps = pb.getDoubleParameter("Eps");
		jSpinnerEps.removeChangeListener(this);
		jSpinnerEps.setValue((int) (Math.log10(eps) + 7d));
		jSpinnerEps.addChangeListener(this);
		//jLabelEps.setText("eps: " + eps);

		fuzzy = pb.getDoubleParameter("Fuzzy");
		jSpinnerFuzzy.removeChangeListener(this);
		jSpinnerFuzzy.setValue(fuzzy);
		jSpinnerFuzzy.addChangeListener(this);
		//jLabelFuzzy.setText("Fuzziness: " + fuzzy);

		rank = pb.getIntParameter("Rank");
		jSpinnerRank.removeChangeListener(this);
		jSpinnerRank.setValue(rank);
		jSpinnerRank.addChangeListener(this);
		//jLabelRank.setText("Rank: " + rank);

		if (pb.getIntParameter("Out") == 0) buttOrigCol.setSelected(true);
		if (pb.getIntParameter("Out") == 1) buttEquiCol.setSelected(true);
	}



	// ------------------------------------------------------------------------------------------------------------------
	/**
	 * This method updates the GUI if needed This method overrides OperatorGUI
	 */
	@Override
	public void update() {
		System.out.println("IQM:  Updating GUI...");
		// here, it does nothing
	}

	

	// -----------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: FCM
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtFCM() {
		if (buttFCM == null) {
			buttFCM = new JRadioButton();
			buttonGroup_1.add(buttFCM);
			buttFCM.setText("FCM");
			buttFCM.setToolTipText("fuzzy c-means clustering");
			buttFCM.addActionListener(this);
			buttFCM.setActionCommand("parameter");
		}
		return buttFCM;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtFLICM() {
		if (buttFLICM == null) {
			buttFLICM = new JRadioButton();
			buttonGroup_1.add(buttFLICM);
			buttFLICM.setText("FLICM");
			buttFLICM.setToolTipText("fuzzy local information c-means  clustering");
			buttFLICM.addActionListener(this);
			buttFLICM.setActionCommand("parameter");
		}
		return buttFLICM;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		if (jPanelMethod == null) {
			jPanelMethod = new JPanel();
			//jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
			jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelMethod.add(getJRadioButtonButtFCM());
			jPanelMethod.add(getJRadioButtonButtFLICM());
		}
		return jPanelMethod;
	}



	// Spinners------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelK
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelK() {
		if (jSpinnerPanelK == null) {
			jSpinnerPanelK = new JPanel();
			jSpinnerPanelK.setLayout(new BorderLayout());
			jLabelK = new JLabel("Number of clusters k: ");
			// jLabelK.setPreferredSize(new Dimension(70, 20));
			jLabelK.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(2, 2, 100, 1); // init, min, max, step
			jSpinnerK = new JSpinner(sModel);
			jSpinnerK.setToolTipText("number of cluster centers (maximum 100)");
			//jSpinnerK.setPreferredSize(new Dimension(60, 20));
			jSpinnerK.setEditor(new JSpinner.NumberEditor(jSpinnerK, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerK.getEditor()).getTextField().setEditable(false);
			jSpinnerK.addChangeListener(this);
			jSpinnerPanelK.add(jLabelK,   BorderLayout.WEST);
			jSpinnerPanelK.add(jSpinnerK, BorderLayout.CENTER);
		}
		return jSpinnerPanelK;
	}	
	
	/**
	 * This method initializes jJPanelItMax
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelItMax() {
		if (jSpinnerPanelItMax == null) {
			jSpinnerPanelItMax = new JPanel();
			jSpinnerPanelItMax.setLayout(new BorderLayout());
			jLabelItMax = new JLabel("Maximal iterations: ");
			// jLabelItMax.setPreferredSize(new Dimension(70, 20));
			jLabelItMax.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(20, 20, 1000, 1); // init, min, max, step
			jSpinnerItMax = new JSpinner(sModel);
			jSpinnerItMax.setToolTipText("Number of maximal iterations (maximum 1000)");
			//jSpinnerItMax.setPreferredSize(new Dimension(60, 20));
			jSpinnerItMax.setEditor(new JSpinner.NumberEditor(jSpinnerItMax, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerItMax.getEditor()).getTextField().setEditable(false);
			jSpinnerItMax.addChangeListener(this);		
			jSpinnerPanelItMax.add(jLabelItMax,   BorderLayout.WEST);
			jSpinnerPanelItMax.add(jSpinnerItMax, BorderLayout.CENTER);
		}
		return jSpinnerPanelItMax;
	}
	
	/**
	 * This method initializes jJPanelEps
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelEps() {
		if (jSpinnerPanelEps == null) {
			jSpinnerPanelEps = new JPanel();
			jSpinnerPanelEps.setLayout(new BorderLayout());
			jLabelEps = new JLabel("eps: ");
			// jLabelEps.setPreferredSize(new Dimension(70, 22));
			jLabelEps.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(6, 1, 100, 1); // init, min, max, step
			//// 1 bis 10 wird in 10hoch -6 bis 10 hoch+3 umgewandelt
			jSpinnerEps = new JSpinner(sModel);
			jSpinnerEps.setToolTipText("Accuracy");
			//jSpinnerEps.setPreferredSize(new Dimension(60, 24));
			jSpinnerEps.addChangeListener(this);
			jSpinnerEps.setEditor(new JSpinner.NumberEditor(jSpinnerEps, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerEps.getEditor()).getTextField().setEditable(false);
			
			//disable text field of spinner
			((JSpinner.NumberEditor) jSpinnerEps.getEditor()).getTextField().setVisible(false);
			((JSpinner.NumberEditor) jSpinnerEps.getEditor()).getTextField().setEnabled(false);		
			
			eps = Math.pow(10d, ((Integer)jSpinnerEps.getValue() - 7));
			jLabelEps.setText("Accuracy eps: " + eps);
			jSpinnerPanelEps.add(jLabelEps,   BorderLayout.WEST);
			jSpinnerPanelEps.add(jSpinnerEps, BorderLayout.CENTER);
		}
		return jSpinnerPanelEps;
	}	
	/**
	 * This method initializes jJPanelFuzzy
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelFuzzy() {
		if (jSpinnerPanelFuzzy == null) {
			jSpinnerPanelFuzzy = new JPanel();
			jSpinnerPanelFuzzy.setLayout(new BorderLayout());
			jLabelFuzzy = new JLabel("Fuzziness: ");
			// jLabelFuzzy.setPreferredSize(new Dimension(70, 22));
			jLabelFuzzy.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1.5, 1.0, 10.0, 0.1); // init, min, max, step
			jSpinnerFuzzy = new JSpinner(sModel);
			jSpinnerFuzzy.setToolTipText("Fuzziness (maximum 10.0)");
			//jSpinnerFuzzy.setPreferredSize(new Dimension(60, 24));
			jSpinnerFuzzy.addChangeListener(this);
			jSpinnerFuzzy.setEditor(new JSpinner.NumberEditor(jSpinnerFuzzy, "#0.00"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerFuzzy.getEditor()).getTextField().setEditable(false);
			jSpinnerPanelFuzzy.add(jLabelFuzzy,   BorderLayout.WEST);
			jSpinnerPanelFuzzy.add(jSpinnerFuzzy, BorderLayout.CENTER);
		}
		return jSpinnerPanelFuzzy;
	}	
	/**
	 * This method initializes jJPanelRank
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRank() {
		if (jSpinnerPanelRank == null) {
			jSpinnerPanelRank = new JPanel();
			jSpinnerPanelRank.setLayout(new BorderLayout());
			jLabelRank = new JLabel("Rank: ");
			// jLabelRank.setPreferredSize(new Dimension(70, 22));
			jLabelRank.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, 10, 1); // init, min, max, step
			jSpinnerRank = new JSpinner(sModel);
			jSpinnerRank.setToolTipText("Number of Rank < k (maximum 10)");
			//jSpinnerRank.setPreferredSize(new Dimension(60, 24));
			jSpinnerRank.addChangeListener(this);
			jSpinnerRank.setEditor(new JSpinner.NumberEditor(jSpinnerRank, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerRank.getEditor()).getTextField().setEditable(false);
			jSpinnerPanelRank.add(jLabelRank,   BorderLayout.WEST);
			jSpinnerPanelRank.add(jSpinnerRank, BorderLayout.CENTER);
		}
		return jSpinnerPanelRank;
	}
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSettings() {
		if (jPanelSettings == null) {
			jPanelSettings = new JPanel();
			jPanelSettings.setLayout(new GridBagLayout());
			//jPanelSettings.setLayout(new BoxLayout(jPanelSettings, BoxLayout.Y_AXIS));
			//jPanelSettings.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelSettings.setBorder(new TitledBorder(null, "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			
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
			gbc_3.insets = new Insets(5, 5, 0, 5); // top  left  bottom  right
			//gbc_3.fill = GridBagConstraints.BOTH;
			gbc_3.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_4 = new GridBagConstraints();
			gbc_4.gridx = 0;
			gbc_4.gridy = 3;
			gbc_4.gridwidth = 1;
			gbc_4.insets = new Insets(5, 5, 0, 5); // top  left  bottom  right
			//gbc_4.fill = GridBagConstraints.BOTH;
			gbc_4.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_5 = new GridBagConstraints();
			gbc_5.gridx = 0;
			gbc_5.gridy = 4;
			gbc_5.gridwidth = 1;
			gbc_5.insets = new Insets(5, 5, 5, 5); // top  left  bottom  right
			//gbc_5.fill = GridBagConstraints.BOTH;
			gbc_5.anchor = GridBagConstraints.EAST;
			
			
			jPanelSettings.add(getJPanelK(),     gbc_1);
			jPanelSettings.add(getJPanelItMax(), gbc_2);
			jPanelSettings.add(getJPanelEps(),   gbc_3);
			jPanelSettings.add(getJPanelFuzzy(), gbc_4);
			jPanelSettings.add(getJPanelRank(),  gbc_5);
		}
		return jPanelSettings;
	}
	
	//--------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: OrigCol
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtOrigCol() {
		if (buttOrigCol == null) {
			buttOrigCol = new JRadioButton();
			buttonGroup.add(buttOrigCol);
			buttOrigCol.setText("Calculated clusters");
			buttOrigCol.setToolTipText("the calculated clusters ares used as output colors");
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
			buttonGroup.add(buttEquiCol);
			buttEquiCol.setText("Equidistant grey values");
			buttEquiCol.setToolTipText("equidistant clusters are used as output grey values");
			buttEquiCol.addActionListener(this);
			buttEquiCol.setActionCommand("parameter");
		}
		return buttEquiCol;
	}

	/**
	 * This method initializes jJPanelBar
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
		}
		return jPanelOutCol;
	}
	
	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			System.out.println("IQM:  Performing AutoPreview");
			this.showPreview();
		}

	}
	@Override
	public void stateChanged(ChangeEvent e) {
		Object obE = e.getSource();
		if (obE instanceof JSpinner) {
			jSpinnerK.removeChangeListener(this);
			jSpinnerItMax.removeChangeListener(this);
			jSpinnerEps.removeChangeListener(this);
			jSpinnerFuzzy.removeChangeListener(this);
			jSpinnerRank.removeChangeListener(this);

			// Spinners-----------------------------------------------------------------------------
			if (obE == jSpinnerK) {
				nK = (Integer) jSpinnerK.getValue();
				//jLabelK.setText("k: " + nK);
				@SuppressWarnings("unused")
				int r = (Integer) jSpinnerRank.getValue(); // Rank must not be greater than k
				if (rank > nK) {
					rank = nK;
					jSpinnerRank.setValue(rank);
					//jLabelRank.setText("Rank: " + rank);
				}
			}
			if (obE == jSpinnerItMax) {
				itMax = (Integer) jSpinnerItMax.getValue();
				//jLabelItMax.setText("itMax: " + itMax);
			}
			if (obE == jSpinnerEps) {
				eps = Math.pow(10d, ((Integer)jSpinnerEps.getValue() - 7));
				System.out.println("IQM:  jSpinnerEps.getValue()" + jSpinnerEps.getValue());
				// System.out.println("OperatorGUI_KMeansFuzzy: eps " + eps );
				// if (jSpinnerEps.getValue() == 2) eps = Math.pow(10d, -5d);
				// //10 hoch -5 wird nicht richtig dargestellt??
				// System.out.println("OperatorGUI_KMeansFuzzy: eps " + eps );
				jLabelEps.setText("eps: " + eps);
			}
			if (obE == jSpinnerFuzzy) {
				fuzzy = (Double) jSpinnerFuzzy.getValue();
				//jLabelFuzzy.setText("Fuzziness: " + fuzzy);
			}
			if (obE == jSpinnerRank) {
				rank  = (Integer) jSpinnerRank.getValue();
				int k = (Integer) jSpinnerK.getValue(); // Rank must not be greater than k
				if (rank > k) {
					rank = k;
					//jSpinnerRank.setValue(rank);
				}
				//jLabelRank.setText("Rank: " + rank);
			}
			jSpinnerK.addChangeListener(this);
			jSpinnerItMax.addChangeListener(this);
			jSpinnerEps.addChangeListener(this);
			jSpinnerFuzzy.addChangeListener(this);
			jSpinnerRank.addChangeListener(this);

		}
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			System.out.println("IQM:  Performing AutoPreview");
			this.showPreview();
		}
		
	}
}// END
