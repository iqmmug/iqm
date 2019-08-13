package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: Result.java
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


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import at.mug.iqm.api.model.CustomDataModel;
import at.mug.iqm.api.model.IDataModel;
import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.model.TableModel;

/**
 * The result is a ready-to-use implementation of {@link IResult}.
 * 
 * @author Philipp Kainz
 * 
 */
public class Result implements IResult {

	protected List<ArrayList<IqmDataBox>> resultRegistry = new Vector<ArrayList<IqmDataBox>>(
			4);

	protected ArrayList<IqmDataBox> imageResults = new ArrayList<IqmDataBox>(2);
	protected ArrayList<IqmDataBox> plotResults = new ArrayList<IqmDataBox>(2);
	protected ArrayList<IqmDataBox> tableResults = new ArrayList<IqmDataBox>(2);
	protected ArrayList<IqmDataBox> customResults = new ArrayList<IqmDataBox>(2);

	public Result() {
		this.resultRegistry.add(0, this.imageResults);
		this.resultRegistry.add(1, this.plotResults);
		this.resultRegistry.add(2, this.tableResults);
		this.resultRegistry.add(3, this.customResults);
	}

	/**
	 * Creates a new <code>Result</code> and adds the item to the correct result
	 * list.
	 * 
	 * @param item
	 */
	public Result(IqmDataBox item) {
		this();
		this.addItem(item);
	}

	/**
	 * Creates a new <code>Result</code> and adds the item to the correct result
	 * list.
	 * 
	 * @param item
	 */
	public Result(IDataModel item) {
		this();
		this.addItem(item);
	}

	/**
	 * Gets the registry of the results.
	 * 
	 * @return a {@link List} of {@link ArrayList}s containing image, plot,
	 *         table and custom results
	 */
	public List<ArrayList<IqmDataBox>> listAllElements() {
		return this.resultRegistry;
	}

	/**
	 * Gets all image results as {@link IqmDataBox}es in this result object.
	 * 
	 * @return the image results, or <code>null</code> if no images are set
	 */
	public ArrayList<IqmDataBox> listImageResults() {
		if (this.imageResults.isEmpty()) {
			return null;
		} else {
			return this.imageResults;
		}
	}

	/**
	 * Gets all plot results as {@link IqmDataBox}es in this result object.
	 * 
	 * @return the plot results, or <code>null</code> if no plots are set
	 */
	public ArrayList<IqmDataBox> listPlotResults() {
		if (this.plotResults.isEmpty()) {
			return null;
		} else {
			return this.plotResults;
		}
	}

	/**
	 * Gets all table results as {@link IqmDataBox}es in this result object.
	 * 
	 * @return the table results
	 */
	public ArrayList<IqmDataBox> listTableResults() {
		if (this.tableResults.isEmpty()) {
			return null;
		} else {
			return this.tableResults;
		}
	}

	/**
	 * Gets all custom results as {@link IqmDataBox}es in this result object.
	 * 
	 * @return the custom results
	 */
	public ArrayList<IqmDataBox> listCustomResults() {
		if (this.customResults.isEmpty()) {
			return null;
		} else {
			return this.customResults;
		}
	}

	/**
	 * Returns whether or not the result contains images.
	 * 
	 * @return <code>true</code>, if images are present, <code>false</code>
	 *         otherwise
	 */
	public boolean hasImages() {
		return !this.imageResults.isEmpty();
	}

	/**
	 * Returns whether or not the result contains plots.
	 * 
	 * @return <code>true</code>, if plots are present, <code>false</code>
	 *         otherwise
	 */
	public boolean hasPlots() {
		return !this.plotResults.isEmpty();
	}

	/**
	 * Returns whether or not the result contains tables.
	 * 
	 * @return <code>true</code>, if tables are present, <code>false</code>
	 *         otherwise
	 */
	public boolean hasTables() {
		return !this.tableResults.isEmpty();
	}

	/**
	 * Returns whether or not the result contains custom results.
	 * 
	 * @return <code>true</code>, if custom results are present,
	 *         <code>false</code> otherwise
	 */
	public boolean hasCustomResults() {
		return !this.customResults.isEmpty();
	}

	/**
	 * Adds an item to the <code>Result</code>.
	 * <p>
	 * The correct {@link ArrayList} for the item is determined by the data type
	 * of the {@link IqmDataBox}.
	 * 
	 * @param item
	 *            the {@link IqmDataBox}
	 * @throws IllegalArgumentException
	 *             if the item is <code>null</code>
	 */
	public void addItem(IqmDataBox item) throws IllegalArgumentException {
		if (item == null) {
			throw new IllegalArgumentException("The item must not be null!");
		}

		List<IqmDataBox> list = null;
		switch (item.getDataType()) {
		case IMAGE:
			list = imageResults;
			break;
		case PLOT:
			list = plotResults;
			break;
		case TABLE:
			list = tableResults;
			break;
		case CUSTOM:
			list = customResults;
			break;
		default:
			return;
		}
		// add the item to the correct list
		list.add(item);
	}

	/**
	 * Adds a {@link IDataModel} item to the <code>Result</code>.
	 * <p>
	 * The correct {@link ArrayList} of the item is determined by the data type
	 * of the {@link IqmDataBox}.
	 * 
	 * @param item
	 * @throws IllegalArgumentException
	 *             if the item is <code>null</code>
	 */
	public void addItem(IDataModel item) throws IllegalArgumentException {
		if (item == null || !(item instanceof IDataModel)) {
			throw new IllegalArgumentException(
					"The item is null or has an unrecognized data model type!");
		}

		// wrap the data model in an IqmDataBox
		IqmDataBox box = null;
		if (item instanceof ImageModel) {
			box = new IqmDataBox((ImageModel) item);
		} else if (item instanceof PlotModel) {
			box = new IqmDataBox((PlotModel) item);
		} else if (item instanceof TableModel) {
			box = new IqmDataBox((TableModel) item);
		} else if (item instanceof CustomDataModel)
			box = new IqmDataBox((CustomDataModel) item);
		else {
			return;
		}

		List<IqmDataBox> list = null;
		switch (box.getDataType()) {
		case IMAGE:
			list = imageResults;
			break;
		case PLOT:
			list = plotResults;
			break;
		case TABLE:
			list = tableResults;
			break;
		case CUSTOM:
			list = customResults;
			break;
		default:
			return;
		}

		// add the box to the correct list
		list.add(box);
	}

	
	@Override
	public boolean isMultiResult() {
		return isMultiResult(this);
	}

	/**
	 * This function checks if the result holds more than one item.
	 * 
	 * @param result
	 *            a {@link IResult} to be tested
	 * @return <code>true</code>, if the result contains more than one item,
	 *         <code>false</code> otherwise
	 */
	public static boolean isMultiResult(final IResult result) {
		boolean isMulti = false;

		// check for multiple entries in one of the result lists
		for (ArrayList<IqmDataBox> entry : result.listAllElements()) {
			if (entry.size() > 1) {
				isMulti = true;
				break;
			}
		}

		// if at least one entry exists in several lists, multi is true
		if (!isMulti) {
			Vector<Boolean> truthVector = new Vector<Boolean>();
			truthVector.add(result.hasImages());
			truthVector.add(result.hasPlots());
			truthVector.add(result.hasTables());
			truthVector.add(result.hasCustomResults());

			int counter = 0;
			for (boolean b : truthVector) {
				if (b == true) {
					counter++;
				}
			}

			if (counter > 1) {
				isMulti = true;
			}
		}

		return isMulti;
	}

}
