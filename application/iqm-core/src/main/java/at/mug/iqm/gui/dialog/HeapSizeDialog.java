package at.mug.iqm.gui.dialog;

/*
 * #%L
 * Project: IQM - Application Core
 * File: HeapSizeDialog.java
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


import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;

/**
 * This modal dialog is used for setting the heap memory maximum and initial
 * size (JVM Arguments <code>-Xmx</code> and <code>-Xms</code>).
 * <p>
 * This dialog is just shown on windows systems, see the Edit menu for details.
 * 
 * @author Michael Mayrhofer-Reinhartshuber, Philipp Kainz
 * @since 2011 06
 * 
 */
public class HeapSizeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2027659403527925888L;

	private String userDir; // directory of .exe and .l4j.ini-files
	private String stringXms; // Strings for handling runtime parameters of
								// init/max heap size
	private String stringXmx;
	private String fileNameMem; // directory+filename of .l4j.ini-file
	private JTextField txtInitial, txtMaximum; // textfields of input-dialog

	/**
	 * The constructor.
	 * 
	 */
	public HeapSizeDialog() {

		this.setTitle(I18N.getGUILabelText("heapSize.title"));
		this.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.menu.edit.memory")).getImage());
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(10, 10, 0, 10);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 57, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 40, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		// Contents of the input dialog:
		JLabel captionInitial = new JLabel(
				I18N.getGUILabelText("heapSize.captionInitial.text"));
		GridBagConstraints gbc_captionInitial = new GridBagConstraints();
		gbc_captionInitial.anchor = GridBagConstraints.EAST;
		gbc_captionInitial.insets = new Insets(0, 0, 5, 5);
		gbc_captionInitial.gridx = 0;
		gbc_captionInitial.gridy = 0;
		panel.add(captionInitial, gbc_captionInitial);
		txtInitial = new JTextField(stringXms, 10);
		GridBagConstraints gbc_txtInitial = new GridBagConstraints();
		gbc_txtInitial.anchor = GridBagConstraints.WEST;
		gbc_txtInitial.insets = new Insets(0, 0, 5, 5);
		gbc_txtInitial.gridx = 1;
		gbc_txtInitial.gridy = 0;
		panel.add(txtInitial, gbc_txtInitial);
		JLabel hintInitial = new JLabel(
				I18N.getGUILabelText("heapSize.hintInitial.text"));
		GridBagConstraints gbc_hintInitial = new GridBagConstraints();
		gbc_hintInitial.anchor = GridBagConstraints.WEST;
		gbc_hintInitial.insets = new Insets(0, 0, 5, 0);
		gbc_hintInitial.gridx = 2;
		gbc_hintInitial.gridy = 0;
		panel.add(hintInitial, gbc_hintInitial);

		JLabel captionMaximum = new JLabel(
				I18N.getGUILabelText("heapSize.captionMaximum.text"));
		GridBagConstraints gbc_captionMaximum = new GridBagConstraints();
		gbc_captionMaximum.anchor = GridBagConstraints.EAST;
		gbc_captionMaximum.insets = new Insets(0, 0, 5, 5);
		gbc_captionMaximum.gridx = 0;
		gbc_captionMaximum.gridy = 1;
		panel.add(captionMaximum, gbc_captionMaximum);
		txtMaximum = new JTextField(stringXmx, 10);
		GridBagConstraints gbc_txtMaximum = new GridBagConstraints();
		gbc_txtMaximum.anchor = GridBagConstraints.WEST;
		gbc_txtMaximum.insets = new Insets(0, 0, 5, 5);
		gbc_txtMaximum.gridx = 1;
		gbc_txtMaximum.gridy = 1;
		panel.add(txtMaximum, gbc_txtMaximum);
		JLabel hintMaximum = new JLabel(
				I18N.getGUILabelText("heapSize.hintMaximum.text"));
		GridBagConstraints gbc_hintMaximum = new GridBagConstraints();
		gbc_hintMaximum.anchor = GridBagConstraints.WEST;
		gbc_hintMaximum.insets = new Insets(0, 0, 5, 0);
		gbc_hintMaximum.gridx = 2;
		gbc_hintMaximum.gridy = 1;
		panel.add(hintMaximum, gbc_hintMaximum);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(null);
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.gridwidth = 3;
		gbc_buttonPanel.gridx = 0;
		gbc_buttonPanel.gridy = 2;
		panel.add(buttonPanel, gbc_buttonPanel);

		JButton buttSave = new JButton(
				I18N.getGUILabelText("heapSize.buttOK.text"));
		buttSave.setFont(new Font("Tahoma", Font.BOLD, 11));
		JButton buttCancel = new JButton(
				I18N.getGUILabelText("heapSize.buttCancel.text"));

		buttSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (saveMemoryFile() == true) {
					setVisible(false);
				}
			}
		});
		buttonPanel.setLayout(new GridLayout(0, 2, 0, 0));
		buttonPanel.add(buttSave);
		buttCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonPanel.add(buttCancel);

		txtInitial.requestFocus();

		// get active directory:
		userDir = System.getProperty("user.dir");

		// look for *.l4j.ini-file:
		// default filename, which is just used for testing from development
		// environment:
		fileNameMem = userDir + File.separator + "memory.l4j.ini";

		// get name of executed file - same name with ending ".l4j.ini" contains
		// heap sizes:
		// Attention: The new heap size values for the JVM only works if the
		// executed file is
		// a ".exe"-file which was created with Launch4J (jar - to - exe)!
		// In all other cases the values are just read from / written into a
		// file without any function.
		try {
			String javaClassPath = System.getProperty("java.class.path");
			String[] filesNameTmp = javaClassPath.split(System
					.getProperty("path.separator"));

			String fileNameTmp = filesNameTmp[0].toString();

			// added 2011 08 02
			int length = userDir.length();
			int subStringStart = length;
			if (length == 3) { // number for root directory including slash
				// do nothing, subStringStart is OK
			} else { // number for sub-directories without last slash
				subStringStart = subStringStart + 1;
			}
			fileNameTmp = fileNameTmp.substring(subStringStart,
					fileNameTmp.length());

			if (fileNameTmp.contains(".jar") || fileNameTmp.contains(".exe")) {
				fileNameMem = fileNameTmp
						.substring(0, fileNameTmp.length() - 4) + ".l4j.ini";
			}
		} catch (Exception ignored) {
		}

		File iqmMemoryFile = new File(fileNameMem);

		stringXms = "";
		stringXmx = "";

		// if file exists read current values:
		if (iqmMemoryFile.exists()) {

			try {
				// read current content of file:
				FileReader fr = new FileReader(fileNameMem);
				BufferedReader br = new BufferedReader(fr); 

				// "# Optional Runtime parameters":
				br.readLine();
				// "-Xms4096m":
				stringXms = br.readLine();
				// "-Xmx8192m":
				stringXmx = br.readLine();

				// extract memory values from optional runtime parameters:
				if (!(stringXms == null) && stringXms.indexOf("-Xms") != -1) {
					stringXms = stringXms.substring(4);
				} else {
					stringXms = "";
				}
				if (!(stringXmx == null) && stringXmx.indexOf("-Xmx") != -1) {
					stringXmx = stringXmx.substring(4);
				} else {
					stringXmx = "";
				}

				br.close();

			} catch (IOException ignored) {
				stringXms = "";
				stringXmx = "";
			}

		} else {
			stringXms = "";
			stringXmx = "";
		}

		this.pack();

		CommonTools.centerFrameOnScreen(this);

	}

	/**
	 * Save the memory file to an ini-file located in the execution directory
	 * right next to the executed jar/exe.
	 * 
	 * @return <code>true</code>, if successful, or <code>false</code>, if not
	 *         successful
	 */
	public boolean saveMemoryFile() {

		try {
			String initialHeap = txtInitial.getText();
			String maximumHeap = txtMaximum.getText();
			
			// check if input values are in correct format (e.g.: "9999m",
			// "1111m"):
			int xms = Integer.parseInt(initialHeap.substring(0, initialHeap.length() - 1));
			int xmx = Integer.parseInt(maximumHeap.substring(0, maximumHeap.length() - 1));
			
			// read entire memory of the system
			
			if (xms > xmx) {
				DialogUtil.getInstance().showDefaultErrorMessage(I18N.getMessage("application.heapSize.illegalArguments"));
				return false;
			}

			try {

				// write to file:
				FileWriter fw = new FileWriter(fileNameMem);
				BufferedWriter bw = new BufferedWriter(fw);

				bw.write("# Optional Runtime parameters");
				bw.newLine();
				bw.write("-Xms" + initialHeap);
				bw.newLine();
				bw.write("-Xmx" + maximumHeap);

				bw.close();

			} catch (IOException ignored) {
				return (false);
			}

		} catch (Exception e) {
			DialogUtil
					.getInstance()
					.showDefaultErrorMessage(
							I18N
									.getMessage("application.heapSize.incorrectInputFormat"));
			return (false);
		}

		DialogUtil.getInstance().showDefaultInfoMessage(
				I18N.getMessage("application.heapSize.saved",
						fileNameMem));
		return (true);
	}
}// END
