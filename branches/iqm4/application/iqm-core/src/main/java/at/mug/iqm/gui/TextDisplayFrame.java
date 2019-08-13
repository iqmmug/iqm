package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: TextDisplayFrame.java
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
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;

/**
 * This frame is able to display plain text from a string.
 * 
 * @author Philipp Kainz
 */
public class TextDisplayFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -985683849398150097L;
	
	// class specific logger
	private static final Logger logger = LogManager.getLogger(TextDisplayFrame.class);
	
	/**
	 * The panel containing the {@link JTextArea} and the text.
	 */
    private TextPanel textPanel;
	
	/**
	 * This is the default constructor.
	 */
    public TextDisplayFrame(TextPanel tp) {
        super();
        logger.debug("Constructing new instance...");
        this.textPanel = tp;
        this.createAndAssemble();                              
    }
    
    /**
     * Creates the GUI. 
     */
    public void createAndAssemble() {
    	
    	this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    	this.setTitle(I18N.getGUILabelText("textDisplay.frame.title"));
    	this.setIconImage(new ImageIcon(Resources.getImageURL("icon.application.grey.32x32")).getImage());
		Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
		int width  = (int) dimScreen.getWidth()/2;
		int height = (int) dimScreen.getHeight()/2;
		this.setPreferredSize(new Dimension(width, height));
		this.setLocation(width/2, height/2);

        this.getContentPane().add(this.textPanel, BorderLayout.CENTER);
        
        this.pack();
    }
    
}


