package my.plugins.myOperator;

import javax.media.jai.PlanarImage;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;

/**
 * This class represents the algorithm of this IQM operator.
 * 
 * @author Philipp Kainz
 * 
 */
public class MyOperator extends AbstractOperator {

	/**
	 * Gets the unique name of the operator.
	 * 
	 * This method is merely a wrapper for the unique name of the operator
	 * stored in the operator's associated {@link IOperatorDescriptor}.
	 * 
	 * @return the name of the operator, declared in the operator's descriptor
	 */
	public String getName() {
		if (this.name == null) {
			this.name = new MyOperatorDescriptor().getName();
		}
		return this.name;
	}

	/**
	 * Gets the {@link OperatorType} of the operator.
	 * 
	 * This method is merely a wrapper for the type of the operator stored in
	 * the operator's associated {@link IOperatorDescriptor}.
	 * 
	 * @return the type of the operator, declared in the operator's descriptor
	 */
	public OperatorType getType() {
		if (this.type == null) {
			this.type = MyOperatorDescriptor.TYPE;
		}
		return this.type;
	}

	/**
	 * This is the starting point for the execution of the operator.
	 * 
	 * The algorithm code is launched using the {@link #run(IWorkPackage)}
	 * method and returns a {@link IResult}.
	 * 
	 * @param wp
	 *            the work package for this algorithm
	 * @return a result of the processed work package
	 */
	@Override
	public IResult run(IWorkPackage wp) throws Exception {
		System.out.println("Now we are executing some plugin code...");
		
		// sample extraction of the containing image at index 0
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		// Get the planar image at source index 0
		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		int someParamValue = pb.getIntParameter("someName");
		
		/*
		 * IMPLEMENT YOUR IMAGE PROCESSING ALGORITHM HERE
		 * 
		 * You may also use other methods within this class or from other
		 * classes
		 */

		// initialize a new image model
		ImageModel im = new ImageModel();

		// set the name of the image
		im.setModelName("Model: processed by MyOperator");
		
		// set the image data of the processed image
		// NOTE: for demo purposes we use the same image
		im.setImage(pi);

		// produce a new result
		Result result = new Result();

		// add the item to the result
		result.addItem(im);

		// return the result
		return result;
	}

}
