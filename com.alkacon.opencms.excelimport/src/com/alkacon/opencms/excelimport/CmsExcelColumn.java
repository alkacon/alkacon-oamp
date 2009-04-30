
package com.alkacon.opencms.excelimport;

import java.util.HashMap;

/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.excelimport/src/com/alkacon/opencms/excelimport/CmsExcelColumn.java,v $
 * Date   : $Date: 2009/04/30 10:52:08 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
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

/**
 * Includes all cells from all rows from one column from an excel file. 
 * Here is saved the excel column name set by user and the name from excel internal.<p>
 * 
 * On this column can become accessed.<p> 
 * 
 * @author Mario Jaeger
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.5.0
 */
public class CmsExcelColumn {

    /** The fields of all rows in this column. */
    private HashMap m_columnMap = new HashMap();

    /** The excel column name. */
    private int m_excelColumnName;

    /** The user column name. */
    private String m_userColumnName;

    /** The constructor.
     * 
     * @param userColumnName name from column given by user
     * @param excelColumnName name from column by excel itself
     */
    public CmsExcelColumn(String userColumnName, int excelColumnName) {

        m_userColumnName = userColumnName;
        m_excelColumnName = excelColumnName;
    }

    /**
     * Add new cell value in given row.<p>
     * 
     * @param cellRow number of row with new value
     * @param value new value
     */
    public void addNewCellValue(int cellRow, String value) {

        m_columnMap.put(new Integer(cellRow), value);
    }

    /**
     * Gets the value from a cell. <p>
     * 
     * @param cellRow number of row to get value from
     * 
     * @return value from cell in row
     */
    public String getCellStringValue(int cellRow) {

        String value = "";
        if (m_columnMap.containsKey(new Integer(cellRow))) {
            value = (String)m_columnMap.get(new Integer(cellRow));
        }
        return value;
    }

    /**
     * Gets column name from excel internal.<p>
     * 
     * @return excel column name set from excel internal
     */
    public int getExcelColumnName() {

        int excelRowName = 0;
        if (m_userColumnName != null) {
            excelRowName = m_excelColumnName;
        }
        return excelRowName;
    }

    /**
     * Gets column name set by user.<p>
     * 
     * @return user column name set by user
     */
    public String getUserColumnName() {

        String userRowName = "";
        if (m_userColumnName != null) {
            userRowName = m_userColumnName;
        }
        return userRowName;
    }
}
