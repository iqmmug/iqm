package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpKMeans.java
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
import at.mug.iqm.img.bundle.descriptors.IqmOpKMeansDescriptor;
import at.mug.iqm.img.bundle.jai.stuff.KMeansImageClustering;

/**
 * This an implementation of a JAISTUFF algorithm by R.Santos
 * https://jaistuff.dev.java.net/algorithms.html
 * 
 * @author Ahammer, Kainz
 * @since   2009 05
 */
public class IqmOpKMeans extends AbstractOperator {

	public IqmOpKMeans() {
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
		
		int init = pb.getIntParameter("Init");
		int nK = pb.getIntParameter("nK");
		int itMax = pb.getIntParameter("ItMax");
		double eps = pb.getDoubleParameter("Eps");
		int out = pb.getIntParameter("Out");

		int numBands = pi.getNumBands();
		int width = pi.getWidth();
		int height = pi.getHeight();
		double typeGreyMax = ImageTools.getImgTypeGreyMax(pi);
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		char initCh = "SDR".charAt(init);
		// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
		// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		// RenderingHints rh = null;
		// ParameterBlock pb = new ParameterBlock();
		// System.out.println("I'm here1");
		KMeansImageClustering clusterer = new KMeansImageClustering(pi, nK,
				itMax, eps, initCh);
		clusterer.addProgressListener(this.getProgressListeners("operatorProgress")[0], "operatorProgress");
		clusterer.setCancelable(this.isCancelable());
		clusterer.setParentTask(this.getParentTask());
		clusterer.run();
		pi = clusterer.getOutput();
		// System.out.println("I'm here2");
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
			float[][] clusterCenters = clusterer.getClusterCenters();
			int[] greyClusterCenters = new int[nK];

			for (int i = 0; i < nK; i++) {
				for (int b = 0; b < numBands; b++) {
					greyClusterCenters[i] = greyClusterCenters[i]
							+ Math.round(clusterCenters[i][b]);
				}
				greyClusterCenters[i] = Math
						.round((float) greyClusterCenters[i] / numBands);
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
							wr.setSample(
									x,
									y,
									0,
									Math.round((float) ((typeGreyMax)
											/ (nK - 1) * i)));
						}
					}
				}
			}

			TiledImage ti = new TiledImage(0, 0, width, height,
					pi.getTileGridXOffset(), pi.getTileGridYOffset(),
					pi.getSampleModel(), pi.getColorModel());
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
			this.name = new IqmOpKMeansDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpKMeansDescriptor.TYPE;
	}
}
