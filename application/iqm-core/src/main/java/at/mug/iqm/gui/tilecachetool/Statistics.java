package at.mug.iqm.gui.tilecachetool;

/*
 * #%L
 * Project: IQM - Application Core
 * File: Statistics.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 * <p>Title: Tile Cache Monitoring Tool</p>
 * <p>Description: Monitors and displays JAI Tile Cache activity.</p>
 * <p>Copyright: Copyright (c) 2002, 2010</p>
 * <p>    All Rights Reserved</p>
 * <p>Company: Virtual Visions Software, Inc.</p>
 *
 * @author Dennis Sigel
 * @version 1.02
 *
 * Purpose:  Diplsay various statistics about the Tile Cache.
 */
public final class Statistics extends JPanel {

    private static final long serialVersionUID = 1L;
    private long[] stats = null;
    private int percent  = 0;

    private static final Color DARK_BLUE   = new Color(10, 10, 120);
    private static final Color DARK_RED    = new Color(240, 40, 40);
    private static final Color BACKGROUND  = new Color(180, 180, 220);
    private static final Font DEFAULT_FONT = new Font("monospaced",
                                                      Font.BOLD,
                                                      14);
    private static final String[] LABELS = {
        "     Tile Count: ",
        "     Cache Hits: ",
        "   Cache Misses: ",
        "       Add Tile: ",
        "    Remove Tile: ",
        "  Remove(flush): ",
        "Remove(control): ",
        "Update(addTile): ",
        "Update(getTile): ",
        " Used Memory(%): "
    };


    public Statistics() {
        setLayout(null);
        setPreferredSize( new Dimension(270, 200) );

        EmptyBorder emptyBorder = new EmptyBorder(5, 5, 5, 5);
        LineBorder lineBorder   = new LineBorder(Color.gray, 1);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder,
                                                                     "Statistics",
                                                                     TitledBorder.LEFT,
                                                                     TitledBorder.TOP,
                                                                     DEFAULT_FONT,
                                                                     Color.black);

        CompoundBorder compoundBorder = new CompoundBorder(emptyBorder,
                                                           titledBorder);

        setBorder(compoundBorder);

        stats = new long[LABELS.length];
    }

    public void set(long tileCount,
                    long cacheHits,
                    long cacheMisses,
                    long addTile,
                    long removeTile,
                    long removeFlushed,
                    long removeMemoryControl,
                    long updateAddTile,
                    long updateGetTile,
                    int percent) {

        stats[0] = tileCount;
        stats[1] = cacheHits;
        stats[2] = cacheMisses;
        stats[3] = addTile;
        stats[4] = removeTile;
        stats[5] = removeFlushed;
        stats[6] = removeMemoryControl;
        stats[7] = updateAddTile;
        stats[8] = updateGetTile;
        stats[9] = percent;

        this.percent = percent;
        repaint();
    };

    public void set(int percent) {
        this.percent = percent;
        stats[9] = percent;
        repaint();
    }

    public synchronized void clear() {
        for ( int i = 0; i < stats.length; i++ ) {
            stats[i] = 0;
        }

        percent = 0;
        repaint();
    }

    @Override
	public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        Insets insets  = getInsets();

        // clear drawing area
        g2d.setColor(BACKGROUND);
        g2d.fillRect(insets.left,
                     insets.top,
                     getWidth() - insets.left - insets.right,
                     getHeight() - insets.top - insets.bottom);

        int x1 = insets.left + 10;
        int y1 = insets.top  + 10;
        int y2 = getHeight() - insets.bottom - 5;

        int w = 20;
        int h = y2 - y1 + 1;

        // clear the percentage bar graph
        g2d.setColor(DARK_BLUE);
        g2d.fillRect(x1, y1, w, h);

        // draw a border
        g2d.setColor(Color.white);
        g2d.drawRect(x1-1, y1-1, w+1, h+1);

        // fill tile cache percentage graph
        g2d.setColor(DARK_RED);
        int p = (int)(h * (percent / 100.0F));

        if ( p > 0 ) {
            g2d.fillRect(x1, y2-p, w, p);
        };

        // draw text
        int x = insets.left + 50;
        int y = insets.top  + 20;

        g2d.setColor(Color.black);
        g2d.setFont(DEFAULT_FONT);

        int fontHeight = getFontMetrics(DEFAULT_FONT).getAscent();

        for ( int i = 0; i < LABELS.length; i++ ) {
            g2d.drawString(LABELS[i] + stats[i], x, y);
            y += fontHeight;
        }
    }
}

