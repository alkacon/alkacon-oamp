/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsFieldItem.java,v $
 * Date   : $Date: 2008/01/17 15:24:55 $
 * Version: $Revision: 1.2 $
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

package com.alkacon.opencms.formgenerator;

/**
 * Represents a single input field item object.<p>
 * 
 * This object is needed to create checkboxes, radio buttons and selectboxes
 * and represents an item for these types.<p>
 * 
 * @author Andreas Zahner 
 * 
 * @version $Revision: 1.2 $
 * 
 * @since 7.0.4 
 */
public class CmsFieldItem {

    /** A flag indicating if the item is selected. */
    private boolean m_isSelected;

    /** The label of the item. */
    private String m_label;

    /** The flag indicating if the items should be shown in one row. */
    private boolean m_showInRow;

    /** The value of the item. */
    private String m_value;

    /**
     * Empty constructor creates an empty field item.<p>
     */
    public CmsFieldItem() {

        m_label = "";
        m_value = "";
    }

    /**
     * Constructor that creates an initialized field item.<p>
     * 
     * @param value the value of the field item
     * @param label the label of the field item
     * @param isSelected true if the current item is selected, otherwise false
     * @param showInRow true if the items should be shown in a row, otherwise false
     */
    public CmsFieldItem(String value, String label, boolean isSelected, boolean showInRow) {

        m_label = label;
        m_isSelected = isSelected;
        m_value = value;
        m_showInRow = showInRow;
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
     * Returns if the current item is selected or not.<p>
     * 
     * @return true if the current item is selected, otherwise false
     */
    public boolean isSelected() {

        return m_isSelected;
    }

    /**
     * Returns if the items should be shown in a row.<p>
     * 
     * @return true if the items should be shown in a row, otherwise false
     */
    public boolean isShowInRow() {

        return m_showInRow;
    }

    /**
     * Sets if the items should be shown in a row.<p>
     * 
     * @param showInRow the flag indicating if the items should be shown in a row
     */
    public void setShowInRow(boolean showInRow) {

        m_showInRow = showInRow;
    }

    /**
     * Sets the label text of the field item.<p>
     * 
     * @param label the description text of the field item
     */
    protected void setLabel(String label) {

        m_label = label;
    }

    /**
     * Sets if the current item is selected or not.<p>
     * 
     * @param isSelected true if the current item is selected, otherwise false
     */
    protected void setSelected(boolean isSelected) {

        m_isSelected = isSelected;
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
