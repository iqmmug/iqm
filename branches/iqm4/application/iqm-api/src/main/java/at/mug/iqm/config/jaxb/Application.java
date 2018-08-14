//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.29 at 10:29:42 PM CEST 
//


package at.mug.iqm.config.jaxb;

/*
 * #%L
 * Project: IQM - API
 * File: Application.java
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element ref="{}paths"/>
 *         &lt;element ref="{}cleanerTask"/>
 *         &lt;element ref="{}memoryMonitor"/>
 *         &lt;element ref="{}gui" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "paths",
    "cleanerTask",
    "memoryMonitor",
    "gui"
})
@XmlRootElement(name = "application")
public class Application {

    @XmlElement(required = true)
    protected Paths paths;
    @XmlElement(required = true)
    protected CleanerTask cleanerTask;
    @XmlElement(required = true)
    protected MemoryMonitor memoryMonitor;
    protected Gui gui;

    /**
     * Gets the value of the paths property.
     * 
     * @return
     *     possible object is
     *     {@link Paths }
     *     
     */
    public Paths getPaths() {
        return paths;
    }

    /**
     * Sets the value of the paths property.
     * 
     * @param value
     *     allowed object is
     *     {@link Paths }
     *     
     */
    public void setPaths(Paths value) {
        this.paths = value;
    }

    /**
     * Gets the value of the cleanerTask property.
     * 
     * @return
     *     possible object is
     *     {@link CleanerTask }
     *     
     */
    public CleanerTask getCleanerTask() {
        return cleanerTask;
    }

    /**
     * Sets the value of the cleanerTask property.
     * 
     * @param value
     *     allowed object is
     *     {@link CleanerTask }
     *     
     */
    public void setCleanerTask(CleanerTask value) {
        this.cleanerTask = value;
    }

    /**
     * Gets the value of the memoryMonitor property.
     * 
     * @return
     *     possible object is
     *     {@link MemoryMonitor }
     *     
     */
    public MemoryMonitor getMemoryMonitor() {
        return memoryMonitor;
    }

    /**
     * Sets the value of the memoryMonitor property.
     * 
     * @param value
     *     allowed object is
     *     {@link MemoryMonitor }
     *     
     */
    public void setMemoryMonitor(MemoryMonitor value) {
        this.memoryMonitor = value;
    }

    /**
     * Gets the value of the gui property.
     * 
     * @return
     *     possible object is
     *     {@link Gui }
     *     
     */
    public Gui getGui() {
        return gui;
    }

    /**
     * Sets the value of the gui property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gui }
     *     
     */
    public void setGui(Gui value) {
        this.gui = value;
    }

}
