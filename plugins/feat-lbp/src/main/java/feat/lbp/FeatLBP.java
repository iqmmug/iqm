package feat.lbp;

/*
* This file is part of IQM, hereinafter referred to as "this program".
* 
* Copyright (C) 2009 - 2014 Helmut Ahammer, Philipp Kainz
* 
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
*/

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import at.mug.iqm.api.model.ImageModel;
import at.mug.iqm.api.model.IqmDataBox;
import at.mug.iqm.api.model.PlotModel;
import at.mug.iqm.api.model.TableModel;
import at.mug.iqm.api.operator.AbstractOperator;
import at.mug.iqm.api.operator.IOperatorDescriptor;
import at.mug.iqm.api.operator.IResult;
import at.mug.iqm.api.operator.IWorkPackage;
import at.mug.iqm.api.operator.OperatorType;
import at.mug.iqm.api.operator.ParameterBlockIQM;
import at.mug.iqm.api.operator.Result;
import at.mug.iqm.api.operator.WorkPackage;

/**
 * Create a Local Binary Pattern descriptor from a given image patch.
 * 
 * @author Philipp Kainz
 * 
 */
public class FeatLBP extends AbstractOperator {

	/**
	 * The default constructor for the operator. 
	 */
	public FeatLBP() {
		// declare that this operator is not cancelable (since we do not
		// implement the checks)
		setCancelable(false);
	}

	/**
	 * Gets the unique name of the operator.
	 * 
	 * This method is merely a wrapper for the unique name of the operator
	 * stored in the operator's associated {@link IOperatorDescriptor}.
	 * 
	 * @return the name of the operator, declared in the operator's descriptor
	 */
	public String getName() {
		if (this.name == null) {
			this.name = new FeatLBPDescriptor().getName();
		}
		return this.name;
	}

	/**
	 * Gets the {@link OperatorType} of the operator.
	 * 
	 * This method is merely a wrapper for the type of the operator stored in
	 * the operator's associated {@link IOperatorDescriptor}.
	 * 
	 * @return the type of the operator, declared in the operator's descriptor
	 */
	public OperatorType getType() {
		if (this.type == null) {
			this.type = FeatLBPDescriptor.TYPE;
		}
		return this.type;
	}

	/**
	 * This is the starting point for the execution of the operator.
	 * 
	 * The algorithm code is launched using the {@link #run(IWorkPackage)}
	 * method and returns a {@link IResult}.
	 * 
	 * @param wp
	 *            the work package for this algorithm
	 * @return a result of the processed work package
	 */
	@Override
	public IResult run(IWorkPackage wp) throws Exception {
		// construct a new result container
		Result result = new Result();

		// extraction of the containing image at index 0
		ParameterBlockIQM pb = wp.getParameters();
		// Get the planar image at source index 0
		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage();

		// get the parameters
		int neighbours = pb.getIntParameter("neighbours");
		float radius = pb.getFloatParameter("radius");
		int kernelSize = pb.getIntParameter("kernelsize");
		boolean smooth = pb.getBooleanParameter("smooth");
		int cells = pb.getIntParameter("cells");

		// the full feature vector
		Vector<Double> featVec = new Vector<Double>(cells * cells
				* (int) (Math.pow(2, neighbours)));

		// get original image width and height
		int width = pi.getWidth();
		int height = pi.getHeight();

		// ##########################################
		// BORDER HANDLING
		ParameterBlock pbBrdr = new ParameterBlock();
		pbBrdr.addSource(pi);

		// round the radius to the next higher integer for border processing
		int ext = (int) Math.ceil(radius);
		pbBrdr.add(ext);
		pbBrdr.add(ext);
		pbBrdr.add(ext);
		pbBrdr.add(ext);
		pbBrdr.add(BorderExtender.createInstance(BorderExtender.BORDER_REFLECT));
		pi = JAI.create("border", pbBrdr, null);

		if (pi.getMinX() != 0 || pi.getMinY() != 0) {
			ParameterBlockJAI pbTrans = new ParameterBlockJAI("translate");
			pbTrans.addSource(pi);
			pbTrans.setParameter("xTrans", pi.getMinX() * -1.0f);
			pbTrans.setParameter("yTrans", pi.getMinY() * -1.0f);
			pi = JAI.create("translate", pbTrans);
		}

		// ##########################################
		// OPTIONAL GAUSSIAN LOW-PASS FILTERING
		if (smooth) {
			WorkPackage wpSmooth = WorkPackage.create("IqmOpSmooth");
			ParameterBlockIQM params = wpSmooth.getParameters();
			params.setParameter("Method", 1); // Gaussian
			params.setParameter("KernelSize", kernelSize);
			wpSmooth.addSource(new IqmDataBox(pi));

			Result res_tmp = (Result) wpSmooth.process();
			pi = res_tmp.listImageResults().get(0).getImage();
		}

		// produce c x c images, histograms, and tables by tiling the image
		int dx_cell = Math.round(width / cells);
		int dy_cell = Math.round(height / cells);

		// concatenate the histograms row by row
		for (int c_y = 0; c_y < cells; c_y++) {
			// show the progress of the computation
			fireProgressChanged((int) ((c_y) * 100.0d / (cells)));
			for (int c_x = 0; c_x < cells; c_x++) {

				// define the dimensions of the cell including the border
				// extension
				Rectangle cell_ext = new Rectangle();
				cell_ext.setLocation((c_x * dx_cell), (c_y * dy_cell));
				cell_ext.setSize(dx_cell + (2 * ext), dy_cell + (2 * ext));

				// get the cell data including the border extension
				Raster r = pi.getData(cell_ext).createTranslatedChild(0, 0);

				// create a writable raster where to store the LBP values to
				WritableRaster wr = r.createCompatibleWritableRaster(dx_cell,
						dy_cell);

				// run the computation of the LBP code in the cell
				Object[] lbp = computeLBP(r, wr, neighbours, radius);

				// construct the image
				TiledImage cell_img = new TiledImage(0, 0, dx_cell, dy_cell, 0,
						0, pi.getColorModel().createCompatibleSampleModel(
								dx_cell, dy_cell), pi.getColorModel());
				cell_img.setData(wr);

				// compute the image result
				if (wp.isImageComputationEnabled()) {
					// initialize a new image model
					ImageModel im = new ImageModel(cell_img);

					// set the name of the image
					im.setModelName("LBP Image: Cell (" + c_x + "," + c_y + ")");

					// add the item to the result
					result.addItem(im);
				}

				if (wp.isPlotComputationEnabled()) {
					// Histogram h = computeHistogram(cell_img);
					// PlotModel pm = PlotTools.toPlotModel(
					// PlotTools.createHistogramDataset(h)).get(0);

					PlotModel pm = new PlotModel();
					int[] histogram = (int[]) lbp[2];
					Vector<Double> v = new Vector<Double>(histogram.length);
					Vector<Double> d = new Vector<Double>(histogram.length);
					for (int i = 0; i < histogram.length; i++) {
						v.add(i, histogram[i] * 1.0d);
						d.add(i, i * 1.0d);
					}
					pm.setData(v);
					pm.setDomain(d);

					pm.setModelName("LBP Histogram: Cell (" + c_x + "," + c_y
							+ ")");

					// add the single result
					result.addItem(pm);

					// concatenate the histogram to the feature vector
					featVec.addAll(c_x * c_y * histogram.length, v);
				}

				// assemble the table result
				if (wp.isTableComputationEnabled()) {
					Object[][] codeTable = (Object[][]) lbp[1];

					TableModel tm = new TableModel(codeTable, new Object[] {
							"x_coord", "y_coord", "lbp_code" });
					tm.setModelName("LBP Code Table: Cell (" + c_x + "," + c_y
							+ ")");
					result.addItem(tm);
				}

			}
		}

		// in the end, add the full feature vector
		PlotModel fv = new PlotModel();
		fv.createLinearDomain(0, featVec.size() - 1, 1);
		fv.setData(featVec);
		fv.setModelName("Concatenated LBP Feature Vector");
		result.addItem(fv);

		// return the result
		return result;
	}

	/**
	 * Computes the Local Binary Pattern feature descriptor as visual output
	 * (image).
	 * 
	 * @param r
	 *            the augmented raster (border extended)
	 * @param wr
	 *            the writable raster of the original image size
	 * @param neighbours
	 *            the number of neighbours
	 * @param radius
	 *            the rad
	 * @return the image at index 0, the code table consisting of 3 columns at
	 *         index 1, the histogram at index 2. The code table column 1 is the
	 *         x coordinate, column 2 is the y coordinate, column 3 is the
	 *         binary (010101...) code of the grey value.
	 */
	public Object[] computeLBP(Raster r, WritableRaster wr, int neighbours,
			float rad) {

		int[] thr = new int[neighbours];

		int width = r.getWidth();
		int height = r.getHeight();
		int[][] codes = new int[wr.getWidth()][wr.getHeight()];
		Object[][] codeTable = new Object[wr.getWidth() * wr.getHeight()][3];
		int[] histogram = new int[(int) Math.pow(2, neighbours)];

		int border = (int) Math.ceil(rad);

		int px = 0;
		int minLBP = 0;
		int maxLBP = 0;
		// run through all pixels and compare the neighbourhood
		for (int x = border; x < (width - border); x++) {
			for (int y = border; y < (height - border); y++) {

				// center the kernel on the pixel x,y
				// threshold the nxn patch using the center pixel
				int i_c = r.getSample(x, y, 0);

				// LBP code as integer
				int lbp_code = 0;

				// get the intensity values from the samples
				for (int p = 0; p < neighbours; p++) {
					// calculate coordinates
					double p_x = -rad * Math.sin(2 * Math.PI * p / neighbours);
					double p_y = rad * Math.cos(2 * Math.PI * p / neighbours);

					// get the intensity at the position p
					int i_p = 0;

					// if coordinates do not hit a pixel directly, interpolate
					if (p_x % 1 == 0 && p_y % 1 == 0) {
						// get the sample at the position
						i_p = r.getSample((int) (x + p_x), (int) (y + p_y), 0);
					} else {
						// interpolate using bilinear strategy
						int i_00 = r.getSample(x + (int) Math.floor(p_x), y
								+ (int) Math.floor(p_y), 0);
						int i_10 = r.getSample(x + (int) Math.ceil(p_x), y
								+ (int) Math.floor(p_y), 0);
						int i_01 = r.getSample(x + (int) Math.floor(p_x), y
								+ (int) Math.ceil(p_y), 0);
						int i_11 = r.getSample(x + (int) Math.ceil(p_x), y
								+ (int) Math.ceil(p_y), 0);
						double dx = p_x - Math.floor(p_x);
						double dy = p_y - Math.floor(p_y);
						double Q_0 = (1 - dx) * i_00 + dx * i_10;
						double Q_1 = (1 - dx) * i_01 + dx * i_11;
						i_p = (int) Math.round((1 - dy) * Q_0 + dy * Q_1);
					}

					// threshold the pixel intensities (grey-value invariance)
					thr[p] = sign(i_p, i_c);

					// compute the sum iteratively
					lbp_code += Math.round(thr[p] * Math.pow(2.0d, (double) p));
				}

				int lbp_orig = lbp_code;
				// make the measure rotational invariant by minimizing the
				// binary signature (minimal decimal value of the code)
				for (int i = 0; i < neighbours; i++) {
					int p1 = lbp_orig >>> i;
					int mask = ((int) Math.pow(2.0d, neighbours)) - 1;
					int p2 = lbp_orig & mask;
					p2 = p2 << (31 - i);
					int shifted = p1 | p2;

					// take the minimum
					if (shifted >= 0 && shifted < lbp_code) {
						lbp_code = shifted;
					}
				}

				// set the code to the code array
				codes[x - border][y - border] = lbp_code;

				// check for a new min and a new max code
				minLBP = (lbp_code < minLBP ? lbp_code : minLBP);
				maxLBP = (lbp_code > maxLBP ? lbp_code : maxLBP);

				// increase counter of corresponding bin
				histogram[lbp_code]++;

				// put the position and intensity as binary string in the code
				// table
				codeTable[px][0] = (x - border);
				codeTable[px][1] = (y - border);
				String bincode = String.format(
						"%" + String.valueOf(neighbours) + "s",
						Integer.toBinaryString(lbp_code)).replace(' ', '0');
				codeTable[px++][2] = bincode;
			}
		}

		// normalize the codes to an 8-bit image
		for (int i = 0; i < wr.getWidth(); i++) {
			for (int j = 0; j < wr.getHeight(); j++) {
				// scale the sample
				int code = codes[i][j];
				double scale = (1.0d * (code - minLBP) / (maxLBP - minLBP));
				int code_norm = (int) (scale * 255);
				wr.setSample(i, j, 0, code_norm);
			}
		}

		return new Object[] { wr, codeTable, histogram };
	}

	/**
	 * Compute the sign function of two variables.
	 * 
	 * @param i
	 * @param j
	 * @return 1, if <code>i >= j</code>, 0 otherwise
	 */
	private int sign(int i, int j) {
		return ((i - j >= 0) ? 1 : 0);
	}
}
