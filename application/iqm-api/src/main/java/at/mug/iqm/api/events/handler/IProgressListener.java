package at.mug.iqm.api.events.handler;

/*
 * #%L
 * Project: IQM - API
 * File: IProgressListener.java
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


import java.beans.PropertyChangeListener;

import at.mug.iqm.api.events.OperatorProgressEvent;
import at.mug.iqm.api.processing.AbstractProcessingTask;


/**
 * An interface for classes that are listening to {@link OperatorProgressEvent}s
 * of emitted by a task.
 * <p> 
 * The property name is "<code>operatorProgress</code>", if selective listening is required.
 * 
 * @author Philipp Kainz
 *
 */
public interface IProgressListener extends PropertyChangeListener{
	
	/**
	 * Set the current processing task.
	 * @param task
	 */
	void setProcessingTask(AbstractProcessingTask task);
	
	/**
	 * Get the current processing task.
	 * @return <code>null</code> if no processing task is set
	 */
	AbstractProcessingTask getProcessingTask();
}

