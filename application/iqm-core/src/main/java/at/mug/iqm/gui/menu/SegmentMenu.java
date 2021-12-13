package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: SegmentMenu.java
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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.workflow.ExecutionProxy;
import at.mug.iqm.img.bundle.descriptors.IqmOpColDeconDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpDirLocalDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpDistMapDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpKMeansDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpKMeansFuzzyDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpRGBRelativeDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpROISegmentDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpRegGrowDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpStatRegMergeDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpTempMatchDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpThresholdDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpWatershedDescriptor;

/**
 * This is the base class for the segment menu in IQM.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 */
public class SegmentMenu extends DeactivatableMenu implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3188458597762637574L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(SegmentMenu.class);

	// class variable declaration
	private OperatorMenuItem colDeconMenuItem;
	private OperatorMenuItem distMapMenuItem;
	private OperatorMenuItem regGrowMenuItem;
	private OperatorMenuItem kMeansMenuItem;
	private OperatorMenuItem kMeansFuzzyMenuItem;
	private OperatorMenuItem statRegMergeMenuItem;
	private OperatorMenuItem thresholdMenuItem;
	private OperatorMenuItem dirLocalMenuItem;
	private OperatorMenuItem tempMatchMenuItem;
	private OperatorMenuItem rgbRelativeMenuItem;
	private OperatorMenuItem roiSegmentMenuItem;
	private OperatorMenuItem watershedMenuItem;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped SegmentMenu.
	 */
	public SegmentMenu() {
		logger.debug("Generating new instance of 'segment' menu.");

		// initialize the variables
		this.colDeconMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.distMapMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.regGrowMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.kMeansMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.kMeansFuzzyMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.statRegMergeMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.thresholdMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.dirLocalMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.tempMatchMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.rgbRelativeMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.roiSegmentMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.watershedMenuItem = new OperatorMenuItem(OperatorType.IMAGE);

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Menu 'segment' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items to 'segment' menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.segment.text"));

		// assemble: add created elements to the JMenu
		this.add(this.createColDeconMenuItem());
		this.add(this.createDistMapMenuItem());
		this.add(this.createRegGrowMenuItem());
		this.add(this.createKMeansMenuItem());
		this.add(this.createKMeansFuzzyMenuItem());
		this.add(this.createStatRegMergeMenuItem());
		this.add(this.createDirLocalMenuItem());
		this.add(this.createThresholdMenuItem());
		this.add(this.createTempMatchMenuItem());
		this.add(this.createRGBRelativeMenuItem());
		this.add(this.createROISegmentMenuItem());
		this.add(this.createWatershedMenuItem());
	}

	/**
	 * This method initializes colDeconMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createColDeconMenuItem() {
		this.colDeconMenuItem.setText(I18N
				.getGUILabelText("menu.segment.colDecon.text"));
		this.colDeconMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.colDecon.ttp"));
		this.colDeconMenuItem.addActionListener(this);
		this.colDeconMenuItem.setActionCommand("coldecon");
		return this.colDeconMenuItem;
	}

	/**
	 * This method initializes distMapMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createDistMapMenuItem() {
		this.distMapMenuItem.setText(I18N
				.getGUILabelText("menu.segment.distMap.text"));
		this.distMapMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.distMap.ttp"));
		this.distMapMenuItem.addActionListener(this);
		this.distMapMenuItem.setActionCommand("distmap");
		return this.distMapMenuItem;
	}

	/**
	 * This method initializes jRegGrowMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createRegGrowMenuItem() {
		this.regGrowMenuItem.setText(I18N
				.getGUILabelText("menu.segment.regGrow.text"));
		this.regGrowMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.regGrow.ttp"));
		this.regGrowMenuItem.addActionListener(this);
		this.regGrowMenuItem.setActionCommand("reggrow");
		return this.regGrowMenuItem;
	}

	/**
	 * This method initializes kMeansMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createKMeansMenuItem() {
		this.kMeansMenuItem.setText(I18N
				.getGUILabelText("menu.segment.kMeans.text"));
		this.kMeansMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.kMeans.ttp"));
		this.kMeansMenuItem.addActionListener(this);
		this.kMeansMenuItem.setActionCommand("kmeans");
		return this.kMeansMenuItem;
	}

	/**
	 * This method initializes kMeansFuzzyMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createKMeansFuzzyMenuItem() {
		this.kMeansFuzzyMenuItem.setText(I18N
				.getGUILabelText("menu.segment.kMeansFuzzy.text"));
		this.kMeansFuzzyMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.kMeansFuzzy.ttp"));
		this.kMeansFuzzyMenuItem.addActionListener(this);
		this.kMeansFuzzyMenuItem.setActionCommand("kmeansfuzzy");
		return this.kMeansFuzzyMenuItem;
	}

	/**
	 * This method initializesStatRegMergeMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createStatRegMergeMenuItem() {
		this.statRegMergeMenuItem.setText(I18N
				.getGUILabelText("menu.segment.statRegMerge.text"));
		this.statRegMergeMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.statRegMerge.ttp"));
		this.statRegMergeMenuItem.addActionListener(this);
		this.statRegMergeMenuItem.setActionCommand("statregmerge");
		return this.statRegMergeMenuItem;
	}

	/**
	 * This method initializes dirlocalMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createDirLocalMenuItem() {
		this.dirLocalMenuItem.setText(I18N
				.getGUILabelText("menu.segment.localDirection.text"));
		this.dirLocalMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.localDirection.ttp"));
		this.dirLocalMenuItem.addActionListener(this);
		this.dirLocalMenuItem.setActionCommand("dirlocal");
		return this.dirLocalMenuItem;
	}

	/**
	 * This method initializes thresholdMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createThresholdMenuItem() {
		this.thresholdMenuItem.setText(I18N
				.getGUILabelText("menu.segment.threshold.text"));
		this.thresholdMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.threshold.ttp"));
		this.thresholdMenuItem.addActionListener(this);
		this.thresholdMenuItem.setActionCommand("threshold");
		return this.thresholdMenuItem;
	}

	/**
	 * This method initializes tempMatchMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createTempMatchMenuItem() {
		this.tempMatchMenuItem.setText(I18N
				.getGUILabelText("menu.segment.tempMatch.text"));
		this.tempMatchMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.tempMatch.ttp"));
		this.tempMatchMenuItem.addActionListener(this);
		this.tempMatchMenuItem.setActionCommand("tempmatch");
		return this.tempMatchMenuItem;
	}

	/**
	 * This method initializes jROISegmentMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createROISegmentMenuItem() {
		this.roiSegmentMenuItem.setText(I18N
				.getGUILabelText("menu.segment.roiSegment.text"));
		this.roiSegmentMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.roiSegment.ttp"));
		this.roiSegmentMenuItem.addActionListener(this);
		this.roiSegmentMenuItem.setActionCommand("roisegment");
		return this.roiSegmentMenuItem;
	}

	/**
	 * This method initializes RGBRelativeMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createRGBRelativeMenuItem() {
		this.rgbRelativeMenuItem.setText(I18N
				.getGUILabelText("menu.segment.rgbRelative.text"));
		this.rgbRelativeMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.rgbRelative.ttp"));
		this.rgbRelativeMenuItem.addActionListener(this);
		this.rgbRelativeMenuItem.setActionCommand("rgbrelative");
		return this.rgbRelativeMenuItem;
	}

	/**
	 * This method initializes WatershedMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createWatershedMenuItem() {
		this.watershedMenuItem.setText(I18N
				.getGUILabelText("menu.segment.watershed.text"));
		this.watershedMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.segment.watershed.ttp"));
		this.watershedMenuItem.addActionListener(this);
		this.watershedMenuItem.setActionCommand("watershed");
		return this.watershedMenuItem;
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

		if ("coldecon".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpColDeconDescriptor());
		}
		if ("distmap".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpDistMapDescriptor());
		}
		if ("reggrow".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpRegGrowDescriptor());
		}
		if ("kmeans".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpKMeansDescriptor());
		}
		if ("kmeansfuzzy".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpKMeansFuzzyDescriptor());
		}
		if ("statregmerge".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpStatRegMergeDescriptor());
		}
		if ("dirlocal".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpDirLocalDescriptor());
		}
		if ("threshold".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpThresholdDescriptor());
		}
		if ("tempmatch".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpTempMatchDescriptor());
		}
		if ("rgbrelative".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpRGBRelativeDescriptor());
		}
		if ("roisegment".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpROISegmentDescriptor());
		}
		if ("watershed".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpWatershedDescriptor());
		}
	}
}
