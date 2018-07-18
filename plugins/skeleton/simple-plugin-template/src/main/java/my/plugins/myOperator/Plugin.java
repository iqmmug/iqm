package my.plugins.myOperator;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.plugin.AbstractPlugin;
import at.mug.iqm.api.plugin.IPlugin;

/**
 * This class represents the entry point of a plugin. The IQM plugin framework
 * launches {@link #init()} and so registers the plugin and the corresponding
 * operator.
 * 
 * @author Philipp Kainz
 * 
 */
public class Plugin extends AbstractPlugin {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 6649962234708122962L;

	@Override
	public IPlugin init() {
		// register the operator (self-registration)
		IOperatorDescriptor odesc = MyOperatorDescriptor.register();

		// register the plugin and associate the plugin with the operator
		// (self-registration)
		Application.getPluginRegistry().register(this, odesc.getName());

		// print out the plugin and operator information to console
		System.out.println("Plugin: '" + this.getName() + "' with operator '"
				+ odesc.getName() + "' initialized!");

		// return a reference to this plugin
		return this;
	}
}
