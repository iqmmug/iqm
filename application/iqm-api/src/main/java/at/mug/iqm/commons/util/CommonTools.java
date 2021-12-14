package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: CommonTools.java
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.util.Vector;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;

 
 

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.IMainFrame;
import at.mug.iqm.api.gui.roi.AngleROI;
import at.mug.iqm.api.operator.DataType;

/**
 * This class provides tools for common usage.
 * 
 * @author Philipp Kainz
 * 
 */
public final class CommonTools {
	/**
	 * Custom class logger.
	 */
	  
	/**
	 * The re-show delay for the {@link ToolTipManager}. Default is '0'.
	 */
	private static int toolTipReshowDelay = 0;
	/**
	 * The dismiss delay for the ToolTipManager. Default is '0'.
	 */
	private static int toolTipDismissDelay = 0;
	/**
	 * The initial delay for the ToolTipManager. Default is '0'.
	 */
	private static int toolTipInitDelay = 0;

	/**
	 * This method sets the default toolTipDelays It is called at startup
	 */
	public static void setDefaultToolTipDelays() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		toolTipInitDelay = ttm.getInitialDelay();
		toolTipReshowDelay = ttm.getReshowDelay();
		toolTipDismissDelay = ttm.getDismissDelay();
	}

	/**
	 * This method gets the default toolTipInitDelay It is called e.g after
	 * jFreeChart
	 * 
	 * @return int toolTipInitDelay
	 */
	public static int getDefaultToolTipInitDelay() {
		return toolTipInitDelay;
	}

	/**
	 * This method gets the default toolTipReshowDelay It is called e.g after
	 * jFreeChart
	 * 
	 * @return int toolTipReshowDelay
	 */
	public static int getDefaultToolTipReshowDelay() {
		return toolTipReshowDelay;
	}

	/**
	 * This method gets the default toolTipDismissDelay It is called e.g after
	 * jFreeChart
	 * 
	 * @return int toolTipDismissDelay
	 */
	public static int getDefaultToolTipDismissDelay() {
		return toolTipDismissDelay;
	}

	/**
	 * Centers a specified {@link Window} on the screen.
	 */
	public static void centerFrameOnScreen(Window w) {
		w.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth() - w.getWidth()) / 2, (int) (Toolkit
				.getDefaultToolkit().getScreenSize().getHeight() - w
				.getHeight()) / 2);
	}

	/**
	 * Displays the {@link BufferedImage} in a separate {@link JFrame}.
	 * 
	 * @param image
	 */
	public static void showImage(BufferedImage image, String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel p = new JPanel();
		JLabel picLabel = new JLabel(new ImageIcon(image));
		p.add(picLabel);
		frame.getContentPane().add(p);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Displays the {@link ImageIcon} in a separate {@link JFrame}.
	 * 
	 * @param image
	 */
	public static void showImage(ImageIcon image, String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel p = new JPanel();
		JLabel picLabel = new JLabel(image);
		p.add(picLabel);
		frame.getContentPane().add(p);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Displays the {@link Image} in a separate {@link JFrame}.
	 * 
	 * @param image
	 */
	public static void showImage(Image image, String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel p = new JPanel();
		JLabel picLabel = new JLabel(new ImageIcon(image));
		p.add(picLabel);
		frame.getContentPane().add(p);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Creates a buffered image of a given {@link Component}'s graphics.
	 * 
	 * @param component
	 *            the component to be rendered
	 * @return a {@link BufferedImage} of type
	 *         {@link BufferedImage#TYPE_INT_RGB}.
	 */
	public static BufferedImage createSnapshot(Component component) {
		Dimension size = component.getSize();
		BufferedImage image = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2D = (Graphics2D) image.getGraphics();
		component.paint(g2D);
		return image;
	}

	/**
	 * Starts a wait cursor on the specified {@link JFrame}.
	 * 
	 * @param window
	 *            the frame to be locked
	 */
	public static void disableUserInteraction(JFrame window) {
		CursorToolkit.startWaitCursor(window.getRootPane());
	}

	/**
	 * Stops the wait cursor and re-enables the user interaction on a given
	 * {@link JFrame}.
	 * 
	 * @param window
	 *            the frame to be unlocked
	 */
	public static void enableUserInteraction(JFrame window) {
		CursorToolkit.stopWaitCursor(window.getRootPane());
	}

	/**
	 * Starts a wait cursor on the the {@link IMainFrame}.
	 */
	public static void disableMainFrameInteraction() {
		CursorToolkit.startWaitCursor(((JFrame) Application.getMainFrame())
				.getRootPane());
	}

	/**
	 * Stops the wait cursor and re-enables the user interaction on the
	 * {@link IMainFrame}.
	 */
	public static void enableMainFrameInteraction() {
		CursorToolkit.stopWaitCursor(((JFrame) Application.getMainFrame())
				.getRootPane());
	}

	/**
	 * Aligns all elements of the integer array in a single one-line
	 * {@link String}.
	 * 
	 * @param array
	 * @return a {@link String}
	 */
	public static String intArrayToString(int[] array) {
		String result = "";
		try {
			String s = "[";

			for (int i : array)
				s += i + ", ";

			result = s.substring(0, s.length() - 2) + "]";
		} catch (Exception ex) {
			System.out.println("Cannot convert to String: "
					+ ex.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * Aligns all elements of a double array in a single one-line {@link String}
	 * .
	 * 
	 * @param array
	 * @return a {@link String}
	 */
	public static String doubleArrayToString(double[] array) {
		String result = "";
		try {
			String s = "[";

			for (double i : array)
				s += i + ", ";

			result = s.substring(0, s.length() - 2) + "]";
		} catch (Exception ex) {
			System.out.println("Cannot convert to String: "
					+ ex.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * Aligns all elements of an {@link Object} array in a single one-line
	 * {@link String}. The standard {@link #toString()} method is applied to
	 * each element.
	 * 
	 * @param array
	 * @return a {@link String}
	 */
	public static String arrayToString(Object[] array) {
		String result = "";
		try {
			String s = "[";

			for (Object i : array)
				s += i.toString() + ", ";

			result = s.substring(0, s.length() - 2) + "]";
		} catch (Exception ex) {
			System.out.println("Cannot convert to String: "
					+ ex.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * Checks the modifier for a given mask.
	 * 
	 * @param modifiers
	 * @param mask
	 * @return <code>true</code> if the modifier matches, <code>false</code>
	 *         otherwise
	 */
	public static boolean checkModifiers(int modifiers, int mask) {
		return ((modifiers & mask) == mask);
	}

	/**
	 * This method zooms an image.
	 * 
	 * @param pi
	 *            - the source image
	 * @return the zoomed image
	 */
	public static PlanarImage zoomImage(PlanarImage pi, double zoom) {

		if (pi == null) {
			throw new ImagingOpException("Unable to scale: Image is null!");
		}

		long start = System.currentTimeMillis();

		ParameterBlockJAI zoomPB = new ParameterBlockJAI("scale");
		zoomPB.addSource(pi);
		zoomPB.setParameter("xScale", (float) zoom);
		zoomPB.setParameter("yScale", (float) zoom);
		zoomPB.setParameter("xTrans", 0.0F);
		zoomPB.setParameter("yTrans", 0.0F);
		zoomPB.setParameter("interpolation", new InterpolationNearest());
		pi = JAI.create("scale", zoomPB);

		System.out.println("IQM Info: Image scaling [" + String.valueOf(zoom) + "] done in: "
				+ (System.currentTimeMillis() - start) / 1000.0d + " s.");

		// showImage(pi.getAsBufferedImage(), "Zoom: " + String.valueOf(zoom));

		return pi;
	}

	/**
	 * This method calculates the real image pixel position.
	 * 
	 * @param p
	 *            pixel position of device
	 * @param zoom
	 *            the zoom value in double precision
	 * @return the image pixel position
	 */
	public static Point getRealPixelPosition(Point p, double zoom) {
		Point pNew = new Point(); // image pixels
		pNew.setLocation((int) (p.getX() / zoom), (int) (p.getY() / zoom));
		return pNew;
	}

	/**
	 * This method calculates the real image pixel position.
	 * 
	 * @param p
	 *            pixel position of device
	 * @param zoom
	 *            the zoom value in double precision
	 * @return the image pixel position
	 */
	public static Point getRealPixelPosition(Point2D p, double zoom) {
		return getRealPixelPosition(new Point((int) p.getX(), (int) p.getY()),
				zoom);
	}

	/**
	 * This method prints the length value of a line
	 * 
	 * @param coords
	 * @return a double precision length
	 */
	public static double calcLength(Vector<Float> coords) {
		double lineLength = 0d;
		if (coords.size() == 4) {
			double lx = coords.get(0) - coords.get(2);
			double ly = coords.get(1) - coords.get(3);
			lineLength = Math.sqrt(lx * lx + ly * ly); // Pythagoras
			System.out.println("IQM Trace: Line length: " + lineLength);
		} else {
			System.out.println("IQM Error: Length calculation not possible for this shape!");
		}
		return lineLength;
	}

	/**
	 * This method calculates the angle value of a given shape object.
	 * 
	 * @param s
	 *            a shape object, e.g. {@link AngleROI}
	 * @return a double[], containing the small angle on index <code>0</code>,
	 *         the large angle on index <code>1</code>, the first leg length on
	 *         index <code>2</code>, and the second leg length on index
	 *         <code>3</code>
	 */
	public static double[] calcAngleAndLegLenghts(AngleROI s) {
		Vector<Float> coords = getShapeCoordinates(s);
		System.out.println("IQM:  Shape Coordinates: " + coords);
		double[] result = { 0.0d, 0.0d, 0.0d, 0.0d };

		// if the first line is moved
		if (coords.size() == 4) {
			// calculate angle and line lenghts
			// line 1 length
			double l1x = coords.get(0) - coords.get(2);
			double l1y = coords.get(1) - coords.get(3);

			double length1 = Math.sqrt(l1x * l1x + l1y * l1y);

			result[2] = length1;
		}

		// if the first point has been drawn and e.g. the mouse is still moving,
		// show partial results
		else if (coords.size() >= 6) {
			// calculate angle and line lenghts
			// line 1 length
			double l1x = coords.get(0) - coords.get(2);
			double l1y = coords.get(1) - coords.get(3);
			double l2x = coords.get(4) - coords.get(2);
			double l2y = coords.get(5) - coords.get(3);

			double dotP = l1x * l2x + l1y * l2y; // dot Product
			double length1 = Math.sqrt(l1x * l1x + l1y * l1y);
			double length2 = Math.sqrt(l2x * l2x + l2y * l2y);
			double denominator = length1 * length2;
			double product = denominator != 0.0 ? dotP / denominator : 0.0;
			double angle = Math.toDegrees(Math.acos(product));
			double angle2 = 360 - Math.abs(angle);
			double angleSmall;
			double angleLarge;
			if (angle2 < angle) {
				angleSmall = angle2;
				angleLarge = angle;
			} else {
				angleSmall = angle;
				angleLarge = angle2;
			}

			result[0] = angleSmall;
			result[1] = angleLarge;
			result[2] = length1;
			result[3] = length2;
		}
		System.out.println("IQM:  Angles and LegLengths: " + doubleArrayToString(result));
		return result;
	}

	/**
	 * This method gets the Polygon (shape) coordinates.
	 * 
	 * @return vector of {@link ROIShape}
	 */
	@SuppressWarnings("unused")
	public static Vector<Float> getShapeCoordinates(ROIShape roiShape) {
		// for single segment data
		float[] ff = new float[6];

		// coordinates for 3 Polygon points
		Vector<Float> coords = new Vector<Float>();

		int numSegment = -1;
		for (PathIterator pIt = (roiShape.getAsShape()).getPathIterator(null); !pIt
				.isDone();) {
			int type = pIt.currentSegment(ff);
			int n = 0;
			switch (type) {
			case PathIterator.SEG_MOVETO:
			case PathIterator.SEG_LINETO:
				numSegment += 1;
				n = 1;
				break;
			case PathIterator.SEG_QUADTO:
				n = 2;
				break;
			case PathIterator.SEG_CUBICTO:
				n = 3;
				break;
			default:
				// do nothing
			}

			coords.addElement(ff[0]); // only coordinates are taken
			coords.addElement(ff[1]);
			pIt.next();
		}
		return coords;
	}

	/**
	 * This method sets the tab index in the main frame according to the given
	 * data type.
	 * 
	 * @param dataType
	 *            the {@link DataType}
	 */
	public static void setTabForDataType(final DataType dataType) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				switch (dataType) {
				case IMAGE:
					Application.getMainFrame().getItemContent()
							.setSelectedIndex(0);
					break;

				case PLOT:
					Application.getMainFrame().getItemContent()
							.setSelectedIndex(1);
					break;

				case TABLE:
					Application.getMainFrame().getItemContent()
							.setSelectedIndex(2);
					break;

				case CUSTOM:
					Application.getMainFrame().getItemContent()
							.setSelectedIndex(3);
					break;

				default:
					break;
				}
			}
		});
	}

	/**
	 * Show a dialog in order to locate a single file.
	 * 
	 * @param dialogType
	 *            {@link JFileChooser#SAVE_DIALOG}, or
	 *            {@link JFileChooser#OPEN_DIALOG}
	 * @return
	 */
	public static File showFileDialog(int dialogType) {
		return CommonTools.showFileDialog(dialogType, null);
	}
	
	/**
	 * Show a dialog in order to locate a single file, 
	 * optionally stating the file name.
	 * 
	 * @param dialogType
	 *            {@link JFileChooser#SAVE_DIALOG}, or
	 *            {@link JFileChooser#OPEN_DIALOG}
	 * @param filename
	 * 			the file name to be set to the dialog
	 * @return
	 */
	public static File showFileDialog(int dialogType, String filename) {
		JFileChooser fc = new JFileChooser();
		fc.setName("CommonTools.SingleFileChooserDialog");
		
		PropertyManager pm = PropertyManager.getManager(fc.getName());
		
		String curDir = pm.getProperty("lastDir");
		if (curDir == null){
			curDir = System.getProperty("user.home");
		}
		fc.setCurrentDirectory(new File(curDir));

		JFrame frame = new JFrame();
		frame.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.application.grey.32x32")).getImage());
		
		frame.setTitle(I18N.getGUILabelText("application.dialog.filechooser.single.generic.title"));
		
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);

		int selection = -1;
		if (dialogType == JFileChooser.SAVE_DIALOG){
			if (filename != null){
				fc.setSelectedFile(new File(filename));
			}
			selection = fc.showSaveDialog(frame);
		} else if (dialogType == JFileChooser.OPEN_DIALOG){
			selection = fc.showOpenDialog(frame);
		} else {
			System.out.println("IQM Error: The option '" + dialogType + "' is not recognized for the file chooser dialog!");
			return null;
		}
		
		if (selection != JFileChooser.APPROVE_OPTION){
			return null;
		} else {
			pm.setProperty("lastDir", fc.getCurrentDirectory().toString());
			return fc.getSelectedFile();
		}
		
	}

}
