package at.mug.iqm.api.gui.util;

/*
 * #%L
 * Project: IQM - API
 * File: IqmSlideShow.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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


import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.Resources;

/**
 * This class starts an extra slide show window
 * @author Helmut Ahammer, Philipp Kainz
 * @since  2012 02 29
 */
public class IqmSlideShow extends JFrame implements ActionListener, WindowListener, ChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 69330638302674941L;
	
	// class specific logger
	private static final Logger logger = Logger.getLogger(IqmSlideShow.class);
	
	// variable declaration
	private SlideShow  slideSh;  //  @jve:decl-index=0:
	private boolean    threadSuspended;
	private boolean    threadStopped;

	private JButton jButtonPlay;
	private JButton jButtonStop;
	private JButton jButtonPause;

    private JLabel     jLabelPlayDelay; 
	private JPanel     panelPlayDelay;
	private JSlider jSliderPlayDelay;
	

	public IqmSlideShow() {
		  
		  super();
		  logger.debug("Creating a new instance of '" + this.getClass().getName() +
		  		"'.");
		  
		  this.setTitle("Slide Show");
		  
		  // initialize
		  this.slideSh = new SlideShow();
		  this.threadSuspended = false;
		  this.threadStopped = false;
		  
		  this.jButtonPlay = new JButton();
		  this.jButtonStop = new JButton();
		  this.jButtonPause = new JButton();
		  
		  this.jLabelPlayDelay = new JLabel();
		  this.panelPlayDelay = new JPanel();
		  this.jSliderPlayDelay = new JSlider();
		  
		  //this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);  //see Windows events
		  this.addWindowListener(this);

		  this.setIconImage(new ImageIcon(Resources.getImageURL("icon.application.magenta.32x32")).getImage());
		
		  // set the layout
		  this.setLayout(new FlowLayout());  
		  this.getContentPane().add(this.createJButtonPlay());
	      this.getContentPane().add(this.createJButtonPause());
	      this.getContentPane().add(this.createJButtonStop());
	      this.getContentPane().add(this.createJLabelPlayDelay());
	      this.getContentPane().add(this.createJSliderPlayDelay());
	      
	      logger.debug("IqmSlideShow constructed, will now be set to visible.");      
	}
	
	/**
	 * This method packs and displays the slide show frame 'always on top'.
	 */
	public void showIqmSlideShow(){
		 this.setAlwaysOnTop(true);
	     this.pack();
	     this.setResizable(false);
	     this.setVisible(true);
	}
	
	  /**
     * This class performs a slide show
     * @author Helmut Ahammer
     *
     */
	class SlideShow extends Thread{
	
		//private Thread Slide;	
		/**
         * Stops the slideshow.
         */
        public synchronized void cancel(){
        	threadSuspended = true;
        	threadStopped   = true;
        	//interrupt();
        	slideSh = null;
        	logger.debug(this.getName() + " has been stopped.");
        }
       
        /**
         * Starts the Slideshow
         */
        @Override
		public void run(){
        	this.setName("SlideShowThread");
        	threadSuspended = false;
        	int max = 0;
        	// determine the max slide show elements
           	if (Application.getManager().isLeftListActive())  
           		max = Application.getManager().getManagerModelLeft().getSize();
           	if (Application.getManager().isRightListActive()) 
           		max = Application.getManager().getManagerModelRight().getSize();

            while(!threadStopped){          
        		logger.debug("Slideshow is running");
            	for (int i=0; i<max; i++){
                   	try{
                   		long delay = jSliderPlayDelay.getValue();
                   		long del;
                   		if (delay <= 0.5) {
                   			del = (long)((float) 0.5 * 1000);
                   		}
                   		else{
                   			del = delay * 1000;
                   		}                 		
                   		Application.getManager().displayItem(i);
    	        		sleep(del);
    	        		                  	
    	        		synchronized(this) {
                            while (threadSuspended){
                                wait();  
                            }
                        }
    	        	}
                   	catch(InterruptedException e){
                   		logger.error("An error occurred: ", e);
                   	}
            	}   	
            }    
        }
	}
	
	/**
	 * This method initializes jButtonPlay	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton createJButtonPlay() {
		this.jButtonPlay.setText("Play");
		this.jButtonPlay.addActionListener(this);
		this.jButtonPlay.setActionCommand("play");
		return this.jButtonPlay;
	}
	
	/**
	 * This method initializes jButtonPause	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton createJButtonPause() {
		this.jButtonPause.setText("Pause/Resume");
		this.jButtonPause.addActionListener(this);
		this.jButtonPause.setActionCommand("pause");
		return jButtonPause;
	}
	
	/**
	 * This method initializes jButtonStop	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton createJButtonStop() {
		this.jButtonStop.setText("Stop");
		this.jButtonStop.addActionListener(this);
		this.jButtonStop.setActionCommand("stop");
		return this.jButtonStop;
	}
	
	private JPanel createJSliderPlayDelay() {
		this.panelPlayDelay.setLayout(new BorderLayout());
		this.panelPlayDelay.setMinimumSize(new Dimension(100,18));			
		this.panelPlayDelay.setPreferredSize(new Dimension(100,18));			
		this.jSliderPlayDelay = new JSlider(Adjustable.HORIZONTAL);
		this.jSliderPlayDelay.setMinimum(0);
		this.jSliderPlayDelay.setMaximum(5);
		this.jSliderPlayDelay.setValue(1);
		
		int delay = jSliderPlayDelay.getValue();
		
	    this.jLabelPlayDelay.setText("Delay: " + delay+ " [s] ");
	    this.jSliderPlayDelay.setToolTipText("Value for the play delay");
	    this.jSliderPlayDelay.addChangeListener(this);	
	    this.panelPlayDelay.add(jSliderPlayDelay);
		return this.panelPlayDelay;
	}
	
	/**
	 * @return the jLabelPlayDelay
	 */
	public JLabel createJLabelPlayDelay() {
		this.jLabelPlayDelay.setText("Delay:");
		return this.jLabelPlayDelay;
	}

	/**
	 * @param jLabelPlayDelay the jLabelPlayDelay to set
	 */
	public void setjLabelPlayDelay(JLabel jLabelPlayDelay) {
		this.jLabelPlayDelay = jLabelPlayDelay;
	}
	/**
	 * @return the panelPlayDelay
	 */
	public JPanel getPanelPlayDelay() {
		return this.panelPlayDelay;
	}

	/**
	 * @param panelPlayDelay the panelPlayDelay to set
	 */
	public void setPanelPlayDelay(JPanel panelPlayDelay) {
		this.panelPlayDelay = panelPlayDelay;
	}


	/**
	 * This method sets and performs the corresponding actions to the inputs.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("play".equals(e.getActionCommand())) {
			logger.info("Starting to play");
			this.slideSh = new SlideShow();
			this.threadSuspended = false;
			this.threadStopped   = false;
			this.slideSh.start();
		}
		if ("pause".equals(e.getActionCommand())) {
			//System.out.println("State of thread:"+slideSh.getState());
			if (this.slideSh != null){				
				threadSuspended = !threadSuspended;
		        if (!threadSuspended){
                  	synchronized(this.slideSh) {
                  	logger.info("Restarting to play");
		            slideSh.notify();
                  	}
		        }
		        else{
		        	logger.info("Pause playing");
		        }
			}
		}
		if ("stop".equals(e.getActionCommand())) {
			logger.info("Stopping playing");
			if (this.slideSh != null){
				this.slideSh.cancel();
			}
		}
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		logger.info("Slideshow canceled");
		if (this.slideSh != null){
			this.slideSh.cancel();
		}
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
	}


	/**
	 * This method reacts on JSlider changes 	
	 * The new Play Delay or process delay or image number is set
	 */
	@Override
	public void stateChanged(ChangeEvent e) {

		Object obE = e.getSource();
		if (obE instanceof JSlider){
			if (obE == this.jSliderPlayDelay){
				int delay =  this.jSliderPlayDelay.getValue();
				this.jLabelPlayDelay.setText("Delay: " + delay + " [s] "); 
				logger.debug("Updating JLabelPlayDelay to " + delay + "s");
			}
		
		}
	}
}
