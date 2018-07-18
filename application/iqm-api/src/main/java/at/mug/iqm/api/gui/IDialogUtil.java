package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: IDialogUtil.java
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


import java.awt.Component;
import java.awt.Window;

import javax.swing.JOptionPane;

/**
 * @author Philipp Kainz
 *
 */
public interface IDialogUtil {
	
	// these constants have to be questioned when reacting to the 
	// users' choice
	// wrapping the option type constants of the JOptionPane
	public static final int DEFAULT_OPTION = JOptionPane.DEFAULT_OPTION;
	public static final int YES_NO_OPTION = JOptionPane.YES_NO_OPTION;
	public static final int YES_NO_CANCEL_OPTION = JOptionPane.YES_NO_CANCEL_OPTION;
	public static final int OK_CANCEL_OPTION = JOptionPane.OK_CANCEL_OPTION;

	public static final int YES_OPTION = JOptionPane.YES_OPTION;
	public static final int NO_OPTION = JOptionPane.NO_OPTION;
	public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
	public static final int OK_OPTION = JOptionPane.OK_OPTION;
	public static final int CLOSED_OPTION = JOptionPane.CLOSED_OPTION;

	// wrapping the message type constants of the JOptionPane
	public static final int ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE;
	public static final int INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE;
	public static final int WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE;
	public static final int QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE;
	public static final int PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;
	
	int showDefaultQuestionMessage(String question);
	
	void showErrorMessage(String errMessage, Exception exception, boolean withStackTrace);
	
	void showErrorMessage(String errMessage, Exception exception);
	
	void showDefaultErrorMessage(String errMessage);
	
	int showDefaultWarnMessage(String warnMessage);
	
	void showDefaultInfoMessage(String infoMessage);

	int showQuestionMessage(Window parent, String question);

	int showWarnMessage(Component window, String warnMessage, String title);
	
}
