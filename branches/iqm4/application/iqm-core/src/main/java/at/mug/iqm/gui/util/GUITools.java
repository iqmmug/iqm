package at.mug.iqm.gui.util;

/*
 * #%L
 * Project: IQM - Application Core
 * File: GUITools.java
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.util.List;
import java.util.Vector;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import at.mug.iqm.api.gui.IStatusPanel;
import at.mug.iqm.api.gui.roi.AngleROI;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.gui.LogFrame;
import at.mug.iqm.gui.MainFrame;
import at.mug.iqm.gui.StatusPanel;
import at.mug.iqm.gui.TableDisplayFrame;
import at.mug.iqm.gui.dialog.AboutIQMDialog;
import at.mug.iqm.gui.tilecachetool.TCTool;

/**
 * @author Philipp Kainz
 * 
 */
public final class GUITools {

	// class specific logger
	private static final Logger logger = Logger.getLogger(GUITools.class);
	/**
	 * The top-most GUI component of IQM. Default is 'null'.
	 */
	private static MainFrame mainFrame = null;
	/**
	 * The status panel. Default is 'null';
	 */
	private static StatusPanel statusPanel = null;
	/**
	 * The frame for displaying a result from an operator in a table. Default is
	 * 'null'.
	 */
	private static TableDisplayFrame currTableJFrame = null;
	/**
	 * The TileCache Tool to be displayed. This variable is queried whether the
	 * frame is already visible or not. Default is 'null'.
	 */
	private static TCTool tcTool = null;

	/**
	 * The instance of the user log window. This variable is queried whether the
	 * frame is already visible or not. Default is 'null'.
	 */
	private static LogFrame logWindow = null;

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
	 * Displays the {@link JTable} in a {@link JFrame}.
	 * 
	 * @param table
	 */
	public static void showTable(JTable table) {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		frame.setLayout(new BorderLayout());

		Container container = frame.getContentPane();
		container.add(new JScrollPane(table), BorderLayout.CENTER);

		frame.setPreferredSize(new Dimension(200, 200));
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * This method calculates the real image pixel position. Mainly for mouse
	 * cursor events.
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

		logger.trace("Zoom received in the GUITools="
				+ String.format("%2s", Double.valueOf(zoom * 100.0d)) + "%");

		ParameterBlockJAI zoomPB = new ParameterBlockJAI("scale");
		zoomPB.addSource(pi);
		zoomPB.setParameter("xScale", (float) zoom);
		zoomPB.setParameter("yScale", (float) zoom);
		zoomPB.setParameter("xTrans", 0.0F);
		zoomPB.setParameter("yTrans", 0.0F);
		zoomPB.setParameter("interpolation", new InterpolationNearest()); // just
																			// resample
		pi = JAI.create("scale", zoomPB);

		// showImage(pi.getAsBufferedImage(), "Zoom: " + String.valueOf(zoom));

		return pi;
	}

	/**
	 * This method gets the Polygon (shape) coordinates.
	 * 
	 * @return vector of {@link ROIShape}
	 */
	public static Vector<Float> getShapeCoordinates(ROIShape roiShape) {
		return CommonTools.getShapeCoordinates(roiShape);
	}

	/**
	 * This method prints the length value of a line
	 * 
	 * @param coords
	 * @return a double precision length
	 */
	public static double calcLength(Vector<Float> coords) {
		return CommonTools.calcLength(coords);
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
		return CommonTools.calcAngleAndLegLenghts(s);
	}

	/**
	 * This method displays the Table data
	 * 
	 * @param table
	 */
	public static void displayTableData(JTable table) {

		TableDisplayFrame frame = null;
		frame = GUITools.getCurrTableJFrame();
		if (frame == null) {
			frame = new TableDisplayFrame();
			// Get the size of the default screen
			Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
			int xLoc = dimScreen.width / 3 * 2 - 20;
			int yLoc = dimScreen.height / 4 * 1;
			frame.setLocation(xLoc, yLoc);
			frame.createAndShowGUI();
			frame.setResizable(true);
			frame.setAlwaysOnTop(true);
		}
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // IMPORTANT FOR
															// SCROLLBARS!
		frame.setTable(table);
		GUITools.setCurrTableJFrame(frame);
		frame.setVisible(true);
		frame.toFront();
	}

	/**
	 * This method displays joined rows in a {@link JTable}.
	 * 
	 * @param boxList
	 *            the list of {@link IqmDataBox}es containing the
	 *            {@link TableModel}s
	 */
	public static void displayJoinedTables(List<IqmDataBox> boxList) {
		int numTables = boxList.size();
		IqmDataBox b = boxList.get(0);
		if (b instanceof VirtualDataBox) {
			b = VirtualDataManager.getInstance().load((IVirtualizable) b);
		}
		TableModel model = b.getTableModel();
		JTable jTable = new JTable(model);
		int numColumns = model.getColumnCount();
		model.removeTableModelListener(jTable); // adding a lot of data would be
												// very slow due to active model
												// operatorProgressListener
		Object[] newDummyValues = new Object[numColumns];
		for (int n = 1; n < numTables; n++) { // model loop
			int numRows = model.getRowCount();
			IqmDataBox box = boxList.get(n);
			if (box instanceof VirtualDataBox) {
				box = VirtualDataManager.getInstance().load(
						(IVirtualizable) box);
			}

			TableModel newModel = box.getTableModel();
			int numNewRows = newModel.getRowCount();
			for (int r = 0; r < numNewRows; r++) {// Row loop
				model.addRow(newDummyValues);
				for (int c = 0; c < numColumns; c++) { // Columns loop
					Object newValue = newModel.getValueAt(r, c);
					model.setValueAt(newValue, r + numRows, c);
				}
			}
		}
		model.addTableModelListener(jTable);
		model.fireTableStructureChanged(); // this is mandatory because it
											// updates the table
		displayTableData(jTable);
	}

	/**
	 * This method gets the {@link MainFrame}.
	 * 
	 * @return the main frame
	 */
	public static MainFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * This method sets the IqmFrame
	 * 
	 * @param arg
	 */
	public static void setMainFrame(MainFrame arg) {
		mainFrame = arg;
	}

	/**
	 * @return the statusPanel
	 */
	public static IStatusPanel getStatusPanel() {
		return statusPanel;
	}

	/**
	 * @param statusPanel
	 *            the statusPanel to set
	 */
	public static void setStatusPanel(StatusPanel statusPanel) {
		GUITools.statusPanel = statusPanel;
	}

	/**
	 * @return the tcTool
	 */
	public static TCTool getTcTool() {
		return tcTool;
	}

	/**
	 * @param tcTool
	 *            the tcTool to set
	 */
	public static void setTcTool(TCTool tcTool) {
		GUITools.tcTool = tcTool;
	}

	/**
	 * @return the logWindow
	 */
	public static LogFrame getLogFrame() {
		return logWindow;
	}

	/**
	 * @param logWindow
	 *            the logWindow to set
	 */
	public static void setLogFrame(LogFrame logWindow) {
		GUITools.logWindow = logWindow;
	}

	/**
	 * This method sets current JTable JFrame
	 * 
	 * @param fr
	 */
	public static void setCurrTableJFrame(TableDisplayFrame fr) {
		currTableJFrame = fr;
	}

	/**
	 * This method gets current frame for displaying a {@link TableModel}.
	 * 
	 * @return the {@link TableDisplayFrame}
	 */
	public static TableDisplayFrame getCurrTableJFrame() {
		return currTableJFrame;
	}

	/**
	 * Starts a wait cursor on the specified {@link JFrame}.
	 * 
	 * @param window
	 *            the frame to be locked
	 */
	public static void disableUserInteraction(JFrame window) {
		CommonTools.disableUserInteraction(window);
	}

	/**
	 * Stops the wait cursor and re-enables the user interaction on a given
	 * {@link JFrame}.
	 * 
	 * @param window
	 *            the frame to be unlocked
	 */
	public static void enableUserInteraction(JFrame window) {
		CommonTools.enableUserInteraction(window);
	}

	/**
	 * Starts a wait cursor on the specified {@link JFrame}.
	 */
	public static void disableMainFrameInteraction() {
		CommonTools.disableMainFrameInteraction();
	}

	/**
	 * Stops the wait cursor and re-enables the user interaction on a given
	 * {@link JFrame}.
	 */
	public static void enableMainFrameInteraction() {
		CommonTools.enableMainFrameInteraction();
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
	 * Shows the 'About IQM' dialog.
	 */
	public static void showAboutDialog() {
		AboutIQMDialog aboutDialog = new AboutIQMDialog();
		aboutDialog.setLocationRelativeTo(getMainFrame());
		aboutDialog.setVisible(true);
	}

}
