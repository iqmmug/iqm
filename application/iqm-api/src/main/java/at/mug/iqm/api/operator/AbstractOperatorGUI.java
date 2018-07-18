package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractOperatorGUI.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.MultiResultDialog;
import at.mug.iqm.api.gui.MultiResultSelectorPanel;
import at.mug.iqm.commons.util.CommonTools;

public abstract class AbstractOperatorGUI extends JFrame implements
		IOperatorGUI, ActionListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7846934011686537233L;

	private static final Logger logger = Logger
			.getLogger(AbstractOperatorGUI.class);

	protected String opName;

	protected IWorkPackage workPackage;

	protected List<DataType> outputTypes;

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private JPanel customControls;
	private JButton btnReset;
	private JPanel controlsPanel;
	private JPanel buttonPanel;
	private JButton btnPreview;
	private JScrollPane contentScrollPane;
	private JPanel opGUIContent;
	private JSeparator separator_1;
	private OutputOptionsPanel outputOptionsPanel;
	private JScrollPane controlsScrollpane;
	private JButton btnApply;
	private Component horizontalGlue_1;
	private Component horizontalStrut;
	private JLabel lblMultiResults;
	private JPanel panel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AbstractOperatorGUI frame = new AbstractOperatorGUI() {

						/**
						 * 
						 */
						private static final long serialVersionUID = -2030548736227198733L;

						@Override
						public void actionPerformed(ActionEvent e) {

						}

						@Override
						public void windowOpened(WindowEvent e) {

						}

						@Override
						public void windowClosed(WindowEvent e) {
						}

						@Override
						public void windowIconified(WindowEvent e) {
						}

						@Override
						public void windowDeiconified(WindowEvent e) {
						}

						@Override
						public void windowActivated(WindowEvent e) {
						}

						@Override
						public void windowDeactivated(WindowEvent e) {
						}

						@Override
						public void setParameterValuesToGUI() {
						}

						@Override
						public void showPreview() {
						}

						@Override
						public void update() {
						}

						@Override
						public void reset() {

						}

						@Override
						public void updateParameterBlock() {
						}

					};
					frame.initialize();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AbstractOperatorGUI() {
		// on ESC key close frame
		enableESCOptionForClosing();

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		setTitle("AbstractOperatorGUI");
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		JPanel _pnl = new JPanel();
		_pnl.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(_pnl);
		_pnl.setLayout(new BorderLayout(0, 0));

		contentScrollPane = new JScrollPane();
		_pnl.add(contentScrollPane, BorderLayout.CENTER);
		contentScrollPane.setBorder(null);
		contentScrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));

		opGUIContent = new JPanel();
		contentScrollPane.setViewportView(opGUIContent);

		controlsPanel = new JPanel();
		controlsPanel.setBorder(null);
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));

		buttonPanel = new JPanel();
		buttonPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		controlsPanel.add(buttonPanel);

		btnPreview = new JButton(I18N.getGUILabelText("opGUI.buttPreview.text"));
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPreview();
			}
		});
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		horizontalGlue_1 = Box.createHorizontalGlue();
		buttonPanel.add(horizontalGlue_1);
		btnPreview
				.setToolTipText(I18N.getGUILabelText("opGUI.buttPreview.ttp"));
		btnPreview.setFont(new Font(btnPreview.getFont().getFamily(),
				Font.BOLD, btnPreview.getFont().getSize()));
		buttonPanel.add(btnPreview);

		btnReset = new JButton(I18N.getGUILabelText("opGUI.buttReset.text"));
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		btnReset.setToolTipText(I18N.getGUILabelText("opGUI.buttReset.ttp"));
		buttonPanel.add(btnReset);

		horizontalStrut = Box.createHorizontalStrut(20);
		buttonPanel.add(horizontalStrut);

		panel = new JPanel();
		buttonPanel.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		btnApply = new JButton(I18N.getGUILabelText("opGUI.buttApply.text"));
		panel.add(btnApply);
		btnApply.setEnabled(false);
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// get all selected items from the multiselection panel
				// and add it to the tank
				MultiResultSelectorPanel pnl = Application
						.getCurrentExecutionProtocol().getMultiResultDialog()
						.getSelectorPanel();

				if (CommonTools.checkModifiers(e.getModifiers(),
						ActionEvent.ALT_MASK)) {
					Application.getTank().addNewItems(pnl.getImageResults());
					Application.getTank().addNewItems(pnl.getPlotResults());
					Application.getTank().addNewItems(pnl.getTableResults());
					Application.getTank().addNewItems(pnl.getCustomResults());
				} else {
					Application.getTank().addNewItems(
							pnl.getSelectedImageResults());
					Application.getTank().addNewItems(
							pnl.getSelectedPlotResults());
					Application.getTank().addNewItems(
							pnl.getSelectedTableResults());
					Application.getTank().addNewItems(
							pnl.getSelectedCustomResults());
				}

			}
		});
		btnApply.setToolTipText(I18N.getGUILabelText("opGUI.buttApply.ttp"));

		lblMultiResults = new JLabel();
		panel.add(lblMultiResults);
		lblMultiResults.setBorder(new EmptyBorder(2, 2, 0, 2));
		lblMultiResults.setToolTipText(I18N
				.getGUILabelText("opGUI.lblMultiResults.ttp"));
		lblMultiResults.setEnabled(false);
		lblMultiResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				showOrHideMultiResultList();
			}
		});
		lblMultiResults.setIcon(new ImageIcon(Resources
				.getImageURL("icon.opGUI.multiresults24")));

		outputOptionsPanel = new OutputOptionsPanel();
		outputOptionsPanel.getChckbxCustoms().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// set the flag for processing images to the work
						// package
						workPackage
								.setCustomComputationEnabled(outputOptionsPanel
										.getChckbxCustoms().isSelected());

						// set preview button enabled or disabled
						setPreviewButtonState();
					}
				});
		outputOptionsPanel.getChckbxTables().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// set the flag for processing images to the work
						// package
						workPackage
								.setTableComputationEnabled(outputOptionsPanel
										.getChckbxTables().isSelected());

						// set preview button enabled or disabled
						setPreviewButtonState();
					}
				});
		outputOptionsPanel.getChckbxPlots().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// set the flag for processing images to the work
						// package
						workPackage
								.setPlotComputationEnabled(outputOptionsPanel
										.getChckbxPlots().isSelected());

						// set preview button enabled or disabled
						setPreviewButtonState();
					}
				});
		outputOptionsPanel.getChckbxImages().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// set the flag for processing images to the work
						// package
						workPackage
								.setImageComputationEnabled(outputOptionsPanel
										.getChckbxImages().isSelected());

						// set preview button enabled or disabled
						setPreviewButtonState();
					}
				});
		controlsPanel.add(outputOptionsPanel);

		separator_1 = new JSeparator();
		controlsPanel.add(separator_1);

		customControls = new JPanel();
		FlowLayout fl_customControls = (FlowLayout) customControls.getLayout();
		fl_customControls.setVgap(0);
		controlsPanel.add(customControls);

		controlsScrollpane = new JScrollPane();
		controlsScrollpane.setBorder(null);
		controlsScrollpane.setViewportBorder(null);
		controlsScrollpane.setViewportView(controlsPanel);
		_pnl.add(controlsScrollpane, BorderLayout.NORTH);
		pack();
	}

	public void showOrHideMultiResultList() {
		System.out.println("Showing PREVIEW RESULT list.");
		MultiResultDialog dialog = Application.getCurrentExecutionProtocol()
				.getMultiResultDialog();

		if (dialog.isVisible()) {
			dialog.setVisible(false);
		} else if (!dialog.isVisible()) {
			dialog.setVisible(true);
		}
	}

	/**
	 * The preview button will only be enabled, if there is something checked to
	 * be processed by the operator.
	 */
	protected void setPreviewButtonState() {
		// check which types are supported and enable/disable the preview button

		outputTypes = OperatorDescriptorFactory.createDescriptor(opName)
				.getOutputTypes();

		if (outputTypes.contains(DataType.IMAGE) && outputTypes.size() == 1) {
			if (!outputOptionsPanel.getChckbxImages().isSelected()) {
				btnPreview.setEnabled(false);
			} else {
				btnPreview.setEnabled(true);
			}
		}

		if (outputTypes.contains(DataType.PLOT) && outputTypes.size() == 1) {
			if (!outputOptionsPanel.getChckbxPlots().isSelected()) {
				btnPreview.setEnabled(false);
			} else {
				btnPreview.setEnabled(true);
			}
		}

		if (outputTypes.contains(DataType.TABLE) && outputTypes.size() == 1) {
			if (!outputOptionsPanel.getChckbxTables().isSelected()) {
				btnPreview.setEnabled(false);
			} else {
				btnPreview.setEnabled(true);
			}
		}

		if (outputTypes.contains(DataType.CUSTOM) && outputTypes.size() == 1) {
			if (!outputOptionsPanel.getChckbxCustoms().isSelected()) {
				btnPreview.setEnabled(false);
			} else {
				btnPreview.setEnabled(true);
			}
		}

		for (Component c : outputOptionsPanel.getComponents()) {
			if (c instanceof JCheckBox) {
				if (!((JCheckBox) c).isSelected()) {
					btnPreview.setEnabled(false);
				} else {
					btnPreview.setEnabled(true);
					break;
				}
			}
		}

	}

	protected void enableESCOptionForClosing() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
		getRootPane().getActionMap().put("close", new AbstractAction() {
			private static final long serialVersionUID = -5477442838772337075L;

			public void actionPerformed(ActionEvent e) {
				Window source = ((Window) getRootPane().getParent());

				windowClosing(new WindowEvent(source,
						WindowEvent.WINDOW_CLOSING));
			}
		});
	}

	protected void disableESCOptionForClosing() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		getRootPane().getActionMap().remove("close");
	}

	public JPanel getCustomControls() {
		return customControls;
	}

	public JButton getBtnReset() {
		return btnReset;
	}

	public JPanel getControlsPanel() {
		return controlsPanel;
	}

	public JPanel getOpGUIContent() {
		return opGUIContent;
	}

	public JPanel getButtonPanel() {
		return buttonPanel;
	}

	public JButton getBtnPreview() {
		return btnPreview;
	}

	public JScrollPane getScrollPane() {
		return contentScrollPane;
	}

	public void initialize() {
		logger.debug("Initializing '" + this.getClass().getName() + "'...");

		this.setAlwaysOnTop(true);

		// add custom window listener
		this.addWindowListener(this);
		this.pack();
	}

	public abstract void update() throws Exception;

	public abstract void setParameterValuesToGUI() throws Exception;

	public abstract void updateParameterBlock() throws Exception;

	public abstract void showPreview();

	/**
	 * This method updates the parameter block and sets the containing
	 * parameters to the GUI elements. Furthermore, it updates the GUI in order
	 * to reflect any new changes.
	 * 
	 * @param pb
	 *            the updated parameters for this operator
	 */
	public void updateParameterBlock(ParameterBlockIQM pb) {
		this.workPackage.setParameters(pb);
		try {
			this.setParameterValuesToGUI();
			this.update();
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.debug("The source image is missing in the operator GUI, select one or close the operator.");
		} catch (Exception e) {
			// DialogUtil
			// .getInstance()
			// .showErrorMessage(
			// "The parameters could not be updated and set to the GUI elements!",
			// e);
			logger.error("The parameters could not be updated and set to the GUI elements!");
		}
	}

	public void setVisible(boolean b) {
		super.setVisible(true);
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getOpName() {
		return this.opName;
	}

	public IWorkPackage getWorkPackage() {
		return this.workPackage;
	}

	public void setWorkPackage(IWorkPackage workPackage) {
		this.workPackage = workPackage;
	}

	public void reset() {
		// IMPLEMENT RESET BEHAVIOUR HERE THAT IS COMMON TO ALL
		// OPERATOR GUIS (IMAGE/PLOT)
		disableMultiResultsButton();
		try {
			Application.getCurrentExecutionProtocol().getMultiResultDialog()
					.dispose();
		} catch (NullPointerException ex) {
		}
	}

	public void disableInputs() {
		disableESCOptionForClosing();
		removeWindowListener(this);
	}

	public void enableInputs() {
		enableESCOptionForClosing();
		addWindowListener(this);
	}

	public void destroy() {
		if (this != null) {
			this.dispose();
		}
	}

	public void windowClosing(WindowEvent e) {
		this.destroy();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public OutputOptionsPanel getOutputOptionsPanel() {
		return outputOptionsPanel;
	}

	public JButton getBtnApply() {
		return btnApply;
	}

	public void enableMultiResultsButton() {
		this.lblMultiResults.setEnabled(true);
		this.btnApply.setEnabled(true);
	}

	public void disableMultiResultsButton() {
		this.lblMultiResults.setEnabled(false);
		this.btnApply.setEnabled(false);
	}
}
