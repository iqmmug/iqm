package my.plugins.myOperator;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.OperatorGUIFactory;
import at.mug.iqm.api.operator.ParameterBlockIQM;

/**
 * This class represents the graphical UI of an operator. One may either extend
 * the {@link AbstractImageOperatorGUI} or the {@link AbstractPlotOperatorGUI}
 * in order to create a GUI.
 * 
 * @author Philipp Kainz
 * 
 */
public class MyOperatorGUI extends AbstractImageOperatorGUI implements
		ChangeListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -6684951054122832268L;

	/**
	 * A private parameter block, holding the current settings for the operator.
	 */
	private ParameterBlockIQM pb;
	private JSpinner spinner;

	/**
	 * Mandatory empty constructor for usage with the {@link OperatorGUIFactory}
	 * .
	 */
	public MyOperatorGUI() {
		// within this method all elements for this GUI must be initialized
		logger.debug("Now initializing...");

		this.setOpName(new MyOperatorDescriptor().getName());

		this.initialize();

		this.setTitle("A title for the GUI frame");

		this.getOpGUIContent().setLayout(
				new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel = new JPanel();
		getOpGUIContent().add(panel);

		// use the I18N class in your package to internationalize your GUI
		// description elements get the messages from
		// /src/main/resources/.../messages.properties
		JLabel lblSomeParameterName = new JLabel(
				I18N.getMessage("someParamKey"));
		panel.add(lblSomeParameterName);

		// use WindowBuilder or manually add components and behaviour here
		// ...
		spinner = new JSpinner();
		lblSomeParameterName.setLabelFor(spinner);
		spinner.setModel(new SpinnerNumberModel(55, Integer.MIN_VALUE,
				Integer.MAX_VALUE, 1));
		panel.add(spinner);

		this.pack();
	}

	@Override
	public void setParameterValuesToGUI() {
		// the first statement here MUST be the following line
		this.pb = this.workPackage.getParameters();

		// remove the listeners, if any before setting the spinner
		spinner.removeChangeListener(this);
		spinner.setValue(this.pb.getIntParameter("someName"));
		spinner.addChangeListener(this);

	}

	@Override
	public void updateParameterBlock() {
		// updates the parameter block according to the current GUI element
		// settings

		// e.g.
		this.pb.setParameter("someName",
				((Number) spinner.getValue()).intValue());

	}

	@Override
	public void update() {
		// this method alters GUI elements according to special needs
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// use this method for common action events
		// you will have to register the ActionListener using
		// myComponent.addActionListener(this)
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// update the parameter block each time the spinner changes
		this.updateParameterBlock();
	}
}
