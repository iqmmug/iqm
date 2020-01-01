package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: CoreMenuBar.java
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


import javax.swing.JFrame;
import javax.swing.JMenu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;

import com.bric.window.WindowMenu;

/**
 * This is the menu bar class for the core implementation of IQM.
 * 
 * @author Philipp Kainz
 */
public class CoreMenuBar extends AbstractMenuBar {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -8887845277353427355L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(CoreMenuBar.class);

	// class variable declaration // menus inherit from JMenu
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu enhanceMenu;
	private JMenu processMenu;
	private JMenu segmentMenu;
	private JMenu measureMenu;
	private JMenu fractalMenu;
	private JMenu plotMenu;
	private JMenu plotAnalysisMenu;
	private JMenu pluginMenu;
	private JMenu scriptMenu;
	private JMenu infoMenu;

	/**
	 * 
	 */
	public CoreMenuBar() {
		super();
		logger.debug("Generating new instance of '" + this.getClass().getName()
				+ "'.");

		this.createAndAssemble();

		logger.debug("'" + this.getClass().getName() + "' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	@Override
	protected void createAndAssemble() {
		logger.debug("Creating subcomponents and assembling '"
				+ this.getClass().getName() + "'.");

		this.fileMenu = new FileMenu();
		this.editMenu = new EditMenu();
		this.enhanceMenu = new EnhanceMenu();
		this.processMenu = new ProcessMenu();
		this.segmentMenu = new SegmentMenu();
		this.measureMenu = new MeasureMenu();
		this.fractalMenu = new FractalMenu();
		this.plotMenu = new PlotMenu();
		this.plotAnalysisMenu = new PlotAnalysisMenu();
		this.pluginMenu = (JMenu) PluginMenu.getInstance();
		this.infoMenu = new InfoMenu();

		this.addNewMenu(this.getFileMenu());
		this.addNewMenu(this.getEditMenu());
		this.addNewMenu(this.getEnhanceMenu());
		this.addNewMenu(this.getProcessMenu());
		this.addNewMenu(this.getSegmentMenu());
		this.addNewMenu(this.getMeasureMenu());
		this.addNewMenu(this.getFractalMenu());
		this.addNewMenu(this.getPlotMenu());
		this.addNewMenu(this.getPlotAnalysisMenu());
		this.addNewMenu(this.getPluginMenu());
		this.addNewMenu(new WindowMenu((JFrame) Application.getMainFrame()));
		this.addNewMenu(this.getInfoMenu());
	}

	/**
	 * @return the fileMenu
	 */
	public JMenu getFileMenu() {
		return fileMenu;
	}

	/**
	 * @return the editMenu
	 */
	public JMenu getEditMenu() {
		return editMenu;
	}

	/**
	 * @return the enhanceMenu
	 */
	public JMenu getEnhanceMenu() {
		return enhanceMenu;
	}

	/**
	 * @return the processMenu
	 */
	public JMenu getProcessMenu() {
		return processMenu;
	}

	/**
	 * @return the segmentMenu
	 */
	public JMenu getSegmentMenu() {
		return segmentMenu;
	}

	/**
	 * @return the measureMenu
	 */
	public JMenu getMeasureMenu() {
		return measureMenu;
	}

	/**
	 * @return the fractalMenu
	 */
	public JMenu getFractalMenu() {
		return fractalMenu;
	}

	/**
	 * @return the plotMenu
	 */
	public JMenu getPlotMenu() {
		return plotMenu;
	}

	/**
	 * @return the plotAnalyisMenu
	 */
	public JMenu getPlotAnalysisMenu() {
		return plotAnalysisMenu;
	}

	/**
	 * @return the infoMenu
	 */
	public JMenu getInfoMenu() {
		return infoMenu;
	}

	/**
	 * @param fileMenu
	 *            the fileMenu to set
	 */
	public void setFileMenu(FileMenu fileMenu) {
		this.fileMenu = fileMenu;
	}

	/**
	 * @param editMenu
	 *            the editMenu to set
	 */
	public void setEditMenu(JMenu editMenu) {
		this.editMenu = editMenu;
	}

	/**
	 * @param enhanceMenu
	 *            the enhanceMenu to set
	 */
	public void setEnhanceMenu(JMenu enhanceMenu) {
		this.enhanceMenu = enhanceMenu;
	}

	/**
	 * @param processMenu
	 *            the processMenu to set
	 */
	public void setProcessMenu(JMenu processMenu) {
		this.processMenu = processMenu;
	}

	/**
	 * @param segmentMenu
	 *            the segmentMenu to set
	 */
	public void setSegmentMenu(JMenu segmentMenu) {
		this.segmentMenu = segmentMenu;
	}

	/**
	 * @param measureMenu
	 *            the measureMenu to set
	 */
	public void setMeasureMenu(JMenu measureMenu) {
		this.measureMenu = measureMenu;
	}

	/**
	 * @param fractalMenu
	 *            the fractalMenu to set
	 */
	public void setFractalMenu(JMenu fractalMenu) {
		this.fractalMenu = fractalMenu;
	}

	/**
	 * @param plotMenu
	 *            the plotMenu to set
	 */
	public void setPlotMenu(JMenu plotMenu) {
		this.plotMenu = plotMenu;
	}

	/**
	 * @param plotAnalysisMenu
	 *            the plotAnalysisMenu to set
	 */
	public void setPlotAnalysisMenu(JMenu plotAnalysisMenu) {
		this.plotAnalysisMenu = plotAnalysisMenu;
	}

	/**
	 * @param infoMenu
	 *            the infoMenu to set
	 */
	public void setInfoMenu(JMenu infoMenu) {
		this.infoMenu = infoMenu;
	}

	public JMenu getPluginMenu() {
		return pluginMenu;
	}

	public JMenu getScriptMenu() {
		return scriptMenu;
	}

	public void setScriptMenu(JMenu scriptMenu) {
		this.scriptMenu = scriptMenu;
	}
}
