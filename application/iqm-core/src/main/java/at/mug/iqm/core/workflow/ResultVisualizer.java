package at.mug.iqm.core.workflow;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ResultVisualizer.java
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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.swing.SwingUtilities;

 
 

import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.api.plot.charts.ChartType;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.commons.util.table.TableTools;
import at.mug.iqm.core.processing.ExecutorServiceProcessingTask;
import at.mug.iqm.core.processing.SerialProcessingTask;
import at.mug.iqm.gui.TankPanel;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class is responsible for displaying the processed result in the
 * appropriate manner. The result type is determined within the resulting
 * {@link IqmDataBox} object. 
 * 
 * @author Philipp Kainz
 * 
 */
public class ResultVisualizer {
	// class specific logger
	  

	public ResultVisualizer() {
		System.out.println("IQM:  Initializing...");
	}
	
	public void display(Result result){
		System.out.println("IQM:  Displaying results from [" + result.toString() + "].");
		
		// visualize the contents, don't use ELSE IF!
		if (result.hasImages()) {
			ArrayList<IqmDataBox> imageResults = result.listImageResults();

			System.out.println("IQM:  The image list contains " + imageResults.size()
					+ " element" + (imageResults.size() > 1 ? "s" : "")
					+ ".");

			PlanarImage pi = imageResults.get(0).getImage();
			Look.getInstance().setImage(pi);
			Manager.getInstance().setPreviewImage(pi);

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					GUITools.getMainFrame().getItemContent()
							.setSelectedIndex(0);
				}
			});
		}

		if (result.hasPlots()) {
			ArrayList<IqmDataBox> plotResults = result.listPlotResults();
			Iterator<IqmDataBox> plotIterator = plotResults.iterator();

			System.out.println("IQM:  The plot list contains " + plotResults.size()
					+ " element" + (plotResults.size() > 1 ? "s" : "")
					+ ".");

			List<PlotModel> models = new Vector<PlotModel>(
					plotResults.size());

			while (plotIterator.hasNext()) {
				IqmDataBox content = plotIterator.next();
				models.add(content.getPlotModel());
			}

			// sets the plot models as "preview"
			Plot.getInstance().setNewData(models, ChartType.DEFAULT);

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					GUITools.getMainFrame().getItemContent()
							.setSelectedIndex(1);
				}
			});

		}

		if (result.hasTables()) {

			ArrayList<IqmDataBox> tableResults = result.listTableResults();

			System.out.println("IQM:  The table list contains " + tableResults.size()
					+ " element" + (tableResults.size() > 1 ? "s" : "")
					+ ".");

			TableModel tm = TableTools.mergeBoxes(tableResults);

			Table.getInstance().setNewData(tm);

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					GUITools.getMainFrame().getItemContent()
							.setSelectedIndex(2);
				}
			});
		}

		if (result.hasCustomResults()) {

			ArrayList<IqmDataBox> customResults = result
					.listCustomResults();
			Iterator<IqmDataBox> customIterator = customResults.iterator();

			System.out.println("IQM:  The custom list contains " + customResults.size()
					+ " element" + (customResults.size() > 1 ? "s" : "")
					+ ".");

			while (customIterator.hasNext()) {
				IqmDataBox content = customIterator.next();
				String model = (String) content.getCustomContent()
						.getContent()[0];
				Text.getInstance().setNewData(model);
				// TODO continue implementation of the custom data model visualization
			}

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					GUITools.getMainFrame().getItemContent()
							.setSelectedIndex(3);
				}
			});
		}
	}

	/**
	 * Displays the results of an operator or any other
	 * {@link AbstractProcessingTask} generating an {@link IResult}.
	 * 
	 * @param result
	 * @throws NullPointerException if no {@link ExecutionProxy} is running
	 */
	public void displayPreviewResults(Result result) {
		if (result != null) {
			// set the result in the current ExecutionProxy instance
			ExecutionProxy proxy = (ExecutionProxy) ExecutionProxy.getCurrentInstance();
			proxy.displayPreviewResults(result);
		}
	}

	/**
	 * This method adds a {@link List} of {@link IqmDataBox}es to the
	 * {@link TankPanel}.
	 * <p>
	 * Note: This method is called at the end of any
	 * {@link AbstractProcessingTask}, when the new items are about to be added
	 * to the Tank list.
	 * 
	 * @param items
	 *            the {@link List} of {@link IqmDataBox}es
	 */
	public void addResultsToTank(List<IqmDataBox> items) {
		if (items != null && !items.isEmpty()) {
			System.out.println("IQM:  Adding results from a list to the Tank.");
			// add the elements to the tank, according to certain conditions
			switch (items.get(0).getDataType()) {
			case IMAGE:
			case PLOT:
			case CUSTOM:
			case TABLE:
				Tank.getInstance().addNewItems(items);
				break;
			default:
				break;
			}
		}

	}

	/**
	 * This method adds a {@link List} of of {@link ArrayList}s of
	 * {@link IqmDataBox}es to the {@link TankPanel}.
	 * <p>
	 * Note: This method is called at the end of any
	 * {@link SerialProcessingTask}, when the new items are about to be added to
	 * the Tank list.
	 * 
	 * @param items
	 *            the {@link List} of {@link ArrayList}s of {@link IqmDataBox}es
	 */
	public void add2DResultListToTank(List<ArrayList<IqmDataBox>> items) {
		if (items != null && !items.isEmpty()) {
			System.out.println("IQM:  Adding results from a 2D-array-list to the Tank.");

			for (int i = 0; i < items.size(); i++) {
				addResultsToTank(items.get(i));
			}

		}
	}

	/**
	 * This method adds a {@link List} of {@link ArrayList}s of
	 * {@link ArrayList}s of {@link IqmDataBox}es to the {@link TankPanel}.
	 * <p>
	 * Note: This method is called at the end of any
	 * {@link ExecutorServiceProcessingTask}, when the new items are about to be
	 * added to the Tank list.
	 * 
	 * @param items
	 *            the {@link List} of {@link ArrayList}s of {@link ArrayList}s
	 *            of {@link IqmDataBox}es
	 */
	public void add3DResultListToTank(
			List<ArrayList<ArrayList<IqmDataBox>>> items) {
		if (items != null && !items.isEmpty()) {
			System.out.println("IQM:  Adding results from a 3D-array-list to the Tank.");

			try {
				add2DResultListToTank(mergeResults(items));
			} catch (Exception e) {
				System.out.println("IQM Error: "+ 
						"Cannot display results from a 3D-array list, because merge failed."+ e);
			}

		}
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
}
