package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: ErrorMessagePanel.java
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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

/**
 * This class is responsible for displaying a scrollable {@link JTextArea}
 * containing a {@link Throwable} object. Additionally a custom message can be
 * displayed on top of the {@link Throwable} object.
 * 
 * @author Philipp Kainz
 */
public class ErrorMessagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7871995974986388228L;

	// Logging variables
	private static Class<?> caller = ErrorMessagePanel.class;
	private static final Logger logger = Logger
			.getLogger(ErrorMessagePanel.class);

	private String message;
	private Throwable exception;
	private boolean withStackTrace;
	
	private JScrollPane scrollPaneException;
	private JTextArea textAreaException;
	private JScrollPane scrollPaneMessage;
	private JTextArea textAreaMessage;

	/**
	 * This is the constructor. It returns an instance containing either both
	 * message and exception in a JTextArea, or just one of them. It is
	 * prohibited to
	 * 
	 * @param message
	 *            - the message to be displayed
	 * @param exception
	 *            - the exception to be displayed
	 */
	public ErrorMessagePanel(String message, Throwable exception, boolean withStackTrace) {
		if (message == null && exception == null) {
			throw new NullPointerException(
					"Both message and exception are not allowed to be null!");
		} else {
			this.message = message;
			this.exception = exception;
			this.withStackTrace = withStackTrace;
			this.createAndAssemble();
		}
	}

	/**
	 * Creates the objects and assembles the content's {@link JPanel}.
	 */
	private void createAndAssemble() {
		logger.debug("Creating a new instance of " + caller + "...");
		this.setLayout(new BorderLayout(5, 5));

		Font font = new Font("Courier", Font.PLAIN, 11);

		if (message != null) {
			// create area for custom message
			textAreaMessage = new JTextArea();
			textAreaMessage.setBackground(this.getBackground());
			textAreaMessage.setForeground(Color.black);
			textAreaMessage.setFont(this.getFont());
			textAreaMessage.setText(message);
			textAreaMessage.setEditable(false);
			textAreaMessage.setLineWrap(true);
			textAreaMessage.setWrapStyleWord(true);
			textAreaMessage.setCaretPosition(0);

			// wrap it in a scroll pane
			scrollPaneMessage = new JScrollPane(textAreaMessage);
			// add it to the parent (this)
			this.add(scrollPaneMessage, BorderLayout.NORTH);
		}

		if (exception != null) {
			// construct the textarea for the exception
			// the width determines the width of other components, due
			// to BorderLayout
			textAreaException = new JTextArea(8, 80);
			StringWriter sw = new StringWriter();
			if (withStackTrace){
				exception.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				textAreaException.setText(exceptionAsString);
			}else {
				textAreaException.setText(exception.getLocalizedMessage());
			}
			
			textAreaException.setFont(font);
			textAreaException.setEditable(false);
			textAreaException.setLineWrap(true);
			textAreaException.setWrapStyleWord(true);
			textAreaException.setCaretPosition(0);

			// wrap it in a scroll pane
			scrollPaneException = new JScrollPane(textAreaException);
			// add it to the parent (this)
			this.add(scrollPaneException, BorderLayout.CENTER);
		}

		logger.debug("Done.");
	}

	/**
	 * Testing method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ErrorMessagePanel emp = new ErrorMessagePanel(
				"my msg",
				new IOException(
						"VVVVVVVVVVVVVVVVVVVVVV VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
								+ "RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR"
								+ "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY"
								+ "\n" + "long exception."), true);
		JOptionPane.showMessageDialog(new JFrame(), emp, "Title",
				JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}
}
