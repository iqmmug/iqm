package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ROILayerSelectorPanel.java
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.util.ROILayerWidget;
import at.mug.iqm.commons.util.CommonTools;

/**
 * This panel represents all control elements for a stack of ROI layers.
 * 
 * @author Philipp Kainz
 * 
 */
public class ROILayerSelectorPanel extends JPanel {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -8804474889622759852L;

	/**
	 * A custom class logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ROILayerSelectorPanel.class);

	// control elements
	private JLabel lblAdd;
	private JLabel lblRemove;
	private JLabel lblMoveUp;
	private JLabel lblMoveDown;

	/**
	 * A button group for all radio buttons.
	 */
	private ButtonGroup selectorGroup;

	/**
	 * The {@link ILookPanel} managed by this instance.
	 */
	private ILookPanel displayPanel;

	/**
	 * An ordered list of {@link ROILayerWidget}s. Greater indices indicate
	 * greater z-order.
	 */
	private ArrayList<ROILayerWidget> widgets;

	/**
	 * The currently selected layer index.
	 */
	private int selectedLayerIndex = Integer.MAX_VALUE;

	/**
	 * The currently selected layer.
	 */
	private IDrawingLayer currentLayer;

	/**
	 * The currently selected widget.
	 */
	private ROILayerWidget currentWidget;

	/**
	 * All layers of the managed {@link ILookPanel}. The {@link Integer} is the
	 * z-order of the corresponding layer.
	 */
	private ArrayList<IDrawingLayer> layers;

	/**
	 * A list of all existing ROI layers.
	 */
	private JLayeredPane layerPane;
	/**
	 * A container for the control elements.
	 */
	private Box selectorContainer;
	private JLabel lblHideAll;
	private JLabel lblShowAll;

	/**
	 * Create the panel.
	 */
	public ROILayerSelectorPanel() {
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBackground(Color.LIGHT_GRAY);
		scrollPane.setBorder(new EmptyBorder(5, 1, 1, 1));
		scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		add(scrollPane);

		JPanel pnlSelectors = new JPanel();
		scrollPane.setViewportView(pnlSelectors);
		pnlSelectors.setLayout(new BorderLayout(0, 0));

		selectorContainer = Box.createVerticalBox();
		pnlSelectors.add(selectorContainer, BorderLayout.NORTH);

		JPanel northPanel = new JPanel();
		add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new BorderLayout(0, 0));

		JPanel buttonPanel = new JPanel();
		northPanel.add(buttonPanel, BorderLayout.CENTER);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		lblAdd = new JLabel();
		lblAdd.setToolTipText(I18N.getGUILabelText("roi.layer.add.ttp"));
		lblAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				createLayer();
			}
		});
		lblAdd.setBorder(new EmptyBorder(2, 2, 2, 2));
		buttonPanel.add(lblAdd);
		lblAdd.setIcon(new ImageIcon(Resources
				.getImageURL("icon.roi.layers.add")));

		lblRemove = new JLabel();
		lblRemove.setToolTipText(I18N.getGUILabelText("roi.layer.remove.ttp"));
		lblRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				removeLayer(selectedLayerIndex);
			}
		});
		lblRemove.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblRemove.setIcon(new ImageIcon(Resources
				.getImageURL("icon.roi.layers.remove")));
		buttonPanel.add(lblRemove);

		Component horizontalStrut = Box.createHorizontalStrut(15);
		buttonPanel.add(horizontalStrut);

		lblMoveUp = new JLabel();
		lblMoveUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				moveLayerUp();
			}
		});
		lblMoveUp.setToolTipText(I18N.getGUILabelText("roi.layer.move.up.ttp"));
		lblMoveUp.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblMoveUp.setIcon(new ImageIcon(Resources
				.getImageURL("icon.roi.layers.move.up")));
		buttonPanel.add(lblMoveUp);

		lblMoveDown = new JLabel();
		lblMoveDown.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				moveLayerDown();
			}
		});
		lblMoveDown.setToolTipText(I18N
				.getGUILabelText("roi.layer.move.down.ttp"));
		lblMoveDown.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblMoveDown.setIcon(new ImageIcon(Resources
				.getImageURL("icon.roi.layers.move.down")));
		buttonPanel.add(lblMoveDown);

		Component horizontalStrut_1 = Box.createHorizontalStrut(15);
		buttonPanel.add(horizontalStrut_1);

		lblHideAll = new JLabel();
		lblHideAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				for (IDrawingLayer layer : layers) {
					if (layer.isLayerVisible()) {
						hideAllLayers();
						break;
					}
				}
			}
		});
		lblHideAll.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblHideAll.setIcon(new ImageIcon(Resources
				.getImageURL("icon.roi.layers.all.hide")));
		lblHideAll.setToolTipText(I18N
				.getGUILabelText("roi.layer.all.hide.ttp"));
		buttonPanel.add(lblHideAll);

		lblShowAll = new JLabel();
		lblShowAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				for (IDrawingLayer layer : layers) {
					if (!layer.isLayerVisible()) {
						showAllLayers();
						break;
					}
				}
			}
		});
		lblShowAll.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblShowAll.setIcon(new ImageIcon(Resources
				.getImageURL("icon.roi.layers.all.show")));
		lblShowAll.setToolTipText(I18N
				.getGUILabelText("roi.layer.all.show.ttp"));
		buttonPanel.add(lblShowAll);

		Component horizontalGlue = Box.createHorizontalGlue();
		buttonPanel.add(horizontalGlue);

		JPanel headerPanel = new JPanel();
		headerPanel.setBorder(new EmptyBorder(1, 1, 1, 1));
		northPanel.add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel lblRoiLayers = new JLabel(
				I18N.getGUILabelText("roi.layer.header"));
		lblRoiLayers.setHorizontalAlignment(SwingConstants.CENTER);
		lblRoiLayers.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblRoiLayers.setIcon(new ImageIcon(Resources
				.getImageURL("icon.roi.layers.default")));
		lblRoiLayers.setOpaque(true);
		lblRoiLayers.setBackground(Color.WHITE);
		headerPanel.add(lblRoiLayers);
	}

	/**
	 * Generate a new layer selection panel for the given look panel in order to
	 * manage the ROIs.
	 * 
	 * @param lookPanel
	 */
	public ROILayerSelectorPanel(ILookPanel lookPanel) {
		this();
		this.displayPanel = lookPanel;
		// get the layer pane from the display panel
		this.layerPane = displayPanel.getLayerPane();

		// initialize the component (completely empty)
		initialize();
	}

	/**
	 * Hides all layers from the display.
	 */
	public void hideAllLayers() {
		for (IDrawingLayer layer : layers) {
			widgets.get(layers.indexOf(layer)).getChckbxVisible()
					.setSelected(false);
			layer.setLayerVisible(false);
		}
		layerPane.repaint();
	}

	/**
	 * Shows all layers from the display.
	 */
	public void showAllLayers() {
		for (IDrawingLayer layer : layers) {
			widgets.get(layers.indexOf(layer)).getChckbxVisible()
					.setSelected(true);
			layer.setLayerVisible(true);
		}
		layerPane.repaint();
	}

	/**
	 * This method selects the layer at the specified index and de-selects all
	 * other layers.
	 * 
	 * @param selectedIndex
	 *            the z-order of the layer
	 * @return the highlighted widget at the specified position
	 */
	protected ROILayerWidget highlightWidget(int selectedIndex) {
		// run through all layer selectors and de-select all but the selected
		// index
		for (IDrawingLayer layer : layers) {
			int zOrder = layer.getZOrder();
			ROILayerWidget ls = widgets.get(zOrder);
			if (selectedIndex == zOrder) {
				ls.select();
				// request the focus on the radio button
				// avoids flickering
				ls.getRdbtnActive().requestFocus();
				selectedLayerIndex = zOrder;
				currentWidget = ls;
			} else {
				ls.deselect();
			}
		}
		return currentWidget;
	}

	/**
	 * Remove the selector widget at a corresponding zOrder from the container.
	 * 
	 * @param zOrder
	 * @param immediateRepaint
	 */
	protected void removeWidget(int zOrder, boolean immediateRepaint) {
		// determine the selected layer by index from the widget hash map
		selectorContainer.remove(currentWidget);
		widgets.remove(currentWidget);

		// re-organize the layer z-order
		reIndexLayers();

		selectorContainer.revalidate();
		if (immediateRepaint)
			selectorContainer.repaint();
	}

	/**
	 * Creates the default layer (0).
	 */
	public IDrawingLayer createDefaultLayer() {
		if (this.layers.size() == 0) {
			return this.addLayer("default", true);
		} else {
			return null;
		}
	}

	/**
	 * Creates a new layer to draw on.
	 * 
	 * @return a new layer on top of all others
	 */
	public IDrawingLayer createLayer() {
		return this.addLayer(null, true);
	}

	/**
	 * Creates a new layer with a given name to draw on.
	 * 
	 * @return a new layer on top of all others, identified by name
	 */
	public IDrawingLayer createLayer(String name) {
		return this.addLayer(name, true);
	}

	/**
	 * Entirely removes all layers and creates a new default layer.
	 */
	public IDrawingLayer resetLayers() {
		for (int i = 0; i < layers.size(); i++) {
			IDrawingLayer layer = layers.get(i);
			cleanUp(layer.getZOrder());
		}

		// remove all layers and add a new default layer
		return createDefaultLayer();
	}

	/**
	 * (Re)-initializes the component.
	 */
	private void initialize() {
		currentLayer = null;
		currentWidget = null;

		layers = new ArrayList<>(10);
		widgets = new ArrayList<>(10);

		selectedLayerIndex = Integer.MAX_VALUE;

		selectorGroup = new ButtonGroup();

		selectorContainer.removeAll();
		selectorContainer.validate();
	}

	/**
	 * Removes all instances of {@link IDrawingLayer} ayers from the {@link JLayeredPane}.
	 */
	private void cleanDrawingLayersFromPane() {
		Component[] cArray = this.layerPane.getComponents();
		// start with 1, since the bounds of the image layer have already been
		// set.
		for (Component c : cArray) {
			if (c instanceof IDrawingLayer) {
				this.layerPane.remove(c);
			}
		}
	}

	/**
	 * Replaces all layers with a new list of layers.
	 * 
	 * @param layers
	 *            the list of new layers, if <code>null</code> the list will be
	 *            replaced by a single default layer
	 * @return a reference to the active drawing layer
	 */
	public IDrawingLayer replaceLayers(List<IDrawingLayer> layers) {

		// remove all present widgets and layers
		initialize();
		
		// remove all layers from the layerpane
		cleanDrawingLayersFromPane();

		// rebuild the layers
		this.layers = (ArrayList<IDrawingLayer>) layers;

		// return the selected layer
		IDrawingLayer activeLayer = null;

		// create new layer widgets for each instance in the list
		for (int idx = 0; idx < layers.size(); idx++) {
			// get the current layer
			final DefaultDrawingLayer layer = (DefaultDrawingLayer) layers
					.get(idx);

			logger.debug("Adding layer: " + layer.getName());

			// bind the layer to a specific display panel
			layer.setDisplayPanel(this.displayPanel);
			layer.initialize();

			if (layer.isSelected()) {
				activeLayer = layer;
			}

			// get the current bounds of this layer
			ImageLayer imgLayer = displayPanel.getImageLayer();
			Rectangle layerBounds = null;
			try {
				layerBounds = new Rectangle(imgLayer.getOrigin().x,
						imgLayer.getOrigin().y,
						imgLayer.getSource().getWidth(), imgLayer.getSource()
								.getHeight());
			} catch (NullPointerException ex) {
				logger.warn("There is no image loaded yet. " + ex);
				layerBounds = new Rectangle(0, 0, 0, 0);
			}
			layer.setBounds(layerBounds);

			// add it to the layered pane
			layerPane.add(layer, new Integer(layer.getZOrder() + 1));

			// generate new widget control element and set the initial values
			ROILayerWidget widget = new ROILayerWidget(layer);
			widget.getRdbtnActive().addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					selectLayer(layer.getZOrder());
				}
			});
			// set the name to be displayed
			widget.setLayerName(layer.getName());
			// set the zOrder to be displayed
			widget.setZOrder(layer.getZOrder());
			// active status
			JRadioButton btnActive = widget.getRdbtnActive();
			btnActive.setSelected(layer.isSelected());

			// visibility
			JCheckBox cbxVisible = widget.getChckbxVisible();
			cbxVisible.setSelected(layer.isLayerVisible());

			// add the button to the group
			selectorGroup.add(btnActive);

			// add the widget to the top of the list
			selectorContainer.add(widget, 0);

			// request the focus on the radio button
			btnActive.requestFocus();

			// add it to the control element array list
			widgets.add(layer.getZOrder(), widget);
		}

		// re-index the layers
		reIndexLayers();

		// revalidate the container and rebuild indices
		selectorContainer.revalidate();

		// select and highlight the new layer
		selectLayer(activeLayer.getZOrder());

		// repaint the component
		selectorContainer.repaint();

		logger.debug("layers.size: " + layers.size());
		logger.debug("layerSelectors.size: " + widgets.size());
		logger.debug("selectorContainer.components: "
				+ selectorContainer.getComponentCount());
		logger.debug("layeredPane.components: " + layerPane.getComponentCount());

		return activeLayer;
	}

	/**
	 * Add a layer widget to the end of the layer list (on top of all others).
	 * 
	 * @param name
	 *            the name of the layer
	 * @param immediateRepaint
	 *            a flag whether the GUI shall be repainted immediately after
	 *            adding the layer widget
	 * @return a reference to the latest generated layer
	 */
	protected IDrawingLayer addLayer(String name, boolean immediateRepaint) {
		// get new layer index
		int zOrder = layers.size();
		logger.debug("Adding layer: " + name);

		// generate new layer
		final TransparentROILayer layer = new TransparentROILayer(displayPanel);
		layer.setZOrder(zOrder);
		if (name == null) {
			name = "Layer " + zOrder;
		}
		layer.setName(name);
		layer.setLayerVisible(true); // is already default
		layer.setSelected(true); // is already default

		// get the current bounds of this layer
		ImageLayer imgLayer = displayPanel.getImageLayer();
		Rectangle layerBounds = null;
		try {
			layerBounds = new Rectangle(imgLayer.getOrigin().x,
					imgLayer.getOrigin().y, imgLayer.getSource().getWidth(),
					imgLayer.getSource().getHeight());
		} catch (NullPointerException ex) {
			logger.warn("There is no image loaded yet. " + ex);
			layerBounds = new Rectangle(0, 0, 0, 0);
		}
		layer.setBounds(layerBounds);
		// add it to the layer list
		layers.add(zOrder, layer);

		// add it to the layered pane
		layerPane.add(layer, new Integer(zOrder + 1));

		// generate new widget control element and set the initial values
		ROILayerWidget widget = new ROILayerWidget(layer);
		widget.getRdbtnActive().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectLayer(layer.getZOrder());
			}
		});
		// set the name to be displayed
		widget.setLayerName(name);
		// set the zOrder to be displayed
		widget.setZOrder(zOrder);
		// add the button to the group
		selectorGroup.add(widget.getRdbtnActive());

		// add the widget to the top of the list
		selectorContainer.add(widget, 0);

		// request the focus on the radio button
		widget.getRdbtnActive().requestFocus();

		// add it to the control element array list
		widgets.add(zOrder, widget);

		// revalidate the container and rebuild indices
		selectorContainer.revalidate();

		// re-organize the layer z-order
		reIndexLayers();

		// select and highlight the new layer
		selectLayer(zOrder);

		// repaint the component
		if (immediateRepaint)
			selectorContainer.repaint();

		logger.debug("layers.size: " + layers.size());
		logger.debug("layerSelectors.size: " + widgets.size());
		logger.debug("selectorContainer.components: "
				+ selectorContainer.getComponentCount());
		logger.debug("layeredPane.components: " + layerPane.getComponentCount());

		return layer;
	}

	/**
	 * Selects and highlights the layer at the given <code>zOrder</code>
	 * position. The current layer is set to the selected one.
	 * 
	 * @param zOrder
	 *            the position of the layer in the stack
	 * @return the layer located at the given position
	 * @throws NoSuchElementException
	 *             if the zOrder is not within the stack
	 */
	public IDrawingLayer selectLayer(int zOrder) {
		if (zOrder > layers.size())
			throw new NoSuchElementException(
					"There is no such layer with zOrder [" + zOrder + "].");
		// set widget selected, return the layer currently associated with
		// the given zOrder
		highlightWidget(zOrder);
		DefaultDrawingLayer layer = (DefaultDrawingLayer) layers.get(zOrder);
		this.currentLayer = layer;

		// bring the current layer to front
		setLayerToFront(layer);

		return layer;
	}

	/**
	 * Get a layer by the its z-order. Getting the element by this method does
	 * not select the layer until it is not present and is being created.
	 * Furthermore, it does not change the current layer variable, i.e.
	 * {@link #getCurrentLayer()} does not return the same layer as this method.
	 * 
	 * @param zOrder
	 *            the index of the layer
	 * @return the layer, if it exists, or a new layer on top of all others
	 *         otherwise
	 */
	public IDrawingLayer getLayer(int zOrder) {
		if (zOrder < layers.size()) {
			logger.debug("Getting layer [" + zOrder + "].");
			return layers.get(zOrder);
		} else {
			logger.debug("Layer [" + zOrder
					+ "] is not accessible, creating a new one.");
			return this.createLayer();
		}
	}

	/**
	 * Removes a specified layer from the list.
	 * 
	 * @param zOrder
	 *            the index of the layer
	 */
	public void removeLayer(int zOrder) {
		if (layers.size() == 1) {
			logger.info("There is just one layer left, which will not be removed.");
			return;
		}
		if (zOrder < layers.size()) {
			logger.debug("Attempting to remove layer [" + zOrder
					+ "] and corresponding widget.");

			cleanUp(zOrder);

			if (layers.size() == 1 || zOrder == 0) {
				// select the first layer
				selectLayer(0);
			} else {
				selectLayer(zOrder - 1);
			}
		} else {
			logger.debug("No layer registered for z-order [" + zOrder + "]!");
		}

		logger.debug("layers.size: " + layers.size());
		logger.debug("layerSelectors.size: " + widgets.size());
		logger.debug("selectorContainer.components: "
				+ selectorContainer.getComponentCount());
		logger.debug("layeredPane.components: " + layerPane.getComponentCount());
	}

	/**
	 * Cleans all traces for a layer according to the given z-order.
	 * 
	 * @param zOrder
	 */
	private void cleanUp(int zOrder) {
		DefaultDrawingLayer layerForRemoval = (DefaultDrawingLayer) layers
				.get(zOrder);

		// remove the corresponding layer from the pane
		layerPane.remove(layerForRemoval);

		// remove the layer itself
		layers.remove(zOrder);

		// remove the GUI element
		removeWidget(zOrder, true);
	}

	/**
	 * Moves the selected layer one position downwards. This action has no
	 * effect, if the bottommost element is selected.
	 */
	public void moveLayerDown() {
		if (layers.isEmpty() || selectedLayerIndex == 0)
			return;

		int target = selectedLayerIndex - 1;

		selectorContainer.removeAll();
		selectorContainer.validate();
		swapLayers(selectedLayerIndex, target);

		// reverse add the elements
		for (int i = widgets.size() - 1; i >= 0; i--) {
			selectorContainer.add(widgets.get(i));
		}

		reIndexLayers();
		selectLayer(target);

		selectorContainer.revalidate();
		selectorContainer.repaint();
	}

	/**
	 * Moves the selected layer one position upwards. This action has no effect,
	 * if the topmost element is selected.
	 */
	public void moveLayerUp() {
		if (layers.isEmpty() || (selectedLayerIndex == (widgets.size() - 1)))
			return;

		int target = selectedLayerIndex + 1;

		selectorContainer.removeAll();
		selectorContainer.validate();
		swapLayers(selectedLayerIndex, target);

		// reverse add the elements
		for (int i = widgets.size() - 1; i >= 0; i--) {
			selectorContainer.add(widgets.get(i));
		}

		reIndexLayers();
		selectLayer(target);

		selectorContainer.revalidate();
		selectorContainer.repaint();
	}

	/**
	 * Swap the source index with the destination index for the widgets and the
	 * layers.
	 * 
	 * @param sourceIndex
	 * @param destinationIndex
	 */
	public void swapLayers(int sourceIndex, int destinationIndex) {
		Collections.swap(widgets, sourceIndex, destinationIndex);
		Collections.swap(layers, sourceIndex, destinationIndex);
	}

	/**
	 * Get a list of all layers.
	 * 
	 * @return a list of all layers
	 */
	public List<IDrawingLayer> getLayers() {
		return layers;
	}

	/**
	 * Gets the currently selected layer.
	 * 
	 * @return the current layer
	 */
	public IDrawingLayer getCurrentLayer() {
		return currentLayer;
	}

	/**
	 * Gets the reference to the look panel, this selector widget is associated
	 * with.
	 * 
	 * @return the look panel
	 */
	public ILookPanel getLookPanel() {
		return displayPanel;
	}

	/**
	 * Re-indexes the layers with running numbers as z-orders.
	 */
	protected void reIndexLayers() {
		// rearrange the z-order from bottom to top
		// the layers are already ordered without missing elements in the
		// array list
		for (int i = 0; i < layers.size(); i++) {
			ROILayerWidget ls = widgets.get(i);
			// calling setZorder on the selector also sets the layer
			ls.setZOrder(i);

			// set layers in the layered pane, leaving out the initial layer
			// because this contains the image!
			if (i != 0) {
				DefaultDrawingLayer layer = (DefaultDrawingLayer) layers
						.get(i - 1);
				layerPane.setLayer(layer, new Integer(i), 0);
			}
		}
	}

	/**
	 * Gets the image layer, which is located below all ROI layers.
	 * 
	 * @return the image layer instance
	 */
	public ImageLayer getImageLayer() {
		return this.displayPanel.getImageLayer();
	}

	/**
	 * Get the layered pane of the managed look panel.
	 * 
	 * @return the {@link JLayeredPane}
	 */
	public JLayeredPane getLayerPane() {
		return layerPane;
	}

	/**
	 * Set the layered pane.
	 * 
	 * @param layerPane
	 */
	public void setLayerPane(JLayeredPane layerPane) {
		this.layerPane = layerPane;
	}

	/**
	 * Sets the layer to front and repaints the active layer.
	 * 
	 * @param layer
	 */
	public void setLayerToFront(DefaultDrawingLayer layer) {
		layerPane.setLayer(layer, IDrawingLayer.ACTIVE_FRONT_LAYER, 0);
		layer.requestFocusInWindow();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().add(new ROILayerSelectorPanel());
				f.setPreferredSize(new Dimension(190, 400));
				f.pack();
				CommonTools.centerFrameOnScreen(f);
				f.setVisible(true);
			}
		});
	}
}
