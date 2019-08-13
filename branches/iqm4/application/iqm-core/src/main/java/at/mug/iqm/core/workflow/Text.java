package at.mug.iqm.core.workflow;

/*
 * #%L
 * Project: IQM - Application Core
 * File: Text.java
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


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.ITextPanel;
import at.mug.iqm.api.workflow.IText;
import at.mug.iqm.gui.TextPanel;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class is a static accessor for the current {@link ITextPanel}.
 * 
 * @author Philipp Kainz
 * 
 */
public final class Text implements IText {

	// class specific logger
	private static final Logger logger = LogManager.getLogger(Text.class);

	/**
	 * The current {@link TextPanel} instance to control.
	 */
	private ITextPanel textPanel = null;

	private Text() {
		Application.setText(this);
	}

	public static IText getInstance() {
		IText text = Application.getText();
		if (text == null) {
			text = new Text();
		}
		return text;
	}

	/**
	 * Gets the current {@link TextPanel} instance.
	 * 
	 * @return a reference to the current instance
	 */
	@Override
	public ITextPanel getTextPanel() {
		return textPanel;
	}

	/**
	 * Sets the {@link TextPanel} instance to control.
	 * 
	 * @param arg
	 */
	@Override
	public void setTextPanel(ITextPanel arg) {
		textPanel = arg;
	}

	/**
	 * Set the new data to the {@link TextPanel}.
	 * 
	 * @param model
	 *            the string to be displayed
	 */
	@Override
	public void setNewData(String model) {
		logger.debug("Setting new data...");
		textPanel.writeText(model);
		// TODO implement model name display in the title bar
		GUITools.getMainFrame().resetTitleBar();
	}

	/**
	 * Indicates whether or not the {@link TextPanel} currently contains some
	 * content.
	 * 
	 * @see ITextPanel#isEmpty()
	 */
	@Override
	public void reset() {
		this.textPanel.reset();
		
		GUITools.getMainFrame().resetTitleBar();
	}

	@Override
	public String getData() {
		return (String) this.textPanel.getText();
	}
}
