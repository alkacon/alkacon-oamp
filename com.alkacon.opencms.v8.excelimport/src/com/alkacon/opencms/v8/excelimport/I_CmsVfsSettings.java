/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.v8.excelimport/src/com/alkacon/opencms/v8/excelimport/I_CmsVfsSettings.java,v $
 * Date   : $Date: 2010/09/07 11:03:14 $
 * Version: $Revision: 1.2 $
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

package com.alkacon.opencms.v8.excelimport;

import org.opencms.file.CmsObject;

/**
 * Interface to get parameters for path to XML contents and file names 
 * from new XML contents. Class which implements this interface has to 
 * implement two methods. One for setting path to XML contents and one
 * for setting new file name. Both methods have parameters with current
 * CmsObject, the current selected path in the workplace and the
 * selected resource type. <p>
 * 
 * @author Mario Jaeger
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 7.5.0
 */
public interface I_CmsVfsSettings {

    /**
     * Returns the file name for a new XML content.<p>
     * 
     * @param cmsObject current CmsObject
     * @param workplacePath current path in workplace
     * @param resourceType selected resource type
     * @param cmsExcelContent the excel content
     * 
     * @return file name for a new XML content
     */
    String getNewFileName(
        CmsObject cmsObject,
        String workplacePath,
        String resourceType,
        CmsExcelContent cmsExcelContent);

    /**
     * Returns the path to this location where XML contents shall become new created and updated.<p>
     * 
     * @param cmsObject current CmsObject
     * @param workplacePath current path in workplace
     * @param resourceType selected resource type
     * @param cmsExcelContent the excel content
     * 
     * @return path to XML contents
     */
    String getPathToXmlContents(
        CmsObject cmsObject,
        String workplacePath,
        String resourceType,
        CmsExcelContent cmsExcelContent);
}
