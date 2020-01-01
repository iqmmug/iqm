package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Plot Operator Bundle
 * File: PlotGUI_Math.java
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


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpMathDescriptor;

/**
 * @author Ahammer
 * @since  2012 12
 * @update 2014 12 changed buttons to JRadioButtons and added some TitledBorders
 */
public class PlotGUI_Math extends AbstractPlotOperatorGUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8050400556849306323L;

	private static final Logger logger = LogManager.getLogger(PlotGUI_Math.class);

	public static final int DIFF_Y_PLUS = 0;
	public static final int DIFF_Y_MINUS = 1;
	public static final int DIFF_Y_PLUSMINUS = 2;
	public static final int SUM = 3;

	public static final int DELTA_X_ACTUAL = 0;
	public static final int DELTA_X_UNITY = 1;
	
	private ParameterBlockIQM pb;

	private JPanel       jPanelMethod         = null;
	private ButtonGroup  buttGroupMethod      = null;
	private JRadioButton buttMethodHidden     = null;
	private JRadioButton buttDiff_Y_Plus      = null;
	private JRadioButton buttDiff_Y_Minus     = null;
	private JRadioButton buttDiff_Y_PlusMinus = null;
	private JRadioButton buttSum              = null;

	private JPanel       jPanelDeltaX       = null;
	private ButtonGroup  buttGroupDeltaX    = null;
	private JRadioButton buttDelta_X_Actual = null;
	private JRadioButton buttDelta_X_Unity  = null;

	public PlotGUI_Math() {
		logger.debug("Now initializing...");
		
		this.setOpName(new PlotOpMathDescriptor().getName());	
		this.initialize();	
		this.setTitle("Mathematics");
		this.getOpGUIContent().setLayout(new GridBagLayout());
		
		this.getOpGUIContent().add(getJPanelMethod(), getGridBagConstraints_Method());
		this.getOpGUIContent().add(getJPanelDeltaX(), getGridBagConstraints_DeltaX());
		
		this.pack();
	}

	private GridBagConstraints getGridBagConstraints_Method() {
		GridBagConstraints gbc_Method = new GridBagConstraints();
		gbc_Method.gridx = 0;
		gbc_Method.gridy = 0;
		gbc_Method.insets = new Insets(5, 0, 0, 0);
		gbc_Method.fill = GridBagConstraints.BOTH;							
																	
		return gbc_Method;
	}

	private GridBagConstraints getGridBagConstraints_DeltaX() {
		GridBagConstraints gbc_DeltaX = new GridBagConstraints();
		gbc_DeltaX.gridx = 0;
		gbc_DeltaX.gridy = 1;
		gbc_DeltaX.insets = new Insets(5, 0, 5, 0);
		gbc_DeltaX.fill = GridBagConstraints.BOTH;			
		return gbc_DeltaX;
	}
	
    //----------------------------------------------------------------------------------------
	@Override
	public void setParameterValuesToGUI() {
		this.pb = this.workPackage.getParameters();
		
		if (pb.getIntParameter("method") == DIFF_Y_PLUS)      buttDiff_Y_Plus.setSelected(true);
		if (pb.getIntParameter("method") == DIFF_Y_MINUS)     buttDiff_Y_Minus.setSelected(true);
		if (pb.getIntParameter("method") == DIFF_Y_PLUSMINUS) buttDiff_Y_PlusMinus.setSelected(true);
		if (pb.getIntParameter("method") == SUM)              buttSum.setSelected(true);
		

		if (pb.getIntParameter("optDeltaX") == DELTA_X_ACTUAL) buttDelta_X_Actual.setSelected(true);
		if (pb.getIntParameter("optDeltaX") == DELTA_X_UNITY)  buttDelta_X_Unity.setSelected(true);
	}

	@Override
	public void updateParameterBlock() {
		if (this.buttDiff_Y_Plus.isSelected())      pb.setParameter("method", PlotGUI_Math.DIFF_Y_PLUS);
		if (this.buttDiff_Y_Minus.isSelected())     pb.setParameter("method", PlotGUI_Math.DIFF_Y_MINUS);
		if (this.buttDiff_Y_PlusMinus.isSelected()) pb.setParameter("method", PlotGUI_Math.DIFF_Y_PLUSMINUS);
		if (this.buttSum.isSelected())              pb.setParameter("method", PlotGUI_Math.SUM);

		if (this.buttDelta_X_Actual.isSelected()) pb.setParameter("optDeltaX", PlotGUI_Math.DELTA_X_ACTUAL);
		if (this.buttDelta_X_Unity.isSelected())  pb.setParameter("optDeltaX", PlotGUI_Math.DELTA_X_UNITY);	
	}

	// ----------------------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Diff_Y_Plus
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDiff_Y_Plus() {
		if (buttDiff_Y_Plus == null) {
			buttDiff_Y_Plus = new JRadioButton();
			buttDiff_Y_Plus.setText("Diff_Y_Plus");
			buttDiff_Y_Plus.setToolTipText("calculates the difference using ((y+1)-y)/((x+1)-x)");
			buttDiff_Y_Plus.addActionListener(this);
			buttDiff_Y_Plus.setActionCommand("parameter");
		}
		return buttDiff_Y_Plus;
	}

	/**
	 * This method initializes the Option: Diff_Y_Minus
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDiff_Y_Minus() {
		if (buttDiff_Y_Minus == null) {
			buttDiff_Y_Minus = new JRadioButton();
			buttDiff_Y_Minus.setText("Diff_Y_Minus");
			buttDiff_Y_Minus.setToolTipText("calculates the difference using (y-(y-1))/(x-(x-1))");
			buttDiff_Y_Minus.addActionListener(this);
			buttDiff_Y_Minus.setActionCommand("parameter");
		}
		return buttDiff_Y_Minus;
	}

	/**
	 * This method initializes the Option: Diff_Y_PlusMinus
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDiff_Y_PlusMinus() {
		if (buttDiff_Y_PlusMinus == null) {
			buttDiff_Y_PlusMinus = new JRadioButton();
			buttDiff_Y_PlusMinus.setText("Diff_Y_PlusMinus");
			buttDiff_Y_PlusMinus.setToolTipText("calculates the difference using ((y+1)-(y-1))/((x+1)-(x-1))");
			buttDiff_Y_PlusMinus.addActionListener(this);
			buttDiff_Y_PlusMinus.setActionCommand("parameter");
			buttDiff_Y_PlusMinus.setSelected(false);
		}
		return buttDiff_Y_PlusMinus;
	}

	/**
	 * This method initializes the Option: Sum
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSum() {
		if (buttSum == null) {
			buttSum = new JRadioButton();
			buttSum.setText("Sum");
			buttSum.setToolTipText("calculates the sum (integral)");
			buttSum.addActionListener(this);
			buttSum.setActionCommand("parameter");
		}
		return buttSum;
	}

	/**
	 * This method initializes the Option: Hidden
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonMethodHidden() {
		if (buttMethodHidden == null) {
			buttMethodHidden = new JRadioButton();
			buttMethodHidden.setText("MethodHidden");
			buttMethodHidden.setVisible(false);
		}
		return buttMethodHidden;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelMethod() {
		jPanelMethod = new JPanel();
		jPanelMethod.setLayout(new BoxLayout(jPanelMethod, BoxLayout.Y_AXIS));
		//jPanelMethod.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		jPanelMethod.setBorder(new TitledBorder(null, "Calculation", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMethod.add(getJRadioButtonDiff_Y_Plus());
		jPanelMethod.add(getJRadioButtonDiff_Y_Minus());
		jPanelMethod.add(getJRadioButtonDiff_Y_PlusMinus());
		jPanelMethod.add(getJRadioButtonSum());
		jPanelMethod.add(getJRadioButtonMethodHidden());

		this.setButtonGroupMethod(); // Grouping of JRadioButtons
		return jPanelMethod;
	}

	private void setButtonGroupMethod() {
		buttGroupMethod = new ButtonGroup();
		buttGroupMethod.add(buttDiff_Y_Plus);
		buttGroupMethod.add(buttDiff_Y_Minus);
		buttGroupMethod.add(buttDiff_Y_PlusMinus);
		buttGroupMethod.add(buttSum);
		buttGroupMethod.add(buttMethodHidden);
	}

	// --------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: Delta_X_Actual
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDelta_X_Actual() {
		if (buttDelta_X_Actual == null) {
			buttDelta_X_Actual = new JRadioButton();
			buttDelta_X_Actual.setText("Actual");
			buttDelta_X_Actual.setToolTipText("calculation using actual x values of plot");
			buttDelta_X_Actual.addActionListener(this);
			buttDelta_X_Actual.setActionCommand("parameter");
			buttDelta_X_Actual.setSelected(true);
		}
		return buttDelta_X_Actual;
	}

	/**
	 * This method initializes the Option: Delta_X_Unity
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonDelta_X_Unity() {
		if (buttDelta_X_Unity == null) {
			buttDelta_X_Unity = new JRadioButton();
			buttDelta_X_Unity.setText("Unity");
			buttDelta_X_Unity.setToolTipText("calculation using delta X = 1");
			buttDelta_X_Unity.addActionListener(this);
			buttDelta_X_Unity.setActionCommand("parameter");
		}
		return buttDelta_X_Unity;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDeltaX() {
		jPanelDeltaX = new JPanel();
		//jPanelDeltaX.setLayout(new BoxLayout(jPanelDeltaX, BoxLayout.Y_AXIS));
		jPanelDeltaX.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelDeltaX.setBorder(new TitledBorder(null, "Delta X option", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelDeltaX.add(getJRadioButtonDelta_X_Actual());
		jPanelDeltaX.add(getJRadioButtonDelta_X_Unity());

		this.setButtonGroupDeltaX(); // Grouping of JRadioButtons
		return jPanelDeltaX;
	}

	private void setButtonGroupDeltaX() {
		buttGroupDeltaX = new ButtonGroup();
		buttGroupDeltaX.add(buttDelta_X_Actual);
		buttGroupDeltaX.add(buttDelta_X_Unity);
	}


	@Override
	public void update() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug(e.getActionCommand() + " event has been triggered.");
		if ("parameter".equals(e.getActionCommand())) {
			if (e.getSource() == buttDiff_Y_Plus) {
			}
			if (e.getSource() == buttDiff_Y_Minus) {
			}
			if (e.getSource() == buttDiff_Y_PlusMinus) {
			}
			this.updateParameterBlock();
		}

	}


}
