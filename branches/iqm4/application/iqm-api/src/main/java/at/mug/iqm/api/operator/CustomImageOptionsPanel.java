package at.mug.iqm.api.operator;

/*
 * #%L
 * Project: IQM - API
 * File: CustomImageOptionsPanel.java
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


import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * This panel represents custom options for all image operator GUIs.
 * 
 * @author Philipp Kainz
 *
 */
public class CustomImageOptionsPanel extends JPanel{
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 6662519819761136957L;
	private JCheckBox chckbxInvertOutputImage;
	private JCheckBox chckbxAutoPreview;

	/**
	 * Create the panel.
	 */
	public CustomImageOptionsPanel() {
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setAlignment(FlowLayout.TRAILING);
		
		chckbxAutoPreview = new JCheckBox("Auto preview");
		chckbxAutoPreview.setMnemonic('A');
		
		add(chckbxAutoPreview);
		
		chckbxInvertOutputImage = new JCheckBox("Invert output image");
		chckbxInvertOutputImage.setMnemonic('I');
		
		add(chckbxInvertOutputImage);

	}

	public JCheckBox getChckbxInvertOutputImage() {
		return chckbxInvertOutputImage;
	}
	public JCheckBox getChckbxAutoPreview() {
		return chckbxAutoPreview;
	}

}
