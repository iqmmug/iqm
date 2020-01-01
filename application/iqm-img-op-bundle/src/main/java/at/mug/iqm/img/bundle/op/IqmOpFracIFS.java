package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpFracIFS.java
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


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.BorderExtender;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.ParameterBlockImg;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.img.bundle.descriptors.IqmOpFracIFSDescriptor;

/**
 * Some code is adapted from:
 * THE NONLINEAR WORKBOOK
 * Chaos, Fractals, Cellular Automata, Neural Networks, Genetic Algorithms,
 * Gene Expression Programming, Support Vector Machine, Wavelets, 
 * Hidden Markov Models, Fuzzy Logic with C++, Java and SymbolicC++ Programs(4th Edition)
 * by Willi-Hans Steeb (University of Johannesburg, South Africa) 
 * see http://www.worldscibooks.com/chaos/6883.html
 * 
 * @author Ahammer, Kainz
 * @since   2009 10
 * @update 2016-12-16 bugfix: rescaling to original size for Menger
 */

public class IqmOpFracIFS extends AbstractOperator {

	public IqmOpFracIFS() {
		this.setCancelable(true);
	}

	/**
	 * Helper routine to calculate a to the power of n
	 * 
	 * @param a
	 * @param n
	 * @return the power of n
	 */
	public int power(int a, int n) {
		int t = 1;
		for (int i = 1; i <= n; i++) {
			t = t * a;
		}
		return t;
	}

	/**
	 * This method draws the Heighway's Dragon This is adapted from the
	 * Nonlinear Workbook
	 * 
	 * @param g
	 * @param scaling
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @param n
	 */
	public void dragonr(Graphics g, int scaling, int x1, int y1, int x2,
			int y2, int x3, int y3, int n) {
		if (n == 1) {
			g.drawLine(x1 + scaling, y1 + scaling, x2 + scaling, y2 + scaling);
			g.drawLine(x2 + scaling, y2 + scaling, x3 + scaling, y3 + scaling);
		} else {
			int x4 = (x1 + x3) / 2;
			int y4 = (y1 + y3) / 2;
			int x5 = x3 + x2 - x4;
			int y5 = y3 + y2 - y4;
			dragonr(g, scaling, x2, y2, x4, y4, x1, y1, n - 1);
			dragonr(g, scaling, x2, y2, x5, y5, x3, y3, n - 1);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public IResult run(IWorkPackage wp) {

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage piOut = null;

		// PlanarImage pi = (PlanarImage) pbJAI.getSource(0); //no source image needed
		PlanarImage pi;
		int width    = pb.getIntParameter("Width");
		int height   = pb.getIntParameter("Height");
		int itMax    = pb.getIntParameter("ItMax");
		int numPoly  = pb.getIntParameter("NumPoly");
		int fracType = pb.getIntParameter("FracType");

		// create new image
		SampleModel sampleModel = RasterFactory.createBandedSampleModel( DataBuffer.TYPE_BYTE, width, height, 1);
		// SampleModel sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,  width, height, 1);
		ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
		byte[] byteVector = new byte[width * height];
		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// index += 1;
				byteVector[index++] = 0;
			}
		}
		DataBufferByte db = new DataBufferByte(byteVector, width * height);
		Raster wr = RasterFactory.createWritableRaster(sampleModel, db, new Point(0, 0));
		TiledImage ti = new TiledImage(0, 0, width, height, 0, 0, sampleModel, colorModel);
		ti.setData(wr);
		pi = ti;
		piOut = pi;

		// Following parts are adapted from the book:
		// THE NONLINEAR WORKBOOK Chaos, Fractals, Cellular Automata, Neural Networks, Genetic Algorithms, Gene Expression Programming, Support Vector Machine, Wavelets,
		// Hidden Markov Models, Fuzzy Logic with C++, Java and SymbolicC++  Programs(4th Edition)
		// by Willi-Hans Steeb (University of Johannesburg, South Africa)
		// see http://www.worldscibooks.com/chaos/6883.html

		if (fracType == 0) {// Sierpinski Gasket Method 1
			// adapted from THE NONLINEAR WORKBOOK
			pi.setProperty("image_name", "IFS Sierpinski Gasket 1st method " + itMax + " iterations");
			pi.setProperty("file_name", "");
			Graphics g = pi.getGraphics();
			// g.drawRect(0, 0, imgWidth-1, imgHeight-1);
			int n, n1, l, k, m, u1l, u2l, v1l, v2l, xl;
			double u1, u2, v1, v2, a, h, s, x, y;
			// itMax = 4; // step in the construction
			double T[] = new double[itMax];
			a = Math.sqrt(3.0);
			for (m = 0; m <= itMax; m++) {
				int proz = (m + 1) * 100 / itMax;
				this.fireProgressChanged(proz);
				for (n = 0; n <= ((int) Math.exp(m * Math.log(3.0))); n++) {
					n1 = n;
					for (l = 0; l <= (m - 1); l++) {
						T[l] = n1 % 3;
						n1 = n1 / 3;
					}
					x = 0.0;
					y = 0.0;
					for (k = 0; k <= (m - 1); k++) {
						double temp = Math.exp(k * Math.log(2.0));
						x += Math.cos((4.0 * T[k] + 1.0) * Math.PI / 6.0) / temp;
						y += Math.sin((4.0 * T[k] + 1.0) * Math.PI / 6.0) / temp;
					}
					u1 = x + a / (Math.exp((m + 1.0) * Math.log(2.0)));
					u2 = x - a / (Math.exp((m + 1.0) * Math.log(2.0)));
					v1 = y - 1.0 / (Math.exp((m + 1.0) * Math.log(2.0)));
					v2 = y + 1.0 / (Math.exp(m * Math.log(2.0)));
					xl = (int) (width / 4 * x + width / 2 + 0.5); // imgWidth/4 gibt die absolute Gr��e an
					u1l = (int) (width / 4 * u1 + width / 2 + 0.5);
					u2l = (int) (width / 4 * u2 + width / 2 + 0.5);
					v1l = (int) (width / 4 * v1 + width / 10 * 6 + 0.5);
					v2l = (int) (width / 4 * v2 + width / 10 * 6 + 0.5); // imgWidth/10*6 gibt die vertikale Zentrierung an
					g.drawLine(u1l, v1l, xl, v2l);
					g.drawLine(xl, v2l, u2l, v1l);
					g.drawLine(u2l, v1l, u1l, v1l);
				}
			}
			piOut = pi;
		}
		if (fracType == 1) {// /Sierpinski Gasket Method 2
			pi.setProperty("image_name", "IFS Sierpinski Gasket 2nd method "
					+ itMax + " iterations");
			pi.setProperty("file_name", "");
			Graphics g = pi.getGraphics();
			g.setColor(Color.WHITE);

			// length of initial triangle
			int l = width / 10 * 10;
			// imgHeight of initial triangle
			int h = (int) (Math.sqrt(3.0d) / 2.0d * l);
			// Offset of the lower left point
			// int offSetX = imgWidth/2-l/2;
			int offSetX = 0;

			// initial triangle
			Polygon polygon = new Polygon();
			polygon.addPoint(offSetX - 1, 0);
			polygon.addPoint(width / 2 - 1, h - 1);
			polygon.addPoint(width - offSetX - 1, 0);
			// g.drawPolygon(polygon); result very bad (missing lines, scattered
			// lines, very dependent on interpolation method
			g.fillPolygon(polygon);

			ParameterBlock pbCrop = new ParameterBlock();
			pbCrop.addSource(pi);
			pbCrop.add(0.0f); // ((float) offSetX);
			pbCrop.add(0.0f); // ((float) offSetY);
			pbCrop.add((float) width); // (float) newWidth);
			pbCrop.add((float) h); // (float) newHeight);
			pi = JAI.create("Crop", pbCrop, null);
			piOut = pi;

			// Affine transformation
			Interpolation interP = null;
			// interP = new InterpolationNearest(); //am besten f�r gef�llte dreiecke
			interP = new InterpolationBilinear(); // am besten f�r gef�llte/ dreiecke
			// interP = new InterpolationBicubic(1); //???? 1 oder was????
			// //schlecht f�r gef�llte Dreiecke
			// interP = new InterpolationBicubic2(10);
			// AffineTransform at = new AffineTransform(m00, m10, m01, m11, m02, m12);
			// AffineTransform at = new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 20.0f, 0.0f);

			AffineTransform at1 = new AffineTransform(0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f);
			AffineTransform at2 = new AffineTransform(0.5f, 0.0f, 0.0f, 0.5f, 0.5f * width, 0.0f * width);
			AffineTransform at3 = new AffineTransform(0.5f, 0.0f, 0.0f, 0.5f, 1.0f / 4.0f * width, (float) (Math.sqrt(3) / 4.0f) * width);

			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY)); // nicht sicher ob n�tig
			PlanarImage pi1 = null;
			PlanarImage pi2 = null;
			PlanarImage pi3 = null;
			for (int i = 0; i < itMax; i++) {
				int proz = (i + 1) * 100 / itMax;
				this.fireProgressChanged(proz);
				pbCrop.removeParameters();
				pbCrop.removeSources();
				pbCrop.addSource(pi);
				pbCrop.add(at1);
				pbCrop.add(interP);
				pi1 = JAI.create("Affine", pbCrop, rh);

				pbCrop.removeParameters();
				pbCrop.removeSources();
				pbCrop.addSource(pi);
				pbCrop.add(at2);
				pbCrop.add(interP);
				pi2 = JAI.create("Affine", pbCrop, rh);

				pbCrop.removeParameters();
				pbCrop.removeSources();
				pbCrop.addSource(pi);
				pbCrop.add(at3);
				pbCrop.add(interP);
				pi3 = JAI.create("Affine", pbCrop, rh);

				double[][] threshold = { { 0 } };
				double[] bgColor = new double[] { 0 };
				pbCrop.removeParameters();
				pbCrop.removeSources();
				pbCrop.addSource(pi1);
				pbCrop.addSource(pi2);
				pbCrop.addSource(pi3);
				pbCrop.add(MosaicDescriptor.MOSAIC_TYPE_OVERLAY);
				pbCrop.add(null);
				pbCrop.add(null);
				pbCrop.add(null);
				pbCrop.add(null);
				pi = JAI.create("Mosaic", pbCrop);
			}

			// binarize da affine interpoliert
			pbCrop.removeParameters();
			pbCrop.removeSources();
			pbCrop.addSource(pi);
			pbCrop.add(1.0d); // Threshold
			pi = JAI.create("binarize", pbCrop, null);

			IndexColorModel icm = (IndexColorModel) (pi.getColorModel());
			byte[][] data = new byte[3][icm.getMapSize()];
			icm.getReds(data[0]);
			icm.getGreens(data[1]);
			icm.getBlues(data[2]);
			LookupTableJAI lut = new LookupTableJAI(data);
			pi = JAI.create("lookup", pi, lut); // RGB image

			pbCrop.removeSources();
			pbCrop.removeParameters();
			double[][] m = { { 0.114, 0.587, 0.299, 0 } };
			pbCrop.addSource(pi);
			pbCrop.add(m);
			pi = JAI.create("bandcombine", pbCrop, null);

			TransposeType flipType = TransposeDescriptor.FLIP_VERTICAL;
			pbCrop.removeParameters();
			pbCrop.removeSources();
			pbCrop.addSource(pi);
			pbCrop.add(flipType);
			pi = JAI.create("Transpose", pbCrop, null);
			piOut = pi;
		}

		if (fracType == 2) {// Fern
			// adapted from THE NONLINEAR WORKBOOK
			pi.setProperty("image_name", "IFS Fern " + itMax + " iterations");
			pi.setProperty("file_name", "");
			Graphics g = pi.getGraphics();

			double mxx, myy, bxx, byy;
			double x, y, xn, yn, r;
			int pex, pey;
			// int max = 15000; // number of iterations
			int max = itMax;
			x = 0.5;
			y = 0.0; // starting point

			// convert(0.0,1.0,1.0,-0.5);
			double xiz = 0.0d;
			double ysu = 1.0d;
			double xde = 1.0d;
			double yinf = -0.5d;
			double maxx, maxy, xxfin, xxcom, yyin, yysu;
			// maxx = 600; maxy = 450;
			maxx = width / 10 * 10;
			maxy = height / 10 * 22; // links rechts bzw H�he
			xxcom = 0.15 * maxx;
			xxfin = 0.75 * maxx;
			yyin = 0.8 * maxy;
			yysu = 0.2 * maxy;
			mxx = 2.5 * (xxfin - xxcom) / (xde - xiz); // Breite
			bxx = 0.415 * (xxcom + xxfin - mxx * (xiz + xde)); // links rechts
			myy = 1.0 * (yyin - yysu) / (yinf - ysu);
			byy = 0.37 * (yysu + yyin - myy * (yinf + ysu)); // oben unten

			// Original values (too small)
			// maxx = 600; maxy = 450; //links rechts bzw H�he
			// xxcom = 0.15*maxx; xxfin = 0.75*maxx;
			// yyin = 0.8*maxy; yysu = 0.2*maxy;
			// mxx = (xxfin-xxcom)/(xde-xiz);
			// bxx = 0.5*(xxcom+xxfin-mxx*(xiz+xde));
			// myy = (yyin-yysu)/(yinf-ysu);
			// byy = 0.5*(yysu+yyin-myy*(yinf+ysu));

			// setBackground(Color.white);
			// g.setColor(Color.black);
			for (int i = 0; i <= max; i++) {
				int proz = (i + 1) * 100 / max;
				this.fireProgressChanged(proz);

				r = Math.random(); // generate a random number
				if (r <= 0.02) {
					xn = 0.5;
					yn = 0.27 * y;
				} // map 1
				else if ((r > 0.02) && (r <= 0.17)) {
					xn = -0.139 * x + 0.263 * y + 0.57; // map 2
					yn = 0.246 * x + 0.224 * y - 0.036;
				} else if ((r > 0.17) && (r <= 0.3)) {
					xn = 0.17 * x - 0.215 * y + 0.408; // map 3
					yn = 0.222 * x + 0.176 * y + 0.0893;
				} else {
					xn = 0.781 * x + 0.034 * y + 0.1075; // map 4
					yn = -0.032 * x + 0.739 * y + 0.27;
				}
				x = xn;
				y = yn;
				pex = (int) (mxx * x + bxx);
				pey = (int) (myy * y + byy);
				g.drawLine(pex, pey, pex, pey); // output to screen
			}
			piOut = pi;
		}
		if (fracType == 3) {// Koch Line
			// adapted from THE NONLINEAR WORKBOOK
			pi.setProperty("image_name", "IFS Koch Line " + itMax + " iterations");
			pi.setProperty("file_name", "");
			Graphics g = pi.getGraphics();

			int p, m, u, v, u1, v1, a, b;
			double h, s, x, y, x1, y1;
			// p = 3; // step in the construction
			p = itMax;
			int kmax = 200;
			// int kmax = itMax;
			int T[] = new int[kmax];
			m = 0;
			// h = 1.0/((double) this.power(3,p)); //Original
			h = 1.0 / this.power(3, p); // Gr��e
			x = 0.0;
			y = 0.0;
			int nMax = (this.power(4, p) - 1);
			for (int n = 1; n <= nMax; n++) {
				int proz = (n + 1) * 100 / nMax;
				this.fireProgressChanged(proz);
				m = n;
				{
					for (int l = 0; l <= (p - 1); l++) {
						T[l] = m % 4;
						m = m / 4;
					}
					s = 0.0; // Winkelorientierung
					for (int k = 0; k <= (p - 1); k++) {
						s += ((T[k] + 1) % 3 - 1);
					}
					x1 = x;
					y1 = y;
					x += Math.cos(Math.PI * s / 3.0) * h;
					y += Math.sin(Math.PI * s / 3.0) * h;
					u = (int) (width / 100 * 95 * x + 10 + 0.5);
					v = (int) (width / 100 * 95 * y + height / 2 + 0.5);
					u1 = (int) (width / 100 * 95 * x1 + 10 + 0.5);
					v1 = (int) (width / 100 * 95 * y1 + height / 2 + 0.5);
					g.drawLine(u, v, u1, v1);
				}
			}
			piOut = pi;
		}
		if (fracType == 4) {// Koch Snowflake
			pi.setProperty("image_name", "IFS Koch Snowflake " + itMax + " iterations");
			pi.setProperty("file_name", "");
			Graphics g = pi.getGraphics();
			int mPoly = numPoly; // Mehrseitiges Polygon als Inititator (von 3 bis-------)
			if (mPoly < 3)
				mPoly = 3;
			double theta = 2d * Math.PI / mPoly;
			double gamma = Math.PI - theta;
			if (mPoly == 3)
				gamma = gamma + (3d * (Math.PI / 180d)); // !!!!!!!!!!

			System.out.println("Initiator: " + mPoly + "seitiges Polygon");
			// System.out.println("gamma: "+ gamma*180/(Math.PI));

			int n = mPoly; // Anzahl der Vektoren
			double[][] xOld = new double[2][n]; // Anfangswerte
			double[][] yOld = new double[2][n];

			// Bestimmung der Seitenl�nge und Position des Polygons, daher sind alle Polygone schoen im Bild
			// double radiusAussen = 150.0; //Aussenradius eines Polygons
			double radiusAussen = width / 3; // Aussenradius eines Polygons
			double radiusInnen = radiusAussen * Math.cos(theta / 2); // InnenRadius
			double seite = 2d * radiusAussen * Math.sin(theta / 2); // Seitenl�nge des Polygons

			xOld[0][0] = (width / 2 - seite / 2);
			xOld[1][0] = (width / 2 + seite / 2);

			yOld[0][0] = (width / 2 - radiusInnen);
			yOld[1][0] = (width / 2 - radiusInnen);

			// first line
			// PLOTS, [XOld[0,0], XOld[1,0]], [YOld[0,0], YOld[1,0]], /DEVICE
			// g.drawLine((int)xOld[0][0], (int)yOld[0][0], (int)xOld[1][0], (int)yOld[1][0]);

			double betrag = (Math.sqrt(Math.pow(yOld[1][0] - yOld[0][0], 2)
					+ Math.pow(xOld[1][0] - xOld[0][0], 2)));

			// mPoly-1 Vektoren, Anfangspolynom

			for (int i = 1; i < mPoly; i++) {
				int vecPosOld = i - 1;
				double alpha = (Math
						.atan((yOld[1][vecPosOld] - yOld[0][vecPosOld])
								/ (xOld[1][vecPosOld] - xOld[0][vecPosOld])));// Winkel des alten Vektors

				// Korrektur fueSonderfaelle: Vektor genau auf einer der 4Achsenrichtungen bzw. fuer 2.ten, 3.ten, 4.ten Quadranten
				if ((yOld[1][vecPosOld] > yOld[0][vecPosOld])
						&& (xOld[1][vecPosOld] == xOld[0][vecPosOld]))
					alpha = Math.PI / 2;
				if ((yOld[1][vecPosOld] < yOld[0][vecPosOld])
						&& (xOld[1][vecPosOld] == xOld[0][vecPosOld]))
					alpha = Math.PI * 3d / 2d;
				if ((yOld[1][vecPosOld] == yOld[0][vecPosOld])
						&& (xOld[1][vecPosOld] > xOld[0][vecPosOld]))
					alpha = 0d;
				if ((yOld[1][vecPosOld] == yOld[0][vecPosOld])
						&& (xOld[1][vecPosOld] < xOld[0][vecPosOld]))
					alpha = Math.PI;
				if ((yOld[1][vecPosOld] > yOld[0][vecPosOld])
						&& (xOld[1][vecPosOld] < xOld[0][vecPosOld]))
					alpha = Math.PI + alpha;
				if ((yOld[1][vecPosOld] < yOld[0][vecPosOld])
						&& (xOld[1][vecPosOld] < xOld[0][vecPosOld]))
					alpha = Math.PI + alpha;
				if ((yOld[1][vecPosOld] < yOld[0][vecPosOld])
						&& (xOld[1][vecPosOld] > xOld[0][vecPosOld]))
					alpha = (2d * Math.PI) + alpha;

				alpha = alpha + theta;

				// Neuer Vektor
				xOld[0][i] = xOld[1][i - 1];
				xOld[1][i] = xOld[1][i - 1] + betrag * Math.cos(alpha);

				yOld[0][i] = yOld[1][i - 1];
				yOld[1][i] = yOld[1][i - 1] + betrag * Math.sin(alpha);

				// rest of initial polygon
				// PLOTS, [xOld[0,i], xOld[1,i]], [yOld[0,i], yOld[1,i]], /DEVICE
				// g.drawLine((int)xOld[0][i], (int)yOld[0][i], (int)xOld[1][i], (int)yOld[1][i]);

			}

			// *******Hauptschleife Iteration*******************

			int iMax = itMax; // 8 //only the last iteration draws the lines
			for (int i = 1; i <= iMax; i++) {
				int proz = (i + 1) * 100 / iMax;
				this.fireProgressChanged(proz);

				System.out.println("Iteration Nr.: " + i);

				// VectorSize = SIZE(xOld)
				int vectorSize = xOld[0].length;

				// if i == 1 THEN VectorSize = [0][0][1] ELSE VectorSize =
				// SIZE(xOld)
				double[][] x = new double[2][3 * vectorSize];
				double[][] y = new double[2][3 * vectorSize];

				for (int nn = 1; nn <= vectorSize; nn++) {// Vektorschleife

					// 1.ter neuer Teilvektor
					int vecPosNeu = 3 * (nn - 1);
					int vecPosOld = (nn - 1);

					double betragOld = Math.sqrt(Math.pow(yOld[1][0] - yOld[0][0], 2) + Math.pow(xOld[1][0] - xOld[0][0], 2));
					double betragNeu = (betragOld / 2) / Math.sqrt(1.25 - Math.cos(gamma));
					double alpha = Math.atan((yOld[1][vecPosOld] - yOld[0][vecPosOld]) / (xOld[1][vecPosOld] - xOld[0][vecPosOld]));// Winkel des alten Vektors

					// Korrektur für Sonderfälle: Vektor genau auf einer der 4Achsenrichtungen bzw.für 2.ten, 3.ten, 4.ten Quadranten
					if ((yOld[1][vecPosOld] > yOld[0][vecPosOld])
							&& (xOld[1][vecPosOld] == xOld[0][vecPosOld]))
						alpha = Math.PI / 2;
					if ((yOld[1][vecPosOld] < yOld[0][vecPosOld])
							&& (xOld[1][vecPosOld] == xOld[0][vecPosOld]))
						alpha = Math.PI * 3d / 2d;
					if ((yOld[1][vecPosOld] == yOld[0][vecPosOld])
							&& (xOld[1][vecPosOld] > xOld[0][vecPosOld]))
						alpha = 0d;
					if ((yOld[1][vecPosOld] == yOld[0][vecPosOld])
							&& (xOld[1][vecPosOld] < xOld[0][vecPosOld]))
						alpha = Math.PI;
					if ((yOld[1][vecPosOld] > yOld[0][vecPosOld])
							&& (xOld[1][vecPosOld] < xOld[0][vecPosOld]))
						alpha = Math.PI + alpha;
					if ((yOld[1][vecPosOld] < yOld[0][vecPosOld])
							&& (xOld[1][vecPosOld] < xOld[0][vecPosOld]))
						alpha = Math.PI + alpha;
					if ((yOld[1][vecPosOld] < yOld[0][vecPosOld])
							&& (xOld[1][vecPosOld] > xOld[0][vecPosOld]))
						alpha = (2d * Math.PI) + alpha;

					alpha = Math.asin((betragNeu / 2) * (Math.sin(gamma) / (betragOld / 2))) + alpha; // Neuer Winkel f�r 1.ten Vektor

					x[0][vecPosNeu] = xOld[0][vecPosOld];
					x[1][vecPosNeu] = xOld[0][vecPosOld] + Math.cos(alpha) * betragNeu;
					y[0][vecPosNeu] = yOld[0][vecPosOld];
					y[1][vecPosNeu] = yOld[0][vecPosOld] + Math.sin(alpha) * betragNeu;

					// PLOTS, [X[0,vecPosNeu], X[1,vecPosNeu]],[Y[0,vecPosNeu], Y[1,vecPosNeu]], /DEVICE
					if (i == iMax)
						g.drawLine((int) x[0][vecPosNeu], (int) y[0][vecPosNeu], (int) x[1][vecPosNeu], (int) y[1][vecPosNeu]);

					// 2.ter neuer Teilvektor
					vecPosNeu = vecPosNeu + 1;
					x[0][vecPosNeu] = x[1][vecPosNeu - 1];
					y[0][vecPosNeu] = y[1][vecPosNeu - 1];
					double beta = Math.PI - alpha - gamma;

					x[1][vecPosNeu] = x[1][vecPosNeu - 1] + Math.cos(beta) * betragNeu;
					y[1][vecPosNeu] = y[1][vecPosNeu - 1] - Math.sin(beta) * betragNeu;

					// PLOTS, [X[0,vecPosNeu], X[1,vecPosNeu]],[Y[0,vecPosNeu], Y[1,vecPosNeu]], /DEVICE
					if (i == iMax)
						g.drawLine((int) x[0][vecPosNeu], (int) y[0][vecPosNeu], (int) x[1][vecPosNeu], (int) y[1][vecPosNeu]);

					// 3.ter neuer TeilVektor
					vecPosNeu = vecPosNeu + 1;
					x[0][vecPosNeu] = x[1][vecPosNeu - 1];
					x[1][vecPosNeu] = x[1][vecPosNeu - 1] + Math.cos(alpha) * betragNeu;
					y[0][vecPosNeu] = y[1][vecPosNeu - 1];
					y[1][vecPosNeu] = y[1][vecPosNeu - 1] + Math.sin(alpha) * betragNeu;

					// PLOTS, [X[0,vecPosNeu], X[1,vecPosNeu]],[Y[0,vecPosNeu], Y[1,vecPosNeu]], /DEVICE
					if (i == iMax)
						g.drawLine((int) x[0][vecPosNeu], (int) y[0][vecPosNeu], (int) x[1][vecPosNeu], (int) y[1][vecPosNeu]);

				} // Vektorschleife

				xOld = new double[2][3 * vectorSize]; // R�cksetzen
				yOld = new double[2][3 * vectorSize];
				xOld = x;
				yOld = y;

			} // Hauptschleife Iteration i

			piOut = pi;
		}
		if (fracType == 5) {// Menger

			// this algorithm properly works only for image sizes
			// 2*3*3*3*3.......
			int tempWidth = 2;
			int tempHeight = 2;

			while (width > tempWidth) {
				tempWidth = tempWidth * 3;
			}
			tempHeight = tempWidth;

			System.out.println("IqmOpFracIFS:     width:   " + width);
			System.out.println("IqmOpFracIFS:     tempWidth:   " + tempWidth);

			ParameterBlock pbExtender = new ParameterBlock();
			RenderingHints rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
					BorderExtender.createInstance(BorderExtender.BORDER_COPY));

			// Define this for the output image
			// pi.setProperty("image_name", "IFS Menger Carpet "+ itMax + " iterations");
			// pi.setProperty("file_name", "");

			// Graphics g = pi.getGraphics();
			// g.setColor(Color.WHITE);

			// Polygon polygon = new Polygon();
			// polygon.addPoint(Math.round((float)tempWidth/3),
			// Math.round((float)tempHeight/3));
			// polygon.addPoint(Math.round((float)tempWidth/3),
			// Math.round((float)tempHeight/3*2));
			// polygon.addPoint(Math.round((float)tempWidth/3*2),
			// Math.round((float)tempHeight/3*2));
			// polygon.addPoint(Math.round((float)tempWidth/3*2),
			// Math.round((float)tempHeight/3));
			// g.fillPolygon(polygon);

			//
			// float zoomX = 0.0f;
			// float zoomY = 0.0f;
			// //rescale image
			// zoomX = (float)tempWidth / (float)width;
			// zoomY = (float)tempHeight / (float)height;
			// pb = new ParameterBlock();
			// pb.addSource(ti);
			// pb.add(zoomX);//x scale factor
			// pb.add(zoomY);//y scale factor
			// pb.add(0.0F);//x translate
			// pb.add(0.0F);//y translate
			// int optIntP = 1;
			// if (optIntP == 0)
			// pb.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
			// if (optIntP == 1)
			// pb.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
			// if (optIntP == 2)
			// pb.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
			// if (optIntP == 3)
			// pb.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2));
			// rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
			// BorderExtender.createInstance(BorderExtender.BORDER_COPY));
			// pi = JAI.create("scale", pb, rh);
			//
			// sampleModel =
			// RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE,
			// tempWidth, tempHeight, 1);
			sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1);
			colorModel = PlanarImage.createColorModel(sampleModel);
			byteVector = new byte[tempWidth * tempHeight];
			index = 0;
			for (int y = 0; y < tempHeight; y++) {
				for (int x = 0; x < tempWidth; x++) {
					// index += 1;
					byteVector[index++] = 0;
				}
			}
			db = new DataBufferByte(byteVector, tempWidth * tempHeight);
			wr = RasterFactory.createWritableRaster(sampleModel, db, new Point(0, 0));
			ti = new TiledImage(0, 0, tempWidth, tempHeight, 0, 0, sampleModel, colorModel);
			ti.setData(wr);

			// set initial quadrat
			int xMin = Math.round((float) tempWidth / 3);
			int xMax = Math.round((float) tempWidth / 3 * 2);
			int yMin = Math.round((float) tempHeight / 3);
			int yMax = Math.round((float) tempHeight / 3 * 2);
			for (int x = xMin - 1; x < xMax - 1; x++) {
				for (int y = yMin - 1; y < yMax - 1; y++) {
					ti.setSample(x, y, 0, 255);
				}
			}
			pi = ti;

			// Affine transformation
			Interpolation interP = null;
			// interP = new InterpolationNearest();
			interP = new InterpolationBilinear();
			// interP = new InterpolationBicubic(1); //???? 1 oder was????
			// interP = new InterpolationBicubic2(10);
			// AffineTransform at = new AffineTransform(m00, m10, m01, m11, m02,
			// m12);
			// AffineTransform at = new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f,
			// 20.0f, 0.0f);

			// tempWidth = tempWidth+1;
			// tempHeight = tempHeight + 1;
			AffineTransform at1 = new AffineTransform(1.0f / 3.0f, 0.0f, 0.0f, 1.0f / 3.0f, 0.0f * tempWidth, 0.0f * tempHeight);
			AffineTransform at2 = new AffineTransform(1.0f / 3.0f, 0.0f, 0.0f, 1.0f / 3.0f, 0.0f * tempWidth, 1.0f / 3.0f * tempHeight);
			AffineTransform at3 = new AffineTransform(1.0f / 3.0f, 0.0f, 0.0f, 1.0f / 3.0f, 0.0f * tempWidth, 2.0f / 3.0f * tempHeight);
			AffineTransform at4 = new AffineTransform(1.0f / 3.0f, 0.0f, 0.0f, 1.0f / 3.0f, 1.0f / 3.0f * tempWidth, 2.0f / 3.0f * tempHeight);
			AffineTransform at5 = new AffineTransform(1.0f / 3.0f, 0.0f, 0.0f, 1.0f / 3.0f, 2.0f / 3.0f * tempWidth, 2.0f / 3.0f * tempHeight);
			AffineTransform at6 = new AffineTransform(1.0f / 3.0f, 0.0f, 0.0f, 1.0f / 3.0f, 2.0f / 3.0f * tempWidth, 1.0f / 3.0f * tempHeight);
			AffineTransform at7 = new AffineTransform(1.0f / 3.0f, 0.0f, 0.0f, 1.0f / 3.0f, 2.0f / 3.0f * tempWidth, 0.0f * tempHeight);
			AffineTransform at8 = new AffineTransform(1.0f / 3.0f, 0.0f, 0.0f, 1.0f / 3.0f, 1.0f / 3.0f * tempWidth, 0.0f * tempHeight);
			AffineTransform at9 = new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f * tempWidth, 0.0f * tempHeight);

			rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY)); // nicht sicher ob n?tig

			PlanarImage pi1 = null;
			PlanarImage pi2 = null;
			PlanarImage pi3 = null;
			PlanarImage pi4 = null;
			PlanarImage pi5 = null;
			PlanarImage pi6 = null;
			PlanarImage pi7 = null;
			PlanarImage pi8 = null;
			PlanarImage pi9 = null;

			pbExtender = new ParameterBlock();

			for (int i = 0; i < itMax; i++) {
				int proz = (i + 1) * 100 / itMax;
				this.fireProgressChanged(proz);

				pbExtender.removeParameters();
				pbExtender.removeSources();
				pbExtender.addSource(pi);
				pbExtender.add(at1);
				pbExtender.add(interP);
				pi1 = JAI.create("Affine", pbExtender, rh);

				pbExtender.removeParameters();
				pbExtender.removeSources();
				pbExtender.addSource(pi);
				pbExtender.add(at2);
				pbExtender.add(interP);
				pi2 = JAI.create("Affine", pbExtender, rh);

				pbExtender.removeParameters();
				pbExtender.removeSources();
				pbExtender.addSource(pi);
				pbExtender.add(at3);
				pbExtender.add(interP);
				pi3 = JAI.create("Affine", pbExtender, rh);

				pbExtender.removeParameters();
				pbExtender.removeSources();
				pbExtender.addSource(pi);
				pbExtender.add(at4);
				pbExtender.add(interP);
				pi4 = JAI.create("Affine", pbExtender, rh);

				pbExtender.removeParameters();
				pbExtender.removeSources();
				pbExtender.addSource(pi);
				pbExtender.add(at5);
				pbExtender.add(interP);
				pi5 = JAI.create("Affine", pbExtender, rh);

				pbExtender.removeParameters();
				pbExtender.removeSources();
				pbExtender.addSource(pi);
				pbExtender.add(at6);
				pbExtender.add(interP);
				pi6 = JAI.create("Affine", pbExtender, rh);

				pbExtender.removeParameters();
				pbExtender.removeSources();
				pbExtender.addSource(pi);
				pbExtender.add(at7);
				pbExtender.add(interP);
				pi7 = JAI.create("Affine", pbExtender, rh);

				pbExtender.removeParameters();
				pbExtender.removeSources();
				pbExtender.addSource(pi);
				pbExtender.add(at8);
				pbExtender.add(interP);
				pi8 = JAI.create("Affine", pbExtender, rh);

				pbExtender.removeParameters();
				pbExtender.removeSources();
				pbExtender.addSource(pi);
				pbExtender.add(at9);
				pbExtender.add(interP);
				pi9 = JAI.create("Affine", pbExtender, rh);

				double[][] threshold = { { 0 } };
				double[] bgColor = new double[] { 0 };
				pbExtender.removeParameters();
				pbExtender.removeSources();
				pbExtender.addSource(pi1);
				pbExtender.addSource(pi2);
				pbExtender.addSource(pi3);
				pbExtender.addSource(pi4);
				pbExtender.addSource(pi5);
				pbExtender.addSource(pi6);
				pbExtender.addSource(pi7);
				pbExtender.addSource(pi8);
				pbExtender.addSource(pi9);
				pbExtender.add(MosaicDescriptor.MOSAIC_TYPE_OVERLAY);
				// pb.add(null);
				pbExtender.add(null);
				pbExtender.add(null);
				pbExtender.add(null);
				pbExtender.add(null);
				// pi = null;
				pi = JAI.create("Mosaic", pbExtender);
			}

			 //rescale to original size
			 float zoomX = (float)width /(float)tempWidth;
			 float zoomY = (float)height/(float)tempHeight;
			 ParameterBlock pbRescale = new ParameterBlock();
			 pbRescale.addSource(pi);
			 pbRescale.add(zoomX);//x scale factor
			 pbRescale.add(zoomY);//y scale factor
			 pbRescale.add(0.0F);//x translate
			 pbRescale.add(0.0F);//y translate
			 int optIntP = 0;
			 if (optIntP == 0) pbRescale.add(Interpolation.getInstance(Interpolation.INTERP_NEAREST));
			 if (optIntP == 1) pbRescale.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
			 if (optIntP == 2) pbRescale.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC));
			 if (optIntP == 3) pbRescale.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2));
			
			 rh = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
			 BorderExtender.createInstance(BorderExtender.BORDER_COPY));
			 pi = JAI.create("scale", pbRescale, rh);

			 //binarize
			 ParameterBlock pbBin = new ParameterBlock();
			 pbBin.addSource(pi);
			 pbBin.add(126.0d);//threshold
			 pi = JAI.create("binarize", pbBin, rh);

			piOut = pi;

			piOut.setProperty("image_name", "IFS Menger Carpet " + itMax + " iterations");
			piOut.setProperty("file_name", "");

		}// Menger

		if (fracType == 6) {// Heighway's Dragon
			pi.setProperty("image_name", "IFS Heighway's Dragon " + itMax + " iterations");
			pi.setProperty("file_name", "");
			Graphics g = pi.getGraphics();
			g.setColor(Color.WHITE);

			// Polygon polygon = new Polygon();
			// polygon.addPoint(imgWidth/3, imgHeight/3);
			// polygon.addPoint(imgWidth/3, imgHeight/3*2);
			// polygon.addPoint(imgWidth/3*2, imgHeight/3*2);
			// polygon.addPoint(imgWidth/3*2, imgHeight/3);
			// g.fillPolygon(polygon);

			int scaling = (int) (Math.min(width, height) / 3.65);
			int xorig = scaling;
			int yorig = scaling;
			int x1 = xorig + scaling;
			int y1 = yorig;
			int x2 = xorig;
			int y2 = yorig - scaling;
			int x3 = xorig - scaling;
			int y3 = yorig;
			dragonr(g, scaling, x1, y1, x2, y2, x3, y3, itMax);

			piOut = pi;
		}// Heighwway's Dragon

		ImageModel im = new ImageModel(piOut);
		im.setModelName(String.valueOf(piOut.getProperty("image_name")));
		
		if (isCancelled(getParentTask())){
			return null;
		}
		return new Result(im);
	}

	@Override
	public String getName() {
		if (this.name == null) {
			this.name = new IqmOpFracIFSDescriptor().getName();
		}
		return this.name;
	}

	@Override
	public OperatorType getType() {
		return IqmOpFracIFSDescriptor.TYPE;
	}
}
