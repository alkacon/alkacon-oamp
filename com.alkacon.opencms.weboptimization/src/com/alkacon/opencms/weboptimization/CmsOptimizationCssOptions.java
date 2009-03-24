/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.weboptimization/src/com/alkacon/opencms/weboptimization/CmsOptimizationCssOptions.java,v $
 * Date   : $Date: 2009/03/24 12:52:42 $
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

package com.alkacon.opencms.weboptimization;

/**
 * Bean for css optimization options.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.0.6
 */
public class CmsOptimizationCssOptions {

    /** Insert a line break after the specified column number. */
    private int m_lineBreakPos = -1;

    /**
     * Default constructor.<p>
     */
    public CmsOptimizationCssOptions() {

        // empty
    }

    /**
     * Constructor.<p>
     * 
     * @param lineBreakPos Insert a line break after the specified column number
     */
    public CmsOptimizationCssOptions(int lineBreakPos) {

        m_lineBreakPos = lineBreakPos;
    }

    /**
     * Returns the column number to insert a line break after.<p>
     * 
     * @return the column number to insert a line break after
     */
    public int getLineBreakPos() {

        return m_lineBreakPos;
    }

    /**
     * Sets the column number to insert a line break after.<p>
     * 
     * @param lineBreakPos the column number to insert a line break after
     */
    public void setLineBreakPos(int lineBreakPos) {

        m_lineBreakPos = lineBreakPos;
    }
}
