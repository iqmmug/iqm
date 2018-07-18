package feat.lbp;

/*
* This file is part of IQM, hereinafter referred to as "this program".
* 
* Copyright (C) 2009 - 2014 Helmut Ahammer, Philipp Kainz
* 
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
*/

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.OperatorGUIFactory;
import at.mug.iqm.api.operator.ParameterBlockIQM;

/**
 * This class represents the graphical UI of an operator. One may either extend
 * the {@link AbstractImageOperatorGUI} or the {@link AbstractPlotOperatorGUI}
 * in order to create a GUI.
 * 
 * @author Philipp Kainz
 * 
 */
public class FeatLBPGUI extends AbstractImageOperatorGUI implements
		ChangeListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -6684951054122832268L;

	/**
	 * A private parameter block, holding the current settings for the operator.
	 */
	private ParameterBlockIQM pb;
	private JSpinner spnrNeighbours;
	private JSpinner spnrRadius;
	private JCheckBox chckbxSmooth;
	private JSpinner spnrKernelSize;
	private JSpinner spnrCells;

	/**
	 * Mandatory empty constructor for usage with the {@link OperatorGUIFactory}
	 * .
	 */
	public FeatLBPGUI() {
		// within this method all elements for this GUI must be initialized
		logger.debug("Now initializing...");

		this.setOpName(new FeatLBPDescriptor().getName());

		this.initialize();

		this.setTitle(I18N.getMessage("frame.title"));
		getOpGUIContent().setLayout(new BorderLayout(0, 0));

		Box vBoxPartitioning = Box.createVerticalBox();
		vBoxPartitioning.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), I18N.getMessage("partitioning.border.title"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getOpGUIContent().add(vBoxPartitioning, BorderLayout.NORTH);

		JPanel pnlCells = new JPanel();
		vBoxPartitioning.add(pnlCells);

		JLabel lblNCells = new JLabel(I18N.getMessage("lblCells.text"));
		lblNCells
				.setToolTipText(I18N.getMessage("lblCells.ttp"));

		pnlCells.add(lblNCells);

		spnrCells = new JSpinner();
		spnrCells.addChangeListener(this);
		spnrCells.setModel(new SpinnerNumberModel(1, 1, 100000, 1));
		pnlCells.add(spnrCells);

		Box vBoxCellSettings = Box.createVerticalBox();
		vBoxCellSettings.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), I18N.getMessage("lbp.border.title"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getOpGUIContent().add(vBoxCellSettings);

		JPanel pnlPR = new JPanel();
		vBoxCellSettings.add(pnlPR);

		JPanel pnlNeighbours = new JPanel();
		pnlPR.add(pnlNeighbours);
		pnlNeighbours.setBorder(new EmptyBorder(0, 0, 5, 0));
		pnlNeighbours.setLayout(new BorderLayout(5, 0));

		// use the I18N class in your package to internationalize your GUI
		// description elements get the messages from
		// /src/main/resources/.../messages.properties
		JLabel lblNeighbours = new JLabel(I18N.getMessage("lblNeighbours.text"));
		pnlNeighbours.add(lblNeighbours, BorderLayout.WEST);

		spnrNeighbours = new JSpinner();
		spnrNeighbours.addChangeListener(this);
		lblNeighbours.setLabelFor(spnrNeighbours);
		spnrNeighbours.setModel(new SpinnerNumberModel(8, 4, 100, 1));
		pnlNeighbours.add(spnrNeighbours);

		JPanel pnlRadius = new JPanel();
		pnlPR.add(pnlRadius);
		pnlRadius.setBorder(new EmptyBorder(0, 0, 5, 0));
		pnlRadius.setLayout(new BorderLayout(5, 0));

		JLabel lblRadius = new JLabel("Radius");
		pnlRadius.add(lblRadius, BorderLayout.WEST);

		spnrRadius = new JSpinner();
		spnrRadius.addChangeListener(this);
		spnrRadius.setModel(new SpinnerNumberModel(1.0d, 1.0d, 6000.0d, 0.25d));
		pnlRadius.add(spnrRadius);

		JPanel pnlSmooth = new JPanel();
		vBoxCellSettings.add(pnlSmooth);

		chckbxSmooth = new JCheckBox(I18N.getMessage("chckbxSmooth.text"));
		chckbxSmooth.addChangeListener(this);
		chckbxSmooth
				.setToolTipText(I18N.getMessage("chckbxSmooth.ttp"));
		pnlSmooth.add(chckbxSmooth);

		JLabel lblKernelSize = new JLabel(I18N.getMessage("lblKernelSize.text"));
		pnlSmooth.add(lblKernelSize);

		spnrKernelSize = new JSpinner();
		spnrKernelSize.addChangeListener(this);
		spnrKernelSize.setModel(new SpinnerNumberModel(3, 3, 101, 2));
		pnlSmooth.add(spnrKernelSize);

		this.pack();
	}

	@Override
	public void setParameterValuesToGUI() {
		// the first statement here MUST be the following line
		this.pb = this.workPackage.getParameters();

		// remove the listeners, if any before setting the spinner
		spnrNeighbours.removeChangeListener(this);
		spnrNeighbours.setValue(this.pb.getIntParameter("neighbours"));
		spnrNeighbours.addChangeListener(this);

		spnrRadius.removeChangeListener(this);
		spnrRadius.setValue((double) this.pb.getFloatParameter("radius"));
		spnrRadius.addChangeListener(this);

		spnrKernelSize.removeChangeListener(this);
		spnrKernelSize.setValue(this.pb.getIntParameter("kernelsize"));
		spnrKernelSize.addChangeListener(this);

		spnrCells.removeChangeListener(this);
		spnrCells.setValue(this.pb.getIntParameter("cells"));
		spnrCells.addChangeListener(this);

		chckbxSmooth.setSelected(this.pb.getBooleanParameter("smooth"));
	}

	@Override
	public void updateParameterBlock() {
		// updates the parameter block according to the current GUI element
		// settings
		this.pb.setParameter("neighbours",
				((Number) spnrNeighbours.getValue()).intValue());

		this.pb.setParameter("radius",
				((Number) spnrRadius.getValue()).floatValue());

		this.pb.setParameter("kernelsize",
				((Number) spnrKernelSize.getValue()).intValue());

		this.pb.setParameter("cells",
				((Number) spnrCells.getValue()).intValue());

		this.pb.setParameter("smooth", chckbxSmooth.isSelected());
	}

	@Override
	public void update() {
		// this method alters GUI elements according to special requirements
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// use this method for common action events
		// you will have to register the ActionListener using
		// myComponent.addActionListener(this)
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// update the parameter block each time the spinner changes
		this.updateParameterBlock();
		
		if (this.isAutoPreviewSelected())
			this.showPreview();
	}
}
