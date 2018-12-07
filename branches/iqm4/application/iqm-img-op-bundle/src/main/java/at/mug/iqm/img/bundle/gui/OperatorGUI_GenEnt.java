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

	private int minQ;
	private int maxQ;
	private int maxEps;
	private float paramEta;
	private float paramKappa;
	private float paramB;
	private float paramBeta;
	private float paramGamma;
	
	private JCheckBox    chkBxRenyi    = null;
	private JCheckBox    chkBxTsallis  = null;
	private JCheckBox    chkBxH1       = null;
	private JCheckBox    chkBxH2       = null;
	private JCheckBox    chkBxH3       = null;
	private JCheckBox    chkBxSEta     = null;
	private JCheckBox    chkBxSKappa   = null;
	private JCheckBox    chkBxSB       = null;
	private JCheckBox    chkBxSE       = null;
	private JCheckBox    chkBxSBeta    = null;
	private JCheckBox    chkBxSGamma   = null;
	private JPanel       jPanelEntropyType  = null;
	

	private JPanel   jPanelMinQ   = null;
	private JLabel   jLabelMinQ   = null;
	private JSpinner jSpinnerMinQ = null;

	private JPanel   jPanelMaxQ   = null;
	private JLabel   jLabelMaxQ   = null;
	private JSpinner jSpinnerMaxQ = null;

	private JPanel   jPanelMaxEps   = null;
	private JLabel   jLabelMaxEps   = null;
	private JSpinner jSpinnerMaxEps = null;
	
	private JPanel   jPanelParamEta   = null;
	private JLabel   jLabelParamEta   = null;
	private JSpinner jSpinnerParamEta = null;

	private JPanel   jPanelParamKappa   = null;
	private JLabel   jLabelParamKappa   = null;
	private JSpinner jSpinnerParamKappa = null;
	
	private JPanel   jPanelParamB   = null;
	private JLabel   jLabelParamB   = null;
	private JSpinner jSpinnerParamB = null;
	
	private JPanel   jPanelParamBeta   = null;
	private JLabel   jLabelParamBeta   = null;
	private JSpinner jSpinnerParamBeta = null;
	
	private JPanel   jPanelParamGamma   = null;
	private JLabel   jLabelParamGamma   = null;
	private JSpinner jSpinnerParamGamma = null;
	
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
		if (chkBxH1.isSelected())       pb.setParameter("H1", 1);
		if (!chkBxH1.isSelected())      pb.setParameter("H1", 0);
		if (chkBxH2.isSelected())       pb.setParameter("H2", 1);
		if (!chkBxH2.isSelected())      pb.setParameter("H2", 0);
		if (chkBxH3.isSelected())       pb.setParameter("H3", 1);
		if (!chkBxH3.isSelected())      pb.setParameter("H3", 0);
		if (chkBxSEta.isSelected())     pb.setParameter("SEta", 1);
		if (!chkBxSEta.isSelected())    pb.setParameter("SEta", 0);
		if (chkBxSKappa.isSelected())   pb.setParameter("SKappa", 1);
		if (!chkBxSKappa.isSelected())  pb.setParameter("SKappa", 0);
		if (chkBxSB.isSelected())       pb.setParameter("SB", 1);
		if (!chkBxSB.isSelected())      pb.setParameter("SB", 0);
		if (chkBxSE.isSelected())       pb.setParameter("SE", 1);
		if (!chkBxSE.isSelected())      pb.setParameter("SE", 0);
		if (chkBxSBeta.isSelected())    pb.setParameter("SBeta", 1);
		if (!chkBxSBeta.isSelected())   pb.setParameter("SBeta", 0);
		if (chkBxSGamma.isSelected())   pb.setParameter("SGamma", 1);
		if (!chkBxSGamma.isSelected())  pb.setParameter("SGamma", 0);
		
		pb.setParameter("MinQ",   ((Number) jSpinnerMinQ  .getValue()).intValue());
		pb.setParameter("MaxQ",   ((Number) jSpinnerMaxQ  .getValue()).intValue());
		pb.setParameter("MaxEps", ((Number) jSpinnerMaxEps.getValue()).intValue());	
		
		pb.setParameter("ParamEta",   ((Number) jSpinnerParamEta  .getValue()).floatValue());
		pb.setParameter("ParamKappa", ((Number) jSpinnerParamKappa.getValue()).floatValue());
		pb.setParameter("ParamB",     ((Number) jSpinnerParamB    .getValue()).floatValue());
		pb.setParameter("ParamBeta",  ((Number) jSpinnerParamBeta .getValue()).floatValue());
		pb.setParameter("ParamGamma", ((Number) jSpinnerParamBeta .getValue()).floatValue());
	
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
		if (pb.getIntParameter("H1") == 0)      chkBxH1.setSelected(false);
		if (pb.getIntParameter("H1") == 1)      chkBxH1.setSelected(true);
		if (pb.getIntParameter("H2") == 0)      chkBxH2.setSelected(false);
		if (pb.getIntParameter("H2") == 1)      chkBxH2.setSelected(true);
		if (pb.getIntParameter("H3") == 0)      chkBxH3.setSelected(false);
		if (pb.getIntParameter("H3") == 1)      chkBxH3.setSelected(true);
		if (pb.getIntParameter("SEta") == 0)    chkBxSEta.setSelected(false);
		if (pb.getIntParameter("SEta") == 1)    chkBxSEta.setSelected(true);
		if (pb.getIntParameter("SKappa") == 0)  chkBxSKappa.setSelected(false);
		if (pb.getIntParameter("SKappa") == 1)  chkBxSKappa.setSelected(true);
		if (pb.getIntParameter("SB") == 0)      chkBxSB.setSelected(false);
		if (pb.getIntParameter("SB") == 1)      chkBxSB.setSelected(true);
		if (pb.getIntParameter("SE") == 0)      chkBxSE.setSelected(false);
		if (pb.getIntParameter("SE") == 1)      chkBxSE.setSelected(true);
		if (pb.getIntParameter("SBeta") == 0)   chkBxSBeta.setSelected(false);
		if (pb.getIntParameter("SBeta") == 1)   chkBxSBeta.setSelected(true);
		if (pb.getIntParameter("SGamma") == 0)  chkBxSGamma.setSelected(false);
		if (pb.getIntParameter("SGamma") == 1)  chkBxSGamma.setSelected(true);

		jSpinnerMinQ.removeChangeListener(this);
		jSpinnerMinQ.setValue(pb.getIntParameter("MinQ"));
		jSpinnerMinQ.addChangeListener(this);

		jSpinnerMaxQ.removeChangeListener(this);
		jSpinnerMaxQ.setValue(pb.getIntParameter("MaxQ"));
		jSpinnerMaxQ.addChangeListener(this);

		jSpinnerMaxEps.removeChangeListener(this);
		jSpinnerMaxEps.setValue(pb.getIntParameter("MaxEps"));
		jSpinnerMaxEps.addChangeListener(this);
		
		jSpinnerParamEta.removeChangeListener(this);
		jSpinnerParamEta.setValue(pb.getFloatParameter("ParamEta"));
		jSpinnerParamEta.addChangeListener(this);
			
		jSpinnerParamKappa.removeChangeListener(this);
		jSpinnerParamKappa.setValue(pb.getFloatParameter("ParamKappa"));
		jSpinnerParamKappa.addChangeListener(this);
			
		jSpinnerParamB.removeChangeListener(this);
		jSpinnerParamB.setValue(pb.getFloatParameter("ParamB"));
		jSpinnerParamB.addChangeListener(this);
			
		jSpinnerParamBeta.removeChangeListener(this);
		jSpinnerParamBeta.setValue(pb.getFloatParameter("ParamBeta"));
		jSpinnerParamBeta.addChangeListener(this);
			
		jSpinnerParamGamma.removeChangeListener(this);
		jSpinnerParamGamma.setValue(pb.getFloatParameter("ParamGamma"));
		jSpinnerParamGamma.addChangeListener(this);

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
				// System.out.println("OperatorGUI_FracBox: newBoxWidth: " +
				// newBoxWidth);
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
		PlanarImage pi = ((IqmDataBox) this.pb.getSources().firstElement()).getImage();
		int width = pi.getWidth();
		int height = pi.getHeight();
		minQ = -5;
		maxQ = 5;
		maxEps = this.getMaxEps(width, height);

		// System.out.println("OperatorGUI_GenEnt: numberMax: " +
		// numberMax);
		SpinnerModel sModel = new SpinnerNumberModel(minQ, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1); // init, min,// max, step
		jSpinnerMinQ.removeChangeListener(this);
		jSpinnerMinQ.setModel(sModel);
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMinQ.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setEditable(true);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
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

		sModel = new SpinnerNumberModel(maxEps, 3, maxEps, 1); // init, min,  max, step
		jSpinnerMaxEps.removeChangeListener(this);
		jSpinnerMaxEps.setModel(sModel);
		defEditor = (JSpinner.DefaultEditor) jSpinnerMaxEps.getEditor();
		ftf = defEditor.getTextField();
		ftf.setEditable(true);
		intFormatter = (InternationalFormatter) ftf.getFormatter();
		decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")// ;
		// jSpinnerNumMaxEps.setValue(maxEps);
		jSpinnerMaxEps.addChangeListener(this);

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
	 * This method initializes jJPanelMinQ
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMinQ() {
		if (jPanelMinQ == null) {
			jPanelMinQ = new JPanel();
			jPanelMinQ.setLayout(new BorderLayout());
			jLabelMinQ = new JLabel("min q: ");
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
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMinQ.add(jLabelMinQ, BorderLayout.NORTH);
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
			jLabelMaxQ = new JLabel("max q: ");
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
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMaxQ.add(jLabelMaxQ, BorderLayout.NORTH);
			jPanelMaxQ.add(jSpinnerMaxQ, BorderLayout.CENTER);
		}
		return jPanelMaxQ;
	}
	
	/**
	 * This method initializes jJPanelMaxEps
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMaxEps() {
		if (jPanelMaxEps == null) {
			jPanelMaxEps = new JPanel();
			jPanelMaxEps.setLayout(new BorderLayout());
			jLabelMaxEps = new JLabel("Max eps [pixel]: ");
			// jLabelMaxEps.setPreferredSize(new Dimension(70, 20));
			jLabelMaxEps.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerMaxEps = new JSpinner(sModel);
			// jSpinnerMaxEps = new JSpinner();
			jSpinnerMaxEps.setPreferredSize(new Dimension(60, 20));
			jSpinnerMaxEps.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMaxEps.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelMaxEps.add(jLabelMaxEps, BorderLayout.NORTH);
			jPanelMaxEps.add(jSpinnerMaxEps, BorderLayout.CENTER);
		}
		return jPanelMaxEps;
	}

	/**
	 * This method initializes jJPanelParamEta
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelParamEta() {
		if (jPanelParamEta == null) {
			jPanelParamEta = new JPanel();
			jPanelParamEta.setLayout(new BorderLayout());
			jLabelParamEta = new JLabel("ParamEta: ");
			//jLabelParamEta.setPreferredSize(new Dimension(70, 20));
			jLabelParamEta.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1.0f, Float.MIN_VALUE, Float.MAX_VALUE, 0.1f); // init, min, max, step
			jSpinnerParamEta = new JSpinner(sModel);
			//jSpinnerParamEta = new JSpinner();
			//jSpinnerParamEta.setPreferredSize(new Dimension(60, 20));
			jSpinnerParamEta.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerParamEta.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")	
			jPanelParamEta.add(jLabelParamEta, BorderLayout.NORTH);
			jPanelParamEta.add(jSpinnerParamEta, BorderLayout.CENTER);
		}
		return jPanelParamEta;
	}

	/**
	 * This method initializes jJPanelParamKappa
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelParamKappa() {
		if (jPanelParamKappa == null) {
			jPanelParamKappa = new JPanel();
			jPanelParamKappa.setLayout(new BorderLayout());
			jLabelParamKappa = new JLabel("ParamKappa: ");
			// jLabelParamKappa.setPreferredSize(new Dimension(70, 20));
			jLabelParamKappa.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0.5f, Float.MIN_VALUE, 1.0f-Float.MIN_VALUE, 0.1f); // init, min, max, step
			jSpinnerParamKappa = new JSpinner(sModel);
			// jSpinnerParamKappa = new JSpinner();
			//jSpinnerParamKappa.setPreferredSize(new Dimension(60, 20));
			jSpinnerParamKappa.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerParamKappa.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelParamKappa.add(jLabelParamKappa, BorderLayout.NORTH);
			jPanelParamKappa.add(jSpinnerParamKappa, BorderLayout.CENTER);
		}
		return jPanelParamKappa;
	}
	/**
	 * This method initializes jJPanelParamB
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelParamB() {
		if (jPanelParamB == null) {
			jPanelParamB = new JPanel();
			jPanelParamB.setLayout(new BorderLayout());
			jLabelParamB = new JLabel("ParamB: ");
			// jLabelParamB.setPreferredSize(new Dimension(70, 20));
			jLabelParamB.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1.0f, Float.MIN_VALUE, Float.MAX_VALUE, 0.1f); // init, min, max, step
			jSpinnerParamB = new JSpinner(sModel);
			// jSpinnerParamB = new JSpinner();
			//jSpinnerParamB.setPreferredSize(new Dimension(60, 20));
			jSpinnerParamB.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerParamB.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelParamB.add(jLabelParamB, BorderLayout.NORTH);
			jPanelParamB.add(jSpinnerParamB, BorderLayout.CENTER);
		}
		return jPanelParamB;
	}
	/**
	 * This method initializes jJPanelParamBeta
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelParamBeta() {
		if (jPanelParamBeta == null) {
			jPanelParamBeta = new JPanel();
			jPanelParamBeta.setLayout(new BorderLayout());
			jLabelParamBeta = new JLabel("ParamBeta: ");
			// jLabelParamBeta.setPreferredSize(new Dimension(70, 20));
			jLabelParamBeta.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0.5f, Float.MIN_VALUE, 1.0, 0.1f); // init, min, max, step
			jSpinnerParamBeta = new JSpinner(sModel);
			// jSpinnerParamBeta = new JSpinner();
			//jSpinnerParamBeta.setPreferredSize(new Dimension(60, 20));
			jSpinnerParamBeta.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerParamBeta.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelParamBeta.add(jLabelParamBeta, BorderLayout.NORTH);
			jPanelParamBeta.add(jSpinnerParamBeta, BorderLayout.CENTER);
		}
		return jPanelParamBeta;
	}
	
	/**
	 * This method initializes jJPanelParamGamma
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelParamGamma() {
		if (jPanelParamGamma == null) {
			jPanelParamGamma = new JPanel();
			jPanelParamGamma.setLayout(new BorderLayout());
			jLabelParamGamma = new JLabel("ParamGamma: ");
			// jLabelParamGamma.setPreferredSize(new Dimension(70, 20));
			jLabelParamGamma.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1.0f, Float.MIN_VALUE, Float.MAX_VALUE, 0.1f); // init, min, max, step
			jSpinnerParamGamma = new JSpinner(sModel);
			// jSpinnerParamGamma = new JSpinner();
			//jSpinnerParamGamma.setPreferredSize(new Dimension(60, 20));
			jSpinnerParamGamma.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerParamGamma.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#0.00"); // decimalFormat.applyPattern("#,##0.0")
			jPanelParamGamma.add(jLabelParamGamma, BorderLayout.NORTH);
			jPanelParamGamma.add(jSpinnerParamGamma, BorderLayout.CENTER);
		}
		return jPanelParamGamma;
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
	private JCheckBox getJCheckBoxH1() {
		if (chkBxH1 == null) {
			chkBxH1 = new JCheckBox();
			chkBxH1.setText("H1");
			chkBxH1.setToolTipText("generalized H1 entropies");
			chkBxH1.addActionListener(this);
			chkBxH1.setActionCommand("parameter");
			chkBxH1.setEnabled(true);
		}
		return chkBxH1;
	}
	
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxH2() {
		if (chkBxH2 == null) {
			chkBxH2 = new JCheckBox();
			chkBxH2.setText("H2");
			chkBxH2.setToolTipText("generalized H2 entropies");
			chkBxH2.addActionListener(this);
			chkBxH2.setActionCommand("parameter");
			chkBxH2.setEnabled(true);
		}
		return chkBxH2;
	}
	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxH3() {
		if (chkBxH3 == null) {
			chkBxH3 = new JCheckBox();
			chkBxH3.setText("H3");
			chkBxH3.setToolTipText("generalized H3 entropies");
			chkBxH3.addActionListener(this);
			chkBxH3.setActionCommand("parameter");
			chkBxH3.setEnabled(true);
		}
		return chkBxH3;
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
			chkBxSEta.setToolTipText("generalized SEta entropies");
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
			chkBxSKappa.setToolTipText("generalized SKappa entropies");
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
			chkBxSB.setToolTipText("generalized SB entropies");
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
	private JCheckBox getJCheckBoxSE() {
		if (chkBxSE == null) {
			chkBxSE = new JCheckBox();
			chkBxSE.setText("SE");
			chkBxSE.setToolTipText("generalized SE entropies");
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
	private JCheckBox getJCheckBoxSBeta() {
		if (chkBxSBeta == null) {
			chkBxSBeta = new JCheckBox();
			chkBxSBeta.setText("SBeta");
			chkBxSBeta.setToolTipText("generalized SBeta entropies");
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
			jPanelEntropyType.add(getJCheckBoxH1());
			jPanelEntropyType.add(getJCheckBoxH2());
			jPanelEntropyType.add(getJCheckBoxH3());
			jPanelEntropyType.add(getJCheckBoxSEta());
			jPanelEntropyType.add(getJCheckBoxSKappa());
			jPanelEntropyType.add(getJCheckBoxSB());
			jPanelEntropyType.add(getJCheckBoxSE());
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
			gbl_paramPanel.columnWidths  = new int[] { 0, 0, 0 };
			gbl_paramPanel.rowHeights    = new int[] { 0, 0, 0, 0, 0 };
			gbl_paramPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			gbl_paramPanel.rowWeights    = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			jPanelParamOption.setLayout(gbl_paramPanel);
			
			GridBagConstraints gbc_jPanelMinQ = new GridBagConstraints();
			gbc_jPanelMinQ.insets = new Insets(5, 5, 20, 0); //top left bottom right 
			gbc_jPanelMinQ.gridx = 0;
			gbc_jPanelMinQ.gridy = 0;
			
			GridBagConstraints gbc_jPanelMaxQ = new GridBagConstraints();
			gbc_jPanelMaxQ.insets = new Insets(5, 10, 20, 0);
			gbc_jPanelMaxQ.gridx = 1;
			gbc_jPanelMaxQ.gridy = 0;
			
			GridBagConstraints gbc_jPanelMaxEps = new GridBagConstraints();
			gbc_jPanelMaxEps.insets = new Insets(5, 0, 20, 0);
			gbc_jPanelMaxEps.gridx = 3;
			gbc_jPanelMaxEps.gridy = 0;
			
			GridBagConstraints gbc_jPanelParamEta = new GridBagConstraints();
			gbc_jPanelParamEta.insets = new Insets(5, 0, 20, 0);
			gbc_jPanelParamEta.gridx = 0;
			gbc_jPanelParamEta.gridy = 3;
			GridBagConstraints gbc_jPanelParamKappa = new GridBagConstraints();
			gbc_jPanelParamKappa.insets = new Insets(5, 30, 20, 0);
			gbc_jPanelParamKappa.gridx = 1;
			gbc_jPanelParamKappa.gridy = 3;
			GridBagConstraints gbc_jPanelParamB = new GridBagConstraints();
			gbc_jPanelParamB.insets = new Insets(5, 0, 20, 0);
			gbc_jPanelParamB.gridx = 2;
			gbc_jPanelParamB.gridy = 3;
			GridBagConstraints gbc_jPanelParamBeta = new GridBagConstraints();
			gbc_jPanelParamBeta.insets = new Insets(5, 0, 20, 0);
			gbc_jPanelParamBeta.gridx = 3;
			gbc_jPanelParamBeta.gridy = 3;
			GridBagConstraints gbc_jPanelParamGamma = new GridBagConstraints();
			gbc_jPanelParamGamma.insets = new Insets(5, 30, 20, 5);
			gbc_jPanelParamGamma.gridx = 4;
			gbc_jPanelParamGamma.gridy = 3;
		
				
			jPanelParamOption.add(getJPanelMinQ(),     gbc_jPanelMinQ);
			jPanelParamOption.add(getJPanelMaxQ(),     gbc_jPanelMaxQ);
			jPanelParamOption.add(getJPanelMaxEps(),   gbc_jPanelMaxEps);
			
			jPanelParamOption.add(getJPanelParamEta(),   gbc_jPanelParamEta);
			jPanelParamOption.add(getJPanelParamKappa(), gbc_jPanelParamKappa);
			jPanelParamOption.add(getJPanelParamB(),     gbc_jPanelParamB);
			jPanelParamOption.add(getJPanelParamBeta(),  gbc_jPanelParamBeta);
			jPanelParamOption.add(getJPanelParamGamma(), gbc_jPanelParamGamma);
		
		}
		return jPanelParamOption;
	}	
	//------------------------------------------------------------------------------------------------------------
	private JPanel getJPanelGridOption() {
		if (jPanelGridOption == null) {
			jPanelGridOption = new JPanel();
		
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 0, 0, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			jPanelGridOption.setLayout(gbl_panel);
			
			//jPanelGridOption.setLayout(new BoxLayout(jPanelGridOption, BoxLayout.X_AXIS));
			
			jPanelGridOption.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Box options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			
			buttGlidingBox = new JRadioButton();
			buttGlidingBox.setText("Gliding Box");
			buttGlidingBox.setToolTipText("Gliding Box");
			buttGlidingBox.addActionListener(this);
			buttGlidingBox.setActionCommand("parameter");

			buttRasterBox = new JRadioButton();
			buttRasterBox.setText("Raster Box");
			buttRasterBox.setToolTipText("Raster Box");
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
					
			if (chkBxRenyi == e.getSource()) {	
			}
			if (chkBxTsallis == e.getSource()) {	
			}
			if (chkBxH1 == e.getSource()) {	
			}
			if (chkBxH2 == e.getSource()) {	
			}
			if (chkBxH3 == e.getSource()) {	
			}
			if (chkBxSEta == e.getSource()) {
				if (chkBxSEta.isSelected()) {
					jSpinnerParamEta.setEnabled(true);
					jLabelParamEta.setEnabled(true);
				}
				if (!chkBxSEta.isSelected()) {
					jSpinnerParamEta.setEnabled(false);
					jLabelParamEta.setEnabled(false);
				}
			}
			if (chkBxSKappa == e.getSource()) {	
				if (chkBxSKappa.isSelected()) {
					jSpinnerParamKappa.setEnabled(true);
					jLabelParamKappa.setEnabled(true);
				}
				if (!chkBxSKappa.isSelected()) {
					jSpinnerParamKappa.setEnabled(false);
					jLabelParamKappa.setEnabled(false);
				}
			}
			if (chkBxSB == e.getSource()) {	
				if (chkBxSB.isSelected()) {
					jSpinnerParamB.setEnabled(true);
					jLabelParamB.setEnabled(true);
				}
				if (!chkBxSB.isSelected()) {
					jSpinnerParamB.setEnabled(false);
					jLabelParamB.setEnabled(false);
				}
			}
			if (chkBxSE == e.getSource()) {	
				
			}
			if (chkBxSBeta == e.getSource()) {	
				if (chkBxSBeta.isSelected()) {
					jSpinnerParamBeta.setEnabled(true);
					jLabelParamBeta.setEnabled(true);
				}
				if (!chkBxSBeta.isSelected()) {
					jSpinnerParamBeta.setEnabled(false);
					jLabelParamBeta.setEnabled(false);
				}
			}
			if (chkBxSGamma == e.getSource()) {
				if (chkBxSGamma.isSelected()) {
					jSpinnerParamGamma.setEnabled(true);
					jLabelParamGamma.setEnabled(true);
				}
				if (!chkBxSGamma.isSelected()) {
					jSpinnerParamGamma.setEnabled(false);
					jLabelParamGamma.setEnabled(false);
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

		minQ     = ((Number) jSpinnerMinQ  .getValue()).intValue();
		maxQ     = ((Number) jSpinnerMaxQ  .getValue()).intValue();
		maxEps   = ((Number) jSpinnerMaxEps.getValue()).intValue();
		
		jSpinnerMinQ.removeChangeListener(this);
		jSpinnerMaxQ.removeChangeListener(this);
		jSpinnerMaxEps.removeChangeListener(this);
		
		
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
		if (jSpinnerMaxEps == e.getSource()) {
				
		}
	
		
		jSpinnerMinQ.addChangeListener(this);
		jSpinnerMaxQ.addChangeListener(this);
		jSpinnerMaxEps.addChangeListener(this);
		

		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	
}// END
