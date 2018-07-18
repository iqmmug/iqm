package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: TankPanelDropEventHandler.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.workflow.ITank;
import at.mug.iqm.commons.util.DialogUtil;

/**
 * This class listens for drag events of files from the file system. Any image
 * files are loaded to the {@link ITank}.
 * 
 * @author Philipp Kainz
 * 
 */
public class TankPanelDropEventHandler implements DropTargetListener {
	
	private File selectedFile;
	
	/**
	 * The constructor.
	 */
	public TankPanelDropEventHandler() {
	}

	protected void checkAcceptance(DropTargetDragEvent dtde) {
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
		} else {
			dtde.rejectDrag();
		}
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		checkAcceptance(dtde);
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		checkAcceptance(dtde);
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent dtde) {

		Transferable transferable = dtde.getTransferable();
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			dtde.acceptDrop(dtde.getDropAction());
			try {

				List<File> transferData = (List<File>) transferable
						.getTransferData(DataFlavor.javaFileListFlavor);
				if (transferData != null && transferData.size() > 0) {

					// check for too large lists
					if (transferData.size() > 200) {
						int ans = DialogUtil
								.getInstance()
								.showDefaultWarnMessage(
										I18N.getMessage(
												"question.dragndrop.largefilelist",
												transferData.size()));
						if (ans != IDialogUtil.YES_OPTION) {
							return;
						}
					}

					// parse content type (image/plot) and add the files or
					// return
					DataType t = Application.getTank().parseDraggedContent(
							transferData);
					if (t == null)
						return;

					switch (t) {
					case IMAGE:
						// Add the image files from harddisk to the tank
						Application.getTank().loadImagesFromHD(transferData);
						break;
					case PLOT:
						// choose one file from the list
						if (transferData.size() > 1) {
							FileSelectionDialog fsf = new FileSelectionDialog();
							fsf.setFileList(transferData);
							fsf.setEventHandler(this);
							fsf.setLocationRelativeTo(null);
							fsf.setVisible(true);
							
							if (selectedFile == null) 
								return;
							else
								Application.getTank().runPlotParser(selectedFile);
						} else {
							// run the parser with the file
							Application.getTank().runPlotParser(transferData.get(0));
						}
						break;
						
					case TABLE:
						// TODO currently not supported
						break;
					default:
						break;
					}

					// complete the drag action
					dtde.dropComplete(true);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			dtde.rejectDrop();
		}
	}

	public void setSelectedFile(File f){
		this.selectedFile = f;
	}
}
