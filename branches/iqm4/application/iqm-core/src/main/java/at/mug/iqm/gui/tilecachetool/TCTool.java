package at.mug.iqm.gui.tilecachetool;

/*
 * #%L
 * Project: IQM - Application Core
 * File: TCTool.java
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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.RenderedImage;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.media.jai.CachedTile;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.JAI;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import at.mug.iqm.core.Resources;
import at.mug.iqm.gui.util.GUITools;

import com.sun.media.jai.util.SunTileCache;

/**
 * <p>Title: Tile Cache Monitoring Tool</p>
 * <p>Description: Monitors and displays JAI Tile Cache activity.</p>
 * <p>Copyright: Copyright (c) 2002, 2010</p>
 * <p>    All Rights Reserved   </p>
 * <p>Company: Virtual Visions Software, Inc.</p>
 *
 * @author Dennis Sigel
 * @version 1.02
 *
 * NOTE:  The act of observing can change the observed behavior!!!
 *        This tool will impact performance and will use memory
 *        from the JVM which interacts with the garbage collector.
 *        An attempt was made to minimize these perturbations in
 *        order to provide a useful method of monitoring and
 *        understanding the JAI tile cache.
 */
public final class TCTool extends JFrame
                          implements ActionListener,
                                     Observer,
                                     WindowListener{

    private static final long serialVersionUID = 1L;
    private SunTileCache cache = null;

    private JPanel top_panel;
    private JButton gc_btn;
    private JButton flush_btn;
    private JButton reset_btn;
    private JButton quit_btn;
    private JMenu options_menu;
    private JMenuItem pack_item;  // refit window size
    private JMenu capacity_menu;  // slider cache memory capacity (max value)
    private JMenu delay_menu;
    private JMenu rows_menu;
    private JMenu laf_menu;
    private JRadioButton rad1;
    private JRadioButton rad2;
    private JRadioButton rad3;
    private JRadioButton rad4;

    private static final String METAL   = "javax.swing.plaf.metal.MetalLookAndFeel";
    private static final String WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    private static final String MOTIF   = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    private static final String MAC     = "com.sun.java.swing.plaf.mac.MacLookAndFeel";

    private String current_laf = UIManager.getSystemLookAndFeelClassName();

    private final MemoryChart memoryChart = new MemoryChart();
    private final EventViewer eventViewer = new EventViewer();
    private final Statistics statistics   = new Statistics();
    private final TCInfo information      = new TCInfo();

    private static final Color LIGHT_BLUE = new Color(200, 200, 220);
    private static final LineBorder LINE_BORDER = new LineBorder(Color.darkGray,
                                                                 1);

    private static final String[] capacities = {
        "32MB",
        "64MB",
        "128MB",
        "256MB",
        "512MB"
    };

    private static final String[] delays = {
        "10ms",
        "50ms",
        "100ms",
        "250ms",
        "500ms",
        "1000ms",
        "2000ms",
        "5000ms"
    };

    private static final String[] rows = {
        "1",
        "4",
        "7",
        "10",
        "15",
        "20"
    };

    // cumulative statistics
    private long addTile             = 0;
    private long removeTile          = 0;
    private long removeFlushed       = 0;
    private long removeMemoryControl = 0;
    private long updateAddTile       = 0;
    private long updateGetTile       = 0;

    // action events generated by the tile cache
    private EnumeratedParameter[] actions;
    private int CACHE_EVENT_ADD;
    private int CACHE_EVENT_REMOVE;
    private int CACHE_EVENT_REMOVE_BY_FLUSH;
    private int CACHE_EVENT_REMOVE_BY_MEMORY_CONTROL;
    private int CACHE_EVENT_UPDATE_FROM_ADD;
    private int CACHE_EVENT_UPDATE_FROM_GETTILE;
    private int CACHE_EVENT_ABOUT_TO_REMOVE_TILE;

    // about box message
    private static final String about_msg =
        "<html>" +
        "<center>Tile Cache Tool</center" +
        "<p><center>Version 1.02</center></p>" +
        "<center>October 25, 2002, May 15, 2010</center>" +
        "<p><center>Copyright (c) 2002, 2010 Virtual Visions Software, Inc.</center></p>" +
        "<center>All Rights Reserved</center>" +
        "<br></br>" +
        "</html>";


    /** Default Constructor */
    public TCTool() {
        create(-1, null);
        this.setIconImage(new ImageIcon(Resources.getImageURL("icon.menu.info.tileInfo")).getImage());
    }

    /** Constructor with a non-default memory capacity */
    public TCTool(long memoryCapacity) {
        create(memoryCapacity, null);
    }

    /** Constructor with a custom tile cache */
    public TCTool(SunTileCache sunTileCache) {
        create(-1, sunTileCache);
    }

    // create a simple about box
    private JMenuItem createAboutBox() {
        JMenuItem about = new JMenuItem("About...");

        about.addActionListener( new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(top_panel, about_msg);
            }
        });

        return about;
    }

    // build the user interface
    private void create(long memoryCapacity, SunTileCache sunTileCache) {
        setTitle("Tile Cache Tool");
        // we have our own closing routine
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        setBackground(LIGHT_BLUE);

        top_panel = new JPanel();
        top_panel.setLayout( new BorderLayout() );
        getContentPane().add(top_panel);

        // control options (part 1)
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout( new FlowLayout(FlowLayout.LEFT, 15, 5) );
        menuBar.setBackground(LIGHT_BLUE);
        options_menu = new JMenu("Options");

        pack_item = new JMenuItem("Refit Window");
        pack_item.addActionListener(this);
        pack_item.setToolTipText("Restore this window to original size.");

        capacity_menu = new JMenu("Max Memory");
        for ( int i = 0; i < capacities.length; i++ ) {
            JMenuItem item = new JMenuItem(capacities[i]);
            item.addActionListener(this);
            capacity_menu.add(item);
        }

        delay_menu = new JMenu("Time Delay");

        for ( int i = 0; i < delays.length; i++ ) {
            JMenuItem item = new JMenuItem(delays[i]);
            item.addActionListener(this);
            delay_menu.add(item);
        }

        rows_menu = new JMenu("Event Rows");
        rows_menu.addActionListener(this);

        for ( int i = 0; i < rows.length; i++ ) {
            JMenuItem item = new JMenuItem(rows[i]);
            item.addActionListener(this);
            rows_menu.add(item);
        }

        JMenuItem tmp_item;

        // user interface look and feel
        laf_menu = new JMenu("Look & Feel");
        tmp_item = new JMenuItem("Metal");
        tmp_item.addActionListener(this);
        laf_menu.add(tmp_item);

        tmp_item = new JMenuItem("Windows");
        tmp_item.addActionListener(this);
        laf_menu.add(tmp_item);

        tmp_item = new JMenuItem("Motif");
        tmp_item.addActionListener(this);
        laf_menu.add(tmp_item);

        tmp_item = new JMenuItem("Mac");
        tmp_item.addActionListener(this);
        laf_menu.add(tmp_item);

        options_menu.add(pack_item);
        options_menu.add(capacity_menu);
        options_menu.add(delay_menu);
        options_menu.add(rows_menu);
        options_menu.add(laf_menu);
        options_menu.add( createAboutBox() );

        menuBar.add(options_menu);
        top_panel.add(menuBar, BorderLayout.NORTH);

        // control options (part 2);
        JPanel t2 = new JPanel();
        t2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        t2.setBorder(LINE_BORDER);
        JLabel label1 = new JLabel("Diagnostics:  ");
        t2.add(label1);
        rad1 = new JRadioButton("On", true);
        rad2 = new JRadioButton("Off");
        rad1.addActionListener(this);
        rad2.addActionListener(this);
        t2.add(rad1);
        t2.add(rad2);

        rad1.setToolTipText("Perform diagnostics monitoring");
        rad2.setToolTipText("Stop diagnostics monitoring");

        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(rad1);
        bg1.add(rad2);
        menuBar.add(t2);

        // control options (part 3)
        JPanel t3 = new JPanel();
        t3.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        t3.setBorder(LINE_BORDER);
        JLabel label2 = new JLabel("Cache:  ");
        t3.add(label2);
        rad3 = new JRadioButton("Enabled", true);
        rad4 = new JRadioButton("Disabled");
        rad3.addActionListener(this);
        rad4.addActionListener(this);
        t3.add(rad3);
        t3.add(rad4);

        // important information about JAI.enableDefaultTileCache()
        //                             JAI.disableDefaultTileCache()
        /**
         * Calling JAI.enableDefaultTileCache() or JAI.disableDefaultTileCache()
         * marks a change in tile cache usage.  All state before the calls is
         * maintained "as is", so if the tile cache is currently enabled, then
         * a call is made to disable it, ops/tiles that were in the cache prior
         * to the call will still be cachable.  New tile requests will not be
         * cached.  The TCTool will still monitor items in the cache that were
         * there prior to the "disable" call.  Likewise, it the cache is currently
         * disabled then enabled, tiles prior to the enable call will not be
         * cached or monitored.  The tile cache is flushed when a call is made
         * to JAI.disableDefaultTileCache(), but any valid tiles that existed
         * will be recomputed and cached again.
         */
        rad3.setToolTipText("Resume normal cache operations");
        rad4.setToolTipText("Block new cache operations (status quo)");

        ButtonGroup bg2 = new ButtonGroup();
        bg2.add(rad3);
        bg2.add(rad4);
        menuBar.add(t3);

        JPanel center_panel = new JPanel();
        center_panel.setLayout( new BorderLayout() );
        center_panel.setBackground(LIGHT_BLUE);
        top_panel.add(center_panel, BorderLayout.CENTER);

        if ( sunTileCache == null ) {
            cache = (SunTileCache) JAI.getDefaultInstance().getTileCache();
        } else {
            cache = sunTileCache;
        }

        // obtain cache event actions
        actions = SunTileCache.getCachedTileActions();

        CACHE_EVENT_ADD                      = actions[0].getValue();
        CACHE_EVENT_REMOVE                   = actions[1].getValue();
        CACHE_EVENT_REMOVE_BY_FLUSH          = actions[2].getValue();
        CACHE_EVENT_REMOVE_BY_MEMORY_CONTROL = actions[3].getValue();
        CACHE_EVENT_UPDATE_FROM_ADD          = actions[4].getValue();
        CACHE_EVENT_UPDATE_FROM_GETTILE      = actions[5].getValue();
        CACHE_EVENT_ABOUT_TO_REMOVE_TILE     = actions[6].getValue();

        if ( memoryCapacity >= 0 ) {
            cache.setMemoryCapacity(memoryCapacity);
        }

        cache.enableDiagnostics();
        cache.addObserver(this);

        // build a subpanel for other controls and stats
        JPanel stat_panel = new JPanel();
        stat_panel.setBackground(LIGHT_BLUE);
        stat_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        stat_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 2));

        // buttons
        gc_btn    = new JButton("Force GC");
        flush_btn = new JButton("Flush Cache");
        reset_btn = new JButton("Reset");
        quit_btn  = new JButton("Quit");

        gc_btn.addActionListener(this);
        flush_btn.addActionListener(this);
        reset_btn.addActionListener(this);
        quit_btn.addActionListener(this);

        gc_btn.setToolTipText("Force a call to System.gc()");
        flush_btn.setToolTipText("Empty the tile cache.");
        reset_btn.setToolTipText("Clear counters and reset.");
        quit_btn.setToolTipText("Close the Tile Cache Tool window.");

        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(4, 1, 5, 15));
        p1.setBackground(LIGHT_BLUE);
        p1.add(gc_btn);
        p1.add(flush_btn);
        p1.add(reset_btn);
        p1.add(quit_btn);
        stat_panel.add(p1);

        // Tile Cache Options
        information.setTileCache(cache);
        information.setBackground(LIGHT_BLUE);
        information.setStatistics(statistics);
        stat_panel.add(information);
        center_panel.add(stat_panel, BorderLayout.NORTH);

        // Tile Cache Statistics
        statistics.setBackground(LIGHT_BLUE);
        stat_panel.add(statistics);

        // JVM memory monitor
        center_panel.add(memoryChart, BorderLayout.CENTER);
        memoryChart.start();

        // JAI event logging and monitoring
        JPanel p0 = new JPanel();
        p0.setBackground(LIGHT_BLUE);
        p0.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        p0.add(eventViewer);
        center_panel.add(p0, BorderLayout.SOUTH);

        final TCTool tctool = this;

        WindowListener wl = new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {
                memoryChart.stop();

                cache.deleteObserver(tctool);
                cache.disableDiagnostics();

                cache = null;
                System.gc();
            }

            @Override
			public void windowIconified(WindowEvent e) {
                memoryChart.stop();
            }

            @Override
			public void windowDeiconified(WindowEvent e) {
                memoryChart.start();
            }
        };

        addWindowListener(wl);

        pack();
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        Object tmp = e.getSource();

        if ( tmp instanceof JRadioButton ) {
            JRadioButton rb = (JRadioButton) tmp;

            if ( rb.isSelected() ) {
                if ( rb == rad1 ) {
                    if ( cache != null ) {
                        cache.enableDiagnostics();
                        memoryChart.start();
                    }
                } else if ( rb == rad2 ) {
                    if ( cache != null ) {
                        cache.disableDiagnostics();
                        memoryChart.stop();
                    }
                } else if ( rb == rad3 ) {
                    JAI.enableDefaultTileCache();
                } else if ( rb == rad4 ) {
                    JAI.disableDefaultTileCache();
                }
            }
        }

        if ( tmp instanceof JButton ) {
            JButton button = (JButton) tmp;

            if ( button == gc_btn ) {
                System.gc();
            } else if ( button == flush_btn ) {
                if ( cache != null ) {
                    cache.flush();
                }
            } else if ( button == reset_btn ) {
                if ( cache != null ) {
                    cache.flush();
                }

                // clear cumulative values
                addTile             = 0;
                removeTile          = 0;
                removeFlushed       = 0;
                removeMemoryControl = 0;
                updateAddTile       = 0;
                updateGetTile       = 0;

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
					public void run() {
                        statistics.clear();
                        eventViewer.clear();
                    }
                });

                System.gc();  //too early
            } else if ( button == quit_btn ) {
                memoryChart.stop();
                cache.deleteObserver(this);
                cache.disableDiagnostics();
                cache = null;

                dispose();
                System.gc();
            }
        }

        if ( tmp instanceof JMenuItem ) {
            JMenuItem item = (JMenuItem) tmp;

            if ( item == pack_item ) {
                pack();
            } else {
                String cmd = item.getText();

                // check look and feel options
                if ( cmd.equalsIgnoreCase("Metal") ) {
                    setLookAndFeel(METAL);
                    return;
                } else if ( cmd.equalsIgnoreCase("Windows") ) {
                    setLookAndFeel(WINDOWS);
                    return;
                } else if ( cmd.equalsIgnoreCase("Motif") ) {
                    setLookAndFeel(MOTIF);
                    return;
                } else if ( cmd.equalsIgnoreCase("Mac") ) {
                    setLookAndFeel(MAC);
                    return;
                }

                // memory capacity range for slider
                for ( int i = 0; i < capacities.length; i++ ) {
                    if ( cmd.equals(capacities[i]) ) {
                        String str = capacities[i].substring(0, capacities[i].lastIndexOf("M"));
                        int mem_max = Integer.parseInt(str);
                        information.setMemoryCapacitySliderMaximum( mem_max );
                        return;
                    }
                }

                // memory chart speed
                for ( int i = 0; i < delays.length; i++ ) {
                    if ( cmd.equals(delays[i]) ) {
                        String str = delays[i].substring(0, delays[i].lastIndexOf("m"));
                        memoryChart.setDelay( Integer.parseInt(str) );
                        return;
                    }
                }

                // number of rows in the event viewer
                for ( int i = 0; i < rows.length; i++ ) {
                    if ( cmd.equals(rows[i]) ) {
                        eventViewer.setRows( Integer.parseInt(rows[i]) );
                        pack();
                        return;
                    }
                }
            }
        }
    }

    private void setLookAndFeel(String laf) {
        if ( current_laf != laf ) {
            current_laf = laf;

            try {
                UIManager.setLookAndFeel(current_laf);
                SwingUtilities.updateComponentTreeUI(this);
                pack();
            } catch( Exception e ) {
                System.out.println("Look and Feel not supported.");
                current_laf = METAL;
            }
        }
    }

    @Override
	public synchronized void update(Observable possibleSunTileCache,
                                    Object possibleCachedTile) {

        SunTileCache sun_tile_cache = null;
        CachedTile cached_tile = null;
        int cache_event = -1;

        // check SunTileCache
        if ( possibleSunTileCache instanceof SunTileCache ) {
            sun_tile_cache = (SunTileCache) possibleSunTileCache;
        } else {
            return;
        }

        // check cached tile
        if ( (possibleCachedTile != null) &&
             (possibleCachedTile instanceof CachedTile) ) {
            cached_tile = (CachedTile) possibleCachedTile;
            cache_event = cached_tile.getAction();

            if ( cache_event == CACHE_EVENT_ABOUT_TO_REMOVE_TILE ) {
                return;
            }
        } else {
            return;
        }

        // collect information
        RenderedImage image  = cached_tile.getOwner();

        final long tileSize    = cached_tile.getTileSize();
        final long timeStamp   = cached_tile.getTileTimeStamp();
        final long cacheHits   = sun_tile_cache.getCacheHitCount();
        final long cacheMisses = sun_tile_cache.getCacheMissCount();
        final long tileCount   = sun_tile_cache.getCacheTileCount();

        float memoryCapacity = sun_tile_cache.getMemoryCapacity();
        float memoryUsage    = sun_tile_cache.getCacheMemoryUsed();
        final int percentTCM = (int) ((100.0F * memoryUsage / memoryCapacity) + 0.5F);

        String jai_op;

        // image can be null if it was garbage collected
        if ( image != null ) {
            String temp = image.getClass().getName();
            jai_op = temp.substring(temp.lastIndexOf(".") + 1);
        } else {
            jai_op = "Op was removed by GC";
        }

        final Vector<String> eventData = new Vector<String>(5,1);
        eventData.addElement(jai_op);

        if ( cache_event == CACHE_EVENT_ADD ) {
            eventData.addElement("Add");
            addTile++;
        } else if ( cache_event == CACHE_EVENT_REMOVE ) {
            eventData.addElement("Remove");
            removeTile++;
        } else if ( cache_event == CACHE_EVENT_REMOVE_BY_FLUSH ) {
            eventData.addElement("Remove by Flush");
            removeFlushed++;
        } else if ( cache_event == CACHE_EVENT_REMOVE_BY_MEMORY_CONTROL ) {
            eventData.addElement("Remove by Memory Control");
            removeMemoryControl++;
        } else if ( cache_event == CACHE_EVENT_UPDATE_FROM_ADD ) {
            eventData.addElement("Update from Add");
            updateAddTile++;
        } else if ( cache_event == CACHE_EVENT_UPDATE_FROM_GETTILE ) {
            eventData.addElement("Update from GetTile");
            updateGetTile++;
        }

        eventData.addElement("" + tileSize);
        eventData.addElement("" + timeStamp);

        /* synchronizes data fields */
        final long f_addTile             = addTile;
        final long f_removeTile          = removeTile;
        final long f_removeFlushed       = removeFlushed;
        final long f_removeMemoryControl = removeMemoryControl;
        final long f_updateAddTile       = updateAddTile;
        final long f_updateGetTile       = updateGetTile;

        /**
         *  Bug 0001 reported and suggested fix by Mike Pilone
         */

        /* fixes hang in Swing applications */
        SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                eventViewer.insertRow(0, eventData);

                statistics.set(tileCount,
                               cacheHits,
                               cacheMisses,
                               f_addTile,
                               f_removeTile,
                               f_removeFlushed,
                               f_removeMemoryControl,
                               f_updateAddTile,
                               f_updateGetTile,
                               percentTCM);
            }
        });
    }

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		
		this.setVisible(false);
		GUITools.setTcTool(null);
		this.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}
}
