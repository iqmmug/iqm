package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: FileSelectionDialog.java
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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to select a file from a list.
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings("rawtypes")
public class FileSelectionDialog extends JDialog {

	// private logger
	private static final Logger logger = LogManager.getLogger(FileSelectionDialog.class);

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7995586826789946566L;
	private JPanel contentPane;

	private JList fileNameList;
	private DefaultListModel listModel;

	private TankPanelDropEventHandler eventHandler;

	private File selectedItem;

	private static final File[] listData = new File(
			System.getProperty("user.home")).listFiles();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileSelectionDialog frame = new FileSelectionDialog();
					frame.setFileList(listData);
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
	@SuppressWarnings("unchecked")
	public FileSelectionDialog() {
		setModal(true);
		setAlwaysOnTop(true);
		setIconImage(new ImageIcon(Resources.getImageURL("icon.plot.generic16"))
				.getImage());
		setTitle(I18N.getGUILabelText("application.dialog.fileselector.title"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(580, 300));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel centerPanel = new JPanel();
		contentPane.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));

		listModel = new DefaultListModel();

		fileNameList = new JList(listModel);
		fileNameList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// when selection is finished
				if (e.getValueIsAdjusting() == false) {
					selectedItem = (File) fileNameList.getSelectedValue();
					logger.debug(selectedItem);
				}
			}
		});
		fileNameList.setToolTipText("");
		fileNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		centerPanel.add(new JScrollPane(fileNameList));

		JPanel buttonPanel = new JPanel();
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		JPanel grpPanel = new JPanel();
		buttonPanel.add(grpPanel);
		grpPanel.setLayout(new GridLayout(0, 2, 10, 0));

		JButton btnCancel = new JButton();
		btnCancel
				.setText(I18N
						.getGUILabelText("application.dialog.fileselector.btnCancel.text"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					eventHandler.setSelectedFile(null);
				} catch (NullPointerException ex) {
					logger.error(ex);
				}
				dispose();
			}
		});
		grpPanel.add(btnCancel);

		JButton btnLoad = new JButton();
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					eventHandler.setSelectedFile(getSelectedItem());
				} catch (NullPointerException ex) {
					logger.error(ex);
				}
				dispose();
			}
		});
		btnLoad.setText(I18N
				.getGUILabelText("application.dialog.fileselector.btnLoad.text"));
		grpPanel.add(btnLoad);

		this.pack();
	}

	@SuppressWarnings("unchecked")
	public void setFileList(List fileList) {
		this.listModel = new DefaultListModel();
		for (int i = 0; i<fileList.size(); i++) {
			this.listModel.addElement((File) fileList.get(i));
		}
		updateListModel();
	}

	public void setFileList(File[] fileList) {
		this.setFileList(Arrays.asList(fileList));
	}

	@SuppressWarnings("unchecked")
	protected void updateListModel() {
		fileNameList.setModel(listModel);
	}

	public File getSelectedItem() {
		return selectedItem;
	}
	
	public void setEventHandler(TankPanelDropEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
}
