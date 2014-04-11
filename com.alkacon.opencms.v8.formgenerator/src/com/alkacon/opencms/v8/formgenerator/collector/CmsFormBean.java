/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.v8.formgenerator.collector;

import com.alkacon.opencms.v8.formgenerator.database.CmsFormDataBean;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.staticexport.CmsLinkManager;

import java.util.Map;

/**
 * Similar to {@link com.alkacon.opencms.v8.formgenerator.database.CmsFormDataBean},
 * but with direct functionality to get information about the pages, where the form was filled out from.
 * 
 */
public class CmsFormBean {

    /** The form entry to access */
    protected final CmsFormDataBean m_form;
    /** Cms object, used to extract resource properties etc. */
    protected CmsObject m_cms;
    /** The resource of the page where the form was filled out. */
    protected CmsResource m_resource = null;

    /**
     * Create a CmsFormBean to access the given form data that is persisted in the database
     * 
     * @param cms Appropriately initialized CmsObject
     * @param form The database entry for the form data that should be accessed
     */
    public CmsFormBean(CmsObject cms, CmsFormDataBean form) {

        m_cms = cms;
        m_form = form;
        if ((m_cms != null) && (m_form != null)) {
            try {
                m_resource = m_cms.readResource(m_form.getResourceId());
            } catch (CmsException e) {
                // Nothing to do, m_resource remains null
            }
        }
    }

    /**
     * @return title of the page where the form was filled out
     */
    public String getTitle() {

        if (m_resource == null) {
            return "";
        }
        CmsProperty titleProp;
        try {
            titleProp = m_cms.readPropertyObject(m_resource, "Title", false);
        } catch (CmsException e) {
            return "";
        }
        return titleProp.getValue("");

    }

    /**
     * @return the uri of the page where the form was filled out (already adjusted with the link manager)
     */
    public String getUri() {

        if ((m_cms == null) || (m_resource == null)) {
            return null;
        }
        CmsLinkManager linkManager = OpenCms.getLinkManager();
        return linkManager.substituteLink(m_cms, m_resource);
    }

    /**
     * Returns a map, where the key is the field name and 
     * the value the field value, with all fields in the form.<p>
     * 
     * For better EL usage.<p>
     * 
     * @return all fields in the form
     * 
     */
    public Map<String, String> getFields() {

        return m_form.getAllFields();
    }

    /**
     * Get the date when the form was filled out.
     * 
     * @return date when the form was filled out
     */
    public Long getDateCreated() {

        return Long.valueOf(m_form.getDateCreated());
    }
}
