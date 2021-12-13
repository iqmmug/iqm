package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: QRSPeaksExtractorFrame.java
 * 
 * $Id: QRSPeaksExtractorFrame.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-api/src/main/java/at/mug/iqm/commons/gui/QRSPeaksExtractorFrame.java $
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2021 Helmut Ahammer, Philipp Kainz
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import at.mug.iqm.api.Application;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.exception.UnreadableECGFileException;
import at.mug.iqm.api.gui.WaitingDialog;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.CompletionWaiter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.plot.QRSPeaksExtractor;

/**
 * This GUI class provides a front end for the QRS peaks extraction out of an ECG file.
 * 
 * @author Helmut Ahammer
 * @since  2018 11
 * 
 */
public class QRSPeaksExtractorFrame extends JFrame implements ActionListener,
		ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8821328371974838675L;

	// Logging variables
	private static final Logger logger = LogManager.getLogger(QRSPeaksExtractorFrame.class);

	private int offSet       = 0;   // starting offset
	private int sampleRate   = 180; // sampel rate in Hz  180 Herbert Syncope, 125 Helena
	private int oseaMethod   = 1;   // 0...QRSDetect, 1..QRSDetsct2, 2...BeatDetectionAndClassify
	private int outputOption = 1;   // 0...xy coordinates,  1...intervals 
	
	private JSpinner jSpinnerOffSet;
	private JSpinner jSpinnerSampleRate;
	
	private JRadioButton buttQRSDetect       = null; //
	private JRadioButton buttQRSDetect2      = null; //
	private JRadioButton buttBeatDetectClass = null; //
	private JToolBar jToolBarOseaMethod      = null;
	private ButtonGroup buttGroupOseaMethod  = null;

	private JRadioButton buttOutputCoordinates = null; // output image
	private JRadioButton buttOutputIntervals   = null; //
	private JToolBar jToolBarOutputOptions     = null;
	private ButtonGroup buttGroupOutputOptions = null;

	private JButton buttOK = null;
	private JButton buttCancel = null;

	private JPanel content = new JPanel();

	private File[] files = null;

	private QRSPeaksExtractor qrsPeaksExtractor = new QRSPeaksExtractor();

	private JTable infoTable = null;
	private DefaultTableModel tableModel = null;
	private Object[][] tableData = null;

	/**
	 * Create a new instance and pass the selected files.
	 * 
	 * @param files
	 *            - a java.io.File[]
	 */
	public QRSPeaksExtractorFrame(File[] files) {
		this.setFiles(files);
		this.createAndAssemble();
	}

	/**
	 * Creates objects and assembles the panels.
	 */
	private void createAndAssemble() {

		// get table data of the first image and determine maxOffSet
		boolean loadSuccess = this.getNewTableData(this.files[0]);
		// this.getNewTableData(testFiles[0]);
		if (!loadSuccess) {
			return;
		}

		this.content.setLayout(new BorderLayout(0, 5));

		this.content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.setTitle("Choose Values");
		this.setIconImage(new ImageIcon(Resources.getImageURL("icon.menu.file.extractSVS")).getImage());

		JPanel spinnerPanel           = new JPanel(new GridLayout(2, 0, 2, 2));
		JPanel spinnerOffSetPanel     = new JPanel(new BorderLayout(5, 5));
		JPanel spinnerSampleRatePanel = new JPanel(new BorderLayout(5, 5));

		spinnerOffSetPanel.add(new JLabel("Starting offset:"), BorderLayout.NORTH);
		spinnerOffSetPanel.add(createJSpinnerOffSet(), BorderLayout.CENTER);
		spinnerSampleRatePanel.add(new JLabel("Sample rate [Hz]:"), BorderLayout.NORTH);
		spinnerSampleRatePanel.add(createJSpinnerSampleRate(), BorderLayout.CENTER);
		spinnerPanel.add(spinnerOffSetPanel);
		spinnerPanel.add(spinnerSampleRatePanel);
		this.content.add(spinnerPanel, BorderLayout.NORTH);

		
//		JPanel toolBarPanelOseaMethod    = new JPanel(new BorderLayout(5, 5));
//		//JLabel osea = new JLabel("OSEA method");
//		//osea.setHorizontalAlignment(SwingConstants.CENTER);
//		//toolBarPanelOseaMethod.add(osea, BorderLayout.NORTH);
//		toolBarPanelOseaMethod.add(createJToolBarOseaMethod(), BorderLayout.CENTER);
//		toolBarPanelOseaMethod.setBorder(new TitledBorder(null, "OSEA method", TitledBorder.LEADING, TitledBorder.TOP, null, null));

//		JPanel toolBarPanelOutputOption  = new JPanel(new BorderLayout(5, 5));
//		//JLabel outputOpt = new JLabel("Output option");
//		//outputOpt.setHorizontalAlignment(SwingConstants.CENTER);
//		//toolBarPanelOutputOption.add(outputOpt, BorderLayout.NORTH);
//		toolBarPanelOutputOption.add(createJToolBarOutputOptions(), BorderLayout.CENTER);
//		toolBarPanelOutputOption.setBorder(new TitledBorder(null, "Output option", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel toolBarPanel = new JPanel(new GridLayout(0, 2, 2, 2));
		toolBarPanel.add(createJToolBarOseaMethod(), BorderLayout.CENTER);
		toolBarPanel.add(createJToolBarOutputOptions(), BorderLayout.CENTER);
		buttQRSDetect2.setSelected(true);
		buttOutputIntervals.setSelected(true);
		this.content.add(toolBarPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 5, 0));
		buttonPanel.add(createButtOK());
		buttonPanel.add(createButtCancel());

		JPanel butTab = new JPanel(new BorderLayout(5, 5));
		butTab.add(buttonPanel, BorderLayout.NORTH);
		//butTab.add(new JScrollPane(createInfoTable()), BorderLayout.CENTER);
		this.content.add(butTab, BorderLayout.SOUTH);

		this.getContentPane().add(this.content);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.pack();
		CommonTools.centerFrameOnScreen(this);

		this.setVisible(true);
	}

	/**
	 * Retrieves the plot informations for the table tableModel.
	 * 
	 * @param file
	 * @return <code>true</code> if meta data reading is successful,
	 *         <code>false</code> otherwise
	 */
	private boolean getNewTableData(File file) {
		try {
			this.tableData = qrsPeaksExtractor.readECGMetaData(file);
			return true;
		} catch (UnreadableECGFileException e) {
			logger.error("An error occurred: ", e);
			DialogUtil.getInstance().showErrorMessage(
					"An error occurred, this ECG file may be corrupt: \n"
							+ file.toString(), e);
			return false;
		}
	}

	/**
	 * Creates a new JTable instance of the information table.
	 * 
	 * @return the {@link JTable} for display
	 */
	private JTable createInfoTable() {
		//This method is not adapted to ECG files yet!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//		this.tableModel = new DefaultTableModel() {
//			private static final long serialVersionUID = -4270936313059404384L; 
//
//			@Override
//			public boolean isCellEditable(int row, int column) {
//				// all cells false
//				return false;
//			};
//
//		};

		this.infoTable = new JTable(this.tableModel);
//
//		this.infoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		this.infoTable.setRowSelectionAllowed(true);
//
//		DefaultTableCellRenderer dcr = (DefaultTableCellRenderer) this.infoTable.getDefaultRenderer(tableModel.getColumnClass(0));
//		dcr.setHorizontalAlignment(SwingConstants.RIGHT);
//
//		//this.tableModel.setDataVector(this.tableData, _columnIdentifiers);
//
//		TableColumn column = null;
//		for (int i = 0; i < 6; i++) {
//			column = this.infoTable.getColumnModel().getColumn(i);
//			if (i == 0) {
//				column.setPreferredWidth(15);
//			} else if (i == 1) {
//				column.setPreferredWidth(40);
//			} else if (i == 2) {
//				column.setPreferredWidth(40);
//			} else {
//				column.setPreferredWidth(50);
//			}
//		}
		return this.infoTable;
	}

	/**
	 * Gets the current selection of the GUI (parameters).
	 * 
	 * @return an <code>int[4]</code> of the selected values
	 */
	private int[] getParametersFromGUI() {
		logger.debug("Selected offset : " + offSet);
		logger.debug("Selected sample rate : " + sampleRate);
		logger.debug("Selected osea method: " + oseaMethod);
		logger.debug("Output option: " + outputOption);
		return new int[] { offSet, sampleRate, oseaMethod, outputOption };
	}

	/**
	 * This method returns a spinner for the starting offset selection
	 * 
	 * @return a {@link JSpinner} with the starting offset
	 */
	private JSpinner createJSpinnerOffSet() {
		JFormattedTextField ftf = null;
		SpinnerModel sModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
		jSpinnerOffSet = new JSpinner(sModel);
		// Set the formatted text field.
		ftf = getTextField(jSpinnerOffSet);
		if (ftf != null) {
			ftf.setColumns(5);
			ftf.setHorizontalAlignment(SwingConstants.CENTER);
			ftf.setPreferredSize(new Dimension(20, 20));
			ftf.setEditable(true);
		}
		jSpinnerOffSet.addChangeListener(this);
		return jSpinnerOffSet;
	}

	/**
	 * This method returns a spinner for the sample rate selection
	 * 
	 * @return a {@link JSpinner} with the sample rate
	 */
	private JSpinner createJSpinnerSampleRate() {
		JFormattedTextField ftf = null;
		SpinnerModel sModel = new SpinnerNumberModel(180, 1, Integer.MAX_VALUE, 1); //180Hz Herbert, 125 HZ Helena
		jSpinnerSampleRate = new JSpinner(sModel);
		// Set the formatted text field.
		ftf = getTextField(jSpinnerSampleRate);
		if (ftf != null) {
			ftf.setColumns(5);
			ftf.setHorizontalAlignment(SwingConstants.CENTER);
			ftf.setPreferredSize(new Dimension(20, 20));
			ftf.setEditable(true);
		}
		jSpinnerSampleRate.addChangeListener(this);
		return jSpinnerSampleRate;
	}

	/**
	 * Return the formatted text field used by the editor, or null if the editor
	 * doesn't descend from JSpinner.DefaultEditor.
	 * 
	 * return a {@link JFormattedTextField}, or <code>null</code>
	 */
	private JFormattedTextField getTextField(JSpinner spinner) {
		JComponent editor = spinner.getEditor();
		if (editor instanceof JSpinner.DefaultEditor) {
			return ((JSpinner.DefaultEditor) editor).getTextField();
		} else {
			System.err.println("Unexpected editor type: "+ spinner.getEditor().getClass()+ " isn't a descendant of DefaultEditor");
			return null;
		}
	}

	/**
	 * This method initializes the Option: QRSDetect
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtQRSDetect() {
		buttQRSDetect = new JRadioButton();
		buttQRSDetect.setText("QRSDetect");
		buttQRSDetect.setToolTipText("osea QRSDetect option using medians");
		buttQRSDetect.addActionListener(this);
		buttQRSDetect.setActionCommand("parameter");
		return buttQRSDetect;
	}

	/**
	 * This method initializes the Option: QRSDetect2
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtQRSDetect2() {
		buttQRSDetect2 = new JRadioButton();
		buttQRSDetect2.setText("QRSDetect2");
		buttQRSDetect2.setToolTipText("osea QRSDetect2 option using means");
		buttQRSDetect2.addActionListener(this);
		buttQRSDetect2.setActionCommand("parameter");
		return buttQRSDetect2;
	}

	/**
	 * This method initializes the Option: Beat detection and classify
	 * 
	 * @return javax.swing.JRadioButto
	 */
	private JRadioButton createJRadioButtonMenuButtBeatDetectClass() {
		buttBeatDetectClass = new JRadioButton();
		buttBeatDetectClass.setText("BeatDetectionAndClassify");
		buttBeatDetectClass.setToolTipText("osea BeatDetectionAndClassify option using QRSDetect2 for detection");
		buttBeatDetectClass.addActionListener(this);
		buttBeatDetectClass.setActionCommand("parameter");
		return buttBeatDetectClass;
	}


	/**
	 * This method initializes jJToolBarBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar createJToolBarOseaMethod() {
		jToolBarOseaMethod = new JToolBar("Osea Method");
		jToolBarOseaMethod.setOrientation(1); // 0 Horizontal 1 Vertical orientation
		jToolBarOseaMethod.setBorder(new TitledBorder(null, "OSEA method", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jToolBarOseaMethod.setFloatable(false);
		jToolBarOseaMethod.add(createJRadioButtonMenuButtQRSDetect());
		jToolBarOseaMethod.add(createJRadioButtonMenuButtQRSDetect2());
		jToolBarOseaMethod.add(createJRadioButtonMenuButtBeatDetectClass());

		buttGroupOseaMethod = new ButtonGroup();
		buttGroupOseaMethod.add(buttQRSDetect);
		buttGroupOseaMethod.add(buttQRSDetect2);
		buttGroupOseaMethod.add(buttBeatDetectClass);

		return jToolBarOseaMethod;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtCoordinates() {
		buttOutputCoordinates = new JRadioButton();
		buttOutputCoordinates.setText("XY coordinates");
		buttOutputCoordinates.setToolTipText("Coordinates of QRS peak points");
		buttOutputCoordinates.addActionListener(this);
		buttOutputCoordinates.setActionCommand("parameter");
		return buttOutputCoordinates;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtIntervals() {
		buttOutputIntervals = new JRadioButton();
		buttOutputIntervals.setText("Intervals");
		buttOutputIntervals.setToolTipText("Intervals between QRS peaks");
		buttOutputIntervals.addActionListener(this);
		buttOutputIntervals.setActionCommand("parameter");
		return buttOutputIntervals;
	}


	/**
	 * This method initializes jJToolBarBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar createJToolBarOutputOptions() {
		jToolBarOutputOptions = new JToolBar("File outputOption");
		jToolBarOutputOptions.setOrientation(1); // 0 Horizontal 1 Vertical orientation
		jToolBarOutputOptions.setBorder(new TitledBorder(null, "Output option", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jToolBarOutputOptions.setFloatable(false);
		jToolBarOutputOptions.add(createJRadioButtonMenuButtCoordinates());
		jToolBarOutputOptions.add(createJRadioButtonMenuButtIntervals());
		buttGroupOutputOptions = new ButtonGroup();
		buttGroupOutputOptions.add(buttOutputCoordinates);
		buttGroupOutputOptions.add(buttOutputIntervals);

		return jToolBarOutputOptions;
	}

	/**
	 * Get the OK button.
	 * 
	 * @return the buttOK
	 */
	private JButton createButtOK() {
		buttOK = new JButton("OK");
		buttOK.addActionListener(this);
		buttOK.setActionCommand("ok");
		return buttOK;
	}

	/**
	 * Get the cancel button.
	 * 
	 * @return the button
	 */
	private JButton createButtCancel() {
		buttCancel = new JButton("Cancel");
		buttCancel.addActionListener(this);
		buttCancel.setActionCommand("cancel");
		return buttCancel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (buttQRSDetect.isSelected())       oseaMethod = 0;
		if (buttQRSDetect2.isSelected())      oseaMethod = 1;
		if (buttBeatDetectClass.isSelected()) oseaMethod = 2;
	

		if (buttOutputCoordinates.isSelected()) outputOption = 0;
		if (buttOutputIntervals.isSelected())   outputOption = 1;

		if ("ok".equals(e.getActionCommand())) {
			int[] params = getParametersFromGUI();
			//
			// // check which image is about to be extracted and ask user for
			// // continuing
			// if (params[0] == 1) {
			// int selection = DialogUtil
			// .getInstance()
			// .showDefaultWarnMessage(
			// I18N.getMessage(
			// "application.dialog.largeQRSPeaksWarning",
			// null, null));
			// if (selection != IDialogUtil.YES_OPTION) {
			// logger.info("User cancelled the extraction process.");
			// return;
			// }
			// }
			// collect parameters and call the extractor routine
			QRSPeaksExtractor task = new QRSPeaksExtractor();

			task.setParams(params);
			task.setFiles(files);
			task.addPropertyChangeListener(Application.getMainFrame()
					.getStatusPanel());

			WaitingDialog dlg = new WaitingDialog(
					"Extracting ECG file(s), please wait...", false);
			dlg.getPcs().addPropertyChangeListener(
					Application.getMainFrame().getStatusPanel());
			task.addPropertyChangeListener(new CompletionWaiter(dlg));

			task.execute();
			dlg.setVisible(true);

			this.dispose();
		}

		if ("cancel".equals(e.getActionCommand())) {
			this.dispose();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (jSpinnerOffSet == e.getSource()) {
			offSet = (((Number) jSpinnerOffSet.getValue()).intValue());
		}
		if (jSpinnerSampleRate == e.getSource()) {
			sampleRate = (((Number) jSpinnerSampleRate.getValue()).intValue());
		}
	}


	

	/**
	 * @return the buttOK
	 */
	public JButton getButtOK() {
		return buttOK;
	}

	/**
	 * @param buttOK
	 *            the buttOK to set
	 */
	public void setButtOK(JButton buttOK) {
		this.buttOK = buttOK;
	}

	/**
	 * @return the buttCancel
	 */
	public JButton getButtCancel() {
		return buttCancel;
	}

	/**
	 * @param buttCancel
	 *            the buttCancel to set
	 */
	public void setButtCancel(JButton buttCancel) {
		this.buttCancel = buttCancel;
	}

	/**
	 * @return the files
	 */
	public File[] getFiles() {
		return files;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles(File[] files) {
		this.files = files;
	}

	/**
	 * @return the qrsPeaksExtractor
	 */
	public QRSPeaksExtractor getQRSPeaksExtractor() {
		return qrsPeaksExtractor;
	}

	/**
	 * @return the tableModel
	 */
	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	/**
	 * @return the tableData
	 */
	public Object[][] getTableData() {
		return tableData;
	}

	/**
	 * @param qraPeaksExtractor
	 *            the qrsPeaksExtractor to set
	 */
	public void setQRSPeaksExtractor(QRSPeaksExtractor qrsPeaksExtractor) {
		this.qrsPeaksExtractor = qrsPeaksExtractor;
	}

	/**
	 * @param tableModel
	 *            the tableModel to set
	 */
	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}

	/**
	 * @return the infoTable
	 */
	public JTable getInfoTable() {
		return infoTable;
	}

	/**
	 * @param infoTable
	 *            the infoTable to set
	 */
	public void setInfoTable(JTable infoTable) {
		this.infoTable = infoTable;
	}

}// END

