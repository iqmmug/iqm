package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: OperatorCompletionWaiter.java
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


import java.beans.PropertyChangeEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.AbstractOperatorGUI;
import at.mug.iqm.api.operator.IOperatorGUI;
import at.mug.iqm.api.processing.IExecutionProtocol;


/**
 * Helper class for displaying a window while a background thread (e.g.
 * {@link SwingWorker}) of an operator is working.
 * <p>
 * This class does everything the {@link CompletionWaiter} does, but
 * additionally it re-enables the inputs on an opened operator GUI.
 * 
 * @see IOperatorGUI#enableInputs()
 * @see IOperatorGUI#disableInputs()
 * 
 * 
 * @author Philipp Kainz
 */
public class OperatorCompletionWaiter extends CompletionWaiter {

	public OperatorCompletionWaiter(JDialog dialog) {
		super(dialog);
	}

	public OperatorCompletionWaiter(JFrame frame) {
		super(frame);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if ("state".equals(event.getPropertyName())
				&& SwingWorker.StateValue.DONE == event.getNewValue()) {
			this.enableOperatorGUIInputs();
		}
	}

	/**
	 * A wrapper for {@link IOperatorGUI#enableInputs()}.
	 */
	protected void enableOperatorGUIInputs() {
		// re-enable the inputs on the operator GUI
		IExecutionProtocol protocol = Application.getCurrentExecutionProtocol();
		if (protocol != null) {
			protocol.enableInputs((AbstractOperatorGUI) protocol.getOperatorGUI());
		}
	}
}
