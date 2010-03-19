/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsShowDefaultFile.java,v $
 * Date   : $Date: 2010/03/19 15:31:13 $
 * Version: $Revision: 1.3 $
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

package com.alkacon.opencms.documentcenter;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.main.CmsException;
import org.opencms.main.I_CmsResourceInit;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.util.CmsStringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides a default index page if there is none present in a folder.
 * 
 * This is necessary for folders containing only documents in the VFS
 * of Opencms.<p>
 *
 * @author  Andreas Zahner 
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 6.0.0 
 */
public class CmsShowDefaultFile implements I_CmsResourceInit {

    /** Stores absolute path to the default page which should be displayed. */
    public static final String DEFAULTFILE = "/system/modules/com.alkacon.opencms.documentcenter/pages/documents.jsp";

    /** The name of the documents module. */
    public static final String MODULENAME = "com.alkacon.opencms.documentcenter";

    /** The module parameter to set the default file to another URI. */
    public static final String PARAMETER_DEFAULTFILE = "defaultfile";

    /** Property to disable the documentcenter. */
    public static final String PROPERTY_DISABLE_DOCCENTER = "documentcenter.disable";

    /**
     * @see org.opencms.main.I_CmsResourceInit#initResource(org.opencms.file.CmsResource, org.opencms.file.CmsObject, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public CmsResource initResource(CmsResource resource, CmsObject cms, HttpServletRequest req, HttpServletResponse res) {

        // search only when file is null!
        if (resource == null) {
            // get the requested URI
            String uri = cms.getRequestContext().getUri();
            if (uri.startsWith(CmsResource.VFS_FOLDER_SYSTEM)) {
                // don't show the system folder!
                return resource;
            }

            // cut the site root from the uri if it is there already
            String siteRoot = OpenCms.getSiteManager().getSiteRoot(uri);
            if (siteRoot != null) {
                uri = uri.substring(siteRoot.length());
            }

            if (!uri.endsWith("/")) {
                uri += "/";
            }
            try {
                // make sure that the folder is existing!
                if (cms.readFolder(uri, CmsResourceFilter.IGNORE_EXPIRATION) != null) {
                    // test if the documentcenter is disbled fo this folder
                    CmsProperty propertyDisabled = cms.readPropertyObject(uri, PROPERTY_DISABLE_DOCCENTER, true);
                    if (!propertyDisabled.equals(CmsProperty.getNullProperty())) {
                        if (propertyDisabled.getValue().equals("true")) {
                            return resource;
                        }
                    }

                    // get the module and check the module parameter "defaultfile"
                    CmsFile indexFile = null;
                    CmsModule docModule = OpenCms.getModuleManager().getModule(MODULENAME);
                    String siteroot = cms.getRequestContext().getSiteRoot();
                    //siteroot
                    if (siteroot.startsWith(CmsResource.VFS_FOLDER_SITES)) {
                        siteroot = siteroot.substring(CmsResource.VFS_FOLDER_SITES.length() + 1);
                    }
                    if (!CmsStringUtil.isEmptyOrWhitespaceOnly(siteroot)) {
                        //checks if default-file is marked for a site. (the parameter:defaultfile_SiteFolderName)
                        String fileSite = docModule.getParameter(PARAMETER_DEFAULTFILE + "_" + siteroot);
                        if (!CmsStringUtil.isEmptyOrWhitespaceOnly(fileSite)) {
                            indexFile = cms.readFile(fileSite, CmsResourceFilter.IGNORE_EXPIRATION);
                        }
                    }

                    if (indexFile == null) {
                        String fileName = docModule.getParameter(PARAMETER_DEFAULTFILE, DEFAULTFILE);
                        // read the file to ensure that it is present
                        indexFile = cms.readFile(fileName, CmsResourceFilter.IGNORE_EXPIRATION);
                    }
                    return indexFile;
                }
            } catch (CmsException e) {
                // ignore this exception
            } catch (UnsupportedOperationException e2) {
                // ignore this exception
            }
        }
        return resource;
    }

}
