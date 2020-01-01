package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: MultiResultDialog.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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


import java.awt.EventQueue;

import javax.swing.JDialog;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.CommonTools;

/**
 * Display multi-dimensional result objects.
 * 
 * @author Philipp Kainz
 * @since 3.0.1
 *
 */
public class MultiResultDialog extends JDialog {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 9072178269342785624L;

	private Result result;
	private MultiResultSelectorPanel selectorPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MultiResultDialog dialog = new MultiResultDialog();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public MultiResultDialog() {
		setTitle(I18N.getGUILabelText("multiresult.dialog.title"));
		setAlwaysOnTop(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		pack();
	}

	/**
	 * A dialog with the {@link Result} object.
	 * 
	 * @param result
	 */
	public MultiResultDialog(Result result){
		this();
		this.result = result;
		update();
		CommonTools.centerFrameOnScreen(this);
	}
	
	/**
	 * Updates the dialog.
	 */
	public void update(){
		selectorPanel = new MultiResultSelectorPanel(result);
		this.setContentPane(selectorPanel);
		validate();
		pack();
	}
	
	/**
	 * Gets the instance of {@link MultiResultSelectorPanel} displayed in this
	 * dialog.
	 * 
	 * @return the selector panel
	 */
	public MultiResultSelectorPanel getSelectorPanel() {
		return this.selectorPanel;
	}
	
	/**
	 * Gets the result object.
	 * 
	 * @return the result object
	 */
	public Result getResult() {
		return result;
	}
	
	/**
	 * Set the result for display.
	 * 
	 * @param result
	 */
	public void setResult(Result result) {
		this.result = result;
	}
}
