package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: ImageNavigatorPanel.java
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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.commons.util.CursorFactory;
import at.mug.iqm.commons.util.image.Thumbnail;

/**
 * This class provides navigation support for navigating in the image at greater
 * magnifications where scrollbars are not convenient.
 * 
 * @author Philipp Kainz
 * 
 */
public class ImageNavigatorPanel extends JPanel implements MouseListener,
		MouseMotionListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7544472348802386056L;

	/**
	 * The display panel where the image is displayed.
	 */
	protected ILookPanel displayPanel;
	/**
	 * The thumbnail to be displayed.
	 */
	private Thumbnail thumbnail = null;

	private JLayeredPane layeredPane;
	private JPanel imagePanel;
	private JLabel lblThumbnail;
	private DrawingLayer drawingLayer;

	private Rectangle defaultBounds = new Rectangle(0, 0, 200, 120);
	
	private ChangeListener[] vpChangeListeners;
	private JViewport vp;
	private Point dragStart;
	
	/**
	 * The visible rectangle of the view port.
	 */
	private Rectangle visibleRect = new Rectangle();
	private Rectangle viewRectangle = new Rectangle();

	/**
	 * Create the panel.
	 */
	protected ImageNavigatorPanel() {
		// setPreferredSize(new Dimension(200, 140));
		setLayout(new BorderLayout(0, 0));

		JPanel northPanel = new JPanel();
		add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel lblHeader = new JLabel(
				I18N.getGUILabelText("image.navigator.header.text"));
		lblHeader.setOpaque(true);
		lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
		lblHeader.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblHeader.setBackground(Color.WHITE);
		northPanel.add(lblHeader);

		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(Color.LIGHT_GRAY);
		centerPanel.setBorder(new EmptyBorder(5, 1, 1, 1));
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridLayout(1, 0, 0, 0));

		JPanel layerRoot = new JPanel();
		// add the image panel to the layered pane
		layeredPane = new JLayeredPane();
		layerRoot.setLayout(new BorderLayout());
		layerRoot.add(layeredPane, BorderLayout.CENTER);

		imagePanel = new JPanel();
		imagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		lblThumbnail = new JLabel("");
		lblThumbnail.setOpaque(false);
		imagePanel.add(lblThumbnail);
		// default bounds for the panel
		imagePanel.setBounds(defaultBounds);

		drawingLayer = new DrawingLayer();
		drawingLayer.addMouseListener(this);
		drawingLayer.addMouseMotionListener(this);
		// default bounds (but will be updated elsewhere)
		// drawingLayer.setBounds(defaultBounds);

		// add the layeredPane to the center panel
		layeredPane.add(imagePanel, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(drawingLayer, JLayeredPane.POPUP_LAYER);

		// always set the default bounds to the root pane
		layeredPane.setBounds(defaultBounds);

		// add the layeredPane container to the center panel
		centerPanel.add(layerRoot);
		repaint();
	}

	@Override
	public void repaint() {
		super.repaint();
		try {
			layeredPane.setBounds(getBounds());
			imagePanel.setBounds(getBounds());
			Rectangle bds = new Rectangle((imagePanel.getWidth() + 1) / 2
					- (thumbnail.getIconWidth() + 1) / 2, 0,
					thumbnail.getIconWidth(), thumbnail.getIconHeight());
			lblThumbnail.setBounds(bds);
			drawingLayer.setBounds(bds);
		} catch (Exception ignored) {}
	}

	public ImageNavigatorPanel(ILookPanel displayPanel) {
		this();
		this.displayPanel = displayPanel;
		this.vp = displayPanel.getScrollPane().getViewport();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// handle the gestures on the drawing layer
		// compute the displacement vector
		Point p = e.getPoint();
//		System.out.println(dragStart);
//		System.out.println(p);
		double dx = p.getX() - dragStart.getX();
		double dy = p.getY() - dragStart.getY();
		
		// compute the new location on the view port
		double newX = viewRectangle.getX() + dx;
		double newY = viewRectangle.getY() + dy;
		
//		System.out.println(viewRectangle);
//		System.out.println(newX + ", " + newY);

//		System.out.print("dX=" + dx + ", dY="+dy + " --> ");

		// handle boundary conditions
		if (newX < 0.0d) {
			newX = 0.0d;
		}
		if (newY < 0.0d) {
			newY = 0.0d;
		}
		if (newX > (thumbnail.getIconWidth() - viewRectangle.getWidth())) {
			newX = (thumbnail.getIconWidth() - viewRectangle.getWidth())-1;
		}
		if (newY > (thumbnail.getIconHeight() - viewRectangle.getHeight())) {
			newY = (thumbnail.getIconHeight() - viewRectangle.getHeight())-1;
		}
//		System.out.println(newX + ", " + newY);

		// re-calculate the view port position for each move
		Point newViewPosition = new Point();
		newViewPosition.setLocation(
				(int) (newX / thumbnail.getIconWidth() * displayPanel.getRenderedImage().getWidth()),
				(int) (newY / thumbnail.getIconHeight() * displayPanel.getRenderedImage().getHeight())
				);
		viewRectangle.setLocation((int) newX, (int) newY);
		
		vp.setViewPosition(newViewPosition);
		
		// set the new starting point 
		dragStart = p;
		drawingLayer.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// get the drag-layer from the layeredPane and check if
		// the user moves over the rectangle of the viewport
		drawingLayer.trackMouse(e.getPoint());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("removing listeners");
		// unregister viewport listeners 
		vpChangeListeners = vp.getChangeListeners();
		for (ChangeListener cl : vpChangeListeners){
			if (cl instanceof ViewPortChangeListener)
				vp.removeChangeListener(cl);
		}
		
		// record the start point
		dragStart = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// add the change listeners again
		for (ChangeListener cl : vpChangeListeners){
			if (cl instanceof ViewPortChangeListener)
				vp.addChangeListener(cl);
		}
		System.out.println("re-added listeners");
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	/**
	 * Updates the navigator view with a thumb nail of the current image from
	 * the {@link ImageLayer}.
	 */
	public void updateNavigator() {
		// get image thumb nail from IqmDataBox
		try {
//			ITank tank = Application.getTank();
//			int tankIndex = tank.getCurrIndex();
//			if (tankIndex < 0) {
//				reset();
//				return;
//			}
//			int managerIndex = Application.getManager().getCurrItemIndex();
//			if (managerIndex < 0) {
//				reset();
//				return;
//			}
//			IqmDataBox box = tank.getTankIqmDataBoxAt(tankIndex, managerIndex);
			
			IqmDataBox box = new IqmDataBox(Application.getLook().getCurrentImage());
						
			// just continue if the box contains an image
			if (box.getDataType() != DataType.IMAGE){
				reset();
				return;
			}
			
			// get the thumb nail
			thumbnail = box.getThumbnail();
			int thnailWidth = thumbnail.getIconWidth();
			int thnailHeight = thumbnail.getIconHeight();

			// set the icon to the label
			lblThumbnail.setIcon(thumbnail);
			Rectangle bds = new Rectangle(Math.round((getWidth() + 1) / 2.0f - (thnailWidth + 1)
					/ 2.0f), 0, thnailWidth, thnailHeight);
			imagePanel.setBounds(bds);
			lblThumbnail.setBounds(bds);
			// get the updated bounds
			drawingLayer.setBounds(bds);

			// get the dimensions of the visible rectangle
			Rectangle viewPortRect = vp.getVisibleRect();
			Point viewPosition = vp.getViewPosition();
			
			Rectangle imageBounds = displayPanel.getRenderedImage().getBounds();

//			System.out.println("Image Bounds: " + imageBounds);
//			System.out.println("Viewport    : " + viewPortRect);
//			System.out.println("ViewPosition: " + viewPosition);
			
			// get the boundaries within the image bounds as intersection of
			// visible rectangle with image bounds
			this.visibleRect = viewPortRect.intersection(imageBounds);

//			System.out.println("Intersection: " + visibleRect);
			
			// if the visible rectangle fits into the screen, draw the entire border
			if (visibleRect.contains(imageBounds)){
				this.viewRectangle = new Rectangle(thnailWidth-1, thnailHeight-1);
			} else {
				// otherwise scale the visible rectangle down to the thumbnail size
				int sWidth = (int) (viewPortRect.getWidth() * thnailWidth / imageBounds.getWidth());
				int sHeight = (int) (viewPortRect.getHeight() * thnailHeight / imageBounds.getHeight()); 
				
				Dimension smallDim = new Dimension(
						Math.min(sWidth, thnailWidth-1), 
						Math.min(sHeight, thnailHeight-1));
				
				// translate the view position, too
				Point location = new Point(
						(int) (viewPosition.getX() * thnailWidth / imageBounds.getWidth()),
						(int) (viewPosition.getY() * thnailHeight / imageBounds.getHeight()));
				
				// scale the rectangle again for drawing using the zoom factor
				this.viewRectangle = new Rectangle(location.x, location.y,
						smallDim.width, smallDim.height);
			}
			
//			System.out.println("Small Rectngl:" + this.viewRectangle.getBounds());
//			System.out.println("Small Rectngl:" + this.viewRectangle.getLocation());

			layeredPane.revalidate();
			layeredPane.repaint();
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Update of image navigator failed - maybe that a current image in the Look panel is missing");
		}
	}

	/**
	 * Clears the navigator view.
	 */
	public void reset() {
		lblThumbnail.setBounds(0, 0, 0, 0);
		lblThumbnail.setIcon(null);
		lblThumbnail.revalidate();
		lblThumbnail.repaint();

		drawingLayer.setBounds(0, 0, 0, 0);
		drawingLayer.revalidate();
		drawingLayer.repaint();

		layeredPane.revalidate();
		layeredPane.repaint();
	}

	private class DrawingLayer extends JLabel {

		/**
		 * The UID for serialization.
		 */
		private static final long serialVersionUID = 1L;

		public DrawingLayer() {
			super();
			setOpaque(false);
			setLayout(null);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			//System.out.println("painting");
			Graphics2D g2d = (Graphics2D) g;
			
			// store rectangle in temporary variable
			Rectangle tmp = (Rectangle) viewRectangle.clone();
			
			// draw black border
			g2d.setStroke(new BasicStroke(1.0F));
			g2d.setColor(Color.black);
			g2d.draw(tmp);
			
			// shrink the rectangle and draw white inner border
			tmp.grow(-1, -1);
			g2d.setColor(Color.cyan);
			g2d.draw(tmp);
//			g2d.setPaint(new Color(0.85f, 1.0f, 1.0f, 0.5f));
//			g2d.fillRect(tmp.x + 1, tmp.y + 1,
//					tmp.width-1, tmp.height-1);
			g2d.dispose();
		}

		public void trackMouse(Point p) {
			if (viewRectangle.contains(p)){
				DrawingLayer.this.setCursor(CursorFactory.getMoveCursor());
			}else {
				DrawingLayer.this.setCursor(CursorFactory.getDefaultCursor());
			}
		}
	}
}
