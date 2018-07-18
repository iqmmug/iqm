package at.mug.iqm.core.processing;

/*
 * #%L
 * Project: IQM - Application Core
 * File: SerialProcessingTask.java
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
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.api.operator.IOperatorGUI;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorDescriptorFactory;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.api.operator.StackProcessingType;
import at.mug.iqm.api.operator.WorkPackage;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.api.processing.IExplicitProcessingTask;
import at.mug.iqm.api.processing.ProcessingHistory;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.workflow.Manager;
import at.mug.iqm.core.workflow.ResultVisualizer;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class performs a stack processing task on <code>1...n</code> items
 * sequentially.
 * 
 * @author Philipp Kainz
 * @since 2.1
 */
public class SerialProcessingTask extends AbstractProcessingTask implements
		IExplicitProcessingTask {

	// class specific logger
	private Class<?> caller = SerialProcessingTask.class;
	private static final Logger logger = Logger
			.getLogger(SerialProcessingTask.class);

	/**
	 * The number of items to be processed.
	 */
	int nStackItems = -1;

	/**
	 * A file object representing the virtual directory.
	 */
	File virtDir = null;

	/**
	 * This is the constructor. Images to be processed are obtained while the
	 * algorithm runs. <code>headless</code> will be set to <code>true</code>.
	 * This constructor shall be used when no GUI is needed to be updated.
	 * <p>
	 * <b>Hint</b>: Use this in scripts.
	 * 
	 * @param wp
	 *            the work package for this task
	 * @param virtual
	 *            a flag for virtual processing
	 */
	public SerialProcessingTask(IWorkPackage wp, boolean virtual) {
		this.setWorkPackage(wp);
		if (wp != null) {
			this.setOperator(wp.getOperator());
		}
		this.setVirtual(virtual);
		this.setHeadless(true);
	}

	/**
	 * This is the constructor. Images to be processed are obtained while the
	 * algorithm runs. <code>headless</code> will be set to <code>false</code>.
	 * This constructor shall be used when a GUI is needed to be updated.
	 * 
	 * @param wp
	 *            the work package for this task
	 * @param virtual
	 *            a flag for virtual processing
	 * @param opGUI
	 *            the calling operator gui
	 */
	public SerialProcessingTask(IWorkPackage wp, IOperatorGUI opGUI,
			boolean virtual) {
		this.setWorkPackage(wp);
		if (wp != null) {
			this.setOperator(wp.getOperator());
		}
		this.setOperatorGUI(opGUI);
		this.setVirtual(virtual);
		this.setHeadless(false);
	}

	/**
	 * A default constructor. <code>headless</code> will be set to
	 * <code>true</code>.
	 */
	public SerialProcessingTask() {
		super();
		this.setHeadless(true);
	}

	/**
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected List<ArrayList<IqmDataBox>> doInBackground() throws Exception {
		return this.processExplicit();
	}

	/**
	 * Display the results using the {@link ResultVisualizer} class.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void done() {
		List<ArrayList<IqmDataBox>> result = null;
		try {
			if (!this.isHeadless()) {
				logger.debug("Retrieving result list from serial processing.");
				result = (List<ArrayList<IqmDataBox>>) this.get();

				if (result != null) {
					new ResultVisualizer().add2DResultListToTank(result);
					Manager.getInstance().startTogglePreviewIfSelected();

					// show processing information on board
					this.duration = System.currentTimeMillis() - this.startTime;
					TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
					SimpleDateFormat sdf = new SimpleDateFormat();
					sdf.applyPattern("HHH:mm:ss:SSS");
					BoardPanel.appendTextln(
							"SerialProcessingTask finished, elapsed time: "
									+ sdf.format(this.duration), caller);
					float sec = this.duration / 1000F;
					float min = this.duration / (60 * 1000F);
					float hour = this.duration / (60 * 60 * 1000F);
					float day = this.duration / (24 * 60 * 60 * 1000F);
					BoardPanel.appendTextln(
							"SerialProcessingTask finished, elapsed time: "
									+ day + "d/" + hour + "h/" + min + "m/"
									+ sec + "s", caller);
				} else {
					BoardPanel
							.appendTextln("Result is NULL, nothing to display.");
				}

				// TODO close the operator GUI if required here
			}
		} catch (ExecutionException e) {
			// log the error message
			logger.error("An error occurred: ", e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			// log the error message
			logger.error("An error occurred: ", e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
			DialogUtil.getInstance().showErrorMessage(
					"Could not finish serial stack processing!", e, true);
		} finally {
			// reset progress bar and collect garbage
			setProgress(0);
			this.firePropertyChange("singleTaskRunning", 1, 0);
			GUITools.enableMainFrameInteraction();
			System.gc();
		}
	}

	/**
	 * This method executes the operator of this task on each element
	 * separately.
	 * <p>
	 * The number of total output elements is equal to the number of source
	 * elements but an operator may produce more than one result. Thus, a
	 * <code>List&lt;ArrayList&lt;IqmDataBox&gt;&gt;</code> is generated.
	 * 
	 * @return all list of all processed results, images at index 1, plots at
	 *         index 2, tables at index 3, custom results at index 4
	 * @throws Exception
	 *             if anything goes wrong during processing
	 */
	private List<ArrayList<IqmDataBox>> executeDefaultRoutine()
			throws Exception {

		this.setProgress(0);

		// ##############################################################
		// ACQUIRE AND DETERMINE SOURCE OBJECTS
		// get the list of sources to be processed
		// the sources must be a list of IqmDataBox'es in that case
		List<Object> srcs = this.workPackage.getSources();
		if (srcs != null && srcs.get(0) instanceof IqmDataBox) {
			// check how many boxes have been handed over
			nStackItems = this.workPackage.getSources().size();
		} else {
			nStackItems = 0;
		}

		// ##############################################################
		// PREPARE RESULT LISTS
		List<ArrayList<IqmDataBox>> resultList = new ArrayList<ArrayList<IqmDataBox>>(
				4);

		ArrayList<IqmDataBox> imageList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> plotList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> tableList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> customList = new ArrayList<IqmDataBox>();

		resultList.add(0, imageList);
		resultList.add(1, plotList);
		resultList.add(2, tableList);
		resultList.add(3, customList);

		// ##############################################################
		// fetch the first item from the indices[] and produce a result
		// run through the stack and process each image with the
		// current operator and parameters
		for (int n = 0; n < nStackItems; n++) {

			BoardPanel.appendTextln("Processing item: " + (n + 1) + "/"
					+ nStackItems);

			// this is the result of one operator execution
			// this result may contain multiple elements of each supported type
			Result singleResult = new Result();

			IqmDataBox source1 = (IqmDataBox) this.workPackage.getSources()
					.get(n);
			// load the source, if it is virtual
			if (source1 instanceof VirtualDataBox) {
				source1 = VirtualDataManager.getInstance().load(
						(IVirtualizable) source1);
			}

			ParameterBlockIQM pbTmp = this.workPackage.getParameters().clone();
			pbTmp.removeSources();
			pbTmp.addSource(source1);

			// we need a clone of the work package that now contains the (loaded)
			// sources in a temporary PB and all computation settings
			WorkPackage wpTmp = (WorkPackage) this.workPackage.clone();
			wpTmp.setOperator(this.getOperator());
			wpTmp.setParameters(pbTmp);
			
			// ##############################################################
			// RUN THE OPERATOR AND PRODUCE RESULT
			SingleProcessingTask task;
			// start a new SingleProcessingTask for EACH item calculation
			if (!this.isHeadless()) {
				task = new SingleProcessingTask(wpTmp, this.getOperatorGUI());
			} else {
				// call single task without GUI (e.g. in scripts)
				task = new SingleProcessingTask(wpTmp);
			}

			// set the progress listener to the single processing task
			task.addPropertyChangeListener(GUITools.getStatusPanel());

			// get result
			// wait for the completion and gather the result
			logger.debug("Waiting for a result from a single worker... ");
			this.firePropertyChange("singleTaskRunning", 0, 1);
			singleResult = (Result) task.processExplicit();
			this.firePropertyChange("singleTaskRunning", 1, 0);

			// OPERATOR IS FINISHED
			// ##############################################################

			// FILL THE RESULT LIST
			resultList = this.addResultsToList(singleResult, resultList);

			// calculate values for the progress bar in the status panel
			int proz = (n + 1) * 100;
			proz = proz / nStackItems;

			// set the stack progress bar in the status panel and inform user
			this.setProgress(proz);
			BoardPanel.appendTextln("Item processed: " + (n + 1) + "/"
					+ nStackItems + " (" + proz + " %)");

		}
		return resultList;
	}

	/**
	 * This method executes the operator of this task in a serial manner within
	 * a single stack.
	 * <p>
	 * The number of processed elements is equal to the number of source
	 * elements, but the items are about to be combined pairwise. Each pair
	 * consists of subsequent items, e.g. <code>[0,1] --&gt; new_1</code>,
	 * <code>[new_1, 2] --&gt; new_2</code>, and so forth. The first element at
	 * position <code>[0]</code> remains unchanged and is also added as first
	 * element in the result list.
	 * <p>
	 * <b>Note: Operators implementing
	 * {@link StackProcessingType#SINGLE_STACK_SEQUENTIAL} cannot be executed by
	 * any parallel {@link AbstractProcessingTask}!</b>
	 * 
	 * @return all processed results
	 * @throws Exception
	 *             if anything goes wrong during processing
	 */
	private List<ArrayList<IqmDataBox>> executeSingleStackSequentialRoutine()
			throws Exception {

		this.setProgress(0);

		// ##############################################################
		// ACQUIRE AND DETERMINE SOURCE OBJECTS
		// get the list of sources to be processed
		// the sources must be a list of IqmDataBox'es in that case
		List<Object> srcs = this.workPackage.getSources();
		if (srcs != null && srcs.get(0) instanceof IqmDataBox) {
			// check how many boxes have been handed over
			nStackItems = this.workPackage.getSources().size();
		} else {
			nStackItems = 0;
		}

		// ##############################################################
		// PREPARE RESULT LISTS
		List<ArrayList<IqmDataBox>> resultList = new ArrayList<ArrayList<IqmDataBox>>(
				4);

		ArrayList<IqmDataBox> imageList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> plotList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> tableList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> customList = new ArrayList<IqmDataBox>();

		resultList.add(0, imageList);
		resultList.add(1, plotList);
		resultList.add(2, tableList);
		resultList.add(3, customList);

		// ##############################################################
		// fetch the first item from the indices[]
		// run through the stack and process each image with the
		// current operator and parameters
		for (int n = 0; n < nStackItems - 1; n++) {

			BoardPanel.appendTextln("Processing item: " + (n + 1) + "/"
					+ nStackItems);

			// the processed result of one operator iteration
			Result result = new Result();
			IqmDataBox source1 = null;
			IqmDataBox source2 = null;

			source1 = (IqmDataBox) this.workPackage.getSources().get(n);
			// load the first source from the disk, if it is virtual
			if (source1 instanceof VirtualDataBox) {
				source1 = VirtualDataManager.getInstance().load(
						(IVirtualizable) source1);
			}

			// add the first item as it is to the list,
			// as it is specified in the
			// StackProcessingType.SINGLE_STACK_SEQUENTIAL
			// clone for new thumbnail generation
			if (n == 0) {
				switch (source1.getDataType()) {
				case IMAGE:
					imageList.add(0, source1 = source1.clone());
					break;
				case PLOT:
					plotList.add(0, source1 = source1.clone());
					break;
				case TABLE:
					tableList.add(0, source1 = source1.clone());
					break;
				case CUSTOM:
					customList.add(0, source1 = source1.clone());
				default:
					break;
				}
			}

			// get the subsequent item from the Tank
			source2 = (IqmDataBox) this.workPackage.getSources().get(n + 1);
			// load the second source from the disk, if it is virtual
			if (source2 instanceof VirtualDataBox) {
				source2 = VirtualDataManager.getInstance().load(
						(IVirtualizable) source2);
			}

			ParameterBlockIQM pbTmp = this.workPackage.getParameters().clone();
			pbTmp.removeSources();
			pbTmp.setSource(source1, 0);
			pbTmp.setSource(source2, 1);

			// we need a clone of the work package that now contains the (loaded)
			// sources in a temporary PB and all computation settings
			WorkPackage wpTmp = (WorkPackage) this.workPackage.clone();
			wpTmp.setOperator(this.getOperator());
			wpTmp.setParameters(pbTmp);
			
			// ##############################################################
			// RUN THE OPERATOR AND PRODUCE RESULT
			SingleProcessingTask task;
			// start a new SingleProcessingTask for EACH item calculation
			if (!this.isHeadless()) {
				task = new SingleProcessingTask(wpTmp, this.getOperatorGUI());
			} else {
				// call single task without GUI (e.g. in scripts)
				task = new SingleProcessingTask(wpTmp);
			}

			// set the progress listener to the single processing task
			task.addPropertyChangeListener(GUITools.getStatusPanel());

			// get result
			// wait for the completion and gather the result
			logger.debug("Waiting for a result from a single worker... ");
			this.firePropertyChange("singleTaskRunning", 0, 1);
			result = (Result) task.processExplicit();
			this.firePropertyChange("singleTaskRunning", 1, 0);

			// OPERATOR IS FINISHED
			// ##############################################################

			// add the single result to the entire result list
			resultList = this.addResultsToList(result, resultList);

			// calculate values for the progress bar in the status panel
			int proz = (n + 1) * 100;
			proz = proz / nStackItems;

			// set the stack progress bar in the status panel and inform user
			this.setProgress(proz);
			BoardPanel.appendTextln("Item processed: " + (n + 1) + "/"
					+ nStackItems + " (" + proz + " %)");

		}
		return resultList;
	}

	/**
	 * This method executes the operator of this task in a serial manner across
	 * two parallel item stacks of the same size.
	 * <p>
	 * The number of processed elements is equal to the number of source
	 * elements, but the items are about to be combined pairwise. Each pair
	 * consists of parallel items in the left and right manager list,
	 * respectively. The first source item at index 0 is taken from the
	 * currently selected manager, and the corresponding second item is taken
	 * from the opposite manager list.
	 * <p>
	 * For instance: <code>[left_0, right_0] --&gt; new_0</code>,
	 * <code>[left_1, right_1] --&gt; new_1</code>, and so forth.
	 * 
	 * 
	 * @return all processed results
	 * @throws Exception
	 *             if anything goes wrong during processing
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<ArrayList<IqmDataBox>> executeMultiStackEvenRoutine()
			throws Exception {
		this.setProgress(0);

		// ##############################################################
		// ACQUIRE AND DETERMINE SOURCE OBJECTS
		// get the list of sources to be processed
		// the sources must be a list of IqmDataBox'es in that case
		List srcs = this.workPackage.getSources();
		if (srcs != null && srcs.get(0) instanceof List) {
			// check how many boxes have been handed over to be processed
			nStackItems = ((List) this.workPackage.getSources().get(0)).size();
		} else {
			nStackItems = 0;
		}

		// ##############################################################
		// PREPARE RESULT LISTS
		List<ArrayList<IqmDataBox>> resultList = new ArrayList<ArrayList<IqmDataBox>>(
				4);

		ArrayList<IqmDataBox> imageList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> plotList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> tableList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> customList = new ArrayList<IqmDataBox>();

		resultList.add(0, imageList);
		resultList.add(1, plotList);
		resultList.add(2, tableList);
		resultList.add(3, customList);

		// ##############################################################
		// fetch the first item from the indices[]
		// run through the stack and process each image with the
		// current operator and parameters
		for (int n = 0; n < nStackItems; n++) {

			BoardPanel.appendTextln("Processing item: " + (n + 1) + "/"
					+ nStackItems);

			// the processed result of one operator iteration
			Result result = new Result();
			IqmDataBox source1 = null;
			IqmDataBox source2 = null;

			List<Object> firstList = (List<Object>) this.workPackage
					.getSources().get(0);
			List<Object> secondList = (List<Object>) this.workPackage
					.getSources().get(1);

			source1 = (IqmDataBox) firstList.get(n);
			// load the first source from the disk, if it is virtual
			if (source1 instanceof VirtualDataBox) {
				source1 = VirtualDataManager.getInstance().load(
						(IVirtualizable) source1);
			}

			source2 = (IqmDataBox) secondList.get(n);
			// load the second source from the disk, if it is virtual
			if (source2 instanceof VirtualDataBox) {
				source2 = VirtualDataManager.getInstance().load(
						(IVirtualizable) source2);
			}

			ParameterBlockIQM pbTmp = this.workPackage.getParameters().clone();
			pbTmp.removeSources();
			pbTmp.setSource(source1, 0);
			pbTmp.setSource(source2, 1);
			
			// we need a clone of the work package that now contains the (loaded)
			// sources in a temporary PB and all computation settings
			WorkPackage wpTmp = (WorkPackage) this.workPackage.clone();
			wpTmp.setOperator(this.getOperator());
			wpTmp.setParameters(pbTmp);

			// ##############################################################
			// RUN THE OPERATOR AND PRODUCE RESULT
			SingleProcessingTask task;
			// start a new SingleProcessingTask for EACH item calculation
			if (!this.isHeadless()) {
				task = new SingleProcessingTask(wpTmp, this.getOperatorGUI());
			} else {
				// call single task without GUI (e.g. in scripts)
				task = new SingleProcessingTask(wpTmp);
			}

			// set the progress listener to the single processing task
			task.addPropertyChangeListener(GUITools.getStatusPanel());

			// get result
			// wait for the completion and gather the result
			logger.debug("Waiting for a result from a single worker... ");
			this.firePropertyChange("singleTaskRunning", 0, 1);
			result = (Result) task.processExplicit();
			this.firePropertyChange("singleTaskRunning", 1, 0);

			// OPERATOR IS FINISHED
			// ##############################################################

			// add the result to the list of all items
			resultList = this.addResultsToList(result, resultList);

			// calculate values for the progress bar in the status panel
			int proz = (n + 1) * 100;
			proz = proz / nStackItems;

			// set the stack progress bar in the status panel and inform user
			this.setProgress(proz);
			BoardPanel.appendTextln("Item processed: " + (n + 1) + "/"
					+ nStackItems + " (" + proz + " %)");

		}
		return resultList;
	}

	/**
	 * This method executes the operator of this task in a serial manner across
	 * one item stack and a single item.
	 * <p>
	 * The number of processed elements is equal to the number of source
	 * elements, but the items are about to be combined pairwise. Each pair
	 * consists of one item in the left and right manager list, respectively.
	 * The stack is processed using the single item for each calculation.
	 * <p>
	 * The first source item at index 0 is taken from the currently selected
	 * manager, and the corresponding second item is taken from the opposite
	 * manager list.
	 * <p>
	 * For instance: <code>[left_0, right_0] --&gt; new_0</code>,
	 * <code>[left_1, right_0] --&gt; new_1</code>,
	 * <code>[left_2, right_0] --&gt; new_2</code>, and so forth.
	 * 
	 * 
	 * @return all processed results
	 * @throws Exception
	 *             if anything goes wrong during processing
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<ArrayList<IqmDataBox>> executeMultiStackOddRoutine()
			throws Exception {
		this.setProgress(0);

		// ##############################################################
		// ACQUIRE AND DETERMINE SOURCE OBJECTS
		// get the list of sources to be processed
		// the sources must be a list of IqmDataBox'es in that case
		List srcs = this.workPackage.getSources();
		if (srcs != null && srcs.get(0) instanceof List) {
			// check how many boxes have been handed over to be processed
			nStackItems = ((List) this.workPackage.getSources().get(0)).size();
		} else {
			nStackItems = 0;
		}

		// ##############################################################
		// PREPARE RESULT LISTS
		List<ArrayList<IqmDataBox>> resultList = new ArrayList<ArrayList<IqmDataBox>>(
				4);

		ArrayList<IqmDataBox> imageList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> plotList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> tableList = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> customList = new ArrayList<IqmDataBox>();

		resultList.add(0, imageList);
		resultList.add(1, plotList);
		resultList.add(2, tableList);
		resultList.add(3, customList);

		// ##############################################################
		// fetch the first item from the indices[]
		// run through the stack and process each image with the
		// current operator and parameters
		for (int n = 0; n < nStackItems; n++) {

			BoardPanel.appendTextln("Processing item: " + (n + 1) + "/"
					+ nStackItems);

			// the processed result of one operator iteration
			Result result = new Result();
			IqmDataBox source1 = null;
			IqmDataBox source2 = null;
			
			List<Object> firstList = (List<Object>) this.workPackage
					.getSources().get(0);
			List<Object> secondList = (List<Object>) this.workPackage
					.getSources().get(1);

			source1 = (IqmDataBox) firstList.get(n);
			// load the first source from the disk, if it is virtual
			if (source1 instanceof VirtualDataBox) {
				source1 = VirtualDataManager.getInstance().load(
						(IVirtualizable) source1);
			}

			source2 = (IqmDataBox) secondList.get(0);
			// load the second source from the disk, if it is virtual
			if (source2 instanceof VirtualDataBox) {
				source2 = VirtualDataManager.getInstance().load(
						(IVirtualizable) source2);
			}

			ParameterBlockIQM pbTmp = this.workPackage.getParameters().clone();
			pbTmp.removeSources();
			pbTmp.setSource(source1, 0);
			pbTmp.setSource(source2, 1);
			
			// we need a clone of the work package that now contains the (loaded)
			// sources in a temporary PB and all computation settings
			WorkPackage wpTmp = (WorkPackage) this.workPackage.clone();
			wpTmp.setOperator(this.getOperator());
			wpTmp.setParameters(pbTmp);

			// ##############################################################
			// RUN THE OPERATOR AND PRODUCE RESULT
			SingleProcessingTask task;
			// start a new SingleProcessingTask for EACH item calculation
			if (!this.isHeadless()) {
				task = new SingleProcessingTask(wpTmp, this.getOperatorGUI());
			} else {
				// call single task without GUI (e.g. in scripts)
				task = new SingleProcessingTask(wpTmp);
			}

			// set the progress listener to the single processing task
			task.addPropertyChangeListener(GUITools.getStatusPanel());

			// get result
			// wait for the completion and gather the result
			logger.debug("Waiting for a result from a single worker... ");
			this.firePropertyChange("singleTaskRunning", 0, 1);
			result = (Result) task.processExplicit();
			this.firePropertyChange("singleTaskRunning", 1, 0);

			// OPERATOR IS FINISHED
			// ##############################################################

			// add the results to the list
			resultList = this.addResultsToList(result, resultList);

			// calculate values for the progress bar in the status panel
			int proz = (n + 1) * 100;
			proz = proz / nStackItems;

			// set the stack progress bar in the status panel and inform user
			this.setProgress(proz);
			BoardPanel.appendTextln("Item processed: " + (n + 1) + "/"
					+ nStackItems + " (" + proz + " %)");

		}
		return resultList;
	}

	/**
	 * A helper method for filling the final result list from an operator and
	 * for optional writing of the processed items to the hard disk.
	 * <p>
	 * The resultList will contain image results in sequential order at
	 * 
	 * @param singleResult
	 *            the result from one operator iteration
	 * @param resultList
	 *            the {@link List}, where the image, plot, table, or custom
	 *            boxes of the <code>singleResult</code> are about to be added
	 * @return a reference to the result list
	 * 
	 * @throws Exception
	 *             if any error occurs during adding
	 */
	private List<ArrayList<IqmDataBox>> addResultsToList(Result singleResult,
			List<ArrayList<IqmDataBox>> resultList) throws Exception {

		if (!isVirtual()) {
			if (singleResult.hasImages()) {
				List<IqmDataBox> imageResults = singleResult.listImageResults();
				logger.debug("Result has been retrieved and contains "
						+ imageResults.size() + " image(s).");
				ArrayList<IqmDataBox> existingElements = resultList.get(0);
				existingElements.addAll(imageResults);
				resultList.set(0, existingElements);
			}
			if (singleResult.hasPlots()) {
				List<IqmDataBox> plotResults = singleResult.listPlotResults();
				logger.debug("Result has been retrieved and contains "
						+ plotResults.size() + " plot(s).");
				ArrayList<IqmDataBox> existingElements = resultList.get(1);
				existingElements.addAll(plotResults);
				resultList.set(1, existingElements);
			}
			if (singleResult.hasTables()) {
				List<IqmDataBox> tableResults = singleResult.listTableResults();
				logger.debug("Result has been retrieved and contains "
						+ tableResults.size() + " table(s).");
				ArrayList<IqmDataBox> existingElements = resultList.get(2);
				existingElements.addAll(tableResults);
				resultList.set(2, existingElements);
			}
			if (singleResult.hasCustomResults()) {
				List<IqmDataBox> customResults = singleResult
						.listImageResults();
				logger.debug("Result has been retrieved and contains "
						+ customResults.size() + " custom result(s).");
				ArrayList<IqmDataBox> existingElements = resultList.get(3);
				existingElements.addAll(customResults);
				resultList.set(3, existingElements);
			}
		} else {
			if (singleResult.hasImages()) {
				// persist all previous elements, if they are not persisted yet
				List<IqmDataBox> existingElements = resultList.get(0);
				if (!existingElements.isEmpty()
						&& !(existingElements.get(0) instanceof IVirtualizable)) {
					existingElements = VirtualDataManager.getInstance().save(
							existingElements, virtDir);
				}

				List<IqmDataBox> vImageResults = VirtualDataManager
						.getInstance().save(singleResult.listImageResults(),
								virtDir);
				logger.debug("Result has been retrieved, virtualized successfully to ["
						+ virtDir.toString()
						+ "] and contains "
						+ vImageResults.size() + " image(s).");
				existingElements.addAll(vImageResults);
				resultList.set(0, (ArrayList<IqmDataBox>) existingElements);
			}
			if (singleResult.hasPlots()) {
				// persist all previous elements, if they are not persisted yet
				List<IqmDataBox> existingElements = resultList.get(1);
				if (!existingElements.isEmpty()
						&& !(existingElements.get(0) instanceof IVirtualizable)) {
					existingElements = VirtualDataManager.getInstance().save(
							existingElements, virtDir);
				}

				List<IqmDataBox> vPlotResults = VirtualDataManager
						.getInstance().save(singleResult.listPlotResults(),
								virtDir);
				logger.debug("Result has been retrieved, virtualized successfully to ["
						+ virtDir.toString()
						+ "] and contains "
						+ vPlotResults.size() + " plot(s).");
				existingElements.addAll(vPlotResults);
				resultList.set(1, (ArrayList<IqmDataBox>) existingElements);
			}
			if (singleResult.hasTables()) {
				// persist all previous elements, if they are not persisted yet
				List<IqmDataBox> existingElements = resultList.get(2);
				if (!existingElements.isEmpty()
						&& !(existingElements.get(0) instanceof IVirtualizable)) {
					existingElements = VirtualDataManager.getInstance().save(
							existingElements, virtDir);
				}

				List<IqmDataBox> vTableResults = VirtualDataManager
						.getInstance().save(singleResult.listTableResults(),
								virtDir);
				logger.debug("Result has been retrieved, virtualized successfully to ["
						+ virtDir.toString()
						+ "] and contains "
						+ vTableResults.size() + " table(s).");
				existingElements.addAll(vTableResults);
				resultList.set(2, (ArrayList<IqmDataBox>) existingElements);
			}
			if (singleResult.hasCustomResults()) {
				// persist all previous elements, if they are not persisted yet
				List<IqmDataBox> existingElements = resultList.get(3);
				if (!existingElements.isEmpty()
						&& !(existingElements.get(0) instanceof IVirtualizable)) {
					existingElements = VirtualDataManager.getInstance().save(
							existingElements, virtDir);
				}

				List<IqmDataBox> vCustomResults = VirtualDataManager
						.getInstance().save(singleResult.listImageResults(),
								virtDir);
				logger.debug("Result has been retrieved, virtualized successfully to ["
						+ virtDir.toString()
						+ "] and contains "
						+ vCustomResults.size() + " custom result(s).");
				existingElements.addAll(vCustomResults);
				resultList.set(3, (ArrayList<IqmDataBox>) existingElements);
			}
		}
		return resultList;
	}

	@Override
	public List<ArrayList<IqmDataBox>> processExplicit() throws Exception {
		// record the execution to the processing history
		ProcessingHistory.getInstance().add(this.workPackage);

		GUITools.disableMainFrameInteraction();

		// the resulting items after processing shall be stored in a List
		List<ArrayList<IqmDataBox>> resultList = new ArrayList<ArrayList<IqmDataBox>>();

		// check if virtual flag is set and create new directory
		if (this.isVirtual()) {
			virtDir = VirtualDataManager.getNewVirtualDirectory();
			boolean success = virtDir.mkdir();
			if (success) {
				BoardPanel.appendTextln(
						I18N.getMessage("application.virtDirCreated",
								virtDir.toString()), caller);
			} else {
				// cancel processing, if creation fails
				DialogUtil.getInstance().showDefaultErrorMessage(
						I18N.getMessage("application.virtDirCreationFailed",
								virtDir.toString()));
				return null;
			}
		}

		BoardPanel.appendTextln("Serial stack processing is starting... ",
				caller);
		this.startTime = System.currentTimeMillis();

		// get the stack type of the operator's stackprocessing type from the
		// registry
		StackProcessingType spType = OperatorDescriptorFactory
				.createDescriptor(getOperator().getName())
				.getStackProcessingType();

		// execute custom code for each stack processing type
		switch (spType) {
		case SINGLE_STACK_SEQUENTIAL:
			resultList = this.executeSingleStackSequentialRoutine();
			break;

		case MULTI_STACK_EVEN:
			resultList = this.executeMultiStackEvenRoutine();
			break;

		case MULTI_STACK_ODD:
			resultList = this.executeMultiStackOddRoutine();
			break;

		case DEFAULT:
		default:
			resultList = this.executeDefaultRoutine();
			break;
		}

		return resultList;
	}
}
