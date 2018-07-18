package at.mug.iqm.api.model;

/*
 * #%L
 * Project: IQM - API
 * File: TableModel.java
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


import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import at.mug.iqm.commons.util.table.TableTools;

/**
 * 
 * @author Philipp Kainz
 * 
 */
public class TableModel extends DefaultTableModel implements IDataModel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 6145732781990160869L;

	/**
	 * The properties of the {@link TableModel} with an initial capacity of 5
	 * items.
	 */
	protected Hashtable<String, Object> properties = new Hashtable<String, Object>(
			5);

	/**
	 * Default constructor, puts the <code>model_name</code> to the properties
	 * hashtable. Default name is "TableModel".
	 */
	public TableModel() {
		this("TableModel");
	}

	/**
	 * Custom constructor, puts the <code>model_name</code> to the properties
	 * hashtable.
	 * 
	 * @param name
	 *            the name of the model
	 */
	public TableModel(String name) {
		super();
		this.setProperty("model_name", name);
	}

	public TableModel(Object rowData[][], Object columnNames[]) {
		super(rowData, columnNames);	
		updateMetaInfo();
	}

	@Override
	public void addColumn(Object columnName) {
		super.addColumn(columnName);
		updateMetaInfo();
	}
	
	@Override
	public void addColumn(Object columnName, Object[] columnData) {
		super.addColumn(columnName, columnData);
		updateMetaInfo();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addColumn(Object columnName, Vector columnData) {
		super.addColumn(columnName, columnData);
		updateMetaInfo();
	}
	
	@Override
	public void addRow(Object[] rowData) {
		super.addRow(rowData);
		updateMetaInfo();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addRow(Vector rowData) {
		super.addRow(rowData);
		updateMetaInfo();
	}
	
	/**
	 * Updates the properties with the current rows and columns
	 */
	private void updateMetaInfo(){
		this.setProperty("Rows", this.getRowCount());
		this.setProperty("Columns", this.getColumnCount());
	}
	
	/**
	 * @see at.mug.iqm.api.model.IDataModel#getModelName()
	 */
	@Override
	public String getModelName() {
		return (String) this.getProperty("model_name");
	}

	/**
	 * @see at.mug.iqm.api.model.IDataModel#setModelName(java.lang.String)
	 */
	@Override
	public void setModelName(String name) {
		this.setProperty("model_name", name);
	}

	/**
	 * @see at.mug.iqm.api.model.IDataModel#getProperties()
	 */
	@Override
	public Hashtable<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * @see at.mug.iqm.api.model.IDataModel#setProperties(Hashtable)
	 */
	@Override
	public void setProperties(Hashtable<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * @see at.mug.iqm.api.model.IDataModel#getProperty(String)
	 */
	@Override
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	/**
	 * @see at.mug.iqm.api.model.IDataModel#setProperty(String, Object)
	 */
	@Override
	public void setProperty(String key, Object value) {
		this.properties.put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Hashtable<String, Object> copyProperties(Hashtable<String, Object> target) {
		if (target == null){
			target = new Hashtable<String, Object>(
				this.properties.size());
		}

		target = (Hashtable<String, Object>) this.properties.clone();

		return target;
	}
	
	@Override
	public TableModel clone() throws CloneNotSupportedException {
		TableModel theClone = TableTools.convertToTableModel((DefaultTableModel) super.clone());
		
		theClone.setProperties(copyProperties(null));
		
		return theClone;
	}

}
