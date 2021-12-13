package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_FracSurrogate.java
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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
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

import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.commons.util.plot.Surrogate;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracSurrogateDescriptor;

/**
 * @author Ahammer
 * @since  2012 11
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class PlotGUI_FracSurrogate extends AbstractPlotOperatorGUI implements
		ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2548053727607836322L;

	private static final Logger logger = LogManager.getLogger(PlotGUI_FracSurrogate.class);

	private ParameterBlockIQM pb;

	private JPanel       jPanelMethod    = null;
	private ButtonGroup  buttGroupMethod = null;
	private JRadioButton buttShuffle     = null;
	private JRadioButton buttGaussian    = null;
	private JRadioButton buttRandomPhase = null;
	private JRadioButton buttAAFT        = null;

	private JPanel   jPanelNumSignals = null;
	private JLabel   jLabelNumSignals = null;
	private JSpinner jSpinnerNumSignals = null;

	public PlotGUI_FracSurrogate() {
		logger.debug("Now initializing...");
		
		this.setOpName(new PlotOpFracSurrogateDescriptor().getName());	
		this.initialize();
		this.setTitle("Fractal Surrogate Plot");
		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(getJPanelMethod(),      getGBC_Method());
		this.getOpGUIContent().add(getJPanelNumSignals(),  getGBC_NumSignals());

		this.pack();
	}

	private GridBagConstraints getGBC_Method() {
		GridBagConstraints gbc_Method = new GridBagConstraints();
		gbc_Method.gridx = 0;
		gbc_Method.gridy = 0;
		gbc_Method.insets = new Insets(5, 0, 0, 0); // top left bottom right
		gbc_Method.fill = GridBagConstraints.BOTH;
		return gbc_Method;
	}

	private GridBagConstraints getGBC_NumSignals() {
		GridBagConstraints gbc_NumSignals = new GridBagConstraints();
		gbc_NumSignals.gridx = 0;
		gbc_NumSignals.gridy = 1;
		gbc_NumSignals.insets = new Insets(5, 0, 5, 0); // top left bottom right
		gbc_NumSignals.fill = GridBagConstraints.BOTH;
		return gbc_NumSignals;
	}
	//-----------------------------------------------------------------------------------------------------------
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		switch (pb.getIntParameter("method")) {
		case Surrogate.SURROGATE_SHUFFLE:
			buttShuffle.setSelected(true);
			break;

		case Surrogate.SURROGATE_GAUSSIAN:
			buttGaussian.setSelected(true);
			break;

		case Surrogate.SURROGATE_RANDOMPHASE:
			buttRandomPhase.setSelected(true);
			break;

		case Surrogate.SURROGATE_AAFT:
			buttAAFT.setSelected(true);
			break;
		}
	}

	@Override
	public void updateParameterBlock() {
		if (this.buttShuffle.isSelected())
			pb.setParameter("method", Surrogate.SURROGATE_SHUFFLE);
		if (this.buttGaussian.isSelected())
			pb.setParameter("method", Surrogate.SURROGATE_GAUSSIAN);
		if (this.buttRandomPhase.isSelected())
			pb.setParameter("method", Surrogate.SURROGATE_RANDOMPHASE);
		if (this.buttAAFT.isSelected())
			pb.setParameter("method", Surrogate.SURROGATE_AAFT);
		
		// add the number of signals to the work package
		this.workPackage.setIterations(((Number)jSpinnerNumSignals.getValue()).intValue());
	}
    //-------------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: Shuffle
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonShuffle() {
		if (buttShuffle == null) {
			buttShuffle = new JRadioButton();
			buttShuffle.setText("Shuffle");
			buttShuffle.setToolTipText("calculates a randomly shuffled image");
			buttShuffle.addActionListener(this);
			buttShuffle.setActionCommand("parameter");
		}
		return buttShuffle;
	}

	/**
	 * This method initializes the Option: Gaussian
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonGaussian() {
		if (buttGaussian == null) {
			buttGaussian = new JRadioButton();
			buttGaussian.setText("Gaussian");
			buttGaussian.setToolTipText("calculates a Gaussian image with identical mean and standardeviation as original image");
			buttGaussian.addActionListener(this);
			buttGaussian.setActionCommand("parameter");
		}
		return buttGaussian;
	}

	/**
	 * This method initializes the Option: RandomPhase
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonRandomPhase() {
		if (buttRandomPhase == null) {
			buttRandomPhase = new JRadioButton();
			buttRandomPhase.setText("Random Phase");
			buttRandomPhase.setToolTipText("calculates a phase randomized image using DFT");
			buttRandomPhase.addActionListener(this);
			buttRandomPhase.setActionCommand("parameter");
			buttRandomPhase.setEnabled(true);
			buttRandomPhase.setSelected(true);
		}
		return buttRandomPhase;
	}

	/**
	 * This method initializes the Option: AAFT
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonAAFT() {
		if (buttAAFT == null) {
			buttAAFT = new JRadioButton();
			buttAAFT.setText("AAFT");
			buttAAFT.setToolTipText("calculates an image using the AAFT method");
			buttAAFT.addActionListener(this);
			buttAAFT.setActionCommand("parameter");
			buttAAFT.setEnabled(true);
		}
		return buttAAFT;
	}


	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethod.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethod.add(getJRadioButtonShuffle());
		jPanelMethod.add(getJRadioButtonGaussian());
		jPanelMethod.add(getJRadioButtonRandomPhase());
		jPanelMethod.add(getJRadioButtonAAFT());

		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttShuffle);
		buttGroupMethod.add(buttGaussian);
		buttGroupMethod.add(buttRandomPhase);
		buttGroupMethod.add(buttAAFT);
	}

	/**
	 * This method initializes jJPanelNumSignals
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelNumSignals() {
		if (jPanelNumSignals == null) {
			jPanelNumSignals = new JPanel();
			//jPanelNumSignals.setLayout(new BoxLayout(jPanelNumSignals, BoxLayout.Y_AXIS));
			jPanelNumSignals.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			jPanelNumSignals.setBorder(new TitledBorder(null, "Number of signals", TitledBorder.LEADING, TitledBorder.TOP, null, null));				
			jLabelNumSignals = new JLabel("#: ");
			jLabelNumSignals.setHorizontalAlignment(SwingConstants.RIGHT);
			int init = 1;
			SpinnerModel sModel = new SpinnerNumberModel(init, 1,Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerNumSignals = new JSpinner(sModel);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerNumSignals.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")/ ;
			jSpinnerNumSignals.setValue(1); // only in order to set format pattern
			jSpinnerNumSignals.setValue(init); // only in order to set format pattern
			jSpinnerNumSignals.addChangeListener(this);
			jPanelNumSignals.add(jLabelNumSignals);
			jPanelNumSignals.add(jSpinnerNumSignals);
		}
		return jPanelNumSignals;
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void update() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (e.getSource() == buttShuffle) {
			}
			if (e.getSource() == buttGaussian) {
			}
			if (e.getSource() == buttRandomPhase) {
			}
			if (e.getSource() == buttAAFT) {
			}

			this.updateParameterBlock();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateParameterBlock();
	}


}
