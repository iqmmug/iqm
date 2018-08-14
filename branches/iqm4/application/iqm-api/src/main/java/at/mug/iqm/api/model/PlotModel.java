package at.mug.iqm.api.model;

/*
 * #%L
 * Project: IQM - API
 * File: PlotModel.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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

import java.util.Collections;
import java.util.Vector;

/**
 * This class is the data-model for plot data, which is stored in the tank. This
 * model can be serialized within an {@link IqmDataBox}.
 * 
 * @author Michael Mayrhofer-R., Philipp Kainz
 * @since 2012 10 24
 * 
 */
public class PlotModel extends AbstractDataModel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -4455157891348409410L;

	/**
	 * The variable for the data header (y-axis).
	 */
	private String dataHeader;
	/**
	 * The variable for the data unit.
	 */
	private String dataUnit;
	/**
	 * The variable for the data values.
	 */
	private Vector<Double> data;
	/**
	 * The variable for the domain header (x-axis).
	 */
	private String domainHeader;
	/**
	 * The variable for the domain units.
	 */
	private String domainUnit;
	/**
	 * The variable for the domain values.
	 */
	private Vector<Double> domain;

	/**
	 * Standard Constructor. Any values for the variables have to be set
	 * manually through setter-methods.
	 */
	public PlotModel() {
		this.setModelName("PlotModel");
	}

	/**
	 * Directly construct a new {@link PlotModel}.
	 * 
	 * @param domainHeader
	 *            The header of the x-axis. May be <code>null</code>.
	 * @param domainUnit
	 *            The units of the x-axis. May be <code>null</code>.
	 * @param dataHeader
	 *            The header of the y-axis. May be <code>null</code>.
	 * @param dataUnit
	 *            The units of the y-axis. May be <code>null</code>.
	 * @param domain
	 *            The values for the x-axis. Must not be <code>null</code>.
	 * @param data
	 *            The values for the y-axis. Must not be <code>null</code>.
	 */
	public PlotModel(String domainHeader, String domainUnit, String dataHeader,
			String dataUnit, Vector<Double> domain, Vector<Double> data) {
		this();
		this.setDomainHeader(domainHeader);
		this.setDomainUnit(domainUnit);
		this.setDataHeader(dataHeader);
		this.setDataUnit(dataUnit);
		this.setDomain(domain);
		this.setData(data);
	}

	/**
	 * Directly construct a new {@link PlotModel}.
	 * 
	 * @param name
	 *            The name of the model. Must not be <code>null</code>.
	 * @param domainHeader
	 *            The header of the x-axis. May be <code>null</code>.
	 * @param domainUnit
	 *            The units of the x-axis. May be <code>null</code>.
	 * @param dataHeader
	 *            The header of the y-axis. May be <code>null</code>.
	 * @param dataUnit
	 *            The units of the y-axis. May be <code>null</code>.
	 * @param domain
	 *            The values for the x-axis. Must not be <code>null</code>.
	 * @param data
	 *            The values for the y-axis. Must not be <code>null</code>.
	 */
	public PlotModel(String name, String domainHeader, String domainUnit,
			String dataHeader, String dataUnit, Vector<Double> domain,
			Vector<Double> data) {
		this.setModelName(name);
		this.setDomainHeader(domainHeader);
		this.setDomainUnit(domainUnit);
		this.setDataHeader(dataHeader);
		this.setDataUnit(dataUnit);
		this.setDomain(domain);
		this.setData(data);
	}

	/**
	 * Get the values for the plot's range (y-coordinates).
	 * <p>
	 * <b>Optional</b>. Header description of the data (i.e. the y-axis): e.g.
	 * "velocity" or "v".
	 */
	public String getDataHeader() {
		return dataHeader;
	}

	/**
	 * Set the header for the plot's range (y-coordinates).
	 * <p>
	 * <b>Optional</b>. Header description of the data (i.e. the y-axis): e.g.
	 * "velocity" or "v".
	 * 
	 * @param dataHeader
	 */
	public void setDataHeader(String dataHeader) {
		this.dataHeader = dataHeader;
		setProperty("dataHeader", dataHeader == null ? "N/A" : dataHeader);
	}

	/**
	 * Get the values for the plot's range (y-coordinates).
	 * <p>
	 * <b>Optional</b>. Unit, the data is represented in: e.g. "km/h".
	 */
	public String getDataUnit() {
		return dataUnit;
	}

	/**
	 * Set the unit for the plot's range (y-coordinates).
	 * <p>
	 * <b>Optional</b>. Unit, the data is represented in: e.g. "km/h".
	 * 
	 * @param dataUnit
	 */
	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
		setProperty("dataUnit", dataUnit == null ? "N/A" : dataUnit);
	}

	/**
	 * Get the values for the plot's range (y-coordinates).
	 * <p>
	 * <b>Required</b>. The original series (1 dimensional) data vector in
	 * double precision.
	 */
	public Vector<Double> getData() {
		return data;
	}

	/**
	 * Set the values for the plot's range (y-coordinates).
	 * <p>
	 * <b>Required</b>. The original series (1 dimensional) data vector in
	 * double precision.
	 * 
	 * @param data
	 */
	public void setData(Vector<Double> data) {
		this.data = data;

		setProperty("rangeMin", Collections.min(data));
		setProperty("rangeMax", Collections.max(data));
		setProperty("rangeSize", data.size());
	}

	/**
	 * Get the header description for the plot's domain (x-coordinates).
	 * <p>
	 * <b>Optional</b>. Header description of the domain (i.e. the x-axis): e.g.
	 * "space" or "s".
	 */
	public String getDomainHeader() {
		return domainHeader;
	}

	/**
	 * Set the header description for the plot's domain (x-coordinates).
	 * <p>
	 * <b>Optional</b>. Header description of the domain (i.e. the x-axis): e.g.
	 * "space" or "s".
	 * 
	 * @param domainHeader
	 */
	public void setDomainHeader(String domainHeader) {
		this.domainHeader = domainHeader;
		setProperty("domainHeader", domainHeader == null ? "N/A" : domainHeader);
	}

	/**
	 * Get the unit for the plot's domain (x-coordinates).
	 * <p>
	 * <b>Optional</b>. Unit, the data is represented in: e.g. "km".
	 * 
	 */
	public String getDomainUnit() {
		return domainUnit;
	}

	/**
	 * Set the unit for the plot's domain (x-coordinates).
	 * <p>
	 * <b>Optional</b>. Unit, the data is represented in: e.g. "km".
	 * 
	 * @param domainUnit
	 */
	public void setDomainUnit(String domainUnit) {
		this.domainUnit = domainUnit;
		setProperty("domainUnit", domainUnit == null ? "N/A" : domainUnit);
	}

	/**
	 * Get the values for the plot's domain (x-coordinates).
	 * <p>
	 * <b>Optional</b>. A custom (1-dimensional) domain value vector in double
	 * precision.
	 */
	public Vector<Double> getDomain() {
		return domain;
	}

	/**
	 * Set the values for the plot's domain (x-coordinates).
	 * <p>
	 * <b>Optional</b>. A custom (1-dimensional) domain value vector in double
	 * precision.
	 * 
	 * @param domain
	 */
	public void setDomain(Vector<Double> domain) {
		this.domain = domain;

		setProperty("domainMin", Collections.min(domain));
		setProperty("domainMax", Collections.max(domain));
		setProperty("domainSize", domain.size());
	}

	/**
	 * Creates a linear domain for a given range.
	 * 
	 * @param start start point
	 * @param end end point
	 * @param step the step size 
	 */
	public void createLinearDomain(Number start, Number end, Number step) {
		this.domain = new Vector<Double>();

		int idx = 0;
		for (double i = start.doubleValue(); i <= end.doubleValue(); i += step.doubleValue()) {
			this.domain.add(idx, i);
			idx++;
		}

		setProperty("domainMin", start.doubleValue());
		setProperty("domainMax", end.doubleValue());
		setProperty("domainSize", domain.size());
	}

	@Override
	public PlotModel clone() throws CloneNotSupportedException {
		PlotModel theClone = new PlotModel(modelName, domainHeader, domainUnit,
				dataHeader, dataUnit, domain, data);

		copyProperties(theClone.getProperties());

		return theClone;
	}
}
