package at.mug.iqm.api.gui.util;

/*
 * #%L
 * Project: IQM - API
 * File: AdjustingTableCellRenderer.java
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


import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class AdjustingTableCellRenderer extends JLabel implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1928616408701132757L;

	@Override
	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus,
			final int row, int column) {

		JLabel cellSpacingLabel = (this);
		Font f = cellSpacingLabel.getFont();

		if (isSelected) {
			this.setBackground(table.getSelectionBackground());
			this.setForeground(table.getSelectionForeground());
			this.setFont(f.deriveFont(Font.PLAIN));
			this.setBorder(null);
		} else {
			this.setBackground(table.getBackground());
			this.setForeground(table.getForeground());
			this.setFont(f.deriveFont(Font.PLAIN));
			this.setBorder(null);
		}

		if (cellSpacingLabel != null) {
			cellSpacingLabel.setBorder(
					new CompoundBorder(
							new EmptyBorder(
									new Insets(1, 4, 1, 4)), 
							cellSpacingLabel.getBorder()));
		}

		this.setOpaque(true);
		this.setText(String.valueOf(value));

		return this;
	}
	
	@Override
	public void validate() {}
    @Override
	public void revalidate() {}
    @Override
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
    @Override
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
}
