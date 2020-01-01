package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: OutputOptionsPanel.java
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
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import at.mug.iqm.api.I18N;

public class OutputOptionsPanel extends JPanel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -4101086071957256824L;
	private JCheckBox chckbxPlots;
	private JCheckBox chckbxImages;
	private JCheckBox chckbxCustoms;
	private JCheckBox chckbxTables;
	private JPanel panel;

	/**
	 * Create the panel.
	 */
	public OutputOptionsPanel() {
		setToolTipText(I18N.getGUILabelText("opGUI.outputOptions.panel.ttp"));
		setBorder(new CompoundBorder(
				new EmptyBorder(5, 5, 5, 5),
				new TitledBorder(
						UIManager.getBorder("TitledBorder.border"), // NO-I18N;
						I18N.getGUILabelText("opGUI.outputOptions.panel.title"),
						TitledBorder.LEADING, TitledBorder.TOP, null,
						new Color(0, 0, 0))));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(2);
		flowLayout.setHgap(0);
		add(panel);

		chckbxImages = new JCheckBox(
				I18N.getGUILabelText("opGUI.outputOptions.chbxImages.text"));
		panel.add(chckbxImages);
		chckbxImages.setToolTipText(I18N
				.getGUILabelText("opGUI.outputOptions.chbxImages.ttp"));

		chckbxPlots = new JCheckBox(
				I18N.getGUILabelText("opGUI.outputOptions.chbxPlots.text"));
		panel.add(chckbxPlots);
		chckbxPlots.setToolTipText(I18N
				.getGUILabelText("opGUI.outputOptions.chbxPlots.ttp"));

		chckbxTables = new JCheckBox(
				I18N.getGUILabelText("opGUI.outputOptions.chbxTables.text"));
		panel.add(chckbxTables);
		chckbxTables.setToolTipText(I18N
				.getGUILabelText("opGUI.outputOptions.chbxTables.ttp"));

		chckbxCustoms = new JCheckBox(
				I18N.getGUILabelText("opGUI.outputOptions.chbxCustoms.text"));
		panel.add(chckbxCustoms);
		chckbxCustoms.setToolTipText(I18N
				.getGUILabelText("opGUI.outputOptions.chbxCustoms.ttp"));

	}

	public JCheckBox getChckbxPlots() {
		return chckbxPlots;
	}

	public JCheckBox getChckbxImages() {
		return chckbxImages;
	}

	public JCheckBox getChckbxCustoms() {
		return chckbxCustoms;
	}

	public JCheckBox getChckbxTables() {
		return chckbxTables;
	}

	/**
	 * Initialize the output options panel for a specific {@link IOperatorGUI}.
	 * <p>
	 * This method also sets the default output options for image, plot, table
	 * and custom result computation to the {@link IWorkPackage} in the
	 * {@link IOperatorGUI}.
	 * 
	 * @param opGUI
	 *            the GUI to be initialized
	 */
	public void initialize(IOperatorGUI opGUI) {
		IWorkPackage workPackage = opGUI.getWorkPackage();

		// enable and disable corresponding output options
		// get the descriptor
		List<DataType> outputTypes = OperatorDescriptorFactory
				.createDescriptor(opGUI.getOpName()).getOutputTypes();

		if (outputTypes.contains(DataType.IMAGE)) {
			getChckbxImages().setEnabled(true);
			getChckbxImages().setSelected(true);
			workPackage.setImageComputationEnabled(true);
		} else {
			getChckbxImages().setEnabled(false);
			getChckbxImages().setSelected(false);
			workPackage.setImageComputationEnabled(false);
		}
		if (outputTypes.contains(DataType.PLOT)) {
			getChckbxPlots().setEnabled(true);
			getChckbxPlots().setSelected(true);
			workPackage.setPlotComputationEnabled(true);
		} else {
			getChckbxPlots().setEnabled(false);
			getChckbxPlots().setSelected(false);
			workPackage.setPlotComputationEnabled(false);
		}
		if (outputTypes.contains(DataType.TABLE)) {
			getChckbxTables().setEnabled(true);
			getChckbxTables().setSelected(true);
			workPackage.setTableComputationEnabled(true);
		} else {
			getChckbxTables().setEnabled(false);
			getChckbxTables().setSelected(false);
			workPackage.setTableComputationEnabled(false);
		}
		if (outputTypes.contains(DataType.CUSTOM)) {
			getChckbxCustoms().setEnabled(true);
			getChckbxCustoms().setSelected(true);
			workPackage.setCustomComputationEnabled(true);
		} else {
			getChckbxCustoms().setEnabled(false);
			getChckbxCustoms().setSelected(false);
			workPackage.setCustomComputationEnabled(false);
		}
	}
}
