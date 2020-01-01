package at.mug.iqm.core.workflow;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ExecutionProxy.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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

import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.events.handler.IGUIUpdateEmitter;
import at.mug.iqm.api.exception.OperatorAlreadyOpenException;
import at.mug.iqm.api.exception.UnknownOperatorException;
import at.mug.iqm.api.gui.MultiResultDialog;
import at.mug.iqm.api.gui.WaitingDialog;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractImageOperatorGUI;
import at.mug.iqm.api.operator.AbstractOperatorGUI;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.IOperator;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IOperatorGUI;
import at.mug.iqm.api.operator.IOperatorValidator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorDescriptorFactory;
import at.mug.iqm.api.operator.OperatorFactory;
import at.mug.iqm.api.operator.OperatorGUIFactory;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.OperatorValidatorFactory;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.ParameterBlockPlot;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.api.operator.WorkPackage;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.api.processing.ExecutionState;
import at.mug.iqm.api.processing.IExecutionProtocol;
import at.mug.iqm.api.workflow.IManager;
import at.mug.iqm.api.workflow.ITank;
import at.mug.iqm.commons.util.CursorToolkit;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.OperatorCompletionWaiter;
import at.mug.iqm.core.I18N;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class is the central control instance for launching operators in IQM via
 * the GUI, i.e. the menu items.
 * 
 * @author Philipp Kainz
 * 
 */
public class ExecutionProxy implements IExecutionProtocol {

	/**
	 * Custom class logger.
	 */
	private static final Logger logger = LogManager.getLogger(ExecutionProxy.class);

	/**
	 * Set the default state to {@link ExecutionState#IDLE}.
	 */
	private int state = ExecutionState.IDLE;

	/**
	 * The cached operator name.
	 */
	private String operatorName;

	/**
	 * The descriptor.
	 */
	private IOperatorDescriptor operatorDescriptor;

	/**
	 * The operator (processing algorithm).
	 */
	private IOperator operator;

	/**
	 * The operator's GUI.
	 */
	private IOperatorGUI operatorGUI;

	/**
	 * The operator's validator.
	 */
	private IOperatorValidator operatorValidator;

	/**
	 * The work package of this protocol.
	 */
	private IWorkPackage workPackage;

	/**
	 * The preview result object of the protocol.
	 */
	private Result previewResult;

	/**
	 * The dialog for displaying the multiple results of an operator.
	 */
	private MultiResultDialog multiResultDialog;

	/**
	 * The parameters of this protocol.
	 */
	private ParameterBlockIQM pb;

	/**
	 * The constructor always uses the name of the operator.
	 * 
	 * @param operatorName
	 *            the unique name of the operator, located in the
	 *            <code>String[][] resources</code> of the
	 *            {@link IOperatorDescriptor}
	 * @throws OperatorAlreadyOpenException
	 *             if another operator is already launched
	 */
	private ExecutionProxy(String operatorName) {
		this.state = ExecutionState.UNDEFINED;
		this.operatorName = operatorName;
		Application.setCurrentExecutionProtocol(this);
	}

	/**
	 * This method launches a new instance of the {@link IOperator} specified by
	 * the given name.
	 * 
	 * @param name
	 *            the registered name of the operator
	 * @return the instance of the execution protocol
	 * @throws UnknownOperatorException
	 *             if the operator is not found.
	 */
	public static IExecutionProtocol launchInstance(String name)
			throws UnknownOperatorException {
		IExecutionProtocol protocol = null;

		// look up the name of the operator in the registry and get the
		// descriptor
		IOperatorDescriptor desc = OperatorDescriptorFactory
				.createDescriptor(name);
		if (desc == null) {
			throw new UnknownOperatorException("The requested name [" + name
					+ "] does not identify any registered operator!");
		} else {
			protocol = launchInstance(desc);
		}

		return protocol;
	}

	/**
	 * This method launches a new instance of the {@link IOperator} specified by
	 * the given {@link IOperatorDescriptor}.
	 * 
	 * @param opDesc
	 *            the descriptor of the operator to be launched
	 * @return the instance of the execution protocol
	 */
	public static IExecutionProtocol launchInstance(IOperatorDescriptor opDesc) {
		IExecutionProtocol currentInstance = getCurrentInstance();

		ExecutionProxy proxy = null;
		if (currentInstance != null) {
			// check for the same operator name
			if (currentInstance.getOperatorName().equals(opDesc.getName())) {
				// if they are equal, bring the launched GUI to front
				((AbstractOperatorGUI) currentInstance.getOperatorGUI())
				.toFront();
			} else {
				// otherwise finish the operator
				currentInstance.getOperatorGUI().destroy();

				// and launch the new one
				proxy = new ExecutionProxy(opDesc.getName());
				proxy.launchProtocol();
			}
		} else {
			proxy = new ExecutionProxy(opDesc.getName());
			proxy.launchProtocol();
		}
		return currentInstance;
	}

	/**
	 * Returns the current control instance for executing operators.
	 * 
	 * @return the active control instance of {@link ExecutionProxy}, or
	 *         <code>null</code> if the instance is not initialized
	 */
	public static IExecutionProtocol getCurrentInstance() {
		return Application.getCurrentExecutionProtocol();
	}

	/**
	 * Initializes all necessary classes and constructs instances of them.
	 * 
	 * @return {@link ExecutionState#INITIALIZED} if successful,
	 *         {@link ExecutionState#INITIALIZATION_FAILED} otherwise
	 */
	protected int initialize() {

		this.operatorDescriptor = OperatorDescriptorFactory
				.createDescriptor(this.operatorName);
		this.operator = OperatorFactory.createOperator(this.operatorName);
		this.operatorGUI = OperatorGUIFactory.createGUI(this.operatorName);
		this.operatorValidator = OperatorValidatorFactory
				.createValidator(this.operatorName);

		if (this.operatorDescriptor == null || this.operator == null
				|| this.operatorGUI == null || this.operatorValidator == null) {

			this.state = ExecutionState.INITIALIZATION_FAILED;
		} else {
			this.state = ExecutionState.INITIALIZED;
		}
		return this.state;
	}

	/**
	 * Launch the protocol.
	 * 
	 * @return {@link ExecutionState#FINISHED} if no errors occur,
	 *         {@link ExecutionState#ERROR} otherwise
	 */
	public int launchProtocol() {
		try {
			logger.debug("Execution protocol for operator ["
					+ this.operatorName + "] is launching...");
			System.out.println("Execution protocol for operator ["
					+ this.operatorName + "] is launching...");
			int currentState = this.initialize();
			if (currentState > 0) {
				currentState = this.buildWorkPackage();
				if (currentState > 0) {
					currentState = this.validateWorkPackage();
					if (currentState > 0) {
						currentState = this.showOperatorGUI();
						if (currentState < 0) {
							this.finishProtocol();
						}
					} else {
						this.finishProtocol();
					}
				} else {
					this.finishProtocol();
				}
			} else {
				this.finishProtocol();
			}
		} catch (Exception e) {
			DialogUtil.getInstance().showErrorMessage(
					"Cannot launch the protocol for [" + this.operatorName
					+ "]. Error at ExecutionState '"
					+ ExecutionState.stateToString(getState())
					+ "' (Code " + getState() + ").", e, true);
			this.finishProtocol();
		}
		return this.state;
	}

	/**
	 * Constructs a work package for an operator. The containing parameter block
	 * is a subclass of {@link ParameterBlockIQM}.
	 * 
	 * @return {@link ExecutionState#WORKPACKAGE_CONSTRUCTED} if successful,
	 *         {@link ExecutionState#ERROR}, if not
	 */
	protected int buildWorkPackage() {
		try {
			ITank tank = Tank.getInstance();
			IManager mgr = Manager.getInstance();
			Vector<Object> sources = null;
			// the tank index in either the left or the right list
			int currTankIdx = -1;

			switch (getOperatorType()) {
			case IMAGE:
				this.pb = new ParameterBlockImg(this.operatorDescriptor);

				// determine the current manager panel and get the selected
				// images
				if (mgr.isLeftListActive()) {
					currTankIdx = mgr.getTankIndexInMainLeft();
				}
				if (mgr.isRightListActive()) {
					currTankIdx = mgr.getTankIndexInMainRight();
				}

				// determine selected cells in the left manager
				int[] currMgrIdxs_Img = mgr.getManagerPanel().getCurrMgrIdxs();

				// pre-allocate source memory
				sources = new Vector<Object>(currMgrIdxs_Img.length);

				// add the items from tank to the sources
				for (int i : currMgrIdxs_Img) {
					IqmDataBox box = tank.getTankIqmDataBoxAt(currTankIdx, i);
					if (box instanceof IVirtualizable) {
						box = VirtualDataManager.getInstance().load(
								(IVirtualizable) box);
					}
					sources.add(box);
				}

				this.pb.setSources(sources);
				this.pb.setManagerIndices(currMgrIdxs_Img);

				// DEBUG ONLY!
				// for (int i=0; i<sources.size(); i++)
				// GUITools.showImage(((IqmDataBox) this.pb.getSource(i))
				// .getImage().getAsBufferedImage(), "Source "+i);

				break;

			case PLOT:
				this.pb = new ParameterBlockPlot(this.operatorDescriptor);
				// determine the current manager panel and get the plot models

				// determine the current manager panel and get the selected
				// images
				if (mgr.isLeftListActive()) {
					currTankIdx = mgr.getTankIndexInMainLeft();
				}
				if (mgr.isRightListActive()) {
					currTankIdx = mgr.getTankIndexInMainRight();
				}

				// determine selected cells in the left manager
				int[] currMgrIdxs_Plot = mgr.getManagerPanel().getCurrMgrIdxs();

				// pre-allocate source memory
				sources = new Vector<Object>(currMgrIdxs_Plot.length);

				// add the items from tank to the sources
				for (int i : currMgrIdxs_Plot) {
					IqmDataBox box = tank.getTankIqmDataBoxAt(currTankIdx, i);
					if (box instanceof IVirtualizable) {
						box = VirtualDataManager.getInstance().load(
								(IVirtualizable) box);
					}
					sources.add(box);
				}

				this.pb.setSources(sources);
				this.pb.setManagerIndices(currMgrIdxs_Plot);
				break;

			case IMAGE_GENERATOR:
			case PLOT_GENERATOR:
				this.pb = new ParameterBlockIQM(this.operatorDescriptor);
				// do not set any sources
				break;
			default:
				this.pb = new ParameterBlockIQM(this.operatorDescriptor);
				break;
			}

			this.workPackage = new WorkPackage(this.operator, this.pb);

			this.state = ExecutionState.WORKPACKAGE_CONSTRUCTED;
		} catch (Exception e) {
			DialogUtil.getInstance().showErrorMessage(
					"Could not build initial work package for ["
							+ this.operatorName + "]!", e);
			this.state = ExecutionState.ERROR;
		}
		return this.state;
	}

	/**
	 * Validates the work package and sets the state of the protocol to
	 * {@link ExecutionState#VALIDATING_PARAMETERS} while the
	 * {@link IOperatorValidator} is validating.
	 * 
	 * @return {@link ExecutionState#VALIDATION_PASSED} if successful,
	 *         {@link ExecutionState#VALIDATION_FAILED} otherwise
	 */
	protected int validateWorkPackage() {
		try {
			this.state = ExecutionState.VALIDATING_PARAMETERS;

			logger.debug("Validating work package for operator ["
					+ this.operatorName + "]");

			boolean valid = this.operatorValidator.validate(this.workPackage);

			if (valid) {
				this.state = ExecutionState.VALIDATION_PASSED;
			} else {
				this.state = ExecutionState.VALIDATION_FAILED;
			}

		} catch (IllegalArgumentException iae) {
			DialogUtil.getInstance().showErrorMessage(
					"The validation for [" + this.operatorName + "] failed!",
					iae);
			this.state = ExecutionState.ERROR;
		} catch (Exception e) {
			DialogUtil.getInstance().showErrorMessage(null, e, true);
			this.state = ExecutionState.ERROR;
		}

		return this.state;
	}

	/**
	 * Shows the requested operator GUI.
	 * 
	 * @return {@link ExecutionState#GUI_VISIBLE} if successful,
	 *         {@link ExecutionState#ERROR} otherwise
	 */
	protected int showOperatorGUI() {
		try {

			switch (getOperatorType()) {
			case IMAGE:
				AbstractImageOperatorGUI imgGUI = (AbstractImageOperatorGUI) this.operatorGUI;
				this.operatorGUI = imgGUI;

				// wire all listeners to the GUI
				// --> ControlPanel implements IGUIUpdateListener for
				// "virtualFlagChanged"
				imgGUI.addGUIUpdateListener(GUITools.getMainFrame()
						.getMainPanel().getControlPanel(), "virtualFlagChanged");

				// the GUI has been constructed work package!
				// the work package contains the default parameter block
				imgGUI.setWorkPackage(this.workPackage);
				// now set the default parameters from the work package to the
				// GUI
				imgGUI.setParameterValuesToGUI();

				// initialize the preference manager for this gui
				imgGUI.getPreferencesPanel().initialize(imgGUI);
				imgGUI.getOutputOptionsPanel().initialize(imgGUI);

				// update the GUI
				imgGUI.update();

				// show the GUI
				imgGUI.setVisible(true);
				break;

			case IMAGE_GENERATOR:
				AbstractImageOperatorGUI imgGeneratorGUI = (AbstractImageOperatorGUI) this.operatorGUI;
				this.operatorGUI = imgGeneratorGUI;

				// wire all listeners to the GUI
				// --> ControlPanel implements IGUIUpdateListener for
				// "virtualFlagChanged"
				imgGeneratorGUI
				.addGUIUpdateListener(GUITools.getMainFrame()
						.getMainPanel().getControlPanel(),
						"virtualFlagChanged");

				// the GUI has been constructed work package!
				// the work package contains the default parameter block
				imgGeneratorGUI.setWorkPackage(this.workPackage);
				// now set the default parameters from the work package to the
				// GUI
				imgGeneratorGUI.setParameterValuesToGUI();

				// initialize the preference manager for this gui
				imgGeneratorGUI.getPreferencesPanel().initialize(
						imgGeneratorGUI);
				imgGeneratorGUI.getOutputOptionsPanel().initialize(
						imgGeneratorGUI);

				// update the GUI
				imgGeneratorGUI.update();

				// if the operator type is a generator, we have to
				// alter the control panel and set "create copies" to
				// "create samples"
				AbstractButton _a = GUITools.getMainFrame().getMainPanel()
						.getControlPanel().getButtonCopies();
				_a.setText(I18N
						.getGUILabelText("options.buttCreateCopies.alt.generator.text"));

				// show the GUI
				imgGeneratorGUI.setVisible(true);
				break;

			case PLOT:

				AbstractPlotOperatorGUI plotGUI = (AbstractPlotOperatorGUI) this.operatorGUI;
				this.operatorGUI = plotGUI;

				// wire all listeners to the GUI
				// --> ControlPanel implements IGUIUpdateListener for
				// "virtualFlagChanged"
				plotGUI.addGUIUpdateListener(GUITools.getMainFrame()
						.getMainPanel().getControlPanel(), "virtualFlagChanged");

				// the GUI has been constructed without a work package!
				// the work package contains the default parameter block
				plotGUI.setWorkPackage(this.workPackage);
				plotGUI.setParameterValuesToGUI();

				// initialize the preference manager for this GUI
				plotGUI.getPreferencesPanel().initialize(plotGUI);
				plotGUI.getOutputOptionsPanel().initialize(plotGUI);

				// update the GUI
				plotGUI.update();

				plotGUI.setVisible(true);

				break;
			case PLOT_GENERATOR:

				AbstractPlotOperatorGUI plotGeneratorGUI = (AbstractPlotOperatorGUI) this.operatorGUI;
				this.operatorGUI = plotGeneratorGUI;

				// wire all listeners to the GUI
				// --> ControlPanel implements IGUIUpdateListener for
				// "virtualFlagChanged"
				plotGeneratorGUI
				.addGUIUpdateListener(GUITools.getMainFrame()
						.getMainPanel().getControlPanel(),
						"virtualFlagChanged");

				// the GUI has been constructed without a work package!
				// the work package contains the default parameter block
				plotGeneratorGUI.setWorkPackage(this.workPackage);
				plotGeneratorGUI.setParameterValuesToGUI();

				// initialize the preference manager for this GUI
				plotGeneratorGUI.getPreferencesPanel().initialize(
						plotGeneratorGUI);
				plotGeneratorGUI.getOutputOptionsPanel().initialize(
						plotGeneratorGUI);

				// update the GUI
				plotGeneratorGUI.update();

				// if the operator type is a generator, we have to
				// alter the control panel and set "create copies" to
				// "create samples"
				AbstractButton _b = GUITools.getMainFrame().getMainPanel()
						.getControlPanel().getButtonCopies();
				_b.setText(I18N
						.getGUILabelText("options.buttCreateCopies.alt.generator.text"));

				plotGeneratorGUI.setVisible(true);
				break;

			case OTHER:

				AbstractOperatorGUI gui = (AbstractOperatorGUI) this.operatorGUI;
				this.operatorGUI = gui;

				// wire all listeners to the GUI
				// --> ControlPanel implements IGUIUpdateListener for
				// "virtualFlagChanged"
				if (gui instanceof IGUIUpdateEmitter) {
					((IGUIUpdateEmitter) gui).addGUIUpdateListener(GUITools
							.getMainFrame().getMainPanel().getControlPanel(),
							"virtualFlagChanged");
				}

				// the GUI has been constructed without a work package!
				// the work package contains the default parameter block
				gui.setWorkPackage(this.workPackage);
				gui.setParameterValuesToGUI();

				gui.setVisible(true);
				break;

			default:
				break;
			}
			GUITools.getStatusPanel()
			.getLblOperatorName()
			.setText(
					this.operatorName
					+ " - "
					+ I18N.getGUILabelText("statuspanel.operatorName.idle.text"));

			this.state = ExecutionState.GUI_VISIBLE;
		} catch (Exception e) {
			DialogUtil.getInstance()
			.showErrorMessage(
					"An error occurred in the ExecutionProxy! The GUI for operator ["
							+ this.operatorName + "] cannot be shown.",
							e, true);
			this.state = ExecutionState.ERROR;
		}

		return this.state;
	}

	@Override
	public void executePreview(IWorkPackage wp, IOperatorGUI opGUI) {
		// int[] idxs =
		// Manager.getInstance().getManagerPanel().getCurrMgrIdxs();
		// System.err.print("Indices in executePreview(): ");
		// for (int i : idxs) System.err.print(i + " ");
		// System.err.println();

		Manager.getInstance().resetTogglePreviewIfRunning();
		Manager.getInstance().setPreviewImage(null);

		// if the user passes two null objects, the protocols current
		// information is taken
		if (wp != null) {
			this.workPackage = wp;
		}
		if (opGUI != null) {
			this.operatorGUI = opGUI;
		}

		int[] mgrIdcs = this.workPackage.getManagerIndices();

		switch (operatorDescriptor.getType()) {
		case IMAGE_GENERATOR:
		case PLOT_GENERATOR:
		case OTHER:
			break;

		default:
			List<Object> sources = this.workPackage.getSources();

			IqmDataBox source1 = Tank.getInstance().getCurrentTankIqmDataBoxAt(
					mgrIdcs[0]);

			if (source1 instanceof IVirtualizable) {
				source1 = VirtualDataManager.getInstance().load(
						(IVirtualizable) source1);
			}

			IqmDataBox source2 = null;

			// build custom parameter blocks for each stack processing type
			switch (operatorDescriptor.getStackProcessingType()) {
			case MULTI_STACK_EVEN:
				// alter sources
				// take the corresponding image in the selected manager's
				// opposite
				// if left is selected, take right, and vice-versa
				if (Manager.getInstance().isLeftListActive()) {
					source2 = Tank.getInstance().getTankIqmDataBoxAt(
							Manager.getInstance().getTankIndexInMainRight(),
							mgrIdcs[0]);
				} else if (Manager.getInstance().isRightListActive()) {
					source2 = Tank.getInstance().getTankIqmDataBoxAt(
							Manager.getInstance().getTankIndexInMainLeft(),
							mgrIdcs[0]);
				}

				if (source2 instanceof IVirtualizable) {
					source2 = VirtualDataManager.getInstance().load(
							(IVirtualizable) source2);
				}

				sources.clear();
				sources.add(0, source1); // the first stack
				sources.add(1, source2); // the second stack
				break;

			case MULTI_STACK_ODD:
				// alter sources
				// take the corresponding image in the selected manager's
				// opposite
				// if left is selected, take right, and vice-versa
				if (Manager.getInstance().isLeftListActive()) {
					source2 = Tank.getInstance().getTankIqmDataBoxAt(
							Manager.getInstance().getTankIndexInMainRight(), 0);
				} else if (Manager.getInstance().isRightListActive()) {
					source2 = Tank.getInstance().getTankIqmDataBoxAt(
							Manager.getInstance().getTankIndexInMainLeft(), 0);
				}

				if (source2 instanceof IVirtualizable) {
					source2 = VirtualDataManager.getInstance().load(
							(IVirtualizable) source2);
				}

				sources.clear();
				sources.add(0, source1); // the stack
				sources.add(1, source2); // the single source
				break;
			case DEFAULT:
				break;
			case SINGLE_STACK_SEQUENTIAL:
			default:
				break;
			}
		}
		// create a new swing worker thread (PreviewProcessingTask) for the
		// preview calculation
		AbstractProcessingTask task = Application.getTaskFactory()
				.createPreviewTask(this.workPackage, this.operatorGUI);
		GUITools.getStatusPanel().setProcessingTask(task); // is used for the
		// status bar
		// updates

		// set the progress change listener to the operator
		// PK: 2012 10 04: updates the progress bar via IQM API
		this.operator.addProgressListener(GUITools.getStatusPanel(),
				"operatorProgress");

		// task.firePropertyChange("taskRunning", int, int) enables/disables the
		// progress bars
		task.addPropertyChangeListener(GUITools.getStatusPanel());

		// create a waiting dialog/frame
		WaitingDialog dialog = new WaitingDialog(
				I18N.getGUILabelText("statuspanel.taskName.processing.text",
						this.operator.getName()), this.operator.isCancelable());

		dialog.getPcs().addPropertyChangeListener(GUITools.getStatusPanel());

		task.addPropertyChangeListener(new OperatorCompletionWaiter(dialog));

		// the inputs on the GUI are disabled by the completion waiter
		this.disableInputs((AbstractOperatorGUI) this.operatorGUI);

		// execute the thread
		task.execute();

		dialog.setVisible(true);
	}

	/**
	 * Executes a serial stack processing task with the selected manager items.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void executeSerialProcessing() {
		// prepare manager
		Manager.getInstance().resetTogglePreviewIfRunning();

		// Sources should already be set by
		// ExecutionProxy#updateSources(int[],int[])

		switch (this.operatorDescriptor.getStackProcessingType()) {
		case DEFAULT:
		case SINGLE_STACK_SEQUENTIAL:
			// if just one single element is selected, process the entire stack
			// in the selected manager list
			if (this.workPackage.getManagerIndices().length == 1) {
				List<Object> allSources = null;
				this.workPackage.removeSources();
				// check, which manager model shall be processed
				// i.e. which manager holds the current tank index
				if (Manager.getInstance().isLeftListActive()) {
					allSources = getAllSourcesForManager(0);
				} else if (Manager.getInstance().isRightListActive()) {
					allSources = getAllSourcesForManager(1);
				}
				this.workPackage.setSources(allSources);
			}
			break;

		case MULTI_STACK_EVEN:
			// two evenly sized stacks in both manager lists
			// if just one single element is selected, process the entire stack
			// in the selected manager list

			List<Vector<Object>> allSources_mse = new Vector<Vector<Object>>(2);

			if (this.workPackage.getManagerIndices().length == 1) {
				this.workPackage.removeSources();
				// check, which manager model shall be processed
				// i.e. which manager holds the current tank index
				if (Manager.getInstance().isLeftListActive()) {
					// add left manager first, then right manager
					allSources_mse.add(0, getAllSourcesForManager(0));
					allSources_mse.add(1, getAllSourcesForManager(1));
				} else if (Manager.getInstance().isRightListActive()) {
					// add right manager first, then left manager
					allSources_mse.add(0, getAllSourcesForManager(1));
					allSources_mse.add(1, getAllSourcesForManager(0));
				}

			} else {
				this.workPackage.removeSources();
				// run through the selected manager indices and collect the
				// sources
				Vector<Object> firstStack = new Vector<Object>();
				Vector<Object> secondStack = new Vector<Object>();

				Vector<Object> allLeft = getAllSourcesForManager(0);
				Vector<Object> allRight = getAllSourcesForManager(1);

				for (int idx : this.workPackage.getManagerIndices()) {
					if (Manager.getInstance().isLeftListActive()) {
						// add left manager first, then right manager
						firstStack.add(allLeft.get(idx));
						secondStack.add(allRight.get(idx));
					} else if (Manager.getInstance().isRightListActive()) {
						// add right manager first, then left manager
						firstStack.add(allRight.get(idx));
						secondStack.add(allLeft.get(idx));
					}
				}

				// set the sources
				allSources_mse.add(0, firstStack);
				allSources_mse.add(1, secondStack);
			}

			// the serial processing task must be able to cope with two lists of
			// objects in the sources
			this.workPackage.setSources(new Vector(allSources_mse));
			break;

		case MULTI_STACK_ODD:
			// two stacks in both manager lists, one of which contains just one item.
			// if just one single element is selected, process the entire stack
			// in the selected manager list

			List<Vector<Object>> allSources_mso = new Vector<Vector<Object>>(2);
			Vector<Object> firstStack_mso = new Vector<Object>();
			Vector<Object> secondStack_mso = new Vector<Object>();

			if (this.workPackage.getManagerIndices().length == 1) {
				this.workPackage.removeSources();

				Vector<Object> allLeft = getAllSourcesForManager(0);
				Vector<Object> allRight = getAllSourcesForManager(1);

				// check, which manager model shall be processed
				// i.e. which manager holds the current tank index
				if (Manager.getInstance().isLeftListActive()) {
					// add entire left manager first, then the first item in the right manager
					firstStack_mso.addAll(allLeft);
					secondStack_mso.add(allRight.get(0));
				} else if (Manager.getInstance().isRightListActive()) {
					// add entire right manager first, then the first item in the left manager
					firstStack_mso.addAll(allRight);
					secondStack_mso.add(allLeft.get(0));
				}

			} else {
				this.workPackage.removeSources();
				// run through the selected manager indices and collect the
				// sources
				Vector<Object> allLeft = getAllSourcesForManager(0);
				Vector<Object> allRight = getAllSourcesForManager(1);

				for (int idx : this.workPackage.getManagerIndices()) {
					if (Manager.getInstance().isLeftListActive()) {
						// add left manager first, then right manager
						firstStack_mso.add(allLeft.get(idx));
						secondStack_mso.add(allRight.get(idx));
					} else if (Manager.getInstance().isRightListActive()) {
						// add right manager first, then left manager
						firstStack_mso.add(allRight.get(idx));
						secondStack_mso.add(allLeft.get(idx));
					}
				}
			}

			// set the sources
			allSources_mso.add(0, firstStack_mso);
			allSources_mso.add(1, secondStack_mso);

			// the serial processing task must be able to cope with two lists of
			// objects in the sources
			this.workPackage.setSources(new Vector(allSources_mso));
			break;

		default:
			break;
		}
		// ALL SOURCES HAVE BEEN SET
		// ##############################################################

		// create a new swing worker thread (SerialProcessingTask) for the
		// preview calculation
		AbstractProcessingTask task = Application.getTaskFactory()
				.createSerialTask(this.workPackage, this.operatorGUI,
						Application.isVirtual());
		GUITools.getStatusPanel().setProcessingTask(task); // is used for the
		// status bar
		// updates

		// set the progress change listener to the operator
		// PK: 2012 10 04: updates the progress bar via IQM API
		this.operator.addProgressListener(GUITools.getStatusPanel(),
				"operatorProgress");

		// task.firePropertyChange("taskRunning", int, int) enables/disables the
		// progress bars
		task.addPropertyChangeListener(GUITools.getStatusPanel());

		// create a waiting dialog/frame
		WaitingDialog dialog = new WaitingDialog(
				I18N.getGUILabelText("statuspanel.taskName.processing.text",
						this.operator.getName()), this.operator.isCancelable());

		dialog.getPcs().addPropertyChangeListener(GUITools.getStatusPanel());

		task.addPropertyChangeListener(new OperatorCompletionWaiter(dialog));

		// the inputs on the GUI are disabled by the completion waiter
		this.disableInputs((AbstractOperatorGUI) this.operatorGUI);

		// execute the thread in background (where the done method is called
		// when finished)
		task.execute();

		dialog.setVisible(true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void executeParallelProcessing(int nThreads) {

		Manager.getInstance().resetTogglePreviewIfRunning();

		// Sources should already be set by
		// ExecutionProxy#updateSources(int[],int[])

		switch (this.operatorDescriptor.getStackProcessingType()) {
		case DEFAULT:
		case SINGLE_STACK_SEQUENTIAL:
			// if just one single element is selected, process the entire stack
			// in the selected manager list
			if (this.workPackage.getManagerIndices().length == 1) {
				List<Object> allSources = null;
				this.workPackage.removeSources();
				// check, which manager model shall be processed
				// i.e. which manager holds the current tank index
				if (Manager.getInstance().isLeftListActive()) {
					allSources = getAllSourcesForManager(0);
				} else if (Manager.getInstance().isRightListActive()) {
					allSources = getAllSourcesForManager(1);
				}
				this.workPackage.setSources(allSources);
			}
			break;

		case MULTI_STACK_EVEN:
			// two evenly sized stacks in both manager lists
			// if just one single element is selected, process the entire stack
			// in the selected manager list

			List<Vector<Object>> allSources_mse = new Vector<Vector<Object>>(2);

			if (this.workPackage.getManagerIndices().length == 1) {
				this.workPackage.removeSources();
				// check, which manager model shall be processed
				// i.e. which manager holds the current tank index
				if (Manager.getInstance().isLeftListActive()) {
					// add left manager first, then right manager
					allSources_mse.add(0, getAllSourcesForManager(0));
					allSources_mse.add(1, getAllSourcesForManager(1));
				} else if (Manager.getInstance().isRightListActive()) {
					// add right manager first, then left manager
					allSources_mse.add(0, getAllSourcesForManager(1));
					allSources_mse.add(1, getAllSourcesForManager(0));
				}

			} else {
				this.workPackage.removeSources();
				// run through the selected manager indices and collect the
				// sources
				Vector<Object> firstStack = new Vector<Object>();
				Vector<Object> secondStack = new Vector<Object>();

				Vector<Object> allLeft = getAllSourcesForManager(0);
				Vector<Object> allRight = getAllSourcesForManager(1);

				for (int idx : this.workPackage.getManagerIndices()) {
					if (Manager.getInstance().isLeftListActive()) {
						// add left manager first, then right manager
						firstStack.add(allLeft.get(idx));
						secondStack.add(allRight.get(idx));
					} else if (Manager.getInstance().isRightListActive()) {
						// add right manager first, then left manager
						firstStack.add(allRight.get(idx));
						secondStack.add(allLeft.get(idx));
					}
				}

				// set the sources
				allSources_mse.add(0, firstStack);
				allSources_mse.add(1, secondStack);
			}

			// the serial processing task must be able to cope with two lists of
			// objects in the sources
			this.workPackage.setSources(new Vector(allSources_mse));
			break;

		case MULTI_STACK_ODD:
			// two stacks in both manager lists, one of which contains just one item.
			// if just one single element is selected, process the entire stack
			// in the selected manager list

			List<Vector<Object>> allSources_mso = new Vector<Vector<Object>>(2);
			Vector<Object> firstStack_mso = new Vector<Object>();
			Vector<Object> secondStack_mso = new Vector<Object>();

			if (this.workPackage.getManagerIndices().length == 1) {
				this.workPackage.removeSources();

				Vector<Object> allLeft = getAllSourcesForManager(0);
				Vector<Object> allRight = getAllSourcesForManager(1);

				// check, which manager model shall be processed
				// i.e. which manager holds the current tank index
				if (Manager.getInstance().isLeftListActive()) {
					// add entire left manager first, then the first item in the right manager
					firstStack_mso.addAll(allLeft);
					secondStack_mso.add(allRight.get(0));
				} else if (Manager.getInstance().isRightListActive()) {
					// add entire right manager first, then the first item in the left manager
					firstStack_mso.addAll(allRight);
					secondStack_mso.add(allLeft.get(0));
				}

			} else {
				this.workPackage.removeSources();
				// run through the selected manager indices and collect the
				// sources
				Vector<Object> allLeft = getAllSourcesForManager(0);
				Vector<Object> allRight = getAllSourcesForManager(1);

				for (int idx : this.workPackage.getManagerIndices()) {
					if (Manager.getInstance().isLeftListActive()) {
						// add left manager first, then right manager
						firstStack_mso.add(allLeft.get(idx));
						secondStack_mso.add(allRight.get(idx));
					} else if (Manager.getInstance().isRightListActive()) {
						// add right manager first, then left manager
						firstStack_mso.add(allRight.get(idx));
						secondStack_mso.add(allLeft.get(idx));
					}
				}
			}

			// set the sources
			allSources_mso.add(0, firstStack_mso);
			allSources_mso.add(1, secondStack_mso);

			// the serial processing task must be able to cope with two lists of
			// objects in the sources
			this.workPackage.setSources(new Vector(allSources_mso));
			break;

		default:
			break;
		}
		// ALL SOURCES HAVE BEEN SET
		// ##############################################################

		// create a new swing worker thread (PreviewProcessingTask) for the
		// preview calculation
		AbstractProcessingTask task = Application.getTaskFactory()
				.createParallelTask(this.workPackage, this.operatorGUI,
						Application.isVirtual(), nThreads);
		GUITools.getStatusPanel().setProcessingTask(task); // is used for the
		// status bar
		// updates

		// set the progress change listener to the operator
		// PK: 2012 10 04: updates the progress bar via IQM API
		this.operator.addProgressListener(GUITools.getStatusPanel(),
				"operatorProgress");

		// task.firePropertyChange("taskRunning", int, int) enables/disables the
		// progress bars
		task.addPropertyChangeListener(GUITools.getStatusPanel());

		// create a waiting dialog/frame
		WaitingDialog dialog = new WaitingDialog(
				I18N.getGUILabelText("statuspanel.taskName.processing.text",
						this.operator.getName()), this.operator.isCancelable());

		dialog.getPcs().addPropertyChangeListener(GUITools.getStatusPanel());

		task.addPropertyChangeListener(new OperatorCompletionWaiter(dialog));

		// the inputs on the GUI are disabled by the completion waiter
		this.disableInputs((AbstractOperatorGUI) this.operatorGUI);

		// execute the thread
		task.execute();

		dialog.setVisible(true);
	}

	/**
	 * Sets a wait cursor to a {@link JFrame} and disables the inputs.
	 * 
	 * @param window
	 */
	@Override
	public void disableInputs(final AbstractOperatorGUI window) {
		logger.trace("Setting wait cursor on dialog.");
		window.disableInputs();
		CursorToolkit.startWaitCursor(window.getRootPane());
	}

	/**
	 * Sets the default cursor to a {@link JFrame} and enables the inputs.
	 * 
	 * @param window
	 */
	@Override
	public void enableInputs(final AbstractOperatorGUI window) {
		logger.trace("Setting default cursor on dialog.");
		window.enableInputs();
		CursorToolkit.stopWaitCursor(window.getRootPane());
	}

	/**
	 * Gets the current state of the protocol.
	 * 
	 * @return the current state of the protocol.
	 */
	public int getState() {
		System.out.println("IExecutionProtocol.STATE=" + this.state);
		logger.debug("IExecutionProtocol.STATE=" + this.state);
		return this.state;
	}

	/**
	 * Gets the type of the requested operator.
	 * 
	 * @return
	 */
	public OperatorType getOperatorType() {
		return this.operator.getType();
	}

	/**
	 * Updates the sources after items are changed in the manager and validates
	 * them again using the operator's validator.
	 * <p>
	 * If validation fails, the source is set back to the original one when the
	 * operator was launched and the user is notified that the processing is not
	 * possible with the selected images.
	 * <p>
	 * Perform a reset of the sources by passing <code>null</code> for both
	 * parameters. This will yield a <code>null</code> value in return.
	 * 
	 * @param currMgrIdxs
	 *            the currently selected cells
	 * @param targetMgrIdxs
	 *            the manager list indexes to be selected after validation
	 */
	@Override
	public int[] updateSources(int[] currMgrIdxs, int[] targetMgrIdxs) {

		if (currMgrIdxs == null && targetMgrIdxs == null) {
			this.workPackage.removeSources();
			return null;
		}

		// get a copy of the work package of the operator's GUI
		IWorkPackage wpClone = this.workPackage.clone();

		// remove all sources and add new ones, parameters remain as set
		wpClone.removeSources();

		// get the new sources and add them to the source list
		// fetch items from current tank according to the manager indices
		Vector<Object> newSources = new Vector<Object>();

		int tankIndex = -1;
		if (Manager.getInstance().isLeftListActive()) {
			tankIndex = Manager.getInstance().getTankIndexInMainLeft();
		} else if (Manager.getInstance().isRightListActive()) {
			tankIndex = Manager.getInstance().getTankIndexInMainRight();
		}

		for (int i : targetMgrIdxs) {
			IqmDataBox box = Tank.getInstance().getTankIqmDataBoxAt(tankIndex,
					i);

			// deserialize if required
			if (box instanceof IVirtualizable) {
				box = VirtualDataManager.getInstance().load(
						(IVirtualizable) box);
			}
			newSources.add(box);
		}

		// set the new sources to the clone
		wpClone.setSources(newSources);
		wpClone.setManagerIndices(targetMgrIdxs);

		// call the validator again
		try {
			boolean valid = this.operatorValidator.validate(wpClone);

			// if ok, set the new work package to the operator GUI
			// and update the parameter controlling GUI elements
			if (valid) {
				this.workPackage = wpClone;
				this.operatorGUI.setWorkPackage(this.workPackage);
			}

			// DEBUG ONLY!! simulate fail->
			// targetMgrIdxs = currMgrIdxs;

			// DEBUG ONLY!! simulate success->
			// this.workPackage = wpClone;
			// this.operatorGUI.setWorkPackage(this.workPackage);

			this.state = ExecutionState.SOURCE_UPDATE_SUCCESSFUL;
		} catch (Exception e) {
			// catch IllegalArgumentException
			// roll back the sources of the work package
			this.state = ExecutionState.SOURCE_UPDATE_FAILED;
			targetMgrIdxs = currMgrIdxs;
			// notifyUser(e);
		}

		// DEBUG OUTPUT
		// System.out.println("Tank index in the ExecutionProxy: " + tankIndex);
		// System.out.print("Target Manager indices in the ExecutionProxy: ");
		// for (int i : targetMgrIdxs)
		// System.out.print(i + "  ");
		// System.out.println();

		return targetMgrIdxs;
	}

	/**
	 * This method updates the operator GUI.
	 * 
	 * @throws Exception
	 */
	@Override
	public void updateGUI() throws Exception {
		if (this.operatorGUI != null) {
			this.operatorGUI.update();
		}
	}

	public void cancelProtocol() {
		// try to close the GUI
		try {
			this.operatorGUI.destroy();
		} catch (Exception e) {

		}
	}

	/**
	 * Clean up the the protocol and set the static instance to
	 * <code>null</code> for subsequent protocol executions.
	 */
	@Override
	public void finishProtocol() {
		this.state = ExecutionState.FINISHED;
		Application.setCurrentExecutionProtocol(null);
		GUITools.getStatusPanel()
		.getLblOperatorName()
		.setText(
				I18N.getGUILabelText("statuspanel.operatorName.idle.text"));

		// reset the button's text in the control panel
		AbstractButton b = GUITools.getMainFrame().getMainPanel()
				.getControlPanel().getButtonCopies();
		b.setText(I18N.getGUILabelText("options.buttCreateCopies.text"));

		logger.debug("Protocol for operator [" + this.operatorName
				+ "] finished.");
		System.out.println("Protocol for operator [" + this.operatorName
				+ "] finished.");
	}

	@Override
	public void setVirtualFlagToOperatorGUI(boolean virtual) {
		if (this.operatorGUI instanceof AbstractPlotOperatorGUI) {
			AbstractPlotOperatorGUI gui = (AbstractPlotOperatorGUI) this.operatorGUI;
			gui.getChckbxVirtual().setSelected(virtual);
		}

		if (this.operatorGUI instanceof AbstractImageOperatorGUI) {
			AbstractImageOperatorGUI gui = (AbstractImageOperatorGUI) this.operatorGUI;
			gui.getChckbxVirtual().setSelected(virtual);
		}
	}

	@Override
	public String getOperatorName() {
		return this.operatorName;
	}

	@Override
	public IWorkPackage getWorkPackage() {
		return workPackage;
	}

	@Override
	public IOperatorGUI getOperatorGUI() {
		return operatorGUI;
	}

	@Override
	public IOperatorDescriptor getOperatorDescriptor() {
		return operatorDescriptor;
	}

	public void setPreviewResult(IResult previewResult) {
		this.previewResult = (Result) previewResult;
	}

	public void displayPreviewResults(Result result) {
		setPreviewResult(result);
		// decide whether or not the result list should be shown
		if (!result.isMultiResult()) {
			((AbstractOperatorGUI) operatorGUI).disableMultiResultsButton();
			if (this.multiResultDialog != null) {
				this.multiResultDialog.setVisible(false);
			}
			new ResultVisualizer().display(result);
		} else {
			((AbstractOperatorGUI) operatorGUI).enableMultiResultsButton();
			if (this.multiResultDialog == null) {
				this.multiResultDialog = new MultiResultDialog(result);
				// set the location of the dialog
				int top = 10;
				int left = ((int) (((AbstractOperatorGUI) operatorGUI)
						.getLocation().x - this.multiResultDialog.getWidth() - 10));
				this.multiResultDialog.setLocation(left, top);
			} else {
				this.multiResultDialog.setResult(result);
				this.multiResultDialog.update();
			}
			// display the first item of a list
			this.multiResultDialog.getSelectorPanel()
			.displayFirstAvailableItem();
			this.multiResultDialog.setVisible(true);
		}
	}

	@Override
	public Result getPreviewResult() {
		return this.previewResult;
	}

	@Override
	public MultiResultDialog getMultiResultDialog() {
		return this.multiResultDialog;
	}

	/**
	 * Gets all sources for the currently selected manager list into a
	 * {@link Vector} of {@link IqmDataBox}es
	 * 
	 * @param managerList
	 *            left list = 0, right list = 1
	 * @return the {@link WorkPackage}
	 */
	private Vector<Object> getAllSourcesForManager(int managerList) {

		// fetch items from current tank according to the manager indices
		Vector<Object> newSources = new Vector<Object>();

		int tankIndex = -1;
		if (managerList == 0) {
			tankIndex = Manager.getInstance().getTankIndexInMainLeft();
		} else if (managerList == 1) {
			tankIndex = Manager.getInstance().getTankIndexInMainRight();
		}

		int nStackItems = Tank.getInstance().getTankDataAt(tankIndex).size();

		int[] targetMgrIdxs = new int[nStackItems];
		for (int i = 0; i < nStackItems; i++) {
			IqmDataBox box = Tank.getInstance().getTankIqmDataBoxAt(tankIndex,
					i);

			// deserialize if required
			if (box instanceof IVirtualizable) {
				box = VirtualDataManager.getInstance().load(
						(IVirtualizable) box);
			}
			newSources.add(box);
			targetMgrIdxs[i] = i;
		}

		return newSources;
	}
}
