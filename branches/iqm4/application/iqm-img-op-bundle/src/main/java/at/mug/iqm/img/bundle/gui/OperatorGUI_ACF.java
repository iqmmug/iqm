package at.mug.iqm.img.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_ACF.java
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


import java.awt.event.ActionEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.img.bundle.descriptors.IqmOpACFDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2012 11
 */
public class OperatorGUI_ACF extends AbstractImageOperatorGUI  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 842917659929312098L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(OperatorGUI_ACF.class);

	/**
	 * constructor
	 */
	public OperatorGUI_ACF() {
		logger.debug("Now initializing...");
		
		this.setOpName(new IqmOpACFDescriptor().getName());
		
		this.initialize();
		
		this.setTitle("Auto Correlation Function");
		
		this.pack();
	}

	/**
	 * This method updates the GUI, if needed. 
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		// here, it does nothing
	}

	@Override
	public void setParameterValuesToGUI() {
		// THIS GUI DOES NOT DECLARE ANY ELEMENTS,
		// SO THIS METHOD IS EMPTY

	}

	@Override
	public void updateParameterBlock() {
		// THIS GUI DOES NOT DECLARE ANY ELEMENTS,
		// SO THIS METHOD IS EMPTY
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// THIS GUI DOES NOT DECLARE ANY ELEMENTS,
		// SO THIS METHOD IS EMPTY
	}
}// END
