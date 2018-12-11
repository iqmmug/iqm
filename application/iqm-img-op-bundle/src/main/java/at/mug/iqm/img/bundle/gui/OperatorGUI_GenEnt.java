package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_GenEnt.java
 * 
 * $Id: OperatorGUI_GenEnt.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/gui/OperatorGUI_GenEnt.java $
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.media.jai.PlanarImage;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpGenEntDescriptor;

/**
 * <li>Generalized Entropies
 * <li>according to a review of Amigó, J.M., Balogh, S.G., Hernández, S., 2018. A Brief Review of Generalized Entropies. Entropy 20, 813. https://doi.org/10.3390/e20110813
 * <li>and to: Tsallis Introduction to Nonextensive Statistical Mechanics, 2009, S105-106
 * 
 * @author Ahammer
 * @since  2018-12-04
 */

public class OperatorGUI_GenEnt extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 47978534483668000L;

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(OperatorGUI_GenEnt.class);

	private ParameterBlockIQM pb = null;

	private int eps = 2;
	private int minQ;
	private int maxQ;
	private int maxEps = Integer.MAX_VALUE;
	private double minEta;
	private double maxEta;
	private double minKappa;
	private double maxKappa;
	private double minB;
	private double maxB;
	private double minBeta;
	private double maxBeta;
	private double minGamma;
	private double maxGamma;
	
	private JCheckBox    chkBxRenyi    = null;
	private JCheckBox    chkBxTsallis  = null;
	private JCheckBox    chkBxH        = null;
	private JCheckBox    chkBxSE       = null;
	private JCheckBox    chkBxSEta     = null;
	private JCheckBox    chkBxSKappa   = null;
	private JCheckBox    chkBxSB       = null;
	private JCheckBox    chkBxSBeta    = null;
	private JCheckBox    chkBxSGamma   = null;
	private JPanel       jPanelEntropyType  = null;
	
	private JPanel   jPanelEps   = null;
	private JLabel   jLabelEps   = null;
	private JSpinner jSpinnerEps = null;

	private JPanel   jPanelMinQ   = null;
	private JLabel   jLabelMinQ   = null;
	private JSpinner jSpinnerMinQ = null;

	private JPanel   jPanelMaxQ   = null;
	private JLabel   jLabelMaxQ   = null;
	private JSpinner jSpinnerMaxQ = null;
	
	private JPanel   jPanelMinEta   = null;
	private JLabel   jLabelMinEta   = null;
	private JSpinner jSpinnerMinEta = null;
	
	private JPanel   jPanelMaxEta   = null;
	private JLabel   jLabelMaxEta   = null;
	private JSpinner jSpinnerMaxEta = null;

	private JPanel   jPanelMinKappa   = null;
	private JLabel   jLabelMinKappa   = null;
	private JSpinner jSpinnerMinKappa = null;
	
	private JPanel   jPanelMaxKappa   = null;
	private JLabel   jLabelMaxKappa   = null;
	private JSpinner jSpinnerMaxKappa = null;
	
	private JPanel   jPanelMinB   = null;
	private JLabel   jLabelMinB   = null;
	private JSpinner jSpinnerMinB = null;
	
	private JPanel   jPanelMaxB   = null;
	private JLabel   jLabelMaxB   = null;
	private JSpinner jSpinnerMaxB = null;
	
	private JPanel   jPanelMinBeta   = null;
	private JLabel   jLabelMinBeta   = null;
	private JSpinner jSpinnerMinBeta = null;
	
	private JPanel   jPanelMaxBeta   = null;
	private JLabel   jLabelMaxBeta   = null;
	private JSpinner jSpinnerMaxBeta = null;
	
	private JPanel   jPanelMinGamma   = null;
	private JLabel   jLabelMinGamma   = null;
	private JSpinner jSpinnerMinGamma = null;
	
	private JPanel   jPanelMaxGamma   = null;
	private JLabel   jLabelMaxGamma   = null;
	private JSpinner jSpinnerMaxGamma = null;
	
	private JPanel jPanelParamOption = null;
	
	private JRadioButton buttGlidingBox      = null;
	private JRadioButton buttRasterBox       = null;
	private JPanel       jPanelGridOption    = null;
	private ButtonGroup  buttGroupGridOption = null;
	
	
	/**
	 * constructor
	 */
	public OperatorGUI_GenEnt() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpGenEntDescriptor().getName());

		this.initialize();

		this.setTitle("Generalized Entropies");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.entropy.genent.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());
	
		this.getOpGUIContent().add(getJPanelEntropyTypes(), getGridBagConstraints_EntropyTypes());
		this.getOpGUIContent().add(getJPanelParamOption(),  getGridBagConstraints_ParamOption());
		this.getOpGUIContent().add(getJPanelGridOption(),   getGridBagConstraints_GridOption());
	
		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_EntropyTypes() {
		GridBagConstraints gbc_EntropyTypes = new GridBagConstraints();
		gbc_EntropyTypes.insets = new Insets(5, 0, 0, 0);
		gbc_EntropyTypes.fill = GridBagConstraints.BOTH;
		gbc_EntropyTypes.gridx = 0;
		gbc_EntropyTypes.gridy = 0;
		
		return gbc_EntropyTypes;
	}
	
	private GridBagConstraints getGridBagConstraints_ParamOption() {
		GridBagConstraints gbc_ParamOption = new GridBagConstraints();
		gbc_ParamOption.insets = new Insets(5, 0, 0, 0);
		gbc_ParamOption.fill = GridBagConstraints.BOTH;
		gbc_ParamOption.gridx = 0;
		gbc_ParamOption.gridy = 1;
		
		return gbc_ParamOption;
	}

	private GridBagConstraints getGridBagConstraints_GridOption(){
		GridBagConstraints gbc_GridOptionPanel = new GridBagConstraints();
		gbc_GridOptionPanel.fill = GridBagConstraints.BOTH;
		gbc_GridOptionPanel.insets = new Insets(5, 0, 0, 0);
		gbc_GridOptionPanel.gridx = 0;
		gbc_GridOptionPanel.gridy = 2;
	return gbc_GridOptionPanel;
    }
	
	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		if (chkBxRenyi.isSelected())    pb.setParameter("Renyi", 1);
		if (!chkBxRenyi.isSelected())   pb.setParameter("Renyi", 0);
		if (chkBxTsallis.isSelected())  pb.setParameter("Tsallis", 1);
		if (!chkBxTsallis.isSelected()) pb.setParameter("Tsallis", 0);
		if (chkBxH.isSelected())        pb.setParameter("H", 1);
		if (!chkBxH.isSelected())       pb.setParameter("H", 0);
		if (chkBxSE.isSelected())       pb.setParameter("SE", 1);
		if (!chkBxSE.isSelected())      pb.setParameter("SE", 0);
		if (chkBxSEta.isSelected())     pb.setParameter("SEta", 1);
		if (!chkBxSEta.isSelected())    pb.setParameter("SEta", 0);
		if (chkBxSKappa.isSelected())   pb.setParameter("SKappa", 1);
		if (!chkBxSKappa.isSelected())  pb.setParameter("SKappa", 0);
		if (chkBxSB.isSelected())       pb.setParameter("SB", 1);
		if (!chkBxSB.isSelected())      pb.setParameter("SB", 0);	
		if (chkBxSBeta.isSelected())    pb.setParameter("SBeta", 1);
		if (!chkBxSBeta.isSelected())   pb.setParameter("SBeta", 0);
		if (chkBxSGamma.isSelected())   pb.setParameter("SGamma", 1);
		if (!chkBxSGamma.isSelected())  pb.setParameter("SGamma", 0);
		
		pb.setParameter("Eps",    ((Number) jSpinnerEps .getValue()).intValue());	
		
		pb.setParameter("MinQ",   ((Number) jSpinnerMinQ.getValue()).intValue());
		pb.setParameter("MaxQ",   ((Number) jSpinnerMaxQ.getValue()).intValue());	
		pb.setParameter("MinEta",   ((Number) jSpinnerMinEta  .getValue()).doubleValue());
		pb.setParameter("MaxEta",   ((Number) jSpinnerMaxEta  .getValue()).doubleValue());
		pb.setParameter("MinKappa", ((Number) jSpinnerMinKappa.getValue()).doubleValue());
		pb.setParameter("MaxKappa", ((Number) jSpinnerMaxKappa.getValue()).doubleValue());
		pb.setParameter("MinB",     ((Number) jSpinnerMinB    .getValue()).doubleValue());
		pb.setParameter("MaxB",     ((Number) jSpinnerMaxB    .getValue()).doubleValue());
		pb.setParameter("MinBeta",  ((Number) jSpinnerMinBeta .getValue()).doubleValue());
		pb.setParameter("MaxBeta",  ((Number) jSpinnerMaxBeta .getValue()).doubleValue());
		pb.setParameter("MinGamma", ((Number) jSpinnerMinBeta .getValue()).doubleValue());
		pb.setParameter("MaxGamma", ((Number) jSpinnerMaxBeta .getValue()).doubleValue());
	
		if (buttGlidingBox.isSelected())  pb.setParameter("GridMethod", 0);
		if (buttRasterBox.isSelected())   pb.setParameter("GridMethod", 1);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();
			
		if (pb.getIntParameter("Renyi") == 0)   chkBxRenyi.setSelected(false);
		if (pb.getIntParameter("Renyi") == 1)   chkBxRenyi.setSelected(true);
		if (pb.getIntParameter("Tsallis") == 0) chkBxTsallis.setSelected(false);
		if (pb.getIntParameter("Tsallis") == 1) chkBxTsallis.setSelected(true);
		if (pb.getIntParameter("H") == 0)       chkBxH.setSelected(false);
		if (pb.getIntParameter("H") == 1)       chkBxH.setSelected(true);
		if (pb.getIntParameter("SE") == 0)      chkBxSE.setSelected(false);
		if (pb.getIntParameter("SE") == 1)      chkBxSE.setSelected(true);
		if (pb.getIntParameter("SEta") == 0)    chkBxSEta.setSelected(false);
		if (pb.getIntParameter("SEta") == 1)    chkBxSEta.setSelected(true);
		if (pb.getIntParameter("SKappa") == 0)  chkBxSKappa.setSelected(false);
		if (pb.getIntParameter("SKappa") == 1)  chkBxSKappa.setSelected(true);
		if (pb.getIntParameter("SB") == 0)      chkBxSB.setSelected(false);
		if (pb.getIntParameter("SB") == 1)      chkBxSB.setSelected(true);
		if (pb.getIntParameter("SBeta") == 0)   chkBxSBeta.setSelected(false);
		if (pb.getIntParameter("SBeta") == 1)   chkBxSBeta.setSelected(true);
		if (pb.getIntParameter("SGamma") == 0)  chkBxSGamma.setSelected(false);
		if (pb.getIntParameter("SGamma") == 1)  chkBxSGamma.setSelected(true);

		jSpinnerEps.removeChangeListener(this);
		jSpinnerEps.setValue(pb.getIntParameter("Eps"));
		jSpinnerEps.addChangeListener(this);
		
		jSpinnerMinQ.removeChangeListener(this);
		jSpinnerMinQ.setValue(pb.getIntParameter("MinQ"));
		jSpinnerMinQ.addChangeListener(this);

		jSpinnerMaxQ.removeChangeListener(this);
		jSpinnerMaxQ.setValue(pb.getIntParameter("MaxQ"));
		jSpinnerMaxQ.addChangeListener(this);
		
		jSpinnerMinEta.removeChangeListener(this);
		jSpinnerMinEta.setValue(pb.getDoubleParameter("MinEta"));
		jSpinnerMinEta.addChangeListener(this);
		
		jSpinnerMaxEta.removeChangeListener(this);
		jSpinnerMaxEta.setValue(pb.getDoubleParameter("MaxEta"));
		jSpinnerMaxEta.addChangeListener(this);
			
		jSpinnerMinKappa.removeChangeListener(this);
		jSpinnerMinKappa.setValue(pb.getDoubleParameter("MinKappa"));
		jSpinnerMinKappa.addChangeListener(this);
		
		
		jSpinnerMaxKappa.removeChangeListener(this);
		jSpinnerMaxKappa.setValue(pb.getDoubleParameter("MaxKappa"));
		jSpinnerMaxKappa.addChangeListener(this);
			
		jSpinnerMinB.removeChangeListener(this);
		jSpinnerMinB.setValue(pb.getDoubleParameter("MinB"));
		jSpinnerMinB.addChangeListener(this);
		
		jSpinnerMaxB.removeChangeListener(this);
		jSpinnerMaxB.setValue(pb.getDoubleParameter("MaxB"));
		jSpinnerMaxB.addChangeListener(this);
			
		jSpinnerMinBeta.removeChangeListener(this);
		jSpinnerMinBeta.setValue(pb.getDoubleParameter("MinBeta"));
		jSpinnerMinBeta.addChangeListener(this);
		
		jSpinnerMaxBeta.removeChangeListener(this);
		jSpinnerMaxBeta.setValue(pb.getDoubleParameter("MaxBeta"));
		jSpinnerMaxBeta.addChangeListener(this);
			
		jSpinnerMinGamma.removeChangeListener(this);
		jSpinnerMinGamma.setValue(pb.getDoubleParameter("MinGamma"));
		jSpinnerMinGamma.addChangeListener(this);
		
		jSpinnerMaxGamma.removeChangeListener(this);
		jSpinnerMaxGamma.setValue(pb.getDoubleParameter("MaxGamma"));
		jSpinnerMaxGamma.addChangeListener(this);

		if (pb.getIntParameter("GridMethod") == 0) buttGlidingBox.setSelected(true);
		if (pb.getIntParameter("GridMethod") == 1) buttRasterBox.setSelected(true);
	}

	// ---------------------------------------------------------------------------------------------
	private int getMaxEps(int width, int height) { // as normal Box counting
		int result = 0;
		if (buttGlidingBox.isSelected()) {
			result = (width + height) / 2;
		}
		if (buttRasterBox.isSelected()) {
			float boxWidth = 1f;
			int number = 1; // inclusive original image
			while ((boxWidth <= width) && (boxWidth <= height)) {
				boxWidth = boxWidth * 2;
				// System.out.println("OperatorGUI_FracBox: newBoxWidth: " + newBoxWidth);
				number = number + 1;
			}
			result = number - 1;
		}
		return result;
	}
	
	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method updates the GUI if needed This method overrides IqmOpJFrame
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		PlanarImage  pi = ((IqmDataBox) this.pb.getSources().firstElement()).getImage();
		int width  = pi.getWidth();
		int height = pi.getHeight();
		minQ = -5;
		maxQ = 5;
		maxEps = this.getMaxEps(width, height);

		
		
		SpinnerModel sModel = new SpinnerNumberModel(eps, 2, maxEps, 1); // init, min,  max, step
		jSpinnerEps.removeChangeListener(this);
		jSpinnerEps.setModel(sModel);
		DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerEps.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setEditable(true);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")// ;
		// jSpinnerNumMaxEps.setValue(maxEps);
		jSpinnerEps.addChangeListener(this);
		
		// System.out.println("OperatorGUI_GenEnt: numberMax: " +
		// numberMax);
		 sModel = new SpinnerNumberModel(minQ, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); // init, min,// max, step
		jSpinnerMinQ.removeChangeListener(this);
		jSpinnerMinQ.setModel(sModel);
		defEditor = (JSpinner.DefaultEditor) jSpinnerMinQ.getEditor();
		ftf = defEditor.getTextField();
		ftf.setEditable(true);
		intFormatter = (InternationalFormatter) ftf.getFormatter();
		decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")										// ;
		jSpinnerMinQ.setValue(minQ);
		jSpinnerMinQ.addChangeListener(this);

		sModel = new SpinnerNumberModel(maxQ, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); // init, min, max, step
		jSpinnerMaxQ.removeChangeListener(this);
		jSpinnerMaxQ.setModel(sModel);
		defEditor = (JSpinner.DefaultEditor) jSpinnerMaxQ.getEditor();
		ftf = defEditor.getTextField();
		ftf.setEditable(true);
		intFormatter = (InternationalFormatter) ftf.getFormatter();
		decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")// ;
		jSpinnerMaxQ.setValue(maxQ);
		jSpinnerMaxQ.addChangeListener(this);

	

		int init = 10;
		if (buttGlidingBox.isSelected()) {
			if (maxEps < 10)
				init = maxEps;
		}
		if (buttRasterBox.isSelected()) {
			init = maxEps;
		}
		
		this.updateParameterBlock();
	}
	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelEps
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPaneEps() {
		if (jPanelEps == null) {
			jPanelEps = new JPanel();
			jPanelEps.setLayout(new BorderLayout());
			jLabelEps = new JLabel("eps [pixel]: ");
			// jLabelEps.setPreferredSize(new Dimension(70, 20));
			jLabelEps.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 2, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerEps = new JSpinner(sModel);
			// jSpinnerEps = new JSpinner();
			jSpinnerEps.setPreferredSize(new Dimension(60, 20));
			jSpinnerEps.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerEps.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelEps.add(jLabelEps, BorderLayout.WEST);
			jPanelEps.add(jSpinnerEps, BorderLayout.CENTER);
		}
		return jPanelEps;
	}
	
	/**
	 * This method initializes jJPanelMinQ
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMinQ() {
		if (jPanelMinQ == null) {
			jPanelMinQ = new JPanel();
			jPanelMinQ.setLayout(new BorderLayout());
			jLabelMinQ = new JLabel("q:    min ");
			// jLabelMinQ.setPreferredSize(new Dimension(70, 20));
			jLabelMinQ.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(-5, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); // init, mi max, step
			jSpinnerMinQ = new JSpinner(sModel);
			// jSpinnerMinQ = new JSpinner();
			jSpinnerMinQ.setPreferredSize(new Dimension(60, 20));
			jSpinnerMinQ.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMinQ.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(10);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMinQ.add(jLabelMinQ, BorderLayout.WEST);
			jPanelMinQ.add(jSpinnerMinQ, BorderLayout.CENTER);
		}
		return jPanelMinQ;
	}
	/**
	 * This method initializes jJPanelMaxQ
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxQ() {
		if (jPanelMaxQ == null) {
			jPanelMaxQ = new JPanel();
			jPanelMaxQ.setLayout(new BorderLayout());
			jLabelMaxQ = new JLabel("max ");
			// jLabelMaxQ.setPreferredSize(new Dimension(70, 20));
			jLabelMaxQ.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(5, -Integer.MAX_VALUE,Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerMaxQ = new JSpinner(sModel);
			// jSpinnerMaxQ = new JSpinner();
			jSpinnerMaxQ.setPreferredSize(new Dimension(60, 20));
			jSpinnerMaxQ.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMaxQ.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(10);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMaxQ.add(jLabelMaxQ, BorderLayout.WEST);
			jPanelMaxQ.add(jSpinnerMaxQ, BorderLayout.CENTER);
		}
		return jPanelMaxQ;
	}	
	/**
	 * This method initializes jJPanelMinEta
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMinEta() {
		if (jPanelMinEta == null) {
			jPanelMinEta = new JPanel();
			jPanelMinEta.setLayout(new BorderLayout());
			jLabelMinEta = new JLabel("eta:    min ");
			//jLabelMinEta.setPreferredSize(new Dimension(70, 20));
			jLabelMinEta.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0.1, Double.MIN_VALUE, Double.MAX_VALUE, 0.1); // init, min, max, step
			jSpinnerMinEta = new JSpinner(sModel);
			//jSpinnerMinEta = new JSpinner();
			//jSpinnerMinEta.setPreferredSize(new Dimension(60, 20));
			jSpinnerMinEta.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMinEta.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")	
			jPanelMinEta.add(jLabelMinEta, BorderLayout.WEST);
			jPanelMinEta.add(jSpinnerMinEta, BorderLayout.CENTER);
		}
		return jPanelMinEta;
	}
	/**
	 * This method initializes jJPanelMaxEta
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxEta() {
		if (jPanelMaxEta == null) {
			jPanelMaxEta = new JPanel();
			jPanelMaxEta.setLayout(new BorderLayout());
			jLabelMaxEta = new JLabel("max ");
			//jLabelMaxEta.setPreferredSize(new Dimension(70, 20));
			jLabelMaxEta.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1.0, Double.MIN_VALUE, Double.MAX_VALUE, 0.1); // init, min, max, step
			jSpinnerMaxEta = new JSpinner(sModel);
			//jSpinnerMaxEta = new JSpinner();
			//jSpinnerMaxEta.setPreferredSize(new Dimension(60, 20));
			jSpinnerMaxEta.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMaxEta.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")	
			jPanelMaxEta.add(jLabelMaxEta, BorderLayout.WEST);
			jPanelMaxEta.add(jSpinnerMaxEta, BorderLayout.CENTER);
		}
		return jPanelMaxEta;
	}
	/**
	 * This method initializes jJPanelMinKappa
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMinKappa() {
		if (jPanelMinKappa == null) {
			jPanelMinKappa = new JPanel();
			jPanelMinKappa.setLayout(new BorderLayout());
			jLabelMinKappa = new JLabel("kappa:    min ");
			// jLabelMinKappa.setPreferredSize(new Dimension(70, 20));
			jLabelMinKappa.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0.0, 0.0, 1.0-Double.MIN_VALUE, 0.1); // init, min, max, step  //0.0f FOR MIN DOES NOT WORK! WHY?
			jSpinnerMinKappa = new JSpinner(sModel);
			// jSpinnerMinKappa = new JSpinner();
			//jSpinnerMinKappa.setPreferredSize(new Dimension(60, 20));
			jSpinnerMinKappa.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMinKappa.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMinKappa.add(jLabelMinKappa, BorderLayout.WEST);
			jPanelMinKappa.add(jSpinnerMinKappa, BorderLayout.CENTER);
		}
		return jPanelMinKappa;
	}
	/**
	 * This method initializes jJPanelMaxKappa
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxKappa() {
		if (jPanelMaxKappa == null) {
			jPanelMaxKappa = new JPanel();
			jPanelMaxKappa.setLayout(new BorderLayout());
			jLabelMaxKappa = new JLabel("max ");
			// jLabelMaxKappa.setPreferredSize(new Dimension(70, 20));
			jLabelMaxKappa.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0.9, Double.MIN_VALUE, 1.0-Double.MIN_VALUE, 0.1); // init, min, max, step
			jSpinnerMaxKappa = new JSpinner(sModel);
			// jSpinnerMaxKappa = new JSpinner();
			//jSpinnerMaxKappa.setPreferredSize(new Dimension(60, 20));
			jSpinnerMaxKappa.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMaxKappa.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMaxKappa.add(jLabelMaxKappa, BorderLayout.WEST);
			jPanelMaxKappa.add(jSpinnerMaxKappa, BorderLayout.CENTER);
		}
		return jPanelMaxKappa;
	}
	/**
	 * This method initializes jJPanelMinB
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMinB() {
		if (jPanelMinB == null) {
			jPanelMinB = new JPanel();
			jPanelMinB.setLayout(new BorderLayout());
			jLabelMinB = new JLabel("b:    min ");
			// jLabelMinB.setPreferredSize(new Dimension(70, 20));
			jLabelMinB.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1.0, Double.MIN_VALUE, Double.MAX_VALUE, 1.0); // init, min, max, step
			jSpinnerMinB = new JSpinner(sModel);
			// jSpinnerMinB = new JSpinner();
			//jSpinnerMinB.setPreferredSize(new Dimension(60, 20));
			jSpinnerMinB.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMinB.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMinB.add(jLabelMinB, BorderLayout.WEST);
			jPanelMinB.add(jSpinnerMinB, BorderLayout.CENTER);
		}
		return jPanelMinB;
	}
	/**
	 * This method initializes jJPanelMaxB
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxB() {
		if (jPanelMaxB == null) {
			jPanelMaxB = new JPanel();
			jPanelMaxB.setLayout(new BorderLayout());
			jLabelMaxB = new JLabel("max ");
			// jLabelMaxB.setPreferredSize(new Dimension(70, 20));
			jLabelMaxB.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10.0, 1.0, Double.MAX_VALUE, 1.0); // init, min, max, step
			jSpinnerMaxB = new JSpinner(sModel);
			// jSpinnerMaxB = new JSpinner();
			//jSpinnerMaxB.setPreferredSize(new Dimension(60, 20));
			jSpinnerMaxB.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMaxB.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMaxB.add(jLabelMaxB, BorderLayout.WEST);
			jPanelMaxB.add(jSpinnerMaxB, BorderLayout.CENTER);
		}
		return jPanelMaxB;
	}
	/**
	 * This method initializes jJPanelMinBeta
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMinBeta() {
		if (jPanelMinBeta == null) {
			jPanelMinBeta = new JPanel();
			jPanelMinBeta.setLayout(new BorderLayout());
			jLabelMinBeta = new JLabel("beta:    min ");
			// jLabelMinBeta.setPreferredSize(new Dimension(70, 20));
			jLabelMinBeta.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0.5, -Double.MAX_VALUE, Double.MAX_VALUE, 0.1); // init, min, max, step
			jSpinnerMinBeta = new JSpinner(sModel);
			// jSpinnerMinBeta = new JSpinner();
			//jSpinnerMinBeta.setPreferredSize(new Dimension(60, 20));
			jSpinnerMinBeta.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMinBeta.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMinBeta.add(jLabelMinBeta, BorderLayout.WEST);
			jPanelMinBeta.add(jSpinnerMinBeta, BorderLayout.CENTER);
		}
		return jPanelMinBeta;
	}
	
	/**
	 * This method initializes jJPanelMaxBeta
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxBeta() {
		if (jPanelMaxBeta == null) {
			jPanelMaxBeta = new JPanel();
			jPanelMaxBeta.setLayout(new BorderLayout());
			jLabelMaxBeta = new JLabel("max ");
			// jLabelMaxBeta.setPreferredSize(new Dimension(70, 20));
			jLabelMaxBeta.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1.5, -Double.MAX_VALUE, Double.MAX_VALUE, 0.1); // init, min, max, step
			jSpinnerMaxBeta = new JSpinner(sModel);
			// jSpinnerMaxBeta = new JSpinner();
			//jSpinnerMaxBeta.setPreferredSize(new Dimension(60, 20));
			jSpinnerMaxBeta.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMaxBeta.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMaxBeta.add(jLabelMaxBeta, BorderLayout.WEST);
			jPanelMaxBeta.add(jSpinnerMaxBeta, BorderLayout.CENTER);
		}
		return jPanelMaxBeta;
	}
	/**
	 * This method initializes jJPanelMinGamma
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMinGamma() {
		if (jPanelMinGamma == null) {
			jPanelMinGamma = new JPanel();
			jPanelMinGamma.setLayout(new BorderLayout());
			jLabelMinGamma = new JLabel("gamma:    min ");
			// jLabelMinGamma.setPreferredSize(new Dimension(70, 20));
			jLabelMinGamma.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0.1, 0.0, Double.MAX_VALUE, 0.1); // init, min, max, step
			jSpinnerMinGamma = new JSpinner(sModel);
			// jSpinnerMinGamma = new JSpinner();
			//jSpinnerMinGamma.setPreferredSize(new Dimension(60, 20));
			jSpinnerMinGamma.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMinGamma.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMinGamma.add(jLabelMinGamma, BorderLayout.WEST);
			jPanelMinGamma.add(jSpinnerMinGamma, BorderLayout.CENTER);
		}
		return jPanelMinGamma;
	}
	/**
	 * This method initializes jJPanelMaxGamma
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxGamma() {
		if (jPanelMaxGamma == null) {
			jPanelMaxGamma = new JPanel();
			jPanelMaxGamma.setLayout(new BorderLayout());
			jLabelMaxGamma = new JLabel("max ");
			// jLabelMaxGamma.setPreferredSize(new Dimension(70, 20));
			jLabelMaxGamma.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1.0, Double.MIN_VALUE, Double.MAX_VALUE, 0.1); // init, min, max, step
			jSpinnerMaxGamma = new JSpinner(sModel);
			// jSpinnerMaxGamma = new JSpinner();
			//jSpinnerMaxGamma.setPreferredSize(new Dimension(60, 20));
			jSpinnerMaxGamma.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMaxGamma.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMaxGamma.add(jLabelMaxGamma, BorderLayout.WEST);
			jPanelMaxGamma.add(jSpinnerMaxGamma, BorderLayout.CENTER);
		}
		return jPanelMaxGamma;
	}
	

	// -------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxRenyi() {
		if (chkBxRenyi == null) {
			chkBxRenyi = new JCheckBox();
			chkBxRenyi.setText("Renyi");
			chkBxRenyi.setToolTipText("Generalized Renyi entropies");
			chkBxRenyi.addActionListener(this);
			chkBxRenyi.setActionCommand("parameter");
			chkBxRenyi.setBorder( new EmptyBorder( 10, 0, 10, 0 ));  //top left bottom right		
		}
		return chkBxRenyi;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxTsallis() {
		if (chkBxTsallis == null) {
			chkBxTsallis = new JCheckBox();
			chkBxTsallis.setText("Tsallis");
			chkBxTsallis.setToolTipText("generalized Tsallis entropies");
			chkBxTsallis.addActionListener(this);
			chkBxTsallis.setActionCommand("parameter");
			chkBxTsallis.setEnabled(true);
			
		}
		return chkBxTsallis;
	}
	
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxH() {
		if (chkBxH == null) {
			chkBxH = new JCheckBox();
			chkBxH.setText("H");
			chkBxH.setToolTipText("generalized graph entropies");
			chkBxH.addActionListener(this);
			chkBxH.setActionCommand("parameter");
			chkBxH.setEnabled(true);
		}
		return chkBxH;
	}
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxSE() {
		if (chkBxSE == null) {
			chkBxSE = new JCheckBox();
			chkBxSE.setText("SE");
			chkBxSE.setToolTipText("Exponential (Tsekouras)  entropy");
			chkBxSE.addActionListener(this);
			chkBxSE.setActionCommand("parameter");
			chkBxSE.setEnabled(true);
		}
		return chkBxSE;
	}
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxSEta() {
		if (chkBxSEta == null) {
			chkBxSEta = new JCheckBox();
			chkBxSEta.setText("SEta");
			chkBxSEta.setToolTipText("generalized Anteneodo-Plastino entropies");
			chkBxSEta.addActionListener(this);
			chkBxSEta.setActionCommand("parameter");
			chkBxSEta.setEnabled(true);
		}
		return chkBxSEta;
	}
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxSKappa() {
		if (chkBxSKappa == null) {
			chkBxSKappa = new JCheckBox();
			chkBxSKappa.setText("SKappa");
			chkBxSKappa.setToolTipText("generalized Kaniadakis entropies");
			chkBxSKappa.addActionListener(this);
			chkBxSKappa.setActionCommand("parameter");
			chkBxSKappa.setEnabled(true);
		}
		return chkBxSKappa;
	}
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxSB() {
		if (chkBxSB == null) {
			chkBxSB = new JCheckBox();
			chkBxSB.setText("SB");
			chkBxSB.setToolTipText("generalized Curado entropies");
			chkBxSB.addActionListener(this);
			chkBxSB.setActionCommand("parameter");
			chkBxSB.setEnabled(true);
		}
		return chkBxSB;
	}
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxSBeta() {
		if (chkBxSBeta == null) {
			chkBxSBeta = new JCheckBox();
			chkBxSBeta.setText("SBeta");
			chkBxSBeta.setToolTipText("generalized Shafee entropies");
			chkBxSBeta.addActionListener(this);
			chkBxSBeta.setActionCommand("parameter");
			chkBxSBeta.setEnabled(true);
		}
		return chkBxSBeta;
	}
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxSGamma() {
		if (chkBxSGamma == null) {
			chkBxSGamma = new JCheckBox();
			chkBxSGamma.setText("SGamma");
			chkBxSGamma.setToolTipText("generalized SGamma entropies");
			chkBxSGamma.addActionListener(this);
			chkBxSGamma.setActionCommand("parameter");
			chkBxSGamma.setEnabled(true);
		}
		return chkBxSGamma;
	}
	
	/**
	 * This method initializes jJPanelMethodEps
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelEntropyTypes() {
		if (jPanelEntropyType == null) {
			jPanelEntropyType = new JPanel();
			jPanelEntropyType.setLayout(new BoxLayout(jPanelEntropyType, BoxLayout.X_AXIS));
			jPanelEntropyType.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Check entropy types", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			jPanelEntropyType.add(getJCheckBoxRenyi());
			jPanelEntropyType.add(getJCheckBoxTsallis());
			jPanelEntropyType.add(getJCheckBoxH());
			jPanelEntropyType.add(getJCheckBoxSE());
			jPanelEntropyType.add(getJCheckBoxSEta());
			jPanelEntropyType.add(getJCheckBoxSKappa());
			jPanelEntropyType.add(getJCheckBoxSB());	
			jPanelEntropyType.add(getJCheckBoxSBeta());
			jPanelEntropyType.add(getJCheckBoxSGamma());	
			// jPanelEntropyType.addSeparator();
	
		}
		return jPanelEntropyType;
	}
	// -------------------------------------------------------------------------------------------
	
	private JPanel getJPanelParamOption() {
		if (jPanelParamOption == null) {
			jPanelParamOption = new JPanel();
			jPanelParamOption.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Generalized entropy options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			GridBagLayout gbl_paramPanel = new GridBagLayout();
//			gbl_paramPanel.columnWidths  = new int[] { 0, 0, 0 };
//			gbl_paramPanel.rowHeights    = new int[] { 0, 0, 0, 0, 0 };
//			gbl_paramPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
//			gbl_paramPanel.rowWeights    = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			jPanelParamOption.setLayout(gbl_paramPanel);
			
			GridBagConstraints gbc_jPanelMaxEps = new GridBagConstraints();
			gbc_jPanelMaxEps.insets = new Insets(20, 0, 20, 0);
			gbc_jPanelMaxEps.gridx = 0;
			gbc_jPanelMaxEps.gridy = 0;
			gbc_jPanelMaxEps.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_jPanelMinQ = new GridBagConstraints();
			gbc_jPanelMinQ.insets = new Insets(5, 5, 10, 0); //top left bottom right 
			gbc_jPanelMinQ.gridx = 0;
			gbc_jPanelMinQ.gridy = 1;
			gbc_jPanelMinQ.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_jPanelMaxQ = new GridBagConstraints();
			gbc_jPanelMaxQ.insets = new Insets(5, 10, 10, 0);
			gbc_jPanelMaxQ.gridx = 1;
			gbc_jPanelMaxQ.gridy = 1;
			gbc_jPanelMaxQ.anchor = GridBagConstraints.EAST;
				
			GridBagConstraints gbc_jPanelMinEta = new GridBagConstraints();
			gbc_jPanelMinEta.insets = new Insets(5, 5, 10, 0);
			gbc_jPanelMinEta.gridx = 0;
			gbc_jPanelMinEta.gridy = 2;
			gbc_jPanelMinEta.anchor = GridBagConstraints.EAST;
			
			
			GridBagConstraints gbc_jPanelMaxEta = new GridBagConstraints();
			gbc_jPanelMaxEta.insets = new Insets(5, 10, 10, 0);
			gbc_jPanelMaxEta.gridx = 1;
			gbc_jPanelMaxEta.gridy = 2;
			gbc_jPanelMaxEta.anchor = GridBagConstraints.EAST;
		
			GridBagConstraints gbc_jPanelMinKappa = new GridBagConstraints();
			gbc_jPanelMinKappa.insets = new Insets(5, 5, 10, 0);
			gbc_jPanelMinKappa.gridx = 0;
			gbc_jPanelMinKappa.gridy = 3;
			gbc_jPanelMinKappa.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_jPanelMaxKappa = new GridBagConstraints();
			gbc_jPanelMaxKappa.insets = new Insets(5, 10, 10, 0);
			gbc_jPanelMaxKappa.gridx = 1;
			gbc_jPanelMaxKappa.gridy = 3;
			gbc_jPanelMaxKappa.anchor = GridBagConstraints.EAST;
			
			
			GridBagConstraints gbc_jPanelMinB = new GridBagConstraints();
			gbc_jPanelMinB.insets = new Insets(5, 5, 10, 0);
			gbc_jPanelMinB.gridx = 0;
			gbc_jPanelMinB.gridy = 4;
			gbc_jPanelMinB.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_jPanelMaxB = new GridBagConstraints();
			gbc_jPanelMaxB.insets = new Insets(5, 10, 10, 0);
			gbc_jPanelMaxB.gridx = 1;
			gbc_jPanelMaxB.gridy = 4;
			gbc_jPanelMaxB.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_jPanelMinBeta = new GridBagConstraints();
			gbc_jPanelMinBeta.insets = new Insets(5, 5, 10, 0);
			gbc_jPanelMinBeta.gridx = 0;
			gbc_jPanelMinBeta.gridy = 5;
			gbc_jPanelMinBeta.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_jPanelMaxBeta = new GridBagConstraints();
			gbc_jPanelMaxBeta.insets = new Insets(5, 10, 10, 0);
			gbc_jPanelMaxBeta.gridx = 1;
			gbc_jPanelMaxBeta.gridy = 5;
			gbc_jPanelMaxBeta.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_jPanelMinGamma = new GridBagConstraints();
			gbc_jPanelMinGamma.insets = new Insets(5, 5, 10, 0);
			gbc_jPanelMinGamma.gridx = 0;
			gbc_jPanelMinGamma.gridy = 6;
			gbc_jPanelMinGamma.anchor = GridBagConstraints.EAST;
			
			GridBagConstraints gbc_jPanelMaxGamma = new GridBagConstraints();
			gbc_jPanelMaxGamma.insets = new Insets(5, 1, 10, 0);
			gbc_jPanelMaxGamma.gridx = 1;
			gbc_jPanelMaxGamma.gridy = 6;
			gbc_jPanelMaxGamma.anchor = GridBagConstraints.EAST;
								
			jPanelParamOption.add(getJPaneEps(),     gbc_jPanelMaxEps);
			
			jPanelParamOption.add(getJPanelMinQ(),     gbc_jPanelMinQ);
			jPanelParamOption.add(getJPanelMaxQ(),     gbc_jPanelMaxQ);
			jPanelParamOption.add(getJPanelMinEta(),   gbc_jPanelMinEta);
			jPanelParamOption.add(getJPanelMaxEta(),   gbc_jPanelMaxEta);
			jPanelParamOption.add(getJPanelMinKappa(), gbc_jPanelMinKappa);
			jPanelParamOption.add(getJPanelMaxKappa(), gbc_jPanelMaxKappa);
			jPanelParamOption.add(getJPanelMinB(),     gbc_jPanelMinB);
			jPanelParamOption.add(getJPanelMaxB(),     gbc_jPanelMaxB);
			jPanelParamOption.add(getJPanelMinBeta(),  gbc_jPanelMinBeta);
			jPanelParamOption.add(getJPanelMaxBeta(),  gbc_jPanelMaxBeta);
			jPanelParamOption.add(getJPanelMinGamma(), gbc_jPanelMinGamma);
			jPanelParamOption.add(getJPanelMaxGamma(), gbc_jPanelMaxGamma);	
		}
		return jPanelParamOption;
	}	
	//------------------------------------------------------------------------------------------------------------
	private JPanel getJPanelGridOption() {
		if (jPanelGridOption == null) {
			jPanelGridOption = new JPanel();
		
			GridBagLayout gbl_panel = new GridBagLayout();
//			gbl_panel.columnWidths = new int[] { 0, 0, 0 };
//			gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
//			gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
//			gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			jPanelGridOption.setLayout(gbl_panel);
			
			//jPanelGridOption.setLayout(new BoxLayout(jPanelGridOption, BoxLayout.X_AXIS));
			
			jPanelGridOption.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Box options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			
			buttGlidingBox = new JRadioButton();
			buttGlidingBox.setText("Gliding box");
			buttGlidingBox.setToolTipText("Gliding box");
			buttGlidingBox.addActionListener(this);
			buttGlidingBox.setActionCommand("parameter");

			buttRasterBox = new JRadioButton();
			buttRasterBox.setText("Raster Bbx");
			buttRasterBox.setToolTipText("Raster box");
			buttRasterBox.addActionListener(this);
			buttRasterBox.setActionCommand("parameter");
			buttRasterBox.setEnabled(true);
		
			GridBagConstraints gbc_jPanelGlidingBox = new GridBagConstraints();
			gbc_jPanelGlidingBox.insets = new Insets(10, 5, 10, 5); //top  left bottom right
			gbc_jPanelGlidingBox.gridx = 0;
			gbc_jPanelGlidingBox.gridy = 0;
			
			GridBagConstraints gbc_jPanelRasterBox = new GridBagConstraints();
			gbc_jPanelRasterBox.insets = new Insets(10, 5, 10, 5);
			gbc_jPanelRasterBox.gridx = 1;
			gbc_jPanelRasterBox.gridy = 0;
			
			jPanelGridOption.add(buttGlidingBox, gbc_jPanelGlidingBox);
			jPanelGridOption.add(buttRasterBox,  gbc_jPanelRasterBox);
			
			this.setButtonGroupGridOption(); // Grouping of JRadioButtons
			
		}
		return jPanelGridOption;
	}
	
	private void setButtonGroupGridOption() {
		// if (buttGroupGridoption == null) {
		buttGroupGridOption = new ButtonGroup();
		buttGroupGridOption.add(buttGlidingBox);
		buttGroupGridOption.add(buttRasterBox);
	}

	// ----------------------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
					
			if (chkBxRenyi == e.getSource() || chkBxTsallis == e.getSource()) {
				if (chkBxRenyi.isSelected() || chkBxTsallis.isSelected()) {
					jSpinnerMinQ.setEnabled(true);
					jLabelMinQ.setEnabled(true);
					jSpinnerMaxQ.setEnabled(true);
					jLabelMaxQ.setEnabled(true);
				}		
				if (!chkBxRenyi.isSelected() && !chkBxTsallis.isSelected()) {
					jSpinnerMinQ.setEnabled(false);
					jLabelMinQ.setEnabled(false);
					jSpinnerMaxQ.setEnabled(false);
					jLabelMaxQ.setEnabled(false);
				}	
			}	
			if (chkBxH == e.getSource()) {	
			}
			if (chkBxSE == e.getSource()) {				
			}
			if (chkBxSEta == e.getSource()) {
				if (chkBxSEta.isSelected()) {
					jSpinnerMinEta.setEnabled(true);
					jLabelMinEta.setEnabled(true);
					jSpinnerMaxEta.setEnabled(true);
					jLabelMaxEta.setEnabled(true);
				}
				if (!chkBxSEta.isSelected()) {
					jSpinnerMinEta.setEnabled(false);
					jLabelMinEta.setEnabled(false);
					jSpinnerMaxEta.setEnabled(false);
					jLabelMaxEta.setEnabled(false);
				}
			}
			if (chkBxSKappa == e.getSource()) {	
				if (chkBxSKappa.isSelected()) {
					jSpinnerMinKappa.setEnabled(true);
					jLabelMinKappa.setEnabled(true);
					jSpinnerMaxKappa.setEnabled(true);
					jLabelMaxKappa.setEnabled(true);
				}
				if (!chkBxSKappa.isSelected()) {
					jSpinnerMinKappa.setEnabled(false);
					jLabelMinKappa.setEnabled(false);
					jSpinnerMaxKappa.setEnabled(false);
					jLabelMaxKappa.setEnabled(false);
				}
			}
			if (chkBxSB == e.getSource()) {	
				if (chkBxSB.isSelected()) {
					jSpinnerMinB.setEnabled(true);
					jLabelMinB.setEnabled(true);
					jSpinnerMaxB.setEnabled(true);
					jLabelMaxB.setEnabled(true);
				}
				if (!chkBxSB.isSelected()) {
					jSpinnerMinB.setEnabled(false);
					jLabelMinB.setEnabled(false);
					jSpinnerMaxB.setEnabled(false);
					jLabelMaxB.setEnabled(false);
				}
			}
			if (chkBxSBeta == e.getSource()) {	
				if (chkBxSBeta.isSelected()) {
					jSpinnerMinBeta.setEnabled(true);
					jLabelMinBeta.setEnabled(true);
					jSpinnerMaxBeta.setEnabled(true);
					jLabelMaxBeta.setEnabled(true);
				}
				if (!chkBxSBeta.isSelected()) {
					jSpinnerMinBeta.setEnabled(false);
					jLabelMinBeta.setEnabled(false);
					jSpinnerMaxBeta.setEnabled(false);
					jLabelMaxBeta.setEnabled(false);
				}
			}
			if (chkBxSGamma == e.getSource()) {
				if (chkBxSGamma.isSelected()) {
					jSpinnerMinGamma.setEnabled(true);
					jLabelMinGamma.setEnabled(true);
					jSpinnerMaxGamma.setEnabled(true);
					jLabelMaxGamma.setEnabled(true);
				}
				if (!chkBxSGamma.isSelected()) {
					jSpinnerMinGamma.setEnabled(false);
					jLabelMinGamma.setEnabled(false);
					jSpinnerMaxGamma.setEnabled(false);
					jLabelMaxGamma.setEnabled(false);
				}
			}
					
			if (e.getSource() == buttGlidingBox) {
				chkBxRenyi.setEnabled(true);
				chkBxTsallis.setEnabled(true);
				//list of chekboxes
				
			}
			if (e.getSource() == buttRasterBox) {
				chkBxRenyi.setEnabled(true); //?????????????????????????????????
				chkBxTsallis.setEnabled(true); //????????????????????????????????	
			}
			
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

		maxEps   = ((Number) jSpinnerEps      .getValue()).intValue();
		minQ     = ((Number) jSpinnerMinQ     .getValue()).intValue();
		maxQ     = ((Number) jSpinnerMaxQ     .getValue()).intValue();
		minEta   = ((Number) jSpinnerMinEta   .getValue()).doubleValue();
		maxEta   = ((Number) jSpinnerMaxEta   .getValue()).doubleValue();
		minKappa = ((Number) jSpinnerMinKappa .getValue()).doubleValue();
		maxKappa = ((Number) jSpinnerMaxKappa .getValue()).doubleValue();
		minB     = ((Number) jSpinnerMinB     .getValue()).doubleValue();
		maxB     = ((Number) jSpinnerMaxB     .getValue()).doubleValue();
		minBeta  = ((Number) jSpinnerMinBeta  .getValue()).doubleValue();
		maxBeta  = ((Number) jSpinnerMaxBeta  .getValue()).doubleValue();
		minGamma = ((Number) jSpinnerMinGamma .getValue()).doubleValue();
		maxGamma = ((Number) jSpinnerMaxGamma .getValue()).doubleValue();
		
		
		jSpinnerEps     .removeChangeListener(this);
		jSpinnerMinQ    .removeChangeListener(this);
		jSpinnerMaxQ    .removeChangeListener(this);
		jSpinnerMinEta  .removeChangeListener(this);
		jSpinnerMaxEta  .removeChangeListener(this);
		jSpinnerMinKappa.removeChangeListener(this);
		jSpinnerMaxKappa.removeChangeListener(this);
		jSpinnerMinB    .removeChangeListener(this);
		jSpinnerMaxB    .removeChangeListener(this);
		jSpinnerMinBeta .removeChangeListener(this);
		jSpinnerMaxBeta .removeChangeListener(this);
		jSpinnerMinGamma.removeChangeListener(this);
		jSpinnerMaxGamma.removeChangeListener(this);
	
		if (jSpinnerEps == e.getSource()) {
			
		}	
		if (jSpinnerMinQ == e.getSource()) {
			if (maxQ <= minQ) {
				maxQ = minQ + 1;
				jSpinnerMaxQ.setValue(maxQ);
			}
		}
		if (jSpinnerMaxQ == e.getSource()) {
			if (minQ >= maxQ) {
				minQ = maxQ - 1;
				jSpinnerMinQ.setValue(minQ);
			}
		}
		if (jSpinnerMinEta == e.getSource()) {
			if (maxEta <= minEta) {
				maxEta = minEta + 0.1f;
				jSpinnerMaxEta.setValue(maxEta);
			}
		}

		if (jSpinnerMaxEta == e.getSource()) {
			if (minEta >= maxEta) {
				minEta = maxEta - 0.1f;
				jSpinnerMinEta.setValue(minEta);
			}
		}
		if (jSpinnerMinKappa == e.getSource()) {
			if (maxKappa <= minKappa) {
				maxKappa = minKappa + 0.1f;
				jSpinnerMaxKappa.setValue(maxKappa);
			}
//			if (minKappa < 0.0f) { //not sure why this is needed
//				minKappa = 0.0f;
//				jSpinnerMinKappa.setValue(minKappa);
//			}		
		}
		if (jSpinnerMaxKappa == e.getSource()) {
			if (minKappa >= maxKappa) {
				minKappa = maxKappa - 0.1f;
				jSpinnerMinKappa.setValue(minKappa);
			}
		}
		if (jSpinnerMinB == e.getSource()) {
			if (maxB <= minB) {
				maxB = minB + 0.1f;
				jSpinnerMaxB.setValue(maxB);
			}
		}
		if (jSpinnerMaxB == e.getSource()) {
			if (minB >= maxB) {
				minB = maxB - 0.1f;
				jSpinnerMinB.setValue(minB);
			}
		}
		if (jSpinnerMinBeta == e.getSource()) {
			if (maxBeta <= minBeta) {
				maxBeta = minBeta + 0.1f;
				jSpinnerMaxBeta.setValue(maxBeta);
			}
		}
		if (jSpinnerMaxBeta == e.getSource()) {
			if (minBeta >= maxBeta) {
				minBeta = maxBeta - 0.1f;
				jSpinnerMinBeta.setValue(minBeta);
			}
		}
		if (jSpinnerMinGamma == e.getSource()) {
			if (maxGamma <= minGamma) {
				maxGamma = minGamma + 0.1f;
				jSpinnerMaxGamma.setValue(maxGamma);
			}
		}
		if (jSpinnerMaxGamma == e.getSource()) {
			if (minGamma >= maxGamma) {
				minGamma = maxGamma - 0.1f;
				jSpinnerMinGamma.setValue(minGamma);
			}
		}
		
		jSpinnerEps     .addChangeListener(this);
		jSpinnerMinQ    .addChangeListener(this);
		jSpinnerMaxQ    .addChangeListener(this);
		jSpinnerMinEta  .addChangeListener(this);
		jSpinnerMaxEta  .addChangeListener(this);
		jSpinnerMinKappa.addChangeListener(this);
		jSpinnerMaxKappa.addChangeListener(this);
		jSpinnerMinB    .addChangeListener(this);
		jSpinnerMaxB    .addChangeListener(this);
		jSpinnerMinBeta .addChangeListener(this);
		jSpinnerMaxBeta .addChangeListener(this);
		jSpinnerMinGamma.addChangeListener(this);
		jSpinnerMaxGamma.addChangeListener(this);
		
		

		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	
}// END
