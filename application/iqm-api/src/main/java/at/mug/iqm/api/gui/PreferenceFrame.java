package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: PreferenceFrame.java
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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import at.mug.iqm.api.I18N;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.config.ConfigManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class PreferenceFrame extends JDialog {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -6483640090973574899L;
	private JPanel contentPane;
	private JTextField txtCleanerName;
	private JTextField txtCleanerInterval;
	private JTextField txtMMName;
	private JTextField txtMMInterval;
	private JButton btnOK;
	private JButton btnCancel;
	private JComboBox<String> cbxLAF;
	private DefaultComboBoxModel<String> cbxLAFModel;
	private JCheckBox chckbxKeepExistingRoi;
	private JButton btnApply;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PreferenceFrame frame = new PreferenceFrame();
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
	public PreferenceFrame() {
		setPreferredSize(new Dimension(600, 400));
		setTitle(I18N.getGUILabelText("prefs.frame.title"));
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel pnlButtons = new JPanel();
		contentPane.add(pnlButtons, BorderLayout.SOUTH);

		btnCancel = new JButton(I18N.getGUILabelText("prefs.btnCancel.text"));
		btnCancel.setToolTipText(I18N.getGUILabelText("prefs.btnCancel.ttp"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		btnApply = new JButton(I18N.getGUILabelText("prefs.btnApply.text"));
		btnApply.setToolTipText(I18N.getGUILabelText("prefs.btnApply.ttp"));
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfiguration();
			}
		});
		pnlButtons.add(btnApply);
		pnlButtons.add(btnCancel);

		btnOK = new JButton(I18N.getGUILabelText("prefs.btnOK.text"));
		btnOK.setToolTipText(I18N.getGUILabelText("prefs.btnOK.ttp"));
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfiguration();
				dispose();
			}
		});
		pnlButtons.add(btnOK);

		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabs, BorderLayout.CENTER);
		cbxLAFModel = new DefaultComboBoxModel<>();

		// get all supported look and feels from the system
		UIManager.LookAndFeelInfo plaf[] = UIManager.getInstalledLookAndFeels();
		cbxLAFModel.addElement("default"); // NOI18N;
		for (int i = 0, n = plaf.length; i < n; i++) {
			cbxLAFModel.addElement(plaf[i].getClassName());
		}

		JPanel pnlGeneral = new JPanel();
		JScrollPane scrollPaneGeneral = new JScrollPane(pnlGeneral);
		tabs.addTab(I18N.getGUILabelText("prefs.tab.general.text"), null,
				scrollPaneGeneral, null);
		pnlGeneral.setLayout(new BorderLayout(0, 0));

		Box verticalBox = Box.createVerticalBox();
		pnlGeneral.add(verticalBox, BorderLayout.NORTH);

		JPanel pnlGenGUI = new JPanel();
		verticalBox.add(pnlGenGUI);
		pnlGenGUI.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED, null, null), "GUI", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		pnlGenGUI
				.setLayout(new FormLayout(new ColumnSpec[] {
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"), }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblLAF = new JLabel("Look-and-Feel:");
		pnlGenGUI.add(lblLAF, "2, 2, right, default");

		cbxLAF = new JComboBox<>();
		cbxLAF.setModel(cbxLAFModel);
		pnlGenGUI.add(cbxLAF, "4, 2, fill, default");

		JPanel pnlCleanerTask = new JPanel();
		pnlCleanerTask.setBorder(new TitledBorder(null, "Cleaner Task",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox.add(pnlCleanerTask);
		pnlCleanerTask
				.setLayout(new FormLayout(new ColumnSpec[] {
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"), }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblCleanerName = new JLabel("Name:");
		pnlCleanerTask.add(lblCleanerName, "2, 2, right, default");

		txtCleanerName = new JTextField();
		pnlCleanerTask.add(txtCleanerName, "4, 2, left, default");
		txtCleanerName.setColumns(20);

		JLabel lblCleanerInterval = new JLabel(
				I18N.getGUILabelText("prefs.lblCleanerInterval.text"));
		pnlCleanerTask.add(lblCleanerInterval, "2, 4, right, default");

		txtCleanerInterval = new JFormattedTextField(
				NumberFormat.getIntegerInstance());
		pnlCleanerTask.add(txtCleanerInterval, "4, 4, left, default");
		txtCleanerInterval.setColumns(10);

		JPanel pnlMemoryMonitor = new JPanel();
		pnlMemoryMonitor.setBorder(new TitledBorder(null, "Memory Monitor",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox.add(pnlMemoryMonitor);
		pnlMemoryMonitor
				.setLayout(new FormLayout(new ColumnSpec[] {
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"), }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblMMName = new JLabel("Name:");
		pnlMemoryMonitor.add(lblMMName, "2, 2, right, default");

		txtMMName = new JTextField();
		pnlMemoryMonitor.add(txtMMName, "4, 2, left, default");
		txtMMName.setColumns(20);

		JLabel lblMMInterval = new JLabel(
				I18N.getGUILabelText("prefs.lblMMInterval.text"));
		pnlMemoryMonitor.add(lblMMInterval, "2, 4, right, default");

		txtMMInterval = new JFormattedTextField(
				NumberFormat.getIntegerInstance());
		pnlMemoryMonitor.add(txtMMInterval, "4, 4, left, default");
		txtMMInterval.setColumns(10);

		JPanel pnlAnnotations = new JPanel();
		pnlAnnotations.setBorder(new EmptyBorder(2, 2, 2, 2));
		tabs.addTab(I18N.getGUILabelText("prefs.tab.annotations.text"), null,
				pnlAnnotations, null);
		pnlAnnotations.setLayout(new BorderLayout(0, 0));

		JPanel pnlAnnGeneral = new JPanel();
		pnlAnnGeneral.setBorder(new TitledBorder(null, I18N
				.getGUILabelText("prefs.annotations.general.title"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlAnnotations.add(pnlAnnGeneral, BorderLayout.NORTH);
		pnlAnnGeneral.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		chckbxKeepExistingRoi = new JCheckBox(
				I18N.getGUILabelText("prefs.annotations.keepExistingROIs.text"));
		pnlAnnGeneral.add(chckbxKeepExistingRoi);
		init();

		// add listeners
		cbxLAF.addItemListener(new ItemListener() {
			private boolean alreadyShownRestartRequired;

			public void itemStateChanged(ItemEvent e) {
				if (!alreadyShownRestartRequired) {
					DialogUtil.getInstance().showDefaultInfoMessage(
							I18N.getMessage("prefs.available.afterrestart"));
					alreadyShownRestartRequired = true;
				}
			}
		});

		pack();
	}

	/**
	 * Initialize the frame.
	 */
	protected void init() {
		loadConfiguration();
	}

	/**
	 * Loads the configuration from the config file using the current instance
	 * of {@link ConfigManager}.
	 */
	protected void loadConfiguration() {
		// fill the components with the info from the XML file
		ConfigManager m = ConfigManager.getCurrentInstance();

		// General TAB
		// get the Look and Feel
		cbxLAFModel.setSelectedItem(m.getIQMConfig().getApplication().getGui()
				.getLookAndFeel());

		txtCleanerName.setText(m.getIQMConfig().getApplication()
				.getCleanerTask().getName());
		txtCleanerInterval.setText(String.valueOf(m.getIQMConfig()
				.getApplication().getCleanerTask().getInterval()));

		txtMMName.setText(m.getIQMConfig().getApplication().getMemoryMonitor()
				.getName());
		txtMMInterval.setText(String.valueOf(m.getIQMConfig().getApplication()
				.getMemoryMonitor().getInterval()));

		// Annotations TAB
		chckbxKeepExistingRoi.setSelected(m.getKeepExistingROIs());

	}

	/**
	 * Saves the configuration using the current instance of
	 * {@link ConfigManager}.
	 */
	protected void saveConfiguration() {
		// tell the config manager to write the file
		ConfigManager m = ConfigManager.getCurrentInstance();

		String laf = String.valueOf(cbxLAF.getSelectedItem());
		m.getIQMConfig().getApplication().getGui().setLookAndFeel(laf);

		m.getIQMConfig().getApplication().getCleanerTask()
				.setName(txtCleanerName.getText());
		m.getIQMConfig().getApplication().getCleanerTask()
				.setInterval(Long.parseLong(txtCleanerInterval.getText()));

		m.getIQMConfig().getApplication().getMemoryMonitor()
				.setName(txtMMName.getText());
		m.getIQMConfig().getApplication().getMemoryMonitor()
				.setInterval(Long.parseLong(txtMMInterval.getText()));

		m.write();
	}
}
