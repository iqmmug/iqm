package at.mug.iqm.gui.tilecachetool;

/*
 * #%L
 * Project: IQM - Application Core
 * File: EventViewer.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * <p>Title: Tile Cache Monitoring Tool</p>
 * <p>Description: Monitors and displays JAI Tile Cache activity.</p>
 * <p>Copyright: Copyright (c) 2002, 2010</p>
 * <p>   All Rights Reserved</p>
 * <p>Company: Virtual Visions Software, Inc.</p>
 *
 * @author Dennis Sigel
 * @version 1.02
 *
 * Purpose:  Display various JAI events in a table.
 */
public final class EventViewer extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollpane;

    private Object[][] initial_data = { {"-", "-", "-", "-"} };

    private static final Color LIGHT_BLUE = new Color(240, 240, 255);
    private static final Font TABLE_FONT  = new Font("monospaced", Font.BOLD, 12);
    private static final String[] COLUMN_LABELS = {
        "JAI Operator",
        "Event",
        "Tile Size",
        "Timestamp"
    };


    /**
     * Default Constructor
     */
    public EventViewer() {
        setLayout( new FlowLayout(FlowLayout.LEFT, 1, 1) );

        model = new DefaultTableModel(initial_data, COLUMN_LABELS);
        table = new JTable(model);

        table.setFont(TABLE_FONT);
        table.setBackground(LIGHT_BLUE);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoscrolls(true);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);

        scrollpane = new JScrollPane(table);
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        EmptyBorder empty_border = new EmptyBorder(5, 5, 5, 5);
        BevelBorder bevel_border = new BevelBorder(BevelBorder.LOWERED);

        scrollpane.setBorder( new CompoundBorder(empty_border, bevel_border) );

        // add some initial room for imgWidth (arbitrary)
        Dimension dim = new Dimension(scrollpane.getPreferredSize().width + 100,
                                      140);

        scrollpane.setPreferredSize(dim);
        add(scrollpane);
    }

    /**
     * Sets the number of displayed rows in the event viewer.
     *
     * @param rows Positive number of displayed event rows
     * @throws IllegalArgumentException if rows is .le. 0
     * @since TCT 1.0
     */
    public void setRows(int rows) {
        if ( rows <= 0 ) {
            throw new IllegalArgumentException("rows must be greater than 0.");
        }

        int hdr_height = (int)table.getTableHeader().getHeaderRect(0).getHeight();
        int height     = table.getRowHeight() * (rows + 1) + hdr_height;
        Dimension dim  = new Dimension(scrollpane.getPreferredSize().width,
                                       height);

        scrollpane.setPreferredSize(dim);
        scrollpane.revalidate();
    }

    /**
     * Removes all of the rows of events in the event viewer.
     * @since TCT 1.0
     */
    public void clear() {
        int count = model.getRowCount() - 1;
        for ( int i = count; i >= 0; i-- ) {
            model.removeRow(i);
        }

        model = new DefaultTableModel(initial_data, COLUMN_LABELS);

        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
    }

    /**
     * Insert a new row of event information.
     * @param row insert data before this <code>row</code>
     * @param data set of events
     * @throws IllegalArgumentException if <code>rows</code> is .lt. 0
     * @throws IllegalArgumentException if <code>data</code> is <code>null</code>
     */
    public synchronized void insertRow(int row, Vector<?> data) {
        if ( row < 0 ) {
            throw new IllegalArgumentException("Rows must be greater or equal to 0.");
        }

        if ( data == null ) {
            throw new IllegalArgumentException("Data cannot be null.");
        }

        model.insertRow(row, data);
    }
}

