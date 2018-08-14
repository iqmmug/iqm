package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_Filter.java
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
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFilterDescriptor;

/**
 * GUI for filtering signals.
 * 
 * @author Philipp Kainz, Michael Mayrhofer-Reinhartshuber
 * @since  3.2
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 * @update 2016 01 restrictions for range
 */
public class PlotGUI_Filter extends AbstractPlotOperatorGUI implements
		ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2129126866758754431L;

	private static final Logger logger = Logger.getLogger(PlotGUI_Filter.class);

	private ParameterBlockIQM pb;

	JRadioButton              rdbtnMean       = null;
	JRadioButton              rdbtnMedian     = null;
	private final ButtonGroup btnGroupMethods = new ButtonGroup();

	JSpinner spnRange = null;

	public PlotGUI_Filter() {
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpFilterDescriptor().getName());
		this.initialize();
		this.setTitle("Signal Filter");
		getOpGUIContent().setLayout(new GridBagLayout());
		//getOpGUIContent().setLayout(new BoxLayout(getOpGUIContent(), BoxLayout.Y_AXIS));
		//Box verticalBox = Box.createVerticalBox();
		//getOpGUIContent().add(verticalBox);

		JPanel pnlMethods = new JPanel();
		//pnlMethods.setLayout(new BoxLayout(pnlMethods, BoxLayout.X_AXIS));
		pnlMethods.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		pnlMethods.setBorder(new TitledBorder(null, "Method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rdbtnMean = new JRadioButton("Mean");
		rdbtnMean.setToolTipText("Mean of values in the range of (i-range/2) to (i+range/2)");
		rdbtnMean.addActionListener(this);
		rdbtnMean.setSelected(true);
		rdbtnMedian = new JRadioButton("Median");
		rdbtnMedian.setToolTipText("Median of values in the range of (i-range/2) to (i+range/2)");
		rdbtnMedian.addActionListener(this);
		btnGroupMethods.add(rdbtnMean);
		btnGroupMethods.add(rdbtnMedian);
		pnlMethods.add(rdbtnMean);
		pnlMethods.add(rdbtnMedian);

		JPanel pnlSettings = new JPanel();
		//pnlSettings.setLayout(new BoxLayout(pnlSettings, BoxLayout.X_AXIS));
		pnlSettings.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		pnlSettings.setBorder(new TitledBorder(null, "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		JLabel lblRange = new JLabel("Range: ");
		spnRange = new JSpinner();
		int init = 3;
		spnRange.setModel(new SpinnerNumberModel(init, null, null, new Integer(2)));
		spnRange.setToolTipText("Calculation range from (i-range/2) to (i+range/2)");
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) spnRange.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setColumns(5);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#");      // decimalFormat.applyPattern("#,##0.0") ;
		spnRange.setValue(1);    // only in order to set format pattern
		spnRange.setValue(init); // only in order to set format pattern
		spnRange.addChangeListener(this);
		
		pnlSettings.add(lblRange);
		pnlSettings.add(spnRange);
		
		
		GridBagConstraints gbc_1 = new GridBagConstraints();
		gbc_1.fill = GridBagConstraints.BOTH;
		gbc_1.insets = new Insets(5, 0, 0, 0);
		gbc_1.gridx = 0;
		gbc_1.gridy = 0;
		
	
		GridBagConstraints gbc_2 = new GridBagConstraints();
		gbc_2.fill = GridBagConstraints.BOTH;
		gbc_2.insets = new Insets(5, 0, 5, 0);
		gbc_2.gridx = 0;
		gbc_2.gridy = 1;
	
		getOpGUIContent().add(pnlMethods,   gbc_1);
		getOpGUIContent().add(pnlSettings,  gbc_2);
	
		this.pack();
	}

	@Override
	public void showPreview() {
		
		// check if range value is not out of size:
		IqmDataBox box = (IqmDataBox) this.workPackage.getSources().get(0);
		PlotModel plotModel = box.getPlotModel();
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
		int size = signal.size();	
		// if it is larger than signal size, set to size:
		if (pb.getIntParameter("range")>size) {
			pb.setParameter("range", size);
			spnRange.setValue(size);
			update();
		}
		if (this.getBtnPreview().isEnabled()){
			System.out
			.println("####################################### Performing PLOT preview!");
			Application.getCurrentExecutionProtocol().executePreview(
					this.workPackage, this);
		}
	}
	
	@Override
	public void update() {
		IqmDataBox box = (IqmDataBox) this.workPackage.getSources().get(0);
		PlotModel plotModel = box.getPlotModel();
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
		int size = signal.size();	
		this.pb = this.workPackage.getParameters();
	
		spnRange.setModel(new SpinnerNumberModel(pb.getIntParameter("range"), 3, size, 2));
		
		this.updateParameterBlock();
	}
	
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();
		
		if (pb.getIntParameter("method") == PlotOpFilterDescriptor.METHOD_MEAN_MAC) rdbtnMean.setSelected(true);
		if (pb.getIntParameter("method") == PlotOpFilterDescriptor.METHOD_MEDIAN)   rdbtnMedian.setSelected(true);
		
		spnRange.removeChangeListener(this);
		spnRange.setValue(pb.getIntParameter("range"));
		spnRange.addChangeListener(this);
	}

	@Override
	public void updateParameterBlock() {

		if (this.rdbtnMean.isSelected())   pb.setParameter("method", PlotOpFilterDescriptor.METHOD_MEAN_MAC);
		if (this.rdbtnMedian.isSelected()) pb.setParameter("method", PlotOpFilterDescriptor.METHOD_MEDIAN);

		// only odd numbers are allowed:
		if (((Number)spnRange.getValue()).intValue() % 2 == 0) {
			spnRange.setValue(Math.max(((Number)spnRange.getValue()).intValue()-1,3));
		}
		pb.setParameter("range", ((Number) spnRange.getValue()).intValue());
		

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");

		this.updateParameterBlock();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateParameterBlock();
	}



}
