package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: ParameterBlockIQM.java
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


import java.awt.image.renderable.ParameterBlock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class extends the {@link ParameterBlock} and is the parent class for all
 * parameter blocks used by IQM operators.
 * <p>
 * The parameter blocks can be constructed using the
 * {@link ParameterBlockIQM#ParameterBlockIQM(IOperatorDescriptor)} constructor.
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings("rawtypes")
public class ParameterBlockIQM extends ParameterBlock {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 3422256334822029608L;

	/**
	 * The cached operator name for replication.
	 */
	private String operatorName;

	/**
	 * The number of parameters.
	 */
	private int numParams;
	/**
	 * The names of the parameters.
	 */
	private String[] paramNames;
	/**
	 * The classes of the parameters.
	 */
	private Class[] paramClasses;

	/**
	 * An array of selected manager indices to be processed.
	 */
	private int[] managerIndices = new int[0];

	/**
	 * The indices of the parameters in the
	 * <code>Vector&lt;Object&gt; parameters</code> of {@link ParameterBlock}.
	 */
	private HashMap<String, Integer> paramIndices = new HashMap<String, Integer>(
			10);

	/**
	 * Overrides the super constructor.
	 */
	public ParameterBlockIQM() {
		super();
	}

	/**
	 * Constructor for IQM parameter blocks. Since IQM operators are executed
	 * independently from JAI, we can use an {@link IOperatorDescriptor} in
	 * order to generate the default parameters of an operator.
	 * 
	 * @param odesc
	 */
	public ParameterBlockIQM(IOperatorDescriptor odesc) {
		// add the default values from the descriptor to the
		// parameter block
		this.generate(odesc);
	}

	/**
	 * Constructor for IQM parameter blocks. Since IQM operators are executed
	 * independently from JAI, we can use an {@link IOperatorDescriptor} in
	 * order to generate the default parameters of an operator.
	 * <p>
	 * The name is looked up in the {@link IOperatorRegistry} and the
	 * corresponding {@link IOperatorDescriptor} is used to build a new
	 * {@link ParameterBlockIQM} instance.
	 * 
	 * @param name
	 */
	public ParameterBlockIQM(String name) {
		this.operatorName = name;
		// fetch descriptor from the registry
		IOperatorDescriptor odesc = OperatorDescriptorFactory
				.createDescriptor(this.operatorName);
		this.generate(odesc);
	}

	/**
	 * Assembles the {@link ParameterBlockIQM} according to a given descriptor.
	 * 
	 * @param odesc
	 */
	private void generate(IOperatorDescriptor odesc) {
		this.operatorName = odesc.getName();
		this.numParams = odesc.getParamClasses().length;
		this.paramClasses = odesc.getParamClasses();
		this.paramNames = odesc.getParamNames();

		// calculate all indices from the names
		this.createParamIndices(odesc.getParamNames());

		// preallocate memory for the parameter vector
		this.parameters = new Vector<Object>(this.numParams);
		// preallocate the memory for the source vector
		this.sources = new Vector<Object>(odesc.getNumSources());

		Object[] defaults = odesc.getParamDefaults();

		for (int i = 0; i < this.numParams; i++) {
			this.parameters.addElement(defaults[i]);
		}
	}

	public int indexOfParameter(String name) {
		Object index = paramIndices.get(name);
		if (index == null)
			throw new IllegalArgumentException("This parameter is unknown: "
					+ name);
		return ((Integer) index).intValue();
	}

	private void createParamIndices(String[] names) {
		String[] keys = names;

		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				this.paramIndices.put(keys[i], new Integer(i));
			}
		}
	}

	public void setParameter(String name, byte b) {
		setParameter0(name, new Byte(b));
	}

	public void setParameter(String name, boolean b) {
		setParameter0(name, new Boolean(b));
	}

	public void setParameter(String name, char c) {
		setParameter0(name, new Character(c));
	}

	public void setParameter(String name, short s) {
		setParameter0(name, new Short(s));
	}

	public void setParameter(String name, int i) {
		setParameter0(name, new Integer(i));
	}

	public void setParameter(String name, long l) {
		setParameter0(name, new Long(l));
	}

	public void setParameter(String name, float f) {
		setParameter0(name, new Float(f));
	}

	public void setParameter(String name, double d) {
		setParameter0(name, new Double(d));
	}

	public void setParameter(String name, Object obj) {
		setParameter0(name, obj);
	}

	private void setParameter0(String name, Object value) {
		// get the index from the paramIndices hash map
		int index = this.indexOfParameter(name);

		// set the element at the ParameterBlock's parameter vector
		this.parameters.setElementAt(value, index);
	}

	public Object getObjectParameter(String paramName) {
		return getObjectParameter0(paramName);
	}

	public byte getByteParameter(String paramName) {
		return ((Byte) getObjectParameter0(paramName)).byteValue();
	}

	public boolean getBooleanParameter(String paramName) {
		return ((Boolean) getObjectParameter0(paramName)).booleanValue();
	}

	public char getCharParameter(String paramName) {
		return ((Character) getObjectParameter0(paramName)).charValue();
	}

	public short getShortParameter(String paramName) {
		return ((Short) getObjectParameter0(paramName)).shortValue();
	}

	public int getIntParameter(String paramName) {
		return ((Integer) getObjectParameter0(paramName)).intValue();
	}

	public long getLongParameter(String paramName) {
		return ((Long) getObjectParameter0(paramName)).longValue();
	}

	public float getFloatParameter(String paramName) {
		return ((Float) getObjectParameter0(paramName)).floatValue();
	}

	public double getDoubleParameter(String paramName) {
		return ((Double) getObjectParameter0(paramName)).doubleValue();
	}

	private Object getObjectParameter0(String name) {
		// get the index from the paramIndices hash map
		int index = this.indexOfParameter(name);

		// return the object at this index
		return super.getObjectParameter(index);
	}

	@Override
	public Class[] getParamClasses() {
		return this.paramClasses;
	}

	public String[] getParamNames() {
		return this.paramNames;
	}

	public ParameterBlockIQM clone() {
		ParameterBlockIQM theClone = new ParameterBlockIQM(operatorName);
		theClone.setParameters(this.parameters);
		theClone.setSources(this.sources);
		theClone.setManagerIndices(this.managerIndices);
		return theClone;
	}

	/**
	 * Sets the default parameters to the {@link ParameterBlockIQM} without
	 * altering the current sources or manager indices.
	 * 
	 * @return a self-reference to the object
	 */
	public ParameterBlockIQM resetParametersOnly() {
		this.setParameters(new ParameterBlockIQM(operatorName).getParameters());
		return this;
	}

	public ParameterBlock toParameterBlock() {
		ParameterBlock pb = new ParameterBlock(sources, parameters);
		return pb;
	}

	public String toString() {
		String s = "";

		StringBuffer sb = new StringBuffer(200);

		// get the param indices of all names
		Iterator<String> iterator = paramIndices.keySet().iterator();

		// print key-value pairs
		sb.append("Parameters: ").append(super.toString());
		while (iterator.hasNext()) {
			sb.append("\n");
			String key = iterator.next().toString();
			int index = paramIndices.get(key);
			sb.append(String.format("%16s::%16s%16s", key, String.valueOf(paramClasses[index]),
					String.valueOf(parameters.get(index))));
		}

		sb.append("\n").append("selectedManagerIndices: int[]=[");

		for (int i : managerIndices) {
			sb.append(i + ", ");
		}
		if (managerIndices.length != 0) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("]\n");

		return s += sb.toString();
	}

	/**
	 * Sets the array of currently selected manager items to the parameter
	 * block.
	 * 
	 * @param managerIndices
	 */
	public void setManagerIndices(int[] managerIndices) {
		this.managerIndices = managerIndices;
	}

	/**
	 * Gets the array of currently selected manager items in this parameter
	 * block.
	 * 
	 * @return an integer array of all selected manager indices
	 */
	public int[] getManagerIndices() {
		return managerIndices;
	}
}
