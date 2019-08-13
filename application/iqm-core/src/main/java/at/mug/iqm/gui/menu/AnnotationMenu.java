package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: AnnotationMenu.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.gui.IDrawingLayer;
import at.mug.iqm.api.gui.ROILayerManager;
import at.mug.iqm.api.gui.WaitingDialog;
import at.mug.iqm.api.gui.roi.XMLAnnotationManager;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.CompletionWaiter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.core.I18N;
import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.model.IqmDataBox;

/**
 * This menu hosts the export/import user interface for image annotations.
 * 
 * @author Philipp Kainz
 * @since 3.1
 */
public class AnnotationMenu extends JMenu {

	/**
	 * The UID for serialization
	 */
	private static final long serialVersionUID = 1932917444323071264L;

	// private logger
	private static final Logger logger = Logger.getLogger(AnnotationMenu.class);

	private JMenuItem mntmSave;
	private JMenuItem mntmLoad;

	/**
	 * Constructor.
	 */
	public AnnotationMenu() {
		logger.debug("Generating new instance of 'annotation' menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.annotation.text"));

		this.add(createSaveMenuItem());
		this.add(createLoadMenuItem());
	}

	private JMenuItem createSaveMenuItem() {
		this.mntmSave = new JMenuItem();
		this.mntmSave
				.setText(I18N.getGUILabelText("menu.annotation.save.text"));
		this.mntmSave.setToolTipText(I18N
				.getGUILabelText("menu.annotation.save.ttp"));

		this.mntmSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				writeAnnotations();
			}
		});

		return this.mntmSave;
	}

	private JMenuItem createLoadMenuItem() {
		this.mntmLoad = new JMenuItem();
		this.mntmLoad
				.setText(I18N.getGUILabelText("menu.annotation.load.text"));
		this.mntmLoad.setToolTipText(I18N
				.getGUILabelText("menu.annotation.load.ttp"));

		this.mntmLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loadAnnotations();
			}
		});

		return this.mntmLoad;
	}

	/**
	 * Shows a file dialog and lets the user select the target file.
	 */
	protected void writeAnnotations() {
		// let the user choose the file, but suggest the currently active image name
		// if it is an image
		String suggestedFileName = null; 
		IqmDataBox selectedItem = Application.getTank().getSelectedIqmDataBox();
		if (selectedItem != null && selectedItem.getDataType() == DataType.IMAGE){
			suggestedFileName = (String) selectedItem.getProperty("model_name");
			if (suggestedFileName != null){
				int endIdx = suggestedFileName.lastIndexOf(".");
				if (endIdx >= 0){
					suggestedFileName = (String) suggestedFileName.subSequence(0, endIdx);
				}
				suggestedFileName += ".xml";
			}
		}
		final File xmlFile = CommonTools
				.showFileDialog(JFileChooser.SAVE_DIALOG, suggestedFileName);
		if (xmlFile == null)
			return;

		// kick off a background thread
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				XMLAnnotationManager mgr = new XMLAnnotationManager();
				mgr.setXmlFile(xmlFile);
				mgr.setAllLayers(ROILayerManager.getAllLayers());
				try {
					mgr.write();
					DialogUtil.getInstance().showDefaultInfoMessage(
							I18N.getMessage("annotations.save.success"));
				} catch (JAXBException e) {
					DialogUtil.getInstance().showErrorMessage(
							I18N.getMessage("annotations.save.error"), e, true);
				}
			}
		});
		t.start();
	}

	/**
	 * Loads the annotations from a choosable file in a background thread.
	 * 
	 * @throws JAXBException
	 */
	protected void loadAnnotations() {

		// let the user choose the file
		final File xmlFile = CommonTools
				.showFileDialog(JFileChooser.OPEN_DIALOG);
		if (xmlFile == null)
			return;

		// launch a background thread and re-assemble the layers
		SwingWorker<Void, Void> loader = new SwingWorker<Void, Void>() {

			List<IDrawingLayer> allLayers;

			@Override
			protected Void doInBackground() throws Exception {

				try {
					XMLAnnotationManager mgr = new XMLAnnotationManager();
					mgr.setXmlFile(xmlFile);

					allLayers = mgr.read();
				} catch (JAXBException e) {
					e.printStackTrace();
					allLayers = null;
				}

				return null;
			}

			@Override
			protected void done() {
				// ask the user, whether the loaded layers should be
				// appended, prepended or the old list should be replaced
				if (allLayers == null || allLayers.isEmpty()) {
					DialogUtil.getInstance().showDefaultInfoMessage(
							"This file does not contain any annotations.");
					return;
				}

				int selection = DialogUtil
						.getInstance()
						.showDefaultWarnMessage(
								"Do you want to replace the entire stack of annotation layers?");

				if (selection != IDialogUtil.YES_OPTION) {
					return;
				}

				// replace the layers in the roi manager
				ROILayerManager.replaceLayers(allLayers);
			}
		};

		// wait for the assembling to be finished
		WaitingDialog wd = new WaitingDialog(
				I18N.getMessage("annotations.load.waiting.message"), true);
		CompletionWaiter cw = new CompletionWaiter(wd);
		loader.addPropertyChangeListener(cw);
		loader.execute();
		wd.setVisible(true);
	}

	public static void main(String[] args) {

	}
}
