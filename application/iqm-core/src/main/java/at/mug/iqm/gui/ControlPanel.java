package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ControlPanel.java
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


import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.InternationalFormatter;

 
 

import at.mug.iqm.api.Application;
import at.mug.iqm.api.events.VirtualFlagChangedEvent;
import at.mug.iqm.api.events.handler.IGUIUpdateEmitter;
import at.mug.iqm.api.events.handler.IGUIUpdateListener;
import at.mug.iqm.api.model.CustomDataModel;
import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.processing.IExecutionProtocol;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.WrapLayout;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.processing.CreateCopiesTask;
import at.mug.iqm.core.processing.TaskFactory;
import at.mug.iqm.core.util.CoreTools;
import at.mug.iqm.core.workflow.ExecutionProxy;
import at.mug.iqm.core.workflow.Look;
import at.mug.iqm.core.workflow.Plot;
import at.mug.iqm.core.workflow.Table;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.core.workflow.Text;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class is responsible for representing a graphical user interface. Using
 * this panel, the user can control the image processing.
 * 
 * @author Philipp Kainz
 */
public class ControlPanel extends JPanel implements ActionListener,
		ChangeListener, IGUIUpdateListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8945280479923038458L;

	// class specific logger
	  

	// class variable declaration
	private JButton buttTake;
	private JButton buttProcStack;
	private JButton buttCopies;

	private JCheckBox chbxVirtual;

	private JLabel lblNumThread;

	private JPanel scrollBarPanelNumThread;
	private JSlider sliderNumThread;
	private int numCores; // Number of available cores

	private JCheckBox chbxSerial;
	private JCheckBox chbxExService;
	private JCheckBox chbxPJ;

	private JPanel pnlCopies;
	private JLabel lblCopies;
	private JSpinner spinnerCopies;

	public ControlPanel() {
		System.out.println("IQM:  Generating new instance of '" + this.getClass().getName()
				+ "'.");

		this.buttTake = new JButton();
		this.buttProcStack = new JButton();
		this.buttCopies = new JButton();

		this.chbxVirtual = new JCheckBox();

		this.lblNumThread = new JLabel();

		this.scrollBarPanelNumThread = new JPanel();
		this.sliderNumThread = new JSlider();
		this.numCores = CoreTools.getNumberOfProcessors(); // Number of
															// available cores
		this.chbxSerial = new JCheckBox();
		this.chbxExService = new JCheckBox();
		this.chbxPJ = new JCheckBox();

		this.pnlCopies = new JPanel();
		this.lblCopies = new JLabel();
		this.spinnerCopies = new JSpinner();

		this.createAndAssemble();
	}

	/**
	 * Constructs the layout and components of this panel.
	 */
	private void createAndAssemble() {
		System.out.println("IQM:  Creating subcomponents and assembling '"
				+ this.getClass().getName() + "'.");

		this.setLayout(new WrapLayout(FlowLayout.LEFT));
		this.add(createJCheckBoxVirtual());

		this.add(createJButtonOk());
		this.add(createJButtonProcStack());

		this.add(createJCheckBoxSerial());
		this.add(createjLabelNumThread());
		this.add(createJSliderNumThread());
		this.add(createJCheckBoxExecuter());
		// this.add(createJCheckBoxPJ());
		this.createButtonGroupParallelOption();

		this.add(createJButtonCopies());
		this.add(createJPanelCopies());
	}

	/**
	 * This method initializes chbxVirtual
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox createJCheckBoxVirtual() {
		chbxVirtual = new JCheckBox(
				I18N.getGUILabelText("options.chbxVirtual.text"));
		// chbxVirtual.setMnemonic(KeyEvent.VK_H);
		chbxVirtual.setSelected(false);
		chbxVirtual.addActionListener(this);
		chbxVirtual.setActionCommand("virtual");
		chbxVirtual.setToolTipText(I18N
				.getGUILabelText("options.chbxVirtual.ttp"));
		return chbxVirtual;
	}

	/**
	 * This method initializes buttTake
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton createJButtonOk() {
		buttTake.setText(I18N.getGUILabelText("options.buttTake.text"));
		buttTake.addActionListener(this);
		buttTake.setActionCommand("ok");
		buttTake.setEnabled(true);
		buttTake.setToolTipText(I18N.getGUILabelText("options.buttTake.ttp"));
		return buttTake;
	}

	/**
	 * This method initializes buttProcStack
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton createJButtonProcStack() {
		buttProcStack.setText(I18N
				.getGUILabelText("options.buttProcStack.text"));
		buttProcStack.addActionListener(this);
		buttProcStack.setActionCommand("proccstack");
		buttProcStack.setEnabled(true);
		buttProcStack.setToolTipText(I18N
				.getGUILabelText("options.buttProcStack.ttp"));
		return buttProcStack;
	}

	/**
	 * This method initializes buttCopies
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton createJButtonCopies() {
		buttCopies.setText(I18N
				.getGUILabelText("options.buttCreateCopies.text"));
		buttCopies.addActionListener(this);
		buttCopies.setActionCommand("createcopies");
		buttCopies.setEnabled(true);
		buttCopies.setToolTipText(I18N
				.getGUILabelText("options.buttCreateCopies.ttp"));
		return buttCopies;
	}

	private JPanel createJSliderNumThread() {
		scrollBarPanelNumThread.setLayout(new BorderLayout());
		sliderNumThread = new JSlider(Adjustable.HORIZONTAL);
		sliderNumThread.setPreferredSize(new Dimension(100,20));
		sliderNumThread.setMinimum(1);
		sliderNumThread.setMaximum(2 * numCores);
		sliderNumThread.setValue(numCores);
		//.setValues(numCores, 0, 1, 2 * numCores);
		int num = sliderNumThread.getValue();
		lblNumThread.setText(I18N.getGUILabelText("options.lblNumThread.text",
				num, numCores));
		sliderNumThread.setToolTipText(I18N
				.getGUILabelText("options.scrBarNumThread.ttp"));
		sliderNumThread.addChangeListener(this);
		sliderNumThread.setEnabled(false);
		lblNumThread.setEnabled(false);
		scrollBarPanelNumThread.add(sliderNumThread, BorderLayout.CENTER);
		return scrollBarPanelNumThread;
	}

	/**
	 * This method creates a button group of the parallel execution options
	 * 
	 * @return ButtonGroup
	 */
	private ButtonGroup createButtonGroupParallelOption() {
		ButtonGroup group = new ButtonGroup();
		group.add(chbxSerial);
		group.add(chbxExService);
		group.add(chbxPJ);
		return group;
	}

	/**
	 * This method initializes chbxSerial
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox createJCheckBoxSerial() {
		chbxSerial = new JCheckBox(
				I18N.getGUILabelText("options.chbxSerial.text"));
		// chbxSerial.setMnemonic(KeyEvent.VK_H);
		chbxSerial.setToolTipText(I18N
				.getGUILabelText("options.chbxSerial.ttp"));
		chbxSerial.setSelected(true);
		lblNumThread.setEnabled(false);
		chbxSerial.addActionListener(this);
		chbxSerial.setActionCommand("serial");
		return chbxSerial;
	}

	/**
	 * This method initializes chbxExService
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox createJCheckBoxExecuter() {
		chbxExService = new JCheckBox(
				I18N.getGUILabelText("options.chbxExSrv.text"));
		// chbxExService.setMnemonic(KeyEvent.VK_H);
		chbxExService.setToolTipText(I18N
				.getGUILabelText("options.chbxExSrv.ttp"));
		chbxExService.setSelected(false);
		chbxExService.addActionListener(this);
		chbxExService.setActionCommand("executer");
		return chbxExService;
	}

	/**
	 * This method initializes jCheckBoxPj
	 * 
	 * @return javax.swing.JCheckBox
	 */
	@SuppressWarnings("unused")
	private JCheckBox createJCheckBoxPJ() {
		chbxPJ = new JCheckBox(I18N.getGUILabelText("options.chbxPJ.text"));
		// jCheckBoxPJ.setMnemonic(KeyEvent.VK_H);
		chbxPJ.setToolTipText(I18N.getGUILabelText("options.chbxPJ.ttp"));
		chbxPJ.setSelected(false);
		chbxPJ.addActionListener(this);
		chbxPJ.setActionCommand("pj");
		return chbxPJ;
	}

	/**
	 * This method initializes jJPanelCopies
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelCopies() {
		pnlCopies.setLayout(new BorderLayout());
		lblCopies = new JLabel("#: ");
		// jLabelCopies.setMinimumSize(new Dimension(40, 20));
		// jLabelCopies.setPreferredSize(new Dimension(40, 20));
		lblCopies.setHorizontalAlignment(SwingConstants.RIGHT);
		SpinnerModel sModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1); // init,
																					// min,
																					// max,
																					// step
		spinnerCopies = new JSpinner(sModel);
		spinnerCopies.setPreferredSize(new Dimension(60, 20));
		// jSpinnerCopies.setMinimumSize(new Dimension(60, 20));
		JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) spinnerCopies
				.getEditor();
		JFormattedTextField ftf = defEditor.getTextField();
		InternationalFormatter intFormatter = (InternationalFormatter) ftf
				.getFormatter();
		DecimalFormat decimalFormat = (DecimalFormat) intFormatter.getFormat();
		decimalFormat.applyPattern("#"); // decimalFormat.applyPattern("#,##0.0")
											// ;

		pnlCopies.add(lblCopies, BorderLayout.WEST);
		pnlCopies.add(spinnerCopies, BorderLayout.CENTER);
		return pnlCopies;
	}

	/**
	 * @return the jLabelNumThread
	 */
	private JLabel createjLabelNumThread() {
		lblNumThread = new JLabel();
		lblNumThread.setText(I18N.getGUILabelText("options.lblNumThread.text"));
		lblNumThread.setEnabled(true);
		return lblNumThread;
	}

	/**
	 * @return the buttTake
	 */
	public JButton getButtonTake() {
		return buttTake;
	}

	/**
	 * @return the buttProcStack
	 */
	public JButton getButtonProcStack() {
		return buttProcStack;
	}

	/**
	 * @return the buttCopies
	 */
	public JButton getButtonCopies() {
		return buttCopies;
	}

	/**
	 * @return the chbxVirtual
	 */
	public JCheckBox getCheckBoxVirtual() {
		return chbxVirtual;
	}

	/**
	 * @return the chbxSerial
	 */
	public JCheckBox getCheckBoxSerial() {
		return chbxSerial;
	}

	/**
	 * @return the chbxExService
	 */
	public JCheckBox getCheckBoxExSrv() {
		return chbxExService;
	}

	/**
	 * @return the jCheckBoxPJ
	 */
	public JCheckBox getCheckBoxPJ() {
		return chbxPJ;
	}

	/**
	 * @return the jPanelCopies
	 */
	public JPanel getPanelCopies() {
		return pnlCopies;
	}

	/**
	 * @return the jLabelCopies
	 */
	public JLabel getLabelCopies() {
		return lblCopies;
	}

	/**
	 * @return the jSpinnerCopies
	 */
	public JSpinner getSpinnerCopies() {
		return spinnerCopies;
	}

	// ------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("virtual".equals(e.getActionCommand())) { // Set IQM to virtual mode
			System.out.println("IQM:  'Virtual' checkbox has been clicked");
			Application.setVirtual(chbxVirtual.isSelected());

			try {
				// notify all launched GUIs
				ExecutionProxy protocol = (ExecutionProxy) ExecutionProxy
						.getCurrentInstance();
				if (protocol != null) {
					((IGUIUpdateListener) protocol.getOperatorGUI())
							.propertyChange(new VirtualFlagChangedEvent(this,
									chbxVirtual.isSelected()));
				}
			} catch (NullPointerException ex) {
				System.out.println("IQM:  "+ex);
			} catch (Exception ex) {
				System.out.println("IQM Error: An error occurred: " + ex);
			}
		}

		// call processing routine for the currently shown item(s)
		if ("ok".equals(e.getActionCommand())) {
			// this takes the preview item and stores it in a new Tank index
			// position.
			System.out.println("IQM:  OK/Take button has been clicked");
			System.out.println("IQM:  Preparing to put item(s) to Tank and Manager");

			Vector<IqmDataBox> resultVec = new Vector<IqmDataBox>();
			switch (GUITools.getMainFrame().getItemContent().getSelectedIndex()) {
			case 0: // image
				PlanarImage currentImage = Look.getInstance().getCurrentImage();

				PlanarImage pi;
				if (CommonTools.checkModifiers(e.getModifiers(),
						ActionEvent.ALT_MASK)) {
					if (Look.getInstance().getCurrentLookPanel()
							.getColorChannelSelectorPanel()
							.hasSelectiveChannelsDisplayed()) {
						// this gets the extracted image in the size of the
						// original
						pi = Look.getInstance().getCurrentLookPanel()
								.getColorChannelSelectorPanel()
								.getExtractedImage();
					} else {
						// otherwise get the current image
						pi = Look.getInstance().getCurrentImage();
					}

				} else {
					pi = Look.getInstance().getCurrentImage();
				}

				// if the dummy image is loaded, do nothing
				if (pi == null) {
					return;
				}
				// copy image properties
				ImageTools.copyCustomImageProperties(currentImage, pi);

				ImageModel im = new ImageModel(pi);
				try {
					im.setModelName(String.valueOf(pi.getProperty("image_name")));
					im.setFileName(String.valueOf(pi.getProperty("file_name")));
				} catch (Exception ex) {
					System.out.println("IQM Error: An error occurred: " + ex);
				}
				// add the planar image to the vector
				resultVec.add(new IqmDataBox(im));
				break;
			case 1: // plot
				List<PlotModel> plotModels = Plot.getInstance().getPlotPanel()
						.getPlotModels();
				if (plotModels == null || plotModels.isEmpty()) {
					return;
				}

				// add all models from the plot panel
				for (int i = 0; i < plotModels.size(); i++) {
					PlotModel pm = plotModels.get(i);
					resultVec.add(new IqmDataBox(pm));
				}
				break;

			case 2: // table
				TableModel tableModel = Table.getInstance().getTablePanel()
						.getTableModel(false);

				// add the model from the table panel
				resultVec.add(new IqmDataBox(tableModel));
				break;

			case 3: // text
				CustomDataModel textModel = new CustomDataModel();
				Object[] text = new Object[] { Text.getInstance().getData() };
				textModel.setContent(text);

				// add the model from the table panel
				resultVec.add(new IqmDataBox(textModel));
				break;

			default:
				return;
			}

			// pass new instance of vector to tank (sets icons, sets images in
			// tank panel)
			Tank.getInstance().addNewItems(resultVec);
		}

		if ("proccstack".equals(e.getActionCommand())) { // Stack processing
			// check for any launched operator (protocol)
			IExecutionProtocol protocol = Application
					.getCurrentExecutionProtocol();

			if (protocol == null) {
				return;
			} else {

				System.out.println("IQM:  Preparing to process image stack...");
				Application.setVirtual(chbxVirtual.isSelected());
				try {
					if (chbxSerial.isSelected()) {
						protocol.executeSerialProcessing();
					} else if (chbxExService.isSelected()) {
						protocol.executeParallelProcessing(sliderNumThread
								.getValue());
					} else if (chbxPJ.isSelected()) {
						// NOT IMPLEMENTED
					}
				} catch (Exception e1) {
					System.out.println("IQM Error: An error occurred: " + e1);
				}
			}
		}

		if ("createcopies".equals(e.getActionCommand())) {
			int numCopies = ((Number) spinnerCopies.getValue()).intValue();

			System.out.println("IQM:  Preparing to create " + numCopies
					+ " copies/samples...");

			// check for any launched operator (protocol)
			IExecutionProtocol protocol = Application
					.getCurrentExecutionProtocol();
			// check whether or not any item is loaded to the manager
			// get currently displayed item
			int currentManagerItemIndex = Application.getManager()
					.getCurrItemIndex();

			IWorkPackage wp = null;
			CreateCopiesTask task = null;
			try {
				// no operator is launched and no item is displayed
				if (protocol == null && currentManagerItemIndex == -1) {
					System.out.println("IQM:  No protocol is launched and no item is selected in any manager list, returning.");
					return;
				}

				// no operator is launched but an item is displayed
				else if (protocol == null && currentManagerItemIndex != -1) {
					System.out.println("IQM:  No protocol is launched but an item is selected in a manager list, continuing.");
					// continue and construct a new task for creating plain
					// copies of the currently selected (displayed) item
					task = (CreateCopiesTask) TaskFactory.getInstance()
							.createCopiesTask(null, numCopies,
									Application.isVirtual());

					// run the task;
					task.execute();
				}

				// an operator is launched but no item is displayed
				else if (protocol != null && currentManagerItemIndex == -1) {
					System.out.println("IQM:  A protocol is launched but no item is selected in any manager list, continuing.");
					// continue and create new samples with the parameters from
					// the launched protocol
					// take the work package from the currently launched
					// operator, this contains the currently selected parameters
					// as well as the operator to be executed
					wp = protocol.getWorkPackage();

					// construct the task with the parameters of the currently
					// launched operator
					task = (CreateCopiesTask) TaskFactory.getInstance()
							.createCopiesTask(wp, numCopies,
									Application.isVirtual());

					// setProgress(int) of the stack progress bar
					task.addPropertyChangeListener(GUITools.getStatusPanel());
					// for the single progress bar updates by the operator
					// bar
					if (task.getOperator() != null) {
						task.getOperator().addProgressListener(
								GUITools.getStatusPanel(), "operatorProgress");
					}

					// run the task;
					task.execute();
				}

				// an operator is launched and an item is displayed
				else if (protocol != null && currentManagerItemIndex != -1) {
					System.out.println("IQM:  A protocol is launched and an item is selected in any manager list, continuing.");
					// continue and create new samples with the parameters from
					// the launched protocol
					// take the work package from the currently launched
					// operator, this contains the currently selected parameters
					// as well as the operator to be executed
					wp = protocol.getWorkPackage();

					// construct the task with the parameters of the currently
					// launched operator
					task = (CreateCopiesTask) TaskFactory.getInstance()
							.createCopiesTask(wp, numCopies,
									Application.isVirtual());

					// setProgress(int) of the stack progress bar
					task.addPropertyChangeListener(GUITools.getStatusPanel());
					// for the single progress bar updates by the operator
					// bar
					if (task.getOperator() != null) {
						task.getOperator().addProgressListener(
								GUITools.getStatusPanel(), "operatorProgress");
					}

					// run the task;
					task.execute();
				}

			} catch (NullPointerException npe) {
				System.out.println("IQM Info: There is no image to create a copy from! " + npe);
				System.out.println("IQM:  An error occurred: "+ npe);
				return;
			} catch (Exception e1) {
				System.out.println("IQM Error: An error occurred: " + e1);
			}
		}

		if ("serial".equals(e.getActionCommand())) {
			if (chbxSerial.isSelected()) {
				lblNumThread.setEnabled(false);
				sliderNumThread.setEnabled(false);
			} else {
				lblNumThread.setEnabled(true);
				sliderNumThread.setEnabled(true);
			}

		}

		if ("executer".equals(e.getActionCommand())) {
			if (chbxExService.isSelected()) {
				lblNumThread.setEnabled(true);
				sliderNumThread.setEnabled(true);
			} else {
				lblNumThread.setEnabled(false);
				sliderNumThread.setEnabled(false);
			}
		}

		if ("pj".equals(e.getActionCommand())) {
			if (chbxPJ.isSelected()) {
				lblNumThread.setEnabled(true);
				sliderNumThread.setEnabled(true);
			} else {
				lblNumThread.setEnabled(false);
				sliderNumThread.setEnabled(false);
			}
		}
	}

	/**
	 * This method reacts on {@link JSlider} state changes. The new number of
	 * threads for parallel processing is set.
	 * 
	 * @param e
	 */
	@Override
	public void stateChanged(ChangeEvent e) {

		Object obE = e.getSource();
		if (obE instanceof JSlider) {
			if (obE == sliderNumThread) {
				int num = sliderNumThread.getValue();
				lblNumThread.setText(I18N.getGUILabelText(
						"options.lblNumThread.text", num, numCores));
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt instanceof VirtualFlagChangedEvent) {
			if (evt.getSource() instanceof IGUIUpdateEmitter) {
				if ((Boolean) evt.getNewValue() == true) {
					this.getCheckBoxVirtual().setSelected(true);
					Application.setVirtual(true);
				} else if ((Boolean) evt.getNewValue() == false) {
					this.getCheckBoxVirtual().setSelected(false);
					Application.setVirtual(false);
				}
			}
		}
	}
}
