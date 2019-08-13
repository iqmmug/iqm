package at.mug.iqm.core.processing;

/*
 * #%L
 * Project: IQM - Application Core
 * File: PreviewProcessingTask.java
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


import java.util.List;
import java.util.Vector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.IOperatorGUI;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.api.processing.ProcessingHistory;
import at.mug.iqm.core.workflow.Manager;
import at.mug.iqm.core.workflow.ResultVisualizer;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class performs a distinct preview, triggered by the currently active
 * {@link IOperatorGUI}. It cannot be processed in <code>virtual</code> mode.
 * 
 * @author Philipp Kainz
 * 
 */
public class PreviewProcessingTask extends AbstractProcessingTask {

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(PreviewProcessingTask.class);

	/**
	 * Default constructor.
	 */
	public PreviewProcessingTask() {
		super();
		this.setHeadless(true);
		this.setVirtual(false);
	}

	/**
	 * This constructor sets the required variables directly. This constructor
	 * shall be used when no GUI is needed to be updated or no elements of the
	 * GUI influence the execution of this task. <code>headless</code> will be
	 * set to <code>true</code>. This constructor shall be used when no GUI is
	 * needed to be updated. <code>virtual</code> is always <code>false</code>.
	 * 
	 * @param wp
	 *            the work package containing operator and parameters
	 */
	public PreviewProcessingTask(IWorkPackage wp) {
		this.setWorkPackage(wp);
		if (wp != null) {
			this.setOperator(wp.getOperator());
		}

		this.setHeadless(true);
		this.setVirtual(false);
	}

	/**
	 * This constructor sets the required variables directly.
	 * <code>headless</code> will be set to <code>false</code>. This constructor
	 * shall be used when a GUI is needed to be updated, or the GUI contains
	 * elements for custom processing. <code>virtual</code> is always
	 * <code>false</code>.
	 * 
	 * @param wp
	 *            the work package containing operator and parameters
	 * 
	 * @param opGUI
	 *            GUI of the containing operator
	 * 
	 */
	public PreviewProcessingTask(IWorkPackage wp, IOperatorGUI opGUI) {
		this.setWorkPackage(wp);
		if (wp != null) {
			this.setOperator(wp.getOperator());
		}

		this.setOperatorGUI(opGUI);

		this.setHeadless(false);
		this.setVirtual(false);
	}

	/**
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected IResult doInBackground() throws Exception {
		
		// record the execution to the processing history
		ProcessingHistory.getInstance().add(this.workPackage);
		
		GUITools.disableMainFrameInteraction();
		IResult result = new Result();
		SingleProcessingTask singleTask = null;

		if (this.isHeadless()) {
			// construct new processing task
			singleTask = new SingleProcessingTask(this.getWorkPackage());
		} else {
			singleTask = new SingleProcessingTask(this.getWorkPackage(),
					this.getOperatorGUI());
		}

		// IMPORTANT: set the parent task for the single processing task to this
		// reference
		// this is for cancellation
		singleTask.setParentTask(this);
		this.setChildTask(singleTask);

		int totalIterations = this.getWorkPackage().getIterations();
		List<Result> multiResults = new Vector<Result>(totalIterations);
		// if the operator should run >1 times, execute the operator several
		// times and collect the results.
		for (int i = 0; i < totalIterations; i++) {
			// execute the processing task within this worker thread (this will
			// block until SingleProcessingTask has finished)
			try {
				System.out.println(Thread.currentThread().getName()
						+ ": Pre-Execute");
				this.firePropertyChange("singleTaskRunning", 0, 1);
				// this causes another background thread
				// singleTask.execute();
				// Result singleResult = (IqmDataBox) singleTask.get();
				// in order to run the execution of the single task within the
				// same
				// thread (un)comment the following line:
				Result singleResult = (Result) singleTask.processExplicit();

				if (totalIterations > 1){
					this.setProgress((i + 1) * 100 / totalIterations);
				}

				// add the result to the list
				multiResults.add(i, singleResult);

				this.firePropertyChange("singleTaskRunning", 1, 0);
				System.out.println(Thread.currentThread().getName()
						+ ": Post-Execute");

			} catch (Exception e) {
				logger.error("An error occurred: ", e);
				return null;
			}
		}

		// extract the single results from the multi-results
		for (Result r : multiResults) {
			if (r.hasImages()) {
				for (IqmDataBox box : r.listImageResults()) {
					result.addItem(box);
				}
			}
			if (r.hasPlots()) {
				for (IqmDataBox box : r.listPlotResults()) {
					result.addItem(box);
				}
			}
			if (r.hasTables()) {
				for (IqmDataBox box : r.listTableResults()) {
					result.addItem(box);
				}
			}
			if (r.hasCustomResults()) {
				for (IqmDataBox box : r.listCustomResults()) {
					result.addItem(box);
				}
			}
		}

		return result;
	}

	@Override
	protected void done() {
		Result result = null;
		try {
			logger.debug("Retrieving result from preview processing. Task# "
					+ this.toString());
			// get the result when processing is done
			result = (Result) this.get();

			// process the result according to its content
			if (result != null) {
				new ResultVisualizer().displayPreviewResults(result);
				Manager.getInstance().startTogglePreviewIfSelected();
			} else {
				BoardPanel.appendTextln("Result is NULL, nothing to display.");
			}
		} catch (InterruptedException e) {
			logger.error("An error occurred: ", e);
		} catch (ExecutionException e) {
			logger.error("An error occurred: ", e);
		} catch (CancellationException e) {
			logger.error("The task has been cancelled.");
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
		} finally {
			logger.debug("Done preview processing. Task# " + this.toString());

			// turn off the progress bar in the status panel
			this.firePropertyChange("singleTaskRunning", 1, 0);
			this.setProgress(0);

			GUITools.enableMainFrameInteraction();

			// collect garbage
			System.gc();
		}
	}
}
