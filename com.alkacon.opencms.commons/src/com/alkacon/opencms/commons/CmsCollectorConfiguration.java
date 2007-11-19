/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.commons/src/com/alkacon/opencms/commons/CmsCollectorConfiguration.java,v $
 * Date   : $Date: 2007/11/19 15:49:15 $
 * Version: $Revision: 1.2 $
 *
 * This file is part of the Alkacon OpenCms Add-On Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * The Alkacon OpenCms Add-On Package is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Alkacon OpenCms Add-On Package is distributed 
 * in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Alkacon OpenCms Add-On Package.  
 * If not, see http://www.gnu.org/licenses/.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com.
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org.
 */

package com.alkacon.opencms.commons;

import org.opencms.loader.CmsLoaderException;
import org.opencms.main.OpenCms;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single configuration item for the configurable collector.<p>
 * 
 * A configuration item is represented by the URI to collect resources from, the resource type and optional properties
 * that must be set on the resources to add them to the result.<p>
 * 
 * @author Andreas Zahner
 * @author Michael Moossen
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 6.0.1
 */
public class CmsCollectorConfiguration {

    /** The properties that must be set on the collected resources. */
    private List m_properties;

    /** Flag to indicate if the collector should recursively search the given uri, default is to search recursively. */
    private boolean m_recursive = true;

    /** The resource type to collect resources. */
    private String m_resourceType;

    /** The URI to collect resources from. */
    private String m_uri;

    /**
     * Default constructor.<p>
     * 
     * @param uri the uri to look up the resources from, default is to search recursively
     */
    public CmsCollectorConfiguration(String uri) {

        m_uri = uri;
        m_properties = new ArrayList();
    }

    /**
     * Constructor to create a new collector configuration.<p>
     * 
     * @param uri the uri to look up the resources from, default is to search recursively
     * @param resourceType the required resource type
     * @param properties the list of mandatory properties on the resources
     */
    public CmsCollectorConfiguration(String uri, String resourceType, List properties) {

        this(uri);
        m_resourceType = resourceType;
        setProperties(properties);
    }

    /**
     * Constructor to create a new collector configuration.<p>
     * 
     * @param uri the uri to look up the resources from, default is to search recursively
     * @param resourceType the required resource type
     * @param properties the list of mandatory properties on the resources
     * @param recurse Flag to indicate if the collector should recursively search the given uri
     */
    public CmsCollectorConfiguration(String uri, String resourceType, List properties, boolean recurse) {

        this(uri, resourceType, properties);
        m_recursive = recurse;
    }

    /**
     * Returns the properties that must be set on the collected resources.<p>
     *
     * @return the properties that must be set on the collected resources
     */
    public List getProperties() {

        return m_properties;
    }

    /**
     * Returns the resource type to collect resourcese.<p>
     *
     * @return the resource type to collect resources
     */
    public String getResourceType() {

        return m_resourceType;
    }

    /**
     * Returns the ID of the configured resource type to collect.<p>
     * 
     * @return the ID of the configured resource type to collect
     */
    public int getResourceTypeId() {

        try {
            return OpenCms.getResourceManager().getResourceType(getResourceType()).getTypeId();
        } catch (CmsLoaderException e) {
            // no valid resource type found
            return -1;
        }
    }

    /**
     * Returns the URI to collect resources from.<p>
     *
     * @return the URI to collect resources from
     */
    public String getUri() {

        return m_uri;
    }

    /**
     * Returns the flag to indicate if the collector should recursively search the given uri.<p>
     *
     * @return the flag to indicate if the collector should recursively search the given uri
     */
    public boolean isRecursive() {

        return m_recursive;
    }

    /**
     * Sets the properties that must be set on the collected resources.<p>
     *
     * @param properties the properties that must be set on the collected resources
     */
    public void setProperties(List properties) {

        m_properties.clear();
        if (properties != null) {
            m_properties.addAll(properties);
        }
    }

    /**
     * Sets the flag to indicate if the collector should recursively search the given uri.<p>
     *
     * @param recursive the flag to set
     */
    public void setRecursive(boolean recursive) {

        m_recursive = recursive;
    }

    /**
     * Sets the resource type to collect resources.<p>
     *
     * @param resourceType the resource type to collect resources
     */
    public void setResourceType(String resourceType) {

        m_resourceType = resourceType;
    }

    /**
     * Sets the URI to collect resources from.<p>
     *
     * @param uri the URI to collect resources from
     */
    public void setUri(String uri) {

        m_uri = uri;
    }
}