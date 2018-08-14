package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: IStatusPanel.java
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


import javax.swing.JLabel;
import javax.swing.JProgressBar;

import at.mug.iqm.api.events.handler.IProgressListener;
import at.mug.iqm.api.processing.AbstractProcessingTask;

/**
 * This interface declares methods for a GUI element receiving events from
 * operators
 * 
 * @author Philipp Kainz
 * 
 */
public interface IStatusPanel extends IProgressListener {

	/**
	 * @return the AbstractProcessingTask
	 */
	AbstractProcessingTask getProcessingTask();

	/**
	 * @param task
	 *            the AbstractProcessingTask to set
	 */
	void setProcessingTask(AbstractProcessingTask task);

	/**
	 * @return the progressBarSingleOperation
	 */
	JProgressBar getProgressBarSingleOperation();

	/**
	 * @return the progressBarStack
	 */
	JProgressBar getProgressBarStack();

	/**
	 * Resets and hides the progress bar for stack processing. This bar is also
	 * triggered by "non-Operator" progress like opening/saving images.
	 */
	void resetProgressBarValueStack();

	/**
	 * This method paints the progress bar for a single operation with the new
	 * value.
	 * 
	 * @param value
	 *            - the value for the progress bar
	 */
	void setProgressBarValueStack(int value);

	/**
	 * Gets the label for the operator name.
	 * 
	 * @return a {@link JLabel}
	 */
	JLabel getLblOperatorName();

	/**
	 * Hides the I/O processing icon.
	 */
	void hideIOProcessingIcon();

	/**
	 * Displays the I/O processing icon.
	 */
	void showIOProcessingIcon();

}
