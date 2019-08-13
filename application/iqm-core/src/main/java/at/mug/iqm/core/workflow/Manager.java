package at.mug.iqm.core.workflow;

/*
 * #%L
 * Project: IQM - Application Core
 * File: Manager.java
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


import java.awt.List;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IManagerPanel;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.api.plot.charts.ChartType;
import at.mug.iqm.api.workflow.IManager;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.CursorFactory;
import at.mug.iqm.core.util.ApplicationObserver;
import at.mug.iqm.gui.ManagerPanel;
import at.mug.iqm.gui.TankPanel;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class is the main workflow control class for IQM's image processing.
 * <p>
 * <b>Changes</b>
 * <ul>
 * 	<li>2009 bug JTable for virtual image sequence corrected
 * 	<li>2012 02 21 MAJOR PROJECT CHANGE: Iqm uses the new IqmDataBox class
 * 	<li>2012 03 26 PK: restructuring class
 * 	<li>2012 10 21 MMR: functions for TYPE_PLOT as a new type in IqmDataBox
 *         included
 * 	<li>2012 11 08 MMR: setCurrManagerImage(int[] i) for multiple plots
 * </ul>
 * 
 * @author Helmut Ahammer, Philipp Kainz, Michael Mayrhofer-R.
 * @since 2009

 */
@SuppressWarnings({"rawtypes","unchecked"})
public final class Manager implements IManager {

	// class specific logger
	private static Class<?> caller = Manager.class;
	private static final Logger logger = LogManager.getLogger(Manager.class);

	/**
	 * The GUI element to administer.
	 */
	private ManagerPanel managerPanel = null;

	// cached items for easy access
	private DefaultListModel managerModelLeft = null;
	private JList managerListLeft = null;
	private DefaultListModel managerModelRight = null;
	private JList managerListRight = null;

	/**
	 * This is the image for the preview function in the operator GUIs.
	 */
	private PlanarImage previewImage = null;

	/**
	 * The tank index in the left manager list. Default is -1.
	 */
	private int tankIndexInMainLeft = -1;
	/**
	 * The tank index in the right manager list. Default is -1.
	 */
	private int tankIndexInMainRight = -1;

	/**
	 * The currently selected index in one of the manager lists. Default is -1.
	 */
	private int currItemIndex = -1;

	/**
	 * A private constructor for this singleton.
	 */
	private Manager() {
		Application.setManager(this);
	}

	/**
	 * Gets the singleton instance of this {@link IManager}.
	 * 
	 * @return the singleton instance of the {@link Manager}
	 */
	public static IManager getInstance() {
		IManager mgr = Application.getManager();
		if (mgr == null) {
			mgr = new Manager();
		}
		return mgr;
	}

	/**
	 * This method initializes the {@link Manager}.
	 */
	@Override
	public void initialize() {
		managerModelLeft = managerPanel.getManagerModelLeft();
		managerListLeft = managerPanel.getManagerListLeft();
		managerModelRight = managerPanel.getManagerModelRight();
		managerListRight = managerPanel.getManagerListRight();
	}

	@Override
	public void setManagerPanel(IManagerPanel arg) {
		managerPanel = (ManagerPanel) arg;
	}

	@Override
	public IManagerPanel getManagerPanel() {
		return managerPanel;
	}

	/**
	 * This method returns the model of the left {@link List}.
	 * 
	 * @return mangerModelLeft
	 */
	@Override
	public DefaultListModel getManagerModelLeft() {
		return managerModelLeft;
	}

	/**
	 * This method returns the model of the right {@link List}.
	 * 
	 * @return mangerModelRight
	 */
	@Override
	public DefaultListModel getManagerModelRight() {
		return managerModelRight;
	}

	@Override
	public boolean isLeftListActive() {
		return managerPanel.getJRadioButtonMainLeft().isSelected();
	}

	@Override
	public boolean isRightListActive() {
		return managerPanel.getJRadioButtonMainRight().isSelected();
	}

	/**
	 * This method resets the left manager list.
	 */
	@Override
	public void resetLeftModel() {
		logger.debug("Left manager is resetting.");

		managerPanel.setCursor(CursorFactory.getWaitCursor());

		managerListLeft.removeListSelectionListener(managerPanel);
		managerModelLeft.removeAllElements();

		managerPanel.updateWorkPackage(null, null);

		managerListLeft.setModel(managerModelLeft);
		managerListLeft.addListSelectionListener(managerPanel);

		managerPanel.resetItemSlider();

		managerPanel.setIndexForDisplay(-1);

		setPreviewImage(null);
		setTankIndexInMainLeft(-1);
		managerPanel.setCursor(CursorFactory.getDefaultCursor());

		Look.getInstance().reset();
		Plot.getInstance().reset();
		Table.getInstance().reset();
		Text.getInstance().reset();

		// reset the menu state
		ApplicationObserver.setInitialMenuActivation();
	}

	/**
	 * This method resets the right manager list.
	 */
	@Override
	public void resetRightModel() {
		logger.debug("Right manager is resetting.");

		managerPanel.setCursor(CursorFactory.getWaitCursor());

		managerListRight.removeListSelectionListener(managerPanel);
		managerModelRight.removeAllElements();

		managerPanel.updateWorkPackage(null, null);

		managerListRight.setModel(managerModelRight);
		managerListRight.addListSelectionListener(managerPanel);

		managerPanel.resetItemSlider();

		managerPanel.setIndexForDisplay(-1);

		setPreviewImage(null);
		setTankIndexInMainRight(-1);
		managerPanel.setCursor(CursorFactory.getDefaultCursor());

		Look.getInstance().reset();
		Plot.getInstance().reset();
		Table.getInstance().reset();
		Text.getInstance().reset();

		// reset the menu state
		ApplicationObserver.setInitialMenuActivation();
	}

	/**
	 * This method sets the tank index number, which contains the current
	 * elements in the left manager. If the elements in the left manager are
	 * from tank index 1, then the value of <code>tankIndex</code> is 1.
	 * 
	 * @param tankIndex
	 *            - the tank index for the left manager
	 */
	@Override
	public void setTankIndexInMainLeft(int tankIndex) {
		tankIndexInMainLeft = tankIndex;
		String text = "";
		if (tankIndex != -1) {
			text = String.valueOf(tankIndex + 1);
		}
		managerPanel.getLblTankIndexLeft().setText(text);

		if (tankIndex != -1) {
			if (Tank.getInstance().isVirtual(tankIndex)) {
				managerPanel.showVirtualIconLeft();
			} else {
				managerPanel.hideVirtualIconLeft();
			}
		} else {
			managerPanel.hideVirtualIconLeft();
		}

	}

	/**
	 * This method gets the current Tank index of left manager.
	 * 
	 * @return the integer index
	 */
	@Override
	public int getTankIndexInMainLeft() {
		return tankIndexInMainLeft;
	}

	/**
	 * This method sets the tank index number, which contains the current
	 * elements in the right manager. If the elements in the right manager are
	 * from tank index 1, then the value of <code>tankIndex</code> is 1.
	 * 
	 * @param tankIndex
	 *            - the tank index for the right manager
	 */
	@Override
	public void setTankIndexInMainRight(int tankIndex) {
		tankIndexInMainRight = tankIndex;
		String text = "";
		if (tankIndex != -1) {
			text = String.valueOf(tankIndex + 1);
		}
		managerPanel.getLblTankIndexRight().setText(text);

		if (tankIndex != -1) {
			if (Tank.getInstance().isVirtual(tankIndex)) {
				managerPanel.showVirtualIconRight();
			} else {
				managerPanel.hideVirtualIconRight();
			}
		} else {
			managerPanel.hideVirtualIconRight();
		}
	}

	/**
	 * This method gets the current Tank index of the ScrollPane Main2
	 * 
	 * @return the integer index
	 */
	@Override
	public int getTankIndexInMainRight() {
		return tankIndexInMainRight;
	}

	/**
	 * This method constructs the {@link DefaultListModel} for a manager's
	 * {@link JList}.
	 * 
	 * @param index
	 *            the index of the item in the tank
	 * @param onlyNumbers
	 *            whether or not only numbers are shown as list items
	 */
	@Override
	public DefaultListModel getIconList(int index, boolean onlyNumbers) {

		// get the first data box in order to determine the type
		int nStackElements = Tank.getInstance().getTankDataAt(index).size();

		DefaultListModel iconList = new DefaultListModel();

		for (int i = 0; i < nStackElements; i++) {
			ImageIcon ic = null;
			IqmDataBox currentStackElement = Tank.getInstance()
					.getTankDataAt(index).get(i);

			if (!onlyNumbers) {
				ic = currentStackElement.getManagerThumbnail();
			}

			iconList.add(i, ic);
		}
		return iconList;
	}

	/**
	 * This methods sets the new Manager's icons and data. Furthermore, it
	 * displays the first item of the item list in the <code>tankIndex</code>.
	 * 
	 * @param tankIndex
	 *            current index of the selected element in the {@link TankPanel}
	 */
	@Override
	public void setNewIconsForTankIndex(int tankIndex) {
		setCurrItemIndex(tankIndex);
//		System.out.println("SETTING ICONS FOR TANK INDEX: " + tankIndex);

		if (tankIndex == -1) {
			BoardPanel.appendTextln("No tank items available", caller);
			return;
		}

		if (this.managerPanel.isRadioButtonMainLeftSelected()) {
			boolean onlyNumbers = true;
			if (this.managerPanel.isCheckBoxMainLeftSelected()) {
				onlyNumbers = false;
			}
			// remove the list cell renderer
			ListCellRenderer cr = managerListLeft.getCellRenderer();
			managerListLeft.setCellRenderer(null);

			DefaultListModel iconList = this
					.getIconList(tankIndex, onlyNumbers);
			this.setNewIconsMainLeft(iconList, tankIndex);
			this.setTankIndexInMainLeft(tankIndex);

			managerListLeft.setCellRenderer(cr);

		}
		if (this.managerPanel.isRadioButtonMainRightSelected()) {
			boolean onlyNumbers = true;
			if (this.managerPanel.isCheckBoxMainRightSelected()) {
				onlyNumbers = false;
			}

			ListCellRenderer cr = managerListRight.getCellRenderer();
			managerListRight.setCellRenderer(null);

			DefaultListModel iconList = this
					.getIconList(tankIndex, onlyNumbers);
			this.setNewIconsMainRight(iconList, tankIndex);
			this.setTankIndexInMainRight(tankIndex);

			managerListRight.setCellRenderer(cr);
		}

		// display the first item in the manager
		displayItem(0);

		// set the first item selected in the manager panel
		// and update any open operator with the new sources
		managerPanel.setCurrMgrIdxs(managerPanel.updateWorkPackage(
				new int[] { 0 }, new int[] { 0 }));

		System.out.println("Current Manager indices: "
				+ CommonTools.intArrayToString(managerPanel.getCurrMgrIdxs()));
	}

	/**
	 * This methods sets the new icons and data in the left list.
	 * 
	 * @param listOfIcons
	 *            - DefaultListModel of ImageIcons
	 * @param tankIndex
	 *            - int tank index number
	 */
	@Override
	public void setNewIconsMainLeft(DefaultListModel listOfIcons, int tankIndex) {
		resetLeftModel();

		managerListLeft.removeListSelectionListener(managerPanel);

		int length = listOfIcons.getSize();
		for (int i = 0; i < length; i++) {
			managerModelLeft.addElement(listOfIcons.getElementAt(i));
		}
		managerListLeft.setModel(managerModelLeft);
		managerListLeft.ensureIndexIsVisible(0);
		managerListLeft.addListSelectionListener(managerPanel);
		managerListLeft.setSelectedIndex(0);
	}

	/**
	 * This methods sets the new icons and data in the right list.
	 * 
	 * @param listOfIcons
	 *            - DefaultListModel of ImageIcons
	 * @param tankIndex
	 *            - int tank index number
	 */
	@Override
	public void setNewIconsMainRight(DefaultListModel listOfIcons, int tankIndex) {
		resetRightModel();

		managerListRight.removeListSelectionListener(managerPanel);

		int length = listOfIcons.getSize();
		for (int i = 0; i < length; i++) {
			managerModelRight.addElement(listOfIcons.getElementAt(i));
		}
		managerListRight.setModel(managerModelRight);
		managerListRight.ensureIndexIsVisible(0);
		managerListRight.addListSelectionListener(managerPanel);
		managerListRight.setSelectedIndex(0);

	}

	/**
	 * This method sets the current manager item and displays the item in the
	 * corresponding panel (e.g. an image is set to the <code>ILookPanel</code>
	 * ).
	 * <p>
	 * It also sets the scroll bar of the items to the correct values.
	 * 
	 * @param mgrIndex
	 *            current Manager item index
	 */
	@Override
	public void displayItem(int mgrIndex) {
		int nItems = 0;

		// check which tank index is selected for display
		if (isLeftListActive()) {
			int tankIndex = this.getTankIndexInMainLeft();
			Tank.getInstance().setCurrIndex(tankIndex);
			nItems = managerModelLeft.getSize();
			logger.debug("Left Manager List is selected.");
		} else if (isRightListActive()) {
			int tankIndex = this.getTankIndexInMainRight();
			Tank.getInstance().setCurrIndex(tankIndex);
			nItems = managerModelRight.getSize();
			logger.debug("Right Manager List is selected.");
		}

		// get the data box at the mgrIndex position of the tank index
		// and display the selected item
		IqmDataBox boxReference = Tank.getInstance()
				.getCurrentTankIqmDataBoxAt(mgrIndex);

		IqmDataBox box = null;
		// try to load the last item in the stack
		if (boxReference == null) {
			boxReference = Tank.getInstance().getCurrentTankIqmDataBoxAt(0);
		}

		if (boxReference == null) {
			BoardPanel
					.appendTextln(
							"Current tank item is not defined, cannot display anything!",
							caller);
		} else {

			logger.debug("Manager.displayItem(int)" + " -- box type = "
					+ boxReference.getDataTypeAsString());

			// load the data box from the disk if it is virtual
			if (boxReference instanceof VirtualDataBox) {
				box = VirtualDataManager.getInstance().load(
						(VirtualDataBox) boxReference);
			} else {
				box = boxReference;
			}
			

			// set the currently displayed item number
			setCurrItemIndex(mgrIndex);

			// set the slider values
			managerPanel.setSliderValues(mgrIndex, nItems);

			switch (box.getDataType()) {
			case IMAGE:

				Look.getInstance().setImage(box.getImage());

				// activate the corresponding tab
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						GUITools.getMainFrame().getItemContent()
								.setSelectedIndex(0);
						// enable the image menus
						ApplicationObserver.enableImageMenusExclusively();
					}
				});
				break;

			case PLOT:
				// forward to display items for single plot displaying
				displayPlotItems(new int[] { mgrIndex });

				break;

			case TABLE:
				displayTableItems(new int[] { mgrIndex });
				
				break;

			case CUSTOM:
				
				// TODO continue custom implementation
				String s = box.getCustomContent().getContent()[0].toString();

				Text.getInstance().setNewData(s);

				// activate the corresponding tab
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						GUITools.getMainFrame().getItemContent()
								.setSelectedIndex(3);

						// disable all menus but generators
						ApplicationObserver.setInitialMenuActivation();
					}
				});
				break;

			default:
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						// disable all menus but generators
						ApplicationObserver.setInitialMenuActivation();
					}
				});

				return;
			}
		}
	}

	/**
	 * This method displays the selected items in a manager list within a single
	 * plot.
	 * 
	 * @param indices
	 *            array of selected Manager indices
	 */
	@Override
	public void displayPlotItems(int[] indices) {
		if (indices == null || indices.length == 0)
			return;

		int nItems = 0;

		if (isLeftListActive()) {
			int tankIndex = this.getTankIndexInMainLeft();
			Tank.getInstance().setCurrIndex(tankIndex);
			nItems = managerModelLeft.getSize();
			logger.debug("Left Manager List is selected.");
		} else if (isRightListActive()) {
			int tankIndex = this.getTankIndexInMainRight();
			Tank.getInstance().setCurrIndex(tankIndex);
			nItems = managerModelRight.getSize();
			logger.debug("Right Manager List is selected.");
		}

		IqmDataBox iqmDataBox = Tank.getInstance().getCurrentTankIqmDataBoxAt(
				indices[0]);

		Vector<PlotModel> plotModelsForDisplay = new Vector<PlotModel>(
				indices.length);
		if (iqmDataBox.getDataType() == DataType.PLOT) {
			for (int ix = 0; ix < indices.length; ix++) {
				IqmDataBox box = Tank.getInstance().getCurrentTankIqmDataBoxAt(
						indices[ix]);
				if (box instanceof IVirtualizable) {
					box = VirtualDataManager.getInstance().load(
							(IVirtualizable) box);
				}
				plotModelsForDisplay.add(ix, box.getPlotModel());
			}

			logger.debug("Manager.displayPlotItems(int[]) -- box type = "
					+ iqmDataBox.getDataTypeAsString());

			Plot.getInstance().setNewData(plotModelsForDisplay,
					ChartType.DEFAULT);

			// set the first index of the array as current item
			setCurrItemIndex(indices[0]);

			managerPanel.setSliderValues(indices[0], nItems);

			// activate the corresponding tab
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					GUITools.getMainFrame().getItemContent()
							.setSelectedIndex(1);

					// enable the plot menus
					ApplicationObserver.enablePlotMenusExclusively();
				}
			});
		}
	}
	
	/**
	 * This method displays the selected items in a manager list within a single
	 * table.
	 * 
	 * @param indices
	 *            array of selected Manager indices
	 */
	@Override
	public void displayTableItems(int[] indices) {
		if (indices == null || indices.length == 0)
			return;

		int nItems = 0;

		if (isLeftListActive()) {
			int tankIndex = this.getTankIndexInMainLeft();
			Tank.getInstance().setCurrIndex(tankIndex);
			nItems = managerModelLeft.getSize();
			logger.debug("Left Manager List is selected.");
		} else if (isRightListActive()) {
			int tankIndex = this.getTankIndexInMainRight();
			Tank.getInstance().setCurrIndex(tankIndex);
			nItems = managerModelRight.getSize();
			logger.debug("Right Manager List is selected.");
		}

		IqmDataBox iqmDataBox = Tank.getInstance().getCurrentTankIqmDataBoxAt(
				indices[0]);

		Vector<TableModel> tableModelsForDisplay = new Vector<TableModel>(
				indices.length);
		if (iqmDataBox.getDataType() == DataType.TABLE) {
			for (int ix = 0; ix < indices.length; ix++) {
				IqmDataBox box = Tank.getInstance().getCurrentTankIqmDataBoxAt(
						indices[ix]);
				if (box instanceof IVirtualizable) {
					box = VirtualDataManager.getInstance().load(
							(IVirtualizable) box);
				}
				tableModelsForDisplay.add(ix, box.getTableModel());
			}

			logger.debug("Manager.displayTableItems(int[]) -- box type = "
					+ iqmDataBox.getDataTypeAsString());

			Table.getInstance().setNewData(tableModelsForDisplay);
//			Table.getInstance().setNewData(iqmDataBox.getTableModel());

			// set the first index of the array as current item
			setCurrItemIndex(indices[0]);

			managerPanel.setSliderValues(indices[0], nItems);

			// activate the corresponding tab
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					GUITools.getMainFrame().getItemContent()
							.setSelectedIndex(2);

					// enable the table menus
					ApplicationObserver.enableTableMenusExclusively();
				}
			});
		}
	}
	
	/**
	 * This method sets the preview image, which has been created using the
	 * "preview" button in an operator GUI.
	 * 
	 * @param pi
	 */
	@Override
	public void setPreviewImage(RenderedImage pi) {
		if (pi == null) {
			previewImage = null;
		} else {
			previewImage = PlanarImage.wrapRenderedImage(pi);
		}
	}

	/**
	 * This method gets the preview image, which has been created using the
	 * "preview" button in an operator GUI.
	 * 
	 * @return PlanarImage
	 */
	@Override
	public PlanarImage getPreviewImage() {
		return previewImage;

	}

	/**
	 * This method starts the toggle preview if it is selected
	 */
	@Override
	public void startTogglePreviewIfSelected() {
		managerPanel.startTogglePreviewIfSelected();
	}

	/**
	 * This method resets the toggle preview
	 */
	@Override
	public void resetTogglePreviewIfRunning() {
		managerPanel.resetTogglePreviewIfRunning();
	}

	/**
	 * This method sets the current manager item number.
	 * 
	 * @param arg
	 *            current manager item number
	 */
	@Override
	public void setCurrItemIndex(int arg) {
		currItemIndex = arg;
	}

	/**
	 * This method gets the current manager item number.
	 * 
	 * @return current manager item number
	 */
	@Override
	public int getCurrItemIndex() {
		return currItemIndex;
	}

	/**
	 * This method gets the selected indices from the manager panel.
	 * 
	 * @return an integer array of selected indices 
	 */
	public int[] getSelectedIndices() {
		return this.managerPanel.getCurrMgrIdxs();
	}

	/**
	 * Gets the type of the data box in the currently selected manager.
	 * 
	 * @return the int representation of {@link IqmDataBox} types
	 */
	@Override
	public DataType getDataBoxTypeInSelectedManager() {
		DataType dataBoxType = DataType.UNSUPPORTED;

		int mgrIndex = getCurrItemIndex();
		int tankIndex = -1;
		if (isLeftListActive()) {
			tankIndex = getTankIndexInMainLeft();
		} else if (isRightListActive()) {
			tankIndex = getTankIndexInMainRight();
		}

		if (tankIndex != -1) {
			dataBoxType = Tank.getInstance()
					.getTankIqmDataBoxAt(tankIndex, mgrIndex).getDataType();
		}

		return dataBoxType;
	}
	
	@Override
	public void setLeftListActive(boolean b) {
		this.managerPanel.getJRadioButtonMainLeft().setSelected(true);
	}
	
	@Override
	public void setRightListActive(boolean b) {
		this.managerPanel.getJRadioButtonMainRight().setSelected(true);
	}

	@Override
	public int getActiveListIndex() {
		return (isLeftListActive()?0:1);
	}

	@Override
	public java.util.List<IqmDataBox> getSelectedDataBoxes() {
		int[] selIdxs = getSelectedIndices();
		java.util.List<IqmDataBox> boxes = new ArrayList<IqmDataBox>(selIdxs.length);
		for (int idx : selIdxs){
			boxes.add(Tank.getInstance().getCurrentTankIqmDataBoxAt(idx));
		}
		return boxes;
	}
}// END

