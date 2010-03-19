/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsFieldValue.java,v $
 * Date   : $Date: 2010/03/19 15:31:11 $
 * Version: $Revision: 1.2 $
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

package com.alkacon.opencms.formgenerator;

import java.util.Iterator;

/**
 * Represents a single input field value of a submitted form.<p>
 * 
 * This object is needed to create the output for the optional confirmation page, the notification email
 * or the final page after submission.<p>
 * 
 * @author Andreas Zahner 
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.4 
 */
public class CmsFieldValue {

    /** The label of the field. */
    private String m_label;

    /** A flag indicating if the field is shown. */
    private boolean m_show;

    /** The value of the field. */
    private String m_value;

    /**
     * Constructor that creates an initialized field item.<p>
     * 
     * @param field the form field to create the value from 
     */
    public CmsFieldValue(I_CmsField field) {

        if (field.needsItems()) {
            // check which item has been selected
            StringBuffer fieldValue = new StringBuffer(8);
            Iterator k = field.getItems().iterator();
            boolean isSelected = false;
            while (k.hasNext()) {
                CmsFieldItem currentItem = (CmsFieldItem)k.next();
                if (currentItem.isSelected()) {
                    if (isSelected) {
                        fieldValue.append(", ");
                    }
                    fieldValue.append(currentItem.getLabel());
                    isSelected = true;
                }
            }
            m_value = fieldValue.toString();
        } else {
            // for other field types, append value
            m_value = field.getValue();
        }

        if (CmsHiddenField.class.isAssignableFrom(field.getClass())) {
            // for hidden fields, set show field flag to false
            m_show = false;
        } else {
            // all other fields are shown           
            m_show = true;
        }

        // set the label String of current field
        m_label = field.getLabel();
    }

    /**
     * Returns the label text of the field item.<p>
     * 
     * @return the label text of the field item
     */
    public String getLabel() {

        return m_label;
    }

    /**
     * Returns the value of the field item.<p>
     * 
     * @return the value of the field item
     */
    public String getValue() {

        return m_value;
    }

    /**
     * Returns if the current item is shown or not.<p>
     * 
     * @return true if the current item is shown, otherwise false
     */
    public boolean isShow() {

        return m_show;
    }

    /**
     * Sets the label text of the field item.<p>
     * 
     * @param label the label text of the field item
     */
    protected void setLabel(String label) {

        m_label = label;
    }

    /**
     * Sets if the current item is shown or not.<p>
     * 
     * @param show true if the current item is shown, otherwise false
     */
    protected void setShow(boolean show) {

        m_show = show;
    }

    /**
     * Sets the value of the field item.<p>
     * 
     * @param value the value of the field item
     */
    protected void setValue(String value) {

        m_value = value;
    }
}
