/*
 * Created on Jun 24, 2005
 * @author Rafael Santos (rafael.santos@lac.inpe.br)
 * 
 * Part of the Java Advanced Imaging Stuff site
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI)
 * 
 * STATUS: Complete.
 * 
 * Redistribution and usage conditions must be done under the
 * Creative Commons license:
 * English: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.en
 * Portuguese: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.pt
 * More information on design and applications are on the projects' page
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI).
 */

package at.mug.iqm.img.bundle.jai.stuff;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: FuzzyLICMeansImageClustering.java
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

import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Random;

import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;

import at.mug.iqm.api.gui.BoardPanel;

/**
 * This class implements a basic Fuzzy C-Means clustering algorithm as an image
 * processing task. This implementation tries to speed things up, but needs to
 * keep all image data on memory. This implementation deals only with
 * integer-like pixel data but can cluster N-dimensional data. This
 * implementation can return ranked images (i.e. second, third, etc. best
 * choices) as well as the membership- function based images.
 * <p>
 * <b>Changes</b>
 * <ul>
 * 	<li>2010 05 adapted to FLICM Clustering according to Krinidis S, Chatzis
 *         V., A Robust Fuzzy Local Information C-Means Clustering Algorithm,
 *         IEEE Trans. Img. Proc., 19, 2010, 1328-1337.
 *  <li>2010 05 added methods: calculateFuzzyFactorGs(), calcNorm2() adapted
 *         methods: calculateMFsFromClusterCenters()
 * </ul>
 * 
 * 
 * @author Helmut Ahammer
 * @since 2009 06 Original from Rafael Santos
 *       (rafael.santos@lac.inpe.br), see below
 * 
 */
@SuppressWarnings({ "unused" })
public class FuzzyLICMeansImageClustering extends ImageProcessingTask {
	// A copy of the input image.
	private PlanarImage pInput;
	// The input image dimensions.
	private int width, height, numBands;
	// Some clustering parameters.
	private int maxIterations, numClusters;
	// The FCM additional parameters and membership function values.
	private float fuzziness; // "m"
	private float[][][] membership;
	private float[][][] fuzzyFactorG;
	// The iteration counter will be global so we can get its value on the
	// middle of the clustering process.
	private int iteration;
	// A metric of clustering "quality", called "j" as in the equations.
	private double j = Float.MAX_VALUE;
	// A small value, if the difference of the cluster "quality" does not
	// changes beyond this value, we consider the clustering converged.
	private double epsilon;
	// This flag will be true when the clustering has finished.
	private boolean hasFinished = false;
	private long position;
	// The cluster centers.
	private float[][] clusterCenters;
	// A big array with all the input data and a small one for a single pixel.
	private int[] inputData;
	private float[] aPixel;
	// A big array with the output data (cluster indexes).
	private short[][] outputData;

	/**
	 * The constructor for the class, which sets the input image, the number of
	 * desired clusters, the maximum number of iterations, the fuzziness ("m"
	 * value) and a value that will be used to decide whether the convergence
	 * has stopped. It also allocates the required memory.
	 * 
	 * @param pInput
	 *            the input planar image.
	 * @param numClusters
	 *            the desired number of clusters.
	 * @param maxIterations
	 *            the maximum number of iterations.
	 * @param fuzziness
	 *            the fuzziness (a.k.a. the "m" value)
	 * @param epsilon
	 *            a small value used to verify if clustering has converged.
	 */
	public FuzzyLICMeansImageClustering(PlanarImage pInput, int numClusters,
			int maxIterations, float fuzziness, double epsilon) {

		this.pInput = pInput;
		// Get the image dimensions.
		width = pInput.getWidth();
		height = pInput.getHeight();
		numBands = pInput.getSampleModel().getNumBands();
		// Get some clustering parameters.
		this.numClusters = numClusters;
		this.maxIterations = maxIterations;
		this.fuzziness = fuzziness;
		this.epsilon = epsilon;
		iteration = 0;
		// We need arrays to store the clusters' centers, validity tags and
		// membership values.
		clusterCenters = new float[numClusters][numBands];
		membership = new float[width][height][numClusters];
		fuzzyFactorG = new float[width][height][numClusters];
		// Gets the raster for the input image.
		Raster raster = pInput.getData();
		// Gets the whole image data on memory. Get memory for a single pixel
		// too.
		inputData = new int[width * height * numBands];
		aPixel = new float[numBands];
		// Gets memory for the output data (cluster indexes).
		outputData = new short[width][height];
		raster.getPixels(0, 0, width, height, inputData);
		// Initialize the membership functions randomly.
		Random generator = new Random(); // easier to debug if a seed is used
		// For each data point (in the membership function table)
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++) {
				// For each cluster's membership assign a random value.
				float sum = 0f;
				float sumG = 0f;
				for (int c = 0; c < numClusters; c++) {
					membership[w][h][c] = 0.001f + generator.nextFloat();
					fuzzyFactorG[w][h][c] = 0.001f + generator.nextFloat();
					sum += membership[w][h][c];
					sumG += fuzzyFactorG[w][h][c];
				}
				// Normalize so the sum of MFs for a particular data point will
				// be equal to 1.
				for (int c = 0; c < numClusters; c++)
					membership[w][h][c] /= sum;
				for (int c = 0; c < numClusters; c++)
					fuzzyFactorG[w][h][c] /= sumG;
			}
		// Initialize the global position value.
		position = 0;
	}

	/**
	 * This method performs the bulk of the processing. It runs the classic
	 * Fuzzy C-Means clustering algorithm: 1 - Calculate the cluster centers. 2
	 * - Update the membership function. 3 - Calculate statistics and repeat
	 * from 1 if needed.
	 */
	@Override
	public void run() {
		double lastJ;
		// Calculate the initial objective function just for kicks.
		lastJ = calculateObjectiveFunction();
		// Do all required iterations (until the clustering converges)
		for (iteration = 0; iteration < maxIterations; iteration++) {

			if (isCancelled(getParentTask())) {
				this.interrupt();
				return;
			}

			// Calculate cluster centers from MFs.
			calculateClusterCentersFromMFs();
			// Then calculate the MFs from the cluster centers !
			calculateMFsFromClusterCenters();
			calculateFuzzyFactorGs();
			// Then see how our objective function is going.
			j = calculateObjectiveFunction();
			if (Math.abs(lastJ - j) < epsilon) {
				BoardPanel
						.appendTextln("FuzzyCMeansImageClustering: Objective function smaller than eps");
				break;
			}
			lastJ = j;
		} // end of the iterations loop.
		hasFinished = true;
		BoardPanel
				.appendTextln("FuzzyCMeansImageClustering: Finished at iteration: "
						+ iteration);
		// Means that all calculations are done, too.
		position = getSize();
	}

	/**
	 * This method calculates the cluster centers from the membership functions.
	 */
	private void calculateClusterCentersFromMFs() {
		float top, bottom;
		// For each band and cluster
		for (int b = 0; b < numBands; b++)
			for (int c = 0; c < numClusters; c++) {
				// For all data points calculate the top and bottom parts of the
				// equation.
				top = bottom = 0;
				for (int h = 0; h < height; h++) {
					int proz = (h + 1) * 100 / height;
					this.fireProgressChanged(proz);
					
					if (isCancelled(getParentTask())) {
						this.interrupt();
						return;
					}
					for (int w = 0; w < width; w++) {
						// Index will help locate the pixel data position.
						int index = (h * width + w) * numBands;
						top += Math.pow(membership[w][h][c], fuzziness)
								* inputData[index + b];
						bottom += Math.pow(membership[w][h][c], fuzziness);
					}
				}
				// Calculate the cluster center.
				clusterCenters[c][b] = top / bottom;
				// Upgrade the position vector (batch).
				position += width * height;
			}
	}

	/**
	 * adapted by Ahammer 2010 05 This method calculates the membership
	 * functions from the cluster centers.
	 */
	private void calculateMFsFromClusterCenters() {
		float sumTerms = 0f;
		float[] diffPixelCluster = new float[numBands];

		// For each cluster and data point
		for (int c = 0; c < numClusters; c++)
			for (int h = 0; h < height; h++) {
				int proz = (h + 1) * 100 / height;
				this.fireProgressChanged(proz);
				
				if (isCancelled(getParentTask())) {
					this.interrupt();
					return;
				}
				for (int w = 0; w < width; w++) {
					// Get a pixel (as a single array).
					int index = (h * width + w) * numBands;
					for (int b = 0; b < numBands; b++)
						aPixel[b] = inputData[index + b];

					for (int b = 0; b < numBands; b++)
						diffPixelCluster[b] = aPixel[b] - clusterCenters[c][b];
					float normTop = calcNormP(diffPixelCluster, 2.f);

					// Bottom is the sum of distances from this data point to
					// all clusters.
					sumTerms = 0f;
					for (int cj = 0; cj < numClusters; cj++) {
						for (int b = 0; b < numBands; b++)
							diffPixelCluster[b] = aPixel[b]
									- clusterCenters[cj][b];
						float normThis = calcNormP(diffPixelCluster, 2.0f);
						sumTerms += Math.pow((normTop + fuzzyFactorG[w][h][c])
								/ (normThis + fuzzyFactorG[w][h][cj]),
								(1f / (fuzziness - 1f)));
					}
					// Then the MF can be calculated as...
					membership[w][h][c] = (1f / sumTerms);
					// Upgrade the position vector (batch).
					position += (numBands + numClusters);
				}
			}
	}

	/**
	 * added by Ahammer 2010 05 This method calculates the fuzzy factor G
	 */
	private void calculateFuzzyFactorGs() {
		float[] aPixel2 = new float[numBands];
		float[] diffPixelCluster = new float[numBands];
		int[] inputData2 = new int[width * height * numBands];
		// For each cluster and data point
		for (int c = 0; c < numClusters; c++) {
			for (int h = 0; h < height; h++) {
				int proz = (h + 1) * 100 / height;
				this.fireProgressChanged(proz);
				
				if (isCancelled(getParentTask())) {
					this.interrupt();
					return;
				}
				for (int w = 0; w < width; w++) {
					// Get a pixel (as a single array).
					int index = (h * width + w) * numBands;
					for (int b = 0; b < numBands; b++)
						aPixel[b] = inputData[index + b];
					// Calculate Gki
					float sumTerms = 0.0f;
					for (int hh = h - 3; hh < h + 3; hh++) { // Neighborhood 3x3
																// or 5x5,
																// ......
						for (int ww = w - 3; ww < w + 3; ww++) {
							if ((hh > 0) && (hh < height) && (ww > 0)
									&& (ww < width)) { // check if not out of
														// bounds
								// Get a pixel (as a single array).
								int index2 = (hh * width + ww) * numBands;
								if (index != index2) {
									for (int bb = 0; bb < numBands; bb++)
										aPixel2[bb] = inputData2[index2 + bb];
									float distancePixel = calcDistance(aPixel,
											aPixel2);
									for (int bb = 0; bb < numBands; bb++)
										diffPixelCluster[bb] = aPixel2[bb]
												- clusterCenters[c][bb];
									float norm = calcNormP(diffPixelCluster,
											2.f);
									sumTerms = sumTerms
											+ 1.0f
											/ (distancePixel + 1.0f)
											* (float) Math
													.pow((1 - membership[ww][hh][c]),
															fuzziness) * norm;
								} //
							}

						}// ww
					}// hh
					fuzzyFactorG[w][h][c] = sumTerms;
				}// w
			} // h
		} // c
	}

	/*
	 * adapted by Ahammer 2010 05 This method calculates the objective function
	 * ("j") which reflects the quality of the clustering.
	 */
	private double calculateObjectiveFunction() {
		double j = 0;
		float[] diffPixelCluster = new float[numBands];
		// For all data values and clusters
		for (int h = 0; h < height; h++) {
			int proz = (h + 1) * 100 / height;
			this.fireProgressChanged(proz);
			
			if (isCancelled(getParentTask())) {
				this.interrupt();
			}
			for (int w = 0; w < width; w++) {
				for (int c = 0; c < numClusters; c++) {
					// Get the current pixel data.
					int index = (h * width + w) * numBands;
					for (int b = 0; b < numBands; b++)
						aPixel[b] = inputData[index + b];
					// Calculate the difference between a pixel and a cluster
					// center.
					for (int b = 0; b < numBands; b++)
						diffPixelCluster[b] = aPixel[b] - clusterCenters[c][b];
					j += Math.pow(membership[w][h][c], fuzziness)
							* calcNormP(diffPixelCluster, 2.f)
							+ fuzzyFactorG[w][h][c];
					// Upgrade the position vector (batch).
					position += (2 * numBands);
				} // c
			} // w
		} // h
		return j;
	}

	/**
	 * This method calculates the Euclidean distance between two N-dimensional
	 * vectors.
	 * 
	 * @param a1
	 *            the first data vector.
	 * @param a2
	 *            the second data vector.
	 * @return the Euclidean distance between those vectors.
	 */
	private float calcDistance(float[] a1, float[] a2) {
		float distance = 0f;
		for (int e = 0; e < a1.length; e++)
			distance += (a1[e] - a2[e]) * (a1[e] - a2[e]);
		return (float) Math.sqrt(distance);
	}

	/**
	 * added by Ahammer 2010 05 This method calculates the p-norm.
	 * 
	 * @param vec
	 *            data vector..
	 * @return the p-norm of vector.
	 */
	private float calcNormP(float[] vec, float p) {
		float sum = 0f;
		for (int e = 0; e < vec.length; e++) {
			sum += Math.pow(Math.abs(vec[e]), p);
		}
		return (float) Math.pow(sum, 1.f / p);
	}

	/**
	 * This method returns the estimated size (steps) for this task. The value
	 * is, of course, an approximation, just so we will be able to give the user
	 * a feedback on the processing time. In this case, the value is calculated
	 * as the number of loops in the run() method.
	 */
	@Override
	public long getSize() {
		// Return the estimated size for this task:
		return (long) maxIterations * // The maximum number of iterations times
				((numClusters * width * height * (2 * numBands)) + // Step 0 of
																	// method
																	// run()
						(width * height * numBands * numClusters) + // Step 1 of
																	// method
																	// run()
						(numClusters * width * height * (numBands + numClusters)) + // Step
																					// 2
																					// of
																					// run()
				(numClusters * width * height * (2 * numBands)) // Step 3 of
																// method run()
				);
	}

	/**
	 * This method returns a measure of the progress of the algorithm.
	 */
	@Override
	public long getPosition() {
		return position;
	}

	/**
	 * This method returns true if the clustering has finished.
	 */
	@Override
	public boolean isFinished() {
		return (position == getSize());
	}

	/**
	 * This method will return a rank image, i.e. an image which pixels are the
	 * cluster centers of the Nth best choice for the classification. For
	 * example, if the membership functions for a pixel of an image clustered
	 * with six clusters are [0.10 0.25 0.40 0.20 0.03 0.02] and we ask for the
	 * image with rank 2 (ranks starts with zero), for that pixel the third best
	 * choice for cluster index will be selected (0.20) and the centers of the
	 * cluster with index 3 will be used. It is important to notice that this
	 * method can be called while the clustering task (method run) is running,
	 * and the resulting image is not guaranteed to be close to a clustered
	 * result.
	 * 
	 * @param rank
	 *            the desired rank for the classification.
	 * @return a TiledImage with the classification results considering that
	 *         rank.
	 */
	public TiledImage getRankedImage(int rank) {
		// Create a SampleModel for the output data (same number of bands as the
		// input image).
		SampleModel sampleModel = RasterFactory.createBandedSampleModel(
				DataBuffer.TYPE_INT, width, height, numBands);
		// Create a WritableRaster using that sample model.
		WritableRaster raster = RasterFactory.createWritableRaster(sampleModel,
				new Point(0, 0));
		// A pixel array will contain all bands for a specific x,y.
		int[] pixelArray = new int[numBands];
		// For all pixels in the image...
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++) {
				// Get the class (cluster center) for that pixel with the
				// specified rank.
				int aCluster = getRankedIndex(membership[w][h], rank);
				// Fill the array with that cluster center.
				for (int band = 0; band < numBands; band++)
					pixelArray[band] = Math
							.round(clusterCenters[aCluster][band]); // Math.round
																	// added
																	// 2009 08
				// Put it on the raster.
				raster.setPixel(w, h, pixelArray);
			}
		// Set the raster on the output image.
		TiledImage pOutput = new TiledImage(pInput, false);
		pOutput.setData(raster);
		return pOutput;
	}

	/**
	 * This method will return a membership function image, i.e. an image which
	 * pixels correspond to the membership functions for the cluster which is
	 * the Nth best choice for the classification. For example, if the
	 * membership functions for a pixel of an image clustered with six clusters
	 * are [0.10 0.25 0.40 0.20 0.03 0.02] and we ask for the membership
	 * function image with rank 2 (ranks starts with zero), for that pixel the
	 * third best membership function will be selected (0.20) and used (scaled
	 * by 255) as the pixel value. It is important to notice that this method
	 * can be called while the clustering task (method run) is running, and the
	 * resulting image is not guaranteed to be close to a clustered result.
	 * 
	 * @param rank
	 *            the desired rank for the classification.
	 * @return a TiledImage with the membership function value results
	 *         considering that rank.
	 */
	public TiledImage getRankedMFImage(int rank) {
		// Create a SampleModel for the output data (1 band only).
		SampleModel sampleModel = RasterFactory.createBandedSampleModel(
				DataBuffer.TYPE_BYTE, width, height, 1);
		// Create a compatible ColorModel.
		ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
		// Create a WritableRaster.
		WritableRaster raster = RasterFactory.createWritableRaster(sampleModel,
				new Point(0, 0));
		// For all pixels in the image...
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++) {
				// Get the membership function (considering the rank) for that
				// pixel.
				int aCluster = (int) (255 * getRankedMF(membership[w][h], rank));
				// Put it on the raster.
				raster.setPixel(w, h, new int[] { aCluster });
			}
		// Set the raster on the output image.
		TiledImage pOutput = new TiledImage(0, 0, width, height, 0, 0,
				sampleModel, colorModel);
		pOutput.setData(raster);
		return pOutput;
	}

	/**
	 * This method returns the ranked index of a cluster from an array
	 * containing the membership functions. For example, if the array contains
	 * [0.10 0.25 0.40 0.20 0.03 0.02] and we ask for index with rank 2 (ranks
	 * starts with zero), the third best choice will be selected (0.20) and its
	 * index (3) will be returned.
	 * 
	 * @param data
	 *            the array with the membership functions.
	 * @param rank
	 *            the rank of the cluster we want to get.
	 * @return the index of the cluster.
	 */
	private int getRankedIndex(float[] data, int rank) {
		// Create temporary arrays for the indexes and the data.
		int[] indexes = new int[data.length];
		float[] tempData = new float[data.length];
		// Fill those arrays.
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = i;
			tempData[i] = data[i];
		}
		// Sort both arrays together, using data as the sorting key.
		for (int i = 0; i < indexes.length - 1; i++)
			for (int j = i; j < indexes.length; j++) {
				if (tempData[i] < tempData[j]) {
					int tempI = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tempI;
					float tempD = tempData[i];
					tempData[i] = tempData[j];
					tempData[j] = tempD;
				}
			}
		// Return the cluster index for the rank we want.
		return indexes[rank];
	}

	/**
	 * This method returns the ranked membership function of a cluster from an
	 * array containing the membership functions. For example, if the array
	 * contains [0.10 0.25 0.40 0.20 0.03 0.02] and we ask for the MF with rank
	 * 2 (ranks starts with zero), the third best choice will be selected (0.20)
	 * and returned.
	 * 
	 * @param data
	 *            the array with the membership functions.
	 * @param rank
	 *            the rank of the cluster we want to get.
	 * @return the MF with that rank.
	 */
	private float getRankedMF(float[] data, int rank) {
		// Create temporary arrays for the indexes and the data.
		int[] indexes = new int[data.length];
		float[] tempData = new float[data.length];
		// Fill those arrays.
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = i;
			tempData[i] = data[i];
		}
		// Sort both arrays together, using data as the sorting key.
		for (int i = 0; i < indexes.length - 1; i++)
			for (int j = i; j < indexes.length; j++) {
				if (tempData[i] < tempData[j]) {
					int tempI = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tempI;
					float tempD = tempData[i];
					tempData[i] = tempData[j];
					tempData[j] = tempD;
				}
			}
		// Return the cluster index for the rank we want.
		return tempData[rank];
	}

	/**
	 * This method returns the Partition Coefficient measure of cluster validity
	 * (see Fuzzy Algorithms With Applications to Image Processing and Pattern
	 * Recognition, Zheru Chi, Hong Yan, Tuan Pham, World Scientific, pp. 91)
	 */
	public double getPartitionCoefficient() {
		double pc = 0;
		// For all data values and clusters
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++)
				for (int c = 0; c < numClusters; c++)
					pc += membership[w][h][c] * membership[w][h][c];
		pc = pc / (height * width);
		return pc;
	}

	/**
	 * This method returns the Partition Entropy measure of cluster validity
	 * (see Fuzzy Algorithms With Applications to Image Processing and Pattern
	 * Recognition, Zheru Chi, Hong Yan, Tuan Pham, World Scientific, pp. 91)
	 */
	public double getPartitionEntropy() {
		double pe = 0;
		// For all data values and clusters
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++)
				for (int c = 0; c < numClusters; c++)
					pe += membership[w][h][c] * Math.log(membership[w][h][c]);
		pe = -pe / (height * width);
		return pe;
	}

	/**
	 * This method returns the Compactness and Separation measure of cluster
	 * validity (see Fuzzy Algorithms With Applications to Image Processing and
	 * Pattern Recognition, Zheru Chi, Hong Yan, Tuan Pham, World Scientific,
	 * pp. 93)
	 */
	public double getCompactnessAndSeparation() {
		double cs = 0;
		// For all data values and clusters
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++) {
				// Get the current pixel data.
				int index = (h * width + w) * numBands;
				for (int b = 0; b < numBands; b++)
					aPixel[b] = inputData[index + b];
				for (int c = 0; c < numClusters; c++) {
					// Calculate the distance between a pixel and a cluster
					// center.
					float distancePixelToCluster = calcSquaredDistance(aPixel,
							clusterCenters[c]);
					cs += membership[w][h][c] * membership[w][h][c]
							* distancePixelToCluster * distancePixelToCluster;
				}
			}
		cs /= (height * width);
		// Calculate minimum distance between ALL clusters
		float minDist = Float.MAX_VALUE;
		for (int c1 = 0; c1 < numClusters - 1; c1++)
			for (int c2 = c1 + 1; c2 < numClusters; c2++) {
				float distance = calcSquaredDistance(clusterCenters[c1],
						clusterCenters[c2]);
				minDist = Math.min(minDist, distance);
			}
		cs = cs / (minDist * minDist);
		return cs;
	}

	/**
	 * This method calculates the squared distance between two N-dimensional
	 * vectors.
	 * 
	 * @param a1
	 *            the first data vector.
	 * @param a2
	 *            the second data vector.
	 * @return the squared distance between those vectors.
	 */
	private float calcSquaredDistance(float[] a1, float[] a2) {
		float distance = 0f;
		for (int e = 0; e < a1.length; e++)
			distance += (a1[e] - a2[e]) * (a1[e] - a2[e]);
		return distance;
	}

	// ------------------------------------------------------------------------------
	/**
	 * This method returns the cluster centers. added 2009 08
	 * 
	 * @return cluster centers
	 */
	public float[][] getClusterCenters() {
		// clusterCenters[aClass][band]
		return clusterCenters;
	}

}
