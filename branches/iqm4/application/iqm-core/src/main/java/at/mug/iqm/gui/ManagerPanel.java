package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: ManagerPanel.java
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

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.media.jai.PlanarImage;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.gui.IManagerPanel;
import at.mug.iqm.api.gui.util.ToggleRunnable;
import at.mug.iqm.api.model.IVirtualizable;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.DataType;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.persistence.VirtualDataManager;
import at.mug.iqm.api.processing.IExecutionProtocol;
import at.mug.iqm.commons.gui.OpenImageDialog;
import at.mug.iqm.commons.util.ArrayListTransferHandler;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.ManagerListCellRendererLeft;
import at.mug.iqm.commons.util.ManagerListCellRendererRight;
import at.mug.iqm.commons.util.ScrollablePanel;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.util.ApplicationObserver;
import at.mug.iqm.core.util.CoreTools;
import at.mug.iqm.core.workflow.ExecutionProxy;
import at.mug.iqm.core.workflow.Manager;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.dialog.ManagerItemExportDialog;
import at.mug.iqm.gui.util.GUITools;

/**
 * This class represents the manager. Two lists contain the items, where
 * processing operators can be applied to.
 * 
 * @author Helmut Ahammer, Philipp Kainz, Michael Mayrhofer-R.
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ManagerPanel extends JPanel implements IManagerPanel,
		MouseListener, ActionListener, ChangeListener, ListSelectionListener {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 5014949081846105468L;

	// class specific logger
	private static Class<?> caller = ManagerPanel.class;
	private static final Logger logger = Logger.getLogger(ManagerPanel.class);

	public static final int MANAGER_LIST_LEFT = 0;
	public static final int MANAGER_LIST_RIGHT = 1;

	private JPanel managerContentPanel = null;

	private JPanel controlsPanel;

	private JPanel listPanel = null;

	private ScrollablePanel managerListContainerLeft = null;
	private ScrollablePanel managerListContainerRight = null;

	private JCheckBox chbxAutoPreview = null;
	private JCheckBox chbxTogglePreview = null;

	private JLabel lblItemNr = null;
	private JSlider itemSlider;

	private DefaultListModel managerModelLeft = null;
	private JList managerListLeft = null;
	private DefaultListModel managerModelRight = null;
	private JList managerListRight = null;

	private ExecutorService execToggle = null;

	private JRadioButton radbutMainLeft = null;
	private JRadioButton radbutMainRight = null;
	private ButtonGroup buttGroupMain = null;

	private JCheckBox chbxMainLeft = null;
	private JCheckBox chbxMainRight = null;

	private JPanel toggleStatusPanel = null;

	/**
	 * The array of currently selected manager items. Default is [-1].
	 */
	private int[] currMgrIdxs = new int[] { -1 };

	/**
	 * The index of the item, the user clicked on. Default is 0.
	 */
	private int indexForDisplay = -1;

	/**
	 * An {@link ArrayListTransferHandler} for the left manager list, enabling
	 * drag&drop gestures.
	 */
	@SuppressWarnings("unused")
	private ArrayListTransferHandler alHandlerLeft = new ArrayListTransferHandler();

	/**
	 * An {@link ArrayListTransferHandler} for the right manager list, enabling
	 * drag&drop gestures.
	 */
	@SuppressWarnings("unused")
	private ArrayListTransferHandler alHandlerRight = new ArrayListTransferHandler();
	private JButton btnExport;
	private JLabel lblTankIndexLeft;
	private JLabel lblTankIndexRight;
	private JPanel panel_Left;
	private JPanel panel_Right;
	private JLabel lblVirtualIconLeft;
	private JLabel lblVirtualIconRight;
	private JPanel mainControlsLeft;
	private JPanel mainControlsRight;

	/**
	 * This is the default constructor
	 */
	public ManagerPanel() {
		super();

		// initializing
		this.managerContentPanel = new JPanel();

		this.controlsPanel = new JPanel();
		controlsPanel.setBorder(new TitledBorder(null, I18N
				.getGUILabelText("manager.panel.title"), TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		this.listPanel = new JPanel();

		this.chbxAutoPreview = new JCheckBox();
		this.chbxTogglePreview = new JCheckBox();

		this.lblItemNr = new JLabel();

		this.managerModelLeft = new DefaultListModel();
		this.managerListLeft = new JList();
		this.managerModelRight = new DefaultListModel();
		this.managerListRight = new JList();

		this.radbutMainLeft = new JRadioButton();
		this.radbutMainRight = new JRadioButton();
		this.buttGroupMain = new ButtonGroup();

		this.chbxMainLeft = new JCheckBox();
		this.chbxMainRight = new JCheckBox();

		this.toggleStatusPanel = new JPanel();

		this.createAndAssemble();
	}

	/**
	 * This method initializes the content and returns
	 * 
	 * @return javax.swing.JPanel
	 */
	private void createAndAssemble() {
		logger.debug("Creating subcomponents and assembling '"
				+ this.getClass().getName() + "'...");

		// set the gridbagConstraints for each component in controlsPanel
		GridBagConstraints gbcJLabelItemNr = new GridBagConstraints();
		gbcJLabelItemNr.gridx = 0;
		gbcJLabelItemNr.gridy = 0;
		gbcJLabelItemNr.gridwidth = 2;
		gbcJLabelItemNr.weightx = 1;
		gbcJLabelItemNr.insets = new Insets(0, 0, 5, 0); // int top, int left,
		// int bottom, int
		// right
		gbcJLabelItemNr.anchor = GridBagConstraints.CENTER;

		GridBagConstraints gbcJSliderItemNr = new GridBagConstraints();
		gbcJSliderItemNr.gridwidth = 2;
		gbcJSliderItemNr.fill = GridBagConstraints.HORIZONTAL;
		gbcJSliderItemNr.insets = new Insets(0, 5, 5, 5); // int top, int
		// left, int
		// bottom, int
		// right
		gbcJSliderItemNr.gridx = 0;
		gbcJSliderItemNr.gridy = 1;
		gbcJSliderItemNr.anchor = GridBagConstraints.CENTER;

		GridBagConstraints gbcAutoPreview = new GridBagConstraints();
		gbcAutoPreview.gridx = 0;
		gbcAutoPreview.gridy = 2;
		gbcAutoPreview.insets = new Insets(5, 5, 5, 5); // int top, int left,
		// int bottom, int
		// right
		gbcAutoPreview.anchor = GridBagConstraints.WEST;

		GridBagConstraints gbcTogglePreview = new GridBagConstraints();
		gbcTogglePreview.gridx = 0;
		gbcTogglePreview.gridy = 3;
		gbcTogglePreview.insets = new Insets(0, 5, 0, 5); // int top, int left,
		// int bottom, int
		// right
		gbcTogglePreview.anchor = GridBagConstraints.WEST;

		GridBagConstraints gbcSpinner = new GridBagConstraints();
		gbcSpinner.gridx = 1;
		gbcSpinner.gridy = 3;
		gbcSpinner.gridwidth = 1;
		gbcSpinner.fill = GridBagConstraints.VERTICAL;
		gbcSpinner.insets = new Insets(-3, 0, 0, 0); // int top, int left, int
		// bottom, int right
		gbcSpinner.anchor = GridBagConstraints.WEST;

		// -----------------------------END GridBagConstraints controlsPanel

		// set the constraints for the list panel
		GridBagConstraints gbcMainPanelLeft = new GridBagConstraints(); // MainPanel
		// left
		gbcMainPanelLeft.gridx = 1;
		gbcMainPanelLeft.gridy = 1;
		gbcMainPanelLeft.fill = GridBagConstraints.VERTICAL; // if BOTH: this
		// overrides the
		// setPreferredSize(Dimension
		// dim) in the
		// scroll panel
		gbcMainPanelLeft.weighty = 1;
		gbcMainPanelLeft.insets = new Insets(0, 0, 0, 0); // int top, int left,
		// int bottom, int
		// right

		GridBagConstraints gbcProgBarStack = new GridBagConstraints(); // ProgressbarStack
		gbcProgBarStack.gridx = 2;
		gbcProgBarStack.gridy = 1;
		gbcProgBarStack.weighty = 1;
		gbcProgBarStack.fill = GridBagConstraints.VERTICAL; // if BOTH: this
		// overrides the
		// setPreferredSize(Dimension
		// dim) in the
		// scroll panel
		gbcProgBarStack.insets = new Insets(21, 0, 0, 0); // int top, int left,
		// int bottom, int
		// right

		GridBagConstraints gbcMainPanelRight = new GridBagConstraints(); // right
		// MainPanel
		gbcMainPanelRight.gridx = 3;
		gbcMainPanelRight.gridy = 1;
		gbcMainPanelRight.fill = GridBagConstraints.VERTICAL;
		gbcProgBarStack.weighty = 1;
		gbcMainPanelRight.insets = new Insets(0, 0, 0, 0); // int top, int left,
		// int bottom, int
		// right

		// -----------------------------END GridBagConstraints listPanel

		// put everything in one row
		this.setLayout(new GridLayout(0, 1));
		this.setPreferredSize(new Dimension(178, 400));

		managerContentPanel.setLayout(new BorderLayout());

		// --------------------------------------------------------------------

		// construct the controls on the top
		controlsPanel.setLayout(new GridBagLayout());

		// add the scroll bar and the image number label
		controlsPanel.add(createJLabelItemNr(), gbcJLabelItemNr);
		controlsPanel.add(createItemSlider(), gbcJSliderItemNr);

		// add the first 2 check boxes to the scrollablePanel
		controlsPanel.add(createChbxAutoPreview(), gbcAutoPreview);

		btnExport = new JButton();
		btnExport.setBorderPainted(false);
		btnExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleExportAction(e);
			}
		});
		btnExport.setPreferredSize(new Dimension(23, 23));
		btnExport.setIcon(new ImageIcon(Resources
				.getImageURL("icon.exportItem")));
		btnExport.setToolTipText(I18N
				.getGUILabelText("manager.exportButton.ttp"));
		GridBagConstraints gbc_btnExport = new GridBagConstraints();
		gbc_btnExport.anchor = GridBagConstraints.SOUTHWEST;
		gbc_btnExport.insets = new Insets(0, 0, 5, 0);
		gbc_btnExport.gridx = 1;
		gbc_btnExport.gridy = 2;
		controlsPanel.add(btnExport, gbc_btnExport);
		controlsPanel.add(createChbxTogglePreview(), gbcTogglePreview);
		// add the spinner wheel for toggle preview
		controlsPanel.add(createToggleStatusPanel(), gbcSpinner);

		// add to parent layout
		managerContentPanel.add(this.controlsPanel, BorderLayout.NORTH);

		// --------------------------------------------------------------------

		listPanel.setLayout(new GridLayout());

		// create the panels for the two controls
		mainControlsLeft = new JPanel();
		mainControlsRight = new JPanel();
		mainControlsLeft.setLayout(new BorderLayout(0, 0));

		panel_Left = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_Left.getLayout();
		flowLayout_1.setVgap(0);
		flowLayout_1.setHgap(0);
		mainControlsLeft.add(panel_Left, BorderLayout.WEST);
		radbutMainLeft = new JRadioButton();
		panel_Left.add(radbutMainLeft);
		radbutMainLeft.setToolTipText(I18N
				.getGUILabelText("manager.isActivated"));
		radbutMainLeft.setMinimumSize(new Dimension(20, 20));
		radbutMainLeft.setPreferredSize(new Dimension(20, 20));
		radbutMainLeft.addActionListener(this);
		radbutMainLeft.setActionCommand("buttmain");
		radbutMainLeft.setSelected(true);
		chbxMainLeft = new JCheckBox();
		panel_Left.add(chbxMainLeft);
		chbxMainLeft.setToolTipText(I18N.getGUILabelText("manager.showHideThumbs"));
		chbxMainLeft.addActionListener(this);
		chbxMainLeft.setActionCommand("checkmain");
		chbxMainLeft.setSelected(true);

		mainControlsRight.setLayout(new BorderLayout(0, 0));

		panel_Right = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_Right.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		mainControlsRight.add(panel_Right, BorderLayout.WEST);
		radbutMainRight = new JRadioButton();
		panel_Right.add(radbutMainRight);
		radbutMainRight.setToolTipText(I18N
				.getGUILabelText("manager.isActivated"));
		radbutMainRight.setMinimumSize(new Dimension(20, 20));
		radbutMainRight.setPreferredSize(new Dimension(20, 20));
		radbutMainRight.addActionListener(this);
		radbutMainRight.setActionCommand("buttmain2");
		chbxMainRight = new JCheckBox();
		panel_Right.add(chbxMainRight);
		chbxMainRight
				.setToolTipText(I18N.getGUILabelText("manager.showHideThumbs"));
		chbxMainRight.setSelected(true);
		chbxMainRight.addActionListener(this);
		chbxMainRight.setActionCommand("checkmain2");

		// --------------------------------------------------------------------

		// create the panels, where the image(s) are held
		// standard border layout for the panels (components are placed under
		// the previous)
		JPanel mainPanelLeft = new JPanel(new BorderLayout());

		managerListContainerLeft = new ScrollablePanel();
		managerListContainerLeft
				.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.FIT);
		managerListContainerLeft.setLayout(new GridLayout(1, 0));
		managerListContainerLeft.add(new JScrollPane(this
				.createManagerListLeft()));

		// set the size of the list when scroll bars shall appear
		managerListContainerLeft.setPreferredSize(new Dimension(85, 100));

		JPanel mainPanelRight = new JPanel(new BorderLayout());
		managerListContainerRight = new ScrollablePanel();
		managerListContainerRight
				.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.FIT);
		managerListContainerRight.setLayout(new GridLayout(1, 0));
		managerListContainerRight.add(new JScrollPane(this
				.createManagerListRight()));

		// set the size of the list when scroll bars shall appear
		managerListContainerRight.setPreferredSize(new Dimension(85, 100));

		mainPanelLeft.add(mainControlsLeft, BorderLayout.NORTH);

		lblTankIndexLeft = new JLabel("");
		lblTankIndexLeft.setBorder(new EmptyBorder(0, 0, 0, 3));
		lblTankIndexLeft.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblTankIndexLeft.setHorizontalAlignment(SwingConstants.RIGHT);
		// mainControlsLeft.add(lblTankIndexLeft, BorderLayout.EAST);
		panel_Left.add(lblTankIndexLeft);
		lblTankIndexLeft.setToolTipText(I18N
				.getGUILabelText("manager.lblTankIndex.ttp"));

		lblVirtualIconLeft = new JLabel(new ImageIcon(
				Resources.getImageURL("icon.virtual.12x12")));
		lblVirtualIconLeft.setHorizontalAlignment(SwingConstants.LEFT);
		lblVirtualIconLeft.setHorizontalTextPosition(SwingConstants.LEFT);
		lblVirtualIconLeft.setBorder(new EmptyBorder(0, 2, 0, 2));
		// mainControlsLeft.add(lblVirtualIconLeft, BorderLayout.CENTER);
		lblVirtualIconLeft.setToolTipText(I18N
				.getGUILabelText("manager.virtualIcon.ttp"));
		mainPanelLeft.add(new JScrollPane(managerListContainerLeft),
				BorderLayout.CENTER);
		mainPanelRight.add(mainControlsRight, BorderLayout.NORTH);

		lblTankIndexRight = new JLabel("");
		lblTankIndexRight.setBorder(new EmptyBorder(0, 0, 0, 3));
		lblTankIndexRight.setToolTipText(I18N
				.getGUILabelText("manager.lblTankIndex.ttp"));
		// mainControlsRight.add(lblTankIndexRight, BorderLayout.EAST);
		panel_Right.add(lblTankIndexRight);

		lblVirtualIconRight = new JLabel(new ImageIcon(
				Resources.getImageURL("icon.virtual.12x12")));
		lblVirtualIconRight.setHorizontalAlignment(SwingConstants.LEFT);
		lblVirtualIconRight.setHorizontalTextPosition(SwingConstants.LEFT);
		lblVirtualIconRight.setBorder(new EmptyBorder(0, 2, 0, 2));
		// mainControlsRight.add(lblVirtualIconRight, BorderLayout.CENTER);
		lblVirtualIconRight.setToolTipText(I18N
				.getGUILabelText("manager.virtualIcon.ttp"));
		mainPanelRight.add(new JScrollPane(managerListContainerRight),
				BorderLayout.CENTER);

		listPanel.add(mainPanelLeft, gbcMainPanelLeft);
		listPanel.add(mainPanelRight, gbcMainPanelRight);

		managerContentPanel.add(this.listPanel, BorderLayout.CENTER);

		this.createButtonGroupMain();

		// add the generated content scrollablePanel to this scrollablePanel
		// (ensures top left location)
		this.add(managerContentPanel);
	}

	/**
	 * This method determines the currently selected manager items and exports
	 * them to either a new stack or to new single items in the tank list.
	 * <p>
	 * The user is prompted for the export type.
	 * 
	 * @param e
	 */
	protected void handleExportAction(ActionEvent e) {
		if (managerModelLeft.isEmpty() && managerModelRight.isEmpty()) {
			logger.info("No items are available for export.");
			return;
		}
		if (isRadioButtonMainLeftSelected() && managerModelLeft.isEmpty()) {
			logger.info("No items are available for export.");
			return;
		}
		if (isRadioButtonMainRightSelected() && managerModelRight.isEmpty()) {
			logger.info("No items are available for export.");
			return;
		}

		// take all data boxes in the selected indices and display
		// dialog
		ManagerItemExportDialog dlg = new ManagerItemExportDialog();
		int selection = dlg.getExportSelection();
		boolean pairwise = dlg.isPairwise();
		dlg.dispose();

		// get the selected manager indices
		int[] selMgrIdcs = this.getCurrMgrIdxs();
		int tankIdx1 = -1;
		int tankIdx2 = -1;

		if (selection == ManagerItemExportDialog.CANCEL) {
			return;
		} else if (selection == ManagerItemExportDialog.CHOICE_SINGLE_ITEMS) {
			// determine the selected manager list where the items should be
			// exported from
			if (isRadioButtonMainLeftSelected()) {
				tankIdx1 = Manager.getInstance().getTankIndexInMainLeft();
			} else if (isRadioButtonMainRightSelected()) {
				tankIdx1 = Manager.getInstance().getTankIndexInMainRight();
			}

			// add lists of item count 1 for each manager index
			for (int i : selMgrIdcs) {
				List<IqmDataBox> newItems = new ArrayList<IqmDataBox>();
				IqmDataBox box = Tank.getInstance().getTankIqmDataBoxAt(
						tankIdx1, i);
				if (box instanceof IVirtualizable) {
					box = VirtualDataManager.getInstance().load(
							(IVirtualizable) box);
				}
				try {
					newItems.add(box.clone());

					if (box != null && !newItems.isEmpty()) {
						Tank.getInstance().addNewItems(newItems);
					}
				} catch (CloneNotSupportedException e1) {
					e1.printStackTrace();
				}
			}

		} else if (selection == ManagerItemExportDialog.CHOICE_NEW_STACK) {

			if (pairwise) {
				// determine the selected manager list where the items should be
				// exported from
				if (isRadioButtonMainLeftSelected()
						&& !managerModelRight.isEmpty()) {
					tankIdx1 = Manager.getInstance().getTankIndexInMainLeft();
					tankIdx2 = Manager.getInstance().getTankIndexInMainRight();
				} else if (isRadioButtonMainRightSelected()
						&& !managerModelLeft.isEmpty()) {
					tankIdx1 = Manager.getInstance().getTankIndexInMainRight();
					tankIdx2 = Manager.getInstance().getTankIndexInMainLeft();
				} else {
					return;
				}

				// export pairwise, while both manager lists contain the index i
				// and the data types are equal
				for (int i : selMgrIdcs) {
					List<IqmDataBox> newItems = new ArrayList<IqmDataBox>();
					IqmDataBox box1 = null;
					try {
						box1 = Tank.getInstance().getTankIqmDataBoxAt(tankIdx1,
								i);
						if (box1 instanceof IVirtualizable) {
							box1 = VirtualDataManager.getInstance().load(
									(IVirtualizable) box1);
						}
						newItems.add(box1.clone());
					} catch (ArrayIndexOutOfBoundsException ex) {
					} catch (CloneNotSupportedException e1) {
						e1.printStackTrace();
					}

					IqmDataBox box2 = null;
					try {
						box2 = Tank.getInstance().getTankIqmDataBoxAt(tankIdx2,
								i);
						if (box2 instanceof IVirtualizable) {
							box2 = VirtualDataManager.getInstance().load(
									(IVirtualizable) box2);
						}
						newItems.add(box2.clone());
					} catch (ArrayIndexOutOfBoundsException ex) {
					} catch (CloneNotSupportedException e1) {
						e1.printStackTrace();
					}

					if (box1.getDataType() != box2.getDataType()) {
						String msg = "Cannot merge different data types: ["
								+ box1.getDataType() + "<>"
								+ box2.getDataType() + "].";
						DialogUtil.getInstance().showDefaultErrorMessage(msg);
						logger.info(msg);
						return;
					}

					if (box1 != null && box2 != null && !newItems.isEmpty()) {
						Tank.getInstance().addNewItems(newItems);
					} else {
						// otherwise leave the loop and return
						return;
					}
				}

			} else {
				// determine the selected manager list where the items should be
				// exported from
				if (isRadioButtonMainLeftSelected()) {
					tankIdx1 = Manager.getInstance().getTankIndexInMainLeft();
				} else if (isRadioButtonMainRightSelected()) {
					tankIdx1 = Manager.getInstance().getTankIndexInMainRight();
				}

				// add new items to the list and create new tank item in the end
				List<IqmDataBox> newItems = new ArrayList<IqmDataBox>();
				for (int i : selMgrIdcs) {
					IqmDataBox box = Tank.getInstance().getTankIqmDataBoxAt(
							tankIdx1, i);
					if (box instanceof IVirtualizable) {
						box = VirtualDataManager.getInstance().load(
								(IVirtualizable) box);
					}
					try {
						newItems.add(box.clone());
					} catch (CloneNotSupportedException e1) {
						e1.printStackTrace();
					}
				}
				if (!newItems.isEmpty()) {
					Tank.getInstance().addNewItems(newItems);
				}
			}
		}

	}

	private JLabel createJLabelItemNr() {
		lblItemNr.setText(I18N.getGUILabelText("manager.lblItemNr.text", 0, 0));
		return lblItemNr;
	}

	private JPanel createToggleStatusPanel() {
		toggleStatusPanel.add(new JLabel(new ImageIcon(Resources
				.getImageURL("icon.togglePreview"))));
		toggleStatusPanel.setVisible(false);
		return toggleStatusPanel;
	}

	/**
	 * This method initializes chbxAutoPreview
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox createChbxAutoPreview() {
		chbxAutoPreview = new JCheckBox();
		chbxAutoPreview.setText(I18N
				.getGUILabelText("manager.chbxAutoPreview.text"));
		chbxAutoPreview.addActionListener(this);
		chbxAutoPreview.setActionCommand("autopreview");
		chbxAutoPreview.setToolTipText(I18N
				.getGUILabelText("manager.chbxAutoPreview.ttp"));

		return chbxAutoPreview;
	}

	/**
	 * This method initializes chbxTogglePreview
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox createChbxTogglePreview() {
		chbxTogglePreview = new JCheckBox();
		chbxTogglePreview.setText(I18N
				.getGUILabelText("manager.chbxTogglePreview.text"));
		chbxTogglePreview.setSelected(false);
		chbxTogglePreview.addActionListener(this);
		chbxTogglePreview.setActionCommand("togglepreview");
		chbxTogglePreview.setToolTipText(I18N
				.getGUILabelText("manager.chbxTogglePreview.ttp"));

		return chbxTogglePreview;
	}

	private void createButtonGroupMain() {
		buttGroupMain.add(radbutMainLeft);
		buttGroupMain.add(radbutMainRight);
	}

	/**
	 * This method initializes managerListLeft
	 * 
	 * @return javax.swing.JList
	 */
	private JList createManagerListLeft() {
		managerListLeft = new JList();
		if (managerModelLeft == null) {
			managerModelLeft = new DefaultListModel();
		}
		managerListLeft.setModel(managerModelLeft); // for initialization

		managerListLeft.setCellRenderer(new ManagerListCellRendererLeft());
		managerListLeft.setLayoutOrientation(JList.VERTICAL);
		managerListLeft.setAutoscrolls(true);
		managerListLeft.setVisibleRowCount(1);
		managerListLeft.addListSelectionListener(this);

		// TODO continue implementing custom ordering of list elements
		// managerListLeft.setTransferHandler(alHandlerLeft);
		// managerListLeft.setDragEnabled(true);

		managerListLeft.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				logger.debug(KeyEvent.getKeyText(evt.getKeyCode()));
				if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
					if (currMgrIdxs[0] != -1) {
						confirmDeletionOfItems(currMgrIdxs);
					}
				}
			}
		});

		// Mouse 2x Click
		managerListLeft.addMouseListener(this);

		return managerListLeft;
	}

	/**
	 * Shows a confirm dialog for deleting selected indices.
	 * 
	 * @param idxs
	 */
	public void confirmDeletionOfItems(int[] idxs) {
		int selection = DialogUtil.getInstance().showQuestionMessage(
				GUITools.getMainFrame(),
				I18N.getMessage("application.dialog.managerDelete.text",
						idxs.length));
		if (selection == IDialogUtil.YES_OPTION) {
			logger.debug("User selected >YES< on confirmation dialog for deleting manager item(s) "
					+ CommonTools.intArrayToString(idxs) + ".");

			removeSelectedItems(currMgrIdxs, Manager.getInstance()
					.getActiveListIndex());

		} else {
			logger.debug("User selected >NO< on confirmation dialog for deleting manager items. Items will not be deleted.");
		}
	}

	/**
	 * Remove selected indices from one of the two manager lists.
	 * 
	 * @param idxs
	 *            the indices to be deleted
	 * @param list
	 *            the integer identifier of the list, {@link #MANAGER_LIST_LEFT}
	 *            ={@value #MANAGER_LIST_LEFT} is the left list, or
	 *            {@link #MANAGER_LIST_RIGHT}={@value #MANAGER_LIST_RIGHT} if it
	 *            is the right list
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the list contains less items than specified for deletion
	 */
	public void removeSelectedItems(int[] idxs, int list) {
		int tankIndex;
		if (list == MANAGER_LIST_LEFT) {
			tankIndex = Manager.getInstance().getTankIndexInMainLeft();
		} else if (list == MANAGER_LIST_RIGHT) {
			tankIndex = Manager.getInstance().getTankIndexInMainRight();
		} else {
			throw new IllegalArgumentException(
					"The manager list identifiers must either be "
							+ MANAGER_LIST_LEFT + " (left) or "
							+ MANAGER_LIST_RIGHT + " (right)!");
		}

		// get the list of boxes
		List<IqmDataBox> currTankList = Tank.getInstance().getTankDataAt(
				tankIndex);

		// check whether the selected list contains all items requested to be
		// removed
		if (idxs.length > currTankList.size()) {
			throw new ArrayIndexOutOfBoundsException(
					"The indices you want to remove exceed the size of the current tank list!");
		}

		// remove the entire tank, if the lists are equal
		if (idxs.length == currTankList.size()) {
			Tank.getInstance().deleteTankItems(new int[] { tankIndex });
			return;
		} else {
			// remove the items from the tank stack
			List<IqmDataBox> toRemove = new ArrayList<IqmDataBox>(idxs.length);
			for (int idx : idxs) {
				toRemove.add(currTankList.get(idx));
			}
			currTankList.removeAll(toRemove);
			Tank.getInstance().update(currTankList, tankIndex);
			return;
		}
	}

	/**
	 * This method initializes managerListRight
	 * 
	 * @return javax.swing.JList
	 */
	private JList createManagerListRight() {
		managerListRight = new JList();
		if (managerModelRight == null) {
			managerModelRight = new DefaultListModel();
		}
		managerListRight.setModel(managerModelRight); // for initialization

		managerListRight.setCellRenderer(new ManagerListCellRendererRight());
		managerListRight.setLayoutOrientation(JList.VERTICAL);
		managerListRight.setVisibleRowCount(1);
		managerListRight.addListSelectionListener(this);

		// TODO continue implementing custom ordering of list elements
		// managerListRight.setTransferHandler(alHandlerRight);
		// managerListRight.setDragEnabled(true);

		managerListRight.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				logger.debug(KeyEvent.getKeyText(evt.getKeyCode()));
				if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
					if (currMgrIdxs[0] != -1) {
						confirmDeletionOfItems(currMgrIdxs);
					}
				}
			}
		});

		// for Mouse 2x Click
		managerListRight.addMouseListener(this);

		return managerListRight;
	}

	private JSlider createItemSlider() {
		itemSlider = new JSlider(Adjustable.HORIZONTAL, 0, 0, 0);
		itemSlider.setToolTipText(I18N
				.getGUILabelText("manager.scrBarItemNr.ttp"));
		itemSlider.setSnapToTicks(true);
		itemSlider.addChangeListener(this);
		return itemSlider;
	}

	/**
	 * This method toggles the preview between the processed and the original
	 * image (e.g. for comparison purposes).
	 * 
	 */
	@Override
	public void startTogglePreviewIfSelected() {
		if (isCheckBoxTogglePreviewSelected()) {
			logger.debug("Performing toggle preview");

			if (Tank.getInstance().getCurrentTankIqmDataBoxAt(
					Manager.getInstance().getCurrItemIndex()) == null) {
				int selection = DialogUtil
						.getInstance()
						.showDefaultQuestionMessage(
								I18N.getMessage("application.dialog.noImageOpen.text"));
				if (selection == IDialogUtil.YES_OPTION) {
					OpenImageDialog dlg = new OpenImageDialog();
					File[] files = dlg.showDialog();

					if (files == null || files.length < 1) {
						return;
					}

					Tank.getInstance().loadImagesFromHD(files);
				} else {
					BoardPanel.appendTextln(I18N.getMessage("tank.isEmpty"),
							caller);
				}
				this.chbxTogglePreview.setSelected(false);
			} else if (Manager.getInstance().getPreviewImage() == null) {
				DialogUtil.getInstance().showDefaultInfoMessage(
						I18N.getMessage("manager.preview.isEmpty"));
				this.chbxTogglePreview.setSelected(false);
			} else {
				try {
					// get the image at the selected manager index
					int idx = Manager.getInstance().getCurrItemIndex();
					IqmDataBox iqmDataBox1 = Tank.getInstance()
							.getCurrentTankIqmDataBoxAt(idx);

					// convert the content to a planar image
					PlanarImage pi1 = iqmDataBox1.getImage();
					PlanarImage pi2 = Manager.getInstance().getPreviewImage();

					// shut down executor service if it is running
					try {
						execToggle.shutdownNow();
						execToggle.awaitTermination(1, TimeUnit.SECONDS);
					} catch (NullPointerException npe) {
						logger.debug("Executor service 'execToggle' is currently not running");
					} catch (InterruptedException e) {
						logger.error("An error occurred: ", e);
					} finally {
						// eventually start the service as a thread
						logger.info("Creating new executor service 'execToggle'");
						execToggle = Executors.newSingleThreadExecutor();
						this.toggleStatusPanel.setVisible(true);
						execToggle.execute(new ToggleRunnable(pi1, pi2));
					}

				} catch (NullPointerException npe) {
					logger.error("Cannot create toggle service", npe);
				}
			}
		}
	}

	/**
	 * This method resets the toggle preview
	 * 
	 */
	@Override
	public void resetTogglePreviewIfRunning() {
		try {
			execToggle.shutdownNow();
			execToggle.awaitTermination(1, TimeUnit.SECONDS);
			chbxTogglePreview.setSelected(false);
		} catch (NullPointerException e) {
			logger.trace("There is currently no toggle service running.");
		} catch (InterruptedException e) {
			logger.warn("Toggle service has been interrupted. ", e);
		} finally {
			execToggle = null;
			this.toggleStatusPanel.setVisible(false);
		}
	}

	public void setManagerForIndex(int index) throws NullPointerException,
			Exception {
		logger.debug("SetManagerForIndex(" + index + ")");
		this.setManagerForIndex(new int[] { index }, index);
	}

	public void setManagerForIndex(int[] indices, int indexForDisplay)
			throws NullPointerException, Exception {
		// System.out.println(chbxAutoPreview.isSelected());
		// System.out.println(chbxTogglePreview.isSelected());
		logger.debug("indexForDisplay=" + indexForDisplay);
		logger.debug("this.indexForDisplay=" + this.indexForDisplay);

		this.resetTogglePreviewIfRunning();

		if (indices == null || indexForDisplay < 0 || indices.length == 0)
			return;

		if (isRadioButtonMainLeftSelected()) {
			int tankIndex = Manager.getInstance().getTankIndexInMainLeft();
			Tank.getInstance().setCurrIndex(tankIndex);
			logger.debug("Left Manager List is selected.");
		} else if (isRadioButtonMainRightSelected()) {
			int tankIndex = Manager.getInstance().getTankIndexInMainRight();
			Tank.getInstance().setCurrIndex(tankIndex);
			logger.debug("Right Manager List is selected.");
		}

		// ensure, that the item you selected is visible in the list
		// the last index of the selected indices must be smaller than the
		// maximum
		// index of the model, otherwise a selection is not possible
		if (indices[indices.length - 1] < managerModelLeft.getSize()) {
			managerListLeft.removeListSelectionListener(this);
			managerListLeft.setSelectedIndices(indices);
			managerListLeft.ensureIndexIsVisible(indexForDisplay);
			managerListLeft.addListSelectionListener(this);
		} else {
			// if the requested index for selection is out of the model's range
			// select all other indices or select just the last one
			managerListLeft.removeListSelectionListener(this);
			managerListLeft.setSelectedIndex(managerModelLeft.getSize() - 1);
			managerListLeft
					.ensureIndexIsVisible(managerModelLeft.getSize() - 1);
			managerListLeft.addListSelectionListener(this);
		}
		if (indices[indices.length - 1] < managerModelRight.getSize()) {
			managerListRight.removeListSelectionListener(this);
			managerListRight.setSelectedIndices(indices);
			managerListRight.ensureIndexIsVisible(indexForDisplay);
			managerListRight.addListSelectionListener(this);
		} else {
			managerListRight.removeListSelectionListener(this);
			managerListRight.setSelectedIndex(managerModelRight.getSize() - 1);
			managerListRight
					.ensureIndexIsVisible(managerModelRight.getSize() - 1);
			managerListRight.addListSelectionListener(this);
		}

		// perform additional updates
		IqmDataBox box = Tank.getInstance().getSelectedIqmDataBox();
		if (box == null) {
			box = Tank.getInstance().getCurrentTankIqmDataBoxAt(0);
			if (box == null) {
				this.startTogglePreviewIfSelected();
				return;
			}
		}

		// remember the currently selected indices in the panel
		this.currMgrIdxs = indices;
		logger.debug("SELECTED INDICES: "
				+ CommonTools.intArrayToString(this.currMgrIdxs));

		// display the clicked item
		switch (box.getDataType()) {
		case IMAGE:
			Manager.getInstance().displayItem(indexForDisplay);
			// reset the preview image, since we perform the calculation on a
			// different item
			Manager.getInstance().setPreviewImage(null);
			break;
		case PLOT:
			// set the manager for the plot indices
			Manager.getInstance().displayPlotItems(indices);
			break;
		case TABLE:
			// set the manager for the table indices
			Manager.getInstance().displayTableItems(indices);
			break;
		default:
			break;
		}

		// update the current operator GUI, if opened
		this.updateOperatorGUI();

		if (isCheckBoxAutoPreviewSelected()) {
			this.performAutoPreview();
		}
		this.startTogglePreviewIfSelected();
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		Object source = e.getSource();
		if (source instanceof JSlider) {
			if (source == itemSlider
					&& (!this.managerModelRight.isEmpty() || !this.managerModelLeft
							.isEmpty())) {
				try {
					// turn off auto preview
					this.chbxAutoPreview.setSelected(false);
					this.indexForDisplay = itemSlider.getValue();

					// update work packages
					int targetMgrIdx = this.updateWorkPackage(this.currMgrIdxs,
							new int[] { itemSlider.getValue() })[0];

					this.setManagerForIndex(targetMgrIdx);
				} catch (Exception ex) {
					logger.debug("An error occurred: ", ex);
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (Tank.getInstance().isEmpty())
			return;

		if (e.getSource() == managerListLeft) {
			if (managerModelLeft.isEmpty())
				return;
		}
		if (e.getSource() == managerListRight) {
			if (managerModelRight.isEmpty())
				return;
		}

		try {
			// only when selected Tank item is of TYPE_PLOT, TYPE_IMAGE or
			// TYPE_TABLE
			// multiple selection is allowed!
			switch (Tank.getInstance().getSelectedIqmDataBox().getDataType()) {
			case IMAGE:
			case PLOT:
			case TABLE:
				logger.debug("Multiple Interval Selection enabled on both manager lists.");
				managerListLeft
						.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				managerListRight
						.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				break;
			case CUSTOM:
			default:
				logger.debug("Single Selection enabled on both manager lists.");
				managerListLeft
						.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				managerListRight
						.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				break;
			}
		} catch (NullPointerException npe) {
			logger.debug("There is no data box selected: " + npe);
		}

		// at double left click on the same item, reload it
		if (e.getClickCount() == 2 && (e.getButton() == MouseEvent.BUTTON1)) {
			logger.debug("Double click detected.");
			try {
				// enable the correct tab for the clicked item
				this.setTabForDataBoxType(Tank.getInstance()
						.getSelectedIqmDataBox().getDataType());
			} catch (NullPointerException ignored) {
			}

			if (e.getSource() == managerListLeft) {
				// if click occurs in the same list on the same single item,
				// don't fire
				if (isRadioButtonMainLeftSelected()) {
					if (managerListLeft.locationToIndex(e.getPoint()) == managerListLeft
							.getSelectedIndex()) {

						this.valueChanged(new ListSelectionEvent(
								managerListLeft, indexForDisplay,
								Integer.MIN_VALUE, false));

						// re-set the highlighted tank cell
						Tank.getInstance().setCurrIndex(
								Tank.getInstance().getCurrIndex());
						logger.debug("Reloading the same item in the left list, returning.");
						return;
					}
				}
			}
			if (e.getSource() == managerListRight) {
				// if click occurs in the same list on the same single item,
				// don't fire
				if (isRadioButtonMainRightSelected()) {
					if (managerListRight.locationToIndex(e.getPoint()) == managerListRight
							.getSelectedIndex()) {

						this.valueChanged(new ListSelectionEvent(
								managerListRight, indexForDisplay,
								Integer.MIN_VALUE, false));

						// re-set the highlighted tank cell
						Tank.getInstance().setCurrIndex(
								Tank.getInstance().getCurrIndex());
						logger.debug("Reloading the same item in the right list, returning.");
						return;
					}
				}
			}
		}

		// single left click loads the new item to the display panel
		if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)) {

			try {
				// enable the correct tab for the clicked item
				this.setTabForDataBoxType(Tank.getInstance()
						.getSelectedIqmDataBox().getDataType());
			} catch (NullPointerException ignored) {
				logger.debug(ignored);
			}

			if (e.getSource() == managerListLeft) {
				// if click occurs in the same list on the same single item,
				// don't fire
				if (isRadioButtonMainLeftSelected()) {
					if (managerListLeft.locationToIndex(e.getPoint()) == managerListLeft
							.getSelectedIndex()) {

						// re-set the highlighted tank cell
						Tank.getInstance().setCurrIndex(
								Tank.getInstance().getCurrIndex());
						logger.debug("Nothing changed in the left list, returning.");
						return;
					}
				}
				// if the click occurs in the other list on a single item, fire
				else if (isRadioButtonMainRightSelected()) {
					indexForDisplay = managerListLeft.locationToIndex(e
							.getPoint());
					logger.debug("Clicked on index [" + indexForDisplay
							+ "] in the left manager list.");

					logger.debug("Right selected index="
							+ managerListRight.getSelectedIndex());
					logger.debug("LeftModelSize-1     ="
							+ (managerModelLeft.getSize() - 1)
							+ "-->"
							+ (managerListRight.getSelectedIndex() > managerModelLeft
									.getSize() - 1));

					radbutMainLeft.setSelected(true);
					if (indexForDisplay == managerListRight.getSelectedIndex()
							|| (indexForDisplay == managerModelLeft.getSize() - 1 && managerListRight
									.getSelectedIndex() > managerModelLeft
									.getSize() - 1)) {
						this.valueChanged(new ListSelectionEvent(
								managerListLeft, indexForDisplay,
								Integer.MIN_VALUE, false));
					} else if (managerListRight.getSelectedIndex() == -1) {
						this.valueChanged(new ListSelectionEvent(
								managerListLeft, indexForDisplay,
								Integer.MIN_VALUE, false));
					}
				}
				// in all other cases on this list, the ListSelectionEvent is
				// fired by the model.
			}
			if (e.getSource() == managerListRight) {
				// if click occurs in the same list on the same single item,
				// don't fire
				if (isRadioButtonMainRightSelected()) {
					if (managerListRight.locationToIndex(e.getPoint()) == managerListRight
							.getSelectedIndex()) {

						// re-set the highlighted tank cell
						Tank.getInstance().setCurrIndex(
								Tank.getInstance().getCurrIndex());
						logger.debug("Nothing changed in the right list, returning.");
						return;
					}
				} else if (isRadioButtonMainLeftSelected()) {
					indexForDisplay = managerListRight.locationToIndex(e
							.getPoint());
					logger.debug("Clicked on index [" + indexForDisplay
							+ "] in the right manager list.");

					logger.debug("Left selected index="
							+ managerListLeft.getSelectedIndex());
					logger.debug("RightModelSize-1   ="
							+ (managerModelRight.getSize() - 1)
							+ "-->"
							+ (managerListLeft.getSelectedIndex() < managerModelRight
									.getSize() - 1));

					radbutMainRight.setSelected(true);
					if (indexForDisplay == managerListLeft.getSelectedIndex()
							|| (indexForDisplay == managerModelRight.getSize() - 1 && managerListLeft
									.getSelectedIndex() > managerModelRight
									.getSize() - 1)) {
						this.valueChanged(new ListSelectionEvent(
								managerListRight, indexForDisplay,
								Integer.MIN_VALUE, false));
					} else if (managerListLeft.getSelectedIndex() == -1) {
						this.valueChanged(new ListSelectionEvent(
								managerListRight, indexForDisplay,
								Integer.MIN_VALUE, false));
					}
				}
			}
		}

		if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON3)) {
			if (e.getSource() == managerListLeft) {
				logger.debug("Deleting the entire left manager");
				Manager.getInstance().resetLeftModel();
			}
			if (e.getSource() == managerListRight) {
				logger.debug("Deleting the entire right manager");
				Manager.getInstance().resetRightModel();
			}
		}
	}

	/**
	 * This method updates the work package of any open operator.
	 * <p>
	 * The sources are validated by the custom operator validator and the source
	 * indices are updated on success. If the validation fails, the sources will
	 * not be altered and remain unchanged.
	 * 
	 * @param currMgrIdxs
	 *            all currently selected manager indices
	 * @param targetMgrIdxs
	 *            the target indices
	 * @return an integer array of indices - either the current manager indices
	 *         or the new ones
	 */
	public int[] updateWorkPackage(int[] currMgrIdxs, int[] targetMgrIdxs) {
		// TEMPORARY FIX OF TICKET #24:
		// https://sourceforge.net/p/iqm/tickets/24/
		try {
			switch (Tank.getInstance().getSelectedIqmDataBox().getDataType()) {
			case TABLE:
			case CUSTOM:
				return targetMgrIdxs;
			case EMPTY:
			case IMAGE:
			case PLOT:
			case UNSUPPORTED:
			default:
				break;
			}
		} catch (Exception e) {
			logger.trace("Not updating work package: ", e);
		}

		int[] newIndices = null;
//		try {
//			IExecutionProtocol protocol = ExecutionProxy.getCurrentInstance();
//			if (protocol != null) {
//				try {
//					// TODO Ticket #55 filed
////					// perform source update only, if the data type in the current
////					// manager matches the currently opened operator
////					String opType = OperatorType.operatorTypeAsString(protocol
////							.getOperatorDescriptor().getType());
////					try {
////						Tank tank = (Tank) Tank.getInstance();
////						IqmDataBox box = tank.getCurrentTankIqmDataBoxAt(indexForDisplay);
////						String dataType = DataType.dataTypeAsString(
////								box.getDataType());
////						
////						// TODO implement robust compatibility check
////						if (opType.contains(dataType.substring("TYPE_".length()))){
//							newIndices = protocol.updateSources(currMgrIdxs,
//									targetMgrIdxs);
////							System.out.println(CommonTools.intArrayToString(newIndices));
////						} else {
////							return targetMgrIdxs;
////						}
////					}catch (Exception ex){
////						System.out.println("ERROR UPDATING");
////						return targetMgrIdxs;
////					}
//				} catch (Exception e) {
//					DialogUtil.getInstance().showErrorMessage(
//							"GUI update from ManagerPanel failed!", e, true);
//					newIndices = currMgrIdxs;
//				}
//			} else {
//				newIndices = targetMgrIdxs;
//			}
//		} catch (NullPointerException ex) {
//			logger.error(ex);
//		}
		
		//This solves ticket #55  2017-04 AH
		IExecutionProtocol protocol = ExecutionProxy.getCurrentInstance();
		Tank tank = (Tank) Tank.getInstance();
		IqmDataBox box = tank.getCurrentTankIqmDataBoxAt(indexForDisplay);
		if ((protocol != null) && (box != null)){
			//perform source update only, if the data type in the current manager item matches the currently opened operator		
			//get type of operator input
			String opType = OperatorType.operatorTypeAsString(protocol.getOperatorDescriptor().getType());		
			//get type of data (tank) item
			String dataType = DataType.dataTypeAsString(box.getDataType());
			logger.debug("operator type: " + opType);
			logger.debug("data type type: " + dataType);
			if (dataType.equals("TYPE_" +opType)){
				newIndices = protocol.updateSources(currMgrIdxs, targetMgrIdxs);
				logger.debug("Operator type matches data (tank) item type: GUI has been updated");
			} else{	
				logger.debug("Operator type does not match data (tank) item type: GUI has not been updated");
				newIndices = targetMgrIdxs;
			}		
		} else {
			logger.debug("ExecutionProxy or IqmDataBox is null");
			newIndices = targetMgrIdxs;
		}		
		return newIndices;
	}

	/**
	 * This method updates any operator GUI, if it is opened.
	 */
	public void updateOperatorGUI() {
//		try {
//			IExecutionProtocol protocol = ExecutionProxy.getCurrentInstance();
//			if (protocol != null) {
//				try {
//					// perform update only, if the data type in the current
//					// manager matches
//					// the currently opened operator
//					String opType = OperatorType.operatorTypeAsString(protocol
//							.getOperatorDescriptor().getType());
//					String dataType = DataType.dataTypeAsString(Tank
//							.getInstance()
//							.getCurrentTankIqmDataBoxAt(indexForDisplay)
//							.getDataType());
//
//					// TODO implement robust compatibility check
//					if (opType.contains(dataType.substring("TYPE_".length()))){
//						protocol.updateGUI();
//					}
//
//				} catch (Exception e) {
//					DialogUtil.getInstance().showErrorMessage(
//							"GUI update from ManagerPanel failed!", e, true);
//				}
//			}
//		} catch (NullPointerException ex) {
//			logger.trace(ex);
//		}
		//This solves ticket #55  2017-04 AH
		IExecutionProtocol protocol = ExecutionProxy.getCurrentInstance();
		Tank tank = (Tank) Tank.getInstance();
		IqmDataBox box = tank.getCurrentTankIqmDataBoxAt(indexForDisplay);
		try {
			if ((protocol != null) && (box != null)){
				//perform source update only, if the data type in the current manager item matches the currently opened operator		
				//get type of operator input
				String opType = OperatorType.operatorTypeAsString(protocol.getOperatorDescriptor().getType());		
				//get type of data (tank) item
				String dataType = DataType.dataTypeAsString(box.getDataType());
				logger.debug("operator type: " + opType);
				logger.debug("data type type: " + dataType);
				if (dataType.equals("TYPE_" +opType)){
					protocol.updateGUI();
					logger.debug("Operator type matches data (tank) item type: GUI has been updated");
				} else{	
					logger.debug("Operator type does not match data (tank) item type: GUI has not been updated");	
				}		
			} else {
				logger.debug("ExecutionProxy or IqmDataBox is null");		
			}
		} catch (Exception e) {
			//e.printStackTrace();
			DialogUtil.getInstance().showErrorMessage("GUI update from ManagerPanel failed!", e, true);
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// logger.debug(e.getClass() + " --> " + e.getActionCommand());
		// System.err.println(e.getClass());

		// action event if the user selects one item from the manager list
		// LEFT LIST
		if ("buttmain".equals(e.getActionCommand())) {
			this.resetItemSlider();
			if (managerModelLeft.isEmpty()) {
				logger.debug("The left manager list is empty.");
				// disable inappropriate menu items
				ApplicationObserver.setInitialMenuActivation();
				updateWorkPackage(null, null);
				return;
			}

			int[] currMgrIdxs = this.currMgrIdxs;
			int[] targetMgrIdxs = managerListLeft.getSelectedIndices();

			targetMgrIdxs = this.updateWorkPackage(currMgrIdxs, targetMgrIdxs);

			// DEBUG OPTIONS
			System.out.print("All selected indices in the ActionEvent: "
					+ CommonTools.intArrayToString(targetMgrIdxs));

			if (indexForDisplay > managerModelLeft.getSize()) {
				indexForDisplay = managerModelLeft.getSize() - 1;
			}
			if (indexForDisplay == -1 && managerModelLeft.getSize() > 0) {
				indexForDisplay = 0;
			}

			try {
				this.setManagerForIndex(targetMgrIdxs, indexForDisplay);
			} catch (Exception ex) {
				logger.error("An error occurred: ", ex);
			}
		}

		// RIGHT LIST
		if ("buttmain2".equals(e.getActionCommand())) {
			this.resetItemSlider();
			if (managerModelRight.isEmpty()) {
				logger.debug("The right manager list is empty.");
				// disable inappropriate menu items
				ApplicationObserver.setInitialMenuActivation();
				updateWorkPackage(null, null);
				return;
			}

			int[] currMgrIdxs = this.currMgrIdxs;
			int[] targetMgrIdxs = managerListRight.getSelectedIndices();

			targetMgrIdxs = this.updateWorkPackage(currMgrIdxs, targetMgrIdxs);

			if (indexForDisplay >= managerModelRight.getSize()) {
				indexForDisplay = managerModelRight.getSize() - 1;
			}

			if (indexForDisplay == -1 && managerModelRight.getSize() > 0) {
				indexForDisplay = 0;
			}

			// DEBUG OPTIONS
			System.out.print("All selected indices in the ActionEvent: "
					+ CommonTools.intArrayToString(targetMgrIdxs));

			try {
				this.setManagerForIndex(targetMgrIdxs, indexForDisplay);
			} catch (Exception ex) {
				logger.error("An error occurred: ", ex);
			}
		}

		if ("checkmain".equals(e.getActionCommand())) { // scroll pane icons
			// turn on /off
			if (chbxMainLeft.isSelected()) {
				chbxMainLeft.setToolTipText(I18N
						.getGUILabelText("manager.showHideThumbs"));
			} else {
				chbxMainLeft.setToolTipText(I18N
						.getGUILabelText("manager.showHideThumbs"));
			}

			this.revalidate();

			// set temporarily selected, at least
			radbutMainLeft.setSelected(true);
			int idxTank = Manager.getInstance().getTankIndexInMainLeft();

			// render the list model
			Manager.getInstance().setNewIconsForTankIndex(idxTank);

			try {
				this.setManagerForIndex(this.currMgrIdxs, indexForDisplay);
			} catch (Exception ex) {
				logger.error("An error occurred: ", ex);
			}

			if (this.isRadioButtonMainLeftSelected()) { // set back to initial
				// selection state
				radbutMainLeft.setSelected(true);
			} else {
				radbutMainRight.setSelected(true);
			}
		}

		if ("checkmain2".equals(e.getActionCommand())) { // scroll pane icons
			// turn on /off
			if (chbxMainRight.isSelected()) {
				chbxMainRight.setToolTipText(I18N
						.getGUILabelText("manager.showHideThumbs"));
			} else {
				chbxMainRight.setToolTipText(I18N
						.getGUILabelText("manager.showHideThumbs"));
			}

			this.revalidate();

			// set tmporarily selected
			radbutMainRight.setSelected(true);

			int idxTank = Manager.getInstance().getTankIndexInMainRight();

			// render the list model
			Manager.getInstance().setNewIconsForTankIndex(idxTank);

			try {
				this.setManagerForIndex(this.currMgrIdxs, indexForDisplay);
			} catch (Exception ex) {
				logger.error("An error occurred: ", ex);
			}

			if (this.isRadioButtonMainRightSelected()) { // set back to initial
				// selection state
				radbutMainRight.setSelected(true);
			} else {
				radbutMainLeft.setSelected(true);
			}
		}

		if ("autopreview".equals(e.getActionCommand())) {
			// on click on auto preview the check box will be (1) selected
			// and then (2) the action event is fired
			if (isCheckBoxAutoPreviewSelected()) {
				// set the new preview image to the manager
				this.performAutoPreview();
			} else {
				Manager.getInstance().setPreviewImage(null);
				return;
			}
		}
		if ("togglepreview".equals(e.getActionCommand())) {
			if (isCheckBoxTogglePreviewSelected()) {
				this.startTogglePreviewIfSelected();
			} else {
				this.resetTogglePreviewIfRunning();
			}
		}
	}

	private void performAutoPreview() {
		IExecutionProtocol protocol = Application.getCurrentExecutionProtocol();
		if (protocol == null) {
			BoardPanel.appendTextln(
					I18N.getMessage("application.operator.isEmpty"), caller);
			chbxAutoPreview.setSelected(false);
			return;
		} else {

			// int selection = DialogUtil.getInstance()
			// .showDefaultWarnMessage(
			// "Current Manager Indices: "
			// + intArrayToString(currMgrIdxs)
			// + "\nIndexForDisplay: "
			// + indexForDisplay);
			// if (selection == IDialogUtil.YES_OPTION){
			// StackTraceElement[] st = Thread.currentThread().getStackTrace();
			// for (StackTraceElement s : st)
			// System.err.println(s);
			// call the preview method of the operator GUI using the currently
			// active sources
			protocol.executePreview(null, null);
			// }
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// logger.debug(e.getClass() + " --> " + e.getValueIsAdjusting());
		String source = (e.getSource() == managerListLeft ? "managerListLeft"
				: "managerListRight");
		logger.debug(source + " -- " + e.getClass() + " --> "
				+ e.getValueIsAdjusting());

		// listen to mouse click events on one of the lists
		// perform changes on GUI only if the value is not adjusting any more
		if (e.getValueIsAdjusting() == false) {

			int[] currMgrIdxs = null;// currently selected cells
			int[] targetMgrIdxs = null; // (target) cells that should be
										// selected

			// get all selected indices of the list
			currMgrIdxs = this.currMgrIdxs; // currently selected cells

			if (e.getSource() == managerListLeft) {
				if (managerModelLeft.isEmpty())
					return;
				// if the change event occurs on the same item don't alter the
				// list selection
				if (e.getFirstIndex() == e.getLastIndex()) {
					managerListLeft.setSelectedIndex(e.getFirstIndex());
				}
				// get the currently selected items
				// this might only contain one index, too
				targetMgrIdxs = managerListLeft.getSelectedIndices();
			}
			if (e.getSource() == managerListRight) {
				if (managerModelRight.isEmpty())
					return;
				// if the change event occurs on the same item don't alter the
				// list selection
				if (e.getFirstIndex() == e.getLastIndex()) {
					managerListRight.setSelectedIndex(e.getFirstIndex());
				}
				// get the currently selected items
				// this might only contain one index, too
				targetMgrIdxs = managerListRight.getSelectedIndices();
			}

			// update sources in the operator - if one is opened -
			// and validate them.
			// If validate yields true, continue and set the
			// manager items in the correct manager list
			// If validation fails, the protocol returns the same selection
			// of items as before and the manager list is not changed
			targetMgrIdxs = this.updateWorkPackage(currMgrIdxs, targetMgrIdxs);
			this.currMgrIdxs = targetMgrIdxs;

			String s = "";
			for (int i : targetMgrIdxs)
				s += i + " ";

			if (targetMgrIdxs.length > 1) {
				try {
					logger.debug("More items selected, at indices ["
							+ s.substring(0, s.length() - 1) + "]");
				} catch (Exception ex) {
					logger.error("", ex);
				}
				try {
					this.setManagerForIndex(targetMgrIdxs, indexForDisplay);
				} catch (Exception ex) {
					logger.error("An error occurred: ", ex);
				}
			} else {
				try {
					logger.debug("One item selected, at index ["
							+ s.substring(0, s.length() - 1) + "]");
				} catch (Exception ex) {
					logger.error("", ex);
				}
				try {
					this.indexForDisplay = targetMgrIdxs[0];
					this.setManagerForIndex(targetMgrIdxs[0]);
				} catch (Exception ex) {
					logger.error("An error occurred: ", ex);
				}
			}
		}
	}

	public void resetItemSlider() {
		getLabelItemNr().setText(
				I18N.getGUILabelText("manager.lblItemNr.text", 0, 0));

		// disable change listeners temporarily
		ChangeListener[] al = itemSlider.getChangeListeners();

		itemSlider.removeChangeListener(al[0]);
		itemSlider.setMinimum(0);
		itemSlider.setMaximum(0);
		itemSlider.setValue(0);
		itemSlider.addChangeListener(al[0]);
	}

	@Override
	public JLabel getLabelItemNr() {
		return lblItemNr;
	}

	@Override
	public JSlider getItemSlider() {
		return itemSlider;
	}

	/**
	 * @return the managerModelLeft
	 */
	@Override
	public DefaultListModel getManagerModelLeft() {
		return managerModelLeft;
	}

	/**
	 * @return the managerListLeft
	 */
	@Override
	public JList getManagerListLeft() {
		return managerListLeft;
	}

	/**
	 * @return the managerModelRight
	 */
	@Override
	public DefaultListModel getManagerModelRight() {
		return managerModelRight;
	}

	/**
	 * @return the managerListRight
	 */
	@Override
	public JList getManagerListRight() {
		return managerListRight;
	}

	/**
	 * @return the JRadioButtonMain
	 */
	@Override
	public JRadioButton getJRadioButtonMainLeft() {
		return radbutMainLeft;
	}

	/**
	 * @return the JRadioButtonMain2
	 */
	@Override
	public JRadioButton getJRadioButtonMainRight() {
		return radbutMainRight;
	}

	@Override
	public boolean isRadioButtonMainLeftSelected() {
		return radbutMainLeft.isSelected();
	}

	@Override
	public boolean isCheckBoxMainLeftSelected() {
		return chbxMainLeft.isSelected();
	}

	@Override
	public boolean isRadioButtonMainRightSelected() {
		return radbutMainRight.isSelected();
	}

	@Override
	public boolean isCheckBoxMainRightSelected() {
		return chbxMainRight.isSelected();
	}

	@Override
	public boolean isCheckBoxAutoPreviewSelected() {
		return chbxAutoPreview.isSelected();
	}

	@Override
	public boolean isCheckBoxTogglePreviewSelected() {
		return chbxTogglePreview.isSelected();
	}

	@Override
	public int[] getCurrMgrIdxs() {
		return currMgrIdxs;
	}

	@Override
	public void setCurrMgrIdxs(int[] currMgrIdxs) {
		this.currMgrIdxs = currMgrIdxs;
	}

	@Override
	public int getIndexForDisplay() {
		return indexForDisplay;
	}

	@Override
	public void setIndexForDisplay(int indexForDisplay) {
		this.indexForDisplay = indexForDisplay;
	}

	public void setSliderValues(int mgrIndex, int nItems) {
		int d = String.valueOf(nItems).length();

		lblItemNr.setText(I18N.getGUILabelText("manager.lblItemNr.text",
				CoreTools.getStringWithLeadingZeros(mgrIndex + 1, d), nItems));

		ChangeListener[] cl = itemSlider.getChangeListeners();
		itemSlider.removeChangeListener(cl[0]);
		itemSlider.setMinimum(0);
		itemSlider.setValue(mgrIndex);
		itemSlider.setMaximum(nItems - 1);
		itemSlider.addChangeListener(cl[0]);
	}

	public void setTabForDataBoxType(final DataType dataType) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				switch (dataType) {
				case IMAGE:
					GUITools.getMainFrame().getItemContent()
							.setSelectedIndex(0);
					break;

				case PLOT:
					GUITools.getMainFrame().getItemContent()
							.setSelectedIndex(1);
					break;

				case TABLE:
					GUITools.getMainFrame().getItemContent()
							.setSelectedIndex(2);
					break;

				case CUSTOM:
					GUITools.getMainFrame().getItemContent()
							.setSelectedIndex(3);
					break;

				default:
					break;
				}
			}
		});
	}

	public void showVirtualIconLeft() {
		mainControlsLeft.add(lblVirtualIconLeft, BorderLayout.CENTER);
		mainControlsLeft.validate();
		mainControlsLeft.repaint();
	}

	public void hideVirtualIconLeft() {
		mainControlsLeft.remove(lblVirtualIconLeft);
		mainControlsLeft.validate();
		mainControlsLeft.repaint();
	}

	public void showVirtualIconRight() {
		mainControlsRight.add(lblVirtualIconRight, BorderLayout.CENTER);
		mainControlsRight.validate();
		mainControlsRight.repaint();
	}

	public void hideVirtualIconRight() {
		mainControlsRight.remove(lblVirtualIconRight);
		mainControlsRight.validate();
		mainControlsRight.repaint();
	}

	public JButton getBtnExport() {
		return btnExport;
	}

	public JLabel getLblTankIndexLeft() {
		return lblTankIndexLeft;
	}

	public JLabel getLblTankIndexRight() {
		return lblTankIndexRight;
	}

	public JLabel getLblVirtualIconRight() {
		return lblVirtualIconRight;
	}

	public JLabel getLblVirtualIconLeft() {
		return lblVirtualIconLeft;
	}

	@Override
	public void setRadioButtonMainRight(boolean b) {
		this.radbutMainLeft.setSelected(true);
	}

	@Override
	public void setRadioButtonMainLeft(boolean b) {
		this.radbutMainRight.setSelected(true);
	}

	@Override
	public void selectAll() {
		// active list index
		if (isRadioButtonMainLeftSelected()) {
			int nItems = this.managerModelLeft.size();
			this.managerListLeft.setSelectionInterval(0, nItems - 1);
		} else {
			int nItems = this.managerModelRight.size();
			this.managerListRight.setSelectionInterval(0, nItems - 1);
		}
	}
}// END
