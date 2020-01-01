package at.mug.iqm.gui;

/*
 * #%L
 * Project: IQM - Application Core
 * File: MainFrame.java
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java.ayatana.ApplicationMenu;
import org.java.ayatana.AyatanaDesktop;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.IDialogUtil;
import at.mug.iqm.api.gui.IMainFrame;
import at.mug.iqm.api.gui.WaitingDialog;
import at.mug.iqm.commons.util.CompletionWaiter;
import at.mug.iqm.commons.util.DialogUtil;
import at.mug.iqm.commons.util.OperatingSystem;
import at.mug.iqm.commons.util.PropertyManager;
import at.mug.iqm.config.ConfigManager;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.util.DeleteFilesTask;
import at.mug.iqm.core.workflow.Look;
import at.mug.iqm.core.workflow.Manager;
import at.mug.iqm.core.workflow.Plot;
import at.mug.iqm.core.workflow.Table;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.core.workflow.Text;
import at.mug.iqm.gui.menu.AbstractMenuBar;
import at.mug.iqm.gui.menu.CoreMenuBar;
import at.mug.iqm.gui.util.GUITools;

/**
 * 
 * This is the main frame for the IQM application.
 * 
 * @author Helmut Ahammer, Philipp Kainz, Michael Mayrhofer-R.
 * @since 2012 02 24
 * 
 */
public class MainFrame extends JFrame implements WindowListener, IMainFrame,
		ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1346549404978265675L;

	// class specific logger
	private static Class<?> caller = MainFrame.class;
	private static final Logger logger = LogManager.getLogger(MainFrame.class);

	private JPanel applicationPanel;

	// class variable declaration
	private JPanel leftRightPanel;
	private JPanel managerTankPanel;
	private JTabbedPane itemContent;

	private AbstractMenuBar coreMenuBar;
	private MainPanel mainPanel;
	private BoardPanel boardPanel;
	private ManagerPanel managerPanel;
	private LookPanel lookPanel;
	private PlotPanel plotPanel;
	private TablePanel tablePanel;
	private TextPanel textPanel;
	private TankPanel tankPanel;
	private StatusPanel statusPanel;

	private final int timerDelay = 1000;
	private Timer propertyWriteTimer;

	/**
	 * This is the default constructor. One has to call
	 * <code>initializeDefaults()</code> explicitly.
	 * <p>
	 * If other elements than the default (core) shall be displayed, they must
	 * be set using the setter methods. After that, one has to explicitly call
	 * <code>initializeCustoms()</code>.
	 * </p>
	 */
	public MainFrame() {
		super();

		Application.setMainFrame(this);

		this.applicationPanel = new JPanel();

		// instantiate all known member variables (this already constructs the
		// panels!)
		this.leftRightPanel = new JPanel();
		this.managerTankPanel = new JPanel();
		this.itemContent = new JTabbedPane(SwingConstants.BOTTOM);
	}

	/**
	 * Initializes the default elements of the standard UI.
	 */
	public void initializeDefaults() {
		// construct the ui
		this.constructDefaultElements();
		this.createAndAssemble();
		this.initialize();
	}

	/**
	 * Constructs the default elements of the standard UI.
	 */
	public void constructDefaultElements() {
		logger.debug("Constructing the default GUI (core version)");

		this.coreMenuBar = new CoreMenuBar();
		this.mainPanel = new MainPanel();
		this.managerPanel = new ManagerPanel();
		this.boardPanel = new BoardPanel();
		this.lookPanel = new LookPanel();
		this.plotPanel = new PlotPanel();
		this.tablePanel = new TablePanel();
		this.textPanel = new TextPanel();
		this.tankPanel = new TankPanel();
		this.statusPanel = new StatusPanel();

	}

	/**
	 * Creates the user interface of IQM.
	 */
	private void createAndAssemble() {

		this.setJMenuBar(coreMenuBar);

		// construct the menu bar for the OS
		if (OperatingSystem.isUnix()) {
			if (AyatanaDesktop.isSupported()) {
				System.setProperty("jayatana.force", "true");
				ApplicationMenu.tryInstall(this);
			}

			// DEFINE UNIX MAIN FRAME TITLE
			Class<?> xtoolkit = Toolkit.getDefaultToolkit().getClass();
			if (xtoolkit.getName().equals("sun.awt.X11.XToolkit")) {
				try {
					final Field awtAppClassName = xtoolkit
							.getDeclaredField("awtAppClassName");
					awtAppClassName.setAccessible(true);
					awtAppClassName.set(null, "IQM");
				} catch (NoSuchFieldException nsfe) {
					logger.warn("Cannot set awt title: ", nsfe);
				} catch (IllegalAccessException iae) {
					logger.warn("Cannot set awt title: ", iae);
				}
			}

		}

		// set the layout
		applicationPanel.setLayout(new BorderLayout(5, 0));

		// set the content and layout of the JFrame
		applicationPanel.add(getMainPanel(), BorderLayout.NORTH);
		applicationPanel.add(createLeftRightPanel(), BorderLayout.CENTER);
		applicationPanel.add(getStatusPanel(), BorderLayout.PAGE_END);

		this.getContentPane().add(applicationPanel);

		// tell all components to acquire the look and feel
		SwingUtilities.updateComponentTreeUI(this);

		// set the icon and title for the frame
		this.setIconImage(new ImageIcon(Resources
				.getImageURL("icon.application.red.32x32")).getImage());
		this.setTitle(I18N
				.getGUILabelText(
						"application.frame.main.titleWithoutModelName",
						IQMConstants.APPLICATION_NAME,
						IQMConstants.APPLICATION_VERSION));

		// use a custom exit strategy
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		// for exit add a custom window listener
		this.addWindowListener(this);

		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				handleComponentPropertyEvent();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				handleComponentPropertyEvent();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		// allow resizing
		this.setResizable(true);
		// pack all components;
		this.pack();
		// set the status bars invisible for the startup
		// IMPORTANT: set it after this.pack()!
		getStatusPanel().getProgressBarStack().setVisible(false);
		getStatusPanel().getProgressBarSingleOperation().setVisible(false);
	}

	/**
	 * Handles the timer for writing the time stamp 1 second after each resize
	 * or move event.
	 */
	public void handleComponentPropertyEvent() {
		if (propertyWriteTimer == null) {
			propertyWriteTimer = new Timer(timerDelay, MainFrame.this);
			propertyWriteTimer.start();
		} else {
			propertyWriteTimer.restart();
		}
	}

	@Override
	public void initialize() {
		// set static variables in GUITools
		// take the constructed instance of IQM Frame
		GUITools.setMainFrame(this);
		GUITools.setStatusPanel(getStatusPanel());

		// get the look panel and set it to the static variable in Look
		Look.getInstance().addLookPanel(getLookPanel());
		// initialize the look panel
		getLookPanel().initialize();

		// get the plot panel and set it to the static variable in Plot
		Plot.getInstance().setPlotPanel(getPlotPanel());

		// get the table panel and set it to the static variable in Table
		Table.getInstance().setTablePanel(getTablePanel());

		// get the text panel and set it to the static variable in Text
		Text.getInstance().setTextPanel(getTextPanel());

		// get the manager panel, initialize it and set it to the static
		// variable in Manager
		Manager.getInstance().setManagerPanel(getManagerPanel());
		Manager.getInstance().initialize();

		// get the tank panel and set it to the static variable in Tank
		Tank.getInstance().setTankPanel(getTankPanel());
		
		// update the navigator after switching to the component
		this.itemContent.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (itemContent.getSelectedIndex() == 0) {
					System.out.println("updating navigator");
					Look.getInstance().getCurrentLookPanel().updateNavigator();
				}
			}
		});

		logger.debug("Done.");
	}

	public JPanel getApplicationPanel() {
		return applicationPanel;
	}

	public void setPanel(JPanel iqmPanel) {
		this.applicationPanel = iqmPanel;
	}

	private JPanel createLeftRightPanel() {
		leftRightPanel.setLayout(new BorderLayout(2, 0));
		leftRightPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		leftRightPanel.add(createItemContent(), BorderLayout.CENTER);
		JScrollPane mgrPane = new JScrollPane(createManagerContainer());
		mgrPane.setViewportBorder(BorderFactory.createEmptyBorder());
		mgrPane.setBorder(BorderFactory.createEmptyBorder());
		leftRightPanel.add(mgrPane, BorderLayout.WEST);
		return leftRightPanel;
	}

	/**
	 * This method constructs the look and tank container.
	 */
	private JPanel createItemContent() {
		JPanel container = new JPanel(new BorderLayout(0, 2));

		// add the look panel center
		this.itemContent.insertTab(
				I18N.getGUILabelText("application.tabbedPane.image.title"),
				new ImageIcon(Resources.getImageURL("icon.image.generic16")),
				getLookPanel(), null, 0);

		this.itemContent
				.insertTab(
						I18N.getGUILabelText("application.tabbedPane.plot.title"),
						new ImageIcon(Resources
								.getImageURL("icon.menu.file.openPlot")),
						getPlotPanel(), null, 1);

		this.itemContent
				.insertTab(
						I18N.getGUILabelText("application.tabbedPane.table.title"),
						new ImageIcon(Resources
								.getImageURL("icon.menu.file.openTable")),
						getTablePanel(), null, 2);

		this.itemContent.insertTab(
				I18N.getGUILabelText("application.tabbedPane.text.title"),
				new ImageIcon(Resources.getImageURL("icon.tabbedPane.text")),
				getTextPanel(), null, 3);

		// add this item content
		container.add(this.itemContent, BorderLayout.CENTER);

		// add the tank panel south
		container.add(getTankPanel(), BorderLayout.PAGE_END);

		return container;
	}

	private JPanel createManagerContainer() {
		managerTankPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbcManager = new GridBagConstraints();
		gbcManager.gridx = 0;
		gbcManager.gridy = 0;
		gbcManager.weighty = 1; // expands the panel to its vertical maximum
		gbcManager.weightx = 0;
		gbcManager.fill = GridBagConstraints.VERTICAL;
		gbcManager.anchor = GridBagConstraints.LINE_END;

		managerTankPanel.add(getManagerPanel(), gbcManager);
		return managerTankPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.mug.iqm.gui.IMainFrame#closeIQMAndExit()
	 */
	@Override
	public void closeIQMAndExit() throws Exception {
		// halt the background threads
		try {
			Thread mm = Application.getMemoryMonitor();
			mm.interrupt();

			Thread ct = Application.getCleanerTask();
			ct.interrupt();
		} catch (Exception e) {
			logger.error(e);
		}

		writeWindowProperties();

		// close the frame
		this.setVisible(false);
		// initiate the deletion of temporary files
		DeleteFilesTask dtfTask = new DeleteFilesTask(ConfigManager
				.getCurrentInstance().getTempPath().toString());
		WaitingDialog dlg = new WaitingDialog(
				I18N.getMessage("application.dialog.waiting.deletingTempData"),
				false);
		dtfTask.addPropertyChangeListener(new CompletionWaiter(dlg));
		dtfTask.execute();
		dlg.setVisible(true);
		// force repaint
		dlg.paint(dlg.getGraphics());

		try {
			boolean success = dtfTask.get();
			if (!success) {
				Toolkit.getDefaultToolkit().beep();
				DialogUtil.getInstance().showDefaultErrorMessage(
						I18N.getMessage("application.deletingTempFiles.error"));
				logger.info("Could not delete all temporary files, trying again next startup. Exit code: -1.");
				System.exit(-1);
			} else {
				logger.debug("All temporary files deleted? -> " + success
						+ ". Now shutting down, bye.");
				logger.info("Exit code: 0.");
				System.out.println("Exit code: 0.");
				// finally exit normally (status code 0)
				System.exit(0);
			}
		} catch (InterruptedException e) {
			logger.error("", e);
			logger.info("Exit code: -1.");
			System.out.println("Exit code: -1.");
			System.exit(-1);
		} catch (ExecutionException e) {
			logger.error("", e);
			logger.info("Exit code: -1.");
			System.out.println("Exit code: -1.");
			System.exit(-1);
		} finally {
			logger.info("Exit code: -101.");
			System.out.println("Exit code: -101.");
			System.exit(-101);
		}
	}

	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public void setBoardPanel(BoardPanel boardPanel) {
		this.boardPanel = boardPanel;
	}

	public BoardPanel getBoardPanel() {
		return boardPanel;
	}

	public void setManagerPanel(ManagerPanel managerPanel) {
		this.managerPanel = managerPanel;
	}

	@Override
	public ManagerPanel getManagerPanel() {
		return managerPanel;
	}

	public void setLookPanel(LookPanel lookPanel) {
		this.lookPanel = lookPanel;
	}

	@Override
	public LookPanel getLookPanel() {
		return lookPanel;
	}

	public void setPlotPanel(PlotPanel iqmPlotPanel) {
		this.plotPanel = iqmPlotPanel;
	}

	@Override
	public PlotPanel getPlotPanel() {
		return plotPanel;
	}

	@Override
	public TablePanel getTablePanel() {
		return tablePanel;
	}

	public void setTablePanel(TablePanel iqmTablePanel) {
		this.tablePanel = iqmTablePanel;
	}

	@Override
	public TextPanel getTextPanel() {
		return textPanel;
	}

	public void setTextPanel(TextPanel iqmTextPanel) {
		this.textPanel = iqmTextPanel;
	}

	public void setTankPanel(TankPanel iqmTankPanel) {
		this.tankPanel = iqmTankPanel;
	}

	@Override
	public TankPanel getTankPanel() {
		return tankPanel;
	}

	@Override
	public StatusPanel getStatusPanel() {
		return this.statusPanel;
	}

	public void setStatusPanel(StatusPanel statusPanel) {
		this.statusPanel = statusPanel;
	}

	@Override
	public JTabbedPane getItemContent() {
		return itemContent;
	}

	public void setItemContent(JTabbedPane itemContent) {
		this.itemContent = itemContent;
	}
	
	public JPanel getManagerTankPanel() {	
		return managerTankPanel;
	}
	
	public void setManagerTankPanel(JPanel managerTankPanel) {	
		this.managerTankPanel = managerTankPanel;
	}

	/**
	 * Writes the current properties of the frame.
	 */
	@Override
	public void windowActivated(WindowEvent arg0) {
		handleComponentPropertyEvent();
	}

	/**
	 * Custom windowClosed handler.
	 * 
	 * @see WindowListener#windowClosed(WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent arg0) {
		System.exit(0);
	}

	/**
	 * Custom windowClosing handler. The user is asked to save open unsaved
	 * work, if the Tank holds is not empty.
	 * 
	 * @see WindowListener#windowClosing(WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent arg0) {
		if (!Tank.getInstance().isEmpty()) {
			// ask the user, if he/she really wants to quit.
			int selection = DialogUtil.getInstance().showQuestionMessage(this,
					I18N.getMessage("application.exit.warn"));
			if (selection != IDialogUtil.YES_OPTION) {
				logger.debug("User aborted exiting the application.");
				return;
			} else {
				logger.debug("User confirmed exiting the application, shutting down.");
				BoardPanel.appendTextln(
						I18N.getMessage("application.deletingTempFiles.info"),
						caller);

				// close IQM
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {
							GUITools.getMainFrame().closeIQMAndExit();
						} catch (Exception e1) {
							// log the error message
							logger.error("", e1);
							logger.info("Exit code: -1.");
							System.out.println("Exit code: -1");
							System.exit(-1);
						}
					}
				});

			}
		} else {
			// close IQM
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						GUITools.getMainFrame().closeIQMAndExit();
					} catch (Exception e1) {
						// log the error message
						logger.error("", e1);
						logger.info("Exit code: -1.");
						System.out.println("Exit code: -1");
						System.exit(-1);
					}
				}
			});
		}
	}

	@Override
	public void resetTitleBar() {
		setTitle(I18N
				.getGUILabelText(
						"application.frame.main.titleWithoutModelName",
						IQMConstants.APPLICATION_NAME,
						IQMConstants.APPLICATION_VERSION));
	}

	/**
	 * Unused.
	 */
	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	/**
	 * Unused.
	 */
	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	/**
	 * Unused.
	 */
	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	/**
	 * Unused.
	 */
	@Override
	public void windowOpened(WindowEvent arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.mug.iqm.gui.IMainFrame#setSelectedTabIndex(int)
	 */
	@Override
	public void setSelectedTabIndex(int i) {
		this.itemContent.setSelectedIndex(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.mug.iqm.gui.IMainFrame#getSelectedTabIndex()
	 */
	@Override
	public int getSelectedTabIndex() {
		return this.itemContent.getSelectedIndex();
	}

	public AbstractMenuBar getCoreMenuBar() {
		return coreMenuBar;
	}

	public void setCoreMenuBar(AbstractMenuBar coreMenuBar) {
		this.coreMenuBar = coreMenuBar;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == propertyWriteTimer) {
			propertyWriteTimer.stop();
			propertyWriteTimer = null;
			writeWindowProperties();
		}
	}

	public void writeWindowProperties() {
		// write the properties
		PropertyManager pm = PropertyManager.getManager(this);
		pm.setProperty("loc_x", String.valueOf(this.getLocation().x));
		pm.setProperty("loc_y", String.valueOf(this.getLocation().y));
		pm.setProperty("width", String.valueOf(this.getWidth()));
		pm.setProperty("height", String.valueOf(this.getHeight()));
		pm.setProperty("extendedState", String.valueOf(this.getExtendedState()));
	}

}
