package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ILookPanel.java
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;

import at.mug.iqm.api.gui.util.ZoomHelper;

/**
 * This interface represents an image canvas.
 * 
 * @author Philipp Kainz
 * 
 */
public interface ILookPanel extends ActionListener {

	/**
	 * Gets the layer container of the panel.
	 * 
	 * @return the {@link JLayeredPane}
	 */
	JLayeredPane getLayerPane();

	/**
	 * Gets all ROI layers.
	 * 
	 * @return a {@link List} of {@link IDrawingLayer}
	 */
	List<IDrawingLayer> getROILayers();

	/**
	 * Gets the currently selected ROI layer.
	 * 
	 * @return the {@link IDrawingLayer}
	 */
	IDrawingLayer getCurrentROILayer();

	// LEGACY METHODS OF ILOOKPANEL
	/**
	 * Set the additional text for the {@link ImageInfoPanel}.
	 * 
	 * @param additionalText
	 */
	void setAdditionalText(String additionalText);

	/**
	 * Gets the additional text of the {@link ImageInfoPanel}.
	 * 
	 * @return the additional text
	 */
	String getAdditionalText();

	/**
	 * This method calculates and sets the offset of the view Mainly for zooming
	 * the image with the mouse
	 * 
	 * @param p
	 *            a {@link Point} representing pixel values in the real image
	 */
	void setViewOffset(Point p);

	/**
	 * Set the zoom of the image.
	 * 
	 * @param zoom
	 */
	void setZoom(double zoom);

	/**
	 * Get the current zoom of the image.
	 * 
	 * @return the current zoom
	 */
	double getZoom();

	/**
	 * This method allows external classes access to the pixel info which was
	 * obtained in the mouseMoved method.
	 * 
	 * @return the pixel information, formatted as a string
	 */
	String getPixelInfoString();

	/**
	 * Set the pixel information.
	 * 
	 * @param pixelInfo
	 */
	void setPixelInfo(StringBuffer pixelInfo);

	/**
	 * Get the pixel information.
	 * 
	 * @return the pixel information as string
	 */
	StringBuffer getPixelInfo();

	/**
	 * Set the integer pixel values in an array.
	 * 
	 * @param intPixelValue
	 */
	void setIntPixelValue(int[] intPixelValue);

	/**
	 * Set the float pixel values in an array.
	 * 
	 * @param floatPixelValue
	 */
	void setFloatPixelValue(float[] floatPixelValue);

	/**
	 * Set the double pixel values in an array.
	 * 
	 * @param doublePixelValue
	 */
	void setDoublePixelValue(double[] doublePixelValue);

	/**
	 * Set the lookup table.
	 * 
	 * @param lut
	 */
	void setLut(short[][] lut);

	/**
	 * Set a flag whether or not the currently displayed image is indexed.
	 * 
	 * @param isIndexed
	 */
	void setIndexed(boolean isIndexed);

	/**
	 * Sets the current image (original).
	 * 
	 * @param currentImage
	 */
	void setCurrentImage(PlanarImage currentImage);

	/**
	 * Gets the current image (original).
	 * 
	 * @return the current image in original dimensions (no zoom)
	 */
	PlanarImage getCurrentImage();

	/**
	 * Set the rendered/processed image.
	 * 
	 * @param rendImage
	 */
	void setRenderedImage(PlanarImage rendImage);

	/**
	 * Get the rendered/processed image with current zoom factor.
	 * 
	 * @return the rendered/processed image
	 */
	PlanarImage getRenderedImage();

	/**
	 * Get the pixel intensity values as integer array.
	 * 
	 * @return an int array of intensities
	 */
	int[] getIntPixelValue();

	/**
	 * Get the pixel intensity values as float array.
	 * 
	 * @return a float array of intensities
	 */
	float[] getFloatPixelValue();

	/**
	 * Get the pixel intensity values as double array.
	 * 
	 * @return a double array of intensities
	 */
	double[] getDoublePixelValue();

	/**
	 * Gets the lookup table for the indexed image.
	 * 
	 * @return a 2D short array
	 */
	short[][] getLut();

	/**
	 * Get a flag, whether or not the image is indexed.
	 * 
	 * @return <code>true</code> if the image is indexed, <code>false</code>
	 *         otherwise
	 */
	boolean isIndexed();

	/**
	 * Set the image data buffer type.
	 * 
	 * @param imgDataBufferType
	 */
	void setImgDataBufferType(String imgDataBufferType);

	/**
	 * Get the image data buffer type.
	 * 
	 * @return a string representing the data buffer type
	 */
	String getImgDataBufferType();

	/**
	 * Set the image height.
	 * 
	 * @param imgHeight
	 */
	void setImageHeight(int imgHeight);

	/**
	 * Set the image width.
	 * 
	 * @param imgWidth
	 */
	void setImageWidth(int imgWidth);

	/**
	 * Gets the width of the original image in pixels.
	 * 
	 * @return the image height in pixels
	 */
	int getImageHeight();

	/**
	 * Gets the width of the original image in pixels.
	 * 
	 * @return the image width in pixels
	 */
	int getImageWidth();

	/**
	 * Destroys the GUI.
	 * 
	 * @throws Exception
	 */
	void destroyGUI() throws Exception;

	/**
	 * Get the tool box of the panel.
	 * 
	 * @return the tool box panel
	 */
	LookToolboxPanel getToolboxPanel();

	/**
	 * Get the context menu popup listener for showing pop up menus.
	 * 
	 * @return a {@link ContextPopupListener}
	 */
	ContextPopupListener getContextPopupListener();

	/**
	 * Get the property change support for this panel.
	 * 
	 * @return a {@link PropertyChangeSupport}
	 */
	PropertyChangeSupport getPcs();

	/**
	 * Gets the image layer of this panel.
	 * 
	 * @return the image layer painting the rendered image
	 */
	ImageLayer getImageLayer();

	/**
	 * A flag, determining whether or not the panel is currently displaying an
	 * image.
	 * 
	 * @return <code>true</code>, if no image is displayed, <code>false</code> otherwise
	 */
	boolean isEmpty();

	/**
	 * Gets the image info panel.
	 * 
	 * @return the {@link ImageInfoPanel}
	 */
	ImageInfoPanel getImageInfoPanel();

	/**
	 * Gets the valid zoom stages for the look panel.
	 * 
	 * @return the {@link ZoomHelper}
	 */
	ZoomHelper getZoomHelper();

	/**
	 * Updates the layer bounds to new dimensions in the layered pane.
	 * 
	 * @param dim
	 *            the dimension
	 */
	void updateLayerBounds(Dimension dim);

	/**
	 * Gets the scroll pane of the current look panel.
	 * 
	 * @return the {@link JScrollPane}
	 */
	JScrollPane getScrollPane();

	/**
	 * Updates the image layer, i.e. sets the new source image to be painted and
	 * repaints the GUI.
	 */
	void updateImageLayer();

	/**
	 * Deletes the selected ROI on the current ROI layer.
	 */
	void deleteSelectedROI();

	/**
	 * Deletes all ROI shapes on the current ROI layer.
	 */
	void deleteAllROIShapesOnLayer();

	/**
	 * Deletes all ROI shapes on every layer.
	 */
	void deleteAllROIShapes();

	/**
	 * Gets the selection panel for the ROI layers.
	 * 
	 * @return the {@link ROILayerSelectorPanel}
	 */
	ROILayerSelectorPanel getROILayerSelectorPanel();

	/**
	 * Initializes the look panel. <br>
	 * <b>IMPORTANT NOTE</b>: This method is mandatory to be called after the
	 * object has been constructed.
	 */
	void initialize();

	/**
	 * Gets the selector panel for color components.
	 * 
	 * @return the {@link ColorChannelSelectorPanel}
	 */
	ColorChannelSelectorPanel getColorChannelSelectorPanel();

	/**
	 * Updates the image navigator according to the current viewport position
	 * and size.
	 */
	void updateNavigator();
}
