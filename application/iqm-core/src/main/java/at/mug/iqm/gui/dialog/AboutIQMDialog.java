package at.mug.iqm.gui.dialog;

/*
 * #%L
 * Project: IQM - Application Core
 * File: AboutIQMDialog.java
 * 
 * $Id$
 * $HeadURL$
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


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.registry.OperatorRegistry;
import at.mug.iqm.gui.util.GUITools;


/**
 * This class represents the "About" dialog in the info menu.
 * @author Philipp Kainz
 *
 */
public class AboutIQMDialog extends JDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5253005190743054657L;

	// class specific logger
	private static final Logger logger = Logger.getLogger(AboutIQMDialog.class);

	private JPanel dialogContent = null;
	private JPanel textContent   = null;

	private ImageIcon iqmLogo	 = null;

	private JLabel 	title		 = null;
	private JLabel	version		 = null;
	private JLabel  license		 = null;
	private JLabel 	copyright	 = null;
	private JLabel 	coCopyright	 = null;
	private JLabel	contact		 = null;

	private JButton	btnAuthors  	 = null;
	private JButton	btnLibraries	 = null;
	private JButton	btnOperators	 = null;
	private JButton btnClose;

	/**
	 * Standard constructor.
	 */
	public AboutIQMDialog() {
		super(new JFrame(), I18N.getGUILabelText("application.dialog.about.title"));

		logger.debug("Initializing...");

		this.dialogContent = new JPanel();
		this.textContent   = new JPanel(new GridLayout(6, 1, 0, 2));
		
		this.iqmLogo   = new ImageIcon(Resources.getImageURL("img.application.about"));
		this.title     = new JLabel();
		this.version   = new JLabel();
		this.license   = new JLabel();
		this.copyright = new JLabel();
		this.coCopyright = new JLabel();
		this.contact   = new JLabel();

		this.btnAuthors   = new JButton();
		this.btnLibraries = new JButton();
		this.btnOperators = new JButton();
		this.btnClose = new JButton();

		this.createAndAssemble();
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		logger.debug("Done.");
	}

	/**
	 * Creates subcomponents and assembles the dialog.
	 */
	private void createAndAssemble() {
		logger.debug("Creating new 'About' dialog...");

		this.setLayout(new GridLayout());
		this.dialogContent.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		this.dialogContent.setLayout(new BorderLayout(25, 10));

		this.dialogContent.add(new JLabel(this.iqmLogo), BorderLayout.WEST);

		this.title.setText      (I18N.getMessage("application.about.name"));
		this.version.setText    (I18N.getMessage("application.about.version", IQMConstants.APPLICATION_VERSION));
		this.license.setText    (I18N.getMessage("application.about.license"));
		this.copyright.setText  (I18N.getMessage("application.copyright",   IQMConstants.CURRENTYEAR));
		this.coCopyright.setText(I18N.getMessage("application.coCopyright", IQMConstants.CURRENTYEAR));
		this.contact.setText    (I18N.getMessage("application.about.contact"));

		this.btnAuthors.setText(I18N.getGUILabelText("application.dialog.about.btnAuthors.text"));
		this.btnAuthors.addActionListener(this);
		this.btnAuthors.setActionCommand("authors");

		this.btnLibraries.setText(I18N.getGUILabelText("application.dialog.about.btnLibraries.text"));
		this.btnLibraries.addActionListener(this);
		this.btnLibraries.setActionCommand("libraries");
		
		this.btnOperators.setText(I18N.getGUILabelText("application.dialog.about.btnOperators.text"));
		this.btnOperators.addActionListener(this);
		this.btnOperators.setActionCommand("operators");
		
		this.btnClose.setText(I18N.getGUILabelText("application.dialog.generic.btnClose.text"));
		this.btnClose.addActionListener(this);
		this.btnClose.setActionCommand("close");

		this.textContent.add(this.title);
		this.textContent.add(this.version);
		this.textContent.add(this.license);
		this.textContent.add(this.copyright);
		this.textContent.add(this.coCopyright);
		this.textContent.add(this.contact);

		this.dialogContent.add(this.textContent, BorderLayout.CENTER);
		
		JPanel tmpPnl = new JPanel(new GridLayout(0,3));
		tmpPnl.add(this.btnAuthors);
		tmpPnl.add(this.btnLibraries);
		tmpPnl.add(this.btnOperators);
				
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BorderLayout());
		btnPanel.add(tmpPnl, BorderLayout.CENTER);
		btnPanel.add(new JPanel().add(this.btnClose), BorderLayout.SOUTH);
		
		this.dialogContent.add(btnPanel, BorderLayout.SOUTH);

		this.add(this.dialogContent);

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setPreferredSize(new Dimension(600, 200));
		this.setIconImage(new ImageIcon(Resources.getImageURL("icon.application.red.32x32")).getImage());
		this.setResizable(false);
		this.setLocationRelativeTo(GUITools.getMainFrame());
		this.validate();
		this.pack();
	}

	/** 
	 * Implements all actions for the dialog's behaviour.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("authors")){
			final JDialog d = new JDialog();
			d.setModal(true);
			d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			DefaultTableModel model = new DefaultTableModel();

			@SuppressWarnings("serial")
			JTable table = new JTable(){
				@Override
				public boolean isCellEditable(int rowIndex, int colIndex) {
					return false; 
				};
			};
			String[] _colIDs = new String[]{"Name", "Contribution"};
			model.setColumnIdentifiers(_colIDs);

			String[] data = new String[]{ 
					"Founder of IQM:","",
					"Helmut Ahammer", "Copyright holder of IQM",
					"","",	
					"Coauthor of IQM:","",
					"Philipp Kainz","Copyright holder of IQM",
					"","",
					"Substantial contributors:","",
					"Philipp Kainz","Major rework for IQM2 and IQM3", 
					"Philipp Waltl","Saving of GUI user settings",
					
					"","",		
					"Contributors:","",
					"Roland Lohr","Opening ASCII data",			
					"Jakob Hatzl","Image preview in open dialogs",
					"Michael Mayrhofer-R.","Memory handling, plot functionalities",				
					"Philip Peinsold, Andreas Dorn", "Region growing",
					"Gabriel Kleinoscheg", "Nexel scan",
					"Juergen Kleinowitz", "Otsu's method", 
					"Clemens Kaar", "Morphological operators - Flexible kernels",
					"Michael Mayrhofer-R.","Pyramid dimensions",
					"Adam Dolgos", "Peak and valley finder for plots",
					"","",
					"Plugins:","",
					"Martin Reiss", "3D Fractal dimensions",
					"Nikolaus Sabathiel", "2D Higuchi dimensions",
					"Juergen Kleinowitz", "ROI Learning Segmentation"
					
			};

			for (int i = 0; i < data.length-1; i=i+2){
				model.addRow(new String[]{data[i], data[i+1]});
			}

			JButton closeButton = new JButton(I18N.getGUILabelText("application.dialog.generic.btnClose.text"));
			closeButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					d.dispose();					
				}
			});
			JPanel pnl = new JPanel();
			pnl.add(closeButton);
						
			table.setModel(model);
			d.getContentPane().setLayout(new BorderLayout());
			d.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
			d.getContentPane().add(pnl, BorderLayout.SOUTH);
			d.setTitle(I18N.getGUILabelText("application.dialog.authors.title"));
			d.setIconImage(new ImageIcon(Resources.getImageURL("icon.application.red.32x32")).getImage());
			d.pack();
			d.setLocationRelativeTo(this);
			d.setVisible(true);
		}
		if (e.getActionCommand().equals("libraries")){
			final JDialog d = new JDialog();
			d.setModal(true);
			d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			DefaultTableModel model = new DefaultTableModel();

			@SuppressWarnings("serial")
			JTable table = new JTable(){
				@Override
				public boolean isCellEditable(int rowIndex, int colIndex) {
					return false; 
				};
			};
			String[] _colIDs = new String[]{"Name", "Copyright/License"};
			model.setColumnIdentifiers(_colIDs);

			String[] data = new String[]{ 
					"IQM", "Copyright (c) Helmut Ahammer, Philipp Kainz",
					"Java", "Copyright (c) 1995 Oracle Corporation",
					"JAI", "Copyright (c) 1999 Oracle Corporation",
					"ImageJ", "Copyright (c) 1999-2010 Wayne Rasband",
					"Color_Deconvolution 1.3", "Copyright (c) 2004-2010 Gabriel Landini",
					"bUnwarpJ", "Copyright (c) 2006-2010 Ignacio Arganda-Carreras",
					"TurboReg", "Copyright (c) Philipe Thevenaz",
					"UnwarpJ ", "Copyright (c) Carlos Oscar Sanchez Sorzano",
					"Image_Stabilizer", "Copyright (c) 2008-2010 Kang Li" ,
					"Watershed ", "Copyright (c) Daniel Sage",
					"flanagan", "Copyright (c) Michael Thomas Flanagan",
					"KMeansImageClustering", "Copyright (c) Rafael Duarte Coelho dos Santos",
					"FuzzyCMeansImageClustering", "Copyright (c) Rafael Duarte Coelho dos Santos",
					"TemplateMatching", "Copyright (c) Rafael Duarte Coelho dos Santos",
					"loci_tools", "Copyright (c) UW-Madison LOCI",
					"TCTool", "Copyright (c) Virtual Visions Software, Inc.",
					"Jai-tools", "Copyright (c) Michael Bedward",
					"log4j-1.2.16", "Copyright (c) 2007 The Apache Software Foundation",
					"commons-math3-3.0", "Copyright (c) 2001-2012 The Apache Software Foundation",
					"commons-codec", "Copyright (c) 2001-2012 The Apache Software Foundation",
					"tika", "Copyright (c) The Apache Software Foundation",
					"JFreeChart 1.0.14", "Copyright (c) David Gilbert"
			};

			for (int i = 0; i < data.length-1; i=i+2){
				model.addRow(new String[]{data[i], data[i+1]});
			}

			JButton closeButton = new JButton(I18N.getGUILabelText("application.dialog.generic.btnClose.text"));
			closeButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					d.dispose();					
				}
			});
			JPanel pnl = new JPanel();
			pnl.add(closeButton);
			
			table.setModel(model);
			d.getContentPane().setLayout(new BorderLayout());
			d.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
			d.getContentPane().add(pnl, BorderLayout.SOUTH);
			d.setTitle(I18N.getGUILabelText("application.dialog.libraries.title"));
			d.setIconImage(new ImageIcon(Resources.getImageURL("icon.application.red.32x32")).getImage());
			d.pack();
			d.setLocationRelativeTo(this);
			d.setVisible(true);
		}
		if (e.getActionCommand().equals("operators")){
			final JDialog d = new JDialog();
			d.setModal(true);
			d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			DefaultTableModel model = new DefaultTableModel();

			@SuppressWarnings("serial")
			JTable table = new JTable(){
				@Override
				public boolean isCellEditable(int rowIndex, int colIndex) {
					return false; 
				};
			};
			String[] _colIDs = new String[]{"ID", "Name"};
			model.setColumnIdentifiers(_colIDs);
			List<String> opList = OperatorRegistry.getInstance().getNames();

			for (int i = 0; i < opList.size(); i++){
				model.addRow(new String[]{String.valueOf((i+1)), opList.get(i)});
			}

			JButton closeButton = new JButton(I18N.getGUILabelText("application.dialog.generic.btnClose.text"));
			closeButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					d.dispose();					
				}
			});
			JPanel pnl = new JPanel();
			pnl.add(closeButton);
			
			table.setModel(model);
			d.getContentPane().setLayout(new BorderLayout());
			d.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
			d.getContentPane().add(pnl, BorderLayout.SOUTH);
			d.setTitle(I18N.getGUILabelText("application.dialog.availableOperators.title"));
			d.setIconImage(new ImageIcon(Resources.getImageURL("icon.application.red.32x32")).getImage());
			d.pack();
			d.setLocationRelativeTo(this);
			d.setVisible(true);
		}
		if (e.getActionCommand().equals("close")){
			this.dispose();
		}
	}

}
