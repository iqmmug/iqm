package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ScriptEditor.java
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchDialogSearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.script.GroovyScriptConnector;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.CompletionWaiter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.OutputStreamInterceptor;
import at.mug.iqm.commons.util.PropertyManager;
import at.mug.iqm.config.ConfigManager;

/**
 * This class represents the script editor for editing Groovy scripts in IQM.
 * 
 * @author Philipp Kainz
 * @since 3.1
 * 
 */
public class ScriptEditor extends JFrame implements ActionListener,
		PropertyChangeListener, WindowListener {

	private static final Logger logger = Logger.getLogger(ScriptEditor.class);

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 8041611573627159195L;
	private static ScriptEditor currentInstance;
	private JPanel contentPane;
	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private PropertyManager pm;

	/**
	 * The list holding all {@link ScriptEditorTab}s.
	 */
	private ArrayList<ScriptEditorTab> allTabs = new ArrayList<ScriptEditorTab>(
			10);

	private String defaultTitle = I18N
			.getGUILabelText("script.editor.frame.defaultTitle");
	private JLabel lblInfo;
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmOpen;
	private JMenuItem mntmClose;
	private JMenuItem mntmExit;
	private JMenuItem mntmUndo;
	private JMenuItem mntmRedo;
	private JMenuItem mntmCut;
	private JMenuItem mntmCopy;
	private JMenuItem mntmPaste;
	private JMenuItem mntmFind;
	private JMenuItem mntmReplace;
	private JMenuItem mntmGoToLine;
	private JLabel lblProcessingIcon;
	private GroovyScriptConsoleFrame frame;
	private OutputStreamInterceptor osi;
	private PrintStream stdOut;

	/**
	 * The executing thread.
	 */
	private SwingWorker<Void, Void> executor;
	private JButton btnConsole;
	private Component horizontalStrut;
	private JTabbedPane tabbedPane;
	private JMenuItem mntmNew;
	private JMenuItem mntmSaveAll;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ScriptEditor frame = new ScriptEditor();
					frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
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
	private ScriptEditor() {
		setTitle(defaultTitle);
		addWindowListener(this);
		setIconImage(new ImageIcon(
				Resources.getImageURL("icon.script.generic16")).getImage());
		setBounds(100, 100, 450, 300);

		JMenuBar scriptMenuBar = new JMenuBar();
		setJMenuBar(scriptMenuBar);

		JMenu mnFile = new JMenu(
				I18N.getGUILabelText("script.editor.menu.file.text"));
		mnFile.setMnemonic('F');
		scriptMenuBar.add(mnFile);

		mntmOpen = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.open.text"));
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				getToolkit().getMenuShortcutKeyMask()));
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// open a new file
				openFile(true);
			}
		});

		mntmNew = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.new.text"));
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// open a new tab
				newTab();
			}
		});
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				getToolkit().getMenuShortcutKeyMask()));
		mnFile.add(mntmNew);
		mnFile.add(mntmOpen);

		mntmSave = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.save.text"));
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				getToolkit().getMenuShortcutKeyMask()));
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// save the tab to existing or new file
				ScriptEditorTab tab = getSelectedTab();
				if (tab.getFile() == null) {
					saveAs();
				} else {
					save(getSelectedTab());
				}
			}
		});
		mnFile.add(mntmSave);

		mntmSaveAs = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.saveas.text"));
		mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				getToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_MASK));
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// save as new file
				saveAs();
			}
		});
		mnFile.add(mntmSaveAs);

		mntmSaveAll = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.saveall.text"));
		mntmSaveAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				getToolkit().getMenuShortcutKeyMask() | InputEvent.ALT_MASK));
		mntmSaveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAll();
			}
		});
		mnFile.add(mntmSaveAll);

		mnFile.addSeparator();
		mntmClose = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.close.text"));
		mntmClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				getToolkit().getMenuShortcutKeyMask()));
		mntmClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// close the tab and file
				closeFile();
			}
		});
		mnFile.add(mntmClose);
		mnFile.addSeparator();

		mntmExit = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.exit.text"));
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				getToolkit().getMenuShortcutKeyMask()));
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				windowClosing(null);
			}
		});
		mnFile.add(mntmExit);

		JMenu mnEdit = new JMenu(
				I18N.getGUILabelText("script.editor.menu.edit.text"));
		mnEdit.setMnemonic('E');
		scriptMenuBar.add(mnEdit);

		mntmFind = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.find.text"));
		mntmFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (replaceDialog.isVisible()) {
					replaceDialog.setVisible(false);
				}
				findDialog.setVisible(true);
			}
		});
		mntmFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				getToolkit().getMenuShortcutKeyMask()));

		mntmUndo = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.undo.text"));
		mntmUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleUndoAction();
			}
		});
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				getToolkit().getMenuShortcutKeyMask()));
		mnEdit.add(mntmUndo);

		mntmRedo = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.redo.text"));
		mntmRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleRedoAction();
			}
		});
		mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				getToolkit().getMenuShortcutKeyMask()));
		mnEdit.add(mntmRedo);
		mnEdit.addSeparator();
		mntmCut = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.cut.text"));
		mntmCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getSelectedTab().getTextArea().cut();
			}
		});
		mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				getToolkit().getMenuShortcutKeyMask()));
		mnEdit.add(mntmCut);

		mntmCopy = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.copy.text"));
		mntmCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GroovyTextArea textArea = allTabs.get(
						tabbedPane.getSelectedIndex()).getTextArea();
				textArea.copy();
			}
		});
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				getToolkit().getMenuShortcutKeyMask()));
		mnEdit.add(mntmCopy);

		mntmPaste = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.paste.text"));
		mntmPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GroovyTextArea textArea = allTabs.get(
						tabbedPane.getSelectedIndex()).getTextArea();
				textArea.paste();
			}
		});
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				getToolkit().getMenuShortcutKeyMask()));
		mnEdit.add(mntmPaste);

		mnEdit.addSeparator();
		mnEdit.add(mntmFind);

		mntmReplace = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.replace.text"));
		mntmReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (findDialog.isVisible()) {
					findDialog.setVisible(false);
				}
				replaceDialog.setVisible(true);
			}
		});
		mntmReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				getToolkit().getMenuShortcutKeyMask()));
		mnEdit.add(mntmReplace);
		mnEdit.addSeparator();

		mntmGoToLine = new JMenuItem(
				I18N.getGUILabelText("script.editor.menu.gotoline.text"));
		mntmGoToLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (findDialog.isVisible()) {
					findDialog.setVisible(false);
				}
				if (replaceDialog.isVisible()) {
					replaceDialog.setVisible(false);
				}
				GoToDialog dialog = new GoToDialog(ScriptEditor.this);
				dialog.setResizable(false);
				GroovyTextArea textArea = allTabs.get(
						tabbedPane.getSelectedIndex()).getTextArea();
				dialog.setMaxLineNumberAllowed(textArea.getLineCount());
				dialog.setVisible(true);
				int line = dialog.getLineNumber();
				if (line > 0) {
					try {
						textArea.setCaretPosition(textArea
								.getLineStartOffset(line - 1));
						textArea.requestFocusInWindow();
					} catch (BadLocationException ble) {
						ble.printStackTrace();
					}
				}
			}
		});
		mntmGoToLine.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				getToolkit().getMenuShortcutKeyMask()));
		mnEdit.add(mntmGoToLine);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JToolBar toolBar = new JToolBar();
		toolBar.setBorderPainted(false);
		toolBar.setFloatable(false);
		contentPane.add(toolBar, BorderLayout.NORTH);

		JButton btnRun = new JButton(
				I18N.getGUILabelText("script.editor.btnRun.text"));
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runScript();
			}
		});
		btnRun.setToolTipText(I18N.getGUILabelText("script.editor.btnRun.ttp"));
		btnRun.setIcon(new ImageIcon(Resources.getImageURL("icon.script.run16")));
		toolBar.add(btnRun);

		btnConsole = new JButton(
				I18N.getGUILabelText("script.editor.btnConsole.text"));
		btnConsole.setIcon(new ImageIcon(Resources
				.getImageURL("icon.script.console16")));
		btnConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GroovyScriptConsoleFrame f = GroovyScriptConsoleFrame
						.getInstance();
				f.setVisible(true);
				f.toFront();
			}
		});

		horizontalStrut = Box.createHorizontalStrut(5);
		toolBar.add(horizontalStrut);
		btnConsole.setToolTipText(I18N
				.getGUILabelText("script.editor.btnConsole.ttp"));
		toolBar.add(btnConsole);

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		statusPanel.setPreferredSize(new Dimension(10, 18));
		contentPane.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setLayout(new BorderLayout(0, 0));

		lblInfo = new JLabel("");
		lblInfo.setBorder(new EmptyBorder(0, 5, 0, 5));
		statusPanel.add(lblInfo, BorderLayout.WEST);

		lblProcessingIcon = new JLabel("");
		lblProcessingIcon.setIcon(new ImageIcon(Resources
				.getImageURL("icon.processing.bar")));
		lblProcessingIcon.setVisible(false);
		statusPanel.add(lblProcessingIcon, BorderLayout.EAST);

		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				validateMenuState();
			}
		});

		contentPane.add(tabbedPane, BorderLayout.CENTER);

		// add an initial tab
		newTab();

		initDialogs();
		setInitialMenuState();

		enableF5Running();

		stdOut = System.out;

		// print output to standard out and frame console
		frame = GroovyScriptConsoleFrame.getInstance();
		osi = new OutputStreamInterceptor(frame.getConsole(), stdOut, true);

		pm = PropertyManager.getManager(this);

		pack();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	/**
	 * Sets the menu state according to the editing state of the text in the
	 * currently selected tab.
	 */
	private void validateMenuState() {
		try {
			// set the menu state for the currently selected tab
			logger.debug("Validating menu for SelectedTabIndex: "
					+ tabbedPane.getSelectedIndex());

			ScriptEditorTab tab = getSelectedTab();

			// an edited tab
			if (tab.isEdited()) {
				// enable save buttons
				mntmSave.setEnabled(true);
			} else if (tab.isEmpty()) {
				setInitialMenuState();
			} else {
				mntmSave.setEnabled(false);
			}
		} catch (Exception e) {
			logger.debug("No tab available yet: " + e);
		}
	}

	/**
	 * Enables running the script using the F5 key.
	 */
	private void enableF5Running() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "runscript");
		getRootPane().getActionMap().put("runscript", new AbstractAction() {

			private static final long serialVersionUID = -5477425838772337075L;

			public void actionPerformed(ActionEvent e) {
				runScript();
			}
		});
	}

	/**
	 * Disables running a script using the F5 key.
	 */
	private void disableF5Running() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		getRootPane().getActionMap().remove("runscript");
	}

	/**
	 * Creates the Find and Replace dialogs.
	 */
	public void initDialogs() {
		findDialog = new FindDialog(this, this);
		replaceDialog = new ReplaceDialog(this, this);
		replaceDialog.setSearchContext(findDialog.getSearchContext());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// check the current tab
		GroovyTextArea textArea = getSelectedTab().getTextArea();

		String command = e.getActionCommand();
		logger.debug("Action: " + command);
		SearchDialogSearchContext context = findDialog.getSearchContext();

		if (FindDialog.ACTION_FIND.equals(command)) {
			if (!SearchEngine.find(textArea, context)) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
		} else if (ReplaceDialog.ACTION_REPLACE.equals(command)) {
			if (!SearchEngine.replace(textArea, context)) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
		} else if (ReplaceDialog.ACTION_REPLACE_ALL.equals(command)) {
			int count = SearchEngine.replaceAll(textArea, context);
			JOptionPane.showMessageDialog(null,
					I18N.getMessage("dialog.replace.occurrences", count));
		}
	}

	/**
	 * Searches for an index in the tabbed pane where the file may already be
	 * loaded.
	 * 
	 * @param f
	 *            the file to search for
	 * @return the index of the tab, where the file is loaded in, or -1, if the
	 *         file is not yet loaded
	 */
	public int getTabIndexForFile(File f) {
		if (f == null) {
			return -1;
		}
		try {
			for (int i = 0; i < tabbedPane.getTabCount(); i++) {
				String c1 = allTabs.get(i).getFile().toString().toLowerCase();
				String c2 = f.toString().toLowerCase();
				if (c1.equals(c2)) {
					return i;
				}
			}
		} catch (NullPointerException e) {
			logger.debug("Current tab does not have a valid file object.");
			return -1;
		}
		return -1;
	}

	/**
	 * Opens a file selected by a file chooser, or sets the tab visible, which
	 * contains the script
	 */
	public void openFile(boolean viaDialog) {
		File f = null;
		if (viaDialog) {
			// locate the file
			final JFileChooser fc = new JFileChooser();

			File cd = getCurDir();

			fc.setCurrentDirectory(cd);

			fc.setMultiSelectionEnabled(false);
			fc.setAcceptAllFileFilterUsed(true);
			int ans = fc.showOpenDialog(this);

			if (ans != JFileChooser.APPROVE_OPTION) {
				return;
			}

			f = fc.getSelectedFile();
			pm.setProperty("dir", f.getParent());
		}

		// determine whether the selected file is already open
		int idx = getTabIndexForFile(f);

		if (idx != -1) {
			// show the already opened tab
			tabbedPane.setSelectedIndex(idx);
		} else {
			// no tab is open, but we want to decide, whether to load it to an
			// existing empty tab or to a new one

			// get the tab at the current position
			ScriptEditorTab tab = getSelectedTab();

			// check if the tab is empty
			if (tab.isEmpty()) {
				// load the file in the current tab
				tab.setFile(f);
				boolean success = tab.loadFile();
				if (!success) {
					// reset the tab, if loading fails
					tab.reset();
				}
			} else {
				// open a new tab and remember the index
				int i = newTab();
				tab = allTabs.get(i);
				tab.setFile(f);
				boolean success = tab.loadFile();
				if (!success) {
					// remove the tab, if loading fails
					removeTab(allTabs.get(i));
				}
			}
		}
	}

	private File getCurDir() {
		File cd = null;
		try {
			// try to read the property file
			String path = pm.getProperty("dir");
			if (path == null) {
				// take the default IQM script path
				cd = ConfigManager.getCurrentInstance()
						.getDefaultIqmScriptPath();
				cd = ConfigManager.getCurrentInstance().getScriptPath();
			} else {
				cd = new File(path);
			}
		} catch (NullPointerException e) {
			logger.info("No configuration manager loaded, "
					+ "defaulting to current directory.");
		}
		return cd;
	}

	/**
	 * Kicks off the script engine for the selected tab.
	 */
	protected void runScript() {
		final ScriptEditorTab tab = getSelectedTab();

		if (tab.isEmpty())
			return;

		if (tab.isEdited()) {
			// ask for saving
			int ans = DialogUtil.getInstance().showDefaultQuestionMessage(
					I18N.getMessage("script.run.notsavedyet"));

			if (ans != IDialogUtil.YES_OPTION) {
				return;
			}

			if (tab.getFile() == null) {
				saveAs();
			} else {
				save(tab);
			}
		}

		// evaluate the script with the correct context
		this.lblInfo.setText(I18N
				.getGUILabelText("script.editor.lblInfo.scriptRunning"));
		this.lblProcessingIcon.setVisible(true);

		// intercept/copy the output to a text area
		if (!frame.isVisible()) {
			frame.setVisible(true);
		}

		requestFocus();

		PrintStream ps = new PrintStream(osi);
		System.setOut(ps);
		
		// TODO implement stopping the script engine
		executor = new SwingWorker<Void, Void>() {

			@Override
			public Void doInBackground() {
//				enableCancellation();
				CommonTools.disableUserInteraction(ScriptEditor.this);
				disableF5Running();
				GroovyScriptConnector runner = new GroovyScriptConnector();
				runner.runScript(tab.getFile());
				return null;
			}

			@Override
			protected void done() {
				CommonTools.enableUserInteraction(ScriptEditor.this);
				enableF5Running();
//				disableCancellation();
				lblInfo.setText("");
				lblProcessingIcon.setVisible(false);
				System.out.println("\n####### Script ["
						+ tab.getFile().getName() + "] finished. #######\n");
				System.setOut(stdOut);
			}
		};

		WaitingDialog dlg = new WaitingDialog("Script is executing", false);
		CompletionWaiter cw = new CompletionWaiter(dlg);
		executor.addPropertyChangeListener(cw);
		executor.execute();
		dlg.setVisible(true);
	}

	/**
	 * Don't let the user cancel the script using Ctrl+C. 
	 */
	protected void disableCancellation() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
		getRootPane().getActionMap().remove("cancelscript");
	}

	/**
	 * Let the user cancel the script using Ctrl+C.
	 */
	protected void enableCancellation() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK),
				"cancelscript");
		getRootPane().getActionMap().put("cancelscript", new AbstractAction() {

			private static final long serialVersionUID = -5477425838778337075L;

			public void actionPerformed(ActionEvent e) {
				if (executor != null && !executor.isDone()) {
					// TODO implement stopping scripting engine
					executor.cancel(true);
				}
			}
		});
	}

	/**
	 * Closes the current file and waits for edits
	 */
	public void closeFile() {
		// get the tab object to close
		ScriptEditorTab tab = allTabs.get(tabbedPane.getSelectedIndex());

		// check, if the tab has been modified
		if (tab.isEdited()) {
			logger.info("File in tab is edited and not saved yet.");
			int ans = DialogUtil.getInstance().showDefaultQuestionMessage(
					I18N.getMessage("script.close.notsavedyet"));

			if (ans == IDialogUtil.YES_OPTION) {
				save(tab);
			} else if (ans == IDialogUtil.CANCEL_OPTION) {
				return;
			} // else discard the changes
		}

		// remove the tab
		removeTab(tab);

		logger.info("Script file closed.");
	}

	private void save(ScriptEditorTab tab) {
		lblInfo.setText(I18N.getGUILabelText(
				"script.editor.lblInfo.scriptSaving", tab.getFile().getName()));

		// get the selected tab and save the file
		boolean success = tab.saveFile();

		if (!success) {
			// if not successful
			DialogUtil.getInstance().showDefaultErrorMessage(
					I18N.getMessage("script.file.notSaved"));
			mntmSave.setEnabled(true);
		} else {
			mntmSave.setEnabled(false);
		}
		lblInfo.setText("");
	}

	/**
	 * Choose a location and save the file as new one in the same tab.
	 */
	private void saveAs() {
		final JFileChooser fc = new JFileChooser();

		File cd = getCurDir();

		fc.setCurrentDirectory(cd);
		FileNameExtensionFilter allFormats = new FileNameExtensionFilter(
				IQMConstants.GROOVY_FILTER_DESCRIPTION,
				IQMConstants.GY_EXTENSION, IQMConstants.GVY_EXTENSION,
				IQMConstants.GROOVY_EXTENSION, IQMConstants.GSH_EXTENSION);
		fc.addChoosableFileFilter(allFormats);
		fc.setFileFilter(allFormats);
		fc.setMultiSelectionEnabled(false);
		fc.setAcceptAllFileFilterUsed(true);
		int ans = fc.showSaveDialog(this);

		if (ans != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File target = fc.getSelectedFile();

		int pt = target.getName().toString().lastIndexOf(".");

		// append standard extension "groovy"
		String ext = (pt == -1) ? "" : target.getName().toString()
				.substring(pt);
		if (ext.isEmpty() || !ext.equals(IQMConstants.GY_EXTENSION)
				|| !ext.equals(IQMConstants.GVY_EXTENSION)
				|| !ext.equals(IQMConstants.GROOVY_EXTENSION)
				|| !ext.equals(IQMConstants.GSH_EXTENSION)) {
			ext = ".groovy";
			target = new File(target.toString().concat(ext));
		}

		if (target.exists()) {
			int ow = DialogUtil.getInstance().showDefaultWarnMessage(
					I18N.getMessage("application.fileExists.overwrite"));
			if (ow != IDialogUtil.YES_OPTION) {
				return;
			}
		}

		pm.setProperty("dir", target.getParent());

		// save as new file and switch to new file
		ScriptEditorTab tab = getSelectedTab();
		tab.saveAsAndSwitch(target);
	}

	private void setInitialMenuState() {
		// enable the file menu
		mntmSave.setEnabled(false);
	}

	/**
	 * Gets the current instance of the script editor.
	 * 
	 * @return the singleton instance
	 */
	public static ScriptEditor getInstance() {
		if (currentInstance == null) {
			currentInstance = new ScriptEditor();
			Application.setScriptEditor(currentInstance);
		}
		return currentInstance;
	}

	private void handleUndoAction() {
		getSelectedTab().getTextArea().undoLastAction();
	}

	private void handleRedoAction() {
		getSelectedTab().getTextArea().redoLastAction();
	}

	/**
	 * Opens a script file in a new editor tab.
	 * 
	 * @param f
	 *            the file to open
	 */
	public void openInNewTab(File f) {
		// add a new tab to the editor
		// load a file to the tab
		ScriptEditorTab tab = getSelectedTab();
		if (!tab.isEmpty()) {
			newTab();
			tab = getSelectedTab();
		}

		tab.setFile(f);
		tab.loadFile();
	}

	/**
	 * Add a new tab.
	 * 
	 * @return the index of the newly constructed tab.
	 */
	public int newTab() {
		// add a new tab to the editor
		ScriptEditorTab tab = new ScriptEditorTab(this);
		tabbedPane.addTab(tab.getCaption(), tab);
		int idx = tabbedPane.getTabCount() - 1;
		tabbedPane.setSelectedIndex(idx);
		allTabs.add(tab);
		tab.reset();
		return idx;
	}

	/**
	 * Removes the specified tab object.
	 * 
	 * @param tab
	 *            the tab to be removed
	 * @return the integer of the selected tab after removal
	 */
	public int removeTab(ScriptEditorTab tab) {
		// control the case when just one tab is left
		if (allTabs.size() == 1) {
			// add a new tab and erase the specified one
			newTab();
		}

		int idx = allTabs.indexOf(tab);
		tabbedPane.remove(idx);
		allTabs.remove(idx);

		// set the new index to the next one
		idx = (idx == 0) ? idx : idx - 1;
		tabbedPane.setSelectedIndex(idx);
		return idx;
	}

	/**
	 * Returns the currently selected tab.
	 * 
	 * @return the {@link ScriptEditorTab}
	 */
	public ScriptEditorTab getSelectedTab() {
		return allTabs.get(tabbedPane.getSelectedIndex());
	}

	/**
	 * Saves all open tabs either to their specified files or to a new file, if
	 * they contain any content
	 */
	public void saveAll() {
		for (ScriptEditorTab tab : allTabs) {
			if (!tab.isEmpty()) {
				if (tab.getFile() == null) {
					saveAs();
				} else {
					save(getSelectedTab());
				}
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("caption")) {
			try {
				ScriptEditorTab tab = ((ScriptEditorTab) evt.getSource());
				String title = (String) evt.getNewValue();
				int selIdx = tabbedPane.getSelectedIndex();

				// change the caption of the currently selected tab
				tabbedPane.setTitleAt(selIdx, title);
				tabbedPane.setToolTipTextAt(selIdx,
						(tab.getFile() == null) ? null : tab.getFile()
								.getPath());

				// set the menu states
				if (title.endsWith("*") && tab.isEdited()) {
					validateMenuState();
				}

			} catch (Exception e) {
				logger.error("No tab available yet: " + e);
			}
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		try {
			ScriptEditorTab curTab = getSelectedTab();
			curTab.getTextArea().requestFocusInWindow();
		} catch (NullPointerException npe) {
			logger.error("", npe);
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		boolean toSave = false;
		// check if any file is marked as edited
		for (ScriptEditorTab tab : allTabs) {
			if (tab.isEdited()) {
				toSave = true;
			}
		}
		if (toSave) {
			// ask if all tabs should be saved
			int ans = DialogUtil.getInstance().showDefaultQuestionMessage(
					I18N.getMessage("script.editor.saveallfiles"));

			if (ans == IDialogUtil.YES_OPTION) {
				saveAll();
			}
		}
		tabbedPane.removeAll();
		allTabs.clear();
		newTab();
		this.setInitialMenuState();
		this.dispose();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

}
