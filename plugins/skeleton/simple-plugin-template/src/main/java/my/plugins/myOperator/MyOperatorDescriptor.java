package my.plugins.myOperator;

import javax.media.jai.util.Range;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.AbstractOperatorDescriptor;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorRegistry;
import at.mug.iqm.api.operator.OperatorType;

/**
 * This is the descriptor for the operator. Each operator is associated with
 * exactly one descriptor.
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings("rawtypes")
public class MyOperatorDescriptor extends AbstractOperatorDescriptor {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7678312186952291169L;

	/**
	 * Set the type of the associated operator to be one of the types declared
	 * in {@link OperatorType}.
	 */
	public static final OperatorType TYPE = OperatorType.IMAGE;

	/**
	 * Set the output types of the associated operator to be one or more of the
	 * types declared in {@link DataType}.
	 */
	public static final DataType[] OUTPUT_TYPES = new DataType[] { DataType.PLOT };

	// Optionally, you can declare a particular stack processing type, if the
	// operator differs from default processing
	// If you specify the stack processing type, you have to include it in the
	// register() method as last argument.
	// public static final StackProcessingType STACK_PROCESSING_TYPE =
	// StackProcessingType.MULTI_STACK_EVEN;

	/**
	 * Use these resources for describing the parts of your parameters.
	 */
	private final static String[][] resources = {

			{ "GlobalName", "mySimpleOperatorTemplate" },
			{ "Vendor", "mug.qmnm" },
			{
					"Description",
					"A simple operator. Here we briefly describe, "
							+ "what this operator does." },
			{ "DocURL", "https://sourceforge.net/projects/iqm/" },
			{ "Version", "1.0" },
			{ "arg0Desc", "This is a description of argument 0." } };
	/**
	 * Use the supported modes for JAI registration.
	 */
	private final static String[] supportedModes = { "rendered" };
	/**
	 * Declare the number of sources.
	 */
	private final static int numSources = 1;
	/**
	 * Declare the name of the parameters. The order of the elements matters!
	 */
	private final static String[] paramNames = { "someName" };
	/**
	 * Declare the classes of the parameters. The order is associated to indexes
	 * given in {@link #paramNames}.
	 */
	private final static Class[] paramClasses = { Integer.class };
	/**
	 * Set some default values for the parameters. The default parameter block
	 * will be constructed using these values.
	 */
	private final static Object[] paramDefaults = { 0 };
	/**
	 * Define a set of valid parameter values.
	 */
	private static final Range[] validParamValues = { new Range(Integer.class,
			Integer.MIN_VALUE, Integer.MAX_VALUE) };

	/**
	 * Constructor for this descriptor.
	 */
	public MyOperatorDescriptor() {
		super(resources, supportedModes, numSources, paramNames, paramClasses,
				paramDefaults, validParamValues, TYPE, OUTPUT_TYPES);
	}

	/**
	 * This method registers the operator with the IQM {@link IOperatorRegistry}
	 * .
	 */
	public static IOperatorDescriptor register() {
		MyOperatorDescriptor odesc = new MyOperatorDescriptor();

		// register the operator
		Application.getOperatorRegistry().register(odesc.getName(),
				odesc.getClass(), MyOperator.class, MyOperatorGUI.class,
				MyOperatorValidator.class, odesc.getType(),
				odesc.getStackProcessingType());

		return odesc;
	}

}
