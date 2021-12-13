package at.mug.iqm.gui.tilecachetool;

/*
 * #%L
 * Project: IQM - Application Core
 * File: TCInfo.java
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
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.media.jai.util.SunTileCache;

/**
 * <p>Title: Tile Cache Monitoring Tool</p>
 * <p>Description: Monitors and displays JAI Tile Cache activity.</p>
 * <p>Copyright: Copyright (c) 2002, 2010</p>
 * <p>    All Rights Reserved</p>
 * <p>Company: Virtual Visions Software, Inc.</p>
 *
 * @author Dennis Sigel
 * @version 1.02
 */
public final class TCInfo extends JPanel
                          implements ChangeListener {

    private static final long serialVersionUID = 1L;
    private JSlider memoryCapacitySlider;
    private JSlider memoryThresholdSlider;
    private JLabel  memoryCapacityLabel;
    private JLabel  memoryThresholdLabel;

    private SunTileCache cache = null;
    private Statistics statistics;

    private float memoryUsage;
    private float memoryCapacity;
    private int percentTCM;

    private static final Color BACKGROUND = new Color(180, 180, 220);
    private static final Color LIGHT_BLUE = new Color(200, 200, 230);
    private static final BevelBorder BEVEL_BORDER = new BevelBorder(BevelBorder.LOWERED);


    /**
     * Default constructor
     */
    public TCInfo() {
        setLayout( new FlowLayout(FlowLayout.LEFT, 10, 1));

        // controller for tile cache memory capacity
        JPanel p1 = new JPanel();
        p1.setLayout( new BorderLayout() );
        p1.setBackground(LIGHT_BLUE);

        // use initial arbitrary values (adjusted later)
        memoryCapacityLabel = new JLabel("16 MB");
        memoryCapacityLabel.setBackground(LIGHT_BLUE);
        memoryCapacityLabel.setForeground(Color.black);
        p1.add(memoryCapacityLabel, BorderLayout.NORTH);

        memoryCapacitySlider = new JSlider(SwingConstants.VERTICAL, 0, 128, 16);

        memoryCapacitySlider.setBorder(BEVEL_BORDER);
        memoryCapacitySlider.createStandardLabels(64, 0);
        memoryCapacitySlider.setBackground(BACKGROUND);
        memoryCapacitySlider.setForeground(Color.black);
        memoryCapacitySlider.setPaintLabels(true);
        memoryCapacitySlider.setPaintTicks(true);
        memoryCapacitySlider.setMajorTickSpacing(32);
        memoryCapacitySlider.setMinorTickSpacing(8);
        memoryCapacitySlider.setSnapToTicks(false);
        memoryCapacitySlider.addChangeListener(this);

        p1.add(memoryCapacitySlider);
        add(p1);

        // controller for tile cache threshold
        JPanel p2 = new JPanel();
        p2.setLayout( new BorderLayout() );
        p2.setBackground(LIGHT_BLUE);

        memoryThresholdLabel = new JLabel("75%");
        memoryThresholdLabel.setBackground(LIGHT_BLUE);
        memoryThresholdLabel.setForeground(Color.black);
        p2.add(memoryThresholdLabel, BorderLayout.NORTH);

        memoryThresholdSlider = new JSlider(SwingConstants.VERTICAL, 0, 100, 75);

        memoryThresholdSlider.setBorder(BEVEL_BORDER);
        Hashtable<Integer, JLabel> mt_labels = new Hashtable<Integer, JLabel>();

        for ( int j = 0; j <= 100; j+=10 ) {
            JLabel label = new JLabel(j + "%");
            label.setForeground(Color.black);
            mt_labels.put(new Integer(j), label);
        }

        memoryThresholdSlider.setBackground(BACKGROUND);
        memoryThresholdSlider.setForeground(Color.black);
        memoryThresholdSlider.setPaintLabels(true);
        memoryThresholdSlider.setLabelTable(mt_labels);
        memoryThresholdSlider.setPaintTicks(true);
        memoryThresholdSlider.setMajorTickSpacing(10);
        memoryThresholdSlider.setMinorTickSpacing(5);
        memoryThresholdSlider.setSnapToTicks(true);
        memoryThresholdSlider.addChangeListener(this);

        p2.add(memoryThresholdSlider);
        add(p2);
    }

    /**
     * Set cache to a SunTileCache
     * @param c is a sun tile cache object
     * @throws IllegalArgumentException is <code>c</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>c</code> not an instance of SunTileCache
     * @since TCT 1.0
     */
    public void setTileCache(SunTileCache c) {
        if ( c == null ) {
            throw new IllegalArgumentException("cache cannot be null.");
        }

        if ( c instanceof SunTileCache ) {
            cache = c;
        } else {
            throw new IllegalArgumentException("cache not an instance of SunTileCache");
        }

        memoryCapacitySlider.setValue((int)(cache.getMemoryCapacity() / (1024L * 1024L)));
        memoryThresholdSlider.setValue((int)(100.0F * cache.getMemoryThreshold()));
    }

    /**
     * Provide access to the statistics object
     * @param stats a Statistics object
     * @since TCT 1.0
     */
    public void setStatistics(Statistics stats) {
        statistics = stats;
    }

    /**
     * set memory capacity slider range to proper values.  If the value
     * is below the current cache capacity, max_mem will be set to the
     * current cache capacity.
     *
     * @param max_mem maximum slider value for cache memory capacity
     * @throws IllegalArgumentException if <code>max_mem</code> is .le. 0
     * @since TCT 1.0
     */
     public void setMemoryCapacitySliderMaximum(int max_mem) {
         if ( max_mem <= 0L ) {
             throw new IllegalArgumentException("max_mem must be greater than 0");
         }

         int mem_cap = (int) (cache.getMemoryCapacity() / (1024L * 1024L));

         if ( max_mem < mem_cap ) {
             max_mem = mem_cap;
         }

         memoryCapacitySlider.setMaximum(max_mem);
     }

    /**
     * Responce to slider changes
     */
    @Override
	public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();

        if ( slider == memoryCapacitySlider ) {
            memoryCapacityLabel.setText( slider.getValue() + " MB" );
            cache.setMemoryCapacity( slider.getValue() * 1024L * 1024L );

            if ( statistics != null ) {
                memoryCapacity = cache.getMemoryCapacity();
                memoryUsage    = cache.getCacheMemoryUsed();
                percentTCM = (int)((100.0F * memoryUsage / memoryCapacity) + 0.5F);
                statistics.set(percentTCM);
            }
        } else if ( slider == memoryThresholdSlider ) {
            memoryThresholdLabel.setText( slider.getValue() + "%" );
            cache.setMemoryThreshold( slider.getValue() / 100.0F );
        }
    }
}

