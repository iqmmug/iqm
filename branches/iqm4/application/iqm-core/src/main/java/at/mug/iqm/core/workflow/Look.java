package at.mug.iqm.core.workflow;

/*
 * #%L
 * Project: IQM - Application Core
 * File: Look.java
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
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.ArrayList;

import javax.media.jai.PlanarImage;
import javax.swing.JComponent;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.ILookPanel;
import at.mug.iqm.api.gui.ImageLayer;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.api.workflow.ILook;
import at.mug.iqm.commons.util.image.ImageAnalyzer;
import at.mug.iqm.core.I18N;
import at.mug.iqm.gui.LookExtraFrame;
import at.mug.iqm.gui.LookPanel;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class is a accessor for the current {@link LookPanel} and its containing
 * {@link ImageLayer}.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * 
 */
public final class Look implements ILook {

	/**
	 * Custom class logger.
	 */
	private final Logger logger = Logger.getLogger(Look.class);

	/**
	 * An {@link ArrayList} of {@link LookPanel}s, initially constructed of size
	 * <code>5</code>.
	 */
	private ArrayList<ILookPanel> lookPanels = new ArrayList<ILookPanel>(5);

	/**
	 * A reference to the current {@link LookPanel}.
	 */
	private LookPanel currentLookPanel = null;

	private Look() {
		Application.setLook(this);
	}

	public static ILook getInstance() {
		ILook look = Application.getLook();
		if (look == null) {
			look = new Look();
		}
		return look;
	}

	/**
	 * Gets the reference to the currently active {@link LookPanel} instance.
	 * 
	 * @return the current look panel
	 */
	@Override
	public ILookPanel getCurrentLookPanel() {
		return currentLookPanel;
	}

	/**
	 * Set the current {@link LookPanel} and the reference to it's
	 * {@link ImageLayer}.
	 * <p>
	 * The active look panel instance will get a colored top border, so that the
	 * user can determine the currently active instance.
	 * 
	 * @param lookPanel
	 */
	@Override
	public void setCurrentLookPanel(ILookPanel lookPanel) {
		currentLookPanel = (LookPanel) lookPanel;
		logger.debug("The currently active LookPanel is: " + currentLookPanel);
		// draw a color top border in the active panel
		for (ILookPanel lp : lookPanels) {
			if (lp == currentLookPanel) {
				((LookPanel) lp).setBackground(Color.ORANGE);
				((LookPanel) lp).repaint();
				try {
					((JComponent) lp.getCurrentROILayer())
							.requestFocusInWindow();
				} catch (Exception ignored) {
				}
			} else {
				((LookPanel) lp).setBackground(UIManager
						.getColor("Panel.background"));
				((LookPanel) lp).repaint();
			}
		}
	}

	/**
	 * This method creates a new instance of {@link LookExtraFrame}.
	 */
	@Override
	public void createExtraLookFrame() {

		// construct the frame
		LookExtraFrame iqmLookExtraFrame = new LookExtraFrame();

		// add the extra look frame's panel to the list and set it to the
		// current instance
		this.addLookPanel(iqmLookExtraFrame.getLookPanel());
		try {
			// optionally take the image from the currently selected tank data
			// box and pass it on to the extra look frame
			int idx = Application.getManager().getCurrItemIndex();

			IqmDataBox iqmDataBox = Tank.getInstance()
					.getCurrentTankIqmDataBoxAt(idx);

			if (iqmDataBox != null && iqmDataBox instanceof IVirtualizable) {
				iqmDataBox = VirtualDataManager.getInstance().load(
						(IVirtualizable) iqmDataBox);
			}

			PlanarImage pi = iqmDataBox.getImage();
			if (pi == null) {
				BoardPanel
						.appendTextln(
								"There is no image available, creating an empty panel.",
								Look.class);
				logger.debug("There is no image available, creating a new empty LookPanel.");
			} else {
				logger.debug("An image has been found, creating a new LookPanel and pass the image on to that panel.");
				this.setImage(pi);
				iqmLookExtraFrame.pack();
				iqmLookExtraFrame.validate();
			}
		} catch (Exception ex) {
			logger.debug("There is no image available, creating a new empty LookPanel.");
		}
		// finally show the look extra frame
		iqmLookExtraFrame.setVisible(true);
	}

	/**
	 * This method gets the original image in the current {@link LookPanel}.
	 * 
	 * @return the {@link PlanarImage} instance of the image
	 */
	@Override
	public PlanarImage getCurrentImage() {
		return currentLookPanel.getCurrentImage();
	}

	/**
	 * This method adds a new image to the currently active {@link ImageLayer}.
	 * 
	 * @param img
	 *            the {@link RenderedImage}
	 */
	@Override
	public void setImage(RenderedImage img) {
		if (img == null) {
			currentLookPanel.resetAllImages();
			// ################################################
			// reset the image information to the LookPanel

			// Create the StringBuffer instance for the pixel information.
			currentLookPanel.setPixelInfo(new StringBuffer(50));

			GUITools.getMainFrame().resetTitleBar();
			
			// construct the layer menu according to the image
			currentLookPanel.updateColorChannels();

			// update the image layer
			currentLookPanel.updateImageLayer();

			// reset the navigation panel
			currentLookPanel.getImageNavigatorPanel().reset();
			
		} else {
			PlanarImage pi = PlanarImage.wrapRenderedImage(img);

			// this is the current image
			// - either loaded from the hard disk or
			// - returned image from any operator
			currentLookPanel.setCurrentImage(pi);

			// ################################################
			// set the image information to the LookPanel

			// Create the StringBuffer instance for the pixel information.
			currentLookPanel.setPixelInfo(new StringBuffer(50));

			// Gather some data about the image and set it to the LookPanel
			currentLookPanel.setImageWidth(pi.getWidth());
			currentLookPanel.setImageHeight(pi.getHeight());

			int numBands = ImageAnalyzer.getNumberOfBands(pi);

			switch (ImageAnalyzer.getDataBufferType(pi)) {
			case DataBuffer.TYPE_BYTE:
				currentLookPanel.setImgDataBufferType("BYTE");
				currentLookPanel.setIntPixelValue(new int[numBands]);
				break;
			case DataBuffer.TYPE_SHORT:
				currentLookPanel.setImgDataBufferType("SHORT");
				currentLookPanel.setIntPixelValue(new int[numBands]);
				break;
			case DataBuffer.TYPE_USHORT:
				currentLookPanel.setImgDataBufferType("USHORT");
				currentLookPanel.setIntPixelValue(new int[numBands]);
				break;
			case DataBuffer.TYPE_INT:
				currentLookPanel.setImgDataBufferType("INT");
				currentLookPanel.setIntPixelValue(new int[numBands]);
				break;
			case DataBuffer.TYPE_FLOAT:
				currentLookPanel.setImgDataBufferType("FLOAT");
				currentLookPanel.setFloatPixelValue(new float[numBands]);
				break;
			case DataBuffer.TYPE_DOUBLE:
				currentLookPanel.setImgDataBufferType("DOUBLE");
				currentLookPanel.setDoublePixelValue(new double[numBands]);
				break;
			}

			// ################################################
			// set indexed image properties to the panel
			boolean indexed = ImageAnalyzer.isIndexed(pi);
			currentLookPanel.setIndexed(indexed);

			logger.trace("PlanarImage is indexed: " + indexed);

			if (indexed) {
				// short table: all values >=0
				currentLookPanel.setLut(ImageAnalyzer.getShortLookUpTable(pi));
			}

			// update the title of current image to frame
			try {
				// provoke a null pointer, if "image_name" is not set in pi
				// property
				if (pi.getProperty("image_name").equals("")) {
					GUITools.getMainFrame().resetTitleBar();
				} else {
					GUITools.getMainFrame()
							.setTitle(
									I18N.getGUILabelText(
											"application.frame.main.titleWithModelName",
											IQMConstants.APPLICATION_NAME,
											IQMConstants.APPLICATION_VERSION,
											pi.getProperty("image_name")));
				}
			} catch (NullPointerException ignored) {
			}

			// #####################################################
			// ZOOM the image
			logger.trace("The current zoom of the current LookPanel="
					+ String.format("%2s",
							Double.valueOf(currentLookPanel.getZoom() * 100.0d))
					+ "%");
			// calculate the individual zoom levels for each new image
			double minDim = Math.min(pi.getWidth(), pi.getHeight()) * 1.0d;

			// calculate the zoom level list according to the minimum dimension
			// of the image
			currentLookPanel.getZoomHelper().calculateZoomLevels(minDim);

			// set the zoom to the smallest possible when changing the image,
			// if it is smaller than the current value
			if (currentLookPanel.getZoom() < currentLookPanel.getZoomHelper()
					.getMinimalZoom()) {
				currentLookPanel.setZoom(currentLookPanel.getZoomHelper()
						.getMinimalZoom());
			}

			// zoom the image according to the current zoom setting
			PlanarImage zoomedImage = GUITools.zoomImage(
					currentLookPanel.getCurrentImage(),
					currentLookPanel.getZoom());

			// ######################################################
			// set the rendered image
			currentLookPanel.setRenderedImage(zoomedImage);

			// construct the layer menu according to the image
			currentLookPanel.updateColorChannels();

			// repaint the zoomed image
			currentLookPanel.updateImageLayer();
			
			// draw image on navigation panel
			currentLookPanel.updateNavigator();
		}
	}

	/**
	 * This method resets the Look window
	 */
	@Override
	public void reset() {
		if (currentLookPanel != null) {
			logger.debug("LookPanel is resetting");

			setImage(null);
			
			Manager.getInstance().setCurrItemIndex(-1);
		}
	}

	/**
	 * @return the lookPanels
	 */
	@Override
	public ArrayList<ILookPanel> getLookPanels() {
		return lookPanels;
	}

	/**
	 * @param lookPanels
	 *            the lookPanels to set
	 */
	@Override
	public void setLookPanels(ArrayList<ILookPanel> lookPanels) {
		this.lookPanels = lookPanels;
	}

	/**
	 * Add a new {@link LookPanel} to the list and set it to the current
	 * instance.
	 * 
	 * @param lookPanel
	 */
	@Override
	public void addLookPanel(ILookPanel lookPanel) {
		this.lookPanels.add(lookPanel);
		this.setCurrentLookPanel(lookPanel);

		logger.debug("Total # of LookPanels=[" + lookPanels.size() + "]:"
				+ lookPanels);
	}

	/**
	 * Removes a given {@link LookPanel} from the list and sets the the
	 * previously active instance to the current one.
	 * 
	 * @param lookPanel
	 */
	@Override
	public void removeLookPanel(ILookPanel lookPanel) {
		this.lookPanels.remove(lookPanel);
		if (!this.lookPanels.isEmpty()) {
			// the initial look panel must always be at index 0
			this.setCurrentLookPanel(this.lookPanels.get(this.lookPanels.size() - 1));
		}

		logger.debug("Total # of LookPanels=[" + lookPanels.size() + "]:"
				+ lookPanels);
	}

} // End of Look
