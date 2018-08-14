package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ImageInfoPanel.java
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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.Resources;

/**
 * This panel displays current pixel information.
 * 
 * @author Philipp Kainz
 * 
 */
public class ImageInfoPanel extends JPanel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -101313891561052070L;

	/**
	 * Custom class logger
	 */
	private static final Logger logger = Logger.getLogger(ImageInfoPanel.class);

	/**
	 * The instance of {@link ILookPanel} where this {@link ImageInfoPanel}
	 * belongs to.
	 */
	private ILookPanel displayPanel;

	/**
	 * The icon for the panel.
	 */
	private JLabel imageInfoIcon;
	/**
	 * The label containing all image information text.
	 */
	private JLabel imageInfoLabel;

	/**
	 * The reset button for the {@link ILookPanel}.
	 */
	private JButton buttonReset;

	/**
	 * The popup listener for the right mouse button click.
	 */
	private ContextPopupListener contextPopupListener;

	/**
	 * Custom constructor. The {@link ContextPopupListener} is set to the
	 * instance.
	 * 
	 * @param contextPopupListener
	 *            the listener
	 */
	public ImageInfoPanel(ContextPopupListener contextPopupListener) {
		logger.debug("Creating a new instance...");
		this.setContextPopupListener(contextPopupListener);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Application.getLook().setCurrentLookPanel(getDisplayPanel());
			}
		});
		this.createAndAssemble();
	}

	/**
	 * This method initializes the image info panel, displaying the current
	 * pixel information.
	 */
	private void createAndAssemble() {
		this.setLayout(new BorderLayout(5, 5));
		this.setMaximumSize(new Dimension(10, 26));
		this.setPreferredSize(new Dimension(200, 26));
		this.setMinimumSize(this.getMaximumSize());
		this.setBackground(Color.LIGHT_GRAY);

		this.addMouseListener(this.getContextPopupListener());

		// add an icon for current image info
		this.add(this.getImageInfoIcon(), BorderLayout.WEST);
		this.add(this.getImageInfoLabel(), BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		add(panel, BorderLayout.EAST);

		buttonReset = new JButton("Reset");
		buttonReset.setEnabled(false);
		buttonReset.setVisible(false); // TODO enable, if required
		panel.add(buttonReset);
		buttonReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Application.getLook().reset();
			}
		});
	}

	/**
	 * @return the imageInfoIcon
	 */
	public JLabel getImageInfoIcon() {
		if (this.imageInfoIcon == null) {
			this.imageInfoIcon = new JLabel(new ImageIcon(
					Resources.getImageURL("icon.look.imageInfo")));
			// add a slight margin to the left edge
			this.imageInfoIcon.setBorder(BorderFactory.createEmptyBorder(0, 2,
					0, 0));
		}
		return imageInfoIcon;
	}

	/**
	 * @param imageInfoIcon
	 *            the imageInfoIcon to set
	 */
	public void setImageInfoIcon(JLabel imageInfoIcon) {
		this.imageInfoIcon = imageInfoIcon;
	}

	/**
	 * @return the imageInfoLabel
	 */
	public JLabel getImageInfoLabel() {
		if (this.imageInfoLabel == null) {
			this.imageInfoLabel = new JLabel();
		}
		return imageInfoLabel;
	}

	/**
	 * @param imageInfoLabel
	 *            the imageInfoLabel to set
	 */
	public void setImageInfoLabel(JLabel imageInfoLabel) {
		this.imageInfoLabel = imageInfoLabel;
	}

	/**
	 * @return the contextPopupListener
	 */
	public ContextPopupListener getContextPopupListener() {
		return contextPopupListener;
	}

	/**
	 * @param contextPopupListener
	 *            the contextPopupListener to set
	 */
	public void setContextPopupListener(
			ContextPopupListener contextPopupListener) {
		this.contextPopupListener = contextPopupListener;
	}

	public ILookPanel getDisplayPanel() {
		return displayPanel;
	}

	public void setDisplayPanel(ILookPanel displayPanel) {
		this.displayPanel = displayPanel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				ContextPopupMenu menu = new ContextPopupMenu();
				menu.createAndAssemble();
				f.getContentPane().add(
						new ImageInfoPanel(new ContextPopupListener(menu)));
				f.pack();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}

}
