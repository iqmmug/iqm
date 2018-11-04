package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_PointFinder.java
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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.Vector;

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

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFFTDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpPointFinderDescriptor;
import flanagan.math.FourierTransform;

/**
 * @author Ahammer
 * @since 2012 11
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 * @update 2017 Adam Dolgos added several options 
 * @update 2018-10 HA added QRS peak detection options Chen&Chen and osea
 */
public class PlotGUI_PointFinder extends AbstractPlotOperatorGUI implements ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6129126866758754431L;

	private static final Logger logger = Logger.getLogger(PlotGUI_PointFinder.class);

	private ParameterBlockIQM pb;

	private JPanel       jPanelMethod     = null;
	private ButtonGroup  buttGroupMethod  = null;
	private JRadioButton buttSlope        = null;
	private JRadioButton buttPeaks        = null;
	private JRadioButton buttValleys      = null;
	private JRadioButton buttQRSPeaksChen = null;
	private JRadioButton buttQRSPeaksOsea = null;
	
	private JPanel       jPanelOptions         = null;
	
	private JPanel       jPanelThresholdMAC    = null;
	private ButtonGroup  buttGroupThresholdMAC = null;
	private JRadioButton buttThreshold         = null;
	private JRadioButton buttMAC               = null;

	private JPanel   jPanelThres   = null;
	private JLabel   jLabelThres   = null;
	private JSpinner jSpinnerThres = null;

	private JPanel   jPanelTau   = null;
	private JLabel   jLabelTau   = null;
	private JSpinner jSpinnerTau = null;

	private JPanel   jPanelOffset   = null;
	private JLabel   jLabelOffset   = null;
	private JSpinner jSpinnerOffset = null;

	private JPanel   jPanelScaleDown   = null;
	private JLabel   jLabelScaleDown   = null;
	private JSpinner jSpinnerScaleDown = null;
	
	private JPanel       jPanelSlopeOptions = null;
	private ButtonGroup  buttGroupSlope = null;
	private JRadioButton buttPositive   = null;
	private JRadioButton buttNegative   = null;
		
	private JPanel   jPanelChenM   = null;
	private JLabel   jLabelChenM   = null;
	private JSpinner jSpinnerChenM = null;

	private JPanel   jPanelSumInterval   = null;
	private JLabel   jLabelSumInterval   = null;
	private JSpinner jSpinnerSumInterval = null;

	private JPanel   jPanelPeakFrame   = null;
	private JLabel   jLabelPeakFrame   = null;
	private JSpinner jSpinnerPeakFrame = null;
	
	private JPanel       jPanelOseaMethods       = null;
	private ButtonGroup  buttGroupOseaMethods    = null;
	private JRadioButton buttQRSDetect           = null;
	private JRadioButton buttQRSDetect2          = null;
	private JRadioButton buttQRSBeatDetectClass  = null;
		
	private JPanel   jPanelSampleRate   = null;
	private JLabel   jLabelSampleRate   = null;
	private JSpinner jSpinnerSampleRate = null;

	private JPanel       jPanelOutputOptions    = null;
	private ButtonGroup  buttGroupOutputOptions = null;
	private JRadioButton buttCoordinates        = null;
	private JRadioButton buttIntervals          = null;
	private JRadioButton buttHeights            = null;
	private JRadioButton buttEnergies           = null;
	private JRadioButton buttDeltaHeights       = null;


	public PlotGUI_PointFinder() {
		//getOpGUIContent().setBorder(new EmptyBorder(10, 0, 0, 0));
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpPointFinderDescriptor().getName());
		this.initialize();
		this.setTitle("Point Finder");

		this.getOpGUIContent().setLayout(new GridBagLayout());
		this.getOpGUIContent().add(getJPanelMethod(),        getGridBagConstraints_Method());
		this.getOpGUIContent().add(getJPanelOptions(),       getGridBagConstraints_Options());
		this.getOpGUIContent().add(getJPanelOutputOptions(), getGridBagConstraints_OutputOptions());
	
		this.pack();
	}

	private GridBagConstraints getGridBagConstraints_Method() {
		GridBagConstraints gbc_Method = new GridBagConstraints();
		gbc_Method.gridx = 0;
		gbc_Method.gridy = 0;
		gbc_Method.insets = new Insets(10, 0, 0, 0);
		gbc_Method.fill = GridBagConstraints.BOTH;
		return gbc_Method;
	}
	private GridBagConstraints getGridBagConstraints_Options() {
		GridBagConstraints gbc_Options = new GridBagConstraints();
		gbc_Options.gridx = 0;
		gbc_Options.gridy = 1;
		gbc_Options.insets = new Insets(5, 0, 0, 0);
		gbc_Options.fill = GridBagConstraints.BOTH;
		return gbc_Options;
	}
	private GridBagConstraints getGridBagConstraints_OutputOptions() {
		GridBagConstraints gbc_OutOptions = new GridBagConstraints();
		gbc_OutOptions.gridx = 0;
		gbc_OutOptions.gridy = 2;
		gbc_OutOptions.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gbc_OutOptions.fill = GridBagConstraints.BOTH;
		return gbc_OutOptions;
	}
	// ----------------------------------------------------------------------------------------------------------
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		switch (pb.getIntParameter("method")) {
		case PlotOpPointFinderDescriptor.METHOD_SLOPE: this.buttSlope.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.METHOD_PEAKS:this.buttPeaks.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.METHOD_VALLEYS:this.buttValleys.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.METHOD_QRSPEAKS_CHENCHEN:this.buttQRSPeaksChen.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.METHOD_QRSPEAKS_OSEA:this.buttQRSPeaksOsea.setSelected(true);
			break;
		default:
			break;
		}
		
		switch (pb.getIntParameter("options")) {
		case PlotOpPointFinderDescriptor.OPTION_THRES: this.buttThreshold.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.OPTION_MAC:this.buttMAC.setSelected(true);
			break;
		default:
			break;
		}
		
		switch (pb.getIntParameter("slope")) {
		case PlotOpPointFinderDescriptor.SLOPE_POSITIVE: buttPositive.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.SLOPE_NEGATIVE: buttNegative.setSelected(true);
			break;
		default:
			break;
		}

		jSpinnerThres.removeChangeListener(this);
		jSpinnerThres.setValue(pb.getIntParameter("threshold"));
		jSpinnerThres.addChangeListener(this);

		jSpinnerTau.removeChangeListener(this);
		jSpinnerTau.setValue(pb.getIntParameter("tau"));
		jSpinnerTau.addChangeListener(this);

		jSpinnerOffset.removeChangeListener(this);
		jSpinnerOffset.setValue(pb.getIntParameter("offset"));
		jSpinnerOffset.addChangeListener(this);

		jSpinnerScaleDown.removeChangeListener(this);
		jSpinnerScaleDown.setValue(pb.getIntParameter("scaledown"));
		jSpinnerScaleDown.addChangeListener(this);
	
		jSpinnerChenM.removeChangeListener(this);
		jSpinnerChenM.setValue(pb.getIntParameter("chenm"));
		jSpinnerChenM.addChangeListener(this);
		
		jSpinnerSumInterval.removeChangeListener(this);
		jSpinnerSumInterval.setValue(pb.getIntParameter("suminterval"));
		jSpinnerSumInterval.addChangeListener(this);
		
		jSpinnerPeakFrame.removeChangeListener(this);
		jSpinnerPeakFrame.setValue(pb.getIntParameter("peakframe"));
		jSpinnerPeakFrame.addChangeListener(this);
		
		switch (pb.getIntParameter("oseamethod")) {
		case PlotOpPointFinderDescriptor.OSEA_QRSDETECTION: buttQRSDetect.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.OSEA_QRSDETECTION2: buttQRSDetect2.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.OSEA_QRSBEATDETECTIONANDCLASSIFY: buttQRSBeatDetectClass.setSelected(true);
			break;
		default:
			break;
		}
		
		jSpinnerSampleRate.removeChangeListener(this);
		jSpinnerSampleRate.setValue(pb.getIntParameter("samplerate"));
		jSpinnerSampleRate.addChangeListener(this);
		
		
		switch (pb.getIntParameter("outputoptions")) {
		case PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES:buttCoordinates.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS:buttIntervals.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.OUTPUTOPTION_HEIGHTS:buttHeights.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.OUTPUTOPTION_DELTAHEIGHTS:buttDeltaHeights.setSelected(true);
			break;
		case PlotOpPointFinderDescriptor.OUTPUTOPTION_ENERGIES:buttEnergies.setSelected(true);
			break;
		default:
			break;
		}
	}

	@Override
	public void updateParameterBlock() {

		if (this.buttSlope.isSelected())      pb.setParameter("method", PlotOpPointFinderDescriptor.METHOD_SLOPE);
		if (this.buttPeaks.isSelected())      pb.setParameter("method", PlotOpPointFinderDescriptor.METHOD_PEAKS);
		if (this.buttValleys.isSelected())    pb.setParameter("method", PlotOpPointFinderDescriptor.METHOD_VALLEYS);
		if (this.buttQRSPeaksChen.isSelected())   pb.setParameter("method", PlotOpPointFinderDescriptor.METHOD_QRSPEAKS_CHENCHEN);
		if (this.buttQRSPeaksOsea.isSelected())   pb.setParameter("method", PlotOpPointFinderDescriptor.METHOD_QRSPEAKS_OSEA);
			
		if (this.buttThreshold.isSelected())  pb.setParameter("options", PlotOpPointFinderDescriptor.OPTION_THRES);
		if (this.buttMAC.isSelected())        pb.setParameter("options", PlotOpPointFinderDescriptor.OPTION_MAC);
		
		if (this.buttPositive.isSelected())    pb.setParameter("slope", PlotOpPointFinderDescriptor.SLOPE_POSITIVE);
		if (this.buttNegative.isSelected())    pb.setParameter("slope", PlotOpPointFinderDescriptor.SLOPE_NEGATIVE);

		pb.setParameter("threshold",          ((Number) jSpinnerThres.    getValue()).intValue());
		pb.setParameter("tau",                ((Number) jSpinnerTau.      getValue()).intValue());
		pb.setParameter("offset",             ((Number) jSpinnerOffset.   getValue()).intValue());
		pb.setParameter("scaledown",          ((Number) jSpinnerScaleDown.getValue()).intValue());
		pb.setParameter("chenm",              ((Number) this.jSpinnerChenM.      getValue()).intValue());
		pb.setParameter("suminterval",        ((Number) this.jSpinnerSumInterval.getValue()).intValue());
		pb.setParameter("peakframe",          ((Number) this.jSpinnerPeakFrame.  getValue()).intValue());
		
		if (this.buttQRSDetect.isSelected())          pb.setParameter("oseamethod", PlotOpPointFinderDescriptor.OSEA_QRSDETECTION);
		if (this.buttQRSDetect2.isSelected())         pb.setParameter("oseamethod", PlotOpPointFinderDescriptor.OSEA_QRSDETECTION2);
		if (this.buttQRSBeatDetectClass.isSelected()) pb.setParameter("oseamethod", PlotOpPointFinderDescriptor.OSEA_QRSBEATDETECTIONANDCLASSIFY);
		
		pb.setParameter("samplerate",          ((Number) this.jSpinnerSampleRate.getValue()).intValue());
		
		if (this.buttCoordinates.isSelected())  pb.setParameter("outputoptions", PlotOpPointFinderDescriptor.OUTPUTOPTION_COORDINATES);
		if (this.buttIntervals.isSelected())    pb.setParameter("outputoptions", PlotOpPointFinderDescriptor.OUTPUTOPTION_INTERVALS);
		if (this.buttHeights.isSelected())      pb.setParameter("outputoptions", PlotOpPointFinderDescriptor.OUTPUTOPTION_HEIGHTS);
		if (this.buttDeltaHeights.isSelected()) pb.setParameter("outputoptions", PlotOpPointFinderDescriptor.OUTPUTOPTION_DELTAHEIGHTS);
		if (this.buttEnergies.isSelected())     pb.setParameter("outputoptions", PlotOpPointFinderDescriptor.OUTPUTOPTION_ENERGIES);
	}

	//---------------------------------------------------------------------------------------------------------
	
	/**
	 * This method initializes the Option: Slope
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSlope() {
		if (buttSlope == null) {
			buttSlope = new JRadioButton();
			buttSlope.setMargin(new Insets(5, 50, 0, 0));   //top, left, bottom, right
			buttSlope.setText("Slope");
			buttSlope.setToolTipText("Slopes are used to find points in a signal");
			buttSlope.addActionListener(this);
			buttSlope.setActionCommand("parameter");
			buttSlope.setSelected(true);
		}
		return buttSlope;
	}
	/**
	 * This method initializes the Option: Peaks
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonPeaks() {
		if (buttPeaks == null) {
			buttPeaks = new JRadioButton();
			buttPeaks.setMargin(new Insets(4, 50, 0, 0));   //top, left, bottom, right
			buttPeaks.setText("Peaks");
			// buttPeaks.setPreferredSize(new Dimension(95,10));
			buttPeaks.setToolTipText("Peaks are used to find points in a signal");
			buttPeaks.addActionListener(this);
			buttPeaks.setActionCommand("parameter");
		}
		return buttPeaks;
	}
	
	/**
	 * This method initializes the Option: Valleys
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonValleys() {
		if (buttValleys == null) {
			buttValleys = new JRadioButton();
			buttValleys.setMargin(new Insets(4, 50, 0, 0));   //top, left, bottom, right
			buttValleys.setText("Valleys");
			// buttValleys.setPreferredSize(new Dimension(95,10));
			buttValleys.setToolTipText("Valleys are used to find points in a signal");
			buttValleys.addActionListener(this);
			buttValleys.setActionCommand("parameter");
		}
		return buttValleys;
	}
	
	/**
	 * This method initializes the Option: QRSPeaksChen
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonQRSPeaksChen() {
		if (buttQRSPeaksChen == null) {
			buttQRSPeaksChen = new JRadioButton();
			buttQRSPeaksChen.setMargin(new Insets(4, 50, 0, 0));   //top, left, bottom, right
			buttQRSPeaksChen.setText("QRSPeaks-ChenChen");
			//buttQRSPeaksChen.setPreferredSize(new Dimension(95,10));
			buttQRSPeaksChen.setToolTipText("QRS peaks detection according to Chen&Chen etal.");
			buttQRSPeaksChen.addActionListener(this);
			buttQRSPeaksChen.setActionCommand("parameter");
		}
		return buttQRSPeaksChen;
	}
	
	/**
	 * This method initializes the Option: QRSPeaksOsea
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonQRSPeaksOsea() {
		if (buttQRSPeaksOsea == null) {
			buttQRSPeaksOsea = new JRadioButton();
			buttQRSPeaksOsea.setMargin(new Insets(4, 50, 5, 0));   //top, left, bottom, right
			buttQRSPeaksOsea.setText("QRSPeaks-OSEA");
			// buttQRSPeaksOsea.setPreferredSize(new Dimension(95,10));
			buttQRSPeaksOsea.setToolTipText("QRS peaks detection according to OSEA");
			buttQRSPeaksOsea.addActionListener(this);
			buttQRSPeaksOsea.setActionCommand("parameter");
		}
		return buttQRSPeaksOsea;
	}
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		if (jPanelMethod == null) {
			jPanelMethod = new JPanel();
			jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
			//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jPanelMethod.add(getJRadioButtonSlope());
			jPanelMethod.add(getJRadioButtonPeaks());
			jPanelMethod.add(getJRadioButtonValleys());
			jPanelMethod.add(getJRadioButtonQRSPeaksChen());
			jPanelMethod.add(getJRadioButtonQRSPeaksOsea());

			// jPanelMethod.addSeparator();
			this.setButtonGroupMethod(); // Grouping of JRadioButtons
		}
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		if (buttGroupMethod == null) {
			buttGroupMethod = new ButtonGroup();
			buttGroupMethod.add(buttSlope);
			buttGroupMethod.add(buttPeaks);
			buttGroupMethod.add(buttValleys);
			buttGroupMethod.add(buttQRSPeaksChen);
			buttGroupMethod.add(buttQRSPeaksOsea);
		}
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: Threshold
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonThreshold() {
		if (buttThreshold == null) {
			buttThreshold = new JRadioButton();
			buttThreshold.setText("Threshold");
			buttThreshold.setToolTipText("Thresholding is used to find points in a signal");
			buttThreshold.addActionListener(this);
			buttThreshold.setActionCommand("parameter");
			buttThreshold.setSelected(true);
		}
		return buttThreshold;
	}

	/**
	 * This method initializes the Option: MAC
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMAC() {
		if (buttMAC == null) {
			buttMAC = new JRadioButton();
			buttMAC.setText("MAC");
			//buttMAC.setPreferredSize(new Dimension(95, 10));
			buttMAC.setToolTipText("Moving Average Curves (MACs) according to Lu et al., Med. Phys. 33, 3634 (2006); http://dx.doi.org/10.1118/1");
			buttMAC.addActionListener(this);
			buttMAC.setActionCommand("parameter");
			buttMAC.setEnabled(true);
			buttMAC.setVisible(true);

		}
		return buttMAC;
	}
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelThresholdMAC() {
		if (jPanelThresholdMAC == null) {
			jPanelThresholdMAC = new JPanel();
			//jPanelThresholdMAC.setLayout(new BoxLayout(jPanelMethod, BoxLayout.X_AXIS));
			jPanelThresholdMAC.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelThresholdMAC.add(getJRadioButtonThreshold());
			jPanelThresholdMAC.add(getJRadioButtonMAC());
			// jPanelMethod.addSeparator();
			this.setButtonGroupThresholdMAC(); // Grouping of JRadioButtons
		}
		return jPanelThresholdMAC;
	}

	private void setButtonGroupThresholdMAC() {
		if (buttGroupThresholdMAC == null) {
			buttGroupThresholdMAC = new ButtonGroup();
			buttGroupThresholdMAC.add(buttThreshold);
			buttGroupThresholdMAC.add(buttMAC);		
		}
	}
	
	/**
	 * This method initializes the Option: Positive
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonPositive() {
		if (buttPositive == null) {
			buttPositive = new JRadioButton();
			buttPositive.setText("Positive slope");
			// buttPositive.setPreferredSize(new Dimension(105,10));
			buttPositive.setToolTipText("threshold on positve slope");
			buttPositive.addActionListener(this);
			buttPositive.setActionCommand("parameter");
			buttPositive.setSelected(true);
		}
		return buttPositive;
	}

	/**
	 * This method initializes the Option: Negative
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonNegative() {
		if (buttNegative == null) {
			buttNegative = new JRadioButton();
			buttNegative.setText("Negative slope");
			// buttNegative.setPreferredSize(new Dimension(95,10));
			buttNegative.setToolTipText("threshold on negative slope");
			buttNegative.addActionListener(this);
			buttNegative.setActionCommand("parameter");
		}
		return buttNegative;
	}
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSlope() {
		jPanelOutputOptions = new JPanel();
		//jPanelOutputOptions.setLayout(new BoxLayout(jPanelOutputOptions, BoxLayout.Y_AXIS));
		jPanelOutputOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		//jPanelOutputOptions.setBorder(new TitledBorder(null, "Slope", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelOutputOptions.add(getJPanelSlopeOptions());
		//		this.setButtonGroupSlope(); // Grouping of JRadioButtons
		return jPanelOutputOptions;
	}

	private JPanel getJPanelSlopeOptions() {
		jPanelSlopeOptions = new JPanel();
		//jPanelSlope.setLayout(new BoxLayout(jPanelSlope, BoxLayout.Y_AXIS));
		jPanelSlopeOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelSlopeOptions.add(getJRadioButtonPositive());
		jPanelSlopeOptions.add(getJRadioButtonNegative());
		this.setButtonGroupSlope(); // Grouping of JRadioButtons
		return jPanelSlopeOptions;
	}
	private void setButtonGroupSlope() {
		buttGroupSlope = new ButtonGroup();
		buttGroupSlope.add(buttPositive);
		buttGroupSlope.add(buttNegative);
	}
			
	/**
	 * This method initializes jJPanelThres
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelThres() {
		if (jPanelThres == null) {
			jPanelThres = new JPanel();
			jPanelThres.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jLabelThres = new JLabel("Threshold: ");
			jLabelThres.setToolTipText("Thresold value (use scale down factor to set with finer detail)");
			jLabelThres.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, Integer.MIN_VALUE, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerThres = new JSpinner(sModel);
			jSpinnerThres.addChangeListener(this);
			jSpinnerThres.setToolTipText("thresold value (use scale down factor to set with finer detail)");
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThres.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelThres.add(jLabelThres);
			jPanelThres.add(jSpinnerThres);
		}
		return jPanelThres;
	}

	/**
	 * This method initializes jJPanelTau
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelTau() {
		if (jPanelTau == null) {
			jPanelTau = new JPanel();
			jPanelTau.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jLabelTau = new JLabel("Tau: ");
			jLabelTau.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerTau = new JSpinner(sModel);
			jSpinnerTau.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerTau.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");											
			jPanelTau.add(jLabelTau);
			jPanelTau.add(jSpinnerTau);
		}
		return jPanelTau;
	}

	/**
	 * This method initializes jJPanelOffset
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOffset() {
		if (jPanelOffset == null) {
			jPanelOffset = new JPanel();
			jPanelOffset.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jLabelOffset = new JLabel("Offset: ");
			jLabelOffset.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerOffset = new JSpinner(sModel);
			jSpinnerOffset.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerOffset.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelOffset.add(jLabelOffset);
			jPanelOffset.add(jSpinnerOffset);
		}
		return jPanelOffset;
	}
	
	/**
	 * This method initializes jJPanelScaleDown
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelScaleDown() {
		if (jPanelScaleDown == null) {
			jPanelScaleDown = new JPanel();
			jPanelScaleDown.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			//jPanelScaleDown.setBorder(new TitledBorder(null, "Scale down factor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jLabelScaleDown = new JLabel("Scale down factor: ");
			jLabelScaleDown.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelScaleDown.setToolTipText("Scale down factor for threshold or offset value (not for tau)");
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerScaleDown = new JSpinner(sModel);
			jSpinnerScaleDown.addChangeListener(this);
			jSpinnerScaleDown.setToolTipText("Scale down factor for threshold or offset value (not for tau)");
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerScaleDown.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelScaleDown.add(jLabelScaleDown);
			jPanelScaleDown.add(jSpinnerScaleDown);
		}
		return jPanelScaleDown;
	}
	
	/**
	 * This method initializes jJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelChenM() {
		if (jPanelChenM == null) {
			jPanelChenM = new JPanel();
			jPanelChenM.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jLabelChenM = new JLabel("M: ");
			jLabelChenM.setToolTipText("Chen&Chen highpass filter parameter, usually set to 3,5,7,9...");
			jLabelChenM.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerChenM = new JSpinner(sModel);
			jSpinnerChenM.addChangeListener(this);
			jSpinnerChenM.setToolTipText("Chen&Chen highpass filter parameter, usually set to 3,5,7,9...");
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerChenM.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelChenM.add(jLabelChenM);
			jPanelChenM.add(jSpinnerChenM);
		}
		return jPanelChenM;
	}
	
	/**
	 * This method initializes jJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSumInterval() {
		if (jPanelSumInterval == null) {
			jPanelSumInterval = new JPanel();
			jPanelSumInterval.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jLabelSumInterval = new JLabel("SumInterval: ");
			jLabelSumInterval.setToolTipText("Chen&Chen lowpass filter parameter, usually set to 10,20,30,40,50...");
			jLabelSumInterval.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(30, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerSumInterval = new JSpinner(sModel);
			jSpinnerSumInterval.addChangeListener(this);
			jSpinnerSumInterval.setToolTipText("Chen&Chen lowpass filter parameter, usually set to 10,20,30,40,50...");
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerSumInterval.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelSumInterval.add(jLabelSumInterval);
			jPanelSumInterval.add(jSpinnerSumInterval);
		}
		return jPanelSumInterval;
	}
	
	/**
	 * This method initiaSumInterval jJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelPeakFrame() {
		if (jPanelPeakFrame == null) {
			jPanelPeakFrame = new JPanel();
			jPanelPeakFrame.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jLabelPeakFrame = new JLabel("PeakFrame: ");
			jLabelPeakFrame.setToolTipText("Chen&Chen frame for peak parameter, usually set to 100, 150, 200, 250, 300,...");
			jLabelPeakFrame.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(250, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerPeakFrame = new JSpinner(sModel);
			jSpinnerPeakFrame.addChangeListener(this);
			jSpinnerPeakFrame.setToolTipText("Chen&Chen frame for peak parameter, usually set to 100, 150, 200, 250, 300,...");
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerPeakFrame.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelPeakFrame.add(jLabelPeakFrame);
			jPanelPeakFrame.add(jSpinnerPeakFrame);
		}
		return jPanelPeakFrame;
	}
	
	/**
	 * This method initializes the Option: QRSDetect 
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonQRSDetect() {
		if (buttQRSDetect == null) {
			buttQRSDetect = new JRadioButton();
			buttQRSDetect.setText("QRSDetect");
			// buttQRSDetect.setPreferredSize(new Dimension(95,10));
			buttQRSDetect.setToolTipText("osea QRSDetect option using medians");
			buttQRSDetect.addActionListener(this);
			buttQRSDetect.setActionCommand("parameter");
		}
		return buttQRSDetect;
	}

	/**
	 * This method initializes the Option: QRSDetect2 
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonQRSDetect2() {
		if (buttQRSDetect2 == null) {
			buttQRSDetect2 = new JRadioButton();
			buttQRSDetect2.setText("QRSDetect2");
			// buttQRSDetect2.setPreferredSize(new Dimension(95,10));
			buttQRSDetect2.setToolTipText("osea QRSDetect2 option using means");
			buttQRSDetect2.addActionListener(this);
			buttQRSDetect2.setActionCommand("parameter");
		}
		return buttQRSDetect2;
	}
	
	/**
	 * This method initializes the Option: QRSBeatDetectClass 
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonQRSBeatDetectClass() {
		if (buttQRSBeatDetectClass == null) {
			buttQRSBeatDetectClass = new JRadioButton();
			buttQRSBeatDetectClass.setText("BeatDetectionAndClassify");
			// buttQRSBeatDetectClass.setPreferredSize(new Dimension(95,10));
			buttQRSBeatDetectClass.setToolTipText("osea BeatDetectionAndClassify option using QRSDetect2 for detection");
			buttQRSBeatDetectClass.addActionListener(this);
			buttQRSBeatDetectClass.setActionCommand("parameter");
		}
		return buttQRSBeatDetectClass;
	}
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOseaMethods() {
		jPanelOseaMethods = new JPanel();
		//jPanelOseaMethods.setLayout(new BoxLayout(jPanelOseaMethods, BoxLayout.Y_AXIS));
		jPanelOseaMethods.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		//jPanelOseaMethods.setBorder(new TitledBorder(null, "Slope", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelOseaMethods.add(getJRadioButtonQRSDetect());
		jPanelOseaMethods.add(getJRadioButtonQRSDetect2());
		jPanelOseaMethods.add(getJRadioButtonQRSBeatDetectClass());
		setButtonGroupOseaMethods(); // Grouping of JRadioButtons
		return jPanelOseaMethods;
	}


	private void setButtonGroupOseaMethods() {
		buttGroupOseaMethods = new ButtonGroup();
		buttGroupOseaMethods.add(buttQRSDetect);
		buttGroupOseaMethods.add(buttQRSDetect2);
		buttGroupOseaMethods.add(buttQRSBeatDetectClass);
	}
	
	/**
	 * This method initializes jJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSampleRate() {
		if (jPanelSampleRate == null) {
			jPanelSampleRate = new JPanel();
			jPanelSampleRate.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			jLabelSampleRate = new JLabel("Sample rate [Hz]: ");
			jLabelSampleRate.setToolTipText("Sample rate of signal in Hz");
			jLabelSampleRate.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, Integer.MIN_VALUE, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerSampleRate = new JSpinner(sModel);
			jSpinnerSampleRate.addChangeListener(this);
			jSpinnerSampleRate.setToolTipText("Sample rate of signal  in Hz");
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerSampleRate.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelSampleRate.add(jLabelSampleRate);
			jPanelSampleRate.add(jSpinnerSampleRate);
		}
		return jPanelSampleRate;
	}
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOptions() {
		if (jPanelOptions == null) {
			jPanelOptions = new JPanel();
			//jPanelOptions.setLayout(new BoxLayout(jPanelOptions, BoxLayout.Y_AXIS));
			//jPanelOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			GridBagLayout gbl_Options = new GridBagLayout();
		/*	gbl_Options.columnWidths = new int[] { 0, 0 };
			gbl_Options.rowHeights = new int[] { 0, 0, 0, 0 };
			gbl_Options.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
			gbl_Options.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };*/
			
			jPanelOptions.setLayout(gbl_Options);
			jPanelOptions.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));

			GridBagConstraints gbc_1 = new GridBagConstraints();
			gbc_1.anchor = GridBagConstraints.WEST;
			gbc_1.insets = new Insets(5, 0, 0, 0);
			gbc_1.gridx = 0;
			gbc_1.gridy = 0;

			GridBagConstraints gbc_2 = new GridBagConstraints();
			gbc_2.anchor = GridBagConstraints.WEST;
			gbc_2.insets = new Insets(10, 0, 0, 0);
			gbc_2.gridx = 0;
			gbc_2.gridy = 1;
			
			GridBagConstraints gbc_3 = new GridBagConstraints();
			gbc_3.anchor = GridBagConstraints.EAST;
			gbc_3.insets = new Insets(10, 0, 0, 0);
			gbc_3.gridx = 0;
			gbc_3.gridy = 2;
			
			GridBagConstraints gbc_4 = new GridBagConstraints();
			gbc_4.anchor = GridBagConstraints.EAST;
			gbc_4.insets = new Insets(10, 0, 0, 0);
			gbc_4.gridx = 0;
			gbc_4.gridy = 3;
			
			GridBagConstraints gbc_5 = new GridBagConstraints();
			gbc_5.anchor = GridBagConstraints.EAST;
			gbc_5.insets = new Insets(10, 0, 0, 0);
			gbc_5.gridx = 0;
			gbc_5.gridy = 4;
			
			GridBagConstraints gbc_6 = new GridBagConstraints();
			gbc_6.anchor = GridBagConstraints.EAST;
			gbc_6.insets = new Insets(10, 0, 0, 0);
			gbc_6.gridx = 0;
			gbc_6.gridy = 5;
			
			GridBagConstraints gbc_7 = new GridBagConstraints();
			gbc_7.anchor = GridBagConstraints.EAST;
			gbc_7.insets = new Insets(10, 0, 0, 0);
			gbc_7.gridx = 0;
			gbc_7.gridy = 6;
			
			GridBagConstraints gbc_8 = new GridBagConstraints();
			gbc_8.anchor = GridBagConstraints.EAST;
			gbc_8.insets = new Insets(10, 0, 0, 0);
			gbc_8.gridx = 0;
			gbc_8.gridy = 7;
			
			GridBagConstraints gbc_9 = new GridBagConstraints();
			gbc_9.anchor = GridBagConstraints.EAST;
			gbc_9.insets = new Insets(10, 0, 0, 0);
			gbc_9.gridx = 0;
			gbc_9.gridy = 8;
			
			GridBagConstraints gbc_10 = new GridBagConstraints();
			gbc_10.anchor = GridBagConstraints.EAST;
			gbc_10.insets = new Insets(10, 0, 0, 0);
			gbc_10.gridx = 0;
			gbc_10.gridy = 9;
		
			GridBagConstraints gbc_11 = new GridBagConstraints();
			gbc_11.anchor = GridBagConstraints.EAST;
			gbc_11.gridx = 0;
			gbc_11.gridy = 10;
			gbc_11.insets = new Insets(5, 0, 5, 0);

			jPanelOptions.add(getJPanelThresholdMAC(), gbc_1);
			jPanelOptions.add(getJPanelSlope(),        gbc_2);
			jPanelOptions.add(getJPanelThres(),        gbc_3);
			jPanelOptions.add(getJPanelTau(),          gbc_4);
			jPanelOptions.add(getJPanelOffset(),       gbc_5);
			jPanelOptions.add(getJPanelScaleDown(),    gbc_6);
			jPanelOptions.add(getJPanelChenM(),        gbc_7);
			jPanelOptions.add(getJPanelSumInterval(),  gbc_8);
			jPanelOptions.add(getJPanelPeakFrame(),    gbc_9);
			jPanelOptions.add(getJPanelOseaMethods(),  gbc_10);
			jPanelOptions.add(getJPanelSampleRate(),   gbc_11);		
		}
		return jPanelOptions;
	}

	// --------------------------------------------------------------------------------------------


	/**
	 * This method initializes the Option: Coordinates
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonCoordinates() {
		if (buttCoordinates == null) {
			buttCoordinates = new JRadioButton();
			buttCoordinates.setText("Coordinates");
			buttCoordinates.setToolTipText("Coordinates of points (slope points, peaks or valleys)");
			// buttCoordinates.setPreferredSize(new Dimension(95,10));
	//		buttCoordinates.setToolTipText("threshold on slope");
			buttCoordinates.addActionListener(this);
			buttCoordinates.setActionCommand("parameter");
		}
		return buttCoordinates;
	}
	/**
	 * This method initializes the Option: Intervals
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonIntervals() {
		if (buttIntervals == null) {
			buttIntervals = new JRadioButton();
			buttIntervals.setText("Intervals");
			buttIntervals.setToolTipText("Intervals between points (slope points, peaks or valleys)");
			// buttIntervals.setPreferredSize(new Dimension(95,10));
	//		buttIntervals.setToolTipText("threshold on slope");
			buttIntervals.addActionListener(this);
			buttIntervals.setActionCommand("parameter");
		}
		return buttIntervals;
	}

	/**
	 * This method initializes the Option: Heights
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonHeights() {
		if (buttHeights == null) {
			buttHeights = new JRadioButton();
			buttHeights.setText("Heights");
			buttHeights.setToolTipText("Height of a point relative to the Median of all values back to the privious point");
			// buttHeights.setPreferredSize(new Dimension(95,10));
			buttHeights.addActionListener(this);
			buttHeights.setActionCommand("parameter");
		}
		return buttHeights;
	}

	/**
	 * This method initializes the Option: DeltaHeights
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDeltaHeights() {
		if (buttDeltaHeights == null) {
			buttDeltaHeights = new JRadioButton();
			buttDeltaHeights.setText("Delta heights");
			buttDeltaHeights.setToolTipText("Height difference of two points");
			
			// buttPeaks.setPreferredSize(new Dimension(95,10));
			buttDeltaHeights.addActionListener(this);
			buttDeltaHeights.setActionCommand("parameter");
		}
		return buttDeltaHeights;
	}

	/**
	 * This method initializes the Option: Energies
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonEnergies() {
		if (buttEnergies == null) {
			buttEnergies = new JRadioButton();
			buttEnergies.setText("Energies");
			buttEnergies.setToolTipText("Interval times height");
			// buttEnergies.setPreferredSize(new Dimension(95,10));
	//		buttEnergies.setToolTipText("threshold on slope");
			buttEnergies.addActionListener(this);
			buttEnergies.setActionCommand("parameter");
			buttEnergies.setVisible(false);
		}
		return buttEnergies;
	}

	
	private JPanel getJPanelOutputOptions() {
		jPanelOutputOptions = new JPanel();
		//jPanelSlope.setLayout(new BoxLayout(jPanelSlope, BoxLayout.Y_AXIS));
		jPanelOutputOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelOutputOptions.setBorder(new TitledBorder(null, "Output", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelOutputOptions.add(getJRadioButtonCoordinates());
		jPanelOutputOptions.add(getJRadioButtonIntervals());
		jPanelOutputOptions.add(getJRadioButtonHeights());
		jPanelOutputOptions.add(getJRadioButtonDeltaHeights());
		jPanelOutputOptions.add(getJRadioButtonEnergies());
		this.setButtonOutputOptions(); // Grouping of JRadioButtons
		return jPanelOutputOptions;
	}
	private void setButtonOutputOptions() {
		buttGroupOutputOptions = new ButtonGroup();
		buttGroupOutputOptions.add(buttCoordinates);
		buttGroupOutputOptions.add(buttIntervals);
		buttGroupOutputOptions.add(buttHeights);
		buttGroupOutputOptions.add(buttDeltaHeights);
		buttGroupOutputOptions.add(buttEnergies);
	}
	// --------------------------------------------------------------------------------------------

	@Override
	public void update() {

		IqmDataBox box = (IqmDataBox) this.workPackage.getSources().get(0);
		PlotModel plotModel = box.getPlotModel();
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
		int size = signal.size();

		//estimation of Tau
		//eliminate mean value in order to suppress the DC component in the power spectrum
		double sum = 0.0;
		for (int i = 0; i < size; i++) {
			sum = sum + signal.get(i);
		}
		double mean = sum / size;
		for (int i = 0; i < size; i++) {
			signal.set(i, signal.get(i) - mean);
		}
		BoardPanel.appendTextln("PlotGUI_PointFinder: Mean value of signal: " + mean);
		BoardPanel.appendTextln("PlotGUI_PointFinder: Mean value subtracted from signal in order to computer Tau");

		//estimate Tau with FFT
		//-----------------------------------------------------------------------------------------
		int windowing = PlotOpFFTDescriptor.WINDOWING_GAUSSIAN;
		//int sampleRate  = 1;
		//int resultLogLin = PlotOpFFTDescriptor.RESULT_LIN;

		//Vector<Double> signalPS = new Vector<Double>();
		FourierTransform ft = new FourierTransform();

		if (windowing == PlotOpFFTDescriptor.WINDOWING_WITHOUT) {
			ft.removeWindow();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_RECTANGULAR) {
			ft.setRectangular();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_BARTLETT) {
			ft.setBartlett();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_WELCH) {
			ft.setWelch();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_HANN) {
			ft.setHann();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_HAMMING) {
			ft.setHamming();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_KAISER) {
			ft.setKaiser();
		}
		if (windowing == PlotOpFFTDescriptor.WINDOWING_GAUSSIAN) {
			ft.setGaussian();
		}

		// data length must not have a power of 2 because .powerSpectrum() truncates the signal to the next smaller power of 2 value
		double[] data = new double[signal.size()];
		// set data
		for (int i = 0; i < signal.size(); i++) {
			data[i] = signal.get(i);
		}

		ft.setData(data);
		// ft.transform();

		double[][] ps = ft.powerSpectrum();

		//		for (int i = 0; i < ps[0].length; i++){
		//			if(resultLogLin == PlotOpFFTDescriptor.RESULT_LOG){
		//				signalPS.add(Math.log(ps[1][i]));  //log Power Spectrum
		//			}
		//			if(resultLogLin == PlotOpFFTDescriptor.RESULT_LIN){
		//				signalPS.add(ps[1][i]);  //Power Spectrum
		//			}
		//			//rangePS.add(ps[0][i]);   //frequencies
		//			//if sample frequency is not known:
		//			//rangePS.add( (double)i + 1.0d);   //frequencies
		//
		//			//if sample frequency is known:	
		//			rangePS.add((((double)i +1.0) /ps[0].length) * (double)(sampleRate)/2.0);
		//		}
		//find maximum of the power spectrum
		double psMax = -Double.MAX_VALUE;
		double frequ = -1.0;
		int numK = ps[0].length;
		for (int i = 0; i < ps[0].length; i++) {

			if (ps[1][i] > psMax) {
				psMax = ps[1][i];
				frequ = i + 1;
			}
		}
		int tau = (int) Math.round(1.0 / frequ * numK * 2);
		//-----------------------------------------------------------------------------------------

		//set spinner
		SpinnerModel sModel = new SpinnerNumberModel(Math.min(tau, size / 2 - 1), 1, size / 2 - 1, 1); // init, min, max, step
		jSpinnerTau.setModel(sModel);
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerTau.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setColumns(5);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
		jSpinnerTau.setValue(Math.min(tau, size / 2 - 1)); // only in order to set format pattern
		jSpinnerTau.addChangeListener(this);

		//------------------------------------------------------------------------------------------

		if (buttSlope.isSelected()) {
			buttThreshold.setEnabled(true);
			buttMAC.setEnabled(true);
		
			if (buttThreshold.isSelected()) {
				jLabelThres.setEnabled(true);
				jSpinnerThres.setEnabled(true);
						
				jLabelTau.setEnabled(false);
				jSpinnerTau.setEnabled(false);
	
				jLabelOffset.setEnabled(false);
				jSpinnerOffset.setEnabled(false);
			}
			if (buttMAC.isSelected()) {
				jLabelThres.setEnabled(false);
				jSpinnerThres.setEnabled(false);
						
				jLabelTau.setEnabled(true);
				jSpinnerTau.setEnabled(true);
	
				jLabelOffset.setEnabled(true);
				jSpinnerOffset.setEnabled(true);
			}

			jLabelScaleDown.setEnabled(true);
			jSpinnerScaleDown.setEnabled(true);
			
			buttPositive.setEnabled(true);
			buttNegative.setEnabled(true);
			
			buttCoordinates.setEnabled(true);
			//buttCoordinates.setSelected(true);
			buttIntervals.setEnabled(true);
			//buttIntervals.setSelected(false);
			buttHeights.setEnabled(false);
			buttDeltaHeights.setEnabled(false);
			buttEnergies.setEnabled(false);
			
			jLabelChenM.setEnabled(false);
			jSpinnerChenM.setEnabled(false);

			jLabelSumInterval.setEnabled(false);
			jSpinnerSumInterval.setEnabled(false);
			
			jLabelPeakFrame.setEnabled(false);
			jSpinnerPeakFrame.setEnabled(false);
			
			buttQRSDetect.setEnabled(false);
			buttQRSDetect.setEnabled(false);
			
			buttQRSDetect2.setEnabled(false);
			buttQRSDetect2.setEnabled(false);
			
			buttQRSBeatDetectClass.setEnabled(false);
			buttQRSBeatDetectClass.setEnabled(false);
			
			jLabelSampleRate.setEnabled(false);;
			jSpinnerSampleRate.setEnabled(false);;
			
			
		}
		if (buttPeaks.isSelected() || this.buttValleys.isSelected()){
			buttThreshold.setEnabled(true);
			buttThreshold.setSelected(true);
			buttMAC.setEnabled(false);
		
			if (buttThreshold.isSelected()) {
				jLabelThres.setEnabled(true);
				jSpinnerThres.setEnabled(true);
						
				jLabelTau.setEnabled(false);
				jSpinnerTau.setEnabled(false);
	
				jLabelOffset.setEnabled(false);
				jSpinnerOffset.setEnabled(false);
			}
			if (buttMAC.isSelected()) {
				jLabelThres.setEnabled(false);
				jSpinnerThres.setEnabled(false);
						
				jLabelTau.setEnabled(true);
				jSpinnerTau.setEnabled(true);
	
				jLabelOffset.setEnabled(true);
				jSpinnerOffset.setEnabled(true);
			}
			jLabelScaleDown.setEnabled(true);
			jSpinnerScaleDown.setEnabled(true);
			
			buttPositive.setEnabled(false);
			buttNegative.setEnabled(false);
			
			buttCoordinates.setEnabled(true);
			buttIntervals.setEnabled(true);
			buttHeights.setEnabled(true);
			buttDeltaHeights.setEnabled(true);
			buttEnergies.setEnabled(true);
			
			jLabelChenM.setEnabled(false);
			jSpinnerChenM.setEnabled(false);

			jLabelSumInterval.setEnabled(false);
			jSpinnerSumInterval.setEnabled(false);
			
			jLabelPeakFrame.setEnabled(false);
			jSpinnerPeakFrame.setEnabled(false);
			
			buttQRSDetect.setEnabled(false);
			buttQRSDetect.setEnabled(false);
			
			buttQRSDetect2.setEnabled(false);
			buttQRSDetect2.setEnabled(false);
			
			buttQRSBeatDetectClass.setEnabled(false);
			buttQRSBeatDetectClass.setEnabled(false);
			
			jLabelSampleRate.setEnabled(false);;
			jSpinnerSampleRate.setEnabled(false);
		}
		
		if (buttQRSPeaksChen.isSelected()){
			buttThreshold.setEnabled(false);
			buttThreshold.setSelected(false);
			buttMAC.setEnabled(false);
		
			
			jLabelThres.setEnabled(false);
			jSpinnerThres.setEnabled(false);
						
			jLabelTau.setEnabled(false);
			jSpinnerTau.setEnabled(false);
	
			jLabelOffset.setEnabled(false);
			jSpinnerOffset.setEnabled(false);
			
			jLabelScaleDown.setEnabled(false);
			jSpinnerScaleDown.setEnabled(false);
			
			buttPositive.setEnabled(false);
			buttNegative.setEnabled(false);
			
			buttCoordinates.setEnabled(true);
			buttIntervals.setEnabled(true);
			buttHeights.setEnabled(false);
			buttDeltaHeights.setEnabled(false);
			buttEnergies.setEnabled(false);
			
			jLabelChenM.setEnabled(true);
			jSpinnerChenM.setEnabled(true);

			jLabelSumInterval.setEnabled(true);
			jSpinnerSumInterval.setEnabled(true);
			
			jLabelPeakFrame.setEnabled(true);
			jSpinnerPeakFrame.setEnabled(true);
			
			buttQRSDetect.setEnabled(false);
			buttQRSDetect.setEnabled(false);
			
			buttQRSDetect2.setEnabled(false);
			buttQRSDetect2.setEnabled(false);
			
			buttQRSBeatDetectClass.setEnabled(false);
			buttQRSBeatDetectClass.setEnabled(false);
			
			jLabelSampleRate.setEnabled(false);;
			jSpinnerSampleRate.setEnabled(false);;
		}
		if (buttQRSPeaksOsea.isSelected()){
			buttThreshold.setEnabled(false);
			buttThreshold.setSelected(false);
			buttMAC.setEnabled(false);
		
			
			jLabelThres.setEnabled(false);
			jSpinnerThres.setEnabled(false);
						
			jLabelTau.setEnabled(false);
			jSpinnerTau.setEnabled(false);
	
			jLabelOffset.setEnabled(false);
			jSpinnerOffset.setEnabled(false);
			
			jLabelScaleDown.setEnabled(false);
			jSpinnerScaleDown.setEnabled(false);
			
			buttPositive.setEnabled(false);
			buttNegative.setEnabled(false);
			
			buttCoordinates.setEnabled(true);
			buttIntervals.setEnabled(true);
			buttHeights.setEnabled(false);
			buttDeltaHeights.setEnabled(false);
			buttEnergies.setEnabled(false);
			
			jLabelChenM.setEnabled(false);
			jSpinnerChenM.setEnabled(false);

			jLabelSumInterval.setEnabled(false);
			jSpinnerSumInterval.setEnabled(false);
			
			jLabelPeakFrame.setEnabled(false);
			jSpinnerPeakFrame.setEnabled(false);
			
			buttQRSDetect.setEnabled(true);
			buttQRSDetect.setEnabled(true);
			
			buttQRSDetect2.setEnabled(true);
			buttQRSDetect2.setEnabled(true);
			
			buttQRSBeatDetectClass.setEnabled(true);
			buttQRSBeatDetectClass.setEnabled(true);
			
			jLabelSampleRate.setEnabled(true);;
			jSpinnerSampleRate.setEnabled(true);;
		}
		this.updateParameterBlock();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
		
//			if (e.getSource() == buttSlope) {
//				
//			}
						
			this.update();
			this.updateParameterBlock();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateParameterBlock();
	}

}
