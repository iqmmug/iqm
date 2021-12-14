package at.mug.iqm.core.processing;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ExecutorServiceProcessingTask.java
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

 
 

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
import at.mug.iqm.core.workflow.ResultVisualizer;
import at.mug.iqm.gui.util.GUITools;

/**
 * This task processes items and operators with a given operator in a parallel
 * manner using {@link ExecutorService}.
 * 
 * @author Philipp Kainz
 */
public class ExecutorServiceProcessingTask extends AbstractProcessingTask
implements IExplicitProcessingTask {
	// class specific logger
	private Class<?> caller = ExecutorServiceProcessingTask.class;
	  

	// class variable declaration
	private List<ArrayList<ArrayList<IqmDataBox>>> allResultLists;
	private int nStackItems = 1;
	private ExecutorService service;
	private int nThreads;

	private int nItemsProcessed = 0;

	public ExecutorServiceProcessingTask(IWorkPackage wp, boolean virtual,
			int nThreads) {
		this.setWorkPackage(wp);
		this.setOperator(wp.getOperator());
		this.setVirtual(virtual);
		this.setnThreads(nThreads);
		this.setHeadless(true);
	}

	public ExecutorServiceProcessingTask(IWorkPackage wp, boolean virtual,
			IOperatorGUI opGUI, int nThreads) {
		this.setWorkPackage(wp);
		this.setOperator(wp.getOperator());
		this.setVirtual(virtual);
		this.setnThreads(nThreads);
		this.setHeadless(false);
		this.setOperatorGUI(opGUI);
	}

	/**
	 * Empty default constructor.
	 */
	public ExecutorServiceProcessingTask() {
		super();
	}

	@Override
	protected List<ArrayList<IqmDataBox>> doInBackground() throws Exception {
		return this.processExplicit();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void done() {
		List<ArrayList<IqmDataBox>> resultLists;
		try {
			if (!this.isHeadless()) {
				resultLists = (List<ArrayList<IqmDataBox>>) this.get();

				if (resultLists != null) {
					new ResultVisualizer().add2DResultListToTank(resultLists);

					this.duration = System.currentTimeMillis() - this.startTime;
					TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
					SimpleDateFormat sdf = new SimpleDateFormat();
					sdf.applyPattern("HHH:mm:ss:SSS");
					BoardPanel.appendTextln(
							"processImageStackParallelExecuter finished, elapsed time: "
									+ sdf.format(this.duration), caller);
					float sec = this.duration / 1000F;
					float min = this.duration / (60 * 1000F);
					float hour = this.duration / (60 * 60 * 1000F);
					float day = this.duration / (24 * 60 * 60 * 1000F);
					BoardPanel.appendTextln(
							"processImageStackParallelExecuter finished, elapsed time: "
									+ day + "d/" + hour + "h/" + min + "m/"
									+ sec + "s", caller);
				} else {
					BoardPanel
					.appendTextln("Result is NULL, nothing to display.");
				}
				// TODO close the GUI if required here
			}
		} catch (ExecutionException e) {
			// log the error message
			System.out.println("IQM Error: An error occurred: " + e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			// log the error message
			System.out.println("IQM Error: An error occurred: " + e);
			e.printStackTrace();

		} catch (Exception e) {
			// log the error message
			System.out.println("IQM Error: An error occurred: " + e);
			e.printStackTrace();
		} finally {
			// reset progress bar and collect garbage
			setProgress(0);
			this.firePropertyChange("singleTaskRunning", 1, 0);
			GUITools.enableMainFrameInteraction();
			System.gc();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.mug.iqm.core.processing.task.AbstractProcessingTask#process(java.util
	 * .List)
	 */
	@Override
	protected void process(List<Object> chunks) {
	}

	/**
	 * @return the nItemsProcessed
	 */
	public int getNItemsProcessed() {
		return nItemsProcessed;
	}

	public synchronized void increaseNItemsProcessed() {
		this.nItemsProcessed++;
	}

	public synchronized void decreaseNItemsProcessed() {
		this.nItemsProcessed--;
	}

	/**
	 * @return the nThreads
	 */
	public int getnThreads() {
		return nThreads;
	}

	/**
	 * @param nThreads
	 *            the nThreads to set
	 */
	public void setnThreads(int nThreads) {
		this.nThreads = nThreads;
	}

	/**
	 * This method executes the operator of this task on each element
	 * separately.
	 * <p>
	 * The number of processed elements is equal to the number of source
	 * elements.
	 * 
	 * @return all processed results
	 * @throws Exception
	 *             if anything goes wrong during processing
	 */
	private List<ArrayList<ArrayList<IqmDataBox>>> executeDefaultRoutine()
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
		// PREPARE RESULT LISTS, 3-dimensional list
		// 1st list: for each item to be processed (must be set by the
		// processing thread) --> class member list
		// 2nd list: type lists from an operator (0 -> images; 1 -> plots; 2 ->
		// tables; 3 -> custom results)
		// 3rd list (innermost): image, plot, table, custom data boxes
		// preserve space for the result vector
		// by adding an empty lists nStackItems-1 times
		for (int n = 0; n < nStackItems; n++) {
			ArrayList<ArrayList<IqmDataBox>> resultList = new ArrayList<ArrayList<IqmDataBox>>(
					4);

			ArrayList<IqmDataBox> imageList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> plotList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> tableList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> customList = new ArrayList<IqmDataBox>();

			resultList.add(0, imageList);
			resultList.add(1, plotList);
			resultList.add(2, tableList);
			resultList.add(3, customList);

			allResultLists.add(n, resultList);
		}

		// ##############################################################
		// run through the stack and process each image with the
		// current operator and parameters
		for (int n = 0; n < nStackItems; n++) {

			BoardPanel.appendTextln("Processing item: " + (n + 1) + "/"
					+ nStackItems);

			IqmDataBox source = (IqmDataBox) this.workPackage.getSources().get(n);
			// load the source, if it is virtual
			if (source instanceof VirtualDataBox) {
				source = VirtualDataManager.getInstance().load(
						(IVirtualizable) source);
			}

			ParameterBlockIQM pbTmp = this.workPackage.getParameters().clone();
			pbTmp.removeSources();
			pbTmp.addSource(source);
			
			// we need a clone of the work package that now contains the (loaded)
			// sources in a temporary PB and all computation settings
			WorkPackage wpTmp = (WorkPackage) this.workPackage.clone();
			wpTmp.setOperator(this.getOperator());
			wpTmp.setParameters(pbTmp);

			// define a worker thread for the n-th item
			ExecutorServiceWorkerThread worker = new ExecutorServiceWorkerThread(wpTmp, n);

			// execute the thread (i.e. add it to the service's scheduler
			// to be executed by one of the pool threads)
			try {
				this.firePropertyChange("singleTaskRunning", 0, 1);
				service.execute(worker);
			} catch (RejectedExecutionException ree) {
				System.out.println("IQM Error: An error occurred: " + ree);
			} catch (Exception e) {
				System.out.println("IQM Error: An error occurred: " + e);
			}

		}

		service.shutdown();
		service.awaitTermination(1000, TimeUnit.DAYS);
		this.firePropertyChange("singleTaskRunning", 1, 0);

		return allResultLists;
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
	private List<ArrayList<ArrayList<IqmDataBox>>> executeMultiStackEvenRoutine()
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
		// PREPARE RESULT LISTS, 3-dimensional list
		// 1st list: for each item to be processed (must be set by the
		// processing thread) --> class member list
		// 2nd list: type lists from an operator (0 -> images; 1 -> plots; 2 ->
		// tables; 3 -> custom results)
		// 3rd list (innermost): image, plot, table, custom data boxes
		// preserve space for the result vector
		// by adding an empty lists nStackItems-1 times
		for (int n = 0; n < nStackItems; n++) {
			ArrayList<ArrayList<IqmDataBox>> resultList = new ArrayList<ArrayList<IqmDataBox>>(
					4);

			ArrayList<IqmDataBox> imageList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> plotList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> tableList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> customList = new ArrayList<IqmDataBox>();

			resultList.add(0, imageList);
			resultList.add(1, plotList);
			resultList.add(2, tableList);
			resultList.add(3, customList);

			allResultLists.add(n, resultList);
		}

		// ##############################################################
		// run through the stack and process each image with the
		// current operator and parameters
		for (int n = 0; n < nStackItems; n++) {

			BoardPanel.appendTextln("Processing item: " + (n + 1) + "/"
					+ nStackItems);

			// the processed result of one operator iteration
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
			// RUN THE OPERATOR AND PRODUCE RESULTS
			// define a worker thread for the n-th item
			ExecutorServiceWorkerThread worker = new ExecutorServiceWorkerThread(wpTmp, n);

			// execute the thread (i.e. add it to the service's scheduler
			// to be executed by one of the pool threads)
			try {
				this.firePropertyChange("singleTaskRunning", 0, 1);
				service.execute(worker);

			} catch (RejectedExecutionException ree) {
				System.out.println("IQM Error: An error occurred: " + ree);
			} catch (Exception e) {
				System.out.println("IQM Error: An error occurred: " + e);
			}

			// OPERATOR IS FINISHED
			// ##############################################################

		}

		service.shutdown();
		service.awaitTermination(1000, TimeUnit.DAYS);
		this.firePropertyChange("singleTaskRunning", 1, 0);

		return allResultLists;
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
	private List<ArrayList<ArrayList<IqmDataBox>>> executeMultiStackOddRoutine()
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
		// PREPARE RESULT LISTS, 3-dimensional list
		// 1st list: for each item to be processed (must be set by the
		// processing thread) --> class member list
		// 2nd list: type lists from an operator (0 -> images; 1 -> plots; 2 ->
		// tables; 3 -> custom results)
		// 3rd list (innermost): image, plot, table, custom data boxes
		// preserve space for the result vector
		// by adding an empty lists nStackItems-1 times
		for (int n = 0; n < nStackItems; n++) {
			ArrayList<ArrayList<IqmDataBox>> resultList = new ArrayList<ArrayList<IqmDataBox>>(
					4);

			ArrayList<IqmDataBox> imageList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> plotList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> tableList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> customList = new ArrayList<IqmDataBox>();

			resultList.add(0, imageList);
			resultList.add(1, plotList);
			resultList.add(2, tableList);
			resultList.add(3, customList);

			allResultLists.add(n, resultList);
		}

		// ##############################################################
		// run through the stack and process each image with the
		// current operator and parameters
		for (int n = 0; n < nStackItems; n++) {

			BoardPanel.appendTextln("Processing item: " + (n + 1) + "/"
					+ nStackItems);

			// the processed result of one operator iteration
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
			// RUN THE OPERATOR AND PRODUCE RESULTS
			// define a worker thread for the n-th item
			ExecutorServiceWorkerThread worker = new ExecutorServiceWorkerThread(wpTmp, n);

			// execute the thread (i.e. add it to the service's scheduler
			// to be executed by one of the pool threads)
			try {
				this.firePropertyChange("singleTaskRunning", 0, 1);
				service.execute(worker);

			} catch (RejectedExecutionException ree) {
				System.out.println("IQM Error: An error occurred: " + ree);
			} catch (Exception e) {
				System.out.println("IQM Error: An error occurred: " + e);
			}

			// OPERATOR IS FINISHED
			// ##############################################################

		}

		service.shutdown();
		service.awaitTermination(1000, TimeUnit.DAYS);
		this.firePropertyChange("singleTaskRunning", 1, 0);

		return allResultLists;
	}

	/**
	 * Merge all results to a list of 4 {@link ArrayList}s.
	 * <p>
	 * The following list gives an overview of the indices and their content:
	 * <ul>
	 * <li>index 0: images
	 * <li>index 1: plots
	 * <li>index 2: tables
	 * <li>index 3: customs
	 * </ul>
	 * If an operator produces more than one output item, all result items of
	 * subsequent operator executions are added to the end of the corresponding
	 * array list index.
	 * <p>
	 * For example: an operator produces 3 images only and there is a stack of 2
	 * sources to be processed. The list at index 1 will contain the following
	 * images in order:
	 * 
	 * <pre>
	 * images[0] = source0_image0
	 * images[1] = source0_image1
	 * images[2] = source0_image2
	 * images[3] = source1_image0
	 * images[4] = source1_image1
	 * images[5] = source1_image2
	 * </pre>
	 * 
	 * @param allResults
	 *            all results from parallel processing
	 * @return a list of merged results containing images at index 0, plots at
	 *         index 1, tables at index 2 and customs at index 3
	 * 
	 * @throws Exception
	 *             if anything goes wrong
	 */
	protected synchronized List<ArrayList<IqmDataBox>> mergeResults(
			List<ArrayList<ArrayList<IqmDataBox>>> allResults) throws Exception {

		List<ArrayList<IqmDataBox>> mergedResults = new ArrayList<ArrayList<IqmDataBox>>(
				4);

		// take all image results and merge it to index 0
		ArrayList<IqmDataBox> mergedImageResults = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> mergedPlotResults = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> mergedTableResults = new ArrayList<IqmDataBox>();
		ArrayList<IqmDataBox> mergedCustomResults = new ArrayList<IqmDataBox>();

		// run through every result and collect the items
		for (int i = 0; i < allResults.size(); i++) {
			ArrayList<ArrayList<IqmDataBox>> singleResult = allResults.get(i);
			mergedImageResults.addAll(singleResult.get(0));
			mergedPlotResults.addAll(singleResult.get(1));
			mergedTableResults.addAll(singleResult.get(2));
			mergedCustomResults.addAll(singleResult.get(3));
		}

		mergedResults.add(0, mergedImageResults);
		mergedResults.add(1, mergedPlotResults);
		mergedResults.add(2, mergedTableResults);
		mergedResults.add(3, mergedCustomResults);

		return mergedResults;
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
	 */
	private ArrayList<ArrayList<IqmDataBox>> addResultsToList(
			Result singleResult, ArrayList<ArrayList<IqmDataBox>> resultList,
			File virtDir) throws Exception {

		if (!isVirtual()) {
			if (singleResult.hasImages()) {
				List<IqmDataBox> imageResults = singleResult.listImageResults();
				System.out.println("IQM:  Result has been retrieved and contains "
						+ imageResults.size() + " image(s).");
				ArrayList<IqmDataBox> existingElements = resultList.get(0);
				existingElements.addAll(imageResults);
				resultList.set(0, existingElements);
			}
			if (singleResult.hasPlots()) {
				List<IqmDataBox> plotResults = singleResult.listPlotResults();
				System.out.println("IQM:  Result has been retrieved and contains "
						+ plotResults.size() + " plot(s).");
				ArrayList<IqmDataBox> existingElements = resultList.get(1);
				existingElements.addAll(plotResults);
				resultList.set(1, existingElements);
			}
			if (singleResult.hasTables()) {
				List<IqmDataBox> tableResults = singleResult.listTableResults();
				System.out.println("IQM:  Result has been retrieved and contains "
						+ tableResults.size() + " table(s).");
				ArrayList<IqmDataBox> existingElements = resultList.get(2);
				existingElements.addAll(tableResults);
				resultList.set(2, existingElements);
			}
			if (singleResult.hasCustomResults()) {
				List<IqmDataBox> customResults = singleResult
						.listImageResults();
				System.out.println("IQM:  Result has been retrieved and contains "
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
				System.out.println("IQM:  Result has been retrieved, virtualized successfully to ["
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
				System.out.println("IQM:  Result has been retrieved, virtualized successfully to ["
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
				System.out.println("IQM:  Result has been retrieved, virtualized successfully to ["
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
				System.out.println("IQM:  Result has been retrieved, virtualized successfully to ["
						+ virtDir.toString()
						+ "] and contains "
						+ vCustomResults.size() + " custom result(s).");
				existingElements.addAll(vCustomResults);
				resultList.set(3, (ArrayList<IqmDataBox>) existingElements);
			}
		}
		return resultList;
	}

	protected synchronized File createVirtDir() {
		File virtualDirectory = VirtualDataManager.getNewVirtualDirectory();
		boolean success = virtualDirectory.mkdir();
		if (success) {
			BoardPanel.appendTextln("Created new directory for virtual files: "
					+ virtualDirectory.toString(), caller);
			return virtualDirectory;
		} else {
			// cancel processing, if creation fails
			BoardPanel.appendTextln("Couldn't create new directory: "
					+ virtualDirectory.toString(), caller);
			return null;
		}
	}

	// inline class start
	class ExecutorServiceWorkerThread implements Runnable {
		private final int nn;
		private final IWorkPackage wp;

		public ExecutorServiceWorkerThread(final IWorkPackage wp, final int n) {
			this.wp = wp;
			this.nn = n;
		}

		@Override
		public void run() {
			File virtualDirectory = null;
			// check if virtual flag is set and create new directory
			if (virtual) {
				virtualDirectory = createVirtDir();
			}

			BoardPanel.appendTextln("Parallel Stack Processing #" + (nn + 1)
					+ " runs in thread: " + Thread.currentThread().getName(),
					caller);

			SingleProcessingTask task;

			if (!headless) {
				task = new SingleProcessingTask(wp, operatorGUI);
			} else {
				// call single task without GUI (e.g. in scripts)
				task = new SingleProcessingTask(wp);
			}

			// set the progress listener to the single processing task
			task.addPropertyChangeListener(GUITools.getStatusPanel());

			// get the result
			Result singleTaskResult = null;
			try {
				singleTaskResult = (Result) task.processExplicit();
			} catch (Exception e) {
				// log the error message
				System.out.println("IQM Error: An error occurred: " + e);
				return;
			}

			// ##############################################################
			// PREPARE RESULT LISTS
			ArrayList<ArrayList<IqmDataBox>> resultList = new ArrayList<ArrayList<IqmDataBox>>(
					4);

			ArrayList<IqmDataBox> imageList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> plotList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> tableList = new ArrayList<IqmDataBox>();
			ArrayList<IqmDataBox> customList = new ArrayList<IqmDataBox>();

			resultList.add(0, imageList);
			resultList.add(1, plotList);
			resultList.add(2, tableList);
			resultList.add(3, customList);

			// single task result contains images, plots, tables, customs
			// add them to each single list
			ArrayList<ArrayList<IqmDataBox>> resultCollection;
			try {
				resultCollection = addResultsToList(singleTaskResult,
						resultList, virtualDirectory);
			} catch (Exception e) {
				System.out.println("IQM Error: Error on parallel execution thread: "
						+ Thread.currentThread().getName());
				DialogUtil
				.getInstance()
				.showErrorMessage(
						I18N.getMessage("application.parallelProcessing.error"),
						e, true);
				return;
			}

			// allResultLists.add(nn, ob); // not thread safe!!!!!!!!!!!!!!!!
			// allResultLists.set(nn, ob); // thread safe!!!!!!!!!!!!!!!!!!!!
			allResultLists.set(nn, resultCollection);

			// increase the image counter
			increaseNItemsProcessed();
			int a = getNItemsProcessed();
			// calculate the progress bar values and set the bar
			int proz = a * 100 / (nStackItems);
			setProgress(proz);

			BoardPanel.appendTextln(Thread.currentThread().getName()
					+ ": image# " + (nn + 1) + " processed: " + a + "/"
					+ nStackItems + " (" + proz + " %)", caller);
		}
	}

	// inline class end

	@Override
	public List<ArrayList<IqmDataBox>> processExplicit() throws Exception {
		// record the execution to the processing history
		ProcessingHistory.getInstance().add(this.workPackage);

		GUITools.disableMainFrameInteraction();
		allResultLists = new ArrayList<ArrayList<ArrayList<IqmDataBox>>>();

		BoardPanel.appendTextln("Parallel Execution Task is starting...",
				caller);
		this.startTime = System.currentTimeMillis();

		// determine the number of threads to use for processing
		BoardPanel.appendTextln(
				"Number of available threads: " + getnThreads(), caller);

		// create a new executor service with a fixed thread pool
		service = Executors.newFixedThreadPool(getnThreads());

		// RUN ALL ROUTINES
		// get the stack type of the operator's name from the registry
		StackProcessingType spType = OperatorDescriptorFactory
				.createDescriptor(getOperator().getName())
				.getStackProcessingType();

		// execute custom code for each stack processing type
		switch (spType) {
		case SINGLE_STACK_SEQUENTIAL:
			DialogUtil
			.getInstance()
			.showDefaultInfoMessage(
					I18N.getMessage("application.parallelProcessing.useSerialInstead"));
			break;

		case MULTI_STACK_EVEN:
			allResultLists = this.executeMultiStackEvenRoutine();
			break;

		case MULTI_STACK_ODD:
			allResultLists = this.executeMultiStackOddRoutine();
			break;

		case DEFAULT:
		default:
			allResultLists = this.executeDefaultRoutine();
			break;
		}

		return mergeResults(allResultLists);
	}
}
