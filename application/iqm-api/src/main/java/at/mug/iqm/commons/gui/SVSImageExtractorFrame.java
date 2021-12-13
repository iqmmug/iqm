package at.mug.iqm.commons.gui;

/*
 * #%L
 * Project: IQM - API
 * File: SVSImageExtractorFrame.java
 * 
 * $Id$
 * $HeadURL$
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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.exception.UnreadableSVSFileException;
import at.mug.iqm.api.gui.WaitingDialog;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.CompletionWaiter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.image.SVSImageExtractor;

/**
 * This GUI class provides a front end for the image extraction out of an Aperio
 * SVS file.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2009 09
 * 
 */
public class SVSImageExtractorFrame extends JFrame implements ActionListener,
		ChangeListener {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -1164991986615901682L;

	// Logging variables
	private static final Logger logger = LogManager.getLogger(SVSImageExtractorFrame.class);

	private JSpinner jSpinnerImgNum;
	private JSpinner jSpinnerRescale;
	private int maxImgNum = 1;
	private int imgNum = 1; // image number
	private int rescale = 1; // rescaling factor formula: original size/rescale
	private int interP = 1;
	private int extension = 0; // tif output image

	private JRadioButton buttNN = null; // nearest neighbor
	private JRadioButton buttBL = null; // bilinear
	private JRadioButton buttBC = null; // bicubic
	private JRadioButton buttBC2 = null; // bicubic2
	private JToolBar jToolBarIntP = null;
	private ButtonGroup buttGroupIntP = null;

	private JRadioButton buttTif = null; // output image
	private JRadioButton buttJpg = null; //
	private JRadioButton buttPng = null; //
	private JRadioButton buttBmp = null; //
	private JToolBar jToolBarExt = null;
	private ButtonGroup buttGroupExt = null;

	private JButton buttOK = null;
	private JButton buttCancel = null;

	private JPanel content = new JPanel();

	private File[] files = null;

	private SVSImageExtractor svsImageExtractor = new SVSImageExtractor();

	private JTable infoTable = null;
	private Object[] _columnIdentifiers = { "Image#", "Width [px]",
			"Height [px]", "Width Ratio [x:1]", "Height Ratio [y:1]",
			"Expected Size [GiB]" };
	private DefaultTableModel tableModel = null;
	private Object[][] tableData = null;

	/**
	 * Create a new instance and pass the selected files.
	 * 
	 * @param files
	 *            - a java.io.File[]
	 */
	public SVSImageExtractorFrame(File[] files) {
		this.setFiles(files);
		this.createAndAssemble();
	}

	/**
	 * Creates objects and assembles the panels.
	 */
	private void createAndAssemble() {

		// get table data of the first image and determine maxImgNum
		boolean loadSuccess = this.getNewTableData(this.files[0]);
		// this.getNewTableData(testFiles[0]);
		if (!loadSuccess) {
			return;
		}

		this.content.setLayout(new BorderLayout(0, 5));

		this.content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.setTitle("Choose Values");
		this.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.menu.file.extractSVS")).getImage());

		JPanel spinnerPanel = new JPanel(new GridLayout(2, 0, 2, 2));
		JPanel spinnerImgNumPanel = new JPanel(new BorderLayout(5, 5));
		JPanel spinnerRescalePanel = new JPanel(new BorderLayout(5, 5));

		spinnerImgNumPanel.add(new JLabel("Image Number:"), BorderLayout.NORTH);
		spinnerImgNumPanel.add(createJSpinnerImgNum(), BorderLayout.CENTER);
		spinnerRescalePanel.add(new JLabel("Rescale Factor:"),
				BorderLayout.NORTH);
		spinnerRescalePanel.add(createJSpinnerRescale(), BorderLayout.CENTER);
		spinnerPanel.add(spinnerImgNumPanel);
		spinnerPanel.add(spinnerRescalePanel);
		this.content.add(spinnerPanel, BorderLayout.NORTH);

		JPanel toolBarPanel = new JPanel(new GridLayout(0, 2, 2, 2));
		JPanel toolBarIntPPanel = new JPanel(new BorderLayout(5, 5));
		JPanel toolBarExtPanel = new JPanel(new BorderLayout(5, 5));

		JLabel intp = new JLabel("Interpolation Method");
		intp.setHorizontalAlignment(SwingConstants.CENTER);
		toolBarIntPPanel.add(intp, BorderLayout.NORTH);
		toolBarIntPPanel.add(createJToolBarIntP(), BorderLayout.CENTER);

		JLabel ext = new JLabel("Image File Extension");
		ext.setHorizontalAlignment(SwingConstants.CENTER);
		toolBarExtPanel.add(ext, BorderLayout.NORTH);
		toolBarExtPanel.add(createJToolBarExt(), BorderLayout.CENTER);
		toolBarPanel.add(toolBarIntPPanel);
		toolBarPanel.add(toolBarExtPanel);
		buttBL.setSelected(true);
		buttTif.setSelected(true);
		this.content.add(toolBarPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 5, 0));
		buttonPanel.add(createButtOK());
		buttonPanel.add(createButtCancel());

		JPanel butTab = new JPanel(new BorderLayout(5, 5));
		butTab.add(buttonPanel, BorderLayout.NORTH);
		butTab.add(new JScrollPane(createInfoTable()), BorderLayout.CENTER);
		this.content.add(butTab, BorderLayout.SOUTH);

		this.getContentPane().add(this.content);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.pack();
		CommonTools.centerFrameOnScreen(this);

		this.setVisible(true);
	}

	/**
	 * Retrieves the image informations for the table tableModel.
	 * 
	 * @param file
	 * @return <code>true</code> if meta data reading is successful,
	 *         <code>false</code> otherwise
	 */
	private boolean getNewTableData(File file) {
		try {
			this.tableData = svsImageExtractor.readImageMetaData(file);
			this.maxImgNum = this.tableData.length;
			return true;
		} catch (UnreadableSVSFileException e) {
			logger.error("An error occurred: ", e);
			DialogUtil.getInstance().showErrorMessage(
					"An error occurred, this SVS file is corrupt: \n"
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
		this.tableModel = new DefaultTableModel() {
			private static final long serialVersionUID = -4270936413059404384L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			};

		};

		this.infoTable = new JTable(this.tableModel);

		this.infoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.infoTable.setRowSelectionAllowed(true);

		DefaultTableCellRenderer dcr = (DefaultTableCellRenderer) this.infoTable
				.getDefaultRenderer(tableModel.getColumnClass(0));
		dcr.setHorizontalAlignment(SwingConstants.RIGHT);

		this.tableModel.setDataVector(this.tableData, _columnIdentifiers);

		TableColumn column = null;
		for (int i = 0; i < 6; i++) {
			column = this.infoTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(15);
			} else if (i == 1) {
				column.setPreferredWidth(40);
			} else if (i == 2) {
				column.setPreferredWidth(40);
			} else {
				column.setPreferredWidth(50);
			}
		}
		return this.infoTable;
	}

	/**
	 * Gets the current selection of the GUI (parameters).
	 * 
	 * @return an <code>int[4]</code> of the selected values
	 */
	private int[] getParametersFromGUI() {
		logger.debug("Selected Image Number : " + imgNum);
		logger.debug("Selected Rescale Factor : " + rescale);
		logger.debug("Selected Interpolation Method: " + interP);
		logger.debug("File extension: " + extension);
		return new int[] { imgNum, rescale, interP, extension };
	}

	/**
	 * This method returns a spinner for the image number selection
	 * 
	 * @return a {@link JSpinner} with the number of images in the stack
	 */
	private JSpinner createJSpinnerImgNum() {
		JFormattedTextField ftf = null;
		SpinnerModel sModel = new SpinnerNumberModel(1, 1, this.maxImgNum, 1);
		jSpinnerImgNum = new JSpinner(sModel);
		// Set the formatted text field.
		ftf = getTextField(jSpinnerImgNum);
		if (ftf != null) {
			ftf.setColumns(5);
			ftf.setHorizontalAlignment(SwingConstants.CENTER);
			ftf.setPreferredSize(new Dimension(20, 20));
			ftf.setEditable(false);
		}
		jSpinnerImgNum.addChangeListener(this);
		return jSpinnerImgNum;
	}

	/**
	 * This method returns a spinner for the rescale factor selection
	 * 
	 * @return a {@link JSpinner} with the rescale factors
	 */
	private JSpinner createJSpinnerRescale() {
		JFormattedTextField ftf = null;
		SpinnerModel sModel = new SpinnerNumberModel(1, 1, 10, 1);
		jSpinnerRescale = new JSpinner(sModel);
		// Set the formatted text field.
		ftf = getTextField(jSpinnerRescale);
		if (ftf != null) {
			ftf.setColumns(5);
			ftf.setHorizontalAlignment(SwingConstants.CENTER);
			ftf.setPreferredSize(new Dimension(20, 20));
			ftf.setEditable(false);
		}
		jSpinnerRescale.addChangeListener(this);
		return jSpinnerRescale;
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
			System.err.println("Unexpected editor type: "
					+ spinner.getEditor().getClass()
					+ " isn't a descendant of DefaultEditor");
			return null;
		}
	}

	/**
	 * This method initializes the Option: Nearest Neighbor
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtNN() {
		buttNN = new JRadioButton();
		buttNN.setText("Nearest Neighbor");
		buttNN.setToolTipText("uses Nearest Neighbor resampling for resizing");
		buttNN.addActionListener(this);
		buttNN.setActionCommand("parameter");
		return buttNN;
	}

	/**
	 * This method initializes the Option: Bilinear interpolation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtBL() {
		buttBL = new JRadioButton();
		buttBL.setText("Bilinear");
		buttBL.setToolTipText("uses Bilinear interpolation for resizing");
		buttBL.addActionListener(this);
		buttBL.setActionCommand("parameter");
		return buttBL;
	}

	/**
	 * This method initializes the Option: Bicubic interpolation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtBC() {
		buttBC = new JRadioButton();
		buttBC.setText("Bicubic");
		buttBC.setToolTipText("uses Bicubic interpoolation for resizing");
		buttBC.addActionListener(this);
		buttBC.setActionCommand("parameter");
		return buttBC;
	}

	/**
	 * This method initializes the Option: Bicubic2 interpolation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtBC2() {
		buttBC2 = new JRadioButton();
		buttBC2.setText("Bicubic2");
		buttBC2.setToolTipText("uses Bicubic2 interpolation for resizing");
		buttBC2.addActionListener(this);
		buttBC2.setActionCommand("parameter");
		return buttBC2;
	}

	/**
	 * This method initializes jJToolBarBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar createJToolBarIntP() {
		jToolBarIntP = new JToolBar("Interpolation Method");
		jToolBarIntP.setOrientation(1); // 0 Horizontal 1 Vertical orientation
		jToolBarIntP.setFloatable(false);
		jToolBarIntP.add(createJRadioButtonMenuButtNN());
		jToolBarIntP.add(createJRadioButtonMenuButtBL());
		jToolBarIntP.add(createJRadioButtonMenuButtBC());
		jToolBarIntP.add(createJRadioButtonMenuButtBC2());

		buttGroupIntP = new ButtonGroup();
		buttGroupIntP.add(buttNN);
		buttGroupIntP.add(buttBL);
		buttGroupIntP.add(buttBC);
		buttGroupIntP.add(buttBC2);

		return jToolBarIntP;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtTif() {
		buttTif = new JRadioButton();
		buttTif.setText(IQMConstants.TIF_EXTENSION);
		buttTif.setToolTipText("TIF/TIFF output image");
		buttTif.addActionListener(this);
		buttTif.setActionCommand("parameter");
		return buttTif;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtJpg() {
		buttJpg = new JRadioButton();
		buttJpg.setText(IQMConstants.JPG_EXTENSION);
		buttJpg.setToolTipText("JPEG output image");
		buttJpg.addActionListener(this);
		buttJpg.setActionCommand("parameter");
		return buttJpg;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtPng() {
		buttPng = new JRadioButton();
		buttPng.setText(IQMConstants.PNG_EXTENSION);
		buttPng.setToolTipText("PNG output image");
		buttPng.addActionListener(this);
		buttPng.setActionCommand("parameter");
		return buttPng;
	}

	/**
	 * This method initializes the Option:
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton createJRadioButtonMenuButtBmp() {
		buttBmp = new JRadioButton();
		buttBmp.setText(IQMConstants.BMP_EXTENSION);
		buttBmp.setToolTipText("BMP output image");
		buttBmp.addActionListener(this);
		buttBmp.setActionCommand("parameter");
		return buttBmp;
	}

	/**
	 * This method initializes jJToolBarBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar createJToolBarExt() {
		jToolBarExt = new JToolBar("File Extension");
		jToolBarExt.setOrientation(1); // 0 Horizontal 1 Vertical orientation
		jToolBarExt.setFloatable(false);
		jToolBarExt.add(createJRadioButtonMenuButtTif());
		jToolBarExt.add(createJRadioButtonMenuButtJpg());
		jToolBarExt.add(createJRadioButtonMenuButtPng());
		jToolBarExt.add(createJRadioButtonMenuButtBmp());

		buttGroupExt = new ButtonGroup();
		buttGroupExt.add(buttTif);
		buttGroupExt.add(buttJpg);
		buttGroupExt.add(buttPng);
		buttGroupExt.add(buttBmp);

		return jToolBarExt;
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
		if (buttNN.isSelected())
			interP = IQMConstants.INTERPOLATION_NEAREST_NEIGHBOR;
		if (buttBL.isSelected())
			interP = IQMConstants.INTERPOLATION_BILINEAR;
		if (buttBC.isSelected())
			interP = IQMConstants.INTERPOLATION_BICUBIC;
		if (buttBC2.isSelected())
			interP = IQMConstants.INTERPOLATION_BICUBIC2;

		if (buttTif.isSelected())
			extension = 0;
		if (buttJpg.isSelected())
			extension = 1;
		if (buttPng.isSelected())
			extension = 2;
		if (buttBmp.isSelected())
			extension = 3;

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
			// "application.dialog.largeSVSImageWarning",
			// null, null));
			// if (selection != IDialogUtil.YES_OPTION) {
			// logger.info("User cancelled the extraction process.");
			// return;
			// }
			// }
			// collect parameters and call the extractor routine
			SVSImageExtractor task = new SVSImageExtractor();

			task.setParams(params);
			task.setFiles(files);
			task.addPropertyChangeListener(Application.getMainFrame()
					.getStatusPanel());

			WaitingDialog dlg = new WaitingDialog(
					"Extracting SVS image(s), please wait...", false);
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
		if (jSpinnerImgNum == e.getSource()) {
			imgNum = (((Number) jSpinnerImgNum.getValue()).intValue());
		}
		if (jSpinnerRescale == e.getSource()) {
			rescale = (((Number) jSpinnerRescale.getValue()).intValue());
		}
	}

	/**
	 * @return the maxImgNum
	 */
	public int getMaxImgNum() {
		return maxImgNum;
	}

	/**
	 * @param maxImgNum
	 *            the maxImgNum to set
	 */
	public void setMaxImgNum(int maxImgNum) {
		this.maxImgNum = maxImgNum;
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
	 * @return the svsImageExtractor
	 */
	public SVSImageExtractor getSvsImageExtractor() {
		return svsImageExtractor;
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
	 * @param svsImageExtractor
	 *            the svsImageExtractor to set
	 */
	public void setSvsImageExtractor(SVSImageExtractor svsImageExtractor) {
		this.svsImageExtractor = svsImageExtractor;
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

