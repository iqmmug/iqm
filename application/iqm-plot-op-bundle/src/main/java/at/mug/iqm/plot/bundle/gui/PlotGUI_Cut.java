package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_Cut.java
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



import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpCutDescriptor;

/**
 * @author Ahammer
 * @since  2012 11
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class PlotGUI_Cut extends AbstractPlotOperatorGUI implements
		ChangeListener {

	/**
	 * 
	*/
	private static final long serialVersionUID = 2449971939385375939L;

	private static final Logger logger = LogManager.getLogger(PlotGUI_Cut.class);

	private ParameterBlockIQM pb;

	private JPanel   jPanelStart   = null;
	private JLabel   jLabelStart   = null;
	private JSpinner jSpinnerStart = null;
	private JPanel   jPanelEnd     = null;
	private JLabel   jLabelEnd     = null;
	private JSpinner jSpinnerEnd   = null;
	
	private JPanel   jPanelOptions = null;

	public PlotGUI_Cut() {
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpCutDescriptor().getName());
		this.initialize();
		this.setTitle("Plot Cut");
		getOpGUIContent().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		this.getOpGUIContent().add(getJPanelOptions());


		this.pack();
	}
	
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();

		jSpinnerStart.removeChangeListener(this);
		jSpinnerEnd.removeChangeListener(this);

		jSpinnerStart.setValue(pb.getIntParameter("start"));
		jSpinnerEnd.setValue(pb.getIntParameter("end"));

		jSpinnerStart.addChangeListener(this);
		jSpinnerEnd.addChangeListener(this);

	}

	@Override
	public void updateParameterBlock() {
		pb.setParameter("start", ((Number) jSpinnerStart.getValue()).intValue());
		pb.setParameter("end",   ((Number) jSpinnerEnd.getValue()).intValue());
	}


	/**
	 * This method initializes jJPanelStart
	 * 
	 * @return javax.swing.JPanel
	 */
	@SuppressWarnings("unused")
	private JPanel getJPanelStart() {
		if (jPanelStart == null) {
			jPanelStart = new JPanel();
			jPanelStart.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jLabelStart = new JLabel("Start: ");
			jLabelStart.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerStart = new JSpinner(sModel);
			//jSpinnerStart.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerStart.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			JFormattedTextField jFormattedTextFieldStart = ftf;
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
			//jSpinnerStart.removeChangeListener(this);
			jSpinnerStart.setValue(2);	
			jSpinnerStart.setValue(1);	
			jSpinnerStart.addChangeListener(this);
			jPanelStart.add(jLabelStart);
			jPanelStart.add(jSpinnerStart);
		}
		return jPanelStart;
	}

	/**
	 * This method initializes jJPanelEnd
	 * 
	 * @return javax.swing.JPanel
	 */
	@SuppressWarnings("unused")
	private JPanel getJPanelEnd() {
		if (jPanelEnd == null) {
			jPanelEnd = new JPanel();
			jPanelEnd.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jLabelEnd = new JLabel("End: ");
			jLabelEnd.setHorizontalAlignment(SwingConstants.RIGHT);
			SpinnerModel sModel = new SpinnerNumberModel(2, 2, Integer.MAX_VALUE, 1); // init, min, max, step
			jSpinnerEnd = new JSpinner(sModel);
			jSpinnerEnd.addChangeListener(this);
			JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerEnd.getEditor();
			JFormattedTextField ftf = defEditor.getTextField();
			ftf.setColumns(5);
			JFormattedTextField jFormattedTextFieldEnd = ftf;
			InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
			DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
			decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")// ;
			jPanelEnd.add(jLabelEnd);
			jPanelEnd.add(jSpinnerEnd);
		}
		return jPanelEnd;
	}
	
	
	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOptions() {
		if (jPanelOptions == null) {
			jPanelOptions = new JPanel();
			//jPanelStartStop.setLayout(new BoxLayout(jPanelStartStop, BoxLayout.Y_AXIS));
			jPanelOptions.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelOptions.setBorder(new TitledBorder(null, "Cut out options", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
			jPanelOptions.add(getJPanelStart());
			jPanelOptions.add(getJPanelEnd());
		}
		return jPanelOptions;
	}

	@Override
	public void update() {
		logger.debug("Updating GUI...");
		
		IqmDataBox box = (IqmDataBox) this.workPackage.getSources().get(0);
		PlotModel plotModel = box.getPlotModel();
		Vector<Double> signal = new Vector<Double>(plotModel.getData());
		int size = signal.size();	
		SpinnerModel sModel = new SpinnerNumberModel(999, 1, Integer.MAX_VALUE, 1); // init, min, max, step
		jSpinnerStart.setModel(sModel);
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) jSpinnerStart.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		ftf.setColumns(5);
		InternationalFormatter intFormatter = (InternationalFormatter) ftf.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
		jSpinnerStart.setValue(1);	     // only in order to set format pattern
		jSpinnerStart.addChangeListener(this);
		
		
		
		sModel = new SpinnerNumberModel(2, 2, size, 1); // init, min, max, step
		jSpinnerEnd.removeChangeListener(this);
		jSpinnerEnd.setModel(sModel);	
		defEditor = (JSpinner.DefaultEditor) jSpinnerEnd.getEditor();
		ftf = defEditor.getTextField();
		ftf.setColumns(5);
		intFormatter = (InternationalFormatter) ftf.getFormatter();
		decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
		jSpinnerEnd.setValue(size);      // only in order to set format pattern and proper value
		jSpinnerEnd.addChangeListener(this);
		
		this.updateParameterBlock();
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		int start = ((Number) jSpinnerStart.getValue()).intValue();
		int end = ((Number) jSpinnerEnd.getValue()).intValue();

		if (jSpinnerStart == e.getSource()) {
			if (start >= end) {
				jSpinnerStart.setValue(end - 1);
				// start = end-1;
			}

		}
		if (jSpinnerEnd == e.getSource()) {
			if (end <= start) {
				jSpinnerEnd.setValue(start + 1);
				// end = start +1;
			}
		}

		this.updateParameterBlock();

	}


	/**
	 * Unused in this GUI.
	 * 
	 * @param arg0
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

}
