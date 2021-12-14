package at.mug.iqm.commons.util.image;

/*
 * #%L
 * Project: IQM - API
 * File: ImageInfoExtractor.java
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


import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;

import javax.media.jai.PlanarImage;

 
 

/**
 * @author Helmut Ahammer
 * @since 2009
 */
public class ImageInfoExtractor {

	// class specific logger
	  

	private StringBuffer imageInfo = new StringBuffer();
	private PlanarImage 	    pi = null;

	/**
	 * This is the constructor.
	 */
	public ImageInfoExtractor() {}


	public String getImageInfo(PlanarImage pi){
		this.pi = pi;
		if (this.pi == null){
			System.out.println("IQM Info: No image and therefore no information!");
			return null;
		}
		this.constructInfo(this.pi);
		return new String(this.imageInfo);
	}

	private void constructInfo(PlanarImage pi) {
		// Get the image file size (non-JAI related).
		//File image = new File(args[0]);
		//BoardJ.appendTexln("Image file size: "+image.length()+" bytes.");	
		// Show the image dimensions and coordinates.
		this.imageInfo.append("Image Name:        " + String.valueOf(pi.getProperty("image_name")) + "\n");
		this.imageInfo.append("File Name:         " + String.valueOf(pi.getProperty("file_name")) + "\n");
		this.imageInfo.append("Dimensions:        ");
		this.imageInfo.append(pi.getWidth()+"x"+pi.getHeight()+" pixels");
		// Remember getMaxX and getMaxY return the coordinate of the next point!
		this.imageInfo.append(" (minimum "+pi.getMinX()+","+pi.getMinY()+")" + "\n"); //+" to ") + (pi.getMaxX()-1)+","+(pi.getMaxY()-1)+")");

		this.imageInfo.append("Number of Sources: " + pi.getNumSources() + "\n");
		this.imageInfo.append("Tile Factory:      " + pi.getTileFactory() + "\n");
		//	BoardJ.appendTexln("JAI.getDefaultTileSize() "+ JAI.getDefaultTileSize());
		//	BoardJ.appendTexln("JAI parallelism:         "+ JAI.getDefaultInstance().getTileScheduler().getParallelism());
		//	BoardJ.appendTexln("JAI PrefetchParallelism: "+ JAI.getDefaultInstance().getTileScheduler().getPrefetchParallelism());
		//	BoardJ.appendTexln("JAI PrefetchPriority:    "+ JAI.getDefaultInstance().getTileScheduler().getPrefetchPriority());
		//	BoardJ.appendTexln("JAI Priority:            "+ JAI.getDefaultInstance().getTileScheduler().getPriority());

		if ((pi.getNumXTiles() != 1)||(pi.getNumYTiles() != 1)){ // Is it tiled?
			// Tiles number, dimensions and coordinates.
			this.imageInfo.append("Tiles:             ");
			this.imageInfo.append(pi.getTileWidth()+"x"+pi.getTileHeight()+" pixels"+" ("+pi.getNumXTiles()+"x"+pi.getNumYTiles()+" tiles)");
			this.imageInfo.append(" (minimum "+pi.getMinTileX()+","+pi.getMinTileY()+")");//+" to "+pi.getMaxTileX()+","+pi.getMaxTileY()+")");
			this.imageInfo.append(" offset: "+pi.getTileGridXOffset()+","+pi.getTileGridXOffset() + "\n");
		}
		// Display info about the SampleModel of the image.
		SampleModel sm = pi.getSampleModel();
		int dt = pi.getAsBufferedImage().getType();
		String dataType;
		switch(dt) {
		case BufferedImage.TYPE_3BYTE_BGR:      dataType= "TYPE_3BYTE_BGR"; break;
		case BufferedImage.TYPE_4BYTE_ABGR:     dataType= "TYPE_4BYTE_ABGR"; break;
		case BufferedImage.TYPE_4BYTE_ABGR_PRE: dataType= "TYPE_4BYTE_ABGR_PRE"; break;
		case BufferedImage.TYPE_BYTE_BINARY:    dataType= "TYPE_BYTE_BINARY"; break;
		case BufferedImage.TYPE_BYTE_GRAY:      dataType= "TYPE_BYTE_GRAY"; break;
		case BufferedImage.TYPE_BYTE_INDEXED:   dataType= "TYPE_BYTE_INDEXED"; break;
		case BufferedImage.TYPE_CUSTOM:         dataType= "TYPE_CUSTOM"; break;
		case BufferedImage.TYPE_INT_ARGB:       dataType= "TYPE_INT_ARGB"; break;
		case BufferedImage.TYPE_INT_ARGB_PRE:   dataType= "TYPE_INT_ARGB_PRE"; break;
		case BufferedImage.TYPE_INT_BGR:        dataType= "TYPE_INT_BGR"; break;
		case BufferedImage.TYPE_INT_RGB:        dataType= "TYPE_INT_RGB"; break;
		case BufferedImage.TYPE_USHORT_555_RGB: dataType= "TYPE_USHORT_555_RGB"; break;
		case BufferedImage.TYPE_USHORT_565_RGB: dataType= "TYPE_USHORT_565_RGB"; break;
		case BufferedImage.TYPE_USHORT_GRAY:    dataType= "TYPE_USHORT_GRAY"; break;
		default: dataType = "Unknown"; break;
		}
		this.imageInfo.append("Sample Model:      "+sm + "\n");
		this.imageInfo.append("Data Type:         "+dataType + "\n");	
		this.imageInfo.append("Bands:             "+sm.getNumBands() + "\n");
		this.imageInfo.append("Data type:         ");
		switch(sm.getDataType()){
		case DataBuffer.TYPE_BYTE:      this.imageInfo.append("Byte" + "\n");      break;
		case DataBuffer.TYPE_SHORT:     this.imageInfo.append("Short" + "\n");     break;
		case DataBuffer.TYPE_USHORT:    this.imageInfo.append("Ushort" + "\n");    break;
		case DataBuffer.TYPE_INT:       this.imageInfo.append("Int" + "\n");       break;
		case DataBuffer.TYPE_FLOAT:     this.imageInfo.append("Float" + "\n");     break;
		case DataBuffer.TYPE_DOUBLE:    this.imageInfo.append("Double" + "\n");    break;
		case DataBuffer.TYPE_UNDEFINED: this.imageInfo.append("Undefined" + "\n"); break;
		default: 						this.imageInfo.append("Unknown" + "\n");   break;
		}
		// Display info about the ColorModel of the image.
		ColorModel cm = pi.getColorModel();
		if (cm != null){
			this.imageInfo.append("Color Model:       "+ cm + "\n");
			this.imageInfo.append("Color components:  "+ cm.getNumComponents() + "\n");
			this.imageInfo.append("Bits per pixel:    "+ cm.getPixelSize() + "\n");
			this.imageInfo.append("Transparency:      ");
			switch(cm.getTransparency()){
			case Transparency.OPAQUE:      this.imageInfo.append("opaque" + "\n");      break;
			case Transparency.BITMASK:     this.imageInfo.append("bitmask" + "\n");     break;
			case Transparency.TRANSLUCENT: this.imageInfo.append("translucent" + "\n"); break;
			}
		}
		else {
			this.imageInfo.append("No color model." + "\n");
		}

		//optional additional info (uses ImageInfoUtil.java from book AWT 2D JAI Rodrigues)
		//	String[][] info = ImageInfoUtil.getImageInfo(pi.getAsBufferedImage());
		//	System.out.println("");
		//	System.out.println("ImageInfo: Additional Info");
		//	for (int i= 0; i< 10; i++){
		//		System.out.println(""+info[i][0]+":             "+info[i][1]);
		//	}


	}
}//END
