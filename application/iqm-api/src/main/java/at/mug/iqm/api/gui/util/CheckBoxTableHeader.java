package at.mug.iqm.api.gui.util;

/*
 * #%L
 * Project: IQM - API
 * File: CheckBoxTableHeader.java
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


import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Idea taken from: http://www.coderanch.com/t/343795/GUI/java/Check-Box-JTable-header
 * @author Philipp Kainz
 * @update 2017-11-08 HA fixed bug of MouseEvent overflow because of repeatedly added MouseListeners 
 *
 */
public class CheckBoxTableHeader 
extends JCheckBox
implements TableCellRenderer, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4837448787519488955L;
	protected int column;
	protected String columnHeaderText = "";
	protected boolean mousePressed = false;
	
	public CheckBoxTableHeader(ItemListener itemListener) {
		this.addItemListener(itemListener);
	}
	
	public CheckBoxTableHeader(ItemListener itemListener, String colHeadText) {
		this(itemListener);
		if (colHeadText == null || colHeadText.isEmpty()){
			this.columnHeaderText = "";
		}else {
			this.columnHeaderText = colHeadText;
		}
	}
	
	@Override
	//This method is called each time the table is rendered, therefore also eg. after scrolling or resizing the window
	public Component getTableCellRendererComponent(
			JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (table != null) {
			JTableHeader header = table.getTableHeader();
			if (header != null) {
				this.setForeground(header.getForeground());
				this.setBackground(header.getBackground());
				this.setFont(header.getFont());
				//It is necessary to remove the MouseListener first.
				//Scrolling or resizing the window repeatedly adds a MouseListener to the same header
				header.removeMouseListener(this);
				header.addMouseListener(this);
			}
		}
		setColumn(column);
		if (this.columnHeaderText.isEmpty()){
			this.setText(String.valueOf(column));
		}else {
			this.setText(this.columnHeaderText);
		}
		// set the column index as name of the column
		this.setName(String.valueOf(column));
		this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));	
		return this;
	}
	
	protected void setColumn(int column) {
		this.column = column;
	}
	
	public int getColumn() {
		return column;
	}
	
	protected void handleClickEvent(MouseEvent e) {
		if (mousePressed) {
			mousePressed = false;
			JTableHeader header = (JTableHeader)(e.getSource());
			JTable tableView = header.getTable();
			TableColumnModel columnModel = tableView.getColumnModel();
			int viewColumn = columnModel.getColumnIndexAtX(e.getX());
			int column = tableView.convertColumnIndexToModel(viewColumn);

			if (viewColumn == this.column 
					&& e.getClickCount() == 1 
					&& column != -1) {
				doClick();
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		handleClickEvent(e);
		((JTableHeader)e.getSource()).repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
}
