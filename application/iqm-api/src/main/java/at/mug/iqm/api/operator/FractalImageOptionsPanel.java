package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: FractalImageOptionsPanel.java
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


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import at.mug.iqm.api.I18N;

/**
 * This panel represents options for all image fractal operator GUIs.
 * 
 * @author Helmut Ahammer
 *
 */
public class FractalImageOptionsPanel extends JPanel{
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -5736831489189407734L;
	private JSpinner spinnerRegStart;
	private JCheckBox chckBoxShowPlot;
	private JCheckBox chckBoxDeleteExisitingPlot;
	private JSpinner spinnerRegEnd;
	private JLabel labelStart;
	
	public JSpinner getSpinnerRegStart() {
		return spinnerRegStart;
	}
	public JSpinner getSpinnerRegEnd() {
		return spinnerRegEnd;
	}
	public JCheckBox getChckBoxShowPlot() {
		return chckBoxShowPlot;
	}
	public JCheckBox getChckBoxDeleteExisitingPlot() {
		return chckBoxDeleteExisitingPlot;
	}
	
	/**
	 * Create the panel.
	 */
	public FractalImageOptionsPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{227, 0};
		gridBagLayout.rowHeights = new int[]{65, 56, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panelStartEnd = new JPanel();
		GridBagConstraints gbc_panelStartEnd = new GridBagConstraints();
		gbc_panelStartEnd.anchor = GridBagConstraints.NORTHWEST;
		gbc_panelStartEnd.insets = new Insets(10, 0, 5, 0);
		gbc_panelStartEnd.gridx = 0;
		gbc_panelStartEnd.gridy = 0;
		add(panelStartEnd, gbc_panelStartEnd);
		panelStartEnd.setBorder(new TitledBorder(null, I18N.getGUILabelText("fractalImageOptionsPanel.titelBorderRegression.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelStartEnd.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panelStart = new JPanel();
		panelStartEnd.add(panelStart);
		panelStart.setLayout(new FlowLayout());
		
		labelStart = new JLabel(I18N.getGUILabelText("fractalImageOptionsPanel.lblStart.text"));
		labelStart.setHorizontalAlignment(SwingConstants.RIGHT);
		panelStart.add(labelStart);
		
		spinnerRegStart = new JSpinner();
		spinnerRegStart.setPreferredSize(new Dimension(70, 22));
		spinnerRegStart.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		panelStart.add(spinnerRegStart);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panelStartEnd.add(horizontalStrut);
		
		JPanel panelEnd = new JPanel();
		panelStartEnd.add(panelEnd);
		panelEnd.setLayout(new FlowLayout());
		
		JLabel labelEnd = new JLabel(I18N.getGUILabelText("fractalImageOptionsPanel.lblEnd.text"));
		labelEnd.setHorizontalAlignment(SwingConstants.RIGHT);
		panelEnd.add(labelEnd);
		
		spinnerRegEnd = new JSpinner();
		spinnerRegEnd.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		spinnerRegEnd.setPreferredSize(new Dimension(70, 22));
		panelEnd.add(spinnerRegEnd);
		
		JPanel panelChkBxs = new JPanel();
		GridBagConstraints gbc_panelChkBxs = new GridBagConstraints();
		gbc_panelChkBxs.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelChkBxs.anchor = GridBagConstraints.NORTH;
		gbc_panelChkBxs.gridx = 0;
		gbc_panelChkBxs.gridy = 1;
		add(panelChkBxs, gbc_panelChkBxs);
		panelChkBxs.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
									I18N.getGUILabelText("fractalImageOptionsPanel.titelBorderPlotOutputoptions.text"),
							        TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0,0, 0)));
		panelChkBxs.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		chckBoxShowPlot = new JCheckBox();
		chckBoxShowPlot.setText(I18N.getGUILabelText("fractalImageOptionsPanel.chkBxShowPlot.text"));
		chckBoxShowPlot.setActionCommand("parameter");
		panelChkBxs.add(chckBoxShowPlot);
		
		chckBoxDeleteExisitingPlot = new JCheckBox();
		chckBoxDeleteExisitingPlot.setText(I18N.getGUILabelText("fractalImageOptionsPanel.chkBxDeleteExistingPlot.text"));
		chckBoxDeleteExisitingPlot.setActionCommand("parameter");
		panelChkBxs.add(chckBoxDeleteExisitingPlot);

	}
}
