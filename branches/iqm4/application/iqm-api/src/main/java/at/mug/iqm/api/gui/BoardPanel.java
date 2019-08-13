package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: BoardPanel.java
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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;

/**
 * This class prints out a log for the user.
 *
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2012 02 24 new "Iqm2" layout
 * 
 * <li> 2012 02 24 adapted from Iqm1 JFrame version
 * <li> 2012 03 27 PK: removed unnecessary code fragments 
 */
public class BoardPanel extends JPanel implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -536789050426854287L;

	// Standard class logger
	private static final Logger logger = LogManager.getLogger(BoardPanel.class);
	
	// Initialize class variables first
	private static       JTextArea    textArea 		= new JTextArea();
    private final static String       newline  		= "\n"; 
    private static       JPopupMenu   popup    		= new JPopupMenu();
	private static       JPanel 	  boardJPanel 	= new JPanel();
    
	/**
	 * Standard constructor. 
	 */
    public BoardPanel() {
        super();
        this.createAndShowGUI();                              
    }
    
    /**
     * Append one line to the JTextArea.
     * @param arg
     */
    public static synchronized void appendTextln (String arg) {
    	textArea.append(arg + newline);
    	logger.debug(arg);
    	System.out.println(arg);
    	
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    /**
     * Appends one line to the JTextArea, including leading unqualified name of the caller class.
     * @param arg
     */
    public static synchronized void appendTextln (String arg, Class<?> caller) {
    	String clsName = caller.getName();
    	textArea.append(clsName.substring(clsName.lastIndexOf('.')+1) + ": " + arg + newline);
    	logger.debug(clsName.substring(clsName.lastIndexOf('.')+1) + ": " + arg);
    	System.out.println(arg);
    	
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    /**
     * Append text to the JTextArea.
     * @param arg
     */
    public static synchronized void appendText(String arg) {
    	textArea.append(arg);
    	logger.debug(arg);
    	System.out.print(arg);
    	
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    /**
     * Append text to the JTextArea, including leading unqualified name of the caller class.
     * @param arg
     */
    public static synchronized void appendText(String arg, Class<?> caller) {
    	String clsName = caller.getName();
    	textArea.append(clsName.substring(clsName.lastIndexOf('.')+1) + ": " + arg);
    	logger.debug(clsName.substring(clsName.lastIndexOf('.')+1) + ": " + arg);
    	System.out.print(arg);
    	
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    public void setJTextArea(JTextArea arg){
    	textArea = arg;
    }
    
    public JTextArea getJTextArea(){
    	return textArea;
    }
    
    /**
     * Create the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    public void createAndShowGUI() {
        textArea.setEditable(false);
   
        //popup
        JMenuItem menuItem = new JMenuItem(I18N.getGUILabelText("board.popup.clear"));
        menuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.popup.board.clear")));
        menuItem.setActionCommand("clear");
        menuItem.addActionListener(this);
        popup.add(menuItem);
                      
        //Add operatorProgressListener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        textArea.addMouseListener(popupListener);

        this.setLayout(new BorderLayout());
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        
        this.add(textArea, BorderLayout.CENTER);
    }
    
	/**
	 * This method destroys the GUI
	*/
	public static void destroyGUI() {
		boardJPanel.setVisible(false);
		boardJPanel = null;	
	}	
    
	/**
	 * This method contains all logic for specific actions set in this class.
	 */
    @Override
	public void actionPerformed(ActionEvent e) {
    	if ("clear".equals(e.getActionCommand())) {
			String s = textArea.getText();
	        int end = s.length();
	        textArea.replaceRange("", 0, end);
	        logger.info("Board has been manually cleared.");
		}
	}
    
    
    /**
     * In-line class for the right click on the JTextarea.
     */
    class PopupListener extends MouseAdapter {
        @Override
		public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
		public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
    

