/*
 * Created on Jun 5, 2005
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
 * File: TemplateMatchingWithRasters.java
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
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;


/**
 * This class demonstrates an implementation of a basic Template Matching
 * algorithm, using Raster(s) to access the data. This class inherits from
 * ImageProcessingTask.
 * IMPORTANT: This implementation does not lock access to the data while the
 * template matching is being done.
 * 
 * @author Helmut Ahammer
 * @since 2009 Original from Rafael Santos (rafael.santos@lac.inpe.br)
 */
public class TemplateMatchingWithRasters extends ImageProcessingTask
{
	// The input image, input template and output images.
	private PlanarImage input;  
	private PlanarImage template;
	private TiledImage output;
	// Dimensions on the image and mask.
	private int imageWidth,imageHeight,templateWidth,templateHeight;
	private int hotSpotX,hotSpotY;
	// Flags and counters.
	private boolean finished = false;
	private int position;

	/**
	 * The constructor for this class, which gets the input images and set some
	 * parameters.
	 * @param input the input image.
	 * @param template the template matching mask.
	 */
	public TemplateMatchingWithRasters(PlanarImage input, PlanarImage template)
	{
	
		this.input = input;
		this.template = template;
		// Get some info about the input and template images.
		imageWidth = input.getWidth();
		imageHeight = input.getHeight();
		templateWidth = template.getWidth();
		templateHeight = template.getHeight();
		hotSpotX = templateWidth/2;
		hotSpotY = templateHeight/2;
		// Create an output image with the same features as the input one.
		output = new TiledImage(0,0,imageWidth,imageHeight,0,0,
				input.getSampleModel(),input.getColorModel());
		finished = false;
		position = 0;
	}

	/**
	 * This method returns a measure of length for the algorithm. We will consider the
	 * number of processed lines.
	 */
	@Override
	public long getSize()
	{
		return imageHeight-2*hotSpotY;
	}

	/**
	 * This method returns a measure of the progress of the algorithm.
	 */
	@Override
	public long getPosition()
	{
		return position;
	}

	/**
	 * This method is the main driver of the algorithm, it will perform the template
	 * matching.
	 */  
	@Override
	public void run()
	{
		position = 0;
		// Get the raster from the input image. 
		Raster inputRaster = input.getData();
		// Get the raster from the template image.
		Raster templateRaster = template.getData();
		// Create arrays for the template and image region (same size as template).
		int[] pixelT = new int[templateWidth*templateHeight];
		int[] pixelI = new int[templateWidth*templateHeight];
		// Get the template pixels. 
		templateRaster.getPixels(0,0,templateWidth,templateHeight,pixelT);
		// Create a WritableRaster.
		WritableRaster raster =
				RasterFactory.createBandedRaster(DataBuffer.TYPE_BYTE,
						imageWidth,imageHeight,1,new Point(0,0));
		// Define some useful values.
		int maxValue = templateWidth*templateHeight;
		// Scan all pixels corresponding to the output image.
		for(int yi=hotSpotY;yi<imageHeight-hotSpotY;yi++)
		{
			int proz = (yi+1)*100 / (imageHeight-hotSpotY);
			
			this.fireProgressChanged(proz);
			
			if (isCancelled(getParentTask())){
				this.interrupt();
				return;
			}

			for(int xi=hotSpotX;xi<imageWidth-hotSpotX;xi++)
			{
				int result = 0;
				// Get the region of the image under the moving window.
				inputRaster.getPixels(xi-hotSpotX,yi-hotSpotY,templateWidth,templateHeight,pixelI);
				// Get the sum of the absolute difference between the pixels on the image
				// and on the template. We can use a better-performing loop here.
				for(int t=0;t<templateWidth*templateHeight;t++)
					result += Math.abs(pixelT[t]-pixelI[t]);
				result = result/maxValue;
				raster.setSample(xi,yi,0,result);
			}
			// Set the output raster on the output image. Do it every line, so we can
			// get some partial results.
			output.setData(raster);
			position++;
		}
		finished = true;
	}

	/**
	 * This method tells whether the algorithm has finished.
	 */
	@Override
	public boolean isFinished()
	{
		return finished;
	}

	/**
	 * This method returns the output image.
	 * @return the template matching resulting image.
	 */
	public TiledImage getOutput()
	{
		return output;
	}

}
