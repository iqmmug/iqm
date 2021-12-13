package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: IAutoPreviewable.java
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


import at.mug.iqm.api.operator.IOperatorGUI;

/**
 * This interface enables an implementing class to be queried, whether or not
 * an "auto preview" is possible on a change of parameters in the {@link IOperatorGUI}.
 * 
 * @author Philipp Kainz
 * 
 */
public interface IAutoPreviewable {
	/**
	 * Determines whether or not a control element for "auto preview" is
	 * selected.
	 * 
	 * @return <code>true</code> if the it is selected,
	 *         <code>false</code> otherwise
	 */
	boolean isAutoPreviewSelected();

}
