package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: FileMenu.java
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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IPlotPanel;
import at.mug.iqm.api.gui.ITablePanel;
import at.mug.iqm.api.gui.WaitingDialog;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.commons.gui.OpenImageDialog;
import at.mug.iqm.commons.gui.OpenPlotDialog;
import at.mug.iqm.commons.gui.OpenTableDialog;
import at.mug.iqm.commons.gui.QRSPeaksExtractorOpenDialog;
import at.mug.iqm.commons.gui.SVSImageExtractorOpenDialog;
import at.mug.iqm.commons.gui.SaveImageSequenceDialog;
import at.mug.iqm.commons.gui.SaveImageStackDialog;
import at.mug.iqm.commons.gui.SavePlotDataDialog;
import at.mug.iqm.commons.gui.SaveSingleImageDialog;
import at.mug.iqm.commons.gui.SaveTableDialog;
import at.mug.iqm.commons.gui.SaveTableSequDialog;
import at.mug.iqm.commons.io.ImageFileWriter;
import at.mug.iqm.commons.io.TableFileWriter;
import at.mug.iqm.commons.util.CompletionWaiter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.DynamicResourceLoader;
import at.mug.iqm.commons.util.OperatingSystem;
import at.mug.iqm.commons.util.plot.PlotParser;
import at.mug.iqm.commons.util.table.TableTools;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.workflow.Look;
import at.mug.iqm.core.workflow.Plot;
import at.mug.iqm.core.workflow.Table;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.gui.dialog.OpenImageHeaderDialog;
import at.mug.iqm.gui.util.GUITools;

/**
 * This is the base class for the file menu in IQM.
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @update HA 2018-08  added wav reading writing
 */
public class FileMenu extends DeactivatableMenu implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3340271210580280839L;
	// class specific logger
	private static final Logger logger = LogManager.getLogger(FileMenu.class);

	// class variable declaration
	private JMenuItem openMenuItem;
	private JMenuItem saveSingleMenuItem;
	private JMenuItem saveSequMenuItem;
	private JMenuItem saveStackMenuItem;
	private JMenuItem openHeaderMenuItem;
	private JMenuItem extractSVSMenuItem;
	private JMenuItem extractQRSPeaksMenuItem;
	private JMenuItem openPlotMenuItem;
	private JMenuItem openTableMenuItem;
	private JMenuItem savePlotDataMenuItem;
	private JMenuItem saveTableMenuItem;
	private JMenuItem saveTableSequMenuItem;
	private JMenuItem quitMenuItem;

	private JMenu openSamplesMenu;
	private JMenu openSamplesImagesMenu;
	private JMenu openSamplesPlotsMenu;
	private ArrayList<JMenuItem> sampleImageMenuItems;
	private ArrayList<JMenuItem> samplePlotsMenuItems;

	private RecentFilesMenu openRecentMenu;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped FileMenu.
	 */
	public FileMenu() {
		logger.debug("Generating new instance.");

		// initialize the variables
		this.openMenuItem = new JMenuItem();
		this.saveSingleMenuItem = new JMenuItem();
		this.saveSequMenuItem = new JMenuItem();
		this.saveStackMenuItem = new JMenuItem();
		this.openHeaderMenuItem = new JMenuItem();
		this.extractSVSMenuItem = new JMenuItem();
		this.extractQRSPeaksMenuItem = new JMenuItem();
		this.openPlotMenuItem = new JMenuItem();
		this.openTableMenuItem = new JMenuItem();
		this.saveTableMenuItem = new JMenuItem();
		this.savePlotDataMenuItem = new JMenuItem();
		this.saveTableSequMenuItem = new JMenuItem();
		this.quitMenuItem = new JMenuItem();

		this.openSamplesMenu = new JMenu();
		this.openSamplesImagesMenu = new JMenu();
		this.openSamplesPlotsMenu = new JMenu();

		this.sampleImageMenuItems = new ArrayList<JMenuItem>();
		this.samplePlotsMenuItems = new ArrayList<JMenuItem>();

		this.openRecentMenu = new RecentFilesMenu();

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Done.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items to a menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.file.text"));

		// assemble: add created elements to the JMenu
		this.add(this.createOpenMenuItem());
		this.add(this.createOpenPlotMenuItem());
		this.add(this.createOpenTableMenuItem());
		this.add(this.createOpenHeaderMenuItem());
		this.add(this.createOpenRecentMenu());
		this.addSeparator();
		this.add(this.createOpenSamplesMenu());
		this.addSeparator();
		this.add(this.createExtractSVSMenuItem());
		this.add(this.createExtractQRSPeaksMenuItem());
		this.addSeparator();
		this.add(this.createSaveSingleMenuItem());
		this.add(this.createSaveSequMenuItem());
		this.add(this.createSaveStackMenuItem());
		this.add(this.createSavePlotDataMenuItem());
		this.add(this.createSaveTableMenuItem());
		this.add(this.createSaveTableSequMenuItem());
		this.addSeparator();
		this.add(this.createQuitMenuItem());
	}

	/**
	 * This method initializes openMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createOpenMenuItem() {
		this.openMenuItem.setText(I18N.getGUILabelText("menu.file.open.text"));
		this.openMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.file.open")));
		this.openMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.file.open.ttp"));
		this.openMenuItem.addActionListener(this);
		this.openMenuItem.setActionCommand("open");
		this.openMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_O, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		return this.openMenuItem;
	}

	/**
	 * This method initializes saveSingleMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSaveSingleMenuItem() {
		this.saveSingleMenuItem.setText(I18N
				.getGUILabelText("menu.file.saveSingle.text"));
		this.saveSingleMenuItem.setToolTipText(I18N
				.getGUILabelText("menu.file.saveSingle.ttp"));
		this.saveSingleMenuItem.setIcon(new ImageIcon(Resources
				.getImageURL("icon.menu.file.saveSingle")));
		this.saveSingleMenuItem.addActionListener(this);
		this.saveSingleMenuItem.setActionCommand("savesingle");
		this.saveSingleMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		return this.saveSingleMenuItem;
	}

	/**
	 * This method initializes saveSequMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSaveSequMenuItem() {
		this.saveSequMenuItem.setText       (I18N.getGUILabelText("menu.file.saveSequ.text"));
		this.saveSequMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.saveSequ.ttp"));
		this.saveSequMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.saveSequ")));
		this.saveSequMenuItem.addActionListener(this);
		this.saveSequMenuItem.setActionCommand("savesequ");
		return this.saveSequMenuItem;
	}

	/**
	 * This method initializes saveStackMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSaveStackMenuItem() {
		this.saveStackMenuItem.setText(I18N.getGUILabelText("menu.file.saveStack.text"));
		this.saveStackMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.saveStack.ttp"));
		this.saveStackMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.saveStack")));
		this.saveStackMenuItem.addActionListener(this);
		this.saveStackMenuItem.setActionCommand("savestack");
		return this.saveStackMenuItem;
	}

	/**
	 * This method initializes savePlotDataMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSavePlotDataMenuItem() {
		this.savePlotDataMenuItem.setText(I18N.getGUILabelText("menu.file.savePlotData.text"));
		this.savePlotDataMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.savePlotData.ttp"));
		this.savePlotDataMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.savePlotData")));
		this.savePlotDataMenuItem.addActionListener(this);
		this.savePlotDataMenuItem.setActionCommand("saveplotdata");
		return this.savePlotDataMenuItem;
	}

	/**
	 * This method initializes openHeaderMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createOpenHeaderMenuItem() {
		this.openHeaderMenuItem.setText(I18N.getGUILabelText("menu.file.openHeader.text"));
		this.openHeaderMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.openHeader.ttp"));
		this.openHeaderMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.openHeader")));
		this.openHeaderMenuItem.addActionListener(this);
		this.openHeaderMenuItem.setActionCommand("openheader");
		return this.openHeaderMenuItem;
	}

	/**
	 * This method initializes ExtractSVSMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createExtractSVSMenuItem() {
		this.extractSVSMenuItem.setText(I18N.getGUILabelText("menu.file.extractSVS.text"));
		this.extractSVSMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.extractSVS.ttp"));
		this.extractSVSMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.extractSVS")));
		this.extractSVSMenuItem.addActionListener(this);
		this.extractSVSMenuItem.setActionCommand("extractsvs");
		return this.extractSVSMenuItem;
	}
	
	/**
	 * This method initializes ExtractQRSPeaksMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createExtractQRSPeaksMenuItem() {
		this.extractQRSPeaksMenuItem.setText(I18N.getGUILabelText("menu.file.extractQRSPeaks.text"));
		this.extractQRSPeaksMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.extractQRSPeaks.ttp"));
		this.extractQRSPeaksMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.extractSVS")));
		this.extractQRSPeaksMenuItem.addActionListener(this);
		this.extractQRSPeaksMenuItem.setActionCommand("extractqrspeaks");
		return this.extractQRSPeaksMenuItem;
	}

	/**
	 * This method initializes openPlotMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createOpenPlotMenuItem() {
		this.openPlotMenuItem.setText(I18N.getGUILabelText("menu.file.openPlot.text"));
		this.openPlotMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.openPlot.ttp"));
		this.openPlotMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.openPlot")));
		this.openPlotMenuItem.addActionListener(this);
		this.openPlotMenuItem.setActionCommand("openplot");
		return this.openPlotMenuItem;
	}

	/**
	 * This method initializes openTableMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createOpenTableMenuItem() {
		this.openTableMenuItem.setText(I18N.getGUILabelText("menu.file.openTable.text"));
		this.openTableMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.openTable.ttp"));
		this.openTableMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.openTable")));
		this.openTableMenuItem.addActionListener(this);
		this.openTableMenuItem.setActionCommand("opentable");
		return this.openTableMenuItem;
	}

	/**
	 * This method initializes saveTableMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSaveTableMenuItem() {
		this.saveTableMenuItem.setText(I18N.getGUILabelText("menu.file.saveTable.text"));
		this.saveTableMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.saveTable.ttp"));
		this.saveTableMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.saveTable")));
		this.saveTableMenuItem.addActionListener(this);
		this.saveTableMenuItem.setActionCommand("savetable");
		return this.saveTableMenuItem;
	}

	/**
	 * This method initializes saveTableSequMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSaveTableSequMenuItem() {
		this.saveTableSequMenuItem.setText(I18N.getGUILabelText("menu.file.saveTableSequ.text"));
		this.saveTableSequMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.saveTableSequ.ttp"));
		this.saveTableSequMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.saveTable")));
		this.saveTableSequMenuItem.addActionListener(this);
		this.saveTableSequMenuItem.setActionCommand("savetablesequ");
		return this.saveTableSequMenuItem;
	}

	private JMenu createOpenSamplesMenu() {
		this.openSamplesMenu.setText(I18N.getGUILabelText("menu.file.openSamples.text"));
		// this.openSamplesMenu.setToolTipText("");

		this.openSamplesMenu.add(this.createOpenSamplesImagesMenu());
		this.openSamplesMenu.add(this.createOpenSamplesPlotMenu());

		return this.openSamplesMenu;
	}

	private RecentFilesMenu createOpenRecentMenu() {
		this.openRecentMenu.setText(I18N.getGUILabelText("menu.file.openRecent.text"));

		this.openRecentMenu.createMenu();

		return this.openRecentMenu;
	}

	private JMenuItem createOpenSamplesPlotMenu() {
		this.openSamplesPlotsMenu.setText(I18N.getGUILabelText("menu.file.openSamples.plot.text"));
		// this.openSamplesPlotsMenu.setToolTipText("");

		// assemble the menu items in the menu
		Iterator<JMenuItem> iter = this.createSamplePlotMenuItems().iterator();

		while (iter.hasNext()) {
			this.openSamplesPlotsMenu.add(iter.next());
		}

		return this.openSamplesPlotsMenu;
	}

	private ArrayList<JMenuItem> createSamplePlotMenuItems() {

		try {
			// list all files and get their file names
			String[] fileNames = new DynamicResourceLoader().getResourceListing(DynamicResourceLoader.class, "samples/plot/");

			// sort the files according to the file name
			Vector<String> sortedNames = new Vector<String>(fileNames.length);
			sortedNames.addAll(Arrays.asList(fileNames));

			Collections.sort(sortedNames);

			for (String s : sortedNames) {
				// construct the JMenuItem objects
				final JMenuItem item = new JMenuItem();

				// set the name of the image
				item.setText(s);

				// add anonymous action listener, triggered on click
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// load the image to the tank/look panel
						logger.debug("Clicked on '" + item.getText() + "'");
						InputStream is = null;
						OutputStream out = null;
						try {
							is = DynamicResourceLoader.class.getResourceAsStream("/samples/plot/"+ item.getText());

							File targetDir = new File(ConfigManager
									.getCurrentInstance().getTempPath()
									.toString()
									+ File.separator
									+ "samples"
									+ File.separator + "plot");

							if (!targetDir.exists()) {
								targetDir.mkdirs();
							}

							// write the inputStream to a FileOutputStream
							out = new FileOutputStream(targetDir + File.separator + item.getText());

							int read = 0;
							byte[] bytes = new byte[1024];

							// write it to the temp directory
							while ((read = is.read(bytes)) != -1) {
								out.write(bytes, 0, read);
							}

							is.close();
							out.flush();
							out.close();

							File f = new File(targetDir + File.separator + item.getText());

							logger.debug("File exists? -> " + f.exists());

							// using a PlotParser to read file and show content
							// for selection of data:
							PlotParser pP = new PlotParser(f);
							WaitingDialog dialog = new WaitingDialog();
							pP.addPropertyChangeListener(new CompletionWaiter(dialog));
							pP.addPropertyChangeListener(GUITools.getStatusPanel());
							dialog.setVisible(true);

							pP.execute();

						} catch (IOException ex) {
							DialogUtil.getInstance().showDefaultErrorMessage(
									ex.getMessage());
						} finally {
							try {
								is.close();
								out.flush();
								out.close();
							} catch (IOException ignored) {
							} catch (NullPointerException ignored) {
							}
						}
					}
				});

				// add the menu item to the list
				this.samplePlotsMenuItems.add(item);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return this.samplePlotsMenuItems;
	}

	private JMenu createOpenSamplesImagesMenu() {
		this.openSamplesImagesMenu.setText(I18N.getGUILabelText("menu.file.openSamples.image.text"));
		// this.openSamplesImagesMenu.setToolTipText(I18N.getGUILabelText("menu.file.openSamples.image.ttp"));

		// assemble the menu items in the menu
		Iterator<JMenuItem> iter = this.createSampleImageMenuItems().iterator();

		while (iter.hasNext()) {
			this.openSamplesImagesMenu.add(iter.next());
		}

		return this.openSamplesImagesMenu;
	}

	private ArrayList<JMenuItem> createSampleImageMenuItems() {

		try {
			// list all files and get their file names
			String[] fileNames = new DynamicResourceLoader().getResourceListing(DynamicResourceLoader.class, "samples/image/");

			// sort the files according to the file name
			Vector<String> sortedNames = new Vector<String>(fileNames.length);
			sortedNames.addAll(Arrays.asList(fileNames));

			Collections.sort(sortedNames);

			for (String s : sortedNames) {
				// construct the JMenuItem objects
				final JMenuItem item = new JMenuItem();

				// set the name of the image
				item.setText(s);

				// add anonymous action listener, triggered on click
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// load the image to the tank/look panel
						logger.debug("Clicked on '" + item.getText() + "'");
						InputStream is = null;
						OutputStream out = null;
						try {
							is = DynamicResourceLoader.class.getResourceAsStream("/samples/image/" + item.getText());

							File targetDir = new File(ConfigManager
									.getCurrentInstance().getTempPath()
									.toString()
									+ File.separator
									+ "samples"
									+ File.separator + "image");

							if (!targetDir.exists()) {
								targetDir.mkdirs();
							}

							// write the inputStream to a FileOutputStream
							out = new FileOutputStream(targetDir + File.separator + item.getText());

							int read = 0;
							byte[] bytes = new byte[1024];

							// write it to the temp directory
							while ((read = is.read(bytes)) != -1) {
								out.write(bytes, 0, read);
							}

							is.close();
							out.flush();
							out.close();

							File f = new File(targetDir + File.separator + item.getText());

							logger.debug("File exists? -> " + f.exists());

							Tank.getInstance().loadImagesFromHD(
									new File[] { f });

						} catch (IOException ex) {
							logger.error(ex);
						} finally {
							try {
								is.close();
								out.flush();
								out.close();
							} catch (IOException ignored) {
							} catch (NullPointerException ignored) {
							}
						}

					}
				});

				// add the menu item to the list
				this.sampleImageMenuItems.add(item);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return this.sampleImageMenuItems;
	}

	/**
	 * This method initializes quitMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createQuitMenuItem() {
		this.quitMenuItem.setText(I18N.getGUILabelText("menu.file.quit.text"));
		this.quitMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.file.quit")));
		this.quitMenuItem.setToolTipText(I18N.getGUILabelText("menu.file.quit.ttp"));
		this.quitMenuItem.addActionListener(this);
		this.quitMenuItem.setActionCommand("quit");
		
		if (!OperatingSystem.isMac()) {
			this.quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_Q, Toolkit.getDefaultToolkit()
							.getMenuShortcutKeyMask()));
		}
		return this.quitMenuItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	/**
	 * This method sets and performs the corresponding actions to the menu
	 * items.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("open".equals(e.getActionCommand())) {
			logger.debug("Image opening dialog");
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// first fire up the open dialog for all supported image
					// formats
					OpenImageDialog dlg = new OpenImageDialog();
					File[] files = dlg.showDialog();

					if (files == null || files.length < 1) {
						return;
					}

					// log ALL loaded files anyway
					logger.info("Selected image(s) for loading: " + Arrays.asList(files).toString());

					Application.getTank().loadImagesFromHD(files);
				}
			});

		}
		if ("savesingle".equals(e.getActionCommand())) {
			logger.debug("Launching single image saving dialog.");
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (Look.getInstance().getCurrentLookPanel().isEmpty()) {
						BoardPanel.appendTextln(I18N.getMessage("application.noImageToSave"));
					} else {
						SaveSingleImageDialog dlg = new SaveSingleImageDialog();
						File target = dlg.showDialog();
						if (target == null) {
							return;
						}

						// get the encoding
						String encoding = dlg.getEncoding();

						// get the currently shown image
						PlanarImage image = Application.getLook().getCurrentImage();

						// write the file
						ImageFileWriter fw = new ImageFileWriter(target, image, encoding);
						fw.setWithROIs(dlg.drawROIs());
						Thread t = new Thread(fw);
						t.start();
					}
				}
			});

		}
		if ("savesequ".equals(e.getActionCommand())) {
			logger.debug("Image sequence saving dialog");
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// store the current tank index as sequence
					int index = Tank.getInstance().getCurrIndex();

					if (index == -1) {
						BoardPanel.appendTextln(I18N.getMessage("application.missingImage"));
						return;
					}

					// get the number of images to be stored as a sequence
					List<IqmDataBox> boxes = Tank.getInstance().getTankDataAt(
							index);

					// locate the target
					SaveImageSequenceDialog dlg = new SaveImageSequenceDialog();
					File targetFileName = dlg.showDialog();

					if (targetFileName == null) {
						return;
					}

					// write the files
					ImageFileWriter fw = new ImageFileWriter(targetFileName, boxes, dlg.getEncoding(), dlg.getExtension(), ImageFileWriter.MODE_SEQUENCE);
					fw.setWithROIs(dlg.drawROIs());
					Thread t = new Thread(fw);
					t.start();
				}
			});

		}
		if ("savestack".equals(e.getActionCommand())) {
			logger.debug("Image stack saving dialog");
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// store the current tank index as sequence
					int index = Tank.getInstance().getCurrIndex();

					if (index == -1) {
						BoardPanel.appendTextln(I18N.getMessage("application.missingImage"));
						return;
					}

					// get the number of images to be stored as a sequence
					List<IqmDataBox> boxes = Tank.getInstance().getTankDataAt(index);

					// locate the target
					SaveImageStackDialog dlg = new SaveImageStackDialog();
					File targetFileName = dlg.showDialog();

					if (targetFileName == null) {
						return;
					}

					// write the files
					ImageFileWriter fw = new ImageFileWriter(targetFileName,boxes, dlg.getEncoding(), dlg.getExtension(),ImageFileWriter.MODE_STACK);
					fw.setWithROIs(dlg.drawROIs());
					Thread t = new Thread(fw);
					t.start();
				}
			});

		}
		if ("openheader".equals(e.getActionCommand())) {
			logger.debug("Header opening dialog");
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new OpenImageHeaderDialog().run();
				}
			});

		}
		if ("extractsvs".equals(e.getActionCommand())) {
			logger.debug("Extract svs file(s)");
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new SVSImageExtractorOpenDialog().run();
				}
			});

		}
		if ("extractqrspeaks".equals(e.getActionCommand())) {
			logger.debug("Extract QRS peaks of a file");
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new QRSPeaksExtractorOpenDialog().run();
				}
			});

		}
		if ("openplot".equals(e.getActionCommand())) {
			logger.debug("Plot opening dialog");
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new OpenPlotDialog().run();
				}
			});

		}
		if ("opentable".equals(e.getActionCommand())) {
			logger.debug("Table opening dialog");
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new OpenTableDialog().run();
				}
			});

		}
		if ("savetable".equals(e.getActionCommand())) {
			logger.debug("Table saving dialog");
			if (Table.getInstance().getTablePanel().isEmpty())
				return;
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					SaveTableDialog dialog = new SaveTableDialog();
					File destination = dialog.showDialog();

					if (destination == null) {
						return;
					}

					// write the files
					String extension = dialog.getExtension();
					boolean exportModel = dialog.exportModel();

					ITablePanel tp = Application.getTable().getTablePanel();
					Object outputObject = null;

					if (extension.equals(IQMConstants.JTB_EXTENSION)) {
						outputObject = tp.getTableClone();
					} else {
						// gather selected data
						if (extension.equals(IQMConstants.CSV_EXTENSION)) {
							if (exportModel) {
								outputObject = TableTools.convertToCSV(tp.getTableModel());
							} else {
								outputObject = TableTools.convertToCSV(tp.getTableClone());
							}
						} else if (extension.equals(IQMConstants.DAT_EXTENSION)) {
							if (exportModel) {
								outputObject = TableTools.convertToTabDelimited(tp.getTableModel());
							} else {
								outputObject = TableTools.convertToTabDelimited(tp.getTableClone());
							}
						} else if (extension.equals(IQMConstants.TXT_EXTENSION)) {
							if (exportModel) {
								outputObject = TableTools.convertToTabDelimited(tp.getTableModel());
							} else {
								outputObject = TableTools.convertToTabDelimited(tp.getTableClone());
							}
						}
					}

					// write the file according to the content
					TableFileWriter tfw = new TableFileWriter(destination, outputObject, extension);
					tfw.run();
				}
			});

		}

		if ("saveplotdata".equals(e.getActionCommand())) {
			logger.debug("Plot data saving dialog");
			if (Plot.getInstance().isEmpty())
				return;
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					SavePlotDataDialog dialog = new SavePlotDataDialog();
					File destination = dialog.showDialog();

					if (destination == null) {
						return;
					}

					// write the files
					String extension = dialog.getExtension();
					boolean exportModel = dialog.exportModel();

					IPlotPanel pp = Application.getPlot().getPlotPanel();
					Object outputObject = null;

					if (extension.equals(IQMConstants.JTB_EXTENSION)) {
						outputObject = pp.exportTable();
					} else {
						// gather selected data
						if (extension.equals(IQMConstants.CSV_EXTENSION)) {
							if (exportModel) {
								outputObject = TableTools.convertToCSV(pp.getTableModel());
							} else {
								outputObject = TableTools.convertToCSV(pp.exportTable());
							}
						} else if (extension.equals(IQMConstants.DAT_EXTENSION)) {
							if (exportModel) {
								outputObject = TableTools.convertToTabDelimited(pp.getTableModel());
							} else {
								outputObject = TableTools
										.convertToTabDelimited(pp.exportTable());
							}
						} else if (extension.equals(IQMConstants.TXT_EXTENSION)) {
							if (exportModel) {
								outputObject = TableTools.convertToTabDelimited(pp.getTableModel());
							} else {
								outputObject = TableTools.convertToTabDelimited(pp.exportTable());
							}
						} else if (extension.equals(IQMConstants.WAV_EXTENSION)) {
							if (exportModel) {
								outputObject = pp.getTableModel();
							} else {
								outputObject = pp.exportTable().getModel();
							}
						}
						
					}

					// write the file according to the content
					TableFileWriter tfw = new TableFileWriter(destination, outputObject, extension);
					tfw.run();
				}
			});

		}
		if ("savetablesequ".equals(e.getActionCommand())) {
			logger.debug("Table sequence saving dialog");
			if (Table.getInstance().getTablePanel().isEmpty())
				return;

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					int index = Tank.getInstance().getCurrIndex();

					if (index == -1) {
						BoardPanel.appendTextln(I18N.getMessage("application.missingTable"));
						return;
					}

					SaveTableSequDialog dialog = new SaveTableSequDialog();
					File destination = dialog.showDialog();

					if (destination == null) {
						return;
					}

					// write the file(s)
					String extension = dialog.getExtension();

					// get list of tables
					List<IqmDataBox> boxes = Tank.getInstance().getTankDataAt(
							index);

					// write the content to the files
					TableFileWriter tfw = new TableFileWriter(destination,
							boxes, extension, TableFileWriter.MODE_SEQUENCE);
					tfw.run();
				}
			});

		}

		if ("quit".equals(e.getActionCommand())) {
			// close IQM
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						GUITools.getMainFrame().windowClosing(null);
					} catch (Exception e1) {
						// log the error message
						logger.error("", e1);
						logger.info("Exit code: -1.");
						System.exit(-1);
					}
				}
			});
		}
	}
}
