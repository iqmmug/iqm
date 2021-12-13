package at.mug.iqm.api.gui.util;

/*
 * #%L
 * Project: IQM - API
 * File: ROILayerWidget.java
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.I18N;
import at.mug.iqm.api.gui.IDrawingLayer;
import at.mug.iqm.api.gui.ROILayerSelectorPanel;

/**
 * The {@link ROILayerWidget} represents a widget for the
 * {@link ROILayerSelectorPanel}. Each instance of this object is bound to a
 * {@link IDrawingLayer}.
 * 
 * @author Philipp Kainz
 * @since 3.0.1
 * 
 */
public class ROILayerWidget extends JPanel {
	/**
	 * The UID for serialization.
	 */
	private static final long serialVersionUID = -7608146986602767137L;

	/**
	 * A custom class logger.
	 */
	private static final Logger logger = LogManager.getLogger(ROILayerWidget.class);

	private JTextField txtName;
	private JCheckBox chckbxVisible;
	private JRadioButton rdbtnActive;
	private JLabel lblColor;
	private JPanel colorPanel;

	/**
	 * This is the layer which is maintained by this control element (widget).
	 */
	private IDrawingLayer layer;
	/**
	 * The container where every widget is added to.
	 */
	private JPanel content;
	/**
	 * A label where the current z-order of the layer is displayed.
	 */
	private JLabel lblZOrder;

	/**
	 * Create the panel.
	 */
	public ROILayerWidget() {
		setBorder(null);
		setLayout(new BorderLayout(0, 0));

		content = new JPanel();
		content.setBorder(new EmptyBorder(4, 2, 2, 2));
		add(content, BorderLayout.WEST);
		FlowLayout fl_content = new FlowLayout(FlowLayout.CENTER, 2, 1);
		content.setLayout(fl_content);

		rdbtnActive = new JRadioButton();
		rdbtnActive.setToolTipText(I18N
				.getGUILabelText("roi.layer.widget.select.ttp"));
		rdbtnActive.setSelected(true);
		content.add(rdbtnActive);
		rdbtnActive.setBorder(new EmptyBorder(0, 0, 0, 0));

		chckbxVisible = new JCheckBox();
		chckbxVisible.setToolTipText(I18N
				.getGUILabelText("roi.layer.widget.visible.hide.ttp"));
		chckbxVisible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisibility(chckbxVisible.isSelected());
			}
		});
		chckbxVisible.setSelected(true);
		content.add(chckbxVisible);
		chckbxVisible.setBorder(new EmptyBorder(2, 2, 2, 2));

		txtName = new JTextField();
		txtName.setBackground(getBackground());
		txtName.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				txtName.setBackground(Color.white);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				txtName.setBackground(getBackground());
			}
		});
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					String name = txtName.getText();

					if (name.equals("")) {
						name = "default";
					}
					logger.debug("ENTER: setting name to " + name);
					setLayerName(name);
				}
			}
		});
		Font f = new Font(txtName.getFont().getFontName(), Font.PLAIN, 10);
		txtName.setFont(f);
		txtName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				txtName.selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				String name = txtName.getText();
				// set a default name if the user deletes the entire text
				if (name.equals("")) {
					name = "default";
				}
				logger.debug("Focus lost: setting name to " + name);
				setLayerName(name);
			}
		});
		txtName.setBorder(new CompoundBorder(new MatteBorder(1, 1, 1, 1,
				(Color) new Color(255, 255, 255)), new EmptyBorder(2, 2, 2, 2)));
		content.add(txtName);
		txtName.setText("Layer");
		txtName.setColumns(8);

		colorPanel = new JPanel();
		colorPanel.setBorder(new CompoundBorder(new EmptyBorder(1, 1, 1, 1),
				new LineBorder(new Color(255, 255, 255))));
		content.add(colorPanel);
		colorPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));

		lblColor = new JLabel();
		colorPanel.add(lblColor);
		lblColor.setBorder(new EmptyBorder(0, 0, 0, 0));
		lblColor.setOpaque(true);
		lblColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				selectLayerColor();
			}
		});
		lblColor.setToolTipText(I18N
				.getGUILabelText("roi.layer.widget.color.ttp"));
		lblColor.setPreferredSize(new Dimension(15, 15));

		lblZOrder = new JLabel();
		lblZOrder.setIconTextGap(0);
		lblZOrder.setToolTipText(I18N
				.getGUILabelText("roi.layer.widget.zorder.ttp"));
		lblZOrder.setForeground(Color.DARK_GRAY);
		lblZOrder.setFont(new Font(lblZOrder.getFont().getFontName(),
				Font.PLAIN, 10));
		lblZOrder.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblZOrder.setPreferredSize(new Dimension(22, 15));
		content.add(lblZOrder);
	}

	/**
	 * A constructor to set the layer to be managed by this element directly.
	 * 
	 * @param layer
	 */
	public ROILayerWidget(IDrawingLayer layer) {
		this();
		this.setLayer(layer);
		setLayerColor(layer.getDefaultLayerColor());
	}

	/**
	 * Choose a layer color with a color chooser
	 */
	protected void selectLayerColor() {
		Color c = JColorChooser.showDialog(this,
				I18N.getGUILabelText("roi.layer.widget.color.chooser.title"),
				lblColor.getBackground());
		if (c != null) {
			lblColor.setBackground(c);
			layer.setLayerColor(c);
			lblColor.repaint();
			layer.update();
		}
	}

	/**
	 * Sets the layer's name entered by the user.
	 * 
	 * @param name
	 */
	public void setLayerName(String name) {
		this.txtName.setText(name);
		this.layer.setName(name);
	}

	/**
	 * Gets the layer's name entered by the user.
	 * 
	 * @return the name of the layer
	 */
	public String getLayerName() {
		return this.layer.getName();
	}

	/**
	 * Selects the layer and thus set it active.
	 */
	public void select() {
		this.rdbtnActive.setSelected(true);
		this.rdbtnActive.requestFocus();
		this.layer.setSelected(true);
		this.content.setBackground(Color.LIGHT_GRAY);
		this.chckbxVisible.setBackground(Color.LIGHT_GRAY);
		this.rdbtnActive.setBackground(Color.LIGHT_GRAY);
	}

	/**
	 * De-selects the layer and thus set it inactive.
	 */
	public void deselect() {
		this.rdbtnActive.setSelected(false);
		this.layer.setSelected(false);
		this.content.setBackground(getBackground());
		this.chckbxVisible.setBackground(getBackground());
		this.rdbtnActive.setBackground(getBackground());
	}

	/**
	 * Gets the current selection state of this element and layer.
	 * 
	 * @return <code>true</code> if selected, <code>false</code> otherwise
	 */
	public boolean isSelected() {
		return this.layer.isSelected();
	}

	/**
	 * Determine the visibility of the layer by setting a flag.
	 * 
	 * @param visibility
	 *            <code>true</code> if the layer should be rendered,
	 *            <code>false</code> otherwise
	 */
	public void setVisibility(boolean visibility) {
		logger.debug("Setting layer visibility of [" + this.layer + "] to ["
				+ visibility + "]");
		if (visibility == true) {
			chckbxVisible.setSelected(true);
			chckbxVisible.setToolTipText(I18N
					.getGUILabelText("roi.layer.widget.visible.hide.ttp"));
			layer.setLayerVisible(true);
		} else {
			chckbxVisible.setSelected(false);
			chckbxVisible.setToolTipText(I18N
					.getGUILabelText("roi.layer.widget.visible.show.ttp"));
			layer.setLayerVisible(false);
		}
		// draw the layer
		layer.update();
	}

	/**
	 * Gets the current visibility state of this layer.
	 * 
	 * @return <code>true</code> if the layer is rendered, <code>false</code>
	 *         otherwise
	 */
	public boolean isLayerVisible() {
		return this.layer.isLayerVisible();
	}

	/**
	 * Sets the color of the managed layer.
	 * 
	 * @param c
	 *            the color of the layer
	 */
	public void setLayerColor(Color c) {
		if (c == null)
			throw new IllegalArgumentException(
					"The layer's color must not be null!");
		this.lblColor.setBackground(c);
		this.layer.setLayerColor(c);
	}

	/**
	 * Gets the layer color of the managed layer.
	 * 
	 * @return the color of the managed layer
	 */
	public Color getLayerColor() {
		return this.layer.getLayerColor();
	}

	/**
	 * Sets the z-order of the element.
	 * 
	 * @param zOrder
	 *            the order
	 */
	public void setZOrder(int zOrder) {
		this.lblZOrder.setText(String.valueOf(zOrder));
		this.layer.setZOrder(zOrder);
	}

	/**
	 * Gets the z-order of the managed layer.
	 * 
	 * @return the z-order of the layer
	 */
	public int getZOrder() {
		return this.layer.getZOrder();
	}

	/**
	 * Get the managed layer instance.
	 * 
	 * @return the {@link IDrawingLayer}
	 */
	public IDrawingLayer getLayer() {
		return layer;
	}

	/**
	 * Set the {@link IDrawingLayer} to be managed by this control element.
	 * 
	 * @param layer
	 *            the layer
	 */
	public void setLayer(IDrawingLayer layer) {
		this.layer = layer;
	}

	/**
	 * Get the text field.
	 * 
	 * @return the text field
	 */
	public JTextField getTxtName() {
		return txtName;
	}

	/**
	 * Get the visibility check box.
	 * 
	 * @return the check box
	 */
	public JCheckBox getChckbxVisible() {
		return chckbxVisible;
	}

	/**
	 * Get the radio button for selecting the layer
	 * 
	 * @return the radio button
	 */
	public JRadioButton getRdbtnActive() {
		return rdbtnActive;
	}

	/**
	 * Get the label for choosing the color.
	 * 
	 * @return the label for color choosing
	 */
	public JLabel getLblColor() {
		return lblColor;
	}

	/**
	 * Get the label for the z-order.
	 * 
	 * @return the label for z-order
	 */
	public JLabel getLblZOrder() {
		return lblZOrder;
	}

	@Override
	public String toString() {
		String str = this.getClass().getName();
		str += "[layerName=" + getLayerName() + ", " + "isSelected="
				+ isSelected() + ", ";
		str += "isVisible=" + isLayerVisible() + ", ";
		str += "zOrder=" + getZOrder() + ", ";
		str += "layerColor=" + getLayerColor() + "]";
		return str;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().add(new ROILayerWidget());
				f.validate();
				f.pack();
				f.setVisible(true);
			}
		});
	}

}
