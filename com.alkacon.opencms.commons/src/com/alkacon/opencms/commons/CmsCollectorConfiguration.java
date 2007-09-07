/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.commons/src/com/alkacon/opencms/commons/CmsCollectorConfiguration.java,v $
 * Date   : $Date: 2007/09/07 12:10:46 $
 * Version: $Revision: 1.1 $
 *
 * Copyright (c) 2005 Alkacon Software GmbH (http://www.alkacon.com)
 * All rights reserved.
 * 
 * This source code is the intellectual property of Alkacon Software GmbH.
 * It is PROPRIETARY and CONFIDENTIAL.
 * Use of this source code is subject to license terms.
 *
 * In order to use this source code, you need written permission from 
 * Alkacon Software GmbH. Redistribution of this source code, in modified 
 * or unmodified form, is not allowed unless written permission by 
 * Alkacon Software GmbH has been given.
 *
 * ALKACON SOFTWARE GMBH MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THIS SOURCE CODE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. ALKACON SOFTWARE GMBH SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOURCE CODE OR ITS DERIVATIVES.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
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
 * @version $Revision: 1.1 $ 
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