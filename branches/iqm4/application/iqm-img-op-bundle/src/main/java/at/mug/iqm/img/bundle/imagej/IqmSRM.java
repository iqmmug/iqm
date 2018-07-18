package at.mug.iqm.img.bundle.imagej;

/*
 * #%L
 * Project: IQM - Standard Image Operator Bundle
 * File: IqmSRM.java
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

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.util.Arrays;

/** 
 * This is an extended version of the ImageJ algorithm by Johannes Schindelin
 * http://fiji.sc/wiki/index.php/Statistical_Region_Merging#cite_note-0
 * based on following algorithm:
 * R. Nock, F. Nielsen (2004), "Statistical Region Merging", IEEE Trans. Pattern Anal. Mach. Intell. 26 (11): 1452-1458mages.
 * <p>
 * <b>Changes</b>
 * <ul>
 * 	<li>2012 01 07 expanded to RGB (changed a lot)
 * </ul>
 * 
 * @author Helmut Ahammer
 * @since   2012 01
 */
public class IqmSRM implements PlugInFilter {
	ImagePlus image;

	@Override
	public int setup(String arg, ImagePlus image) {
		this.image = image;
		return DOES_8G | NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		boolean isStack = image.getStackSize() > 1;

		GenericDialog gd = new GenericDialog("SRM");
		gd.addNumericField("Q", Q, 2);
		gd.addCheckbox("showAverages", true);
		if (isStack)
			gd.addCheckbox("3d", true);
		gd.showDialog();

		if (gd.wasCanceled())
			return;

		Q = (float)gd.getNextNumber();
		boolean showAverages = gd.getNextBoolean();
		boolean do3D = isStack ? gd.getNextBoolean() : false;
		if (do3D){
			//srm3D(showAverages).show();
		}
		else{
			srm2D(ip, showAverages).show();
		}
	}

	//added for Iqm 
	public ImagePlus run(ImageProcessor ip, int q, int method, int out){ //Iqm Mode
		this.Q = q;
		this.method = method;
		boolean showAverages = true;
		if (out == 0) showAverages = false;
		if (out == 1) showAverages = true;	
		return srm2D(ip, showAverages);
	}

	final float g = 256; // number of different intensity values
	protected float Q = 25; //25; // complexity of the assumed distributions
	protected int method = 0;
	protected float delta;


	/*
	 * The predicate: is the difference of the averages of the two
	 * regions R and R' smaller than
	 *
	 * g sqrt(1/2Q (1/|R| + 1/|R'|) ln 2/delta)
	 *
	 * Instead of calculating the square root all the time, we calculate
	 * the factor g^2 / 2Q ln 2/delta, and compare
	 *
	 * (<R> - <R'>)^2 < factor (1/|R| + 1/|R'|)
	 *
	 * instead.
	 */
	protected float factor, logDelta;
	protected int numBands;

	/*
	 * For performance reasons, these are held in w * h arrays
	 */
	float[][] average;  //[][numBands]
	int[]     count;    
	int[]     regionIndex; // if < 0, it is -1 - actual_regionIndex

	/*
	 * The statistical region merging wants to merge regions in a specific
	 * order: for all neighboring pixel pairs, in ascending order of
	 * intensity differences.
	 *
	 * In that order, it is tested if the regions these two pixels belong
	 * to (by construction, the regions must be distinct) should be merged.
	 *
	 * For efficiency, we do it by bucket sorting, because there are only
	 * g + 1 possible differences.
	 *
	 * After sorting, for each difference the pixel pair with the largest
	 * index is stored in neighborBuckets[difference], and every
	 * nextNeighbor[index] points to the pixel pair with the same
	 * difference and the next smaller index (or -1 if there is none).
	 *
	 * The pixel pairs are identified by
	 *
	 *     2 * (x + (w - 1) * y) + direction
	 *
	 * where direction = 0 means "right neighbor", and direction = 1 means
	 * " lower neighbor".  (We do not need "left" or "up", as the order
	 * within the pair is not important.)
	 *
	 * In n dimensions, it must be n * pixel_count, and "direction"
	 * specifies the Cartesian unit vector (axis) determining the neighbor.
	 */
	int[] nextNeighbor, neighborBucket; //[]


	protected ImagePlus srm2D(ImageProcessor ip, boolean showAverages) {
		int w = ip.getWidth(), h = ip.getHeight();
		numBands =  ip.getNChannels();
		//System.out.println("IqmSRM: numBands: " + numBands);

		delta = 1f / (6 * w * h);
		/*
		 * This would be the non-relaxed formula:
		 *
		 * factor = g * g / 2 / Q * (float)Math.log(2 / delta);
		 *
		 * The paper claims that this is more prone to oversegmenting.
		 */
		factor = g * g / 2 / Q;
		logDelta = 2f * (float)Math.log(6 * w * h);

		byte[][] pixel = new byte[w*h][numBands]; 


		for (int j=0; j<h; j++){
			for (int i=0; i<w; i++){
				int[] iArray = new int[numBands];		
				iArray = ip.getPixel(i, j, iArray);
				for (int b=0; b<numBands; b++){	
					pixel[j*w+i][b] = (byte)iArray[b];
				}
			}
		}
		initializeRegions2D(pixel, ip.getWidth(), ip.getHeight());
		initializeNeighbors2D(pixel, w, h);
		mergeAllNeighbors2D(w);

		if (showAverages) {
			int[] iArray = new int[numBands];
			for (int i = 0; i < (w*h); i++){		

				int y= (i/w); //= (int)Math.floor(i/w); 
				int x = (i-(y*w));
				for (int b=0; b<numBands; b++){	
					iArray[b] = (int)average[getRegionIndex(i)][b];									
				}
				ip.putPixel(x, y, iArray); //sets integers which must be converted to byte later
				//System.out.println("IqmSRM: i,x,y, iArray[0]:  "+i+"  "+x+"  "+y+ "    "+iArray[0]);	
				//System.out.println("IqmSRM: i,x,y:  "+i+"  "+x+"  "+y);	
			}		
		}
		else {
			int regionCount = consolidateRegions();

			if (regionCount > 1<<8) {
				if (regionCount > 1<<16)
					//					IJ.showMessage("Found " + regionCount
					//						+ " regions, which does not fit"
					//						+ " in 16-bit.");
					System.out.println("IqmSRM: Found " + regionCount + " regions, which does not fit in 16-bit.");
				short[] pixel16 = new short[w * h];
				for (int i = 0; i < pixel16.length; i++)
					pixel16[i] = (short)regionIndex[i];
				ip = new ShortProcessor(w, h, pixel16, null);
			}
			else {
				byte[] pixel8 = new byte[w * h];
				for (int i = 0; i < pixel.length; i++)
					pixel8[i] = (byte)regionIndex[i];
				ip = new ByteProcessor(w, h, pixel8, null);

			}
		}

		//String title = image.getTitle() + " (SRM Q=" + Q + ")";
		return new ImagePlus("SRM", ip);
	}

	void initializeRegions2D(byte[][] pixel, int w, int h) {
		average     = new float[w * h][numBands];
		count       = new int  [w * h];
		regionIndex = new int  [w * h];

		for (int i = 0; i < (w*h); i++) {
			for (int b=0; b<numBands; b++){	
				average[i][b] = (pixel[i][b]) & 0xff;	
			}	
			count[i] = 1;
			regionIndex[i] = i;
		}
	}

	protected void addNeighborPair(int neighborIndex, byte[][] pixel, int i1, int i2) {
		int difference = 0;
		for (int b=0; b<numBands; b++){	
			int diff = Math.abs((pixel[i1][b] & 0xff) - (pixel[i2][b] & 0xff));
			if (diff > difference) difference = diff;

		}
		nextNeighbor[neighborIndex] = neighborBucket[difference];
		neighborBucket[difference] = neighborIndex;
	}

	void initializeNeighbors2D(byte[][] pixel, int w, int h) {
		nextNeighbor = new int[2 * w * h];

		// bucket sort
		neighborBucket = new int[256];
		Arrays.fill(neighborBucket, -1);

		for (int j = h - 1; j >= 0; j--)
			for (int i = w - 1; i >= 0; i--) {
				int index = i + w * j;
				int neighborIndex = 2 * index;

				// vertical
				if (j < h - 1)
					addNeighborPair(neighborIndex + 1,
							pixel, index, index + w);

				// horizontal
				if (i < w - 1)
					addNeighborPair(neighborIndex,
							pixel, index, index + 1);
			}
	}


	// recursively find out the region index for this pixel
	int getRegionIndex(int i) {
		i = regionIndex[i];
		while (i < 0) i = regionIndex[-1 - i];
		return i;
	}

	// should regions i1 and i2 be merged?
	boolean predicate(int i1, int i2) {
		float[] difference = new float[numBands];
		for (int b=0; b<numBands; b++){	
			difference[b] = average[i1][b] - average[i2][b];
		}
		/*
		 * This would be the non-relaxed predicate mentioned in the
		 * paper.
		 *
		 * return difference * difference <
			factor * (1f / count[i1] + 1f / count[i2]);
		 *
		 */
		float log1 = (float)Math.log(1 + count[i1]) * (g < count[i1] ? g : count[i1]);
		float log2 = (float)Math.log(1 + count[i2]) * (g < count[i2] ? g : count[i2]);	
		float compare = .1f * factor * ((log1 + logDelta) / count[i1] + ((log2 + logDelta) / count[i2]));

		//merging if difference is smaller 
		boolean predicate = false;
		if (method <= 2){  //At least one band, At least 2 bands, all bands
			int nSmaller = method+1;  //number of bands that must be smaller 
			if (nSmaller > numBands) nSmaller = numBands; 

			int count = 0;
			for (int b=0; b<numBands; b++){	
				if (difference[b] * difference[b] < compare){
					count = count + 1;
				}
			}
			predicate = (count >= nSmaller);
		}
		if (method == 3){  //Red
			predicate = (difference[0] * difference[0] < compare);
		}
		if (method == 4){  //Green
			predicate = (difference[1] * difference[1] < compare);
		}
		if (method == 5){  //Blue
			predicate = (difference[2] * difference[2] < compare);
		}	
		return (predicate);
	}

	void mergeAllNeighbors2D(int w) {
		for (int i = 0; i < neighborBucket.length; i++) {
			//for (int b=0; b<numBands; b++){	
			int neighborIndex = neighborBucket[i];
			while (neighborIndex >= 0) {
				int i1 = neighborIndex / 2;
				int i2 = i1 + (0 == (neighborIndex & 1) ? 1 : w);

				i1 = getRegionIndex(i1);
				i2 = getRegionIndex(i2);

				if (predicate(i1, i2)) mergeRegions(i1, i2);

				neighborIndex = nextNeighbor[neighborIndex];
			}
			//}
		}
	}


	void mergeRegions(int i1, int i2) {
		if (i1 == i2)
			return;
		int mergedCount = count[i1] + count[i2];
		float[] mergedAverage = new float[numBands];
		for (int b=0; b<numBands; b++){	
			mergedAverage[b] = (average[i1][b] * count[i1]+ average[i2][b] * count[i2]) / mergedCount;
		}
		// merge larger index into smaller index
		if (i1 > i2) {
			for (int b=0; b<numBands; b++){	
				average[i2][b] = mergedAverage[b];
			}	
			count[i2] = mergedCount;
			regionIndex[i1] = -1 - i2;
		}
		else {
			for (int b=0; b<numBands; b++){	
				average[i1][b] = mergedAverage[b];
			}
			count[i1] = mergedCount;
			regionIndex[i2] = -1 - i1;
		}
	}

	int consolidateRegions() {
		/*
		 * By construction, a negative regionIndex will always point
		 * to a smaller regionIndex.
		 *
		 * So we can get away by iterating from small to large and
		 * replacing the positive ones with running numbers, and the
		 * negative ones by the ones they are pointing to (that are
		 * now guaranteed to contain a non-negative index).
		 */
		int count = 0;
		for (int i = 0; i < regionIndex.length; i++)
			if (regionIndex[i] < 0)
				regionIndex[i] =
				regionIndex[-1 - regionIndex[i]];
			else
				regionIndex[i] = count++;
		return count;
	}
}
