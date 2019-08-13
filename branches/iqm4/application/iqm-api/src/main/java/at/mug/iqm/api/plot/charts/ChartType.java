package at.mug.iqm.api.plot.charts;

/*
 * #%L
 * Project: IQM - API
 * File: ChartType.java
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


/**
 * This enumeration defines the possible chart types in IQM.
 * <p>
 * According to each enumeration item, a different chart can be constructed.
 * 
 * @author Philipp Kainz
 * 
 */
public enum ChartType {
	/**
	 * The enum element for a histogram plot.
	 */
	IMAGE_HISTOGRAM,
	/**
	 * The enum element for a linear regression plot.
	 */
	LINEAR_REGRESSION,
	/**
	 * The enum element for a vector plot.
	 */
	VECTOR_CHART,
	/**
	 * The enum element for a 2-D line chart.
	 */
	XY_LINE_CHART,
	/**
	 * The enum element for a standard (generic) chart.
	 */
	DEFAULT;

}
