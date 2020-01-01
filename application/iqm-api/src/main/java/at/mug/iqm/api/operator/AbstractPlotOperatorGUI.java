package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractPlotOperatorGUI.java
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


import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JSeparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.events.VirtualFlagChangedEvent;
import at.mug.iqm.api.events.handler.IGUIUpdateEmitter;
import at.mug.iqm.api.events.handler.IGUIUpdateListener;
import at.mug.iqm.api.gui.IAutoPreviewable;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.persistence.VirtualDataManager;


public abstract class AbstractPlotOperatorGUI extends AbstractOperatorGUI
		implements IAutoPreviewable, IGUIUpdateEmitter, IGUIUpdateListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 15646846846212L;

	protected static final Logger logger = LogManager.getLogger(AbstractPlotOperatorGUI.class);

	private PreferencesPanel preferencesPanel;
	private JCheckBox chckbxVirtual;
	private boolean isVirtualBefore;
	private CustomPlotOptionsPanel customPlotOptionsPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AbstractPlotOperatorGUI frame = new AbstractPlotOperatorGUI() {

						/**
						 * 
						 */
						private static final long serialVersionUID = -9199357711123291063L;

						@Override
						public void actionPerformed(ActionEvent e) {
						}

						@Override
						public void setParameterValuesToGUI() {
						}

						@Override
						public void updateParameterBlock() {
						}

						@Override
						public void update() {
						}

						@Override
						public boolean isAutoPreviewSelected() {
							return false;
						}

						@Override
						public void addGUIUpdateListener(
								PropertyChangeListener listener,
								String propertyName) {

						}

						@Override
						public PropertyChangeListener[] getGUIUpdateListeners(
								String propertyName) {
							return null;
						}

						@Override
						public void fireVirtualFlagChanged(boolean virtual) {

						}

					};
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
	public AbstractPlotOperatorGUI() {
		super();
		FlowLayout flowLayout = (FlowLayout) getOpGUIContent().getLayout();
		flowLayout.setVgap(0);
		getScrollPane().setViewportBorder(BorderFactory.createEmptyBorder());
		setTitle("AbstractPlotOperatorGUI");
		getCustomControls().setLayout(
				new BoxLayout(getCustomControls(), BoxLayout.Y_AXIS));

		customPlotOptionsPanel = new CustomPlotOptionsPanel();
		getCustomControls().add(customPlotOptionsPanel);

		customPlotOptionsPanel.getChckbxAutoPreview().setText(
				I18N.getGUILabelText("opGUI.chbxAutoPreview.text"));
		customPlotOptionsPanel.getChckbxAutoPreview().setToolTipText(
				I18N.getGUILabelText("opGUI.chbxAutoPreview.ttp"));

		chckbxVirtual = new JCheckBox(I18N.getGUILabelText("opGUI.chbxVirtual.text"));
		chckbxVirtual.setToolTipText(I18N.getGUILabelText("opGUI.chbxVirtual.ttp"));
		chckbxVirtual.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionEventVirtual(e);
			}
		});
		customPlotOptionsPanel.add(chckbxVirtual);

		preferencesPanel = new PreferencesPanel();
		getCustomControls().add(preferencesPanel);

		JSeparator separator = new JSeparator();
		getControlsPanel().add(separator);

		pack();
	}

	public void initialize() {
		super.initialize();

		this.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth() * (2d / 3d)), 10);

		this.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.application.orange.32x32")).getImage());

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// set the virtual check box automatically to true
		// on closing set it to the state of before opening the GUI
		isVirtualBefore = Application.isVirtual();
		logger.debug("Window opened. Is now virtual (before operator GUI launch): "
				+ isVirtualBefore);
		chckbxVirtual.setSelected(isVirtualBefore);
	}

	public void showPreview() {
		/*
		 * NEW IMPLEMENTATION
		 */
		if (this.getBtnPreview().isEnabled()){
			System.out
			.println("####################################### Performing PLOT preview!");
			Application.getCurrentExecutionProtocol().executePreview(
					this.workPackage, this);
		}
	}

	public abstract void update();

	public void destroy() {
		logger.debug("Destroying...");
		this.getCustomPlotOptions().getChckbxAutoPreview().setSelected(false);
		this.reset();
		if (this != null) {
			if (!isVirtualBefore && chckbxVirtual.isSelected()) {
				this.fireVirtualFlagChanged(false);
			} else if (isVirtualBefore && !chckbxVirtual.isSelected()) {
				this.fireVirtualFlagChanged(false);
			}
			this.dispose();
			try {
				Application.getCurrentExecutionProtocol().finishProtocol();
			} catch (NullPointerException e) {
				logger.error("The execution proxy is not available! " + e);
			}
		}
	}

	public void reset() {
		// set GUI elements to default
		logger.debug("########################## Resetting Starting");
		System.out.println("########################## Resetting Starting");

		// execute common reset behavior
		super.reset();

		Application.getManager().resetTogglePreviewIfRunning();

		IqmDataBox box = Application.getTank().getSelectedIqmDataBox();
		if (box != null && box.getDataType() == DataType.PLOT) {
			if (box instanceof IVirtualizable) {
				box = VirtualDataManager.getInstance().load(
						(IVirtualizable) box);
			}
			Application.getPlot().setNewData(box.getPlotModel(), null);
		}

		ParameterBlockIQM pb = this.workPackage
				.getParameters().resetParametersOnly();
		this.updateParameterBlock(pb);

		// set preferences to default
		this.getPreferencesPanel()
				.getPreferenceBox()
				.getModel()
				.setSelectedItem(
						I18N
								.getGUILabelText("templateManager.templates.default.text"));
		this.getPreferencesPanel().setLastChosenTemplateIndex(
				this.getPreferencesPanel().getPreferenceBox()
						.getSelectedIndex());

		System.out
				.println("############################## Resetting finished.");
	}

	public abstract void setParameterValuesToGUI();

	public abstract void updateParameterBlock();

	@Override
	public void addGUIUpdateListener(PropertyChangeListener listener,
			String propertyName) {
		if (propertyName == null) {
			// add the property change listener anyway
			this.pcs.addPropertyChangeListener(listener);
		} else {
			// check if the listener is already registered to the property.
			PropertyChangeListener[] existinglisteners = this.pcs
					.getPropertyChangeListeners(propertyName);
			if (existinglisteners.length != 0) {
				for (PropertyChangeListener pcl : existinglisteners) {
					// add only, if it is not yet registered
					if (pcl != listener) {
						this.pcs.addPropertyChangeListener(propertyName,
								listener);
					}
				}
			} else {
				this.pcs.addPropertyChangeListener(propertyName, listener);
			}
		}
	}

	@Override
	public PropertyChangeListener[] getGUIUpdateListeners(String propertyName) {
		PropertyChangeListener[] listeners;
		if (propertyName == null) {
			listeners = this.pcs.getPropertyChangeListeners();
		} else {
			listeners = this.pcs.getPropertyChangeListeners(propertyName);
		}
		return listeners;
	}

	@Override
	public void fireVirtualFlagChanged(boolean virtual) {
		this.pcs.firePropertyChange(new VirtualFlagChangedEvent(this, virtual));
	}

	public boolean isAutoPreviewSelected() {
		return getCustomPlotOptions().getChckbxAutoPreview().isSelected();
	}

	public PreferencesPanel getPreferencesPanel() {
		return preferencesPanel;
	}

	public JCheckBox getChckbxVirtual() {
		return chckbxVirtual;
	}

	/**
	 * This method handles an action event.
	 */
	protected void actionEventVirtual(ActionEvent e) {
		logger.debug("Virtual Checkbox isSelected --> " + chckbxVirtual.isSelected());
		this.fireVirtualFlagChanged(chckbxVirtual.isSelected());
	}

	public CustomPlotOptionsPanel getCustomPlotOptions() {
		return customPlotOptionsPanel;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt instanceof VirtualFlagChangedEvent) {
			if ((Boolean) evt.getNewValue() == true) {
				chckbxVirtual.setSelected(true);
				Application.setVirtual(true);
			} else if ((Boolean) evt.getNewValue() == false) {
				chckbxVirtual.setSelected(false);
				Application.setVirtual(false);
			}
		}
	}
}
