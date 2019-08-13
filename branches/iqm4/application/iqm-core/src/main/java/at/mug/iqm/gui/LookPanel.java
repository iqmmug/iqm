package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: LookPanel.java
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.DataBuffer;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.ColorChannelSelectorPanel;
import at.mug.iqm.api.gui.ContextPopupListener;
import at.mug.iqm.api.gui.ContextPopupMenu;
import at.mug.iqm.api.gui.DefaultDrawingLayer;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.gui.IDrawingLayer;
import at.mug.iqm.api.gui.ILookPanel;
import at.mug.iqm.api.gui.ImageInfoPanel;
import at.mug.iqm.api.gui.ImageLayer;
import at.mug.iqm.api.gui.ImageNavigatorPanel;
import at.mug.iqm.api.gui.LookToolboxPanel;
import at.mug.iqm.api.gui.ROILayerSelectorPanel;
import at.mug.iqm.api.gui.ViewPortChangeListener;
import at.mug.iqm.api.gui.util.ZoomHelper;
import at.mug.iqm.commons.gui.OpenImageDialog;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.image.ImageHeaderExtractor;
import at.mug.iqm.commons.util.image.ImageInfoExtractor;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.workflow.Look;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.util.GUITools;

/**
 * This panel holds responsible for displaying, zooming of images and drawing
 * ROIs on them.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2012 02 24
 * 
 */
public class LookPanel extends JPanel implements ILookPanel, ActionListener,
		ComponentListener, MouseListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -3912758686700608387L;
	/**
	 * Class specific logger.
	 */
	private static final Logger logger = Logger.getLogger(LookPanel.class);

	/**
	 * The top most container for all panels in the {@link LookPanel}.
	 */
	private JPanel lookContent = null;

	/**
	 * The split pane for tool boxes and the canvas.
	 */
	private JSplitPane splitPane;

	/**
	 * The panel for the tool box containing the ROI draw buttons.
	 */
	private LookToolboxPanel toolboxPanel = null;

	/**
	 * The panel for the ROI layer selection.
	 */
	private ROILayerSelectorPanel roiLayerSelectorPanel = null;

	/**
	 * The panel for the color channel selection.
	 */
	private ColorChannelSelectorPanel colorChannelSelectorPanel = null;

	/**
	 * The layered pane for ROI and image layers.
	 */
	private JLayeredPane layerPane = null;

	/**
	 * The panel where the image info is set to.
	 */
	private ImageInfoPanel imageInfoPanel = null;
	/**
	 * Variable for additional text in the {@link ImageInfoPanel} right after
	 * the zoom information, e.g angle, length.
	 */
	private String additionalText = "";

	/**
	 * The {@link ScrollPane}, a container where the image will be displayed via
	 * the {@link ImageLayer} instance.
	 */
	private JScrollPane imageScrollPane = null;
	/**
	 * The variable for displaying the image and the ROI elements.
	 */
	private ImageLayer imageLayer = null;

	/**
	 * A {@link ZoomHelper} for this instance.
	 */
	private ZoomHelper zoomHelper = null;

	/**
	 * The context pop up menu for {@link LookToolboxPanel} and the
	 * {@link ImageInfoPanel}.
	 */
	private ContextPopupMenu contextPopupMenu = null;
	/**
	 * The context pop up menu listener for the {@link ContextPopupMenu}.
	 */
	private ContextPopupListener contextPopupListener = null; // MouseAdapter

	/**
	 * A {@link PropertyChangeSupport} for emitting events.
	 */
	private PropertyChangeSupport pcs;

	/**
	 * The variable for the current zoom. Default is <code>1.0d</code>.
	 */
	private double zoom = 1.0d;

	/*
	 * ######### IMAGES
	 */
	/**
	 * The image instance of the rendered image (e.g. after an operation).
	 */
	private PlanarImage renderedImage = null;
	/**
	 * The image instance which is currently shown in the {@link LookPanel}.
	 */
	private PlanarImage currentImage = null;

	/*
	 * ######### ROI
	 */

	/*
	 * ############# IMAGE INFOS
	 */
	/**
	 * The {@link StringBuffer} for the pixel information. It contains zoom
	 * information, the {@link DataBuffer} type of the image and the values of
	 * the pixel under the mouse pointer.
	 */
	private StringBuffer pixelInfo;

	/**
	 * The pixel horizontal pixel count, i.e. the width of the currently
	 * displayed image (most of the time the original image).
	 */
	private int imgWidth;
	/**
	 * The pixel horizontal pixel count, i.e. the height of the currently
	 * displayed image (most of the time the original image).
	 */
	private int imgHeight;

	/**
	 * The image's {@link DataBuffer}: BYTE, SHORT, USHORT, INT, FLOAT, DOUBLE.
	 */
	private String imgDataBufferType;
	/**
	 * The variable for the image information, indicating whether the image uses
	 * an indexed color model or not.
	 */
	private boolean indexed; // true if indexed color model
	/**
	 * The look-up-table data of an indexed image.
	 */
	private short[][] lut; // lookup table data of indexed images
	/**
	 * The local array of double pixel values (DOUBLE).
	 */
	private double[] doublePixelValue; // pixel grey values for DOUBLE
	/**
	 * The local array of float pixel values (FLOAT).
	 */
	private float[] floatPixelValue; // pixel grey values for FLOAT
	/**
	 * The local array of integer pixel values (BYTE, SHORT, USHORT).
	 */
	private int[] intPixelValue; // pixel grey values for BYTE SHORT USHORT

	/**
	 * A flag whether or not the panel currently displays an item.
	 */
	private boolean isEmpty = true;

	/**
	 * The image navigator panel for general orientation.
	 */
	private ImageNavigatorPanel imageNavigatorPanel;

	/**
	 * Default constructor.
	 */
	public LookPanel() {
		super();

		// set the property change support for emitting events
		this.pcs = new PropertyChangeSupport(this);

		// create the zoom helper instance
		this.zoomHelper = new ZoomHelper();

		// creates the context menus and listeners for all instances
		this.createContextMenuAndBehavior();

		// creates the GUI elements
		this.createAndAssemble();
	}

	/**
	 * This method creates and assembles the GUI.
	 */
	private void createAndAssemble() {
		// this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); //automatic
		// resize
		this.setLayout(new BorderLayout()); // essential for automatic resize;
		this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		this.add(this.getLookContent());
	}

	@Override
	public void initialize() {
		this.roiLayerSelectorPanel.createDefaultLayer();
	}

	/**
	 * Create a new context popup menu with this instance as
	 * {@link ActionListener}. All commands have to be implemented in this
	 * class.
	 */
	private void createContextMenuAndBehavior() {
		this.setContextPopupMenu(new ContextPopupMenu(this));
		this.setContextPopupListener(new ContextPopupListener(this
				.getContextPopupMenu()));
	}

	/**
	 * @return the contextPopupMenu
	 */
	private ContextPopupMenu getContextPopupMenu() {
		return contextPopupMenu;
	}

	/**
	 * @param contextPopupMenu
	 *            the contextPopupMenu to set
	 */
	private void setContextPopupMenu(ContextPopupMenu contextPopupMenu) {
		this.contextPopupMenu = contextPopupMenu;
	}

	/**
	 * @return the contextPopupListener
	 */
	@Override
	public ContextPopupListener getContextPopupListener() {
		return contextPopupListener;
	}

	/**
	 * @param contextPopupListener
	 *            the contextPopupListener to set
	 */
	private void setContextPopupListener(
			ContextPopupListener contextPopupListener) {
		this.contextPopupListener = contextPopupListener;
	}

	/**
	 * This method initializes lookContent
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getLookContent() {
		if (lookContent == null) {
			lookContent = new JPanel();
			lookContent.setLayout(new BorderLayout());
			lookContent.add(getToolboxPanel(), BorderLayout.WEST);

			JScrollPane canvas = createJScrollPane();

			JPanel rightControls = new JPanel(new BorderLayout());

			JTabbedPane controlTabs = new JTabbedPane(JTabbedPane.BOTTOM,
					JTabbedPane.SCROLL_TAB_LAYOUT);
			controlTabs.insertTab(
					I18N.getGUILabelText("look.controls.tab.roilayers.text"),
					new ImageIcon(Resources.getImageURL("icon.roilayers.tab")),
					getROILayerSelectorPanel(),
					I18N.getGUILabelText("look.controls.tab.roilayers.ttp"), 0);
			controlTabs
					.insertTab(
							I18N.getGUILabelText("look.controls.tab.colorchannels.text"),
							new ImageIcon(Resources
									.getImageURL("icon.colorchannels.tab")),
							getColorChannelSelectorPanel(),
							I18N.getGUILabelText("look.controls.tab.colorchannels.ttp"),
							1);

			rightControls.add(controlTabs, BorderLayout.CENTER);
			rightControls.setPreferredSize(new Dimension(200, 400));

			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvas,
					rightControls);

			JPanel navigatorContainer = new JPanel();
			navigatorContainer.setBorder(null);
			rightControls.add(navigatorContainer, BorderLayout.NORTH);
			navigatorContainer.setPreferredSize(new Dimension(200, 140));
			navigatorContainer.setLayout(new GridLayout(1, 0, 0, 0));

			imageNavigatorPanel = createNavigatorPanel();
			navigatorContainer.add(imageNavigatorPanel);

			splitPane.setOneTouchExpandable(true);
			splitPane.setResizeWeight(1.0);

			lookContent.add(splitPane, BorderLayout.CENTER);
			lookContent.add(getImageInfoPanel(), BorderLayout.NORTH);
		}
		return lookContent;
	}

	public JSplitPane getSplitPane() {
		return splitPane;
	}

	/**
	 * This method gets the {@link ImageLayer}.
	 * 
	 * @return the {@link LookPanel}'s instance of {@link ImageLayer}
	 */
	@Override
	public ImageLayer getImageLayer() {
		if (imageLayer == null) {
			imageLayer = this.createImageLayer();
		}
		return imageLayer;
	}

	private ImageNavigatorPanel createNavigatorPanel() {
		this.imageNavigatorPanel = new ImageNavigatorPanel(this);
		return this.imageNavigatorPanel;
	}

	/**
	 * Creates a new instance of the displaying class.
	 * 
	 * @return a new {@link ImageLayer}
	 */
	private ImageLayer createImageLayer() {
		// hand over reference to self
		this.imageLayer = new ImageLayer(this);
		return this.imageLayer;
	}

	/**
	 * This method initializes the image info panel, displaying the current
	 * pixel information.
	 * 
	 * @return javax.swing.JPanel
	 */
	public ImageInfoPanel getImageInfoPanel() {
		if (imageInfoPanel == null) {
			imageInfoPanel = new ImageInfoPanel(this.getContextPopupListener());
			imageInfoPanel.setDisplayPanel(this);
		}

		return imageInfoPanel;
	}

	/**
	 * Gets the selector panel for color components.
	 * 
	 * @return the {@link ColorChannelSelectorPanel}
	 */
	@Override
	public ColorChannelSelectorPanel getColorChannelSelectorPanel() {
		if (this.colorChannelSelectorPanel == null) {
			this.colorChannelSelectorPanel = new ColorChannelSelectorPanel(this);
		}
		return colorChannelSelectorPanel;
	}

	/**
	 * Sets the selector panel for color components.
	 * 
	 * @param colorChannelSelectorPanel
	 */
	public void setColorChannelSelectorPanel(
			ColorChannelSelectorPanel colorChannelSelectorPanel) {
		this.colorChannelSelectorPanel = colorChannelSelectorPanel;
	}

	/**
	 * @return the toolboxPanel
	 */
	@Override
	public LookToolboxPanel getToolboxPanel() {
		if (this.toolboxPanel == null) {
			toolboxPanel = new LookToolboxPanel(this);
		}
		return toolboxPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane createJScrollPane() {
		JLayeredPane layeredPane = getLayerPane();
		JPanel layerRoot = new JPanel(new BorderLayout());
		layerRoot.add(layeredPane, BorderLayout.CENTER);
		ImageLayer imageLayer = this.getImageLayer();
		imageLayer.setLayout(new GridLayout());

		imageScrollPane = new JScrollPane(layerRoot);

		imageScrollPane.getViewport().addChangeListener(
				new ViewPortChangeListener(this));

		layeredPane.add(imageLayer, JLayeredPane.DEFAULT_LAYER);

		return imageScrollPane;
	}

	public JLayeredPane getLayerPane() {
		if (this.layerPane == null) {
			this.layerPane = new JLayeredPane();
		}
		return layerPane;
	}

	/**
	 * This method destroys the GUI
	 */
	@Override
	public void destroyGUI() throws Exception {
		// remove this instance from the look list of all
		// LookPanels
		Look.getInstance().removeLookPanel(this);

		logger.debug("LookPanel " + this.getName() + " has been destroyed.");
		System.gc();
	}

	/**
	 * @return the imgWidth
	 */
	@Override
	public int getImageWidth() {
		return imgWidth;
	}

	/**
	 * @return the imgHeight
	 */
	@Override
	public int getImageHeight() {
		return imgHeight;
	}

	/**
	 * @param imgWidth
	 *            the imgWidth to set
	 */
	@Override
	public void setImageWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}

	/**
	 * @param imgHeight
	 *            the imgHeight to set
	 */
	@Override
	public void setImageHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}

	/**
	 * @return the imgType
	 */
	@Override
	public String getImgDataBufferType() {
		return imgDataBufferType;
	}

	/**
	 * @param imgDataBufferType
	 *            the imgType to set
	 */
	@Override
	public void setImgDataBufferType(String imgDataBufferType) {
		this.imgDataBufferType = imgDataBufferType;
	}

	/**
	 * @return the isIndexed
	 */
	@Override
	public boolean isIndexed() {
		return indexed;
	}

	/**
	 * @return the lut
	 */
	@Override
	public short[][] getLut() {
		return lut;
	}

	/**
	 * @return the doublePixelValue
	 */
	@Override
	public double[] getDoublePixelValue() {
		return doublePixelValue;
	}

	/**
	 * @return the floatPixelValue
	 */
	@Override
	public float[] getFloatPixelValue() {
		return floatPixelValue;
	}

	/**
	 * @return the intPixelValue
	 */
	@Override
	public int[] getIntPixelValue() {
		return intPixelValue;
	}

	/**
	 * @return the renderedImage
	 */
	@Override
	public PlanarImage getRenderedImage() {
		return renderedImage;
	}

	/**
	 * @return the currentImage
	 */
	@Override
	public PlanarImage getCurrentImage() {
		return currentImage;
	}

	/**
	 * @param currentImage
	 *            the currentImage to set
	 */
	@Override
	public void setCurrentImage(PlanarImage currentImage) {
		this.currentImage = currentImage;
		if (currentImage == null) {
			this.isEmpty = true;
		}
	}

	/**
	 * @param isIndexed
	 *            the isIndexed to set
	 */
	@Override
	public void setIndexed(boolean isIndexed) {
		this.indexed = isIndexed;
	}

	/**
	 * @param lut
	 *            the lut to set
	 */
	@Override
	public void setLut(short[][] lut) {
		this.lut = lut;
	}

	/**
	 * @param doublePixelValue
	 *            the doublePixelValue to set
	 */
	@Override
	public void setDoublePixelValue(double[] doublePixelValue) {
		this.doublePixelValue = doublePixelValue;
	}

	/**
	 * @param floatPixelValue
	 *            the floatPixelValue to set
	 */
	@Override
	public void setFloatPixelValue(float[] floatPixelValue) {
		this.floatPixelValue = floatPixelValue;
	}

	/**
	 * @param intPixelValue
	 *            the intPixelValue to set
	 */
	@Override
	public void setIntPixelValue(int[] intPixelValue) {
		this.intPixelValue = intPixelValue;
	}

	/**
	 * @param rendImage
	 *            the rendImage to set
	 */
	@Override
	public void setRenderedImage(PlanarImage rendImage) {
		this.renderedImage = rendImage;
		if (rendImage == null) {
			this.isEmpty = true;
		}
	}

	/**
	 * This method allows external classes access to the pixel info which was
	 * obtained in the mouseMoved method.
	 * 
	 * @return the pixel information, formatted as a string
	 */
	@Override
	public StringBuffer getPixelInfo() {
		return pixelInfo;
	}

	@Override
	public void setPixelInfo(StringBuffer pixelInfo) {
		this.pixelInfo = pixelInfo;
	}

	@Override
	public String getPixelInfoString() {
		return pixelInfo.toString();
	}

	/**
	 * @return the zoom
	 */
	@Override
	public double getZoom() {
		return zoom;
	}

	/**
	 * @param zoom
	 *            the zoom to set
	 */
	@Override
	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	@Override
	public void setViewOffset(Point p) {
		JViewport vp = imageScrollPane.getViewport();
		double xNew = (p.getX() * zoom) - (vp.getExtentSize().getWidth() / 2);
		double yNew = (p.getY() * zoom) - (vp.getExtentSize().getHeight() / 2);
		if (xNew < 0) {
			xNew = 0;
		}
		if (yNew < 0) {
			yNew = 0;
		}
		// System.out.println("x_new: " + x_new);
		// System.out.println("y_new: " + y_new);

		Point pNew = new Point();
		pNew.setLocation(xNew, yNew);
		// !!!!!! without vp.validate(), the Viewport has still the old size
		// //Bug ID: 5066771
		vp.validate();
		vp.setViewPosition(pNew);
		vp.repaint();
	}

	@Override
	public JScrollPane getScrollPane() {
		return this.imageScrollPane;
	}

	/**
	 * @return the additionalText
	 */
	@Override
	public String getAdditionalText() {
		return additionalText;
	}

	/**
	 * @param additionalText
	 *            the additionalText to set
	 */
	@Override
	public void setAdditionalText(String additionalText) {
		this.additionalText = additionalText;
	}

	/**
	 * @param imageInfoPanel
	 *            the imageInfoPanel to set
	 */
	public void setImageInfoPanel(ImageInfoPanel imageInfoPanel) {
		this.imageInfoPanel = imageInfoPanel;
	}

	/**
	 * This method deletes the currently selected ROI and sets the last element
	 * of the {@link ArrayList} to the current ROI.
	 */
	@Override
	public void deleteSelectedROI() {
		this.roiLayerSelectorPanel.getCurrentLayer().deleteSelectedROI();
	}

	/**
	 * This method deletes all ROIs and all ROI vectors on the selected layer.
	 */
	@Override
	public void deleteAllROIShapesOnLayer() {
		this.roiLayerSelectorPanel.getCurrentLayer().deleteAllROIs();
	}

	/**
	 * This method deletes all ROIs and all ROI vectors in every layer.
	 */
	@Override
	public void deleteAllROIShapes() {
		int selection = DialogUtil.getInstance().showDefaultQuestionMessage(
				I18N.getMessage("question.delete.roi.allLayers"));

		if (selection == IDialogUtil.YES_OPTION) {
			for (IDrawingLayer layer : this.roiLayerSelectorPanel.getLayers())
				layer.deleteAllROIs();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("imageinfo".equals(e.getActionCommand())) {
			TextPanel tp = new TextPanel();
			TextDisplayFrame tdf = new TextDisplayFrame(tp);

			String info = new ImageInfoExtractor().getImageInfo(Look
					.getInstance().getCurrentImage());
			if (info != null) {
				tdf.setTitle(I18N.getGUILabelText(
						"textDisplay.frame.imageInfo.single.title",
						Look.getInstance().getCurrentImage()
								.getProperty("file_name")));
				tp.writeText(info);
				tdf.setVisible(true);
			} else {
				int selection = DialogUtil
						.getInstance()
						.showDefaultQuestionMessage(
								I18N.getMessage("application.dialog.noImageOpen.question"));
				if (selection == IDialogUtil.YES_OPTION) {
					// first fire up the open dialog for all supported image
					// formats
					OpenImageDialog dlg = new OpenImageDialog();
					File[] files = dlg.showDialog();

					if (files == null || files.length < 1) {
						return;
					}

					// log ALL loaded files anyway
					logger.info("Selected image(s) for loading: "
							+ Arrays.asList(files).toString());

					Application.getTank().loadImagesFromHD(files);
				}
			}
		}
		if ("imageheader".equals(e.getActionCommand())) {
			TextPanel tp = new TextPanel();
			TextDisplayFrame tdf = new TextDisplayFrame(tp);

			String header = new ImageHeaderExtractor().getImageHeader(Look
					.getInstance().getCurrentImage());
			if (header != null) {
				tdf.setTitle(I18N.getGUILabelText(
						"textDisplay.frame.imageHeader.single.title",
						Look.getInstance().getCurrentImage()
								.getProperty("file_name")));
				tp.writeText(header);
				tdf.setVisible(true);
			} else {
				int selection = DialogUtil
						.getInstance()
						.showDefaultQuestionMessage(
								I18N.getMessage("application.dialog.noImageOpen.question"));
				if (selection == IDialogUtil.YES_OPTION) {
					OpenImageDialog dlg = new OpenImageDialog();
					File[] files = dlg.showDialog();
					
					if (files == null || files.length < 1){
						return;
					}
					
					Tank.getInstance().loadImagesFromHD(files);
				}
			}

		}
		if ("deletecurrroi".equals(e.getActionCommand())) {
			this.deleteSelectedROI();
			logger.debug("Current ROI has been deletedon the current layer "
					+ roiLayerSelectorPanel.getCurrentLayer().getName());
		}
		if ("deleteallrois".equals(e.getActionCommand())) {
			this.deleteAllROIShapesOnLayer();
			logger.debug("All ROIs have been deleted on the current layer "
					+ roiLayerSelectorPanel.getCurrentLayer().getName());
		}
		if ("deleteallroisalllayers".equals(e.getActionCommand())) {
			this.deleteAllROIShapes();
			logger.debug("All ROIs have been deleted");
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		logger.trace("LookPanel has been resized, repainting...");
		updateImageLayer();
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
	}

	/**
	 * Updates the image in the object's {@link ImageLayer} instance.
	 */
	@Override
	public void updateImageLayer() {

		if (this.renderedImage == null) {
			logger.debug("Setting 'null' as rendered image in image layer.");
			imageLayer.set(null);
			isEmpty = true;
			layerPane.repaint();
		} else {

			// PK: DEBUG
			// GUITools.showImage(this.getRenderedImage().getAsBufferedImage(),
			// "Rendered image");
			// GUITools.showImage(this.getCurrentImage().getAsBufferedImage(),
			// "Current image");

			if (this.colorChannelSelectorPanel.hasSelectiveChannelsDisplayed()) {
				logger.debug("Getting extracted image from color channel selector panel");

				// zoom the extracted image according to the current zoom
				// setting
				PlanarImage zoomedImage = this.colorChannelSelectorPanel
						.getExtractedImage();

				if (zoom != 1.0d) {
					zoomedImage = GUITools.zoomImage(
							this.colorChannelSelectorPanel.getExtractedImage(),
							zoom);
				}
				imageLayer.set(zoomedImage);
			} else {
				logger.debug("Getting the rendered image from the LookPanel.");
				imageLayer.set(this.renderedImage);

				DecimalFormat fmt = new DecimalFormat("0.##");
				String zom = "Zoom: " + fmt.format(zoom * 100.0d) + "%";
				String siz = String.format("%6s", "") + "X " + getImageWidth()
						+ String.format("%2s", "") + "Y " + getImageHeight()
						+ String.format("%3s", "");

				pixelInfo = new StringBuffer(50);
				pixelInfo.append(zom + siz);
			}
			isEmpty = false;
		}
		imageInfoPanel.getImageInfoLabel().setText(pixelInfo.toString());
	}

	@Override
	public void updateLayerBounds(Dimension dim) {
		// firstly assign dimension to the image layer
		layerPane.setPreferredSize(dim);

		Component[] layers = this.layerPane.getComponents();
		// start with 1, since the bounds of the image layer have already been
		// set.
		for (Component c : layers) {
			if (c instanceof DefaultDrawingLayer) {
				DefaultDrawingLayer layer = (DefaultDrawingLayer) c;
				logger.debug("Setting bounds of layer " + c.getName() + " to "
						+ dim);
				layer.setBounds(new Rectangle(dim));
			}
		}
	}

	/**
	 * @return the pcs
	 */
	@Override
	public PropertyChangeSupport getPcs() {
		return pcs;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// set the current look panel to this instance
		if (SwingUtilities.isLeftMouseButton(e)) {
			logger.debug("Activated LookPanel instance, setting current instance in Look: "
					+ this);
			Look.getInstance().setCurrentLookPanel(this);
		} else {
			// request the focus of the image layer for receiving key inputs
			((DefaultDrawingLayer) this.getCurrentROILayer()).requestFocus();
		}
//		// request the focus for mouse and keyboard events
//		((DefaultDrawingLayer) currentLookPanel.getCurrentROILayer()).requestFocusInWindow();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Get the {@link ZoomHelper} instance associated with this instance.
	 * 
	 * @return a {@link ZoomHelper} instance
	 */
	public ZoomHelper getZoomHelper() {
		return zoomHelper;
	}

	/**
	 * Returns whether or not the panel currently displays an item.
	 * 
	 * @return <code>true</code>, if an item is displayed, <code>false</code>,
	 *         if not
	 */
	@Override
	public boolean isEmpty() {
		return isEmpty;
	}

	/**
	 * This is just here so that we can accept the keyboard focus
	 */
	@Override
	public boolean isFocusTraversable() {
		return true;
	}

	/**
	 * Get the ROI layer manager for this panel.
	 * 
	 * @return the {@link ROILayerSelectorPanel}
	 */
	@Override
	public ROILayerSelectorPanel getROILayerSelectorPanel() {
		if (this.roiLayerSelectorPanel == null) {
			this.roiLayerSelectorPanel = new ROILayerSelectorPanel(this);
		}
		return roiLayerSelectorPanel;
	}

	/**
	 * Set the ROI manager for this panel.
	 * 
	 * @param roiLayerSelectorPanel
	 */
	public void setROILayerSelectorPanel(
			ROILayerSelectorPanel roiLayerSelectorPanel) {
		this.roiLayerSelectorPanel = roiLayerSelectorPanel;
	}

	/**
	 * Gets all ROI layers of this look panel.
	 * 
	 * @return a {@link List} of all {@link IDrawingLayer}s in this panel
	 */
	public List<IDrawingLayer> getROILayers() {
		return this.roiLayerSelectorPanel.getLayers();
	}

	public IDrawingLayer getCurrentROILayer() {
		return this.roiLayerSelectorPanel.getCurrentLayer();
	}

	/**
	 * Resets all images.
	 * <ul>
	 * <li>current image
	 * <li>rendered image
	 * <li>extracted image (selective channels)
	 * </ul>
	 */
	public void resetAllImages() {
		setCurrentImage(null);
		setRenderedImage(null);
		colorChannelSelectorPanel.setExtractedImage(null);
		colorChannelSelectorPanel.setSelectiveChannelsDisplayed(false);
	}

	/**
	 * A delegation method to update the color channel manager.
	 */
	public void updateColorChannels() {
		colorChannelSelectorPanel.updateChannelWidgets();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	@Override
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
	}

	/**
	 * A delegation method for updating the navigator view.
	 */
	public void updateNavigator() {
		this.imageNavigatorPanel.updateNavigator();
	}

	/**
	 * Returns a reference to the {@link ImageNavigatorPanel} associated with
	 * this instance.
	 * 
	 * @return the {@link ImageNavigatorPanel} of this instance
	 */
	public ImageNavigatorPanel getImageNavigatorPanel() {
		return imageNavigatorPanel;
	}
}
