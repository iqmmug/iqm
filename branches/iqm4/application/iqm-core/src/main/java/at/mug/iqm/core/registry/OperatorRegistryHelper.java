package at.mug.iqm.core.registry;

/*
 * #%L
 * Project: IQM - Application Core
 * File: OperatorRegistryHelper.java
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


import java.util.ArrayList;
import java.util.List;

import javax.media.jai.JAI;

import org.apache.log4j.Logger;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.core.I18N;
import at.mug.iqm.img.bundle.descriptors.IqmOpACFDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpAffineDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpAlignDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpBUnwarpJDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpBorderDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCalcImageDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCalcValueDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCoGRegDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpColDeconDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpComplexLogDepthDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpConvertDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCreateFracSurfDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCreateImageDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpCropDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpDFTDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpDirLocalDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpDistMapDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpEdgeDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracBoxDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracFFTDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracGenDimDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracHiguchiDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracIFSDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracHRMDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracLacDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracMinkowskiDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracPyramidDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracScanDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracSurrogateDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpHistoModifyDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpImgStabilizerDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpInvertDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpKMeansDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpKMeansFuzzyDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpMorphDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpNexelScanDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpRGBRelativeDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpROISegmentDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpRankDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpRegGrowDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpResizeDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpSmoothDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpStackStatDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpStatRegMergeDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpStatisticsDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpTempMatchDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpThresholdDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpTurboRegDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpUnsharpMaskDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpWatershedDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpWhiteBalanceDescriptor;
import at.mug.iqm.img.bundle.descriptors.IqmOpGenEntDescriptor;
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
 * This is a utility class for a controlled initialization of all image and plot
 * operators on IQM startup.
 * 
 * @author Philipp Kainz
 */
public final class OperatorRegistryHelper {
	// class specific logger
	private static final Logger logger = Logger
			.getLogger(OperatorRegistryHelper.class);

	/**
	 * This method initializes every IQM image processing operator of the
	 * standard operator bundle.
	 * <p>
	 * In order to register an image operator, one has to call
	 * <code>register()</code> on the operator's {@link IOperatorDescriptor}
	 * like <br>
	 * 
	 * <pre>
	 * IOperatorDescriptor myDescriptor = IqmOpThresholdDescriptor.register();
	 * </pre>
	 * 
	 * <p>
	 * In order to use the operator with the JAI system using
	 * {@link JAI#create(String, Object)} from anywhere in the operators, one
	 * has to call <code>registerWithJAI()</code>, too, like <br>
	 * 
	 * <pre>
	 * IqmOpThresholdDescriptor.registerWithJAI();
	 * </pre>
	 * 
	 * <p>
	 * A convenient method for displaying the registration process on the start
	 * screen is provided by calling {@link #updateWelcomeText(String)}, like <br>
	 * 
	 * <pre>
	 * updateWelcomeText(thresDesc.getName());
	 * </pre>
	 * 
	 * @throws Exception
	 *             if an operator's descriptor encounters any errors
	 */
	public static void registerImageOperators() throws Exception {
		logger.debug("Initializing image operators of the standard operator bundle...");

		IOperatorDescriptor invertDesc = IqmOpInvertDescriptor.register(); // 2009 01
		updateWelcomeText(invertDesc.getName());

		IOperatorDescriptor convertDesc = IqmOpConvertDescriptor.register(); // 2009 03
		updateWelcomeText(convertDesc.getName());

		IOperatorDescriptor resizeDesc = IqmOpResizeDescriptor.register(); // 2009 04
		updateWelcomeText(resizeDesc.getName());

		IOperatorDescriptor thresDesc = IqmOpThresholdDescriptor.register(); // 2009 04
		IqmOpThresholdDescriptor.registerWithJAI();
		updateWelcomeText(thresDesc.getName());

		IOperatorDescriptor rgbRelDesc = IqmOpRGBRelativeDescriptor.register(); // 2009 04
		updateWelcomeText(rgbRelDesc.getName());

		IOperatorDescriptor morphDesc = IqmOpMorphDescriptor.register(); // 2009 04
		updateWelcomeText(morphDesc.getName());

		IOperatorDescriptor calcImgDesc = IqmOpCalcImageDescriptor.register(); // 2009 05
		updateWelcomeText(calcImgDesc.getName());

		IOperatorDescriptor calcValueDesc = IqmOpCalcValueDescriptor.register(); // 2009 05
		updateWelcomeText(calcValueDesc.getName());

		IOperatorDescriptor cDeconDesc = IqmOpColDeconDescriptor.register(); // 2009 05
		updateWelcomeText(cDeconDesc.getName());

		IOperatorDescriptor kMeansDesc = IqmOpKMeansDescriptor.register(); // 2009 05
		updateWelcomeText(kMeansDesc.getName());

		IOperatorDescriptor kMeansFuzzyDesc = IqmOpKMeansFuzzyDescriptor.register(); // 2009 05
		updateWelcomeText(kMeansFuzzyDesc.getName());

		IOperatorDescriptor tMatchDesc = IqmOpTempMatchDescriptor.register(); // 2009 05
		updateWelcomeText(tMatchDesc.getName());

		IOperatorDescriptor bUnwarpJDesc = IqmOpBUnwarpJDescriptor.register(); // 2009 05
		updateWelcomeText(bUnwarpJDesc.getName());

		IOperatorDescriptor turboRegDesc = IqmOpTurboRegDescriptor.register(); // 200905
		updateWelcomeText(turboRegDesc.getName());

		IOperatorDescriptor rankDesc = IqmOpRankDescriptor.register(); // 2009 06
		updateWelcomeText(rankDesc.getName());

		IOperatorDescriptor histoModDesc = IqmOpHistoModifyDescriptor.register(); // 2009 06
		updateWelcomeText(histoModDesc.getName());

		IOperatorDescriptor edgeDesc = IqmOpEdgeDescriptor.register(); // 2009 06
		updateWelcomeText(edgeDesc.getName());

		IOperatorDescriptor smoothDesc = IqmOpSmoothDescriptor.register(); // 2009 06
		updateWelcomeText(smoothDesc.getName());

		IOperatorDescriptor uMaskDesc = IqmOpUnsharpMaskDescriptor.register(); // 2009 06
		updateWelcomeText(uMaskDesc.getName());

		IOperatorDescriptor alignDesc = IqmOpAlignDescriptor.register(); // 2009 06
		updateWelcomeText(alignDesc.getName());

		IOperatorDescriptor affineDesc = IqmOpAffineDescriptor.register(); // 2009 06
		updateWelcomeText(affineDesc.getName());

		IOperatorDescriptor cropDesc = IqmOpCropDescriptor.register(); // 2009 06
		updateWelcomeText(cropDesc.getName());

		IOperatorDescriptor borderDesc = IqmOpBorderDescriptor.register(); // 2009 06
		updateWelcomeText(borderDesc.getName());

		IOperatorDescriptor statDesc = IqmOpStatisticsDescriptor.register(); // 2009 06
		updateWelcomeText(statDesc.getName());

		IOperatorDescriptor fracPyrDesc = IqmOpFracPyramidDescriptor.register(); // 2009 07
		updateWelcomeText(fracPyrDesc.getName());

		IOperatorDescriptor cogDesc = IqmOpCoGRegDescriptor.register(); // 2009 07
		updateWelcomeText(cogDesc.getName());

		IOperatorDescriptor fracMinkowskiDesc = IqmOpFracMinkowskiDescriptor.register(); // 2009 07
		updateWelcomeText(fracMinkowskiDesc.getName());

		IOperatorDescriptor fIFSDesc = IqmOpFracIFSDescriptor.register(); // 2009 10
		updateWelcomeText(fIFSDesc.getName());

		IOperatorDescriptor fHRMDesc = IqmOpFracHRMDescriptor.register(); // 2016 05
		updateWelcomeText(fHRMDesc.getName());

		IOperatorDescriptor fBoxDesc = IqmOpFracBoxDescriptor.register(); // 2009 11
		updateWelcomeText(fBoxDesc.getName());

		IOperatorDescriptor dftDesc = IqmOpDFTDescriptor.register(); // 2009 11
		updateWelcomeText(dftDesc.getName());

		IOperatorDescriptor wbDesc = IqmOpWhiteBalanceDescriptor.register(); // 2009 12
		updateWelcomeText(wbDesc.getName());

		IOperatorDescriptor roiSegDesc = IqmOpROISegmentDescriptor.register(); // 2009 12
		updateWelcomeText(roiSegDesc.getName());

		IOperatorDescriptor fracSurfDesc = IqmOpCreateFracSurfDescriptor.register(); // 2010 01
		updateWelcomeText(fracSurfDesc.getName());

		IOperatorDescriptor createImageDesc = IqmOpCreateImageDescriptor.register(); // 2010 02
		updateWelcomeText(createImageDesc.getName());

		IOperatorDescriptor fracHiguchiDesc = IqmOpFracHiguchiDescriptor.register(); // 2010 04
		updateWelcomeText(fracHiguchiDesc.getName());

		IOperatorDescriptor wsDesc = IqmOpWatershedDescriptor.register(); // 2010 05
		updateWelcomeText(wsDesc.getName());

		IOperatorDescriptor dirLocDesc = IqmOpDirLocalDescriptor.register(); // 2010  05
		updateWelcomeText(dirLocDesc.getName());

		IOperatorDescriptor imgStabDesc = IqmOpImgStabilizerDescriptor.register(); // 2010 05
		updateWelcomeText(imgStabDesc.getName());

		IOperatorDescriptor distMDesc = IqmOpDistMapDescriptor.register(); // 2010 05
		updateWelcomeText(distMDesc.getName());

		IOperatorDescriptor fracFFTDesc = IqmOpFracFFTDescriptor.register(); // 2011  02
		updateWelcomeText(fracFFTDesc.getName());

		IOperatorDescriptor fracLacDesc = IqmOpFracLacDescriptor.register(); // 2011 04
		updateWelcomeText(fracLacDesc.getName());

		IOperatorDescriptor regGrowDesc = IqmOpRegGrowDescriptor.register(); // 2011 12
		updateWelcomeText(regGrowDesc.getName());

		IOperatorDescriptor nexScDesc = IqmOpNexelScanDescriptor.register(); // 2011 12
		updateWelcomeText(nexScDesc.getName());

		IOperatorDescriptor statRegMDesc = IqmOpStatRegMergeDescriptor.register(); // 2012 01
		updateWelcomeText(statRegMDesc.getName());

		IOperatorDescriptor stackStatDesc = IqmOpStackStatDescriptor.register(); // 2012 01
		updateWelcomeText(stackStatDesc.getName());

		IOperatorDescriptor fracGenDimDesc = IqmOpFracGenDimDescriptor.register(); // 2012 03
		updateWelcomeText(fracGenDimDesc.getName());

		IOperatorDescriptor fracScanDesc = IqmOpFracScanDescriptor.register(); // 2012 06
		updateWelcomeText(fracScanDesc.getName());

		IOperatorDescriptor acfDesc = IqmOpACFDescriptor.register(); // 2012 11
		updateWelcomeText(acfDesc.getName());

		IOperatorDescriptor fracSurrogateDesc = IqmOpFracSurrogateDescriptor.register(); // 2012 11
		updateWelcomeText(fracSurrogateDesc.getName());
		
		IOperatorDescriptor complexLogDepthDesc = IqmOpComplexLogDepthDescriptor.register(); // 2014 01
		updateWelcomeText(complexLogDepthDesc.getName());
		
		IOperatorDescriptor GenEntDesc = IqmOpGenEntDescriptor.register(); // 2018 12
		updateWelcomeText(GenEntDesc.getName());

		logger.debug("Done.");
	}

	/**
	 * This method initializes every IQM image processing operator of the
	 * standard operator bundle.
	 * <p>
	 * In order to register an image operator, one has to call
	 * <code>register()</code> on the operator's {@link IOperatorDescriptor}
	 * like <br>
	 * 
	 * <pre>
	 * IOperatorDescriptor myDescriptor = IqmOpThresholdDescriptor.register();
	 * </pre>
	 * 
	 * <p>
	 * A convenient method for displaying the registration process on the start
	 * screen is provided by calling {@link #updateWelcomeText(String)}, like <br>
	 * 
	 * <pre>
	 * updateWelcomeText(thresDesc.getName());
	 * </pre>
	 * 
	 * @throws Exception
	 *             if an operator's descriptor encounters any errors
	 */
	public static void registerPlotOperators() throws Exception {
		logger.debug("Initializing plot operators of the standard operator bundle...");

		IOperatorDescriptor sigGenDesc = PlotOpSignalGeneratorDescriptor.register();
		updateWelcomeText(sigGenDesc.getName());

		IOperatorDescriptor dcmGenDesc = PlotOpDCMGeneratorDescriptor.register();
		updateWelcomeText(dcmGenDesc.getName());

		IOperatorDescriptor fracGenDesc = PlotOpFractalGeneratorDescriptor.register();
		updateWelcomeText(fracGenDesc.getName());

		IOperatorDescriptor plotCutDesc = PlotOpCutDescriptor.register();
		updateWelcomeText(plotCutDesc.getName());

		IOperatorDescriptor plotPntFinDesc = PlotOpPointFinderDescriptor.register();
		updateWelcomeText(plotPntFinDesc.getName());

		IOperatorDescriptor plotACDesc = PlotOpAutoCorrelationDescriptor.register();
		updateWelcomeText(plotACDesc.getName());

		IOperatorDescriptor plotFFTDesc = PlotOpFFTDescriptor.register();
		updateWelcomeText(plotFFTDesc.getName());

		IOperatorDescriptor plotStatDesc = PlotOpStatisticsDescriptor.register();
		updateWelcomeText(plotStatDesc.getName());

		IOperatorDescriptor plotMathDesc = PlotOpMathDescriptor.register();
		updateWelcomeText(plotMathDesc.getName());

		IOperatorDescriptor plotFracSurrDesc = PlotOpFracSurrogateDescriptor.register();
		updateWelcomeText(plotFracSurrDesc.getName());

		IOperatorDescriptor plotFracHurstDesc = PlotOpFracHurstDescriptor.register();
		updateWelcomeText(plotFracHurstDesc.getName());
		
		IOperatorDescriptor plotComplLogDepthDesc = PlotOpComplLogDepthDescriptor.register();
		updateWelcomeText(plotComplLogDepthDesc.getName());
		
		IOperatorDescriptor plotFracAllomScaleDesc = PlotOpFracAllomScaleDescriptor.register();
		updateWelcomeText(plotFracAllomScaleDesc.getName());
		
		IOperatorDescriptor plotHRVDesc = PlotOpHRVDescriptor.register();
		updateWelcomeText(plotHRVDesc.getName());

		IOperatorDescriptor plotFracHigDesc = PlotOpFracHiguchiDescriptor.register();
		updateWelcomeText(plotFracHigDesc.getName());
		
		IOperatorDescriptor plotFracDFADesc = PlotOpFracDFADescriptor.register();
		updateWelcomeText(plotFracDFADesc.getName());

		IOperatorDescriptor plotEntropyDesc = PlotOpEntropyDescriptor.register();
		updateWelcomeText(plotEntropyDesc.getName());
		
		IOperatorDescriptor plotGenEntropyDesc = PlotOpGenEntropyDescriptor.register();
		updateWelcomeText(plotGenEntropyDesc.getName());
		
		IOperatorDescriptor plotSymbAggDesc = PlotOpSymbolicAggregationDescriptor.register(); //2014 01
		updateWelcomeText(plotSymbAggDesc.getName());
		
		IOperatorDescriptor plotFilterDesc = PlotOpFilterDescriptor.register(); //2014 10
		updateWelcomeText(plotFilterDesc.getName());
		
		IOperatorDescriptor plotResampleDesc = PlotOpResampleDescriptor.register(); //2018-11
		updateWelcomeText(plotResampleDesc.getName());

		logger.debug("Done.");
	}

	public static void updateRegistry() throws Exception {
		logger.debug("Re-registering all operators...");
		List<String> registeredOperators = new ArrayList<String>(Application
				.getOperatorRegistry().getNames());

		for (String name : registeredOperators) {
			Application.getOperatorRegistry().updateRegistryEntry(name);
		}

		logger.debug("Done.");
	}

	/**
	 * Updates the dynamic welcome text in the startup screen using a given
	 * text.
	 * 
	 * @param text
	 */
	private static void updateWelcomeText(String text) {
		Application
				.getApplicationStarter()
				.updateDynamicWelcomeText(
						I18N.getMessage(
								"application.dialog.welcome.operator.registering",
								text));
	}
}
