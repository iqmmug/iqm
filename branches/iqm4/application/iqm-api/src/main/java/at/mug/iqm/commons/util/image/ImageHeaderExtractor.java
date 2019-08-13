package at.mug.iqm.commons.util.image;

/*
 * #%L
 * Project: IQM - API
 * File: ImageHeaderExtractor.java
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


import java.io.File;
import java.io.IOException;

import javax.media.jai.PlanarImage;

import org.apache.log4j.Logger;
//import org.w3c.dom.NamedNodeMap; //split package org.w3c.dom java.xml and loci_tools.jar
//import org.w3c.dom.Node;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import at.mug.iqm.api.gui.BoardPanel;
import at.mug.iqm.commons.util.DialogUtil;

/**
 * This class extracts the headers of images.
 * <p>
 * <b>Changes</b>
 * <ul>
 * 	<li>2010 05 registering tiff or jpeg2000 readers if readers are not
 *         available through native imagio
 * </ul>
 * 
 * @author Helmut Ahammer, Philipp Kainz
 * @since 2009
 * @update 2018-09 new metadata reading using:
 *		   https://mvnrepository.com/artifact/com.drewnoakes/metadata-extractor
 *		   which is already loaded with:
 *		   https://mvnrepository.com/artifact/org.apache.tika/tika-parsers
 */
public class ImageHeaderExtractor {

	// class specific logger
	private static final Logger logger = Logger
			.getLogger(ImageHeaderExtractor.class);

	private StringBuffer imageHeader = new StringBuffer();
	private PlanarImage pi = null;

	/**
	 * This is the constructor.
	 */
	public ImageHeaderExtractor() {
	}

	/**
	 * This returns the header of the current image.
	 * 
	 * @return a String representation of the header to be displayed
	 */
	public String getImageHeader(PlanarImage pi) {
		this.pi = pi;
		if (this.pi == null) {
			logger.info("No image and therefore no information.");
			return null;
		}
		this.extractHeader(this.pi);
		return new String(this.imageHeader);
	}

	/**
	 * This returns the header of the current image.
	 * 
	 * @param file
	 *            - the file to be analyzed
	 */
	public String getHeaderOfImage(File file) {
		this.readMetadata(file);
		return new String(this.imageHeader);
	}

	/**
	 * Extract an image's header (2)
	 * 
	 * @param pi
	 */
	private void extractHeader(PlanarImage pi) {
		try {
			if (pi == null) {
				DialogUtil
						.getInstance()
						.showDefaultInfoMessage(
								"this.imageHeader: No image and therefore no information!");
			} else {
				if (pi.getProperty("image_name").equals(""))
					return;
				String fileName = String.valueOf(pi.getProperty("file_name"));
				File file = new File(fileName);
				this.readMetadata(file);
			}
		} catch (Exception e) {
			DialogUtil.getInstance().showDefaultInfoMessage(
					"There is no header information available.");
		}
	}

	/**
	 * meta data is generated from the header. (3)
	 * 
	 * @param file
	 */
	private void readMetadata(File file) {
			//2018-09 new metadata reading using:
		    //https://mvnrepository.com/artifact/com.drewnoakes/metadata-extractor which is already loaded with:
		    //https://mvnrepository.com/artifact/org.apache.tika/tika-parsers
		    Metadata metadata = null;
			try {
				metadata = ImageMetadataReader.readMetadata(file);
			} catch (ImageProcessingException e) {
				e.printStackTrace();
				DialogUtil.getInstance().showDefaultInfoMessage("ImageProcessingException, image header not readable!");
				BoardPanel.appendTextln("ImageProcessingException, image header not readable!");
	
			} catch (IOException e) {
				e.printStackTrace();
				DialogUtil.getInstance().showDefaultInfoMessage("Image header not readable!");
				BoardPanel.appendTextln("Image header not readable!");
			}
		    System.out.println("Meta data of: " + file);
		    this.imageHeader.append("Meta data of: " + file + "\n");
		    for (Directory directory : metadata.getDirectories()) {
		      System.out.println("  directory: " + directory);
		      this.imageHeader.append("  directory: " + directory+ "\n");
		      for (Tag tag : directory.getTags()) {
		        System.out.println("     tag: " + tag);
		        this.imageHeader.append("     tag:" + tag + "\n");
		      }
		    }
		}
		    
		
		//this old version uses org.w3c.dom.NamedNodeMap and org.w3c.dom.Node;
		//org.w3c.dom is a split package because both java.xml and loci_tools.jar use them.
		//not compatible with Java-9+ modules - split packages are not allowed
	    //changed
//		try {
//			ImageInputStream iis = ImageIO.createImageInputStream(file);
//			if (iis == null) {
//				DialogUtil.getInstance().showDefaultErrorMessage(
//						"No image readers available!");
//				return;
//			}
//			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
//			if (!readers.hasNext()) { // no reader, perhaps simple imageio
//										// without tiff reader installed
//				IIORegistry registry = IIORegistry.getDefaultInstance();
//				// registry.registerServiceProvider(new
//				// com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi());
//				if (file.toString().toLowerCase()
//						.endsWith(IQMConstants.TIF_EXTENSION)
//						|| file.toString().toLowerCase()
//								.endsWith(IQMConstants.TIFF_EXTENSION)) {
//					registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi());
//					logger.info("Additional tiff reader registered.");
//
//				}
//				if (file.toString().toLowerCase().endsWith("jp2")
//						|| file.toString().toLowerCase().endsWith("jpc")
//						|| file.toString().toLowerCase().endsWith("j2c")
//						|| file.toString().toLowerCase().endsWith("j2k")
//						|| file.toString().toLowerCase().endsWith("jpx")) {
//					registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReaderSpi());
//					logger.info("Additional jpeg2000 reader registered.");
//				}
//				readers = ImageIO.getImageReaders(iis);
//			}
//
//			if (readers.hasNext()) {// reader(s) available
//
//				// pick the first available ImageReader
//				ImageReader reader = readers.next();
//
//				// attach source to the reader
//				reader.setInput(iis, false); // ImageInputStream, boolean
//												// seekForwardOnly
//
//				// read metadata of first image
//				// reader.getNumThumbnails(arg0);
//
//				int numImages = reader.getNumImages(true); // boolean
//															// allowSearch
//															// (allowSearch ==
//															// true und
//															// seekForwardOnly
//															// == true geht
//															// nicht)
//				logger.info("this.imageHeader: Number of Images: " + numImages);
//
//				// BoardJ.appendTexln("this.imageHeader: File contains more than one image");
//				int firstWidth = reader.getWidth(0);
//				int firstHeight = reader.getHeight(0);
//				for (int n = 0; n < numImages; n++) {
//					int width = reader.getWidth(n);
//					int height = reader.getHeight(n);
//					float ratioWidth = (float) firstWidth / width;
//					float ratioHeight = (float) firstHeight / height;
//					String strNumber = String.format("%3d", n + 1);
//					String strWidth = String.format("%6d", width);
//					String strHeight = String.format("%6d", height);
//					String strRatioWidth = String.format("%5.1f", ratioWidth);
//					String strRatioHeight = String.format("%5.1f", ratioHeight);
//					this.imageHeader.append("Image Number:" + strNumber
//							+ "     Width:" + strWidth + "   Height:"
//							+ strHeight + "     Width Ratio:" + strRatioWidth
//							+ ":1" + "  Height Ratio:" + strRatioHeight
//							+ ":1\n");
//				}
//				int num = 0;
//				if (numImages == 1) { // standard
//					num = 0;
//				}
//				if (numImages == -1) { // allowSearch is false and a search
//										// would be required.
//					num = 0;
//				}
//				if (numImages > 1) {
//					ImageHeaderNumImgSelection select = new ImageHeaderNumImgSelection(
//							numImages);
//					num = select.getSelectedValue() - 1; // selected image
//															// number
//					if (num < 0) { // Abbruch
//						return;
//					}
//				}
//
//				IIOMetadata metadata = reader.getImageMetadata(num);
//
//				// String[] names = metadata.getMetadataFormatNames(); //da
//				// kommen gleich mehrere!? z.B. bei jpeg Bild auch tif
//				// Metadaten!?
//				// for (int ii = 0; ii < names.length; ii++) {
//				// BoardJ.appendTexln( "Format name: " + names[ ii ] );
//				// this.imageHeader.displayMetadata(metadata.getAsTree(names[ii]));
//				// }
//
//				String fn = metadata.getNativeMetadataFormatName();
//				this.imageHeader.append("Format name: " + fn + "\n");
//
//				this.constructMetadata(metadata.getAsTree(fn));
//
//				reader.dispose();
//			} else {// no readers found
//				DialogUtil
//						.getInstance()
//						.showDefaultInfoMessage(
//								"No readers found!\nThe available format readers of ImageIO have been written to the log window!");
//				BoardPanel
//						.appendTextln("No image reader found for this image format");
//				BoardPanel
//						.appendTextln("Perhaps jai-imageio is not installed or the image is defect");
//				BoardPanel
//						.appendTextln("Following image readers are available:");
//				String[] rfn = ImageIO.getReaderFormatNames();
//				for (int n = 0; n < rfn.length; n++) {
//					BoardPanel.appendTextln("Available readers: " + rfn[n]);
//				}
//
//			}
//		} catch (Exception e) {
//			logger.error("An error occurred: ", e);
//		}
//	}
//
//	/**
//	 * Construct the meta data (caller for the recursive method).
//	 * 
//	 * @param root
//	 */
//	private void constructMetadata(Node root) {
//		this.constructMetadata(root, 0);
//	}
//
//	/**
//	 * Recursively constructs the meta data for a specific node and level.
//	 * 
//	 * @param node
//	 * @param level
//	 */
//	private void constructMetadata(Node node, int level) {
//		// print open tag of element
//		indent(level);
//		this.imageHeader.append("<" + node.getNodeName());
//		NamedNodeMap map = node.getAttributes();
//		if (map != null) {
//
//			// print attribute values
//			for (int i = 0; i < map.getLength(); i++) {
//				Node attr = map.item(i);
//				this.imageHeader.append(" " + attr.getNodeName() + "=\""
//						+ attr.getNodeValue() + "\"");
//			}
//		}
//
//		Node child = node.getFirstChild();
//		if (child == null) {
//			// no children, so close element and return
//			this.imageHeader.append("/>\n");
//			return;
//		}
//
//		// children, so close current tag
//		this.imageHeader.append(">\n");
//		int count = 0;
//		while (child != null) {
//			// print children recursively
//			this.constructMetadata(child, level + 1);
//			child = child.getNextSibling();
//			count = count + 1;
//			if ((level > 2) && (count > 20)) {
//				indent(level);
//				this.imageHeader.append("    .");
//				indent(level);
//				this.imageHeader.append("    .");
//				indent(level);
//				this.imageHeader.append("    only first 20 items shown");
//				indent(level);
//				this.imageHeader.append("    .");
//				indent(level);
//				this.imageHeader.append("    .");
//				break;
//			}
//		}
//
//		// print close tag of element
//		indent(level);
//		this.imageHeader.append("</" + node.getNodeName() + ">\n");
//	}

	/**
	 * Add an indent to the <code>level</code>.
	 * 
	 * @param level
	 */
	private void indent(int level) {
		for (int i = 0; i < level; i++) {
			// this.imageHeader.append("    ");
			this.imageHeader.append("\t");
		}
	}

}// END
