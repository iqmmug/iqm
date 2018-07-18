package at.mug.iqm.api.gui.roi;

/*
 * #%L
 * Project: IQM - API
 * File: XMLAnnotationManager.java
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

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import at.mug.iqm.api.IQMConstants;
import at.mug.iqm.api.gui.DefaultDrawingLayer;
import at.mug.iqm.api.gui.IDrawingLayer;
import at.mug.iqm.api.gui.TransparentROILayer;
import at.mug.iqm.api.gui.roi.jaxb.Angle;
import at.mug.iqm.api.gui.roi.jaxb.AngleRoi;
import at.mug.iqm.api.gui.roi.jaxb.Angles;
import at.mug.iqm.api.gui.roi.jaxb.AnnotationLayer;
import at.mug.iqm.api.gui.roi.jaxb.AnnotationLayers;
import at.mug.iqm.api.gui.roi.jaxb.Area;
import at.mug.iqm.api.gui.roi.jaxb.Bounds;
import at.mug.iqm.api.gui.roi.jaxb.Color;
import at.mug.iqm.api.gui.roi.jaxb.ColorChannel;
import at.mug.iqm.api.gui.roi.jaxb.Dash;
import at.mug.iqm.api.gui.roi.jaxb.DashArray;
import at.mug.iqm.api.gui.roi.jaxb.DashPhase;
import at.mug.iqm.api.gui.roi.jaxb.Data;
import at.mug.iqm.api.gui.roi.jaxb.EllipseRoi;
import at.mug.iqm.api.gui.roi.jaxb.EndCap;
import at.mug.iqm.api.gui.roi.jaxb.FreehandRoi;
import at.mug.iqm.api.gui.roi.jaxb.Height;
import at.mug.iqm.api.gui.roi.jaxb.Legs;
import at.mug.iqm.api.gui.roi.jaxb.Length;
import at.mug.iqm.api.gui.roi.jaxb.LineJoin;
import at.mug.iqm.api.gui.roi.jaxb.LineRoi;
import at.mug.iqm.api.gui.roi.jaxb.LineSegment;
import at.mug.iqm.api.gui.roi.jaxb.LineWidth;
import at.mug.iqm.api.gui.roi.jaxb.Location;
import at.mug.iqm.api.gui.roi.jaxb.MiterLimit;
import at.mug.iqm.api.gui.roi.jaxb.ObjectFactory;
import at.mug.iqm.api.gui.roi.jaxb.Path;
import at.mug.iqm.api.gui.roi.jaxb.Point;
import at.mug.iqm.api.gui.roi.jaxb.PointRoi;
import at.mug.iqm.api.gui.roi.jaxb.PolygonRoi;
import at.mug.iqm.api.gui.roi.jaxb.RectangleRoi;
import at.mug.iqm.api.gui.roi.jaxb.RoiSet;
import at.mug.iqm.api.gui.roi.jaxb.Segment;
import at.mug.iqm.api.gui.roi.jaxb.Segments;
import at.mug.iqm.api.gui.roi.jaxb.Stroke;
import at.mug.iqm.api.gui.roi.jaxb.Width;
import at.mug.iqm.api.gui.roi.jaxb.WindingRule;
import at.mug.iqm.api.gui.roi.jaxb.X;
import at.mug.iqm.api.gui.roi.jaxb.Y;
import at.mug.iqm.commons.util.BASE64Helper;
import at.mug.iqm.commons.util.CommonTools;
import at.mug.iqm.commons.util.XMLGregCalUtil;

/**
 * This class is responsible for loading and storing entire stacks of
 * {@link IDrawingLayer}, or {@link DefaultDrawingLayer} respectively.
 * 
 * @author Philipp Kainz
 * @since 3.1
 */
public class XMLAnnotationManager {

	// private class logger
	private static final Logger logger = Logger
			.getLogger(XMLAnnotationManager.class);

	private final String xmlSchemaLocation = "/xsd/AnnotationLayers.xsd";
	public static final String XML_DATA_ENCODING = "base64";

	// export/import file and schema location/inputstream
	private File xmlFile;
	private InputStream xmlSchemaFileIS;

	/**
	 * This is the internal java object for all layers.
	 */
	private List<DefaultDrawingLayer> allLayers;

	// JAXB context objects
	private JAXBContext jaxbContext;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	// the java mapping objects
	/**
	 * The root element of the serialized layers.
	 */
	private AnnotationLayers allLayersJAXB;
	private ObjectFactory of = new ObjectFactory();

	/**
	 * Standard constructor.
	 */
	public XMLAnnotationManager() {
	}

	/**
	 * Create an {@link XMLAnnotationManager} for a specified location.
	 * 
	 * @param allLayers
	 * @param xmlFile
	 */
	public XMLAnnotationManager(List<DefaultDrawingLayer> allLayers,
			File xmlFile) {
		this.xmlFile = xmlFile;
		this.allLayers = allLayers;
	}

	/**
	 * Get the location of the XML file.
	 * 
	 * @return a file object
	 */
	public File getXmlFile() {
		return xmlFile;
	}

	/**
	 * Set the location of the XML file.
	 * 
	 * @param xmlFile
	 */
	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	/**
	 * Set a list of all layers available for saving.
	 * 
	 * @param allLayers
	 */
	public void setAllLayers(List<DefaultDrawingLayer> allLayers) {
		this.allLayers = allLayers;
	}

	/**
	 * Get the re-assembled list of all layers.
	 * 
	 * @return list which can be passed on the the look panel for rendering
	 */
	public List<DefaultDrawingLayer> getAllLayers() {
		return allLayers;
	}

	/**
	 * Validates the XML file against the defined schema.
	 */
	private void validate() {
		Schema schema;
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			this.jaxbContext = JAXBContext.newInstance(AnnotationLayers.class
					.getPackage().getName());

			this.xmlSchemaFileIS = XMLAnnotationManager.class
					.getResourceAsStream(this.xmlSchemaLocation);

			schema = schemaFactory.newSchema(new StreamSource(
					this.xmlSchemaFileIS));
			this.unmarshaller = jaxbContext.createUnmarshaller();
			this.unmarshaller
					.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			this.unmarshaller.setSchema(schema);
			logger.debug("Validation of XML file passed.");
		} catch (SAXException e) {
			// log the error message
			logger.error("An error occurred: ", e);
		} catch (NullPointerException e) {
			// log the error message
			logger.error("An error occurred: ", e);
		} catch (JAXBException e) {
			// log the error message
			logger.error("An error occurred: ", e);
		}
	}

	/**
	 * Writes an XML file to the specified path in the file system.
	 */
	protected synchronized void writeXML() {
		logger.debug("Writing the annotations to XML file...");
		OutputStreamWriter out;
		try {
			out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8");
			try {
				// set context (fully qualified package name)
				this.jaxbContext = JAXBContext
						.newInstance(AnnotationLayers.class.getPackage()
								.getName());

				this.marshaller = this.jaxbContext.createMarshaller();
				this.marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						true);
				this.marshaller.marshal(allLayersJAXB, out);
			} catch (JAXBException ex) {
				logger.error(ex);
			}
			out.flush();
			out.close();
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	/**
	 * Converts the set List of {@link DefaultDrawingLayer}s to XML and writes
	 * the file.
	 * 
	 * @throws JAXBException
	 */
	public void write() throws JAXBException {
		// convert to jaxb
		allLayersJAXB = convertToXML(allLayers);
		writeXML();
	}

	/**
	 * Public delegate method for reading the xml file into a {@link List} of
	 * {@link DefaultDrawingLayer}s.
	 * 
	 * @return the list
	 * @throws JAXBException
	 */
	public List<IDrawingLayer> read() throws JAXBException {
		allLayersJAXB = readXML();
		List<IDrawingLayer> castedLayers = new ArrayList<>();
		allLayers = convertToList(allLayersJAXB);
		for (DefaultDrawingLayer l : allLayers) {
			castedLayers.add((IDrawingLayer) l);
		}
		return castedLayers;
	}

	/**
	 * Loads the annotation layers from an existing XML file. The file is
	 * validated against the corresponding XML schema before loading.
	 * 
	 * @throws JAXBException
	 *             - if the XML file does not comply with the schema
	 */
	protected AnnotationLayers readXML() throws JAXBException {
		// load from XML bean using ObjectFactory
		logger.debug("Loading annotations from file " + this.xmlFile.toString()
				+ "...");
		this.validate();
		// add the mapping root
		allLayersJAXB = (AnnotationLayers) unmarshaller.unmarshal(this.xmlFile);
		return allLayersJAXB;
	}

	/**
	 * Converts the XML mapping format to a {@link List} of
	 * {@link DefaultDrawingLayer}s.
	 * 
	 * @param allLayersJAXB
	 * @param allLayers
	 * @return the assembled list of {@link DefaultDrawingLayer}s
	 */
	protected List<DefaultDrawingLayer> convertToList(AnnotationLayers root) {

		if (root == null)
			throw new IllegalArgumentException(
					"The root element must not be NULL!");

		// the XML document is already read and the content is stored in the
		// root object
		List<AnnotationLayer> allLayersJAXB = root.getAnnotationLayer();

		allLayers = new ArrayList<DefaultDrawingLayer>(allLayersJAXB.size());

		logger.info("Attempting to parse XML object...");

		for (AnnotationLayer layerJAXB : allLayersJAXB) {
			// create new ROI layers without binding it to a display panel
			DefaultDrawingLayer layer = new TransparentROILayer();

			// set global attributes
			layer.setID(UUID.fromString(layerJAXB.getId()));
			layer.setZOrder(layerJAXB.getZOrder().intValue());
			layer.setLayerVisible(layerJAXB.isVisible());
			layer.setSelected(layerJAXB.isSelected());
			layer.setName(layerJAXB.getName());

			// set the layer colors
			for (Color cJAXB : layerJAXB.getColor()) {
				switch (cJAXB.getName()) {
				case DefaultDrawingLayer.DEFAULT_LAYER_COLOR_NAME:
					layer.setDefaultLayerColor(parseXML(cJAXB));
					break;
				case DefaultDrawingLayer.HIGHLIGHT_COLOR_NAME:
					layer.setHighlightColor(parseXML(cJAXB));
					break;
				default:
					logger.warn("No property mapped with name ["
							+ cJAXB.getName() + "].");
					break;
				}
			}

			// set the layer strokes
			for (Stroke sJAXB : layerJAXB.getStroke()) {
				switch (sJAXB.getName()) {
				case DefaultDrawingLayer.DEFAULT_LAYER_STROKE_NAME:
					layer.setDefaultROIStroke(parseXML(sJAXB));
					break;
				case DefaultDrawingLayer.HIGHLIGHT_STROKE_NAME:
					layer.setHighlightStroke(parseXML(sJAXB));
					break;
				default:
					logger.warn("No property mapped with name ["
							+ sJAXB.getName() + "].");
					break;
				}
			}

			// map the existing ROIsets
			for (RoiSet rsJAXB : layerJAXB.getRoiSet()) {
				if (rsJAXB.getClazz().equals(PointROI.class.getName())) {
					ArrayList<PointROI> pointROIs = new ArrayList<PointROI>(10);
					// map the point ROIs
					for (PointRoi prJAXB : rsJAXB.getPointRoi()) {
						PointROI pr = parseXMLPointROI(prJAXB);
						pointROIs.add(prJAXB.getId().intValue(), pr);
						logger.debug("Adding " + pr);
					}
					layer.drawPointROIs(pointROIs);

				} else if (rsJAXB.getClazz().equals(
						RectangleROI.class.getName())) {
					ArrayList<RectangleROI> rectangleROIs = new ArrayList<RectangleROI>(
							10);
					// map the rectangle ROIs
					for (RectangleRoi rrJAXB : rsJAXB.getRectangleRoi()) {
						RectangleROI rr = parseXMLRectangleROI(rrJAXB);
						rectangleROIs.add(rrJAXB.getId().intValue(), rr);
						logger.debug("Adding " + rr);
					}
					layer.drawRectangleROIs(rectangleROIs);

				} else if (rsJAXB.getClazz().equals(AngleROI.class.getName())) {
					ArrayList<AngleROI> angleROIs = new ArrayList<AngleROI>(10);
					// map the angle ROIs
					for (AngleRoi arJAXB : rsJAXB.getAngleRoi()) {
						AngleROI ar = parseXMLAngleROI(arJAXB);
						angleROIs.add(arJAXB.getId().intValue(), ar);
						logger.debug("Adding " + ar);
					}
					layer.drawAngleROIs(angleROIs);

				} else if (rsJAXB.getClazz().equals(EllipseROI.class.getName())) {
					ArrayList<EllipseROI> ellipseROIs = new ArrayList<EllipseROI>(
							10);
					// map the ellipse ROIs
					for (EllipseRoi erJAXB : rsJAXB.getEllipseRoi()) {
						EllipseROI er = parseXMLEllipseROI(erJAXB);
						ellipseROIs.add(erJAXB.getId().intValue(), er);
						logger.debug("Adding " + er);
					}
					layer.drawEllipseROIs(ellipseROIs);

				} else if (rsJAXB.getClazz()
						.equals(FreehandROI.class.getName())) {
					ArrayList<FreehandROI> freehandROIs = new ArrayList<FreehandROI>(
							10);
					// map the freehand ROIs
					for (FreehandRoi frJAXB : rsJAXB.getFreehandRoi()) {
						FreehandROI fr = parseXMLFreehandROI(frJAXB);
						freehandROIs.add(frJAXB.getId().intValue(), fr);
						logger.debug("Adding " + fr);
					}
					layer.drawFreehandROIs(freehandROIs);

				} else if (rsJAXB.getClazz().equals(LineROI.class.getName())) {
					ArrayList<LineROI> lineROIs = new ArrayList<LineROI>(10);
					// map the line ROIs
					for (LineRoi lrJAXB : rsJAXB.getLineRoi()) {
						LineROI lr = parseXMLLineROI(lrJAXB);
						lineROIs.add(lrJAXB.getId().intValue(), lr);
						logger.debug("Adding " + lr);
					}
					layer.drawLineROIs(lineROIs);

				} else if (rsJAXB.getClazz().equals(PolygonROI.class.getName())) {
					ArrayList<PolygonROI> polygonROIs = new ArrayList<PolygonROI>(
							10);
					// map the polygon ROIs
					for (PolygonRoi prJAXB : rsJAXB.getPolygonRoi()) {
						PolygonROI pr = parseXMLPolygonROI(prJAXB);
						polygonROIs.add(prJAXB.getId().intValue(), pr);
						logger.debug("Adding " + pr);
					}
					layer.drawPolygonROIs(polygonROIs);
				}
			}
			// add it to all layers
			allLayers.add(layer.getZOrder(), layer);
		}

		return allLayers;
	}

	/**
	 * Parse a given XML object to a {@link PointROI} instance.
	 * 
	 * @param jaxbObject
	 *            the XML object
	 * @return the java object
	 */
	public PointROI parseXMLPointROI(PointRoi jaxbObject) {
		Location loc = jaxbObject.getLocation();

		// the path is not necessarily being deserialized
		PointROI pr = new PointROI(loc.getPoint().getX().getValue()
				.doubleValue(), loc.getPoint().getY().getValue().doubleValue());

		return pr;
	}

	/**
	 * Parse a given XML object to a {@link RectangleROI} instance.
	 * 
	 * @param jaxbObject
	 *            the XML object
	 * @return the java object
	 */
	public RectangleROI parseXMLRectangleROI(RectangleRoi jaxbObject) {
		return new RectangleROI(parseXML(jaxbObject.getPath()));
	}

	/**
	 * Parse a given XML object to a {@link AngleROI} instance.
	 * 
	 * @param jaxbObject
	 *            the XML object
	 * @return the java object
	 */
	public AngleROI parseXMLAngleROI(AngleRoi jaxbObject) {
		return new AngleROI(parseXML(jaxbObject.getPath()));
	}

	/**
	 * Parse a given XML object to a {@link EllipseROI} instance.
	 * 
	 * @param jaxbObject
	 *            the XML object
	 * @return the java object
	 */
	public EllipseROI parseXMLEllipseROI(EllipseRoi jaxbObject) {
		return new EllipseROI(parseXML(jaxbObject.getPath()));
	}

	/**
	 * Parse a given XML object to a {@link FreehandROI} instance.
	 * 
	 * @param jaxbObject
	 *            the XML object
	 * @return the java object
	 */
	public FreehandROI parseXMLFreehandROI(FreehandRoi jaxbObject) {
		return new FreehandROI(parseXML(jaxbObject.getPath()));
	}

	/**
	 * Parse a given XML object to a {@link LineROI} instance.
	 * 
	 * @param jaxbObject
	 *            the XML object
	 * @return the java object
	 */
	public LineROI parseXMLLineROI(LineRoi jaxbObject) {
		return new LineROI(parseXML(jaxbObject.getPath()));
	}

	/**
	 * Parse a given XML object to a {@link PolygonROI} instance.
	 * 
	 * @param jaxbObject
	 *            the XML object
	 * @return the java object
	 */
	public PolygonROI parseXMLPolygonROI(PolygonRoi jaxbObject) {
		return new PolygonROI(parseXML(jaxbObject.getPath()));
	}

	/**
	 * Reconstructs a Shape object according to a given path.
	 * 
	 * @param jaxbPath
	 * @return the {@link Shape}
	 */
	public Shape parseXML(Path jaxbPath) {
		GeneralPath gp = new GeneralPath();

		// winding rule
		int wr = jaxbPath.getWindingRule().getCode().intValue();
		gp.setWindingRule(wr);

		// segments and coordinates
		Segments segRootJAXB = jaxbPath.getSegments();

		// TODO for now, we trust the sequence number is ascending in the XML
		// shape
		// representation
		for (Segment sJAXB : segRootJAXB.getSegment()) {
			logger.debug("Reading segment: sequence# "
					+ sJAXB.getSequence().intValue());

			double[] coords = new double[6];
			// parse the coordinates in the segment
			for (Point p : sJAXB.getPoint()) {
				double[] point = parseXML(p);
				switch (p.getId()) {
				case "0":
					coords[0] = point[0];
					coords[1] = point[1];
					break;
				case "1":
					coords[2] = point[0];
					coords[3] = point[1];
					break;
				case "2":
					coords[4] = point[0];
					coords[5] = point[1];
					break;
				default:
					logger.warn("No point mapped with ID [" + p.getId() + "].");
					break;
				}
			}

			int code = sJAXB.getCode().intValue();
			logger.debug("Using method "
					+ AbstractROIShape.segmentCodeToString(code)
					+ " for path segment, sequcence# "
					+ sJAXB.getSequence().intValue());

			switch (code) {
			case AbstractROIShape.SEG_MOVETO:
				gp.moveTo(coords[0], coords[1]);
				break;

			case AbstractROIShape.SEG_LINETO:
				gp.lineTo(coords[0], coords[1]);
				break;

			case AbstractROIShape.SEG_QUADTO:
				gp.quadTo(coords[0], coords[1], coords[2], coords[3]);
				break;

			case AbstractROIShape.SEG_CUBICTO:
				gp.curveTo(coords[0], coords[1], coords[2], coords[3],
						coords[4], coords[5]);
				break;

			case AbstractROIShape.SEG_CLOSE:
				gp.closePath();
				break;

			default:
				logger.warn("No property mapped with ID [" + sJAXB.getCode()
						+ "].");
				break;
			}
		}

		logger.debug("Successfully deserialized " + gp);
		return gp;
	}

	/**
	 * Parses a 2D point into its double coordinates.
	 * 
	 * @param jaxbPoint
	 * @return
	 */
	public double[] parseXML(Point jaxbPoint) {
		X xJAXB = jaxbPoint.getX();
		double x = xJAXB.getValue().doubleValue();
		Y yJAXB = jaxbPoint.getY();
		double y = yJAXB.getValue().doubleValue();
		logger.debug("Successfully deserialized 2D point to coordinates (" + x
				+ "," + y + ")");
		return new double[] { x, y };
	}

	/**
	 * Parse a given XML object to a {@link java.awt.BasicStroke} instance.
	 * 
	 * @param jaxbObject
	 *            the XML object
	 * @return the java object
	 */
	public java.awt.BasicStroke parseXML(Stroke jaxbObject) {
		DashArray da = jaxbObject.getDashArray();
		float[] dashArray = null;
		if (da != null && !da.getDash().isEmpty()) {
			dashArray = new float[da.getDash().size()];

			// map the dash array
			for (Dash d : da.getDash()) {
				dashArray[d.getOrder().intValue()] = d.getValue().floatValue();
			}
		}

		BasicStroke bs = new BasicStroke(jaxbObject.getLineWidth().getValue()
				.floatValue(), jaxbObject.getEndCap().getValue().intValue(),
				jaxbObject.getLineJoin().getValue().intValue(), jaxbObject
						.getMiterLimit().getValue().floatValue(), dashArray,
				jaxbObject.getDashPhase().getValue().floatValue());
		logger.debug("Successfully deserialized " + bs);
		return bs;

	}

	/**
	 * Parse a given XML object to a {@link java.awt.Color} instance.
	 * 
	 * @param jaxbObject
	 *            the XML object
	 * @return the Java object
	 */
	public java.awt.Color parseXML(Color jaxbObject) {
		float r = 0, g = 0, b = 0, a = 255;
		for (ColorChannel cc : jaxbObject.getColorChannel()) {
			if (cc.getType().equals("red")) {
				r = cc.getValue().intValue();
				if (cc.getClazz().equals(Integer.class.getName()))
					r /= 255f;
			} else if (cc.getType().equals("green")) {
				g = cc.getValue().intValue();
				if (cc.getClazz().equals(Integer.class.getName()))
					g /= 255f;
			} else if (cc.getType().equals("blue")) {
				b = cc.getValue().intValue();
				if (cc.getClazz().equals(Integer.class.getName()))
					b /= 255f;
			} else if (cc.getType().equals("alpha")) {
				a = cc.getValue().intValue();
				if (cc.getClazz().equals(Integer.class.getName()))
					a /= 255f;
			}
		}

		java.awt.Color c = new java.awt.Color(r, g, b, a);
		logger.debug("Successfully deserialized " + c);
		return c;
	}

	/**
	 * Converts a list of {@link DefaultDrawingLayer} to the XML mapping format.
	 * 
	 * @param allLayers
	 * @param allLayersJAXB
	 * @return the XML representation ready for serialization
	 */
	protected AnnotationLayers convertToXML(List<DefaultDrawingLayer> allLayers) {

		AnnotationLayers root = of.createAnnotationLayers();

		logger.info("Attempting to serialized to XML object...");

		// set the root attributes
		root.setIQMVersion(IQMConstants.APPLICATION_VERSION);
		root.setJavaVersion(System.getProperty("java.version"));
		root.setLastUpdate(XMLGregCalUtil.asXMLGregorianCalendar(new Date()));

		// skim through the available ROI layers and assemble the XML object
		for (int layerIndex = 0; layerIndex < allLayers.size(); layerIndex++) {
			// fetch the java layer object
			DefaultDrawingLayer layer = allLayers.get(layerIndex);

			// create a new layer binding object
			AnnotationLayer layerJAXB = of.createAnnotationLayer();

			// set the attributes
			layerJAXB.setName(layer.getName());
			layerJAXB.setId(layer.getID().toString());
			layerJAXB.setSelected(layer.isSelected());
			layerJAXB.setVisible(layer.isLayerVisible());
			layerJAXB.setZOrder(BigInteger.valueOf(layer.getZOrder()));

			Color defLayerColorJAXB = toXML(layer.getDefaultLayerColor(),
					of.createColor(),
					DefaultDrawingLayer.DEFAULT_LAYER_COLOR_NAME);
			layerJAXB.getColor().add(defLayerColorJAXB);

			Stroke defLayerStrokeJAXB = toXML(layer.getDefaultROIStroke(),
					of.createStroke(),
					DefaultDrawingLayer.DEFAULT_LAYER_STROKE_NAME);
			layerJAXB.getStroke().add(defLayerStrokeJAXB);

			Color highlightColorJAXB = toXML(layer.getHighlightColor(),
					of.createColor(), DefaultDrawingLayer.HIGHLIGHT_COLOR_NAME);
			layerJAXB.getColor().add(highlightColorJAXB);

			Stroke highlightStrokeJAXB = toXML(layer.getHighlightStroke(),
					of.createStroke(),
					DefaultDrawingLayer.HIGHLIGHT_STROKE_NAME);
			layerJAXB.getStroke().add(highlightStrokeJAXB);

			// map the LineROI set
			RoiSet lines = lineROItoXML(layer.getLineROIs(), of.createRoiSet());
			if (lines != null)
				layerJAXB.getRoiSet().add(lines);

			// map the PointROI set
			RoiSet points = pointROItoXML(layer.getPointROIs(),
					of.createRoiSet());
			if (points != null)
				layerJAXB.getRoiSet().add(points);

			// map the RectangleROI set
			RoiSet rectangles = rectangleROItoXML(layer.getRectangleROIs(),
					of.createRoiSet());
			if (rectangles != null)
				layerJAXB.getRoiSet().add(rectangles);

			// map the EllipseROI set
			RoiSet ellipses = ellipseROItoXML(layer.getEllipseROIs(),
					of.createRoiSet());
			if (ellipses != null)
				layerJAXB.getRoiSet().add(ellipses);

			// map the FreehandROI set
			RoiSet freehands = freehandROItoXML(layer.getFreehandROIs(),
					of.createRoiSet());
			if (freehands != null)
				layerJAXB.getRoiSet().add(freehands);

			// add the PolygonROI set
			RoiSet polygons = polygonROItoXML(layer.getPolygonROIs(),
					of.createRoiSet());
			if (polygons != null)
				layerJAXB.getRoiSet().add(polygons);

			// add the AngleROI set
			RoiSet angles = angleROItoXML(layer.getAngleROIs(),
					of.createRoiSet());
			if (angles != null)
				layerJAXB.getRoiSet().add(angles);

			root.getAnnotationLayer().add(layerJAXB);
		}

		return root;
	}

	/**
	 * Converts the {@link ArrayList} of {@link AngleROI}s to XML
	 * representation.
	 * 
	 * @param shapes
	 * @param roiSetJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @return the XML representation
	 */
	public RoiSet angleROItoXML(ArrayList<AngleROI> shapes, RoiSet roiSetJAXB) {
		if (roiSetJAXB == null) {
			roiSetJAXB = of.createRoiSet();
		}

		if (shapes.isEmpty()) {
			return null;
		}

		// set the attribute
		roiSetJAXB.setClazz(AngleROI.class.getName());

		// loop through existing shapes
		for (int i = 0; i < shapes.size(); i++) {
			AngleROI angle = shapes.get(i);
			AngleRoi angleJAXB = of.createAngleRoi();

			angleJAXB.setId(BigInteger.valueOf(i));

			// create the legs
			Legs legsJAXB = of.createLegs();
			LineSegment ls1 = of.createLineSegment();
			ls1.setType("firstLeg");
			Length l1 = of.createLength();
			l1.setClazz(Double.class.getName());
			l1.setUnits("px");
			l1.setValue(BigDecimal.valueOf(angle.getLengthFirstLeg()));
			ls1.setLength(l1);
			LineSegment ls2 = of.createLineSegment();
			ls2.setType("secondLeg");
			Length l2 = of.createLength();
			l2.setClazz(Double.class.getName());
			l2.setUnits("px");
			l2.setValue(BigDecimal.valueOf(angle.getLengthSecondLeg()));
			ls2.setLength(l2);

			legsJAXB.getLineSegment().add(ls1);
			legsJAXB.getLineSegment().add(ls2);

			angleJAXB.setLegs(legsJAXB);

			Angles anglesJAXB = of.createAngles();
			Angle a1 = of.createAngle();
			a1.setClazz(Double.class.getName());
			a1.setType("enclosing");
			a1.setUnits("degree");
			a1.setValue(BigDecimal.valueOf(angle.getAngle()));

			Angle a2 = of.createAngle();
			a2.setClazz(Double.class.getName());
			a2.setType("complementary");
			a2.setUnits("degree");
			a2.setValue(BigDecimal.valueOf(angle.getComplementaryAngle()));

			anglesJAXB.getAngle().add(a1);
			anglesJAXB.getAngle().add(a2);

			angleJAXB.setAngles(anglesJAXB);

			angleJAXB.setPath(toXML(angle.getPathIterator(), of.createPath()));

			angleJAXB.setBounds(toXML(angle.getBounds2D(), of.createBounds()));

			angleJAXB.setData(toXML(angle, of.createData()));

			roiSetJAXB.getAngleRoi().add(angleJAXB);
		}
		return roiSetJAXB;
	}

	/**
	 * Converts the {@link ArrayList} of {@link PolygonROI}s to XML
	 * representation.
	 * 
	 * @param shapes
	 * @param roiSetJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @return the XML representation
	 */
	public RoiSet polygonROItoXML(ArrayList<PolygonROI> shapes,
			RoiSet roiSetJAXB) {
		if (roiSetJAXB == null) {
			roiSetJAXB = of.createRoiSet();
		}

		if (shapes.isEmpty()) {
			return null;
		}

		// set the attribute
		roiSetJAXB.setClazz(PolygonROI.class.getName());

		// loop through existing shapes
		for (int i = 0; i < shapes.size(); i++) {
			PolygonROI polygon = shapes.get(i);
			PolygonRoi polygonJAXB = of.createPolygonRoi();

			polygonJAXB.setId(BigInteger.valueOf(i));

			polygonJAXB.setPath(toXML(polygon.getPathIterator(),
					of.createPath()));

			polygonJAXB.setBounds(toXML(polygon.getBounds2D(),
					of.createBounds()));

			polygonJAXB.setData(toXML(polygon, of.createData()));

			roiSetJAXB.getPolygonRoi().add(polygonJAXB);
		}
		return roiSetJAXB;
	}

	/**
	 * Converts the {@link ArrayList} of {@link FreehandROI}s to XML
	 * representation.
	 * 
	 * @param shapes
	 * @param roiSetJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @return the XML representation
	 */
	public RoiSet freehandROItoXML(ArrayList<FreehandROI> shapes,
			RoiSet roiSetJAXB) {
		if (roiSetJAXB == null) {
			roiSetJAXB = of.createRoiSet();
		}

		if (shapes.isEmpty()) {
			return null;
		}

		// set the attribute
		roiSetJAXB.setClazz(FreehandROI.class.getName());

		// loop through existing shapes
		for (int i = 0; i < shapes.size(); i++) {
			FreehandROI fh = shapes.get(i);
			FreehandRoi fhJAXB = of.createFreehandRoi();

			fhJAXB.setId(BigInteger.valueOf(i));

			fhJAXB.setPath(toXML(fh.getPathIterator(), of.createPath()));

			fhJAXB.setBounds(toXML(fh.getBounds2D(), of.createBounds()));

			fhJAXB.setData(toXML(fh, of.createData()));

			roiSetJAXB.getFreehandRoi().add(fhJAXB);
		}
		return roiSetJAXB;
	}

	/**
	 * Converts the {@link ArrayList} of {@link EllipseROI}s to XML
	 * representation.
	 * 
	 * @param shapes
	 * @param roiSetJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @return the XML representation
	 */
	public RoiSet ellipseROItoXML(ArrayList<EllipseROI> shapes,
			RoiSet roiSetJAXB) {
		if (roiSetJAXB == null) {
			roiSetJAXB = of.createRoiSet();
		}

		if (shapes.isEmpty()) {
			return null;
		}

		// set the attribute
		roiSetJAXB.setClazz(EllipseROI.class.getName());

		// loop through existing shapes
		for (int i = 0; i < shapes.size(); i++) {
			EllipseROI ellipse = shapes.get(i);
			EllipseRoi ellipseJAXB = of.createEllipseRoi();

			ellipseJAXB.setId(BigInteger.valueOf(i));

			Area area = of.createArea();
			area.setClazz(Double.class.getName());
			area.setUnits("px^2");
			area.setValue(BigDecimal.valueOf(ellipse.getArea()));
			ellipseJAXB.setArea(area);

			ellipseJAXB.setPath(toXML(ellipse.getPathIterator(),
					of.createPath()));

			ellipseJAXB.setBounds(toXML(ellipse.getBounds2D(),
					of.createBounds()));

			ellipseJAXB.setData(toXML(ellipse, of.createData()));

			roiSetJAXB.getEllipseRoi().add(ellipseJAXB);
		}
		return roiSetJAXB;
	}

	/**
	 * Converts the {@link ArrayList} of {@link RectangleROI}s to XML
	 * representation.
	 * 
	 * @param shapes
	 * @param roiSetJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @return the XML representation
	 */
	public RoiSet rectangleROItoXML(ArrayList<RectangleROI> shapes,
			RoiSet roiSetJAXB) {
		if (roiSetJAXB == null) {
			roiSetJAXB = of.createRoiSet();
		}

		if (shapes.isEmpty()) {
			return null;
		}

		// set the attribute
		roiSetJAXB.setClazz(RectangleROI.class.getName());

		// loop through existing shapes
		for (int i = 0; i < shapes.size(); i++) {
			RectangleROI rect = shapes.get(i);
			RectangleRoi rectJAXB = of.createRectangleRoi();

			rectJAXB.setId(BigInteger.valueOf(i));

			Area area = of.createArea();
			area.setClazz(Double.class.getName());
			area.setUnits("px^2");
			area.setValue(BigDecimal.valueOf(rect.getArea()));
			rectJAXB.setArea(area);

			rectJAXB.setPath(toXML(rect.getPathIterator(), of.createPath()));

			rectJAXB.setBounds(toXML(rect.getBounds2D(), of.createBounds()));

			rectJAXB.setData(toXML(rect, of.createData()));

			roiSetJAXB.getRectangleRoi().add(rectJAXB);
		}
		return roiSetJAXB;
	}

	/**
	 * Converts the {@link ArrayList} of {@link PointROI}s to XML
	 * representation.
	 * 
	 * @param shapes
	 * @param roiSetJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @return the XML representation
	 */
	public RoiSet pointROItoXML(ArrayList<PointROI> shapes, RoiSet roiSetJAXB) {
		if (roiSetJAXB == null) {
			roiSetJAXB = of.createRoiSet();
		}

		if (shapes.isEmpty()) {
			return null;
		}

		// set the attribute
		roiSetJAXB.setClazz(PointROI.class.getName());

		// loop through existing shapes
		for (int i = 0; i < shapes.size(); i++) {
			PointROI point = shapes.get(i);
			PointRoi pointJAXB = of.createPointRoi();

			pointJAXB.setId(BigInteger.valueOf(i));

			Location loc = of.createLocation();
			loc.setPoint(toXML(point.getLocation(), of.createPoint(), "0"));
			pointJAXB.setLocation(loc);

			pointJAXB.setPath(toXML(point.getPathIterator(), of.createPath()));

			pointJAXB.setBounds(toXML(point.getBounds2D(), of.createBounds()));

			pointJAXB.setData(toXML(point, of.createData()));

			roiSetJAXB.getPointRoi().add(pointJAXB);
		}
		return roiSetJAXB;
	}

	/**
	 * Converts the {@link ArrayList} of {@link LineROI}s to XML representation.
	 * 
	 * @param shapes
	 * @param roiSetJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @return the XML representation
	 */
	public RoiSet lineROItoXML(ArrayList<LineROI> shapes, RoiSet roiSetJAXB) {
		if (roiSetJAXB == null) {
			roiSetJAXB = of.createRoiSet();
		}

		if (shapes.isEmpty()) {
			return null;
		}

		// set the attribute
		roiSetJAXB.setClazz(LineROI.class.getName());

		// loop through existing shapes
		for (int i = 0; i < shapes.size(); i++) {
			LineROI line = shapes.get(i);
			LineRoi lineJAXB = of.createLineRoi();

			lineJAXB.setId(BigInteger.valueOf(i));

			lineJAXB.getPoint()
					.add(toXML(line.getStartPoint(), of.createPoint(),
							"startPoint"));
			lineJAXB.getPoint().add(
					toXML(line.getEndPoint(), of.createPoint(), "endPoint"));

			// set the line length
			Length l = of.createLength();
			l.setClazz(Double.class.getName());
			l.setUnits("px");
			l.setValue(BigDecimal.valueOf(line.getLength()));

			LineSegment ls = of.createLineSegment();
			ls.setLength(l);
			lineJAXB.setLineSegment(ls);

			// set the path
			lineJAXB.setPath(toXML(line.getPathIterator(), of.createPath()));

			// set the bounds object
			lineJAXB.setBounds(toXML(line.getBounds2D(), of.createBounds()));

			// set the serialized stream
			lineJAXB.setData(toXML(line, of.createData()));

			// add the line
			roiSetJAXB.getLineRoi().add(lineJAXB);
		}

		return roiSetJAXB;
	}

	public Data toXML(Object javaObject, Data dataJAXB) {
		if (dataJAXB == null) {
			dataJAXB = of.createData();
		}

		dataJAXB.setEncoding(XML_DATA_ENCODING);

		// serialize the object
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(javaObject);
			oos.close();

			byte[] obytes = bos.toByteArray();
			bos.close();

			dataJAXB.setValue(BASE64Helper.encodeToString(obytes));
		} catch (IOException ex) {
			logger.error("Cannot serialize object. ", ex);
			dataJAXB.setValue(null);
		}

		return dataJAXB;
	}

	/**
	 * Generates an XML representation of the given {@link PathIterator} object.
	 * 
	 * @param iter
	 * @param pathJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @return the XML representation
	 */
	public Path toXML(PathIterator iter, Path pathJAXB) {
		if (pathJAXB == null) {
			pathJAXB = of.createPath();
		}

		WindingRule wr = of.createWindingRule();
		wr.setName(AbstractROIShape.windingRuleToString(iter.getWindingRule()));
		wr.setCode(BigInteger.valueOf(iter.getWindingRule()));
		pathJAXB.setWindingRule(wr);

		Segments segmentsJAXB = of.createSegments();

		int i = 0;
		while (!iter.isDone()) {
			double[] coords = new double[6];
			int segCode = iter.currentSegment(coords);

			System.out.println(CommonTools.doubleArrayToString(coords));

			Segment segmentJAXB = of.createSegment();
			segmentJAXB.setType(AbstractROIShape.segmentCodeToString(segCode));
			segmentJAXB.setCode(BigInteger.valueOf(segCode));
			segmentJAXB.setSequence(BigInteger.valueOf(i));

			// get the coordinates of the segment
			segmentJAXB.getPoint().add(
					toXML(new Point2D.Double(coords[0], coords[1]),
							of.createPoint(), "0"));
			segmentJAXB.getPoint().add(
					toXML(new Point2D.Double(coords[2], coords[3]),
							of.createPoint(), "1"));
			segmentJAXB.getPoint().add(
					toXML(new Point2D.Double(coords[4], coords[5]),
							of.createPoint(), "2"));

			segmentsJAXB.getSegment().add(segmentJAXB);

			logger.trace("sequ#"
					+ i
					+ ": "
					+ AbstractROIShape.segmentCodeToString(segCode)
					+ " ("
					+ segCode
					+ ") : "
					+ AbstractROIShape.windingRuleToString(iter
							.getWindingRule()) + " (" + iter.getWindingRule()
					+ ") : " + CommonTools.doubleArrayToString(coords));
			i++;
			iter.next();
		}

		// set the total number of segments
		segmentsJAXB.setCount(BigInteger.valueOf(i));

		pathJAXB.setSegments(segmentsJAXB);

		return pathJAXB;
	}

	/**
	 * Converts the boundaries from a {@link Rectangle2D} to the XML
	 * representation.
	 * 
	 * @param bounds
	 * @param boundsJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @return the XML representation
	 */
	public Bounds toXML(Rectangle2D bounds, Bounds boundsJAXB) {
		if (boundsJAXB == null) {
			boundsJAXB = of.createBounds();
		}

		X x = of.createX();
		x.setClazz(Double.class.getName());
		x.setValue(BigDecimal.valueOf(bounds.getX()));

		Y y = of.createY();
		y.setClazz(Double.class.getName());
		y.setValue(BigDecimal.valueOf(bounds.getY()));

		boundsJAXB.setX(x);
		boundsJAXB.setY(y);

		Width w = of.createWidth();
		w.setClazz(Double.class.getName());
		w.setValue(BigDecimal.valueOf(bounds.getWidth()));

		Height h = of.createHeight();
		h.setClazz(Double.class.getName());
		h.setValue(BigDecimal.valueOf(bounds.getHeight()));

		boundsJAXB.setWidth(w);
		boundsJAXB.setHeight(h);

		return boundsJAXB;
	}

	/**
	 * Converts a {@link Point2D} to the XML representation.
	 * 
	 * @param point
	 * @param pointJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @param id
	 * @return the XML representation
	 */
	public Point toXML(Point2D point, Point pointJAXB, String id) {
		if (pointJAXB == null) {
			pointJAXB = of.createPoint();
		}

		pointJAXB.setId(id);
		X x = of.createX();
		x.setClazz(Double.class.getName());
		x.setValue(BigDecimal.valueOf(point.getX()));

		Y y = of.createY();
		y.setClazz(Double.class.getName());
		y.setValue(BigDecimal.valueOf(point.getY()));

		pointJAXB.setX(x);
		pointJAXB.setY(y);

		return pointJAXB;
	}

	/**
	 * Converts a given {@link java.awt.BasicStroke} instance to the
	 * serializable form.
	 * 
	 * @param stroke
	 * @param strokeJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @param name
	 * @return the XML representation
	 */
	public Stroke toXML(BasicStroke stroke, Stroke strokeJAXB, String name) {
		if (strokeJAXB == null) {
			strokeJAXB = of.createStroke();
		}

		strokeJAXB.setClazz(stroke.getClass().getName());
		strokeJAXB.setName(name);
		LineWidth lw = of.createLineWidth();
		lw.setClazz(Float.class.getName());
		lw.setValue(BigDecimal.valueOf(stroke.getLineWidth()));
		strokeJAXB.setLineWidth(lw);

		LineJoin lj = of.createLineJoin();
		lj.setClazz(Integer.class.getName());
		lj.setValue(BigInteger.valueOf(stroke.getLineJoin()));
		strokeJAXB.setLineJoin(lj);

		EndCap ec = of.createEndCap();
		ec.setClazz(Float.class.getName());
		ec.setValue(BigInteger.valueOf(stroke.getEndCap()));
		strokeJAXB.setEndCap(ec);

		MiterLimit ml = of.createMiterLimit();
		ml.setClazz(Float.class.getName());
		ml.setValue(BigDecimal.valueOf(stroke.getMiterLimit()));
		strokeJAXB.setMiterLimit(ml);

		DashArray da = of.createDashArray();
		float[] dashArray = stroke.getDashArray();

		if (dashArray != null) {
			for (int i = 0; i < dashArray.length; i++) {
				Dash dash = of.createDash();
				dash.setClazz(Float.class.getName());
				dash.setOrder(BigInteger.valueOf(i));
				dash.setValue(BigDecimal.valueOf(stroke.getDashArray()[i]));
				da.getDash().add(dash);
			}
		}

		strokeJAXB.setDashArray(da);

		DashPhase dp = of.createDashPhase();
		dp.setClazz(Float.class.getName());
		dp.setValue(BigDecimal.valueOf(stroke.getDashPhase()));
		strokeJAXB.setDashPhase(dp);

		return strokeJAXB;
	}

	/**
	 * Converts a given {@link java.awt.Color} instance to the serializable
	 * form.
	 * 
	 * @param c
	 * @param colorJAXB
	 *            an existing object, if <code>null</code>, a new object will be
	 *            generated
	 * @param name
	 * @return the assembled XML object
	 */
	public Color toXML(java.awt.Color c, Color colorJAXB, String name) {
		if (colorJAXB == null) {
			colorJAXB = of.createColor();
		}

		colorJAXB.setClazz(c.getClass().getName());
		colorJAXB.setName(name);

		// create the channel info
		ColorChannel red = of.createColorChannel();
		red.setClazz(Integer.class.getName());
		red.setType("red");
		red.setValue(BigInteger.valueOf(c.getRed()));

		ColorChannel green = of.createColorChannel();
		green.setClazz(Integer.class.getName());
		green.setType("green");
		green.setValue(BigInteger.valueOf(c.getGreen()));

		ColorChannel blue = of.createColorChannel();
		blue.setClazz(Integer.class.getName());
		blue.setType("blue");
		blue.setValue(BigInteger.valueOf(c.getBlue()));

		ColorChannel alpha = of.createColorChannel();
		alpha.setClazz(Integer.class.getName());
		alpha.setType("alpha");
		alpha.setValue(BigInteger.valueOf(c.getAlpha()));

		colorJAXB.getColorChannel().add(red);
		colorJAXB.getColorChannel().add(green);
		colorJAXB.getColorChannel().add(blue);
		colorJAXB.getColorChannel().add(alpha);

		return colorJAXB;
	}

	private static void testLoad(File f) {
		XMLAnnotationManager mgr = new XMLAnnotationManager();
		mgr.setXmlFile(f);
		try {
			List<IDrawingLayer> list = mgr.read();
			for (IDrawingLayer l : list) {
				logger.debug(l.getName());
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException,
			InstantiationException, IllegalAccessException {
		testLoad(new File(args[0]));
	}

}
