package at.mug.iqm.api.processing;

/*
 * #%L
 * Project: IQM - API
 * File: IProcessingTask.java
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


/**
 * A processing task can be executed in the memory or can temporarily store
 * the processed items to the hard disk. Then, only the current element is 
 * deserialized, processed in memory and subsequently again serialized to the 
 * hard disk.
 * <p> 
 * This is important for stacks of very large data items. 
 * 
 * @author Philipp Kainz
 *
 */
public interface IProcessingTask {

	/**
	 * Returns whether or not the virtual flag has been set at the execution
	 * start of a task.
	 * 
	 * @return <code>true</code> if every item is serialized to the hard disk
	 *         and loaded again on demand, <br><code>false</code> if the entire
	 *         processing is performed within the memory.
	 */
	boolean isVirtual();
	
	AbstractProcessingTask getParentTask();
	
	void setParentTask(AbstractProcessingTask parentTask);

}
