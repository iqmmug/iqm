package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpDirLocal.java
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


import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.BorderExtender;
import javax.media.jai.DataBufferFloat;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
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
import at.mug.iqm.api.plot.charts.VectorPlotFrame;
import at.mug.iqm.commons.lau.Basic;
import at.mug.iqm.commons.lau.Linear_algebra;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.img.bundle.descriptors.IqmOpDirLocalDescriptor;

/**
 * @author Ahammer, Kainz
 * @since 2010 05
 */
public class IqmOpDirLocal extends AbstractOperator {

	public IqmOpDirLocal() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@Override
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		fireProgressChanged(5);
		
		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();
		
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));

		int gradient = pb.getIntParameter("Gradient");
		int eigen = pb.getIntParameter("Eigen");
		int outImg = pb.getIntParameter("OutImg");
		int showVecPlot = pb.getIntParameter("ShowVecPlot");

		int numBands = pi.getNumBands();

		KernelJAI kernel_h = null;
		KernelJAI kernel_v = null;
		// -----------------------------------------------------
		if (gradient == 0) { // Roberts
			float[] h_data = { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
					0.0f };
			float[] v_data = { -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
					0.0f };
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
		}
		// -----------------------------------------------------
		if (gradient == 1) { // Pixel difference see WK Pratt Digital Image
								// Processing 2nd ed. p503
			float[] h_data = { 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f,
					0.0f };
			float[] v_data = { 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
					0.0f };
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
		}
		// -----------------------------------------------------
		if (gradient == 2) { // Separated Pixel Difference see WK Pratt Digital
								// Image Processing 2nd ed. p503
			float[] h_data = { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f,
					0.0f };
			float[] v_data = { 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
					0.0f };
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
		}
		// -----------------------------------------------------
		if (gradient == 3) { // Sobel
			float[] h_data = { 1.0f, 0.0f, -1.0f, 2.0f, 0.0f, -2.0f, 1.0f,
					0.0f, -1.0f };
			float[] v_data = { -1.0f, -2.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
					2.0f, 1.0f };
			for (int i = 0; i < h_data.length; i++) {
				h_data[i] = h_data[i] * (1.0f / 4.0f);
			}
			for (int i = 0; i < v_data.length; i++) {
				v_data[i] = v_data[i] * (1.0f / 4.0f);
			}

			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
			// kernel_h = KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL;
			// kernel_v = KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;
		}
		// -----------------------------------------------------
		if (gradient == 4) { // Prewitt
			float[] h_data = { 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f,
					0.0f, -1.0f };
			float[] v_data = { -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
					1.0f, 1.0f };
			for (int i = 0; i < h_data.length; i++) {
				h_data[i] = h_data[i] * (1.0f / 3.0f);
			}
			for (int i = 0; i < v_data.length; i++) {
				v_data[i] = v_data[i] * (1.0f / 3.0f);
			}
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
		}
		// -----------------------------------------------------
		if (gradient == 5) { // FreiChen
			float[] h_data = { 1.0f, 0.0f, -1.0f, (float) Math.sqrt(2), 0.0f,
					-(float) Math.sqrt(2), 1.0f, 0.0f, -1.0f };
			float[] v_data = { -1.0f, -(float) Math.sqrt(2), -1.0f, 0.0f, 0.0f,
					0.0f, 1.0f, (float) Math.sqrt(2), 1.0f };
			for (int i = 0; i < h_data.length; i++) {
				h_data[i] = h_data[i] * (1.0f / (2.0f + (float) Math.sqrt(2)));
			}
			for (int i = 0; i < v_data.length; i++) {
				v_data[i] = v_data[i] * (1.0f / (2.0f + (float) Math.sqrt(2)));
			}
			kernel_h = new KernelJAI(3, 3, h_data);
			kernel_v = new KernelJAI(3, 3, v_data);
		}
		// -----------------------------------------------------
		// if (gradient == ){ //
		// float[] h_data = { 0.0f, 0.0f, 0.0f,
		// 0.0f, 0.0f, 0.0f,
		// 0.0f, 0.0f, 0.0f };
		// float[] v_data = { 0.0f, 0.0f, 0.0f,
		// 0.0f, 0.0f, 0.0f,
		// 0.0f, 0.0f, 0.0f };
		// kernel_h = new KernelJAI(3,3, h_data);
		// kernel_v = new KernelJAI(3,3, v_data);
		// }

		// -----------------------------------------------------

		// int kernelSize = pbJAI.getIntParameter("KernelSize");

		ParameterBlock pbF = new ParameterBlock();
		pbF.addSource(pi);
		pbF.add(DataBuffer.TYPE_FLOAT); // Type angeben
		PlanarImage piF = JAI.create("Format", pbF, null);
		
		fireProgressChanged(20);
		if (isCancelled(getParentTask())){
			return null;
		}

		RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
				BorderExtender.createInstance(BorderExtender.BORDER_COPY));
		ParameterBlock pbConvolve = new ParameterBlock();
		pbConvolve.addSource(piF);
		pbConvolve.add(kernel_h);
		PlanarImage piGradientX = JAI.create("Convolve", pbConvolve, rh);

		pbConvolve = new ParameterBlock();
		pbConvolve.addSource(piF);
		pbConvolve.add(kernel_v);
		PlanarImage piGradientY = JAI.create("Convolve", pbConvolve, rh);

		Raster rX = piGradientX.getData();
		Raster rY = piGradientY.getData();

		
		fireProgressChanged(30);
		if (isCancelled(getParentTask())){
			return null;
		}
		// create float grey output image
		int width = pi.getWidth();
		int height = pi.getHeight();
		SampleModel sm = RasterFactory.createBandedSampleModel(
				DataBuffer.TYPE_FLOAT, width, height, 1); // 1 banded sample
															// model
		float[] floatArr = new float[width * height];
		DataBufferFloat db = new DataBufferFloat(floatArr, width * height);
		WritableRaster wrOut = RasterFactory.createWritableRaster(sm, db,
				new Point(0, 0));
		// create tiled image
		ColorModel cm = PlanarImage.createColorModel(sm); // compatible color
															// model
		TiledImage tiOut = new TiledImage(0, 0, width, height, 0, 0, sm, cm);
		tiOut.setData(wrOut);

		TiledImage tiVP = null;
		if (showVecPlot == 1) {
			// create float 2band tiled image as data storage for vector plot VP
			SampleModel smVP = RasterFactory.createBandedSampleModel(
					DataBuffer.TYPE_FLOAT, width, height, 2); // 2 banded sample
																// model for
																// vector data
			tiVP = new TiledImage(0, 0, width, height, 0, 0, smVP, null);
		}

		int[] pxlX = new int[numBands];
		int[] pxlY = new int[numBands];

		int aa = 3;
		int ab = 4;
		int bb = -3;
		for (int x = 0; x < rX.getWidth(); x++) {
			for (int y = 0; y < rY.getHeight(); y++) {
				rX.getPixel(x, y, pxlX);
				rX.getPixel(x, y, pxlY);
				int proz = y*60/rY.getHeight()/rX.getWidth() + x*60/rX.getWidth();
				
				
				fireProgressChanged(proz+30);
				if (isCancelled(getParentTask())){
					return null;
				}
				// Board.appendTexln("IqmOpDirLocal: pxlX: "+ pxlX);
				// Board.appendTexln("IqmOpDirLocal: pxlY: "+ pxlY);

				if (eigen == 0) { // flanagan Jakobi
					flanagan.math.Matrix J = new flanagan.math.Matrix(2, 2);
					for (int b = 0; b < numBands; b++) {
						J.setElement(0, 0, J.getElement(0, 0) + pxlX[b]
								* pxlX[b]);
						J.setElement(0, 1, J.getElement(0, 1) + pxlX[b]
								* pxlY[b]);
						J.setElement(1, 0, J.getElement(1, 0) + pxlY[b]
								* pxlX[b]);
						J.setElement(1, 1, J.getElement(1, 1) + pxlY[b]
								* pxlY[b]);
					}
					if (x == (rX.getWidth() - 1) && y == (rY.getHeight() - 1)) {
						J.setElement(0, 0, aa);
						J.setElement(0, 1, ab);
						J.setElement(1, 0, ab);
						J.setElement(1, 1, bb);
					}
					// IqmBoardPanel.appendTexln("IqmOpDirLocal: Matrix_Flanagan 0, 0 : "+
					// J.getElement(0, 0));
					// IqmBoardPanel.appendTexln("IqmOpDirLocal: Matrix_Flanagan 0, 1 : "+
					// J.getElement(0, 1));
					// IqmBoardPanel.appendTexln("IqmOpDirLocal: Matrix_Flanagan 1, 0 : "+
					// J.getElement(1, 0));
					// IqmBoardPanel.appendTexln("IqmOpDirLocal: Matrix_Flanagan 1, 1 : "+
					// J.getElement(1, 1));
					double[] val = J.getSortedEigenValues();
					double[][] vec = J.getSortedEigenVectorsAsColumns();

					for (int i = 0; i < 2; i++) {
						// IqmBoardPanel.appendTexln("IqmOpDirLocal: Eigenvalue Flanagan "+i+": "+
						// val[i]);
					}
					for (int i = 0; i < 2; i++) {
						String vecStr = " ";
						for (int j = 0; j < 2; j++) {
							vecStr = vecStr + " " + vec[i][j]; // eigenvectors
																// as columns
						}
						// IqmBoardPanel.appendTexln("IqmOpDirLocal: Eigenvector Flanagan "+i+": "+
						// vecStr);
					}
					// Index of confidence: lamda1/(lamda1 + lamda2)
					float idxConf = (float) (val[0] / (val[0] + val[1]));

					// calculate angle
					double a[] = new double[2];
					double b[] = new double[2];
					// set a;
					a[0] = 1; // unity vector in x direction
					a[1] = 0;
					// set b; //first eigenvector
					b[0] = vec[0][0];
					b[1] = vec[0][1];
					// double sp = Basic.vecvec(l, u, shift, a, b);
					// //ScalarProdukt //0� ==> 1; 90� ==> 0;
					double sp = 1.0;
					float angle = (float) Math.acos(sp); // for unity vectors ok
															// //1st and 3rd
															// quadrant
					if (b[0] * b[1] < 0)
						angle = -angle; // 2nd and 4th quadrant
					if (outImg == 0)
						tiOut.setSample(x, y, 0, angle / 90.0 * 255.0); // Angle
					if (outImg == 1)
						tiOut.setSample(x, y, 0, idxConf * 255.f); // Index of
																	// Confidence

					if (showVecPlot == 1) {
						// vector perpendicular to first eigenvector, which
						// directs to the greatest gradient
						tiVP.setSample(x, y, 0, -(float) vec[0][1] * 5); // multiply
																			// with
																			// constant
																			// because
																			// unity
																			// vectors
																			// are
																			// small
																			// and
																			// can't
																			// bee
																			// seen
																			// in
																			// a
																			// plot
						tiVP.setSample(x, y, 1, (float) vec[0][0] * 5);
					}

				} // flanagan Jakobi

				if (eigen == 1 || eigen == 2) { // Lau Inverse Iteration || QR

					double[][] J = new double[3][3];
					for (int b = 0; b < numBands; b++) {
						J[1][1] = J[1][1] + pxlX[b] * pxlX[b];
						J[1][2] = J[1][2] + pxlX[b] * pxlY[b];
						J[2][1] = J[2][1] + pxlX[b] * pxlY[b];
						J[2][2] = J[2][2] + pxlY[b] * pxlY[b];
					}
					if (x == (rX.getWidth() - 1) && y == (rY.getHeight() - 1)) {
						J[1][1] = aa;
						J[1][2] = ab;
						J[2][1] = ab;
						J[2][2] = bb;
					}
					// IqmBoardPanel.appendTexln("IqmOpDirLocal: Matrix 1, 1 : "+
					// J[1][1]);
					// IqmBoardPanel.appendTexln("IqmOpDirLocal: Matrix 1, 2 : "+
					// J[1][2]);
					// IqmBoardPanel.appendTexln("IqmOpDirLocal: Matrix 2, 1 : "+
					// J[2][1]);
					// IqmBoardPanel.appendTexln("IqmOpDirLocal: Matrix 2, 2 : "+
					// J[2][2]);
					int n = 2; // Order of Matrix
					int numval = 2; // Number of Eigenvalues(vectors) to be
									// calculated
					double[] val = new double[numval + 1]; // Eigenvalues
					double[][] vec = new double[n + 1][numval + 1]; // Eigenvectors
					double[] em = new double[10];
					em[0] = 1.0E-6; // Double.MIN_VALUE; //Machine Precision
					em[2] = 1.0E-5; // relative tolerance for the eigenvalues
					em[4] = 1.0E-3; // orthogonalization parameter
					em[6] = 1.0E-5; // relative tolerance for the eigenvectors
					em[8] = 5.0; // maximum number of inverse iterations allowed
									// for the calculation of each eigenvector
					// Lau S.228ff
					if (eigen == 1) { // Lau Inverse Iteration
						Linear_algebra.eigsym2(J, n, numval, val, vec, em); // Inverse
																			// Iteration
					}
					if (eigen == 2) { // Lau QR
						Linear_algebra.qrisym(J, numval, val, em); // QR
						vec = J;
					}
					for (int i = 0; i < (numval + 1); i++) {
						// IqmBoardPanel.appendTexln("IqmOpDirLocal: Eigenvalue "+i+": "+
						// val[i]);
					}
					for (int i = 0; i < (numval + 1); i++) {
						String vecStr = " ";
						for (int j = 0; j < (n + 1); j++) {
							vecStr = vecStr + " " + vec[j][i]; // eigenvectors
																// as columns
						}
						// IqmBoardPanel.appendTexln("IqmOpDirLocal: Eigenvector "+i+": "+
						// vecStr);
					}
					// Board.appendTexln("IqmOpDirLocal: Infinity norm of Matrix: "
					// + em[1]);
					// Board.appendTexln("IqmOpDirLocal: Number of iterations used for calculating "+numval+" eigenvalues: "
					// + em[3]);
					// Board.appendTexln("IqmOpDirLocal: Number of eigenvectors involved in the last Gram-Schmidt orthogonalization: "
					// + em[5]);
					// Board.appendTexln("IqmOpDirLocal: Maximum Euclidian norm of the residues of the calculated eigenvectors: "
					// + em[7]);
					// Board.appendTexln("IqmOpDirLocal: Largest number of inverse iterations performed for the calculation of some eigenvector: "
					// + em[9]);

					// scalar product
					int l = 1; // lower bound of the running subscripts
					int u = n; // upper bound of the running subscripts
					int shift = 0; // index shifting parameter
					double a[] = new double[3];
					double b[] = new double[3];
					// set a;
					a[1] = 1; // unity vector in x direction
					a[2] = 0;
					// set b; //first eigenvector
					b[1] = vec[n - 1][1];
					b[2] = vec[n][1];

					double sp = Basic.vecvec(l, u, shift, a, b); // ScalarProdukt
																	// //0� ==>
																	// 1; 90�
																	// ==> 0;
					float angle = (float) Math.acos(sp); // for unity vectors ok
															// //1st and 3rd
															// quadrant
					if (b[1] * b[2] < 0)
						angle = -angle; // 2nd and 4th quadrant
					// Board.appendTexln("IqmOpDirLocal: Scalar product: " +
					// sp);

					// Index of confidence: lamda1/(lamda1 + lamda2)
					float idxConf = (float) (val[1] / (val[1] + val[2]));
					// Set Output Value
					if (outImg == 0)
						tiOut.setSample(x, y, 0, angle / 90.0 * 255.0); // Angle
					if (outImg == 1)
						tiOut.setSample(x, y, 0, idxConf * 255.f); // Index of
																	// Confidence
					// Board.appendTexln("IqmOpDirLocal: angle: " +
					// angle*360.0/2.0/Math.PI);
					// Board.appendTexln("IqmOpDirLocal: Index of Confidence: "
					// + idxConf*255f);

					if (showVecPlot == 1) {
						// Vector perpendicular to eigenvector with largest
						// eigenvalue
						if (Double.isNaN(vec[n - 1][2])
								|| Double.isNaN(vec[n - 1][2])) { // NaN
							tiVP.setSample(x, y, 0, 0f);
							tiVP.setSample(x, y, 1, 0f);
						} else {// all other data
								// tiVP.getWritableTile(0, 0).setPixel(x, y, new
								// float[] {(float) b[1]*5, (float) b[2]*5});
							tiVP.setSample(x, y, 0, -(float) vec[1][2] * 5); // multiply
																				// with
																				// constant
																				// because
																				// unity
																				// vectors
																				// are
																				// small
																				// and
																				// can't
																				// bee
																				// seen
																				// in
																				// a
																				// plot
							tiVP.setSample(x, y, 1, (float) vec[1][1] * 5);
						}
					}// Lau

				}//
			}
		}
		if (showVecPlot == 1) {
			pbConvolve = new ParameterBlock();
			pbConvolve.addSource(tiVP);
			pbConvolve.add(1f / 10f);// x scale factor
			pbConvolve.add(1f / 10f);// y scale factor
			pbConvolve.add(0.0F);// x translate
			pbConvolve.add(0.0F);// y translate
			// pb.add(new InterpolationNearest());
			// pb.add(new InterpolationBilinear());
			// pb.add(new InterpolationBicubic(10)); //???? 1 oder was????
			// pb.add(new InterpolationBicubic2(10));

			// pb.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
			pbConvolve.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
			// pb.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
			// pb.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2));
			// RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
			// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
			PlanarImage piVP = JAI.create("scale", pbConvolve, rh);
			VectorPlotFrame vp = new VectorPlotFrame(piVP.getData(), fileName,
					imgName);
			vp.pack();
			// int horizontalPercent = 5;
			// int verticalPercent = 5;
			// RefineryUtilities.positionFrameOnScreen(vp, horizontalPercent,
			// verticalPercent);
			CommonTools.centerFrameOnScreen(vp);
			vp.setVisible(true);
		}
		// ParameterBlock pbF = new ParameterBlock();
		// pbF.addSource(tiOut);
		// pbF.add(DataBuffer.TYPE_BYTE); //Type angeben
		// PlanarImage piF = JAI.create("Format", pbF, null);

		ImageModel im = new ImageModel(tiOut);
		im.setModelName(imgName);
		im.setFileName(fileName);
		
		fireProgressChanged(95);
		if (isCancelled(getParentTask())){
			return null;
		}
		
		return new Result(im);
	}
	
	@Override
	public String getName() {
		if (this.name == null){
			this.name = new IqmOpDirLocalDescriptor().getName();
		}
		return this.name;
	}
	
		@Override
	public OperatorType getType() {
		return IqmOpDirLocalDescriptor.TYPE;
	}
}
