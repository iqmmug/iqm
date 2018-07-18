package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: IResult.java
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

import java.util.ArrayList;
import java.util.List;

import at.mug.iqm.api.model.IDataModel;
import at.mug.iqm.api.model.IqmDataBox;

/**
 * The <code>IResult</code> is produced by the <code>IOperator</code> by
 * processing of an <code>IWorkPackage</code>. Since the output of an
 * <code>IOperator</code> can be heterogeneous (e.g. image AND plot AND table),
 * the results are stored in a {@link List}. Each index position on the list
 * corresponds to a unique data type (see IQM’s DataModel specification). For
 * instance, if ten images are the output of an operator, the list contains an
 * ArrayList of ten <code>IqmDataBox</code>es at index 0.
 * <p>
 * The specified order of result elements in the list is:
 * 
 * <ul>
 * <li>0: images (0…n)
 * <li>1: plots (0…n)
 * <li>2: tables (0…n)
 * <li>3: custom (0…n)
 * <li>&gt;3: currently not used, but extensible for other types
 * </ul>
 * 
 * Furthermore, the <code>IResult</code> declares methods for convenient access
 * to and management of the single result elements.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IResult {

	/**
	 * Gets the registry of the results.
	 * 
	 * @return a {@link List} of {@link ArrayList}s containing image, plot,
	 *         table and custom results
	 */
	List<ArrayList<IqmDataBox>> listAllElements();

	/**
	 * Gets all image results as {@link IqmDataBox}es in this result object.
	 * 
	 * @return the image results, or <code>null</code> if no images are set
	 */
	ArrayList<IqmDataBox> listImageResults();

	/**
	 * Gets all plot results as {@link IqmDataBox}es in this result object.
	 * 
	 * @return the plot results, or <code>null</code> if no plots are set
	 */
	ArrayList<IqmDataBox> listPlotResults();

	/**
	 * Gets all table results as {@link IqmDataBox}es in this result object.
	 * 
	 * @return the table results, or <code>null</code> if no tables are set
	 */
	ArrayList<IqmDataBox> listTableResults();

	/**
	 * Gets all custom results as {@link IqmDataBox}es in this result object.
	 * 
	 * @return the custom results, or <code>null</code> if no custom results are
	 *         set
	 */
	ArrayList<IqmDataBox> listCustomResults();

	/**
	 * Returns whether or not the result contains images.
	 * 
	 * @return <code>true</code>, if images are present, <code>false</code>
	 *         otherwise
	 */
	boolean hasImages();

	/**
	 * Returns whether or not the result contains plots.
	 * 
	 * @return <code>true</code>, if plots are present, <code>false</code>
	 *         otherwise
	 */
	boolean hasPlots();

	/**
	 * Returns whether or not the result contains tables.
	 * 
	 * @return <code>true</code>, if tables are present, <code>false</code>
	 *         otherwise
	 */
	boolean hasTables();

	/**
	 * Returns whether or not the result contains custom results.
	 * 
	 * @return <code>true</code>, if custom results are present,
	 *         <code>false</code> otherwise
	 */
	boolean hasCustomResults();

	/**
	 * Adds an item to the <code>Result</code>.
	 * <p>
	 * The correct {@link ArrayList} of the item is determined by the data type
	 * of the {@link IqmDataBox}.
	 * 
	 * @param item
	 * @throws IllegalArgumentException
	 *             if the item is <code>null</code>
	 */
	void addItem(IqmDataBox item) throws IllegalArgumentException;

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
	void addItem(IDataModel item) throws IllegalArgumentException;

	/**
	 * This function checks if the result holds more than one item.
	 * 
	 * @return <code>true</code>, if the result contains more than one item,
	 *         <code>false</code> otherwise
	 */
	boolean isMultiResult();

}
