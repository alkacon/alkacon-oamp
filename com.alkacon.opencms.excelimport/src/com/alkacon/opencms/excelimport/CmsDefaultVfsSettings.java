/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.excelimport/src/com/alkacon/opencms/excelimport/CmsDefaultVfsSettings.java,v $
 * Date   : $Date: 2009/04/30 10:52:07 $
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

package com.alkacon.opencms.excelimport;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.util.PrintfFormat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

/**
 * Class to get import path and file names for XML contents. Class implements 
 * interface I_CmsVfsSettings. In this class are set the default settings for 
 * path to XML contents an for file names from new XML contents.
 * Default path to XML contents is the current selected path in the workplace.
 * Default for file name for new XML content is constructed from resource type 
 * and a number of 4 digits.<p>
 * 
 * @author Mario Jaeger
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 7.5.0
 */
public class CmsDefaultVfsSettings implements I_CmsVfsSettings {

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsDefaultVfsSettings.class);

    /** Format for file create parameter. */
    private static final PrintfFormat NUMBER_FORMAT = new PrintfFormat("%0.4d");

    /**
     * Returns file name for new XML content. The format from name is constructed with name from 
     * OpenCms resource type and a number of 4 digits.<p>
     * 
     * @param cms current CmsObject
     * @param workplacePath current path in workplace
     * @param resourceType selected resource type
     * 
     * @return file name for new XML content
     */
    public String getNewFileName(CmsObject cms, String workplacePath, String resourceType) {

        String newFileName = "";
        String foldername = workplacePath;

        // must check ALL resources in folder because name doesn't care for type
        List resources = null;
        try {
            resources = cms.readResources(foldername, CmsResourceFilter.ALL, false);
        } catch (CmsException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e);
            }
        }
        if (resources != null) {
            // now create a list of all resources that just contains the file names
            List result = new ArrayList(resources.size());
            for (int i = 0; i < resources.size(); i++) {
                CmsResource resource = (CmsResource)resources.get(i);
                result.add(resource.getRootPath());
            }

            String fileName = cms.getRequestContext().addSiteRoot(foldername);
            String checkFileName = "";
            String number = "";

            int j = 0;
            do {
                number = NUMBER_FORMAT.sprintf(++j);
                String postName = resourceType + "_" + number + ".html";
                checkFileName = fileName + postName;
                newFileName = postName;
            } while (result.contains(checkFileName));
        }
        return newFileName;
    }

    /**
     * Gets path to this location where XML contents shall become new created and updated. 
     * This the path which is selected by current user in OpenCms workplace.<p>
     * 
     * @param cmsObject current CmsObject
     * @param workplacePath current path in workplace
     * @param resourceType selected resource type
     * 
     * @return path to XML contents
     */
    public String getPathToXmlContents(CmsObject cmsObject, String workplacePath, String resourceType) {

        return workplacePath;
    }
}
