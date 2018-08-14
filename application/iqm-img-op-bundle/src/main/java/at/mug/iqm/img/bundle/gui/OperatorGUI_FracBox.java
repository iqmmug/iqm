package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracBox.java
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.renderable.ParameterBlock;
import java.text.DecimalFormat;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
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
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracBoxDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 11
 */
public class OperatorGUI_FracBox extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1647744384994979397L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_FracBox.class);

	private ParameterBlockIQM pb = null;

	private int numBoxes = 0;

	private JPanel   jPanelNumBoxes   = null;
	private JLabel   jLabelNumBoxes   = null;
	private JSpinner jSpinnerNumBoxes = null;

	private JRadioButton buttDBC           = null;
	private JRadioButton buttRDBC          = null;
	private JPanel       jPanelGreyMode    = null;
	private ButtonGroup  buttGroupGreyMode = null;

	private JPanel   regressionPanel  = null;;
	private JPanel   jPanelRegStart   = null;
	private JLabel   jLabelRegStart   = null;
	private JSpinner jSpinnerRegStart = null;
	private JPanel   jPanelRegEnd     = null;
	private JLabel   jLabelRegEnd     = null;
	private JSpinner jSpinnerRegEnd   = null;

	private JCheckBox jCheckBoxShowPlot           = null;
	private JCheckBox jCheckBoxDeleteExistingPlot = null;
	
	private JPanel    plotOptionsPanel;
	private JPanel    boxOptionsPanel;

	/**
	 * constructor
	 */
	public OperatorGUI_FracBox() {

		logger.debug("Now initializing...");

		this.setOpName(new IqmOpFracBoxDescriptor().getName());

		this.initialize();

		this.setTitle("Fractal Box Dimension");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.box.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());
		GridBagConstraints gbc_boxOptionsPanel = new GridBagConstraints();
		gbc_boxOptionsPanel.fill = GridBagConstraints.BOTH;
		gbc_boxOptionsPanel.gridx = 0;
		gbc_boxOptionsPanel.gridy = 0;
		getOpGUIContent().add(getBoxOptionsPanel(), gbc_boxOptionsPanel);
		this.getOpGUIContent().add(getRegressionPanel(), getGridBagConstraintsRegression());
		GridBagConstraints gbc_plotOptionsPanel = new GridBagConstraints();
		gbc_plotOptionsPanel.fill = GridBagConstraints.BOTH;
		gbc_plotOptionsPanel.gridx = 0;
		gbc_plotOptionsPanel.gridy = 2;
		getOpGUIContent().add(getPlotOptionsPanel(), gbc_plotOptionsPanel);

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		pb.setParameter("NumBoxes", ((Number) jSpinnerNumBoxes.getValue()).intValue());

		if (buttDBC.isSelected()) pb.setParameter("GreyMode", 0);
		if (buttRDBC.isSelected())pb.setParameter("GreyMode", 1);

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

		jSpinnerNumBoxes.removeChangeListener(this);
		jSpinnerNumBoxes.setValue(pb.getIntParameter("NumBoxes"));
		jSpinnerNumBoxes.addChangeListener(this);

		if (pb.getIntParameter("GreyMode") == 0) buttDBC.setSelected(true);
		if (pb.getIntParameter("GreyMode") == 1) buttRDBC.setSelected(true);

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
	public static synchronized int getMaxBoxNumber(int width, int height) { // inclusive original image
		float boxWidth = 1f;
		int number = 1; // inclusive original image
		while ((boxWidth <= width) && (boxWidth <= height)) {
			boxWidth = boxWidth * 2;
			// System.out.println("OperatorGUI_FracBox: newBoxWidth: " +
			// newBoxWidth);
			number = number + 1;
		}
		return number - 1;
	}

	// ---------------------------------------------------------------------------------------------
	private GridBagConstraints getGridBagConstraintsRegression() {
		GridBagConstraints gbc_regressionPanel = new GridBagConstraints();
		gbc_regressionPanel.gridx = 0;
		gbc_regressionPanel.gridy = 1;
		// gridBagConstraintsRegression.fill = GridBagConstraints.BOTH;
		return gbc_regressionPanel;
	}

	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");

		IqmDataBox box = (IqmDataBox) this.workPackage.getSources().get(0);
		PlanarImage pi = box.getImage();

		int numBands = pi.getNumBands();
		int width = pi.getWidth();
		int height = pi.getHeight();
		numBoxes = getMaxBoxNumber(width, height);
		// String type = IqmTools.getImgTyp(pi);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

		// Set up the parameters for the Histogram object.
		int[] bins = { (int) typeGreyMax + 1, (int) typeGreyMax + 1,(int) typeGreyMax + 1 }; // The number of bins e.g. {256, 256, 256}
		double[] lows = { 0.0D, 0.0D, 0.0D }; // The low incl.value e.g. {0.0D, 0.0D, 0.0D}
		double[] highs = { typeGreyMax + 1, typeGreyMax + 1, typeGreyMax + 1 }; // The high excl.value e.g. {256.0D, 256.0D, 256.0D}
		// Create the parameter block.
		ParameterBlock pbHisto = new ParameterBlock();
		pbHisto.addSource(pi); // Source image
		pbHisto.add(null); // ROI
		pbHisto.add(1); // Horizontal sampling
		pbHisto.add(1); // Vertical sampling
		pbHisto.add(bins); // Number of Bins
		pbHisto.add(lows); // Lowest inclusive values
		pbHisto.add(highs); // Highest exclusive values
		// Perform the histogram operation.
		RenderedOp rOp = JAI.create("Histogram", pbHisto, null);
		// Retrieve the histogram data.
		Histogram histogram = (Histogram) rOp.getProperty("histogram");
		// System.out.println("OperatorGUI_FracBox: (int)typeGreyMax: " +
		// (int)typeGreyMax);

		buttDBC.setEnabled(false);
		buttRDBC.setEnabled(false);

		for (int b = 0; b < numBands; b++) {
			double totalGrey = histogram.getSubTotal(b, 1, (int) typeGreyMax); // without
																				// 0!
			double totalBinary = histogram.getSubTotal(b, (int) typeGreyMax,
					(int) typeGreyMax);

			if (totalBinary != totalGrey) { // is not Binary
				buttDBC.setEnabled(true);
				buttRDBC.setEnabled(true);
			}

		}

		SpinnerModel sModel = new SpinnerNumberModel(numBoxes, 3, numBoxes, 1); // init, min, max, step
		jSpinnerNumBoxes.removeChangeListener(this);
		jSpinnerNumBoxes.setModel(sModel);
		jSpinnerNumBoxes.setValue(numBoxes);
		jSpinnerRegEnd.removeChangeListener(this);
		jSpinnerRegEnd.setValue(numBoxes);
		jSpinnerRegEnd.addChangeListener(this);
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumBoxes.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setEditable(true);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
		jSpinnerNumBoxes.addChangeListener(this);

		this.updateParameterBlock();
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelNumBoxes
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumBoxes() {
		if (jPanelNumBoxes == null) {
			jPanelNumBoxes = new JPanel();
			jPanelNumBoxes.setLayout(new BorderLayout());
			jLabelNumBoxes = new JLabel("Box number: ");
			// jLabelNumBoxes.setPreferredSize(new Dimension(70, 22));
			jLabelNumBoxes.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumBoxes = new JSpinner(sModel);
			// jSpinnerNumBoxes = new JSpinner();
			jSpinnerNumBoxes.setPreferredSize(new Dimension(60, 20));
			jSpinnerNumBoxes.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumBoxes.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelNumBoxes.add(jLabelNumBoxes, BorderLayout.NORTH);
			jPanelNumBoxes.add(jSpinnerNumBoxes, BorderLayout.CENTER);
		}
		return jPanelNumBoxes;
	}

	// -------------------------------------------------------------------------------------------

	private void setButtonGroupGreyMode() {
		buttGroupGreyMode = new ButtonGroup();
		buttGroupGreyMode.add(buttDBC);
		buttGroupGreyMode.add(buttRDBC);
	}

	// -----------------------------------------------------------------------------------------

	/**
	 * This method initializes jJPanelRegression
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRegressionPanel() {
		if (regressionPanel == null) {
			regressionPanel = new JPanel();
			regressionPanel.setBorder(new TitledBorder(null, "Regression", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			regressionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			regressionPanel.add(getJPanelRegStart());
			regressionPanel.add(getJPanelRegEnd());
		}
		return regressionPanel;
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
			jLabelRegStart.setHorizontalAlignment(SwingConstants.RIGHT);
			jSpinnerRegStart = new JSpinner(new SpinnerNumberModel());
			jSpinnerRegStart.setPreferredSize(new Dimension(60, 20));
			jSpinnerRegStart.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegStart.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelRegStart.add(jLabelRegStart);
			jPanelRegStart.add(jSpinnerRegStart);
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
			jLabelRegEnd.setHorizontalAlignment(SwingConstants.RIGHT);
			jSpinnerRegEnd = new JSpinner(new SpinnerNumberModel());
			jSpinnerRegEnd.setPreferredSize(new Dimension(60, 20));
			jSpinnerRegEnd.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegEnd.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelRegEnd.add(jLabelRegEnd);
			jPanelRegEnd.add(jSpinnerRegEnd);
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

		numBoxes = ((Number) jSpinnerNumBoxes.getValue()).intValue();
		int regStart = ((Number) jSpinnerRegStart.getValue()).intValue();
		int regEnd = ((Number) jSpinnerRegEnd.getValue()).intValue();

		jSpinnerNumBoxes.removeChangeListener(this);
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);

		if (jSpinnerNumBoxes == e.getSource()) {
			// if(regEnd > numBoxes){
			jSpinnerRegEnd.setValue(numBoxes);
			regEnd = numBoxes;
			// }
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 2);
				regStart = regEnd - 2;
			}
		}
		if (jSpinnerRegEnd == e.getSource()) {
			if (regEnd > numBoxes) {
				jSpinnerRegEnd.setValue(numBoxes);
				regEnd = numBoxes;
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

		jSpinnerNumBoxes.addChangeListener(this);
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
			plotOptionsPanel.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Plot output options",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			plotOptionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			plotOptionsPanel.add(getJCheckBoxShowPlot());
			plotOptionsPanel.add(getJCheckBoxDeleteExistingPlot());
		}
		return plotOptionsPanel;
	}

	private JPanel getBoxOptionsPanel() {
		if (boxOptionsPanel == null) {
			boxOptionsPanel = new JPanel();
			boxOptionsPanel.setBorder(new TitledBorder(UIManager
					.getBorder("TitledBorder.border"), "Box counting options",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0,0, 0)));
			boxOptionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			boxOptionsPanel.add(getJPanelNumBoxes());

			buttDBC = new JRadioButton();
			buttDBC.setText("DBC");
			buttDBC.setToolTipText("Differential BC for grey scale images");
			buttDBC.addActionListener(this);
			buttDBC.setActionCommand("parameter");

			buttRDBC = new JRadioButton();
			buttRDBC.setText("RDBC");
			buttRDBC.setToolTipText("Relative DBC for grey scale images");
			buttRDBC.addActionListener(this);
			buttRDBC.setActionCommand("parameter");

			jPanelGreyMode = new JPanel();
			jPanelGreyMode.setLayout(new BoxLayout(jPanelGreyMode, BoxLayout.Y_AXIS));
			jPanelGreyMode.add(buttDBC);
			jPanelGreyMode.add(buttRDBC);
			boxOptionsPanel.add(jPanelGreyMode);
			this.setButtonGroupGreyMode(); // Grouping of JRadioButtons
		}
		return boxOptionsPanel;
	}
}// END
