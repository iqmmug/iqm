package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: TablePanel.java
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import at.mug.iqm.api.gui.ITablePanel;
import at.mug.iqm.api.gui.util.AdjustingTable;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.commons.util.table.TableTools;

/**
 * This class displays a {@link JTable} on a panel.
 * 
 * @author Philipp Kainz
 * 
 */
public class TablePanel extends JPanel implements ITablePanel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -4533508097054098366L;
	private TableModel tableModel;
	private AdjustingTable table;
	private JScrollPane scrollPane;

	private boolean multipleModelsDisplayed = false;

	/**
	 * A flag whether or not the panel currently displays an item.
	 */
	private boolean isEmpty = true;

	private TableModel testModel = new TableModel(new Object[][] {
			{ "0", "v1" }, { "1", "v2" }, { "2", "v3" }, }, new String[] {
			"ID", "Value" });

	private JButton btnAddTable;
	private JLabel lblDebugButtons;
	private JPanel panelOptions;
	private JButton button;

	/**
	 * Create the panel.
	 */
	public TablePanel() {
		setBackground(Color.GRAY);
		setBorder(new MatteBorder(5, 1, 1, 1, (Color) Color.GRAY));
		setLayout(new BorderLayout(2, 2));

		table = new AdjustingTable();
		scrollPane = new JScrollPane(table);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		add(scrollPane);

		JPanel panel = new JPanel();
		panel.setBackground(Color.GREEN);
		//DEBUG PANEL add(panel, BorderLayout.NORTH);

		JButton btnRemoveTable = new JButton("Reset");
		btnRemoveTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});

		lblDebugButtons = new JLabel("DEBUG BUTTONS");
		panel.add(lblDebugButtons);
		panel.add(btnRemoveTable);

		btnAddTable = new JButton("Add Table");
		btnAddTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addTableToPanel();
			}
		});
		panel.add(btnAddTable);

		panelOptions = new JPanel();
		panelOptions.setPreferredSize(new Dimension(10, 26));
		panelOptions.setBorder(new EmptyBorder(2, 2, 2, 2));
		panelOptions.setBackground(Color.LIGHT_GRAY);
		add(panelOptions, BorderLayout.SOUTH);
		panelOptions.setLayout(new BorderLayout(5, 5));

		button = new JButton("Reset");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		panelOptions.add(button, BorderLayout.EAST);

	}

	/**
	 * Sets the table model of the table and fires
	 * {@link DefaultTableModel#fireTableStructureChanged()} to the listeners.
	 * <p>
	 * If <code>tableModel</code> is <code>null</code>, an empty
	 * {@link DefaultTableModel} will be set to the table.
	 * <p>
	 * It also sets <code>isEmpty</code> to <code>false</code>.
	 * 
	 * @param tableModel
	 */
	@Override
	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
		if (tableModel == null) {
			this.tableModel = new TableModel();
		}

		this.table.setModel(this.tableModel);

		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// this.table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		JViewport vp = getScrollPane().getViewport();
		vp.removeAll();
		vp.add(this.table);

		this.table.setFillsViewportHeight(false);

		// enable this for fixed column width
		// this.tableModel.fireTableStructureChanged();

		// set the table's selection mode
		this.setDefaultTableSelectionMode();

		this.multipleModelsDisplayed = false;

		this.isEmpty = false;
	}

	@Override
	public void setTableModels(List<TableModel> tableModels) {
		// merge all table models and set them to the panel
		this.setTableModel(TableTools.mergeTableModels(tableModels));
		if (tableModels.size() > 1) {
			multipleModelsDisplayed = true;
		}
	}

	/**
	 * Gets the table model from the table and fires
	 * {@link DefaultTableModel#fireTableStructureChanged()} to the listeners.
	 * <p>
	 * If <code>table</code> is <code>null</code>, an empty
	 * {@link DefaultTableModel} will be set to the table.
	 * <p>
	 * It also sets <code>isEmpty</code> to <code>false</code>.
	 * 
	 * @param table
	 */
	@Override
	public void setTable(JTable table) {
		this.tableModel = TableTools.convertToTableModel(table.getModel());
		if (tableModel == null) {
			this.tableModel = new TableModel();
		}

		this.table.setModel(this.tableModel);

		JViewport vp = getScrollPane().getViewport();
		vp.removeAll();
		vp.add(this.table);
		this.table.setFillsViewportHeight(true);

		this.setDefaultTableSelectionMode();

		this.tableModel.fireTableStructureChanged();

		this.isEmpty = false;
	}

	/**
	 * Sets the default selection mode of the table.
	 */
	protected void setDefaultTableSelectionMode() {
//		System.out.println("Setting TableSelectionMode");
////		this.table
////				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
////		this.table.setColumnSelectionAllowed(true);
////		this.table.setRowSelectionAllowed(false);
//		//this.table.setCellSelectionEnabled(true);
//
//		// set up action listener on table header
//		JTableHeader header = table.getTableHeader();
//		
//		MouseListener[] mls = header.getMouseListeners();
//		for (MouseListener tmp : mls){
//			header.removeMouseListener(tmp);
//		}
//		
//		header.addMouseListener(new MouseAdapter() {
//			public void mouseReleased(final MouseEvent e) {
//				if (SwingUtilities.isRightMouseButton(e)){
//					// show a popupmenu
//					JPopupMenu popupmenu = new JPopupMenu();
//					popupmenu.setLocation(e.getPoint());
//					JMenuItem selCol = new JMenuItem("Select Column");
//					selCol.addActionListener(new ActionListener() {
//						
//						@Override
//						public void actionPerformed(ActionEvent evt) {
//							select(e);
//						}
//					});
//					popupmenu.add(selCol);
//					popupmenu.show(table.getTableHeader(), e.getPoint().x, e.getPoint().y);
//				}
//			}
//			
//			protected void select(MouseEvent e){
//				table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//				table.setColumnSelectionAllowed(true);
//				table.setRowSelectionAllowed(false);
//				
//				if (!e.isShiftDown()) {
//					table.clearSelection();
//				}
//
//				int colIdx = table.columnAtPoint(e.getPoint());
//				table.addColumnSelectionInterval(colIdx, colIdx);
//				
//				// reset to default
//				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//				table.setColumnSelectionAllowed(false);
//				table.setRowSelectionAllowed(true);
//			}
//		});
	}

	/**
	 * Gets the reference to the currently displayed {@link DefaultTableModel}
	 * of the {@link JTable}.
	 * 
	 * @return the {@link TableModel}
	 */
	@Override
	public TableModel getTableModel() {
		return tableModel;
	}

	/**
	 * Gets the reference to the currently displayed {@link DefaultTableModel}
	 * of the {@link JTable} without any listeners.
	 * 
	 * @return the {@link TableModel}
	 */
	@Override
	public TableModel getTableModel(boolean withListeners) {
		if (withListeners) {
			return getTableModel();
		} else {
			TableModel tmClone = tableModel;
			try {
				tmClone = tableModel.clone();
				tmClone.removeTableModelListener(getTable());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

			return tmClone;
		}
	}

	/**
	 * Get a clone of the displayed {@link JTable} object.
	 * 
	 * @return a copy of the {@link JTable}.
	 */
	@Override
	public JTable getTableClone() {
		JTable copy = new JTable(table.getModel());
		return copy;
	}

	/**
	 * Removes the table from the panel, i.e. sets the table model of the table
	 * to a new {@link DefaultTableModel}.
	 * <p>
	 * It also sets <code>isEmpty</code> to <code>true</code>.
	 */
	@Override
	public void reset() {
		this.setTableModel(null);
		this.multipleModelsDisplayed = false;
		this.isEmpty = true;
		this.revalidate();
	}

	/**
	 * Determines whether or not the TablePanel currently shows a table.
	 * 
	 * @return <code>true</code> if a table is shown, <code>false</code> if not
	 */
	@Override
	public boolean isEmpty() {
		return isEmpty;
	}

	/**
	 * A test method for adding a table to the panel
	 */
	private void addTableToPanel() {
		this.setTableModel(testModel);
		this.revalidate();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.getContentPane().add(new TablePanel());
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

	@Override
	public JTable getTable() {
		return table;
	}

	@Override
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	@Override
	public boolean isMultipleModelsDisplayed() {
		return multipleModelsDisplayed;
	}

	@Override
	public void setMultipleModelsDisplayed(boolean multipleModelsDisplayed) {
		this.multipleModelsDisplayed = multipleModelsDisplayed;
	}
}
