package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ProcessMenu.java
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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.workflow.ExecutionProxy;
import at.mug.iqm.img.bundle.descriptors.IqmOpAffineDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpAlignDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpBUnwarpJDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpBorderDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCalcImageDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCalcValueDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCoGRegDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpConvertDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCropDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpImgStabilizerDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpInvertDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpMorphDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpResizeDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpTurboRegDescriptor;

/**
 * This is the base class for the process menu in IQM.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 */
public class ProcessMenu extends DeactivatableMenu implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2572007859398471886L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(ProcessMenu.class);

	// class variable declaration
	private OperatorMenuItem alignMenuItem;
	private OperatorMenuItem affineMenuItem;
	private OperatorMenuItem invertMenuItem;
	private OperatorMenuItem calcImageMenuItem;
	private OperatorMenuItem calcValueMenuItem;
	private OperatorMenuItem convertMenuItem;
	private OperatorMenuItem morphMenuItem;
	private OperatorMenuItem resizeMenuItem;
	private OperatorMenuItem cropMenuItem;
	private OperatorMenuItem borderMenuItem;
	private OperatorMenuItem coGRegMenuItem;
	private OperatorMenuItem turboRegMenuItem;
	private OperatorMenuItem bUnwarpJMenuItem;
	private OperatorMenuItem imgStabilizerMenuItem;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped ProcessMenu.
	 */
	public ProcessMenu() {
		logger.debug("Generating new instance of 'process' menu.");

		// initialize the variables
		this.alignMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.affineMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.invertMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.calcImageMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.calcValueMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.convertMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.morphMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.resizeMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.cropMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.borderMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.coGRegMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.turboRegMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.bUnwarpJMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.imgStabilizerMenuItem = new OperatorMenuItem(OperatorType.IMAGE);

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Menu 'process' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items to 'process' menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.process.text"));

		// assemble: add created elements to the JMenu
		this.add(this.createAlignMenuItem());
		this.add(this.createResizeMenuItem());
		this.add(this.createCropMenuItem());
		this.add(this.createAffineMenuItem());
		this.addSeparator();
		this.add(this.createCalcImageMenuItem());
		this.add(this.createCalcValueMenuItem());
		this.addSeparator();
		this.add(this.createInvertMenuItem());
		this.add(this.createConvertMenuItem());
		this.addSeparator();
		this.add(this.createMorphMenuItem());
		this.add(this.createBorderMenuItem());
		this.addSeparator();
		this.add(this.createCoGRegMenuItem());
		this.add(this.createTurboRegMenuItem());
		this.add(this.createBUnwarpJMenuItem());
		this.add(this.createImgStabilizerMenuItem());
	}

	/**
	 * This method initializes alignMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createAlignMenuItem() {
		this.alignMenuItem.setText(I18N
				.getGUILabelText("menu.process.align.text"));
		this.alignMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.align.ttp"));
		this.alignMenuItem.addActionListener(this);
		this.alignMenuItem.setActionCommand("align");
		return this.alignMenuItem;
	}

	/**
	 * This method initializes affineMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createAffineMenuItem() {
		this.affineMenuItem.setText(I18N
				.getGUILabelText("menu.process.affineTransformation.text"));
		this.affineMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.affineTransformation.ttp"));
		this.affineMenuItem.addActionListener(this);
		this.affineMenuItem.setActionCommand("affine");
		return this.affineMenuItem;
	}

	/**
	 * This method initializes invertMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createInvertMenuItem() {
		this.invertMenuItem.setText(I18N
				.getGUILabelText("menu.process.invert.text"));
		this.invertMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.invert.ttp"));
		this.invertMenuItem.addActionListener(this);
		this.invertMenuItem.setActionCommand("invert");
		return this.invertMenuItem;
	}

	/**
	 * This method initializes calcImageMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createCalcImageMenuItem() {
		this.calcImageMenuItem.setText(I18N
				.getGUILabelText("menu.process.calcImage.text"));
		this.calcImageMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.calcImage.ttp"));
		this.calcImageMenuItem.addActionListener(this);
		this.calcImageMenuItem.setActionCommand("calcimage");
		return this.calcImageMenuItem;
	}

	/**
	 * This method initializes calcValueMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createCalcValueMenuItem() {
		this.calcValueMenuItem.setText(I18N
				.getGUILabelText("menu.process.calcValue.text"));
		this.calcValueMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.calcValue.ttp"));
		this.calcValueMenuItem.addActionListener(this);
		this.calcValueMenuItem.setActionCommand("calcvalue");
		return this.calcValueMenuItem;
	}

	/**
	 * This method initializes convertMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createConvertMenuItem() {
		this.convertMenuItem.setText(I18N
				.getGUILabelText("menu.process.convert.text"));
		this.convertMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.convert.ttp"));
		this.convertMenuItem.addActionListener(this);
		this.convertMenuItem.setActionCommand("convert");
		return this.convertMenuItem;
	}

	/**
	 * This method initializes morphMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createMorphMenuItem() {
		this.morphMenuItem.setText(I18N
				.getGUILabelText("menu.process.morph.text"));
		this.morphMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.morph.ttp"));
		this.morphMenuItem.addActionListener(this);
		this.morphMenuItem.setActionCommand("morph");
		return this.morphMenuItem;
	}

	/**
	 * This method initializes resizeMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createResizeMenuItem() {
		this.resizeMenuItem.setText(I18N
				.getGUILabelText("menu.process.resize.text"));
		this.resizeMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.resize.ttp"));
		this.resizeMenuItem.addActionListener(this);
		this.resizeMenuItem.setActionCommand("resize");
		return this.resizeMenuItem;
	}

	/**
	 * This method initializes cropMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createCropMenuItem() {
		this.cropMenuItem.setText(I18N
				.getGUILabelText("menu.process.crop.text"));
		this.cropMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.crop.ttp"));
		this.cropMenuItem.addActionListener(this);
		this.cropMenuItem.setActionCommand("crop");
		return this.cropMenuItem;
	}

	/**
	 * This method initializes borderMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createBorderMenuItem() {
		this.borderMenuItem.setText(I18N
				.getGUILabelText("menu.process.border.text"));
		this.borderMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.border.ttp"));
		this.borderMenuItem.addActionListener(this);
		this.borderMenuItem.setActionCommand("border");
		return this.borderMenuItem;
	}

	/**
	 * This method initializes coGRegMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createCoGRegMenuItem() {
		this.coGRegMenuItem.setText(I18N
				.getGUILabelText("menu.process.coGReg.text"));
		this.coGRegMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.coGReg.ttp"));
		this.coGRegMenuItem.addActionListener(this);
		this.coGRegMenuItem.setActionCommand("cogreg");
		return this.coGRegMenuItem;
	}

	/**
	 * This method initializes turboRegMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createTurboRegMenuItem() {
		this.turboRegMenuItem.setText(I18N
				.getGUILabelText("menu.process.turboReg.text"));
		this.turboRegMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.turboReg.ttp"));
		this.turboRegMenuItem.addActionListener(this);
		this.turboRegMenuItem.setActionCommand("turboreg");
		return this.turboRegMenuItem;
	}

	/**
	 * This method initializes bUnwarpJMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createBUnwarpJMenuItem() {
		this.bUnwarpJMenuItem.setText(I18N
				.getGUILabelText("menu.process.bUnwarpJ.text"));
		this.bUnwarpJMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.bUnwarpJ.ttp"));
		this.bUnwarpJMenuItem.addActionListener(this);
		this.bUnwarpJMenuItem.setActionCommand("bunwarpj");
		return this.bUnwarpJMenuItem;
	}

	/**
	 * This method initializes imgStabilizerMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createImgStabilizerMenuItem() {
		this.imgStabilizerMenuItem.setText(I18N
				.getGUILabelText("menu.process.imgStabilizer.text"));
		this.imgStabilizerMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.process.imgStabilizer.ttp"));
		this.imgStabilizerMenuItem.addActionListener(this);
		this.imgStabilizerMenuItem.setActionCommand("imgstabilizer");
		return this.imgStabilizerMenuItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	/**
	 * This method sets and performs the corresponding actions to the menu
	 * items.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		logger.debug(e.getActionCommand());

		if ("align".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpAlignDescriptor());
		}
		if ("affine".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpAffineDescriptor());
		}
		if ("invert".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpInvertDescriptor());
		}
		if ("calcimage".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpCalcImageDescriptor());
		}
		if ("calcvalue".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpCalcValueDescriptor());
		}
		if ("convert".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpConvertDescriptor());
		}
		if ("morph".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpMorphDescriptor());
		}
		if ("resize".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpResizeDescriptor());
		}
		if ("crop".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpCropDescriptor());
		}
		if ("border".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpBorderDescriptor());
		}
		if ("cogreg".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpCoGRegDescriptor());
		}
		if ("turboreg".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpTurboRegDescriptor());
		}
		if ("bunwarpj".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpBUnwarpJDescriptor());
		}
		if ("imgstabilizer".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpImgStabilizerDescriptor());
		}

	}
}
