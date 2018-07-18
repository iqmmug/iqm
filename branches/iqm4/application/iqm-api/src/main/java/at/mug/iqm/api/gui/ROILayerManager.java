package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ROILayerManager.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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

import java.util.ArrayList;
import java.util.List;

import at.mug.iqm.api.Application;

/**
 * This class provides static methods for the management of ROI layers in the
 * current look panel.
 * 
 * @author Philipp Kainz
 * 
 */
public final class ROILayerManager {

	/**
	 * Creates a new layer with a given name for the current layer name of the
	 * current look panel.
	 * 
	 * @param name
	 * @return a new {@link DefaultDrawingLayer}
	 */
	public static DefaultDrawingLayer createLayer(String name) {
		return (DefaultDrawingLayer) Application.getLook()
				.getCurrentLookPanel().getROILayerSelectorPanel()
				.createLayer(name);
	}

	/**
	 * Creates a layer with a default name of the current look panel.
	 * 
	 * @return a new default layer
	 */
	public static DefaultDrawingLayer createLayer() {
		return (DefaultDrawingLayer) Application.getLook()
				.getCurrentLookPanel().getROILayerSelectorPanel().createLayer();
	}

	/**
	 * Remove the layer of a given z-order of the current look panel.
	 * 
	 * @param zOrder
	 */
	public static void removeLayer(int zOrder) {
		Application.getLook().getCurrentLookPanel().getROILayerSelectorPanel()
				.removeLayer(zOrder);
	}

	/**
	 * Get the layer of a given z-order of the current look panel.
	 * 
	 * @param zOrder
	 * @return the {@link DefaultDrawingLayer}
	 */
	public static DefaultDrawingLayer getLayer(int zOrder) {
		return (DefaultDrawingLayer) Application.getLook()
				.getCurrentLookPanel().getROILayerSelectorPanel()
				.getLayer(zOrder);
	}

	/**
	 * Gets the currently active ROI layer of the current look panel.
	 * 
	 * @return the active {@link DefaultDrawingLayer}
	 */
	public static DefaultDrawingLayer getCurrentROILayer() {
		return (DefaultDrawingLayer) Application.getLook()
				.getCurrentLookPanel().getCurrentROILayer();
	}

	/**
	 * Gets all visible layers of the current look panel.
	 * 
	 * @return a {@link List}
	 */
	public static List<IDrawingLayer> getVisibleLayers() {
		List<IDrawingLayer> visLayers = new ArrayList<IDrawingLayer>();

		for (IDrawingLayer layer : Application.getLook().getCurrentLookPanel()
				.getROILayers()) {
			if (layer.isLayerVisible()) {
				visLayers.add(layer);
			}
		}
		return visLayers;
	}

	/**
	 * Gets all visible layers of the current look panel for saving. The z-order
	 * will be replaced by a running index, starting with the lowest visible layer at 0.
	 * 
	 * @return a {@link List}
	 */
	public static List<IDrawingLayer> getVisibleLayersForExport() {
		List<IDrawingLayer> visLayers = new ArrayList<IDrawingLayer>();

		int zOrderNew = 0;
		for (IDrawingLayer layer : Application.getLook().getCurrentLookPanel()
				.getROILayers()) {
			if (layer.isLayerVisible()) {
				layer.setZOrder(zOrderNew);
				visLayers.add(layer);
				zOrderNew++;
			}
		}
		return visLayers;
	}

	/**
	 * Gets a {@link List} of all layers of the current look panel.
	 * 
	 * @return a {@link List} of all layers
	 */
	public static List<DefaultDrawingLayer> getAllLayers() {
		List<DefaultDrawingLayer> allLayers = new ArrayList<DefaultDrawingLayer>();

		for (IDrawingLayer layer : Application.getLook().getCurrentLookPanel()
				.getROILayers()) {
			allLayers.add((DefaultDrawingLayer) layer);
		}
		return allLayers;
	}

	/**
	 * This method replaces all layers of the current look panel with the given
	 * list. If the list is <code>null</code>, a new default layer will be
	 * created.
	 * 
	 * @param layers
	 *            the list of layers which will replace the current layers
	 * @return the reference to the active list
	 */
	public static IDrawingLayer replaceLayers(List<IDrawingLayer> layers) {
		ILookPanel curPanel = Application.getLook().getCurrentLookPanel();
		ROILayerSelectorPanel rlsp = curPanel.getROILayerSelectorPanel();

		return rlsp.replaceLayers(layers);
	}

}
