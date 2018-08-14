package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracFFT.java
 * 
 * $Id$
 * $HeadURL$
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.jai.PlanarImage;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
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

import org.apache.log4j.Logger;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracFFTDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2011 02
 */
public class OperatorGUI_FracFFT extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3924572224015036368L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_FracFFT.class);

	private ParameterBlockIQM pb = null;

	private int numMaxK = 100; //dummy value

	private JPanel   jPanelNumMaxK   = null;
	private JLabel   jLabelNumMaxK   = null;
	private JSpinner jSpinnerNumMaxK = null;

	private JRadioButton buttDt1         = null;
	private JRadioButton buttDt2         = null;
	private JPanel       jPanelTopDim    = null;
	private ButtonGroup  buttGroupTopDim = null;

	private JPanel   jPanelRegression = null;;
	private JPanel   jPanelRegStart   = null;
	private JLabel   jLabelRegStart   = null;
	private JSpinner jSpinnerRegStart = null;
	private JPanel   jPanelRegEnd     = null;
	private JLabel   jLabelRegEnd     = null;
	private JSpinner jSpinnerRegEnd   = null;

	private JCheckBox jCheckBoxShowPlot           = null;
	private JCheckBox jCheckBoxDeleteExistingPlot = null;
	private JPanel    plotOptionsPanel;
	private JPanel    fftOptionsPanel;

	/**
	 * constructor
	 */
	public OperatorGUI_FracFFT() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpFracFFTDescriptor().getName());

		this.initialize();

		this.setTitle("Fractal FFT Dimension");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.fft.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());
		this.getOpGUIContent().add(getFFtOptionsPanel(),  getGBC_FFTOptionsPanel());	
		this.getOpGUIContent().add(getJPanelRegression(), getGBC_Regression());
		this.getOpGUIContent().add(getPlotOptionsPanel(), getGBC_PlotOptionsPanel());

		this.pack();
	}
	private GridBagConstraints getGBC_FFTOptionsPanel() {
		GridBagConstraints gbc_fftOptionsPanel = new GridBagConstraints();
		gbc_fftOptionsPanel.fill = GridBagConstraints.BOTH;
		gbc_fftOptionsPanel.gridx = 0;
		gbc_fftOptionsPanel.gridy = 0;
		gbc_fftOptionsPanel.insets = new Insets(5, 0, 0, 0);
		return gbc_fftOptionsPanel;
	}
	private GridBagConstraints getGBC_Regression() {
		GridBagConstraints gridBagConstraintsRegression = new GridBagConstraints();
		gridBagConstraintsRegression.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraintsRegression.gridx = 0;
		gridBagConstraintsRegression.gridy = 1;
		// gridBagConstraintsRegression.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsRegression;
	}
	private GridBagConstraints getGBC_PlotOptionsPanel() {
		GridBagConstraints gbc_plotOptionsPanel = new GridBagConstraints();
		gbc_plotOptionsPanel.fill = GridBagConstraints.BOTH;
		gbc_plotOptionsPanel.gridx = 0;
		gbc_plotOptionsPanel.gridy = 2;
		return gbc_plotOptionsPanel;
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		pb.setParameter("NumMaxK", ((Number) jSpinnerNumMaxK.getValue()).intValue());

		if (buttDt1.isSelected()) pb.setParameter("TopDim", 0);
		if (buttDt2.isSelected()) pb.setParameter("TopDim", 1);

		pb.setParameter("RegStart", ((Number) jSpinnerRegStart.getValue()).intValue());
		pb.setParameter("RegEnd",   ((Number) jSpinnerRegEnd.getValue()).intValue());

		if (jCheckBoxShowPlot.isSelected())  pb.setParameter("ShowPlot", 1);
		if (!jCheckBoxShowPlot.isSelected()) pb.setParameter("ShowPlot", 0);
		if (jCheckBoxDeleteExistingPlot.isSelected())  pb.setParameter("DeleteExistingPlot", 1);
		if (!jCheckBoxDeleteExistingPlot.isSelected()) pb.setParameter("DeleteExistingPlot", 0);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		jSpinnerNumMaxK.removeChangeListener(this);
		jSpinnerNumMaxK.setValue(pb.getIntParameter("NumMaxK"));
		jSpinnerNumMaxK.addChangeListener(this);

		if (pb.getIntParameter("TopDim") == 0) buttDt1.setSelected(true);
		if (pb.getIntParameter("TopDim") == 1) buttDt2.setSelected(true);

		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegStart.setValue(pb.getIntParameter("RegStart"));
		jSpinnerRegStart.addChangeListener(this);

		jSpinnerRegEnd.removeChangeListener(this);
		jSpinnerRegEnd.setValue(pb.getIntParameter("RegEnd"));
		jSpinnerRegEnd.addChangeListener(this);

		if (pb.getIntParameter("ShowPlot") == 0) jCheckBoxShowPlot.setSelected(false);
		if (pb.getIntParameter("ShowPlot") == 1) jCheckBoxShowPlot.setSelected(true);
		
		if (pb.getIntParameter("DeleteExistingPlot") == 0) jCheckBoxDeleteExistingPlot.setSelected(false);
		if (pb.getIntParameter("DeleteExistingPlot") == 1) jCheckBoxDeleteExistingPlot.setSelected(true);

	}

	// ---------------------------------------------------------------------------------------------
	public static synchronized int getMaxK(int width, int height) { //
		// DFT in JAI expands each length to the next higher multiple of 2
		int maxK = 2; //
		int dftWidth = 2; // initialize
		int dftHeight = 2;
		while (dftWidth < width) {
			dftWidth = dftWidth * 2;
		}
		while (dftHeight < height) {
			dftHeight = dftHeight * 2;
		}
		// only the upper half is taken as information for k
		int cropHeight = dftHeight / 2;
		int cropWidth = dftWidth / 2;
		// maxK = dftWidth*cropHeight;
		maxK = cropWidth * cropHeight; // because maximum is in the middle
		return maxK;
	}

	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method updates the GUI if needed This method overrides OperationGUI
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		PlanarImage pi = ((IqmDataBox) this.pb.getSources().firstElement()).getImage();
		int width      = pi.getWidth();
		int height     = pi.getHeight();
		numMaxK        = getMaxK(width, height);	
		//System.out.println("OperatorGUI_FracFFT: numberMax: " + numMaxK);
	
		jSpinnerNumMaxK.removeChangeListener(this);
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);
		
		SpinnerModel sModel1 = new SpinnerNumberModel(numMaxK, 2, numMaxK, 1); // init, min, max, step
		jSpinnerNumMaxK.setModel(sModel1);
		jSpinnerNumMaxK.setEditor(new JSpinner.NumberEditor(jSpinnerNumMaxK, "#"));  //"#0.00
		((JSpinner.NumberEditor) jSpinnerNumMaxK.getEditor()).getTextField().setEditable(true);
		
		SpinnerModel sModel2 = new SpinnerNumberModel(1, 1, numMaxK - 1, 1); // init, min, max, step
		jSpinnerRegStart.setModel(sModel2);
		jSpinnerRegStart.setEditor(new JSpinner.NumberEditor(jSpinnerRegStart, "#"));  //"#0.00
		((JSpinner.NumberEditor) jSpinnerRegStart.getEditor()).getTextField().setEditable(true);
		
		SpinnerNumberModel sModel3 = new SpinnerNumberModel(numMaxK, 2, numMaxK, 1); // init, min, max, step
		jSpinnerRegEnd.setModel(sModel3);
		jSpinnerRegEnd.setEditor(new JSpinner.NumberEditor(jSpinnerRegEnd, "#"));  //"#0.00
		((JSpinner.NumberEditor) jSpinnerRegEnd.getEditor()).getTextField().setEditable(true);
		
		jSpinnerNumMaxK.addChangeListener(this);
		jSpinnerRegStart.addChangeListener(this);
		jSpinnerRegEnd.addChangeListener(this);
			
		this.updateParameterBlock();
		this.pack();
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelNumMaxK
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumMaxK() {
		if (jPanelNumMaxK == null) {
			jPanelNumMaxK = new JPanel();
			jPanelNumMaxK.setLayout(new BorderLayout());
			jLabelNumMaxK = new JLabel("Maximal k: ");
			// jLabelNumMaxK.setPreferredSize(new Dimension(70, 22));
			jLabelNumMaxK.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(2, 2, numMaxK, 1); // init, min, max, step
			jSpinnerNumMaxK = new JSpinner(sModel);
			//jSpinnerNumMaxK.setPreferredSize(new Dimension(60, 24));
			jSpinnerNumMaxK.setEditor(new JSpinner.NumberEditor(jSpinnerNumMaxK, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerNumMaxK.getEditor()).getTextField().setEditable(true);
			jSpinnerNumMaxK.addChangeListener(this);

			jPanelNumMaxK.add(jLabelNumMaxK, BorderLayout.NORTH);
			jPanelNumMaxK.add(jSpinnerNumMaxK, BorderLayout.CENTER);
		}
		return jPanelNumMaxK;
	}

	private void setButtonGroupTopDim() {
		buttGroupTopDim = new ButtonGroup();
		buttGroupTopDim.add(buttDt1);
		buttGroupTopDim.add(buttDt2);
	}

	// -----------------------------------------------------------------------------------------

	/**
	 * This method initializes jJPanelRegression
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRegression() {
		if (jPanelRegression == null) {
			jPanelRegression = new JPanel();
			jPanelRegression.setLayout(new BoxLayout(jPanelRegression, BoxLayout.X_AXIS));
			jPanelRegression.setBorder(new TitledBorder(null, "Regression", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			//jPanelRegression.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelRegression.add(getJPanelRegStart());
			jPanelRegression.add(getJPanelRegEnd());
		}
		return jPanelRegression;
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelRegStart
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRegStart() {
		if (jPanelRegStart == null) {
			jPanelRegStart = new JPanel();
			jPanelRegStart.setLayout(new FlowLayout());
			jLabelRegStart = new JLabel("Start:");
			// jLabelRegStart.setPreferredSize(new Dimension(40, 22));
			jLabelRegStart.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, numMaxK, 1); // init, min, max, step
			jSpinnerRegStart = new JSpinner(sModel);
			//jSpinnerRegStart.setPreferredSize(new Dimension(60, 24));
			jSpinnerRegStart.setEditor(new JSpinner.NumberEditor(jSpinnerRegStart, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerRegStart.getEditor()).getTextField().setEditable(true);
			jSpinnerRegStart.addChangeListener(this);
			jPanelRegStart.add(jLabelRegStart,   BorderLayout.WEST);
			jPanelRegStart.add(jSpinnerRegStart, BorderLayout.CENTER);
		}
		return jPanelRegStart;
	}

	/**
	 * This method initializes jJPanelRegEnd
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRegEnd() {
		if (jPanelRegEnd == null) {
			jPanelRegEnd = new JPanel();
			jPanelRegEnd.setLayout(new FlowLayout());
			jLabelRegEnd = new JLabel("End:");
			// jLabelRegEnd.setPreferredSize(new Dimension(40, 22));
			jLabelRegEnd.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(numMaxK, 2, numMaxK, 1); // init, min, max, step
			jSpinnerRegEnd = new JSpinner(sModel);
			//jSpinnerRegEnd.setPreferredSize(new Dimension(60, 24));
			jSpinnerRegEnd.setEditor(new JSpinner.NumberEditor(jSpinnerRegEnd, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerRegEnd.getEditor()).getTextField().setEditable(true);
			jSpinnerRegEnd.addChangeListener(this);
			jPanelRegEnd.add(jLabelRegEnd,   BorderLayout.WEST);
			jPanelRegEnd.add(jSpinnerRegEnd, BorderLayout.CENTER);
		}
		return jPanelRegEnd;
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxShowPlot
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxShowPlot() {
		if (jCheckBoxShowPlot == null) {
			jCheckBoxShowPlot = new JCheckBox();
			jCheckBoxShowPlot.setText("ShowPlot");
			jCheckBoxShowPlot.addActionListener(this);
			jCheckBoxShowPlot.setActionCommand("parameter");
		}
		return jCheckBoxShowPlot;
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxDeleteExistingPlot
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxDeleteExistingPlot() {
		if (jCheckBoxDeleteExistingPlot == null) {
			jCheckBoxDeleteExistingPlot = new JCheckBox();
			jCheckBoxDeleteExistingPlot.setText("DeleteExistingPlot");
			jCheckBoxDeleteExistingPlot.addActionListener(this);
			jCheckBoxDeleteExistingPlot.setActionCommand("parameter");
		}
		return jCheckBoxDeleteExistingPlot;
	}

	// ----------------------------------------------------------------------------------------------------------
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

	// -------------------------------------------------------------------------------------------------
	@Override
	public void stateChanged(ChangeEvent e) {

		numMaxK      = ((Number) jSpinnerNumMaxK.getValue()).intValue();
		int regStart = ((Number) jSpinnerRegStart.getValue()).intValue();
		int regEnd   = ((Number) jSpinnerRegEnd.getValue()).intValue();

		jSpinnerNumMaxK.removeChangeListener(this);
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);

		if (jSpinnerNumMaxK == e.getSource()) {
			if (regEnd > numMaxK) {
				jSpinnerRegEnd.setValue(numMaxK);
				regEnd = numMaxK;
			}
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 2);
				regStart = regEnd - 2;
			}
		}
		if (jSpinnerRegEnd == e.getSource()) {
			if (regEnd > numMaxK) {
				jSpinnerRegEnd.setValue(numMaxK);
				regEnd = numMaxK;
			}
			if (regEnd <= (regStart + 1)) {
				jSpinnerRegEnd.setValue(regStart + 2);
				regEnd = regStart + 2;
			}
		}
		if (jSpinnerRegStart == e.getSource()) {
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 2);
				regStart = regEnd - 2;
			}
		}

		jSpinnerNumMaxK.addChangeListener(this);
		jSpinnerRegStart.addChangeListener(this);
		jSpinnerRegEnd.addChangeListener(this);

		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	private JPanel getPlotOptionsPanel() {
		if (plotOptionsPanel == null) {
			plotOptionsPanel = new JPanel();
			//plotOptionsPanel.setLayout(new BoxLayout(plotOptionsPanel, BoxLayout.X_AXIS));
			plotOptionsPanel.setBorder(new TitledBorder(UIManager .getBorder("TitledBorder.border"), "Plot output options",
					                  TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			plotOptionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			plotOptionsPanel.add(getJCheckBoxShowPlot());
			plotOptionsPanel.add(getJCheckBoxDeleteExistingPlot());
		}
		return plotOptionsPanel;
	}

	private JPanel getFFtOptionsPanel() {
		if (fftOptionsPanel == null) {
			fftOptionsPanel = new JPanel();
			//fftOptionsPanel.setLayout(new BoxLayout(fftOptionsPanel, BoxLayout.X_AXIS));
			fftOptionsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "FFT options",
					                 TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			fftOptionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			fftOptionsPanel.add(getJPanelNumMaxK());

			buttDt1 = new JRadioButton();
			buttDt1.setText("Dt = 1");
			buttDt1.setToolTipText("Topological Dimension = 1");
			buttDt1.addActionListener(this);
			buttDt1.setActionCommand("parameter");

			buttDt2 = new JRadioButton();
			buttDt2.setText("Dt = 2");
			buttDt2.setToolTipText("Topological Dimension = 2");
			buttDt2.addActionListener(this);
			buttDt2.setActionCommand("parameter");

			jPanelTopDim = new JPanel();
			jPanelTopDim.setLayout((LayoutManager) new BoxLayout(jPanelTopDim, BoxLayout.Y_AXIS));
			jPanelTopDim.add(buttDt1);
			jPanelTopDim.add(buttDt2);
			fftOptionsPanel.add(jPanelTopDim);

			this.setButtonGroupTopDim(); // Grouping of JRadioButtons
		}
		return fftOptionsPanel;
	}
}// END
