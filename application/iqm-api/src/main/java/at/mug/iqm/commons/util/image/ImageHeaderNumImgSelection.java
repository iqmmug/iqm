package at.mug.iqm.commons.util.image;

/*
 * #%L
 * Project: IQM - API
 * File: ImageHeaderNumImgSelection.java
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


import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Helmut Ahammer
 * @since   2009 09
 */
public class ImageHeaderNumImgSelection {
	private int maxValue;

	public ImageHeaderNumImgSelection(int maxValue){
		this.maxValue = maxValue;
	}

	public int getSelectedValue() {
		JFrame frame = new JFrame();

		frame.setAlwaysOnTop(true);
		JOptionPane optionPane = new JOptionPane();
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		//optionPane.setOptionType(JOptionPane.OK_OPTION);
		JSpinner jSpinner = getJSpinner(optionPane);
		optionPane.setMessage(new Object[] { "Image Number: ", jSpinner });
		optionPane.setInputValue(1);
		JDialog dialog = optionPane.createDialog(frame, "Choose Value");
		frame.pack();
		dialog.setVisible(true);
		System.out.println("Selected: " + optionPane.getInputValue());
		Object ob = optionPane.getInputValue(); 
		int result = -1; 
		if ( ob instanceof Integer){
			result = ((Integer)(ob)).intValue();
		}	    
		return result;
	}


	private JSpinner getJSpinner(final JOptionPane optionPane) {
		JFormattedTextField ftf = null;
		SpinnerModel sModel = new SpinnerNumberModel(1, 1, this.maxValue, 1); //init, min, max, step 
		JSpinner jSpinner = new JSpinner(sModel);
		//Set the formatted text field.
		ftf = getTextField(jSpinner);
		if (ftf != null ) {
			ftf.setColumns(5); 
			ftf.setHorizontalAlignment(SwingConstants.CENTER);	
			ftf.setPreferredSize(new Dimension(20,20));
			ftf.setEditable(false);
		}
		//add ChangeListener
		ChangeListener changeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				JSpinner theSpinner = (JSpinner) changeEvent.getSource();
				//if (!theSpinner.getValueIsAdjusting()) {
				optionPane.setInputValue(((Number)theSpinner.getValue()).intValue());
				//}
			}
		};
		jSpinner.addChangeListener(changeListener);
		return jSpinner;
	}
	/**
	 * Return the formatted text field used by the editor, or
	 * null if the editor doesn't descend from JSpinner.DefaultEditor.
	 */
	public JFormattedTextField getTextField(JSpinner spinner) {
		JComponent editor = spinner.getEditor();
		if (editor instanceof JSpinner.DefaultEditor) {
			return ((JSpinner.DefaultEditor)editor).getTextField();
		} else {
			System.err.println("Unexpected editor type: "
					+ spinner.getEditor().getClass()
					+ " isn't a descendant of DefaultEditor");
			return null;
		}
	}


}//END


