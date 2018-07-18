package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractOperator.java
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
import java.beans.PropertyChangeSupport;

import at.mug.iqm.api.events.OperatorProgressEvent;
import at.mug.iqm.api.processing.AbstractProcessingTask;

/**
 * This class is the parent class for all operators in IQM.
 * <p>
 * <code>PlotOperator</code>s and <code>ImageOperator</code>s inherit from this
 * class.
 * 
 * @author Philipp Kainz
 */
public abstract class AbstractOperator implements IOperator {

	/**
	 * The cached unique name of the operator.
	 */
	protected String name = null;

	/**
	 * The type of the operator.
	 */
	protected OperatorType type = null;

	/**
	 * A reference to the task, an operator is executed in.
	 */
	protected AbstractProcessingTask parentTask = null;

	/**
	 * A flag, whether the operator can be cancelled or not. Default is
	 * <code>true</code>.
	 */
	protected boolean isCancelable = true;

	/**
	 * {@link PropertyChangeSupport} for the Operator class. Use this variable
	 * to emit property changes like status updates from 0 to 100%.
	 */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Common initialization method for all operators. This initialization sets
	 * all operators cancelable <code>true</code> per default.
	 */
	protected void initializeOperator() {
		System.out.println("Initializing operator");
		this.setCancelable(true);
	}

	public abstract IResult run(IWorkPackage wp) throws Exception;

	public abstract String getName();

	public abstract OperatorType getType();

	public void setParentTask(AbstractProcessingTask parentTask) {
		this.parentTask = parentTask;
	}

	@Override
	public AbstractProcessingTask getParentTask() {
		return this.parentTask;
	}

	/**
	 * This method should be invoked within long-running operators which shall
	 * be cancelled at certain execution stages.
	 * 
	 * @see ICancelable#isCancelled(AbstractProcessingTask)
	 */
	public boolean isCancelled(AbstractProcessingTask task) {
		try {
			// if no task is submitted, check the parent task
			if (task != null && task.isCancelled()) {
				// System.out.println("Task is cancelled.");
				return true;
			} else {
				// System.out.println("Task is not cancelled.");
				if (this.parentTask != null && this.parentTask.isCancelled()) {
					// System.out.println("Parent task is cancelled.");
					return true;
				} else {
					// System.out.println("Parent task is not cancelled.");
					return false;
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.gc();
			return false;
		}
	}

	/**
	 * @return the isCancelable
	 */
	public boolean isCancelable() {
		return isCancelable;
	}

	/**
	 * @param isCancelable
	 *            the isCancelable to set
	 */
	public void setCancelable(boolean isCancelable) {
		this.isCancelable = isCancelable;
	}

	/**
	 * Get the {@link PropertyChangeSupport} for this object.
	 * 
	 * @return the pcs
	 */
	public PropertyChangeSupport getPcs() {
		if (this.pcs == null) {
			this.pcs = new PropertyChangeSupport(this);
		}
		return this.pcs;
	}

	public void addProgressListener(PropertyChangeListener listener,
			String propertyName) {

		if (propertyName == null) {
			// add the listener to all events
			this.pcs.addPropertyChangeListener(listener);
		} else {
			// check if the listener is already registered to the property.
			PropertyChangeListener[] existinglisteners = this.pcs
					.getPropertyChangeListeners(propertyName);

			// if no listener is registered yet for this property
			if (existinglisteners.length == 0) {
				this.pcs.addPropertyChangeListener(propertyName, listener);
			} else {
				for (PropertyChangeListener pcl : existinglisteners) {
					// add only, if it is not yet registered
					if (pcl != listener) {
						this.pcs.addPropertyChangeListener(propertyName,
								listener);
					}
				}
			}
		}
	}

	public PropertyChangeListener[] getProgressListeners(String propertyName) {
		PropertyChangeListener[] listeners;
		if (propertyName == null) {
			listeners = this.pcs.getPropertyChangeListeners();
		} else {
			listeners = this.pcs.getPropertyChangeListeners(propertyName);
		}
		return listeners;
	}

	public void fireProgressChanged(int progress) {
		this.pcs.firePropertyChange(new OperatorProgressEvent(this, progress));
	}

}
