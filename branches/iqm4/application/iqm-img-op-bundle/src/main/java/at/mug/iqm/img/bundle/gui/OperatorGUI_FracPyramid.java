package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracPyramid.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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
import java.awt.Insets;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpFracPyramidDescriptor;

/**
 * @author Ahammer, Kainz, Mayrhofer-R.
 * @since  2009 07
 * @update 2015 06 HA added Information Dimension Option
 * @update 2016 01 MMR added new Option for PDM
 * @update 2016 02 HA added new option KC
 */
public class OperatorGUI_FracPyramid extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8760249637957968294L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_FracPyramid.class);

	private ParameterBlockIQM pb = null;

	private int numPyrImgs = 0;

	private JPanel   jPanelNumPyrImgs   = null;
	private JLabel   jLabelNumPyrImgs   = null;
	private JSpinner jSpinnerNumPyrImgs = null;

	private JRadioButton buttNN        = null; // nearest neighbor filtreredsubsample
	private JRadioButton buttBL        = null; // bilinear
	private JRadioButton buttBC        = null; // bicubic
	private JRadioButton buttBC2       = null; // bicubic2
	private JRadioButton buttAV        = null; // average subsampleaverage
	private JPanel       jPanelIntP    = null;
	private ButtonGroup  buttGroupIntP = null;

	private JRadioButton buttPGM           = null; // PGM (Gradient Method, according to MMR, Chinga)
	private JRadioButton buttPDM           = null; // PDM (Differences Method, according to MMR)
	private JRadioButton buttBlanket       = null; // Blanket Method
	private JRadioButton buttAllometric    = null; // Allometric scaling according to B.West
	private JRadioButton buttInformation   = null; //Information dimension
	private JRadioButton buttKC            = null; //Kolmogorov Complexity
	
	private JPanel       jPanelGreyMode    = null;
	private ButtonGroup  buttGroupGreyMode = null;

	private JPanel   jPanelRegression = null;
	private JPanel   jPanelRegStart   = null;
	private JLabel   jLabelRegStart   = null;
	private JSpinner jSpinnerRegStart = null;
	private JPanel   jPanelRegEnd     = null;
	private JLabel   jLabelRegEnd     = null;
	private JSpinner jSpinnerRegEnd   = null;

	private JCheckBox jCheckBoxShowPlot           = null;
	private JCheckBox jCheckBoxDeleteExistingPlot = null;
	private JPanel    plotOptionsPanel;
	private JLabel    lblGreymode;
	private JPanel    panel;
	private JPanel    pyramidOptionsPanel;

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		pb.setParameter("NumPyrImgs",
				((Number) jSpinnerNumPyrImgs.getValue()).intValue());

		if (buttNN.isSelected()) pb.setParameter("Interpolation", 0);
		if (buttBL.isSelected()) pb.setParameter("Interpolation", 1);
		if (buttBC.isSelected()) pb.setParameter("Interpolation", 2);
		if (buttBC2.isSelected())pb.setParameter("Interpolation", 3);
		if (buttAV.isSelected()) pb.setParameter("Interpolation", 4);

		if (buttPGM.isSelected())         pb.setParameter("GreyMode", 0);
		if (buttBlanket.isSelected())     pb.setParameter("GreyMode", 1);
		if (buttAllometric.isSelected())  pb.setParameter("GreyMode", 2);
		if (buttInformation.isSelected()) pb.setParameter("GreyMode", 3);
		if (buttPDM.isSelected())         pb.setParameter("GreyMode", 4);
		if (buttKC.isSelected())          pb.setParameter("GreyMode", 5);

		pb.setParameter("RegStart", ((Number) jSpinnerRegStart.getValue()).intValue());
		pb.setParameter("RegEnd",   ((Number) jSpinnerRegEnd.getValue()).intValue());

		if (jCheckBoxShowPlot.isSelected()) pb.setParameter("ShowPlot", 1);
		if (!jCheckBoxShowPlot.isSelected())pb.setParameter("ShowPlot", 0);
		if (jCheckBoxDeleteExistingPlot.isSelected()) pb.setParameter("DeleteExistingPlot", 1);
		if (!jCheckBoxDeleteExistingPlot.isSelected())pb.setParameter("DeleteExistingPlot", 0);

	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		jSpinnerNumPyrImgs.removeChangeListener(this);
		jSpinnerNumPyrImgs.setValue(pb.getIntParameter("NumPyrImgs"));
		jSpinnerNumPyrImgs.addChangeListener(this);

		if (pb.getIntParameter("Interpolation") == 0)
			buttNN.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 1)
			buttBL.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 2)
			buttBC.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 3)
			buttBC2.setSelected(true);
		if (pb.getIntParameter("Interpolation") == 4)
			buttAV.setSelected(true);

		if (pb.getIntParameter("GreyMode") == 0) buttPGM.setSelected(true);
		if (pb.getIntParameter("GreyMode") == 1) buttBlanket.setSelected(true);
		if (pb.getIntParameter("GreyMode") == 2) buttAllometric.setSelected(true);
		if (pb.getIntParameter("GreyMode") == 3) buttInformation.setSelected(true);
		if (pb.getIntParameter("GreyMode") == 4) buttPDM.setSelected(true);
		if (pb.getIntParameter("GreyMode") == 5) buttKC.setSelected(true);

		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegStart.setValue(pb.getIntParameter("RegStart"));
		jSpinnerRegStart.addChangeListener(this);

		jSpinnerRegEnd.removeChangeListener(this);
		jSpinnerRegEnd.setValue(pb.getIntParameter("RegEnd"));
		jSpinnerRegEnd.addChangeListener(this);

		if (pb.getIntParameter("ShowPlot") == 0)
			jCheckBoxShowPlot.setSelected(false);
		if (pb.getIntParameter("ShowPlot") == 1)
			jCheckBoxShowPlot.setSelected(true);
		if (pb.getIntParameter("DeleteExistingPlot") == 0)
			jCheckBoxDeleteExistingPlot.setSelected(false);
		if (pb.getIntParameter("DeleteExistingPlot") == 1)
			jCheckBoxDeleteExistingPlot.setSelected(true);

	}

	/**
	 * constructor
	 */
	public OperatorGUI_FracPyramid() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpFracPyramidDescriptor().getName());

		this.initialize();

		this.setTitle("Fractal Pyramid Dimension");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.pyramid.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());
		this.getOpGUIContent().add(getPyramidOptionsPanel(), getGridBagConstraints_PyramidOptionsPanel());
		this.getOpGUIContent().add(getJPanelRegression(),    getGridBagConstraints_Regression());
		this.getOpGUIContent().add(getPlotOptionsPanel(),    getGridBagConstraints_plotOptionsPanel());
	
		this.pack();
	}

	public static synchronized int getMaxPyramidNumber(int width, int height) { // inclusive original image
		float oldWidth = width;
		float oldHeight = height;
		int number = 1; // inclusive original image
		boolean abbruch = false;
		while (!abbruch) {
			float newWidth = oldWidth / 2;
			float newHeight = oldHeight / 2;
			if ((newWidth < 1.0) || (newHeight < 1.0)) {
				abbruch = true;
			} else {
				oldWidth = newWidth;
				oldHeight = newHeight;
				// System.out.println("OperatorGUI_FracPyramid: newWidth: " + newWidth);
				// System.out.println("OperatorGUI_FracPyramid: newHeight: " + newHeight);
				number = number + 1;
			}
		}
		return number;
	}

	private GridBagConstraints getGridBagConstraints_PyramidOptionsPanel(){
		GridBagConstraints gbc_pyramidOptionsPanel = new GridBagConstraints();
		//gbc_pyramidOptionsPanel.fill = GridBagConstraints.BOTH;
		gbc_pyramidOptionsPanel.gridx = 0;
		gbc_pyramidOptionsPanel.gridy = 0;
		gbc_pyramidOptionsPanel.insets = new Insets(5, 0, 0, 0); // top, left bottom right
		return gbc_pyramidOptionsPanel;
	}
	
	private GridBagConstraints getGridBagConstraints_Regression() {
		GridBagConstraints gridBagConstraintsRegression = new GridBagConstraints();
		gridBagConstraintsRegression.gridx = 0;
		gridBagConstraintsRegression.gridy = 1;
		//gridBagConstraintsRegression.fill = GridBagConstraints.BOTH;
		gridBagConstraintsRegression.fill = GridBagConstraints.HORIZONTAL;
		return gridBagConstraintsRegression;
	}
	
	private GridBagConstraints getGridBagConstraints_plotOptionsPanel(){
		GridBagConstraints gbc_plotOptionsPanel = new GridBagConstraints();
		gbc_plotOptionsPanel.gridx = 0;
		gbc_plotOptionsPanel.gridy = 2;
		gbc_plotOptionsPanel.fill = GridBagConstraints.HORIZONTAL;
		return gbc_plotOptionsPanel;
	}
	


	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method updates the GUI, if needed.
	 * 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		PlanarImage pi = ((IqmDataBox) this.workPackage.getSources().get(0)).getImage();
		int         numBands = pi.getNumBands();
		int         width    = pi.getWidth();
		int         height   = pi.getHeight();
		numPyrImgs = getMaxPyramidNumber(width, height);
		// String type = IqmTools.getImgTyp(pi);
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

		// Set up the parameters for the Histogram object.
		int[]    bins  = { (int) typeGreyMax + 1, (int) typeGreyMax + 1, (int) typeGreyMax + 1 }; // The number of bins e.g. {256, 256, 256}
		double[] lows  = { 0.0D, 0.0D, 0.0D }; // The low incl.value e.g. {0.0D, 0.0D, 0.0D}
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

		buttPGM.setEnabled(false);
		buttPDM.setEnabled(false);
		buttBlanket.setEnabled(false);
		buttAllometric.setEnabled(false);	
		buttInformation.setEnabled(false);	
		buttKC.setEnabled(false);	
		lblGreymode.setForeground(Color.GRAY);

		for (int b = 0; b < numBands; b++) {
			double totalGrey = histogram.getSubTotal(b, 1, (int) typeGreyMax); // without 0!
			double totalBinary = histogram.getSubTotal(b, (int) typeGreyMax, (int) typeGreyMax);

			if (totalBinary != totalGrey) { // is not Binary
				buttPGM.setEnabled(true);
				buttPDM.setEnabled(true);
				buttBlanket.setEnabled(true);
				buttAllometric.setEnabled(true);
				buttInformation.setEnabled(true);
				buttKC.setEnabled(true);
				lblGreymode.setForeground(Color.BLACK);
			}
		}
		SpinnerModel sModel = new SpinnerNumberModel(numPyrImgs, 3, numPyrImgs, 1); // init, min, max, step

		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegStart.setModel(new SpinnerNumberModel(1, 1, numPyrImgs - 1, 1));
		jSpinnerRegStart.addChangeListener(this);
		jSpinnerNumPyrImgs.removeChangeListener(this);
		jSpinnerNumPyrImgs.setModel(sModel);
		jSpinnerNumPyrImgs.setValue(numPyrImgs);
		jSpinnerRegEnd.removeChangeListener(this);
		jSpinnerRegEnd.setModel(new SpinnerNumberModel(numPyrImgs, 2, numPyrImgs, 1));
		jSpinnerRegEnd.addChangeListener(this);
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumPyrImgs.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setEditable(true);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
		jSpinnerNumPyrImgs.addChangeListener(this);

		this.updateParameterBlock();
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelNumPyrImgs
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumPyrImgs() {
		if (jPanelNumPyrImgs == null) {
			jPanelNumPyrImgs = new JPanel();
			jPanelNumPyrImgs.setLayout(new BorderLayout());
			jLabelNumPyrImgs = new JLabel("Image number: ");
			// jLabelNumPyrImgs.setPreferredSize(new Dimension(70, 22));
			jLabelNumPyrImgs.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumPyrImgs = new JSpinner(sModel);
			// jSpinnerNumPyrImgs = new JSpinner();
			jSpinnerNumPyrImgs.setPreferredSize(new Dimension(60, 20));
			jSpinnerNumPyrImgs.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumPyrImgs.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelNumPyrImgs.add(jLabelNumPyrImgs, BorderLayout.NORTH);
			jPanelNumPyrImgs.add(jSpinnerNumPyrImgs, BorderLayout.CENTER);
		}
		return jPanelNumPyrImgs;
	}

	private void setButtonGroupIntP() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupIntP = new ButtonGroup();
		buttGroupIntP.add(buttNN);
		buttGroupIntP.add(buttBL);
		buttGroupIntP.add(buttBC);
		buttGroupIntP.add(buttBC2);
		buttGroupIntP.add(buttAV);
	}

	private void setButtonGroupGreyMode() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupGreyMode = new ButtonGroup();
		buttGroupGreyMode.add(buttPDM);
		buttGroupGreyMode.add(buttPGM);
		buttGroupGreyMode.add(buttBlanket);
		buttGroupGreyMode.add(buttAllometric);
		buttGroupGreyMode.add(buttInformation);
		buttGroupGreyMode.add(buttKC);	
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
			jPanelRegression.setBorder(new TitledBorder(null, "Regression", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jPanelRegression.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
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
			SpinnerModel sModel = new SpinnerNumberModel(); // init, min, max, step
			jSpinnerRegStart = new JSpinner(sModel);
			// jSpinnerRegStart = new JSpinner();
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
			// jLabelRegEnd.setPreferredSize(new Dimension(40, 22));
			jLabelRegEnd.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(); // init, min, max, step
			jSpinnerRegEnd = new JSpinner(sModel);
			// jSpinnerRegEnd = new JSpinner();
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

		numPyrImgs = ((Number) jSpinnerNumPyrImgs.getValue()).intValue();
		int regStart = ((Number) jSpinnerRegStart.getValue()).intValue();
		int regEnd = ((Number) jSpinnerRegEnd.getValue()).intValue();

		jSpinnerNumPyrImgs.removeChangeListener(this);
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);

		if (jSpinnerNumPyrImgs == e.getSource()) {
			// if(regEnd > numPyrImgs){
			jSpinnerRegEnd.setValue(numPyrImgs);
			regEnd = numPyrImgs;
			// }
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 2);
				regStart = regEnd - 2;
			}
		}
		if (jSpinnerRegEnd == e.getSource()) {
			if (regEnd > numPyrImgs) {
				jSpinnerRegEnd.setValue(numPyrImgs);
				regEnd = numPyrImgs;
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

		jSpinnerNumPyrImgs.addChangeListener(this);
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
			plotOptionsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Plot output options",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			plotOptionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			plotOptionsPanel.add(getJCheckBoxShowPlot());
			plotOptionsPanel.add(getJCheckBoxDeleteExistingPlot());
		}
		return plotOptionsPanel;
	}

	private JLabel getLblGreymode() {
		if (lblGreymode == null) {
			lblGreymode = new JLabel("GreyMode:");
		}
		return lblGreymode;
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(getLblGreymode(), BorderLayout.WEST);

			buttPGM = new JRadioButton();
			buttPGM.setText("PGM");
			buttPGM.setToolTipText("Surface area: Sum of image gradients (using Sobel operator)");
			buttPGM.addActionListener(this);
			buttPGM.setActionCommand("parameter");

			buttPDM = new JRadioButton();
			buttPDM.setText("PDM");
			buttPDM.setToolTipText("Differences: Differences of neighboring grey values");
			buttPDM.addActionListener(this);
			buttPDM.setActionCommand("parameter");
			
			buttBlanket = new JRadioButton();
			buttBlanket.setText("Blanket");
			buttBlanket.setToolTipText("Surface area: Blanket method");
			buttBlanket.addActionListener(this);
			buttBlanket.setActionCommand("parameter");

			buttAllometric = new JRadioButton();
			buttAllometric.setText("Allometric");
			buttAllometric.setToolTipText("Allometric scaling of variance to average");
			buttAllometric.addActionListener(this);
			buttAllometric.setActionCommand("parameter");
			
			buttInformation = new JRadioButton();
			buttInformation.setText("Information");
			buttInformation.setToolTipText("Information dimension");
			buttInformation.addActionListener(this);
			buttInformation.setActionCommand("parameter");
			
			buttKC = new JRadioButton();
			buttKC.setText("KC");
			buttKC.setToolTipText("Kolmogorov Complexity");
			buttKC.addActionListener(this);
			buttKC.setActionCommand("parameter");

			jPanelGreyMode = new JPanel();
			panel.add(jPanelGreyMode);
			jPanelGreyMode.setToolTipText("");
			jPanelGreyMode.setLayout(new BoxLayout(jPanelGreyMode, BoxLayout.X_AXIS));
			jPanelGreyMode.add(buttPDM);
			jPanelGreyMode.add(buttPGM);			
			jPanelGreyMode.add(buttBlanket);
			jPanelGreyMode.add(buttAllometric);
			jPanelGreyMode.add(buttInformation);
			jPanelGreyMode.add(buttKC);
			this.setButtonGroupGreyMode(); // Grouping of JRadioButtons
		}
		return panel;
	}

	private JPanel getPyramidOptionsPanel() {
		if (pyramidOptionsPanel == null) {
			pyramidOptionsPanel = new JPanel();
			pyramidOptionsPanel.setBorder(new TitledBorder(null,"Pyramid options", TitledBorder.LEADING, TitledBorder.TOP,null, null));
			GridBagLayout gbl_pyramidOptionsPanel = new GridBagLayout();
			gbl_pyramidOptionsPanel.columnWidths = new int[] { 0, 0, 0 };
			gbl_pyramidOptionsPanel.rowHeights = new int[] { 0, 0, 0 };
			gbl_pyramidOptionsPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			gbl_pyramidOptionsPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			pyramidOptionsPanel.setLayout(gbl_pyramidOptionsPanel);
			GridBagConstraints gbc_jPanelNumPyrImgs = new GridBagConstraints();
			gbc_jPanelNumPyrImgs.insets = new Insets(5, 5, 5, 5);
			gbc_jPanelNumPyrImgs.gridx = 0;
			gbc_jPanelNumPyrImgs.gridy = 0;
			pyramidOptionsPanel.add(getJPanelNumPyrImgs(), gbc_jPanelNumPyrImgs);

			buttNN = new JRadioButton();
			buttNN.setText("Nearest Neighbor");
			buttNN.setToolTipText("uses FilteredSubSample and Nearest Neighbor resampling");
			buttNN.addActionListener(this);
			buttNN.setActionCommand("parameter");

			buttBL = new JRadioButton();
			buttBL.setText("Bilinear");
			buttBL.setToolTipText("uses FilteredSubSample and Bilinear interpolation");
			buttBL.addActionListener(this);
			buttBL.setActionCommand("parameter");

			buttBC = new JRadioButton();
			buttBC.setText("Bicubic");
			buttBC.setToolTipText("uses FilteredSubSample and Bicubic interpolation");
			buttBC.addActionListener(this);
			buttBC.setActionCommand("parameter");

			buttBC2 = new JRadioButton();
			buttBC2.setText("Bicubic2");
			buttBC2.setToolTipText("uses FilteredSubSample and Bicubic2 interpolation");
			buttBC2.addActionListener(this);
			buttBC2.setActionCommand("parameter");

			buttAV = new JRadioButton();
			buttAV.setText("Average");
			buttAV.setToolTipText("uses SubsampleAverage");
			buttAV.addActionListener(this);
			buttAV.setActionCommand("parameter");

			jPanelIntP = new JPanel();
			GridBagConstraints gbc_jPanelIntP = new GridBagConstraints();
			gbc_jPanelIntP.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelIntP.gridx = 1;
			gbc_jPanelIntP.gridy = 0;
			pyramidOptionsPanel.add(jPanelIntP, gbc_jPanelIntP);
			jPanelIntP.setLayout(new BoxLayout(jPanelIntP, BoxLayout.Y_AXIS));
			jPanelIntP.add(buttNN);
			jPanelIntP.add(buttBL);
			jPanelIntP.add(buttBC);
			jPanelIntP.add(buttBC2);
			jPanelIntP.add(buttAV);

			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.fill = GridBagConstraints.VERTICAL;
			gbc_panel.gridwidth = 2;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 1;
			pyramidOptionsPanel.add(getPanel(), gbc_panel);

			this.setButtonGroupIntP(); // Grouping of JRadioButtons
		}
		return pyramidOptionsPanel;
	}
}// END
