package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracIFS.java
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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracIFSDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2009 10
 */
public class OperatorGUI_FracIFS extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5589979392178850162L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_FracIFS.class);

	/**
	 * The cached parameter block from the work package.
	 */
	private ParameterBlockIQM pb = null;

	private JPanel   jPanelWidth    = null;
	private JLabel   jLabelWidth    = null;
	private JSpinner jSpinnerWidth  = null;
	private JPanel   jPanelHeight   = null;
	private JLabel   jLabelHeight   = null;
	private JSpinner jSpinnerHeight = null;
	private JPanel   jPanelSize     = null;

	private JPanel   jPanelItMax     = null;
	private JLabel   jLabelItMax     = null;
	private JSpinner jSpinnerItMax   = null;
	private JPanel   jPanelNumPoly   = null;
	private JLabel   jLabelNumPoly   = null;
	private JSpinner jSpinnerNumPoly = null;
	private JPanel   jPanelOptions   = null;

	private JRadioButton buttSierpinski1  = null;
	private JRadioButton buttSierpinski2  = null;
	private JRadioButton buttFern         = null;
	private JRadioButton buttKochLine     = null;
	private JRadioButton buttKochSnow     = null;
	private JRadioButton buttMenger       = null;
	private JRadioButton buttHeighway     = null;
	private JPanel       jPanelFracTyp    = null;
	private ButtonGroup  buttGroupFracTyp = null;


	/**
	 * constructor
	 */
	public OperatorGUI_FracIFS() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpFracIFSDescriptor().getName());

		this.initialize();

		this.setTitle("Fractal IFS Generator");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.ifs.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelSize(),      getGridBagConstraintsSize());
		this.getOpGUIContent().add(getJPanelFracTypes(), getGridBagConstraintsFracTypes());
		this.getOpGUIContent().add(getJPanelOptions(),   getGridBagConstraintsOptions());

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		pb.setParameter("Width",  ((Number) jSpinnerWidth.getValue()).intValue());
		pb.setParameter("Height", ((Number) jSpinnerHeight.getValue()).intValue());
		pb.setParameter("ItMax",  ((Number) jSpinnerItMax.getValue()).intValue());
		pb.setParameter("NumPoly",((Number) jSpinnerNumPoly.getValue()).intValue());
		if (buttSierpinski1.isSelected()) pb.setParameter("FracType", 0);
		if (buttSierpinski2.isSelected()) pb.setParameter("FracType", 1);
		if (buttFern.isSelected())     pb.setParameter("FracType", 2);
		if (buttKochLine.isSelected()) pb.setParameter("FracType", 3);
		if (buttKochSnow.isSelected()) pb.setParameter("FracType", 4);
		if (buttMenger.isSelected())   pb.setParameter("FracType", 5);
		if (buttHeighway.isSelected()) pb.setParameter("FracType", 6);

		jSpinnerNumPoly.setEnabled(false);
		if (buttKochSnow.isSelected()) jSpinnerNumPoly.setEnabled(true);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		// there is no need for a current image
		jSpinnerWidth.removeChangeListener(this);
		jSpinnerHeight.removeChangeListener(this);
		jSpinnerItMax.removeChangeListener(this);
		jSpinnerNumPoly.removeChangeListener(this);
		jSpinnerWidth.setValue(pb.getIntParameter("Width"));
		jSpinnerHeight.setValue(pb.getIntParameter("Height"));
		jSpinnerItMax.setValue(pb.getIntParameter("ItMax"));
		jSpinnerNumPoly.setValue(pb.getIntParameter("NumPoly"));
		jSpinnerWidth.addChangeListener(this);
		jSpinnerHeight.addChangeListener(this);
		jSpinnerItMax.addChangeListener(this);
		jSpinnerNumPoly.addChangeListener(this);

		if (pb.getIntParameter("FracType") == 0) buttSierpinski1.setSelected(true);
		if (pb.getIntParameter("FracType") == 1) buttSierpinski2.setSelected(true);
		if (pb.getIntParameter("FracType") == 2) buttFern.setSelected(true);
		if (pb.getIntParameter("FracType") == 3) buttKochLine.setSelected(true);
		if (pb.getIntParameter("FracType") == 4) buttKochSnow.setSelected(true);
		if (pb.getIntParameter("FracType") == 5) buttMenger.setSelected(true);
		if (pb.getIntParameter("FracType") == 6) buttHeighway.setSelected(true);

		jSpinnerNumPoly.setEnabled(false);
		if (buttKochSnow.isSelected()) jSpinnerNumPoly.setEnabled(true);

	}

	private GridBagConstraints getGridBagConstraintsSize() {
		GridBagConstraints gridBagConstraintsSize = new GridBagConstraints();
		gridBagConstraintsSize.gridx = 0;
		gridBagConstraintsSize.gridy = 0;
		gridBagConstraintsSize.gridwidth = 2;// ?
		gridBagConstraintsSize.insets = new Insets(5, 0, 0, 0); // top left bottom right
		// gridBagConstraintsSize.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsSize;
	}

	private GridBagConstraints getGridBagConstraintsFracTypes() {
		GridBagConstraints gridBagConstraints_FracTypes = new GridBagConstraints();
		gridBagConstraints_FracTypes.gridx = 0;
		gridBagConstraints_FracTypes.gridy = 1;
		//gridBagConstraints_Options.gridheight = 2;// ?
		gridBagConstraints_FracTypes.insets = new Insets(5, 0, 0, 0); // top left bottom right
		// gridBagConstraintsButtonFracTypGroup.fill = GridBagConstraints.BOTH;
		return gridBagConstraints_FracTypes;
	}

	private GridBagConstraints getGridBagConstraintsOptions() {
		GridBagConstraints gridBagConstraintsOptions = new GridBagConstraints();
		gridBagConstraintsOptions.gridx = 1;
		gridBagConstraintsOptions.gridy = 1;
		gridBagConstraintsOptions.gridwidth = 1;// ?
		gridBagConstraintsOptions.insets = new Insets(5, 0, 0, 0); // top left bottom right
		// gridBagConstraintsItMax.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsOptions;
	}
	/**
	 * This method updates the GUI, if needed.
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	/**
	 * This method initializes jJPanelWidth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelWidth() {
		if (jPanelWidth == null) {
			jPanelWidth = new JPanel();
			jPanelWidth.setLayout(new BorderLayout());
			jLabelWidth = new JLabel("Width: ");
			jLabelWidth.setPreferredSize(new Dimension(70, 22));
			jLabelWidth.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(512, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerWidth = new JSpinner(sModel);
			jSpinnerWidth.setPreferredSize(new Dimension(60, 24));
			jSpinnerWidth.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerWidth.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelWidth.add(jLabelWidth, BorderLayout.WEST);
			jPanelWidth.add(jSpinnerWidth, BorderLayout.CENTER);
		}
		return jPanelWidth;
	}

	/**
	 * This method initializes jJPanelHeight
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelHeight() {
		if (jPanelHeight == null) {
			jPanelHeight = new JPanel();
			jPanelHeight.setLayout(new BorderLayout());
			jLabelHeight = new JLabel("Height: ");
			jLabelHeight.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelHeight.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(512, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerHeight = new JSpinner(sModel);
			jSpinnerHeight.setPreferredSize(new Dimension(60, 24));
			jSpinnerHeight.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerHeight.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelHeight.add(jLabelHeight, BorderLayout.WEST);
			jPanelHeight.add(jSpinnerHeight, BorderLayout.CENTER);
			jSpinnerHeight.setEnabled(false);
		}
		return jPanelHeight;
	}
	
	/**
	 * This method initializes jPanelSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSize() {
		if (jPanelSize == null) {
			jPanelSize = new JPanel();
			jPanelSize.setLayout(new BoxLayout(jPanelSize, BoxLayout.X_AXIS));
			jPanelSize.setBorder(new TitledBorder(null, "Image size", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelSize.add(getJPanelWidth());
			jPanelSize.add(getJPanelHeight());
			// jPanelSize.addSeparator();
		}
		return jPanelSize;
	}


	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtSierpinski1() {
		if (buttSierpinski1 == null) {
			buttSierpinski1 = new JRadioButton();
			buttSierpinski1.setText("Sierpinski 1");
			buttSierpinski1.setToolTipText("generates a Sierpinski gasket by adding smaller triangles D=log(3)/log(2)=1.585");
			buttSierpinski1.addActionListener(this);
			buttSierpinski1.setActionCommand("parameter");
		}
		return buttSierpinski1;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtSierpinski2() {
		if (buttSierpinski2 == null) {
			buttSierpinski2 = new JRadioButton();
			buttSierpinski2.setText("Sierpinski 2");
			buttSierpinski2.setToolTipText("generates a Sierpinski gasket (triangle) D=log(3)/log(2)=1.585");
			buttSierpinski2.addActionListener(this);
			buttSierpinski2.setActionCommand("parameter");
		}
		return buttSierpinski2;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtFern() {
		if (buttFern == null) {
			buttFern = new JRadioButton();
			buttFern.setText("Fern");
			buttFern.setToolTipText("generates a Fern");
			buttFern.addActionListener(this);
			buttFern.setActionCommand("parameter");
		}
		return buttFern;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtKochLine() {
		// if (buttKochLine == null) {
		buttKochLine = new JRadioButton();
		buttKochLine.setText("Koch Line");
		buttKochLine.setToolTipText("generates a single Koch curev D=log(4)/log(3)=1.2619");
		buttKochLine.addActionListener(this);
		buttKochLine.setActionCommand("parameter");
		// }
		return buttKochLine;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtKochSnow() {
		// if (buttKochSnow == null) {
		buttKochSnow = new JRadioButton();
		buttKochSnow.setText("Koch Snowflake");
		buttKochSnow.setToolTipText("generates a Koch Snowflake");
		buttKochSnow.addActionListener(this);
		buttKochSnow.setActionCommand("parameter");
		// }
		return buttKochSnow;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtMenger() {
		// if (buttMenger == null) {
		buttMenger = new JRadioButton();
		buttMenger.setText("Menger");
		buttMenger.setToolTipText("generates a Menger carpet (Sierpinski carpet) D=log(8)/log(3)=1.8928");
		buttMenger.addActionListener(this);
		buttMenger.setActionCommand("parameter");
		// }
		return buttMenger;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtHeighway() {
		// if (buttHeighway == null) {
		buttHeighway = new JRadioButton();
		buttHeighway.setText("Heighway Dragon");
		buttHeighway.setToolTipText("generates a Heighway dragon D=ln(2)/ln(sqrt(2))=2  Boundary: D=1.5236270862");
		buttHeighway.addActionListener(this);
		buttHeighway.setActionCommand("parameter");
		// }
		return buttHeighway;
	}

	/**
	 * This method initializes jJPanelBar
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelFracTypes() {
		// if (jPanelFracTyp== null) {
		jPanelFracTyp = new JPanel();
		jPanelFracTyp.setLayout(new BoxLayout(jPanelFracTyp, BoxLayout.Y_AXIS));
		jPanelFracTyp.setBorder(new TitledBorder(null, "Fractal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelFracTyp.add(getJRadioButtonButtSierpinski1());
		jPanelFracTyp.add(getJRadioButtonButtSierpinski2());
		jPanelFracTyp.add(getJRadioButtonButtFern());
		jPanelFracTyp.add(getJRadioButtonButtKochLine());
		jPanelFracTyp.add(getJRadioButtonButtKochSnow());
		jPanelFracTyp.add(getJRadioButtonButtMenger());
		jPanelFracTyp.add(getJRadioButtonButtHeighway());
		// jPanelFracTyp.addSeparator();
		this.setButtonGroupFracTyp(); // Grouping of JRadioButtons
		// }
		return jPanelFracTyp;
	}

	private void setButtonGroupFracTyp() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupFracTyp = new ButtonGroup();
		buttGroupFracTyp.add(buttSierpinski1);
		buttGroupFracTyp.add(buttSierpinski2);
		buttGroupFracTyp.add(buttFern);
		buttGroupFracTyp.add(buttKochLine);
		buttGroupFracTyp.add(buttKochSnow);
		buttGroupFracTyp.add(buttMenger);
		buttGroupFracTyp.add(buttHeighway);
	}
	// --------------------------------------------------------------------------------------------

	/**
	 * This method initializes jJPanelItMax
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelItMax() {
		if (jPanelItMax == null) {
			jPanelItMax = new JPanel();
			jPanelItMax.setLayout(new BorderLayout());
			jLabelItMax = new JLabel("ItMax: ");
			jLabelItMax.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelItMax.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(4, 0, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerItMax = new JSpinner(sModel);
			jSpinnerItMax.setPreferredSize(new Dimension(60, 24));
			jSpinnerItMax.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerItMax.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelItMax.add(jLabelItMax, BorderLayout.WEST);
			jPanelItMax.add(jSpinnerItMax, BorderLayout.CENTER);
		}
		return jPanelItMax;
	}

	/**
	 * This method initializes jJPanelNumPoly
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumPoly() {
		if (jPanelNumPoly == null) {
			jPanelNumPoly = new JPanel();
			jPanelNumPoly.setLayout(new BorderLayout());
			jLabelNumPoly = new JLabel("NumPoly: ");
			jLabelNumPoly.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelNumPoly.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(3, 3, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumPoly = new JSpinner(sModel);
			jSpinnerNumPoly.setPreferredSize(new Dimension(60, 24));
			jSpinnerNumPoly.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumPoly.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			jPanelNumPoly.add(jLabelNumPoly, BorderLayout.WEST);
			jPanelNumPoly.add(jSpinnerNumPoly, BorderLayout.CENTER);
		}
		return jPanelNumPoly;
	}
	
	/**
	 * This method initializes jPanelOpzions
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOptions() {
		if (jPanelOptions == null) {
			jPanelOptions = new JPanel();
			jPanelOptions.setLayout(new BoxLayout(jPanelOptions, BoxLayout.Y_AXIS));
			jPanelOptions.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelOptions.add(getJPanelItMax());
			jPanelOptions.add(getJPanelNumPoly());
			// jPanelSpinners.addSeparator();
		}
		return jPanelOptions;
	}

	// --------------------------------------------------------------------------------------------
	
	// --------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			int itMax = ((Number) jSpinnerItMax.getValue()).intValue();
			if (buttSierpinski1 == e.getSource()) {
				if (itMax > 10)
					jSpinnerItMax.setValue(10);
			}
			if (buttSierpinski2 == e.getSource()) {
				if (itMax > 10) jSpinnerItMax.setValue(10);
			}
			if (buttFern == e.getSource()) {
				// if (itMax > 10) jSpinnerItMax.setValue(10);
			}
			if (buttKochLine == e.getSource()) {
				if (itMax > 5) jSpinnerItMax.setValue(5);
			}
			if (buttKochSnow == e.getSource()) {
				if (itMax > 10) jSpinnerItMax.setValue(10);
			}
			if (buttMenger == e.getSource()) {
				if (itMax > 10) jSpinnerItMax.setValue(10);
			}
			if (buttHeighway == e.getSource()) {
				if (itMax > 10) jSpinnerItMax.setValue(10);
			}
			this.updateParameterBlock();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {

		int width = ((Number) jSpinnerWidth.getValue()).intValue();

		if (jSpinnerWidth == e.getSource()) {
			jSpinnerHeight.setValue(width);
		}
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
}// END
