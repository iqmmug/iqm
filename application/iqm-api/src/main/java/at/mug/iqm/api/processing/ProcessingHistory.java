package at.mug.iqm.api.processing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.mug.iqm.api.operator.IWorkPackage;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractWorkflowProtocol.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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
 * This class represents a record of the subsequent execution of operators.
 * Within the execution framework, each call of an operator is recorded along
 * the parameters passed and enables the reconstruction of complex processing
 * chains.
 * <p>
 * The history is unique in an application context designed as static class.
 * 
 * @author Philipp Kainz
 * @since 3.1
 */
public class ProcessingHistory {

	/**
	 * The list of entries.
	 */
	private List<HistoryEntry> entries = new ArrayList<HistoryEntry>(30);
	/**
	 * The singleton instance.
	 */
	private static ProcessingHistory instance = null;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Private constructor.
	 */
	private ProcessingHistory() {
	}

	/**
	 * Gets the singleton instance of the processing history.
	 * 
	 * @return a reference to the singleton
	 */
	public static ProcessingHistory getInstance() {
		if (ProcessingHistory.instance == null) {
			ProcessingHistory.instance = new ProcessingHistory();
		}
		return ProcessingHistory.instance;
	}

	/**
	 * Gets the entries as a list of {@link HistoryEntry} instances.
	 * 
	 * @return
	 */
	public List<HistoryEntry> getEntries() {
		return this.entries;
	}

	/**
	 * Concatenates all entries to a string representation.
	 * 
	 * @return a String with the entries in reverse chronological order (latest
	 *         execution first)
	 */
	public String listEntries() {
		if (this.entries.isEmpty()){
			return "";
		}
		List<HistoryEntry> entriesRev = entries.subList(0, entries.size());
		Collections.reverse(entriesRev);
		String list = "";
		int n = 0;
		for (HistoryEntry e : entriesRev) {
			list += ++n + " -- " + sdf.format(e.getExecutionDate()) + ": " + e.toString()
					+ "\n";
		}
		return list;
	}

	/**
	 * Adds an entry to the processing history.
	 * 
	 * @param he
	 *            the entry
	 */
	public synchronized void add(HistoryEntry he) {
		this.entries.add(he);
	}

	/**
	 * Adds an entry to the processing history.
	 * 
	 * @param wp
	 *            the work package
	 */
	public synchronized void add(IWorkPackage wp) {
		this.entries.add(new HistoryEntry(wp));
	}
}
