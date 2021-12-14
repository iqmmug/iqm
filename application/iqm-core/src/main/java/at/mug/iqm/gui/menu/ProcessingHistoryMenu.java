package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ProcessingHistoryMenu.java
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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

 
 

import at.mug.iqm.api.processing.ProcessingHistory;
import at.mug.iqm.commons.gui.GenericFileDialog;
import at.mug.iqm.commons.io.PlainTextFileWriter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.core.I18N;
import at.mug.iqm.gui.TextDisplayFrame;
import at.mug.iqm.gui.TextPanel;

/**
 * This is the menu entry for managing the processing history.
 * 
 * @author Philipp Kainz
 * @since 3.1
 */
public class ProcessingHistoryMenu extends JMenu implements ActionListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -6493431116988885579L;

	// class logger
	  

	private static TextDisplayFrame historyFrame = null;
	private TextPanel tp = null;

	private JMenuItem showHistory = null;
	private JMenuItem clearHistory = null;
	private JMenuItem exportHistory = null;

	/**
	 * The default constructor.
	 */
	public ProcessingHistoryMenu() {
		setText(I18N.getGUILabelText("menu.procHistory.text"));

		this.add(this.createShowHistory());
		this.add(this.createClearHistory());
		this.addSeparator();
		this.add(this.createExportHistory());

		this.tp = new TextPanel();
		historyFrame = new TextDisplayFrame(tp);
		historyFrame.setTitle("Processing History");
		historyFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}

	/**
	 * Create the menu entry for exporting the history.
	 * 
	 * @return
	 */
	private JMenuItem createExportHistory() {
		this.exportHistory = new JMenuItem();
		this.exportHistory.setText(I18N
				.getGUILabelText("menu.procHistory.export.text"));
		this.exportHistory.setToolTipText(I18N
				.getGUILabelText("menu.procHistory.export.ttp"));
		this.exportHistory.addActionListener(this);
		this.exportHistory.setActionCommand("exporthistory");
		return this.exportHistory;
	}

	/**
	 * Create the menu entry for clearing the history.
	 * 
	 * @return
	 */
	private JMenuItem createClearHistory() {
		this.clearHistory = new JMenuItem();
		this.clearHistory.setText(I18N
				.getGUILabelText("menu.procHistory.clear.text"));
		this.clearHistory.setToolTipText(I18N
				.getGUILabelText("menu.procHistory.clear.ttp"));
		this.clearHistory.addActionListener(this);
		this.clearHistory.setActionCommand("clearhistory");
		return this.clearHistory;
	}

	/**
	 * Create the menu entry for showing the history.
	 * 
	 * @return
	 */
	private JMenuItem createShowHistory() {
		this.showHistory = new JMenuItem();
		this.showHistory.setText(I18N
				.getGUILabelText("menu.procHistory.show.text"));
		this.showHistory.setToolTipText(I18N
				.getGUILabelText("menu.procHistory.show.ttp"));
		this.showHistory.addActionListener(this);
		this.showHistory.setActionCommand("showhistory");
		return this.showHistory;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("IQM:  "+e.getActionCommand());

		if ("showhistory".equals(e.getActionCommand())) {
			tp.reset();
			tp.writeText(ProcessingHistory.getInstance().listEntries());
			tp.revalidate();
			bringToFront(historyFrame);
		}

		if ("clearhistory".equals(e.getActionCommand())) {
			int sel = DialogUtil.getInstance().showDefaultWarnMessage(
					I18N.getMessage("warn.procHistory.clear"));

			if (sel != DialogUtil.YES_OPTION) {
				return;
			}

			tp.reset();
			ProcessingHistory.getInstance().getEntries().clear();
			tp.revalidate();
		}

		if ("exporthistory".equals(e.getActionCommand())) {
			String fn = "IQMProcessingHistory_"
					+ new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			GenericFileDialog fd = new GenericFileDialog(null, "Save Processing History");
			fd.setSelectedFile(new File(fn));
			int sel = fd.showSaveDialog(null);
			if (sel != GenericFileDialog.APPROVE_OPTION) {
				return;
			}

			PlainTextFileWriter ptfw = new PlainTextFileWriter(
					fd.getSelectedFile(), ProcessingHistory.getInstance()
							.listEntries());
			try {
				ptfw.write();
			} catch (IOException e1) {
				DialogUtil.getInstance().showErrorMessage(
						"Cannot write plain text file!", e1, true);
			}

		}
	}

	private void bringToFront(Window w) {
		if (w.isVisible())
			w.toFront();
		else
			w.setVisible(true);
	}

}
