package at.mug.iqm.core.processing;

/*
 * #%L
 * Project: IQM - Application Core
 * File: CreateCopiesTask.java
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


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.api.operator.AbstractOperatorGUI;
import at.mug.iqm.api.operator.IOperator;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.core.workflow.ExecutionProxy;
import at.mug.iqm.core.workflow.ResultVisualizer;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.util.GUITools;

/**
 * This task creates <code>n</code> copies of the currently displayed item. If
 * random generators are opened, <code>n</code> individual samples are created.
 * 
 * @author Philipp Kainz
 * @update 2018 06 27 thumbnail creation only when they are actually needed (according to Kleinowitz).
 */
public class CreateCopiesTask extends AbstractProcessingTask {
	// class specific logger
	private Class<?> caller = CreateCopiesTask.class;
	private static final Logger logger = Logger
			.getLogger(CreateCopiesTask.class);

	// class variable declaration
	private int numCopies;

	/**
	 * This is the default constructor.
	 * <p>
	 * This task always runs <code>headless=true</code>.
	 * 
	 * @param wp
	 *            the work package, may contain a <code>null</code> object of
	 *            {@link ParameterBlockIQM} and {@link IOperator}
	 * @param numCopies
	 *            number of copies to make
	 * @param virtual
	 *            whether or not the items should be processed virtually
	 */
	public CreateCopiesTask(IWorkPackage wp, int numCopies, boolean virtual) {
		this.setWorkPackage(wp);
		if (wp != null) {
			this.setOperator(wp.getOperator());
		}
		this.numCopies = numCopies;
		this.setVirtual(virtual);
		this.setHeadless(true);

		this.addPropertyChangeListener(GUITools.getStatusPanel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.mug.iqm.core.processing.task.AbstractProcessingTask#doInBackground()
	 */
	@Override
	protected List<IqmDataBox> doInBackground() throws Exception {
		GUITools.disableMainFrameInteraction();
		this.setProgress(0);
		List<IqmDataBox> resultList = new ArrayList<IqmDataBox>(1000);
		File virtDir = null;

		this.startTime = System.currentTimeMillis();

		// create new virtual directory, if required
		if (this.isVirtual()) {
			virtDir = VirtualDataManager.getNewVirtualDirectory();
			boolean success = virtDir.mkdir();
			if (success) {
				BoardPanel
						.appendTextln("Created new directory for virtual files: "
								+ virtDir.toString());
			} else {
				DialogUtil.getInstance().showDefaultErrorMessage(
						"Couldn't create new directory: " + virtDir.toString());
				return null;
			}
		}

		int currentManagerItemIndex = Application.getManager()
				.getCurrItemIndex();

		IqmDataBox original = null;

		// no operator is launched but an item is displayed
		if (this.getWorkPackage() == null && currentManagerItemIndex != -1) {
			logger.debug("Getting the item from the manager.");
			original = Tank.getInstance().getCurrentTankIqmDataBoxAt(
					currentManagerItemIndex);
			if (original instanceof IVirtualizable) {
				original = VirtualDataManager.getInstance().load(
						(IVirtualizable) original);
			}

		} else {
			ExecutionProxy.getCurrentInstance().disableInputs(
					(AbstractOperatorGUI) ExecutionProxy.getCurrentInstance()
							.getOperatorGUI());

			// if the operator is a generator, proceed, otherwise return null
			switch (getOperator().getType()) {
			case IMAGE_GENERATOR:
			case PLOT_GENERATOR:
				logger.debug("Creating new samples with the parameters from the launched generator.");
				break;
			default:
				logger.debug("Getting the item from the manager.");
				if (!isVirtual()) {
					original = Tank.getInstance().getCurrentTankIqmDataBoxAt(
							currentManagerItemIndex);
				} else {
					if (original instanceof IVirtualizable) {
						original = VirtualDataManager.getInstance().load(
								(IVirtualizable) original);
					}
				}
				break;
			}
		}

		logger.debug("Processing copies of item #"
				+ (currentManagerItemIndex + 1) + " from current manager.");

		// IqmDataBox processedResults = null;
		List<IqmDataBox> processedResults = new Vector<IqmDataBox>(1000);

		// set the original as source for the processed results
		// this list will be overwritten by generator routines
		processedResults.add(original);

		long startTime;

		// perform the copy calculation n times
		for (int n = 0; n < numCopies; n++) {
			startTime = System.currentTimeMillis();

			// if the operator type is a GENERATOR, run the task n times
			// explicitly!
			try {
				switch (this.getOperator().getType()) {
				case IMAGE_GENERATOR:
					// processing a new image every time
					SingleProcessingTask imageGenTask = (SingleProcessingTask) TaskFactory
							.getInstance().createSingleTask(
									this.getWorkPackage());

					// set the progress listener to the operator running in the
					// single processing task
					imageGenTask.getOperator().addProgressListener(
							this.getOperator().getProgressListeners(
									"operatorProgress")[0], "operatorProgress");

					imageGenTask.setParentTask(this);

					// set the progress listener to the single processing task
					this.firePropertyChange("singleTaskRunning", 0, 1);
					Result imgResult = (Result) imageGenTask.processExplicit();
					processedResults = imgResult.listImageResults();
					original = processedResults.get(0);
					this.firePropertyChange("singleTaskRunning", 1, 0);
					break;

				case PLOT_GENERATOR:
					// processing a new plot every time
					SingleProcessingTask plotGenTask = (SingleProcessingTask) TaskFactory
							.getInstance().createSingleTask(
									this.getWorkPackage());

					// set the progress listener to the operator running in the
					// single processing task
					plotGenTask.getOperator().addProgressListener(
							this.getOperator().getProgressListeners(
									"operatorProgress")[0], "operatorProgress");

					plotGenTask.setParentTask(this);

					// set the progress listener to the single processing task
					this.firePropertyChange("singleTaskRunning", 0, 1);
					Result plotResult = (Result) plotGenTask.processExplicit();
					processedResults = plotResult.listPlotResults();
					original = processedResults.get(0);
					this.firePropertyChange("singleTaskRunning", 1, 0);
					break;
				default:
					break;
				}
			} catch (NullPointerException npe) {
				// this null-pointer is thrown, if the copies task has been
				// called without a work package
				logger.trace("No operator is launched, creating a copy instead of a new sample: "
						+ npe);
			}

			if (!isVirtual()) {
				// add the processed results
				// NOTE: the processedResult may just be the original image!
				switch (original.getDataType()) {
				case IMAGE:
				case PLOT:
				case TABLE:
				case CUSTOM:
					for (IqmDataBox processedBox : processedResults) {
						//processedBox.createThumbnails();
						resultList.add(processedBox.clone());
					}
					break;
				default:
					break;
				}
			} else {
				switch (original.getDataType()) {
				case IMAGE:
				case PLOT:
				case TABLE:
				case CUSTOM:
					// store the objects to the hard drive and morph them into
					// virtual objects
					for (IqmDataBox processedBox : processedResults) {
						//processedBox.createThumbnails();
						// create new thumbnails
						VirtualDataBox vBox = (VirtualDataBox) VirtualDataManager
								.getInstance().save(processedBox, virtDir);
						resultList.add(vBox);
						processedBox = null;
					}
					break;
				default:
					break;
				}
			}

			int proz = (n + 1) * 100;
			proz = proz / (numCopies);
			this.setProgress(proz); // updates the stack progress bar

			long diff = System.currentTimeMillis() - startTime;
			float sec = diff / 1000F;
			BoardPanel.appendTextln("Copy " + (n + 1) + " of " + numCopies
					+ " processed. (" + proz + "%), Duration (s): " + sec,
					caller);

		}
		processedResults = null;
		return resultList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.mug.iqm.core.processing.task.AbstractProcessingTask#done()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void done() {
		List<IqmDataBox> resultList;
		try {
			resultList = (ArrayList<IqmDataBox>) this.get();

			if (resultList != null) {
				new ResultVisualizer().addResultsToTank(resultList);

				this.duration = System.currentTimeMillis() - this.startTime;
				TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern("HHH:mm:ss:SSS");
				BoardPanel.appendTextln(
						"processCreateCopies finished, elapsed time: "
								+ sdf.format(this.duration), caller);
				float sec = this.duration / 1000F;
				float min = this.duration / (60 * 1000F);
				float hour = this.duration / (60 * 60 * 1000F);
				float day = this.duration / (24 * 60 * 60 * 1000F);
				BoardPanel.appendTextln(
						"processCreateCopies finished, elapsed time: " + day
								+ "d/" + hour + "h/" + min + "m/" + sec + "s",
						caller);
			} else {
				BoardPanel
						.appendTextln("There is no image to create a copy from!");
			}

		} catch (ExecutionException e) {
			// log the error message
			logger.error("An error occurred: ", e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			// log the error message
			logger.error("An error occurred: ", e);
			e.printStackTrace();
		} finally {
			GUITools.getStatusPanel().setProcessingTask(null);
			GUITools.enableMainFrameInteraction();
			try {
				ExecutionProxy.getCurrentInstance().enableInputs(
						(AbstractOperatorGUI) ExecutionProxy
								.getCurrentInstance().getOperatorGUI());
			} catch (Exception ignored) {
			}
			resultList = null;
			// reset progress bar and collect garbage
			this.setProgress(0);
			System.gc();
		}
	}
}
