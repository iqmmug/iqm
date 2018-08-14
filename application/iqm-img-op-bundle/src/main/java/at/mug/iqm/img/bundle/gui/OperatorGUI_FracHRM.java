package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_FracHRM.java
 * 
 * $Id: OperatorGUI_FracHRM.java 548 2016-01-18 09:36:47Z kainzp $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/trunk/iqm/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/gui/OperatorGUI_FracHRM.java $
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.img.bundle.Resources;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracHRMDescriptor;


/**
 * @author Ahammer
 * @since  2016 06
 */
public class OperatorGUI_FracHRM extends AbstractImageOperatorGUI implements
		ActionListener, ChangeListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4357820134181937876L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_FracHRM.class);

	/**
	 * The cached parameter block from the work package.
	 */
	private ParameterBlockIQM pb = null;

	private JPanel   jPanelInitWidth    = null;
	private JLabel   jLabelInitWidth    = null;
	private JSpinner jSpinnerInitWidth  = null;
	private JPanel   jPanelInitHeight   = null;
	private JLabel   jLabelInitHeight   = null;
	private JSpinner jSpinnerInitHeight = null;
	private JPanel   jPanelInitSize     = null;
	
	private JLabel   jLabelFinalSize  = null;
	private JPanel   jPanelFinalSize  = null;

	private JPanel   jPanelItMax     = null;
	private JLabel   jLabelItMax     = null;
	private JSpinner jSpinnerItMax   = null;
	
	private JPanel   jPanelP1        = null;
	private JLabel   jLabelP1        = null;
	private JSpinner jSpinnerP1      = null;
	private JPanel   jPanelP2        = null;
	private JLabel   jLabelP2        = null;
	private JSpinner jSpinnerP2      = null;
	private JPanel   jPanelP3        = null;
	private JLabel   jLabelP3        = null;
	private JSpinner jSpinnerP3      = null;
	private JPanel   jPanelP4        = null;
	private JLabel   jLabelP4        = null;
	private JSpinner jSpinnerP4      = null;
	private JPanel   jPanelP5        = null;
	private JLabel   jLabelP5        = null;
	private JSpinner jSpinnerP5      = null;
	private JPanel   jPanelP6        = null;
	private JLabel   jLabelP6        = null;
	private JSpinner jSpinnerP6      = null;
	private JPanel   jPanelP7        = null;
	private JLabel   jLabelP7        = null;
	private JSpinner jSpinnerP7      = null;
	private JPanel   jPanelP8        = null;
	private JLabel   jLabelP8        = null;
	private JSpinner jSpinnerP8      = null;
	private JPanel   jPanelSettings   = null;

	private JRadioButton buttPlotnick      = null;
	private JRadioButton buttAlternative1  = null;
	private JRadioButton buttAlternative2  = null;
	private JPanel       jPanelMethod      = null;
	private ButtonGroup  buttGroupMethod   = null;


	/**
	 * constructor
	 */
	public OperatorGUI_FracHRM() {
		logger.debug("Now initializing...");

		this.setOpName(new IqmOpFracHRMDescriptor().getName());

		this.initialize();

		this.setTitle("Lacunarity HRM Generator");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getImageURL("icon.gui.fractal.hrm.enabled")));
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelInitSize(),  getGBC_InitSize());
		this.getOpGUIContent().add(getJPanelFinalSize(), getGBC_FinalSize());
		this.getOpGUIContent().add(getJPanelSettings(),  getGBC_Settings());
		this.getOpGUIContent().add(getJPanelMethods(),   getGBC_Methods());

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 * 
	 */
	@Override
	public void updateParameterBlock() {
		pb.setParameter("Width",  ((Number) jSpinnerInitWidth.getValue()).intValue());
		pb.setParameter("Height", ((Number) jSpinnerInitHeight.getValue()).intValue());
		pb.setParameter("ItMax",  ((Number) jSpinnerItMax.getValue()).intValue());
		pb.setParameter("P1",     ((Number) jSpinnerP1.getValue()).doubleValue());
		pb.setParameter("P2",     ((Number) jSpinnerP2.getValue()).doubleValue());
		pb.setParameter("P3",     ((Number) jSpinnerP3.getValue()).doubleValue());
		pb.setParameter("P4",     ((Number) jSpinnerP4.getValue()).doubleValue());
		pb.setParameter("P5",     ((Number) jSpinnerP5.getValue()).doubleValue());
		pb.setParameter("P6",     ((Number) jSpinnerP6.getValue()).doubleValue());
		pb.setParameter("P7",     ((Number) jSpinnerP7.getValue()).doubleValue());
		pb.setParameter("P8",     ((Number) jSpinnerP8.getValue()).doubleValue());
		if (buttPlotnick.isSelected())     pb.setParameter("Method", 0);
		if (buttAlternative1.isSelected()) pb.setParameter("Method", 1);
		if (buttAlternative2.isSelected()) pb.setParameter("Method", 2);
	}

	/**
	 * This method sets the current parameter values
	 * 
	 */
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		// there is no need for a current image
		jSpinnerInitWidth.removeChangeListener(this);
		jSpinnerInitHeight.removeChangeListener(this);
		jSpinnerItMax.removeChangeListener(this);
		jSpinnerP1.removeChangeListener(this);
		jSpinnerP2.removeChangeListener(this);		
		jSpinnerP3.removeChangeListener(this);
		jSpinnerP4.removeChangeListener(this);
		jSpinnerP5.removeChangeListener(this);
		jSpinnerP6.removeChangeListener(this);
		jSpinnerP7.removeChangeListener(this);
		jSpinnerP8.removeChangeListener(this);
		jSpinnerInitWidth.setValue(pb.getIntParameter("Width"));
		jSpinnerInitHeight.setValue(pb.getIntParameter("Height"));
		jSpinnerItMax.setValue(pb.getIntParameter("ItMax"));
		jSpinnerP1.setValue(pb.getDoubleParameter("P1"));
		jSpinnerP2.setValue(pb.getDoubleParameter("P2"));
		jSpinnerP3.setValue(pb.getDoubleParameter("P3"));
		jSpinnerP4.setValue(pb.getDoubleParameter("P4"));
		jSpinnerP5.setValue(pb.getDoubleParameter("P5"));
		jSpinnerP6.setValue(pb.getDoubleParameter("P6"));
		jSpinnerP7.setValue(pb.getDoubleParameter("P7"));
		jSpinnerP8.setValue(pb.getDoubleParameter("P8"));
		jSpinnerInitWidth.addChangeListener(this);
		jSpinnerInitHeight.addChangeListener(this);
		jSpinnerItMax.addChangeListener(this);
		jSpinnerP1.addChangeListener(this);
		jSpinnerP2.addChangeListener(this);
		jSpinnerP3.addChangeListener(this);
		jSpinnerP4.addChangeListener(this);
		jSpinnerP5.addChangeListener(this);
		jSpinnerP6.addChangeListener(this);
		jSpinnerP7.addChangeListener(this);
		jSpinnerP8.addChangeListener(this);
	
		if (pb.getIntParameter("Method") == 0) buttPlotnick.setSelected(true);
		if (pb.getIntParameter("Method") == 1) buttAlternative1.setSelected(true);
		if (pb.getIntParameter("Method") == 2) buttAlternative2.setSelected(true);
	}

	private GridBagConstraints getGBC_InitSize() {
		GridBagConstraints gbcInitSize = new GridBagConstraints();
		gbcInitSize.gridx = 0;
		gbcInitSize.gridy = 0;
		gbcInitSize.gridwidth = 2;// ?
		gbcInitSize.insets = new Insets(5, 0, 0, 0); // top left bottom right
		//gbcInitSize.fill = GridBagConstraints.BOTH;
		return gbcInitSize;
	}
	
	private GridBagConstraints getGBC_FinalSize() {
		GridBagConstraints gbcFinalSize = new GridBagConstraints();
		gbcFinalSize.gridx = 0;
		gbcFinalSize.gridy = 1;
		gbcFinalSize.gridwidth = 2;// ?
		gbcFinalSize.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbcFinalSize.fill = GridBagConstraints.BOTH;
		return gbcFinalSize;
	}

	private GridBagConstraints getGBC_Settings() {
		GridBagConstraints gbcSettings = new GridBagConstraints();
		gbcSettings.gridx = 0;
		gbcSettings.gridy = 2;
		gbcSettings.gridwidth = 1;// ?
		gbcSettings.insets = new Insets(5, 0, 0, 0); // top left bottom right
		// gridBagConstraintsSettings.fill = GridBagConstraints.BOTH;
		return gbcSettings;
	}
	
	private GridBagConstraints getGBC_Methods() {
		GridBagConstraints gbcMethods = new GridBagConstraints();
		gbcMethods.gridx = 1;
		gbcMethods.gridy = 2;
		//gridBagConstraints_Methods.gridheight = 2;// ?
		gbcMethods.insets = new Insets(5, 0, 0, 0); // top left bottom right
		// gridBagConstraints_Methods.fill = GridBagConstraints.BOTH;
		return gbcMethods;
	}
	
	/**
	 * This method updates the GUI, if needed.
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		
		int initWidth   = ((Number) jSpinnerInitWidth.getValue()).intValue();
		int initHeight  = ((Number) jSpinnerInitWidth.getValue()).intValue();
		int itMax       = ((Number) jSpinnerItMax.getValue()).intValue();	
		int finalWidth  = (int)Math.pow(initWidth, itMax);
		int finalHeight = (int)Math.pow(initHeight,itMax);
		if (finalWidth < 10000 && finalHeight < 10000){
			jLabelFinalSize.setText("            Width: " + finalWidth + "                      Height: " + finalHeight);
			jLabelFinalSize.setForeground(Color.BLACK);
			jLabelFinalSize.setToolTipText("Final width and height");
		} else {	
			jLabelFinalSize.setText("            Width: " + finalWidth + " !                    Height: " + finalHeight +" !");
			jLabelFinalSize.setForeground(Color.RED);
			jLabelFinalSize.setToolTipText("Values seem to be far too high");
		}
		enableAllSpinners();
		if (itMax < 8) this.disableSpinner(8);
		if (itMax < 7) this.disableSpinner(7);
		if (itMax < 6) this.disableSpinner(6);
		if (itMax < 5) this.disableSpinner(5);
		if (itMax < 4) this.disableSpinner(4);
		if (itMax < 3) this.disableSpinner(3);
		if (itMax < 2) this.disableSpinner(2);
		
		this.setParameterValuesToGUI();	
	}

	/**
	 * This method initializes jJPanelWidth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelWidth() {
		if (jPanelInitWidth == null) {
			jPanelInitWidth = new JPanel();
			jPanelInitWidth.setLayout(new BorderLayout());
			jLabelInitWidth = new JLabel("Width: ");
			jLabelInitWidth.setPreferredSize(new Dimension(70, 22));
			jLabelInitWidth.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(3, 2, 10, 1); // init, min, max, step
			jSpinnerInitWidth = new JSpinner(sModel);
			jSpinnerInitWidth.setPreferredSize(new Dimension(60, 24));
			jSpinnerInitWidth.addChangeListener(this);
			jSpinnerInitWidth.setEditor(new JSpinner.NumberEditor(jSpinnerInitWidth, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerInitWidth.getEditor()).getTextField().setEditable(true);	
			jPanelInitWidth.add(jLabelInitWidth, BorderLayout.WEST);
			jPanelInitWidth.add(jSpinnerInitWidth, BorderLayout.CENTER);
		}
		return jPanelInitWidth;
	}

	/**
	 * This method initializes jJPanelHeight
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelHeight() {
		if (jPanelInitHeight == null) {
			jPanelInitHeight = new JPanel();
			jPanelInitHeight.setLayout(new BorderLayout());
			jLabelInitHeight = new JLabel("Height: ");
			jLabelInitHeight.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelInitHeight.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(3, 2, 10, 1); // init, min, max, step
			jSpinnerInitHeight = new JSpinner(sModel);
			jSpinnerInitHeight.setPreferredSize(new Dimension(60, 24));
			jSpinnerInitHeight.addChangeListener(this);
			jSpinnerInitHeight.setEditor(new JSpinner.NumberEditor(jSpinnerInitHeight, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerInitHeight.getEditor()).getTextField().setEditable(true);	
			jPanelInitHeight.add(jLabelInitHeight, BorderLayout.WEST);
			jPanelInitHeight.add(jSpinnerInitHeight, BorderLayout.CENTER);
			jSpinnerInitHeight.setEnabled(false);
		}
		return jPanelInitHeight;
	}
	
	/**
	 * This method initializes jPanelInitSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelInitSize() {
		if (jPanelInitSize == null) {
			jPanelInitSize = new JPanel();
			jPanelInitSize.setLayout(new BoxLayout(jPanelInitSize, BoxLayout.X_AXIS));
			jPanelInitSize.setBorder(new TitledBorder(null, "Initial image size", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelInitSize.add(getJPanelWidth());
			jPanelInitSize.add(getJPanelHeight());
			//jPanelInitSize.addSeparator();
		}
		return jPanelInitSize;
	}

	/**
	 * This method initializes jPanelFinalSize
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelFinalSize() {
		if (jPanelFinalSize == null) {
			jPanelFinalSize = new JPanel();
			jPanelFinalSize.setLayout(new BoxLayout(jPanelFinalSize, BoxLayout.X_AXIS));
			jPanelFinalSize.setBorder(new TitledBorder(null, "Final image size", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			if (jLabelFinalSize == null) jLabelFinalSize = new JLabel();
			jLabelFinalSize.setPreferredSize(new Dimension(70, 22));
			jPanelFinalSize.add(jLabelFinalSize);
			//jPanelFinalSize.addSeparator();
		}
		return jPanelFinalSize;
	}
	
	/**
	 * This method initializes a Button:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getButtPlotnick() {
		if (buttPlotnick == null) {
			buttPlotnick = new JRadioButton();
			buttPlotnick.setText("Plotnick");
			buttPlotnick.setToolTipText("generates a hierarchical random map according to the Plotnick etal 1993 paper");
			buttPlotnick.addActionListener(this);
			buttPlotnick.setActionCommand("parameter");
			buttPlotnick.setEnabled(true);
		}
		return buttPlotnick;
	}

	/**
	 * This method initializes a button:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getButtAlternative1() {
		if (buttAlternative1 == null) {
			buttAlternative1 = new JRadioButton();
			buttAlternative1.setText("Alternative1");
			buttAlternative1.setToolTipText("alternative method");
			buttAlternative1.addActionListener(this);
			buttAlternative1.setActionCommand("parameter");
			buttAlternative1.setEnabled(false);
		}
		return buttAlternative1;
	}

	/**
	 * This method initializes a button:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getButtAlternative2() {
		if (buttAlternative2 == null) {
			buttAlternative2 = new JRadioButton();
			buttAlternative2.setText("Alternative2");
			buttAlternative2.setToolTipText("alternative method");
			buttAlternative2.addActionListener(this);
			buttAlternative2.setActionCommand("parameter");
			buttAlternative2.setEnabled(false);
		}
		return buttAlternative2;
	}



	/**
	 * This method initializes a jJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethods() {
		// if (jPanelImgTyp== null) {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanelMethod.add(getButtPlotnick());
		jPanelMethod.add(getButtAlternative1());
		jPanelMethod.add(getButtAlternative2());
		jPanelMethod.setEnabled(true);
		// jPanelImgTyp.addSeparator();
		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		// }
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		// if (ButtonGroup buttGroup == null) {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttPlotnick);
		buttGroupMethod.add(buttAlternative1);
		buttGroupMethod.add(buttAlternative2);
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
			SpinnerModel sModel = new SpinnerNumberModel(3, 1, 8, 1); // init, min, max, step
			jSpinnerItMax = new JSpinner(sModel);
			jSpinnerItMax.setPreferredSize(new Dimension(60, 24));
			jSpinnerItMax.addChangeListener(this);
			jSpinnerItMax.setEditor(new JSpinner.NumberEditor(jSpinnerItMax, "#"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerItMax.getEditor()).getTextField().setEditable(true);	
			jPanelItMax.add(jLabelItMax, BorderLayout.WEST);
			jPanelItMax.add(jSpinnerItMax, BorderLayout.CENTER);
		}
		return jPanelItMax;
	}

	/**
	 * This method initializes jJPanelP1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelP1() {
		if (jPanelP1 == null) {
			jPanelP1 = new JPanel();
			jPanelP1.setLayout(new BorderLayout());
			jLabelP1 = new JLabel("P1: ");
			jLabelP1.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelP1.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.01); // init, min, max, step
			jSpinnerP1 = new JSpinner(sModel);
			jSpinnerP1.setPreferredSize(new Dimension(60, 24));
			jSpinnerP1.addChangeListener(this);
			jSpinnerP1.setEditor(new JSpinner.NumberEditor(jSpinnerP1, "0.00"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerP1.getEditor()).getTextField().setEditable(true);	
			jPanelP1.add(jLabelP1, BorderLayout.WEST);
			jPanelP1.add(jSpinnerP1, BorderLayout.CENTER);
		}
		return jPanelP1;
	}
	
	/**
	 * This method initializes jJPanelP2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelP2() {
		if (jPanelP2 == null) {
			jPanelP2 = new JPanel();
			jPanelP2.setLayout(new BorderLayout());
			jLabelP2 = new JLabel("P2: ");
			jLabelP2.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelP2.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.01); // init, min, max, step
			jSpinnerP2 = new JSpinner(sModel);
			jSpinnerP2.setPreferredSize(new Dimension(60, 24));
			jSpinnerP2.addChangeListener(this);
			jSpinnerP2.setEditor(new JSpinner.NumberEditor(jSpinnerP2, "0.00"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerP2.getEditor()).getTextField().setEditable(true);	
			jPanelP2.add(jLabelP2, BorderLayout.WEST);
			jPanelP2.add(jSpinnerP2, BorderLayout.CENTER);
		}
		return jPanelP2;
	}
	
	/**
	 * This method initializes jJPanelP3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelP3() {
		if (jPanelP3 == null) {
			jPanelP3 = new JPanel();
			jPanelP3.setLayout(new BorderLayout());
			jLabelP3 = new JLabel("P3: ");
			jLabelP3.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelP3.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.01); // init, min, max, step
			jSpinnerP3 = new JSpinner(sModel);
			jSpinnerP3.setPreferredSize(new Dimension(60, 24));
			jSpinnerP3.addChangeListener(this);
			jSpinnerP3.setEditor(new JSpinner.NumberEditor(jSpinnerP3, "0.00"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerP3.getEditor()).getTextField().setEditable(true);	
			jPanelP3.add(jLabelP3, BorderLayout.WEST);
			jPanelP3.add(jSpinnerP3, BorderLayout.CENTER);		
		}
		return jPanelP3;
	}
	
	/**
	 * This method initializes jJPanelP4
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelP4() {
		if (jPanelP4 == null) {
			jPanelP4 = new JPanel();
			jPanelP4.setLayout(new BorderLayout());
			jLabelP4 = new JLabel("P4: ");
			jLabelP4.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelP4.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.01); // init, min, max, step
			jSpinnerP4 = new JSpinner(sModel);
			jSpinnerP4.setPreferredSize(new Dimension(60, 24));
			jSpinnerP4.addChangeListener(this);
			jSpinnerP4.setEditor(new JSpinner.NumberEditor(jSpinnerP4, "0.00"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerP4.getEditor()).getTextField().setEditable(true);	
			jPanelP4.add(jLabelP4, BorderLayout.WEST);
			jPanelP4.add(jSpinnerP4, BorderLayout.CENTER);
		}
		return jPanelP4;
	}
	/**
	 * This method initializes jJPanelP5
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelP5() {
		if (jPanelP5 == null) {
			jPanelP5 = new JPanel();
			jPanelP5.setLayout(new BorderLayout());
			jLabelP5 = new JLabel("P5: ");
			jLabelP5.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelP5.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.01); // init, min, max, step
			jSpinnerP5 = new JSpinner(sModel);
			jSpinnerP5.setPreferredSize(new Dimension(60, 24));
			jSpinnerP5.addChangeListener(this);
			jSpinnerP5.setEditor(new JSpinner.NumberEditor(jSpinnerP5, "0.00"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerP5.getEditor()).getTextField().setEditable(true);	
			jPanelP5.add(jLabelP5, BorderLayout.WEST);
			jPanelP5.add(jSpinnerP5, BorderLayout.CENTER);
		}
		return jPanelP5;
	}
	/**
	 * This method initializes jJPanelP6
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelP6() {
		if (jPanelP6 == null) {
			jPanelP6 = new JPanel();
			jPanelP6.setLayout(new BorderLayout());
			jLabelP6 = new JLabel("P6: ");
			jLabelP6.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelP6.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.01); // init, min, max, step
			jSpinnerP6 = new JSpinner(sModel);
			jSpinnerP6.setPreferredSize(new Dimension(60, 24));
			jSpinnerP6.addChangeListener(this);
			jSpinnerP6.setEditor(new JSpinner.NumberEditor(jSpinnerP6, "0.00"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerP6.getEditor()).getTextField().setEditable(true);	
			jPanelP6.add(jLabelP6, BorderLayout.WEST);
			jPanelP6.add(jSpinnerP6, BorderLayout.CENTER);
		}
		return jPanelP6;
	}
	/**
	 * This method initializes jJPanelP7
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelP7() {
		if (jPanelP7 == null) {
			jPanelP7 = new JPanel();
			jPanelP7.setLayout(new BorderLayout());
			jLabelP7 = new JLabel("P7: ");
			jLabelP7.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelP7.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.01); // init, min, max, step
			jSpinnerP7 = new JSpinner(sModel);
			jSpinnerP7.setPreferredSize(new Dimension(60, 24));
			jSpinnerP7.addChangeListener(this);
			jSpinnerP7.setEditor(new JSpinner.NumberEditor(jSpinnerP7, "0.00"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerP7.getEditor()).getTextField().setEditable(true);	
			jPanelP7.add(jLabelP7, BorderLayout.WEST);
			jPanelP7.add(jSpinnerP7, BorderLayout.CENTER);
		}
		return jPanelP7;
	}
	/**
	 * This method initializes jJPanelP8
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelP8() {
		if (jPanelP8 == null) {
			jPanelP8 = new JPanel();
			jPanelP8.setLayout(new BorderLayout());
			jLabelP8 = new JLabel("P8: ");
			jLabelP8.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelP8.setPreferredSize(new Dimension(70, 22));
			SpinnerModel sModel = new SpinnerNumberModel(0.5, 0.0, 1.0, 0.01); // init, min, max, step
			jSpinnerP8 = new JSpinner(sModel);
			jSpinnerP8.setPreferredSize(new Dimension(60, 24));
			jSpinnerP8.addChangeListener(this);
			jSpinnerP8.setEditor(new JSpinner.NumberEditor(jSpinnerP8, "0.00"));  //"#0.00
			((JSpinner.NumberEditor) jSpinnerP8.getEditor()).getTextField().setEditable(true);	
			jPanelP8.add(jLabelP8, BorderLayout.WEST);
			jPanelP8.add(jSpinnerP8, BorderLayout.CENTER);
		}
		return jPanelP8;
	}

	/**
	 * This method initializes jPanelOpzions
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSettings() {
		if (jPanelSettings == null) {
			jPanelSettings = new JPanel();
			jPanelSettings.setLayout(new BoxLayout(jPanelSettings, BoxLayout.Y_AXIS));
			jPanelSettings.setBorder(new TitledBorder(null, "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
			jPanelSettings.add(getJPanelItMax());
			jPanelSettings.add(new JSeparator(SwingConstants.HORIZONTAL));
			jPanelSettings.add(getJPanelP1());
			jPanelSettings.add(getJPanelP2());
			jPanelSettings.add(getJPanelP3());
			jPanelSettings.add(getJPanelP4());
			jPanelSettings.add(getJPanelP5());
			jPanelSettings.add(getJPanelP6());
			jPanelSettings.add(getJPanelP7());
			jPanelSettings.add(getJPanelP8());
			//jPanelSpinners.addSeparator();
		}
		return jPanelSettings;
	}

	// --------------------------------------------------------------------------------------------
	
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

	private  void enableAllSpinners(){
		for (int s = 1; s <= 8; s++){
			enableSpinner(s);
		}
	}
	
	private  void disableAllSpinners(){
		for (int s = 1; s <= 8; s++){
			disableSpinner(s);
		}
	}
	private void enableSpinner(int s) {
		if (s == 1){jPanelP1.setEnabled(true);
					jLabelP1.setEnabled(true);
					jSpinnerP1.setEnabled(true);}
		if (s == 2){jPanelP2.setEnabled(true);
					jLabelP2.setEnabled(true);
					jSpinnerP2.setEnabled(true);}
		if (s == 3){jPanelP3.setEnabled(true);
					jLabelP3.setEnabled(true);
					jSpinnerP3.setEnabled(true);}
		if (s == 4){jPanelP4.setEnabled(true);
					jLabelP4.setEnabled(true);
					jSpinnerP4.setEnabled(true);}
		if (s == 5){jPanelP5.setEnabled(true);
					jLabelP5.setEnabled(true);
					jSpinnerP5.setEnabled(true);}
		if (s == 6){jPanelP6.setEnabled(true);
					jLabelP6.setEnabled(true);
					jSpinnerP6.setEnabled(true);}
		if (s == 7){jPanelP7.setEnabled(true);
					jLabelP7.setEnabled(true);
					jSpinnerP7.setEnabled(true);}
		if (s == 8){jPanelP8.setEnabled(true);
					jLabelP8.setEnabled(true);
					jSpinnerP8.setEnabled(true);}
		
	}
	
	private void disableSpinner(int i) {
		if (i == 1){jPanelP1.setEnabled(false);
					jLabelP1.setEnabled(false);
					jSpinnerP1.setEnabled(false);}
		if (i == 2){jPanelP2.setEnabled(false);
					jLabelP2.setEnabled(false);
					jSpinnerP2.setEnabled(false);}
		if (i == 3){jPanelP3.setEnabled(false);
					jLabelP3.setEnabled(false);
					jSpinnerP3.setEnabled(false);}
		if (i == 4){jPanelP4.setEnabled(false);
					jLabelP4.setEnabled(false);
					jSpinnerP4.setEnabled(false);}
		if (i == 5){jPanelP5.setEnabled(false);
					jLabelP5.setEnabled(false);
					jSpinnerP5.setEnabled(false);}
		if (i == 6){jPanelP6.setEnabled(false);
					jLabelP6.setEnabled(false);
					jSpinnerP6.setEnabled(false);}
		if (i == 7){jPanelP7.setEnabled(false);
					jLabelP7.setEnabled(false);
					jSpinnerP7.setEnabled(false);}
		if (i == 8){jPanelP8.setEnabled(false);
					jLabelP8.setEnabled(false);
					jSpinnerP8.setEnabled(false);}
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		int initWidth = ((Number) jSpinnerInitWidth.getValue()).intValue();
		if (jSpinnerInitWidth == e.getSource()) {
			jSpinnerInitHeight.setValue(initWidth);
		}	
		int initHeight = ((Number) jSpinnerInitWidth.getValue()).intValue();
		int itMax      = ((Number) jSpinnerItMax.getValue()).intValue();	
		int finalWidth  = (int)Math.pow(initWidth, itMax);
		int finalHeight = (int)Math.pow(initHeight,itMax);
		if (finalWidth < 10000 && finalHeight < 10000){
			jLabelFinalSize.setText("            Width: " + finalWidth + "                      Height: " + finalHeight);
			jLabelFinalSize.setForeground(Color.BLACK);
			jLabelFinalSize.setToolTipText("Final width and height");
		} else {	
			jLabelFinalSize.setText("            Width: " + finalWidth + " !                    Height: " + finalHeight +" !");
			jLabelFinalSize.setForeground(Color.RED);
			jLabelFinalSize.setToolTipText("Values seem to be far too high");
		}

		enableAllSpinners();
		if (itMax < 8) this.disableSpinner(8);
		if (itMax < 7) this.disableSpinner(7);
		if (itMax < 6) this.disableSpinner(6);
		if (itMax < 5) this.disableSpinner(5);
		if (itMax < 4) this.disableSpinner(4);
		if (itMax < 3) this.disableSpinner(3);
		if (itMax < 2) this.disableSpinner(2);
			
		this.updateParameterBlock();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}
}// END
