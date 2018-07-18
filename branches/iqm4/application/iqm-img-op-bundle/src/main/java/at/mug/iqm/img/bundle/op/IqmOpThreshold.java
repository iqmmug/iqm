package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpThreshold.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2017 Helmut Ahammer, Philipp Kainz
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


import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.img.bundle.descriptors.IqmOpThresholdDescriptor;

/**
 * @author Ahammer, Kainz
 * @since 2009 04
 */
public class IqmOpThreshold extends AbstractOperator {

	public IqmOpThreshold() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	// /**
	// * This method sets the lower threshold value(s) calculated by the preset
	// method
	// * It uses the histogram of the total image!
	// * Nicht sicher ob das thread safe ist!!!!!!!!!!!!!!!!!!!
	// */
	// private ParameterBlockJAI setPresetLowValues(ParameterBlockJAI pbJAI, int
	// preset) {
	// int p0 = pbJAI.getIntParameter("ThresholdLow1");
	// int p1 = pbJAI.getIntParameter("ThresholdHigh1");
	// int p2 = pbJAI.getIntParameter("ThresholdLow2");
	// int p3 = pbJAI.getIntParameter("ThresholdHigh2");
	// int p4 = pbJAI.getIntParameter("ThresholdLow3");
	// int p5 = pbJAI.getIntParameter("ThresholdHigh3");
	// PlanarImage pi = (PlanarImage) pbJAI.getSource(0);
	// int numBands = pi.getNumBands();
	// ParameterBlock pb1 = new ParameterBlock();
	// pb1.addSource(pi);
	// pb1.add(null); //Roi
	// pb1.add(1); //Sampling
	// pb1.add(1);
	// pb1.add(new int[]{256}); //Number of bins
	// pb1.add(new double[]{0}); //Start
	// pb1.add(new double[]{256}); //End
	// PlanarImage pi2 = JAI.create("histogram", pb1);
	// Histogram histo = (Histogram) pi2.getProperty("histogram");
	// double[] th = null;
	// if (preset == 1) th = histo.getMean();
	// if (preset == 2) th = histo.getMaxEntropyThreshold();
	// if (preset == 3) th = histo.getMaxVarianceThreshold();
	// if (preset == 4) th = histo.getMinErrorThreshold();
	// if (preset == 5) th = histo.getMinFuzzinessThreshold();
	// if (preset == 6) th = histo.getModeThreshold(2); //power of 2
	// if (preset == 7) th = histo.getPTileThreshold(0.5); //0 bis 1
	// p0 = (int)th[0];
	// if (numBands >= 2) p2 = (int)th[1];
	// if (numBands == 3) p4 = (int)th[2];
	// if (p1 < p0) p1 = p0;
	// if (p3 < p2) p3 = p2;
	// if (p5 < p4) p5 = p4;
	// pbJAI.setParameter("ThresholdLow1", p0);
	// pbJAI.setParameter("ThresholdHigh1", p1);
	// pbJAI.setParameter("ThresholdLow2", p2);
	// pbJAI.setParameter("ThresholdHigh2", p3);
	// pbJAI.setParameter("ThresholdLow3", p4);
	// pbJAI.setParameter("ThresholdHigh3", p5);
	// Board.appendTexln("IqmOpThreshold: Thresholds: Band1:"+p0+"/"+p1+"   Band2:"+p2+"/"+p3+"  Band3:"+p4+"/"+p5);
	// return pbJAI;
	// }


	@Override
	public IResult run(IWorkPackage wp) {
		PlanarImage piOut = null;

		// clone the parameters and sources
		ParameterBlockIQM pb = wp.getParameters().clone();

		// get the sources from the work package
		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		pb.removeSources();
		pb.addSource(pi);

		piOut = JAI.create(getName(), pb, null);

		int binarize = pb.getIntParameter("Binarize");
		int numBands = piOut.getNumBands();

		if (isCancelled(getParentTask())){
			return null;
		}
		
		if ((binarize == 1) && (numBands > 1)) {
			// create a single banded grey image
			double[][] m = { { 1. / 3, 1. / 3, 1. / 3, 0 } };
			ParameterBlock pbJAI = new ParameterBlock();
			pbJAI.addSource(piOut);
			pbJAI.add(m);
			piOut = JAI.create("bandcombine", pbJAI, null);
		}
		ImageModel im = new ImageModel(piOut);
		im.setFileName(String.valueOf(pi.getProperty("file_name")));
		im.setModelName(String.valueOf(pi.getProperty("image_name")));
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpThresholdDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpThresholdDescriptor.TYPE;
	}
}
