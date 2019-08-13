package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_HistoModify.java
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.jai.PlanarImage;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.plot.PlotTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpHistoModifyDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2009 06
 * @update 2014 11 JRadioButtons and TitledBorder
 */
public class OperatorGUI_HistoModify extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {
	
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 1628979061994057933L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_HistoModify.class);

	private ParameterBlockIQM pbJAI = null; // @jve:decl-index=0:

	private double offSetLow, offSetHigh; // Offset for lowest and highest grey value

	private JPanel   jSpinnerPanelOffSetLow = null;
	private JSpinner jSpinnerOffSetLow;
	private JLabel   jLabelOffSetLow;

	private JPanel   jSpinnerPanelOffSetHigh = null;
	private JSpinner jSpinnerOffSetHigh;
	private JLabel   jLabelOffSetHigh;

	private JCheckBox jCheckBoxBandCombine = null;

	private JRadioButton buttMethodRescale = null;
	private JRadioButton buttMethodEqual   = null;
	private JRadioButton buttMethodNorm    = null;
	private JRadioButton buttMethodPiece   = null;
	private JPanel       jPanelMethod      = null;
	private ButtonGroup  buttGroupMethod   = null;

	private JButton jButtonHistoOld = null;
	private JButton jButtonHistoNew = null;
	
	private JPanel pnlOffset;

	/**
	 * constructor
	 */
	public OperatorGUI_HistoModify() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpHistoModifyDescriptor().getName());

		this.initialize();

		this.setTitle("Histogram Modification");

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0 };
		this.getOpGUIContent().setLayout(gridBagLayout);

		this.getOpGUIContent().add(getPnlOffset(),            getGridBagConstraints_Offset());
		this.getOpGUIContent().add(getJCheckBoxBandCombine(), getGridBagConstraints_BandCombine());
		this.getOpGUIContent().add(getJPanelMethod(),         getGridBagConstraints_PanelMethod());
		this.getOpGUIContent().add(getJButtonHistoOld(),      getGridBagConstraints_ButtonHistoOld());
		this.getOpGUIContent().add(getJButtonHistoNew(),      getGridBagConstraints_ButtonHistoNew());

		this.pack();
	}
	
	// ------------------------------------------------------------------------------
	private GridBagConstraints getGridBagConstraints_Offset(){
		GridBagConstraints gbc_pnlOffset = new GridBagConstraints();
		gbc_pnlOffset.gridwidth = 2;
		gbc_pnlOffset.insets = new Insets(5, 0, 0, 0); // top,left bottom  right
		gbc_pnlOffset.gridx = 0;
		gbc_pnlOffset.gridy = 0;
		return gbc_pnlOffset;
	}
	
	private GridBagConstraints getGridBagConstraints_BandCombine() {
		GridBagConstraints gridBagConstraintsBandCombine = new GridBagConstraints();
		gridBagConstraintsBandCombine.gridwidth = 2;
		gridBagConstraintsBandCombine.gridx = 0;
		gridBagConstraintsBandCombine.gridy = 1;
		gridBagConstraintsBandCombine.insets = new Insets(5, 0, 0, 0); // top,left bottom  right
		// gridBagConstraintsBandCombine.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsBandCombine;
	}
	
	private GridBagConstraints getGridBagConstraints_PanelMethod(){
		GridBagConstraints gbc_pnlMethod = new GridBagConstraints();
		gbc_pnlMethod.gridwidth = 2;
		gbc_pnlMethod.insets = new Insets(5, 0, 0, 0);
		gbc_pnlMethod.gridx = 0;
		gbc_pnlMethod.gridy = 2;
		return gbc_pnlMethod;
	}

	private GridBagConstraints getGridBagConstraints_ButtonHistoOld() {
		GridBagConstraints gridBagConstraintsButtonHistoOld = new GridBagConstraints();
		gridBagConstraintsButtonHistoOld.gridx = 0;
		gridBagConstraintsButtonHistoOld.gridy = 3;
		// gridBagConstraintsButtonHistoOld.gridwidth = 2;
		gridBagConstraintsButtonHistoOld.insets = new Insets(5, 5, 5, 5); // top,  left bottom right
		gridBagConstraintsButtonHistoOld.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsButtonHistoOld;
	}

	private GridBagConstraints getGridBagConstraints_ButtonHistoNew() {
		GridBagConstraints gridBagConstraintsButtonHistoNew = new GridBagConstraints();
		gridBagConstraintsButtonHistoNew.gridx = 1;
		gridBagConstraintsButtonHistoNew.gridy = 3;
		gridBagConstraintsButtonHistoNew.insets = new Insets(5, 5, 5, 5); // top, left bottom right
		gridBagConstraintsButtonHistoNew.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsButtonHistoNew;
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		pbJAI.setParameter("OffSetLow", offSetLow);
		pbJAI.setParameter("OffSetHigh", offSetHigh);
		if (jCheckBoxBandCombine.isSelected())  pbJAI.setParameter("BandCombine", 1);
		if (!jCheckBoxBandCombine.isSelected()) pbJAI.setParameter("BandCombine", 0);

		if (buttMethodRescale.isSelected())  pbJAI.setParameter("Method", 0);
		if (buttMethodEqual.isSelected())    pbJAI.setParameter("Method", 1);
		if (buttMethodNorm.isSelected())     pbJAI.setParameter("Method", 2);
		if (buttMethodPiece.isSelected())    pbJAI.setParameter("Method", 3);
	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pbJAI = this.workPackage.getParameters();

		offSetLow = pbJAI.getDoubleParameter("OffSetLow");
		offSetHigh = pbJAI.getDoubleParameter("OffSetHigh");

		jSpinnerOffSetLow.removeChangeListener(this);
		jSpinnerOffSetLow.setValue(offSetLow);
		jSpinnerOffSetLow.addChangeListener(this);
//		jSpinnerOffSetLow.setToolTipText("Offset low: " + (double) offSetLow + " %");

		jSpinnerOffSetHigh.removeChangeListener(this);
		jSpinnerOffSetHigh.setValue(offSetHigh);
		jSpinnerOffSetHigh.addChangeListener(this);
//		jSpinnerOffSetHigh.setToolTipText("Offset high: " + (double) offSetHigh + " %");

		if (pbJAI.getIntParameter("BandCombine") == 0)  jCheckBoxBandCombine.setSelected(false);
		if (pbJAI.getIntParameter("BandCombine") == 1)  jCheckBoxBandCombine.setSelected(true);

		if (pbJAI.getIntParameter("Method") == 0)  buttMethodRescale.setSelected(true);
		if (pbJAI.getIntParameter("Method") == 1)  buttMethodEqual.setSelected(true);
		if (pbJAI.getIntParameter("Method") == 2)  buttMethodNorm.setSelected(true);
		if (pbJAI.getIntParameter("Method") == 3)  buttMethodPiece.setSelected(true);

		if ((offSetLow > 0) || (offSetHigh > 0)) {
			jCheckBoxBandCombine.setEnabled(true);
		} else {
			jCheckBoxBandCombine.setEnabled(false);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------
	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	// ----------------------------------------------------------------------------------------
	/**
	 * This method initializes jSpinnerOffSetLow
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getJSpinnerOffSetLow() {
		if (jSpinnerPanelOffSetLow == null) {
			jSpinnerPanelOffSetLow = new JPanel();
			BorderLayout bl_jSpinnerPanelOffSetLow = new BorderLayout();
			bl_jSpinnerPanelOffSetLow.setHgap(10);
			jSpinnerPanelOffSetLow.setLayout(bl_jSpinnerPanelOffSetLow);
			jSpinnerOffSetLow = new JSpinner(new SpinnerNumberModel(0.0d, 0.0d, 100.0d, 1.0d));
			jSpinnerOffSetLow.setPreferredSize(new Dimension(60, 20));
			jSpinnerOffSetLow.setEditor(new JSpinner.NumberEditor(jSpinnerOffSetLow, "#0.00"));	
			jSpinnerOffSetLow.setToolTipText("low grey level offset [%]");
			jLabelOffSetLow = new JLabel();
			jLabelOffSetLow.setToolTipText("low grey level offset [%]");
			jLabelOffSetLow.setLabelFor(jSpinnerOffSetLow);
			jLabelOffSetLow.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelOffSetLow.setText("low [%]:");
			jSpinnerPanelOffSetLow.add(jLabelOffSetLow,   BorderLayout.CENTER);
			jSpinnerPanelOffSetLow.add(jSpinnerOffSetLow, BorderLayout.EAST);
		}
		return jSpinnerPanelOffSetLow;
	}

	/**
	 * This method initializes jSpinnerOffSetHigh
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getJSpinnerOffSetHigh() {
		if (jSpinnerPanelOffSetHigh == null) {
			jSpinnerPanelOffSetHigh = new JPanel();
			BorderLayout bl_jSpinnerPanelOffSetHigh = new BorderLayout();
			bl_jSpinnerPanelOffSetHigh.setHgap(10);
			jSpinnerPanelOffSetHigh.setLayout(bl_jSpinnerPanelOffSetHigh);
			jSpinnerOffSetHigh = new JSpinner(new SpinnerNumberModel(0.d, 0.d, 100.d, 1.0d));
			jSpinnerOffSetHigh.setPreferredSize(new Dimension(60, 20));
			jSpinnerOffSetHigh.setEditor(new JSpinner.NumberEditor(jSpinnerOffSetHigh, "#0.00"));	
			jSpinnerOffSetHigh.setToolTipText("high grey level offset [%]");
			jLabelOffSetHigh = new JLabel();
			jLabelOffSetHigh.setToolTipText("high grey level offset [%]");
			jLabelOffSetHigh.setLabelFor(jSpinnerOffSetHigh);
			jLabelOffSetHigh.setHorizontalAlignment(SwingConstants.TRAILING);
			jLabelOffSetHigh.setText("high [%]:");
			jSpinnerPanelOffSetHigh.add(jLabelOffSetHigh,   BorderLayout.CENTER);
			jSpinnerPanelOffSetHigh.add(jSpinnerOffSetHigh, BorderLayout.EAST);
		}
		return jSpinnerPanelOffSetHigh;
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxBandCombine
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxBandCombine() {
		if (jCheckBoxBandCombine == null) {
			jCheckBoxBandCombine = new JCheckBox();
			jCheckBoxBandCombine.setText("Band combine");
			jCheckBoxBandCombine.setSelected(false);
			jCheckBoxBandCombine.addActionListener(this);
			jCheckBoxBandCombine.setActionCommand("parameter");
			jCheckBoxBandCombine.setEnabled(false);
		}
		return jCheckBoxBandCombine;
	}

	// -----------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: MethodRescale
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtMethodRescale() {
		buttMethodRescale = new JRadioButton();
		buttMethodRescale.setText("Rescale");
		buttMethodRescale.setToolTipText("histogram rescaling");
		buttMethodRescale.addActionListener(this);
		buttMethodRescale.setActionCommand("parameter");
		return buttMethodRescale;
	}

	/**
	 * This method initializes the Option: MethodEqual
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtMethodEqual() {
		buttMethodEqual = new JRadioButton();
		buttMethodEqual.setText("Equalize");
		buttMethodEqual.setToolTipText("histogram equalization");
		buttMethodEqual.addActionListener(this);
		buttMethodEqual.setActionCommand("parameter");
		return buttMethodEqual;
	}

	/**
	 * This method initializes the Option: MethodNorm
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtMethodNorm() {
		buttMethodNorm = new JRadioButton();
		buttMethodNorm.setText("Normalization");
		buttMethodNorm.setToolTipText("histogram normalization");
		buttMethodNorm.addActionListener(this);
		buttMethodNorm.setActionCommand("parameter");
		return buttMethodNorm;
	}

	/**
	 * This method initializes the Option: MethodPiece
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtMethodPiece() {
		buttMethodPiece = new JRadioButton();
		buttMethodPiece.setText("Piecewise");
		buttMethodPiece.setToolTipText("picewise linear mapping");
		buttMethodPiece.addActionListener(this);
		buttMethodPiece.setActionCommand("parameter");
		return buttMethodPiece;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttMethodRescale);
		buttGroupMethod.add(buttMethodEqual);
		buttGroupMethod.add(buttMethodNorm);
		buttGroupMethod.add(buttMethodPiece);
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes jButtonHistoOld
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JButton getJButtonHistoOld() {
		if (jButtonHistoOld == null) {
			jButtonHistoOld = new JButton();
			jButtonHistoOld.setText("Get old histogram");
			jButtonHistoOld.addActionListener(this);
			jButtonHistoOld.setActionCommand("histoold");
		}
		return jButtonHistoOld;
	}

	/**
	 * This method initializes jButtonHistoNew
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JButton getJButtonHistoNew() {
		if (jButtonHistoNew == null) {
			jButtonHistoNew = new JButton();
			jButtonHistoNew.setText("Get new histogram");
			jButtonHistoNew.addActionListener(this);
			jButtonHistoNew.setActionCommand("histonew");
		}
		return jButtonHistoNew;
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}
		if ("histoold".equals(e.getActionCommand())) { // button
			PlanarImage pi = ((IqmDataBox) this.workPackage.getSources().get(0)).getImage();
			PlotTools.showHistogram(pi);
			this.updateParameterBlock();
		}
		if ("histonew".equals(e.getActionCommand())) { // button
			PlanarImage pi = Application.getManager().getPreviewImage();
			if (pi != null) PlotTools.showHistogram(pi);
			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	// ------------------------------------------------------------------------------------------------
	@Override
	public void stateChanged(ChangeEvent e) {
		Object obE = e.getSource();
		if (obE == jSpinnerOffSetLow) {
			offSetLow = ((Number) jSpinnerOffSetLow.getValue()).floatValue();
//				jSpinnerOffSetLow.setToolTipText("Offset low: " + (offSetLow / 10d) + " %");
		}
		if (obE == jSpinnerOffSetHigh) {
			offSetHigh = ((Number) jSpinnerOffSetHigh.getValue()).floatValue();
//				jSpinnerOffSetHigh.setToolTipText("Offset high: " + (offSetHigh / 10d) + "%");
		}
		if ((offSetLow > 0) || (offSetHigh > 0)) {
			jCheckBoxBandCombine.setEnabled(true);
		} else {
			jCheckBoxBandCombine.setEnabled(false);
		}
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}

	private JPanel getJPanelMethod() {
		if (jPanelMethod == null) {
			jPanelMethod = new JPanel();
			jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
			//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelMethod.add(getJRadioButtonMenuButtMethodRescale());
			jPanelMethod.add(getJRadioButtonMenuButtMethodEqual());
			jPanelMethod.add(getJRadioButtonMenuButtMethodNorm());
			jPanelMethod.add(getJRadioButtonMenuButtMethodPiece());
		
			this.setButtonGroupMethod();
		}
		return jPanelMethod ;
	}
	private JPanel getPnlOffset() {
		if (pnlOffset == null) {
			pnlOffset = new JPanel();
			pnlOffset.setLayout(new BoxLayout(pnlOffset, BoxLayout.Y_AXIS));	
			pnlOffset.setBorder(new TitledBorder(null, "Offset", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			pnlOffset.add(getJSpinnerOffSetLow());
			pnlOffset.add(getJSpinnerOffSetHigh());
		}
		return pnlOffset;
	}
}// END
