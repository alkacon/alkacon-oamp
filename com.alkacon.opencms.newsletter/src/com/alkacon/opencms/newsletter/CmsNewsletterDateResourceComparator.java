/*
 * File   : $Source: /home/cvs/EbkModules/de.erzbistumkoeln.newsletter/src/de/erzbistumkoeln/newsletter/CmsNewsletterDateResourceComparator.java,v $
 * Date   : $Date: 2010-10-14 12:44:53 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2009 Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.newsletter;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.file.collectors.CmsDateResourceComparator;
import org.opencms.main.CmsException;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comparator for sorting newsletter resource objects based on dates.<p>
 * 
 * Serves as {@link java.util.Comparator} for resources and as comparator key for the resource
 * at the same time. Uses lazy initializing of comparator keys for a resource.<p>
 * 
 * @author Andreas Zahner 
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.9.2 
 */
public class CmsNewsletterDateResourceComparator implements Comparator<CmsResource> {

    /** The date sort order. */
    private boolean m_asc;

    /** The current OpenCms user context. */
    private CmsObject m_cms;

    /** The date of this comparator key. */
    private long m_date;

    /** The internal map of comparator keys. */
    private Map<CmsUUID, CmsNewsletterDateResourceComparator> m_keys;

    /**
     * Creates a new instance of this comparator key.<p>
     * 
     * @param cms the current OpenCms user context
     * @param asc if true, the date sort order is ascending, otherwise descending
     */
    public CmsNewsletterDateResourceComparator(CmsObject cms, boolean asc) {

        m_cms = cms;
        m_asc = asc;
        m_keys = new HashMap<CmsUUID, CmsNewsletterDateResourceComparator>();
    }

    /**
     * Creates a new, empty instance of this comparator key, used for the calculated map valued.<p>
     */
    private CmsNewsletterDateResourceComparator() {

        // NOOP
    }

    /**
     * Calculates the date to use for comparison of this resource based on the given date identifiers.<p>
     * 
     * @param cms the current OpenCms user context
     * @param resource the resource to create the key for
     * @param defaultValue the default value to use in case no value can be calculated
     * 
     * @return the calculated date
     * 
     * @see CmsDateResourceComparator for a description about how the date identifieres are used
     */
    public static long calculateDate(CmsObject cms, CmsResource resource, long defaultValue) {

        long result = 0;

        String propValue = "";
        try {
            propValue = cms.readPropertyObject(resource, CmsNewsletterManager.PROPERTY_NEWSLETTER_DATA, false).getValue();
        } catch (CmsException e) {
            // failed to read the property, ignore
        }
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(propValue)) {
            // there is a value set for the property
            List<String> valueParts = CmsStringUtil.splitAsList(propValue, CmsProperty.VALUE_LIST_DELIMITER, true);
            try {
                result = Long.parseLong(valueParts.get(0));
                resource.setDateReleased(result);
            } catch (NumberFormatException e) {
                // ignore, the default value is used
            }
        }

        if (result == 0) {
            // if nothing else was found, use default
            result = defaultValue;
        }
        return result;
    }

    /**
     * Creates a new instance of this comparator key.<p>
     * 
     * @param cms the current OpenCms user context
     * @param resource the resource to create the key for
     * 
     * @return a new instance of this comparator key
     */
    private static CmsNewsletterDateResourceComparator create(CmsObject cms, CmsResource resource) {

        CmsNewsletterDateResourceComparator result = new CmsNewsletterDateResourceComparator();
        result.m_date = calculateDate(cms, resource, resource.getDateLastModified());
        return result;
    }

    /**
     * Compares {@link CmsResource} objects based on the date
     * found in the {@link CmsNewsletterManager#PROPERTY_NEWSLETTER_DATA} property.<p>
     * 
     * @param res0 the first resource to be compared
     * @param res1 the second resource to be compared
     * 
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(CmsResource res0, CmsResource res1) {

        if (res0 == res1) {
            return 0;
        }

        CmsNewsletterDateResourceComparator key0 = m_keys.get(res0.getStructureId());
        CmsNewsletterDateResourceComparator key1 = m_keys.get(res1.getStructureId());

        if (key0 == null) {
            // initialize key if null
            key0 = CmsNewsletterDateResourceComparator.create(m_cms, res0);
            m_keys.put(res0.getStructureId(), key0);
        }
        if (key1 == null) {
            // initialize key if null
            key1 = CmsNewsletterDateResourceComparator.create(m_cms, res1);
            m_keys.put(res1.getStructureId(), key1);
        }

        if (m_asc) {
            // sort in ascending order
            if (key0.m_date > key1.m_date) {
                return 1;
            }
            if (key0.m_date < key1.m_date) {
                return -1;
            }
        } else {
            // sort in descending order
            if (key0.m_date > key1.m_date) {
                return -1;
            }
            if (key0.m_date < key1.m_date) {
                return 1;
            }
        }

        return 0;
    }

    /**
     * Returns the date of this resource comparator key.<p>
     * 
     * @return the date of this resource comparator key
     */
    public long getDate() {

        return m_date;
    }
}