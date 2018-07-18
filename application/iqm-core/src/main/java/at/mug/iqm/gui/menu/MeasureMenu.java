package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: MeasureMenu.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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
import java.awt.image.RenderedImage;

import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.commons.util.image.IntegralImage;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.workflow.ExecutionProxy;
import at.mug.iqm.core.workflow.Look;
import at.mug.iqm.img.bundle.descriptors.IqmOpACFDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpDFTDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpNexelScanDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpStackStatDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpStatisticsDescriptor;

/**
 * This is the base class for the measure menu in IQM.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 */
public class MeasureMenu extends DeactivatableMenu implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5333828789266605725L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(MeasureMenu.class);

	// class variable declaration
	private OperatorMenuItem statisticsMenuItem;
	private OperatorMenuItem nexelScanMenuItem;
	private OperatorMenuItem stackStatMenuItem;
	private OperatorMenuItem dftMenuItem;
	private OperatorMenuItem acfMenuItem;
	private OperatorMenuItem integralImageMenuItem;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped MeasureMenu.
	 */
	public MeasureMenu() {
		logger.debug("Generating new instance of 'measure' menu.");

		// initialize the variables
		this.statisticsMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.nexelScanMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.stackStatMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.dftMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.acfMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.integralImageMenuItem = new OperatorMenuItem(OperatorType.IMAGE);

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Menu 'measure' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items in 'measure' menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.measure.text"));

		// assemble: add created elements to the JMenu
		this.add(this.createStatisticsMenuItem());
		this.add(this.createNexelScanMenuItem());
		this.add(this.createStackStatMenuItem());
		this.add(this.createDftMenuItem());
		this.add(this.createAcfMenuItem());
		this.add(this.createIntegralImageMenuItem());
	}

	/**
	 * This method initializes integralImageMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createIntegralImageMenuItem() {
		this.integralImageMenuItem.setText(I18N
				.getGUILabelText("menu.measure.integralimage.text"));
		this.integralImageMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.measure.integralimage.ttp"));
		this.integralImageMenuItem.addActionListener(this);
		this.integralImageMenuItem.setActionCommand("integralimage");
		return this.integralImageMenuItem;
	}

	/**
	 * This method initializes statisticsMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createStatisticsMenuItem() {
		this.statisticsMenuItem.setText(I18N
				.getGUILabelText("menu.measure.statistics.text"));
		this.statisticsMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.measure.statistics.ttp"));
		this.statisticsMenuItem.addActionListener(this);
		this.statisticsMenuItem.setActionCommand("statistics");
		return this.statisticsMenuItem;
	}

	/**
	 * This method initializes nexelScanMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createNexelScanMenuItem() {
		this.nexelScanMenuItem.setText(I18N
				.getGUILabelText("menu.measure.nexelScan.text"));
		this.nexelScanMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.measure.nexelScan.ttp"));
		this.nexelScanMenuItem.addActionListener(this);
		this.nexelScanMenuItem.setActionCommand("nexelscan");
		return this.nexelScanMenuItem;
	}

	/**
	 * This method initializes stackStatMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createStackStatMenuItem() {
		this.stackStatMenuItem.setText(I18N
				.getGUILabelText("menu.measure.stackStatistics.text"));
		this.stackStatMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.measure.stackStatistics.ttp"));
		this.stackStatMenuItem.addActionListener(this);
		this.stackStatMenuItem.setActionCommand("stackstat");
		return this.stackStatMenuItem;
	}

	/**
	 * This method initializes dftMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createDftMenuItem() {
		this.dftMenuItem.setText(I18N.getGUILabelText("menu.measure.dft.text"));
		this.dftMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.measure.dft.ttp"));
		this.dftMenuItem.addActionListener(this);
		this.dftMenuItem.setActionCommand("dft");
		return this.dftMenuItem;
	}

	/**
	 * This method initializes acfMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createAcfMenuItem() {
		this.acfMenuItem.setText(I18N.getGUILabelText("menu.measure.acf.text"));
		this.acfMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.measure.acf.ttp"));
		this.acfMenuItem.addActionListener(this);
		this.acfMenuItem.setActionCommand("acf");
		return this.acfMenuItem;
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

		if ("statistics".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpStatisticsDescriptor());
		}
		if ("nexelscan".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpNexelScanDescriptor());
		}
		if ("stackstat".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpStackStatDescriptor());
		}
		if ("dft".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpDFTDescriptor());
		}
		if ("acf".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpACFDescriptor());
		}
		if ("integralimage".equals(e.getActionCommand())) {
			RenderedImage intImg = (RenderedImage) new IntegralImage(Look
					.getInstance().getCurrentImage().getAsBufferedImage())
					.getAsImage();
			Look.getInstance().setImage(intImg);
		}
	}
}
