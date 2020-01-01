/*
 * Created on Jun 1, 2005
 * @author Rafael Santos (rafael.santos@lac.inpe.br)
 * 
 * Part of the Java Advanced Imaging Stuff site
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI)
 * 
 * STATUS: Complete.
 * 
 * Redistribution and usage conditions must be done under the
 * Creative Commons license:
 * English: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.en
 * Portuguese: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.pt
 * More information on design and applications are on the projects' page
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI).
 *
 */

package at.mug.iqm.img.bundle.jai.stuff;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: ImageProcessingTask.java
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import at.mug.iqm.api.events.OperatorProgressEvent;
import at.mug.iqm.api.events.handler.IProgressEmitter;
import at.mug.iqm.api.operator.ICancelable;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.api.processing.IProcessingTask;


/**
 * This abstract class represents a image processing task that is executed
 * on its own thread.
 * This class uses the concept of processing task size and position - if
 * one can estimate the size (in steps, for example) of the task, an
 * application could get the position of the processing, i.e. how far we
 * are on the task.
 * 
 * @author Rafael Santos, Philipp Kainz
 */
public abstract class ImageProcessingTask 
extends Thread 
implements IProgressEmitter, ICancelable, IProcessingTask{

	/**
	 * {@link PropertyChangeSupport} for the Operator class.
	 * Use this variable to emit property changes like status updates from 
	 * 0 to 100%.
	 */
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	/**
	 * A flag whether or not the processing task uses temporary hard disk
	 * storage for large data formats.
	 */
	protected boolean virtual;
	
	/**
	 * A reference to the task, an operator is executed in.
	 */
	protected AbstractProcessingTask parentTask = null;

	/**
	 * A flag, whether the operator can be cancelled or not.
	 */
	protected boolean isCancelable;
	
	/**
	 * This is the method (inherited from Thread) which will do the bulk
	 * image processing. It is declared as abstract as a reminder to the
	 * programmer, which must implement it.
	 */
	@Override
	public abstract void run();

	/**
	 * This method returns the size of the image processing task. The size can
	 * be any estimated value measured in any unit (e.g. number of loops or
	 * processed pixels).
	 * This method must be implemented on classes which inherit from this one and its
	 * result should be constant, i.e. not rely on variables or counters that may
	 * change during the execution of the algorithm.
	 * @return the size of the image processing size.
	 */
	public abstract long getSize();

	/**
	 * This method returns the position on the image processing task, i.e. how many
	 * processing steps were already done. The classes that inherits from this one
	 * must implement this method.
	 * @return the position of the task processing.
	 */
	public abstract long getPosition();

	/**
	 * This method returns true if the task is finished. Please notice that
	 * it is not implemented as (position == size) since there may be cases
	 * where the task size is estimated and the position may not be ever
	 * equal to the exact size.
	 * This method must be implemented on classes which inherit from this one.
	 * @return true if the processing task has finished.
	 */
	public abstract boolean isFinished();

	/**
	 * Get the {@link PropertyChangeSupport} for this object.
	 * @return the pcs
	 */
	public PropertyChangeSupport getPcs() {
		if (this.pcs == null){
			this.pcs = new PropertyChangeSupport(this);
		}
		return pcs;
	}

	/**
	 * Set the {@link PropertyChangeSupport} for this object 
	 * (should be done in constructor of the implementing class).
	 * @param pcs the pcs to set
	 */
	public void setPcs(PropertyChangeSupport pcs) {
		this.pcs = pcs;
	}

	public void addProgressListener(PropertyChangeListener listener,
			String propertyName) {

		if (propertyName == null){
			// add the listener to all events
			this.pcs.addPropertyChangeListener(listener);
		}else{
			// check if the listener is already registered to the property.
			PropertyChangeListener[] existinglisteners = this.pcs
					.getPropertyChangeListeners(propertyName);

			// if no listener is registered yet for this property
			if (existinglisteners.length == 0){
				this.pcs.addPropertyChangeListener(propertyName, listener);
			}else{
				for (PropertyChangeListener pcl : existinglisteners) {
					// add only, if it is not yet registered
					if (pcl != listener) {
						this.pcs.addPropertyChangeListener(propertyName, listener);
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

	/**
	 * This method should be invoked within long-running operators which shall
	 * be cancelable.
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

	public boolean isVirtual() {
		return this.virtual;
	}

	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}
	public void setParentTask(AbstractProcessingTask parentTask) {
		this.parentTask = parentTask;
	}

	public AbstractProcessingTask getParentTask() {
		return this.parentTask;
	}
}
