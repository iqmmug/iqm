package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracHiguchi.java
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


import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.image.Raster;
import java.util.List;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.api.gui.ILookPanel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.commons.util.Higuchi;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracHiguchiDescriptor;

/**
 * @author Ahammer, Kainz
 * @since   2010 04
 */
public class IqmOpFracHiguchi extends AbstractOperator {

	public IqmOpFracHiguchi() {
		this.setCancelable(true);
	}

	private Vector<Double> removeZeroes(Vector<Double> data) {
		int length = data.size();
		Vector<Double> dataNew = new Vector<Double>();
		// dataNew = (Vector<Double>) data.clone(); //Essentiell!!!!!
		for (int i = 0; i < length; i++) {
			double value = data.get(i);
			if (value != 0) {
				dataNew.add(value);
			}
		}
		return dataNew;
	}

	/**
	 * This method gets the Polygon (shape) coordinates
	 * 
	 * @param roiShape
	 * @return vector of coordinates
	 */
	@SuppressWarnings("unused")
	private Vector<Float> getShapeCoordinates(ROIShape roiShape) {
		float[] ff = new float[6]; // for single segment data
		Vector<Float> coords = new Vector<Float>(); // coordinates for 3 Polygon
													// points
		int numSegment = -1;
		for (PathIterator pIt = (roiShape.getAsShape()).getPathIterator(null); !pIt
				.isDone();) {
			int type = pIt.currentSegment(ff);
			int n = 0;
			switch (type) {
			case PathIterator.SEG_MOVETO:
			case PathIterator.SEG_LINETO:
				numSegment += 1;
				n = 1;
				break;
			case PathIterator.SEG_QUADTO:
				n = 2;
				break;
			case PathIterator.SEG_CUBICTO:
				n = 3;
				break;
			default:
				// throw new InternalError();
			}
			for (int i = 0; i < n; i++) {
				// System.out.println("" + ff[i*2] + "  " +ff[i*2+1]);
				// //coordinates
			}
			coords.addElement(ff[0]); // only coordinates are taken
			coords.addElement(ff[1]);
			pIt.next();
		}
		return coords;
	}


	@Override
	public IResult run(IWorkPackage wp) {

		// PlanarImage piOut = null;

		// at first, delete all ROIs (spiral lines produces some)
		Application.getLook().getCurrentLookPanel().deleteSelectedROI();
		Application.getLook().getCurrentLookPanel().deleteAllROIShapesOnLayer();

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();

		int numK = pb.getIntParameter("NumK");
		int optAppend = pb.getIntParameter("Append");
		int optBack = pb.getIntParameter("Back");
		int optDx = pb.getIntParameter("Dx");
		int optDy = pb.getIntParameter("Dy");
		int regStart = pb.getIntParameter("RegStart");
		int regEnd = pb.getIntParameter("RegEnd");
		boolean optShowPlot = false;
		boolean optDeleteExistingPlot = false;
		if (pb.getIntParameter("ShowPlot") == 1)
			optShowPlot = true;
		if (pb.getIntParameter("DeleteExistingPlot") == 1)
			optDeleteExistingPlot = true;

		int numBands = pi.getData().getNumBands();
		// String type = IqmTools.getImgTyp(pi);
		// double typeGreyMax = IqmTools.getImgTypeGreyMax(pi);

		// int pixelSize = pi.getColorModel().getPixelSize();
		// String type = IqmTools.getCurrImgTyp(pixelSize, numBands);
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		// initialize table for Dimension data
		TableModel model = new TableModel("Higuchi Dimension [" + imgName + "]");
		// adding a lot of columns would be very slow due to active model
		model.addColumn("FileName");
		model.addColumn("ImageName");
		model.addColumn("Band");
		model.addColumn("RegStart");
		model.addColumn("RegEnd");
		model.addColumn("Append");
		model.addColumn("Back");

		for (int b = 0; b < numBands; b++) { // mehrere Bands
			model.addRow(new Object[] { fileName, imgName, b, regStart, regEnd,
					optAppend, optBack });
		}

		double[] dimX = new double[numBands];
		double[] dimY = new double[numBands];
		double[] dim = new double[numBands];
		double[] p3X = new double[numBands];
		double[] p3Y = new double[numBands];
		double[] p3 = new double[numBands];
		double[] p4X = new double[numBands];
		double[] p4Y = new double[numBands];
		double[] p4 = new double[numBands];
		double[] p5X = new double[numBands];
		double[] p5Y = new double[numBands];
		double[] p5 = new double[numBands];
		double[][] regDataDimX = new double[numK][numBands];
		double[][] regDataDimY = new double[numK][numBands];

		// values for Progress bar
		int prozX = 100;
		int prozY = 100;
		int offSetProzY = 0;
		if (optDx == 1) {
			prozY = 50;
			offSetProzY = 50;
		}
		if (optDy == 1) {
			prozX = 50;
		}
		// -------------------------------------------------------------------------------------------------------------
		if (optAppend == 0) { // Project
			if (optDx == 1) {
				Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																				// bands
				for (int b = 0; b < numBands; b++)
					dataY.add(new Vector<Double>()); // Initialize
				int[] pixel = new int[numBands];
				for (int x = 0; x < width; x++) {
					int proz = (x + 1) * prozX / width;

					this.fireProgressChanged(proz);
					
					if (isCancelled(getParentTask())){
						return null;
					}

					for (int b = 0; b < numBands; b++)
						(dataY.get(b)).add(0.0d);
					for (int y = 0; y < height; y++) {
						raster.getPixel(x, y, pixel);
						for (int b = 0; b < numBands; b++)
							dataY.get(b).set(
									x,
									dataY.get(b).get(x).doubleValue()
											+ pixel[b]);
					}
				}
				for (int b = 0; b < numBands; b++) {
					if (optBack == 1) { // exclusive zeroes
						Vector<Double> dataYNew = this.removeZeroes(dataY
								.get(b));
						// IqmTools.plotXY(dataYNew, true, "x", "y new");
						// System.out.println("IqmOpFracHiguchi: dataY_New.size(): "
						// + dataYNew.size());
						dataY.get(b).removeAllElements();
						// System.out.println("IqmOpFracHiguchi: dataY_New.size(): "
						// + dataYNew.size());
						for (int i = 0; i < dataYNew.size(); i++) {
							(dataY.get(b)).add(dataYNew.elementAt(i));
						}
						// IqmTools.plotXY(dataY.get(b), true, "x",
						// "y without zeros");
						// System.out.println("IqmOpFracHiguchi: dataY.get(b).size(): "
						// + dataY.get(b).size());
					}
					Higuchi hig = new Higuchi();
					Vector<Double> L = hig.calcLengths(dataY.get(b), numK);
					double[] result = hig.calcDimension(L, regStart, regEnd,
							imgName + "_Band" + b, optShowPlot,
							optDeleteExistingPlot);
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhx:"
							+ (-result[0]) + " StdDev:" + result[1] + " r2:"
							+ result[2] + " adj.r2:" + result[3]);
					dimX[b] = -result[0];
					p3X[b] = result[1];
					p4X[b] = result[2];
					p5X[b] = result[3];
					for (int i = 0; i < numK; i++) {
						regDataDimX[i][b] = L.get(i).doubleValue();
					}
				}
			} // optDx
			if (optDy == 1) {
				Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																				// bands
				for (int b = 0; b < numBands; b++)
					dataY.add(new Vector<Double>()); // Initialize
				int[] pixel = new int[numBands];
				for (int y = 0; y < height; y++) {
					int proz = (y + 1) * prozY / width + offSetProzY;

					this.fireProgressChanged(proz);
					
					if (isCancelled(getParentTask())){
						return null;
					}

					for (int b = 0; b < numBands; b++)
						(dataY.get(b)).add(0.0d);
					for (int x = 0; x < width; x++) {
						raster.getPixel(x, y, pixel);
						for (int b = 0; b < numBands; b++)
							dataY.get(b).set(
									y,
									dataY.get(b).get(y).doubleValue()
											+ pixel[b]);
					}
				}
				for (int b = 0; b < numBands; b++) {
					if (optBack == 1) { // exclusive zeroes
						Vector<Double> dataYNew = this.removeZeroes(dataY
								.get(b));
						// IqmTools.plotXY(dataYNew, true, "x", "y new");
						// System.out.println("IqmOpFracHiguchi: dataY_New.size(): "
						// + dataYNew.size());
						dataY.get(b).removeAllElements();
						// System.out.println("IqmOpFracHiguchi: dataY_New.size(): "
						// + dataYNew.size());
						for (int i = 0; i < dataYNew.size(); i++) {
							(dataY.get(b)).add(dataYNew.elementAt(i));
						}
						// IqmTools.plotXY(dataY.get(b), true, "x",
						// "y without zeros");
						// System.out.println("IqmOpFracHiguchi: dataY.get(b).size(): "
						// + dataY.get(b).size());
					}
					Higuchi hig = new Higuchi();
					Vector<Double> L = hig.calcLengths(dataY.get(b), numK);
					double[] result = hig.calcDimension(L, regStart, regEnd,
							imgName + "_Band" + b, optShowPlot,
							optDeleteExistingPlot);
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhy:"
							+ (-result[0]) + " StdDev:" + result[1] + " r2:"
							+ result[2] + " adj.r2:" + result[3]);
					dimY[b] = -result[0];
					p3Y[b] = result[1];
					p4Y[b] = result[2];
					p5Y[b] = result[3];
					for (int i = 0; i < numK; i++) {
						regDataDimY[i][b] = L.get(i).doubleValue();
					}
				}
			} // optDy
		} // optAppend = 0 Project
			// -------------------------------------------------------------------------------------------------------------
		if (optAppend == 1) { // Separate
			if (optDx == 1) {
				Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				int[] pixel = new int[numBands];
				Vector<Vector<Double>> dimXVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p3XVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p4XVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p5XVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				for (int b = 0; b < numBands; b++) {
					dimXVec.add(new Vector<Double>()); // Initialize
					p3XVec.add(new Vector<Double>()); // Initialize
					p4XVec.add(new Vector<Double>()); // Initialize
					p5XVec.add(new Vector<Double>()); // Initialize
				}
				for (int y = 0; y < height; y++) {
					int proz = (y + 1) * prozX / height;

					this.fireProgressChanged(proz);
					if (isCancelled(getParentTask())){
						return null;
					}

					// Vector<Double> dataY = new Vector<Double>();
					Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																					// bands
					for (int b = 0; b < numBands; b++)
						dataY.add(new Vector<Double>()); // Initialize
					for (int x = 0; x < width; x++) {
						raster.getPixel(x, y, pixel);
						for (int b = 0; b < numBands; b++)
							dataY.get(b).add((double) pixel[b]);
					}
					for (int b = 0; b < numBands; b++) {
						if (optBack == 1) { // exclusive zeroes
							Vector<Double> dataYNew = this.removeZeroes(dataY
									.get(b));
							dataY.get(b).removeAllElements();
							for (int i = 0; i < dataYNew.size(); i++) {
								(dataY.get(b)).add(dataYNew.elementAt(i));
							}
						}
						if (dataY.get(b).size() > numK * 2) { // only data
																// series which
																// are large
																// enough
							Higuchi hig = new Higuchi();
							// IqmTools.plotXY(dataY.get(b), true, "x",
							// "y separate");
							Vector<Double> L = hig.calcLengths(dataY.get(b),
									numK);
							double[] result = hig.calcDimension(L, regStart,
									regEnd, imgName + "_Band" + b, optShowPlot,
									optDeleteExistingPlot);
							// BoardJ.appendTexln("IqmOpFracHiguchi:  Dhx:" +
							// (-result[0])+ " StdDev:" + result[1] + " r2:" +
							// result[2] + " adj.r2:"+result[3]);

							if (result[2] > 0.90) { // only higher r2
								dimXVec.get(b).add(-result[0]);
								p3XVec.get(b).add(result[1]);
								p4XVec.get(b).add(result[2]);
								p5XVec.get(b).add(result[3]);
								for (int i = 0; i < numK; i++) {
									regDataDimX[i][b] = regDataDimX[i][b]
											+ L.get(i).doubleValue();
								}
							}
						}
					}
				}
				// mean values
				for (int b = 0; b < numBands; b++) {
					for (int y = 0; y < dimXVec.get(b).size(); y++) {
						dimX[b] = dimX[b] + dimXVec.get(b).get(y);
						p3X[b] = p3X[b] + p3XVec.get(b).get(y);
						p4X[b] = p4X[b] + p4XVec.get(b).get(y);
						p5X[b] = p5X[b] + p5XVec.get(b).get(y);
					}
					// System.out.println("IqmOpFracHiguchi: dimXVec.get(b).size() "+dimXVec.get(b).size());
					dimX[b] = dimX[b] / dimXVec.get(b).size();
					p3X[b] = p3X[b] / p3XVec.get(b).size();
					p4X[b] = p4X[b] / p4XVec.get(b).size();
					p5X[b] = p5X[b] / p5XVec.get(b).size();
					for (int i = 0; i < numK; i++) {
						regDataDimX[i][b] = regDataDimX[i][b]
								/ dimXVec.get(b).size();
					}
					BoardPanel
							.appendTextln("IqmOpFracHiguchi: Calulated number of series along x direction: "
									+ dimXVec.get(b).size());
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhx:" + dimX[b]
							+ " StdDev:" + p3X[b] + " r2:" + p4X[b]
							+ " adj.r2:" + p5X[b]);
				} // b bands

			} // optDx
			if (optDy == 1) {
				Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				// int normY = imgWidth;
				int[] pixel = new int[numBands];
				Vector<Vector<Double>> dimYVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p3YVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p4YVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p5YVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				for (int b = 0; b < numBands; b++) {
					dimYVec.add(new Vector<Double>()); // Initialize
					p3YVec.add(new Vector<Double>()); // Initialize
					p4YVec.add(new Vector<Double>()); // Initialize
					p5YVec.add(new Vector<Double>()); // Initialize
				}
				for (int x = 0; x < width; x++) {
					int proz = (x + 1) * prozY / width + offSetProzY;

					this.fireProgressChanged(proz);
					if (isCancelled(getParentTask())){
						return null;
					}

					// Vector<Double> dataY = new Vector<Double>();
					Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																					// bands
					for (int b = 0; b < numBands; b++)
						dataY.add(new Vector<Double>()); // Initialize
					for (int y = 0; y < height; y++) {
						raster.getPixel(x, y, pixel);
						for (int b = 0; b < numBands; b++)
							dataY.get(b).add((double) pixel[b]);
					}
					for (int b = 0; b < numBands; b++) {
						if (optBack == 1) { // exclusive zeroes
							Vector<Double> dataYNew = this.removeZeroes(dataY
									.get(b));
							dataY.get(b).removeAllElements();
							for (int i = 0; i < dataYNew.size(); i++) {
								(dataY.get(b)).add(dataYNew.elementAt(i));
							}
						}
						if (dataY.get(b).size() > numK * 2) {
							Higuchi hig = new Higuchi();
							Vector<Double> L = hig.calcLengths(dataY.get(b),
									numK);
							double[] result = hig.calcDimension(L, regStart,
									regEnd, imgName + "_Band" + b, optShowPlot,
									optDeleteExistingPlot);
							// BoardJ.appendTexln("IqmOpFracHiguchi:  Dhx:" +
							// (-result[0])+ " StdDev:" + result[1] + " r2:" +
							// result[2] + " adj.r2:"+result[3]);
							if (result[2] > 0.90) { // only higher r2
								dimYVec.get(b).add(-result[0]);
								p3YVec.get(b).add(result[1]);
								p4YVec.get(b).add(result[2]);
								p5YVec.get(b).add(result[3]);
								for (int i = 0; i < numK; i++) {
									regDataDimY[i][b] = regDataDimY[i][b]
											+ L.get(i).doubleValue();
								}
							}
						}
					}
				}
				// mean
				for (int b = 0; b < numBands; b++) {
					for (int x = 0; x < dimYVec.get(b).size(); x++) {
						dimY[b] = dimY[b] + dimYVec.get(b).get(x);
						p3Y[b] = p3Y[b] + p3YVec.get(b).get(x);
						p4Y[b] = p4Y[b] + p4YVec.get(b).get(x);
						p5Y[b] = p5Y[b] + p5YVec.get(b).get(x);
					}
					dimY[b] = dimY[b] / dimYVec.get(b).size();
					p3Y[b] = p3Y[b] / p3YVec.get(b).size();
					p4Y[b] = p4Y[b] / p4YVec.get(b).size();
					p5Y[b] = p5Y[b] / p5YVec.get(b).size();
					for (int i = 0; i < numK; i++) {
						regDataDimY[i][b] = regDataDimY[i][b]
								/ dimYVec.get(b).size();
					}
					BoardPanel
							.appendTextln("IqmOpFracHiguchi: Calulated number of series along y direction: "
									+ dimYVec.get(b).size());
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhy:" + dimY[b]
							+ " StdDev:" + p3Y[b] + " r2:" + p4Y[b]
							+ " adj.r2:" + p5Y[b]);
				}
			} // optDy
		} // optAppend = 1 Separate
			// -------------------------------------------------------------------------------------------------------------
		if (optAppend == 2) { // Meander
			if (optDx == 1) {
				Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				// Vector<Double> dataY = new
				// Vector<Double>(imgWidth*imgHeight);
				Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																				// bands
				for (int b = 0; b < numBands; b++)
					dataY.add(new Vector<Double>()); // Initialize
				int[] pixel = new int[numBands];
				boolean even = false;
				for (int y = 0; y < height; y++) {
					int proz = (y + 1) * prozX / height;
					this.fireProgressChanged(proz);
					if (isCancelled(getParentTask())){
						return null;
					}
					if (y % 2 == 0)
						even = true;
					if (y % 2 != 0)
						even = false;
					// System.out.println("IqmOpFracHiguchi: y: "+ y+ " even");
					if (even) {
						for (int x = 0; x < width; x++) { // even
							raster.getPixel(x, y, pixel);
							for (int b = 0; b < numBands; b++)
								dataY.get(b).add((double) pixel[b]);
						}
					} else {
						for (int x = (width - 1); x >= 0; x--) { // odd
							raster.getPixel(x, y, pixel);
							for (int b = 0; b < numBands; b++)
								dataY.get(b).add((double) pixel[b]);
						}
					}
				}
				for (int b = 0; b < numBands; b++) {
					if (optBack == 1) { // exclusive zeroes
						Vector<Double> dataYNew = this.removeZeroes(dataY
								.get(b));
						dataY.get(b).removeAllElements();
						for (int i = 0; i < dataYNew.size(); i++) {
							(dataY.get(b)).add(dataYNew.elementAt(i));
						}
					}
					// IqmTools.plotXY(dataY.get(b), true, "x", "y new");
					Higuchi hig = new Higuchi();
					Vector<Double> L = hig.calcLengths(dataY.get(b), numK);
					double[] result = hig.calcDimension(L, regStart, regEnd,
							imgName + "_Band" + b, optShowPlot,
							optDeleteExistingPlot);
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhx:"
							+ (-result[0]) + " StdDev:" + result[1] + " r2:"
							+ result[2] + " adj.r2:" + result[3]);
					dimX[b] = -result[0];
					p3X[b] = result[1];
					p4X[b] = result[2];
					p5X[b] = result[3];
					for (int i = 0; i < numK; i++) {
						regDataDimX[i][b] = L.get(i).doubleValue();
					}
				} // b bands
			} // optDx
			if (optDy == 1) {
				Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				// Vector<Double> dataY = new Vector<Double>();
				Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																				// bands
				for (int b = 0; b < numBands; b++)
					dataY.add(new Vector<Double>()); // Initialize
				int[] pixel = new int[numBands];
				boolean even = false;
				for (int x = 0; x < width; x++) {
					int proz = (x + 1) * prozY / width + offSetProzY;
					this.fireProgressChanged(proz);
					if (isCancelled(getParentTask())){
						return null;
					}
					if (x % 2 == 0)
						even = true;
					if (x % 2 != 0)
						even = false;
					// System.out.println("IamFracHiguchi: x: "+ x+ " even");
					if (even) {
						for (int y = 0; y < height; y++) { // even
							raster.getPixel(x, y, pixel);
							for (int b = 0; b < numBands; b++)
								dataY.get(b).add((double) pixel[b]);
						}
					} else {
						for (int y = (height - 1); y >= 0; y--) { // odd
							raster.getPixel(x, y, pixel);
							for (int b = 0; b < numBands; b++)
								dataY.get(b).add((double) pixel[b]);
						}
					}
				}
				for (int b = 0; b < numBands; b++) {
					// IqmTools.plotXY( dataY, true, "x", "meander y");
					if (optBack == 1) { // exclusive zeroes
						Vector<Double> dataYNew = this.removeZeroes(dataY
								.get(b));
						dataY.get(b).removeAllElements();
						for (int i = 0; i < dataYNew.size(); i++) {
							(dataY.get(b)).add(dataYNew.elementAt(i));
						}
					}
					Higuchi hig = new Higuchi();
					Vector<Double> L = hig.calcLengths(dataY.get(b), numK);
					double[] result = hig.calcDimension(L, regStart, regEnd,
							imgName + "_Band" + b, optShowPlot,
							optDeleteExistingPlot);
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhy:"
							+ (-result[0]) + " StdDev:" + result[1] + " r2:"
							+ result[2] + " adj.r2:" + result[3]);
					dimY[b] = -result[0];
					p3Y[b] = result[1];
					p4Y[b] = result[2];
					p5Y[b] = result[3];
					for (int i = 0; i < numK; i++) {
						regDataDimY[i][b] = L.get(i).doubleValue();
					}
				} // b bands
			} // optDy
		} // optAppend = 2 Meander
			// -------------------------------------------------------------------------------------------------------------
		if (optAppend == 3) { // Contiguous
			if (optDx == 1) {
				Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				// Vector<Double> dataY = new Vector<Double>();
				Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																				// bands
				for (int b = 0; b < numBands; b++)
					dataY.add(new Vector<Double>()); // Initialize
				int[] pixel = new int[numBands];
				for (int y = 0; y < height; y++) {
					int proz = (y + 1) * prozX / height;
					this.fireProgressChanged(proz);
					if (isCancelled(getParentTask())){
						return null;
					}
					for (int x = 0; x < width; x++) {
						raster.getPixel(x, y, pixel);
						for (int b = 0; b < numBands; b++)
							dataY.get(b).add((double) pixel[b]);
					}
				}
				for (int b = 0; b < numBands; b++) {
					if (optBack == 1) { // exclusive zeroes
						Vector<Double> dataYNew = this.removeZeroes(dataY
								.get(b));
						dataY.get(b).removeAllElements();
						for (int i = 0; i < dataYNew.size(); i++) {
							(dataY.get(b)).add(dataYNew.elementAt(i));
						}
					}
					Higuchi hig = new Higuchi();
					Vector<Double> L = hig.calcLengths(dataY.get(b), numK);
					double[] result = hig.calcDimension(L, regStart, regEnd,
							imgName + "_Band" + b, optShowPlot,
							optDeleteExistingPlot);
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhx:"
							+ (-result[0]) + " StdDev:" + result[1] + " r2:"
							+ result[2] + " adj.r2:" + result[3]);
					dimX[b] = -result[0];
					p3X[b] = result[1];
					p4X[b] = result[2];
					p5X[b] = result[3];
					for (int i = 0; i < numK; i++) {
						regDataDimX[i][b] = L.get(i).doubleValue();
					}
				} // b bands
			} // optDx
			if (optDy == 1) {
				Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				// Vector<Double> dataY = new Vector<Double>();
				Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																				// bands
				for (int b = 0; b < numBands; b++)
					dataY.add(new Vector<Double>()); // Initialize
				int[] pixel = new int[numBands];
				for (int x = 0; x < width; x++) {
					int proz = (x + 1) * prozY / width + offSetProzY;
					this.fireProgressChanged(proz);
					if (isCancelled(getParentTask())){
						return null;
					}
					for (int y = 0; y < height; y++) {
						raster.getPixel(x, y, pixel);
						for (int b = 0; b < numBands; b++)
							dataY.get(b).add((double) pixel[b]);
					}
				}
				for (int b = 0; b < numBands; b++) {
					// IqmTools.plotXY( dataY, true, "x", "contiguous y");
					if (optBack == 1) { // exclusive zeroes
						Vector<Double> dataYNew = this.removeZeroes(dataY
								.get(b));
						dataY.get(b).removeAllElements();
						for (int i = 0; i < dataYNew.size(); i++) {
							(dataY.get(b)).add(dataYNew.elementAt(i));
						}
					}
					Higuchi hig = new Higuchi();
					Vector<Double> L = hig.calcLengths(dataY.get(b), numK);
					double[] result = hig.calcDimension(L, regStart, regEnd,
							imgName + "_Band" + b, optShowPlot,
							optDeleteExistingPlot);
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhy:"
							+ (-result[0]) + " StdDev:" + result[1] + " r2:"
							+ result[2] + " adj.r2:" + result[3]);
					dimY[b] = -result[0];
					p3Y[b] = result[1];
					p4Y[b] = result[2];
					p5Y[b] = result[3];
					for (int i = 0; i < numK; i++) {
						regDataDimY[i][b] = L.get(i).doubleValue();
					}
				} // b bands
			} // optDy
		} // optAppend = 3 Contiguous
			// -------------------------------------------------------------------------------------------------------------
		if (optAppend == 4) { // 180 radial lines
			int nLines = 180;
			if (optDx == 1) {// data from left to right
				// Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				Vector<Vector<Double>> dimXVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p3XVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p4XVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p5XVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				for (int b = 0; b < numBands; b++) {
					dimXVec.add(new Vector<Double>()); // Initialize
					p3XVec.add(new Vector<Double>()); // Initialize
					p4XVec.add(new Vector<Double>()); // Initialize
					p5XVec.add(new Vector<Double>()); // Initialize
				}
				// define angles
				double[] angles = new double[nLines];
				for (int i = 0; i < angles.length; i++)
					angles[i] = (i * Math.PI / (nLines - 1) - (Math.PI / 2.0)); // -pi/2,...,0,...+pi/2
				for (int a = 0; a < nLines; a++) { // loop through angles
					int proz = (a + 1) * prozX / height;
					this.fireProgressChanged(proz);
					if (isCancelled(getParentTask())){
						return null;
					}
					// Vector<Double> dataY = new Vector<Double>();
					Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																					// bands
					for (int b = 0; b < numBands; b++)
						dataY.add(new Vector<Double>()); // Initialize

					double slope = Math.tan(angles[a]);

					// set start point x1,y1 and end point x2,y2
					double offSetX = Math.floor((width - 1) / 2.0);
					double offSetY = Math.floor((height - 1) / 2.0);
					double x1;
					double y1;
					double x2;
					double y2;

					if (angles[a] == +(Math.PI / 2.0)) {
						x1 = 0;
						y1 = -offSetY;
						x2 = 0;
						y2 = +offSetY;
					} else if (angles[a] == -(Math.PI / 2.0)) {
						x1 = 0;
						y1 = offSetY;
						x2 = 0;
						y2 = -offSetY;
					} else if (angles[a] == 0) {
						x1 = -offSetX;
						y1 = 0;
						x2 = offSetX;
						y2 = 0;
					} else {
						x1 = -offSetX;
						y1 = (int) (slope * x1);

						x2 = offSetX;
						y2 = (int) (slope * x2);

						while (x1 + offSetX >= width) { // out of image
							x1 = x1 - 1;
							y1 = (int) (slope * x1);
						}
						while (x1 + offSetX < 0) { // out of image
							x1 = x1 + 1;
							y1 = (int) (slope * x1);
						}
						while (x2 + offSetX >= width) { // out of image
							x2 = x2 - 1;
							y2 = (int) (slope * x2);
						}
						while (x2 + offSetX < 0) { // out of image
							x2 = x2 + 1;
							y2 = (int) (slope * x2);
						}
						while (y1 + offSetY >= height) { // out of image
							// System.out.println("IqmOpFracHiguchi: x1: " + x1+
							// "  y1: "+ y1);
							x1 = x1 + 1;
							y1 = (int) (slope * x1);
						}
						while (y1 + offSetY < 0) { // out of image
							// System.out.println("IqmOpFracHiguchi: x1: " + x1+
							// "  y1: "+ y1);
							x1 = x1 + 1;
							y1 = (int) (slope * x1);
						}
						while (y2 + offSetY >= height) {
							// System.out.println("IqmOpFracHiguchi: x2: " + x2+
							// "  y2: "+ y2);
							x2 = x2 - 1;
							y2 = (int) (slope * x2);
						}
						while (y2 + offSetY < 0) {
							// System.out.println("IqmOpFracHiguchi: x2: " + x2+
							// "  y2: "+ y2);
							x2 = x2 - 1;
							y2 = (int) (slope * x2);
						}
					}
					// System.out.println("IqmOpFracHiguchi: x1: " + x1+
					// "  y1: "+ y1 + "    x2: " + x2+ "  y2: "+ y2);
					if (x1 + offSetX >= width)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: x1 too high, x1: "
										+ x1 + offSetX);
					if (x1 + offSetX < 0)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: x1 too low,  x1: "
										+ x1 + offSetX);
					if (x2 + offSetX >= width)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: x2 too high, x2: "
										+ x2 + offSetX);
					if (x2 + offSetX < 0)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: x2 too low,  x2: "
										+ x2 + offSetX);
					if (y1 + offSetY >= height)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: y1 too high, y1: "
										+ y1 + offSetY);
					if (y1 + offSetY < 0)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: y1 too low,  y1: "
										+ y1 + offSetY);
					if (y2 + offSetY >= height)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: y2 too high, y2: "
										+ y2 + offSetY);
					if (y2 + offSetY < 0)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: y2 too low,  y2: "
										+ y2 + offSetY);

					Image image = pi.getAsBufferedImage();
					ImagePlus imgP = new ImagePlus("", image);
					ImageProcessor proc = imgP.getProcessor();
					proc.setCalibrationTable(null);
					// System.out.println("IqmOpFracHiguchi: x1+offSetX:" +
					// (x1+offSetX) +"  y1+ offSetY :"+ (y1 +offSetY)+
					// "    x2+offSetX: " + (x2+offSetX)+ "  y2+offSetY: "+
					// (y2+offSetY));
					double[] linePoly = proc.getLine(x1 + offSetX,
							y1 + offSetY, x2 + offSetX, y2 + offSetY);
					// proc.drawLine((int)(x1+offSetX), (int)(y1+offSetY),
					// (int)(x2+offSetX), (int)(y2+offSetY));
					for (int p = 0; p < linePoly.length; p++) {
						for (int b = 0; b < numBands; b++) {
							dataY.get(b).add(linePoly[p]);
						}
					}

					// get plots of radial lines
					// Vector<Double> dataXPlot = new Vector<Double>(width);
					// //Vector<Double> dataYPlot = new Vector<Double>(width);
					// for (int iii = 0; iii < dataY.get(0).size(); iii++){
					// dataXPlot.add((double)(iii+1));
					// }
					// boolean isLineVisible = true;
					// String plotTitle = "Radial lines";
					// String xTitle = "pixel";
					// String yTitle = "#";
					// IqmTools.displayPlotXY(dataXPlot, dataY.get(0),
					// isLineVisible, plotTitle, xTitle, yTitle);
					for (int b = 0; b < numBands; b++) {
						if (optBack == 1) { // exclusive zeroes
							Vector<Double> dataYNew = this.removeZeroes(dataY
									.get(b));
							dataY.get(b).removeAllElements();
							for (int i = 0; i < dataYNew.size(); i++) {
								(dataY.get(b)).add(dataYNew.elementAt(i));
							}
						}
						if (dataY.get(b).size() > numK * 2) { // only data
																// series which
																// are large
																// enough
							Higuchi hig = new Higuchi();
							// IqmTools.plotXY(dataY.get(b), true, "x",
							// "y separate");
							Vector<Double> L = hig.calcLengths(dataY.get(b),
									numK);
							double[] result = hig.calcDimension(L, regStart,
									regEnd, imgName + "_Band" + b, optShowPlot,
									optDeleteExistingPlot);
							// BoardJ.appendTexln("IqmOpFracHiguchi:  Dhx:" +
							// (-result[0])+ " StdDev:" + result[1] + " r2:" +
							// result[2] + " adj.r2:"+result[3]);

							if (result[2] > 0.90) { // only higher r2
								dimXVec.get(b).add(-result[0]);
								p3XVec.get(b).add(result[1]);
								p4XVec.get(b).add(result[2]);
								p5XVec.get(b).add(result[3]);
								for (int i = 0; i < numK; i++) {
									regDataDimX[i][b] = regDataDimX[i][b]
											+ L.get(i).doubleValue();
								}
							}
						}
					}
				}
				// mean values
				for (int b = 0; b < numBands; b++) {
					for (int y = 0; y < dimXVec.get(b).size(); y++) {
						dimX[b] = dimX[b] + dimXVec.get(b).get(y);
						p3X[b] = p3X[b] + p3XVec.get(b).get(y);
						p4X[b] = p4X[b] + p4XVec.get(b).get(y);
						p5X[b] = p5X[b] + p5XVec.get(b).get(y);
					}
					// System.out.println("IqmOpFracHiguchi: dimXVec.get(b).size() "+dimXVec.get(b).size());
					dimX[b] = dimX[b] / dimXVec.get(b).size();
					p3X[b] = p3X[b] / p3XVec.get(b).size();
					p4X[b] = p4X[b] / p4XVec.get(b).size();
					p5X[b] = p5X[b] / p5XVec.get(b).size();
					for (int i = 0; i < numK; i++) {
						regDataDimX[i][b] = regDataDimX[i][b]
								/ dimXVec.get(b).size();
					}
					BoardPanel
							.appendTextln("IqmOpFracHiguchi: Calulated number of radial lines from left to right: "
									+ dimXVec.get(b).size());
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhx:" + dimX[b]
							+ " StdDev:" + p3X[b] + " r2:" + p4X[b]
							+ " adj.r2:" + p5X[b]);
				} // b bands

			} // optDx
			if (optDy == 1) { // data from right to left
				// Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				// int normY = imgWidth;
				Vector<Vector<Double>> dimYVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p3YVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p4YVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				Vector<Vector<Double>> p5YVec = new Vector<Vector<Double>>(); // for
																				// bands
																				// and
																				// separated
																				// results
				for (int b = 0; b < numBands; b++) {
					dimYVec.add(new Vector<Double>()); // Initialize
					p3YVec.add(new Vector<Double>()); // Initialize
					p4YVec.add(new Vector<Double>()); // Initialize
					p5YVec.add(new Vector<Double>()); // Initialize
				}
				// define angles
				double[] angles = new double[nLines];
				for (int i = 0; i < angles.length; i++)
					angles[i] = (i * Math.PI / (nLines - 1) - (Math.PI / 2.0)); // -pi/2,...,0,...+pi/2
				for (int a = 0; a < nLines; a++) { // loop through angles
					int proz = (a + 1) * prozY / height + offSetProzY;
					this.fireProgressChanged(proz);
					if (isCancelled(getParentTask())){
						return null;
					}
					// Vector<Double> dataY = new Vector<Double>();
					Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																					// bands
					for (int b = 0; b < numBands; b++)
						dataY.add(new Vector<Double>()); // Initialize

					double slope = Math.tan(angles[a]);

					// set start point x1,y1 and end point x2,y2
					double offSetX = Math.floor((width - 1) / 2.0);
					double offSetY = Math.floor((height - 1) / 2.0);
					double x1;
					double y1;
					double x2;
					double y2;

					if (angles[a] == +(Math.PI / 2.0)) {
						x1 = 0;
						y1 = offSetY;
						x2 = 0;
						y2 = -offSetY;
					} else if (angles[a] == -(Math.PI / 2.0)) {
						x1 = 0;
						y1 = -offSetY;
						x2 = 0;
						y2 = +offSetY;
					} else if (angles[a] == 0) {
						x1 = offSetX;
						y1 = 0;
						x2 = -offSetX;
						y2 = 0;
					} else {
						x1 = offSetX;
						y1 = (int) (slope * x1);

						x2 = -offSetX;
						y2 = (int) (slope * x2);

						while (x1 + offSetX >= width) { // out of image
							x1 = x1 - 1;
							y1 = (int) (slope * x1);
						}
						while (x1 + offSetX < 0) { // out of image
							x1 = x1 + 1;
							y1 = (int) (slope * x1);
						}
						while (x2 + offSetX >= width) { // out of image
							x2 = x2 - 1;
							y2 = (int) (slope * x2);
						}
						while (x2 + offSetX < 0) { // out of image
							x2 = x2 + 1;
							y2 = (int) (slope * x2);
						}
						while (y1 + offSetY >= height) { // out of image
							// System.out.println("IqmOpFracHiguchi: x1: " + x1+
							// "  y1: "+ y1);
							x1 = x1 - 1;
							y1 = (int) (slope * x1);
						}
						while (y1 + offSetY < 0) { // out of image
							// System.out.println("IqmOpFracHiguchi: x1: " + x1+
							// "  y1: "+ y1);
							x1 = x1 - 1;
							y1 = (int) (slope * x1);
						}
						while (y2 + offSetY >= height) {
							// System.out.println("IqmOpFracHiguchi: x2: " + x2+
							// "  y2: "+ y2);
							x2 = x2 + 1;
							y2 = (int) (slope * x2);
						}
						while (y2 + offSetY < 0) {
							// System.out.println("IqmOpFracHiguchi: x2: " + x2+
							// "  y2: "+ y2);
							x2 = x2 + 1;
							y2 = (int) (slope * x2);
						}
					}
					// System.out.println("IqmOpFracHiguchi: x1: " + x1+
					// "  y1: "+ y1 + "    x2: " + x2+ "  y2: "+ y2);
					if (x1 + offSetX >= width)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: x1 too high, x1: "
										+ x1 + offSetX);
					if (x1 + offSetX < 0)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: x1 too low,  x1: "
										+ x1 + offSetX);
					if (x2 + offSetX >= width)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: x2 too high, x2: "
										+ x2 + offSetX);
					if (x2 + offSetX < 0)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: x2 too low,  x2: "
										+ x2 + offSetX);
					if (y1 + offSetY >= height)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: y1 too high, y1: "
										+ y1 + offSetY);
					if (y1 + offSetY < 0)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: y1 too low,  y1: "
										+ y1 + offSetY);
					if (y2 + offSetY >= height)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: y2 too high, y2: "
										+ y2 + offSetY);
					if (y2 + offSetY < 0)
						BoardPanel
								.appendTextln("IqmOpFracHiguchi: ERROR: y2 too low,  y2: "
										+ y2 + offSetY);

					Image image = pi.getAsBufferedImage();
					ImagePlus imgP = new ImagePlus("", image);
					ImageProcessor proc = imgP.getProcessor();
					// System.out.println("IqmOpFracHiguchi: x1+offSetX:" +
					// (x1+offSetX) +"  y1+ offSetY :"+ (y1 +offSetY)+
					// "    x2+offSetX: " + (x2+offSetX)+ "  y2+offSetY: "+
					// (y2+offSetY));
					double[] linePoly = proc.getLine(x1 + offSetX,
							y1 + offSetY, x2 + offSetX, y2 + offSetY);
					// proc.drawLine((int)(x1+offSetX), (int)(y1+offSetY),
					// (int)(x2+offSetX), (int)(y2+offSetY));
					for (int p = 0; p < linePoly.length; p++) {
						for (int b = 0; b < numBands; b++) {
							dataY.get(b).add(linePoly[p]);
						}
					}
					// get plots of radial lines
					// Vector<Double> dataXPlot = new Vector<Double>(width);
					// //Vector<Double> dataYPlot = new Vector<Double>(width);
					// for (int iii = 0; iii < dataY.get(0).size(); iii++){
					// dataXPlot.add((double)(iii+1));
					// }
					// boolean isLineVisible = true;
					// String plotTitle = "Radial lines";
					// String xTitle = "pixel";
					// String yTitle = "#";
					// IqmTools.displayPlotXY(dataXPlot, dataY.get(0),
					// isLineVisible, plotTitle, xTitle, yTitle);

					for (int b = 0; b < numBands; b++) {
						if (optBack == 1) { // exclusive zeroes
							Vector<Double> dataYNew = this.removeZeroes(dataY
									.get(b));
							dataY.get(b).removeAllElements();
							for (int i = 0; i < dataYNew.size(); i++) {
								(dataY.get(b)).add(dataYNew.elementAt(i));
							}
						}
						if (dataY.get(b).size() > numK * 2) {
							Higuchi hig = new Higuchi();
							Vector<Double> L = hig.calcLengths(dataY.get(b),
									numK);
							double[] result = hig.calcDimension(L, regStart,
									regEnd, imgName + "_Band" + b, optShowPlot,
									optDeleteExistingPlot);
							// BoardJ.appendTexln("IqmOpFracHiguchi:  Dhx:" +
							// (-result[0])+ " StdDev:" + result[1] + " r2:" +
							// result[2] + " adj.r2:"+result[3]);
							if (result[2] > 0.90) { // only higher r2
								dimYVec.get(b).add(-result[0]);
								p3YVec.get(b).add(result[1]);
								p4YVec.get(b).add(result[2]);
								p5YVec.get(b).add(result[3]);
								for (int i = 0; i < numK; i++) {
									regDataDimY[i][b] = regDataDimY[i][b]
											+ L.get(i).doubleValue();
								}
							}
						}
					}
				}
				// mean
				for (int b = 0; b < numBands; b++) {
					for (int x = 0; x < dimYVec.get(b).size(); x++) {
						dimY[b] = dimY[b] + dimYVec.get(b).get(x);
						p3Y[b] = p3Y[b] + p3YVec.get(b).get(x);
						p4Y[b] = p4Y[b] + p4YVec.get(b).get(x);
						p5Y[b] = p5Y[b] + p5YVec.get(b).get(x);
					}
					dimY[b] = dimY[b] / dimYVec.get(b).size();
					p3Y[b] = p3Y[b] / p3YVec.get(b).size();
					p4Y[b] = p4Y[b] / p4YVec.get(b).size();
					p5Y[b] = p5Y[b] / p5YVec.get(b).size();
					for (int i = 0; i < numK; i++) {
						regDataDimY[i][b] = regDataDimY[i][b]
								/ dimYVec.get(b).size();
					}
					BoardPanel
							.appendTextln("IqmOpFracHiguchi: Calulated number of radial lines from right to left: "
									+ dimYVec.get(b).size());
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhy:" + dimY[b]
							+ " StdDev:" + p3Y[b] + " r2:" + p4Y[b]
							+ " adj.r2:" + p5Y[b]);
				}
			} // optDy
		} // optAppend = 4 180 radial lines
			// -------------------------------------------------------------------------------------------------------------
		if (optAppend == 5) { // Spiral

			int w = 10; // number of turns
			if (optDx == 1) {
				Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				// Vector<Double> dataY = new Vector<Double>();
				Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																				// bands
				for (int b = 0; b < numBands; b++)
					dataY.add(new Vector<Double>()); // Initialize
				int[] pixel = new int[numBands];

				double phi = 0; // initial angle
				double dPhi = (2 * Math.PI) / 10000; // initial delta phi
				double phiMax = -w * 2 * Math.PI; // maximal angle
				double rMax = (Math.min(width, height) / 2 - 1); // maximal
																	// radius
				double a = rMax / (w * 2 * Math.PI); // factor a
				int x = (int) Math.round(a * phi * Math.cos(phi)); // initial x
				int y = (int) Math.round(a * phi * Math.sin(phi)); // initial y
				int xOld = x; // initial xOld
				int yOld = y; // initial yOld
				raster.getPixel(x + width / 2, y + height / 2, pixel);
				for (int b = 0; b < numBands; b++)
					dataY.get(b).add((double) pixel[b]);

				int countXYValuesNotChanging = 0;

				GeneralPath polyLine = new GeneralPath();
				polyLine.moveTo(x + width / 2, y + height / 2);

				while (phi > phiMax) {
					int proz = (int) (phi * prozX / phiMax);
					this.fireProgressChanged(proz);
					if (isCancelled(getParentTask())){
						return null;
					}
					phi = phi - dPhi;
					x = (int) Math.round(a * phi * Math.cos(phi));
					y = (int) Math.round(a * phi * Math.sin(phi));
					if ((x != xOld) || (y != yOld)) {
						raster.getPixel(x + width / 2, y + height / 2, pixel);
						for (int b = 0; b < numBands; b++)
							dataY.get(b).add((double) pixel[b]);
						if (countXYValuesNotChanging > 6)
							dPhi = 2 * dPhi; // adaptation of dphi
						if (countXYValuesNotChanging <= 3)
							dPhi = dPhi / 2.0;
						countXYValuesNotChanging = 0;
						xOld = x;
						yOld = y;
						polyLine.lineTo(x + width / 2, y + height / 2);

					} else {
						countXYValuesNotChanging = countXYValuesNotChanging + 1;
					}
				}
				ROIShape roiShapeReal = new ROIShape(polyLine);
				
				// TODO adjust to new ROI capability
				ILookPanel clp = Application.getLook().getCurrentLookPanel();
				List<ROIShape> roiShapeRealVector = clp.getCurrentROILayer().getAllROIShapes();
				roiShapeRealVector.add(roiShapeReal);
				clp.getCurrentROILayer().setCurrentROIShape(roiShapeReal);
				clp.getCurrentROILayer().setAllROIShapes(roiShapeRealVector);
				clp.getCurrentROILayer().update();

				BoardPanel
						.appendTextln("IqmOpFracHiguchi: number of data elements: "
								+ dataY.get(0).size());

				for (int b = 0; b < numBands; b++) {
					if (optBack == 1) { // exclusive zeroes
						Vector<Double> dataYNew = this.removeZeroes(dataY
								.get(b));
						dataY.get(b).removeAllElements();
						for (int i = 0; i < dataYNew.size(); i++) {
							(dataY.get(b)).add(dataYNew.elementAt(i));
						}
					}
					Higuchi hig = new Higuchi();
					Vector<Double> L = hig.calcLengths(dataY.get(b), numK);
					double[] result = hig.calcDimension(L, regStart, regEnd,
							imgName + "_Band" + b, optShowPlot,
							optDeleteExistingPlot);
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhx:"
							+ (-result[0]) + " StdDev:" + result[1] + " r2:"
							+ result[2] + " adj.r2:" + result[3]);
					dimX[b] = -result[0];
					p3X[b] = result[1];
					p4X[b] = result[2];
					p5X[b] = result[3];
					for (int i = 0; i < numK; i++) {
						regDataDimX[i][b] = L.get(i).doubleValue();
					}
				} // b bands
			} // optDx
			if (optDy == 1) {
				Raster raster = pi.getData();
				int width = pi.getWidth();
				int height = pi.getHeight();
				// Vector<Double> dataY = new Vector<Double>();
				Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																				// bands
				for (int b = 0; b < numBands; b++)
					dataY.add(new Vector<Double>()); // Initialize
				int[] pixel = new int[numBands];

				double phi = 0; // initial angle
				double dPhi = (2 * Math.PI) / 10000; // initial delta phi
				double phiMax = w * 2 * Math.PI; // maximal angle
				double rMax = (Math.min(width, height) / 2 - 1); // maximal
																	// radius
				double a = rMax / (w * 2 * Math.PI); // factor a
				int x = (int) Math.round(a * phi * Math.cos(phi)); // initial x
				int y = (int) Math.round(a * phi * Math.sin(phi)); // initial y
				int xOld = x; // initial xOld
				int yOld = y; // initial yOld
				raster.getPixel(x + width / 2, y + height / 2, pixel);
				for (int b = 0; b < numBands; b++)
					dataY.get(b).add((double) pixel[b]);

				int countXYValuesNotChanging = 0;

				GeneralPath polyLine = new GeneralPath();
				polyLine.moveTo(x + width / 2, y + height / 2);

				while (phi < phiMax) {
					int proz = (int) (phi * prozY / phiMax) + offSetProzY;
					this.fireProgressChanged(proz);
					if (isCancelled(getParentTask())){
						return null;
					}
					phi = phi + dPhi;
					x = (int) Math.round(a * phi * Math.cos(phi));
					y = (int) Math.round(a * phi * Math.sin(phi));
					if ((x != xOld) || (y != yOld)) {
						raster.getPixel(x + width / 2, y + height / 2, pixel);
						for (int b = 0; b < numBands; b++)
							dataY.get(b).add((double) pixel[b]);
						if (countXYValuesNotChanging > 6)
							dPhi = 2 * dPhi; // adaptation of dphi
						if (countXYValuesNotChanging <= 3)
							dPhi = dPhi / 2.0;
						countXYValuesNotChanging = 0;
						xOld = x;
						yOld = y;
						polyLine.lineTo(x + width / 2, y + height / 2);

					} else {
						countXYValuesNotChanging = countXYValuesNotChanging + 1;
					}
				}

				// TODO adjust to new ROI capability
				ROIShape roiShapeReal = new ROIShape(polyLine);
				ILookPanel clp = Application.getLook().getCurrentLookPanel();
				List<ROIShape> roiShapeRealVector = clp.getCurrentROILayer().getAllROIShapes();
				roiShapeRealVector.add(roiShapeReal);
				clp.getCurrentROILayer().setCurrentROIShape(roiShapeReal);
				clp.getCurrentROILayer().setAllROIShapes(roiShapeRealVector);
				clp.getCurrentROILayer().update();

				BoardPanel
						.appendTextln("IqmOpFracHiguchi: number of data elements: "
								+ dataY.get(0).size());

				for (int b = 0; b < numBands; b++) {
					// IqmTools.plotXY( dataY, true, "x", "contiguous y");
					if (optBack == 1) { // exclusive zeroes
						Vector<Double> dataYNew = this.removeZeroes(dataY
								.get(b));
						dataY.get(b).removeAllElements();
						for (int i = 0; i < dataYNew.size(); i++) {
							(dataY.get(b)).add(dataYNew.elementAt(i));
						}
					}
					Higuchi hig = new Higuchi();
					Vector<Double> L = hig.calcLengths(dataY.get(b), numK);
					double[] result = hig.calcDimension(L, regStart, regEnd,
							imgName + "_Band" + b, optShowPlot,
							optDeleteExistingPlot);
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhy:"
							+ (-result[0]) + " StdDev:" + result[1] + " r2:"
							+ result[2] + " adj.r2:" + result[3]);
					dimY[b] = -result[0];
					p3Y[b] = result[1];
					p4Y[b] = result[2];
					p5Y[b] = result[3];
					for (int i = 0; i < numK; i++) {
						regDataDimY[i][b] = L.get(i).doubleValue();
					}
				} // b bands
			} // optDy
		} // optAppend = 5 Spiral
			// --------------------------------------------------------------------------------------------------------------------------------------------
		if (optAppend == 6) { // current line ROI

			ROIShape rs = Application.getLook().getCurrentLookPanel().getCurrentROILayer().getCurrentROIShape();

			if (rs == null) {
				BoardPanel
						.appendTextln("IqmOpFracHiguchi: Currrent ROI is not defined and null!");
				BoardPanel
						.appendTextln("IqmOpFracHiguchi: ROI processing not possible");
				return new Result(new IqmDataBox(pi));
			} else {
				BoardPanel
						.appendTextln("IqmOpFracHiguchi: Current ROI is defined and valid");
			}

			Vector<Float> coords = getShapeCoordinates(rs);
			// if (coords.size() == 6){ //3 points
			// }
			ImageProcessor proc = null;
			Float x1 = null;
			Float y1 = null;
			Float x2 = null;
			Float y2 = null;

			if (coords.size() == 4) { // 2 points (line)
				x1 = coords.get(0);
				y1 = coords.get(1);
				x2 = coords.get(2);
				y2 = coords.get(3);

				Image image = pi.getAsBufferedImage();
				ImagePlus imgP = new ImagePlus("", image);
				proc = imgP.getProcessor();
			} else {
				BoardPanel
						.appendTextln("IqmOpFracHiguchi: Current ROI is not a single line");
				return new Result(new IqmDataBox(pi));
			}

			if (optDx == 1) {
				// Vector<Double> dataY = new Vector<Double>();
				Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																				// bands
				for (int b = 0; b < numBands; b++)
					dataY.add(new Vector<Double>()); // Initialize

				double[] linePoly = proc.getLine(x1, y1, x2, y2);
				for (int p = 0; p < linePoly.length; p++) {
					for (int b = 0; b < numBands; b++) {
						dataY.get(b).add(linePoly[p]);
						// System.out.println("LinePoly" + linePoly[p]);
					}
				}

				BoardPanel
						.appendTextln("IqmOpFracHiguchi: number of data elements: "
								+ dataY.get(0).size());
				if (regEnd > linePoly.length / 2) {
					BoardPanel
							.appendTextln("IqmOpFracHiguchi: regression end value: "
									+ regEnd);
					BoardPanel
							.appendTextln("IqmOpFracHiguchi: regression end value is too large, choose a smaller k value");
					return new Result(new IqmDataBox(pi));
				}

				for (int b = 0; b < numBands; b++) {
					if (optBack == 1) { // exclusive zeroes
						Vector<Double> dataYNew = this.removeZeroes(dataY
								.get(b));
						dataY.get(b).removeAllElements();
						for (int i = 0; i < dataYNew.size(); i++) {
							(dataY.get(b)).add(dataYNew.elementAt(i));
						}
					}
					Higuchi hig = new Higuchi();
					Vector<Double> L = hig.calcLengths(dataY.get(b), numK);
					double[] result = hig.calcDimension(L, regStart, regEnd,
							imgName + "_Band" + b, optShowPlot,
							optDeleteExistingPlot);
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhx:"
							+ (-result[0]) + " StdDev:" + result[1] + " r2:"
							+ result[2] + " adj.r2:" + result[3]);
					dimX[b] = -result[0];
					p3X[b] = result[1];
					p4X[b] = result[2];
					p5X[b] = result[3];
					for (int i = 0; i < numK; i++) {
						regDataDimX[i][b] = L.get(i).doubleValue();
					}
				} // b bands
			} // optDx
			if (optDy == 1) {

				// Vector<Double> dataY = new Vector<Double>();
				Vector<Vector<Double>> dataY = new Vector<Vector<Double>>(); // for
																				// bands
				for (int b = 0; b < numBands; b++)
					dataY.add(new Vector<Double>()); // Initialize

				double[] linePoly = proc.getLine(x2, y2, x1, y1);
				for (int p = 0; p < linePoly.length; p++) {
					for (int b = 0; b < numBands; b++) {
						dataY.get(b).add(linePoly[p]);
						// System.out.println("LinePoly" + linePoly[p]);
					}
				}
				BoardPanel
						.appendTextln("IqmOpFracHiguchi: number of data elements: "
								+ dataY.get(0).size());
				if (regEnd > linePoly.length / 2) {
					BoardPanel
							.appendTextln("IqmOpFracHiguchi: regression end value: "
									+ regEnd);
					BoardPanel
							.appendTextln("IqmOpFracHiguchi: regression end value is too large, choose a smaller k value");
					return new Result(new IqmDataBox(pi));
				}
				for (int b = 0; b < numBands; b++) {
					// IqmTools.plotXY( dataY, true, "x", "contiguous y");
					if (optBack == 1) { // exclusive zeroes
						Vector<Double> dataYNew = this.removeZeroes(dataY
								.get(b));
						dataY.get(b).removeAllElements();
						for (int i = 0; i < dataYNew.size(); i++) {
							(dataY.get(b)).add(dataYNew.elementAt(i));
						}
					}
					Higuchi hig = new Higuchi();
					Vector<Double> L = hig.calcLengths(dataY.get(b), numK);
					double[] result = hig.calcDimension(L, regStart, regEnd,
							imgName + "_Band" + b, optShowPlot,
							optDeleteExistingPlot);
					BoardPanel.appendTextln("IqmOpFracHiguchi  Dhy:"
							+ (-result[0]) + " StdDev:" + result[1] + " r2:"
							+ result[2] + " adj.r2:" + result[3]);
					dimY[b] = -result[0];
					p3Y[b] = result[1];
					p4Y[b] = result[2];
					p5Y[b] = result[3];
					for (int i = 0; i < numK; i++) {
						regDataDimY[i][b] = L.get(i).doubleValue();
					}
				} // b bands
			} // optDy
		} // optAppend = 6 Current line ROI
			// --------------------------------------------------------------------------------------------------------------------------------------------

		// Dh average or not
		if (optAppend == 0 || optAppend == 1 || optAppend == 2
				|| optAppend == 3 || optAppend == 4 || optAppend == 5
				|| optAppend == 6) { // eh alle
			for (int b = 0; b < numBands; b++) {
				if (optDx == 1 && optDy == 0) {
					dim[b] = dimX[b];
					p3[b] = p3X[b];
					p4[b] = p4X[b];
					p5[b] = p5X[b];
				}
				if (optDx == 0 && optDy == 1) {
					dim[b] = dimY[b];
					p3[b] = p3Y[b];
					p4[b] = p4Y[b];
					p5[b] = p5Y[b];
				}
				if (optDx == 1 && optDy == 1) {
					dim[b] = (dimX[b] + dimY[b]) / 2;
					p3[b] = (p3X[b] + p3Y[b]) / 2;
					p4[b] = (p4X[b] + p4Y[b]) / 2;
					p5[b] = (p5X[b] + p5Y[b]) / 2;
				}
			}
		}

		// set table data header
		int numColumns = model.getColumnCount();
		model.addColumn("Dh_x");
		model.addColumn("Dh_y");
		model.addColumn("Dh");
		model.addColumn("StdDev_x");
		model.addColumn("StdDev_y");
		model.addColumn("StdDev");
		model.addColumn("r2_x");
		model.addColumn("r2_y");
		model.addColumn("r2");
		model.addColumn("adjustet_r2_x");
		model.addColumn("adjustet_r2_y");
		model.addColumn("adjustet_r2");

		// set regression data header
		for (int n = 0; n < numK; n++)
			model.addColumn("DataX_" + (n + 1));
		for (int n = 0; n < numK; n++)
			model.addColumn("DataDhx_" + (n + 1));
		for (int n = 0; n < numK; n++)
			model.addColumn("DataDhy_" + (n + 1));

		// set table data
		for (int b = 0; b < numBands; b++) { // several bands
			model.setValueAt(dimX[b], b, numColumns);
			model.setValueAt(dimY[b], b, numColumns + 1);
			model.setValueAt(dim[b], b, numColumns + 2);
			model.setValueAt(p3X[b], b, numColumns + 3);
			model.setValueAt(p3Y[b], b, numColumns + 4);
			model.setValueAt(p3[b], b, numColumns + 5);
			model.setValueAt(p4X[b], b, numColumns + 6);
			model.setValueAt(p4Y[b], b, numColumns + 7);
			model.setValueAt(p4[b], b, numColumns + 8);
			model.setValueAt(p5X[b], b, numColumns + 9);
			model.setValueAt(p5Y[b], b, numColumns + 10);
			model.setValueAt(p5[b], b, numColumns + 11);

			for (int n = 0; n < numK; n++) {
				// model.setValueAt(lnBoxWidth[n][b], b, numColumns+n));
				model.setValueAt(n + 1, b, numColumns + 11 + 1 + n);
			}
			for (int n = 0; n < numK; n++) {
				// model.setValueAt(lnTotals[n][b], b, numColumns+number+n);
				model.setValueAt(regDataDimX[n][b], b, numColumns + numK + 11
						+ 1 + n);
			}
			for (int n = 0; n < numK; n++) {
				// model.setValueAt(lnTotals[n][b], b, numColumns+number+n);
				model.setValueAt(regDataDimY[n][b], b, numColumns + (2 * numK)
						+ 11 + 1 + n);
			}
		}// bands

		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(model);
		// return piOut; //Optional wird ein Bild ausgegeben
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpFracHiguchiDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpFracHiguchiDescriptor.TYPE;
	}
}
