package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: IntNumberVerifier.java
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


import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.apache.log4j.Logger;

/**
 * This class checks whether an input can be parsed as an Integer or not. 
 * @author Philipp Kainz
 * @since   21.03.2012
 */
public class IntNumberVerifier extends InputVerifier { 
	// class specific logger
	private static final Logger logger = Logger.getLogger(IntNumberVerifier.class);

	/**
	 * Overrides the InputVerifier.verify method.
	 * @param input the {@link JComponent} to be verified
	 */
	@Override
	public boolean verify(JComponent input) {
		logger.debug("Verifying an input (JComponent)"); 
		JFormattedTextField ftf = (JFormattedTextField)input;
		JFormattedTextField.AbstractFormatter formatter = ftf.getFormatter();
		if (formatter != null) {
			String text = ftf.getText();
			try {
				text = text.replace(",", ".");
				Integer.valueOf(text);
				//Float.valueOf(text);
				return true;
			} catch (NumberFormatException e) {
				logger.error("String cannot be parsed as an integer! ", e);
				return false;
			}	 
		}
		return true;
	}
}

