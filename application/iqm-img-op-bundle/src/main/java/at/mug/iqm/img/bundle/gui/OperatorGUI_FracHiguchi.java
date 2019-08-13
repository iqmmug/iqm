package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracHiguchi.java
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


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracHiguchiDescriptor;

/**
 * @author Ahammer, Kainz
 * @since  2010 04
 * @update 2014 12 changed GUI layout
 */
public class OperatorGUI_FracHiguchi extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3660245429029584915L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_FracHiguchi.class);

	private ParameterBlockIQM pb = null;

	private int width;
	private int height;

	private int numMaxK = 0;
	private int numK = 0;

	private JPanel   jPanelNumK   = null;
	private JLabel   jLabelNumK   = null;
	private JSpinner jSpinnerNumK = null;

	private JRadioButton buttProj        = null;
	private JRadioButton buttSep         = null;
	private JRadioButton buttMeand       = null;
	private JRadioButton buttCont        = null;
	private JRadioButton buttRadial      = null;
	private JRadioButton buttSpiral      = null;
	private JRadioButton buttLineROI     = null;
	private JPanel       jPanelAppend    = null;
	private ButtonGroup  buttGroupAppend = null;

	private JRadioButton buttIncl      = null;
	private JRadioButton buttExcl      = null;
	private JPanel       jPanelBack    = null;
	private ButtonGroup  buttGroupBack = null;

	private JCheckBox jCheckBoxDx   = null;
	private JCheckBox jCheckBoxDy   = null;
	private JPanel    jPanelDxDy    = null;
	
	private JPanel    jPanelOptions = null;

	private JPanel   jPanelRegression = null;;
	private JPanel   jPanelRegStart   = null;
	private JLabel   jLabelRegStart   = null;
	private JSpinner jSpinnerRegStart = null;
	private JPanel   jPanelRegEnd     = null;
	private JLabel   jLabelRegEnd     = null;
	private JSpinner jSpinnerRegEnd   = null;

	private JCheckBox jCheckBoxShowPlot           = null;
	private JCheckBox jCheckBoxDeleteExistingPlot = null;
	private JPanel    jPanelPlotOptions           = null;



	/**
	 * constructor
	 */
	public OperatorGUI_FracHiguchi() {
		getOpGUIContent().setBorder(new EmptyBorder(10, 10, 10, 10));
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpFracHiguchiDescriptor().getName());	
		this.initialize();
		this.setTitle("Fractal Higuchi Dimension");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.higuchi.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());
		
		this.getOpGUIContent().add(getJPanelNumK(),       getGridBagConstraints_NumK());
		this.getOpGUIContent().add(getJPanelOptions(),    getGridBagConstraints_Options());
		this.getOpGUIContent().add(getJPanelRegression(), getGridBagConstraints_Regression());
		this.getOpGUIContent().add(getPlotOptionsPanel(), getGridBagConstraints_PlotOptions());

		this.pack();
	}
	private GridBagConstraints getGridBagConstraints_NumK(){
		GridBagConstraints gbc_NumK = new GridBagConstraints();
		gbc_NumK.gridx = 0;
		gbc_NumK.gridy = 0;
		gbc_NumK.insets = new Insets(5, 0, 0, 0);
		gbc_NumK.fill = GridBagConstraints.BOTH;
		return gbc_NumK;
	}
	
	private GridBagConstraints getGridBagConstraints_Options(){
		GridBagConstraints gbc_Options = new GridBagConstraints();
		gbc_Options.gridx = 0;
		gbc_Options.gridy = 1;
		gbc_Options.insets = new Insets(5, 0, 0, 0);
		gbc_Options.fill = GridBagConstraints.BOTH;
		return gbc_Options;
	}
	
	private GridBagConstraints getGridBagConstraints_Regression() {
		GridBagConstraints gbc_Regression = new GridBagConstraints();
		gbc_Regression.gridx = 0;
		gbc_Regression.gridy = 2;
		gbc_Regression.insets = new Insets(5, 0, 0, 0);
		gbc_Regression.fill = GridBagConstraints.BOTH;
		return gbc_Regression;
	}
	
	private GridBagConstraints getGridBagConstraints_PlotOptions(){
		GridBagConstraints gbc_PlotOptions = new GridBagConstraints();
		gbc_PlotOptions.fill = GridBagConstraints.BOTH;
		gbc_PlotOptions.gridx = 0;
		gbc_PlotOptions.gridy = 3;
		gbc_PlotOptions.insets = new Insets(5, 0, 5, 0);
		return gbc_PlotOptions;
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {

		pb.setParameter("NumK", ((Number) jSpinnerNumK.getValue()).intValue());

		if (buttProj.isSelected())   pb.setParameter("Append", 0);
		if (buttSep.isSelected())    pb.setParameter("Append", 1);
		if (buttMeand.isSelected())  pb.setParameter("Append", 2);
		if (buttCont.isSelected())   pb.setParameter("Append", 3);
		if (buttRadial.isSelected()) pb.setParameter("Append", 4);
		if (buttSpiral.isSelected()) pb.setParameter("Append", 5);
		if (buttLineROI.isSelected())pb.setParameter("Append", 6);

		if (buttIncl.isSelected())   pb.setParameter("Back", 0);
		if (buttExcl.isSelected())   pb.setParameter("Back", 1);

		if (jCheckBoxDx.isSelected()) pb.setParameter("Dx", 1);
		if (!jCheckBoxDx.isSelected())pb.setParameter("Dx", 0);
		if (jCheckBoxDy.isSelected()) pb.setParameter("Dy", 1);
		if (!jCheckBoxDy.isSelected())pb.setParameter("Dy", 0);

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

		jSpinnerNumK.removeChangeListener(this);
		jSpinnerNumK.setValue(pb.getIntParameter("NumK"));
		jSpinnerNumK.addChangeListener(this);

		if (pb.getIntParameter("Append") == 0) buttProj.setSelected(true);
		if (pb.getIntParameter("Append") == 1) buttSep.setSelected(true);
		if (pb.getIntParameter("Append") == 2) buttMeand.setSelected(true);
		if (pb.getIntParameter("Append") == 3) buttCont.setSelected(true);
		if (pb.getIntParameter("Append") == 4) buttRadial.setSelected(true);
		if (pb.getIntParameter("Append") == 5) buttSpiral.setSelected(true);
		if (pb.getIntParameter("Append") == 6) buttLineROI.setSelected(true);

		if (pb.getIntParameter("Back") == 0) buttIncl.setSelected(true);
		if (pb.getIntParameter("Back") == 1) buttExcl.setSelected(true);

		if (pb.getIntParameter("Dx") == 0) jCheckBoxDx.setSelected(false);
		if (pb.getIntParameter("Dx") == 1) jCheckBoxDx.setSelected(true);
		if (pb.getIntParameter("Dy") == 0) jCheckBoxDy.setSelected(false);
		if (pb.getIntParameter("Dy") == 1) jCheckBoxDy.setSelected(true);

		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegStart.setValue(pb.getIntParameter("RegStart"));
		jSpinnerRegStart.addChangeListener(this);

		jSpinnerRegEnd.removeChangeListener(this);
		jSpinnerRegEnd.setValue(pb.getIntParameter("RegEnd"));
		jSpinnerRegEnd.addChangeListener(this);

		if (pb.getIntParameter("ShowPlot") == 0)  jCheckBoxShowPlot.setSelected(false);
		if (pb.getIntParameter("ShowPlot") == 1)  jCheckBoxShowPlot.setSelected(true);
		if (pb.getIntParameter("DeleteExistingPlot") == 0)  jCheckBoxDeleteExistingPlot.setSelected(false);
		if (pb.getIntParameter("DeleteExistingPlot") == 1)  jCheckBoxDeleteExistingPlot.setSelected(true);

	}

	/**
	 * This method gets the maximal k
	 * 
	 */
	private int getMaxHiguchiNumber(int width, int height) {

		int kMax = 1; // inclusive original image
		if (buttProj.isSelected() || buttSep.isSelected()
				|| buttRadial.isSelected() || buttLineROI.isSelected()) {
			if (width <= height) {
				kMax = width / 2;
			} else {
				kMax = height / 2;
			}
		}
		if (buttCont.isSelected() || buttMeand.isSelected()
				|| buttSpiral.isSelected()) {
			// kMax = (imgWidth * imgHeight)/3;
			if (width <= height) {
				kMax = width / 2;
			} else {
				kMax = height / 2;
			}
		}
		return kMax;
	}

	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method updates the GUI if needed This method overrides OperatorGUI
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");

		PlanarImage pi = ((IqmDataBox) this.pb.getSources().firstElement()).getImage();

		width  = pi.getWidth();
		height = pi.getHeight();

		numMaxK = this.getMaxHiguchiNumber(width, height);
		numK = numMaxK / 4 * 1;
		if (numK > numMaxK)
			numK = numMaxK;

		// System.out.println("OperatorGUI_FracHiguchi: numberMax: " +
		// numberMax);
		SpinnerModel sModel = new SpinnerNumberModel(numK, 3, numMaxK, 1); // init, min, max, step
		jSpinnerNumK.removeChangeListener(this);
		jSpinnerNumK.setModel(sModel);
		jSpinnerNumK.setValue(numK);
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumK.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setColumns(5);
		ftf.setEditable(true);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
		jSpinnerNumK.addChangeListener(this);
		
		jSpinnerRegStart.removeChangeListener(this);
		// jSpinnerRegStart.setValue(numK);
		SpinnerModel sModelRegStart = new SpinnerNumberModel(1, 1, numMaxK - 1, 1); // init, min, max, step
		jSpinnerRegStart.setModel(sModelRegStart);
		jSpinnerRegStart.addChangeListener(this);

		jSpinnerRegEnd.removeChangeListener(this);
		// jSpinnerRegEnd.setValue(numK);
		SpinnerModel sModelRegEnd = new SpinnerNumberModel(numK, 3, numMaxK, 1); // init, min, max, step
		jSpinnerRegEnd.setModel(sModelRegEnd);
		jSpinnerRegEnd.addChangeListener(this);

		this.updateParameterBlock();
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelNumK
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumK() {
		if (jPanelNumK == null) {
			jPanelNumK = new JPanel();
			//jPanelNumK.setLayout(new BoxLayout(jPanelNumK, BoxLayout.X_AXIS));
			jPanelNumK.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelNumK.setBorder(new TitledBorder(null, "Number of data series",TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jLabelNumK = new JLabel("k: ");
			jLabelNumK.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumK = new JSpinner(sModel);
			// jSpinnerNumK = new JSpinner();
			jSpinnerNumK.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumK.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelNumK.add(jLabelNumK);
			jPanelNumK.add(jSpinnerNumK);
		}
		return jPanelNumK;
	}
	//---------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtIncl() {
		buttIncl = new JRadioButton();
		buttIncl.setText("Inclusive Background");
		buttIncl.setToolTipText("calculation is carried out including zero values");
		buttIncl.addActionListener(this);
		buttIncl.setActionCommand("parameter");
		return buttIncl;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMenuButtExcl() {
		buttExcl = new JRadioButton();
		buttExcl.setText("Exclusive Background");
		buttExcl.setToolTipText("calculation is carried out excluding zero values");
		buttExcl.addActionListener(this);
		buttExcl.setActionCommand("parameter");
		return buttExcl;
	}

	private void setButtonGroupBack() {
		buttGroupBack = new ButtonGroup();
		buttGroupBack.add(buttIncl);
		buttGroupBack.add(buttExcl);
	}
	
	/**
	 * This method initializes jJPanelBack
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBack() {
		jPanelBack = new JPanel();
		jPanelBack.setLayout(new BoxLayout(jPanelBack, BoxLayout.Y_AXIS));
		jPanelBack.add(getJRadioButtonMenuButtIncl());
		jPanelBack.add(getJRadioButtonMenuButtExcl());
		this.setButtonGroupBack(); // Grouping of JRadioButtons
		return jPanelBack;
	}

	/**
	 * This method initializes jCheckBoxDx
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxDx() {
		if (jCheckBoxDx == null) {
			jCheckBoxDx = new JCheckBox();
			jCheckBoxDx.setText("Dx");
			jCheckBoxDx.addActionListener(this);
			jCheckBoxDx.setActionCommand("parameter");
		}
		return jCheckBoxDx;
	}

	/**
	 * This method initializes jCheckBoxDy
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxDy() {
		if (jCheckBoxDy == null) {
			jCheckBoxDy = new JCheckBox();
			jCheckBoxDy.setText("Dy");
			jCheckBoxDy.addActionListener(this);
			jCheckBoxDy.setActionCommand("parameter");
		}
		return jCheckBoxDy;
	}	
	
	private JPanel getDxdyPanel() {
		if (jPanelDxDy == null) {
			jPanelDxDy = new JPanel();
			jPanelDxDy.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelDxDy.add(getJCheckBoxDx());
			jPanelDxDy.add(getJCheckBoxDy());
		}
		return jPanelDxDy;
	}

	private JPanel getJPanelOptions() {
		if (jPanelOptions == null) {
			jPanelOptions = new JPanel();
			jPanelOptions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Options",TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 0, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			jPanelOptions.setLayout(gbl_panel);

			buttProj = new JRadioButton();
			buttProj.setText("Rows and columns projected");
			buttProj.setToolTipText("rows and columns are projected to a single data series respectively");
			buttProj.addActionListener(this);
			buttProj.setActionCommand("parameter");

			buttSep = new JRadioButton();
			buttSep.setText("Rows and columns separately and averaged");
			buttSep.setToolTipText("every row and column is separately calculated");
			buttSep.addActionListener(this);
			buttSep.setActionCommand("parameter");

			buttMeand = new JRadioButton();
			buttMeand.setText("Single meander row");
			buttMeand.setToolTipText("Rows are meander like stitched together to a single data series");
			buttMeand.addActionListener(this);
			buttMeand.setActionCommand("parameter");

			buttCont = new JRadioButton();
			buttCont.setText("Single contiguous row");
			buttCont.setToolTipText("Rows are contiguously stitched together to a single data series");
			buttCont.addActionListener(this);
			buttCont.setActionCommand("parameter");

			buttRadial = new JRadioButton();
			buttRadial.setText("180 radial lines and averaged");
			buttRadial.setToolTipText("180 radial lines in 1 steps through the midpoint of the image");
			buttRadial.addActionListener(this);
			buttRadial.setActionCommand("parameter");

			buttSpiral = new JRadioButton();
			buttSpiral.setText("Single spiral line");
			buttSpiral.setToolTipText("Spiral through image beginnig in the corner of the image");
			buttSpiral.addActionListener(this);
			buttSpiral.setActionCommand("parameter");

			buttLineROI = new JRadioButton();
			buttLineROI.setText("Single line ROI");
			buttLineROI.setToolTipText("single line defined by current line ROI");
			buttLineROI.addActionListener(this);
			buttLineROI.setActionCommand("parameter");

			GridBagConstraints gbc_jPanelAppend = new GridBagConstraints();
			gbc_jPanelAppend.insets = new Insets(0, 0, 5, 0);
			gbc_jPanelAppend.gridx = 0;
			gbc_jPanelAppend.gridy = 0;
			jPanelAppend = new JPanel();
			jPanelAppend.setLayout(new BoxLayout(jPanelAppend, BoxLayout.Y_AXIS));
			jPanelOptions.add(jPanelAppend, gbc_jPanelAppend);
			
			jPanelAppend.add(buttProj);
			jPanelAppend.add(buttSep);
			jPanelAppend.add(buttMeand);
			jPanelAppend.add(buttCont);
			jPanelAppend.add(buttRadial);
			jPanelAppend.add(buttSpiral);
			jPanelAppend.add(buttLineROI);
		
			GridBagConstraints gbc_backToolbar = new GridBagConstraints();
			gbc_backToolbar.insets = new Insets(10, 0, 10, 0);
			gbc_backToolbar.fill = GridBagConstraints.HORIZONTAL;
			gbc_backToolbar.gridx = 0;
			gbc_backToolbar.gridy = 1;
			jPanelOptions.add(getJPanelBack(), gbc_backToolbar);
		
			GridBagConstraints gbc_dxdyPanel = new GridBagConstraints();
			gbc_dxdyPanel.fill = GridBagConstraints.BOTH;
			gbc_dxdyPanel.gridx = 0;
			gbc_dxdyPanel.gridy = 2;
			jPanelOptions.add(getDxdyPanel(), gbc_dxdyPanel);

			this.setButtonGroupAppend(); // Grouping of JRadioButtons

		}
		return jPanelOptions;
	}

	private void setButtonGroupAppend() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupAppend = new ButtonGroup();
		buttGroupAppend.add(buttProj);
		buttGroupAppend.add(buttSep);
		buttGroupAppend.add(buttMeand);
		buttGroupAppend.add(buttCont);
		buttGroupAppend.add(buttRadial);
		buttGroupAppend.add(buttSpiral);
		buttGroupAppend.add(buttLineROI);
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
			SpinnerModel sModel = new SpinnerNumberModel(1, 1, 3, 1); // init, min, max, step
			jSpinnerRegStart = new JSpinner(sModel);
			jSpinnerRegStart.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegStart.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
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
			SpinnerModel sModel = new SpinnerNumberModel(2, 2, 3, 1); // init, min, max, step
			jSpinnerRegEnd = new JSpinner(sModel);
			// jSpinnerRegEnd = new JSpinner();
			jSpinnerRegEnd.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerRegEnd.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			ftf.setEditable(false);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelRegEnd.add(jLabelRegEnd);
			jPanelRegEnd.add(jSpinnerRegEnd);
		}
		return jPanelRegEnd;
	}
	/**
	 * This method initializes jJPanelRegression
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRegression() {
		if (jPanelRegression == null) {
			jPanelRegression = new JPanel();
			jPanelRegression.setBorder(new TitledBorder(null, "Regression", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			jPanelRegression.setLayout(new BoxLayout(jPanelRegression, BoxLayout.X_AXIS));
			//jPanelRegression.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelRegression.add(getJPanelRegStart());
			jPanelRegression.add(getJPanelRegEnd());
		}
		return jPanelRegression;
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
	
	private JPanel getPlotOptionsPanel() {
		if (jPanelPlotOptions == null) {
			jPanelPlotOptions = new JPanel();
			jPanelPlotOptions.setBorder(new TitledBorder(null, "Plot output options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0,0, 0)));
			jPanelPlotOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelPlotOptions.add(getJCheckBoxShowPlot());
			jPanelPlotOptions.add(getJCheckBoxDeleteExistingPlot());
		}
		return jPanelPlotOptions;
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

		numK = ((Number) jSpinnerNumK.getValue()).intValue();
		int regStart = ((Number) jSpinnerRegStart.getValue()).intValue();
		int regEnd = ((Number) jSpinnerRegEnd.getValue()).intValue();

		jSpinnerNumK.removeChangeListener(this);
		jSpinnerRegStart.removeChangeListener(this);
		jSpinnerRegEnd.removeChangeListener(this);

		if (jSpinnerNumK == e.getSource()) {
			// if(regEnd > numK){
			jSpinnerRegEnd.setValue(numK);
			regEnd = numK;
			// }
			if (regStart >= (regEnd - 1)) {
				jSpinnerRegStart.setValue(regEnd - 2);
				regStart = regEnd - 2;
			}
		}
		if (jSpinnerRegEnd == e.getSource()) {
			if (regEnd > numK) {
				jSpinnerRegEnd.setValue(numK);
				regEnd = numK;
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

		jSpinnerNumK.addChangeListener(this);
		jSpinnerRegStart.addChangeListener(this);
		jSpinnerRegEnd.addChangeListener(this);

		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}


}// END
