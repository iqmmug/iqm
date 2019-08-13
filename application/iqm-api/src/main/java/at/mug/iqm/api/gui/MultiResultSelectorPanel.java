package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: MultiResultSelectorPanel.java
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.api.plot.charts.ChartType;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.MultiResultListCellRenderer;
import at.mug.iqm.commons.util.table.TableTools;

/**
 * This panel is responsible for displaying the list of results, if an operator
 * puts out multiple results. It provides selection mechanisms like the
 * {@link IManagerPanel} for image, plot, table and custom results.
 * 
 * @author Philipp Kainz
 * 
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class MultiResultSelectorPanel extends JPanel implements
		ListSelectionListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 7548348452978754466L;

	private static final Logger logger = LogManager.getLogger(MultiResultSelectorPanel.class);

	/**
	 * This is the {@link Result} element which has to be visualized.
	 */
	private Result result = null;

	/*
	 * The list models containing the result data for showing in the JList.
	 */
	private List<IqmDataBox> imageResults = new ArrayList<IqmDataBox>();
	private List<IqmDataBox> selectedImageResults = new ArrayList<IqmDataBox>();
	private DefaultListModel imageListModel = new DefaultListModel();

	private List<IqmDataBox> plotResults = new ArrayList<IqmDataBox>();
	private List<IqmDataBox> selectedPlotResults = new ArrayList<IqmDataBox>();
	private DefaultListModel plotListModel = new DefaultListModel();

	private List<IqmDataBox> tableResults = new ArrayList<IqmDataBox>();
	private List<IqmDataBox> selectedTableResults = new ArrayList<IqmDataBox>();
	private DefaultListModel tableListModel = new DefaultListModel();

	private List<IqmDataBox> customResults = new ArrayList<IqmDataBox>();
	private List<IqmDataBox> selectedCustomResults = new ArrayList<IqmDataBox>();
	private DefaultListModel customListModel = new DefaultListModel();

	/*
	 * These variables hold the currently displayed indices of the lists, so
	 * that the corresponding cells can be highlighted.
	 */
	private int[] selectedImageIdxs;
	private int previousImageIndex = -1;
	private int[] selectedPlotIdxs;
	private int previousPlotIndex = -1;
	private int[] selectedTableIdxs;
	private int previousTableIndex = -1;
	private int[] selectedCustomIdxs;
	private int previousCustomIndex = -1;

	/*
	 * GUI control elements.
	 */
	private JTabbedPane tabbedPane;
	private JPanel pnlImageResults;
	private JList resultListImage;
	private JLabel lblImageResults;
	private JPanel pnlPlotResults;
	private JPanel pnlTableResults;
	private JPanel pnlCustomResults;
	private JList resultListPlot;
	private JLabel lblPlotResults;
	private JList resultListTable;
	private JLabel lblTableResults;
	private JList resultListCustom;
	private JLabel lblCustomResults;

	/**
	 * Create the panel.
	 */
	public MultiResultSelectorPanel() {
		setPreferredSize(new Dimension(200, 300));
		setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		add(tabbedPane);

		pnlImageResults = new JPanel();
		tabbedPane.addTab(null,
				new ImageIcon(Resources.getImageURL("icon.image.generic16")),
				pnlImageResults, I18N.getGUILabelText("multiresult.image.ttp"));
		pnlImageResults.setLayout(new BorderLayout(0, 0));

		lblImageResults = new JLabel(I18N.getGUILabelText(
				"multiresult.image.header.text", 0));
		lblImageResults.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1,
				0, (Color) new Color(0, 0, 0)), new EmptyBorder(0, 0, 2, 0)));
		lblImageResults.setHorizontalAlignment(SwingConstants.CENTER);
		pnlImageResults.add(lblImageResults, BorderLayout.NORTH);
		
		// DEBUG ONLY
		// this.imageListModel = new DefaultListModel();
		// this.imageListModel.addElement("One");
		// this.imageListModel.addElement("Two");

		resultListImage = new JList(imageListModel);
		resultListImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = resultListImage.locationToIndex(e.getPoint());
				if (index == previousImageIndex) {
					ListSelectionEvent evt = new ListSelectionEvent(
							resultListImage, index, index, false);
					valueChanged(evt);
				}
				previousImageIndex = index;
			}
		});
		resultListImage.setName("imageList");
		lblImageResults.setLabelFor(resultListImage);
		resultListImage.setCellRenderer(new MultiResultListCellRenderer());
		resultListImage.addListSelectionListener(this);
		JScrollPane scrollPaneImage = new JScrollPane(resultListImage);
		scrollPaneImage.setViewportBorder(null);
		pnlImageResults.add(scrollPaneImage);

		pnlPlotResults = new JPanel();
		tabbedPane.addTab(null,
				new ImageIcon(Resources.getImageURL("icon.plot.generic16")),
				pnlPlotResults, I18N.getGUILabelText("multiresult.plot.ttp"));
		pnlPlotResults.setLayout(new BorderLayout(0, 0));

		lblPlotResults = new JLabel(I18N.getGUILabelText(
				"multiresult.plot.header.text", 0));
		lblPlotResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlotResults.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0,
				(Color) new Color(0, 0, 0)), new EmptyBorder(0, 0, 2, 0)));
		pnlPlotResults.add(lblPlotResults, BorderLayout.NORTH);

		resultListPlot = new JList(plotListModel);
		resultListPlot.setName("plotList");
		resultListPlot.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = resultListPlot.locationToIndex(e.getPoint());
				if (index == previousPlotIndex) {
					ListSelectionEvent evt = new ListSelectionEvent(
							resultListPlot, index, index, false);
					valueChanged(evt);
				}
				previousPlotIndex = index;
			}
		});
		resultListPlot.addListSelectionListener(this);
		resultListPlot.setCellRenderer(new MultiResultListCellRenderer());
		JScrollPane scrollPanePlot = new JScrollPane(resultListPlot);
		scrollPanePlot.setViewportBorder(null);
		pnlPlotResults.add(scrollPanePlot, BorderLayout.CENTER);

		pnlTableResults = new JPanel();
		tabbedPane.addTab(null,
				new ImageIcon(Resources.getImageURL("icon.table.generic16")),
				pnlTableResults, I18N.getGUILabelText("multiresult.table.ttp"));
		pnlTableResults.setLayout(new BorderLayout(0, 0));

		lblTableResults = new JLabel(I18N.getGUILabelText(
				"multiresult.table.header.text", 0));
		lblTableResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblTableResults.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1,
				0, (Color) new Color(0, 0, 0)), new EmptyBorder(0, 0, 2, 0)));
		pnlTableResults.add(lblTableResults, BorderLayout.NORTH);

		resultListTable = new JList(tableListModel);
		resultListTable.setName("tableList");
		resultListTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = resultListTable.locationToIndex(e.getPoint());
				if (index == previousTableIndex) {
					ListSelectionEvent evt = new ListSelectionEvent(
							resultListTable, index, index, false);
					valueChanged(evt);
				}
				previousTableIndex = index;
			}
		});
		resultListTable.addListSelectionListener(this);
		resultListTable.setCellRenderer(new MultiResultListCellRenderer());
		JScrollPane scrollPaneTable = new JScrollPane(resultListTable);
		scrollPaneTable.setViewportBorder(null);
		pnlTableResults.add(scrollPaneTable, BorderLayout.CENTER);

		pnlCustomResults = new JPanel();
		tabbedPane.addTab(null,
				new ImageIcon(Resources.getImageURL("icon.custom.generic16")),
				pnlCustomResults,
				I18N.getGUILabelText("multiresult.custom.ttp"));
		pnlCustomResults.setLayout(new BorderLayout(0, 0));

		lblCustomResults = new JLabel(I18N.getGUILabelText(
				"multiresult.custom.header.text", 0));
		lblCustomResults.setHorizontalAlignment(SwingConstants.CENTER);
		lblCustomResults.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1,
				0, (Color) new Color(0, 0, 0)), new EmptyBorder(0, 0, 2, 0)));
		pnlCustomResults.add(lblCustomResults, BorderLayout.NORTH);

		resultListCustom = new JList(customListModel);
		resultListCustom.setName("customList");
		resultListCustom.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = resultListCustom.locationToIndex(e.getPoint());
				if (index == previousCustomIndex) {
					ListSelectionEvent evt = new ListSelectionEvent(
							resultListCustom, index, index, false);
					valueChanged(evt);
				}
				previousCustomIndex = index;
			}
		});
		resultListCustom.addListSelectionListener(this);
		resultListCustom.setCellRenderer(new MultiResultListCellRenderer());
		JScrollPane scrollPaneCustom = new JScrollPane(resultListCustom);
		scrollPaneCustom.setViewportBorder(null);
		pnlCustomResults.add(scrollPaneCustom, BorderLayout.CENTER);
	}

	public MultiResultSelectorPanel(Result result) {
		this();
		if (result != null) {
			this.result = result;

			boolean[] tabIdxToRemove = new boolean[] { false, false, false,
					false };
			if (!result.hasImages()) {
				tabIdxToRemove[0] = true;
			} else {
				initializeImageList();
			}
			if (!result.hasPlots()) {
				tabIdxToRemove[1] = true;
			} else {
				initializePlotList();
			}
			if (!result.hasTables()) {
				tabIdxToRemove[2] = true;
			} else {
				initializeTableList();
			}
			if (!result.hasCustomResults()) {
				tabIdxToRemove[3] = true;
			} else {
				initializeCustomList();
			}

			for (int i = tabIdxToRemove.length - 1; i >= 0; i--) {
				if (tabIdxToRemove[i] == true) {
					tabbedPane.removeTabAt(i);
				}
			}
		}
	}

	public void initializeImageList() {
		// fill the list
		imageListModel = new DefaultListModel();
		imageResults = result.listImageResults();

		for (int i = 0; i < imageResults.size(); i++) {
			IqmDataBox box = imageResults.get(i);
			imageListModel.add(i, box);
		}

		resultListImage.setModel(imageListModel);
		lblImageResults.setText(I18N.getGUILabelText(
				"multiresult.image.header.text", imageResults.size()));
	}

	public void initializePlotList() {
		// fill the list
		plotListModel = new DefaultListModel();
		plotResults = result.listPlotResults();

		for (int i = 0; i < plotResults.size(); i++) {
			IqmDataBox box = plotResults.get(i);
			plotListModel.add(i, box);
		}

		resultListPlot.setModel(plotListModel);
		lblPlotResults.setText(I18N.getGUILabelText(
				"multiresult.plot.header.text", plotResults.size()));
	}

	public void initializeTableList() {
		// fill the list
		tableListModel = new DefaultListModel();
		tableResults = result.listTableResults();

		for (int i = 0; i < tableResults.size(); i++) {
			IqmDataBox box = tableResults.get(i);
			tableListModel.add(i, box);
		}

		resultListTable.setModel(tableListModel);
		lblTableResults.setText(I18N.getGUILabelText(
				"multiresult.table.header.text", tableResults.size()));
	}

	public void initializeCustomList() {
		// fill the list
		customListModel = new DefaultListModel();
		customResults = result.listCustomResults();

		for (int i = 0; i < customResults.size(); i++) {
			IqmDataBox box = customResults.get(i);
			customListModel.add(i, box);
		}

		resultListCustom.setModel(customListModel);
		lblCustomResults.setText(I18N.getGUILabelText(
				"multiresult.custom.header.text", customResults.size()));
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			// System.out.println(e);
			// determine the source
			JList source = (JList) e.getSource();
			// System.out.println(source.getName());

			int[] selectedIndices = source.getSelectedIndices();

			if (source == resultListImage) {
				selectedImageResults.clear();
				selectedImageIdxs = selectedIndices.clone();

				// display the first of the selected images on multi-selection
				RenderedImage img = null;
				if (selectedImageIdxs.length != 0) {
					img = imageResults.get(selectedImageIdxs[0]).getImage();

					for (int i : selectedImageIdxs) {
						IqmDataBox content = imageResults.get(i);
						selectedImageResults.add(content);
					}
					CommonTools.setTabForDataType(DataType.IMAGE);
				}

				Application.getLook().setImage(img);
				Application.getManager().setPreviewImage(img);

			} else if (source == resultListPlot) {
				selectedPlotResults.clear();
				selectedPlotIdxs = selectedIndices.clone();

				if (selectedPlotIdxs.length != 0) {

					List<PlotModel> models = new Vector<PlotModel>(
							selectedPlotIdxs.length);

					for (int i : selectedPlotIdxs) {
						IqmDataBox content = plotResults.get(i);
						selectedPlotResults.add(content);
						models.add(content.getPlotModel());
					}

					// sets the plot models as "preview"
					Application.getPlot().setNewData(models, ChartType.DEFAULT);

					CommonTools.setTabForDataType(DataType.PLOT);
				} else {
					Application.getPlot().reset();
				}

			} else if (source == resultListTable) {
				selectedTableResults.clear();
				selectedTableIdxs = selectedIndices.clone();

				if (selectedTableIdxs.length != 0) {

					TableModel tm = TableTools.mergeBoxes(tableResults);

					for (int i : selectedTableIdxs) {
						IqmDataBox content = tableResults.get(i);
						selectedTableResults.add(content);
					}

					Application.getTable().setNewData(tm);

					CommonTools.setTabForDataType(DataType.TABLE);
				} else {
					Application.getTable().reset();
				}
			} else if (source == resultListCustom) {
				selectedCustomResults.clear();
				selectedCustomIdxs = selectedIndices.clone();

				if (selectedCustomIdxs.length != 0) {

					for (int i : selectedCustomIdxs) {
						IqmDataBox content = customResults.get(i);
						selectedCustomResults.add(content);
					}

					String model = (String) customResults.get(0)
							.getCustomContent().getContent()[0];
					Application.getText().setNewData(model);

					CommonTools.setTabForDataType(DataType.CUSTOM);
				} else {
					Application.getText().reset();
				}
			}

			// set the following items
			System.out.println("Images: "
					+ CommonTools.intArrayToString(selectedImageIdxs));
			System.out.println("Plots:  "
					+ CommonTools.intArrayToString(selectedPlotIdxs));
			System.out.println("Tables: "
					+ CommonTools.intArrayToString(selectedTableIdxs));
			System.out.println("Custom: "
					+ CommonTools.intArrayToString(selectedCustomIdxs));

		}

	}

	/**
	 * Takes the first available item from the result and displays the item.
	 */
	public void displayFirstAvailableItem() {
		logger.debug("Displaying results from [" + result.toString() + "].");

		selectAllItems();

		if (result.hasImages()) {
			logger.debug("The image list contains " + imageResults.size()
					+ " element" + (imageResults.size() > 1 ? "s" : "") + ".");

			int firstIndex = resultListImage.getSelectedIndices()[0];

			PlanarImage pi = imageResults.get(firstIndex).getImage();
			Application.getLook().setImage(pi);
			Application.getManager().setPreviewImage(pi);

			CommonTools.setTabForDataType(DataType.IMAGE);
			return;
		}

		// display the merged plot results
		if (result.hasPlots()) {
			Iterator<IqmDataBox> plotIterator = plotResults.iterator();

			logger.debug("The plot list contains " + plotResults.size()
					+ " element" + (plotResults.size() > 1 ? "s" : "") + ".");

			List<PlotModel> models = new Vector<PlotModel>(plotResults.size());

			while (plotIterator.hasNext()) {
				IqmDataBox content = plotIterator.next();
				models.add(content.getPlotModel());
			}

			// sets the plot models as "preview"
			Application.getPlot().setNewData(models, ChartType.DEFAULT);

			CommonTools.setTabForDataType(DataType.PLOT);
			return;
		}

		// display the merged table results
		if (result.hasTables()) {

			logger.debug("The table list contains " + tableResults.size()
					+ " element" + (tableResults.size() > 1 ? "s" : "") + ".");

			TableModel tm = TableTools.mergeBoxes(tableResults);

			Application.getTable().setNewData(tm);

			CommonTools.setTabForDataType(DataType.TABLE);
			return;
		}

		// display the first custom result
		if (result.hasCustomResults()) {

			logger.debug("The custom list contains " + customResults.size()
					+ " element" + (customResults.size() > 1 ? "s" : "") + ".");

			int firstIndex = resultListCustom.getSelectedIndices()[0];

			// TODO continue developing displaying the custom results
			IqmDataBox content = customResults.get(firstIndex);
			String model = (String) content.getCustomContent().getContent()[0];
			Application.getText().setNewData(model);

			CommonTools.setTabForDataType(DataType.CUSTOM);
			return;
		}
	}

	/**
	 * Selects all items in any existing result list.
	 */
	public void selectAllItems() {
		// select all image models
		int start = 0;
		int end = imageListModel.getSize() - 1;
		if (end >= 0) {
			resultListImage.setSelectionInterval(start, end);
		}
		// select all plot models
		end = plotListModel.getSize() - 1;
		if (end >= 0) {
			resultListPlot.setSelectionInterval(start, end);
		}

		// select all table models
		end = tableListModel.getSize() - 1;
		if (end >= 0) {
			resultListTable.setSelectionInterval(start, end);
		}

		// select all custom models
		end = customListModel.getSize() - 1;
		if (end >= 0) {
			resultListCustom.setSelectionInterval(start, end);
		}

	}

	public List<IqmDataBox> getImageResults() {
		return imageResults;
	}

	public List<IqmDataBox> getPlotResults() {
		return plotResults;
	}

	public List<IqmDataBox> getTableResults() {
		return tableResults;
	}

	public List<IqmDataBox> getCustomResults() {
		return customResults;
	}

	public List<IqmDataBox> getSelectedImageResults() {
		return selectedImageResults;
	}

	public List<IqmDataBox> getSelectedPlotResults() {
		return selectedPlotResults;
	}

	public List<IqmDataBox> getSelectedTableResults() {
		return selectedTableResults;
	}

	public List<IqmDataBox> getSelectedCustomResults() {
		return selectedCustomResults;
	}
}
