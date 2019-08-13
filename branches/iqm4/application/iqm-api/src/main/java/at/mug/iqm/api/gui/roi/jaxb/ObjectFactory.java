//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.19 at 06:52:53 PM CEST 
//


package at.mug.iqm.api.gui.roi.jaxb;

/*
 * #%L
 * Project: IQM - API
 * File: ObjectFactory.java
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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the at.mug.iqm.api.gui.roi.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _LineJoin_QNAME = new QName("", "lineJoin");
    private final static QName _MiterLimit_QNAME = new QName("", "miterLimit");
    private final static QName _EndCap_QNAME = new QName("", "endCap");
    private final static QName _Data_QNAME = new QName("", "data");
    private final static QName _DashArray_QNAME = new QName("", "dashArray");
    private final static QName _RoiSet_QNAME = new QName("", "roiSet");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: at.mug.iqm.api.gui.roi.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Segments }
     * 
     */
    public Segments createSegments() {
        return new Segments();
    }

    /**
     * Create an instance of {@link Segment }
     * 
     */
    public Segment createSegment() {
        return new Segment();
    }

    /**
     * Create an instance of {@link Point }
     * 
     */
    public Point createPoint() {
        return new Point();
    }

    /**
     * Create an instance of {@link X }
     * 
     */
    public X createX() {
        return new X();
    }

    /**
     * Create an instance of {@link Y }
     * 
     */
    public Y createY() {
        return new Y();
    }

    /**
     * Create an instance of {@link Location }
     * 
     */
    public Location createLocation() {
        return new Location();
    }

    /**
     * Create an instance of {@link Data }
     * 
     */
    public Data createData() {
        return new Data();
    }

    /**
     * Create an instance of {@link EndCap }
     * 
     */
    public EndCap createEndCap() {
        return new EndCap();
    }

    /**
     * Create an instance of {@link AnnotationLayers }
     * 
     */
    public AnnotationLayers createAnnotationLayers() {
        return new AnnotationLayers();
    }

    /**
     * Create an instance of {@link AnnotationLayer }
     * 
     */
    public AnnotationLayer createAnnotationLayer() {
        return new AnnotationLayer();
    }

    /**
     * Create an instance of {@link Color }
     * 
     */
    public Color createColor() {
        return new Color();
    }

    /**
     * Create an instance of {@link ColorChannel }
     * 
     */
    public ColorChannel createColorChannel() {
        return new ColorChannel();
    }

    /**
     * Create an instance of {@link Stroke }
     * 
     */
    public Stroke createStroke() {
        return new Stroke();
    }

    /**
     * Create an instance of {@link LineWidth }
     * 
     */
    public LineWidth createLineWidth() {
        return new LineWidth();
    }

    /**
     * Create an instance of {@link LineJoin }
     * 
     */
    public LineJoin createLineJoin() {
        return new LineJoin();
    }

    /**
     * Create an instance of {@link MiterLimit }
     * 
     */
    public MiterLimit createMiterLimit() {
        return new MiterLimit();
    }

    /**
     * Create an instance of {@link DashArray }
     * 
     */
    public DashArray createDashArray() {
        return new DashArray();
    }

    /**
     * Create an instance of {@link DashPhase }
     * 
     */
    public DashPhase createDashPhase() {
        return new DashPhase();
    }

    /**
     * Create an instance of {@link RoiSet }
     * 
     */
    public RoiSet createRoiSet() {
        return new RoiSet();
    }

    /**
     * Create an instance of {@link PolygonRoi }
     * 
     */
    public PolygonRoi createPolygonRoi() {
        return new PolygonRoi();
    }

    /**
     * Create an instance of {@link Path }
     * 
     */
    public Path createPath() {
        return new Path();
    }

    /**
     * Create an instance of {@link WindingRule }
     * 
     */
    public WindingRule createWindingRule() {
        return new WindingRule();
    }

    /**
     * Create an instance of {@link Bounds }
     * 
     */
    public Bounds createBounds() {
        return new Bounds();
    }

    /**
     * Create an instance of {@link Width }
     * 
     */
    public Width createWidth() {
        return new Width();
    }

    /**
     * Create an instance of {@link Height }
     * 
     */
    public Height createHeight() {
        return new Height();
    }

    /**
     * Create an instance of {@link FreehandRoi }
     * 
     */
    public FreehandRoi createFreehandRoi() {
        return new FreehandRoi();
    }

    /**
     * Create an instance of {@link Angles }
     * 
     */
    public Angles createAngles() {
        return new Angles();
    }

    /**
     * Create an instance of {@link Angle }
     * 
     */
    public Angle createAngle() {
        return new Angle();
    }

    /**
     * Create an instance of {@link RectangleRoi }
     * 
     */
    public RectangleRoi createRectangleRoi() {
        return new RectangleRoi();
    }

    /**
     * Create an instance of {@link Area }
     * 
     */
    public Area createArea() {
        return new Area();
    }

    /**
     * Create an instance of {@link Length }
     * 
     */
    public Length createLength() {
        return new Length();
    }

    /**
     * Create an instance of {@link LineRoi }
     * 
     */
    public LineRoi createLineRoi() {
        return new LineRoi();
    }

    /**
     * Create an instance of {@link LineSegment }
     * 
     */
    public LineSegment createLineSegment() {
        return new LineSegment();
    }

    /**
     * Create an instance of {@link AngleRoi }
     * 
     */
    public AngleRoi createAngleRoi() {
        return new AngleRoi();
    }

    /**
     * Create an instance of {@link Legs }
     * 
     */
    public Legs createLegs() {
        return new Legs();
    }

    /**
     * Create an instance of {@link PointRoi }
     * 
     */
    public PointRoi createPointRoi() {
        return new PointRoi();
    }

    /**
     * Create an instance of {@link Dash }
     * 
     */
    public Dash createDash() {
        return new Dash();
    }

    /**
     * Create an instance of {@link EllipseRoi }
     * 
     */
    public EllipseRoi createEllipseRoi() {
        return new EllipseRoi();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineJoin }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "lineJoin")
    public JAXBElement<LineJoin> createLineJoin(LineJoin value) {
        return new JAXBElement<LineJoin>(_LineJoin_QNAME, LineJoin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MiterLimit }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "miterLimit")
    public JAXBElement<MiterLimit> createMiterLimit(MiterLimit value) {
        return new JAXBElement<MiterLimit>(_MiterLimit_QNAME, MiterLimit.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndCap }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "endCap")
    public JAXBElement<EndCap> createEndCap(EndCap value) {
        return new JAXBElement<EndCap>(_EndCap_QNAME, EndCap.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Data }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "data")
    public JAXBElement<Data> createData(Data value) {
        return new JAXBElement<Data>(_Data_QNAME, Data.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DashArray }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "dashArray")
    public JAXBElement<DashArray> createDashArray(DashArray value) {
        return new JAXBElement<DashArray>(_DashArray_QNAME, DashArray.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RoiSet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "roiSet")
    public JAXBElement<RoiSet> createRoiSet(RoiSet value) {
        return new JAXBElement<RoiSet>(_RoiSet_QNAME, RoiSet.class, null, value);
    }

}
