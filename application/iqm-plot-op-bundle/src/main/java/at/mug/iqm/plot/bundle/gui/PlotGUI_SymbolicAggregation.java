package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_SymbolicAggregation.java
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
import java.text.DecimalFormat;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpSymbolicAggregationDescriptor;

/**
 * According to Lin, Keogh, Lonardi Chiu, A Symbolic Representation of Time Series, with Implications for Streaming Algorithms. 
 * Proceedings of the 8th ACM SIGMOD Workshop on Research Issues in Data Mining and Knowledge Discovery 2003
 * 
 * @author Ahammer
 * @since  2014 01
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 * 
 */
public class PlotGUI_SymbolicAggregation extends AbstractPlotOperatorGUI implements ChangeListener {

	private static final Logger logger = LogManager.getLogger(PlotGUI_SymbolicAggregation.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8971570064739948207L;

	private ParameterBlockIQM pb;

	private JPanel   jPanelAggLength   = null;
	private JLabel   jLabelAggLength   = null;
	private JSpinner jSpinnerAggLength = null;

	private JPanel   jPanelAlphabetSize   = null;
	private JLabel   jLabelAlphabetSize   = null;
	private JSpinner jSpinnerAlphabetSize = null;

	private JPanel   jPanelWordLength   = null;
	private JLabel   jLabelWordLength   = null;
	private JSpinner jSpinnerWordLength = null;

	private JPanel   jPanelSubWordLength   = null;
	private JLabel   jLabelSubWordLength   = null;
	private JSpinner jSpinnerSubWordLength = null;

	private JPanel   jPanelMag   = null;
	private JLabel   jLabelMag   = null;
	private JSpinner jSpinnerMag = null;
	
	private JPanel   jPanelImageSize = null;
	private JLabel   jLabelImageSize = null;
	
	private JPanel   jPanelOptions = null;
	
	private JPanel       jPanelColorModel    = null;
	private ButtonGroup  buttGroupColorModel = null;
	private JRadioButton buttGrey            = null;
	private JRadioButton buttColor           = null;
	
	
	/**
	 * This method calculates the size of the output image and sets the GUI element
	 */
	private void calcAndSetImageSize() {
		int alphabetSize  = ((Number) jSpinnerAlphabetSize.getValue()).intValue();
		int subWordLength = ((Number) jSpinnerSubWordLength.getValue()).intValue();
		int mag           = ((Number) jSpinnerMag.getValue()).intValue();	
		int imageSize     = ((int) (Math.sqrt(alphabetSize))) * (int) Math.pow(2, subWordLength-1) * mag;
		jLabelImageSize.setText("Image size: " + imageSize + "x" + imageSize);
	}

	

	public PlotGUI_SymbolicAggregation() {
		getOpGUIContent().setBorder(new EmptyBorder(10, 10, 10, 10));
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpSymbolicAggregationDescriptor().getName());
		this.initialize();
		this.setTitle("Plot Symbolic Aggregation");
		this.getOpGUIContent().setLayout(new GridBagLayout());
	
		this.getOpGUIContent().add(getJPanelOptions(),       getGBC_Options());
		this.getOpGUIContent().add(getJPanelColorModel(),    getGBC_ColorModel());
		
		this.pack();
	}
	private GridBagConstraints getGBC_Options() {
		GridBagConstraints gbc_Options = new GridBagConstraints();
		gbc_Options.gridx = 0;
		gbc_Options.gridy = 0;
		gbc_Options.gridwidth = 1;
		gbc_Options.anchor = GridBagConstraints.EAST;
		gbc_Options.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_Options.fill = GridBagConstraints.BOTH;
		return gbc_Options;
	}
	private GridBagConstraints getGBC_ColorModel() {
		GridBagConstraints gbc_ColorModel = new GridBagConstraints();
		gbc_ColorModel.gridx = 0;
		gbc_ColorModel.gridy = 1;
		gbc_ColorModel.gridwidth = 1;
		gbc_ColorModel.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gbc_ColorModel.fill = GridBagConstraints.BOTH;
		return gbc_ColorModel;
	}

	

	// ------------------------------------------------------------------------------------------------------
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();
		
		this.removeAllChangeListeners();
		jSpinnerAggLength.setValue     (pb.getIntParameter("aggLength"));	
		jSpinnerAlphabetSize.setValue  (pb.getIntParameter("alphabetSize"));
		jSpinnerWordLength.setValue    (pb.getIntParameter("wordLength"));
		jSpinnerSubWordLength.setValue (pb.getIntParameter("subWordLength"));
		jSpinnerMag.setValue           (pb.getIntParameter("mag"));
		this.addAllChangeListeners();
			
		if (pb.getIntParameter("colorModel") == PlotOpSymbolicAggregationDescriptor.COLORMODEL_GREY)  buttGrey.setSelected(true);
		if (pb.getIntParameter("colorModel") == PlotOpSymbolicAggregationDescriptor.COLORMODEL_COLOR) buttColor.setSelected(true);
			
		this.calcAndSetImageSize();	
	}

	private void addAllChangeListeners() {
		jSpinnerAlphabetSize.addChangeListener(this);
		jSpinnerWordLength.addChangeListener(this);
		jSpinnerSubWordLength.addChangeListener(this);
		jSpinnerMag.addChangeListener(this);	
		jSpinnerAggLength.addChangeListener(this);
	}

	private void removeAllChangeListeners() {
		jSpinnerAlphabetSize.removeChangeListener(this);
		jSpinnerWordLength.removeChangeListener(this);
		jSpinnerSubWordLength.removeChangeListener(this);
		jSpinnerMag.removeChangeListener(this);
		jSpinnerAggLength.removeChangeListener(this);
	}

	@Override
	public void updateParameterBlock() {
			
		pb.setParameter("aggLength",    ((Number) jSpinnerAggLength.getValue()).intValue());	
		pb.setParameter("alphabetSize", ((Number) jSpinnerAlphabetSize.getValue()).intValue());
		pb.setParameter("wordLength",   ((Number) jSpinnerWordLength.getValue()).intValue());
		pb.setParameter("subWordLength",((Number) jSpinnerSubWordLength.getValue()).intValue());
		pb.setParameter("mag",          ((Number) jSpinnerMag.getValue()).intValue());
		
		if (this.buttGrey.isSelected())   pb.setParameter("colorModel", PlotOpSymbolicAggregationDescriptor.COLORMODEL_GREY); 
		if (this.buttColor.isSelected())  pb.setParameter("colorModel", PlotOpSymbolicAggregationDescriptor.COLORMODEL_COLOR); 
	}

	//----------------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes jJPanelAggLength
	 * 
	 * @return javax.swing.JPanel
	 */

	private JPanel getJPanelAggLength() {
		if (jPanelAggLength == null) {
			jPanelAggLength = new JPanel();
			jPanelAggLength.setBorder(new EmptyBorder(0, 0, 0, 0));
			jPanelAggLength.setLayout(new BorderLayout());
			jLabelAggLength = new JLabel("Aggregation length: ");
			jLabelAggLength.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(2, 2, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerAggLength = new JSpinner(sModel);
			jSpinnerAggLength.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerAggLength.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelAggLength.add(jLabelAggLength, BorderLayout.WEST);
			jPanelAggLength.add(jSpinnerAggLength, BorderLayout.CENTER);
		}
		return jPanelAggLength;
	}
	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelAlphabetSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelAlphabetSize() {
		if (jPanelAlphabetSize == null) {
			jPanelAlphabetSize = new JPanel();
			jPanelAlphabetSize.setBorder(new EmptyBorder(0, 0, 0, 0));
			jPanelAlphabetSize.setLayout(new BorderLayout());
			jLabelAlphabetSize = new JLabel("Alphabet size: ");
			jLabelAlphabetSize.setToolTipText("number of distinct characters");
			jLabelAlphabetSize.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(4, 2, 10, 1); // init, min, max, step
			jSpinnerAlphabetSize = new JSpinner(sModel);
			jSpinnerAlphabetSize.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerAlphabetSize.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelAlphabetSize.add(jLabelAlphabetSize, BorderLayout.WEST);
			jPanelAlphabetSize.add(jSpinnerAlphabetSize, BorderLayout.CENTER);
			jSpinnerAlphabetSize.setEnabled(false);
		}
		return jPanelAlphabetSize;
	}



	/**
	 * This method initializes jJPanelWordLength
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelWordLength() {
		if (jPanelWordLength == null) {
			jPanelWordLength = new JPanel();
			jPanelWordLength.setBorder(new EmptyBorder(0, 0, 0, 0));
			jPanelWordLength.setLayout(new BorderLayout());
			jLabelWordLength = new JLabel("Word length: ");
			jLabelWordLength.setToolTipText("number of alphabet characters");
			jLabelWordLength.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(2, 1, Integer.MAX_VALUE, 1); // init, min,  max, step
			jSpinnerWordLength = new JSpinner(sModel);
			jSpinnerWordLength.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerWordLength.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelWordLength.add(jLabelWordLength, BorderLayout.WEST);
			jPanelWordLength.add(jSpinnerWordLength, BorderLayout.CENTER);
		}
		return jPanelWordLength;
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelSubWordLength
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSubWordLength() {
		if (jPanelSubWordLength == null) {
			jPanelSubWordLength = new JPanel();
			jPanelSubWordLength.setBorder(new EmptyBorder(0, 0, 0, 0));
			jPanelSubWordLength.setLayout(new BorderLayout());
			jLabelSubWordLength = new JLabel("Sub word length: ");
			jLabelSubWordLength.setToolTipText("number of characters for reconstruction - Chaos game");
			jLabelSubWordLength.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1); // init, min, max,step
			jSpinnerSubWordLength = new JSpinner(sModel);
			jSpinnerSubWordLength.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerSubWordLength.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelSubWordLength.add(jLabelSubWordLength, BorderLayout.WEST);
			jPanelSubWordLength.add(jSpinnerSubWordLength, BorderLayout.CENTER);
		}
		return jPanelSubWordLength;
	}
	
	/**
	 * This method initializes jJPanelMag
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMag() {
		if (jPanelMag == null) {
			jPanelMag = new JPanel();
			jPanelMag.setBorder(new EmptyBorder(0, 0, 0, 0));
			jPanelMag.setLayout(new BorderLayout());
			jLabelMag = new JLabel("Magnification: ");
			jLabelMag.setToolTipText("Magnification for output image");
			jLabelMag.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(100, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerMag = new JSpinner(sModel);
			jSpinnerMag.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerMag.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(4);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0");
			jPanelMag.add(jLabelMag, BorderLayout.WEST);
			jPanelMag.add(jSpinnerMag, BorderLayout.CENTER);		
		}
		return jPanelMag;
	}

	// ----------------------------------------------------------------------------------------------------------
	
	/**
	 * This method initializes jJPanelImageSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelImageSize() {
		if (jPanelImageSize == null) {
			jPanelImageSize = new JPanel();
			jPanelImageSize.setBorder(new EmptyBorder(0, 0, 0, 0));
			jPanelImageSize.setLayout(new BorderLayout());
			jLabelImageSize = new JLabel("ImageSize: ");
			jLabelImageSize.setToolTipText("Size of output image");
			jLabelImageSize.setHorizontalAlignment(SwingConstants.RIGHT);
			// jLabelImageSize.setPreferredSize(new Dimension(70, 22));
			jPanelImageSize.add(jLabelImageSize, BorderLayout.CENTER);	
		}
		return jPanelImageSize;
	}
	private GridBagConstraints getGBC_AggLength() {
		GridBagConstraints gbc_AggLength = new GridBagConstraints();
		gbc_AggLength.gridx = 0;
		gbc_AggLength.gridy = 0;
		gbc_AggLength.gridwidth = 1;
		gbc_AggLength.anchor = GridBagConstraints.EAST;
		gbc_AggLength.insets = new Insets(5, 5, 0, 5); // top left bottom right
		//gbc_AggLength.fill = GridBagConstraints.BOTH;
		return gbc_AggLength;
	}
	private GridBagConstraints getGBC_AlphabetSize() {
		GridBagConstraints gbc_AlphabetSize = new GridBagConstraints();
		gbc_AlphabetSize.gridx = 0;
		gbc_AlphabetSize.gridy = 1;
		gbc_AlphabetSize.gridwidth = 1;
		gbc_AlphabetSize.anchor = GridBagConstraints.EAST;
		gbc_AlphabetSize.insets = new Insets(5, 5, 0, 5); // top left bottom right
		//gbc_AlphabetSize.fill = GridBagConstraints.BOTH;
		return gbc_AlphabetSize;
	}
	private GridBagConstraints getGBC_WordLength() {
		GridBagConstraints gbc_WordLength = new GridBagConstraints();
		gbc_WordLength.gridx = 0;
		gbc_WordLength.gridy = 2;
		gbc_WordLength.gridwidth = 1;
		gbc_WordLength.anchor = GridBagConstraints.EAST;
		gbc_WordLength.insets = new Insets(20, 5, 0, 5); // top left bottom right
		//gbc_WordLength.fill = GridBagConstraints.BOTH;
		return gbc_WordLength;
	}
	private GridBagConstraints getGBC_SubWordLength() {
		GridBagConstraints gbc_SubWordLength = new GridBagConstraints();
		gbc_SubWordLength.gridx = 0;
		gbc_SubWordLength.gridy = 3;
		gbc_SubWordLength.gridwidth = 1;
		gbc_SubWordLength.anchor = GridBagConstraints.EAST;
		gbc_SubWordLength.insets = new Insets(5, 5, 0, 5); // top left bottom right
		//gbc_SubWordLength.fill = GridBagConstraints.BOTH;
		return gbc_SubWordLength;
	}
	private GridBagConstraints getGBC_Mag() {
		GridBagConstraints gbc_Mag = new GridBagConstraints();
		gbc_Mag.gridx = 0;
		gbc_Mag.gridy = 4;
		gbc_Mag.gridwidth = 1;
		gbc_Mag.anchor = GridBagConstraints.EAST;
		gbc_Mag.insets = new Insets(20, 5, 0, 5); // top left bottom right
		//gbc_Mag.fill = GridBagConstraints.BOTH;
		return gbc_Mag;
	}
	private GridBagConstraints getGBC_ImageSize() {
		GridBagConstraints gbc_ImageSize = new GridBagConstraints();
		gbc_ImageSize.gridx = 0;
		gbc_ImageSize.gridy = 5;
		gbc_ImageSize.gridwidth = 1;
		gbc_ImageSize.anchor = GridBagConstraints.EAST;
		gbc_ImageSize.insets = new Insets(5, 5, 5, 5); // top left bottom right
		//gbc_ImageSize.fill = GridBagConstraints.BOTH;
		return gbc_ImageSize;
	}
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOptions() {
		jPanelOptions = new JPanel();
		//jPanelOptions.setLayout(new BoxLayout(jPanelOptions, BoxLayout.X_AXIS));
		//jPanelOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelOptions.setLayout(new GridBagLayout());
		jPanelOptions.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		jPanelOptions.add(getJPanelAggLength(),     getGBC_AggLength());	
		jPanelOptions.add(getJPanelAlphabetSize(),  getGBC_AlphabetSize());	
		jPanelOptions.add(getJPanelWordLength(),    getGBC_WordLength());
		jPanelOptions.add(getJPanelSubWordLength(), getGBC_SubWordLength());
		jPanelOptions.add(getJPanelMag(),           getGBC_Mag());
		jPanelOptions.add(getJPanelImageSize(),     getGBC_ImageSize());
		return jPanelOptions;
	}
	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Grey
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonGrey() {
		if (buttGrey == null) {
			buttGrey = new JRadioButton();
			buttGrey.setText("Grey");
			buttGrey.setToolTipText("grey level output image");
			buttGrey.addActionListener(this);
			buttGrey.setActionCommand("parameter");
		}
		return buttGrey;
	}

	/**
	 * This method initializes the Option: Color
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonColor() {
		if (buttColor == null) {
			buttColor = new JRadioButton();
			buttColor.setText("Color");
			buttColor.setToolTipText("color output image");
			buttColor.addActionListener(this);
			buttColor.setActionCommand("parameter");
		}
		return buttColor;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelColorModel() {
		jPanelColorModel = new JPanel();
		//jPanelColorModel.setLayout(new BoxLayout(jPanelColorModel, BoxLayout.X_AXIS));
		jPanelColorModel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelColorModel.setBorder(new TitledBorder(null, "Color model", TitledBorder.LEADING, TitledBorder.TOP, null, null));		

		jPanelColorModel.add(getJRadioButtonGrey());
		jPanelColorModel.add(getJRadioButtonColor());
		this.setButtonGroupColorModel(); // Grouping of JRadioButtons
		return jPanelColorModel;
	}

	private void setButtonGroupColorModel() {
		buttGroupColorModel = new ButtonGroup();
		buttGroupColorModel.add(buttGrey);
		buttGroupColorModel.add(buttColor);
	}
	// ----------------------------------------------------------------------------------------------------------

	@Override
	public void update() {
	
		this.calcAndSetImageSize();
		this.pack();
		this.updateParameterBlock();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			this.update();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.removeAllChangeListeners();
		if ( e.getSource() == this.jSpinnerAggLength) {	
		}
		if ( e.getSource() == this.jSpinnerAlphabetSize) {
//			int spinnerAlphabetSize = ((Number) jSpinnerAlphabetSize.getValue()).intValue();
//			int paramAlphabetSize   = pb.getIntParameter("alphabetSize");
//			int newValue = 0;
//			if (spinnerAlphabetSize > paramAlphabetSize){
//				newValue = (int) Math.sqrt((double)paramAlphabetSize);
//				newValue = (newValue + 1) * (newValue + 1);
//			} else {
//				newValue = (int) Math.sqrt((double)paramAlphabetSize);
//				newValue = (newValue - 1) * (newValue - 1);
//			}
//			if (newValue < 4) newValue = 4;		
//			jSpinnerAlphabetSize.setValue  (newValue);		
//			
//			if  (newValue > ((Number) jSpinnerMag.getValue()).intValue()){
//				jSpinnerMag.setValue  (newValue);
//			}			
		}
		if ( e.getSource() == this.jSpinnerWordLength) {	
		}
		if ( e.getSource() == this.jSpinnerSubWordLength) {
		}
		if ( e.getSource() == this.jSpinnerMag){ 
		}
		
		int wordLength    = ((Number) jSpinnerWordLength.getValue()).intValue();
		int subWordLength = ((Number) jSpinnerSubWordLength.getValue()).intValue();
		if (subWordLength > wordLength) jSpinnerSubWordLength.setValue(wordLength);
		
		
		this.calcAndSetImageSize();
		this.addAllChangeListeners();
		this.updateParameterBlock();
	}
	


}
