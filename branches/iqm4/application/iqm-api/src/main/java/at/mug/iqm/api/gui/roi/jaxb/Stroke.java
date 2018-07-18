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
 * File: Stroke.java
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *       &lt;sequence>
 *         &lt;element ref="{}lineWidth"/>
 *         &lt;element ref="{}endCap" minOccurs="0"/>
 *         &lt;element ref="{}lineJoin" minOccurs="0"/>
 *         &lt;element ref="{}miterLimit" minOccurs="0"/>
 *         &lt;element ref="{}dashArray" minOccurs="0"/>
 *         &lt;element ref="{}dashPhase" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="class" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "lineWidth",
    "endCap",
    "lineJoin",
    "miterLimit",
    "dashArray",
    "dashPhase"
})
@XmlRootElement(name = "stroke")
public class Stroke {

    @XmlElement(required = true)
    protected LineWidth lineWidth;
    @XmlElement(nillable = true)
    protected EndCap endCap;
    @XmlElement(nillable = true)
    protected LineJoin lineJoin;
    @XmlElement(nillable = true)
    protected MiterLimit miterLimit;
    @XmlElement(nillable = true)
    protected DashArray dashArray;
    protected DashPhase dashPhase;
    @XmlAttribute(name = "class", required = true)
    protected String clazz;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the lineWidth property.
     * 
     * @return
     *     possible object is
     *     {@link LineWidth }
     *     
     */
    public LineWidth getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets the value of the lineWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineWidth }
     *     
     */
    public void setLineWidth(LineWidth value) {
        this.lineWidth = value;
    }

    /**
     * Gets the value of the endCap property.
     * 
     * @return
     *     possible object is
     *     {@link EndCap }
     *     
     */
    public EndCap getEndCap() {
        return endCap;
    }

    /**
     * Sets the value of the endCap property.
     * 
     * @param value
     *     allowed object is
     *     {@link EndCap }
     *     
     */
    public void setEndCap(EndCap value) {
        this.endCap = value;
    }

    /**
     * Gets the value of the lineJoin property.
     * 
     * @return
     *     possible object is
     *     {@link LineJoin }
     *     
     */
    public LineJoin getLineJoin() {
        return lineJoin;
    }

    /**
     * Sets the value of the lineJoin property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineJoin }
     *     
     */
    public void setLineJoin(LineJoin value) {
        this.lineJoin = value;
    }

    /**
     * Gets the value of the miterLimit property.
     * 
     * @return
     *     possible object is
     *     {@link MiterLimit }
     *     
     */
    public MiterLimit getMiterLimit() {
        return miterLimit;
    }

    /**
     * Sets the value of the miterLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link MiterLimit }
     *     
     */
    public void setMiterLimit(MiterLimit value) {
        this.miterLimit = value;
    }

    /**
     * Gets the value of the dashArray property.
     * 
     * @return
     *     possible object is
     *     {@link DashArray }
     *     
     */
    public DashArray getDashArray() {
        return dashArray;
    }

    /**
     * Sets the value of the dashArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link DashArray }
     *     
     */
    public void setDashArray(DashArray value) {
        this.dashArray = value;
    }

    /**
     * Gets the value of the dashPhase property.
     * 
     * @return
     *     possible object is
     *     {@link DashPhase }
     *     
     */
    public DashPhase getDashPhase() {
        return dashPhase;
    }

    /**
     * Sets the value of the dashPhase property.
     * 
     * @param value
     *     allowed object is
     *     {@link DashPhase }
     *     
     */
    public void setDashPhase(DashPhase value) {
        this.dashPhase = value;
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

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
