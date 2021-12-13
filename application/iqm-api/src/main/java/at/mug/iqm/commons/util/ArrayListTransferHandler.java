package at.mug.iqm.commons.util;

/*
 * #%L
 * Project: IQM - API
 * File: ArrayListTransferHandler.java
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


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import at.mug.iqm.api.Application;

/*
 * ArrayListTransferHandler.java is used by the 1.4 DragListDemo.java example.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ArrayListTransferHandler extends TransferHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4387070255003751257L;

	DataFlavor localArrayListFlavor, serialArrayListFlavor;

	String localArrayListType = DataFlavor.javaJVMLocalObjectMimeType
			+ ";class=java.util.ArrayList";

	JList source = null;

	int[] selectedSourceIndices = null;

	int insertIndex = -1; // Location where items were added

	int itemsForInsertion = 0; // Number of items added

	public ArrayListTransferHandler() {
		try {
			localArrayListFlavor = new DataFlavor(localArrayListType);
		} catch (ClassNotFoundException e) {
			System.err
					.println("ArrayListTransferHandler: unable to create data flavor");
		}
		serialArrayListFlavor = new DataFlavor(ArrayList.class, "ArrayList");
	}

	public boolean importData(JComponent c, Transferable t) {
		System.out.println("Import Data...");
		JList target = null;
		ArrayList listToInsert = null;
		if (!canImport(c, t.getTransferDataFlavors())) {
			return false;
		}
		try {
			target = (JList) c;
			if (hasLocalArrayListFlavor(t.getTransferDataFlavors())) {
				listToInsert = (ArrayList) t
						.getTransferData(localArrayListFlavor);
			} else if (hasSerialArrayListFlavor(t.getTransferDataFlavors())) {
				listToInsert = (ArrayList) t
						.getTransferData(serialArrayListFlavor);
			} else {
				return false;
			}
		} catch (UnsupportedFlavorException ufe) {
			System.out.println("importData: unsupported data flavor");
			return false;
		} catch (IOException ioe) {
			System.out.println("importData: I/O exception");
			return false;
		}

		// At this point we use the same code to retrieve the data
		// locally or serially.

		// We'll drop at the current selected index.
		int dropIndex = target.getSelectedIndex();

		// Prevent the user from dropping data back on itself.
		// For example, if the user is moving items #4,#5,#6 and #7 and
		// attempts to insert the items after item #5, this would
		// be problematic when removing the original items.
		// This is interpreted as dropping the same data on itself
		// and has no effect.
		if (source.equals(target)) {
			if (selectedSourceIndices != null
					&& dropIndex >= selectedSourceIndices[0] - 1
					&& dropIndex <= selectedSourceIndices[selectedSourceIndices.length - 1]) {
				selectedSourceIndices = null;
				return true;
			}
		}

		// alter the models
		DefaultListModel targetListModel = (DefaultListModel) target.getModel();
		int maxIndex = targetListModel.getSize();

		// determine the position of the drop index
		if (dropIndex < 0) {
			dropIndex = maxIndex;
		} else {
			dropIndex++;
			if (dropIndex > maxIndex) {
				dropIndex = maxIndex;
			}
		}

		// set the index after which the dragged components will be added
		insertIndex = dropIndex;

		// set the total number of indices to insert
		itemsForInsertion = listToInsert.size();
		System.out.println("addCount: " + itemsForInsertion);

		// re-build the entire target model after the drop index
		for (int i = 0; i < listToInsert.size(); i++) {
			targetListModel.add(dropIndex++, listToInsert.get(i));
		}

		System.out.println("TargetListModel: ");
		for (int i = 0; i < targetListModel.getSize(); i++)
			System.out.println(targetListModel.get(i));

		return true;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
		System.out.println("Export done...");
		if ((action == MOVE) && (selectedSourceIndices != null)) {
			DefaultListModel sourceModel = (DefaultListModel) source.getModel();

			// If we are moving items around in the same list, we
			// need to adjust the indices accordingly since those
			// after the insertion point have moved.
			if (itemsForInsertion > 0) {
				for (int i = 0; i < selectedSourceIndices.length; i++) {
					if (selectedSourceIndices[i] > insertIndex) {
						// add the indices from the array list
						// e.g. if insertIndex = 5 in a list of 8 items
						// and itemsForInsertion = 3, we set the following
						// indices
						// 1.) 6 --> 6+3=9
						// 2.) 7 --> 7+3=10
						// 3.) 8 --> 8+3=11
						selectedSourceIndices[i] += itemsForInsertion;
					}
				}
			}
			for (int i = selectedSourceIndices.length - 1; i >= 0; i--) {
				sourceModel.remove(selectedSourceIndices[i]);
			}

			// for (int i : selectedSourceIndices){
			// System.out.println(i);
			// }
			//
			// System.out.println("SourceListModel: ");
			// for (int i = 0; i < sourceModel.getSize(); i++)
			// System.out.println(sourceModel.get(i));
		}

		// TODO switch the items in the tank list at the selected index
		int tankIdx = -1;
		if (Application.getManager().isLeftListActive()) {
			tankIdx = Application.getManager().getTankIndexInMainLeft();
		} else if (Application.getManager().isRightListActive()) {
			tankIdx = Application.getManager().getTankIndexInMainRight();
		}
		System.out.println(tankIdx);
		// List<IqmDataBox> boxes =
		// Application.getTank().getTankDataAt(tankIdx);

		selectedSourceIndices = null;
		insertIndex = -1;
		itemsForInsertion = 0;
	}

	private boolean hasLocalArrayListFlavor(DataFlavor[] flavors) {
		System.out.println("hasLocalArrayListFlavor...");
		if (localArrayListFlavor == null) {
			return false;
		}

		for (int i = 0; i < flavors.length; i++) {
			if (flavors[i].equals(localArrayListFlavor)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasSerialArrayListFlavor(DataFlavor[] flavors) {
		System.out.println("hasSerialArrayListFlavor...");
		if (serialArrayListFlavor == null) {
			return false;
		}

		for (int i = 0; i < flavors.length; i++) {
			if (flavors[i].equals(serialArrayListFlavor)) {
				return true;
			}
		}
		return false;
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		System.out.println("Can Import...");
		if (hasLocalArrayListFlavor(flavors)) {
			return true;
		}
		if (hasSerialArrayListFlavor(flavors)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	protected Transferable createTransferable(JComponent c) {
		System.out.println("Creating transferable...");
		if (c instanceof JList) {
			source = (JList) c;
			selectedSourceIndices = source.getSelectedIndices();
			Object[] values = source.getSelectedValues();
			if (values == null || values.length == 0) {
				return null;
			}
			ArrayList alist = new ArrayList(values.length);
			for (int i = 0; i < values.length; i++) {
				Object o = values[i];
				alist.add(o);
			}
			return new ArrayListTransferable(alist);
		}
		return null;
	}

	public int getSourceActions(JComponent c) {
		System.out.println("get Source Actions...");
		return COPY_OR_MOVE;
	}

	public class ArrayListTransferable implements Transferable {
		ArrayList data;

		public ArrayListTransferable(ArrayList alist) {
			data = alist;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			System.out.println("Getting transfer data...");
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return data;
		}

		public DataFlavor[] getTransferDataFlavors() {
			System.out.println("Getting transfer data flavours...");
			return new DataFlavor[] { localArrayListFlavor,
					serialArrayListFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			System.out.println("isDataFlavourSupported...");
			if (localArrayListFlavor.equals(flavor)) {
				return true;
			}
			if (serialArrayListFlavor.equals(flavor)) {
				return true;
			}
			return false;
		}
	}
}
