package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ITemplateSupport.java
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


import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.templatemanager.XMLPreferencesManager;


/**
 * This interface enables an implementing class to set a template in the
 * template list.
 * 
 * @author Philipp Kainz
 * 
 */
public interface ITemplateSupport {

	/**
	 * Set the chosen template to the combo box of templates.
	 * 
	 * @param templateName
	 */
	void setChosenTemplate(String templateName);

	/**
	 * Tell the implementing class to fill the combo box with fresh values from
	 * <code>IQMOperatorTemplates.xml</code>.
	 */
	void fillPreferencesBox();

	/**
	 * This method gathers the parameters for an operator and constructs a
	 * {@link ParameterBlockIQM} instance which will be stored as template in
	 * the <code>IQMOperatorTemplates.xml</code> file.
	 * 
	 * @return the {@link ParameterBlockIQM} of a specific operator (may not be
	 *         the default!)
	 */
	ParameterBlockIQM getParameters();

	/**
	 * Gets the name of the operator. This String is queried by the
	 * {@link XMLPreferencesManager} when managing templates for an operator.
	 * 
	 * @return the operator name for this {@link ITemplateSupport}
	 */
	String getOpName();

}
