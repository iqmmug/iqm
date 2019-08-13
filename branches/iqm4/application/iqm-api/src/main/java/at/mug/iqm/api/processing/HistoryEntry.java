package at.mug.iqm.api.processing;

/*
 * #%L
 * Project: IQM - API
 * File: HistoryEntry.java
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

import java.util.Date;

import at.mug.iqm.api.operator.IWorkPackage;

/**
 * This class represents a single entry, which keeps track of the parameters and
 * operators executed in a session.
 * 
 * @author Philipp Kainz
 * @since 3.1
 */
public class HistoryEntry {

	private Date executionDate;
	private IWorkPackage workPackage;

	public HistoryEntry() {
	}

	public HistoryEntry(IWorkPackage workPackage) {
		this(new Date(), workPackage);
	}

	public HistoryEntry(Date execDate, IWorkPackage workPackage) {
		this();
		this.executionDate = execDate;
		this.workPackage = workPackage;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setWorkPackage(IWorkPackage workPackage) {
		this.workPackage = workPackage;
	}

	public IWorkPackage getWorkPackage() {
		return workPackage;
	}

	@Override
	public String toString() {
		String s = "";
		s += this.workPackage.getOperator().getType() + "::" + this.workPackage.getOperator().getName() + "\n";
		s += this.workPackage.getParameters().toString();
		return s;
	}
}
