package at.mug.iqm.img.bundle.op;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmOpRegGrow.java
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


import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;

import at.mug.iqm.api.Application;
import at.mug.iqm.api.gui.BoardPanel;
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
import at.mug.iqm.img.bundle.descriptors.IqmOpRegGrowDescriptor;


/**
 * @author Peinsold, Dorn, Ahammer, Kainz
 * @since 2011 11
 * 
 */
public class IqmOpRegGrow extends AbstractOperator {

	private boolean[][][] regionArray; // [imgWidth][imgHeight][NumberOfSeedPoints
										// = NumberOfRegions]
	private WritableRaster wr;
	private int range;
	private double minGrayValue;
	private double maxGrayValue;
	private double typeGrayMax;
	// private double seedGrayValue;
	private Stack<Point> pointStack;

	public IqmOpRegGrow() {
		// WARNING: Don't declare fields here
		// Fields declared here aren't thread safe!
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IResult run(IWorkPackage wp) {

		PlanarImage piOut = null;

		ParameterBlockIQM pb = null;
		if (wp.getParameters() instanceof ParameterBlockImg) {
			pb = (ParameterBlockImg) wp.getParameters();
		} else {
			pb = wp.getParameters();
		}

		PlanarImage pi = ((IqmDataBox) pb.getSource(0)).getImage(); // input image
		
		this.range = pb.getIntParameter("GreyRange"); // input grey value
															// range
		int modus = pb.getIntParameter("Modus"); // 0 ROI Mean 1 ROI center

		int numBands = pi.getData().getNumBands();
		if (numBands > 1) {
			BoardPanel
					.appendTextln("IqmOpRegGrow: Region Growing only for single banded images possible!");
			return new Result(new IqmDataBox(pi));
		}
		String imgName = String.valueOf(pi.getProperty("image_name"));
		String fileName = String.valueOf(pi.getProperty("file_name"));
		
		typeGrayMax = ImageTools.getImgTypeGreyMax(pi);

		// get a writable raster for ti
		Raster r = pi.getData();
		this.wr = r.createCompatibleWritableRaster(pi.getMinX(), pi.getMinY(),
				pi.getWidth(), pi.getHeight());
		wr.setRect(r);

		// ArrayList of seed points
		ArrayList<Point> seedPoints = new ArrayList<Point>();
		// Example Seed-Points
		// seedPoints.add(new Point(100, 100));
		// seedPoints.add(new Point(60, 80));
		// seedPoints.add(new Point(130, 40));

		// ArayList of seed gray values
		ArrayList<Double> seedGrayValues = new ArrayList<Double>();

		if (modus == 0 || modus == 1) { // ROI center grey values ROI mean gray
										// values
			// Get Seed Points from ROI's in the Look window
			ROIShape rs = Application.getLook().getCurrentLookPanel().getCurrentROILayer().getCurrentROIShape();
			if (rs == null) {
				BoardPanel
						.appendTextln("IqmOpRegGrow: Currrent ROI is not defined and null!");
				BoardPanel
						.appendTextln("IqmOpRegGrow: ROI processing not possible");
				return new Result(new IqmDataBox(pi));
			} else {
				BoardPanel
						.appendTextln("IqmOpRegGrow: Current ROI is defined and valid");
			}
			ParameterBlockJAI pbHist = new ParameterBlockJAI("histogram");
			pbHist.addSource(pi);
			List<ROIShape> shapeVector = Application.getLook().getCurrentLookPanel().getCurrentROILayer()
					.getAllROIShapes();
			Iterator itr = shapeVector.iterator();
			while (itr.hasNext()) {
				ROIShape roiShape = (ROIShape) itr.next();
				if (roiShape != null) {
					// Approximate center of gravity
					Rectangle2D bounds = roiShape.getBounds2D();
					Point seedPoint = new Point((int) bounds.getCenterX(),
							(int) bounds.getCenterY());
					seedPoints.add(seedPoint);
					if (modus == 0) { // ROI center
						seedGrayValues.add((double) wr.getSample(seedPoint.x,
								seedPoint.y, 0));
					}
					if (modus == 1) { // ROI mean
						// get seed grey means
						pbHist.setParameter("roi", roiShape);
						RenderedOp rOp = JAI.create("Histogram", pbHist);
						Histogram histo = (Histogram) rOp
								.getProperty("histogram");
						seedGrayValues.add(histo.getMean()[0]);
					}

				}
			}
		} // ROI Mean or ROI Center

		// Array to mark pixels within the range
		this.regionArray = new boolean[pi.getWidth()][pi.getHeight()][seedPoints
				.size()];
		for (int i = 0; i < pi.getWidth(); i++)
			for (int j = 0; j < pi.getHeight(); j++)
				for (int k = 0; k < seedPoints.size(); k++)
					this.regionArray[i][j][k] = false;

		// Iterate through the seedPoints
		// int count = 0;
		// for(Point seedPoint : seedPoints) {
		for (int i = 0; i < seedPoints.size(); i++) {
			Point seedPoint = seedPoints.get(i);
			// Double seedMean = seedMeans.get(i);
			// int seedGrayValue = wr.getSample(seedPoint.x, seedPoint.y, 0);
			double seedGrayValue = seedGrayValues.get(i);
			System.out.println("IqmOpRegGrow: seed grey value: "
					+ seedGrayValue);
			// calculate minimal value for suitable gray values
			this.minGrayValue = seedGrayValue - this.range;
			if (this.minGrayValue < 0)
				this.minGrayValue = 0;

			// calculate maximal value for suitable gray values
			this.maxGrayValue = seedGrayValue + this.range;
			if (this.maxGrayValue > (int) typeGrayMax)
				this.maxGrayValue = (int) typeGrayMax;

			// Board.appendTexln("IqmOpRegGrow: minGrayValue: "+ minGrayValue);
			// Board.appendTexln("IqmOpRegGrow: maxGrayValue: "+ maxGrayValue);

			// Start iterative algorithm
			// this.calcRegGrow(seedPoint, count);
			this.calcRegGrow(seedPoint, i);
			// count++;
		}

		// Set pixel to black which are not part of any region
		this.markRegions(seedPoints.size());

		// set output object
		// Get a writable image with same size and type as input image
		TiledImage ti = new TiledImage(pi.getMinX(), pi.getMinY(),
				pi.getWidth(), pi.getHeight(), pi.getTileGridXOffset(),
				pi.getTileGridYOffset(), pi.getSampleModel(),
				pi.getColorModel());
		ti.setData(wr);
		piOut = ti;

		// some pixels inside ROI aren't in between the range and are set to
		// black
		// reset pixels inside ROI to original grey values
		if (modus == 0 || modus == 1) { // fill ROI
			ROIShape rs = Application.getLook().getCurrentLookPanel().getCurrentROILayer().getCurrentROIShape();
			if (rs == null) {
				BoardPanel
						.appendTextln("IqmOpRegGrow: Currrent ROI is not defined and null!");
				BoardPanel
						.appendTextln("IqmOpRegGrow: ROI processing not possible");
				return new Result(new IqmDataBox(pi));
			} else {
				BoardPanel
						.appendTextln("IqmOpRegGrow: Current ROI is defined and valid");
			}
			Area area = new Area(rs.getAsShape());
			List<ROIShape> shapeVector = Application.getLook().getCurrentLookPanel().getCurrentROILayer()
					.getAllROIShapes();
			Iterator itr = shapeVector.iterator();
			while (itr.hasNext()) {
				ROIShape roiShape = (ROIShape) itr.next();
				if (roiShape != null) {
					area.add(new Area(roiShape.getAsShape()));
				}
			}
			rs = new ROIShape(area);
			TiledImage tiROI = new TiledImage(pi.getMinX(), pi.getMinY(),
					pi.getWidth(), pi.getHeight(), pi.getTileGridXOffset(),
					pi.getTileGridYOffset(), pi.getSampleModel(),
					pi.getColorModel());
			try {
				tiROI.setData(pi.copyData(), rs);
			} catch (NullPointerException e) {
				BoardPanel
						.appendTextln("IqmOpRegGrow: Segmentation with ROI is not possible, perhaps the ROI is a line");
				// e.printStackTrace();
			}
			ParameterBlock pbOR = new ParameterBlock();
			pbOR.addSource(ti);
			pbOR.addSource(tiROI);

			piOut = JAI.create("OR", pbOR, null); // add images
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
			this.name = new IqmOpRegGrowDescriptor().getName();
		}
		return this.name;
	}
	
	@Override
	public OperatorType getType() {
		return IqmOpRegGrowDescriptor.TYPE;
	}
	
	/**
	 * Calculates the RegGrow-Algorithm for one seedPoint
	 * 
	 * @param seedPoint
	 */
	private void calcRegGrow(Point seedPoint, int seedcount) {
		this.pointStack = new Stack<Point>();
		this.pointStack.push(seedPoint);

		while (!this.pointStack.empty()) {
			Point currPoint = this.pointStack.pop();

			if (this.regionArray[currPoint.x][currPoint.y][seedcount] == false) {
				int greyValue = this.wr.getSample(currPoint.x, currPoint.y, 0);

				if (this.isInRange(greyValue)) {
					this.setPixelTrue(currPoint, seedcount);

					// check Pixel above
					Point pointAbove = new Point(currPoint.x, currPoint.y - 1);
					this.checkPoint(pointAbove, seedcount);

					// check Pixel above left
					Point pointAboveLeft = new Point(currPoint.x - 1,
							currPoint.y - 1);
					this.checkPoint(pointAboveLeft, seedcount);

					// check Pixel above right
					Point pointAboveRight = new Point(currPoint.x + 1,
							currPoint.y - 1);
					this.checkPoint(pointAboveRight, seedcount);

					// check Pixel on the right side
					Point pointRight = new Point(currPoint.x + 1, currPoint.y);
					this.checkPoint(pointRight, seedcount);

					// check Pixel below
					Point pointBelow = new Point(currPoint.x, currPoint.y + 1);
					this.checkPoint(pointBelow, seedcount);

					// check Pixel below left
					Point pointBelowLeft = new Point(currPoint.x - 1,
							currPoint.y + 1);
					this.checkPoint(pointBelowLeft, seedcount);

					// check Pixel below right
					Point pointBelowRight = new Point(currPoint.x + 1,
							currPoint.y + 1);
					this.checkPoint(pointBelowRight, seedcount);

					// check Pixel on the left side
					Point pointLeft = new Point(currPoint.x - 1, currPoint.y);
					this.checkPoint(pointLeft, seedcount);
				}
			}
		}
	}

	/**
	 * Checks whether the point should be pushed on the stack or not
	 * 
	 * @param p
	 */
	private void checkPoint(Point p, int seedcount) {
		if (this.isPointWithinBounds(p))
			if (this.regionArray[p.x][p.y][seedcount] == false)
				this.pointStack.push(p);
	}

	/**
	 * Set the corresponding point in the controllArray to 1
	 * 
	 * @param p
	 */
	private void setPixelTrue(Point p, int seedcount) {
		this.regionArray[p.x][p.y][seedcount] = true;
	}

	/**
	 * Checks if the grey value is bigger than min grey value and smaller than
	 * max grey value
	 * 
	 * @param greyValue
	 * @return true if the grey value is in range
	 */
	private boolean isInRange(double greyValue) {
		if ((greyValue >= this.minGrayValue)
				&& (greyValue <= this.maxGrayValue))
			return true;

		return false;
	}

	/**
	 * Check if the point is within bounds or not
	 * 
	 * @param p
	 * @return true if the point is within bounds
	 */
	private boolean isPointWithinBounds(Point p) {
		if ((p.x < this.regionArray.length) && (p.x >= 0))
			if ((p.y >= 0) && (p.y < this.regionArray[0].length))
				return true;

		return false;
	}

	/**
	 * Set all pixel black which are not part of any region
	 */
	private void markRegions(int seedcount) {
		boolean markBlack = true;

		for (int i = 0; i < this.regionArray.length; i++) // imgWidth
		{
			for (int j = 0; j < this.regionArray[0].length; j++) // imgHeight
			{
				for (int k = 0; k < seedcount; k++) // number of seed points =
													// number of regions
				// it would be possible to set an individual region to an
				// individual band of the raster
				// this would yield a multibanded image
				{
					if (this.regionArray[i][j][k] == true)
						markBlack = false;

				}

				if (markBlack) // set grey value to zero, because it is not in
								// any region
				{
					this.wr.setSample(i, j, 0, 0);
				}

				markBlack = true;

			}
		}
	}

}
