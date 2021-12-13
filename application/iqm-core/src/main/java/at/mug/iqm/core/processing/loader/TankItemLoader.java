package at.mug.iqm.core.processing.loader;

/*
 * #%L
 * Project: IQM - Application Core
 * File: TankItemLoader.java
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.VirtualDataBox;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.api.processing.AbstractProcessingTask;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.workflow.Manager;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.LookPanel;
import at.mug.iqm.gui.PlotPanel;
import at.mug.iqm.gui.TankPanel;
import at.mug.iqm.gui.util.GUITools;

/**
 * This loader is responsible for <b>asynchronously</b> assembling items a given
 * {@link List} of {@link IqmDataBox}es to the {@link DefaultListModel} in the
 * {@link TankPanel} . This class is called by the {@link Tank}'s methods, e.g.
 * when the user double clicks on the {@link JList} items in order to show an
 * item in the {@link LookPanel} or {@link PlotPanel}.
 * <p>
 * <b>Changes</b>
 * <ul>
 * <li>2012 10 20 MMR: new DataBox-Type "Plot"
 * </ul>
 * 
 * @author Philipp Kainz, Michael Mayrhofer-R.
 * @update 2016-11-21 selectAll = true for plot results
 * 
 */
public class TankItemLoader extends AbstractProcessingTask {

	/**
	 * Logging variable.
	 */
	private static final Logger logger = LogManager.getLogger(TankItemLoader.class);

	/**
	 * The list to load.
	 */
	protected List<IqmDataBox> list;
	/**
	 * A flag whether to replace an existing stack.
	 */
	protected boolean replaceItem;
	/**
	 * The index to be replaced.
	 */
	protected int replaceIndex;

	/**
	 * The type of the {@link IqmDataBox} content. Default:
	 * {@link DataType#UNSUPPORTED}
	 */
	protected DataType dataType = DataType.UNSUPPORTED;

	/**
	 * The constructor.
	 * 
	 * @param list
	 */
	public TankItemLoader(List<IqmDataBox> list, boolean replaceItem, int replaceIndex) {
		
		this.setList(list);
		this.replaceItem = replaceItem;
		this.replaceIndex = replaceIndex;

		switch (list.get(0).getDataType()) {
		case IMAGE:
			this.setDataType(DataType.IMAGE);
			break;
		case PLOT:
			this.setDataType(DataType.PLOT);
			break;
		case TABLE:
			this.setDataType(DataType.TABLE);
			break;
		case CUSTOM:
			this.setDataType(DataType.CUSTOM);
			break;
		default:
			DialogUtil.getInstance().showDefaultErrorMessage(
					I18N.getMessage("application.tank.error.content"));
			return;
		}
			
	}

	@Override
	protected List<IqmDataBox> doInBackground() throws Exception {

		this.startTime = System.currentTimeMillis();

		boolean isVirtual = Application.isVirtual();
		File virtDir = null;
		if (isVirtual) {
			// ##############################################################
			// CREATE NEW VIRTUAL DIRECTORY FOR THIS STACK
			virtDir = VirtualDataManager.getNewVirtualDirectory();
			boolean success = virtDir.mkdir();
			if (success) {
				logger.info("Created directory: " + virtDir.toString());
			} else {
				logger.error("Unable to create directory:  "
						+ virtDir.toString());
				DialogUtil.getInstance().showDefaultErrorMessage(
						I18N.getMessage(
								"application.tank.error.virtDirCreate.failed",
								virtDir.toString()));
				return null;
			}
		}
		List<IqmDataBox> result = this.addNewItems(this.getList(), isVirtual, virtDir);

		this.duration = System.currentTimeMillis() - this.startTime;
		logger.info("Processed " + result.size() + " item" + (result.size() > 1 ? "s" : "") + " in " + this.duration/ 1000.0F + " seconds.");
		//logger.debug("Processed " + result.size() + " item" + (result.size() > 1 ? "s" : "") + " in " + this.duration/ 1000.0F + " seconds.");

		
		//TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		//SimpleDateFormat sdf = new SimpleDateFormat();
		//sdf.applyPattern("HHH:mm:ss:SSS");
		//BoardPanel.appendTextln("TankItemLoader: Time for doInBackground(): "+ sdf.format(duration));
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void done() {
		List<IqmDataBox> list;
		try {
			list = (ArrayList<IqmDataBox>) this.get();
			setTankAndManager(list);
		} catch (ExecutionException e) {
			// log the error message
			logger.error("An error occurred: ", e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			// log the error message
			logger.error("An error occurred: ", e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("An error occurred: ", e);
			e.printStackTrace();
		} finally {
			GUITools.getStatusPanel().resetProgressBarValueStack();
		}
		
	}

	/**
	 * Set the {@link Tank} and {@link Manager}.
	 * 
	 * @param listNew
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void setTankAndManager(List<IqmDataBox> listNew) throws Exception {
		if (listNew == null || listNew.isEmpty())
			return;

		TankPanel tp = (TankPanel) Tank.getInstance().getTankPanel();

		int tankIdx = -1;

		if (replaceItem) {
			// replace the tank position
			tp.getModel().setElementAt(listNew, replaceIndex);
			tankIdx = replaceIndex;
		} else {
			// add the new image, or image stack (both are DefaultListModel)
			// to
			// the tank
			tp.getModel().addElement(listNew);
			tankIdx = tp.getModel().getSize() - 1;
		}

		// replace the model in the tank with itself and ensure that the
		// index is visible
		tp.getTankList().ensureIndexIsVisible(tp.getModel().getSize() - 1);

		int modelSize = tp.getModel().getSize();
		int maxIndex = modelSize - 1;
		Tank.getInstance().setMaxIndex(maxIndex);

		int tabIdx = -1;
		boolean selectAll = false;

		// activate the corresponding tab
		switch (this.getDataType()) {
		case IMAGE:
			tabIdx = 0;
			break;

		case PLOT:
			tabIdx = 1;
			selectAll = true;
			break;

		case TABLE:
			tabIdx = 2;
			selectAll = true;
			break;

		case CUSTOM:
			tabIdx = 3;
			break;

		default:
			break;
		}

		// select the tab in the viewport
		GUITools.getMainFrame().getItemContent().setSelectedIndex(tabIdx);

		// send icons and data to the manager
		Tank.getInstance().setManagerForTankIndex(tankIdx);

		// select all
		if (selectAll) {
			Manager.getInstance().getManagerPanel().selectAll();
		}

	}

	/**
	 * This method adds 1...n new items to the {@link JList}. An item stack is
	 * stored as a {@link List} of {@link IqmDataBox}es. All elements (Plot,
	 * File, Image, Custom) are stored in an {@link IqmDataBox}.
	 * <p>
	 * For instance, a single image is represented as a list of one
	 * {@link IqmDataBox}.
	 * 
	 * @param list
	 * @return a {@link List} of {@link IqmDataBox}es
	 */
	protected List<IqmDataBox> addNewItems(List<IqmDataBox> list,
			boolean isVirtual, File virtDir) {

		List<IqmDataBox> listNew = new ArrayList<IqmDataBox>();

		switch (this.getDataType()) {
		case IMAGE:
		case PLOT:
		case TABLE:
		case CUSTOM:
			for (int n = 0; n < list.size(); n++) {
				IqmDataBox box = list.get(n);

				// CONVERT TO VIRTUAL LIST IF APPLICATION IS IN VIRTUAL STATE
				// AND THE BOX IS NOT ALREADY VIRTUALIZED
				if (isVirtual && !(box instanceof VirtualDataBox)) {
					VirtualDataBox vBox = (VirtualDataBox) VirtualDataManager
							.getInstance().save(box, virtDir);
					listNew.add(n, vBox);
					box = null;
				} else {
					listNew.add(n, box);
				}
				this.setProgress((n + 1) * 100 / list.size());
				try {
					Thread.sleep(2L);
				} catch (InterruptedException e) {
					logger.error("An error occurred: ", e);
				}
			}
			break;
		default:
			break;
		}
		return listNew;
	}

	/**
	 * @return the list
	 */
	public List<IqmDataBox> getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<IqmDataBox> list) {
		this.list = list;
	}

	/**
	 * @return the dataType
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

}
