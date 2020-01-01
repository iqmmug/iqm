package at.mug.iqm.api.gui.util;

/*
 * #%L
 * Project: IQM - API
 * File: ColorChannelWidget.java
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
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.I18N;

public class ColorChannelWidget extends JPanel {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -6265551014270634043L;

	/**
	 * A custom class logger.
	 */
	private static final Logger logger = LogManager.getLogger(ColorChannelWidget.class);

	// content
	private JPanel content;
	private JLabel lblChannelName;
	private JCheckBox chbxVisibility;
	private JLabel lblChannelIndex;

	private String channelName = "channel/color name";
	private int channelIndex = 0;

	/**
	 * Create the panel.
	 */
	public ColorChannelWidget() {
		setLayout(new BorderLayout(0, 0));

		content = new JPanel();
		content.setBorder(new EmptyBorder(2, 2, 2, 2));
		FlowLayout fl_content = (FlowLayout) content.getLayout();
		fl_content.setVgap(1);
		fl_content.setHgap(2);
		add(content, BorderLayout.WEST);
		content.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		chbxVisibility = new JCheckBox();
		lblChannelIndex = new JLabel(String.valueOf(channelIndex));
		lblChannelIndex.setBorder(new EmptyBorder(0, 5, 0, 0));
		Font f = new Font(lblChannelIndex.getFont().getFontName(), Font.PLAIN,
				10);
		lblChannelIndex.setFont(f);
		content.add(lblChannelIndex);
		chbxVisibility.setBorder(new EmptyBorder(2, 2, 2, 2));
		content.add(chbxVisibility);

		lblChannelName = new JLabel(channelName);
		lblChannelName.setHorizontalTextPosition(SwingConstants.LEADING);
		lblChannelName.setBorder(new EmptyBorder(0, 0, 0, 0));

		lblChannelName.setHorizontalAlignment(SwingConstants.LEFT);
		lblChannelName.setFont(f);
		content.add(lblChannelName);

		content.setToolTipText(I18N
				.getGUILabelText("channel.widget.content.ttp"));
	}

	public void setChannelSelected(boolean isSelected) {
		logger.debug("Setting channel visibility of [" + this.channelName
				+ "] to [" + isSelected + "]");
		if (isSelected == true) {
			chbxVisibility.setSelected(true);
			chbxVisibility.setToolTipText(I18N
					.getGUILabelText("channel.widget.visible.hide.ttp"));
		} else {
			chbxVisibility.setSelected(false);
			chbxVisibility.setToolTipText(I18N
					.getGUILabelText("channel.widget.visible.show.ttp"));
		}
	}

	public JCheckBox getChbxVisibility() {
		return chbxVisibility;
	}

	public JLabel getLblIcon() {
		return lblChannelIndex;
	}

	public boolean isChannelSelected() {
		return chbxVisibility.isSelected();
	}

	/**
	 * Set the name of the channel. This name will also be displayed in the
	 * widget's label.
	 * 
	 * @param name
	 */
	public void setChannelName(String name) {
		if (name == null) {
			name = I18N.getGUILabelText("channel.single.text");
		}
		this.channelName = name;
		this.lblChannelName.setText(name);
	}

	public String getChannelName() {
		return this.channelName;
	}

	public void setIcon(ImageIcon icon) {
		lblChannelIndex.setIcon(icon);
	}

	public void setChannelIndex(int index) {
		this.channelIndex = index;
		// update the label
		lblChannelIndex.setText(String.valueOf(index));
		lblChannelIndex.revalidate();
	}

	public int getChannelIndex() {
		return this.channelIndex;
	}
}
