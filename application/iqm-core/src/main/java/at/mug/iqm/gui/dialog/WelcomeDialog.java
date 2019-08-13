package at.mug.iqm.gui.dialog;

/*
 * #%L
 * Project: IQM - Application Core
 * File: WelcomeDialog.java
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;

/**
 * This class is responsible for displaying the welcome screen.
 * 
 * @author Philipp Kainz
 * 
 */
public class WelcomeDialog extends JDialog {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7588942388984507473L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(WelcomeDialog.class);

	private JPanel welcomePanel;

	private BufferedImage image;

	private JLabel dynamicText;

	/**
	 * This constructs a new {@link JDialog} displaying an animated progress bar
	 * an a short, customizable message.
	 */
	public WelcomeDialog() {
		super(new JFrame(), true);
		this.setUndecorated(true);
		this.getContentPane().setBackground(Color.black);
		this.getContentPane().add(this.createContent());
		this.pack();
		logger.debug("Packing done.");
		CommonTools.centerFrameOnScreen(this);
	}

	/**
	 * This method constructs the welcomePanel
	 * 
	 * @return JPanel
	 */
	private JPanel createContent() {
		this.welcomePanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1052788959989205798L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(image, 0, 0, null);
			}
		};

		this.welcomePanel.setOpaque(false);
		this.welcomePanel.setLayout(new BoxLayout(welcomePanel,
				BoxLayout.Y_AXIS));

		try {
			image = ImageIO.read(Resources.getImageURL("img.welcome"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		image = process(image);

		// place holder for drawing
		JPanel placeHolder = new JPanel();
		placeHolder.setPreferredSize(new Dimension(image.getWidth(), image
				.getHeight()));
		placeHolder.setOpaque(false);
		this.welcomePanel.add(placeHolder);

		// progress bar
		ImageIcon icon = new ImageIcon(
				Resources.getImageURL("icon.processing.bar"));
		JLabel lblIcon = new JLabel(icon);
		lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.welcomePanel.add(lblIcon);

		// update the plugin information
		this.dynamicText = new JLabel(" ");
		this.dynamicText.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.dynamicText
				.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		this.dynamicText.setFont(new Font("Dialog", Font.PLAIN, 11));
		this.dynamicText.setForeground(Color.white);
		this.welcomePanel.add(this.dynamicText);

		return welcomePanel;
	}

	private BufferedImage process(BufferedImage old) {
		int w = old.getWidth();
		int h = old.getHeight();
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(old, 0, 0, null);
		g2d.setPaint(Color.white);
		g2d.setFont(new Font("Dialog", Font.PLAIN, 11));
		String s = I18N.getMessage("application.copyright",    IQMConstants.CURRENTYEAR);
		String s2 = I18N.getMessage("application.coCopyright", IQMConstants.CURRENTYEAR);
		String s1 = IQMConstants.APPLICATION_NAME
				+ " "
				+ IQMConstants.APPLICATION_VERSION
				+ " "
				+ I18N.getMessage("application.isLoading");
		FontMetrics fm = g2d.getFontMetrics();
		int x = (img.getWidth() - fm.stringWidth(s1)) / 2;
		int y = img.getHeight() - fm.getHeight() - 40;
		g2d.drawString(s1, x, y);
		g2d.drawString(s, (img.getWidth() - fm.stringWidth(s)) / 2,
				(int) (y + 1.5 * fm.getHeight()));
		g2d.drawString(s2, (img.getWidth() - fm.stringWidth(s2)) / 2 + 20,
				y + 3 * fm.getHeight());
		g2d.dispose();
		return img;
	}

	/**
	 * Updates a dynamic {@link JLabel} by setting a given text to the
	 * {@link WelcomeDialog} while waiting for the GUI to initialize.
	 * 
	 * @param text the String to be displayed
	 */
	public void updateDynamicText(String text) {
		logger.trace(text);
		this.dynamicText.setText(text);
	}
}
