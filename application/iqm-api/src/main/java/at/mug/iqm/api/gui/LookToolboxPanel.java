package at.mug.iqm.api.gui;

/*
 * #%L
 * Project: IQM - API
 * File: LookToolboxPanel.java
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.I18N;
import at.mug.iqm.api.Resources;
import at.mug.iqm.api.gui.roi.events.handler.CanvasMover;

/**
 * This class holds the buttons for the look panel.
 * 
 * @author Philipp Kainz
 * 
 */
public class LookToolboxPanel extends JPanel implements ActionListener,
		KeyListener {

	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = 6498737623305895910L;

	/**
	 * The arrow button.
	 */
	private JToggleButton buttonArrow = null;
	/**
	 * The zoom button.
	 */
	private JToggleButton buttonZoom = null;
	/**
	 * The rectangle button.
	 */
	private JToggleButton buttonRectangle = null;
	/**
	 * The switch menu for the rectangle button.
	 */
	private SwitchMenu rectSwitchMenu = null;
	/**
	 * The single row button.
	 */
	private JToggleButton buttonSingleRow = null;
	/**
	 * The single column button.
	 */
	private JToggleButton buttonSingleColumn = null;

	/**
	 * The oval button.
	 */
	private JToggleButton buttonOval = null;
	/**
	 * The polygon button.
	 */
	private JToggleButton buttonPolygon = null;
	/**
	 * The free-hand button.
	 */
	private JToggleButton buttonFreehand = null;
	/**
	 * The line button.
	 */
	private JToggleButton buttonLine = null;
	/**
	 * The angle button.
	 */
	private JToggleButton buttonAngle = null;
	/**
	 * The point button.
	 */
	private JToggleButton buttonPoint = null;
	/**
	 * The edit ROI button.
	 */
	private JToggleButton buttonEditRoi = null;
	/**
	 * The drag canvas button.
	 */
	private JToggleButton buttonDragCanvas = null;

	/**
	 * The {@link ILookPanel} instance.
	 */
	private ILookPanel displayPanel = null;

	/**
	 * A reference to the {@link ILookPanel}'s {@link ContextPopupListener}
	 * instance.
	 */
	private ContextPopupListener popupListener = null;

	/**
	 * A canvas mover for the dragCanvas button.
	 */
	private CustomCanvasMover customCanvasMover = null;

	/**
	 * A flag, determining whether or not a key is currently pressed.
	 */
	private boolean isKeyPressed;

	/**
	 * Button group for the toolbox items.
	 */
	private ButtonGroup buttonGroup;

	/**
	 * A list of valid key codes
	 */
	private List<Integer> validKeyCodes = new Vector<Integer>();

	/**
	 * Default constructor.
	 */
	public LookToolboxPanel(final ILookPanel displayPanel) {
		super();
		this.setDisplayPanel(displayPanel);
		this.setPopupListener(displayPanel.getContextPopupListener());
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Application.getLook().setCurrentLookPanel(displayPanel);
			}
		});
		this.customCanvasMover = new CustomCanvasMover(displayPanel);

		// create valid keyCodes
		validKeyCodes.add(KeyEvent.VK_A);
		validKeyCodes.add(KeyEvent.VK_E);
		validKeyCodes.add(KeyEvent.VK_F);
		validKeyCodes.add(KeyEvent.VK_L);
		validKeyCodes.add(KeyEvent.VK_O);
		validKeyCodes.add(KeyEvent.VK_P);
		validKeyCodes.add(KeyEvent.VK_R);
		validKeyCodes.add(KeyEvent.VK_D);
		validKeyCodes.add(KeyEvent.VK_M);
		validKeyCodes.add(KeyEvent.VK_Z);
		validKeyCodes.add(KeyEvent.VK_ESCAPE);
		// this may delete the currently selected ROI shape
		validKeyCodes.add(KeyEvent.VK_DELETE);

		buttonGroup = new ButtonGroup();

		this.createButtons();
	}

	/**
	 * This method constructs the objects on the panel and initializes them.
	 */
	private void createButtons() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setMinimumSize(new Dimension(28, Integer.MAX_VALUE));
		this.setMaximumSize(new Dimension(28, Integer.MAX_VALUE));

		this.add(createButtonArrow(), 0);
		this.add(createButtonZoom(), 1);
		this.add(createButtonDragCanvas(), 2);

		// add the context menu for the 3 rectangle buttons
		rectSwitchMenu = new SwitchMenu();

		JMenuItem rectangleMenuItem = new JCheckBoxMenuItem();
		rectangleMenuItem.setText(I18N
				.getGUILabelText("look.button.rectangle.text"));
		// rectangleMenuItem.setToolTipText(I18N
		// .getGUILabelText("look.button.rectangle.ttp"));
		rectangleMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.rectangle.unselected")));
		// rectangleMenuItem.setSelectedIcon(new ImageIcon(Resources
		// .getImageURL("icon.look.button.rectangle.selected")));
		rectangleMenuItem.setSelected(true);
		rectangleMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// change the button
				remove(3);
				add(buttonRectangle, 3);
				activateRectangleLikeButton((DefaultDrawingLayer) displayPanel
						.getCurrentROILayer());
				revalidate();
			}
		});

		JMenuItem singleRowMenuItem = new JCheckBoxMenuItem();
		singleRowMenuItem.setText(I18N
				.getGUILabelText("look.button.singleRow.text"));
		// singleRowMenuItem.setToolTipText(I18N
		// .getGUILabelText("look.button.singleRow.ttp"));
		singleRowMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.singleRow.unselected")));
		// singleRowMenuItem.setSelectedIcon(new ImageIcon(Resources
		// .getImageURL("icon.look.button.singleRow.selected")));

		singleRowMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// change the button
				remove(3);
				add(buttonSingleRow, 3);
				activateRectangleLikeButton((DefaultDrawingLayer) displayPanel
						.getCurrentROILayer());
				revalidate();
			}
		});

		JMenuItem singleColMenuItem = new JCheckBoxMenuItem();
		singleColMenuItem.setText(I18N
				.getGUILabelText("look.button.singleCol.text"));
		// singleColMenuItem.setToolTipText(I18N
		// .getGUILabelText("look.button.singleCol.ttp"));
		singleColMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.singleCol.unselected")));
		// singleColMenuItem.setSelectedIcon(new ImageIcon(Resources
		// .getImageURL("icon.look.button.singleCol.selected")));

		singleColMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// change the button
				remove(3);
				add(buttonSingleColumn, 3);
				activateRectangleLikeButton((DefaultDrawingLayer) displayPanel
						.getCurrentROILayer());
				revalidate();
			}
		});

		ButtonGroup rectSwitchMenuGrp = new ButtonGroup();
		rectSwitchMenuGrp.add(rectangleMenuItem);
		rectSwitchMenuGrp.add(singleRowMenuItem);
		rectSwitchMenuGrp.add(singleColMenuItem);

		rectSwitchMenu.add(rectangleMenuItem);
		rectSwitchMenu.add(singleRowMenuItem);
		rectSwitchMenu.add(singleColMenuItem);

		this.add(createButtonRectangle(), 3);
		buttonRectangle.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					rectSwitchMenu.show(buttonRectangle, e.getX(), e.getY());
				}
			}
		});

		// create the buttons
		createButtonSingleRow();
		buttonSingleRow.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					rectSwitchMenu.show(buttonSingleRow, e.getX(), e.getY());
				}
			}
		});
		createButtonSingleColumn();
		buttonSingleColumn.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					rectSwitchMenu.show(buttonSingleColumn, e.getX(), e.getY());
				}
			}
		});

		this.add(createButtonOval(), 4);
		this.add(createButtonPolygon(), 5);
		this.add(createButtonFreehand(), 6);
		this.add(createButtonLine(), 7);
		this.add(createButtonAngle(), 8);

		this.add(createButtonPoint(), 9);
		this.add(createButtonEditRoi(), 10);

		//buttonArrow.setSelected(true);
		buttonZoom.setSelected(true);
		
		Component verticalGlue = Box.createVerticalGlue();
		add(verticalGlue);

	}

	/**
	 * This method initializes arrowButton
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonArrow() {
		buttonArrow = new JToggleButton();
		buttonArrow.setPreferredSize(new Dimension(26, 26));
		buttonArrow.setMaximumSize(new Dimension(26, 26));
		buttonArrow.setMinimumSize(new Dimension(26, 26));
		buttonArrow.setActionCommand("buttonArrow");
		buttonArrow.addActionListener(this);
		buttonArrow.setToolTipText(I18N
				.getGUILabelText("look.button.arrow.ttp"));
		buttonArrow.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.arrow.unselected")));
		buttonArrow.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.arrow.selected")));
		int sw = 1;
		buttonArrow.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw, sw));
		buttonArrow.setFocusPainted(false);

		buttonGroup.add(buttonArrow);

		return buttonArrow;
	}

	public JToggleButton getButtonArrow() {
		return buttonArrow;
	}

	public void setButtonArrow(JToggleButton buttonArrow) {
		this.buttonArrow = buttonArrow;
	}

	/**
	 * This method initializes buttonZoom
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonZoom() {
		buttonZoom = new JToggleButton();
		buttonZoom.setPreferredSize(new Dimension(26, 26));
		buttonZoom.setMaximumSize(new Dimension(26, 26));
		buttonZoom.setMinimumSize(new Dimension(26, 26));
		buttonZoom.setActionCommand("buttonZoom");
		buttonZoom.addActionListener(this);
		buttonZoom.setToolTipText(I18N.getGUILabelText("look.button.zoom.ttp"));
		buttonZoom.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.zoom.unselected")));
		buttonZoom.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.zoom.selected")));
		int sw = 1;
		buttonZoom.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw, sw));
		buttonZoom.setFocusPainted(false);

		buttonGroup.add(buttonZoom);

		return buttonZoom;
	}

	/**
	 * @return the buttonZoom
	 */
	public JToggleButton getButtonZoom() {
		return buttonZoom;
	}

	/**
	 * @param buttonZoom
	 *            the buttonZoom to set
	 */
	public void setButtonZoom(JToggleButton buttonZoom) {
		this.buttonZoom = buttonZoom;
	}

	/**
	 * This method initializes buttonRectangle
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonRectangle() {
		buttonRectangle = new JToggleButton();
		buttonRectangle.setPreferredSize(new Dimension(26, 26));
		buttonRectangle.setMaximumSize(new Dimension(26, 26));
		buttonRectangle.setMinimumSize(new Dimension(26, 26));
		buttonRectangle.setActionCommand("buttonRectangle");
		buttonRectangle.addActionListener(this);
		buttonRectangle.setToolTipText(I18N
				.getGUILabelText("look.button.rectangle.ttp"));
		buttonRectangle.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.rectangle.unselected")));
		buttonRectangle.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.rectangle.selected")));
		int sw = 1;
		buttonRectangle.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw,
				sw));
		buttonRectangle.setFocusPainted(false);

		buttonGroup.add(buttonRectangle);

		return buttonRectangle;
	}

	/**
	 * @return the buttonRectangle
	 */
	public JToggleButton getButtonRectangle() {
		return buttonRectangle;
	}

	/**
	 * @param buttonRectangle
	 *            the buttonRectangle to set
	 */
	public void setButtonRectangle(JToggleButton buttonRectangle) {
		this.buttonRectangle = buttonRectangle;
	}

	/**
	 * This method initializes buttonOval
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonOval() {
		buttonOval = new JToggleButton();
		buttonOval.setPreferredSize(new Dimension(26, 26));
		buttonOval.setMaximumSize(new Dimension(26, 26));
		buttonOval.setMinimumSize(new Dimension(26, 26));
		buttonOval.setActionCommand("buttonOval");
		buttonOval.addActionListener(this);
		buttonOval.setToolTipText(I18N.getGUILabelText("look.button.oval.ttp"));
		buttonOval.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.oval.unselected")));
		buttonOval.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.oval.selected")));
		int sw = 1;
		buttonOval.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw, sw));
		buttonOval.setFocusPainted(false);

		buttonGroup.add(buttonOval);

		return buttonOval;
	}

	/**
	 * @return the buttonOval
	 */
	public JToggleButton getButtonOval() {
		return buttonOval;
	}

	/**
	 * @param buttonOval
	 *            the buttonOval to set
	 */
	public void setButtonOval(JToggleButton buttonOval) {
		this.buttonOval = buttonOval;
	}

	/**
	 * This method initializes buttonPolygon
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonPolygon() {
		buttonPolygon = new JToggleButton();
		buttonPolygon.setPreferredSize(new Dimension(26, 26));
		buttonPolygon.setMaximumSize(new Dimension(26, 26));
		buttonPolygon.setMinimumSize(new Dimension(26, 26));
		buttonPolygon.setActionCommand("buttonPolygon");
		buttonPolygon.addActionListener(this);
		buttonPolygon.setToolTipText(I18N
				.getGUILabelText("look.button.polygon.ttp"));
		buttonPolygon.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.polygon.unselected")));
		buttonPolygon.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.polygon.selected")));
		int sw = 1;
		buttonPolygon
				.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw, sw));
		buttonPolygon.setFocusPainted(false);

		buttonGroup.add(buttonPolygon);

		return buttonPolygon;
	}

	/**
	 * @return the buttonPolygon
	 */
	public JToggleButton getButtonPolygon() {
		return buttonPolygon;
	}

	/**
	 * @param buttonPolygon
	 *            the buttonPolygon to set
	 */
	public void setButtonPolygon(JToggleButton buttonPolygon) {
		this.buttonPolygon = buttonPolygon;
	}

	/**
	 * This method initializes buttonFreehand
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonFreehand() {
		buttonFreehand = new JToggleButton();
		buttonFreehand.setPreferredSize(new Dimension(26, 26));
		buttonFreehand.setMaximumSize(new Dimension(26, 26));
		buttonFreehand.setMinimumSize(new Dimension(26, 26));
		buttonFreehand.setActionCommand("buttonFreehand");
		buttonFreehand.addActionListener(this);
		buttonFreehand.setToolTipText(I18N
				.getGUILabelText("look.button.freehand.ttp"));
		buttonFreehand.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.freehand.unselected")));
		buttonFreehand.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.freehand.selected")));
		int sw = 1;
		buttonFreehand.setBorder(BorderFactory
				.createEmptyBorder(sw, sw, sw, sw));
		buttonFreehand.setFocusPainted(false);

		buttonGroup.add(buttonFreehand);

		return buttonFreehand;
	}

	/**
	 * @return the buttonFreehand
	 */
	public JToggleButton getButtonFreehand() {
		return buttonFreehand;
	}

	/**
	 * @param buttonFreehand
	 *            the buttonFreehand to set
	 */
	public void setButtonFreehand(JToggleButton buttonFreehand) {
		this.buttonFreehand = buttonFreehand;
	}

	/**
	 * This method initializes buttonLine
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonLine() {
		buttonLine = new JToggleButton();
		buttonLine.setPreferredSize(new Dimension(26, 26));
		buttonLine.setMaximumSize(new Dimension(26, 26));
		buttonLine.setMinimumSize(new Dimension(26, 26));
		buttonLine.setActionCommand("buttonLine");
		buttonLine.addActionListener(this);
		buttonLine.setToolTipText(I18N.getGUILabelText("look.button.line.ttp"));
		buttonLine.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.line.unselected")));
		buttonLine.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.line.selected")));
		int sw = 1;
		buttonLine.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw, sw));
		buttonLine.setFocusPainted(false);

		buttonGroup.add(buttonLine);

		return buttonLine;
	}

	/**
	 * @return the buttonLine
	 */
	public JToggleButton getButtonLine() {
		return buttonLine;
	}

	/**
	 * @param buttonLine
	 *            the buttonLine to set
	 */
	public void setButtonLine(JToggleButton buttonLine) {
		this.buttonLine = buttonLine;
	}

	/**
	 * This method initializes buttonSingleRow
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonSingleRow() {
		buttonSingleRow = new JToggleButton();
		buttonSingleRow.setPreferredSize(new Dimension(26, 26));
		buttonSingleRow.setMaximumSize(new Dimension(26, 26));
		buttonSingleRow.setMinimumSize(new Dimension(26, 26));
		buttonSingleRow.setActionCommand("buttonSingleRow");
		buttonSingleRow.addActionListener(this);
		buttonSingleRow.setToolTipText(I18N
				.getGUILabelText("look.button.singleRow.ttp"));
		buttonSingleRow.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.singleRow.unselected")));
		buttonSingleRow.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.singleRow.selected")));
		int sw = 1;
		buttonSingleRow.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw,
				sw));
		buttonSingleRow.setFocusPainted(false);

		buttonGroup.add(buttonSingleRow);

		return buttonSingleRow;
	}

	/**
	 * This method initializes buttonSingleColumn
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonSingleColumn() {
		buttonSingleColumn = new JToggleButton();
		buttonSingleColumn.setPreferredSize(new Dimension(26, 26));
		buttonSingleColumn.setMaximumSize(new Dimension(26, 26));
		buttonSingleColumn.setMinimumSize(new Dimension(26, 26));
		buttonSingleColumn.setActionCommand("buttonSingleColumn");
		buttonSingleColumn.addActionListener(this);
		buttonSingleColumn.setToolTipText(I18N
				.getGUILabelText("look.button.singleCol.ttp"));
		buttonSingleColumn.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.singleCol.unselected")));
		buttonSingleColumn.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.singleCol.selected")));
		int sw = 1;
		buttonSingleColumn.setBorder(BorderFactory.createEmptyBorder(sw, sw,
				sw, sw));
		buttonSingleColumn.setFocusPainted(false);

		buttonGroup.add(buttonSingleColumn);

		return buttonSingleColumn;
	}

	public JToggleButton getButtonSingleColumn() {
		return buttonSingleColumn;
	}

	public JToggleButton getButtonSingleRow() {
		return buttonSingleRow;
	}

	/**
	 * This method initializes buttonAngle
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonAngle() {
		buttonAngle = new JToggleButton();
		buttonAngle.setPreferredSize(new Dimension(26, 26));
		buttonAngle.setMaximumSize(new Dimension(26, 26));
		buttonAngle.setMinimumSize(new Dimension(26, 26));
		buttonAngle.setActionCommand("buttonAngle");
		buttonAngle.addActionListener(this);
		buttonAngle.setToolTipText(I18N
				.getGUILabelText("look.button.angle.ttp"));
		buttonAngle.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.angle.unselected")));
		buttonAngle.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.angle.selected")));
		int sw = 1;
		buttonAngle.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw, sw));
		buttonAngle.setFocusPainted(false);

		buttonGroup.add(buttonAngle);

		return buttonAngle;
	}

	/**
	 * @return the buttonAngle
	 */
	public JToggleButton getButtonAngle() {
		return buttonAngle;
	}

	/**
	 * @param buttonAngle
	 *            the buttonAngle to set
	 */
	public void setButtonAngle(JToggleButton buttonAngle) {
		this.buttonAngle = buttonAngle;
	}

	/**
	 * This method initializes the point button.
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonPoint() {
		buttonPoint = new JToggleButton();
		buttonPoint.setPreferredSize(new Dimension(26, 26));
		buttonPoint.setMaximumSize(new Dimension(26, 26));
		buttonPoint.setMinimumSize(new Dimension(26, 26));
		buttonPoint.setActionCommand("buttonPoint");
		buttonPoint.addActionListener(this);
		buttonPoint.setToolTipText(I18N
				.getGUILabelText("look.button.point.ttp"));
		buttonPoint.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.point.unselected")));
		buttonPoint.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.point.selected")));
		int sw = 1;
		buttonPoint.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw, sw));
		buttonPoint.setFocusPainted(false);

		buttonGroup.add(buttonPoint);

		return buttonPoint;
	}

	public JToggleButton getButtonPoint() {
		return buttonPoint;
	}

	public void setButtonPoint(JToggleButton buttonPoint) {
		this.buttonPoint = buttonPoint;
	}

	/**
	 * This method initializes buttonEditRoi
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonEditRoi() {
		buttonEditRoi = new JToggleButton();
		buttonEditRoi.setPreferredSize(new Dimension(26, 26));
		buttonEditRoi.setMaximumSize(new Dimension(26, 26));
		buttonEditRoi.setMinimumSize(new Dimension(26, 26));
		buttonEditRoi.setActionCommand("buttonEditRoi");
		buttonEditRoi.addActionListener(this);
		buttonEditRoi.setToolTipText(I18N
				.getGUILabelText("look.button.editROI.ttp"));
		buttonEditRoi.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.editROI.unselected")));
		buttonEditRoi.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.editROI.selected")));
		int sw = 1;
		buttonEditRoi
				.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw, sw));
		buttonEditRoi.setFocusPainted(false);

		buttonGroup.add(buttonEditRoi);

		return buttonEditRoi;
	}

	/**
	 * @return the buttonEditRoi
	 */
	public JToggleButton getButtonEditRoi() {
		return buttonEditRoi;
	}

	/**
	 * @param buttonEditRoi
	 *            the buttonEditRoi to set
	 */
	public void setButtonEditRoi(JToggleButton buttonEditRoi) {
		this.buttonEditRoi = buttonEditRoi;
	}

	/**
	 * This method initializes buttonDragCanvas
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton createButtonDragCanvas() {
		buttonDragCanvas = new JToggleButton();
		buttonDragCanvas.setPreferredSize(new Dimension(26, 26));
		buttonDragCanvas.setMaximumSize(new Dimension(26, 26));
		buttonDragCanvas.setMinimumSize(new Dimension(26, 26));
		buttonDragCanvas.setActionCommand("buttonDragCanvas");
		buttonDragCanvas.addActionListener(this);
		buttonDragCanvas.setToolTipText(I18N
				.getGUILabelText("look.button.dragCanvas.ttp"));
		buttonDragCanvas.setIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.dragCanvas.unselected")));
		buttonDragCanvas.setSelectedIcon(new ImageIcon(Resources
				.getImageURL("icon.look.button.dragCanvas.selected")));
		int sw = 1;
		buttonDragCanvas.setBorder(BorderFactory.createEmptyBorder(sw, sw, sw,
				sw));
		buttonDragCanvas.setFocusPainted(false);

		buttonGroup.add(buttonDragCanvas);

		return buttonDragCanvas;
	}

	/**
	 * @return the buttonDragCanvas
	 */
	public JToggleButton getButtonDragCanvas() {
		return buttonDragCanvas;
	}

	/**
	 * @param buttonDragCanvas
	 *            the buttonDragCanvas to set
	 */
	public void setButtonDragCanvas(JToggleButton buttonDragCanvas) {
		this.buttonDragCanvas = buttonDragCanvas;
	}

	/**
	 * @return the displayPanel
	 */
	public ILookPanel getDisplayPanel() {
		return displayPanel;
	}

	/**
	 * @param displayPanel
	 *            the displayPanel to set
	 */
	public void setDisplayPanel(ILookPanel displayPanel) {
		this.displayPanel = displayPanel;
	}

	/**
	 * @return the popupListener
	 */
	public ContextPopupListener getPopupListener() {
		return popupListener;
	}

	/**
	 * @param popupListener
	 *            the popupListener to set
	 */
	public void setPopupListener(ContextPopupListener popupListener) {
		this.popupListener = popupListener;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				LookToolboxPanel item = new LookToolboxPanel(null);
				f.getContentPane().add(item);
				f.pack();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}

	public void activateArrowButton(DefaultDrawingLayer activeLayer) {
		buttonArrow.setSelected(true);

		// prevents the right mouse button from popping up the
		// context menu.
		disableEditPopupMenu(activeLayer);
		enableDefaultCanvasMovement(activeLayer);
		activeLayer.repaint();
	}

	public void activateZoomButton(DefaultDrawingLayer activeLayer) {
		buttonZoom.setSelected(true);

		// prevents the right mouse button from popping up the
		// context menu.
		disableEditPopupMenu(activeLayer);
		enableDefaultCanvasMovement(activeLayer);
		activeLayer.repaint();
	}

	public void activateDragCanvasButton(DefaultDrawingLayer activeLayer) {
		buttonDragCanvas.setSelected(true);
		// prevents the right mouse button from popping up the
		// context menu.
		disableEditPopupMenu(activeLayer);

		// let only the canvas mover and the ROI Cursor changer be the
		// active listeners
		enableCustomCanvasMovement(activeLayer);

		activeLayer.repaint();
	}

	public void activateLineButton(DefaultDrawingLayer activeLayer) {
		buttonLine.setSelected(true);
		// prevents the right mouse button from popping up the
		// context menu.
		disableEditPopupMenu(activeLayer);
		enableDefaultCanvasMovement(activeLayer);
		activeLayer.repaint();
	}

	public void activateRectangleLikeButton(DefaultDrawingLayer activeLayer) {
		// get the selected index of the context menu
		int selIdx = -1;
		for (int i = 0; i < rectSwitchMenu.getComponentCount(); i++) {
			if (rectSwitchMenu.getComponent(i) instanceof JMenuItem) {
				JMenuItem item = (JMenuItem) rectSwitchMenu.getComponent(i);
				if (item.isSelected()) {
					selIdx = i;
					break;
				}
			}
		}

		switch (selIdx) {
		case 0:
			buttonRectangle.setSelected(true);
			break;
		case 1:
			buttonSingleRow.setSelected(true);
			break;
		case 2:
			buttonSingleColumn.setSelected(true);
			break;
		default:
			break;
		}

		// prevents the right mouse button from popping up the
		// context menu.
		disableEditPopupMenu(activeLayer);
		enableDefaultCanvasMovement(activeLayer);
		activeLayer.repaint();
	}

	public void activateOvalButton(DefaultDrawingLayer activeLayer) {
		buttonOval.setSelected(true);

		// prevents the right mouse button from popping up the
		// context menu.
		disableEditPopupMenu(activeLayer);
		enableDefaultCanvasMovement(activeLayer);
		activeLayer.repaint();
	}

	public void activateAngleButton(DefaultDrawingLayer activeLayer) {
		buttonAngle.setSelected(true);

		// prevents the right mouse button from popping up the
		// context menu.
		disableEditPopupMenu(activeLayer);
		activeLayer.repaint();
	}

	public void activatePointButton(DefaultDrawingLayer activeLayer) {
		buttonPoint.setSelected(true);

		// prevents the right mouse button from popping up the
		// context menu.
		disableEditPopupMenu(activeLayer);
		enableDefaultCanvasMovement(activeLayer);
		activeLayer.repaint();
	}

	public void activatePolygonButton(DefaultDrawingLayer activeLayer) {
		buttonPolygon.setSelected(true);

		// prevents the right mouse button from popping up the
		// context menu.
		disableEditPopupMenu(activeLayer);
		enableDefaultCanvasMovement(activeLayer);
		activeLayer.repaint();
	}

	public void activateFreehandButton(DefaultDrawingLayer activeLayer) {
		buttonFreehand.setSelected(true);

		// prevents the right mouse button from popping up the
		// context menu.
		disableEditPopupMenu(activeLayer);
		enableDefaultCanvasMovement(activeLayer);
		activeLayer.repaint();
	}

	public void activateEditButton(DefaultDrawingLayer activeLayer) {
		buttonEditRoi.setSelected(true);
		/*
		 * The pop up is only shown, if the EDIT ROI button is activated.
		 */
		enableEditPopupMenu(activeLayer);
		enableDefaultCanvasMovement(activeLayer);
		activeLayer.repaint();
	}

	/**
	 * This method handles the exclusive toggling for the buttons of the tool
	 * bar.
	 * 
	 * @param e
	 *            the action event
	 * 
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println(e.getActionCommand());
		DefaultDrawingLayer activeLayer = (DefaultDrawingLayer) displayPanel
				.getCurrentROILayer();

		if ("buttonArrow".equals(e.getActionCommand())) {
			activateArrowButton(activeLayer);
		} else if ("buttonZoom".equals(e.getActionCommand())) {
			activateZoomButton(activeLayer);
		} else if ("buttonRectangle".equals(e.getActionCommand())) {
			activateRectangleLikeButton(activeLayer);
		} else if ("buttonOval".equals(e.getActionCommand())) {
			activateOvalButton(activeLayer);
		} else if ("buttonPolygon".equals(e.getActionCommand())) {
			activatePolygonButton(activeLayer);
		} else if ("buttonFreehand".equals(e.getActionCommand())) {
			activateFreehandButton(activeLayer);
		} else if ("buttonLine".equals(e.getActionCommand())) {
			activateLineButton(activeLayer);
		} else if ("buttonAngle".equals(e.getActionCommand())) {
			activateAngleButton(activeLayer);
		} else if ("buttonPoint".equals(e.getActionCommand())) {
			activatePointButton(activeLayer);
		} else if ("buttonEditRoi".equals(e.getActionCommand())) {
			activateEditButton(activeLayer);
		} else if ("buttonDragCanvas".equals(e.getActionCommand())) {
			activateDragCanvasButton(activeLayer);
		}

		// activate the layer
		activeLayer.requestFocusInWindow();
	}

	private void clearAllListeners(DefaultDrawingLayer activeLayer) {
		activeLayer.removeAllDefaultListeners();
		activeLayer.removeDefaultCanvasMover();
		activeLayer.removeMouseListener(customCanvasMover);
		activeLayer.removeMouseMotionListener(customCanvasMover);
		activeLayer.removeKeyListener(customCanvasMover);
	}

	private void disableEditPopupMenu(DefaultDrawingLayer activeLayer) {
		activeLayer.removeMouseListener(popupListener);
	}

	private void enableEditPopupMenu(DefaultDrawingLayer activeLayer) {
		activeLayer.removeMouseListener(popupListener);
		activeLayer.addMouseListener(popupListener);
	}

	private void enableCustomCanvasMovement(DefaultDrawingLayer activeLayer) {
		clearAllListeners(activeLayer);
		// re add the required listeners
		activeLayer.addMouseMotionListener(activeLayer.getCursorChanger());
		activeLayer.addMouseListener(customCanvasMover);
		activeLayer.addMouseMotionListener(customCanvasMover);
	}

	private void enableDefaultCanvasMovement(DefaultDrawingLayer activeLayer) {
		clearAllListeners(activeLayer);
		activeLayer.addAllDefaultListeners();
		activeLayer.addDefaultCanvasMover();
	}

	/**
	 * Inner class spoofing a pressed space bar for moving the canvas.
	 * 
	 * @author Philipp Kainz
	 */
	class CustomCanvasMover extends CanvasMover {

		public CustomCanvasMover(ILookPanel displayPanel) {
			super(displayPanel);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			setSpaceBarDown(true);
			super.mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			setSpaceBarDown(false);
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (!validKeyCodes.contains(keyCode))
			return;
		if (displayPanel.isEmpty())
			return;
		if (isKeyPressed)
			return;

		DefaultDrawingLayer activeLayer = (DefaultDrawingLayer) displayPanel
				.getCurrentROILayer();

		// delete the ROI only if the user selected the "edit" button
		if (buttonEditRoi.isSelected() && keyCode == KeyEvent.VK_DELETE) {
			activeLayer.deleteSelectedROI();
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			activateArrowButton(activeLayer);
		} else if (keyCode == KeyEvent.VK_E) {
			activateEditButton(activeLayer);
		} else if (keyCode == KeyEvent.VK_M) {
			activateDragCanvasButton(activeLayer);
		} else if (keyCode == KeyEvent.VK_Z) {
			activateZoomButton(activeLayer);
		} else if (keyCode == KeyEvent.VK_A) {
			activateAngleButton(activeLayer);
		} else if (keyCode == KeyEvent.VK_F) {
			activateFreehandButton(activeLayer);
		} else if (keyCode == KeyEvent.VK_L) {
			activateLineButton(activeLayer);
		} else if (keyCode == KeyEvent.VK_O) {
			activateOvalButton(activeLayer);
		} else if (keyCode == KeyEvent.VK_P) {
			activatePolygonButton(activeLayer);
		} else if (keyCode == KeyEvent.VK_D) {
			activatePointButton(activeLayer);
		} else if (keyCode == KeyEvent.VK_R) {
			int activeIndex = -1;
			if (buttonRectangle.isSelected()) {
				activeIndex = 0;
			} else if (buttonSingleRow.isSelected()) {
				activeIndex = 1;
			} else if (buttonSingleColumn.isSelected()) {
				activeIndex = 2;
			} else {
				activeIndex = -99;
			}

			// another button is selected
			if (activeIndex < 0) {
				activateButton(3);
			} else {
				// TODO switch through the buttons
				rectSwitchMenu.switchToElement(activeIndex + 1);
			}
		}
	}

	void activateButton(int idx) {
		((AbstractButton) getComponent(idx)).doClick();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!validKeyCodes.contains(e.getKeyCode()))
			return;
		if (displayPanel.isEmpty())
			return;
		isKeyPressed = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (!validKeyCodes.contains(e.getKeyCode()))
			return;
	}

	/**
	 * This nested class provides support for switching buttons associated with
	 * a default position on the button panel.
	 * 
	 * @author Philipp Kainz
	 * @since 3.1
	 */
	class SwitchMenu extends JPopupMenu {

		/**
		 * The UID for serialization.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Circles through menu items in this {@link JPopupMenu}.
		 * 
		 * @param idx
		 */
		public void switchToElement(int idx) {
			if (idx >= getComponentCount()) {
				idx = 0;
			}
			// get the currently displayed element
			((AbstractButton) this.getComponent(idx)).doClick();
		}

	}

	/**
	 * Returns the selected tool.
	 * 
	 * @return a {@link ButtonModel}
	 */
	public ButtonModel getSelectedTool() {
		return buttonGroup.getSelection();
	}
}
