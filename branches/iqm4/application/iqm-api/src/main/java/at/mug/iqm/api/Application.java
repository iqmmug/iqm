package at.mug.iqm.api;

/*
 * #%L
 * Project: IQM - API
 * File: Application.java
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


import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.gui.IMainFrame;
import at.mug.iqm.api.gui.ScriptEditor;
import at.mug.iqm.api.operator.IOperator;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.api.plugin.IPluginMenu;
import at.mug.iqm.api.plugin.IPluginRegistry;
import at.mug.iqm.api.processing.IExecutionProtocol;
import at.mug.iqm.api.processing.ITaskFactory;
import at.mug.iqm.api.processing.ProcessingHistory;
import at.mug.iqm.api.workflow.ILook;
import at.mug.iqm.api.workflow.IManager;
import at.mug.iqm.api.workflow.IPlot;
import at.mug.iqm.api.workflow.ITable;
import at.mug.iqm.api.workflow.ITank;
import at.mug.iqm.api.workflow.IText;


/**
 * This class contains singleton references for the IQM application at runtime.
 * 
 * @author Philipp Kainz
 * 
 */
public final class Application {

	private static final Logger logger = Logger.getLogger(Application.class);

	private static boolean virtual;
	private static IOperator currentOperator;
	private static IOperatorRegistry operatorRegistry;
	private static IPluginRegistry pluginRegistry;
	private static ITaskFactory taskFactory;
	private static IPluginMenu pluginMenu;

	// workflow elements
	private static IExecutionProtocol currentExecutionProtocol;
	private static ProcessingHistory processingHistory;
	
	// script elements
	private static ScriptEditor scriptEditor;

	// GUI control elements
	private static ITank tank;
	private static IManager manager;
	private static ILook look;
	private static IPlot plot;
	private static ITable table;
	private static IText text;

	private static IMainFrame mainFrame;

	// DialogUtility
	private static IDialogUtil dialogUtil;

	private static IApplicationStarter applicationStarter = null;

	private static VirtualDataManager virtualDataManager;

	private static Thread cleanerTask;
	
	private static Thread memoryMonitor;
	
	public static IApplicationStarter getApplicationStarter() {
		return applicationStarter;
	}

	public static void setApplicationStarter(
			IApplicationStarter applicationStarter) {
		Application.applicationStarter = applicationStarter;
	}

	public static IOperatorRegistry getOperatorRegistry() {
		return operatorRegistry;
	}

	public static void setOperatorRegistry(IOperatorRegistry operatorRegistry) {
		Application.operatorRegistry = operatorRegistry;
	}

	public static ITaskFactory getTaskFactory() {
		return taskFactory;
	}

	public static void setTaskFactory(ITaskFactory taskFactory) {
		Application.taskFactory = taskFactory;
	}

	public static void setPluginRegistry(IPluginRegistry pluginRegistry) {
		Application.pluginRegistry = pluginRegistry;
	}

	public static IPluginRegistry getPluginRegistry() {
		return pluginRegistry;
	}

	public static IPluginMenu getPluginMenu() {
		return pluginMenu;
	}

	public static void setPluginMenu(IPluginMenu pluginMenu) {
		Application.pluginMenu = pluginMenu;
	}

	/**
	 * This method sets the virtual modus flag and changes the standard "red"
	 * frame icon to a "blue" one.
	 * <p>
	 * This method is called when clicking on the "Virtual" check-box in the
	 * control panel and sets the flag for application-wide virtual processing
	 * in {@link Application}.
	 * 
	 * @param flag
	 *            whether or not the application is in virtual state
	 */
	public static void setVirtual(boolean flag) {
		virtual = flag;
		URL url = null;
		if (!virtual) {
			url = Resources.getImageURL("icon.application.red.32x32");
			logger.debug("Entering real mode, using RAM");
		}
		if (virtual) {
			url = Resources.getImageURL("icon.application.blue.32x32");
			logger.debug("Entering virtual mode, using hard disk");
		}

		// change the icon in the frame
		((JFrame) getMainFrame()).setIconImage(new ImageIcon(url).getImage());
		
		// tick the checkbox in the control panel
		getMainFrame().getMainPanel().setVirtual(virtual);
	}

	public static boolean isVirtual() {
		return virtual;
	}

	public static IOperator getCurrentOperator() {
		return currentOperator;
	}

	public static void setCurrentOperator(IOperator currentOperator) {
		Application.currentOperator = currentOperator;
	}

	public static ITank getTank() {
		return tank;
	}

	public static void setTank(ITank tank) {
		Application.tank = tank;
	}

	public static IManager getManager() {
		return manager;
	}

	public static void setManager(IManager manager) {
		Application.manager = manager;
	}

	public static ILook getLook() {
		return look;
	}

	public static void setLook(ILook look) {
		Application.look = look;
	}

	public static IPlot getPlot() {
		return plot;
	}

	public static void setPlot(IPlot plot) {
		Application.plot = plot;
	}

	public static ITable getTable() {
		return table;
	}

	public static void setTable(ITable table) {
		Application.table = table;
	}

	public static IText getText() {
		return text;
	}

	public static void setText(IText text) {
		Application.text = text;
	}

	public static IDialogUtil getDialogUtil() {
		return dialogUtil;
	}

	public static void setDialogUtil(IDialogUtil dialogUtil) {
		Application.dialogUtil = dialogUtil;
	}

	public static IMainFrame getMainFrame() {
		return mainFrame;
	}

	public static void setMainFrame(IMainFrame mainFrame) {
		Application.mainFrame = mainFrame;
	}

	public static IExecutionProtocol getCurrentExecutionProtocol() {
		return currentExecutionProtocol;
	}

	public static void setCurrentExecutionProtocol(
			IExecutionProtocol currentExeProtocol) {
		Application.currentExecutionProtocol = currentExeProtocol;
	}

	public static boolean isOperatorLaunched() {
		if (currentExecutionProtocol == null)
			return false;
		else
			return true;
	}

	public static VirtualDataManager getVirtualDataManager() {
		return virtualDataManager;
	}

	public static void setVirtualDataManager(
			VirtualDataManager virtualDataManager) {
		Application.virtualDataManager = virtualDataManager;
	}

	public static Thread getCleanerTask() {
		return cleanerTask;
	}

	public static void setCleanerTask(Thread cleanerTask) {
		Application.cleanerTask = cleanerTask;
	}

	public static Thread getMemoryMonitor() {
		return memoryMonitor;
	}

	public static void setMemoryMonitor(Thread memoryMonitor) {
		Application.memoryMonitor = memoryMonitor;
	}

	public static ScriptEditor getScriptEditor() {
		return scriptEditor;
	}
	
	public static void setScriptEditor(ScriptEditor scriptEditor) {
		Application.scriptEditor = scriptEditor;
	}
	
	public static ProcessingHistory getProcessingHistory(){
		return processingHistory;
	}
}
