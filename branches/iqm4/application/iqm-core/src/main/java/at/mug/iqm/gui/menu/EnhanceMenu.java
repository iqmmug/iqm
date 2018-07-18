package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: EnhanceMenu.java
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

import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.workflow.ExecutionProxy;
import at.mug.iqm.img.bundle.descriptors.IqmOpEdgeDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpHistoModifyDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpRankDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpSmoothDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpUnsharpMaskDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpWhiteBalanceDescriptor;

/**
 * This is the base class for the enhance menu in IQM.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 */
public class EnhanceMenu extends DeactivatableMenu implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5333828789266605725L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(EnhanceMenu.class);

	// class variable declaration
	private OperatorMenuItem edgeMenuItem;
	private OperatorMenuItem rankMenuItem;
	private OperatorMenuItem smoothMenuItem;
	private OperatorMenuItem unsharpMaskMenuItem;
	private OperatorMenuItem whiteBalanceMenuItem;
	private OperatorMenuItem histoModifyMenuItem;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped EnhanceMenu.
	 */
	public EnhanceMenu() {

		logger.debug("Generating new instance of 'enhance' menu.");

		// initialize the variables
		this.edgeMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.rankMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.smoothMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.unsharpMaskMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.whiteBalanceMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.histoModifyMenuItem = new OperatorMenuItem(OperatorType.IMAGE);

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Menu 'enhance' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items to the enhance menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.enhance.text"));

		// assemble: add created elements to the JMenu
		this.add(this.createEdgeMenuItem());
		this.addSeparator();
		this.add(this.createRankMenuItem());
		this.add(this.createSmoothMenuItem());
		this.add(this.createUnsharpMaskMenuItem());
		this.addSeparator();
		this.add(this.createWhiteBalanceMenuItem());
		this.add(this.createHistoModifyMenuItem());
	}

	/**
	 * This method initializes edgeMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createEdgeMenuItem() {
		this.edgeMenuItem.setText(I18N
				.getGUILabelText("menu.enhance.edgeDetection.text"));
		this.edgeMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.enhance.edgeDetection.ttp"));
		this.edgeMenuItem.addActionListener(this);
		this.edgeMenuItem.setActionCommand("edge");
		return this.edgeMenuItem;
	}

	/**
	 * This method initializes rankMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createRankMenuItem() {
		this.rankMenuItem.setText(I18N
				.getGUILabelText("menu.enhance.rankFilter.text"));
		this.rankMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.enhance.rankFilter.ttp"));
		this.rankMenuItem.addActionListener(this);
		this.rankMenuItem.setActionCommand("rank");
		return this.rankMenuItem;
	}

	/**
	 * This method initializes smoothMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSmoothMenuItem() {
		this.smoothMenuItem.setText(I18N
				.getGUILabelText("menu.enhance.smoothFilter.text"));
		this.smoothMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.enhance.smoothFilter.ttp"));
		this.smoothMenuItem.addActionListener(this);
		this.smoothMenuItem.setActionCommand("smooth");
		return this.smoothMenuItem;
	}

	/**
	 * This method initializes unsharpMaskMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createUnsharpMaskMenuItem() {
		this.unsharpMaskMenuItem.setText(I18N
				.getGUILabelText("menu.enhance.unsharpMaskFilter.text"));
		this.unsharpMaskMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.enhance.unsharpMaskFilter.ttp"));
		this.unsharpMaskMenuItem.addActionListener(this);
		this.unsharpMaskMenuItem.setActionCommand("unsharpmask");
		return this.unsharpMaskMenuItem;
	}

	/**
	 * This method initializes whiteBalanceMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createWhiteBalanceMenuItem() {
		this.whiteBalanceMenuItem.setText(I18N
				.getGUILabelText("menu.enhance.whiteBalance.text"));
		this.whiteBalanceMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.enhance.whiteBalance.ttp"));
		this.whiteBalanceMenuItem.addActionListener(this);
		this.whiteBalanceMenuItem.setActionCommand("whitebalance");
		return this.whiteBalanceMenuItem;
	}

	/**
	 * This method initializes histoModifyMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createHistoModifyMenuItem() {
		this.histoModifyMenuItem.setText(I18N
				.getGUILabelText("menu.enhance.histoModification.text"));
		this.histoModifyMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.enhance.histoModification.ttp"));
		this.histoModifyMenuItem.addActionListener(this);
		this.histoModifyMenuItem.setActionCommand("histomodify");
		return this.histoModifyMenuItem;
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

		if ("edge".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpEdgeDescriptor());
		}
		if ("rank".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpRankDescriptor());
		}
		if ("smooth".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpSmoothDescriptor());
		}
		if ("unsharpmask".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpUnsharpMaskDescriptor());
		}
		if ("whitebalance".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpWhiteBalanceDescriptor());
		}
		if ("histomodify".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpHistoModifyDescriptor());
		}
	}
}
