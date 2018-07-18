package at.mug.iqm.gui.dialog;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ManagerItemExportDialog.java
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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.gui.util.GUITools;

/**
 * @author Philipp Kainz
 *
 */
public class ManagerItemExportDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2164924733218838803L;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	public static final int CANCEL = -1;
	/**
	 * This is the choice element for exporting n selected items to a new single
	 * tank stack. This selection may be accompanied by a flag, determining
	 * whether or not the images at each position should be exported pairwise.
	 */
	public static final int CHOICE_NEW_STACK = 0;
	/**
	 * This is the choice element for exporting n selected items to n separate
	 * tank items.
	 */
	public static final int CHOICE_SINGLE_ITEMS = 1;
	private int selection = -1;
	private JRadioButton rdbtnExpToSingle;
	private JRadioButton rdbtnExpToStack;
	private JCheckBox chckbxExportImagePairs;
	private JButton btnExport;

	public ManagerItemExportDialog() {
		setIconImage(new ImageIcon(Resources.getImageURL("icon.exportItem"))
				.getImage());
		setResizable(false);
		setModal(true);
		setTitle("Export Settings");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel buttonPanel = new JPanel();
		panel.add(buttonPanel, BorderLayout.SOUTH);

		btnExport = new JButton(I18N.getGUILabelText("application.dialog.manager.export.btnExport.text"));
		btnExport.setEnabled(false);
		btnExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleExport();
			}
		});
		btnExport.setFont(new Font("Tahoma", Font.BOLD, 11));
		buttonPanel.add(btnExport);

		JButton btnCancel = new JButton(I18N.getGUILabelText("application.dialog.manager.export.btnCancel.text"));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selection = CANCEL;
				setVisible(false);
			}
		});
		buttonPanel.add(btnCancel);

		JPanel optionsPanel = new JPanel();
		panel.add(optionsPanel);
		optionsPanel.setLayout(new BorderLayout(0, 0));

		JPanel rbutPanel = new JPanel();
		optionsPanel.add(rbutPanel);
		rbutPanel.setLayout(new BoxLayout(rbutPanel, BoxLayout.Y_AXIS));

		rdbtnExpToStack = new JRadioButton(I18N.getGUILabelText("application.dialog.manager.export.toStack.text"));
		rdbtnExpToStack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnExport.setEnabled(true);
				enablePairwiseExport();
			}
		});
		rdbtnExpToStack
				.setToolTipText(I18N.getGUILabelText("application.dialog.manager.export.toStack.ttp"));
		rbutPanel.add(rdbtnExpToStack);
		buttonGroup.add(rdbtnExpToStack);

		rdbtnExpToSingle = new JRadioButton(I18N.getGUILabelText("application.dialog.manager.export.toSingle.text"));
		rdbtnExpToSingle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnExport.setEnabled(true);
				disablePairwiseExport();
			}
		});
		rdbtnExpToSingle
				.setToolTipText(I18N.getGUILabelText("application.dialog.manager.export.toSingle.ttp"));
		rbutPanel.add(rdbtnExpToSingle);
		buttonGroup.add(rdbtnExpToSingle);

		JPanel chbxPanel = new JPanel();
		optionsPanel.add(chbxPanel, BorderLayout.SOUTH);

		chckbxExportImagePairs = new JCheckBox(I18N.getGUILabelText("application.dialog.manager.export.pairwise.text"));
		chckbxExportImagePairs.setAlignmentX(Component.CENTER_ALIGNMENT);
		chckbxExportImagePairs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setGUIforPairwiseExport();
			}
		});
		chbxPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		chckbxExportImagePairs
				.setToolTipText(I18N.getGUILabelText("application.dialog.manager.export.pairwise.ttp"));
		chbxPanel.add(chckbxExportImagePairs);

		pack();
		setLocationRelativeTo(GUITools.getMainFrame());
		setVisible(true);
	}

	protected void enablePairwiseExport() {
		if (rdbtnExpToStack.isSelected()) {
			chckbxExportImagePairs.setEnabled(true);
		}
	}

	protected void disablePairwiseExport() {
		if (rdbtnExpToSingle.isSelected()) {
			chckbxExportImagePairs.setSelected(false);
			chckbxExportImagePairs.setEnabled(false);
		}
	}

	protected void handleExport() {
		if (rdbtnExpToSingle.isSelected()) {
			selection = CHOICE_SINGLE_ITEMS;
		} else if (rdbtnExpToStack.isSelected()) {
			selection = CHOICE_NEW_STACK;
		}
		setVisible(false);
	}

	private void setGUIforPairwiseExport() {
		if (chckbxExportImagePairs.isSelected()) {
			// only stack export is available
			rdbtnExpToStack.setSelected(true);
			rdbtnExpToSingle.setEnabled(false);
		} else {
			rdbtnExpToSingle.setEnabled(true);
		}
		btnExport.setEnabled(true);
	}

	public int getExportSelection() {
		return selection;
	}

	public boolean isPairwise() {
		return chckbxExportImagePairs.isSelected();
	}

	public JButton getBtnExport() {
		return btnExport;
	}
}
