package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: OutputStreamInterceptor.java
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


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

/**
 * This class intercepts the output and redirects/copies it to a
 * {@link JTextArea}.
 * 
 * @author Philipp Kainz
 * 
 */
public class OutputStreamInterceptor extends OutputStream {

	/**
	 * A {@link JTextArea} where the output is copied to.
	 */
	private JTextArea textArea;
	/**
	 * A flag, whether or not the output should be copied to the standard
	 * {@link System}.out too.
	 */
	private boolean console;
	/**
	 * The standard {@link PrintStream} of the {@link System}.
	 */
	private PrintStream stdOut;

	/**
	 * The constructor.
	 * 
	 * @param textArea
	 *            a {@link JTextArea} where the output is redirected/copied to.
	 * @param stdOut
	 *            the standard {@link PrintStream} of {@link System}
	 * @param console
	 *            whether or not the system's output should be also printed to
	 *            the console
	 */
	public OutputStreamInterceptor(JTextArea textArea, PrintStream stdOut,
			boolean console) {
		this.textArea = textArea;
		this.stdOut = stdOut;
		this.console = console;
	}

	@Override
	public void write(int b) throws IOException {
		if (console) {
			stdOut.print(String.valueOf((char) b));
		}
		textArea.append(String.valueOf((char) b));
		textArea.setCaretPosition(textArea.getText().length());
	}
}
