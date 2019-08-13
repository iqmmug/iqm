package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: OpenTableDialog.java
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


import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.exception.EmptyFileException;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.table.TableTools;
import at.mug.iqm.config.ConfigManager;

/**
 * This class is responsible for opening a dialog, where the user is able to
 * select a file for being displayed in a {@link JTable}.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2009 06
 */
public class OpenTableDialog extends Thread{

	private File currImgDir;

	// Logging variables
	private static Class<?> caller = OpenTableDialog.class;
	private static final Logger logger = Logger
			.getLogger(OpenTableDialog.class);

	private FileInputStream fis;
	private ObjectInputStream ois;

	/**
	 * Standard Constructor.
	 */
	public OpenTableDialog() {
	}

	/**
	 * Runs the dialog.
	 */
	public void run() {
		try {

			JFileChooser fc = new JFileChooser();

			fc.setMultiSelectionEnabled(false);
			fc.setDialogTitle(I18N
					.getGUILabelText("application.dialog.openTable.title"));
			FileNameExtensionFilter filt = new FileNameExtensionFilter(
					I18N.getGUILabelText("application.dialog.fileExtensionFilter.tableFiles.supported"),
					IQMConstants.JTB_EXTENSION);
			fc.addChoosableFileFilter(filt);

			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					IQMConstants.JTB_FILTER_DESCRIPTION,
					IQMConstants.JTB_EXTENSION));
			fc.setFileFilter(filt); // default setting

			currImgDir = ConfigManager.getCurrentInstance().getImagePath();
			fc.setCurrentDirectory(currImgDir);
			fc.setPreferredSize(new Dimension(700, 500));

			JFrame frame = new JFrame();
			frame.setIconImage(new ImageIcon(Resources
					.getImageURL("icon.menu.file.openTable")).getImage());

			int returnVal = fc.showOpenDialog(frame);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				BoardPanel.appendTextln(
						I18N.getMessage("application.noTableOpened"), caller);
			} else {
				final File file = fc.getSelectedFile();

				BoardPanel.appendTextln(I18N.getMessage(
						"application.openingTable", file.getName().toString()),
						caller);

				// set default directory
				currImgDir = fc.getCurrentDirectory();

				// update configuration
				ConfigManager.getCurrentInstance().setImagePath(currImgDir);

				new SwingWorker<Boolean, Void>() {
					// the table can be serialized as JTable or as TableModel
					JTable table = null;
					TableModel tableModel = null;

					@Override
					protected Boolean doInBackground() throws Exception {
						try {
							fis = new FileInputStream(file.toString());
							ois = new ObjectInputStream(fis);

							Object o = ois.readObject();

							// it is possible that the table model cannot be
							// casted
							// e.g. if the user selects to store the sorted
							// table from the
							// table panel
							if (o instanceof String) {
								// convert CSV to
								// at.mug.iqm.api.model.TableModel
								tableModel = (TableModel) TableTools.convertToTableModel((String) o);
								tableModel.setModelName(file.getName());
							}

							else if (o instanceof TableModel) {
								tableModel = (TableModel) o;
							} else if (o instanceof JTable) {
								table = (JTable) o;
							}
						} catch (EmptyFileException e) {
							logger.error("An error occurred: ", e);
							DialogUtil
									.getInstance()
									.showErrorMessage(
											I18N.getMessage("application.error.generic"),
											e);
						} catch (FileNotFoundException e) {
							logger.error("An error occurred: ", e);
							DialogUtil
									.getInstance()
									.showErrorMessage(
											I18N.getMessage("application.error.generic"),
											e);
						} catch (IOException e) {
							logger.error("An error occurred: ", e);
							DialogUtil
									.getInstance()
									.showErrorMessage(
											I18N.getMessage("application.error.generic"),
											e);
						} finally {
							if (fis != null) {
								try {
									fis.close();
								} catch (IOException e) {
									// do nothing
								}
							}
						}
						return null;
					}

					@Override
					protected void done() {
						if (this.table != null) {
							this.tableModel = TableTools
									.convertToTableModel(this.table.getModel());

							Vector<IqmDataBox> vec = new Vector<IqmDataBox>(1);
							vec.add(new IqmDataBox(tableModel));
							Application.getTank().addNewItems(vec);
						}
					}
				}.execute();

			}
		}

		catch (Exception e) {
			logger.error("An error occurred: ", e);
			DialogUtil.getInstance().showErrorMessage(
					I18N.getMessage("application.error.generic"), e);
		}
	}
}
