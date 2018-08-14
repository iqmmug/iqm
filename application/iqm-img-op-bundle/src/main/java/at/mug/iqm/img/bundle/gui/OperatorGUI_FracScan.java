package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracScan.java
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
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
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
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.jaitools.KernelFactory;
import at.mug.iqm.commons.jaitools.KernelUtil;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracScanDescriptor;

/**
 * @author Ahammer
 * @since   2012 28
 */
public class OperatorGUI_FracScan extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * The UID for serialization
	 */
	private static final long serialVersionUID = 6791427245269266377L;

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(OperatorGUI_FracScan.class);

	private ParameterBlockIQM pb = null;
	private int kernelSize = 0;

	private JRadioButton buttBoxScan = null;
	private JRadioButton buttNexelScan = null;
	private JPanel jPanelScanType = null;
	private ButtonGroup buttGroupScanType = null;

	private JPanel jPanelBoxSize = null;
	private JLabel jLabelBoxSize = null;
	private JSpinner jSpinnerBoxSize = null;

	private ButtonGroup  buttGroupKernelShape = null;
	private JRadioButton buttRectangle = null;
	private JRadioButton buttCircle = null;
	private JPanel       jPanelKernelShape = null;
	private JLabel       jLabelKernelShape = null;

	private JPanel   jSpinnerPanelKernelSize = null;
	private JSpinner jSpinnerKernelSize;
	private JLabel   jLabelKernelSize;
	
	private TitledBorder tbKernel      = null;
	private JPanel       jPanelKernel = null;

	private JPanel   jPanelEps = null;
	private JLabel   jLabelEps = null;
	private JSpinner jSpinnerEps = null;

	private JPanel jPanelMethod = null;
	private ButtonGroup buttGroupMethod = null;
	private JRadioButton buttBoxCount = null;
	private JRadioButton buttPyramid = null;
	private JRadioButton buttMinkowski = null;
	private JRadioButton buttFFT = null;

	private JRadioButton buttClamp = null;
	private JRadioButton buttNormalize = null;
	private JRadioButton buttActual = null;
	private JRadioButton buttNormalize0to3 = null;
	private JPanel jPanelResultOptions = null;
	private ButtonGroup buttGroupResultOptions = null;

	/**
	 * constructor
	 */
	public OperatorGUI_FracScan() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpFracScanDescriptor().getName());

		this.initialize();

		this.setTitle("Fractal Scan");
		this.setIconImage(Toolkit.getDefaultToolkit().
				getImage(Resources.getImageURL("icon.gui.fractal.fracscan.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelScanType(),     getGridBagConstraintsScanType());
		this.getOpGUIContent().add(getJPanelBoxSize(),      getGridBagConstraintsBoxSize());
		this.getOpGUIContent().add(getJPanelKernel(),       getGridBagConstraintsKernel());
		this.getOpGUIContent().add(getJPanelEps(),          getGridBagConstraintsEps());
		this.getOpGUIContent().add(getJPanelMethod(),       getGridBagConstraintsMethod());
		this.getOpGUIContent().add(getJPanelResultOptions(),getGridBagConstraintsResultOptions());
		this.pack();
	}
	private GridBagConstraints getGridBagConstraintsScanType() {
		GridBagConstraints gridBagConstraintsScanType = new GridBagConstraints();
		gridBagConstraintsScanType.gridx = 0;
		gridBagConstraintsScanType.gridy = 0;
		gridBagConstraintsScanType.gridwidth = 1;//
		gridBagConstraintsScanType.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gridBagConstraintsScanType.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsScanType;
	}

	private GridBagConstraints getGridBagConstraintsBoxSize() {
		GridBagConstraints gridBagConstraintsBoxSize = new GridBagConstraints();
		gridBagConstraintsBoxSize.gridwidth = 3;
		gridBagConstraintsBoxSize.gridx = 0;
		gridBagConstraintsBoxSize.gridy = 1;
		gridBagConstraintsBoxSize.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gridBagConstraintsBoxSize.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsBoxSize;
	}

	private GridBagConstraints getGridBagConstraintsKernel() {
		GridBagConstraints gridBagConstraintsKernel = new GridBagConstraints();
		gridBagConstraintsKernel.gridx = 0;
		gridBagConstraintsKernel.gridy = 2;
		gridBagConstraintsKernel.gridwidth = 1;//
		gridBagConstraintsKernel.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gridBagConstraintsKernel.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsKernel;
	}
	private GridBagConstraints getGridBagConstraintsEps() {
		GridBagConstraints gridBagConstraintsEps = new GridBagConstraints();
		gridBagConstraintsEps.gridx = 0;
		gridBagConstraintsEps.gridy = 4;
		gridBagConstraintsEps.gridwidth = 1;// ?
		gridBagConstraintsEps.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gridBagConstraintsEps.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsEps;
	}
	private GridBagConstraints getGridBagConstraintsMethod() {
		GridBagConstraints gridBagConstraintsMethod = new GridBagConstraints();
		gridBagConstraintsMethod.gridx = 0;
		gridBagConstraintsMethod.gridy = 5;
		gridBagConstraintsMethod.gridwidth = 1;// ?
		gridBagConstraintsMethod.insets = new Insets(5, 0, 0, 0); // top left/ bottom right
		gridBagConstraintsMethod.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsMethod;
	}
	private GridBagConstraints getGridBagConstraintsResultOptions() {
		GridBagConstraints gridBagConstraintsResultOptions = new GridBagConstraints();
		gridBagConstraintsResultOptions.gridx = 0;
		gridBagConstraintsResultOptions.gridy = 6;
		gridBagConstraintsResultOptions.gridwidth = 1;//
		gridBagConstraintsResultOptions.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gridBagConstraintsResultOptions.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsResultOptions;
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		if (buttBoxScan.isSelected())  pb.setParameter("ScanType", 0);
		if (buttNexelScan.isSelected()) pb.setParameter("ScanType", 1);

		pb.setParameter("BoxSize", ((Number) jSpinnerBoxSize.getValue()).intValue());

		if (buttRectangle.isSelected()) pb.setParameter("KernelShape", 0);
		if (buttCircle.isSelected())    pb.setParameter("KernelShape", 1);

		pb.setParameter("KernelSize", kernelSize);
		pb.setParameter("Eps", ((Number) jSpinnerEps.getValue()).intValue());

		if (buttBoxCount.isSelected())  pb.setParameter("Method", 0);
		if (buttPyramid.isSelected())   pb.setParameter("Method", 1);
		if (buttMinkowski.isSelected()) pb.setParameter("Method", 2);
		if (buttFFT.isSelected())       pb.setParameter("Method", 3);

		if (buttClamp.isSelected())         pb.setParameter("ResultOptions", 0);
		if (buttNormalize.isSelected())     pb.setParameter("ResultOptions", 1);
		if (buttActual.isSelected())        pb.setParameter("ResultOptions", 2);
		if (buttNormalize0to3.isSelected()) pb.setParameter("ResultOptions", 3);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		if (pb.getIntParameter("ScanType") == 0)
			buttBoxScan.setSelected(true);
		if (pb.getIntParameter("ScanType") == 1)
			buttNexelScan.setSelected(true);

		jSpinnerBoxSize.removeChangeListener(this);
		jSpinnerBoxSize.setValue(pb.getIntParameter("BoxSize"));
		jSpinnerBoxSize.addChangeListener(this);

		if (pb.getIntParameter("KernelShape") == 0)
			buttRectangle.setSelected(true);
		if (pb.getIntParameter("KernelShape") == 1)
			buttCircle.setSelected(true);

		kernelSize = pb.getIntParameter("KernelSize");
		jSpinnerKernelSize.removeChangeListener(this);
		jSpinnerKernelSize.setValue(kernelSize);
		jSpinnerKernelSize.addChangeListener(this);
		jSpinnerKernelSize.setToolTipText("Kernel size: " + kernelSize + "x"
				+ kernelSize);

		jSpinnerEps.removeChangeListener(this);
		jSpinnerEps.setValue(pb.getIntParameter("Eps"));
		jSpinnerEps.addChangeListener(this);

		if (pb.getIntParameter("Method") == 0)
			buttBoxCount.setSelected(true);
		if (pb.getIntParameter("Method") == 1)
			buttPyramid.setSelected(true);
		if (pb.getIntParameter("Method") == 2)
			buttMinkowski.setSelected(true);
		if (pb.getIntParameter("Method") == 3)
			buttFFT.setSelected(true);

		if (pb.getIntParameter("ResultOptions") == 0)
			buttClamp.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 1)
			buttNormalize.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 2)
			buttActual.setSelected(true);
		if (pb.getIntParameter("ResultOptions") == 3)
			buttNormalize0to3.setSelected(true);

		this.update();
	}



	/**
	 * This method updates the GUI This method overrides IqmOpJFrame
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// if (buttBoxCount.isSelected() ||
		// buttPyramid.isSelected() ||
		// buttPyramid.isSelected() ||
		// buttFFT.isSelected() ||

		// {
		// //do nothing
		// }
		// if (buttMinkowski.isSelected()){
		// jPanelEps.setEnabled(true);
		// jLabelEps.setEnabled(true);
		// jSpinnerEps.setEnabled(true);
		// }
		// else
		// {
		// jPanelEps.setEnabled(false);
		// jLabelEps.setEnabled(false);
		// jSpinnerEps.setEnabled(false);
		// }
		//

		if (buttBoxScan.isSelected()) {
			jPanelBoxSize.setEnabled(true);
			jLabelBoxSize.setEnabled(true);
			jSpinnerBoxSize.setEnabled(true);
		} else {
			jPanelBoxSize.setEnabled(false);
			jLabelBoxSize.setEnabled(false);
			jSpinnerBoxSize.setEnabled(false);
		}

		if (buttNexelScan.isSelected()) {
			tbKernel.setTitleColor(Color.BLACK);
			repaint();
			jPanelKernelShape.setEnabled(true);
			jLabelKernelShape.setEnabled(true);
			buttRectangle.setEnabled(true);
			buttCircle.setEnabled(true);
			jSpinnerPanelKernelSize.setEnabled(true);
			jSpinnerKernelSize.setEnabled(true);
			jLabelKernelSize.setEnabled(true);

		} else {
			tbKernel.setTitleColor(Color.GRAY);
			repaint();
			jPanelKernelShape.setEnabled(false);
			jLabelKernelShape.setEnabled(false);
			buttRectangle.setEnabled(false);
			buttCircle.setEnabled(false);
			jSpinnerPanelKernelSize.setEnabled(false);
			jSpinnerKernelSize.setEnabled(false);
			jLabelKernelSize.setEnabled(false);
		}

	}

	/**
	 * This method initializes the Option: BoxScan
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonBoxScan() {
		if (buttBoxScan == null) {
			buttBoxScan = new JRadioButton();
			buttBoxScan.setText("Box scan");
			buttBoxScan.setToolTipText("scanning a grid of boxes");
			//buttBoxScan.setIconTextGap(0);
			buttBoxScan.addActionListener(this);
			buttBoxScan.setActionCommand("parameter");
		}
		return buttBoxScan;
	}

	/**
	 * This method initializes the Option: NexelScan
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonNexelScan() {
		if (buttNexelScan == null) {
			buttNexelScan = new JRadioButton();
			buttNexelScan.setText("Nexel scan");
			buttNexelScan.setToolTipText("scanning with moving nexel (kernel)");
			//buttNexelScan.setIconTextGap(0);
			buttNexelScan.addActionListener(this);
			buttNexelScan.setActionCommand("parameter");
			buttNexelScan.setEnabled(true);
		}
		return buttNexelScan;
	}



	private void setButtonGroupScanType() {
		// if (ButtonGroup buttGroupScanType == null) {
		buttGroupScanType = new ButtonGroup();
		buttGroupScanType.add(buttBoxScan);
		buttGroupScanType.add(buttNexelScan);
	}

	/**
	 * This method initializes jJPanelScanType
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelScanType() {
		// if (jPanelScanType == null) {
		jPanelScanType = new JPanel();
		jPanelScanType.setLayout(new BoxLayout(jPanelScanType, BoxLayout.X_AXIS));
		jPanelScanType.setBorder(new TitledBorder(null, "Scan type", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelScanType.add(getJRadioButtonBoxScan());
		jPanelScanType.add(getJRadioButtonNexelScan());
		this.setButtonGroupScanType(); // Grouping of JRadioButtons
		// jPanelScanType.addSeparator();
		return jPanelScanType;
	}

	// ---------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelBoxSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBoxSize() {
		if (jPanelBoxSize == null) {
			jPanelBoxSize = new JPanel();
			//jPanelBoxSize.setLayout(new BoxLayout(jPanelBoxSize, BoxLayout.X_AXIS));
			jPanelBoxSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelBoxSize.setBorder(new TitledBorder(null, "Box size", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jLabelBoxSize = new JLabel("Box size [pixel]: ");
			// jLabelBoxSize.setPreferredSize(new Dimension(70, 22));
			jLabelBoxSize.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(16, 1, 1000, 1); // init, min, max, step
			jSpinnerBoxSize = new JSpinner(sModel);
			jSpinnerBoxSize.setPreferredSize(new Dimension(60, 20));
			jSpinnerBoxSize.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerBoxSize.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelBoxSize.add(jLabelBoxSize, BorderLayout.WEST);
			jPanelBoxSize.add(jSpinnerBoxSize, BorderLayout.CENTER);
		}
		return jPanelBoxSize;
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: Rectangle
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonRectangle() {
		if (buttRectangle == null) {
			buttRectangle = new JRadioButton();
			buttRectangle.setText("Rectangle");
			buttRectangle.setToolTipText("rectangular kernel shape");
			buttRectangle.setIconTextGap(0);
			buttRectangle.addActionListener(this);
			buttRectangle.setActionCommand("parameter");
		}
		return buttRectangle;
	}

	/**
	 * This method initializes the Option: Circle
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonCircle() {
		if (buttCircle == null) {
			buttCircle = new JRadioButton();
			buttCircle.setText("Circle");
			buttCircle.setToolTipText("circular kernel shape");
			buttCircle.setIconTextGap(0);
			buttCircle.addActionListener(this);
			buttCircle.setActionCommand("parameter");
			buttCircle.setEnabled(false);
		}
		return buttCircle;
	}

	/**
	 * This method initializes jJPanelKernelShape
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernelShape() {
		if (jPanelKernelShape == null) {
			jPanelKernelShape = new JPanel();
			jPanelKernelShape.setLayout(new BoxLayout(jPanelKernelShape, BoxLayout.X_AXIS));
			//jPanelKernelShape.setBorder(new TitledBorder(null, "Kernel shape", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jLabelKernelShape = new JLabel("Kernel shape: ");
			jPanelKernelShape.add(jLabelKernelShape);
			jPanelKernelShape.add(getJRadioButtonRectangle());
			jPanelKernelShape.add(getJRadioButtonCircle());
	
			// jPanelKernelShape.addSeparator();
			this.setButtonGroupKernelShape(); // Grouping of JRadioButtons
		}
		return jPanelKernelShape;
	}

	private void setButtonGroupKernelShape() {
		// if (ButtonGroup buttGroupKernelShape == null) {
		buttGroupKernelShape = new ButtonGroup();
		buttGroupKernelShape.add(buttRectangle);
		buttGroupKernelShape.add(buttCircle);
	}

	/**
	 * This method initializes jSpinnerKernelSize
	 * 
	 * @return {@link javax.swing.JPanel}
	 */
	private JPanel getJSpinnerKernelSize() {
		if (jSpinnerPanelKernelSize == null) {
			jSpinnerPanelKernelSize = new JPanel();
			//jSpinnerPanelKernelSize.setLayout(new BorderLayout());
			jSpinnerPanelKernelSize.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jSpinnerKernelSize = new JSpinner(new SpinnerNumberModel(3, 3, 101, 2));
			jSpinnerKernelSize.setEditor(new JSpinner.NumberEditor(jSpinnerKernelSize, "0"));
			((JSpinner.NumberEditor) jSpinnerKernelSize.getEditor()).getTextField().setEditable(false);
			jSpinnerKernelSize.setPreferredSize(new Dimension(60, 20));
			jSpinnerKernelSize.addChangeListener(this);	
			jLabelKernelSize = new JLabel();
			jLabelKernelSize.setText("Kernel size: ");
			jLabelKernelSize.setToolTipText("Size of the structuring element");
			jSpinnerPanelKernelSize.add(jLabelKernelSize);
			jSpinnerPanelKernelSize.add(jSpinnerKernelSize);
		}
		return jSpinnerPanelKernelSize;
	}
	
	/**
	 * This method initializes jPanelKernel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelKernel() {
		if (jPanelKernel == null) {
			jPanelKernel = new JPanel();
			jPanelKernel.setLayout(new BoxLayout(jPanelKernel, BoxLayout.Y_AXIS));
			tbKernel = new TitledBorder(null, "Kernel", TitledBorder.LEADING, TitledBorder.TOP, null, null);
			tbKernel.setTitleColor(Color.GRAY);
			jPanelKernel.setBorder(tbKernel);	
			jPanelKernel.add(getJPanelKernelShape());
			jPanelKernel.add(getJSpinnerKernelSize());
		}
		return jPanelKernel;
	}

	// ---------------------------------------------------------------------------
	/**
	 * This method initializes jJPanelEps
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelEps() {
		if (jPanelEps == null) {
			jPanelEps = new JPanel();
			//jPanelEps.setLayout(new BoxLayout(jPanelEps, BoxLayout.X_AXIS));
			jPanelEps.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelEps.setBorder(new TitledBorder(null, "Epsilon", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jLabelEps = new JLabel("eps: ");
			// jLabelEps.setPreferredSize(new Dimension(70, 22));
			jLabelEps.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(1, 0, 100, 1); // init, min, max, step
			jSpinnerEps = new JSpinner(sModel);
			jSpinnerEps.setPreferredSize(new Dimension(60, 20));
			jSpinnerEps.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerEps.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setEditable(true);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelEps.add(jLabelEps);
			jPanelEps.add(jSpinnerEps);
		}
		return jPanelEps;
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: BoxCount
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonBoxCount() {
		if (buttBoxCount == null) {
			buttBoxCount = new JRadioButton();
			buttBoxCount.setText("BoxCount");
			buttBoxCount.setToolTipText("Local Box counting dimension");
			buttBoxCount.addActionListener(this);
			buttBoxCount.setActionCommand("parameter");
			buttBoxCount.setEnabled(true);
		}
		return buttBoxCount;
	}

	/**
	 * This method initializes the Option: Pyramid
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonPyramid() {
		if (buttPyramid == null) {
			buttPyramid = new JRadioButton();
			buttPyramid.setText("Pyramid");
			buttPyramid.setToolTipText("local Pyramid dimension");
			buttPyramid.addActionListener(this);
			buttPyramid.setActionCommand("parameter");
			buttPyramid.setEnabled(true);
		}
		return buttPyramid;
	}

	/**
	 * This method initializes the Option: Minkowski
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMinkowski() {
		if (buttMinkowski == null) {
			buttMinkowski = new JRadioButton();
			buttMinkowski.setText("Minkowski");
			buttMinkowski
					.setToolTipText("lokal Minkowski or Blanket dimension");
			buttMinkowski.addActionListener(this);
			buttMinkowski.setActionCommand("parameter");
		}
		return buttMinkowski;
	}

	/**
	 * This method initializes the Option: FFT
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonFFT() {
		if (buttFFT == null) {
			buttFFT = new JRadioButton();
			buttFFT.setText("FFT");
			buttFFT.setToolTipText("local FFT dimension");
			buttFFT.addActionListener(this);
			buttFFT.setActionCommand("parameter");
			buttFFT.setEnabled(true);
		}
		return buttFFT;
	}

	/**
	 * This method initializes jJPanelMethod
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		// if (jPanelMethod == null) {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		jPanelMethod.add(getJRadioButtonBoxCount());
		jPanelMethod.add(getJRadioButtonPyramid());
		jPanelMethod.add(getJRadioButtonMinkowski());
		jPanelMethod.add(getJRadioButtonFFT());

		// jPanelMethod.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		// }
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroupMethod == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttBoxCount);
		buttGroupMethod.add(buttPyramid);
		buttGroupMethod.add(buttMinkowski);
		buttGroupMethod.add(buttFFT);
	}

	// ------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Clamp
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonClamp() {
		if (buttClamp == null) {
			buttClamp = new JRadioButton();
			buttClamp.setText("Clamp");
			buttClamp.setToolTipText("clamps result to byte");
			buttClamp.addActionListener(this);
			buttClamp.setActionCommand("parameter");
		}
		return buttClamp;
	}

	/**
	 * This method initializes the Option: Normalize
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonNormalize() {
		if (buttNormalize == null) {
			buttNormalize = new JRadioButton();
			buttNormalize.setText("Normalize");
			// buttNormalize.setPreferredSize(new Dimension(90,10));
			buttNormalize.setToolTipText("normalizes result to byte");
			buttNormalize.addActionListener(this);
			buttNormalize.setActionCommand("parameter");
		}
		return buttNormalize;
	}

	/**
	 * This method initializes the Option: Actual
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonActual() {
		if (buttActual == null) {
			buttActual = new JRadioButton();
			buttActual.setText("Actual");
			// buttActual.setPreferredSize(new Dimension(85,10));
			buttActual.setToolTipText("does nothing with result");
			buttActual.addActionListener(this);
			buttActual.setActionCommand("parameter");
			// buttActual.setEnabled(false);
		}
		return buttActual;
	}

	/**
	 * This method initializes the Option: Normalize0to3
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonNormalize0to3() {
		if (buttNormalize0to3 == null) {
			buttNormalize0to3 = new JRadioButton();
			buttNormalize0to3.setText("Normalize 0to3");
			// buttNormalize0to3.setPreferredSize(new Dimension(85,10));
			buttNormalize0to3
					.setToolTipText("normalizes the value range [0,3] to [0,255]");
			buttNormalize0to3.addActionListener(this);
			buttNormalize0to3.setActionCommand("parameter");
			// buttNormalize0to3.setEnabled(false);
		}
		return buttNormalize0to3;
	}

	/**
	 * This method initializes jJPanelResultOptions
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelResultOptions() {
		// if (jPanelResultOptions == null) {
		jPanelResultOptions = new JPanel();
		jPanelResultOptions.setLayout(new BoxLayout(jPanelResultOptions, BoxLayout.Y_AXIS));
		jPanelResultOptions.setBorder(new TitledBorder(null, "Result options", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		jPanelResultOptions.add(getJRadioButtonClamp());
		jPanelResultOptions.add(getJRadioButtonNormalize());
		jPanelResultOptions.add(getJRadioButtonActual());
		jPanelResultOptions.add(getJRadioButtonNormalize0to3());

		// jPanelResultOptions.addSeparator();
		this.setButtonGroupResultOptions(); // Grouping of JRadioButtons
		// }
		return jPanelResultOptions;
	}

	private void setButtonGroupResultOptions() {
		// if (ButtonGroup buttGroupResultOptions == null) {
		buttGroupResultOptions = new ButtonGroup();
		buttGroupResultOptions.add(buttClamp);
		buttGroupResultOptions.add(buttNormalize);
		buttGroupResultOptions.add(buttActual);
		buttGroupResultOptions.add(buttNormalize0to3);
	}


	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (e.getSource() == buttRectangle) {
				KernelJAI kernel = KernelFactory.createRectangle(kernelSize,
						kernelSize);
				BoardPanel.appendTextln("Kernel shape:");
				BoardPanel
						.appendTextln(KernelUtil.kernelToString(kernel, true));
			}
			if (e.getSource() == buttCircle) {
				KernelJAI kernel = KernelFactory
						.createCircle((kernelSize - 1) / 2); // Size = radius*2
																// +1
				BoardPanel.appendTextln("Kernel shape:");
				BoardPanel
						.appendTextln(KernelUtil.kernelToString(kernel, true));
			}
			if (e.getSource() == buttBoxCount) {
				PlanarImage pi = ((IqmDataBox) this.workPackage.getSources()
						.get(0)).getImage();
				int width = pi.getWidth();
				int height = pi.getHeight();
				int num = OperatorGUI_FracBox.getMaxBoxNumber(width, height);
				jSpinnerEps.removeChangeListener(this);
				SpinnerModel sModel = new SpinnerNumberModel(num, 0, num, 1); // init,
																				// min,
																				// max,
																				// step
				jSpinnerEps.setModel(sModel);
				jSpinnerEps.addChangeListener(this);
			}
			if (e.getSource() == buttPyramid) {
				PlanarImage pi = ((IqmDataBox) this.workPackage.getSources()
						.get(0)).getImage();
				int width = pi.getWidth();
				int height = pi.getHeight();
				int num = OperatorGUI_FracPyramid.getMaxPyramidNumber(width,
						height);
				jSpinnerEps.removeChangeListener(this);
				SpinnerModel sModel = new SpinnerNumberModel(num, 0, num, 1); // init,
																				// min,
																				// max,
																				// step
				jSpinnerEps.setModel(sModel);
				jSpinnerEps.addChangeListener(this);
			}
			if (e.getSource() == buttMinkowski) {
				jSpinnerEps.removeChangeListener(this);
				SpinnerModel sModel = new SpinnerNumberModel(20, 0, 100, 1); // init,
																				// min,
																				// max,
																				// step
				jSpinnerEps.setModel(sModel);
				jSpinnerEps.addChangeListener(this);
			}
			if (e.getSource() == buttFFT) {
				PlanarImage pi = ((IqmDataBox) this.workPackage.getSources()
						.get(0)).getImage();
				int width = pi.getWidth();
				int height = pi.getHeight();
				int num = OperatorGUI_FracFFT.getMaxK(width, height);
				jSpinnerEps.removeChangeListener(this);
				SpinnerModel sModel = new SpinnerNumberModel(num, 0, num, 1); // init,
																				// min,
																				// max,
																				// step
				jSpinnerEps.setModel(sModel);
				jSpinnerEps.addChangeListener(this);

			}
			this.updateParameterBlock();
			this.update(); 
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
		Object obE = e.getSource();
		if (obE == jSpinnerKernelSize) {
			kernelSize = ((Number) jSpinnerKernelSize.getValue()).intValue();
			System.out.println(kernelSize);
			jSpinnerKernelSize.setToolTipText("Kernel size: " + kernelSize + "x" + kernelSize);
		}
		
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}
	}
}// END
