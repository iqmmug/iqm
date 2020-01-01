package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: PlotMenu.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

import javax.media.jai.Histogram;
import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.table.JTableHeader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.exception.OperatorNotAllowedException;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.gui.PlotSelectionFrame;
import at.mug.iqm.api.gui.roi.AbstractROIShape;
import at.mug.iqm.api.gui.roi.RectangleROI;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.api.plot.charts.ChartType;
import at.mug.iqm.api.plot.charts.HistogramChart;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.image.ImageAnalyzer;
import at.mug.iqm.commons.util.plot.PlotTools;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.workflow.Look;
import at.mug.iqm.core.workflow.Plot;
import at.mug.iqm.core.workflow.Table;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.util.GUITools;

/**
 * This is the base class for the plot menu in IQM.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 */
public class PlotMenu extends DeactivatableMenu implements ActionListener {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -4917148341896174270L;

	// class specific logger
	private static Class<?> caller = PlotMenu.class;
	private static final Logger logger = LogManager.getLogger(PlotMenu.class);

	// class variable declaration
	private OperatorMenuItem projectToXMenuItem;
	private OperatorMenuItem projectToYMenuItem;

	private DeactivatableMenu intensityProfilesMenu;
	private OperatorMenuItem rowProfileMenuItem;
	private OperatorMenuItem columnProfileMenuItem;

	private DeactivatableMenu histogramMenu;
	private OperatorMenuItem histogramMenuItem;
	private OperatorMenuItem histogramROIMenuItem;
	
	private OperatorMenuItem plotColumnMenuItem;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped PlotMenu.
	 */
	public PlotMenu() {
		logger.debug("Generating new instance of 'plot' menu.");

		// initialize the variables
		this.projectToXMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.projectToYMenuItem = new OperatorMenuItem(OperatorType.IMAGE);

		this.intensityProfilesMenu = new DeactivatableMenu();
		this.rowProfileMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.columnProfileMenuItem = new OperatorMenuItem(OperatorType.IMAGE);

		this.histogramMenu = new DeactivatableMenu();
		this.histogramMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		this.histogramROIMenuItem = new OperatorMenuItem(OperatorType.IMAGE);
		
		// table operator
		this.plotColumnMenuItem = new OperatorMenuItem(OperatorType.TABLE);

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Menu 'plot' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items in 'plot' menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.plot.text"));

		// assemble: add created elements to the JMenu
		this.add(this.createHistogramMenu());
		this.add(this.createIntensityProfileMenu());
		this.addSeparator();
		this.add(this.createProjectToXMenuItem());
		this.add(this.createProjectToYMenuItem());
		this.addSeparator();
		this.add(this.createPlotColumnMenuItem());
	}

	/**
	 * This method initializes the histogram menu.
	 * 
	 * @return {@link javax.swing.JMenu}
	 */
	private JMenu createHistogramMenu() {
		this.histogramMenu.setText(I18N.getGUILabelText("menu.histogram.text"));

		this.histogramMenu.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.plot.histogram")));

		this.histogramMenu.add(this.createHistogramMenuItem());
		this.histogramMenu.add(this.createHistogramROIMenuItem());

		return this.histogramMenu;
	}

	/**
	 * This method initializes the intensity profile menu.
	 * 
	 * @return {@link javax.swing.JMenu}
	 */
	private JMenu createIntensityProfileMenu() {
		this.intensityProfilesMenu.setText(I18N
				.getGUILabelText("menu.plot.profiles.text"));

		this.intensityProfilesMenu.add(this.createRowProfileMenuItem());
		this.intensityProfilesMenu.add(this.createColumnProfileMenuItem());

		return this.intensityProfilesMenu;
	}

	private JMenuItem createRowProfileMenuItem() {
		this.rowProfileMenuItem.setText(I18N
				.getGUILabelText("menu.plot.profiles.row.text"));
		this.rowProfileMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.plot.profiles.row.ttp"));
		this.rowProfileMenuItem.setActionCommand("rowprofile");
		this.rowProfileMenuItem.addActionListener(this);

		return this.rowProfileMenuItem;
	}

	private JMenuItem createColumnProfileMenuItem() {
		this.columnProfileMenuItem.setText(I18N
				.getGUILabelText("menu.plot.profiles.col.text"));
		this.columnProfileMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.plot.profiles.col.ttp"));
		this.columnProfileMenuItem.setActionCommand("colprofile");
		this.columnProfileMenuItem.addActionListener(this);

		return this.columnProfileMenuItem;
	}

	/**
	 * This method initializes histogramMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createHistogramMenuItem() {
		this.histogramMenuItem.setText(I18N
				.getGUILabelText("menu.histogram.full.text"));
		this.histogramMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.histogram.full.ttp"));
		this.histogramMenuItem.addActionListener(this);
		this.histogramMenuItem.setActionCommand("histogram");
		return this.histogramMenuItem;
	}

	/**
	 * This method initializes histogramROIMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createHistogramROIMenuItem() {
		this.histogramROIMenuItem.setText(I18N
				.getGUILabelText("menu.histogram.roi.text"));
		this.histogramROIMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.histogram.roi.ttp"));
		this.histogramROIMenuItem.addActionListener(this);
		this.histogramROIMenuItem.setActionCommand("histogramROI");
		return this.histogramROIMenuItem;
	}

	/**
	 * This method initializes projectToXMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createProjectToXMenuItem() {
		this.projectToXMenuItem.setText(I18N
				.getGUILabelText("menu.plot.projectToX.text"));
		this.projectToXMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.plot.projectToX.ttp"));
		this.projectToXMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.plot.projectToX")));
		this.projectToXMenuItem.addActionListener(this);
		this.projectToXMenuItem.setActionCommand("projecttox");
		return this.projectToXMenuItem;
	}

	/**
	 * This method initializes projectToYMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createProjectToYMenuItem() {
		this.projectToYMenuItem.setText(I18N
				.getGUILabelText("menu.plot.projectToY.text"));
		this.projectToYMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.plot.projectToY.ttp"));
		this.projectToYMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.plot.projectToY")));
		this.projectToYMenuItem.addActionListener(this);
		this.projectToYMenuItem.setActionCommand("projecttoy");
		return this.projectToYMenuItem;
	}
	
	/**
	 * This method initializes plotColumnMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createPlotColumnMenuItem() {
		this.plotColumnMenuItem.setText(I18N
				.getGUILabelText("menu.plot.column.text"));
		this.plotColumnMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.plot.column.ttp"));
		this.plotColumnMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.plot.column")));
		this.plotColumnMenuItem.addActionListener(this);
		this.plotColumnMenuItem.setActionCommand("plotcolumn");
		return this.plotColumnMenuItem;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	/**
	 * This method sets and performs the corresponding actions to the menu
	 * items.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " -- Modifiers: "
				+ e.getModifiers());

		if ("histogram".equals(e.getActionCommand())) {
			int idx = Application.getManager().getCurrItemIndex();
			IqmDataBox iqmDataBox = Tank.getInstance()
					.getCurrentTankIqmDataBoxAt(idx);
			if (iqmDataBox instanceof IVirtualizable) {
				iqmDataBox = VirtualDataManager.getInstance().load(
						(IVirtualizable) iqmDataBox);
			}
			PlanarImage pi = iqmDataBox.getImage();

			if (CommonTools.checkModifiers(e.getModifiers(),
					ActionEvent.ALT_MASK)) {
				PlotTools.showHistogram(pi);
			} else {
				// create histogram
				Plot.getInstance().reset();
				Histogram histo = PlotTools.getHistogram(pi);
				Plot.getInstance().setChartData(
						new HistogramChart(histo, "Histogram"),
						ChartType.IMAGE_HISTOGRAM);
				Application.getMainFrame().setSelectedTabIndex(1);
			}
		}
		if ("histogramROI".equals(e.getActionCommand())) {
			int idx = Application.getManager().getCurrItemIndex();
			IqmDataBox iqmDataBox = Tank.getInstance()
					.getCurrentTankIqmDataBoxAt(idx);
			if (iqmDataBox instanceof IVirtualizable) {
				iqmDataBox = VirtualDataManager.getInstance().load(
						(IVirtualizable) iqmDataBox);
			}
			PlanarImage pi = iqmDataBox.getImage();

			if (Look.getInstance().getCurrentLookPanel().getCurrentROILayer()
					.isEmpty()) {
				DialogUtil.getInstance().showDefaultInfoMessage(
						I18N.getMessage("annotations.noInstance"));
				return;
			}

			// extract the sub image under the current ROI
			AbstractROIShape roiShape = (AbstractROIShape) Look.getInstance()
					.getCurrentLookPanel().getCurrentROILayer()
					.getCurrentROIShape();

			// get the pixels covered by the ROI shape's area
			if (!(roiShape instanceof RectangleROI)) {
				int sel = DialogUtil.getInstance().showDefaultWarnMessage(
						I18N.getMessage("warn.roiNotRectangular"));
				if (sel != DialogUtil.YES_OPTION) {
					logger.info("User cancelled creating ROI histogram.");
					return;
				}
			}

			BufferedImage bi = pi.getAsBufferedImage().getSubimage(
					roiShape.getBounds().x, roiShape.getBounds().y,
					roiShape.getBounds().width, roiShape.getBounds().height);

			PlanarImage pi2 = PlanarImage.wrapRenderedImage(bi);
			pi2.setProperty(
					"image_name",
					pi.getProperty("image_name") + " @ ("
							+ roiShape.getBounds().x + ","
							+ roiShape.getBounds().y + "), w="
							+ roiShape.getBounds().width + ", h="
							+ roiShape.getBounds().height);
			pi2.setProperty("file_name", pi.getProperty("file_name"));

			// show the histogram
			if (CommonTools.checkModifiers(e.getModifiers(),
					ActionEvent.ALT_MASK)) {
				PlotTools.showHistogram(pi2);
			} else {
				Plot.getInstance().reset();
				Histogram histo = PlotTools.getHistogram(pi2);
				Plot.getInstance().setChartData(
						new HistogramChart(histo, "Histogram"),
						ChartType.IMAGE_HISTOGRAM);
				Application.getMainFrame().setSelectedTabIndex(1);
			}
		}
		if ("projecttox".equals(e.getActionCommand())) {
			int idx = Application.getManager().getCurrItemIndex();
			IqmDataBox iqmDataBox = Tank.getInstance()
					.getCurrentTankIqmDataBoxAt(idx);
			if (iqmDataBox instanceof IVirtualizable) {
				iqmDataBox = VirtualDataManager.getInstance().load(
						(IVirtualizable) iqmDataBox);
			}
			PlanarImage pi = iqmDataBox.getImage();
			if (pi == null) {
				DialogUtil.getInstance().showErrorMessage(
						I18N.getMessage("application.operator.notAllowed"),
						new OperatorNotAllowedException(I18N
								.getMessage("application.missingImage")));
				BoardPanel.appendTextln(
						I18N.getMessage("application.missingImage"), caller);
			} else {
				if (!ImageAnalyzer.is8BitGrey(pi)) {
					DialogUtil.getInstance().showErrorMessage(
							I18N.getMessage("application.operator.notAllowed"),
							new OperatorNotAllowedException(I18N.getMessage(
									"operator.justSingleBand",
									e.getActionCommand())));
					BoardPanel.appendTextln(
							I18N.getMessage("operator.justSingleBand",
									e.getActionCommand()), caller);
				} else {
					PlotTools.projectImage(pi, IQMConstants.DIRECTION_X);
				}
			}
		}
		if ("projecttoy".equals(e.getActionCommand())) {
			int idx = Application.getManager().getCurrItemIndex();
			IqmDataBox iqmDataBox = Tank.getInstance()
					.getCurrentTankIqmDataBoxAt(idx);
			if (iqmDataBox instanceof IVirtualizable) {
				iqmDataBox = VirtualDataManager.getInstance().load(
						(IVirtualizable) iqmDataBox);
			}
			PlanarImage pi = iqmDataBox.getImage();
			if (pi == null) {
				DialogUtil.getInstance().showErrorMessage(
						I18N.getMessage("application.operator.notAllowed"),
						new OperatorNotAllowedException(I18N
								.getMessage("application.missingImage")));
				BoardPanel.appendTextln(
						I18N.getMessage("application.missingImage"), caller);
			} else {
				if (!ImageAnalyzer.is8BitGrey(pi)) {
					DialogUtil.getInstance().showErrorMessage(
							I18N.getMessage("application.operator.notAllowed"),
							new OperatorNotAllowedException(I18N.getMessage(
									"operator.justSingleBand",
									e.getActionCommand())));
					BoardPanel.appendTextln(
							I18N.getMessage("operator.justSingleBand",
									e.getActionCommand()), caller);
				} else {
					PlotTools.projectImage(pi, IQMConstants.DIRECTION_Y);
				}
			}

		}
		if ("rowprofile".equals(e.getActionCommand())) {

			if (Look.getInstance().getCurrentLookPanel().getCurrentROILayer()
					.isEmpty()) {
				DialogUtil.getInstance().showDefaultInfoMessage(
						I18N.getMessage("annotations.noInstance"));
				return;
			}

			// get the current ROI shape
			int index = Look.getInstance().getCurrentLookPanel()
					.getCurrentROILayer().getCurrentROIShape().getBounds().x;
			List<PlotModel> models = PlotTools.getIntensityProfile(Look
					.getInstance().getCurrentImage().getData(),
					IQMConstants.DIRECTION_HORIZONTAL, index);
			Plot.getInstance().setNewData(models, ChartType.XY_LINE_CHART);
			Application.getMainFrame().setSelectedTabIndex(1);
		}
		if ("colprofile".equals(e.getActionCommand())) {

			if (Look.getInstance().getCurrentLookPanel().getCurrentROILayer()
					.isEmpty()) {
				DialogUtil.getInstance().showDefaultInfoMessage(
						I18N.getMessage("annotations.noInstance"));
				return;
			}

			int index = Look.getInstance().getCurrentLookPanel()
					.getCurrentROILayer().getCurrentROIShape().getBounds().y;
			List<PlotModel> models = PlotTools.getIntensityProfile(Look
					.getInstance().getCurrentImage().getData(),
					IQMConstants.DIRECTION_VERTICAL, index);
			Plot.getInstance().setNewData(models, ChartType.XY_LINE_CHART);
			Application.getMainFrame().setSelectedTabIndex(1);
		}
		if ("plotcolumn".equals(e.getActionCommand())){
			
			// take the selected columns from the table view
			TableModel tableModel = Table.getInstance().getTablePanel().getTableModel();
			JTableHeader tableHeader = Table.getInstance().getTablePanel().getTable().getTableHeader();
			
			@SuppressWarnings("unchecked")
			Vector<Vector> doubleData = tableModel.getDataVector();  //
		
			//PlotSelectionFrame and PlotParser need Data in String format see Ticket #70
			//Convert Double values to String
			Vector<Vector<String>> stringData = new Vector<Vector<String>>();
			for (int i = 0; i < doubleData.size(); i++){
				Vector<String> tempVec = new Vector<String>();
				for (int j = 0; j < doubleData.get(i).size(); j++){	
					tempVec.add(String.valueOf(doubleData.get(i).get(j)));
				}
				stringData.add(tempVec);		
			}
			
			PlotSelectionFrame psf = new PlotSelectionFrame(stringData);
			psf.setLocationRelativeTo(GUITools.getMainFrame());
			psf.createAndSelect(tableHeader);
			psf.setVisible(true);
		}
	}
}
