package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: AbstractTableSavingDialog.java
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.config.ConfigManager;

/**
 * An abstract implementation of the {@link ISaveTableDialog}. This class may be
 * subclassed by any dialog required for storing tables to the file system.
 * 
 * @author Philipp Kainz
 * @since 3.2
 * @update 2018-08 HA added wav extension
 * 
 */
public abstract class AbstractTableSavingDialog extends JFileChooser implements
		ISaveTableDialog, PropertyChangeListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 3879770302565940024L;
	private static final Logger logger = Logger
			.getLogger(AbstractTableSavingDialog.class);

	protected File currImgDir;
	protected boolean extensionExists = false;
	protected JPanel additionalOptionsPanel;
	protected JCheckBox chbxStoreModel;
	protected FileNameExtensionFilter allFormats;
	protected String extension;
	protected ImageIcon frameIcon;
	
	public AbstractTableSavingDialog() {
		super();
		this.setDialogType(JFileChooser.SAVE_DIALOG);
	}

	public AbstractTableSavingDialog(File file) {
		super(file);
		this.setDialogType(JFileChooser.SAVE_DIALOG);
	}

	@Override
	public File showDialog(){
		JFrame frame = new JFrame();
		frame.setIconImage(this.frameIcon.getImage());

		int returnVal = this.showSaveDialog(frame);

		// react to the user's choice
		if (returnVal != APPROVE_OPTION) {
			logger.info("No table(s) saved.");
			return null;
		}

		File destination = this.getSelectedFile();

		// Save property to XML file
		// update configuration
		ConfigManager.getCurrentInstance().setImagePath(this.getCurrentDirectory());

		return resolveDestination(destination);
	}

	/**
	 * Resolves the file extension and determines the file encoding. Encoding
	 * string for the JAI library is obtained using {@link #getEncoding()}.
	 * 
	 * @param destination
	 *            the file selected using the dialog
	 * @return the resolved file name
	 */
	protected File resolveDestination(File destination) {
		
		// initialize the variables
		extensionExists = false;
		extension = "default";  //default
//		File testFile;
//		boolean testFileExists = false;

		// look for existing extension
		// and compare it to all known and allowed extensions.
		try {
			extension = destination.toString().substring(destination.toString().lastIndexOf("."),destination.toString().length());
			extension = extension.substring(1);
			for (String s : allFormats.getExtensions()) {
				if (!s.equals(extension)) {
					extensionExists = false;
				} else {
					extensionExists = true;
					break;
				}
			}
		} catch (NullPointerException npe) {
			extensionExists = false;
			logger.debug(npe);
		} catch (StringIndexOutOfBoundsException se) {
			extensionExists = false;
			logger.debug(se);
		}

		
	
		if (!extensionExists) {
			// check, whether a special filter is selected and take its extension		
			if        (this.getFileFilter().getDescription().equals(IQMConstants.JTB_FILTER_DESCRIPTION)) {
				extension = IQMConstants.JTB_EXTENSION;
			} else if (this.getFileFilter().getDescription().equals(IQMConstants.CSV_FILTER_DESCRIPTION)) {
				extension = IQMConstants.CSV_EXTENSION;
			} else if (this.getFileFilter().getDescription().equals(IQMConstants.TXT_FILTER_DESCRIPTION)) {
				extension = IQMConstants.TXT_EXTENSION;
			} else if (this.getFileFilter().getDescription().equals(IQMConstants.DAT_FILTER_DESCRIPTION)) {
				extension = IQMConstants.DAT_EXTENSION;
			} else if (this.getFileFilter().getDescription().equals(IQMConstants.WAV_FILTER_DESCRIPTION)) {
				extension = IQMConstants.WAV_EXTENSION;
			} else if (this.getFileFilter().getDescription().equals(IQMConstants.ECG_FILTER_DESCRIPTION)) {
				extension = IQMConstants.ECG_EXTENSION;
			} else {
				extension = "txt";
			}
			//add extension to destination
			destination = new File(destination.toString() + "." + extension);
		
		}
		
		// check for already existing file
		if (destination.exists()) {
			Toolkit.getDefaultToolkit().beep();

			int selected = DialogUtil.getInstance().showDefaultWarnMessage(I18N.getMessage("application.fileExists.overwrite"));
			if (selected != IDialogUtil.YES_OPTION) {
				BoardPanel.appendTextln(I18N.getMessage("application.tableNotSaved"));
				return null;
			} else {
				//do nothing here and overwrite in the following
			}
		}
		return destination;
	}
	
	/**
	 * Gets the extension of the file.
	 */
	@Override
	public String getExtension() {
		return extension;
	}

	/**
	 * This method initializes the table saving dialog. It constructs the
	 * supported file extensions and filters as well as the panel with the
	 * additional options (e.g. model or table export).
	 */
	protected void init() {
		this.setMultiSelectionEnabled(false);
		
		allFormats = new FileNameExtensionFilter(
				I18N.getGUILabelText("application.dialog.fileExtensionFilter.tableFiles.supported"),
				IQMConstants.JTB_EXTENSION, IQMConstants.DAT_EXTENSION,
				IQMConstants.CSV_EXTENSION, IQMConstants.TXT_EXTENSION);
		this.addChoosableFileFilter(allFormats);
		this.addChoosableFileFilter(new FileNameExtensionFilter(IQMConstants.JTB_FILTER_DESCRIPTION, IQMConstants.JTB_EXTENSION));
		this.addChoosableFileFilter(new FileNameExtensionFilter(IQMConstants.DAT_FILTER_DESCRIPTION, IQMConstants.DAT_EXTENSION));
		this.addChoosableFileFilter(new FileNameExtensionFilter(IQMConstants.CSV_FILTER_DESCRIPTION, IQMConstants.CSV_EXTENSION));
		this.addChoosableFileFilter(new FileNameExtensionFilter(IQMConstants.TXT_FILTER_DESCRIPTION, IQMConstants.TXT_EXTENSION));
		//this.addChoosableFileFilter(new FileNameExtensionFilter(IQMConstants.WAV_FILTER_DESCRIPTION, IQMConstants.WAV_EXTENSION));
		this.setFileFilter(allFormats); // default setting

		try {
			currImgDir = ConfigManager.getCurrentInstance().getImagePath();
		} catch (NullPointerException npe) {
			currImgDir = new File(System.getProperty("user.home"));
		}
		
		this.setCurrentDirectory(currImgDir);
		this.setPreferredSize(new Dimension(700, 500));
		this.setAccessory(getAdditionalOptionsPanel());
		this.setAcceptAllFileFilterUsed(false);
	}

	/**
	 * Initializes the accessory panel.
	 * 
	 * @return a panel with custom saving options
	 */
	public JPanel getAdditionalOptionsPanel() {
		if (additionalOptionsPanel == null) {
			additionalOptionsPanel = new JPanel(new BorderLayout());

			BoxLayout boxlayout = new BoxLayout(additionalOptionsPanel, BoxLayout.LINE_AXIS);
			additionalOptionsPanel.setLayout(boxlayout);

			chbxStoreModel = new JCheckBox("Export model");
			chbxStoreModel.setToolTipText("<html>Export the model using the default sort order of the columns, the table's model will be exported only.<br/>"
							+ "Uncheck this box, if you want to store a custom order of the "
							+ "table's columns as shown in the TablePanel, the entire JTable object will be exported.</html>");
			chbxStoreModel.setSelected(false);

			additionalOptionsPanel.add(Box.createHorizontalGlue());
			additionalOptionsPanel.add(chbxStoreModel);
		}
		return additionalOptionsPanel;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		logger.debug(evt.getSource() + ", " + evt.getPropertyName() + ", " + evt.getNewValue());
		if (evt.getPropertyName().equals(
				JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
			if (((FileNameExtensionFilter) evt.getNewValue()).getDescription().equals(IQMConstants.JTB_FILTER_DESCRIPTION)) {
				chbxStoreModel.setEnabled(false);
			} else {
				chbxStoreModel.setEnabled(true);
			}
			if (((FileNameExtensionFilter) evt.getNewValue()).getDescription().equals(IQMConstants.WAV_FILTER_DESCRIPTION)) {
				chbxStoreModel.setEnabled(false);
				DialogUtil.getInstance().showDefaultInfoMessage("<html><font color=\"red\">WAV format leads to rounding errors due to conversion to signed long!<br/>"
																						+ "Data values are normalized to the range [-1,1] </font></html>");
			} else {
				chbxStoreModel.setEnabled(true);
			}
						
		}
	}

	@Override
	public boolean exportModel() {
		return chbxStoreModel.isSelected();
	}
	
	public void setFrameIcon(ImageIcon frameIcon) {
		this.frameIcon = frameIcon;
	}
	
	
}
