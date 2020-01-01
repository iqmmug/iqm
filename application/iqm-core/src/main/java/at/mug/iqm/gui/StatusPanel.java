package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: StatusPanel.java
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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.RepaintManager;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.events.OperatorCancelledEvent;
import at.mug.iqm.api.events.OperatorProgressEvent;
import at.mug.iqm.api.gui.IStatusPanel;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.OperatingSystem;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.processing.PreviewProcessingTask;
import at.mug.iqm.core.workflow.ExecutionProxy;

/**
 * This class is the container for the progress bar, and other relevant
 * information. It contains the currently selected operator.
 * 
 * @author Philipp Kainz
 */
public class StatusPanel extends JPanel implements ActionListener, IStatusPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1823147602305885117L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(StatusPanel.class);

	// class variable declaration
	private JPanel statusPanelContent;
	private JPanel leftPanel;

	private JProgressBar progressBarSingleOperation;
	private JProgressBar progressBarStack;
	private JLabel lblOperatorName;
	private JLabel lblTaskName;

	private JLabel ioProcessingLabel;

	private AbstractProcessingTask processingTask;
	private JSeparator separator;

	/**
	 * 
	 * This is the constructor.
	 */
	public StatusPanel() {
		super();

		logger.debug("Constructing '" + this.getClass().getName() + "'...");

		this.statusPanelContent = new JPanel();
		statusPanelContent.setBorder(new EmptyBorder(2, 0, 0, 0));

		this.progressBarSingleOperation = new JProgressBar();
		this.progressBarStack = new JProgressBar();

		this.leftPanel = new JPanel(new BorderLayout(5, 0));
		// this.leftPanel.setBackground(Color.blue);

		this.lblOperatorName = new JLabel();
		this.lblTaskName = new JLabel();

		this.ioProcessingLabel = new JLabel(new ImageIcon(
				Resources.getImageURL("icon.ioProcessing")));
		this.ioProcessingLabel.setToolTipText("I/O operation in progress");
		this.ioProcessingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0,
				0, 5));

		this.createAndAssemble();

		this.validate();

		logger.debug("Done.");
	}

	private void createAndAssemble() {
		this.setLayout(new BorderLayout(0, 0));
		this.setPreferredSize(new Dimension(600, 24));
		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		separator = new JSeparator();

		if (!OperatingSystem.isMac()) {
			add(separator, BorderLayout.NORTH);
		}

		this.statusPanelContent.setLayout(new BorderLayout(5, 0));
		this.leftPanel.add(this.createLblOperatorName(), BorderLayout.CENTER);
		this.statusPanelContent.add(this.leftPanel, BorderLayout.WEST);

		JPanel tmpSingle = new JPanel(new BorderLayout(10, 0));
		tmpSingle.add(this.createProgressBarSingleOperation(),
				BorderLayout.CENTER);
		this.statusPanelContent.add(tmpSingle, BorderLayout.EAST);

		JPanel tmpStack = new JPanel(new BorderLayout(10, 0));
		tmpStack.add(this.createLblTaskName(), BorderLayout.CENTER);
		tmpStack.add(this.createProgressBarStack(), BorderLayout.EAST);
		this.statusPanelContent.add(tmpStack, BorderLayout.CENTER);

		this.add(this.statusPanelContent);
	}

	/**
	 * Shows the IO processing icon.
	 */
	@Override
	public void showIOProcessingIcon() {
		leftPanel.add(this.ioProcessingLabel, BorderLayout.EAST);
		leftPanel.revalidate();
		leftPanel.repaint();
		logger.debug("Showing I/O processing label.");
	}

	/**
	 * Hides the IO processing icon.
	 */
	@Override
	public void hideIOProcessingIcon() {
		leftPanel.remove(this.ioProcessingLabel);
		leftPanel.revalidate();
		leftPanel.repaint();
		logger.debug("Hiding I/O processing label.");
	}

	/**
	 * Initializes the progress bar for single operations.
	 * 
	 * @return the constructed progress bar for single operations
	 */
	private JProgressBar createProgressBarSingleOperation() {
		this.progressBarSingleOperation
				.setPreferredSize(new Dimension(200, 20));
		this.progressBarSingleOperation.setIndeterminate(true);
		return this.progressBarSingleOperation;
	}

	/**
	 * Initializes the progress bar for stack operations.
	 * 
	 * @return the constructed progress bar for stack operations
	 */
	private JProgressBar createProgressBarStack() {
		this.progressBarStack.setPreferredSize(new Dimension(200, 20));
		this.progressBarStack.setIndeterminate(false);
		return this.progressBarStack;
	}

	/**
	 * Constructs the label containing operator names.
	 * 
	 * @return the label containing the operator names
	 */
	private JLabel createLblOperatorName() {
		this.lblOperatorName.setText(I18N
				.getGUILabelText("statuspanel.operatorName.idle.text"));
		this.lblOperatorName.setBorder(BorderFactory.createEmptyBorder(0, 5, 0,
				0));
		this.lblOperatorName.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)
						&& e.getClickCount() == 2) {
					try {
						((Window) ExecutionProxy.getCurrentInstance()
								.getOperatorGUI()).toFront();
					} catch (NullPointerException ignored) {
					}
				}
			}
		});
		// this.lblOperatorName.setBorder(BorderFactory.createLineBorder(Color.blue));
		return this.lblOperatorName;
	}

	/**
	 * Constructs the label containing the task name.
	 * 
	 * @return the label containing the task name
	 */
	private JLabel createLblTaskName() {
		this.lblTaskName.setText("");
		this.lblTaskName.setHorizontalAlignment(SwingConstants.TRAILING);
		return this.lblTaskName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand());
	}

	/**
	 * @return the lblOperatorName
	 */
	@Override
	public JLabel getLblOperatorName() {
		return lblOperatorName;
	}

	/**
	 * @param lblOperatorName
	 *            the lblOperatorName to set
	 */
	public void setLblOperatorName(JLabel lblOperatorName) {
		this.lblOperatorName = lblOperatorName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.mug.iqm.gui.IStatusPanel#getProcessingTask()
	 */
	@Override
	public AbstractProcessingTask getProcessingTask() {
		return this.processingTask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.mug.iqm.gui.IStatusPanel#setProcessingTask(com.pluggableapp.api.processing
	 * .AbstractProcessingTask)
	 */
	@Override
	public void setProcessingTask(AbstractProcessingTask task) {
		this.processingTask = task;
	}

	/**
	 * @return the statusPanelContent
	 */
	public JPanel getStatusPanelContent() {
		return statusPanelContent;
	}

	/**
	 * @param statusPanelContent
	 *            the statusPanelContent to set
	 */
	public void setStatusPanelContent(JPanel statusPanelContent) {
		this.statusPanelContent = statusPanelContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.mug.iqm.gui.IStatusPanel#getProgressBarSingleOperation()
	 */
	@Override
	public JProgressBar getProgressBarSingleOperation() {
		return progressBarSingleOperation;
	}

	/**
	 * @param progressBarSingleOperation
	 *            the progressBarSingleOperation to set
	 */
	public void setProgressBarSingleOperation(
			JProgressBar progressBarSingleOperation) {
		this.progressBarSingleOperation = progressBarSingleOperation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.mug.iqm.gui.IStatusPanel#getProgressBarStack()
	 */
	@Override
	public JProgressBar getProgressBarStack() {
		return progressBarStack;
	}

	/**
	 * @param progressBarStack
	 *            the progressBarStack to set
	 */
	public void setProgressBarStack(JProgressBar progressBarStack) {
		this.progressBarStack = progressBarStack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.mug.iqm.gui.IStatusPanel#resetProgressBarValueStack()
	 */
	@Override
	public void resetProgressBarValueStack() {
		this.setProgressBarValueStack(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.mug.iqm.gui.IStatusPanel#setProgressBarValueStack(int)
	 */
	@Override
	public void setProgressBarValueStack(int value) {
		// System.out.println(Thread.currentThread().getName() +
		// " is setting the progress bar stack." );

		if (value > 0 && value <= 100) {
			// stack bar is working
			this.progressBarStack.setStringPainted(true);
			this.progressBarStack.setString(I18N.getGUILabelText(
					"statuspanel.progressBarStack.text", value));
			this.progressBarStack.setValue(value);

			// if there is no processing task, print "Busy" and
			// "Processing, please wait..."
			if (this.processingTask == null) {
				this.lblTaskName
						.setText(I18N
								.getGUILabelText("statuspanel.taskName.processing.generic.text")); // Processing,
																									// please
																									// wait
																									// ...
				this.lblOperatorName
						.setText(I18N
								.getGUILabelText("statuspanel.operatorName.working.text")); // Busy
			} else {
				this.lblTaskName.setText(I18N.getGUILabelText(
						"statuspanel.taskName.processing.text",
						this.processingTask.getOperator().getName())); // "Processing [OpName], please wait..."
				this.lblOperatorName
						.setText(this.processingTask.getOperator().getName()
								+ " - "
								+ I18N.getGUILabelText("statuspanel.operatorName.working.text")); // "OpName - Busy"
			}

			// NECESSARY for continuous updates of the progress bar
			this.progressBarStack.paint(this.progressBarStack.getGraphics());
			this.progressBarStack.setVisible(true);
			// System.out.println(this.progressBarStack.isVisible());
			// this.forceRepaint();

		} else {
			// stack bar is idle
			if (this.processingTask == null) {
				this.lblTaskName.setText(""); // ""
				this.lblOperatorName.setText(I18N
						.getGUILabelText("statuspanel.operatorName.idle.text")); // Ready
			} else {
				this.lblTaskName.setText(""); // ""
				this.lblOperatorName
						.setText(this.processingTask.getOperator().getName()
								+ " - "
								+ I18N.getGUILabelText("statuspanel.operatorName.idle.text")); // "OpName - Ready"
			}
			this.progressBarStack.setVisible(false);
			// System.out.println(this.progressBarStack.isVisible());
			// this.forceRepaint();
		}
	}

	/**
	 * @return the lblTaskName
	 */
	public JLabel getLblTaskName() {
		return lblTaskName;
	}

	/**
	 * @param lblTaskName
	 *            the lblTaskName to set
	 */
	public void setLblTaskName(JLabel lblTaskName) {
		this.lblTaskName = lblTaskName;
	}

	/**
	 * This method listens to the property changes of a <code>SwingWorker</code>
	 * , if specified. The <code>execute()</code> method instantiates such a
	 * task.
	 * 
	 * @param evt
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		/*
		 * This event is emitted by the SwingWorker task SingleProcessingTask
		 */
		// turn processing progress bar ON
		if ("singleTaskRunning".equals(evt.getPropertyName())
				&& evt.getOldValue().equals(0) && evt.getNewValue().equals(1)) {

			// write appropriate strings to the text fields
			if (this.processingTask != null) {
				System.out.println(processingTask.getOperator());
				String opName = this.processingTask.getOperator().getName();

				this.lblTaskName.setText(I18N.getGUILabelText(
						"statuspanel.taskName.processing.text", opName));// "Processing [OpName], please wait..."
				System.out.println(opName);
				this.lblOperatorName
						.setText(opName
								+ " - "
								+ I18N.getGUILabelText("statuspanel.operatorName.working.text")); // "OpName - Busy"
			} else {
				this.lblTaskName
						.setText(I18N
								.getGUILabelText("statuspanel.taskName.processing.generic.text")); // "Processing, please wait..."
				this.lblOperatorName
						.setText(I18N
								.getGUILabelText("statuspanel.operatorName.working.text")); // "Busy"
			}

			// set the bar visible
			this.getProgressBarSingleOperation().setVisible(true);
		}

		/*
		 * This event is emitted by the SwingWorker task SingleProcessingTask
		 */
		// turn processing progress bar OFF
		else if ("singleTaskRunning".equals(evt.getPropertyName())
				&& evt.getOldValue().equals(1) && evt.getNewValue().equals(0)) {
			try {
				// write the appropriate strings for idle state
				if (this.processingTask != null) {
					String opName = this.processingTask.getOperator().getName();
					this.lblTaskName.setText(""); // no label
					this.lblOperatorName
							.setText(opName
									+ " - "
									+ I18N.getGUILabelText("statuspanel.operatorName.idle.text")); // "OpName - Ready"
				} else {
					this.lblTaskName.setText(""); // no label
					this.lblOperatorName
							.setText(I18N
									.getGUILabelText("statuspanel.operatorName.idle.text")); // Ready
				}

			} catch (Exception e) {
				logger.error("An error occurred: ", e);
			} finally {
				// set the bar invisible
				try {
					this.getProgressBarSingleOperation()
							.setStringPainted(false);
					if (!getProgressBarSingleOperation().isIndeterminate()) {
						this.getProgressBarSingleOperation().setIndeterminate(
								true);
					}
				} catch (Throwable t) {
					logger.error("An error occurred: ", t);
				} finally {
					this.getProgressBarSingleOperation().setVisible(false);
				}
			}
		}

		/*
		 * This clause is for listening to SwingWorker progress updates.
		 */
		else if ("progress".equals(evt.getPropertyName())) {
			if (!getProgressBarStack().isIndeterminate()) {
				int progress = (Integer) evt.getNewValue();
				logger.debug(progress + "% processed by "
						+ evt.getSource().toString());
				this.setProgressBarValueStack(progress);
			}
		}

		/*
		 * This clause is for listening to operator updates within an
		 * SwingWorker task.
		 */
		else if (evt instanceof OperatorProgressEvent) {
			logger.debug(evt.getSource() + " --- " + evt.getPropertyName()
					+ ": " + evt.getNewValue());
			// paint the value
			this.getProgressBarSingleOperation().setStringPainted(true);
			if (getProgressBarSingleOperation().isIndeterminate()) {
				this.getProgressBarSingleOperation().setIndeterminate(false);
			}
			this.getProgressBarSingleOperation().setValue(
					(Integer) evt.getNewValue());
		}

		// PK: 2012 10 17: for now it is just possible to cancel a preview task!
		else if (evt instanceof OperatorCancelledEvent) {
			try {
				if (this.processingTask != null
						&& this.processingTask instanceof PreviewProcessingTask
						&& this.processingTask.getChildTask().getOperator()
								.isCancelable()) {
					logger.info("Canceling: " + this.processingTask.toString());
					boolean cancelSuccess = this.processingTask.cancel(true);
					if (cancelSuccess) {
						logger.info("Successfully cancelled: "
								+ this.processingTask.getOperator().getName());
					} else {
						logger.info("Cancellation failed for "
								+ this.processingTask.getOperator().getName());
					}
					this.getProgressBarSingleOperation().setValue(0);
					this.setProgressBarValueStack(0);
					this.lblOperatorName
							.setText(I18N
									.getGUILabelText("statuspanel.operatorName.idle.text"));
					this.processingTask = null;
				} else {
					DialogUtil
							.getInstance()
							.showDefaultInfoMessage(
									"This operator is not declared cancelable, please wait until it finishes!");
				}
			} catch (Exception e) {
				logger.error("An error occurred: " + e);
			}
		}

		this.repaint();
	}

	public void forceRepaint() {
		RepaintManager mgr = RepaintManager.currentManager(this);
		mgr.markCompletelyDirty(this);
		logger.trace("Complete repainting of the GUI.");
		mgr.paintDirtyRegions();
	}

}
