package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: TankListCellRenderer.java
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


import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;

/**
 * This class implements a custom {@link ListCellRenderer} for the tank list.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * 
 */
@SuppressWarnings("rawtypes")
public class TankListCellRenderer extends JLabel implements ListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4736793740949708543L;

	public TankListCellRenderer() {
		// Create the label.
		super("", null, JLabel.CENTER);
		// Set the position of its text, relative to its icon:
		this.setVerticalTextPosition(JLabel.BOTTOM);
		this.setHorizontalTextPosition(JLabel.CENTER);
		this.setOpaque(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		if (value != null) {

			boolean differentItems = false;
			String toolTipText = "";
			List<IqmDataBox> valueList = ((List<IqmDataBox>) value);

			// if the value list contains images, check for heterogeneous
			// images in the stack
			IqmDataBox iqmDataBox = valueList.get(0);

			int numStack = ((List<IqmDataBox>) value).size();
			ImageIcon tankIcon = iqmDataBox.getTankThumbnail();

			String typeForDisplay = "";
			this.setIcon(tankIcon);
			try {
				switch (iqmDataBox.getDataType()) {
				case IMAGE:
					typeForDisplay = (String) iqmDataBox.getProperties().get(
							"DataType");

					if (valueList.size() > 1) {
						List<String> types = new Vector<String>(10);
						types.add(typeForDisplay);
						Iterator<IqmDataBox> iter = valueList.iterator();

						while (iter.hasNext()) {
							String typeToCheck = (String) ((IqmDataBox) iter
									.next()).getProperties().get("DataType");
							if (!typeForDisplay.equals(typeToCheck)
									&& !types.contains(typeToCheck)) {
								types.add(typeToCheck);
								differentItems = true;
							}
						}
						if (differentItems == false) {
							toolTipText = "This image stack contains images of the same color model: "
									+ typeForDisplay;
						} else {
							for (String s : types) {
								toolTipText += s + ", ";
							}

							toolTipText = "This image stack contains images of different color models: "
									+ toolTipText.substring(0,
											toolTipText.length() - 2);
						}
					}
					break;
				case PLOT:
					typeForDisplay = "Plot";
					break;
				case TABLE:
					typeForDisplay = "Table";
					break;
				case CUSTOM:
					typeForDisplay = "Text";
					break;
				default:
					break;
				}

				if (iqmDataBox instanceof IVirtualizable)
					typeForDisplay += "*";
			} catch (Exception e) {
				System.err.println(e);
			}

			this.setIcon(tankIcon);

			if (toolTipText.equals(""))
				this.setToolTipText(null);
			else
				this.setToolTipText(toolTipText);

			if (numStack == 1)
				this.setText((index + 1) + "  " + typeForDisplay);
			if (numStack > 1)
				this.setText((index + 1) + "(" + numStack + ") "
						+ typeForDisplay);
			if (!isSelected) {
				if (differentItems) {
					this.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
							Color.RED));
				} else {
					this.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
							Color.WHITE));
				}
				this.setBackground(Color.white);
				this.setForeground(Color.black);
			} else {
				if (differentItems) {
					this.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
							Color.RED));
				} else {
					this.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
							Color.WHITE));
				}
				this.setBackground(list.getSelectionBackground());
				this.setForeground(list.getSelectionForeground());
			}
		}
		return this;
	}
}
