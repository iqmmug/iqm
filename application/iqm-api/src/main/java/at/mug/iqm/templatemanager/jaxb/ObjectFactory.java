package at.mug.iqm.templatemanager.jaxb;

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


import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the opgui.preferences.jaixml package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes.
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IqmOperatorDescriptor.Parameters }
     * 
     */
    public IqmOperatorDescriptor.Parameters createIqmOperatorDescriptorParameters() {
        return new IqmOperatorDescriptor.Parameters();
    }

    /**
     * Create an instance of {@link IqmOperatorTemplates.ParameterBlock }
     * 
     */
    public IqmOperatorTemplates.ParameterBlock createIqmOperatorTemplatesParameterBlock() {
        return new IqmOperatorTemplates.ParameterBlock();
    }

    /**
     * Create an instance of {@link IqmOperatorTemplates }
     * 
     */
    public IqmOperatorTemplates createIqmOperatorTemplates() {
        return new IqmOperatorTemplates();
    }

    /**
     * Create an instance of {@link IqmOperatorDescriptor }
     * 
     */
    public IqmOperatorDescriptor createIqmOperatorDescriptor() {
        return new IqmOperatorDescriptor();
    }

}
