package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ImageLayer.java
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


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.text.DecimalFormat;

import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.commons.util.CommonTools;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * This class is responsible for displaying the image on a panel.
 * 
 * @author Philipp Kainz
 * 
 */
public class ImageLayer extends DisplayJAI {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 6921193710647682126L;

	/**
	 * Custom class logger.
	 */
	private static final Logger logger = LogManager.getLogger(ImageLayer.class);

	/**
	 * The raster of the displayed image.
	 */
	private Raster imgData = null;
	
	/**
	 * A reference to the current look panel.
	 */
	protected ILookPanel displayPanel = null;

	/**
	 * Default constructor, calls the super constructor.
	 */
	public ImageLayer() {
		super();
		logger.debug("Constructing a new instance...");
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	/**
	 * Custom constructor calls the default constructor. Set the
	 * {@link ILookPanel} reference directly to the object.
	 * 
	 * @param displayPanel
	 *            the {@link ILookPanel}
	 */
	public ImageLayer(ILookPanel displayPanel) {
		this();
		this.displayPanel = displayPanel;
	}

	/**
	 * Set the image to be displayed, <code>null</code> is permitted.
	 * 
	 * @param im
	 *            the image, or <code>null</code> to remove the image
	 */
	public void set(RenderedImage im) {
		if (im == null) {
			source = null;
			imgData = null;
			setBounds(0, 0, 0, 0);
			displayPanel.updateLayerBounds(new Dimension());
		} else {
			source = im;

			// Swing geometry
			int w = source.getWidth();
			int h = source.getHeight();
			Insets insets = getInsets();
			Dimension dim = new Dimension(w + insets.left + insets.right, h
					+ insets.top + insets.bottom);

			setPreferredSize(dim);

			imgData = this.displayPanel.getCurrentImage().getData();
			setBounds(0, 0, im.getWidth(), im.getHeight());
			displayPanel.updateLayerBounds(dim);
		}

		revalidate();
		repaint();
	}

	/**
	 * Set the image to be displayed, <code>null</code> is permitted.
	 * 
	 * @param im
	 *            the image, or <code>null</code> to remove the image
	 * @param x
	 *            the origin x
	 * @param y
	 *            the origin y
	 */
	@Override
	public void set(RenderedImage im, int x, int y) {
		if (im == null) {
			source = null;
			imgData = null;
			originX = 0;
			originY = 0;
			setBounds(x, y, 0, 0);
			displayPanel.getLayerPane().setPreferredSize(new Dimension());
		} else {
			source = im;
			
			int w = source.getWidth();
			int h = source.getHeight();
			Insets insets = getInsets();
			Dimension dim = new Dimension(w + insets.left + insets.right, h
					+ insets.top + insets.bottom);

			setPreferredSize(dim);

			originX = x;
			originY = y;

			imgData = this.displayPanel.getCurrentImage().getData();
			setBounds(x, y, im.getWidth(), im.getHeight());
			displayPanel.getLayerPane().setPreferredSize(getPreferredSize());
		}

		revalidate();
		repaint();
	}

	/**
	 * This methods paints the graphic and ROIs.
	 */
	@Override
	synchronized public void paintComponent(Graphics g) {
		logger.debug(Thread.currentThread().getName() + " paints image...");
		long start = System.currentTimeMillis();

		// restrict the area to be painted
		JViewport vp = displayPanel.getScrollPane().getViewport();
		Point location = vp.getViewPosition();
		Dimension extendSize = vp.getExtentSize();

		Rectangle clip = new Rectangle(location, extendSize);
		g.setClip(clip);

		if (source == null) {
			setBounds(clip);
		}
		Graphics2D g2d = (Graphics2D) g;

		Rectangle clipBounds = g2d.getClipBounds();
		g2d.setColor(getBackground());
		g2d.fillRect(clipBounds.x, clipBounds.y, clipBounds.width,
				clipBounds.height);

		Insets insets = getInsets();
		int tx = insets.left + originX;
		int ty = insets.top + originY;

		try {
			g2d.drawRenderedImage(source,
					AffineTransform.getTranslateInstance(tx, ty));
		} catch (OutOfMemoryError e) {
			logger.fatal("The rendered image cannot be drawn! ", e);
		}

		logger.debug("Painted image in milliseconds: "
				+ (System.currentTimeMillis() - start) / 1.0d);
	}

	/**
	 * @see DisplayJAI#repaint()
	 */
	@Override
	public void repaint() {
		super.repaint();
	}

	/**
	 * On mouse move events, this component sets current image information to
	 * the image info panel in the look panel.
	 * 
	 * @param e
	 *            the mouse event
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (this.displayPanel.getCurrentImage() == null) {
			displayPanel.setPixelInfo(new StringBuffer());
			displayPanel.setAdditionalText("");
			this.displayPanel.getImageInfoPanel().getImageInfoLabel()
					.setText("");
			return;
		}

		StringBuffer pixelInfo = this.displayPanel.getPixelInfo();
		int imgWidth = this.displayPanel.getImageWidth();
		int imgHeight = this.displayPanel.getImageHeight();
		double zoom = this.displayPanel.getZoom();
		String dataBufferString = this.displayPanel.getImgDataBufferType();

		// clear the StringBuffer
		pixelInfo.setLength(0);

		DecimalFormat fmt = new DecimalFormat("0.##");
		String zoomString = "Zoom: " + fmt.format(zoom * 100.0d) + "%";
		String sizeString = String.format("%6s", "") + "X " + imgWidth
				+ String.format("%2s", "") + "Y " + imgHeight
				+ String.format("%3s", "");

		// calculates the real pixel position
		Point currPos = CommonTools.getRealPixelPosition(e.getPoint(), zoom);

		if ((currPos.getX() >= imgWidth) || (currPos.getY() >= imgHeight)
				|| (currPos.getX() < 0) || (currPos.getY() < 0)) {
			pixelInfo.append(" " + dataBufferString + " " + zoomString
					+ sizeString);
			this.displayPanel.getImageInfoPanel().getImageInfoLabel()
					.setText(this.displayPanel.getPixelInfo().toString());
			return;
		}

		String positionString = "    " + "x" + " " + ((int) currPos.getX() + 1)
				+ "  " + "y" + " " + ((int) currPos.getY() + 1) + "   ";

		pixelInfo.append(" " + dataBufferString + " " + zoomString + sizeString
				+ positionString);

		if (this.displayPanel.isIndexed()) { // indexed color model
			pixelInfo.append("    ");
			// // read the pixel
			imgData.getPixel((int) currPos.getX(), (int) currPos.getY(),
					this.displayPanel.getIntPixelValue());

			int index = this.displayPanel.getIntPixelValue()[0];
			pixelInfo.append("Index: " + index);

			short[][] lut = this.displayPanel.getLut();
			// get RGB values from LUT.
			pixelInfo.append("    " + "RGB:" + " " + lut[0][index] + " "
					+ lut[1][index] + " " + lut[2][index]);
		} else { //
			pixelInfo.append("    " + dataBufferString.toLowerCase() + " ");

			try {
				if (dataBufferString.equals("BYTE")
						|| dataBufferString.equals("SHORT")
						|| dataBufferString.equals("USHORT")
						|| dataBufferString.equals("INT")) {

					int[] pixelValues = this.displayPanel.getIntPixelValue();

					imgData.getPixel((int) currPos.getX(),
							(int) currPos.getY(), pixelValues);

					for (int i = 0; i < pixelValues.length; i++)
						pixelInfo.append(pixelValues[i] + " ");
				}
				if (this.displayPanel.getImgDataBufferType().equals("FLOAT")) {

					float[] pixelValues = this.displayPanel
							.getFloatPixelValue();

					imgData.getPixel((int) currPos.getX(),
							(int) currPos.getY(), pixelValues);

					for (int i = 0; i < pixelValues.length; i++)
						pixelInfo.append(pixelValues[i] + " ");
				}
				if (this.displayPanel.getImgDataBufferType().equals("DOUBLE")) {

					double[] pixelValues = this.displayPanel
							.getDoublePixelValue();

					imgData.getPixel((int) currPos.getX(),
							(int) currPos.getY(), pixelValues);
					for (int i = 0; i < pixelValues.length; i++)
						pixelInfo.append(pixelValues[i] + " ");
				}

				// delete last colon
				this.displayPanel.setPixelInfo(pixelInfo.deleteCharAt(pixelInfo
						.length() - 1));

			} catch (Exception ee) {
				logger.error("An error occurred: ", ee);
				return;
			}
		}

		// the additional text has been set by the event handlers in the look
		// panel (e.g. for ROIs etc.)
		pixelInfo.append(this.displayPanel.getAdditionalText());
		// values for angle or length
		this.displayPanel.getImageInfoPanel().getImageInfoLabel()
				.setText(this.displayPanel.getPixelInfo().toString());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (this.displayPanel.getToolboxPanel().getButtonZoom().isSelected()) {
			if (!SwingUtilities.isLeftMouseButton(e)
					&& !SwingUtilities.isRightMouseButton(e)) {
				return;
			}

			// disable zooming on no present image
			if (this.displayPanel.getCurrentImage() == null) {
				logger.debug("There is no image available!");
				return;
			}

			applyZoom(e);
		}
	}

	public void applyZoom(MouseEvent e) {

		// ZOOM (pressed)

		// calculation and setting of the mouse click point
		// current mouse position in real image pixel values
		Point mP = CommonTools.getRealPixelPosition(e.getPoint(),
				this.displayPanel.getZoom());

		// get the zoom factor from the panel
		double zoom = this.displayPanel.getZoom();

		if (SwingUtilities.isLeftMouseButton(e)) {
			logger.debug("mouse pressed left on zoom button");
			if (zoom >= this.displayPanel.getZoomHelper().getZoomLevels()
					.getLast()) {
				return;
			} else {
				zoom = this.displayPanel.getZoomHelper().getHigherZoomLevel(
						zoom);
				this.displayPanel.setZoom(zoom);
			}
		}

		else if (SwingUtilities.isRightMouseButton(e)) {
			logger.debug("mouse pressed right on zoom button");
			// check, if lower zoom is allowed
			double minDim = Math.min(this.displayPanel.getCurrentImage()
					.getWidth(), this.displayPanel.getCurrentImage()
					.getHeight()) * 1.0d;
			if ((minDim * zoom) <= 1.25) {
				return;
			} else {
				zoom = this.displayPanel.getZoomHelper()
						.getLowerZoomLevel(zoom);
				this.displayPanel.setZoom(zoom);
			}
		}

		if (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) {
			// Reset to 1.0 PK 2012 03 08
			logger.debug("Resetting zoom to 1.0d @ ctrl+left");
			zoom = 1.0d;
			this.displayPanel.setZoom(zoom);
		}

		// set the rendered image
		this.displayPanel.setRenderedImage(CommonTools.zoomImage(
				this.displayPanel.getCurrentImage(),
				this.displayPanel.getZoom()));

		this.displayPanel.updateImageLayer();

		// centers the pixel below the mouse pointer, if possible
		this.displayPanel.setViewOffset(mP);

		// update the text in the image info panel
		mouseMoved(e);
	}
	
	@Override
	public void setIgnoreRepaint(boolean ignoreRepaint) {
		super.setIgnoreRepaint(ignoreRepaint);
	}
	
	@Override
	public boolean getIgnoreRepaint() {
		return super.getIgnoreRepaint();
	}
}
