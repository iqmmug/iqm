package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractOperatorDescriptor.java
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


import java.util.Arrays;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;

import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.util.Range;

/**
 * This is the abstract operator descriptor of IQM operators. Any descriptor has
 * to inherit from this class.
 * 
 * Note: This class extensively uses the JAI implementation of
 * {@link OperationDescriptor} and extends it by some custom members and
 * methods.
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class AbstractOperatorDescriptor extends OperationDescriptorImpl
		implements IOperatorDescriptor {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 4846356524921288343L;

	/**
	 * The type of the operator.
	 */
	protected OperatorType type = null;

	/**
	 * The type of stack processing.
	 */
	protected StackProcessingType stackProcessingType = StackProcessingType.DEFAULT;

	/**
	 * A list of all data types, this operator can produce.
	 */
	protected final List<DataType> outputTypes;

	protected final String[][] resources;

	protected final int numSources;

	protected final String[] paramNames;
	protected final Class[] paramClasses;
	protected final Object[] paramDefaults;
	protected final Range[] validParamValues;

	/**
	 * This constructor builds an object for both JAI and IQM usage.
	 * 
	 * @param resources
	 *            the resources array
	 * @param supportedModes
	 *            the supported modes, see also
	 *            {@link OperationDescriptorImpl#getSupportedModes()}
	 * @param numSources
	 *            the number of sources
	 * @param paramNames
	 *            the names of the parameters
	 * @param paramClasses
	 *            the classes of the parameters
	 * @param paramDefaults
	 *            the default values of the parameters
	 * @param validParamValues
	 *            valid parameter ranges
	 * @param type
	 *            the type of the operator, see {@link OperatorType}
	 * @param outputTypes
	 *            the {@link DataType}s this operator is able to produce
	 * @param stackProcessingType
	 *            the stack processing type of the operator, see
	 *            {@link StackProcessingType}
	 */
	public AbstractOperatorDescriptor(final String[][] resources,
			String[] supportedModes, int numSources, String[] paramNames,
			Class[] paramClasses, Object[] paramDefaults,
			Range[] validParamValues, OperatorType type,
			DataType[] outputTypes, StackProcessingType stackProcessingType) {
		// create descriptor for JAI registration
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues);

		// register with IQM
		this.resources = resources;
		this.numSources = numSources;
		this.paramNames = paramNames;
		this.paramClasses = paramClasses;
		this.paramDefaults = paramDefaults;
		this.validParamValues = validParamValues;
		this.outputTypes = Arrays.asList(outputTypes);
		this.type = type;
		this.stackProcessingType = stackProcessingType;
	}

	/**
	 * This constructor builds an object for both JAI and IQM usage.
	 * 
	 * @param resources
	 *            the resources array
	 * @param supportedModes
	 *            the supported modes, see also
	 *            {@link OperationDescriptorImpl#getSupportedModes()}
	 * @param numSources
	 *            the number of sources
	 * @param paramNames
	 *            the names of the parameters
	 * @param paramClasses
	 *            the classes of the parameters
	 * @param paramDefaults
	 *            the default values of the parameters
	 * @param validParamValues
	 *            valid parameter ranges
	 * @param type
	 *            the type of the operator, see {@link OperatorType}
	 * @param outputTypes
	 *            the {@link DataType}s this operator is able to produce
	 */
	public AbstractOperatorDescriptor(final String[][] resources,
			String[] supportedModes, int numSources, String[] paramNames,
			Class[] paramClasses, Object[] paramDefaults,
			Range[] validParamValues, OperatorType type, DataType[] outputTypes) {
		// create descriptor for JAI registration
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues);

		// register with IQM
		this.resources = resources;
		this.numSources = numSources;
		this.paramNames = paramNames;
		this.paramClasses = paramClasses;
		this.paramDefaults = paramDefaults;
		this.validParamValues = validParamValues;
		this.type = type;
		this.outputTypes = Arrays.asList(outputTypes);
	}

	/**
	 * This constructor builds an object for IQM use only.
	 * <p>
	 * The super constructor of {@link OperationDescriptorImpl} is overloaded
	 * and the value of <code>supportedModes</code> is overwritten by
	 * 
	 * <pre>
	 * new String[] { &quot;IQMCustom&quot; }
	 * </pre>
	 * 
	 * @param resources
	 *            the resources array
	 * @param numSources
	 *            the number of sources
	 * @param paramNames
	 *            the names of the parameters
	 * @param paramClasses
	 *            the classes of the parameters
	 * @param paramDefaults
	 *            the default values of the parameters
	 * @param validParamValues
	 *            valid parameter ranges
	 * @param type
	 *            the type of the operator, see {@link OperatorType}
	 * @param outputTypes
	 *            the {@link DataType}s this operator is able to produce
	 * @param stackProcessingType
	 *            the stack processing type of the operator, see
	 *            {@link StackProcessingType}
	 */
	public AbstractOperatorDescriptor(final String[][] resources,
			int numSources, String[] paramNames, Class[] paramClasses,
			Object[] paramDefaults, Range[] validParamValues,
			OperatorType type, DataType[] outputTypes,
			StackProcessingType stackProcessingType) {
		// create descriptor for JAI
		// this is just a "dummy" construction since JAI does not provide an
		// empty constructor
		// of OperationDescriptorImpl.class
		super(resources, new String[] { "IQMCustom" }, numSources, paramNames,
				paramClasses, paramDefaults, validParamValues);

		// register with IQM
		this.resources = resources;
		this.numSources = numSources;
		this.paramNames = paramNames;
		this.paramClasses = paramClasses;
		this.paramDefaults = paramDefaults;
		this.validParamValues = validParamValues;
		this.type = type;
		this.outputTypes = Arrays.asList(outputTypes);
		this.stackProcessingType = stackProcessingType;
	}

	/**
	 * This constructor builds an object for IQM use only.
	 * <p>
	 * The super constructor of {@link OperationDescriptorImpl} is overloaded
	 * and the value of <code>supportedModes</code> is overwritten by
	 * 
	 * <pre>
	 * new String[] { &quot;IQMCustom&quot; }
	 * </pre>
	 * 
	 * @param resources
	 *            the resources array
	 * @param numSources
	 *            the number of sources
	 * @param paramNames
	 *            the names of the parameters
	 * @param paramClasses
	 *            the classes of the parameters
	 * @param paramDefaults
	 *            the default values of the parameters
	 * @param validParamValues
	 *            valid parameter ranges
	 * @param type
	 *            the type of the operator, see {@link OperatorType}
	 * @param outputTypes
	 *            the {@link DataType}s this operator is able to produce
	 */
	public AbstractOperatorDescriptor(final String[][] resources,
			int numSources, String[] paramNames, Class[] paramClasses,
			Object[] paramDefaults, Range[] validParamValues,
			OperatorType type, DataType[] outputTypes) {
		// create descriptor for JAI
		// this is just a "dummy" construction since JAI does not provide an
		// empty constructor
		// of OperationDescriptorImpl.class
		super(resources, new String[] { "IQMCustom" }, numSources, paramNames,
				paramClasses, paramDefaults, validParamValues);

		// register with IQM
		this.resources = resources;
		this.numSources = numSources;
		this.paramNames = paramNames;
		this.paramClasses = paramClasses;
		this.paramDefaults = paramDefaults;
		this.validParamValues = validParamValues;
		this.type = type;
		this.outputTypes = Arrays.asList(outputTypes);
	}

	/**
	 * Returns the number of sources required by this operation. All modes have
	 * the same number of sources.
	 */
	public int getNumSources() {
		return numSources;
	}

	public String getName() {
		return getResourceBundle().getString("GlobalName");
	}

	public OperatorType getType() {
		return type;
	}

	public String getVendor() {
		return getResourceBundle().getString("Vendor");
	}

	public String getDescription() {
		return getResourceBundle().getString("Description");
	}

	public String getArgumentDescription(int index) {
		return getResourceBundle().getString("arg" + index + "Desc");
	}

	public String getVersion() {
		return getResourceBundle().getString("Version");
	}

	public String getDocURL() {
		return getResourceBundle().getString("DocURL");
	}

	public String[][] getResources() {
		return resources;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public Class[] getParamClasses() {
		return paramClasses;
	}

	public Object[] getParamDefaults() {
		return paramDefaults;
	}

	public Range[] getValidParamValues() {
		return validParamValues;
	}

	protected ResourceBundle getResourceBundle() {
		return new ListResourceBundle() {
			public Object[][] getContents() {
				return getResources();
			}
		};
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(getName()).append(", ").append(getVersion()).append(", ")
				.append(getVendor()).append(", ").append(getDocURL())
				.append(", ").append(getDescription());

		return sb.toString();
	}

	@Override
	public StackProcessingType getStackProcessingType() {
		return this.stackProcessingType;
	}

	public List<DataType> getOutputTypes() {
		return this.outputTypes;
	}
}
