/*
 * File   : $Source: /usr/local/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsFormReportColumn.java,v $
 * Date   : $Date: 2010-07-02 10:45:52 $
 * Version: $Revision: 1.1 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
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

package com.alkacon.opencms.v8.formgenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a form report column with all necessary data.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsFormReportColumn {

    /** The field database label of the column.  */
    private String m_columnDbLabel;

    /** The column ID, generated from the field database label. */
    private String m_columnId;

    /** The field label of the column. */
    private String m_columnLabel;

    /** The field type of the column. */
    private String m_columnType;

    /** The list of items of the column, if field type is table. */
    private List<CmsFieldItem> m_columnItems;

    /**
     * Returns a list of configured form report columns from the given form fields.<p>
     * 
     * @param fields the fields to create columns from
     * @return the configured form report columns
     */
    public static List<CmsFormReportColumn> getColumnsFromFields(List<I_CmsField> fields) {

        List<CmsFormReportColumn> result = new ArrayList<CmsFormReportColumn>(fields.size());
        for (Iterator<I_CmsField> i = fields.iterator(); i.hasNext();) {
            result.add(new CmsFormReportColumn(i.next()));
        }
        return result;
    }

    /**
     * Constructor, creates a configured form report column.<p>
     * 
     * @param field the field to generate the column from
     */
    public CmsFormReportColumn(I_CmsField field) {

        m_columnDbLabel = field.getDbLabel();
        m_columnLabel = field.getLabel();
        m_columnId = String.valueOf(m_columnDbLabel.hashCode());
        m_columnType = field.getType();
        m_columnItems = field.getItems();
    }

    /**
     * Returns if the column IDs of the compared field columns are the same.<p>
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if ((obj == null) || !(obj instanceof CmsFormReportColumn)) {
            return false;
        }
        return getColumnId().equals(((CmsFormReportColumn)obj).getColumnId());
    }

    /**
     * Returns the field database label of the column.<p>
     * 
     * @return the field database label of the column
     */
    public String getColumnDbLabel() {

        return m_columnDbLabel;
    }

    /**
     * Returns the column ID, generated from the field database label.<p>
     * 
     * This ID is necessary to avoid grid display errors in case the field labels contains characters
     * that are not allowed in HTML id attributes.<p>
     * 
     * @return the column ID, generated from the field database label
     */
    public String getColumnId() {

        return m_columnId;
    }

    /**
     * Returns the field label of the column.<p>
     * 
     * @return the field label of the column
     */
    public String getColumnLabel() {

        return m_columnLabel;
    }

    /**
     * Returns the field type of the column.<p>
     * 
     * @return the field type of the column
     */
    public String getColumnType() {

        return m_columnType;
    }

    /**
     * Returns the list of items of the column, if field type is table.<p>
     * 
     * @return the list of items of the column, if field type is table
     */
    public List<CmsFieldItem> getColumnItems() {

        return m_columnItems;
    }

    /**
     * Returns the hash code generated from the column ID.<p>
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return getColumnDbLabel().hashCode();
    }
}
