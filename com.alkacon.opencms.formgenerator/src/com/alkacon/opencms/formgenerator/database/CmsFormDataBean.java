/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/database/CmsFormDataBean.java,v $
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
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents a single set / record of data that has been entered by a single 
 * user that filled out a form. <p>
 * 
 * 
 * @author Achim Westermann
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 7.0.4
 *
 */
public class CmsFormDataBean {

    /**
     * The <code>SortedMap&lt;{@link String}, {@link Map.Entry}&gt;</code> with all field entries. 
     */
    private SortedMap m_fieldEntries;

    /**
     * Defcon.<p>
     */
    CmsFormDataBean() {

        m_fieldEntries = new TreeMap();
    }

    /**
     * Adds a new field entry to this data record of the form.<p>
     * 
     * Internal method for <code>{@link I_CmsFormDataAccess}</code>.<p> 
     * 
     * @param field the field to add 
     * 
     * @return the previous contained field with the same field name (<code>{@link Map.Entry#getKey()}</code>) or null
     */
    Map.Entry addEntry(Map.Entry field) {

        return (Map.Entry)this.m_fieldEntries.put(field.getKey(), field);
    }

    /**
     * Returns the entry for the given field name of this form data set or 
     * <code>{@link CmsFormDataEntry#EMPTY}</code> if not present.<p> 
     *  
     * @param fieldName the name of the field to read  
     * 
     * @return the entry for the given field name of this form data set or 
     *      <code>{@link CmsFormDataEntry#EMPTY}</code> if not present
     */
    public Map.Entry getFieldEntry(final String fieldName) {

        Map.Entry result = (Map.Entry)m_fieldEntries.get(fieldName);
        if (result == null) {
            result = CmsFormDataEntry.EMPTY;
        }
        return result;
    }

}
