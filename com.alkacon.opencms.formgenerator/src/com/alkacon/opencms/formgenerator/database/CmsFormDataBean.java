/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/database/CmsFormDataBean.java,v $
 * Date   : $Date: 2008/05/21 11:54:29 $
 * Version: $Revision: 1.6 $
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

import org.opencms.util.CmsUUID;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents a single set / record of data that has been entered by a single 
 * user that filled out a form. <p>
 * 
 * @author Achim Westermann
 * @author Michael Moossen
 * 
 * @version $Revision: 1.6 $
 * 
 * @since 7.0.4
 */
public class CmsFormDataBean {

    /** The creation date. */
    private long m_dateCreated;

    /** The entry id. */
    private int m_entryId;

    /** The <code>SortedMap&lt;{@link String}, {@link java.util.Map.Entry}&gt;</code> with all field entries. */
    private SortedMap m_fieldEntries;

    /** the name of the form. **/
    private String m_formId;

    /** The resource id. */
    private CmsUUID m_resourceId;

    /** The state. */
    private int m_state;

    /**
     * Default constructor.<p>
     */
    protected CmsFormDataBean() {

        m_fieldEntries = new TreeMap();
    }

    /**
     * Returns a set of all field labels in this form.<p>
     * 
     * @return a set of all field labels
     * 
     * @see java.util.Map#keySet()
     */
    public Set getAllFieldLabels() {

        return m_fieldEntries.keySet();
    }

    /**
     * Returns a map, where the key is the field name and 
     * the value the field value, with all fields in the form.<p>
     * 
     * @return all fields in the form
     */
    public Map getAllFields() {

        return Collections.unmodifiableMap(m_fieldEntries);
    }

    /**
     * Returns a collection of all field values in this form.<p>
     * 
     * @return a collection of all field values
     * 
     * @see java.util.Map#values()
     */
    public Collection getAllFieldValues() {

        return m_fieldEntries.values();
    }

    /**
     * Returns the creation date.<p>
     *
     * @return the creation date
     */
    public long getDateCreated() {

        return m_dateCreated;
    }

    /**
     * Returns the entry id.<p>
     * 
     * @return the entry id
     */
    public int getEntryId() {

        return m_entryId;
    }

    /**
     * Returns a map, where the key is the field name and 
     * the value the field value, with all fields in the form.<p>
     * 
     * For better EL usage.<p>
     * 
     * @return all fields in the form
     * 
     * @see #getAllFields
     */
    public Map getField() {

        return getAllFields();
    }

    /**
     * Returns the value of the given field, or <code>null</code> 
     * if no field with the given name exists.<p>
     * 
     * @param fieldLabel the field label to get the value for
     * 
     * @return the value of the given field
     * 
     * @see java.util.Map#get(java.lang.Object)
     */
    public String getFieldValue(String fieldLabel) {

        return (String)m_fieldEntries.get(fieldLabel);
    }

    /**
     * Returns the form Id.<p>
     *
     * @return the form Id
     */
    public String getFormId() {

        return m_formId;
    }

    /**
     * Returns the resource id.<p>
     *
     * @return the resource id
     */
    public CmsUUID getResourceId() {

        return m_resourceId;
    }

    /**
     * Returns the state.<p>
     *
     * @return the state
     */
    public int getState() {

        return m_state;
    }

    /**
     * Checks if the form has the given field.<p>
     * 
     * @param fieldLabel the field label to look for
     * 
     * @return <code>true</code> if the form has the given field
     *  
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean hasField(String fieldLabel) {

        return m_fieldEntries.containsKey(fieldLabel);
    }

    /**
     * Checks if the form has the given field value.<p>
     * 
     * @param fieldValue the field value to look for
     * 
     * @return <code>true</code> if the form has the given field value
     *  
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean hasValue(String fieldValue) {

        return m_fieldEntries.containsValue(fieldValue);
    }

    /**
     * Returns the total number of fields in this form.<p>
     * 
     * @return the total number of fields
     * 
     * @see java.util.Map#size()
     */
    public int size() {

        return m_fieldEntries.size();
    }

    /**
     * Adds a new field to this form.<p>
     * 
     * @param fieldLabel the label of the field
     * @param fieldValue the value of the field
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    protected void addField(String fieldLabel, String fieldValue) {

        m_fieldEntries.put(fieldLabel, fieldValue);
    }

    /**
     * Sets the creation date.<p>
     *
     * @param dateCreated the creation date to set
     */
    protected void setDateCreated(long dateCreated) {

        m_dateCreated = dateCreated;
    }

    /**
     * Sets the entry Id.<p>
     *
     * @param entryId the entry Id to set
     */
    protected void setEntryId(int entryId) {

        m_entryId = entryId;
    }

    /**
     * Sets the form Id.<p>
     *
     * @param formId the form Id to set
     */
    protected void setFormId(String formId) {

        m_formId = formId;
    }

    /**
     * Sets the resource id.<p>
     *
     * @param resourceId the resource id to set
     */
    protected void setResourceId(CmsUUID resourceId) {

        m_resourceId = resourceId;
    }

    /**
     * Sets the state.<p>
     *
     * @param state the state to set
     */
    protected void setState(int state) {

        m_state = state;
    }
}
