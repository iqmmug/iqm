package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ColorChannelSelectorPanel.java
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.util.ColorChannelWidget;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.image.ImageAnalyzer;
import at.mug.iqm.commons.util.image.ImageTools;

/**
 * This panel maintains the selection of color bands of an image.
 * 
 * @author Philipp Kainz
 * 
 */
public class ColorChannelSelectorPanel extends JPanel implements ActionListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 4793642762199892675L;

	/**
	 * A custom class logger.
	 */
	private static final Logger logger = LogManager.getLogger(ColorChannelSelectorPanel.class);

	// GUI elements
	private ILookPanel displayPanel;
	private JPanel headerPanel;
	private JLabel lblHeader;
	// private JLabel lblHideAll;
	private JLabel lblShowAll;

	// class members for image/channel management
	private PlanarImage extractedImage;
	private boolean hasSelectiveChannelsDisplayed;

	private ArrayList<ColorChannelWidget> widgets = new ArrayList<ColorChannelWidget>();
	private List<Integer> selectedChannelIndexes = new ArrayList<Integer>();
	private Box widgetContainer;

	/**
	 * Create the panel.
	 */
	public ColorChannelSelectorPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel northPanel = new JPanel();
		add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new BorderLayout(0, 0));

		headerPanel = new JPanel();
		northPanel.add(headerPanel, BorderLayout.NORTH);
		headerPanel.setBorder(new EmptyBorder(1, 1, 1, 1));
		headerPanel.setLayout(new GridLayout(0, 1, 0, 0));

		lblHeader = new JLabel(I18N.getGUILabelText("channels.text"));
		headerPanel.add(lblHeader);
		lblHeader.setOpaque(true);
		lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
		lblHeader.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblHeader.setBackground(Color.WHITE);
		lblHeader.setIcon(new ImageIcon(Resources.getImageURL("icon.layers")));

		JPanel buttonPanel = new JPanel();
		northPanel.add(buttonPanel, BorderLayout.CENTER);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		// lblHideAll = new JLabel();
		// lblHideAll.addMouseListener(new MouseAdapter() {
		// @Override
		// public void mousePressed(MouseEvent e) {
		// hideAll();
		// }
		// });
		// lblHideAll.setBorder(new EmptyBorder(2, 2, 2, 2));
		// lblHideAll.setIcon(new ImageIcon(Resources
		// .getImageURL("icon.channels.all.hide")));
		// lblHideAll
		// .setToolTipText(I18N.getGUILabelText("channels.all.hide.ttp"));
		// buttonPanel.add(lblHideAll);

		lblShowAll = new JLabel();
		lblShowAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				reset();
			}
		});
		lblShowAll.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblShowAll.setIcon(new ImageIcon(Resources
				.getImageURL("icon.channels.all.show")));
		lblShowAll
				.setToolTipText(I18N.getGUILabelText("channels.all.show.ttp"));
		buttonPanel.add(lblShowAll);

		Component horizontalGlue = Box.createHorizontalGlue();
		buttonPanel.add(horizontalGlue);

		JPanel panel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(panel);
		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(new EmptyBorder(5, 1, 1, 1));
		scrollPane.setBackground(Color.LIGHT_GRAY);
		panel.setLayout(new BorderLayout(0, 0));

		widgetContainer = Box.createVerticalBox();
		panel.add(widgetContainer, BorderLayout.NORTH);
	}

	public ColorChannelSelectorPanel(ILookPanel displayPanel) {
		this();
		this.displayPanel = displayPanel;
	}

	/**
	 * Show all layers equals a reset of the color channels.
	 */
	protected void reset() {
		System.out.println("Showing all channels (resetting).");
		selectedChannelIndexes.clear();
		// re-add all channels
		for (int i = 0; i < widgets.size(); i++) {
			ColorChannelWidget widget = widgets.get(i);
			widget.getChbxVisibility().setSelected(true);
			selectedChannelIndexes.add(i, widget.getChannelIndex());
		}
		this.extractedImage = displayPanel.getCurrentImage();
		setSelectiveChannelsDisplayed(false);
		displayPanel.updateImageLayer();
	}

	/**
	 * Explicitly set the specified band selected in the channel menu.
	 * 
	 * @param index
	 *            the index to be selected
	 * @param selected
	 *            a flag whether or not the index should be selected or
	 *            deselected, <code>true</code> for sole selection,
	 *            <code>false</code> for sole deselection
	 * 
	 */
	protected void selectChannelExplicitly(int index, boolean selected) {
		selectedChannelIndexes.clear();
		for (int j = 0; j < widgets.size(); j++) {
			ColorChannelWidget item = widgets.get(j);
			// perform selection changes
			if (index == j) {
				if (selected) {
					item.getChbxVisibility().setSelected(true);
					selectedChannelIndexes.add(j);
				} else {
					item.getChbxVisibility().setSelected(false);
					selectedChannelIndexes.remove((Integer) j);
				}

			} else {
				if (selected) {
					item.getChbxVisibility().setSelected(false);
					selectedChannelIndexes.remove((Integer) j);
				} else {
					item.getChbxVisibility().setSelected(true);
					selectedChannelIndexes.add(j);
				}
			}
		}
	}

	/**
	 * Get all selected channels in the context menu.
	 * 
	 * @return a list of selected indices
	 */
	public List<Integer> getSelectedChannels() {
		return selectedChannelIndexes;
	}

	protected ColorChannelWidget constructWidget(String name, int band) {
		ColorChannelWidget widget = new ColorChannelWidget();
		if (name == null) {
			name = I18N.getGUILabelText("channel.single.text") + " " + band;
		}
		widget.setChannelName(name);
		widget.setChannelIndex(band);

		JCheckBox chbxVisibility = widget.getChbxVisibility();
		chbxVisibility.setSelected(true);
		chbxVisibility.addActionListener(this);
		chbxVisibility.setActionCommand("channels");

		return widget;
	}

	/**
	 * Constructs or updates the channel menu according to the provided image in
	 * the look panel.
	 * 
	 * @return a reference to the menu
	 */
	public List<ColorChannelWidget> updateChannelWidgets() {
		logger.debug("Updating channel widgets...");
		// get the # of bands from the current image
		PlanarImage pi;
		try {
			setSelectiveChannelsDisplayed(false);
			widgets.clear();
			widgetContainer.removeAll();
			selectedChannelIndexes.clear();

			pi = displayPanel.getCurrentImage();
			// DEBUG ONLY
			// CommonTools.showImage(pi.getAsBufferedImage(),
			// "rendered img (colorchannel selector)");
			// System.out.println("Image OK");

			// determine the image type
			if (ImageAnalyzer.isIndexed(pi)) {
				ColorChannelWidget w = constructWidget("Index", 0);
				w.setToolTipText("The channels are not available for indexed color models.");
				w.getChbxVisibility().setSelected(false);
				widgets.add(0, w);
			} else if (ImageAnalyzer.isBinary(pi)) {
				ColorChannelWidget w = constructWidget("Binary", 0);
				w.setToolTipText("The channels are not available for binary images.");
				w.getChbxVisibility().setEnabled(false);
				widgets.add(0, w);
			} else if (ImageAnalyzer.is8BitGrey(pi)) {
				// construct one channel
				ColorChannelWidget w = constructWidget("Grey", 0);
				w.setIcon(new ImageIcon(Resources
						.getImageURL("icon.layers.grey")));
				widgets.add(0, w);
			} else if (ImageAnalyzer.isRGB(pi) && ImageAnalyzer.has3Bands(pi)) {
				ColorChannelWidget red = constructWidget("Red", 0);
				red.setIcon(new ImageIcon(Resources
						.getImageURL("icon.layers.red")));
				widgets.add(0, red);

				ColorChannelWidget green = constructWidget("Green", 1);
				green.setIcon(new ImageIcon(Resources
						.getImageURL("icon.layers.green")));
				widgets.add(1, green);

				ColorChannelWidget blue = constructWidget("Blue", 2);
				blue.setIcon(new ImageIcon(Resources
						.getImageURL("icon.layers.blue")));
				widgets.add(2, blue);
			} else if (ImageAnalyzer.isRGB(pi)
					&& ImageAnalyzer.hasAlphaBand(pi)
					&& ImageAnalyzer.has4Bands(pi)) {
				ColorChannelWidget red = constructWidget("Red", 0);
				red.setIcon(new ImageIcon(Resources
						.getImageURL("icon.layers.red")));
				widgets.add(0, red);

				ColorChannelWidget green = constructWidget("Green", 1);
				green.setIcon(new ImageIcon(Resources
						.getImageURL("icon.layers.green")));
				widgets.add(1, green);

				ColorChannelWidget blue = constructWidget("Blue", 2);
				blue.setIcon(new ImageIcon(Resources
						.getImageURL("icon.layers.blue")));
				widgets.add(2, blue);

				ColorChannelWidget alpha = constructWidget("Alpha", 3);
				alpha.setIcon(new ImageIcon(Resources
						.getImageURL("icon.layers.alpha")));
				widgets.add(3, alpha);
			}
			// in any other case simply create an anonymous menu item for
			// each band, without icon
			else {
				for (int band = 0; band < pi.getNumBands(); band++) {
					ColorChannelWidget w = constructWidget(null, band);
					widgets.add(band, w);
				}
			}

			// add the widgets to the GUI
			for (ColorChannelWidget c : widgets) {
				widgetContainer.add(c);
				selectedChannelIndexes.add((Integer) c.getChannelIndex());
			}

			// System.out.println("Selected channels=" + getSelectedChannels());

		} catch (NullPointerException e) {
			// System.out.println("No image loaded.");
			logger.debug("No image loaded yet.");
		}
		return widgets;
	}

	public PlanarImage getExtractedImage() {
		return extractedImage;
	}

	public void setExtractedImage(PlanarImage extractedImage) {
		this.extractedImage = extractedImage;
	}

	public boolean hasSelectiveChannelsDisplayed() {
		return hasSelectiveChannelsDisplayed;
	}

	public void setSelectiveChannelsDisplayed(
			boolean hasSelectiveChannelsDisplayed) {
		this.hasSelectiveChannelsDisplayed = hasSelectiveChannelsDisplayed;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("channels".equals(e.getActionCommand())) {

			int modifiers = e.getModifiers();
			boolean alt = CommonTools.checkModifiers(modifiers,
					ActionEvent.ALT_MASK);
			boolean shift = CommonTools.checkModifiers(modifiers,
					ActionEvent.SHIFT_MASK);
			JCheckBox source = (JCheckBox) e.getSource();
			ColorChannelWidget activeWidget = null;
			int index = 0;
			// search for the clicked index
			for (int i = 0; i < widgets.size(); i++) {
				if (widgets.get(i).getChbxVisibility() == source) {
					activeWidget = widgets.get(i);
					index = i;
					break;
				}
			}
			if (selectedChannelIndexes.size() == 1
					&& selectedChannelIndexes.contains((Integer) index) && (!alt && !shift)) {
				activeWidget.getChbxVisibility().setSelected(true);
				return;
			}

			

			logger.debug("Clicked on: " + index
					+ (alt ? " with ALT modifier" : "")
					+ (shift ? ", with SHIFT modifier" : ""));

			if (alt && !shift) {
				selectChannelExplicitly(index, true);
			}

			if (alt && shift) {
				selectChannelExplicitly(index, false);
			}

			if (!alt && !shift) {
				for (int i = 0; i < widgets.size(); i++) {
					if (widgets.get(i).getChbxVisibility() == source) {
						if (source.isSelected()) {
							selectedChannelIndexes.add((Integer) i);
						} else {
							selectedChannelIndexes.remove((Integer) i);
						}
					}
				}
			}

			// get all selected indices
			List<Integer> allSelectedIdxs = getSelectedChannels();

			if (allSelectedIdxs.size() == 0) {
				return;
			}

			PlanarImage currentImage = displayPanel.getCurrentImage();
			if (allSelectedIdxs.size() == currentImage.getNumBands()) {
				// display the original image and do not calculate anything
				this.extractedImage = currentImage;
				this.setSelectiveChannelsDisplayed(false);
			} else {
				// extract/merge the channels
				this.extractedImage = ImageTools.extractChannels(
						displayPanel.getCurrentImage(), allSelectedIdxs);
				this.setSelectiveChannelsDisplayed(true);
			}
			// DEBUG ONLY
			// CommonTools.showImage(extractedImage.getAsBufferedImage(),
			// "extracted "
			// + allSelectedIdxs);

			// repaint the zoomed image
			displayPanel.updateImageLayer();
		}
	}

}
