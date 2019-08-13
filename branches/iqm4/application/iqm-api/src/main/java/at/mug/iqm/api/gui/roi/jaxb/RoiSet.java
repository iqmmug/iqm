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
 * File: RoiSet.java
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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{}angleRoi" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}ellipseRoi" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}freehandRoi" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}lineRoi" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}pointRoi" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}polygonRoi" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}rectangleRoi" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="class" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "angleRoi",
    "ellipseRoi",
    "freehandRoi",
    "lineRoi",
    "pointRoi",
    "polygonRoi",
    "rectangleRoi"
})
public class RoiSet {

    protected List<AngleRoi> angleRoi;
    protected List<EllipseRoi> ellipseRoi;
    protected List<FreehandRoi> freehandRoi;
    protected List<LineRoi> lineRoi;
    protected List<PointRoi> pointRoi;
    protected List<PolygonRoi> polygonRoi;
    protected List<RectangleRoi> rectangleRoi;
    @XmlAttribute(name = "class", required = true)
    protected String clazz;

    /**
     * Gets the value of the angleRoi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the angleRoi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAngleRoi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AngleRoi }
     * 
     * 
     */
    public List<AngleRoi> getAngleRoi() {
        if (angleRoi == null) {
            angleRoi = new ArrayList<AngleRoi>();
        }
        return this.angleRoi;
    }

    /**
     * Gets the value of the ellipseRoi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ellipseRoi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEllipseRoi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EllipseRoi }
     * 
     * 
     */
    public List<EllipseRoi> getEllipseRoi() {
        if (ellipseRoi == null) {
            ellipseRoi = new ArrayList<EllipseRoi>();
        }
        return this.ellipseRoi;
    }

    /**
     * Gets the value of the freehandRoi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the freehandRoi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFreehandRoi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FreehandRoi }
     * 
     * 
     */
    public List<FreehandRoi> getFreehandRoi() {
        if (freehandRoi == null) {
            freehandRoi = new ArrayList<FreehandRoi>();
        }
        return this.freehandRoi;
    }

    /**
     * Gets the value of the lineRoi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lineRoi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLineRoi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LineRoi }
     * 
     * 
     */
    public List<LineRoi> getLineRoi() {
        if (lineRoi == null) {
            lineRoi = new ArrayList<LineRoi>();
        }
        return this.lineRoi;
    }

    /**
     * Gets the value of the pointRoi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pointRoi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPointRoi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PointRoi }
     * 
     * 
     */
    public List<PointRoi> getPointRoi() {
        if (pointRoi == null) {
            pointRoi = new ArrayList<PointRoi>();
        }
        return this.pointRoi;
    }

    /**
     * Gets the value of the polygonRoi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the polygonRoi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolygonRoi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PolygonRoi }
     * 
     * 
     */
    public List<PolygonRoi> getPolygonRoi() {
        if (polygonRoi == null) {
            polygonRoi = new ArrayList<PolygonRoi>();
        }
        return this.polygonRoi;
    }

    /**
     * Gets the value of the rectangleRoi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rectangleRoi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRectangleRoi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RectangleRoi }
     * 
     * 
     */
    public List<RectangleRoi> getRectangleRoi() {
        if (rectangleRoi == null) {
            rectangleRoi = new ArrayList<RectangleRoi>();
        }
        return this.rectangleRoi;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

}
