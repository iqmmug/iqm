package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: DialogUtil.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2021 Helmut Ahammer, Philipp Kainz
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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.gui.IDialogUtil;

/**
 * This class constructs dialogs for user feedbacks and interactions. It uses
 * {@link JOptionPane} as container.
 * 
 * @author Philipp Kainz
 */
public class DialogUtil implements IDialogUtil {

	// Logging variables
	private static final Logger logger = LogManager.getLogger(DialogUtil.class);

	private DialogUtil() {
		Application.setDialogUtil(this);
	}

	/**
	 * Returns the current instance of the {@link DialogUtil} object. It creates
	 * a new instance, if none is present.
	 * 
	 * @return the singleton instance
	 */
	public static IDialogUtil getInstance() {
		IDialogUtil dlgUtil = Application.getDialogUtil();
		if (dlgUtil == null) {
			dlgUtil = new DialogUtil();
		}
		return dlgUtil;
	}

	/**
	 * This method creates an instance of a JOptionPane used to inform the user
	 * about something.
	 * 
	 * @param infoMessage
	 *            - the message to be displayed
	 */
	@Override
	public void showDefaultInfoMessage(String infoMessage) {
		logger.debug("Creating a new default information dialog.");

		JOptionPane
				.showMessageDialog(
						null,
						infoMessage,
						I18N.getGUILabelText("application.dialog.information.generic.title"),
						JOptionPane.INFORMATION_MESSAGE);

		logger.info(infoMessage);
	}

	/**
	 * This method creates an instance of a JOptionPane when a user is warned
	 * about something that requires interaction.
	 * 
	 * @param warnMessage
	 *            - the message to be displayed
	 * @return an integer, indicating the selection of the user: YES, NO,
	 *         CANCEL, CLOSED
	 */
	@Override
	public int showDefaultWarnMessage(String warnMessage) {
		logger.debug("Creating a new default warning dialog.");

		int userSelection = JOptionPane
				.showConfirmDialog(
						null,
						warnMessage,
						I18N.getGUILabelText("application.dialog.warning.generic.title"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);

		logger.warn(warnMessage);

		return userSelection;
	}

	/**
	 * This method creates an instance of a JOptionPane when a user is warned
	 * about something that requires interaction.
	 * 
	 * @param window
	 *            a parent window, or <code>null</code>
	 * @param warnMessage
	 *            the message to be displayed
	 * @param title
	 *            the title of the message
	 * @return an integer, indicating the selection of the user: YES, NO,
	 *         CANCEL, CLOSED
	 */
	@Override
	public int showWarnMessage(Component window, String warnMessage, String title) {
		logger.debug("Creating a new default warning dialog.");

		int userSelection = JOptionPane
				.showConfirmDialog(
						window,
						warnMessage,
						title,
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);

		logger.warn(warnMessage);

		return userSelection;
	}

	/**
	 * This method creates an instance of a JOptionPane used to inform the user
	 * about an error.
	 * 
	 * @param errMessage
	 *            - the message to be displayed
	 */
	@Override
	public void showDefaultErrorMessage(String errMessage) {
		logger.debug("Creating a new default error dialog.");

		JOptionPane.showMessageDialog(null, errMessage,
				I18N.getGUILabelText("application.dialog.error.generic.title"),
				JOptionPane.ERROR_MESSAGE);

		logger.error(errMessage);
	}

	/**
	 * This method creates an instance of a JOptionPane used to inform the user
	 * about an exception.
	 * 
	 * @param errMessage
	 *            - the (additional) message to be displayed
	 * @param exception
	 *            - the exception to be formatted
	 * @param withStackTrace
	 *            - whether or not the stack trace should be shown in the dialog
	 */
	@Override
	public void showErrorMessage(String errMessage, Exception exception,
			boolean withStackTrace) {
		logger.debug("Creating a new error dialog with message and exception.");
		ErrorMessagePanel emp;

		try {
			emp = new ErrorMessagePanel(errMessage, exception, withStackTrace);

			JOptionPane.showMessageDialog(new JFrame(), emp, I18N
					.getGUILabelText("application.dialog.error.generic.title"),
					JOptionPane.ERROR_MESSAGE);
		} catch (NullPointerException e) {
			logger.error("An error occurred: ", e);
		}

		logger.error(exception.getLocalizedMessage());
	}

	/**
	 * This method creates an instance of a JOptionPane used to inform the user
	 * about an exception.
	 * <p>
	 * This is basically a wrapper method for
	 * {@link #showErrorMessage(String, Exception, boolean)}, turning off the
	 * printing of the stack trace to the {@link ErrorMessagePanel}.
	 * 
	 * @param errMessage
	 *            - the (additional) message to be displayed
	 * @param exception
	 *            - the exception to be formatted
	 * 
	 * @see #showErrorMessage(String, Exception, boolean)
	 */
	@Override
	public void showErrorMessage(String errMessage, Exception exception) {
		showErrorMessage(errMessage, exception, false);
	}

	/**
	 * This method creates an instance of a JOptionPane when a user is
	 * questioned about something that requires interaction.
	 * 
	 * @param question
	 *            - the question to be displayed
	 * @return an integer, indicating the selection of the user: YES, NO,
	 *         CANCEL, CLOSED
	 */
	@Override
	public int showDefaultQuestionMessage(String question) {
		logger.debug("Creating a new default question dialog.");

		int userSelection = JOptionPane.showConfirmDialog(null, question, I18N
				.getGUILabelText("application.dialog.question.generic.title"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

		logger.info(question);

		return userSelection;
	}

	/**
	 * This method creates an instance of a JOptionPane when a user is
	 * questioned about something that requires interaction.
	 * 
	 * @param parent
	 *            - the parent window
	 * @param question
	 *            - the question to be displayed
	 * @return an integer, indicating the selection of the user: YES, NO,
	 *         CANCEL, CLOSED
	 */
	@Override
	public int showQuestionMessage(Window parent, String question) {
		logger.debug("Creating a new default question dialog.");

		int userSelection = JOptionPane
				.showConfirmDialog(
						parent,
						question,
						I18N.getGUILabelText("application.dialog.question.generic.title"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);

		logger.info(question);

		return userSelection;
	}
}
