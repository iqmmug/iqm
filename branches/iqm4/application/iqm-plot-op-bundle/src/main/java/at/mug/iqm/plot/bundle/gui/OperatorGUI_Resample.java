package at.mug.iqm.plot.bundle.gui;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: OperatorGUI_Resample.java
 * 
 * $Id: OperatorGUI_Resample.java 649 2018-08-14 11:05:54Z iqmmug $
 * $HeadURL: https://svn.code.sf.net/p/iqm/code-0/branches/iqm4/application/iqm-img-op-bundle/src/main/java/at/mug/iqm/img/bundle/gui/OperatorGUI_Resample.java $
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2018 Helmut Ahammer, Philipp Kainz
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



import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import at.mug.iqm.api.operator.AbstractPlotOperatorGUI;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.plot.bundle.descriptors.PlotOpResampleDescriptor;

/**
 * @author Ahammer
 * @since  2018-11
 * @update 
 */
public class OperatorGUI_Resample extends AbstractPlotOperatorGUI implements
		ActionListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5161696780502561840L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(OperatorGUI_Resample.class);

	private ParameterBlockIQM pb = null;

	private float resampleFactor = 1.0f;
	

	private JRadioButton butt_10     = null;
	private JRadioButton butt_5 	 = null;
	private JRadioButton butt_2      = null; // "_" means "divide by"
	private JRadioButton buttX2      = null;
	private JRadioButton buttX5      = null;
	private JRadioButton buttX10     = null;
	private JPanel       jPanelMag   = null;
	private ButtonGroup  buttGroupMag = null;

	private JRadioButton buttNone      = null; // simple resampling
	private JRadioButton buttBL 	   = null; // bilinear
	private JRadioButton buttBC 	   = null; // bicubic
	private JRadioButton buttBC2       = null; // bicubic2
	private JPanel       jPanelIntP    = null;
	private ButtonGroup  buttGroupIntP = null;



	/**
	 * constructor
	 */
	public OperatorGUI_Resample() {
		logger.debug("Now initializing...");

		this.setOpName(new PlotOpResampleDescriptor().getName());

		this.initialize();

		this.setTitle("Resample");

		this.getOpGUIContent().setLayout(new GridBagLayout());

		this.getOpGUIContent().add(createJPanelMag(),       createGridBagConstraintsButtonGroupMag());
		this.getOpGUIContent().add(createJPanelIntP(),      createGridBagConstraintsButtonGroupIntP());

		this.pack();
	}

	/**
	 * This method sets the current parameter block The individual values of the
	 * GUI current ParameterBlock
	 */
	@Override
	public void updateParameterBlock() {
		pb.setParameter("ResampleFactor", resampleFactor);
		
		if (buttNone.isSelected()) pb.setParameter("Interpolation", PlotOpResampleDescriptor.INTERPOLATION_NONE);
		if (buttBL.isSelected()) pb.setParameter("Interpolation", PlotOpResampleDescriptor.INTERPOLATION_BILINEAR);
		if (buttBC.isSelected()) pb.setParameter("Interpolation", PlotOpResampleDescriptor.INTERPOLATION_BICUBIC);
		if (buttBC2.isSelected())pb.setParameter("Interpolation", PlotOpResampleDescriptor.INTERPOLATION_BICUBIC2);	
	}

	/**
	 * This method sets the current parameter values
	 */
	@Override
	public void setParameterValuesToGUI() {

		this.pb = this.workPackage.getParameters();

		resampleFactor = pb.getFloatParameter("ResampleFactor");

		if (Math.round(1.0f / resampleFactor) == 10.0f) butt_10.setSelected(true); // ensures button appearance
		if (Math.round(1.0f / resampleFactor) == 5.0f)  butt_5.setSelected(true); // ensures button appearance
		if (resampleFactor == (1.0f / 2.0f))            butt_2.setSelected(true);
		if (resampleFactor == 2.0f)                     buttX2.setSelected(true);
		if (resampleFactor == 5.0f)                     buttX5.setSelected(true);
		if (resampleFactor == 10.0f)                    buttX10.setSelected(true);
		
		if (pb.getIntParameter("Interpolation") == PlotOpResampleDescriptor.INTERPOLATION_NONE)     buttNone.setSelected(true);
		if (pb.getIntParameter("Interpolation") == PlotOpResampleDescriptor.INTERPOLATION_BILINEAR) buttBL.setSelected(true);
		if (pb.getIntParameter("Interpolation") == PlotOpResampleDescriptor.INTERPOLATION_BICUBIC)  buttBC.setSelected(true);
		if (pb.getIntParameter("Interpolation") == PlotOpResampleDescriptor.INTERPOLATION_BICUBIC2) buttBC2.setSelected(true);


	}

	/**
	 * This method updates the GUI if needed This method overrides IqmOpJFrame
	 */
	@Override
	public void update() {
		logger.debug("Updating GUI...");
		this.updateParameterBlock();
		this.setParameterValuesToGUI();
	}


	private GridBagConstraints createGridBagConstraintsButtonGroupMag() {
		GridBagConstraints gridBagConstraintsButtonMagGroup = new GridBagConstraints();
		gridBagConstraintsButtonMagGroup.gridx = 0;
		gridBagConstraintsButtonMagGroup.gridy = 0;
		// gridBagConstraintsButtonMagGroup.gridwidth = 3;//?
		gridBagConstraintsButtonMagGroup.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		gridBagConstraintsButtonMagGroup.fill = GridBagConstraints.BOTH;
		return gridBagConstraintsButtonMagGroup;
	}

	private GridBagConstraints createGridBagConstraintsButtonGroupIntP() {
		GridBagConstraints gridBagConstraintsButtonIntPGroup = new GridBagConstraints();
		gridBagConstraintsButtonIntPGroup.gridx = 0;
		gridBagConstraintsButtonIntPGroup.gridy = 1;
		gridBagConstraintsButtonIntPGroup.insets = new Insets(5, 0, 0, 0); // top left  bottom  right
		//gridBagConstraintsButtonIntPGroup.fill = GridBagConstraints.BOTH;
		//gridBagConstraintsButtonIntPGroup.anchor = GridBagConstraints.NORTH;
		return gridBagConstraintsButtonIntPGroup;
	}



	// ------------------------------------------------------------------------------------------------------

	/**
	 * This method initializes the Option: /10
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButt_10() {
		butt_10 = new JRadioButton();
		butt_10.setText("/10");
		butt_10.setToolTipText("Downsamples to 1/10");
		butt_10.addActionListener(this);
		butt_10.setActionCommand("parameter");
		return butt_10;
	}

	/**
	 * This method initializes the Option: /5
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButt_5() {
		butt_5 = new JRadioButton();
		butt_5.setText("/5");
		butt_5.setToolTipText("Downsamples to 1/5");
		butt_5.addActionListener(this);
		butt_5.setActionCommand("parameter");
		return butt_5;
	}

	/**
	 * This method initializes the Option: /2
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButt_2() {
		butt_2 = new JRadioButton();
		butt_2.setText("/2");
		butt_2.setToolTipText("Downsamples to 1/2");
		butt_2.addActionListener(this);
		butt_2.setActionCommand("parameter");
		return butt_2;
	}

	/**
	 * This method initializes the Option: 2 times
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtX2() {
		buttX2 = new JRadioButton();
		buttX2.setText("x2");
		buttX2.setToolTipText("Upsamples 2 times");
		buttX2.addActionListener(this);
		buttX2.setActionCommand("parameter");
		return buttX2;
	}

	/**
	 * This method initializes the Option: 5 times
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtX5() {
		buttX5 = new JRadioButton();
		buttX5.setText("x5");
		buttX5.setToolTipText("Upsamples 5 times");
		buttX5.addActionListener(this);
		buttX5.setActionCommand("parameter");
		return buttX5;
	}

	/**
	 * This method initializes the Option: 10 times
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtX10() {
		buttX10 = new JRadioButton();
		buttX10.setText("x10");
		buttX10.setToolTipText("Upsamples 10 times");
		buttX10.addActionListener(this);
		buttX10.setActionCommand("parameter");
		return buttX10;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelMag() {
		jPanelMag = new JPanel();
		jPanelMag.setLayout(new BoxLayout(jPanelMag, BoxLayout.Y_AXIS));
		//jPanelMag.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelMag.setBorder(new TitledBorder(null, "Factor", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelMag.add(getJRadioButtonButt_10());
		jPanelMag.add(getJRadioButtonButt_5());
		jPanelMag.add(getJRadioButtonButt_2());
		jPanelMag.add(getJRadioButtonButtX2());
		jPanelMag.add(getJRadioButtonButtX5());
		jPanelMag.add(getJRadioButtonButtX10());
		// jPanelMag.addSeparator();
		this.setButtonGroupMag(); // Grouping of JRadioButtons
		return jPanelMag;
	}

	private void setButtonGroupMag() {
		buttGroupMag = new ButtonGroup();
		buttGroupMag.add(butt_10);
		buttGroupMag.add(butt_5);
		buttGroupMag.add(butt_2);
		buttGroupMag.add(buttX2);
		buttGroupMag.add(buttX5);
		buttGroupMag.add(buttX10);
	}

	// -------------------------------------------------------------------------------------------
	/**
	 * This method initializes the Option: None
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtNone() {
		buttNone = new JRadioButton();
		buttNone.setText("None");
		buttNone.setToolTipText("uses simple resampling");
		buttNone.addActionListener(this);
		buttNone.setActionCommand("parameter");
		return buttNone;
	}

	/**
	 * This method initializes the Option: Bilinear interpolation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtBL() {
		buttBL = new JRadioButton();
		buttBL.setText("Bilinear");
		buttBL.setToolTipText("uses Bilinear interpolation for resizing");
		buttBL.addActionListener(this);
		buttBL.setActionCommand("parameter");
		return buttBL;
	}

	/**
	 * This method initializes the Option: Bicubic interpolation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtBC() {
		buttBC = new JRadioButton();
		buttBC.setText("Bicubic");
		buttBC.setToolTipText("uses Bicubic interpoolation for resizing");
		buttBC.addActionListener(this);
		buttBC.setActionCommand("parameter");
		buttBC.setEnabled(false);
		return buttBC;
	}

	/**
	 * This method initializes the Option: Bicubic2 interpolation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonButtBC2() {
		buttBC2 = new JRadioButton();
		buttBC2.setText("Bicubic2");
		buttBC2.setToolTipText("uses Bicubic2 interpolation for resizing");
		buttBC2.addActionListener(this);
		buttBC2.setActionCommand("parameter");
		buttBC2.setEnabled(false);
		return buttBC2;
	}

	/**
	 * This method initializes JPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel createJPanelIntP() {
		jPanelIntP = new JPanel();
		jPanelIntP.setLayout(new BoxLayout(jPanelIntP, BoxLayout.Y_AXIS));
		//jPanelIntP.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		jPanelIntP.setBorder(new TitledBorder(null, "Interpolation", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		jPanelIntP.add(getJRadioButtonButtNone());
		jPanelIntP.add(getJRadioButtonButtBL());
		jPanelIntP.add(getJRadioButtonButtBC());
		jPanelIntP.add(getJRadioButtonButtBC2());
		// jPanelIntP.addSeparator();
		this.setButtonGroupIntP(); // Grouping of JRadioButtons
		return jPanelIntP;
	}

	private void setButtonGroupIntP() {
		buttGroupIntP = new ButtonGroup();
		buttGroupIntP.add(buttNone);
		buttGroupIntP.add(buttBL);
		buttGroupIntP.add(buttBC);
		buttGroupIntP.add(buttBC2);
	}

	// ----------------------------------------------------------------------------------------------------------

	

	// ----------------------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("parameter".equals(e.getActionCommand())) {

			if (butt_10 == e.getSource()) {
				resampleFactor = 1.0f / 10.0f;
				
			}
			if (butt_5 == e.getSource()) {
				resampleFactor = 1.0f / 5.0f;
				
			}
			if (butt_2 == e.getSource()) {
				resampleFactor = 1.0f / 2.0f;
				
			}
			if (buttX2 == e.getSource()) {
				resampleFactor = 2.0f;
				
			}
			if (buttX5 == e.getSource()) {
				resampleFactor = 5.0f;
				
			}
			if (buttX10 == e.getSource()) {
				resampleFactor = 10.0f;
				
			}
			this.update();
			//this.updateParameterBlock();
			//this.setParameterValuesToGUI();
		}

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

	// -------------------------------------------------------------------------------------------------
	@Override
	public void stateChanged(ChangeEvent e) {
		logger.debug("State has changed.");
	
	
		this.updateParameterBlock();
		this.setParameterValuesToGUI();

		// preview if selected
		if (this.isAutoPreviewSelected()) {
			logger.debug("Performing AutoPreview");
			this.showPreview();
		}

	}

}// END
