/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.formgenerator/src/com/alkacon/opencms/formgenerator/CmsFieldText.java,v $
 * Date   : $Date: 2010/05/21 13:49:18 $
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

package com.alkacon.opencms.formgenerator;

/**
 * Represents an additional text for an input field, with information in which column to show the text.<p>
 * 
 * @author Andreas Zahner
 */
public class CmsFieldText {

    /** The text to show below the input field. */
    private String m_text;

    /** The column where to show the text. */
    private int m_column;

    /** Indicates that the text should be shown in both columns. */
    public static final int COL_BOTH = 0;

    /** Indicates that the text should be shown in the left column. */
    public static final int COL_LEFT = 1;

    /** Indicates that the text should be shown in the right column. */
    public static final int COL_RIGHT = 2;

    /**
     * Constructor, with parameters.<p>
     * 
     * @param text the text to show below the input field
     * @param column the column where to show the text
     */
    public CmsFieldText(String text, int column) {

        m_text = text;
        m_column = column;
        if ((m_column < COL_BOTH) || (m_column > COL_RIGHT)) {
            m_column = COL_BOTH;
        }
    }

    /**
     * Constructor, with parameters.<p>
     * 
     * @param text the text to show below the input field
     * @param column the column where to show the text
     */
    public CmsFieldText(String text, String column) {

        m_text = text;
        try {
            m_column = Integer.parseInt(column);
        } catch (Exception e) {
            // ignore
        }
        if ((m_column < COL_BOTH) || (m_column > COL_RIGHT)) {
            m_column = COL_BOTH;
        }
    }

    /**
     * Returns the column where to show the text.<p>
     * 
     * @return the column where to show the text
     */
    public int getColumn() {

        return m_column;
    }

    /**
     * Returns the text to show below the input field.<p>
     * 
     * @return the text to show below the input field
     */
    public String getText() {

        return m_text;
    }
}
