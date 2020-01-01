package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: ManagerListCellRenderer.java
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


import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 * This class implements a custom {@link ListCellRenderer} for the manager list.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class ManagerListCellRenderer extends JLabel implements ListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6129361503889046556L;

	public ManagerListCellRenderer() {
		this.setOpaque(true);
		this.setVerticalTextPosition(SwingConstants.CENTER);
		this.setHorizontalTextPosition(SwingConstants.RIGHT);
	}

	// value is here a single ImageIcon
	@Override
	public abstract Component getListCellRendererComponent(JList list, Object value,
			int mgrIndex, boolean isSelected, boolean cellHasFocus);
}
