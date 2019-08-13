package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractImageOperatorGUI.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2019 Helmut Ahammer, Philipp Kainz
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

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.events.VirtualFlagChangedEvent;
import at.mug.iqm.api.events.handler.IGUIUpdateEmitter;
import at.mug.iqm.api.events.handler.IGUIUpdateListener;
import at.mug.iqm.api.gui.IAutoPreviewable;
import at.mug.iqm.api.gui.IImageInvertible;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.persistence.VirtualDataManager;

public abstract class AbstractImageOperatorGUI extends AbstractOperatorGUI
		implements IImageInvertible, IAutoPreviewable, IGUIUpdateEmitter,
		IGUIUpdateListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger
			.getLogger(AbstractImageOperatorGUI.class);

	private CustomImageOptionsPanel customImageOptions;
	private PreferencesPanel preferencesPanel;
	private JCheckBox chckbxVirtual;
	private boolean isVirtualBefore;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AbstractImageOperatorGUI frame = new AbstractImageOperatorGUI() {

						/**
						 * 
						 */
						private static final long serialVersionUID = -5273393044138081711L;

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
	public AbstractImageOperatorGUI() {
		super();

		// on close window the close method is called
		getScrollPane().setViewportBorder(BorderFactory.createEmptyBorder());
		setTitle("AbstractImageOperatorGUI");
		getCustomControls().setLayout(
				new BoxLayout(getCustomControls(), BoxLayout.Y_AXIS));

		customImageOptions = new CustomImageOptionsPanel();
		// add custom listeners
		customImageOptions.getChckbxAutoPreview().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						actionEventAutoPreview(e);
					}
				});

		customImageOptions.getChckbxAutoPreview().setText(
				I18N.getGUILabelText("opGUI.chbxAutoPreview.text"));
		customImageOptions.getChckbxAutoPreview().setToolTipText(
				I18N.getGUILabelText("opGUI.chbxAutoPreview.ttp"));

		customImageOptions.getChckbxInvertOutputImage().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						actionEventInvert(e);
					}
				});
		customImageOptions.getChckbxInvertOutputImage().setText(
				I18N.getGUILabelText("opGUI.chbxInvert.text"));
		customImageOptions.getChckbxInvertOutputImage().setToolTipText(
				I18N.getGUILabelText("opGUI.chbxInvert.ttp"));

		getCustomControls().add(customImageOptions);

		chckbxVirtual = new JCheckBox(I18N.getGUILabelText("opGUI.chbxVirtual.text"));
		chckbxVirtual.setToolTipText(I18N.getGUILabelText("opGUI.chbxVirtual.ttp"));
		chckbxVirtual.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionEventVirtual(e);
			}
		});
		customImageOptions.add(chckbxVirtual);

		preferencesPanel = new PreferencesPanel();
		getCustomControls().add(preferencesPanel);

		JSeparator separator = new JSeparator();
		getControlsPanel().add(separator);

		pack();
	}

	@Override
	public void initialize() {
		logger.debug("Now initializing...");
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

	public CustomImageOptionsPanel getCustomImageOptions() {
		return customImageOptions;
	}

	public PreferencesPanel getPreferencesPanel() {
		return preferencesPanel;
	}

	public boolean isInvertSelected() {
		return getCustomImageOptions().getChckbxInvertOutputImage()
				.isSelected();
	}

	public boolean isAutoPreviewSelected() {
		return getCustomImageOptions().getChckbxAutoPreview().isSelected();
	}

	public void showPreview() {
		/*
		 * NEW IMPLEMENTATION Request a new preview task from the factory and
		 * execute it. The source images are fetched from the manager list(s)
		 */
		if (this.getBtnPreview().isEnabled()){
			System.out
			.println("####################################### Performing IMAGE preview!");
			Application.getCurrentExecutionProtocol().executePreview(
					this.workPackage, this);
		}
	}

	public abstract void update();

	/**
	 * This method resets the current operator GUI. The parameter block will be
	 * deleted and the preview image in <code>Manager</code> is being set to
	 * null.
	 */
	public void reset() {
		// set gui elements to default
		logger.debug("########################## Resetting Starting");
		System.out.println("########################## Resetting Starting");

		// execute common reset behavior
		super.reset();

		Application.getManager().resetTogglePreviewIfRunning();
		Application.getManager().setPreviewImage(null);

		IqmDataBox box = Application.getTank().getSelectedIqmDataBox();
		if (box != null && box.getDataType() == DataType.IMAGE) {
			if (box instanceof IVirtualizable) {
				box = VirtualDataManager.getInstance().load(
						(IVirtualizable) box);
			}
			Application.getLook().setImage(box.getImage());
		}

		// reset the parameters only
		ParameterBlockIQM pb = this.workPackage.getParameters()
				.resetParametersOnly();
		this.updateParameterBlock(pb);

		// set preferences to default
		this.getPreferencesPanel()
				.getPreferenceBox()
				.getModel()
				.setSelectedItem(
						I18N.getGUILabelText("templateManager.templates.default.text"));
		this.getPreferencesPanel().setLastChosenTemplateIndex(
				this.getPreferencesPanel().getPreferenceBox()
						.getSelectedIndex());

		// if you don't want to reset the "invert" check box, simply comment
		// the next line
		this.getCustomImageOptions().getChckbxInvertOutputImage()
				.setSelected(false);

		System.out
				.println("############################## Resetting finished.");
	}

	/**
	 * This method requires a custom implementation.
	 */
	public abstract void setParameterValuesToGUI();

	/**
	 * This method requires a custom implementation.
	 */
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

	/**
	 * This method destroys the GUI. It may be overridden by the subclasses.
	 */
	@Override
	public void destroy() {
		logger.debug("Destroying. Was virtual before operator GUI launch: "
				+ isVirtualBefore);
		this.getCustomImageOptions().getChckbxAutoPreview().setSelected(false);
		this.reset();
		if (this != null) {
			if (!isVirtualBefore && chckbxVirtual.isSelected()) {
				this.fireVirtualFlagChanged(false);
			} else if (isVirtualBefore && !chckbxVirtual.isSelected()) {
				this.fireVirtualFlagChanged(true);
			}
			this.dispose();
			try {
				Application.getCurrentExecutionProtocol().finishProtocol();
			} catch (NullPointerException e) {
				logger.debug("The execution proxy is not available! " + e);
			}
		}
	}

	/**
	 * This method handles an action event.
	 */
	protected void actionEventAutoPreview(ActionEvent e) {
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview...");
			this.showPreview();
		}
	}

	/**
	 * This method handles an action event.
	 */
	protected void actionEventInvert(ActionEvent e) {
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview with invert option set...");
			this.showPreview();
		}
	}

	/**
	 * This method handles an action event.
	 */
	protected void actionEventVirtual(ActionEvent e) {
		logger.debug("Virtual Checkbox isSelected --> " + chckbxVirtual.isSelected());
		this.fireVirtualFlagChanged(chckbxVirtual.isSelected());
	}

	public JCheckBox getChckbxVirtual() {
		return chckbxVirtual;
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
