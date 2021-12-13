package at.mug.iqm.gui.menu;

/*
 * #%L
 * Project: IQM - Application Core
 * File: PlotAnalysisMenu.java
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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.mug.iqm.api.gui.OperatorMenuItem;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.commons.util.table.TableTools;
import at.mug.iqm.core.I18N;
import at.mug.iqm.core.Resources;
import at.mug.iqm.core.workflow.ExecutionProxy;
import at.mug.iqm.core.workflow.Plot;
import at.mug.iqm.core.workflow.Tank;
import at.mug.iqm.plot.bundle.descriptors.PlotOpAutoCorrelationDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpComplLogDepthDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpCutDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpDCMGeneratorDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpEntropyDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFFTDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFilterDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracAllomScaleDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracDFADescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracHiguchiDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracHurstDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFracSurrogateDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpFractalGeneratorDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpGenEntropyDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpHRVDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpMathDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpPointFinderDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpResampleDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpSignalGeneratorDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpStatisticsDescriptor;
import at.mug.iqm.plot.bundle.descriptors.PlotOpSymbolicAggregationDescriptor;

/**
 * This is the base class for the PlotAnalysis menu in IQM.
 * 
 * @author Helmut Ahammer, Philipp Kainz, Michael Mayrhofer-R.
 */
public class PlotAnalysisMenu extends DeactivatableMenu implements
		ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3301909889493957179L;

	// class specific logger
	private static final Logger logger = LogManager.getLogger(PlotAnalysisMenu.class);

	// class variable declaration
	private OperatorMenuItem tableOfPlotMenuItem;
	private OperatorMenuItem signalGeneratorMenuItem;
	private OperatorMenuItem fractalGeneratorMenuItem;
	private OperatorMenuItem dcmGeneratorMenuItem;
	private OperatorMenuItem cutMenuItem;
	private OperatorMenuItem statisticsMenuItem;
	private OperatorMenuItem pointFinderMenuItem;
	private OperatorMenuItem autoCorrMenuItem;
	private OperatorMenuItem fFTMenuItem;
	private OperatorMenuItem mathMenuItem;
	private OperatorMenuItem allomScaleMenuItem;
	private OperatorMenuItem hrvMenuItem;
	private OperatorMenuItem higuchiMenuItem;
	private OperatorMenuItem dfaMenuItem;
	private OperatorMenuItem entropyMenuItem;
	private OperatorMenuItem genEntropyMenuItem;
	private OperatorMenuItem hurstMenuItem;
	private OperatorMenuItem logDepthMenuItem;
	private OperatorMenuItem symbAggMenuItem;
	private OperatorMenuItem surrogateMenuItem;
	private OperatorMenuItem filterMenuItem;
	private OperatorMenuItem resampleMenuItem;

	/**
	 * 
	 * This is the standard constructor. On instantiation it returns a fully
	 * equipped PlotAnalysisMenu.
	 */
	public PlotAnalysisMenu() {
		logger.debug("Generating new instance of 'PlotAnalysis' menu.");

		// initialize the variables
		this.tableOfPlotMenuItem       = new OperatorMenuItem(OperatorType.PLOT);
		this.signalGeneratorMenuItem   = new OperatorMenuItem(OperatorType.PLOT_GENERATOR);
		this.fractalGeneratorMenuItem  = new OperatorMenuItem(OperatorType.PLOT_GENERATOR);
		this.dcmGeneratorMenuItem      = new OperatorMenuItem(OperatorType.PLOT_GENERATOR);
		this.cutMenuItem               = new OperatorMenuItem(OperatorType.PLOT);
		this.statisticsMenuItem        = new OperatorMenuItem(OperatorType.PLOT);
		this.pointFinderMenuItem       = new OperatorMenuItem(OperatorType.PLOT);
		this.autoCorrMenuItem          = new OperatorMenuItem(OperatorType.PLOT);
		this.fFTMenuItem               = new OperatorMenuItem(OperatorType.PLOT);
		this.mathMenuItem              = new OperatorMenuItem(OperatorType.PLOT);
		this.allomScaleMenuItem        = new OperatorMenuItem(OperatorType.PLOT);
		this.hrvMenuItem               = new OperatorMenuItem(OperatorType.PLOT);
		this.higuchiMenuItem           = new OperatorMenuItem(OperatorType.PLOT);
		this.dfaMenuItem               = new OperatorMenuItem(OperatorType.PLOT);
		this.entropyMenuItem           = new OperatorMenuItem(OperatorType.PLOT);
		this.genEntropyMenuItem        = new OperatorMenuItem(OperatorType.PLOT);
		this.hurstMenuItem             = new OperatorMenuItem(OperatorType.PLOT);
		this.logDepthMenuItem          = new OperatorMenuItem(OperatorType.PLOT);
		this.symbAggMenuItem           = new OperatorMenuItem(OperatorType.PLOT);
		this.surrogateMenuItem         = new OperatorMenuItem(OperatorType.PLOT);
		this.filterMenuItem            = new OperatorMenuItem(OperatorType.PLOT);
		this.resampleMenuItem          = new OperatorMenuItem(OperatorType.PLOT);

		// assemble the gui elements to a JMenu
		this.createAndAssembleMenu();

		logger.debug("Menu 'PlotAnalysis' generated.");
	}

	/**
	 * This method constructs the items.
	 */
	private void createAndAssembleMenu() {
		logger.debug("Assembling menu items in 'PlotAnalysis' menu.");

		// set menu attributes
		this.setText(I18N.getGUILabelText("menu.plotanalysis.text"));

		// assemble: add created elements to the JMenu
		this.add(this.createTableOfPlotMenuItem());
		this.add(this.createSignalGeneratorMenuItem());
		this.add(this.createFractalGeneratorMenuItem());
		this.add(this.createDCMGeneratorMenuItem());
		this.addSeparator();
		this.add(this.createCutMenuItem());
		this.add(this.createStatisticsMenuItem());
		this.add(this.createPointFinderMenuItem());
		this.add(this.createAutoCorrMenuItem());
		this.add(this.createFFTMenuItem());
		this.add(this.createMathMenuItem());
		this.add(this.createFilterMenuItem());
		this.add(this.createResampleMenuItem());
		this.addSeparator();
		this.add(this.createAllomScaleMenuItem());
		this.add(this.createHRVMenuItem());
		this.add(this.createHiguchiMenuItem());
		this.add(this.createDFAMenuItem());
		this.add(this.createEntropyMenuItem());
		this.add(this.createGenEntropyMenuItem());
		this.add(this.createHurstMenuItem());
		this.add(this.createLogDepthMenuItem());
		this.add(this.createSymbAggMenuItem());
		this.addSeparator();
		this.add(this.createSurrogateMenuItem());

	}

	/**
	 * This method initializes tableOfPlotAnalysisMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createTableOfPlotMenuItem() {
		this.tableOfPlotMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.table.text"));
		this.tableOfPlotMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.table.ttp"));
		this.tableOfPlotMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.plotanalysis.tableOfPlot")));
		this.tableOfPlotMenuItem.addActionListener(this);
		this.tableOfPlotMenuItem.setActionCommand("tableofplot");
		return this.tableOfPlotMenuItem;
	}

	/**
	 * This method initializes signalGeneratorAnalysisMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSignalGeneratorMenuItem() {
		this.signalGeneratorMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.signalGenerator.text"));
		this.signalGeneratorMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.signalGenerator.ttp"));
		// this.signalGeneratorMenuItem.setIcon(new
		// ImageIcon(GUIResources.getImagePathURL("icon.menu.plotanalysis.signalGenerator")));
		this.signalGeneratorMenuItem.addActionListener(this);
		this.signalGeneratorMenuItem.setActionCommand("signalgenerator");
		return this.signalGeneratorMenuItem;
	}

	/**
	 * This method initializes fractalGeneratorAnalysisMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFractalGeneratorMenuItem() {
		this.fractalGeneratorMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.fractalGenerator.text"));
		this.fractalGeneratorMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.fractalGenerator.ttp"));
		// this.fractalGeneratorMenuItem.setIcon(new
		// ImageIcon(GUIResources.getImagePathURL("icon.menu.plotanalysis.fractalGenerator")));
		this.fractalGeneratorMenuItem.addActionListener(this);
		this.fractalGeneratorMenuItem.setActionCommand("fractalgenerator");
		return this.fractalGeneratorMenuItem;
	}

	/**
	 * This method initializes dcmGeneratorAnalysisMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createDCMGeneratorMenuItem() {
		this.dcmGeneratorMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.dcmGenerator.text"));
		this.dcmGeneratorMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.dcmGenerator.ttp"));
		// this.dcmGeneratorMenuItem.setIcon(new
		// ImageIcon(GUIResources.getImagePathURL("icon.menu.plotanalysis.dcmGenerator")));
		this.dcmGeneratorMenuItem.addActionListener(this);
		this.dcmGeneratorMenuItem.setActionCommand("dcmgenerator");
		return this.dcmGeneratorMenuItem;
	}

	/**
	 * This method initializes cutAnalysisMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createCutMenuItem() {
		this.cutMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.cut.text"));
		this.cutMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.cut.ttp"));
		// this.cutMenuItem.setIcon(new
		// ImageIcon(GUIResources.getImagePathURL("icon.menu.plotanalysis.cut")));
		this.cutMenuItem.addActionListener(this);
		this.cutMenuItem.setActionCommand("cutofplot");
		return this.cutMenuItem;
	}

	/**
	 * This method initializes statisticsAnalysisMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createStatisticsMenuItem() {
		this.statisticsMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.statistics.text"));
		this.statisticsMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.statistics.ttp"));
		// this.statisticsMenuItem.setIcon(new
		// ImageIcon(GUIResources.getImagePathURL("icon.menu.plotanalysis.statistics")));
		this.statisticsMenuItem.addActionListener(this);
		this.statisticsMenuItem.setActionCommand("statisticsofplot");
		return this.statisticsMenuItem;
	}

	/**
	 * This method initializes pointFinderAnalysisMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createPointFinderMenuItem() {
		this.pointFinderMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.pointFinder.text"));
		this.pointFinderMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.pointFinder.ttp"));
		// this.pointFinderMenuItem.setIcon(new
		// ImageIcon(GUIResources.getImagePathURL("icon.menu.plotanalysis.pointFinder")));
		this.pointFinderMenuItem.addActionListener(this);
		this.pointFinderMenuItem.setActionCommand("pointfinderofplot");
		return this.pointFinderMenuItem;
	}

	/**
	 * This method initializes autoCorrMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createAutoCorrMenuItem() {
		this.autoCorrMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.autoCorr.text"));
		this.autoCorrMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.autoCorr.ttp"));
		this.autoCorrMenuItem.addActionListener(this);
		this.autoCorrMenuItem.setActionCommand("autocorrofplot");
		return this.autoCorrMenuItem;
	}

	/**
	 * This method initializes fFTMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFFTMenuItem() {
		this.fFTMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.fFT.text"));
		this.fFTMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.fFT.ttp"));
		this.fFTMenuItem.addActionListener(this);
		this.fFTMenuItem.setActionCommand("fftofplot");
		return this.fFTMenuItem;
	}

	/**
	 * This method initializes mathMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createMathMenuItem() {
		this.mathMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.math.text"));
		this.mathMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.math.ttp"));
		this.mathMenuItem.addActionListener(this);
		this.mathMenuItem.setActionCommand("mathofplot");
		return this.mathMenuItem;
	}
	
	/**
	 * This method initializes filterMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createFilterMenuItem() {
		this.filterMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.filter.text"));
		this.filterMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.filter.ttp"));
		this.filterMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.plotanalysis.filter")));
		this.filterMenuItem.addActionListener(this);
		this.filterMenuItem.setActionCommand("filterplot");
		return this.filterMenuItem;
	}

	/**
	 * This method initializes resampleMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createResampleMenuItem() {
		this.resampleMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.resample.text"));
		this.resampleMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.resample.ttp"));
		//this.resampleMenuItem.setIcon(new ImageIcon(Resources.getImageURL("icon.menu.plotanalysis.resample")));
		this.resampleMenuItem.addActionListener(this);
		this.resampleMenuItem.setActionCommand("resampleplot");
		return this.resampleMenuItem;
	}

	/**
	 * This method initializes AllomScaleMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createAllomScaleMenuItem() {
		this.allomScaleMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.allomScale.text"));
		this.allomScaleMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.allomScale.ttp"));
		this.allomScaleMenuItem.addActionListener(this);
		this.allomScaleMenuItem.setActionCommand("allomscaleofplot");
		return this.allomScaleMenuItem;
	}
	/**
	 * This method initializes HRVMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createHRVMenuItem() {
		this.hrvMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.hrv.text"));
		this.hrvMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.hrv.ttp"));
		this.hrvMenuItem.addActionListener(this);
		this.hrvMenuItem.setActionCommand("hrvofplot");
		return this.hrvMenuItem;
	}
	/**
	 * This method initializes HiguchiMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createHiguchiMenuItem() {
		this.higuchiMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.higuchi.text"));
		this.higuchiMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.higuchi.ttp"));
		this.higuchiMenuItem.addActionListener(this);
		this.higuchiMenuItem.setActionCommand("higuchiofplot");
		return this.higuchiMenuItem;
	}
	/**
	 * This method initializes DFAMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createDFAMenuItem() {
		this.dfaMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.DFA.text"));
		this.dfaMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.DFA.ttp"));
		this.dfaMenuItem.addActionListener(this);
		this.dfaMenuItem.setActionCommand("dfaofplot");
		return this.dfaMenuItem;
	}

	/**
	 * This method initializes entropyMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createEntropyMenuItem() {
		this.entropyMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.entropy.text"));
		this.entropyMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.entropy.ttp"));
		this.entropyMenuItem.addActionListener(this);
		this.entropyMenuItem.setActionCommand("entropyofplot");
		return this.entropyMenuItem;
	}
	/**
	 * This method initializes genEntropyMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createGenEntropyMenuItem() {
		this.genEntropyMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.genentropy.text"));
		this.genEntropyMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.genentropy.ttp"));
		this.genEntropyMenuItem.addActionListener(this);
		this.genEntropyMenuItem.setActionCommand("genentropyofplot");
		return this.genEntropyMenuItem;
	}

	/**
	 * This method initializes hurstMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createHurstMenuItem() {
		this.hurstMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.hurst.text"));
		this.hurstMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.hurst.ttp"));
		this.hurstMenuItem.addActionListener(this);
		this.hurstMenuItem.setActionCommand("hurstofplot");
		return this.hurstMenuItem;
	}
	/**
	 * This method initializes LogDepthMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createLogDepthMenuItem() {
		this.logDepthMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.logdepth.text"));
		this.logDepthMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.logdepth.ttp"));
		this.logDepthMenuItem.addActionListener(this);
		this.logDepthMenuItem.setActionCommand("logdepthofplot");
		return this.logDepthMenuItem;
	}
	
	/**
	 * This method initializes symbAggMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSymbAggMenuItem() {
		this.symbAggMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.symbAgg.text"));
		this.symbAggMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.symbAgg.ttp"));
		this.symbAggMenuItem.addActionListener(this);
		this.symbAggMenuItem.setActionCommand("symbaggofplot");
		return this.symbAggMenuItem;
	}

	/**
	 * This method initializes surrogateMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem createSurrogateMenuItem() {
		this.surrogateMenuItem.setText(I18N.getGUILabelText("menu.plotanalysis.surrogate.text"));
		this.surrogateMenuItem.setToolTipText(I18N.getGUILabelText("menu.plotanalysis.surrogate.ttp"));
		this.surrogateMenuItem.addActionListener(this);
		this.surrogateMenuItem.setActionCommand("surrogateofplot");
		return this.surrogateMenuItem;
	}

	/**
	 * This method performs the corresponding actions to the menu items.
	 * 
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		logger.debug(e.getActionCommand());

		if ("higuchiofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpFracHiguchiDescriptor());

		} else if ("tableofplot".equals(e.getActionCommand())) {
			if (!Plot.getInstance().isEmpty()) {
				// GUITools.displayTableData(Plot.getInstance().getPlotPanel()
				// .exportTable());
				// extract and convert the table model from the current plot
				// instance
				TableModel tm = TableTools.convertToTableModel(Plot
						.getInstance().getPlotPanel().exportTable().getModel());
				// add the item to the table
				Tank.getInstance().addNewItem(new IqmDataBox(tm));
			}
		} else if ("signalgenerator".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpSignalGeneratorDescriptor());
		} else if ("fractalgenerator".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpFractalGeneratorDescriptor());
		} else if ("dcmgenerator".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpDCMGeneratorDescriptor());
		} else if ("cutofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpCutDescriptor());
		} else if ("statisticsofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpStatisticsDescriptor());
		} else if ("pointfinderofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpPointFinderDescriptor());
		} else if ("autocorrofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpAutoCorrelationDescriptor());
		} else if ("fftofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpFFTDescriptor());
		} else if ("mathofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpMathDescriptor());
		} else if ("allomscaleofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpFracAllomScaleDescriptor());
		} else if ("hrvofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpHRVDescriptor());
		} else if ("entropyofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpEntropyDescriptor());
		} else if ("genentropyofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpGenEntropyDescriptor());
		} else if ("dfaofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpFracDFADescriptor());
		} else if ("hurstofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpFracHurstDescriptor());
		} else if ("logdepthofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpComplLogDepthDescriptor());
		} else if ("symbaggofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpSymbolicAggregationDescriptor());
		} else if ("surrogateofplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpFracSurrogateDescriptor());
		} else if ("filterplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpFilterDescriptor());
		} else if ("resampleplot".equals(e.getActionCommand())) {
			ExecutionProxy.launchInstance(new PlotOpResampleDescriptor());
		}
	}
}
