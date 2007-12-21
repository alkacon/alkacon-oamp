/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/database/Attic/CmsFormDataEntry.java,v $
 * Date   : $Date: 2007/12/21 14:34:01 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2007 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * The Alkacon OpenCms Add-On Module Package is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Alkacon OpenCms Add-On Module Package is distributed 
 * in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Alkacon OpenCms Add-On Module Package.  
 * If not, see http://www.gnu.org/licenses/.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com.
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org.
 */
package com.alkacon.opencms.formgenerator.database;

import java.util.Map;

/** 
 * Represents a single key - value pair of form data.<p>
 * 
 * @author Achim Westermann
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4
 *
 */
public class CmsFormDataEntry implements Map.Entry, Comparable {

    /** 
     * Typed constant for an empty form data entry. 
     */
    public static final CmsFormDataEntry EMPTY = new CmsFormDataEntry("", "") {

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public final boolean equals(Object obj) {

            return obj == CmsFormDataEntry.EMPTY;
        }

        /**
         * @see com.alkacon.opencms.formgenerator.database.CmsFormDataEntry#setKey(java.lang.Object)
         */
        public void setKey(Object key) {

            // nop, immutable
        }

        /**
         * @see com.alkacon.opencms.formgenerator.database.CmsFormDataEntry#setValue(java.lang.Object)
         */
        public final Object setValue(Object value) {

            // immutable       
            return this;
        }
    };

    /** The name of the field entry of the form data.<p> */
    private Object m_key;

    /** The value of the field entry of the form data.<p> */
    private Object m_value;

    /**
     * Constructor intended for implementations of <code>{@link I_CmsFormDataAccess}</code>.<p>
     * 
     * @param fieldName the name of the field entry of the form data 
     * 
     * @param fieldValue the value of the field entry of the form data 
     */
    public CmsFormDataEntry(final String fieldName, final String fieldValue) {

        m_key = fieldName;
        m_value = fieldValue;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Object o) {

        CmsFormDataEntry other = (CmsFormDataEntry)o;
        return m_key.toString().compareTo(other.m_key.toString());
    }

    /** 
     * Returns the field name of this field entry of the form data.<p>
     * 
     * @return the field name of this field entry of the form data
     */
    public final Object getKey() {

        return m_key;
    }

    /** 
     * Returns the field value of this field entry of the form data.<p>
     * 
     * @return the field value of this field entry of the form data
     */
    public final Object getValue() {

        return m_value;
    }

    /** 
     * Sets the field name of this field entry of the form data.<p>
     * 
     * Intended for the database access layer (<code>{@link I_CmsFormDataAccess}</code>) only.<p>
     * 
     * @param key  the field name of this field entry of the form data.
     */
    public void setKey(final Object key) {

        m_key = key;
    }

    /** 
     * Sets the field value of this field entry of the form data.<p>
     * 
     * Intended for the database access layer (<code>{@link I_CmsFormDataAccess}</code>) only.<p>
     * 
     * @param value  the field name of this field entry of the form data
     * 
     * @return the previous value that was set
     */
    public Object setValue(final Object value) {

        Object result = m_value;
        m_value = value;
        return result;
    }

}
