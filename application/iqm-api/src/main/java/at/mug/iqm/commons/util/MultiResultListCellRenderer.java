package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: MultiResultListCellRenderer.java
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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import at.mug.iqm.api.model.IqmDataBox;

/**
 * 
 * @author Philipp Kainz
 *
 */
@SuppressWarnings("rawtypes")
public class MultiResultListCellRenderer extends JLabel implements
		ListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8316044364909810097L;
	
	public MultiResultListCellRenderer() {
		this.setOpaque(true);
		this.setVerticalTextPosition(SwingConstants.CENTER);
		this.setHorizontalTextPosition(SwingConstants.RIGHT);
	}

	public Component getListCellRendererComponent(JList list, // the list
			Object value, // value to display
			int index, // cell index
			boolean isSelected, // is the cell selected
			boolean cellHasFocus) // does the cell have focus
	{
		IqmDataBox box = (IqmDataBox) value;
		ImageIcon icon = box.getManagerThumbnail();
		setIcon(icon);
		setText(" " + (index+1) + " ");

		// fetch the data type of the image
		String toolTipText = "<html>";

		switch (box.getDataType()) {
		case IMAGE:
			String imgType = (String) box.getProperties().get("DataType");
			String height = (String) box.getProperties().get("Height");
			String width = (String) box.getProperties().get("Width");
			String fileName = (String) box.getProperties().get("file_name");

			toolTipText += "<b>Name: "
					+ (String) box.getProperties().get("model_name") + "<br/>";
			toolTipText += imgType + ", " + width + "x" + height + "px"
					+ "</b>";
			if (fileName != null && !fileName.equals("")) {
				toolTipText += "<p>File name: "
						+ (String) box.getProperties().get("file_name")
						+ "</p>";
			}
			// THIS PRINTS ALL AVAILABLE IMAGE PROPERTIES TO THE TOOL
			// TIP
			// toolTipText += "<p>" + ImageAnalyzer.printProperties(pi) +
			// "</p>";

			break;

		case PLOT:

			String domainUnit = (String) box.getProperties().get("domainUnit");
			String domainHeader = (String) box.getProperties().get(
					"domainHeader");
			if (domainHeader == null) {
				domainHeader = "";
			}
			if (domainUnit != null) {
				domainUnit = " [" + domainUnit + "]:";
			} else {
				domainUnit = "";
			}

			Object domainMin = box.getProperties().get("domainMin");
			Object domainMax = box.getProperties().get("domainMax");

			String rangeUnit = (String) box.getProperties().get("rangeUnit");
			String rangeHeader = (String) box.getProperties()
					.get("rangeHeader");

			if (rangeHeader == null) {
				rangeHeader = "";
			}
			if (rangeUnit != null) {
				rangeUnit = " [" + rangeUnit + "]:";
			} else {
				rangeUnit = "";
			}

			Object rangeMin = box.getProperties().get("rangeMin");
			Object rangeMax = box.getProperties().get("rangeMax");

			toolTipText += "<b>Name: "
					+ (String) box.getProperties().get("model_name")
					+ "<br/></b>";
			toolTipText += "Domain (X): " + domainHeader + domainUnit + " #"
					+ box.getProperties().get("domainSize") + ", min="
					+ domainMin + ", max=" + domainMax + "<br/>"
					+ "Range (Y): " + rangeHeader + rangeUnit + " min="
					+ rangeMin + ", max=" + rangeMax + "<br/>" + "<p>";
			break;

		case TABLE:
			toolTipText += "<b>Name: "
					+ (String) box.getDataModel().getModelName()
					+ "<br/></b>";
			toolTipText += "Rows: # " + box.getProperties().get("Rows")
					+ ", Columns: # " + box.getProperties().get("Columns");
			break;
		default:
			break;
		}

		toolTipText += "</html>";
		this.setToolTipText(toolTipText);
		
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		return this;
	}
}
