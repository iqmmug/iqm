package at.mug.iqm.api.plot.charts;

/*
 * #%L
 * Project: IQM - API
 * File: HistogramPlotFrame.java
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


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.jai.Histogram;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import at.mug.iqm.api.Resources;
import at.mug.iqm.commons.util.CommonTools;

/**
 * This method shows the histogram of an image in an extra window. 
 * This class uses JFreeChart : a free chart library for the Java(tm) platform
 * http://www.jfree.org/jfreechart/
 * 
 * @author Helmut Ahammer
 * @since   2009 05
 */
public class HistogramPlotFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4731889208242716034L;
	
    /**
     * This class displays the JAI histogram
     *
     * @param histo JAI histogram
     * @param frameTitle  the frame title
     * @param histoTitle  the histogram title
     */
    public HistogramPlotFrame(Histogram histo, String frameTitle, String histoTitle) {
        super(frameTitle);
        HistogramChart chartPanel = new HistogramChart(histo, histoTitle);

		this.setIconImage(new ImageIcon(Resources.getImageURL("icon.application.magenta.32x32")).getImage());
		
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setContentPane(chartPanel); 
        this.setAlwaysOnTop(false);
        
        this.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		super.windowClosing(e);
        		closeAndDestroyFrame();
        	}
		});
        
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setInitialDelay(0);  //ms
        ttm.setReshowDelay(10000);
        ttm.setDismissDelay(5000);
    }
    
    /**
     * This method destroys the jFrame
     * It resets some ToolTip parameters
     */
	private void closeAndDestroyFrame(){
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setInitialDelay(CommonTools.getDefaultToolTipInitDelay());  //ms
        ttm.setReshowDelay(CommonTools.getDefaultToolTipReshowDelay());
        ttm.setDismissDelay(CommonTools.getDefaultToolTipDismissDelay());
		this.setVisible(false);
		this.dispose();
	}
}

