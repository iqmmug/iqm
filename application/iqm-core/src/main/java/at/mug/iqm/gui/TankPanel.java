package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: TankPanel.java
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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.gui.ITankPanel;
import at.mug.iqm.api.gui.TankPanelDropEventHandler;
import at.mug.iqm.api.gui.WaitingDialog;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.commons.util.CompletionWaiter;
import at.mug.iqm.commons.util.CursorFactory;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.ScrollablePanel;
import at.mug.iqm.commons.util.TankListCellRenderer;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.util.DeleteFilesTask;
import at.mug.iqm.core.workflow.Manager;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.util.GUITools;

/**
 * This panel is responsible for displaying the history of processing steps.
 * Each cell represents either a processing step, a single item or an item
 * stack.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2012 02 24 new "Iqm2" layout
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class TankPanel extends JPanel implements ITankPanel, ActionListener,
		MouseListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -4927390300874649683L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(TankPanel.class);

	private int keepNumber = -1;

	private JPanel tankContentPanel = null; // is parent container

	private ScrollablePanel tankListContainer = null; // is container for JList

	private JMenuBar jMenuBar = null;
	private JMenu optionMenu = null;
	private JRadioButtonMenuItem tankOpt1 = null;
	private JRadioButtonMenuItem tankOpt2 = null;
	private JRadioButtonMenuItem tankOpt3 = null;
	private JRadioButtonMenuItem tankOpt4 = null;
	private JMenuItem menuItemDelete = null;
	
	/**
	 * Support for drag and drop of files.
	 */
	private DropTarget dropTarget;
    private TankPanelDropEventHandler dropTargetHandler;

	/**
	 * The tank list.
	 */
	private JList tankList = null;

	/**
	 * Model where the items are stored.
	 */
	private DefaultListModel model = null;
	/**
	 * The button group for the selection in the menu.
	 */
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JMenuItem mntmMergeSelectedItems;

	/**
	 * This method initializes Tank, default orientation is horizontal.
	 * 
	 */
	public TankPanel() {
		super();
		this.createAndShowGUI();
	}

	/**
	 * This method creates and shows the GUI
	 */
	public void createAndShowGUI() {
		this.setLayout(new GridLayout(1, 0));

		// horizontal layout
		this.setPreferredSize(new Dimension(800, 103));
		this.addPropertyChangeListener(GUITools.getStatusPanel());

		this.add(this.createTankContentPanel());
	}

	/**
	 * This method gets the flag items to keep.
	 * 
	 * @return the number of items to keep
	 */
	@Override
	public int getKeepNumber() {
		return keepNumber;
	}

	/**
	 * This method sets the flag how many images to keep.
	 * 
	 * @param arg 
	 */
	@Override
	public void setKeepNumber(int arg) {
		keepNumber = arg;
	}

	/**
	 * This method initializes tankContentPanel. It is the top-most container
	 * for the components.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createTankContentPanel() {
		if (tankContentPanel == null) {
			tankContentPanel = new JPanel();
			tankContentPanel.setLayout(new BorderLayout());

			// according to
			// http://tips4java.wordpress.com/2009/12/20/scrollable-panel/
			tankListContainer = new ScrollablePanel();

			tankListContainer
					.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
			// set the width and height of the JList (vertical size)
			tankListContainer.setPreferredSize(new Dimension(800, 101));

			tankListContainer.setLayout(new GridLayout(1, 0));
			tankListContainer.add(new JScrollPane(this.getTankList()));

			// assemble
			tankContentPanel.add(createOptionsMenuBar(),
					BorderLayout.LINE_START);
			tankContentPanel.add(new JScrollPane(tankListContainer),
					BorderLayout.CENTER);
		}
		return tankContentPanel;
	}

	@Override
	public DefaultListModel getModel() {
		return this.model;
	}

	@Override
	public void setModel(DefaultListModel arg) {
		this.model = arg;
	}

	/**
	 * This method initializes MenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar createOptionsMenuBar() {
		if (jMenuBar == null) {
			jMenuBar = new JMenuBar();
			jMenuBar.add(createOptionMenu());
		}
		return jMenuBar;
	}

	/**
	 * This method initializes optionMenu
	 * 
	 * @return javax.swing.JjMenu
	 */
	private JMenu createOptionMenu() {
		if (optionMenu == null) {
			optionMenu = new JMenu();
			optionMenu.setIcon(new ImageIcon(Resources
					.getImageURL("icon.tankpanel.options")));
			optionMenu.setToolTipText(I18N
					.getGUILabelText("tank.optionMenu.ttp"));
			optionMenu.add(getMntmMergeSelectedItems());
			optionMenu.addSeparator();
			optionMenu.add(createKeepAllImages());
			optionMenu.add(createKeep1Image());
			optionMenu.add(createKeep10Images());
			optionMenu.add(createKeep50Images());
			optionMenu.addSeparator();
			optionMenu.add(createJMenuItemCustomDelete());
		}
		return optionMenu;
	}

	/**
	 * This method initializes tankOpt1
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JRadioButtonMenuItem createKeepAllImages() {
		if (tankOpt1 == null) {
			tankOpt1 = new JRadioButtonMenuItem();
			tankOpt1.addActionListener(this);
			buttonGroup.add(tankOpt1);
			tankOpt1.setSelected(true);
			tankOpt1.setText(I18N
					.getGUILabelText("tank.optionMenu.keepAllImages"));
			tankOpt1.setActionCommand("tankopt1");
		}
		return tankOpt1;
	}

	/**
	 * This method initializes tankOpt2
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JRadioButtonMenuItem createKeep1Image() {
		if (tankOpt2 == null) {
			tankOpt2 = new JRadioButtonMenuItem();
			tankOpt2.addActionListener(this);
			buttonGroup.add(tankOpt2);
			tankOpt2.setText(I18N
					.getGUILabelText("tank.optionMenu.keep1Image"));
			tankOpt2.setActionCommand("tankopt2");
		}
		return tankOpt2;
	}

	/**
	 * This method initializes tankOpt3
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JRadioButtonMenuItem createKeep10Images() {
		if (tankOpt3 == null) {
			tankOpt3 = new JRadioButtonMenuItem();
			tankOpt3.addActionListener(this);
			buttonGroup.add(tankOpt3);
			tankOpt3.setText(I18N
					.getGUILabelText("tank.optionMenu.keep10Images"));
			tankOpt3.setActionCommand("tankopt3");
		}
		return tankOpt3;
	}

	/**
	 * This method initializes tankOpt4
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JRadioButtonMenuItem createKeep50Images() {
		if (tankOpt4 == null) {
			tankOpt4 = new JRadioButtonMenuItem();
			tankOpt4.addActionListener(this);
			buttonGroup.add(tankOpt4);
			tankOpt4.setText(I18N
					.getGUILabelText("tank.optionMenu.keep50Images"));
			tankOpt4.setActionCommand("tankopt4");
		}
		return tankOpt4;
	}

	/**
	 * This method initializes jMenuItemDelete
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createJMenuItemCustomDelete() {
		if (menuItemDelete == null) {
			menuItemDelete = new JMenuItem();
			menuItemDelete.setText(I18N
					.getGUILabelText("tank.optionMenu.deleteSelected"));
			menuItemDelete.setMnemonic(KeyEvent.VK_DELETE);
			menuItemDelete.setActionCommand("delete");
			menuItemDelete.addActionListener(this);
		}
		return menuItemDelete;
	}

	/**
	 * This method creates the TankList. It will hold the model for the tank
	 * images.
	 * 
	 * @return javax.swing.JList
	 */
	@Override
	public JList getTankList() {
		if (tankList == null) {
			tankList = new JList();
			if (this.model == null) {
				this.model = new DefaultListModel();
			}

			tankList.setModel(this.model);

			// we need our own renderer class for the cells
			tankList.setCellRenderer(new TankListCellRenderer());
			tankList.setFixedCellWidth(70);

			tankList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			tankList.setVisibleRowCount(1);
			tankList.addMouseListener(this);
			tankList.addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent evt) {
					handleListKeyReleased(evt);
				}
			});
		}
		return tankList;
	}

	/**
	 * This method listens, if the "Delete"-key is released in the tank list.
	 * 
	 * @param evt
	 *            - the key event
	 */
	private void handleListKeyReleased(KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
			this.showDeleteConfirmation(null);
		}
	}

	/**
	 * This method deletes all images in the tank by calling
	 * {@link TankPanel#deleteSelectedIndices(int[])}.
	 */
	@Override
	public void deleteAllIndices() {
		int size = this.model.getSize();
		logger.debug("Deleting " + size + " items.");
		if (size > 0) { // only if there are really items to delete
			int[] indices = new int[size];
			for (int i = 0; i < size; i++) {
				indices[i] = i;
			}
			this.deleteSelectedIndices(indices);
		}
		Tank.getInstance().setCurrIndex(-1);
	}

	/**
	 * This method deletes images of selected indices by calling
	 * deleteSelectedIndex(int idx).
	 * 
	 * @param indices
	 *            - int[] of the selected indices
	 */
	public void deleteSelectedIndices(int[] indices) {
		// set wait cursor while deleting
		this.setCursor(CursorFactory.getWaitCursor());
		for (int i = indices.length - 1; i >= 0; i--) {
			this.deleteSelectedIndex(indices[i]);
		}
		this.setCursor(CursorFactory.getDefaultCursor());
	}

	/**
	 * This method deletes images of the selected index.
	 * 
	 * @param index
	 *            - int, the selected index
	 */
	private void deleteSelectedIndex(int index) {
		if (index == -1) {
			logger.debug("No items available to delete.");
			return;
		}
		int items = ((List<IqmDataBox>) this.model.getElementAt(index)).size();

		// delete the svo files on the disk, too
		for (int i = 0; i < items; i++) {
			IqmDataBox iqmDataBox = ((List<IqmDataBox>) this.model
					.getElementAt(index)).get(i);

			if (iqmDataBox instanceof IVirtualizable) {
				// remove each box from the virtual cache
				VirtualDataManager.getInstance().delete(
						(IVirtualizable) iqmDataBox);

				if (i == items - 1) {
					try {
						// use the file deletion task to purge the directory
						// where the svo files of this stack are stored
						DeleteFilesTask dft = new DeleteFilesTask(
								((IVirtualizable) iqmDataBox).getLocation()
										.getParentFile().toString());
						WaitingDialog dialog = new WaitingDialog(
								I18N
										.getMessage("application.dialog.waiting.deletingTankData"),
								false);
						dft.addPropertyChangeListener(new CompletionWaiter(
								dialog));
						dft.execute();
						dialog.setVisible(true);
					} catch (Exception e1) {
						logger.error(e1);
						this.setCursor(CursorFactory.getDefaultCursor());
					}
				}
			}

		}
		// remove the elements from tank list
		this.deleteSelectedIndexFromTank(index);

		Tank.getInstance().setCurrIndex(-1);
	}

	/**
	 * This method deletes the selected images from the tank
	 * 
	 * @param idx
	 *            - the index to be removed
	 */
	private void deleteSelectedIndexFromTank(int idx) {
		// remove the index from the model
		this.model.remove(idx);
		Manager.getInstance().resetLeftModel(); // for an empty left manager
												// list
		Manager.getInstance().resetRightModel(); // for an empty right manager
													// list
		logger.debug("Removed item(s) from tank index " + (idx + 1));
		Tank.getInstance().setCurrIndex(-1);
		System.gc();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("tankopt1".equals(e.getActionCommand())) {
			this.setKeepNumber(ITankPanel.KEEP_ALL);
		} else if ("tankopt2".equals(e.getActionCommand())) {
			this.setKeepNumber(ITankPanel.KEEP_1);
		} else if ("tankopt3".equals(e.getActionCommand())) {
			this.setKeepNumber(ITankPanel.KEEP_10);
		} else if ("tankopt4".equals(e.getActionCommand())) {
			this.setKeepNumber(ITankPanel.KEEP_50);
		}

		if ("delete".equals(e.getActionCommand())) { // delete selected images
			this.showDeleteConfirmation(null);
		}
	}

	/**
	 * A double left mouse click loads the items from the tank index to the
	 * selected manager list (left or right).
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2)) {
			int index = tankList.locationToIndex(e.getPoint());
			Tank.getInstance().setManagerForTankIndex(index);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) { // right button
			this.showDeleteConfirmation(e);
		}
	}

	/**
	 * This method shows a notification to the user whether he/she wants to
	 * delete the selected indices from the tank list. Pass <code>null</code>,
	 * if you don't want to determine the mouse position.
	 * 
	 * @param e
	 *            - the mouse event for calculating the mouse position
	 */
	private void showDeleteConfirmation(MouseEvent e) {
		if (!this.model.isEmpty()) {
			// if the event is triggered by a mouse event
			// and one single element is selected
			if (e != null) {
				// more items selected
				int[] indices = tankList.getSelectedIndices();
				if (indices.length > 1) {
					this.confirmDeletionOfMoreItems();
				} else {
					// the item the user clicked on
					int index = tankList.locationToIndex(e.getPoint());
					this.confirmDeletionOfItem(index);
				}
			}
			// this branch is chosen at a key event, when the passed MouseEvent
			// is null
			else if (e == null) {
				this.confirmDeletionOfMoreItems();
			}
		}
	}

	private void confirmDeletionOfItem(int index) {
		int selection = DialogUtil.getInstance().showDefaultQuestionMessage(
				I18N.getMessage("application.dialog.tankDelete.text",
						Tank.getInstance().getTankDataAt(index).size(), "["
								+ (index + 1) + "]"));
		if (selection == IDialogUtil.YES_OPTION) {
			logger.debug("User selected >YES< on confirmation dialog for deleting tank items. Items will be deleted from tank ["
					+ (index + 1) + "].");
			this.deleteSelectedIndex(index);
		} else {
			logger.debug("User selected >NO< on confirmation dialog for deleting tank items. Items will not be deleted.");
		}
	}

	private void confirmDeletionOfMoreItems() {
		int[] indices = tankList.getSelectedIndices();
		int sumImages = 0;
		StringBuffer sbIndices = new StringBuffer();
		for (int index : indices) {
			sumImages += Tank.getInstance().getTankDataAt(index).size();
			sbIndices.append((index + 1) + ", ");
		}
		String strIndices = new String(sbIndices);
		try {
			int selection = DialogUtil.getInstance()
					.showDefaultQuestionMessage(
							I18N.getMessage(
									"application.dialog.tankDelete.text",
									sumImages,
									"["
											+ strIndices.substring(0,
													strIndices.length() - 2)
											+ "]"));
			if (selection == IDialogUtil.YES_OPTION) {
				logger.debug("User selected >YES< on confirmation dialog for deleting tank images. Images will be deleted from tank ["
						+ strIndices.substring(0, strIndices.length() - 2)
						+ "].");
				this.deleteSelectedIndices(indices);
			} else {
				logger.debug("User selected >NO< on confirmation dialog for deleting tank images. Images will not be deleted.");
			}
		} catch (StringIndexOutOfBoundsException err) {
			logger.error("An error occurred: ", err);
		}
	}

	private JMenuItem getMntmMergeSelectedItems() {
		if (mntmMergeSelectedItems == null) {
			mntmMergeSelectedItems = new JMenuItem(
					I18N.getGUILabelText("tank.optionMenu.exportSelected.text"));
			mntmMergeSelectedItems.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					handleExportAction();
				}
			});
			mntmMergeSelectedItems.setIcon(new ImageIcon(Resources
					.getImageURL("icon.exportItem")));
		}
		return mntmMergeSelectedItems;
	}

	protected void handleExportAction() {
		if (model.isEmpty()){
			return;
		}
		
		// get the selected indices
		int[] tankIdcs = tankList.getSelectedIndices();

		// check them for homogeneous data types
		DataType firstType = Tank.getInstance().getTankDataAt(tankIdcs[0])
				.get(0).getDataType();

		List<IqmDataBox> mergedItems = new ArrayList<IqmDataBox>();

		// check the data types
		for (int i : tankIdcs) {
			List<IqmDataBox> itemList = Tank.getInstance().getTankDataAt(i);
			for (int j = 0; j < itemList.size(); j++) {
				IqmDataBox box = itemList.get(j);
				DataType toCheck = box.getDataType();
				if (firstType != toCheck) {
					String msg = "Cannot merge tank items, since the data types are heterogeneous: ["
							+ firstType + "<>" + toCheck + "].";
					DialogUtil.getInstance().showDefaultErrorMessage(msg);
					logger.info(msg);
					return;
				} else {
					try {
						if (box instanceof IVirtualizable) {
							box = VirtualDataManager.getInstance().load(
									(IVirtualizable) box);
						}

						mergedItems.add(box.clone());
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
						logger.error("An error occurred: ", e);
						return;
					}
				}
			}

		}

		if (!mergedItems.isEmpty()) {
			Tank.getInstance().addNewItems(mergedItems);
		}

	}
	
    protected DropTarget getTankPanelDropTarget() {
        if (dropTarget == null) {
            dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, null);
        }
        return dropTarget;
    }

    protected TankPanelDropEventHandler getTankPanelDropTargetHandler() {
        if (dropTargetHandler == null) {
            dropTargetHandler = new TankPanelDropEventHandler();
        }
        return dropTargetHandler;
    }

    /**
     * Wire the notifications for the drop target listener.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        try {
            getTankPanelDropTarget().addDropTargetListener(getTankPanelDropTargetHandler());
        } catch (TooManyListenersException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Remove notifications for the drop target listener.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        getTankPanelDropTarget().removeDropTargetListener(getTankPanelDropTargetHandler());
    }
}
