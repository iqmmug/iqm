package at.mug.iqm.api.processing;

/*
 * #%L
 * Project: IQM - API
 * File: ExecutionState.java
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


import at.mug.iqm.api.operator.IOperatorValidator;

/**
 * This class defines a list of subsequent execution states.
 * 
 * @author Philipp Kainz
 * 
 */
public final class ExecutionState {

	/**
	 * An undefined state, set to {@link Integer#MIN_VALUE}.
	 * <p>
	 * This state is used, when no other state is applicable.
	 */
	public static final int UNDEFINED = Integer.MIN_VALUE;

	/**
	 * An idle state, code 0.
	 * <p>
	 * The protocol is currently inactive.
	 */
	public static final int IDLE = 0;

	/**
	 * The state right after initialization, code 1.
	 * <p>
	 * All required classes are registered for an execution and new instances
	 * have been constructed.
	 */
	public static final int INITIALIZED = 1;

	/**
	 * The state, if initialization fails, code -1.
	 * <p>
	 * Some or all of the required operator classes cannot be instantiated.
	 */
	public static final int INITIALIZATION_FAILED = -1;

	/**
	 * The state right after work package construction using the default
	 * parameters and selected sources, code 2.
	 * <p>
	 * A work package for the requested operator is finished.
	 */
	public static final int WORKPACKAGE_CONSTRUCTED = 2;

	/**
	 * The state, when the {@link IOperatorValidator} has been initiated and the
	 * validation result has not yet been received, code 3.
	 * <p>
	 * The call of validate in the {@link IOperatorValidator} placed.
	 */
	public static final int VALIDATING_PARAMETERS = 3;

	/**
	 * The state, if the validation of parameters has been successfully
	 * completed, code 4.
	 * <p>
	 * The operator is permitted to be executed on the selected item(s).
	 */
	public static final int VALIDATION_PASSED = 4;

	/**
	 * The state, if the validation of parameters and sources failed, code -3.
	 * <p>
	 * The operator is not permitted to be executed on the selected item(s).
	 */
	public static final int VALIDATION_FAILED = -3;

	/**
	 * The state, when GUI of the operator is visible, code 5.
	 * <p>
	 * The GUI has been constructed and the default values have been set to the
	 * custom GUI control elements.
	 */
	public static final int GUI_VISIBLE = 5;

	/**
	 * The state, when the user updates the sources of the operator and the
	 * validator is running again, code 6.
	 * <p>
	 * The old sources have been replaced by the new ones and the validator
	 * checks, if the sources are permitted.<br>
	 * The parameters remain unchanged.
	 */
	public static final int SOURCES_UPDATING = 6;

	/**
	 * The state, when the user successfully updated the sources of the operator
	 * and the validation has been passed, code 7.
	 * <p>
	 * The new sources are set to the GUI of the operator.
	 */
	public static final int SOURCE_UPDATE_SUCCESSFUL = 7;

	/**
	 * The state, when the source update of the operator fails, code 6.
	 * <p>
	 * The sources are reset to the last known working state.
	 */
	public static final int SOURCE_UPDATE_FAILED = -6;

	/**
	 * The state, when the protocol encountered errors.
	 */
	public static final int ERROR = -99;

	/**
	 * The state, when the operator has been kicked off and the execution
	 * protocol is finished.
	 */
	public static final int FINISHED = Integer.MAX_VALUE;

	/**
	 * Maps a given {@link ExecutionState} constant to its String
	 * representation.
	 * 
	 * @param state one of the constant fields in {@link ExecutionState}
	 * @return the string representation 
	 */
	public static String stateToString(int state) {
		String str = "";

		switch (state) {
		case UNDEFINED:
			str = "UNDEFINED";
			break;
		case INITIALIZED:
			str = "INITIALIZED";
			break;
		case INITIALIZATION_FAILED:
			str = "INITIALIZATION_FAILED";
			break;
		case WORKPACKAGE_CONSTRUCTED:
			str = "WORKPACKAGE_CONSTRUCTED";
			break;
		case VALIDATING_PARAMETERS:
			str = "VALIDATING_PARAMETERS";
			break;
		case VALIDATION_PASSED:
			str = "VALIDATION_PASSED";
			break;
		case VALIDATION_FAILED:
			str = "VALIDATION_FAILED";
			break;
		case GUI_VISIBLE:
			str = "GUI_VISIBLE";
			break;
		case SOURCES_UPDATING:
			str = "SOURCES_UPDATING";
			break;
		case SOURCE_UPDATE_SUCCESSFUL:
			str = "SOURCE_UPDATE_SUCCESSFUL";
			break;
		case SOURCE_UPDATE_FAILED:
			str = "SOURCE_UPDATE_FAILED";
			break;
		case ERROR:
			str = "ERROR";
			break;
		case FINISHED:
			str = "FINISHED";
			break;
		default:
			break;
		}
		return str;
	}

}
