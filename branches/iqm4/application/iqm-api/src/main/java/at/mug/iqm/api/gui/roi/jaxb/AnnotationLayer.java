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
 * File: AnnotationLayer.java
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element ref="{}color" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}stroke" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}roiSet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="selected" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="visible" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="zOrder" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "color",
    "stroke",
    "roiSet"
})
@XmlRootElement(name = "annotationLayer")
public class AnnotationLayer {

    protected List<Color> color;
    protected List<Stroke> stroke;
    @XmlElement(nillable = true)
    protected List<RoiSet> roiSet;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "selected", required = true)
    protected boolean selected;
    @XmlAttribute(name = "visible", required = true)
    protected boolean visible;
    @XmlAttribute(name = "zOrder", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger zOrder;

    /**
     * Gets the value of the color property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the color property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Color }
     * 
     * 
     */
    public List<Color> getColor() {
        if (color == null) {
            color = new ArrayList<Color>();
        }
        return this.color;
    }

    /**
     * Gets the value of the stroke property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stroke property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStroke().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Stroke }
     * 
     * 
     */
    public List<Stroke> getStroke() {
        if (stroke == null) {
            stroke = new ArrayList<Stroke>();
        }
        return this.stroke;
    }

    /**
     * Gets the value of the roiSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roiSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoiSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RoiSet }
     * 
     * 
     */
    public List<RoiSet> getRoiSet() {
        if (roiSet == null) {
            roiSet = new ArrayList<RoiSet>();
        }
        return this.roiSet;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
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

    /**
     * Gets the value of the selected property.
     * 
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the value of the selected property.
     * 
     */
    public void setSelected(boolean value) {
        this.selected = value;
    }

    /**
     * Gets the value of the visible property.
     * 
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the value of the visible property.
     * 
     */
    public void setVisible(boolean value) {
        this.visible = value;
    }

    /**
     * Gets the value of the zOrder property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getZOrder() {
        return zOrder;
    }

    /**
     * Sets the value of the zOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setZOrder(BigInteger value) {
        this.zOrder = value;
    }

}
