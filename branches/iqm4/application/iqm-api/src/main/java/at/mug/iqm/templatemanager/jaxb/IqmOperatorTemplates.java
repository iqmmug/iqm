package at.mug.iqm.templatemanager.jaxb;

/*
 * #%L
 * Project: IQM - API
 * File: IqmOperatorTemplates.java
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
 *         &lt;element name="parameterBlock" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="template" type="{http://xml.netbeans.org/schema/iqmOperatorDescriptor}iqmOperatorDescriptor" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="blockname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "parameterBlock"
})
@XmlRootElement(name = "iqmOperatorTemplates")
public class IqmOperatorTemplates {

    protected List<IqmOperatorTemplates.ParameterBlock> parameterBlock;

    /**
     * Gets the value of the parameterBlock property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameterBlock property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameterBlock().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IqmOperatorTemplates.ParameterBlock }
     * 
     * 
     */
    public List<IqmOperatorTemplates.ParameterBlock> getParameterBlock() {
        if (parameterBlock == null) {
            parameterBlock = new ArrayList<IqmOperatorTemplates.ParameterBlock>();
        }
        return this.parameterBlock;
    }


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
     *         &lt;element name="template" type="{http://xml.netbeans.org/schema/jaipreferences}iqmOperatorDescriptor" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="blockname" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "template"
    })
    public static class ParameterBlock {

        @XmlElement(required = true)
        protected List<IqmOperatorDescriptor> template;
        @XmlAttribute(name = "blockname")
        protected String blockname;

        /**
         * Gets the value of the template property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the template property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTemplate().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link IqmOperatorDescriptor }
         * 
         * 
         */
        public List<IqmOperatorDescriptor> getTemplate() {
            if (template == null) {
                template = new ArrayList<IqmOperatorDescriptor>();
            }
            return this.template;
        }

        /**
         * Gets the value of the blockname property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBlockname() {
            return blockname;
        }

        /**
         * Sets the value of the blockname property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBlockname(String value) {
            this.blockname = value;
        }

    }

}
