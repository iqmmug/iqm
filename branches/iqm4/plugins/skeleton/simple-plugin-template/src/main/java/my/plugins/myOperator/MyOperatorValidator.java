package my.plugins.myOperator;

import at.mug.iqm.api.operator.DefaultImageOperatorValidator;

/**
 * This class implements custom validation activities for the operator. One is
 * free to inherit directly from the {@link DefaultImageOperatorValidator} in
 * order to spare double code.
 * 
 * If you want to extend the default validation routine or override it (e.g.
 * perform NO validation at all), simply override
 * {@link DefaultImageOperatorValidator#validate(at.mug.iqm.api.operator.IWorkPackage)}
 * in this class.
 * 
 * @author Philipp Kainz
 * 
 */
public class MyOperatorValidator extends DefaultImageOperatorValidator {

	public MyOperatorValidator() {
	}

}
