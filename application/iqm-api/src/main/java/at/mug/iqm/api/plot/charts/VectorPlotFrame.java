package at.mug.iqm.api.plot.charts;

/*
 * #%L
 * Project: IQM - API
 * File: VectorPlotFrame.java
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


import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.Raster;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import at.mug.iqm.api.Resources;
import at.mug.iqm.commons.util.CommonTools;

/**
 * This method shows a vector plot in an extra window. This class uses
 * JFreeChart: a free chart library for the Java(tm) platform
 * http://www.jfree.org/jfreechart/
 * 
 * @author Helmut Ahammer
 * @since 2010 05
 */
public class VectorPlotFrame extends JFrame implements WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1488224441719569966L;

	/**
	 * Construct a new <code>VectorPlot</code> from a given {@link Raster}.
	 * 
	 * @param r
	 * @param frameTitle
	 * @param plotTitle
	 */
	public VectorPlotFrame(Raster r, String frameTitle, String plotTitle) {
		super(frameTitle);
		VectorPlot vp = new VectorPlot(r, plotTitle);

		this.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.application.magenta.32x32")).getImage());

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this); // for exit
		this.setContentPane(vp);
		this.setAlwaysOnTop(false);

		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(0); // ms
		ttm.setReshowDelay(10000);
		ttm.setDismissDelay(5000);
	}

	/**
	 * This method destroys the jFrame It resets some ToolTip parameters
	 */
	private void closeAndDestroyFrame() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(CommonTools.getDefaultToolTipInitDelay()); // ms
		ttm.setReshowDelay(CommonTools.getDefaultToolTipReshowDelay());
		ttm.setDismissDelay(CommonTools.getDefaultToolTipDismissDelay());
		this.setVisible(false);
		this.dispose();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		try {
			this.closeAndDestroyFrame();
		} catch (Exception e) {
			e.printStackTrace();
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
}
