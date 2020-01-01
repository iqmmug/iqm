package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Convert.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

import javax.media.jai.PlanarImage;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpConvertDescriptor;

/**
 * @author Ahammer, Kainz
 * @since 2009 03
 * @update 2014 05 HA 8bit to RGB false color options
 */
public class OperatorGUI_Convert extends AbstractImageOperatorGUI implements
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5007316734431004440L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_Convert.class);

	private ParameterBlockIQM pb = null;

	private JRadioButton butt8toRGB;
	private JRadioButton butt8to16;
	private JRadioButton butt8toFC;      //8bit to false color
	private JRadioButton butt8toFCRed;   //8bit to false color red
	private JRadioButton butt8toFCGreen; //8bit to false color green
	private JRadioButton butt8toFCBlue;  //8bit to false color blue
	

	private JRadioButton butt16to8;

	private JRadioButton buttPaletteToRGB;
	private JRadioButton buttPaletteToGreyNTSC;
	private JRadioButton buttPaletteToGreyMean;
	
	private JRadioButton buttRGBA2RGBnoAlphaPre;

	private JRadioButton buttNTSC;
	private JRadioButton buttMeanRGB;
	private JRadioButton buttChR;
	private JRadioButton buttChG;
	private JRadioButton buttChB;
	private JRadioButton buttRGB2HSV;
	private JRadioButton buttRGB2HLS;
	private JRadioButton buttRGB2CIELAB;
	private JRadioButton buttRGB2CIELUV;
	private JRadioButton buttRGB2CIEXYZ;
	private JRadioButton buttHSV2RGB;
	private JRadioButton buttHLS2RGB;
	private JRadioButton buttCIELAB2RGB;
	private JRadioButton buttCIELUV2RGB;
	private JRadioButton buttCIEXYZ2RGB;

	private JPanel pnl8bit;
	private JPanel pnl16bit;
	private JPanel pnlPalette;
	private JPanel pnlRGB;
	private JPanel pnlRGBa;
	
	private TitledBorder tb8bit;
	private TitledBorder tb16bit;
	private TitledBorder tbPalette;
	private TitledBorder tbRGBa;
	private TitledBorder tbRGB;

	private ButtonGroup buttGroup8bit = null;
	private ButtonGroup buttGroup16bit = null;
	private ButtonGroup buttGroupRGB = null;
	private ButtonGroup buttGroupPalette = null;

	private String imgType = "";
	
	

	/**
	 * constructor
	 */
	public OperatorGUI_Convert() {
		getOpGUIContent().setBorder(new EmptyBorder(10, 10, 10, 10));
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpConvertDescriptor().getName());
		this.initialize();
		this.setTitle("Convert Image");
    	getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(createPanel8bit(),    getGridBagConstraints_8bit());		
		this.getOpGUIContent().add(createPanel16bit(),	 getGridBagConstraints_16bit());		
		this.getOpGUIContent().add(createPanelPalette(), getGridBagConstraints_Palette());
		this.getOpGUIContent().add(createPanelRGBa(),    getGridBagConstraints_RGBa());
		this.getOpGUIContent().add(createPanelRGB(),     getGridBagConstraints_RGB());

		this.pack();
	}
	
	private GridBagConstraints getGridBagConstraints_8bit() {
		GridBagConstraints gbc_8bit = new GridBagConstraints();
		gbc_8bit.gridx = 0;
		gbc_8bit.gridy = 0;
		gbc_8bit.insets = new Insets(10, 0, 0, 0); // top left  bottom  right
		gbc_8bit.fill = GridBagConstraints.BOTH;
		return gbc_8bit;
	}
	private GridBagConstraints getGridBagConstraints_16bit() {
		GridBagConstraints gbc_16bit = new GridBagConstraints();
		gbc_16bit.gridx = 0;
		gbc_16bit.gridy = 1;
		gbc_16bit.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		gbc_16bit.fill = GridBagConstraints.HORIZONTAL;
		return gbc_16bit;
	}
	private GridBagConstraints getGridBagConstraints_Palette() {
		GridBagConstraints gbc_Palette = new GridBagConstraints();
		gbc_Palette.gridx = 0;
		gbc_Palette.gridy = 2;
		gbc_Palette.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		gbc_Palette.fill = GridBagConstraints.HORIZONTAL;
		return gbc_Palette;
	}
	private GridBagConstraints getGridBagConstraints_RGBa() {
		GridBagConstraints gbc_RGBa = new GridBagConstraints();
		gbc_RGBa.gridx = 0;
		gbc_RGBa.gridy = 3;
		gbc_RGBa.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		gbc_RGBa.fill = GridBagConstraints.HORIZONTAL;
		return gbc_RGBa;
	}
	private GridBagConstraints getGridBagConstraints_RGB() {
		GridBagConstraints gbc_RGB = new GridBagConstraints();
		gbc_RGB.gridx = 0;
		gbc_RGB.gridy = 4;
		gbc_RGB.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		gbc_RGB.fill = GridBagConstraints.HORIZONTAL;
		return gbc_RGB;
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		if (butt8toRGB.isSelected())     pb.setParameter("Options8bit", 0);
		if (butt8to16.isSelected())      pb.setParameter("Options8bit", 1);
		if (butt8toFC.isSelected())      pb.setParameter("Options8bit", 2);
		if (butt8toFCRed.isSelected())   pb.setParameter("Options8bit", 3);
		if (butt8toFCGreen.isSelected()) pb.setParameter("Options8bit", 4);
		if (butt8toFCBlue.isSelected())  pb.setParameter("Options8bit", 5);
		
		if (butt16to8.isSelected())      pb.setParameter("Options16bit", 0);
		
		if (buttPaletteToRGB.isSelected())      pb.setParameter("OptionsPalette", 0);
		if (buttPaletteToGreyNTSC.isSelected()) pb.setParameter("OptionsPalette", 1);
		if (buttPaletteToGreyMean.isSelected()) pb.setParameter("OptionsPalette", 2);
		
		if (buttNTSC.isSelected())   pb.setParameter("OptionsRGB", 0);
		if (buttMeanRGB.isSelected())pb.setParameter("OptionsRGB", 1);
		if (buttChR.isSelected())    pb.setParameter("OptionsRGB", 2);
		if (buttChG.isSelected())    pb.setParameter("OptionsRGB", 3);
		if (buttChB.isSelected())    pb.setParameter("OptionsRGB", 4);
		if (buttRGB2HSV.isSelected())pb.setParameter("OptionsRGB", 5);
		if (buttRGB2HLS.isSelected())pb.setParameter("OptionsRGB", 6);
		
		if (buttRGB2CIELAB.isSelected()) pb.setParameter("OptionsRGB", 7);
		if (buttRGB2CIELUV.isSelected()) pb.setParameter("OptionsRGB", 8);
		if (buttRGB2CIEXYZ.isSelected()) pb.setParameter("OptionsRGB", 9);
		if (buttHSV2RGB.isSelected())    pb.setParameter("OptionsRGB", 10);
		if (buttHLS2RGB.isSelected())    pb.setParameter("OptionsRGB", 11);
		if (buttCIELAB2RGB.isSelected()) pb.setParameter("OptionsRGB", 12);
		if (buttCIELUV2RGB.isSelected()) pb.setParameter("OptionsRGB", 13);
		if (buttCIEXYZ2RGB.isSelected()) pb.setParameter("OptionsRGB", 14);
		
		if (buttRGBA2RGBnoAlphaPre.isSelected()) pb.setParameter("OptionsRGB", 15);

	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		// System.out.println(imgType==""?"INITIAL":imgType);
		if (imgType == "")
			return;

		if (imgType.equals(IQMConstants.IMAGE_TYPE_8_BIT)) {
			if (pb.getIntParameter("Options8bit") == 0) butt8toRGB.setSelected(true);
			if (pb.getIntParameter("Options8bit") == 1) butt8to16.setSelected(true);
			if (pb.getIntParameter("Options8bit") == 2) butt8toFC.setSelected(true);
			if (pb.getIntParameter("Options8bit") == 3) butt8toFCRed.setSelected(true);
			if (pb.getIntParameter("Options8bit") == 4) butt8toFCGreen.setSelected(true);
			if (pb.getIntParameter("Options8bit") == 2) butt8toFCBlue.setSelected(true);	
		}
		if (imgType.equals(IQMConstants.IMAGE_TYPE_16_BIT)) {
			if (pb.getIntParameter("Options16bit") == 0) butt16to8.setSelected(true);	
		}
		if (imgType.equals(IQMConstants.IMAGE_TYPE_INDEXED)) {
			if (pb.getIntParameter("OptionsPalette") == 0) buttPaletteToRGB.setSelected(true);
			if (pb.getIntParameter("OptionsPalette") == 1) buttPaletteToGreyNTSC.setSelected(true);
			if (pb.getIntParameter("OptionsPalette") == 2) buttPaletteToGreyMean.setSelected(true);
		}
		if (imgType.equals(IQMConstants.IMAGE_TYPE_RGB)) {
			if (pb.getIntParameter("OptionsRGB") == 0) buttNTSC.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 1) buttMeanRGB.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 2) buttChR.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 3) buttChG.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 4) buttChB.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 5) buttRGB2HSV.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 6) buttRGB2HLS.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 7) buttRGB2CIELAB.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 8) buttRGB2CIELUV.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 9) buttRGB2CIEXYZ.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 10) buttHSV2RGB.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 11) buttHLS2RGB.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 12) buttCIELAB2RGB.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 13) buttCIELUV2RGB.setSelected(true);
			if (pb.getIntParameter("OptionsRGB") == 14) buttCIEXYZ2RGB.setSelected(true);
		} 
		if (imgType.equals(IQMConstants.IMAGE_TYPE_RGBA)){
			if (pb.getIntParameter("OptionsRGBa") == 0) buttRGBA2RGBnoAlphaPre.setSelected(true);
		}
	}

	/**
	 * This method updates the GUI if needed This method overrides OperatorGUI
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");

		IqmDataBox iqmDataBox = (IqmDataBox) this.workPackage.getSources().get(0);

		PlanarImage pi = iqmDataBox.getImage();
		imgType = ImageTools.getImgType(pi);

		// disable all buttons and panels per default
		this.disableAndDeselectAllItems();

		if (imgType.equals(IQMConstants.IMAGE_TYPE_8_BIT)) {
			butt8toRGB.setEnabled(true);
			butt8to16.setEnabled(true);
			butt8toFC.setEnabled(true);
			butt8toFCRed.setEnabled(true);
			butt8toFCGreen.setEnabled(true);
			butt8toFCBlue.setEnabled(true);
			tb8bit.setTitleColor(Color.BLACK);
		}
		if (imgType.equals(IQMConstants.IMAGE_TYPE_16_BIT)) {
			butt16to8.setEnabled(true);
			tb16bit.setTitleColor(Color.BLACK);
		}
		if (imgType.equals(IQMConstants.IMAGE_TYPE_INDEXED)) {
			buttPaletteToRGB.setEnabled(true);
			buttPaletteToGreyNTSC.setEnabled(true);
			buttPaletteToGreyMean.setEnabled(true);
			tbPalette.setTitleColor(Color.BLACK);
		}
		if (imgType.equals(IQMConstants.IMAGE_TYPE_RGB)) {
			buttNTSC.setEnabled(true);
			buttMeanRGB.setEnabled(true);
			buttChR.setEnabled(true);
			buttChG.setEnabled(true);
			buttChB.setEnabled(true);
			buttRGB2HSV.setEnabled(true);
			buttRGB2HLS.setEnabled(true);
			buttRGB2CIELAB.setEnabled(true);
			buttRGB2CIELUV.setEnabled(true);
			buttRGB2CIEXYZ.setEnabled(true);
			buttHSV2RGB.setEnabled(true);
			buttHLS2RGB.setEnabled(true);
			buttCIELAB2RGB.setEnabled(true);
			buttCIELUV2RGB.setEnabled(true);
			buttCIEXYZ2RGB.setEnabled(true);
			tbRGB.setTitleColor(Color.BLACK);
		}
		if (imgType.equals(IQMConstants.IMAGE_TYPE_RGBA)){
			buttRGBA2RGBnoAlphaPre.setEnabled(true);
			tbRGBa.setTitleColor(Color.BLACK);
		}

		this.repaint(); //because of TitledBorder's color
		try {
			this.setParameterValuesToGUI();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void disableAndDeselectAllItems() {
		for (AbstractButton btn : Collections.list(buttGroup8bit.getElements())){
			btn.setEnabled(false);
		}
	
		for (AbstractButton btn : Collections.list(buttGroup16bit.getElements())){
			btn.setEnabled(false);
		}
		
		for (AbstractButton btn : Collections.list(buttGroupPalette.getElements())){
			btn.setEnabled(false);
		}
		
		for (AbstractButton btn : Collections.list(buttGroupRGB.getElements())){
			btn.setEnabled(false);
		}
		
		buttRGBA2RGBnoAlphaPre.setEnabled(false);

		
		buttGroupRGB.clearSelection();
		buttGroupPalette.clearSelection();
		buttGroup8bit.clearSelection();
		buttGroup16bit.clearSelection();
		
		buttRGBA2RGBnoAlphaPre.setSelected(false);
		
		
		tb8bit.setTitleColor(Color.GRAY);
		tb16bit.setTitleColor(Color.GRAY);
		tbPalette.setTitleColor(Color.GRAY);
		tbRGBa.setTitleColor(Color.GRAY);
		tbRGB.setTitleColor(Color.GRAY);	
		
	}

	/**
	 * This method initializes the Option: 8bit to RGB
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButt8toRGB() {
		butt8toRGB = new JRadioButton();
		butt8toRGB.addActionListener(this);
		butt8toRGB.setText("8bit to RGB");
		butt8toRGB.setToolTipText("converts an 8bit image to a RGB 24bit color image");
		butt8toRGB.setActionCommand("parameter");
		return butt8toRGB;
	}

	/**
	 * This method initializes the Option: 8bit to 16bit
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButt8to16() {
		butt8to16 = new JRadioButton();
		butt8to16.addActionListener(this);
		butt8to16.setText("8bit to 16bit");
		butt8to16.setToolTipText("converts an 8bit image to 16bit");
		butt8to16.setActionCommand("parameter");
		return butt8to16;
	}
	/**
	 * This method initializes the Option: 8bit to false color
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButt8toFC() {
		butt8toFC = new JRadioButton();
		butt8toFC.addActionListener(this);
		butt8toFC.setText("8bit to false color");
		butt8toFC.setToolTipText("converts an 8bit image to RGB false color");
		butt8toFC.setActionCommand("parameter");
		return butt8toFC;
	}
	/**
	 * This method initializes the Option: 8bit to false color red
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButt8toFCRed	() {
		butt8toFCRed	 = new JRadioButton();
		butt8toFCRed	.addActionListener(this);
		butt8toFCRed	.setText("8bit to red false color");
		butt8toFCRed	.setToolTipText("converts an 8bit image to RGB red false color");
		butt8toFCRed	.setActionCommand("parameter");
		return butt8toFCRed	;
	}
	/**
	 * This method initializes the Option: 8bit to false color green
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButt8toFCGreen	() {
		butt8toFCGreen	 = new JRadioButton();
		butt8toFCGreen	.addActionListener(this);
		butt8toFCGreen	.setText("8bit to green false color");
		butt8toFCGreen	.setToolTipText("converts an 8bit image to RGB green false color");
		butt8toFCGreen	.setActionCommand("parameter");
		return butt8toFCGreen	;
	}
	/**
	 * This method initializes the Option: 8bit to false color blue
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButt8toFCBlue	() {
		butt8toFCBlue	 = new JRadioButton();
		butt8toFCBlue	.addActionListener(this);
		butt8toFCBlue	.setText("8bit to blue false color");
		butt8toFCBlue	.setToolTipText("converts an 8bit image to RGB blue false color");
		butt8toFCBlue	.setActionCommand("parameter");
		return butt8toFCBlue	;
	}

	/**
	 * This method initializes the Option: 16bit to 8bit
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButt16to8() {
		butt16to8 = new JRadioButton();
		butt16to8.addActionListener(this);
		butt16to8.setText("16bit to 8bit");
		butt16to8.setToolTipText("converts a 16bit image to 8bit");
		butt16to8.setActionCommand("parameter");
		return butt16to8;
	}

	/**
	 * This method initializes the Option: Palette to RGB
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtPaletteToRGB() {
		buttPaletteToRGB = new JRadioButton();
		buttPaletteToRGB.addActionListener(this);
		buttPaletteToRGB.setText("Palette to RGB");
		buttPaletteToRGB.setToolTipText("converts a Palette (indexed) image to RGB");
		buttPaletteToRGB.setActionCommand("parameter");
		return buttPaletteToRGB;
	}

	/**
	 * This method initializes the Option: Palette to Grey NTSC
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtPaletteToGreyNTSC() {
		buttPaletteToGreyNTSC = new JRadioButton();
		buttPaletteToGreyNTSC.addActionListener(this);
		buttPaletteToGreyNTSC.setText("Palette to Grey(NTSC)");
		buttPaletteToGreyNTSC.setToolTipText("converts a Palette (indexed) image to Grey using RGB NTSC weights");
		buttPaletteToGreyNTSC.setActionCommand("parameter");
		return buttPaletteToGreyNTSC;
	}

	/**
	 * This method initializes the Option: Palette to Grey Mean
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtPaletteToGreyMean() {
		buttPaletteToGreyMean = new JRadioButton();
		buttPaletteToGreyMean.addActionListener(this);
		buttPaletteToGreyMean.setText("Palette to Grey(Mean)");
		buttPaletteToGreyMean.setToolTipText("converts a Palette (indexed) image to Grey using the RGB mean");
		buttPaletteToGreyMean.setActionCommand("parameter");
		return buttPaletteToGreyMean;
	}

	/**
	 * This method initializes the Option: NTSC
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtNTSC() {
		buttNTSC = new JRadioButton();
		buttNTSC.addActionListener(this);
		buttNTSC.setText("NTSC Luminance: 29.9%R  58.7%G  11.4%B");
		buttNTSC.setToolTipText("calculates the NTSC luminance");
		buttNTSC.setActionCommand("parameter");
		return buttNTSC;
	}

	/**
	 * This method initializes the Option: Mean of RGB
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtMeanRGB() {
		buttMeanRGB = new JRadioButton();
		buttMeanRGB.addActionListener(this);
		buttMeanRGB.setText("RGB Mean");
		buttMeanRGB.setToolTipText("calculates the mean of RGB channels");
		buttMeanRGB.setActionCommand("parameter");
		return buttMeanRGB;
	}

	/**
	 * This method initializes the Option: Extract red channel
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtChR() {
		buttChR = new JRadioButton();
		buttChR.addActionListener(this);
		buttChR.setText("extract R");
		buttChR.setToolTipText("extracts the red channel");
		buttChR.setActionCommand("parameter");
		return buttChR;
	}

	/**
	 * This method initializes the Option: Extract the green channel
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtChG() {
		buttChG = new JRadioButton();
		buttChG.setText("extract G");
		buttChG.addActionListener(this);
		buttChG.setToolTipText("extracts the green channel");
		buttChG.setActionCommand("parameter");
		return buttChG;
	}

	/**
	 * This method initializes the Option: Extract the blue channel
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtChB() {
		buttChB = new JRadioButton();
		buttChB.setText("extract B");
		buttChB.addActionListener(this);
		buttChB.setToolTipText("extracts the blue channel");
		buttChB.setActionCommand("parameter");
		return buttChB;
	}

	/**
	 * This method initializes the Option: RGB2HSV conversion
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtRGB2HSV() {
		buttRGB2HSV = new JRadioButton();
		buttRGB2HSV.setText("RGB to HSV");
		buttRGB2HSV.addActionListener(this);
		buttRGB2HSV.setToolTipText("converts to the HSV color space");
		buttRGB2HSV.setActionCommand("parameter");
		return buttRGB2HSV;
	}

	/**
	 * This method initializes the Option: RGB2HLS conversion
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtRGB2HLS() {
		buttRGB2HLS = new JRadioButton();
		buttRGB2HLS.setText("RGB to HLS");
		buttRGB2HLS.addActionListener(this);
		buttRGB2HLS.setToolTipText("converts to the HLS color space");
		buttRGB2HLS.setActionCommand("parameter");
		return buttRGB2HLS;
	}

	/**
	 * This method initializes the Option: RGB2CIELAB conversion
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtRGB2CIELAB() {
		buttRGB2CIELAB = new JRadioButton();
		buttRGB2CIELAB.setText("RGB to CIELAB");
		buttRGB2CIELAB.addActionListener(this);
		buttRGB2CIELAB.setToolTipText("converts to the CIELAB color space");
		buttRGB2CIELAB.setActionCommand("parameter");
		return buttRGB2CIELAB;
	}

	/**
	 * This method initializes the Option: RGB2CIELUV conversion
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtRGB2CIELUV() {
		buttRGB2CIELUV = new JRadioButton();
		buttRGB2CIELUV.setText("RGB to CIELUV");
		buttRGB2CIELUV.addActionListener(this);
		buttRGB2CIELUV.setToolTipText("converts to the CIELUV color space");
		buttRGB2CIELUV.setActionCommand("parameter");
		return buttRGB2CIELUV;
	}

	/**
	 * This method initializes the Option: RGB2CIEXYZ conversion
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtRGB2CIEXYZ() {
		buttRGB2CIEXYZ = new JRadioButton();
		buttRGB2CIEXYZ.setText("RGB to CIEXYZ");
		buttRGB2CIEXYZ.addActionListener(this);
		buttRGB2CIEXYZ.setToolTipText("converts to the CIEXYZ color space");
		buttRGB2CIEXYZ.setActionCommand("parameter");
		return buttRGB2CIEXYZ;
	}

	/**
	 * This method initializes the Option: HSV2RGB conversion
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createJRadioButtonMenuButtHSV2RGB() {
		buttHSV2RGB = new JRadioButton();
		buttHSV2RGB.setText("HSV to RGB");
		buttHSV2RGB.addActionListener(this);
		buttHSV2RGB.setToolTipText("converts the HSV color space to RGB");
		buttHSV2RGB.setActionCommand("parameter");
		return buttHSV2RGB;
	}

	/**
	 * This method initializes the Option: HLS2RGB conversion
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtHLS2RGB() {
		buttHLS2RGB = new JRadioButton();
		buttHLS2RGB.setText("HLS to RGB");
		buttHLS2RGB.addActionListener(this);
		buttHLS2RGB.setToolTipText("converts the HLS color space to RGB");
		buttHLS2RGB.setActionCommand("parameter");
		return buttHLS2RGB;
	}

	/**
	 * This method initializes the Option: CIELAB2RGB conversion
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtCIELAB2RGB() {
		buttCIELAB2RGB = new JRadioButton();
		buttCIELAB2RGB.setText("CIELAB to RGB");
		buttCIELAB2RGB.addActionListener(this);
		buttCIELAB2RGB.setToolTipText("converts the CIELAB color space to RGB");
		buttCIELAB2RGB.setActionCommand("parameter");
		return buttCIELAB2RGB;
	}

	/**
	 * This method initializes the Option: CIELUV2RGB conversion
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtCIELUV2RGB() {
		buttCIELUV2RGB = new JRadioButton();
		buttCIELUV2RGB.setText("CIELUV to RGB");
		buttCIELUV2RGB.addActionListener(this);
		buttCIELUV2RGB.setToolTipText("converts the CIELUV color space to RGB");
		buttCIELUV2RGB.setActionCommand("parameter");
		return buttCIELUV2RGB;
	}

	/**
	 * This method initializes the Option: CIEXYZ2RGB conversion
	 * 
	 * @return {@link javax.swing.JRadioButton}
	 */
	private JRadioButton createButtCIEXYZ2RGB() {
		buttCIEXYZ2RGB = new JRadioButton();
		buttCIEXYZ2RGB.setText("CIEXYZ to RGB");
		buttCIEXYZ2RGB.addActionListener(this);
		buttCIEXYZ2RGB.setToolTipText("converts the CIEXYZ color space to RGB");
		buttCIEXYZ2RGB.setActionCommand("parameter");
		return buttCIEXYZ2RGB;
	}

	/**
	 * This method initializes pnl8bit
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel createPanel8bit() {
		pnl8bit = new JPanel();
		pnl8bit.setLayout(new BoxLayout(pnl8bit, BoxLayout.Y_AXIS));
		//pnl8bit.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		tb8bit = new TitledBorder(null, "8-bit", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		pnl8bit.setBorder(tb8bit);	
		pnl8bit.add(createButt8toRGB());
		pnl8bit.add(createButt8to16());
		pnl8bit.add(createButt8toFC());
		pnl8bit.add(createButt8toFCRed());
		pnl8bit.add(createButt8toFCGreen());
		pnl8bit.add(createButt8toFCBlue());

		this.setButtonGroup8bit(); // Grouping of JRadioButtons
		return pnl8bit;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel createPanel16bit() {
		pnl16bit = new JPanel();
		pnl16bit.setLayout(new BoxLayout(pnl16bit, BoxLayout.Y_AXIS));
		//pnl16bit.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		tb16bit = new TitledBorder(null, "16-bit", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		pnl16bit.setBorder(tb16bit);	
		pnl16bit.add(createButt16to8());
		this.setButtonGroup16bit(); // Grouping of JRadioButtons
		return pnl16bit;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel createPanelPalette() {
		pnlPalette = new JPanel();
		pnlPalette.setLayout(new BoxLayout(pnlPalette, BoxLayout.Y_AXIS));
		//pnlPalette.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		tbPalette = new TitledBorder(null, "Palette", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		pnlPalette.setBorder(tbPalette);	
		pnlPalette.add(createButtPaletteToRGB());
		pnlPalette.add(createButtPaletteToGreyNTSC());
		pnlPalette.add(createButtPaletteToGreyMean());
		this.setButtonGroupPalettte(); // Grouping of JRadioButtons
		return pnlPalette;
	}
	
	/**
	 * This method initializes jPanel
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel createPanelRGBa() {
		pnlRGBa = new JPanel();
		pnlRGBa.setLayout(new BoxLayout(pnlRGBa, BoxLayout.Y_AXIS));
		//pnlRGBa.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		tbRGBa = new TitledBorder(null, "RGBa", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		pnlRGBa.setBorder(tbRGBa);	
		pnlRGBa.add(createButtRGBA2RGBnoAlphaPre());
		return pnlRGBa;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel createPanelRGB() {
		pnlRGB = new JPanel();
		pnlRGB.setLayout(new BoxLayout(pnlRGB, BoxLayout.Y_AXIS));
		//pnlRGB.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		tbRGB = new TitledBorder(null, "RGB", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		pnlRGB.setBorder(tbRGB);	
		pnlRGB.add(createButtNTSC());
		pnlRGB.add(createButtMeanRGB());
		pnlRGB.add(createButtChR());
		pnlRGB.add(createButtChG());
		pnlRGB.add(createButtChB());
		pnlRGB.add(createButtRGB2HSV());
		pnlRGB.add(createButtRGB2HLS());
		pnlRGB.add(createButtRGB2CIELAB());
		pnlRGB.add(createButtRGB2CIELUV());
		pnlRGB.add(createButtRGB2CIEXYZ());
		pnlRGB.add(createJRadioButtonMenuButtHSV2RGB());
		pnlRGB.add(createButtHLS2RGB());
		pnlRGB.add(createButtCIELAB2RGB());
		pnlRGB.add(createButtCIELUV2RGB());
		pnlRGB.add(createButtCIEXYZ2RGB());

		this.setButtonGroupRGB(); // Grouping of JRadioButtons
		return pnlRGB;
	}

	private JRadioButton createButtRGBA2RGBnoAlphaPre() {
		buttRGBA2RGBnoAlphaPre = new JRadioButton();
		buttRGBA2RGBnoAlphaPre.addActionListener(this);
		buttRGBA2RGBnoAlphaPre.setToolTipText("converts RGBa images to the RGB color space (alpha channel is not premultiplied)");
		buttRGBA2RGBnoAlphaPre.setText("RGBa to RGB (no premultiplied alpha)");
		buttRGBA2RGBnoAlphaPre.setActionCommand("parameter");
		return buttRGBA2RGBnoAlphaPre;
	}

	private void setButtonGroup8bit() {
		buttGroup8bit = new ButtonGroup();
		buttGroup8bit.add(butt8toRGB);
		buttGroup8bit.add(butt8to16);
		buttGroup8bit.add(butt8toFC);
		buttGroup8bit.add(butt8toFCRed);
		buttGroup8bit.add(butt8toFCGreen);
		buttGroup8bit.add(butt8toFCBlue);
	}

	private void setButtonGroup16bit() {
		buttGroup16bit = new ButtonGroup();
		buttGroup16bit.add(butt16to8);
	}

	private void setButtonGroupPalettte() {
		buttGroupPalette = new ButtonGroup();
		buttGroupPalette.add(buttPaletteToRGB);
		buttGroupPalette.add(buttPaletteToGreyNTSC);
		buttGroupPalette.add(buttPaletteToGreyMean);
	}

	private void setButtonGroupRGB() {
		buttGroupRGB = new ButtonGroup();
		buttGroupRGB.add(buttMeanRGB);
		buttGroupRGB.add(buttNTSC);
		buttGroupRGB.add(buttChR);
		buttGroupRGB.add(buttChG);
		buttGroupRGB.add(buttChB);
		buttGroupRGB.add(buttRGB2HSV);
		buttGroupRGB.add(buttRGB2HLS);
		buttGroupRGB.add(buttRGB2CIELAB);
		buttGroupRGB.add(buttRGB2CIELUV);
		buttGroupRGB.add(buttRGB2CIEXYZ);
		buttGroupRGB.add(buttHSV2RGB);
		buttGroupRGB.add(buttHLS2RGB);
		buttGroupRGB.add(buttCIELAB2RGB);
		buttGroupRGB.add(buttCIELUV2RGB);
		buttGroupRGB.add(buttCIEXYZ2RGB);
	}

	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("parameter".equals(e.getActionCommand())) {
			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
}// END
