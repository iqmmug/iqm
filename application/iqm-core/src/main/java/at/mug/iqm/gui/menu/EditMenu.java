package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: EditMenu.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.process.ImageProcessor;

import java.awt.Event;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.gui.PreferenceFrame;
import at.mug.iqm.api.gui.util.IqmSlideShow;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.ImageSelection;
import at.mug.iqm.commons.util.OperatingSystem;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.workflow.ExecutionProxy;
import at.mug.iqm.core.workflow.Look;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.dialog.HeapSizeDialog;
import at.mug.iqm.gui.util.GUITools;
import at.mug.iqm.img.bundle.descriptors.IqmOpCreateImageDescriptor;

/**
 * This is the base class for the edit menu in IQM.
 * 
 * @author Michael Mayrhofer-Reinhartshuber, Philipp Kainz
 * @since 16.03.2012
 */
public class EditMenu extends DeactivatableMenu implements ActionListener {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -6389948467867582328L;

	// class specific logger
	private static Class<?> caller = EditMenu.class;
	private static final Logger logger = Logger.getLogger(EditMenu.class);

	// class variable declaration
	private JMenuItem copyToClipMenuItem;
	private JMenuItem heapSizeMenuItem; // MMR - 10.06.2011
	private JMenuItem garbCollMenuItem;
	private JMenuItem extraLookMenuItem;
	private JMenuItem slideShowMenuItem;
	private OperatorMenuItem createImageMenuItem;
	private JMenuItem runImageJMenuItem;
	private JMenuItem getImageJImageMenuItem;
	private JMenuItem preferencesMenuItem;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped EditMenu.
	 */
	public EditMenu() {
		logger.debug("Generating new instance of 'edit' menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.edit.text"));

		// initialize the variables
		this.copyToClipMenuItem = new JMenuItem();
		this.heapSizeMenuItem = new JMenuItem(); // MMR - 10.06.2011
		this.garbCollMenuItem = new JMenuItem();
		this.extraLookMenuItem = new JMenuItem();
		this.slideShowMenuItem = new JMenuItem();
		this.createImageMenuItem = new OperatorMenuItem(
				OperatorType.IMAGE_GENERATOR);
		this.runImageJMenuItem = new JMenuItem();
		this.getImageJImageMenuItem = new JMenuItem();
		
		this.preferencesMenuItem = new JMenuItem();

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Menu 'edit' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items in 'edit' menu.");

		// assemble: add created elements to the JMenu
		this.add(this.createCopyToClipMenuItem());
		if (OperatingSystem.isWindows()) {
			this.add(this.createHeapSizeMenuItem());
		}
		this.add(this.createGarbCollMenuItem());
		this.add(this.createExtraLookMenuItem());
		this.add(this.createSlideShowMenuItem());
		this.addSeparator();
		this.add(this.createCreateImageMenuItem());
		this.addSeparator();
		this.add(new ProcessingHistoryMenu());
		this.addSeparator();
		this.add(new AnnotationMenu());
		this.addSeparator();
		this.add(this.createRunImageJMenuItem());
		this.add(this.createGetImageJImageMenuItem());
		this.addSeparator();
		this.add(this.createPreferencesMenuItem());
	}
	
	/**
	 * This method initializes the preferences menu item
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createPreferencesMenuItem() {
		this.preferencesMenuItem.setText(I18N
				.getGUILabelText("menu.edit.preferences.text"));
		this.preferencesMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.edit.preferences.ttp"));
		this.preferencesMenuItem.addActionListener(this);
		this.preferencesMenuItem.setActionCommand("prefs");
		this.preferencesMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		return this.preferencesMenuItem;
	}
	
	/**
	 * This method initializes CopyToClipMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createCopyToClipMenuItem() {
		this.copyToClipMenuItem.setText(I18N
				.getGUILabelText("menu.edit.copy.text"));
		this.copyToClipMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.edit.copy.ttp"));
		this.copyToClipMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.edit.copy")));
		this.copyToClipMenuItem.addActionListener(this);
		this.copyToClipMenuItem.setActionCommand("copytoclip");
		this.copyToClipMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		return this.copyToClipMenuItem;
	}

	// MMR - 10.06.2011
	/**
	 * This method initializes heapSizeMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createHeapSizeMenuItem() {
		this.heapSizeMenuItem.setText(I18N
				.getGUILabelText("menu.edit.memory.text"));
		this.heapSizeMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.edit.memory.ttp"));
		this.heapSizeMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.edit.memory")));
		this.heapSizeMenuItem.addActionListener(this);
		this.heapSizeMenuItem.setActionCommand("heapsize");
		return this.heapSizeMenuItem;
	}

	/**
	 * This method initializes GarbCollMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createGarbCollMenuItem() {
		this.garbCollMenuItem.setText(I18N
				.getGUILabelText("menu.edit.garbColl.text"));
		this.garbCollMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.edit.garbColl.ttp"));
		this.garbCollMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.edit.garbColl")));
		this.garbCollMenuItem.addActionListener(this);
		this.garbCollMenuItem.setActionCommand("garbcoll");
		this.garbCollMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask() + Event.ALT_MASK));
		return this.garbCollMenuItem;
	}

	/**
	 * This method initializes extraLookMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createExtraLookMenuItem() {
		this.extraLookMenuItem.setText(I18N
				.getGUILabelText("menu.edit.extraLook.text"));
		this.extraLookMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.edit.extraLook.ttp"));
		this.extraLookMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.edit.extraLook")));
		this.extraLookMenuItem.addActionListener(this);
		this.extraLookMenuItem.setActionCommand("extralook");
		this.extraLookMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_L, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask() + Event.ALT_MASK));
		return this.extraLookMenuItem;
	}

	/**
	 * This method initializes SlideShowMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSlideShowMenuItem() {
		this.slideShowMenuItem.setText(I18N
				.getGUILabelText("menu.edit.slideShow.text"));
		this.slideShowMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.edit.slideShow.ttp"));
		this.slideShowMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.edit.slideShow")));
		this.slideShowMenuItem.addActionListener(this);
		this.slideShowMenuItem.setActionCommand("slideshow");
		return this.slideShowMenuItem;
	}

	/**
	 * This method initializes createImageMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createCreateImageMenuItem() {
		this.createImageMenuItem.setText(I18N
				.getGUILabelText("menu.edit.createImage.text"));
		this.createImageMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.edit.createImage.ttp"));
		this.createImageMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.edit.createImage")));
		this.createImageMenuItem.addActionListener(this);
		this.createImageMenuItem.setActionCommand("createimage");
		return this.createImageMenuItem;
	}

	/**
	 * This method initializes runImageJMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createRunImageJMenuItem() {
		this.runImageJMenuItem.setText(I18N
				.getGUILabelText("menu.edit.runImageJ.text"));
		this.runImageJMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.edit.runImageJ.ttp"));
		this.runImageJMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.edit.imagej")));
		this.runImageJMenuItem.addActionListener(this);
		this.runImageJMenuItem.setActionCommand("runimagej");
		return this.runImageJMenuItem;
	}

	/**
	 * This method initializes getImageJImageMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createGetImageJImageMenuItem() {
		this.getImageJImageMenuItem.setText(I18N
				.getGUILabelText("menu.edit.getIJImage.text"));
		this.getImageJImageMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.edit.getIJImage.ttp"));
		this.getImageJImageMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.edit.imagej")));
		this.getImageJImageMenuItem.addActionListener(this);
		this.getImageJImageMenuItem.setActionCommand("getimagejimage");
		return this.getImageJImageMenuItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	/**
	 * This method sets and performs the corresponding actions to the menu
	 * items.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		logger.debug(e.getActionCommand());

		if ("copytoclip".equals(e.getActionCommand())) {
			Object ob = Look.getInstance().getCurrentImage();
			if (!(ob instanceof PlanarImage)) {
				DialogUtil.getInstance().showDefaultInfoMessage(
						I18N.getMessage("application.missingImage"));
				BoardPanel.appendTextln(
						I18N.getMessage("application.missingImage"), caller);
			} else if (ob instanceof PlanarImage) {
				try {
					BufferedImage bi = ((PlanarImage) ob).getAsBufferedImage();
					// Image image =
					// Toolkit.getDefaultToolkit().createImage(bi.getSource());
					ImageSelection imageSelection = new ImageSelection(bi);
					Toolkit.getDefaultToolkit().getSystemClipboard()
							.setContents(imageSelection, null);
					DialogUtil
							.getInstance()
							.showDefaultInfoMessage(
									I18N.getMessage("application.dialog.image.copy.success"));
				} catch (Exception ex) {
					DialogUtil
							.getInstance()
							.showDefaultErrorMessage(
									I18N.getMessage("application.dialog.image.copy.error"));
				}
			}

		}
		// MMR - 10.06.2011
		// PK - 2012 04 19
		if ("heapsize".equals(e.getActionCommand())) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new HeapSizeDialog().setVisible(true);
				}
			});

		}
		if ("garbcoll".equals(e.getActionCommand())) {
			Runtime r = Runtime.getRuntime();
			long freeMemBefore = r.freeMemory();

			BoardPanel.appendTextln(I18N.getMessage(
					"application.garbageCollector.before",
					(freeMemBefore / 1024.0F / 1024.0F)), caller);
			System.gc();
			r.gc();
			long freeMemAfter = r.freeMemory();
			BoardPanel.appendTextln(I18N.getMessage(
					"application.garbageCollector.after",
					(freeMemAfter / 1024.0F / 1024.0F)), caller);

			long diff = (long) ((freeMemAfter - freeMemBefore) / 1024.0F / 1024.0F);
			if (diff > 0) {
				DialogUtil.getInstance().showDefaultInfoMessage(
						I18N.getMessage("application.garbageCollector.diff",
								String.valueOf(diff)));
			} else {
				DialogUtil.getInstance().showDefaultInfoMessage(
						I18N.getMessage("application.garbageCollector.fail"));
			}

		}
		if ("extralook".equals(e.getActionCommand())) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					Look.getInstance().createExtraLookFrame();
				}
			});

		}
		if ("slideshow".equals(e.getActionCommand())) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new IqmSlideShow().showIqmSlideShow();
				}
			});

		}
		if ("createimage".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new IqmOpCreateImageDescriptor());
		}

		if ("runimagej".equals(e.getActionCommand())) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					ImageJ imageJ = IJ.getInstance();
					if (imageJ != null) {
						try {
							DialogUtil
									.getInstance()
									.showDefaultInfoMessage(
											I18N.getMessage("application.dialog.imagej.isRunning"));
							imageJ.setVisible(true);
						} catch (Exception ex) {
							imageJ = new ImageJ();
							logger.error(ex);
							DialogUtil.getInstance().showErrorMessage(null, ex);
						}
					} else {
						// print additional info
						IJ.debugMode = false;
						imageJ = new ImageJ();
					}

					int idx = Application.getManager().getCurrItemIndex();
					if (idx >= 0) {
						IqmDataBox iqmDataBox = Tank.getInstance()
								.getCurrentTankIqmDataBoxAt(idx);
						if (iqmDataBox.getDataType() == DataType.IMAGE) {

							PlanarImage pi = iqmDataBox.getImage();
							if (pi == null) {
								DialogUtil
										.getInstance()
										.showDefaultInfoMessage(
												I18N.getMessage("appliation.missingImage"));
								BoardPanel.appendTextln(I18N
										.getMessage("appliation.missingImage"),
										caller);
								return;
							}

							Image image = pi.getAsBufferedImage();
							ImagePlus imp = new ImagePlus("", image);
							imp.show();
							imp.updateImage();
						}
					}
				}
			});

		}
		if ("getimagejimage".equals(e.getActionCommand())) {
			// check whether or not imagej is launched
			ImageJ imageJ = IJ.getInstance();

			if (imageJ != null) {
				ImagePlus imp = WindowManager.getCurrentImage();
				if (imp == null)
					return;
				ImageStack stack = imp.getStack();
				
				int nSlices = stack.getSize();
				Vector<IqmDataBox> resultVector = new Vector<IqmDataBox>(nSlices);
				
				for (int i=1; i<=nSlices; i++){
					ImageProcessor ip = stack.getProcessor(i);
					BufferedImage img = ip.getBufferedImage();
					
					//TODO: Convert img from DirectColorModel to RGB/...
					
					PlanarImage pi = PlanarImage.wrapRenderedImage(img);
					pi.setProperty("image_name", imp.getTitle());
					pi.setProperty("file_name", "");
					
					resultVector.add(new IqmDataBox(pi));
				}
				
				Tank.getInstance().addNewItems(resultVector);
			} else {
				int selection = DialogUtil
						.getInstance()
						.showDefaultQuestionMessage(
								I18N.getMessage("application.dialog.imagejimage.isNotRunning"));

				if (selection == IDialogUtil.YES_OPTION) {
					this.actionPerformed(new ActionEvent(this, -23948,
							"runimagej"));
				}
			}
		}
		if ("prefs".equals(e.getActionCommand())) {
			// run the preferences dialog
			PreferenceFrame pf = new PreferenceFrame();
			pf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			pf.setLocationRelativeTo(GUITools.getMainFrame());
			pf.setModal(true);
			pf.setVisible(true);
		}
	}
}
