package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpKMeansFuzzy.java
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


import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.image.ImageTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpKMeansFuzzyDescriptor;
import at.mug.iqm.img.bundle.jai.stuff.FuzzyCMeansImageClustering;
import at.mug.iqm.img.bundle.jai.stuff.FuzzyLICMeansImageClustering;

/**
 * This an implementation of a JAISTUFF algorithm by R.Santos
 * https://jaistuff.dev.java.net/algorithms.html
 * 
 * @author Ahammer, Kainz
 * @since   2009 05
 */
public class IqmOpKMeansFuzzy extends AbstractOperator {

	public IqmOpKMeansFuzzy() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
		this.setCancelable(true);
	}

	@Override
	public IResult run(IWorkPackage wp) {

		PlanarImage piOut = null;
		
		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		int method  = pb.getIntParameter("Method");  
		int nK      = pb.getIntParameter("nK");
		int itMax   = pb.getIntParameter("ItMax");
		double eps  = pb.getDoubleParameter("Eps");
		float fuzzy = (float) pb.getDoubleParameter("Fuzzy");
		int rank    = pb.getIntParameter("Rank");
		int out     = pb.getIntParameter("Out");

		int numBands       = pi.getNumBands();
		int width          = pi.getWidth();
		int height         = pi.getHeight();
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		String imgName     = String.valueOf(pi.getProperty("image_name"));
		String fileName     = String.valueOf(pi.getProperty("file_name"));

		// char initCh = "SDR".charAt(init);
		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		// RenderingHints rh = null;
		// ParameterBlock pb = new ParameterBlock();
		// System.out.println("I'm here1");

		FuzzyCMeansImageClustering clusterer0 = null;
		FuzzyLICMeansImageClustering clusterer1 = null;
		if (method == 0) { // FCM
			clusterer0 = new FuzzyCMeansImageClustering(pi, nK, itMax, fuzzy, eps);
			clusterer0.addProgressListener(this.getProgressListeners("operatorProgress")[0], "operatorProgress");
			clusterer0.setCancelable(this.isCancelable());
			clusterer0.setParentTask(this.getParentTask());
			clusterer0.run();
			pi = clusterer0.getRankedImage(rank - 1);

		}
		if (method == 1) { // FLICM
			clusterer1 = new FuzzyLICMeansImageClustering(pi, nK, itMax, fuzzy, eps);
			clusterer1.addProgressListener(this.getProgressListeners("operatorProgress")[0], "operatorProgress");
			clusterer1.setCancelable(this.isCancelable());
			clusterer1.setParentTask(this.getParentTask());
			clusterer1.run();
			pi = clusterer1.getRankedImage(rank - 1);

		}
		if (method == 2) { //

		}

		// Output Options
		if (out == 0)
			piOut = pi; // simply the calculated clusters
		if (out == 1) { // equidistant clusters
			// If RGB convert to Grey
			if (numBands == 3) {
				double[][] m = { { 1. / 3, 1. / 3, 1. / 3, 0 } };
				ParameterBlock pbBC = new ParameterBlock();
				pbBC.addSource(pi);
				pbBC.add(m);
				pi = JAI.create("bandcombine", pbBC, null);
			}

			float[][] clusterCenters = null;
			if (method == 0)
				clusterCenters = clusterer0.getClusterCenters();
			if (method == 1)
				clusterCenters = clusterer1.getClusterCenters();
			int[] greyClusterCenters = new int[nK];

			for (int i = 0; i < nK; i++) {
				for (int b = 0; b < numBands; b++) {
					greyClusterCenters[i] = greyClusterCenters[i] + Math.round(clusterCenters[i][b]);
				}
				greyClusterCenters[i] = Math.round((float) greyClusterCenters[i] / numBands);
				// System.out.println("IqmOpKMeans: greyClusterCenters[i]   " +
				// greyClusterCenters[i] );
			}

			// sort (sometimes it is necessary)
			Arrays.sort(greyClusterCenters);

			Raster ra = pi.getData();
			WritableRaster wr = ra.createCompatibleWritableRaster();
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					for (int i = 0; i < nK; i++) {
						// System.out.println("IqmOpKMeans: ra.getSample(x, y, 0)  "
						// + ra.getSample(x, y, 0));
						if (ra.getSample(x, y, 0) == greyClusterCenters[i]) {
							wr.setSample(x, y, 0, Math.round((float) ((typeGreyMax) / (nK - 1) * i)));
						}
					}
				}
			}

			TiledImage ti = new TiledImage(0, 0, width, height, pi.getTileGridXOffset(),
																pi.getTileGridYOffset(),
																pi.getSampleModel(),
																pi.getColorModel());
			ti.setData(wr);
			piOut = ti;
		}
		ImageModel im = new ImageModel(piOut);
		im.setModelName(imgName);
		im.setFileName(fileName);
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpKMeansFuzzyDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpKMeansFuzzyDescriptor.TYPE;
	}
	
}
