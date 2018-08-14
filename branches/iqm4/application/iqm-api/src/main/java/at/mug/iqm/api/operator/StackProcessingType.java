package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: StackProcessingType.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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


/**
 * This enumeration contains all possible stack processing types of an
 * {@link IOperator} in IQM.
 * <p>
 * Supported items:
 * <ul>
 * <li>{@link #DEFAULT}
 * <li>{@link #SINGLE_STACK_SEQUENTIAL}
 * <li>{@link #MULTI_STACK_EVEN}
 * <li>{@link #MULTI_STACK_ODD}
 * <li>{@link #SINGLE_STACK_3D_DEFAULT}
 * </ul>
 * 
 * @author Philipp Kainz
 * 
 */
public enum StackProcessingType {
	/**
	 * The enum element for default processing.
	 * <p>
	 * The operator takes single items for processing and does not require any
	 * stack.
	 */
	DEFAULT,
	/**
	 * The enum element indicating that the operator works in single stack mode.
	 * <p>
	 * Operators implementing this stack type will take items in a custom order
	 * from the selected manager and process them.
	 * <p>
	 * For instance, image registration methods are subject to single image
	 * stacks consisting of subsequent images.
	 */
	SINGLE_STACK_SEQUENTIAL,
	/**
	 * The enum element for multiple parallel stack processing.
	 * <p>
	 * Operators implementing this stack type require the same number of items
	 * available in both manager lists. The order of first and second item is
	 * determined by the selection state of the manager right before execution.
	 */
	MULTI_STACK_EVEN,
	/**
	 * The enum element for multiple parallel stack processing.
	 * <p>
	 * Operators implementing this stack type require n items in one manager
	 * list - which will be the sources - and a single item in the other manager
	 * list. The single item will be <b>constant</b> for the entire processing.
	 * The order of first and second item is determined by the selection state
	 * of the manager right before execution, e.g. if template matching requires
	 * one source image and one template, the currently selected manager
	 * contains the source image(s) and the other one contains the template
	 * image.
	 */
	MULTI_STACK_ODD, 
	
	/**
	 * The enum element indicating that the operator works always with the whole image stack.
	 * <p>
	 * Operators implementing this stack type will take all items in order to process the whole stack.
	 * Examples are image processing algorithms in 3D or image stack statistics on a pixel basis. 
	 */
	SINGLE_STACK_3D_DEFAULT
}
