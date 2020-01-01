package at.mug.iqm.templatemanager.jaxb;

/*
 * #%L
 * Project: IQM - API
 * File: IqmOperatorDescriptor.java
 * 
 * $Id$
 * $HeadURL$
 * 
 * This file is part of IQM, hereinafter referred to as "this program".
 * %%
 * Copyright (C) 2009 - 2020 Helmut Ahammer, Philipp Kainz
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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for iqmOperatorDescriptor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="iqmOperatorDescriptor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parameters" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="parametername" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="parameterclass" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="parametervalue" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="templatename" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "iqmOperatorDescriptor", propOrder = {
    "parameters"
})
public class IqmOperatorDescriptor {

    @XmlElement(required = true)
    protected List<IqmOperatorDescriptor.Parameters> parameters;
    @XmlAttribute(name = "templatename")
    protected String templatename;

    /**
     * Gets the value of the parameters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IqmOperatorDescriptor.Parameters }
     * 
     * 
     */
    public List<IqmOperatorDescriptor.Parameters> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<IqmOperatorDescriptor.Parameters>();
        }
        return this.parameters;
    }

    /**
     * Gets the value of the templatename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemplatename() {
        return templatename;
    }

    /**
     * Sets the value of the templatename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemplatename(String value) {
        this.templatename = value;
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
     *       &lt;attribute name="parametername" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="parameterclass" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="parametervalue" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Parameters {

        @XmlAttribute(name = "parametername", required = true)
        protected String parametername;
        @XmlAttribute(name = "parameterclass", required = true)
        protected String parameterclass;
        @XmlAttribute(name = "parametervalue", required = true)
        protected String parametervalue;

        /**
         * Gets the value of the parametername property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getParametername() {
            return parametername;
        }

        /**
         * Sets the value of the parametername property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setParametername(String value) {
            this.parametername = value;
        }

        /**
         * Gets the value of the parameterclass property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getParameterclass() {
            return parameterclass;
        }

        /**
         * Sets the value of the parameterclass property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setParameterclass(String value) {
            this.parameterclass = value;
        }

        /**
         * Gets the value of the parametervalue property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getParametervalue() {
            return parametervalue;
        }

        /**
         * Sets the value of the parametervalue property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setParametervalue(String value) {
            this.parametervalue = value;
        }

    }

}
