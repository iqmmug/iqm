package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: FractalMenu.java
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

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.workflow.ExecutionProxy;
import at.mug.iqm.img.bundle.descriptors.IqmOpComplexLogDepthDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCreateFracSurfDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracBoxDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracFFTDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracGenDimDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracHiguchiDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracIFSDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracHRMDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracLacDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracMinkowskiDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracPyramidDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracScanDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracSurrogateDescriptor;

/**
 * This is the base class for the fractal menu in IQM.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 */
public class FractalMenu extends DeactivatableMenu implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3554830035229045459L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(FractalMenu.class);

	// class variable declaration
	private OperatorMenuItem fracIFSMenuItem;
	private OperatorMenuItem fracHRMMenuItem;
	private OperatorMenuItem fracSurfMenuItem;
	private OperatorMenuItem fracLacMenuItem;
	private OperatorMenuItem fracBoxMenuItem;
	private OperatorMenuItem fracPyramidMenuItem;
	private OperatorMenuItem fracMinkowskiMenuItem;
	private OperatorMenuItem fracGenDimMenuItem;
	private OperatorMenuItem fracFFTMenuItem;
	private OperatorMenuItem fracHiguchiMenuItem;
	private OperatorMenuItem complexLogDepthMenuItem;
	private OperatorMenuItem fracScanMenuItem;
	private OperatorMenuItem fracSurrogateMenuItem;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped FractalMenu.
	 */
	public FractalMenu() {
		logger.debug("Generating new instance of 'fractal' menu.");

		// initialize the variables
		this.fracIFSMenuItem          = new OperatorMenuItem(OperatorType.IMAGE_GENERATOR);
		this.fracHRMMenuItem          = new OperatorMenuItem(OperatorType.IMAGE_GENERATOR);
		this.fracSurfMenuItem         = new OperatorMenuItem(OperatorType.IMAGE_GENERATOR);
		this.fracLacMenuItem          = new OperatorMenuItem(OperatorType.IMAGE);
		this.fracBoxMenuItem          = new OperatorMenuItem(OperatorType.IMAGE);
		this.fracPyramidMenuItem      = new OperatorMenuItem(OperatorType.IMAGE);
		this.fracMinkowskiMenuItem    = new OperatorMenuItem(OperatorType.IMAGE);
		this.fracGenDimMenuItem       = new OperatorMenuItem(OperatorType.IMAGE);
		this.fracFFTMenuItem          = new OperatorMenuItem(OperatorType.IMAGE);
		this.fracHiguchiMenuItem      = new OperatorMenuItem(OperatorType.IMAGE);
		this.complexLogDepthMenuItem  = new OperatorMenuItem(OperatorType.IMAGE);
		this.fracScanMenuItem         = new OperatorMenuItem(OperatorType.IMAGE);
		this.fracSurrogateMenuItem    = new OperatorMenuItem(OperatorType.IMAGE);

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Menu 'fractal' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items in 'fractal' menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.fractal.text"));

		// assemble: add created elements to the JMenu
		this.add(this.createFracIFSMenuItem());
		this.add(this.createFracHRMMenuItem());
		this.add(this.createFracSurfMenuItem());
		this.addSeparator();
		this.add(this.createFracLacMenuItem());
		this.add(this.createFracBoxMenuItem());
		this.add(this.createFracPyramidMenuItem());
		this.add(this.createFracMinkowskiMenuItem());
		this.add(this.createFracGenDimMenuItem());
		this.add(this.createFracFFTMenuItem());
		this.add(this.createFracHiguchiMenuItem());
		this.add(this.createFracScanMenuItem());
		this.addSeparator();
		this.add(this.createComplexLogDepthMenuItem());
		this.addSeparator();
		this.add(this.createFracSurrogateMenuItem());
		

	}

	/**
	 * This method initializes fracIFSMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracIFSMenuItem() {
		this.fracIFSMenuItem.setText(I18N.getGUILabelText("menu.fractal.ifs.text"));
		this.fracIFSMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.ifs.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.ifs.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.ifs.disabled"));
		this.fracIFSMenuItem.setSelectedIcon(iconEnabled);
		this.fracIFSMenuItem.setIcon(iconEnabled);
		this.fracIFSMenuItem.setDisabledIcon(iconDisabled);
		this.fracIFSMenuItem.addActionListener(this);
		this.fracIFSMenuItem.setActionCommand("fracifs");
		return this.fracIFSMenuItem;
	}

	/**
	 * This method initializes fracHRMMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracHRMMenuItem() {
		this.fracHRMMenuItem.setText(I18N.getGUILabelText("menu.fractal.hrm.text"));
		this.fracHRMMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.hrm.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.hrm.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.hrm.disabled"));
		this.fracHRMMenuItem.setSelectedIcon(iconEnabled);
		this.fracHRMMenuItem.setIcon(iconEnabled);
		this.fracHRMMenuItem.setDisabledIcon(iconDisabled);
		this.fracHRMMenuItem.addActionListener(this);
		this.fracHRMMenuItem.setActionCommand("frachrm");
		return this.fracHRMMenuItem;
	}
	
	/**
	 * This method initializes fracSurfMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracSurfMenuItem() {
		this.fracSurfMenuItem.setText(I18N.getGUILabelText("menu.fractal.surface.text"));
		this.fracSurfMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.surface.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.surface.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.surface.disabled"));
		this.fracSurfMenuItem.setSelectedIcon(iconEnabled);
		this.fracSurfMenuItem.setIcon(iconEnabled);
		this.fracSurfMenuItem.setDisabledIcon(iconDisabled);
		this.fracSurfMenuItem.addActionListener(this);
		this.fracSurfMenuItem.setActionCommand("fracsurf");
		return this.fracSurfMenuItem;
	}

	/**
	 * This method initializes fracLacMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracLacMenuItem() {
		this.fracLacMenuItem.setText(I18N.getGUILabelText("menu.fractal.lacunarity.text"));
		this.fracLacMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.lacunarity.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.lacunarity.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.lacunarity.disabled"));
		this.fracLacMenuItem.setSelectedIcon(iconEnabled);
		this.fracLacMenuItem.setIcon(iconEnabled);
		this.fracLacMenuItem.setDisabledIcon(iconDisabled);
		this.fracLacMenuItem.addActionListener(this);
		this.fracLacMenuItem.setActionCommand("fraclac");
		return this.fracLacMenuItem;
	}

	/**
	 * This method initializes fracBoxMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracBoxMenuItem() {
		this.fracBoxMenuItem.setText(I18N.getGUILabelText("menu.fractal.boxCounting.text"));
		this.fracBoxMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.boxCounting.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.box.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.box.disabled"));
		this.fracBoxMenuItem.setSelectedIcon(iconEnabled);
		this.fracBoxMenuItem.setIcon(iconEnabled);
		this.fracBoxMenuItem.setDisabledIcon(iconDisabled);
		this.fracBoxMenuItem.addActionListener(this);
		this.fracBoxMenuItem.setActionCommand("fracbox");
		return this.fracBoxMenuItem;
	}

	/**
	 * This method initializes fracPyramidMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracPyramidMenuItem() {
		this.fracPyramidMenuItem.setText(I18N.getGUILabelText("menu.fractal.pyramid.text"));
		this.fracPyramidMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.pyramid.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.pyramid.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.pyramid.disabled"));
		this.fracPyramidMenuItem.setSelectedIcon(iconEnabled);
		this.fracPyramidMenuItem.setIcon(iconEnabled);
		this.fracPyramidMenuItem.setDisabledIcon(iconDisabled);
		this.fracPyramidMenuItem.addActionListener(this);
		this.fracPyramidMenuItem.setActionCommand("fracpyramid");
		return this.fracPyramidMenuItem;
	}

	/**
	 * This method initializes fracMinkowskiMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracMinkowskiMenuItem() {
		this.fracMinkowskiMenuItem.setText(I18N.getGUILabelText("menu.fractal.minkowski.text"));
		this.fracMinkowskiMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.minkowski.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.minkowski.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.minkowski.disabled"));
		this.fracMinkowskiMenuItem.setSelectedIcon(iconEnabled);
		this.fracMinkowskiMenuItem.setIcon(iconEnabled);
		this.fracMinkowskiMenuItem.setDisabledIcon(iconDisabled);
		this.fracMinkowskiMenuItem.addActionListener(this);
		this.fracMinkowskiMenuItem.setActionCommand("fracminkowski");
		return this.fracMinkowskiMenuItem;
	}

	/**
	 * This method initializes fracGenDimMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracGenDimMenuItem() {
		this.fracGenDimMenuItem.setText(I18N.getGUILabelText("menu.fractal.genDim.text"));
		this.fracGenDimMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.genDim.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.gendim.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.gendim.disabled"));
		this.fracGenDimMenuItem.setSelectedIcon(iconEnabled);
		this.fracGenDimMenuItem.setIcon(iconEnabled);
		this.fracGenDimMenuItem.setDisabledIcon(iconDisabled);
		this.fracGenDimMenuItem.addActionListener(this);
		this.fracGenDimMenuItem.setActionCommand("fracgendim");
		return this.fracGenDimMenuItem;
	}

	/**
	 * This method initializes fracFFTMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracFFTMenuItem() {
		this.fracFFTMenuItem.setText(I18N.getGUILabelText("menu.fractal.fft.text"));
		this.fracFFTMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.fft.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.fft.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.fft.disabled"));
		this.fracFFTMenuItem.setSelectedIcon(iconEnabled);
		this.fracFFTMenuItem.setIcon(iconEnabled);
		this.fracFFTMenuItem.setDisabledIcon(iconDisabled);	
		this.fracFFTMenuItem.addActionListener(this);
		this.fracFFTMenuItem.setActionCommand("fracfft");
		return this.fracFFTMenuItem;
	}

	/**
	 * This method initializes fracHiguchiMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracHiguchiMenuItem() {
		this.fracHiguchiMenuItem.setText(I18N.getGUILabelText("menu.fractal.higuchi.text"));
		this.fracHiguchiMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.higuchi.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.higuchi.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.higuchi.disabled"));
		this.fracHiguchiMenuItem.setSelectedIcon(iconEnabled);
		this.fracHiguchiMenuItem.setIcon(iconEnabled);
		this.fracHiguchiMenuItem.setDisabledIcon(iconDisabled);
		this.fracHiguchiMenuItem.addActionListener(this);
		this.fracHiguchiMenuItem.setActionCommand("frachiguchi");
		return this.fracHiguchiMenuItem;
	}
	
	/**
	 * This method initializes fracLogDepthMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createComplexLogDepthMenuItem() {
		this.complexLogDepthMenuItem.setText(I18N.getGUILabelText("menu.complex.logDepth.text"));
		this.complexLogDepthMenuItem.setToolTipText(I18N.getGUILabelText("menu.complex.logDepth.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.complex.logicaldepth.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.complex.logicaldepth.disabled"));
		this.complexLogDepthMenuItem.setSelectedIcon(iconEnabled);
		this.complexLogDepthMenuItem.setIcon(iconEnabled);
		this.complexLogDepthMenuItem.setDisabledIcon(iconDisabled);	
		this.complexLogDepthMenuItem.addActionListener(this);
		this.complexLogDepthMenuItem.setActionCommand("complexlogdepth");
		return this.complexLogDepthMenuItem;
	}

	/**
	 * This method initializes fracFracScanMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracScanMenuItem() {
		this.fracScanMenuItem.setText(I18N.getGUILabelText("menu.fractal.scan.text"));
		this.fracScanMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.scan.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.fracscan.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.fracscan.disabled"));
		this.fracScanMenuItem.setSelectedIcon(iconEnabled);
		this.fracScanMenuItem.setIcon(iconEnabled);
		this.fracScanMenuItem.setDisabledIcon(iconDisabled);
		this.fracScanMenuItem.addActionListener(this);
		this.fracScanMenuItem.setActionCommand("fracscan");
		return this.fracScanMenuItem;
	}

	/**
	 * This method initializes surrogateMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFracSurrogateMenuItem() {
		this.fracSurrogateMenuItem.setText(I18N.getGUILabelText("menu.fractal.surrogate.text"));
		this.fracSurrogateMenuItem.setToolTipText(I18N.getGUILabelText("menu.fractal.surrogate.ttp"));
		ImageIcon iconEnabled  = new ImageIcon(Resources.getImageURL("icon.menu.fractal.surrogate.enabled"));
		ImageIcon iconDisabled = new ImageIcon(Resources.getImageURL("icon.menu.fractal.surrogate.disabled"));
		this.fracSurrogateMenuItem.setSelectedIcon(iconEnabled);
		this.fracSurrogateMenuItem.setIcon(iconEnabled);
		this.fracSurrogateMenuItem.setDisabledIcon(iconDisabled);
		this.fracSurrogateMenuItem.addActionListener(this);
		this.fracSurrogateMenuItem.setActionCommand("fracsurrogate");
		return this.fracSurrogateMenuItem;
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

		if ("fracifs".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracIFSDescriptor());
		} else if ("frachrm".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracHRMDescriptor());
		} else if ("fracsurf".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpCreateFracSurfDescriptor());
		} else if ("fraclac".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracLacDescriptor());
		} else if ("fracbox".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracBoxDescriptor());
		} else if ("fracpyramid".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracPyramidDescriptor());
		} else if ("fracminkowski".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracMinkowskiDescriptor());
		} else if ("fracfft".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracFFTDescriptor());
		} else if ("fracgendim".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracGenDimDescriptor());
		} else if ("frachiguchi".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracHiguchiDescriptor());
		} else if ("complexlogdepth".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpComplexLogDepthDescriptor());
		} else if ("fracscan".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracScanDescriptor());
		} else if ("fracsurrogate".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpFracSurrogateDescriptor());
		}
	}
}
