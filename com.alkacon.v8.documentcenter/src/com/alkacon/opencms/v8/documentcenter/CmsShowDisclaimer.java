/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.documentcenter/src/com/alkacon/opencms/documentcenter/CmsShowDisclaimer.java,v $
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

package com.alkacon.opencms.v8.documentcenter;

import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.CmsResourceInitException;
import org.opencms.main.I_CmsResourceInit;
import org.opencms.main.OpenCms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;

/**
 * Provides a disclaimer page if a "disclaimer_page" property 
 * is set on a resource.
 * This class implements com.opencms.core.I_CmsResourceInit.<p>
 *
 * @author Andreas Zahner 
 * @author Thomas Weckert 
 * @author Michael Emmerich 
 * 
 * @version $Revision: 1.3 $ 
 * 
 * @since 6.0.0 
 */
public class CmsShowDisclaimer implements I_CmsResourceInit {

    /** value for the session variable when disclaimer was accepted. */
    public static final String ACCEPTED_VALUE = "true";

    /** Disclaimer property name. */
    public static final String DISCLAIMER_PROPERTY = "disclaimer_page";

    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsShowDisclaimer.class);

    /**
     * @see org.opencms.main.I_CmsResourceInit#initResource(org.opencms.file.CmsResource, org.opencms.file.CmsObject, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public CmsResource initResource(CmsResource resource, CmsObject cms, HttpServletRequest req, HttpServletResponse res) {

        String disclaimer = null;

        if (resource != null) {
            // do not show the disclaimer if the file list of the document center is 
            // requested.
            if (cms.getSitePath(resource).equals(CmsShowDefaultFile.DEFAULTFILE)) {
                return resource;
            }
            // do not show the disclaimer for the internal files of the document center
            // (all resources starting with "$")
            if (resource.getName().startsWith("$")) {
                return resource;
            }
            try {
                disclaimer = cms.readPropertyObject(cms.getSitePath(resource), DISCLAIMER_PROPERTY, true).getValue();
            } catch (CmsException e) {
                // do nothing
            }
        }

        if (disclaimer == null) {
            String uri = cms.getRequestContext().getUri();
            try {
                disclaimer = cms.readPropertyObject(uri, DISCLAIMER_PROPERTY, true).getValue();
            } catch (CmsException e) {
                // do nothing
            }
        } else {
            // override the disclaimer if the disclaimer property is set to "false"
            // this can be used to disable the disclaimer for single resources in a folder
            // which is protected by a disclaimer.
            if (disclaimer.equals("false")) {
                return resource;
            }
        }

        // found disclaimer property, eventually show other file!
        if (disclaimer != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(DISCLAIMER_PROPERTY + ": " + disclaimer);
            }

            try {
                CmsFile fileDisclaimer = cms.readFile(disclaimer);
                // get the http session
                HttpSession session = req.getSession();
                // get the value of the session attribute according to the requested resource
                String accepted = (String)session.getAttribute(disclaimer);

                if (OpenCms.getLog(this).isDebugEnabled()) {
                    OpenCms.getLog(this).debug("Session Attribute " + disclaimer + ": " + accepted);
                }

                // disclaimer has not been accepted, show disclaimer page
                if (!ACCEPTED_VALUE.equals(accepted)) {
                    return fileDisclaimer;
                }
                // Error getting the disclaimer, return original requested file            
            } catch (CmsResourceInitException e) {
                // resource init exceptions should be handled in OpenCmsCore's initResource()

            } catch (CmsException e) {
                if (org.opencms.main.OpenCms.getLog(this).isErrorEnabled()) {
                    org.opencms.main.OpenCms.getLog(this).error("Error reading disclaimer page '" + disclaimer, e);
                }
            } catch (UnsupportedOperationException e2) {
                // could occur for a static export request
                if (org.opencms.main.OpenCms.getLog(this).isErrorEnabled()) {
                    org.opencms.main.OpenCms.getLog(this).error(e2);
                }
            }
        }

        return resource;
    }

}
