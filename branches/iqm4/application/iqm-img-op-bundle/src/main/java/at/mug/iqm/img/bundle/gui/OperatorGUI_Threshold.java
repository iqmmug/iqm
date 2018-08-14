package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Threshold.java
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.text.DecimalFormat;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.plot.charts.HistogramXYLineChart;
import at.mug.iqm.commons.util.ColorConverter;
import at.mug.iqm.commons.util.Otsu;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpThresholdDescriptor;


/**
 * @author Ahammer, Kainz, Kleinowitz
 * @since   2009 04
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class OperatorGUI_Threshold 
extends AbstractImageOperatorGUI 
implements ActionListener, ChangeListener, ChartChangeListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7674747507007268841L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_Threshold.class);

	private  ParameterBlockIQM    pb      = null;

	private HistogramXYLineChart hpXYlc1 = null;
	private HistogramXYLineChart hpXYlc2 = null;
	private HistogramXYLineChart hpXYlc3 = null;
	private ChartPanel 			 cp1     = new ChartPanel((JFreeChart) null);
	private ChartPanel 			 cp2     = new ChartPanel((JFreeChart) null);
	private ChartPanel 			 cp3     = new ChartPanel((JFreeChart) null);
	private JFreeChart 			 chart1  = null;
	private JFreeChart 			 chart2  = null;
	private JFreeChart 			 chart3  = null;

	private  int                 thLow1   = 0;
	private  int                 thHigh1  = 255;
	private  int                 thLow2   = 0;
	private  int                 thHigh2  = 255;
	private  int                 thLow3   = 0;
	private  int                 thHigh3  = 255;	

	private  int   numBands    = 0;
	private double typeGreyMax = 0.0d;

	private  JPanel 		jSpinnerPanelThLow1  = null;
	private  JPanel 		jSpinnerPanelThMid1  = null;
	private  JPanel 		jSpinnerPanelThHigh1 = null;
	private  JSpinner	    jSpinnerThLow1       = null;
	private  JSpinner 	    jSpinnerThMid1       = null;
	private  JSpinner 	    jSpinnerThHigh1      = null;
	private  JLabel 		jLabelThLow1;
	private  JLabel 		jLabelThMid1;
	private  JLabel 		jLabelThHigh1;

	private  JPanel 		jSpinnerPanelThLow2  = null;
	private  JPanel 		jSpinnerPanelThMid2  = null;
	private  JPanel 		jSpinnerPanelThHigh2 = null;
	private  JSpinner	    jSpinnerThLow2       = null;
	private  JSpinner 	    jSpinnerThMid2       = null;
	private  JSpinner 	    jSpinnerThHigh2      = null;
	private  JLabel 		jLabelThLow2;
	private  JLabel 		jLabelThMid2;
	private  JLabel 		jLabelThHigh2;

	private  JPanel 		jSpinnerPanelThLow3  = null;
	private  JPanel 		jSpinnerPanelThMid3  = null;
	private  JPanel 		jSpinnerPanelThHigh3 = null;
	private  JSpinner	    jSpinnerThLow3       = null;
	private  JSpinner 	    jSpinnerThMid3       = null;
	private  JSpinner 	    jSpinnerThHigh3      = null;
	private  JLabel 		jLabelThLow3;
	private  JLabel 		jLabelThMid3;
	private  JLabel 		jLabelThHigh3;

	private  JCheckBox 	    jCheckBoxBinarize	 = null;
	private  JCheckBox 	    jCheckBoxLinkSlider	 = null;
	
	
	private ChartPanel chart_1_panel;
	private ChartPanel chart_2_panel;
	private ChartPanel chart_3_panel;
	private JPanel     histogram_1_panel;
	private JPanel     histogram_2_panel;
	private JPanel     histogram_3_panel;
	
	private JComboBox            cbxLLPresets;
	private DefaultComboBoxModel presetModel = new DefaultComboBoxModel(new String[] {"No Preset", "Mean Threshold", "Max Entropy Threshold", "Max Variance Threshold", "Min Error Threshold", "Min Fuzziness Threshold", "Mode Threshold", "PTile Threshold", "Otsu's Threshold"});
	
	private JLabel     lblPresetOptions;
	private JPanel     panel_checkboxes;
	private JPanel     llPresets_panel;
	private JPanel     chart_1_controls;
	private JPanel     chart_2_controls;
	private JPanel     chart_3_controls;
	private JPanel     color_panel;
	private JLabel     lblColor;
	
	private JComboBox  cbxColor;
	private DefaultComboBoxModel colorSpaceModel = new DefaultComboBoxModel(new String[]{""});

	/**
	 * constructor
	 */
	public OperatorGUI_Threshold() {
		logger.debug("Now initializing...");
		getOpGUIContent().setBorder(new EmptyBorder(10, 10, 10, 10));

		this.setOpName(new IqmOpThresholdDescriptor().getName());

		this.initialize(); 

		this.setTitle("Threshold Segmentation");

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		this.getOpGUIContent().setLayout(gridBagLayout);
		
		// add the color tool box
		GridBagConstraints gbc_color_panel = new GridBagConstraints();
		gbc_color_panel.insets = new Insets(0, 0, 5, 0);
		gbc_color_panel.fill = GridBagConstraints.BOTH;
		gbc_color_panel.gridx = 0;
		gbc_color_panel.gridy = 0;
		getOpGUIContent().add(getColor_panel(), gbc_color_panel);

		getOpGUIContent().add(getHistogram_1_panel(), getGbc_histogram_1_panel());
		getOpGUIContent().add(getHistogram_2_panel(), getGbc_histogram_2_panel());
		getOpGUIContent().add(getHistogram_3_panel(), getGbc_histogram_3_panel());
		getOpGUIContent().add(getPanel_checkboxes(),  getGbc_panel_checkboxes());
		getOpGUIContent().add(getLlPresets_panel(),   getGbc_llPresets_panel());
		
		this.pack();
	}

	private GridBagConstraints getGbc_histogram_3_panel() {
		GridBagConstraints gbc_histogram_3_panel = new GridBagConstraints();
		gbc_histogram_3_panel.insets = new Insets(0, 0, 5, 0);
		gbc_histogram_3_panel.gridx = 0;
		gbc_histogram_3_panel.gridy = 3;
		return gbc_histogram_3_panel;
	}

	private GridBagConstraints getGbc_histogram_2_panel() {
		GridBagConstraints gbc_histogram_2_panel = new GridBagConstraints();
		gbc_histogram_2_panel.insets = new Insets(0, 0, 5, 0);
		gbc_histogram_2_panel.gridx = 0;
		gbc_histogram_2_panel.gridy = 2;
		return gbc_histogram_2_panel;
	}

	private GridBagConstraints getGbc_histogram_1_panel() {
		GridBagConstraints gbc_histogram_1_panel = new GridBagConstraints();
		gbc_histogram_1_panel.insets = new Insets(0, 0, 5, 0);
		gbc_histogram_1_panel.gridx = 0;
		gbc_histogram_1_panel.gridy = 1;
		return gbc_histogram_1_panel;
	}
	
	private GridBagConstraints getGbc_panel_checkboxes(){
		GridBagConstraints gbc_panel_checkboxes = new GridBagConstraints();
		gbc_panel_checkboxes.insets = new Insets(0, 0, 5, 0);
		gbc_panel_checkboxes.fill = GridBagConstraints.VERTICAL;
		gbc_panel_checkboxes.gridx = 0;
		gbc_panel_checkboxes.gridy = 4;
		return gbc_panel_checkboxes;
	}
	
	private GridBagConstraints getGbc_llPresets_panel(){
		GridBagConstraints gbc_llPresets_panel = new GridBagConstraints();
		gbc_llPresets_panel.gridx = 0;
		gbc_llPresets_panel.gridy = 5;
		return gbc_llPresets_panel;
	}

	/**
	 * This method sets the current parameter block
	 * The individual values of the GUI  current ParameterBlock
	 *
	 */
	@Override
	public void updateParameterBlock(){
		pb.setParameter("Color", cbxColor.getSelectedIndex());
		
		pb.setParameter("ThresholdLow1", thLow1);
		pb.setParameter("ThresholdHigh1",thHigh1);
		pb.setParameter("ThresholdLow2", thLow2);
		pb.setParameter("ThresholdHigh2",thHigh2);
		pb.setParameter("ThresholdLow3", thLow3);
		pb.setParameter("ThresholdHigh3",thHigh3);

		if (jCheckBoxLinkSlider.isSelected())  pb.setParameter("LinkSlider", 1);
		if (!jCheckBoxLinkSlider.isSelected()) pb.setParameter("LinkSlider", 0);
		if (jCheckBoxBinarize.isSelected())    pb.setParameter("Binarize", 1);
		if (!jCheckBoxBinarize.isSelected())   pb.setParameter("Binarize", 0);

		pb.setParameter("Preset", cbxLLPresets.getSelectedIndex());	
	}

	/**
	 * This method sets the current parameter values
	 *
	 */
	@Override
	public void setParameterValuesToGUI(){

		this.pb = this.workPackage.getParameters();
		
		// don't set the values 
		if (numBands == 0) return;
		
		this.cbxColor.removeItemListener(this);
		this.cbxColor.setSelectedIndex(pb.getIntParameter("Color"));
		this.cbxColor.addItemListener(this);
		
		// set the histograms
		if (numBands == 1 ){
			thLow1  = pb.getIntParameter("ThresholdLow1");
			thHigh1 = pb.getIntParameter("ThresholdHigh1");
			remListeners_chart_1();
			this.setHistogram1(thLow1, (int) (thLow1+thHigh1)/2, thHigh1);
			addListeners_chart_1();
		}
		else if (numBands >= 3){
			thLow1  = pb.getIntParameter("ThresholdLow1");
			thHigh1 = pb.getIntParameter("ThresholdHigh1");
			remListeners_chart_1();
			this.setHistogram1(thLow1, (int) (thLow1+thHigh1)/2, thHigh1);
			addListeners_chart_1();
			
			thLow2  = pb.getIntParameter("ThresholdLow2");
			thHigh2 = pb.getIntParameter("ThresholdHigh2");
			remListeners_chart_2();
			this.setHistogram2(thLow2, (int) (thLow2+thHigh2)/2, thHigh2);
			addListeners_chart_2();
			
			thLow3  = pb.getIntParameter("ThresholdLow3");
			thHigh3 = pb.getIntParameter("ThresholdHigh3");
			remListeners_chart_3();
			this.setHistogram3(thLow3, (int) (thLow3+thHigh3)/2, thHigh3);
			addListeners_chart_3();
		}


		if (pb.getIntParameter("LinkSlider") == 0) jCheckBoxLinkSlider.setSelected(false);
		if (pb.getIntParameter("LinkSlider") == 1) jCheckBoxLinkSlider.setSelected(true);
		if (pb.getIntParameter("Binarize")   == 0) jCheckBoxBinarize.setSelected(false);
		if (pb.getIntParameter("Binarize")   == 1) jCheckBoxBinarize.setSelected(true);

		this.cbxLLPresets.removeItemListener(this);
		this.cbxLLPresets.setSelectedIndex(pb.getIntParameter("Preset"));
		this.cbxLLPresets.addItemListener(this);
	}

	/**
	 * This method updates the GUI. 
	 */
	@Override
	public void update(){ 
		logger.debug("Updating GUI...");

		PlanarImage pi = ((IqmDataBox) this.workPackage.getSources().get(0)).getImage();
		
		numBands = pi.getNumBands();
		typeGreyMax = ImageTools.getImgTypeGreyMax(pi);

		if (numBands == 1){
			this.updateGUIForSingleBand(pi);
		}
		if (numBands >= 3){
			this.updateGUIForThreeBand(pi);
		}
		
		this.pack();
		try{
			this.setParameterValuesToGUI();
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	private void updateGUIForSingleBand(PlanarImage pi){
		cbxColor.removeItemListener(this);
		colorSpaceModel = new DefaultComboBoxModel(new String[]{"Grey"});
		cbxColor.setModel(colorSpaceModel);
		cbxColor.setSelectedIndex(0);
		pb.setParameter("Color", (int) 0);
		cbxColor.addItemListener(this);
		
		this.getOpGUIContent().remove(this.histogram_1_panel);
		this.getOpGUIContent().remove(this.histogram_2_panel);
		this.getOpGUIContent().remove(this.histogram_3_panel);

		// disable the link slider check box
		jCheckBoxLinkSlider.setEnabled(false);

		//change threshold setting only if necessary
		if (thHigh1 > typeGreyMax) thHigh1 = (int) typeGreyMax;  
		if (thLow1 >= thHigh1) thLow1 = 0;
		//########### SET VALUES TO PB MANUALLY
		pb.setParameter("ThresholdLow1", thLow1);
		pb.setParameter("ThresholdHigh1", thHigh1);	
		
		//########### CREATE THE CHART
		// create a histogram out of the image
		Histogram histo = getBandHistogram(pi, 0);

		// remove the histogram (chartpanel) from the panel
		this.chart_1_panel.remove(cp1);

		// put the histogram into a jfreechart panel and name it "grey"
		cp1 = getHistoChartPanel1(histo, "grey");

		// re-add the constructed chart panel 
		this.chart_1_panel.add(cp1);
		
		this.getOpGUIContent().add(this.histogram_1_panel, getGbc_histogram_1_panel());
		
		this.pack();
		int newFrameWidth = getWidth();
		int newFrameHeight = getHeight();
		
		this.setPreferredSize(new Dimension(newFrameWidth, newFrameHeight));
	}
	
	private void updateGUIForThreeBand(PlanarImage pi){
		int color = pb.getIntParameter("Color");
		
		cbxColor.removeItemListener(this);
		if (color == 0){ // this means, that the updated occurs when switching from grey to color
			colorSpaceModel = new DefaultComboBoxModel(new String[] {"", "RGB", "HSV", "HLS", "CIELAB", "CIELUV", "CIEXYZ"});
			cbxColor.setModel(colorSpaceModel);
		}
		
		if (color == 0 || color == 1){
			cbxColor.setSelectedIndex(1);
			color = 1; // mandatory update
		}
		cbxColor.addItemListener(this);

		this.getOpGUIContent().remove(this.histogram_1_panel);
		this.getOpGUIContent().remove(this.histogram_2_panel);
		this.getOpGUIContent().remove(this.histogram_3_panel);

		// disable the link slider check box
		jCheckBoxLinkSlider.setEnabled(true);
		
		//change threshold setting only if necessary
		//########### SET VALUES TO PB MANUALLY
		pb.setParameter("Color", cbxColor.getSelectedIndex());
		
		if (thHigh1 > typeGreyMax) thHigh1 = (int) typeGreyMax;  
		if (thLow1 >= thHigh1) thLow1 = 0;
		pb.setParameter("ThresholdLow1", thLow1);
		pb.setParameter("ThresholdHigh1", thHigh1);	
		
		if (thHigh2 > typeGreyMax) thHigh2 = (int) typeGreyMax;  
		if (thLow2 >= thHigh2) thLow2 = 0;
		pb.setParameter("ThresholdLow2", thLow2);
		pb.setParameter("ThresholdHigh2", thHigh2);	
		
		if (thHigh3 > typeGreyMax) thHigh3 = (int) typeGreyMax;  
		if (thLow3 >= thHigh3) thLow3 = 0;
		pb.setParameter("ThresholdLow3", thLow3);
		pb.setParameter("ThresholdHigh3", thHigh3);	
		//########### SET VALUES TO PB MANUALLY
		
		// convert to other color space than RGB, if applicable
		if (numBands > 1){ 
			pi = convertIfRequired(pi);
		}
		
		// construct the histograms
		if (numBands > 1){		
			//########### HISTOGRAM 1
			this.chart_1_panel.remove(cp1);
			
			Histogram histo1 = getBandHistogram(pi, 0);
			
			if (color == 1)
				cp1 = getHistoChartPanel1(histo1, "red");
			if (color == 2)
				cp1 = getHistoChartPanel1(histo1, "hue");
			if (color == 3)
				cp1 = getHistoChartPanel1(histo1, "L*");
			if (color == 4)
				cp1 = getHistoChartPanel1(histo1, "L*");
			if (color == 5)
				cp1 = getHistoChartPanel1(histo1, "X");
			
			this.chart_1_panel.add(cp1);
			
			//########### HISTOGRAM 2
			this.chart_2_panel.remove(cp2);
			
			Histogram histo2 = getBandHistogram(pi, 1);

			if (color == 1)
				cp2 = getHistoChartPanel2(histo2, "green");
			if (color == 2)
				cp2 = getHistoChartPanel2(histo2, "sat");
			if (color == 3)
				cp2 = getHistoChartPanel2(histo2, "light");
			if (color == 4)
				cp2 = getHistoChartPanel2(histo2, "a*");
			if (color == 5)
				cp2 = getHistoChartPanel2(histo2, "u*");
			if (color == 6)
				cp2 = getHistoChartPanel2(histo2, "Y");
			
			this.chart_2_panel.add(cp2);

			//########### HISTOGRAM 3
			Histogram histo = getBandHistogram(pi, 2);
			
			this.chart_3_panel.remove(cp3);
			
			if (color == 1)
				cp3 = getHistoChartPanel3(histo, "blue");
			if (color == 2)
				cp3 = getHistoChartPanel3(histo, "value");
			if (color == 3)
				cp3 = getHistoChartPanel3(histo, "sat");
			if (color == 4)
				cp3 = getHistoChartPanel3(histo, "b*");
			if (color == 5)
				cp3 = getHistoChartPanel3(histo, "v*");
			if (color == 6)
				cp3 = getHistoChartPanel3(histo, "Z");		
			
			this.chart_3_panel.add(cp3);
		}
			
		// RE-ADD THE HISTOGRAM PANELS
		this.getOpGUIContent().add(this.histogram_1_panel, getGbc_histogram_1_panel());
		this.getOpGUIContent().add(this.histogram_2_panel, getGbc_histogram_2_panel());
		this.getOpGUIContent().add(this.histogram_3_panel, getGbc_histogram_3_panel());
		
		this.setPreferredSize(null);
	}
	
	private TiledImage convertIfRequired(PlanarImage pi){
		int color = pb.getIntParameter("Color");
		Raster raster = pi.getData();		         		
		TiledImage ti = new TiledImage(pi, false);  //true overwrites pi (sharedBuffers)
		if (color==2) {
			ti.setData(ColorConverter.RGBtoHSV(raster));
		}
		else if (color==3) {
			ti.setData(ColorConverter.RGBtoHLS(raster));
		}
		else if (color==4){
			ti.setData(ColorConverter.RGBtoCIELAB(raster));
		}
		else if (color==5){
			ti.setData(ColorConverter.RGBtoCIELUV(raster));
		}
		else if (color==6){
			ti.setData(ColorConverter.RGBtoCIEXYZ(raster));
		}
		else{
			// pi remains unchanged
		}
		return ti;
	}
	
	/**
	 * This method sets the lower threshold value(s) calculated by the preset method
	 */
	protected void setPresetValues() {
		logger.debug("Setting low level preset options...");
		// reset and return if the user has chosen "no preset"
		if (this.cbxLLPresets.getSelectedIndex() == 0){
			return;
		}
		
		// unselect the slider linkage
		pb.setParameter("LinkSlider", 0);
	
		// fetch the image from the tank
		int idx = Application.getManager().getCurrItemIndex();   
		IqmDataBox iqmDataBox = Application.getTank().getCurrentTankIqmDataBoxAt(idx); 
		PlanarImage pi = iqmDataBox.getImage();    
		
		if (numBands > 1){ 
			pi = convertIfRequired(pi);
		}
	
		Histogram histo = this.getHistogram(pi);
		double[] th = null;
	
		switch (this.cbxLLPresets.getSelectedIndex()) {
		case 1:
			th = histo.getMean();
			break;
		case 2:
			th = histo.getMaxEntropyThreshold();
			break;
		case 3:
			th = histo.getMaxVarianceThreshold();
			break;
		case 4:
			th = histo.getMinErrorThreshold();
			break;
		case 5:
			th = histo.getMinFuzzinessThreshold();
			break;
		case 6:
			th = histo.getModeThreshold(2); 
			break;
		case 7:
			th = histo.getPTileThreshold(0.5); 
			break;
		case 8:
			Otsu otsu = new Otsu(histo);
			th = otsu.calcThreshold();
			break;
		default:
			return;
		}
		
		//########### SET VALUES TO PB MANUALLY
		if (numBands == 1){
			thLow1 = (int)th[0];
			thHigh1 = (Integer) jSpinnerThHigh1.getValue();
			pb.setParameter("ThresholdLow1", (int) thLow1);
			pb.setParameter("ThresholdHigh1", (int) thHigh1);
		}
		if (numBands >= 3){
			thLow1 = (int)th[0];
			thHigh1 = (Integer) jSpinnerThHigh1.getValue();
			pb.setParameter("ThresholdLow1", (int) thLow1);
			pb.setParameter("ThresholdHigh1", (int) thHigh1);
			
			thLow2 = (int)th[1];	
			thHigh2 = (Integer) jSpinnerThHigh2.getValue();
			pb.setParameter("ThresholdLow2", (int) thLow2);
			pb.setParameter("ThresholdHigh2", (int) thHigh2);

			thLow3 = (int)th[2];	
			thHigh3 = (Integer) jSpinnerThHigh3.getValue();
			pb.setParameter("ThresholdLow3", (int) thLow3);
			pb.setParameter("ThresholdHigh3", (int) thHigh2);
			//########### SET VALUES TO PB MANUALLY
		}

		pb.setParameter("Preset", cbxLLPresets.getSelectedIndex());
	}

	/**
	 * This method gets the Histogram using JFreeChart
	 * @param Histogram
	 * @return DisplayHistogram
	 */
	private ChartPanel getHistoChartPanel1(Histogram histo, String type){
		hpXYlc1 = new HistogramXYLineChart(histo, type);	
		cp1 = hpXYlc1.getChartPanel();  
		cp1.setPreferredSize(new Dimension(350,130));
		chart1 = cp1.getChart();
		chart1.addChangeListener(this);
		return cp1;  
	}
	/**
	 * This method gets the Histogram using JFreeChart
	 * @param Histogram
	 * @return DisplayHistogram
	 */
	private ChartPanel getHistoChartPanel2(Histogram histo, String type){
		hpXYlc2 = new HistogramXYLineChart(histo, type);	
		cp2 = hpXYlc2.getChartPanel();  
		cp2.setPreferredSize(new Dimension(350,130));
		chart2 = cp2.getChart();
		chart2.addChangeListener(this);
		return cp2;  
	}
	/**
	 * This method gets the Histogram using JFreeChart
	 * @param Histogram
	 * @return DisplayHistogram
	 */
	private ChartPanel getHistoChartPanel3(Histogram histo, String type){
		hpXYlc3 = new HistogramXYLineChart(histo, type);	
		cp3 = hpXYlc3.getChartPanel();  
		cp3.setPreferredSize(new Dimension(350,130));
		chart3 = cp3.getChart();
		chart3.addChangeListener(this);
		return cp3;  
	}


	/**
	 * This method gets the histogram of a band
	 * @param PlanarImage
	 * @param int band
	 * @return Histogram
	 */
	private Histogram getBandHistogram(PlanarImage pi, int band){
		pi = JAI.create("bandselect", pi, new int[] {band});
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);  	
		ParameterBlock pb = new ParameterBlock();  
		pb.addSource(pi);  
		pb.add(null); //Roi
		pb.add(1); //Sampling
		pb.add(1); 
		pb.add(new int[]{(int)typeGreyMax+1}); //Number of bins
		pb.add(new double[]{0}); //Start
		pb.add(new double[]{typeGreyMax+1}); //End
		PlanarImage pi2 = JAI.create("histogram", pb);  
		Histogram histo = (Histogram) pi2.getProperty("histogram");
		return histo;
	}
	/**
	 * This method gets the histogram
	 * @param PlanarImage
	 * @return Histogram
	 */
	private  Histogram getHistogram(PlanarImage pi){
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);  	
		ParameterBlock pb = new ParameterBlock();  
		pb.addSource(pi);  
		pb.add(null); //Roi
		pb.add(1); //Sampling
		pb.add(1); 
		pb.add(new int[]{(int)typeGreyMax+1}); //Number of bins
		pb.add(new double[]{0}); //Start
		pb.add(new double[]{typeGreyMax+1}); //End
		PlanarImage pi2 = JAI.create("histogram", pb);  
		Histogram histo = (Histogram) pi2.getProperty("histogram");
		return histo;
	}

	//-----------------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jSpinnerPanelThLow1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelThLow1() {
		if (jSpinnerPanelThLow1 == null) {
			jSpinnerPanelThLow1 = new JPanel();
			jSpinnerPanelThLow1.setLayout(new BorderLayout());
			jLabelThLow1 = new JLabel("Low ");
			// jLabelThLow1.setPreferredSize(new Dimension(70, 22));
			jLabelThLow1.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerThLow1 = new JSpinner(sModel);
			//jSpinnerThLow1.setPreferredSize(new Dimension(60, 24));
			jSpinnerThLow1.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThLow1.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			
			jSpinnerPanelThLow1.add(jLabelThLow1, BorderLayout.WEST);
			jSpinnerPanelThLow1.add(jSpinnerThLow1, BorderLayout.CENTER);
		}
		return jSpinnerPanelThLow1;
	}
	/**
	 * This method initializes jSpinnerPanelThMid1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelThMid1() {
		if (jSpinnerPanelThMid1 == null) {
			jSpinnerPanelThMid1 = new JPanel();
			jSpinnerPanelThMid1.setLayout(new BorderLayout());
			jLabelThMid1 = new JLabel("  Mid ");
			// jLabelThMid1.setPreferredSize(new Dimension(70, 22));
			jLabelThMid1.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerThMid1 = new JSpinner(sModel);
			//jSpinnerThMid1.setPreferredSize(new Dimension(60, 24));
			jSpinnerThMid1.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThMid1.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
										
			jSpinnerPanelThMid1.add(jLabelThMid1, BorderLayout.WEST);
			jSpinnerPanelThMid1.add(jSpinnerThMid1, BorderLayout.CENTER);
		}
		return jSpinnerPanelThMid1;
	}
	/**
	 * This method initializes jSpinnerPanelThHigh1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelThHigh1() {
		if (jSpinnerPanelThHigh1 == null) {
			jSpinnerPanelThHigh1 = new JPanel();
			jSpinnerPanelThHigh1.setLayout(new BorderLayout());
			jLabelThHigh1 = new JLabel("  High ");
			// jLabelThHigh1.setPreferredSize(new Dimension(70, 22));
			jLabelThHigh1.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerThHigh1 = new JSpinner(sModel);
			//jSpinnerThHigh1.setPreferredSize(new Dimension(60, 24));
			jSpinnerThHigh1.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThHigh1.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
										// ;
			jSpinnerPanelThHigh1.add(jLabelThHigh1, BorderLayout.WEST);
			jSpinnerPanelThHigh1.add(jSpinnerThHigh1, BorderLayout.CENTER);
		}
		return jSpinnerPanelThHigh1;
	}
	
	/**
	 * This method initializes jSpinnerPanelThLow2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelThLow2() {
		if (jSpinnerPanelThLow2 == null) {
			jSpinnerPanelThLow2 = new JPanel();
			jSpinnerPanelThLow2.setLayout(new BorderLayout());
			jLabelThLow2 = new JLabel("Low ");
			// jLabelThLow2.setPreferredSize(new Dimension(70, 22));
			jLabelThLow2.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerThLow2 = new JSpinner(sModel);
			//jSpinnerThLow2.setPreferredSize(new Dimension(60, 24));
			jSpinnerThLow2.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThLow2.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
									
			jSpinnerPanelThLow2.add(jLabelThLow2, BorderLayout.WEST);
			jSpinnerPanelThLow2.add(jSpinnerThLow2, BorderLayout.CENTER);
		}
		return jSpinnerPanelThLow2;
	}
	/**
	 * This method initializes jSpinnerPanelThMid2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelThMid2() {
		if (jSpinnerPanelThMid2 == null) {
			jSpinnerPanelThMid2 = new JPanel();
			jSpinnerPanelThMid2.setLayout(new BorderLayout());
			jLabelThMid2 = new JLabel("  Mid ");
			// jLabelThMid2.setPreferredSize(new Dimension(70, 22));
			jLabelThMid2.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerThMid2 = new JSpinner(sModel);
			//jSpinnerThMid2.setPreferredSize(new Dimension(60, 24));
			jSpinnerThMid2.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThMid2.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
										
			jSpinnerPanelThMid2.add(jLabelThMid2, BorderLayout.WEST);
			jSpinnerPanelThMid2.add(jSpinnerThMid2, BorderLayout.CENTER);
		}
		return jSpinnerPanelThMid2;
	}
	
	/**
	 * This method initializes jSpinnerPanelThHigh2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelThHigh2() {
		if (jSpinnerPanelThHigh2 == null) {
			jSpinnerPanelThHigh2 = new JPanel();
			jSpinnerPanelThHigh2.setLayout(new BorderLayout());
			jLabelThHigh2 = new JLabel("  High ");
			// jLabelThHigh2.setPreferredSize(new Dimension(70, 22));
			jLabelThHigh2.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerThHigh2 = new JSpinner(sModel);
			//jSpinnerThHigh2.setPreferredSize(new Dimension(60, 24));
			jSpinnerThHigh2.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThHigh2.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
										
			jSpinnerPanelThHigh2.add(jLabelThHigh2, BorderLayout.WEST);
			jSpinnerPanelThHigh2.add(jSpinnerThHigh2, BorderLayout.CENTER);
		}
		return jSpinnerPanelThHigh2;
	}
	
	/**
	 * This method initializes jSpinnerPanelThLow3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelThLow3() {
		if (jSpinnerPanelThLow3 == null) {
			jSpinnerPanelThLow3 = new JPanel();
			jSpinnerPanelThLow3.setLayout(new BorderLayout());
			jLabelThLow3 = new JLabel("Low ");
			// jLabelThLow3.setPreferredSize(new Dimension(70, 22));
			jLabelThLow3.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerThLow3 = new JSpinner(sModel);
			//jSpinnerThLow3.setPreferredSize(new Dimension(60, 24));
			jSpinnerThLow3.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThLow3.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
										
			jSpinnerPanelThLow3.add(jLabelThLow3, BorderLayout.WEST);
			jSpinnerPanelThLow3.add(jSpinnerThLow3, BorderLayout.CENTER);
		}
		return jSpinnerPanelThLow3;
	}
	
	/**
	 * This method initializes jSpinnerPanelThMid3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelThMid3() {
		if (jSpinnerPanelThMid3 == null) {
			jSpinnerPanelThMid3 = new JPanel();
			jSpinnerPanelThMid3.setLayout(new BorderLayout());
			jLabelThMid3 = new JLabel("  Mid ");
			// jLabelThMid3.setPreferredSize(new Dimension(70, 22));
			jLabelThMid3.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerThMid3 = new JSpinner(sModel);
			//jSpinnerThMid3.setPreferredSize(new Dimension(60, 24));
			jSpinnerThMid3.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThMid3.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
										
			jSpinnerPanelThMid3.add(jLabelThMid3, BorderLayout.WEST);
			jSpinnerPanelThMid3.add(jSpinnerThMid3, BorderLayout.CENTER);
		}
		return jSpinnerPanelThMid3;
	}
	
	/**
	 * This method initializes jSpinnerPanelThHigh3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJSpinnerPanelThHigh3() {
		if (jSpinnerPanelThHigh3 == null) {
			jSpinnerPanelThHigh3 = new JPanel();
			jSpinnerPanelThHigh3.setLayout(new BorderLayout());
			jLabelThHigh3 = new JLabel("  High ");
			// jLabelThHigh3.setPreferredSize(new Dimension(70, 22));
			jLabelThHigh3.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerThHigh3 = new JSpinner(sModel);
			//jSpinnerThHigh3.setPreferredSize(new Dimension(60, 24));
			jSpinnerThHigh3.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerThHigh3.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
									
			jSpinnerPanelThHigh3.add(jLabelThHigh3, BorderLayout.WEST);
			jSpinnerPanelThHigh3.add(jSpinnerThHigh3, BorderLayout.CENTER);
		}
		return jSpinnerPanelThHigh3;
	}
	//------------------------------------------------------------------------------------	
	/**
	 * This method initializes jCheckBoxLinkSlider	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private  JCheckBox getJCheckBoxLinkSlider() {
		if (jCheckBoxLinkSlider == null) {
			jCheckBoxLinkSlider = new JCheckBox();
			jCheckBoxLinkSlider.setText("Link slider");
			jCheckBoxLinkSlider.addActionListener(this);			
			jCheckBoxLinkSlider.setActionCommand("parameter");			
		}
		return jCheckBoxLinkSlider;
	}
	//------------------------------------------------------------------------------------
	/**
	 * This method initializes jCheckBoxBinarize	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private  JCheckBox getJCheckBoxBinarize() {
		if (jCheckBoxBinarize == null) {
			jCheckBoxBinarize = new JCheckBox();
			jCheckBoxBinarize.setText("Binarize");
			jCheckBoxBinarize.addActionListener(this);			
			jCheckBoxBinarize.setActionCommand("parameter");			
		}
		return jCheckBoxBinarize;
	}
	//------------------------------------------------------------------------------------


	/**
	 * This method sets the values to histogram 1.
	 *
	 * @param low
	 * @param mid
	 * @param high
	 */
	private void setHistogram1(int low, int mid, int high){
		jSpinnerThLow1. setModel(new SpinnerNumberModel(low,  0, (int)typeGreyMax, 1)); // init, min, max, step
		jSpinnerThMid1. setModel(new SpinnerNumberModel(mid,  0, (int)typeGreyMax, 1)); // init, min, max, step
		jSpinnerThHigh1.setModel(new SpinnerNumberModel(high, 0, (int)typeGreyMax, 1));	// init, min, max, step
		hpXYlc1.setMarker(low, "Low");
		hpXYlc1.setMarker(mid, "Mid");
		hpXYlc1.setMarker(high, "High");
	}
	/**
	 * This method sets the values to histogram 2.
	 *
	 * @param low
	 * @param mid
	 * @param high
	 */
	private void setHistogram2(int low, int mid, int high){
		jSpinnerThLow2. setModel(new SpinnerNumberModel(low,  0, (int)typeGreyMax, 1)); // init, min, max, step
		jSpinnerThMid2. setModel(new SpinnerNumberModel(mid,  0, (int)typeGreyMax, 1)); // init, min, max, step
		jSpinnerThHigh2.setModel(new SpinnerNumberModel(high, 0, (int)typeGreyMax, 1));	// init, min, max, step
		hpXYlc2.setMarker(low, "Low");
		hpXYlc2.setMarker(mid, "Mid");
		hpXYlc2.setMarker(high, "High");
	}
	/**
	 * This method sets the values to histogram 3.
	 *
	 * @param low
	 * @param mid
	 * @param high
	 */
	private void setHistogram3(int low, int mid, int high){
		jSpinnerThLow3. setModel(new SpinnerNumberModel(low,  0, (int)typeGreyMax, 1)); // init, min, max, step
		jSpinnerThMid3. setModel(new SpinnerNumberModel(mid,  0, (int)typeGreyMax, 1)); // init, min, max, step
		jSpinnerThHigh3.setModel(new SpinnerNumberModel(high, 0, (int)typeGreyMax, 1));	// init, min, max, step
		hpXYlc3.setMarker(low, "Low");
		hpXYlc3.setMarker(mid, "Mid");
		hpXYlc3.setMarker(high, "High");
	}


	//--------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		logger.debug(e.getSource());

		Object source = e.getSource();
		if (source instanceof JSpinner){
			cbxLLPresets.removeItemListener(this);
			cbxLLPresets.setSelectedIndex(0);
			cbxLLPresets.addItemListener(this);
			if (numBands >= 1){
				remListeners_chart_1();
			}
			if (numBands >= 2){
				remListeners_chart_2();
			}
			if (numBands >=3){
				remListeners_chart_3();
			}
			//ScrollBars1-----------------------------------------------------------------------------
			if (source == jSpinnerThLow1){
				thLow1 = (Integer) jSpinnerThLow1.getValue();
				thHigh1 = (Integer) jSpinnerThHigh1.getValue();
				if (thLow1 > thHigh1) thLow1 = thHigh1;
				int mid1 = (thLow1+thHigh1)/2;
				this.setHistogram1(thLow1, mid1, thHigh1); 
				if (jCheckBoxLinkSlider.isSelected()){
					if (numBands >=2){
						thLow2 = thLow1; thHigh2 = thHigh1;
						this.setHistogram2(thLow2, mid1, thHigh2); 
					}
					if (numBands >=3){
						thLow3 = thLow1; thHigh3 = thHigh1;
						this.setHistogram3(thLow3, mid1, thHigh3); 
					}
				}
				System.out.println("SBL1 -- thLow1: " +thLow1 +"     thHigh1: " +thHigh1);
			}
			else if (source == jSpinnerThMid1){
				int delta1 = thHigh1-thLow1;				
				int mid1 = (Integer) jSpinnerThMid1.getValue();
				if (mid1+(delta1/2) > typeGreyMax) mid1 = (int)typeGreyMax-(delta1/2);
				if (mid1-(delta1/2) < 0)   mid1 = 0  +(delta1/2);	
				thLow1  = mid1-(delta1/2);
				thHigh1 = mid1+(delta1/2);
				this.setHistogram1(thLow1, mid1, thHigh1); 
				if (jCheckBoxLinkSlider.isSelected()){
					if (numBands >=2){
						thLow2 = thLow1; thHigh2 = thHigh1;
						this.setHistogram2(thLow2, mid1, thHigh2); 
					}
					if (numBands >=3){
						thLow3 = thLow1; thHigh3 = thHigh1;
						this.setHistogram3(thLow3, mid1, thHigh3); 
					}
				}
				System.out.println("SBM1 -- thLow1: " +thLow1 +"     thHigh1: " +thHigh1);
			}
			else if (source == jSpinnerThHigh1){
				thLow1 = (Integer) jSpinnerThLow1.getValue();
				thHigh1 = (Integer) jSpinnerThHigh1.getValue();
				if (thHigh1 < thLow1) thHigh1 = thLow1;
				int mid1 = (thLow1+thHigh1)/2;
				this.setHistogram1(thLow1, mid1, thHigh1); 
				if (jCheckBoxLinkSlider.isSelected()){
					if (numBands >=2){
						thLow2 = thLow1; thHigh2 = thHigh1;
						this.setHistogram2(thLow2, mid1, thHigh2); 
					}
					if (numBands >=3){
						thLow3 = thLow1; thHigh3 = thHigh1;
						this.setHistogram3(thLow3, mid1, thHigh3); 
					}
				}
				System.out.println("SBH1 -- thLow1: " +thLow1 +"     thHigh1: " +thHigh1);
			}
			//ScrollBars2-----------------------------------------------------------------------------
			else if (source == jSpinnerThLow2){
				thLow2 = (Integer) jSpinnerThLow2.getValue();
				thHigh2 = (Integer) jSpinnerThHigh2.getValue();
				if (thLow2 > thHigh2) thLow2 = thHigh2;
				int mid2 = (thLow2+thHigh2)/2;
				this.setHistogram2(thLow2, mid2, thHigh2);
				if (jCheckBoxLinkSlider.isSelected()){
					thLow1 = thLow2; thHigh1 = thHigh2;
					this.setHistogram1(thLow1, mid2, thHigh1); 
					if (numBands >=3){
						thLow3 = thLow2; thHigh3 = thHigh2;
						this.setHistogram3(thLow3, mid2, thHigh3); 
					}
				}
				System.out.println("SBL2 -- thLow2: " +thLow2 +"     thHigh2: " +thHigh2);
			}
			else if (source == jSpinnerThMid2){
				int delta2 = thHigh2-thLow2;				
				int mid2 = (Integer) jSpinnerThMid2.getValue();
				if (mid2+(delta2/2) > typeGreyMax) mid2 = (int)typeGreyMax-(delta2/2);
				if (mid2-(delta2/2) < 0)   mid2 = 0  +(delta2/2);	
				thLow2  = mid2-(delta2/2);
				thHigh2 = mid2+(delta2/2);
				this.setHistogram2(thLow2, mid2, thHigh2); 
				if (jCheckBoxLinkSlider.isSelected()){
					thLow1 = thLow2; thHigh1 = thHigh2;
					this.setHistogram1(thLow1, mid2, thHigh1); 
					if (numBands >=3){
						thLow3 = thLow2; thHigh3 = thHigh2;
						this.setHistogram3(thLow3, mid2, thHigh3); 
					}
				}
				System.out.println("SBM2 -- thLow2: " +thLow2 +"     thHigh2: " +thHigh2);
			}
			else if (source == jSpinnerThHigh2){
				thLow2 = (Integer) jSpinnerThLow2.getValue();
				thHigh2 = (Integer) jSpinnerThHigh2.getValue();
				if (thHigh2 < thLow2) thHigh2 = thLow2;
				int mid2 = (thLow2+thHigh2)/2;
				this.setHistogram2(thLow2, mid2, thHigh2);
				if (jCheckBoxLinkSlider.isSelected()){
					thLow1 = thLow2; thHigh1 = thHigh2;
					this.setHistogram1(thLow1, mid2, thHigh1); 
					if (numBands >=3){
						thLow3 = thLow2; thHigh3 = thHigh2;
						this.setHistogram3(thLow3, mid2, thHigh3); 
					}
				}
				System.out.println("SBH2 -- thLow2: " +thLow2 +"     thHigh2: " +thHigh2);
			}
			//ScrollBars3-----------------------------------------------------------------------------
			else if (source == jSpinnerThLow3){
				thLow3 = (Integer) jSpinnerThLow3.getValue();
				thHigh3 = (Integer) jSpinnerThHigh3.getValue();
				if (thLow3 > thHigh3) thLow3 = thHigh3;
				int mid3 = (thLow3+thHigh3)/2;
				this.setHistogram3(thLow3, mid3, thHigh3); 
				if (jCheckBoxLinkSlider.isSelected()){
					thLow1 = thLow3; thHigh1 = thHigh3;
					thLow2 = thLow3; thHigh2 = thHigh3;
					this.setHistogram1(thLow1, mid3, thHigh1); 
					this.setHistogram2(thLow2, mid3, thHigh2); 
				}
				System.out.println("SBL3 -- thLow3: " +thLow3 +"     thHigh3: " +thHigh3);
			}
			else if (source == jSpinnerThMid3){
				int delta3 = thHigh3-thLow3;				
				int mid3 = (Integer) jSpinnerThMid3.getValue();
				if (mid3+(delta3/2) > typeGreyMax) mid3 = (int)typeGreyMax-(delta3/2);
				if (mid3-(delta3/2) < 0)   mid3 = 0  +(delta3/2);	
				thLow3  = mid3-(delta3/2);
				thHigh3 = mid3+(delta3/2);
				this.setHistogram3(thLow3, mid3, thHigh3); 
				if (jCheckBoxLinkSlider.isSelected()){
					thLow1 = thLow3; thHigh1 = thHigh3;
					thLow2 = thLow3; thHigh2 = thHigh3;
					this.setHistogram1(thLow1, mid3, thHigh1); 
					this.setHistogram2(thLow2, mid3, thHigh2); 
				}
				System.out.println("SBM3 -- thLow3: " +thLow3 +"     thHigh3: " +thHigh3);
			}
			else if (source == jSpinnerThHigh3){
				thLow3 = (Integer) jSpinnerThLow3.getValue();
				thHigh3 = (Integer) jSpinnerThHigh3.getValue();
				if (thHigh3 < thLow3) thHigh3 = thLow3;
				int mid3 = (thLow3+thHigh3)/2;
				this.setHistogram3(thLow3, mid3, thHigh3); 
				if (jCheckBoxLinkSlider.isSelected()){
					thLow1 = thLow3; thHigh1 = thHigh3;
					thLow2 = thLow3; thHigh2 = thHigh3;
					this.setHistogram1(thLow1, mid3, thHigh1); 
					this.setHistogram2(thLow2, mid3, thHigh2); 
				}
				System.out.println("SBH3 -- thLow3: " +thLow3 +"     thHigh3: " +thHigh3);
			}
			if (numBands >= 1){
				addListeners_chart_1();
			}
			if (numBands >=2){
				addListeners_chart_2();
			}
			if (numBands >=3){
				addListeners_chart_3();
			}
		}

		this.updateParameterBlock();

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
		
	}
	
	@Override
	public void chartChanged(ChartChangeEvent event) {
//		logger.debug(event.getSource());
//		System.out.println("Chart1: " + chart1);
//		System.out.println("Chart2: " + chart2);
//		System.out.println("Chart3: " + chart3);
		if (event.getSource() == chart1){
			chart1.removeChangeListener(this);
			if (thLow1 != hpXYlc1.binLow){                  //if chart is modified using ctrl mouse dragged
				jSpinnerThLow1.setValue(hpXYlc1.binLow);  //triggers Adjustment Event and setMarker
			}
			if ((thLow1+thHigh1)/2 != hpXYlc1.binMid){ 
				jSpinnerThMid1.setValue(hpXYlc1.binMid);  
			}
			if (thHigh1 != hpXYlc1.binHigh){ 
				jSpinnerThHigh1.setValue(hpXYlc1.binHigh); 
			}
			chart1.addChangeListener(this);		 
		}
		else if (event.getSource() == chart2){
			System.out.println(event.getSource()== chart2);
			chart2.removeChangeListener(this);
			if (thLow2 != hpXYlc2.binLow){                  //if chart is modified using ctrl mouse dragged
				jSpinnerThLow2.setValue(hpXYlc2.binLow);  //triggers Adjustment Event and setMarker
			}
			if ((thLow2+thHigh2)/2 != hpXYlc2.binMid){ 
				jSpinnerThMid2.setValue(hpXYlc2.binMid);  
			}
			if (thHigh2 != hpXYlc2.binHigh){ 
				jSpinnerThHigh2.setValue(hpXYlc2.binHigh); 
			}
			chart2.addChangeListener(this);	
		}
		else if (event.getSource() == chart3){
			chart3.removeChangeListener(this);
			if (thLow3 != hpXYlc3.binLow){                  //if chart is modified using ctrl mouse dragged 
				jSpinnerThLow3.setValue(hpXYlc3.binLow);  //triggers Adjustment Event and setMarker
			}
			if ((thLow3+thHigh3)/2 != hpXYlc3.binMid){ 
				jSpinnerThMid3.setValue(hpXYlc3.binMid);  
			}
			if (thHigh3 != hpXYlc3.binHigh){ 
				jSpinnerThHigh3.setValue(hpXYlc3.binHigh); 
			}
			chart3.addChangeListener(this);		 
		}
		this.updateParameterBlock();

		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
	private JPanel getHistogram_1_panel() {
		if (histogram_1_panel == null) {
			histogram_1_panel = new JPanel();
			histogram_1_panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			GridBagLayout gbl_histogram_1_panel = new GridBagLayout();
			gbl_histogram_1_panel.columnWeights = new double[]{0.0};
			gbl_histogram_1_panel.rowWeights = new double[]{0.0, 0.0};
			histogram_1_panel.setLayout(gbl_histogram_1_panel);

			chart_1_panel = new ChartPanel((JFreeChart) null);
			chart_1_panel.setPreferredSize(new Dimension(350, 130));
			GridBagConstraints gbc_chart_1_panel = new GridBagConstraints();
			gbc_chart_1_panel.gridx = 0;
			gbc_chart_1_panel.gridy = 0;
			histogram_1_panel.add(chart_1_panel, gbc_chart_1_panel);
			chart_1_panel.setLayout(new BorderLayout(0, 0));
			GridBagConstraints gbc_chart_1_controls = new GridBagConstraints();
			gbc_chart_1_controls.fill = GridBagConstraints.BOTH;
			gbc_chart_1_controls.gridx = 0;
			gbc_chart_1_controls.gridy = 1;
			histogram_1_panel.add(getChart_1_controls(), gbc_chart_1_controls);
		}
		return histogram_1_panel;
	}
	private JPanel getHistogram_2_panel() {
		if (histogram_2_panel == null) {
			histogram_2_panel = new JPanel();
			histogram_2_panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			GridBagLayout gbl_histogram_2_panel = new GridBagLayout();
			gbl_histogram_2_panel.columnWidths = new int[]{0, 0};
			gbl_histogram_2_panel.rowHeights = new int[]{0, 0, 0};
			gbl_histogram_2_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_histogram_2_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			histogram_2_panel.setLayout(gbl_histogram_2_panel);

			chart_2_panel = new ChartPanel((JFreeChart) null);
			chart_2_panel.setPreferredSize(new Dimension(350, 130));
			GridBagConstraints gbc_chart_2_panel = new GridBagConstraints();
			gbc_chart_2_panel.gridx = 0;
			gbc_chart_2_panel.gridy = 0;
			histogram_2_panel.add(chart_2_panel, gbc_chart_2_panel);
			chart_2_panel.setLayout(new BorderLayout(0, 0));
			GridBagConstraints gbc_chart_2_controls = new GridBagConstraints();
			gbc_chart_2_controls.fill = GridBagConstraints.BOTH;
			gbc_chart_2_controls.gridx = 0;
			gbc_chart_2_controls.gridy = 1;
			histogram_2_panel.add(getChart_2_controls(), gbc_chart_2_controls);
		}
		return histogram_2_panel;
	}
	private JPanel getHistogram_3_panel() {
		if (histogram_3_panel == null) {
			histogram_3_panel = new JPanel();
			histogram_3_panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			GridBagLayout gbl_histogram_3_panel = new GridBagLayout();
			gbl_histogram_3_panel.columnWidths = new int[]{0, 0};
			gbl_histogram_3_panel.rowHeights = new int[]{0, 0, 0};
			gbl_histogram_3_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_histogram_3_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			histogram_3_panel.setLayout(gbl_histogram_3_panel);


			chart_3_panel = new ChartPanel((JFreeChart) null);
			chart_3_panel.setPreferredSize(new Dimension(350, 130));
			GridBagConstraints gbc_chart_3_panel = new GridBagConstraints();
			gbc_chart_3_panel.gridx = 0;
			gbc_chart_3_panel.gridy = 0;
			histogram_3_panel.add(chart_3_panel, gbc_chart_3_panel);
			chart_3_panel.setLayout(new BorderLayout(0, 0));
			GridBagConstraints gbc_chart_3_controls = new GridBagConstraints();
			gbc_chart_3_controls.fill = GridBagConstraints.BOTH;
			gbc_chart_3_controls.gridx = 0;
			gbc_chart_3_controls.gridy = 1;
			histogram_3_panel.add(getChart_3_controls(), gbc_chart_3_controls);
		}
		return histogram_3_panel;
	}
	private JComboBox getCbxLLPresets() {
		if (cbxLLPresets == null) {
			cbxLLPresets = new JComboBox();
			cbxLLPresets.setAlignmentX(Component.LEFT_ALIGNMENT);
			cbxLLPresets.setModel(this.presetModel);
			cbxLLPresets.setActionCommand("llPresetChanged");
			cbxLLPresets.setMaximumRowCount(15);
			cbxLLPresets.setPreferredSize(new Dimension(160, 22));
			cbxLLPresets.setSelectedIndex(0);
			cbxLLPresets.addItemListener(this);
		}
		return cbxLLPresets;
	}
	private JLabel getLblPresetOptions() {
		if (lblPresetOptions == null) {
			lblPresetOptions = new JLabel();
			lblPresetOptions.setHorizontalAlignment(SwingConstants.TRAILING);
			lblPresetOptions.setAlignmentX(Component.RIGHT_ALIGNMENT);
			lblPresetOptions.setText("Low level preset options:");
		}
		return lblPresetOptions;
	}
	private JPanel getPanel_checkboxes() {
		if (panel_checkboxes == null) {
			panel_checkboxes = new JPanel();
			panel_checkboxes.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			panel_checkboxes.add(getJCheckBoxLinkSlider());
			panel_checkboxes.add(getJCheckBoxBinarize());
		}
		return panel_checkboxes;
	}
	private JPanel getLlPresets_panel() {
		if (llPresets_panel == null) {
			llPresets_panel = new JPanel();
			llPresets_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			llPresets_panel.add(getLblPresetOptions());
			llPresets_panel.add(getCbxLLPresets());
		}
		return llPresets_panel;
	}

	private void handleChangeEventLLPresets() {
		this.setPresetValues();
		this.setParameterValuesToGUI();
		
		//preview if selected
		if (this.isAutoPreviewSelected()){
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
	
	private void handleChangeEventColor(){
		this.updateParameterBlock();  //essential for buttons
		this.update();  //2011 11 05  bugfix: is necessary for GUI histogram update after color model change
	}

	private JPanel getChart_1_controls() {
		if (chart_1_controls == null) {
			chart_1_controls = new JPanel();
			chart_1_controls.setLayout(new GridLayout(0, 3, 5, 0));
			chart_1_controls.add(getJSpinnerPanelThLow1());
			chart_1_controls.add(getJSpinnerPanelThMid1());
			chart_1_controls.add(getJSpinnerPanelThHigh1());
		}
		return chart_1_controls;
	}
	private JPanel getChart_2_controls() {
		if (chart_2_controls == null) {
			chart_2_controls = new JPanel();
			chart_2_controls.setLayout(new GridLayout(0, 3, 5, 0));
			chart_2_controls.add(getJSpinnerPanelThLow2());
			chart_2_controls.add(getJSpinnerPanelThMid2());
			chart_2_controls.add(getJSpinnerPanelThHigh2());
		}
		return chart_2_controls;
	}
	private JPanel getChart_3_controls() {
		if (chart_3_controls == null) {
			chart_3_controls = new JPanel();
			chart_3_controls.setLayout(new GridLayout(0, 3, 5, 0));
			chart_3_controls.add(getJSpinnerPanelThLow3());
			chart_3_controls.add(getJSpinnerPanelThMid3());
			chart_3_controls.add(getJSpinnerPanelThHigh3());
		}
		return chart_3_controls;
	}

	/**
	 * Add this instance as {@link AdjustmentListener} for the {@link jSpinner}s of 
	 * histogram panel 3.
	 */
	private void addListeners_chart_3() {
		jSpinnerThLow3.addChangeListener(this);
		jSpinnerThMid3.addChangeListener(this);
		jSpinnerThHigh3.addChangeListener(this);
		chart3.addChangeListener(this);
	}

	/**
	 * Remove this instance as {@link AdjustmentListener} for the {@link jSpinner}s of 
	 * histogram panel 3.
	 */
	private void remListeners_chart_3() {
		jSpinnerThLow3.removeChangeListener(this);
		jSpinnerThMid3.removeChangeListener(this);
		jSpinnerThHigh3.removeChangeListener(this);
		chart3.removeChangeListener(this);
	}

	/**
	 * Add this instance as {@link AdjustmentListener} for the {@link jSpinner}s of 
	 * histogram panel 2.
	 */
	private void addListeners_chart_2() {
		jSpinnerThLow2.addChangeListener(this);
		jSpinnerThMid2.addChangeListener(this);
		jSpinnerThHigh2.addChangeListener(this);
		chart2.addChangeListener(this);
	}

	/**
	 * Remove this instance as {@link AdjustmentListener} for the {@link jSpinner}s of 
	 * histogram panel 2.
	 */
	private void remListeners_chart_2() {
		jSpinnerThLow2.removeChangeListener(this);
		jSpinnerThMid2.removeChangeListener(this);
		jSpinnerThHigh2.removeChangeListener(this);
		chart2.removeChangeListener(this);
	}

	/**
	 * Add this instance as {@link AdjustmentListener} for the {@link jSpinner}s of 
	 * histogram panel 1.
	 */
	private void addListeners_chart_1() {
		jSpinnerThLow1.addChangeListener(this);
		jSpinnerThMid1.addChangeListener(this);
		jSpinnerThHigh1.addChangeListener(this);
		chart1.addChangeListener(this);
	}

	/**
	 * Remove this instance as {@link AdjustmentListener} for the {@link jSpinner}s of 
	 * histogram panel 1.
	 */
	private void remListeners_chart_1() {
		jSpinnerThLow1.removeChangeListener(this);
		jSpinnerThMid1.removeChangeListener(this);
		jSpinnerThHigh1.removeChangeListener(this);
		chart1.removeChangeListener(this);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == cbxLLPresets){
			if (cbxLLPresets.getSelectedIndex() != 0
					&& e.getStateChange() == ItemEvent.SELECTED){
				//				logger.debug(e.getSource());
				System.out.println(cbxLLPresets.getSelectedIndex());
				System.out.println(e.getStateChange() == ItemEvent.SELECTED);
				handleChangeEventLLPresets();

			}
		}
		if (e.getSource() == cbxColor){
			if (e.getStateChange() == ItemEvent.SELECTED){
				if (numBands >= 3)
					if (cbxColor.getSelectedIndex() == 0 
							|| cbxColor.getSelectedIndex() == 1){
					cbxColor.removeItemListener(this);
					cbxColor.setSelectedIndex(1);
					handleChangeEventColor();
					cbxColor.addItemListener(this);
				}else{
					handleChangeEventColor();
				}
//				logger.debug(e.getSource());
//				System.out.println(cbxColor.getSelectedIndex());
//				System.out.println(e.getStateChange() == ItemEvent.SELECTED);
			}
		}
	}
	private JPanel getColor_panel() {
		if (color_panel == null) {
			color_panel = new JPanel();
			color_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			color_panel.add(getLblColor());
			color_panel.add(getCbxColor());
		}
		return color_panel;
	}
	private JLabel getLblColor() {
		if (lblColor == null) {
			lblColor = new JLabel();
			lblColor.setText("Color space:");
			lblColor.setHorizontalAlignment(SwingConstants.TRAILING);
			lblColor.setAlignmentX(1.0f);
		}
		return lblColor;
	}
	private JComboBox getCbxColor() {
		if (cbxColor == null) {
			cbxColor = new JComboBox();
			cbxColor.setModel(colorSpaceModel);
			cbxColor.setSelectedIndex(-1);
			cbxColor.setPreferredSize(new Dimension(160, 22));
			cbxColor.setMaximumRowCount(15);
			cbxColor.setAlignmentX(0.0f);
			cbxColor.addItemListener(this);
		}
		return cbxColor;
	}


}//END
