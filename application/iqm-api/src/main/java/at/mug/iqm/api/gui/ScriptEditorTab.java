package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ScriptEditorTab.java
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
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rtextarea.RTextScrollPane;

import at.mug.iqm.api.I18N;
import at.mug.iqm.commons.io.PlainTextFileReader;
import at.mug.iqm.commons.io.PlainTextFileWriter;
import at.mug.iqm.commons.util.CursorFactory;
import at.mug.iqm.commons.util.DialogUtil;

/**
 * This class represents a single tab in the script editor.
 * 
 * @author Philipp Kainz
 * 
 */
public class ScriptEditorTab extends JPanel implements DocumentListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 861550411646659217L;
	private boolean edited = false;
	private GroovyTextArea textArea;
	private File file = null;
	private String caption;
	private ScriptEditor editor;
	private RTextScrollPane sp;

	/**
	 * Create a new tab for a given {@link ScriptEditor}.
	 * 
	 * @param editor
	 */
	public ScriptEditorTab(ScriptEditor editor) {
		super();
		this.editor = editor;
		textArea = new GroovyTextArea();
		textArea.setRows(20);
		textArea.setColumns(75);
		textArea.setAntiAliasingEnabled(true);
		textArea.setAutoIndentEnabled(true);
		textArea.setCodeFoldingEnabled(true);

		textArea.getDocument().addDocumentListener(this);

		sp = new RTextScrollPane(textArea);
		sp.setFoldIndicatorEnabled(true);

		// add auto-completion
		CompletionProvider provider = createCompletionProvider();
		AutoCompletion ac = new AutoCompletion(provider);
		ac.install(textArea);

		setLayout(new BorderLayout(0, 0));
		add(sp, BorderLayout.CENTER);
	}

	private CompletionProvider createCompletionProvider() {
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		provider.addCompletion(new ShorthandCompletion(provider, "sysout",
				"System.out.println()", "System.out.println()"));
		provider.addCompletion(new ShorthandCompletion(provider, "syserr",
				"System.err.println()", "System.err.println()"));

		// TODO continue implementing autocomplete
		// Method[] ms = getClass().getDeclaredMethods();
		// for (Method m : ms) {
		// FunctionCompletion fc = new FunctionCompletion(provider, m.getName(),
		// m.getGenericReturnType().toString());
		// System.out.println("Declaring Class: " +
		// m.getDeclaringClass().getSimpleName().toString());
		//
		// provider.addCompletion(fc);
		// }

		return provider;
	}

	private void initialize() {
		textArea.getDocument().removeDocumentListener(this);
		textArea.setText(null);
		textArea.discardAllEdits();
		textArea.getDocument().addDocumentListener(this);

		// just add it once!
		removePropertyChangeListener("caption", editor);
		addPropertyChangeListener("caption", editor);

		setCaption(I18N.getGUILabelText("script.editor.tab.new.text"));
	}

	/**
	 * Returns a flag whether or not the tab is edited.
	 * 
	 * @return <code>true</code> if changes have been made since last save,
	 *         <code>false</code> otherwise
	 */
	public boolean isEdited() {
		return edited;
	}

	private void setEdited(boolean edited) {
		// if it already was edited and edited is set to true, don't do anything
		if (this.edited && edited) {
			return;
		}
		String t = getCaption();
		String cap = "";
		if (this.edited && !edited) {
			cap = t.substring(0, t.length() - 1);
		}
		if (!this.edited && edited) {
			cap = t + "*";
		}
		this.edited = edited;
		// put this to the end because of event firing
		setCaption(cap);
	}

	/**
	 * Get the text area.
	 * 
	 * @return the {@link GroovyTextArea}
	 */
	public GroovyTextArea getTextArea() {
		return textArea;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		setEdited(true);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		setEdited(true);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		setEdited(true);
	}

	/**
	 * Get the caption of this tab.
	 * 
	 * @return the string caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Set the caption of the tab.
	 * 
	 * @param caption
	 */
	public void setCaption(String caption) {
		firePropertyChange("caption", (String) this.caption, (String) caption);
		this.caption = caption;
	}

	/**
	 * Get the file object of this tab.
	 * 
	 * @return a {@link File}, or <code>null</code> if the tab is not (yet)
	 *         associated with a file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Set the file object associated with the tab.
	 * 
	 * @param file
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Indicates whether or not the tab is empty.
	 * 
	 * @return <code>true</code> if no file is loaded, <code>false</code>
	 *         otherwise
	 */
	public boolean isEmpty() {
		boolean emptyFile = (file == null);
		boolean notEdited = (!edited);
		boolean emptyText = ((textArea.getText().equals("")) ? true : false);
		return (emptyFile && notEdited && emptyText);
	}

	/**
	 * Loads the file to the tab.
	 * 
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean loadFile() {
		try {
			PlainTextFileReader ptf = new PlainTextFileReader(file);

			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<String> content = executor.submit(ptf);

			String s = content.get();
			if (s == null) {
				DialogUtil.getInstance().showDefaultErrorMessage(
						I18N.getMessage("application.onlyplaintextallowed"));
				return false;
			}

			textArea.getDocument().removeDocumentListener(this);
			textArea.setText(s);
			textArea.getDocument().addDocumentListener(this);
			textArea.setCaretPosition(0);

			// if the file is freshly loaded set edited to false
			setEdited(false);

			// set the caption
			setCaption(file.getName());

			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Saves the currently edited file by overwriting the content.
	 * 
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean saveFile() {
		try {
			// overwrite file
			PlainTextFileWriter writer = new PlainTextFileWriter(file,
					textArea.getText());
			Thread t = new Thread(writer);
			t.start();
			setCursor(CursorFactory.getWaitCursor());
			t.join();
			setEdited(false);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			setEdited(true);
			return false;
		} finally {
			setCursor(CursorFactory.getDefaultCursor());
		}
	}

	/**
	 * Resets the entire tab to original state.
	 */
	public void reset() {
		this.file = null;
		this.edited = false;
		this.initialize();
	}

	/**
	 * Saves the file and switches to the new file.
	 * 
	 * @param target
	 */
	public void saveAsAndSwitch(File target) {
		setFile(target);
		saveFile();
		loadFile();
	}
}
