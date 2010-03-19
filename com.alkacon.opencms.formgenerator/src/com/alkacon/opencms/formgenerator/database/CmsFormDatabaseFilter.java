/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/database/CmsFormDatabaseFilter.java,v $
 * Date   : $Date: 2010/03/19 15:31:14 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.formgenerator.database;

import org.opencms.file.CmsResource;
import org.opencms.util.CmsUUID;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A filter to retrieve web form data.<p>
 * 
 * @author Michael Moossen 
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 7.0.5
 */
public final class CmsFormDatabaseFilter implements Cloneable {

    /** Constant to ignore start date. */
    public static final long DATE_IGNORE_FROM = CmsResource.DATE_RELEASED_DEFAULT;

    /** Constant to ignore end date. */
    public static final long DATE_IGNORE_TO = CmsResource.DATE_EXPIRED_DEFAULT;

    /** To filter all forms. */
    public static final CmsFormDatabaseFilter DEFAULT = new CmsFormDatabaseFilter(false);

    /** To filter all forms, as headers. */
    public static final CmsFormDatabaseFilter HEADERS = new CmsFormDatabaseFilter(true);

    /** Constant to ignore index from. */
    public static final int INDEX_IGNORE_FROM = 0;

    /** Constant to ignore index to. */
    public static final int INDEX_IGNORE_TO = Integer.MAX_VALUE;

    /** To filter forms older than the given date. */
    private long m_dateFrom = DATE_IGNORE_FROM;

    /** To filter forms newer than the given date. */
    private long m_dateTo = DATE_IGNORE_TO;

    /** To filter forms for a given entry id. */
    private int m_entryId;

    /** The fields to filter. */
    private Map m_fields = new HashMap();

    /** To filter forms for a given form id. */
    private String m_formId;

    /** To return only form headers, or full forms. */
    private final boolean m_headersOnly;

    /** To filter forms starting from the given index. */
    private int m_indexFrom = INDEX_IGNORE_FROM;

    /** To filter forms ending with the given index. */
    private int m_indexTo = INDEX_IGNORE_TO;

    /** If asc order, desc order otherwise. */
    private boolean m_orderAsc;

    /** To filter forms for a given resource id. */
    private CmsUUID m_resourceId;

    /** The states to filter. */
    private Set m_states = new HashSet();

    /**
     * Private constructor.<p>
     * 
     * @param headersOnly if returning only form headers, or full forms
     */
    private CmsFormDatabaseFilter(boolean headersOnly) {

        m_headersOnly = headersOnly;
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {

        CmsFormDatabaseFilter filter = new CmsFormDatabaseFilter(m_headersOnly);
        filter.m_entryId = m_entryId;
        filter.m_formId = m_formId;
        filter.m_resourceId = m_resourceId;
        filter.m_dateFrom = m_dateFrom;
        filter.m_dateTo = m_dateTo;
        filter.m_indexFrom = m_indexFrom;
        filter.m_indexTo = m_indexTo;
        filter.m_states = new HashSet(m_states);
        filter.m_fields = new HashMap(m_fields);
        filter.m_orderAsc = m_orderAsc;
        return filter;
    }

    /**
     * Returns an extended filter with the given dates restriction.<p>
     * 
     * @param from the start date to filter
     * @param to the end date to filter
     *  
     * @return an extended filter with the given dates restriction
     */
    public CmsFormDatabaseFilter filterDate(long from, long to) {

        CmsFormDatabaseFilter filter = (CmsFormDatabaseFilter)this.clone();
        filter.m_dateFrom = from;
        filter.m_dateTo = to;
        return filter;
    }

    /**
     * Returns an extended filter with the given entry id restriction.<p>
     * 
     * @param entryId the entry id to filter
     *  
     * @return an extended filter with the given entry id restriction
     */
    public CmsFormDatabaseFilter filterEntryId(int entryId) {

        CmsFormDatabaseFilter filter = (CmsFormDatabaseFilter)this.clone();
        filter.m_entryId = entryId;
        return filter;
    }

    /**
     * Returns an extended filter with the given field/value pair restriction.<p>
     * 
     * @param field the field to filter
     * @param value the value to filter
     *  
     * @return an extended filter with the given field/value pair restriction
     */
    public CmsFormDatabaseFilter filterField(String field, String value) {

        CmsFormDatabaseFilter filter = (CmsFormDatabaseFilter)this.clone();
        filter.m_fields.put(field, value);
        return filter;
    }

    /**
     * Returns an extended filter with the given form id restriction.<p>
     * 
     * @param formId the form id to filter
     *  
     * @return an extended filter with the given form id restriction
     */
    public CmsFormDatabaseFilter filterFormId(String formId) {

        CmsFormDatabaseFilter filter = (CmsFormDatabaseFilter)this.clone();
        filter.m_formId = formId;
        return filter;
    }

    /**
     * Returns an extended filter with the given index restriction.<p>
     * 
     * @param from the starting index to filter
     * @param to the ending index to filter
     *  
     * @return an extended filter with the given index restriction
     */
    public CmsFormDatabaseFilter filterIndex(int from, int to) {

        CmsFormDatabaseFilter filter = (CmsFormDatabaseFilter)this.clone();
        filter.m_indexFrom = from;
        filter.m_indexTo = to;
        return filter;
    }

    /**
     * Returns an extended filter with asc ordering.<p>
     * 
     * @return an extended filter with asc ordering
     */
    public CmsFormDatabaseFilter filterOrderAsc() {

        CmsFormDatabaseFilter filter = (CmsFormDatabaseFilter)this.clone();
        filter.m_orderAsc = true;
        return filter;
    }

    /**
     * Returns an extended filter with desc ordering.<p>
     * 
     * @return an extended filter with desc ordering
     */
    public CmsFormDatabaseFilter filterOrderDesc() {

        CmsFormDatabaseFilter filter = (CmsFormDatabaseFilter)this.clone();
        filter.m_orderAsc = false;
        return filter;
    }

    /**
     * Returns an extended filter with the given resource id restriction.<p>
     * 
     * @param resourceId the resource id to filter
     *  
     * @return an extended filter with the given resource id restriction
     */
    public CmsFormDatabaseFilter filterResourceId(CmsUUID resourceId) {

        CmsFormDatabaseFilter filter = (CmsFormDatabaseFilter)this.clone();
        filter.m_resourceId = resourceId;
        return filter;
    }

    /**
     * Returns an extended filter with the given state restriction.<p>
     *
     * @param state the state to filter
     *  
     * @return an extended filter with the given state restriction
     */
    public CmsFormDatabaseFilter filterState(int state) {

        CmsFormDatabaseFilter filter = (CmsFormDatabaseFilter)this.clone();
        filter.m_states.add(new Integer(state));
        return filter;
    }

    /**
     * Returns the end Date.<p>
     *
     * @return the end Date
     */
    public long getDateEnd() {

        return m_dateTo;
    }

    /**
     * Returns the entry Id.<p>
     *
     * @return the entry Id
     */
    public int getEntryId() {

        return m_entryId;
    }

    /**
     * Returns the fields to filter.<p>
     *
     * @return the fields to filter
     */
    public Map getFields() {

        return Collections.unmodifiableMap(m_fields);
    }

    /**
     * Returns the form id restriction.<p>
     *
     * @return the form id restriction
     */
    public String getFormId() {

        return m_formId;
    }

    /**
     * Returns the index From.<p>
     *
     * @return the index From
     */
    public int getIndexFrom() {

        return m_indexFrom;
    }

    /**
     * Returns the index To.<p>
     *
     * @return the index To
     */
    public int getIndexTo() {

        return m_indexTo;
    }

    /**
     * Returns the resource id to filter.<p>
     *
     * @return the resource id to filter
     */
    public CmsUUID getResourceId() {

        return m_resourceId;
    }

    /**
     * Returns the start Date.<p>
     *
     * @return the start Date
     */
    public long getStartDate() {

        return m_dateFrom;
    }

    /**
     * Returns the states to filter.<p>
     *
     * @return the states to filter
     */
    public Set getStates() {

        return Collections.unmodifiableSet(m_states);
    }

    /**
     * Checks if returning only form headers, or full forms.<p>
     *
     * @return <code>true</code> if returning only form headers
     */
    public boolean isHeadersOnly() {

        return m_headersOnly;
    }

    /**
     * Checks if the order is asc.<p>
     *
     * @return <code>true</code> if the order is asc
     */
    public boolean isOrderAsc() {

        return m_orderAsc;
    }

    /**
     * Checks if the given field/value pair matches this filter.<p>
     * 
     * @param field the field to test
     * @param value the value to test
     * 
     * @return if the given field/value pair matches this filter
     */
    public boolean matchField(String field, String value) {

        if (m_fields.isEmpty()) {
            return true;
        }
        if (m_fields.keySet().contains(field)) {
            if (value == null) {
                return true;
            }
            return m_fields.get(field).equals(value);
        }
        return false;
    }

    /**
     * Checks if the given state matches this filter.<p>
     * 
     * @param state the state to test
     * 
     * @return if the given state matches this filter
     */
    public boolean matchState(int state) {

        if (m_states.isEmpty()) {
            return true;
        }
        return m_states.contains(new Integer(state));
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer str = new StringBuffer(128);
        str.append("[");
        str.append("headersOnly").append("=").append(m_headersOnly);
        if (m_entryId != 0) {
            str.append(", ").append("entryId").append("=").append(m_entryId);
        }
        if (m_formId != null) {
            str.append(", ").append("formId").append("=").append(m_formId);
        }
        if (m_resourceId != null) {
            str.append(", ").append("resourceId").append("=").append(m_resourceId);
        }
        if (m_dateFrom != DATE_IGNORE_FROM) {
            str.append(", ").append("startDate").append("=").append(m_dateFrom);
        }
        if (m_dateTo != DATE_IGNORE_TO) {
            str.append(", ").append("endDate").append("=").append(m_dateTo);
        }
        if (m_indexFrom != INDEX_IGNORE_FROM) {
            str.append(", ").append("indexFrom").append("=").append(m_indexFrom);
        }
        if (m_indexTo != INDEX_IGNORE_TO) {
            str.append(", ").append("indexTo").append("=").append(m_indexTo);
        }
        if (!m_states.isEmpty()) {
            str.append(", ").append("states").append("=").append(m_states);
        }
        if (!m_fields.isEmpty()) {
            str.append("fields").append("=").append(m_fields);
        }
        str.append("]");
        return str.toString();
    }
}
